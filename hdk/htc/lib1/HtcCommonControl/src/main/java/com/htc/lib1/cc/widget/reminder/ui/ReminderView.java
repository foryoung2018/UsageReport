package com.htc.lib1.cc.widget.reminder.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.res.HtcResUtil;
import com.htc.lib1.cc.widget.reminder.debug.MyLog;
import com.htc.lib1.cc.widget.reminder.drag.BaseTile;
import com.htc.lib1.cc.widget.reminder.drag.BaseTile.Button;
import com.htc.lib1.cc.widget.reminder.drag.DragAnimation;
import com.htc.lib1.cc.widget.reminder.drag.DraggableView;
import com.htc.lib1.cc.widget.reminder.drag.GestureEvent;
import com.htc.lib1.cc.widget.reminder.drag.WorkspaceView;
import com.htc.lib1.cc.widget.reminder.ui.footer.ReminderPanel;
import com.htc.lib1.cc.widget.reminder.util.MyUtil;

/**
 * ReminderView
 */
public class ReminderView extends WorkspaceView {

    private static final String TAG = "RemiView";

    private ReminderTile mTile;
    private ReminderTile mSubTile;
    private boolean mIsAllcaps = false;
    private boolean mShowTile1 = false;
    private boolean mShowTile2 = false;

    /**
     * ReminderView
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public ReminderView(Context context) {
        super(context);
        onConstruct(context);
    }

    /**
     * ReminderView
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs AttributeSet
     */
    public ReminderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onConstruct(context);
    }

    /**
     * ReminderView
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs AttributeSet
     * @param defStyle int
     */
    public ReminderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onConstruct(context);
    }

    private void onConstruct(Context context) {
        MyLog.si(TAG, "onC:" + this);
        mIsAllcaps = HtcResUtil.isInAllCapsLocale(context);
        mTile = new ReminderTile(context);
        mSubTile = new ReminderTile(context);
        if (mTileContainer != null) {
            LinearLayout.LayoutParams linearLP =
                    new LinearLayout.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            1);
            if (mTile != null) {
                mTileContainer.addView(mTile, linearLP);
                updateTileVisibility(mTile, View.GONE);
            }
            if (mSubTile != null) {
                mTileContainer.addView(mSubTile, linearLP);
                updateTileVisibility(mSubTile, View.GONE);
            }
        }
        setMastHeadVisibility(isShowMastHeadForDefault());
        setReminderView(this);
    }

    /** onStart */
    @Deprecated
    @Override
    public void onStart() {
        super.onStart();
    }

    /** onResume */
    @Deprecated
    @Override
    public void onResume() {
        super.onResume();
    }

    /** onPause */
    @Deprecated
    @Override
    public void onPause() {
        super.onPause();
    }

    /** onStop */
    @Deprecated
    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * cleanUp
     * Release some objects to avoid the leakage issue.
     */
    @Override
    public void cleanUp() {
        super.cleanUp();
    }

    /**
     * Update UI
     */
    public void updateUI() {
        ReminderPanel panel = getReminderPanel();
        if (panel != null) {
            panel.update(getButtons());
        }
    }

    private List<BaseTile.Button> getButtons() {
        List<BaseTile.Button> buttons = new ArrayList<BaseTile.Button>();
        int size = this.getButtonCount();
        if (isSupportButtonSize(size) && buttons != null) {
            for (int i=0; i<size; i++) {
                buttons.add(getButton(i));
            }
        } else {
            MyLog.w(TAG, "getButtons: " + size);
        }
        return buttons;
    }

    private boolean isSupportButtonSize(int size) {
        return (size == 2 || size == 3 || size == 4);
    }

    /**
     * onButtonDrop(button)
     * @param button Button
     */
    public void onButtonDrop(Button button) {
    }

    /**
     * onButtonDropEnd(button)
     * @param button Button
     */
    public void onButtonDropEnd(Button button) {
    }

    /**
     * onTileDrop(tile)
     * @param tile ReminderTile
     */
    public void onTileDrop(ReminderTile tile) {
    }

    /**
     * onTileDropEnd(tile)
     * @param tile ReminderTile
     */
    public void onTileDropEnd(ReminderTile tile) {
    }

    /**
     * onButtonAccessibilityAction(button)
     * @param button Button
     */
    public void onButtonAccessibilityAction(Button button) {
        onButtonDrop(button);
        onButtonDropEnd(button);
    }

    /**
     * onTileAccessibilityAction(tile)
     * @param tile ReminderTile
     */
    public void onTileAccessibilityAction(ReminderTile tile) {
        onTileDrop(tile);
        onTileDropEnd(tile);
    }

    /** @hide */
    public void setTileDragAnimation(DragAnimation anim, int index) {
        /** setTileAnimation(animation, index) */
        ReminderTile tile = getTile(index);
        if (tile != null) {
            tile.setDragAnimation(anim);
        }
    }

    /**
     * setTileDraggable
     * @param isDraggable
     * @param index
     * index: 1 or 2
     */
    public void setTileDraggable(boolean isDraggable, int index) {
        ReminderTile tile = getTile(index);
        if (tile != null) {
            tile.setDragType(isDraggable?
                    DraggableView.DRAG_TYPE_ONLY_VERTICAL:
                    DraggableView.DRAG_TYPE_MANUAL_DRAG);
        }
    }

    /**
     * setReminderTile(layoutId, index)
     * @param resId int
     * @param index int
     * @return ReminderTile
     * index: 1 or 2
     */
    public ReminderTile setReminderTile(int resId, int index) {
        View view = null;
        ReminderTile tile = null;
        MyLog.i(TAG, "setRemiTile: " + index + ", " + resId);

        tile = getTile(index);
        if (tile != null) {
            tile.removeAllViews();
            if (resId <= 0) {
                updateTileVisibility(tile, View.GONE);
            } else {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = (inflater != null)? inflater.inflate(resId, tile, true):null;
                updateTileVisibility(tile, View.VISIBLE);
                if (view != null && view instanceof ReminderTile) {
                    view = getTileLayout((ViewGroup)view);
                }
                updateTileContent(view, tile);
            }
        }

        return tile;
    }

    private View getTileLayout(ViewGroup vGroup) {
        // Reminder Tile
        // -> DraggableView - LayoutConatiner
        // -> ->Tile Layout
        View tile = null;
        if (vGroup != null && (vGroup.getChildCount() >= 1)) {
            ViewGroup container = (ViewGroup) vGroup.getChildAt(0);
            if (container != null && (container.getChildCount() >= 1)) {
                tile = container.getChildAt(0);
            }
        }
        return tile;
    }

    /**
     * setReminderTile(view, index)
     * @param view View
     * @param index int
     * @return ReminderTile
     * index: 1 or 2
     */
    public ReminderTile setReminderTile(View view, int index) {
        MyLog.i(TAG, "setRemiTile: " + index + ", " + view);
        ReminderTile tile = getTile(index);
        if (tile != null) {
            tile.removeAllViews();
            if (view == null) {
                updateTileVisibility(tile, View.GONE);
            } else {
                tile.addView(view);
                updateTileVisibility(tile, View.VISIBLE);
                updateTileContent(view, tile);
            }
        }
        return tile;
    }

    private void updateTileVisibility(ReminderTile tile, int visibility) {
        if (tile == null) {
            return;
        }
        if (tile == mTile) {
            mShowTile1 = (visibility == View.VISIBLE);
        } else if (tile == mSubTile) {
            mShowTile2 = (visibility == View.VISIBLE);
        }
        tile.setVisibility(visibility);
        updateMiddleGapVisibility();
    }

    private void updateTileContent(View view, ReminderTile tile) {
        if (view != null) {
            // Background must set in view for dragging view.
            Resources res = MyUtil.getResourceFormResApp(getContext());
            if (res != null) {
//                    int id = MyUtil.getIdFromRes(
//                            mContext,
//                            ReminderResWrap.DRAWABLE_LOCKSCREEN_PANEL);
                int id = R.drawable.lockscreen_panel;
                try {
                    Drawable bg = res.getDrawable(id);
                    view.setBackground(bg);
                } catch(Exception e) {
                    MyLog.w(TAG, "updateTileContent E: " + e);
                } catch (OutOfMemoryError er) {
                    MyLog.w(TAG, "updateTileContent E: " + er);
                }
            }
        }
    }

    private void updateMiddleGapVisibility() {
        if (mTwoTileMiddleGapDivider == null || mReminderPanel == null) {
            return;
        }
        if (mShowTile1 && mShowTile2) {
            mTwoTileMiddleGapDivider.setVisibility(View.VISIBLE);
            mReminderPanel.updateMiddleGapVisibility(View.VISIBLE);
        } else {
            mTwoTileMiddleGapDivider.setVisibility(View.GONE);
            mReminderPanel.updateMiddleGapVisibility(View.GONE);
        }
    }

    /**
     * getTile(index)
     * @param index int
     * @return ReminderTile
     * index: i or 2
     */
    public ReminderTile getTile(int index) {
        if (index == 1 ) {
            return mTile;
        } else if (index == 2) {
            return mSubTile;
        }
        return null;
    }

    /**
     * getButtonCount()
     * @return index int
     * count: should be 2 or 4.
     */
    public int getButtonCount() {
        return 0;
    }

    /**
     * getButton(index)
     * @param index int
     * @return Button
     * index should be 0, 1, 2, 3.
     */
    public Button getButton(int index) {
        return null;
    }

    /** @hide */
    public boolean fadOutwhenDrop() {
        /** if reminder view doesn't request. Tile always fade out when drop. */
        return true;
    }

    /**
     * over ride this function to add the dragging position limitation.
     * ex. Incall Tile only can drag to dialer Call ID position.
     * @return drag threshold
     */
    @Deprecated
    public int getDragThreshold() {
        return -1000;
    }

    /** @hide */
    public int getDragType() {
        return DraggableView.DRAG_TYPE_ONLY_VERTICAL;
    }

    /** @hide */
    public View startDragView() {
        return null;
    }

    /** @hide */
    public boolean stopDragView() {
        return false;
    }

    /** @hide */
    public void onGestureChanged(GestureEvent event, Bundle extra) {
    }

    /**
     * Check the title if all letter should be uppercase.
     * by com.htc.util.res.HtcResUtil.isInAllCapsLocale(context)
     * @param view TextView
     * @param str Title String
     */
    public void setTitle(TextView view, String str) {
         String text = "";
        if (!TextUtils.isEmpty(str)) {
            text = mIsAllcaps? str.toUpperCase():str;
        }
        MyLog.v(TAG, "setTitle: " + str);
        if (view != null) {
            view.setText(text);
        }
    }

    /**
     * To Show/Hide MastHead.
     * And the default is "show".
     * @return isShow
     */
    @Deprecated
    public boolean isShowMastHeadForDefault() {
        return true;
    }

    /**
     * Set MastHead Visibility
     * @param visible
     */
    @Deprecated
    public void setMastHeadVisibility(boolean visible) {
//      remove masthead ChrisWang-00-[
//        if (mMasthead != null) {
//            int visibility = (visible)? View.VISIBLE:View.INVISIBLE;
//            mMasthead.setVisibility(visibility);
//        }
//      remove masthead ChrisWang-00-]
    }

    /**
     * ReminderTile
     */
    public class ReminderTile extends BaseTile implements View.OnClickListener {
        private boolean mButtonAccessEnable = true;
        private boolean mAccessibilityEnabled = false;

        /**
         * ReminderTile
         * @param context The Context the view is running in, through which it can access the
         *            current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
         */
        public ReminderTile(Context context) {
            super(context);
            mAccessibilityEnabled = isAccessibilityEnable();
            // For using on activity, it should be clickable,
            // Otherwise, it can't be dragging
            // due to DraggableCtl hadn't received the move event.
            setClickable(true);
            if (mAccessibilityEnabled) {
                setDragType(DraggableView.DRAG_TYPE_MANUAL_DRAG);
                setOnClickListener(this);
            } else {
                setDragType(getDragType());
            }
        }

        private boolean mSplite = false;
        private StringBuffer mAccHint;
        /**
         * Add the string for accessibility.
         * @param string string
         */
        public void addStringForAccessibility(String string) {
            synchronized (this) {
                if (!TextUtils.isEmpty(string)) {
                    if (mAccHint == null) {
                        mSplite = false;
                        mAccHint = new StringBuffer(100);
                    }
                    if (mSplite) {
                        mAccHint.append(", ");
                    } else {
                        mSplite = true;
                    }
                    mAccHint.append(string);
                }
            }
        }

        /** Reset the string for accessibility. */
        public void resetStringForAccessibility() {
            synchronized (this) {
                mAccHint = null;
            }
        }

        /** @hide */
        public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
            int type = event.getEventType();
            if (type == AccessibilityEvent.TYPE_VIEW_HOVER_ENTER
                    || type == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
                onAccessibilityEnter();
                String hint = null;
                if (mAccHint != null) {
                    hint = mAccHint.toString();
                }
                if (!TextUtils.isEmpty(hint)) {
                    event.getText().add(hint);
                }
            }
            return true;
        }

        /** @hide */
        public void onAccessibilityEnter() {
            MyLog.d(TAG, "onAccessibilityEnter");
            if (mAccHint != null) {
                announceForAccessibility(mAccHint.toString());
            }
        }

        private boolean isAccessibilityEnable() {
            return MyUtil.isAccessibilityEnable();
        }

        /** @hide */
        public void onButtonDrop(Button button) {
            MyLog.i(TAG, "RemiTile onButtnDrop:" + button);
            super.onButtonDrop(button);
            ReminderView.this.onButtonDrop(button);
        }

        /** @hide */
        public void onButtonDropEnd(Button button) {
            MyLog.i(TAG, "RemiTile onButtonDropEnd:" + button);
            super.onButtonDropEnd(button);
            ReminderView.this.onButtonDropEnd(button);
        }

        /** @hide */
        public int getButtonCount() {
            return ReminderView.this.getButtonCount();
        }

        /** @hide */
        public Button getButton(int index) {
            return ReminderView.this.getButton(index);
        }

        /** @hide */
        protected boolean fadOutwhenDrop() {
            return ReminderView.this.fadOutwhenDrop();
        }

        /** @hide */
        public View startDragView() {
            View view = ReminderView.this.startDragView();
            if (view == null) {
                view = super.startDragView();
            }
            return view;
        }

        /** @hide */
        public void stopDragView() {
            boolean result = ReminderView.this.stopDragView();
            if (!result) {
                super.stopDragView();
            }
        }

        /** @hide */
        public int getDragThreshold() {
            return ReminderView.this.getDragThreshold();
        }

        /**
         * set Hint
         * @param hint string
         */
        public void setHint(String hint) {
            super.setHint(hint);
        }

        /**
         * set DragAnimation
         * @param animation DragAnimation
         */
        public void setDragAnimation(DragAnimation animation) {
            super.setDragAnimation(animation);
        }

        /** @hide */
        public boolean onInterceptHoverEvent(MotionEvent ev) {
            return true;
        }

        /** @hide */
        public void onClick(View v) {
            if (isAccessibilityEnable()) {
                MyLog.i(TAG, "onClick");
                ReminderView.this.onTileAccessibilityAction(this);
            }
        }

        /** @hide */
        public void onButtonAccessibilityAction(Button button) {
            if (isAccessibilityEnable()) {
                ReminderView.this.onButtonAccessibilityAction(button);
            }
        }

        /**
         * set Button Accessibility Enabled
         * @param enable boolean
         */
        public void setButtonAccessibilityEnabled(boolean enable) {
            mButtonAccessEnable = enable;
        }

        /** @hide */
        public boolean isButtonAccessibilityEnabled() {
            return mButtonAccessEnable;
        }

        /** @hide */
         public void setPressed(boolean pressed) {
            /** RminderTile shouldn't show press color */
            // Only make the item can't be pressed when it didn't enable accessibility.
            // Otherwise, the view can't be clicked.
            if (mAccessibilityEnabled) {
                super.setPressed(pressed);
            } else {
                super.setPressed(false);
            }
        }

        /** @hide */
        public void onGestureChanged(GestureEvent event, Bundle extra) {
            super.onGestureChanged(event, extra);
            ReminderView.this.onGestureChanged(event, extra);
        }
    }   // End of ReminderTile.
}
