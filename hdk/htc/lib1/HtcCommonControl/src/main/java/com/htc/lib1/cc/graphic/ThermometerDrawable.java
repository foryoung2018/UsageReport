/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.htc.lib1.cc.graphic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * A Drawable that can help user easily make a thermometer .You can use
 * {@link #setTextColor(int, int)},{@link #setTextSize(int, int)},
 * {@link #setTextStyle(Context, int, int)} to custom the number/unit text style.You can also set
 * the thermometer number by {@link #setThermNumber(int)},and chose a unit type with
 * {@link #setThermUnit(int)}.
 * <p>
 * Use this drawable with a ImageView is very simple.The following is a sample:
 * </p>
 *
 * <pre class="prettyprint">
 * ThermometerDrawable thermometerDrawable = new ThermometerDrawable();
 * thermometerDrawable.setThermUnit(ThermometerDrawable.UNIT_TYPE_SIGN);
 * thermometerDrawable.setTextStyle(this, ThermometerDrawable.TYPE_NUMBER, R.style.weather_01);
 * thermometerDrawable.setTextStyle(this, ThermometerDrawable.TYPE_THERM, R.style.weather_02);
 * thermometerDrawable.setThermNumber(-3);
 *
 * ImageView imageView = (ImageView) findViewById(R.id.image1);
 * imageView.setImageDrawable(thermometerDrawable);
 *
 * </pre>
 *
 * @hide
 * @deprecated try level not release.
 */
@Deprecated
public class ThermometerDrawable extends Drawable {

    /**
     * Normal degree unit.
     *
     * @see #setThermUnit(int)
     */
    public static final int UNIT_TYPE_SIGN = Thermometer.UNIT_TYPE_SIGN;

    /**
     * Fahrenheit degree unit.
     *
     * @see #setThermUnit(int)
     */
    public static final int UNIT_TYPE_FAHRENHEIT = Thermometer.UNIT_TYPE_FAHRENHEIT;

    /**
     * Celsius degree unit.
     *
     * @see #setThermUnit(int)
     */
    public static final int UNIT_TYPE_CELSIUS = Thermometer.UNIT_TYPE_CELSIUS;

    /**
     * Type flag to set the TextColor/TextSize/TextStyle for thermometer number text.
     *
     * @see #setTextColor(int, int)
     * @see #setTextSize(int, int)
     * @see #setTextStyle(Context, int, int)
     */
    public static final int TYPE_NUMBER = Thermometer.TYPE_NUMBER;

    /**
     * Type flag to set the TextColor/TextSize/TextStyle for thermometer unit text.
     *
     * @see #setTextColor(int, int)
     * @see #setTextSize(int, int)
     * @see #setTextStyle(Context, int, int)
     */
    public static final int TYPE_THERM = Thermometer.TYPE_THERM;

    private Thermometer mThermometer;

    public ThermometerDrawable() {
        mThermometer = new Thermometer();
    }

    /**
     * Set the thermometer unit.
     *
     * @param type should be one of {@link #UNIT_TYPE_SIGN} ,{@link #UNIT_TYPE_FAHRENHEIT},
     *            {@link #UNIT_TYPE_CELSIUS}.
     */
    public void setThermUnit(int type) {
        mThermometer.setThermUnit(type);
        invalidateSelf();
    }

    public void setThermNumber(int num) {
        mThermometer.setThermNumber(num);
        invalidateSelf();
    }

    /**
     * @param paintType should be one of {@link #TYPE_NUMBER} ,{@link #TYPE_THERM}.
     * @param color the color you want to set.
     */
    public void setTextColor(int paintType, int color) {
        mThermometer.setPaintColor(paintType, color);
        invalidateSelf();
    }

    /**
     * @param paintType should be one of {@link #TYPE_NUMBER} ,{@link #TYPE_THERM}.
     * @param size the size you want to set.
     */
    public void setTextSize(int paintType, int size) {
        mThermometer.setPaintSize(paintType, size);
        invalidateSelf();
    }

    /**
     * @param paintType should be one of {@link #TYPE_NUMBER} ,{@link #TYPE_THERM}.
     * @param styleId the styleId you want to apply.
     */
    public void setTextStyle(Context context, int paintType, int styleId) {
        mThermometer.setPaintStyle(context, paintType, styleId);
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        final Rect bounds = getBounds();
        if (bounds.isEmpty()) return;

        final int count = canvas.save();
        canvas.clipRect(bounds);
        if (bounds.left != 0 || bounds.top != 0) {
            canvas.translate(bounds.left, bounds.top);
        }
        mThermometer.draw(canvas);
        canvas.restoreToCount(count);
    }
    @Override
    public void setAlpha(int alpha) {
        mThermometer.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mThermometer.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public int getIntrinsicWidth() {
        return mThermometer.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mThermometer.getIntrinsicHeight();
    }

}
