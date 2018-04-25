/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.htc.lib1.cc.widget;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.HtcCommonUtil;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.WrapperListAdapter;

import android.widget.HeaderViewListAdapter;

/**
 * A view that shows items in a vertically scrolling list and the items position can be reordered.
 * You must set an {@link android.widget.ImageView} as the last child of the item and set the id
 * of that {@link android.widget.ImageView} as dragger through {@link #setDraggerId(int)}.
 */
public class HtcReorderListView extends HtcListView {

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected static final int DRAG_MODE_REST = 0;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected static final int DRAG_MODE_DRAG = 1;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected static final int DRAG_MODE_WAVE = 2;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected static final int DRAG_MODE_SCROLL = 3;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected static final int INVALID_DRAG_POS = -2;

    private static final boolean DEBUG = false;
    private static final String TAG = "HtcReorderListView";

    // Accessibility strings
    private final String DRAGGABLE_ITEM;
    private final int DRAGGABLE_ITEM_STRING_LENGTH;
    private final String MOVE_ABOVE;
    private final String MOVE_BELOW;

    /*
     * Since the frequency of ACTION_MOVE is very low when user's finger point on screen,
     * we have to post the runnable to smoothScrollBy fixed distance by ourself to scroll
     * the list view more smoothly.
     */
    private final int FAST_SCROLL_DOWN = 0;
    private final int SLOW_SCROLL_DOWN = 1;
    private final int FAST_SCROLL_UP = 2;
    private final int SLOW_SCROLL_UP = 3;
    private int mLastScrollMode = Integer.MIN_VALUE;
    private SmoothScrollRunnable mSmoothScrollRunnable = new SmoothScrollRunnable();

    private View mDragViewWithFrame;
    // A temporary view to store the cache of the drag item view
    private ImageView mDragItemCache;
    // To check if the dragging item is attached to window or not
    @ExportedProperty(category = "CommonControl")
    private boolean mIsAttachedToWindow = false;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected int mDragMode = DRAG_MODE_REST;
    protected int mDividerMode = DRAG_MODE_REST;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    /**
     * At which position is the item currently being dragged. Note that this
     * takes in to account header items.
     */
    private int mDragPos;
    /**
     * At which position was the item being dragged originally
     */
    private int mSrcDragPos = INVALID_DRAG_POS;
    /**
     * At which position was the last dragged item
     */
    private int mLastDragPos;

    /**
     * Y value from on the previous motion event (if any)
     */
    int mLastY;

    @ExportedProperty(category = "CommonControl")
    int mCacheItemPadding[] = new int[4];

    private int mDragPointY;    // at what y offset inside the item did the user grab it
    private int mMotionDownY = Integer.MIN_VALUE;
    private int mCurrentMotionY = 0;
    private int mYOffset;  // the difference between screen coordinates and coordinates in this view
    private int mOrigY;
    private DropListener mDropListener;
    private SeparatorPositionListener mSeparatorPosListener;
    @ExportedProperty(category = "CommonControl")
    private int mUpperBound;
    @ExportedProperty(category = "CommonControl")
    private int mLowerBound;
    @ExportedProperty(category = "CommonControl")
    private int mHeight;
    private Rect mTempRect = new Rect();
    private Bitmap mDragBitmap;
    @ExportedProperty(category = "CommonControl")
    private int mItemHeightNormal = -1;
    @ExportedProperty(category = "CommonControl")
    private int mItemHeightExpanded;
    @ExportedProperty(category = "CommonControl")
    private int mSeparatorHeightNormal = -1;
    private View targetDragger = null;

    // Remove drag view immediately for ACTION_CANCEL
    private boolean removeDragViewImmediately = false;
    // Choreograph the press animation only on action down
    private boolean mIsActionDown = false;
    // TODO: ask designer for the exact number
    @ExportedProperty(category = "CommonControl")
    private int mFrameCornerTopPadding = 0;
    @ExportedProperty(category = "CommonControl")
    private int mFrameCornerBottomPadding = 0;
    @ExportedProperty(category = "CommonControl")
    private int mFrameShadowHeight = 0;
    private Paint mBoundPaint = new Paint();

    // HtcReorderListView's adapter
    DisableItemAdapter mDisableAdapter;
    // The DataSetObserver to register on AP's adapter, to set the flag on
    // when notifyDataSetChanged called
    ReorderDataSetObserver mReorderDataSetObserver;

    /*
     * Set on/off such flag to know if AP triggered notifyDataSetChanged or not
     */
    // flag on when DropListener.drop() callback, flag off at the end of onLayout()
    private boolean mIsDropCallbacked = false;
    // flag on when AP called notifyDataSetChanged, flag off at the end of onLayout()
    private boolean mIsNotifyDataSetChanged = false;

    /*
     * Since item's height growing during reordering animation, the top/bottom divider on
     * 9-patch background asset will be covered by app background color. To workaround
     * such limitation, need to draw divider extra for synchronizing UI look.
     */
    // The divider drawable
    private Drawable mDividerDrawable;
    // Represents top/bottom divider height
    @ExportedProperty(category = "CommonControl")
    private final int mFrameDividerHeight;
    // The position to draw top divider
    private int mYTopDivider = INVALID_DRAG_POS;
    // The position to draw bottom divider
    private int mYBottomDivider = INVALID_DRAG_POS;

    //animation runnable
    private WaveRunnable mWaveRunnable;

    private Drawable imageDragger = null;

    @ExportedProperty(category = "CommonControl", resolveId = true)
    private int mDraggerId = R.id.img_1x1;
    @ExportedProperty(category = "CommonControl")
    private int mOverlayColor;
    @ExportedProperty(category = "CommonControl")
    private int mListItemMargin = 0;
    @ExportedProperty(category = "CommonControl")
    private final float displayDensity;
    OnScrollListener mOnScrollListener;
    private AccessibilityManager mAccessibilityManager;
    private static final int DRAG_DISTANCE = 10;

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @param context The Context the HtcReorderListView is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the HtcReorderListView.
     */
    public HtcReorderListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDragViewWithFrame = inflater.inflate(R.layout.htc_reorder_list_drag_item, null);

        mDragViewWithFrame.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View arg0) {
                mIsAttachedToWindow = true;
            }
            @Override
            public void onViewDetachedFromWindow(View arg0) {
                mIsAttachedToWindow = false;
            }
        });

        mWindowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mOverlayColor = HtcCommonUtil.getCommonThemeColor(context, R.styleable.ThemeColor_overlay_color);

        TypedArray ta2 = context.obtainStyledAttributes(attrs,
                R.styleable.HtcReorderListView,
                R.attr.htcReorderListViewStyle, R.style.HtcReorderListViewStyle);

        imageDragger = (Drawable) ta2.getDrawable(R.styleable.HtcReorderListView_android_drawable);

        ta2.recycle();

        mAccessibilityManager = (AccessibilityManager) context.getSystemService(Service.ACCESSIBILITY_SERVICE);

        android.content.res.Resources res = context.getResources();

        mListItemMargin = res.getDimensionPixelOffset(R.dimen.margin_l);

        // Initializes the divider drawables
        mDividerDrawable = res.getDrawable(R.drawable.common_list_divider);
        if (mDividerDrawable != null) {
            mFrameDividerHeight = mDividerDrawable.getIntrinsicHeight();
        } else {
            mFrameDividerHeight = 0;
        }

        Drawable rearrange = res.getDrawable(R.drawable.common_rearrange_frame);
        if (null != rearrange) {
            mFrameShadowHeight = (rearrange.getIntrinsicHeight() - 1) / 2;
        }

        // Accessibility strings
        DRAGGABLE_ITEM = res.getString(R.string.st_draggable_item);
        DRAGGABLE_ITEM_STRING_LENGTH = DRAGGABLE_ITEM.length();
        MOVE_ABOVE = res.getString(R.string.va_move_above);
        MOVE_BELOW = res.getString(R.string.va_move_below);

        displayDensity = res.getDisplayMetrics().density;

        mOnScrollListener = new OnScrollListener() {
            @Override
            public void onScroll(android.widget.AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                if (mSL != null)
                    mSL.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }

            @Override
            public void onScrollStateChanged(android.widget.AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
//                mHtcReorderListView.setTouchMode(scrollState);
                mTouchMode = scrollState;
                if (mSL != null)
                    mSL.onScrollStateChanged(view, scrollState);
            }
        };
        super.setOnScrollListener(mOnScrollListener);

        setDividerController(new IDividerController(){
            @Override
            public int getDividerType(int position) {
                if (mDragMode == DRAG_MODE_REST)
                    mDividerMode = DRAG_MODE_REST;
                else
                    mDividerMode = mDividerMode > mDragMode ? mDividerMode : mDragMode;

                int expand = 0;
                if (mDragPos >= mSrcDragPos) {
                    expand = mDragPos;
                } else {
                    expand = mDragPos - 1;
                }

                if(mDividerMode >= DRAG_MODE_SCROLL && position == mSrcDragPos && position != expand)
                    return IDividerController.DIVIDER_TYPE_NONE;
                else
                    return IDividerController.DIVIDER_TYPE_NORAML;
            }
    });
    }

    private int mTouchMode = 0;
    OnScrollListener mSL = null;
    public void setOnScrollListener(OnScrollListener sl) {
        if(mOnScrollListener == null && sl != null) {
            super.setOnScrollListener(sl);
        } else {
            mSL = sl;
        }
    }
//    public void setTouchMode(int scrollstate) {
//        mTouchMode = scrollstate;
//    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (mDropListener != null) {
            mMotionDownY = (int) ev.getY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (DEBUG) Log.d(TAG,"onInterceptTouchEvent() DOWN dragmode = " + mDragMode);

                    // Do not allow to trigger another window (disable the drag event) if item is still attached to window
                    if (mIsAttachedToWindow) {
                        break;
                    }

                    if (mTouchMode == OnScrollListener.SCROLL_STATE_FLING/*HtcAbsListView.TOUCH_MODE_FLING*/) {
                        break;
                    }

                    if (isRunningExitAnim()) {
                        if (DEBUG) Log.d(TAG, "onInterceptTouchEvent.DOWN still running exit anim mDragMode = " + mDragMode);
                        break;
                    }

                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    int rawX = (int) ev.getRawX();
                    int itemnum = pointToPosition(x, y);
                    if (itemnum == android.widget.AdapterView.INVALID_POSITION) {
                        break;
                    }

                    // (Separator case) disable the drag behavior when item is disabled.
                    if (isItemDisabled(itemnum)) {
                        break;
                    }

                    View item = (View) getChildAt(itemnum - getFirstVisiblePosition());
                    mOrigY = y;
                    mDragPointY = y - item.getTop();
                    mYOffset = ((int)ev.getRawY()) - y;
                    targetDragger = item.findViewById(mDraggerId);
                    if (targetDragger == null)
                        break;
                    //get dragger's exact location, track touch event based on its location.
                    int[] draggerLoc = {0, 0};
                    int[] draggerLoc2 = {0, 0};
                    targetDragger.getLocationInWindow(draggerLoc);
                    targetDragger.getLocationOnScreen(draggerLoc2);
                    if (DEBUG) {
                        Log.d(TAG, "onInterceptTouchEvent() draggerLoc=" + draggerLoc[0] +
                                ", " + draggerLoc[1] +
                                "; motion event x=" + x + " y=" + y);
                    }
                    //start dragging if the drag icon is touched.
                    int extand = 10;
                    if (rawX > draggerLoc2[0] - extand &&
                                    rawX < draggerLoc2[0] + targetDragger.getWidth() + extand) {
                        imageDragger.setColorFilter(mOverlayColor, PorterDuff.Mode.SRC_ATOP);
                        ((ImageView)targetDragger).setImageDrawable(imageDragger);

                        /*
                         * Accessibility starts
                         * Perform haptic feedback when dragger has been touched.
                         */
                        if (mAccessibilityManager != null && mAccessibilityManager.isEnabled()) {
                            if (!targetDragger.isHapticFeedbackEnabled()) {
                                targetDragger.setHapticFeedbackEnabled(true);
                            }
                            targetDragger.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        }
                        /*
                         * Accessibility ends
                         */

                        mDragMode = DRAG_MODE_DRAG;
                        mDragPos = itemnum;
                        mSrcDragPos = mDragPos;
                        mLastDragPos = mSrcDragPos;

                        mLastY = Integer.MIN_VALUE;

                        mHeight = getHeight();
                        //XXX: assume all item have the same height and paddings.
                        mItemHeightNormal = item.getHeight();
                        mItemHeightExpanded = mItemHeightNormal + mItemHeightNormal;
                        mCacheItemPadding[0] = item.getPaddingLeft();
                        mCacheItemPadding[1] = item.getPaddingTop();
                        mCacheItemPadding[2] = item.getPaddingRight();
                        mCacheItemPadding[3] = item.getPaddingBottom();

                        adjustScrollBounds();
                        item.destroyDrawingCache();
                        item.buildDrawingCache();
                        // Create a copy of the drawing cache so that it does not get recycled
                        // by the framework when the list tries to clean up memory
                        Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());

                        if (DEBUG) {
                            Log.d(TAG, "onInterceptTouchEvent() DOWN start dragging pos = "
                                            + mSrcDragPos);
                        }
                        // startDragging pollute a temp view in window.
                        startDragging(bitmap, y);
                        mIsActionDown = true;
                        return true;
                    }
                    break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    boolean isRunningExitAnim() {
        return mWaveRunnable != null && mWaveRunnable.mDraggingWindow != null;
    }

    /*
     * pointToPosition() doesn't consider invisible views, but we
     * need to, so implement a slightly different version.
     */
    private int myPointToPosition(int x, int y) {
        if (y < 0) {
            // when dragging off the top of the screen, calculate position
            // by going back from a visible item
            int pos = myPointToPosition(x, y + mItemHeightNormal);
            if (pos >= 0) {
                return pos;
            }
        }

        Rect frame = mTempRect;
        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            child.getHitRect(frame);
            if (frame.contains(x, y)) {
                return getFirstVisiblePosition() + i;
            }
        }
        return INVALID_POSITION;
    }

    private int getItemForPosition(int x, int y) {
        int adjY = y;
        if (mLastY != Integer.MIN_VALUE && mLastY >= y) {
            adjY = y - mDragPointY - mItemHeightNormal / 2;
        } else if (mDragPos == 0 && mSrcDragPos != 0) {
            // because of the first item movement is via padding
            // when mLastY < y still need to offset the y to
            // to keep the finger on the same index
            adjY = y - mDragPointY - mItemHeightNormal / 2;
        }

        int pos = myPointToPosition(x, adjY);

        if (pos >= 0 && adjY >= 0) {
            if (pos < mSrcDragPos) {
                pos++;
            }
        } else if (pos <= 1 && adjY < 0) {
            //XXX: dirty way to solve the top item not moving
            // this shouldn't happen anymore now that myPointToPosition deals
            // with this situation
            pos = 0;
        }

        if (DEBUG) {
            Rect frame = new Rect();
            View child = getChildAt(pos - getFirstVisiblePosition());
            if (child != null) child.getHitRect(frame);
            Log.d(TAG, "y=" + y + " adjY=" + adjY +
                            " mLastY=" + mLastY +
                            " hit item " + pos +
                            " mSrcDragPos=" + mSrcDragPos +
                            " item rect=" + frame +
                            " mYOffset=" + mYOffset);
        }

        return pos;
    }

    /*
     * bounds are local scope, which top = 0, bottom = height.
     */
    private void adjustScrollBounds() {
        int range = mHeight / 10;
        mUpperBound = getPaddingTop() + range;
        mLowerBound = mHeight - getPaddingBottom() - range;
    }

    /*
     * Restore size and visibility for all listitems
     */
    private void unExpandViews(boolean deletion) {
        //TODO(Alice): when drag from index smaller to index higher, and the
        // source position is out of screen, drop will cause list shift.
        // Due to the list item height restore, scroll it back
        for (int i = 0;; i++) {
            View v = getChildAt(i);
            if (v == null) {
                try {
                    layoutChildren(); // force children to be recreated where needed
                    v = getChildAt(i);
                } catch (IllegalStateException ex) {
                    // layoutChildren throws this sometimes, presumably because we're
                    // in the process of being torn down but are still getting touch
                    // events
                    Log.w(TAG, "The content of the adapter has changed but "
                        + "HtcReorderListView did not receive a notification.");
                }
                if (v == null) {
                    return;
                }
            }
            if (v instanceof HtcListItem) {
                HtcListItem viewTarget = (HtcListItem) v;
                if (viewTarget.getHeight() != mItemHeightNormal) {
                    viewTarget.setTopSpace(0);
                    viewTarget.setBottomSpace(0);
                }
            // (Separator case)
            } else if (v instanceof HtcListItemSeparator) {
                HtcListItemSeparator viewTarget = (HtcListItemSeparator) v;
                if (viewTarget.getHeight() != mSeparatorHeightNormal) {
                    viewTarget.setTopSpace(0);
                    viewTarget.setBottomSpace(0);
                }
            } else {
                ViewGroup.LayoutParams params = v.getLayoutParams();
                if (params.height != mItemHeightNormal) {
                    params.height = mItemHeightNormal;
                    v.setLayoutParams(params);
                }
                // setPadding and setVisibility will only be done if changed,
                // this is handled in View.java
                v.setPadding(mCacheItemPadding[0], mCacheItemPadding[1],
                                mCacheItemPadding[2], mCacheItemPadding[3]);
            }
            v.setVisibility(View.VISIBLE);
        }
    }

    private View drag = null;

    /* Adjust visibility and size to make it appear as though
     * an item is being dragged around and other items are making
     * room for it:
     * If dropping the item would result in it still being in the
     * same place, then make the dragged listitem's size normal,
     * but make the item invisible.
     * Otherwise, if the dragged listitem is still on screen, make
     * it as small as possible and expand the item below the insert
     * point.
     * If the dragged item is not on screen, only expand the item
     * below the current insertpoint.
     */
    private void expandItem() {
        if (mWaveRunnable != null) {
            // this is the first dragging, no animation is needed
//            if (mLastDragPos == mDragPos && mDragPos == mSrcDragPos) {
            if (mDragPos == mSrcDragPos) {
                drag = getChildAt(mSrcDragPos - getFirstVisiblePosition());
                if (drag != null) {
                    if (mIsActionDown) {
                        /*
                         * Fisherson_Lin: To prevent the twinkling effect, add a property animation
                         * overlapping with fade in window animation. The drag view will be INVISIBLE
                         * at the end of property animation.
                         */
                        drag.setPivotX(drag.getWidth() * 1.0f);
                        drag.setPivotY(drag.getHeight() * 0.5f);
                        drag.setScaleX(1.001f);
                        drag.setScaleY(1.01f);

                        mIsActionDown = false;
                    } else {
                        drag.setVisibility(INVISIBLE);
                    }
                }
            }

            MovingItem item;
            int expand;
            int shrink;

            if (mDragPos >= mSrcDragPos) {
                expand = mDragPos;
            } else {
                expand = mDragPos - 1;
            }

            // get space back from previous expanding item
            shrink = (mLastDragPos < mSrcDragPos) ? mLastDragPos - 1 : mLastDragPos;

            if (shrink == expand) {
                return;
            }

            if (DEBUG) Log.d(TAG, "expandItem() mWaveRunnable != null mDragPos=" + mDragPos +
                            " mScrDragPos=" + mSrcDragPos +
                            " mLastDragPos=" + mLastDragPos +
                            " shirnk=" + shrink + " expand=" + expand);

            item = new MovingItem(shrink, expand);
            mWaveRunnable.mItems.add(item);
            post(mWaveRunnable);

        } else {
            // without animation
            int childnum = mDragPos - getFirstVisiblePosition();
            if (mDragPos < mSrcDragPos) {
              childnum--;
            }

            if (DEBUG) Log.d(TAG, "doExpansion() mDragPos=" + mDragPos +
                            " mScrDragPos=" + mSrcDragPos +
                            " childnum=" + childnum);

            View firstDrag = getChildAt(mSrcDragPos - getFirstVisiblePosition());

            for (int i = 0;; i++) {
                View vv = getChildAt(i);
                if (vv == null) {
                    break;
                }

                int height = mItemHeightNormal;
                int visibility = View.VISIBLE;

                if (vv.equals(firstDrag)) {
                    // processing the item that is being dragged
                    if (mDragPos == mSrcDragPos || getPositionForView(vv) == getCount() - 1) {
                        // hovering over the original location
                        visibility = View.VISIBLE;
                    } else {
                        // not hovering over it
                        // Ideally the item would be completely gone, but neither
                        // setting its size to 0 nor settings visibility to GONE
                        // has the desired effect.
                        height = 1;
                        visibility = View.VISIBLE;
                    }
                } else if (i == childnum) {
                    if (mDragPos >= 0 && mDragPos <= getCount() - 1) {
                        height = mItemHeightExpanded - 1;
                    }
                } else if (i == 0 && childnum == -1) {
                    if (mSrcDragPos != 0) {
                        height = mItemHeightExpanded - 1;
                    }
                }

                // when childnum = -1, the dragger is dragged over top
                if (i == 0 && mSrcDragPos != 0 && childnum == -1) {
                    vv.setPadding(mCacheItemPadding[0], mCacheItemPadding[1] + mItemHeightNormal,
                                    mCacheItemPadding[2], mCacheItemPadding[3]);
                    height = mItemHeightExpanded - 1;
                } else {
                    vv.setPadding(mCacheItemPadding[0], mCacheItemPadding[1],
                                    mCacheItemPadding[2], mCacheItemPadding[3]);
                }

                ViewGroup.LayoutParams params = vv.getLayoutParams();
                params.height = height;
                vv.setLayoutParams(params);
                vv.setVisibility(visibility);
            }
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int childcount = getChildCount();
        int first = getFirstVisiblePosition();
        if (mItemHeightNormal == -1) {
            return;
        }
        for (int i = 0; i < childcount; i++) {
            View child = getChildAt(i);
            if (child == null) continue;
            int height = child.getHeight();

            Rect r = mTempRect;
            r.setEmpty();
            Rect r2 = null;

            if(height == 1) {
                mBoundPaint.setColor(Color.WHITE);
                //Log.d(TAG,"dragView.getLeft()" + child.getLeft() + "dragView.getBottom()" + child.getBottom());
                if(i != 0)
                    canvas.drawLine(child.getLeft(), child.getBottom(), child.getRight(), child.getBottom() , mBoundPaint);
                continue;
            }

            if (i == mSrcDragPos - first) {
                // the rect of the item in mSrcDragPos
                child.getHitRect(r);
            // (Separator case)
            } else if (child instanceof HtcListItemSeparator) {
                if (mSeparatorHeightNormal == -1) {
                    // the first moment to assign separator height
                    mSeparatorHeightNormal = height;
                    continue;
                }
                if (height > mSeparatorHeightNormal) {
                    child.getHitRect(r);
                    if (mSrcDragPos != 0 && i == 0) {
                        int paddingTop = ((HtcListItemSeparator) child).getTopSpace();

                        if (paddingTop != 0) {
                            r.bottom = paddingTop;
                            mYTopDivider = r.bottom;
                            // r2 is for item 0 only, since item 0 is moved by changing padding
                            // r2 is the bottom black when moving item 0
                            int extra = height - mSeparatorHeightNormal;
                            if (extra > paddingTop) {
                                r2 = new Rect();
                                child.getHitRect(r2);
                                r2.top = r2.bottom - (extra - paddingTop);
                            }
                        } else {
                            r.top += mSeparatorHeightNormal;
                            mYBottomDivider = r.top;
                        }
                    // Normal idle case, shift rect top to mItemHeightNormal
                    } else {
                        r.top += mSeparatorHeightNormal;
                        mYBottomDivider = r.top;
                    }
                }
            } else if (height > mItemHeightNormal) {
                child.getHitRect(r);
                if (mSrcDragPos != 0 && i == 0) {
                    int paddingTop;
                    if (child instanceof HtcListItem) {
                        paddingTop = ((HtcListItem) child).getTopSpace();
                    } else {
                        paddingTop = child.getPaddingTop();
                    }
                    if (paddingTop != 0) {
                        r.bottom = paddingTop;
                        mYTopDivider = r.bottom;
                        // r2 is for item 0 only, since item 0 is moved by changing padding
                        // r2 is the bottom black when moving item 0
                        int extra = height - mItemHeightNormal;
                        if (extra > paddingTop) {
                            r2 = new Rect();
                            child.getHitRect(r2);
                            r2.top = r2.bottom - (extra - paddingTop);
                        }
                    } else {
                        r.top += mItemHeightNormal;
                        mYBottomDivider = r.top;
                    }
                // Normal idle case, shift rect top to mItemHeightNormal
                } else {
                    r.top += mItemHeightNormal;
                    mYBottomDivider = r.top;
                }
            }

            if (!r.isEmpty()) {
                Path path = new Path();
                path.addRect(new RectF(r), Path.Direction.CW);
                if (r2 != null) {
                    path.addRect(new RectF(r2), Path.Direction.CW);
                }
                if (mYBottomDivider != INVALID_DRAG_POS) {
                    if (mDividerDrawable != null) {
                        if (child instanceof HtcListItemSeparator)
                            mDividerDrawable.setBounds(0, mYBottomDivider - mFrameDividerHeight, canvas.getWidth(), mYBottomDivider);
                        else
                            mDividerDrawable.setBounds(mListItemMargin, mYBottomDivider - mFrameDividerHeight, canvas.getWidth() - mListItemMargin, mYBottomDivider);
                        mDividerDrawable.draw(canvas);
                    }
                    mYBottomDivider = INVALID_DRAG_POS;
                }
                if (mYTopDivider != INVALID_DRAG_POS) {
                    if (mSrcDragPos != 0 && i == 0 && child != null && !(child instanceof HtcListItemSeparator)) {
                        mDividerDrawable.setBounds(mListItemMargin, mYTopDivider, canvas.getWidth() - mListItemMargin, mYTopDivider + mFrameDividerHeight);
                        mDividerDrawable.draw(canvas);
                    }
                    mYTopDivider = INVALID_DRAG_POS;
                }
            } else {
                mYBottomDivider = INVALID_DRAG_POS;
                mYTopDivider = INVALID_DRAG_POS;
            }
            //drawBounds(canvas);
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /* draw scroll region for debugging*/
    void drawBounds(Canvas canvas) {
        //for printing the bounds
        int w = getWidth();
        mBoundPaint.setColor(Color.BLUE);
        canvas.drawLine(0, mUpperBound, w, mUpperBound + 1, mBoundPaint);

        mBoundPaint.setColor(Color.RED);
        int bound = mUpperBound / 2;
        canvas.drawLine(0, bound, w, bound + 1, mBoundPaint);

        mBoundPaint.setColor(Color.BLUE);
        canvas.drawLine(0, mLowerBound, w, mLowerBound + 1, mBoundPaint);

        mBoundPaint.setColor(Color.RED);
        bound = (mHeight + mLowerBound) / 2;
        canvas.drawLine(0, bound, w, bound + 1, mBoundPaint);
    }

    /*
     * The action flow when user triggers ACTION_UP from a dragging window
     * 1. MotionEvent.ACTION_UP
     * 2. Instantiate the mWaveRunnable.mDraggingWindow and post the mWaveRunnable to run
     * 3. When mDuration(160ms) of the mWaveRunnable ends,
     *      i. endWave() -> mDraggingWindow = null
     *      ii. forceDrop()
     * 4. drop(from, to) callback (to AP owner), and Adapter.notifyDataSetChanged() should be triggered
     * 5. stopDragging() called in onLayout()
     * 6. animatorUpScale setTarget to targetView and animatorUpScale.start()
     * 7. Invalid the variables(mDragPos, mDragMode, ...)
     * 8. mWindowManager.removeView(mDragViewWithFrame) at 10% of onAnimationUpdate()
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (mDropListener != null && mDragMode != DRAG_MODE_REST) {
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_UP:
                     //TODO: process drop when dragging window done animation
                    if (mWaveRunnable == null) {
                        mWaveRunnable = new WaveRunnable();
                    }
                    int[] destLoc = new int[2];
                    View destView = null;
                    if (mSrcDragPos > mDragPos) {
                        if(mDragPos != getFirstVisiblePosition())
                            destView = getChildAt(mDragPos - 1 - getFirstVisiblePosition());
                        else
                            destView = getChildAt(mDragPos - getFirstVisiblePosition());
                    } else {
                        destView = getChildAt(mDragPos - getFirstVisiblePosition());
                    }
                    mWindowManager.updateViewLayout(mDragViewWithFrame, mWindowParams);
                    if (destView != null) {
                        destView.getLocationOnScreen(destLoc);

                        int distOffset;
                        if (mSrcDragPos > mDragPos) {
                            if (mDragPos != getFirstVisiblePosition()) {
                                distOffset = mItemHeightNormal;
                                if (getChildAt(mDragPos - 1 - getFirstVisiblePosition()) instanceof HtcListItemSeparator) {
                                    distOffset = mSeparatorHeightNormal;
                                }
                            } else {
                                distOffset = 0;
                            }
                        } else {
                            distOffset = destView.getHeight() - mItemHeightNormal;
                        }


                        int dist = mWindowParams.y + mFrameShadowHeight - (destLoc[1] + distOffset);
                        Log.d(TAG, "distance for dragging window = " + dist + " mDragPos=" + mDragPos +
                                        " destLoc.y=" + destLoc[1] +
                                        " window y = " + mWindowParams.y);
                        mWaveRunnable.mDraggingWindow = new DragWindowMove(dist);
                        mWindowManager.updateViewLayout(mDragViewWithFrame, mWindowParams);
                        post(mWaveRunnable);
                    } else {
                        Log.w(TAG, "onTouchEvent UP/CANCEL destView = null");
                        forceDrop();
                    }
                    break;

                case MotionEvent.ACTION_CANCEL:
                    removeDragViewImmediately = true;
                    forceDrop();
                    break;

                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    // waverunnable is running exit animation.
                    if (isRunningExitAnim()) {
                        Log.e(TAG, "onTouchEvent.MOVE still running exit anim mDragMode = " + mDragMode);
                        break;
                    }

                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    mCurrentMotionY = (int) ev.getY();
                    if (Math.abs(y - mLastY) < 3 && !shouldScroll(y)) {
                        break;
                    }

                    if (mWaveRunnable == null) {
                        mWaveRunnable = new WaveRunnable();
                    }

                    if (mDragMode >= DRAG_MODE_DRAG) {
                    // drag the draging window around
                        dragView(x, y);

                        int itemnum = getItemForPosition(mTempRect.centerX(), y);

                        if (y > mHeight && itemnum == -1) {
                            itemnum = getLastVisiblePosition();
                        }

                        //Log.d(TAG, "onTouchEvent itemnum=" + itemnum);

                        if (itemnum >= 0) {
//                            setOverFlingEnabled(false);
                            updateStateOrScroll(x, y);

                            if (action == MotionEvent.ACTION_DOWN || itemnum != mDragPos) {
                                // dragger is not in scroll zone, expand items
                                if (mDragMode < DRAG_MODE_SCROLL && !shouldScroll(y)) {
                                    if (mDragPos < 0) {
                                        // we were previously in scroll mode, make sure every
                                        // item is in the right size
                                        if (DEBUG) Log.d(TAG, "back from scroll mode, unexpand items");
                                        unExpand();
                                    }
                                    mLastDragPos = mDragPos;
                                    mDragPos = itemnum;
                                    expandItem();
                                } else if (mDragMode < DRAG_MODE_SCROLL) {
                                    if (DEBUG) Log.d(TAG, "should scroll but waiting for wave to finish");
                                    mWaveRunnable.mItems.clear();
                                    unExpand();
                                    mDragPos = INVALID_DRAG_POS;
                                } else {
                                    if (DEBUG) Log.d(TAG, "dragpos to invalid position");
                                    // dragger is in scroll zone but still running animation
                                    mDragPos = INVALID_DRAG_POS;
                                }
                            } else if (itemnum == mDragPos && mDragMode == DRAG_MODE_SCROLL) {
                                if (DEBUG) Log.d(TAG, "itemnum == mDragPos && mDragMode == DRAG_MODE_SCROLL");
                                mDragPos = INVALID_DRAG_POS;
                            }
                        }

                        mLastY = y;
                    }
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    void updateStateOrScroll(int x, int y) {
        // update drag mode
        if (mDragMode == DRAG_MODE_WAVE && mWaveRunnable.mItems.isEmpty()) {
            if (DEBUG) Log.d(TAG, " finish animation, to drag mode");
            mDragMode = DRAG_MODE_DRAG;
        } else if (!mWaveRunnable.mItems.isEmpty()) {
            mDragMode = DRAG_MODE_WAVE;
        }

        if (mDragMode == DRAG_MODE_WAVE) {
            // do nothing
        } else if (scrollList(x, y)) {
            mDragMode = DRAG_MODE_SCROLL;
        } else {
            mDragMode = DRAG_MODE_DRAG;
        }
    }

    boolean shouldScroll(int y) {
        int speed = 0;
        if (y > mLowerBound) {
            int threshold = (mLowerBound + mHeight) / 2;
            // scroll the list up a bit
            if (getLastVisiblePosition() < getCount() - 1) {
                speed = y > threshold ? 24 : 8;
            }
        } else if (y < mUpperBound) {
            // scroll the list down a bit
            speed = y < mUpperBound / 2 ? -24 : -8;
            int top = getPaddingTop();
            if (getFirstVisiblePosition() == 0
                    && getChildAt(0).getTop() >= top) {
                // if we're already at the top, don't try to scroll, because
                // it causes the framework to do some extra drawing that messes
                // up our animation
                speed = 0;
            }
        }

        if (speed != 0) {
            // prevent for item focus changing in dragging window (scrolling)
//            this.requestAccessibilityFocus();
            Log.d(TAG, "should scroll");
            return true;
        }
        return false;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mWaveRunnable != null) {
            mWaveRunnable.endWave();
        }
        forceDrop();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    // If the finger is above upper bound, scroll the list down, vice versa.
    // By moving the central item's top to a specific location to accomplish scrolling.
    private boolean scrollList(int x, int y) {
        int scrollMode = Integer.MIN_VALUE;
        if (y > mLowerBound) {
            int threshold = (mLowerBound + mHeight) / 2;
            // scroll the list up a bit
            if (getLastVisiblePosition() < getCount() - 1) {
                scrollMode = y > threshold ? FAST_SCROLL_DOWN : SLOW_SCROLL_DOWN;
            } else {
                removeCallbacks(mSmoothScrollRunnable);
            }
        } else if (y < mUpperBound) {
            // scroll the list down a bit
            scrollMode = y < mUpperBound / 2 ? FAST_SCROLL_UP : SLOW_SCROLL_UP;
            int top = getPaddingTop();
            if (getFirstVisiblePosition() == 0
                    && getChildAt(0).getTop() >= top) {
                // if we're already at the top, don't try to scroll, because
                // it causes the framework to do some extra drawing that messes
                // up our animation
                scrollMode = Integer.MIN_VALUE;
                removeCallbacks(mSmoothScrollRunnable);
            }
        } else {
            removeCallbacks(mSmoothScrollRunnable);
        }

        if (scrollMode != Integer.MIN_VALUE) {
            // only rebuild the runnable when scrollMode has changed
            if (scrollMode != mLastScrollMode) {
                removeCallbacks(mSmoothScrollRunnable);
                mLastScrollMode = scrollMode;
                postOnAnimation(mSmoothScrollRunnable);
            }
            return true;
        } else {
            mLastScrollMode = scrollMode;
        }
        return false;
    }

    private class SmoothScrollRunnable implements Runnable {

        @Override
        public void run() {
            switch(mLastScrollMode) {
            case FAST_SCROLL_DOWN:
                unExpand();
                if (isReadyToOverScroll())
                    smoothScrollBy((int) (12 * displayDensity), 30);
                break;
            case SLOW_SCROLL_DOWN:
                unExpand();
                if (isReadyToOverScroll())
                    smoothScrollBy((int) (4 * displayDensity), 30);
                break;
            case FAST_SCROLL_UP:
                unExpand();
                if (isReadyToOverScroll())
                    smoothScrollBy((int) (-12 * displayDensity), 30);
                break;
            case SLOW_SCROLL_UP:
                unExpand();
                if (isReadyToOverScroll())
                    smoothScrollBy((int) (-4 * displayDensity), 30);
                break;
            default:
                return;
            }

            final int childCount = getChildCount();
            if (childCount == 0) {
                return;
            }

            final int firstTop = getChildAt(0).getTop();
            final int lastBottom = getChildAt(childCount - 1).getBottom();

//            final Rect listPadding = mListPadding;
            final int firstPosition = getFirstVisiblePosition();

            final boolean cannotScrollDown = (firstPosition == 0 &&
                    firstTop >= getListPaddingTop());
            final boolean cannotScrollUp = (firstPosition + childCount == getCount() &&
                    lastBottom <= mHeight - getListPaddingBottom());

            if (mLastScrollMode == FAST_SCROLL_UP && cannotScrollDown) {
                MotionEvent moveToTop = MotionEvent.obtain(0, 0, MotionEvent.ACTION_MOVE, 0, -mYOffset, 0);
                onTouchEvent(moveToTop);
                moveToTop.recycle();
            }

            if (mLastScrollMode == FAST_SCROLL_DOWN && cannotScrollUp) {
                MotionEvent moveToBottom = MotionEvent.obtain(0, 0, MotionEvent.ACTION_MOVE, 0, mHeight+mYOffset, 0);
                onTouchEvent(moveToBottom);
                moveToBottom.recycle();
            }

            postOnAnimation(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addHeaderView(View v, Object data, boolean isSelectable) {
        Log.e(TAG, "HtcReorderListView cannot add HeaderView.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFooterView(View v, Object data, boolean isSelectable) {
        Log.e(TAG, "HtcReorderListView cannot add FooterView.");
    }

    private boolean isReadyToOverScroll() {
        final Adapter adapter = this.getAdapter();
        if (null == adapter || adapter.isEmpty()) {
            return false;
        } else {
            if (mCurrentMotionY < mMotionDownY && mCurrentMotionY < this.getListPaddingTop() + mItemHeightNormal) {
                if (getFirstVisiblePosition() != 0)
                    return true;
                View firstVisibleChild = this.getChildAt(0);
                if (firstVisibleChild != null) {
                    return firstVisibleChild.getTop() < this.getListPaddingTop();
                }
                return false;
            } else if (mCurrentMotionY > mMotionDownY && mCurrentMotionY > this.getHeight() - this.getListPaddingBottom() - mItemHeightNormal) {
                if (getLastVisiblePosition() != adapter.getCount() - 1)
                    return true;
                View lastVisibleChild = this.getChildAt(this.getChildCount() - 1);
                if (lastVisibleChild != null) {
                    return lastVisibleChild.getBottom() > this.getHeight() - this.getListPaddingBottom();
                }
                return false;
            } else {
                return false;
            }
        }
    }

    void unExpand() {
        int first = getFirstVisiblePosition();
        for (int i = 0;; i++) {
            View v = getChildAt(i);
            if (v == null) {
                return;
            }
            if (v instanceof HtcListItem) {
                HtcListItem targetView = (HtcListItem) v;
                if (first + i == mSrcDragPos) {
                    if (targetView.getTopSpace() != 1) {
                        targetView.setTopSpace(1 - mItemHeightNormal);
                        targetView.setBottomSpace(0);
                        v.setVisibility(View.INVISIBLE);
                    }
                } else if (v.getHeight() != mItemHeightNormal) {
                    targetView.setTopSpace(0);
                    targetView.setBottomSpace(0);
                    v.setVisibility(View.VISIBLE);
                }
            } else if (v instanceof HtcListItemSeparator) {
                HtcListItemSeparator targetView = (HtcListItemSeparator) v;
                if (first + i == mSrcDragPos) {
                    if (targetView.getTopSpace() != 1) {
                        targetView.setTopSpace(1 - mItemHeightNormal);
                        targetView.setBottomSpace(0);
                        v.setVisibility(View.INVISIBLE);
                    }
                } else if (v.getHeight() != mItemHeightNormal) {
                    targetView.setTopSpace(0);
                    targetView.setBottomSpace(0);
                    v.setVisibility(View.VISIBLE);
                }
            } else {
                ViewGroup.LayoutParams params = v.getLayoutParams();
                if (first + i == mSrcDragPos) {
                    if (params.height != 1) {
                        params.height = 1;
                        v.setLayoutParams(params);
                        v.setVisibility(View.INVISIBLE);
                    }
                } else if (params.height != mItemHeightNormal) {
                    if (DEBUG) Log.i(TAG, " restore child height to normal @" + i);
                    params.height = mItemHeightNormal;
                    v.setLayoutParams(params);
                    v.setVisibility(View.VISIBLE);
                }
                // setPadding and setVisibility will only be done if changed,
                // this is handled in View.java
                v.setPadding(mCacheItemPadding[0], mCacheItemPadding[1],
                                mCacheItemPadding[2], mCacheItemPadding[3]);
            }
        }
    }

    private void startDragging(Bitmap bm, int y) {
        stopDragging();

        int[] listLoc = {0, 0};
        getLocationOnScreen(listLoc);
        mWindowParams.x = listLoc[0] + getPaddingLeft();
        mWindowParams.y = y - mDragPointY + mYOffset;
        mWindowParams.height = bm.getHeight();
        mWindowParams.width = (int)(bm.getWidth());

        mDragItemCache = (ImageView) mDragViewWithFrame.findViewById(R.id.drag_item_cache);

        mDragItemCache.setImageBitmap(bm);

        int resId = R.drawable.common_rearrange_frame;

        if(0 != resId) {
            mFrameCornerTopPadding = mDragViewWithFrame.getPaddingTop();
            mFrameCornerBottomPadding = mDragViewWithFrame.getPaddingBottom();
            mWindowParams.height += mFrameCornerTopPadding + mFrameCornerBottomPadding;
        }

        // if there's previous bitmap, recycle it first
        if (mDragBitmap != null) {
            if (mDragViewWithFrame != null)
                mDragItemCache.setImageBitmap(null);
            mDragBitmap.recycle();
            mDragBitmap = null;
        }

        mDragBitmap = bm;
        mWindowParams.x += mDragViewWithFrame.getPaddingRight();
        mWindowManager.addView(mDragViewWithFrame, mWindowParams);
        mIsRemoved = false;
        if(targetDragger != null) {
            imageDragger.clearColorFilter();
            ((ImageView)targetDragger).setImageDrawable(imageDragger);
        }
    }

    private void dragView(int x, int y) {
        int adjY = y - mDragPointY + mYOffset - mFrameShadowHeight;
        int[] location = new int[2];
        getLocationOnScreen(location);
        int topBound = location[1] - mFrameCornerTopPadding;
        int bottomBound = location[1] + mHeight - mWindowParams.height + mFrameCornerBottomPadding;
        // do not let the drag view be dragged over ListView's range
        if (adjY < topBound) {
            mWindowParams.y = topBound;
        } else if (adjY > bottomBound) {
            mWindowParams.y = bottomBound;
        } else {
            mWindowParams.y = adjY;
        }
        mWindowManager.updateViewLayout(mDragViewWithFrame, mWindowParams);

        drag = getChildAt(mSrcDragPos - getFirstVisiblePosition());

        if (drag != null && drag.getVisibility() == View.VISIBLE && Math.abs(y-mOrigY) > DRAG_DISTANCE)
            drag.setVisibility(INVISIBLE);
    }

    private void moveDragView(int deltaY) {
        if (mDragViewWithFrame == null || mDragItemCache == null) {
            return;
        }
        mWindowParams.y -= deltaY;
        mWindowManager.updateViewLayout(mDragViewWithFrame, mWindowParams);

    }

    boolean mIsRemoved = true;
    View mDragView;
    private void stopDragging() {

        if (mDragViewWithFrame != null && mDragItemCache!= null) {
            if (mWindowManager == null) {
                mWindowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
            }
            boolean isDragViewRemoved = false;

            if (removeDragViewImmediately == false) {
                if (mDragPos >= getFirstVisiblePosition() && mDragPos <= getLastVisiblePosition()) {
                    mDragView = getChildAt(mDragPos - getFirstVisiblePosition());
                    isDragViewRemoved = true;
                    unExpandViews(false);
                    mDragView.setPivotX(mDragView.getWidth() * 1.0f);
                    mDragView.setPivotY(mDragView.getHeight() * 0.5f);
                    mIsRemoved = false;
                    safeRemoveViewAndRecycleBitmap();
                    invalidate();
                }
            }

            if (isDragViewRemoved == false) {
                safeRemoveView();
                mDragItemCache.setImageDrawable(null);
                mDragItemCache = null;
                if (mDragBitmap != null) {
                    mDragBitmap.recycle();
                    mDragBitmap = null;
                }
                removeDragViewImmediately = false;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getWrappedAdapter() != null && mReorderDataSetObserver == null) {
            mReorderDataSetObserver = new ReorderDataSetObserver();
            getWrappedAdapter().registerDataSetObserver(mReorderDataSetObserver);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        setOverFlingEnabled(true);
        removeCallbacks(mSmoothScrollRunnable);

        if (getWrappedAdapter() != null && mReorderDataSetObserver != null) {
            getWrappedAdapter().unregisterDataSetObserver(mReorderDataSetObserver);
            mReorderDataSetObserver = null;
        }

        safeRemoveView();
        mAccessibilityManager = null;
    }

    /**
     * Set the drop listener to handle the callback when user drop the item.
     *
     * @param l The listener to handle user dropping the item.
     */
    public void setDropListener(DropListener l) {
        mDropListener = l;
    }

    /**
     * Interface that should be implemented to enable HtcReorderListView
     * to reorder two items and notify the adapter to change.
     */
    public interface DropListener {
        /**
         * The callback when user drop the item.
         *
         * @param from The source position of the dragging item
         * @param to The destination position of the dragging item
         */
        void drop(int from, int to);
    }

    public void setSeparatorPositionListener(SeparatorPositionListener sp) {
        mSeparatorPosListener = sp;
    }

    /**
     * To have a HtcListItemSeparator in HtcReorderListView, implement this interface
     * with providing the separator position and the maximum enable item count.
     */
    public interface SeparatorPositionListener {

        /**
         * Return the position of the separator in adapter
         * @return the position of the separator in adapter
         */
        int getSeparatorPos();

        /**
         * Return the maximum item count which can be enabled.
         * @return the maximum item count which can be enabled.
         */
        int getMaxEnableItemCount();
    }

    private void onFocusLost2() {
        if (mWaveRunnable != null) {
            mWaveRunnable.endWave();
        }
        forceDrop();
    }
    //-------------------------------------------------------------------------------------------------------
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (!gainFocus) {
            onFocusLost2();
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) {
            onFocusLost2();
        }
        super.onWindowFocusChanged(hasWindowFocus);
    }
    //-------------------------------------------------------------------------------------------------------
    private void forceDrop() {
//        setOverFlingEnabled(true);
        removeCallbacks(mSmoothScrollRunnable);
        if (mDragMode >= DRAG_MODE_DRAG) {
            mDragMode = DRAG_MODE_REST;
            // stopDragging();
            if (mDropListener != null && mDragPos >= 0 && mDragPos < getCount() && mSrcDragPos < getCount()) {
                if (DEBUG) Log.d(TAG, "forceDrop() mDropListener=" + mDropListener +
                                " mDragPos=" + mDragPos);
                mDropListener.drop(mSrcDragPos , mDragPos);
                if (mSrcDragPos != mDragPos) {
                    mIsDropCallbacked = true;
                }
            }
            if (mIsDropCallbacked && mIsNotifyDataSetChanged) {
                return;
            }
            stopDragging();
            unExpandViews(false);

            mDragPos = INVALID_DRAG_POS;
            mSrcDragPos = INVALID_DRAG_POS;
            mLastDragPos = INVALID_DRAG_POS;
            mDragMode = DRAG_MODE_REST;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (mIsDropCallbacked && mIsNotifyDataSetChanged) {
            syncLayoutPositionIfNeeded();
        }

        super.onLayout(changed, l, t, r, b);

        if (mIsDropCallbacked && mIsNotifyDataSetChanged) {
            stopDragging();
            unExpandViews(false);

            mDragPos = INVALID_DRAG_POS;
            mSrcDragPos = INVALID_DRAG_POS;
            mLastDragPos = INVALID_DRAG_POS;
            mDragMode = DRAG_MODE_REST;
        }
        // Reset both flags
        mIsDropCallbacked = mIsNotifyDataSetChanged = false;
    }

    /**
     * When mSrcDragPos < getFirstVisiblePosition, means the item has been dragging down
     * and its source/original position is out of visible area. Need to cheat on sync position
     * of HtcListView to shift each item to correct position.
     */
    private void syncLayoutPositionIfNeeded() {
        if (mSrcDragPos != INVALID_DRAG_POS && mSrcDragPos < getFirstVisiblePosition()) {
            if (getLastVisiblePosition() == getCount() - 1) {
                View lastChild = getChildAt(getChildCount() - 1);
                if (lastChild != null) {
                    // The bottom line of last list item is not shown, need to shift sync position
                    if (lastChild.getBottom() > getBottom()) {
//                        setLayoutSyncPositionShift(true);
                        setSelectionFromTop(getFirstVisiblePosition()-1, getChildAt(0).getTop());
                    }
                }
            } else {
//                setLayoutSyncPositionShift(true);
                setSelectionFromTop(getFirstVisiblePosition()-1, getChildAt(0).getTop());
            }
        }
    }

    // An item represents an item animation
    private class MovingItem {
        // For printing.
        String stringName;

        final int expandIndex;
        final int shrinkIndex;
        long startTime = -1;
        int current = 0;

        MovingItem(int from, int to) {
            shrinkIndex = from;
            expandIndex = to;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("MovingItem{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" startTime="); sb.append(startTime); sb.append(',');
            sb.append(" current="); sb.append(current); sb.append(',');
            sb.append(" expand index="); sb.append(expandIndex); sb.append(',');
            sb.append(" shrink index="); sb.append(shrinkIndex); sb.append('}');
            stringName = sb.toString();
            return stringName;
        }
    }

    private class DragWindowMove {
        long startTime = -1;
        int current = 0;
        int distance = 0;

        DragWindowMove(int dist) {
            distance = dist;
        }
    }

    void moveItems(int expandIndex, int shrinkIndex, int delta) {
        int firstVisible = getFirstVisiblePosition();
        int shrinkI = (shrinkIndex == -1) ? 0 : shrinkIndex;
        int expandI = (expandIndex == -1) ? 0 : expandIndex;

        boolean forceShrinkListItemZero = false;

        /*
         * When dragged item 1 above item 0,
         * then dragging item 1 below item 0,
         * item 0 should only shrink itself
         */
        if (shrinkIndex == -1 && expandIndex == 1) {
            forceShrinkListItemZero = true;
        }

        View shrink = getChildAt(shrinkI - firstVisible);
        View expand = getChildAt(expandI - firstVisible);

        ViewGroup.LayoutParams params;
        int height;

        if (shrink != null) {
            if (shrink instanceof HtcListItem) {
                if((((HtcListItem) shrink).getBottomSpace() - delta) >= 0) {
                    int bottomSpace = ((HtcListItem) shrink).getBottomSpace() - delta;
                    ((HtcListItem) shrink).setBottomSpace(bottomSpace);
                }
                else if(((HtcListItem) shrink).getBottomSpace() > 0) {
                    int topSpace = ((HtcListItem) shrink).getTopSpace() - delta + ((HtcListItem) shrink).getBottomSpace();
                    ((HtcListItem) shrink).setTopSpace(topSpace);
                    ((HtcListItem) shrink).setBottomSpace(0);
                } else {
                    if (!forceShrinkListItemZero) {
                        int TopSpace = ((HtcListItem) shrink).getTopSpace() - delta;
                        ((HtcListItem) shrink).setTopSpace(TopSpace);
                    }
                }
                // when childnum = -1, the dragger is dragged over top
                if (shrinkIndex == -1) {
                    int topSpace = ((HtcListItem) shrink).getTopSpace();
                    int bottomSpace = ((HtcListItem) shrink).getBottomSpace();
                    topSpace -= delta;

                    if (!forceShrinkListItemZero) {
                        bottomSpace += delta;
                    }

                    ((HtcListItem) shrink).setTopSpace(topSpace);
                    ((HtcListItem) shrink).setBottomSpace(bottomSpace);
                }
            } else if (shrink instanceof HtcListItemSeparator) {
                if((((HtcListItemSeparator) shrink).getBottomSpace() - delta) >= 0) {
                    int bottomSpace = ((HtcListItemSeparator) shrink).getBottomSpace() - delta;
                    ((HtcListItemSeparator) shrink).setBottomSpace(bottomSpace);
                }
                else if(((HtcListItemSeparator) shrink).getBottomSpace() > 0) {
                    int topSpace = ((HtcListItemSeparator) shrink).getTopSpace() - delta + ((HtcListItemSeparator) shrink).getBottomSpace();
                    ((HtcListItemSeparator) shrink).setTopSpace(topSpace);
                    ((HtcListItemSeparator) shrink).setBottomSpace(0);
                } else {
                    if (!forceShrinkListItemZero) {
                        int TopSpace = ((HtcListItemSeparator) shrink).getTopSpace() - delta;
                        ((HtcListItemSeparator) shrink).setTopSpace(TopSpace);
                    }
                }
                // when childnum = -1, the dragger is dragged over top
                if (shrinkIndex == -1) {
                    int topSpace = ((HtcListItemSeparator) shrink).getTopSpace();
                    int bottomSpace = ((HtcListItemSeparator) shrink).getBottomSpace();
                    topSpace -= delta;

                    if (!forceShrinkListItemZero) {
                        bottomSpace += delta;
                    }

                    ((HtcListItemSeparator) shrink).setTopSpace(topSpace);
                    ((HtcListItemSeparator) shrink).setBottomSpace(bottomSpace);
                }
            } else {
                // list item might be wrap_content/match parent
                params = shrink.getLayoutParams();
                height = params.height;
                if (height < 0) {
                    height = shrink.getHeight();
                }
                height -= delta;
                params.height = height;
                if (DEBUG) Log.d(TAG, "set shrink item " + shrinkIndex + " h=" + height);
                shrink.setLayoutParams(params);

                // when childnum = -1, the dragger is dragged over top
                if (shrinkIndex == -1) {
                    int paddingTop = shrink.getPaddingTop();
                    paddingTop -= delta;
                    shrink.setPadding(mCacheItemPadding[0], paddingTop,
                            mCacheItemPadding[2], mCacheItemPadding[3]);
                }
            }
        }

        if (expand != null) {
            if (expand instanceof HtcListItem) {
                if((((HtcListItem) expand).getTopSpace() + delta) <= 0) {
                    int topSpace = ((HtcListItem) expand).getTopSpace() + delta;
                    ((HtcListItem) expand).setTopSpace(topSpace);
                }
                else if(((HtcListItem) expand).getTopSpace() < 0) {
                    int bottomSpace = ((HtcListItem) expand).getBottomSpace() + delta + ((HtcListItem) expand).getTopSpace();
                    ((HtcListItem) expand).setBottomSpace(bottomSpace);
                    ((HtcListItem) expand).setTopSpace(0);
                } else {
                    int bottomSpace = ((HtcListItem) expand).getBottomSpace() + delta;
                    ((HtcListItem) expand).setBottomSpace(bottomSpace);
                }
                // when childnum = -1, the dragger is dragged over top
                if (expandIndex == -1) {
                    int topSpace = ((HtcListItem) expand).getTopSpace();
                    int bottomSpace = ((HtcListItem) expand).getBottomSpace();
                    topSpace += delta;
                    bottomSpace -= delta;
                    ((HtcListItem) expand).setTopSpace(topSpace);
                    ((HtcListItem) expand).setBottomSpace(bottomSpace);
                } else if (expandIndex == getCount() - 1) {
                    View lastVisibleChild = this.getChildAt(this.getChildCount() - 1);
                    int bottomSpace = ((HtcListItem) expand).getBottomSpace();
                    if (mSrcDragPos == (getCount() - 1) && lastVisibleChild != null)
                    {
                        if (lastVisibleChild.getBottom() >= getHeight() - getListPaddingBottom() && bottomSpace > 0)
                        {
                            int space = Math.max(bottomSpace - (lastVisibleChild.getBottom() - getHeight() - getListPaddingBottom()), 0);
                            ((HtcListItem) expand).setBottomSpace(space);
                        }
                    }
                    if (isReadyToOverScroll())
                        smoothScrollBy(mItemHeightNormal, 30);
                }
            } else if (expand instanceof HtcListItemSeparator) {
                if((((HtcListItemSeparator) expand).getTopSpace() + delta) <= 0) {
                    int topSpace = ((HtcListItemSeparator) expand).getTopSpace() + delta;
                    ((HtcListItemSeparator) expand).setTopSpace(topSpace);
                }
                else if(((HtcListItemSeparator) expand).getTopSpace() < 0) {
                    int bottomSpace = ((HtcListItemSeparator) expand).getBottomSpace() + delta + ((HtcListItemSeparator) expand).getTopSpace();
                    ((HtcListItemSeparator) expand).setBottomSpace(bottomSpace);
                    ((HtcListItemSeparator) expand).setTopSpace(0);
                } else {
                    int bottomSpace = ((HtcListItemSeparator) expand).getBottomSpace() + delta;
                    ((HtcListItemSeparator) expand).setBottomSpace(bottomSpace);
                }
                // when childnum = -1, the dragger is dragged over top
                if (expandIndex == -1) {
                    int topSpace = ((HtcListItemSeparator) expand).getTopSpace();
                    int bottomSpace = ((HtcListItemSeparator) expand).getBottomSpace();
                    topSpace += delta;
                    bottomSpace -= delta;
                    ((HtcListItemSeparator) expand).setTopSpace(topSpace);
                    ((HtcListItemSeparator) expand).setBottomSpace(bottomSpace);
                } else if (expandIndex == getCount() - 1) {
                    if (isReadyToOverScroll())
                        smoothScrollBy(mItemHeightNormal, 30);
                }
            } else {
                // list item might be wrap_content/match parent
                params = expand.getLayoutParams();
                height = params.height;
                if (height < 0) {
                    height = expand.getHeight();
                }
                height += delta;
                params.height = height;
                if (DEBUG) Log.d(TAG, "set expand item " + expandIndex + " h=" + height);
                expand.setLayoutParams(params);

                // when childnum = -1, the dragger is dragged over top
                if (expandIndex == -1) {
                    int paddingTop = expand.getPaddingTop();
                    paddingTop += delta;
                    expand.setPadding(mCacheItemPadding[0], paddingTop,
                                mCacheItemPadding[2], mCacheItemPadding[3]);
//                  } else if (expandIndex == getCount() - 1 && shrink == null) {
                } else if (expandIndex == getCount() - 1) {
                    if (DEBUG) Log.d(TAG, "moveitems() expandIndex == " + (getCount() - 1) +
                                " shrinkIndex=" + shrinkIndex +
                                " smooth scrollby " + delta);
                    // expand the last item without any other item shirnking,
                    // the extension will not push the list up, so scroll the list here
                    if (isReadyToOverScroll())
                        smoothScrollBy(delta, 50);
                }
            }
        }
    }

    private class WaveRunnable implements Runnable {
        ArrayList<MovingItem> mItems;
        // the duration of moving items shifted by dragging window
        // and the duration of dragging window moving back from ACTION_UP
        int mDuration = 160;
        int mDistance = mItemHeightNormal;
        Interpolator mInterpolator;
        DragWindowMove mDraggingWindow;

        WaveRunnable() {
            mItems = new ArrayList<MovingItem> ();
            mInterpolator = new DecelerateInterpolator();
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void endWave() {
            mItems.removeAll(mItems);
            mDraggingWindow = null;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void run() {
            if (mDraggingWindow == null && (mItems.isEmpty() || mDragMode == DRAG_MODE_REST)) {
                mItems.removeAll(mItems);
                return;
            }

            long now;

            int N = mItems.size();
            for (int i = 0; i < N; i++) {
                //Do animation on each moving item
                MovingItem mi = mItems.get(i);
                if (DEBUG) Log.d(TAG, "WaveRunnable.run() child:" + i + " =" + mi);
                if (mi.startTime < 0) {
                    mi.startTime = SystemClock.uptimeMillis();
                }

                now = SystemClock.uptimeMillis();
                int shrink = mi.shrinkIndex;
                int expand = mi.expandIndex;
                if (shrink == -1) shrink++;
                if (expand == -1) expand++;

                if (now > mi.startTime + mDuration) {
                    if (DEBUG) Log.d(TAG, "item: " + i + " times out!");
                    int delta = mDistance - mi.current;
                    moveItems(mi.expandIndex, mi.shrinkIndex, delta);

                    /*
                     * Accessibility starts
                     */
                    if (mAccessibilityManager != null && mAccessibilityManager.isEnabled()) {
                        StringBuilder sb = new StringBuilder();
                        View itemView = null;
                        int childPosition = INVALID_DRAG_POS;

                        if (mi.expandIndex < mi.shrinkIndex) { // Dragging up, would talkback "Move above itemName"
                            if (mi.shrinkIndex == mSrcDragPos) {
                                if (mi.shrinkIndex - 1 >= 0) {
                                    sb.append(MOVE_ABOVE);
                                    childPosition = mi.shrinkIndex - 1 - getFirstVisiblePosition();
                                }
                            } else {
                                if (mi.shrinkIndex >= 0) {
                                    sb.append(MOVE_ABOVE);
                                    childPosition = mi.shrinkIndex - getFirstVisiblePosition();
                                }
                            }
                        } else { // Dragging down, would talkback "Move below itemName"
                            if (mi.expandIndex == mSrcDragPos) {
                                if (mi.expandIndex - 1 >= 0) {
                                    sb.append(MOVE_BELOW);
                                    childPosition = mi.expandIndex - 1 - getFirstVisiblePosition();
                                }
                            } else {
                                if (mi.expandIndex >= 0) {
                                    sb.append(MOVE_BELOW);
                                    childPosition = mi.expandIndex - getFirstVisiblePosition();
                                }
                            }
                        }

                        if (childPosition != INVALID_DRAG_POS) {
                            itemView = getChildAt(childPosition);
                            if (itemView != null) {
                                CharSequence cs = itemView.getContentDescription();
                                if (cs == null) {
                                    findTextViewToAppendString(itemView, sb);
                                } else if (DRAGGABLE_ITEM_STRING_LENGTH < cs.length()) {
                                    cs = cs.subSequence(DRAGGABLE_ITEM_STRING_LENGTH, cs.length());
                                    sb.append(cs);
                                }
                            }
                            announceForAccessibility(sb.toString());
                        }
                    }
                    /*
                     * Accessibility ends
                     */

                    mi = null;
                    mItems.remove(i);
                    i--;
                    N--;
                    continue;
                }

                float progress = (float)(now - mi.startTime) / mDuration;
                float dst;
                if (mInterpolator != null) {
                    dst = mInterpolator.getInterpolation(progress);
                } else {
                    dst = progress;
                }
                int delta = (int)(dst * mDistance - mi.current);
                mi.current += delta;

                if (DEBUG) Log.d(TAG, "movingRunnable shrink=" + mi.shrinkIndex +
                                " expand=" + mi.expandIndex +
                                " progress=" + progress +
                                " current=" + mi.current +
                                " interpolator=" + dst +
                                " delta=" + delta);
                moveItems(mi.expandIndex, mi.shrinkIndex, delta);
            }

            DragWindowMove dmw = mDraggingWindow;
            if (dmw != null) {
                if (dmw.startTime < 0) {
                    dmw.startTime = SystemClock.uptimeMillis();
                }
                now = SystemClock.uptimeMillis();
                if (now > dmw.startTime + mDuration) {
                    // done moving drag view
                    int delta = dmw.distance - dmw.current;
                    try {
                        moveDragView(delta);
                    } catch (java.lang.IllegalArgumentException e) {
                        Log.w(TAG, "Animation dropped! Can't find moving view.");
                    }
                    endWave();
                    Log.w(TAG, "WaveRunnable drag view time up forceDrop()");
                    forceDrop();
                } else {
                    float progress = (float)(now - dmw.startTime) / mDuration;
                    float dst;
                    if (mInterpolator != null) {
                        dst = mInterpolator.getInterpolation(progress);
                    } else {
                        dst = progress;
                    }
                    int delta = (int)(dst * dmw.distance - dmw.current);
                    dmw.current += delta;
                    try {
                        moveDragView(delta);
                    } catch (java.lang.IllegalArgumentException e) {
                        // if view is destroyed before stopDragging()
                        // moveDragView will throw exception
                        // need to find a better way to control dragging window
                        Log.w(TAG, "Animation dropped! Can't find moving view.");
                        if (mDragViewWithFrame != null && mDragItemCache!= null) {
                            if (mWindowManager == null) {
                                mWindowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
                            }
                            mDragItemCache.setImageDrawable(null);
                            mDragItemCache = null;
                        }
                        if (mDragBitmap != null) {
                            mDragBitmap.recycle();
                            mDragBitmap = null;
                        }
                    }
                }
            }

            post(this);
        }
    }

    /**
     * Depth First Search to find all the TextViews and append the texts
     * to specific string.
     *
     * @param itemView The subject view(ViewGroup) to find
     * @param sb The String to append the founded texts
     * @return True if found at least 1 visible TextView, false otherwise
     */
    private boolean findTextViewToAppendString(View itemView, StringBuilder sb) {
        if (itemView == null) {
            return false;
        }
        if (itemView instanceof TextView) { // Found!
            if (itemView.getVisibility() == View.VISIBLE) {
                sb.append(" ");
                sb.append(((TextView)itemView).getText().toString());
                return true;
            } else {
                return false;
            }
        }
        ViewGroup itemViewGroup;
        if (itemView instanceof ViewGroup) {
            itemViewGroup = (ViewGroup)itemView;
            boolean found = false;
            for (int i = 0 ; i < itemViewGroup.getChildCount() ; i++) {
                found |= findTextViewToAppendString(itemViewGroup.getChildAt(i), sb);
            }
            return found; // Can't find any TextView in original itemView
        } else {
            return false;
        }
    }

    /**
     * Set all items focusable. (default is true)
     *
     * @param itemFocusable true to set all items focusable, false otherwise.
     *
     * @deprecated [Not use any longer]
     */
    /**@hide*/
    public void setAllItemFocusable(boolean itemFocusable) {
    }

    /**
     * Register such DataSetObserver to set the flag on when notifyDataSetChanged was called
     * by AP's adapter.
     */
    class ReorderDataSetObserver extends DataSetObserver {
        /**
         * @hide
         */
        public void onChanged() {
            mIsNotifyDataSetChanged = true;
        }
        /**
         * @hide
         */
        public void onInvalidated() {
            mIsNotifyDataSetChanged = true;
        }
    }

    /**
     * Sets the data behind this HtcReorderListView.
     *
     * @param adapter The ListAdapter which is responsible for maintaining the
     *        data backing this list and for producing a view to represent an
     *        item in that data set.
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        // unregister mReorderDataSetObserver is it exists
        if (getWrappedAdapter() != null && mReorderDataSetObserver != null) {
            getWrappedAdapter().unregisterDataSetObserver(mReorderDataSetObserver);
        }

        mDisableAdapter = new DisableItemAdapter(adapter);
        super.setAdapter(mDisableAdapter);

        // register mReorderDataSetObserver to AP's adapter, to set the flag on
        // when notifyDataSetChanged was called.
        if (getWrappedAdapter() != null) {
            mReorderDataSetObserver = new ReorderDataSetObserver();
            getWrappedAdapter().registerDataSetObserver(mReorderDataSetObserver);
        }
    }

    /**
     * Get the adapter of this reorder list view.
     *
     * @return The adapter of this reorder list view.
     */
    @Override
    public ListAdapter getAdapter() {
        if (mDisableAdapter == null) {
            return super.getAdapter();
        }

        // If HeaderView exists, it will be wrapped by HtcHeaderViewListAdapter
        // in HtcListView (not here). Otherwise mItemCount will be error calculated when
        // AP calls notifyDataSetChanged by original wrapped adapter
        if (super.getAdapter() instanceof HeaderViewListAdapter) {
            return super.getAdapter();
        }

        return mDisableAdapter.getWrappedAdapter();
    }

    /**
     * Get the AP's adapter if HtcReorderListView's adapter is not null
     * @return AP's adapter
     */
    private ListAdapter getWrappedAdapter() {
        return mDisableAdapter != null ? mDisableAdapter.getWrappedAdapter() : null;
    }

    /**
     * Set the resource id of the dragger.
     *
     * @param id The id of the dragger.
     */
    public void setDraggerId(int id) {
        mDraggerId = id;
    }

    /**
     * Set the color for background when item is in dragging.
     * @param color The color of the background when item is in dragging
     *
     * @deprecated [Not use any longer]
     */
    /**@hide*/
    public void setReorderBackgroundColor(int color) {
    }

    /**
     * When the item is below separator and enable item count is not lower than maximum count,
     * then disable the item (include drag behavior).
     *
     * @param position the position of the item
     * @return true if below and in maximum count, false otherwise.
     */
    private boolean isItemDisabled(int position) {
        if (mSeparatorPosListener != null) {
            int separatorPos = mSeparatorPosListener.getSeparatorPos();
            int maxEnableItemCount = mSeparatorPosListener.getMaxEnableItemCount();
            if (position > separatorPos) {
                if (separatorPos >= maxEnableItemCount) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * In order to prevent item from being clicked, selected or pressed,
     * set all the item focusable, so that the item will receive events
     * on its own. If AP has any need of receiving events through the list,
     * call XXX to disable item focusable.
     * <p>Note that items will have selector on if the focusable is set to false.</p>
     **/
    class DisableItemAdapter implements WrapperListAdapter {

        private ListAdapter mWrappedAdapter;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public DisableItemAdapter(ListAdapter adapter) {
            mWrappedAdapter = adapter;
        }
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public ListAdapter getWrappedAdapter() {
            return mWrappedAdapter;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public boolean areAllItemsEnabled() {
            if (mWrappedAdapter != null) {
                return mWrappedAdapter.areAllItemsEnabled();
            }
            return false;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public boolean isEnabled(int position) {
            if (mWrappedAdapter != null) {
                if (isItemDisabled(position)) {
                    return false;
                }
                return mWrappedAdapter.isEnabled(position);
            }
            return false;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public int getCount() {
            if (mWrappedAdapter != null) {
                return mWrappedAdapter.getCount();
            }
            return 0;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public Object getItem(int position) {
            if (mWrappedAdapter != null) {
                return mWrappedAdapter.getItem(position);
            }
            return null;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public long getItemId(int position) {
            if (mWrappedAdapter != null) {
                return mWrappedAdapter.getItemId(position);
            }
            return -1;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public int getItemViewType(int position) {
            if (mWrappedAdapter != null) {
                return mWrappedAdapter.getItemViewType(position);
            }
            return 0;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public View getView(int position, View convertView, ViewGroup parent) {
            boolean isConvertView = false;
            if (mWrappedAdapter != null) {
                if(convertView != null) {
                    isConvertView = true;
                    if (convertView instanceof HtcListItem) {
                        ((HtcListItem) convertView).setTopSpace(0);
                        ((HtcListItem) convertView).setBottomSpace(0);
                        convertView.setVisibility(View.VISIBLE);
                    } else if (convertView instanceof HtcListItemSeparator) {
                        ((HtcListItemSeparator) convertView).setTopSpace(0);
                        ((HtcListItemSeparator) convertView).setBottomSpace(0);
                        convertView.setVisibility(View.VISIBLE);
                    } else {
                        ViewGroup.LayoutParams params = convertView.getLayoutParams();
                        params.height = mItemHeightNormal;
                        convertView.setLayoutParams(params);
                        convertView.setVisibility(View.VISIBLE);
                    }
                }
                View v = mWrappedAdapter.getView(position, convertView, parent);

                /*
                 * Accessibility starts
                 */
                if (mAccessibilityManager != null && mAccessibilityManager.isEnabled()) {
                    if (isEnabled(position)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(DRAGGABLE_ITEM);
                        findTextViewToAppendString(v, sb);
                        v.setContentDescription(sb.toString());
                    }
                }
                /*
                 * Accessibility ends
                 */

                if(isConvertView == false) {
                    View dragger = v.findViewById(mDraggerId);
                    if (dragger != null) {
                        imageDragger.clearColorFilter();
                        ((ImageView)dragger).setImageDrawable(imageDragger);
                    }
                }

                if (mSeparatorPosListener != null) {
                    if (isItemDisabled(position)) {
                        v.setEnabled(false);
                        View dragger = v.findViewById(mDraggerId);
                        if (dragger != null) {
                            dragger.setAlpha((float) 0.4);
                        }
                    } else if (position != mSeparatorPosListener.getSeparatorPos()) {
                        v.setEnabled(true);
                        View dragger = v.findViewById(mDraggerId);
                        if (dragger != null) {
                            dragger.setAlpha((float) 1.0);
                        }
                    }
                }
                return v;
            }
            return null;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public int getViewTypeCount() {
            if (mWrappedAdapter != null) {
                return mWrappedAdapter.getViewTypeCount();
            }
            return 0;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public boolean hasStableIds() {
            if (mWrappedAdapter != null) {
                return mWrappedAdapter.hasStableIds();
            }
            return false;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public boolean isEmpty() {
            if (mWrappedAdapter != null) {
                return mWrappedAdapter.isEmpty();
            }
            return false;
        }

        public void registerDataSetObserver(DataSetObserver observer) {
            if (mWrappedAdapter != null) {
                mWrappedAdapter.registerDataSetObserver(observer);
            }
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (mWrappedAdapter != null) {
                mWrappedAdapter.unregisterDataSetObserver(observer);
            }
        }
    }

    private void safeRemoveView() {
        if (mWindowManager != null && mDragViewWithFrame != null && !mIsRemoved) {
            mWindowManager.removeView(mDragViewWithFrame);
            mIsRemoved = true;
        }
    }

    private void safeRemoveViewAndRecycleBitmap() {
        if (!mIsRemoved) {
            if (mWindowManager != null && mDragViewWithFrame != null) mWindowManager.removeView(mDragViewWithFrame);
            mDragItemCache.setImageDrawable(null);
            mDragItemCache = null;
            if (mDragBitmap != null) {
                mDragBitmap.recycle();
                mDragBitmap = null;
            }
            mIsRemoved = true;
        }
    }
}
