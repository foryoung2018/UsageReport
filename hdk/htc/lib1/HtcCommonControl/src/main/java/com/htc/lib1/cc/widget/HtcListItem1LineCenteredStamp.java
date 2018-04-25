package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;

/**
 * single stamp, always 1 line.<br/>
 * this widget will be vertically centered<br/>
 * text style: <b>list_secondary_xs</b> or <b>darklist_secondary_xs</b><br/>
 * see also: HtcListItem2LineStamp
 */
public class HtcListItem1LineCenteredStamp extends FrameLayout implements
        IHtcListItemStampComponent, IHtcListItemAutoMotiveControl, IHtcListItemComponent {
    @ExportedProperty(category = "CommonControl")
    private int mRightMargin = 0;
    private TextView mTextView = null;
    @ExportedProperty(category = "CommonControl")
    private boolean mIsMarqueeEnabled = false;
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = HtcListItem2TextComponent.MODE_WHITE_LIST, to = "MODE_WHITE_LIST"),
            @IntToString(from = HtcListItem2TextComponent.MODE_DARK_LIST, to = "MODE_DARK_LIST")
    })
    protected int mMode = HtcListItem2TextComponent.MODE_WHITE_LIST;
    @ExportedProperty(category = "CommonControl")
    private boolean mIsAutomotiveMode = false;

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    com.htc.lib1.cc.R.styleable.HtcListItemTextComponentMode);
            mIsMarqueeEnabled = a.getBoolean(R.styleable.HtcListItemTextComponentMode_isMarquee,
                    false);
            mMode = a.getInt(R.styleable.HtcListItemTextComponentMode_textMode,
                    HtcListItem2TextComponent.MODE_WHITE_LIST);
            a.recycle();
        } else {
            mIsMarqueeEnabled = false;
            mMode = HtcListItem2TextComponent.MODE_WHITE_LIST;
        }

        init(context);
    }

    private void init(Context context) {
        mTextView = new HtcFadingEdgeTextView(context);
        mTextView.setText("");
        enableMarquee(mIsMarqueeEnabled);

        mRightMargin = HtcListItemManager.getDesiredChildrenGap(context);
        super.setPadding(0, 0, 0, 0);

        setDefaultTextStyle();

        addView(mTextView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * Simple constructor to use when creating a HtcListItem1LineCenteredStamp
     * from code. It will new textView and set default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItem1LineCenteredStamp(Context context) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        init(context);
    }

    /**
     * Constructor that is called when inflating a HtcListItem1LineCenteredStamp
     * from XML. This is called when a view is being constructed from an XML
     * file, supplying attributes that were specified in the XML file.It will
     * new extView and set default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItem1LineCenteredStamp(Context context, AttributeSet attrs) {
        super(context, attrs);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        init(context, attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of HtcListItem1LineCenteredStamp allows subclasses to use
     * their own base style when they are inflating. It will new textView and
     * set default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @param defStyle The default style to apply to this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItem1LineCenteredStamp(Context context, AttributeSet attrs, int defStyle) {
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
        final int top = (b - t - mTextView.getMeasuredHeight()) / 2;
        final int right = r - l;
        mTextView.layout(right - mTextView.getMeasuredWidth(), top, right,
                top + mTextView.getMeasuredHeight());
    }

    /**
     * set the style of the text. Only for HTC defined style.
     *
     * @param defStyle the resource ID of the style (The font size will be
     *            changed.)
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setTextStyle(int defStyle) {
        ((HtcFadingEdgeTextView) mTextView).setTextStyle(defStyle);
    }

    /**
     * Sets the string value of the TextView.
     *
     * @param text string value of the TextView
     */
    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setText(int rId) {
        mTextView.setText(rId);
    }

    /** if false, views will be disabled and alpha value will be set to 0.4 */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
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

    /**
     * please note: if true, marquee will NOT be used, instead, using fade out.
     * otherwise, use Truncate.END. to enable marquee, please use
     * enableMarquee(int, boolean)
     *
     * @param enable is marquee enabled
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void enableMarquee(boolean enable) {
        mIsMarqueeEnabled = enable;
        ((HtcFadingEdgeTextView) mTextView).setEnableMarquee(mIsMarqueeEnabled);
    }

    private void setDefaultTextStyle() {
        if (mMode == HtcListItem.MODE_DEFAULT || mMode == HtcListItem.MODE_KEEP_MEDIUM_HEIGHT) {
            if (mMode == HtcListItem2TextComponent.MODE_WHITE_LIST) {
                setTextStyle(com.htc.lib1.cc.R.style.fixed_list_secondary);
            } else {
                setTextStyle(com.htc.lib1.cc.R.style.fixed_list_secondary);
            }
        } else {
            setTextStyle(com.htc.lib1.cc.R.style.automotive_darklist_secondary_m);
        }
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
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

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = HtcListItem.MODE_DEFAULT, to = "MODE_DEFAULT"),
            @IntToString(from = HtcListItem.MODE_CUSTOMIZED, to = "MODE_CUSTOMIZED"),
            @IntToString(from = HtcListItem.MODE_KEEP_MEDIUM_HEIGHT, to = "MODE_KEEP_MEDIUM_HEIGHT"),
            @IntToString(from = HtcListItem.MODE_AUTOMOTIVE, to = "MODE_AUTOMOTIVE"),
            @IntToString(from = HtcListItem.MODE_POPUPMENU, to = "MODE_POPUPMENU")
    })
    int mItemMode = HtcListItem.MODE_DEFAULT;

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
