
/*
 * Copyright 2008 The Android Open Source Project
 *
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */


#include "SkBitmap.h"
#include "SkColorPriv.h"
#include "SkDither.h"
#include "SkFlattenable.h"
#include "SkMallocPixelRef.h"
#include "SkMask.h"
#include "SkOrderedReadBuffer.h"
#include "SkOrderedWriteBuffer.h"
#include "SkPixelRef.h"
#include "SkThread.h"
#include "SkUnPreMultiply.h"
#include "SkUtils.h"
#include "SkPackBits.h"
#include <new>

#include<android/log.h>
#define LOG_TAG "libgif"
#define ALOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, fmt, ##args)

SK_DEFINE_INST_COUNT(SkBitmap::Allocator)

static bool isPos32Bits(const Sk64& value) {
    return !value.isNeg() && value.is32();
}

struct MipLevel {
    void*       fPixels;
    uint32_t    fRowBytes;
    uint32_t    fWidth, fHeight;
};

struct SkBitmap::MipMap : SkNoncopyable {
    int32_t fRefCnt;
    int     fLevelCount;
//  MipLevel    fLevel[fLevelCount];
//  Pixels[]

    static MipMap* Alloc(int levelCount, size_t pixelSize) {
        if (levelCount < 0) {
            return NULL;
        }
        Sk64 size;
        size.setMul(levelCount + 1, sizeof(MipLevel));
        size.add(sizeof(MipMap));
        size.add(SkToS32(pixelSize));
        if (!isPos32Bits(size)) {
            return NULL;
        }
        MipMap* mm = (MipMap*)sk_malloc_throw(size.get32());
        mm->fRefCnt = 1;
        mm->fLevelCount = levelCount;
        return mm;
    }

    const MipLevel* levels() const { return (const MipLevel*)(this + 1); }
    MipLevel* levels() { return (MipLevel*)(this + 1); }

    const void* pixels() const { return levels() + fLevelCount; }
    void* pixels() { return levels() + fLevelCount; }

    void ref() {
        if (SK_MaxS32 == sk_atomic_inc(&fRefCnt)) {
            sk_throw();
        }
    }
    void unref() {
        SkASSERT(fRefCnt > 0);
        if (sk_atomic_dec(&fRefCnt) == 1) {
            sk_free(this);
        }
    }
};

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

////////////////////////////////// Abel Lin ///////////////////////////////////
SkBitmap::SkBitmap() {
    sk_bzero(this, sizeof(*this));
}

SkBitmap::SkBitmap(const SkBitmap& src) {
    SkDEBUGCODE(src.validate();)
    sk_bzero(this, sizeof(*this));
    *this = src;
    SkDEBUGCODE(this->validate();)
}

SkBitmap::~SkBitmap() {
    SkDEBUGCODE(this->validate();)
    this->freePixels();
}


SkBitmap& SkBitmap::operator=(const SkBitmap& src) {
    if (this != &src) {
        this->freePixels();
        memcpy(this, &src, sizeof(src));

        // inc src reference counts
        SkSafeRef(src.fPixelRef);
        SkSafeRef(src.fMipMap);

        // we reset our locks if we get blown away
        fPixelLockCount = 0;

        /*  The src could be in 3 states
            1. no pixelref, in which case we just copy/ref the pixels/ctable
            2. unlocked pixelref, pixels/ctable should be null
            3. locked pixelref, we should lock the ref again ourselves
        */
        if (NULL == fPixelRef) {
            // leave fPixels as it is
            SkSafeRef(fColorTable); // ref the user's ctable if present
        } else {    // we have a pixelref, so pixels/ctable reflect it
            // ignore the values from the memcpy
            fPixels = NULL;
            fColorTable = NULL;
            // Note that what to for genID is somewhat arbitrary. We have no
            // way to track changes to raw pixels across multiple SkBitmaps.
            // Would benefit from an SkRawPixelRef type created by
            // setPixels.
            // Just leave the memcpy'ed one but they'll get out of sync
            // as soon either is modified.
        }
    }

    SkDEBUGCODE(this->validate();)
    return *this;
}

void SkBitmap::reset() {
    this->freePixels();
    sk_bzero(this, sizeof(*this));
}


void SkBitmap::setConfig(Config c, int width, int height, size_t rowBytes) {
    this->freePixels();

    if ((width | height) < 0) {
        goto err;
    }

    if (rowBytes == 0) {
        rowBytes = SkBitmap::ComputeRowBytes(c, width);
        if (0 == rowBytes && kNo_Config != c) {
            goto err;
        }
    }

    fConfig     = SkToU8(c);
    fWidth      = width;
    fHeight     = height;
    fRowBytes   = SkToU32(rowBytes);

    fBytesPerPixel = (uint8_t)ComputeBytesPerPixel(c);

    SkDEBUGCODE(this->validate();)
    return;

    // if we got here, we had an error, so we reset the bitmap to empty
err:
    this->reset();
}

void SkBitmap::lockPixels() const {
    if (NULL != fPixelRef && 0 == sk_atomic_inc(&fPixelLockCount)) {
        fPixelRef->lockPixels();
        this->updatePixelsFromRef();
    }
    SkDEBUGCODE(this->validate();)
}

bool SkBitmap::allocPixels(Allocator* allocator, SkColorTable* ctable) {
    HeapAllocator stdalloc;

    if (NULL == allocator) {
        allocator = &stdalloc;
    }
    return allocator->allocPixelRef(this, ctable);
}


bool SkBitmap::isOpaque() const {
    switch (fConfig) {
        case kNo_Config:
            return true;

        case kA1_Config:
        case kA8_Config:
        case kARGB_4444_Config:
        case kARGB_8888_Config:
            return (fFlags & kImageIsOpaque_Flag) != 0;

        case kIndex8_Config: {
            uint32_t flags = 0;

            this->lockPixels();
            // if lockPixels failed, we may not have a ctable ptr
            if (fColorTable) {
                flags = fColorTable->getFlags();
            }
            this->unlockPixels();

            return (flags & SkColorTable::kColorsAreOpaque_Flag) != 0;
        }

        case kRGB_565_Config:
            return true;

        default:
            SkDEBUGFAIL("unknown bitmap config pased to isOpaque");
            return false;
    }
}


void SkBitmap::setIsOpaque(bool isOpaque) {
    /*  we record this regardless of fConfig, though it is ignored in
        isOpaque() for configs that can't support per-pixel alpha.
    */
    if (isOpaque) {
        fFlags |= kImageIsOpaque_Flag;
    } else {
        fFlags &= ~kImageIsOpaque_Flag;
    }
}


static uint16_t pack_8888_to_4444(unsigned a, unsigned r, unsigned g, unsigned b) {
    unsigned pixel = (SkA32To4444(a) << SK_A4444_SHIFT) |
                     (SkR32To4444(r) << SK_R4444_SHIFT) |
                     (SkG32To4444(g) << SK_G4444_SHIFT) |
                     (SkB32To4444(b) << SK_B4444_SHIFT);
    return SkToU16(pixel);
}

void SkBitmap::internalErase(const SkIRect& area,
                             U8CPU a, U8CPU r, U8CPU g, U8CPU b) const {
#ifdef SK_DEBUG
    SkDEBUGCODE(this->validate();)
    SkASSERT(!area.isEmpty());
    {
        SkIRect total = { 0, 0, this->width(), this->height() };
        SkASSERT(total.contains(area));
    }
#endif

    if (kNo_Config == fConfig || kIndex8_Config == fConfig) {
        return;
    }

    SkAutoLockPixels alp(*this);
    // perform this check after the lock call
    if (!this->readyToDraw()) {
        return;
    }

    int height = area.height();
    const int width = area.width();
    const int rowBytes = fRowBytes;

    // make rgb premultiplied
    if (255 != a) {
        r = SkAlphaMul(r, a);
        g = SkAlphaMul(g, a);
        b = SkAlphaMul(b, a);
    }

    switch (fConfig) {
        case kA1_Config: {
            uint8_t* p = this->getAddr1(area.fLeft, area.fTop);
            const int left = area.fLeft >> 3;
            const int right = area.fRight >> 3;

            int middle = right - left - 1;

            uint8_t leftMask = 0xFF >> (area.fLeft & 7);
            uint8_t rightMask = ~(0xFF >> (area.fRight & 7));
            if (left == right) {
                leftMask &= rightMask;
                rightMask = 0;
            }

            a = (a >> 7) ? 0xFF : 0;
            while (--height >= 0) {
                uint8_t* startP = p;

                *p = (*p & ~leftMask) | (a & leftMask);
                p++;
                if (middle > 0) {
                    memset(p, a, middle);
                    p += middle;
                }
                if (rightMask) {
                    *p = (*p & ~rightMask) | (a & rightMask);
                }

                p = startP + rowBytes;
            }
            break;
        }
        case kA8_Config: {
            uint8_t* p = this->getAddr8(area.fLeft, area.fTop);
            while (--height >= 0) {
                memset(p, a, width);
                p += rowBytes;
            }
            break;
        }
        case kARGB_4444_Config:
        case kRGB_565_Config: {
            uint16_t* p = this->getAddr16(area.fLeft, area.fTop);;
            uint16_t v;

            if (kARGB_4444_Config == fConfig) {
                v = pack_8888_to_4444(a, r, g, b);
            } else {
                v = SkPackRGB16(r >> (8 - SK_R16_BITS),
                                g >> (8 - SK_G16_BITS),
                                b >> (8 - SK_B16_BITS));
            }
            while (--height >= 0) {
                sk_memset16(p, v, width);
                p = (uint16_t*)((char*)p + rowBytes);
            }
            break;
        }
        case kARGB_8888_Config: {
            uint32_t* p = this->getAddr32(area.fLeft, area.fTop);
            uint32_t  v = SkPackARGB32(a, r, g, b);

            while (--height >= 0) {
                sk_memset32(p, v, width);
                p = (uint32_t*)((char*)p + rowBytes);
            }
            break;
        }
    }

    this->notifyPixelsChanged();
}

void SkBitmap::eraseARGB(U8CPU a, U8CPU r, U8CPU g, U8CPU b) const {
    SkIRect area = { 0, 0, this->width(), this->height() };
    if (!area.isEmpty()) {
        this->internalErase(area, a, r, g, b);
    }
}



/**
 *  Using the pixelRefOffset(), rowBytes(), and Config of bm, determine the (x, y) coordinate of the
 *  upper left corner of bm relative to its SkPixelRef.
 *  x and y must be non-NULL.
 */
bool get_upper_left_from_offset(SkBitmap::Config config, size_t offset, size_t rowBytes,
                                   int32_t* x, int32_t* y);
bool get_upper_left_from_offset(SkBitmap::Config config, size_t offset, size_t rowBytes,
                                   int32_t* x, int32_t* y) {
    SkASSERT(x != NULL && y != NULL);
    if (0 == offset) {
        *x = *y = 0;
        return true;
    }
    // Use integer division to find the correct y position.
    *y = SkToS32(offset / rowBytes);
    // The remainder will be the x position, after we reverse get_sub_offset.
    *x = SkToS32(offset % rowBytes);
    switch (config) {
        case SkBitmap::kA8_Config:
            // Fall through.
        case SkBitmap::kIndex8_Config:
            // x is unmodified
            break;

        case SkBitmap::kRGB_565_Config:
            // Fall through.
        case SkBitmap::kARGB_4444_Config:
            *x >>= 1;
            break;

        case SkBitmap::kARGB_8888_Config:
            *x >>= 2;
            break;

        case SkBitmap::kNo_Config:
            // Fall through.
        case SkBitmap::kA1_Config:
            // Fall through.
        default:
            return false;
    }
    return true;
}

static bool get_upper_left_from_offset(const SkBitmap& bm, int32_t* x, int32_t* y) {
    return get_upper_left_from_offset(bm.config(), bm.pixelRefOffset(), bm.rowBytes(), x, y);
}

#include "SkCanvas.h"
#include "SkPaint.h"

/*
namespace SkCanvas {
    bool reject_bitmap(const SkBitmap& bitmap) {
        return  bitmap.width() <= 0 || bitmap.height() <= 0;
    }

    void internalDrawBitmap(const SkBitmap& bitmap, const SkMatrix& matrix, const SkPaint* paint) {
        if (reject_bitmap(bitmap)) {
            return;
        }

        SkLazyPaint lazy;
        if (NULL == paint) {
            paint = lazy.init();
        }

        SkDEBUGCODE(bitmap.validate();)
        CHECK_LOCKCOUNT_BALANCE(bitmap);

        LOOPER_BEGIN(*paint, SkDrawFilter::kBitmap_Type)

        while (iter.next()) {
            iter.fDevice->drawBitmap(iter, bitmap, matrix, looper.paint());
        }

        LOOPER_END
    }

    drawBitmap(const SkBitmap& bitmap, SkScalar x, SkScalar y, const SkPaint* paint) {
        SkDEBUGCODE(bitmap.validate();)

        if (NULL == paint || paint->canComputeFastBounds()) {
            SkRect bounds = {
                x, y,
                x + SkIntToScalar(bitmap.width()),
                y + SkIntToScalar(bitmap.height())
            };
            if (paint) {
                (void)paint->computeFastBounds(bounds, &bounds);
            }
            if (this->quickReject(bounds)) {
                return;
            }
        }

        SkMatrix matrix;
        matrix.setTranslate(x, y);
        internalDrawBitmap(bitmap, matrix, paint);
    }
}
*/
bool SkBitmap::canCopyTo(Config dstConfig) const {
    if (this->getConfig() == kNo_Config) {
        return false;
    }

    bool sameConfigs = (this->config() == dstConfig);
    switch (dstConfig) {
        case kA8_Config:
        case kRGB_565_Config:
        case kARGB_8888_Config:
            break;
        case kA1_Config:
        case kIndex8_Config:
            if (!sameConfigs) {
                return false;
            }
            break;
        case kARGB_4444_Config:
            return sameConfigs || kARGB_8888_Config == this->config();
        default:
            return false;
    }

    // do not copy src if srcConfig == kA1_Config while dstConfig != kA1_Config
    if (this->getConfig() == kA1_Config && !sameConfigs) {
        return false;
    }

    return true;
}

bool SkBitmap::copyTo(SkBitmap* dst, Config dstConfig, Allocator* alloc) const {
    if (!this->canCopyTo(dstConfig)) {
        return false;
    }

    // if we have a texture, first get those pixels
    SkBitmap tmpSrc;
    const SkBitmap* src = this;

    if (fPixelRef) {
        SkIRect subset;
        if (get_upper_left_from_offset(*this, &subset.fLeft, &subset.fTop)) {
            subset.fRight = subset.fLeft + fWidth;
            subset.fBottom = subset.fTop + fHeight;
            if (fPixelRef->readPixels(&tmpSrc, &subset)) {
                SkASSERT(tmpSrc.width() == this->width());
                SkASSERT(tmpSrc.height() == this->height());

                // did we get lucky and we can just return tmpSrc?
                if (tmpSrc.config() == dstConfig && NULL == alloc) {
                    dst->swap(tmpSrc);
                    if (dst->pixelRef() && this->config() == dstConfig) {
                        dst->pixelRef()->fGenerationID = fPixelRef->getGenerationID();
                    }
                    return true;
                }

                // fall through to the raster case
                src = &tmpSrc;
            }
        }
    }

    // we lock this now, since we may need its colortable
    SkAutoLockPixels srclock(*src);
    if (!src->readyToDraw()) {
        return false;
    }

    SkBitmap tmpDst;
    tmpDst.setConfig(dstConfig, src->width(), src->height());

    // allocate colortable if srcConfig == kIndex8_Config
    SkColorTable* ctable = (dstConfig == kIndex8_Config) ?
    new SkColorTable(*src->getColorTable()) : NULL;
    SkAutoUnref au(ctable);
    if (!tmpDst.allocPixels(alloc, ctable)) {
        return false;
    }

    if (!tmpDst.readyToDraw()) {
        // allocator/lock failed
        return false;
    }

    /* do memcpy for the same configs cases, else use drawing
    */
    if (src->config() == dstConfig) {
        if (tmpDst.getSize() == src->getSize()) {
            memcpy(tmpDst.getPixels(), src->getPixels(), src->getSafeSize());
            SkPixelRef* pixelRef = tmpDst.pixelRef();
            if (pixelRef != NULL) {
                pixelRef->fGenerationID = this->getGenerationID();
            }
        } else {
            const char* srcP = reinterpret_cast<const char*>(src->getPixels());
            char* dstP = reinterpret_cast<char*>(tmpDst.getPixels());
            // to be sure we don't read too much, only copy our logical pixels
            size_t bytesToCopy = tmpDst.width() * tmpDst.bytesPerPixel();
            for (int y = 0; y < tmpDst.height(); y++) {
                memcpy(dstP, srcP, bytesToCopy);
                srcP += src->rowBytes();
                dstP += tmpDst.rowBytes();
            }
        }
    } else if (SkBitmap::kARGB_4444_Config == dstConfig
               && SkBitmap::kARGB_8888_Config == src->config()) {
        SkASSERT(src->height() == tmpDst.height());
        SkASSERT(src->width() == tmpDst.width());
        for (int y = 0; y < src->height(); ++y) {
            SkPMColor16* SK_RESTRICT dstRow = (SkPMColor16*) tmpDst.getAddr16(0, y);
            SkPMColor* SK_RESTRICT srcRow = (SkPMColor*) src->getAddr32(0, y);
            DITHER_4444_SCAN(y);
            for (int x = 0; x < src->width(); ++x) {
                dstRow[x] = SkDitherARGB32To4444(srcRow[x],
                                                 DITHER_VALUE(x));
            }
        }
    } else {
        // if the src has alpha, we have to clear the dst first
        if (!src->isOpaque()) {
            tmpDst.eraseColor(SK_ColorTRANSPARENT);
        }

        ALOGE("SkBitmap::copyTo -> abort()");
        abort();
        /* [Abel]
        SkCanvas canvas(tmpDst);
        SkPaint  paint;

        paint.setDither(true);
        canvas.drawBitmap(*src, 0, 0, &paint);
        */
    }

    tmpDst.setIsOpaque(src->isOpaque());

    dst->swap(tmpDst);
    return true;
}
////////////////////////////////// Abel Lin ///////////////////////////////////


void SkBitmap::swap(SkBitmap& other) {
    SkTSwap(fColorTable, other.fColorTable);
    SkTSwap(fPixelRef, other.fPixelRef);
    SkTSwap(fPixelRefOffset, other.fPixelRefOffset);
    SkTSwap(fPixelLockCount, other.fPixelLockCount);
    SkTSwap(fMipMap, other.fMipMap);
    SkTSwap(fPixels, other.fPixels);
    SkTSwap(fRowBytes, other.fRowBytes);
    SkTSwap(fWidth, other.fWidth);
    SkTSwap(fHeight, other.fHeight);
    SkTSwap(fConfig, other.fConfig);
    SkTSwap(fFlags, other.fFlags);
    SkTSwap(fBytesPerPixel, other.fBytesPerPixel);

    SkDEBUGCODE(this->validate();)
}

int SkBitmap::ComputeBytesPerPixel(SkBitmap::Config config) {
	int bpp;
	switch (config) {
		case kNo_Config:
		case kA1_Config:
			bpp = 0;   // not applicable
			break;
		case kA8_Config:
		case kIndex8_Config:
			bpp = 1;
			break;
		case kRGB_565_Config:
		case kARGB_4444_Config:
			bpp = 2;
			break;
		case kARGB_8888_Config:
			bpp = 4;
			break;
		default:
			SkDEBUGFAIL("unknown config");
			bpp = 0;   // error
			break;
	}
	return bpp;
}

size_t SkBitmap::ComputeRowBytes(Config c, int width) {
	if (width < 0) {
		return 0;
	}

	Sk64 rowBytes;
	rowBytes.setZero();

	switch (c) {
		case kNo_Config:
			break;
		case kA1_Config:
			rowBytes.set(width);
			rowBytes.add(7);
			rowBytes.shiftRight(3);
			break;
		case kA8_Config:
		case kIndex8_Config:
			rowBytes.set(width);
			break;
		case kRGB_565_Config:
		case kARGB_4444_Config:
			rowBytes.set(width);
			rowBytes.shiftLeft(1);
			break;
		case kARGB_8888_Config:
			rowBytes.set(width);
			rowBytes.shiftLeft(2);
			break;
		default:
			SkDEBUGFAIL("unknown config");
			break;
	}
	return isPos32Bits(rowBytes) ? rowBytes.get32() : 0;
}

Sk64 SkBitmap::ComputeSize64(Config c, int width, int height) {
	ALOGE("SkBitmap::ComputeSize64 not implemented!");
	abort();
	Sk64 size;
	return size;
}

size_t SkBitmap::ComputeSize(Config c, int width, int height) {
	ALOGE("SkBitmap::ComputeSize not implemented!");
	abort();
}

Sk64 SkBitmap::ComputeSafeSize64(Config config,
                                 uint32_t width,
                                 uint32_t height,
                                 size_t rowBytes) {
	ALOGE("SkBitmap::ComputeSafeSize64 not implemented!");
	abort();
	Sk64 size;
	return size;
}

size_t SkBitmap::ComputeSafeSize(Config config,
                                 uint32_t width,
                                 uint32_t height,
                                 size_t rowBytes) {
	ALOGE("SkBitmap::ComputeSafeSize not implemented!");
	abort();
    return 0;
}

void SkBitmap::getBounds(SkRect* bounds) const {
	ALOGE("SkBitmap::getBounds not implemented!");
	abort();
}

void SkBitmap::getBounds(SkIRect* bounds) const {
	ALOGE("SkBitmap::getBounds not implemented!");
	abort();
}

///////////////////////////////////////////////////////////////////////////////

void SkBitmap::updatePixelsFromRef() const {
	if (NULL != fPixelRef) {
		if (fPixelLockCount > 0) {
			SkASSERT(fPixelRef->isLocked());

			void* p = fPixelRef->pixels();
			if (NULL != p) {
				p = (char*)p + fPixelRefOffset;
			}
			fPixels = p;
			SkRefCnt_SafeAssign(fColorTable, fPixelRef->colorTable());
		} else {
			SkASSERT(0 == fPixelLockCount);
			fPixels = NULL;
			if (fColorTable) {
				fColorTable->unref();
				fColorTable = NULL;
			}
		}
	}
}

SkPixelRef* SkBitmap::setPixelRef(SkPixelRef* pr, size_t offset) {
    // do this first, we that we never have a non-zero offset with a null ref
    if (NULL == pr) {
        offset = 0;
    }

    if (fPixelRef != pr || fPixelRefOffset != offset) {
        if (fPixelRef != pr) {
            this->freePixels();
            SkASSERT(NULL == fPixelRef);

            SkSafeRef(pr);
            fPixelRef = pr;
        }
        fPixelRefOffset = offset;
        this->updatePixelsFromRef();
    }

    SkDEBUGCODE(this->validate();)
    return pr;
}

void SkBitmap::unlockPixels() const {
    SkASSERT(NULL == fPixelRef || fPixelLockCount > 0);

    if (NULL != fPixelRef && 1 == sk_atomic_dec(&fPixelLockCount)) {
        fPixelRef->unlockPixels();
        this->updatePixelsFromRef();
    }
    SkDEBUGCODE(this->validate();)
}

bool SkBitmap::lockPixelsAreWritable() const {
    return (fPixelRef) ? fPixelRef->lockPixelsAreWritable() : false;
}

void SkBitmap::setPixels(void* p, SkColorTable* ctable) {
	if (NULL == p) {
		this->setPixelRef(NULL, 0);
		return;
	}

	Sk64 size = this->getSize64();
	SkASSERT(!size.isNeg() && size.is32());

	this->setPixelRef(new SkMallocPixelRef(p, size.get32(), ctable, false))->unref();
	// since we're already allocated, we lockPixels right away
	this->lockPixels();
	SkDEBUGCODE(this->validate();)
}

void SkBitmap::freePixels() {
	// if we're gonna free the pixels, we certainly need to free the mipmap
	this->freeMipMap();

	if (fColorTable) {
		fColorTable->unref();
		fColorTable = NULL;
	}

	if (NULL != fPixelRef) {
		if (fPixelLockCount > 0) {
			fPixelRef->unlockPixels();
		}
		fPixelRef->unref();
		fPixelRef = NULL;

		fPixelRefOffset = 0;
	}
	fPixelLockCount = 0;
	fPixels = NULL;
}

void SkBitmap::freeMipMap() {
    if (fMipMap) {
        fMipMap->unref();
        fMipMap = NULL;
    }
}

uint32_t SkBitmap::getGenerationID() const {
    return (fPixelRef) ? fPixelRef->getGenerationID() : 0;
}

void SkBitmap::notifyPixelsChanged() const {
    SkASSERT(!this->isImmutable());
    if (fPixelRef) {
        fPixelRef->notifyPixelsChanged();
    }
}

GrTexture* SkBitmap::getTexture() const {
	ALOGE("SkBitmap::getTexture not implemented!");
	abort();
	return NULL;
}

///////////////////////////////////////////////////////////////////////////////

/** We explicitly use the same allocator for our pixels that SkMask does,
 so that we can freely assign memory allocated by one class to the other.
 */
bool SkBitmap::HeapAllocator::allocPixelRef(SkBitmap* dst,
                                            SkColorTable* ctable) {
	Sk64 size = dst->getSize64();
	if (size.isNeg() || !size.is32()) {
		return false;
	}

	void* addr = sk_malloc_flags(size.get32(), 0);  // returns NULL on failure
	if (NULL == addr) {
		return false;
	}

	dst->setPixelRef(new SkMallocPixelRef(addr, size.get32(), ctable))->unref();
	// since we're already allocated, we lockPixels right away
	dst->lockPixels();
	return true;
}

///////////////////////////////////////////////////////////////////////////////

size_t SkBitmap::getSafeSize() const {
    // This is intended to be a size_t version of ComputeSafeSize64(), just
    // faster. The computation is meant to be identical.
    return (fHeight ? ((fHeight - 1) * fRowBytes) +
            ComputeRowBytes(getConfig(), fWidth): 0);
}

Sk64 SkBitmap::getSafeSize64() const {
    return ComputeSafeSize64(getConfig(), fWidth, fHeight, fRowBytes);
}

bool SkBitmap::copyPixelsTo(void* const dst, size_t dstSize,
                            size_t dstRowBytes, bool preserveDstPad) const {
	ALOGE("SkBitmap::copyPixelsTo not implemented!");
	abort();
    return false;
}

///////////////////////////////////////////////////////////////////////////////

bool SkBitmap::isImmutable() const {
    return fPixelRef ? fPixelRef->isImmutable() :
        fFlags & kImageIsImmutable_Flag;
}

void SkBitmap::setImmutable() {
    if (fPixelRef) {
        fPixelRef->setImmutable();
    } else {
        fFlags |= kImageIsImmutable_Flag;
    }
}

bool SkBitmap::isVolatile() const {
    return (fFlags & kImageIsVolatile_Flag) != 0;
}

void SkBitmap::setIsVolatile(bool isVolatile) {
	ALOGE("SkBitmap::setIsVolatile not implemented!");
	abort();
}

void* SkBitmap::getAddr(int x, int y) const {
	SkASSERT((unsigned)x < (unsigned)this->width());
	SkASSERT((unsigned)y < (unsigned)this->height());

	char* base = (char*)this->getPixels();
	if (base) {
		base += y * this->rowBytes();
		switch (this->config()) {
			case SkBitmap::kARGB_8888_Config:
				base += x << 2;
				break;
			case SkBitmap::kARGB_4444_Config:
			case SkBitmap::kRGB_565_Config:
				base += x << 1;
				break;
			case SkBitmap::kA8_Config:
			case SkBitmap::kIndex8_Config:
				base += x;
				break;
			case SkBitmap::kA1_Config:
				base += x >> 3;
				break;
			default:
				SkDEBUGFAIL("Can't return addr for config");
				base = NULL;
				break;
		}
	}
	return base;
}

SkColor SkBitmap::getColor(int x, int y) const {
	ALOGE("SkBitmap::getColor not implemented!");
	abort();
	return 0;
}

bool SkBitmap::ComputeIsOpaque(const SkBitmap& bm) {
	SkAutoLockPixels alp(bm);
	if (!bm.getPixels()) {
		return false;
	}

	const int height = bm.height();
	const int width = bm.width();

	switch (bm.config()) {
		case SkBitmap::kA1_Config: {
			// TODO
		} break;
		case SkBitmap::kA8_Config: {
			unsigned a = 0xFF;
			for (int y = 0; y < height; ++y) {
				const uint8_t* row = bm.getAddr8(0, y);
				for (int x = 0; x < width; ++x) {
					a &= row[x];
				}
				if (0xFF != a) {
					return false;
				}
			}
			return true;
		} break;
		case SkBitmap::kIndex8_Config: {
			SkAutoLockColors alc(bm);
			const SkPMColor* table = alc.colors();
			if (!table) {
				return false;
			}
			SkPMColor c = (SkPMColor)~0;
			for (int i = bm.getColorTable()->count() - 1; i >= 0; --i) {
				c &= table[i];
			}
			return 0xFF == SkGetPackedA32(c);
		} break;
		case SkBitmap::kRGB_565_Config:
			return true;
			break;
		case SkBitmap::kARGB_4444_Config: {
			unsigned c = 0xFFFF;
			for (int y = 0; y < height; ++y) {
				const SkPMColor16* row = bm.getAddr16(0, y);
				for (int x = 0; x < width; ++x) {
					c &= row[x];
				}
				if (0xF != SkGetPackedA4444(c)) {
					return false;
				}
			}
			return true;
		} break;
		case SkBitmap::kARGB_8888_Config: {
			SkPMColor c = (SkPMColor)~0;
			for (int y = 0; y < height; ++y) {
				const SkPMColor* row = bm.getAddr32(0, y);
				for (int x = 0; x < width; ++x) {
					c &= row[x];
				}
				if (0xFF != SkGetPackedA32(c)) {
					return false;
				}
			}
			return true;
		}
		default:
			break;
	}
	return false;
}


///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

void SkBitmap::eraseArea(const SkIRect& rect, SkColor c) const {
    SkIRect area = { 0, 0, this->width(), this->height() };
    if (area.intersect(rect)) {
        this->internalErase(area, SkColorGetA(c), SkColorGetR(c),
                            SkColorGetG(c), SkColorGetB(c));
    }
}

//////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////

#define SUB_OFFSET_FAILURE  ((size_t)-1)

/**
 *  Based on the Config and rowBytes() of bm, return the offset into an SkPixelRef of the pixel at
 *  (x, y).
 *  Note that the SkPixelRef does not need to be set yet. deepCopyTo takes advantage of this fact.
 *  Also note that (x, y) may be outside the range of (0 - width(), 0 - height()), so long as it is
 *  within the bounds of the SkPixelRef being used.
 */
static size_t get_sub_offset(const SkBitmap& bm, int x, int y) {
    switch (bm.getConfig()) {
        case SkBitmap::kA8_Config:
        case SkBitmap:: kIndex8_Config:
            // x is fine as is for the calculation
            break;

        case SkBitmap::kRGB_565_Config:
        case SkBitmap::kARGB_4444_Config:
            x <<= 1;
            break;

        case SkBitmap::kARGB_8888_Config:
            x <<= 2;
            break;

        case SkBitmap::kNo_Config:
        case SkBitmap::kA1_Config:
        default:
            return SUB_OFFSET_FAILURE;
    }
    return y * bm.rowBytes() + x;
}


bool SkBitmap::extractSubset(SkBitmap* result, const SkIRect& subset) const {
	ALOGE("SkBitmap::extractSubset not implemented!");
	abort();
    return false;
}

///////////////////////////////////////////////////////////////////////////////


bool SkBitmap::deepCopyTo(SkBitmap* dst, Config dstConfig) const {
    if (!this->canCopyTo(dstConfig)) {
        return false;
    }

    // If we have a PixelRef, and it supports deep copy, use it.
    // Currently supported only by texture-backed bitmaps.
    if (fPixelRef) {
        SkPixelRef* pixelRef = fPixelRef->deepCopy(dstConfig);
        if (pixelRef) {
            uint32_t rowBytes;
            if (dstConfig == fConfig) {
                pixelRef->fGenerationID = fPixelRef->getGenerationID();
                // Use the same rowBytes as the original.
                rowBytes = fRowBytes;
            } else {
                // With the new config, an appropriate fRowBytes will be computed by setConfig.
                rowBytes = 0;
            }
            dst->setConfig(dstConfig, fWidth, fHeight, rowBytes);

            size_t pixelRefOffset;
            if (0 == fPixelRefOffset || dstConfig == fConfig) {
                // Use the same offset as the original.
                pixelRefOffset = fPixelRefOffset;
            } else {
                // Find the correct offset in the new config. This needs to be done after calling
                // setConfig so dst's fConfig and fRowBytes have been set properly.
                int32_t x, y;
                if (!get_upper_left_from_offset(*this, &x, &y)) {
                    return false;
                }
                pixelRefOffset = get_sub_offset(*dst, x, y);
                if (SUB_OFFSET_FAILURE == pixelRefOffset) {
                    return false;
                }
            }
            dst->setPixelRef(pixelRef, pixelRefOffset)->unref();
            return true;
        }
    }

    if (this->getTexture()) {
        return false;
    } else {
        return this->copyTo(dst, dstConfig, NULL);
    }
}

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

static void downsampleby2_proc32(SkBitmap* dst, int x, int y,
                                 const SkBitmap& src) {
    x <<= 1;
    y <<= 1;
    const SkPMColor* p = src.getAddr32(x, y);
    const SkPMColor* baseP = p;
    SkPMColor c, ag, rb;

    c = *p; ag = (c >> 8) & 0xFF00FF; rb = c & 0xFF00FF;
    if (x < src.width() - 1) {
        p += 1;
    }
    c = *p; ag += (c >> 8) & 0xFF00FF; rb += c & 0xFF00FF;

    p = baseP;
    if (y < src.height() - 1) {
        p += src.rowBytes() >> 2;
    }
    c = *p; ag += (c >> 8) & 0xFF00FF; rb += c & 0xFF00FF;
    if (x < src.width() - 1) {
        p += 1;
    }
    c = *p; ag += (c >> 8) & 0xFF00FF; rb += c & 0xFF00FF;

    *dst->getAddr32(x >> 1, y >> 1) =
        ((rb >> 2) & 0xFF00FF) | ((ag << 6) & 0xFF00FF00);
}

static inline uint32_t expand16(U16CPU c) {
    return (c & ~SK_G16_MASK_IN_PLACE) | ((c & SK_G16_MASK_IN_PLACE) << 16);
}

// returns dirt in the top 16bits, but we don't care, since we only
// store the low 16bits.
static inline U16CPU pack16(uint32_t c) {
    return (c & ~SK_G16_MASK_IN_PLACE) | ((c >> 16) & SK_G16_MASK_IN_PLACE);
}

static void downsampleby2_proc16(SkBitmap* dst, int x, int y,
                                 const SkBitmap& src) {
    x <<= 1;
    y <<= 1;
    const uint16_t* p = src.getAddr16(x, y);
    const uint16_t* baseP = p;
    SkPMColor       c;

    c = expand16(*p);
    if (x < src.width() - 1) {
        p += 1;
    }
    c += expand16(*p);

    p = baseP;
    if (y < src.height() - 1) {
        p += src.rowBytes() >> 1;
    }
    c += expand16(*p);
    if (x < src.width() - 1) {
        p += 1;
    }
    c += expand16(*p);

    *dst->getAddr16(x >> 1, y >> 1) = (uint16_t)pack16(c >> 2);
}

static uint32_t expand4444(U16CPU c) {
    return (c & 0xF0F) | ((c & ~0xF0F) << 12);
}

static U16CPU collaps4444(uint32_t c) {
    return (c & 0xF0F) | ((c >> 12) & ~0xF0F);
}

static void downsampleby2_proc4444(SkBitmap* dst, int x, int y,
                                   const SkBitmap& src) {
    x <<= 1;
    y <<= 1;
    const uint16_t* p = src.getAddr16(x, y);
    const uint16_t* baseP = p;
    uint32_t        c;

    c = expand4444(*p);
    if (x < src.width() - 1) {
        p += 1;
    }
    c += expand4444(*p);

    p = baseP;
    if (y < src.height() - 1) {
        p += src.rowBytes() >> 1;
    }
    c += expand4444(*p);
    if (x < src.width() - 1) {
        p += 1;
    }
    c += expand4444(*p);

    *dst->getAddr16(x >> 1, y >> 1) = (uint16_t)collaps4444(c >> 2);
}

void SkBitmap::buildMipMap(bool forceRebuild) {
	ALOGE("SkBitmap::buildMipMap not implemented!");
	abort();
}

bool SkBitmap::hasMipMap() const {
	ALOGE("SkBitmap::hasMipMap not implemented!");
	abort();
    return false;
}

int SkBitmap::extractMipLevel(SkBitmap* dst, SkFixed sx, SkFixed sy) {
	ALOGE("SkBitmap::extractMipLevel not implemented!");
	abort();
    return 0;
}

SkFixed SkBitmap::ComputeMipLevel(SkFixed sx, SkFixed sy) {
	ALOGE("SkBitmap::ComputeMipLevel not implemented!");
	abort();
    return SkIntToFixed(0);
}

///////////////////////////////////////////////////////////////////////////////

static bool GetBitmapAlpha(const SkBitmap& src, uint8_t* SK_RESTRICT alpha,
                           int alphaRowBytes) {
	ALOGE("bool GetBitmapAlpha not implemented!");
	abort();
    return false;
}

#include "SkPaint.h"
#include "SkMaskFilter.h"
#include "SkMatrix.h"

bool SkBitmap::extractAlpha(SkBitmap* dst, const SkPaint* paint,
                            Allocator *allocator, SkIPoint* offset) const {
	ALOGE("SkBitmap::extractAlpha not implemented!");
	abort();
    return false;
}

///////////////////////////////////////////////////////////////////////////////

enum {
    SERIALIZE_PIXELTYPE_NONE,
    SERIALIZE_PIXELTYPE_REF_DATA
};

void SkBitmap::flatten(SkFlattenableWriteBuffer& buffer) const {
	ALOGE("SkBitmap::flatten not implemented!");
	abort();
}

void SkBitmap::unflatten(SkFlattenableReadBuffer& buffer) {
	ALOGE("SkBitmap::unflatten not implemented!");
	abort();
}

///////////////////////////////////////////////////////////////////////////////

SkBitmap::RLEPixels::RLEPixels(int width, int height) {
	ALOGE("SkBitmap::RLEPixels not implemented!");
	abort();
}

SkBitmap::RLEPixels::~RLEPixels() {
	ALOGE("SkBitmap::~RLEPixels not implemented!");
	abort();
}

///////////////////////////////////////////////////////////////////////////////

#ifdef SK_DEBUG
void SkBitmap::validate() const {
}
#endif
