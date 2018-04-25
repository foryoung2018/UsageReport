package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup.LayoutParams;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.widget.HtcImageButton;

/**
 * An ImageButton with a read-only progress bar and also support RemoteViews.
 */
public class HtcProgressButton extends HtcImageButton {

    @ExportedProperty(category = "CommonControl")
    private int mEdgeLength;

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = HtcButtonUtil.BUTTON_SIZE_MODE_SMALL, to = "BUTTON_SIZE_MODE_SMALL"),
            @IntToString(from = HtcButtonUtil.BUTTON_SIZE_MODE_MIDDLE, to = "BUTTON_SIZE_MODE_MIDDLE")
    })
    private int mSizeMode = HtcButtonUtil.BUTTON_SIZE_MODE_SMALL;

    @ExportedProperty(category = "CommonControl")
    private float mMax = 0f, mCurrentProgress = 0f, mPercent = 0f;

    @ExportedProperty(category = "CommonControl")
    private float mCurrentScaleWidth, mCurrentScaleHeight;

    private ProgressBackgroundDrawable mBackground;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcProgressButton(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     *
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     */
    public HtcProgressButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of View allows subclasses to use their own base style when
     * they are inflating. For example, a Button class's constructor would call
     * this version of the super class constructor and supply
     * <code>R.attr.buttonStyle</code> for <var>defStyle</var>; this allows
     * the theme's button style to modify all of the base view attributes (in
     * particular its background) as well as the Button class's attributes.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view. If 0, no style
     *        will be applied (beyond what is included in the theme). This may
     *        either be an attribute resource, whose value will be retrieved
     *        from the current theme, or an explicit style resource.
     * @see #View(Context, AttributeSet)
     */
    public HtcProgressButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * Constructor to indicate if need do mutiply to content and button size.
     * @param context The Context the view is running in.
     * @param isContentMultiply If need do multiply to content, default is true.
     * @param sizeMode The button mode (middle/small), default is small mode.
     */
    public HtcProgressButton(Context context, boolean isContentMultiply, int sizeMode) {
        super(context, HtcButtonUtil.BACKGROUND_MODE_DARK, isContentMultiply);
        mSizeMode = sizeMode;
        init(context);
    }

    private void init(Context context) {
        int diameter, stroke;

        if (mSizeMode == HtcButtonUtil.BUTTON_SIZE_MODE_MIDDLE) {
            diameter = context.getResources().getDimensionPixelSize(com.htc.lib1.cc.R.dimen.progress_button_diameter_middle);
            stroke = context.getResources().getDimensionPixelSize(com.htc.lib1.cc.R.dimen.progress_button_stroke_middle);
        } else {
            diameter = context.getResources().getDimensionPixelSize(com.htc.lib1.cc.R.dimen.progress_button_diameter_small);
            stroke = context.getResources().getDimensionPixelSize(com.htc.lib1.cc.R.dimen.progress_button_stroke_small);
        }

        setPadding(0,0,0,0);
        mEdgeLength = diameter;

        if (mBackground == null)
            mBackground = new ProgressBackgroundDrawable(context);

        mBackground.setDiameter(mEdgeLength);
        setBackground(mBackground);
        setProgressBarWidth(stroke);

    }

    /**
     * To set the button size mode dynamically.
     * @param sizeMode The button mode (middle/small).
     */
    public void setButtonSizeMode(int sizeMode) {
        if (sizeMode == mSizeMode) return;
        mSizeMode = sizeMode;
        init(getContext());
    }

    /**
     * Set the max value of the progress bar.
     * @param max The max progress value, it should be greater than 0f.
     */
    public void setMax(float max) {
        if (max > 0f) mMax = max;
        else throw new IllegalArgumentException("The parameter max should be greater than zero.");
    }

    /**
     * Update the UI presentation to fit the specified progress in percentage, this API will cause re-draw if the progress differs from before.
     * Please make sure {@link #setMax(float)} has been called before this API.
     * @param current The current progress value, it should be greater than 0f and less or equal than the max progress set before.
     */
    public void setProgress(float current) {
        if (mMax <= 0f) throw new RuntimeException("Should setMax before setCurrentProgress and the max should be greater than zero.");
        if (current < 0f) current = 0f;
        if (current > mMax) current = mMax;

        if (mBackground != null && mCurrentProgress != current) {
            mCurrentProgress = current;
            mPercent = mCurrentProgress / mMax;
            mBackground.setSweepAngle(mPercent);
        }
    }

    /**
     * Just set the bar width and it will not cause re-draw, please call {@link #setCurrentProgress(float)} after set bar width.
     * @param barWidth The width of the progress bar, it should be in the range between 0f and the width of this view.
     */
    private void setProgressBarWidth(float barWidth) {
        if (mBackground != null)
            mBackground.setProgressBarWidth(barWidth);
        else throw new RuntimeException("The background of progress button is null!!!");
    }

    /**@hide*/
    protected void onMeasure(int wSpec, int hSpec) {
        LayoutParams params = getLayoutParams();

        if (params == null) {
            super.onMeasure(wSpec, hSpec);
        } else {
            int edgeLength = Math.min(MeasureSpec.getSize(wSpec), MeasureSpec.getSize(hSpec)), edgeSpec;
            if (params.width == LayoutParams.WRAP_CONTENT || params.height == LayoutParams.WRAP_CONTENT) edgeLength = mEdgeLength;
            else edgeLength = (edgeLength>mEdgeLength ? edgeLength : mEdgeLength);
            edgeSpec = MeasureSpec.makeMeasureSpec(edgeLength, MeasureSpec.EXACTLY);
            super.onMeasure(edgeSpec, edgeSpec);
        }
    }

    /**
     * This is the background of this button, it draws the progress bar part.
     */
    private class ProgressBackgroundDrawable extends Drawable {
        private final float mStartAngle = 270.0f, mFullCircle = 360.0f;

        private int mRailColor=0xFF000000, mRailAlpha = 0xFF*60/100;
        private int mOverlayColor, mDiameter;
        private float  mStroke = 0.0f, mSweepAngle = 0.0f;

        private Paint mProgressPaint;
        private RectF mBoundsf, mMaskBounds;
        private final Xfermode xmode;

        public ProgressBackgroundDrawable(Context context) {
            mProgressPaint = new Paint();
            mProgressPaint.setAntiAlias(true);
            mProgressPaint.setStyle(Paint.Style.FILL);
            xmode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

            if (HtcButtonUtil.isDarkMode(getBackgroundMode())) {
                mOverlayColor = mRailColor = context.getResources().getColor(com.htc.lib1.cc.R.color.ap_background_color);
                mRailAlpha = 0xFF*30/100;
            } else {
                mOverlayColor = HtcButtonUtil.getCategoryColor(context, null);
            }
        }

        /**
         * To set the diamter of the outer circle.
         * @param diameter The diameter.
         */
        public void setDiameter(int diameter) {
            mDiameter = diameter;
        }

        /**
         * @see android.graphics.drawable.Drawable#draw(Canvas)
         */
        @Override
        public void draw(Canvas canvas) {
            if (mBoundsf == null)
                throw new RuntimeException("No bounds for background to draw.");

            int sc = canvas.saveLayer(mBoundsf.left, mBoundsf.top, mBoundsf.right, mBoundsf.bottom, null,
                                      Canvas.MATRIX_SAVE_FLAG |
                                      Canvas.CLIP_SAVE_FLAG |
                                      Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                                      Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                                      Canvas.CLIP_TO_LAYER_SAVE_FLAG);

            //Draw bottom rail.
            mProgressPaint.setColor(mRailColor);
            mProgressPaint.setAlpha(mRailAlpha);
            canvas.drawArc(mBoundsf, mStartAngle, mFullCircle, true, mProgressPaint);

            //Draw progress.
            mProgressPaint.setAlpha(0xFF);
            mProgressPaint.setColor(mOverlayColor);
            canvas.drawArc(mBoundsf, mStartAngle, mSweepAngle, true, mProgressPaint);

            //Draw mask.
            mProgressPaint.setXfermode(xmode);
            canvas.drawArc(mMaskBounds, mStartAngle, mFullCircle, true, mProgressPaint);

            mProgressPaint.setXfermode(null);
            canvas.restoreToCount(sc);
        }

        /**
         * @see android.graphics.drawable.Drawable#getOpacity()
         */
        @Override
        public int getOpacity() {
            return 0;
        }

        /**
         * @see android.graphics.drawable.Drawable#setAlpha(int)
         */
        @Override
        public void setAlpha(int alpha) {
        }

        /**
         * @see android.graphics.drawable.Drawable#setColorFilter(ColorFilter)
         */
        @Override
        public void setColorFilter(ColorFilter cf) {
        }

        /*
         * There is a flag named mBackgroundSizeChanged inside View used to determine setBounds() needs to be invoked or not.
         * The flag will be set while setFrame() during layout() or user call setBackground() and then setBounds() will be invoked during View.draw().
         */
        /**
         * To set the bounds for the outer circle.
         * @param bounds It should be a square to draw a circle inside it.
         * @see android.graphics.drawable.Drawable#setBounds(android.graphics.Rect)
         */
        @Override
        public void setBounds(Rect bounds) {
            if (bounds == null) throw new IllegalArgumentException("The parameter bounds should not be null!!!");
            this.setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
        }

        /**
         * To set the bounds for the outer circle.
         * @param left The left X.
         * @param top The top Y.
         * @param right The right X.
         * @param bottom The bottom Y.
         * @see android.graphics.drawable.Drawable#setBounds(int, int, int, int)
         */
        @Override
        public void setBounds(int left, int top, int right, int bottom) {
            //Reset the bounds to let the background can be drawn at center.
            int cX = left + (right-left)/2, cY = top + (bottom-top)/2, radius = mDiameter/2;

            left = cX - radius;
            top = cY - radius;
            right = cX + radius;
            bottom = cY + radius;

            super.setBounds(left, top, right, bottom);
        }

        /**
         * @see android.graphics.drawable.Drawable#onBoundsChange(android.graphics.Rect)
         */
        @Override
        protected void onBoundsChange(Rect bounds) {
            if (bounds == null) throw new IllegalArgumentException("The parameter bounds should not be null!!!");
            if (mBoundsf == null) mBoundsf = new RectF(bounds.left, bounds.top, bounds.right, bounds.bottom);
            else mBoundsf.set(bounds);

            this.setMaskBounds(mBoundsf);
        }

        // Used to set the mask range.
        private void setMaskBounds(RectF bounds) {
            float left, top, right, bottom;

            if (bounds == null)
                throw new IllegalArgumentException("The parameter bounds should not be null!!!");

            left = bounds.left + mStroke;
            top = bounds.top + mStroke;
            right = bounds.right - mStroke;
            bottom = bounds.bottom - mStroke;

            if (mMaskBounds == null) mMaskBounds = new RectF(left, top, right, bottom);
            else mMaskBounds.set(left, top, right, bottom);
        }

        /**
         * To update the current progress and trigger re-draw phase.
         * @param percent The percentage of the progress.
         */
        public void setSweepAngle(float percent) {
            mSweepAngle = mFullCircle * percent;
            invalidate();
        }

        /**
         * To set the width of the progress bar.
         * @param barWidth Should be between 0 and (outer-radius subs inner-radius).
         */
        public void setProgressBarWidth(float barWidth) {
            if (barWidth != mStroke) {
                mStroke = barWidth;

                // If mBoundsf already exist, it means the bar width changes dynamically, we should re-compute the mask bound.
                if (mBoundsf != null) {
                    this.setMaskBounds(mBoundsf);
                }
            }
        }
    }
}

