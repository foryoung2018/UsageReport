/*
 * Copyright (C) 2008 HTC Inc.
 *
 */
package com.htc.lib1.cc.widget;

/**
 * EasingFunction is a interface for all easing function.
 * @author jasonechiu
 * @deprecated [Not use any longer] Not support class
 */
/**@hide*/
public interface EasingFunction {

    /**
     * The easing function.
     * @param v0 initial velocity
     * @param t current time
     * @param b beginning value
     * @param c change in value
     * @param d duration
     * @return current value
     */
    float currentResult(float v0, float t, float b, float c, float d);

    static final float INTERPOLATE_PI = 3.14159f;
}
