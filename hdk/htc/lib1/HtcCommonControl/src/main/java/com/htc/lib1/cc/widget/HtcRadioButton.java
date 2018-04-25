package com.htc.lib1.cc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.htc.lib1.cc.R;

/**
 * HtcRadioButton
 */
public class HtcRadioButton extends HtcCompoundButton {
    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcRadioButton(Context context) {
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
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     */
    public HtcRadioButton(Context context, AttributeSet attrs) {
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
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view. If 0, no style
     *        will be applied (beyond what is included in the theme). This may
     *        either be an attribute resource, whose value will be retrieved
     *        from the current theme, or an explicit style resource.
     * @see #View(Context, AttributeSet)
     */
    public HtcRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    /**
     * Constructor to indicate the background mode.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param backgroundMode The background mode, default is Light mode.
     */
    public HtcRadioButton(Context context, int backgroundMode) {
        super(context, backgroundMode, true, true);
        init(context, null, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        mIsContentMultiplyRequired = true;
        mHasOnState = true;
        setButtonDrawables(context, attrs, defStyle);
    }

    /**
     * To Set the Drawables for the assets used by this Button.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view.
     * @deprecated [module internal use] The access level will be changed on S50
     */
    /**@hide*/
    public void setButtonDrawables(Context context, AttributeSet attrs, int defStyle) {
        boolean isSkinable = true;
        Drawable bkgOuter = null, bkgPressed = null, bkgRest = null, fgOn = null, fgRest = null;

        switch (mBackgroundMode) {
            case HtcButtonUtil.BACKGROUND_MODE_DARK:
                bkgOuter = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_B_CIRCLE_OUTER);
                bkgPressed = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_B_CIRCLE_OUTER);
                fgRest = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_RADIO_REST_DARK);
                fgOn = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_RADIO_REST_DARK);
                break;
            case HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVELIGHT:
                setButtonDrawableResources(R.drawable.automotive_common_circle_outer, R.drawable.automotive_common_circle_outer, 0,
                    R.drawable.automotive_common_radio_rest_light, R.drawable.automotive_common_radio_rest_light);
                isSkinable = false;
                break;
            case HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVEDARK:
                setButtonDrawableResources(R.drawable.automotive_common_b_circle_outer, R.drawable.automotive_common_b_circle_outer, 0,
                    R.drawable.automotive_common_radio_rest_dark, R.drawable.automotive_common_radio_rest_dark);
                isSkinable = false;
                break;
            default:
                bkgOuter = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_CIRCLE_OUTER);
                bkgPressed = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_CIRCLE_OUTER);
                fgRest = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_RADIO_REST_LIGHT);
                fgOn = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_RADIO_REST_LIGHT);
                break;
        }

        if (isSkinable) super.setButtonDrawables(bkgOuter, bkgPressed, bkgRest, fgRest, fgOn);
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the radio button is already checked, this method will not toggle the radio button.
     */
    @Override
    public void toggle() {
        // we override to prevent toggle when the radio is already
        // checked (as opposed to check boxes widgets)
        if (!isChecked()) {
            super.toggle();
        }
    }

    /**
     * @hide
     */
    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(android.widget.RadioButton.class.getName());
    }

    /**
     * @hide
     */
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(android.widget.RadioButton.class.getName());
    }

    /**
     * Describe how this button draws its rest-off state.
     * @param canvas The canvas on which the rest-state will be drawn.
     */
    @Override
    protected void drawRestOff(Canvas canvas) {
        if (mContentPress != null) mContentPress.clearColorFilter();
        super.drawRestOff(canvas);
    }

    /**
     * Describe how this button draws its press-on state.
     * @param canvas The canvas on which the press-state will be drawn.
     */
    @Override
    protected void drawPressOn(Canvas canvas) {
        if (mContentPress != null) mContentPress.setColorFilter(mMultiplyColor, PorterDuff.Mode.SRC_ATOP);
        super.drawPressOn(canvas);
    }

    /**
     * Describe how this button draws its rest-on state.
     * @param canvas The canvas on which the rest-state will be drawn.
     */
    @Override
    protected void drawRestOn(Canvas canvas) {
        if (mContentPress != null) mContentPress.setColorFilter(mCategoryColor, PorterDuff.Mode.SRC_ATOP);
        super.drawRestOn(canvas);
    }

    /**
     * Describe how this button draws its press-off state.
     * @param canvas The canvas on which the press-state will be drawn.
     */
    @Override
    protected void drawPressOff(Canvas canvas) {
        if (mContentPress != null) mContentPress.clearColorFilter();
        super.drawPressOff(canvas);
    }
}
