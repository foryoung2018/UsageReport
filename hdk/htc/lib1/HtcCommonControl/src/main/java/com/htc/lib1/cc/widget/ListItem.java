
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class ListItem extends ViewGroup {
    private final static boolean DBG = false;
    @ViewDebug.ExportedProperty(category = "CommonControl", prefix = "ListItem")
    private final static boolean SUPPORT_RTL=Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN;
    private final static String TAG = "ListItem";

    private final static int TYPE_DEFAULT = LayoutParams.SIZE_WRAP_CONTENT
            | LayoutParams.ALIGN_TOP_EDGE;

    public static final int MODE_DEFAULT = 0;
    public static final int MODE_AUTOMOTIVE = 3;
    public static final int MODE_POPUPMENU = 4;
    @ViewDebug.ExportedProperty(category = "CommonControl", prefix = "ListItem", mapping = {
            @ViewDebug.IntToString(from = MODE_DEFAULT, to = "MODE_DEFAULT"),
            @ViewDebug.IntToString(from = MODE_AUTOMOTIVE, to = "MODE_AUTOMOTIVE"),
            @ViewDebug.IntToString(from = MODE_POPUPMENU, to = "MODE_POPUPMENU")
    })
    private int mMode = MODE_DEFAULT;
    @ViewDebug.ExportedProperty(category = "CommonControl", prefix = "ListItem")
    private int mDesiredMinHeight;
    @ViewDebug.ExportedProperty(category = "CommonControl", prefix = "ListItem")
    private int mSize147;
    @ViewDebug.ExportedProperty(category = "CommonControl", prefix = "ListItem")
    private int mSizeDefaultHeight;
    @ViewDebug.ExportedProperty(category = "CommonControl", prefix = "ListItem")
    private int mDividerWidth;
    @ViewDebug.ExportedProperty(category = "CommonControl", prefix = "ListItem")
    private int mScreenWidth;

    private Drawable mVirticalDividerLight;
    private Drawable mVirticalDividerDark;

    private HtcListItemManager mHtcListItemManager;
    private MeasureLayoutManager mMeasureLayoutManager;

    /**
     * Simple constructor to use when creating this widget from code. It will
     * new a ListItem with default style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public ListItem(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating this widget from XML. This is
     * called when a view is being constructed from an XML file, supplying
     * attributes that were specified in the XML file.It will new a ListItem
     * with default style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @Deprecated [Module internal use]
     */
    /** @hide */
    public ListItem(Context context, AttributeSet attrs) {
        this(context, attrs, com.htc.lib1.cc.R.attr.htcListItemStyle);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of this widget allows subclasses to use their own base style
     * when they are inflating. It will a widget with style defStyle.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @param defStyle The default style to apply to this widget.
     * @Deprecated [Module internal use]
     */
    /** @hide */
    public ListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs,
                com.htc.lib1.cc.R.styleable.HtcListItem, defStyle,
                com.htc.lib1.cc.R.style.htcListItem);
        mMode = a.getInt(com.htc.lib1.cc.R.styleable.HtcListItem_itemMode, MODE_DEFAULT);
        mVirticalDividerLight = a
                .getDrawable(com.htc.lib1.cc.R.styleable.HtcListItem_android_childDivider);
        mVirticalDividerDark = a
                .getDrawable(com.htc.lib1.cc.R.styleable.HtcListItem_android_childIndicator);
        a.recycle();
        initValues();
    }

    private Drawable getVirticalDivider() {
        return (mMode == MODE_POPUPMENU || mMode == MODE_AUTOMOTIVE) ? mVirticalDividerDark
                : mVirticalDividerLight;
    }

    private void initValues() {
        mHtcListItemManager = HtcListItemManager.getInstance(getContext());
        mMeasureLayoutManager = new MeasureLayoutManager();
        mDesiredMinHeight = mHtcListItemManager.getDesiredListItemHeight(mMode);
        mSize147 = HtcListItemManager.getActionButtonWidth(getContext(), mMode);
        mSizeDefaultHeight = mHtcListItemManager.getDesiredListItemHeight(mMode);
        mDividerWidth = HtcListItemManager.getVerticalDividerWidth(getContext());
        mScreenWidth = getScreenWidth(getContext());
    }

    @Override
    protected void onMeasure(int w, int h) {
        if (DBG) {
            Log.d(TAG,
                    "width mode=" + MeasureSpec.getMode(w) +
                            " width size=" + MeasureSpec.getSize(w) +
                            " height mode=" + MeasureSpec.getMode(h) +
                            " height size=" + MeasureSpec.getSize(h));
        }
        final int widthMode = MeasureSpec.getMode(w);
        final int widthSize = (MeasureSpec.UNSPECIFIED == widthMode) ? mScreenWidth : MeasureSpec
                .getSize(w);
        mMeasureLayoutManager.initLineIndicatorValues(getPaddingStart(), getPaddingEnd());
        // first measure SIZE_147,SIZE_DEFAULT_HEIGHT,SIZE_WRAP_CONTENT
        measureConfirmedSizeChild(widthMode, widthSize, h);
        // then measure
        // SIZE_REST,SIZE_AUTO_1_3_REST,SIZE_AUTO_2_3_REST,SIZE_3_10_REST,SIZE_4_10_REST
        measureRestSizeChild(widthMode, widthSize, h);
        calculateMaxHeight();
        setMeasuredDimension(widthSize, mMeasureLayoutManager.mTotalHeight);
    }

    private static int getScreenWidth(Context context) {
        final WindowManager manage = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        final Display display = manage.getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    private void calculateMaxHeight() {
        mMeasureLayoutManager.initHeightValues();
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child == null || child.getVisibility() == View.GONE) {
                continue;
            }
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int height = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            mMeasureLayoutManager.calculateHeight(lp.type, height);
        }
        if (mMeasureLayoutManager.mHasPrimaryBaseline
                && mMeasureLayoutManager.mHasSecondaryBaseline) {
            mMeasureLayoutManager.mTotalHeight = Math.max(mMeasureLayoutManager.mFullLineHeight,
                    mDesiredMinHeight - mHtcListItemManager.getDesiredBottomGap(mMode)
                            + mMeasureLayoutManager.mFooterLineHeight);
        } else {
            mMeasureLayoutManager.mTotalHeight = Math.max(mMeasureLayoutManager.mFullLineHeight,
                    mMeasureLayoutManager.mFirstLineHeight
                            + mMeasureLayoutManager.mSecondaryLineHeight
                            + mMeasureLayoutManager.mFirstLineHeight);
        }
        mMeasureLayoutManager.mTotalHeight = Math.max(mMeasureLayoutManager.mTotalHeight,
                mDesiredMinHeight);
        mMeasureLayoutManager.mTotalHeight += getPaddingTop() + getPaddingBottom();
    }

    private void calculateRestSize() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child == null || child.getVisibility() == View.GONE) {
                continue;
            }
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            final int type = lp.type;
            if (checkType(type, LayoutParams.TYPE_SIZE_REST_SET | LayoutParams.TYPE_SIZE_AUTO_REST_SET)) {
                int widthOfDivider = 0;
                if (checkType(type, LayoutParams.DIVIDER_START)) {
                    widthOfDivider += mDividerWidth;
                }
                if (checkType(type, LayoutParams.DIVIDER_END)) {
                    widthOfDivider += mDividerWidth;
                }
                mMeasureLayoutManager.calculateLineIndicator(type,
                        lp.getMarginStart() + lp.getMarginEnd() + widthOfDivider);
            } else {
                continue;
            }
        }
    }

    private void measureRestSizeChild(int widthMode, int widthSize, int heightMeasureSpec) {
        calculateRestSize();
        final int count = getChildCount();
        // Measure SIZE_REST,SIZE_3_10_REST,SIZE_4_10_REST
        measureRestFixedSizeChild(count, widthSize, heightMeasureSpec);
        // Measure SIZE_AUTO_1_3_REST,SIZE_AUTO_2_3_REST
        measureRestAutoSizeChild(count, widthSize, heightMeasureSpec);
    }

    private void measureRestFixedSizeChild(int count, int widthSize, int heightMeasureSpec) {
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child == null || child.getVisibility() == View.GONE) {
                continue;
            }
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            final int type = lp.type;
            if (checkType(type, LayoutParams.TYPE_SIZE_REST_SET)) {
                int restSize = mMeasureLayoutManager.getRestSize(widthSize, type);
                if (checkType(type, LayoutParams.SIZE_3_10_REST)) {
                    restSize = (int) ((restSize * 3 / 10.0) + 0.5);
                } else if (checkType(type, LayoutParams.SIZE_4_10_REST)) {
                    restSize = (int) ((restSize * 4 / 10.0) + 0.5);
                }
                child.measure(MeasureSpec.makeMeasureSpec(restSize, MeasureSpec.EXACTLY), heightMeasureSpec);
            }
        }
    }

    private void measureRestAutoSizeChild(int count, int widthSize, int heightMeasureSpec) {
        final int fullLineRestSize = mMeasureLayoutManager.getRestSize(widthSize, LayoutParams.TYPE_LINE_SET_DEFAULT);
        final int restSize_1_3 = (int) ((fullLineRestSize / 3.0) + 0.5);
        final int restSize_2_3 = (int) ((fullLineRestSize * 2 / 3.0) + 0.5);
        int restSize_1_3_Actual = 0;
        int restSize_2_3_Actual = 0;
        int restSize_1_3_Final = 0;
        int restSize_2_3_Final = 0;

        // Measure SIZE_AUTO_1_3_REST,SIZE_AUTO_2_3_REST first UNSPECIFIED
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child == null || child.getVisibility() == View.GONE) {
                continue;
            }
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            final int type = lp.type;
            if (checkType(type, LayoutParams.TYPE_SIZE_AUTO_REST_SET)) {
                child.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), heightMeasureSpec);
                if (checkType(type, LayoutParams.SIZE_AUTO_1_3_REST)) {
                    restSize_1_3_Actual = child.getMeasuredWidth();
                } else {
                    restSize_2_3_Actual = child.getMeasuredWidth();
                }
            }
        }

        // calculate final rest auto size
        if (restSize_1_3_Actual < restSize_1_3 && restSize_2_3_Actual > restSize_2_3) {
            restSize_1_3_Final = restSize_1_3_Actual;
            restSize_2_3_Final = fullLineRestSize - restSize_1_3_Final;
        } else if (restSize_1_3_Actual > restSize_1_3 && restSize_2_3_Actual < restSize_2_3) {
            restSize_2_3_Final = restSize_2_3_Actual;
            restSize_1_3_Final = fullLineRestSize - restSize_2_3_Final;
        } else {
            restSize_1_3_Final = restSize_1_3;
            restSize_2_3_Final = restSize_2_3;
        }

        // Measure SIZE_AUTO_1_3_REST,SIZE_AUTO_2_3_REST Second EXACTLY
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child == null || child.getVisibility() == View.GONE) {
                continue;
            }
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            final int type = lp.type;
            if (checkType(type, LayoutParams.SIZE_AUTO_1_3_REST)) {
                child.measure(MeasureSpec.makeMeasureSpec(restSize_1_3_Final, MeasureSpec.EXACTLY), heightMeasureSpec);
            } else if (checkType(type, LayoutParams.SIZE_AUTO_2_3_REST)) {
                child.measure(MeasureSpec.makeMeasureSpec(restSize_2_3_Final, MeasureSpec.EXACTLY), heightMeasureSpec);
            }
        }
    }

    // measure SIZE_147,SIZE_DEFAULT_HEIGHT,SIZE_WRAP_CONTENT
    private void measureChildFixedAndWrapContent(int heightMeasureSpec, int widthMode,
            int widthSize, View child) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        final int type = lp.type;
        final int marginStart = lp.getMarginStart();
        final int marginEnd = lp.getMarginEnd();
        if (checkType(type, LayoutParams.TYPE_SIZE_FIX_WRAP_CONTENT_SET)) {
            // add dividerWidth
            int widthOfDivider = 0;
            if (checkType(type, LayoutParams.DIVIDER_START)) {
                widthOfDivider += mDividerWidth;
            }
            if (checkType(type, LayoutParams.DIVIDER_END)) {
                widthOfDivider += mDividerWidth;
            }
            if (checkType(type, LayoutParams.SIZE_147)) {
                child.measure(MeasureSpec.makeMeasureSpec(mSize147,
                        MeasureSpec.EXACTLY), heightMeasureSpec);
            } else if (checkType(type, LayoutParams.SIZE_DEFAULT_HEIGHT)) {
                child.measure(MeasureSpec.makeMeasureSpec(mSizeDefaultHeight,
                        MeasureSpec.EXACTLY), heightMeasureSpec);
            } else if (checkType(type, LayoutParams.SIZE_WRAP_CONTENT)) {
                child.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        heightMeasureSpec);
                final int restSize = mMeasureLayoutManager.getRestSize(widthSize - widthOfDivider
                        - marginStart - marginEnd, type);
                if (child.getMeasuredWidth() > restSize) {
                    child.measure(MeasureSpec.makeMeasureSpec(restSize, MeasureSpec.EXACTLY),
                            heightMeasureSpec);
                }
            }

            int mSize = child.getMeasuredWidth() + marginStart + marginEnd
                    + widthOfDivider;
            if (checkType(type, LayoutParams.TYPE_ALIGN_147_EDGE_SET)) {
                mSize += mSize147;
            } else if (checkType(type, LayoutParams.TYPE_ALIGN_DEFAULT_HEIGHT_EDGE_SET)) {
                mSize += mSizeDefaultHeight;
            }

            if (checkType(type, LayoutParams.TYPE_START_OR_END_EDGE_SET)) {
                mMeasureLayoutManager.calculateLineIndicatorWithStartOrEndEdge(type, mSize);
            } else {
                mMeasureLayoutManager.calculateLineIndicator(type, mSize);
            }
        } else {
            return;
        }
    }

    private void measureConfirmedSizeChild(int widthMode, int widthSize, int heightMeasureSpec) {
        final int count = getChildCount();
        // position start end
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child == null || child.getVisibility() == View.GONE) {
                continue;
            }
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            final int type = lp.type;
            if (checkType(type, LayoutParams.TYPE_START_OR_END_SET)) {
                measureChildFixedAndWrapContent(heightMeasureSpec, widthMode, widthSize, child);
            } else {
                continue;
            }
        }
        // position rest
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child == null || child.getVisibility() == View.GONE)
                continue;
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            final int type = lp.type;
            if (!checkType(type, LayoutParams.TYPE_START_OR_END_SET)) {
                measureChildFixedAndWrapContent(heightMeasureSpec, widthMode, widthSize, child);
            } else {
                continue;
            }
        }
    }

    private void savePosition(boolean isLayoutRtl, View child, int width) {
        saveTopBottomPosition(child);
        saveStartEndPostition(child);
        layoutChild(isLayoutRtl, child, width);
    }

    private void saveTopBottomPosition(View child) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        final int type = lp.type;
        final int childMeasuredHeight = child.getMeasuredHeight();
        lp.mTop = getPaddingTop();
        if (checkType(type, LayoutParams.PRIMARY)) {
            lp.mTop += mHtcListItemManager.getPrimaryBaseLine(mMode) - (child.getBaseline() != -1 ? child.getBaseline() : child.getMeasuredHeight());
        } else if (checkType(type, LayoutParams.SECONDARY)) {
            lp.mTop += mHtcListItemManager.getSecondaryBaseLine(mMode) - (child.getBaseline() != -1 ? child.getBaseline() : child.getMeasuredHeight());
        } else if (checkType(type, LayoutParams.CENTER_VERTICAL)) {
            if (checkType(type, LayoutParams.FIRST_LINE)) {
                lp.mTop += (mMeasureLayoutManager.mFirstLineHeight - childMeasuredHeight) / 2;
            } else if (checkType(type, LayoutParams.SECONDARY_LINE)) {
                lp.mTop += mMeasureLayoutManager.mFirstLineHeight
                        + (mMeasureLayoutManager.mSecondaryLineHeight - childMeasuredHeight) / 2;
            } else if (checkType(type, LayoutParams.FOOTER_LINE)) {
                if (mMeasureLayoutManager.mHasPrimaryBaseline
                        && mMeasureLayoutManager.mHasSecondaryBaseline) {
                    lp.mTop += (mHtcListItemManager.getDesiredListItemHeight(mMode) - mHtcListItemManager
                            .getDesiredBottomGap(mMode))
                            + (mMeasureLayoutManager.mFooterLineHeight - childMeasuredHeight) / 2;
                } else {
                    lp.mTop += mMeasureLayoutManager.mFirstLineHeight
                            + mMeasureLayoutManager.mSecondaryLineHeight
                            + (mMeasureLayoutManager.mFooterLineHeight - childMeasuredHeight) / 2;
                }
            } else {
                lp.mTop = (mMeasureLayoutManager.mTotalHeight - childMeasuredHeight) / 2;
            }
        } else if (checkType(type, LayoutParams.DEFAULT_HEIGHT_CENTER_VERTICAL)) {
            lp.mTop += mDesiredMinHeight > childMeasuredHeight ? (mDesiredMinHeight - childMeasuredHeight) / 2 : 0;
        } else {// LayoutParams.ALIGN_TOP_EDGE
            if (checkType(type, LayoutParams.FIRST_LINE)) {
                lp.mTop += lp.topMargin;
            } else if (checkType(type, LayoutParams.SECONDARY_LINE)) {
                lp.mTop += mMeasureLayoutManager.mFirstLineHeight + lp.topMargin;
            } else if (checkType(type, LayoutParams.FOOTER_LINE)) {
                if (mMeasureLayoutManager.mHasPrimaryBaseline
                        && mMeasureLayoutManager.mHasSecondaryBaseline) {
                    lp.mTop += (mDesiredMinHeight - mHtcListItemManager.getDesiredBottomGap(mMode))
                            + lp.topMargin;
                } else {
                    lp.mTop += mMeasureLayoutManager.mFirstLineHeight
                            + mMeasureLayoutManager.mSecondaryLineHeight + lp.topMargin;
                }
            } else {
                lp.mTop += lp.topMargin;
            }
        }
        lp.mBottom = lp.mTop + childMeasuredHeight;
    }

    private void saveStartEndPostition(View child) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        final int type = lp.type;
        final int marginStart = lp.getMarginStart();
        final int marginEnd = lp.getMarginEnd();
        final int measuredWidth = getMeasuredWidth();
        final int childMeasuredWidth = child.getMeasuredWidth();
        final int positionToEdge = mMeasureLayoutManager.getPositionToEdge(type);
        int widthOfDivider = 0;
        if (checkType(type, LayoutParams.END)) {
            if (checkType(type, LayoutParams.DIVIDER_END)) {
                mMeasureLayoutManager.addDivider(measuredWidth - positionToEdge - mDividerWidth);
                widthOfDivider += mDividerWidth;
            }

            lp.mStart = measuredWidth
                    - (positionToEdge + widthOfDivider + marginEnd + childMeasuredWidth);

            if (checkType(type, LayoutParams.DIVIDER_START)) {
                mMeasureLayoutManager.addDivider(lp.mStart - marginStart - mDividerWidth);
                widthOfDivider += mDividerWidth;
            }
        } else {
            if (checkType(type, LayoutParams.DIVIDER_START)) {
                mMeasureLayoutManager.addDivider(positionToEdge);
                widthOfDivider += mDividerWidth;
            }

            lp.mStart = positionToEdge + widthOfDivider + marginStart;

            if (checkType(type, LayoutParams.DIVIDER_END)) {
                mMeasureLayoutManager.addDivider(lp.mStart + childMeasuredWidth + marginEnd);
                widthOfDivider += mDividerWidth;
            }
        }

        if (checkType(type, LayoutParams.TYPE_START_OR_END_EDGE_SET)) {
            mMeasureLayoutManager.calculateLineIndicatorWithStartOrEndEdge(type, positionToEdge
                    + childMeasuredWidth + marginStart + marginEnd + widthOfDivider);
        } else {
            mMeasureLayoutManager.calculateLineIndicator(type, marginStart
                    + childMeasuredWidth + marginEnd + widthOfDivider);
        }
        lp.mEnd = lp.mStart + childMeasuredWidth;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mMeasureLayoutManager.initLineIndicatorValues(getPaddingStart(), getPaddingEnd());
        mMeasureLayoutManager.clearPositionOfDivier();
        final int count = getChildCount();
        final int width = getMeasuredWidth();
        final boolean isLayoutRtl = SUPPORT_RTL ? getLayoutDirection() == View.LAYOUT_DIRECTION_RTL : false;
        // START END
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child == null || child.getVisibility() == View.GONE) {
                continue;
            }
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            final int type = lp.type;
            if (checkType(type, LayoutParams.TYPE_START_OR_END_SET)) {
                savePosition(isLayoutRtl, child, width);
            } else {
                continue;
            }
        }
        // Rest
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child == null || child.getVisibility() == View.GONE) {
                continue;
            }
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            final int type = lp.type;
            if (!checkType(type, LayoutParams.TYPE_START_OR_END_SET)) {
                savePosition(isLayoutRtl, child, width);
            } else {
                continue;
            }
        }
    }

    private void layoutChild(boolean isLayoutRtl, View child, int width) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (isLayoutRtl) {
            child.layout(width - lp.mEnd, lp.mTop, width - lp.mStart, lp.mBottom);
        } else {
            child.layout(lp.mStart, lp.mTop, lp.mEnd, lp.mBottom);
        }
    }

    /**
     * Draw Divider.
     */
    /** @hide */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        final boolean isLayoutRtl = SUPPORT_RTL ? getLayoutDirection() == View.LAYOUT_DIRECTION_RTL : false;
        final Drawable drawable = getVirticalDivider();
        if (null != drawable) {
            final int width = getMeasuredWidth();
            int left, right, top, bottom;
            final List<Integer> positionOfDivier = mMeasureLayoutManager.mPositionOfDivier;
            final int m1 = HtcListItemManager.getM1(getContext());
            top = m1;
            bottom = mMeasureLayoutManager.mTotalHeight - m1;
            if (null != positionOfDivier) {
                final int size = positionOfDivier.size();
                for (int i = 0; i < size; i++) {
                    if (isLayoutRtl) {
                        right = width - positionOfDivier.get(i);
                        left = right - mDividerWidth;
                    } else {
                        left = positionOfDivier.get(i);
                        right = left + mDividerWidth;
                    }
                    drawable.setBounds(left, top, right, bottom);
                    drawable.draw(canvas);
                }
            }
        }
    }

    public void setAutoMotiveMode(boolean enable) {
        if (enable) {
            if (mMode != MODE_AUTOMOTIVE) {
                mMode = MODE_AUTOMOTIVE;
                initValues();
            }
        } else {
            mMode = MODE_DEFAULT;
            initValues();
        }
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof IHtcListItemAutoMotiveControl) {
                ((IHtcListItemAutoMotiveControl) child).setAutoMotiveMode(enable);
            }
        }
        requestLayout();
    }

    public void setStartIndent(boolean enable) {
        final int startPadding = enable ? HtcListItemManager.getIndentSpace(getContext()) : 0;
        if (SUPPORT_RTL) {
            setPaddingRelative(startPadding, getPaddingTop(), getPaddingEnd(), getPaddingBottom());
        } else {
            setPadding(startPadding, getPaddingTop(), getPaddingEnd(), getPaddingBottom());
        }
    }

    private static boolean checkType(int type, int specification) {
        return (type & specification) != 0 ? true : false;
    }

    @Override
    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected android.view.ViewGroup.LayoutParams generateLayoutParams(
            android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(TYPE_DEFAULT);
    }

    static private class TripleIndicator {
        private int mFirst;
        private int mSecond;
        private int mFooter;

        private void init(int value) {
            mFirst = mSecond = mFooter = value;
        }

        private void calculateLineIndicator(int type, int value) {
            if (checkType(type, LayoutParams.FIRST_LINE)) {
                mFirst += value;
            } else if (checkType(type, LayoutParams.SECONDARY_LINE)) {
                mSecond += value;
            } else if (checkType(type, LayoutParams.FOOTER_LINE)) {
                mFooter += value;
            } else {
                mFirst = mSecond = mFooter = getMaxLine() + value;
            }
        }

        private void calculateLineIndicatorWithStartOrEndEdge(int type, int value) {
            if (checkType(type, LayoutParams.FIRST_LINE)) {
                mFirst = Math.max(mFirst, value);
            } else if (checkType(type, LayoutParams.SECONDARY_LINE)) {
                mSecond = Math.max(mSecond, value);
            } else if (checkType(type, LayoutParams.FOOTER_LINE)) {
                mFooter = Math.max(mFooter, value);
            } else {
                mFirst = Math.max(mFirst, value);
                mSecond = Math.max(mSecond, value);
                mFooter = Math.max(mFooter, value);
            }
        }

        private int getIndicatorVaule(int type) {
            if (checkType(type, LayoutParams.FIRST_LINE)) {
                return mFirst;
            } else if (checkType(type, LayoutParams.SECONDARY_LINE)) {
                return mSecond;
            } else if (checkType(type, LayoutParams.FOOTER_LINE)) {
                return mFooter;
            } else {
                return getMaxLine();
            }
        }

        private int getMaxLine() {
            return Math.max(mFirst, Math.max(mSecond, mFooter));
        }
    }

    private class MeasureLayoutManager {
        TripleIndicator mStart = new TripleIndicator();
        TripleIndicator mEnd = new TripleIndicator();

        private int mTotalHeight;
        private int mFullLineHeight;
        private int mFirstLineHeight;
        private int mSecondaryLineHeight;
        private int mFooterLineHeight;

        private boolean mHasPrimaryBaseline = false;
        private boolean mHasSecondaryBaseline = false;

        private List<Integer> mPositionOfDivier = new ArrayList<Integer>();

        private void initLineIndicatorValues(int paddingStart, int paddingEnd) {
            mStart.init(paddingStart);
            mEnd.init(paddingEnd);
        }

        private void clearPositionOfDivier() {
            mPositionOfDivier.clear();
        }

        private void initHeightValues() {
            mTotalHeight = 0;
            mFullLineHeight = 0;
            mFirstLineHeight = 0;
            mSecondaryLineHeight = 0;
            mFooterLineHeight = 0;
            mHasPrimaryBaseline = false;
            mHasSecondaryBaseline = false;
        }

        private void calculateLineIndicator(int type, int value) {
            if (checkType(type, LayoutParams.END)) {
                mEnd.calculateLineIndicator(type, value);
            } else {
                mStart.calculateLineIndicator(type, value);
            }
        }

        private void calculateLineIndicatorWithStartOrEndEdge(int type, int value) {
            if (checkType(type, LayoutParams.END)) {
                mEnd.calculateLineIndicatorWithStartOrEndEdge(type, value);
            } else {
                mStart.calculateLineIndicatorWithStartOrEndEdge(type, value);
            }
        }

        private int getRestSize(int widthSize, int type) {
            if (checkType(type, LayoutParams.FIRST_LINE)) {
                return Math.max(0, widthSize - mStart.mFirst - mEnd.mFirst);
            } else if (checkType(type, LayoutParams.SECONDARY_LINE)) {
                return Math.max(0, widthSize - mStart.mSecond - mEnd.mSecond);
            } else if (checkType(type, LayoutParams.FOOTER_LINE)) {
                return Math.max(0, widthSize - mStart.mFooter - mEnd.mFooter);
            } else {
                return Math.max(0, widthSize - mStart.getMaxLine() - mEnd.getMaxLine());
            }
        }

        private int getIndicatorVaule(int type) {
            return checkType(type, LayoutParams.END) ? mEnd.getIndicatorVaule(type) : mStart
                    .getIndicatorVaule(type);
        }

        private int getPositionToEdge(int type) {
            if (checkType(type, LayoutParams.TYPE_ALIGN_EDGE_SET)) {
                return 0;
            } else if (checkType(type, LayoutParams.TYPE_ALIGN_147_EDGE_SET)) {
                return mSize147;
            } else if (checkType(type, LayoutParams.TYPE_ALIGN_DEFAULT_HEIGHT_EDGE_SET)) {
                return mSizeDefaultHeight;
            } else {
                return getIndicatorVaule(type);
            }
        }

        private void calculateFullLineHeight(int fullLineHeight) {
            this.mFullLineHeight = Math.max(this.mFullLineHeight, fullLineHeight);
        }

        private void calculateFirstLineHeight(int firstLineHeight) {
            this.mFirstLineHeight = Math.max(this.mFirstLineHeight, firstLineHeight);
        }

        private void calculateSecondaryLineHeight(int secondaryLineHeight) {
            this.mSecondaryLineHeight = Math.max(this.mSecondaryLineHeight,
                    secondaryLineHeight);
        }

        private void calculateFooterLineHeight(int footerLineHeight) {
            this.mFooterLineHeight = Math.max(this.mFooterLineHeight, footerLineHeight);
        }

        private void setHasPrimaryBaseline(boolean mHasPrimaryBaseline) {
            this.mHasPrimaryBaseline = mHasPrimaryBaseline;
        }

        private void setHasSecondaryBaseline(boolean mHasSecondaryBaseline) {
            this.mHasSecondaryBaseline = mHasSecondaryBaseline;
        }

        private void calculateHeight(int type, int height) {
            if (checkType(type, LayoutParams.FIRST_LINE)) {
                if (checkType(type, LayoutParams.PRIMARY)) {
                    mMeasureLayoutManager.setHasPrimaryBaseline(true);
                }
                mMeasureLayoutManager.calculateFirstLineHeight(height);
            } else if (checkType(type, LayoutParams.SECONDARY_LINE)) {
                if (checkType(type, LayoutParams.SECONDARY)) {
                    mMeasureLayoutManager.setHasSecondaryBaseline(true);
                }
                mMeasureLayoutManager.calculateSecondaryLineHeight(height);
            } else if (checkType(type, LayoutParams.FOOTER_LINE)) {
                mMeasureLayoutManager.calculateFooterLineHeight(height);
            } else {
                mMeasureLayoutManager.calculateFullLineHeight(height);
            }
        }

        private void addDivider(int position) {
            mPositionOfDivier.add(position);
        }
    }

    @Override
    public int getPaddingStart() {
        return SUPPORT_RTL ? super.getPaddingStart() : super.getPaddingLeft();
    }

    @Override
    public int getPaddingEnd() {
        return SUPPORT_RTL ? super.getPaddingEnd() : super.getPaddingRight();
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public static final int SIZE_WRAP_CONTENT = 0x00000001;
        public static final int SIZE_147 = 0x00000002;
        public static final int SIZE_DEFAULT_HEIGHT = 0x00000004;
        public static final int SIZE_REST = 0x00000008;
        public static final int SIZE_1_3_REST = 0x00000010;
        public static final int SIZE_2_3_REST = 0x00000020;
        public static final int SIZE_AUTO_1_3_REST = 0x00000010;
        public static final int SIZE_AUTO_2_3_REST = 0x00000020;
        public static final int SIZE_3_10_REST = 0x00000040;
        public static final int SIZE_4_10_REST = 0x00000080;

        public static final int START = 0x00000100;
        public static final int END = 0x00000200;

        public static final int ALIGN_START_EDGE = 0x00000400;
        public static final int ALIGN_START_147_EDGE = 0x00000800;
        public static final int ALIGN_START_DEFAULT_HEIGHT_EDGE = 0x00001000;
        public static final int ALIGN_END_EDGE = 0x00002000;
        public static final int ALIGN_END_147_EDGE = 0x00004000;
        public static final int ALIGN_END_DEFAULT_HEIGHT_EDGE = 0x00008000;

        public static final int DIVIDER_START = 0x00010000;
        public static final int DIVIDER_END = 0x00020000;

        public static final int CENTER_HORIZONTAL = 0x00040000;
        public static final int CENTER_VERTICAL = 0x00080000;
        public static final int ALIGN_TOP_EDGE = 0x00100000;
        public static final int PRIMARY = 0x00200000; // font style baseline
        public static final int SECONDARY = 0x00400000; // font style baseline
        public static final int DEFAULT_HEIGHT_CENTER_VERTICAL = 0x04000000;

        public static final int FIRST_LINE = 0x00800000;
        public static final int SECONDARY_LINE = 0x01000000;
        public static final int FOOTER_LINE = 0x02000000;

        @ViewDebug.ExportedProperty(category = "CommonControl")
        private int mStart, mTop, mEnd, mBottom;

        @ViewDebug.ExportedProperty(category = "CommonControl")
        private int type = TYPE_DEFAULT;

        final static int TYPE_ALIGN_EDGE_SET = ALIGN_START_EDGE | ALIGN_END_EDGE;
        final static int TYPE_ALIGN_147_EDGE_SET = ALIGN_START_147_EDGE | ALIGN_END_147_EDGE;
        final static int TYPE_ALIGN_DEFAULT_HEIGHT_EDGE_SET = ALIGN_START_DEFAULT_HEIGHT_EDGE
                | ALIGN_END_DEFAULT_HEIGHT_EDGE;
        final static int TYPE_SIZE_REST_SET = SIZE_REST | SIZE_3_10_REST | SIZE_4_10_REST;
        final static int TYPE_SIZE_AUTO_REST_SET = SIZE_AUTO_1_3_REST | SIZE_AUTO_2_3_REST;
        final static int TYPE_SIZE_FIX_WRAP_CONTENT_SET = SIZE_147 | SIZE_DEFAULT_HEIGHT
                | SIZE_WRAP_CONTENT;
        final static int TYPE_ALIGN_START_EDGE_SET = ALIGN_START_EDGE | ALIGN_START_147_EDGE
                | ALIGN_START_DEFAULT_HEIGHT_EDGE;
        final static int TYPE_ALIGN_END_EDGE_SET = ALIGN_END_EDGE | ALIGN_END_147_EDGE
                | ALIGN_END_DEFAULT_HEIGHT_EDGE;

        final static int TYPE_START_OR_END_SET = START | END;
        final static int TYPE_START_OR_END_EDGE_SET = TYPE_ALIGN_EDGE_SET | TYPE_ALIGN_147_EDGE_SET
                | TYPE_ALIGN_DEFAULT_HEIGHT_EDGE_SET;
        final static int TYPE_SIZE_SET = TYPE_SIZE_REST_SET | TYPE_SIZE_FIX_WRAP_CONTENT_SET;
        final static int TYPE_TOP_POSITION_SET = CENTER_VERTICAL | ALIGN_TOP_EDGE | PRIMARY
                | SECONDARY;
        final static int TYPE_LINE_SET = FIRST_LINE | SECONDARY_LINE | FOOTER_LINE;

        final static int TYPE_START_OR_END_SET_DEFAULT = ~TYPE_START_OR_END_SET;
        final static int TYPE_START_OR_END_EDGE_SET_DEFAULT = ~TYPE_START_OR_END_EDGE_SET;
        final static int TYPE_SIZE_SET_DEFAULT = (~TYPE_SIZE_SET) | SIZE_WRAP_CONTENT;
        final static int TYPE_TOP_POSITION_SET_DEFAULT = (~TYPE_TOP_POSITION_SET) | ALIGN_TOP_EDGE;
        final static int TYPE_LINE_SET_DEFAULT = ~TYPE_LINE_SET;

        /**
         * {@inheritDoc}
         *
         * @hide
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs,
                    com.htc.lib1.cc.R.styleable.HtcListItem_Layout);
            int type = a.getInt(com.htc.lib1.cc.R.styleable.HtcListItem_Layout_layout_type,
                    this.type);
            setType(type);
            a.recycle();
        }

        /**
         * {@inheritDoc}
         *
         * @hide
         */
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(int type) {
            super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            setType(type);
        }

        private void setType(int type) {
            int t;
            t = getValidType(type, TYPE_START_OR_END_SET, TYPE_START_OR_END_SET_DEFAULT);
            t = getValidType(t, TYPE_START_OR_END_EDGE_SET, TYPE_START_OR_END_EDGE_SET_DEFAULT);
            t = getValidType(t, TYPE_SIZE_SET, TYPE_SIZE_SET_DEFAULT);
            t = getValidType(t, TYPE_TOP_POSITION_SET, TYPE_TOP_POSITION_SET_DEFAULT);
            t = getValidType(t, TYPE_LINE_SET, TYPE_LINE_SET_DEFAULT);
            if (checkType(t, TYPE_ALIGN_START_EDGE_SET)) {
                t = t & TYPE_START_OR_END_SET_DEFAULT | START;
            } else if (checkType(t, TYPE_ALIGN_END_EDGE_SET)) {
                t = t & TYPE_START_OR_END_SET_DEFAULT | END;
            } else if (checkType(t, TYPE_SIZE_AUTO_REST_SET | DEFAULT_HEIGHT_CENTER_VERTICAL)) {
                t = t & TYPE_LINE_SET_DEFAULT;
            }
            this.type = t;
        }

        private static int getValidType(int type, int specificationSet, int specificationSetDefault) {
            final int t = type & specificationSet;
            int count = 0;
            final int flag = 1;
            for (int i = 0; i < 32; i++) {
                if ((t & flag << i) != 0) {
                    count += 1;
                }
                if (count > 1) {
                    break;
                }
            }
            return (count > 1) ? type & specificationSetDefault : type;
        }

        @Override
        public int getMarginStart() {
            return SUPPORT_RTL ? super.getMarginStart() : leftMargin;
        }

        @Override
        public int getMarginEnd() {
            return SUPPORT_RTL ? super.getMarginEnd() : rightMargin;
        }
    }
}
