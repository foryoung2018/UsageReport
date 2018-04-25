package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.LogUtil;

@android.widget.RemoteViews.RemoteView
public class HtcListItemSeparator extends FrameLayout {

    private final static String TAG = "HtcListItemSeparator";

    private TextView mTextView[];

    private static final int TEXT_MAX_NUM = 3;
    /**
     * The field TEXT_LEFT indicate that text label is in left side of Separator
     */
    public static final int TEXT_LEFT = 0;
    /**
     * The field TEXT_MIDDLE indicate that text label is in middle side of
     * Separator
     */
    public static final int TEXT_MIDDLE = 1;
    /**
     * The field TEXT_RIGHT indicate that text label is in right side of
     * Separator
     */
    public static final int TEXT_RIGHT = 2;

    private ImageView mImageView[];
    private static final int ICON_MAX_NUM = 1;
    /**
     * The field ICON_LEFT indicate that text label is in left side of Separator
     */
    public static final int ICON_LEFT = 0;

    private HtcIconButton mIconButton; // Action Button

    private ToggleButton mToggleButtonLight;

    private HtcImageButton mImageButton;

    @ExportedProperty(category = "CommonControl")
    private boolean mButtonFound = false;
    @ExportedProperty(category = "CommonControl")
    private boolean mIsIconButton = false;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsToggleButtonLight = false;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsImageButton = false;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsSeparatorWithPowerBy = false;

    private Drawable mDivider = null;
    @ExportedProperty(category = "CommonControl")
    private boolean bEnableDivider = false;

    /**
     * The text mode MODE_WHITE_LIST is used for default separator.
     */
    public final static int MODE_WHITE_STYLE = 0;
    /**
     * The text mode MODE_DARK_LIST is used for dark separator.
     */
    public final static int MODE_DARK_STYLE = 1;

    /**
     * Do NOT use this text mode for separator.
     */
    public final static int MODE_AUTOMOTIVE_WHITE = 2;
    /**
     * The text mode MODE_AUTOMOTIVE_DARK is used for separator in automotive.
     */
    public final static int MODE_AUTOMOTIVE_DARK = 4;
    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = MODE_WHITE_STYLE, to = "MODE_WHITE_STYLE"),
            @IntToString(from = MODE_DARK_STYLE, to = "MODE_DARK_STYLE"),
            @IntToString(from = MODE_AUTOMOTIVE_WHITE, to = "MODE_AUTOMOTIVE_WHITE"),
            @IntToString(from = MODE_AUTOMOTIVE_DARK, to = "MODE_AUTOMOTIVE_DARK")
    })
    private int mTextMode = MODE_WHITE_STYLE;

    /**
     * This is used for default case
     */
    public static final int ITEMMODE_DEFAULT = 0;
    /**
     * This is used for automotive case
     */
    public static final int ITEMMODE_AUTOMOTIVE = 3;
    /**
     * This is used for popup menu case
     */
    public static final int ITEMMODE_POPUPMENU = 4;

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = ITEMMODE_DEFAULT, to = "ITEMMODE_DEFAULT"),
            @IntToString(from = ITEMMODE_AUTOMOTIVE, to = "ITEMMODE_AUTOMOTIVE"),
            @IntToString(from = ITEMMODE_POPUPMENU, to = "ITEMMODE_POPUPMENU")
    })
    private int mItemMode = ITEMMODE_DEFAULT;

    private static int HEIGHT_DEFAULT = 0;
    private static int HEIGHT_AUTOMOTIVE_DARK = 0;

    private static int M1 = 30;
    private static int M2 = 20;
    private static int M3 = 14;
    private static int M4 = 12;
    private static int M5 = 9;

    @ExportedProperty(category = "CommonControl", resolveId = true)
    private int[] mCustomTextStyle = new int[3];

    private LayerDrawable mLayerDrawable = null;

    private static final int ASSET_COMMON_DIV = 1000;
    private static final int ASSET_COMMON_B_DIV = 1001;
    private static final int ASSET_SECTION_DIVIDER_TOP = 1003;
    private static final int ASSET_SECTION_B_DIVIDER_TOP = 1004;
    private static final int ASSET_SECTION_B_DIVIDER = 1005;

    private static final float VERTICAL_DIVIDER_RATIO = 0.147f;

    private static final float TEXTDATIO_3_LEFT = 0.3f;
    private static final float TEXTDATIO_3_MIDDLE = 0.4f;
    private static final float TEXTDATIO_3_RIGHT = 0.3f;

    private static final float TEXTDATIO_2_LEFT = 0.67f;
    private static final float TEXTDATIO_2_RIGHT = 0.33f;

    @ExportedProperty(category = "CommonControl")
    private int mActionButtonWidth;
    @ExportedProperty(category = "CommonControl")
    private boolean mAllCapsConfirmed = false;
    @ExportedProperty(category = "CommonControl")
    private boolean mAllCaps = true;

    @ExportedProperty(category = "CommonControl")
    private int mTopSpace = 0;
    @ExportedProperty(category = "CommonControl")
    private int mBottomSpace = 0;

    @ExportedProperty(category = "CommonControl")
    private int mSeparatorStartMargin = 0;
    @ExportedProperty(category = "CommonControl")
    private int mSeparatorEndMargin = 0;

    private void init(Context context) {
        initSize(context);
        mTextView = new TextView[TEXT_MAX_NUM];
        mImageView = new ImageView[ICON_MAX_NUM];
        mAllCapsConfirmed = false;
        mAllCaps = true;

        initDrawables(context, null);
        setBackgroundStyle(mTextMode);
    }

    private void init(Context context, int itemMode, int textMode) {
        initSize(context);
        mTextView = new TextView[TEXT_MAX_NUM];
        mImageView = new ImageView[ICON_MAX_NUM];
        mAllCapsConfirmed = false;
        mAllCaps = true;

        // 1, Texts
        // 2, Icons
        // 3, mode
        if (itemMode == ITEMMODE_DEFAULT || itemMode == ITEMMODE_POPUPMENU
                || itemMode == ITEMMODE_AUTOMOTIVE)
            mItemMode = itemMode;
        else
            mItemMode = ITEMMODE_DEFAULT;

        if (textMode == MODE_WHITE_STYLE || textMode == MODE_DARK_STYLE
                || textMode == MODE_AUTOMOTIVE_DARK)
            mTextMode = textMode;
        else
            mTextMode = MODE_WHITE_STYLE;

        initDrawables(context, null);
        setBackgroundStyle(mTextMode);
    }

    private void init(Context context, AttributeSet attrs) {
        initSize(context);
        mTextView = new TextView[TEXT_MAX_NUM];
        mImageView = new ImageView[ICON_MAX_NUM];
        mAllCapsConfirmed = false;
        mAllCaps = true;

        // 1, mode
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HtcListItem);
        mItemMode = a.getInt(R.styleable.HtcListItem_itemMode, ITEMMODE_DEFAULT);
        a = context.obtainStyledAttributes(attrs, R.styleable.HtcListItemTextComponentMode);
        mTextMode = a.getInt(R.styleable.HtcListItemTextComponentMode_textMode, MODE_WHITE_STYLE);
        initDrawables(context, attrs);
        setDivider(mTextMode);
        a.recycle();

        // 2, Icons
        a = context.obtainStyledAttributes(attrs, R.styleable.HtcListItemSeparator);
        int[] iconIds = new int[ICON_MAX_NUM];
        iconIds[0] = a.getResourceId(R.styleable.HtcListItemSeparator_leftIcon, 0);

        for (int i = 0; i < iconIds.length; i++)
            if (iconIds[i] != 0)
                getImageView(i).setImageResource(iconIds[i]);

        // 3, Texts
        a = context.obtainStyledAttributes(attrs, R.styleable.HtcListItemSeparator);
        String[] texts = new String[TEXT_MAX_NUM];
        texts[TEXT_LEFT] = a.getString(R.styleable.HtcListItemSeparator_leftText);
        texts[TEXT_MIDDLE] = a.getString(R.styleable.HtcListItemSeparator_middleText);
        texts[TEXT_RIGHT] = a.getString(R.styleable.HtcListItemSeparator_rightText);

        for (int i = 0; i < texts.length; i++)
            if (!TextUtils.isEmpty(texts[i])) {
                setText(i, texts[i]);
            }

        a.recycle();

        setBackgroundImage(mItemMode, mTextMode);
    }

    private void initDrawables(Context context, AttributeSet attrs) {

        TypedArray ta = context.obtainStyledAttributes(attrs,
                com.htc.lib1.cc.R.styleable.HtcListItemSeparator,
                com.htc.lib1.cc.R.attr.htcListItemSeparatorStyle,
                com.htc.lib1.cc.R.style.HtcListItemSeparatorStyle);

        if (ta != null) {
            mLayerDrawable = (LayerDrawable) ta
                    .getDrawable(com.htc.lib1.cc.R.styleable.HtcListItemSeparator_android_drawable);
            ta.recycle();
        }
    }

    private Drawable getDrawable(int asset) {
        Drawable drawable = null;
        switch (asset) {
            case ASSET_COMMON_DIV:
                if (mLayerDrawable != null)
                    drawable = mLayerDrawable.getDrawable(0);
                else
                    drawable = getContext().getResources().getDrawable(
                            com.htc.lib1.cc.R.drawable.common_list_divider);
                break;
            case ASSET_COMMON_B_DIV:
                if (mLayerDrawable != null)
                    drawable = mLayerDrawable.getDrawable(1);
                else
                    drawable = getContext().getResources().getDrawable(
                            com.htc.lib1.cc.R.drawable.common_b_div);
                break;
            case ASSET_SECTION_DIVIDER_TOP:
                if (mLayerDrawable != null)
                    drawable = mLayerDrawable.getDrawable(2);
                else
                    drawable = getContext().getResources().getDrawable(
                            com.htc.lib1.cc.R.drawable.section_divider_top);
                break;
            case ASSET_SECTION_B_DIVIDER_TOP:
                if (mLayerDrawable != null)
                    drawable = mLayerDrawable.getDrawable(3);
                else
                    drawable = getContext().getResources().getDrawable(
                            com.htc.lib1.cc.R.drawable.section_b_divider_top);
                break;
            case ASSET_SECTION_B_DIVIDER:
                if (mLayerDrawable != null)
                    drawable = mLayerDrawable.getDrawable(4);
                else
                    drawable = getContext().getResources().getDrawable(
                            com.htc.lib1.cc.R.drawable.section_b_divider);
                break;
            default:
                Log.e("HtcListItemSeparator", "fail to getDrawable.");
                drawable = getContext().getResources().getDrawable(
                        com.htc.lib1.cc.R.drawable.common_list_divider);
        }

        return drawable;
    }

    /**
     * use this to new the divider and set the divider's pic
     */
    private void setDivider(int mode) {
        if (mode == MODE_WHITE_STYLE)
            mDivider = getDrawable(ASSET_COMMON_DIV);
        else if (mode == MODE_DARK_STYLE)
            mDivider = getDrawable(ASSET_COMMON_B_DIV);
        else if (mode == MODE_AUTOMOTIVE_DARK)
            mDivider = getDrawable(ASSET_COMMON_B_DIV);
    }

    /**
     * get all the margins & heights.
     *
     * @param context
     */
    private void initSize(Context context) {
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        if (context == null)
            return;
        Resources res = context.getResources();

        HEIGHT_DEFAULT = res
                .getDimensionPixelSize(R.dimen.htc_list_item_separator_with_text_height);
        HEIGHT_AUTOMOTIVE_DARK = res
                .getDimensionPixelSize(R.dimen.htc_list_item_separator_automotive_dark_height);

        mActionButtonWidth = (int) (HtcListItemManager.getPortraitWindowWidth(context) * VERTICAL_DIVIDER_RATIO);

        M1 = HtcListItemManager.getM1(context);
        M2 = HtcListItemManager.getM2(context);
        M3 = HtcListItemManager.getM3(context);
        M4 = HtcListItemManager.getM4(context);
        M5 = HtcListItemManager.getM5(context);

        mSeparatorStartMargin = mSeparatorEndMargin = M1;
    }

    /**
     * Simple constructor to use when creating this widget from code. It will
     * new a HtcListItemSeparator with default style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItemSeparator(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor that is called when inflating this widget from code. It will
     * new a HtcListItemSeparator with specified style, item mode, and text
     * mode.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param itemMode to indicate item mode for HtcListItem.
     * @param textMode to indicate text mode for text.
     */
    public HtcListItemSeparator(Context context, int itemMode, int textMode) {
        super(context);
        init(context, itemMode, textMode);
    }

    /**
     * Constructor that is called when inflating this widget from code. It will
     * new a HtcListItemSeparator with specified style, mode.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param textMode to indicate item mode for HtcListItem.
     */
    public HtcListItemSeparator(Context context, int textMode) {
        super(context);
        init(context, ITEMMODE_DEFAULT, textMode);
    }

    /**
     * Constructor that is called when inflating this widget from XML. This is
     * called when a view is being constructed from an XML file, supplying
     * attributes that were specified in the XML file.It will new a
     * HtcListItemSeparator with default style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemSeparator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of this widget allows subclasses to use their own base style
     * when they are inflating. It will a widget with style defStyle.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @param defStyle The default style to apply to this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemSeparator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void findButton() {
        final int count = getChildCount();
        View v;
        for (int i = count - 1; i >= 0; i--) {
            v = getChildAt(i);
            if (v instanceof HtcIconButton) {
                mIconButton = (HtcIconButton) v;
                mButtonFound = true;
                mIsIconButton = true;
                mIsToggleButtonLight = false;
                setButtonStyle();
                return;
            } else if (v instanceof ToggleButton) {
                mToggleButtonLight = (ToggleButton) v;
                mToggleButtonLight.setFocusable(true);
                mButtonFound = true;
                mIsIconButton = false;
                mIsToggleButtonLight = true;
                return;
            } else if (v instanceof HtcImageButton) {
                mImageButton = (HtcImageButton) v;
                mButtonFound = true;
                mIsIconButton = false;
                mIsToggleButtonLight = false;
                mIsImageButton = true;
                return;
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 1, try to find button first
        if (!mButtonFound)
            findButton();
    }

    void setTopSpace(int height) {
        mTopSpace = height;
        requestLayout();
    }

    void setBottomSpace(int height) {
        mBottomSpace = height;
        requestLayout();
    }

    int getTopSpace() {
        return mTopSpace;
    }

    int getBottomSpace() {
        return mBottomSpace;
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    protected void onMeasure(int wSpec, int hSpec) {
        // 2, set new width & height
        int w = 0;
        switch (MeasureSpec.getMode(wSpec)) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                w = MeasureSpec.getSize(wSpec);
                break;
            case MeasureSpec.UNSPECIFIED:
                // TODO use maxwidth or screen width
                break;
            default:
                break;
        }

        int height = 0;

        if (mTextMode != MODE_AUTOMOTIVE_DARK) {
            height = HEIGHT_DEFAULT;
        } else {
            // fixed height in automotive_dark mode
            height = HEIGHT_AUTOMOTIVE_DARK;
        }

        height += mTopSpace + mBottomSpace;

        // measure text
        if (mButtonFound) {
            if (mIsToggleButtonLight) {
                if (mToggleButtonLight != null) {
                    mToggleButtonLight.measure(
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

                    if (mTextView[TEXT_LEFT] != null) {
                        // int widthLeft = w -
                        // mToggleButtonLight.getMeasuredWidth() - M2;
                        int widthLeft = w - mToggleButtonLight.getMeasuredWidth()
                                - mSeparatorStartMargin;
                        if (widthLeft < 0) {
                            LogUtil.logE(TAG,
                                    "w - mToggleButtonLight.getMeasuredWidth() - mSeparatorStartMargin < 0 :",
                                    " w = ", w,
                                    ", mToggleButtonLight.getMeasuredWidth() = ", mToggleButtonLight.getMeasuredWidth(),
                                    ", mSeparatorStartMargin = ", mSeparatorStartMargin);
                            widthLeft = 0;
                        }
                        mTextView[TEXT_LEFT].measure(
                                MeasureSpec.makeMeasureSpec((widthLeft), MeasureSpec.EXACTLY),
                                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                    }
                }

            } else if (mIsIconButton) {
                if (mIconButton != null) {
                    mIconButton.measure(
                            MeasureSpec.makeMeasureSpec(mActionButtonWidth, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                    bEnableDivider = true;
                }

                if (mTextView[TEXT_LEFT] != null) {

                    int widthLeft = (int) ((w - mActionButtonWidth) - mSeparatorStartMargin - M2);
                    if (widthLeft < 0) {
                        LogUtil.logE(TAG,
                                "w - mActionButtonWidth - mSeparatorStartMargin - M2 < 0 :",
                                " w = ", w,
                                ", mActionButtonWidth = ", mActionButtonWidth,
                                ", mSeparatorStartMargin = ", mSeparatorStartMargin,
                                ", M2 = ", M2);
                        widthLeft = 0;
                    }
                    mTextView[TEXT_LEFT].measure(
                            MeasureSpec.makeMeasureSpec((widthLeft), MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                }
            } else if (mIsImageButton) {
                if (mImageButton != null) {
                    mImageButton.measure(
                            MeasureSpec.makeMeasureSpec(mActionButtonWidth, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                    bEnableDivider = true;
                }

                if (mTextView[TEXT_LEFT] != null) {

                    int widthLeft = (int) ((w - mActionButtonWidth) - mSeparatorStartMargin - M2);
                    if (widthLeft < 0) {
                        LogUtil.logE(TAG,
                                "w - mActionButtonWidth - mSeparatorStartMargin - M2 < 0 :",
                                " w = ", w,
                                ", mActionButtonWidth = ", mActionButtonWidth,
                                ", mSeparatorStartMargin = ", mSeparatorStartMargin,
                                ", M2 = ", M2);
                        widthLeft = 0;
                    }
                    mTextView[TEXT_LEFT].measure(
                            MeasureSpec.makeMeasureSpec((widthLeft), MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                }
            }
        } else {
            if (mIsSeparatorWithPowerBy) {
                int widthMarginLeft = mSeparatorStartMargin + M5;

                if (mImageView[ICON_LEFT] != null
                        && mImageView[ICON_LEFT].getVisibility() != View.GONE) {
                    int widthIconLeft = (mImageView[ICON_LEFT] != null) ? mImageView[ICON_LEFT]
                            .getDrawable().getIntrinsicWidth() : 0;
                    widthMarginLeft += widthIconLeft;

                    mImageView[ICON_LEFT].measure(
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                }

                if (mTextView[TEXT_LEFT] != null) {
                    int widthLeft = (int) (w - widthMarginLeft - mSeparatorEndMargin);
                    if (widthLeft < 0) {
                        LogUtil.logE(TAG,
                                "w - widthMarginLeft - mSeparatorEndMargin < 0 :",
                                " w = ", w,
                                ", widthMarginLeft = ", widthMarginLeft,
                                ", mSeparatorEndMargin = ", mSeparatorEndMargin);
                        widthLeft = 0;
                    }
                    mTextView[TEXT_LEFT].measure(
                            MeasureSpec.makeMeasureSpec((widthLeft), MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                }
            } else {
                int widthMarginLeft = mSeparatorStartMargin;

                if (mImageView[ICON_LEFT] != null
                        && mImageView[ICON_LEFT].getVisibility() != View.GONE) {
                    int widthIconLeft = (mImageView[ICON_LEFT] != null) ? mImageView[ICON_LEFT]
                            .getDrawable().getIntrinsicWidth() : 0;
                    if ((widthIconLeft != 0))

                        widthMarginLeft = widthMarginLeft + widthIconLeft + M3;

                    mImageView[ICON_LEFT].measure(
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                }

                if (mTextView[TEXT_LEFT] != null && mTextView[TEXT_MIDDLE] != null
                        && mTextView[TEXT_RIGHT] != null) {
                    int totalWidth = w - 2 * M2 - mSeparatorStartMargin - mSeparatorEndMargin;
                    if (totalWidth < 0) {
                        LogUtil.logE(TAG,
                                "w - 2 * M2 - mSeparatorStartMargin - mSeparatorEndMargin < 0 :",
                                " w = ", w,
                                ", M2 = ", M2,
                                ", mSeparatorStartMargin = ", mSeparatorStartMargin,
                                ", mSeparatorEndMargin = ", mSeparatorEndMargin);
                        totalWidth = 0;
                    }
                    int widthLeft = (int) (totalWidth * TEXTDATIO_3_LEFT);
                    int widthMiddle = (int) (totalWidth * TEXTDATIO_3_MIDDLE);
                    int widthRight = (int) (totalWidth * TEXTDATIO_3_RIGHT);
                    mTextView[TEXT_LEFT].measure(
                            MeasureSpec.makeMeasureSpec((widthLeft), MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                    mTextView[TEXT_MIDDLE].measure(
                            MeasureSpec.makeMeasureSpec((widthMiddle), MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                    mTextView[TEXT_RIGHT].measure(
                            MeasureSpec.makeMeasureSpec((widthRight), MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

                } else if (mTextView[TEXT_LEFT] != null && mTextView[TEXT_RIGHT] != null) {
                    int totalWidth = w - M2 - mSeparatorStartMargin - mSeparatorEndMargin;
                    if (totalWidth < 0) {
                        LogUtil.logE(TAG,
                                "w - M2 - mSeparatorStartMargin - mSeparatorEndMargin < 0 :",
                                " w = ", w,
                                ", M2 = ", M2,
                                ", mSeparatorStartMargin = ", mSeparatorStartMargin,
                                ", mSeparatorEndMargin = ", mSeparatorEndMargin);
                        totalWidth = 0;
                    }
                    int widthLeft = (int) (totalWidth * TEXTDATIO_2_LEFT);
                    int widthRight = (int) (totalWidth * TEXTDATIO_2_RIGHT);
                    mTextView[TEXT_LEFT].measure(
                            MeasureSpec.makeMeasureSpec((widthLeft), MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                    mTextView[TEXT_RIGHT].measure(
                            MeasureSpec.makeMeasureSpec((widthRight), MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

                } else if (mTextView[TEXT_LEFT] != null) {
                    int widthLeft = w - widthMarginLeft - mSeparatorEndMargin;
                    if (widthLeft < 0) {
                        LogUtil.logE(TAG,
                                "w - widthMarginLeft - mSeparatorEndMargin < 0 :",
                                " w = ", w,
                                ", widthMarginLeft = ", widthMarginLeft,
                                ", mSeparatorEndMargin = ", mSeparatorEndMargin);
                        widthLeft = 0;
                    }
                    mTextView[TEXT_LEFT].measure(
                            MeasureSpec.makeMeasureSpec((widthLeft), MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                } else if (mTextView[TEXT_RIGHT] != null) {
                    int widthRight = w - mSeparatorStartMargin - mSeparatorEndMargin;
                    if (widthRight < 0) {
                        LogUtil.logE(TAG,
                                "w - mSeparatorStartMargin - mSeparatorEndMargin < 0 :",
                                " w = ", w,
                                ", mSeparatorStartMargin = ", mSeparatorStartMargin,
                                ", mSeparatorEndMargin = ", mSeparatorEndMargin);
                        widthRight = 0;
                    }
                    mTextView[TEXT_RIGHT].measure(
                            MeasureSpec.makeMeasureSpec((widthRight), MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                }
            }
        }

        setMeasuredDimension(w, height);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final boolean isLayoutRtl = (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) ? getLayoutDirection() == View.LAYOUT_DIRECTION_RTL : false;
        final int width = r - l;
        final int height = b - t;
        int childLeft = 0;
        if (mButtonFound) {
            // Layout button
            if (mIsIconButton && mIconButton != null && mIconButton.getVisibility() != View.GONE) {
                int buttonWidth = mIconButton.getMeasuredWidth();
                int buttonHeight = mIconButton.getMeasuredHeight();

                childLeft = isLayoutRtl ? 0 : (width - buttonWidth);
                mIconButton.layout(
                        childLeft,
                        (height - buttonHeight) / 2,
                        childLeft + buttonWidth,
                        (height + buttonHeight) / 2);

            } else if (mIsImageButton && mImageButton != null
                    && mImageButton.getVisibility() != View.GONE) {
                int buttonWidth = mImageButton.getMeasuredWidth();
                int buttonHeight = mImageButton.getMeasuredHeight();

                childLeft = isLayoutRtl ? 0 : (width - buttonWidth);
                mImageButton.layout(
                        childLeft,
                        (height - buttonHeight) / 2,
                        childLeft + buttonWidth,
                        (height + buttonHeight) / 2);

            } else if (mIsToggleButtonLight && mToggleButtonLight != null
                    && mToggleButtonLight.getVisibility() != View.GONE) {
                int buttonWidth = mToggleButtonLight.getMeasuredWidth();
                int buttonHeight = mToggleButtonLight.getMeasuredHeight();

                childLeft = isLayoutRtl ? 0 : (width - buttonWidth);
                mToggleButtonLight.layout(
                        childLeft,
                        (height - buttonHeight) / 2,
                        childLeft + buttonWidth,
                        (height + buttonHeight) / 2);

            }
            // layout text
            if (mTextView[TEXT_LEFT] != null) {
                int textWidth = mTextView[TEXT_LEFT].getMeasuredWidth();
                int textHeight = mTextView[TEXT_LEFT].getMeasuredHeight();

                childLeft = isLayoutRtl ? (width - mSeparatorStartMargin -textWidth) : mSeparatorStartMargin;
                mTextView[TEXT_LEFT].layout(childLeft,
                        (height - textHeight) / 2,
                        childLeft + textWidth,
                        (height + textHeight) / 2);
            }
        } else {
            if (mIsSeparatorWithPowerBy) {
                // Separator with Power By
                int iconWidth = 0;
                int iconHeight = 0;
                if (mImageView[ICON_LEFT] != null
                        && mImageView[ICON_LEFT].getVisibility() != View.GONE) {
                    iconWidth = mImageView[ICON_LEFT].getMeasuredWidth();
                    iconHeight = mImageView[ICON_LEFT].getMeasuredHeight();
                    if (mTextView[TEXT_LEFT] != null) {
                        childLeft = isLayoutRtl ? (width - mSeparatorStartMargin -iconWidth) : mSeparatorStartMargin;
                        mImageView[ICON_LEFT].layout(
                                childLeft,
                                (height - iconHeight) / 2,
                                childLeft + iconWidth,
                                (height + iconHeight) / 2);

                        int textLeftWidth = mTextView[TEXT_LEFT].getMeasuredWidth();
                        int textLeftHeight = mTextView[TEXT_LEFT].getMeasuredHeight();
                        int widthMarginLeft = mSeparatorStartMargin + iconWidth + M5;

                        childLeft = isLayoutRtl ? (width - widthMarginLeft - textLeftWidth)
                                : widthMarginLeft;
                        mTextView[TEXT_LEFT]
                                .layout(childLeft,
                                        (height - textLeftHeight) / 2,
                                        childLeft + textLeftWidth,
                                        (height + textLeftHeight) / 2);
                    } else {
                        mImageView[ICON_LEFT].layout((width - iconWidth) / 2,
                                (height - iconHeight) / 2,
                                (width + iconWidth) / 2,
                                (height + iconHeight) / 2);
                    }
                }
            } else {
                int iconWidth = 0;
                int iconHeight = 0;
                if (mImageView[ICON_LEFT] != null
                        && mImageView[ICON_LEFT].getVisibility() != View.GONE) {
                    iconWidth = mImageView[ICON_LEFT].getMeasuredWidth();
                    iconHeight = mImageView[ICON_LEFT].getMeasuredHeight();
                    childLeft = isLayoutRtl ? (width - mSeparatorStartMargin -iconWidth) : mSeparatorStartMargin;
                    mImageView[ICON_LEFT].layout(childLeft,
                            (height - iconHeight) / 2, childLeft
                                    + iconWidth, (height + iconHeight) / 2);
                }

                if (mTextView[TEXT_LEFT] != null) {
                    int textLeftWidth = mTextView[TEXT_LEFT].getMeasuredWidth();
                    int textLeftHeight = mTextView[TEXT_LEFT].getMeasuredHeight();
                    int widthMarginLeft = (iconWidth == 0) ? mSeparatorStartMargin : iconWidth + M3
                            + mSeparatorStartMargin;

                    childLeft = isLayoutRtl ? (width - widthMarginLeft -textLeftWidth) : widthMarginLeft;
                    mTextView[TEXT_LEFT].layout(childLeft,
                            (height + mTopSpace - mBottomSpace - textLeftHeight) / 2,
                            childLeft + textLeftWidth,
                            (height + mTopSpace - mBottomSpace + textLeftHeight) / 2);
                }

                if (mTextView[TEXT_MIDDLE] != null) {
                    int textMiddleWidth = mTextView[TEXT_MIDDLE].getMeasuredWidth();
                    int textMiddleHeight = mTextView[TEXT_MIDDLE].getMeasuredHeight();

                    mTextView[TEXT_MIDDLE].layout((width - textMiddleWidth) / 2,
                            (height - textMiddleHeight) / 2,
                            (width - textMiddleWidth) / 2 + textMiddleWidth,
                            (height + textMiddleHeight) / 2);
                }

                if (mTextView[TEXT_RIGHT] != null) {
                    int textRightWidth = mTextView[TEXT_RIGHT].getMeasuredWidth();
                    int textRightHeight = mTextView[TEXT_RIGHT].getMeasuredHeight();

                    childLeft = isLayoutRtl ?
                            mSeparatorEndMargin : (width - mSeparatorEndMargin - textRightWidth);
                    mTextView[TEXT_RIGHT].layout(childLeft,
                            (height + mTopSpace - mBottomSpace - textRightHeight) / 2,
                            childLeft + textRightWidth,
                            (height + mTopSpace - mBottomSpace + textRightHeight) / 2);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (bEnableDivider && mDivider != null) {
            mDivider.setBounds(
                    (int) (getWidth() - mActionButtonWidth)
                            - HtcListItemManager.getVerticalDividerWidth(getContext()), M5,
                    (int) (getWidth() - mActionButtonWidth), getHeight() - M5);
            mDivider.draw(canvas);
        }
    }

    /**
     * Use this API to set text to left, middle, or right of Separator
     *
     * @param whichText one of TEXT_LEFT, TEXT_MIDDLE, TEXT_RIGHT
     * @param text CharSequence
     */
    public void setText(int whichText, CharSequence text) {
        switch (whichText) {
            case TEXT_LEFT:
            case TEXT_MIDDLE:
            case TEXT_RIGHT:
                if (!mAllCapsConfirmed) {
                    mAllCaps = com.htc.lib1.cc.util.res.HtcResUtil.isInAllCapsLocale(getContext());
                    mAllCapsConfirmed = true;
                }
                if (text == null)
                    text = "";
                getTextView(whichText).setText(
                        mAllCaps ? text.toString().toUpperCase() : text.toString());
        }
    }

    /**
     * Use this API to set text to left, middle, or right of Separator
     *
     * @param whichText one of TEXT_LEFT, TEXT_MIDDLE, TEXT_RIGHT
     * @param resId text resource id
     */
    public void setText(int whichText, int resId) {
        switch (whichText) {
            case TEXT_LEFT:
            case TEXT_MIDDLE:
            case TEXT_RIGHT:
                setText(whichText, getContext().getResources().getText(resId));

        }
    }

    /**
     * Use this API to set text to left, middle, or right of Separator
     *
     * @param bundle a bundle with 2 objects, Int with key "whichText" and
     *            CharSequence with key "text" or a bundle with 2 objects, Int
     *            with key "whichText" and Int with key "resId".
     * @deprecated [Not use any longer]
     */
    // @android.view.RemotableViewMethod
    /** @hide */
    public void setText(Bundle bundle) {
        int whichText = bundle.getInt("whichText", TEXT_LEFT);
        CharSequence text = bundle.getCharSequence("text", "");
        int resId = bundle.getInt("resId", 0);

        if (resId != 0)
            setText(whichText, resId);
        else
            setText(whichText, text);
    }

    private TextView getTextView(int whichText) {
        if (mTextView[whichText] == null) {
            mTextView[whichText] = new TextView(getContext());
            mTextView[whichText].setSingleLine();
            mTextView[whichText].setEllipsize(TruncateAt.END);

            addView(mTextView[whichText], new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));

            setBackgroundImage(mItemMode, mTextMode);
            setTextAppearance(mTextMode, whichText);
        }
        return mTextView[whichText];
    }

    /**
     * Use this API to set icon to left of Separator
     *
     * @param whichIcon one of ICON_LEFT
     * @param d Drawable
     */
    public void setIcon(int whichIcon, Drawable d) {
        switch (whichIcon) {
            case ICON_LEFT:
                getImageView(whichIcon).setImageDrawable(d);
        }
    }

    /**
     * Use this API to set icon to left of Separator
     *
     * @param whichIcon one of ICON_LEFT
     * @param resId resource id
     */
    public void setIcon(int whichIcon, int resId) {
        switch (whichIcon) {
            case ICON_LEFT:
                getImageView(whichIcon).setImageResource(resId);
        }
    }

    private ImageView getImageView(int whichIcon) {
        if (mImageView[whichIcon] == null) {
            mImageView[whichIcon] = new ImageView(getContext());
            addView(mImageView[whichIcon], new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT));
            setBackgroundImage(mItemMode, mTextMode);
        }
        return mImageView[whichIcon];
    }

    /**
     * set to White or Dark, text font of texts & IconButton will be changed
     * too.
     *
     * @param style one of</br> HtcListItemSeparator.MODE_WHITE_STYLE</br>
     *            or</br> HtcListItemSeparator.MODE_DARK_STYLE
     */
    public void setBackgroundStyle(int style) {
        if (style != MODE_WHITE_STYLE && style != MODE_DARK_STYLE && style != MODE_AUTOMOTIVE_DARK)
            return;
        mTextMode = style;
        setBackgroundImage(mItemMode, mTextMode);
        setDivider(mTextMode);

        for (int i = 0; i < TEXT_MAX_NUM; i++) {
            setTextAppearance(mTextMode, i);
        }

    }

    private void setButtonStyle() {
        if (mTextMode == MODE_WHITE_STYLE) {
            if (mIconButton != null && mIconButton.getVisibility() == View.VISIBLE)
                mIconButton.setTextAppearance(getContext(), R.style.fixed_separator_primary_m);
        } else if (mTextMode == MODE_DARK_STYLE) {
            if (mIconButton != null && mIconButton.getVisibility() == View.VISIBLE)
                mIconButton.setTextAppearance(getContext(), R.style.fixed_b_separator_primary_m);
        } else if (mTextMode == MODE_AUTOMOTIVE_DARK) {
            if (mIconButton != null && mIconButton.getVisibility() == View.VISIBLE)
                mIconButton.setTextAppearance(getContext(), R.style.automotive_b_separator_primary_m);
        }
    }

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = ASSET_COMMON_B_DIV, to = "ASSET_COMMON_B_DIV"),
            @IntToString(from = ASSET_COMMON_DIV, to = "ASSET_COMMON_DIV"),
            @IntToString(from = ASSET_SECTION_B_DIVIDER, to = "ASSET_SECTION_B_DIVIDER"),
            @IntToString(from = ASSET_SECTION_B_DIVIDER_TOP, to = "ASSET_SECTION_B_DIVIDER_TOP"),
            @IntToString(from = ASSET_SECTION_DIVIDER_TOP, to = "ASSET_SECTION_DIVIDER_TOP")
    })
    private int mCurrentBackground = 0;

    /**
     * set the background when there is at least 1 child visible
     */
    private void setBackgroundImage(int itemMode, int textMode) {
        if (itemMode == ITEMMODE_DEFAULT) {
            if (textMode == MODE_WHITE_STYLE) {
                if (mCurrentBackground != ASSET_SECTION_DIVIDER_TOP) {
                    setBackground(getDrawable(ASSET_SECTION_DIVIDER_TOP));
                    mCurrentBackground = ASSET_SECTION_DIVIDER_TOP;
                }
            } else if (textMode == MODE_DARK_STYLE) {
                if (mCurrentBackground != ASSET_SECTION_B_DIVIDER_TOP) {
                    setBackground(getDrawable(ASSET_SECTION_B_DIVIDER_TOP));
                    mCurrentBackground = ASSET_SECTION_B_DIVIDER_TOP;
                }
            } else if (textMode == MODE_AUTOMOTIVE_DARK) {
                if (mCurrentBackground != ASSET_SECTION_B_DIVIDER_TOP) {
                    setBackground(getDrawable(ASSET_SECTION_B_DIVIDER_TOP));
                    mCurrentBackground = ASSET_SECTION_B_DIVIDER_TOP;
                }
            } else {
                Log.e("HtcListItemSeparator", "setBackgroundImage: unknown text mode: " + textMode);
            }
        } else if (itemMode == ITEMMODE_POPUPMENU) {
            if (mCurrentBackground != ASSET_SECTION_B_DIVIDER) {
                setBackground(getDrawable(ASSET_SECTION_B_DIVIDER));
                mCurrentBackground = ASSET_SECTION_B_DIVIDER;
            }
        } else if (itemMode == ITEMMODE_AUTOMOTIVE) {
            if (mCurrentBackground != ASSET_SECTION_B_DIVIDER_TOP) {
                setBackground(getDrawable(ASSET_SECTION_B_DIVIDER_TOP));
                mCurrentBackground = ASSET_SECTION_B_DIVIDER_TOP;
            }
        } else {
            Log.e("HtcListItemSeparator", "setBackgroundImage: unknown item mode: " + itemMode);
        }
    }

    /**
     * change the text font of textviews & iconbutton.
     */
    // rule 1: if there are 3 textviews, the center 1 is primary_m
    // otherwise, all textviews are primary_m
    // rule 2: for seprtor with ToggleBtn, use ... (wait for Vance)
    private void setTextAppearance(int style, int whichText) {
        if (whichText != TEXT_LEFT && whichText != TEXT_MIDDLE && whichText != TEXT_RIGHT)
            return;

        if (style == MODE_WHITE_STYLE) {
            if (mTextView[whichText] != null)
                mTextView[whichText].setTextAppearance(getContext(), R.style.fixed_separator_primary_m);
        } else if (style == MODE_DARK_STYLE) {
            if (mTextView[whichText] != null)
                mTextView[whichText].setTextAppearance(getContext(),
                        R.style.fixed_b_separator_primary_m);
        } else if (style == MODE_AUTOMOTIVE_DARK) {
            if (mTextView[whichText] != null)
                mTextView[whichText].setTextAppearance(getContext(),
                        R.style.fixed_automotive_b_separator_primary_m);
        }

    }

    /**
     * this method is added to set the text's style. because the height of
     * separator will be changed along with the text style
     *
     * @param index TEXT_LEFT, TEXT_MIDDLE, or TEXT_RIGHT.
     * @param style text style
     */
    public void setTextStyle(int index, int style) {
        if (index >= 0 && index < TEXT_MAX_NUM) {
            mCustomTextStyle[index] = style;
            if (mCustomTextStyle[index] != 0 && mTextView[index] != null)
                mTextView[index].setTextAppearance(getContext(), mCustomTextStyle[index]);
        }
    }

    private void removeButton() {
        final int count = getChildCount();
        View v;
        for (int i = count - 1; i >= 0; i--) {
            v = getChildAt(i);
            if ((v instanceof HtcIconButton) || (v instanceof ToggleButton)
                    || (v instanceof HtcImageButton)/* benny] */) {
                removeView(v);
            }
        }
    }

    /**
     * Use this API to set HtcIconButton to Separator
     *
     * @param iconButton
     */
    public void setIconButton(HtcIconButton iconButton) {
        if (iconButton != null) {
            addView(iconButton, new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT));
            mIconButton = iconButton;
            mButtonFound = true;
            mIsIconButton = true;
            mIsToggleButtonLight = false;

            if (mTextMode == MODE_WHITE_STYLE) {
                mIconButton.setTextAppearance(getContext(), R.style.fixed_separator_primary_m);
            } else if (mTextMode == MODE_DARK_STYLE) {
                mIconButton.setTextAppearance(getContext(), R.style.fixed_b_separator_primary_m);
            } else if (mTextMode == MODE_AUTOMOTIVE_DARK) {
                mIconButton.setTextAppearance(getContext(), R.style.automotive_b_separator_primary_m);
            }
        }
    }

    /**
     * Use this API to set HtcImageButton to Separator
     *
     * @param imageButton
     */
    public void setImageButton(HtcImageButton imageButton) {
        if (imageButton != null) {
            addView(imageButton, new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT));
            mImageButton = imageButton;
            mButtonFound = true;
            mIsIconButton = false;
            mIsImageButton = true;
            mIsToggleButtonLight = false;

        }
    }

    /**
     * Hide setToggleButton(HtcToggleButtonLight) because
     * setToggleButton(HtcToggleButton) has been hided by SDK team
     *
     * @hide
     */
    public void setToggleButton(ToggleButton toggleButtonLight) {
        if (toggleButtonLight != null) {
            addView(toggleButtonLight, new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT));
            mToggleButtonLight = toggleButtonLight;
            mButtonFound = true;
            mIsIconButton = false;
            mIsToggleButtonLight = true;

        }
    }

    /**
     * Set Separator with Bower By icon & text
     */
    public void setSeparatorWithPowerBy() {
        if (!mIsSeparatorWithPowerBy) {
            mIsSeparatorWithPowerBy = true;
            requestLayout();
        }
    }
}
