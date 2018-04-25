package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.res.HtcResUtil;

/**
 * Serial Number in frame.
 * <ul>
 * <li>Number Only
 *
 * <pre class="prettyprint">
 *      &lt;com.htc.widget.HtcListItemSerialNumber
 *       android:id="@+id/number"/&gt;
 * </pre>
 *
 * </ul>
 */
public class HtcListItemSerialNumber extends HtcListItemImageComponent implements
        IHtcListItemComponentNoLeftTopMargin {

    @ExportedProperty(category = "CommonControl")
    private int mNumber = 0;
    @ExportedProperty(category = "CommonControl")
    private String mNumberString = "0";
    private TextPaint mNumberPaint = new TextPaint();

    // The text bound of this number
    private Rect mNumberBound = new Rect();
    @ExportedProperty(category = "CommonControl")
    private boolean mIsDarkModeEnabled = false;

    // The resource ID of Dark mode background
    @ExportedProperty(category = "CommonControl", resolveId = true)
    private int mDarkModeBgResId = 0;

    private void init(Context context) {
        HtcResUtil.setTextAppearance(context, R.style.list_body_secondary_xl, mNumberPaint);
        getNumberBounds(mNumber);
        setWillNotDraw(false);
    }

    private void init(Context context, AttributeSet attrs) {
        init(context);
        TypedArray a = context.obtainStyledAttributes(attrs,
                com.htc.lib1.cc.R.styleable.HtcListItemSerialNumber);
        mDarkModeBgResId = a.getResourceId(
                com.htc.lib1.cc.R.styleable.HtcListItemSerialNumber_android_background,
                R.drawable.common_photo_frame);
        a.recycle();
    }

    /**
     * Simple constructor to use when creating a HtcListItemSerialNumber from
     * code.
     *
     * @param context The Context the HtcListItemSerialNumber is running in,
     *            through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItemSerialNumber(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @param context The Context the HtcListItemSerialNumber is running in,
     *            through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the
     *            HtcListItemSerialNumber.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemSerialNumber(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style.
     *
     * @param context The Context the HtcListItemSerialNumber is running in,
     *            through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the
     *            HtcListItemSerialNumber.
     * @param defStyle The default style to apply to this
     *            HtcListItemSerialNumber. If 0, no style will be applied
     *            (beyond what is included in the theme). This may either be an
     *            attribute resource, whose value will be retrieved from the
     *            current theme, or an explicit style resource.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemSerialNumber(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setLayoutParams(ViewGroup.LayoutParams params) {

        params.width = mComponentWidth;
        params.height = mComponentHeight;

        super.setLayoutParams(params);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public ViewGroup.LayoutParams getLayoutParams() {
        if (super.getLayoutParams() != null)
            return super.getLayoutParams();
        else {
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(mComponentWidth,
                    mComponentHeight);
            super.setLayoutParams(params);
            return params;
        }
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setPadding(int left, int top, int right, int bottom) {
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    protected void onMeasure(int w, int h) {
        super.onMeasure(w, h);
        setMeasuredDimension(mComponentWidth, mComponentHeight);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int viewWidth = this.getWidth();
        final int viewHeight = this.getHeight();
        int left, top;

        left = (viewWidth - mNumberBound.width()) / 2 - mNumberBound.left;
        top = (viewHeight - mNumberBound.height()) / 2 - mNumberBound.top;

        if (mNumberString != null) {
            canvas.drawText(mNumberString, left, top, mNumberPaint);
        }
    }

    /**
     * Set the background to dark mode.
     *
     * @param isDarkModeEnabled True to use dark background, false otherwise.
     */
    public void setDarkMode(boolean isDarkModeEnabled) {
        if (mIsDarkModeEnabled != isDarkModeEnabled) {
            mIsDarkModeEnabled = isDarkModeEnabled;
            if (mIsDarkModeEnabled == true) {
                this.setBackgroundResource(mDarkModeBgResId);
            } else {
                this.setBackground(null);
            }
        }
    }

    /**
     * Set the serial number.
     *
     * @param number The serial number to show in frame.
     */
    public void setNumber(int number) {
        if (mNumber != number) {
            mNumber = number;
            getNumberBounds(mNumber);
            invalidate();
        }
    }

    /**
     * Get the serial number.
     *
     * @return The serial number in frame.
     * @deprecated [Not use any longer]
     */
    /** @hide */
    @ExportedProperty(category = "CommonControl")
    public int getNumber() {
        return mNumber;
    }

    /**
     * Initialize for number string and bound.
     */
    private void getNumberBounds(int number) {
        mNumberString = Integer.toString(mNumber);
        mNumberPaint.getTextBounds(mNumberString, 0, mNumberString.length(), mNumberBound);
    }
}
