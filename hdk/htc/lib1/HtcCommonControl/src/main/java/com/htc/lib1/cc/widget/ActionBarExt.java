
package com.htc.lib1.cc.widget;

import java.lang.ref.WeakReference;

import android.view.View;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup;
import android.view.Window;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.animation.Animation;
import android.graphics.drawable.Drawable;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.DecelerateInterpolator;

import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.cc.util.ActionBarUtil;

/**
 * An Extension of action bar. This class is used for config default action to HTC style.
 */

public class ActionBarExt {
    static final boolean ENABLE_DEBUG = false;

    /**
     * set the actionbar's background to default. use with
     * {@link ActionBarUtil#getActionBarBackground(Context, int)}.
     *
     * @hide
     */
    public static final int MODE_DEFAULT = 0;
    /**
     * set the actionbar's background to transparent. use with.
     * {@link ActionBarUtil#getActionBarBackground(Context, int)}
     *
     * @hide
     */
    public static final int MODE_TRANSPARENT = 1;
    /**
     * set the actionbar's background to gradient_transparent. use with.
     * {@link ActionBarUtil#getActionBarBackground(Context, int)}
     *
     * @hide
     */
    public static final int MODE_GRADIENT_TRANSPARENT = 2;

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = MODE_DEFAULT, to = "MODE_DEFAULT"),
            @IntToString(from = MODE_TRANSPARENT, to = "MODE_TRANSPARENT"),
            @IntToString(from = MODE_GRADIENT_TRANSPARENT, to = "MODE_GRADIENT_TRANSPARENT")
    })
    private int mBackgroundMode = MODE_DEFAULT;

    private Drawable mBackgroundDrawable = null;

    WeakReference<Window> mWindow = null;
    private WeakReference<ActionBar> mActionBar = null;
    private WeakReference<ActionBarContainer> mSearchContainer = null;
    private WeakReference<ActionBarContainer> mCustomContainer = null;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param window The Window the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param iactionBar activity action bar
     */
    public ActionBarExt(Window window, ActionBar iactionBar) {
        // check the consructor argument
        if (window == null || iactionBar == null || null == window.getContext() || null == window.getContext().getResources()) throw new RuntimeException("invalid null argument");

        mWindow = new WeakReference<Window>(window);
        mActionBar = new WeakReference<ActionBar>(iactionBar);
        enableHTCActionBar();

        // acquire the internal actionbar view
        if (mActionBar != null && getActionBarView() == null) {
            View containerView = getContainerView();
            // setup the internal action bar container and enable animation
            if (containerView != null && containerView.getVisibility() == View.INVISIBLE) containerView.setVisibility(View.VISIBLE);
        }

        if (getActionBarView() == null) throw new RuntimeException("actionbar internal view null");

        mBackgroundDrawable = ActionBarUtil.getActionBarBackground(getContext(), MODE_DEFAULT);

        // force reload and setup the background
        adjustActionBar();
    }

    /* @hide */
    public ActionBarExt(Dialog dialog, ActionBar iactionBar) {
        this(((null != dialog) ? dialog.getWindow() : null), iactionBar);
    }

    /* @hide */
    public ActionBarExt(Activity activity, ActionBar iactionBar) {
        this(((null != activity) ? activity.getWindow() : null), iactionBar);
    }

    private void enableHTCActionBar() {
        // setup the overall environment
        ActionBar ab = mActionBar.get();
        if (null == ab) return;

        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowCustomEnabled(true);

        if (getContainerView() != null) getContainerView().setBackground(null);
        ab.setBackgroundDrawable(null);
    }

    /**
     * Acquire the internal search container.
     *
     * @return search container
     */
    public ActionBarContainer getSearchContainer() {
        ActionBarContainer abc = (null == mSearchContainer || null == mSearchContainer.get()) ? null : mSearchContainer.get();

        if (null == abc) {
            if (mCommonHeight < 0) mCommonHeight = ActionBarUtil.getActionBarHeight(getContext(), false);

            Context context = getContext();
            if (context == null) throw new RuntimeException("window context is null");

            abc = new ActionBarContainer(context);
            mSearchContainer = new WeakReference<ActionBarContainer>(abc);

            // setup the search container environment
            abc.setVisibility(View.GONE);
            abc.setTag("searchContainer");
            abc.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mCommonHeight));

            ViewGroup containerView = getContainerView();
            if (containerView != null) {
                containerView.addView(abc, 1);
            }
        }

        if (ENABLE_DEBUG) android.util.Log.i("HTCActionBar", "getSearchContainer():" + abc);

        return abc;
    }

    /**
     * Acquire the internal custom container.
     *
     * @return custom container
     */
    public ActionBarContainer getCustomContainer() {
        ActionBar ab = (null == mActionBar || null == mActionBar.get()) ? null : mActionBar.get();
        if (null == ab) return null;

        ActionBarContainer abc = (null == mCustomContainer || null == mCustomContainer.get()) ? null : mCustomContainer.get();

        // runtime create and initialize container
        if (null == abc) {
            // avoid duplicate create custom container
            if (ab.getCustomView() instanceof ActionBarContainer) {
                abc = (ActionBarContainer) ab.getCustomView();
                mCustomContainer = new WeakReference<ActionBarContainer>(abc);

                if (ENABLE_DEBUG) android.util.Log.i("HTCActionBar", "getCustomContainer():" + abc);

                return abc;
            }

            if (mCommonHeight < 0) mCommonHeight = ActionBarUtil.getActionBarHeight(getContext(),
                    false);

            Context context = getContext();
            if (context == null) throw new RuntimeException("window context is null");

            abc = new ActionBarContainer(context);
            mCustomContainer = new WeakReference<ActionBarContainer>(abc);
            abc.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mCommonHeight));

            // setup and replace previous actionbar custom view
            // actionBar null check was done by constructor, we don't check again.
            if (abc != null) ab.setCustomView(abc);
        }

        if (ENABLE_DEBUG) android.util.Log.i("ActionBar", "getCustomContainer():" + abc);

        return abc;
    }

    /**
     * set action bar to full screen mode.
     *
     * @param enable the enabled state of fullscreen mode when enable is true the background is
     *            gradient transparent when enable is false the background is slid color if you use
     *            both setTransparentEnabled(enable) and setFullScreenEnabled(enable) the background
     *            is the second method result such as :
     *            setTransparentEnabled(false);setFullScreenEnabled(true); the background is
     *            gradient transparent. such as : setFullScreenEnabled(true)
     *            ;setTransparentEnabled(false); the background is slid color.
     * @deprecated use {@link #setBackgroundDrawable(Drawable)} instead.
     */
    public void setFullScreenEnabled(boolean enable) {
        if (enable) {
            setBackgroundDrawable(ActionBarUtil.getActionBarBackground(getContext(), MODE_GRADIENT_TRANSPARENT));
        } else {
            setBackgroundDrawable(ActionBarUtil.getActionBarBackground(getContext(), MODE_DEFAULT));
        }

        if (ENABLE_DEBUG) android.util.Log.i("HTCActionBar", "setFullScreenEnabled():" + enable);
    }

    /**
     * set action bar to transparent mode.
     *
     * @param enable the enabled state of transparent mode when enable is true the background is
     *            gradient transparent when enable is false the background is slid color if you use
     *            both setTransparentEnabled(enable) and setFullScreenEnabled(enable) the background
     *            is the second method result such as :
     *            setTransparentEnabled(true);setFullScreenEnabled(false); the background is slid
     *            color such as : setFullScreenEnabled(false) ;setTransparentEnabled(true); the
     *            background is gradient transparent.
     * @deprecated use {@link #setBackgroundDrawable(Drawable)} instead.
     */
    public void setTransparentEnabled(boolean enable) {
        if (enable) {
            setBackgroundDrawable(ActionBarUtil.getActionBarBackground(getContext(), MODE_TRANSPARENT));
        } else {
            setBackgroundDrawable(ActionBarUtil.getActionBarBackground(getContext(), MODE_DEFAULT));
        }

        if (ENABLE_DEBUG) android.util.Log.i("HTCActionBar", "setTransparentEnabled():" + enable);
    }

    private void updateBackgroundDrawable(View updateView) {
        // skip to avoid special corner case
        if (updateView == null) return;

        // check reload drawable from resource
        updateView.setBackground(mBackgroundDrawable);
        updateView.setPadding(0, 0, 0, 0);
        updateView.invalidate();
    }

    private int mCommonHeight = Integer.MIN_VALUE;

    // record the current animation view
    private View mAnimationOutView = null;
    private View mAnimationInView = null;

    private AnimationSet mAnimationInSet = null;
    private AnimationSet mAnimationOutSet = null;

    private static final int ANIM_DURATION = 500;

    // setup the switch animation environment
    private void setupAnimationEnv() {
        Animation animation1 = null;
        Animation animation2 = null;

        if (mCommonHeight < 0) mCommonHeight = ActionBarUtil.getActionBarHeight(getContext(), false);

        // skip to avoid useless operation
        if (mAnimationInSet == null || mAnimationOutSet == null) {
            // setup the animation out environment
            animation1 = new TranslateAnimation(
                    TranslateAnimation.ABSOLUTE, 0, TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, 0, TranslateAnimation.ABSOLUTE, -mCommonHeight);
            animation1.setInterpolator(new DecelerateInterpolator(2.5f));

            animation2 = new AlphaAnimation(1.0f, 0.0f);
            animation2.setInterpolator(new DecelerateInterpolator(2.5f));

            mAnimationOutSet = new AnimationSet(true);
            mAnimationOutSet.addAnimation(animation1);
            mAnimationOutSet.addAnimation(animation2);
            mAnimationOutSet.setDuration(ANIM_DURATION);
            mAnimationOutSet.setInterpolator(new DecelerateInterpolator(2.5f));

            // setup the listen to change relative state
            mAnimationOutSet.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    if (mAnimationOutView != null)
                    mAnimationOutView.setVisibility(View.VISIBLE);
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    if (mAnimationOutView != null)
                    mAnimationOutView.setVisibility(View.GONE);
                }

            });

            // setup the animation in environment
            animation1 = new TranslateAnimation(
                    TranslateAnimation.ABSOLUTE, 0, TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, -mCommonHeight, TranslateAnimation.ABSOLUTE, 0);
            animation1.setInterpolator(new DecelerateInterpolator(2.5f));

            animation2 = new AlphaAnimation(0.0f, 1.0f);
            animation2.setInterpolator(new DecelerateInterpolator(2.5f));

            mAnimationInSet = new AnimationSet(true);
            mAnimationInSet.addAnimation(animation1);
            mAnimationInSet.addAnimation(animation2);
            mAnimationInSet.setDuration(ANIM_DURATION);
            mAnimationInSet.setInterpolator(new DecelerateInterpolator(2.5f));

            // setup the listen to change relative state
            mAnimationInSet.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    if (mAnimationInView != null)
                    mAnimationInView.setVisibility(View.VISIBLE);
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                }
            });
        }
    }

    private static final int SHOW_DEFAULT = 0;
    private static final int SHOW_SEARCH = 1;
    private int mContainerState = SHOW_DEFAULT;
    private int mOutFocusability = 0;

    /**
     * apply switch animation between two container.
     */
    public void switchContainer() {
        // skip when one of container not available
        if (getActionBarView() == null || mSearchContainer == null || null == mSearchContainer.get()) return;

        // execute only the first time usage
        setupAnimationEnv();

        // cancel previous unfinished animation
        mAnimationInSet.cancel();
        mAnimationOutSet.cancel();

        // setup the positive and nagative view
        if (mContainerState == SHOW_DEFAULT) {
            mContainerState = SHOW_SEARCH;
            mAnimationOutView = getActionBarView();
            mAnimationInView = getSearchContainer();
            if (mOutFocusability != 0) getSearchContainer().setDescendantFocusability(mOutFocusability);
        } else {
            mContainerState = SHOW_DEFAULT;
            mAnimationOutView = getSearchContainer();
            mAnimationInView = getActionBarView();
            ActionBarContainer abc = getSearchContainer();
            if (null != abc) {
                int descendant = abc.getDescendantFocusability();
                if (descendant != ViewGroup.FOCUS_BLOCK_DESCENDANTS) mOutFocusability = descendant;
                abc.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                abc.clearFocus();
            }
        }

        if (mAnimationInView != null) mAnimationInView.startAnimation(mAnimationInSet);
        if (mAnimationOutView != null) mAnimationOutView.startAnimation(mAnimationOutSet);
    }

    /**
     * invalidate action bar.
     *
     * @hide
     */
    protected void invalidate() {
        if (mActionBar != null && null != mActionBar.get()) {
            ViewGroup vg = (ViewGroup) getContainerView();
            if (null != vg) vg.invalidate();

            if (ActionBarExt.ENABLE_DEBUG) android.util.Log.i("HTCActionBar", "invalidate()");
        }
    }

    private Context getContext() {
        if (null == mWindow || null == mWindow.get()) return null;
        return mWindow.get().getContext();
    }

    private Resources getResources() {
        if (null == getContext()) return null;

        return getContext().getResources();
    }

    private int getIdentifier(String entryname, String typename, String packageName) {
        if (null == getResources()) return 0;

        return getResources().getIdentifier(entryname, typename, packageName);
    }

    private View findViewById(int id) {
        if (null == mWindow || null == mWindow.get()) return null;

        return (ViewGroup) mWindow.get().findViewById(id);
    }

    private boolean getResourcesMetrices() {
        if (null == getResources() || null == getResources().getDisplayMetrics()) return false;

        return true;
    }

    /**
     * @return the actionBarView.
     */
    private View getActionBarView() {
        int resId;
        resId = getIdentifier("action_bar", "id", "android");
        if (resId > 0) return findViewById(resId);

        return null;
    }

    /**
     * @return the mActionBarContainer
     */
    private ViewGroup getContainerView() {
        int resId = getIdentifier("action_bar_container", "id", "android");
        if (resId > 0) return (ViewGroup) findViewById(resId);

        return null;
    }

    private void adjustActionBar() {
        if (!getResourcesMetrices()) return;
        updateBackgroundDrawable(getActionBarView());
    }

    public void setBackgroundDrawable(Drawable d) {
        mBackgroundDrawable = d;
        adjustActionBar();
    }
}
