package com.htc.lib1.cc.widget.reminder.drag;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.htc.lib1.cc.widget.reminder.debug.MyLog;
import com.htc.lib1.cc.widget.reminder.ui.ReminderView;
import com.htc.lib1.cc.widget.reminder.ui.ReminderView.ReminderTile;
import com.htc.lib1.cc.widget.reminder.ui.footer.ReminderPanel;
import com.htc.lib1.cc.widget.reminder.ui.footer.Sphere;
import com.htc.lib1.cc.widget.reminder.util.MyUtil;

/** @hide */
public class WorkspaceCtrl extends Workspace {

    private final static String TAG = "WSCtrl";

    private ArrayList<DraggableView> mDraggableList = new ArrayList<DraggableView>();
    private DragController mDragController;
    private WorkspaceView mWorkspaceView;
    private ReminderView mReminderView;

    private MyUIHandler mUIHandler = new MyUIHandler();
    private static final int WHAT_UI_UPDATE_FOR_GESTURE_CHANGE = 1001;

    private int mPreDragState = GestureEvent.ACTION_IDLE;
    private int mDragState = GestureEvent.ACTION_IDLE;
    private boolean mIsDraggingVerticalType = false;
    private boolean mIsDrop = false;

    public WorkspaceCtrl(Context context, WorkspaceView view) {
        onInit(context, view);
    }

    public void onInit(Context context, WorkspaceView view) {
        mWorkspaceView = view;
        mDragController = new DragController(context);
        if (mDragController != null) {
            mDragController.setDragLayer(mWorkspaceView);
        }
        registerGestureCallBack(mWorkSpaceCallBack);
    }

    public void onStart() {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void cleanUp() {
        unregisterGestureCallBack(mWorkSpaceCallBack);
        if (mDragController != null) {
            mDragController.cancelDragging();
        }
        removeAllMessages();
        if (mDraggableList != null) {
            mDraggableList.clear();
            mDraggableList = null;
        }
    }

    /**
     * For Workspace:
     * - bindDragView(DraggableView view)
     * - unbindDragView(DraggableView view)
     * - registerCallBack(GestureCallBack callback)
     * - unregisterCallBack(GestureCallBack callback)
     */
    @Override
    public void bindDragView(DraggableView view) {
        DraggableView draggable = (DraggableView) view;
        if (draggable == null) {
            return;
        }
        if (mDraggableList != null && !mDraggableList.contains(draggable)) {
            mDraggableList.add(draggable);
            MyLog.v(TAG, "bindDragView(" + view + ")");
            draggable.setActionListener(mActionListener);
        }
    }

    @Override
    public void unbindDragView(DraggableView view) {
        DraggableView draggable = (DraggableView) view;
        if (draggable == null) {
            return;
        }
        if (mDraggableList != null && mDraggableList.contains(draggable)) {
            mDraggableList.remove(draggable);
            MyLog.v(TAG, "unbindDragView(" + view + ")");
        }
        draggable.setActionListener(null);
    }

    @Override
    public void registerGestureCallBack(GestureCallBack callback) {
        if (mDragController != null) {
            mDragController.registerGestureCallBack(callback);
        }
    }

    @Override
    public void unregisterGestureCallBack(GestureCallBack callback) {
        if (mDragController != null) {
            mDragController.unregisterGestureCallBack(callback);
        }
    }

    /**
     * For DraggableView.doActionListener:
     * to listen doAction(DraggableView view, int action, Bundle extra)
     */
    private DraggableView.doActionListener mActionListener =
            new DraggableView.doActionListener() {
                @Override
                public Bundle doAction(DraggableView draggableview, int action, Bundle extra) {
                    if (mDragController == null) {
                        return null;
                    }
                    DraggableView curDraggable = mDragController.getCurDraggableView();
                    if (curDraggable == null) {
                        handleAction(draggableview, action);
                    } else {
                        if (mDragController.isClicking()) {
                            mDragController.forceResetDragState();
                            handleAction(draggableview, action);
                        } else {
                            MyLog.w(TAG, "doAction - DraggableView already existed.");
                        }
                    }
                    return null;
                }
        };

    private void handleAction(DraggableView draggableView, int action) {
        DragController dragCtrl = mDragController;
        if (draggableView == null || dragCtrl == null) {
            MyLog.w(TAG, "handleAction Failed.");
            return;
        }
        // Do nothing if ReminderPanel isn't showing.
        if (draggableView instanceof Sphere && !isShowingPanel()) {
            MyLog.w(TAG, "handleAction panel isn't showing.");
            return;
        }
        if (action == DraggableView.DO_ACTION_START_DRAG ||
            action == DraggableView.DO_ACTION_CLICK_ANIMATION) {
            draggableView.clearFocus();
            draggableView.setPressed(false);

            DragAnimation anim = draggableView.getDragAnimation();
            int dragType = draggableView.getDragType();
            // Need to consider the Window Size & WorkspaceView Size.
            int[] viewPosition = new int[2];
            draggableView.getLocationInWindow(viewPosition);
            if (mWorkspaceView != null) {
                int[] wsPosition = new int[2];
                mWorkspaceView.getLocationInWindow(wsPosition);
                viewPosition[0] -= wsPosition[0];
                viewPosition[1] -= wsPosition[1];
                MyLog.v(TAG, "handleAction wsX:" + wsPosition[0] + ", wsY: " + wsPosition[1]);
            }

            int[] viewSize = new int[2];
            View targetView = draggableView.startDragView();
            viewSize[0] = draggableView.getWidth();
            viewSize[1] = draggableView.getHeight();

            if (action == DraggableView.DO_ACTION_START_DRAG) {
                dragCtrl.startDrag(targetView, viewPosition, dragType, anim, draggableView, viewSize);
            } else if (action == DraggableView.DO_ACTION_CLICK_ANIMATION) {
                dragCtrl.playClickAnimation(targetView, viewPosition, dragType, anim, draggableView, viewSize);
            }
        }
    }

    /**
     * MyUIHandler
     * - WHAT_UI_UPDATE_FOR_GESTURE_CHANGE
     */
    private class MyUIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg == null) {
                return;
            }
            switch(msg.what) {
            case WHAT_UI_UPDATE_FOR_GESTURE_CHANGE:
                updateUI4GestureChanged();
                break;
            }
        }
    }

    private void removeAllMessages() {
        MyUtil.removeMessage(mUIHandler, WHAT_UI_UPDATE_FOR_GESTURE_CHANGE);
    }

    private Workspace.GestureCallBack mWorkSpaceCallBack = new Workspace.GestureCallBack() {
        @Override
        public Bundle onGestureChanged(DraggableView view, GestureEvent event,
                Bundle bundle) {
            if (event == null) return null;
            if (updateDragState(event.action)) {
                MyUtil.sendMessage(mUIHandler, WHAT_UI_UPDATE_FOR_GESTURE_CHANGE);
                MyLog.si(TAG, "onGestureChanged: " + mDragState);
                switch (event.action) {
                case GestureEvent.ACTION_TOUCH_DOWN:
                    handleTouchDownEvent();
                    break;
                case GestureEvent.ACTION_DRAG_START:
                    handleDragStartEvent(view, bundle);
                    break;
                case GestureEvent.ACTION_DRAG_END:
                    handleDragEndEvent(view, bundle);
                    break;
                case GestureEvent.ACTION_CLICK:
                    handleClickEvent();
                    break;
                case GestureEvent.ACTION_DROP:
                    updateUnlockHint();
                    break;
                case GestureEvent.ACTION_IDLE:
                    handleIdleEvent(view);
                    break;
                }
            }
            return null;
        }
    };

    private boolean updateDragState(int dragState) {
        mPreDragState = mDragState;
        if (mDragState != dragState) {
            mDragState = dragState;
            return true;
        }
        return false;
    }

    private void handleIdleEvent(DraggableView view) {
        // Drop End
        if (mPreDragState == GestureEvent.ACTION_DROP &&
            mDragState == GestureEvent.ACTION_IDLE) {
            if (view!= null && mReminderView != null) {
                if (view instanceof ReminderTile) {
                    mReminderView.onTileDropEnd((ReminderTile)view);
                }
            }
        }

        updateUnlockHint();
        resetDragStateParams(false);
    }

    private void handleTouchDownEvent() {
        resetDragStateParams(false);
    }

    private void resetDragStateParams(boolean forceUpdate) {
        if ((mDragState == GestureEvent.ACTION_IDLE && mDragState == mPreDragState) ||
            (mDragState == GestureEvent.ACTION_TOUCH_DOWN) ||
            forceUpdate) {
            mIsDraggingVerticalType = false;
            mIsDrop = false;
        }
    }

    private void handleDragStartEvent(DraggableView view, Bundle bundle) {
        mIsDraggingVerticalType = MyUtil.isVerticalDragType(view);
        mIsDrop = false;
        updateUnlockHint();
    }

    private void handleDragEndEvent(DraggableView view, Bundle bundle) {
        boolean isDrop = false;
        if (bundle != null) {
            isDrop = bundle.getBoolean(DraggableView.BUNDLE_KEY_ISDROP, false);
            mIsDrop = isDrop;
        }
        // Only handle Tile here.
        // For Button part, it will handle on ReminderSphere.
        if (mIsDrop && mReminderView != null) {
            if (view instanceof ReminderTile) {
                mReminderView.onTileDrop((ReminderTile)view);
            }
        }
    }

    private void handleClickEvent() {
        updateUnlockHint();
    }

    private void updateUI4GestureChanged() {
        MyUtil.removeMessage(mUIHandler, WHAT_UI_UPDATE_FOR_GESTURE_CHANGE);
        if (mWorkspaceView != null) {
            mWorkspaceView.updateUI4GestureChanged();
        }
    }

    private void updateUnlockHint() {
        if (mWorkspaceView != null) {
            DraggableView view =
                    (mDragController != null)?mDragController.getCurDraggableView():null;
            mWorkspaceView.updateUnlockHint(view);
        }
    }

    public int getDragState() {
        return mDragState;
    }

    public int getPreDragState() {
        return mPreDragState;
    }

    public boolean isDraggingVerticalType() {
        return mIsDraggingVerticalType;
    }

    public boolean isDrop() {
        return mIsDrop;
    }

    public DragController getDragController() {
        return mDragController;
    }

    public void setReminderView(ReminderView view) {
        mReminderView = view;
    }

    private boolean isShowingPanel() {
        ReminderPanel panel = (mWorkspaceView != null)?
                mWorkspaceView.mReminderPanel:null;
        return (panel != null)? panel.isShowing():false;
    }
}
