package com.htc.lib1.cc.widget.reminder.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/** @hide */
public class SWLayerTextView extends TextView {
    public SWLayerTextView(Context context) {
        super(context);
        initRenderLayer();
    }

    public SWLayerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRenderLayer();
    }

    public SWLayerTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initRenderLayer();
    }

    private void initRenderLayer() {
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
}
