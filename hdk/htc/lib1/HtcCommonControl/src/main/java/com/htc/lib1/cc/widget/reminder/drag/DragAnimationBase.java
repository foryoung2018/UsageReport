package com.htc.lib1.cc.widget.reminder.drag;

import android.animation.Animator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Animation.AnimationListener;

/**
 * DragAnimationBase
 */
public class DragAnimationBase extends DragAnimation {

    private static final String TAG  = "DragAnimaBase";
    /** @hide */
    public static final int DEFAULT_DURATION = 400;

    private KeySplineInterpolator mKeySplineInterpolator =
            new KeySplineInterpolator(0.34f, 0.74f, 0f, 1f);


    /**
     * play Drop Animation
     * @param view view
     * @param listener listener
     * @param extras extras
     * @return false
     */
    @Override
    public boolean playDropAnimation(View view, AnimationListener listener, Bundle extras) {
        final View targetView = view;
        final AnimationListener animationLis = listener;
        if (targetView == null || animationLis == null || extras == null) {
            return false;
        }
        int origY = extras.getInt(DragAnimation.KEY_ORIGINAL_Y, 0);
        int viewY = extras.getInt(KEY_DRAGVIEW_TOP, 0);
        int origX = extras.getInt(DragAnimation.KEY_ORIGINAL_X, 0);
        int viewX = extras.getInt(KEY_DRAGVIEW_LEFT, 0);
        int height = extras.getInt(KEY_DRAGVIEW_HEIGHT, 0);
        PropertyValuesHolder pvhMoveX =
                PropertyValuesHolder.ofInt("moveX", viewX - origX, viewX - origX);
        PropertyValuesHolder pvhMoveY =
                PropertyValuesHolder.ofInt("moveY", viewY - origY, - height - origY);
        PropertyValuesHolder pvhAlpha =
                PropertyValuesHolder.ofFloat("myAlpha", 1f, 0f);
        ObjectAnimator dropAnimator =
                ObjectAnimator.ofPropertyValuesHolder(targetView, pvhMoveY, pvhAlpha, pvhMoveX);
        if (dropAnimator == null) {
            return false;
        }
        dropAnimator.setDuration(DEFAULT_DURATION);
        if (mKeySplineInterpolator != null) {
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
                        float alpha = (Float) animator.getAnimatedValue("myAlpha");
                        targetView.setAlpha(alpha);
                        int moveX = (Integer) animator.getAnimatedValue("moveX");
                        int moveY = (Integer) animator.getAnimatedValue("moveY");
                        targetView.scrollTo(-moveX, -moveY);
                    }
                }
            }
        });
        dropAnimator.start();
        return true;
    }

    /**
     * play Drag Back Animation
     * @param view view
     * @param listener listener
     * @param extras extras
     * @return false
     */
    @Override
    public boolean playDragBackAnimation(View view, AnimationListener listener, Bundle extras) {
        final View targetView = view;
        final AnimationListener animationLis = listener;
        if (targetView == null || animationLis == null || extras == null) {
            return false;
        }
        // Translate Animator
        int origX = extras.getInt(KEY_ORIGINAL_X, 0);
        int origY = extras.getInt(KEY_ORIGINAL_Y, 0);
        int viewX = extras.getInt(KEY_DRAGVIEW_LEFT, 0);
        int viewY = extras.getInt(KEY_DRAGVIEW_TOP, 0);

        PropertyValuesHolder pvhMoveX =
                PropertyValuesHolder.ofInt("moveX", viewX - origX, 0);
        PropertyValuesHolder pvhMoveY =
                PropertyValuesHolder.ofInt("moveY", viewY - origY, 0);
        ObjectAnimator backAnimator =
                ObjectAnimator.ofPropertyValuesHolder(targetView, pvhMoveY, pvhMoveX);
        if (backAnimator == null) {
            return false;
        }
        backAnimator.setDuration(DEFAULT_DURATION);
        if (mKeySplineInterpolator != null) {
            backAnimator.setInterpolator(mKeySplineInterpolator);
        }
        backAnimator.addListener(new AnimatorListener() {
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
        backAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                if (animator != null && targetView != null) {
                    ViewParent parent = targetView.getParent();
                    if (parent == null) {
                        animator.cancel();
                    } else {
                        int moveX = (Integer) animator.getAnimatedValue("moveX");
                        int moveY = (Integer) animator.getAnimatedValue("moveY");
                        targetView.scrollTo(-moveX, -moveY);
                    }
                }
            }
        });
        backAnimator.start();
        return true;
    }

    /**
     * play Click Animation
     * @param view view
     * @param listener listener
     * @param extras extras
     * @return false
     */
    @Override
    public boolean playClickAnimation(View view, AnimationListener listener, Bundle extras) {
        final View targetView = view;
        final AnimationListener animationLis = listener;
        if (targetView == null || animationLis == null) {
            return false;
        }
        int[] vibration = null;
        if (extras != null) {
            vibration = extras.getIntArray(KEY_CLICK_VIBRATION);
        }
        if (vibration == null) {
            return false;
        }
        int size = vibration.length;
        PropertyValuesHolder pvhVibration = null;
        int dureation = 462;
        if (size == 15) {
            dureation = 462;
            Keyframe kf0  = Keyframe.ofInt(0.000f, vibration[0]);
            Keyframe kf1  = Keyframe.ofInt(0.071f, vibration[1]);
            Keyframe kf2  = Keyframe.ofInt(0.142f, vibration[2]);
            Keyframe kf3  = Keyframe.ofInt(0.214f, vibration[3]);
            Keyframe kf4  = Keyframe.ofInt(0.285f, vibration[4]);
            Keyframe kf5  = Keyframe.ofInt(0.357f, vibration[5]);
            Keyframe kf6  = Keyframe.ofInt(0.428f, vibration[6]);
            Keyframe kf7  = Keyframe.ofInt(0.500f, vibration[7]);
            Keyframe kf8  = Keyframe.ofInt(0.571f, vibration[8]);
            Keyframe kf9  = Keyframe.ofInt(0.642f, vibration[9]);
            Keyframe kf10 = Keyframe.ofInt(0.714f, vibration[10]);
            Keyframe kf11 = Keyframe.ofInt(0.785f, vibration[11]);
            Keyframe kf12 = Keyframe.ofInt(0.857f, vibration[12]);
            Keyframe kf13 = Keyframe.ofInt(0.928f, vibration[13]);
            Keyframe kf14 = Keyframe.ofInt(1.000f, vibration[14]);
            pvhVibration =
                    PropertyValuesHolder.ofKeyframe("moveY",
                            kf0, kf1, kf2, kf3, kf4, kf5, kf6, kf7, kf8, kf9, kf10, kf11, kf12, kf13, kf14);
        } else if (size == 30) {
            dureation = 1000;
            Keyframe kf0  = Keyframe.ofInt(0.000f, vibration[0]);
            Keyframe kf1  = Keyframe.ofInt(0.034f, vibration[1]);
            Keyframe kf2  = Keyframe.ofInt(0.069f, vibration[2]);
            Keyframe kf3  = Keyframe.ofInt(0.103f, vibration[3]);
            Keyframe kf4  = Keyframe.ofInt(0.138f, vibration[4]);
            Keyframe kf5  = Keyframe.ofInt(0.172f, vibration[5]);
            Keyframe kf6  = Keyframe.ofInt(0.207f, vibration[6]);
            Keyframe kf7  = Keyframe.ofInt(0.241f, vibration[7]);
            Keyframe kf8  = Keyframe.ofInt(0.276f, vibration[8]);
            Keyframe kf9  = Keyframe.ofInt(0.310f, vibration[9]);
            Keyframe kf10 = Keyframe.ofInt(0.345f, vibration[10]);
            Keyframe kf11 = Keyframe.ofInt(0.379f, vibration[11]);
            Keyframe kf12 = Keyframe.ofInt(0.414f, vibration[12]);
            Keyframe kf13 = Keyframe.ofInt(0.448f, vibration[13]);
            Keyframe kf14 = Keyframe.ofInt(0.483f, vibration[14]);
            Keyframe kf15 = Keyframe.ofInt(0.517f, vibration[15]);
            Keyframe kf16 = Keyframe.ofInt(0.552f, vibration[16]);
            Keyframe kf17 = Keyframe.ofInt(0.586f, vibration[17]);
            Keyframe kf18 = Keyframe.ofInt(0.621f, vibration[18]);
            Keyframe kf19 = Keyframe.ofInt(0.655f, vibration[19]);
            Keyframe kf20 = Keyframe.ofInt(0.690f, vibration[20]);
            Keyframe kf21 = Keyframe.ofInt(0.724f, vibration[21]);
            Keyframe kf22 = Keyframe.ofInt(0.759f, vibration[22]);
            Keyframe kf23 = Keyframe.ofInt(0.793f, vibration[23]);
            Keyframe kf24 = Keyframe.ofInt(0.828f, vibration[24]);
            Keyframe kf25 = Keyframe.ofInt(0.862f, vibration[25]);
            Keyframe kf26 = Keyframe.ofInt(0.897f, vibration[26]);
            Keyframe kf27 = Keyframe.ofInt(0.931f, vibration[27]);
            Keyframe kf28 = Keyframe.ofInt(0.966f, vibration[28]);
            Keyframe kf29 = Keyframe.ofInt(1.000f, vibration[29]);
            pvhVibration =
                    PropertyValuesHolder.ofKeyframe("moveY",
                            kf0, kf1, kf2, kf3, kf4, kf5, kf6, kf7, kf8, kf9, kf10, kf11, kf12, kf13, kf14,
                            kf15, kf16, kf17, kf18, kf19, kf20, kf21, kf22, kf23, kf24, kf25, kf26, kf27, kf28, kf29);
        } else {
            return false;
        }
        ObjectAnimator clickAnim =
                ObjectAnimator.ofPropertyValuesHolder(targetView, pvhVibration);
        if (clickAnim == null) {
            return false;
        }
        clickAnim.setDuration(dureation);
        clickAnim.addListener(new AnimatorListener() {
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
        clickAnim.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                if (animator != null && targetView != null) {
                    ViewParent parent = targetView.getParent();
                    if (parent == null) {
                        animator.cancel();
                    } else {
                        int moveY = (Integer) animator.getAnimatedValue();
                        targetView.scrollTo(0, -moveY);
                    }
                }
            }
        });
        clickAnim.start();
        return true;
    }
}
