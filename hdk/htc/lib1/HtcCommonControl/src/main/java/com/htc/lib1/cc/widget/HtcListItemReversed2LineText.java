package com.htc.lib1.cc.widget;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htc.lib1.cc.util.CheckUtil;

/**
 * This component is used with HtcListItem. There are 2 lines of texts in the
 * component by default. For the variants:
 * <ul>
 * <li>Default
 *
 * <pre class="prettyprint">
 *     ---------------
 *     | Line 1 Text |   setSecondaryText("Line 1 Text");
 *  |             |
 *     | Line 2 Text |   setPrimaryText("Line 2 Text");
 *     ---------------
 * </pre>
 */

public class HtcListItemReversed2LineText extends ViewGroup implements IHtcListItemTextComponent,
        IHtcListItemComponent {

    private static final String TAG = "HtcListItemReversed2LineText";
    private TextView[] mText;
    final static int MAX_NUM_TEXT = 2;

    private void init(Context context) {
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        mText = new TextView[MAX_NUM_TEXT];

        mText[0] = new TextView(context);
        mText[0].setSingleLine(true);
        mText[0].setEllipsize(TextUtils.TruncateAt.END);
        mText[0].setHorizontalFadingEdgeEnabled(false);

        mText[1] = new TextView(context);
        mText[1].setSingleLine(true);
        mText[1].setEllipsize(TextUtils.TruncateAt.END);
        mText[1].setHorizontalFadingEdgeEnabled(false);

        setDefaultTextStyle();

        super.setPadding(0, 0, 0, 0);

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
    public HtcListItemReversed2LineText(Context context) {
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
    public HtcListItemReversed2LineText(Context context, AttributeSet attrs) {
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
    public HtcListItemReversed2LineText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * @hide
     */
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) params).setMargins(0, 0, 0, 0);
        }

        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;

        super.setLayoutParams(params);
    }

    /**
     * @hide
     */
    public ViewGroup.LayoutParams getLayoutParams() {
        if (super.getLayoutParams() != null)
            return super.getLayoutParams();
        else {
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 0, 0, 0);
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
    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        String exception = "In order to follow UIGL, this widget cannot set padding to the left, right, top, and bottom.";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            throw new IllegalStateException(exception);
        } else {
            Log.e(TAG, exception);
        }
    }

    /**
     * @hide
     */
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);

        if (mText[0] != null) {
            measureChild(mText[0], widthSpec, heightSpec);
        }
        if (mText[1] != null) {
            measureChild(mText[1], widthSpec, heightSpec);
        }

        setMeasuredDimension(getMeasuredWidth(), getTextMeasuredHeight(mText[0])
                + getTextMeasuredHeight(mText[1]));
    }

    private int getTextMeasuredHeight(TextView view) {
        return (view != null) ? view.getMeasuredHeight() : 0;
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int secondaryTextHeight = (mText[0] != null) ? mText[0].getMeasuredHeight() : 0;
        int primaryTextHeight = (mText[1] != null) ? mText[1].getMeasuredHeight() : 0;
        final int width = r - l;

        if (mText[0] != null)
            mText[0].layout(0, 0, width, secondaryTextHeight);

        if (mText[1] != null)
            mText[1].layout(0, secondaryTextHeight, width, secondaryTextHeight
                    + primaryTextHeight);
    }

    /**
     * @deprecated [Not use any longer] This API will no longer be supported in
     *             Sense 5.0
     */
    /** @hide */
    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled)
            return;
        super.setEnabled(enabled);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != null) {
                child.setEnabled(enabled);
            }
        }
    }

    private void setDefaultTextStyle() {
        setSecondaryTextStyle(com.htc.lib1.cc.R.style.list_secondary_m);
        setPrimaryTextStyle(com.htc.lib1.cc.R.style.list_primary_m);

    }

    /**
     * set the style of primary text. Only for HTC defined style.
     *
     * @param style The font size won't be changed unless you call
     *            setUseFontSizeInStyle(true)
     */
    private void setSecondaryTextStyle(int style) {
        setTextStyle(0, style);
    }

    /**
     * set the style of secondary text. Only for HTC defined style.
     *
     * @param style The font size won't be changed unless you call
     *            setUseFontSizeInStyle(true)
     */
    private void setPrimaryTextStyle(int style) {
        setTextStyle(1, style);
    }

    /**
     * set the style of the text
     *
     * @param index which text you would like to change the style [0-1]
     * @param defStyle the resource ID of the style (The text size won't be
     *            changed unless you call setUseFontSizeInStyle(true))
     */
    private void setTextStyle(int index, int defStyle) {
        if (mText[index] != null) {
            mText[index].setTextAppearance(getContext(), defStyle);
        }
    }

    /**
     * set the text of the first line of text
     *
     * @param text the text displayed
     */
    public void setSecondaryText(String text) {
        setText(mText[0], text);
    }

    /**
     * set the text of the first line of text
     *
     * @param rId the resourcr ID of the text displayed
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setSecondaryText(int rId) {
        String text = getContext().getResources().getString(rId);
        setText(mText[0], text);
    }

    /**
     * set the text of the first line of text
     *
     * @param text
     */
    public void setSecondaryText(CharSequence text) {
        setText(mText[0], text);
    }

    /**
     * set the text of the second line of text
     *
     * @param text the text displayed
     */
    public void setPrimaryText(String text) {
        setText(mText[1], text);
    }

    /**
     * set the text of the second line of text
     *
     * @param text
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setPrimaryText(CharSequence text) {
        setText(mText[1], text);
    }

    /**
     * set the text of the second line of text
     *
     * @param rId the resource ID of the text displayed
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setPrimaryText(int rId) {
        String text = getContext().getResources().getString(rId);
        setText(mText[1], text);
    }

    private void setText(TextView view, String text) {
        if (null == view) {
            return;
        }

        view.setText(text);

        if (text == null) {
            view.setVisibility(View.GONE);
        } else if (text.equals("")) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void setText(TextView view, CharSequence text) {
        if (null == view) {
            return;
        }

        view.setText(text);

        if (text == null) {
            view.setVisibility(View.GONE);
        } else if (text.equals("")) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * get primary textView, please do not change it to null or change its font
     * size . Please keep its common property, this is very important.
     *
     * @return primary textView
     */
    public TextView getPrimaryTextView() {
        if (mText != null && mText[1] != null)
            return mText[1];
        return null;
    }

    /**
     * @deprecated [Module internal use]
     */
    /** @hide */
    public void notifyItemMode(int itemMode) {

    }
}
