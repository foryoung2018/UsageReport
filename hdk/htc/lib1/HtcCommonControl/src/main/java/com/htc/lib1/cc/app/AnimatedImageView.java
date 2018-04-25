package com.htc.lib1.cc.app;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @deprecated [Not use any longer] Not support class
 */
/**@hide*/
public class AnimatedImageView extends ImageView {

    private boolean isPlayAnimation = false;

    private static final int ROTATE_SPEED = 30;    //degree per update

    private int mRotateDegree = 0;

    private static final int ROTATE_DURATION = 300;

    private long mLastInvalidate = 0;

    public AnimatedImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public AnimatedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public AnimatedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void startAnimation(){
        isPlayAnimation = true;
        mRotateDegree = 0;
        mLastInvalidate = 0;
        invalidate();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void stopAnimation(){
        isPlayAnimation = false;
        invalidate();
    }


/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onDraw(Canvas canvas) {
        if(isPlayAnimation){
            if(mLastInvalidate == 0 || (System.currentTimeMillis() - mLastInvalidate) >=  ROTATE_DURATION){
                mRotateDegree += ROTATE_SPEED;
                mRotateDegree %= 360;
                mLastInvalidate = System.currentTimeMillis();
                postInvalidateDelayed(ROTATE_DURATION);
            }
            canvas.translate(getWidth() / 2.0f, getHeight() / 2.0f);
            canvas.rotate(mRotateDegree);
            canvas.translate(-getWidth() / 2.0f, -getHeight() / 2.0f);
            super.onDraw(canvas);


            return;
        } else {
            super.onDraw(canvas);
        }

    }


}
