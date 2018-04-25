/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.htc.lib1.cc.view.tabbar;

import android.content.Context;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.WindowUtil;
import com.htc.lib1.cc.view.viewpager.HtcPagerAdapter;
import com.htc.lib1.cc.view.viewpager.HtcViewPager;

/**
 * To be used with ViewPager to provide a tab indicator component which give constant feedback as to
 * the user's scroll progress.
 * <p>
 * To use the component, simply add it to your view hierarchy. Then in your
 * {@link android.app.Activity} or {@link android.support.v4.app.Fragment} call
 * {@link #setViewPager(ViewPager)} providing it the ViewPager this layout is being used for.
 * <p>
 * The colors can be customized in two ways. The first and simplest is to provide an array of colors
 * via {@link #setSelectedIndicatorColors(int...)} and {@link #setDividerColors(int...)}. The
 * alternative is via the {@link TabColorizer} interface which provides you complete control over
 * which color is used for any individual position.
 * <p>
 * The views used as tabs can be customized by calling {@link #setCustomTabView(int, int)},
 * providing the layout ID of your custom layout.
 */
public class TabBar extends HorizontalScrollView implements HtcViewPager.Decor{

    /**
     * Allows complete control over the colors drawn in the tab layout. Set with
     * {@link #setCustomTabColorizer(TabColorizer)}.
     */
    public interface TabColorizer {
        /**
         * @return return the color of the indicator used when {@code position} is selected.
         */
        int getIndicatorColor(int position);

    }
    private static final String TAG = "TabBar";
    private static final int TITLE_OFFSET_DIPS = 24;
    private static final int TEXTCOLOR_ALPHA_MASK = 0xA6FFFFFF;

    @ExportedProperty(category = "CommonControl")
    private int mTitleOffset;
    @ExportedProperty(category = "CommonControl")
    private int mSelectedIndicatorThickness;

    private HtcViewPager mViewPager;
    private PageListener mPageListener;

    private final TabBarStrip mTabStrip;
    private OnLongClickListener mOnLongClickListener = null;
    private TabAdapter mAdapter;
    private int mScreenHeightDp;
    private int mScreenWidthtDp;
    private Drawable mBackgroundDrawable;
    @ExportedProperty(category = "CommonControl")
    private boolean isAutomotive = false;

    @ExportedProperty(category = "CommonControl")
    private int mUserBarHeight = -1;

    public TabBar(Context context) {
        this(context, null);
    }

    public TabBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false);
        // Make sure that the Tab Strips fills this View
        setFillViewport(true);

        mTitleOffset = (int) (TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);

        mTabStrip = new TabBarStrip(context);
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, getBarHeight(context));
        setLayoutParams(params);
        mScreenHeightDp = getContext().getResources().getConfiguration().screenHeightDp;
        mScreenWidthtDp = getContext().getResources().getConfiguration().screenWidthDp;
    }

    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    public void linkWithParent(ViewParent parent) {
        if (parent instanceof HtcViewPager) {
            HtcViewPager viewPager = (HtcViewPager) parent;
            mViewPager = viewPager;
            if (viewPager != null) {
                setAdapter(viewPager.getAdapter());
                mPageListener = (mPageListener == null) ? new PageListener(): mPageListener;
                viewPager.setOnAdapterChangeListener(mPageListener);
                viewPager.setInternalPageChangeListener(mPageListener);
            }
        } else {
            throw new IllegalArgumentException("Only support two type as virtual parent: HtcViewPager and CarouselHost");
        }
    }

    /**
     * Create a default view to be used for tabs. This is called if a custom tab view is not set via
     * {@link #setCustomTabView(int, int)}.
     */
    private TextView createDefaultTabView(Context context) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setAllCaps(true);
        int padding = TabBarUtils.dimen.m1(context);
        textView.setPadding(padding, 0, padding, 0);
        textView.setTextAppearance(context, isAutomotive ? R.style.fixed_automotive_b_separator_primary_s : R.style.fixed_b_separator_secondary_xs);
        textView.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        return textView;
    }

    /**
     * @hide
     * @deprecated [Module internal use]
     */
    public void populateTabStrip() {
        final View.OnClickListener tabClickListener = new TabClickListener();

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View tabView = null;
            TextView tabTitleView = null;

            if (tabView == null) {
                tabView = createDefaultTabView(getContext());
            }

            if (tabTitleView == null && TextView.class.isInstance(tabView)) {
                tabTitleView = (TextView) tabView;
            }

            tabTitleView.setText(mAdapter.getPageTitle(i));
            tabView.setOnClickListener(tabClickListener);
            tabView.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if(mOnLongClickListener != null)
                        return mOnLongClickListener.onLongClick(TabBar.this);
                    return false;
                }
            });
            if(mAdapter.isCNMode()){
                Log.d(TAG, "CN mode: set textview width");
                mTabStrip.addView(tabView, new LinearLayout.LayoutParams(
                    0,LayoutParams.MATCH_PARENT,1.0f));
            }else{
                mTabStrip.addView(tabView, new LayoutParams(
                    LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT));
            }
        }
        mTabStrip.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mOnLongClickListener != null)
                    return mOnLongClickListener.onLongClick(TabBar.this);
                return false;
            }
        });
        tuningUIForOrientation();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if ((mScreenHeightDp == newConfig.screenHeightDp) && (mScreenWidthtDp == newConfig.screenWidthDp)) return;
        mScreenHeightDp = newConfig.screenHeightDp;
        mScreenWidthtDp = newConfig.screenWidthDp;
        tuningUIForOrientation();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        scrollToTab(mViewPager.getCurrentItem(),0);
    }

    @Override
    @Deprecated
    public void setBackgroundDrawable(Drawable background) {
        if (Log.isLoggable(TAG, Log.DEBUG)){
            Log.d(TAG, "setBackground from AP side background = "+background);
        }
        mBackgroundDrawable = background;
        setupBackground();
    }

    private void setupBackground() {
        if (isScreenPortriat() || mUserBarHeight > 0 || isAutomotive) {
            super.setBackgroundDrawable(mBackgroundDrawable != null ? mBackgroundDrawable : new ColorDrawable(TabBarUtils.color.backgroundColor(getContext(), isAutomotive)));
        } else {
            super.setBackgroundDrawable(new ColorDrawable(TabBarUtils.color.landscapeBackground(getContext())));
        }
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = mTabStrip.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        View selectedChild = mTabStrip.getChildAt(tabIndex);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft() + positionOffset;

            if (tabIndex > 0 || positionOffset > 0) {
                // If we're not at the first child and are mid-scroll, make sure we obey the offset
                targetScrollX -= mTitleOffset;
            }

            scrollTo(targetScrollX, 0);
        }
    }

    @ExportedProperty(category = "CommonControl")
    private boolean isScreenPortriat(){
        return !WindowUtil.isSuitableForLandscape(getResources());
    }

    private void tuningUIForOrientation(){
        setupBackground();

        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) getLayoutParams();
        if (params != null) {
            params.height = getBarHeight();
            setLayoutParams(params);
        }
        mSelectedIndicatorThickness = TabBarUtils.dimen.indicatorThickness(getContext(),isAutomotive);
        tintTabTextColor();
    }

    private void tintTabTextColor(){
        int curTextColor = (isScreenPortriat() || mUserBarHeight > 0 || isAutomotive) ?
                TabBarUtils.color.portriatTextColor(getContext()) : TabBarUtils.color.landscapeTextColor(getContext());
        int otherTextColor = curTextColor & TEXTCOLOR_ALPHA_MASK;

        if(mTabStrip != null){
            for(int i = 0;i < mTabStrip.getChildCount(); i++){
                TextView tv = (TextView)mTabStrip.getChildAt(i);
                tv.setTextColor((i == mViewPager.getCurrentItem()) ? curTextColor : otherTextColor);
                Drawable selector = (isScreenPortriat() || isAutomotive) ?
                        TabBarUtils.drawable.darkTextSeletor(getContext()) : TabBarUtils.drawable.lightTextSeletor(getContext());
                tv.setBackground(selector);
            }
        }
    }
    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
            boolean clampedY) {
        //disable scroll in China Sense
        if(mAdapter != null && mAdapter.isCNMode()){
            return;
        }else{
            super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        }
    }
    /**
     * @hide
     * @deprecated [Module internal use]
     */
    public class PageListener extends DataSetObserver implements HtcViewPager.OnPageChangeListener, HtcViewPager.OnAdapterChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = mTabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }

            mTabStrip.onViewPagerPageChanged(position, positionOffset);

            View selectedTitle = mTabStrip.getChildAt(position);
            int extraOffset = (selectedTitle != null)
                    ? (int) (positionOffset * selectedTitle.getWidth())
                    : 0;
            scrollToTab(position, extraOffset);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == HtcViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }

            tintTabTextColor();
        }

        @Override
        public void onAdapterChanged(HtcPagerAdapter oldAdapter,
                HtcPagerAdapter newAdapter) {
            setAdapter(newAdapter);
        }
        @Override
        public void onChanged() {

            if(mTabStrip != null && mAdapter != null){
                mTabStrip.removeAllViews();
                populateTabStrip();
                mTabStrip.onViewPagerPageChanged(mViewPager.getCurrentItem(), 0);
                mTabStrip.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        scrollToTab(mViewPager.getCurrentItem(), 0);
                        mTabStrip.getViewTreeObserver().removeOnPreDrawListener(this);
                        return false;
                    }
                });
            }
        }

    }

    private class TabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                if (v == mTabStrip.getChildAt(i)) {
                    if(mViewPager != null)
                        mViewPager.setCurrentItem(i);
                    else
                        mTabStrip.onViewPagerPageChanged(i, 0f);
                    return;
                }
            }
        }
    }

    /**
     * Use for tab reorder
     * @param l OnLongClickListener
     * @hide
     */
    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        mOnLongClickListener = l;
    }

    /**
     * Adapter for tab bar
     */
    public interface TabAdapter extends PageTitleStrategy {
        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        int getCount();

        /**
         * Register an observer that is called when changes happen to the data used by this adapter.
         *
         * @param observer the object that gets notified when the data set changes.
         */
        void registerDataSetObserver(DataSetObserver observer);

        /**
         * Unregister an observer that has previously been registered with this
         * adapter via {@link #registerDataSetObserver}.
         *
         * @param observer the object to unregister.
         */
        void unregisterDataSetObserver(DataSetObserver observer);
    }

    boolean isAdapterSame(TabAdapter adapter) {
        return mAdapter == adapter;
    }

    public void setAdapter(TabAdapter adapter) {
        //instantiate new mAdapter if the adapter is changed
        if (mAdapter == null || !isAdapterSame(adapter)) {
            if(mViewPager != null){
                //if the adapter is inconsistent between TabBar and HtcViewPager, print warning message
                if (adapter != ((HtcViewPager)mViewPager).getAdapter()) {
                    Log.w(TAG, "Please DO NOT set adapter directly if the parent is a view pager", new Throwable());
                }
                TabAdapter oldAdapter = mAdapter;
                if (oldAdapter != null && mPageListener != null) {
                    oldAdapter.unregisterDataSetObserver(mPageListener);
                }
                mAdapter = adapter;
                if(adapter != null){
                    mPageListener = (mPageListener == null) ? new PageListener(): mPageListener;
                    adapter.registerDataSetObserver(mPageListener);
                    isAutomotive = adapter.isAutomotiveMode();
                }
            }
            if(mTabStrip != null && mAdapter != null){
                mTabStrip.removeAllViews();
                populateTabStrip();
            }
        }
    }

    /**
     * Set the height for Tabbar
     * @param height the height of Tabbar, if height = -1, use the default height.
     */
    public void setBarHeight(int height) {
        if (Log.isLoggable(TAG, Log.DEBUG)){
            Log.d(TAG, "Set bar height = " + height);
        }
        if ((mUserBarHeight == height) || (height < -1))
            return;

        mUserBarHeight = height;
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) getLayoutParams();
        if (params != null){
            params.height = getBarHeight();
            setLayoutParams(params);
        }
    }

    /**
     * Return the current height of Tabbar
     * @return the current height of Tabbar
     */
    @ExportedProperty(category = "CommonControl")
    public int getBarHeight() {
        if (mUserBarHeight != -1)
            return mUserBarHeight;
        return TabBarUtils.dimen.height(getContext(), isAutomotive);
    }

    /**
     * Get the height of tab bar that defined by UI guideline
     * @param ctx the associated context
     * @return the height of tab bar
     */
    public static int getBarHeight(Context ctx) {
        return TabBarUtils.dimen.height(ctx, false);
    }
    /**
     * @hide
     * @deprecated [Module internal use]
     */
    public class TabBarStrip extends LinearLayout {

        private final Paint mSelectedIndicatorPaint;
        @ExportedProperty(category = "CommonControl")
        private int mSelectedPosition;
        @ExportedProperty(category = "CommonControl")
        private float mSelectionOffset;
        private final SimpleTabColorizer mDefaultTabColorizer;

        TabBarStrip(Context context) {
            this(context, null);
        }

        TabBarStrip(Context context, AttributeSet attrs) {
            super(context, attrs);
            setWillNotDraw(false);

            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.colorForeground, outValue, true);

            mDefaultTabColorizer = new SimpleTabColorizer();
            mDefaultTabColorizer.setIndicatorColors(TabBarUtils.color.categoryLight(context));
            mSelectedIndicatorThickness = TabBarUtils.dimen.indicatorThickness(context, isAutomotive);
            mSelectedIndicatorPaint = new Paint();
        }


        void onViewPagerPageChanged(int position, float positionOffset) {
            mSelectedPosition = position;
            mSelectionOffset = positionOffset;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            final int height = getHeight();
            final int childCount = getChildCount();
//            final int dividerHeightPx = (int) (Math.min(Math.max(0f, mDividerHeight), 1f) * height);
            final TabBar.TabColorizer tabColorizer = mDefaultTabColorizer;

            // Thick colored underline below the current selection
            if (childCount > 0) {
                View selectedTitle = getChildAt(mSelectedPosition);
                int left = selectedTitle.getLeft();
                int right = selectedTitle.getRight();
                int color = tabColorizer.getIndicatorColor(mSelectedPosition);

                if (mSelectionOffset > 0f && mSelectedPosition < (getChildCount() - 1)) {

                    // Draw the selection partway between the tabs
                    View nextTitle = getChildAt(mSelectedPosition + 1);
                    left = (int) (mSelectionOffset * nextTitle.getLeft() +
                            (1.0f - mSelectionOffset) * left);
                    right = (int) (mSelectionOffset * nextTitle.getRight() +
                            (1.0f - mSelectionOffset) * right);
                }

                mSelectedIndicatorPaint.setColor(color);

                canvas.drawRect(left, height - mSelectedIndicatorThickness, right,
                        height, mSelectedIndicatorPaint);
            }
        }
    }
    private static class SimpleTabColorizer implements TabBar.TabColorizer {
        private int[] mIndicatorColors;

        @Override
        public final int getIndicatorColor(int position) {
            return mIndicatorColors[position % mIndicatorColors.length];
        }

        void setIndicatorColors(int... colors) {
            mIndicatorColors = colors;
        }
    }
}

