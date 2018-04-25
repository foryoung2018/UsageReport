/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.htc.lib1.cc.widget.quicktips;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.widget.RelativeLayout;

/*@hide*/
public class RotateRelativeLayout extends RelativeLayout {

    private String TAG = "RotateRelativeLayout";

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = SCREEN_MODE_PORTRAIT, to = "SCREEN_MODE_PORTRAIT"),
            @IntToString(from = SCREEN_MODE_LANDSCAPE, to = "SCREEN_MODE_LANDSCAPE"),
            @IntToString(from = SCREEN_MODE_IPORTRAIT, to = "SCREEN_MODE_IPORTRAIT"),
            @IntToString(from = SCREEN_MODE_ILANDSCAPE, to = "SCREEN_MODE_ILANDSCAPE")
    })
    private int current_orientation_mode;

    private RectF newRectF;
    private Matrix invMatrix;
    private Matrix rotMatrix;

    public static final int SCREEN_MODE_PORTRAIT = 0;

    public static final int SCREEN_MODE_LANDSCAPE = 1;

    public static final int SCREEN_MODE_IPORTRAIT = 2;

    public static final int SCREEN_MODE_ILANDSCAPE = 3;

    public RotateRelativeLayout(Context context) {
        // TODO Auto-generated constructor stub
        super(context);
        rotMatrix = new Matrix();
        invMatrix = new Matrix();
        newRectF = new RectF();
        current_orientation_mode = SCREEN_MODE_PORTRAIT;
    }

    public RotateRelativeLayout(Context context, AttributeSet attrs) {
        // TODO Auto-generated constructor stub
        super(context, attrs);
        rotMatrix = new Matrix();
        invMatrix = new Matrix();
        newRectF = new RectF();
        current_orientation_mode = SCREEN_MODE_PORTRAIT;
    }

    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
        // TODO Auto-generated method stub
        boolean p7;
        int i8;
        int i9;
        int i10;
        int i11;
        //Log.e(TAG, "onLayout left = " + arg1 + ", top = " + arg2 + ", right = " + arg3 + ", bottom = " + arg4);

        if(this.current_orientation_mode != SCREEN_MODE_PORTRAIT || this.current_orientation_mode == SCREEN_MODE_IPORTRAIT)
        {
            p7 = arg0;
            i9 = arg1;
            i8 = arg2;
            i11 = arg3;
            i10 = arg4;

            //Log.e(TAG, "onLayout left = " + i8 + ", top = " + i9 + ", right = " + i10 + ", bottom = " + i11);
            super.onLayout(p7, i8, i9, i10, i11);
            return;
        }

        super.onLayout(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        if (this.current_orientation_mode == SCREEN_MODE_PORTRAIT)
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        else {
            if (this.current_orientation_mode != SCREEN_MODE_IPORTRAIT
                    && (!mIsInverse || mIsArrowStyle))// If it's no arrow style and inverse case, don't swap the width with height
                super.onMeasure(heightMeasureSpec, widthMeasureSpec);
            else
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        }

        this.rotateMeasureMent();

        return;
    }

    protected void rotateMeasureMent() {
        //Log.e(TAG, "rotateMeasureMent height = " + this.getMeasuredHeight() + ", width = " + this.getMeasuredWidth());

        if(this.current_orientation_mode != SCREEN_MODE_IPORTRAIT && this.current_orientation_mode != SCREEN_MODE_PORTRAIT)
        {

            this.setMeasuredDimension(this.getMeasuredHeight(), this.getMeasuredWidth());

        }

        this.rotMatrix.reset();

        switch (this.current_orientation_mode) {
        case 0: // SCREEN_MODE_PORTRAIT
            this.rotMatrix.setRotate(0);
            this.rotMatrix.postTranslate(0, 0);
            break;
        case 1: // SCREEN_MODE_LANDSCAPE
            this.rotMatrix.setRotate(90);
            this.rotMatrix.postTranslate(this.getMeasuredHeight(), 0);
            break;
        case 2: // SCREEN_MODE_IPORTRAIT
            this.rotMatrix.setRotate(180);
            this.rotMatrix.postTranslate(this.getMeasuredWidth(), this.getMeasuredHeight());
            break;
        case 3: // SCREEN_MODE_ILANDSCAPE
            this.rotMatrix.setRotate(270);
            this.rotMatrix.postTranslate(0, this.getMeasuredWidth());
            break;
        default:
            break;

        }

        this.invMatrix = new Matrix(this.rotMatrix);
        this.rotMatrix.invert(this.invMatrix);

        return;
    }

    @Override
    protected void dispatchDraw(Canvas arg0) {
        arg0.save();
        arg0.concat(this.invMatrix);
        // TODO Auto-generated method stub
        super.dispatchDraw(arg0);
        arg0.restore();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
        float[] event = new float[2];
        event[0] = arg0.getX();
        event[1] = arg0.getY();
        this.rotMatrix.mapPoints(event);
        arg0.setLocation(event[0], event[1]);

        return super.dispatchTouchEvent(arg0);
    }

    @Override
    public boolean dispatchTrackballEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        // event.setLocation(event.getY(), event.getX());
        if (this.current_orientation_mode == SCREEN_MODE_IPORTRAIT)
            event.setLocation(-event.getX(), -event.getY());
        else if (this.current_orientation_mode == SCREEN_MODE_LANDSCAPE)
            event.setLocation(-event.getY(), event.getX());
        else if (this.current_orientation_mode == SCREEN_MODE_ILANDSCAPE)
            event.setLocation(event.getY(), -event.getX());
        return super.dispatchTrackballEvent(event);
    }

    @Override
    public ViewParent invalidateChildInParent(int[] arg0, Rect arg1) {
        // TODO Auto-generated method stub
        arg1.offset(arg0[0], arg0[1]);
        this.newRectF.set(arg1);
        this.invMatrix.mapRect(newRectF);
        this.newRectF.roundOut(arg1);
        this.invalidate(arg1);
        return null;
        // return super.invalidateChildInParent(arg0, arg1);
    }

    public void setRotation(int newRotation) {

        //if (this.current_orientation_mode == newRotation || newRotation == -1)
        if (newRotation == -1)
            return;

        //Log.e(TAG, "setRotation newRotation = " + newRotation + ", this.current_orientation_mode = " + this.current_orientation_mode);

        mIsInverse = (this.current_orientation_mode - newRotation) % 2 == 0;

        this.current_orientation_mode = newRotation;
        this.requestLayout();
        this.invalidate();
    }

    @ExportedProperty(category = "CommonControl")
    private boolean mIsInverse = false;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsArrowStyle = false;

    protected void setRotation(int newRotation, boolean isAnchorStyle) {
        mIsArrowStyle = isAnchorStyle;
        setRotation(newRotation);
    }

    @Override
    @ExportedProperty(category = "CommonControl")
    public float getRotation() {
        //Log.e(TAG, "getRotation current_orientation_mode = " + current_orientation_mode);
        return this.current_orientation_mode;
    }


}
