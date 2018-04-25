
package com.htc.lib1.cc.widget;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.cc.util.LogUtil;

/**
 * HtcGridItem is designed as a single grid item for HtcGridView.
 * NOTE: HtcGridItem MUST be created after super.onCreate() in Activity otherwise texts may be truncated after font style changes.
 *
 * To follow UI guideline, please set the layout width and layout height to WRAP_CONTENT or MATCH_PARENT,
 * and specify the mode MODE_GENERIC on HtcGridView (HtcGridView.setMode(HtcGridView.MODE_GENERIC)).
 * In general, the item width is determined by HtcGridView (according the number of columns, stretch mode and gaps),
 * and the value of image height always equals the value of image (item) width by default.
 *
 * HtcGridItem provides the below features:
 *     1. An 1-line primary text below a image.
 *     2. An 1-line primary text and an 1-line secondary text below a image.
 *     3. An two-line primary text below a image. The two-line primary text is only supported when the secondary is gone.
 *     4. An indicator shown on the lower right corner of the image.
 */
public class HtcGridItem extends HtcGridItemBase {

    private final static String TAG = "HtcGridItem";

    @ExportedProperty(category = "CommonControl")
    private int mTextHorizontalMargin = 0;

    @ExportedProperty(category = "CommonControl")
    private int mTextMarginBottom = 0;

    @ExportedProperty(category = "CommonControl")
    private int mAutomotiveTextMarginBottom = 0;

    @ExportedProperty(category = "CommonControl")
    private int mPrimaryTextMarginTop = 0;

    @ExportedProperty(category = "CommonControl")
    private int mAutomotivePrimaryTextMarginTop = 0;

    @ExportedProperty(category = "CommonControl")
    private static int sTwoLineTextHeight = 0;

    @ExportedProperty(category = "CommonControl")
    private static int sAutomotiveTwoLineTextHeight = 0;

    @ExportedProperty(category = "CommonControl")
    private static int sCurrentFontSize = 0;

    @ExportedProperty(category = "CommonControl")
    private static int sAutomotiveFontSize = 0;

    @ExportedProperty(category = "CommonControl")
    private static int sCurrentFontStyle = 0;

    @ExportedProperty(category = "CommonControl")
    private static boolean sAutomotiveEnabled = false;

    @ExportedProperty(category = "CommonControl")
    private static boolean sItemModeChanged = false;

    @ExportedProperty(category = "CommonControl")
    private boolean mShouldCheckFontStyleChanged = true;

    private boolean mInit = true;

    @ExportedProperty(category = "CommonControl")
    private boolean mTwoLinePrimaryText = false;

    @ExportedProperty(category = "CommonControl")
    private boolean mShowSecondaryText = false;

    private TextView mSecondaryText;

    private static Field sFlipFontField = null;

    /**
     * Simple constructor to use when creating this widget from code. It will
     * new a view with default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcGridItem(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating this widget from XML. This is
     * called when a view is being constructed from an XML file, supplying
     * attributes that were specified in the XML file.It will new a view with
     * default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     */
    public HtcGridItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of this widget allows subclasses to use their own base style
     * when they are inflating. It will new a view with default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @param defStyle The default style to apply to this widget.
     */
    public HtcGridItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (mAutomotiveEnabled) {
            sAutomotiveEnabled = mAutomotiveEnabled;
        }
        mPrimaryText.setSingleLine();
        setupMargins();
    }

    private void setupMargins() {
        Resources res = getContext().getResources();
        mTextHorizontalMargin = res.getDimensionPixelOffset(R.dimen.margin_m);
        mPrimaryTextMarginTop = res.getDimensionPixelOffset(R.dimen.spacing);
        mTextMarginBottom = res.getDimensionPixelOffset(R.dimen.margin_l);
        mAutomotiveTextMarginBottom = res.getDimensionPixelOffset(R.dimen.spacing_2);
        mAutomotivePrimaryTextMarginTop = res.getDimensionPixelOffset(R.dimen.leading);
    }

    /**
     * {@inheritDoc}
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = mItemWidth - 2 * mTextHorizontalMargin;
        if (width < 0) {
            LogUtil.logE(TAG,
                    "mItemWidth - 2 * mTextHorizontalMargin < 0 :",
                    " mItemWidth = ", mItemWidth,
                    ", mTextHorizontalMargin = ", mTextHorizontalMargin);
            width = 0;
        }
        // Measure the primary text
        int primaryTextWidthMS = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int primaryTextHeightMS = getPrimaryTextHeightMeasureSpec();
        mPrimaryText.measure(primaryTextWidthMS, primaryTextHeightMS);
        int primaryTextHeight = mPrimaryText.getMeasuredHeight();

        // Measure the secondary text
        if (mSecondaryText != null && mSecondaryText.getVisibility() != View.GONE) {
            int secondaryTextWidthMS = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            int secondaryTextHeightMS = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            mSecondaryText.measure(secondaryTextWidthMS, secondaryTextHeightMS);
        }

        // Set the total height of the grid item.
        int secondaryTextHeight = mSecondaryText != null
                && mSecondaryText.getVisibility() != View.GONE ? mSecondaryText.getMeasuredHeight()
                : 0;
        int primaryTextMarginTop = !mAutomotiveEnabled ? mPrimaryTextMarginTop : mAutomotivePrimaryTextMarginTop;
        int textMarginBottom = !mAutomotiveEnabled ? mTextMarginBottom
                : mAutomotiveTextMarginBottom;

        int totalHeight = mImageView.getMeasuredHeight() + primaryTextMarginTop
                + primaryTextHeight + secondaryTextHeight + textMarginBottom;
        setMeasuredDimension(mItemWidth, totalHeight);
    }

    /**
     * {@inheritDoc}
     */
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // Layout the primary text
        int primaryTextMarginTop = !mAutomotiveEnabled ? mPrimaryTextMarginTop : mAutomotivePrimaryTextMarginTop;
        int primaryTextTop = mImageView.getBottom() + primaryTextMarginTop;
        int primaryTextLeft = mTextHorizontalMargin;
        mPrimaryText.layout(primaryTextLeft, primaryTextTop,
                primaryTextLeft + mPrimaryText.getMeasuredWidth(),
                primaryTextTop + mPrimaryText.getMeasuredHeight());

        // Layout the secondary text
        if (mSecondaryText != null && mSecondaryText.getVisibility() != View.GONE) {
            int secondaryTextLeft = mTextHorizontalMargin;
            int secondaryTextTop = mPrimaryText.getBottom();
            mSecondaryText.layout(secondaryTextLeft, secondaryTextTop, secondaryTextLeft
                    + mSecondaryText.getMeasuredWidth(),
                    secondaryTextTop + mSecondaryText.getMeasuredHeight());
        }
    }

    private boolean isFontSizeChanged(int fontSize) {
        return !mAutomotiveEnabled ? mInit && sCurrentFontSize != fontSize : mInit
                && sAutomotiveFontSize != fontSize;
    }

    private boolean shouldMeasureWithItemModeChanged(int fontSize) {
        return !mAutomotiveEnabled ? sCurrentFontSize != fontSize : sAutomotiveFontSize != fontSize;
    }

    private boolean isFirstMeasured() {
        return !mAutomotiveEnabled ? sCurrentFontSize == 0 : sAutomotiveFontSize == 0;
    }

    private void recordStaticFontSize(int fontSize) {
        if (mAutomotiveEnabled) {
            sAutomotiveFontSize = fontSize;
        } else {
            sCurrentFontSize = fontSize;
        }
    }

    boolean isFontStyleChanged() {
        if(!mShouldCheckFontStyleChanged) {
            // we only need to check the font style change once after the item is created.
            return false;
        }

        try {
            if(sFlipFontField == null) {
                Class<?> classOfConfiguration = Class.forName("android.content.res.Configuration");
                if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                    sFlipFontField = classOfConfiguration.getDeclaredField("fontChange");
                } else {
                    sFlipFontField = classOfConfiguration.getDeclaredField("FlipFont");
                }
            }

            int flipFont = sFlipFontField.getInt(getContext().getResources().getConfiguration());
            if(sCurrentFontStyle!=0 && flipFont != sCurrentFontStyle) {
                sCurrentFontStyle = flipFont;
                return true;
            }
            sCurrentFontStyle = flipFont;
        } catch (ClassNotFoundException e) {
            Log.d(TAG,"android.content.res.Configuration class is not found");
        } catch (IllegalAccessException e) {
            Log.d(TAG,"IllegalAccessException");
        } catch (NoSuchFieldException e) {
            Log.d(TAG,android.os.Build.VERSION.SDK_INT+": No FlipFont field in android.content.res.Configuration");
        }

        return false;
    }

    private void determine2LinePrimaryTextHeight() {
        int fontSize = (int) mPrimaryText.getTextSize();
        if (HtcBuildFlag.Htc_DEBUG_flag) {
            Log.d(TAG, "sItemModeChanged = " + sItemModeChanged + " mAutomotiveEnabled= "
                    + mAutomotiveEnabled + " mInit = " + mInit + " sCurrentFontSize = "
                    + sCurrentFontSize + " sAutomotiveFontSize=" + sAutomotiveFontSize
                    + " fontSize = " + fontSize + " sCurrentFontStyle="+sCurrentFontStyle);
        }
        // To reduce the redundant measure time, we only calculate 2-line height
        // in three cases:
        // 1. First measure
        // 2. Font size change
        // 3. Item mode is changed and the font size is out of date.
        if (sItemModeChanged) {
            if (shouldMeasureWithItemModeChanged(fontSize)) {
                measure2LineHeight();
            }
            sItemModeChanged = false;
            recordStaticFontSize(fontSize);
        } else if (isFontSizeChanged(fontSize) || isFirstMeasured() || isFontStyleChanged()) {
            measure2LineHeight();
            recordStaticFontSize(fontSize);
            mInit = false;
        }
        mShouldCheckFontStyleChanged = false;
    }

    private void measure2LineHeight() {
        mPrimaryText.setText("\n ");
        int primaryTextWidthMS = MeasureSpec.makeMeasureSpec(mItemWidth, MeasureSpec.EXACTLY);
        int primaryTextHeightMS = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        mPrimaryText.measure(primaryTextWidthMS, primaryTextHeightMS);
        if (!mAutomotiveEnabled) {
            sTwoLineTextHeight = mPrimaryText.getMeasuredHeight();
        } else {
            sAutomotiveTwoLineTextHeight = mPrimaryText.getMeasuredHeight();
        }
        Log.d(TAG, "measure2LineHeight: sTwoLineTextHeight = " + sTwoLineTextHeight);
    }

    private int getPrimaryTextHeightMeasureSpec() {
        if (mTwoLinePrimaryText) {
            int twoLineHeight = mAutomotiveEnabled ? sAutomotiveTwoLineTextHeight
                    : sTwoLineTextHeight;
            return MeasureSpec.makeMeasureSpec(twoLineHeight, MeasureSpec.EXACTLY);
        } else {
            return MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
    }

    /**
     * Enable/disable the two line primary text feature (the max line is 2).
     * The two line primary is supported only if the secondary text is gone.
     * Thus, if the secondary text is visible, enabling the two line primary text would
     * cause the secondary text gone.
     *
     * @param isTwoLine Whether the max line of the primary text is two or not.
     */
    public void setTwoLinePrimaryText(boolean isTwoLine) {
        if (mTwoLinePrimaryText == isTwoLine) {
            return;
        }
        if (isTwoLine) {
            if (mSecondaryText != null && mSecondaryText.getVisibility() != View.GONE) {
                // Since the secondary text is visible, we should hide the secondary text.
                showSecondaryText(false);
                Log.w(TAG, "Only if the secondary text is gone, the primary text is allowed to be two line.");
            }
            if (mAutomotiveEnabled) {
                sAutomotiveEnabled = mAutomotiveEnabled;
            }
            mPrimaryText.setSingleLine(false);
            mPrimaryText.setMaxLines(2);
            determine2LinePrimaryTextHeight();
        } else {
            mPrimaryText.setSingleLine(true);
            mPrimaryText.setMaxLines(1);
        }
        mTwoLinePrimaryText = isTwoLine;
    }

    /**
     * {@inheritDoc}
     */
    public void setAutomotiveMode(boolean enabled) {
        super.setAutomotiveMode(enabled);
        if (sAutomotiveEnabled != enabled) {
            sAutomotiveEnabled = enabled;
            sItemModeChanged = true;
        }
        if (mTwoLinePrimaryText) {
            determine2LinePrimaryTextHeight();
        }
    }

    void applyAutomotiveFontStyle() {
        mPrimaryText.setTextAppearance(getContext(), R.style.fixed_automotive_title_primary_s);
        if (mSecondaryText != null) {
            mSecondaryText.setTextAppearance(getContext(),
                    R.style.fixed_automotive_darklist_secondary_s);
        }
    }

    void applyGenericFontStyle() {
        mPrimaryText.setTextAppearance(getContext(), R.style.list_primary_xxs);
        if (mSecondaryText != null) {
            mSecondaryText.setTextAppearance(getContext(), R.style.list_secondary_xs);
        }
    }

    /**
     * Set the text of the secondary text
     *
     * @param text the text displayed
     */
    public void setSecondaryText(String text) {
        ensureSecondaryTextExist();
        setText(mSecondaryText, text);
        setSecondaryTextVisiblity(View.VISIBLE);
        checkSecondaryTextUsage(mShowSecondaryText);
    }

    /**
     * Set the text of the secondary text
     *
     * @param text
     */
    public void setSecondaryText(CharSequence text) {
        if(text == null) {
            text = "";
        }
        setSecondaryText(text.toString());
    }

    /**
     * Set the text of the secondary text
     *
     * @param rId the resource ID of the text displayed
     */
    public void setSecondaryText(int rId) {
        String text = getContext().getResources().getString(rId);
        setSecondaryText(text);
    }

    /**
     * Show/hide the secondary text
     *
     * @param shown Whether show the secondary text or not.
     */
    public void showSecondaryText(boolean shown) {
        if (mShowSecondaryText == shown) {
            return;
        }
        ensureSecondaryTextExist();
        checkSecondaryTextUsage(shown);
        setSecondaryTextVisiblity(shown ? View.VISIBLE : View.GONE);
    }

    /**
     * Whether the secondary text is currently visible.
     * @return Whether the secondary text is currently visible.
     */
    public boolean isSecondaryTextVisible() {
        return mShowSecondaryText;
    }

    private void setSecondaryTextVisiblity(int visiblity) {
        mSecondaryText.setVisibility(visiblity);
        mShowSecondaryText = visiblity == View.VISIBLE;
    }

    private void checkSecondaryTextUsage(boolean shown) {
        if (mTwoLinePrimaryText && shown) {
            // Since the primary text is two line, we should change the primary text to one line.
            setTwoLinePrimaryText(false);
            Log.w(TAG, "Only if the primary text is single line, the secondary text could be visible.");
        }
    }

    private void ensureSecondaryTextExist() {

        if (mSecondaryText == null) {
            mSecondaryText = new TextView(getContext());
            if (!mAutomotiveEnabled) {
                mSecondaryText
                        .setTextAppearance(getContext(), R.style.list_secondary_xs);
            } else {
                mSecondaryText.setTextAppearance(getContext(),
                        R.style.fixed_automotive_darklist_secondary_s);
            }
            mSecondaryText.setSingleLine();
            mSecondaryText.setEllipsize(TruncateAt.END);
            addView(mSecondaryText);
            mShowSecondaryText = mSecondaryText.getVisibility() == View.VISIBLE;
        }
    }
}
