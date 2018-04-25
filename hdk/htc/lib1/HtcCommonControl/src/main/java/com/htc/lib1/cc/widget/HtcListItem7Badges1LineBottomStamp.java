package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.util.res.HtcResUtil;

/**
 * The component looks as the following:<br>
 * <pre class="prettyprint">
 * ---------------------
 * |   [I] [I] [I] [T] | <- setBadgeState(int index, boolean enable);
 * |           5:34 PM | <- setTextStamp(String text); setStampTextStyle(int defStyle);
 * ---------------------
 * </pre>
 */

/**
 * the last badge is a ToggleButton, others are ImageViews
 */
public class HtcListItem7Badges1LineBottomStamp extends View implements IHtcListItemComponent {
    private static final String TAG = HtcListItem7Badges1LineBottomStamp.class.getName();
    // TODO make private if you can make sure no one is using this
    /** only those ImageViews */
    static final int MAX_BADGES = 6;
    /** 6 ImageViews, 1 ToggleButton, 1 HtcFlagButton */
    /**
     * @deprecated [Alternative Solution] Please use FLAG_BUTTON_INDEX,
     *             BUBBLE_COUNT_INDEX, MIN_BADGE_INDEX, and MIN_BADGE_INDEX
     *             directly.
     **/
    /** @hide */
    public static final int MAX_BADGE_COUNTS = 7;

    private static final int TOTAL_BADGE_COUNTS = 8;

    /**
     * FLAG_BUTTON_INDEX indicate the index of flag button in
     * HtcListItem7Badges1LineBottomStamp
     */
    public static final int FLAG_BUTTON_INDEX = 6;
    /**
     * BUBBLE_COUNT_INDEX indicate the index of bubble count in
     * HtcListItem7Badges1LineBottomStamp
     */
    public static final int BUBBLE_COUNT_INDEX = 7;
    /**
     * MIN_BADGE_INDEX indicate minimum index of Badge in
     * HtcListItem7Badges1LineBottomStamp
     */
    public static final int MIN_BADGE_INDEX = 0;
    /**
     * MAX_BADGE_INDEX indicate maximum index of Badge in
     * HtcListItem7Badges1LineBottomStamp
     */
    public static final int MAX_BADGE_INDEX = 5;
    private Drawable mBadges[] = null;
    private boolean[] bBadgeState = new boolean[TOTAL_BADGE_COUNTS];

    @ExportedProperty(category = "CommonControl")
    private int mBadgeGap;
    private static Drawable fgOn = null;
    private static Drawable bkgRest = null;

    @ExportedProperty(category = "CommonControl")
    private String mStamp;
    private TextPaint mStampPaint = new TextPaint();
    @ExportedProperty(category = "CommonControl")
    private String mText;
    private TextPaint mBubblePaint = new TextPaint();
    private FontMetrics mBubbleFontMetrics = null;
    @ExportedProperty(category = "CommonControl")
    private float mBubbleFontHeight = 0;

    @ExportedProperty(category = "CommonControl")
    private boolean mDownEvent;
    @ExportedProperty(category = "CommonControl")
    private boolean mIsFlagOn = false;
    @ExportedProperty(category = "CommonControl")
    private boolean mIsFlagClickable = true;
    @ExportedProperty(category = "CommonControl")
    private int mStampVisibility = View.GONE;

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public final static int MODE_WHITE_LIST = 0;
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public final static int MODE_DARK_LIST = 1;
    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = MODE_WHITE_LIST, to = "MODE_WHITE_LIST"),
            @IntToString(from = MODE_DARK_LIST, to = "MODE_DARK_LIST")
    })
    private int mMode = 0;
    private HtcListItemManager mHtcListItemManager;

    @ExportedProperty(category = "CommonControl")
    boolean mIsAutomotiveMode = false;
    @ExportedProperty(category = "CommonControl")
    boolean mM2Enable=false;
    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    com.htc.lib1.cc.R.styleable.HtcListItemTextComponentMode);
            mMode = a.getInt(R.styleable.HtcListItemTextComponentMode_textMode, MODE_WHITE_LIST);
            a.recycle();
        } else {
            mMode = MODE_WHITE_LIST;
        }
        init(context);
    }

    private void initDrawable(Context context, AttributeSet attrs, int defStyle) {
        bkgRest = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle,
                HtcButtonUtil.BTNASSET_COMMON_FLAG_REST);
        fgOn = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle,
                HtcButtonUtil.BTNASSET_COMMON_FLAG_ON);
    }

    private void init(Context context) {
        mHtcListItemManager = HtcListItemManager.getInstance(context);

        mBadges = new Drawable[MAX_BADGES];
        mBadgeGap = HtcListItemManager.getM4(context);

        HtcResUtil.setTextAppearance(context, com.htc.lib1.cc.R.style.fixed_list_secondary,
                mStampPaint);
        mStampPaint.setTextAlign(Paint.Align.RIGHT);
        mStampPaint.setAntiAlias(true);

        HtcResUtil.setTextAppearance(context,
                com.htc.lib1.cc.R.style.fixed_notification_info_m, mBubblePaint);
        mBubblePaint.setTextAlign(Paint.Align.RIGHT);
        mBubblePaint.setAntiAlias(true);
        mBubbleFontMetrics = mBubblePaint.getFontMetrics();
        mBubbleFontHeight = mBubbleFontMetrics.bottom - mBubbleFontMetrics.top;
        mBubblePaint.setColor(HtcCommonUtil.getCommonThemeColor(context, com.htc.lib1.cc.R.styleable.ThemeColor_light_category_color));
    }

    /**
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public static void setTextPaintStyle(Context context, TextPaint p, int styleId) {
    }

    /**
     * Use this API to set indicator with index is 0 to 5 from left to right.
     *
     * @param index Indicator index
     * @param resId Resource id
     */
    public void setBadgeImageResource(int index, int resId) {
        if (index >= 0 && index < MAX_BADGES) {
            mBadges[index] = getContext().getResources().getDrawable(resId);
            bBadgeState[index] = false;
        } else {
            Log.d(TAG, "setBadgeImageResource: index out of bound.");
        }
    }

    /**
     * Simple constructor to use when creating this widget from code. It will
     * new a view with default text style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     */
    public HtcListItem7Badges1LineBottomStamp(Context context) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        initDrawable(context, null, 0);
        init(context);
    }

    /**
     * Constructor that is called when inflating this widget from XML. This is
     * called when a view is being constructed from an XML file, supplying
     * attributes that were specified in the XML file.It will new a view with
     * default text style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItem7Badges1LineBottomStamp(Context context, AttributeSet attrs) {
        super(context, attrs);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        initDrawable(context, attrs, 0);
        init(context, attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of this widget allows subclasses to use their own base style
     * when they are inflating. It will new a view with default text style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @param defStyle The default style to apply to this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItem7Badges1LineBottomStamp(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        initDrawable(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     */
    public HtcListItem7Badges1LineBottomStamp(Context context, int mode) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        if (mode >= MODE_WHITE_LIST && mode <= MODE_DARK_LIST) {
            mMode = mode;
        } else {
            mMode = MODE_WHITE_LIST;
        }
        init(context);
    }

    @ExportedProperty(category = "CommonControl")
    private int mStampWidth = 0;
    @ExportedProperty(category = "CommonControl")
    private int mBadgesTotalWidth = 0;
    @ExportedProperty(category = "CommonControl")
    private int mBubbleWidth = 0;

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    protected void onMeasure(int w, int h) {
        // note this will measure all the children.
        super.onMeasure(w, h);
        int badgesTotalWidth = 0;

        // all 6 badges
        for (int i = 0; i < MAX_BADGES; i++) {
            if (getBadgeState(i)) {
                badgesTotalWidth += mBadges[i].getIntrinsicWidth() + mBadgeGap;
            }
        }

        // bubble count
        if (getBadgeState(BUBBLE_COUNT_INDEX)) {
            if (mText != null) {
                badgesTotalWidth += mBubbleWidth + mBadgeGap;
            }
        }

        // new flag button
        if (getBadgeState(FLAG_BUTTON_INDEX)) {
            badgesTotalWidth += fgOn.getIntrinsicWidth() + mBadgeGap;
        }

        // remove the last badge's right margin if there is at least 1 badge.
        if (badgesTotalWidth != 0) {
            badgesTotalWidth -= mBadgeGap;
        }
        // stamp
        setMeasuredDimension(resolveSize(Math.max(badgesTotalWidth, mStampWidth), w),
                mHtcListItemManager.getDesiredListItemHeight(mItemMode));

        mBadgesTotalWidth = badgesTotalWidth;
    }

    private int cFlagTouchLeft = 0, cFlagTouchTop = 0, cFlagTouchRight = 0, cFlagTouchBottom = 0;

    /**
     * {@inheritDoc}
     *
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void onDraw(Canvas canvas) {
        final boolean isLayoutRtl = (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) ? getLayoutDirection() == View.LAYOUT_DIRECTION_RTL : false;
        int rightMargin = getWidth() - Math.max(mBadgesTotalWidth, mStampWidth) - (mM2Enable ? HtcListItemManager.getM2(getContext()) : 0);
        int cLeft = 0, cTop = 0, cRight = 0;
        int cFlagLeft = 0, cFlagTop = 0, cFlagRight = 0, cFlagBottom = 0;
        int center = getHeight() / 2;
        cRight = getWidth() - rightMargin;
        cLeft = isLayoutRtl ? rightMargin : 0;

        if (getBadgeState(FLAG_BUTTON_INDEX)) {
            cLeft = isLayoutRtl ? cLeft : (cRight - fgOn.getIntrinsicWidth());
            cRight = isLayoutRtl ? (cLeft + fgOn.getIntrinsicWidth()) : cRight;
            cFlagRight = cRight;
            cFlagLeft = cLeft;
            if (bBadgeVirticalCenter)
                cFlagTop = cTop = center - fgOn.getIntrinsicHeight() / 2;
            else
                cFlagTop = cTop = (int) (mHtcListItemManager.getDesiredCenterFor7Badge(mItemMode) - 0.5f * fgOn
                        .getIntrinsicHeight());

            cFlagBottom = cTop + fgOn.getIntrinsicHeight();
            // //
            cFlagTouchLeft = cFlagLeft - fgOn.getIntrinsicWidth();
            cFlagTouchTop = cFlagTop - fgOn.getIntrinsicHeight();
            cFlagTouchRight = cFlagRight + HtcListItemManager.getM1(getContext());
            cFlagTouchBottom = cFlagBottom + fgOn.getIntrinsicHeight();
            // //
            canvas.drawBitmap(mIsFlagOn ? ((BitmapDrawable) fgOn).getBitmap()
                    : ((BitmapDrawable) bkgRest).getBitmap(), cLeft, cTop, null);
            // update right
            cRight = isLayoutRtl ? cRight : (cRight - fgOn.getIntrinsicWidth() - mBadgeGap);
            cLeft = isLayoutRtl ? (cLeft + fgOn.getIntrinsicWidth() + mBadgeGap) : cLeft;
        }

        // bubble count
        if (getBadgeState(BUBBLE_COUNT_INDEX)) {
            float textBaseY;
            if (bBadgeVirticalCenter)
                textBaseY = getHeight() - (getHeight() - mBubbleFontHeight) / 2
                        - mBubbleFontMetrics.bottom;
            else
                textBaseY = mHtcListItemManager.getPrimaryBaseLine(mMode) - mBubbleFontMetrics.bottom;
            int length = (mText == null ? 0 : mText.length());

            cLeft = isLayoutRtl ? cLeft : (cRight - mBubbleWidth);
            cRight = isLayoutRtl ? (cLeft + mBubbleWidth) : cRight;
            if (mText != null)
                canvas.drawText(mText, 0, length, cRight, textBaseY, mBubblePaint);

            cRight = isLayoutRtl ? cRight : (cRight - mBubbleWidth - mBadgeGap);
            cLeft = isLayoutRtl ? (cLeft + mBubbleWidth + mBadgeGap) : cLeft;
        }

        for (int i = MAX_BADGES - 1; i >= 0; i--) {
            if (getBadgeState(i)) {
                if (bBadgeVirticalCenter)
                    cTop = center - mBadges[i].getIntrinsicWidth() / 2;
                else
                    cTop = (int) (mHtcListItemManager.getDesiredCenterFor7Badge(mItemMode) - 0.5f * mBadges[i]
                            .getIntrinsicHeight());

                cLeft = isLayoutRtl ? cLeft : (cRight - mBadges[i].getIntrinsicWidth());

                if (mBadges[i] != null) {
                    canvas.drawBitmap(((BitmapDrawable) mBadges[i]).getBitmap(), cLeft, cTop, null);
                    // update right
                    cRight = isLayoutRtl ? cRight
                            : (cRight - mBadges[i].getIntrinsicWidth() - mBadgeGap);
                    cLeft = isLayoutRtl ? (cLeft + mBadges[i].getIntrinsicWidth() + mBadgeGap)
                            : cLeft;
                }
            }
        }

        if (!bBadgeVirticalCenter && getStampVisibility() == View.VISIBLE) {
            float textBaseY;
            textBaseY = mHtcListItemManager.getSecondaryBaseLine(mItemMode);

            int textBaseX = isLayoutRtl ? (rightMargin + mStampWidth)
                    : (getWidth() - rightMargin);
            if (mStamp != null)
                canvas.drawText(mStamp, 0, mStamp.length(), textBaseX, textBaseY, mStampPaint);
        }
    }

    /**
     * if false, views will be disabled and alpha value will be set to 0.4
     *
     * @hide
     */
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled)
            return;
        super.setEnabled(enabled);
        HtcListItemManager.setViewOpacity(this, enabled);
    }

    /**
     * Set whether to show the small icon or not
     *
     * @param index The index of those small icons which is 0-6. (from left to
     *            right)
     * @param enable Whether to show the icon or not
     */
    public void setBadgeState(int index, boolean enable) {
        if (index >= 0 && index < MAX_BADGES) {
            if (mBadges[index] != null && bBadgeState[index] != enable) {
                bBadgeState[index] = enable;
                requestLayout();
                invalidate();
            }
        } else if (index == FLAG_BUTTON_INDEX) {
            if (bBadgeState[index] != enable) {
                bBadgeState[index] = enable;
                requestLayout();
                invalidate();
            }
        }
    }

    /**
     * get the state of badge
     *
     * @param index which badge [0-6]
     * @return
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public boolean getBadgeState(int index) {
        if (index >= 0 && index < MAX_BADGES) {
            if (mBadges[index] != null)
                return bBadgeState[index];
            else
                return false;
        } else if (index == BUBBLE_COUNT_INDEX) {
            return bBadgeState[index];
        } else if (index == FLAG_BUTTON_INDEX) {
            return bBadgeState[index];
        }
        return false;
    }

    /**
     * get the instance of the small badges
     *
     * @param index
     * @return
     * @deprecated [Not use any longer] This method will no longer be supported
     *             in Sense 5.0 due to design change
     */
    /** @hide */
    public View getBadge(int index) {
        return null;
    }

    /**
     * Set the text of the text stamp
     *
     * @param text the string of text stamp
     */
    public void setTextStamp(String text) {
        String oldText = mStamp;
        mStamp = text == null ? "" : text;
        if (!mStamp.equals(oldText)) {
            setStampVisibility("".equals(mStamp) ? View.GONE : View.VISIBLE);
            mStampWidth = (int) mStampPaint.measureText(mStamp);
            requestLayout();
            invalidate();
        }
    }

    /**
     * Set the text of the text stamp
     *
     * @param rId resource ID of the displayed string
     */
    public void setTextStamp(int rId) {
        String text = getContext().getResources().getString(rId);
        setTextStamp(text);
    }

    /**
     * Get the text of the text stamp
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public String getTextStamp() {
        return mStamp;
    }

    /**
     * The text style of the stamp. Only for HTC defined style.
     *
     * @param defStyle The font size will be changed
     * @deprecated [Not use any longer] This method will no longer be supported
     *             in Sense 5.0 due to design change
     */
    /** @hide */
    public void setStampTextStyle(int defStyle) {
    }

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = HtcListItem.MODE_DEFAULT, to = "MODE_DEFAULT"),
            @IntToString(from = HtcListItem.MODE_CUSTOMIZED, to = "MODE_CUSTOMIZED"),
            @IntToString(from = HtcListItem.MODE_KEEP_MEDIUM_HEIGHT, to = "MODE_KEEP_MEDIUM_HEIGHT"),
            @IntToString(from = HtcListItem.MODE_AUTOMOTIVE, to = "MODE_AUTOMOTIVE"),
            @IntToString(from = HtcListItem.MODE_POPUPMENU, to = "MODE_POPUPMENU")
    })
    int mItemMode = HtcListItem.MODE_DEFAULT;

    /**
     * {@hide}
     *
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void notifyItemMode(int itemMode) {
        mItemMode = itemMode;
        mIsAutomotiveMode = (mItemMode == HtcListItem.MODE_AUTOMOTIVE) ? true : false;
    }

    /**
     * @deprecated [Not use any longer] This method will no longer be supported
     *             in Sense 5.0 due to design change
     */
    /** @hide */
    public int[] getStampCoordinatesInfo() {
        int[] info = new int[7];
        return info;
    }

    private static final int TOUCH_SLOP = 24;
    private static int sScaledTouchSlop = -1;

    private void initializeSlop(Context context) {
        if (sScaledTouchSlop == -1) {
            final Resources res = context.getResources();
            final Configuration config = res.getConfiguration();
            final float density = res.getDisplayMetrics().density;
            final float sizeAndDensity;
            if (config.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_XLARGE)) {
                sizeAndDensity = density * 1.5f;
            } else {
                sizeAndDensity = density;
            }
            sScaledTouchSlop = (int) (sizeAndDensity * TOUCH_SLOP + 0.5f);
        }
    }

    /**
     * Overriding this method allows us to "catch" clicks in the checkbox or
     * star and process them accordingly.
     *
     * @param event The motion event.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initializeSlop(getContext());

        boolean handled = false;
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isEnabled() && mIsFlagClickable && touchX >= cFlagTouchLeft
                        && touchX <= cFlagTouchRight && touchY >= cFlagTouchTop
                        && touchY <= cFlagTouchBottom) {
                    mDownEvent = true;
                    handled = true;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                mDownEvent = false;
                break;

            case MotionEvent.ACTION_UP:
                if (mDownEvent) {
                    if (isEnabled() && mIsFlagClickable && touchX >= cFlagTouchLeft
                            && touchX <= cFlagTouchRight && touchY >= cFlagTouchTop
                            && touchY <= cFlagTouchBottom) {
                        setFlagButtonChecked(!mIsFlagOn);
                        handled = true;
                    }
                }
                break;
        }

        if (handled) {
            invalidate();
        } else if (hasOnClickListeners()) {
            handled = super.onTouchEvent(event);
        }
        return handled;
    }

    /**
     * get if the flag button checked
     *
     * @return if the flag button checked
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public boolean isFlagButtonChecked() {
        return mIsFlagOn;
    }

    /**
     * <p>
     * Changes the checked state of this button.
     * </p>
     *
     * @param checked true to check the button, false to uncheck it
     */
    public void setFlagButtonChecked(boolean checked) {
        if (mIsFlagOn != checked) {
            mIsFlagOn = checked;

            if (mPerformFlagButtonCheck == null) {
                mPerformFlagButtonCheck = new PerformFlagButtonCheck();
            }
            if (!post(mPerformFlagButtonCheck)) {
                performFlagButtonClick();
            }
        }
    }

    /**
     * Enables or disables click events for the flag button.
     *
     * @param clickable true to enable click events for the flag button, false
     *            to disable it.
     */
    public void setFlagButtonClickable(boolean clickable) {
        if (mIsFlagClickable != clickable)
            mIsFlagClickable = clickable;
    }

    /**
     * Listener used to dispatch check events.
     */
    private OnFlagButtonCheckedChangeListener mFlagButtonOnCheckedChangeListener;

    /**
     * Register a callback to be invoked when this view is checked. If this view
     * is not clickable, it becomes clickable.
     *
     * @param l The callback that will run
     * @see #setClickable(boolean)
     */
    public void setFlagButtonOnCheckedChangeListener(OnFlagButtonCheckedChangeListener l) {
        if (!isClickable()) {
            setClickable(true);
        }
        mFlagButtonOnCheckedChangeListener = l;
    }

    /**
     * Return whether this view has an attached OnFlagButtonCheckChangeListener.
     * Returns true if there is a listener, false if there is none.
     *
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public boolean hasFlagButtonOnCheckedChangeListeners() {
        return (mFlagButtonOnCheckedChangeListener != null);
    }

    private PerformFlagButtonCheck mPerformFlagButtonCheck;

    /**
     * Call this view's OnClickListener, if it is defined. Performs all normal
     * actions associated with clicking: reporting accessibility event, playing
     * a sound, etc.
     */
    private void performFlagButtonClick() {
        if (mFlagButtonOnCheckedChangeListener != null) {
            mFlagButtonOnCheckedChangeListener.onCheckedChanged(this, mIsFlagOn);
        }
    }

    private final class PerformFlagButtonCheck implements Runnable {
        public void run() {
            performFlagButtonClick();
        }
    }

    /**
     * Interface definition for a callback to be invoked when a view is checked.
     */
    public interface OnFlagButtonCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param view The view of flag button whose state has changed.
         * @param isChecked The new checked state of view of flag button.
         */
        void onCheckedChanged(HtcListItem7Badges1LineBottomStamp view, boolean isChecked);
    }

    /**
     * set the visibility of the stamp
     *
     * @param v The visibility of the stamp
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setStampVisibility(int v) {
        mStampVisibility = v;
    }

    /**
     * get the visibility of the stamp
     *
     * @return The visibility of the stamp
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public int getStampVisibility() {
        return mStampVisibility;
    }

    /**
     * This API is package level and used by HtcListItemMessageBody.
     */
    int getMeasuredStampWidth() {
        return mStampWidth;
    }

    /**
     * This API is package level and used by HtcListItemMessageBody.
     */
    int getMeasuredBadgesWidth() {
        return mBadgesTotalWidth;
    }

    @ExportedProperty(category = "CommonControl")
    private boolean bBadgeVirticalCenter = false;

    /**
     * Use this API to set all badges vertically centered in list item.
     *
     * @param isVerticalCenter Is badge vertical center
     */
    public void setBadgesVerticalCenter(boolean isVerticalCenter) {
        bBadgeVirticalCenter = isVerticalCenter;
    }

    boolean isBadgesVerticalCenter() {
        return bBadgeVirticalCenter;
    }

    private static final int MAX_BOUND = 1000;
    private int mUpperBound = MAX_BOUND;

    /**
     * Use this API to set an integer in parenthesis
     *
     * @param count an integer to set in parenthesis
     */
    public void setBubbleCount(int count) {
        String oldBubbleCount = mText;
        if (count <= 0) {
            mText = "";
        } else if (count < mUpperBound) {
            mText = "(" + String.valueOf(count) + ")";
        } else {
            mText = "(" + String.valueOf(mUpperBound - 1) + "+)";
        }
        if (!mText.equals(oldBubbleCount)) {
            mBubbleWidth = (int) mBubblePaint.measureText(mText);
            bBadgeState[BUBBLE_COUNT_INDEX] = mText.equals("") ? false : true;
            requestLayout();
            invalidate();
        }
    }

    /**
     * Use this API to set upper bound of count
     *
     * @param upperBound The upper bound of count
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setUpperBound(int upperBound) {
        if (upperBound > 0 && upperBound < MAX_BOUND)
            mUpperBound = upperBound;
    }

    void setM2Enable(boolean enable) {
        if (mM2Enable != enable) {
            mM2Enable = enable;
        }
    }
}
