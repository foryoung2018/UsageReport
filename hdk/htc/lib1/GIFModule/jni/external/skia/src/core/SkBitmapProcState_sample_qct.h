
#include "SkUtils.h"

#if defined(QCTPROJ)
#if defined(USE_S16_OPAQUE) && defined(__ARM_HAVE_NEON)

#define FILTER_PROC(x, y, a, b, c, d, dst) \
    do {                                                        \
        uint32_t tmp = Filter_565_Expanded(x, y, a, b, c, d);   \
        *(dst) = SkExpanded_565_To_PMColor(tmp);                \
    } while (0)

#define MAKENAME(suffix)        NAME_WRAP(S16_opaque_D32 ## suffix)
#define DSTSIZE                 32
#define SRCTYPE                 uint16_t
#define CHECKSTATE(state)       SkASSERT(state.fBitmap->config() == SkBitmap::kRGB_565_Config); \
                                SkASSERT(state.fAlphaScale == 256)
#define RETURNDST(src)          SkPixel16ToPixel32(src)
#define SRC_TO_FILTER(src)      src

#define TILEX_PROCF(fx, max)    SkClampMax((fx) >> 16, max)
#define TILEY_PROCF(fy, max)    SkClampMax((fy) >> 16, max)
#define TILEX_LOW_BITS(fx, max) (((fx) >> 12) & 0xF)
#define TILEY_LOW_BITS(fy, max) (((fy) >> 12) & 0xF)

#if DSTSIZE==32
    #define DSTTYPE SkPMColor
#elif DSTSIZE==16
    #define DSTTYPE uint16_t
#else
    #error "need DSTSIZE to be 32 or 16"
#endif

#if (DSTSIZE == 32)
    #define BITMAPPROC_MEMSET(ptr, value, n) sk_memset32(ptr, value, n)
#elif (DSTSIZE == 16)
    #define BITMAPPROC_MEMSET(ptr, value, n) sk_memset16(ptr, value, n)
#else
    #error "unsupported DSTSIZE"
#endif

extern "C" void Blit_Pixel16ToPixel32( uint32_t * colors, const uint16_t *srcAddr, int n );
void clampx_nofilter_trans_S16_D32_DX(const SkBitmapProcState& s,
                                  uint32_t xy[], int count, int x, int y, DSTTYPE* SK_RESTRICT colors) {
#ifdef PREAMBLE
    PREAMBLE(s);
#endif

    SkASSERT((s.fInvType & ~SkMatrix::kTranslate_Mask) == 0);

    //int xpos = nofilter_trans_preamble(s, &xy, x, y);
    SkPoint pt;
    s.fInvProc(s.fInvMatrix, SkIntToScalar(x) + SK_ScalarHalf,
               SkIntToScalar(y) + SK_ScalarHalf, &pt);
    uint32_t Y = s.fIntTileProcY(SkScalarToFixed(pt.fY) >> 16,
                           s.fBitmap->height());
    int xpos = SkScalarToFixed(pt.fX) >> 16;

    const SRCTYPE* SK_RESTRICT srcAddr = (const SRCTYPE*)s.fBitmap->getPixels();
    SRCTYPE src;

    // buffer is y32, x16, x16, x16, x16, x16
    // bump srcAddr to the proper row, since we're told Y never changes
    //SkASSERT((unsigned)orig_xy[0] < (unsigned)s.fBitmap->height());
    //srcAddr = (const SRCTYPE*)((const char*)srcAddr +
    //                                            orig_xy[0] * s.fBitmap->rowBytes());
    SkASSERT((unsigned)Y < (unsigned)s.fBitmap->height());
    srcAddr = (const SRCTYPE*)((const char*)srcAddr +
                                                Y * s.fBitmap->rowBytes());
    const int width = s.fBitmap->width();
    int n;
    if (1 == width) {
        // all of the following X values must be 0
        memset(xy, 0, count * sizeof(uint16_t));
        src = srcAddr[0];
        DSTTYPE dstValue = RETURNDST(src);
        BITMAPPROC_MEMSET(colors, dstValue, count);
        //return;
        goto done_sample;
    }


    // fill before 0 as needed
    if (xpos < 0) {
        n = -xpos;
        if (n > count) {
            n = count;
        }
        src = srcAddr[0];
        for( int i = 0; i < n ; i++ ){
            *colors++ = RETURNDST(src);
        }

        count -= n;
        if (0 == count) {
            //return;
            goto done_sample;
        }
        xpos = 0;
    }

    // fill in 0..width-1 if needed
    if (xpos < width) {
        n = width - xpos;
        if (n > count) {
            n = count;
        }
        //for (int i = 0; i < n; i++) {
        //    src = srcAddr[xpos++];
        //    *colors++ = RETURNDST(src);
        //}
        Blit_Pixel16ToPixel32(colors, (uint16_t *)&(srcAddr[xpos]), n );
#if defined(QCTPROJ)
        colors += n;
#endif
        count -= n;
        if (0 == count) {
            //return;
            goto done_sample;
        }
    }

    for (int i = 0; i < count; i++) {
        src = srcAddr[width - 1];
        *colors++ = RETURNDST(src);
    }

done_sample:
#ifdef POSTAMBLE
    POSTAMBLE(s);
#endif
    return ;
}

#undef MAKENAME
#undef DSTSIZE
#undef DSTTYPE
#undef SRCTYPE
#undef CHECKSTATE
#undef RETURNDST
#undef SRC_TO_FILTER
#undef FILTER_TO_DST

#ifdef PREAMBLE
    #undef PREAMBLE
#endif
#ifdef POSTAMBLE
    #undef POSTAMBLE
#endif

#undef FILTER_PROC_TYPE
#undef GET_FILTER_TABLE
#undef GET_FILTER_ROW
#undef GET_FILTER_ROW_PROC
#undef GET_FILTER_PROC
#undef BITMAPPROC_MEMSET

#endif
#endif
