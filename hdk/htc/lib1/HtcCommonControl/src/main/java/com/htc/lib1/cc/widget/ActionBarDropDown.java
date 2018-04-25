
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.graphics.Paint.FontMetricsInt;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.ActionBarUtil;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.LogUtil;
import com.htc.lib1.cc.util.res.HtcResUtil;

/**
 * A widget can be used in Htc action bar.
 */
public class ActionBarDropDown extends ViewGroup {
    /**
     * @hide
     */
    public static final int MODE_DEFAULT = Integer.MIN_VALUE;
    /**
     * Hide Automatically by SDK Team [U12000].
     *
     * @hide
     */
    public static final int MODE_EXTERNAL = 1;

    /**
     * Automotive mode.
     */
    public static final int MODE_AUTOMOTIVE = 2;

    /**
     * One TextView multiline mode.
     */
    public static final int MODE_ONE_MULTIILINE_TEXTVIEW = 3;

    private ActionBarTextView mPrimaryView = null;
    private ActionBarTextView mSecondaryView = null;
    private ActionBarTextView mCounterView = null;
    private ImageView mArrowView = null;

    @ExportedProperty(category = "CommonControl")
    private int mMeasureSpecM2;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param mode contruct with specific mode
     */
    public ActionBarDropDown(Context context, int mode) {
        this(context);

        setSupportMode(mode);
    }

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public ActionBarDropDown(Context context) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        mMeasureSpecM2 = HtcResUtil.getM2(context);

        getDefaultHeight();
        // setup the module overall environment
        // setPadding(padding,0,padding,0);
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

        // inflate the external layout and merge to current layout
        LayoutInflater.from(context).inflate(R.layout.action_dropdown, this, true);

        mArrowView = (ImageView) findViewById(R.id.arrow);
        mPrimaryView = (ActionBarTextView) findViewById(R.id.primary);
        mSecondaryView = (ActionBarTextView) findViewById(R.id.secondary);
        mCounterView = (ActionBarTextView) findViewById(R.id.counter);
        // check the layout resource correctness
        if (mPrimaryView == null || mSecondaryView == null || mArrowView == null) throw new RuntimeException("inflate layout resource incorrect");

        mPrimaryView.setState(ActionBarTextView.PRIMARY_DEFAULT_ONLY);
        mSecondaryView.setState(ActionBarTextView.SECONDARY_DEFAULT);
        mCounterView.setState(ActionBarTextView.COUNTER_FOLLOW_PRIMARY);

        setBackground(ActionBarUtil.getActionMenuItemBackground(context));

        /* support D-Pad function */
        setFocusable(true);
    }

    /**
     * Get primary text.
     *
     * @return return primary text
     */
    public CharSequence getPrimaryText() {
        return mPrimaryView == null ? null : mPrimaryView.getText();
    }

    /**
     * Get secondary text.
     *
     * @return return secondary text
     */
    public CharSequence getSecondaryText() {
        return mSecondaryView == null ? null : mSecondaryView.getText();
    }

    /**
     * Set primary text.
     *
     * @param text the string set to primary text
     */
    public void setPrimaryText(String text) {
        if (mPrimaryView != null) {
            mPrimaryView.setText(text);
            setPrimaryVisibility(VISIBLE);
            requestLayout();
        }
    }

    /**
     * Set primary text.
     *
     * @param resource the string resource id set to primary text
     */
    public void setPrimaryText(int resource) {
        if (mPrimaryView != null) {
            mPrimaryView.setText(resource);
            setPrimaryVisibility(VISIBLE);
            requestLayout();
        }
    }

    /**
     * Set secondary text.
     *
     * @param text the string set to secondary text
     */
    public void setSecondaryText(String text) {
        if (mSecondaryView != null) {
            mSecondaryView.setText(text);
            setSecondaryVisibility(VISIBLE);
            requestLayout();
        }
    }

    /**
     * Set secondary text.
     *
     * @param resource the string resource id set to secondary text
     */
    public void setSecondaryText(int resource) {
        if (mSecondaryView != null) {
            mSecondaryView.setText(resource);
            setSecondaryVisibility(VISIBLE);
            requestLayout();
        }
    }

    /**
     * Get primary text visibility.
     *
     * @return visibility of primary text
     * @deprecated [Not use any longer]
     * @hide
     */
    @Deprecated
    public int getPrimaryVisibility() {
        return mPrimaryView == null ? GONE : mPrimaryView.getVisibility();
    }

    /**
     * Get secondary text visibility.
     *
     * @return visibility of secondary text
     */
    public int getSecondaryVisibility() {
        return mSecondaryView == null ? GONE : mSecondaryView.getVisibility();
    }

    /**
     * Set primary text visibility.
     *
     * @param visibility Set the visibility state of this view.
     */
    public void setPrimaryVisibility(int visibility) {
        if (mPrimaryView != null && mPrimaryView.getVisibility() != visibility) {
            mPrimaryView.setVisibility(visibility);
        }
    }

    /**
     * Set secondary text visibility.
     *
     * @param visibility Set the visibility state of this view.
     */
    public void setSecondaryVisibility(int visibility) {
        if (mSecondaryView != null && mSecondaryView.getVisibility() != visibility) {
            mSecondaryView.setVisibility(visibility);
            adjustPrimaryState();
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Set arrow enable.
     *
     * @param enable Set the enabled state of arrow view.
     */
    public void setArrowEnabled(boolean enable) {
        if (mArrowView != null && mArrowView.getVisibility() != (enable ? VISIBLE : GONE)) mArrowView.setVisibility(enable ? VISIBLE : GONE);
    }

    /**
     * Hide Automatically by SDK Team [U12000].
     *
     * @hide
     */
    @Override
    public void setLayerType(int layerType, android.graphics.Paint paint) {
        super.setLayerType(layerType, paint);
    }

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = MODE_DEFAULT, to = "MODE_DEFAULT"),
            @IntToString(from = MODE_EXTERNAL, to = "MODE_EXTERNAL"),
            @IntToString(from = MODE_AUTOMOTIVE, to = "MODE_AUTOMOTIVE"),
            @IntToString(from = MODE_ONE_MULTIILINE_TEXTVIEW, to = "MODE_ONE_MULTIILINE_TEXTVIEW")
    })
    private int mSupportMode = MODE_DEFAULT;

    private void adjustPrimaryState() {
        if (null == mPrimaryView || null == mSecondaryView) return;

        if (mSupportMode == MODE_AUTOMOTIVE) {
            mPrimaryView.setState((View.VISIBLE == mSecondaryView.getVisibility()) ? ActionBarTextView.PRIMARY_AUTOMTIVE_TWO_TEXTVIEW : ActionBarTextView.PRIMARY_AUTOMTIVE_ONLY);
        } else if (mSupportMode == MODE_ONE_MULTIILINE_TEXTVIEW) {
            mPrimaryView.setState(ActionBarTextView.PRIMARY_MULTILINE_ONLY);
        } else {
            mPrimaryView.setState((View.VISIBLE == mSecondaryView.getVisibility()) ? ActionBarTextView.PRIMARY_DEFAULT_TWO_TEXTVIEW : ActionBarTextView.PRIMARY_DEFAULT_ONLY);
        }
    }

    private void adjustSecondaryState() {
        if (null == mSecondaryView) {
            return;
        }
        if (mSupportMode == MODE_AUTOMOTIVE) {
            mSecondaryView.setState(ActionBarTextView.SECONDARY_AUTOMOTIVE);
        } else {
            mSecondaryView.setState(ActionBarTextView.SECONDARY_DEFAULT);
        }
    }

    /**
     * @hide
     **/
    public void setCounter(boolean enable) {
        if (mCounterView != null && mCounterView.getVisibility() != (enable ? VISIBLE : GONE)) {
            mCounterView.setVisibility(enable ? VISIBLE : GONE);
        }
    }

    // support special usage for automotive mode only
    /**
     * Hide Automatically by SDK Team [U12000].
     *
     * @hide
     */
    public void setSupportMode(int mode) {
        // skip to avoid useless operation
        if (mSupportMode == mode) return;

        if (mode == MODE_AUTOMOTIVE) {
            mSupportMode = MODE_AUTOMOTIVE;
            setupAutomotiveMode();
        } else if (mode == MODE_ONE_MULTIILINE_TEXTVIEW) {
            mSupportMode = MODE_ONE_MULTIILINE_TEXTVIEW;
            if (null != mPrimaryView) {
                mPrimaryView.setState(ActionBarTextView.PRIMARY_MULTILINE_ONLY);
            }
            setSecondaryVisibility(View.GONE);

        } else {
            mSupportMode = MODE_DEFAULT;
        }
        getDefaultHeight();
        adjustPrimaryState();
        adjustSecondaryState();
    }

    // special support for automotive usage
    private void setupAutomotiveMode() {
        // reset the margin value for arrow view
        if (mArrowView != null) {
            mArrowView.setImageResource(R.drawable.automotive_common_arrow_down);
            MarginLayoutParams mlp = (MarginLayoutParams) mArrowView.getLayoutParams();
            if (null != mlp) {
                if (ActionBarUtil.IS_SUPPORT_RTL) {
                    mlp.setMarginStart(mMeasureSpecM2);
                } else {
                    mlp.leftMargin = mMeasureSpecM2;
                }
                mArrowView.setLayoutParams(mlp);
            }
        }
    }

    /**
     * Use to get primary textview.
     *
     * @return the TextView of primary text
     */
    public TextView getPrimaryView() {
        return mPrimaryView;
    }

    /**
     * Use to get secondary textview.
     *
     * @return the TextView of secondary text
     */
    public TextView getSecondaryView() {
        return mSecondaryView;
    }

    /**
     * Use to get counterView.
     *
     * @return the TextView of counterView
     * @hide
     */
    public TextView getCounterView() {
        return mCounterView;
    }

    /**
     * (non-Javadoc).
     *
     * @see android.widget.RelativeLayout#onLayout(boolean, int, int, int, int)
     * @hide
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final boolean isLayoutRtl;
        final int currentStart;
        if (ActionBarUtil.IS_SUPPORT_RTL) {
            isLayoutRtl = getLayoutDirection() == LAYOUT_DIRECTION_RTL;
            currentStart = getPaddingStart();
        } else {
            isLayoutRtl = false;
            currentStart = getPaddingLeft();
        }
        final int currentTop = getPaddingTop();
        final int parentWidth = r - l;
        final int parentHeight = b - t - currentTop - getPaddingBottom();
        int topPrimary = 0, topSecondary = 0, primaryUsed = 0, secondaryUsed = 0;
        int childWidth, childHeight, childLeft, childTop = 0;
        MarginLayoutParams mlp;

        if (null != mPrimaryView && VISIBLE == mPrimaryView.getVisibility()) {
            childWidth = mPrimaryView.getMeasuredWidth();
            childHeight = mPrimaryView.getMeasuredHeight();
            mlp = (MarginLayoutParams) mPrimaryView.getLayoutParams();
            childLeft = ActionBarUtil.getChildLeft(parentWidth, primaryUsed, ActionBarUtil.getStartMarginForPlatform(mlp) + currentStart, childWidth, isLayoutRtl);

            if (null != mSecondaryView && VISIBLE == mSecondaryView.getVisibility()) {
                if (isDefaultFontEnabled()) {
                    if (MODE_AUTOMOTIVE != mSupportMode) {
                        childTop = (int) (((float) mNormalPrimaryHeight) * 0.14f + 0.5f) + mNormalPrimaryBaseline - getPrimaryBaseline();
                    } else {
                        childTop = mNormalPrimaryBaseline - getPrimaryBaseline();
                    }
                } else {
                    if (MODE_AUTOMOTIVE != mSupportMode) {
                        childTop = (int) (((float) childHeight) * 0.14f + 0.5f);
                    }
                }
                topPrimary = childTop += currentTop;
                childTop += mlp.topMargin;
            } else {
                childTop = (parentHeight - childHeight) / 2;
                topPrimary = childTop += currentTop;
                childTop += mlp.topMargin - mlp.bottomMargin;
            }
            ActionBarUtil.setChildFrame(mPrimaryView, childLeft, childTop, childWidth, childHeight);
            primaryUsed += ActionBarUtil.getChildUsedWidth(childWidth, mlp);
        }

        if (null != mSecondaryView && VISIBLE == mSecondaryView.getVisibility()) {
            childWidth = mSecondaryView.getMeasuredWidth();
            childHeight = mSecondaryView.getMeasuredHeight();
            mlp = (MarginLayoutParams) mSecondaryView.getLayoutParams();

            if (isDefaultFontEnabled()) {
                if (MODE_AUTOMOTIVE == mSupportMode) {
                    childTop = (int) (((float) mNormalPrimaryHeight) * 0.9f + 0.5f) + mNormalSecondaryBaseline - getSecondrayBaseline();
                } else {
                    childTop = (int) (((float) mNormalPrimaryHeight) * 1.04f + 0.5f) + mNormalSecondaryBaseline - getSecondrayBaseline();
                }
            } else {
                if (MODE_AUTOMOTIVE == mSupportMode) {
                    childTop = (int) (((float) mPrimaryView.getMeasuredHeight()) * 0.9f + 0.5f);
                } else {
                    childTop = (int) (((float) mPrimaryView.getMeasuredHeight()) * 1.04f + 0.5f);
                }
            }

            topSecondary = childTop += currentTop;
            childTop += mlp.topMargin;

            childLeft = ActionBarUtil.getChildLeft(parentWidth, secondaryUsed, ActionBarUtil.getStartMarginForPlatform(mlp) + currentStart, childWidth, isLayoutRtl);

            ActionBarUtil.setChildFrame(mSecondaryView, childLeft, childTop, childWidth, childHeight);
            secondaryUsed += ActionBarUtil.getChildUsedWidth(childWidth, mlp);
        }

        if (null != mCounterView && VISIBLE == mCounterView.getVisibility()) {
            childWidth = mCounterView.getMeasuredWidth();
            childHeight = mCounterView.getMeasuredHeight();
            mlp = (MarginLayoutParams) mCounterView.getLayoutParams();

            if (null != mSecondaryView && VISIBLE == mSecondaryView.getVisibility()) {
                childTop = topSecondary + getSecondrayBaseline() - mCounterView.getBaseline();
                childTop += mlp.topMargin;
                childLeft = ActionBarUtil.getChildLeft(parentWidth, secondaryUsed, ActionBarUtil.getStartMarginForPlatform(mlp) + currentStart, childWidth, isLayoutRtl);
            } else {
                if (MODE_ONE_MULTIILINE_TEXTVIEW == mSupportMode) {
                    childTop = (parentHeight - childHeight) / 2 + currentTop + mlp.topMargin - mlp.bottomMargin;
                } else {
                    childTop = topPrimary + getPrimaryBaseline() - mCounterView.getBaseline();
                    childTop += mlp.topMargin;
                }

                childLeft = ActionBarUtil.getChildLeft(parentWidth, primaryUsed, ActionBarUtil.getStartMarginForPlatform(mlp) + currentStart, childWidth, isLayoutRtl);
                primaryUsed += ActionBarUtil.getChildUsedWidth(childWidth, mlp);
            }
            ActionBarUtil.setChildFrame(mCounterView, childLeft, childTop, childWidth, childHeight);
        }

        if (null != mArrowView && VISIBLE == mArrowView.getVisibility()) {
            childWidth = mArrowView.getMeasuredWidth();
            childHeight = mArrowView.getMeasuredHeight();
            mlp = (MarginLayoutParams) mArrowView.getLayoutParams();

            boolean showArrow = true;
            if (MODE_ONE_MULTIILINE_TEXTVIEW == mSupportMode) {
                childTop = (parentHeight - childHeight) / 2 + currentTop + mlp.topMargin - mlp.bottomMargin;
                if (null != mPrimaryView) {
                    Layout tl = mPrimaryView.getLayout();
                    showArrow = (null != tl && tl.getLineCount() > 0 && tl.getEllipsisCount(tl.getLineCount() - 1) > 0);
                }
            } else {
                childTop = topPrimary + getPrimaryBaseline() - ((HtcResUtil.getHeightOfChar(mPrimaryView, "e") + childHeight) / 2);
                childTop += mlp.topMargin;
            }
            childLeft = ActionBarUtil.getChildLeft(parentWidth, primaryUsed, ActionBarUtil.getStartMarginForPlatform(mlp) + currentStart, childWidth, isLayoutRtl);
            ActionBarUtil.setChildFrame(mArrowView, childLeft, childTop, showArrow ? childWidth : 0, showArrow ? childHeight : 0);
        }
    }

    /**
     * (non-Javadoc).
     *
     * @see android.widget.RelativeLayout#onMeasure(int, int)
     * @hide
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        MarginLayoutParams mlp;
        int primaryUsedWidth = 0;
        int secondaryUsedWidth = 0;

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);

        if (ActionBarUtil.IS_SUPPORT_RTL) {
            width = width - getPaddingStart() - getPaddingEnd();
        } else {
            width = width - getPaddingLeft() - getPaddingRight();
        }

        int countUsedWidth = 0;
        if (null != mCounterView && VISIBLE == mCounterView.getVisibility()) {
            mCounterView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

            mlp = (MarginLayoutParams) mCounterView.getLayoutParams();
            countUsedWidth = ActionBarUtil.getChildUsedWidth(mCounterView.getMeasuredWidth(), mlp);
        }
        if (null != mSecondaryView && VISIBLE == mSecondaryView.getVisibility()) {
            secondaryUsedWidth += countUsedWidth;
        } else {
            primaryUsedWidth += countUsedWidth;
        }

        int arrowUsedWidth = 0;
        if (null != mArrowView && VISIBLE == mArrowView.getVisibility()) {
            mArrowView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

            mlp = (MarginLayoutParams) mArrowView.getLayoutParams();

            arrowUsedWidth = ActionBarUtil.getChildUsedWidth(mArrowView.getMeasuredWidth(), mlp);
            primaryUsedWidth += arrowUsedWidth;
        }

        if (null != mPrimaryView && VISIBLE == mPrimaryView.getVisibility()) {
            mlp = (MarginLayoutParams) mPrimaryView.getLayoutParams();
            int margin = ActionBarUtil.getChildUsedWidth(0, mlp);
            int usedWidth = ((MODE_ONE_MULTIILINE_TEXTVIEW == mSupportMode) ? countUsedWidth : primaryUsedWidth);

            if (width - usedWidth - margin < 0) {
                LogUtil.logE("ActionBarDropDown", "primaryWidth < 0 : ",
                        "width = ", width,
                        ",usedWidth = ", usedWidth,
                        ",margin = ", margin);
            }
            ActionBarUtil.measureActionBarTextView(mPrimaryView, width, usedWidth + margin, MODE_ONE_MULTIILINE_TEXTVIEW != mSupportMode);

            if (MODE_ONE_MULTIILINE_TEXTVIEW == mSupportMode) {
                Layout l = mPrimaryView.getLayout();
                if (null != l && l.getLineCount() > 0 && l.getEllipsisCount(l.getLineCount() - 1) > 0) {
                    int primaryWidth = width - primaryUsedWidth - margin;
                    if (primaryWidth < 0) {
                        primaryWidth = 0;
                        LogUtil.logE("ActionBarDropDown", "multiLinePrimaryWidth < 0 : ",
                                "width = ", width,
                                ",primaryUsedWidth = ", primaryUsedWidth,
                                ",margin = ", margin);
                    }
                    int primaryWidthMeasureSpec = MeasureSpec.makeMeasureSpec(primaryWidth, MeasureSpec.EXACTLY);
                    int primaryHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

                    mPrimaryView.measure(primaryWidthMeasureSpec, primaryHeightMeasureSpec);
                } else {
                    mArrowView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
                }
            }
            getPrimaryBaseline();
        }

        if (null != mSecondaryView && VISIBLE == mSecondaryView.getVisibility()) {
            mlp = (MarginLayoutParams) mSecondaryView.getLayoutParams();
            int margin = ActionBarUtil.getChildUsedWidth(0, mlp);

            if (ActionBarContainer.ENABLE_DEBUG) {
                if (width - secondaryUsedWidth - margin <= 0) {
                    LogUtil.logE("ActionBarDropDown", "secondaryWidth <= 0 : ",
                            "width = ", width,
                            ",secondaryUsedWidth = ", secondaryUsedWidth,
                            ",margin = ", margin);
                }
            }
            ActionBarUtil.measureActionBarTextView(mSecondaryView, width, secondaryUsedWidth + margin, true);
            getSecondrayBaseline();
        }
    }
    /**
     * (non-Javadoc).
     *
     * @see android.view.ViewGroup#generateDefaultLayoutParams()
     * @hide
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * (non-Javadoc).
     *
     * @see android.view.ViewGroup#generateLayoutParams(android.util.AttributeSet)
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * (non-Javadoc).
     *
     * @see android.view.ViewGroup#generateLayoutParams(android.view.ViewGroup. LayoutParams)
     */
    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    /**
     * (non-Javadoc).
     *
     * @see android.view.ViewGroup#checkLayoutParams(android.view.ViewGroup.LayoutParams )
     * @hide
     */
    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }

    @ExportedProperty(category = "CommonControl")
    private int mNormalPrimaryHeight;
    @ExportedProperty(category = "CommonControl")
    private int mNormalPrimaryBaseline;
    @ExportedProperty(category = "CommonControl")
    private int mNormalSecondaryBaseline;

    private void getDefaultHeight() {
        FontMetricsInt primaryFontMetricsInt;
        FontMetricsInt secondaryFontMetricsInt;
        if (mSupportMode == MODE_AUTOMOTIVE) {
            primaryFontMetricsInt = HtcResUtil.getFontStyleMetrics(getContext(), R.style.fixed_automotive_title_primary_s);
            secondaryFontMetricsInt = HtcResUtil.getFontStyleMetrics(getContext(), R.style.fixed_automotive_title_secondary_m);
        } else {
            primaryFontMetricsInt = HtcResUtil.getFontStyleMetrics(getContext(), R.style.fixed_title_primary_m);
            secondaryFontMetricsInt = HtcResUtil.getFontStyleMetrics(getContext(), R.style.fixed_title_secondary_m);
        }
        if (primaryFontMetricsInt != null) {
            mNormalPrimaryBaseline = 0 - primaryFontMetricsInt.top;
            mNormalPrimaryHeight = primaryFontMetricsInt.bottom - primaryFontMetricsInt.top;
        }
        if (secondaryFontMetricsInt != null) {
            mNormalSecondaryBaseline = 0 - secondaryFontMetricsInt.top;
        }
    }

    @ExportedProperty(category = "CommonControl")
    private boolean isDefaultFontEnabled() {
        return mNormalPrimaryBaseline > 0 && mNormalPrimaryHeight > 0 && mNormalSecondaryBaseline > 0;
    }

    @ExportedProperty(category = "CommonControl")
    private int mPrimaryBaseline = -1;
    @ExportedProperty(category = "CommonControl")
    private int mSecondaryBaseline = -1;

    private int getPrimaryBaseline() {
        if (mPrimaryView != null) {
            int baseline = mPrimaryView.getBaseline();
            if (baseline != -1 && baseline != mPrimaryBaseline) {
                mPrimaryBaseline = baseline;
            }
        }
        return mPrimaryBaseline;
    }

    private int getSecondrayBaseline() {
        if (mSecondaryView != null) {
            int baseline = mSecondaryView.getBaseline();
            if (baseline != -1 && baseline != mSecondaryBaseline) {
                mSecondaryBaseline = baseline;
            }
        }
        return mSecondaryBaseline;
    }
}
