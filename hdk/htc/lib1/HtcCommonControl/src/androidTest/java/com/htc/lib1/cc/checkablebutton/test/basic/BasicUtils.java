/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.htc.lib1.cc.checkablebutton.test.basic;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.htc.lib1.cc.widget.HtcCompoundButton;

public class BasicUtils {

    public static int getCompoundSuggestMinumWidth(View view, Drawable outer, Drawable content) {
        if (outer != null) {
            return outer.getIntrinsicWidth() + view.getPaddingLeft() + view.getPaddingRight();
        } else if (content != null) {
            return content.getIntrinsicWidth() + view.getPaddingLeft() + view.getPaddingRight();
        } else {
            return view.getPaddingLeft() + view.getPaddingRight();
        }
    }

    public static int getCompoundSuggestMinumHeight(View view, Drawable outer, Drawable content) {
        if (outer != null) {
            return outer.getIntrinsicHeight() + view.getPaddingTop() + view.getPaddingBottom();
        } else if (content != null) {
            return content.getIntrinsicHeight() + view.getPaddingTop() + view.getPaddingBottom();
        } else {
            return view.getPaddingTop() + view.getPaddingBottom();
        }
    }

    public static void measureHtcCompoundExpectedSize(View targetView, Point size, int outerResId, int contentResId) {
        final Context context = targetView.getContext();
        final Drawable backgroundRest = outerResId > 0 ? context.getResources().getDrawable(outerResId) : null;
        final Drawable contentPress = contentResId > 0 ? context.getResources().getDrawable(contentResId) : null;

        size.x = HtcCompoundButton.getDefaultSize(BasicUtils.getCompoundSuggestMinumWidth(targetView, backgroundRest, contentPress), size.x);
        size.y = HtcCompoundButton.getDefaultSize(BasicUtils.getCompoundSuggestMinumHeight(targetView, backgroundRest, contentPress), size.y);

        size.x = getFixedMeasureSize(size.x);
        size.y = getFixedMeasureSize(size.y);
    }

    public static int getFixedMeasureSize(int size) {
        return size & View.MEASURED_SIZE_MASK;
    }
}
