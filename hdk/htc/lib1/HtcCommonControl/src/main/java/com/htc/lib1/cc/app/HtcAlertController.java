/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.htc.lib1.cc.app;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import com.htc.lib1.cc.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.TouchDelegate;

import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.util.res.HtcResUtil;
import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.lib1.cc.widget.HtcCompoundButton.OnCheckedChangeListener;
import com.htc.lib1.cc.widget.HtcIconButton;
import com.htc.lib1.cc.widget.HtcListItemSingleText;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.widget.IHtcAbsListView;

/**
 * Generally, you do not have to use this class directly.
 * To show an alert dialog, use HtcAlertDialog.Builder.
 * @see com.android.internal.AlertController
 */
public class HtcAlertController {

    private final Context mContext;
    private final DialogInterface mDialogInterface;
    private final Window mWindow;

    private CharSequence mTitle;

    private CharSequence mMessage;

    private ListView mListView;

    private View mView;

    private int mViewSpacingLeft;

    private int mViewSpacingTop;

    private int mViewSpacingRight;

    private int mViewSpacingBottom;

    private boolean mViewSpacingSpecified = false;

    private Button mButtonPositive;

    private CharSequence mButtonPositiveText;

    private Message mButtonPositiveMessage;

    private Button mButtonNegative;

    private CharSequence mButtonNegativeText;

    private Message mButtonNegativeMessage;

    private Button mButtonNeutral;

    private CharSequence mButtonNeutralText;

    private Message mButtonNeutralMessage;

    private ScrollView mScrollView;

    private int mIconId = -1;

    private Drawable mIcon;

    private ImageView mIconView;

    private TextView mTitleView;

    private TextView mMessageView;

    private View mCustomTitleView;

    //Mark.SL_Chen custom panel use bright background [
    private boolean mForceInverseBackground = true;
    //Mark.SL_Chen custom panel use bright background ]

    private ListAdapter mAdapter;

    private int mCheckedItem = -1;

    private int mAlertDialogLayout;
    private int mListLayout;
    private int mMultiChoiceItemLayout;
    private int mSingleChoiceItemLayout;
    private int mListItemLayout;
    private int mCheckPanelLayout;

    private Handler mHandler;

    //s: Added by Tiffanie
    private boolean mButtonPositiveDisabled;
    private boolean mButtonNegativeDisabled;
    private boolean mButtonNeutralDisabled;
    //e

    //+Kun
    private boolean mTitleCenter;
    //-Kun

    private int mAutoLinkMask = 0; //Arc, for hyper link feature

    // To support AutoMotive mode
    private boolean mIsAutoMotive;

    // For App control padding in button panel on their own [
    private View mPadding1 = null;
    private View mPadding3 = null;
    // For App control padding in button panel on their own ]

    // henry: new feature at Sense55 to insert a checkbox above the button panel [
    private CharSequence mCheckBoxLabel;
    private boolean mCheckBoxDefault;
    private OnCheckedChangeListener mCheckBoxListener;
    private boolean mCheckBoxEnabled;
    private View mCheckBoxPanel;
    private HtcCheckBox mCheckBoxCheckBox;
    private TextView mCheckBoxTextView;
    // henry: new feature at Sense55 to insert a checkbox above the button panel ]

    private int mMarginM1, mMarginM2, mMarginM3_2, mHtcFooterHeight;

    View.OnClickListener mButtonHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Message m = null;
            if (v == mButtonPositive && mButtonPositiveMessage != null) {
                m = Message.obtain(mButtonPositiveMessage);
            } else if (v == mButtonNegative && mButtonNegativeMessage != null) {
                m = Message.obtain(mButtonNegativeMessage);
            } else if (v == mButtonNeutral && mButtonNeutralMessage != null) {
                m = Message.obtain(mButtonNeutralMessage);
            }
            if (m != null) {
                m.sendToTarget();
            }

            // Post a message so we dismiss after the above handlers are executed
            mHandler.obtainMessage(ButtonHandler.MSG_DISMISS_DIALOG, mDialogInterface)
                    .sendToTarget();
        }
    };

    private static final class ButtonHandler extends Handler {
        // Button clicks have Message.what as the BUTTON{1,2,3} constant
        private static final int MSG_DISMISS_DIALOG = 1;

        private WeakReference<DialogInterface> mDialog;

        /**
         *  Hide Automatically by SDK Team [U12000]
         *  @hide
         */
        public ButtonHandler(DialogInterface dialog) {
            mDialog = new WeakReference<DialogInterface>(dialog);
        }

        /**
         *  Hide Automatically by SDK Team [U12000]
         *  @hide
         */
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case DialogInterface.BUTTON_POSITIVE:
                case DialogInterface.BUTTON_NEGATIVE:
                case DialogInterface.BUTTON_NEUTRAL:
                    ((DialogInterface.OnClickListener) msg.obj).onClick(mDialog.get(), msg.what);
                    break;

                case MSG_DISMISS_DIALOG:
                    ((DialogInterface) msg.obj).dismiss();
            }
        }
    }

    /**
     * the constructor
     *
     * @param context see com.android.internal.app.AlertController
     * @param di see com.android.internal.app.AlertController
     * @param window see com.android.internal.app.AlertController
     */
    public HtcAlertController(Context context, DialogInterface di, Window window) {
        mContext = context;
        mDialogInterface = di;
        mWindow = window;
        mHandler = new ButtonHandler(di);

// TODO get these from theme
        mAlertDialogLayout = R.layout.alert_dialog;
        mListLayout = R.layout.select_dialog;
        mMultiChoiceItemLayout = R.layout.dialog_listitem_check;
        mSingleChoiceItemLayout = R.layout.dialog_listitem_radio;
        mListItemLayout = R.layout.dialog_listitem;
        mCheckPanelLayout = R.layout.dialog_checkpanel;

        mMarginM1 = context.getResources().getDimensionPixelOffset(R.dimen.margin_l);
        mMarginM2 = context.getResources().getDimensionPixelOffset(R.dimen.margin_m);
        mMarginM3_2 = context.getResources().getDimensionPixelOffset(R.dimen.margin_s_2);
        mHtcFooterHeight = context.getResources().getDimensionPixelSize(R.dimen.htc_footer_height);
    }

    /**
     * check if a view is a text editor or contains a text editor
     *
     * @param v the view to be checked
     * @return boolean indicating the result
     */
    static boolean canTextInput(View v) {
        if (v.onCheckIsTextEditor()) {
            return true;
        }

        if (!(v instanceof ViewGroup)) {
            return false;
        }

        ViewGroup vg = (ViewGroup)v;
        int i = vg.getChildCount();
        while (i > 0) {
            i--;
            v = vg.getChildAt(i);
            if (canTextInput(v)) {
                return true;
            }
        }

        return false;
    }

    /**
     * setup view and window attributes.
     */
    public void installContent() {
        /* We use a custom title so never request a window title */
        mWindow.requestFeature(Window.FEATURE_NO_TITLE);

        if (mView == null || !canTextInput(mView)) {
            mWindow.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
        mWindow.setContentView(mAlertDialogLayout);
        setupView();
    }

    /**
     * set title
     *
     * @param title see com.android.internal.app.AlertController
     */
    public void setTitle(CharSequence title) {
        mTitle = title;
        if (mTitleView != null) {
            mTitleView.setText(title);
        }
    }

    /**
     * set custom title
     *
     * @param customTitleView see android.internal.app.AlertController
     * @see AlertDialog.Builder#setCustomTitle(View)
     * @deprecated [Not use any longer] customized title is not supported
     */
    public void setCustomTitle(View customTitleView) {
        mCustomTitleView = customTitleView;
    }

    /**
     * set text message
     *
     * @param message the text message
     */
    public void setMessage(CharSequence message) {
        mMessage = message;
        if (mMessageView != null) {
            mMessageView.setText(message);
        }
    }

    /**
     * Set the view to display in the dialog.
     *
     * @param view see com.android.internal.app.AlertController
     */
    public void setView(View view) {
        mView = view;
        mViewSpacingSpecified = false;
    }

    /**
     * Set the view to display in the dialog along with the spacing around that view
     *
     * @param view see com.android.internal.app.AlertController
     * @param viewSpacingLeft see com.android.internal.app.AlertController
     * @param viewSpacingTop see com.android.internal.app.AlertController
     * @param viewSpacingRight see com.android.internal.app.AlertController
     * @param viewSpacingBottom see com.android.internal.app.AlertController
     */
    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight,
            int viewSpacingBottom) {
        mView = view;
        mViewSpacingSpecified = true;
        mViewSpacingLeft = viewSpacingLeft;
        mViewSpacingTop = viewSpacingTop;
        mViewSpacingRight = viewSpacingRight;
        mViewSpacingBottom = viewSpacingBottom;
    }

    /**
     * Sets a click listener or a message to be sent when the button is clicked.
     * You only need to pass one of {@code listener} or {@code msg}.
     *
     * @param whichButton Which button, can be one of
     *            {@link DialogInterface#BUTTON_POSITIVE},
     *            {@link DialogInterface#BUTTON_NEGATIVE}, or
     *            {@link DialogInterface#BUTTON_NEUTRAL}
     * @param text The text to display in positive button.
     * @param listener The {@link DialogInterface.OnClickListener} to use.
     * @param msg The {@link Message} to be sent when clicked.
     */
    public void setButton(int whichButton, CharSequence text,
            DialogInterface.OnClickListener listener, Message msg) {

        if (msg == null && listener != null) {
            msg = mHandler.obtainMessage(whichButton, listener);
        }

        switch (whichButton) {

            case DialogInterface.BUTTON_POSITIVE:
                mButtonPositiveText = text;
                mButtonPositiveMessage = msg;
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                mButtonNegativeText = text;
                mButtonNegativeMessage = msg;
                break;

            case DialogInterface.BUTTON_NEUTRAL:
                mButtonNeutralText = text;
                mButtonNeutralMessage = msg;
                break;

            default:
                throw new IllegalArgumentException("Button does not exist");
        }
    }

    //+Arc, for hyper link feature
    /**
     * @hide
     */
    public void setMessage(CharSequence message, int nAutoLinkMask) {
        mMessage = message;
        mAutoLinkMask = nAutoLinkMask;

        if (mMessageView != null) {
            mMessageView.setAutoLinkMask(nAutoLinkMask);
            mMessageView.setText(message);
        }
    }
    //-Arc

    //s: Added by Tiffanie
    /**
     * @hide
     */
    public void setButtonDisabled(int whichButton, boolean disabled) {
        switch (whichButton) {
        case DialogInterface.BUTTON_POSITIVE:
            mButtonPositiveDisabled = disabled;
            break;

        case DialogInterface.BUTTON_NEUTRAL:
            mButtonNeutralDisabled = disabled;
            break;

        case DialogInterface.BUTTON_NEGATIVE:
            mButtonNegativeDisabled = disabled;
            break;

        default:
            throw new IllegalArgumentException("Button does not exist");
        }
    }
    //e

    //+Kun
    /**
     * Set title center-aligned. Builder sets this true by default.
     * (Sense 50 new design)
     * But if you extend HtcAlertDialog, you have to call this in the
     * constructor by yourself.
     * @param enable true to align title center
     * @deprecated [Module internal use]
     */
    @Deprecated
    public void setTitleCenterEnabled(boolean enable){
        mTitleCenter = enable;
    }
    //-Kun

    /**
     * Helper method for Sense 5.5 new feature to insert a check item above the button panel.
     * See HtcAlertDialog.Builder for details.
     * Caution: user should NOT call this directly.
     *
     * @hide
     */
    public void setCheckBox(CharSequence checkBoxLabel, boolean checkBoxDefault, OnCheckedChangeListener checkBoxListener, boolean checkBoxEnabled) {
        mCheckBoxLabel = checkBoxLabel;
        mCheckBoxDefault = checkBoxDefault;
        mCheckBoxListener = checkBoxListener;
        mCheckBoxEnabled = checkBoxEnabled;
    }

    /**
     * Helper method for Sense 5.5 new feature to insert a check item above the button panel.
     * See HtcAlertDialog.Builder for details.
     * Caution: user should NOT call this directly.
     *
     * @hide
     */
    public void setCheckBoxEnabled(boolean enabled) {
        mCheckBoxEnabled = enabled;
        mCheckBoxCheckBox.setEnabled(enabled);
        mCheckBoxTextView.setEnabled(enabled);
    }

    /**
     * Helper method for Sense 5.5 new feature to insert a check item above the button panel.
     * See HtcAlertDialog.Builder for details.
     * Caution: user should NOT call this directly.
     *
     * @hide
     */
    public void setCheckBoxChecked(boolean checked) {
        mCheckBoxCheckBox.setChecked(checked);
    }

    /**
     * Helper method for Sense 5.5 new feature to insert a check item above the button panel.
     * See HtcAlertDialog.Builder for details.
     * Caution: user should NOT call this directly.
     *
     * @hide
     */
    public boolean isCheckBoxChecked() {
        return mCheckBoxCheckBox.isChecked();
    }

    // For App control padding in button panel on their own [
    /**
     * @hide
     */
    public View getPadding1() {
        return mPadding1;
    }

    /**
     * @hide
     */
    public View getPadding3() {
        return mPadding3;
    }
    // For App control padding in button panel on their own ]

    /**
     * Set resId to 0 if you don't want an icon.
     * @param resId the resourceId of the drawable to use as the icon or 0
     * if you don't want an icon.
     * @deprecated [Not use any longer] icon on header is not supported
     * @hide
     */
    @Deprecated
    public void setIcon(int resId) {
        mIconId = resId;
        if (mIconView != null) {
            if (resId > 0) {
                mIconView.setImageResource(mIconId);
            } else if (resId == 0) {
                mIconView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * set icon in header bar
     *
     * @param icon see com.android.internal.app.AlertController
     * @deprecated [Not use any longer] icon on header is not supported
     * @hide
     */
    public void setIcon(Drawable icon) {
        mIcon = icon;
        if ((mIconView != null) && (mIcon != null)) {
            mIconView.setImageDrawable(icon);
        }
    }

    /**
     * inverse the content background.
     * default is true to set the background to light color according to the UI guideline
     *
     * @param forceInverseBackground see com.android.internal.app.AlertController
     * @deprecated [Module internal use]
     */
    @Deprecated
    public void setInverseBackgroundForced(boolean forceInverseBackground) {
        mForceInverseBackground = forceInverseBackground;
    }

    /**
     * get ListView
     *
     * @return see com.android.internal.app.AlertController
     */
    public ListView getListView() {
        return mListView;
    }

    /**
     * get button
     *
     * @param whichButton see com.android.internal.app.AlertController
     * @return the button you want, or null
     */
    public Button getButton(int whichButton) {
        switch (whichButton) {
            case DialogInterface.BUTTON_POSITIVE:
                return mButtonPositive;
            case DialogInterface.BUTTON_NEGATIVE:
                return mButtonNegative;
            case DialogInterface.BUTTON_NEUTRAL:
                return mButtonNeutral;
            default:
                return null;
        }
    }

    /**
     * callback method on key down, app should not call this
     * @param keyCode the key code
     * @param event the key event
     * @return return true if key is handled.
     * @deprecated [Module internal use] should marked (at)hide
     * @hide
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mScrollView != null && mScrollView.executeKeyEvent(event);
    }

    /**
     * callback method on key up, app should not call this
     * @param keyCode the key code
     * @param event the key event
     * @return return true if key is handled.
     * @deprecated [Module internal use] should marked (at)hide
     * @hide
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mScrollView != null && mScrollView.executeKeyEvent(event);
    }

    private void setupView() {
        LinearLayout contentPanel = (LinearLayout) mWindow.findViewById(R.id.contentPanel);
        setupContent(contentPanel);
        boolean hasButtons = setupButtons();

        LinearLayout topPanel = (LinearLayout) mWindow.findViewById(R.id.topPanel);
        TypedArray a = mContext.obtainStyledAttributes(
                null, R.styleable.AlertDialog, R.attr.alertDialogStyle, 0);
        boolean hasTitle = setupTitle(topPanel);

        View buttonPanel = mWindow.findViewById(R.id.buttonPanel);
        if (!hasButtons && !hasTitle) {
            buttonPanel.setVisibility(View.GONE);
            //mWindow.setCloseOnTouchOutsideIfNotSet(true); TODO this can be resolved by theme attribute: com.android.internal.R.styleable.Window_windowCloseOnTouchOutside
        }

        FrameLayout customPanel = null;
        if (mView != null) {
            customPanel = (FrameLayout) mWindow.findViewById(R.id.customPanel);
            FrameLayout custom = (FrameLayout) mWindow.findViewById(R.id.custom);
            custom.addView(mView, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
            if (mViewSpacingSpecified) {
                custom.setPadding(mViewSpacingLeft, mViewSpacingTop, mViewSpacingRight,
                        mViewSpacingBottom);
            }
            if (mListView != null) {
                ((LinearLayout.LayoutParams) customPanel.getLayoutParams()).weight = 0;
            }
        } else {
            mWindow.findViewById(R.id.customPanel).setVisibility(View.GONE);
        }

        setBackground(topPanel, contentPanel, customPanel, hasButtons, a, hasTitle, buttonPanel);
        a.recycle();
    }

    private boolean setupTitle(LinearLayout topPanel) {
        boolean hasTitle = true;

        if (mCustomTitleView != null) {
            // Add the custom title view directly to the topPanel layout
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            topPanel.addView(mCustomTitleView, 0, lp);

            // Hide the title template
            View titleTemplate = mWindow.findViewById(R.id.title_template);
            titleTemplate.setVisibility(View.GONE);
        } else {
            final boolean hasTextTitle = !TextUtils.isEmpty(mTitle);

            mIconView = (ImageView) mWindow.findViewById(R.id.icon);
            if (hasTextTitle) {
                /* Display the title if a title is supplied, else hide it */
                mTitleView = (TextView) mWindow.findViewById(R.id.alertTitle);

                mTitleView.setText(mTitle);

                /* Do this last so that if the user has supplied any
                 * icons we use them instead of the default ones. If the
                 * user has specified 0 then make it disappear.
                 */
                if (mIconId > 0) {
                    mIconView.setImageResource(mIconId);
                } else if (mIcon != null) {
                    mIconView.setImageDrawable(mIcon);
                } else if (mIconId == 0) {

                    /* Apply the padding from the icon to ensure the
                     * title is aligned correctly.
                     */
                    mTitleView.setPadding(mIconView.getPaddingLeft(),
                            mIconView.getPaddingTop(),
                            mIconView.getPaddingRight(),
                            mIconView.getPaddingBottom());
                    mIconView.setVisibility(View.GONE);
                }
                // +Kun(2009-04-22), center template
                if (mTitleCenter) {
                    mTitleView.setGravity(Gravity.CENTER);
                }
                // -Kun
                //For support AutoMotive Dialog [
                if (mIsAutoMotive && mContext != null) {
                    // set title text style to FL01
                    mTitleView.setTextAppearance(mContext, R.style.fixed_automotive_title_primary_s);
                    // set left/right margin to M1
                    //View titleTemplate = topPanel.findViewById(R.id.title_template);
                    //ViewGroup.LayoutParams lp = titleTemplate.getLayoutParams();
                    //if (lp instanceof ViewGroup.MarginLayoutParams) {
                    //    Resources res = mContext.getResources();
                    //    int M1 = res.getDimensionPixelOffset(R.dimen.margin_l);
                    //    ((ViewGroup.MarginLayoutParams) lp).leftMargin = M1;
                    //    ((ViewGroup.MarginLayoutParams) lp).rightMargin = M1;
                    //    titleTemplate.setLayoutParams(lp);
                    //}
                }
                //For support AutoMotive Dialog ]
            } else {

                // Hide the title template
                View titleTemplate = mWindow.findViewById(R.id.title_template);
                titleTemplate.setVisibility(View.GONE);
                mIconView.setVisibility(View.GONE);
                topPanel.setVisibility(View.GONE);
                hasTitle = false;
            }
        }
        return hasTitle;
    }

    private void setupContent(LinearLayout contentPanel) {
        mScrollView = (ScrollView) mWindow.findViewById(R.id.scrollView);
        mScrollView.setFocusable(false);
        mScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // Special case for users that only want to display a String
        mMessageView = (TextView) mWindow.findViewById(R.id.message);
        if (mMessageView == null) {
            return;
        }

        contentPanel.setMinimumHeight(HtcAlertDialog.getDefaultListItemHeight(mContext, mIsAutoMotive));
        if (mMessage != null) {
            //+Arc, for hyper link feature
            if (mAutoLinkMask != 0) mMessageView.setAutoLinkMask(mAutoLinkMask);
            //-Arc
            mMessageView.setText(mMessage);

            //For support AutoMotive Dialog
            if (mIsAutoMotive) {
                // change font style to FL14
                mMessageView.setTextAppearance(mContext, R.style.fixed_automotive_list_body_primary_m);
                // change padding to M1
                int leftPad = mMarginM1;
                int topPad = mMessageView.getPaddingTop();
                int rightPad = leftPad;
                int bottomPad = mMessageView.getPaddingBottom();
                mMessageView.setPadding(leftPad, topPad, rightPad, bottomPad);
            }
            //For support AutoMotive Dialog
        } else {
            mMessageView.setVisibility(View.GONE);
            mScrollView.removeView(mMessageView);

            if (mListView != null) {
                contentPanel.removeView(mWindow.findViewById(R.id.scrollView));
                contentPanel.addView(mListView,
                        new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
                contentPanel.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 0, 1.0f));
            } else {
                contentPanel.setVisibility(View.GONE);
            }
        }

        // Sense55 new feature: support default checkbox at the bottom
        if (!TextUtils.isEmpty(mCheckBoxLabel)) {
            //mCheckBoxPanel = mWindow.findViewById(R.id.checkPanel);
            View buttonPanel = mWindow.findViewById(R.id.buttonPanel);
            ViewGroup parentPanel = (ViewGroup) buttonPanel.getParent();
            int index = parentPanel.indexOfChild(buttonPanel);

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mCheckBoxPanel = inflater.inflate(mCheckPanelLayout, parentPanel, false);

            // setup basic functionality
            mCheckBoxCheckBox = (HtcCheckBox) mCheckBoxPanel.findViewById(android.R.id.checkbox);
            mCheckBoxCheckBox.setChecked(mCheckBoxDefault);
            mCheckBoxCheckBox.setOnCheckedChangeListener(mCheckBoxListener);

            // user can also tap on text to toggle
            mCheckBoxTextView = (TextView) mCheckBoxPanel.findViewById(android.R.id.text1);
            mCheckBoxTextView.setText(mCheckBoxLabel);
            parentPanel.post(new Runnable() {
                    // Post in parent's queue to make sure the parent lays out
                    // before we call get HitRect
                    public void run() {
                        Rect tmpRect = new Rect();
                        mCheckBoxTextView.getHitRect(tmpRect);

                        final Rect hitRect = new Rect();
                        mCheckBoxCheckBox.getHitRect(hitRect);
                        hitRect.union(tmpRect);

                        ((ViewGroup) mCheckBoxCheckBox.getParent()).setTouchDelegate(
                                new TouchDelegate(hitRect, mCheckBoxCheckBox));
                    }
                    });
            //mCheckBoxTextView.setOnClickListener(new OnClickListener() {
            //    public void onClick(View v) {
            //        mCheckBoxCheckBox.toggle();
            //    }
            //});

            // enable or disable
            setCheckBoxEnabled(mCheckBoxEnabled);

            // adjust paddings inbetween
            if (null != mMessage && null == mView) {
                // if concatenated directly after text message (no custom view)
                // set message padding to 0, and remove minimum height constrain
                int leftPad = mMessageView.getPaddingLeft();
                int topPad = mMessageView.getPaddingTop();
                int rightPad = mMessageView.getPaddingRight();
                mMessageView.setPadding(leftPad, topPad, rightPad, 0);

                final int M3x2 = mMarginM3_2;
                leftPad = mCheckBoxCheckBox.getPaddingLeft();
                rightPad = mCheckBoxCheckBox.getPaddingRight();
                int bottomPad = mCheckBoxCheckBox.getPaddingBottom();
                mCheckBoxCheckBox.setPadding(leftPad, M3x2, rightPad, bottomPad);

                contentPanel.setMinimumHeight(0);
            } else if (null == mMessage && null == mView && null != mListView) {
                // if concatenated after list view
                // show divider
                View checkBoxDivider = mCheckBoxPanel.findViewById(R.id.checkBoxDivider);
                LayoutParams lp = checkBoxDivider.getLayoutParams();
                lp.height = checkBoxDivider.getBackground().getIntrinsicHeight();
                checkBoxDivider.setLayoutParams(lp);
                checkBoxDivider.setVisibility(View.VISIBLE);
            }

            // adjust for automotive mode
            // do nothing

            // done and show
            //mCheckBoxPanel.setVisibility(View.VISIBLE);
            parentPanel.addView(mCheckBoxPanel, index, new LinearLayout.LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private boolean setupButtons() {
        int BIT_BUTTON_POSITIVE = 1;
        int BIT_BUTTON_NEGATIVE = 2;
        int BIT_BUTTON_NEUTRAL = 4;
        int whichButtons = 0;
        mButtonPositive = (Button) mWindow.findViewById(R.id.button1);
        mButtonPositive.setOnClickListener(mButtonHandler);

        if (TextUtils.isEmpty(mButtonPositiveText)) {
            mButtonPositive.setVisibility(View.GONE);
        } else {
            mButtonPositive.setText(mButtonPositiveText);
            mButtonPositive.setVisibility(View.VISIBLE);
            whichButtons = whichButtons | BIT_BUTTON_POSITIVE;
        }

        mButtonNegative = (Button) mWindow.findViewById(R.id.button2);
        mButtonNegative.setOnClickListener(mButtonHandler);

        if (TextUtils.isEmpty(mButtonNegativeText)) {
            mButtonNegative.setVisibility(View.GONE);
        } else {
            mButtonNegative.setText(mButtonNegativeText);
            mButtonNegative.setVisibility(View.VISIBLE);

            whichButtons = whichButtons | BIT_BUTTON_NEGATIVE;
        }

        mButtonNeutral = (Button) mWindow.findViewById(R.id.button3);
        mButtonNeutral.setOnClickListener(mButtonHandler);

        if (TextUtils.isEmpty(mButtonNeutralText)) {
            mButtonNeutral.setVisibility(View.GONE);
        } else {
            mButtonNeutral.setText(mButtonNeutralText);
            mButtonNeutral.setVisibility(View.VISIBLE);

            whichButtons = whichButtons | BIT_BUTTON_NEUTRAL;
        }

        //s: Added by Tiffanie
        if (mButtonPositiveDisabled) {
            mButtonPositive.setEnabled(false);
        }
        if (mButtonNegativeDisabled) {
            mButtonNegative.setEnabled(false);
        }
        if (mButtonNeutralDisabled) {
            mButtonNeutral.setEnabled(false);
        }
        //e

        /*
         * If we only have 1 button it should be centered on the layout and
         * expand to fill 50% of the available space.
         */
        if (whichButtons == BIT_BUTTON_POSITIVE) {
            centerButton(mButtonPositive);
        } else if (whichButtons == BIT_BUTTON_NEGATIVE) {
            centerButton(mButtonNegative);
        } else if (whichButtons == BIT_BUTTON_NEUTRAL) {
            centerButton(mButtonNeutral);
        }

        Context context = mContext;
        if(context != null) {
            mPadding1 = mWindow.findViewById(R.id.padding1);
            mPadding3 = mWindow.findViewById(R.id.padding3);

            //For support AutoMotive Dialog
            if(mIsAutoMotive) {
                // set button text style to FL04
                int button_font_style;
                button_font_style = R.style.fixed_automotive_darklist_primary_xs;
                mButtonPositive.setTextAppearance(context, button_font_style);
                mButtonNegative.setTextAppearance(context, button_font_style);
                mButtonNeutral.setTextAppearance(context, button_font_style);

                mPadding1.setBackgroundResource(R.drawable.common_b_div_land);
                mPadding3.setBackgroundResource(R.drawable.common_b_div_land);
            }
            //For support AutoMotive Dialog

            int dividerHeight = mHtcFooterHeight - 2 * mMarginM2;
            int dividerWidth = mPadding1.getBackground().getIntrinsicWidth();
            ViewGroup.LayoutParams lp = null;
            if (whichButtons == (BIT_BUTTON_POSITIVE | BIT_BUTTON_NEGATIVE)) {
                mPadding1.setVisibility(View.VISIBLE);
                lp = mPadding1.getLayoutParams();
                lp.height = dividerHeight;
                lp.width = dividerWidth;
                mPadding1.setLayoutParams(lp);
            } else if (whichButtons == (BIT_BUTTON_POSITIVE | BIT_BUTTON_NEUTRAL)) {
                mPadding3.setVisibility(View.VISIBLE);
                lp = mPadding3.getLayoutParams();
                lp.height = dividerHeight;
                lp.width = dividerWidth;
                mPadding3.setLayoutParams(lp);
            } else if (whichButtons == (BIT_BUTTON_NEGATIVE | BIT_BUTTON_NEUTRAL)) {
                mPadding1.setVisibility(View.VISIBLE);
                lp = mPadding1.getLayoutParams();
                lp.height = dividerHeight;
                lp.width = dividerWidth;
                mPadding1.setLayoutParams(lp);
            } else if (whichButtons == (BIT_BUTTON_POSITIVE | BIT_BUTTON_NEGATIVE | BIT_BUTTON_NEUTRAL)) {
                mPadding1.setVisibility(View.VISIBLE);
                mPadding3.setVisibility(View.VISIBLE);
                lp = mPadding1.getLayoutParams();
                lp.height = dividerHeight;
                lp.width = dividerWidth;
                mPadding1.setLayoutParams(lp);
                lp = mPadding3.getLayoutParams();
                lp.height = dividerHeight;
                lp.width = dividerWidth;
                mPadding3.setLayoutParams(lp);
            }
        }

        ((HtcIconButton)mButtonPositive).useSelectorWhenPressed(true);
        ((HtcIconButton)mButtonNegative).useSelectorWhenPressed(true);
        ((HtcIconButton)mButtonNeutral).useSelectorWhenPressed(true);

        setupButtonTextLayout();
        final boolean enableAllCaps = HtcResUtil.isInAllCapsLocale(context);
        mButtonPositive.setAllCaps(enableAllCaps);
        mButtonNegative.setAllCaps(enableAllCaps);
        mButtonNeutral.setAllCaps(enableAllCaps);
        return whichButtons != 0;
    }

    private void centerButton(Button button) {
        //+Kun(2008/04/22), update button layout
        float weight = 0.0f;
        // update left
        View leftSpacer = mWindow.findViewById(R.id.leftSpacer);
        if (leftSpacer != null) {
            LinearLayout.LayoutParams paramsL = (LinearLayout.LayoutParams) leftSpacer.getLayoutParams();
            paramsL.weight = weight;
            leftSpacer.setLayoutParams(paramsL);
            leftSpacer.setVisibility(View.VISIBLE);
        }
        // update right
        View rightSpacer = mWindow.findViewById(R.id.rightSpacer);
        if (rightSpacer != null) {
            LinearLayout.LayoutParams paramsR = (LinearLayout.LayoutParams) rightSpacer.getLayoutParams();
            paramsR.weight = weight;
            rightSpacer.setLayoutParams(paramsR);
            rightSpacer.setVisibility(View.VISIBLE);
        }
        //-Kun
    }

    private void setBackground(LinearLayout topPanel, LinearLayout contentPanel,
            View customPanel, boolean hasButtons, TypedArray a, boolean hasTitle,
            View buttonPanel) {

        /* Get all the different background required */
        int fullDark = a.getResourceId(R.styleable.AlertDialog_android_fullDark, R.drawable.common_dialogbox_full_dark);
        int topDark = a.getResourceId(R.styleable.AlertDialog_android_topDark, R.drawable.common_dialogbox_top_dark);
        int centerDark = a.getResourceId(R.styleable.AlertDialog_android_centerDark, R.drawable.common_dialogbox_center_dark);
        int bottomDark = a.getResourceId(R.styleable.AlertDialog_android_bottomDark, R.drawable.common_dialogbox_bottom_dark);
        int fullBright = a.getResourceId(R.styleable.AlertDialog_android_fullBright, R.drawable.common_dialogbox_full_bright);
        int topBright = a.getResourceId(R.styleable.AlertDialog_android_topBright, R.drawable.common_dialogbox_top_bright);
        int centerBright = a.getResourceId(R.styleable.AlertDialog_android_centerBright, R.drawable.common_dialogbox_center_bright);
        int bottomBright = a.getResourceId(R.styleable.AlertDialog_android_bottomBright, R.drawable.common_dialogbox_bottom_bright);
        int bottomMedium = a.getResourceId(R.styleable.AlertDialog_android_bottomMedium, R.drawable.common_dialogbox_bottom_medium);

        if (mIsAutoMotive) {
            // override assets for automotive mode
            bottomMedium = R.drawable.automotive_common_dialogbox_bottom_medium;
            bottomDark = R.drawable.automotive_common_dialogbox_bottom_dark;
            topDark = R.drawable.automotive_common_dialogbox_top_dark;
            centerDark = R.drawable.automotive_common_dialogbox_center_dark;
        }

        // get category color for Sense6
        //final int[] categoryAttr = { R.attr.multiply_color };
        //TypedArray b = mContext.obtainStyledAttributes(categoryAttr);
        final int categoryColor = //b.getColor(0, Color.TRANSPARENT);
        HtcCommonUtil.getCommonThemeColor(mContext, R.styleable.ThemeColor_multiply_color);
        //b.recycle();

        if (HtcBuildFlag.Htc_DEBUG_flag) {
            Log.d("HtcAlertController", "setBackground:"
                    + " fullDark=" + fullDark
                    + " topDark=" + topDark
                    + " centerDark=" + centerDark
                    + " bottomDark=" + bottomDark
                    + " fullBright=" + fullBright
                    + " topBright=" + topBright
                    + " centerBright=" + centerBright
                    + " bottomBright=" + bottomBright
                    + " bottomMedium=" + bottomMedium
                    + " mForceInverseBackground=" + mForceInverseBackground
                    + " mIsAutoMotive=" + mIsAutoMotive
                    + " hasTitle=" + hasTitle
                    + " hasButtons=" + hasButtons
                    + " categoryColor=" + categoryColor
                    );
        }

        /*
         * We now set the background of all of the sections of the alert.
         * First collect together each section that is being displayed along
         * with whether it is on a light or dark background, then run through
         * them setting their backgrounds.  This is complicated because we need
         * to correctly use the full, top, middle, and bottom graphics depending
         * on how many views they are and where they appear.
         */

        View[] views = new View[4];
        boolean[] light = new boolean[4];
        View lastView = null;
        boolean lastLight = false;

        int pos = 0;
        if (hasTitle) {
            views[pos] = topPanel;
            light[pos] = false;
            pos++;
        }

        /* The contentPanel displays either a custom text message or
         * a ListView. If it's text we should use the dark background
         * for ListView we should use the light background. If neither
         * are there the contentPanel will be hidden so set it as null.
         */
        views[pos] = (contentPanel.getVisibility() == View.GONE)
                ? null : contentPanel;
        //Mark.SL_Chen contentPanel use bright background [
        //light[pos] = mListView != null;
        light[pos] = true;
        //Mark.SL_Chen contentPanel use bright background [
        pos++;
        if (customPanel != null) {
            views[pos] = customPanel;
            light[pos] = mForceInverseBackground;
            pos++;
        }
        if (hasButtons || hasTitle) {
            views[pos] = buttonPanel;
            light[pos] = false;
        }

        boolean setView = false;
        for (pos=0; pos<views.length; pos++) {
            View v = views[pos];
            if (v == null) {
                continue;
            }
            if (lastView != null) {
                if (!setView) {
                    lastView.setBackgroundResource(lastLight ? topBright : topDark);
                    if (hasTitle && !mIsAutoMotive) { // do not support theme change in auto mode
                        Drawable topBackground = lastView.getBackground();
                        topBackground.mutate().setColorFilter(categoryColor, PorterDuff.Mode.SRC_ATOP);
                        lastView.setBackground(topBackground);
                    }
                } else {
                    lastView.setBackgroundResource(lastLight && !mIsAutoMotive ? centerBright : centerDark);
                }
                setView = true;
            }
            lastView = v;
            lastLight = light[pos];
        }

        if (lastView != null) {
            if (setView) {

                /* ListViews will use the Bright background but buttons use
                 * the Medium background.
                 */
                lastView.setBackgroundResource(hasButtons ? bottomMedium :
                    (mIsAutoMotive ? bottomDark : bottomBright));
                setupButtonsHeight(lastView.getBackground(), lastView, hasButtons);
            } else {
                lastView.setBackgroundResource(lastLight ? fullBright : fullDark);
            }
        }

        /* TODO: uncomment section below. The logic for this should be if
         * it's a Contextual menu being displayed AND only a Cancel button
         * is shown then do this.
         */
//        if (hasButtons && (mListView != null)) {

            /* Yet another *special* case. If there is a ListView with buttons
             * don't put the buttons on the bottom but instead put them in the
             * footer of the ListView this will allow more items to be
             * displayed.
             */

            /*
            contentPanel.setBackgroundResource(bottomBright);
            buttonPanel.setBackgroundResource(centerMedium);
            ViewGroup parent = (ViewGroup) mWindow.findViewById(R.id.parentPanel);
            parent.removeView(buttonPanel);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.MATCH_PARENT);
            buttonPanel.setLayoutParams(params);
            mListView.addFooterView(buttonPanel);
            */
//        }

        if ((mListView != null) && (mAdapter != null)) {
            mListView.setAdapter(mAdapter);
            if (mCheckedItem > -1) {
                mListView.setItemChecked(mCheckedItem, true);
                mListView.setSelection(mCheckedItem);
            }
        }

        if (null != mCheckBoxPanel) {
            mCheckBoxPanel.setBackgroundResource(mForceInverseBackground && !mIsAutoMotive ? centerBright : centerDark);
        }
    }

    private void setupButtonTextLayout() {
        // sync button text layout policy consistent with Vincent's Footer Button
        // HenryCY_Lee on 20 Mar 2013
        // now we will not adjust the policy by language.
        // all buttons should behave as below:
        // 1. if contains space(s) => 2 lines, truncateAt.END
        // 2. if no space(s) => 1 lines, truncateAt.MARQUEE

        //String language = mContext.getResources().getConfiguration().locale.getLanguage();
        //String chinese = Locale.CHINESE.getLanguage(); // zh
        //String japanese = Locale.JAPANESE.getLanguage(); // ja
        //String korean = Locale.KOREAN.getLanguage(); // ko
        //String vietnamese = "vi";
        //boolean isCJKV = chinese.equals(language) || japanese.equals(language) ||
        //    korean.equals(language) || vietnamese.equals(language);

        Button[] buttons = { mButtonPositive, mButtonNegative, mButtonNeutral };
        for (Button button : buttons) {
            CharSequence text = button.getText();
            if (TextUtils.isEmpty(text)) {
                continue;
            }

            boolean containsSpace = false;
            //if (!isCJKV) {
                for (int i = 0; i < text.length(); ++i) {
                    if (Character.isWhitespace(text.charAt(i))) {
                        containsSpace = true;
                        break;
                    }
                }
            //}

            //if (isCJKV || containsSpace) {
            if (containsSpace) {
                // allow 2 lines
                button.setSingleLine(false);
                button.setMaxLines(2);
                button.setEllipsize(TruncateAt.END);
            } else {
                // one line only
                button.setSingleLine(true);
                button.setMaxLines(1);
                button.setEllipsize(TruncateAt.MARQUEE);
                button.setHorizontalFadingEdgeEnabled(true);
            }
        }
    }

    private void setupButtonsHeight(Drawable background, View view, boolean hasButtons) {
        // if no buttons, set to wrap content
        if (!hasButtons) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            view.setLayoutParams(lp);
        }
    }

    /**
    * This api is for enable AutoMotive mode
    * When you call this api, your activity must lock at landscape mode always.
    * otherwise will have exception.
    * And you also can not call this at landscape and rotate your activity to portrait mode.
    * This will cause HtcAlertDialog truncate.
    * @hide
    */
    public void setIsAutoMotive(boolean isAutoMotive) {
        mIsAutoMotive = isAutoMotive;

        if (mIsAutoMotive) {
            mAlertDialogLayout = R.layout.alert_dialog_automotive;
            mMultiChoiceItemLayout = R.layout.dialog_listitem_check_automotive;
            mSingleChoiceItemLayout = R.layout.dialog_listitem_radio_automotive;
            mListItemLayout = R.layout.dialog_listitem_automotive;
            mListLayout = R.layout.select_dialog_automotive;
            mCheckPanelLayout = R.layout.dialog_checkpanel_automotive;
            // henry: do not set to automotive mode and back
        }
    }

    /**
     * The parameters for the alert.
     */
    public static class AlertParams {
        /**
         * context of the dialog
         */
        public final Context mContext;
        /**
         * layoutInflater of the dialog
         * @hide
         */
        public final LayoutInflater mInflater;
        /**
         * icon resource id for this dialog
         */
        public int mIconId = 0;
        /**
         * icon drawable for this dialog
         */
        public Drawable mIcon;
        /**
         * title text for this dialog
         */
        public CharSequence mTitle;
        /**
         * title view
         */
        public View mCustomTitleView;
        /**
         * message text for this dialog
         */
        public CharSequence mMessage;
        /**
         * text on the positive button
         */
        public CharSequence mPositiveButtonText;
        /**
         * click listener for the positive button
         */
        public DialogInterface.OnClickListener mPositiveButtonListener;
        /**
         * text on the negative button
         */
        public CharSequence mNegativeButtonText;
        /**
         * click listener for the negative button
         */
        public DialogInterface.OnClickListener mNegativeButtonListener;
        /**
         * text on the neutral button
         */
        public CharSequence mNeutralButtonText;
        /**
         * click listener for the neutral button
         */
        public DialogInterface.OnClickListener mNeutralButtonListener;
        /**
         * is the dialog cancelable
         */
        public boolean mCancelable;
        /**
         * listener when the dialog is canceled
         */
        public DialogInterface.OnCancelListener mOnCancelListener;
        /**
         * call-back on dismiss
         * @hide
         */
        public DialogInterface.OnDismissListener mOnDismissListener;
        /**
         * the callback that will be called if a key is dispatched to the dialog.
         */
        public DialogInterface.OnKeyListener mOnKeyListener;
        /**
         * content of the list items
         */
        public CharSequence[] mItems;
        /**
         * adapter of the listview
         */
        public ListAdapter mAdapter;
        /**
         * listener when clicked
         */
        public DialogInterface.OnClickListener mOnClickListener;
        /**
         * view of the dialog
         */
        public View mView;
        /**
         * padding left of the view
         */
        public int mViewSpacingLeft;
        /**
         * padding top of the view
         */
        public int mViewSpacingTop;
        /**
         * padding right of the view
         */
        public int mViewSpacingRight;
        /**
         * padding bottom of the view
         */
        public int mViewSpacingBottom;
        /**
         * if the padding of the view was specified
         */
        public boolean mViewSpacingSpecified = false;
        /**
         * status of check items
         */
        public boolean[] mCheckedItems;
        /**
         * is multi-choice
         */
        public boolean mIsMultiChoice;
        /**
         * is single-choice
         */
        public boolean mIsSingleChoice;
        /**
         * the checked item
         */
        public int mCheckedItem = -1;
        /**
         * listener of checkbox
         */
        public DialogInterface.OnMultiChoiceClickListener mOnCheckboxClickListener;
        /**
         * cursor to data
         */
        public Cursor mCursor;
        /**
         * Column Index
         */
        public String mLabelColumn;
        /**
         * Column checked
         */
        public String mIsCheckedColumn;
        /**
         * is inverse background
         */
        //Mark.SL_Chen custom panel use bright background [
        public boolean mForceInverseBackground = true;
        //Mark.SL_Chen custom panel use bright background ]
        /**
         * listener to be invoked when an item in the list is selected.
         */
        public AdapterView.OnItemSelectedListener mOnItemSelectedListener;
        /**
         * callback to be invoked before the ListView will be bound to an adapter
         */
        public OnPrepareListViewListener mOnPrepareListViewListener;
        //s: Added by Tiffanie
        /**
         * @hide
         */
        public boolean mPositiveButtonDisabled;
        /**
         * @hide
         */
        public boolean mNeutralButtonDisabled;
        /**
         * @hide
         */
        public boolean mNegativeButtonDisabled;
        //e
        //+Kun
        /**
         * Sense6 default style. also set in HtcAlertDialog's constructor
         * @hide
         */
        public boolean mTitleCenter = false;
        //-Kun
        /**
         * @hide
         */
        public int mAutoLinkMask = 0; //Arc, for hyper link feature
        /**
         * For support AutoMotive Dialog
         */
        public boolean mIsAutoMotive = false;
        /**
         * Check-item's text
         * @hide
         */
        public CharSequence mCheckBoxLabel;
        /**
         * Check-item's default checked state
         * @hide
         */
        public boolean mCheckBoxDefault;
        /**
         * Check-item's onCheckedChangeListener
         * @hide
         */
        public OnCheckedChangeListener mCheckBoxListener;
        /**
         * Check-item's enabled state
         * @hide
         */
        public boolean mCheckBoxEnabled;

        /**
         * Interface definition for a callback to be invoked before the ListView
         * will be bound to an adapter.
         */
        public interface OnPrepareListViewListener {

            /**
             * Called before the ListView is bound to an adapter.
             * @param listView The ListView that will be shown in the dialog.
             */
            void onPrepareListView(ListView listView);
        }

        /**
         * the constructor
         *
         * @param context see com.android.internal.app.AlertController.AlertParams
         */
        public AlertParams(Context context) {
            mContext = context;
            mCancelable = true;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /**
         * apply settings to the alert controller
         *
         * @param dialog the alert controller
         */
        public void apply(HtcAlertController dialog) {
            //For support AutoMotive [
            if(mIsAutoMotive) {
                dialog.setIsAutoMotive(true);
            }
            //For support AutoMotive ]

            if (mCustomTitleView != null) {
                dialog.setCustomTitle(mCustomTitleView);
            } else {
                if (mTitle != null) {
                    dialog.setTitle(mTitle);
                }
                if (mIcon != null) {
                    dialog.setIcon(mIcon);
                }
                if (mIconId >= 0) {
                    dialog.setIcon(mIconId);
                }
            }
            if (mMessage != null) {
                //+Arc, for hyper link feature
                if (mAutoLinkMask == 0) {
                    dialog.setMessage(mMessage);
                } else {
                    dialog.setMessage(mMessage, mAutoLinkMask);
                }//-Arc
            }
            if (mPositiveButtonText != null) {
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, mPositiveButtonText,
                        mPositiveButtonListener, null);
            }
            if (mNegativeButtonText != null) {
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, mNegativeButtonText,
                        mNegativeButtonListener, null);
            }
            if (mNeutralButtonText != null) {
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL, mNeutralButtonText,
                        mNeutralButtonListener, null);
            }
            //s: Added by Tiffanie
            if (mPositiveButtonDisabled) {
                dialog.setButtonDisabled(DialogInterface.BUTTON_POSITIVE, mPositiveButtonDisabled);
            }
            if (mNeutralButtonDisabled) {
                dialog.setButtonDisabled(DialogInterface.BUTTON_NEUTRAL, mNeutralButtonDisabled);
            }
            if (mNegativeButtonDisabled) {
                dialog.setButtonDisabled(DialogInterface.BUTTON_NEGATIVE, mNegativeButtonDisabled);
            }
            //e
            dialog.setInverseBackgroundForced(mForceInverseBackground);
            // For a list, the client can either supply an array of items or an
            // adapter or a cursor
            if ((mItems != null) || (mCursor != null) || (mAdapter != null)) {
                createListView(dialog);
            }
            if (mView != null) {
                if (mViewSpacingSpecified) {
                    dialog.setView(mView, mViewSpacingLeft, mViewSpacingTop, mViewSpacingRight,
                            mViewSpacingBottom);
                } else {
                    dialog.setView(mView);
                }
            }
            //+Kun
            dialog.setTitleCenterEnabled(mTitleCenter);
            //-Kun
            /*
            dialog.setCancelable(mCancelable);
            dialog.setOnCancelListener(mOnCancelListener);
            if (mOnKeyListener != null) {
                dialog.setOnKeyListener(mOnKeyListener);
            }
            */
            dialog.setCheckBox(mCheckBoxLabel, mCheckBoxDefault, mCheckBoxListener, mCheckBoxEnabled);
        }

        private void createListView(final HtcAlertController dialog) {
            final HtcListView listView = (HtcListView)
                    mInflater.inflate(dialog.mListLayout, null);
            ListAdapter adapter;

            listView.enableAnimation(IHtcAbsListView.ANIM_OVERSCROLL, false);
            listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

            if (mIsMultiChoice) {
                if (mCursor == null) {
                    adapter = new ArrayAdapter<CharSequence>(
                            mContext, 0, 0, mItems) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            if (null == convertView) {
                                convertView = mInflater.inflate(dialog.mMultiChoiceItemLayout, parent, false);
                            }
                            HtcListItemSingleText text = (HtcListItemSingleText) convertView.findViewById(android.R.id.text1);
                            if (null != text) {
                                text.setText(this.getItem(position));
                            }
                            if (mCheckedItems != null) {
                                boolean isItemChecked = mCheckedItems[position];
                                if (isItemChecked) {
                                    listView.setItemChecked(position, true);
                                }
                            }
                            return convertView;
                        }
                    };
                } else {
                    adapter = new CursorAdapter(mContext, mCursor, false) {
                        private final int mLabelIndex;
                        private final int mIsCheckedIndex;

                        {
                            final Cursor cursor = getCursor();
                            mLabelIndex = cursor.getColumnIndexOrThrow(mLabelColumn);
                            mIsCheckedIndex = cursor.getColumnIndexOrThrow(mIsCheckedColumn);
                        }

                        @Override
                        public void bindView(View view, Context context, Cursor cursor) {
                            HtcListItemSingleText text = (HtcListItemSingleText) view.findViewById(android.R.id.text1);
                            if (null != text) {
                            text.setText(cursor.getString(mLabelIndex));
                            }
                            listView.setItemChecked(cursor.getPosition(),
                                    cursor.getInt(mIsCheckedIndex) == 1);
                        }

                        @Override
                        public View newView(Context context, Cursor cursor, ViewGroup parent) {
                            return mInflater.inflate(dialog.mMultiChoiceItemLayout,
                                    parent, false);
                        }
                    };
                }
            } else {
                final int layout = mIsSingleChoice
                        ? dialog.mSingleChoiceItemLayout : dialog.mListItemLayout;
                if (mCursor == null) {
                    adapter = (mAdapter != null) ? mAdapter
                            : new ArrayAdapter<CharSequence>(mContext, 0, 0, mItems) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    if (null == convertView) {
                                        convertView = mInflater.inflate(layout, parent, false);
                                    }
                                    HtcListItemSingleText text = (HtcListItemSingleText) convertView.findViewById(android.R.id.text1);
                                    if (null != text) {
                                        text.setText(this.getItem(position));
                                    }
                                    return convertView;
                                 }
                    };
                } else {
                    adapter = new CursorAdapter(mContext, mCursor, false) {
                        private final int mLabelIndex;

                        {
                            final Cursor cursor = getCursor();
                            mLabelIndex = cursor.getColumnIndexOrThrow(mLabelColumn);
                        }

                        @Override
                        public void bindView(View view, Context context, Cursor cursor) {
                            HtcListItemSingleText text = (HtcListItemSingleText) view.findViewById(android.R.id.text1);
                            if (null != text) {
                                text.setText(cursor.getString(mLabelIndex));
                            }
                        }

                        @Override
                        public View newView(Context context, Cursor cursor, ViewGroup parent) {
                            View convertView = mInflater.inflate(layout, parent, false);
                            return convertView;
                        }
                    };
                }
            }

            if (mOnPrepareListViewListener != null) {
                mOnPrepareListViewListener.onPrepareListView(listView);
            }

            /* Don't directly set the adapter on the ListView as we might
             * want to add a footer to the ListView later.
             */
            dialog.mAdapter = adapter;
            dialog.mCheckedItem = mCheckedItem;

            if (mOnClickListener != null) {
                listView.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView parent, View v, int position, long id) {
                        mOnClickListener.onClick(dialog.mDialogInterface, position);
                        if (!mIsSingleChoice) {
                            dialog.mDialogInterface.dismiss();
                        }
                    }
                });
            } else if (mOnCheckboxClickListener != null) {
                listView.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView parent, View v, int position, long id) {
                        if (mCheckedItems != null) {
                            mCheckedItems[position] = listView.isItemChecked(position);
                        }
                        mOnCheckboxClickListener.onClick(
                                dialog.mDialogInterface, position, listView.isItemChecked(position));
                    }
                });
            }

            // Attach a given OnItemSelectedListener to the ListView
            if (mOnItemSelectedListener != null) {
                listView.setOnItemSelectedListener(mOnItemSelectedListener);
            }

            if (mIsSingleChoice) {
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            } else if (mIsMultiChoice) {
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            }
            dialog.mListView = listView;
        }
    }

}
