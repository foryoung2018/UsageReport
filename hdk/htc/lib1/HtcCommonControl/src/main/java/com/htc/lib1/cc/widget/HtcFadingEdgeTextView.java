package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

class HtcFadingEdgeTextView extends TextView {
     public HtcFadingEdgeTextView(Context context) {
         super(context, null);
     }

     public HtcFadingEdgeTextView(Context context,
                        AttributeSet attrs) {
        super(context, attrs);
     }

    public HtcFadingEdgeTextView(Context context,
                        AttributeSet attrs,
                        int defStyle) {
        super(context, attrs, defStyle);
    }
/*
    public void draw(Canvas canvas) {
        if(getEllipsize() == null){
            Layout layout = getLayout();
            if(getLineCount() == 1 && layout != null && layout.getLineWidth(0) > getMeasuredWidth()){
                setHorizontalFadingEdgeEnabled(true);
            } else {
                setHorizontalFadingEdgeEnabled(false);
            }
        }
        super.draw(canvas);
    }
*/
    void setTextStyle(int defStyle){
        Context context = getContext();
        setTextAppearance(context, defStyle);
/*
        TypedArray appearance =
            context.obtainStyledAttributes(defStyle,
                    com.android.internal.R.styleable.TextView);

        int shadowColor = appearance.getInt(com.android.internal.R.styleable.TextView_shadowColor, 0);
        float dx = appearance.getFloat(com.android.internal.R.styleable.TextView_shadowDx, 0);
        float dy = appearance.getFloat(com.android.internal.R.styleable.TextView_shadowDy, 0);
        float r = appearance.getFloat(com.android.internal.R.styleable.TextView_shadowRadius, 0);

        if (shadowColor != 0) {
            setShadowLayer(r, dx, dy, shadowColor);
        }
*/
    }

    void setEnableMarquee(boolean isMarquee){
        if(isMarquee){
            setSingleLine(true);
            setEllipsize(TextUtils.TruncateAt.MARQUEE);
            setMarqueeRepeatLimit(-1);
            setHorizontalFadingEdgeEnabled(true);
        } else {
            setSingleLine(true);
            setEllipsize(TextUtils.TruncateAt.END);//setEllipsize(null);
            setHorizontalFadingEdgeEnabled(false);
        }
    }
}
