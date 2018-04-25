package com.htc.lib1.cc.widget.reminder.drag;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Animation.AnimationListener;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.widget.reminder.debug.MyLog;
import com.htc.lib1.cc.widget.reminder.util.MyUtil;

/**
 * BaseTile
 */
public class BaseTile extends DraggableView {
    private static final String TAG  = "BaseTile";
    private float mFadeoutAnime[] = {1.0f, 0.8f, 0.6f, 0.4f, 0.2f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    private String mTileHint = null;

    /**
     * Button
     * @author htc
     */
    public static class Button {
        private String   mHint;
        private String   mTitle;
        private Drawable mIcon;
        private int mID;
        private BaseTile mParentTile;
        private boolean mUnlockByButton = false;
        /**
         * Button
         * @param tile BaseTile
         */
        public Button(BaseTile tile) {
            mParentTile = tile;
        }

        /**
         * set Hint
         * @param text String
         */
        public void setHint(String text) {
            MyLog.i(TAG, "setHint: " + text);
            mHint = text;
        }

        /**
         * set Title
         * @param text String
         */
        public void setTitle(String text) {
            MyLog.i(TAG, "setTitle: " + text);
            mTitle = text;
        }

        /**
         * set Icon
         * @param icon Drawable
         */
        public void setIcon(Drawable icon) {
            if (icon != null) {
                MyLog.i(TAG, "setIcon ih: "  + icon.getIntrinsicHeight() + ", iw: " + icon.getIntrinsicWidth());
            } else {
                MyLog.w(TAG, "setIcon: null");
            }
            mIcon = icon;
        }

        /**
         * get Hint
         * @return Hint
         */
        public String getHint() {
            return mHint;
        }

        /**
         * get Title
         * @return Title
         */
        public String getTitle() {
            return mTitle;
        }

        /**
         * get Icon
         * @return Icon
         */
        public Drawable getIcon() {
            return mIcon;
        }

        /**
         * set Id
         * @param id int
         */
        public void setId(int id) {
            MyLog.i(TAG, "setId: " + id);
            mID = id;
        }

        /**
         * get Id
         * @return Id int
         */
        public int getId() {
            return mID;
        }

        /** @hide */
        public BaseTile getParentTile() {
            return mParentTile;
        }

        /** @hide */
        public void onDrop() {
            if (mParentTile != null) {
                mParentTile.onButtonDrop(this);
            }
        }

        /** @hide */
        public void onDropEnd() {
            if (mParentTile != null) {
                mParentTile.onButtonDropEnd(this);
            }
        }
    }

    /**
     * Base Tile
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public BaseTile(Context context) {
        super(context);
        init();
    }

    /**
     * Base Tile
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs AttributeSet
     */
    public BaseTile(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Base Tile
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs AttributeSet
     * @param defStyle default Style
     */
    public BaseTile(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.setDragType(DRAG_TYPE_ONLY_VERTICAL);
        this.setDragAnimation(new DropDragAnimation());
    }

    /**
     *  Lock DragAnimation:
     *  To override playDropAnimation().
     */
    private class DropDragAnimation extends DragAnimationBase {

        private KeySplineInterpolator mKeySplineInterpolator =
                new KeySplineInterpolator(0.34f, 0.74f, 0f, 1f);

        @Override
        public boolean playDropAnimation(View view, AnimationListener listener, Bundle extras) {
            final View targetView = view;
            final AnimationListener animationLis = listener;
            if (targetView == null || animationLis == null || extras == null) {
                return false;
            }
            final boolean fadeout = fadOutwhenDrop();
            int origY = extras.getInt(DragAnimation.KEY_ORIGINAL_Y, 0);
            int viewY = extras.getInt(DragAnimation.KEY_DRAGVIEW_TOP, 0);
            int height = extras.getInt(DragAnimation.KEY_DRAGVIEW_HEIGHT, 0);
            PropertyValuesHolder pvhMoveY =
                    PropertyValuesHolder.ofInt("moveY", viewY - origY, - height - origY);
            PropertyValuesHolder pvhAlpha = fadeout?
            PropertyValuesHolder.ofFloat("myAlpha", mFadeoutAnime):PropertyValuesHolder.ofFloat("myAlpha", 1f, 1f);
            ObjectAnimator dropAnimator =
                    ObjectAnimator.ofPropertyValuesHolder(targetView, pvhMoveY, pvhAlpha);
            if (dropAnimator == null) {
                return false;
            }
            dropAnimator.setDuration(DEFAULT_DURATION);
            //fade out animation without interpolator
            if (mKeySplineInterpolator != null && !fadeout) {
                dropAnimator.setInterpolator(mKeySplineInterpolator);
            }
            dropAnimator.addListener(new AnimatorListener() {
                @Override
                public void onAnimationCancel(Animator animator) {
                    animationLis.onAnimationEnd(null);
                }
                @Override
                public void onAnimationEnd(Animator animator) {
                    animationLis.onAnimationEnd(null);
                }
                @Override
                public void onAnimationRepeat(Animator animator) {
                    animationLis.onAnimationRepeat(null);
                }
                @Override
                public void onAnimationStart(Animator animator) {
                    animationLis.onAnimationStart(null);
                }
            });
            dropAnimator.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    if (animator != null && targetView != null) {
                        ViewParent parent = targetView.getParent();
                        if (parent == null) {
                            animator.cancel();
                        } else {
                            if (fadeout) {
                                float alpha = (Float) animator.getAnimatedValue("myAlpha");
                                targetView.setAlpha(alpha);
                            }
                            int moveY = (Integer) animator.getAnimatedValue("moveY");
                            targetView.scrollTo(0, -moveY);
                        }
                    }
                }
            });
            dropAnimator.start();
            return true;
        }
    }

    /** @hide */
    protected boolean fadOutwhenDrop() {
        return true;
    }

    /** @hide */
    public int getButtonCount() {
        return 0;
    }

    /** @hide */
    public Button getButton(int index) {
        return null;
    }

    /** @hide */
    public void onButtonDrop(Button button) {
    }

    /** @hide */
    public void onButtonDropEnd(Button button) {
    }

    /** @hide */
    public void setHint(String hint) {
        mTileHint = hint;
    }

    /** @hide */
    public String getHint() {
        if (!TextUtils.isEmpty(mTileHint)) {
            return mTileHint;
        }
        String hint = null;
        try {
            Resources res = MyUtil.getResourceFormResApp(getContext());
            if (res != null) {
//                hint = res.getString(
//                        MyUtil.getIdFromRes(mContext, ReminderResWrap.STRING_UNLOCK_HINT_UP));
                hint = res.getString(R.string.reminderview_common_unlock_hint_up);
            }
        } catch (Exception e) {
            MyLog.w(TAG, "getHint E: " + e);
        }
        return hint;
    }
}

