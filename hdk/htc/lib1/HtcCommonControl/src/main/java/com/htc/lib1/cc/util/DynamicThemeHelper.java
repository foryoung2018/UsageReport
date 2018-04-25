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

package com.htc.lib1.cc.util;

import android.content.Context;
import android.view.ViewDebug.ExportedProperty;

import com.htc.lib1.cc.R;

/**
 * @hide
 * @deprecated internal use only
 */
@Deprecated
public class DynamicThemeHelper {
    private Context mContext;

    public DynamicThemeHelper(Context context) {
        mContext = context;
    }

    @ExportedProperty(category = "DynamicTheme", hasAdjacentMapping = true)
    private String[] dumpDynamicThemeColor() {
        final int count = R.styleable.ThemeColor.length * 2;
        String[] themeColors = new String[count];
        for (int i = 0, j = 0; i < count; i += 2, j++) {
            themeColors[i] = mContext.getResources().getResourceEntryName(R.styleable.ThemeColor[j]);
            themeColors[i + 1] = Integer.toHexString(HtcCommonUtil.getCommonThemeColor(mContext, j));
        }
        return themeColors;
    }

}
