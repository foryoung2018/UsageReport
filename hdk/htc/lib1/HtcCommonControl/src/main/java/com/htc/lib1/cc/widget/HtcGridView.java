package com.htc.lib1.cc.widget;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.animation.DecelerateInterpolator;
import android.widget.GridView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;

/**
 * The HTC style of the grid view with over-scroll, delete and intro animation.
 */

public class HtcGridView extends GridView implements Refreshable, IHtcAbsListView {

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

    private ArrayList<View> mIntroAnimateViewList = new ArrayList<View>();

    // Start: For deleted Animation
    AnimatorSet mDelAniSet = null;

    @ExportedProperty(category = "CommonControl")
    private boolean mEndDelAniEarly = false;

    private ArrayList<Integer> mDelPosList = null;

    private ArrayList<Integer> mDelOriViewTopList = null;

    private ArrayList<Integer> mDelOriViewLeftList = null;

    private ArrayList<View> mDelViewList = null;

    private ArrayList<View> mNowViewList = null;

    // End: For deleted Animation
    @ExportedProperty(category = "CommonControl")
    private boolean mDelAnimationFlag;

    @ExportedProperty(category = "CommonControl")
    private boolean mAllItemFlyIn;

    @ExportedProperty(category = "CommonControl")
    private boolean mIntroAnimationEnabled = true;

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

    ListViewUtil mListViewUtil;

    // This mode will keep the original grid view setting
    public final static int MODE_NONE = 0;
    // The generic style
    public final static int MODE_GENERIC = 1;
    // The overlay style
    public final static int MODE_OVERLAY = 2;
    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = MODE_NONE, to = "MODE_NONE"),
            @IntToString(from = MODE_GENERIC, to = "MODE_GENERIC"),
            @IntToString(from = MODE_OVERLAY, to = "MODE_OVERLAY")
    })
    private int mMode = MODE_NONE;
    @ExportedProperty(category = "CommonControl")
    private int mGap = 16;

    /**
     * Simple constructor to use when creating a HtcListView from code.
     *
     * @param context The Context the HtcListView is running in, through which
     *            it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcGridView(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a HtcListView from XML. This is
     * called when a view is being constructed from an XML file, supplying
     * attributes that were specified in the XML file. This version uses a
     * default style of 0, so the only attribute values applied are those in the
     * Context's Theme and the given AttributeSet.
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the HtcListView is running in, through which
     *            it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the
     *            HtcAbsListView.
     * @see #View(Context, AttributeSet, int)
     */
    public HtcGridView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.listViewStyle);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of HtcListView allows subclasses to use their own base style
     * when they are inflating. For example, a Button class's constructor would
     * call this version of the super class constructor and supply
     * <code>R.attr.buttonStyle</code> for <var>defStyle</var>; this allows the
     * theme's button style to modify all of the base view attributes (in
     * particular its background) as well as the Button class's attributes.
     *
     * @param context The Context the HtcListView is running in, through which
     *            it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the
     *            HtcListView.
     * @param defStyle The default style to apply to this HtcListView. If 0, no
     *            style will be applied (beyond what is included in the theme).
     *            This may either be an attribute resource, whose value will be
     *            retrieved from the current theme, or an explicit style
     *            resource.
     * @see #View(Context, AttributeSet)
     */
    public HtcGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        if (mListViewUtil == null) {
            mListViewUtil = new ListViewUtil();
        }
        mListViewUtil.init(this, new ListViewUtil.Callbacks() {
            @Override
            public boolean superOnInterceptTouchEvent(MotionEvent ev) {
                return HtcGridView.super.onInterceptTouchEvent(ev);
            }

            @Override
            public boolean superOnTouchEvent(MotionEvent ev) {
                return HtcGridView.super.onTouchEvent(ev);
            }

            @Override
            public void superOnOverScrolled(int scrollX, int scrollY, boolean clampedX,
                    boolean clampedY) {
                HtcGridView.super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
            }

            @Override
            public void superOnDetachedFromWindow() {
                HtcGridView.super.onDetachedFromWindow();
            }

            @Override
            public void superOnWindowFocusChanged(boolean hasWindowFocus) {
                HtcGridView.super.onWindowFocusChanged(hasWindowFocus);
            }

            @Override
            public void superDispatchDraw(Canvas canvas) {
                HtcGridView.super.dispatchDraw(canvas);
            }

            @Override
            public void superInvalidate() {
                HtcGridView.super.invalidate();
            }

            @Override
            public void superRequestLayout() {
                HtcGridView.super.requestLayout();
            }
        });
        mGap = getContext().getResources().getDimensionPixelOffset(R.dimen.gridview_gap);
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
    protected void dispatchDraw(Canvas canvas) {
        // Update start translationY before draw divider
        // Start: For Del Animation
        if (mDelAnimationFlag == true) {
            mDelAnimationFlag = false;
            startDelDropAnimation();
        }
        if (mListViewUtil.isIntroAnimationEnabled() && mIntroAnimationEnabled) {
            mIntroAnimationEnabled = false;
            startIntroAnimation();
        }
        super.dispatchDraw(canvas);
    }

    /**
     * Register the delete animation listener
     *
     * @param listener the DeleteAnimationListener
     */
    public void setDeleteAnimationListener(DeleteAnimationListener listener) {
        mDeleteAnimationListener = listener;
    }

    private boolean isDelAnimationEnabled() {
        return mListViewUtil.isDelAnimationEnabled();
    }

    /**
     * Set the indices of deleted items in data set. When this method is
     * invoked, the delete animation will be triggered if there are some deleted
     * items.
     *
     * @param d The indices of the deleted items in data set
     */
    public void setDelPositionsList(ArrayList<Integer> d) {
        if (d == null) {
            mDisableTouchEvent = false;
            throw new java.security.InvalidParameterException("The input parameter d is null!");
        }
        if (mAnimRunning == true) {
            mDisableTouchEvent = false;
            return;
        }
        if ((isDelAnimationEnabled() == false) || (mIsDisableAnimation == true)) {
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

        if (mDelOriViewLeftList == null)
            mDelOriViewLeftList = new ArrayList<Integer>();
        else
            mDelOriViewLeftList.clear();

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
                        if (child != null) {
                            mDelOriViewTopList.add(child.getTop());
                            mDelOriViewLeftList.add(child.getLeft());
                        }
                    }
                } else {
                    child = getChildAt(i);
                    if (child != null) {
                        mDelOriViewTopList.add(child.getTop());
                        mDelOriViewLeftList.add(child.getLeft());
                    }
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
        int time = 150;

        View child;

        PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat("alpha", 1f, 0f);
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 0.5f);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 0.5f);

        for (int i = 0; i < size; ++i) {
            child = mDelViewList.get(i);
            anim = ObjectAnimator.ofPropertyValuesHolder(child, pvhScaleY, pvhScaleX, pvhAlpha);
            anim.setDuration(time);
            // anim.setInterpolator(mDecelerateInterpolator);
            // anim.addUpdateListener(this.mAnimatorUpdateListener);
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

                if ((getAdapter() != null) && (getAdapter().getCount() == 0)) { // delete all data
                    mAnimRunning = false;
                    mInDeleteAnimation = false;
                    mDisableTouchEvent = false;
                    mDelPosList.clear();
                    mDelOriViewTopList.clear();
                    mDelOriViewLeftList.clear();
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
    }

    /**
     * Trigger the intro animation.
     */
    public void startIntroAnimation() {
        if (mListViewUtil.isIntroAnimationEnabled() != true) {
            return;
        }

        mAnimRunning = true;

        if (mIntroAnimateViewList.size() <= 0) {
            for (int i = 0; i < this.getChildCount(); i++) {
                // prepare animate list
                View temp = this.getChildAt(i);
                if (temp != null) {
                    temp.setAlpha(0f);
                    mIntroAnimateViewList.add(temp);
                }
            }
        }

        ArrayList<Animator> AnimationList = new ArrayList<Animator>();

        PropertyValuesHolder pvhBiggerA = PropertyValuesHolder.ofFloat("alpha", 0.0f, 1f);

        PropertyValuesHolder pvhTabletBiggerX = PropertyValuesHolder.ofFloat("scaleX", 0.3f, 1f);

        PropertyValuesHolder pvhTabletBiggerY = PropertyValuesHolder.ofFloat("scaleY", 0.3f, 1f);

        for (int i = 0; i < mIntroAnimateViewList.size(); i++) {
            View tempview = mIntroAnimateViewList.get(i);
            tempview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            tempview.setScaleX(0.3f);
            tempview.setScaleY(0.3f);
            ObjectAnimator temp = ObjectAnimator.ofPropertyValuesHolder(tempview, pvhBiggerA,
                    pvhTabletBiggerX, pvhTabletBiggerY);
            temp.setStartDelay(i * 15);
            temp.setDuration(150);
            AnimationList.add(i, temp);
        }

        AnimatorSet IntroAnimateSet = new AnimatorSet();
        IntroAnimateSet.playTogether(AnimationList);
        IntroAnimateSet.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationCancel(Animator animation) {
                mAnimRunning = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimRunning = false;
                mIntroAnimateViewList.clear();
                disableHardwareLayer();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }
        });

        IntroAnimateSet.start();
    }

    private void disableHardwareLayer() {
        for (int i = 0; i < getChildCount(); i++) {
            View item = getChildAt(i);
            item.setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }

    private void startDelDropAnimation() {

        setDelViewLocation();
        mAnimRunning = true;
        mDelAniSet = new AnimatorSet();
        ArrayList<Animator> AnimationList = new ArrayList<Animator>();

        android.util.Log.d("testGridView", "mNowViewlist.size() = " + mNowViewList.size());
        for (int i = 0; i < mNowViewList.size(); i++) {
            View viewtemp;
            PropertyValuesHolder pvTop;
            PropertyValuesHolder pvLeft;

            viewtemp = mNowViewList.get(i);
            viewtemp.setX(mDelOriViewLeftList.get(i));
            viewtemp.setY(mDelOriViewTopList.get(i));
            pvTop = PropertyValuesHolder.ofFloat("Y", (float) mDelOriViewTopList.get(i),
                    (float) mNowViewList.get(i).getTop());
            pvLeft = PropertyValuesHolder.ofFloat("X", (float) mDelOriViewLeftList.get(i),
                    (float) mNowViewList.get(i).getLeft());

            ObjectAnimator temp = ObjectAnimator.ofPropertyValuesHolder(viewtemp, pvTop, pvLeft);
            temp.setInterpolator(new DecelerateInterpolator(1.0f));
            temp.setDuration(150);
            temp.setStartDelay(i * 30);

            AnimationList.add(i, temp);
        }

        mDelAniSet.playTogether(AnimationList);

        mDelAniSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mEndDelAniEarly = false;
                mAnimRunning = false;
                mInDeleteAnimation = false;
                mDisableTouchEvent = false;

                mDelPosList.clear();
                mDelOriViewTopList.clear();
                mDelOriViewLeftList.clear();
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

    private void setDelViewLocation() {

        int nowFirstPosition = getFirstVisiblePosition();
        int nowCurChildCount = this.getChildCount();
        boolean nowLastPage = false;
        if (this.getLastVisiblePosition() == this.getAdapter().getCount() - 1)
            nowLastPage = true;

        boolean nowFirstPage = false;
        if (nowFirstPosition == 0)
            nowFirstPage = true;

        View child = null;
        int childCount = getChildCount();
        int diff = 0;

        if (mNowViewList == null)
            mNowViewList = new ArrayList<View>();
        else
            mNowViewList.clear();

        for (int i = 0; i < childCount; ++i) {
            child = getChildAt(i);
            mNowViewList.add(child);
        }

        int childIndex = 1;

        if (mOriLastPage == false) { // original not at last page
            if (nowLastPage == false) { // Case 1
                if (mOriUpperDeleteCount == 0) {
                    android.util.Log.d("HtcGridView", "DeleteAnimation Case 1");
                    // Do nothing
                } else if (mOriUpperDeleteCount >= mOriCurLeftCount) { // Case 3
                    android.util.Log.d("HtcGridView", "DeleteAnimation Case 3");
                    mDelOriViewTopList.clear();
                    mDelOriViewLeftList.clear();
                } else { // Case 2
                    android.util.Log.d("HtcGridView", "DeleteAnimation Case 2");
                    for (int i = 0; i < mOriUpperDeleteCount; i++) {
                        mDelOriViewTopList.remove(0);
                        mDelOriViewLeftList.remove(0);
                    }
                }
            } else {
                if (nowFirstPage == false) {
                    if (mOriUpperDeleteCount == 0) { // Case 4
                        android.util.Log.d("HtcGridView", "DeleteAnimation Case 4 ");
                    } else {
                        if (mOriCurDeleteCount == 0) {
                            if (mOriUpperDeleteCount >= mOriCurLeftCount) { // Case
                                                                            // 9
                                android.util.Log.d("HtcGridView", "DeleteAnimation Case 9 ");
                            } else { // Case 10
                                android.util.Log.d("HtcGridView", "DeleteAnimation Case 10 ");
                            }
                        } else {
                            if (mOriUpperDeleteCount >= mOriCurLeftCount) { // Case
                                                                            // 5
                                android.util.Log.d("HtcGridView", "DeleteAnimation Case 5 ");
                            } else { // Case 6
                                android.util.Log.d("HtcGridView", "DeleteAnimation Case 6 ");
                            }
                        }
                    }
                } else {
                    if (mOriCurDeleteCount == 0) { // Case 11
                        android.util.Log.i("HtcGridView", "DeleteAnimation Case 11 ");
                    } else {
                        if (mOriUpperDeleteCount >= mOriCurLeftCount) { // Case
                                                                        // 7
                            android.util.Log.d("HtcGridView", "DeleteAnimation Case 7 ");
                        } else { // Case 8
                            android.util.Log.d("HtcGridView", "DeleteAnimation Case 8 ");
                        }
                    }
                }

                if (mAllItemFlyIn) { // the count of deleted items is bigger
                                     // than the count of items below the origin
                                     // screen
                    mDelOriViewTopList.clear();
                    mDelOriViewLeftList.clear();
                    for (int i = 0; i < mNowViewList.size(); i++) {
                        child = mNowViewList.get(i);

                        if (child == null)
                            throw new IllegalArgumentException(
                                    "The child view is Null in setDelViewLocation !!! If you have any data changed, please call notifyDataSetChanged");

                        mDelOriViewTopList.add(this.getBottom() + child.getHeight());
                        mDelOriViewLeftList.add(child.getLeft());
                    }
                } else {
                    // add item from the bottom
                    for (int i = 0; i < mOriBelowLeftCount; i++) {
                        childIndex = nowCurChildCount - mOriBelowLeftCount + i;
                        if (childIndex < 0) {
                            childIndex = 0;
                            android.util.Log.d("HtcGridView", "nowCurChildCount = "
                                    + nowCurChildCount + ", mOriBelowLeftCount = "
                                    + mOriBelowLeftCount);
                        }
                        child = mNowViewList.get(childIndex);

                        if (child == null)
                            throw new IllegalArgumentException(
                                    "The child view is Null in setDelViewLocation !!! If you have any data changed, please call notifyDataSetChanged");

                        mDelOriViewTopList.add(this.getBottom() + child.getHeight());
                        mDelOriViewLeftList.add(child.getLeft());
                    }

                    // if the count of OriList bigger than nowViewCountremove
                    // the item from mDelOriViewTopList and mDelOriViewLeftList
                    diff = mDelOriViewTopList.size() - nowCurChildCount;
                    for (int i = 0; i < diff; i++) {
                        mDelOriViewTopList.remove(0);
                        mDelOriViewLeftList.remove(0);
                    }

                    // add item from the top
                    childIndex = 1;
                    diff = nowCurChildCount - mDelOriViewTopList.size();
                    while (nowCurChildCount > mDelOriViewTopList.size()) {
                        child = mNowViewList.get(diff - childIndex);
                        if (child == null)
                            throw new IllegalArgumentException(
                                    "The child view is Null in setDelViewLocation !!! If you have any data changed, please call notifyDataSetChanged");

                        mDelOriViewTopList.add(0, -child.getHeight());
                        mDelOriViewLeftList.add(0, child.getLeft());
                        childIndex++;
                    }
                }
            }
        } else {
            if (mOriUpperDeleteCount == 0) {
                if (mOriCurDeleteCount == 0) {

                } else { // Case 14
                    android.util.Log.d("HtcGridView", "DeleteAnimation Case 14 ");
                }
            } else {
                if (mOriCurDeleteCount == 0) {
                    if (mOriUpperDeleteCount >= mOriCurLeftCount) { // Case 12
                        android.util.Log.d("HtcGridView", "DeleteAnimation Case 12 ");
                        mDelOriViewTopList.clear();
                        mDelOriViewLeftList.clear();
                    } else { // Case 13
                        android.util.Log.d("HtcGridView", "DeleteAnimation Case 13 ");
                        for (int i = 0; i < mOriUpperDeleteCount; i++) {
                            mDelOriViewTopList.remove(0);
                            mDelOriViewLeftList.remove(0);
                        }
                    }
                } else {
                    if (nowFirstPage == false) {
                        if (mOriUpperDeleteCount >= mOriCurLeftCount) { // Case
                                                                        // 15
                            android.util.Log.d("HtcGridView", "DeleteAnimation Case 15 ");
                        } else { // Case 16
                            android.util.Log.d("HtcGridView", "DeleteAnimation Case 16 ");
                        }
                    } else { // Case 17
                        android.util.Log.d("HtcGridView", "DeleteAnimation Case 17 ");
                    }
                }
            }

            // if the count of OriList bigger than nowViewCountremove the item
            // from mDelOriViewTopList and mDelOriViewLeftList
            diff = mDelOriViewTopList.size() - nowCurChildCount;
            for (int i = 0; i < diff; i++) {
                mDelOriViewTopList.remove(0);
                mDelOriViewLeftList.remove(0);
            }

            // add item from the top
            childIndex = 1;
            diff = nowCurChildCount - mDelOriViewTopList.size();
            while (nowCurChildCount > mDelOriViewTopList.size()) {
                child = mNowViewList.get(diff - childIndex);
                if (child == null)
                    throw new IllegalArgumentException(
                            "The child view is Null in setDelViewLocation !!! If you have any data changed, please call notifyDataSetChanged");

                mDelOriViewTopList.add(0, -child.getHeight());
                mDelOriViewLeftList.add(0, child.getLeft());
                childIndex++;
            }
        }

        // add item from the bottom
        diff = mNowViewList.size() - mDelOriViewTopList.size();
        for (int i = 0; i < diff; i++) {
            childIndex = nowCurChildCount - diff + i;
            child = mNowViewList.get(childIndex);

            if (child == null)
                throw new IllegalArgumentException(
                        "The child view is Null in setDelViewLocation !!! If you have any data changed, please call notifyDataSetChanged");

            mDelOriViewTopList.add(this.getBottom() + child.getHeight());
            mDelOriViewLeftList.add(child.getLeft());
        }

        // check the invert count
        int invertCount = 0;
        for (int i = mNowViewList.size() - 1; i >= 0; i--) {
            if (mNowViewList.get(i).getTop() == mDelOriViewTopList.get(i).intValue()) {
                if (mNowViewList.get(i).getLeft() == mDelOriViewLeftList.get(i).intValue()) {
                    mDelOriViewTopList.remove(i);
                    mDelOriViewLeftList.remove(i);
                    mNowViewList.remove(i);
                } else if (mDelOriViewLeftList.get(i).intValue() < mNowViewList.get(i).getLeft()) {
                    invertCount++;
                }
            } else if (mDelOriViewTopList.get(i).intValue() < mNowViewList.get(i).getTop()) {
                invertCount++;
            }
        }

        // change the order
        if (invertCount > 1) { // only one, do not need to change order
            ArrayList<View> tmpViewList = (ArrayList<View>) mNowViewList.clone();
            ArrayList<Integer> tmpOriTopList = (ArrayList<Integer>) mDelOriViewTopList.clone();
            ArrayList<Integer> tmpOriLeftList = (ArrayList<Integer>) mDelOriViewLeftList.clone();
            mNowViewList.clear();
            mDelOriViewTopList.clear();
            mDelOriViewLeftList.clear();
            int tmpPos = 0;
            for (int i = 0; i < tmpViewList.size(); i++) {
                if (i < invertCount) { // change order
                    tmpPos = invertCount - i - 1;
                } else {
                    tmpPos = i;
                }
                mNowViewList.add(tmpViewList.get(tmpPos));
                mDelOriViewTopList.add(tmpOriTopList.get(tmpPos));
                mDelOriViewLeftList.add(tmpOriLeftList.get(tmpPos));
            }
        }
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

    /**
     * Set the listener that will receive notifications every time the list or
     * grid be pulled down.
     *
     * @param onPullDownListener the pull-down listener
     */
    public void setOnPullDownListener(OnPullDownListener onPullDownListener) {
        mListViewUtil.setOnPullDownListener(onPullDownListener);
    }

    @Override
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

    @Override
    public void enableAnimation(int animationType, boolean enable) {
        mListViewUtil.enableAnimation(animationType, enable);
    }

    public void setOnScrollListener(OnScrollListener l) {
        if(mListViewUtil.isUserOnScrollListener(l))
            mListViewUtil.setOnScrollListener(l);
        else
            super.setOnScrollListener(l);
    }

    /**
     * Set the HTC style of the grid view: MODE_GENERIC and MODE_OVERLAY.
     * The corresponding horizontal spacing, vertical spacing and stretch mode
     * will be changed for MODE_GENERIC and MODE_OVERLAY modes.
     * The default mode is MODE_NONE that keeps the original grid view setting
     * (The spacing is 0 and stretch mode is STRETCH_COLUMN_WIDTH).
     *
     * @param mode Either MODE_NONE, MODE_GENERIC or MODE_OVERLAY.
     */
    public void setMode(int mode) {
        if(mode > MODE_OVERLAY || mode < 0) {
            throw new IllegalArgumentException("Invalid mode! Only MODE_NONE, MODE_GENERIC or MODE_OVERLAY is allowed.");
        }
        if(mode == mMode) {
            return;
        }
        switch(mode) {
            case MODE_NONE:
                setHorizontalSpacing(0);
                setVerticalSpacing(0);
                break;
            case MODE_GENERIC:
            case MODE_OVERLAY:
                setHorizontalSpacing(mGap);
                setVerticalSpacing(mGap);
                break;
        }
        setStretchMode(STRETCH_COLUMN_WIDTH);
        mMode = mode;
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

    /**
     * To end current deleting Animator
     * HtcGridView may trigger animation layout error issue when switch the screen orientation
     * User shold call this API in any layout change of HtcListView parent. for example: orientation change invoke the whole screen layout change
     */
    public void endDelAnimator() {
        if (mDelAniSet != null && mDelAniSet.isRunning()) {
            mEndDelAniEarly = true;
            mDelAniSet.end();
        }
    }
}
