
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

import com.htc.lib1.cc.R;

/**
 * Application must be set the size of the Vcard, if the set issue is not the correct size,
 * application should be responsible for yourself.
 *
 * @hide
 * @deprecated try level not release.
 */
public class Vcard extends RelativeLayout implements OnGlobalLayoutListener {

    private static final String TAG = "Vcard";
    private static final int BOUND = 300;
    private boolean mIsOnGlobalLayoutListenerExist;
    private int mBottomBoxHeight;
    private int mMarginM2;
    private int mVcardMinHeight;
    private int mVcardMinWidth;
    private View mDivider;
    private ImageView mSmallPhoto;
    private ImageButton mIconButton;

    public Vcard(Context context) {
        this(context, null);
    }

    public Vcard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Vcard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.htc_vcard, this, true);

        mSmallPhoto = (ImageView) findViewById(android.R.id.icon);
        mIconButton = (ImageButton) findViewById(android.R.id.button1);
        mDivider = findViewById(android.R.id.icon1);

        final Resources res = getResources();
        mMarginM2 = res.getDimensionPixelOffset(R.dimen.margin_m);
        mBottomBoxHeight = res.getDimensionPixelOffset(R.dimen.quickcontact_namebar_height);

        mVcardMinHeight = calculationVcardMinHeight(res);
        mVcardMinWidth = calculationVcardMinWidth(res);
        setMinimumHeight(mVcardMinHeight);
        setMinimumWidth(mVcardMinWidth);
    }

    /**
     * Calculation Vcard's minimum height,smallPhoto's height plus two M2 and plus BottomBox's
     * height.
     *
     * @return Vcard's minimum height.
     */
    private int calculationVcardMinHeight(Resources res) {
        final int smallPhotoHeight = res
                .getDimensionPixelOffset(R.dimen.quickcontact_smallphoto_size);
        final int vcardMinHeight = mMarginM2 * 2 + smallPhotoHeight + mBottomBoxHeight;
        return vcardMinHeight;
    }

    /**
     * Calculation Vcard's minimum width,Divider's width plus two M2 and plus IconButton's width and
     * two IconButtonMarginLR.
     *
     * @return Vcard's minimum width.
     */
    private int calculationVcardMinWidth(Resources res) {
        final int dividerWidth = res
                .getDimensionPixelOffset(R.dimen.htc_list_item_vertical_divider_width);
        final Drawable drawable = res.getDrawable(R.drawable.icon_btn_contact_card_dark);
        final int iconButtonWidth = drawable.getMinimumWidth();
        final int iconButtonMarginLR = res
                .getDimensionPixelOffset(R.dimen.quickcontact_namebar_icon_margin_lr);
        final int vcardMinWidth = mMarginM2 * 2 + dividerWidth + iconButtonWidth
                + iconButtonMarginLR * 2;
        return vcardMinWidth;
    }

    /** @hide **/
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!mIsOnGlobalLayoutListenerExist) {
            getViewTreeObserver().addOnGlobalLayoutListener(this);
            mIsOnGlobalLayoutListenerExist = true;
            checkDivider();
        }
    }

    /** @hide **/
    @Override
    public void onGlobalLayout() {
        checkDivider();
    }

    private void checkDivider() {
        if ((mIconButton.getVisibility() != View.VISIBLE) && (mDivider.getVisibility() == View.VISIBLE)) {
            mDivider.setVisibility(View.GONE);
        }
    }

    /** @hide **/
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mIsOnGlobalLayoutListenerExist) {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
            mIsOnGlobalLayoutListenerExist = false;
        }
    }

    /**
     * Will contact photo set as the background of Vcard and Small photo.
     *
     * @param drawable The contact photo drawable.
     */
    public void setVcardBackgroundImage(Drawable drawable) {

        if (null == drawable) {
            Log.e(TAG, "drawable = null", new Exception());
            return;
        }

        final View vcardMaskBackground = findViewById(R.id.vcard_mask_photo);
        final ImageView vcardPhoto = (ImageView) findViewById(R.id.vcard_photo);

        vcardPhoto.setImageDrawable(drawable);
        vcardPhoto.setScaleType(ScaleType.CENTER_CROP);

        if (drawable.getMinimumHeight() < BOUND || drawable.getMinimumWidth() < BOUND) {
            vcardMaskBackground.setVisibility(View.VISIBLE);
            mSmallPhoto.setVisibility(View.VISIBLE);
            mSmallPhoto.setImageDrawable(drawable);
        } else {
            vcardMaskBackground.setVisibility(View.GONE);
            mSmallPhoto.setVisibility(View.GONE);
        }
    }

}
