package com.htc.lib1.cc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.ViewDragHelper;

/**
 * Created by henry on 8/26/14.
 *
 * this is used in HtcShareActivity.
 * do not use this in any other cases.
 *
 * because this and gridView both consume drag events,
 * we are to change the behaviors when this should not handle the events,
 * and leave them to the gridView.
 * @hide
 */
public class HtcShareSlidingUpPanelLayout extends SlidingUpPanelLayout {

    private static final boolean NO_COLLAPSE_WHEN_TOUCH_EXPLORATION = true;

    private static final String TAG = "HtcShareSlidingUpPanelLayout";
    private static final boolean DEBUG = false;

    private final AccessibilityManager mAM;
    private AdapterView mAdapterView;

    public HtcShareSlidingUpPanelLayout(Context context) {
        this(context, null);
    }

    public HtcShareSlidingUpPanelLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HtcShareSlidingUpPanelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mAM = (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);

        // expand the drag panel for touchExploration, because this "sliding up" behavior is not friendly to those people
        AccessibilityManager access = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (NO_COLLAPSE_WHEN_TOUCH_EXPLORATION && access.isEnabled() && access.isTouchExplorationEnabled()) {
            setPanelState(PanelState.EXPANDED);
            if (DEBUG) Log.d(TAG, "HtcShareSlidingPanelLayout: expand panel for accessibility");
        }
    }

    public void setGridView(AdapterView adapterView) {
        this.mAdapterView = adapterView;
    }

    @Override
    public void setDragView(View dragView) {
        super.setDragView(dragView);
        if (null != dragView) {
            dragView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEnabled() || !isTouchEnabled()) return;
                    if (PanelState.EXPANDED != getPanelState() && PanelState.ANCHORED != getPanelState()) {
                        if (getAnchorPoint() < 1.0f) {
                            setPanelState(PanelState.ANCHORED);
                        } else {
                            setPanelState(PanelState.EXPANDED);
                        }
                    } else {
                        // disable tap to collapse
                        if (NO_COLLAPSE_WHEN_TOUCH_EXPLORATION && mAM.isEnabled() && mAM.isTouchExplorationEnabled()) {
                            // do not collapse in "touch exploration" mode
                            if (DEBUG) Log.d(TAG, "onClick: prohibit tap to collapse for accessibility");
                            return;
                        }
                        setPanelState(PanelState.COLLAPSED);
                    }
                }
            });
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        boolean abort = false;
        if (mFirstLayout && ViewDragHelper.STATE_SETTLING == mDragHelper.getViewDragState() && PanelState.DRAGGING == getPanelState()) {
            // settling state got interrupted by size change
            abort = true;
        }
        super.onLayout(changed, l, t, r, b);
        if (abort) {
            // abort ongoing animation immediately
            if (DEBUG) Log.d(TAG, "onLayout: abort pending animation");
            mDragHelper.abort();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean ret = super.onInterceptTouchEvent(ev);


        final int NA = 0;
        final int DRAG_DOWN = 1;
        final int DRAG_UP = -1;
        int direction = NA;
        if ((ev.getY() - mInitialMotionY) > mDragHelper.getTouchSlop()) {
            direction = DRAG_DOWN;
        } else if (mInitialMotionY - ev.getY() > mDragHelper.getTouchSlop()) {
            direction = DRAG_UP;
        }

        boolean gridViewAtTop = 0 == mAdapterView.getChildCount() || (0 == mAdapterView.getFirstVisiblePosition() && 0 == mAdapterView.getChildAt(0).getTop()) ;
//        Log.d("henry", "ShareSlidingUpPanelLayout.onInterceptTouchEvent: expanded=" + isPanelExpanded() + " gridViewAtTop=" + gridViewAtTop + " action=" + action + " direction=" + direction);

        // only alternate behavior when dragPanel is expanded
        if (PanelState.EXPANDED == getPanelState()) {
            // disable drag to collapse
            boolean NO_COLLAPSE = NO_COLLAPSE_WHEN_TOUCH_EXPLORATION && mAM.isEnabled() && mAM.isTouchExplorationEnabled();
            if (gridViewAtTop && DRAG_DOWN == direction && !NO_COLLAPSE) {
                // do as usual (intercept, if you want)
            } else {
                // do not intercept touch event (leave this motionEvent to adapterView)
//                Log.d("henry", "ShareSlidingUpPanelLayout.onInterceptTouchEvent: force returning false. ret=" + ret);
                if (true == ret) {
                    if (DEBUG) Log.d(TAG, "onInterceptTouchEvent: setDragState to idle");
                    mDragHelper.abort(); // only abort when we have an argument
                }
                return false;
            }
        }

        return ret;
    }
}
