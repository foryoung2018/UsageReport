package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;

class HtcListItem2TextComponent extends FrameLayout implements IHtcListItemComponent {
    final static int MAX_NUM_TEXT = 2;
    /**
     * The 2 textViews of first line and second line
     */
    protected TextView[] mText;

    /**
     * Text mode of HtcListItem text component, The text mode include 1.
     * whitelist 2. darklist 3. automotive_darklist
     */
    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = MODE_WHITE_LIST, to = "MODE_WHITE_LIST"),
            @IntToString(from = MODE_DARK_LIST, to = "MODE_DARK_LIST")
    })
    protected int mMode = MODE_WHITE_LIST;

    @ExportedProperty(category = "CommonControl", resolveId = true)
    int mTextStyle[] = {
            0, 0
    };
    private static final int INVALID_TEXTHEIGHT = -1;

    /** @deprecated this field is not used anymore, will be deleted at last */
    @Deprecated
    protected int mTextTopY[];
    @ExportedProperty(category = "CommonControl")
    private boolean mTextTopYUsed = false;
    /**
     * The font size of 2 textViews of first line and second line
     */
    @ExportedProperty(category = "CommonControl")
    protected int mFontSize[]; // it seems this can't be deleted.

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @ExportedProperty(category = "CommonControl")
    protected boolean mIsMarqueeEnabled = false;
    /**
     * The text mode MODE_WHITE_LIST is used for default list item.
     */
    public final static int MODE_WHITE_LIST = 0;
    /**
     * The text mode MODE_DARK_LIST is used for list item in popup menu.
     */
    public final static int MODE_DARK_LIST = 1;

    @ExportedProperty(category = "CommonControl")
    boolean mIsAutomotiveMode = false;

    @ExportedProperty(category = "CommonControl")
    boolean mIsFrontImageExist = false;
    @ExportedProperty(category = "CommonControl")
    int mBlackIconRightMargin;
    @ExportedProperty(category = "CommonControl")
    int mBlackIconSize = 0;

    private HtcListItemManager mHtcListItemManager;

    private void init(Context context) {
        mHtcListItemManager = HtcListItemManager.getInstance(context);

        mText = new TextView[MAX_NUM_TEXT];
        mTextTopY = new int[MAX_NUM_TEXT];
        mFontSize = new int[MAX_NUM_TEXT];
        mBlackIconRightMargin = HtcListItemManager.getM4(context);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    com.htc.lib1.cc.R.styleable.HtcListItemTextComponentMode);
            mMode = a.getInt(R.styleable.HtcListItemTextComponentMode_textMode, MODE_WHITE_LIST);
            mIsMarqueeEnabled = a.getBoolean(R.styleable.HtcListItemTextComponentMode_isMarquee,
                    false);
            a.recycle();
        } else {
            mMode = MODE_WHITE_LIST;
            mIsMarqueeEnabled = false;
        }
        init(context);
    }

    /**
     * Simple constructor to use when creating this widget from code. It will
     * new 2 textViews with default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItem2TextComponent(Context context) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

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
    public HtcListItem2TextComponent(Context context, AttributeSet attrs) {
        super(context, attrs);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

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
    public HtcListItem2TextComponent(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

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
    public HtcListItem2TextComponent(Context context, int mode) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        if (mode >= MODE_WHITE_LIST && mode <= MODE_DARK_LIST) {
            mMode = mode;
        } else {
            mMode = MODE_WHITE_LIST;
        }
        init(context);
    }

    /**
     * set the style of the text
     *
     * @param index which text you would like to change the style [0-1]
     * @param defStyle the resource ID of the style (The text size won't be
     *            changed unless you call setUseFontSizeInStyle(true))
     */
    private void setTextStyle(int index, int defStyle) {
        if (index >= 0 && index < MAX_NUM_TEXT) {
            ((HtcFadingEdgeTextView) mText[index]).setTextStyle(defStyle);
            if (!mIsAutomotiveMode && !mUseFontSizeInStyle)
                mText[index].setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontSize[index]);

            mTextStyle[index] = defStyle;

        }
    }

    @ExportedProperty(category = "CommonControl")
    private boolean mUseFontSizeInStyle = false;

    /**
     * in this class, mFontSize is used, but some APPs don't want to be tied.
     *
     * @param b if true, use the font size in the style, otherwise use
     *            mFontSize.
     */
    public void setUseFontSizeInStyle(boolean b) {
        mUseFontSizeInStyle = b;
        setTextStyle(0, mTextStyle[0]);
        setTextStyle(1, mTextStyle[1]);
    }

    void setText(TextView view, String text) {
        view.setText(text);

        if (text == null) {
            view.setVisibility(View.GONE);
        } else if (text.equals("")) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    void setText(TextView view, CharSequence text) {
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
     * set the text of the first line of text
     *
     * @param text the text displayed
     */
    public void setPrimaryText(String text) {
        setText(mText[0], text);
    }

    /**
     * set the text of the first line of text
     *
     * @param rId the resourcr ID of the text displayed
     */
    public void setPrimaryText(int rId) {
        String text = getContext().getResources().getString(rId);
        setText(mText[0], text);
    }

    /**
     * set the text of the first line of text
     *
     * @param text
     */
    public void setPrimaryText(CharSequence text) {
        setText(mText[0], text);
    }

    /**
     * set the visibility of primary text
     *
     * @param visibility The visibility of primary text
     */
    public void setPrimaryTextVisibility(int visibility) {
        mText[0].setVisibility(visibility);
    }

    /**
     * set the style of primary text. Only for HTC defined style.
     *
     * @param style The font size won't be changed unless you call
     *            setUseFontSizeInStyle(true)
     */
    public void setPrimaryTextStyle(int style) {
        setTextStyle(0, style);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public final void setPrimaryTextAutoLinkMask(int mask) {
        mText[0].setAutoLinkMask(mask);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public final void setPrimaryLinkTextColor(ColorStateList colors) {
        mText[0].setLinkTextColor(colors);
    }

    /**
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public final void setPrimaryLinkTextColor(int color) {
        mText[0].setLinkTextColor(color);
    }

    /**
     * get the content of primary text
     *
     * @return string the content of primary text
     */
    @ExportedProperty(category = "CommonControl")
    public String getPrimaryText() {
        return mText[0].getText().toString();
    }

    /**
     * get the content of primary text
     *
     * @return CharSequence the content of primary text
     */
    public CharSequence getPrimaryCharSequence() {
        return mText[0].getText();
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @ExportedProperty(category = "CommonControl")
    public int getPrimaryTextVisibility() {
        return mText[0].getVisibility();
    }

    /**
     * set the text of the second line of text
     *
     * @param text the text displayed
     */
    public void setSecondaryText(String text) {
        setText(mText[1], text);
    }

    /**
     * set the text of the second line of text
     *
     * @param text
     */
    public void setSecondaryText(CharSequence text) {
        setText(mText[1], text);
    }

    /**
     * set the text of the second line of text
     *
     * @param rId the resource ID of the text displayed
     */
    public void setSecondaryText(int rId) {
        String text = getContext().getResources().getString(rId);
        setText(mText[1], text);
    }

    /**
     * set the visibility of secondary text
     *
     * @param visibility The visibility of secondary text
     */
    public void setSecondaryTextVisibility(int visibility) {
        mText[1].setVisibility(visibility);
    }

    /**
     * set the style of secondary text. Only for HTC defined style.
     *
     * @param style The font size won't be changed unless you call
     *            setUseFontSizeInStyle(true)
     */
    public void setSecondaryTextStyle(int style) {
        setTextStyle(1, style);
    }

    /**
     * Sets the autolink mask of the text. See
     * {@link android.text.util.Linkify#ALL Linkify.ALL} and peers for possible
     * values.
     *
     * @attr ref android.R.styleable#TextView_autoLink
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public final void setSecondaryTextAutoLinkMask(int mask) {
        mText[1].setAutoLinkMask(mask);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public final void setSecondaryLinkTextColor(ColorStateList colors) {
        mText[1].setLinkTextColor(colors);
    }

    /**
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public final void setSecondaryLinkTextColor(int color) {
        mText[1].setLinkTextColor(color);
    }

    /**
     * get the content of second line of text
     *
     * @return string content of second line of text
     */
    @ExportedProperty(category = "CommonControl")
    public String getSecondaryText() {
        return mText[1].getText().toString();
    }

    /**
     * get the content of second line of text
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public CharSequence getSecondaryCharSequence() {
        return mText[1].getText();
    }

    /**
     * get the visibility of the second line text
     *
     * @return visibility The visibility of the second line text
     */
    @ExportedProperty(category = "CommonControl")
    public int getSecondaryTextVisibility() {
        return mText[1].getVisibility();
    }

    /** if false, views will be disabled and alpha value will be set to 0.4 */
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
            child.setEnabled(enabled);
        }
    }

    /**
     * please note: if true, marquee will NOT be used, instead, using fade out.
     * otherwise, use Truncate.END. to enable marquee, please use
     * enableMarquee(int, boolean)
     *
     * @param enable Is marquee enabled
     */
    public void enableMarquee(boolean enable) {
        mIsMarqueeEnabled = enable;
        ((HtcFadingEdgeTextView) mText[0]).setEnableMarquee(enable);
        ((HtcFadingEdgeTextView) mText[1]).setEnableMarquee(enable);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int totalHeight = 0;
        int maxWidth = 0;
        boolean isUseDesiredHeight = true;

        // don't use getChildAt(), or black icon's height will be counted in.
        for (int i = 0; i < mText.length; i++) {
            if (mText[i].getVisibility() != GONE) {
                if (i == 0) {
                    measureChild(mText[i], widthSpec, heightSpec);
                    totalHeight += mText[0].getMeasuredHeight();
                } else if (i == 1) {
                    if (mIsFrontImageExist) {
                        measureChildWithMargins(mText[i], widthSpec, mBlackIconSize
                                + mBlackIconRightMargin, heightSpec, 0);
                    } else {
                        measureChild(mText[i], widthSpec, heightSpec);
                    }

                    if (mText[1].getLineCount() > 1) {
                        isUseDesiredHeight = false;
                    }
                    totalHeight += mText[1].getMeasuredHeight();
                }

                maxWidth = Math.max(maxWidth, mText[i].getMeasuredWidth());
            }
        }

        final int desireHeight = mHtcListItemManager.getDesiredListItemHeight(mItemMode);
        if (!isUseDesiredHeight) {
            // try to add margins/gaps to totalHeight
            if (mText[0].getVisibility() != GONE && mText[1].getVisibility() != GONE) {
                // topGap and bottomGap must be honored.
                totalHeight += mHtcListItemManager.getDesiredTopGap(mItemMode)
                        + mHtcListItemManager.getDesiredBottomGap(mItemMode);

                if (totalHeight < desireHeight)
                    totalHeight = desireHeight;
            } else if (mText[0].getVisibility() != GONE || mText[1].getVisibility() != GONE) {
                // topGap and bottomGap should be honored.
                totalHeight += mHtcListItemManager.getDesiredTopGap(mItemMode)
                        + mHtcListItemManager.getDesiredBottomGap(mItemMode);
            } else {
                totalHeight = desireHeight;
            }
        } else {
            totalHeight = desireHeight;
        }

        // TODO maybe EXACTLY should be used.
        setMeasuredDimension(resolveSize(maxWidth, widthSpec), resolveSize(totalHeight, heightSpec));
    }

    /**
     * use this method when both text 1 and text 2 are not GONE.
     *
     * @param index 0 or 1.
     * @param isAlignLeft for 2LineText, true; for 2LineStamp, false.
     * @param offsetX 2linetext uses this to support the small icon.
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    protected void layoutText(int index, boolean isAlignLeft, int offsetX, int l, int t, int r, int b) {
        final boolean isLayoutRtl = (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) ? getLayoutDirection() == View.LAYOUT_DIRECTION_RTL : false;
        int top = 0;
        int left = 0;
        final int width = r - l;
        if (index == 0) {
            top = mHtcListItemManager.getPrimaryBaseLine(mItemMode) - mText[0].getBaseline();
        } else if (index == 1) {
            top = mHtcListItemManager.getSecondaryBaseLine(mItemMode) - mText[1].getBaseline();
        } else
            return;
        if (isLayoutRtl) {
            left = isAlignLeft ? (width - mText[index].getMeasuredWidth() - offsetX)
                    : offsetX;
            mText[index].layout(
                    left,
                    top,
                    left + mText[index].getMeasuredWidth(),
                    top + mText[index].getMeasuredHeight());
        } else {
            mText[index].layout(
                    offsetX + (isAlignLeft ? 0 : width - mText[index].getMeasuredWidth()),
                    top,
                    offsetX + (isAlignLeft ? mText[index].getMeasuredWidth() : width),
                    top + mText[index].getMeasuredHeight());
        }
    }

    /**
     * use this method when only 1 of text 1 and text 2 is GONE.
     *
     * @param index 0 or 1.
     * @param isAlignLeft for 2LineText, true; for 2LineStamp, false.
     * @param offsetX 2linetext uses this to support the small icon.
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    protected void layoutTextAtCenter(int index, boolean isAlignLeft, int offsetX, int l, int t, int r, int b) {
        // workaround for protected field, it should be private.
        if (mTextTopY[0] != 0 || mTextTopY[1] != 0)
            mTextTopYUsed = true;

        if (index > 1 || index < 0)
            return;

        if (index == 1) {
            if (mText[index].getLineCount() != 1) {
            }
        }

        final int width = r - l;
        final int height = b - t;
        int top = (height - mText[index].getMeasuredHeight()) / 2;
        // workaround for protected field, it should be private.
        if (mTextTopYUsed)
            top = mTextTopY[index];

        mText[index].layout(
                offsetX + (isAlignLeft ? 0 : width - mText[index].getMeasuredWidth()),
                top,
                offsetX + (isAlignLeft ? mText[index].getMeasuredWidth() : width), top
                        + mText[index].getMeasuredHeight());
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    protected void dispatchDraw(Canvas arg0) {
        super.dispatchDraw(arg0);
    }

    // Luolai Jyun from U21 need these, because the mText is labeled with @hide
    /**
     * Use this API to get primary textView
     *
     * @return textView primary textView
     */
    public TextView getPrimaryTextView() {
        if (mText != null && mText[0] != null)
            return mText[0];
        return null;
    }

    /**
     * Use this API to get secondary textView
     *
     * @return textView secondary textView
     */
    public TextView getSecondaryTextView() {
        if (mText != null && mText[1] != null)
            return mText[1];
        return null;
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
    }
}
