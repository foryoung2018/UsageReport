/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.htc.lib1.cc.widget.preference;



import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.lib1.cc.widget.HtcAlertDialog.Builder;
import com.htc.lib1.cc.widget.preference.HtcPreference.ReferenceViewCreater;

/**
 * A {@link Preference} that displays a list of entries as
 * a dialog.
 * <p>
 * This preference will store a string into the SharedPreferences. This string will be the value
 * from the {@link #setEntryValues(CharSequence[])} array.
 */
public class HtcListPreference extends ListPreference {
    private int mCustomLayoutResId = 0;
    private int mClickedDialogEntryIndex;
    /** The dialog, if it is showing. */
    protected Dialog mDialog;
    protected Context mDialogContext;
    /** Which button was clicked. */
    protected int mWhichButtonClicked;
    protected HtcAlertDialog.Builder mBuilder;

    /**
     * preference with list dialog box UI
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs attribute set
     */
    public HtcListPreference(Context context, AttributeSet attrs) {
        super(new ContextThemeWrapper(context, R.style.Preference), attrs);
    }

    /**
     * preference with list dialog box UI
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListPreference(Context context) {
        super(new ContextThemeWrapper(context, R.style.Preference));
    }

    /**
     * @hide
     * {@inheritDoc}
     * @see com.htc.preference.HtcPreference#onClick()
     */
    @Override
    protected void onClick() {
//        if (mDialog != null && mDialog.isShowing()) return;
//        showDialog(null);
        super.onClick();
    }
    /**
    * @hide
     *  Hide Automatically by SDK Team [U12000]
     */
    @Override
    protected View onCreateView(ViewGroup parent) {
        return HtcPreference.adjustCreateView(getContext(), parent, mCustomLayoutResId, new ReferenceViewCreater() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return HtcListPreference.super.onCreateView(parent);
            }
        });
    }

    /**
     * @hide
     * Sets the layout resource that is inflated as the {@link View} to be shown
     * for this Preference.
     * Note:If using custom layout, do not set padding/margin in your layout
     * HtcPreference would auto set these space for alignment
     *
     * @param layoutResId The layout resource ID to be inflated and returned as a View
     */
    @Override
    public void setLayoutResource(int layoutResId) {
        mCustomLayoutResId = layoutResId;
    }

    /**
     * @hide
     * Gets the layout resource that will be shown as the {@link View} for this Preference.
     *
     * @return The layout resource ID what you set from setLayoutResource().
     */
    @Override
    public int getLayoutResource() {
        return mCustomLayoutResId;
    }

    /**
     * @hide
     * Shows the dialog associated with this Preference. This is normally initiated
     * automatically on clicking on the preference. Call this method if you need to
     * show the dialog on some other event.
     * @param state Optional instance state to restore on the dialog
     */
    protected void showDialog(Bundle state) {
        //[HTC][START] TJ Tsai, 2012.01.18
        //Context context = getContext();
//        Context context = (this.mDialogContext != null) ?
//                this.mDialogContext : getContext();
//        //[HTC][END] TJ Tsai, 2012.01.18
//
//        mWhichButtonClicked = DialogInterface.BUTTON_NEGATIVE;
//
//        mBuilder = new HtcAlertDialog.Builder(context)
//        .setTitle(getDialogTitle())
//        .setIcon(getDialogIcon());
//
//        View contentView = onCreateDialogView();
//        if (contentView != null) {
//            onBindDialogView(contentView);
//            mBuilder.setView(contentView);
//        } else {
//            mBuilder.setMessage(getDialogMessage());
//        }
//
//        onPrepareDialogBuilder(mBuilder);
//
//        //            getPreferenceManager().registerOnActivityDestroyListener(this);
//
//        // Create the dialog
//        final Dialog dialog = mDialog = mBuilder.create();
//        if (state != null) {
//            dialog.onRestoreInstanceState(state);
//        }
//        if (needInputMethod()) {
//            requestInputMethod(dialog);
//        }
//        dialog.setOnDismissListener(this);
//        dialog.show();
        super.showDialog(state);
    }
    /**
     * @hide
     * @deprecated [Module internal use]
     * {@inheritDoc}
     * @see com.htc.preference.HtcDialogPreference#onPrepareDialogBuilder(com.htc.widget.HtcAlertDialog.Builder)
     */
    protected void onPrepareDialogBuilder(Builder builder) {

//        if (getEntries() == null || getEntryValues() == null) {
//            throw new IllegalStateException(
//                    "ListPreference requires an entries array and an entryValues array.");
//        }
//
//        mClickedDialogEntryIndex = getValueIndex();
//        builder.setSingleChoiceItems(getEntries(), mClickedDialogEntryIndex,
//                new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                mClickedDialogEntryIndex = which;
//
//                /*
//                 * Clicking on an item simulates the positive button
//                 * click, and dismisses the dialog.
//                 */
//                HtcListPreference.this.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
//                dialog.dismiss();
//            }
//        });
//
//        /*
//         * The typical interaction for list-based dialogs is to have
//         * click-on-an-item dismiss the dialog instead of the user having to
//         * press 'Ok'.
//         */
//        builder.setPositiveButton(null, null);
//        builder.setNegativeButton(null, null);
    }
    /**
     * @hide
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
//        CharSequence[] entryValues = getEntryValues();
//        if (positiveResult && mClickedDialogEntryIndex >= 0 && getEntries() != null) {
//            String value = entryValues[mClickedDialogEntryIndex].toString();
//            if (callChangeListener(value)) {
//                setValue(value);
//            }
//        }
    }
    /**
     * @hide
     * @deprecated [Module internal use]
     * <P>Sets the context of the dialog. <B style="color: red">This
     * context should be the one of the host activity since the dialog
     * will be mounted to the host activity.</B> If the context is not
     * the one of the host activity to which the dialog will be
     * mounted, then it will throw a BadTokenException like below.</P>
     *
     * <PRE>
     * E/AndroidRuntime(11768): FATAL EXCEPTION: main
     * E/AndroidRuntime(11768): android.view.WindowManager$BadTokenException:
     *    Unable to add window -- token null is not for an application
     * E/AndroidRuntime(11768):     at android.view.ViewRootImpl.setView(ViewRootImpl.java:568)
     * E/AndroidRuntime(11768):     at android.view.WindowManagerImpl.addView(WindowManagerImpl.java:406)
     * E/AndroidRuntime(11768):     at android.view.WindowManagerImpl.addView(WindowManagerImpl.java:320)
     * E/AndroidRuntime(11768):     at android.view.WindowManagerImpl$CompatModeWrapper.addView(WindowManagerImpl.java:152)
     * E/AndroidRuntime(11768):     at android.app.Dialog.show(Dialog.java:301)
     * E/AndroidRuntime(11768):     at com.htc.preference.HtcDialogPreference.showDialog(HtcDialogPreference.java:333)
     * </PRE>
     * @param dialogContext context which dialog will show
     */
    public void setDialogContext(Context dialogContext) {
//        if (HtcBuildFlag.Htc_DEBUG_flag) {
//            final String TAG = this.getClass().getSimpleName();
//
//            Log.v(TAG, "dialogContext: " + dialogContext);
//            if (dialogContext != null) {
//                Log.v(TAG, "dialogContext: " +
//                        dialogContext.getPackageName());
//                Log.v(TAG, "dialogContext: " +
//                        dialogContext.getPackageCodePath());
//                Log.v(TAG, "dialogContext: " +
//                        dialogContext.getPackageResourcePath());
//            }
//        }
//        this.mDialogContext = dialogContext;
    }

    /**
     * @hide
     * @deprecated [Module internal use]
     * Gets the dialog that is shown by this preference.
     *
     * @return The dialog, or null if a dialog is not being shown.
     */
    public Dialog getDialog() {
//        return mDialog;
        return super.getDialog();
    }

    /**
     * @hide
     * @deprecated [Module internal use]
     * Returns the context of the dialog.
     * @return
     */
    public Context getDialogContext() {
//        return this.mDialogContext;
        return null;
    }
    //[HTC][END] TJ Tsai, 2012.01.18

    /**
     * Sets the required flags on the dialog window to enable input method window to show up.
     */
    private void requestInputMethod(Dialog dialog) {
//        Window window = dialog.getWindow();
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private int getValueIndex() {
        return findIndexOfValue(getValue());
    }
    /**
     * @hide
     * Returns the index of the given value (in the entry values array).
     *
     * @param value The value whose index should be returned.
     * @return The index of the value, or -1 if not found.
     */
    @Override
    public int findIndexOfValue(String value) {
//        CharSequence[] entryValues = getEntryValues();
//        if (value != null && entryValues != null) {
//            for (int i = entryValues.length - 1; i >= 0; i--) {
//                if (entryValues[i].equals(value)) {
//                    return i;
//                }
//            }
//        }
//        return -1;
        return super.findIndexOfValue(value);
    }
    /**
    * @hide
     * Sets the value of the key. This should be one of the entries in
     * {@link #getEntryValues()}.
     *
     * @param value The value to set for the key.
     */
    @Override
    public void setValue(String value) {
        super.setValue(value);
    }
    /**
    * @hide
     * Returns the value of the key. This should be one of the entries in
     * {@link #getEntryValues()}.
     *
     * @return The value of the key.
     */
    public String getValue() {
        return super.getValue();
    }
    /**
     * Returns whether the preference needs to display a soft input method when the dialog
     * is displayed. Default is false. Subclasses should override this method if they need
     * the soft input method brought up automatically.
     * @hide
     */
    protected boolean needInputMethod() {
        return false;
    }
}
