
package com.htc.lib1.cc.widget;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.htc.lib1.cc.util.CheckUtil;

/**
 * The HTC style of the list view with over-scroll and delete animation.
 */
public class HtcListView extends ListView implements Refreshable, IHtcAbsListView {

    @ExportedProperty(category = "CommonControl")
    int mDividerMargin = 0;

    ListViewUtil mListViewUtil;

    @ExportedProperty(category = "CommonControl")
    private boolean mAnimRunning;

    @ExportedProperty(category = "CommonControl")
    private boolean mDisableTouchEvent;

    private DeleteAnimationListener mDeleteAnimationListener;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsDisableAnimation;

    @ExportedProperty(category = "CommonControl")
    private boolean mInDeleteAnimation;

    private int mOriFirstPosition;

    private boolean mOriLastPage;

    private int mOriUpperDeleteCount;

    private int mOriCurDeleteCount;

    private int mOriCurLeftCount;

    private int mOriBelowLeftCount;

    /**
     * The decelerate interpolator for add and delete animation
     */
    private DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator(1.2f);

    /**
     * The decelerate interpolator of duration for add and delete animation
     */
    private DecelerateInterpolator mDurationInterpolator = new DecelerateInterpolator(1.0f);

    private ArrayList<ObjectAnimator> mAnimatorList = new ArrayList<ObjectAnimator>();

    // Start: For deleted Animation
    AnimatorSet mDelAniSet = null;

    @ExportedProperty(category = "CommonControl")
    private boolean mEndDelAniEarly = false;

    private ArrayList<Integer> mDelPosList = null;

    private ArrayList<Integer> mDelOriViewTopList = null;

    private ArrayList<View> mDelViewList = null;

    private ArrayList<View> mNowViewList = null;

    // End: For deleted Animation
    @ExportedProperty(category = "CommonControl")
    private boolean mDelAnimationFlag;

    /**
     * The callbacks to be invoked when delete animation start or end.
     */
    public interface DeleteAnimationListener {
        /**
         * The method is invoked when delete animation start.
         */
        public void onAnimationStart();

        /**
         * The method is invoked when delete animation end.
         */
        public void onAnimationEnd();

        public void onAnimationUpdate();
    }

    public HtcListView(Context context) {
        this(context, null);
    }

    public HtcListView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.listViewStyle);
    }

    public HtcListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        if(mListViewUtil == null) {
            mListViewUtil = new ListViewUtil();
        }

        mListViewUtil.init(this, new ListViewUtil.Callbacks() {
            @Override
            public boolean superOnInterceptTouchEvent(MotionEvent ev) {
                return HtcListView.super.onInterceptTouchEvent(ev);
            }

            @Override
            public boolean superOnTouchEvent(MotionEvent ev) {
                return HtcListView.super.onTouchEvent(ev);
            }

            @Override
            public void superOnOverScrolled(int scrollX, int scrollY, boolean clampedX,
                    boolean clampedY) {
                HtcListView.super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
            }

            @Override
            public void superOnDetachedFromWindow() {
                HtcListView.super.onDetachedFromWindow();
            }

            @Override
            public void superOnWindowFocusChanged(boolean hasWindowFocus) {
                HtcListView.super.onWindowFocusChanged(hasWindowFocus);
            }

            @Override
            public void superDispatchDraw(Canvas canvas) {
                HtcListView.super.dispatchDraw(canvas);
            }

            @Override
            public void superInvalidate() {
                HtcListView.super.invalidate();
            }

            @Override
            public void superRequestLayout() {
                HtcListView.super.requestLayout();
            }
        });

        mDividerMargin = mListViewUtil.getDividerMargin();
    }

    /**
     * Set the listener that will receive notifications every time the list or
     * grid be pulled down.
     *
     * @param onPullDownListener the pull-down listener
     */
    public void setOnPullDownListener(OnPullDownListener onPullDownListener) {
        mListViewUtil.setOnPullDownListener(onPullDownListener);
    }

    public void setOnScrollListener(OnScrollListener l) {
        if(mListViewUtil.isUserOnScrollListener(l))
            mListViewUtil.setOnScrollListener(l);
        else
            super.setOnScrollListener(l);
    }

    public boolean isOverScrolledAnimationEnabled() {
        return mListViewUtil.isOverScrolledAnimationEnabled();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean handled = mListViewUtil.onInterceptTouchEvent(event);
        return handled | super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        /**
         * Drop the touch event during the intro animation
         */
        if (mAnimRunning || mDisableTouchEvent || mInDeleteAnimation)
            return true;
        else
            return super.dispatchTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return mListViewUtil.onTouchEvent(ev);
    }

    @Override
    public void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        mListViewUtil.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    protected void onDetachedFromWindow() {
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

    protected void dispatchDraw(Canvas canvas) {
        // Update start translationY before draw divider
        // Start: For Del Animation
        if (mDelAnimationFlag == true) {
            mDelAnimationFlag = false;
            startDelDropAnimation();
        }
        mListViewUtil.dispatchDraw(canvas);
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

    /**
     * Register the delete animation listener
     *
     * @param listener the DeleteAnimationListener
     */
    public void setDeleteAnimationListener(DeleteAnimationListener listener) {
        mDeleteAnimationListener = listener;
    }

    AnimatorUpdateListener mAnimatorUpdateListener = new AnimatorUpdateListener(){
        public void onAnimationUpdate(ValueAnimator animation) {
            //invalidate the whole list view region during delete animation.
            invalidate();
        }
    };

    public void setDelPositionsList(ArrayList<Integer> d) {
        if (d == null) {
            mDisableTouchEvent = false;
            throw new java.security.InvalidParameterException("The input parameter d is null!");
        }
        if (mAnimRunning == true) {
            mDisableTouchEvent = false;
            return;
        }
        if ((mListViewUtil.isDelAnimationEnabled() == false) || (mIsDisableAnimation == true)) {
            if (mDeleteAnimationListener != null) {
                mDeleteAnimationListener.onAnimationUpdate();
                mDeleteAnimationListener.onAnimationStart();
                mDeleteAnimationListener.onAnimationEnd();
            }
            mDisableTouchEvent = false;
            return;
        }

        int listLength = d.size();
        if (listLength == 0) {
            mDisableTouchEvent = false;
            return;
        }
        // If the CheckForTap runnable is handled after the delete animation is triggered,
        // the layoutChildren() call in CheckForTap would cause illegalStateException
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            cancelPendingInputEvents();
        }
        mAnimRunning = true;
        if (mDeleteAnimationListener != null) {
            mDeleteAnimationListener.onAnimationStart();
        }

        mInDeleteAnimation = true;

        mOriFirstPosition = getFirstVisiblePosition();
        int childCount = getChildCount();

        if (mOriFirstPosition + childCount == getAdapter().getCount() + listLength) {
            mOriLastPage = true;
        } else {
            mOriLastPage = false;
        }

        mOriUpperDeleteCount = 0;
        mOriCurDeleteCount = 0;

        mOriCurLeftCount = 0;
        mOriBelowLeftCount = 0;

        if (mDelPosList == null)
            mDelPosList = new ArrayList<Integer>();
        else
            mDelPosList.clear();

        if (mDelOriViewTopList == null)
            mDelOriViewTopList = new ArrayList<Integer>();
        else
            mDelOriViewTopList.clear();

        if (mDelViewList == null)
            mDelViewList = new ArrayList<View>();
        else
            mDelViewList.clear();

        int delPos = 0;
        int belowDeleteCount = 0;
        for (int i = 0; i < listLength; ++i) {
            delPos = d.get(i);
            if (delPos < mOriFirstPosition) {
                mOriUpperDeleteCount++;
            } else if (delPos < mOriFirstPosition + childCount) {
                mDelPosList.add(delPos);
                mDelViewList.add(getChildAt(delPos - mOriFirstPosition));
                mOriCurDeleteCount++;
            } else {
                belowDeleteCount++;
            }
        }
        boolean isDel = false;

        if (mOriUpperDeleteCount > 0 || mDelPosList.size() > 0) {
            isDel = true;
        }

        int pos = 0;
        if (isDel == false) {
            mAnimRunning = false;
            mInDeleteAnimation = false;
            mDisableTouchEvent = false;
            if (mDeleteAnimationListener != null) {
                mDeleteAnimationListener.onAnimationUpdate();
                mDeleteAnimationListener.onAnimationEnd();
            }
        } else {
            View child = null;
            int size = mDelPosList.size();
            for (int i = 0; i < childCount; ++i) {
                if (size > 0) {
                    pos = mOriFirstPosition + i;
                    if (mDelPosList.contains(pos) == false) {
                        child = getChildAt(i);
                        if (child != null)
                            mDelOriViewTopList.add(child.getTop());
                    }
                } else {
                    child = getChildAt(i);
                    if (child != null)
                        mDelOriViewTopList.add(child.getTop());
                }
            }

            mOriCurLeftCount = this.getChildCount() - mOriCurDeleteCount;
            mOriBelowLeftCount = getAdapter().getCount() + listLength - getLastVisiblePosition()
                    - 1 - belowDeleteCount;

            startDelGoneAnimation();
        }
    }

    private void startDelGoneAnimation() {
        mAnimRunning = true;

        int size = mDelViewList.size();
        if (size == 0) {
            mDelAnimationFlag = true;
            if (mDeleteAnimationListener != null) {
                mDeleteAnimationListener.onAnimationUpdate();
            }
            mDisableTouchEvent = false;
            return;
        }
        ObjectAnimator anim;

        mDelAniSet = new AnimatorSet();
        int time = 300;

        View child;

        PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat("alpha", 1f, 0f);
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 0.5f);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 0.5f);

        for (int i = 0; i < size; ++i) {
            child = mDelViewList.get(i);
            anim = ObjectAnimator.ofPropertyValuesHolder(child, pvhScaleY, pvhScaleX, pvhAlpha);
            anim.setDuration(time);
            anim.setInterpolator(mDecelerateInterpolator);
            anim.addUpdateListener(mAnimatorUpdateListener);
            mDelAniSet.playTogether(anim);
        }

        mDelAniSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                int size = mDelViewList.size();
                View child = null;
                for (int i = 0; i < size; ++i) {
                    child = mDelViewList.get(i);
                    child.setScaleX(1f);
                    child.setScaleY(1f);
                    child.setAlpha(1f);
                }

                if ((getAdapter() != null) && ((getAdapter().getCount() == 0) ||
                        ((getEmptyView() != null) && getAdapter().isEmpty()))) { // delete all data
                    mAnimRunning = false;
                    mInDeleteAnimation = false;
                    mDisableTouchEvent = false;
                    mDelPosList.clear();
                    mDelOriViewTopList.clear();
                    mDelViewList.clear();
                    if (mDeleteAnimationListener != null) {
                        mDeleteAnimationListener.onAnimationUpdate();
                        mDeleteAnimationListener.onAnimationEnd();
                    }
                } else {
                    mDelAnimationFlag = true;
                    if (mDeleteAnimationListener != null) {
                        mDeleteAnimationListener.onAnimationUpdate();
                    }
                }
            }
        });
        mDelAniSet.start();
        if (isAccessibilityEnabled()) endDelAnimator();
    }

    private void startDelDropAnimation() {
        ObjectAnimator anim;

        mDelAniSet = new AnimatorSet();
        setDelViewLocation();

        final int minDuration = 150;
        int time = minDuration;
        View child = null;

        for (int i = 0; i < mNowViewList.size(); ++i) {
            child = mNowViewList.get(i);

            float pos = i;
            float count = mNowViewList.size() - 1;
            time = minDuration
                    + (int) (time * mDurationInterpolator.getInterpolation((pos) / count));
            anim = getAnimator(i, child, mDelOriViewTopList.get(i));
            anim.setDuration(time);

            anim.setInterpolator(mDecelerateInterpolator);
            anim.addUpdateListener(mAnimatorUpdateListener);
            mDelAniSet.playTogether(anim);
        }

        mDelAniSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mEndDelAniEarly = false;
                mAnimRunning = false;
                mInDeleteAnimation = false;
                mDisableTouchEvent = false;

                mDelPosList.clear();
                mDelOriViewTopList.clear();
                mDelViewList.clear();
                mNowViewList.clear();
                invalidate();

                if (mDeleteAnimationListener != null) {
                    mDeleteAnimationListener.onAnimationEnd();
                }
            }
        });
        mDelAniSet.start();
        if (mEndDelAniEarly) {
            endDelAnimator();
        }
    }

    private boolean isAccessibilityEnabled() {
        AccessibilityManager accessManager = (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        return accessManager != null && accessManager.isEnabled();
    }

    private void setDelViewLocation() {
        int nowFirstPosition = getFirstVisiblePosition();
        int positionDiff = Math.abs(nowFirstPosition - mOriFirstPosition);
        int nowCurChildCount = this.getChildCount();
        boolean nowLastPage = false;
        if (this.getLastVisiblePosition() == this.getAdapter().getCount() - 1)
            nowLastPage = true;

        boolean nowFirstPage = false;
        if (nowFirstPosition == 0)
            nowFirstPage = true;

        int top = this.getTop();
        int bottom = this.getBottom();

        View child = null;
        int childCount = getChildCount();
        int height = 100;
        int diff = 0;

        if (mNowViewList == null)
            mNowViewList = new ArrayList<View>();
        else
            mNowViewList.clear();

        for (int i = 0; i < childCount; ++i) {
            child = getChildAt(i);
            mNowViewList.add(child);
            if (i == 0 && child != null)
                height = child.getHeight();
        }
        int childIndex = 1;
        if (mOriLastPage == false) { // original not at last page
            if (nowLastPage == false) { // Case 1
                if (mOriUpperDeleteCount == 0) {
                    android.util.Log.d("HtcListView", "DeleteAnimation Case 1");
                    // Do nothing
                } else if (mOriUpperDeleteCount >= mOriCurLeftCount) { // Case 3
                    android.util.Log.d("HtcListView", "DeleteAnimation Case 3 ");
                    mDelOriViewTopList.clear();
                } else { // Case 2
                    android.util.Log.d("HtcListView", "DeleteAnimation Case 2 ");
                    for (int i = 0; i < mOriUpperDeleteCount; i++) {
                        mDelOriViewTopList.remove(0);
                    }
                }
            } else {
                if (nowFirstPage == false) {
                    if (mOriUpperDeleteCount == 0) { // Case 4
                        android.util.Log.d("HtcListView", "DeleteAnimation Case 4 ");
                    } else {
                        if (mOriCurDeleteCount == 0) {
                            if (mOriUpperDeleteCount >= mOriCurLeftCount) { // Case
                                                                            // 9
                                android.util.Log.d("HtcListView", "DeleteAnimation Case 9 ");
                            } else { // Case 10
                                android.util.Log.d("HtcListView", "DeleteAnimation Case 10 ");
                            }
                        } else {
                            if (mOriUpperDeleteCount >= mOriCurLeftCount) { // Case
                                                                            // 5
                                android.util.Log.d("HtcListView", "DeleteAnimation Case 5 ");
                            } else { // Case 6
                                android.util.Log.d("HtcListView", "DeleteAnimation Case 6 ");
                            }
                        }
                    }
                } else {
                    if (mOriCurDeleteCount == 0) { // Case 11
                        android.util.Log.d("HtcListView", "DeleteAnimation Case 11 ");
                    } else {
                        if (mOriUpperDeleteCount >= mOriCurLeftCount) { // Case
                                                                        // 7
                            android.util.Log.d("HtcListView", "DeleteAnimation Case 7 ");
                        } else { // Case 8
                            android.util.Log.d("HtcListView", "DeleteAnimation Case 8 ");
                        }
                    }
                }

                // add item from the bottom
                for (int i = 0; i < mOriBelowLeftCount; i++) {
                    mDelOriViewTopList.add(bottom + (i + 1) * height);
                }

                // if the count of OriList bigger than nowViewCountremove the
                // item from mDelOriViewTopList and mDelOriViewLeftList
                diff = mDelOriViewTopList.size() - nowCurChildCount;
                for (int i = 0; i < diff; i++) {
                    mDelOriViewTopList.remove(0);
                }

                // add item from the top
                childIndex = 1;
                while (nowCurChildCount > mDelOriViewTopList.size()) {
                    mDelOriViewTopList.add(0, -height * childIndex);
                    childIndex++;
                }
            }
        } else {
            if (mOriUpperDeleteCount == 0) {
                if (mOriCurDeleteCount == 0) {

                } else { // Case 14
                    android.util.Log.d("HtcListView", "DeleteAnimation Case 14 ");
                }
            } else {
                if (mOriCurDeleteCount == 0) {
                    if (mOriUpperDeleteCount >= mOriCurLeftCount) { // Case 12
                        android.util.Log.d("HtcListView", "DeleteAnimation Case 12 ");
                        mDelOriViewTopList.clear();
                    } else { // Case 13
                        android.util.Log.d("HtcListView", "DeleteAnimation Case 13 ");
                        for (int i = 0; i < mOriUpperDeleteCount; i++) {
                            mDelOriViewTopList.remove(0);
                        }
                    }
                } else {
                    if (nowFirstPage == false) {
                        if (mOriUpperDeleteCount >= mOriCurLeftCount) { // Case
                                                                        // 15
                            android.util.Log.d("HtcListView", "DeleteAnimation Case 15 ");
                        } else { // Case 16
                            android.util.Log.d("HtcListView", "DeleteAnimation Case 16 ");
                        }
                    } else { // Case 17
                        android.util.Log.d("HtcListView", "DeleteAnimation Case 17 ");
                    }
                }
            }

            // add item from the top
            childIndex = 1;
            while (nowCurChildCount > mDelOriViewTopList.size()) {
                mDelOriViewTopList.add(0, -height * childIndex);
                childIndex++;
            }
        }

        // add item from the bottom
        diff = mNowViewList.size() - mDelOriViewTopList.size();
        for (int i = 0; i < diff; i++) {
            mDelOriViewTopList.add(bottom + height * (i + 1));
        }

        // check the invert count
        int invertCount = 0;
        for (int i = childCount - 1; i >= 0; i--) {
            if (mNowViewList.get(i).getTop() == mDelOriViewTopList.get(i).intValue()) {
                mNowViewList.remove(i);
                mDelOriViewTopList.remove(i);
            } else if (mDelOriViewTopList.get(i).intValue() < mNowViewList.get(i).getTop()) {
                invertCount++;
            }
        }

        // change the order
        if (invertCount > 1) { // only one, do not need to change order
            ArrayList<View> tmpViewList = (ArrayList<View>) mNowViewList.clone();
            ArrayList<Integer> tmpOriTopList = (ArrayList<Integer>) mDelOriViewTopList.clone();
            mNowViewList.clear();
            mDelOriViewTopList.clear();
            int tmpPos = 0;
            for (int i = 0; i < tmpViewList.size(); i++) {
                if (i < invertCount) { // change order
                    tmpPos = invertCount - i - 1;
                } else {
                    tmpPos = i;
                }
                mNowViewList.add(tmpViewList.get(tmpPos));
                mDelOriViewTopList.add(tmpOriTopList.get(tmpPos));
            }
        }
    }

    private ObjectAnimator getAnimator(int index, View child, float startValue) {
        ObjectAnimator animator = null;
        if (index >= mAnimatorList.size()) {
            PropertyValuesHolder y = PropertyValuesHolder.ofFloat("y", startValue, child.getTop());
            animator = ObjectAnimator.ofPropertyValuesHolder(child, y);
            mAnimatorList.add(animator);
        } else {
            animator = mAnimatorList.get(index);
            PropertyValuesHolder[] pvhArray = animator.getValues();
            PropertyValuesHolder y = pvhArray[0];
            y.setFloatValues(startValue, child.getTop());
            animator.setTarget(child);
        }
        return animator;
    }

    /**
     * Disable touch event before add animation or delete animation start. When
     * add animation or delete animation ends, it will enable touch event.
     *
     * @hide
     */
    public void disableTouchEventInAnim() {
        mDisableTouchEvent = true;
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

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mListViewUtil.onSizeChanged(w, h, oldw, oldh);
    }

    void setDarkModeEnabled(boolean enabled) {}

    /**
     * To end current deleting Animator
     * HtcListView may trigger animation layout error issue when switch the screen orientation
     * User shold call this API in any layout change of HtcListView parent. for example: orientation change invoke the whole screen layout change
     */
    public void endDelAnimator() {
        if (mDelAniSet != null && mDelAniSet.isRunning()) {
            mEndDelAniEarly = true;
            mDelAniSet.end();
        }
    }

    /**
     * When delete animation is running,don't InitializeAccessibilityNodeInfoForItem
     */
    @Override
    public void onInitializeAccessibilityNodeInfoForItem(View view, int position, AccessibilityNodeInfo info) {
        if (mIsDisableAnimation) return;

        ListAdapter listAdapter = getAdapter();
        if (listAdapter == null || listAdapter.getCount() == 0) return;

        if (listAdapter instanceof HeaderViewListAdapter) {
            final HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) listAdapter;
            ListAdapter adapter = headerViewListAdapter.getWrappedAdapter();
            final int adapterCount = adapter != null ? adapter.getCount() : 0;
            final int adjPosition = position - headerViewListAdapter.getHeadersCount();
            if (adjPosition - adapterCount >= headerViewListAdapter.getFootersCount()) return;
        }

        super.onInitializeAccessibilityNodeInfoForItem(view, position, info);
    }
}
