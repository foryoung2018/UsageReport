package com.htc.lib1.cc.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.htc.lib1.cc.R;

/**
 * Image without the rounded rectangle as the background.
 * <ul>
 * <li>Image Only
 *
 * <pre class="prettyprint">
 *      &lt;com.htc.widget.HtcListItemColorIcon
 *       android:id="@+id/photo"/&gt;
 * </pre>
 *
 * </ul>
 */
public class HtcListItemColorIcon extends HtcListItemImageComponent {
    private ImageView mBadge;
    @ExportedProperty(category = "CommonControl")
    private int mBadgeSize = 0;
    @ExportedProperty(category = "CommonControl")
    private int M2;
    @ExportedProperty(category = "CommonControl")
    private int mActionButtonWidth = 0;

    private void init(Context context) {

        mBadge = new ImageView(context);
        mBadgeSize = context.getResources().getDimensionPixelOffset(
                R.dimen.htc_list_item_color_icon_size);
        mBadge.setScaleType(ScaleType.FIT_XY);
        M2 = HtcListItemManager.getM2(context);
        mActionButtonWidth = HtcListItemManager.getActionButtonWidth(context,
                HtcListItem.MODE_AUTOMOTIVE);

        super.setPadding(0, 0, 0, 0);
        addView(mBadge, 0, new LayoutParams(mBadgeSize, mBadgeSize));
    }

    /**
     * Simple constructor to use when creating a HtcListItemColorIcon from code.
     *
     * @param context The Context the HtcListItemColorIcon is running in,
     *            through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItemColorIcon(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @param context The Context the HtcListItemColorIcon is running in,
     *            through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the
     *            HtcListItemColorIcon.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemColorIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style.
     *
     * @param context The Context the HtcListItemColorIcon is running in,
     *            through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the
     *            HtcListItemColorIcon.
     * @param defStyle The default style to apply to this HtcListItemColorIcon.
     *            If 0, no style will be applied (beyond what is included in the
     *            theme). This may either be an attribute resource, whose value
     *            will be retrieved from the current theme, or an explicit style
     *            resource.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemColorIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setLayoutParams(ViewGroup.LayoutParams params) {

        params.width = mIsAutomotiveMode ? mActionButtonWidth : mComponentWidth - M2;
        params.height = mComponentHeight;

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
                    mIsAutomotiveMode ? mActionButtonWidth : mComponentWidth - M2, mComponentHeight);
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
    protected void onMeasure(int w, int h) {
        measureChild(mBadge, w, h);
        super.onMeasure(w, h);
        if (mIsAutomotiveMode)
            setMeasuredDimension(mActionButtonWidth, mComponentHeight);
        else
            setMeasuredDimension(mComponentWidth - M2, mComponentHeight);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mIsAutomotiveMode)
            mBadge.layout((mActionButtonWidth - mBadgeSize) / 2,
                    (mComponentHeight - mBadgeSize) / 2, (mActionButtonWidth + mBadgeSize) / 2,
                    (mComponentHeight + mBadgeSize) / 2);
        else
            mBadge.layout((mComponentWidth - M2 - mBadgeSize) / 2,
                    (mComponentHeight - mBadgeSize) / 2, (mComponentWidth - M2 + mBadgeSize) / 2,
                    (mComponentHeight + mBadgeSize) / 2);
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * Set the image drawable
     *
     * @param drawable the drawable display in the image
     */
    public void setColorIconImageDrawable(Drawable drawable) {
        mBadge.setImageDrawable(drawable);
    }

    /**
     * Set the image resource
     *
     * @param rId the resource ID of the drawable display in the image
     */
    public void setColorIconImageResource(int rId) {
        mBadge.setImageResource(rId);
    }

    /**
     * Set image bitmap
     *
     * @param bm The color icon image bitmap
     */
    public void setColorIconImageBitmap(Bitmap bm) {
        mBadge.setImageBitmap(bm);
    }

    /**
     * get the drawable of color icon
     *
     * @return The color icon drawable
     */
    public Drawable getColorIconDrawable() {
        return mBadge.getDrawable();
    }

    /**
     * set the scale type of the ImageView hold by this widget.
     *
     * @param scaleType
     */
    public void setScaleType(ImageView.ScaleType scaleType) {
        mBadge.setScaleType(scaleType);
    }
}
