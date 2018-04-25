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

package com.htc.lib1.cc.app;

import android.view.ActionMode;
import android.view.Window;

import com.htc.lib1.cc.widget.HtcAlertDialog;

/**
 * This interface supports ActionModeChanged help for app which you do not need to override google
 * public {@link Window.Callback#onActionModeStarted(ActionMode)}.
 * <p>
 * Now {@link HtcAlertDialog} has been provide
 * {@link HtcAlertDialog#setOnActionModeChangedListener(OnActionModeChangedListener)} for ap to
 * receive ActionModeChange event.
 * </p>
 * <p>
 * Sample:
 *
 * <pre class="prettyprint">
 *         HtcAlertDialog htcAlertDialog = new HtcAlertDialog.Builder(context).create();
 *         htcAlertDialog.setOnActionModeChangedListener(new com.htc.lib1.cc.app.OnActionModeChangedListener() {
 *             &#064;Override
 *             public void onActionModeStarted(ActionMode mode) {
 *                 ActionBarUtil.setActionModeBackground(context, mode, TheDrawableYouWantToSet);
 *             }
 *         });
 *     }
 * </pre>
 *
 * </p>
 */
public interface OnActionModeChangedListener {

    /**
     * Called when an action mode has been started. The appropriate mode callback method will have
     * already been invoked.
     *
     * @param mode The new mode that has just been started.
     */
    public void onActionModeStarted(ActionMode mode);

}
