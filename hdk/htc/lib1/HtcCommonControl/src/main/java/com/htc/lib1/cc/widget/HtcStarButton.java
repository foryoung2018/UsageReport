package com.htc.lib1.cc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.htc.lib1.cc.R;

/**
 * HtcStarButton
 */
public class HtcStarButton extends HtcCompoundButton {
    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcStarButton(Context context) {
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
    public HtcStarButton(Context context, AttributeSet attrs) {
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
    public HtcStarButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    /**
     * @param context The Context the view is running in.
     * @param isSmall To indicate if it is small mode, taks no effect since S50.
     * @deprecated [not use any longer] HtcStarButton change to be one size only from S50
     */
    /**@hide*/
    public HtcStarButton(Context context, boolean isSmall) {
        super(context, null, 0);
        init(context, null, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        mIsContentMultiplyRequired = true;
        mHasOnState = true;
        mTheSameWithPressOn = true;
        mSkipFirstUpDraw = true; //Add by Ahan due to disable animation
        setButtonDrawables(context, attrs, defStyle);
    }

    /**
     * To Set the Drawables for the assets used by this Button.
     * @param context The Context the view is running in.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view.
     * @deprecated [module internal use] The access level of this API will be changed on S50
     */
    /**@hide*/
    public void setButtonDrawables(Context context, AttributeSet attrs, int defStyle) {
        Drawable bkgOuter = null, bkgPressed = null, bkgRest = null, fgOn = null, fgRest = null;

        bkgPressed = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_RATING_REST);
        fgOn = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_RATING_REST);
        fgRest = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_RATING_REST);

        super.setButtonDrawables(bkgOuter, bkgPressed, bkgRest, fgRest, fgOn);

        if (mBackgroundPress != null)
            mBackgroundPress.mutate().setColorFilter(mMultiplyColor, PorterDuff.Mode.SRC_ATOP);

        if (mContentPress != null)
            mContentPress.mutate().setColorFilter(mCategoryColor, PorterDuff.Mode.SRC_ATOP);
    }

    /**
     * Describe how this button draws its press-on state.
     * @param canvas The canvas on which the press-state will be drawn.
     */
    @Override
    protected void drawPressOn(Canvas canvas) {
        drawPressed(canvas);
    }
}
