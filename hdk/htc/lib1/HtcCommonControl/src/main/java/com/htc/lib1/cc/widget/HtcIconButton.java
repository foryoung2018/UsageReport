package com.htc.lib1.cc.widget;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

import com.htc.lib1.cc.R;

/**
 * HtcIconButton, throw exception for unsupported API
 */
public class HtcIconButton extends HtcButton {
    private static final int COMPOUND_LEFT = 0;
    private static final int COMPOUND_TOP = 1;
    private static final int COMPOUND_RIGHT = 2;
    private static final int COMPOUND_BOTTOM = 3;

    @ExportedProperty(category = "CommonControl")
    private int mContentShift = 0;
    private BackgroundDrawable mBackground;

    @ExportedProperty(category = "CommonControl")
    private boolean mUseSelectorWhenPressed = false;

    private Method methodGetVerticalOffset;

    /**
     * Simple constructor to use when creating a view from code.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcIconButton(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called when a view is
     * being constructed from an XML file, supplying attributes that were specified in the XML file.
     * This version uses a default style of 0, so the only attribute values applied are those in the
     * Context's Theme and the given AttributeSet.
     * <p>
     * The method onFinishInflate() will be called after all children have been added.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     */
    public HtcIconButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This constructor of View
     * allows subclasses to use their own base style when they are inflating. For example, a Button
     * class's constructor would call this version of the super class constructor and supply
     * <code>R.attr.buttonStyle</code> for <var>defStyle</var>; this allows the theme's button style
     * to modify all of the base view attributes (in particular its background) as well as the
     * Button class's attributes.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view. If 0, no style will be applied
     *            (beyond what is included in the theme). This may either be an attribute resource,
     *            whose value will be retrieved from the current theme, or an explicit style
     *            resource.
     * @see #View(Context, AttributeSet)
     */
    public HtcIconButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * Constructor to indicate the background mode and if need do mutiply to content.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param backgroundMode The background mode, default is Light mode.
     * @param isContentMultiply If need do multiply to content, default is true.
     */
    public HtcIconButton(Context context, int backgroundMode, boolean isContentMultiply) {
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
    public HtcIconButton(Context context, int backgroundMode, boolean isContentMultiply, int extAnimationMode) {
        super(context, backgroundMode, isContentMultiply, extAnimationMode);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        useSelectorWhenPressed(false);
    }

    @ExportedProperty(category = "CommonControl")
    private int computeShift() {
        int contentShift;
        Drawable drs[] = getCompoundDrawables();
        Layout layout = getLayout();

        if (drs[COMPOUND_LEFT]!=null || drs[COMPOUND_RIGHT]!=null)
            contentShift = computeHorizontalOffset(layout, drs[COMPOUND_LEFT]!=null ? drs[COMPOUND_LEFT] : drs[COMPOUND_RIGHT]);
        else if (drs[COMPOUND_TOP]!=null || drs[COMPOUND_BOTTOM]!=null)
            contentShift = computeVerticalShift(layout, drs[COMPOUND_TOP]!=null ? drs[COMPOUND_TOP] : drs[COMPOUND_BOTTOM]);
        else
            contentShift = 0;

        return contentShift;
    }

    private int computeHorizontalOffset(Layout layout, Drawable dr) {
        if (dr == null) return 0;

        int compoundWidth = dr.getIntrinsicWidth(), compoundPadding = getCompoundDrawablePadding();
        int shiftFrom = 0, shiftTo = 0, shiftAmount = 0, lineMaxWidth = 0;

        if (layout!=null && !getText().equals("")) {
            int lineCount = layout.getLineCount();
            for (int layoutWidth=0, i=0; i<lineCount; i++) {
                layoutWidth = (int)layout.getLineRight(i) - (int)layout.getLineLeft(i);
                if (layoutWidth > lineMaxWidth) {
                    lineMaxWidth = layoutWidth;
                }
            }
        } else {
            lineMaxWidth = 0;
        }

        shiftFrom = (compoundWidth + compoundPadding + lineMaxWidth) / 2;
        shiftTo = (getPaddingLeft() + getWidth() - getPaddingRight()) / 2;
        shiftAmount = shiftTo - shiftFrom - getPaddingLeft();

        return shiftAmount;
    }

    @ExportedProperty(category = "CommonControl")
    private int computeVerticalShift(Layout layout, Drawable dr) {
        int contentHeight = 0;

        if (dr != null) {
            contentHeight += dr.getIntrinsicHeight();
            contentHeight += getCompoundDrawablePadding();
        }

        if (layout != null) {
            float clipTop = getExtendedPaddingTop() + getScrollY();
            float clipBottom = getBottom() - getTop() - getExtendedPaddingBottom() + getScrollY();
            int lines = getLineCount();
            float lineSpacingAdd = layout.getSpacingAdd();
            float allLineHeight = layout.getLineBottom(lines - 1) - layout.getLineTop(0) - lineSpacingAdd; //minus for the very first line

            if (getText().equals("") || getText().length()==0) {
                lines = 0;
                allLineHeight = 0;
            }

            contentHeight += Math.min(allLineHeight, (clipBottom - clipTop));
        }

        int shift = contentHeight == 0 ? 0 : (getHeight() - contentHeight) / 2 - getPaddingTop();
        return shift;
    }

    /**@hide*/
    @ExportedProperty(category = "CommonControl")
    protected int getFadeTop(boolean offsetRequired) {
        int gravity = getGravity();
        if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.TOP) {
            return getSuperFadeTop(offsetRequired) + mContentShift;
        }
        return getSuperFadeTop(offsetRequired);
    }

    //This is a temporarily workaround to substitute super.getFadeTop(boolean) cause it is hidden method.
    @ExportedProperty(category = "CommonControl")
    private int getSuperFadeTop(boolean offsetRequired) {
        if (getLayout() == null) return 0;
        int voffset = 0;

        if ((getGravity() & Gravity.VERTICAL_GRAVITY_MASK) != Gravity.TOP)
            voffset = invokeGetVerticalOffset();

        if (offsetRequired) voffset += getTopPaddingOffset();
        return getExtendedPaddingTop() + voffset;
    }

    //This is a java reflection solution to invoke getVerticalOffset in getSuperFadeTop.
    @ExportedProperty(category = "CommonControl")
    private int invokeGetVerticalOffset() {
        int voffset = 0;

        try {
            if (methodGetVerticalOffset != null)
            voffset = (Integer)methodGetVerticalOffset.invoke(this, true);
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }

        return voffset;
    }

    private void initReflectionComponents() {
        Class cls = getClass();

        while (cls != null && cls != TextView.class)
            cls = cls.getSuperclass();

        try {
            methodGetVerticalOffset = cls.getDeclaredMethod("getVerticalOffset", boolean.class);
            methodGetVerticalOffset.setAccessible(true);
        } catch (NoSuchMethodException e) {
        }
    }

    /** @hide */
    public void setHorizontalFadingEdgeEnabled(boolean horizontalFadingEdgeEnabled) {
        super.setHorizontalFadingEdgeEnabled(horizontalFadingEdgeEnabled);
        if (horizontalFadingEdgeEnabled && methodGetVerticalOffset == null) initReflectionComponents();
    }

    @ExportedProperty(category = "CommonControl")
    private boolean mPreDrawRegistered = false;

    /**@hide*/
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);

        //From 4.4.3, TextView may nullLayouts() during onRtlPropertiesChanged().
        //We should check if getLayout() is null and register a preDrawListener to trigger preDraw if yes.
        //TextView will recreate mLayout during preDraw, this can ensure we can compute right shift amount during draw phase.
        if (getLayout()==null && !mPreDrawRegistered) {
            getViewTreeObserver().addOnPreDrawListener(this);
            mPreDrawRegistered = true;
        }
    }

    /**@hide*/
    public boolean onPreDraw() {
        try { return super.onPreDraw(); }
        finally { mPreDrawRegistered = false; }
    }

    /**@hide*/
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mPreDrawRegistered) {
            getViewTreeObserver().removeOnPreDrawListener(this);
            mPreDrawRegistered = false;
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
     * @hide
     */
    public void draw(Canvas canvas) {
        mContentShift = computeShift();
        super.draw(canvas);
    }

    /**@hide*/
    protected void onDraw(Canvas canvas) {
        int gravityX = getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK;
        int gravityY = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
        int offsetX, offsetY;

        if (gravityX == Gravity.LEFT) offsetX = mContentShift;
        else if (gravityX == Gravity.RIGHT) offsetX = -mContentShift;
        else offsetX = 0;

        if (gravityY == Gravity.TOP) offsetY = mContentShift;
        else if (gravityY == Gravity.BOTTOM) offsetY = -mContentShift;
        else offsetY = 0;

        canvas.translate(offsetX, offsetY);
        super.onDraw(canvas);
        canvas.translate(-offsetX, -offsetY);
    }

    /**@hide*/
    public void invalidateDrawable(Drawable drawable) {
        super.invalidateDrawable(drawable);
        if (drawable instanceof AnimationDrawable) {
            invalidate();
        }
    }

    /**
     * Specifies how to align the text by the view's x- and/or y-axis when the text is smaller than the view.
     * @param gravity It will take no effects, the gravity will be determined by the compound drawables.
     * @see android.view.Gravity
     */
    public void setGravity(int gravity) {
        setGravityByDrawable(getCompoundDrawables());
    }

    private void setGravityByDrawable(Drawable[] drs) {
        int index, drsLength = (drs == null ? -1 : drs.length);

        for (index=0; index<drsLength; index++)
            if (drs[index] != null) break;

        setGravityByDrawable(drsLength<0 ? -1 : index);
    }

    private void setGravityByDrawable(int which) {
        switch (which) {
        case COMPOUND_LEFT:
            super.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            break;
        case COMPOUND_TOP:
            super.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            break;
        case COMPOUND_RIGHT:
            super.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            break;
        case COMPOUND_BOTTOM:
            super.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            break;
        default:
            super.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            break;
        }
    }

    /**
     * This API just for HtcFooterButton usage, and it just takes effects only when no compound drawable exists.
     * @param gravity The gravity for the text.
     * @hide
     */
    void setGravityJustForText(int gravity) {
        Drawable[] drs = getCompoundDrawables();
        for (Drawable tmp : drs) {
            if (tmp != null)
                return;
        }
        super.setGravity(gravity);
    }

    /**
     * To set the the compound drawable anywhere, just the first drawable takes effect by the order left-top-right-bottom, the Drawables must already have had {@link Drawable#setBounds} called.
     * @param left The drawable object of the left icon.
     * @param top The drawable object of the top icon.
     * @param right The drawable object of the right icon.
     * @param bottom The drawable object of the bottom icon.
     * @deprecated [module internal use] This method is used internally.
     */
    @Deprecated
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        Drawable[] drs = findTheValidCompoundDrawable(new Drawable[] {left, top, right, bottom});
        super.setCompoundDrawables(drs[COMPOUND_LEFT], drs[COMPOUND_TOP], drs[COMPOUND_RIGHT], drs[COMPOUND_BOTTOM]);
    }

    /**
     * To set the the compound drawable anywhere, just the first drawable takes effect by the order left-top-right-bottom.
     * @param left The drawable object of the left icon.
     * @param top The drawable object of the top icon.
     * @param right The drawable object of the right icon.
     * @param bottom The drawable object of the bottom icon.
     * @deprecated [module internal use] This method is used internally.
     */
    @Deprecated
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    private Drawable[] findTheValidCompoundDrawable(Drawable[] drs) {
        Drawable tmp = null;

        if (drs == null)
            drs = new Drawable[] {null, null, null, null};

        setGravityByDrawable(drs);

        for (int i=0; i<drs.length; i++) {
            if (tmp == null && drs[i] != null) tmp = drs[i];
            else drs[i] = null;
        }

        return drs;
    }

    /**
     * To set the drawable object for the top icon.
     * @param icon Drawable object for the top icon.
     * @deprecated [Not use any longer] HtcButton has opened to set one compound drawable, use setCompoundDrawablesWithIntrinsicBounds() instead.
     */
    public void setIconDrawable(Drawable icon) {
        this.setIconDrawable(null, icon, null, null);
    }

    /**
     * To set just only one compound drawable anywhere.
     * @param left The left compound drawable
     * @param top The top compound drawable
     * @param right The right compound drawable
     * @param bottom The bottom compound drawable
     * @deprecated [Not use any longer] HtcButton has opened to set one compound drawable, use setCompoundDrawablesWithIntrinsicBounds() instead.
     * @hide
     */
    public void setIconDrawable(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    /**
     * To set the resource id for the top icon.
     * @param icon The resource id for the top icon.
     * @deprecated [Not use any longer] HtcButton has opened to set one compound drawable, use setCompoundDrawablesWithIntrinsicBounds() instead.
     */
    public void setIconResource(int icon) {
        setIconDrawable(icon==0 ? null : getContext().getResources().getDrawable(icon));
    }

    /**
     * To set just only one compound drawable anywhere.
     * @param left The resource id of the left compound drawable
     * @param top The resource id of the top compound drawable
     * @param right The resource id of the right compound drawable
     * @param bottom The resource id of the bottom compound drawable
     * @deprecated [Not use any longer] HtcButton has opened to set one compound drawable, use setCompoundDrawablesWithIntrinsicBounds() instead.
     * @hide
     */
    public void setIconResource(int left, int top, int right, int bottom) {
        android.content.res.Resources res = getContext().getResources();
        setIconDrawable(left==0 ? null : res.getDrawable(left), top==0 ? null : res.getDrawable(top),
                        right==0 ? null : res.getDrawable(right), bottom==0 ? null : res.getDrawable(bottom));
    }

    /**@hide*/
    @Override
    public void setBackgroundDrawable(Drawable background) {
        if (!mUseSelectorWhenPressed) {
            super.setBackgroundDrawable(background);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int resId;
            int mode = getBackgroundMode();
            switch (mode) {
            case HtcButtonUtil.BACKGROUND_MODE_DARK:
            case HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVEDARK:
                resId = R.drawable.list_selector_dark;
                break;
            case HtcButtonUtil.BACKGROUND_MODE_LIGHT:
            case HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVELIGHT:
                resId = R.drawable.list_selector_light;
                break;
            default:
                resId = R.drawable.list_selector_light;
                break;
            }
            Drawable drawable = getResources().getDrawable(resId);
            super.setBackgroundDrawable(drawable);
        } else {
            if (mUseSelectorWhenPressed && mBackground != null) {
                if (background instanceof BackgroundDrawable) {
                    mBackground = (BackgroundDrawable) background;
                } else {
                    mBackground.setRestDrawable(background);
                }
            }
            super.setBackgroundDrawable(mBackground);
        }
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

    //Add By Ahan 20131022 to implement the new press effect
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
