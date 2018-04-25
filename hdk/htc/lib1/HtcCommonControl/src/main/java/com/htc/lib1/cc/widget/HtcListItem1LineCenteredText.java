package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.LogUtil;

/**
 * single text, always 1 line.<br/>
 * this widget will be vertically centered<br/>
 * text style: <b>list_primary_m</b> or <b>darklist_primary_m</b><br/>
 * see also: HtcListItemSingleText
 */
public class HtcListItem1LineCenteredText extends FrameLayout implements IHtcListItemTextComponent,
        IHtcListItemAutoMotiveControl, IHtcListItemComponent {

    private final static String TAG = "HtcListItem1LineCenteredText";

    @ExportedProperty(category = "CommonControl")
    private int mRightMargin = 0;
    @ExportedProperty(category = "CommonControl")
    private int mLeftMargin = 0;

    private TextView mTextView = null;
    @ExportedProperty(category = "CommonControl")
    private boolean mIsMarqueeEnabled = false;
    /**
     * Text mode of HtcListItem text component, The text mode include 1.
     * whitelist 2. darklist 3. automotive_darklist
     */
    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = HtcListItem2TextComponent.MODE_WHITE_LIST, to = "MODE_WHITE_LIST"),
            @IntToString(from = HtcListItem2TextComponent.MODE_DARK_LIST, to = "MODE_DARK_LIST")
    })
    protected int mMode = HtcListItem2TextComponent.MODE_WHITE_LIST;
    @ExportedProperty(category = "CommonControl")
    private boolean mIsAutomotiveMode = false;
    private View mImage = null;
    @ExportedProperty(category = "CommonControl")
    private boolean mIsNoContentText = false;
    @ExportedProperty(category = "CommonControl")
    private boolean mAllCaps = false;

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
        setText("");
        enableMarquee(mIsMarqueeEnabled);
        mIsNoContentText = false;
        mAllCaps = false;

        mRightMargin = HtcListItemManager.getDesiredChildrenGap(context);
        mLeftMargin = HtcListItemManager.getDesiredChildrenGap(context);

        super.setPadding(0, 0, 0, 0);

        setDefaultTextStyle();

        addView(mTextView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * Simple constructor to use when creating a HtcListItem1LineCenteredText
     * from code. It will new textView and set default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItem1LineCenteredText(Context context) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        init(context);
    }

    /**
     * Constructor that is called when inflating a HtcListItem1LineCenteredText
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
    public HtcListItem1LineCenteredText(Context context, AttributeSet attrs) {
        super(context, attrs);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        init(context, attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of HtcListItem1LineCenteredText allows subclasses to use
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
    public HtcListItem1LineCenteredText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        init(context, attrs);
    }

    /**
     * Set the layout parameters associated with this view. This widget will set
     * fixed top, bottom, left and right margin and the width and height is set
     * to match parent.
     *
     * @param params The layout parameters for this widget, cannot be null
     * @deprecated [Module internal use]
     */
    /** @hide */
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
     * @return ViewGroup.LayoutParams The layout parameters for this widget
     * @see android.view.View#getLayoutParams()
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
     * {@inheritDoc}
     *
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (mImage != null && mImage.getVisibility() != View.GONE) {
            int textWidth = getMeasuredWidth() - mImage.getMeasuredWidth()
                    - HtcListItemManager.getM3(getContext());
            if (textWidth < 0) {
                LogUtil.logE(TAG,
                        "getMeasuredWidth() - mImage.getMeasuredWidth() - HtcListItemManager.getM3() < 0 :",
                        " getMeasuredWidth() = ", getMeasuredWidth(),
                        ", mImage.getMeasuredWidth() = ", mImage.getMeasuredWidth(),
                        ", HtcListItemManager.getM3() = ", HtcListItemManager.getM3(getContext()));
                textWidth = 0;
            }
            int widthSpecForText = MeasureSpec.makeMeasureSpec(textWidth, MeasureSpec.AT_MOST);
            mTextView.measure(widthSpecForText, mTextView.getMeasuredHeight());
        } else {
            int textWidth = getMeasuredWidth();
            int widthSpecForText = MeasureSpec.makeMeasureSpec(textWidth, MeasureSpec.EXACTLY);
            mTextView.measure(widthSpecForText, mTextView.getMeasuredHeight());
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
     * {@inheritDoc}
     *
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int height = b - t;
        final int width = r - l;
        if (mImage != null && mImage.getVisibility() != View.GONE) {
            final boolean isLayoutRtl = (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) ? getLayoutDirection() == View.LAYOUT_DIRECTION_RTL : false;
            int margin = HtcListItemManager.getM3(getContext());
            int left = (width - mImage.getMeasuredWidth()
                    - mTextView.getMeasuredWidth() - margin) / 2;
            int topOfText = (height - mTextView.getMeasuredHeight()) / 2;
            int topOfImage = 0;
            topOfImage = (height - mImage.getMeasuredHeight()) / 2;
            left = isLayoutRtl ? (left + mTextView.getMeasuredWidth() + margin) : left;
            mImage.layout(left, topOfImage, left + mImage.getMeasuredWidth(),
                    topOfImage + mImage.getMeasuredHeight());
            left = isLayoutRtl ? (left - margin - mTextView.getMeasuredWidth()) : (left
                    + mImage.getMeasuredWidth() + margin);
            mTextView.layout(left, topOfText, left + mTextView.getMeasuredWidth(), topOfText
                    + mTextView.getMeasuredHeight());
        } else {
            int left = 0;
            int topOfText = (height - mTextView.getMeasuredHeight()) / 2;
            mTextView.layout(left, topOfText, left + mTextView.getMeasuredWidth(), topOfText
                    + mTextView.getMeasuredHeight());
        }
    }

    /**
     * set the style of the text. Only for HTC defined style.
     *
     * @param defStyle the resource ID of the style (The font size will be
     *            changed.)
     */
    public void setTextStyle(int defStyle) {
        ((HtcFadingEdgeTextView) mTextView).setTextStyle(defStyle);
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
        mTextView.setText(mAllCaps ? text.toString().toUpperCase() : text.toString());
    }

    /**
     * Return the text of the TextView is displaying. If setText() was called
     * with an argument of BufferType.SPANNABLE or BufferType.EDITABLE, you can
     * cast the return value from this method to Spannable or Editable,
     * respectively.
     *
     * @return Return the text of the TextView is displaying.
     */
    @ExportedProperty(category = "CommonControl")
    public CharSequence getText() {
        return mTextView.getText();
    }

    /**
     * Sets the resource Id of string to the TextView.
     *
     * @param rId resource Id of string to display
     */
    public void setText(int rId) {
        setText(getContext().getResources().getText(rId));
    }

    /**
     * @deprecated [Not use any longer] This API will no longer be supported in
     *             Sense 5.0
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

    /**
     * please note: if true, marquee will NOT be used, instead, using fade out.
     * otherwise, use Truncate.END. to enable marquee, please use
     * enableMarquee(int, boolean)
     *
     * @param enable is marquee enabled
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void enableMarquee(boolean enable) {
        mIsMarqueeEnabled = enable;
        ((HtcFadingEdgeTextView) mTextView).setEnableMarquee(enable);
    }

    private void setDefaultTextStyle() {
        if (!mIsNoContentText) {
            if (mItemMode == HtcListItem.MODE_DEFAULT
                    || mItemMode == HtcListItem.MODE_KEEP_MEDIUM_HEIGHT) {
                if (mMode == HtcListItem2TextComponent.MODE_WHITE_LIST) {
                    // here should use bold font, follow UI guideline
                    setTextStyle(com.htc.lib1.cc.R.style.list_primary_m);
                } else {
                    setTextStyle(com.htc.lib1.cc.R.style.darklist_primary_m);
                }
            } else if (mItemMode == HtcListItem.MODE_POPUPMENU) {
                setTextStyle(com.htc.lib1.cc.R.style.darklist_primary_s);
            } else {
                setTextStyle(com.htc.lib1.cc.R.style.fixed_automotive_darklist_primary_m);
            }
        } else {
            setTextStyle(com.htc.lib1.cc.R.style.list_body_secondary_l);
        }
    }

    /**
     * This method will change to use Automotive text style
     *
     * @param enable enable automotive mode
     */
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
        // TODO Auto-generated method stub
        mItemMode = itemMode;
        mIsAutomotiveMode = (mItemMode == HtcListItem.MODE_AUTOMOTIVE) ? true : false;
        setDefaultTextStyle();
    }

    /**
     * If param grav is true, set gravity of the textView to center horizontal.
     *
     * @param grav
     */
    public void setGravityCenterHorizontal(boolean grav) {
        if (grav)
            mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        else
            mTextView.setGravity(Gravity.NO_GRAVITY);
    }

    /**
     * Set a view prior to the text.
     *
     * @param v the view is set prior to the text
     */
    public void setView(View v) {
        if (v != null && mImage == null) {
            mImage = v;
            addView(mImage, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            setTextStyle(com.htc.lib1.cc.R.style.fixed_list_primary_xxs);
        } else if (v == null) {
            removeView(mImage);
            mImage = v;
            setDefaultTextStyle();
        }
    }

    /**
     * Sets no content text. It will use style list_body_secondary_l and
     * transform input to ALL CAPS according to the locale.
     */
    public void setTextNoContentStyle() {
        if (!mIsNoContentText) {
            mAllCaps = com.htc.lib1.cc.util.res.HtcResUtil.isInAllCapsLocale(getContext());
            mIsNoContentText = true;
            setGravityCenterHorizontal(true);
            setDefaultTextStyle();
        }
    }
}
