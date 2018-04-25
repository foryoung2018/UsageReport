
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.widget.AutoCompleteTextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.graphic.StateDrawable.ColorState;
import com.htc.lib1.cc.util.ActionBarUtil;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.util.res.HtcResUtil;

/**
 * This class extend AutoCompleteTextView. The api of this class is almost the same as AutoCompleteTextView.
 * It add fade in alpha animation when user touch this widget. You should not setBackground of this widget on
 * your own.
 *
 * @author Mark.SL_Chen
 */
public class HtcAutoCompleteTextView extends AutoCompleteTextView {

    /**
     * The public constant for user to set the mode of this widget. This is used for user to put this widget
     * on bright background. For example: put this widget in a white scene. This is the default value.
     */
    public static final int MODE_BRIGHT_BACKGROUND = HtcInputFieldUtil.MODE_BRIGHT_BACKGROUND;

    /**
     * The public constant for user to set the mode of this widget. This is used for user to put this widget
     * on dark background. For example: put this widget in a black scene.
     */
    public static final int MODE_DARK_BACKGROUND = HtcInputFieldUtil.MODE_DARK_BACKGROUND;

    /**
     * The public constant for user to set the mode of this widget. This is used for full background to put
     * this widget on bright background. For example: compose input for message
     * @deprecated [only support light and dark, full mode not use any longer in sense70]
     */
    @Deprecated
    public static final int MODE_BRIGHT_FULL_BACKGROUND = HtcInputFieldUtil.MODE_BRIGHT_FULL_BACKGROUND;

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = MODE_BRIGHT_BACKGROUND, to = "MODE_BRIGHT_BACKGROUND"),
            @IntToString(from = MODE_DARK_BACKGROUND, to = "MODE_DARK_BACKGROUND")
    })
    private int mMode = MODE_BRIGHT_BACKGROUND;

    private ColorState mLight;

    private ColorState mDark;

    private Drawable mRealBackground;

    @ExportedProperty(category = "CommonControl")
    private int mUserPadding = -1;

    @ExportedProperty(category = "CommonControl")
    private int mUserPaddingLeft = -1;

    @ExportedProperty(category = "CommonControl")
    private int mUserPaddingTop = -1;

    @ExportedProperty(category = "CommonControl")
    private int mUserPaddingRight = -1;

    @ExportedProperty(category = "CommonControl")
    private int mUserPaddingBottom = -1;

    @ExportedProperty(category = "CommonControl")
    private int mDefaultPaddingLeft = -1;

    @ExportedProperty(category = "CommonControl")
    private int mDefaultPaddingTop = -1;

    @ExportedProperty(category = "CommonControl")
    private int mDefaultPaddingRight = -1;

    @ExportedProperty(category = "CommonControl")
    private int mDefaultPaddingBottom = -1;

    @ExportedProperty(category = "CommonControl")
    private int mMinHeight;

    // the minimum width of the dropdown for avoiding the dropdown is too small to show the suggested words
    @ExportedProperty(category = "CommonControl")
    private int mMinDropDownWidth;

    // In case there's APP owner who set the dropdown window offset by himself. No need to be static.
    @ExportedProperty(category = "CommonControl")
    private boolean mIsUserSetDropDown = false;

    @ExportedProperty(category = "CommonControl")
    private int mMultiplyColor;

    @ExportedProperty(category = "CommonControl")
    private int mMeasureSpecM2;

    /**
     * Simple constructor.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     * and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcAutoCompleteTextView(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     * and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attr The attributes of the XML tag that is inflating the view.
     */
    public HtcAutoCompleteTextView(Context context, AttributeSet attr) {
        this(context, attr, R.attr.htcAutoCompleteTextViewStyle);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     * and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attr The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view. If 0, no style will be applied (beyond what is
     *            included in the theme). This may either be an attribute resource, whose value will be
     *            retrieved from the current theme, or an explicit style resource.
     */
    public HtcAutoCompleteTextView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init(context, attr, defStyle);
        /*
         * Because AutoCompleteTextView can not change focus when IME is full Screen,
         * Meanwhile, there is super.setOnClickListener(mPassThroughClickListener) in AutoCompleteTextView constructor,
         * it lead to TextView.onKeyUp return false (!hasOnClickListener).
         */
        setOnEditorActionListener(null);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        Resources res = context.getResources();

        mMinHeight = context.getResources().getDrawable(R.drawable.common_inputfield_rest).getIntrinsicHeight();

        int colorEmpty = Color.RED;
        int colorLightPress = HtcCommonUtil.getCommonThemeColor(getContext(),  R.styleable.ThemeColor_light_category_color);
        int colorDarkPress = colorLightPress;
        int colorFocus = colorLightPress;
        mLight = new ColorState(colorLightPress,colorFocus, colorEmpty);
        mDark = new ColorState(colorDarkPress, colorFocus, colorEmpty);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HtcAutoCompleteTextView, defStyle,
                R.style.htcAutoCompleteTextViewStyleDefault);

        int userDropDownVerticalOffset = (int) a.getDimension(R.styleable.HtcAutoCompleteTextView_android_dropDownVerticalOffset, -1);
        int userDropDownHorizontalOffset = (int) a.getDimension(R.styleable.HtcAutoCompleteTextView_android_dropDownHorizontalOffset, -1);
        int userDropDownWidth = a.getLayoutDimension(R.styleable.HtcAutoCompleteTextView_android_dropDownWidth, -1);

        if (userDropDownVerticalOffset != -1 || userDropDownHorizontalOffset != -1 || userDropDownWidth != -1) {
            mIsUserSetDropDown = true;
        }

        if (userDropDownVerticalOffset == -1) {
            super.setDropDownVerticalOffset(0);
        }

        if (userDropDownHorizontalOffset == -1) {
            super.setDropDownHorizontalOffset(0);
        }

        Drawable popupBackground = a.getDrawable(R.styleable.HtcAutoCompleteTextView_android_popupBackground);
        if (popupBackground == null) {
            popupBackground = res.getDrawable(R.drawable.common_dropdown_background);
        }
        setDropDownBackgroundDrawable(popupBackground);

        mMeasureSpecM2 = HtcResUtil.getM2(context);
        mMinDropDownWidth = getMinDropDownWidth();
        if (null == getBackground()) {
            mMode = a.getInt(R.styleable.HtcAutoCompleteTextView_backgroundMode, MODE_BRIGHT_BACKGROUND);
            setMode(mMode);
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredHeight() < mMinHeight) {
            setMeasuredDimension(getMeasuredWidth(), mMinHeight);
        }
    }

    private Drawable getModeBackground(int mode) {
        Drawable pressedDraw = null;
        Drawable restDraw = null;
        ColorState cs;
        Resources res = getResources();
        switch (mode) {
            case MODE_DARK_BACKGROUND:
                cs = mDark;
                pressedDraw = HtcEditText.getRealBackground(cs,
                        res.getDrawable(R.drawable.common_b_inputfield_pressed));
                restDraw = HtcEditText.getRealBackground(cs,
                        res.getDrawable(R.drawable.common_b_inputfield_rest));
                break;
            case MODE_BRIGHT_FULL_BACKGROUND:
            case MODE_BRIGHT_BACKGROUND:
                cs = mLight;
                pressedDraw = HtcEditText.getRealBackground(cs,
                        res.getDrawable(R.drawable.common_inputfield_pressed));
                restDraw = HtcEditText.getRealBackground(cs,
                        res.getDrawable(R.drawable.common_inputfield_rest));
                break;
            default:
                break;
        }
        return getStateListDrawable(pressedDraw, restDraw);
    }

    private static Drawable getStateListDrawable(Drawable pressedDraw, Drawable resetDraw) {
        StateListDrawable sd = new StateListDrawable();
        sd.addState(new int[] {
                android.R.attr.state_pressed, android.R.attr.state_enabled
        }, pressedDraw);
        sd.addState(new int[] {
                android.R.attr.state_focused, android.R.attr.state_enabled
        }, pressedDraw);
        sd.addState(new int[] {}, resetDraw);
        return sd;
    }
    /**
     * You can use this function to decide the background of this widget. There are two choices: 1. If this
     * widget is put on a bright style scene pass in the parameter
     * HtcAutoCompleteTextView.MODE_BRIGHT_BACKGROUND. This is the default setting. 2. If this widget is put
     * on a dark style scene pass in the parameter HtcAutoCompleteTextView.MODE_DARK_BACKGROUND
     *
     * @param mode The parameter to decide the background of this widget. You can pass in either
     *            HtcAutoCompleteTextView.MODE_BRIGHT_BACKGROUND or
     *            HtcAutoCompleteTextView.MODE_DARK_BACKGROUND.
     */
    public void setMode(int mode) {
        if (getBackground() != null && mMode == mode) {
            return;
        }
        if (mode != MODE_BRIGHT_BACKGROUND && mode != MODE_DARK_BACKGROUND) {
            mMode = MODE_BRIGHT_BACKGROUND;
        } else {
            mMode = mode;
        }

        mRealBackground = getModeBackground(mMode);
        setBackground(mRealBackground);

        // >> Sense 60: default font style according to mode
        setTextAppearance(getContext(), HtcInputFieldUtil.getDefaultFontStyleByMode(mMode));

    }

    /**
     * @deprecated [Module internal use]
     */
    /** @hide */
    protected void setDrawableAlpha(int alpha) {
        Drawable background = getBackground();
        if (background != null) {
            background.setAlpha(alpha);
        }
        invalidate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            if (mMode == MODE_DARK_BACKGROUND) {
                setAlpha(HtcInputFieldUtil.DISABLED_ALPHA_DARK);
            } else {
                setAlpha(HtcInputFieldUtil.DISABLED_ALPHA_LIGHT);
            }
        } else {
            setAlpha(HtcInputFieldUtil.REST_ALPHA);
        }
        super.setEnabled(enabled);
    }

    @ExportedProperty(category = "CommonControl")
    private boolean mIsDropDownMinWidthEnabled = true;

    /**
     * This API needs to be called before measure() is called and dropdown's width or vertical offset or
     * horizontal offset is not modified.
     *
     * @param enabled whether to enlarge width of the drop down window to at least the minimum width which
     *            defined by designers. If false, it will be always the same as the input fields's width.
     */
    public void enableDropDownMinWidth(boolean enabled) {
        if (mIsUserSetDropDown) {
            return;
        }
        mIsDropDownMinWidthEnabled = enabled;
    }

    /**
     * @deprecated [Module internal use]
     */
    /** @hide */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // +1, -2 because there are 1px transparent at left and right in common_inputfield_press
        // Not +1, Not -2 because there are 0px transparent at left and right in common_b_inputfield_press
        // and common_inputfield_full_pressed.9.png
        if (!mIsUserSetDropDown) {
            // >> work around for PopupWindow's issue
            if (isPopupShowing())
                showDropDown();
        }
    }

    @Override
    public void showDropDown() {
        if (!mIsUserSetDropDown) {
            if (mIsDropDownMinWidthEnabled && getWidth() < mMinDropDownWidth) {
                super.setDropDownWidth(mMinDropDownWidth);
            } else {
                super.setDropDownWidth(getWidth());
            }
            super.setDropDownHorizontalOffset(0);
            super.setDropDownVerticalOffset(0);
        }
        super.showDropDown();
    }

    /**
     * @deprecated [Module internal use]
     */
    /** @hide */
    public void setDropDownHorizontalOffset(int offset) {
        super.setDropDownHorizontalOffset(offset);
        mIsUserSetDropDown = true;
    }

    /**
     * @deprecated [Module internal use]
     */
    public void setDropDownVerticalOffset(int offset) {
        super.setDropDownVerticalOffset(offset);
        mIsUserSetDropDown = true;
    }

    /**
     * @deprecated [Module internal use]
     */
    public void setDropDownWidth(int width) {
        super.setDropDownWidth(width);
        mIsUserSetDropDown = true;
    }

    private int mSupportMode = MODE_EXTERNAL;

    /**
     * external mode.
     */
    static final int MODE_EXTERNAL = 1;

    /**
     * automotive mode.
     */
    static final int MODE_AUTOMOTIVE = 2;

    void setSupportMode(int mode) {
        if (mSupportMode == mode) {
            return;
        }

        mSupportMode = mode;
        mMinDropDownWidth = getMinDropDownWidth();
    }

    private int getMinDropDownWidth() {
        return Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels)
                - ActionBarUtil.getActionBarBackupViewWidth(getContext(), mSupportMode == MODE_AUTOMOTIVE) - mMeasureSpecM2;
    }

    /**
     * @hide
     * @deprecated
     */
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (mSelectionChangedListener != null) {
            mSelectionChangedListener.onSelectionChanged(selStart, selEnd);
        }
    }

    /**
     * @hide
     * @deprecated
     */
    @Override
    public boolean onTextContextMenuItem(int id) {
        if (mSelectionChangedListener != null) {
            mSelectionChangedListener.onTextContextMenuItem(id);
        }
        return super.onTextContextMenuItem(id);
    }

    /**
     * Interface definition for a callback to be invoked when the text selection of HtcAutoCompleteTextView is changed.
     */
    public interface OnSelectionChangedListener {
        /**
         * This method is called when the selection has changed, in case any
         * subclasses would like to know.
         *
         * @param selStart The new selection start location.
         * @param selEnd   The new selection end location.
         */
        void onSelectionChanged(int selStart, int selEnd);

        /**
         * Called when a context menu option for the text view is selected.  Currently
         * this will be one of {@link android.R.id#selectAll}, {@link android.R.id#cut},
         * {@link android.R.id#copy} or {@link android.R.id#paste}.
         *
         * @return true if the context menu item action was performed.
         */
        void onTextContextMenuItem(int id);
    }

    private OnSelectionChangedListener mSelectionChangedListener = null;

    /**
     * Set OnSelectionChangedListener for HtcAutoCompleteTextView
     *
     * @param selectionChangedListener
     * @see com.htc.lib1.cc.widget.HtcAutoCompleteTextView.OnSelectionChangedListener
     */
    public void setOnSelectionChangedListener(OnSelectionChangedListener selectionChangedListener) {
        mSelectionChangedListener = selectionChangedListener;
    }

}
