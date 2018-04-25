package com.htc.lib1.cc.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;

import com.htc.lib1.cc.R;

/**
 * HtcButton - The parent of HtcIconButton and HtcRimButton, please never use this directly.
 */
class HtcButton extends Button {

    @ExportedProperty(category = "CommonControl")
    private boolean mIsAnimating = false;

    private Paint mPressPaint = null, mColorOnPaint = null;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsContentMultiplyRequired, mDefaultContentMultiplySet;

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = HtcButtonUtil.BACKGROUND_MODE_LIGHT, to = "BACKGROUND_MODE_LIGHT"),
            @IntToString(from = HtcButtonUtil.BACKGROUND_MODE_DARK, to = "BACKGROUND_MODE_DARK"),
            @IntToString(from = HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVEDARK, to = "BACKGROUND_MODE_AUTOMOTIVEDARK"),
            @IntToString(from = HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVELIGHT, to = "BACKGROUND_MODE_AUTOMOTIVELIGHT"),
            @IntToString(from = HtcButtonUtil.BACKGROUND_MODE_COLORFUL, to = "BACKGROUND_MODE_COLORFUL")
    })
    private int mBackgroundMode;

    @ExportedProperty(category = "CommonControl")
    private boolean mStayInPress;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsSelectedColorOn;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsColorOn = false;

    private boolean mPressCanceledDueToMoveTooFar;

    @ExportedProperty(category = "CommonControl")
    private int mOverlayColor = -1;

    @ExportedProperty(category = "CommonControl")
    private int mCategoryColor = -1;
    private int mGravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = HtcButtonUtil.EXT_ANIMATE_NONE, to = "EXT_ANIMATE_NONE"),
            @IntToString(from = HtcButtonUtil.EXT_ANIMATE_NORIMMULTIPLY, to = "EXT_ANIMATE_NORIMMULTIPLY")
    })
    private int mExtAnimationMode = HtcButtonUtil.EXT_ANIMATE_NONE;
    private Drawable mFocusIndicator;

    @ExportedProperty(category = "CommonControl")
    private boolean mDrawFocusIndicator;

    //TODO: Review these need be keep?
    private boolean mDownAnimating;
    private boolean mUpAnimating;
    private int mCenterX;
    private int mCenterY;
    private boolean mBackgroundChange = false;
    private Mode mMaskMode = null;
    private boolean mIsRimMultiply = true;
    private ObjectAnimator mUpXScaleAnimator = null;
    private ObjectAnimator mUpYScaleAnimator = null;
    private float mMinScaleWidth = HtcButtonUtil.MIN_SCALE;
    private float mMinScaleHeight = HtcButtonUtil.MIN_SCALE;

    @ExportedProperty(category = "CommonControl")
    private boolean mUseDynamicScale = false;

    private AnimatorSet mMultiplyUpAnimatorSet = null;
    private final static int ANIMATION_DURATION_UP = ViewConfiguration.getTapTimeout() / 3;
    private static final boolean isLollipopMr1 = Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP;

    //A listenrt to let users know the state of up animaiton.
    private HtcButtonUtil.OnPressAnimationListener mPressAnimationListener;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcButton(Context context) {
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
    public HtcButton(Context context, AttributeSet attrs) {
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
    public HtcButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs != null) initWhenInflateFromXml(context, attrs, defStyle);
        else initWhenCreateFromJava(true, HtcButtonUtil.BACKGROUND_MODE_LIGHT, HtcButtonUtil.EXT_ANIMATE_NONE);
    }

    /**
     * Constructor to indicate the background mode and if need do mutiply/overlay to content.
     * If you create this button in java code, you should use this to assign the background mode and the overlay enabled or not.
     * @param context The Context the view is running in.
     * @param backgroundMode The background mode, default is Light mode.
     * @param isContentMultiply If need do multiply to content, default is true.
     */
    public HtcButton(Context context, int backgroundMode, boolean isContentMultiply) {
        this(context, backgroundMode, isContentMultiply, HtcButtonUtil.EXT_ANIMATE_NONE);
    }

    /**
     * Constructor to indicate the background mode and if need do mutiply/overlay to content.
     * If you create this button in java code, you should use this to assign the background mode and the overlay enabled or not.
     * @param context The Context the view is running in.
     * @param backgroundMode The background mode, default is Light mode.
     * @param isContentMultiply If need do multiply to content, default is true.
     * @param extAnimationMode If out-rim multiply needs to be played or not, default is true.
     */
    public HtcButton(Context context, int backgroundMode, boolean isContentMultiply, int extAnimationMode) {
        super(context, null, 0);
        initWhenCreateFromJava(isContentMultiply, backgroundMode, extAnimationMode);
    }

    private void initWhenInflateFromXml(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HtcCompoundButtonMode, defStyle, 0);
            mDefaultContentMultiplySet = a.getBoolean(R.styleable.HtcCompoundButtonMode_isContentMultiply, true);
            mIsContentMultiplyRequired = mDefaultContentMultiplySet;
            a.recycle();

            a = context.obtainStyledAttributes(attrs, R.styleable.HtcAnimationButtonMode, defStyle, 0);
            mBackgroundMode = a.getInt(R.styleable.HtcAnimationButtonMode_backgroundMode, HtcButtonUtil.BACKGROUND_MODE_LIGHT);
            mExtAnimationMode = a.getInt(R.styleable.HtcAnimationButtonMode_extAnimationMode, HtcButtonUtil.EXT_ANIMATE_NONE);
            a.recycle();

            a = context.obtainStyledAttributes(attrs, R.styleable.View, defStyle, 0);
            setFocusable(a.getBoolean(R.styleable.View_android_focusable, true));
            setClickable(a.getBoolean(R.styleable.View_android_clickable, true));
            a.recycle();

            a = context.obtainStyledAttributes(attrs, R.styleable.TextView);
            setMinWidth(a.getDimensionPixelSize(R.styleable.TextView_android_minWidth, -1));
            setMinHeight(a.getDimensionPixelSize(R.styleable.TextView_android_minHeight, -1));
            mGravity = a.getInt(R.styleable.TextView_android_gravity, mGravity);
            a.recycle();

            init(context, attrs);
        } else ;//TODO: Throw an exception
    }

    private void initWhenCreateFromJava(boolean isContentMultiply, int backgroundMode, int extAnimationMode) {
        mDefaultContentMultiplySet = mIsContentMultiplyRequired = isContentMultiply;
        mBackgroundMode = backgroundMode;
        mExtAnimationMode = extAnimationMode;

        setFocusable(true);
        setClickable(true);

        //For Widget.Holo.Button is 48dp*64dp
        setMinWidth(-1);
        setMinHeight(-1);

        init(getContext(), null);
    }

    private void init(Context context, AttributeSet attrs) {
        //set whether out-rim animation is enable or not
        setExtendAnimationFlag();

        mOverlayColor = HtcButtonUtil.getOverlayColor(context, attrs);
        mCategoryColor = HtcButtonUtil.getCategoryColor(context, attrs);

        setGravity(mGravity);

        initMultiplyAnimation();

        mPressPaint = new Paint();
        mPressPaint.setColor(mOverlayColor);
        mPressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        mMaskMode = PorterDuff.Mode.SRC_ATOP;
        mPressPaint.setAlpha(HtcButtonUtil.MIN_ALPHA);

        mColorOnPaint = new Paint(mPressPaint);
        mColorOnPaint.setColor(mCategoryColor);
        mColorOnPaint.setAlpha(HtcButtonUtil.MIN_ALPHA);

        mStayInPress = false;
        mIsSelectedColorOn = false;
        mDownAnimating = false;
        mUpAnimating = false;

        // About focus handling
        mFocusIndicator = context.getResources().getDrawable(com.htc.lib1.cc.R.drawable.common_focused);
        if (mFocusIndicator != null) {
            mFocusIndicator.mutate();
            mFocusIndicator.setColorFilter(mOverlayColor, PorterDuff.Mode.SRC_ATOP);
        }
    }

    /**
     * Set listener to get callbacked during button press animation.
     * @param listener The callback.
     * @hide
     */
    public void setOnPressAnimationListener(HtcButtonUtil.OnPressAnimationListener listener) {
        if (listener != null)
            mPressAnimationListener = listener;
    }

    private void setExtendAnimationFlag() {
        if (mExtAnimationMode == HtcButtonUtil.EXT_ANIMATE_NONE) {
            mIsRimMultiply = true;
        } else if ((mExtAnimationMode & HtcButtonUtil.EXT_ANIMATE_NORIMMULTIPLY) == HtcButtonUtil.EXT_ANIMATE_NORIMMULTIPLY) {
            mIsRimMultiply = false;
        }
    }

    // This method just only HtcFooterButton calls it currently.
    /**@hide*/
    protected final void setBackgroundMode(int backgroundMode) {
        if (mBackgroundMode == backgroundMode) return;

        mBackgroundMode = backgroundMode;

        int alpha = mPressPaint.getAlpha();
        mPressPaint.setColor(mOverlayColor);
        mPressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        mMaskMode = PorterDuff.Mode.SRC_ATOP;
        mPressPaint.setAlpha(alpha);

        invalidate();
    }

    /**@hide*/
    protected final int getBackgroundMode() { return mBackgroundMode; }
    /**@hide*/
    protected final int getOverlayColor() { return mOverlayColor; }
    /**@hide*/
    protected final int getCategoryColor() { return mCategoryColor; }
    /**@hide*/
    protected final Paint getOverlayPaint() { return mPressPaint; }
    /**@hide*/
    protected final boolean isAnimating() { return mIsAnimating; }
    /**@hide*/
    protected final boolean getDefaultContentMultiplyOn() { return mDefaultContentMultiplySet; }
    /**@hide**/
    protected final boolean isPressCanceledDueToMoveTooFar() { return mPressCanceledDueToMoveTooFar; }
    /**@hide*/
    final void setContentMultiplyOn(boolean enabled) { mIsContentMultiplyRequired = enabled; }
    /**@hide*/
    final boolean getContentMultiplyOn() { return mIsContentMultiplyRequired; }

    private void onDownAnimationStart() {
        mIsAnimating = true;
        mDownAnimating = true;
        mIsSelectedColorOn = (mColorOnPaint.getAlpha() == HtcButtonUtil.BASE_ALPHA);
        mPressPaint.setAlpha(HtcButtonUtil.MAX_ALPHA);
    }

    private void onDownAnimationEnd() {
        mDownAnimating = false;
    }

    private void onDownAnimationCancel() {
        mDownAnimating = false;
    }

    private void onUpAnimationStart() {
        mUpAnimating = true;
        if (mPressAnimationListener != null)
            mPressAnimationListener.onAnimationStarts(this);
    }

    private void onUpAnimationEnd() {
        mPressPaint.setAlpha(HtcButtonUtil.MIN_ALPHA);

        if (mIsColorOn) {
            mColorOnPaint.setAlpha(HtcButtonUtil.MAX_ALPHA);
        } else if (mStayInPress && mUpAnimating) {
            mColorOnPaint.setAlpha(!mIsSelectedColorOn ? HtcButtonUtil.MAX_ALPHA : HtcButtonUtil.MIN_ALPHA);
        }

        if (mPressAnimationListener != null && mUpAnimating)
            mPressAnimationListener.onAnimationEnds(this);

        mIsAnimating = false;
        mUpAnimating = false;
        mDownAnimating = false;
    }

    private void onUpAnimationCancel() {
        if (mPressAnimationListener != null && mUpAnimating)
            mPressAnimationListener.onAnimationCancels(this);

        mIsAnimating = false;
        mUpAnimating = false;
        mDownAnimating = false;
    }

    private void initMultiplyAnimation() {
        mUpXScaleAnimator = ObjectAnimator.ofFloat(this, "scaleWidth", HtcButtonUtil.MIN_SCALE, HtcButtonUtil.MAX_SCALE);
        mUpXScaleAnimator.setInterpolator(new DecelerateInterpolator());
        mUpXScaleAnimator.setDuration(ANIMATION_DURATION_UP);

        mUpYScaleAnimator = ObjectAnimator.ofFloat(this, "scaleHeight", HtcButtonUtil.MIN_SCALE, HtcButtonUtil.MAX_SCALE);
        mUpYScaleAnimator.setInterpolator(new DecelerateInterpolator());
        mUpYScaleAnimator.setDuration(ANIMATION_DURATION_UP);

        mMultiplyUpAnimatorSet = new AnimatorSet();
        mMultiplyUpAnimatorSet.playTogether(mUpXScaleAnimator, mUpYScaleAnimator);

        mMultiplyUpAnimatorSet.addListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animation) {
                onUpAnimationStart();
            }

            public void onAnimationRepeat(Animator animation) {}

            public void onAnimationEnd(Animator animation) {
                onUpAnimationEnd();
            }

            public void onAnimationCancel(Animator animation) {
                onUpAnimationCancel();
            }
        });
    }

    /**
     *@deprecated [Not use any more] The implementation has been removed, never use this method any more.
     */
    @Deprecated
    protected void setMultiplyAlpha(int alpha) {
    }

    /**@hide*/
    void setScaleWidth(float scale) {
        //setScaleX(scale); //Ahan20131017
    }

    /**@hide*/
    void setScaleHeight(float scale) {
        //setScaleY(scale); //Ahan20131017
    }

    /**
     * Sets the text color for all the states (normal, selected, focused) to be this color.
     * @param colors The ColorStateList of the text.
     * @hide
     */
    public void setTextColor(ColorStateList colors) {
        if (colors != null) {
            int restColor = colors.getColorForState(View.ENABLED_STATE_SET, colors.getDefaultColor());
            setTextColor(restColor);
        } else {
            super.setTextColor(colors);
        }
    }

    private void calculateScaleRate(int w, int h) {
        if (w >= h) {
            mMinScaleHeight = HtcButtonUtil.MIN_SCALE;
            mMinScaleWidth = 1.0f - (1.0f - HtcButtonUtil.MIN_SCALE) * h / w;
        } else {
            mMinScaleWidth = HtcButtonUtil.MIN_SCALE;
            mMinScaleHeight = 1.0f - (1.0f - HtcButtonUtil.MIN_SCALE) * w / h;
        }
    }

    private void resetScaleAnimation(int w, int h) {
        calculateScaleRate(w, h);

        mUpXScaleAnimator.setFloatValues(mMinScaleWidth, HtcButtonUtil.MAX_SCALE);
        mUpYScaleAnimator.setFloatValues(mMinScaleHeight, HtcButtonUtil.MAX_SCALE);
        mUpXScaleAnimator = ObjectAnimator.ofFloat(this, "scaleWidth", mMinScaleWidth, HtcButtonUtil.MAX_SCALE);
        mUpYScaleAnimator = ObjectAnimator.ofFloat(this, "scaleHeight", mMinScaleHeight, HtcButtonUtil.MAX_SCALE);
    }

    /**
     * Called when the size of this view has changed.
     * @param w Current width of this view.
     * @param h Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     * @hide
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w > 0 && h > 0 && (w != oldw || h != oldh)) {
            mCenterX = Math.round(w * 0.5f);
            mCenterY = Math.round(h * 0.5f);
            if (!mIsAnimating && mUseDynamicScale) resetScaleAnimation(w, h);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * To set this button is enabled or not.
     * @param enabled True to enable, false to disable.
     * @hide
     */
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) return;

        if (!enabled) {
            cancelAnimations();
            float alpha = (mBackgroundMode == HtcButtonUtil.BACKGROUND_MODE_DARK || mBackgroundMode == HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVEDARK) ?
                HtcButtonUtil.DISABLE_ALPHA_DARK : HtcButtonUtil.DISABLE_ALPHA;
            setAlpha(alpha);
        } else {
            setAlpha(HtcButtonUtil.VISIBLE_ALPHA);
        }
        super.setEnabled(enabled);
    }

    private void handelUpEventWithAnimation() {
        if (mIsAnimating) {
            onDownAnimationEnd();
            //mMultiplyUpAnimatorSet.start(); //Ahan20131017
            onUpAnimationStart(); //Ahan20131017
            onUpAnimationEnd(); //Ahan20131017
            invalidate();
        }
    }

    private void handleTouchEventWithAnimation(MotionEvent event) {
        if (!isEnabled() || !isClickable()) return;

        switch (event.getAction()) {
        case MotionEvent.ACTION_UP:
            if (isEnabled()) handelUpEventWithAnimation();
            break;
        case MotionEvent.ACTION_DOWN:
            if (isEnabled()) {
                mPressCanceledDueToMoveTooFar = false;
                removePropertyDown();
            }
            break;
        case MotionEvent.ACTION_CANCEL:
            cancelAnimations();
            break;
        case MotionEvent.ACTION_MOVE:
            final int x = (int) event.getX();
            final int y = (int) event.getY();

            // Be lenient about moving outside of buttons
            int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
            if ((x < 0 - slop) || (x >= getWidth() + slop) ||
                (y < 0 - slop) || (y >= getHeight() + slop)) {
                if (mIsAnimating) {
                    cancelAnimations();
                    mPressCanceledDueToMoveTooFar = true;
                }
            }
            break;
        }
    }

    private void cancelAnimations() {
        if (mMultiplyUpAnimatorSet != null) {
            onDownAnimationCancel();
            if (mIsAnimating) cancelEvent();
            onUpAnimationCancel();
        }
    }

    private void removePropertyDown() {
        //mMultiplyUpAnimatorSet.end(); //Ahan20131017
        onUpAnimationEnd(); //Ahan20131017
        onDownAnimationStart();
        mPressPaint.setAlpha(HtcButtonUtil.MAX_ALPHA);
        invalidate();
        setScaleWidth(mMinScaleWidth);
        setScaleHeight(mMinScaleHeight);
        onDownAnimationEnd();
    }

    private void removePropertyUp() {
        onDownAnimationEnd();
        //mMultiplyUpAnimatorSet.start(); //Ahan20131017
        onUpAnimationStart(); //Ahan20131017
        //mMultiplyUpAnimatorSet.end(); //Ahan20131017
        onUpAnimationEnd(); //Ahan20131017
    }

    private void cancelPropertyMove() {
        cancelAnimations();
    }

    private void handleTouchEventWithoutAnimation(MotionEvent event){
        if (isEnabled() && isClickable()) {
            switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (isEnabled() && mIsAnimating) {
                    removePropertyUp();
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if (isEnabled()) {
                    removePropertyDown();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                cancelPropertyMove();
                break;
            case MotionEvent.ACTION_MOVE:
                final int x = (int) event.getX();
                final int y = (int) event.getY();
                // Be lenient about moving outside of buttons
                int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if ((x < 0 - slop) || (x >= getWidth() + slop) ||
                    (y < 0 - slop) || (y >= getHeight() + slop)) {
                    cancelPropertyMove();
                }
                break;
            }
        }
    }

    /**
     * Called when a hardware key down event occurs.
     * @param keyCode A key code that represents the button pressed, from KeyEvent.
     * @param event The KeyEvent object that defines the button action.
     * @return If you handled the event, return true. If you want to allow the event to be handled by the next receiver, return false.
     * @hide
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_ENTER:
            if (HtcButtonUtil.getEnableAnimation()) {
                if (!isEnabled() || !isClickable()) break;
                if (isEnabled()) removePropertyDown();
            } else {
                if (!isEnabled() || !isClickable()) break;
                if (isEnabled()) removePropertyDown();
            }
            break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Called when a hardware key up event occurs.
     * @param keyCode A key code that represents the button pressed, from KeyEvent.
     * @param event The KeyEvent object that defines the button action.
     * @return If you handled the event, return true. If you want to allow the event to be handled by the next receiver, return false.
     * @hide
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_ENTER:
            if (HtcButtonUtil.getEnableAnimation()) {
                if (!isEnabled() || !isClickable()) break;
                if (isEnabled()) handelUpEventWithAnimation();
            } else {
                if (!isEnabled() || !isClickable()) break;
                if (isEnabled()) { if (mIsAnimating) removePropertyUp(); }
            }
            break;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * Call this view's OnClickListener, if it is defined.
     * @return True there was an assigned OnClickListener that was called, false otherwise is returned.
     * @deprecated [not use any longer] This just call super, will be removed in next Sense.
     */
    @Deprecated
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * Call this view's OnLongClickListener, if it is defined.
     * @return True if one of the above receivers consumed the event, false otherwise.
     * @deprecated [not use any longer] This just call super, will be removed in next Sense.
     */
    @Deprecated
    public boolean performLongClick() {
        return super.performLongClick();
    }

    /**
     * Called when a touch screen motion event occurs.
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     * @hide
     */
    public boolean onTouchEvent(MotionEvent event) {
        HtcButtonUtil.setEnableAnimation(true);

        if (HtcButtonUtil.getEnableAnimation()) {
            handleTouchEventWithAnimation(event);
        } else {
            handleTouchEventWithoutAnimation(event);
        }

        return super.onTouchEvent(event);
    }

    private void cancelEvent() {
        mIsAnimating = false;
        setScaleWidth(HtcButtonUtil.MAX_SCALE);
        setScaleHeight(HtcButtonUtil.MAX_SCALE);
        mPressPaint.setAlpha(HtcButtonUtil.MIN_ALPHA);
        invalidate();
    }

    /**
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     */
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mBackgroundChange = true;
    }

    @ExportedProperty(category = "CommonControl")
    private float mShadowRadius, mShadowDx, mShadowDy;

    @ExportedProperty(category = "CommonControl")
    private int mShadowColor;

    /**
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     */
    public void setShadowLayer(float radius, float dx, float dy, int color) {
        mShadowRadius = radius;
        mShadowDx = dx;
        mShadowDy = dy;
        mShadowColor = color;

        super.setShadowLayer(radius, dx, dy, color);
    }

    /**
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     */
    protected void onDraw(Canvas canvas) {
        Rect canvasRect = canvas.getClipBounds();
        if (mIsAnimating || (mColorOnPaint.getAlpha()!=HtcButtonUtil.MIN_ALPHA)) {
            int sc = 0;
            if (mIsColorOn || mIsContentMultiplyRequired) {
                safeDraw(canvas, canvasRect);
                sc = canvas.saveLayer(getScrollX(), getScrollY(), getScrollX() + getWidth(), getScrollY() + getHeight(), null, Canvas.ALL_SAVE_FLAG);
                getPaint().setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, Color.TRANSPARENT);
            }

            safeDraw(canvas, canvasRect);

            if (mIsColorOn || mIsContentMultiplyRequired) {
                canvas.drawColor((mIsAnimating && mIsContentMultiplyRequired ? mOverlayColor : mCategoryColor), PorterDuff.Mode.SRC_ATOP);
                getPaint().setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mShadowColor);
                canvas.restoreToCount(sc);
            }
        } else {
            safeDraw(canvas, canvasRect);
        }

        if (mDrawFocusIndicator && mFocusIndicator!=null) {
            mFocusIndicator.setBounds(canvas.getClipBounds());
            mFocusIndicator.draw(canvas);
        }
    }

    /*
     * In order to remove copy code.
     */
    @SuppressLint("WrongCall")
    private void safeDraw(Canvas canvas, Rect canvasRect) {
        if (isLollipopMr1) {
            canvas.save();
            final int right = getWidth() - getCompoundPaddingRight() + getScrollX();
            canvas.clipRect(canvasRect.left, canvasRect.top, right, canvasRect.bottom);
            super.onDraw(canvas);
            canvas.restore();
        } else {
            super.onDraw(canvas);
        }
    }

    /**@hide*/
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (mIsAnimating) cancelEvent();
        super.onWindowFocusChanged(hasWindowFocus);
    }

    /**@hide*/
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (mIsAnimating) cancelEvent();
        mDrawFocusIndicator = gainFocus;
        if (!isEnabled()) {
            boolean isDarkMode = (mBackgroundMode == HtcButtonUtil.BACKGROUND_MODE_DARK) || (mBackgroundMode == HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVEDARK);
            setAlpha((mDrawFocusIndicator || !isDarkMode) ? HtcButtonUtil.DISABLE_ALPHA : HtcButtonUtil.DISABLE_ALPHA_DARK);
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    /**
     * To enable/disable stayInPress, the content will still keep multiply/overlay color after finger up and clear color after next finger up.
     * @param stay True to enable, false to disable.
     * @hide
     */
    public void stayInPress(boolean stay) {
        mStayInPress = stay;
    }

    /**@hide*/
    protected final boolean isStayInPress() { return mStayInPress; }

    /**
     * To enable/disable colorOn, the content will always keep multiply/overlay color.
     * @param on True to enable, false to disable.
     * @hide
     */
    public void setColorOn(boolean on) {
        boolean current_on = (mColorOnPaint.getAlpha() == HtcButtonUtil.BASE_ALPHA);
        if (current_on != on) {
            if (on) mColorOnPaint.setAlpha(HtcButtonUtil.BASE_ALPHA);
            else mColorOnPaint.setAlpha(HtcButtonUtil.MIN_ALPHA);
            invalidate();
        }

        mIsColorOn = on;
    }

    /**@hide*/
    protected final boolean isColorOnSet() { return mIsColorOn; }

    /**@hide*/
    public void setCustomOverlayColor(int color) {
        int alpha = mPressPaint.getAlpha();
        mOverlayColor = color;
        mPressPaint.setColor(mOverlayColor);
        mPressPaint.setAlpha(alpha);
        invalidate();
    }

    /**@hide*/
    public void setCustomCategoryColor(int color) {
        int alpha = mColorOnPaint.getAlpha();
        mCategoryColor = color;
        mColorOnPaint.setColor(mCategoryColor);
        mColorOnPaint.setAlpha(alpha);
        invalidate();
    }
}
