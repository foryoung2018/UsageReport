package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.LogUtil;

/**
 * This class is used when there's text body. The component looks as following:
 *
 * <pre class="prettyprint">
 * Sense 35:
 * ----------------------------------------
 * || ------                 [] [] [] []  |
 * || |    |   Text fLing 1               |
 * || |    |   Text fLing 2      12:34PM  |
 * || ------                              |
 * ||  body     body    body    body body |
 * ||                                     |
 * ----------------------------------------
 *
 * Sense 40:
 * ________________________________________
 * |  ______                              |
 * || |    |   Text fLing 1  [] [] [] []  |
 * || |    |   Text fLing 2      12:34PM  |
 * || ``````                              |
 * ||          body starts from here      |
 * |                                      |
 * ````````````````````````````````````````
 * ColorBar in message body is not recommended.
 * </pre>
 *
 * ColorBar affects measure and layout too much, so ignore it. Goal: 0, IGNORE
 * ColorBar 1, every part of this widget can be enabled or disabled 2, remove
 * some parts if possible How many parts: 0, ColorBar will be ignored, really 1,
 * big badge 2.1, badge & bubble 2.2, 2 line text 2.3, 2 line stamp 4, body text
 * Rules: 1, if badge not exist, others will be aligned to the left. 2, if
 * 2linetext and/or 2linestamp not exist, body text will be moved up.
 */
public class HtcListItemMessageBody extends FrameLayout implements IHtcListItemTextComponent,
        IHtcListItemComponent {

    final static int MAX_NUM_TEXT = 2;
    private TextView mText[];
    @ExportedProperty(category = "CommonControl", resolveId = true)
    private int mTextStyles[] = {
            0, 0, 0
    };

    private HtcListItem7Badges1LineBottomStamp mBadgeStamp;

    static int MAX_BADGES = 7;

    // 5, body text
    private TextView mBody;

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public final static int MODE_WHITE_LIST = 0;
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public final static int MODE_DARK_LIST = 1;
    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = MODE_WHITE_LIST, to = "MODE_WHITE_LIST"),
            @IntToString(from = MODE_DARK_LIST, to = "MODE_DARK_LIST")
    })
    private int mMode = MODE_WHITE_LIST;

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @ExportedProperty(category = "CommonControl")
    protected boolean mIsMarqueeEnabled = false;

    final static boolean LOG = false;
    final static String LOG_TAG = "HtcListItemMessageBody";
    private HtcListItemManager mHtcListItemManager;

    private void init(Context context, AttributeSet attrs) {
        boolean marquee;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    com.htc.lib1.cc.R.styleable.HtcListItemTextComponentMode);
            mMode = a.getInt(R.styleable.HtcListItemTextComponentMode_textMode, MODE_WHITE_LIST);
            marquee = a.getBoolean(R.styleable.HtcListItemTextComponentMode_isMarquee, false);
            a.recycle();
        } else {
            mMode = MODE_WHITE_LIST;
            marquee = false;
        }
        init(context);
        enableMarquee(marquee);
        setVisibility();
    }

    private void initDrawable(Context context, AttributeSet attrs, int defStyle) {
    }

    // remember to call this at the end of init(Context), make sure
    // both xml-based & programatic way are OK
    private void addAllChild() {

        addView(mText[0], new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mText[1], new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mBody, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mBadgeStamp);
    }

    // init is init, don't do too much irrelevant thing.
    /** init all children then addAllChild() */
    private void init(Context context) {
        mHtcListItemManager = HtcListItemManager.getInstance(context);
        mText = new TextView[MAX_NUM_TEXT];
        mText[0] = new HtcFadingEdgeTextView(context);
        mText[1] = new HtcFadingEdgeTextView(context);

        mBadgeStamp = new HtcListItem7Badges1LineBottomStamp(context);
        mBadgeStamp.setM2Enable(true);

        mBody = new HtcFadingEdgeTextView(context);

        if (mMode == MODE_WHITE_LIST) {
            setPrimaryTextStyle(com.htc.lib1.cc.R.style.list_primary_m);
            setSecondaryTextStyle(com.htc.lib1.cc.R.style.info_primary_m);
            setBodyTextStyle(com.htc.lib1.cc.R.style.list_secondary_m);
        } else {
            setPrimaryTextStyle(com.htc.lib1.cc.R.style.darklist_primary_m_bold);
            setSecondaryTextStyle(com.htc.lib1.cc.R.style.info_primary_m);
            setBodyTextStyle(com.htc.lib1.cc.R.style.list_secondary_m);
        }

        super.setPadding(0, 0, 0, 0);

        addAllChild();
    }

    private void setVisibility() {
        mText[0].setVisibility(View.GONE);
        mText[1].setVisibility(View.GONE);
        mBody.setVisibility(View.GONE);
    }

    /**
     * Simple constructor to use when creating this widget from code. It will
     * new a view with default text style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItemMessageBody(Context context) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        initDrawable(context, null, 0);
        init(context);
    }

    /**
     * Constructor that is called when inflating this widget from XML. This is
     * called when a view is being constructed from an XML file, supplying
     * attributes that were specified in the XML file.It will new a view with
     * default text style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemMessageBody(Context context, AttributeSet attrs) {
        super(context, attrs);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        initDrawable(context, attrs, 0);
        init(context, attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of this widget allows subclasses to use their own base style
     * when they are inflating. It will new a view with default text style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @param defStyle The default style to apply to this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemMessageBody(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        initDrawable(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * Constructor that is called when inflating this widget from code. It will
     * new a view with specified style, mode.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param mode to indicate item mode for HtcListItem.
     */
    public HtcListItemMessageBody(Context context, int mode) {
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
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) params).setMargins(0, 0, 0, 0);
        }
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
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
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);
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
    protected void onMeasure(int wSpec, int hSpec) {

        final int widthOfSpec = MeasureSpec.getSize(wSpec);
        int widthOfPrimaryText;
        int widthOfSecondaryText;
        int widthOfBodyText;
        int mListItemMargin = HtcListItemManager.getM2(getContext());
        int mListItemEndMargin;
        mListItemEndMargin = HtcListItemManager.getM1(getContext());

        widthOfBodyText = widthOfSpec - mListItemEndMargin;
        if (widthOfBodyText < 0) {
            LogUtil.logE(LOG_TAG,
                    "widthOfSpec - mListItemEndMargin < 0 :",
                    " widthOfSpec = ", widthOfSpec,
                    ", mListItemEndMargin = ", mListItemEndMargin);
            widthOfBodyText = 0;
        }
        // 1, big badge

        // when using heightSpec, we don't care the hSpec imposed by parent.
        // To Primary, Secondary, Stamp, Body, their heights are UNSPECIFIED
        final int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        // 2, 7Badges1LineBottomStamp
        if (mBadgeStamp.getVisibility() != View.GONE) {

            widthOfPrimaryText = widthOfSecondaryText = widthOfSpec - mListItemEndMargin - mListItemMargin;
            if (widthOfPrimaryText < 0) {
                LogUtil.logE(LOG_TAG,
                        "widthOfSpec - mListItemEndMargin - mListItemMargin < 0 :",
                        " widthOfSpec = ", widthOfSpec,
                        ", mListItemEndMargin = ", mListItemEndMargin,
                        ", mListItemMargin = ", mListItemMargin);
                widthOfPrimaryText = widthOfSecondaryText = 0;
            }

            measureChild(mBadgeStamp, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    hSpec);

            measureChild(
                    mBadgeStamp,
                    MeasureSpec.makeMeasureSpec(mBadgeStamp.getMeasuredWidth() + mListItemEndMargin
                            + mListItemMargin, MeasureSpec.EXACTLY), hSpec);

            final int badgesWidth = mBadgeStamp.getMeasuredBadgesWidth();
            final int stampWidth = mBadgeStamp.getMeasuredStampWidth();
            if (mBadgeStamp.isBadgesVerticalCenter()) {
                int maxWidth = Math.max(badgesWidth,stampWidth);
                if (maxWidth != 0) {
                    if (widthOfPrimaryText < maxWidth) {
                        LogUtil.logE(LOG_TAG,
                                "widthOfPrimaryText or widthOfSecondaryText < maxWidth :",
                                " widthOfPrimaryText = ", widthOfPrimaryText,
                                ", widthOfSecondaryText = ", widthOfSecondaryText,
                                ", maxWidth = ", maxWidth);
                        widthOfPrimaryText = widthOfSecondaryText = 0;
                    } else {
                        widthOfPrimaryText -= maxWidth;
                        widthOfSecondaryText -= maxWidth;
                    }
                }
            } else {
                if (badgesWidth != 0) {
                    if (widthOfPrimaryText < badgesWidth) {
                        LogUtil.logE(LOG_TAG,
                                "widthOfPrimaryText < badgesWidth :",
                                " widthOfPrimaryText = ", widthOfPrimaryText,
                                ", badgesWidth = ", badgesWidth);
                        widthOfPrimaryText = 0;
                    } else {
                        widthOfPrimaryText -= badgesWidth;
                    }
                }

                if (stampWidth != 0) {
                    if (widthOfSecondaryText < stampWidth) {
                        LogUtil.logE(LOG_TAG,
                                "widthOfSecondaryText < stampWidth :",
                                " widthOfSecondaryText = ", widthOfSecondaryText,
                                ", stampWidth = ", stampWidth);
                        widthOfSecondaryText = 0;
                    } else {
                        widthOfSecondaryText -= stampWidth;
                    }
                }
            }
        } else {
            widthOfPrimaryText = widthOfSecondaryText = widthOfSpec - mListItemEndMargin;
            if (widthOfPrimaryText < 0) {
                LogUtil.logE(LOG_TAG,
                        "widthOfSpec - mListItemEndMargin < 0 :",
                        " widthOfSpec = ", widthOfSpec,
                        ", mListItemEndMargin = ", mListItemEndMargin);
                widthOfPrimaryText = widthOfSecondaryText = 0;
            }
        }

        // 3, primary text
        if (mText[0].getVisibility() != View.GONE) {
            measureChild(mText[0],
                    MeasureSpec.makeMeasureSpec(widthOfPrimaryText, MeasureSpec.EXACTLY),
                    heightSpec);
        }

        // 4, stamp

        // 5, secondary text
        if (mText[1].getVisibility() != View.GONE) {
            measureChild(mText[1],
                    MeasureSpec.makeMeasureSpec(widthOfSecondaryText, MeasureSpec.EXACTLY),
                    heightSpec);
        }
        // 6, body
        if (mBody.getVisibility() != View.GONE) {
            measureChild(mBody, MeasureSpec.makeMeasureSpec(widthOfBodyText, MeasureSpec.EXACTLY),
                    heightSpec);
        }

        int finalHeight = mHtcListItemManager.getDesiredTopGap(mItemMode);// HtcListItemManager.getM2();
        finalHeight = mHtcListItemManager.getDesiredListItemHeight(mItemMode);
        if (mBody.getVisibility() != View.GONE) {
            finalHeight += mBody.getMeasuredHeight();
        }
        if (finalHeight < mHtcListItemManager.getDesiredListItemHeight(mItemMode))
            finalHeight = mHtcListItemManager.getDesiredListItemHeight(mItemMode);

        setMeasuredDimension(widthOfSpec, finalHeight);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cLeft = 0, cTop = 0, cBottom = 0;
        final boolean isLayoutRtl = (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) ? getLayoutDirection() == View.LAYOUT_DIRECTION_RTL : false;
        final int width = r - l;
        // 1, badge & bubble

        // 2, body text
        if (mBody.getVisibility() != View.GONE) {
            cTop = (int) (mHtcListItemManager.getDesiredListItemHeight(mItemMode) - mHtcListItemManager
                    .getDesiredBottomGap(mItemMode));

            cBottom = cTop + mBody.getMeasuredHeight();
            cLeft = isLayoutRtl ? width - mBody.getMeasuredWidth() : 0;
            mBody.layout(cLeft, cTop, cLeft + mBody.getMeasuredWidth(), cBottom);
        }

        // 3, primary text
        if (mText[0].getVisibility() != View.GONE) {
            cTop = mHtcListItemManager.getPrimaryBaseLine(mItemMode) - mText[0].getBaseline();
            cLeft = isLayoutRtl ? width - mText[0].getMeasuredWidth() : 0;
            mText[0].layout(cLeft, cTop, cLeft + mText[0].getMeasuredWidth(),
                    cTop + mText[0].getMeasuredHeight());
        }

        // 4, secondary text
        if (mText[1].getVisibility() != View.GONE) {
            cTop = mHtcListItemManager.getSecondaryBaseLine(mItemMode) - mText[1].getBaseline();
            cLeft = isLayoutRtl ? width - mText[1].getMeasuredWidth() : 0;
            mText[1].layout(cLeft, cTop, cLeft + mText[1].getMeasuredWidth(),
                    cTop + mText[1].getMeasuredHeight());
            cBottom = cTop + mText[1].getBaseline();
        }

        // 5, stamp

        if (mBadgeStamp.getVisibility() != View.GONE) {
            cLeft = isLayoutRtl ? 0 : width - mBadgeStamp.getMeasuredWidth();
            mBadgeStamp.layout(cLeft, 0, cLeft + mBadgeStamp.getMeasuredWidth(),
                    0 + mBadgeStamp.getMeasuredHeight());
        }

    }

    /**
     * @deprecated [Not use any longer] This method will no longer be supported
     *             in Sense 5.0 due to design change
     */
    /** @hide */
    public void setColorBarImageDrawable(Drawable d) {
    }

    /**
     * @deprecated [Not use any longer] This method will no longer be supported
     *             in Sense 5.0 due to design change
     */
    /** @hide */
    public void setColorBarImageResource(int rId) {
    }

    /**
     * @deprecated <b>ColorBar in message body is not recommended.</b> Set the
     *             bitmap for the color bar
     * @param bm
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Deprecated
    public void setColorBarImageBitmap(Bitmap bm) {
    }

    /**
     * @deprecated <b>ColorBar in message body is not recommended.</b> Get the
     *             drawable of htc color bar
     * @return
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Deprecated
    public Drawable getColorBarImageDrawable() {
        return null;
    }

    private void setTextStyle(int index, int defStyle) {
        if (index >= 0 && index < HtcListItem2TextComponent.MAX_NUM_TEXT) {
            mTextStyles[index] = defStyle;
            ((HtcFadingEdgeTextView) mText[index]).setTextStyle(defStyle);
        }
    }

    private boolean compareText(CharSequence text1, CharSequence text2) {
        if (text1 == null && text2 == null)
            return true;
        if (text1 != null && text1.equals(text2))
            return true;
        return false;
    }

    private void setText(TextView view, String text) {
        if (compareText(view.getText(), text))
            return;
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
        if (compareText(view.getText(), text))
            return;
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
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setPrimaryText(String text) {
        setText(mText[0], text);
    }

    /**
     * set the text of the first line of text
     *
     * @param text The text of the first line of text
     */
    public void setPrimaryText(CharSequence text) {
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
     * set the visibility of primary text
     *
     * @param visibility The visibility of primary text
     */
    /** @deprecated This method will no longer be supported in Sense 5+ */
    @Deprecated
    public void setPrimaryTextVisibility(int visibility) {
    }

    /**
     * set the style of primary text
     *
     * @param style the font size is list_primary_m
     */
    public void setPrimaryTextStyle(int style) {
        setTextStyle(0, style);
    }

    /**
     * get the content of primary text
     *
     * @return
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @ExportedProperty(category = "CommonControl")
    public String getPrimaryText() {
        return mText[0].getText().toString();
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
     * @param text the text displayed
     */
    public void setSecondaryText(CharSequence text) {
        setText(mText[1], text);
    }

    /**
     * set the text of the second line of text
     *
     * @param rId the resourcr ID of the text displayed
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setSecondaryText(int rId) {
        String text = getContext().getResources().getString(rId);
        setText(mText[1], text);
    }

    /**
     * set the visibility of primary text
     *
     * @param visibility the visibility of primary text
     */
    /** @deprecated This method will no longer be supported in Sense 5+ */
    @Deprecated
    public void setSecondaryTextVisibility(int visibility) {
    }

    /**
     * set the style of secondary text
     *
     * @param style the font size is list_secondary_m
     */
    public void setSecondaryTextStyle(int style) {
        setTextStyle(1, style);
    }

    /**
     * get the content of second line of text
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @ExportedProperty(category = "CommonControl")
    public String getSecondaryText() {
        return mText[1].getText().toString();
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @ExportedProperty(category = "CommonControl")
    public int getSecondaryTextVisibility() {
        return mText[1].getVisibility();
    }

    /**
     * Set whether to show the small icon or not
     *
     * @param index The index of those small icons which is 0-6. (from left to
     *            right)
     * @param enable Whether to show the icon or not
     */
    /** @deprecated This method will no longer be supported in Sense 5.0 */
    @Deprecated
    public void setBadgeState(int index, boolean enable) {
    }

    /**
     * get the instance of the small badges
     *
     * @param index
     * @return
     */
    /**
     * @deprecated [Not use any longer] This method will no longer be supported
     *             in Sense 5.0 due to design change
     */
    /** @hide */
    public View getBadge(int index) {
        return null;
    }

    /**
     * Set the text of the text stamp
     *
     * @param text the string of text stamp
     */
    /**
     * @deprecated [Not use any longer] mFlagButton will no longer be supported
     *             in Sense 5.0
     */
    /** @hide */
    public void setTextStamp(String text) {
    }

    /**
     * Set the text of the text stamp
     *
     * @param text the string of text stamp
     */
    /**
     * @deprecated [Not use any longer] mFlagButton will no longer be supported
     *             in Sense 5.0
     */
    /** @hide */
    public void setTextStamp(CharSequence text) {
    }

    /**
     * Set the text of the text stamp
     *
     * @param rId resource ID of the displayed string
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setTextStamp(int rId) {
        String text = getContext().getResources().getString(rId);
        setTextStamp(text);
    }

    /**
     * Get the text of the text stamp
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    /**
     * @deprecated [Not use any longer] mFlagButton will no longer be supported
     *             in Sense 5.0
     */
    /** @hide */
    public String getTextStamp() {
        return "";
    }

    /**
     * The text style of the stamp
     *
     * @param defStyle the font size is list_secondary_m
     */
    /**
     * @deprecated [Not use any longer] mFlagButton will no longer be supported
     *             in Sense 5.0
     */
    /** @hide */
    public void setStampTextStyle(int defStyle) {
    }

    /**
     * @deprecated [Not use any longer] mFlagButton will no longer be supported
     *             in Sense 5.0
     */
    /** @hide */
    public void setStampVisibility(int visibility) {
    }

    /**
     * @deprecated [Not use any longer] mFlagButton will no longer be supported
     *             in Sense 5.0
     */
    /** @hide */
    public int getStampVisibility() {
        return 0;
    }

    /**
     * set the text for text body in the bottom
     *
     * @param text the text displayed
     */
    // TODO deleted this
    public void setBodyText(String text) {
        setText(mBody, text);
    }

    /**
     * set the text for text body in the bottom
     *
     * @param text the text displayed
     */
    public void setBodyText(CharSequence text) {
        setText(mBody, text);
        // // TODO use MeasureText instead.
    }

    /**
     * Set the resource ID of the text displayed in the body
     *
     * @param rId
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setBodyTextResource(int rId) {
        String text = getContext().getResources().getString(rId);
        setBodyText(text);
    }

    /**
     * Get the text shown in the body
     *
     * @return
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @ExportedProperty(category = "CommonControl")
    public String getBodyTextContent() {
        return (String) mBody.getText();
    }

    /**
     * Use this API to set visibility of body text
     *
     * @param visibility The visibility of body text
     */
    /** @deprecated This method will no longer be supported in Sense 5+ */
    @Deprecated
    public void setBodyVisibility(int visibility) {
    }

    /**
     * set the style of text for text body
     *
     * @param defStyle style the font size is list_body_read_l
     */
    public void setBodyTextStyle(int defStyle) {
        mTextStyles[2] = defStyle;
        ((HtcFadingEdgeTextView) mBody).setTextStyle(defStyle);
    }

    /**
     * make text body exactly this many lines tall
     *
     * @param lines line count of body text
     */
    public void setBodyTextLine(int lines) {
        mBody.setLines(lines);
    }

    /**
     * make text body at least this many lines tall
     *
     * @param lines min lines of body text
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setBodyTextMinLines(int lines) {
        mBody.setMinLines(lines);
    }

    /**
     * make text body at most this many lines tall
     *
     * @param lines max lines of body text
     */
    public void setBodyTextMaxLines(int lines) {
        mBody.setMaxLines(lines);
    }

    /**
     * set the text body to be visible or gone
     *
     * @param enable Is body text enabled
     */
    public void enableBodyText(boolean enable) {
        if (enable) {
            mBody.setVisibility(View.VISIBLE);
        } else {
            mBody.setVisibility(View.GONE);
        }
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setEnabled(boolean enabled) {
        if (isEnabled() != enabled) {
            super.setEnabled(enabled);

            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != null) {
                    child.setEnabled(enabled);
                    HtcListItemManager.setViewOpacity(child, enabled);
                }
            }
        }
    }

    /**
     * please note: if true, marquee will NOT be used, instead, using fade out.
     * otherwise, use Truncate.END.
     *
     * @param enable
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void enableMarquee(boolean enable) {
        mIsMarqueeEnabled = enable;
        ((HtcFadingEdgeTextView) mText[0]).setEnableMarquee(enable);
        ((HtcFadingEdgeTextView) mText[1]).setEnableMarquee(enable);
    }

    private void setDefaultTextStyle() {
        if (mItemMode == HtcListItem.MODE_DEFAULT
                || mItemMode == HtcListItem.MODE_KEEP_MEDIUM_HEIGHT) {
            if (mMode == MODE_WHITE_LIST) {
                setPrimaryTextStyle(com.htc.lib1.cc.R.style.list_primary_m);
                setSecondaryTextStyle(com.htc.lib1.cc.R.style.info_primary_m);
                setStampTextStyle(com.htc.lib1.cc.R.style.separator_secondary_m);
                setBodyTextStyle(com.htc.lib1.cc.R.style.list_secondary_m);
            } else {
                setPrimaryTextStyle(com.htc.lib1.cc.R.style.darklist_primary_m_bold);
                setSecondaryTextStyle(com.htc.lib1.cc.R.style.info_primary_m);
                setStampTextStyle(com.htc.lib1.cc.R.style.b_separator_secondary_m);
                setBodyTextStyle(com.htc.lib1.cc.R.style.list_secondary_m);
            }
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
        setDefaultTextStyle();
    }

    /**
     * Use this API to get instance of HtcListItem7Badges1LineBottomStamp
     *
     * @return the instance of HtcListItem7Badges1LineBottomStamp
     */
    public HtcListItem7Badges1LineBottomStamp get7Badges1LineBottomStamp() {
        return mBadgeStamp;
    }
}
