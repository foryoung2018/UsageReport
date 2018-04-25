package com.htc.lib1.cc.widget.reminder.ui.footer;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Animation.AnimationListener;

import com.htc.lib1.cc.widget.reminder.Const;
import com.htc.lib1.cc.widget.reminder.drag.DragAnimationBase;
import com.htc.lib1.cc.widget.reminder.drag.DraggableView;
import com.htc.lib1.cc.widget.reminder.util.MyUtil;

/** @hide */
public class Sphere extends DraggableView {

    private static final String TAG = "Sphere";

    public Sphere(Context context) {
        super(context);
        init();
    }

    public Sphere(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Sphere(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public boolean isAccessiblityEnabled() {
        return MyUtil.isAccessibilityEnable();
    }

    private void init() {
        // Set Drag Animation for Drop/DragBack/Click Animation.
        this.setDragAnimation(new SphereDragAnimation());
        if (isAccessiblityEnabled()) {
            this.setDragType(DraggableView.DRAG_TYPE_MANUAL_DRAG);
        } else {
            this.setDragType(DraggableView.DRAG_TYPE_ONLY_HORIZONTAL | DraggableView.DRAG_TYPE_ONLY_VERTICAL);
        }
    }

    public void cleanUp() {
    }

    public boolean isShow() {
        return false;
    }

    private class SphereDragAnimation extends DragAnimationBase {

        @Override
        public boolean playDropAnimation(View view, AnimationListener listener, Bundle extras) {
            final View targetView = view;
            final AnimationListener animationLis = listener;
            if (targetView == null && animationLis == null) {
                return false;
            }
            ObjectAnimator fadeoutAnimator = ObjectAnimator.ofFloat(targetView, "myAlpha", 1f, 0f);
            if (fadeoutAnimator == null) {
                return false;
            }
            fadeoutAnimator.setDuration(Const.DEFAULT_DURATION_SPHERE_DROP);
            fadeoutAnimator.addListener(new AnimatorListener() {
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
            fadeoutAnimator.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    if (animator != null) {
                        ViewParent parent = targetView.getParent();
                        if (parent == null) {
                            animator.cancel();
                        } else {
                            float alpha = (Float) animator.getAnimatedValue("myAlpha");
                            targetView.setAlpha(alpha);
                        }
                    }
                }
            });
            fadeoutAnimator.start();
            return true;
        }
    }
}
