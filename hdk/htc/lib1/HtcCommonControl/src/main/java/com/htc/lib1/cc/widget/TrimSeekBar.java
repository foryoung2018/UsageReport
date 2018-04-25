
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewDebug.ExportedProperty;
import android.widget.SeekBar;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.graphic.MirrorDrawable;
import com.htc.lib1.cc.graphic.TrimIndicatorDrawable;

/**
 * To get a distance in SeekBar, have two indicator - start trim and end trim.
 * every trim like a thumb in SeekBar, also, View or PopupWindow can be used to show they Progress.
 *
 * @hide
 * @deprecated
 */
public class TrimSeekBar extends HtcSeekBar {

    private static final String TAG = "TrimSeekBar";

    // The min scale of distance between start trim and end trim
    private static final float SCALE_MIN_DISTANCE = 0.1f;

    private static final int LEVEL_MAX = 10000;

    // The width of Touch area
    private static final int TRIM_TOUCH_AREA_WIDTH = 87;

    // CallBack TrimSeekBar change listener
    @ExportedProperty(category = "CommonControl")
    private OnTrimChangeListener mOnTrimChangeListener;

    // Start  & End Trim drawable
    @ExportedProperty(category = "CommonControl", deepExport = true, prefix = "trim_")
    private TrimIndicatorDrawable mDrawableTrimStart, mDrawableTrimEnd;

    // To choose which Drawable is touch
    @ExportedProperty(category = "CommonControl")
    private Drawable mDrawableTrimCurrent;

    // The track layer of ProgressDrawable
    @ExportedProperty(category = "CommonControl")
    private Drawable mLayerBackground;

    // Touch x Progress
    @ExportedProperty(category = "CommonControl")
    private int mTouchProgress;

    //progressDrawable start point
    @ExportedProperty(category = "CommonControl")
    private int mStartProgress;

    // Start & End Trim Progress in TrimSeekBar
    @ExportedProperty(category = "CommonControl")
    private int mProgressTrimStart, mProgressTrimEnd;

    // Trim Intrinsic Width & Height
    @ExportedProperty(category = "CommonControl")
    private int mTrimWidth, mTrimHeight;

    // Start / End Trim Point in Window
    @ExportedProperty(category = "CommonControl")
    private Point mPointTrimStart, mPointTrimEnd;

    @ExportedProperty(category = "CommonControl")
    private int mScreenWidth;

    @ExportedProperty(category = "CommonControl")
    private int mMarginM3;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsStartTrim;

    @ExportedProperty(category = "CommonControl")
    private int[] mLocaInWin;

    /**
     * A callback that notifies clients when the trim has been changed.
     */
    public interface OnTrimChangeListener {

        /**
         * Notification that the user has started a touch gesture.
         * @param seekBar The TrimSeekBar in which the touch gesture began
         * @param isStartTrim which trim touched, default is start trim.
         */
        void onTrimStart(TrimSeekBar seekBar, boolean isStartTrim);

        /**
         * Notification that the user has finished a touch gesture.
         * @param seekBar The TrimSeekBar in which the touch gesture began
         * @param isStartTrim which trim touched, default is start trim.
         */
        void onTrimEnd(TrimSeekBar seekBar, boolean isStartTrim);

        /**
         * Notification that the trim has changed.
         * @param seekBar The TrimSeekBar which trim changed
         * @param startTrimProgress The progress of start trim
         * @param endTrimProgress The progress of end trim
         * @param startTrimPoint The point of start trim in window
         * @param endTrimPoint The point of end trim in window
         * @param fromUser True if the progress change was initiated by the user
         */
        void onTrimChanged(TrimSeekBar seekBar, int startTrimProgress, int endTrimProgress,
                Point startTrimPoint, Point endTrimPoint, boolean fromUser);
    }

    /**
     * Simple constructor to use when creating a view from code.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be belong to the subclass of ContextThemeWrapper.
     */
    public TrimSeekBar(Context context) {
        this(context, null);
    }

    /**
     * Simple constructor to use when creating a view from code.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be belong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     */
    public TrimSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, com.htc.lib1.cc.R.attr.htcSeekBarStyle);
    }

    /**
     * Simple constructor to use when creating a view from code.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be belong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view.
     */
    public TrimSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs,
                com.htc.lib1.cc.R.styleable.HtcSeekBar, defStyle, 0);
        int paddingLeft = a.getDimensionPixelSize(
                com.htc.lib1.cc.R.styleable.HtcSeekBar_android_paddingLeft, 0);
        int paddingRight = a.getDimensionPixelSize(
                com.htc.lib1.cc.R.styleable.HtcSeekBar_android_paddingRight, 0);
        a.recycle();
        Resources res = context.getResources();
        mScreenWidth = res.getDisplayMetrics().widthPixels;
        mMarginM3 = res.getDimensionPixelOffset(R.dimen.margin_s);
        Drawable drawable = res.getDrawable(R.drawable.seekbar_trim);

        mTrimWidth = drawable.getIntrinsicWidth();
        mTrimHeight = drawable.getIntrinsicHeight();

        setMinimumWidth(2 * mTrimWidth);
        setPadding(paddingLeft, getPaddingTop(), paddingRight, getPaddingBottom());
        setThumbVisible(false);
        setThumbOffset(0);
        initTrim(drawable);
    }

    /**
     * Sets a listener to receive notifications of changes to the TrimSeekBar's trim.
     * @param l The TrimSeekBar notification listener
     * @see SeekBar.OnSeekBarChangeListener
     */
    public void setOnTrimChangeListener(OnTrimChangeListener l) {
        mOnTrimChangeListener = l;
    }

    /**
     * Set the start Trim progress location
     * @param progress above 0 and below endTrim progress
     */
    public void setStartTrimProgress(int progress) {
        setStartTrimProgress(progress, false);
    }

    /**
     * Set the start Trim progress location
     * @param progress above 0 and below endTrim progress
     * @param fromUser True if the progress change was initiated by the user
     */
    private void setStartTrimProgress(int progress, boolean fromUser) {
        if (mProgressTrimStart == progress) {
            return;
        }
        if (progress >= 0 && progress < mProgressTrimEnd) {
            mProgressTrimStart = progress;
            mPointTrimStart = getTrimPoint(progress);
            updateTrimLevel();
            callOnTrimChanged(fromUser);
        } else {
            Log.e(TAG, "Start out of range, it should above 0 and below end trim progress",
                    new Exception());
        }
        invalidate();
    }

    /**
     * Set the End Trim Progress location
     * @param progress above startTrim progress and below max progress
     */
    public void setEndTrimProgress(int progress) {
        setEndTrimProgress(progress, false);
    }

    /**
     * Set the End Trim Progress location
     * @param progress above startTrim progress and below max progress
     * @param fromUser True if the progress change was initiated by the user.
     */
    private void setEndTrimProgress(int progress, boolean fromUser) {
        if (mProgressTrimEnd == progress) {
            return;
        }
        if (progress > mProgressTrimStart && progress <= getMax()) {
            mProgressTrimEnd = progress;
            mPointTrimEnd = getTrimPoint(progress);
            updateTrimLevel();
            callOnTrimChanged(fromUser);
        } else {
            Log.e(TAG,
                    "progress out of range, End trim progress MUST above MAX progress OR blow start trim progress",
                    new Exception());
        }
        invalidate();
    }

    /**
     * call onTrimChanged function when listener is not null
     * @param fromUser True if the progress change was initiated by the user.
     */
    private void callOnTrimChanged(boolean fromUser) {
        if (mOnTrimChangeListener != null) {
            mOnTrimChangeListener.onTrimChanged(this, mProgressTrimStart, mProgressTrimEnd,
                    mPointTrimStart, mPointTrimEnd, fromUser);
        }
    }

    /**
     * Set ProgressDrawable Start Point in which progress
     * @param progress above 0 and below TrimSeekBar progress
     */
    public void setStartProgress(int progress) {
        if (mStartProgress == progress) {
            return;
        }
        if (progress >= 0 && progress <= getMax()) {
            mStartProgress = progress;
        } else {
            Log.e(TAG,
                    "start progress out of range, start trim progress MUST above MAX progress OR blow start trim progress",
                    new Exception());
        }
        invalidate();
    }

    /**
     * Set trimSeekBar progress drawable.
     * @param draw the progress drawable (draw must be {#LayerDrawable})
     * @see Progress.setProgressDrawable
     */
    @Override
    public void setProgressDrawable(Drawable draw) {
        if (draw instanceof LayerDrawable) {
            mLayerBackground = ((LayerDrawable) draw)
                    .findDrawableByLayerId(android.R.id.background);
        } else {
            Log.e(TAG, "Progress Drawable is not LayerDrawable!", new Exception());
        }
        super.setProgressDrawable(draw);
    }

    /**
     * Get start trim progress
     * @return start trim progress
     */
    public int getStartTrimProgress() {
        return mProgressTrimStart;
    }

    /**
     * Get end trim progress
     * @return end trim progress
     */
    public int getEndTrimProgress() {
        return mProgressTrimEnd;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        getLocationInWindow(mLocaInWin);
        updateTrimBounds(w, h);
        updateTrimLevel();
        updateProgressDrawableBounds();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        width = Math.max(width, mTrimWidth * 2);
        width = Math.min(mScreenWidth, width);

        height = Math.max(height, mTrimHeight);
        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float touchX = event.getX();
                if (touchX > getWidth() - mTrimWidth) {
                    mProgressTrimEnd = getMax();
                } else if (touchX < mTrimWidth) {
                    mProgressTrimStart = 0;
                }
                mDrawableTrimCurrent = findTouchTrim(event.getX());

                if (null != mOnTrimChangeListener && null != mDrawableTrimCurrent) {
                    mOnTrimChangeListener.onTrimStart(this, mIsStartTrim);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mTouchProgress = getProgressFromOffset(event.getX());
                currentDrawableMove(mDrawableTrimCurrent, mTouchProgress);
                if (mOnTrimChangeListener != null && null != mDrawableTrimCurrent) {
                    mPointTrimStart = getTrimPoint(mProgressTrimStart);
                    mPointTrimEnd = getTrimPoint(mProgressTrimEnd);
                    mOnTrimChangeListener.onTrimChanged(this, mProgressTrimStart, mProgressTrimEnd,
                            mPointTrimStart, mPointTrimEnd, true);
                }

                break;
            case MotionEvent.ACTION_UP:
                mTouchProgress = getProgressFromOffset(event.getX());
                if (null == mDrawableTrimCurrent) {
                    touchProgressDrawable(mTouchProgress);
                } else {
                    if ((null != mOnTrimChangeListener)) {
                        mOnTrimChangeListener.onTrimEnd(this, mIsStartTrim);
                    }
                }
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mLayerBackground != null) {
            canvas.save();
            canvas.translate(getPaddingLeft(), 0);
            mLayerBackground.draw(canvas);
            canvas.restore();
        }

        canvas.save();
        int clipLeft = getOffsetFromProgress(mStartProgress);
        int clipRight = getWidth() - mTrimWidth - getPaddingRight();
        canvas.clipRect(clipLeft, 0, clipRight, getHeight());
        super.onDraw(canvas);
        canvas.restore();

        if (mDrawableTrimStart != null) {
            mDrawableTrimStart.draw(canvas);
        }

        if (mDrawableTrimEnd != null) {
            mDrawableTrimEnd.draw(canvas);
        }
    }

    private void initTrim(Drawable drawable) {
        mProgressTrimStart = 0;
        mProgressTrimEnd = getMax();
        MirrorDrawable mirror = new MirrorDrawable(drawable.getConstantState().newDrawable(),
                MirrorDrawable.MIRROR_HORIZONTAL);

        mDrawableTrimStart = new TrimIndicatorDrawable(drawable.getConstantState().newDrawable(),
                TrimIndicatorDrawable.ORIENTATION_HORIZONTAL);
        mDrawableTrimEnd = new TrimIndicatorDrawable(mirror,
                TrimIndicatorDrawable.ORIENTATION_HORIZONTAL);

        mPointTrimEnd = new Point();
        mPointTrimStart = new Point();
        mLocaInWin = new int[2];
    }

    /**
     * compute startTrim progress, endTrim progress and seekBar progress when MontionEvent.ACTION_MOVE.
     * @param currentDrawable which Trim touched
     * @param touchProgress the progress touch X Coordinate
     */
    private void currentDrawableMove(Drawable currentDrawable, int touchProgress) {
        if (currentDrawable == mDrawableTrimStart) {
            touchStartTrimDrawable(touchProgress);
        } else if (currentDrawable == mDrawableTrimEnd) {
            touchEndTrimDrawable(touchProgress);
        } else if (currentDrawable == null) {
            touchProgressDrawable(touchProgress);
        }
        invalidate();
    }

    /**
     * get the point of trim from trim progress
     * @param trimProgress
     * @return  the point of trim
     */
    private Point getTrimPoint(int trimProgress) {
        Point point = new Point();
        point.x = (int) (getOffsetFromProgress(trimProgress) + mLocaInWin[0]);
        point.y = mLocaInWin[1];
        return point;
    }

    /**
     * update startTrim progress when touch startTrim drawable
     * @param touchProgress
     */
    private void touchStartTrimDrawable(int touchProgress) {
        int startTrimProgressMax = (int) (mProgressTrimEnd - getMax() * SCALE_MIN_DISTANCE);
        if (touchProgress > startTrimProgressMax) {
            Log.e(TAG, "touch out of start trim range", new Exception());
            return;
        }
        mProgressTrimStart = touchProgress;
        updateTrimLevel();
    }

    /**
     * update endTrim progress when touch endTrim drawable
     * @param touchProgress
     */
    private void touchEndTrimDrawable(int touchProgress) {
        int endTrimProgressMin = mProgressTrimStart + getMax() / 10;
        if (touchProgress < endTrimProgressMin) {
            Log.e(TAG, "touch out of end trim range", new Exception());
            return;
        }
        mProgressTrimEnd = touchProgress;
        updateTrimLevel();
    }

    /**
     * update progress when touch progress drawable not trims
     * @param touchProgress
     */
    private void touchProgressDrawable(int touchProgress) {
        if (touchProgress > mProgressTrimEnd) {
            touchProgress = mProgressTrimEnd;
        } else if (touchProgress < mProgressTrimStart) {
            touchProgress = mProgressTrimStart;
        }
        setProgress(touchProgress);

    }

    /**
     * find start trim or end trim when touch TrimSeekBar
     * @param touchX touch X Coordinate
     * @return the touch trim
     */
    private Drawable findTouchTrim(float touchX) {
        int endTrimOffset = getOffsetFromProgress(mProgressTrimEnd);
        int startTrimOffset = getOffsetFromProgress(mProgressTrimStart);
        float absEnd = Math.abs(touchX - endTrimOffset - mTrimWidth / 2);
        float absStart = Math.abs(touchX - startTrimOffset + mTrimWidth / 2);
        if (absStart < TRIM_TOUCH_AREA_WIDTH / 2) {
            mIsStartTrim = true;
            return mDrawableTrimStart;
        } else if (absEnd < TRIM_TOUCH_AREA_WIDTH / 2) {
            mIsStartTrim = false;
            return mDrawableTrimEnd;
        }
        return null;
    }

    private void updateTrimBounds(int w, int h) {
        int top = (int) ((h - mTrimHeight) / 2);
        int bottom = top + mTrimHeight;

        int startTrimLeft = getPaddingLeft();
        int startTrimRight = w - mTrimWidth - getPaddingRight();

        int endTrimLeft = getPaddingLeft() + mTrimWidth;
        int endTrimRight = w - getPaddingRight();

        if (mDrawableTrimStart != null) {
            mDrawableTrimStart.setBounds(startTrimLeft, top, startTrimRight, bottom);
        }
        if (mDrawableTrimEnd != null) {
            mDrawableTrimEnd.setBounds(endTrimLeft, top, endTrimRight, bottom);
        }
    }

    private static void updateTrimLevel(Drawable d, int trimProgress, int max) {
        if (d != null && max > 0) {
            d.setLevel((trimProgress * LEVEL_MAX) / max);
        }
    }

    /**
     * Update start trim & end trim level
     */
    private void updateTrimLevel() {
        updateTrimLevel(mDrawableTrimStart, mProgressTrimStart, getMax());
        updateTrimLevel(mDrawableTrimEnd, mProgressTrimEnd, getMax());
    }

    /**
     * Update ProgressDrawable bounds
     */
    private void updateProgressDrawableBounds() {
        Drawable layer = getProgressDrawable();
        int left = mTrimWidth;
        int top = (int) ((getHeight() + mTrimHeight) / 2 - mMarginM3 - mProgressHeight);
        int right = getWidth() - 2 * mTrimWidth - getPaddingLeft() - getPaddingRight() + left;
        int bottom = top + mProgressHeight;
        layer.setBounds(left, top, right, bottom);
    }

    /**
     * get the trim offset from progress
     * @param progress
     * @return offset in TrimSeekBar
     */
    private int getOffsetFromProgress(int progress) {
        if (progress < 0 || progress > getMax()) {
            Log.e(TAG, "progress below 0 OR above MAX", new Exception());
            return Integer.MIN_VALUE;
        }

        float scale = progress / (float) getMax();
        int trimDrawableOffset = (int) Math.ceil((scale * (getWidth() - 2 * mTrimWidth
                - getPaddingLeft() - getPaddingRight())));
        return trimDrawableOffset + getPaddingLeft() + mTrimWidth;
    }

    /**
     * get the progress from Offset in TrimSeekBar
     * @param offset
     * @return trimDrawable progress
     */
    private int getProgressFromOffset(float offset) {
        if (offset < mTrimWidth + getPaddingLeft()) {
            return 0;
        }
        if (offset > getWidth() - mTrimWidth - getPaddingRight()) {
            return getMax();
        }
        float location = offset - mTrimWidth - getPaddingLeft();
        int progressMax = getWidth() - 2 * mTrimWidth - getPaddingLeft() - getPaddingRight();
        float scale = location / progressMax;
        return (int) Math.ceil((scale * getMax()));
    }
}
