package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewDebug.ExportedProperty;
import android.graphics.Bitmap;

import com.htc.lib1.cc.R;

/**
 * HtcRimImageButton
 */
public class HtcRimImageButton extends HtcImageButton {
    private float mCurrentScaleWidth;
    private float mCurrentScaleHeight;

    private int mCenterX = 0;
    private int mCenterY = 0;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsPaddingSetInXML = false;

    private BackgroundDrawable mBackground;

    @ExportedProperty(category = "CommonControl")
    private boolean mUseSelectorWhenPressed = false;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcRimImageButton(Context context) {
        super(context, null);
        init(context);
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
    public HtcRimImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        init(context, attrs, 0);
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
    public HtcRimImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        init(context, attrs, defStyle);
    }

    /**
     * Constructor to indicate the background mode and if need do mutiply to content.
     * @param context The Context the view is running in.
     * @param backgroundMode The background mode, default is Light mode.
     * @param isContentMultiply If need do multiply to content, default is true.
     */
    public HtcRimImageButton(Context context, int backgroundMode, boolean isContentMultiply) {
        super(context, backgroundMode, isContentMultiply);
        init(context);
    }

    /**
     * Constructor to indicate the background mode and if need do mutiply to content.
     * @param context The Context the view is running in.
     * @param backgroundMode The background mode, default is Light mode.
     * @param isContentMultiply If need do multiply to content, default is true.
     * @param extAnimationMode If out-rim multiply needs to be played or not, default is true.
     */
    public HtcRimImageButton(Context context, int backgroundMode, boolean isContentMultiply, int extAnimationMode) {
        super(context, backgroundMode, isContentMultiply, extAnimationMode);
        init(context);
    }

    private void init(Context context, AttributeSet attrs, int defStyle){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.View, defStyle, 0);

        int padding = a.getDimensionPixelSize(R.styleable.View_android_padding, -1);
        int leftPadding = a.getDimensionPixelSize(R.styleable.View_android_paddingLeft, -1);
        int topPadding = a.getDimensionPixelSize(R.styleable.View_android_paddingTop, -1);
        int rightPadding = a.getDimensionPixelSize(R.styleable.View_android_paddingRight, -1);
        int bottomPadding = a.getDimensionPixelSize(R.styleable.View_android_paddingBottom, -1);

        a.recycle();

        mIsPaddingSetInXML = (padding != -1 || leftPadding != -1 || topPadding != -1 || rightPadding != -1 || bottomPadding != -1);

        if (padding >= 0) {
            leftPadding = padding;
            topPadding = padding;
            rightPadding = padding;
            bottomPadding = padding;
        }

        setPadding(leftPadding >= 0 ? leftPadding : getPaddingLeft(),
                topPadding >= 0 ? topPadding : getPaddingTop(),
                rightPadding >= 0 ? rightPadding : getPaddingRight(),
                bottomPadding >= 0 ? bottomPadding : getPaddingBottom());
    }

    private void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable d = null;
            int backgroundMode = getBackgroundMode();
            if (backgroundMode == HtcButtonUtil.BACKGROUND_MODE_DARK) {
                d = context.getResources().getDrawable(
                        R.drawable.button_selector_dark);
            } else {
                d = context.getResources().getDrawable(
                        R.drawable.button_selector_light);
            }
            if (null != d) {
                setBackground(d);
                setContentMultiplyOn(false);
            }
        } else {
            // Init the default background.
            useSelectorWhenPressed(true);
        }
    }

    /**
     * This method does not take effect and just return null now.
     * @param context context
     * @param drs drs
     * @param defBkg defBkg
     * @return Just null
     * @deprecated [Not use any longer] Please never use this API and it will be removed soon.
     * @hide
     */
    public static Bitmap[] compositeBitmap(Context context, Drawable[] drs, boolean defBkg) {
        return null;
    }

    private static Drawable[] loadSkinDrawables(Context context, AttributeSet attrs, int defStyle, int mode) {
        return null;
    }

    private static void setCompositeScale(float ratio, Drawable[] drs) {
    }

    void setScaleWidth(float scale) {
    }

    void setScaleHeight(float scale) {
    }

    /**@hide*/
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w > 0 && h > 0 && (w != oldw || h != oldh)) {
            mCenterX = Math.round(w * 0.5f);
            mCenterY = Math.round(h * 0.5f);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**@hide*/
    protected void onDraw(Canvas canvas) {
        Rect rect = canvas.getClipBounds();
        mCenterX = (rect.left + rect.right) / 2;
        mCenterY = (rect.top + rect.bottom) / 2;

        super.onDraw(canvas);
    }

    /**
     * To set the drawables for out-rim, pressed-state, inner-background for button background.
     * @param background_outer The drawable object for out-rim background, set null if no needs.
     * @param background_press The drawable object for press-state background, set null if no needs.
     * @param background The drawable object for inner-background, this can not be null.
     */
    public void setButtonBackgroundDrawable(Drawable background_outer, Drawable background_press, Drawable background) {
        if (/*background_press != null && background_press != null &&*/ background != null) {
            if (!mIsPaddingSetInXML) setPadding(0, 0, 0, 0);
            if (mBackground == null) mBackground = new BackgroundDrawable(getContext());
            mBackground.setDrawables(background_outer, background_press, background);
            super.setBackgroundDrawable(mBackground);
        } else {
            throw new RuntimeException("Drawable background can't be NULL");
        }
    }

    /**
     * To set the resource id for out-rim, pressed-state, inner-background for button background.
     * @param background_outer The resource id for out-rim background, set 0 if no need.
     * @param background_press The resource id for press-state background, set 0 if no needs
     * @param background The resource id for inner-background, this can not be 0.
     */
    public void setButtonBackgroundResource(int background_outer, int background_press, int background) {
        Drawable bp = (background_press==0 ? null : getContext().getResources().getDrawable(background_press));
        Drawable b = (background==0 ? null : getContext().getResources().getDrawable(background));
        Drawable bo = (background_outer==0 ? null : getContext().getResources().getDrawable(background_outer));
        setButtonBackgroundDrawable(bo, bp, b);
    }

    /**@hide*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);

        switch (event.getAction()) {
        case MotionEvent.ACTION_UP:
            if (isStayInPress() && !isPressCanceledDueToMoveTooFar())
                setColorOn(!isColorOnSet());
            break;
        }

        return ret;
    }

    /**
     * To set the BackgroundDrawable object for this button.
     * @param d The BackgroundDrawable object, it shuold be instance of BackgroundDrawable or the background will be set to null.
     * @hide
     */
    public void setBackgroundDrawable(Drawable background) {
        if (mUseSelectorWhenPressed && mBackground != null) {
            if (background instanceof BackgroundDrawable)
                mBackground = (BackgroundDrawable) background;
            else
                mBackground.setRestDrawable(background);
        }
        super.setBackgroundDrawable(mUseSelectorWhenPressed ? mBackground : background);
    }

    /**@hide*/
    @Override
    public void useSelectorWhenPressed(boolean enabled) {
        Drawable tmp = getBackground();

        if (tmp instanceof BackgroundDrawable) {
            mBackground = (BackgroundDrawable)tmp;
        } else if (mBackground == null) {
            mBackground = new BackgroundDrawable(getContext());
            if (!enabled && tmp != null) //if ture, it means user set bkg in xml, we should keep it.
                mBackground.setRestDrawable(tmp);
        } else {
            mBackground.setRestDrawable(tmp);
        }

        mUseSelectorWhenPressed = enabled;
        setBackground(enabled ? mBackground : mBackground.getRestDrawable());
        setContentMultiplyOn(!enabled && getDefaultContentMultiplyOn());
    }

    private class BackgroundDrawable extends Drawable {
        private Drawable mPressed, mDefaultPressed;
        private Drawable mRest;
        private int mCenterXP, mCenterYP;
        private int mWidth, mHeight;
        private int mSelectorColor;

        /**@hide*/
        public BackgroundDrawable(Context context) {
            this(context, null, com.htc.lib1.cc.R.style.HtcButton);
        }

        /**@hide*/
        public BackgroundDrawable(Context context, AttributeSet attrs, int defStyle) {
            int backMode = getBackgroundMode();
            boolean isDarkMode = HtcButtonUtil.isDarkMode(backMode);

            mSelectorColor = HtcButtonUtil.getSelectorColor(backMode);

            mRest = (isDarkMode ?
                HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_B_BUTTON_REST) :
                HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_BUTTON_REST)).mutate();

            mDefaultPressed = new android.graphics.drawable.ColorDrawable(mSelectorColor);
            mPressed = mDefaultPressed;
        }

        /**@hide*/
        public void setRestDrawable(Drawable rest) {
            mRest = rest;
        }

        /**@hide*/
        public Drawable getRestDrawable() {
            return mRest;
        }

        /**@hide*/
        public void setAlpha(int alpha) {
            if (mRest != null) mRest.setAlpha(alpha);
        }

        /**@hide*/
        public void setScale(float scale_width, float scale_height, int drawable) {
            int width = Math.round(mWidth * scale_width) / 2;
            int height = Math.round(mHeight * scale_height) / 2;
        }

      //TODO: Review this method to fit selector.
        /**@hide*/
        public void setDrawables(Drawable outer, Drawable press, Drawable background) {
            Rect rect;
            boolean isDarkMode = (getBackgroundMode()==HtcButtonUtil.BACKGROUND_MODE_DARK);

            if (mPressed != null && press != null) {
                rect = mPressed.getBounds();
                mPressed = press;
                mPressed.mutate();
                mPressed.setColorFilter(mSelectorColor, PorterDuff.Mode.SRC_ATOP);
                mPressed.setBounds(rect);
            }

            if (mRest != null && background != null) {
                rect = mRest.getBounds();
                mRest = background;
                mRest.mutate();
                mRest.setBounds(rect);
            }

            invalidate();
        }

        /**@hide*/
        public boolean getPadding(Rect padding) {
            if (padding == null) return false;

            if (mRest != null) { return mRest.getPadding(padding); }
            else if (mPressed != null) { return mPressed.getPadding(padding); }
            else {
                padding.left = padding.top = padding.right = padding.bottom = 0;
                return false;
            }
        }

        /**@hide*/
        @Override
        public void draw(Canvas canvas) {
            drawRest(canvas);
            drawPressed(canvas);
        }

        void drawRest(Canvas canvas) {
            if (mRest != null) mRest.draw(canvas);
        }

        void drawPressed(Canvas canvas) {
            if (isAnimating() && mPressed != null) mPressed.draw(canvas);
        }

        /**@hide*/
        @Override
        public int getOpacity() {
            return PixelFormat.RGBA_8888;
        }

        /**@hide*/
        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            if (mRest != null) {
                mRest.setColorFilter(colorFilter);
            }
        }

        /**@hide*/
        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);

            if (mPressed != null) mPressed.setBounds(bounds);
            if (mRest != null) mRest.setBounds(bounds);

            mCenterXP = (bounds.left + bounds.right) / 2;
            mCenterYP = (bounds.top + bounds.bottom) / 2;
            mWidth = bounds.right - bounds.left;
            mHeight = bounds.bottom - bounds.top;
        }

        /**@hide*/
        protected boolean onStateChange(int[] state) {
            return super.onStateChange(state);
        }

        /**@hide*/
        @Override
        public boolean isStateful() {
            return true;
        }

        /**@hide*/
        @Override
        public int getIntrinsicWidth() {
            if (mRest != null) return mRest.getIntrinsicWidth();
            else if (mPressed != null) return mPressed.getIntrinsicWidth();
            else return 0;
        }

        /**@hide*/
        public int getIntrinsicHeight() {
            if (mRest != null) return mRest.getIntrinsicHeight();
            else if (mPressed != null) return mPressed.getIntrinsicHeight();
            else return 0;
        }
    }
}
