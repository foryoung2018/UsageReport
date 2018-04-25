package com.htc.lib1.cc.widget.reminder.drag;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.widget.reminder.debug.MyLog;
import com.htc.lib1.cc.widget.reminder.ui.ReminderView.ReminderTile;
import com.htc.lib1.cc.widget.reminder.util.MyUtil;

/** @hide */
public class DragView extends RelativeLayout {

    private static final String TAG  = "DragView";

    private View mCurDraggingView = null;
    private int mDraggingWidth;
    private int mDraggingHeight;
    private int mOriginalViewX;
    private int mOriginalViewY;
    private int mMoveX;
    private int mMoveY;

    private ViewGroup mBasicDragLayer = null;
    private DragAnimation mDragAnimation = null;
    private DragController mDragController;

    private boolean mEnableMoveVertical   = false;
    private boolean mEnableMoveHorizontal = false;

    private UIHandler mUIHandler = new UIHandler();
    private static final int WHAT_ON_ANIMA_CLICK_END = 1051;
    private static final int WHAT_ON_ANIMA_DRAGBACK_END = 1052;
    private static final int WHAT_ON_ANIMA_DROP_END = 1053;
    private static final long ANIMATION_TIMEOUT = 2000; // 2 sec

    // Default value is for FULL-HD.
    private int[] mVibration = {0, -3, -8, -15, -23, -29, -30,
            -21, -6, 3, 0, -3, -3, -2, 0};

    private boolean mCleanUp;

    private Context mContext;

    /**
     * Construct the drag view.
     * <p>
     * The registration point is the point inside our view that the touch events should
     * be centered upon.
     *
     * @param launcher The Launcher instance
     * @param bitmap The view that we're dragging around.  We scale it up when we draw it.
     * @param registrationX The x coordinate of the registration point.
     * @param registrationY The y coordinate of the registration point.
     */
    public DragView(Context context, ViewGroup dragLayer, View targetView,
            int[] draggablePosition, int dragType,
            DragAnimation anim, DragController dragController,
            int[] targetViewSize) {
        super(context);
        mContext = context;
        mCleanUp = false;
        // Click Animation: Vibration.
        Resources res = MyUtil.getResourceFormResApp(mContext);
//        int id = MyUtil.getIdFromRes(mContext, ReminderResWrap.ARRAY_CLICK_VIBRATION);
        int id = R.array.click_vibration;
        if (res != null && id > 0) {
            TypedArray temp = res.obtainTypedArray(id);
            if (temp != null) {
                int size = temp.length();
                mVibration = new int[size];
                for (int i=0; i<size; i++) {
                    mVibration[i] = temp.getDimensionPixelSize(i, 0);
                }
                temp.recycle();
            }
        }
        // Current Dragging View.
        mCurDraggingView = targetView;
        if (targetViewSize != null && targetViewSize.length >= 2) {
            mDraggingWidth = targetViewSize[0];
            mDraggingHeight = targetViewSize[1];
        }
        // DragController.
        mDragController = dragController;
        // DragAnimation for Drop/DragBack Animation.
        mDragAnimation = anim;
        if (mDragAnimation == null) {
            mDragAnimation = new DragAnimationBase();
        }
        // Basic Drag Layer.
        mBasicDragLayer = dragLayer;
        // Drag Type: enable/disable Vertical/Horizontal Move.
        if ((dragType & DraggableView.DRAG_TYPE_ONLY_VERTICAL) > 0) {
            mEnableMoveVertical = true;
        }
        if ((dragType & DraggableView.DRAG_TYPE_ONLY_HORIZONTAL) > 0) {
            mEnableMoveHorizontal = true;
        }
        // Original Draggable View Position for Drag End Animation.
        if (draggablePosition != null && draggablePosition.length>=2) {
            mOriginalViewX = draggablePosition[0];
            mOriginalViewY = draggablePosition[1];
        } else {
            mOriginalViewX = 0;
            mOriginalViewY = 0;
        }
    }

    public boolean isDragVerticalOnly() {
        return mEnableMoveVertical && !mEnableMoveHorizontal;
    }
    /**
     * Create a window containing this view and show it.
     *
     * @param moveX the x moving distance, the user touched in DragLayer coordinates.
     * @param moveY the y moving distance, the user touched in DragLayer coordinates.
     */
    public void show(int moveX, int moveY) {
        MyLog.d(TAG, "show oriX, oriY: " + mOriginalViewX + ", " + mOriginalViewY
                + " mvX, mvY: " + moveX + ", " + moveY);
        if (mBasicDragLayer != null && mCurDraggingView != null) {
            mMoveX = 0;
            mMoveY = 0;
            mBasicDragLayer.addView(this,
                    new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            RelativeLayout.LayoutParams clp
                    = new RelativeLayout.LayoutParams(mDraggingWidth, mDraggingHeight);
            if (clp != null) {
                clp.leftMargin = mOriginalViewX;
                clp.addRule(ALIGN_PARENT_LEFT);
                clp.topMargin = mOriginalViewY;
                addView(mCurDraggingView, clp);
            }
            move(moveX, moveY);
        }
    }

    /**
     * Move the window containing this view.
     *
     * @param moveX the x moving distance, the user touched in DragLayer coordinates.
     * @param moveY the y moving distance, the user touched in DragLayer coordinates.
     */
    int mDragThreshold_y = -1000;
    public void move(int moveX, int moveY) {
        if (mBasicDragLayer != null) {
            // For Call Mode (Tile),
            // Drag Threshold: y can't over status bar.
            if (mDragThreshold_y == -1000 && mDragController != null) {
                mDragThreshold_y = getMoveThreshold(mDragController.getCurDraggableView());
            }

            if (mEnableMoveHorizontal) {
                mMoveX = moveX;
            }
            if (mEnableMoveVertical) {
                //Drag threshold lower than status bar
                if (mDragThreshold_y > 0
                    && (mOriginalViewY + moveY) < mDragThreshold_y) {
                    mMoveY = mDragThreshold_y - mOriginalViewY;
                }
                //Drag threshold higher than status bar
                else if (mDragThreshold_y < 0
                    && (mOriginalViewY + moveY) < mDragThreshold_y) {
                    mMoveY = mDragThreshold_y - mOriginalViewY;
                } else {
                    mMoveY = moveY;
                }
                // Move to down
                if (mMoveY > 0) {
                    mMoveY = 0;
                }
            }
            scrollTo(-mMoveX, -mMoveY);
        }
    }

    /*
     * some tile should limit dragview top
     * Support: Callview
     */
    private int getMoveThreshold(DraggableView draggable) {
        int threshold = 0;
        if (draggable instanceof ReminderTile) {
            ReminderTile tile = (ReminderTile)draggable;
            if (tile != null) {
                threshold = tile.getDragThreshold();
            }
        }
        return threshold;
    }

    public void remove() {
        mCleanUp = true;
        // Clear All Messages.
        clearDragViewAnimation();
        // Remove View from DragLayer.
        post(new Runnable() {
            public void run() {
                if (mBasicDragLayer != null) {
                    MyLog.i(TAG, "removeView");
                    mBasicDragLayer.removeView(DragView.this);
                }
            }
        });
    }

    private void clearDragViewAnimation() {
        this.clearAnimation();
        MyUtil.removeMessage(mUIHandler, WHAT_ON_ANIMA_CLICK_END);
        MyUtil.removeMessage(mUIHandler, WHAT_ON_ANIMA_DRAGBACK_END);
        MyUtil.removeMessage(mUIHandler, WHAT_ON_ANIMA_DROP_END);
    }

   /**
     * onDragBackStart:
     * Play Drag Back Animation when drag end.
     *
     * @param endX
     * @param endY
     */
    public void onDragBackStart(int endX, int endY) {
        clearDragViewAnimation();
        boolean isSuccessful = false;
        if (mDragAnimation != null) {
            Bundle extras = getAnimationBundle(endX, endY);
            AnimationListener listener = new AnimationListener(){
                @Override
                public void onAnimationEnd(Animation arg0) {
                    MyLog.v(TAG, "onDragBackStart: onAnimationEnd");
                    MyUtil.removeMessage(mUIHandler, WHAT_ON_ANIMA_DRAGBACK_END);
                    MyUtil.sendMessage(mUIHandler, WHAT_ON_ANIMA_DRAGBACK_END);
                }
                @Override
                public void onAnimationRepeat(Animation arg0) {
                }
                @Override
                public void onAnimationStart(Animation arg0) {
                }
            };
            isSuccessful = mDragAnimation.playDragBackAnimation(this, listener, extras);
        }
        if (!isSuccessful) {
            MyLog.w(TAG, "onDragBackStart Fail");
            MyUtil.sendMessage(mUIHandler, WHAT_ON_ANIMA_DRAGBACK_END);
        } else {
            MyUtil.sendMessage(mUIHandler, WHAT_ON_ANIMA_DRAGBACK_END, ANIMATION_TIMEOUT);
        }
    }

    /**
     * onDropStart:
     * Play Drop Animation when drop start.
     *
     * @param dropX
     * @param dropY
     */
    public void onDropStart(int endX, int endY) {
        clearDragViewAnimation();
        boolean isSuccessful = false;
        if (mDragAnimation != null) {
            Bundle extras = getAnimationBundle(endX, endY);
            if (extras != null) {
                int statusbarHeight = MyUtil.getStatusbarHeight(getContext());
                extras.putInt("StatusBarHeight", statusbarHeight);
            }
            AnimationListener listener = new AnimationListener(){
                @Override
                public void onAnimationEnd(Animation arg0) {
                    MyLog.v(TAG, "onDropStart: onAnimationEnd");
                    MyUtil.removeMessage(mUIHandler, WHAT_ON_ANIMA_DROP_END);
                    MyUtil.sendMessage(mUIHandler, WHAT_ON_ANIMA_DROP_END);
                }
                @Override
                public void onAnimationRepeat(Animation arg0) {
                }
                @Override
                public void onAnimationStart(Animation arg0) {
                }
            };
            isSuccessful = mDragAnimation.playDropAnimation(this, listener, extras);
        }
        if (!isSuccessful) {
            MyLog.w(TAG, "onDropStart Fail");
            MyUtil.sendMessage(mUIHandler, WHAT_ON_ANIMA_DROP_END);
        } else {
            MyUtil.sendMessage(mUIHandler, WHAT_ON_ANIMA_DROP_END, ANIMATION_TIMEOUT);
        }
    }

    /**
     *  Touch Indicator Animation
     *  Frames:   0/  1/  2/   3/   4/   5/   6/   7/  8/  9/10/ 11/ 12/ 13/14
     *  iPos:     y/y-3/y-8/y-15/y-23/y-29/y-30/y-21/y-6/y+3/ y/y-3/y-3/y-2/ y
     */
    public void onClick(int endX, int endY) {
        clearDragViewAnimation();
        boolean isSuccessful = false;
        if (mDragAnimation != null) {
            Bundle extras = getAnimationBundle(endX, endY);
            if (extras != null) {
                extras.putIntArray(DragAnimation.KEY_CLICK_VIBRATION, mVibration);
            }
            AnimationListener listener = new AnimationListener(){
                @Override
                public void onAnimationEnd(Animation arg0) {
                    MyLog.v(TAG, "onClick: onAnimationEnd");
                    MyUtil.removeMessage(mUIHandler, WHAT_ON_ANIMA_CLICK_END);
                    MyUtil.sendMessage(mUIHandler, WHAT_ON_ANIMA_CLICK_END);
                }
                @Override
                public void onAnimationRepeat(Animation arg0) {
                }
                @Override
                public void onAnimationStart(Animation arg0) {
                }
            };
            isSuccessful = mDragAnimation.playClickAnimation(this, listener, extras);
        }

        if (!isSuccessful) {
            MyLog.w(TAG, "onClick Fail");
            MyUtil.sendMessage(mUIHandler, WHAT_ON_ANIMA_CLICK_END);
        } else {
            MyUtil.sendMessage(mUIHandler, WHAT_ON_ANIMA_CLICK_END, ANIMATION_TIMEOUT);
        }
    }

    private Bundle getAnimationBundle(int endX, int endY) {
        Bundle bundle = new Bundle();
        if (bundle != null) {
            bundle.putInt(DragAnimation.KEY_DRAG_END_X, endX);
            bundle.putInt(DragAnimation.KEY_DRAG_END_Y, endY);
            bundle.putInt(DragAnimation.KEY_ORIGINAL_X, mOriginalViewX);
            bundle.putInt(DragAnimation.KEY_ORIGINAL_Y, mOriginalViewY);
            bundle.putInt(DragAnimation.KEY_DRAGVIEW_WIDTH, mDraggingWidth);
            bundle.putInt(DragAnimation.KEY_DRAGVIEW_HEIGHT, mDraggingHeight);
            bundle.putInt(DragAnimation.KEY_DRAGVIEW_LEFT, mOriginalViewX + mMoveX);
            bundle.putInt(DragAnimation.KEY_DRAGVIEW_TOP, mOriginalViewY + mMoveY);
            bundle.putBoolean(DragAnimation.KEY_DRAG_HORIZONTAL, mEnableMoveHorizontal);
            bundle.putBoolean(DragAnimation.KEY_DRAG_VERTICAL, mEnableMoveVertical);
        }
        return bundle;
    }

    private class UIHandler extends Handler {
        public UIHandler() {
        }
        @Override
        public void handleMessage(Message msg) {
            if (mCleanUp) {
                return;
            }
            int what = (msg != null)? msg.what:-1;
            switch(what) {
            case WHAT_ON_ANIMA_CLICK_END:
                if (mDragController != null) {
                    mDragController.onClickAnimEnd();
                }
                break;
            case WHAT_ON_ANIMA_DRAGBACK_END:
                if (mDragController != null) {
                    mDragController.onDragBackEnd();
                }
                break;
            case WHAT_ON_ANIMA_DROP_END:
                if (mDragController != null) {
                    mDragController.onDropEnd();
                }
                break;
            }
        }
    }

}

