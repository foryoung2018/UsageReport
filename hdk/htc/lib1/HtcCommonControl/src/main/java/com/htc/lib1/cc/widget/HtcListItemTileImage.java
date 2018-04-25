package com.htc.lib1.cc.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * This appearance of this component looks like HtcListItemQuickContactBadge.
 * However, it has no APIs of QuickContactBadge. This is used when you only
 * needs the appearance but not the behavior of QuickContactBadge.
 */
public class HtcListItemTileImage extends HtcListItemImageComponent implements
        IHtcListItemComponentNoLeftTopMargin {
    private MaskImageView mBadge;
    private Drawable secondaryDrawable = null;

    private void init(Context context) {
        mBadge = new MaskImageView(context);
        mBadge.setScaleType(ScaleType.FIT_XY);

        addView(mBadge, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    /**
     * Simple constructor to use when creating a HtcListItemTileImage from code.
     *
     * @param context The Context the HtcListItemTileImage is running in,
     *            through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItemTileImage(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @param context The Context the HtcListItemTileImage is running in,
     *            through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the
     *            HtcListItemTileImage.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemTileImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style.
     *
     * @param context The Context the HtcListItemTileImage is running in,
     *            through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the
     *            HtcListItemTileImage.
     * @param defStyle The default style to apply to this HtcListItemTileImage.
     *            If 0, no style will be applied (beyond what is included in the
     *            theme). This may either be an attribute resource, whose value
     *            will be retrieved from the current theme, or an explicit style
     *            resource.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemTileImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setLayoutParams(ViewGroup.LayoutParams params) {

        params.width = mComponentWidth;
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
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(mComponentWidth,
                    mComponentHeight);
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
        setMeasuredDimension(mComponentWidth, mComponentHeight);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mBadge.layout(0, 0, mComponentWidth, mComponentHeight);
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * Set the drawable of image
     *
     * @param drawable the drawable displayed
     */
    public void setTileImageDrawable(Drawable drawable) {
        mBadge.setImageDrawable(drawable);
        mBadge.setScaleType(ScaleType.FIT_XY);
    }

    /**
     * Set the resource ID for the drawable displayed
     *
     * @param rId the resource id of the tile image
     */
    public void setTileImageResource(int rId) {
        mBadge.setImageResource(rId);
        mBadge.setScaleType(ScaleType.FIT_XY);
    }

    /**
     * Set the Bitmap for the drawable displayed
     *
     * @param bm the bitmap of the tile image
     */
    public void setTileImageBitmap(Bitmap bm) {
        mBadge.setImageBitmap(bm);
        mBadge.setScaleType(ScaleType.FIT_XY);
    }

    /**
     * Return the drawable of tile image
     *
     * @return The tile image drawable
     */
    public Drawable getTileImageDrawable() {
        return mBadge.getDrawable();
    }

    /**
     * Set the scale type of the tile image
     *
     * @param scaleType The scaleType to set on the tile image
     */
    public void setScaleType(ScaleType scaleType) {
        mBadge.setScaleType(scaleType);
    }

    /**
     * Sets a drawable as the content of this secondary image.
     *
     * @param resId the resource identifier of the the drawable
     */
    public void setSecondaryImageResource(int resId) {
        if (resId != 0) {
            try {
                secondaryDrawable = getContext().getResources().getDrawable(resId);
            } catch (Exception e) {
                Log.w("HtcListItemTileImage", "Unable to find resource: " + secondaryDrawable, e);
            }
            mBadge.invalidate();
        } else {
            return;
        }
    }

    /**
     * Sets a drawable as the content of this secondary image.
     *
     * @param drawable The drawable to set
     */
    public void setSecondaryImageDrawable(Drawable drawable) {
        secondaryDrawable = drawable;
        mBadge.invalidate();
    }

    /**
     * Sets a Bitmap as the content of this secondary image.
     *
     * @param bm The bitmap to set
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setSecondaryImageBitmap(Bitmap bm) {
        setSecondaryImageDrawable(new BitmapDrawable(getContext().getResources(), bm));
    }

    class MaskImageView extends ImageView {

        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        public MaskImageView(Context context) {
            super(context);
        }

        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (secondaryDrawable != null) {
                secondaryDrawable.setBounds(0,
                        getHeight() - secondaryDrawable.getIntrinsicHeight(),
                        secondaryDrawable.getIntrinsicWidth(), getHeight());

                secondaryDrawable.draw(canvas);
            }
        }
    }
}
