package com.htc.lib1.cc.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.LogUtil;

/**
 * This component is used with HtcListItem. There are 2 lines of texts in the
 * component by default. For the variants:
 * <ul>
 * <li>Default
 *
 * <pre class="prettyprint">
 *      ---------------
 *      | Line 1 Text |   setPrimaryText("Line 1 Text");
 *      |             |
 *      | Line 2 Text |   setSecondaryText("Line 2 Text");
 *      ---------------
 * </pre>
 *
 * For setting style of texts:
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

public class HtcListItem2LineTextProgressBar extends HtcListItem2TextComponent implements
        IHtcListItemTextComponent {

    private final static String TAG = "HtcListItem2LineTextProgressBar";

    @ExportedProperty(category = "CommonControl")
    private int mLeftMargin, mRightMargin;
    private TextView mStamp;
    private HtcProgressBar mProgress;
    private HtcListItemManager mHtcListItemManager;

    private void init(Context context) {
        mHtcListItemManager = HtcListItemManager.getInstance(context);
        mText[0] = new HtcFadingEdgeTextView(context);
        mText[1] = new HtcFadingEdgeTextView(context);

        enableMarquee(mIsMarqueeEnabled);

        setDefaultTextStyle();

        mLeftMargin = HtcListItemManager.getDesiredChildrenGap(context);
        mRightMargin = HtcListItemManager.getDesiredChildrenGap(context);

        super.setPadding(0, 0, 0, 0);

        addView(mText[0], new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mText[1], new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * Simple constructor to use when creating this widget from code. It will
     * new 2 textViews with default text style and a progress bar.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItem2LineTextProgressBar(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor that is called when inflating this widget from XML. This is
     * called when a view is being constructed from an XML file, supplying
     * attributes that were specified in the XML file.It will new 2 textViews
     * and set default text style and a progress bar.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItem2LineTextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of this widget allows subclasses to use their own base style
     * when they are inflating. It will new 2 textViews with default text style
     * and a progress bar.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @param defStyle The default style to apply to this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItem2LineTextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * Constructor that is called when inflating this widget from code. It will
     * new this widget with specified style, mode.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param mode to indicate item mode for HtcListItem.
     */
    public HtcListItem2LineTextProgressBar(Context context, int mode) {
        super(context, mode);
        init(context);
    }

    /**
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
     * For layout from XML file, this function will be called to get the layout
     * param of children.
     *
     * @see android.view.View#getLayoutParams()
     * @return ViewGroup.LayoutParams are used by views to tell their parents
     *         how they want to be laid out.
     * @deprecated [Module internal use]
     */
    /** @hide */
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

    private void initSecondaryStamp() {
        mStamp = new HtcFadingEdgeTextView(getContext());
        ((HtcFadingEdgeTextView) mStamp).setTextStyle(R.style.fixed_list_secondary);
        ((HtcFadingEdgeTextView) mStamp).setEnableMarquee(mIsMarqueeEnabled);
        addView(mStamp, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * set the text of the stamp
     *
     * @param text the text displayed
     */
    public void setSecondaryStampText(String text) {
        if (mStamp == null) {
            initSecondaryStamp();
        }
        setText(mStamp, text);
    }

    /**
     * set the text of the stamp
     *
     * @param rId the resource ID of the text displayed
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setSecondaryStampText(int rId) {
        if (mStamp == null) {
            initSecondaryStamp();
        }
        String text = getContext().getResources().getString(rId);
        setText(mStamp, text);
    }

    /**
     * set the text of the stamp
     *
     * @param text the text displayed
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setSecondaryStampText(CharSequence text) {
        if (mStamp == null) {
            initSecondaryStamp();
        }
        setText(mStamp, text);
    }

    /**
     * set the visibility of Stamp text
     *
     * @param visibility the visibility of the stamp
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setSecondaryStampTextVisibility(int visibility) {
        if (mStamp != null)
            mStamp.setVisibility(visibility);
    }

    /**
     * get the content of stamp
     *
     * @return the content of stamp
     * @deprecated [Not use any longer]
     */
    /** @hide */
    @ExportedProperty(category = "CommonControl")
    public String getSecondaryStampText() {
        if (mStamp != null) {
            return mStamp.getText().toString();
        } else {
            return "";
        }
    }

    /**
     * get the content of stamp
     *
     * @return the content of stamp
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public CharSequence getSecondaryStampCharSequence() {
        if (mStamp != null) {
            return mStamp.getText();
        } else {
            return "";
        }
    }

    /**
     * @hide
     */
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        final Context context = getContext();
        int height = 0;
        int gap = HtcListItemManager.getM2(context);
        int width = MeasureSpec.getSize(widthSpec);

        if (mText[1].getVisibility() != GONE) {
            if (mStamp != null && mStamp.getVisibility() != GONE) {
                int desiredStampWidth = width / 3 - (gap / 2);
                if (desiredStampWidth < 0) {
                    LogUtil.logE(TAG,
                            "width / 3 - (gap / 2) < 0 :",
                            " width = ", width,
                            ", gap = ", gap);
                    desiredStampWidth = 0;
                }

                int desiredTextWidth = 2 * width / 3 - (gap / 2);
                if (desiredTextWidth < 0) {
                    LogUtil.logE(TAG,
                            "2 * width / 3 - (gap / 2) < 0 :",
                            " width = ", width,
                            ", gap = ", gap);
                    desiredTextWidth = 0;
                }

                measureChild(mStamp, widthSpec, heightSpec);
                int diff = mStamp.getMeasuredWidth() - desiredStampWidth;
                if (diff >= 0) {
                    measureChild(mStamp, MeasureSpec.makeMeasureSpec(desiredStampWidth,
                            MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                            mStamp.getMeasuredHeight(), MeasureSpec.EXACTLY));
                    measureChild(mText[1],
                            MeasureSpec.makeMeasureSpec(desiredTextWidth, MeasureSpec.EXACTLY),
                            heightSpec);
                } else {
                    measureChild(mText[1], MeasureSpec.makeMeasureSpec(desiredTextWidth - diff
                            - gap / 2, MeasureSpec.EXACTLY), heightSpec);
                }
            } else {
            }
        }

        if (mProgress != null && mProgress.getVisibility() != View.GONE) {
            measureChild(mProgress, widthSpec, heightSpec);
            height = mHtcListItemManager.getDesiredListItemHeight(mItemMode)
                    - mHtcListItemManager.getDesiredBottomGap(mItemMode) + HtcListItemManager.getM3(context)
                    + mProgress.getMeasuredHeight() + HtcListItemManager.getM1(context);
            setMeasuredDimension(getMeasuredWidth(), height);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final boolean isLayoutRtl = (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) ? getLayoutDirection() == View.LAYOUT_DIRECTION_RTL : false;
        final int width = r - l;
        int left;
        if (mProgress != null && mProgress.getVisibility() != View.GONE) {
            int top = mHtcListItemManager.getPrimaryBaseLine(mItemMode) - mText[0].getBaseline();
            int bottom = top + mText[0].getMeasuredHeight();
            if (mText[0].getVisibility() != View.GONE)
                mText[0].layout(0, top, width, bottom);

            top = mHtcListItemManager.getSecondaryBaseLine(mItemMode) - mText[1].getBaseline();
            bottom = top + mText[1].getMeasuredHeight();
            if (mText[1].getVisibility() != View.GONE) {
                left = isLayoutRtl ? width - mText[1].getMeasuredWidth() : 0;
                mText[1].layout(left, top, left + mText[1].getMeasuredWidth(), bottom);
            }
            if (mStamp != null && mStamp.getVisibility() != View.GONE) {
                int topStamp = mHtcListItemManager.getSecondaryBaseLine(mItemMode)
                        - mText[1].getBaseline();
                int bottomStamp = topStamp + mStamp.getMeasuredHeight();
                left = isLayoutRtl ? 0 : width - mStamp.getMeasuredWidth();
                mStamp.layout(left, topStamp, left + mStamp.getMeasuredWidth(), bottomStamp);
            }
            top = bottom + HtcListItemManager.getM3(getContext());
            bottom = top + mProgress.getMeasuredHeight();
            mProgress.layout(0, top, width, bottom);
        } else {
            if (mText[0].getVisibility() != View.GONE && mText[1].getVisibility() != View.GONE) {
                int top = mHtcListItemManager.getPrimaryBaseLine(mItemMode) - mText[0].getBaseline();
                int bottom = top + mText[0].getMeasuredHeight();
                mText[0].layout(0, top, width, bottom);

                top = mHtcListItemManager.getSecondaryBaseLine(mItemMode) - mText[1].getBaseline();
                bottom = top + mText[1].getMeasuredHeight();
                left = isLayoutRtl ? width - mText[1].getMeasuredWidth() : 0;
                mText[1].layout(left, top, left + mText[1].getMeasuredWidth(), bottom);

                if (mStamp != null && mStamp.getVisibility() != View.GONE) {

                    int topStamp = mHtcListItemManager.getSecondaryBaseLine(mItemMode)
                            - mText[1].getBaseline();
                    int bottomStamp = topStamp + mStamp.getMeasuredHeight();
                    left = isLayoutRtl ? 0 : width - mStamp.getMeasuredWidth();
                    mStamp.layout(left, topStamp, left + mStamp.getMeasuredWidth(), bottomStamp);
                }
            } else if (mText[0].getVisibility() != View.GONE) {
                layoutTextAtCenter(0, true, 0, l, t, r, b);
            } else if (mText[1].getVisibility() != View.GONE) {
                layoutTextAtCenter(1, true, 0, l, t, r, b);
            }
        }
    }

    /**
     * Use this API to get progress bar of this widget
     *
     * @return progress bar
     */
    public ProgressBar getProgressBar() {
        if (mProgress == null) {
            mProgress = new HtcProgressBar(getContext());
            addView(mProgress, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        return mProgress;
    }

    private void setDefaultTextStyle() {
        if (mItemMode == HtcListItem.MODE_DEFAULT
                || mItemMode == HtcListItem.MODE_KEEP_MEDIUM_HEIGHT) {
            final Context context = getContext();
            mFontSize[0] = context.getResources().getDimensionPixelSize(R.dimen.list_primary_m);
            mFontSize[1] = context.getResources().getDimensionPixelSize(R.dimen.list_secondary_m);

            if (mMode == MODE_WHITE_LIST) {
                setPrimaryTextStyle(com.htc.lib1.cc.R.style.list_primary_m);
                setSecondaryTextStyle(com.htc.lib1.cc.R.style.list_secondary_m);
            }
        }
    }
}
