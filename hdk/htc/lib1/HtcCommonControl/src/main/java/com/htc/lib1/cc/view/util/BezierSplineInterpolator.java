package com.htc.lib1.cc.view.util;

import android.view.animation.Interpolator;

/**
 * Interpolator using Bezier Spline.
 * Used in BlinkFeed and DecorFlow, for ease-in 'n' ease-out fling behavior.
 * @hide
 */
public class BezierSplineInterpolator implements Interpolator {

    private float m_fX1;
    private float m_fY1;
    private float m_fX2;
    private float m_fY2;

    public BezierSplineInterpolator(float fX1, float fY1, float fX2, float fY2) {
        m_fX1 = fX1;
        m_fY1 = fY1;
        m_fX2 = fX2;
        m_fY2 = fY2;
    }

    @Override
    public float getInterpolation(float fT) {
        if (m_fX1 == m_fY1 && m_fX2 == m_fY2) return fT; // linear
        return calcBezier(getTForX(fT), m_fY1, m_fY2);
    }

    private float getTForX(float fX) {
        // Newton raphson iteration
        float fGuessT = fX;
        for (int i = 0; i < 4; ++i) {
            float fCurrentSlope = getSlope(fGuessT, m_fX1, m_fX2);
            if (fCurrentSlope == 0.0) return fGuessT;
            float fCurrentX = calcBezier(fGuessT, m_fX1, m_fX2) - fX;
            fGuessT -= fCurrentX / fCurrentSlope;
        }
        return fGuessT;
    }

    private float calcBezier(float fT, float fA1, float fA2) {
        return ((calcA(fA1, fA2)*fT + calcB(fA1, fA2))*fT + calcC(fA1))*fT;
    }

    // Returns dx/dt given t, x1, and x2, or dy/dt given t, y1, and y2.
    private float getSlope(float fT, float fA1, float fA2) {
        return 3.0f * calcA(fA1, fA2)*fT*fT + 2.0f * calcB(fA1, fA2) * fT + calcC(fA1);
    }

    private float calcA(float aA1, float aA2) { return 1.0f - 3.0f * aA2 + 3.0f * aA1; }
    private float calcB(float aA1, float aA2) { return 3.0f * aA2 - 6.0f * aA1; }
    private float calcC(float aA1) { return 3.0f * aA1; }
}

