package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.graphics.Bitmap;

import com.htc.lib1.cc.R;

/**
 * HtcCompoundButton
 */
public class HtcCompoundButton extends View implements Checkable {
    private final static float SCALE = 0.5f;

    /**
     * The outer background.
     */
    protected Drawable mBackgroundRest;
    /**
     * The pressed background, it usually be applied multiply/overlay color on.
     */
    protected Drawable mBackgroundPress;
    /**
     * The inner background.
     */
    protected Drawable mInnerBackground;
    /**
     * The on foreground, it usually be applied multiply/overlay color on.
     */
    protected Drawable mContentPress;
    /**
     * The rest foreground.
     */
    protected Drawable mContentRest;
    /**
     * The partial selection with HtcCheckBox asset.
     */
    protected Drawable mPartialSelection;

    /**
     * The background mode.
     */
    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = HtcButtonUtil.BACKGROUND_MODE_LIGHT, to = "BACKGROUND_MODE_LIGHT"),
            @IntToString(from = HtcButtonUtil.BACKGROUND_MODE_DARK, to = "BACKGROUND_MODE_DARK"),
            @IntToString(from = HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVEDARK, to = "BACKGROUND_MODE_AUTOMOTIVEDARK"),
            @IntToString(from = HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVELIGHT, to = "BACKGROUND_MODE_AUTOMOTIVELIGHT"),
            @IntToString(from = HtcButtonUtil.BACKGROUND_MODE_COLORFUL, to = "BACKGROUND_MODE_COLORFUL")
    })
    protected int mBackgroundMode;
    /**
     * Record the Multiply/Overlay color.
     */
    @ExportedProperty(category = "CommonControl")
    protected int mMultiplyColor = -1;
    /**
     * Record the Category color
     */
    @ExportedProperty(category = "CommonControl")
    protected int mCategoryColor = -1;
    /**
     * To indicate if it has on state.
     */
    @ExportedProperty(category = "CommonControl")
    protected boolean mHasOnState = true;
    /**
     * Used to check if it is animating now.
     */
    @ExportedProperty(category = "CommonControl")
    protected boolean mIsAnimating = false;
    /**
     * Used to check if press-off state has the same look with press-on state.
     */
    @ExportedProperty(category = "CommonControl")
    protected boolean mTheSameWithPressOn = false;
    /**
     * Used to check if it is in partial selection mode.
     */
    @ExportedProperty(category = "CommonControl")
    protected boolean mIsPartialSelect = false;
    /**
     * Used to check if partial selection mode has been enabled.
     */
    @ExportedProperty(category = "CommonControl")
    protected boolean mIsPartialModeEnabled = false;
    /**
     * Used to indicate if needs to do multiply/overlay on content.
     */
    @ExportedProperty(category = "CommonControl")
    protected boolean mIsContentMultiplyRequired = false;

    private int mContentPressAlpha;

    @ExportedProperty(category = "CommonControl")
    private int mCenterX = 0;

    @ExportedProperty(category = "CommonControl")
    private int mCenterY = 0;

    @ExportedProperty(category = "CommonControl")
    private boolean mChecked = false;

    @ExportedProperty(category = "CommonControl")
    private boolean mBroadcasting;

    private OnCheckedChangeListener mOnCheckedChangeListener;
    private OnCheckedChangeListener mOnCheckedChangeWidgetListener;

    //The following 2 flags are used by Buttons with On state like CheckBox, DeleteButton, FlagButton, StarButton.
    /**
     * To indicate if this button needs to skip the first time draw after unchecked up.
     */
    @ExportedProperty(category = "CommonControl")
    protected boolean mSkipFirstUpDraw = false;
    /**
     * Used to check if it is doing unchecked up animation.
     */
    @ExportedProperty(category = "CommonControl")
    protected boolean mUnCheckUpAnimating = false;
    /**
     * This flag used to prevent skip draw when AP called performClick directly.
     */
    @ExportedProperty(category = "CommonControl")
    protected boolean isTriggerByPerformClick = false;

    @ExportedProperty(category = "CommonControl")
    private boolean mGainFocus;

    @ExportedProperty(category = "CommonControl")
    private boolean mTriggeredByKeyEvent;
    private Drawable mFocusIndicator;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcCompoundButton(Context context) {
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
    public HtcCompoundButton(Context context, AttributeSet attrs) {
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
    public HtcCompoundButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    /**
     * Constructor that can assign background mode, if need do multiply on content and if has on state or not.
     * @param context The Context the view is running in.
     * @param backgroundMode The background mode, default is Light mode.
     * @param isContentMultiply If need do multiply to content, default is true.
     * @param hasOnState True to enable, false to disable.
     */
    public HtcCompoundButton(Context context, int backgroundMode, boolean isContentMultiply, boolean hasOnState) {
        super(context);

        mBackgroundMode = backgroundMode;
        mIsContentMultiplyRequired = isContentMultiply;
        mHasOnState = hasOnState;

        //It is overlay color in fact although its name is still multiply
        mMultiplyColor = HtcButtonUtil.getOverlayColor(context, null);
        mCategoryColor = HtcButtonUtil.getCategoryColor(context, null);
        setAccessibilityImportant();
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CompoundButton, defStyle, 0);
        setChecked(a.getBoolean(R.styleable.CompoundButton_android_checked, false));
        a.recycle();

        if (attrs != null) {
            a = context.obtainStyledAttributes(attrs, R.styleable.HtcAnimationButtonMode, defStyle, 0);
            mBackgroundMode = a.getInt(R.styleable.HtcAnimationButtonMode_backgroundMode, HtcButtonUtil.BACKGROUND_MODE_LIGHT);
            a.recycle();

            a = context.obtainStyledAttributes(attrs, R.styleable.HtcCompoundButtonMode, defStyle, 0);
            mIsContentMultiplyRequired = a.getBoolean(R.styleable.HtcCompoundButtonMode_isContentMultiply, true);
            mHasOnState = a.getBoolean(R.styleable.HtcCompoundButtonMode_hasOnState, true);
            a.recycle();

            a = context.obtainStyledAttributes(attrs, R.styleable.View, defStyle, 0);
            setFocusable(a.getBoolean(R.styleable.View_android_focusable, true));
            setClickable(a.getBoolean(R.styleable.View_android_clickable, true));
            a.recycle();
        } else {
            mBackgroundMode = HtcButtonUtil.BACKGROUND_MODE_LIGHT;
            mIsContentMultiplyRequired = true;
            mHasOnState = true;
            setFocusable(true);
            setClickable(true);
        }

        //It is overlay color in fact although its name is still multiply
        mMultiplyColor = HtcButtonUtil.getOverlayColor(context, attrs);
        mCategoryColor = HtcButtonUtil.getCategoryColor(context, attrs);
        setAccessibilityImportant();

        mFocusIndicator = context.getResources().getDrawable(com.htc.lib1.cc.R.drawable.common_focused);
        if (mFocusIndicator != null) {
            mFocusIndicator.mutate();
            mFocusIndicator.setColorFilter(mMultiplyColor, PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void setAccessibilityImportant() {
        if (getImportantForAccessibility() == IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
            setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
        }
    }

    //Draw once related methods add by Ahan 2012/08/13 - start
    /**
     * Constant to indicate how many drawables a button has at most.
     */
    protected static int DRAWABLE_COUNTS = 5;
    /**
     * Constant to indicate how many states a burron has.
     */
    protected static int STATES_COUNT = 4;

    private static final int BTN_STATES_RESTOFF = 0;
    private static final int BTN_STATES_RESTON = 1;
    private static final int BTN_STATES_PRESSOFF = 2;
    private static final int BTN_STATES_PRESSON = 3;

    /**
     * Flag to indicate if it is in DrawOnce mode.
     */
    @ExportedProperty(category = "CommonControl")
    protected  boolean mDrawOnce = false;
    private static Bitmap[] states;

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = BTN_STATES_RESTOFF, to = "BTN_STATES_RESTOFF"),
            @IntToString(from = BTN_STATES_RESTON, to = "BTN_STATES_RESTON"),
            @IntToString(from = BTN_STATES_PRESSOFF, to = "BTN_STATES_PRESSOFF"),
            @IntToString(from = BTN_STATES_PRESSON, to = "BTN_STATES_PRESSON")
    })
    private int mCurrentState = BTN_STATES_RESTOFF, mLastState = BTN_STATES_RESTOFF;

    /**
     * @deprecated [Not use any longer]
     * @param context context
     * @param drs custom drawable array to composite, its length should be 5 and [0] for bkgOuter, [1] for bkgPressed, [2] for bkgRest, [3] for fgOn, [4] for fgRest
     * @param defBkg not used anymore
     */
    /**@hide*/
    public static Bitmap[] compositeBitmap(Context context, Drawable[] drs, boolean defBkg) {
        return compositeBitmap(context, drs, defBkg, true, true);
    }

    /**
     * This API is used to get Bitmap array with bitmaps represent each state of this button.
     * @param context context
     * @param drs custom drawable array to composite, its length should be 5 and [0] for bkgOuter, [1] for bkgPressed, [2] for bkgRest, [3] for fgOn, [4] for fgRest
     * @param defBkg not used anymore
     * @param pressed True for press-state multiply/overlay, false for not.
     * @param onState True for on multiply/overlay, false for not.
     * @return Bitmap array with bitmaps for each state of this button.
     */
    public static Bitmap[] compositeBitmap(Context context, Drawable[] drs, boolean defBkg, boolean pressed, boolean onState) {
        Drawable tmpDrawable = null;
        Bitmap[] tmp = new Bitmap[STATES_COUNT];
        Drawable[] myDrs = new Drawable[DRAWABLE_COUNTS];
        Canvas tmpCanvas = new Canvas();

        if (context == null || drs == null)
            throw new IllegalArgumentException("[HtcCompoundButton.compositeBitmap] Context or Drawable[] drs is null");

        if (drs.length == DRAWABLE_COUNTS) myDrs = drs;
        else throw new IllegalArgumentException("[HtcCompoundButton.compositeBitmap] Length of drs is uncorrect, length=>"+drs.length);

        //set Drawables
        for (int i=0; i<DRAWABLE_COUNTS; i++) {
            if (myDrs[i] != null) {
                myDrs[i].mutate();
                // find fisrt drawable which is not null for size reference
                if (tmpDrawable == null) tmpDrawable = myDrs[i];
            }
        }

        if (tmpDrawable == null)
            throw new IllegalArgumentException("[HtcCompoundButton.compositeBitmap] All elements in drs are all null");

        for (int i=0; i<STATES_COUNT; i++)
            tmp[i] = Bitmap.createBitmap(tmpDrawable.getIntrinsicWidth(), tmpDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        int overlay = HtcButtonUtil.getOverlayColor(context, null);
        if (pressed && myDrs[1] != null) myDrs[1].setColorFilter(overlay, PorterDuff.Mode.SRC_ATOP);    //Background Press
        if (onState && myDrs[3] != null) myDrs[3].setColorFilter(overlay, PorterDuff.Mode.SRC_ATOP);    //Content Checked

        //use normal size to composite first
        setCompositeScale(1.0f, drs, tmpDrawable);

        //Rest - Off, just draw the rest-off asset
        tmpCanvas.setBitmap(tmp[0]);
        if (myDrs[0] != null) myDrs[0].draw(tmpCanvas);
        if (myDrs[4] != null) myDrs[4].draw(tmpCanvas);

        //Rest - On, some controls need draw multiple assets for this state, ex: HtcCheckBox
        tmpCanvas.setBitmap(tmp[1]);
        if (myDrs[0] != null) myDrs[0].draw(tmpCanvas);
        if (myDrs[3] != null) myDrs[3].draw(tmpCanvas);

        //then composite scaled bitmaps
        setCompositeScale(0.9f, drs, tmpDrawable);

        //Pressed - Off, just draw the pressed-off asset
        tmpCanvas.setBitmap(tmp[2]);
        if (myDrs[1] != null) myDrs[1].draw(tmpCanvas);
        if (myDrs[4] != null) {
            myDrs[4].setColorFilter(overlay, PorterDuff.Mode.SRC_ATOP);
            myDrs[4].draw(tmpCanvas);
            myDrs[4].clearColorFilter();
        }

        //Pressed - On, just draw the pressed-on asset
        tmpCanvas.setBitmap(tmp[3]);
        if (myDrs[1] != null) myDrs[1].draw(tmpCanvas);
        if (myDrs[3] != null) {
            myDrs[3].setColorFilter(overlay, PorterDuff.Mode.SRC_ATOP);
            myDrs[3].draw(tmpCanvas);
            myDrs[3].clearColorFilter();
        }

        return tmp;
    }

    static void setCompositeScale(float ratio, Drawable[] drs, Drawable tmpDrawable) {
        android.graphics.Rect bounds = new android.graphics.Rect();
        int width = 0, height = 0, wDiff = 0, hDiff = 0;

        if (tmpDrawable != null && drs != null) {
            width = tmpDrawable.getIntrinsicWidth();
            height = tmpDrawable.getIntrinsicHeight();

            ratio = ((ratio>0.0f && ratio<1.0f) ? ratio : 1.0f);

            if (ratio <= 1.0f) {
                bounds.left = bounds.top = 0;
                bounds.right = width;
                bounds.bottom = height;

                for (int i=0; i<drs.length; i++) {
                    if (drs[i] != null) drs[i].setBounds(bounds);
                }

                if (ratio > 0f && ratio < 1.0f) {
                    wDiff = Math.round(width * (1.0f - ratio) / 2.0f);
                    hDiff = Math.round(height * (1.0f - ratio) / 2.0f);

                    bounds.left = wDiff;
                    bounds.top = hDiff;
                    bounds.right = width - wDiff;
                    bounds.bottom = height - hDiff;

                    if (drs[3] != null) drs[3].setBounds(bounds);
                    if (drs[4] != null) drs[4].setBounds(bounds);
                }
            }
        }
    }

    /**
     * Invoke this API will make this button to composite a bitmap array and just draw a bitmap on each state.
     * It will make draw time get a little reduce, please confirm with control owner before you use this API.
     * @param reComposite Pass true means users want to redo compositeBitmap, it should be false in general case
     * @param custom If not null, its length should be 2, [0] for fgRest and [1] for fgOn
     */
    public void setDrawOnce(boolean reComposite, Drawable[] custom) {
        setDrawOnce(reComposite, custom, true, true);
    }

    /**
     * Invoke this API will make this button to composite a bitmap array and just draw a bitmap on each state.
     * It may reduce a little time cost when draw, but please confirm the usage with control owner before you use this API.
     * @param reComposite Pass true means users want to redo compositeBitmap, it should be false in general case
     * @param custom If not null, its length should be 5 at least, [0] for bkgOuter, [1] for bkgPress, [2] for bkgInner, [3] for fgRest and [4] for fgOn
     * @param pressed True for press-state multiply/overlay, false for not.
     * @param onstate True for on multiply/overlay, false for not.
     */
    public void setDrawOnce(boolean reComposite, Drawable[] custom, boolean pressed, boolean onstate) {
        mDrawOnce = true;

        //states == null means it haven't done compositeBitmap before
        if (states == null || reComposite == true) {
            Drawable[] tmp = new Drawable[DRAWABLE_COUNTS];

            if (custom == null) {
                //No custom assets, ex: HtcCheckBox.
                tmp[0] = mBackgroundRest;
                tmp[1] = mBackgroundPress;
                tmp[2] = mInnerBackground;
                tmp[3] = mContentPress;
                tmp[4] = mContentRest;
            } else if (custom != null && custom.length >= tmp.length) {
                //Just custom foreground, ex:MoreButton in HtcMail.
                for (int i=0; i<tmp.length; i++)
                    tmp[i] = custom[i];
            } else {
                throw new IllegalArgumentException("[HtcCompoundButton.setDrawOnce][Invalid arguments] The length of custom should be "+DRAWABLE_COUNTS+" at least if not null, but it is "+custom.length+" now!!!");
            }

            states = null;
            states = compositeBitmap(getContext(), tmp, false, pressed, onstate);
        }
    }

    /**
     * To get the Bitmap array for all states of this button, be sure it is in DrawOnce mode before calling this method.
     * @return Bitmap array for all states of this button, if it is not in DrawOnce mode, it returns null.
     */
    protected Bitmap[] getStatesBitmap() {
        return states;
    }

    /**
     * @deprecated [Not use any longer] This API will be removed on S50.
     * TODO: Remove this API on S50.
     */
    /**@hide*/
    public void stopDrawOnce() {
        mDrawOnce = false;
        states = null;
    }
    //Draw once related methods add by Ahan 2012/08/13 - end

    void onCheckDownAnimationStart() {
        mIsAnimating = true;
    }

    void onCheckDownAnimationEnd() {
    }

    void onCheckDownAnimationCancel() {
    }

    void onCheckUpAnimationStart() {
    }

    void onCheckUpAnimationEnd() {
        mIsAnimating = false;
        invalidate();
    }

    void onCheckUpAnimationCancel() {
        mIsAnimating = false;
        invalidate();
    }

    void onUnCheckDownAnimationStart() {
        mIsAnimating = true;
    }

    void onUnCheckDownAnimationEnd() {
    }

    void onUnCheckDownAnimationCancel() {
    }

    void onUnCheckUpAnimationStart() {
    }

    void onUnCheckUpAnimationEnd() {
        mIsAnimating = false;
        if (!isTriggerByPerformClick && mSkipFirstUpDraw && !mTriggeredByKeyEvent) mUnCheckUpAnimating = true;
        else if (mTriggeredByKeyEvent) mTriggeredByKeyEvent = false;
        invalidate();
    }

    void onUnCheckUpAnimationCancel() {
        mIsAnimating = false;
        invalidate();
    }

    void setOnAlphaAnimation(int alpha) {
        if (mContentPress != null) {
            mContentPress.setAlpha(alpha);
            mContentPressAlpha = alpha;
        }
        if (mPartialSelection != null) mPartialSelection.setAlpha(alpha);
    }

    void setPressAlphaAnimation(int alpha) {
        if (mBackgroundPress != null) mBackgroundPress.setAlpha(alpha);
    }

    /**
     * This is called during layout when the size of this view has changed. If
     * you were just added to the view hierarchy, you're called with the old
     * values of 0.
     *
     * @param w Current width of this view.
     * @param h Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Drawable tmpDrawable = null;
        Drawable[] drawables = {mBackgroundRest, mBackgroundPress, mInnerBackground, mContentPress, mContentRest, mPartialSelection};

        for (int i=0; i<drawables.length; i++)
            if (drawables[i] != null) { tmpDrawable = drawables[i]; break; }

        if (tmpDrawable == null) return;

        if (w > 0 && h > 0) {
            int pl = getPaddingLeft();
            int pt = getPaddingTop();
            int pb = getPaddingBottom();
            int pr = getPaddingRight();

            int pWidth = w - pl - pr;
            int pHeight = h - pt - pb;

            int dw = tmpDrawable.getIntrinsicWidth();
            int dh = tmpDrawable.getIntrinsicHeight();

            //scale center fixed postion
            mCenterX = pl + pWidth / 2;
            mCenterY = pt + pHeight / 2;

            //setup drawable bound for drawing
            int left = mCenterX - dw / 2;
            int top = mCenterY - dh / 2;
            int right = left + dw;
            int bottom = top + dh;

            for (int i=0; i<drawables.length; i++)
                if (drawables[i] != null) drawables[i].setBounds(left, top, right, bottom);
        }

        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * To check if this button has been checked.
     * @return True for checked, false for unchecked.
     */
    @ViewDebug.ExportedProperty
    public boolean isChecked() {
        return mChecked;
    }

    /**
     * <p>Changes the checked state of this button.</p>
     *
     * @param checked true to check the button, false to uncheck it
     */
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            mIsPartialSelect = (mIsPartialModeEnabled ? mChecked : false);
            if (mChecked && mContentPress != null) mContentPress.setAlpha(HtcButtonUtil.BASE_ALPHA);
            if (mDrawOnce) mCurrentState = (isChecked() ? BTN_STATES_RESTON  : BTN_STATES_RESTOFF);
            invalidate();

            // Avoid infinite recursions if setChecked() is called from a listener
            if (mBroadcasting) { return; }

            mBroadcasting = true;

            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
            }

            if (mOnCheckedChangeWidgetListener != null) {
                mOnCheckedChangeWidgetListener.onCheckedChanged(this, mChecked);
            }

            mBroadcasting = false;
        }
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes.
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes. This callback is used for internal purpose only.
     *
     * @param listener the callback to call on checked state change
     * @hide
     */
    void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeWidgetListener = listener;
    }

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a compound button changed.
     */
    public static interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        void onCheckedChanged(HtcCompoundButton buttonView, boolean isChecked);
    }

    /**
     * Initializes an AccessibilityEvent with information about this View which is the event source.
     * @param event The event to initialize.
     */
    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(android.widget.CompoundButton.class.getName());
        event.setChecked(mChecked);
    }

    /**
     * Initializes an AccessibilityNodeInfo with information about this view.
     * @param info The instance to initialize.
     */
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(android.widget.CompoundButton.class.getName());
        info.setCheckable(true);
        info.setChecked(mChecked);
    }

    /**
     * This method is used to draw state bitmaps when setDrawOnce is set.
     * @param canvas The Canvas to which the View is rendered.
     */
    protected void drawStatesBitmap(Canvas canvas) {
        Bitmap[] drawStates = getStatesBitmap();
        if (drawStates != null) {
            if (drawStates[mCurrentState] != null) canvas.drawBitmap(drawStates[mCurrentState], 0, 0, null);
        }
    }

    /**
     * Manually render this view (and all of its children) to the given Canvas.
     * The view must have already done a full layout before this function is
     * called.  When implementing a view, implement
     * {@link #onDraw(android.graphics.Canvas)} instead of overriding this method.
     * If you do need to override this method, call the superclass version.
     *
     * @param canvas The Canvas to which the View is rendered.
     * @deprecated [module internal use] This method should not be invoked directly from AP side
     */
    /**@hide*/
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (mDrawOnce) {
            drawStatesBitmap(canvas);
            return;
        }

        if (!isChecked() && !mIsAnimating) drawRestOff(canvas);
        else if (!isChecked() && mIsAnimating) drawPressOn(canvas);
        else if (isChecked() && !mIsAnimating) drawRestOn(canvas);
        else if (isChecked() && mIsAnimating) drawPressOff(canvas);
        else ;

        if (mGainFocus && mFocusIndicator!=null) {
            mFocusIndicator.setBounds(canvas.getClipBounds());
            mFocusIndicator.draw(canvas);
        }
    }

    //[Ahan][2012/09/25][New draw process for S50]
    /**
     * Describe how this button draws its rest-off state.
     * @param canvas The canvas on which the rest-state will be drawn.
     */
    protected void drawRestOff(Canvas canvas) {
        drawOuter(canvas);
        drawInner(canvas);
        drawFgRest(canvas);
    }

    /**
     * Describe how this button draws its press-on state.
     * @param canvas The canvas on which the press-state will be drawn.
     */
    protected void drawPressOn(Canvas canvas) {
        drawOuter(canvas);
        drawPressed(canvas);
        drawInner(canvas);
        drawFgOn(canvas);
    }

    /**
     * Describe how this button draws its rest-on state.
     * @param canvas The canvas on which the rest-state will be drawn.
     */
    protected void drawRestOn(Canvas canvas) {
        drawOuter(canvas);
        drawInner(canvas);
        drawFgOn(canvas);
    }

    /**
     * Describe how this button draws its press-off state.
     * @param canvas The canvas on which the press-state will be drawn.
     */
    protected void drawPressOff(Canvas canvas) {
        if (mTheSameWithPressOn) {
            drawPressOn(canvas);
            return ;
        }

        drawOuter(canvas);
        drawPressed(canvas);
        drawInner(canvas);
        drawFgRest(canvas);
    }

    /**
     * Describe how this button draws its outer background.
     * @param canvas The canvas on which the outer background will be drawn.
     */
    protected void drawOuter(Canvas canvas) {
        if (mBackgroundRest != null) mBackgroundRest.draw(canvas);
    }

    /**
     * Describe how this button draws its pressed background.
     * @param canvas The canvas on which the pressed background will be drawn.
     */
    protected void drawPressed(Canvas canvas) {
        if (mBackgroundPress != null) mBackgroundPress.draw(canvas);
    }

    /**
     * Describe how this button draws its inner background.
     * @param canvas The canvas on which the inner background will be drawn.
     */
    protected void drawInner(Canvas canvas) {
        if (mInnerBackground != null) mInnerBackground.draw(canvas);
    }

    /**
     * Describe how this button draws its on foreground.
     * @param canvas The canvas on which the on foreground will be drawn.
     */
    protected void drawFgOn(Canvas canvas) {
        if (mContentPress != null) {
            mContentPress.setAlpha(HtcButtonUtil.BASE_ALPHA);
            mContentPress.draw(canvas);
        }
    }

    /**
     * Describe how this button draws its rest foreground.
     * @param canvas The canvas on which the rest foreground will be drawn.
     */
    protected void drawFgRest(Canvas canvas) {
        if (mContentRest != null) {
            mContentRest.setAlpha(HtcButtonUtil.BASE_ALPHA);
            mContentRest.draw(canvas);
        }
    }

    //[Ahan][2012/09/25]
    /**
     * Returns the suggested minimum width that the view should use.
     * @return The suggested minimum width of the view.
     */
    protected int getSuggestedMinimumWidth() {
        //edit by Ahan to fit the design change by desginer on HtcFlagButton
        if (mBackgroundRest != null) return mBackgroundRest.getIntrinsicWidth() + getPaddingLeft() + getPaddingRight();
        else if (mContentPress != null) return mContentPress.getIntrinsicWidth() + getPaddingLeft() + getPaddingRight();
        else return getPaddingLeft() + getPaddingRight();
    }

    /**
     * Returns the suggested minimum height that the view should use.
     * @return The suggested minimum height of the view.
     */
    @ExportedProperty(category = "CommonControl")
    protected int getSuggestedMinimumHeight() {
        //edit by Ahan to fit the design change by desginer on HtcFlagButton
        if (mBackgroundRest != null) return mBackgroundRest.getIntrinsicHeight() + getPaddingTop() + getPaddingBottom();
        else if (mContentPress != null) return mContentPress.getIntrinsicHeight() + getPaddingTop() + getPaddingBottom();
        else return getPaddingTop() + getPaddingBottom();
    }

    /**
     * Called to determine the size requirements for this view and all of its children.
     * @param widthMeasureSpec horizontal space requirements as imposed by the parent. The requirements are encoded with View.MeasureSpec.
     * @param heightMeasureSpec vertical space requirements as imposed by the parent. The requirements are encoded with View.MeasureSpec.
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    /**
     * Utility to return a default size.
     * @param size Default size for this view
     * @param measureSpec Constraints imposed by the parent
     * @return The size this view should be.
     */
    @ExportedProperty(category = "CommonControl")
    public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
        case MeasureSpec.UNSPECIFIED:
            result = size;
            break;
        case MeasureSpec.AT_MOST:
            result = Math.min(size, specSize);
            break;
        case MeasureSpec.EXACTLY:
            result = specSize;
            break;
        }
        return result;
    }

    /**
     * Get the LayoutParams associated with this view. All views should have
     * layout parameters. These supply parameters to the <i>parent</i> of this
     * view specifying how it should be arranged. There are many subclasses of
     * ViewGroup.LayoutParams, and these correspond to the different subclasses
     * of ViewGroup that are responsible for arranging their children.
     *
     * This method may return null if this View is not attached to a parent
     * ViewGroup or {@link #setLayoutParams(android.view.ViewGroup.LayoutParams)}
     * was not invoked successfully. When a View is attached to a parent
     * ViewGroup, this method must not return null.
     *
     * @return The LayoutParams associated with this view, or null if no
     *         parameters have been set yet
     */
    public ViewGroup.LayoutParams getLayoutParams() {
        ViewGroup.LayoutParams layoutParam = super.getLayoutParams();

        if (layoutParam == null) {
            layoutParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        return layoutParam;
    }

    private void cancelEvent() {
        mIsAnimating = false;
        float scale = SCALE;
        if (isChecked() && mHasOnState) {
            setPressAlphaAnimation(HtcButtonUtil.MAX_ALPHA);
            setOnAlphaAnimation(HtcButtonUtil.MAX_ALPHA);
            invalidate();
        } else {
            setPressAlphaAnimation(HtcButtonUtil.MAX_ALPHA);
            setOnAlphaAnimation(HtcButtonUtil.MIN_ALPHA);
            invalidate();
        }
    }

    private void removePropertyDown() {
        float scale = SCALE;
        if (!isChecked() || !mHasOnState) {
            if (mIsAnimating) onCheckUpAnimationEnd();
            onCheckDownAnimationStart();
            setPressAlphaAnimation(HtcButtonUtil.MAX_ALPHA);
            setOnAlphaAnimation(HtcButtonUtil.MAX_ALPHA);
            onCheckDownAnimationEnd();
            invalidate();
        } else {
            if (mIsAnimating) onUnCheckUpAnimationEnd();
            onUnCheckDownAnimationStart();
            setPressAlphaAnimation(HtcButtonUtil.MAX_ALPHA);
            setOnAlphaAnimation(HtcButtonUtil.MIN_ALPHA);
            onUnCheckDownAnimationEnd();
            invalidate();
        }
    }

    private void removePropertyUp() {
        float scale = SCALE;
        if (!isChecked() || !mHasOnState) {
            if (mIsAnimating) onCheckDownAnimationEnd();
            onCheckUpAnimationStart();
            setPressAlphaAnimation(HtcButtonUtil.MIN_ALPHA);
            onCheckUpAnimationEnd();
        } else {
            if (mIsAnimating) onUnCheckDownAnimationEnd();
            onUnCheckUpAnimationStart();
            setPressAlphaAnimation(HtcButtonUtil.MIN_ALPHA);
            onUnCheckUpAnimationEnd();
        }
    }

    private void cancelPropertyMove(){
        if (mIsAnimating) {
            cancelEvent();
        }

        if (!isChecked()) {
            if (mIsAnimating) onCheckDownAnimationCancel();
        } else {
            if (mIsAnimating) onUnCheckDownAnimationCancel();
        }
    }

    private void handleTouchEventWithoutAnimation(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_UP:
            if (!isEnabled() || !isClickable()) break;
            if (isEnabled()) {
                if (!mDrawOnce && mIsAnimating) {
                    removePropertyUp();
                }
                else if (mDrawOnce) {
                    if (mCurrentState == BTN_STATES_PRESSON) mCurrentState = BTN_STATES_RESTON;
                    else if (mCurrentState == BTN_STATES_PRESSOFF) mCurrentState = BTN_STATES_RESTOFF;
                    invalidate();
                }
            }
            break;
        case MotionEvent.ACTION_DOWN:
            if (!isEnabled() || !isClickable()) break;
            if (isEnabled()){
                if (!mDrawOnce) removePropertyDown();
                else if (mDrawOnce) {
                    if (mCurrentState == BTN_STATES_RESTOFF) mCurrentState = BTN_STATES_PRESSON;
                    else if (mCurrentState == BTN_STATES_RESTON) mCurrentState = BTN_STATES_PRESSOFF;
                    invalidate();
                }
            }
            break;
        case MotionEvent.ACTION_CANCEL:
            if (!isEnabled() || !isClickable()) break;
            if (!mDrawOnce) cancelPropertyMove();
            else {
                if (mCurrentState == BTN_STATES_PRESSON) mCurrentState = BTN_STATES_RESTOFF;
                else if (mCurrentState == BTN_STATES_PRESSOFF) mCurrentState = BTN_STATES_RESTON;
                invalidate();
            }
            break;
        case MotionEvent.ACTION_MOVE:
            if (!isEnabled() || !isClickable()) break;
            final int x = (int) event.getX();
            final int y = (int) event.getY();

            // Be lenient about moving outside of buttons
            int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
            if ((x < 0 - slop) || (x >= getWidth() + slop) ||
                (y < 0 - slop) || (y >= getHeight() + slop)) {
                if (!mDrawOnce) cancelPropertyMove();
                else {
                    if (mCurrentState == BTN_STATES_PRESSON) mCurrentState = BTN_STATES_RESTOFF;
                    else if (mCurrentState == BTN_STATES_PRESSOFF) mCurrentState = BTN_STATES_RESTON;
                    invalidate();
                }
            }
            break;
        }
    }

    /**
     * Called when a hardware key down event occurs.
     * @param keyCode A key code that represents the button pressed, from KeyEvent.
     * @param event The KeyEvent object that defines the button action.
     * @return If you handled the event, return true. If you want to allow the event to be handled by the next receiver, return false.
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        HtcButtonUtil.setEnableAnimation(false);

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER: {
                if (HtcButtonUtil.getEnableAnimation()) {
                    if (!isEnabled() || !isClickable()) break;
                    if (isEnabled()) removePropertyDown();//Add by Ahan due to disable animation
                    break;
                } else {
                    if (!isEnabled() || !isClickable()) break;
                    if (isEnabled()) removePropertyDown();
                    break;
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
        HtcButtonUtil.setEnableAnimation(false);
        mTriggeredByKeyEvent = true;

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER: {
                if (HtcButtonUtil.getEnableAnimation()) {
                    if (!isEnabled() || !isClickable()) break;
                    if (isEnabled() && mIsAnimating) removePropertyUp();
                    break;
                } else {
                    if (!isEnabled() || !isClickable()) break;
                    if (isEnabled()) {
                        if(mIsAnimating)removePropertyUp();
                    }
                    break;
                }
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    /**
     * Implement this method to handle touch screen motion events.
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    public boolean onTouchEvent(MotionEvent event) {
        HtcButtonUtil.setEnableAnimation(false);
        if (HtcButtonUtil.getEnableAnimation()) {
            handleTouchEventWithoutAnimation(event);//Add by Ahan due to disable animation
        } else {
            handleTouchEventWithoutAnimation(event);
        }

        return super.onTouchEvent(event);
    }

    /**@hide*/
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (mIsAnimating) cancelEvent();
        super.onWindowFocusChanged(hasWindowFocus);
    }

    /**@hide*/
    protected void onFocusChanged (boolean gainFocus, int direction, android.graphics.Rect previouslyFocusedRect) {
        if (mIsAnimating) cancelEvent();
        mGainFocus = gainFocus;
        if (!isEnabled()) {
            boolean isDarkMode = (mBackgroundMode == HtcButtonUtil.BACKGROUND_MODE_DARK) || (mBackgroundMode == HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVEDARK);
            setAlpha((mGainFocus || !isDarkMode) ? HtcButtonUtil.DISABLE_ALPHA : HtcButtonUtil.DISABLE_ALPHA_DARK);
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    static class SavedState extends BaseSavedState {
        boolean checked;

        /**
         * Constructor called from {@link CompoundButton#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            checked = (Boolean)in.readValue(null);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(checked);
        }

        @Override
        public String toString() {
            return "CompoundButton.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " checked=" + checked + "}";
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    /**
     * Hook allowing a view to generate a representation of its internal state that can later be used to create a new instance with that same state.
     * @return Returns a Parcelable object containing the view's current dynamic state, or null if there is nothing interesting to save. The default implementation returns null.
     */
    @Override
    public Parcelable onSaveInstanceState() {
        // Force our ancestor class to save its state
//        setFreezesText(true);
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.checked = isChecked();
        return ss;
    }

    /**
     * Hook allowing a view to re-apply a representation of its internal state that had previously been generated by onSaveInstanceState().
     * @param state The frozen state that had previously been returned by onSaveInstanceState().
     */
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setChecked(ss.checked);
        requestLayout();
    }

    /**
     * To set this button is enabled or not.
     * @param enabled True to enable, false to disable.
     */
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) return;

        if (enabled) {
            setLayerType(LAYER_TYPE_NONE, null);
            setAlpha(HtcButtonUtil.VISIBLE_ALPHA);
        } else {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
            float alpha = (mBackgroundMode == HtcButtonUtil.BACKGROUND_MODE_DARK || mBackgroundMode == HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVEDARK) ? HtcButtonUtil.DISABLE_ALPHA_DARK : HtcButtonUtil.DISABLE_ALPHA;
            setAlpha(alpha);
        }

        super.setEnabled(enabled);
    }

    /**
     * To swith the checked state of this button.
     */
    public void toggle() {
        setChecked(!isChecked());
    }

    /**
     * Call this view's OnClickListener, if it is defined.  Performs all normal
     * actions associated with clicking: reporting accessibility event, playing
     * a sound, etc.
     *
     * @return True there was an assigned OnClickListener that was called, false
     *         otherwise is returned.
     */
    @Override
    public boolean performClick() {
        /*
         * XXX: These are tiny, need some surrounding 'expanded touch area',
         * which will need to be implemented in Button if we only override
         * performClick()
         */

        /* When clicked, toggle the state */
        if (HtcButtonUtil.getEnableAnimation()) {
            isTriggerByPerformClick = true;
            if (!isChecked() || !mHasOnState) {
                //mCheckUpAnimatorSet.end();//comment by Ahan due to dsiable animation
                if (!mDrawOnce) onCheckUpAnimationEnd(); //Add by Ahan due to disable animation
            } else {
                //mUnCheckUpAnimatorSet.end();//comment by Ahan due to dsiable animation
                if (!mDrawOnce) onUnCheckUpAnimationEnd();//Add by Ahan due to disable animation
            }
        }

        //This is used for state control when mDrawOnce is set
        if (mDrawOnce) {
            mCurrentState = (isChecked() ? BTN_STATES_RESTOFF  : BTN_STATES_RESTON);
            invalidate();
        }

        toggle();
        return super.performClick();
    }

    private void setBounds() {
        Drawable tmpDrawable = null;
        Drawable[] drawables = {mBackgroundRest, mBackgroundPress, mInnerBackground, mContentPress, mContentRest};

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();

        if (drawables == null) return;

        for (int i=0; i<drawables.length; i++)
            if (drawables[i] != null) { tmpDrawable = drawables[i]; break; }

        if (tmpDrawable == null) return;

        if (w != 0 || h != 0) {
            //edit by Ahan to fit the design change by desginer on HtcFlagButton
            int dw = tmpDrawable.getIntrinsicWidth();
            int dh = tmpDrawable.getIntrinsicHeight();

            int left = mCenterX - dw / 2;
            int top = mCenterY - dh / 2;
            int right = left + dw;
            int bottom = top + dh;

            for (int i=0; i<drawables.length; i++)
                if (drawables[i] != null) drawables[i].setBounds(left, top, right, bottom);
        }
    }

    /**
     * To set drawable objects with this button, set null if the asset is not required.
     * @param outer The outer drawable.
     * @param pressed The pressed drawable, it usually be applied multiply/overlay color on.
     * @param inner The inner drawable.
     * @param rest The rest drawable.
     * @param on The on drawable, it usually be applied multiply/overlay color on.
     */
    public void setButtonDrawables(Drawable outer, Drawable pressed, Drawable inner, Drawable rest, Drawable on) {
        setButtonDrawables(outer, pressed, inner, rest, on, true);
    }

    /**
     * To set drawable objects with this button, set null if the asset is not required.
     * @param outer The outer drawable.
     * @param pressed The pressed drawable, it usually be applied multiply/overlay color on.
     * @param inner The inner drawable.
     * @param rest The rest drawable.
     * @param on The on drawable, it usually be applied multiply/overlay color on.
     * @param setColorFilter True to setColorFilter on pressed/on drwable, false for not.
     */
    public void setButtonDrawables(Drawable outer, Drawable pressed, Drawable inner, Drawable rest, Drawable on, boolean setColorFilter) {
        mBackgroundRest = outer;
        if (mBackgroundRest != null) mBackgroundRest.mutate();

        mBackgroundPress = pressed;
        if (mBackgroundPress != null) {
            mBackgroundPress.mutate();
            if (setColorFilter) mBackgroundPress.setColorFilter(mMultiplyColor, PorterDuff.Mode.SRC_ATOP);
        }

        mInnerBackground = inner;
        if (mInnerBackground != null) mInnerBackground.mutate();

        mContentPress = on;
        if (mContentPress != null) {
            mContentPress.mutate();
            if (mIsContentMultiplyRequired && setColorFilter) mContentPress.setColorFilter(mMultiplyColor, PorterDuff.Mode.SRC_ATOP);
        }

        mContentRest = rest;
        if (mContentRest != null) mContentRest.mutate();

        setBounds();
        requestLayout();
    }

    /**
     * To set resource id of the drawables with this button, set 0 if the asset is not required.
     * @param backgroundOuter The resource id of the outer drawable.
     * @param backgroundPress The resource id of the pressed drawable, it usually be applied multiply/overlay color on.
     * @param background The resource id of the inner drawable.
     * @param contentRest The resource id of the rest drawable.
     * @param contentOn The resource id of the on drawable, it usually be applied multiply/overlay color on.
     */
    protected void setButtonDrawablesInner(int backgroundOuter, int backgroundPress, int background, int contentRest, int contentOn) {
        Context context = getContext();
        Drawable bO = (backgroundOuter > 0 ? context.getResources().getDrawable(backgroundOuter) : null);
        Drawable bP = (backgroundPress > 0 ? context.getResources().getDrawable(backgroundPress) : null);
        Drawable b = (background > 0 ? context.getResources().getDrawable(background) : null);
        Drawable cO = (contentOn > 0 ? context.getResources().getDrawable(contentOn) : null);
        Drawable cR = (contentRest > 0 ? context.getResources().getDrawable(contentRest) : null);
        setButtonDrawables(bO, bP, b, cO, cR);
    }

    /**
     * To set resource id of the drawables with this button, set 0 if the asset is not required.
     * @param backgroundOuter The resource id of the outer drawable.
     * @param backgroundPress The resource id of the pressed drawable, it usually be applied multiply/overlay color on.
     * @param background The resource id of the inner drawable.
     * @param contentRest The resource id of the rest drawable.
     * @param contentOn The resource id of the on drawable, it usually be applied multiply/overlay color on.
     */
    public void setButtonDrawableResources(int backgroundOuter, int backgroundPress, int background, int contentRest, int contentOn) {
        Context context = getContext();
        Drawable bO = (backgroundOuter > 0 ? context.getResources().getDrawable(backgroundOuter) : null);
        Drawable bP = (backgroundPress > 0 ? context.getResources().getDrawable(backgroundPress) : null);
        Drawable b = (background > 0 ? context.getResources().getDrawable(background) : null);
        Drawable cO = (contentOn > 0 ? context.getResources().getDrawable(contentOn) : null);
        Drawable cR = (contentRest > 0 ? context.getResources().getDrawable(contentRest) : null);
        setButtonDrawables(bO, bP, b, cR, cO);
    }
}
