
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.widget.RefreshGestureDetector;
import com.htc.lib1.cc.widget.RefreshGestureDetector.RefreshListener;

/**
 * A layout that always shows a text. The added child view will be placed on the
 * bottom. It can only host one direct child view. No Background by default. If
 * it register the refresh gesture detector and no added child view, it will add
 * the pull down to refresh text on the bottom.
 */
public class HtcEmptyView extends ViewGroup {
    private String mPullDownString;

    private String mEmptyString;

    private TextView mText;

    private View mContent;

    private RefreshGestureDetector mRefreshDetector;

    private boolean mAllCapsConfirmed = false;

    private boolean mAllCaps = true;

    private boolean mEnableAllCaps = true;

    private int mPadding;

    private int mGap;

    private int mControlMargin;

    /**
     * Normal mode
     */
    public static final int MODE_NORMAL = 0;

    /**
     * Dark mode
     */
    public static final int MODE_DARK = 1;

    /**
     * Automotive mode
     */
    public static final int MODE_AUTOMOTIVE = 2;

    private int mMode = -1;

    private int mEmptyStringStyle;

    private int mPullStringStyle;

    private boolean mModeChanged = false;

    /**
     * Simple constructor to use when creating this widget from code. It will
     * new a view with default text style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcEmptyView(Context context) {
        this(context, null);
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
     */
    public HtcEmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
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
     */
    public HtcEmptyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.HtcEmptyView, defStyle, 20);
        CharSequence emptyString = a.getText(R.styleable.HtcEmptyView_android_text);
        a.recycle();

        Resources res = context.getResources();

        mPullDownString = res.getString(R.string.st_pull_down_refresh);

        mGap = res.getDimensionPixelOffset(R.dimen.margin_m);

        mControlMargin = res.getDimensionPixelOffset(R.dimen.margin_l);

        DisplayMetrics dm = res.getDisplayMetrics();
        int total_width = (dm.widthPixels < dm.heightPixels) ? dm.widthPixels : dm.heightPixels;
        mPadding = (int) (total_width * 0.166);

        mText = new TextView(context);

        addView(mText, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setMode(MODE_NORMAL);

        mText.setGravity(Gravity.CENTER_HORIZONTAL);
        mAllCapsConfirmed = false;
        checkAllCaps();
        if (mAllCaps)
            mPullDownString = mPullDownString.toUpperCase();
        setText(emptyString);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mRefreshDetector != null)
            return mRefreshDetector.onTouchEvent(ev);
        else
            return super.onTouchEvent(ev);
    }

    /**
     * Register the RefreshListener
     * @param listener the RefreshListener
     */
    public void setRefreshListener(RefreshListener listener) {
        if (mRefreshDetector != null) {
            if (listener == null) {
                mRefreshDetector = null;
                String emptyString = mEmptyString;
                mEmptyString = "";
                setText(emptyString);
                return;
            } else {
                mRefreshDetector.setRefreshListener(listener);
            }
        }
        mRefreshDetector = new RefreshGestureDetector(getContext(), listener);
        appendPullText();
    }

    /**
     * @hide
     */
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int width = MeasureSpec.getSize(widthSpec);
        setMeasuredDimension(widthSpec, heightSpec);

        if (mText != null) {
            measureChild(mText, MeasureSpec.makeMeasureSpec(width - mPadding * 2, MeasureSpec.EXACTLY), heightSpec);
        }
        if (mContent != null && mContent.getVisibility() != View.GONE) {
            measureChild(mContent, MeasureSpec.makeMeasureSpec(width - mControlMargin * 2, MeasureSpec.AT_MOST), heightSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int top = 0;
        int bottom = 0;
        int left = 0;
        //int right = 0;
        int height = mText.getMeasuredHeight();
        if (mContent != null && mContent.getVisibility() != View.GONE) {
            height = height + mGap + mContent.getMeasuredHeight();
            top = (this.getMeasuredHeight() - height) / 2;
            left = (this.getMeasuredWidth() - mText.getMeasuredWidth()) / 2;
            bottom = top + mText.getMeasuredHeight();
            mText.layout(left, top, left + mText.getMeasuredWidth(), bottom);
            top = bottom + mGap;
            left = (this.getMeasuredWidth() - mContent.getMeasuredWidth()) / 2;
            mContent.layout(left, top, left + mContent.getMeasuredWidth(), top + mContent.getMeasuredHeight());
        } else {
            top = (this.getMeasuredHeight() - height)/ 2;
            left = (this.getMeasuredWidth() - mText.getMeasuredWidth()) / 2;
            bottom = top + mText.getMeasuredHeight();
            mText.layout(left, top, left + mText.getMeasuredWidth(), bottom);
        }
    }

    /**
     * Adds a child view. If no layout parameters are already set on the child,
     * the default parameters for this ViewGroup are set on the child.
     *
     * @param child the child view to add
     * @param index the position at which to add the child
     * @param params layout parameters
     */
    public void addView(View child, int index, LayoutParams params) {
        if (getChildCount() >= 2) {
            throw new IllegalStateException("HtcEmptyView can host only one direct child");
        }
        if (getChildCount() == 1) {
            mContent = child;
        }
        super.addView(child, index, params);
    }

    /**
     * Use this API to generate default layout params which width is match
     * parent and height is wrap content.
     *
     * @return The default layout params
     */
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    /**
     * Set the layout parameters associated with this view. This widget will set
     * fixed height to wrap content.
     *
     * @param params The layout parameters for this widget, cannot be null
     */
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        params.height = LayoutParams.MATCH_PARENT;
        params.width = LayoutParams.MATCH_PARENT;
        super.setLayoutParams(params);
    }

    // For prevent keep setting the same text causes lots of relayout
    private boolean compareText(CharSequence text1, CharSequence text2) {
        if (text1 == null && text2 == null)
            return true;
        if (text1 != null && text1.equals(text2))
            return true;
        return false;
    }

    private void checkAllCaps() {
        if (mEnableAllCaps == false) {
            mAllCaps = false;
            return;
        }
        if (!mAllCapsConfirmed) {
            mAllCaps = com.htc.lib1.cc.util.res.HtcResUtil.isInAllCapsLocale(getContext());
            mAllCapsConfirmed = true;
        }
    }

    /**
     * Set the text
     *
     * @param text The text
     */
    public void setText(CharSequence text) {
        if (text == null)
            setText("");
        else
            setText(text.toString());
    }

    /**
     * Set the text
     *
     * @param resId the resource ID of the text
     */
    public void setText(int resId) {
        String text = getContext().getResources().getString(resId);
        setText(text);
    }

    /**
     * Set the text
     *
     * @param text The text
     */
    public void setText(String text) {
        if (text == null)
            text = "";
        if (!mModeChanged && compareText(mEmptyString, text))
            return;

        mModeChanged = false;
        mEmptyString = text;
        checkAllCaps();

        if (mRefreshDetector != null) {
            appendPullText();
        } else {
            String emptyViewText = mAllCaps ? text.toUpperCase() : text;
            Spannable spannable = new SpannableString(emptyViewText);
            TextAppearanceSpan tas1 = new TextAppearanceSpan(getContext(), mEmptyStringStyle);
            spannable.setSpan(tas1, 0, mEmptyString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mText.setText(spannable);
        }
    }

    private void appendPullText() {
        if (mRefreshDetector != null) {
            String emptyViewText = mAllCaps ? mEmptyString.toUpperCase() : mEmptyString;
            emptyViewText = emptyViewText + "\n" + mPullDownString;
            Spannable spannable = new SpannableString(emptyViewText);

            TextAppearanceSpan tas1 = new TextAppearanceSpan(getContext(), mEmptyStringStyle);

            spannable.setSpan(tas1, 0, mEmptyString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            TextAppearanceSpan tas = new TextAppearanceSpan(getContext(), mPullStringStyle);
            spannable.setSpan(tas, mEmptyString.length() + 1,
                    mEmptyString.length() + mPullDownString.length() + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mText.setText(spannable);
        }
    }

    /**
     * disable the all caps for text
     */
    public void disableAllCaps() {
        if (mEnableAllCaps == true) {
            mEnableAllCaps = false;
            String emptyString = mEmptyString;
            mEmptyString = "";
            setText(emptyString);
        }
    }

    /**
     * Enable the automotive mode or not
     * If you want to enable the Automotive mode, you must set
     * the text again after calling this method.
     *
     * @param isEnable true, enable automotive mode.
     */
    public void setAutomotiveMode(boolean isEnable) {
    }

    /**
     * Set mode (MODE_NORMAL, MODE_DARK or MODE_AUTOMOTIVE) to HtcEmptyView.
     * Must set the text again after calling this method.
     *
     * @param mode the mode of HtcEmptyView.
     */
    public void setMode(int mode) {
        if(mode == mMode) {
            return;
        }
        if (mode == MODE_NORMAL) {
            mEmptyStringStyle = R.style.list_body_secondary_l;
            mPullStringStyle = R.style.fixed_list_secondary;
        } else if (mode == MODE_DARK) {
            mEmptyStringStyle = R.style.list_body_l;
            mPullStringStyle = R.style.fixed_list_body_m;
        } else if (mode == MODE_AUTOMOTIVE) {
            mEmptyStringStyle = R.style.fixed_automotive_darklist_secondary_l;
            mPullStringStyle = R.style.fixed_list_body_l;
        } else {
            throw new IllegalArgumentException("Invalid mode! Only MODE_NORMAL, MODE_DARK or MODE_AUTOMOTIVE is allowed.");
        }
        mMode = mode;
        mModeChanged = true;
    }

    /**
     * Sets the padding. This widget cannot set padding to the left, right, top,
     * and bottom.
     *
     * @param left the left padding in pixels
     * @param top the top padding in pixels
     * @param right the right padding in pixels
     * @param bottom the bottom padding in pixels
     */
    public void setPadding(int left, int top, int right, int bottom) {
    }
}
