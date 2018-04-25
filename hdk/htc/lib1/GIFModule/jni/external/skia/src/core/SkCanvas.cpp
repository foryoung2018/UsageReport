
/*
 * Copyright 2008 The Android Open Source Project
 *
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */


#include "SkCanvas.h"
#include "SkBounder.h"
#include "SkDevice.h"
#include "SkDeviceImageFilterProxy.h"
#include "SkDraw.h"
#include "SkDrawFilter.h"
#include "SkDrawLooper.h"
#include "SkMetaData.h"
#include "SkPathOps.h"
#include "SkPicture.h"
#include "SkRasterClip.h"
#include "SkRRect.h"
#include "SkScalarCompare.h"

//+[Abel]
//#include "SkSurface_Base.h"
//-[Abel]

#include "SkTemplates.h"
#include "SkTextFormatParams.h"
#include "SkTLazy.h"
#include "SkUtils.h"

SK_DEFINE_INST_COUNT(SkBounder)
SK_DEFINE_INST_COUNT(SkCanvas)
SK_DEFINE_INST_COUNT(SkDrawFilter)

#define CHECK_LOCKCOUNT_BALANCE(bitmap)
#define CHECK_SHADER_NOSETCONTEXT(paint)

#ifdef SK_TRACE_SAVERESTORE
    static int gLayerCounter;
    static void inc_layer() { ++gLayerCounter; printf("----- inc layer %d\n", gLayerCounter); }
    static void dec_layer() { --gLayerCounter; printf("----- dec layer %d\n", gLayerCounter); }

    static int gRecCounter;
    static void inc_rec() { ++gRecCounter; printf("----- inc rec %d\n", gRecCounter); }
    static void dec_rec() { --gRecCounter; printf("----- dec rec %d\n", gRecCounter); }

    static int gCanvasCounter;
    static void inc_canvas() { ++gCanvasCounter; printf("----- inc canvas %d\n", gCanvasCounter); }
    static void dec_canvas() { --gCanvasCounter; printf("----- dec canvas %d\n", gCanvasCounter); }
#else
    #define inc_layer()
    #define dec_layer()
    #define inc_rec()
    #define dec_rec()
    #define inc_canvas()
    #define dec_canvas()
#endif

///////////////////////////////////////////////////////////////////////////////

/*  This is the record we keep for each SkDevice that the user installs.
    The clip/matrix/proc are fields that reflect the top of the save/restore
    stack. Whenever the canvas changes, it marks a dirty flag, and then before
    these are used (assuming we're not on a layer) we rebuild these cache
    values: they reflect the top of the save stack, but translated and clipped
    by the device's XY offset and bitmap-bounds.
*/
struct DeviceCM {
    DeviceCM*           fNext;
    SkDevice*           fDevice;
    SkRasterClip        fClip;
    const SkMatrix*     fMatrix;
    SkPaint*            fPaint; // may be null (in the future)

    DeviceCM(SkDevice* device, int x, int y, const SkPaint* paint, SkCanvas* canvas)
            : fNext(NULL) {
        if (NULL != device) {
            device->ref();
            device->onAttachToCanvas(canvas);
        }
        fDevice = device;
        fPaint = paint ? SkNEW_ARGS(SkPaint, (*paint)) : NULL;
    }

    ~DeviceCM() {
        if (NULL != fDevice) {
            fDevice->onDetachFromCanvas();
            fDevice->unref();
        }
        SkDELETE(fPaint);
    }

    void updateMC(const SkMatrix& totalMatrix, const SkRasterClip& totalClip,
                  const SkClipStack& clipStack, SkRasterClip* updateClip) {
        int x = fDevice->getOrigin().x();
        int y = fDevice->getOrigin().y();
        int width = fDevice->width();
        int height = fDevice->height();

        if ((x | y) == 0) {
            fMatrix = &totalMatrix;
            fClip = totalClip;
        } else {
            fMatrixStorage = totalMatrix;
            fMatrixStorage.postTranslate(SkIntToScalar(-x),
                                         SkIntToScalar(-y));
            fMatrix = &fMatrixStorage;

            totalClip.translate(-x, -y, &fClip);
        }

        fClip.op(SkIRect::MakeWH(width, height), SkRegion::kIntersect_Op);

        // intersect clip, but don't translate it (yet)

        if (updateClip) {
            updateClip->op(SkIRect::MakeXYWH(x, y, width, height),
                           SkRegion::kDifference_Op);
        }

        fDevice->setMatrixClip(*fMatrix, fClip.forceGetBW(), clipStack);

#ifdef SK_DEBUG
        if (!fClip.isEmpty()) {
            SkIRect deviceR;
            deviceR.set(0, 0, width, height);
            SkASSERT(deviceR.contains(fClip.getBounds()));
        }
#endif
    }

private:
    SkMatrix    fMatrixStorage;
};

/*  This is the record we keep for each save/restore level in the stack.
    Since a level optionally copies the matrix and/or stack, we have pointers
    for these fields. If the value is copied for this level, the copy is
    stored in the ...Storage field, and the pointer points to that. If the
    value is not copied for this level, we ignore ...Storage, and just point
    at the corresponding value in the previous level in the stack.
*/
class SkCanvas::MCRec {
public:
    MCRec*          fNext;
    SkMatrix*       fMatrix;        // points to either fMatrixStorage or prev MCRec
    SkRasterClip*   fRasterClip;    // points to either fRegionStorage or prev MCRec
    SkDrawFilter*   fFilter;        // the current filter (or null)

    DeviceCM*   fLayer;
    /*  If there are any layers in the stack, this points to the top-most
        one that is at or below this level in the stack (so we know what
        bitmap/device to draw into from this level. This value is NOT
        reference counted, since the real owner is either our fLayer field,
        or a previous one in a lower level.)
    */
    DeviceCM*   fTopLayer;

    MCRec(const MCRec* prev, int flags) {
        if (NULL != prev) {
            if (flags & SkCanvas::kMatrix_SaveFlag) {
                fMatrixStorage = *prev->fMatrix;
                fMatrix = &fMatrixStorage;
            } else {
                fMatrix = prev->fMatrix;
            }

            if (flags & SkCanvas::kClip_SaveFlag) {
                fRasterClipStorage = *prev->fRasterClip;
                fRasterClip = &fRasterClipStorage;
            } else {
                fRasterClip = prev->fRasterClip;
            }

            fFilter = prev->fFilter;
            SkSafeRef(fFilter);

            fTopLayer = prev->fTopLayer;
        } else {   // no prev
            fMatrixStorage.reset();

            fMatrix     = &fMatrixStorage;
            fRasterClip = &fRasterClipStorage;
            fFilter     = NULL;
            fTopLayer   = NULL;
        }
        fLayer = NULL;

        // don't bother initializing fNext
        inc_rec();
    }
    ~MCRec() {
        SkSafeUnref(fFilter);
        SkDELETE(fLayer);
        dec_rec();
    }

private:
    SkMatrix        fMatrixStorage;
    SkRasterClip    fRasterClipStorage;
};


class SkDrawIter : public SkDraw {
public:
    SkDrawIter(SkCanvas* canvas, bool skipEmptyClips = true) {
        canvas = canvas->canvasForDrawIter();
        fCanvas = canvas;
        canvas->updateDeviceCMCache();

        fClipStack = &canvas->fClipStack;
        fBounder = canvas->getBounder();
        fCurrLayer = canvas->fMCRec->fTopLayer;
        fSkipEmptyClips = skipEmptyClips;
    }

    bool next() {
        // skip over recs with empty clips
        if (fSkipEmptyClips) {
            while (fCurrLayer && fCurrLayer->fClip.isEmpty()) {
                fCurrLayer = fCurrLayer->fNext;
            }
        }

        const DeviceCM* rec = fCurrLayer;
        if (rec && rec->fDevice) {

            fMatrix = rec->fMatrix;
            fClip   = &((SkRasterClip*)&rec->fClip)->forceGetBW();
            fRC     = &rec->fClip;
            fDevice = rec->fDevice;
            fBitmap = &fDevice->accessBitmap(true);
            fPaint  = rec->fPaint;
            SkDEBUGCODE(this->validate();)

            fCurrLayer = rec->fNext;
            if (fBounder) {
                fBounder->setClip(fClip);
            }
            // fCurrLayer may be NULL now

            return true;
        }
        return false;
    }

    SkDevice* getDevice() const { return fDevice; }
    int getX() const { return fDevice->getOrigin().x(); }
    int getY() const { return fDevice->getOrigin().y(); }
    const SkMatrix& getMatrix() const { return *fMatrix; }
    const SkRegion& getClip() const { return *fClip; }
    const SkPaint* getPaint() const { return fPaint; }

private:
    SkCanvas*       fCanvas;
    const DeviceCM* fCurrLayer;
    const SkPaint*  fPaint;     // May be null.
    SkBool8         fSkipEmptyClips;

    typedef SkDraw INHERITED;
};

typedef SkTLazy<SkPaint> SkLazyPaint;

class AutoDrawLooper {
public:
    AutoDrawLooper(SkCanvas* canvas, const SkPaint& paint,
                   bool skipLayerForImageFilter = false) : fOrigPaint(paint) {
        fCanvas = canvas;
        fLooper = paint.getLooper();
        fFilter = canvas->getDrawFilter();
        fPaint = NULL;
        fSaveCount = canvas->getSaveCount();
        fDoClearImageFilter = false;
        fDone = false;

        if (!skipLayerForImageFilter && fOrigPaint.getImageFilter()) {
            SkPaint tmp;
            tmp.setImageFilter(fOrigPaint.getImageFilter());
            // it would be nice if we had a guess at the bounds, instead of null
            (void)canvas->internalSaveLayer(NULL, &tmp,
                                    SkCanvas::kARGB_ClipLayer_SaveFlag, true);
            // we'll clear the imageFilter for the actual draws in next(), so
            // it will only be applied during the restore().
            fDoClearImageFilter = true;
        }

        if (fLooper) {
            fLooper->init(canvas);
            fIsSimple = false;
        } else {
            // can we be marked as simple?
            fIsSimple = !fFilter && !fDoClearImageFilter;
        }
    }

    ~AutoDrawLooper() {
        if (fDoClearImageFilter) {
            fCanvas->internalRestore();
        }
        SkASSERT(fCanvas->getSaveCount() == fSaveCount);
    }

    const SkPaint& paint() const {
        SkASSERT(fPaint);
        return *fPaint;
    }

    bool next(SkDrawFilter::Type drawType) {
        if (fDone) {
            return false;
        } else if (fIsSimple) {
            fDone = true;
            fPaint = &fOrigPaint;
            return !fPaint->nothingToDraw();
        } else {
            return this->doNext(drawType);
        }
    }

private:
    SkLazyPaint     fLazyPaint;
    SkCanvas*       fCanvas;
    const SkPaint&  fOrigPaint;
    SkDrawLooper*   fLooper;
    SkDrawFilter*   fFilter;
    const SkPaint*  fPaint;
    int             fSaveCount;
    bool            fDoClearImageFilter;
    bool            fDone;
    bool            fIsSimple;

    bool doNext(SkDrawFilter::Type drawType);
};

bool AutoDrawLooper::doNext(SkDrawFilter::Type drawType) {
    fPaint = NULL;
    SkASSERT(!fIsSimple);
    SkASSERT(fLooper || fFilter || fDoClearImageFilter);

    SkPaint* paint = fLazyPaint.set(fOrigPaint);

    if (fDoClearImageFilter) {
        paint->setImageFilter(NULL);
    }

    if (fLooper && !fLooper->next(fCanvas, paint)) {
        fDone = true;
        return false;
    }
    if (fFilter) {
        if (!fFilter->filter(paint, drawType)) {
            fDone = true;
            return false;
        }
        if (NULL == fLooper) {
            // no looper means we only draw once
            fDone = true;
        }
    }
    fPaint = paint;

    // if we only came in here for the imagefilter, mark us as done
    if (!fLooper && !fFilter) {
        fDone = true;
    }

    // call this after any possible paint modifiers
    if (fPaint->nothingToDraw()) {
        fPaint = NULL;
        return false;
    }
    return true;
}

/*  Stack helper for managing a SkBounder. In the destructor, if we were
    given a bounder, we call its commit() method, signifying that we are
    done accumulating bounds for that draw.
*/
class SkAutoBounderCommit {
public:
    SkAutoBounderCommit(SkBounder* bounder) : fBounder(bounder) {}
    ~SkAutoBounderCommit() {
        if (NULL != fBounder) {
            fBounder->commit();
        }
    }
private:
    SkBounder*  fBounder;
};


////////// macros to place around the internal draw calls //////////////////

#define LOOPER_BEGIN_DRAWDEVICE(paint, type)                        \
    this->predrawNotify();                                          \
    AutoDrawLooper  looper(this, paint, true);                      \
    while (looper.next(type)) {                                     \
        SkAutoBounderCommit ac(fBounder);                           \
        SkDrawIter          iter(this);

#define LOOPER_BEGIN(paint, type)                                   \
    this->predrawNotify();                                          \
    AutoDrawLooper  looper(this, paint);                            \
    while (looper.next(type)) {                                     \
        SkAutoBounderCommit ac(fBounder);                           \
        SkDrawIter          iter(this);

#define LOOPER_END    }

////////////////////////////////////////////////////////////////////////////

// can't draw it if its empty, or its too big for a fixed-point width or height
static bool reject_bitmap(const SkBitmap& bitmap) {
    return  bitmap.width() <= 0 || bitmap.height() <= 0;
}

void SkCanvas::internalDrawBitmap(const SkBitmap& bitmap,
                                const SkMatrix& matrix, const SkPaint* paint) {
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

bool SkCanvas::quickReject(const SkRect& rect) const {

    if (!rect.isFinite())
        return true;

    if (fMCRec->fRasterClip->isEmpty()) {
        return true;
    }

    if (fMCRec->fMatrix->hasPerspective()) {
        SkRect dst;
        fMCRec->fMatrix->mapRect(&dst, rect);
        SkIRect idst;
        dst.roundOut(&idst);
        return !SkIRect::Intersects(idst, fMCRec->fRasterClip->getBounds());
    } else {
        const SkRectCompareType& clipR = this->getLocalClipBoundsCompareType();

        // for speed, do the most likely reject compares first
        SkScalarCompareType userT = SkScalarToCompareType(rect.fTop);
        SkScalarCompareType userB = SkScalarToCompareType(rect.fBottom);
        if (userT >= clipR.fBottom || userB <= clipR.fTop) {
            return true;
        }
        SkScalarCompareType userL = SkScalarToCompareType(rect.fLeft);
        SkScalarCompareType userR = SkScalarToCompareType(rect.fRight);
        if (userL >= clipR.fRight || userR <= clipR.fLeft) {
            return true;
        }
        return false;
    }
}

void SkCanvas::drawBitmap(const SkBitmap& bitmap, SkScalar x, SkScalar y,
                          const SkPaint* paint) {
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
    this->internalDrawBitmap(bitmap, matrix, paint);
}
