/*
 * Copyright (C) 2007 The Android Open Source Project
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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import java.lang.ref.WeakReference;

/**
 * <p>A popup window that can be used to display an arbitrary view. The popup
 * windows is a floating container that appears on top of the current
 * activity.</p>
 *
 * @see android.widget.AutoCompleteTextView
 * @see android.widget.Spinner
 * @deprecated [Not use any longer] Because this implementation use PopupWindow and the performance is not better than View. Please use HtcPopupContainer instead of this.
 */
/**@hide*/
public class HtcPopupWindow extends PopupWindow {
    private Context mContext;
    private WindowManager mWindowManager;

    private boolean mIsShowing;
    private boolean mIsDropdown;

    private View mContentView;
    private View mPopupView;
    private boolean mClipToScreen;
    private boolean mAllowScrollingAnchorParent = true;
    private boolean mLayoutInsetDecor = false;
    private boolean mNotTouchModal;

    private OnTouchListener mTouchInterceptor;

    private int mWidthMode;
    private int mLastWidth;
    private int mHeightMode;
    private int mLastHeight;

    private int mPopupWidth;
    private int mPopupHeight;

    private int[] mDrawingLocation = new int[2];
    private int[] mScreenLocation = new int[2];
    private Rect mTempRect = new Rect();

    private Drawable mAboveAnchorBackgroundDrawable;
    private Drawable mBelowAnchorBackgroundDrawable;

    private boolean mAboveAnchor = true;

    private OnDismissListener mOnDismissListener;
    private boolean mIgnoreCheekPress = false;

    private static final int[] ABOVE_ANCHOR_STATE_SET = new int[] {
        android.R.attr.state_above_anchor
    };

    private WeakReference<View> mAnchor;
    private OnScrollChangedListener mOnScrollChangedListener =
        new OnScrollChangedListener() {
            public void onScrollChanged() {
                View anchor = mAnchor != null ? mAnchor.get() : null;
                if (anchor != null && mPopupView != null) {
                    WindowManager.LayoutParams p = (WindowManager.LayoutParams)
                            mPopupView.getLayoutParams();

                    updateAboveAnchor(findDropDownPosition(anchor, p, mAnchorXoff, mAnchorYoff));
                    update(p.x, p.y, -1, -1, true);
                }
            }
        };
    private int mAnchorXoff, mAnchorYoff;

    /**
     * <p>Create a new empty, non focusable popup window of dimension (0,0).</p>
     *
     * <p>The popup does provide a background.</p>
     */
    public HtcPopupWindow(Context context) {
        this(context, null);
    }

    /**
     * <p>Create a new empty, non focusable popup window of dimension (0,0).</p>
     *
     * <p>The popup does provide a background.</p>
     */
    public HtcPopupWindow(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.popupWindowStyle);
    }

    /**
     * <p>Create a new empty, non focusable popup window of dimension (0,0).</p>
     *
     * <p>The popup does provide a background.</p>
     */
    public HtcPopupWindow(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    /**
     * <p>Create a new, empty, non focusable popup window of dimension (0,0).</p>
     *
     * <p>The popup does not provide a background.</p>
     */
    public HtcPopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * <p>Create a new empty, non focusable popup window of dimension (0,0).</p>
     *
     * <p>The popup does not provide any background. This should be handled
     * by the content view.</p>
     */
    public HtcPopupWindow() {
        this(null, 0, 0);
    }

    /**
     * <p>Create a new non focusable popup window which can display the
     * <tt>contentView</tt>. The dimension of the window are (0,0).</p>
     *
     * <p>The popup does not provide any background. This should be handled
     * by the content view.</p>
     *
     * @param contentView the popup's content
     */
    public HtcPopupWindow(View contentView) {
        this(contentView, 0, 0);
    }

    /**
     * <p>Create a new empty, non focusable popup window. The dimension of the
     * window must be passed to this constructor.</p>
     *
     * <p>The popup does not provide any background. This should be handled
     * by the content view.</p>
     *
     * @param width the popup's width
     * @param height the popup's height
     */
    public HtcPopupWindow(int width, int height) {
        this(null, width, height);
    }

    /**
     * <p>Create a new non focusable popup window which can display the
     * <tt>contentView</tt>. The dimension of the window must be passed to
     * this constructor.</p>
     *
     * <p>The popup does not provide any background. This should be handled
     * by the content view.</p>
     *
     * @param contentView the popup's content
     * @param width the popup's width
     * @param height the popup's height
     */
    public HtcPopupWindow(View contentView, int width, int height) {
        this(contentView, width, height, false);
    }

    /**
     * <p>Create a new popup window which can display the <tt>contentView</tt>.
     * The dimension of the window must be passed to this constructor.</p>
     *
     * <p>The popup does not provide any background. This should be handled
     * by the content view.</p>
     *
     * @param contentView the popup's content
     * @param width the popup's width
     * @param height the popup's height
     * @param focusable true if the popup can be focused, false otherwise
     */
    public HtcPopupWindow(View contentView, int width, int height, boolean focusable) {
        if (contentView != null) {
            mContext = contentView.getContext();
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        setContentView(contentView);
        setWidth(width);
        setHeight(height);
        setFocusable(focusable);
    }

    /**
     * Set the flag on popup to ignore cheek press eventt; by default this flag
     * is set to false
     * which means the pop wont ignore cheek press dispatch events.
     *
     * <p>If the popup is showing, calling this method will take effect only
     * the next time the popup is shown or through a manual call to one of
     * the {@link #update()} methods.</p>
     *
     * @see #update()
     */
    public void setIgnoreCheekPress() {
        mIgnoreCheekPress = true;
    }


    /**
     * <p>Return the view used as the content of the popup window.</p>
     *
     * @return a {@link android.view.View} representing the popup's content
     *
     * @see #setContentView(android.view.View)
     */
    public View getContentView() {
        return mContentView;
    }

    /**
     * <p>Change the popup's content. The content is represented by an instance
     * of {@link android.view.View}.</p>
     *
     * <p>This method has no effect if called when the popup is showing.</p>
     *
     * @param contentView the new content for the popup
     *
     * @see #getContentView()
     * @see #isShowing()
     */
    public void setContentView(View contentView) {
        if (isShowing()) {
            return;
        }

        mContentView = contentView;

        if (mContext == null && mContentView != null) {
            mContext = mContentView.getContext();
        }

        if (mWindowManager == null && mContentView != null) {
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
    }

    /**
     * Set a callback for all touch events being dispatched to the popup
     * window.
     */
    public void setTouchInterceptor(OnTouchListener l) {
        mTouchInterceptor = l;
    }


    /**
     * Clip this popup window to the screen, but not to the containing window.
     *
     * @param enabled True to clip to the screen.
     * @hide
     */
    public void setClipToScreenEnabled(boolean enabled) {
        mClipToScreen = enabled;
        setClippingEnabled(!enabled);
    }

    /**
     * Allow PopupWindow to scroll the anchor's parent to provide more room
     * for the popup. Enabled by default.
     *
     * @param enabled True to scroll the anchor's parent when more room is desired by the popup.
     */
    void setAllowScrollingAnchorParent(boolean enabled) {
        mAllowScrollingAnchorParent = enabled;
    }


    /**
     * Allows the popup window to force the flag
     * {@link WindowManager.LayoutParams#FLAG_LAYOUT_INSET_DECOR}, overriding default behavior.
     * This will cause the popup to inset its content to account for system windows overlaying
     * the screen, such as the status bar.
     *
     * <p>This will often be combined with {@link #setLayoutInScreenEnabled(boolean)}.
     *
     * @param enabled true if the popup's views should inset content to account for system windows,
     *                the way that decor views behave for full-screen windows.
     * @hide
     */
    public void setLayoutInsetDecor(boolean enabled) {
        mLayoutInsetDecor = enabled;
    }

    /**
     * Set whether this window is touch modal or if outside touches will be sent to
     * other windows behind it.
     * @hide
     */
    public void setTouchModal(boolean touchModal) {
        mNotTouchModal = !touchModal;
    }

    /**
     * <p>Change the width and height measure specs that are given to the
     * window manager by the popup.  By default these are 0, meaning that
     * the current width or height is requested as an explicit size from
     * the window manager.  You can supply
     * {@link ViewGroup.LayoutParams#WRAP_CONTENT} or
     * {@link ViewGroup.LayoutParams#MATCH_PARENT} to have that measure
     * spec supplied instead, replacing the absolute width and height that
     * has been set in the popup.</p>
     *
     * <p>If the popup is showing, calling this method will take effect only
     * the next time the popup is shown.</p>
     *
     * @param widthSpec an explicit width measure spec mode, either
     * {@link ViewGroup.LayoutParams#WRAP_CONTENT},
     * {@link ViewGroup.LayoutParams#MATCH_PARENT}, or 0 to use the absolute
     * width.
     * @param heightSpec an explicit height measure spec mode, either
     * {@link ViewGroup.LayoutParams#WRAP_CONTENT},
     * {@link ViewGroup.LayoutParams#MATCH_PARENT}, or 0 to use the absolute
     * height.
     */
    public void setWindowLayoutMode(int widthSpec, int heightSpec) {
        mWidthMode = widthSpec;
        mHeightMode = heightSpec;
    }

    /**
     * <p>Indicate whether this popup window is showing on screen.</p>
     *
     * @return true if the popup is showing, false otherwise
     */
    public boolean isShowing() {
        return mIsShowing;
    }

    /**
     * <p>
     * Display the content view in a popup window at the specified location. If the popup window
     * cannot fit on screen, it will be clipped. See {@link android.view.WindowManager.LayoutParams}
     * for more information on how gravity and the x and y parameters are related. Specifying
     * a gravity of {@link android.view.Gravity#NO_GRAVITY} is similar to specifying
     * <code>Gravity.LEFT | Gravity.TOP</code>.
     * </p>
     *
     * @param parent a parent view to get the {@link android.view.View#getWindowToken()} token from
     * @param gravity the gravity which controls the placement of the popup window
     * @param x the popup's x location offset
     * @param y the popup's y location offset
     */
    public void showAtLocation(View parent, int gravity, int x, int y) {
        showAtLocation(parent.getWindowToken(), gravity, x, y);
    }

    /**
     * Display the content view in a popup window at the specified location.
     *
     * @param token Window token to use for creating the new window
     * @param gravity the gravity which controls the placement of the popup window
     * @param x the popup's x location offset
     * @param y the popup's y location offset
     *
     * @hide Internal use only. Applications should use
     *       {@link #showAtLocation(View, int, int, int)} instead.
     */
    public void showAtLocation(IBinder token, int gravity, int x, int y) {
        if (isShowing() || mContentView == null) {
            return;
        }

        unregisterForScrollChanged();

        mIsShowing = true;
        mIsDropdown = false;

        WindowManager.LayoutParams p = createPopupLayout(token);
        p.windowAnimations = computeAnimationResource();

        preparePopup(p);
        if (gravity == Gravity.NO_GRAVITY) {
            gravity = Gravity.TOP | Gravity.LEFT;
        }
        p.gravity = gravity;
        p.x = x;
        p.y = y;
        if (mHeightMode < 0) p.height = mLastHeight = mHeightMode;
        if (mWidthMode < 0) p.width = mLastWidth = mWidthMode;
        invokePopup(p);
    }

    /**
     * <p>Display the content view in a popup window anchored to the bottom-left
     * corner of the anchor view. If there is not enough room on screen to show
     * the popup in its entirety, this method tries to find a parent scroll
     * view to scroll. If no parent scroll view can be scrolled, the bottom-left
     * corner of the popup is pinned at the top left corner of the anchor view.</p>
     *
     * @param anchor the view on which to pin the popup window
     *
     * @see #dismiss()
     */
    public void showAsDropDown(View anchor) {
        showAsDropDown(anchor, 0, 0);
    }

    /**
     * <p>Display the content view in a popup window anchored to the bottom-left
     * corner of the anchor view offset by the specified x and y coordinates.
     * If there is not enough room on screen to show
     * the popup in its entirety, this method tries to find a parent scroll
     * view to scroll. If no parent scroll view can be scrolled, the bottom-left
     * corner of the popup is pinned at the top left corner of the anchor view.</p>
     * <p>If the view later scrolls to move <code>anchor</code> to a different
     * location, the popup will be moved correspondingly.</p>
     *
     * @param anchor the view on which to pin the popup window
     *
     * @see #dismiss()
     */
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (isShowing() || mContentView == null) {
            return;
        }

        registerForScrollChanged(anchor, xoff, yoff);

        mIsShowing = true;
        mIsDropdown = true;

        WindowManager.LayoutParams p = createPopupLayout(anchor.getWindowToken());
        preparePopup(p);

        updateAboveAnchor(findDropDownPosition(anchor, p, xoff, yoff));

        if (mHeightMode < 0) p.height = mLastHeight = mHeightMode;
        if (mWidthMode < 0) p.width = mLastWidth = mWidthMode;

        p.windowAnimations = computeAnimationResource();

        invokePopup(p);
    }

    private void updateAboveAnchor(boolean aboveAnchor) {
        if (aboveAnchor != mAboveAnchor) {
            mAboveAnchor = aboveAnchor;

            if (getBackground() != null) {
                // If the background drawable provided was a StateListDrawable with above-anchor
                // and below-anchor states, use those. Otherwise rely on refreshDrawableState to
                // do the job.
                if (mAboveAnchorBackgroundDrawable != null) {
                    if (mAboveAnchor) {
                        mPopupView.setBackgroundDrawable(mAboveAnchorBackgroundDrawable);
                    } else {
                        mPopupView.setBackgroundDrawable(mBelowAnchorBackgroundDrawable);
                    }
                } else {
                    mPopupView.refreshDrawableState();
                }
            }
        }
    }

    /**
     * Indicates whether the popup is showing above (the y coordinate of the popup's bottom
     * is less than the y coordinate of the anchor) or below the anchor view (the y coordinate
     * of the popup is greater than y coordinate of the anchor's bottom).
     *
     * The value returned
     * by this method is meaningful only after {@link #showAsDropDown(android.view.View)}
     * or {@link #showAsDropDown(android.view.View, int, int)} was invoked.
     *
     * @return True if this popup is showing above the anchor view, false otherwise.
     */
    public boolean isAboveAnchor() {
        return mAboveAnchor;
    }

    /**
     * <p>Prepare the popup by embedding in into a new ViewGroup if the
     * background drawable is not null. If embedding is required, the layout
     * parameters' height is mnodified to take into account the background's
     * padding.</p>
     *
     * @param p the layout parameters of the popup's content view
     */
    private void preparePopup(WindowManager.LayoutParams p) {
        if (mContentView == null || mContext == null || mWindowManager == null) {
            throw new IllegalStateException("You must specify a valid content view by "
                    + "calling setContentView() before attempting to show the popup.");
        }

        if (getBackground() != null) {
            final ViewGroup.LayoutParams layoutParams = mContentView.getLayoutParams();
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            if (layoutParams != null &&
                    layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            // when a background is available, we embed the content view
            // within another view that owns the background drawable
            PopupViewContainer popupViewContainer = new PopupViewContainer(mContext);
            PopupViewContainer.LayoutParams listParams = new PopupViewContainer.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, height
            );
            popupViewContainer.setBackgroundDrawable(getBackground());
            popupViewContainer.addView(mContentView, listParams);

            mPopupView = popupViewContainer;
        } else {
            mPopupView = mContentView;
        }
        mPopupWidth = p.width;
        mPopupHeight = p.height;
    }

    /**
     * <p>Invoke the popup window by adding the content view to the window
     * manager.</p>
     *
     * <p>The content view must be non-null when this method is invoked.</p>
     *
     * @param p the layout parameters of the popup's content view
     */
    private void invokePopup(WindowManager.LayoutParams p) {
        if (mContext != null) {
            p.packageName = mContext.getPackageName();
        }
        mPopupView.setFitsSystemWindows(mLayoutInsetDecor);
        mWindowManager.addView(mPopupView, p);
    }

    /**
     * <p>Generate the layout parameters for the popup window.</p>
     *
     * @param token the window token used to bind the popup's window
     *
     * @return the layout parameters to pass to the window manager
     */
    private WindowManager.LayoutParams createPopupLayout(IBinder token) {
        // generates the layout parameters for the drop down
        // we want a fixed size view located at the bottom left of the anchor
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        // these gravity settings put the view at the top left corner of the
        // screen. The view is then positioned to the appropriate location
        // by setting the x and y offsets to match the anchor's bottom
        // left corner
        p.gravity = Gravity.LEFT | Gravity.TOP;
        p.width = mLastWidth = getWidth();
        p.height = mLastHeight = getHeight();
        if (getBackground() != null) {
            p.format = getBackground().getOpacity();
        } else {
            p.format = PixelFormat.TRANSLUCENT;
        }
        p.flags = computeFlags(p.flags);
        p.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL; //getWindowLayoutType();
        p.token = token;
        p.softInputMode = getSoftInputMode();
        p.setTitle("PopupWindow:" + Integer.toHexString(hashCode()));

        return p;
    }

    private int computeFlags(int curFlags) {
        curFlags &= ~(
                WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                WindowManager.LayoutParams.FLAG_SPLIT_TOUCH);
        if(mIgnoreCheekPress) {
            curFlags |= WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES;
        }
        if (!isFocusable()) {
            curFlags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            if (getInputMethodMode() == INPUT_METHOD_NEEDED) {
                curFlags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
            }
        } else if (getInputMethodMode() == INPUT_METHOD_NOT_NEEDED) {
            curFlags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        }
        if (!isTouchable()) {
            curFlags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }
        if (isOutsideTouchable()) {
            curFlags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        }
        if (!isClippingEnabled()) {
            curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        }
        if (isSplitTouchEnabled()) {
            curFlags |= WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
        }
//        if (isLayoutInScreenEnabled()) {
//            curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
//        }
        if (mLayoutInsetDecor) {
            curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
        }
        if (mNotTouchModal) {
            curFlags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        }
        return curFlags;
    }

    private int computeAnimationResource() {
        if (getAnimationStyle() == -1) {
            if (mIsDropdown) {
                return mAboveAnchor
                        ? mContext.getResources().getIdentifier("Animation_DropDownUp", "style", "android")
                        : mContext.getResources().getIdentifier("Animation_DropDownDown", "style", "android");
            }
            return 0;
        }
        return getAnimationStyle();
    }

    /**
     * <p>Positions the popup window on screen. When the popup window is too
     * tall to fit under the anchor, a parent scroll view is seeked and scrolled
     * up to reclaim space. If scrolling is not possible or not enough, the
     * popup window gets moved on top of the anchor.</p>
     *
     * <p>The height must have been set on the layout parameters prior to
     * calling this method.</p>
     *
     * @param anchor the view on which the popup window must be anchored
     * @param p the layout parameters used to display the drop down
     *
     * @return true if the popup is translated upwards to fit on screen
     */
    private boolean findDropDownPosition(View anchor, WindowManager.LayoutParams p,
            int xoff, int yoff) {

        final int anchorHeight = anchor.getHeight();
        anchor.getLocationInWindow(mDrawingLocation);
        p.x = mDrawingLocation[0] + xoff;
        p.y = mDrawingLocation[1] + anchorHeight + yoff;

        boolean onTop = false;

        p.gravity = Gravity.LEFT | Gravity.TOP;

        anchor.getLocationOnScreen(mScreenLocation);
        final Rect displayFrame = new Rect();
        anchor.getWindowVisibleDisplayFrame(displayFrame);


        final View root = anchor.getRootView();
        int screenY = mScreenLocation[1] - root.getHeight() + yoff;

        onTop = true;
        p.gravity = Gravity.LEFT | Gravity.BOTTOM;
        p.y = root.getHeight() - mDrawingLocation[1] + yoff;

        if (screenY < displayFrame.top ||
                p.x + mPopupWidth - root.getWidth() > 0) {
            // if the drop down disappears at the bottom of the screen. we try to
            // scroll a parent scrollview or move the drop down back up on top of
            // the edit box
            if (mAllowScrollingAnchorParent) {
                int scrollX = anchor.getScrollX();
                int scrollY = anchor.getScrollY();
                Rect r = new Rect(scrollX, scrollY,  scrollX + mPopupWidth + xoff,
                        scrollY + mPopupHeight + anchor.getHeight() + yoff);
                anchor.requestRectangleOnScreen(r, true);
            }

            // now we re-evaluate the space available, and decide from that
            // whether the pop-up will go above or below the anchor.
            anchor.getLocationInWindow(mDrawingLocation);
            p.x = mDrawingLocation[0] + xoff;
            p.y = mDrawingLocation[1] + anchor.getHeight() + yoff;

            // determine whether there is more space above or below the anchor
            anchor.getLocationOnScreen(mScreenLocation);

            View contentView = getContentView();
            View contentRootView = (null != contentView)?contentView.getRootView():null;
            int contentHeight = ( null != contentRootView )?contentRootView.getHeight():0;
            int AboveSpace = mScreenLocation[1] - yoff - displayFrame.top;
            int BelowSpace = displayFrame.bottom - mScreenLocation[1] - anchor.getHeight() - yoff;

            if ( 0 < contentHeight && contentHeight < AboveSpace ) {
                onTop = true;
            } else {
                onTop = BelowSpace < AboveSpace;
            }

            if (onTop) {
                p.gravity = Gravity.LEFT | Gravity.BOTTOM;
                p.y = root.getHeight() - mDrawingLocation[1] + yoff;
            } else {
                p.gravity = Gravity.LEFT | Gravity.TOP;
                p.y = mDrawingLocation[1] + anchor.getHeight() + yoff;
            }
        }

        if (mClipToScreen) {
            final int displayFrameWidth = displayFrame.right - displayFrame.left;

            int right = p.x + p.width;
            if (right > displayFrameWidth) {
                p.x -= right - displayFrameWidth;
            }
            if (p.x < displayFrame.left) {
                p.x = displayFrame.left;
                p.width = Math.min(p.width, displayFrameWidth);
            }

            if (onTop) {
                int popupTop = mScreenLocation[1] + yoff - mPopupHeight;
                if (popupTop < 0) {
                    p.y += popupTop;
                }
            } else {
                p.y = Math.max(p.y, displayFrame.top);
            }
        }

        p.gravity |= Gravity.DISPLAY_CLIP_VERTICAL;

        return onTop;
    }

    /**
     * <p>Dispose of the popup window. This method can be invoked only after
     * {@link #showAsDropDown(android.view.View)} has been executed. Failing that, calling
     * this method will have no effect.</p>
     *
     * @see #showAsDropDown(android.view.View)
     */
    public void dismiss() {
        if (isShowing() && mPopupView != null) {
            mIsShowing = false;

            unregisterForScrollChanged();

            try {
                mWindowManager.removeView(mPopupView);
            } finally {
                if (mPopupView != mContentView && mPopupView instanceof ViewGroup) {
                    ((ViewGroup) mPopupView).removeView(mContentView);
                }
                mPopupView = null;

                if (mOnDismissListener != null) {
                    mOnDismissListener.onDismiss();
                }
            }
        }
    }

    /**
     * Sets the listener to be called when the window is dismissed.
     *
     * @param onDismissListener The listener.
     */
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    /**
     * Updates the state of the popup window, if it is currently being displayed,
     * from the currently set state.  This include:
     * {@link #setClippingEnabled(boolean)}, {@link #setFocusable(boolean)},
     * {@link #setIgnoreCheekPress()}, {@link #setInputMethodMode(int)},
     * {@link #setTouchable(boolean)}, and {@link #setAnimationStyle(int)}.
     */
    public void update() {
        if (!isShowing() || mContentView == null) {
            return;
        }

        WindowManager.LayoutParams p = (WindowManager.LayoutParams)
                mPopupView.getLayoutParams();

        boolean update = false;

        final int newAnim = computeAnimationResource();
        if (newAnim != p.windowAnimations) {
            p.windowAnimations = newAnim;
            update = true;
        }

        final int newFlags = computeFlags(p.flags);
        if (newFlags != p.flags) {
            p.flags = newFlags;
            update = true;
        }

        if (update) {
            mWindowManager.updateViewLayout(mPopupView, p);
        }
    }

    /**
     * <p>Updates the dimension of the popup window. Calling this function
     * also updates the window with the current popup state as described
     * for {@link #update()}.</p>
     *
     * @param width the new width
     * @param height the new height
     */
    public void update(int width, int height) {
        WindowManager.LayoutParams p = (WindowManager.LayoutParams)
                mPopupView.getLayoutParams();
        update(p.x, p.y, width, height, false);
    }

    /**
     * <p>Updates the position and the dimension of the popup window. Width and
     * height can be set to -1 to update location only.  Calling this function
     * also updates the window with the current popup state as
     * described for {@link #update()}.</p>
     *
     * @param x the new x location
     * @param y the new y location
     * @param width the new width, can be -1 to ignore
     * @param height the new height, can be -1 to ignore
     */
    public void update(int x, int y, int width, int height) {
        update(x, y, width, height, false);
    }

    /**
     * <p>Updates the position and the dimension of the popup window. Width and
     * height can be set to -1 to update location only.  Calling this function
     * also updates the window with the current popup state as
     * described for {@link #update()}.</p>
     *
     * @param x the new x location
     * @param y the new y location
     * @param width the new width, can be -1 to ignore
     * @param height the new height, can be -1 to ignore
     * @param force reposition the window even if the specified position
     *              already seems to correspond to the LayoutParams
     */
    public void update(int x, int y, int width, int height, boolean force) {
        if (width != -1) {
            mLastWidth = width;
            setWidth(width);
        }

        if (height != -1) {
            mLastHeight = height;
            setHeight(height);
        }

        if (!isShowing() || mContentView == null) {
            return;
        }

        WindowManager.LayoutParams p = (WindowManager.LayoutParams) mPopupView.getLayoutParams();

        boolean update = force;

        final int finalWidth = mWidthMode < 0 ? mWidthMode : mLastWidth;
        if (width != -1 && p.width != finalWidth) {
            p.width = mLastWidth = finalWidth;
            update = true;
        }

        final int finalHeight = mHeightMode < 0 ? mHeightMode : mLastHeight;
        if (height != -1 && p.height != finalHeight) {
            p.height = mLastHeight = finalHeight;
            update = true;
        }

        if (p.x != x) {
            p.x = x;
            update = true;
        }

        if (p.y != y) {
            p.y = y;
            update = true;
        }

        final int newAnim = computeAnimationResource();
        if (newAnim != p.windowAnimations) {
            p.windowAnimations = newAnim;
            update = true;
        }

        final int newFlags = computeFlags(p.flags);
        if (newFlags != p.flags) {
            p.flags = newFlags;
            update = true;
        }

        if (update) {
            mWindowManager.updateViewLayout(mPopupView, p);
        }
    }

    /**
     * <p>Updates the position and the dimension of the popup window. Calling this
     * function also updates the window with the current popup state as described
     * for {@link #update()}.</p>
     *
     * @param anchor the popup's anchor view
     * @param width the new width, can be -1 to ignore
     * @param height the new height, can be -1 to ignore
     */
    public void update(View anchor, int width, int height) {
        update(anchor, false, 0, 0, true, width, height);
    }

    /**
     * <p>Updates the position and the dimension of the popup window. Width and
     * height can be set to -1 to update location only.  Calling this function
     * also updates the window with the current popup state as
     * described for {@link #update()}.</p>
     *
     * <p>If the view later scrolls to move <code>anchor</code> to a different
     * location, the popup will be moved correspondingly.</p>
     *
     * @param anchor the popup's anchor view
     * @param xoff x offset from the view's left edge
     * @param yoff y offset from the view's bottom edge
     * @param width the new width, can be -1 to ignore
     * @param height the new height, can be -1 to ignore
     */
    public void update(View anchor, int xoff, int yoff, int width, int height) {
        update(anchor, true, xoff, yoff, true, width, height);
    }

    private void update(View anchor, boolean updateLocation, int xoff, int yoff,
            boolean updateDimension, int width, int height) {

        if (!isShowing() || mContentView == null) {
            return;
        }

        WeakReference<View> oldAnchor = mAnchor;
        final boolean needsUpdate = updateLocation && (mAnchorXoff != xoff || mAnchorYoff != yoff);
        if (oldAnchor == null || oldAnchor.get() != anchor || (needsUpdate && !mIsDropdown)) {
            registerForScrollChanged(anchor, xoff, yoff);
        } else if (needsUpdate) {
            // No need to register again if this is a DropDown, showAsDropDown already did.
            mAnchorXoff = xoff;
            mAnchorYoff = yoff;
        }

        WindowManager.LayoutParams p = (WindowManager.LayoutParams) mPopupView.getLayoutParams();

        if (updateDimension) {
            if (width == -1) {
                width = mPopupWidth;
            } else {
                mPopupWidth = width;
            }
            if (height == -1) {
                height = mPopupHeight;
            } else {
                mPopupHeight = height;
            }
        }

        int x = p.x;
        int y = p.y;

        if (updateLocation) {
            updateAboveAnchor(findDropDownPosition(anchor, p, xoff, yoff));
        } else {
            updateAboveAnchor(findDropDownPosition(anchor, p, mAnchorXoff, mAnchorYoff));
        }

        update(p.x, p.y, width, height, x != p.x || y != p.y);
    }

    private void unregisterForScrollChanged() {
        WeakReference<View> anchorRef = mAnchor;
        View anchor = null;
        if (anchorRef != null) {
            anchor = anchorRef.get();
        }
        if (anchor != null) {
            ViewTreeObserver vto = anchor.getViewTreeObserver();
            vto.removeOnScrollChangedListener(mOnScrollChangedListener);
        }
        mAnchor = null;
    }

    private void registerForScrollChanged(View anchor, int xoff, int yoff) {
        unregisterForScrollChanged();

        mAnchor = new WeakReference<View>(anchor);
        ViewTreeObserver vto = anchor.getViewTreeObserver();
        if (vto != null) {
            vto.addOnScrollChangedListener(mOnScrollChangedListener);
        }

        mAnchorXoff = xoff;
        mAnchorYoff = yoff;
    }

    private class PopupViewContainer extends FrameLayout {
        private static final String TAG = "PopupWindow.PopupViewContainer";
        Rect mTmp = new Rect();

        public PopupViewContainer(Context context) {
            super(context);
        }

        @Override
        protected int[] onCreateDrawableState(int extraSpace) {
            if (mAboveAnchor) {
                // 1 more needed for the above anchor state
                final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
                View.mergeDrawableStates(drawableState, ABOVE_ANCHOR_STATE_SET);
                return drawableState;
            } else {
                return super.onCreateDrawableState(extraSpace);
            }
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                if (getKeyDispatcherState() == null) {
                    return super.dispatchKeyEvent(event);
                }

                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getRepeatCount() == 0) {
                    KeyEvent.DispatcherState state = getKeyDispatcherState();
                    if (state != null) {
                        state.startTracking(event, this);
                    }
                    return true;
                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    KeyEvent.DispatcherState state = getKeyDispatcherState();
                    if (state != null && state.isTracking(event) && !event.isCanceled()) {
                        dismiss();
                        return true;
                    }
                }
                return super.dispatchKeyEvent(event);
            } else {
                return super.dispatchKeyEvent(event);
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (mTouchInterceptor != null && mTouchInterceptor.onTouch(this, ev)) {
                return true;
            }
            return super.dispatchTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            final int x = (int) event.getX();
            final int y = (int) event.getY();

            if ((event.getAction() == MotionEvent.ACTION_DOWN)
                    && ((x < 0) || (x >= getWidth()) || (y < 0) || (y >= getHeight()))) {
                dismiss();
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                dismiss();
                return true;
            } else {
                return super.onTouchEvent(event);
            }
        }

        @Override
        public void sendAccessibilityEvent(int eventType) {
            // clinets are interested in the content not the container, make it event source
            if (mContentView != null) {
                mContentView.sendAccessibilityEvent(eventType);
            } else {
                super.sendAccessibilityEvent(eventType);
            }
        }

        /* (non-Javadoc)
         * @see android.widget.FrameLayout#drawableStateChanged()
         */
        @Override
        protected void drawableStateChanged() {
            // TODO Auto-generated method stub
            super.drawableStateChanged();
            Drawable d = getBackground();
            d.getPadding(mTempRect);
            setPadding(mTempRect.left, mTempRect.top, mTempRect.right, mTempRect.bottom);
        }

        /* (non-Javadoc)
         * @see android.widget.FrameLayout#onSizeChanged(int, int, int, int)
         */
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            // TODO Auto-generated method stub
            super.onSizeChanged(w, h, oldw, oldh);
            HtcPopupWindow.this.onSizeChanged(w, h, oldw, oldh);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
           // TODO Auto-generated method stub
           int nLastMeasureWidth = getMeasuredWidth();
           int nLastMeasureHeight = getMeasuredHeight();
           super.onMeasure(widthMeasureSpec, heightMeasureSpec);
           int nNowMeasureWidth = getMeasuredWidth();
           int nNowMeasureHeight = getMeasuredHeight();

           Drawable d = getBackground();
           if ( null != d ) {
              d.getPadding(mTmp);
              if ( nNowMeasureWidth == (mTmp.left + mTmp.right) && nNowMeasureHeight == (mTmp.top + mTmp.bottom) ) {
                 setMeasuredDimension(nLastMeasureWidth, nLastMeasureHeight);
              }
           }
        }

        @Override
        public void draw(Canvas canvas) {
           // TODO Auto-generated method stub
           if ( isShowing() ) {
              super.draw(canvas);
           }
        }
    }

    protected interface OnPopupChangedListener {
        void onSizeChanged(int w, int h, int oldw, int oldh);
    }

    private OnPopupChangedListener mPopupChangeListener;

    /* (non-Javadoc)
     * @see android.widget.FrameLayout#onSizeChanged(int, int, int, int)
     */
    void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        if ( null != getPopupChangeListener() ) {
            getPopupChangeListener().onSizeChanged(w, h, oldw, oldh);
        }
    }

    /**
     * @return the mPopupChangeListener
     */
    protected OnPopupChangedListener getPopupChangeListener() {
        return mPopupChangeListener;
    }

    /**
     * @param mPopupChangeListener the mPopupChangeListener to set
     */
    protected void setPopupChangeListener(OnPopupChangedListener popupChangeListener) {
        this.mPopupChangeListener = popupChangeListener;
    }
}
