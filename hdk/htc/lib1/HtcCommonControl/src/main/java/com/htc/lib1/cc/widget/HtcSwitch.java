package com.htc.lib1.cc.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Switch;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.util.res.HtcResUtil;

public class HtcSwitch extends Switch {
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int MONOSPACE = 3;

    private static final double DISPLAYWIDTH_LARGE_FACTOR = 0.67f;
    private static final double DISPLAYWIDTH_SMALL_FACTOR = 0.33f;
    private static final double DISABLE_LIGHT_ALPHA_RATIO = 0.5f;
    private static final double DISABLE_DARK_ALPHA_RATIO = 0.4f;
    private static final double OFF_OPACITY_RATIO = 0.65f;

    @ViewDebug.ExportedProperty
    private int mDrawableWidth;

    @ViewDebug.ExportedProperty
    private int mDrawableHeight;

    @ViewDebug.ExportedProperty
    private static int mMaxSwitchTextWidth;

    @ViewDebug.ExportedProperty(category = "layout")
    private int mOnLayoutStart;

    @ViewDebug.ExportedProperty(category = "layout")
    private int mOnLayoutTop;

    @ViewDebug.ExportedProperty(category = "layout")
    private int mOffLayoutStart;

    @ViewDebug.ExportedProperty(category = "layout")
    private int mOffLayoutTop;

    @ViewDebug.ExportedProperty(category = "measurement")
    private int maxTextLayoutWidth;

    @ViewDebug.ExportedProperty(category = "measurement")
    private int maxTextHeight;

    @ViewDebug.ExportedProperty(category = "measurement")
    private int mSwitchTextOnPadding;// HtcSwitchTextOn's Padding

    @ViewDebug.ExportedProperty(category = "measurement")
    private int mSwitchTextOffPadding;// HtcSwitchTextOff's Padding

    @ViewDebug.ExportedProperty(category = "measurement")
    private int mSwitchTextOnLayoutWidth;// HtcSwitchTextOnLayout's RealWidth;

    @ViewDebug.ExportedProperty(category = "measurement")
    private int mSwitchTextOffLayoutWidth;// HtcSwitchTextOffLayout's RealWidth;

    private ObjectAnimator mObjectAnimator;
    private float mOffsetX;

    private boolean mIsLayoutRtl;
    private static final boolean isRtlEnable = Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN;

    private Resources mResources;
    private static TextPaint mTextPaint;
    private int mMode;

    @ViewDebug.ExportedProperty(category = "layout")
    private StaticLayout mOnLayout;

    @ViewDebug.ExportedProperty(category = "layout")
    private StaticLayout mOffLayout;

    @ViewDebug.ExportedProperty(deepExport = true)
    private Drawable mSwitchDrawable;

    @ViewDebug.ExportedProperty(deepExport = true)
    private Drawable mSwitchOuterDrawable;

    private ColorStateList mTextColors;

    @ViewDebug.ExportedProperty(category = "CommonControl")
    private boolean mSwitchDrawableRtl = false;

    /**
     * Construct a new Switch with default styling.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. And MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcSwitch(Context context) {
        this(context, null);
    }

    /**
     * Construct a new Switch with default styling, overriding specific style
     * attributes as requested.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. And MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs Specification of attributes that should deviate from default styling.
     */
    public HtcSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.htcSwitchStyle);
    }

    /**
     * Construct a new Switch with a default style determined by the given theme attribute,
     * overriding specific style attributes as requested.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. And MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs Specification of attributes that should deviate from the default styling.
     * @param defStyleAttr An attribute in the current theme that contains a
     *        reference to a style resource that supplies default values for
     *        the view. Can be 0 to not look for defaults.
     * @hide
     */
    public HtcSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTextPaint();
        mResources = getResources();
        mTextPaint.density = mResources.getDisplayMetrics().density;

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.HtcSwitch, R.attr.htcSwitchStyle, 0);
        mMode = a.getInt(R.styleable.HtcSwitch_backgroundMode,
                HtcButtonUtil.BACKGROUND_MODE_LIGHT);
        a.recycle();
        mSwitchDrawable = getInitBackground();
        adjustSwitchDrawableAndTextApperance();
        mDrawableWidth = mSwitchDrawable.getIntrinsicWidth() / 2;
        mDrawableHeight = mSwitchDrawable.getIntrinsicHeight();
        mMaxSwitchTextWidth = (int) (mDrawableWidth * DISPLAYWIDTH_LARGE_FACTOR);

        // Refresh display with current params
        refreshDrawableState();
        setChecked(isChecked());
    }

    private static int calculateSize(int measureSpec, int drawableSzie) {
        return (MeasureSpec.getMode(measureSpec) == MeasureSpec.EXACTLY) ? Math
                .max(MeasureSpec.getSize(measureSpec), drawableSzie)
                : drawableSzie;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMaxSwitchTextWidth % 2 != 0) {
            mMaxSwitchTextWidth -= 1;
        }

        if (mOnLayout == null) {
            mOnLayout = makeLayout(getTextOn(), mTextPaint, mResources);
        }
        if (mOffLayout == null) {
            mOffLayout = makeLayout(getTextOff(), mTextPaint, mResources);
        }

        maxTextHeight = Math.max(mOnLayout.getHeight(), mOffLayout.getHeight());
        setMeasuredDimension(calculateSize(widthMeasureSpec, mDrawableWidth),
                calculateSize(heightMeasureSpec, mDrawableHeight));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        mIsLayoutRtl = isRtlEnable ? getLayoutDirection() == View.LAYOUT_DIRECTION_RTL
                : false;
        if (mIsLayoutRtl) {
            mOnLayoutStart = mDrawableWidth;
            mOffLayoutStart = 0;
        } else {
            mOnLayoutStart = -(int) (mDrawableWidth * DISPLAYWIDTH_LARGE_FACTOR);
            mOffLayoutStart = (int) (mDrawableWidth * DISPLAYWIDTH_SMALL_FACTOR);
        }
        mOnLayoutTop = (mDrawableHeight - maxTextHeight) / 2;
        mOffLayoutTop = mOnLayoutTop;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mIsLayoutRtl = isRtlEnable ? getLayoutDirection() == View.LAYOUT_DIRECTION_RTL : false;

        if (mSwitchDrawableRtl != mIsLayoutRtl) {
            mSwitchDrawable = null;
            adjustSwitchDrawable();
        }
        if (mTextColors != null) {
            mTextPaint.setColor(mTextColors.getColorForState(
                    getDrawableState(), mTextColors.getDefaultColor()));
        }
        mTextPaint.drawableState = getDrawableState();
        final int left = (getWidth() - mDrawableWidth) / 2;
        final int top = (getHeight() - mDrawableHeight) / 2;
        final float offsetX = left - (mIsLayoutRtl ? mOffsetX : -mOffsetX);
        canvas.clipRect(left, top, left + mDrawableWidth, top + mDrawableHeight);

        canvas.save();
        canvas.translate(offsetX, top);
        if (mIsLayoutRtl) {
            mSwitchDrawable
                    .setBounds(0, 0, mDrawableWidth * 2, mDrawableHeight);
        } else {
            mSwitchDrawable.setBounds(-mDrawableWidth, 0, mDrawableWidth,
                    mDrawableHeight);
        }
        mSwitchDrawable.draw(canvas);
        canvas.restore();

        canvas.save();
        canvas.translate(mOffLayoutStart + offsetX, mOffLayoutTop + top);
        mOffLayout.draw(canvas);
        canvas.restore();

        canvas.save();
        canvas.translate(mOnLayoutStart + offsetX, mOnLayoutTop + top);
        mOnLayout.draw(canvas);
        canvas.restore();
    }

    private void adjustSwitchDrawableAndTextApperance() {
        adjustSwitchDrawable();
        adjustSwitchTextAppearance();
        invalidate();
    }

    private void adjustSwitchTextAppearance() {
        final boolean darkMode = mMode == HtcButtonUtil.BACKGROUND_MODE_DARK;
        final double alphaChecked = isChecked() ? 1.0f : OFF_OPACITY_RATIO;
        final double alphaEnabled = isEnabled() ? 1.0f : (darkMode ? DISABLE_DARK_ALPHA_RATIO : DISABLE_LIGHT_ALPHA_RATIO);
        final int alpha = (int) (255 * alphaChecked * alphaEnabled);

        TypedArray appearance = getContext().obtainStyledAttributes(darkMode ? R.style.fixed_b_toggle_primary_m : R.style.fixed_toggle_primary_m, R.styleable.TextAppearance);
        ColorStateList colors = appearance.getColorStateList(R.styleable.TextAppearance_android_textColor);
        mTextColors = ((null != colors) ? colors : getTextColors()).withAlpha(alpha);

        initTextPaint();
        final int textSize = appearance.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, 0);
        if (textSize != 0 && textSize != mTextPaint.getTextSize()) {
            mTextPaint.setTextSize(textSize);
            requestLayout();
        }
        setSwitchTypefaceByIndex(appearance);

        appearance.recycle();
    }

    private void adjustSwitchDrawable() {
        final int alpha = isEnabled() ? 255 : (int) (255 * (mMode == HtcButtonUtil.BACKGROUND_MODE_DARK ? DISABLE_DARK_ALPHA_RATIO : DISABLE_LIGHT_ALPHA_RATIO));
        if (null == mSwitchDrawable) {
            mSwitchDrawable = getInitBackground();
        }
        mSwitchDrawable.setAlpha(alpha);
    }

    /**
     * @deprecated [Not use any longer] This API will no longer be supported
     */
    /** @hide */
    public void setSwitchTextAppearance(Context context, int resid) {
        final boolean darkMode = mMode == HtcButtonUtil.BACKGROUND_MODE_DARK;
        final double alphaChecked = isChecked() ? 1.0f : OFF_OPACITY_RATIO;
        final double alphaEnabled = isEnabled() ? 1.0f : (darkMode ? DISABLE_DARK_ALPHA_RATIO : DISABLE_LIGHT_ALPHA_RATIO);
        final int alpha = (int) (255 * alphaChecked * alphaEnabled);

        TypedArray appearance = context.obtainStyledAttributes(resid, R.styleable.TextAppearance);
        ColorStateList colors = appearance.getColorStateList(R.styleable.TextAppearance_android_textColor);
        mTextColors = ((null != colors) ? colors : getTextColors()).withAlpha(alpha);

        initTextPaint();
        final int textSize = appearance.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, 0);
        if (textSize != 0 && textSize != mTextPaint.getTextSize()) {
            mTextPaint.setTextSize(textSize);
            requestLayout();
        }
        setSwitchTypefaceByIndex(appearance);

        appearance.recycle();
        invalidate();
    }

    private static final Typeface mSwitchTypeface = HtcResUtil.createFontFromFile("RobotoCondensed-Bold.ttf");

    private void setSwitchTypefaceByIndex(TypedArray appearance) {
        final int typefaceIndex = appearance.getInt(R.styleable.TextAppearance_android_typeface, -1);
        final int styleIndex = appearance.getInt(R.styleable.TextAppearance_android_textStyle, -1);
        Typeface tf = mSwitchTypeface;
        if (null != tf) {
            mTextPaint.setTypeface(tf);
            return;
        }
        switch (typefaceIndex) {
        case SANS:
            tf = Typeface.SANS_SERIF;
            break;

        case SERIF:
            tf = Typeface.SERIF;
            break;

        case MONOSPACE:
            tf = Typeface.MONOSPACE;
            break;
        }

        setSwitchTypeface(tf, styleIndex);
    }

    @Override
    public void setSwitchTypeface(Typeface tf, int style) {
        initTextPaint();

        if (style > 0) {
            if (tf == null) {
                tf = Typeface.defaultFromStyle(style);
            } else {
                tf = Typeface.create(tf, style);
            }

            setSwitchTypeface(tf);
            int typefaceStyle = tf != null ? tf.getStyle() : 0;
            int need = style & ~typefaceStyle;
            mTextPaint.setFakeBoldText((need & Typeface.BOLD) != 0);
            mTextPaint.setTextSkewX((need & Typeface.ITALIC) != 0 ? -0.25f : 0);
        } else {
            mTextPaint.setFakeBoldText(false);
            mTextPaint.setTextSkewX(0);
            setSwitchTypeface(tf);
        }
    }

    @Override
    public void setSwitchTypeface(Typeface tf) {
        if (mTextPaint.getTypeface() != tf) {
            mTextPaint.setTypeface(tf);
            requestLayout();
            invalidate();
        }
    }

    protected void setOffsetX(float x) {
        mOffsetX = x;
        postInvalidateOnAnimation();
    }

    private static StaticLayout makeLayout(CharSequence text,
            TextPaint textPaint, Resources resources) {
        final CharSequence transformed = (text != null) ? text.toString()
                .toUpperCase(resources.getConfiguration().locale) : "";

        return new StaticLayout(transformed, 0, transformed.length(),
                textPaint, mMaxSwitchTextWidth, Layout.Alignment.ALIGN_CENTER,
                1.f, 0, true, TextUtils.TruncateAt.END, mMaxSwitchTextWidth);
    }

    @Override
    public void setChecked(boolean checked) {
        if (isChecked() == checked) {
            super.setChecked(checked);
            return;
        }
        super.setChecked(checked);

        // Calling the super method may result in setChecked() getting called
        // recursively with a different value, so load the REAL value...
        checked = isChecked();
        adjustSwitchDrawableAndTextApperance();
        boolean isAttached = true;
        boolean isLaidout = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isAttached = isAttachedToWindow();
            isLaidout = isLaidOut();
        } else {
            if (null == getWindowToken()) {
                isAttached = false;
            }
        }

        if (isAttached && isLaidout) {
            animateSwitchToCheckedState(checked);
        } else {
            if(mDrawableWidth == 0){
                if(null == mSwitchDrawable){
                    mSwitchDrawable = getInitBackground();
                }
                mDrawableWidth = mSwitchDrawable.getIntrinsicWidth()/2;
            }
            // Immediately move the Switch to the new position.
            cancelObjectAnimator();
            setOffsetX(checked ? mDrawableWidth : 0);
        }
    }

    private void cancelObjectAnimator() {
        if (mObjectAnimator != null) {
            mObjectAnimator.cancel();
        }
    }

    private void animateSwitchToCheckedState(boolean checked) {
        if (mObjectAnimator != null && mObjectAnimator.isRunning()) {
            mObjectAnimator.cancel();
            mObjectAnimator = null;
        }
        if (checked) {
            mObjectAnimator = ObjectAnimator.ofFloat(this, "offsetX", 0.0f,
                    (float) mDrawableWidth);
        } else {
            mObjectAnimator = ObjectAnimator.ofFloat(this, "offsetX",
                    (float) mDrawableWidth, 0.0f);
        }
        mObjectAnimator.start();
    }

    private Drawable getInitBackground() {
        final int themeColor = HtcCommonUtil.getCommonThemeColor(getContext(),
                R.styleable.ThemeColor_light_category_color);
        Bitmap bm = null;
        Bitmap outerBitmap = null;

        switch (mMode) {
        case HtcButtonUtil.BACKGROUND_MODE_DARK:
            bm = BitmapFactory.decodeResource(getResources(),
                    R.drawable.common_switch_rest_dark);
            outerBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.common_switch_outer_dark);
            break;
        case HtcButtonUtil.BACKGROUND_MODE_LIGHT:
            bm = BitmapFactory.decodeResource(getResources(),
                    R.drawable.common_switch_rest);
            outerBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.common_switch_outer);
            break;
        }

        mSwitchDrawableRtl = mIsLayoutRtl = isRtlEnable ? getLayoutDirection() == View.LAYOUT_DIRECTION_RTL : false;
        if (mIsLayoutRtl) {
            Matrix matrix = new Matrix();
            matrix.preScale(-1.0f, 1.0f);
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
                    matrix, true);
        }
        Drawable[] array = new Drawable[3];
        array[0] = new ColorDrawable(themeColor);
        array[1] = new BitmapDrawable(getResources(), bm);
        array[2] = new BitmapDrawable(getResources(), outerBitmap);
        return toBitmapDrawable(getResources(), new LayerDrawable(array));
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int[] myDrawableState = getDrawableState();
        if (mSwitchDrawable != null)
            mSwitchDrawable.setState(myDrawableState);
        invalidate();
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == mSwitchDrawable;
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        mSwitchDrawable.jumpToCurrentState();
        if (mObjectAnimator != null && mObjectAnimator.isRunning()) {
            mObjectAnimator.end();
            mObjectAnimator = null;
        }
    }

    private void initTextPaint() {
        if (mTextPaint == null) {
            mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        }
    }

    /**
     * Change HtcSwitch's mode
     *
     * @param mode
     *            One of {@link HtcButtonUtil#BACKGROUND_MODE_LIGHT},
     *            {@link HtcButtonUtil#BACKGROUND_MODE_DARK}.
     */
    public void setMode(int mode) {
        if (mMode == mode) {
            return;
        }
        mMode = mode;
        mSwitchDrawable = getInitBackground();
        adjustSwitchDrawableAndTextApperance();
    }

    @Override
    public void setEnabled(boolean enabled) {
        final boolean oldEnabled = isEnabled();
        super.setEnabled(enabled);
        if (oldEnabled != enabled) {
            adjustSwitchDrawableAndTextApperance();
        }
    }

    /**
     * Get HtcSwitch's mode
     */
    public int getMode() {
        return mMode;
    }

    private static Drawable toBitmapDrawable(Resources resources, Drawable drawable) {
        final int w = drawable.getIntrinsicWidth();
        final int h = drawable.getIntrinsicHeight();
        final Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return new BitmapDrawable(resources, bitmap);
    }
}
