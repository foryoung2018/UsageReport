
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;

import com.htc.lib1.cc.util.LogUtil;

/**
 * HtcOverlayGridItem is designed as a single grid item for HtcGridView.
 * To follow UI guideline, please set the layout width and layout height to WRAP_CONTENT or MATCH_PARENT,
 * and specify the mode MODE_OVERLAY on HtcGridView (HtcGridView.setMode(HtcGridView.MODE_OVERLAY)).
 * In general, the item width is determined by HtcGridView (according the number of columns, stretch mode and gaps),
 * and the value of image height always equals the value of image (item) width by default.
 *
 * HtcGridItemOverlay provides the below features:
 *     1. An 1-line primary text within a overlay block overlaps a image.
 *     2. An indicator shown above the overlay block, and align the right side of the image.
 */
public class HtcOverlayGridItem extends HtcGridItemBase {

    private static final String TAG = "HtcOverlayGridItem";

    @ExportedProperty(category = "CommonControl")
    private int mTextHeight = 0;

    @ExportedProperty(category = "CommonControl")
    private int mPrimaryTextHorizontalMargin = 0;

    @ExportedProperty(category = "CommonControl")
    private int mIndicatorMarginBottom = 0;

    private Rect mOverlayBounds = new Rect();

    /**
     * Simple constructor to use when creating this widget from code. It will
     * new a view with default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcOverlayGridItem(Context context) {
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
    public HtcOverlayGridItem(Context context, AttributeSet attrs) {
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
    public HtcOverlayGridItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        Resources res = getContext().getResources();
        mPrimaryText.setSingleLine();
        DisplayMetrics dm = res.getDisplayMetrics();
        int total_width = (dm.widthPixels < dm.heightPixels) ? dm.widthPixels : dm.heightPixels;
        mTextHeight = (int) (total_width * 0.083);
        setupMargins(res);
        mImageView.setDrawOverlay(true);
    }

    private void setupMargins(Resources res) {
        mIndicatorMarginBottom = getResources().getDimensionPixelOffset(com.htc.lib1.cc.R.dimen.spacing);
        mPrimaryTextHorizontalMargin = res.getDimensionPixelOffset(com.htc.lib1.cc.R.dimen.margin_m);
    }

    /**
     * {@inheritDoc}
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = mItemWidth - mPrimaryTextHorizontalMargin * 2;
        if (width < 0) {
            LogUtil.logE(TAG,
                    "mItemWidth - mPrimaryTextHorizontalMargin * 2 < 0 :",
                    " mItemWidth = ", mItemWidth,
                    ", mPrimaryTextHorizontalMargin = ", mPrimaryTextHorizontalMargin);
            width = 0;
        }
        int primaryTextWidthMS = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int primaryTextHeightMS = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        mPrimaryText.measure(primaryTextWidthMS, primaryTextHeightMS);

        setMeasuredDimension(mItemWidth, mImageView.getMeasuredHeight());
    }

    /**
     * {@inheritDoc}
     */
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // Layout the primary text
        int imageHeight = mImageView.getHeight();
        int textMeasuredHeight = mPrimaryText.getMeasuredHeight();
        int textTop = imageHeight - mTextHeight + (mTextHeight - textMeasuredHeight)/2;
        mPrimaryText.layout(mPrimaryTextHorizontalMargin, textTop,
                mPrimaryTextHorizontalMargin + mPrimaryText.getMeasuredWidth(), textTop + textMeasuredHeight);

        mOverlayBounds.set(0, imageHeight - mTextHeight, mItemWidth, mImageView.getHeight() + 1);
        mImageView.setOverlayBounds(mOverlayBounds);
    }

    void applyGenericFontStyle() {
        mPrimaryText.setTextAppearance(getContext(), com.htc.lib1.cc.R.style.fixed_darklist_primary_xxs);
    }

    void applyAutomotiveFontStyle() {
        applyGenericFontStyle();
    }

    @ExportedProperty(category = "CommonControl")
    int getIndicatorMarginBottom() {
        return mTextHeight + mIndicatorMarginBottom;
    }
}
