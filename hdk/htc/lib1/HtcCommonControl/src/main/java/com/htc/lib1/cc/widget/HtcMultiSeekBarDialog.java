/**
 * provide the functionality to generate multi-suite(include icon, text, seekbar) Dialog UI
 */
package com.htc.lib1.cc.widget;

import com.htc.lib1.cc.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.accessibility.AccessibilityEvent;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityManager;
import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.cc.util.res.HtcResUtil;

/**
 * The class will popup a panel contain one or more seekbar control The control
 * implemented by extends {@link AlertDialog}
 *
 * @author felka Maintain by Bill Lin
 */
public class HtcMultiSeekBarDialog extends AlertDialog {
    private ViewGroup mRoot;
    // Global mSuiteCount : Include INVISIBLE & VISIBLE ViewGroup
    private int mSuiteCount = 0;
    // Global mVisibleSuiteCount : Only VISIBLE ViewGroup
    private int mVisibleSuiteCount = 0;
    private int mPortraitIconWidth = -1; /* -1 means not initial */
    private int mMarginM4;
    private MultiSeekBarDialogAccessibilityDelegate mAccessibilityDelegate;

    private void init() {
        mSuiteCount = 1;
        mRoot = initRoot();
        mMarginM4 = getContext().getResources().getDimensionPixelOffset(R.dimen.margin_xs);
        addNewSuite(mSuiteCount);
    }

    /**
     * HtcMultiSeekBarDialog constructor to initial one suite
     * @param context The Context the view is running in, through which it can access the current
     *             theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     *            {@link AlertDialog}
     */
    public HtcMultiSeekBarDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init();
    }

    /**
     * HtcMultiSeekBarDialog constructor to initial one suite
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     *            {@link AlertDialog}
     * @param theme The specific Theme from application for superclass
     *            {@link AlertDialog}
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcMultiSeekBarDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        init();
    }

    /**
     * HtcMultiSeekBarDialog constructor to initial the one suite
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     *            {@link AlertDialog}
     * @param cancelable
     *            The boolean to sets whether this dialog is cancelable with the
     *            BACK key
     * @param cancelListener
     *            Set a listener to be invoked when the dialog is canceled
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcMultiSeekBarDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
        init();
    }

    /**
     * Initial the root ViewGroup by a LinearLayout
     *
     * @return ll Return ViewGroup cast from LinearLayout
     */
    private ViewGroup initRoot() {
        LinearLayout ll = new LinearLayout(getContext());
        if (null == ll) {
            Log.e(this.getClass().getName(), "Can't allocate the root view");
            return null;
        }

        ll.setOrientation(LinearLayout.VERTICAL);
        return (ViewGroup) ll;
    }

    /**
     * Add a horizontal divider into the root ViewGroup
     *
     * @return iv The ImageView added with horizontal divider
     */
    private ImageView addHorizontalDivider() {
        ImageView iv = new ImageView(getContext());
        iv.setBackgroundResource(R.drawable.inset_list_divider_dark);

        if (null == iv)
            return null;

        mRoot.addView(iv, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return iv;
    }

    /**
     * Add n-suites that include a suite include a icon, a textview, a seekbar
     * and a vertical divider
     *
     * @param nSuiteCount
     *            the number of suites that is requested by caller.
     * @hide
     */
    public void addNewSuite(int nSuiteCount) {
        if (null == mRoot)
            return;

        for (int i = 0; i < nSuiteCount; i++) {
            if (0 < mRoot.getChildCount())
                addHorizontalDivider();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View v = inflater.inflate(R.layout.multiple_seekbar_dialog, null);
            ImageView iv = (ImageView) v.findViewById(R.id.vertical_divider);
            TypedArray a = getContext().obtainStyledAttributes(R.styleable.HtcDivider);
            iv.setBackground(a.getDrawable(R.styleable.HtcDivider_android_dividerVertical));
            a.recycle();
            mRoot.addView(v, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mSuiteCount++;
            mVisibleSuiteCount++;
        }
    }

    /**
     * Add a suite include a icon, a textview, a seekbar and a vertical divider
     */
    public void addNewSuite() {
        addNewSuite(1);
    }

    /**
     * Create the dialog content
     *
     * @see android.app.AlertDialog#onCreate(android.os.Bundle)
     * @param savedInstanceState
     *            The state of the dialog previously saved by
     *            onSaveInstanceState().
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        // The Background Alpha value , default 80% = 204
        //final int mBackgroundAlpha = 255;
        Resources mRes = getContext().getResources();
        setView(mRoot);

        this.setCancelable(true);
        this.setInverseBackgroundForced(true);
        super.onCreate(savedInstanceState);

        //          Window w = getWindow();
        //          View v = w.findViewById(com.android.internal.R.id.customPanel);
        //          if ( null != v ) {
        //              v.setBackgroundResource(android.R.color.transparent);
        //          }

        if (null != mRoot) {
            Drawable mBackground;
            mBackground = mRes.getDrawable(R.drawable.common_dialogbox_full_dark);
            // Designer draw transparant on asset, so I don't need to set Alpha
            //mBackground.setAlpha(mBackgroundAlpha);
            mRoot.setBackground(mBackground);
        }

        setDialogWindow();
    }

    /**
     * Find the base panel in the nIndex-th suite
     *
     * @param nIndex
     *            the first index is from 0 to Count-1
     */
    private ViewGroup findViewGroupById(int nIndex) {
        if (null == mRoot)
            return null;

        final int nCount = mRoot.getChildCount();
        int nSuiteIndex = 0;
        for (int i = 0; i < nCount; i++) {
            View v = mRoot.getChildAt(i);
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                if (nSuiteIndex == nIndex) {
                    return vg;
                }
                nSuiteIndex++;
            }
        }

        return null;
    }

    /**
     * Find the view in the nIndex-th suite
     *
     * @param nIndex
     *            the first index is from 0 to Count-1
     * @param nID
     *            R.id.text, R.id.icon, and R.id.seekbar
     * @return v The specific View by nIndex
     */
    private View findViewById(int nIndex, int nID) {
        ViewGroup vg = findViewGroupById(nIndex);
        if (null == vg)
            return null;

        View v = vg.findViewById(nID);
        return v;
    }

    /**
     * Get the TextView in the nIndex-suite
     *
     * @param nIndex
     *            find the view in the nIndex-suite. nIndex is from 0 to Count-1
     * @return v The specific TextView by nIndex
     */
    private TextView getTextView(int nIndex) {
        View v = findViewById(nIndex, R.id.text);
        if (null != v && v instanceof TextView)
            return (TextView) v;
        return null;
    }

    /**
     * Get the ImageView in the nIndex-suite
     *
     * @param nIndex
     *            find the view in the nIndex-suite. nIndex is from 0 to Count-1
     * @return v The specific ImageView by nIndex
     */
    public ImageView getImageView(int nIndex) {
        View v = findViewById(nIndex, R.id.icon);
        if (null != v && v instanceof ImageView)
            return (ImageView) v;
        return null;
    }

    /**
     * Get the SeekBar in the nIndex-suite
     *
     * @param nIndex
     *            find the view in the nIndex-suite
     * @return v The specific Seekbar View
     */
    public SeekBar getSeekbar(int nIndex) {
        HtcSeekBar v = (HtcSeekBar) findViewById(nIndex, R.id.seekbar);
        // Set SeekBar Dark(Black) Mode by default, ToDo: Support Light Mode
        v.setDisplayMode(HtcSeekBar.DISPLAY_MODE_BLACK);
        if (null != v && v instanceof SeekBar)
            return v;
        return null;
    }

    /**
     * Get the numbers of Visible Group on the panel
     *
     * @return mVisibleSuiteCount The visible group number
     */
    private int getVisibleGroupCount() {
        return mVisibleSuiteCount;
    }

    /**
     * The method to set the OnSeekBarChangeListener of the seekbar in the
     * nIndex-suite
     *
     * @param nIndex
     *            find the view in the nIndex-suite. nIndex is from 0 to Count-1
     * @param listener
     *            the mSeekbarList to set
     */
    public void setSeekbarSeekListener(int nIndex, SeekBar.OnSeekBarChangeListener listener) {
        SeekBar sb = getSeekbar(nIndex);
        if (null == sb)
            return;
        sb.setOnSeekBarChangeListener(listener);
    }

    /**
     * The method to set the OnSeekBarChangeListener of the seekbar in the
     * nIndex-suite
     *
     * @param nIndex
     *            find the view in the nIndex-suite. nIndex is from 0 to Count-1
     * @param s
     *            the string of TextView for mSeekbarList
     */
    public void setTextViewText(int nIndex, String s) {
        TextView tv = getTextView(nIndex);
        if (null == tv)
            return;
        tv.setTextAppearance(getContext(), R.style.b_button_primary_m);
        tv.setText(s);
    }

    /**
     * set the Drawable of the ImageView in the nIndex-suite
     *
     * @param nIndex
     *            find the view in the nIndex-suite. nIndex is from 0 to Count-1
     * @param drawable
     *            the drawable to set into the ImageView
     */
    public void setImageViewDrawable(int nIndex, Drawable drawable) {
        ImageView iv = getImageView(nIndex);
        if (null == iv)
            return;
        iv.setImageDrawable(drawable);
    }

    /**
     * set the Drawable Resource of the ImageView in the nIndex-suite
     *
     * @param nIndex
     *            find the view in the nIndex-suite. nIndex is from 0 to Count-1
     * @param resId
     *            the Drawable Resource to set
     */
    public void setImageViewResource(int nIndex, int resId) {
        ImageView iv = getImageView(nIndex);
        if (null == iv)
            return;
        iv.setImageResource(resId);
    }

    /**
     * The method to Show/Hide
     *
     * @param nIndex
     *            find the view in the nIndex-suite. nIndex is from 0 to Count-1
     * @param nVisibility
     *            the Drawable Resource to set
     */
    public void setSuiteVisibilty(int nIndex, int nVisibility) {
        if (null == mRoot || (View.VISIBLE != nVisibility && View.GONE != nVisibility && View.INVISIBLE != nVisibility))
            return;

        /*
         * The loop to traverse all children of mRoot to find out ViewGroup
         * numbers nCount : All children include horizontal divider between
         * ViewGroup Global mSuiteCount : Include INVISIBLE & VISIBLE ViewGroup
         * Global mVisibleSuiteCount : Only VISIBLE ViewGroup
         */
        final int nCount = mRoot.getChildCount();
        int nSuiteIndex = 0;
        for (int i = 0; i < nCount; i++) {
            View v = mRoot.getChildAt(i);
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                if ((nSuiteIndex == nIndex) && (vg.getVisibility() != nVisibility)) {
                    vg.setVisibility(nVisibility);
                    /*
                     * To identify current View's visibility and update
                     * mVisibleSuiteCount CurrentView=VISIBLE, set to INVISIBLE,
                     * then mVisibleSuiteCount-- CurrentView=INVISIBLE/GONE set
                     * to VISIBLE, then mVisibleSuiteCount++
                     */
                    switch (nVisibility) {
                    case View.VISIBLE:
                        mVisibleSuiteCount++;
                        break;
                    case View.INVISIBLE:
                        mVisibleSuiteCount--;
                        break;
                    case View.GONE:
                        mVisibleSuiteCount--;
                        break;
                    default:
                        if (HtcBuildFlag.Htc_DEBUG_flag || Log.isLoggable("HtcMultiSeekBarDialog", Log.DEBUG))
                            Log.d("HtcMultiSeekBarDialog", "Exception on setVisibility state nSuiteIndex=" + nSuiteIndex + ",nVisibility=" + nVisibility);
                        break;
                    }
                    break;
                }
                nSuiteIndex++;
            }
        }
        /*
         * TODO : Could refactoring at next sense and combine with upon for loop
         * The loop to handle visibility of horizontal divider between ViewGroup
         * nCount : Include horizontal divider between ViewGroup Handle at below
         * condition: vPrev=ViewGroup vCenter=(View)Horizontal Divider
         * vNext=ViewGroup
         */
        for (int i = 1; i < nCount - 1; i++) {
            View vPrev = mRoot.getChildAt(i - 1);
            View vCenter = mRoot.getChildAt(i);
            View vNext = mRoot.getChildAt(i + 1);
            if (vPrev instanceof ViewGroup && (!(vCenter instanceof ViewGroup)) && vNext instanceof ViewGroup) {
                ViewGroup vgPrev = (ViewGroup) vPrev;
                ViewGroup vgNext = (ViewGroup) vNext;
                if (View.VISIBLE == vgPrev.getVisibility() && View.VISIBLE == vgNext.getVisibility()) {
                    vCenter.setVisibility(View.VISIBLE);
                } else {
                    vCenter.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * The method to allow user can set their OnClickListener to listen the
     * click event on the root panel
     *
     * @see android.app.Dialog#show() Start the dialog and display it on screen
     */
    private void setRootClickListener(android.view.View.OnClickListener ocl) {
        // TODO Auto-generated method stub
        if (null != mRoot) {
            mRoot.setOnClickListener(ocl);
        }
    }

    /**
     * The method to allow user set their OnClickListener to listen the click
     * event on the nIndex-suite panel
     *
     * @see android.app.Dialog#show() Start the dialog and display it on screen
     * @param nIndex
     *            To listener the nIndex Suite click event
     * @param ocl
     *            The specific click for the nIndex Suite
     */
    public void setSuiteClickListener(int nIndex, android.view.View.OnClickListener ocl) {
        // TODO Auto-generated method stub
        ViewGroup vg = findViewGroupById(nIndex);
        if (null != vg) {
            vg.setOnClickListener(ocl);
        }
    }

    private WindowManager.LayoutParams getWindowLayoutParams() {
        return getWindow().getAttributes();
    }

    private void setWindowLayoutParams(WindowManager.LayoutParams wmlp) {
        getWindow().setAttributes(wmlp);
    }

    /**
     * The method to show the dialog on specific gravity
     *
     * @see android.app.Dialog#show() Start the dialog and display it on screen
     * @param gravity
     *            To set specific gravity for the dialog
     */
    public void showByGravity(int gravity) {
        // TODO Auto-generated method stub
        WindowManager.LayoutParams wmlp = getWindowLayoutParams();
        if (null != wmlp) {
            wmlp.gravity = gravity;
            setWindowLayoutParams(wmlp);
        }
        show();
    }

    /**
     * The method to setDialogLayot topMargin, bottomMargin, PaddingTop,
     * PaddingBottom
     */
    private void setDialogLayout() {
        Window w = getWindow();
        if (null == w)
            return;

        Context c = getContext();
        if (null == c)
            return;

        Resources r = c.getResources();
        if (null == r)
            return;

        int id = r.getIdentifier("parentPanel", "id", "android");
        View v = w.findViewById(id);
        if (0 != id && null != v) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v.getLayoutParams();
            lp.setMargins(0, lp.topMargin, 0, lp.bottomMargin);
        }

        id = r.getIdentifier("customPanel", "id", "android");
        v = w.findViewById(id);
        if (0 != id && null != v) {
            v.setPadding(0, v.getPaddingTop(), 0, v.getPaddingBottom());
            //remove background of customPanel
            v.setBackgroundResource(android.R.color.transparent);
        }
    }

    /**
     * The method to measure dialog width and margin/padding defined by UIGL To
     * keep the same panel width on both portrait and landscape mode
     */
    private void setDialogWindow() {
        DisplayMetrics dm = new DisplayMetrics();
        Window w = getWindow();
        w.getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams wmlp = getWindowLayoutParams();

        // U16 Bill_lin By design, no matter Portrait/Landscape mode, always get short length to count margin +[
        wmlp.width = (int) (Math.min(dm.heightPixels, dm.widthPixels) - 2 * mMarginM4);
        // +] U16 Bill_lin By design, no matter Portrait/Landscape mode, always get short length to count margin +[
        setWindowLayoutParams(wmlp);
        setDialogLayout();
    }

    /**
     * The method to setup Icon width to be 16% of the portrait screen width
     * Remove the Dialog dim mode via clear FLAG_DIM_BEHIND
     */
    private void customize() {
        if (-1 == mPortraitIconWidth) {
            DisplayMetrics dm = new DisplayMetrics();
            Window w = getWindow();
            w.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            w.getWindowManager().getDefaultDisplay().getMetrics(dm);
            mPortraitIconWidth = ((dm.widthPixels < dm.heightPixels) ? dm.widthPixels : dm.heightPixels) / 6;
        }

        //Get current localization config from HtcResUtil
        final boolean mAllCaps = HtcResUtil.isInAllCapsLocale(getContext());
        final int nCount = mRoot.getChildCount();
        for (int i = 0; i < nCount; i++) {
            View v = mRoot.getChildAt(i);
            SeekBar mSeek = null;
            TextView mTextTitle = null;
            if (v instanceof ViewGroup && View.VISIBLE == v.getVisibility()) {
                ViewGroup vg = (ViewGroup) v;
                View vMod = vg.findViewById(R.id.icon);
                mSeek = (SeekBar) vg.findViewById(R.id.seekbar);
                mTextTitle = (TextView) vg.findViewById(R.id.text);
                //Designer define TextView to Upper Case
                if (null != mTextTitle && mAllCaps)
                    mTextTitle.setAllCaps(mAllCaps);
                //Designer define Icon width to be 16% of the portrait screen width
                if (null == vMod)
                    continue;

                LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) vMod.getLayoutParams();
                llp.width = mPortraitIconWidth;
                vMod.setLayoutParams(llp);

                AccessibilityManager accessibilityManager = (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
                if (accessibilityManager.isEnabled()) {
                    if (null != mSeek && null != mTextTitle) {
                        mTextTitle.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
                        mSeek.setTag(mTextTitle.getText());
                        mSeek.setAccessibilityDelegate((null != mAccessibilityDelegate) ? mAccessibilityDelegate : new MultiSeekBarDialogAccessibilityDelegate());
                    }
                }
            }
        }

        mRoot.requestLayout();

    }

    /**
     * The method to show Dialog Before show dialog, need to setup Icon width to
     * be 16% of the portrait screen width
     *
     * @see android.app.Dialog#show() Start the dialog and display it on screen
     */
    @Override
    public void show() {
        customize();
        super.show();
    }

    class MultiSeekBarDialogAccessibilityDelegate extends AccessibilityDelegate {
        /**
         * An AccessibilityEvent to the host View first and then to its children
         * for adding their text content to the event.
         *
         * @see android.view.View.AccessibilityDelegate#onPopulateAccessibilityEvent(android.view.View,
         *      android.view.accessibility.AccessibilityEvent)
         * @param host
         *            The View hosting the delegate.
         * @param AccessibilityEvent
         *            The event.
         * @hide
         */
        @Override
        public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
            SeekBar mSeek = (SeekBar) ((host instanceof SeekBar) ? host : null);
            if ((null != mSeek) && ((event.getEventType() & AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) != 0)) {
                String sTip = String.format("%s,%s", host.getTag().toString(), getContext().getResources().getString(R.string.ls_doubletap_drag));
                host.announceForAccessibility(sTip);
            }
            super.onPopulateAccessibilityEvent(host, event);
        }

        /**
         * Dispatches an AccessibilityEvent to the host View first and then to
         * its children for adding their text content to the event.
         *
         * @see android.view.View.AccessibilityDelegate#dispatchPopulateAccessibilityEvent(android.view.View,
         *      android.view.accessibility.AccessibilityEvent)
         * @param host
         *            The View hosting the delegate.
         * @param AccessibilityEvent
         *            The event.
         * @hide
         */
        @Override
        public boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
            // TODO Auto-generated method stub
            boolean result = super.dispatchPopulateAccessibilityEvent(host, event);
            SeekBar mSeek = (SeekBar) ((host instanceof SeekBar) ? host : null);
            if (null != mSeek && ((event.getEventType() & AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) != 0)) {
                String sCurrentProgress = "";
                int iProgressCurrent = mSeek.getProgress();
                sCurrentProgress = String.format("%s%s", Integer.toString(iProgressCurrent), "%,");
                event.getText().add(sCurrentProgress);
            }
            return result;
        }
    }
}
