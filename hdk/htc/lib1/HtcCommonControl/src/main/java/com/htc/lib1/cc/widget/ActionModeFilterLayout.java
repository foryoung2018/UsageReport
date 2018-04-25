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

package com.htc.lib1.cc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.View;
import android.view.ActionMode.Callback;
import android.widget.FrameLayout;

import com.htc.lib1.cc.app.OnActionModeChangedListener;
import com.htc.lib1.cc.util.CheckUtil;

/**
 * {@link ActionModeFilterLayout} is a simple {@link FrameLayout} which just filter the
 * ActionModeChange for users.
 * <p>
 * ActionModeFilterLayout should acts as a top-level container for window content that allows users
 * to register {@link #setOnActionModeChangedListener(OnActionModeChangedListener)} for
 * ActionModeChange events.
 * </p>
 * <p>
 * To use a ActionModeFilterLayout, position your primary content view as the child of it.
 * </p>
 * <p>
 * <b>Sample :</b>
 *
 * <pre class="prettyprint">
 * ActionModeFilterLayout actionModeFilterLayout = new ActionModeFilterLayout(context);
 * actionModeFilterLayout.setOnActionModeChangedListener(new OnActionModeChangedListener() {
 *     &#064;Override
 *     public void onActionModeStarted(ActionMode mode) {
 *         ActionBarUtil.setActionModeBackground(context, mode, TheDrawableYouWantToSet);
 *     }
 * });
 *
 * // this is your original contentView
 * View contentView;
 * // position your primary content view as the child of it.
 * actionModeFilterLayout.addView(contentView);
 *
 * PopupWindow popupWindow = new PopupWindow(context);
 * // ActionModeFilterLayout should acts as a top-level container for window content
 * popupWindow.setContentView(actionModeFilterLayout);
 * ...
 * </pre>
 *
 * </p>
 */
public final class ActionModeFilterLayout extends FrameLayout {

    public ActionModeFilterLayout(Context context) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

    }

    public ActionModeFilterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

    }

    public ActionModeFilterLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

    }

    /**
     * @hide
     */
    @Override
    public ActionMode startActionModeForChild(View originalView, Callback callback) {
        ActionMode actionMode = super.startActionModeForChild(originalView, callback);
        if (actionMode != null && mActionModeChangedListener != null) {
            mActionModeChangedListener.onActionModeStarted(actionMode);
        }
        return actionMode;
    }

    private OnActionModeChangedListener mActionModeChangedListener;

    /**
     * @see OnActionModeChangedListener
     */
    public void setOnActionModeChangedListener(OnActionModeChangedListener onActionModeChangedListener) {
        mActionModeChangedListener = onActionModeChangedListener;
    }

}
