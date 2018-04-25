
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.htc.lib1.cc.util.ActionBarUtil;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.LogUtil;
import com.htc.lib1.cc.util.res.HtcResUtil;

import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.widget.ImageView;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.Drawable;

import com.htc.lib1.cc.R;

/**
 * @hide
 */
public class ActionBarRefresh extends ViewGroup {

    public static final int MODE_PULLDOWN = 1;
    public static final int MODE_UPDATING = 2;

    public static final int CATEGORY_TITLE = 1;
    public static final int CATEGORY_SUBTITLE = 2;
    public static final int CATEGORY_ARROW = 4;

    static final int SHIFT = 4;
    static final int MASK = 0x0F;

    public static final int TYPE_UPDATE_WITH_TITLE = CATEGORY_TITLE;
    public static final int TYPE_UPDATE_WITH_TITLE_DROPDOWN = TYPE_UPDATE_WITH_TITLE | CATEGORY_ARROW;
    public static final int TYPE_UPDATE_WITH_SUBTITLE = CATEGORY_TITLE | CATEGORY_SUBTITLE;
    public static final int TYPE_UPDATE_WITH_SUBTITLE_DROPDOWN = TYPE_UPDATE_WITH_SUBTITLE | CATEGORY_ARROW;

    public static final int TYPE_PULLDOWN_WITH_TITLE = CATEGORY_TITLE;
    public static final int TYPE_PULLDOWN_WITH_SUBTITLE = CATEGORY_TITLE | CATEGORY_SUBTITLE;

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = TYPE_UPDATE_WITH_TITLE | (TYPE_PULLDOWN_WITH_TITLE << SHIFT), to = "TYPE_UPDATE_WITH_TITLE | TYPE_PULLDOWN_WITH_TITLE"),
            @IntToString(from = TYPE_UPDATE_WITH_TITLE | (TYPE_PULLDOWN_WITH_SUBTITLE << SHIFT), to = "TYPE_UPDATE_WITH_TITLE | TYPE_PULLDOWN_WITH_SUBTITLE"),
            @IntToString(from = TYPE_UPDATE_WITH_TITLE_DROPDOWN | (TYPE_PULLDOWN_WITH_TITLE << SHIFT), to = "TYPE_UPDATE_WITH_TITLE | TYPE_PULLDOWN_WITH_TITLE"),
            @IntToString(from = TYPE_UPDATE_WITH_TITLE_DROPDOWN | (TYPE_PULLDOWN_WITH_SUBTITLE << SHIFT), to = "TYPE_UPDATE_WITH_TITLE | TYPE_PULLDOWN_WITH_SUBTITLE"),
            @IntToString(from = TYPE_UPDATE_WITH_SUBTITLE | (TYPE_PULLDOWN_WITH_TITLE << SHIFT), to = "TYPE_UPDATE_WITH_TITLE | TYPE_PULLDOWN_WITH_TITLE"),
            @IntToString(from = TYPE_UPDATE_WITH_SUBTITLE | (TYPE_PULLDOWN_WITH_SUBTITLE << SHIFT), to = "TYPE_UPDATE_WITH_TITLE | TYPE_PULLDOWN_WITH_SUBTITLE"),
            @IntToString(from = TYPE_UPDATE_WITH_SUBTITLE_DROPDOWN | (TYPE_PULLDOWN_WITH_TITLE << SHIFT), to = "TYPE_UPDATE_WITH_TITLE | TYPE_PULLDOWN_WITH_TITLE"),
            @IntToString(from = TYPE_UPDATE_WITH_SUBTITLE_DROPDOWN | (TYPE_PULLDOWN_WITH_SUBTITLE << SHIFT), to = "TYPE_UPDATE_WITH_TITLE | TYPE_PULLDOWN_WITH_SUBTITLE")
    })
    private int mDisplayType = TYPE_UPDATE_WITH_TITLE | (TYPE_PULLDOWN_WITH_TITLE << SHIFT);

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = MODE_PULLDOWN, to = "UPDATING_MODE_PULLDOWN"),
            @IntToString(from = MODE_UPDATING, to = "UPDATING_MODE_UPDATING")
    })
    private int mMode = -1;

    // record the current used text view
    private ImageView mIconView = null;
    private ActionBarProgressBar mProgressView = null;
    private ActionBarTextView mPrimaryView = null;
    private ActionBarTextView mSecondaryView = null;
    private ImageView mArrowView = null;

    private String mPullDownTitle = null;
    private String mPullDownSubTitle = null;
    private String mUpdateTitle = null;
    private String mUpdateSubTitle = null;

    public ActionBarRefresh(Context context) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        getDefaultHeight();

        // setup the module overall environment
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

        // inflate external layout and merge to current parent
        LayoutInflater.from(context).inflate(R.layout.action_refresh, this, true);

        mArrowView = (ImageView) findViewById(R.id.arrow);
        mIconView = (ImageView) findViewById(R.id.icon);
        mProgressView = (ActionBarProgressBar) findViewById(R.id.progress);
        mPrimaryView = (ActionBarTextView) findViewById(R.id.primary);
        mSecondaryView = (ActionBarTextView) findViewById(R.id.secondary);

        // check the layout resource correctness
        if (mIconView == null || mProgressView == null || mPrimaryView == null || mSecondaryView == null) {
            throw new RuntimeException("inflate layout resource incorrect");
        }

        Drawable mDrawable = mIconView.getDrawable();
        mIconView.setImageDrawable(mDrawable);

        mPullDownTitle = getResources().getString(R.string.st_action_bar_pull_down);
        mUpdateSubTitle = getResources().getString(R.string.st_action_bar_updating);

        /* Default is UPDATE */
        setMode(MODE_PULLDOWN);
    }

    public int setMode(int mode) {
        if (mMode == mode) {
            return mode;
        }
        if (mode == MODE_PULLDOWN || mode == MODE_UPDATING) {
            mMode = mode;
            setupMode();
            return mode;
        }
        return -1;
    }

    public int getMode() {
        return mMode;
    }

    public void setModeDisplayType(int mode, int type) {
        if (!checkMode(mode) || !checkType(type)) {
            return;
        }
        int displayType = getDisplayType(mode);
        if (displayType == type) {
            return;
        }
        if (mode == MODE_PULLDOWN) {
            mDisplayType = (mDisplayType & MASK) | (type << SHIFT);
        } else if (mode == MODE_UPDATING) {
            mDisplayType = (mDisplayType & ~displayType) | type;
        }
        if (mode == mMode) {
            setupDisplayType();
        }
    }

    public int getDisplayType(int mode) {
        if (mode == MODE_PULLDOWN) {
            return mDisplayType >> SHIFT;
        } else if (mode == MODE_UPDATING) {
            return mDisplayType & MASK;
        }
        return -1;
    }

    public void setModeText(int mode, int category, String text) {
        if (!checkMode(mode)) {
            return;
        }
        if (mode == MODE_PULLDOWN) {
            if (category == CATEGORY_TITLE) {
                mPullDownTitle = text;
            } else if (category == CATEGORY_SUBTITLE) {
                mPullDownSubTitle = text;
            }
        } else if (mode == MODE_UPDATING) {
            if (category == CATEGORY_TITLE) {
                mUpdateTitle = text;
            } else if (category == CATEGORY_SUBTITLE) {
                mUpdateSubTitle = text;
            }
        }
        if (mode == mMode) {
            if (category == CATEGORY_TITLE) {
                if (mMode == MODE_UPDATING && mSecondaryView.getVisibility() == VISIBLE) {
                    mPrimaryView.setText(text);
                } else {
                    setPrimaryText(text);
                }
            } else if (category == CATEGORY_SUBTITLE) {
                if (mSecondaryView.getVisibility() == VISIBLE || mMode == MODE_PULLDOWN) {
                    setSecondaryText(text);
                } else {
                    setPrimaryText(text);
                }
            }
        }
    }

    private void refreshText() {
        if (mMode == MODE_PULLDOWN) {
            setPrimaryText(mPullDownTitle);
            setSecondaryText(mPullDownSubTitle);
        } else if (mMode == MODE_UPDATING) {
            if (mSecondaryView.getVisibility() == VISIBLE) {
                mPrimaryView.setText(mUpdateTitle);
                setSecondaryText(mUpdateSubTitle);
            } else {
                setPrimaryText(mUpdateSubTitle);
            }
        }
    }

    private boolean checkType(int type) {
        if (type == TYPE_UPDATE_WITH_TITLE || type == TYPE_UPDATE_WITH_TITLE_DROPDOWN || type == TYPE_UPDATE_WITH_SUBTITLE || type == TYPE_UPDATE_WITH_SUBTITLE_DROPDOWN
                || type == TYPE_PULLDOWN_WITH_TITLE || type == TYPE_PULLDOWN_WITH_SUBTITLE) {
            return true;
        }
        return false;
    }

    private boolean checkMode(int mode) {
        if (mode != MODE_UPDATING && mode != MODE_PULLDOWN) {
            return false;
        }
        return true;
    }

    private void setupMode() {
        if (mMode == MODE_UPDATING) {
            mProgressView.setVisibility(VISIBLE);
            mIconView.setVisibility(GONE);
            mSecondaryView.setState(ActionBarTextView.SECONDARY_UPDATE);
        } else if (mMode == MODE_PULLDOWN) {
            mIconView.setVisibility(VISIBLE);
            mProgressView.setVisibility(GONE);
            mArrowView.setVisibility(GONE);
            mSecondaryView.setState(ActionBarTextView.SECONDARY_PULLDOWN);
        }
        setupDisplayType();
    }

    private void setupDisplayType() {
        int displayType = getDisplayType(mMode);
        if ((displayType & CATEGORY_SUBTITLE) == CATEGORY_SUBTITLE) {
            mSecondaryView.setVisibility(VISIBLE);
        } else {
            mSecondaryView.setVisibility(GONE);
        }
        if ((displayType & CATEGORY_TITLE) == CATEGORY_TITLE) {
            mPrimaryView.setVisibility(VISIBLE);
            if (mMode == MODE_UPDATING) {
                mPrimaryView.setState(View.GONE == mSecondaryView.getVisibility() ? ActionBarTextView.PRIMARY_UPDATE_ONLY : ActionBarTextView.PRIMARY_DEFAULT_TWO_TEXTVIEW);
            } else if (mMode == MODE_PULLDOWN) {
                mPrimaryView.setState(View.GONE == mSecondaryView.getVisibility() ? ActionBarTextView.PRIMARY_PULLDOWN : ActionBarTextView.PRIMARY_PULLDOWN_TWO_TEXTVIEW);
            }
        }
        if ((displayType & CATEGORY_ARROW) == CATEGORY_ARROW) {
            mArrowView.setVisibility(VISIBLE);
        } else {
            mArrowView.setVisibility(GONE);
        }
        refreshText();
    }

    public void announceForAccessibility() {
        StringBuilder stringBuilder = new StringBuilder();
        if (mMode == MODE_PULLDOWN) {
            stringBuilder.append(mPullDownTitle);
            if (mSecondaryView.getVisibility() == VISIBLE && !TextUtils.isEmpty(mPullDownSubTitle)) {
                stringBuilder.append(" " + mPullDownSubTitle);
            }
        } else if (mMode == MODE_UPDATING) {
            stringBuilder.append(mUpdateTitle);
            if (mSecondaryView.getVisibility() == VISIBLE && !TextUtils.isEmpty(mUpdateSubTitle)) {
                stringBuilder.append(" " + mUpdateSubTitle);
            }
        }
        announceForAccessibility(stringBuilder.toString());
    }

    @ExportedProperty(category = "CommonControl")
    int mNormalPrimaryHeight;
    @ExportedProperty(category = "CommonControl")
    int mNormalPrimaryBaseline;
    @ExportedProperty(category = "CommonControl")
    int mNormalSecondaryBaseline;

    private void getDefaultHeight() {
        FontMetricsInt primaryFontMetricsInt = HtcResUtil.getFontStyleMetrics(getContext(), R.style.fixed_title_primary_m);
        FontMetricsInt secondaryFontMetricsInt = HtcResUtil.getFontStyleMetrics(getContext(), R.style.fixed_title_secondary_m);
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

    /**
     * Set arrow visibility.
     */
    public void setArrowVisibility(int visibility) {
        if (mArrowView != null && mArrowView.getVisibility() != visibility) {
            mArrowView.setVisibility(visibility);
        }
    }

    /**
     * Set primary text.
     */
    public void setPrimaryText(String text) {
        if (mPrimaryView != null) {
            // runtime decide to setup the uppercase or lowercase
            // based on util result with the current language
            mPrimaryView.setText(HtcResUtil.toUpperCase(getContext(), text));
        }
    }

    /**
     * Set primary text.
     */
    public void setPrimaryText(int resId) {
        if (mPrimaryView != null) {
            String text = getContext().getResources().getString(resId);
            // runtime decide to setup the uppercase or lowercase
            // based on util result with the current language
            mPrimaryView.setText(HtcResUtil.toUpperCase(getContext(), text));
        }
    }

    /**
     * Set secondary text.
     */
    public void setSecondaryText(String text) {
        if (mSecondaryView != null) {
            // runtime decide to setup the uppercase or lowercase
            // based on util result with the current language
            mSecondaryView.setText(HtcResUtil.toUpperCase(getContext(), text));
        }
    }

    /**
     * Set secondary text.
     */
    public void setSecondaryText(int resId) {
        if (mSecondaryView != null) {
            String text = getContext().getResources().getString(resId);

            // runtime decide to setup the uppercase or lowercase
            // based on util result with the current language
            mSecondaryView.setText(HtcResUtil.toUpperCase(getContext(), text));
        }
    }

    /**
     * Set primary text visibility.
     */
    public void setPrimaryVisibility(int visibility) {
        if (mPrimaryView != null && mPrimaryView.getVisibility() != visibility) {
            mPrimaryView.setVisibility(visibility);
        }
    }

    /**
     * Set secondary text visibility.
     */
    public void setSecondaryVisibility(int visibility) {
        if (mSecondaryView != null && mSecondaryView.getVisibility() != visibility) {
            mSecondaryView.setVisibility(visibility);
        }
    }

    /**
     * Get primary text.
     */
    public CharSequence getPrimaryText() {
        return mPrimaryView != null ? mPrimaryView.getText() : null;
    }

    /**
     * Get secondary text.
     */
    public CharSequence getSecondaryText() {
        return mSecondaryView != null ? mSecondaryView.getText() : null;
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

        // progress
        int progressUsedWidth = 0;
        if (null != mProgressView && VISIBLE == mProgressView.getVisibility()) {
            mProgressView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

            mlp = (MarginLayoutParams) mProgressView.getLayoutParams();

            progressUsedWidth = ActionBarUtil.getChildUsedWidth(mProgressView.getMeasuredWidth(), mlp);
        }
        if (null != mSecondaryView && VISIBLE == mSecondaryView.getVisibility()) secondaryUsedWidth += progressUsedWidth;
        else primaryUsedWidth += progressUsedWidth;

        int iconUsedWidth = 0;
        if (null != mIconView && VISIBLE == mIconView.getVisibility()) {
            mIconView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

            mlp = (MarginLayoutParams) mIconView.getLayoutParams();

            iconUsedWidth = ActionBarUtil.getChildUsedWidth(mIconView.getMeasuredWidth(), mlp);
            primaryUsedWidth += iconUsedWidth;
            secondaryUsedWidth += iconUsedWidth;
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

            if (width - primaryUsedWidth - margin < 0) {
                LogUtil.logE("ActionBarRefresh", "primaryWidth < 0 : ",
                        "width = ", width,
                        ",primaryUsedWidth = ", primaryUsedWidth,
                        ",margin = ", margin);
            }
            ActionBarUtil.measureActionBarTextView(mPrimaryView, width, primaryUsedWidth + margin, true);
            getPrimaryBaseline();
        }

        if (null != mSecondaryView && VISIBLE == mSecondaryView.getVisibility()) {
            mlp = (MarginLayoutParams) mPrimaryView.getLayoutParams();

            int margin = ActionBarUtil.getChildUsedWidth(0, mlp);

            if (width - secondaryUsedWidth - margin < 0) {
                LogUtil.logE("ActionBarRefresh", "secondaryWidth < 0 : ",
                        "width = ", width,
                        ",secondaryUsedWidth = ", secondaryUsedWidth,
                        ",margin = ", margin);
            }
            ActionBarUtil.measureActionBarTextView(mSecondaryView, width, secondaryUsedWidth + margin, true);
            getSecondrayBaseline();
        }
    }

    /**
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
        final int width = r - l;
        final int height = b - t - currentTop - getPaddingBottom();
        int topPrimary = 0, topSecondary = 0, primaryUsed = 0, secondaryUsed = 0;
        int childWidth, childHeight, childLeft, childTop;
        MarginLayoutParams mlp;
        if (null != mIconView && VISIBLE == mIconView.getVisibility()) {
            childWidth = mIconView.getMeasuredWidth();
            childHeight = mIconView.getMeasuredHeight();
            mlp = (MarginLayoutParams) mIconView.getLayoutParams();

            childLeft = ActionBarUtil.getChildLeft(width, primaryUsed, ActionBarUtil.getStartMarginForPlatform(mlp) + currentStart, childWidth, isLayoutRtl);
            childTop = (height - childHeight) / 2 + mlp.topMargin - mlp.bottomMargin;
            childTop += currentTop;
            ActionBarUtil.setChildFrame(mIconView, childLeft, childTop, childWidth, childHeight);
            secondaryUsed += ActionBarUtil.getChildUsedWidth(childWidth, mlp);
            primaryUsed += ActionBarUtil.getChildUsedWidth(childWidth, mlp);
        } else if (null != mProgressView && VISIBLE == mProgressView.getVisibility()) {
            childWidth = mProgressView.getMeasuredWidth();
            mlp = (MarginLayoutParams) mProgressView.getLayoutParams();
            if (null != mSecondaryView && VISIBLE == mSecondaryView.getVisibility()) {
                secondaryUsed += ActionBarUtil.getChildUsedWidth(childWidth, mlp);
            } else {
                primaryUsed += ActionBarUtil.getChildUsedWidth(childWidth, mlp);
            }
        }

        if (null != mPrimaryView && VISIBLE == mPrimaryView.getVisibility()) {
            childWidth = mPrimaryView.getMeasuredWidth();
            childHeight = mPrimaryView.getMeasuredHeight();
            mlp = (MarginLayoutParams) mPrimaryView.getLayoutParams();

            childLeft = ActionBarUtil.getChildLeft(width, primaryUsed, ActionBarUtil.getStartMarginForPlatform(mlp) + currentStart, childWidth, isLayoutRtl);
            if (null != mSecondaryView && VISIBLE == mSecondaryView.getVisibility()) {
                if (isDefaultFontEnabled()) {
                    childTop = (int) (((float) mNormalPrimaryHeight) * 0.14f + 0.5f) + mNormalPrimaryBaseline - getPrimaryBaseline();
                } else {
                    childTop = (int) (((float) childHeight) * 0.14f + 0.5f);
                }
                topPrimary = childTop += currentTop;
                childTop += mlp.topMargin;
            } else {
                childTop = (height - childHeight) / 2;
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
                childTop = (int) (((float) mNormalPrimaryHeight) * 1.04f + 0.5f) + mNormalSecondaryBaseline - getSecondrayBaseline();
            } else {
                childTop = (int) (((float) mPrimaryView.getMeasuredHeight()) * 1.04f + 0.5f);
            }

            topSecondary = childTop += currentTop;
            childTop += mlp.topMargin;

            childLeft = ActionBarUtil.getChildLeft(width, secondaryUsed, ActionBarUtil.getStartMarginForPlatform(mlp) + currentStart, childWidth, isLayoutRtl);

            ActionBarUtil.setChildFrame(mSecondaryView, childLeft, childTop, childWidth, childHeight);
            secondaryUsed += ActionBarUtil.getChildUsedWidth(childWidth, mlp);
        }

        if (null != mArrowView && VISIBLE == mArrowView.getVisibility()) {
            childWidth = mArrowView.getMeasuredWidth();
            childHeight = mArrowView.getMeasuredHeight();
            mlp = (MarginLayoutParams) mArrowView.getLayoutParams();
            childTop = topPrimary + getPrimaryBaseline() - ((HtcResUtil.getHeightOfChar(mPrimaryView, "e") + childHeight) / 2);
            childTop += mlp.topMargin;

            childLeft = ActionBarUtil.getChildLeft(width, primaryUsed, ActionBarUtil.getStartMarginForPlatform(mlp) + currentStart, childWidth, isLayoutRtl);
            ActionBarUtil.setChildFrame(mArrowView, childLeft, childTop, childWidth, childHeight);
        }
        if (null != mIconView && VISIBLE == mIconView.getVisibility()) return;
        if (null != mProgressView && VISIBLE == mProgressView.getVisibility()) {
            childWidth = mProgressView.getMeasuredWidth();
            childHeight = mProgressView.getMeasuredHeight();
            mlp = (MarginLayoutParams) mProgressView.getLayoutParams();

            childLeft = ActionBarUtil.getChildLeft(width, 0, ActionBarUtil.getStartMarginForPlatform(mlp) + currentStart, childWidth, isLayoutRtl);

            if (null != mSecondaryView && VISIBLE == mSecondaryView.getVisibility()) {
                childTop = topSecondary + getSecondrayBaseline() - ((HtcResUtil.getHeightOfChar(mSecondaryView, "U") + childHeight) / 2);
            } else {
                childTop = topPrimary + getPrimaryBaseline() - ((HtcResUtil.getHeightOfChar(mPrimaryView, "U") + childHeight) / 2);
            }
            childTop += mlp.topMargin;
            ActionBarUtil.setChildFrame(mProgressView, childLeft, childTop, childWidth, childHeight);
        }

    }

    /*
     * (non-Javadoc)
     * @see android.view.ViewGroup#generateDefaultLayoutParams()
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    /*
     * (non-Javadoc)
     * @see android.view.ViewGroup#generateLayoutParams(android.util.AttributeSet)
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /*
     * (non-Javadoc)
     * @see android.view.ViewGroup#generateLayoutParams(android.view.ViewGroup. LayoutParams)
     */
    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    /*
     * (non-Javadoc)
     * @see android.view.ViewGroup#checkLayoutParams(android.view.ViewGroup.LayoutParams )
     */
    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }
}
