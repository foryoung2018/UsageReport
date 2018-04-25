package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;

import com.htc.lib1.cc.util.res.HtcResUtil;
import com.htc.lib1.cc.R;

/**
 * HtcFooterTextButton extends HtcIconButton. This is a lightly footer button
 * which contains only text.
 *
 * @author vincent.yw_wang
 */
public class HtcFooterTextButton extends HtcIconButton {

    private final static String mSpace = ".*\\s.*";

    private Resources mResources = null;
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
    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context
     *            The Context the view is running in, through which it can
     *            access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcFooterTextButton(Context context) {
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
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcFooterTextButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.footerStyle);
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
    public HtcFooterTextButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mResources = getContext().getResources();
        mConfiguration = mResources.getConfiguration();
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HtcFooter, defStyle, R.style.FooterBarStyle);
        mStyleMode = a.getInt(R.styleable.HtcFooter_backgroundMode, HtcFooter.STYLE_MODE_DEFAULT);
        mDisplayMode = a.getInt(R.styleable.HtcFooter_footer_display_mode, HtcFooter.DISPLAY_MODE_DEFAULT);
        a.recycle();
        M3 = mResources.getDimensionPixelOffset(R.dimen.margin_s);
        M5 = mResources.getDimensionPixelOffset(R.dimen.spacing);
        // Since useSelectorWhenPressed() will override view's padding via
        // selector, move this action before setPadding();
        useSelectorWhenPressed(false);
        if(mStyleMode == HtcFooter.STYLE_MODE_LIGHT) {
            setBackgroundResource(R.drawable.list_selector_light);
        }else{
            setBackgroundResource(R.drawable.list_selector_dark);
        }
        setButtonPaddings();
    }

    @Override
    public void useSelectorWhenPressed(boolean enabled) {
        setContentMultiplyOn(enabled);
    }

    private void setButtonAppearance() {

        CharSequence temp = this.getText();
        if (temp != null || TextUtils.isEmpty(temp)) {
            temp = this.getText();
            if (false == temp.toString().matches(mSpace)) {
                setSingleLine(true);
                setMaxLines(1);
                setEllipsize(TruncateAt.MARQUEE);
                setHorizontalFadingEdgeEnabled(true);
            } else {
                setSingleLine(false);
                setMaxLines(2);
                setEllipsize(TruncateAt.END);
            }
        }

        if (isInRight()) {
            if (mStyleMode == HtcFooter.STYLE_MODE_LIGHT)
                setTextAppearance(getContext(), R.style.fixed_separator_secondary_xs);
            else
                setTextAppearance(getContext(), R.style.fixed_list_body_xs);
        } else {
            if (mStyleMode == HtcFooter.STYLE_MODE_LIGHT)
                setTextAppearance(getContext(), R.style.fixed_separator_secondary_m);
            else
                setTextAppearance(getContext(), R.style.fixed_b_button_primary_m);
        }

    }

    /**
     * 判断HtcFooterTextButton是否显示在荧幕的右面
     * @return 如果显示在右面返回 true， 显示在荧幕的下面 false
     */
    @ExportedProperty(category = "CommonControl")
    private boolean isInRight() {
        if (mDisplayMode == HtcFooter.DISPLAY_MODE_ALWAYSRIGHT) {
            return true;
        } else if (mDisplayMode == HtcFooter.DISPLAY_MODE_ALWAYSBOTTOM) {
            return false;
        } else {
            if (mConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * @hide
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setButtonAppearance();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * @hide
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
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

    /**
     * Use to enable automotivemode
     *
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setAutoMotiveMode(boolean enable) {
    }
}
