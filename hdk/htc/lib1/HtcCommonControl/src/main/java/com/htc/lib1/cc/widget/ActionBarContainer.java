
package com.htc.lib1.cc.widget;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.ActionMenuView;
import android.widget.ProgressBar;
import android.util.AttributeSet;
import android.content.res.TypedArray;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator;
import android.app.ActionBar;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Build;
import android.provider.Settings;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import com.htc.lib1.cc.R;
import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.cc.util.ActionBarUtil;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.DynamicThemeHelper;
import com.htc.lib1.cc.util.LogUtil;
import com.htc.lib1.cc.util.res.HtcResUtil;

/**
 * A ViewGroup use to place actionbar widget.
 */
public class ActionBarContainer extends ViewGroup {

    static final boolean ENABLE_DEBUG = HtcBuildFlag.Htc_DEBUG_flag;

    /**
     * ActionBar external mode.
     */
    public static final int MODE_EXTERNAL = 1;

    /**
     * ActionBar automotive mode.
     */
    public static final int MODE_AUTOMOTIVE = 2;

    /**
     * ActionBar updating state NORMAL.
     */
    public static final int UPDATING_MODE_NORMAL = 0;

    /**
     * ActionBar updating state PULLDOWN.
     */
    public static final int UPDATING_MODE_PULLDOWN = 1;

    /**
     * ActionBar updating state UPDATING.
     */
    public static final int UPDATING_MODE_UPDATING = 2;

    /**
     * ActionBar updating state UPDATING_WITH_TITLE.
     */
    public static final int UPDATING_MODE_UPDATING_WITH_TITLE = 3;

    /**
     * ActionBar updating state UPDATING_WITH_DROPDOWN.
     */
    public static final int UPDATING_MODE_UPDATING_WITH_DROPDOWN = 4;

    /**
     * ActionBar updating state PULLDOWN_TITLE.
     */
    public static final int UPDATING_MODE_PULLDOWN_TITLE = 5;

    private static Class sActionMenuViewClass = null;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sActionMenuViewClass = ActionMenuView.class;
        }
        if (sActionMenuViewClass == null) {
            try {
                sActionMenuViewClass = Class.forName("com.android.internal.view.menu.ActionMenuView");
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
    private static final int DEFAULT_CUSTOM_GRAVITY = Gravity.START | Gravity.CENTER_VERTICAL;

    @ExportedProperty(category = "CommonControl")
    private int mMeasureSpecM1, mMeasureSpecM2;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public ActionBarContainer(Context context) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        mMeasureSpecM1 = HtcResUtil.getM1(context);
        mMeasureSpecM2 = HtcResUtil.getM2(context);

        setupEnvironment();

        mAnimController = new ActionBarAnimController(context);
        super.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {

            @Override
            public void onChildViewRemoved(View parent, View child) {
                if (mCenterView != null && mCenterView == child) {
                    mCenterView = null;
                    mAnimController.setForegroundView(null);
                }
                if (mOnHierarchyChangeListener != null) {
                    mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
                }
            }

            @Override
            public void onChildViewAdded(View parent, View child) {
                if (child != null && checkLayoutParams(child.getLayoutParams())) {
                    ActionBar.LayoutParams fllp = (ActionBar.LayoutParams) child.getLayoutParams();
                    if ((fllp.gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL) {
                        mCenterView = child;
                        mAnimController.setForegroundView(mCenterView);
                    }
                }
                if (mOnHierarchyChangeListener != null) {
                    mOnHierarchyChangeListener.onChildViewAdded(parent, child);
                }
            }
        });
    }

    /**
     * setup the different mode common usage environment.
     */
    private void setupEnvironment() {
        mCommonOffset = (mSupportMode == MODE_AUTOMOTIVE) ? mMeasureSpecM1 : mMeasureSpecM2;
        mContainerHeight = ActionBarUtil.getActionBarHeight(getContext(), mSupportMode == MODE_AUTOMOTIVE);
    }

    private void addViewByGravity(View childView, int gravity) {
        if (null == childView) return;

        ActionBar.LayoutParams fllp = null;
        LayoutParams lp = childView.getLayoutParams();
        if (null == lp) {
            fllp = generateDefaultLayoutParams();
        } else {
            if (checkLayoutParams(lp)) fllp = (ActionBar.LayoutParams) lp;
            else {
                fllp = generateLayoutParams(lp);
            }
        }

        fllp.gravity = gravity;
        addView(childView, fllp);
    }

    /**
     * Add child to layout from left direction.
     *
     * @param childView The view you would like to add in left view
     * @deprecated please use {@link #addStartView(View)} to support RTL
     */
    public void addLeftView(View childView) {
        addViewByGravity(childView, Gravity.LEFT);
    }

    /**
     * Add child to layout from start direction.
     *
     * @param childView The view you would like to add in start view
     */
    public void addStartView(View childView) {
        addViewByGravity(childView, Gravity.START);
    }

    /**
     * Add child to layout from right direction.
     *
     * @param childView The view you would like to add in right view
     * @deprecated please use {@link #addEndView(View)} to support RTL instead
     */
    public void addRightView(View childView) {
        addViewByGravity(childView, Gravity.RIGHT);
    }

    /**
     * Add child to layout from end direction.
     *
     * @param childView The view you would like to add in end view
     */
    public void addEndView(View childView) {
        addViewByGravity(childView, Gravity.END);
    }

    /**
     * Add child to layout on the remainder center, center view could be only one.
     * <p>
     * NOTE: The last center view added will be the rotation view which participate the updating
     * state interaction.
     * </p>
     *
     * @param childView The view you would like to add in center view
     */
    public void addCenterView(View childView) {
        addViewByGravity(childView, Gravity.CENTER);
    }

    OnHierarchyChangeListener mOnHierarchyChangeListener;

    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        mOnHierarchyChangeListener = listener;
    }

    /**
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams lparams) {
        return lparams instanceof ActionBar.LayoutParams;
    }

    /**
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    public ActionBar.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lparams) {
        return new ActionBar.LayoutParams(lparams);
    }

    /**
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected ActionBar.LayoutParams generateDefaultLayoutParams() {
        return new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, DEFAULT_CUSTOM_GRAVITY);
    }

    /**
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    public ActionBar.LayoutParams generateLayoutParams(AttributeSet attrSet) {
        return new ActionBar.LayoutParams(getContext(), attrSet);
    }

    private View mCenterView = null;
    private View mProgressView = null;

    /**
     * offset value used for internal module
     */
    @ExportedProperty(category = "CommonControl")
    private int mCommonOffset = Integer.MIN_VALUE;

    @ExportedProperty(category = "CommonControl")
    private boolean mHasMenu;

    /*
     * check viewGroup has menu or not reference Class sActionMenuViewClass get childview counter
     * first and loop if childView is instance of ActonMenuViewClass set the value of mHasMenu true
     * mHasMenu default value is false
     */
    private void checkParentHasMenu() {
        mHasMenu = false;
        if (null == getParent() || sActionMenuViewClass == null) return;

        ViewGroup vg = (ViewGroup) getParent();
        final int size = vg.getChildCount();
        for (int i = 0; i < size; i++) {
            final View child = vg.getChildAt(i);
            if (child.getVisibility() != GONE) {
                if (sActionMenuViewClass.isInstance(child)) {
                    if (child.getMeasuredWidth() > 0) mHasMenu = true;
                    break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated [Module internal use]
     * @hide
     */
    @Deprecated
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mContainerHeight, MeasureSpec.getMode(heightMeasureSpec));

        final int containerWidth = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(containerWidth, mContainerHeight);

        checkParentHasMenu();

        boolean mHasLeftView = false;
        boolean mHasRightView = false;

        int layoutDirection = LAYOUT_DIRECTION_LTR;
        if (ActionBarUtil.IS_SUPPORT_RTL) {
            layoutDirection = getLayoutDirection();
        }

        if (mHasMenu) {
            final int menuHorizontalGravity = Gravity.getAbsoluteGravity(Gravity.END, layoutDirection);
            if (menuHorizontalGravity == Gravity.LEFT) {
                mHasLeftView = true;
            } else if (menuHorizontalGravity == Gravity.RIGHT) {
                mHasRightView = true;
            }
        }

        int widthUsed = 0;

        final int size = getChildCount();
        for (int i = 0; i < size; i++) {
            final View child = getChildAt(i);
            if (null == child || child == mProgressView || child == mRefreshView || VISIBLE != child.getVisibility()) continue;

            ActionBar.LayoutParams fllp = (ActionBar.LayoutParams) child.getLayoutParams();

            int gravity = fllp.gravity;
            if (gravity <= 0) {
                gravity = DEFAULT_CUSTOM_GRAVITY;
            }

            final int absoluteHorizontalGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection) & Gravity.HORIZONTAL_GRAVITY_MASK;
            if (absoluteHorizontalGravity == Gravity.CENTER_HORIZONTAL || absoluteHorizontalGravity == Gravity.FILL_HORIZONTAL) {
                continue;
            } else if (absoluteHorizontalGravity == Gravity.RIGHT) {
                mHasRightView = true;
            } else {
                mHasLeftView = true;
            }

            measureChildWithMargins(child, widthMeasureSpec, widthUsed, heightMeasureSpec, 0);
            widthUsed += child.getMeasuredWidth() + fllp.leftMargin + fllp.rightMargin;
        }

        if (!mHasLeftView) {
            widthUsed += mMeasureSpecM1;
        }
        if (!mHasRightView) {
            widthUsed += mMeasureSpecM1;
        }

        if (null != mProgressView && View.VISIBLE == mProgressView.getVisibility()) {
            measureChildWithMargins(mProgressView, widthMeasureSpec, widthUsed, heightMeasureSpec, 0);
            ActionBar.LayoutParams fllp = (ActionBar.LayoutParams) mProgressView.getLayoutParams();
            widthUsed += mProgressView.getMeasuredWidth() + fllp.leftMargin + fllp.rightMargin + mCommonOffset;
        }
        if (null != mRefreshView && View.VISIBLE == mRefreshView.getVisibility()) {
            measureChildWithMargins(mRefreshView, widthMeasureSpec, widthUsed, heightMeasureSpec, 0);
        }

        for (int i = 0; i < size; i++) {
            final View child = getChildAt(i);
            if (null == child || VISIBLE != child.getVisibility()) continue;

            ActionBar.LayoutParams fllp = (ActionBar.LayoutParams) child.getLayoutParams();
            final int absoluteHorizontalGravity = fllp.gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
            if (absoluteHorizontalGravity != Gravity.CENTER_HORIZONTAL) {
                continue;
            }

            int centerWidth = containerWidth - widthUsed - getPaddingLeft() - getPaddingRight() - fllp.leftMargin - fllp.rightMargin;
            if (centerWidth < 0) {
                centerWidth = 0;
                LogUtil.logE("ActionBarContainer", "centerWidth < 0 : ",
                        "containerWidth = ", containerWidth,
                        ",widthUsed = ", widthUsed,
                        ",getPaddingLeft() = ", getPaddingLeft(),
                        ",getPaddingRight() = ", getPaddingRight(),
                        ",leftMargin = ", fllp.leftMargin,
                        ",rightMargin = ", fllp.rightMargin);
            }
            int centerWidthMeasureSpec = MeasureSpec.makeMeasureSpec(centerWidth, MeasureSpec.EXACTLY);

            int centerHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom() + fllp.topMargin + fllp.bottomMargin, fllp.height);
            child.measure(centerWidthMeasureSpec, centerHeightMeasureSpec);
        }
    }

    private int mCenterGravity = android.view.Gravity.START;

    /**
     * Force center view locate on remainder space center.
     *
     * @param enable True to set Gravity.CENTER, False to set Gravity.START
     * @deprecated [Not use any longer]
     */
    public void setCenterGravityEnabled(boolean enable) {
        if (enable && mCenterGravity != android.view.Gravity.CENTER) {
            mCenterGravity = android.view.Gravity.CENTER;
            requestLayout();
            return;
        }

        if (!enable && mCenterGravity != android.view.Gravity.START) {
            mCenterGravity = android.view.Gravity.START;
            requestLayout();
            return;
        }
    }

    /**
     * {@inhericDoc}.
     *
     * @param changed This is a new size or position for this view
     * @param left Left position, relative to parent
     * @param top Top position, relative to parent
     * @param right Right position, relative to parent
     * @param bottom Bottom position, relative to parent
     * @deprecated [Module internal use]
     * @hide
     */
    @Deprecated
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int layoutDirection = LAYOUT_DIRECTION_LTR;
        if (ActionBarUtil.IS_SUPPORT_RTL) {
            layoutDirection = getLayoutDirection();
        }
        final boolean isLayoutRtl = layoutDirection == LAYOUT_DIRECTION_RTL;

        int childWidth, childHeight;
        int childViewL, childViewR;
        int childViewT, childViewB;
        ActionBar.LayoutParams mParams;
        int currentLeft = getPaddingLeft();
        int currentRight = right - left - getPaddingRight();
        final int currentTop = getPaddingTop();
        int currentBottom = bottom - top - getPaddingBottom();
        final int paddingHeight = currentBottom - currentTop;
        int leftViewNum = 0;
        int rightViewNum = 0;

        if (mBackupView != null && mBackupView.getVisibility() == VISIBLE) {
            childWidth = mBackupView.getMeasuredWidth();
            childHeight = mBackupView.getMeasuredHeight();
            mParams = (android.app.ActionBar.LayoutParams) mBackupView.getLayoutParams();
            if (isLayoutRtl) {
                childViewR = currentRight - mParams.rightMargin;
                childViewL = childViewR - childWidth;
            } else {
                childViewL = currentLeft + mParams.leftMargin;
                childViewR = childViewL + childWidth;
            }
            childViewT = (paddingHeight - childHeight) / 2 + currentTop + mParams.topMargin - mParams.bottomMargin;
            childViewB = childViewT + childHeight;
            mBackupView.layout(childViewL, childViewT, childViewR, childViewB);
            if (isLayoutRtl) {
                rightViewNum++;
                currentRight = childViewL - mParams.leftMargin;
            } else {
                leftViewNum++;
                currentLeft = childViewR + mParams.rightMargin;
            }
        }
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view != null && view.getVisibility() == VISIBLE) {
                if (mProgressView == view || mRefreshView == view || mBackupView == view) {
                    continue;
                }
                mParams = (android.app.ActionBar.LayoutParams) view.getLayoutParams();
                childWidth = view.getMeasuredWidth();
                childHeight = view.getMeasuredHeight();

                int gravity = mParams.gravity;
                if (gravity <= 0) {
                    gravity = DEFAULT_CUSTOM_GRAVITY;
                }

                final int absoluteHorizontalGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection) & Gravity.HORIZONTAL_GRAVITY_MASK;
                if (absoluteHorizontalGravity == Gravity.CENTER_HORIZONTAL || absoluteHorizontalGravity == Gravity.FILL_HORIZONTAL) {
                    continue;
                } else if (absoluteHorizontalGravity == Gravity.RIGHT) {
                    rightViewNum++;
                    childViewR = currentRight - mParams.rightMargin;
                    childViewL = childViewR - childWidth;
                    currentRight = childViewL - mParams.leftMargin;
                } else {
                    leftViewNum++;
                    childViewL = currentLeft + mParams.leftMargin;
                    childViewR = childViewL + childWidth;
                    currentLeft = childViewR + mParams.rightMargin;
                }
                childViewT = (paddingHeight - childHeight) / 2 + currentTop + mParams.topMargin - mParams.bottomMargin;
                childViewB = childViewT + childHeight;
                view.layout(childViewL, childViewT, childViewR, childViewB);
            }
        }
        if (mProgressView != null && mProgressView.getVisibility() == VISIBLE) {
            childWidth = mProgressView.getMeasuredWidth();
            childHeight = mProgressView.getMeasuredHeight();
            mParams = (android.app.ActionBar.LayoutParams) mProgressView.getLayoutParams();
            if (isLayoutRtl) {
                childViewL = currentLeft + ((leftViewNum == 0 && !mHasMenu) ? mMeasureSpecM1 : 0) + mParams.leftMargin;
                childViewR = childViewL + childWidth;
                currentLeft = childViewR + mParams.rightMargin + mCommonOffset;
                leftViewNum++;
            } else {
                childViewR = currentRight - ((rightViewNum == 0 && !mHasMenu) ? mMeasureSpecM1 : 0) - mParams.rightMargin;
                childViewL = childViewR - childWidth;
                currentRight = childViewL - mParams.leftMargin - mCommonOffset;
                rightViewNum++;
            }
            childViewT = (paddingHeight - childHeight) / 2 + currentTop + mParams.topMargin - mParams.bottomMargin;
            childViewB = childViewT + childHeight;
            mProgressView.layout(childViewL, childViewT, childViewR, childViewB);
        }
        if (mRefreshView != null && mRefreshView.getVisibility() == VISIBLE) {
            childWidth = mRefreshView.getMeasuredWidth();
            childHeight = mRefreshView.getMeasuredHeight();
            mParams = (android.app.ActionBar.LayoutParams) mRefreshView.getLayoutParams();
            int rightOffset, leftOffset;
            if (isLayoutRtl) {
                leftOffset = (leftViewNum == 0 && !mHasMenu) ? mMeasureSpecM1 : 0;
                rightOffset = rightViewNum != 0 ? 0 : (mState == UPDATING_MODE_PULLDOWN ? mCommonOffset : mMeasureSpecM1);
            } else {
                rightOffset = (rightViewNum == 0 && !mHasMenu) ? mMeasureSpecM1 : 0;
                leftOffset = leftViewNum != 0 ? 0 : (mState == UPDATING_MODE_PULLDOWN ? mCommonOffset : mMeasureSpecM1);
            }
            childViewL = currentLeft + leftOffset + mParams.leftMargin;
            childViewR = currentRight - rightOffset - mParams.rightMargin;
            childViewT = currentTop + mParams.topMargin;
            childViewB = currentBottom - mParams.bottomMargin;
            mRefreshView.layout(childViewL, childViewT, childViewR, childViewB);
        }

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (null == child || VISIBLE != child.getVisibility()) continue;

            mParams = (ActionBar.LayoutParams) child.getLayoutParams();
            final int absoluteHorizontalGravity = mParams.gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
            if (absoluteHorizontalGravity != Gravity.CENTER_HORIZONTAL) {
                continue;
            }
            childWidth = child.getMeasuredWidth();
            childHeight = child.getMeasuredHeight();

            int rightOffset, leftOffset;
            if (isLayoutRtl) {
                leftOffset = (leftViewNum == 0 && !mHasMenu) ? mMeasureSpecM1 : 0;
                rightOffset = rightViewNum == 0 ? mMeasureSpecM1 : 0;
            } else {
                leftOffset = leftViewNum == 0 ? mMeasureSpecM1 : 0;
                rightOffset = (rightViewNum == 0 && !mHasMenu) ? mMeasureSpecM1 : 0;
            }
            childViewL = currentLeft + leftOffset + mParams.leftMargin;
            childViewR = currentRight - rightOffset - mParams.rightMargin;
            childViewT = (paddingHeight - childHeight) / 2 + currentTop + mParams.topMargin - mParams.bottomMargin;
            childViewB = childViewT + childHeight;
            child.layout(childViewL, childViewT, childViewR, childViewB);
        }
    }

    /**
     * Get progress bar visibility.
     *
     * @return return the visibility of progress view
     */
    public int getProgressVisibility() {
        return mProgressView == null ? GONE : mProgressView.getVisibility();
    }

    /**
     * Set progress bar visibility.
     *
     * @param visibility set the visibility of progress view
     */
    public void setProgressVisibility(int visibility) {
        // only execute on the first time usage
        setupProgressView();

        if (mProgressView != null && mProgressView.getVisibility() != visibility) mProgressView.setVisibility(visibility);
    }

    // runtime create and initialize the progress view environment
    private void setupProgressView() {

        if (mProgressView == null) {
            mProgressView = new ProgressBar(getContext(), null, R.attr.htcActionBarProgressBarStyle);
            mProgressView.setLayoutParams(new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.END));
            addView(mProgressView);
        } else if (mProgressView.getParent() == null) {
            // add view back to avoid AP remove all view
            addView(mProgressView);
        }
    }

    // represent the backup arrow view (internal module)
    private ActionBarItemView mBackupView = null;

    /**
     * Set Back button enable or disable.
     *
     * @param enable to enable back up view or not
     */
    public void setBackUpEnabled(boolean enable) {
        // only execute on the first time usage
        setupBackUpView();

        if (mBackupView != null && mBackupView.getVisibility() != (enable ? VISIBLE : GONE)) mBackupView.setVisibility(enable ? VISIBLE : GONE);
    }

    private View.OnClickListener mBackupListener = null;

    /**
     * Register click listener for backup arrow.
     *
     * @param listener register click listener
     */
    public void setBackUpOnClickListener(View.OnClickListener listener) {
        mBackupListener = listener;

        if (mBackupView != null) mBackupView.setOnClickListener(mBackupListener);
    }

    // runtime create and initialize the backup environment
    private void setupBackUpView() {
        if (mBackupView == null) {
            // setup the backup view environment
            mBackupView = new ActionBarItemView(getContext());
            mBackupView.setClickable(true);
            setBackUpDrawable();
            mBackupView.setOnClickListener(mBackupListener);
            mBackupView.setContentDescription(getResources().getString(R.string.va_back));
            mBackupView.setLayoutParams(new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, Gravity.START));
            // force the backup arrow as first left view
            addView(mBackupView, 0);
        } else if (mBackupView.getParent() == null) {
            // add view back to avoid AP remove all view
            addView(mBackupView);
        }
    }

    private void setBackUpDrawable() {
        if (mBackupView == null) {
            return;
        }
        mBackupView.setSupportMode(mSupportMode == MODE_AUTOMOTIVE ? ActionBarItemView.MODE_AUTOMOTIVE : ActionBarItemView.MODE_EXTERNAL | ActionBarItemView.FLAG_M2_IMG_M2);
        int imageResource;
        TypedArray a = getContext().obtainStyledAttributes(null, R.styleable.HtcActionBarBackup, R.attr.actionBarBackupStyle, R.style.ActionBarBackup);
        // setup the environment value
        if (mSupportMode == MODE_AUTOMOTIVE) {

            imageResource = a.getResourceId(R.styleable.HtcActionBarBackup_android_src, -1);
            a.recycle();
            if (-1 == imageResource) {
                android.util.Log.e("ActionBarContainer", "imageResource is -1!");
                imageResource = R.drawable.icon_btn_up_dark_xl;
            }
        } else {
            imageResource = a.getResourceId(
                    R.styleable.HtcActionBarBackup_android_drawable, -1);
            a.recycle();
            if (-1 == imageResource) {
                android.util.Log.e("ActionBarContainer", "imageResource is -1!");
                imageResource = R.drawable.icon_btn_previous_dark;
            }
        }
        mBackupView.setIcon(imageResource);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (mBackupView.getIcon() != null) {
                mBackupView.getIcon().setAutoMirrored(true);
            }
        }
    }

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = MODE_EXTERNAL, to = "MODE_EXTERNAL"),
            @IntToString(from = MODE_AUTOMOTIVE, to = "MODE_AUTOMOTIVE")
    })
    private int mSupportMode = Integer.MIN_VALUE;

    /**
     * support external contaienr usage for specific mode.
     *
     * @param mode support mode
     */
    public void setSupportMode(int mode) {
        // skip to avoid useless operation
        if (mSupportMode == mode) return;

        if (mode == MODE_EXTERNAL || mode == MODE_AUTOMOTIVE) {
            mSupportMode = mode;
            setupEnvironment();
            if (ActionBarUtil.IS_SUPPORT_RTL) {
                setPaddingRelative(0, 0, 0, 0);
            } else {
                setPadding(0, 0, 0, 0);
            }
            setBackground(mSupportMode == MODE_EXTERNAL ? null : ActionBarUtil.getActionBarBackground(getContext(), ActionBarExt.MODE_DEFAULT));
            updateContainerLayout();
            setBackUpDrawable();
            requestLayout();
        }
    }

    @ExportedProperty(category = "CommonControl")
    private int mContainerHeight = Integer.MIN_VALUE;

    // setup the container environment for support mode
    private void updateContainerLayout() {
        ViewGroup.LayoutParams lparams;// =null;

        // skip to avoid useless operation
        if (getLayoutParams() == null) return;

        // load the container height based on different mode
        mContainerHeight = ActionBarUtil.getActionBarHeight(getContext(), mSupportMode == MODE_AUTOMOTIVE);

        lparams = getLayoutParams();
        lparams.height = mContainerHeight;
        lparams.width = LayoutParams.MATCH_PARENT;
        setLayoutParams(lparams);

        invalidate();
    }

    /**
     * @deprecated [Module internal use]
     * @hide
     */
    @Deprecated
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mSupportMode == MODE_EXTERNAL || mSupportMode == MODE_AUTOMOTIVE) updateContainerLayout();
    }

    /** @hide */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseInnerSoundPool();
    }

    private ActionBarRefresh mRefreshView = null;

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = UPDATING_MODE_NORMAL, to = "UPDATING_MODE_NORMAL"),
            @IntToString(from = UPDATING_MODE_PULLDOWN, to = "UPDATING_MODE_PULLDOWN"),
            @IntToString(from = UPDATING_MODE_UPDATING, to = "UPDATING_MODE_UPDATING"),
            @IntToString(from = UPDATING_MODE_UPDATING_WITH_TITLE, to = "UPDATING_MODE_UPDATING_WITH_TITLE"),
            @IntToString(from = UPDATING_MODE_UPDATING_WITH_DROPDOWN, to = "UPDATING_MODE_UPDATING_WITH_DROPDOWN"),
            @IntToString(from = UPDATING_MODE_PULLDOWN_TITLE, to = "UPDATING_MODE_PULLDOWN_TITLE")
    })
    private int mState = UPDATING_MODE_NORMAL;

    private View.OnClickListener mListener = null;

    private ActionBarAnimController mAnimController;

    private void initRefreshView() {
        if (null == mRefreshView) {
            mRefreshView = new ActionBarRefresh(getContext());
            mAnimController.setBackgroundView(mRefreshView);
        }

        if (null == mRefreshView.getParent()) {
            mRefreshView.setVisibility(View.GONE);
            addView(mRefreshView, new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, Gravity.FILL));
        }

    }

    private void playPullDownSoundEffect(int soundType) {
        if (getWindowToken() == null) {
            return;
        }

        if (Settings.System.getInt(getContext().getContentResolver(), "htc_pull_to_fresh_sound_enabled", 0) == 0) {
            if (ENABLE_DEBUG) android.util.Log.d("HTCActionBar", "pull down sound effect is not eanbled");
            return;
        }

        getOnPlaySoundListener().onPlaySournd(soundType);
    }

    private void ensureViewVisible(View view, float alpha) {
        if (view != null) {
            view.setVisibility(VISIBLE);
            view.setRotationX(0);
            view.setAlpha(alpha);
        }
    }

    /**
     * get pull down to refresh UI state.
     *
     * @return get current updating state
     */
    public int getUpdatingState() {
        return mState;
    }

    private void ensurePullDownState() {
        if (mState != UPDATING_MODE_PULLDOWN) {
            mState = UPDATING_MODE_PULLDOWN;

            initRefreshView();

            prepareUpdatingPullDown(false);
        }
    }

    private void prepareUpdatingPullDown(boolean toEnd) {

        mRefreshView.setMode(ActionBarRefresh.MODE_PULLDOWN);
        mRefreshView.setOnClickListener(null);
        ensureViewVisible(mRefreshView, 0);

        if (toEnd) {
            mAnimController.setAnimatorListener(mRotateAnimatorListenerAdapter);
            mAnimController.setAnimProgress(0, getRotationMax());
        }
    }

    /**
     * set the action bar updating mode.
     * <p>
     * note that if you enable one of {@link #UPDATING_MODE_UPDATING_WITH_DROPDOWN},
     * {@link #UPDATING_MODE_UPDATING_WITH_TITLE}, {@link #UPDATING_MODE_UPDATING},we will show you
     * corresponding view appearance,which like if you use
     * {@link #UPDATING_MODE_UPDATING_WITH_DROPDOWN},you will see a Title,SubTitle,Arrow in this
     * mode;if you use {@link #UPDATING_MODE_UPDATING},you will only see Title.
     * </p>
     *
     * @param state should be one of {@link #UPDATING_MODE_NORMAL},{@link #UPDATING_MODE_PULLDOWN},
     *            {@link #UPDATING_MODE_UPDATING_WITH_DROPDOWN},
     *            {@link #UPDATING_MODE_UPDATING_WITH_TITLE}, {@link #UPDATING_MODE_UPDATING}.
     */
    public void setUpdatingState(int state) {

        if (ENABLE_DEBUG) android.util.Log.d("HTCActionBar", "setUpdatingState state = " + state);

        if (mState == state) return;

        initRefreshView();

        switch (state) {
            case UPDATING_MODE_NORMAL:
                if (mState == UPDATING_MODE_PULLDOWN && getRotationProgress() != 0) {
                    setRotationProgress(getRotationProgress(), 0);
                } else {
                    mAnimController.resetProgress();
                    mRefreshView.setVisibility(View.GONE);
                    ensureViewVisible(mCenterView, 1);
                }
                break;
            case UPDATING_MODE_PULLDOWN:
                prepareUpdatingPullDown(true);
                break;
            case UPDATING_MODE_UPDATING_WITH_DROPDOWN:
            case UPDATING_MODE_UPDATING_WITH_TITLE:
            case UPDATING_MODE_UPDATING:
                mAnimController.resetProgress();
                updateUpdatingMode(state);
                break;
        }

        mState = state;
    }

    private void updateUpdatingMode(int state) {
        mRefreshView.setMode(ActionBarRefresh.MODE_UPDATING);
        if (state == UPDATING_MODE_UPDATING_WITH_DROPDOWN) {
            mRefreshView.setModeDisplayType(ActionBarRefresh.MODE_UPDATING, ActionBarRefresh.TYPE_UPDATE_WITH_SUBTITLE_DROPDOWN);
        } else if (state == UPDATING_MODE_UPDATING_WITH_TITLE) {
            mRefreshView.setModeDisplayType(ActionBarRefresh.MODE_UPDATING, ActionBarRefresh.TYPE_UPDATE_WITH_SUBTITLE);
        } else if (state == UPDATING_MODE_UPDATING) {
            mRefreshView.setModeDisplayType(ActionBarRefresh.MODE_UPDATING, ActionBarRefresh.TYPE_UPDATE_WITH_TITLE);
        }
        if (mListener != null) mRefreshView.setOnClickListener(mListener);

        if (mCenterView != null && mCenterView.getVisibility() == View.VISIBLE) {
            mCenterView.setVisibility(View.INVISIBLE);
        }
        ensureViewVisible(mRefreshView, 1);

        if (mState == UPDATING_MODE_PULLDOWN) {
            playPullDownSoundEffect(UPDATING_MODE_UPDATING);
        }

        mRefreshView.announceForAccessibility();
    }

    /**
     * set click listener for UPDATING_MODE_UPDATING state.
     *
     * @param listener register click listener
     */
    public void setUpdatingViewClickListener(View.OnClickListener listener) {
        mListener = listener;
        if (mRefreshView != null) {
            mRefreshView.setOnClickListener(mListener);
        }
    }

    /**
     * set text for {@link #UPDATING_MODE_PULLDOWN} or {@link #UPDATING_MODE_UPDATING} state.
     * <p>
     * {@link #UPDATING_MODE_PULLDOWN} => set text to SubTitle,and show the SubTitle View when
     * {@link #UPDATING_MODE_PULLDOWN} state
     * </p>
     * <p>
     * {@link #UPDATING_MODE_PULLDOWN_TITLE} => set text to Title View when
     * {@link #UPDATING_MODE_PULLDOWN} state
     * </p>
     * <p>
     * {@link #UPDATING_MODE_UPDATING} => set text to SubTitle View when
     * {@link #UPDATING_MODE_UPDATING} state
     * </p>
     * <p>
     * {@link #UPDATING_MODE_UPDATING_WITH_DROPDOWN} and {@link #UPDATING_MODE_UPDATING_WITH_TITLE}
     * => set text to Title View when {@link #UPDATING_MODE_UPDATING} state
     * </p>
     *
     * @param state which state to show text
     * @param text the string you would like to show
     */
    public void setUpdatingViewText(int state, String text) {
        initRefreshView();
        switch (state) {

            case UPDATING_MODE_PULLDOWN:
                if (mRefreshView != null) {
                    if (text == null) {
                        mRefreshView.setModeDisplayType(ActionBarRefresh.MODE_PULLDOWN, ActionBarRefresh.TYPE_PULLDOWN_WITH_TITLE);
                    } else {
                        mRefreshView.setModeDisplayType(ActionBarRefresh.MODE_PULLDOWN, ActionBarRefresh.TYPE_PULLDOWN_WITH_SUBTITLE);
                    }
                    mRefreshView.setModeText(ActionBarRefresh.MODE_PULLDOWN, ActionBarRefresh.CATEGORY_SUBTITLE, text);
                }
                break;

            case UPDATING_MODE_PULLDOWN_TITLE:
                if (mRefreshView != null) {
                    mRefreshView.setModeText(ActionBarRefresh.MODE_PULLDOWN, ActionBarRefresh.CATEGORY_TITLE, text);
                }
                break;

            case UPDATING_MODE_UPDATING:
                if (mRefreshView != null) {
                    mRefreshView.setModeText(ActionBarRefresh.MODE_UPDATING, ActionBarRefresh.CATEGORY_SUBTITLE, text);
                }
                break;

            case UPDATING_MODE_UPDATING_WITH_DROPDOWN:
            case UPDATING_MODE_UPDATING_WITH_TITLE:
                if (mRefreshView != null) {
                    mRefreshView.setModeText(ActionBarRefresh.MODE_UPDATING, ActionBarRefresh.CATEGORY_TITLE, text);
                }
                break;
        }
    }

    /**
     * set text for {@link #UPDATING_MODE_PULLDOWN} or {@link #UPDATING_MODE_UPDATING} state.
     *
     * @param state which state to show text
     * @param resId the string resource id you would like to show
     * @see #setUpdatingViewText(int, String)
     */
    public void setUpdatingViewText(int state, int resId) {
        String s = null;
        if (resId != 0) s = getResources().getString(resId);
        setUpdatingViewText(state, s);
    }

    /**
     * Set refresh view rotation progress max value.
     *
     * @param max the max value of the progress, default is 100
     */
    public void setRotationMax(int max) {
        mAnimController.setAnimProgressMax(max);
    }

    /**
     * Get refresh view rotation progress max value.
     *
     * @return current max progress value, default is 100
     */
    @ExportedProperty(category = "CommonControl")
    public int getRotationMax() {
        return mAnimController.getAnimProgressMax();
    }

    /**
     * Set refresh view rotation progress, the progress will be 0 to RotationMax value.
     *
     * @param progress the rotation progress
     */
    public void setRotationProgress(int progress) {
        ensurePullDownState();
        mAnimController.setAnimProgress(progress);
        checkPullDownProgress();
    }

    private void checkPullDownProgress() {
        if (mAnimController.getAnimProgress() == mAnimController.getAnimProgressMax()) {
            playPullDownSoundEffect(UPDATING_MODE_PULLDOWN);
            mRefreshView.announceForAccessibility();
        }
    }

    private AnimatorListenerAdapter mRotateAnimatorListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            switch (mState) {
                case UPDATING_MODE_NORMAL:
                    mRefreshView.setVisibility(View.GONE);
                    break;

                case UPDATING_MODE_PULLDOWN:
                    checkPullDownProgress();
                    break;
            }
        }
    };

    /**
     * Set refresh view rotation progress, the value of start and end will be 0 to RotationMax
     * value.We will apply animation in this procedure.
     *
     * @param start the rotation start progress
     * @param end the rotation end progress
     */
    public void setRotationProgress(int start, int end) {
        ensurePullDownState();
        mAnimController.setAnimatorListener(mRotateAnimatorListenerAdapter);
        mAnimController.setAnimProgress(start, end);
    }

    /**
     * Get refresh view rotation progress, the progress will be 0 to RotationMax value.
     *
     * @return get current rotation progress
     */
    @ExportedProperty(category = "CommonControl")
    public int getRotationProgress() {
        return mAnimController.getAnimProgress();
    }

    private OnPlaySoundListener mPlaySoundListener;
    private SoundPoolPlayer mInnerSoundPlayer;

    /**
     * Register a callback to be invoked when ActionBarContainer change to
     * {@link #UPDATING_MODE_UPDATING} or {@link #UPDATING_MODE_PULLDOWN} state.You should play the
     * corresponding sound for it. If user not set ,the default sound player in ActionBarContainer
     * will works.
     *
     * @see OnPlaySoundListener
     */
    public void setOnPlaySoundListener(OnPlaySoundListener playSoundListener) {
        if (playSoundListener == mPlaySoundListener) {
            return;
        }
        if (playSoundListener != null) {
            releaseInnerSoundPool();
        }
        mPlaySoundListener = playSoundListener;
    }

    private OnPlaySoundListener getOnPlaySoundListener() {
        if (mPlaySoundListener == null) {
            if (mInnerSoundPlayer == null) {
                mInnerSoundPlayer = new SoundPoolPlayer();
            }
            return mInnerSoundPlayer;
        }
        return mPlaySoundListener;
    }

    private void releaseInnerSoundPool() {
        if (mInnerSoundPlayer != null) {
            mInnerSoundPlayer.release();
            mInnerSoundPlayer = null;
        }
    }

    /**
     * Interface definition for a callback to be invoked when a sound need to play with sound type.
     * <p>
     * Sample :
     *
     * <pre class="prettyprint">
     * SoundPool sampleSoundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 0);
     * int sampleSoundPullDown = sampleSoundPool.load(context, com.htc.lib1.cc.R.raw.pulldown, priority);
     * int sampleSoundUpdating = sampleSoundPool.load(context, com.htc.lib1.cc.R.raw.updating, priority);
     * sampleActionBarContainer.setOnPlaySoundListener(new OnPlaySoundListener() {
     *     &#064;Override
     *     public void onPlaySournd(int type) {
     *         if (type == ActionBarContainer.UPDATING_MODE_UPDATING) {
     *             sampleSoundPool.play(sampleSoundUpdating, leftVolume, rightVolume, priority, loop, rate);
     *         } else if (type == ActionBarContainer.UPDATING_MODE_PULLDOWN) {
     *             sampleSoundPool.play(sampleSoundPullDown, leftVolume, rightVolume, priority, loop, rate);
     *         }
     *     }
     * });
     * </pre>
     *
     * </p>
     * <p>
     * <b>NOTE:</b>To follow Google {@link SoundPool#release()} document, you should call
     * SoundPool.release() to release all the native resources in use and then set the SoundPool
     * reference to null when you never use it.
     * </p>
     *
     * @see android.media.SoundPool
     */
    public interface OnPlaySoundListener {

        /**
         * @param type should be one of {@link #UPDATING_MODE_PULLDOWN} ,
         *            {@link #UPDATING_MODE_UPDATING}.
         */
        public void onPlaySournd(int type);// TODO Correct "Sournd" spell.

    }

    @ExportedProperty(deepExport = true)
    private DynamicThemeHelper dumpDynamicTheme() {
        return new DynamicThemeHelper(getContext());
    }

    private class SoundPoolPlayer implements OnPlaySoundListener {
        private SoundPool mSoundPool;
        private int mSoundPullDown = Integer.MIN_VALUE, mSoundUpdating = Integer.MIN_VALUE;
        private static final int LEFT_VOLUME = 1;
        private static final int RIGHT_VOLUME = 1;
        private static final int PRIORITY = 0;
        private static final int LOOP = 0;
        private static final int RATE = 1;

        @Override
        public void onPlaySournd(int type) {
            if (mSoundPool == null) {
                mSoundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 0);
                mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {

                    @Override
                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        if (status == 0) {
                            playSoundEffect(sampleId);
                        }
                    }
                });
            }

            if (type == ActionBarContainer.UPDATING_MODE_UPDATING) {
                if (mSoundUpdating == Integer.MIN_VALUE) {
                    mSoundUpdating = mSoundPool.load(getContext(), R.raw.updating, 1);
                    return;
                }
                playSoundEffect(mSoundUpdating);
            } else if (type == ActionBarContainer.UPDATING_MODE_PULLDOWN) {
                if (mSoundPullDown == Integer.MIN_VALUE) {
                    mSoundPullDown = mSoundPool.load(getContext(), R.raw.pulldown, 1);
                    return;
                }
                playSoundEffect(mSoundPullDown);
            }
        }

        private void playSoundEffect(int soundId) {
            if (mSoundPool != null) {
                mSoundPool.play(soundId, LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, RATE);
            }
        }

        public void release() {
            if (mSoundPool != null) {
                mSoundPool.release();
                mSoundPool = null;
                mSoundUpdating = mSoundPullDown = Integer.MIN_VALUE;
            }
        }
    }
}
