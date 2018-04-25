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

package com.htc.lib1.cc.widget;

import com.htc.lib1.cc.app.HtcAlertController;
import com.htc.lib1.cc.app.OnActionModeChangedListener;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Arrays;

import android.app.Dialog;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.htc.lib1.cc.util.res.HtcResUtil;
import com.htc.lib1.cc.widget.HtcCompoundButton.OnCheckedChangeListener;

/**
 * A subclass of Dialog that can display one, two or three buttons. If you only want to
 * display a String in this dialog box, use the setMessage() method.  If you
 * want to display a more complex view, look up the FrameLayout called "custom"
 * and add your view to it:
 *
 * <pre>
 * FrameLayout fl = (FrameLayout) findViewById(android.R.id.custom);
 * fl.addView(myView, new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
 * </pre>
 *
 * <p>The AlertDialog class takes care of automatically setting
 * {@link WindowManager.LayoutParams#FLAG_ALT_FOCUSABLE_IM
 * WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM} for you based on whether
 * any views in the dialog return true from {@link View#onCheckIsTextEditor()
 * View.onCheckIsTextEditor()}.  Generally you want this set for a Dialog
 * without text editors, so that it will be placed on top of the current
 * input method UI.  You can modify this behavior by forcing the flag to your
 * desired mode after calling {@link #onCreate}.
 *
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 * <p>For more information about creating dialogs, read the
 * <a href="{@docRoot}guide/topics/ui/dialogs.html">Dialogs</a> developer guide.</p>
 * </div>
 */
public class HtcAlertDialog extends Dialog implements DialogInterface {
    /**
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @deprecated [Module internal use] will change to private later
     */
    @Deprecated
    protected HtcAlertController mAlert;

    private static final String TAG = "HtcAlertDialog";
    @SuppressWarnings("deprecation")
    private static final boolean DEBUG = com.htc.lib1.cc.htcjavaflag.HtcBuildFlag.Htc_DEBUG_flag;
    private static final boolean ISMARSHMALLOW = Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1;

    /**
     * the constructor
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    @SuppressWarnings("deprecation")
    protected HtcAlertDialog(Context context) {
        super(context);

        // remove shadow background according to Lee's guide and compliant to a flat design
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //getWindow().alwaysReadCloseOnTouchAttr();
        mAlert = new HtcAlertController(getContext(), this, getWindow());

        // Sense5 new UI design, also set in HtcAlertController.AlertParams
        mAlert.setTitleCenterEnabled(false);
    }

    /**
     * Construct an AlertDialog that uses an explicit theme. The actual style that an AlertDialog
     * uses is a private implementation, however you can here supply either the name of an attribute
     * in the theme from which to get the dialog's style (such as
     * {@link android.R.attr#alertDialogTheme} or one of the constants {@link #THEME_TRADITIONAL},
     * {@link #THEME_HOLO_DARK}, or {@link #THEME_HOLO_LIGHT}.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param theme see android.app.AlertDialog
     */
    protected HtcAlertDialog(Context context, int theme) {
        super(context, theme);

        // remove shadow background according to Lee's guide and compliant to a flat design
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //getWindow().alwaysReadCloseOnTouchAttr();
        mAlert = new HtcAlertController(getContext(), this, getWindow());

        // Sense5 new UI design, also set in HtcAlertController.AlertParams
        mAlert.setTitleCenterEnabled(false);
    }

    /**
     * the constructor
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param cancelable see android.app.AlertDialog
     * @param cancelListener see android.app.AlertDialog
     */
    protected HtcAlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        this(context);
        setCancelable(cancelable);
        setOnCancelListener(cancelListener);
    }

    //+Arc, for hyper link feature in sense 2.0
    /**
     * set text message with link handling
     * @param message text message
     * @param nAutoLinkMask how link is handled
     * @see android.widget.TextView
     * @deprecated [Not use any longer]
     */
    @Deprecated
    public void setMessage(CharSequence message, int nAutoLinkMask) {
        mAlert.setMessage(message, nAutoLinkMask);
    }
    //-Arc

    /**
     * Set the enabled state of the check item.
     * @param enabled True if this view is enabled, false otherwise.
     * @deprecated [Not use any longer]
     */
    @Deprecated
    public void setCheckBoxEnabled(boolean enabled) {
        mAlert.setCheckBoxEnabled(enabled);
    }

    /**
     * Changes the checked state of the check item.
     * @param checked The new checked state
     * @deprecated [Not use any longer]
     */
    @Deprecated
    public void setCheckBoxChecked(boolean checked) {
        mAlert.setCheckBoxChecked(checked);
    }

    /**
     * Get the checked state of the check item.
     * @return The current checked state of the view
     */
    public boolean isCheckBoxChecked() {
        return mAlert.isCheckBoxChecked();
    }

    /**
     * @hide
     */
    public static int getDefaultListItemHeight(Context context, boolean automotive) {
        int mode = automotive ? HtcListItem.MODE_AUTOMOTIVE : HtcListItem.MODE_DEFAULT;
        HtcListItemUtil.setContextForMargins(context, mode);
        return HtcListItemUtil.getDesiredListItemHeight(mode);
    }

    // For App control padding in button panel on their own [
    /**
    * This is to get the padding view between each button.
    * This function must be call after show() and remember to check null.
    * @return return padding1
    */
    public View getPadding1() {
        if(mAlert != null)
            return mAlert.getPadding1();
        else
            return null;
    }

    /**
    * This is to get the padding view between each button.
    * This function must be call after show() and remember to check null.
    * @return return padding3
    */
    public View getPadding3() {
        if(mAlert != null)
            return mAlert.getPadding3();
        else
            return null;
    }
    // For App control padding in button panel on their own ]

    /**
     * Gets one of the buttons used in the dialog.
     * <p>
     * If a button does not exist in the dialog, null will be returned.
     * @param whichButton The identifier of the button that should be returned. For example, this
     *            can be {@link DialogInterface#BUTTON_POSITIVE}.
     * @return The button from the dialog, or null if a button does not exist.
     */
    public Button getButton(int whichButton) {
        return mAlert.getButton(whichButton);
    }

    /**
     * Gets the list view used in the dialog.
     * @return The {@link ListView} from the dialog.
     */
    public ListView getListView() {
        return mAlert.getListView();
    }

    /** {@inheritDoc}
     */
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mAlert.setTitle(title);
    }

    /**
     * set custom title
     * @param customTitleView see android.app.AlertDialog
     * @see Builder#setCustomTitle(View)
     * @deprecated [Not use any longer]
     */
    public void setCustomTitle(View customTitleView) {
        mAlert.setCustomTitle(customTitleView);
    }

    /**
     * set text message
     * @param message see android.app.AlertDialog
     */
    public void setMessage(CharSequence message) {
        mAlert.setMessage(message);
    }

    /**
     * Set the view to display in that dialog.
     * @param view the custom view put in content area
     */
    public void setView(View view) {
        mAlert.setView(view);
    }

    /**
     * Set the view to display in that dialog, specifying the spacing to appear around that
     * view.
     * @param view The view to show in the content area of the dialog
     * @param viewSpacingLeft Extra space to appear to the left of {@code view}
     * @param viewSpacingTop Extra space to appear above {@code view}
     * @param viewSpacingRight Extra space to appear to the right of {@code view}
     * @param viewSpacingBottom Extra space to appear below {@code view}
     */
    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight,
            int viewSpacingBottom) {
        mAlert.setView(view, viewSpacingLeft, viewSpacingTop, viewSpacingRight, viewSpacingBottom);
    }

    /**
     * Set a message to be sent when a button is pressed.
     * @param whichButton Which button to set the message for, can be one of
     *            {@link DialogInterface#BUTTON_POSITIVE},
     *            {@link DialogInterface#BUTTON_NEGATIVE}, or
     *            {@link DialogInterface#BUTTON_NEUTRAL}
     * @param text The text to display in positive button.
     * @param msg The {@link Message} to be sent when clicked.
     * @deprecated [Not use any longer]
     * @hide
     */
    public void setButton(int whichButton, CharSequence text, Message msg) {
        mAlert.setButton(whichButton, text, null, msg);
    }

    /**
     * Set a listener to be invoked when the positive button of the dialog is pressed.
     * @param whichButton Which button to set the listener on, can be one of
     *            {@link DialogInterface#BUTTON_POSITIVE},
     *            {@link DialogInterface#BUTTON_NEGATIVE}, or
     *            {@link DialogInterface#BUTTON_NEUTRAL}
     * @param text The text to display in positive button.
     * @param listener The {@link DialogInterface.OnClickListener} to use.
     */
    public void setButton(int whichButton, CharSequence text, OnClickListener listener) {
        mAlert.setButton(whichButton, text, listener, null);
    }

    /**
     * set 1st button text and message, deprecated, do not use
     * @param text the text
     * @param msg the message
     * @deprecated Use {@link #setButton(int, CharSequence, Message)} with
     *             {@link DialogInterface#BUTTON_POSITIVE}.
     * @hide
     */
    @Deprecated
    public void setButton(CharSequence text, Message msg) {
        setButton(BUTTON_POSITIVE, text, msg);
    }

    /**
     * set 2nd button text and message, deprecated, do not use
     * @param text the text
     * @param msg the message
     * @deprecated Use {@link #setButton(int, CharSequence, Message)} with
     *             {@link DialogInterface#BUTTON_NEGATIVE}.
     * @hide
     */
    @Deprecated
    public void setButton2(CharSequence text, Message msg) {
        setButton(BUTTON_NEGATIVE, text, msg);
    }

    /**
     * set 3rd button text and message, deprecated, do not use
     * @param text the text
     * @param msg the message
     * @deprecated Use {@link #setButton(int, CharSequence, Message)} with
     *             {@link DialogInterface#BUTTON_NEUTRAL}.
     * @hide
     */
    @Deprecated
    public void setButton3(CharSequence text, Message msg) {
        setButton(BUTTON_NEUTRAL, text, msg);
    }

    /**
     * Set a listener to be invoked when button 1 of the dialog is pressed.
     * @param text The text to display in button 1.
     * @param listener The {@link DialogInterface.OnClickListener} to use.
     * @deprecated Use
     *             {@link #setButton(int, CharSequence, android.content.DialogInterface.OnClickListener)}
     *             with {@link DialogInterface#BUTTON_POSITIVE}
     */
    @Deprecated
    public void setButton(CharSequence text, final OnClickListener listener) {
        setButton(BUTTON_POSITIVE, text, listener);
    }

    /**
     * Set a listener to be invoked when button 2 of the dialog is pressed.
     * @param text The text to display in button 2.
     * @param listener The {@link DialogInterface.OnClickListener} to use.
     * @deprecated Use
     *             {@link #setButton(int, CharSequence, android.content.DialogInterface.OnClickListener)}
     *             with {@link DialogInterface#BUTTON_NEGATIVE}
     * @hide
     */
    @Deprecated
    public void setButton2(CharSequence text, final OnClickListener listener) {
        setButton(BUTTON_NEGATIVE, text, listener);
    }

    /**
     * Set a listener to be invoked when button 3 of the dialog is pressed.
     * @param text The text to display in button 3.
     * @param listener The {@link DialogInterface.OnClickListener} to use.
     * @deprecated Use
     *             {@link #setButton(int, CharSequence, android.content.DialogInterface.OnClickListener)}
     *             with {@link DialogInterface#BUTTON_POSITIVE}
     * @hide
     */
    @Deprecated
    public void setButton3(CharSequence text, final OnClickListener listener) {
        setButton(BUTTON_NEUTRAL, text, listener);
    }

    /**
     * Set resId to 0 if you don't want an icon.
     * @param resId the resourceId of the drawable to use as the icon or 0
     * if you don't want an icon.
     * @deprecated [Not use any longer]
     * @hide
     */
    @Deprecated
    public void setIcon(int resId) {
        mAlert.setIcon(resId);
    }

    /**
     * set icon in title bar
     * @param icon see android.app.AlertDialog
     * @deprecated [Not use any longer]
     * @hide
     */
    @Deprecated
    public void setIcon(Drawable icon) {
        mAlert.setIcon(icon);
    }

    /**
     * Set an icon as supplied by a theme attribute. e.g. android.R.attr.alertDialogIcon
     * @param attrId ID of a theme attribute that points to a drawable resource.
     * @deprecated [Not use any longer]
     * @hide
     */
    @Deprecated
    public void setIconAttribute(int attrId) {
        TypedValue out = new TypedValue();
        getContext().getTheme().resolveAttribute(attrId, out, true);
        mAlert.setIcon(out.resourceId);
    }

    /**
     * set content background to light or dark
     * @param forceInverseBackground see android.app.AlertDialog
     * @deprecated [Module internal use]
     */
    @Deprecated
    public void setInverseBackgroundForced(boolean forceInverseBackground) {
        mAlert.setInverseBackgroundForced(forceInverseBackground);
    }

    /** {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlert.installContent();

        if (ISMARSHMALLOW) return;
        // Add by HZTSENG : auto launch SIP
        Window theWindow = getWindow();
        WindowManager.LayoutParams lp = theWindow.getAttributes();
        lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
        theWindow.setAttributes(lp);
        if (DEBUG) Log.i(TAG, "[onCreate] auto launch SIP.");
        // End of add by HZTSENG
    }

    /**
     * {@inheritDoc}
     * @deprecated [Module internal use] should marked (at) hide
     * @hide
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAlert.onKeyDown(keyCode, event)) return true;
        return super.onKeyDown(keyCode, event);
    }

    /**
     * {@inheritDoc}
     * @deprecated [Module internal use] should marked (at) hide
     * @hide
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mAlert.onKeyUp(keyCode, event)) return true;
        return super.onKeyUp(keyCode, event);
    }

    /**
     * Builder, standard way to create/show an alert dialog
     * @see android.app.AlertDialog.Builder
     */
    public static class Builder {
        /**
         * used to save alert dialog's parameters
         * @see android.app.AlertDialog.Builder.P
         */
        protected final HtcAlertController.AlertParams P;

        /**
         * used for font size selector to show only: small, medium, large, extra large
         */
        public static final int FONTSIZESELECTOR_NO_HUGE = 4;

        /**
         * used for font size selector to show: small, medium, large, extra large and huge
         */
        public static final int FONTSIZESELECTOR_SHOW_HUGE = 2;

        /**
         * Constructor using a context for this builder and the {@link AlertDialog} it creates.
         * @param context The Context the view is running in, through which it can
         *            access the current theme, resources, etc. and MUST be blong to the subclass of
         *            ContextThemeWrapper.
         */
        public Builder(Context context) {
            P = new HtcAlertController.AlertParams(context);
        }

        /**
         * Returns a {@link Context} with the appropriate theme for dialogs created by this Builder.
         * Applications should use this Context for obtaining LayoutInflaters for inflating views
         * that will be used in the resulting dialogs, as it will cause views to be inflated with
         * the correct theme.
         * @return A Context for built Dialogs.
         */
        public Context getContext() {
            return P.mContext;
        }

        /**
         * Set the title using the given resource id.
         * @param titleId see android.app.AlertDialog.Builder
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(int titleId) {
            P.mTitle = P.mContext.getText(titleId);
            return this;
        }

        /**
         * Set the title displayed in the {@link Dialog}.
         * @param title the title
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(CharSequence title) {
            P.mTitle = title;
            return this;
        }

        /**
         * Set the title using the custom view {@code customTitleView}. The
         * methods {@link #setTitle(int)} and {@link #setIcon(int)} should be
         * sufficient for most titles, but this is provided if the title needs
         * more customization. Using this will replace the title and icon set
         * via the other methods.
         * @param customTitleView The custom view to use as the title.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCustomTitle(View customTitleView) {
            P.mCustomTitleView = customTitleView;
            return this;
        }

        /**
         * Set the message to display using the given resource id.
         * @param messageId see android.app.AlertDialog.Builder
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(int messageId) {
            P.mMessage = P.mContext.getText(messageId);
            return this;
        }

        /**
         * Set the message to display.
         * @param message see android.app.AlertDialog.Builder
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(CharSequence message) {
            P.mMessage = message;
            return this;
        }

        /**
         * Set the resource id of the {@link Drawable} to be used in the title.
         * @param iconId see android.app.AlertDialog.Builder
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setIcon(int iconId) {
            P.mIconId = iconId;
            return this;
        }

        /**
         * Set the {@link Drawable} to be used in the title.
         * @param icon a drawable
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setIcon(Drawable icon) {
            P.mIcon = icon;
            return this;
        }

        /**
         * Set an icon as supplied by a theme attribute. e.g. android.R.attr.alertDialogIcon
         *
         * @param attrId ID of a theme attribute that points to a drawable resource.
         * @return see android.app.AlertDialog.Builder
         */
        public Builder setIconAttribute(int attrId) {
            TypedValue out = new TypedValue();
            P.mContext.getTheme().resolveAttribute(attrId, out, true);
            P.mIconId = out.resourceId;
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         * @param textId The resource id of the text to display in the positive button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(int textId, final OnClickListener listener) {
            P.mPositiveButtonText = P.mContext.getText(textId);
            P.mPositiveButtonListener = listener;
            return this;
        }

        /**
         * Setup a check item above the button panel. (Sense 5.5 new feature)
         * Mostly used as a default preference for the future, so the application
         * doesn't have to ask the user again and again.
         * Caution:
         * 1. the check item will be placed just above the button panel.
         *    (under the custom panel)
         * 2  the check item will not be scrollable as the text message and/or the list view.
         * @param checkBoxLabel the text of the check item
         * @param checkBoxDefault the default checked state
         * @param checkBoxListener the checked state change listener
         * @param checkBoxEnabled the enabled state of the check item
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCheckBox(CharSequence checkBoxLabel, boolean checkBoxDefault, OnCheckedChangeListener checkBoxListener, boolean checkBoxEnabled) {
            P.mCheckBoxLabel = checkBoxLabel;
            P.mCheckBoxDefault = checkBoxDefault;
            P.mCheckBoxListener = checkBoxListener;
            P.mCheckBoxEnabled = checkBoxEnabled;
            return this;
        }

        //+Arc, for hyper link feature
        /**
         * set text message with link feature
         * @param message the text message
         * @param nAutoLinkMask how to handle the link. see TextView for further information
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(CharSequence message, int nAutoLinkMask) {
            P.mMessage = message;
            P.mAutoLinkMask = nAutoLinkMask;
            return this;
        }
        //-Arc

        //s: Added by Tiffanie
        // 2009.09.29 wj javadoc
        /**
         * Disables the Dialog positive button
         * @param disabled Whether to disabled positive button
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButtonDisabled(boolean disabled) {
            P.mPositiveButtonDisabled = true;
            return this;
        }
        // 2009.09.29 wj javadoc
        /**
         * Disables the Dialog neutral button
         *
         * @param disabled Whether to disabled neutral button
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * @deprecated [Not use any longer]
         */
        @Deprecated
        public Builder setNeutralButtonDisabled(boolean disabled) {
            P.mNeutralButtonDisabled = true;
            return this;
        }
        // 2009.09.29 wj javadoc
        /**
         * Disables the Dialog negative button
         *
         * @param disabled Whether to disabled negative button
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButtonDisabled(boolean disabled) {
            P.mNegativeButtonDisabled = true;
            return this;
        }
        //e

        //+Kun
        // 2009.09.29 wj javadoc
        /**
         * Sets the Dialog title divider visibility
         *
         * @param visible The visiblity of title divider
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * @deprecated [Not use any longer] there is no divider in HTC design
         * @hide
         */
        public Builder setTitleDividerVisible(boolean visible){
            return this;
        }
        // 2009.09.29 wj javadoc
        /**
         * Sets the Dialog to enable title center
         *
         * @param enable Whether to enable title center
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitleCenterEnabled(boolean enable){
            P.mTitleCenter = enable;
            return this;
        }
        //-Kun

        /**
        * This api is to enable AutoMotive mode
        *
        * @param b Whether to enable automotive mode
        *
        * @return This Builder object to allow for chaining of calls to set methods
        */
        public Builder setIsAutoMotive(boolean b) {
            if (DEBUG) Log.d(TAG, "call setIsAutoMotive: b=" + b);
            P.mIsAutoMotive = b;
            return this;
        }

        /**
         * Use this API to setup a pre-defined font size selector dialog.
         *
         * Cautions:
         * 1. Please don't forget to set title and onCancelListener, ..., etc.
         * 2. The "which in fontSizeListener" and "preselect" refer to
         *    the fontsize in configuration. See there for details.
         *    (which means they are not indexes)
         * 3. Do NOT support automotive mode
         * 4. the font sizes refer to "list_primary_m"
         *
         * @param preselect pre-selected item, refer to Configuration.FONTSIZE_XXX
         * @param listener onClickListener which will be called on item click
         * @param variation use 0 or refer to FONTSIZESELECTOR_XXX
         * @return This Builder object to allow for chaining of calls to set methods
         *
         */
        public Builder setFontSizeSelector(int preselect, final OnClickListener listener, final int variation) {
            //final String REFERENCE_PACKAGE = "com.htc";
            final int REFERENCE_FONT = com.htc.lib1.cc.R.dimen.list_primary_m;

            // use com.htc's context to get correct dimensions
            Context context = null;
            //try {
            //    // TODO henry: check package name before create
            //    context = P.mContext.createPackageContext(REFERENCE_PACKAGE, 0);
            //} catch (NameNotFoundException e) {
            //    // shall never reach here
            //    Log.e(TAG, "setFontSizeSelector: package " + REFERENCE_PACKAGE + " not found!");
                context = P.mContext;
            //}

            // prepare data
            int[] sizes = HtcResUtil.getDimensionsInDifferentFontSizeConfig(context, REFERENCE_FONT);
            String[] levels = P.mContext.getResources().getStringArray(com.htc.lib1.cc.R.array.st_font_size_levels);
            switch (variation) {
            case FONTSIZESELECTOR_SHOW_HUGE:
                // remove smallest and undefined
                // do nothing, due to smallest and undefined are already removed.
                //levels = Arrays.copyOfRange(levels, Configuration.FONTSIZE_SMALL, Configuration.FONTSIZE_HUGE + 1);
                //sizes = Arrays.copyOfRange(sizes, Configuration.FONTSIZE_SMALL, Configuration.FONTSIZE_HUGE + 1);
                break;
            case FONTSIZESELECTOR_NO_HUGE:
            default:
                // remove smallest and huge and undefined
                //levels = Arrays.copyOfRange(levels, Configuration.FONTSIZE_SMALL, Configuration.FONTSIZE_LARGEST + 1);
                //sizes = Arrays.copyOfRange(sizes, Configuration.FONTSIZE_SMALL, Configuration.FONTSIZE_LARGEST + 1);
                levels = Arrays.copyOf(levels, levels.length - 1);
                sizes = Arrays.copyOf(sizes, sizes.length - 1);
                break;
            }

            final int layout = /* P.mIsAutoMotive ? R.layout.dialog_listitem_radio_automotive : */ com.htc.lib1.cc.R.layout.dialog_listitem_radio;
            final LayoutInflater inflater = (LayoutInflater) P.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final int[] fontSizes = sizes;

            ListAdapter adapter = new ArrayAdapter<CharSequence>(P.mContext, 0, 0, levels) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (null == convertView) {
                        convertView = inflater.inflate(layout, parent, false);
                    }
                    HtcListItemSingleText text = (HtcListItemSingleText) convertView.findViewById(android.R.id.text1);
                    if (null != text) {
                        text.setText(this.getItem(position));
                        View child = text.getChildAt(0);
                        if (child instanceof TextView) {
                            ((TextView) child).setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSizes[position]);
                        }
                    }
                    return convertView;
                }
            };
            OnClickListener listenerWrapper = new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (null != listener) {
                            // TODO henry: be careful, the "which" should relate to Configuration.FONTSIZE_XXX
                            listener.onClick(dialog, which/* + Configuration.FONTSIZE_SMALL*/);
                        }
                        dialog.dismiss();
                    }
                };
            int checkedItem = preselect/* - Configuration.FONTSIZE_SMALL*/; // TODO henry: be careful, this depends on "variation"

            setSingleChoiceItems(adapter, checkedItem, listenerWrapper);
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         * @param text The text to display in the positive button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(CharSequence text, final OnClickListener listener) {
            P.mPositiveButtonText = text;
            P.mPositiveButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         * @param textId The resource id of the text to display in the negative button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(int textId, final OnClickListener listener) {
            P.mNegativeButtonText = P.mContext.getText(textId);
            P.mNegativeButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         * @param text The text to display in the negative button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(CharSequence text, final OnClickListener listener) {
            P.mNegativeButtonText = text;
            P.mNegativeButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the neutral button of the dialog is pressed.
         * @param textId The resource id of the text to display in the neutral button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNeutralButton(int textId, final OnClickListener listener) {
            P.mNeutralButtonText = P.mContext.getText(textId);
            P.mNeutralButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the neutral button of the dialog is pressed.
         * @param text The text to display in the neutral button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNeutralButton(CharSequence text, final OnClickListener listener) {
            P.mNeutralButtonText = text;
            P.mNeutralButtonListener = listener;
            return this;
        }

        /**
         * Sets whether the dialog is cancelable or not.  Default is true.
         *
         * @param cancelable see android.app.AlertDialog.Builder
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCancelable(boolean cancelable) {
            P.mCancelable = cancelable;
            return this;
        }

        /**
         * Sets the callback that will be called if the dialog is canceled.
         *
         * <p>Even in a cancelable dialog, the dialog may be dismissed for reasons other than
         * being canceled or one of the supplied choices being selected.
         * If you are interested in listening for all cases where the dialog is dismissed
         * and not just when it is canceled, see
         * {@link #setOnDismissListener(android.content.DialogInterface.OnDismissListener) setOnDismissListener}.</p>
         * @see #setCancelable(boolean)
         * @see #setOnDismissListener(android.content.DialogInterface.OnDismissListener)
         *
         * @param onCancelListener see android.app.AlertDialog.Builder
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            P.mOnCancelListener = onCancelListener;
            return this;
        }

        /**
         * Sets the callback that will be called when the dialog is dismissed for any reason.
         *
         * @param onDismissListener see android.app.AlertDialog.Builder
         * @return This Builder object to allow for chaining of calls to set methods
         * @hide
         */
        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            P.mOnDismissListener = onDismissListener;
            return this;
        }

        /**
         * Sets the callback that will be called if a key is dispatched to the dialog.
         *
         * @param onKeyListener see android.app.AlertDialog.Builder
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnKeyListener(OnKeyListener onKeyListener) {
            P.mOnKeyListener = onKeyListener;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you will be notified of
         * the selected item via the supplied listener. This should be an array type i.e.
         * R.array.foo
         * @param itemsId see android.app.AlertDialog.Builder
         * @param listener see android.app.AlertDialog.Builder
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setItems(int itemsId, final OnClickListener listener) {
            P.mItems = P.mContext.getResources().getTextArray(itemsId);
            P.mOnClickListener = listener;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you will be notified of the
         * selected item via the supplied listener.
         *
         * @param items the list items to show
         * @param listener the call-back on item click
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setItems(CharSequence[] items, final OnClickListener listener) {
            P.mItems = items;
            P.mOnClickListener = listener;
            return this;
        }

        /**
         * Set a list of items, which are supplied by the given {@link ListAdapter}, to be
         * displayed in the dialog as the content, you will be notified of the
         * selected item via the supplied listener.
         *
         * @param adapter The {@link ListAdapter} to supply the list of items
         * @param listener The listener that will be called when an item is clicked.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setAdapter(final ListAdapter adapter, final OnClickListener listener) {
            P.mAdapter = adapter;
            P.mOnClickListener = listener;
            return this;
        }

        /**
         * Set a list of items, which are supplied by the given {@link Cursor}, to be
         * displayed in the dialog as the content, you will be notified of the
         * selected item via the supplied listener.
         *
         * @param cursor The {@link Cursor} to supply the list of items
         * @param listener The listener that will be called when an item is clicked.
         * @param labelColumn The column name on the cursor containing the string to display
         *          in the label.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * @deprecated [Not use any longer]
         * @hide
         */
        public Builder setCursor(final Cursor cursor, final OnClickListener listener,
                String labelColumn) {
            P.mCursor = cursor;
            P.mLabelColumn = labelColumn;
            P.mOnClickListener = listener;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content,
         * you will be notified of the selected item via the supplied listener.
         * This should be an array type, e.g. R.array.foo. The list will have
         * a check mark displayed to the right of the text for each checked
         * item. Clicking on an item in the list will not dismiss the dialog.
         * Clicking on a button will dismiss the dialog.
         *
         * @param itemsId the resource id of an array i.e. R.array.foo
         * @param checkedItems specifies which items are checked. It should be null in which case no
         *        items are checked. If non null it must be exactly the same length as the array of
         *        items.
         * @param listener notified when an item on the list is clicked. The dialog will not be
         *        dismissed when an item is clicked. It will only be dismissed if clicked on a
         *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMultiChoiceItems(int itemsId, boolean[] checkedItems,
                final OnMultiChoiceClickListener listener) {
            P.mItems = P.mContext.getResources().getTextArray(itemsId);
            P.mOnCheckboxClickListener = listener;
            P.mCheckedItems = checkedItems;
            P.mIsMultiChoice = true;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content,
         * you will be notified of the selected item via the supplied listener.
         * The list will have a check mark displayed to the right of the text
         * for each checked item. Clicking on an item in the list will not
         * dismiss the dialog. Clicking on a button will dismiss the dialog.
         *
         * @param items the text of the items to be displayed in the list.
         * @param checkedItems specifies which items are checked. It should be null in which case no
         *        items are checked. If non null it must be exactly the same length as the array of
         *        items.
         * @param listener notified when an item on the list is clicked. The dialog will not be
         *        dismissed when an item is clicked. It will only be dismissed if clicked on a
         *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems,
                final OnMultiChoiceClickListener listener) {
            P.mItems = items;
            P.mOnCheckboxClickListener = listener;
            P.mCheckedItems = checkedItems;
            P.mIsMultiChoice = true;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content,
         * you will be notified of the selected item via the supplied listener.
         * The list will have a check mark displayed to the right of the text
         * for each checked item. Clicking on an item in the list will not
         * dismiss the dialog. Clicking on a button will dismiss the dialog.
         *
         * @param cursor the cursor used to provide the items.
         * @param isCheckedColumn specifies the column name on the cursor to use to determine
         *        whether a checkbox is checked or not. It must return an integer value where 1
         *        means checked and 0 means unchecked.
         * @param labelColumn The column name on the cursor containing the string to display in the
         *        label.
         * @param listener notified when an item on the list is clicked. The dialog will not be
         *        dismissed when an item is clicked. It will only be dismissed if clicked on a
         *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * @deprecated [Not use any longer]
         * @hide
         */
        public Builder setMultiChoiceItems(Cursor cursor, String isCheckedColumn, String labelColumn,
                final OnMultiChoiceClickListener listener) {
            P.mCursor = cursor;
            P.mOnCheckboxClickListener = listener;
            P.mIsCheckedColumn = isCheckedColumn;
            P.mLabelColumn = labelColumn;
            P.mIsMultiChoice = true;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you will be notified of
         * the selected item via the supplied listener. This should be an array type i.e.
         * R.array.foo The list will have a check mark displayed to the right of the text for the
         * checked item. Clicking on an item in the list will not dismiss the dialog. Clicking on a
         * button will dismiss the dialog.
         *
         * @param itemsId the resource id of an array i.e. R.array.foo
         * @param checkedItem specifies which item is checked. If -1 no items are checked.
         * @param listener notified when an item on the list is clicked. The dialog will not be
         *        dismissed when an item is clicked. It will only be dismissed if clicked on a
         *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSingleChoiceItems(int itemsId, int checkedItem,
                final OnClickListener listener) {
            P.mItems = P.mContext.getResources().getTextArray(itemsId);
            P.mOnClickListener = listener;
            P.mCheckedItem = checkedItem;
            P.mIsSingleChoice = true;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you will be notified of
         * the selected item via the supplied listener. The list will have a check mark displayed to
         * the right of the text for the checked item. Clicking on an item in the list will not
         * dismiss the dialog. Clicking on a button will dismiss the dialog.
         *
         * @param cursor the cursor to retrieve the items from.
         * @param checkedItem specifies which item is checked. If -1 no items are checked.
         * @param labelColumn The column name on the cursor containing the string to display in the
         *        label.
         * @param listener notified when an item on the list is clicked. The dialog will not be
         *        dismissed when an item is clicked. It will only be dismissed if clicked on a
         *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * @deprecated [Not use any longer]
         * @hide
         */
        public Builder setSingleChoiceItems(Cursor cursor, int checkedItem, String labelColumn,
                final OnClickListener listener) {
            P.mCursor = cursor;
            P.mOnClickListener = listener;
            P.mCheckedItem = checkedItem;
            P.mLabelColumn = labelColumn;
            P.mIsSingleChoice = true;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you will be notified of
         * the selected item via the supplied listener. The list will have a check mark displayed to
         * the right of the text for the checked item. Clicking on an item in the list will not
         * dismiss the dialog. Clicking on a button will dismiss the dialog.
         *
         * @param items the items to be displayed.
         * @param checkedItem specifies which item is checked. If -1 no items are checked.
         * @param listener notified when an item on the list is clicked. The dialog will not be
         *        dismissed when an item is clicked. It will only be dismissed if clicked on a
         *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSingleChoiceItems(CharSequence[] items, int checkedItem, final OnClickListener listener) {
            P.mItems = items;
            P.mOnClickListener = listener;
            P.mCheckedItem = checkedItem;
            P.mIsSingleChoice = true;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you will be notified of
         * the selected item via the supplied listener. The list will have a check mark displayed to
         * the right of the text for the checked item. Clicking on an item in the list will not
         * dismiss the dialog. Clicking on a button will dismiss the dialog.
         *
         * @param adapter The {@link ListAdapter} to supply the list of items
         * @param checkedItem specifies which item is checked. If -1 no items are checked.
         * @param listener notified when an item on the list is clicked. The dialog will not be
         *        dismissed when an item is clicked. It will only be dismissed if clicked on a
         *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSingleChoiceItems(ListAdapter adapter, int checkedItem, final OnClickListener listener) {
            P.mAdapter = adapter;
            P.mOnClickListener = listener;
            P.mCheckedItem = checkedItem;
            P.mIsSingleChoice = true;
            return this;
        }

        /**
         * Sets a listener to be invoked when an item in the list is selected.
         *
         * @param listener The listener to be invoked.
         * @see AdapterView#setOnItemSelectedListener(android.widget.AdapterView.OnItemSelectedListener)
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * @deprecated [Not use any longer]
         */
        public Builder setOnItemSelectedListener(final AdapterView.OnItemSelectedListener listener) {
            P.mOnItemSelectedListener = listener;
            return this;
        }

        /**
         * Set a custom view to be the contents of the Dialog. If the supplied view is an instance
         * of a {@link ListView} the light background will be used.
         *
         * @param view The view to use as the contents of the Dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setView(View view) {
            P.mView = view;
            P.mViewSpacingSpecified = false;
            return this;
        }

        /**
         * Set a custom view to be the contents of the Dialog, specifying the
         * spacing to appear around that view. If the supplied view is an
         * instance of a {@link ListView} the light background will be used.
         *
         * @param view The view to use as the contents of the Dialog.
         * @param viewSpacingLeft Spacing between the left edge of the view and
         *        the dialog frame
         * @param viewSpacingTop Spacing between the top edge of the view and
         *        the dialog frame
         * @param viewSpacingRight Spacing between the right edge of the view
         *        and the dialog frame
         * @param viewSpacingBottom Spacing between the bottom edge of the view
         *        and the dialog frame
         * @return This Builder object to allow for chaining of calls to set
         *         methods
         *
         *
         * This is currently hidden because it seems like people should just
         * be able to put padding around the view.
         * @hide
         */
        public Builder setView(View view, int viewSpacingLeft, int viewSpacingTop,
                int viewSpacingRight, int viewSpacingBottom) {
            P.mView = view;
            P.mViewSpacingSpecified = true;
            P.mViewSpacingLeft = viewSpacingLeft;
            P.mViewSpacingTop = viewSpacingTop;
            P.mViewSpacingRight = viewSpacingRight;
            P.mViewSpacingBottom = viewSpacingBottom;
            return this;
        }

        /**
         * Sets the Dialog to use the inverse background, regardless of what the
         * contents is.
         *
         * @param useInverseBackground Whether to use the inverse background
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setInverseBackgroundForced(boolean useInverseBackground) {
            P.mForceInverseBackground = useInverseBackground;
            return this;
        }

        /**
         * Creates a {@link AlertDialog} with the arguments supplied to this builder. It does not
         * {@link Dialog#show()} the dialog. This allows the user to do any extra processing
         * before displaying the dialog. Use {@link #show()} if you don't have any other processing
         * to do and want this to be created and displayed.
         * @return see android.app.AlertDialog.Builder
         */
        public HtcAlertDialog create() {
            final HtcAlertDialog dialog = new HtcAlertDialog(P.mContext);
            P.apply(dialog.mAlert);
            dialog.setCancelable(P.mCancelable);
            if (P.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(P.mOnCancelListener);
            dialog.setOnDismissListener(P.mOnDismissListener);
            if (P.mOnKeyListener != null) {
                dialog.setOnKeyListener(P.mOnKeyListener);
            }
            return dialog;
        }

        /**
         * Creates a {@link AlertDialog} with the arguments supplied to this builder and
         * {@link Dialog#show()}'s the dialog.
         * @return see android.app.AlertDialog.Builder
         */
        public HtcAlertDialog show() {
            HtcAlertDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @hide
     */
    @Override
    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        if (mActionModeChangedListener != null) {
            mActionModeChangedListener.onActionModeStarted(mode);
        }
    }

    private OnActionModeChangedListener mActionModeChangedListener;

    public void setOnActionModeChangedListener(OnActionModeChangedListener onActionModeChangedListener) {
        mActionModeChangedListener = onActionModeChangedListener;
    }
}
