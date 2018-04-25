/**
 * @file     morpho_quadr_int.h
 * @brief    Structure definition of quadrangle data
 * @version  1.0.0
 * @date     2011-02-22
 *
 * Copyright (C) 2011 Morpho, Inc.
 */

#ifndef MORPHO_QUADR_INT_H
#define MORPHO_QUADR_INT_H

#ifdef __cplusplus
extern "C" {
#endif

/** Quadranble Data. */
typedef struct {
    int x1;                    /**< X-coordinate of top left corner */
    int y1;                    /**< Y-coordinate of top left corner */
    int x2;                    /**< X-coordinate of top right corner */
    int y2;                    /**< Y-coordinate of top right corner */
    int x3;                    /**< X-coordinate of bottom left corner */
    int y3;                    /**< Y-coordinate of bottom left corner */
    int x4;                    /**< X-coordinate of bottom right corner */
    int y4;                    /**< Y-coordinate of bottom right corner */
} morpho_QuadrInt;

/** Set the vertex coordinate of the quadrangle area "quadr". */
#define morpho_QuadrInt_setQuadr(quadr, ltx, lty, rtx, rty, lbx, lby, rbx, rby) do { \
    (quadr)->x1=(ltx);\
    (quadr)->y1=(lty);\
    (quadr)->x2=(rtx);\
    (quadr)->y2=(rty);\
    (quadr)->x3=(lbx);\
    (quadr)->y3=(lby);\
    (quadr)->x4=(rbx);\
    (quadr)->y4=(rby);\
 } while(0)

#ifdef __cplusplus
}
#endif

#endif /* #ifndef MORPHO_QUADR_INT_H */
