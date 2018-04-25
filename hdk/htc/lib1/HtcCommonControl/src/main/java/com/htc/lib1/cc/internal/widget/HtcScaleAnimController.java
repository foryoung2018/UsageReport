
package com.htc.lib1.cc.internal.widget;

import android.view.View;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.animation.DecelerateInterpolator;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.Resources;
import com.htc.lib1.cc.R;

/**
 * A widget can control view to do scale down and scale up animation.
 * @hide
 */
public class HtcScaleAnimController implements View.OnClickListener, View.OnKeyListener, View.OnTouchListener,
    OnLayoutChangeListener, Animator.AnimatorListener{
    public interface AnimationListener {
        public void onScaleUpAnimationEnd();

        public void onDrawMultiplyContent(Canvas canvas);

        public boolean isEnableAnimation();
    }

    AnimationListener mListener;
    View mActor;
    private int mColor;
    private OnClickListener mOnClickListener;
    private OnClickListener mAnimationEndOnClickListener = null;
    Drawable mFocusIndicator;

    public HtcScaleAnimController(View view) {
        this(view, null);
    }

    public HtcScaleAnimController(View view, AnimationListener listener) {
        this(view, 0, listener);

        initColor();
    }

    public HtcScaleAnimController(View view, int color,
            AnimationListener listener) {
        if ( null == view )
            throw new UnsupportedOperationException("HtcScaleAnimController(null) is not supported in HtcScaleAnimController");

        setActor(view);
        setColor(color);
        setListener(listener);

        this.setupAnimationEnv();
    }

    private void initColor() {
        if ( null == mActor )
            return ;

        Context c = mActor.getContext();
        if ( null == c )
            return ;

        TypedArray a = c.obtainStyledAttributes(null, R.styleable.ThemeColor, com.htc.lib1.cc.R.attr.skin_color, 0);
        int color = a.getColor(R.styleable.ThemeColor_overlay_color, Integer.MIN_VALUE);
        a.recycle();

        setColor(color);
    }

    public void setColor(int color) {
        mColor = color;

        getFocusIndicator(mActor, mColor);
    }

    /* @hide */
    public Drawable getFocusIndicator() {
        return mFocusIndicator;
    }

    private Drawable getFocusIndicator(View actor, int color) {
        if ( null == mFocusIndicator ) {
            if ( null == actor )
                return null;

            Context c = actor.getContext();
            if ( null == c )
                return null;

            Resources r = c.getResources();
            if ( null == r )
                return null;

            mFocusIndicator = r.getDrawable(com.htc.lib1.cc.R.drawable.common_focused);
            if ( null == mFocusIndicator )
                return null;

            mFocusIndicator.mutate();
        }

        mFocusIndicator.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));

        return mFocusIndicator;
    }

    public void setListener(AnimationListener listener) {
        mListener = listener;
    }

    public void setActor(View v) {
        mActor = v;
        mActor.setOnTouchListener(this);

        mActor.addOnLayoutChangeListener(this);
    }

    public void setViewScale(float ratio) {
        if (null != mActor) {
            mActor.setScaleX(ratio);
            mActor.setScaleY(ratio);
        }
    }

    public boolean isScaleUpAnimRuning() {
        if ((null != mScaleUpAnimator) && (mScaleUpAnimator.isRunning())) {
            return true;
        }

        return false;
    }

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }
    // define the press/release animation duration
    private final int animDuration = 60;
    // define the animator percent
    private static float SIZE_90_PERCENT = 0.9f;
    private static float SIZE_100_PERCENT = 1.0f;

    private ObjectAnimator mScaleUpAnimator = null;
    private ObjectAnimator mScaleDownAnimator = null;

    private boolean mIsPostDelayMultiplyFalse = true;
    // create the overall animation environment
    public void setupAnimationEnv() {

        // skip to avoid useless operation
        if (mScaleUpAnimator != null && mScaleDownAnimator != null)
            return;

        // setup the release animation environment
        mScaleUpAnimator = ObjectAnimator.ofFloat(this, "viewScale", SIZE_90_PERCENT,
                SIZE_100_PERCENT);
        mScaleUpAnimator.setInterpolator(new DecelerateInterpolator());
        mScaleUpAnimator.setDuration(animDuration / 2);
        mScaleUpAnimator.addListener(this);

        // setup the press animation environment
        mScaleDownAnimator = ObjectAnimator.ofFloat(this, "viewScale", SIZE_100_PERCENT,
                SIZE_90_PERCENT);
        mScaleDownAnimator.setInterpolator(new DecelerateInterpolator());
        mScaleDownAnimator.setDuration(animDuration);

        mScaleDownAnimator.addListener(new Animator.AnimatorListener() {
            public void onAnimationCancel(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }

            public void onAnimationEnd(Animator animation) {
            }

            public void onAnimationStart(Animator animation) {
                setInternalMultiplyForceEnabled(true);
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (null != view ) {
            if (view.isClickable() == false || view.isEnabled() == false)
                return false;
        }

        if ( null == mListener || false == mListener.isEnableAnimation() )
            return false;

        if ( null == mScaleUpAnimator || null == mScaleDownAnimator )
            return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mScaleUpAnimator.cancel();
                mScaleDownAnimator.start();
                break;

            case MotionEvent.ACTION_UP:
                mScaleDownAnimator.cancel();
                mScaleUpAnimator.start();
                break;

            case MotionEvent.ACTION_CANCEL:
                mScaleDownAnimator.cancel();
                mScaleUpAnimator.cancel();
                // three finger gesture protection-begin
                setViewScale(SIZE_100_PERCENT);
                setInternalMultiplyForceEnabled(false);
                // three finger gesture protection-end
                break;
        }
        return false;
    }

    private boolean forceMultiply = false;

/**
 * external usage to force enable multiply
 * @param enable the enabled state of multiply
 */
    public void setMultiplyForceEnabled(boolean enable) {
        if (null != mActor) {
            if (forceMultiply != enable) {
                forceMultiply = enable;
                mActor.invalidate();
            }
        }
    }

    private boolean internalForceMultiply = false;

    // internal usage to force enable multiply on animation
    private void setInternalMultiplyForceEnabled(boolean enable) {
        if (null != mActor) {
            if (internalForceMultiply != enable) {
                internalForceMultiply = enable;
                mActor.invalidate();
            }
        }
    }

    public void onDraw(Canvas canvas) {
        if ( null == canvas )
        if ( null == mListener || null == canvas ) {
            return ;
        }

        /* mListener disable animation and only draw the original content */
        if ( false == mListener.isEnableAnimation() ) {
            mListener.onDrawMultiplyContent(canvas);
            return ;
        }

        if ( null == mActor )
            return ;

        /* In D-pad support, mActor has the focus and draw original content and mFocusIndicator */
        if ( mActor.hasFocus() && null != mFocusIndicator ) {
            mListener.onDrawMultiplyContent(canvas);
            canvas.save();
            mFocusIndicator.setBounds(canvas.getClipBounds());
            mFocusIndicator.draw(canvas);
            canvas.restore();
//            printCallStack();
            return;
        }

        // enable color multiply when scale is changed or force enable
        if (internalForceMultiply) {
            // according to clockwork suggestion to modify
            int canvasCount;// =0;

            // save canvas and draw the original content to canvas
            canvasCount = canvas.saveLayer(mActor.getScrollX(),
                    mActor.getScrollY(),
                    mActor.getScrollX() + mActor.getWidth(),
                    mActor.getScrollY() + mActor.getHeight(), null,
                    Canvas.ALL_SAVE_FLAG);

            mListener.onDrawMultiplyContent(canvas);

            // draw the multiply color to canvas and restore canvas
            canvas.drawColor(mColor, PorterDuff.Mode.SRC_ATOP);
            canvas.restoreToCount(canvasCount);
            return ;
        }

        mListener.onDrawMultiplyContent(canvas);
    }

 //   private void printCallStack(){
 //       StackTraceElement[] elements = Thread.currentThread().getStackTrace();
 //       for(int i=3, len=elements.length; i<len; i++) {
 //           android.util.Log.e("HtcScaleAnimController", elements[i].toString());
 //       }
 //   }


    /**
     * {@inheritDoc}
     */
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right,
            int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        Rect r = new Rect(left, top, right, bottom);
        mFocusIndicator.setBounds(0, 0, r.width(), r.height());
    }

    @Override
    public void onClick(View v) {
        if ( null != mScaleUpAnimator && (mScaleUpAnimator.isRunning() || mScaleUpAnimator.isStarted()) ) {
            mAnimationEndOnClickListener = mOnClickListener;
        } else {
            if ( null != mOnClickListener )
                mOnClickListener.onClick(v);
        }
    }

    /** control if setInternalMultiplyForceEnabled is called immediately if true
     * @param isDelay It will call setInternalMultiplyForceEnabled by postDelay after animation end if true. Default is true.
     **/
    public void setPostDelayMultiplyFalse(boolean isDelay) {
        mIsPostDelayMultiplyFalse = isDelay;
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        setInternalMultiplyForceEnabled(false);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if ( mIsPostDelayMultiplyFalse  ) {
            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable(){
                public void run(){
                    setInternalMultiplyForceEnabled(false);
                }
            }, 10);
        } else {
            setInternalMultiplyForceEnabled(false);
        }


        if (null != mListener) {
            mListener.onScaleUpAnimationEnd();
        }

        if ( null != mAnimationEndOnClickListener ) {
            mAnimationEndOnClickListener.onClick(mActor);
            mAnimationEndOnClickListener = null;
        }
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    @Override
    public void onAnimationStart(Animator animation) {
    }
}

