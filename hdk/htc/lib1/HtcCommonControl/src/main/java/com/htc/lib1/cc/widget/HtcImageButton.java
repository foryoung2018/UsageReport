package com.htc.lib1.cc.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import com.htc.lib1.cc.R;

/**
 * HtcImageButton
 */
public class HtcImageButton extends ImageButton {
    //Animation Related
    private AnimatorSet mMultiplyUpAnimatorSet = null;
    private final static int ANIMATION_DURATION_UP = ViewConfiguration.getTapTimeout() / 3;

    private boolean mIsAnimating = false;
    private Paint mMultiplyPaint = null, mColorOnPaint = null;

    private boolean mIsContentMultiplyRequired, mDefaultContentMultiplySet;

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = HtcButtonUtil.BACKGROUND_MODE_LIGHT, to = "BACKGROUND_MODE_LIGHT"),
            @IntToString(from = HtcButtonUtil.BACKGROUND_MODE_DARK, to = "BACKGROUND_MODE_DARK"),
            @IntToString(from = HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVEDARK, to = "BACKGROUND_MODE_AUTOMOTIVEDARK"),
            @IntToString(from = HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVELIGHT, to = "BACKGROUND_MODE_AUTOMOTIVELIGHT"),
            @IntToString(from = HtcButtonUtil.BACKGROUND_MODE_COLORFUL, to = "BACKGROUND_MODE_COLORFUL")
    })
    private int mBackgroundMode;

    //>> stay in green button
    @ExportedProperty(category = "CommonControl")
    private boolean mStayInPress;


    @ExportedProperty(category = "CommonControl")
    private boolean mIsGreenOn;
    private boolean mDownAnimating;
    private boolean mUpAnimating;

    @ExportedProperty(category = "CommonControl")
    private boolean mPressCanceledDueToMoveTooFar;
    //<<

    private int mCenterX;
    private int mCenterY;

    private boolean mBackgroundChange = false;
    private boolean mUseSelectorWhenPressed = false;
    private BackgroundDrawable mBackground;
    private Mode mMaskMode = null;

    @ExportedProperty(category = "CommonControl")
    private int mCategoryColor = -1;

    @ExportedProperty(category = "CommonControl")
    private int mOverlayColor = -1;

    private ColorFilter mOriginalColorFilter = null;

    @ExportedProperty(category = "CommonControl")
    private float mOriginalAlpha = HtcButtonUtil.VISIBLE_ALPHA;

    private ObjectAnimator mUpXScaleAnimator = null;
    private ObjectAnimator mUpYScaleAnimator = null;

    @ExportedProperty(category = "CommonControl")
    private float mMinScaleWidth = HtcButtonUtil.MIN_SCALE;

    @ExportedProperty(category = "CommonControl")
    private float mMinScaleHeight = HtcButtonUtil.MIN_SCALE;

    @ExportedProperty(category = "CommonControl")
    private boolean mUseDynamicScale = false;

    //>> extend animation mode
    private int mExtAnimationMode = HtcButtonUtil.EXT_ANIMATE_NONE;
    private boolean mIsRimMultiply = true;
    //<<

    @ExportedProperty(category = "CommonControl")
    private boolean mIsColorOn = false;

    //Followings are default style of ImageButton

    @ExportedProperty(category = "CommonControl")
    private int mScaleTypeIndex = -1;
    private static ScaleType[] mScaleTypes = ScaleType.values();

    @ExportedProperty(category = "CommonControl")
    private boolean mClickable = true, mFocusable = true;
    //End of default style of ImageButton

    private Drawable mFocusIndicator;

    @ExportedProperty(category = "CommonControl")
    private boolean mDrawFocusIndicator;

    private HtcButtonUtil.OnPressAnimationListener mPressAnimationListener;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcImageButton(Context context) {
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
    public HtcImageButton(Context context, AttributeSet attrs) {
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
    public HtcImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    /**
     * Constructor to indicate the background mode and if need do mutiply to content.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param backgroundMode The background mode, default is Light mode.
     * @param isContentMultiply If need do multiply to content, default is true.
     */
    public HtcImageButton(Context context, int backgroundMode, boolean isContentMultiply) {
        this(context, backgroundMode, isContentMultiply, HtcButtonUtil.EXT_ANIMATE_NONE);
    }

    /**
     * Constructor to indicate the background mode and if need do mutiply to content.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param backgroundMode The background mode, default is Light mode.
     * @param isContentMultiply If need do multiply to content, default is true.
     * @param extAnimationMode If out-rim multiply needs to be played or not, default is true.
     */
    public HtcImageButton(Context context, int backgroundMode, boolean isContentMultiply, int extAnimationMode) {
        super(context, null, 0);

        mDefaultContentMultiplySet = mIsContentMultiplyRequired = isContentMultiply;
        mBackgroundMode = backgroundMode;
        mExtAnimationMode = extAnimationMode;

        setBackgroundDrawable(null);
        setPadding(0, 0, 0, 0);

        init(context, null);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HtcCompoundButtonMode, defStyle, 0);
            mIsContentMultiplyRequired = a.getBoolean(R.styleable.HtcCompoundButtonMode_isContentMultiply, true);
            mDefaultContentMultiplySet = mIsContentMultiplyRequired;
            a.recycle();

            a = context.obtainStyledAttributes(attrs, R.styleable.HtcAnimationButtonMode, defStyle, 0);
            mBackgroundMode = a.getInt(R.styleable.HtcAnimationButtonMode_backgroundMode, HtcButtonUtil.BACKGROUND_MODE_LIGHT);
            mExtAnimationMode = a.getInt(R.styleable.HtcAnimationButtonMode_extAnimationMode, HtcButtonUtil.EXT_ANIMATE_NONE);
            a.recycle();

            a = context.obtainStyledAttributes(attrs, R.styleable.View, defStyle, 0);
            mClickable = a.getBoolean(R.styleable.View_android_clickable, mClickable);
            mFocusable = a.getBoolean(R.styleable.View_android_focusable, mFocusable);
            a.recycle();

            a = context.obtainStyledAttributes(attrs, R.styleable.ImageView, defStyle, 0);
            mScaleTypeIndex = a.getInt(R.styleable.ImageView_android_scaleType, mScaleTypeIndex);
            a.recycle();
        } else {
            mDefaultContentMultiplySet = mIsContentMultiplyRequired = true;
            mBackgroundMode = HtcButtonUtil.BACKGROUND_MODE_LIGHT;
            mExtAnimationMode = HtcButtonUtil.EXT_ANIMATE_NONE;
            setPadding(0, 0, 0, 0);
        }

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mOverlayColor = HtcButtonUtil.getOverlayColor(context, attrs);
        mCategoryColor = HtcButtonUtil.getCategoryColor(context, attrs);

        initMultiplyAnimation();

        mMultiplyPaint = new Paint();
        mMultiplyPaint.setColor(mOverlayColor);
        mMultiplyPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        mMaskMode = PorterDuff.Mode.SRC_ATOP;
        mMultiplyPaint.setAlpha(HtcButtonUtil.MIN_ALPHA);

        mColorOnPaint = new Paint(mMultiplyPaint);
        mColorOnPaint.setColor(mCategoryColor);
        mColorOnPaint.setAlpha(HtcButtonUtil.MIN_ALPHA);

        mStayInPress = false;
        mIsGreenOn = false;
        mDownAnimating = false;
        mUpAnimating = false;
        mOriginalColorFilter = null;

        setScaleType((mScaleTypes!=null && mScaleTypeIndex>=0 && mScaleTypeIndex<mScaleTypes.length) ? mScaleTypes[mScaleTypeIndex] : ScaleType.CENTER);
        setClickable(mClickable);
        setFocusable(mFocusable);

        //Add By Ahan 20131022 to implement new press effect
        useSelectorWhenPressed(false);

        // About focus handling
        mFocusIndicator = context.getResources().getDrawable(com.htc.lib1.cc.R.drawable.common_focused);
        if (mFocusIndicator != null) {
            mFocusIndicator.mutate();
            mFocusIndicator.setColorFilter(mOverlayColor, PorterDuff.Mode.SRC_ATOP);
        }
    }

    public void setOnPressAnimationListener(HtcButtonUtil.OnPressAnimationListener listener) {
        if (listener != null)
            mPressAnimationListener = listener;
    }

    protected final boolean isPressCanceledDueToMoveTooFar() { return mPressCanceledDueToMoveTooFar; }

    void onDownAnimationStart(){
        mIsAnimating = true;
        mDownAnimating = true;
        mIsGreenOn = (mColorOnPaint.getAlpha() == HtcButtonUtil.BASE_ALPHA);
        setMultiplyAlpha(HtcButtonUtil.MAX_ALPHA);
        Drawable d = getDrawable();
        if (d != null) setImageDrawable(d.mutate());
    }

    void onDownAnimationEnd() {
        mDownAnimating = false;
    }

    void onDownAnimationCancel() {
        mDownAnimating = false;
    }

    void onUpAnimationStart() {
        mUpAnimating = true;
        if (mPressAnimationListener != null)
            mPressAnimationListener.onAnimationStarts(this);
    }

    void onUpAnimationEnd() {
        mIsAnimating = false;
        mUpAnimating = false;
        mDownAnimating = false;

        setMultiplyAlpha(HtcButtonUtil.MIN_ALPHA);

        if (mStayInPress) {
            if (mIsGreenOn) {
                mColorOnPaint.setAlpha(HtcButtonUtil.MIN_ALPHA);
            } else {
                mColorOnPaint.setAlpha(HtcButtonUtil.MAX_ALPHA);
            }
        }

        if (mPressAnimationListener != null)
            mPressAnimationListener.onAnimationEnds(this);
    }

    void onUpAnimationCancel() {
        mIsAnimating = false;
        mUpAnimating = false;
        mDownAnimating = false;
        if (mPressAnimationListener != null)
            mPressAnimationListener.onAnimationCancels(this);
    }

    private void initMultiplyAnimation() {
        mUpXScaleAnimator = ObjectAnimator.ofFloat(this, "scaleWidth", mMinScaleWidth, HtcButtonUtil.MAX_SCALE);
        mUpXScaleAnimator.setInterpolator(new DecelerateInterpolator());
        mUpXScaleAnimator.setDuration(ANIMATION_DURATION_UP);

        mUpYScaleAnimator = ObjectAnimator.ofFloat(this, "scaleHeight", mMinScaleHeight, HtcButtonUtil.MAX_SCALE);
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

    void setMultiplyAlpha(int alpha) {
        mMultiplyPaint.setAlpha(alpha);
        invalidate();
    }

    void setScaleWidth(float scale) {
        //setScaleX(scale);
    }

    void setScaleHeight(float scale) {
        //setScaleY(scale);
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

    private void resetScaleAnimation(int w, int h){
        calculateScaleRate(w, h);
        mUpXScaleAnimator.setFloatValues(mMinScaleWidth, HtcButtonUtil.MAX_SCALE);
        mUpYScaleAnimator.setFloatValues(mMinScaleHeight, HtcButtonUtil.MAX_SCALE);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w > 0 && h > 0 && (w != oldw || h != oldh)) {
            mCenterX = Math.round(w * 0.5f);
            mCenterY = Math.round(h * 0.5f);
            if(!mIsAnimating && mUseDynamicScale)resetScaleAnimation(w, h);
        }

        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * To set this button is enabled or not.
     * @param enabled True to enable, false to disable.
     */
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) return;
        if (!enabled) {
            if (mIsAnimating) cancelAnimations();
            float alpha = (mBackgroundMode == HtcButtonUtil.BACKGROUND_MODE_DARK || mBackgroundMode == HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVEDARK) ?
                HtcButtonUtil.DISABLE_ALPHA_DARK : HtcButtonUtil.DISABLE_ALPHA;
            setAlpha(alpha);
        } else {
            super.setAlpha(mOriginalAlpha);
        }
        super.setEnabled(enabled);
    }

   /**
    * To set alpha of the drawable
    * @param alpha The new alpha
    * @hide
    */
   public void setAlpha(float alpha) {
        if (alpha != getAlpha()) {
            mOriginalAlpha = getAlpha();
            super.setAlpha(alpha);
        }
    }

    private void handelUpEventWithAnimation() {
        if (mIsAnimating) {
            onDownAnimationEnd();
            mMultiplyUpAnimatorSet.start();
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
            cancelEvent();
            onUpAnimationCancel();
        }
    }

    private void removePropertyDown() {
        mMultiplyUpAnimatorSet.end();
        onDownAnimationStart();
        setScaleWidth(mMinScaleWidth);
        setScaleHeight(mMinScaleHeight);
        onDownAnimationEnd();
    }

    private void removePropertyUp() {
        onDownAnimationEnd();
        mMultiplyUpAnimatorSet.start();
        mMultiplyUpAnimatorSet.end();
    }

    private void cancelPropertyMove() {
        cancelAnimations();
    }

    private void handleTouchEventWithoutAnimation(MotionEvent event) {
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
                    if (mIsAnimating) cancelPropertyMove();
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
        case KeyEvent.KEYCODE_ENTER: {
            if (HtcButtonUtil.getEnableAnimation()) {
                if (!isEnabled() || !isClickable()) break;
                if (isEnabled()) {removePropertyDown();}
                break;
            } else {
                if (!isEnabled() || !isClickable()) break;
                if (isEnabled()) {removePropertyDown();}
            }
        }
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Called when a hardware key up event occurs.
     * @param keyCode A key code that represents the button pressed, from KeyEvent.
     * @param event The KeyEvent object that defines the button action.
     * @return If you handled the event, return true. If you want to allow the event to be handled by the next receiver, return false.
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_ENTER: {
            if (HtcButtonUtil.getEnableAnimation()) {
                if (!isEnabled() || !isClickable()) break;
                if (isEnabled()) {handelUpEventWithAnimation();}
                break;
            } else {
                if (!isEnabled() || !isClickable()) break;
                if (isEnabled()) {
                    if (mIsAnimating) {
                        removePropertyUp();
                    }
                }
            }
        }
        }

        return super.onKeyUp(keyCode, event);
    }

    //TODO: Remove this API at S60.
    /**
     * Call this view's OnClickListener, if it is defined.
     * @return True there was an assigned OnClickListener that was called, false otherwise is returned.
     * @deprecated [Not use any longer] This API just calls super, will be removed in the future.
     */
    /**@hide*/
    public boolean performClick() {
        return super.performClick();
    }

    //TODO: Remove this API at S60.
    /**
     * This API is used to support widget button click animation.
     * @param autoStartAnim True to enable click aniamtion, false to disable.
     * @deprecated [Not use any longer] This API takes no effect and will be removed in the future.
     */
    /**@hide*/
    public void setAutoStartAnim(boolean autoStartAnim) {
    }

    /**
     * This API is used to support remote views like setting widgets to enable/disable their animations.
     * @param play True to start animation, false to disable.
     * @deprecated [Not use any longer] This API takes no effect and will be removed in the future, never use it.
     */
    /**@hide*/
    public void playAnimationDrawable(boolean play) {
        Drawable d = getDrawable();
        if (!(d instanceof AnimationDrawable)) return ;

        AnimationDrawable ad = (AnimationDrawable) d;
        if (play) ad.start();
        else ad.stop();
    }

    //TODO: Remove this API at S60.
    /**
     * Call this view's OnLongClickListener, if it is defined.
     * @return True if one of the above receivers consumed the event, false otherwise.
     * @deprecated [Not use any longer] This API just calls super, will be removed in the future.
     */
    public boolean performLongClick() {
        return super.performLongClick();
    }

    /**
     * Called when a touch screen motion event occurs.
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
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
        mMultiplyPaint.setAlpha(HtcButtonUtil.MIN_ALPHA);
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

    /**
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     */
    protected void onDraw(Canvas canvas) {
        if (mIsAnimating || (mColorOnPaint.getAlpha()!=HtcButtonUtil.MIN_ALPHA)) {
            int sc = 0;
            if (mIsColorOn || mIsContentMultiplyRequired) {
                sc = canvas.saveLayer(getScrollX(), getScrollY(), getScrollX() + getWidth(), getScrollY() + getHeight(), null, Canvas.ALL_SAVE_FLAG);
                super.onDraw(canvas);
                canvas.drawColor((mIsAnimating ? mOverlayColor : mCategoryColor), mMaskMode);
                canvas.restoreToCount(sc);
            } else {
                super.onDraw(canvas);
            }
        } else {
            super.onDraw(canvas);
        }

        if (mDrawFocusIndicator && mFocusIndicator!=null) {
            mFocusIndicator.setBounds(canvas.getClipBounds());
            mFocusIndicator.draw(canvas);
        }
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (mIsAnimating) cancelEvent();
        super.onWindowFocusChanged(hasWindowFocus);
    }

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
     */
    public void stayInPress(boolean stay) {
        mStayInPress = stay;
    }

    /**@hide*/
    protected final boolean isStayInPress() { return mStayInPress; }

    /**
     * To enable/disable colorOn, the content will always keep multiply/overlay color.
     * @param on True to enable, false to disable.
     */
    public void setColorOn(boolean on) {
        boolean current_on = (mColorOnPaint.getAlpha() == HtcButtonUtil.BASE_ALPHA);
        mIsColorOn = on;

        if (current_on != on) {
            mColorOnPaint.setAlpha(on ? HtcButtonUtil.BASE_ALPHA : HtcButtonUtil.MIN_ALPHA);
            invalidate();
        }
    }

    /**@hide*/
    protected final boolean isColorOnSet() { return mIsColorOn; }

    /**
     * To set the drawable object for foreground icon.
     * @param icon The drawable object for the icon.
     */
    public void setIconDrawable(Drawable icon) {
        setImageDrawable(icon);
    }

    /**
     * To set the resource id for foreground icon.
     * @param icon The resource id for the icon.
     */
    public void setIconResource(int icon) {
        setImageResource(icon);
    }

    /**
     * To set the resource id for foreground icon and make the icon mutatable.
     * This is for appwidgets, not use this API if not appwidgets.
     * @param icon The resource id for the icon.
     * @hide
     */
    public void setMutateIconResource(int icon) {
        android.content.res.Resources res = getContext().getResources();
        Drawable tmp = (res == null ? null : res.getDrawable(icon));
        if (tmp != null) setIconDrawable(tmp.mutate());
    }

    /**@hide*/
    protected final int getBackgroundMode() { return mBackgroundMode; }
    /**@hide*/
    protected final void setContentMultiplyOn(boolean enabled) { mIsContentMultiplyRequired = enabled; }
    /**@hide*/
    protected final boolean getContentMultiplyOn() { return mIsContentMultiplyRequired; }
    /**@hide*/
    protected final boolean getDefaultContentMultiplyOn() { return mDefaultContentMultiplySet; }
    /**@hide*/
    protected final boolean isAnimating() { return mIsAnimating; }

    /**@hide*/
    @Override
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
    public void useSelectorWhenPressed(boolean enabled) {
        Drawable tmp = getBackground();

        if (tmp instanceof BackgroundDrawable) {
            mBackground = (BackgroundDrawable)tmp;
        } else if (mBackground == null) {
            mBackground = new BackgroundDrawable(getContext());
            mBackground.setRestDrawable(tmp);
        } else {
            mBackground.setRestDrawable(tmp);
        }

        mUseSelectorWhenPressed = enabled;
        setBackground(enabled ? mBackground : mBackground.getRestDrawable());
        setContentMultiplyOn(!enabled && getDefaultContentMultiplyOn());
    }

    /**@hide*/
    public void setCustomOverlayColor(int color) {
        int alpha = mMultiplyPaint.getAlpha();
        mOverlayColor = color;
        mMultiplyPaint.setColor(mOverlayColor);
        mMultiplyPaint.setAlpha(alpha);
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

    //Add By Ahan 20131022 to implement new press effect
    private class BackgroundDrawable extends Drawable {
        private Drawable mPressed, mDefaultPressed;
        private Drawable mRest;
        private int mSelectorColor;

        /**@hide*/
        public BackgroundDrawable(Context context) {
            this(context, null, com.htc.lib1.cc.R.style.HtcButton);
        }

        /**@hide*/
        public BackgroundDrawable(Context context, AttributeSet attrs, int defStyle) {
            int backMode = getBackgroundMode();

            mSelectorColor = HtcButtonUtil.getSelectorColor(backMode);

            mRest = null;

            mDefaultPressed = new android.graphics.drawable.ColorDrawable(mSelectorColor);
            mPressed = mDefaultPressed;
            mPressed.setColorFilter(mSelectorColor, PorterDuff.Mode.SRC_ATOP);
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
        @Override
        public void setAlpha(int alpha) {
            if (mRest != null) mRest.setAlpha(alpha);
        }

        //TODO: To remove this API when we sure it doesn't be used any longer.
        /**@hide*/
        public void setScale(float scale_width, float scale_height, int drawable) {
        }

        //TODO: To remove this API when we sure it doesn't be used any longer.
        /**@hide*/
        public void setDrawables(Drawable outer, Drawable press, Drawable background) {
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

        private void drawRest(Canvas canvas) {
            if (mRest != null) mRest.draw(canvas);
        }

        private void drawPressed(Canvas canvas) {
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
            return 0;
        }

        /**@hide*/
        public int getIntrinsicHeight() {
            if (mRest != null) return mRest.getIntrinsicHeight();
            else if (mPressed != null) return mPressed.getIntrinsicHeight();
            return 0;
        }
    }
}
