package com.htc.lib1.cc.widget;

import com.htc.lib1.cc.util.res.HtcResUtil;
import com.htc.lib1.cc.R;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;

/**
 * HtcFooterIconButton extends HtcIconButton. This is a lightly footer button
 * which contains icon and text.
 *
 * Usage : a. Use layout and define all text, icon, mode attrs
 *         b. Set icon in runtime via HtcIconButton API()
 * DarkMode in runtime:
 *             new HtcFooterIconButton(context, attrs, com.htc.lib1.cc.R.style.FooterBarButtonStyle_Dark)
 * Support mode : Dark & Light, always_right & always_button, with icon &
 * without icon
 *
 * ToDo : a.fine tune performance
 *        b.All text style are fixed, check with designer for UX
 *
 * Note : The design for long string is set ellipse and fade out We don't hanlde
 * truncate issue in HtcFooterIconButton & HtcFooterButton
 *
 * @author amt_masd_chj_ge
 */
public class HtcFooterIconButton extends HtcIconButton {

    private Configuration mConfiguration = null;

    @ExportedProperty(category = "CommonControl")
    private int M3 = 0;

    @ExportedProperty(category = "CommonControl")
    private int M5 = 0;

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = HtcFooter.STYLE_MODE_DEFAULT, to = "STYLE_MODE_DEFAULT"),
            @IntToString(from = HtcFooter.STYLE_MODE_COLORFUL, to = "STYLE_MODE_COLORFUL"),
            @IntToString(from = HtcFooter.STYLE_MODE_DARK, to = "STYLE_MODE_DARK"),
            @IntToString(from = HtcFooter.STYLE_MODE_LIGHT, to = "STYLE_MODE_LIGHT"),
            @IntToString(from = HtcFooter.STYLE_MODE_TRANSPARENT, to = "STYLE_MODE_TRANSPARENT")
    })
    private int mStyleMode = HtcFooter.STYLE_MODE_DEFAULT;

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = HtcFooter.DISPLAY_MODE_DEFAULT, to = "DISPLAY_MODE_DEFAULT"),
            @IntToString(from = HtcFooter.DISPLAY_MODE_ALWAYSBOTTOM, to = "DISPLAY_MODE_ALWAYSBOTTOM"),
            @IntToString(from = HtcFooter.DISPLAY_MODE_ALWAYSRIGHT, to = "DISPLAY_MODE_ALWAYSRIGHT")
    })
    private int mDisplayMode = HtcFooter.DISPLAY_MODE_DEFAULT;

    private Drawable mImage = null;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context
     *            The Context the view is running in, through which it can
     *            access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcFooterIconButton(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet. The method onFinishInflate() will be called
     * after all children have been added.
     *
     * @param context
     *            The Context the view is running in, through which it can
     *            access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs
     *            The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     */
    public HtcFooterIconButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.footerButtonStyle);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of View allows subclasses to use their own base style when
     * they are inflating. For example, a Button class's constructor would call
     * this version of the super class constructor and supply
     * <code>R.attr.buttonStyle</code> for <var>defStyle</var>; this allows the
     * theme's button style to modify all of the base view attributes (in
     * particular its background) as well as the Button class's attributes.
     *
     * @param context
     *            The Context the view is running in, through which it can
     *            access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs
     *            The attributes of the XML tag that is inflating the view.
     * @param defStyle
     *            The default style to apply to this view. If 0, no style will
     *            be applied (beyond what is included in the theme). This may
     *            either be an attribute resource, whose value will be retrieved
     *            from the current theme, or an explicit style resource.
     * @see #View(Context, AttributeSet)
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcFooterIconButton(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, R.style.FooterBarButtonStyle);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of View allows subclasses to use their own base style when
     * they are inflating. For example, a Button class's constructor would call
     * this version of the super class constructor and supply
     * <code>R.attr.buttonStyle</code> for <var>defStyle</var>; this allows the
     * theme's button style to modify all of the base view attributes (in
     * particular its background) as well as the Button class's attributes.
     *
     * @param context
     *            The Context the view is running in, through which it can
     *            access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs
     *            The attributes of the XML tag that is inflating the view.
     * @param defStyle
     *            The default style to apply to this view. If 0, no style will
     *            be applied (beyond what is included in the theme). This may
     *            either be an attribute resource, whose value will be retrieved
     *            from the current theme, or an explicit style resource.
     * @param defStyleRes
     *            The default style to apply to this view. If 0, no style will
     *            be applied (beyond what is included in the theme). This will
     *            be an explicit style resource.
     * @see #View(Context, AttributeSet)
     * @hide
     */
    public HtcFooterIconButton(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
        super(context, attrs, defStyle);
        mConfiguration = getResources().getConfiguration();
        init(attrs, defStyle, defStyleRes);
    }

    /**
     * @param attrs The AttributeSet come from constructor
     * @param defStyle  Default style for HtcFooterButton
     * Todo : Remember to fine tune performance by theme
     *     - use android:background="@null" instead of setBackgroundReosurce(0)
     *     - use htc:backgroundMode="light" instead of setBackgroundMode(0)
     *     - use android:singleLine="true" instead of setSingleLine(true);
     *     - use android:ellipsize="marquee" instead of etEllipsize(TruncateAt.MARQUEE);
     *     - use android:requiresFadingEdge="true" instead of setHorizontalFadingEdgeEnabled
     */
    private void init(AttributeSet attrs, int defStyle, int defStyleRes) {
        M3 = getResources().getDimensionPixelOffset(R.dimen.margin_s);
        M5 = getResources().getDimensionPixelOffset(R.dimen.spacing);
        // Since useSelectorWhenPressed() will override view's padding via
        // selector, move this action before setPadding();
        useSelectorWhenPressed(false);
        setButtonPaddings();
        setSingleLine(true);
        setEllipsize(TruncateAt.MARQUEE);
        setHorizontalFadingEdgeEnabled(true);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HtcFooter, defStyle, defStyleRes);
        mStyleMode = a.getInt(R.styleable.HtcFooter_backgroundMode, HtcFooter.STYLE_MODE_DEFAULT);
        mDisplayMode = a.getInt(R.styleable.HtcFooter_footer_display_mode, HtcFooter.DISPLAY_MODE_DEFAULT);
        a.recycle();
        if(mStyleMode == HtcFooter.STYLE_MODE_LIGHT) {
            setBackgroundResource(R.drawable.list_selector_light);
        }else{
            setBackgroundResource(R.drawable.list_selector_dark);
        }
        /*
         Although setCompoundDrawables() will be called after super(), the textAppearance didn't apply correctly.
         We have to call setButtonTextAppearance() in the init() to avoid unexpected UI problem
         */
        setButtonTextAppearance();
    }

    @Override
    public void useSelectorWhenPressed(boolean enabled) {
        setContentMultiplyOn(enabled);
    }

    /**
     * The function use to adjust HtcFooterIconButton TextView style by below factors
     *     - Portrait
     *     - Landscape
     *     - With icon
     *     - Without icon
     *     - Dark mode
     *     - Light mode
     * Note : If AP runtime change HtcFooterIconButton state, need to setButtonTextAppearance in a right timing.
     */
    private void setButtonTextAppearance() {
        Drawable[] dr = this.getCompoundDrawables();
        if (dr != null)
            mImage = dr[1];
        if (isInRight())
            setRightTextAppearance();
        else
            setBottomTextAppearance();
    }

    /**
     * 在HtcFooterIconButton显示在荧幕底部的时候，设置它的fontstyle
     * Dark mode
     *     - With Icon : FK05
     *     - Without Icon : FBD03
     * Light mode
     *     - With Icon : FHL08
     *     - Without Icon : FK04
     */
    private void setBottomTextAppearance() {
        if (mStyleMode == HtcFooter.STYLE_MODE_LIGHT) {
            if (mImage != null)
                setTextAppearance(getContext(), R.style.fixed_label_off_m);
            else
                setTextAppearance(getContext(), R.style.fixed_separator_secondary_m);
        } else {
            if (mImage != null)
                setTextAppearance(getContext(), R.style.fixed_label_on_m);
            else
                setTextAppearance(getContext(), R.style.fixed_b_button_primary_m);
        }
    }

    /**
     * 在HtcFooterIconButton显示在右边的时候，设置它的fontstyle
     * Dark mode
     *     - With Icon : FK05
     *     - Without Icon : FBD03
     * Light mode
     *     - With Icon : FK04
     *     - Without Icon : FHL06
     */
    private void setRightTextAppearance() {
        if (mStyleMode == HtcFooter.STYLE_MODE_LIGHT) {
            if (mImage != null)
                setTextAppearance(getContext(), R.style.fixed_label_off_m);
            else
                setTextAppearance(getContext(), R.style.fixed_separator_secondary_xs);
        } else {
            if (mImage != null)
                setTextAppearance(getContext(), R.style.fixed_label_on_m);
            else
                setTextAppearance(getContext(), R.style.fixed_list_body_xs);
        }
    }

    /**
     * 判断HtcFooterIconButton是否显示在荧幕的右面
     *
     * @return 如果显示在右面返回 true， 显示在荧幕的下面 false
     */
    private boolean isInRight() {
        if (mDisplayMode == HtcFooter.DISPLAY_MODE_ALWAYSRIGHT) {
            return true;
        } else if (mDisplayMode == HtcFooter.DISPLAY_MODE_ALWAYSBOTTOM) {
            return false;
        } else {
            //在某些情况下，mConfiguration 是 null，有可能造成 UI 的issue，目前还不知道造成mConfiguration为null的原因
            if ( null != mConfiguration && (mConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) ) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Override setCompoundDrawables for AP to runtime setIconDrawable()
     * Then adjust the TextView style case by case
     * (non-Javadoc)
     * @see com.htc.lib1.cc.widget.HtcIconButton#setCompoundDrawables(android.graphics.drawable.Drawable, android.graphics.drawable.Drawable, android.graphics.drawable.Drawable, android.graphics.drawable.Drawable)
     * @hide
     */
    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawables(left, top, right, bottom);
        setButtonTextAppearance();
    }

    /**
     * @hide
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // TODO Auto-generated method stub
        super.onLayout(changed, left, top, right, bottom);
        setButtonPaddings();
    }

    /**
     * set Paddings in different conditions
     */
    private void setButtonPaddings() {
        if (isInRight())
            setPadding(M5, 0, M5, 0);
        else
            setPadding(M3, 0, M3, 0);
    }

    /**
     * The function was follow UIGL from Sense50 that all textView in commit bar should convert to upper case
     * HtcResUtil.isInAllCapsLocale(getContext() will handle and convert some Western locale only.
     * (non-Javadoc)
     * @see android.widget.TextView#setText(java.lang.CharSequence, android.widget.TextView.BufferType)
     * @hide
     */
    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!HtcResUtil.isInAllCapsLocale(getContext())) {
            super.setText(text, type);
        } else {
            CharSequence uppercase = text.toString().toUpperCase();
            super.setText(uppercase, type);
        }
    }

}
