
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.htc.lib1.cc.util.CheckUtil;

/**
 * @author felka
 * @hide
 */
final class ActionBarProgressBar extends ProgressBar {

    @ExportedProperty(category = "CommonControl")
    private int mDrawableWidth;
    @ExportedProperty(category = "CommonControl")
    private int mDrawableHeight;

    public ActionBarProgressBar(Context context) {
        this(context, null);
    }

    public ActionBarProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.progressBarStyle);
    }

    public ActionBarProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

    }

    /*
     * (non-Javadoc)
     * @see android.widget.ProgressBar#setIndeterminateDrawable(android.graphics.drawable.Drawable)
     */
    @Override
    public void setIndeterminateDrawable(Drawable d) {
        super.setIndeterminateDrawable(d);

        d = getIndeterminateDrawable();
        if (null != d) {
            mDrawableWidth = d.getIntrinsicWidth();
            mDrawableHeight = d.getIntrinsicHeight();
        }
    }

    /*
     * (non-Javadoc)
     * @see android.view.View#getLayoutParams()
     */
    @Override
    public LayoutParams getLayoutParams() {
        LayoutParams lp = super.getLayoutParams();
        if (null != lp && mDrawableWidth > 0 && mDrawableHeight > 0) {
            lp.width = mDrawableWidth;
            lp.height = mDrawableHeight;
        }
        return lp;
    }

    /**
     * @hide
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getIndeterminateDrawable();
        if (null != d) {
            setMeasuredDimension(d.getIntrinsicWidth(), d.getIntrinsicHeight());
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
