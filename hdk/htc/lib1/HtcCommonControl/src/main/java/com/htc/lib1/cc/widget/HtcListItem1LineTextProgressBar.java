package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.LogUtil;

public class HtcListItem1LineTextProgressBar extends ViewGroup implements IHtcListItemTextComponent {

    private final static String TAG = "HtcListItem1LineTextProgressBar";

    @ExportedProperty(category = "CommonControl")
    private int mLeftMargin, mRightMargin;
    private TextView mText;
    private TextView mStamp;
    private HtcProgressBar mProgress;

    @ExportedProperty(category = "CommonControl")
    private int mTopGap;
    @ExportedProperty(category = "CommonControl")
    private int mCenterGap = 0;
    @ExportedProperty(category = "CommonControl")
    private int mBottomGap;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsMarqueeEnabled = false;

    private void init(Context context) {
        mProgress = new HtcProgressBar(context);

        mText = new HtcFadingEdgeTextView(context);

        enableMarquee(mIsMarqueeEnabled);

        setDefaultTextStyle();

        mTopGap = HtcListItemManager.getM5(context) * 2;
        mBottomGap = HtcListItemManager.getM1(context);
        mLeftMargin = HtcListItemManager.getDesiredChildrenGap(context);
        mRightMargin = HtcListItemManager.getDesiredChildrenGap(context);

        super.setPadding(0, 0, 0, 0);

        addView(mProgress, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mText, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void setDefaultTextStyle() {
        ((HtcFadingEdgeTextView) mText).setTextStyle(R.style.list_primary_m);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    com.htc.lib1.cc.R.styleable.HtcListItemTextComponentMode);
            mIsMarqueeEnabled = a.getBoolean(R.styleable.HtcListItemTextComponentMode_isMarquee,
                    false);
            a.recycle();
        } else {
            mIsMarqueeEnabled = false;
        }
        init(context);
    }

    /**
     * Simple constructor to use when creating this widget from code. It will
     * new a textView with default text style and a progress bar.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItem1LineTextProgressBar(Context context) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        init(context);
    }

    /**
     * Constructor that is called when inflating this widget from XML. This is
     * called when a view is being constructed from an XML file, supplying
     * attributes that were specified in the XML file.It will new a textView
     * with default text style and a progress bar.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItem1LineTextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        init(context, attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of this widget allows subclasses to use their own base style
     * when they are inflating. It will new textView with default text style and
     * a progress bar.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @param defStyle The default style to apply to this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItem1LineTextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        init(context, attrs);
    }

    /**
     * get the progress bar of this widget
     *
     * @return progress bar
     */
    public ProgressBar getProgressBar() {
        return mProgress;
    }

    /**
     * @hide
     */
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) params).setMargins(mLeftMargin, 0, mRightMargin, 0);
        }

        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;

        super.setLayoutParams(params);
    }

    /**
     * For layout from XML file, this function will be called to get the layout
     * param of children.
     *
     * @see android.view.View#getLayoutParams()
     * @deprecated [Module internal use]
     */
    /** @hide */
    public ViewGroup.LayoutParams getLayoutParams() {
        if (super.getLayoutParams() != null)
            return super.getLayoutParams();
        else {
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(mLeftMargin, 0, mRightMargin, 0);
            super.setLayoutParams(params);
            return params;
        }
    }

    private boolean compareText(CharSequence text1, CharSequence text2) {
        if (text1 == null && text2 == null)
            return true;
        if (text1 != null && text1.equals(text2))
            return true;
        return false;
    }

    private void setText(TextView view, String text) {
        if (compareText(view.getText(), text))
            return;
        view.setText(text);

        if (text == null) {
            view.setVisibility(View.GONE);
        } else if (text.equals("")) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void setText(TextView view, CharSequence text) {
        if (compareText(view.getText(), text))
            return;
        view.setText(text);

        if (text == null) {
            view.setVisibility(View.GONE);
        } else if (text.equals("")) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void initStamp() {
        mStamp = new HtcFadingEdgeTextView(getContext());
        ((HtcFadingEdgeTextView) mStamp).setTextStyle(R.style.fixed_list_secondary);
        ((HtcFadingEdgeTextView) mStamp).setEnableMarquee(mIsMarqueeEnabled);
        addView(mStamp, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * set the text of the stamp
     *
     * @param text the text displayed
     */
    public void setStampText(String text) {
        if (mStamp == null) {
            initStamp();
        }
        setText(mStamp, text);
    }

    /**
     * set the text of the stamp
     *
     * @param rId the resource ID of the text displayed
     */
    public void setStampText(int rId) {
        if (mStamp == null) {
            initStamp();
        }
        String text = getContext().getResources().getString(rId);
        setText(mStamp, text);
    }

    /**
     * set the text of the stamp
     *
     * @param text the text displayed
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setStampText(CharSequence text) {
        if (mStamp == null) {
            initStamp();
        }
        setText(mStamp, text);
    }

    /**
     * get the content of stamp
     *
     * @return the content of stamp
     * @deprecated [Not use any longer]
     */
    /** @hide */
    @ExportedProperty(category = "CommonControl")
    public String getStampText() {
        if (mStamp != null) {
            return mStamp.getText().toString();
        } else {
            return "";
        }
    }

    /**
     * get the content of stamp
     *
     * @return the content of stamp
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public CharSequence getStampCharSequence() {
        if (mStamp != null) {
            return mStamp.getText();
        } else {
            return "";
        }
    }

    /**
     * set the text of the first line of text
     *
     * @param text the text displayed
     */
    public void setPrimaryText(String text) {
        setText(mText, text);
    }

    /**
     * set the text of the first line of text
     *
     * @param rId the resource ID of the text displayed
     */
    public void setPrimaryText(int rId) {
        String text = getContext().getResources().getString(rId);
        setText(mText, text);
    }

    /**
     * set the text of the first line of text
     *
     * @param text the text displayed
     */
    public void setPrimaryText(CharSequence text) {
        setText(mText, text);
    }

    /**
     * get the content of primary text
     *
     * @return the content of primary text
     */
    @ExportedProperty(category = "CommonControl")
    public String getPrimaryText() {
        return mText.getText().toString();
    }

    /**
     * get the content of primary text
     *
     * @return CharSequence of primary text
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public CharSequence getPrimaryCharSequence() {
        return mText.getText();
    }

    /**
     * @deprecated [Not use any longer] This API will no longer be supported in
     *             Sense 5.0
     */
    /** @hide */
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled)
            return;
        super.setEnabled(enabled);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != null)
                child.setEnabled(enabled);
        }
    }

    /**
     * set the visibility of Stamp text
     *
     * @param visibility the visibility of the stamp
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setStampTextVisibility(int visibility) {
        mStamp.setVisibility(visibility);
    }

    /**
     * please note: if true, marquee will NOT be used, instead, using fade out.
     * otherwise, use Truncate.END.
     *
     * @param enable is marquee enabled
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void enableMarquee(boolean enable) {
        mIsMarqueeEnabled = enable;
        ((HtcFadingEdgeTextView) mText).setEnableMarquee(enable);
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mText == null)
            return;
        final boolean isLayoutRtl = (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) ? getLayoutDirection() == View.LAYOUT_DIRECTION_RTL : false;
        final int height = b - t;
        final int width = r - l;
        int left;
        int top = (height - mText.getMeasuredHeight() - mProgress.getMeasuredHeight()) / 3;
        if (mProgress != null && mProgress.getVisibility() != View.GONE) {
            if (mText.getVisibility() != View.GONE) {
                left = isLayoutRtl ? (width - mText.getMeasuredWidth()) : 0;
                mText.layout(left, top, left + mText.getMeasuredWidth(),
                        top + mText.getMeasuredHeight());
            }

            if (mStamp != null && mStamp.getVisibility() != View.GONE) {
                left = isLayoutRtl ? 0 : (width - mStamp.getMeasuredWidth());
                mStamp.layout(left, top + (mText.getMeasuredHeight() - mStamp.getMeasuredHeight())
                        / 2, left + mStamp.getMeasuredWidth(), top
                        + (mText.getMeasuredHeight() + mStamp.getMeasuredHeight()) / 2);
            }

            mProgress.layout(0, height - mBottomGap - mProgress.getMeasuredHeight(),
                    width, height - mBottomGap);
        } else {
            if (mText.getVisibility() != View.GONE) {
                layoutTextAtCenter(true, l, t, r, b);
            }
            if (mStamp != null && mStamp.getVisibility() != View.GONE) {
                layoutStampAtCenter(l, t, r, b);
            }
        }

    }

    /**
     * @hide
     */
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        final Context context = getContext();
        int totalHeight = 0;
        int maxWidth = 0;
        int gap = HtcListItemManager.getM2(context);
        int width = MeasureSpec.getSize(widthSpec);

        if (mText.getVisibility() != GONE) {
            if (mStamp != null && mStamp.getVisibility() != GONE) {
                int desiredStampWidth = width / 3 - (gap / 2);
                if (desiredStampWidth < 0) {
                    LogUtil.logE(TAG,
                            "width / 3 - (gap / 2) <0 :",
                            " width = ", width,
                            ", gap = ", gap);
                    desiredStampWidth = 0;
                }

                int desiredTextWidth = 2 * width / 3 - (gap / 2);
                if (desiredTextWidth < 0) {
                    LogUtil.logE(TAG,
                            "2 * width / 3 - (gap / 2) <0 :",
                            " width = ", width,
                            ", gap = ", gap);
                    desiredTextWidth = 0;
                }

                measureChild(mStamp, widthSpec, heightSpec);
                int diff = mStamp.getMeasuredWidth() - desiredStampWidth;
                if (diff >= 0) {
                    measureChild(mStamp, MeasureSpec.makeMeasureSpec(desiredStampWidth,
                            MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                            mStamp.getMeasuredHeight(), MeasureSpec.EXACTLY));
                    measureChild(mText,
                            MeasureSpec.makeMeasureSpec(desiredTextWidth, MeasureSpec.EXACTLY),
                            heightSpec);
                } else {
                    measureChild(mText, MeasureSpec.makeMeasureSpec(desiredTextWidth - diff - gap
                            / 2, MeasureSpec.EXACTLY), heightSpec);
                }
            } else {
                measureChild(mText, widthSpec, heightSpec);
            }
            totalHeight += mText.getMeasuredHeight();
            maxWidth = Math.max(maxWidth, mText.getMeasuredWidth());
        }
        if (mProgress != null && mProgress.getVisibility() != GONE) {
            measureChild(mProgress, widthSpec, heightSpec);
            totalHeight += mProgress.getMeasuredHeight();
            maxWidth = Math.max(maxWidth, mProgress.getMeasuredWidth());
        }

        final int desireHeight = HtcListItemManager.getInstance(context).getDesiredListItemHeight(
                mItemMode);
        if (mText.getVisibility() != GONE
                && (mProgress != null && mProgress.getVisibility() != GONE)) {
            totalHeight += mTopGap + mBottomGap + mCenterGap;
            if (totalHeight < desireHeight)
                totalHeight = desireHeight;
        } else if (mText.getVisibility() != GONE
                || (mProgress != null && mProgress.getVisibility() != GONE)) {
            totalHeight += mTopGap + mBottomGap;
        }

        setMeasuredDimension(resolveSize(maxWidth, widthSpec), resolveSize(totalHeight, heightSpec));
    }

    private void layoutTextAtCenter(boolean isAlignLeft, int l, int t, int r, int b) {
        final int width = r - l;
        final int height = b - t;
        int top = (height - mText.getMeasuredHeight()) / 2;
        mText.layout((isAlignLeft ? 0 : width - mText.getMeasuredWidth()), top,
                (isAlignLeft ? mText.getMeasuredWidth() : width),
                top + mText.getMeasuredHeight());
    }

    private void layoutStampAtCenter(int l, int t, int r, int b) {
        final int width = r - l;
        final int height = b - t;
        int top = (height - mStamp.getMeasuredHeight()) / 2;
        mStamp.layout(width - mStamp.getMeasuredWidth(), top, width, top
                + mStamp.getMeasuredHeight());
    }

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = HtcListItem.MODE_DEFAULT, to = "MODE_DEFAULT"),
            @IntToString(from = HtcListItem.MODE_CUSTOMIZED, to = "MODE_CUSTOMIZED"),
            @IntToString(from = HtcListItem.MODE_KEEP_MEDIUM_HEIGHT, to = "MODE_KEEP_MEDIUM_HEIGHT"),
            @IntToString(from = HtcListItem.MODE_AUTOMOTIVE, to = "MODE_AUTOMOTIVE"),
            @IntToString(from = HtcListItem.MODE_POPUPMENU, to = "MODE_POPUPMENU")
    })
    int mItemMode = HtcListItem.MODE_DEFAULT;
}
