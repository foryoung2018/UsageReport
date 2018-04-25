
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;

/**
 * The HTC style of the expandable list view.
 */
public class HtcExpandableListView extends ExpandableListView implements Refreshable,
        IHtcAbsListView {
    ListViewUtil mListViewUtil;

    @ExportedProperty(category = "CommonControl")
    int mExpandDividerHeight = 6;

    @ExportedProperty(category = "CommonControl")
    int mDividerMargin = 0;

    @ExportedProperty(category = "CommonControl")
    int mCurrentExpandedGroup = -1;

    @ExportedProperty(category = "CommonControl")
    boolean mDarkModeEnabled = false;

    @ExportedProperty(category = "CommonControl")
    boolean mIndicatorEnabled = true;

    Drawable mExpandDivider, mDarkExpandDivider;

    public HtcExpandableListView(Context context) {
        this(context, null);
    }

    public HtcExpandableListView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.expandableListViewStyle);
    }

    public HtcExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        if (mListViewUtil == null) {
            mListViewUtil = new ListViewUtil();
        }
        mListViewUtil.init(this, new ListViewUtil.Callbacks() {
            @Override
            public boolean superOnInterceptTouchEvent(MotionEvent ev) {
                return HtcExpandableListView.super.onInterceptTouchEvent(ev);
            }

            @Override
            public boolean superOnTouchEvent(MotionEvent ev) {
                return HtcExpandableListView.super.onTouchEvent(ev);
            }

            @Override
            public void superOnOverScrolled(int scrollX, int scrollY, boolean clampedX,
                    boolean clampedY) {
                HtcExpandableListView.super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
            }

            @Override
            public void superOnDetachedFromWindow() {
                HtcExpandableListView.super.onDetachedFromWindow();
            }

            @Override
            public void superOnWindowFocusChanged(boolean hasWindowFocus) {
                HtcExpandableListView.super.onWindowFocusChanged(hasWindowFocus);
            }

            @Override
            public void superInvalidate() {
                HtcExpandableListView.super.invalidate();
            }

            @Override
            public void superDispatchDraw(Canvas canvas) {
                HtcExpandableListView.super.dispatchDraw(canvas);
            }

            @Override
            public void superRequestLayout() {
                HtcExpandableListView.super.requestLayout();
            }
        });

        mDividerMargin = mListViewUtil.getDividerMargin();
        Resources res = context.getResources();
        mExpandDividerHeight = res.getDimensionPixelOffset(R.dimen.expand_divider_height);
        mExpandDivider = new ColorDrawable(res.getColor(R.color.dark_ap_background_color));
        mDarkExpandDivider = new ColorDrawable(res.getColor(R.color.ap_background_color));

        // Remove google's group indicator
        setGroupIndicator(null);
    }

    public void setDarkModeEnabled(boolean enabled) {
        mDarkModeEnabled = enabled;
    }

    @Override
    public void setOnPullDownListener(OnPullDownListener onPullDownListener) {
        mListViewUtil.setOnPullDownListener(onPullDownListener);
    }

    public void setOnScrollListener(OnScrollListener l) {
        if(mListViewUtil.isUserOnScrollListener(l))
            mListViewUtil.setOnScrollListener(l);
        else
            super.setOnScrollListener(l);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean handled = mListViewUtil.onInterceptTouchEvent(event);
        return handled | super.onInterceptTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return mListViewUtil.onTouchEvent(ev);
    }

    public void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        mListViewUtil.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    public void onDetachedFromWindow() {
        mListViewUtil.onDetachedFromWindow();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        mListViewUtil.onWindowFocusChanged(hasWindowFocus);
    }

    /**
     * Set a divider controller used to indicate how to draw the divider between
     * list items.
     *
     * @param dividerController The divider controller.
     */
    public void setDividerController(IDividerController dividerController) {
        mListViewUtil.setDividerController(dividerController);
    }

    public void setHeaderDividersEnabled(boolean headerDividersEnabled) {
        super.setHeaderDividersEnabled(headerDividersEnabled);
        if (mListViewUtil == null) {
            mListViewUtil = new ListViewUtil();
        }
        mListViewUtil.setHeaderDividersEnabled(headerDividersEnabled);
    }

    public void setFooterDividersEnabled(boolean footerDividersEnabled) {
        super.setFooterDividersEnabled(footerDividersEnabled);
        if (mListViewUtil == null) {
            mListViewUtil = new ListViewUtil();
        }
        mListViewUtil.setFooterDividersEnabled(footerDividersEnabled);
    }

    public void setOverscrollHeader(Drawable header) {
        super.setOverscrollHeader(header);
        if (mListViewUtil == null) {
            mListViewUtil = new ListViewUtil();
        }
        mListViewUtil.setOverscrollHeader(header);
    }

    public void setOverscrollFooter(Drawable footer) {
        super.setOverscrollHeader(footer);
        if (mListViewUtil == null) {
            mListViewUtil = new ListViewUtil();
        }
        mListViewUtil.setOverscrollFooter(footer);
    }

    void drawDivider(Canvas canvas, Rect bounds, int childIndex) {
        mListViewUtil.drawDivider(canvas, bounds, childIndex);
    }

    public void setClipToPadding(boolean clipToPadding) {
        super.setClipToPadding(clipToPadding);
        mListViewUtil.setClipToPadding(clipToPadding);
    }

    @Override
    public void invalidate() {
        if (mListViewUtil == null) {
            mListViewUtil = new ListViewUtil();
        }
        mListViewUtil.invalidate();
    }

    @Override
    public void requestLayout() {
        if (mListViewUtil == null) {
            mListViewUtil = new ListViewUtil();
        }
        mListViewUtil.requestLayout();
    }

    protected void dispatchDraw(Canvas canvas) {
        if(mIndicatorEnabled) drawIndicator(canvas);

        mListViewUtil.dispatchDraw(canvas);

        int firstPosition = getFirstVisiblePosition();
        int dividerHeight = getDividerHeight();
        int b;
        View item;

        int lastExpandDividerDrawnIndex = -2;
        int expandDividerBottom = (mExpandDividerHeight - dividerHeight)/2;
        // Draw the bold black/white divider when a group is expanded.
        for (int i = 0; i < getChildCount(); i++) {
            int flatPosition = firstPosition + i;
            long packedPosition = getExpandableListPosition(flatPosition);
            int groupPosition = getPackedPositionGroup(packedPosition);
            int itemType = getPackedPositionType(packedPosition);

            View nextItem = null;
            Drawable divider = mDarkModeEnabled ? mDarkExpandDivider : mExpandDivider;
            boolean isExpandedGroup = itemType == PACKED_POSITION_TYPE_GROUP
                    && isGroupExpanded(groupPosition);
            if (isExpandedGroup && i-1 != lastExpandDividerDrawnIndex) {
                item = getChildAt(i);
                nextItem = getChildAt(i - 1);
                int itemTranslationY = (int) item.getTranslationY();
                int direction = itemTranslationY >= 0 ? -1 : 1;
                int offset = nextItem == null ? itemTranslationY : itemTranslationY + direction
                        * (int) ((direction * (nextItem.getTranslationY() - itemTranslationY)) / 2);

                b = item.getTop() + (flatPosition > 0 ? expandDividerBottom : mExpandDividerHeight)
                        + (flatPosition == 0 && itemTranslationY >= 0 ? 0 : offset);
                divider.setBounds(mDividerMargin, b - mExpandDividerHeight, getWidth() - mDividerMargin, b);
                divider.draw(canvas);
            }

            if (itemType == PACKED_POSITION_TYPE_CHILD && isDrawExpandBottomDivider(flatPosition)
                    || isExpandedGroup && isDrawExpandBottomDivider(flatPosition)) {
                item = getChildAt(i);
                b = item.getBottom() + (flatPosition < getCount()-1 ? dividerHeight + expandDividerBottom : 0);
                divider.setBounds(mDividerMargin, b - mExpandDividerHeight, getWidth()
                        - mDividerMargin, b);
                divider.draw(canvas);
                lastExpandDividerDrawnIndex = i;
            }
        }
    }

    boolean isDrawExpandBottomDivider(int flatPosition) {
        int nextItemflatPosition = flatPosition + 1;
        ListAdapter adapter = getAdapter();
        if(adapter == null || adapter!=null && nextItemflatPosition > adapter.getCount() - 1){
            return true;
        }

        long nextItemPackedPosition = getExpandableListPosition(nextItemflatPosition);
        int nextItemType = getPackedPositionType(nextItemPackedPosition);
        return nextItemType != PACKED_POSITION_TYPE_CHILD;
    }

    /**
     * Set whether the indicator is controlled by HtcExpandableListView.
     * In general, if use {@link HtcIndicatorButton} as the indicator, HtcExpandableListView
     * will control the expanded/collapsed state of the indicators.
     * If you does not use {@link HtcIndicatorButton} as the indicator (Maybe add your own indicator
     * to list item (ex: Mail)), you can invoke setIndicatorEnabled(false)
     * to prevent some unnecessary method calls.
     * @param enabled Whether the indicator is controlled by HtcExpandableListView.
     */
    public void setIndicatorEnabled(boolean enabled){
        mIndicatorEnabled = enabled;
    }

    private void drawIndicator(Canvas canvas) {
        ListAdapter adapter = getAdapter();
        if (adapter == null) {
            return;
        }
        final int headerViewsCount = getHeaderViewsCount();
        final int lastChildFlPos = adapter.getCount() - getFooterViewsCount() - headerViewsCount
                - 1;
        final int myB = getBottom();
        final int childCount = getChildCount();
        int firstPosition = getFirstVisiblePosition();
        View item;
        int t, b;
        for (int i = 0, childFlPos = firstPosition - headerViewsCount; i < childCount; i++, childFlPos++) {
            if (childFlPos < 0) {
                // This child is header
                continue;
            } else if (childFlPos > lastChildFlPos) {
                // This child is footer, so are all subsequent children
                break;
            }
            item = getChildAt(i);
            if (item == null)
                continue;
            t = item.getTop();
            b = item.getBottom();
            // This item isn't on the screen
            if ((b < 0) || (t > myB))
                continue;

            int flatPosition = firstPosition + i;
            long packedPosition = getExpandableListPosition(flatPosition);
            int groupPosition = getPackedPositionGroup(packedPosition);
            int itemType = getPackedPositionType(packedPosition);
            // Get more expandable list-related info for this item
            HtcIndicatorButton indicatorButton = null;
            if (itemType == PACKED_POSITION_TYPE_GROUP && item instanceof ViewGroup) {
                if (isGroupExpanded(groupPosition)) {
                    View indicatorView = ((ViewGroup) item)
                            .findViewById(R.id.htc_expandable_indicator);
                    if (indicatorView != null && indicatorView instanceof HtcIndicatorButton) {
                        indicatorButton = (HtcIndicatorButton) indicatorView;
                        indicatorButton.setExpanded(true);
                    }
                } else {
                    View indicatorView = ((ViewGroup) item)
                            .findViewById(R.id.htc_expandable_indicator);
                    if (indicatorView != null && indicatorView instanceof HtcIndicatorButton) {
                        indicatorButton = (HtcIndicatorButton) indicatorView;
                        indicatorButton.setExpanded(false);
                    }
                }
            }
        }
    }

    @Override
    public void enableAnimation(int animationType, boolean enable) {
        mListViewUtil.enableAnimation(animationType, enable);
    }

    @Override
    public void setFastScrollEnabled(boolean enabled) {
        super.setFastScrollEnabled(enabled);
        if (mListViewUtil == null) {
            mListViewUtil = new ListViewUtil();
        }
        mListViewUtil.setFastScrollEnabled(enabled);
    }

    @Override
    public void setVerticalScrollbarPosition(int position) {
        super.setVerticalScrollbarPosition(position);
        mListViewUtil.setScrollbarPosition(position);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mListViewUtil.onSizeChanged(w, h, oldw, oldh);
    }
}
