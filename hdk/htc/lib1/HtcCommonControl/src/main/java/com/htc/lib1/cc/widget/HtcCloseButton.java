package com.htc.lib1.cc.widget;

import com.htc.lib1.cc.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.content.res.TypedArray;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.Drawable;

/**
 * HtcCloseButton
 * @deprecated [Not use any longer] This button does not exist on S50
 */
/**@hide*/
public class HtcCloseButton extends HtcCompoundButton {
    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
     public HtcCloseButton(Context context) {
         this(context, null);
     }

    /**
     * Constructor to indicate the background mode.
     * @param context The Context the view is running in.
     * @param backgroundMode The background mode, default is Light mode.
     */
     public HtcCloseButton(Context context, int backgroundMode) {
         super(context, backgroundMode, true, false);
         init(context, null, 0);
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
     public HtcCloseButton(Context context, AttributeSet attrs) {
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
     public HtcCloseButton(Context context, AttributeSet attrs, int defStyle) {
         super(context, attrs, defStyle);
         init(context, attrs, defStyle);
     }

     private void init(Context context, AttributeSet attrs, int defStyle){
            mIsContentMultiplyRequired = true;
            mHasOnState = false;
            setButtonDrawables(context, attrs, defStyle);
     }

        //Add by Ahan for skin change on S4+
    /**
     * To Set the Drawables for the assets used by this Button.
     * @deprecated [module internal use] Please do not use this API directly, it will become invisible on S50.
     * @param context The Context the view is running in.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view.
     */
     @Deprecated
    public void setButtonDrawables(Context context, AttributeSet attrs, int defStyle) {
            Drawable bkgOuter = null, bkgPressed = null, bkgRest = null, fgOn = null, fgRest = null;

            bkgPressed = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_COLLECT_REST);
            fgOn = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_COLLECT_REST);
            fgRest = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_COLLECT_REST);

            super.setButtonDrawables(bkgOuter, bkgPressed, bkgRest, fgRest, fgOn);
    }
}
