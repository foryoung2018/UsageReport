package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewDebug.ExportedProperty;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ProgressBar;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.view.util.HtcProgressBarUtil;

/**
 * HtcProgressBar extends Google ProgressBar.
 * @see <a href="http://developer.android.com/reference/android/widget/ProgressBar.html">ProgressBar</a>
 */
public class HtcProgressBar extends ProgressBar {

    /**
     * Display mode for HtcProgressBar, default value is 0.
     */
    public static final int DISPLAY_MODE_DEFAULT = 0x00;

    /**
     * Display mode for HtcProgressBar, white mode is the same as default.
     */
    public static final int DISPLAY_MODE_WHITE = DISPLAY_MODE_DEFAULT ;

    /**
     * Display mode for HtcProgressBar, black mode value is 1.
     */
    public static final int DISPLAY_MODE_BLACK = 0x01;

    /**
     * Full mode for HtcProgressBar, full mode value is 2.
     * used by SetupWizard.
     * @hide
     * @deprecated [Module internal use]
     */
    @Deprecated
    public static final int DISPLAY_MODE_FULL = 0x02;
    @ExportedProperty(category = "CommonControl")
    private int mProgressHeight;
    @ExportedProperty(category = "CommonControl")
    private boolean mActionUp = false;

    /**
     * Create a new HtcprogressBar and initial progress of 0.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcProgressBar(Context context) {
        this(context, null);
    }

    /**
     * Create a new HtcprogressBar and initial progress of 0.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs AttributeSet
     */
    public HtcProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, com.htc.lib1.cc.R.attr.htcProgressBarStyle);
    }

    /**
     * Create a new HtcprogressBar and initial progress of 0.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs AttributeSet
     * @param defStyle default style for HtcProgressBar
     */
    public HtcProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setIndeterminate(false);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HtcProgressBar, defStyle, com.htc.lib1.cc.R.style.HTCProgressBarStyle);
        final int nDisplayMode = a.getInt(R.styleable.HtcProgressBar_displayMode, DISPLAY_MODE_DEFAULT);
        a.recycle();

        mProgressHeight = getContext().getResources().getDimensionPixelOffset(com.htc.lib1.cc.R.dimen.htc_progressbar_height);

        HtcProgressBarUtil.setProgressBarMode(context, this, nDisplayMode);
        /** force the progress and secondaryprogress to update the drawable level */
        int nTmp = getProgress();
        setProgress(0);
        setProgress(nTmp);

        nTmp = getSecondaryProgress();
        setSecondaryProgress(0);
        setSecondaryProgress(nTmp);
    }

    /** After super class onSizeChange, HtcSeekBar needs to compute each boundary of each drawable.
     * @param arg0 Current width of HtcProgressBar
     * @param arg1 Current height of HtcProgressBar
     * @param arg2 Old width of HtcProgressBar
     * @param arg3 Old height of HtcProgressBar
     * @see android.widget.AbsSeekBar#onSizeChanged(int, int, int, int)
     * @deprecated [Module internal use]
     */
    /**@hide*/
    @Override
    protected void onSizeChanged(int arg0, int arg1, int arg2, int arg3) {
        super.onSizeChanged(arg0, arg1, arg2, arg3);

        Drawable d = getProgressDrawable();
        if ( ! (d instanceof LayerDrawable) ) {
            return ;
        }

        LayerDrawable ld = (LayerDrawable) d;
        int nDH = mProgressHeight;
        int nVH = getHeight();
        Rect r_ld = ld.getBounds();
        Rect r_background = new Rect();
        r_background.right = r_ld.right;
        r_background.left = r_ld.left;
        r_background.top = (nVH - getPaddingTop() - getPaddingBottom() -nDH) / 2;
        r_background.bottom = (nVH - getPaddingTop() - getPaddingBottom() + nDH ) / 2;
        d = ld.findDrawableByLayerId(android.R.id.background);
        if (null != d) d.setBounds(r_background);
        d = ld.findDrawableByLayerId(android.R.id.secondaryProgress);
        if (null != d) d.setBounds(r_background);
        d = ld.findDrawableByLayerId(android.R.id.progress);
        if (null != d) d.setBounds(r_background);
    }

    /** Implement this method to handle touch screen motion events.
     * @param event The motion event.
     * @deprecated [Module internal use]
     */
    /**@hide*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean b = super.onTouchEvent(event);
        if (!isEnabled())
            return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;

            case MotionEvent.ACTION_UP:
                mActionUp = true;
                int progress = (getMax() > 0) ? (int) (getProgress() * 100 / getMax()) : 0;
                announceForAccessibility(progress + "%");
                return true;
        }
        return b;
    }

    /** Initializes an AccessibilityEvent with information about this View which is the event source.
     * @param event The event to initialize.
     * @deprecated [Module internal use]
     */
    /**@hide*/
    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (!mActionUp)
            event.getText().add("Progeress Bar");
        mActionUp = false;
    }

    /** Initializes an AccessibilityNodeInfo with information about this view.
     * @param event The event to initialize.
     * @deprecated [Module internal use]
     */
    /**@hide*/
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
    }

    /** Populate the accessibility event with its text content.
     * @param event The accessibility event which to populate.
     * @deprecated [Module internal use]
     */
    /**@hide*/
    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);
        if (getMax() > 0){
            int progress = (int) (getProgress() * 100 / getMax());
            event.getText().add(progress + "%");
        }
    }
}
