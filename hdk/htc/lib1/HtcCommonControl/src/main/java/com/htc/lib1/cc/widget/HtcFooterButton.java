
package com.htc.lib1.cc.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewDebug;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.util.res.HtcResUtil;
import com.htc.lib1.cc.util.WindowUtil;

public class HtcFooterButton extends TextView {
    private final static String TAG = "HtcFooterButton";

    @ViewDebug.ExportedProperty
    Drawable mImage = null;

    @ViewDebug.ExportedProperty(category = "layout")
    private boolean isEmpty = false;

    @ViewDebug.ExportedProperty(category = "layout")
    private boolean mIsSingleLine = true;

    @ViewDebug.ExportedProperty(category = "layout")
    private int mCheckedColor;

    @ViewDebug.ExportedProperty(category = "layout")
    private int mDisplayMode = HtcFooter.DISPLAY_MODE_DEFAULT;

    @ViewDebug.ExportedProperty(category = "layout")
    private int mStyleMode = HtcFooter.STYLE_MODE_DEFAULT;

    @ViewDebug.ExportedProperty(category = "layout")
    private int paddingLeft_M3 = 0;

    @ViewDebug.ExportedProperty(category = "layout")
    private int paddingRight_M3 = 0;

    @ViewDebug.ExportedProperty(category = "layout")
    private int paddingLeft_M5 = 0;

    @ViewDebug.ExportedProperty(category = "layout")
    private int paddingRight_M5 = 0;

    @ViewDebug.ExportedProperty(category = "layout")
    private final static int MAX_LINE_1 = 1;

    /* follow Sense70 v1.0 page 34 */
    @ViewDebug.ExportedProperty(category = "layout")
    private final static int FULL_OPAQUE=255;

    @ViewDebug.ExportedProperty(category = "layout")
    private final static int DARK_MODE_DISABLE_ALPHA =102;

    @ViewDebug.ExportedProperty(category = "layout")
    private final static int LIGHT_MODE_DISABLE_ALPHA =128;

    @ViewDebug.ExportedProperty(category = "measurement")
    private int dWidth;

    @ViewDebug.ExportedProperty(category = "measurement")
    private int dHeight;

    @ViewDebug.ExportedProperty(category = "measurement")
    private int tvlHeight;

    @ViewDebug.ExportedProperty(category = "measurement")
    private int vHeight;

    private static final boolean isLollipopMr1 = Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcFooterButton(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called when a view is being constructed from
     * an XML file, supplying attributes that were specified in the XML file. This version uses a default style of 0, so
     * the only attribute values applied are those in the Context's Theme and the given AttributeSet.
     * <p>
     * The method onFinishInflate() will be called after all children have been added.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     */
    public HtcFooterButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This constructor of View allows subclasses to
     * use their own base style when they are inflating. For example, a Button class's constructor would call this
     * version of the super class constructor and supply <code>R.attr.buttonStyle</code> for <var>defStyle</var>; this
     * allows the theme's button style to modify all of the base view attributes (in particular its background) as well
     * as the Button class's attributes.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view. If 0, no style will be applied (beyond what is included
     *            in the theme). This may either be an attribute resource, whose value will be retrieved from the
     *            current theme, or an explicit style resource.
     * @see #View(Context, AttributeSet)
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcFooterButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HtcFooterButton,
                R.attr.footerButtonStyle, R.style.FooterBarButtonStyle);
        Drawable d = a.getDrawable(R.styleable.HtcFooterButton_android_src);
        mIsSingleLine = a.getBoolean(R.styleable.HtcFooterButton_android_singleLine, true);
        setImageDrawable(d);
        a.recycle();

        int M3 = context.getResources().getDimensionPixelOffset(R.dimen.margin_s);
        int M5 = context.getResources().getDimensionPixelOffset(R.dimen.spacing);
        mCheckedColor = HtcCommonUtil.getCommonThemeColor(context, R.styleable.ThemeColor_category_color);

        a = context.obtainStyledAttributes(attrs, R.styleable.View);
        paddingLeft_M5 = a.getDimensionPixelOffset(R.styleable.View_android_paddingLeft, M5);
        paddingRight_M5 = a.getDimensionPixelOffset(R.styleable.View_android_paddingRight, M5);
        paddingLeft_M3 = a.getDimensionPixelOffset(R.styleable.View_android_paddingLeft, M3);
        paddingRight_M3 = a.getDimensionPixelOffset(R.styleable.View_android_paddingRight, M3);
        a.recycle();

        a = context.obtainStyledAttributes(attrs, R.styleable.HtcFooter);
        mDisplayMode = a.getInt(R.styleable.HtcFooter_footer_display_mode,
                HtcFooter.DISPLAY_MODE_DEFAULT);
        mStyleMode = a.getInt(R.styleable.HtcFooter_backgroundMode, HtcFooter.STYLE_MODE_DEFAULT);
        a.recycle();

        isEmpty = isTextEmpty();
        if(mStyleMode == HtcFooter.STYLE_MODE_LIGHT) {
            setBackgroundResource(R.drawable.list_selector_light);
        }else{
            setBackgroundResource(R.drawable.list_selector_dark);
        }
        setClickable(true);
        setSingleLine(mIsSingleLine);
        if(mIsSingleLine){
            setMaxLines(MAX_LINE_1);
        }
        setEllipsize(TruncateAt.MARQUEE);
        setHorizontalFadingEdgeEnabled(true);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setFooterButtonAppearance();
    }

    /**
     * Set FooterButton Text.
     */
    public void setText(CharSequence text, BufferType type) {
        if (!HtcResUtil.isInAllCapsLocale(getContext())) {
            super.setText(text, type);
        } else {
            if (null == text) {
                text = "";
            }
            CharSequence uppercase = text.toString().toUpperCase();
            super.setText(uppercase, type);
        }
        isEmpty = isTextEmpty();
    }

    /**
     * set footerButton Icon resource id
     *
     * @param id Image Resource id
     */
    public void setImageResource(int id) {
        Resources rsrc = getResources();
        if (0 != id) {
            Drawable d = rsrc.getDrawable(id);
            setImageDrawable(d);
        }else{
            setImageDrawable(null);
        }
    }

    /**
     * set footerButton icon bitmap
     *
     * @param bitmap Image Bitmap
     */
    public void setImageBitmap(Bitmap bitmap) {
        setImageDrawable(new BitmapDrawable(getContext().getResources(), bitmap));
    }

    /**
     * set footerButton icon drawable
     *
     * @param mImage Image Drawable
     */
    public void setImageDrawable(Drawable mImage) {
        this.mImage = mImage;
        if (null != mImage) {
            dWidth = mImage.getIntrinsicWidth();
            dHeight = mImage.getIntrinsicHeight();
        }else{
            dWidth = 0;
            dHeight = 0;
        }
        setFooterButtonAppearance();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect canvasRect = canvas.getClipBounds();
        Layout tvLayout = getLayout();
        if (null == mImage || null == tvLayout) {
            safeDraw(canvas, canvasRect);
            return;
        }
        tvlHeight = tvLayout.getHeight();
        if (isEmpty) {
            tvlHeight = 0;
        }
        float yOffset = (vHeight - dHeight - tvlHeight) * 0.5f;
        canvas.save();
        canvas.translate((canvasRect.left + canvasRect.right - dWidth) * 0.5f, yOffset);
        mImage.draw(canvas);
        canvas.restore();
        safeDraw(canvas, canvasRect);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        vHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        initFooterButton();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (null != getLayout()) {
            presetTextPadding();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private boolean isTextEmpty() {
        if (TextUtils.isEmpty(this.getText()) || this.getText().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    private void presetTextPadding() {
        //Need test isHorizontal() return is always right,
        //when user change orientation frequent.
        switch (mDisplayMode) {
            case HtcFooter.DISPLAY_MODE_ALWAYSBOTTOM:
                setTextPadding(HtcFooter.DISPLAY_MODE_ALWAYSBOTTOM);
                break;
            case HtcFooter.DISPLAY_MODE_ALWAYSRIGHT:
                setTextPadding(HtcFooter.DISPLAY_MODE_ALWAYSRIGHT);
                break;
            default:
                if (isHorizontal())
                    setTextPadding(HtcFooter.DISPLAY_MODE_ALWAYSRIGHT);
                else
                    setTextPadding(HtcFooter.DISPLAY_MODE_ALWAYSBOTTOM);
                break;
        }
    }

    private void setTextPadding(int mMode) {
        Layout tvLayout = getLayout();
        int yOffset = 0;
        if (null != tvLayout) {
            tvlHeight = tvLayout.getHeight();
            yOffset = (vHeight - dHeight - tvlHeight) / 2 + dHeight;
        }
        if (mMode == HtcFooter.DISPLAY_MODE_ALWAYSRIGHT) {
            setPadding(paddingLeft_M5, yOffset, paddingRight_M5, 0);
        } else {
            setPadding(paddingLeft_M3, yOffset, paddingRight_M3, 0);
        }
    }

    /**
     * @param mode DISPLAY_MODE_DEFAULT, DISPLAY_MODE_ALWAYSRIGHT, DISPLAY_MODE_ALWAYSBOTTOM
     * @hide
     */
    protected void setDisplayMode(int mode) {
        mDisplayMode = mode;
        initFooterButton();
    }

    /**
     * set Footer Button's appearance e.g. text FADE_OUT, etc
     */
    private void setFooterButtonAppearance() {
        switch (mDisplayMode) {
            case HtcFooter.DISPLAY_MODE_ALWAYSBOTTOM:
                setBottomTextAppearance();
                break;
            case HtcFooter.DISPLAY_MODE_ALWAYSRIGHT:
                setRightTextAppearance();
                break;
            default:
                if (isHorizontal()) {
                    setRightTextAppearance();
                } else {
                    setBottomTextAppearance();
                }
                break;
        }
        if (mImage != null) {
            mImage.setBounds(0, 0, dWidth, dHeight);
        }
    }

    private boolean isHorizontal() {
        return WindowUtil.isSuitableForLandscape(getResources());
    }

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
     * init FooterButton, when it is changed.
     */
    private void initFooterButton() {
        if (null == getContext()) {
            return;
        }
        setEnabled(isEnabled());
        ColorStateList cs = getTextColors();
        setFooterButtonAppearance();
        setTextColor(cs);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (null != mImage) {
            int alpha = FULL_OPAQUE;
            if (enabled == false) {
                alpha = (mStyleMode == HtcFooter.STYLE_MODE_LIGHT) ? LIGHT_MODE_DISABLE_ALPHA
                        : DARK_MODE_DISABLE_ALPHA;
            }
            (mImage.mutate()).setAlpha(alpha);
        }
        super.setEnabled(enabled);
    }

    /**
     * setFooterButton in colorFul mode
     * @param open - true, FooterButton change color with checked state;
     *                            false, FooterButton not change color.
     */
    private void setColorSwitch(boolean open){
        if(open){
            if(null != mImage){
                (mImage.mutate()).setColorFilter(mCheckedColor, PorterDuff.Mode.SRC_IN);
            }
            setTextColor(mCheckedColor);
        }else{
            if(null != mImage){
                mImage.setColorFilter(null);
            }
            setFooterButtonAppearance();
        }
    }

    /**
     * StyleMode should set to {@link HtcFooter#STYLE_MODE_COLORFUL}.
     * @param isChecked - If HtcFooterButton is checked.
     */
    public void enableColorFul(boolean isChecked) {
        if(mStyleMode == HtcFooter.STYLE_MODE_COLORFUL){
            setColorSwitch(isChecked);
        }
    }
}
