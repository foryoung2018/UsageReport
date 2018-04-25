
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.util.Log;

/**
 * Detects various gestures and events using the supplied {@link MotionEvent}s.
 * The {@link OnRefreshListener} callback will notify users when a particular
 * motion event has occurred. This class should only be used with
 * {@link MotionEvent}s reported via touch (don't use for trackball events). To
 * use this class:
 * <ul>
 * <li>Create an instance of the {@code RefreshGestureDetector} for your
 * {@link android.view.View}
 * <li>In the {@link android.view.View#onTouchEvent(MotionEvent)} method ensure you call
 * {@link #onTouchEvent(MotionEvent)}. The methods defined in your callback will
 * be executed when the events occur.
 * </ul>
 */
public class RefreshGestureDetector {

    /**
     * The listener that is used to notify when gestures occur. If you want to
     * listen for all the different gestures then implement this interface.
     */
    public interface RefreshListener {

        /**
         * Callback method to be invoked when it is pulled down to the boundary.
         */
        void onBoundary();

        /**
         * Callback method to be invoked when the pull down to refresh is end.
         */
        void onFinish();

        /**
         * Callback method to be invoked when the action is cancel.
         */
        void onCancel();

        /**
         * Callback method to be invoked when the gap is changed.
         * @param gap The current gap.
         * @param maxGap The maximum gap
         */
        void onGapChanged(int gap, int maxGap);
    }

    /**
     * The listener that is used to notify when gestures occur. If you want to
     * listen for all the different gestures then implement this interface.
     * @deprecated [Alternative Solution] Use RefreshListener instead.
     */
    @Deprecated
    public interface OnRefreshListener {

        /**
         * Callback method to be invoked when it is pulled down to the boundary.
         */
        void onBoundary();

        /**
         * Callback method to be invoked when the pull down to refresh is end.
         */
        void onFinish();

        /**
         * Callback method to be invoked when the action is cancel.
         */
        void onCancel();
    }

    private float mStart;

    private float mCurrent;

    private float mPre;

    private int mMax_diff = 36;

    private static int NONSENSITIVE_FACTOR = 6;

    private boolean mRefreshStarted = false;

    private boolean mRefreshChanged = false;

    private boolean mPullDownStarted = false;

    private OnRefreshListener mRefreshListener;
    private RefreshListener mListener;

    /**
     * Register the OnRefreshListener
     * @param listener the OnRefreshListener
     * @deprecated [Alternative Solution] Use setRefreshListener(RefreshListener listener) instead.
     */
    @Deprecated
    public void setOnRefreshListener(OnRefreshListener listener) {
    }

    /**
     * Register the RefreshListener
     * @param listener the RefreshListener
     */
    public void setRefreshListener(RefreshListener listener) {
        if (listener == null) {
            throw new NullPointerException("RefreshListener must not be null");
        }
        mListener = listener;
    }

    /**
     * Creates a RefreshGestureDetector with the supplied listener. You may only
     * use this constructor from a UI thread (this is the usual situation).
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param listener the listener invoked for all the callbacks, this must not
     *            be null.
     * @deprecated [Alternative Solution] Use RefreshGestureDetector(Context context, RefreshListener listener) instead.
     */
    @Deprecated
    public RefreshGestureDetector(Context context, OnRefreshListener listener) {
        if (listener == null) {
            throw new NullPointerException("OnRefreshListener must not be null");
        }
        mRefreshListener = listener;
        init(context);
    }

    /**
     * Creates a RefreshGestureDetector with the supplied listener. You may only
     * use this constructor from a UI thread (this is the usual situation).
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param listener the listener invoked for all the callbacks, this must not
     *            be null.
     */
    public RefreshGestureDetector(Context context, RefreshListener listener) {
        if (listener == null) {
            throw new NullPointerException("RefreshListener must not be null");
        }
        mListener = listener;
        init(context);
    }

    private void init(Context context) {
        try {
            mMax_diff = context.getResources().getDimensionPixelOffset(com.htc.lib1.cc.R.dimen.pull_down_offset);
        } catch (Resources.NotFoundException e) {
            Log.w("RefreshGestureDetector", "Resource id com.htc.lib1.cc.R.dimen.pull_down_offset is not found");
        }
    }

    /**
     * Analyzes the given motion event and if applicable triggers the
     * appropriate callbacks on the {@link OnRefreshListener} supplied.
     *
     * @param ev The current motion event.
     * @return true if the {@link OnRefreshListener} consumed the event, else
     *         false.
     */
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE && mStart >= 0) {
            mCurrent = ev.getY();
            if (mCurrent > mStart) {
                float dist;
                mPullDownStarted = true;
                if (mCurrent > mPre) {
                    dist = (mCurrent - mStart) / NONSENSITIVE_FACTOR;
                } else {
                    dist = (mPre - mStart) / NONSENSITIVE_FACTOR;
                }

                if (dist >= mMax_diff) {
                    dist = mMax_diff;
                    if (mRefreshStarted != true) {
                        if (mListener != null)
                            mListener.onBoundary();
                        else if (mRefreshListener != null)
                            mRefreshListener.onBoundary();
                        mRefreshStarted = true;
                    }

                } else if (dist < mMax_diff) {
                    if (mRefreshStarted != false) {
                        //mRefreshListener.onCancel();
                        mRefreshStarted = false;
                    }
                }
                if (mPre != mCurrent && (mRefreshStarted == false)) {
                    mRefreshChanged = true;
                    if (mListener != null)
                        mListener.onGapChanged((int) dist, mMax_diff);
                }
            } else {
                if (true == mRefreshChanged) {
                    mPullDownStarted = mRefreshChanged = false;
                    if (mListener != null)
                        mListener.onCancel();
                    else if (mRefreshListener != null)
                        mRefreshListener.onCancel();
                }
            }
            mPre = mCurrent;
        } else if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mPre = mStart = ev.getY();
        } else if (ev.getAction() == MotionEvent.ACTION_UP && mStart >= 0) {
            mRefreshChanged = false;
            if (true == mRefreshStarted) {
                mRefreshStarted = false;
                if (mListener != null)
                    mListener.onFinish();
                else if (mRefreshListener != null)
                    mRefreshListener.onFinish();
            } else if(mPullDownStarted){
                if (mListener != null)
                    mListener.onCancel();
                else if (mRefreshListener != null)
                    mRefreshListener.onCancel();
            }
            mPre = mStart = -200;
            mPullDownStarted = false;
        } else if (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_POINTER_UP) {
            if (mRefreshStarted != false || mRefreshChanged != false) {
                if (mListener != null)
                    mListener.onCancel();
                else if (mRefreshListener != null)
                    mRefreshListener.onCancel();
                mRefreshStarted = false;
            }
            mRefreshChanged = false;
            mPre = mStart = -200;
            mPullDownStarted = false;
        }
        return true;
    }

    void setMaxOverScrollGap(int maxGap) {
        mMax_diff = maxGap;
    }

    void setOverSrrollSensitivity(int sensitivity) {
        NONSENSITIVE_FACTOR = sensitivity;
    }

    void reDetectGesture() {
        mPre = mStart = mCurrent;
    }
}
