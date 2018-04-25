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

import android.os.Build;
import android.os.Trace;

/**
 * @hide
 * @deprecated internal use only
 */
@Deprecated
public final class TraceCompat {

    /**
     * Writes a trace message to indicate that a given section of code has begun. This call must be
     * followed by a corresponding call to {@link #endSection()} on the same thread.
     * <p class="note">
     * At this time the vertical bar character '|', newline character '\n', and null character '\0'
     * are used internally by the tracing mechanism. If sectionName contains these characters they
     * will be replaced with a space character in the trace.
     *
     * @param sectionName The name of the code section to appear in the trace. This may be at most
     *            127 Unicode code units long.
     */
    public static void beginSection(String sectionName) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Trace.beginSection(sectionName);
        } else {
            // TODO what?
        }
    }

    /**
     * Writes a trace message to indicate that a given section of code has ended. This call must be
     * preceeded by a corresponding call to {@link #beginSection(String)}. Calling this method will
     * mark the end of the most recently begun section of code, so care must be taken to ensure that
     * beginSection / endSection pairs are properly nested and called from the same thread.
     */
    public static void endSection() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Trace.endSection();
        } else {
            // TODO what?
        }
    }

}
