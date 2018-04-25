package com.htc.lib1.cc.widget.reminder.drag;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.widget.reminder.debug.MyLog;

/**
 * Draggable View
 */
public class DraggableView extends RelativeLayout {
    /** Log Tag */
    private static final String TAG  = "Draggable";

    /** DRAG TYPE:Vertical */
    /** @hide */
    public static final int DRAG_TYPE_ONLY_VERTICAL       = 0x01;
    /** DRAG TYPE:Horizontal */
    /** @hide */
    public static final int DRAG_TYPE_ONLY_HORIZONTAL     = 0x02;
    /** DRAG TYPE:Both */
    /** @hide */
    public static final int DRAG_TYPE_VERTICAL_HORIZONTAL =
            DRAG_TYPE_ONLY_VERTICAL | DRAG_TYPE_ONLY_HORIZONTAL;
    /** DRAG TYPE:Manual Drag */
    /** @hide */
    public static final int DRAG_TYPE_MANUAL_DRAG         = 0x04;

    private int mDragType = DRAG_TYPE_VERTICAL_HORIZONTAL;
    private DragAnimation mDragAnimation;
    /** boolean Is Dragging? */
    private boolean mIsDragging = false;

    /** Update Visibility by State: Bundle Key: Action */
    /** @hide */
    public static final String BUNDLE_KEY_ACTION = "action";
    /** Update Visibility by State: Bundle Key: isDrop */
    /** @hide */
    public static final String BUNDLE_KEY_ISDROP = "isDrop";
    /** Update Visibility by State: Bundle Key: isUnlockWhenDrop */
    /** @hide */
    public static final String BUNDLE_KEY_ISUNLOCKWHENDROP = "isUnlockWhenDrop";
    /** Update Visibility by State: Bundle Key: dragType */
    /** @hide */
    public static final String BUNDLE_KEY_DRAGTYPE = "dragType";

    /** Send action to Workspace: Touch Down */
    /** @hide */
    public static final int DO_ACTION_TOUCH_DOWN = 1001;
    /** Send action to Workspace: Touch Up */
    /** @hide */
    public static final int DO_ACTION_TOUCH_UP   = 1002;
    /** Send action to Workspace: Start Drag */
    /** @hide */
    public static final int DO_ACTION_START_DRAG = 1003;
    /** Send action to Workspace: Click Animation */
    /** @hide */
    public static final int DO_ACTION_CLICK_ANIMATION = 1004;

    /**
     * sendActionListener:
     * Send action to Workspace/ViewStateManager.
     */
    /** @hide */
    public interface doActionListener {
        /**
         * do Action
         * @param view DraggableView
         * @param action action
         * @param extra extra
         * @return Bundle
         */
        Bundle doAction(DraggableView view, int action, Bundle extra);
    }
    private doActionListener mdoActionListener;

    /**
     * For Gesture CallBack:
     * Send all gesture state to DraggableView itself.
     * ------------------------------------------------------
     * For DraggableView,
     * please use gestureListener to listen the drag state.
     */
    /** @hide */
    public interface gestureListener {
        /**
         * onGesture
         * @param event GestureEvent
         * @return Bundle
         */
        Bundle onGestureChanged(DraggableView view, GestureEvent event, Bundle extra);
    }
    private gestureListener mGestureListener;

    /** For Change UI Parent Function. */
    private RelativeLayout mLayoutContainer;

    /**
     * Constructor
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public DraggableView(Context context) {
        super(context);
        onInit(context, null, 0);
    }

    /**
     * Constructor
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs AttributeSet
     */
    public DraggableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInit(context, attrs, 0);
    }

    /**
     * Constructor
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs AttributeSet
     * @param defStyle defStyle
     */
    public DraggableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onInit(context, attrs, defStyle);
    }

    private void onInit(Context context, AttributeSet attrs, int defStyle) {
        initLayoutContainer();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initLayoutContainer();
    }

    private void initLayoutContainer() {
        if (mLayoutContainer == null) {
            mLayoutContainer = new RelativeLayout(getContext());
            this.addView(mLayoutContainer, new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private boolean isAutoDrag() {
        return (mDragType & DRAG_TYPE_MANUAL_DRAG) == 0;
    }

    /**
     * onTouchEvent
     * @param event MotionEvent
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = (event != null) ? event.getAction() : -1;
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            if (isAutoDrag()) {
                beginDrag();
            }
            break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * Begin Drag
     */
    private void beginDrag() {
        MyLog.v(TAG, "beginDrag - mTouchListener:" + mdoActionListener);
        if (mdoActionListener != null) {
            mdoActionListener.doAction(this, DO_ACTION_START_DRAG, null);
        }
    }

    /**
     * For Draggable:
     * set sendActionListener for the other action callback.
     * @param listener onDragCallBack
     */
    /** @hide */
    public void setActionListener(doActionListener listener) {
        MyLog.v(TAG, "setActionListener: " + listener);
        mdoActionListener = listener;
    }

    /**
     * For Draggable:
     * set GestureCallback Listener for gesture event callback.
     * @param listener GestureCallBack
     */
    /** @hide */
    public void setGestureCallbackListener(gestureListener listener) {
        MyLog.v(TAG, "setGestureListener: " + listener);
       mGestureListener = listener;
    }

    /**
     * set drag type for enable/disable Vertical/Horizontal Move.
     * @param type type
     */
    /** @hide */
    public void setDragType(int type) {
        MyLog.v(TAG, "setDragType: " + type);
        mDragType = type;
    }

    /**
     * get drag type for enable/disable Vertical/Horizontal Move.
     */
    /** @hide */
    public int getDragType() {
        return mDragType;
    }

    /**
     * CallBack Function #onGestureChanged(GestureEvent)
     * @param event GestureEvent
     */
    /** @hide */
    public void onGestureChanged(GestureEvent event, Bundle extra) {
        if (mGestureListener != null) {
            mGestureListener.onGestureChanged(this, event, extra);
        }
    }

    Workspace mWorkspace;
    /**
     * onAttachedToWindow
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mWorkspace = getWorkspace();
        MyLog.v(TAG, "onAttachedToWindow: " + mWorkspace);
        if (mWorkspace != null) {
            mWorkspace.bindDragView(this);
        }
    }

    /**
     * onDetachedFromWindow
     */
    @Override
    public void onDetachedFromWindow() {
        MyLog.v(TAG, "onDetachedFromWindow: " + mWorkspace);
        if (mWorkspace != null) {
            mWorkspace.unbindDragView(this);
            mWorkspace = null;
        }
        super.onDetachedFromWindow();
    }

    /**
     * Get Workspace.
     * @return Workspace
     */
    private Workspace getWorkspace() {
        Workspace workspace = null;
        try {
            View root = getRootView();
            if (root != null) {
//                int id = MyUtil.getIdFromRes(
//                        mContext,
//                        ReminderResWrap.ID_FOREGROUND_CONTAINER);
                int id = R.id.foreground_container;
                View fc = root.findViewById(id);
                if (fc != null) {
                    ViewParent vp = fc.getParent();
                    if (vp != null && vp instanceof WorkspaceView) {
                        WorkspaceView wsView = (WorkspaceView)vp;
                        if (wsView != null) {
                            workspace = wsView.getWorkspace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            MyLog.w(TAG, "getWorkspace e: " + e.getMessage());
        }
        return workspace;
    }

    /**
     * Set DragAnimation for Drop/DragBack Animation.
     * @param animation DragAnimation
     */
    public void setDragAnimation(DragAnimation animation) {
        MyLog.v(TAG, "setDragAnimation(): " + animation);
        mDragAnimation = animation;
    }

    /**
     * Return DragAnimation for Drop/DragBack Animation.
     * @return DragAnimation
     */
    /** @hide */
    public DragAnimation getDragAnimation() {
        return mDragAnimation;
    }

    /**
     * To check whether unlock when Drop.
     */
    /** @hide */
    public boolean isUnlockWhenDrop() {
        return false;
    }

    /**
     * Play Unlock Animation.
     */
    /** @hide */
    public void playUnlockAnimation() {

    }

    /**
     * Update Visibility by Gesture State.
     * @param bundle bundle
     */
    /** @hide */
    protected int mPreAction = GestureEvent.ACTION_IDLE;
    /** @hide */
    public void updateVisibilityByState(Bundle bundle) {
        int action = (bundle != null)? bundle.getInt(BUNDLE_KEY_ACTION, -1):(-1);
        // For the clicking animation is playing,
        // User can touch any draggable view.
        // we should cancel the animation and start dragging.
        // For this, we need to let the draggable view can to handle the touch event
        // even if it is dragging.
        // Therefore, we can't set visibility to INVISIBLE...
        // So, change to set Alpha.
        boolean isShowing = (getAlpha() > 0.0);  //(getVisibility() == View.VISIBLE);
        if (action != mPreAction && action == GestureEvent.ACTION_IDLE) {
            stopDragView();
        }
        if (action == GestureEvent.ACTION_TOUCH_DOWN
            || action == GestureEvent.ACTION_CLICK) {
            // Hide Draggable View
            if (isShowing) {
                //setVisibility(View.INVISIBLE);
                setAlpha((float) 0.0);
            }
        } else if (action == GestureEvent.ACTION_IDLE &&
                (mPreAction == GestureEvent.ACTION_BACK ||
                 mPreAction == GestureEvent.ACTION_CLICK ||
                 mPreAction == GestureEvent.ACTION_IDLE)) {
            // Show Draggable View
            if (!isShowing) {
                //setVisibility(View.VISIBLE);
                setAlpha((float) 1.0);
            }
        }
        mPreAction = action;
    }

    /**
     * Indicates whether launch app after unlock by this view or not.
     * Lockscreen should delay time to unlock until app ready, prevent
     * the screen blink.
     */
    /** @hide */
    public boolean isLaunchApp() {
        return false;
    }

    /**
     * Get app launch delay time.
     * Lockscreen should delay enough time to unlock until app ready, prevent
     * the screen blink.
     */
    /** @hide */
    public long getLaunchDelayTime() {
        long delayTime = 0;
        return delayTime;
    }

    /** @hide */
    public synchronized View startDragView() {
        MyLog.d(TAG, "startDragView");
        int height = getHeight();
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }
        if (height > 0 && lp != null) {
            lp.height = height;
            setLayoutParams(lp);
        }
        // T6_TL_JB43_SENSE55#1390,
        // The specified child already has a paraent...
        // Root Cause:
        // Because we use the same MastHead on the different HostView.
        // Sometime, it existed two HostView.
        // It will encounter the exception issue,
        // If user drag MastHead on two HostView almost at the same time.
        if (mLayoutContainer != null) {
            ViewParent parent = mLayoutContainer.getParent();
            if (parent != null) {
                if (parent != this) {
                    mLayoutContainer.clearAnimation();
                    mLayoutContainer.setVisibility(View.VISIBLE);
                }
                if (parent instanceof ViewGroup) {
                    ((ViewGroup)parent).removeView(mLayoutContainer);
                }
            }
        }
        return mLayoutContainer;
    }

    /** @hide */
    public synchronized void stopDragView() {
        MyLog.d(TAG, "stopDragView");
        if (mLayoutContainer != null) {
            ViewParent parent = mLayoutContainer.getParent();
            mLayoutContainer.clearAnimation();
            mLayoutContainer.setVisibility(View.VISIBLE);
            if (parent != null && parent != this) {
                if (parent instanceof ViewGroup) {
                    ((ViewGroup)parent).removeView(mLayoutContainer);
                }
                this.addView(mLayoutContainer, new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    /**
     * For ViewGroup:
     * All addView(...) will be transfered to
     * addView(view, int, LayoutParams)
     */
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        //MyLog.d(TAG, "addView - child: " + child
        //        + ", index: " + index
        //        + ", mLayoutContainer: " + mLayoutContainer);
        if (child != mLayoutContainer && mLayoutContainer != null) {
            mLayoutContainer.addView(child, index, params);
        } else {
            super.addView(child, index, params);
        }
    }

    @Override
    public void removeAllViews() {
        if (mLayoutContainer != null) {
            mLayoutContainer.removeAllViews();
        } else {
            super.removeAllViews();
        }
    }

    @Override
    public void removeAllViewsInLayout() {
        if (mLayoutContainer != null) {
            mLayoutContainer.removeAllViewsInLayout();
        } else {
            super.removeAllViewsInLayout();
        }
    }

    /** @hide */
    public boolean isPlayUnlockAnimation() {
        return true;
    }

    /** @hide */
    public String getHint() {
        return null;
    }
}
