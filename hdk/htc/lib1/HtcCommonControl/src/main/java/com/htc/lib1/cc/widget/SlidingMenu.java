
package com.htc.lib1.cc.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewDebug.ExportedProperty;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;
import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.widget.SlidingMenuView.OnSizeChangedListener;

import java.util.ArrayList;
import java.util.List;

public class SlidingMenu extends FrameLayout {

    private static final String TAG = "SlidingMenu";

    public static final int SLIDING_WINDOW = 0;
    public static final int SLIDING_CONTENT = 1;
    private boolean mActionbarOverlay = false;

    /**
     * Constant value for use with setTouchModeAbove(). Allows the SlidingMenu
     * to be opened with a swipe gesture on the screen's margin
     */
    public static final int TOUCHMODE_MARGIN = 0;

    /**
     * Constant value for use with setTouchModeAbove(). Allows the SlidingMenu
     * to be opened with a swipe gesture anywhere on the screen
     */
    public static final int TOUCHMODE_FULLSCREEN = 1;

    /**
     * Constant value for use with setTouchModeAbove(). Denies the SlidingMenu
     * to be opened with a swipe gesture
     */
    public static final int TOUCHMODE_NONE = 2;

    /**
     * Constant value for use with setMode(). Puts the menu to the left of the
     * content.
     */
    public static final int LEFT = 0;

    /**
     * Constant value for use with setMode(). Puts the menu to the right of the
     * content.
     */
    public static final int RIGHT = 1;

    /**
     * Constant value for use with setMode(). Puts menus to the left and right
     * of the content.
     */
    public static final int LEFT_RIGHT = 2;

    private SlidingContentView mSlidingContentView;

    private SlidingMenuView mSlidingMenuView;

    private OnOpenListener mOpenListener;

    private OnCloseListener mCloseListener;

    private static final int FIRST_MENU = 0;

    private static final int CONTENT_VIEW = 1;

    private static final int SECOND_MENU = 2;

    private Runnable mRunToggle;

    private static final int INVALID_POINTER_INDEX = MotionEvent.INVALID_POINTER_ID;

    private float mActionX;

    private float mActionY;

    /**
     * The listener interface for receiving onOpen events. The class that is
     * interested in processing a onOpen event implements this interface, and
     * the object created with that class is registered with a component using
     * the component's <code>addOnOpenListener<code> method. When
     * the onOpen event occurs, that object's appropriate
     * method is invoked
     */
    public interface OnOpenListener {

        /**
         * On open.
         */
        public void onOpen();
    }

    /**
     * The listener interface for receiving onOpened events. The class that is
     * interested in processing a onOpened event implements this interface, and
     * the object created with that class is registered with a component using
     * the component's <code>addOnOpenedListener<code> method. When
     * the onOpened event occurs, that object's appropriate
     * method is invoked.
     *
     * @see OnOpenedEvent
     */
    public interface OnOpenedListener {

        /**
         * On opened.
         */
        public void onOpened();
    }

    /**
     * The listener interface for receiving onClose events. The class that is
     * interested in processing a onClose event implements this interface, and
     * the object created with that class is registered with a component using
     * the component's <code>addOnCloseListener<code> method. When
     * the onClose event occurs, that object's appropriate
     * method is invoked.
     *
     * @see OnCloseEvent
     */
    public interface OnCloseListener {

        /**
         * On close.
         */
        public void onClose();
    }

    /**
     * The listener interface for receiving onClosed events. The class that is
     * interested in processing a onClosed event implements this interface, and
     * the object created with that class is registered with a component using
     * the component's <code>addOnClosedListener<code> method. When
     * the onClosed event occurs, that object's appropriate
     * method is invoked.
     *
     * @see OnClosedEvent
     */
    public interface OnClosedListener {

        /**
         * On closed.
         */
        public void onClosed();
    }

    /**
     * The Interface CanvasTransformer.
     */
    public interface CanvasTransformer {

        /**
         * Transform canvas.
         *
         * @param canvas the canvas
         * @param percentOpen the percent open
         */
        public void transformCanvas(Canvas canvas, float percentOpen);
    }

    /**
     * Instantiates a new SlidingMenu.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public SlidingMenu(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new SlidingMenu and attach to Activity.
     *
     * @param activity the activity to attach slidingmenu
     * @param slideStyle the slidingmenu style
     */
    public SlidingMenu(Activity activity, int slideStyle) {
        this(activity, null);
        attachToActivity(activity, slideStyle);
    }

    /**
     * Instantiates a new SlidingMenu.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs the attrs
     */
    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new SlidingMenu.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs the attrs
     * @param defStyle the def style
     */
    public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);
        initSlidingMenu();

        LayoutParams behindParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mSlidingMenuView = new SlidingMenuView(context);
        mSlidingMenuView.setOnSizeChangedListener(new OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                if (0 != w && 0 != oldw && isMenuShowing() && !mScroller.isFinished()) {
                    showContent(false);
                }
            }
        });
        mSlidingMenuView.setVisibility(INVISIBLE);
        addView(mSlidingMenuView, behindParams);
        LayoutParams aboveParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mSlidingContentView = new SlidingContentView(context);
        addView(mSlidingContentView, aboveParams);
        mScrollX = mSlidingContentView.getScrollX();
        mSlidingMenuView.bringToFront();
        // register the CustomViewBehind with the CustomViewAbove
        mSlidingContentView.setCustomMenuView(mSlidingMenuView);
        mSlidingMenuView.setCustomContentView(mSlidingContentView);
        setOnPageChangeListener(new SimpleOnPageChangeListener() {

            public void onPageSelected(int position) {
                if (position == POSITION_OPEN && mOpenListener != null) {
                    mOpenListener.onOpen();
                } else if (position == POSITION_CLOSE && mCloseListener != null) {
                    mCloseListener.onClose();
                }
            }
        });

        // init
        setMode(LEFT);
        setContent(new FrameLayout(context));
        setMenu(new FrameLayout(context));
        setTouchModeAbove(TOUCHMODE_MARGIN);
        setTouchModeBehind(TOUCHMODE_MARGIN);
        setBehindOffset(0);
        setBehindWidth(0);
        setBehindScrollScale(1f);
        setShadowWidth(0);
        setFadeEnabled(false);
        setFadeDegree(0.5f);
        setSelectorEnabled(false);
    }

    private void initSlidingMenu() {
        setWillNotDraw(false);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setFocusable(true);
        final Context context = getContext();
        mScroller = new Scroller(context, sInterpolator);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledPagingTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mMarginThreshold = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                MARGIN_THRESHOLD, getResources().getDisplayMetrics());

        final float density = context.getResources().getDisplayMetrics().density;
        mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);
    }

    /**
     * Attaches the SlidingMenu to an entire Activity
     *
     * @param activity the Activity
     * @param slideStyle either SLIDING_CONTENT or SLIDING_WINDOW
     */
    public void attachToActivity(Activity activity, int slideStyle) {
        attachToActivity(activity, slideStyle, false);
    }

    /**
     * Attaches the SlidingMenu to an entire Activity
     *
     * @param activity the Activity
     * @param slideStyle either SLIDING_CONTENT or SLIDING_WINDOW
     * @param actionbarOverlay whether or not the ActionBar is overlaid
     */
    public void attachToActivity(Activity activity, int slideStyle, boolean actionbarOverlay) {
        if (slideStyle != SLIDING_WINDOW && slideStyle != SLIDING_CONTENT) {
            throw new IllegalArgumentException(
                    "slideStyle must be either SLIDING_WINDOW or SLIDING_CONTENT");
        }

        if (getParent() != null) {
            throw new IllegalStateException("This SlidingMenu appears to already be attached");
        }

        if (SLIDING_WINDOW == slideStyle) {
            mActionbarOverlay = false;
            ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
            ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
            decor.removeView(decorChild);
            decor.addView(this);
            setContent(decorChild);
        } else {
            mActionbarOverlay = actionbarOverlay;
            // take the above view out of
            ViewGroup contentParent = (ViewGroup) activity.findViewById(android.R.id.content);
            View content = contentParent.getChildAt(0);
            contentParent.removeView(content);
            contentParent.addView(this);
            setContent(content);
        }
    }

    /**
     * Set the above view content from a layout resource. The resource will be
     * inflated, adding all top-level views to the above view.
     *
     * @param res the new content
     */
    public void setContent(int res) {
        setContent(LayoutInflater.from(getContext()).inflate(res, null));
    }

    /**
     * Set the above view content to the given View.
     *
     * @param contentView The desired content to display.
     */
    public void setContent(View contentView) {
        mSlidingContentView.setContent(contentView);
        showContent();
    }

    /**
     * Retrieves the current content.
     *
     * @return the current content
     */
    public View getContent() {
        return mSlidingContentView.getContent();
    }

    /**
     * Set the behind view (menu) content from a layout resource. The resource
     * will be inflated, adding all top-level views to the behind view.
     *
     * @param resId the new content
     */
    public void setMenu(int resId) {
        setMenu(LayoutInflater.from(getContext()).inflate(resId, null));
    }

    /**
     * Set the behind view (menu) content to the given View.
     *
     * @param menuView The desired content to display.
     */
    public void setMenu(View menuView) {
        mSlidingMenuView.setContent(menuView);
    }

    /**
     * Retrieves the main menu.
     *
     * @return the main menu
     */
    public View getMenu() {
        return mSlidingMenuView.getContent();
    }

    /**
     * Set the secondary behind view (right menu) content from a layout
     * resource. The resource will be inflated, adding all top-level views to
     * the behind view.
     *
     * @param resId the new content
     */
    public void setSecondaryMenu(int resId) {
        setSecondaryMenu(LayoutInflater.from(getContext()).inflate(resId, null));
    }

    /**
     * Set the secondary behind view (right menu) content to the given View.
     *
     * @param secondaryMenuView The desired content to display.
     */
    public void setSecondaryMenu(View secondaryMenuView) {
        mSlidingMenuView.setSecondaryContent(secondaryMenuView);
    }

    /**
     * Retrieves the current secondary menu (right).
     *
     * @return the current menu
     */
    public View getSecondaryMenu() {
        return mSlidingMenuView.getSecondaryContent();
    }

    /**
     * Sets the sliding enabled.
     *
     * @param isSlidingEnabled true to enable sliding, false to disable it.
     */
    public void setSlidingEnabled(boolean isSlidingEnabled) {
        log("setSlidingEnabled", "isSlidingEnabled = " + isSlidingEnabled);
        mSlidingEnabled = isSlidingEnabled;
    }

    /**
     * Checks if is sliding enabled.
     *
     * @return true, if is sliding enabled
     */
    public boolean isSlidingEnabled() {
        return mSlidingEnabled;
    }

    /**
     * Sets which side the SlidingMenu should appear on.
     *
     * @param mode must be either SlidingMenu.LEFT or SlidingMenu.RIGHT
     */
    public void setMode(int mode) {
        if (mode != LEFT && mode != RIGHT && mode != LEFT_RIGHT) {
            throw new IllegalStateException("SlidingMenu mode must be LEFT, RIGHT, or LEFT_RIGHT");
        }
        mSlidingMenuView.setMode(mode);
    }

    /**
     * Returns the current side that the SlidingMenu is on.
     *
     * @return the current mode, either SlidingMenu.LEFT or SlidingMenu.RIGHT
     */
    public int getMode() {
        return mSlidingMenuView.getMode();
    }

    /**
     * Sets whether or not the SlidingMenu is in static mode (i.e. nothing is
     * moving and everything is showing)
     *
     * @param isStatic true to set static mode, false to disable static mode.
     */
    public void setStatic(boolean isStatic) {
        if (null != mSlidingContentView) {
            if (isStatic) {
                setSlidingEnabled(false);
                mSlidingContentView.setCustomMenuView(null);
                setCurrentItem(CONTENT_VIEW);
            } else {
                setCurrentItem(CONTENT_VIEW);
                mSlidingContentView.setCustomMenuView(mSlidingMenuView);
                setSlidingEnabled(true);
            }
        }

    }

    /**
     * Opens the menu and shows the menu view.
     */
    public void showMenu() {
        showMenu(true);
    }

    /**
     * Opens the menu and shows the menu view.
     *
     * @param animate true to animate the transition, false to ignore animation
     */
    public void showMenu(boolean animate) {
        setCurrentItem(FIRST_MENU, animate);
    }

    /**
     * Opens the menu and shows the secondary menu view. Will default to the
     * regular menu if there is only one.
     */
    public void showSecondaryMenu() {
        showSecondaryMenu(true);
    }

    /**
     * Opens the menu and shows the secondary (right) menu view. Will default to
     * the regular menu if there is only one.
     *
     * @param animate true to animate the transition, false to ignore animation
     */
    public void showSecondaryMenu(boolean animate) {
        setCurrentItem(SECOND_MENU, animate);
    }

    /**
     * Closes the menu and shows the above view.
     */
    public void showContent() {
        showContent(true);
    }

    /**
     * Closes the menu and shows the above view.
     *
     * @param animate true to animate the transition, false to ignore animation
     */
    public void showContent(boolean animate) {
        setCurrentItem(CONTENT_VIEW, animate);
    }

    /**
     * Toggle the SlidingMenu. If it is open, it will be closed, and vice versa.
     */
    public void toggle() {
        toggle(true);
    }

    /**
     * Toggle the SlidingMenu. If it is open, it will be closed, and vice versa.
     *
     * @param animate true to animate the transition, false to ignore animation
     */
    public void toggle(final boolean animate) {
        if (null != mRunToggle) {
            removeCallbacks(mRunToggle);
        }

        mRunToggle = new Runnable() {
            @Override
            public void run() {
                if (isMenuShowing()) {
                    showContent(animate);
                } else {
                    showMenu(animate);
                }
                mRunToggle = null;
            }
        };
        post(mRunToggle);
    }

    /**
     * Checks if is the behind view showing.
     *
     * @return Whether or not the behind view is showing
     */
    public boolean isMenuShowing() {
        return FIRST_MENU == getCurrentItem() || SECOND_MENU == getCurrentItem();
    }

    /**
     * Checks if is the behind view showing.
     *
     * @return Whether or not the behind view is showing
     */
    public boolean isSecondaryMenuShowing() {
        return SECOND_MENU == getCurrentItem();
    }

    /**
     * Gets the behind offset.
     *
     * @return The margin on the right of the screen that the behind view
     *         scrolls to
     */
    public int getBehindOffset() {
        return mSlidingMenuView.getWidthOffset();
    }

    /**
     * Sets the behind offset.
     *
     * @param offset The margin, in pixels, on the right of the screen that the
     *            behind view scrolls to.
     */
    public void setBehindOffset(int offset) {
        mSlidingMenuView.setWidthOffset(offset);
    }

    /**
     * Sets the behind offset.
     *
     * @param resID The dimension resource id to be set as the behind offset.
     *            The menu, when open, will leave this width margin on the right
     *            of the screen.
     */
    public void setBehindOffsetRes(int resID) {
        final int offset = (int) getContext().getResources().getDimension(resID);
        setBehindOffset(offset);
    }

    /**
     * Sets the above offset.
     *
     * @param offset the new above offset, in pixels
     */
    public void setAboveOffset(int offset) {
        mSlidingContentView.setAboveOffset(offset);
    }

    /**
     * Sets the above offset.
     *
     * @param resID The dimension resource id to be set as the above offset.
     */
    public void setAboveOffsetRes(int resID) {
        final int offset = (int) getContext().getResources().getDimension(resID);
        setAboveOffset(offset);
    }

    /**
     * Sets the behind width.
     *
     * @param benhindWidth The width the Sliding Menu will open to, in pixels
     */
    @SuppressWarnings("deprecation")
    public void setBehindWidth(int benhindWidth) {
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        Point parameter = new Point();
        display.getSize(parameter);
        final int width = parameter.x;
        setBehindOffset(width - benhindWidth);
    }

    /**
     * Sets the behind width.
     *
     * @param resId The dimension resource id to be set as the behind width
     *            offset. The menu, when open, will open this wide.
     */
    public void setBehindWidthRes(int resId) {
        final int offset = (int) getContext().getResources().getDimension(resId);
        setBehindWidth(offset);
    }

    /**
     * Gets the behind scroll scale.
     *
     * @return The scale of the parallax scroll
     */
    public float getBehindScrollScale() {
        return mMenuScrollScale;
    }

    /**
     * Sets the behind scroll scale.
     *
     * @param scale The scale of the parallax scroll (i.e. 1.0f scrolls 1 pixel for
     *            every 1 pixel that the above view scrolls and 0.0f scrolls 0
     *            pixels)
     */
    public void setBehindScrollScale(float scale) {
        if (scale < 0 && scale > 1) {
            throw new IllegalStateException("ScrollScale must be between 0 and 1");
        }
        mMenuScrollScale = scale;
    }

    /**
     * Sets the behind canvas transformer.
     *
     * @param canvasTransformer the new behind canvas transformer
     */
    public void setBehindCanvasTransformer(CanvasTransformer canvasTransformer) {
        mSlidingMenuView.setCanvasTransformer(canvasTransformer);
    }

    /**
     * Gets the touch mode above.
     *
     * @return the touch mode above
     */
    public int getTouchModeAbove() {
        return mSlidingContentView.getTouchMode();
    }

    /**
     * Controls whether the SlidingMenu can be opened with a swipe gesture.
     * Options are {@link #TOUCHMODE_MARGIN TOUCHMODE_MARGIN},
     * {@link #TOUCHMODE_FULLSCREEN TOUCHMODE_FULLSCREEN}, or
     * {@link #TOUCHMODE_NONE TOUCHMODE_NONE}
     *
     * @param mode the new touch mode
     */
    public void setTouchModeAbove(int mode) {
        if (mode != TOUCHMODE_FULLSCREEN && mode != TOUCHMODE_MARGIN
                && mode != TOUCHMODE_NONE) {
            throw new IllegalStateException("TouchMode must be set to either" +
                    "TOUCHMODE_FULLSCREEN or TOUCHMODE_MARGIN or TOUCHMODE_NONE.");
        }
        mSlidingContentView.setTouchMode(mode);
    }

    /**
     * Controls whether the SlidingMenu can be opened with a swipe gesture.
     * Options are {@link #TOUCHMODE_MARGIN TOUCHMODE_MARGIN},
     * {@link #TOUCHMODE_FULLSCREEN TOUCHMODE_FULLSCREEN}, or
     * {@link #TOUCHMODE_NONE TOUCHMODE_NONE}
     *
     * @param mode the new touch mode
     */
    public void setTouchModeBehind(int mode) {
        if (mode != TOUCHMODE_FULLSCREEN && mode != TOUCHMODE_MARGIN
                && mode != TOUCHMODE_NONE) {
            throw new IllegalStateException("TouchMode must be set to either" +
                    "TOUCHMODE_FULLSCREEN or TOUCHMODE_MARGIN or TOUCHMODE_NONE.");
        }
        mSlidingMenuView.setTouchMode(mode);
    }

    /**
     * Sets the shadow drawable.
     *
     * @param resId the resource ID of the new shadow drawable
     */
    public void setShadowDrawable(int resId) {
        setShadowDrawable(getContext().getResources().getDrawable(resId));
    }

    /**
     * Sets the shadow drawable.
     *
     * @param shadowDrawable the new shadow drawable
     */
    public void setShadowDrawable(Drawable shadowDrawable) {
        mSlidingMenuView.setShadowDrawable(shadowDrawable);
    }

    /**
     * Sets the secondary (right) shadow drawable.
     *
     * @param resId the resource ID of the new shadow drawable
     */
    public void setSecondaryShadowDrawable(int resId) {
        setSecondaryShadowDrawable(getContext().getResources().getDrawable(resId));
    }

    /**
     * Sets the secondary (right) shadow drawable.
     *
     * @param d the new shadow drawable
     */
    public void setSecondaryShadowDrawable(Drawable d) {
        mSlidingMenuView.setSecondaryShadowDrawable(d);
    }

    /**
     * Sets the shadow width.
     *
     * @param resId The dimension resource id to be set as the shadow width.
     */
    public void setShadowWidthRes(int resId) {
        setShadowWidth((int) getResources().getDimension(resId));
    }

    /**
     * Sets the shadow width.
     *
     * @param shadowWidth the new shadow width, in shadowWidth
     */
    public void setShadowWidth(int shadowWidth) {
        mSlidingMenuView.setShadowWidth(shadowWidth);
    }

    /**
     * Enables or disables the SlidingMenu's fade in and out
     *
     * @param isFadeEnabled true to enable fade, false to disable it
     */
    public void setFadeEnabled(boolean isFadeEnabled) {
        mSlidingMenuView.setFadeEnabled(isFadeEnabled);
    }

    /**
     * Sets how much the SlidingMenu fades in and out. Fade must be enabled, see
     * {@link #setFadeEnabled(boolean) setFadeEnabled(boolean)}
     *
     * @param fadeDegree the new fade degree, between 0.0f and 1.0f
     */
    public void setFadeDegree(float fadeDegree) {
        mSlidingMenuView.setFadeDegree(fadeDegree);
    }

    /**
     * Enables or disables whether the selector is drawn
     *
     * @param isSelectorEnabled true to draw the selector, false to not draw the selector
     */
    public void setSelectorEnabled(boolean isSelectorEnabled) {
        mSlidingMenuView.setSelectorEnabled(isSelectorEnabled);
    }

    /**
     * Sets the selected view. The selector will be drawn here
     *
     * @param selectedView the new selected view
     */
    public void setSelectedView(View selectedView) {
        mSlidingMenuView.setSelectedView(selectedView);
    }

    /**
     * Sets the selector drawable.
     *
     * @param resId a resource ID for the selector drawable
     */
    public void setSelectorDrawable(int resId) {
        mSlidingMenuView.setSelectorBitmap(BitmapFactory.decodeResource(getResources(), resId));
    }

    /**
     * Sets the selector drawable.
     *
     * @param isSelectorBitmap the new selector bitmap
     */
    public void setSelectorBitmap(Bitmap isSelectorBitmap) {
        mSlidingMenuView.setSelectorBitmap(isSelectorBitmap);
    }

    /**
     * Add a View ignored by the Touch Down event when mode is Fullscreen
     *
     * @param ignoredView a view to be ignored
     */
    public void addIgnoredView(View ignoredView) {
        if (!mIgnoredViews.contains(ignoredView)) {
            mIgnoredViews.add(ignoredView);
        }
    }

    /**
     * Remove a View ignored by the Touch Down event when mode is Fullscreen
     *
     * @param ignoredView a view not wanted to be ignored anymore
     */
    public void removeIgnoredView(View ignoredView) {
        mIgnoredViews.remove(ignoredView);
    }

    /**
     * Clear the list of Views ignored by the Touch Down event when mode is
     * Fullscreen
     */
    public void clearIgnoredViews() {
        mIgnoredViews.clear();
    }

    /**
     * Sets the OnOpenListener. {@link OnOpenListener#onOpen()
     * OnOpenListener.onOpen()} will be called when the SlidingMenu is opened
     *
     * @param listener the new OnOpenListener
     */
    public void setOnOpenListener(OnOpenListener listener) {
        mOpenListener = listener;
    }

    /**
     * Sets the OnCloseListener. {@link OnCloseListener#onClose()
     * OnCloseListener.onClose()} will be called when the SlidingMenu is closed
     *
     * @param listener the new setOnCloseListener
     */
    public void setOnCloseListener(OnCloseListener listener) {
        mCloseListener = listener;
    }

    /**
     * Sets the OnOpenedListener. {@link OnOpenedListener#onOpened()
     * OnOpenedListener.onOpened()} will be called after the SlidingMenu is
     * opened
     *
     * @param listener the new OnOpenedListener
     */
    public void setOnOpenedListener(OnOpenedListener listener) {
        mOpenedListener = listener;
    }

    /**
     * Sets the OnClosedListener. {@link OnClosedListener#onClosed()
     * OnClosedListener.onClosed()} will be called after the SlidingMenu is
     * closed
     *
     * @param listener the new OnClosedListener
     */
    public void setOnClosedListener(OnClosedListener listener) {
        mClosedListener = listener;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        // Draw the margin drawable if needed.
        final View v = mSlidingContentView.getContent();
        final float percentOpen = getPercentOpen();
        mSlidingMenuView.drawShadow(v, canvas);
        mSlidingMenuView.drawFade(v, canvas, percentOpen);
        mSlidingMenuView.drawSelector(v, canvas, percentOpen);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final boolean result = super.drawChild(canvas, child, drawingTime);
        if (mSlidingContentView != child) {
            return result;
        }
        final float scrimOpacity = getPercentOpen();
        if (scrimOpacity > 0) {
            final int baseAlpha = (mScrimColor & 0xff000000) >>> 24;
            final int imag = (int) (baseAlpha * scrimOpacity);
            final int color = imag << 24 | (mScrimColor & 0xffffff);
            mScrimPaint.setColor(color);

            canvas.drawRect(0, 0, mSlidingContentView.getRight(), getHeight(), mScrimPaint);
        }
        return result;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        logWithEvent("onInterceptTouchEvent Begin", ev);

        if (!mSlidingEnabled) {
            logWithEvent("onInterceptTouchEvent End", ev);
            return false;
        }

        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        if (MotionEvent.ACTION_CANCEL == action || MotionEvent.ACTION_UP == action
                || (MotionEvent.ACTION_DOWN != action && mIsUnableToDrag)) {
            endDrag();
            logWithEvent("onInterceptTouchEvent End", ev);
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                final int index = ev.getActionIndex();
                mActivePointerId = ev.getPointerId(index);
                if (MotionEvent.INVALID_POINTER_ID == mActivePointerId) {
                    break;
                }
                mLastMotionX = mInitialMotionX = ev.getX(index);
                mLastMotionY = ev.getY(index);
                if (thisTouchAllowed(ev)) {
                    mIsBeingDragged = false;
                    mIsUnableToDrag = false;
                    if (isMenuShowing()
                            && mSlidingMenuView.menuTouchInQuickReturn(
                                    mSlidingContentView.getContent(), mCurItem, ev.getX()
                                            + mScrollX)) {
                        mQuickReturn = true;
                    }
                } else {
                    mIsUnableToDrag = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                determineDrag(ev);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            default:
                break;
        }

        if (!mIsBeingDragged) {
            if (null == mVelocityTracker) {
                mVelocityTracker = VelocityTracker.obtain();
            }
            mVelocityTracker.addMovement(ev);
        }

        logWithEvent("onInterceptTouchEvent End", ev);
        return mIsBeingDragged || mQuickReturn;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        logWithEvent("onTouchEvent Begin", ev);
        final int action = ev.getAction() & MotionEvent.ACTION_MASK;

        if (!mSlidingEnabled) {
            logWithEvent("onTouchEvent End", ev);
            return false;
        }

        if (!mIsBeingDragged && !thisTouchAllowed(ev)) {
            if (MotionEvent.ACTION_UP == action) {
                mQuickReturn = false;
            }
            logWithEvent("onTouchEvent End", ev);
            return false;
        }

        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                /*
                 * If being flinged and user touches, stop the fling. isFinished
                 * will be false if being flinged.
                 */
                completeScroll();

                // Remember where the motion event started
                mActivePointerId = ev.getPointerId(ev.getActionIndex());
                mLastMotionX = mInitialMotionX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mIsBeingDragged) {
                    determineDrag(ev);
                    if (mIsUnableToDrag) {
                        logWithEvent("onTouchEvent End", ev);
                        return false;
                    }
                }
                if (mIsBeingDragged) {
                    // Scroll to follow the motion event
                    if (!getXY(ev, mActivePointerId)) {
                        break;
                    }
                    final float x = mActionX;
                    final float deltaX = mLastMotionX - x;
                    mLastMotionX = x;
                    float oldScrollX = mScrollX;

                    float scrollX = oldScrollX + deltaX;
                    final float leftBound = getLeftBound();
                    final float rightBound = getRightBound();
                    if (scrollX < leftBound) {
                        scrollX = leftBound;
                    } else if (scrollX > rightBound) {
                        scrollX = rightBound;
                    }
                    // Don't lose the rounded component
                    mLastMotionX += scrollX - (int) scrollX;
                    doScroll((int) scrollX, mSlidingContentView.getScrollY());
                    pageScrolled((int) scrollX);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    final int initialVelocity = (int) velocityTracker
                            .getXVelocity(mActivePointerId);
                    final int scrollX = mScrollX;
                    final float pageOffset = (float) (scrollX - getDestScrollX(mCurItem))
                            / mSlidingContentView.getBehindWidth();
                    if (getXY(ev, mActivePointerId)) {
                        final float x = mActionX;
                        final int totalDelta = (int) (x - mInitialMotionX);
                        final int nextPage = determineTargetPage(pageOffset, initialVelocity,
                                totalDelta);
                        setCurrentItemInternal(nextPage, true, true, initialVelocity);
                    } else {
                        setCurrentItemInternal(mCurItem, true, true, initialVelocity);
                    }
                    mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                    endDrag();
                } else if (mQuickReturn
                        && mSlidingMenuView.menuTouchInQuickReturn(
                                mSlidingContentView.getContent(), mCurItem, ev.getX() + mScrollX)) {
                    // close the menu
                    setCurrentItem(CONTENT_VIEW);
                    endDrag();
                    mQuickReturn = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
                    setCurrentItemInternal(mCurItem, true, true);
                    mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                    endDrag();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int index = ev.getActionIndex();
                mLastMotionX = ev.getX(index);
                mActivePointerId = ev.getPointerId(index);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                if (!getXY(ev, mActivePointerId)) {
                    break;
                }
                mLastMotionX = mActionX;
                break;
        }
        logWithEvent("onTouchEvent End", ev);
        return true;
    }

    private int determineTargetPage(float pageOffset, int velocity, int deltaX) {
        int targetPage = mCurItem;
        if (Math.abs(deltaX) > mFlingDistance && Math.abs(velocity) > mMinimumVelocity) {
            if (velocity > 0 && deltaX > 0) {
                targetPage -= 1;
            } else if (velocity < 0 && deltaX < 0) {
                targetPage += 1;
            }
        } else {
            targetPage = (int) Math.round(mCurItem + pageOffset);
        }
        return targetPage;
    }

    private int getLeftBound() {
        return mSlidingMenuView.getAbsLeftBound(mSlidingContentView.getContent());
    }

    private int getRightBound() {
        return mSlidingMenuView.getAbsRightBound(mSlidingContentView.getContent());
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionX = ev.getX(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    private boolean thisTouchAllowed(MotionEvent ev, boolean force) {
        final int x = (int) (ev.getX() + mScrollX);
        if (isMenuShowing()) {
            return mSlidingMenuView.menuOpenTouchAllowed(mSlidingContentView.getContent(),
                    mCurItem, x);
        } else {
            switch (mSlidingContentView.getTouchMode()) {
                case SlidingMenu.TOUCHMODE_FULLSCREEN:
                    if (!force) {
                        return marginTouchAllowed(mSlidingContentView.getContent(), x);
                    }
                    return !isInIgnoredView(ev);
                case SlidingMenu.TOUCHMODE_NONE:
                    return false;
                case SlidingMenu.TOUCHMODE_MARGIN:
                    return marginTouchAllowed(mSlidingContentView.getContent(), x);
            }
        }
        return false;
    }

    private boolean thisTouchAllowed(MotionEvent ev) {
        return thisTouchAllowed(ev, true);
    }

    private boolean isInIgnoredView(MotionEvent ev) {
        Rect rect = new Rect();
        for (View v : mIgnoredViews) {
            v.getHitRect(rect);
            if (rect.contains((int) ev.getX(), (int) ev.getY())) {
                return true;
            }
        }
        return false;
    }

    private boolean thisSlideAllowed(float dx) {
        boolean allowed = false;
        if (isMenuShowing()) {
            allowed = mSlidingMenuView.menuOpenSlideAllowed(dx);
        } else {
            allowed = mSlidingMenuView.menuClosedSlideAllowed(dx);
        }
        return allowed;
    }

    private void determineDrag(MotionEvent ev) {
        logWithEvent("determineDrag Begin", ev);
        if (!getXY(ev, mActivePointerId)) {
            logWithEvent("determineDrag End", ev);
            return;
        }
        final float x = mActionX;
        final float dx = x - mLastMotionX;
        final float xDiff = Math.abs(dx);
        final float y = mActionY;
        final float dy = y - mLastMotionY;
        final float yDiff = Math.abs(dy);

        log("determineDrag", "xDiff = ", String.valueOf(xDiff), " , mTouchSlop = ",
                String.valueOf(mTouchSlop), " , yDiff = "
                , String.valueOf(yDiff), " , thisSlideAllowed = ",
                String.valueOf(thisSlideAllowed(dx)));
        // Easy to start drag to set touch slop to 0 when menu is opened
        if (xDiff > (isMenuShowing() ? 0 : mTouchSlop) && xDiff > yDiff && thisSlideAllowed(dx)) {
            startDrag();
            mLastMotionX = x;
            mLastMotionY = y;
            mSlidingContentView.setScrollingCacheEnabled(true);
        } else if (xDiff > mTouchSlop) {
            mIsUnableToDrag = true;
        }
        logWithEvent("determineDrag End", ev);
    }

    private int getPointerIndex(MotionEvent ev, int id) {
        final int activePointerIndex = ev.findPointerIndex(id);
        if (activePointerIndex == -1) {
            mActivePointerId = MotionEvent.INVALID_POINTER_ID;
        }
        return activePointerIndex;
    }

    private void startDrag() {
        log("startDrag");
        mIsBeingDragged = true;
        mQuickReturn = false;
    }

    private void endDrag() {
        log("endDrag");
        mQuickReturn = false;
        mIsBeingDragged = false;
        mIsUnableToDrag = false;
        mActivePointerId = MotionEvent.INVALID_POINTER_ID;

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public void computeScroll() {
        log("computeScroll Begin", "isFinished = ", String.valueOf(mScroller.isFinished())
                , " , computeScrollOffset = ", String.valueOf(mScroller.computeScrollOffset()));
        if (!mScroller.isFinished()) {
            if (mScroller.computeScrollOffset()) {
                final int oldX = mScrollX;
                final int oldY = mSlidingContentView.getScrollY();
                final int x = mScroller.getCurrX();
                final int y = mScroller.getCurrY();

                log("computeScroll", "oldX = ", String.valueOf(oldX),
                        " , oldY = ", String.valueOf(oldY),
                        " , x = ", String.valueOf(x),
                        " , y = ", String.valueOf(y));
                if (oldX != x || oldY != y) {
                    doScroll(x, y);
                    pageScrolled(x);
                }

                // Keep on drawing until the animation has finished.
                postInvalidateOnAnimation();
                log("computeScroll End");
                return;
            }
        }

        // Done with scroll, clean up state.
        completeScroll();
        log("computeScroll End");
    }

    public static class SavedState extends BaseSavedState {

        private final int mItem;

        public SavedState(Parcelable superState, int item) {
            super(superState);
            mItem = item;
        }

        private SavedState(Parcel in) {
            super(in);
            mItem = in.readInt();
        }

        public int getItem() {
            return mItem;
        }

        /*
         * (non-Javadoc)
         * @see android.view.AbsSavedState#writeToParcel(android.os.Parcel, int)
         */
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mItem);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

    }

    /*
     * (non-Javadoc)
     * @see android.view.View#onSaveInstanceState()
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState, getCurrentItem());
        return ss;
    }

    /*
     * (non-Javadoc)
     * @see android.view.View#onRestoreInstanceState(android.os.Parcelable)
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setCurrentItem(ss.getItem());
    }

    /*
     * (non-Javadoc)
     * @see android.view.ViewGroup#fitSystemWindows(android.graphics.Rect)
     */
    @Override
    protected boolean fitSystemWindows(Rect insets) {
        if (!mActionbarOverlay) {
            setPadding(insets.left, insets.top, insets.right, insets.bottom);
        }
        return true;
    }

    private Handler mHandler = new Handler();

    public void manageLayers(float percentOpen) {

        boolean layer = percentOpen > 0.0f && percentOpen < 1.0f;
        final int layerType = layer ? View.LAYER_TYPE_HARDWARE : View.LAYER_TYPE_NONE;

        if (layerType != getContent().getLayerType()) {
            mHandler.post(new Runnable() {
                public void run() {
                    getContent().setLayerType(layerType, null);
                    getMenu().setLayerType(layerType, null);
                    if (getSecondaryMenu() != null) {
                        getSecondaryMenu().setLayerType(layerType, null);
                    }
                }
            });
        }
    }

    @ExportedProperty(category = "CommonControl")
    private boolean mSlidingEnabled = true;
    @ExportedProperty(category = "CommonControl")
    private boolean mIsBeingDragged;
    @ExportedProperty(category = "CommonControl")
    private boolean mIsUnableToDrag;
    @ExportedProperty(category = "CommonControl")
    private boolean mQuickReturn = false;
    private int mCurItem = 1;
    private static final int POSITION_OPEN = 0;
    private static final int POSITION_CLOSE = 1;
    @ExportedProperty(category = "CommonControl")
    private boolean mScrolling;
    private float mInitialMotionX;
    private List<View> mIgnoredViews = new ArrayList<View>();
    private static final int MARGIN_THRESHOLD = 48; // dips
    private int mMarginThreshold;
    private int mScrollX;
    private float mMenuScrollScale = 1.0f;
    private float mContentScrollScale = 0.2f;
    private static final int MAX_SETTLE_DURATION = 600; // ms
    private static final int MIN_DISTANCE_FOR_FLING = 25; // dips

    private Scroller mScroller;
    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return (float) Math.pow(t, 5) + 1.0f;
        }
    };
    private int mTouchSlop;
    /**
     * Determines speed during touch scrolling
     */
    protected VelocityTracker mVelocityTracker;
    private int mMinimumVelocity;
    protected int mMaximumVelocity;
    private int mFlingDistance;
    /**
     * Position of the last motion event.
     */
    private float mLastMotionX;
    private float mLastMotionY;
    /**
     * /** ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    int mActivePointerId = MotionEvent.INVALID_POINTER_ID;

    private static final int DEFAULT_SCRIM_COLOR = 0x73000000;
    private int mScrimColor = DEFAULT_SCRIM_COLOR;
    private Paint mScrimPaint = new Paint();

    private void dispatchOpenListener(int item) {
        if (POSITION_OPEN == item && mOpenListener != null) {
            mOpenListener.onOpen();
        } else if (POSITION_CLOSE == item && mCloseListener != null) {
            mCloseListener.onClose();
        }
    }

    private void dispatchOpenedListener() {
        if (isMenuShowing()) {
            if (mOpenedListener != null) {
                mOpenedListener.onOpened();
            }
        } else {
            if (mClosedListener != null) {
                mClosedListener.onClosed();
            }
        }
    }

    private int getCurrentItem() {
        return mCurItem;
    }

    /**
     * Set the currently selected page. If the CustomViewPager has already been
     * through its first layout there will be a smooth animated transition
     * between the current item and the specified item.
     *
     * @param item Item index to select
     */
    private void setCurrentItem(int item) {
        setCurrentItemInternal(item, true, false);
    }

    /**
     * Set the currently selected page.
     *
     * @param item Item index to select
     * @param smoothScroll True to smoothly scroll to the new item, false to
     *            transition immediately
     */
    private void setCurrentItem(int item, boolean smoothScroll) {
        // clear flags after pressing back key
        mIsBeingDragged = false;
        mQuickReturn = false;
        setCurrentItemInternal(item, smoothScroll, false);
    }

    private void setCurrentItemInternal(int item, boolean smoothScroll, boolean always) {
        setCurrentItemInternal(item, smoothScroll, always, 0);
    }

    private void setCurrentItemInternal(int item, boolean smoothScroll, boolean always, int velocity) {
        if (!always && mCurItem == item) {
            mSlidingContentView.setScrollingCacheEnabled(false);
            return;
        }

        item = mSlidingMenuView.getMenuPage(item);

        final boolean dispatchSelected = mCurItem != item;
        mCurItem = item;
        final int destX = getDestScrollX(mCurItem);
        if (dispatchSelected) {
            dispatchOpenListener(item);
        }
        if (smoothScroll) {
            smoothScrollTo(destX, 0, velocity);
        } else {
            completeScroll();
            doScroll(destX, 0);
        }
    }

    /**
     * Like {@link View#scrollBy}, but scroll smoothly instead of immediately.
     *
     * @param x the number of pixels to scroll by on the X axis
     * @param y the number of pixels to scroll by on the Y axis
     * @param velocity the velocity associated with a fling, if applicable. (0
     *            otherwise)
     */
    private void smoothScrollTo(int x, int y, int velocity) {
        log("smoothScrollTo Begin", "x = ", String.valueOf(x), " , y = ", String.valueOf(y));
        if (mSlidingContentView.getChildCount() == 0) {
            // Nothing to do.
            mSlidingContentView.setScrollingCacheEnabled(false);
            log("smoothScrollTo End");
            return;
        }
        final int sx = mScrollX;
        final int sy = mSlidingContentView.getScrollY();
        final int dx = x - sx;
        final int dy = y - sy;
        log("smoothScrollTo", "sx = ", String.valueOf(sx),
                ", sy = ", String.valueOf(sy),
                " ,dx = ", String.valueOf(dx),
                " , dy = ", String.valueOf(dy));
        if (0 == dx && 0 == dy) {
            completeScroll();
            dispatchOpenedListener();
            log("smoothScrollTo End");
            return;
        }

        mSlidingContentView.setScrollingCacheEnabled(true);
        mScrolling = true;

        final int width = mSlidingContentView.getBehindWidth();
        final int halfWidth = width / 2;
        final float distanceRatio = Math.min(1f, 1.0f * Math.abs(dx) / width);
        final float distance = halfWidth + halfWidth
                * distanceInfluenceForSnapDuration(distanceRatio);

        int duration = 0;
        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
        } else {
            final float pageDelta = (float) Math.abs(dx) / width;
            duration = (int) ((pageDelta + 1) * 100);
            duration = MAX_SETTLE_DURATION;
        }
        duration = Math.min(duration, MAX_SETTLE_DURATION);

        log("smoothScrollTo startScroll", "sx = ", String.valueOf(sx),
                ", sy = ", String.valueOf(sy),
                " ,dx = ", String.valueOf(dx),
                " , dy = ", String.valueOf(dy),
                " , duration = ", String.valueOf(duration));
        mScroller.startScroll(sx, sy, dx, dy, duration);
        invalidate();
        log("smoothScrollTo End");
    }

    private OnClosedListener mClosedListener;
    private OnOpenedListener mOpenedListener;

    // We want the duration of the page snap animation to be influenced by the
    // distance that
    // the screen has to travel, however, we don't want this duration to be
    // effected in a
    // purely linear fashion. Instead, we use this method to moderate the effect
    // that the distance
    // of travel has on the overall snap duration.
    private float distanceInfluenceForSnapDuration(float duration) {
        duration -= 0.5f; // center the values about 0.
        duration *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(duration);
    }

    private void completeScroll() {
        log("completeScroll Begin");
        boolean needPopulate = mScrolling;
        if (needPopulate) {
            // Done with scroll, no longer want to cache view drawing.
            mSlidingContentView.setScrollingCacheEnabled(false);
            mScroller.abortAnimation();
            final int oldX = mScrollX;
            final int oldY = mSlidingContentView.getScrollY();
            final int x = mScroller.getCurrX();
            final int y = mScroller.getCurrY();
            log("completeScroll", "oldX = ", String.valueOf(oldX),
                    " , oldY = ", String.valueOf(oldY),
                    " , x = ", String.valueOf(x),
                    " , y = ", String.valueOf(y));
            if (oldX != x || oldY != y) {
                doScroll(x, y);
            }
            dispatchOpenedListener();
        }
        mScrolling = false;
        log("completeScroll End");
    }

    @Override
    /**
     * @Hide
     * @deprecated
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode && isMenuShowing()) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    /**
     * @Hide
     * @deprecated
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode && isMenuShowing()) {
            showContent();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    /**
     * @Hide
     * @deprecated
     */
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Let the focused view and/or our descendants get the key first
        return super.dispatchKeyEvent(event) || executeKeyEvent(event);
    }

    /**
     * You can call this function yourself to have the scroll view perform
     * scrolling from a key event, just as if the event had been dispatched to
     * it by the view hierarchy.
     *
     * @param event The key event to execute.
     * @return Return true if the event was handled, else false.
     */
    private boolean executeKeyEvent(KeyEvent event) {
        boolean handled = false;
        if (KeyEvent.ACTION_DOWN == event.getAction()) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    handled = arrowScroll(FOCUS_LEFT);
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    handled = arrowScroll(FOCUS_RIGHT);
                    break;
                case KeyEvent.KEYCODE_TAB:
                    // The focus finder had a bug handling FOCUS_FORWARD and
                    // FOCUS_BACKWARD
                    // before Android 3.0. Ignore the tab key on those devices.
                    if (event.hasNoModifiers()) {
                        handled = arrowScroll(FOCUS_FORWARD);
                    } else if (event.hasModifiers(KeyEvent.META_SHIFT_ON)) {
                        handled = arrowScroll(FOCUS_BACKWARD);
                    }
                    break;
            }
        }
        return handled;
    }

    private boolean arrowScroll(int direction) {
        View currentFocused = findFocus();
        if (this == currentFocused) {
            currentFocused = null;
        }

        boolean handled = false;

        View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused,
                direction);
        if (nextFocused != null && nextFocused != currentFocused) {
            if (View.FOCUS_LEFT == direction) {
                handled = nextFocused.requestFocus();
            } else if (direction == View.FOCUS_RIGHT) {
                // If there is nothing to the right, or this is causing us to
                // jump to the left, then what we really want to do is page
                // right.
                if (currentFocused != null && nextFocused.getLeft() <= currentFocused.getLeft()) {
                    handled = pageRight();
                } else {
                    handled = nextFocused.requestFocus();
                }
            }
        } else if (FOCUS_LEFT == direction || FOCUS_BACKWARD == direction) {
            // Trying to move left and nothing there; try to page.
            handled = pageLeft();
        } else if (FOCUS_RIGHT == direction || FOCUS_FORWARD == direction) {
            // Trying to move right and nothing there; try to page.
            handled = pageRight();
        }
        if (handled) {
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
        }
        return handled;
    }

    private boolean pageLeft() {
        if (mCurItem > 0) {
            setCurrentItem(mCurItem - 1, true);
            return true;
        }
        return false;
    }

    private boolean pageRight() {
        if (mCurItem < 1) {
            setCurrentItem(mCurItem + 1, true);
            return true;
        }
        return false;
    }

    private int getDestScrollX(int page) {
        switch (page) {
            case FIRST_MENU:
            case SECOND_MENU:
                return mSlidingMenuView.getMenuLeft(mSlidingContentView.getContent(), page);
            case CONTENT_VIEW:
                return mSlidingContentView.getContent().getLeft();
            default:
                return 0;
        }
    }

    private void pageScrolled(int xpos) {
        final int widthWithMargin = mSlidingContentView.getWidth();
        final int position = xpos / widthWithMargin;
        final int offsetPixels = xpos % widthWithMargin;
        final float offset = (float) offsetPixels / widthWithMargin;

        onPageScrolled(position, offset, offsetPixels);
    }

    /**
     * This method will be invoked when the current page is scrolled, either as
     * part of a programmatically initiated smooth scroll or a user initiated
     * touch scroll. If you override this method you must call through to the
     * superclass implementation (e.g. super.onPageScrolled(position, offset,
     * offsetPixels)) before onPageScrolled returns.
     *
     * @param position Position index of the first page currently being
     *            displayed. Page position+1 will be visible if positionOffset
     *            is nonzero.
     * @param offset Value from [0, 1) indicating the offset from the page at
     *            position.
     * @param offsetPixels Value in pixels indicating the offset from position.
     */
    private void onPageScrolled(int position, float offset, int offsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position, offset, offsetPixels);
        }
    }

    private OnPageChangeListener mOnPageChangeListener;

    /**
     * Callback interface for responding to changing state of the selected page.
     */
    private interface OnPageChangeListener {

        /**
         * This method will be invoked when the current page is scrolled, either
         * as part of a programmatically initiated smooth scroll or a user
         * initiated touch scroll.
         *
         * @param position Position index of the first page currently being
         *            displayed. Page position+1 will be visible if
         *            positionOffset is nonzero.
         * @param positionOffset Value from [0, 1) indicating the offset from
         *            the page at position.
         * @param positionOffsetPixels Value in pixels indicating the offset
         *            from position.
         */
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        /**
         * This method will be invoked when a new page becomes selected.
         * Animation is not necessarily complete.
         *
         * @param position Position index of the new selected page.
         */
        public void onPageSelected(int position);

    }

    /**
     * Simple implementation of the {@link OnPageChangeListener} interface with
     * stub implementations of each method. Extend this if you do not intend to
     * override every method of {@link OnPageChangeListener}.
     */
    private static class SimpleOnPageChangeListener implements OnPageChangeListener {

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // This space for rent
        }

        public void onPageSelected(int position) {
            // This space for rent
        }

        public void onPageScrollStateChanged(int state) {
            // This space for rent
        }

    }

    /**
     * Set a listener that will be invoked whenever the page changes or is
     * incrementally scrolled. See {@link OnPageChangeListener}.
     *
     * @param listener Listener to set
     */
    private void setOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    /** @hide **/
    float getPercentOpen() {
        final float p = (Math.abs((float) mScrollX - mSlidingContentView.getContent().getLeft()) / mSlidingMenuView
                .getBehindWidth());
        return p;
    }

    private void doScroll(int x, int y) {
        log("doScroll Begin", "x = " + x);
        mScrollX = x;
        mSlidingContentView.scrollTo((int) (x * mContentScrollScale), y);
        doMenuScroll(x, y);
        manageLayers(getPercentOpen());
        log("doScroll End");
    }

    private void doMenuScroll(int x, int y) {
        if (mSlidingEnabled) {
            scrollMenuTo(mSlidingContentView.getContent(), x, y);
        }
    }

    private void scrollMenuTo(View content, int x, int y) {
        log("scrollMenuTo Begin", "x = " + x);
        int vis = View.VISIBLE;
        final int left = content.getLeft();
        final int behindWidth = mSlidingMenuView.getBehindWidth();
        final int width = mSlidingMenuView.getWidth();
        final int mode = mSlidingMenuView.getMode();
        if (SlidingMenu.LEFT == mode) {
            if (x >= left) {
                vis = View.INVISIBLE;
            }
            log("scrollMenuTo LEFT", "x = " + (int) ((x + behindWidth) * mMenuScrollScale));
            mSlidingMenuView.scrollTo(
                    (int) ((x + behindWidth) * mMenuScrollScale), y);
        } else if (SlidingMenu.RIGHT == mode) {
            if (x <= left) {
                vis = View.INVISIBLE;
            }
            log("scrollMenuTo RIGHT", "x = "
                    + (int) (behindWidth - width + (x - behindWidth)
                            * mMenuScrollScale));
            mSlidingMenuView
                    .scrollTo(
                            (int) (behindWidth - width + (x - behindWidth)
                                    * mMenuScrollScale), y);
        } else if (SlidingMenu.LEFT_RIGHT == mode) {
            mSlidingMenuView.getContent().setVisibility(
                    x >= left ? View.INVISIBLE : View.VISIBLE);
            mSlidingMenuView.getSecondaryContent().setVisibility(
                    x <= left ? View.INVISIBLE : View.VISIBLE);
            vis = x == 0 ? View.INVISIBLE : View.VISIBLE;
            if (x <= left) {
                log("scrollMenuTo LEFT_RIGHT", "x <= left x = "
                        + (int) ((x + behindWidth) * mMenuScrollScale));
                mSlidingMenuView.scrollTo(
                        (int) ((x + behindWidth) * mMenuScrollScale), y);
            } else {
                log("scrollMenuTo LEFT_RIGHT", "x > left x = "
                        + (int) (behindWidth - width + (x - behindWidth)
                                * mMenuScrollScale));
                mSlidingMenuView
                        .scrollTo(
                                (int) (behindWidth - width + (x - behindWidth)
                                        * mMenuScrollScale), y);
            }
        }
        mSlidingMenuView.setVisibility(vis);
        log("scrollMenuTo End");
    }

    private boolean marginTouchAllowed(View content, int x) {
        final int left = content.getLeft();
        final int right = content.getRight();

        final int mode = getMode();
        if (SlidingMenu.LEFT == mode) {
            return (x >= left && x <= mMarginThreshold + left);
        } else if (SlidingMenu.RIGHT == mode) {
            return (x <= right && x >= right - mMarginThreshold);
        } else if (SlidingMenu.LEFT_RIGHT == mode) {
            return (x >= left && x <= mMarginThreshold + left) ||
                    (x <= right && x >= right - mMarginThreshold);
        } else {
            return false;
        }
    }

    private static void log(String title, String... detail) {
        if (HtcBuildFlag.Htc_DEBUG_flag) {
            if (null == detail) {
                Log.d(TAG, title);
            } else {
                final StringBuffer sb = new StringBuffer();
                for (String str : detail) {
                    sb.append(str);
                }
                Log.d(TAG, title + " : " + sb.toString());
            }
        }
    }

    private static void log(String title, int action, boolean slidingEnabled,
            boolean isUnableToDrag, boolean quickReturn, boolean isBeingDragged,
            boolean isMenuShowing, boolean thisTouchAllowed, int activePointerId,
            float mLastMotionX,
            float mLastMotionY, float mInitialMotionX, float evGetX, float evGetY, int scrollX,
            int leftBound, int rightBound) {
        log(title, " , mSlidingEnabled = ", String.valueOf(slidingEnabled)
                , " , mQuickReturn = ", String.valueOf(quickReturn)
                , " , mIsBeingDragged = ", String.valueOf(isBeingDragged)
                , " , mIsUnableToDrag = ", String.valueOf(isUnableToDrag)
                , " , isMenuShowing = ", String.valueOf(isMenuShowing)
                , " , thisTouchAllowed = ", String.valueOf(thisTouchAllowed)
                , " , mActivePointerId = ", String.valueOf(activePointerId)
                , " , mLastMotionX = ", String.valueOf(mLastMotionX)
                , " , mLastMotionY = ", String.valueOf(mLastMotionY)
                , " , mInitialMotionX = ", String.valueOf(mInitialMotionX)
                , " , evGetX = ", String.valueOf(evGetX)
                , " , evGetY = ", String.valueOf(evGetY)
                , " , scrollX = ", String.valueOf(scrollX)
                , " , leftBound = ", String.valueOf(leftBound)
                , " , rightBound = ", String.valueOf(rightBound));
    }

    private void logWithEvent(String title, MotionEvent ev) {
        log(title, ev.getAction() & MotionEvent.ACTION_MASK,
                mSlidingEnabled,
                mIsUnableToDrag, mQuickReturn, mIsBeingDragged, isMenuShowing(),
                thisTouchAllowed(ev), mActivePointerId, mLastMotionX,
                mLastMotionY, mInitialMotionX, ev.getX(ev.getActionIndex()),
                ev.getY(ev.getActionIndex()), mScrollX, getLeftBound(), getRightBound());
    }

    private boolean getXY(MotionEvent ev, int id) {
        final int activePointerIndex = getPointerIndex(ev, id);
        final boolean result = !isActionIdAndIndexIncorrect(id, activePointerIndex);
        if (result) {
            try {
                mActionX = ev.getX(activePointerIndex);
                mActionY = ev.getY(activePointerIndex);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                log("Pointer index out of range: " + activePointerIndex);
                mActionX = ev.getX();
                mActionY = ev.getY();
            }
        }
        return result;
    }

    private boolean isActionIdAndIndexIncorrect(int id, int index) {
        final boolean result = MotionEvent.INVALID_POINTER_ID == id
                || INVALID_POINTER_INDEX == index;
        if (result) {
            log("isActionIdAndIndexIncorrect", "id = ", String.valueOf(id), " , index = ",
                    String.valueOf(index));
        }
        return result;
    }
}
