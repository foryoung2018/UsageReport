package com.htc.lib1.cc.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;

import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.util.res.HtcResUtil;

public class HtcListItemBubbleCount extends View implements IHtcListItemControl {
    @ExportedProperty(category = "CommonControl")
    private String mText;
    private TextPaint mPaint = new TextPaint();
    private FontMetrics mFontMetrics = null;
    @ExportedProperty(category = "CommonControl")
    private float mFontHeight = 0;
    @ExportedProperty(category = "CommonControl")
    private int mBubbleWidth = 0;

    private void init(Context context) {
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        setVisibility(View.GONE);

        HtcResUtil.setTextAppearance(context,
                com.htc.lib1.cc.R.style.fixed_notification_info_m, mPaint);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setAntiAlias(true);
        mFontMetrics = mPaint.getFontMetrics();
        mFontHeight = mFontMetrics.bottom - mFontMetrics.top;
        mPaint.setColor(HtcCommonUtil.getCommonThemeColor(context, com.htc.lib1.cc.R.styleable.ThemeColor_light_category_color));
    }

    /**
     * Simple constructor to use when creating this widget from code. It will
     * new a view with default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItemBubbleCount(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor that is called when inflating this widget from XML. This is
     * called when a view is being constructed from an XML file, supplying
     * attributes that were specified in the XML file.It will new a view with
     * default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemBubbleCount(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of this widget allows subclasses to use their own base style
     * when they are inflating. It will new a view with default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @param defStyle The default style to apply to this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemBubbleCount(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setPadding(int left, int top, int right, int bottom) {
    }

    /**
     * @deprecated [Not use any longer] This method will no longer be supported
     *             in Sense 5.0 due to design change
     */
    /** @hide */
    public void setBubbleCount(String text) {
    }

    private static final int MAX_BOUND = 1000;
    @ExportedProperty(category = "CommonControl")
    private int mUpperBound = MAX_BOUND;

    /**
     * Use this API to set an integer in parenthesis
     *
     * @param count an integer to set in parenthesis
     */
    public void setBubbleCount(int count) {
        String oldBubbleCount = mText;
        if (count <= 0) {
            mText = "";
        } else if (count < mUpperBound) {
            mText = "(" + String.valueOf(count) + ")";
        } else {
            mText = "(" + String.valueOf(mUpperBound - 1) + "+)";
        }
        if (!mText.equals(oldBubbleCount)) {
            mBubbleWidth = (int) mPaint.measureText(mText);
            setVisibility(mText.equals("") ? View.GONE : View.VISIBLE);
            requestLayout();
            invalidate();
        }
    }

    /**
     * Use this API to set upper bound of count
     *
     * @param upperBound The upper bound of count
     */
    public void setUpperBound(int upperBound) {
        if (upperBound > 0 && upperBound < MAX_BOUND)
            mUpperBound = upperBound;
    }

    /**
     * @deprecated [Not use any longer] This method will no longer be supported
     *             in Sense 5.0 due to design change
     */
    /** @hide */
    public CharSequence getText() {
        return mText;
    }

    /** if false, views will be disabled and alpha value will be set to 0.4 */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled)
            return;
        super.setEnabled(enabled);
        HtcListItemManager.setViewOpacity(this, enabled);
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void onMeasure(int wSpec, int hSpec) {
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(mBubbleWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((int) mFontHeight, MeasureSpec.EXACTLY));
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void onDraw(Canvas canvas) {
        float textBaseY = getHeight() - (getHeight() - mFontHeight) / 2
                - mFontMetrics.bottom;
        int length = (mText == null ? 0 : mText.length());

        if (mText != null)
            canvas.drawText(mText, 0, length, getWidth() / 2, textBaseY, mPaint);
    }
}
