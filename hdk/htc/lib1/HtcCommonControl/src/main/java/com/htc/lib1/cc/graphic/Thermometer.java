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
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.htc.lib1.cc.util.res.HtcResUtil;

/**
 * @hide
 */
/* package */class Thermometer {
    public static final int UNIT_TYPE_SIGN = 0;
    public static final int UNIT_TYPE_FAHRENHEIT = 1;
    public static final int UNIT_TYPE_CELSIUS = 2;

    private int mThermType = UNIT_TYPE_CELSIUS;

    public static final String DEGREE_SIGN = "\u00B0";
    public static final String DEGREE_FAHRENHEIT = "\u2109";
    public static final String DEGREE_CELSIUS = "\u2103";

    public static final int TYPE_NUMBER = 0;
    public static final int TYPE_THERM = 1;

    private TextPaint mNumPaint;
    private TextPaint mThermPaint;

    private StaticLayout mNumLayout;
    private StaticLayout mThermLayout;

    private String mNumSequence = "0";
    private String mThermSequence = DEGREE_CELSIUS;

    private Rect mNumRect = new Rect();
    private Rect mThermRect = new Rect();
    private int mNumCharHeight;
    private int mThermCharNormalHeight;

    private boolean mNeedUpdate = true;

    public Thermometer() {
        mNumPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mNumPaint.setTextSize(82);
        mNumPaint.setColor(0xff1ac6fe);
        mThermPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mThermPaint.setTextSize(48);
        mThermPaint.setColor(0xff1ac6fe);
    }

    public void setColorFilter(ColorFilter cf) {
        mNumPaint.setColorFilter(cf);
        mThermPaint.setColorFilter(cf);
    }

    public void setAlpha(int alpha) {
        mNumPaint.setAlpha(alpha);
        mThermPaint.setAlpha(alpha);
    }

    public void setThermNumber(int number) {
        mNumSequence = String.valueOf(number);
        mNeedUpdate = true;
    }

    public void setThermUnit(int type) {
        if (type == mThermType) return;

        mThermType = type;
        if (mThermType == UNIT_TYPE_SIGN) {
            setThermString(DEGREE_SIGN);
        } else if (mThermType == UNIT_TYPE_FAHRENHEIT) {
            setThermString(DEGREE_FAHRENHEIT);
        } else {
            setThermString(DEGREE_CELSIUS);
        }
    }

    private void setThermString(String thermString) {
        mThermSequence = thermString;
        mNeedUpdate = true;
    }

    public void setPaintColor(int paintType, int color) {
        if (paintType == TYPE_NUMBER) {
            mNumPaint.setColor(color);
        } else if (paintType == TYPE_THERM) {
            mThermPaint.setColor(color);
        }
    }

    public void setPaintSize(int paintType, int size) {
        if (paintType == TYPE_NUMBER) {
            mNumPaint.setTextSize(size);
        } else if (paintType == TYPE_THERM) {
            mThermPaint.setTextSize(size);
        }
        mNeedUpdate = true;
    }

    public void setPaintStyle(Context context, int paintType, int styleId) {
        if (paintType == TYPE_NUMBER) {
            HtcResUtil.setTextAppearance(context, styleId, mNumPaint);
        } else if (paintType == TYPE_THERM) {
            HtcResUtil.setTextAppearance(context, styleId, mThermPaint);
        }
        mNeedUpdate = true;
    }

    public int getIntrinsicWidth() {
        makeNewLayout();
        return mNumLayout.getWidth() + mThermLayout.getWidth();
    }

    public int getIntrinsicHeight() {
        makeNewLayout();
        return Math.max(mNumLayout.getHeight(), mThermLayout.getHeight());
    }

    public void draw(Canvas canvas) {
        makeNewLayout();

        canvas.save();
        mNumLayout.draw(canvas);

        final int numSpace = mNumLayout.getLineBaseline(0) - mNumCharHeight;
        final int thermSpace = mThermLayout.getLineBaseline(0) - mThermCharNormalHeight;
        canvas.translate(mNumLayout.getWidth(), numSpace - thermSpace);

        mThermLayout.draw(canvas);
        canvas.restore();
    }

    private void makeNewLayout() {
        if (mNumLayout == null || mNeedUpdate) {
            mNumPaint.getTextBounds(mNumSequence, 0, mNumSequence.length(), mNumRect);
            mNumCharHeight = mNumRect.height();

            final int desiredWidth = (int) Layout.getDesiredWidth(mNumSequence, mNumPaint);
            mNumLayout = new StaticLayout(mNumSequence, mNumPaint, desiredWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }

        if (mThermLayout == null || mNeedUpdate) {
            mThermPaint.getTextBounds(DEGREE_CELSIUS, 0, DEGREE_FAHRENHEIT.length(), mThermRect);
            mThermCharNormalHeight = mThermRect.height();

            final int desiredWidth = (int) Layout.getDesiredWidth(mThermSequence, mThermPaint);
            mThermLayout = new StaticLayout(mThermSequence, mThermPaint, desiredWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }

        mNeedUpdate = false;
    }

}
