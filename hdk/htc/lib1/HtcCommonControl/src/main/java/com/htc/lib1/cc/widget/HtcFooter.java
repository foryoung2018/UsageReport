/**
 * HTC Corporation Proprietary Rights Acknowledgment
 *
 * Copyright (C) 2011 HTC Corporation
 *
 * All Rights Reserved.
 *
 * The information contained in this work is the exclusive property of
 * HTC Corporation ("HTC"). Only the user who is legally
 * authorized by HTC ("Authorized User") has right to employ this work
 * within the scope of this statement. Nevertheless, the Authorized User
 * shall not use this work for any purpose other than the purpose agreed by HTC.
 * Any and all addition or modification to this work shall be unconditionally
 * granted back to HTC and such addition or modification shall be solely owned by HTC.
 * No right is granted under this statement, including but not limited to,
 * distribution, reproduction, and transmission, except as otherwise provided in this statement.
 * Any other usage of this work shall be subject to the further written consent of HTC.
 *
 * @author: Walt Li
 * @date: 2012/01/18
 */

package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.widget.LinearLayout;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.cc.util.WindowUtil;

/**
 * <p>
 * HtcFooter extends android {@link android.view.ViewGroup} and display the HTC
 * style footer. It has default width and height and the user doesn't need to
 * care about its size. Using HtcFooter just likes android
 * {@link android.view.ViewGroup}.
 * </p>
 *
 * The following is the example:
 *
 * <pre>
 *  &lt;com.htc.HtcFooter android:layout_height="wrap_content" android:layout_width="match_parent" android:id="@+id/footer"&gt;
 *       &lt;com.htc.HtcFooterButton android:text="XML Button 0" android:id="@+id/button1"/&gt;
 *       &lt;com.htc.HtcFooterButton android:text="XML Button 1" android:id="@+id/button2"/&gt;
 *   &lt;/com.htc.HtcFooter&gt;
 * </pre>
 *
 * @author Felka Chang
 *
 */
public class HtcFooter extends ViewGroup {
    /**
     * The default value of child weight, the default value is 1 and each of
     * children will be equal size.
     */
    private static final float DEF_CHILD_WEIGHT = 1.0f;
    // modified set always mode

    /**
     * Default display mode
     */
    public static final int DISPLAY_MODE_DEFAULT = 0x00;

    /**
     * Right display
     */
    public static final int DISPLAY_MODE_ALWAYSRIGHT = 0x01;

    /**
     * Bottom display
     */
    public static final int DISPLAY_MODE_ALWAYSBOTTOM = 0x02;

    /**
     * Light Mode
     */
    public static final int STYLE_MODE_LIGHT = 0x00;

    /**
     * Dark Mode
     */
    public static final int STYLE_MODE_DARK = 0x01;

    /**
     * ColorFul Mode
     */
    public static final int STYLE_MODE_COLORFUL = 0x07;

    /**
     * Transparent Mode
     */
    public static final int STYLE_MODE_TRANSPARENT = 0x08;

    /**
     * PureLight Mode
     */
    public static final int STYLE_MODE_PURELIGHT = 0x09;

    /**
     * Default Mode
     */
    public static final int STYLE_MODE_DEFAULT = STYLE_MODE_LIGHT;

    private static final int BOTTOM_BACKGROUND_DIVIDER_LIGHT = 0;
    private static final int BOTTOM_BACKGROUND_DIVIDER_BLACK = 1;
    /**
     * Get height property
     */
    public static final int GET_DEFAULT_HEIGHT = 0;

    /**
     * Get width property
     */
    public static final int GET_DEFAULT_WIDTH = 1;

    private static final boolean DEBUG = HtcBuildFlag.Htc_DEBUG_flag;

    private static final String TAG = "HtcFooter";

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = DISPLAY_MODE_DEFAULT, to = "DISPLAY_MODE_DEFAULT"),
            @IntToString(from = DISPLAY_MODE_ALWAYSBOTTOM, to = "DISPLAY_MODE_ALWAYSBOTTOM"),
            @IntToString(from = DISPLAY_MODE_ALWAYSRIGHT, to = "DISPLAY_MODE_ALWAYSRIGHT")
    })
    private int mDisplayMode = DISPLAY_MODE_DEFAULT;

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = STYLE_MODE_DEFAULT, to = "STYLE_MODE_DEFAULT"),
            @IntToString(from = STYLE_MODE_COLORFUL, to = "STYLE_MODE_COLORFUL"),
            @IntToString(from = STYLE_MODE_DARK, to = "STYLE_MODE_DARK"),
            @IntToString(from = STYLE_MODE_LIGHT, to = "STYLE_MODE_LIGHT"),
            @IntToString(from = STYLE_MODE_TRANSPARENT, to = "STYLE_MODE_TRANSPARENT"),
            @IntToString(from = STYLE_MODE_PURELIGHT, to = "STYLE_MODE_PURELIGHT")
    })
    private int mStyleMode = STYLE_MODE_DEFAULT;

    @ExportedProperty(category = "CommonControl")
    private int mSeperatorWidth;

    @ExportedProperty(category = "CommonControl")
    private int mSeperatorHeight;

    @ExportedProperty(category = "CommonControl")
    private int mOneChildWidth;

    @ExportedProperty(category = "CommonControl")
    private int mOneChildHeight;

    private Drawable mSeparatorDrawable;
    private LayerDrawable mDividerLayerDrawable;

    @ExportedProperty(category = "CommonControl")
    private int mPortraitDefHeight;

    @ExportedProperty(category = "CommonControl")
    private int mLandScapreDefWidth;

    private LayerDrawable mBackgroundDrawable;

    @ExportedProperty(category = "CommonControl")
    private int dividerM2 = 0;

    @ExportedProperty(category = "CommonControl")
    private int mLeftRightPadding;

    @ExportedProperty(category = "CommonControl")
    private boolean mShrinkTouchAreaEnabled;

    @ExportedProperty(category = "CommonControl")
    private int mShrinkTouchAreaOffsetLeft;

    @ExportedProperty(category = "CommonControl")
    private int mShrinkTouchAreaOffsetRight;

    @ExportedProperty(category = "CommonControl")
    private int[] mLocation = new int[2];
    private Rect rect = new Rect();

    /**
     * @hide
     */
    public int getOneChildWidth() {
        return mOneChildWidth;
    }

    /**
     * @hide
     */
    public int getOneChildHeight() {
        return mOneChildHeight;
    }

    /**
     * @hide
     */
    public void setOneChildWidth(int nOneChildWidth) {
        if (0 > nOneChildWidth) {
            this.mOneChildWidth = 0;
        } else {
            this.mOneChildWidth = nOneChildWidth;
        }
    }

    /**
     * @hide
     */
    public void setOneChildHeight(int nOneChildHeight) {
        if (0 > nOneChildHeight) {
            this.mOneChildHeight = 0;
        } else {
            this.mOneChildHeight = nOneChildHeight;
        }
    }

    /**
     * Simple constructor to use when creating a view from code.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcFooter(Context context) {
        this(context, null);
    }

    /**
     * Simple constructor to use when creating a view from code.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @hide
     */
    public HtcFooter(Context context, int styleMode) {
        this(context, styleMode, null);
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
     */
    public HtcFooter(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.footerStyle);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     *
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs
     *            The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     */
    public HtcFooter(Context context, int styleMode, AttributeSet attrs) {
        this(context, styleMode, attrs, R.attr.footerStyle);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of View allows subclasses to use their own base style when
     * they are inflating. For example, a Button class's constructor would call
     * this version of the super class constructor and supply
     * <code>R.attr.buttonStyle</code> for <var>defStyle</var>; this allows the
     * theme's button style to modify all of the base view attributes (in
     * particular its background) as well as the Button class's attributes.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs
     *            The attributes of the XML tag that is inflating the view.
     * @param defStyle
     *            The default style to apply to this view. If 0, no style will
     *            be applied (beyond what is included in the theme). This may
     *            either be an attribute resource, whose value will be retrieved
     *            from the current theme, or an explicit style resource.
     * @see #View(Context, AttributeSet)
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcFooter(Context context, AttributeSet attrs, int defStyle) {
        this(context, STYLE_MODE_DEFAULT, attrs, R.attr.footerStyle);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of View allows subclasses to use their own base style when
     * they are inflating. For example, a Button class's constructor would call
     * this version of the super class constructor and supply
     * <code>R.attr.buttonStyle</code> for <var>defStyle</var>; this allows the
     * theme's button style to modify all of the base view attributes (in
     * particular its background) as well as the Button class's attributes.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs
     *            The attributes of the XML tag that is inflating the view.
     * @param defStyle
     *            The default style to apply to this view. If 0, no style will
     *            be applied (beyond what is included in the theme). This may
     *            either be an attribute resource, whose value will be retrieved
     *            from the current theme, or an explicit style resource.
     * @see #View(Context, AttributeSet)
     */
    public HtcFooter(Context context, int styleMode, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mStyleMode = styleMode;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HtcFooter, defStyle, R.style.FooterBarStyle);

        mStyleMode = a.getInt(R.styleable.HtcFooter_backgroundMode, STYLE_MODE_DEFAULT);
        mDividerLayerDrawable = (LayerDrawable) a.getDrawable(R.styleable.HtcFooter_android_divider);
        mBackgroundDrawable = (LayerDrawable) a.getDrawable(R.styleable.HtcFooter_android_src);

        // sometime, we can't get drawable.
        if (mBackgroundDrawable == null) {
            mBackgroundDrawable = (LayerDrawable) context.getResources().getDrawable(R.drawable.footerbkg);
        }
        int nDisplayMode = a.getInt(R.styleable.HtcFooter_footer_display_mode, DISPLAY_MODE_DEFAULT);
        a.recycle();

        mPortraitDefHeight = ensureIntEven(context.getResources().getDimensionPixelSize(R.dimen.htc_footer_height));
        mLandScapreDefWidth = ensureIntEven(context.getResources().getDimensionPixelSize(R.dimen.htc_footer_width));
        mLeftRightPadding = context.getResources().getDimensionPixelOffset(R.dimen.footer_two_side_padding);
        dividerM2 = context.getResources().getDimensionPixelOffset(R.dimen.margin_m);
        SetDisplayMode(nDisplayMode);

        mShrinkTouchAreaOffsetRight = mShrinkTouchAreaOffsetLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, context.getResources().getDisplayMetrics());

    }

    private int ensureIntEven(int i) {
        return (i & 1) == 0 ? i : (++i);
    }

    private boolean isInRight() {
        if (mDisplayMode == DISPLAY_MODE_ALWAYSRIGHT) {
            return true;
        } else if (mDisplayMode == DISPLAY_MODE_ALWAYSBOTTOM) {
            return false;
        } else {
            return isHorizontal();
        }
    }

    private void initFooter() {
        if (null == mDividerLayerDrawable) {
            mDividerLayerDrawable = (LayerDrawable) getResources().getDrawable(
                    R.drawable.common_footer_divider);
            Log.e(TAG, "mDividerLayerDrawable is NULL", new Exception());
        }

        if (mStyleMode == STYLE_MODE_LIGHT || mStyleMode == STYLE_MODE_PURELIGHT)
            setSeparatorDrawable(mDividerLayerDrawable.getDrawable(BOTTOM_BACKGROUND_DIVIDER_LIGHT));
        else
            setSeparatorDrawable(mDividerLayerDrawable.getDrawable(BOTTOM_BACKGROUND_DIVIDER_BLACK));

        setShrinkTouchAreaEnabled(!isInRight());

        if (isShrinkTouchAreaEnabled()) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();

            int wInch = metrics.widthPixels;
            int hInch = metrics.heightPixels;

            if (wInch <= 320 && hInch <= 480) {
                android.util.Log.d("HtcFooter", "force disable footerbar shrink");
                setShrinkTouchAreaEnabled(false);
            }
        }

        setBackgroundStyleMode(mStyleMode);

    }

    @ExportedProperty(category = "CommonControl")
    private int getVisibleChildCount() {
        int nVisibleCount = 0;
        int nChildren = this.getChildCount();
        int i;
        View child;
        for (i = 0; i < nChildren; i++) {
            child = getChildAt(i);
            if (null != child && child.getVisibility() != GONE) {
                nVisibleCount++;
            }
        }
        return nVisibleCount;
    }

    @ExportedProperty(category = "CommonControl")
    private int getChildWeight(View child) {
        if (child.getVisibility() != GONE) {
            try {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();
                return (int) ((1.0f >= lp.weight) ? 1 : lp.weight);
            } catch (Exception e) {
                e.printStackTrace();
                return 1;
            }
        } else {
            return 0;
        }
    }

    @ExportedProperty(category = "CommonControl")
    private int getChildrenTotalWeight() {
        int nTotalWeight = 0;
        int nChildren = this.getChildCount();
        float fChildWeight;
        for (int i = 0; i < nChildren; i++) {
            fChildWeight = getChildWeight(getChildAt(i));
            nTotalWeight += (1f > fChildWeight) ? 0 : (int) (fChildWeight);
        }
        return nTotalWeight;
    }

    /**
     * {@inheritDoc}
     *
     * @see android.view.ViewGroup#onLayout(boolean, int, int, int, int)
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        final int nChildren = this.getChildCount();
        final int nVisibleCount = getVisibleChildCount();

        final int parentLeft = getPaddingLeft();
        final int parentRight = right - left - getPaddingRight();

        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();
        View child;

        switch (nVisibleCount) {
            case 1:
                for (int i = 0; i < nChildren; i++) {
                    child = getChildAt(i);
                    if (null != child && child.getVisibility() != GONE) {
                        child.layout(parentLeft, parentTop, parentRight, parentBottom);
                    }
                }
                break;
            default:
                final int npl = getPaddingLeft();
                final int npt = getPaddingTop();
                int nLastLeft = npl;
                int nLastTop = npt;
                int childTop = getPaddingTop();
                int childLeft = getPaddingLeft();
                for (int i = 0; i < nChildren; i++) {
                    if (mReverseEnabled == true && isHorizontal() == true) {
                        child = getChildAt(nChildren - i - 1);
                    } else {
                        child = getChildAt(i);
                    }

                    if (null != child && child.getVisibility() != GONE) {
                        switch (mDisplayMode) {
                            case DISPLAY_MODE_ALWAYSBOTTOM: {
                                childLeft = nLastLeft;
                                child.layout(childLeft, nLastTop, childLeft + child.getMeasuredWidth(), nLastTop + child.getMeasuredHeight());
                                nLastLeft += child.getMeasuredWidth();
                            }
                                break;
                            case DISPLAY_MODE_ALWAYSRIGHT: {
                                childTop = nLastTop;
                                child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
                                nLastTop += child.getMeasuredHeight();
                            }
                                break;
                            default: {
                                if (isHorizontal()) {

                                    childTop = nLastTop;
                                    child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
                                    nLastTop += child.getMeasuredHeight();
                                } else {
                                    childLeft = nLastLeft;
                                    child.layout(childLeft, nLastTop, childLeft + child.getMeasuredWidth(), nLastTop + child.getMeasuredHeight());
                                    nLastLeft += child.getMeasuredWidth();
                                }
                            }
                                break;
                        }
                    }
                }
                break;
        }
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
        final int npl = getPaddingLeft();
        final int npr = getPaddingRight();
        final int npt = getPaddingTop();
        final int npb = getPaddingBottom();
        int nWidth = MeasureSpec.getSize(widthMeasureSpec);
        int nHeight = MeasureSpec.getSize(heightMeasureSpec);

        ViewGroup.LayoutParams vglp = this.getLayoutParams();
        switch (mDisplayMode) {
            case DISPLAY_MODE_ALWAYSBOTTOM: {
                final int width = MeasureSpec.getSize(widthMeasureSpec);
                if (null == vglp || (0 >= vglp.height || ViewGroup.LayoutParams.WRAP_CONTENT == vglp.height || ViewGroup.LayoutParams.MATCH_PARENT == vglp.height)) {
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.getSize(heightMeasureSpec));
                }
                nHeight = View.getDefaultSize(mPortraitDefHeight, heightMeasureSpec);
                nWidth = width;
            }
                break;
            case DISPLAY_MODE_ALWAYSRIGHT: {
                final int height = MeasureSpec.getSize(heightMeasureSpec);
                if (null == vglp || (0 >= vglp.width || ViewGroup.LayoutParams.WRAP_CONTENT == vglp.width || ViewGroup.LayoutParams.MATCH_PARENT == vglp.width)) {
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.getSize(widthMeasureSpec));
                }
                nWidth = View.getDefaultSize(mLandScapreDefWidth, widthMeasureSpec);
                nHeight = height;
            }
                break;
            default: {
                if (isHorizontal()) {
                    final int height = MeasureSpec.getSize(heightMeasureSpec);
                    if (null == vglp || (0 >= vglp.width || ViewGroup.LayoutParams.WRAP_CONTENT == vglp.width || ViewGroup.LayoutParams.MATCH_PARENT == vglp.width)) {
                        widthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.getSize(widthMeasureSpec));
                    }
                    nWidth = View.getDefaultSize(mLandScapreDefWidth, widthMeasureSpec);
                    nHeight = height;
                } else {
                    final int width = MeasureSpec.getSize(widthMeasureSpec);
                    if (null == vglp || (0 >= vglp.height || ViewGroup.LayoutParams.WRAP_CONTENT == vglp.height || ViewGroup.LayoutParams.MATCH_PARENT == vglp.height)) {
                        heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.getSize(heightMeasureSpec));
                    }
                    nHeight = View.getDefaultSize(mPortraitDefHeight, heightMeasureSpec);
                    nWidth = width;
                }
            }
                break;
        }

        if (DEBUG) {
            android.util.Log.d("HtcFooter", "Horizontal =" + isHorizontal());
            android.util.Log.d("HtcFooter", "Width =" + nWidth);
            android.util.Log.d("HtcFooter", "Height=" + nHeight);
        }
        final int nContentHeight = nHeight - npt - npb;
        if (nContentHeight < 0) {
            Log.e(TAG, "nHeight - npt - npb < 0 , " +
                    "nHeight = " + nHeight + " , " +
                    "paddingTop = " + npt + " , " +
                    "paddingBottom = " + npb, new Exception());
        }
        final int nChildren = this.getChildCount();
        final int nVisibleCount = getVisibleChildCount();

        final int nTotalChildrenWidth = nWidth - npl - npr;
        if (nTotalChildrenWidth < 0) {
            Log.e(TAG, " nWidth - npl - npr < 0 , " +
                    "nWidth = " + nWidth + " , " +
                    "paddingLeft = " + npl + " , " +
                    "paddingRight = " + npr, new Exception());
        }

        int nChildWMeasureSpec = 0;
        int nChildHMeasureSpec = 0;
        int nChildWidth, nChildWeight;
        View child;

        switch (nVisibleCount) {
            case 1:
                for (int i = 0; i < nChildren; i++) {
                    child = getChildAt(i);
                    if (child.getVisibility() != GONE) {
                        nChildWMeasureSpec = MeasureSpec.makeMeasureSpec(nTotalChildrenWidth, MeasureSpec.EXACTLY);
                        nChildHMeasureSpec = MeasureSpec.makeMeasureSpec(nContentHeight, MeasureSpec.EXACTLY);
                        child.measure(nChildWMeasureSpec, nChildHMeasureSpec);
                    }
                }
                break;
            default:
                final int nTotalWeight = getChildrenTotalWeight();
                final int nUnitWidth = (0 < nTotalWeight) ? ((nTotalChildrenWidth) / nTotalWeight) : nTotalChildrenWidth;
                final int nUnitHeight = (0 < nTotalWeight) ? ((nContentHeight) / nTotalWeight) : nContentHeight;
                final int nRemainderWidth = (0 < nTotalWeight) ? ((nTotalChildrenWidth) % nTotalWeight) : 0;
                final int nRemainderHeight = (0 < nTotalWeight) ? ((nContentHeight) % nTotalWeight) : 0;

                int nRestWidth = nRemainderWidth;
                int nRestHeight = nRemainderHeight;

                for (int i = 0; i < nChildren; i++) {
                    child = getChildAt(i);
                    final int nVisChildWeight = getChildWeight(child);

                    if (child.getVisibility() != GONE) {
                        switch (mDisplayMode) {
                            case DISPLAY_MODE_ALWAYSBOTTOM: {
                                nChildWidth = nVisChildWeight * nUnitWidth;

                                if (0 < nRestWidth && 0 < nVisChildWeight) {
                                    nChildWidth += Math.min(nVisChildWeight, nRestWidth);
                                }

                                nChildWMeasureSpec = MeasureSpec.makeMeasureSpec(nChildWidth, MeasureSpec.EXACTLY);
                                nChildHMeasureSpec = MeasureSpec.makeMeasureSpec(nContentHeight, MeasureSpec.EXACTLY);
                                if (nRestWidth > 0 && 0 < nVisChildWeight) {
                                    nRestWidth += Math.min(nVisChildWeight, nRestWidth);
                                }
                            }
                                break;
                            case DISPLAY_MODE_ALWAYSRIGHT: {
                                nChildWeight = nVisChildWeight * nUnitHeight;

                                if (0 < nRestHeight && 0 < nVisChildWeight) {
                                    nChildWeight += Math.min(nRestHeight, nVisChildWeight);
                                }

                                nChildWMeasureSpec = MeasureSpec.makeMeasureSpec(nTotalChildrenWidth, MeasureSpec.EXACTLY);
                                nChildHMeasureSpec = MeasureSpec.makeMeasureSpec(nChildWeight, MeasureSpec.EXACTLY);

                                if (nRestHeight > 0 && 0 < nVisChildWeight) {
                                    nRestHeight += Math.min(nVisChildWeight, nRestHeight);
                                }
                            }
                                break;
                            default: {
                                if (isHorizontal()) {
                                    nChildWeight = nVisChildWeight * nUnitHeight;

                                    if (0 < nRestHeight && 0 < nVisChildWeight) {
                                        nChildWeight += Math.min(nRestHeight, nVisChildWeight);
                                    }

                                    nChildWMeasureSpec = MeasureSpec.makeMeasureSpec(nTotalChildrenWidth, MeasureSpec.EXACTLY);
                                    nChildHMeasureSpec = MeasureSpec.makeMeasureSpec(nChildWeight, MeasureSpec.EXACTLY);

                                    if (nRestHeight > 0 && 0 < nVisChildWeight) {
                                        nRestHeight += Math.min(nVisChildWeight, nRestHeight);
                                    }
                                } else {
                                    nChildWidth = nVisChildWeight * nUnitWidth;

                                    if (0 < nRestWidth && 0 < nVisChildWeight) {
                                        nChildWidth += Math.min(nVisChildWeight, nRestWidth);
                                    }

                                    nChildWMeasureSpec = MeasureSpec.makeMeasureSpec(nChildWidth, MeasureSpec.EXACTLY);
                                    nChildHMeasureSpec = MeasureSpec.makeMeasureSpec(nContentHeight, MeasureSpec.EXACTLY);
                                    if (nRestWidth > 0 && 0 < nVisChildWeight) {
                                        nRestWidth += Math.min(nVisChildWeight, nRestWidth);
                                    }
                                }
                            }
                                break;
                        }
                        child.measure(nChildWMeasureSpec, nChildHMeasureSpec);
                    }
                }
                break;
        }

        this.setMeasuredDimension(View.getDefaultSize(nWidth, widthMeasureSpec), View.getDefaultSize(nHeight, heightMeasureSpec));
    }

    /**
     * {@inheritDoc}
     *
     * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && isShrinkTouchAreaEnabled()) {
            getLocationInWindow(mLocation);
            int x = (int) event.getX();
            // do not dispatch action_down event to footer buttons when user
            // touches on screen edge
            if ((x < mLocation[0] + mShrinkTouchAreaOffsetLeft) || (x > mLocation[0] + getMeasuredWidth() - mShrinkTouchAreaOffsetRight)) {
                return true;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void setShrinkTouchAreaEnabled(boolean enabled) {
        mShrinkTouchAreaEnabled = enabled;
    }

    private boolean isShrinkTouchAreaEnabled() {
        return mShrinkTouchAreaEnabled;
    }

    /**
     * @hide
     */
    public int getSeperatorWidth() {
        return mSeperatorWidth;
    }

    /**
     * @hide
     */
    public int getSeperatorHeight() {
        return mSeperatorHeight;
    }

    /**
     * @hide
     */
    public void setSeperatorWidth(int nSeperatorWidth) {
        if (0 > nSeperatorWidth) {
            this.mSeperatorWidth = 0;
        } else {
            this.mSeperatorWidth = nSeperatorWidth;
        }
    }

    /**
     * @hide
     */
    public void setSeperatorHeight(int nSeperatorHeight) {
        if (0 > nSeperatorHeight) {
            this.mSeperatorHeight = 0;
        } else {
            this.mSeperatorHeight = nSeperatorHeight;
        }
    }

    /**
     * Replace the generateDefaultLayoutParams of ViewGroup, it need's to use
     * LinearLayout.LayoutParams. Why overwrite? we need to assign the default
     * weight of each child
     *
     * @see android.view.ViewGroup#generateDefaultLayoutParams()
     * @return layout params
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected LinearLayout.LayoutParams generateDefaultLayoutParams() {
        // TODO Auto-generated method stub
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(super.generateDefaultLayoutParams());
        lp.weight = DEF_CHILD_WEIGHT;
        return lp;
    }

    /**
     * Replace the generateDefaultLayoutParams of ViewGroup, it need's to use
     * LinearLayout.LayoutParams. Why overwrite? we need to assign the default
     * weight of each child
     *
     * @param attrs
     *            attribute set
     * @see android.view.ViewGroup#generateLayoutParams(android.util.AttributeSet)
     * @return layout params
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    public LinearLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        // TODO Auto-generated method stub
        ViewGroup.LayoutParams vglp;
        try {
            vglp = super.generateLayoutParams(attrs);
        } catch (Exception e) {
            vglp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(vglp);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LinearLayout_Layout);
        float fWeight = a.getFloat(R.styleable.LinearLayout_Layout_android_layout_weight, DEF_CHILD_WEIGHT);
        a.recycle();

        lp.weight = (float) fWeight;
        return lp;
    }

    /**
     * Replace the generateDefaultLayoutParams of ViewGroup, it need's to use
     * LinearLayout.LayoutParams. Why overwrite? we need to assign the default
     * weight of each child
     *
     * @param p
     *            layout params
     * @return layout params
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        // TODO Auto-generated method stub
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(super.generateLayoutParams(p));
        lp.weight = DEF_CHILD_WEIGHT;
        return lp;
    }

    /**
     * {@inheritDoc}
     *
     * @see android.view.View#onDraw(android.graphics.Canvas)
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        Drawable d = getSeparatorDrawable();
        if ((!enableDivider) || (null == d))
            return;

        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        View lastView = null;
        final int nChildren = this.getChildCount();
        final int nVisibleChildren = this.getVisibleChildCount();
        int nSperatorWidth = getSeperatorWidth();
        if (nSperatorWidth < 1)
            nSperatorWidth = 1;
        int nSperatorHeihgt = getSeperatorHeight();
        if (nSperatorHeihgt < 1)
            nSperatorHeihgt = 1;

        if (1 < nVisibleChildren && null != d) {
            View child;
            for (int i = 0; i < nChildren; i++) {
                child = getChildAt(i);
                if (child.getVisibility() != GONE) {
                    if (null != lastView) {
                        switch (mDisplayMode) {
                            case DISPLAY_MODE_ALWAYSBOTTOM: {
                                rect.set(lastView.getRight() - nSperatorWidth, child.getTop() + dividerM2, lastView.getRight(), child.getBottom() - dividerM2);
                            }
                                break;
                            case DISPLAY_MODE_ALWAYSRIGHT: {

                                if (mReverseEnabled) {
                                    rect.set(child.getLeft() + dividerM2, lastView.getTop() - nSperatorHeihgt, child.getRight() - dividerM2, lastView.getTop());
                                } else {
                                    rect.set(child.getLeft() + dividerM2, lastView.getBottom() - nSperatorHeihgt, child.getRight() - dividerM2, lastView.getBottom());
                                }
                            }
                                break;
                            default: {
                                if (isHorizontal()) {
                                    if (mReverseEnabled) {
                                        rect.set(child.getLeft() + dividerM2, lastView.getTop() - nSperatorHeihgt, child.getRight() - dividerM2, lastView.getTop());
                                    } else {
                                        rect.set(child.getLeft() + dividerM2, lastView.getBottom() - nSperatorHeihgt, child.getRight() - dividerM2, lastView.getBottom());
                                    }
                                } else {
                                    rect.set(lastView.getRight() - nSperatorWidth, child.getTop() + dividerM2, lastView.getRight(), child.getBottom() - dividerM2);
                                }
                            }
                                break;
                        }
                        d.setBounds(rect);
                        d.draw(canvas);
                    }
                    lastView = child;
                }
            }
        }
        canvas.restoreToCount(saveCount);
    }

    /**
     * {@inheritDoc}
     *
     * @see android.view.View#draw(android.graphics.Canvas)
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    /**
     * set htcFooter background style mode
     *
     * @param styleMode
     *         Dark
     *         Light
     *
     */
    public void setBackgroundStyleMode(int styleMode) {
        Drawable backgroundDrawable = null;

        mStyleMode = styleMode;

        final int leftPadding = getPaddingLeft();
        final int rightPadding = getPaddingRight();
        final int topPadding = getPaddingTop();
        final int bottomPadding = getPaddingBottom();

        int layerDrawableIndex = -1;

        switch (mDisplayMode) {

            case DISPLAY_MODE_ALWAYSBOTTOM:
                layerDrawableIndex = displayStyleModePortrait(mStyleMode);
                break;
            case DISPLAY_MODE_ALWAYSRIGHT:
                layerDrawableIndex = displayStyleModeLandscape(mStyleMode);
                break;
            default: {
                if (isHorizontal()) {
                    layerDrawableIndex = displayStyleModeLandscape(mStyleMode);
                } else {
                    layerDrawableIndex = displayStyleModePortrait(mStyleMode);
                }
            }
                break;
        }
        // setup footer background
        if (layerDrawableIndex != -1) {
            android.util.Log.d("HtcFooter", "layerDrawableIndex = " + layerDrawableIndex);
            if (mBackgroundDrawable != null)
                backgroundDrawable = mBackgroundDrawable.getDrawable(layerDrawableIndex);
            else
                backgroundDrawable = getResources().getDrawable(R.drawable.common_app_bkg_down_src);
            setBackgroundDrawable(backgroundDrawable);
        }

        /** restore padding states */
        setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
    }

    @ExportedProperty(category = "CommonControl")
    private int displayStyleModePortrait(int styleMode){
        int layerDrawableIndex = -1;
            if (styleMode == STYLE_MODE_LIGHT) {
                layerDrawableIndex = 0;
            } else if (styleMode == STYLE_MODE_DARK) {
                layerDrawableIndex = 1;
            } else if (styleMode == STYLE_MODE_TRANSPARENT) {
                layerDrawableIndex = 2;
            } else if (styleMode == STYLE_MODE_PURELIGHT) {
                layerDrawableIndex = 3;
            }
            return layerDrawableIndex;
    }

    @ExportedProperty(category = "CommonControl")
    private int displayStyleModeLandscape(int styleMode) {
        int layerDrawableIndex = -1;
        if (styleMode == STYLE_MODE_LIGHT) {
            layerDrawableIndex = 4;
        } else if (styleMode == STYLE_MODE_DARK) {
            layerDrawableIndex = 5;
        } else if (styleMode == STYLE_MODE_TRANSPARENT) {
            layerDrawableIndex = 6;
        } else if (styleMode == STYLE_MODE_PURELIGHT) {
            layerDrawableIndex = 7;
        }
        return layerDrawableIndex;
    }

    private Drawable getSeparatorDrawable() {
        return mSeparatorDrawable;
    }

    private void setSeparatorDrawable(Drawable seperator) {
        this.mSeparatorDrawable = seperator;
        if (null == seperator) {
            setSeperatorWidth(0);
            return;
        }
        if (seperator instanceof BitmapDrawable) {
            switch (mDisplayMode) {
                case DISPLAY_MODE_ALWAYSBOTTOM: {
                    setSeperatorWidth(((BitmapDrawable) seperator).getIntrinsicWidth());
                }
                    break;
                case DISPLAY_MODE_ALWAYSRIGHT: {
                    setSeperatorHeight(((BitmapDrawable) seperator).getIntrinsicHeight());
                }
                    break;
                default: {
                    if (isHorizontal()) {
                        setSeperatorHeight(((BitmapDrawable) seperator).getIntrinsicHeight());
                    } else {
                        setSeperatorWidth(((BitmapDrawable) seperator).getIntrinsicWidth());
                    }
                }
                    break;
            }
        } else if (seperator instanceof NinePatchDrawable) {
            switch (mDisplayMode) {
                case DISPLAY_MODE_ALWAYSBOTTOM: {
                    setSeperatorWidth(((NinePatchDrawable) seperator).getIntrinsicWidth());
                }
                    break;
                case DISPLAY_MODE_ALWAYSRIGHT: {
                    setSeperatorHeight(((NinePatchDrawable) seperator).getIntrinsicHeight());
                }
                    break;
                default: {
                    if (isHorizontal()) {
                        setSeperatorHeight(((NinePatchDrawable) seperator).getIntrinsicHeight());
                    } else {
                        setSeperatorWidth(((NinePatchDrawable) seperator).getIntrinsicWidth());
                    }
                }
                    break;
            }
        } else {
            return;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see android.view.View#onConfigurationChanged(android.content.res.Configuration)
     */
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private boolean isHorizontal() {
        return WindowUtil.isSuitableForLandscape(getResources());
    }

    /**
     * {@inheritDoc}
     *
     * @see android.view.View#onSizeChanged(int, int, int, int)
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initFooter();
    }

    /**
     * set Display mode
     *
     * @param mode
     *            :DISPLAY_MODE_ALWAYSBOTTOM, DISPLAY_MODE_ALWAYSRIGHT,
     *            DISPLAY_MODE_DEFAULT
     */
    public void SetDisplayMode(int mode) {
        switch (mode) {
            case DISPLAY_MODE_ALWAYSBOTTOM:
            case DISPLAY_MODE_ALWAYSRIGHT: {
                mDisplayMode = mode;
            }
                break;
            default: {
                mDisplayMode = DISPLAY_MODE_DEFAULT;
            }
                break;
        }
        initFooter();
    }

    /**
     * Only used by OverlapLayout.
     * @return the display mode
     */
    /** @hide */
    public int getDisplayMode() {
        return mDisplayMode;
    }

    @ExportedProperty(category = "CommonControl")
    private boolean mReverseEnabled = false;

    /**
     * Reverse child sequence in landscape mode
     *
     * @param enabled
     *            true or false
     */
    public void ReverseLandScapeSequence(boolean enabled) {

        if (mReverseEnabled != enabled) {
            mReverseEnabled = enabled;
            requestLayout();
        }
    }

    @ExportedProperty(category = "CommonControl")
    private boolean enableDivider = true;

    /**
     * enable or disable divider on footerbar
     *
     * @param enabled
     *            true, false
     */
    public void setDividerEnabled(boolean enabled) {

        if (enableDivider == enabled)
            return;

        enableDivider = enabled;
        invalidate();
    }

    /**
     * get footer bar default property
     *
     * @param property
     *            determine which property
     * @return property value
     */
    @ExportedProperty(category = "CommonControl")
    public int getFooterDefaultProperty(int property) {

        int reval = -1;

        switch (property) {
            case GET_DEFAULT_HEIGHT:
                reval = mPortraitDefHeight;
                break;
            case GET_DEFAULT_WIDTH:
                reval = mLandScapreDefWidth;
                break;
            default:
                android.util.Log.w("HtcFooter", "does not support property " + property);
                break;
        }

        return reval;
    }

    /**
     * ENABLE or DISABLE HtcFooter in ThumbMode,
     * FORCED TO CHANGE the padding of HtcFooter and IGNORE values how setting before it,
     * MeanWhile, use this function should be more careful, the feature was design for portrait style HtcFooter.
     * @param enable
     *                      true, force override original padding
     *                      false, force clean original padding to zero
     */
    public void enableThumbMode(boolean enable) {
        if (enable && mLeftRightPadding > 0) {
            setPadding(mLeftRightPadding, 0, mLeftRightPadding, 0);
        } else {
            setPadding(0, 0, 0, 0);
        }
    }
}
