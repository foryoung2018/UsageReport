package com.htc.lib1.cc.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;

/**
 * This component looks as the following:<br>
 *
 * <pre class="prettyprint">
 * ----------------------------------
 * |              ----------------- |
 * |              |  Line 1 text  | |
 * |  Front text  |               | |
 * |              |  Line 2 text  | |
 * |              ----------------- |
 * ----------------------------------
 * </pre>
 */
public class HtcListItemStockBoard extends FrameLayout implements IHtcListItemComponent {
    private ImageView mBoardPane;
    private TextView mBoardText1;
    private TextView mBoardText2;
    private TextView mFrontText;

    @ExportedProperty(category = "CommonControl")
    private int mDesiredBoardWidth = ViewGroup.LayoutParams.MATCH_PARENT;
    @ExportedProperty(category = "CommonControl")
    private int mDesiredBoardHeight = ViewGroup.LayoutParams.MATCH_PARENT;
    @ExportedProperty(category = "CommonControl")
    private boolean mUseCustomSize = false;

    private static String STOCK = "+100.00%";

    @ExportedProperty(category = "CommonControl")
    private int mBoardFontSize1, mBoardFontSize2, mFrontFontSize;

    @ExportedProperty(category = "CommonControl")
    private int mFrontTextRightMargin = 0;
    @ExportedProperty(category = "CommonControl")
    private int mRightMargin = 0;

    private void init(Context context) {
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        mBoardPane = new ImageView(context);
        mBoardPane.setScaleType(ScaleType.FIT_XY);

        mBoardText1 = new HtcFadingEdgeTextView(context);
        mBoardText1.setSingleLine(true);
        mBoardFontSize1 = context.getResources().getDimensionPixelSize(
                com.htc.lib1.cc.R.dimen.list_primary_xxs);
        setTextLineStyle(0, com.htc.lib1.cc.R.style.fixed_darklist_primary_xxs);

        mBoardText2 = new HtcFadingEdgeTextView(context);
        mBoardText2.setSingleLine(true);
        mBoardFontSize2 = context.getResources().getDimensionPixelSize(
                com.htc.lib1.cc.R.dimen.list_primary_xxs);
        setTextLineStyle(1, com.htc.lib1.cc.R.style.fixed_darklist_primary_xxs);

        mFrontText = new HtcFadingEdgeTextView(context);
        mFrontText.setSingleLine(true);
        mFrontFontSize = context.getResources().getDimensionPixelSize(R.dimen.list_primary_m);
        setFrontTextStyle(com.htc.lib1.cc.R.style.list_primary_m);

        mFrontTextRightMargin = HtcListItemManager.getM2(context);
        mRightMargin = HtcListItemManager.getDesiredChildrenGap(context);

        super.setPadding(0, 0, 0, 0);

        addView(mBoardPane, new LayoutParams(mDesiredBoardWidth, mDesiredBoardHeight));

        addView(mBoardText1, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mBoardText2, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mFrontText, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * Simple constructor to use when creating this widget from code. It will
     * new a HtcListItemStockBoard with default style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItemStockBoard(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor that is called when inflating this widget from XML. This is
     * called when a view is being constructed from an XML file, supplying
     * attributes that were specified in the XML file.It will new a
     * HtcListItemStockBoard with default style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemStockBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) params).setMargins(0, 0, mRightMargin, 0);
        }
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
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
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 0, mRightMargin, 0);
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

    // 1, measure all except board pane.
    // 2, get width of background (max of board 1 and 2),
    // then if use has set new size, use them, or use max.
    // 3, measure background use proper width & height.
    // 4, setMeasuredDimension use bg's height as height and bg's width + front
    // text's width as width.
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    protected void onMeasure(int w, int h) {
        // 1
        measureChild(mBoardText1, w, h);
        measureChild(mBoardText2, w, h);
        measureChild(mFrontText, w, h);
        // 2
        // this part isn't clear in UIGL, whatever.
        final Context context = getContext();
        final int width = (int) mBoardText1.getPaint().measureText(STOCK) + HtcListItemManager.getM5(context)
                * 2;
        final int height = mBoardText1.getMeasuredHeight() + mBoardText2.getMeasuredHeight()
                + HtcListItemManager.getM4(context);
        // 3
        if (mUseCustomSize) {
            measureChild(mBoardPane,
                    MeasureSpec.makeMeasureSpec(mDesiredBoardWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mDesiredBoardHeight, MeasureSpec.EXACTLY));
        } else {
            measureChild(mBoardPane, MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
        // 4
        // we don't care about w & h
        setMeasuredDimension(mBoardPane.getMeasuredWidth() + mFrontText.getMeasuredWidth()
                + mFrontTextRightMargin, mBoardPane.getMeasuredHeight());
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final Context context = getContext();
        final boolean isLayoutRtl = (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) ? getLayoutDirection() == View.LAYOUT_DIRECTION_RTL : false;
        final int height = b - t;
        final int width = r - l;
        int top = (height - (mBoardText1.getMeasuredHeight() + mBoardText2
                .getMeasuredHeight())) / 2;
        int left = 0;

        left = isLayoutRtl ? 0 : (width - mBoardPane.getMeasuredWidth());
        mBoardPane.layout(left, 0, left + mBoardPane.getMeasuredWidth(),
                0 + mBoardPane.getMeasuredHeight());

        int textRight = width - HtcListItemManager.getM5(context);
        left = isLayoutRtl ? HtcListItemManager.getM5(context) : (textRight - mBoardText1.getMeasuredWidth());
        mBoardText1.layout(left, top, left + mBoardText1.getMeasuredWidth(),
                top + mBoardText1.getMeasuredHeight());

        top += mBoardText1.getMeasuredHeight();
        left = isLayoutRtl ? HtcListItemManager.getM5(context) : (textRight - mBoardText2.getMeasuredWidth());
        mBoardText2.layout(left, top, left + mBoardText2.getMeasuredWidth(),
                top + mBoardText2.getMeasuredHeight());

        left = isLayoutRtl ? (width - mFrontText.getMeasuredWidth()) : 0;
        top = (height - mFrontText.getMeasuredHeight()) / 2;
        mFrontText.layout(left, top, left + mFrontText.getMeasuredWidth(),
                top + mFrontText.getMeasuredHeight());
    }

    /**
     * Set the background drawable of the 2 line text
     *
     * @param drawable the background drawable
     */
    public void setBoardImageDrawable(Drawable drawable) {
        mBoardPane.setImageDrawable(drawable);
    }

    /**
     * Set the background drawable of the 2 line text
     *
     * @param rId the resource ID
     */
    public void setBoardImageResource(int rId) {
        mBoardPane.setImageResource(rId);
    }

    /**
     * Set the bitmap for the image background of the 2 line text
     *
     * @param bm
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setBoardImageBitmap(Bitmap bm) {
        mBoardPane.setImageBitmap(bm);
    }

    /**
     * Set the background of the board pane
     *
     * @param drawable background drawable
     */
    public void setBoardBackgroundDrawable(Drawable drawable) {
        mBoardPane.setBackgroundDrawable(drawable);
    }

    /**
     * Set the background of the board pane
     *
     * @param rId resource id of board pane
     */
    public void setBoardBackgroundResource(int rId) {
        mBoardPane.setBackgroundResource(rId);
    }

    /**
     * Get the drawable for the image background of the 2 line text
     *
     * @return
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public Drawable getBoardDrawable() {
        return mBoardPane.getDrawable();
    }

    private void setText(TextView view, String text) {
        view.setText(text);

        if (text == null || text.equals("")) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    private boolean compareText(CharSequence text1, CharSequence text2) {
        if (text1 == null && text2 == null)
            return true;
        if (text1 != null && text1.equals(text2))
            return true;
        return false;
    }

    /**
     * set the text of each line of text
     *
     * @param index which text you would like to change the text [0-1]
     * @param text the text displayed
     */
    public void setTextLine(int index, String text) {
        if (index == 0) {
            if (compareText(mBoardText1.getText(), text))
                return;
            setText(mBoardText1, text);
        } else if (index == 1) {
            if (compareText(mBoardText2.getText(), text))
                return;
            setText(mBoardText2, text);
        }
    }

    /**
     * set the text of each line of text
     *
     * @param index which text you would like to change the text [0-1]
     * @param rId the resource ID of the text displayed
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setTextLineResource(int index, int rId) {
        String text = getContext().getResources().getString(rId);
        setTextLine(index, text);
    }

    /**
     * set the style of the text. Only for HTC defined style.
     *
     * @param index which text you would like to change the style [0-1]
     * @param defStyle the resource ID of the style (The font size won't be
     *            changed)
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setTextLineStyle(int index, int defStyle) {
        if (index == 0) {
            ((HtcFadingEdgeTextView) mBoardText1).setTextStyle(defStyle);
            mBoardText1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mBoardFontSize1);
        } else if (index == 1) {
            ((HtcFadingEdgeTextView) mBoardText2).setTextStyle(defStyle);
            mBoardText2.setTextSize(TypedValue.COMPLEX_UNIT_PX, mBoardFontSize2);
        }
    }

    /**
     * get the text of the 2 line text
     *
     * @param index
     * @return
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public String getTextLineContent(int index) {
        if (index == 0)
            return (String) mBoardText1.getText();
        else if (index == 1)
            return (String) mBoardText2.getText();

        return null;
    }

    /**
     * Set the text of the text area in front of the 2 Line text
     *
     * @param text the text displayed
     */
    public void setFrontText(String text) {
        if (compareText(mFrontText.getText(), text))
            return;
        setText(mFrontText, text);
    }

    /**
     * Set the text of the text area in front of the 2 Line text
     *
     * @param rId the resource ID of the text displayed
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setFrontText(int rId) {
        String text = getContext().getResources().getString(rId);
        setText(mFrontText, text);
    }

    /**
     * set the style of the text. Only for HTC defined style.
     *
     * @param defStyle the resource ID of the style (The font size won't be
     *            changed.)
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setFrontTextStyle(int defStyle) {
        ((HtcFadingEdgeTextView) mFrontText).setTextStyle(defStyle);
        mFrontText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFrontFontSize);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @ExportedProperty(category = "CommonControl")
    public String getFrontText() {
        return (String) mFrontText.getText();
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

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.setEnabled(enabled);
            HtcListItemManager.setViewOpacity(mBoardPane, enabled);
        }
    }

    /**
     * Use this API to set stock board width and height
     *
     * @param width width of stock board
     * @param height height of stock board
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setBoardSize(int width, int height) {
        mUseCustomSize = true;
        mDesiredBoardWidth = width;
        mDesiredBoardHeight = height;
        requestLayout();
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
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void notifyItemMode(int itemMode) {
        mItemMode = itemMode;
        if (mItemMode == HtcListItem.MODE_DEFAULT
                || mItemMode == HtcListItem.MODE_KEEP_MEDIUM_HEIGHT) {
            mFrontFontSize = getResources().getDimensionPixelOffset(R.dimen.list_primary_m);
            setFrontTextStyle(com.htc.lib1.cc.R.style.list_primary_m);
        }
    }
}
