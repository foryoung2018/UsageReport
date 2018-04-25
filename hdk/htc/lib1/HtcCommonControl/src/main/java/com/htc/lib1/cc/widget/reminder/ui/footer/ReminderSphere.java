package com.htc.lib1.cc.widget.reminder.ui.footer;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.res.HtcResUtil;
import com.htc.lib1.cc.widget.reminder.debug.MyLog;
import com.htc.lib1.cc.widget.reminder.drag.BaseTile;
import com.htc.lib1.cc.widget.reminder.drag.BaseTile.Button;
import com.htc.lib1.cc.widget.reminder.drag.DraggableView;
import com.htc.lib1.cc.widget.reminder.drag.GestureEvent;
import com.htc.lib1.cc.widget.reminder.ui.ReminderView.ReminderTile;
import com.htc.lib1.cc.widget.reminder.util.MyUtil;

/** @hide */
public class ReminderSphere extends Sphere implements OnClickListener {

    private static final String TAG = "RemiSphere";

    private ImageView mImageView_Icon = null;
    private TextView  mTextView_Title = null;
    private boolean mInflated = false;
    private boolean mIsAllcaps = false;

    private Button mButton;
    private String mTitle;

    private int mDragState = GestureEvent.ACTION_IDLE;

    /**
     *  The Call Back Functions are from DraggableView.
     */
    private DraggableView.gestureListener mDragListener = new DraggableView.gestureListener() {
        @Override
        public Bundle onGestureChanged(DraggableView view, GestureEvent event,
                Bundle extra) {
            if (event == null) {
                return null;
            }
            int preState = mDragState;
            mDragState = event.action;
            if (mDragState == GestureEvent.ACTION_DROP) {
                if (mButton != null) {
                    mButton.onDrop();
                }
            } else if (preState == GestureEvent.ACTION_DROP && mDragState == GestureEvent.ACTION_IDLE) {
                if (mButton != null) {
                    mButton.onDropEnd();
                }
            }
            return null;
        }
    };

    public ReminderSphere(Context context) {
        super(context);
        init(context);
    }

    public ReminderSphere(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ReminderSphere(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.setClickable(true);
        this.setOnClickListener(this);
        this.setGestureCallbackListener(mDragListener);
    }

    /** @hide */
    public void initView() {
        if (!mInflated) {
            Resources res = MyUtil.getResourceFormResApp(getContext());
//            mImageView_Icon = (ImageView) this.findViewById(
//                    MyUtil.getIdFromRes(mContext, ReminderResWrap.ID_BUTTON_ICON));
            mImageView_Icon = (ImageView) this.findViewById(R.id.buttonpanel_icon);
//            mTextView_Title = (TextView) this.findViewById(
//                    MyUtil.getIdFromRes(mContext, ReminderResWrap.ID_BUTTON_TITLE));
            mTextView_Title = (TextView) this.findViewById(R.id.buttonpanel_title);
            mInflated = true;
            mIsAllcaps = HtcResUtil.isInAllCapsLocale(getContext());
            if (mImageView_Icon == null || mTextView_Title == null) {
                MyLog.w(TAG, "initV iV: " + mImageView_Icon + ", tV: " + mTextView_Title);
            }
        }
    }

    /** @hide */
    public void uninitView() {
        mImageView_Icon = null;
        mTextView_Title = null;
        mInflated = false;
        mButton = null;
    }

    /**
     * Update UI by "BaseTile.Button".
     */
    private void updateUI() {
        if (mButton != null) {
            setIcon(mButton.getIcon());
            setTitle(mButton.getTitle());
        } else {
            MyLog.w(TAG, "updUI: button null");
            setIcon(null);
            setTitle(null);
        }
    }

    /**
     * Set Icon by Drawable.
     * @param icon
     */
    private void setIcon(Drawable icon) {
        if (mImageView_Icon != null) {
            mImageView_Icon.setImageDrawable(icon);
            mImageView_Icon.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Set Title.
     * @param title
     */
    private void setTitle(String title) {
        if (mIsAllcaps && title != null) {
            title = title.toUpperCase();
        }
        if (TextUtils.isEmpty(title)) {
            title = "";
        }
        mTitle = title;
        if (mTextView_Title != null) {
            mTextView_Title.setText(mTitle);
        }
    }

    /** @hide */
    public View startDragView() {
        if (mTextView_Title != null) {
            mTextView_Title.setText(" ");
        }
        return super.startDragView();
    }

    /** @hide */
    public void stopDragView() {
        super.stopDragView();
        String title = "";
        if (mButton != null) {
            title = mButton.getTitle();
        }
        setTitle(title);
    }

    /** @hide */
    public void setButtonInfo(Button button) {
        mButton = button;
        updateUI();
    }

    /** @hide */
    public Button getButtonInfo() {
        return mButton;
    }

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
        boolean isShow = (getAlpha() > 0.0);  //(getVisibility() == View.VISIBLE);
        if (action != mPreAction && action == GestureEvent.ACTION_IDLE) {
            stopDragView();
        }
        if (action == GestureEvent.ACTION_TOUCH_DOWN ||
            action == GestureEvent.ACTION_CLICK) {
            // Hide Draggable View
            if (isShow) {
                //setVisibility(View.INVISIBLE);
                setAlpha((float) 0.0);
            }
        } else if (action == GestureEvent.ACTION_IDLE &&
                (mPreAction == GestureEvent.ACTION_BACK ||
                 mPreAction == GestureEvent.ACTION_CLICK ||
                 mPreAction == GestureEvent.ACTION_IDLE)) {
            // Show Draggable View
            if (!isShow) {
                //setVisibility(View.VISIBLE);
                setAlpha((float) 1.0);
            }
        } else if (action == GestureEvent.ACTION_IDLE &&
                mPreAction == GestureEvent.ACTION_DROP) {
            boolean unlockWhenDrop = this.isUnlockWhenDrop();
            if (!unlockWhenDrop) {
                //setVisibility(View.VISIBLE);
                setAlpha((float) 1.0);
            }
        }
        mPreAction = action;
    }

    /** @hide */
    public boolean isShow() {
        return (mButton != null);
    }

    private boolean isButtonAccessibilityEnabled() {
        if (mButton != null) {
            BaseTile tile = mButton.getParentTile();
            if (tile != null && tile instanceof ReminderTile) {
                ReminderTile reminderTile = (ReminderTile)tile;
                if (reminderTile != null) {
                    return reminderTile.isButtonAccessibilityEnabled();
                }
            }
        }
        return false;
    }

    /** @hide */
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) {
            return true;
        }
        int type = event.getEventType();
        if (type == AccessibilityEvent.TYPE_VIEW_HOVER_ENTER ||
            type == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
            StringBuffer hint = new StringBuffer(100);
            if (hint != null) {
                if (!TextUtils.isEmpty(mTitle)) {
                    hint.append(mTitle)
                        .append(", ");
                }
                Resources res = MyUtil.getResourceFormResApp(getContext());
                if (res != null) {
//                    hint.append(res.getString(
//                            MyUtil.getIdFromRes(mContext,
//                                    ReminderResWrap.STRING_ACCESSIBILITY_TAP_ACTION)));
                    hint.append(res.getString(R.string.accessibility_tap_action));
                }
                MyLog.i(TAG, "onHoverEvent: " + hint);
                List<CharSequence> temp = event.getText();
                if (temp != null) {
                    temp.add(hint);
                }
            }
        }
        return true;
    }

    /** @hide */
    public boolean onInterceptHoverEvent(MotionEvent ev) {
        return true;
    }

    /** @hide */
    public void onHoverChanged(boolean hovered) {
        MyLog.d(TAG, "onHoverChanged:" + hovered);
        super.onHoverChanged(hovered);
    }

    /** @hide */
    public void onClick(View v) {
        if (isButtonAccessibilityEnabled()) {
            MyLog.i(TAG, "onClick");
            if (isAccessibilityEnable()) {
                BaseTile tile = mButton.getParentTile();
                if (tile != null && tile instanceof ReminderTile) {
                    ReminderTile reminderTile = (ReminderTile)tile;
                    if (reminderTile != null) {
                        reminderTile.onButtonAccessibilityAction(mButton);
                    }
                }
            }
        }
    }

    private boolean isAccessibilityEnable() {
        return MyUtil.isAccessibilityEnable();
    }

    /** @hide */
    public String getHint() {
        String hint = null;
        if (mButton != null) {
            hint = mButton.getHint();
            if (!TextUtils.isEmpty(hint)) {
                return hint;
            }
        }
        try {
            Resources res = MyUtil.getResourceFormResApp(getContext());
            if (res != null) {
//                hint = res.getString(
//                        MyUtil.getIdFromRes(mContext, ReminderResWrap.STRING_UNLOCK_HINT_ICON));
                hint = res.getString(R.string.reminderview_common_unlock_hint_icon);
            }
        } catch (Exception e) {
            MyLog.w(TAG, "getHint E: " + e);
        }
        return hint;
    }
}
