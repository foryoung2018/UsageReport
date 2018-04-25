package com.htc.lib1.cc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;

import com.htc.lib1.cc.R;

/**
 * This component is used with HtcListItem. In HTC UI guideline, the stamps are
 * texts at the end of list item. There are 2 lines of text in the component by
 * default. For the variants:
 * <ul>
 * <li>Default
 *
 * <pre class="prettyprint">
 *  ------------
 *  |    Today |   setPrimaryText("Today");
 *  |          |
 *  | 12:34 PM |   setSecondaryText("12:34 PM");
 *  ------------
 * </pre>
 * <li>Variant 1
 *
 * <pre class="prettyprint">
 *  ------------
 *  |          |   setPrimaryTextVisibility(View.INVISIBLE);
 *  |          |
 *  | 12:34 PM |   setSecondaryText("12:34 PM");
 *  ------------
 * </pre>
 * <li>Variant 2
 *
 * <pre class="prettyprint">
 *  ------------
 *  |    Today |   setPrimaryText("Today");
 *  |          |
 *  |          |   setSecondaryText(View.INVISIBLE);
 *  ------------
 * </pre>
 * <li>Variant 3
 *
 * <pre class="prettyprint">
 *  ------------ <br>
 *  |          |   setPrimaryText("Exchange"); <br>
 *  | Exchange | <br>
 *  |          |   setSecondaryTextVisibility(View.GONE); <br>
 *  ------------ <br>
 * </pre>
 *
 * </ul>
 * For setting style of texts:
 *
 * <pre class="prettyprint">
 * setPrimaryTextStyle(com.htc.lib1.cc.R.style.list_secondary_read_m);
 * setSecondaryTextStyle(com.htc.lib1.cc.R.style.list_secondary_read_m);
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
public class HtcListItem2LineStamp extends HtcListItem2TextComponent implements
        IHtcListItemStampComponent {

    @ExportedProperty(category = "CommonControl")
    private int mRightMargin;

    private void init(Context context) {
        mText[0] = new HtcFadingEdgeTextView(context);
        mText[1] = new HtcFadingEdgeTextView(context);

        enableMarquee(mIsMarqueeEnabled);

        setDefaultTextStyle();

        mRightMargin = HtcListItemManager.getDesiredChildrenGap(context);

        super.setPadding(0, 0, 0, 0);

        addView(mText[0], new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mText[1], new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * Simple constructor to use when creating this widget from code. It will
     * new 2 textViews with default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItem2LineStamp(Context context) {
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
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItem2LineStamp(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
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
    public HtcListItem2LineStamp(Context context, AttributeSet attrs, int defStyle) {
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
    public HtcListItem2LineStamp(Context context, int mode) {
        super(context, mode);
        init(context);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) params).setMargins(0, 0, mRightMargin, 0);
        }

        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
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
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 0, mRightMargin, 0);
            super.setLayoutParams(params);
            return params;
        }
    }

    /**
     * @deprecated [Module internal use]
     */
    /** @hide */
    public void setPadding(int left, int top, int right, int bottom) {
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // both exist
        if (mText[0].getVisibility() != View.GONE && mText[1].getVisibility() != View.GONE) {
            layoutText(0, false, 0, l, t, r, b);
            layoutText(1, false, 0, l, t, r, b);
        }
        // only primary text exists
        else if (mText[0].getVisibility() != View.GONE) {
            layoutTextAtCenter(0, false, 0, l, t, r, b);
        }
        // only secondary text exists
        else if (mText[1].getVisibility() != View.GONE) {
            layoutTextAtCenter(1, false, 0, l, t, r, b);
        }
    }

    private void setDefaultTextStyle() {
        if (mItemMode == HtcListItem.MODE_DEFAULT
                || mItemMode == HtcListItem.MODE_KEEP_MEDIUM_HEIGHT) {
            mFontSize[0] = getContext().getResources().getDimensionPixelSize(R.dimen.list_secondary);
            mFontSize[1] = getContext().getResources().getDimensionPixelSize(R.dimen.list_secondary);

            setPrimaryTextStyle(com.htc.lib1.cc.R.style.fixed_list_secondary);
            setSecondaryTextStyle(com.htc.lib1.cc.R.style.fixed_list_secondary);
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
