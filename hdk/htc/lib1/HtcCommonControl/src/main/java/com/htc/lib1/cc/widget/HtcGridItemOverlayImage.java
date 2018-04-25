
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.ImageView;

import com.htc.lib1.cc.util.CheckUtil;

class HtcGridItemOverlayImage extends ImageView {

    @ExportedProperty(category = "CommonControl")
    private boolean mDrawOverlay = false;

    private Rect mOverlayBounds;

    @ExportedProperty(category = "CommonControl")
    private int mOverlayColor = 0x99000000;

    private Drawable mOverlay;

    /**
     * Simple constructor to use when creating this widget from code. It will new a view with
     * default text style.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcGridItemOverlayImage(Context context) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);
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
    public HtcGridItemOverlayImage(Context context, AttributeSet attrs) {
        super(context, attrs);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);
    }

    void setDrawOverlay(boolean drawOverlay) {
        mDrawOverlay = drawOverlay;
        if (drawOverlay && mOverlay == null) {
            mOverlay = new ColorDrawable(mOverlayColor);
        }
    }

    void setOverlayBounds(Rect bounds) {
        mOverlayBounds = bounds;
    }

    /**
     * {@inheritDoc}
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawOverlay && mOverlay != null && mOverlayBounds != null) {
            mOverlay.setBounds(mOverlayBounds);
            mOverlay.draw(canvas);
        }
    }
}
