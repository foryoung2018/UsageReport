package com.htc.lib1.cc.graphic;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.StateSet;
import android.util.Xml;
import android.view.Gravity;

import com.htc.lib1.cc.util.DrawableUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * @deprecated [Not use any longer] Because this implementation use PopupWindow and the performance is not better than View. Please use HtcPopupContainer instead of this.
 */
/**@hide*/
public class PopupWindowDrawable extends StateListDrawable {
    BubbleDrawable  mAbove;
    BubbleDrawable  mBlow;
    Rect    mPadding = null;


    private static final int[] ABOVE_ANCHOR_STATE_SET = new int[] {
        android.R.attr.state_above_anchor
    };
    private static final int[] ZERO_STATE_SET = new int[] {
    };

    /**
     * the constructor of the PopupWindowDrawable
     * @param r the Resources instance, can't be null
     */
    public PopupWindowDrawable(Resources r){
//        parseXML2Drawable(r, "PopupWindowDrawable", R.drawable.popupwindow_drawable, this);
    }

    /* inflate drawable state from XML
     * @see android.graphics.drawable.StateListDrawable#inflate(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet)
     */
    @Override
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs)
            throws XmlPullParserException, IOException {
        final int [] readSets = {
                android.R.attr.state_above_anchor,
                android.R.attr.gravity,
                android.R.attr.drawable,
            };
        int depth;
        int type;
        final int innerDepth = parser.getDepth() + 1;

        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                && ((depth = parser.getDepth()) >= innerDepth
                || type != XmlPullParser.END_TAG)) {
            if (depth > innerDepth || !parser.getName().equals("BubbleDrawable")) {
                continue;
            }

            attrs = Xml.asAttributeSet(parser);
            final int numAttrs = attrs.getAttributeCount();
            if ( 0 >= numAttrs )
                continue;


            TypedArray a = r.obtainAttributes(attrs, readSets);
            int[] states = (a.getBoolean(0, false))?ABOVE_ANCHOR_STATE_SET:ZERO_STATE_SET;
            int gravity = a.getInteger(1, Gravity.TOP);
            int drawableRes = a.getResourceId(2, 0);
            a.recycle();


            BubbleDrawable d = new BubbleDrawable(r);
            DrawableUtil.parseXML2Drawable(r, "BubbleDrawable", drawableRes, d);
            d.setGravity(gravity);
            addState(states, d);

            if ( StateSet.stateSetMatches(ABOVE_ANCHOR_STATE_SET, states))
                mBlow = d;
            if ( StateSet.stateSetMatches(ZERO_STATE_SET, states))
                mAbove = d;
        }
    }

    /**
     *  @deprecated Please don't use this function, please use PopupWindowDrawable(Resources)
     */
    public PopupWindowDrawable(Context c) {
        this((null != c)?c.getResources():null);
    }

    /**
     * @return the mOffset
     * @hide
     */
    public int getOffset() {
        if ( null != mAbove )
            return mAbove.getOffset();
        if ( null != mBlow )
            return mBlow.getOffset();
        return 0;
    }

    /**
     * @param mOffset the mOffset to set
     * @hide
     */
    public void setOffset(int offset) {
        if ( offset != getOffset() ) {
            if ( null != mAbove )
                mAbove.setOffset(offset);
            if ( null != mBlow )
                mBlow.setOffset(offset);
            invalidateSelf();
        }
    }

    /* It needs to get the right(above/below) drawable padding
     * @see android.graphics.drawable.DrawableContainer#getPadding(android.graphics.Rect)
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean getPadding(Rect padding) {
        // TODO Auto-generated method stub

        if ( getCurrent() == mAbove ) {
            return mAbove.getPadding(padding);
        }

        return mBlow.getPadding(padding);
    }

    /**
     *  Get the minimum margin between target view(seekbar) and screen boundary
     *  @param m the rect that contains all of minimum margin(left, top, right, bottom)
     *  @hide
     */
    public void getMinScreenMargin(Rect m) {
        if ( null == m )
            return ;
        if ( null != mAbove ) {
            mAbove.getMinScreenMargin(m);
            return ;
        }
        if ( null != mBlow ) {
            mBlow.getMinScreenMargin(m);
            return ;
        }
    }

    /**
     * @return the mShift
     * @hide
     */
    final int getShift() {
        Drawable d = getCurrent();
        if ( null != d && d instanceof BubbleDrawable )
            ((BubbleDrawable)d).getShift();
        if ( null != mAbove )
            return mAbove.getShift();
        if ( null != mBlow )
            return mBlow.getShift();
        return 0;
    }

    /**
     * @param shift the mShift to set
     * @hide
     */
    public final void setShift(int shift) {
        Drawable d = getCurrent();
        if ( null != d && d instanceof BubbleDrawable )
            ((BubbleDrawable)d).setShift(shift);
        if ( null != mAbove )
            mAbove.setShift(shift);
        if ( null != mBlow )
            mBlow.setShift(shift);
    }

    /**
     * @return the mMargin
     * @hide
     */
    final int getMargin() {
        Drawable d = getCurrent();
        if ( null != d && d instanceof BubbleDrawable )
            ((BubbleDrawable)d).getMargin();
        if ( null != mAbove )
            return mAbove.getMargin();
        if ( null != mBlow )
            return mBlow.getMargin();
        return 0;
    }

    /**
     * @param margin the mMargin to set
     * @hide
     */
    public final void setMargin(int margin) {
        Drawable d = getCurrent();
        if ( null != d && d instanceof BubbleDrawable )
            ((BubbleDrawable)d).setMargin(margin);
        if ( null != mAbove )
            mAbove.setMargin(margin);
        if ( null != mBlow )
            mBlow.setMargin(margin);
    }
}
