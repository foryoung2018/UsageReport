package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.LogUtil;

/**
 * A Layout that always show a label text on the top. The added child view will
 * be placed on the bottom. Can only host one direct child view. No Background
 * by default.
 */
public class HtcListItemLabeledLayout extends ViewGroup implements IHtcListItemTextComponent,
        IHtcListItemComponent {

    private final static String TAG = "HtcListItemLabeledLayout";

    HtcFadingEdgeTextView mLabelView;
    View mControl;
    @ExportedProperty(category = "CommonControl")
    int mLabelMarginTop = 4;
    @ExportedProperty(category = "CommonControl")
    int mMarginLeftAndRight = 16;
    @ExportedProperty(category = "CommonControl")
    int mPaddingLeftAndRight = 0;
    @ExportedProperty(category = "CommonControl")
    int mVerticalGap = 4;
    @ExportedProperty(category = "CommonControl")
    int mControlMarginBottom = 4;
    @ExportedProperty(category = "CommonControl")
    CharSequence mLabel = "";
    @ExportedProperty(category = "CommonControl")
    private boolean mAllCapsConfirmed = false;
    @ExportedProperty(category = "CommonControl")
    private boolean mAllCaps = true;

    @ExportedProperty(category = "CommonControl")
    private boolean mDescriptionText = false;

    private void checkAllCaps() {
        if (!mAllCapsConfirmed) {
            mAllCaps = com.htc.lib1.cc.util.res.HtcResUtil.isInAllCapsLocale(getContext());
            mAllCapsConfirmed = true;
            setDefaultTextStyle();
        }
    }

    /**
     * Sets the properties of this field to transform input to all lowercase
     * display.
     */
    public void setLabelTextAllCapsFalse() {
        if (mAllCaps) {
            mAllCaps = false;
            mAllCapsConfirmed = true;
            mDescriptionText = true;
            setDefaultTextStyle();
        }
    }

    private void setDefaultTextStyle() {
        if (mItemMode == HtcListItem.MODE_KEEP_MEDIUM_HEIGHT) {
            if (mDescriptionText)
                mLabelView.setTextStyle(com.htc.lib1.cc.R.style.fixed_list_body_primary_m);
            else
                mLabelView.setTextStyle(com.htc.lib1.cc.R.style.fixed_separator_primary_l);
        } else {
            if (mDescriptionText)
                mLabelView.setTextStyle(com.htc.lib1.cc.R.style.list_body_primary_m);
            else
                mLabelView.setTextStyle(com.htc.lib1.cc.R.style.separator_primary_l);
        }
    }

    /**
     * Simple constructor to use when creating this widget from code. It will
     * new a view with default text style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     */
    public HtcListItemLabeledLayout(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating this widget from XML. This is
     * called when a view is being constructed from an XML file, supplying
     * attributes that were specified in the XML file.It will new a view with
     * default text style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemLabeledLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of this widget allows subclasses to use their own base style
     * when they are inflating. It will new a view with default text style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @param defStyle The default style to apply to this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemLabeledLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        mLabelView = new HtcFadingEdgeTextView(context);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextView, defStyle, 20);
        CharSequence label = a.getText(R.styleable.TextView_android_text);
        boolean allCaps = a.getBoolean(R.styleable.TextView_android_textAllCaps, true);
        a.recycle();

        Resources res = context.getResources();
        mLabelMarginTop = res.getDimensionPixelOffset(R.dimen.margin_m);
        mMarginLeftAndRight = res.getDimensionPixelOffset(R.dimen.margin_l);
        mVerticalGap = res.getDimensionPixelOffset(R.dimen.margin_m);
        mControlMarginBottom = res.getDimensionPixelOffset(R.dimen.margin_l);
        if (!allCaps) {
            setLabelTextAllCapsFalse();
        }
        ((HtcFadingEdgeTextView) mLabelView).setEnableMarquee(false);
        if (label != null) {
            setLabelText(label);
        }
        addView(mLabelView);
    }

    private boolean compareText(CharSequence text1, CharSequence text2) {
        if (text1 == null && text2 == null)
            return true;
        if (text1 != null && text1.equals(text2))
            return true;
        return false;
    }

    /**
     * Set the label text. But if you set the text with SpannableString,It will
     * not transform input text to ALL CAPS, and if the text need display with
     * ALL CAPS,please transforms source text into an ALL CAPS String before
     * call it.
     *
     * @param text The label text
     */
    public void setLabelText(CharSequence text) {
        if (compareText(mLabel, text))
            return;
        mLabel = text;
        if (mLabel == null)
            mLabel = "";
        if (mLabel instanceof Spannable) {
            mLabelView.setText(mLabel);
        } else {
            checkAllCaps();
            mLabelView.setText(mAllCaps ? mLabel.toString().toUpperCase() : mLabel.toString());
        }
    }

    /**
     * Set the label text
     *
     * @param text The label text
     */
    public void setLabelText(String text) {
        if (compareText(mLabel, text))
            return;
        mLabel = text;
        if (mLabel == null)
            mLabel = "";
        checkAllCaps();
        mLabelView.setText(mAllCaps ? mLabel.toString().toUpperCase() : mLabel.toString());
    }

    /**
     * Set the label text
     *
     * @param resId the resource ID of the label text
     */
    public void setLabelText(int resId) {
        String text = getContext().getResources().getString(resId);
        setLabelText(text);
    }

    /**
     * Get the label text
     *
     * @return CharSequence of label text
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public CharSequence getLabelText() {
        return mLabel;
    }

    @ExportedProperty(category = "CommonControl")
    int getControlHeight() {
        if (mControl != null && mControl.getVisibility() != View.GONE) {
            return mControl.getMeasuredHeight();
        } else {
            return 0;
        }
    }

    @ExportedProperty(category = "CommonControl")
    private int getComponentHeight() {
        return mLabelMarginTop + mLabelView.getMeasuredHeight() + mVerticalGap + getControlHeight()
                + mControlMarginBottom;
    }

    /**
     * <p>
     * Measure the view and its content to determine the measured width and the
     * measured height. This method is invoked by {@link #measure(int, int)} and
     * should be overriden by subclasses to provide accurate and efficient
     * measurement of their contents.
     * </p>
     *
     * @param widthMeasureSpec horizontal space requirements as imposed by the
     *            parent. The requirements are encoded with
     *            {@link android.view.View.MeasureSpec}.
     * @param heightMeasureSpec vertical space requirements as imposed by the
     *            parent. The requirements are encoded with
     *            {@link android.view.View.MeasureSpec}.
     * @deprecated [Module internal use]
     */
    /** @hide */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int widthMeasuredMode = MeasureSpec.EXACTLY;
        int componentWidth=w - mPaddingLeftAndRight * 2;
        if (componentWidth < 0) {
            LogUtil.logE(TAG,
                    "w - mPaddingLeftAndRight * 2 < 0 :",
                    " w = ", w,
                    ", mPaddingLeftAndRight = ", mPaddingLeftAndRight);
            componentWidth = 0;
        }
        int componentWidthMeasureSpec = MeasureSpec.makeMeasureSpec(componentWidth, widthMeasuredMode);
        int componentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.UNSPECIFIED);
        mLabelView.measure(componentWidthMeasureSpec, componentHeightMeasureSpec);
        if (mControl != null) {
            mControl.measure(componentWidthMeasureSpec, componentHeightMeasureSpec);
        }
        setMeasuredDimension(w, getComponentHeight());
        // Avoid the wrong height.
        super.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
    }

    /**
     * Called from layout when this view should assign a size and position to
     * each of its children. Derived classes with children should override this
     * method and call layout on each of their children.
     *
     * @param changed This is a new size or position for this view
     * @param left Left position, relative to parent
     * @param top Top position, relative to parent
     * @param right Right position, relative to parent
     * @param bottom Bottom position, relative to parent
     * @deprecated [Module internal use]
     */
    /** @hide */
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int labelTop = mLabelMarginTop;
        final int width = right - left;
        int labelBottom = labelTop + mLabelView.getMeasuredHeight();
        mLabelView.layout(mPaddingLeftAndRight, labelTop,
                width - mPaddingLeftAndRight, labelBottom);
        if (mControl != null && mControl.getVisibility() != View.GONE) {
            int editTop = labelBottom + mVerticalGap;
            int editBottom = editTop + mControl.getMeasuredHeight();
            mControl.layout(mPaddingLeftAndRight, editTop, width
                    - mPaddingLeftAndRight, editBottom);
        }
    }

    /**
     * Adds a child view. If no layout parameters are already set on the child,
     * the default parameters for this ViewGroup are set on the child.
     *
     * @param child the child view to add
     * @param index the position at which to add the child
     * @param params layout parameters
     * @deprecated [Module internal use]
     */
    /** @hide */
    public void addView(View child, int index, LayoutParams params) {
        if (getChildCount() >= 2) {
            throw new IllegalStateException(
                    "HtcListItemLabeledComponent can host only one direct child");
        }
        if (getChildCount() == 1) {
            mControl = child;
        }
        super.addView(child, index, params);
    }

    /**
     * Use this API to generate default layout params which width is match
     * parent and height is wrap content.
     *
     * @return The default layout params
     * @deprecated [Module internal use]
     */
    /** @hide */
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * Set the layout parameters associated with this view. This widget will set
     * fixed height to wrap content.
     *
     * @param params The layout parameters for this widget, cannot be null
     * @deprecated [Module internal use]
     */
    /** @hide */
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        params.height = LayoutParams.WRAP_CONTENT;
        super.setLayoutParams(params);
    }

    /**
     * Sets the padding. This widget cannot set padding to the left, right, top,
     * and bottom.
     *
     * @param left the left padding in pixels
     * @param top the top padding in pixels
     * @param right the right padding in pixels
     * @param bottom the bottom padding in pixels
     * @deprecated [Module internal use]
     */
    /** @hide */
    public void setPadding(int left, int top, int right, int bottom) {
    }

    /**
     * This is called when the view is attached to a window. At this point it
     * has a Surface and will start drawing. Note that this function is
     * guaranteed to be called before {@link #onDraw(android.graphics.Canvas)},
     * however it may be called any time before the first onDraw -- including
     * before or after {@link #onMeasure(int, int)}.
     *
     * @see #onDetachedFromWindow()
     * @deprecated [Module internal use]
     */
    /** @hide */
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent parent = getParent();
        mPaddingLeftAndRight = (super.getParent() != null && parent instanceof HtcListItem) ? 0
                : mMarginLeftAndRight;
    }

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = HtcListItem.MODE_DEFAULT, to = "MODE_DEFAULT"),
            @IntToString(from = HtcListItem.MODE_CUSTOMIZED, to = "MODE_CUSTOMIZED"),
            @IntToString(from = HtcListItem.MODE_KEEP_MEDIUM_HEIGHT, to = "MODE_KEEP_MEDIUM_HEIGHT"),
            @IntToString(from = HtcListItem.MODE_AUTOMOTIVE, to = "MODE_AUTOMOTIVE"),
            @IntToString(from = HtcListItem.MODE_POPUPMENU, to = "MODE_POPUPMENU")
    })
    int mItemMode = HtcListItem.MODE_DEFAULT;

    /**
     * @hide
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void notifyItemMode(int itemMode) {
        // TODO Auto-generated method stub
        if (mItemMode != itemMode) {
            mItemMode = itemMode;
            setDefaultTextStyle();
        }
    }
}
