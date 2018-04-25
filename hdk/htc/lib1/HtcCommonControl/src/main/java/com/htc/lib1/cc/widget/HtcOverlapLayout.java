package com.htc.lib1.cc.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewDebug.ExportedProperty;
import com.htc.lib1.cc.util.WindowUtil;

/**
 * <p>
 * HtcOverlapLayout is a ViewGroup which can help you handle the HtcFooter position in it and can
 * automatic adjust it's margins to avoid covered by system windows depends on
 * fitSystemWindows(android.graphics.Rect).
 * </p>
 * <p>
 * To be noted that when system window in stable mode(it usually happened when you set
 * View.setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE) or
 * Window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)),If you want your view to
 * place until the top of the screen under the StatusBar,you can invoke
 * {@link #setInsetStatusBar(boolean)} and set to false that we will not include the status bar's
 * height to the top margin.
 * <p>
 */
public class HtcOverlapLayout extends ViewGroup {


    @ExportedProperty(category = "CommonControl", prefix = "HtcOverlapLayout")
    private int mStatusBarHeight = 0;

    private static final boolean DEBUG = false;
    private static final String TAG = "HtcOverlapLayout";

    @ExportedProperty(category = "CommonControl", prefix = "HtcOverlapLayout")
    private int mActionBarTopHeight;

    // add for setactionbar
    // default is true,or else ap set to false
    @ExportedProperty(category = "CommonControl", prefix = "HtcOverlapLayout")
    private boolean mInsetActionBarTop = true;
    @ExportedProperty(category = "CommonControl", prefix = "HtcOverlapLayout")
    private boolean mInsetStatusBar = true;
    @ExportedProperty(category = "CommonControl", prefix = "HtcOverlapLayout")
    private boolean mInsetBottomSystemWindow = true;
    @ExportedProperty(category = "CommonControl", prefix = "HtcOverlapLayout")
    private boolean mInsetRightSystemWindow = true;

    @ExportedProperty(category = "CommonControl", prefix = "HtcOverlapLayout")
    private int mBottomSystemWindow = 0;
    @ExportedProperty(category = "CommonControl", prefix = "HtcOverlapLayout")
    private int mRightSystemWindow = 0;
    private View mActionbarContainerView = null;

    private OnGetSysWinInfoListener mGetSysWinListener;

    /**
     * When in stable mode,you need to decide whether the status bar's height should be include in
     * our topMargin.
     *
     * @param insetStatusBar indicate whether the statusBar height should be include in our top
     *            margin when stable mode, true by default
     */
    public void setInsetStatusBar(boolean insetStatusBar) {
        mInsetStatusBar = insetStatusBar;
        adjustSelfLayoutParameter();
    }

    /** @hide */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int resId = getContext().getResources().getIdentifier("action_bar_container", "id", "android");
        if (resId > 0) {
            mActionbarContainerView = getRootView().findViewById(resId);
        } else {
            mActionbarContainerView = null;
        }
    }

    /** @hide */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mActionbarContainerView = null;
    }

    /**
     * In order to get the height of the statusbar and navigationbar, you need
     * to hook the callback.
     *
     * @see android.view.ViewGroup#fitSystemWindows(android.graphics.Rect)
     */
    @Override
    protected boolean fitSystemWindows(Rect insets) {
        // TODO Auto-generated method stub
        super.fitSystemWindows(insets);

        if (DEBUG)
            Log.e(TAG, "insets = " + insets);

        final int vis = getWindowSystemUiVisibility();
        final boolean stable = (vis & SYSTEM_UI_FLAG_LAYOUT_STABLE) != 0;
        if (stable) {
            mStatusBarHeight = insets.top - getActionBarTopHeight();
        } else {
            mStatusBarHeight = 0;
        }

        int bottomSystemWindowHeight = insets.bottom;
        int rightSystemWindowWidth = insets.right;

        if (bottomSystemWindowHeight >= 0 && bottomSystemWindowHeight != mBottomSystemWindow)
            mBottomSystemWindow = bottomSystemWindowHeight;

        if (rightSystemWindowWidth >= 0 && rightSystemWindowWidth != mRightSystemWindow)
            mRightSystemWindow = rightSystemWindowWidth;

        adjustSelfLayoutParameter();

        if (null != mGetSysWinListener)
            mGetSysWinListener.onGetSysWinInfo(insets);

        /* doesn't consume this fitSystemWindow traversal */
        return false;
    }


    /**
     * Simple constructor to use when creating a view from code.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcOverlapLayout(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called when a view is
     * being constructed from an XML file, supplying attributes that were specified in the XML file.
     * This version uses a default style of 0, so the only attribute values applied are those in the
     * Context's Theme and the given AttributeSet.
     * <p>
     * The method onFinishInflate() will be called after all children have been added.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcOverlapLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This constructor of View
     * allows subclasses to use their own base style when they are inflating. For example, a Button
     * class's constructor would call this version of the super class constructor and supply
     * <code>R.attr.buttonStyle</code> for <var>defStyle</var>; this allows the theme's button style
     * to modify all of the base view attributes (in particular its background) as well as the
     * Button class's attributes. The Context the view is running in, through which it can access
     * the current
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view. If 0, no style will be applied
     *            (beyond what is included in the theme). This may either be an attribute resource,
     *            whose value will be retrieved from the current theme, or an explicit style
     *            resource.
     * @see #View(Context, AttributeSet)
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcOverlapLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private boolean isHorizontal() {
        return WindowUtil.isSuitableForLandscape(getResources());
    }


    /**
     * {@inheritDoc}
     *
     * @see android.view.View#onMeasure(int, int)
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // //initial footer,header value
        int mFooterWidth = 0;
        int mFooterHeight = 0;

        final int count = getChildCount();

        final int ContentWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int ContentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int ChildWMeasureSpec = MeasureSpec.makeMeasureSpec(ContentWidth, MeasureSpec.getMode(heightMeasureSpec));
        int ChildHMeasureSpec = MeasureSpec.makeMeasureSpec(ContentHeight, MeasureSpec.getMode(heightMeasureSpec));

        /* find footer */
        HtcFooter footer = null;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof HtcFooter && View.GONE != child.getVisibility()) {
                footer = (HtcFooter) child;
                break;
            }
        }

        /* no footer */
        if (null == footer) {
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (View.GONE != child.getVisibility())
                    measureChild(child, ChildWMeasureSpec, ChildHMeasureSpec);
            }
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                    MeasureSpec.getSize(heightMeasureSpec));
            return;
        }

        /* measure footer */
        footer.measure(ChildWMeasureSpec, ChildHMeasureSpec);
        mFooterHeight = footer.getMeasuredHeight();
        mFooterWidth = footer.getMeasuredWidth();

        /* prepare the measure spec of the content */
        if (HtcFooter.DISPLAY_MODE_ALWAYSRIGHT == footer.getDisplayMode() ||
                (isHorizontal() && HtcFooter.DISPLAY_MODE_DEFAULT == footer.getDisplayMode())) {
            ChildWMeasureSpec = MeasureSpec.makeMeasureSpec(ContentWidth - mFooterWidth, MeasureSpec.getMode(heightMeasureSpec));
            ChildHMeasureSpec = MeasureSpec.makeMeasureSpec(ContentHeight, MeasureSpec.getMode(heightMeasureSpec));
        } else {
            ChildWMeasureSpec = MeasureSpec.makeMeasureSpec(ContentWidth, MeasureSpec.getMode(heightMeasureSpec));
            ChildHMeasureSpec = MeasureSpec.makeMeasureSpec(ContentHeight - mFooterHeight, MeasureSpec.getMode(heightMeasureSpec));
        }

        /* measure content */
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child == footer)
                continue;
            measureChild(child, ChildWMeasureSpec, ChildHMeasureSpec);
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    /**
     * {@inheritDoc}
     *
     * @see android.view.ViewGroup#onLayout(boolean, int, int, int, int)
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();

        final int parentLeft = 0;
        final int parentRight = right - left;

        final int parentTop = 0;
        final int parentBottom = bottom - top;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (GONE == child.getVisibility())
                continue;

            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            int childLeft = parentLeft;
            int childTop = parentTop;

            if (child instanceof HtcFooter) {
                HtcFooter footer = (HtcFooter) child;

                if (HtcFooter.DISPLAY_MODE_ALWAYSRIGHT == footer.getDisplayMode()
                        || (isHorizontal() && HtcFooter.DISPLAY_MODE_DEFAULT == footer.getDisplayMode())) {
                    childLeft = parentRight - width;
                    childTop = parentTop;
                } else {
                    childLeft = parentLeft;
                    childTop = parentBottom - height;
                }
            }

            child.layout(childLeft, childTop, childLeft + width, childTop + height);
        }
    }

    /**
     * when in stable mode,you need to explicitly tell us the action bar's visibility after you
     * called ActionBar.show() or ActionBar.hide().
     *
     * @param b action bar exist or not, true by default
     * @deprecated it's real implemention is {@link #setInsetActionbarTop(boolean)},please use
     *       {@link #setInsetActionbarTop(boolean)} instead.
     */
    public void isActionBarVisible(boolean b) {
        setInsetActionbarTop(b);
    }

    /**
     * whether we should add top actionbar's height to the top margin of this view.
     *
     * @param b true by default.
     */
    public void setInsetActionbarTop(boolean b) {
        mInsetActionBarTop = b;
        adjustSelfLayoutParameter();
    }

    /**
     * @hide {@inheritDoc}
     * @see android.view.View#requestLayout()
     */
    @Override
    public void requestLayout() {
        super.requestLayout();

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof HtcFooter) {
                child.forceLayout();
                break;
            }
        }
    }

    /* The listener to get the system window dimension */
    public interface OnGetSysWinInfoListener {
        /**
         * @param inset inset.top = statusbar's height + actionbar's height,
         *            inset.bottom = navigationbar + IME's height, inset.right =
         *            navigationbar's width
         */
        public void onGetSysWinInfo(Rect inset);
    }

    /**
     * Set the Listener to get the information of the system window
     *
     * @param getSysWinListener the mGetSysWinListener to set
     */
    public void setOnGetSysWinListener(OnGetSysWinInfoListener getSysWinListener) {
        mGetSysWinListener = getSysWinListener;
    }


    /**
     * application will trigger this to inovke adjust MyFrameLayout and the
     * children of MyFrameLayout
     */
    private void adjustSelfLayoutParameter() {
        MarginLayoutParams mlp = (MarginLayoutParams) getLayoutParams();
        if (null == mlp) {
            mlp = new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            /* always match parent for workaround translucent */
            mlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            mlp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }

        /* inset top margin for actionbar and status bar */
        mlp.topMargin = 0;
        /* inset bottom margin for portrait mode navigation bar */
        mlp.bottomMargin = 0;
        /* inset right margin for landscape mode navigation bar */
        mlp.rightMargin = 0;

        if (mInsetStatusBar) {
            mlp.topMargin += mStatusBarHeight > 0 ? mStatusBarHeight : 0;
        }
        if (mInsetActionBarTop) {
            mlp.topMargin += getActionBarTopHeight();
        }
        /*
         * portrait: navigation bar(bottom) + IME(bottom) landscape: navigation
         * bar(right) + IME(bottom)
         */
        if (mInsetRightSystemWindow) {
            mlp.rightMargin = mRightSystemWindow;
        }
        if (mInsetBottomSystemWindow) {
            mlp.bottomMargin = mBottomSystemWindow;
        }

        setLayoutParams(mlp);

        requestLayout();
    }

    /**
     * Set HtcOverLayout need to inset right margin for bottom
     * SystemWindow(ex:NavigationBar, IME+ NavigationBar)
     *
     * @param insetBottomSystemWindow the insetNavigationBar to set, true by default
     * @hide
     */
    public final void setInsetBottomSystemWindow(boolean insetBottomSystemWindow) {
        mInsetBottomSystemWindow = insetBottomSystemWindow;
    }

    /**
     * Set HtcOverLayout need to inset right margin for right
     * SystemWindow(ex:NavigationBar)
     *
     * @param insetRightSystemWindow inset the right system window, true by default
     * @hide
     */
    public final void setInsetRightSystemWindow(boolean insetRightSystemWindow) {
        mInsetRightSystemWindow = insetRightSystemWindow;
    }

    private int getActionBarTopHeight() {
        if (mActionbarContainerView != null) {
            mActionBarTopHeight = mActionbarContainerView.getMeasuredHeight();
        } else {
            mActionBarTopHeight = 0;
        }
        if (DEBUG) {
            Log.d(TAG, "getActionBarHeight=" + mActionBarTopHeight);
        }
        return mActionBarTopHeight;
    }
}
