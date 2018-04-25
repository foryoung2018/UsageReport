
package com.htc.lib1.cc.widget;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewDebug.ExportedProperty;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * @hide
 * @deprecated try level not release*
 */
public class HtcDrawer extends FrameLayout {

    private final static String TAG = "HtcDrawer";

    private FrameLayout mBarView;

    @ExportedProperty(category = "CommonControl")
    private int mBarSize;

    private OnClickListener mOnBarClickListener;

    private View mContentView;

    private ImageView mArrowView;

    private AnimatorSet mAnimatorSet;

    @ExportedProperty(category = "CommonControl")
    private long mDuration = 300;

    @ExportedProperty(category = "CommonControl")
    private static final float SCALE_TIME_MIN = 0.6f;

    @ExportedProperty(category = "CommonControl")
    private static final float SCALE_TIME_MAX = 1.0f;

    @ExportedProperty(category = "CommonControl")
    private float mScaleTimeFrom = SCALE_TIME_MAX;

    @ExportedProperty(category = "CommonControl")
    private float mScaleTimeTo = SCALE_TIME_MAX;

    @ExportedProperty(category = "CommonControl")
    private int mDrawerTranslationRange;

    @ExportedProperty(category = "CommonControl")
    private int mArrowTranslationRange;

    @ExportedProperty(category = "CommonControl")
    private int mGap;

    private static final String TRANSLATION_TYPE_X = "translationX";

    private static final String TRANSLATION_TYPE_Y = "translationY";

    private String mTranslationType = TRANSLATION_TYPE_Y;

    private static final String ROTATION = "rotation";

    @ExportedProperty(category = "CommonControl")
    private boolean mIsOpen = true;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsInitOpen = false;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsToggleFinish = true;

    private Runnable mRunToggle;

    private Mode mMode = Mode.BOTTOM;

    private View mDividerView;

    @ExportedProperty(category = "CommonControl")
    private int mDividerSize;

    /**
     * Position mode of HtcDrawer on parent view.
     */
    public static enum Mode {
        TOP, LEFT, RIGHT, BOTTOM
    }

    public HtcDrawer(Context context) {
        this(context, null);
    }

    public HtcDrawer(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.htcDrawerStyle);
    }

    public HtcDrawer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HtcDrawer,
                defStyleAttr, R.style.HtcDrawer);

        Drawable drawableArrow = a.getDrawable(R.styleable.HtcDrawer_android_childIndicator);
        mArrowView = new ImageView(context);
        mArrowView.setImageDrawable(drawableArrow);

        initBarView();

        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                    int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (null != mContentView) {
                    final int height = getHeight();
                    if (height > 0 && !mIsInitOpen && mIsOpen) {
                        doToggle(false);
                    }
                }
            }
        });

        final Resources res = context.getResources();
        mGap = a.getDimensionPixelSize(R.styleable.HtcDrawer_android_verticalGap,
                res.getDimensionPixelSize(R.dimen.leading));
        mBarSize = a.getDimensionPixelSize(R.styleable.HtcDrawer_android_scrollbarSize,
                res.getDimensionPixelSize(R.dimen.htcdrawer_bar_size));
        mDividerSize = a.getDimensionPixelSize(R.styleable.HtcDrawer_android_dividerHeight,
                res.getDimensionPixelSize(R.dimen.htc_list_item_vertical_divider_width));
        final int dividerColor = a.getColor(R.styleable.HtcDrawer_android_divider,
                res.getColor(R.color.htcdrawer_divider));
        mDividerView = new View(context);
        mDividerView.setBackgroundColor(dividerColor);
        final int backgroundColor = a.getColor(R.styleable.HtcDrawer_android_background,
                res.getColor(R.color.htcdrawer_background));
        setBackgroundColor(backgroundColor);
        a.recycle();
    }

    private void initBarView() {
        mBarView = new FrameLayout(getContext());
        mBarView.setId(android.R.id.toggle);
        mBarView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(true);
                if (null != mOnBarClickListener) {
                    mOnBarClickListener.onClick(mBarView);
                }
            }
        });

        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        mBarView.addView(mArrowView, lp);
    }

    /**
     * Set content view to HtcDrawer.
     *
     * @param view The content view add to drawer and make sure only use this
     *            method to add view to HtcDrawer. Do not use addView or
     *            removeView etc.
     */
    public void setContentView(View view) {
        if (null != view && mContentView != view) {
            mContentView = view;
            setupByMode();
        }
    }

    private void setupByMode() {
        if (null == mContentView) {
            return;
        }

        removeAllViews();

        final FrameLayout.LayoutParams lpBarView;
        final FrameLayout.LayoutParams lpDividerView;
        switch (mMode) {
            case TOP:
                mArrowView.setRotation(0);
                lpBarView = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, mBarSize, Gravity.BOTTOM);
                lpDividerView = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, mDividerSize, Gravity.BOTTOM);
                break;

            case LEFT:
                mArrowView.setRotation(270);
                lpBarView = new FrameLayout.LayoutParams(
                        mBarSize, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.RIGHT);
                lpDividerView = new FrameLayout.LayoutParams(
                        mDividerSize, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.RIGHT);
                break;

            case RIGHT:
                mArrowView.setRotation(90);
                lpBarView = new FrameLayout.LayoutParams(
                        mBarSize, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.LEFT);
                lpDividerView = new FrameLayout.LayoutParams(
                        mDividerSize, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.LEFT);
                break;

            case BOTTOM:
            default:
                mArrowView.setRotation(180);
                lpBarView = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, mBarSize, Gravity.TOP);
                lpDividerView = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, mDividerSize, Gravity.TOP);
                break;

        }

        final ViewGroup.LayoutParams lpContentView = mContentView.getLayoutParams();
        if (null == lpContentView) {
            addView(mContentView);
        } else {
            addView(mContentView, lpContentView);
        }

        addView(mBarView, lpBarView);
        addView(mDividerView, lpDividerView);
    }

    /**
     * To open or close HtcDrawer.
     *
     * @param isAnimation Open or close HtcDrawer whether with animation.
     */
    public void toggle(final boolean isAnimation) {
        if (null == mContentView) {
            return;
        }

        if (null != mRunToggle) {
            removeCallbacks(mRunToggle);
        }

        mRunToggle = new Runnable() {
            @Override
            public void run() {
                if (!mIsToggleFinish) {
                    mAnimatorSet.end();
                }

                doToggle(isAnimation);
                mRunToggle = null;
            }
        };
        post(mRunToggle);
    }

    private void doToggle(boolean isAnimation) {
        initDataForToggle();
        if (isAnimation) {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.addListener(new AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mIsToggleFinish = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setUpWhenTranslationEnd();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    setUpWhenTranslationEnd();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

            final ObjectAnimator drawerAnimatorTranslation = ObjectAnimator.ofFloat(this,
                    mTranslationType,
                    mDrawerTranslationRange);

            final ObjectAnimator arrowAnimatorTranslation = ObjectAnimator.ofFloat(mArrowView,
                    mTranslationType,
                    mArrowTranslationRange);

            final ValueAnimator arrowAnimatorScale = ValueAnimator.ofFloat(mScaleTimeFrom,
                    mScaleTimeTo);
            arrowAnimatorScale.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    final float scale = (Float) animation.getAnimatedValue();
                    mArrowView.setScaleX(scale);
                    mArrowView.setScaleY(scale);
                }
            });

            final ObjectAnimator arrowAnimatorRotation = ObjectAnimator.ofFloat(mArrowView,
                    ROTATION, mArrowView.getRotation() + 180);

            mAnimatorSet.setDuration(mDuration);
            mAnimatorSet.playTogether(drawerAnimatorTranslation, arrowAnimatorTranslation,
                    arrowAnimatorScale,
                    arrowAnimatorRotation);
            mAnimatorSet.start();
        } else {
            if (TRANSLATION_TYPE_Y.equals(mTranslationType)) {
                setTranslationY(mDrawerTranslationRange);
                mArrowView.setTranslationY(mArrowTranslationRange);
            } else {
                setTranslationX(mDrawerTranslationRange);
                mArrowView.setTranslationY(mArrowTranslationRange);
            }
            mArrowView.setScaleX(mScaleTimeTo);
            mArrowView.setScaleY(mScaleTimeTo);
            turnArrow();
            setUpWhenTranslationEnd();
        }
    }

    private int getDrawerTranslationRangeHorizontal() {
        return getHeight() - mBarSize;
    }

    private int getDrawerTranslationRangeVertical() {
        return getWidth() - mBarSize;
    }

    private int getArrowTranslationRangeHorizontal() {
        return (mBarSize - mArrowView.getHeight()) / 2 - mGap;
    }

    private int getArrowTranslationRangeVertical() {
        return (mBarSize - mArrowView.getWidth()) / 2 - mGap;
    }

    private void initDataForToggle() {
        switch (mMode) {
            case TOP:
                mDrawerTranslationRange = -getDrawerTranslationRangeHorizontal();
                mArrowTranslationRange = getArrowTranslationRangeHorizontal();
                mTranslationType = TRANSLATION_TYPE_Y;
                break;

            case BOTTOM:
                mDrawerTranslationRange = getDrawerTranslationRangeHorizontal();
                mArrowTranslationRange = -getArrowTranslationRangeHorizontal();
                mTranslationType = TRANSLATION_TYPE_Y;
                break;

            case LEFT:
                mDrawerTranslationRange = -getDrawerTranslationRangeVertical();
                mArrowTranslationRange = getArrowTranslationRangeVertical();
                mTranslationType = TRANSLATION_TYPE_X;
                break;

            case RIGHT:
                mDrawerTranslationRange = getDrawerTranslationRangeVertical();
                mArrowTranslationRange = -getArrowTranslationRangeVertical();
                mTranslationType = TRANSLATION_TYPE_X;
                break;
        }

        if (mIsOpen) {
            mArrowTranslationRange = 0;
            mScaleTimeFrom = SCALE_TIME_MIN;
            mScaleTimeTo = SCALE_TIME_MAX;
        }
        else {
            mDrawerTranslationRange = 0;
            mScaleTimeFrom = SCALE_TIME_MAX;
            mScaleTimeTo = SCALE_TIME_MIN;
        }

        if (HtcBuildFlag.Htc_DEBUG_flag) {
            Log.d(TAG, "mMode = " + mMode + " , mDrawerTranslationRange = "
                    + mDrawerTranslationRange + " , mArrowTranslationRange = "
                    + mArrowTranslationRange + " , mTranslationType = " + mTranslationType
                    + " , mScaleTimeFrom = " + mScaleTimeFrom + " , mScaleTimeTo = " + mScaleTimeTo);
        }
    }

    private void setUpWhenTranslationEnd() {
        mIsToggleFinish = true;
        mIsOpen = !mIsOpen;
    }

    private void turnArrow() {
        if (null != mArrowView) {
            mArrowView.setRotation(mArrowView.getRotation() + 180);
        }
    }

    /**
     * Set HtcDrawer mode.
     *
     * @param mode See {@link Mode}.
     */
    public void setMode(Mode mode) {
        if (mMode != mode) {
            mMode = mode;
            setupByMode();
        }
    }

    /**
     * Set HtcDrawer whether open or close when first display.
     *
     * @param isInitOpen HtcDrawer whether open or close when first display.
     */
    public void setIsInitOpen(boolean isInitOpen) {
        mIsInitOpen = isInitOpen;
    }

    /**
     * Set a drawable as arrow image
     *
     * @param drawable The arrow drawable.
     */
    public void setArrowImage(Drawable drawable) {
        if (null != drawable) {
            mArrowView.setImageDrawable(drawable);
        }
    }

    /**
     * Checks if is the HtcDrawer open.
     *
     * @return Whether or not the HtcDrawer is open
     */
    public boolean isOpen() {
        return mIsOpen;
    }

    /**
     * Checks if is the HtcDrawer toggling.
     *
     * @return Whether or not the HtcDrawer is toggling
     */
    public boolean isToggleFinish() {
        return mIsToggleFinish;
    }

    /**
     * The length of each of the the animation
     *
     * @param duration The length of the animation, in milliseconds, of each of
     *            the child animations of this AnimatorSet.
     */
    public void setDuration(long duration) {
        mDuration = duration;
    }

    /**
     * The length of each of the the animation
     *
     * @return duration The length of the animation, in milliseconds, of each of
     *         the child animations of this AnimatorSet.
     */
    public long getDuration() {
        return mDuration;
    }

    /**
     * Register a callback to be invoked when bar view is clicked.
     *
     * @param l The callback that will run
     */
    public void setOnBarClickListener(OnClickListener onBarClickListener) {
        mOnBarClickListener = onBarClickListener;
    }

    /**
     * The size of bar view.
     *
     * @return size of bar view
     */
    public int getBarSize() {
        return mBarSize;
    }

}
