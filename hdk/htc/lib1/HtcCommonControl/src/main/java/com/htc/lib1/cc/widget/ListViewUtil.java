
package com.htc.lib1.cc.widget;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.htc.lib1.cc.R;

class ListViewUtil {

    final static String TAG = "ListViewUtil";

    AbsListView mListView;

    static int PULL_DOWN_REFRESH_GAP = 192;

    static int MAX_OVERSCROLL_GAP = 192;

    int mMotionDownY = Integer.MIN_VALUE;

    int mCurrentMotionY = 0;

    int mLastMotionY = Integer.MIN_VALUE;

    int mTouchSlop = 0;

    int mCurrentScrollY = 0;

    int mAccumulativeDeltaY = 0;

    float mOverScrollSensitivity = 1;

    int mDividerMargin = 0;

    int mSectionDividerMarginLeft = 0;

    int mAutomotiveSectionDividerMarginLeft = 0;

    private static int sListItemHeight = 0;

    boolean mIsBeginOverScroll = false;

    boolean mIsInterceptMotionEvent = false;

    boolean mIsCancelRefresh = false;

    boolean mOverScrollStarted = false;

    boolean mOverScrollToBoundary = false;

    boolean mIsFlinging = false;

    boolean mIsScrollBarAtRight = false;

    boolean mFastScrollEnabled = false;

    boolean mIsBeginFastScroll = false;

    boolean mHtcScrollEnabled = false;

    int mEnabledAnimationType = IHtcAbsListView.ANIM_OVERSCROLL | IHtcAbsListView.ANIM_INTRO
            | IHtcAbsListView.ANIM_DEL;

    Drawable mSectionDivider = null;

    Drawable mAutomotiveSectionDivider = null;

    OnPullDownListener mUserRefreshListener;

    ObjectAnimator mBouncingAnimator;

    DecelerateInterpolator mBouncingInterpolator = new DecelerateInterpolator(2f);

    OnScrollListenerWrapper mOnScrollListenerWrapper = new OnScrollListenerWrapper();

    OnScrollListener mUserOnScrollListener;

    Callbacks mCallbacks;

    interface Callbacks {
        // For over-scroll feature
        public boolean superOnInterceptTouchEvent(MotionEvent ev);

        public boolean superOnTouchEvent(MotionEvent ev);

        public void superOnOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY);

        public void superOnDetachedFromWindow();

        public void superOnWindowFocusChanged(boolean hasWindowFocus);

        // For divider feature
        public void superInvalidate();

        public void superDispatchDraw(Canvas canvas);

        public void superRequestLayout();
    }

    void init(AbsListView absListView, Callbacks callbacks) {
        mListView = absListView;
        mCallbacks = callbacks;
        Context context = mListView.getContext();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mListView.setVerticalFadingEdgeEnabled(false);
        mListView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        mListView.setDrawSelectorOnTop(true);

        Resources res = context.getResources();
        if(sListItemHeight <= 0 ) {
            sListItemHeight = (Integer) HtcProperty.getProperty(mListView.getContext(), "HtcListItemHeight");
        }
        PULL_DOWN_REFRESH_GAP = sListItemHeight;
        MAX_OVERSCROLL_GAP = 2 * PULL_DOWN_REFRESH_GAP;
        mDividerMargin = res.getDimensionPixelOffset(
                R.dimen.common_list_divider_margin);
        mSectionDividerMarginLeft = (Integer) HtcProperty.getProperty(context, "HtcListItemHeight")
                + res.getDimensionPixelOffset(R.dimen.margin_m);
        DisplayMetrics dm = res.getDisplayMetrics();
        int widthInPortrait = (dm.widthPixels < dm.heightPixels) ? dm.widthPixels : dm.heightPixels;
        mAutomotiveSectionDividerMarginLeft = (int) (0.2 * widthInPortrait);
        mListView.setOnScrollListener(mOnScrollListenerWrapper);
        setHtcScrollEnabled(true);
    }

    /**
     * Set the listener that will receive notifications every time the list or
     * grid be pulled down.
     *
     * @param onPullDownListener the pull-down listener
     */
    void setOnPullDownListener(OnPullDownListener onPullDownListener) {
        mUserRefreshListener = onPullDownListener;
    }

    void setOnScrollListener(OnScrollListener l) {
        mUserOnScrollListener = l;
    }

    class OnScrollListenerWrapper implements OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            mIsFlinging = scrollState == OnScrollListener.SCROLL_STATE_FLING;
            if(mUserOnScrollListener != null) {
                mUserOnScrollListener.onScrollStateChanged(view, scrollState);
            }
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                int totalItemCount) {
            if(mUserOnScrollListener != null) {
                mUserOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }
    }

    boolean isUserOnScrollListener(OnScrollListener l) {
        return l != mOnScrollListenerWrapper;
    }

    void enableAnimation(int animationType, boolean enable) {
        if (enable)
            mEnabledAnimationType |= animationType;
        else
            mEnabledAnimationType &= ~animationType;
        if(isOverScrolledAnimationEnabled()) {
            mListView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        } else {
            mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }

    boolean isOverScrolledAnimationEnabled() {
        return (mEnabledAnimationType & IHtcAbsListView.ANIM_OVERSCROLL) != 0;
    }

    boolean isDelAnimationEnabled() {
        return (mEnabledAnimationType & IHtcAbsListView.ANIM_DEL) != 0;
    }

    boolean isIntroAnimationEnabled() {
        return (mEnabledAnimationType & IHtcAbsListView.ANIM_INTRO) != 0;
    }

    private boolean isReadyToOverScroll(boolean isPullDown) {
        final Adapter adapter = mListView.getAdapter();

        if ((null == adapter || adapter.isEmpty()) && ((mListView instanceof ListView && ((ListView)mListView).getFooterViewsCount() == 0 && ((ListView)mListView).getHeaderViewsCount() == 0) ||
            !(mListView instanceof ListView))) {
            return false;
        } else {
            if (isPullDown && mListView.getFirstVisiblePosition() == 0) {
                View firstVisibleChild = mListView.getChildAt(0);
                if (firstVisibleChild != null) {
                    return firstVisibleChild.getTop() >= mListView.getListPaddingTop();
                }
            } else if (!isPullDown && null != adapter && mListView.getLastVisiblePosition() == adapter.getCount() - 1) {
                View lastVisibleChild = mListView.getChildAt(mListView.getChildCount() - 1);
                if (lastVisibleChild != null) {
                    return lastVisibleChild.getBottom() <= mListView.getHeight()
                            - mListView.getListPaddingBottom();
                }
            }
        }
        return false;
    }
    /**
     * Returns whether a coordinate is inside the scroller's activation area.
     * The user has to touch inside thumb itself.
     *
     * @param x The x-coordinate.
     * @return Whether the coordinate is inside the scroller's activation area.
     */
    private boolean isPointInsideScrollBar(float x) {
        setScrollbarPosition(mListView.getVerticalScrollbarPosition());

        if (mIsScrollBarAtRight) {
            return x >= mListView.getWidth() - mListView.getVerticalScrollbarWidth();
        } else {
            return x <= mListView.getVerticalScrollbarWidth();
        }
    }

    void setScrollbarPosition(int position) {
        // Determine if place the scroll bar to the right side.
        if (position == View.SCROLLBAR_POSITION_DEFAULT) {
            position = (mListView.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) ?
                    View.SCROLLBAR_POSITION_LEFT : View.SCROLLBAR_POSITION_RIGHT;
        }
        mIsScrollBarAtRight = position != View.SCROLLBAR_POSITION_LEFT;
    }

    void setFastScrollEnabled(boolean enabled) {
        mFastScrollEnabled = enabled;
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isOverScrolledAnimationEnabled() || (mIsBeginFastScroll && event.getAction() != MotionEvent.ACTION_DOWN)) {
            return false;
        }

        // If the user touch down at a clickable view.
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mIsBeginFastScroll = false;
                touchDown(event);
                mIsBeginFastScroll = mFastScrollEnabled && isPointInsideScrollBar(event.getX());
                break;
            }
            case MotionEvent.ACTION_MOVE:
                touchMove(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touchEnd(event);
                break;
        }

        boolean handled = interceptTouchEvent(event, true);
        return handled;
    }

    boolean onTouchEvent(MotionEvent ev) {
        if (!isOverScrolledAnimationEnabled() || mIsBeginFastScroll) {
            return mCallbacks.superOnTouchEvent(ev);
        }

        boolean handled = false;
        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchDown(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(ev);
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touchEnd(ev);
                break;
        }

        // When send cancel event to superOnIntercepTouchEvent() (begin over-scroll)
        // , we also need to intercept the touch event from child views to avoid
        // triggering unexpected behavior in child views. (ex: HorizontalScrollView case).
        handled = interceptTouchEvent(ev, false);

        return handled;
    }

    void touchDown(MotionEvent ev) {
        if (mBouncingAnimator != null && mBouncingAnimator.isStarted()) {
            mBouncingAnimator.end();
        }
        mListView.setScrollY(0);
        mMotionDownY = (int) ev.getY();
        mIsInterceptMotionEvent = mIsBeginOverScroll = mIsCancelRefresh = false;
    }

    void touchMove(MotionEvent ev) {
        mCurrentMotionY = (int) ev.getY();
        final int oldScroll = mListView.getScrollY();
        boolean firstScroll = mLastMotionY == Integer.MIN_VALUE;
        int deltaY = !firstScroll ? mCurrentMotionY - mLastMotionY : mCurrentMotionY
                - mMotionDownY;
        deltaY = computeDeltaYWithSensitivity(deltaY);

        final int newScroll = oldScroll - deltaY;
        int overScrollDistance = -deltaY;

        if (firstScroll && mCurrentMotionY > mMotionDownY || !firstScroll
                && mCurrentMotionY > mLastMotionY) {
            if (isReadyToOverScroll(true) || oldScroll > 0) {
                if (!mIsBeginOverScroll) {
                    mIsBeginOverScroll = (mCurrentMotionY - mMotionDownY) > mTouchSlop;

                    if(mIsBeginOverScroll) {
                        // Time to start over-scrolling, don't let anyone steal from us
                        final ViewParent parent = mListView.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                }

                if(mIsBeginOverScroll) {
                    if (newScroll > 0 && oldScroll <= 0 || newScroll < 0 && oldScroll > 0) {
                        overScrollDistance = -oldScroll;
                    }
                    overScrollListView(overScrollDistance);
                    if(mListView.getScrollY() < 0 && mUserRefreshListener!=null) {
                        mOverScrollStarted = true;
                    }
                }
            }
        } else if (firstScroll &&  mCurrentMotionY < mMotionDownY || !firstScroll && mCurrentMotionY < mLastMotionY) {
            if (isReadyToOverScroll(false) || oldScroll < 0) {
                if (!mIsBeginOverScroll) {
                    mIsBeginOverScroll = (mMotionDownY - mCurrentMotionY) > mTouchSlop;

                    if(mIsBeginOverScroll) {
                        // Time to start over-scrolling, don't let anyone steal from us
                        final ViewParent parent = mListView.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                }

                if(mIsBeginOverScroll) {
                    if (newScroll < 0 && oldScroll >= 0 || newScroll > 0 && oldScroll < 0) {
                        overScrollDistance = -oldScroll;
                    }
                    overScrollListView(overScrollDistance);
                }
            }
        } else if (mLastMotionY == mCurrentMotionY) {
            return;
        }

        if (mUserRefreshListener != null && mListView.getScrollY() <= 0 && mOverScrollStarted) {
            int gap = Math.abs(mListView.getScrollY());
            if (gap >= PULL_DOWN_REFRESH_GAP && !mOverScrollToBoundary) {
                mOverScrollToBoundary = true;
                mUserRefreshListener.onPullDownToBoundary();
            } else if (gap < PULL_DOWN_REFRESH_GAP && mOverScrollToBoundary) {
                mOverScrollToBoundary = false;
            }

            if (mLastMotionY != mCurrentMotionY && !mOverScrollToBoundary) {
                mUserRefreshListener.onGapChanged(gap, PULL_DOWN_REFRESH_GAP);
            }
        }
        if (mUserRefreshListener != null && mOverScrollStarted && oldScroll < 0
                && mListView.getScrollY() >= 0) {
            mIsBeginOverScroll = mOverScrollStarted = false;
            mUserRefreshListener.onPullDownCancel();
        }
        mLastMotionY = (int) ev.getY();
    }

    void touchEnd(MotionEvent ev) {
        playBouncingAnimation();
        mIsBeginOverScroll = false;
        mMotionDownY = Integer.MIN_VALUE;

        if (mOverScrollStarted && mUserRefreshListener != null) {
            if (mOverScrollToBoundary && ev.getAction() != MotionEvent.ACTION_CANCEL) {
                mUserRefreshListener.onPullDownRelease();
                mUserRefreshListener.onPullDownFinish();
            } else if (!mOverScrollToBoundary || ev.getAction() == MotionEvent.ACTION_CANCEL) {
                mUserRefreshListener.onPullDownCancel();
            }
            mOverScrollStarted = false;
        }
        mLastMotionY = Integer.MIN_VALUE;
        mIsBeginFastScroll = false;
    }

    boolean interceptTouchEvent(MotionEvent ev, boolean isOnInterceptTouchEvent) {
        boolean handled = false;
        if (!mIsInterceptMotionEvent) {
            if (mIsBeginOverScroll) {
                ev.setAction(MotionEvent.ACTION_CANCEL);
                // To avoid dispatching ACTION_CANCEL event to super class too
                // many times.
                mIsInterceptMotionEvent = true;
//                mListView.cancelPendingInputEvents();
            }

            mCurrentScrollY = mListView.getScrollY();
            if(ev.getAction() == MotionEvent.ACTION_CANCEL) {
                mCallbacks.superOnInterceptTouchEvent(ev);
                handled = true;
            }

             if(!isOnInterceptTouchEvent) {
                 // We need to send cancel event to both onInterceptTouchEvent() and onTouchevent().
                 // The first one is to report idle state, and the second one is to remove selector.
                handled = mCallbacks.superOnTouchEvent(ev);
            }
            // AbsListView will set mScrollY to 0 when the touch mode enters
            // TOUCH_MODE_OVERSCROLL.
            // Hence, we need to recover the scrollY.
            if (mListView.getScrollY() != mCurrentScrollY)
                mListView.setScrollY(mCurrentScrollY);
        }
        return handled;
    }

    void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(h!=oldh) {
            mOverScrollSensitivity = h > 0 ? (1.5f * PULL_DOWN_REFRESH_GAP / h) : 1;
        }
    }

    private int computeDeltaYWithSensitivity(int deltaY) {
        int newOverScrollDistance = deltaY;
        if(mUserRefreshListener == null ||
           deltaY < 0 && isReadyToOverScroll(false) && mListView.getScrollY() >= 0 ||
           mListView.getScrollY() <= -PULL_DOWN_REFRESH_GAP) {
            mAccumulativeDeltaY += deltaY;
            int newDeltaY = (int)(mAccumulativeDeltaY * mOverScrollSensitivity);
            if(newDeltaY != 0) {
                newOverScrollDistance = newDeltaY;
                mAccumulativeDeltaY = 0;
            } else {
                newOverScrollDistance = 0;
            }
        }
        return newOverScrollDistance;
    }

    private void overScrollListView(int delta) {
        int oldScrollY = mListView.getScrollY();
        int newScrollY = oldScrollY + delta;
        final int top = -MAX_OVERSCROLL_GAP;
        final int bottom = MAX_OVERSCROLL_GAP;
        if (newScrollY > bottom) {
            newScrollY = bottom;
        } else if (newScrollY < top) {
            newScrollY = top;
        }
        mListView.setScrollY(newScrollY);
    }

    private void playBouncingAnimation() {
        if (isOverScrolledAnimationEnabled() && mListView.getScrollY() != 0) {
            PropertyValuesHolder pvhScrollY = PropertyValuesHolder.ofInt("scrollY", 0);
            mBouncingAnimator = ObjectAnimator.ofPropertyValuesHolder(mListView, pvhScrollY);
            mBouncingAnimator.setInterpolator(mBouncingInterpolator);
            mBouncingAnimator.start();
        }
    }

    void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        boolean isOverScrolling = (mBouncingAnimator != null && mBouncingAnimator.isStarted())
                || mIsBeginOverScroll;
        if (isOverScrolledAnimationEnabled() && (isOverScrolling || !mIsFlinging))
            mCurrentScrollY = mListView.getScrollY();
        mCallbacks.superOnOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (isOverScrolledAnimationEnabled() && (isOverScrolling || !mIsFlinging))
            mListView.setScrollY(mCurrentScrollY);
    }

    void onDetachedFromWindow() {
        mCallbacks.superOnDetachedFromWindow();

        if (mUserRefreshListener != null && mIsBeginOverScroll && !mIsCancelRefresh) {
            mUserRefreshListener.onPullDownCancel();
            mIsInterceptMotionEvent = mIsBeginOverScroll = false;
            mIsCancelRefresh = true;
        }
    }

    void onWindowFocusChanged(boolean hasWindowFocus) {
        if(hasWindowFocus) {
            mCallbacks.superOnWindowFocusChanged(hasWindowFocus);
        } else {
            boolean isOverScrolling = (mBouncingAnimator != null && mBouncingAnimator.isStarted())
                    || mIsBeginOverScroll;
            if (isOverScrolledAnimationEnabled() && isOverScrolling)
                mCurrentScrollY = mListView.getScrollY();
            mCallbacks.superOnWindowFocusChanged(hasWindowFocus);
            if (isOverScrolledAnimationEnabled() && isOverScrolling)
                mListView.setScrollY(mCurrentScrollY);
        }
    }

    Paint mDividerPaint;

    Rect mTempRect = new Rect();

    private boolean mIsPreventReLayout = false;

    private boolean mIsClipToPadding = true;

    private boolean mHeaderDividersEnabled = true;

    private boolean mFooterDividersEnabled = true;

    private Drawable mOverScrollHeader;

    private Drawable mOverScrollFooter;

    IDividerController mDividerController = null;

    /**
     * Set a divider controller used to indicate how to draw the divider between
     * list items.
     *
     * @param dividerController The divider controller.
     */
    public void setDividerController(IDividerController dividerController) {
        mDividerController = dividerController;
    }

    private boolean shouldDrawDivider(int position) {
        return mDividerController == null
                || (mDividerController != null && mDividerController.getDividerType(position) > IDividerController.DIVIDER_TYPE_NONE);
    }

    private int getDividerType(int position) {
        if(mDividerController == null) {
            return -1;
        }

        return mDividerController.getDividerType(position);
    }

    private Drawable getSectionDivider(int dividerType) {
        Drawable divider = null;

        if(dividerType == IDividerController.DIVIDER_TYPE_SECTION) {
            if(mSectionDivider == null) {
                mSectionDivider = mListView.getContext().getResources()
                        .getDrawable(R.drawable.common_list_divider);
            }
            divider = mSectionDivider;
        } else if(dividerType == IDividerController.DIVIDER_TYPE_SECTION_AUTOMOTIVE) {
            if(mAutomotiveSectionDivider == null) {
                mAutomotiveSectionDivider = mListView.getContext().getResources()
                        .getDrawable(R.drawable.common_b_div_land);
            }
            divider = mAutomotiveSectionDivider;
        }
        return divider;
    }

    private int getSectionDividerMarginLeft(int dividerType) {
        int marginLeft = 0;

        if(dividerType == IDividerController.DIVIDER_TYPE_SECTION) {
            marginLeft = mSectionDividerMarginLeft;
        } else if(dividerType == IDividerController.DIVIDER_TYPE_SECTION_AUTOMOTIVE) {
            marginLeft = mAutomotiveSectionDividerMarginLeft;
        }
        return marginLeft;
    }

    void setHeaderDividersEnabled(boolean headerDividersEnabled) {
        mHeaderDividersEnabled = headerDividersEnabled;
    }

    void setFooterDividersEnabled(boolean footerDividersEnabled) {
        mFooterDividersEnabled = footerDividersEnabled;
    }

    void setOverscrollHeader(Drawable header) {
        mOverScrollHeader = header;
    }

    void setOverscrollFooter(Drawable footer) {
        mOverScrollFooter = footer;
    }

    void drawDivider(Canvas canvas, Rect bounds, int childIndex) {
        if (mListView instanceof ListView) {
            Drawable divider = ((ListView) mListView).getDivider();
            // If draw the overScroll divider, childIndex = -1.
            if (childIndex >= 0) {
                int positionInAdapter = childIndex + mListView.getFirstVisiblePosition();
                int dividerType = getDividerType(positionInAdapter);
                if (mDividerController != null && dividerType >= IDividerController.DIVIDER_TYPE_SECTION) {
                    divider = getSectionDivider(dividerType);
                    bounds.left += getSectionDividerMarginLeft(dividerType);
                    bounds.right += -mDividerMargin;
                }
            }
            divider.setBounds(bounds);
            divider.draw(canvas);
        }
    }

    void setClipToPadding(boolean clipToPadding) {
        mIsClipToPadding = clipToPadding;
    }

    private boolean isClipToPadding() {
        boolean isPaddingNotNull = (mListView.getPaddingLeft() | mListView.getPaddingTop()
                | mListView.getPaddingRight() | mListView.getPaddingBottom()) != 0;
        return mIsClipToPadding && isPaddingNotNull;
    }

    public void invalidate() {
        if (mIsPreventReLayout) {
            return;
        }

        if (mCallbacks != null) {
            mCallbacks.superInvalidate();
        }
    }

    public void requestLayout() {
        if (mIsPreventReLayout) {
            return;
        }

        if (mCallbacks != null) {
            mCallbacks.superRequestLayout();
        }
    }

    void dispatchDraw(Canvas canvas) {
        if (mListView instanceof ListView) {
            ListView listView = (ListView) mListView;
            int originalDividerHeight = listView.getDividerHeight();
            mIsPreventReLayout = true;
            listView.setDividerHeight(0);
            mCallbacks.superDispatchDraw(canvas);

            // Draw the dividers
            final int dividerHeight = originalDividerHeight;
            final Drawable overscrollHeader = mOverScrollHeader;
            final Drawable overscrollFooter = mOverScrollFooter;
            final boolean drawOverscrollHeader = overscrollHeader != null;
            final boolean drawOverscrollFooter = overscrollFooter != null;
            final boolean drawDividers = listView.getDivider() != null;

            if (drawDividers) {
                // Only modify the top and bottom in the loop, we set the left
                // and right here
                final Rect bounds = mTempRect;
                bounds.left = listView.getPaddingLeft();
                bounds.right = listView.getRight() - listView.getLeft()
                        - listView.getPaddingRight();

                final ListAdapter adapter = listView.getAdapter();
                final int count = listView.getChildCount();
                final int headerCount = listView.getHeaderViewsCount();
                final int itemCount = listView.getCount();
                final int footerLimit = itemCount - listView.getFooterViewsCount() - 1;
                final boolean headerDividers = mHeaderDividersEnabled;
                final boolean footerDividers = mFooterDividersEnabled;
                final int first = listView.getFirstVisiblePosition();
                final boolean areAllItemsSelectable = true;

                int effectivePaddingTop = 0;
                int effectivePaddingBottom = 0;
                if (isClipToPadding()) {
                    effectivePaddingTop = listView.getListPaddingTop();
                    effectivePaddingBottom = listView.getListPaddingBottom();
                }

                final int listBottom = listView.getBottom() - listView.getTop()
                        - effectivePaddingBottom + listView.getScrollY();
                if (!listView.isStackFromBottom()) {
                    int bottom = 0;

                    for (int i = 0; i < count; i++) {
                        if ((headerDividers || first + i >= headerCount)
                                && (footerDividers || first + i < footerLimit)) {
                            View child = listView.getChildAt(i);
                            bottom = child.getBottom();
                            // Don't draw dividers next to items that are not
                            // enabled

                            if (drawDividers
                                    && shouldDrawDivider(i + listView.getFirstVisiblePosition())
                                    && child.getHeight() > 0
                                    && (bottom < listBottom && !(drawOverscrollFooter
                                            && i == count - 1 && bottom <= listBottom))) {
                                int scaleOffsetY = (int) ((child.getBottom() - child.getTop()) * (1 - child.getScaleY()) / 2);
                                int scaleOffsetX = (int) ((bounds.right - bounds.left) * (1 - child.getScaleX()) / 2);
                                int translationOffsetY = (int) child.getTranslationY();
                                bounds.top = bottom - scaleOffsetY + translationOffsetY;
                                bounds.bottom = bottom + dividerHeight - scaleOffsetY + translationOffsetY;
                                bounds.left = listView.getPaddingLeft() + scaleOffsetX;
                                bounds.right = listView.getRight() - listView.getLeft()
                                        - listView.getPaddingRight() - scaleOffsetX;
                                drawDivider(canvas, bounds, i);
                            }
                        }
                    }
                } else {
                    int top;
                    final int scrollY = listView.getScrollY();
                    final int start = drawOverscrollHeader ? 1 : 0;
                    for (int i = start; i < count; i++) {
                        if ((headerDividers || first + i >= headerCount)
                                && (footerDividers || first + i < footerLimit)) {
                            View child = listView.getChildAt(i);
                            top = child.getTop();
                            // Don't draw dividers next to items that are not
                            // enabled
                            if (top > effectivePaddingTop + scrollY
                                    && shouldDrawDivider(i + listView.getFirstVisiblePosition())
                                    && child.getHeight() > 0) {
                                if ((areAllItemsSelectable || (adapter.isEnabled(first + i) && (i == count - 1 || adapter
                                        .isEnabled(first + i + 1))))) {
                                    int scaleOffsetY = (int) ((child.getBottom() - child.getTop()) * (1 - child.getScaleY()) / 2);
                                    int scaleOffsetX = (int) ((bounds.right - bounds.left) * (1 - child.getScaleX()) / 2);
                                    int translationOffsetY = (int) child.getTranslationY();
                                    bounds.top = top - dividerHeight + scaleOffsetY + translationOffsetY;
                                    bounds.bottom = top + scaleOffsetY + translationOffsetY;
                                    bounds.left = listView.getPaddingLeft() + scaleOffsetX;
                                    bounds.right = listView.getRight() - listView.getLeft()
                                            - listView.getPaddingRight() - scaleOffsetX;
                                    // Give the method the child ABOVE the
                                    // divider, so we
                                    // subtract one from our child
                                    // position. Give -1 when there is no child
                                    // above the
                                    // divider.
                                    drawDivider(canvas, bounds, i - 1);
                                }
                            }
                        }
                    }

                    if (count > 0 && scrollY > 0) {
                        if (!drawOverscrollFooter && drawDividers && shouldDrawDivider(count - 1 + listView.getFirstVisiblePosition())) {
                            bounds.top = listBottom - listView.getScrollY();
                            bounds.bottom = listBottom + dividerHeight - listView.getScrollY();
                            drawDivider(canvas, bounds, -1);
                        }
                    }
                }
            }

            listView.setDividerHeight(originalDividerHeight);
            mIsPreventReLayout = false;
        }
    }

    int getDividerMargin() {
        return mDividerMargin;
    }

    void setHtcScrollEnabled(boolean enabled) {
        if(mHtcScrollEnabled == enabled) {
            return;
        }

        Class<?> classOfAbsListView = null;
        try {
            classOfAbsListView = Class.forName("android.widget.AbsListView");
            Method setHtcScrollEnabledMethod = classOfAbsListView.getDeclaredMethod("setHtcScrollEnabled", int.class, boolean.class);
            setHtcScrollEnabledMethod.setAccessible(true);
            setHtcScrollEnabledMethod.invoke(mListView, sListItemHeight, enabled);
        } catch (ClassNotFoundException e) {
            Log.d(TAG,"[ListViewUtil] andriod.widget.AbsListView class is not found");
        } catch (NoSuchMethodException e) {
            Log.d(TAG,"[ListViewUtil] setHtcScrollEnabled(int, boolean) in android.widget.AbsListView class is not found");
            setHtcScrollEnabledWithoutHeight(classOfAbsListView, enabled);
        } catch (IllegalAccessException e) {
            Log.d(TAG,"[ListViewUtil] IllegalAccessException");
        } catch (IllegalArgumentException e) {
            Log.d(TAG,"[ListViewUtil] IllegalArgumentException");
        } catch (InvocationTargetException e) {
            Log.d(TAG,"[ListViewUtil] InvocationTargetException");
        }
        mHtcScrollEnabled = enabled;
    }

    void setHtcScrollEnabledWithoutHeight(Class<?> classOfAbsListView, boolean enabled) {
        try {
            Method setHtcScrollEnabledMethod = classOfAbsListView.getDeclaredMethod("setHtcScrollEnabled", boolean.class);
            setHtcScrollEnabledMethod.setAccessible(true);
            setHtcScrollEnabledMethod.invoke(mListView, enabled);
            Log.d(TAG,"[ListViewUtil] invoke setHtcScrollEnabled(boolean) instead.");
        } catch (NoSuchMethodException e1) {
            Log.d(TAG,"[ListViewUtil] setHtcScrollEnabled(boolean) in android.widget.AbsListView class is not found");
        } catch (IllegalAccessException e) {
            Log.d(TAG,"[ListViewUtil] IllegalAccessException");
        } catch (IllegalArgumentException e) {
            Log.d(TAG,"[ListViewUtil] IllegalArgumentException");
        } catch (InvocationTargetException e) {
            Log.d(TAG,"[ListViewUtil] InvocationTargetException");
        }
    }
}
