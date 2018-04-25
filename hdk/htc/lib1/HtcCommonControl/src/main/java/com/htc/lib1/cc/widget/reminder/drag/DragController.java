package com.htc.lib1.cc.widget.reminder.drag;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.htc.lib1.cc.widget.reminder.debug.MyLog;
import com.htc.lib1.cc.widget.reminder.drag.Workspace.GestureCallBack;
import com.htc.lib1.cc.widget.reminder.ui.ReminderView.ReminderTile;
import com.htc.lib1.cc.widget.reminder.util.MyUtil;

/** @hide */
public class DragController {

    private static final String TAG  = "DragCtrl";

    private Context mContext;

    /** Whether or not we're dragging. */
    private boolean mDragging;
    private boolean mDragEnd;
    /** To play the clicked animation. */
    private boolean mClicking;
    private int mMinTouchSlop;

    /** X coordinate of the down event. */
    private int mMotionDownX;
    /** Y coordinate of the down event. */
    private int mMotionDownY;
    /** X coordinate of the up event. */
    private int mMotionUpX;
    /** Y coordinate of the up event. */
    private int mMotionUpY;
    private int mMotionMoveX;
    private int mMotionMoveY;

    private ViewGroup mDragLayer;
    /** The view that moves around while you drag.  */
    private DragView mDragView = null;
    private View mMoveTarget;
    /** Filter Multi-Touch Event. */
    private TouchFilter mTouchFilter;
    private SpeedRecorder mSpeedRecorder = new SpeedRecorder();

    private DraggableView mCurDraggableView;
    private DraggableView mPreDraggableView;
    private Workspace.GestureCallBack mWorkspaceCallBack;

    /** The Gesture Event for callback function using. */
    private GestureEvent mGestureEvent = new GestureEvent();

    private int mDragState = GestureEvent.ACTION_IDLE;
    private boolean mEnableMoveHorizontal = false;

    /**
     * Used to create a new DragLayer from XML.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public DragController(Context context) {
        mContext = context;
        SpeedRecorder.init(mContext);
        mTouchFilter = new TouchFilter();
        if (mTouchFilter != null) {
            mTouchFilter.setCallback(new TouchFilter.TouchListener() {
                @Override
                public boolean onTouchEvent(MotionEvent event, int id, int x, int y) {
                    // mDragging && mDragEnd -> the finger already leave from screen.
                    // We should not update the touch record until finished to play the animation.
                    // For the special case (clicking), we still need to update the touch record.
                    if (mDragging && mDragEnd && !isClicking()) {
                        MyLog.d(TAG, "onTouchEvent - mDragging & mDragEnd.");
                        return true;
                    }
                    int action = -1;
                    if (event != null) {
                        action = event.getAction();
                    }
                    switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        MyLog.si(TAG, "Touch(DOWN) x: " + x
                                + ", y: " + y);
                        // Remember where the motion event started
                        mMotionDownX = x;
                        mMotionDownY = y;
                        mMotionMoveX = x;
                        mMotionMoveY = y;
                        mMotionUpX   = x;
                        mMotionUpY   = y;
                        mSpeedRecorder.start(x, y);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        mMotionMoveX = x;
                        mMotionMoveY = y;
                        if (isClicking()) {
                            break;
                        }
                        handleMoveEvent(x, y);
                        break;

                    case MotionEvent.ACTION_UP:
                        MyLog.si(TAG, "Touch(UP) x: " + x
                                + ", y: " + y);
                        if (isClicking()) {
                            break;
                        }
                        // Ensure that we've processed a move event at the current pointer location.
                        handleTouchUp(x, y);
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        int minY = mSpeedRecorder.getMinY();
                        MyLog.si(TAG, "Touch(CANCEL) x:" + x
                                + " y:" + y
                                + " minY:" + minY);
                        if (isClicking()) {
                            break;
                        }
                        if (y <= minY) {
                            handleTouchUp(x, y);
                        } else {
                            cancelDrag(true);
                        }
                        break;
                    }
                    return true;
                }
            });
        }
        mUIHandler = new UIHandler();
        mMinTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
    }

    private void handleTouchUp(int x, int y) {
         mMotionMoveX = x;
         mMotionMoveY = y;
         mMotionUpX = x;
         mMotionUpY = y;
         handleMoveEvent(x, y);
         if (mClicking) {
             onClick();
         } else {
             onDragEnd();
         }
    }

    private UIHandler mUIHandler;
    private static final int WHAT_UI_CLICK_ANIMATION = 1001;
    private class UIHandler extends Handler {
        public void handleMessage(Message msg) {
            if (msg == null) {
                return;
            }
            int what = msg.what;
            switch(what) {
            case WHAT_UI_CLICK_ANIMATION:
                onClickAnimStart();
                break;
            }
        }
    }

    /**
     * Starts a drag.
     *
     * @param b: The bitmap to display as the drag image.  It will be re-scaled to the enlarged size.
     * @param dragAction: The drag action: either {@link #DRAG_ACTION_MOVE} or {@link #DRAG_ACTION_COPY}
     * @param draggablePosition: The original position of draggable view.
     * @param dragType: Enable/Disable Vertical/Horizontal Move.
     */
    public void startDrag(View targetView, int[] draggablePosition,
            int dragType, DragAnimation anim, DraggableView draggable,
            int[] targetViewSize) {

        if (targetView == null || draggable == null) {
            MyLog.w(TAG, "startDrag Failed.");
            return;
        }
        setDragState(GestureEvent.ACTION_IDLE);
        mClicking = true;
        mDragging = true;
        mDragEnd  = false;

        mEnableMoveHorizontal = ((dragType & DraggableView.DRAG_TYPE_ONLY_HORIZONTAL) > 0);
        mDragView = new DragView(mContext, mDragLayer, targetView,
                draggablePosition, dragType, anim, this, targetViewSize);

        final DragView dragView = mDragView;
        MyLog.si(TAG, "startDrag: " + draggable);
        if (dragView != null) {
            mCurDraggableView = draggable;
            mPreDraggableView = null;
            dragView.show(mMotionMoveX - mMotionDownX, mMotionMoveY - mMotionDownY);
            sendGestureEvent(draggable, GestureEvent.ACTION_TOUCH_DOWN, mMotionDownX, mMotionDownY, null);
            updateVisibilityByState(
                    getBundle4UpdateVisibility(GestureEvent.ACTION_TOUCH_DOWN),
                    View.VISIBLE);
        } else {
            MyLog.w(TAG, "startDrag dragView null");
            mCurDraggableView = null;
            mPreDraggableView = null;
        }
    }

    public void playClickAnimation(View targetView, int[] draggablePosition,
            int dragType, DragAnimation anim, DraggableView draggable,
            int[] targetViewSize) {

        if (targetView == null || draggable == null) {
            MyLog.w(TAG, "playClickAnima Failed.");
            return;
        }
        setDragState(GestureEvent.ACTION_IDLE);
        mClicking = true;
        mDragging = true;
        mDragEnd  = false;

        mEnableMoveHorizontal = ((dragType & DraggableView.DRAG_TYPE_ONLY_HORIZONTAL) > 0);
        mDragView = new DragView(mContext, mDragLayer, targetView,
                draggablePosition, dragType, anim, this, targetViewSize);

        final DragView dragView = mDragView;
        MyLog.si(TAG, "playClickAnima draggable: " + draggable);
        if (dragView != null) {
            mCurDraggableView = draggable;
            mPreDraggableView = null;
            dragView.show(0, 0);
            sendGestureEvent(draggable, GestureEvent.ACTION_TOUCH_DOWN, mMotionDownX, mMotionDownY, null);
            updateVisibilityByState(
                    getBundle4UpdateVisibility(GestureEvent.ACTION_CLICK),
                    View.VISIBLE);
            // Post Message to next Render to do onClickAnimStart()
            // Otherwise, it can't get the correct y of dragview.
            MyUtil.removeMessage(mUIHandler, WHAT_UI_CLICK_ANIMATION);
            MyUtil.sendMessage(mUIHandler, WHAT_UI_CLICK_ANIMATION);
        } else {
            MyLog.w(TAG, "playClickAnimation dragView null");
            mCurDraggableView = null;
            mPreDraggableView = null;
        }
    }

    private void onDragStart(int x, int y) {
        DragView dragview = mDragView;
        DraggableView draggable = mCurDraggableView;
        // Invalid Drag State
        if (dragview == null || draggable == null) {
            return;
        }
        MyLog.si(TAG, "onDragStart");
        // Drag Start
        setDragState(GestureEvent.ACTION_DRAG_START);
        sendGestureEvent(draggable, GestureEvent.ACTION_DRAG_START, mMotionDownX, mMotionDownY, null);
        updateVisibilityByState(getBundle4UpdateVisibility(GestureEvent.ACTION_DRAG_START), View.VISIBLE);
        // Update Drag View
        boolean dragVOnly = false;
        if (dragview != null) {
            int moveX = x - mMotionDownX;
            int moveY = y - mMotionDownY;
            dragview.move(moveX, moveY);
            dragVOnly = dragview.isDragVerticalOnly();
        }
        mSpeedRecorder.setVerticalOnly(dragVOnly);
    }

    private void onDragMove(int x, int y) {
        DragView dragview = mDragView;
        DraggableView draggable = mCurDraggableView;
        if (dragview == null || draggable == null) {
            return;
        }
        // Drag Move
        setDragState(GestureEvent.ACTION_DRAG_MOVE);
        sendGestureEvent(draggable, GestureEvent.ACTION_DRAG_MOVE, x, y, null);
        updateVisibilityByState(getBundle4UpdateVisibility(GestureEvent.ACTION_DRAG_MOVE), View.VISIBLE);
        // Update Drag View
        if (dragview != null) {
            int moveX = x - mMotionDownX;
            int moveY = y - mMotionDownY;
            dragview.move(moveX, moveY);
            if (mSpeedRecorder != null) {
                mSpeedRecorder.move(x, y);
            }
        }
    }

    public void cancelDragging() {
        DragView dragview = mDragView;
        DraggableView draggable = mCurDraggableView;
        if (dragview == null || draggable == null) {
            return;
        }
        MyLog.si(TAG, "cancelDragging: " + mDragState);
        if ((mDragState == GestureEvent.ACTION_IDLE)
            || (mDragState == GestureEvent.ACTION_TOUCH_DOWN)) {
            forceResetDragState();
        } else if ((mDragState == GestureEvent.ACTION_DRAG_START)
            || (mDragState == GestureEvent.ACTION_DRAG_MOVE)) {
            cancelDrag(false);
        }
    }

    /**
     * Stop dragging without dropping.
     */
    public void cancelDrag(boolean cancelWithAnima) {
        onDragEnd(true, cancelWithAnima);
    }

    private void onDragEnd() {
        onDragEnd(false, true);
    }

    private void onDragEnd(boolean cancel, boolean cancelWithAnima) {
        DragView dragview = mDragView;
        DraggableView draggable = mCurDraggableView;
        // Invalid Drag State
        if (dragview == null || draggable == null || mDragEnd) {
            return;
        }
        // Drag End
        setDragState(GestureEvent.ACTION_DRAG_END);
        boolean isDrop = false;
        if (!cancel && mSpeedRecorder != null) {
            isDrop = mSpeedRecorder.end(mMotionUpX, mMotionUpY);
        }
        MyLog.si(TAG, "onDragEnd drop:" + isDrop);
        Bundle extra = new Bundle();
        if (extra != null) {
            extra.putBoolean(DraggableView.BUNDLE_KEY_ISDROP, isDrop);
        }
        sendGestureEvent(draggable, GestureEvent.ACTION_DRAG_END, mMotionUpX, mMotionUpY, extra);
        updateVisibilityByState(getBundle4UpdateVisibility(GestureEvent.ACTION_DRAG_END), View.VISIBLE);
        if (isDrop) {
            onDropStart();
        } else {
            // Doesn't need the animation by cancelDragging().
            if (cancel && !cancelWithAnima) {
                onDragBackStart(false);
                return;
            }
            // For MastHead or Tile with action,
            // If user dragging position under the original view,
            // We should play the vertical vibration effect.
            boolean isTileWithAction = isTileWithAction(draggable);
            boolean isBackStart = true;
            if (isTileWithAction && (mMotionUpY > mMotionDownY)) {
                isBackStart = false;
            }
            if (isBackStart) {
                onDragBackStart(true);
            } else {
                onClickAnimStart();
            }
        }
    }

    private boolean isTileWithAction(DraggableView draggable) {
        try {
            if (draggable != null &&
                draggable instanceof ReminderTile) {
                return true;
                }
            } catch (Exception e) {
        }
        return false;
    }

    private void onDragBackStart(boolean withAnimation) {
        DragView dragview = mDragView;
        DraggableView draggable = mCurDraggableView;
        // Invalid Drag State
        if (dragview == null || draggable == null) {
            return;
        }
        MyLog.si(TAG, "onDragBackStart");
        // Drag Back Start
        setDragState(GestureEvent.ACTION_BACK);
        sendGestureEvent(draggable, GestureEvent.ACTION_BACK, mMotionUpX, mMotionUpY, null);
        updateVisibilityByState(getBundle4UpdateVisibility(GestureEvent.ACTION_BACK), View.VISIBLE);
        if (withAnimation) {
            dragview.onDragBackStart(mMotionUpX, mMotionUpY);
        } else {
            onDragBackEnd();
        }
    }

    public void onDragBackEnd() {
        DragView dragview = mDragView;
        DraggableView draggable = mCurDraggableView;
        // Invalid Drag State
        if (dragview == null || draggable == null) {
            return;
        }
        MyLog.si(TAG, "onDragBackEnd");
        //updateVisibilityByState(getBundle4UpdateVisibility(GestureEvent.ACTION_IDLE), View.GONE);
        removeDragView();
    }

    private void onDropStart() {
        DragView dragview = mDragView;
        DraggableView draggable = mCurDraggableView;
        // Invalid Drag State
        if (dragview == null || draggable == null) {
            return;
        }
        MyLog.si(TAG, "onDropStart");
        // Drop Start
        setDragState(GestureEvent.ACTION_DROP);
        Bundle extra = new Bundle();
        if (extra != null) {
            extra.putBoolean(DraggableView.BUNDLE_KEY_ISUNLOCKWHENDROP, draggable.isUnlockWhenDrop());
        }
        sendGestureEvent(draggable, GestureEvent.ACTION_DROP, mMotionUpX, mMotionUpY, extra);
        updateVisibilityByState(getBundle4UpdateVisibility(GestureEvent.ACTION_DROP), View.VISIBLE);
        if (dragview != null) {
            dragview.onDropStart(mMotionUpX, mMotionUpY);
        }
    }

    public void onDropEnd() {
        DragView dragview = mDragView;
        DraggableView draggable = mCurDraggableView;
        if (dragview == null || draggable == null) {
            return;
        }
        MyLog.si(TAG, "onDropEnd");
        //updateVisibilityByState(getBundle4UpdateVisibility(GestureEvent.ACTION_IDLE), View.GONE);
        removeDragView();
    }

    private void onClick() {
        if (mGestureEvent == null) {
            mGestureEvent = new GestureEvent();
        }
        if (mDragging && !mDragEnd) {
            mDragEnd = true;
            MyLog.si(TAG, "onClick");
            onClickAnimStart();
        }
    }

    private void onClickAnimStart() {
        DragView dragview = mDragView;
        DraggableView draggable = mCurDraggableView;
        // Invalid Drag State
        if (dragview == null || draggable == null) {
            return;
        }
        MyLog.si(TAG, "onClickAnimStart");
        setDragState(GestureEvent.ACTION_CLICK);
        sendGestureEvent(mCurDraggableView, GestureEvent.ACTION_CLICK, mMotionUpX, mMotionUpY, null);
        updateVisibilityByState(getBundle4UpdateVisibility(GestureEvent.ACTION_CLICK), View.VISIBLE);
        if (dragview != null) {
            dragview.onClick(mMotionUpX, mMotionUpY);
        }
    }

    public void onClickAnimEnd() {
        DragView dragview = mDragView;
        DraggableView draggable = mCurDraggableView;
        // Invalid Drag State
        if (dragview == null || draggable == null) {
            return;
        }
        MyLog.si(TAG, "onClickAnimEnd");
        //updateVisibilityByState(getBundle4UpdateVisibility(GestureEvent.ACTION_IDLE), View.GONE);
        removeDragView();
    }

    private void removeDragView() {
        setDragState(GestureEvent.ACTION_IDLE);
        sendGestureEvent(mCurDraggableView, GestureEvent.ACTION_IDLE, mMotionUpX, mMotionUpY, null);
        updateVisibilityByState(getBundle4UpdateVisibility(GestureEvent.ACTION_IDLE), View.GONE);
        if (mDragView != null) {
            mDragView.remove();
            mDragView = null;
        }
        mClicking = false;
        mDragging = false;
        mDragEnd  = true;
        if (mCurDraggableView != null) {
            mPreDraggableView = mCurDraggableView;
            mCurDraggableView = null;
        }
    }

    /**
     * Sets the view that should handle move events.
     */
    public void setMoveTarget(View view) {
        mMoveTarget = view;
    }

    public boolean dispatchUnhandledMove(View focused, int direction) {
        return mMoveTarget != null && mMoveTarget.dispatchUnhandledMove(focused, direction);
    }

    private long mLastMoveTime = 0;
    private static final long MIN_MOVE_DURATION = 15;
    private void handleMoveEvent(int x, int y) {
        if (mDragView != null) {
            boolean isClicking = mClicking;
            mClicking = isClicking(x, y);
            if (!mClicking) {
                if (isClicking) {
                    // Beginning Start Drag.
                    onDragStart(x, y);
                } else {
                    long curTime = SystemClock.uptimeMillis();
                    if ((curTime - mLastMoveTime) > MIN_MOVE_DURATION) {
                        mLastMoveTime = curTime;
                        onDragMove(x, y);
                    }
                }
            }
        }
    }

    private boolean isClicking(int x, int y) {
        boolean clicking = mClicking;
        if (mClicking) {
            // For Drag Type is only supported Vertical Direction,
            // When drag downward,
            // We shouldn't trigger onStartDrag().
            if (!mEnableMoveHorizontal && y > mMotionDownY) {
                return clicking;
            }
            int moveX = Math.abs(mMotionDownX - x);
            int moveY = Math.abs(mMotionDownY - y);
            clicking = ((moveX + moveY) > mMinTouchSlop) ? false:true;
        }
        return clicking;
    }

    public boolean handleTouchEvent(MotionEvent event) {
        if (mTouchFilter != null) {
            mTouchFilter.dispatchTouchEvent(event);
        }
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mDragging && !isClicking();
    }

    public void setDragLayer(ViewGroup dragLayer) {
        mDragLayer = dragLayer;
    }

    private void updateVisibilityByState(Bundle bundle, int newDragViewVisibility) {
        if (mCurDraggableView != null) {
            mCurDraggableView.updateVisibilityByState(bundle);
        }
        DragView dragView = mDragView;
        if (dragView != null) {
            int preDragViewVisibility = dragView.getVisibility();
            if (preDragViewVisibility != newDragViewVisibility) {
                dragView.setVisibility(newDragViewVisibility);
            }
        }
    }

    private Bundle getBundle4UpdateVisibility(int action) {
        Bundle bundle = new Bundle();
        if (bundle != null) {
            bundle.putInt(DraggableView.BUNDLE_KEY_ACTION, action);
        }
        return bundle;
    }

    public void updateVisibility4IdleMode(boolean forceUpdate) {
        if (!forceUpdate && mDragState != GestureEvent.ACTION_IDLE) {
            return;
        }
        if (mPreDraggableView != null) {
            Bundle bundle = getBundle4UpdateVisibility(GestureEvent.ACTION_IDLE);
            mPreDraggableView.updateVisibilityByState(bundle);
            mPreDraggableView = null;
        }
    }

    public DraggableView getCurDraggableView() {
        return mCurDraggableView;
    }

    public void registerGestureCallBack(GestureCallBack callback) {
        MyLog.v(TAG, "registerGestureCallBack callback: " + callback);
        mWorkspaceCallBack = callback;
    }

    public void unregisterGestureCallBack(GestureCallBack callback) {
        MyLog.v(TAG, "unregisterGestureCallBack callback: " + callback);
        if (mWorkspaceCallBack == callback) {
            mWorkspaceCallBack = null;
        }
    }

    public void resetDragState(boolean unlocked) {
        DragView dragview = mDragView;
        DraggableView draggable = mCurDraggableView;
        if (unlocked) {
            forceResetDragState();
            return;
        }
        if (draggable == null && dragview == null) {
            updateVisibility4IdleMode(true /* force update */);
            setDragState(GestureEvent.ACTION_IDLE);
            sendGestureEvent(draggable, GestureEvent.ACTION_IDLE, mMotionUpX, mMotionUpY, null);
        } else {
            forceResetDragState();
        }
    }

    public void forceResetDragState() {
        MyLog.i(TAG, "forceResetDragState");
        removeDragView();
        updateVisibility4IdleMode(true /* force update */);
    }

    private void setDragState(int state) {
        int preState = mDragState;
        mDragState = state;
        if (mDragState == GestureEvent.ACTION_IDLE) {
            // For removeDragView().
            // preState: BackEnd & ClickEnd.
            if (preState == GestureEvent.ACTION_BACK ||
                preState == GestureEvent.ACTION_CLICK) {
                updateVisibility4IdleMode(false /* not force update */);
            }
            // FIXME: check this status if we need to reset it.
//            // For removeDragView().
//            // preState: DropEnd.
//            else if (preState == GestureEvent.ACTION_DROP) {
//                updateVisibility4IdleMode(false /* not force update */);
//            }
        }
    }

    private void sendGestureEvent(DraggableView view, int state, int x, int y, Bundle extra) {
        if (view == null) {
            return;
        }
        if (mGestureEvent == null) {
            mGestureEvent = new GestureEvent();
        }
        if (mGestureEvent != null) {
            mGestureEvent.action = state;
            mGestureEvent.x = x;
            mGestureEvent.y = y;
            view.onGestureChanged(mGestureEvent, extra);
            if (mWorkspaceCallBack != null) {
                mWorkspaceCallBack.onGestureChanged(view, mGestureEvent, extra);
            }
        }
    }

    public boolean isClicking() {
        return (mDragState == GestureEvent.ACTION_CLICK);
    }
}
