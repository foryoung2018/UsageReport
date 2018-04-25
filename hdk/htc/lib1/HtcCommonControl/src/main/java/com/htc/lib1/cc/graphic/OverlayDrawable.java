package com.htc.lib1.cc.graphic;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.htc.lib1.cc.R;

/** @hide */
public class OverlayDrawable extends Drawable {

    private Drawable mDrawable;
    private int mColor = -1;
    public static int MODE_DEFAULT = 0;
    public static int MODE_LIGHT = 1;
    public static int MODE_DARK = 2;
    public static int MODE_ACTIONBAR_BACKGROUND =3;

    public static final int PURE_COLOR = 0;
    public static final int PURE_PNG = 1;
    public static final int COLOR_AND_PNG = 2;
    private int mOverlayType = -1;

    public OverlayDrawable() {
    }
    public OverlayDrawable(Drawable drawable, int color) {
        mOverlayType = COLOR_AND_PNG;
        mDrawable = drawable;
        mColor = color;
    }

    public OverlayDrawable(Context context, Drawable drawable) {
        this(context, drawable, MODE_DEFAULT);
    }

    public OverlayDrawable(Context context, Drawable drawable, int mode) {
        mOverlayType = COLOR_AND_PNG;
        mDrawable = drawable;
        /* TODO : change read from theme */
        TypedArray a = context.obtainStyledAttributes(com.htc.lib1.cc.R.styleable.OverlayDrawable);
        if (mode == MODE_LIGHT)
            mColor = a.getColor(com.htc.lib1.cc.R.styleable.OverlayDrawable_light_category_color, 0);
        else if (mode == MODE_DARK)
            mColor = a.getColor(com.htc.lib1.cc.R.styleable.OverlayDrawable_dark_category_color, 0);
        else if (mode == MODE_ACTIONBAR_BACKGROUND)
            mColor = a.getColor(com.htc.lib1.cc.R.styleable.OverlayDrawable_multiply_color, 0);
        else
            mColor = a.getColor(com.htc.lib1.cc.R.styleable.OverlayDrawable_category_color, 0);
        a.recycle();
    }

   /* Inflate drawable state from XML
    * @see android.graphics.drawable.Drawable#inflate(android.content.res.Resources,
    * org.xmlpull.v1.XmlPullParser, android.util.AttributeSet)
    * @hide
    */
    @Override
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs)
    throws XmlPullParserException, IOException {

        if (!"OverlayDrawable".equals(parser.getName()))
            return;

        TypedArray a = r.obtainAttributes(attrs, com.htc.lib1.cc.R.styleable.OverlayDrawable);
        mDrawable = a.getDrawable(com.htc.lib1.cc.R.styleable.OverlayDrawable_android_drawable);

       mColor = a.getColor(com.htc.lib1.cc.R.styleable.OverlayDrawable_android_color,Color.RED);
       android.util.Log.d("OverlayDrawable", "color = " + mColor);

      //  int colorId = a.getResourceId(com.htc.lib1.cc.R.styleable.OverlayDrawable_theme_color,0);
      //  if (colorId > 0)
      //      mColor = r.getColor(colorId);
      //  android.util.Log.d("OverlayDrawable", "color = " + mColor+"  resId = " + colorId);
      // mColor = a.getColor(com.htc.lib1.cc.R.styleable.OverlayDrawable_theme_color,Color.RED);
       // android.util.Log.d("OverlayDrawable", "color = " + "  resId = " + mColor);
        String key = a.getString(com.htc.lib1.cc.R.styleable.OverlayDrawable_android_key);
        if ( key.equals("Color")){
           mOverlayType = PURE_COLOR;
        }else if (key.equals("Png")){
           mOverlayType = PURE_PNG;
        }else if (key.equals("Overlay")){
           mOverlayType = COLOR_AND_PNG;
        }
        a.recycle();
    }



    /**
     * @hide
     */
    @Override
    public int getMinimumHeight() {
        if (mDrawable == null)
            return super.getMinimumHeight();
        return mDrawable.getMinimumHeight();
    }

    /**
     * @hide
     */
    @Override
    public int getMinimumWidth() {
        if (mDrawable == null)
            return super.getMinimumWidth();
        return mDrawable.getMinimumWidth();
    }

    /**
     * @hide
     */
    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        if (mDrawable == null)
            super.setBounds(left, top, right, bottom);
        else
            mDrawable.setBounds(left, top, right, bottom);
    }

    /**
     * @hide
     */
    @Override
    public void setBounds(Rect bounds) {
        if (mDrawable == null)
            super.setBounds(bounds);
        else
            mDrawable.setBounds(bounds);
    }

    /**
     * @hide
     */
    @Override
    public void draw(Canvas canvas) {
        if (canvas == null)
            return;

        if (PURE_COLOR == mOverlayType)
            canvas.drawColor(mColor);
        else if (mOverlayType == PURE_PNG){
            if (mDrawable != null)
                mDrawable.draw(canvas);
        }else if (mOverlayType == COLOR_AND_PNG){
            if ( null != mDrawable ) {
                int saveCount = canvas.saveLayer(0, 0, mDrawable.getBounds().width(),
                    mDrawable.getBounds().height(), null, Canvas.ALL_SAVE_FLAG);
                if (mDrawable != null)
                    mDrawable.draw(canvas);
                canvas.drawColor(mColor, PorterDuff.Mode.SRC_ATOP);
                canvas.restoreToCount(saveCount);
            }
        }
    }

    /**
     * @hide
     */
    @Override
    public void setAlpha(int arg0) {
        if (mDrawable != null)
            mDrawable.setAlpha(arg0);
    }

    /**
     * @hide
     */
    @Override
    public void setColorFilter(ColorFilter arg0)
    {
        if (mDrawable != null)
            mDrawable.setColorFilter(arg0);
    }

    /**
     * @hide
     */
    @Override
    protected void onBoundsChange(Rect bounds)
    {
        super.onBoundsChange(bounds);
        if (mDrawable != null)
            mDrawable.setBounds(bounds);
    }

    /**
     * @hide
     */
    @Override
    public boolean getPadding(Rect padding)
    {
        if (null == mDrawable)
            return super.getPadding(padding);
        return mDrawable.getPadding(padding);
    }

    /**
     * @hide
     */
    @Override
    public int getOpacity()
    {
        if (mDrawable != null)
            return mDrawable.getOpacity();
        return 0;
    }

    /* (non-Javadoc)
     * @see android.graphics.drawable.Drawable#getIntrinsicWidth()
     */
    @Override
    public int getIntrinsicWidth() {
        if (mDrawable != null)
            return mDrawable.getIntrinsicWidth();
        return super.getIntrinsicWidth();
    }

    /* (non-Javadoc)
     * @see android.graphics.drawable.Drawable#getIntrinsicHeight()
     */
    @Override
    public int getIntrinsicHeight() {
        if (mDrawable != null)
            return mDrawable.getIntrinsicHeight();
        return super.getIntrinsicHeight();
    }

}
