package com.htc.lib1.cc.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;

import com.htc.lib1.cc.widget.MyTableView;
import com.htc.lib1.cc.R;

/**
 * HtcDateTimeText
 */
public class HtcDateTimeText extends View {
    private Paint mPaint;

    @ExportedProperty(category = "CommonControl")
    private String mText;

    @ExportedProperty(category = "CommonControl")
    private String mFontFamily;

    @ExportedProperty(category = "CommonControl")
    private int mTextColor;

    @ExportedProperty(category = "CommonControl")
    private float mTextSize;
    private MyTableView mTableView;
    private String mKeyOfDateTimeText;

    private class DateTimeTextShadow {
        private float radius, dx, dy;
        private int shadowColor;

        public DateTimeTextShadow(float r, float x, float y, int c) {
            this.updateShadowStyle(r, x, y, c);
        }

        public void updateShadowStyle(float r, float x, float y, int c) {
            this.radius = r;
            this.dx = x;
            this.dy = y;
            this.shadowColor = c;
        }
    };

    private DateTimeTextShadow[] mShadowStyles = {null, null, null};

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcDateTimeText(Context context) {
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
    public HtcDateTimeText(Context context, AttributeSet attrs) {
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
    public HtcDateTimeText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //Set default shadow style
        mShadowStyles[0] = new DateTimeTextShadow(0.1f, 0f, 0f, 0xffb4b4b4);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Align.CENTER);
        mPaint = paint;

        this.setTextStyle(R.style.fixed_time_pick_primary_m);
    }

    void setTableView(MyTableView tableView, String key) {
        mTableView = tableView;
        mKeyOfDateTimeText = key;
    }

    /**
     * Called to determine the size requirements for this view and all of its children.
     * @param widthMeasureSpec horizontal space requirements as imposed by the parent. The requirements are encoded with View.MeasureSpec.
     * @param heightMeasureSpec vertical space requirements as imposed by the parent. The requirements are encoded with View.MeasureSpec.
     * @hide
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = (mTableView==null ? MeasureSpec.getSize(heightMeasureSpec) : mTableView.getMyTableChildHeight());
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    /**
     * Called when this view should assign a size and position to all of its children.
     * @param changed This is a new size or position for this view
     * @param left Left position, relative to parent
     * @param top Top position, relative to parent
     * @param right Right position, relative to parent
     * @param bottom Bottom position, relative to parent
     * @hide
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * Implement this to do your drawing.
     * @param canvas the canvas on which the background will be drawn
     * @hide
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mText == null)
            throw new IllegalArgumentException("[HtcDateTimeText.onDraw] mText is null!");

        float viewWidth = getWidth(), viewHeight = getHeight();
        float centerX = viewWidth / 2.0f, centerY = viewHeight / 2.0f;

        //find the vertical center aligned Y position, newCenterY
        Rect bounds = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), bounds);
        float hOffset = bounds.width() / 2.0f, vOffset = bounds.height() / 2.0f;
        float newCenterY = centerY + vOffset;
        DateTimeTextShadow tmpShadow;

        //draw shadows from bottom-most to top-most if necessary
        for (int layer=mShadowStyles.length-1; layer>=0; layer--) {
            tmpShadow = mShadowStyles[layer];
            if (tmpShadow == null) continue;
            mPaint.setColor(tmpShadow.shadowColor);
            canvas.drawText(mText, centerX, newCenterY+tmpShadow.dy, mPaint);
        }

        //draw the text
        mPaint.setColor(mTextColor);
        canvas.drawText(mText, centerX, newCenterY, mPaint);
    }

    /**
     * To set the text for this view to draw.
     * @param text The text will be drawn
     */
    public void setText(String text) {
        mText = text;
        invalidate();
    }

    /**
     * To get the text this view draws.
     * @return The text this view draws.
     */
    public String getText() {
        return mText;
    }

    /**
     * To set the text size in px, this API will not trigger draw.
     * @param size The text size in pixel.
     */
    public void setTextSize(float size) {
        if (mPaint == null) return;
        mTextSize = (size>0 ? size : mTextSize);
        mPaint.setTextSize(mTextSize);
        invalidate();
    }

    public float getTextSize() {
        return this.mTextSize;
    }

    /**
     * To set text color, this API will not trigger draw.
     * @param color The resource id for the color.
     */
    public void setTextColor(int color) {
        if (mPaint == null) return;
        mTextColor = color;
        mPaint.setColor(color);
        invalidate();
    }

    public int getTextColor() {
      return this.mTextColor;
    }

    private String[] supportStrs = { "sans-serif", "sans-serif-light", "sans-serif-condensed" };

    /**
     * To set the font face, this aPI will not trigger draw.
     * @param family The font type face, and it currently supports just only "sans-serif", "sans-serif-light" and "sans-serif-condensed" which are defined in UIGL50.
     */
    public void setTextFontFamily(String family) {
        if (family == null || mPaint == null) return;

        for (int i=0; i<supportStrs.length; i++) {
            if (family.equals(supportStrs[i])) {
                mFontFamily = family;
                mPaint.setTypeface(android.graphics.Typeface.create(mFontFamily, android.graphics.Typeface.NORMAL));
                break;
            }
        }

        invalidate();
    }

    public String getFontFamily() {
      return this.mFontFamily;
    }

    /**
     * To set the text style, just size, color and type face will take effects.
     * @param style The resource id of the font style.
     */
    public void setTextStyle(int style) {
        int [] parseAttrs = new int[] { android.R.attr.textSize, android.R.attr.textColor, android.R.attr.fontFamily };
        android.content.res.TypedArray a = getContext().getTheme().obtainStyledAttributes(style, parseAttrs);

        if (a != null) {
            this.setTextSize(a.getDimensionPixelSize(0, -1));
            this.setTextColor(a.getColor(1, -1));
            this.setTextFontFamily(a.getString(2));
            a.recycle();
        }
    }

    /**
     * To set the shadow style of the text.
     * @param layer The layer of this shadow, the lower the layer draws first.
     * @param radius Radius of the shadow. Must be a floating point value, such as "1.2".
     * @param dx Horizontal offset of the shadow. Must be a floating point value, such as "1.2".
     * @param dy Vertical offset of the shadow. Must be a floating point value, such as "1.2".
     * @param color Place a shadow of the specified color behind the text. Must be a color value, in the form of "#rgb", "#argb", "#rrggbb", or "#aarrggbb".
     */
    public void setCustomShadow(int layer, float radius, float dx, float dy, int color) {
        if (layer >= 0 && layer <= mShadowStyles.length) {
            if (mShadowStyles[layer] == null) {
                mShadowStyles[layer] = new DateTimeTextShadow(radius, dx, dy, color);
            } else {
                mShadowStyles[layer].updateShadowStyle(radius, dx, dy, color);
            }
        }
    }
}

