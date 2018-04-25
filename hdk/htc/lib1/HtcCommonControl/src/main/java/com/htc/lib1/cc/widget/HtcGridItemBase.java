
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.widget.HtcIconButton;

abstract class HtcGridItemBase extends ViewGroup {

    private final static String TAG = "HtcGridItemBase";

    // Defined in HtcListItem_itemMode enumeration
    final static int DEFAULT_MODE = 0;

    final static int AUTOMOTIVE_MODE = 3;

    final static float DELETED_ITEM_OPACITY = 0.7f;

    HtcGridItemOverlayImage mImageView;

    TextView mPrimaryText;

    int mMode;

    @ExportedProperty(category = "CommonControl")
    int mItemWidth = 0;

    @ExportedProperty(category = "CommonControl")
    int mIndicatorMarginRight = 0;

    @ExportedProperty(category = "CommonControl")
    int mIndicatorMarginBottom = 0;

    @ExportedProperty(category = "CommonControl")
    int mCustomImageHeight = ViewGroup.LayoutParams.MATCH_PARENT;

    @ExportedProperty(category = "CommonControl")
    protected boolean mAutomotiveEnabled = false;

    @ExportedProperty(category = "CommonControl")
    boolean mItemDeleted = false;

    @ExportedProperty(category = "CommonControl", resolveId = true)
    private int mIndicatorResource = 0;

    @ExportedProperty(category = "CommonControl")
    private int mDeletedIconWitdh = 0;

    @ExportedProperty(category = "CommonControl")
    private int mDeletedIconHeight = 0;

    private Drawable mIndicator;

    private Drawable mDeleteIcon;

    private Rect mDeletedIconBounds;

    @ExportedProperty(category = "CommonControl")
    private int mDeletedOverlayColor = 0xb3000000;

    private ColorDrawable mDeletedOverlay;

    /**
     * Simple constructor to use when creating this widget from code. It will
     * new a view with default text style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     */
    public HtcGridItemBase(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating this widget from XML. This is
     * called when a view is being constructed from an XML file, supplying
     * attributes that were specified in the XML file.It will new a view with
     * default text style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs The attributes of the XML tag that is inflating this widget.
     */
    public HtcGridItemBase(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of this widget allows subclasses to use their own base style
     * when they are inflating. It will new a view with default text style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @param defStyle The default style to apply to this widget.
     */
    public HtcGridItemBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HtcListItem);
        int mode = a.getInt(R.styleable.HtcListItem_itemMode, DEFAULT_MODE);
        mAutomotiveEnabled = (mode == AUTOMOTIVE_MODE);
        a.recycle();
        init();
    }

    private void init() {
        mImageView = new HtcGridItemOverlayImage(getContext());
        mImageView.setScaleType(ScaleType.FIT_XY);
        addView(mImageView);

        mPrimaryText = new TextView(getContext());
        mPrimaryText.setEllipsize(TruncateAt.END);
        addView(mPrimaryText);

        if (mAutomotiveEnabled) {
            applyAutomotiveFontStyle();
        } else {
            applyGenericFontStyle();
        }
        mIndicatorMarginRight = mIndicatorMarginBottom = getResources().getDimensionPixelOffset(
                R.dimen.spacing);
    }

    /**
     * Get the image view of the grid item.
     *
     * @return The instance of the image view
     */
    public ImageView getImage() {
        return mImageView;
    }

    /**
     * set the text of the primary text
     *
     * @param text the text displayed
     */
    public void setPrimaryText(String text) {
        setText(mPrimaryText, text);
    }

    /**
     * set the text of the primary text
     *
     * @param rId the resource ID of the text displayed
     */
    public void setPrimaryText(int rId) {
        String text = getContext().getResources().getString(rId);
        setText(mPrimaryText, text);
    }

    /**
     * set the text of the primary text
     *
     * @param text
     */
    public void setPrimaryText(CharSequence text) {
        setText(mPrimaryText, text);
    }

    void setText(TextView textView, CharSequence text) {
        textView.setText(text);
    }

    int determineImageHeight(int imageWidth) {
        if (mCustomImageHeight > 0) {
            return mCustomImageHeight;
        } else if (mCustomImageHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
            return imageWidth;
        } else {
            return 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int imageSize = MeasureSpec.getSize(widthMeasureSpec);

        int imageWidthMeasureSpec = MeasureSpec.makeMeasureSpec(imageSize, MeasureSpec.EXACTLY);
        int imageHeight = determineImageHeight(imageSize);
        int imageHeightMeasureSpec = (mCustomImageHeight > 0 || mCustomImageHeight == ViewGroup.LayoutParams.MATCH_PARENT) ? MeasureSpec
                .makeMeasureSpec(imageHeight, MeasureSpec.EXACTLY) : MeasureSpec.makeMeasureSpec(
                imageHeight, MeasureSpec.UNSPECIFIED);
        mImageView.measure(imageWidthMeasureSpec, imageHeightMeasureSpec);
        // The grid item width equals the image width
        mItemWidth = mImageView.getMeasuredWidth();
        // NOTE: The derived class should set measure dimension
    }

    /**
     * {@inheritDoc}
     */
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mImageView != null) {
            int width = right - left;
            int imageLeft = (width - mImageView.getMeasuredWidth()) / 2;
            mImageView.layout(imageLeft, 0, imageLeft + mImageView.getMeasuredWidth(),
                    mImageView.getMeasuredHeight());
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mIndicator != null) {
            int indicatorRight = mImageView.getWidth() - mIndicatorMarginRight;
            int indicatorBottom = mImageView.getHeight() - getIndicatorMarginBottom();
            mIndicator.setBounds(indicatorRight - mIndicator.getIntrinsicWidth(), indicatorBottom
                    - mIndicator.getIntrinsicHeight(), indicatorRight, indicatorBottom);
            mIndicator.draw(canvas);
        }



        if(mItemDeleted) {
            //  The deleted overlay should be drawn before the deleted icon.
            if(mDeletedOverlay!=null) {
                mDeletedOverlay.setBounds(0, 0, getWidth(), getHeight());
                mDeletedOverlay.draw(canvas);
            }

            if(mDeleteIcon!=null) {
                int left = (mImageView.getWidth() - mDeletedIconWitdh)/2;
                int top = (mImageView.getHeight() - mDeletedIconHeight)/2;
                mDeletedIconBounds.set(left, top, left + mDeletedIconWitdh, top + mDeletedIconHeight);
                mDeleteIcon.setBounds(mDeletedIconBounds);
                mDeleteIcon.draw(canvas);
            }
        }
    }

    /**
     * Enable or disable automotive mode.
     * Automotive mode is only supported for HtcGridItem.
     * This will change the font style and margins.
     *
     * @param enabled Whether enable automotive mode or not.
     */
    public void setAutomotiveMode(boolean enabled) {
        if (mAutomotiveEnabled == enabled) {
            return;
        }
        mAutomotiveEnabled = enabled;
        if (enabled) {
            applyAutomotiveFontStyle();
        } else {
            applyGenericFontStyle();
        }
    }

    void applyAutomotiveFontStyle() {
    }

    void applyGenericFontStyle() {
    }

    /**
     * Set the indicator to appear above the text and align to right in the
     * item.
     *
     * @param resId The resource identifier of the indicator
     */
    public void setIndicator(int resId) {
        if (mIndicatorResource == resId) {
            return;
        }
        mIndicatorResource = resId;
        if (mIndicatorResource != 0) {
            mIndicator = getResources().getDrawable(mIndicatorResource);
        } else {
            mIndicator = null;
        }
    }

    /**
     * Set the indicator to appear above the text and align to right in the
     * grid item. If the drawable parameter is null, the indicator will be hided.
     *
     * @param drawable The drawable of the indicator
     */
    public void setIndicator(Drawable drawable) {
        mIndicator = drawable;
        mIndicatorResource = 0;
    }

    int getIndicatorMarginBottom() {
        return mIndicatorMarginBottom;
    }

    /**
     * Customize the height of the image. By default, the image height always
     * equals the image width.
     *
     * @param height The custom height of the image.
     */
    private void setImageHeight(int height) {
        mCustomImageHeight = height;
    }

    /**
     * Set the deleted state for the grid item.
     * When the item enters the deleted state, the delete icon will be shown
     * on the center of the image and the whole item will be dim.
     * @param deleted Whether enter the deleted state or not.
     */
    public void setItemDeleted(boolean deleted) {
        if(mItemDeleted == deleted) {
            return;
        }

        mItemDeleted = deleted;
        if(deleted) {
            if(mDeleteIcon == null) {
                mDeleteIcon = getContext().getResources().getDrawable(com.htc.lib1.cc.R.drawable.common_gridview_delete);
                mDeletedIconBounds = new Rect();
                mDeletedIconWitdh = mDeleteIcon.getIntrinsicWidth();
                mDeletedIconHeight = mDeleteIcon.getIntrinsicHeight();
            }

            if(mDeletedOverlay == null) {
                mDeletedOverlay = new ColorDrawable(mDeletedOverlayColor);
            }
        }
        invalidate();
    }

    /**
     * Set the delete icon and delete overlay color.
     *
     * @param deleteIcon The Drawable of DeleteIcon.
     * @param deleteOverlayColor The color of DeleteOverlay.
     */
    void setDeleteIconAndOverlayColor(Drawable deleteIcon, int deleteOverlayColor) {
        if (null == deleteIcon) {
            return;
        }

        mDeleteIcon = deleteIcon;
        mDeletedIconBounds = new Rect();
        mDeletedIconWitdh = mDeleteIcon.getIntrinsicWidth();
        mDeletedIconHeight = mDeleteIcon.getIntrinsicHeight();
        mDeletedOverlay = new ColorDrawable(deleteOverlayColor);
    }
}
