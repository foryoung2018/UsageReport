
package com.htc.lib1.cc.widget;

import android.view.View;
import android.view.Surface;
import android.view.Display;
import android.view.ViewGroup;
import android.content.Context;
import android.view.WindowManager;
import android.util.DisplayMetrics;
import android.animation.ObjectAnimator;
import android.content.res.Configuration;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.view.animation.DecelerateInterpolator;
import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.ActionBarUtil;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.res.HtcResUtil;

/**
 * combine dropdown and search module together this module only available on tablet as center view.
 *
 * @hide
 */
public class ActionBarDropDownSearch extends ViewGroup {
    private boolean mIsTablet = false;
    private Display mDefaultDisplay = null;
    private int mMeasureSpecM2;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public ActionBarDropDownSearch(Context context) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        mMeasureSpecM2 = HtcResUtil.getM2(context);

        mDefaultDisplay = null;
        mDefaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();

        if (mDefaultDisplay == null) throw new RuntimeException("default display null");

        // setup the module overall environment
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mActionBarDropDown = null;
        mActionBarDropDown = new ActionBarDropDown(context);

        // setup and add the initially internal module
        addView(mActionBarDropDown);
        updateEnvironment();
    }

    // update overall environment from current orientation
    private void updateEnvironment() {
        int orientation = mDefaultDisplay.getRotation();

        // update the current screen orientation
        mIsPortrait = orientation == Surface.ROTATION_0 || orientation == Surface.ROTATION_180;
        mIsPortrait = mIsTablet ? !mIsPortrait : mIsPortrait;

        // update the internal module visibility
        if (mActionBarSearch != null && mActionBarSearch.getVisibility() != GONE) mActionBarDropDown.setVisibility(mIsPortrait ? GONE : VISIBLE);
        else if (mActionBarDropDown.getVisibility() != VISIBLE) mActionBarDropDown.setVisibility(VISIBLE);
    }

    // record the current screen orientation
    private boolean mIsPortrait = true;

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////

    private ActionBarDropDown mActionBarDropDown = null;

    /**
     * acquire the internal dropdown module.
     *
     * @return ActionBarDropDown view
     * @deprecated [Not use any longer]
     * @hide
     */
    @Deprecated
    public ActionBarDropDown getActionBarDropDown() {
        return mActionBarDropDown;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////

    private ActionBarSearch mActionBarSearch = null;

    /**
     * acquire the internal search module.
     *
     * @return ActionBarSearch view
     * @deprecated [Not use any longer]
     * @hide
     */
    @Deprecated
    public ActionBarSearch getActionBarSearch() {
        // runtime create and initialize to save performacne
        if (mActionBarSearch == null) {
            mActionBarSearch = new ActionBarSearch(getContext());
            mActionBarSearch.setVisibility(GONE);
            addView(mActionBarSearch);
        }

        return mActionBarSearch;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////

    private AnimatorSet mTransitionSet = null;

    // clean current active animator to avoid leak
    private void cleanTransitionAnimator() {
        if (mTransitionSet != null) {
            mTransitionSet.removeAllListeners();
            mTransitionSet = null;
        }
    }

    /**
     * control the search show/hide with animation.
     *
     * @param enable the enabled state of search show/hide animation
     * @deprecated [Not use any longer]
     * @hide
     */
    @Deprecated
    public void setActionBarSearchEnabled(boolean enable) {
        // runtime create and initialize to save performacne
        if (mActionBarSearch == null) {
            mActionBarSearch = new ActionBarSearch(getContext());
            mActionBarSearch.setVisibility(GONE);
            addView(mActionBarSearch);
        }

        // skip to avoid useless operation
        if (mActionBarSearch.getVisibility() == (enable ? VISIBLE : GONE)) return;

        // cancel previous unfinished animation
        if (mTransitionSet != null) mTransitionSet.cancel();

        ObjectAnimator animation1 = null;
        ObjectAnimator animation2 = null;

        if (enable) {
            // setup environment and start intro animation
            animation1 = ObjectAnimator.ofFloat(mActionBarSearch, "alpha", 0f, 1f);
            animation1.setDuration(ANIM_DURATION);

            animation2 = ObjectAnimator.ofFloat(mActionBarSearch, "rotationX", 360f, 0f);
            animation2.setDuration(ANIM_DURATION);

            mTransitionSet = new AnimatorSet();
            mTransitionSet.playTogether(animation1, animation2);
            mTransitionSet.setInterpolator(new DecelerateInterpolator());

            // setup the listener to monitor animation state
            mTransitionSet.addListener(new Animator.AnimatorListener() {
                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                    // change search module visibility before animation
                    mActionBarSearch.setVisibility(VISIBLE);
                    updateEnvironment();
                }

                public void onAnimationEnd(Animator animator) {
                    if (mTransitionSet != null)
                    cleanTransitionAnimator();
                }

                public void onAnimationCancel(Animator animator) {
                    if (mTransitionSet != null)
                    cleanTransitionAnimator();
                }
            });
            mTransitionSet.start();
        } else {
            // setup environment and start outro animation
            animation1 = ObjectAnimator.ofFloat(mActionBarSearch, "alpha", 1f, 0f);
            animation1.setDuration(ANIM_DURATION);

            animation2 = ObjectAnimator.ofFloat(mActionBarSearch, "rotationX", 0f, 360f);
            animation2.setDuration(ANIM_DURATION);

            mTransitionSet = new AnimatorSet();
            mTransitionSet.playTogether(animation1, animation2);
            mTransitionSet.setInterpolator(new DecelerateInterpolator());

            // setup the listener to monitor animation state
            mTransitionSet.addListener(new Animator.AnimatorListener() {
                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                }

                public void onAnimationEnd(Animator animator) {
                    if (mTransitionSet != null)
                    cleanTransitionAnimator();

                    // change search module visibility after animation
                    mActionBarSearch.setVisibility(GONE);
                    updateEnvironment();
                }

                public void onAnimationCancel(Animator animator) {
                    if (mTransitionSet != null)
                    cleanTransitionAnimator();

                    // change search module visibility after animation
                    mActionBarSearch.setVisibility(GONE);
                    updateEnvironment();

                }
            });
            mTransitionSet.start();
        }
    }

    // define module switch animation duration
    private static final int ANIM_DURATION = 300;

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Hide Automatically by SDK Team [U12000].
     *
     * @hide
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // protect module only used on container
        if (!(getParent() instanceof ActionBarContainer)) throw new RuntimeException("only avaiable on container");
    }

    /**
     * Hide Automatically by SDK Team [U12000].
     *
     * @hide
     */
    @Override
    protected void onDetachedFromWindow() {
        // cancel previous unfinished animation
        if (mTransitionSet != null) mTransitionSet.cancel();

        super.onDetachedFromWindow();
    }

    private DisplayMetrics mDisplayMetrics = null;

    // define the two panel design ratio value
    private static final float PANEL_RATIO = 2.5f;

    /**
     * Hide Automatically by SDK Team [U12000].
     *
     * @hide
     */
    @Override
    protected void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);

        // cancel previous unfinished animation
        if (mTransitionSet != null) mTransitionSet.cancel();

        updateEnvironment();
    }

    /**
     * {@inheritDoc} Hide Automatically by SDK Team [U12000].
     *
     * @hide
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int moduleWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int moduleHeight = MeasureSpec.getSize(heightMeasureSpec);

        // measure all child and set current dimension
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(moduleWidth, moduleHeight);
    }

    /**
     * {@inheritDoc}Hide Automatically by SDK Team [U12000].
     *
     * @deprecated [Module internal use]
     * @hide
     */
    @Deprecated
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childWidth;
        int childHeight;

        int childViewL, childViewR;
        int childViewT, childViewB;
        final boolean isLayoutRtl;
        if (ActionBarUtil.IS_SUPPORT_RTL) {
            isLayoutRtl = getLayoutDirection() == LAYOUT_DIRECTION_RTL;
        } else {
            isLayoutRtl = false;
        }

        int currentLeft, currentRight;

        currentLeft = getPaddingLeft();
        currentRight = right - left - getPaddingRight();

        // paddingWidth does not been used.
        // final int paddingWidth=(right-left)-getPaddingLeft()-getPaddingRight();
        final int paddingHeight = (bottom - top) - getPaddingTop() - getPaddingBottom();

        // arrange the action search module layout position
        if (mActionBarSearch != null && mActionBarSearch.getVisibility() != GONE) {
            // portrait mode occupy all avaiable space
            // landscape mode occupy partial avaiable space
            if (mIsPortrait) {
                childWidth = mActionBarSearch.getMeasuredWidth();
                childHeight = mActionBarSearch.getMeasuredHeight();

                int remainderSpace = currentRight - currentLeft;

                // remeasure to occupy current remainder space
                if (childWidth != remainderSpace) {
                    int widthMeasure = MeasureSpec.makeMeasureSpec(remainderSpace, MeasureSpec.EXACTLY);
                    int heightMeasure = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);

                    mActionBarSearch.measure(widthMeasure, heightMeasure);

                    // childWidth=actionSearch.getMeasuredWidth();

                    childHeight = mActionBarSearch.getMeasuredHeight();
                }

                // calculate the child vertical position
                if (childHeight > paddingHeight) {
                    childViewT = getPaddingTop();
                    childViewB = childViewT + paddingHeight;
                } else {
                    childViewT = getPaddingTop() + (paddingHeight - childHeight) / 2;
                    childViewB = childViewT + childHeight;
                }

                // setup the child view layout position
                mActionBarSearch.layout(currentLeft, childViewT, currentRight, childViewB);
                return;
            } else {
                if (mDisplayMetrics == null) mDisplayMetrics = getResources().getDisplayMetrics();

                childWidth = mActionBarSearch.getMeasuredWidth();
                childHeight = mActionBarSearch.getMeasuredHeight();

                // calculate available space for search module
                int screenSpace = mDisplayMetrics.widthPixels;
                int panelSpace = Math.round(screenSpace * PANEL_RATIO / (PANEL_RATIO + 1));

                int containerSpace = ((View) getParent()).getMeasuredWidth();
                int availableSpace = panelSpace - (screenSpace - containerSpace);

                // remeasure to occupy current available space
                if (childWidth != availableSpace) {
                    int widthMeasure = MeasureSpec.makeMeasureSpec(availableSpace, MeasureSpec.EXACTLY);
                    int heightMeasure = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);

                    mActionBarSearch.measure(widthMeasure, heightMeasure);

                    childWidth = mActionBarSearch.getMeasuredWidth();
                    childHeight = mActionBarSearch.getMeasuredHeight();
                }

                // calculate the child vertical position
                if (childHeight > paddingHeight) {
                    childViewT = getPaddingTop();
                    childViewB = childViewT + paddingHeight;
                } else {
                    childViewT = getPaddingTop() + (paddingHeight - childHeight) / 2;
                    childViewB = childViewT + childHeight;
                }

                // calculate the child horizontal position
                if (isLayoutRtl) {
                    childViewL = currentLeft;
                    childViewR = childViewL + childWidth;
                } else {
                    childViewR = currentRight;
                    childViewL = childViewR - childWidth;
                }

                // update the current right most position
                if (isLayoutRtl) {
                    currentLeft = childViewR;
                } else {
                    currentRight = childViewL;
                }

                // setup the child view layout position
                mActionBarSearch.layout(childViewL, childViewT, childViewR, childViewB);
            }
        }

        // arrange the action dropdown module layout
        if (mActionBarDropDown != null && mActionBarDropDown.getVisibility() != GONE) {
            childWidth = mActionBarDropDown.getMeasuredWidth();
            childHeight = mActionBarDropDown.getMeasuredHeight();

            int remainderSpace = currentRight - currentLeft;

            // remeasure to occupy remainder space if need
            if (childWidth > remainderSpace) {
                int widthMeasure = MeasureSpec.makeMeasureSpec(remainderSpace, MeasureSpec.EXACTLY);
                int heightMeasure = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);

                mActionBarDropDown.measure(widthMeasure, heightMeasure);

                childWidth = mActionBarDropDown.getMeasuredWidth();
                childHeight = mActionBarDropDown.getMeasuredHeight();
            }

            // calculate the child vertical position
            if (childHeight > paddingHeight) {
                childViewT = getPaddingTop();
                childViewB = childViewT + paddingHeight;
            } else {
                childViewT = getPaddingTop() + (paddingHeight - childHeight) / 2;
                childViewB = childViewT + childHeight;
            }

            // calculate the child horizontal position
            childViewL = currentLeft;
            childViewR = childViewL + childWidth;

            // setup the child view layout position
            mActionBarDropDown.layout(childViewL, childViewT, childViewR, childViewB);
        }
    }

    private Drawable mChildDivider = null;

    /**
     * {@inheritDoc}Hide Automatically by SDK Team [U12000].
     *
     * @hide
     */
    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        // draw additional divider for search module
        if (mActionBarSearch != null && mActionBarSearch.getVisibility() != GONE && !mIsPortrait) {
            if (mChildDivider == null) mChildDivider = getResources().getDrawable(R.drawable.common_b_div);

            final boolean isLayoutRtl;
            if (ActionBarUtil.IS_SUPPORT_RTL) {
                isLayoutRtl = getLayoutDirection() == LAYOUT_DIRECTION_RTL;
            } else {
                isLayoutRtl = false;
            }
            int leftBound, rightBound;
            if (isLayoutRtl) {
                leftBound = mActionBarSearch.getRight();
            } else {
                leftBound = mActionBarSearch.getLeft();
            }
            rightBound = leftBound + mChildDivider.getIntrinsicWidth();

            // setup the drawable bound and draw to canvas
            mChildDivider.setBounds(leftBound, mMeasureSpecM2, rightBound, getHeight() - mMeasureSpecM2);
            mChildDivider.draw(canvas);
        }
    }
}
