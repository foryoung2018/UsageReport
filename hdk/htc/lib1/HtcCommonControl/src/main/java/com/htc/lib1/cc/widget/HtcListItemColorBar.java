package com.htc.lib1.cc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * The vertical color bar in HtcListItem.
 *
 */
/**
 * @deprecated [Alternative solution] Please use setColorBarEnabled(boolean isColorBarEnabled) in HtcListItem
 */
/** @hide */
public class HtcListItemColorBar extends ImageView implements IHtcListItemComponent {
    private int mDesiredWidth = 0;

    private void init(Context context) {
        setVisibility(View.VISIBLE);
        this.setScaleType(ScaleType.FIT_XY);
        mDesiredWidth = context.getResources().getDimensionPixelOffset(
                com.htc.lib1.cc.R.dimen.htc_list_item_color_bar_width);
        int padding = HtcListItemManager.getM3(context);
        setPadding(0, padding, 0, padding);
    }

    public HtcListItemColorBar(Context context) {
        super(context);
        init(context);
    }

    public HtcListItemColorBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HtcListItemColorBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

        params.width = mDesiredWidth;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;

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
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(mDesiredWidth,
                    ViewGroup.LayoutParams.MATCH_PARENT);
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
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled)
            return;
        super.setEnabled(enabled);
        HtcListItemManager.setViewOpacity(this, enabled);
    }

    /**
     * {@hide}
     */
    public void notifyItemMode(int itemMode) {
    }
}
