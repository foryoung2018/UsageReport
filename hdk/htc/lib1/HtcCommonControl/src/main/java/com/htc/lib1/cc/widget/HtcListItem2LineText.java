package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.htc.lib1.cc.R;

/**
 * This component is used with HtcListItem. There are 2 lines of texts in the
 * component by default. For the variants:
 * <ul>
 * <li>Default
 *
 * <pre class="prettyprint">
 *  ---------------
 *  | Line 1 Text |   setPrimaryText("Line 1 Text");
 *  |             |
 *  | Line 2 Text |   setSecondaryText("Line 2 Text");
 *  ---------------
 * </pre>
 * <li>Variant 1
 *
 * <pre class="prettyprint">
 *  ---------------
 *  | Line 1 Text |   setPrimaryText("Line 1 Text");
 *  |             |
 *  |             |   setSecondaryTextVisibility(View.INVISIBLE);
 *  ---------------
 * </pre>
 * <li>Variant 2
 *
 * <pre class="prettyprint">
 *  ---------------
 *  |             |   setPrimaryTextVisibility(View.INVISIBLE);
 *  |             |
 *  | Line 2 Text |   setSecondaryText("Line 2 Text");
 *  ---------------
 * </pre>
 * <li>Variant 3
 *
 * <pre class="prettyprint">
 *  ---------------
 *  |             |   setPrimaryText("Line 1 Text");
 *  | Line 1 Text |
 *  |             |   setSecondaryText(View.GONE);
 *  ---------------
 * </pre>
 *
 * </ul>
 * </pre> For setting style of texts:
 *
 * <pre class="prettyprint">
 * setTextStyle(0, com.htc.lib1.cc.R.style.list_primary_read_m);
 * setTextStyle(1, com.htc.lib1.cc.R.style.list_secondary_read_m);
 * </pre>
 *
 * For Dark List
 *
 * <pre class="prettyprint">
 *  &lt;com.htc.widget.HtcListItem2LineStamp
 *      android:id="@+id/stamp"
 *      htc:textMode="darklist"
 *  /&gt;
 * </pre>
 */
public class HtcListItem2LineText extends HtcListItem2TextComponent implements
        IHtcListItemTextComponent, IHtcListItemAutoMotiveControl {
    @ExportedProperty(category = "CommonControl")
    private int mLeftMargin, mRightMargin;
    private ImageView mBlackIcon;

    private void init(Context context) {
        mBlackIcon = new ImageView(context);
        mBlackIcon.setScaleType(ScaleType.FIT_XY);
        mBlackIcon.setVisibility(View.GONE);

        mText[0] = new HtcFadingEdgeTextView(context);
        mText[1] = new HtcFadingEdgeTextView(context);

        enableMarquee(mIsMarqueeEnabled);

        setDefaultTextStyle();

        mLeftMargin = HtcListItemManager.getDesiredChildrenGap(context);
        mRightMargin = HtcListItemManager.getDesiredChildrenGap(context);

        super.setPadding(0, 0, 0, 0);

        addView(mBlackIcon, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mText[0], new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mText[1], new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * Simple constructor to use when creating this widget from code. It will
     * new 2 textViews with default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItem2LineText(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor that is called when inflating this widget from XML. This is
     * called when a view is being constructed from an XML file, supplying
     * attributes that were specified in the XML file.It will new 2 textViews
     * with default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     */
    public HtcListItem2LineText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        init(context, attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of this widget allows subclasses to use their own base style
     * when they are inflating. It will new 2 textViews with default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @param defStyle The default style to apply to this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItem2LineText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        init(context, attrs);
    }

    /**
     * Constructor that is called when inflating this widget from code. It will
     * new this widget with specified style, mode.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param mode to indicate item mode for HtcListItem.
     */
    public HtcListItem2LineText(Context context, int mode) {
        super(context, mode);
        init(context);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                com.htc.lib1.cc.R.styleable.HtcListItem2LineText);

        Drawable background = null;

        background = a.getDrawable(com.htc.lib1.cc.R.styleable.HtcListItem2LineText_indicator);
        a.recycle();

        setIndicatorDrawable(background);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) params).setMargins(mLeftMargin, 0, mRightMargin, 0);
        }

        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;

        super.setLayoutParams(params);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public ViewGroup.LayoutParams getLayoutParams() {
        if (super.getLayoutParams() != null)
            return super.getLayoutParams();
        else {
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(mLeftMargin, 0, mRightMargin, 0);
            super.setLayoutParams(params);
            return params;
        }
    }

    /**
     * Sets the padding. This widget cannot set padding to the left, right, top,
     * and bottom.
     *
     * @param left the left padding in pixels
     * @param top the top padding in pixels
     * @param right the right padding in pixels
     * @param bottom the bottom padding in pixels
     * @deprecated [Module internal use]
     */
    /** @hide */
    public void setPadding(int left, int top, int right, int bottom) {
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        // 2 texts have been measured, only need to measure secondary text if
        // black icon is not GONE.
        if (mBlackIcon.getVisibility() != View.GONE) {
            measureChild(mBlackIcon, widthSpec, heightSpec);

            mIsFrontImageExist = true;
            mBlackIconSize = mBlackIcon.getMeasuredWidth();
        } else {
            mIsFrontImageExist = false;
            mBlackIconSize = 0;
        }

        super.onMeasure(widthSpec, heightSpec);

        // dimensions unchanged.
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // both exist
        if (mText[0].getVisibility() != View.GONE && mText[1].getVisibility() != View.GONE) {
            layoutText(0, true, 0, l, t, r, b);
            if (mBlackIcon.getVisibility() != View.GONE) {
                layoutText(1, true, mBlackIconSize + mBlackIconRightMargin, l, t, r, b);
                layoutBlackIcon(false, l, t, r, b);
            } else {
                layoutText(1, true, 0, l, t, r, b);
            }
        }
        // only primary text exists
        else if (mText[0].getVisibility() != View.GONE) {
            layoutTextAtCenter(0, true, 0, l, t, r, b);
        }
        // only secondary text exists
        else if (mText[1].getVisibility() != View.GONE) {
            if (mBlackIcon.getVisibility() != View.GONE) {
                layoutTextAtCenter(1, true, mBlackIconSize + mBlackIconRightMargin, l, t, r, b);
                layoutBlackIcon(true, l, t, r, b);
            } else {
                layoutTextAtCenter(1, true, 0, l, t, r, b);
            }
        }
    }

    /**
     * @param isVerticalCenter
     * @return the X offset for secondary text
     */
    private int layoutBlackIcon(boolean isVerticalCenter,int l, int t, int r, int b) {
        if (mBlackIcon == null)
            return 0;

        if (mBlackIcon.getVisibility() == View.GONE)
            return 0;
        final boolean isLayoutRtl = (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) ? getLayoutDirection() == View.LAYOUT_DIRECTION_RTL : false;
        int top;
        top = (mText[1].getBottom() + mText[1].getTop() - mBlackIcon.getMeasuredHeight()) / 2;
        int left = 0;
        final int width = r - l;
        left = isLayoutRtl ? width - mBlackIcon.getMeasuredWidth() : 0;
        mBlackIcon.layout(left, top, left + mBlackIcon.getMeasuredWidth(),
                top + mBlackIcon.getMeasuredHeight());

        // this is the same as mBlackIconSize + mBlackIconRightMargin
        return mBlackIcon.getMeasuredWidth() + mBlackIconRightMargin;
    }

    /**
     * Use this API to set indicator drawable before the secondary text
     *
     * @param d Indicator drawable
     */
    public void setIndicatorDrawable(Drawable d) {
        if (d == null)
            mBlackIcon.setVisibility(View.GONE);
        else
            mBlackIcon.setVisibility(View.VISIBLE);
        mBlackIcon.setImageDrawable(d);
    }

    /**
     * Use this API to set indicator resource before the secondary text
     *
     * @param resId Indicator resource Id
     */
    public void setIndicatorResource(int resId) {
        if (resId == 0)
            mBlackIcon.setVisibility(View.GONE);
        else
            mBlackIcon.setVisibility(View.VISIBLE);
        mBlackIcon.setImageResource(resId);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setIndicatorBitmap(Bitmap bm) {
        if (bm == null)
            mBlackIcon.setVisibility(View.GONE);
        else
            mBlackIcon.setVisibility(View.VISIBLE);
        mBlackIcon.setImageBitmap(bm);
    }

    /**
     * @deprecated [Not use any longer] This API will no longer be supported in
     *             Sense 5.0
     */
    /** @hide */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        HtcListItemManager.setViewOpacity(mBlackIcon, enabled);
    }

    private void setDefaultTextStyle() {
        final Context context = getContext();
        if (mItemMode == HtcListItem.MODE_DEFAULT) {
            mFontSize[0] = context.getResources().getDimensionPixelSize(R.dimen.list_primary_m);
            mFontSize[1] = context.getResources().getDimensionPixelSize(R.dimen.list_secondary_m);

            if (mMode == MODE_WHITE_LIST) {
                setPrimaryTextStyle(com.htc.lib1.cc.R.style.list_primary_m);
                setSecondaryTextStyle(com.htc.lib1.cc.R.style.list_secondary_m);
            } else if (mMode == MODE_DARK_LIST) {
                setPrimaryTextStyle(com.htc.lib1.cc.R.style.darklist_primary_m);
                setSecondaryTextStyle(com.htc.lib1.cc.R.style.darklist_secondary_m);
            }
        } else if (mItemMode == HtcListItem.MODE_POPUPMENU) {
            mFontSize[0] = context.getResources().getDimensionPixelSize(R.dimen.list_primary_s);
            mFontSize[1] = context.getResources().getDimensionPixelSize(R.dimen.list_secondary_s);
            setPrimaryTextStyle(com.htc.lib1.cc.R.style.darklist_primary_s);
            setSecondaryTextStyle(com.htc.lib1.cc.R.style.darklist_secondary_s);
        } else if (mItemMode == HtcListItem.MODE_KEEP_MEDIUM_HEIGHT) {
            mFontSize[0] = context.getResources().getDimensionPixelSize(
                    R.dimen.fixed_list_primary_m);
            mFontSize[1] = context.getResources().getDimensionPixelSize(
                    R.dimen.fixed_list_secondary_m);

            if (mMode == MODE_WHITE_LIST) {
                setPrimaryTextStyle(com.htc.lib1.cc.R.style.fixed_list_primary_m);
                setSecondaryTextStyle(com.htc.lib1.cc.R.style.fixed_list_secondary_m);
            } else if (mMode == MODE_DARK_LIST) {
                setPrimaryTextStyle(com.htc.lib1.cc.R.style.fixed_darklist_primary_m);
                setSecondaryTextStyle(com.htc.lib1.cc.R.style.fixed_darklist_secondary_m);
            }
        } else {
            setPrimaryTextStyle(com.htc.lib1.cc.R.style.fixed_automotive_darklist_primary_m);
            setSecondaryTextStyle(com.htc.lib1.cc.R.style.fixed_automotive_darklist_secondary_m);
        }
    }

    /**
     * This method will change to use Automotive text style
     *
     * @param enable enable automotive mode
     */
    public void setAutoMotiveMode(boolean enable) {
        if (mIsAutomotiveMode == enable)
            return;

        mIsAutomotiveMode = enable;

        if (enable) {
            mItemMode = HtcListItem.MODE_AUTOMOTIVE;
            setDefaultTextStyle();
        } else {
            mItemMode = HtcListItem.MODE_DEFAULT;
            setDefaultTextStyle();
        }
        requestLayout();
    }

    /**
     * sometimes the secondary text is too long to show, and it needs to be
     * multi-line.
     *
     * @param isSingeline Is secondary text single line
     */
    public void setSecondaryTextSingleLine(boolean isSingeline) {
        if (isSingeline) {
            ((HtcFadingEdgeTextView) mText[1]).setEnableMarquee(mIsMarqueeEnabled);
        } else {
            mText[1].setEllipsize(TextUtils.TruncateAt.END);
            mText[1].setSingleLine(isSingeline);
        }
    }

    /**
     * @deprecated [Module internal use]
     */
    /** @hide */
    public void notifyItemMode(int itemMode) {
        if (mItemMode != itemMode) {
            mItemMode = itemMode;
            mIsAutomotiveMode = (mItemMode == HtcListItem.MODE_AUTOMOTIVE) ? true : false;
            setDefaultTextStyle();
        }
    }
}
