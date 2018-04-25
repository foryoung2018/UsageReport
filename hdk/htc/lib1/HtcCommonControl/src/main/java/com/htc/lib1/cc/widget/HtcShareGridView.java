package com.htc.lib1.cc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.GridView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by henry on 8/22/14.
 *
 * this is used ONLY in HtcShareActivity.
 * do NOT use this in any other cases.
 *
 * because this gridView and HtcShareSlidingUpPanelLayout both consume drag events,
 * they will conflict.
 * when conflict, you may not properly drag the panel and/or the gridView.
 * here we inhibit some cases and leave the events to the panel.
 *
 * should co-work with the panel.
 * @hide
 */
public class HtcShareGridView extends GridView {

    private static final boolean DEBUG = false;
    private static final String TAG = "HtcShareGridView";

    private SlidingUpPanelLayout mSlidingLayout;
    private int mTouchSlop;
    private float mMotionY;

    public HtcShareGridView(Context context) {
        super(context);
        init();
    }

    public HtcShareGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HtcShareGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    public void setSlidingUpPanelLayout(SlidingUpPanelLayout slidingLayout) {
        mSlidingLayout = slidingLayout;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int actionMasked = ev.getActionMasked();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                // keep y position for later use
                mMotionY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (SlidingUpPanelLayout.PanelState.EXPANDED != mSlidingLayout.getPanelState()) {
                    // collapsed
                    if (mMotionY - ev.getY() > mTouchSlop) { // drag up
                        // if user is dragging up, do not handle(consume) this motionEvent, simply return false
                        int oldAction = ev.getAction();
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        super.onTouchEvent(ev);
                        ev.setAction(oldAction);
                        if (DEBUG) Log.d(TAG, "onTouchEvent: leave MOVE event unhandled when user drags up in collapsed");
                        return false;
                    }
                } else {
                    // expanded
                    if (ev.getY() - mMotionY > mTouchSlop) { // drag down
                        boolean gridViewAtTop = 0 == getChildCount() || (0 == getFirstVisiblePosition() && 0 == getChildAt(0).getTop()) ;
                        if (gridViewAtTop) {
                            int oldAction = ev.getAction();
                            ev.setAction(MotionEvent.ACTION_CANCEL);
                            super.onTouchEvent(ev);
                            ev.setAction(oldAction);
                            if (DEBUG) Log.d(TAG, "onTouchEvent: leave MOVE event unhandled when user drags down in expanded");
                            return false;
                        }
                    }
                }
                break;
        }

        return super.onTouchEvent(ev);
    }
}
