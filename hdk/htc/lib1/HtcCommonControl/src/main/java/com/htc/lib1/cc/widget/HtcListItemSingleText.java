package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;

/**
 * single text, max 2 lines.<br/>
 * this widget will be vertically centered<br/>
 * text style: <b>list_primary_m_bold</b> or <b>darklist_primary_m_bold</b><br/>
 * see also: HtcListItem1LineCenteredText
 */
public class HtcListItemSingleText extends FrameLayout implements IHtcListItemTextComponent,
        IHtcListItemAutoMotiveControl, IHtcListItemComponent {
    @ExportedProperty(category = "CommonControl")
    private int mLeftMargin = 0;
    @ExportedProperty(category = "CommonControl")
    private int mRightMargin = 0;

    private TextView mTextView = null;

    @ExportedProperty(category = "CommonControl", resolveId = true)
    private int mCustomStyle = 0;
    @ExportedProperty(category = "CommonControl")
    private boolean mUseFontSizeInStyle = false;

    @ExportedProperty(category = "CommonControl")
    private int mFontSize = 0;
    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = HtcListItem2TextComponent.MODE_WHITE_LIST, to = "MODE_WHITE_LIST"),
            @IntToString(from = HtcListItem2TextComponent.MODE_DARK_LIST, to = "MODE_DARK_LIST")
    })
    private int mMode = HtcListItem2TextComponent.MODE_WHITE_LIST;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsAutomotiveMode = false;

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    com.htc.lib1.cc.R.styleable.HtcListItemTextComponentMode);
            mMode = a.getInt(R.styleable.HtcListItemTextComponentMode_textMode,
                    HtcListItem2TextComponent.MODE_WHITE_LIST);
            a.recycle();
        } else {
            mMode = HtcListItem2TextComponent.MODE_WHITE_LIST;
        }

        init(context);
    }

    private void init(Context context) {
        mTextView = new HtcFadingEdgeTextView(context);
        // setLines(2) makes no sense, change to setMaxLines(2)
        mTextView.setMaxLines(2);
        mTextView.setEllipsize(TruncateAt.END);

        mLeftMargin = HtcListItemManager.getDesiredChildrenGap(context);
        mRightMargin = HtcListItemManager.getDesiredChildrenGap(context);
        mFontSize = getContext().getResources().getDimensionPixelSize(R.dimen.list_primary_m);

        setDefaultTextStyle();

        addView(mTextView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * Simple constructor to use when creating this widget from code. It will
     * new textView and set default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItemSingleText(Context context) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        init(context);
    }

    /**
     * Constructor that is called when inflating this widget from XML. This is
     * called when a view is being constructed from an XML file, supplying
     * attributes that were specified in the XML file.It will new extView and
     * set default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemSingleText(Context context, AttributeSet attrs) {
        super(context, attrs);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        init(context, attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of this widget allows subclasses to use their own base style
     * when they are inflating. It will new textView and set default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @param defStyle The default style to apply to this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemSingleText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        init(context, attrs);
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
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setPadding(int left, int top, int right, int bottom) {
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int top = (b - t - mTextView.getMeasuredHeight()) / 2;
        mTextView.layout(0, top, mTextView.getMeasuredWidth(), top + mTextView.getMeasuredHeight());
    }

    /**
     * set the style of the text. Only for HTC defined style.
     *
     * @param defStyle the resource ID of the style (The font size won't be
     *            changed.)
     */
    public void setTextStyle(int defStyle) {
        mCustomStyle = defStyle;
        ((HtcFadingEdgeTextView) mTextView).setTextStyle(defStyle);
        if (!mIsAutomotiveMode && !mUseFontSizeInStyle)
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontSize);
    }

    /**
     * Sets the string value of the TextView. TextView <em>does not</em> accept
     * HTML-like formatting, which you can do with text strings in XML resource
     * files. To style your strings, attach android.text.style.* objects to a
     * {@link android.text.SpannableString}, or see the <a
     * href="{@docRoot}
     * guide/topics/resources/available-resources.html#stringresources">
     * Available Resource Types</a> documentation for an example of setting
     * formatted text in the XML resource file.
     *
     * @param text string value of the TextView
     */
    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    /**
     * Sets the resource Id of string to the TextView.
     *
     * @param rId resource Id of string to display
     */
    public void setText(int rId) {
        mTextView.setText(rId);
    }

    /**
     * if false, views will be disabled and alpha value will be set to 0.4
     *
     * @deprecated [Module internal use]
     */
    /** @hide */
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled)
            return;
        super.setEnabled(enabled);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != null)
                child.setEnabled(enabled);
        }
    }

    private void setDefaultTextStyle() {
        if (mItemMode == HtcListItem.MODE_DEFAULT
                || mItemMode == HtcListItem.MODE_KEEP_MEDIUM_HEIGHT) {
            if (mMode == HtcListItem2TextComponent.MODE_WHITE_LIST) {
                setTextStyle(com.htc.lib1.cc.R.style.list_primary_m);
            } else {
                setTextStyle(com.htc.lib1.cc.R.style.darklist_primary_m);
            }
        } else {
            setTextStyle(com.htc.lib1.cc.R.style.fixed_automotive_darklist_primary_m);
        }
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    public void setAutoMotiveMode(boolean enable) {
        if (mIsAutomotiveMode == enable)
            return;

        mIsAutomotiveMode = enable;
        if (enable) {
            mItemMode = HtcListItem.MODE_AUTOMOTIVE;
            setTextStyle(com.htc.lib1.cc.R.style.fixed_automotive_darklist_primary_m);
        } else {
            mItemMode = HtcListItem.MODE_DEFAULT;
            setDefaultTextStyle();
        }
    }

    /**
     * in this class, mFontSize is used, but some APPs don't want to be tied.
     *
     * @param b if true, use the font size in the style, otherwise use
     *            mFontSize.
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setUseFontSizeInStyle(boolean b) {
        mUseFontSizeInStyle = b;
        setTextStyle(mCustomStyle);
    }

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = HtcListItem.MODE_DEFAULT, to = "MODE_DEFAULT"),
            @IntToString(from = HtcListItem.MODE_CUSTOMIZED, to = "MODE_CUSTOMIZED"),
            @IntToString(from = HtcListItem.MODE_KEEP_MEDIUM_HEIGHT, to = "MODE_KEEP_MEDIUM_HEIGHT"),
            @IntToString(from = HtcListItem.MODE_AUTOMOTIVE, to = "MODE_AUTOMOTIVE"),
            @IntToString(from = HtcListItem.MODE_POPUPMENU, to = "MODE_POPUPMENU")
    })
    int mItemMode = HtcListItem.MODE_DEFAULT;

    /**
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void notifyItemMode(int itemMode) {
        mItemMode = itemMode;
        mIsAutomotiveMode = (mItemMode == HtcListItem.MODE_AUTOMOTIVE) ? true : false;
        setDefaultTextStyle();
    }
}
