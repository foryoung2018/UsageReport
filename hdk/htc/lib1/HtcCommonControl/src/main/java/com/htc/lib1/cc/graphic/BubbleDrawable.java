/**
 *
 */
package com.htc.lib1.cc.graphic;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.htc.lib1.cc.util.res.HtcResUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import android.view.Gravity;

/**
 * @author felka
 * @deprecated [Not use any longer] Because this implementation use PopupWindow and the performance is not better than View. Please use HtcPopupContainer instead of this.
 */
/**@hide*/
public class BubbleDrawable extends Drawable {
    Drawable    mBody;
    Drawable    mTriangle;
    Rect        mBodyBounds;
    Rect        mTriangleBounds;
    int         mGravity;
    int         mTrianglePadding;
    int         mOffset;
    int            mShift = 6;
    int            mMargin = 6;

    public BubbleDrawable(Resources r) {
        mBodyBounds = new Rect();
        mTriangleBounds = new Rect();
        mGravity = Gravity.TOP;

        if ( null == r )
            return ;

        // TODO Auto-generated constructor stub
//        mBody = r.getDrawable(HtcResUtil.getPrivateResID("com.htc.lib1.cc.R.drawable.common_popupmenu"));
//        mTriangle = r.getDrawable(HtcResUtil.getPrivateResID("com.htc.lib1.cc.R.drawable.common_popupmenu_arrow"));
//        mTrianglePadding = mTriangle.getIntrinsicHeight() - mShift;
    }

    /**
     *
     */
    public BubbleDrawable(Context c) {
        // TODO Auto-generated constructor stub
           this(((null != c)?c.getResources():null));
    }

    /* Inflate drawable state from XML
     * @see android.graphics.drawable.Drawable#inflate(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet)
     * @hide
     */
    @Override
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs)
            throws XmlPullParserException, IOException {
        // TODO Auto-generated method stub
        super.inflate(r, parser, attrs);

        if ( !"BubbleDrawable".equals(parser.getName()) )
            return ;

        final int [] readAttrSets = {
            android.R.attr.background,
            android.R.attr.padding,
            android.R.attr.layout_margin,
            android.R.attr.drawable,
        };
        TypedArray a = r.obtainAttributes(attrs, readAttrSets);
        mBody = a.getDrawable(0);
        mShift = a.getDimensionPixelSize(1, 6);
        mMargin = a.getDimensionPixelSize(2, 6);
        mTriangle = a.getDrawable(3);
        a.recycle();

        setShift(mShift);
    }

    @Override
    public int getMinimumHeight() {
        // TODO Auto-generated method stub
        return super.getMinimumHeight();
    }

    @Override
    public int getMinimumWidth() {
        // TODO Auto-generated method stub
        return super.getMinimumWidth();
    }

    /* (non-Javadoc)
     * @see android.graphics.drawable.Drawable#draw(android.graphics.Canvas)
     */
    @Override
    public void draw(Canvas canvas) {
        // TODO Auto-generated method stub

        canvas.save();

        mBody.draw(canvas);

        if ( Gravity.BOTTOM == getGravity() )
            canvas.rotate(180, getBounds().centerX(), getBounds().centerY());
        else if ( Gravity.LEFT == getGravity() )
            canvas.rotate(-90, getBounds().centerX(), getBounds().centerY());
        else if ( Gravity.RIGHT == getGravity() )
            canvas.rotate(90, getBounds().centerX(), getBounds().centerY());
        else
            ;

        mTriangle.draw(canvas);


        canvas.restore();
    }

    /* (non-Javadoc)
     * @see android.graphics.drawable.Drawable#getOpacity()
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOpacity() {
        // TODO Auto-generated method stub
        return PixelFormat.RGBA_8888;
    }

    /* (non-Javadoc)
     * @see android.graphics.drawable.Drawable#setAlpha(int)
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void setAlpha(int arg0) {
        // TODO Auto-generated method stub
        mBody.setAlpha(arg0);
        mTriangle.setAlpha(arg0);
    }

    /* (non-Javadoc)
     * @see android.graphics.drawable.Drawable#setColorFilter(android.graphics.ColorFilter)
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void setColorFilter(ColorFilter arg0) {
        // TODO Auto-generated method stub
        mBody.setColorFilter(arg0);
        mTriangle.setColorFilter(arg0);
    }

    /* (non-Javadoc)
     * @see android.graphics.drawable.Drawable#onBoundsChange(android.graphics.Rect)
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onBoundsChange(Rect bounds) {
        // TODO Auto-generated method stub
        super.onBoundsChange(bounds);

        setBodyDrawableGravity(getGravity());
        setTriangleBounds();
    }

    /* (non-Javadoc)
     * @see android.graphics.drawable.Drawable#getPadding(android.graphics.Rect)
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean getPadding(Rect padding) {
        // TODO Auto-generated method stub

        if ( null == mBody )
            return false;

        Rect r = new Rect();

        mBody.getPadding(r);
        final int nPadding = r.left;
        final int nTriangleHeight = mTriangle.getIntrinsicHeight();

        padding.top =  ((Gravity.TOP == (mGravity & Gravity.TOP))?nTriangleHeight:nPadding) ;
        padding.bottom =  ((Gravity.BOTTOM == (mGravity & Gravity.BOTTOM))?nTriangleHeight:nPadding);
        padding.left =  ((Gravity.LEFT == (mGravity & Gravity.LEFT))?nTriangleHeight:nPadding);
        padding.right = ((Gravity.RIGHT == (mGravity & Gravity.RIGHT))?nTriangleHeight:nPadding);

        return true;
    }

    /**
     * @return the mGravity
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getGravity() {
        return mGravity;
    }

    /**
     * @param mGravity the mGravity to set
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setGravity(int mGravity) {
        this.mGravity = mGravity;
        setBodyDrawableGravity(getGravity());
        invalidateSelf();
    }

    /**
     * @param mGravity the mGravity to set
     */
    private void setBodyDrawableGravity(int mGravity) {
        this.mGravity = mGravity;

        Rect bounds = getBounds();

        mBodyBounds.top = bounds.top + ((Gravity.TOP == (mGravity & Gravity.TOP))?mTrianglePadding:0) ;
        mBodyBounds.bottom = bounds.bottom - ((Gravity.BOTTOM == (mGravity & Gravity.BOTTOM))?mTrianglePadding:0);
        mBodyBounds.left = bounds.left + ((Gravity.LEFT == (mGravity & Gravity.LEFT))?mTrianglePadding:0);
        mBodyBounds.right = bounds.right - ((Gravity.RIGHT == (mGravity & Gravity.RIGHT))?mTrianglePadding:0);
        if ( null != mBody )
            mBody.setBounds(mBodyBounds);
    }

    private void setTriangleBounds() {
        Rect bounds = getBounds();

        mTriangleBounds.left = (bounds.width() - (mTriangle.getIntrinsicWidth())>>1);
        mTriangleBounds.right = mTriangleBounds.left + mTriangle.getIntrinsicWidth();
        mTriangleBounds.left += (Gravity.TOP == (mGravity & Gravity.TOP))?-getOffset():getOffset();
        mTriangleBounds.right += (Gravity.TOP == (mGravity & Gravity.TOP))?-getOffset():getOffset();
        mTriangleBounds.top = bounds.top;
        mTriangleBounds.bottom = bounds.top + mTriangle.getIntrinsicHeight();

        if ( null != mTriangle )
            mTriangle.setBounds(mTriangleBounds);
    }

    /**
     * @return the mOffset
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getOffset() {
        return mOffset;
    }




    /**
     * @param mOffset the mOffset to set
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setOffset(int offset) {
        this.mOffset = offset;

        setTriangleBounds();
        invalidateSelf();
    }

    /**
     *  Get the minimum margin between target view(seekbar) and screen boundary
     *  @param m the rect that contains all of minimum margin(left, top, right, bottom)
     *  @hide
     */
    final void getMinScreenMargin(Rect m) {
        if ( null == m )
            return ;

        getPadding(m);
        int halfTriangleWidth = (mTriangle.getIntrinsicWidth()>>1);
        int contentMargin = mMargin + halfTriangleWidth;
        m.left += contentMargin;
        m.top += contentMargin;
        m.right += contentMargin;
        m.bottom += contentMargin;
    }

    /**
     * @return the mShift
     */
    final int getShift() {
        return mShift;
    }

    /**
     * @param shift the mShift to set
     */
    final void setShift(int shift) {
        this.mShift = shift;

        mTrianglePadding = mTriangle.getIntrinsicHeight() - mShift;
    }

    /**
     * @return the mMargin
     */
    final int getMargin() {
        return mMargin;
    }

    /**
     * @param margin the mMargin to set
     */
    final void setMargin(int margin) {
        this.mMargin = margin;
    }

}
