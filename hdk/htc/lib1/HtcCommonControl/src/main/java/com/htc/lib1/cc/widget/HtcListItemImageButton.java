package com.htc.lib1.cc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.htc.lib1.cc.util.CheckUtil;

/**
 * This class is not recommended, please use <b>HtcImageButton</b>, it can meet Sense 40 UI guideline.<br/>
 * Since deprecated, use default appearance.<br/>
 * @deprecated [Alternative Solution]
 */
/** @hide */
public class HtcListItemImageButton extends ImageView implements IHtcListItemControl,
        IHtcListItemComponent {

    private void init(Context context) {
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        setVisibility(View.VISIBLE);
        setScaleType(ScaleType.FIT_XY);
    }

    public HtcListItemImageButton(Context context) {
        super(context);
        init(context);
    }

    public HtcListItemImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HtcListItemImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
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
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
