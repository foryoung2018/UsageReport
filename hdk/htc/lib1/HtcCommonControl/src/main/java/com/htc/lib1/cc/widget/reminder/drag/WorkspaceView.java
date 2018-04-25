package com.htc.lib1.cc.widget.reminder.drag;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.widget.reminder.debug.MyLog;
import com.htc.lib1.cc.widget.reminder.ui.ForegroundContainer;
import com.htc.lib1.cc.widget.reminder.ui.HintView;
import com.htc.lib1.cc.widget.reminder.ui.ReminderView;
import com.htc.lib1.cc.widget.reminder.ui.footer.ReminderPanel;
import com.htc.lib1.cc.widget.reminder.util.MyUtil;

public class WorkspaceView extends RelativeLayout {

    private final static String TAG = "WSView";

    private WorkspaceCtrl mWorkspaceCtrl;
    private DragController mDragController;
    /** @hide */
//  remove masthead ChrisWang-00-[
    private ViewGroup mMasthead;
//  remove masthead ChrisWang-00-]
    /** @hide */
    protected ReminderPanel mReminderPanel;
    private ForegroundContainer mForegroundContainer;
    /** @hide */
    protected LinearLayout mTileContainer;
    private HintView mHintView;
    /** @hide */
    protected View mTwoTileMiddleGapDivider;

    // MastHead Layout Margin Parameters
    private int mMHOriginalMarginLeft;
    private int mMHOriginalMarginRight;
    private int mMHOriginalMarginTop;
    private int mMHOriginalMarginBottom;

    private boolean mWithAnimation = true;

    /**
     * WorkspaceView
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public WorkspaceView(Context context) {
        super(context);
        onInit(context);
    }

    /**
     * WorkspaceView
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs
     */
    public WorkspaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInit(context);
    }

    /**
     * WorkspaceView
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs
     * @param defStyle
     */
    public WorkspaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onInit(context);
    }

    /** @hide */
    public void onInit(Context context) {
        MyUtil.checkAccessibilityEnable(context);
        initView();
    }

    private void initView() {
        try {
            // WorkspaceCtrl: Control the Drag State and then update UI.
            mWorkspaceCtrl = new WorkspaceCtrl(getContext(), this);
            mDragController = (mWorkspaceCtrl != null)?
                    mWorkspaceCtrl.getDragController():null;
            // Disable Multi-Touch across the workspace.
            setMotionEventSplittingEnabled(false);

            // Initial the frame of Reminder View.
            // The layout was included:
            // Tile Container (ForegroundContainer) & Reminder Button.
            Resources res = MyUtil.getResourceFormResApp(getContext());
            LayoutInflater inflater = MyUtil.getLayoutInflaterFromResApp(getContext());
//            int layoutId = MyUtil.getIdFromRes(mContext, ReminderResWrap.LAYOUT_REMINDER_VIEW);
            int layoutId = R.layout.main_lockscreen_reminderview;
            if (inflater != null) {
                inflater.inflate(layoutId, this, true);
            }
//            mReminderPanel = (ReminderPanel) this.findViewById(
//                    MyUtil.getIdFromRes(mContext, ReminderResWrap.ID_REMINDER_PANEL));
            mReminderPanel = (ReminderPanel) this.findViewById(R.id.reminder_panel);
//            mForegroundContainer = (ForegroundContainer) this.findViewById(
//                    MyUtil.getIdFromRes(mContext, ReminderResWrap.ID_FOREGROUND_CONTAINER));
            mForegroundContainer = (ForegroundContainer) this.findViewById(R.id.foreground_container);
//            mMasthead = (Masthead) this.findViewById(
//                    MyUtil.getIdFromRes(mContext, ReminderResWrap.ID_MASTHEAD));
//            remove masthead ChrisWang-00-[
//            mMasthead = (Masthead) this.findViewById(R.id.masthead);
//            if (mMasthead != null) {
//                mMasthead.setClickable(false);
//                mMasthead.setEnableTextSWLayer(true);
//                mMasthead.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
//                mMasthead.changeAnimationState(-1);  // Disable Animation
//                LayoutParams params = (LayoutParams) mMasthead.getLayoutParams();
//                if (params != null) {
//                    mMHOriginalMarginLeft = params.leftMargin;
//                    mMHOriginalMarginRight = params.rightMargin;
//                    mMHOriginalMarginTop = params.topMargin;
//                    mMHOriginalMarginBottom = params.bottomMargin;
//                }
//            }
//          remove masthead ChrisWang-00-]
//            mTileContainer = (LinearLayout) this.findViewById(
//                    MyUtil.getIdFromRes(mContext, ReminderResWrap.ID_TILE_CONTAINER));
            mTileContainer = (LinearLayout) this.findViewById(R.id.tile_container);
//            mHintView = (HintView) this.findViewById(
//                    MyUtil.getIdFromRes(mContext, ReminderResWrap.ID_HINT_VIEW));
            mHintView = (HintView) this.findViewById(R.id.hintview);
            if (mHintView != null) {
                mHintView.initView();
            }
//            mTwoTileMiddleGapDivider = this.findViewById(
//                    MyUtil.getIdFromRes(mContext, ReminderResWrap.ID_TWO_TILE_MIDDLE_GAP));
            mTwoTileMiddleGapDivider = this.findViewById(R.id.two_tile_middle_gap);
            // update UI by Status / Navigation Bar
            updateUIByBar();
        } catch (Exception e) {
            MyLog.w(TAG, "onInit e: " + e.getMessage());
        }
    }

    /**
     * onStart
     */
    @Deprecated
    public void onStart() {
    }

    /**
     * onResume
     */
    @Deprecated
    public void onResume() {
//        mWithAnimation = true;
//        MyUtil.checkAccessibilityEnable(mContext);
    }

    /**
     * onPause
     */
    @Deprecated
    public void onPause() {
//        mWithAnimation = false;
    }

    /**
     * onStop
     */
    @Deprecated
    public void onStop() {
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasWindowFocus);
        MyLog.si(TAG, this+" onWindowFocusChange = "+hasWindowFocus);
        if (hasWindowFocus) {
            MyUtil.checkAccessibilityEnable(getContext());
        }
    }

    /**
     * cleanUp
     */
    public void cleanUp() {
        MyLog.si(TAG, "cleanUp");
        if (mMasthead != null) {
//      remove masthead ChrisWang-00-[
//            mMasthead.stop();
//      remove masthead ChrisWang-00-]
            mMasthead = null;
        }
        if (mWorkspaceCtrl != null) {
            mWorkspaceCtrl.cleanUp();
            mWorkspaceCtrl = null;
        }
        removeAllViews();
    }

    /** @hide */
    public Workspace getWorkspace() {
        return mWorkspaceCtrl;
    }

    /** @hide */
    public void setReminderView(ReminderView view) {
        if (mWorkspaceCtrl != null) {
            mWorkspaceCtrl.setReminderView(view);
        }
    }

    /** @hide */
    public ReminderPanel getReminderPanel() {
        return mReminderPanel;
    }

    public void setMastheadOnTop(ViewGroup mastHead){
        if(mForegroundContainer!= null){
            mMasthead = mastHead;
            RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            if (params != null) {
              mMHOriginalMarginLeft = params.leftMargin;
              mMHOriginalMarginRight = params.rightMargin;
              mMHOriginalMarginTop = params.topMargin;
              mMHOriginalMarginBottom = params.bottomMargin;
            }
            mForegroundContainer.addView(mastHead, params);
            mMasthead.setClickable(false);
            mMasthead.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            relayoutMastHead(isStatusBarTransparent());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mDragController != null) {
            mDragController.handleTouchEvent(event);
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean result = false;
        if (mDragController != null) {
            result = mDragController.onInterceptTouchEvent(event);
        }
        if (!result) {
            result = super.onInterceptHoverEvent(event);
        }
        return result;
    }

    /** @hide */
    public void updateUI4GestureChanged() {
        updateForegroundContainerVisibility();
        updateButtonFotterVisibility();
    }

    private void updateForegroundContainerVisibility() {
        if (mForegroundContainer != null) {
            boolean isDraggingVerticalType = isDraggingVerticalType();
            boolean isDrop = isDrop();
            if (isDraggingVerticalType) {
                int dragState = getDragState();
                int preDragState = getPreDragState();
                if ((dragState == GestureEvent.ACTION_IDLE) &&
                    (preDragState != GestureEvent.ACTION_DROP)) {
                    mForegroundContainer.setVisibilityByAlpha(View.VISIBLE, false);
                } else if (dragState == GestureEvent.ACTION_DRAG_START ||
                    dragState == GestureEvent.ACTION_DRAG_MOVE) {
                    mForegroundContainer.setVisibilityByAlpha(View.INVISIBLE, true);
                } else if ((dragState == GestureEvent.ACTION_DRAG_END && !isDrop) ||
                    (dragState == GestureEvent.ACTION_BACK) ||
                    (dragState == GestureEvent.ACTION_CLICK)) {
                    mForegroundContainer.setVisibilityByAlpha(View.VISIBLE, true);
                }
            }
        }
    }

    private void updateButtonFotterVisibility() {
        if (mReminderPanel != null) {
            int dragState = getDragState();
            boolean isDrop = isDrop();
            if (dragState == GestureEvent.ACTION_DRAG_START ||
                dragState == GestureEvent.ACTION_DRAG_MOVE ||
                (dragState == GestureEvent.ACTION_DRAG_END && isDrop)) {
                mReminderPanel.setFooterVisibility(false, mWithAnimation, 0);
            } else if ((dragState == GestureEvent.ACTION_DRAG_END && !isDrop) ||
                (dragState == GestureEvent.ACTION_BACK) ||
                (dragState == GestureEvent.ACTION_CLICK)) {
                mReminderPanel.setFooterVisibility(true, mWithAnimation, 0);
            }
        }
    }

    private int getDragState() {
        if (mWorkspaceCtrl != null) {
            return mWorkspaceCtrl.getDragState();
        }
        return GestureEvent.ACTION_IDLE;
    }

    private int getPreDragState() {
        if (mWorkspaceCtrl != null) {
            return mWorkspaceCtrl.getPreDragState();
        }
        return GestureEvent.ACTION_IDLE;
    }

    private boolean isDraggingVerticalType() {
        if (mWorkspaceCtrl != null) {
            return mWorkspaceCtrl.isDraggingVerticalType();
        }
        return false;
    }

    private boolean isDrop() {
        if (mWorkspaceCtrl != null) {
            return mWorkspaceCtrl.isDrop();
        }
        return false;
    }

    /** @hide */
    public void updateUnlockHint(DraggableView view) {
        int dragState = getDragState();
        int preDragState = getPreDragState();
        MyLog.d(TAG, "updUnlockHint ds:" + dragState);
        if ((dragState == GestureEvent.ACTION_IDLE &&
            (preDragState == GestureEvent.ACTION_BACK || preDragState == GestureEvent.ACTION_CLICK))) {
            showUnlockHint(view);
        } else if ((dragState == GestureEvent.ACTION_DROP) ||
            (dragState == GestureEvent.ACTION_DRAG_START)) {
            cancelUnlockHint();
        }
    }

    private void cancelUnlockHint() {
         if (mHintView != null) {
             mHintView.cancelUnlockHint();
         }
    }

    private void showUnlockHint(DraggableView currentdragview) {
        if (mHintView != null) {
            setUnlockHint(currentdragview);
            mHintView.showUnlockHint();
        }
    }

    private void setUnlockHint(DraggableView currentdragview) {
        if (mHintView == null) {
            return;
        }
        MyLog.d(TAG, "setUnlockHint: " + currentdragview);
        String unlockhint = "";
        if (currentdragview != null) {
            unlockhint = currentdragview.getHint();
        }
        // Default unlock hint "Pull up to unlock".
        try {
            if (TextUtils.isEmpty(unlockhint)) {
                Resources res = MyUtil.getResourceFormResApp(getContext());
                if (res != null) {
//                    unlockhint = res.getString(
//                            MyUtil.getIdFromRes(mContext, ReminderResWrap.STRING_UNLOCK_HINT_UP));
                    unlockhint = res.getString(R.string.reminderview_common_unlock_hint_up);
                }
            }
        } catch (Exception e) {
            MyLog.w(TAG, "getHint E: " + e);
        }
        mHintView.setNextUnlockHint(unlockhint);
    }

    /**
     * We shouldn't fit system window.
     * Because status / navigation bar are transparent.
     * So, we need the shadow to overlap with them.
     */
// FIXME: Pure Android 4.4 seems no this API.
// So, I mark it firstly.
//    @Override
//    public boolean fitsSystemWindows() {
//        //return super.fitsSystemWindows();
//        return false;
//    }

    @Override
    protected boolean fitSystemWindows(Rect rect) {
        //return super.fitSystemWindows(arg0);
        return false;
    }

    @Override
    public void setFitsSystemWindows(boolean fitSystemWindows) {
        //super.setFitsSystemWindows(fitSystemWindows);
    }

    /**
     * Update UI Position by Status / Navigation Bar Color.
     */
    private void updateUIByBar() {
        //update UI by Status / Navigation Bar
        relayoutMastHead(isStatusBarTransparent());
        relayoutReminderPanel(isNavigationBarTransparent());
    }

    /**
     * Re-layout MastHead by Status Bar Color
     * @param isStatusTransparent
     */
    private void relayoutMastHead(boolean isStatusTransparent) {
//      remove masthead ChrisWang-00-[
        int marginL = mMHOriginalMarginLeft;
        int marginR = mMHOriginalMarginRight;
        int marginT = mMHOriginalMarginTop;
        int marginB = mMHOriginalMarginBottom;
        if (isStatusTransparent) {
            marginT += MyUtil.getStatusbarHeight(getContext());
        }
        if (mMasthead != null) {
            LayoutParams params = (LayoutParams) mMasthead.getLayoutParams();
            if (params != null) {
                params.leftMargin = marginL;
                params.rightMargin = marginR;
                params.topMargin = marginT;
                params.bottomMargin = marginB;
                mMasthead.setLayoutParams(params);
            }
        }
//      remove masthead ChrisWang-00-]
    }

    /**
     * Re-layout ReminderPanel by Navigation Bar Color
     * @param isStatusTransparent
     */
    private void relayoutReminderPanel(boolean isNavigationTransparent) {
        if (mReminderPanel != null) {
            mReminderPanel.relayoutPanel(isNavigationTransparent);
        }
    }

    /** @hide */
    public boolean isStatusBarTransparent() {
        return true;
    }

    /** @hide */
    public boolean isNavigationBarTransparent() {
        return true;
    }

}
