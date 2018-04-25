package com.htc.lib1.cc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

/**
 * This class is not recommended, please use <b>HtcCheckBox</b>, it can meet Sense 40 UI guideline.<br/>
 * Since deprecated, use default appearance.<br/>
 * @deprecated [Alternative Solution]
 */
/** @hide */
public class HtcListItemCheckBox extends CheckBox implements IHtcListItemControl {

    private void init(Context context, AttributeSet attrs) {

        setVisibility(View.VISIBLE);
        setFocusable(false);
        setClickable(false);
        setDuplicateParentStateEnabled(true);
    }

    public HtcListItemCheckBox(Context context) {
        super(context);
        init(context, null);
    }

    public HtcListItemCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HtcListItemCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
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

    @Override
    public void setPressed(boolean pressed) {
        if (pressed && ((View) getParent()).isPressed()) {
            return;
        }
        super.setPressed(pressed);
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
}
