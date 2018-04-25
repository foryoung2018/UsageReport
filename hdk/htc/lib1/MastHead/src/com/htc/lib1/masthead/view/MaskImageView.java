package com.htc.lib1.masthead.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.htc.lib1.masthead.R;

class MaskImageView extends ImageView {

	private Bitmap mMaskBitmap;
	private Paint mMaskPaint;
	private static int MASK_STYLE_NORMAL = 0;
	private static int MASK_STYLE_UPPER = 1;
	private static int MASK_STYLE_LOWER = 2;
	
    public MaskImageView(Context context) {
        super(context);
    }

    public MaskImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.MastheadMaskImageView, defStyle, 0);
        if (a != null) {
            int maskStyle = a.getInt(R.styleable.MastheadMaskImageView_MaskStyle, MASK_STYLE_NORMAL);
            
	        Drawable d = a.getDrawable(R.styleable.MastheadMaskImageView_MastheadMask);
            setMask(d, maskStyle);
	        a.recycle();
        }
    }

    private void setMask(Drawable d, int maskStyle) {
    	if (d == null)
    		return;
		float scale = getScaleRatio(d);
		int left, right, top, bottom;
		if (maskStyle == MASK_STYLE_UPPER) {
			left = 0;
			top = 0;
			right = d.getIntrinsicWidth();
			bottom = d.getIntrinsicHeight() / 2;
		} else if (maskStyle == MASK_STYLE_LOWER) {
			left = 0;
			top = d.getIntrinsicHeight() / 2;
			right = d.getIntrinsicWidth();
			bottom = d.getIntrinsicHeight();    			
		} else {
			left = 0;
			top = 0;
			right = d.getIntrinsicWidth();
			bottom = d.getIntrinsicHeight();    			
		}
		
    	if (d instanceof BitmapDrawable) {
    		mMaskBitmap = ((BitmapDrawable)d).getBitmap();
    		if(mMaskBitmap != null) {
    			mMaskBitmap = mMaskBitmap.extractAlpha();
    			int width = right - left;
    			int height = bottom - top;
    			mMaskBitmap = Bitmap.createBitmap(mMaskBitmap, left, top, width, height);
    			if (scale != 1.0f) {
    				mMaskBitmap = Bitmap.createScaledBitmap(mMaskBitmap, (int) (width * scale),(int) (height * scale), true);
    			}
    		}
    	} else {
    		mMaskBitmap = Bitmap.createBitmap(right - left, bottom - top, Bitmap.Config.ALPHA_8);
    		Canvas c = new Canvas(mMaskBitmap);
    		d.setBounds(left, top, right, bottom);
    		d.draw(c);
    		c.setBitmap(null);
    	}    	   
    	mMaskPaint = new Paint();
    	mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    	setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private float getScaleRatio(Drawable d) {
        float scale = 1.0f;
        Resources res = getResources();
        // x
        float imageW = d.getIntrinsicWidth();
        float canvasW = res.getDimensionPixelSize(R.dimen.theme_clock_4x1_img_width);
        float sx = (imageW != 0) ? canvasW / imageW : 1.0f;
        // y
        float imageH = d.getIntrinsicHeight();
        float canvasH = res.getDimensionPixelSize(R.dimen.theme_clock_4x1_img_height);
        float sy = (imageH != 0) ? canvasH / imageH : 1.0f;
        if (sx < sy) {
            scale = sx;
        } else {
            scale = sy;
        }
        return scale;
    }

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mMaskBitmap != null) {
			canvas.drawBitmap(mMaskBitmap, 0, 0, mMaskPaint);
		}
	}
}
