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

package com.htc.lib1.cc.widget.quicktips;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.IBinder;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.HtcCommonUtil;

import java.lang.ref.WeakReference;

import static com.htc.lib1.cc.util.WindowUtil.getScreenHeightPx;
import static com.htc.lib1.cc.util.WindowUtil.getScreenWidthPx;

/**
 * <p>
 * A popup window that can be used to display an arbitrary view. The popup
 * windows is a floating container that appears on top of the current activity.
 * </p>
 * <pre>
 * [Minimum width]
 *
 * Portrait
 * +--------------------------------------------------------+
 * |                /\  ↑ M2 ↑                              |
 * |      +----------------------+                          |
 * | ←M2→ |     Popup Window     | ←         30 %         → |
 * |      +----------------------+                          |
 * |              ↓ M2 ↓                                    |
 * +--------------------------------------------------------+
 *
 *
 * Landscape
 * +-----------------------------------------------------------------------------+
 * |                                                  ↑ M2 ↑               |     |
 * |                                         +----------------------+ ←M2→ |  F  |
 * |←                                      → |                      |      |  O  |
 * |←               40 %                   → |     Popup Window     |\     |  O  |
 * |←                                      → |                      |/     |  T  |
 * |←                                      → |                      |      |  E  |
 * |                                         +----------------------+ ←M2→ |  R  |
 * |                                                  ↓ M2 ↓               |     |
 * +-----------------------------------------------------------------------------+
 *
 *
 * [Maximum width]
 *
 * Portrait
 * +--------------------------------------------------------+
 * |                /\    ↑ M2 ↑                            |
 * |      +------------------------------------------+      |
 * | ←M2→ |     Popup Window                         | ←M2→ |
 * |      +------------------------------------------+      |
 * |              ↓ M2 ↓                                    |
 * +--------------------------------------------------------+
 *
 * mBubbleVerticalOffset = M2
 * mBubbleHerizontalOffset = M2
 *
 * Landscape
 * +-----------------------------------------------------------------------------+
 * |                                                  ↑ M2 ↑               |     |
 *        +---------------------------------------------------------+ ←M2→ |  F  |
 * |      |                                                         |      |  O  |
 * |      |     Popup Window                                        |\     |  O  |
 * | ←M2→ |                                                         |/     |  T  |
 * |      |                                                         |      |  E  |
 * |      +---------------------------------------------------------+ ←M2→ |  R  |
 * |                                                  ↓ M2 ↓               |     |
 * +-----------------------------------------------------------------------------+
 *
 * mBubbleLandVerticalOffset = M2
 * mBubbleLandHerizontalOffset = M2
 *
 *  define in Sense 6.0 UIGL v1.1 p62,64.
 * </pre>
 * @see android.widget.AutoCompleteTextView
 * @see android.widget.Spinner
 */
public class PopupBubbleWindow {
    private static final String TAG = "PopupBubbleWindow";

    /**
     * Mode for {@link #setInputMethodMode(int)}: the requirements for the input
     * method should be based on the focusability of the popup. That is if it is
     * focusable than it needs to work with the input method, else it doesn't.
     * @hide
     */
    public static final int INPUT_METHOD_FROM_FOCUSABLE = 0;

    /**
     * Mode for {@link #setInputMethodMode(int)}: this popup always needs to
     * work with an input method, regardless of whether it is focusable. This
     * means that it will always be displayed so that the user can also operate
     * the input method while it is shown.
     * @hide
     */
    public static final int INPUT_METHOD_NEEDED = 1;

    /**
     * Mode for {@link #setInputMethodMode(int)}: this popup never needs to work
     * with an input method, regardless of whether it is focusable. This means
     * that it will always be displayed to use as much space on the screen as
     * needed, regardless of whether this covers the input method.
     * @hide
     */
    public static final int INPUT_METHOD_NOT_NEEDED = 2;

    /**
     * Direction for {@link #setExpandDirection(int)}: if it is not indicate the
     * popup window expand direction, it will detect the view's up or down
     * boundary. And expand to more space direction.
     */
    public static final int EXPAND_DEFAULT = 0;

    /**
     * Direction for {@link #setExpandDirection(int)}: the popup window will
     * expand to view's up direction.
     */
    public static final int EXPAND_UP = 1;

    /**
     * Direction for {@link #setExpandDirection(int)}: the popup window will
     * expand to view's down direction.
     */
    public static final int EXPAND_DOWN = 2;

    /**
     * Direction for {@link #setExpandDirection(int)}: the popup window will
     * expand to view's left direction.
     */
    public static final int EXPAND_LEFT = 3;

    /**
     * Direction for {@link #setExpandDirection(int)}: the popup window will
     * expand to view's right direction.
     */
    public static final int EXPAND_RIGHT = 4;

    /**
     * Direction for {@link #setExpandDirection(int)}: the popup window won't
     * have anchor view, so there is no arrow. The popup window will show in the center.
     */
    public static final int EXPAND_NO_ANCHOR = 5;

    /*@hide*/
    protected int mExpandDirection;
    private boolean mExpandDirectionUndefined = false;

    private Context mContext;
    private WindowManager mWindowManager;

    private boolean mIsShowing;
    private boolean mIsDropdown;

    private View mContentView;
    private View mPopupView;
    private boolean mFocusable;
    private int mInputMethodMode = INPUT_METHOD_FROM_FOCUSABLE;
    private int mSoftInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED;
    private boolean mTouchable = true;
    private boolean mOutsideTouchable = false;
    private boolean mClippingEnabled = true;
    private int mSplitTouchEnabled = -1;
    private boolean mLayoutInScreen;
    private boolean mClipToScreen;
    private boolean mAllowScrollingAnchorParent = true;

    private OnTouchListener mTouchInterceptor;

    private int mWidthMode;
    private int mWidth;
    private int mLastWidth;
    private int mHeightMode;
    private int mHeight;
    private int mLastHeight;

    private int mPopupWidth;
    private int mPopupHeight;

    private int mTriangleEdgeLimit;

    private int mGap = 0;// between drawing location and screen location
    private int[] mDrawingLocation = new int[2];
    private int[] mScreenLocation = new int[2];
    private Rect mTempRect = new Rect();

    /**@hide*/
    protected Drawable mBackground;
    private Drawable mTriangle;
    private Drawable mBelowTriangledDrawable;
    private Drawable mExpandLeftTriangledDrawable;
    private Drawable mExpandRightTriangledDrawable;
    private int mTriangledOffset;

    private int mCustomizeTriangleOffset;

    private boolean mAboveAnchor;
    private int mWindowLayoutType = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;

    private android.widget.PopupWindow.OnDismissListener mOnDismissListener;
    /**@hide*/
    protected android.widget.PopupWindow.OnDismissListener mOnUserDismissListener;
    private boolean mIgnoreCheekPress = false;

    private int mAnimationStyle = -1;

    private static final int ROTATE_180_LEVEL = 10000 / 2;

    // for width limit
    Rect mPopupPadding = new Rect();
    private int mMaxWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int mMinWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int mMaxContentWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int mMinContentWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int mMinFooterContentWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int mHtcFooterBarLandWidth;

    /**@hide*/
    protected int mPopupShadowTop;
    /**@hide*/
    protected int mPopupShadowBottom;
    /**@hide*/
    protected int mPopupShadowRight;
    /**@hide*/
    protected int mPopupShadowLeft;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mCustomizedContentWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    private boolean mUsePortraitLimitOnly = true;

    /**@hide*/
    protected int mXoff = 0;
    /**@hide*/
    protected int mYoff = 0;

    /**@hide*/
    protected static int M1;
    /**@hide*/
    protected static int M2;
    /**@hide*/
    protected static int M3;
    /**@hide*/
    protected static int M4;
    /**@hide*/
    protected static int M5;
    /**@hide*/
    protected int mClearIconWidth;
    /**@hide*/
    protected boolean mIsCloseVisible = true;
    /**@hide*/
    protected static int mIncreasedTouchSize = 0;
    /**@hide*/
    protected Drawable mImageSrc;

    /*@hide*/
    protected void initLimit() {
        Resources res = mContext.getResources();

        if( getScreenWidthPx(res) != mScreenWidth ){
            mScreenWidth = getScreenWidthPx(res);
            mScreenHeight = getScreenHeightPx(res);
            //if (false) { // if tablet
            //    mMaxWidth = (int) res.getDimension(R.dimen.bubble_max_width);
            //    mMinWidth = (int) res.getDimension(R.dimen.bubble_min_width);
            //} else {

                mMaxContentWidth = mMaxWidth = mScreenWidth  - 2 * M2;

                boolean isPortrait = mScreenWidth < mScreenHeight;

                if(mUsePortraitLimitOnly){// UIGL: only footer bar popup has landscape width limitation.
                    mMinContentWidth = mMinWidth = (int) (Math.min(getScreenWidthPx(res), getScreenHeightPx(res)) * 0.7 -M2);
                    mMinFooterContentWidth = (int) (Math.max(getScreenWidthPx(res), getScreenHeightPx(res)) * 0.6 - M2 - mHtcFooterBarLandWidth);
                }else if(isPortrait){
                    mMinContentWidth = mMinWidth = (int) (mScreenWidth * 0.7 -M2);
                    mMinFooterContentWidth = (int) (mScreenHeight * 0.6 - M2 - mHtcFooterBarLandWidth);
                }else{
                    mMinContentWidth = mMinWidth = (int) (mScreenWidth * 0.6 - M2 - mHtcFooterBarLandWidth);
                    mMinFooterContentWidth = mMinContentWidth;
                }
            //}

                if (getBackground() != null) {
                    getBackground().getPadding(mPopupPadding);
                    mMinWidth = mMinContentWidth + mPopupPadding.left + mPopupPadding.right;
                    mMaxWidth = mMaxContentWidth + mPopupPadding.left + mPopupPadding.right;
                }
        }

    }

    /**@hide*/
    protected int getMaxContentWidth(){
        initLimit();
        return mMaxContentWidth;
    }

    /**@hide*/
    protected int getMinFooterContentWidth(){
        initLimit();
        return mMinFooterContentWidth;
    }

    /**@hide*/
    protected void setCustomizedContentWidth(int width){
        mCustomizedContentWidth = width;
    }

    /*@hide*/
    protected int checkWidthLimit(int width) {
        initLimit();

        if(mCustomizedContentWidth>0 && mBackground != null){
            mBackground.getPadding(mTempRect);
            width = mTempRect.left + mTempRect.right + mCustomizedContentWidth;
        }

        width = Math.max(width, mMinWidth);
        width = Math.min(width, mMaxWidth);
        return width;
    }

    /*@hide*/
    protected int checkContentWidthLimit(int width) {
        initLimit();

        if(mCustomizedContentWidth>0){
            width = Math.max(mCustomizedContentWidth, mMinContentWidth);
            width = Math.min(width, mMaxContentWidth);
        }else{
            width = Math.max(width, mMinContentWidth);
            width = Math.min(width, mMaxContentWidth);
        }
        return width;
    }
    //end: width limit

    private WeakReference<View> mAnchor;
    private OnScrollChangedListener mOnScrollChangedListener = new OnScrollChangedListener() {
        public void onScrollChanged() {
            /*View anchor = mAnchor != null ? mAnchor.get() : null;
            if (anchor != null && mPopupView != null && dropdownLayout != null
                    && triangleLayout != null) {
                findDropDownPosition(anchor, dropdownLayout, mAnchorXoff, mAnchorYoff);
                findTrianglePosition(anchor, triangleLayout, mAnchorXoff, mAnchorYoff);
                update(dropdownLayout.x, dropdownLayout.y, -1, -1);
            }*/
        }
    };

    /*@hide*/
    protected WeakReference<View> mParent;
    private OnGlobalLayoutListener mOnGlobalLayoutListener = new OnGlobalLayoutListener() {
        public void onGlobalLayout() {
            View anchor = mParent != null ? mParent.get() : null;
            if (PopupBubbleWindow.this.isShowing()) {
                if (anchor == null || (null != anchor && !anchor.isShown())) {
                    PopupBubbleWindow.this.dismiss();
                } else {
                    int pre_x = mDrawingLocation[0];
                    int pre_y = mDrawingLocation[1];
                    anchor.getLocationInWindow(mDrawingLocation);
                    if(mDrawingLocation[0] == pre_x && mDrawingLocation[1] == pre_y){
                        return;
                    }
                    //PopupBubbleWindow.this.dismiss();
                    if(mExpandDirection == PopupBubbleWindow.EXPAND_NO_ANCHOR){
                        PopupBubbleWindow.this.dismiss();
                        PopupBubbleWindow.this.showAtLocation(anchor, Gravity.CENTER, mXoff, mYoff);
                    }
                    else
                        //PopupBubbleWindow.this.showAsDropDown(anchor);
                        PopupBubbleWindow.this.update(anchor, true, mXoff, mYoff, true, PopupBubbleWindow.this.getWidth(), PopupBubbleWindow.this.getHeight());
                }
            }
        }
    };
    private int mAnchorXoff, mAnchorYoff;

    /**@hide*/
    protected int mBubbleHeadOffset;
    /**@hide*/
    protected int mBubbleBodyOffset;
    /**@hide*/
    protected int mBubbleLandHeadOffset;
    /**@hide*/
    protected int mBubbleLandBodyOffset;
    /**@hide*/
    protected int mMultiplyColor;

    /**
     * <p>
     * Create a new, empty, non focusable popup window of dimension (0,0).
     * </p>
     *
     * <p>
     * The popup does provide a background.
     * </p>
     *
     * @param context The Context the PopupBubbleWindow is running in, through which it can
     *                access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     **/
    public PopupBubbleWindow(Context context) {
        this(context, null);
    }

    /**
     * <p>
     * Create a new, empty, non focusable popup window of dimension (0,0).
     * </p>
     *
     * <p>
     * The popup does provide a background.
     * </p>
     *
     * @param context The Context the PopupBubbleWindow is running in, through which it can
     *                access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the PopupBubbleWindow.
     * @deprecated [Module internal use]
     **/
    /**@hide*/
    public PopupBubbleWindow(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.popupBubbleWindowStyle);
    }

    /**
     * <p>
     * Create a new, empty, non focusable popup window of dimension (0,0).
     * </p>
     *
     * <p>
     * The popup does provide a background.
     * </p>
     *
     * @param context The Context the PopupBubbleWindow is running in, through which it can
     *                access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the PopupBubbleWindow.
     * @param defStyle The default style to apply to this PopupBubbleWindow. If 0, no style

     *                     will be applied (beyond what is included in the theme). This may
     *                     either be an attribute resource, whose value will be retrieved
     *                     from the current theme, or an explicit style resource.
     * @deprecated [Module internal use]
     **/
    /**@hide*/
    public PopupBubbleWindow(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    /**
     * <p>
     * Create a new, empty, non focusable popup window of dimension (0,0).
     * </p>
     *
     * <p>
     * The popup does not provide a background.
     * </p>
     *
     * @param context The Context the PopupBubbleWindow is running in, through which it can
     *                access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the PopupBubbleWindow.
     * @param defStyleAttr The default style to apply to this PopupBubbleWindow. If 0, no style
     *                     will be applied (beyond what is included in the theme). This may
     *                     either be an attribute resource, whose value will be retrieved
     *                     from the current theme, or an explicit style resource.
     * @param defStyleRes The default resource identifier of a style resource to this PopupBubbleWindowthat
     *                    supplies default values for the StyledAttributes,
     *                    used only if defStyleAttr is 0 or can not be found
     *                    in the theme.  Can be 0 to not look for defaults.
     * @deprecated [Module internal use]
     **/
    /**@hide*/
    public PopupBubbleWindow(Context context, AttributeSet attrs,
            int defStyleAttr, int defStyleRes) {
        mContext = context;
        Resources res = context.getResources();
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        // init color
        final int [] feedAttr = {
                R.attr.multiply_color
            };
        TypedArray a = context.obtainStyledAttributes(feedAttr);
        mMultiplyColor = a.getColor(0, mContext.getResources().getColor(R.color.multiply_color));
        a.recycle();

        a = context.obtainStyledAttributes(attrs,R.styleable.PopupBubbleWindow, defStyleAttr,defStyleRes);
        final int animStyle = a.getResourceId(R.styleable.PopupBubbleWindow_popupBubbleAnimationStyle, -1);
        mAnimationStyle = animStyle == R.style.HtcAnimation_PopupBubbleWindow ? -1: animStyle;
        a.recycle();

        mBackground = res.getDrawable(R.drawable.tips_panel_shadow);
        initTriangle(res);

        // init margin
        M1 = (int) context.getResources().getDimension(R.dimen.margin_l);
        M2 = (int) context.getResources().getDimension(R.dimen.margin_m);
        M3 = (int) context.getResources().getDimension(R.dimen.margin_s);
        M4 = (int) context.getResources().getDimension(R.dimen.margin_xs);
        M5 = (int) context.getResources().getDimension(R.dimen.spacing);
        mIncreasedTouchSize = 3*M2;

        mTriangledOffset = 0;//(int) context.getResources().getDimension(R.dimen.triangle_offset);
        mBubbleHeadOffset = M2;//(int) res.getDimension(R.dimen.bubble_vertical_offset);
        mBubbleBodyOffset = 3 * M4;//(int) res.getDimension(R.dimen.bubble_herizontal_offset);
        mBubbleLandHeadOffset = M2;//(int) res.getDimension(R.dimen.bubble_land_vertical_offset);
        mBubbleLandBodyOffset = 3 * M4;//(int) res.getDimension(R.dimen.bubble_land_herizontal_offset);
        mHtcFooterBarLandWidth = res.getDimensionPixelSize(R.dimen.htc_footer_width);
        mTriangleEdgeLimit = (int) res.getDimension(R.dimen.triangle_edge_limit);
//        mBubbleHerizontalOffset -= mPopupShadowSize;
//        mBubbleVerticalOffset -= mPopupShadowSize;
//        mBubbleLandVerticalOffset -= mPopupShadowSize;
//        mBubbleLandHerizontalOffset -= mPopupShadowSize;
        Rect rect = new Rect();

        if (mBackground != null) {
            mBackground.getPadding(rect);
            mPopupShadowLeft=  rect.left;
            mPopupShadowTop =  rect.top;
            mPopupShadowRight =  rect.right;
            mPopupShadowBottom =  rect.bottom;
        }else{
            mPopupShadowTop = mPopupShadowBottom = mPopupShadowLeft = mPopupShadowBottom = 0;
        }

        initLimit();
        //setFocusable(true);// for leak window problem

    }

    /**@hide*/
    protected void initTriangle(Resources res) {
        mTriangle = res.getDrawable(R.drawable.common_popupmenu_arrow);
        mTriangle = applyColorMultiply(mTriangle, mMultiplyColor);
        BitmapDrawable arrow=(BitmapDrawable)mTriangle;
        arrow.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
        BitmapDrawable arrow_shadow = (BitmapDrawable) res.getDrawable(R.drawable.tips_arrow_shadow);
        arrow_shadow.setGravity(Gravity.CENTER);
        Drawable[] layer = {
                arrow_shadow,
                arrow
            };
        LayerDrawable triangle_layer = new LayerDrawable(layer);
        mTriangle = triangle_layer;
        Bitmap bitmap = drawableToBitmap(triangle_layer) ;
        mBelowTriangledDrawable = rotateBitmap(bitmap,180);
        mExpandRightTriangledDrawable = rotateBitmap(bitmap,-90);
        mExpandLeftTriangledDrawable = rotateBitmap(bitmap,90);
        bitmap.recycle();
    }

    private Drawable rotateBitmap(Bitmap bitmap, final float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap rotated =  Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        Drawable d = new BitmapDrawable(mContext.getResources(),rotated);
        return d;
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap.Config c = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap( drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),  c);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**@hide*/
    protected static Drawable applyColorMultiply(Drawable dr , int color){
        dr.mutate().setColorFilter(new PorterDuffColorFilter(color,  PorterDuff.Mode.SRC_ATOP));
        return dr;
    }

    /**
     * <p>
     * Create a new empty, non focusable popup window of dimension (0,0).
     * </p>
     *
     * <p>
     * The popup does not provide any background. This should be handled by the
     * content view.
     * </p>
     * @deprecated [Module internal use]
     */
    /**@hide*/
    public PopupBubbleWindow() {
        this(null, 0, 0);
    }

    /**
     * <p>
     * Create a new non focusable popup window which can display the
     * <tt>contentView</tt>. The dimension of the window are (0,0).
     * </p>
     *
     * <p>
     * The popup does not provide any background. This should be handled by the
     * content view.
     * </p>
     *
     * @param contentView
     *            the popup's content
     * @deprecated [Module internal use]
     */
    /**@hide*/
    public PopupBubbleWindow(View contentView) {
        this(contentView, 0, 0);
    }

    /**
     * <p>
     * Create a new empty, non focusable popup window. The dimension of the
     * window must be passed to this constructor.
     * </p>
     *
     * <p>
     * The popup does not provide any background. This should be handled by the
     * content view.
     * </p>
     *
     * @param width
     *            the popup's width
     * @param height
     *            the popup's height
     * @deprecated [Module internal use]
     */
    /**@hide*/
    public PopupBubbleWindow(int width, int height) {
        this(null, width, height);
    }

    /**
     * <p>
     * Create a new non focusable popup window which can display the
     * <tt>contentView</tt>. The dimension of the window must be passed to this
     * constructor.
     * </p>
     *
     * <p>
     * The popup does not provide any background. This should be handled by the
     * content view.
     * </p>
     *
     * @param contentView
     *            the popup's content
     * @param width
     *            the popup's width
     * @param height
     *            the popup's height
     * @deprecated [Module internal use]
     */
    /**@hide*/
    public PopupBubbleWindow(View contentView, int width, int height) {
        this(contentView, width, height, false);//true, set window focusable to avoid leak window
    }

    /**
     * <p>
     * Create a new popup window which can display the <tt>contentView</tt>. The
     * dimension of the window must be passed to this constructor.
     * </p>
     *
     * <p>
     * The popup does not provide any background. This should be handled by the
     * content view.
     * </p>
     *
     * @param contentView
     *            the popup's content
     * @param width
     *            the popup's width
     * @param height
     *            the popup's height
     * @param focusable
     *            true if the popup can be focused, false otherwise
     * @deprecated [Module internal use]
     */
    /**@hide*/
    public PopupBubbleWindow(View contentView, int width, int height,
            boolean focusable) {
        if (contentView != null) {
            mContext = contentView.getContext();
            if (mContext != null)
                mWindowManager = (WindowManager) mContext
                    .getSystemService(Context.WINDOW_SERVICE);
        }
        setContentView(contentView);
        setWidth(width);
        setHeight(height);
        setFocusable(focusable);

        // init margin
        M1 = (int) mContext.getResources().getDimension(R.dimen.margin_l);
        M2 = (int) mContext.getResources().getDimension(R.dimen.margin_m);
        M3 = (int) mContext.getResources().getDimension(R.dimen.margin_s);
        M4 = (int) mContext.getResources().getDimension(R.dimen.margin_xs);
        M5 = (int) mContext.getResources().getDimension(R.dimen.spacing);
        mIncreasedTouchSize = 3*M2;
        mHtcFooterBarLandWidth = mContext.getResources().getDimensionPixelSize(R.dimen.htc_footer_width);
        Rect rect = new Rect();

        if (mBackground != null) {
            mBackground.getPadding(rect);
            mPopupShadowTop =  rect.top;
            mPopupShadowBottom =  rect.bottom;
            mPopupShadowLeft =  rect.left;
            mPopupShadowBottom =  rect.bottom;
        }else{
            mPopupShadowTop = mPopupShadowBottom = mPopupShadowLeft = mPopupShadowBottom = 0;
        }
        initLimit();

    }

    /**@hide*/
    protected float getStatusBarHeight(View anchor){
        Rect frame = new Rect();
        anchor.getWindowVisibleDisplayFrame(frame);
        int status_bar_height = frame== null ? 0 : frame.top;
        return status_bar_height;
    }

    /**
     * <p>
     * Return the drawable used as the popup window's background.
     * </p>
     *
     * @return the background drawable or null
     * @hide
     */
    public Drawable getBackground() {
        return mBackground;
    }

    /**
     * <p>
     * Change the background drawable for this popup window. The background can
     * be set to null.
     * </p>
     *
     * @param background
     *            the popup's background
     * @deprecated [Not use any longer]
     */
    /**@hide*/
    public void setBackgroundDrawable(Drawable background) {
        mBackground = background;
    }

    /**
     *  @hide  Only used by HtcTabPopupWindow.
     */
    public void setTriangleBackgroundDrawable(Drawable background) {
        mTriangle = background;
    }

    /**
     *  Specify an alpha value for the drawable.
     *  0 means fully transparent, and 255 means fully opaque.
     *
     *  @param alpha the alpha value for the drawable.
     * @hide
     */
    public void setBackgroundAlpha(int alpha) {
        if(mBackground != null)
            mBackground.setAlpha(alpha);
        if(mTriangle!= null)
            mTriangle.setAlpha(alpha);
    }

    /**
     * <p>
     * Return the animation style to use the popup appears and disappears
     * </p>
     *
     * @return the animation style to use the popup appears and disappears
     * @hide
     */
    public int getAnimationStyle() {
        return mAnimationStyle;
    }

    /**
     * Set the flag on popup to ignore cheek press eventt; by default this flag
     * is set to false which means the pop wont ignore cheek press dispatch
     * events.
     *
     * <p>
     * If the popup is showing, calling this method will take effect only the
     * next time the popup is shown or through a manual call to one of the
     * {@link #update()} methods.
     * </p>
     *
     * @see #update()
     * @hide
     */
    public void setIgnoreCheekPress() {
        mIgnoreCheekPress = true;
    }

    /**
     * <p>
     * Change the animation style resource for this popup.
     * </p>
     *
     * <p>
     * If the popup is showing, calling this method will take effect only the
     * next time the popup is shown or through a manual call to one of the
     * {@link #update()} methods.
     * </p>
     *
     * @param animationStyle
     *            animation style to use when the popup appears and disappears.
     *            Set to -1 for the default animation, 0 for no animation, or a
     *            resource identifier for an explicit animation.
     *
     * @see #update()
     * @hide
     */
    public void setAnimationStyle(int animationStyle) {
        mAnimationStyle = animationStyle;
    }

    /**
     * <p>
     * Return the view used as the content of the popup window.
     * </p>
     *
     * @return a {@link android.view.View} representing the popup's content
     *
     * @see #setContentView(android.view.View)
     * @hide
     */
    public View getContentView() {
        return mContentView;
    }

    /**
     * <p>
     * Change the popup's content. The content is represented by an instance of
     * {@link android.view.View}.
     * </p>
     *
     * <p>
     * This method has no effect if called when the popup is showing.
     * </p>
     *
     * @param contentView
     *            the new content for the popup
     *
     * @see #getContentView()
     * @see #isShowing()
     *
     * @hide
     */
    public void setContentView(View contentView) {
        if (isShowing()) {
            return;
        }

        mContentView = contentView;
        if(mContentView == null) return;
        if (mContext == null) {
            mContext = mContentView.getContext();
        }

        if (mWindowManager == null) {
            if (mContext != null)
                mWindowManager = (WindowManager) mContext
                    .getSystemService(Context.WINDOW_SERVICE);
        }
    }

    /**
     * Set a callback for all touch events being dispatched to the popup window.
     *
     *  @param l a callback for all touch events being dispatched to the popup window.
     *
     *  @hide
     */
    public void setTouchInterceptor(OnTouchListener l) {
        mTouchInterceptor = l;
    }

    /**
     * <p>
     * Indicate whether the popup window can grab the focus.
     * </p>
     *
     * @return true if the popup is focusable, false otherwise
     *
     * @see #setFocusable(boolean)
     * @hide
     */
    public boolean isFocusable() {
        return mFocusable;
    }

    /**
     * <p>
     * Changes the focusability of the popup window. When focusable, the window
     * will grab the focus from the current focused widget if the popup contains
     * a focusable {@link android.view.View}. By default a popup window is not
     * focusable.
     * </p>
     *
     * <p>
     * If the popup is showing, calling this method will take effect only the
     * next time the popup is shown or through a manual call to one of the
     * {@link #update()} methods.
     * </p>
     *
     * @param focusable
     *            true if the popup should grab focus, false otherwise.
     *
     * @see #isFocusable()
     * @see #isShowing()
     * @see #update()
     *
     * @hide
     */
    public void setFocusable(boolean focusable) {
        mFocusable = focusable;
    }

    /**
     * Return the current value in {@link #setInputMethodMode(int)}.
     *
     * @see #setInputMethodMode(int)
     * @hide
     */
    public int getInputMethodMode() {
        return mInputMethodMode;

    }

    /**
     * Control how the popup operates with an input method: one of
     * {@link #INPUT_METHOD_FROM_FOCUSABLE}, {@link #INPUT_METHOD_NEEDED}, or
     * {@link #INPUT_METHOD_NOT_NEEDED}.
     *
     * <p>
     * If the popup is showing, calling this method will take effect only the
     * next time the popup is shown or through a manual call to one of the
     * {@link #update()} methods.
     * </p>
     *
     * @param mode how the popup operates with an input method: one of
     * {@link #INPUT_METHOD_FROM_FOCUSABLE}, {@link #INPUT_METHOD_NEEDED}, or
     * {@link #INPUT_METHOD_NOT_NEEDED}.
     *
     * @see #getInputMethodMode()
     * @see #update()
     *
     * @hide
     */
    public void setInputMethodMode(int mode) {
        mInputMethodMode = mode;
    }

    /**
     * Sets the operating mode for the soft input area.
     *
     * @param mode
     *            The desired mode, see
     *            {@link android.view.WindowManager.LayoutParams#softInputMode}
     *            for the full list
     *
     * @see android.view.WindowManager.LayoutParams#softInputMode
     * @see #getSoftInputMode()
     * @hide
     */
    public void setSoftInputMode(int mode) {
        mSoftInputMode = mode;
    }

    /**
     * Returns the current value in {@link #setSoftInputMode(int)}.
     *
     * @see #setSoftInputMode(int)
     * @see android.view.WindowManager.LayoutParams#softInputMode
     * @hide
     */
    public int getSoftInputMode() {
        return mSoftInputMode;
    }

    /**
     * <p>
     * Indicates whether the popup window receives touch events.
     * </p>
     *
     * @return true if the popup is touchable, false otherwise
     *
     * @see #setTouchable(boolean)
     * @hide
     */
    public boolean isTouchable() {
        return mTouchable;
    }

    /**
     * <p>
     * Changes the touchability of the popup window. When touchable, the window
     * will receive touch events, otherwise touch events will go to the window
     * below it. By default the window is touchable.
     * </p>
     *
     * <p>
     * If the popup is showing, calling this method will take effect only the
     * next time the popup is shown or through a manual call to one of the
     * {@link #update()} methods.
     * </p>
     *
     * @param touchable
     *            true if the popup should receive touch events, false otherwise
     *
     * @see #isTouchable()
     * @see #isShowing()
     * @see #update()
     *
     * @hide
     */
    public void setTouchable(boolean touchable) {
        mTouchable = touchable;
    }

    /**
     * <p>
     * Indicates whether the popup window will be informed of touch events
     * outside of its window.
     * </p>
     *
     * @return true if the popup is outside touchable, false otherwise
     *
     * @see #setOutsideTouchable(boolean)
     * @hide
     */
    public boolean isOutsideTouchable() {
        return mOutsideTouchable;
    }

    /**
     * <p>
     * Controls whether the pop-up will be informed of touch events outside of
     * its window. This only makes sense for pop-ups that are touchable but not
     * focusable, which means touches outside of the window will be delivered to
     * the window behind. The default is false.
     * </p>
     *
     * <p>
     * If the popup is showing, calling this method will take effect only the
     * next time the popup is shown or through a manual call to one of the
     * {@link #update()} methods.
     * </p>
     *
     * @param touchable
     *            true if the popup should receive outside touch events, false
     *            otherwise
     *
     * @see #isOutsideTouchable()
     * @see #isShowing()
     * @see #update()
     *
     * @hide
     */
    public void setOutsideTouchable(boolean touchable) {
        mOutsideTouchable = touchable;
    }

    /**
     * <p>
     * Indicates whether clipping of the popup window is enabled.
     * </p>
     *
     * @return true if the clipping is enabled, false otherwise
     *
     * @see #setClippingEnabled(boolean)
     * @hide
     */
    public boolean isClippingEnabled() {
        return mClippingEnabled;
    }

    /**
     * <p>
     * Allows the popup window to extend beyond the bounds of the screen. By
     * default the window is clipped to the screen boundaries. Setting this to
     * false will allow windows to be accurately positioned.
     * </p>
     *
     * <p>
     * If the popup is showing, calling this method will take effect only the
     * next time the popup is shown or through a manual call to one of the
     * {@link #update()} methods.
     * </p>
     *
     * @param enabled
     *            false if the window should be allowed to extend outside of the
     *            screen
     * @see #isShowing()
     * @see #isClippingEnabled()
     * @see #update()
     * @hide
     */
    public void setClippingEnabled(boolean enabled) {
        mClippingEnabled = enabled;
    }

    /**
     * Clip this popup window to the screen, but not to the containing window.
     *
     * @param enabled
     *            True to clip to the screen.
     * @exthide
     * @hide
     */
    public void setClipToScreenEnabled(boolean enabled) {
        mClipToScreen = enabled;
        setClippingEnabled(!enabled);
    }

    /**
     * Allow PopupWindow to scroll the anchor's parent to provide more room for
     * the popup. Enabled by default.
     *
     * @param enabled
     *            True to scroll the anchor's parent when more room is desired
     *            by the popup.
     */
    void setAllowScrollingAnchorParent(boolean enabled) {
        mAllowScrollingAnchorParent = enabled;
    }

    /**
     * <p>
     * Indicates whether the popup window supports splitting touches.
     * </p>
     *
     * @return true if the touch splitting is enabled, false otherwise
     *
     * @see #setSplitTouchEnabled(boolean)
     * @hide
     */
    public boolean isSplitTouchEnabled() {
        return false;
        // TODO for gingerbread disable this function
        // if (mSplitTouchEnabled < 0 && mContext != null) {
        // return mContext.getApplicationInfo().targetSdkVersion >=
        // Build.VERSION_CODES.HONEYCOMB;
        // }
        // return mSplitTouchEnabled == 1;
    }

    /**
     * <p>
     * Allows the popup window to split touches across other windows that also
     * support split touch. When this flag is false, the first pointer that goes
     * down determines the window to which all subsequent touches go until all
     * pointers go up. When this flag is true, each pointer (not necessarily the
     * first) that goes down determines the window to which all subsequent
     * touches of that pointer will go until that pointer goes up thereby
     * enabling touches with multiple pointers to be split across multiple
     * windows.
     * </p>
     *
     * @param enabled
     *            true if the split touches should be enabled, false otherwise
     * @see #isSplitTouchEnabled()
     * @hide
     */
    public void setSplitTouchEnabled(boolean enabled) {
        mSplitTouchEnabled = enabled ? 1 : 0;
    }

    /**
     * <p>
     * Indicates whether the popup window will be forced into using absolute
     * screen coordinates for positioning.
     * </p>
     *
     * @return true if the window will always be positioned in screen
     *         coordinates.
     * @hide
     */
    public boolean isLayoutInScreenEnabled() {
        return mLayoutInScreen;
    }

    /**
     * <p>
     * Allows the popup window to force the flag
     * {@link WindowManager.LayoutParams#FLAG_LAYOUT_IN_SCREEN}, overriding
     * default behavior. This will cause the popup to be positioned in absolute
     * screen coordinates.
     * </p>
     *
     * @param enabled
     *            true if the popup should always be positioned in screen
     *            coordinates
     * @hide
     */
    public void setLayoutInScreenEnabled(boolean enabled) {
        mLayoutInScreen = enabled;
    }

    /**
     * Set the layout type for this window. Should be one of the TYPE constants
     * defined in {@link WindowManager.LayoutParams}.
     *
     * @param layoutType
     *            Layout type for this window.
     * @hide
     */
    public void setWindowLayoutType(int layoutType) {
        mWindowLayoutType = layoutType;
    }

    /**
     * @return The layout type for this window.
     * @hide
     */
    public int getWindowLayoutType() {
        return mWindowLayoutType;
    }

    /**
     * <p>
     * Change the width and height measure specs that are given to the window
     * manager by the popup. By default these are 0, meaning that the current
     * width or height is requested as an explicit size from the window manager.
     * You can supply {@link ViewGroup.LayoutParams#WRAP_CONTENT} or
     * {@link ViewGroup.LayoutParams#MATCH_PARENT} to have that measure spec
     * supplied instead, replacing the absolute width and height that has been
     * set in the popup.
     * </p>
     *
     * <p>
     * If the popup is showing, calling this method will take effect only the
     * next time the popup is shown.
     * </p>
     *
     * @param widthSpec
     *            an explicit width measure spec mode, either
     *            {@link ViewGroup.LayoutParams#WRAP_CONTENT},
     *            {@link ViewGroup.LayoutParams#MATCH_PARENT}, or 0 to use the
     *            absolute width.
     * @param heightSpec
     *            an explicit height measure spec mode, either
     *            {@link ViewGroup.LayoutParams#WRAP_CONTENT},
     *            {@link ViewGroup.LayoutParams#MATCH_PARENT}, or 0 to use the
     *            absolute height.
     *
     * @hide
     */
    public void setWindowLayoutMode(int widthSpec, int heightSpec) {
        mWidthMode = widthSpec;
        mHeightMode = heightSpec;
    }

    /**
     * <p>
     * Return this popup's height MeasureSpec
     * </p>
     *
     * @return the height MeasureSpec of the popup
     *
     * @see #setHeight(int)
     * @hide
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * <p>
     * Change the popup's height MeasureSpec
     * </p>
     *
     * <p>
     * If the popup is showing, calling this method will take effect only the
     * next time the popup is shown.
     * </p>
     *
     * @param height
     *            the height MeasureSpec of the popup
     *
     * @see #getHeight()
     * @see #isShowing()
     *
     * @hide
     */
    public void setHeight(int height) {
        mHeight = height;
    }

    /**
     * <p>
     * Return this popup's width MeasureSpec
     * </p>
     *
     * @return the width MeasureSpec of the popup
     *
     * @see #setWidth(int)
     * @hide
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * <p>
     * Change the popup's width MeasureSpec
     * </p>
     *
     * <p>
     * If the popup is showing, calling this method will take effect only the
     * next time the popup is shown.
     * </p>
     *
     * @param width
     *            the width MeasureSpec of the popup
     *
     * @see #getWidth()
     * @see #isShowing()
     *
     * @hide
     */
    public void setWidth(int width) {
        mWidth = width;
    }

    /**
     * <p>
     * Indicate whether this popup window is showing on screen.
     * </p>
     *
     * @return true if the popup is showing, false otherwise
     *
     */
    public boolean isShowing() {
        return mIsShowing;
    }

    /**
     * <p>
     * Display the content view in a popup window at the specified location. If
     * the popup window cannot fit on screen, it will be clipped. See
     * {@link android.view.WindowManager.LayoutParams} for more information on
     * how gravity and the x and y parameters are related. Specifying a gravity
     * of {@link android.view.Gravity#NO_GRAVITY} is similar to specifying
     * <code>Gravity.LEFT | Gravity.TOP</code>.
     * </p>
     *
     * @param parent
     *            a parent view to get the
     *            {@link android.view.View#getWindowToken()} token from
     * @param gravity
     *            the gravity which controls the placement of the popup window
     * @param x
     *            the popup's x location offset
     * @param y
     *            the popup's y location offset
     */
    public void showAtLocation(View parent, int gravity, int x, int y) {
        mExpandDirection = PopupBubbleWindow.EXPAND_NO_ANCHOR;
        mXoff = x;
        mYoff = y;

        if (isShowing() || mContentView == null || parent.getWindowToken() == null) {
            return;
        }

        unregisterForScrollChanged();
        registerForGlobalChanged(parent);
        mIsShowing = true;
        mIsDropdown = false;

        WindowManager.LayoutParams p = createPopupLayout(parent
                .getWindowToken());
//      p.windowAnimations = computeAnimationResource();

        preparePopup(p);
        if (gravity == Gravity.NO_GRAVITY) {
            gravity = Gravity.TOP | Gravity.LEFT;
        }
        p.gravity = gravity;
        p.x = x;
        p.y = y;
        if (mHeightMode < 0)
            p.height = mLastHeight = mHeightMode;
        if (mWidthMode < 0)
            p.width = mLastWidth = mWidthMode;

        if (p != null && p.height == 0) {
            p.height = minWindowHeight;
        }
        if (p != null){
            p.windowAnimations = R.style.DropDownCenter;
        }

        PopupBubbleViewContainer viewContainer = (PopupBubbleViewContainer) mPopupView;

        if (viewContainer != null) {
            // setup the indicator environment and offset
            viewContainer.updateEnvironmentNoArrow();
        }

        if (p != null)
        invokePopup(p);
        //dropdownLayout = p;
    }

    // define minimum height of window
    private final int minWindowHeight = 100;

    /**
     * <p>
     * Display the content view in a popup window anchored to the bottom-left
     * corner of the anchor view. If there is not enough room on screen to show
     * the popup in its entirety, this method tries to find a parent scroll view
     * to scroll. If no parent scroll view can be scrolled, the bottom-left
     * corner of the popup is pinned at the top left corner of the anchor view.
     * </p>
     *
     * @param anchor
     *            the view on which to pin the popup window
     *
     * @see #dismiss()
     */
    public void showAsDropDown(View anchor) {
        showAsDropDown(anchor, 0, 0);
    }

    /**
     * <p>
     * Display the content view in a popup window anchored to the bottom-left
     * corner of the anchor view offset by the specified x and y coordinates. If
     * there is not enough room on screen to show the popup in its entirety,
     * this method tries to find a parent scroll view to scroll. If no parent
     * scroll view can be scrolled, the bottom-left corner of the popup is
     * pinned at the top left corner of the anchor view.
     * </p>
     * <p>
     * If the view later scrolls to move <code>anchor</code> to a different
     * location, the popup will be moved correspondingly.
     * </p>
     *
     * @param anchor
     *            the view on which to pin the popup window
     * @param xoff
     *            the x offset
     * @param yoff
     *            the y offset
     *
     * @see #dismiss()
     */
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (isShowing() || mContentView == null ||  anchor.getWindowToken() == null) {
            return;
        }
        mXoff = xoff;
        mYoff = yoff;

        Rect rect = new Rect();

        if (mBackground != null) {
            mBackground.getPadding(rect);
        }

        if (mWidthMode == ViewGroup.LayoutParams.WRAP_CONTENT ||
                mHeightMode == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mContentView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        if (mWidthMode == ViewGroup.LayoutParams.WRAP_CONTENT) {
        mLastWidth = mWidth = mContentView.getMeasuredWidth() + rect.left + rect.right;
        }

        if (mHeightMode == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mLastHeight = mHeight = mContentView.getMeasuredHeight() + rect.top
                    + rect.bottom;
        }

        registerForScrollChanged(anchor, xoff, yoff);
        registerForGlobalChanged(anchor);
        mIsShowing = true;
        mIsDropdown = true;

        WindowManager.LayoutParams p = createPopupLayout(anchor
                .getWindowToken());

        if (mWidthMode == ViewGroup.LayoutParams.MATCH_PARENT)
            p.width = mLastWidth = mWidthMode;
        if (mHeightMode == ViewGroup.LayoutParams.MATCH_PARENT)
            p.height = mLastHeight = mHeightMode;

        preparePopup(p);

        findDropDownPosition(anchor, p, xoff, yoff);

        if (p != null && p.height == 0) {
            p.height = minWindowHeight;
        }

        WindowManager.LayoutParams p2 = createTriangleLayout(anchor
                .getWindowToken());

        findTrianglePosition(anchor, p2, xoff, yoff);

        if (p != null){
        p.windowAnimations = computeAnimationResource(p, p2);

        WindowManager.LayoutParams lp = fixDrawingPosition(p, p2);
        invokePopup(lp);
        }
    }

    /**
     * @deprecated
     * @hide
     */
    public void setAnimationListener(android.view.animation.Animation.AnimationListener listener) {
    }

    /**
     * Popup window will be show at indicate direction: one of
     * {@link #EXPAND_UP}, {@link #EXPAND_DOWN}, {@link #EXPAND_LEFT}, or
     * {@link #EXPAND_RIGHT}.
     *
     * <p>
     * If the popup is showing, calling this method will take effect only the
     * next time.
     *
     * @param direction the popup window expand direction.
     *
     * @see #getExpandDirection()
     */
    public void setExpandDirection(int direction) {
        mExpandDirection = direction;
    }

    /**
     * Return the current expand direction in {@link #setExpandDirection(int)}.
     *
     * @see #getExpandDirection(int)
     * @hide
     */
    public int getExpandDirection() {
        return mExpandDirection;
    }

    /**
     * Indicates whether the popup is showing above (the y coordinate of the
     * popup's bottom is less than the y coordinate of the anchor) or below the
     * anchor view (the y coordinate of the popup is greater than y coordinate
     * of the anchor's bottom).
     *
     * The value returned by this method is meaningful only after
     * {@link #showAsDropDown(android.view.View)} or
     * {@link #showAsDropDown(android.view.View, int, int)} was invoked.
     *
     * @return True if this popup is showing above the anchor view, false
     *         otherwise.
     * @hide
     */
    public boolean isAboveAnchor() {
        return mAboveAnchor;
    }

    /**
     * <p>
     * Prepare the popup by embedding in into a new ViewGroup if the background
     * drawable is not null. If embedding is required, the layout parameters'
     * height is mnodified to take into account the background's padding.
     * </p>
     *
     * @param p
     *            the layout parameters of the popup's content view
     */
    private void preparePopup(WindowManager.LayoutParams p) {
        if (mContentView == null || mContext == null || mWindowManager == null) {
            throw new IllegalStateException(
                    "You must specify a valid content view by "
                            + "calling setContentView() before attempting to show the popup.");
        }

        if (mBackground != null) {
            final ViewGroup.LayoutParams layoutParams = mContentView
                    .getLayoutParams();
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            if (layoutParams != null
                    && layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            // when a background is available, we embed the content view
            // within another view that owns the background drawable
            PopupBubbleViewContainer popupViewContainer = new PopupBubbleViewContainer(
                    mContext);
            PopupBubbleViewContainer.LayoutParams listParams = new PopupBubbleViewContainer.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, height);

            popupViewContainer.setBackgroundDrawable(mBackground);
            popupViewContainer.addView(mContentView, listParams);

            mPopupView = popupViewContainer;
        } else {
            mPopupView = mContentView;
        }
        mPopupWidth = p.width;
        mPopupHeight = p.height;
    }

    /**
     * <p>
     * Invoke the popup window by adding the content view to the window manager.
     * </p>
     *
     * <p>
     * The content view must be non-null when this method is invoked.
     * </p>
     *
     * @param p
     *            the layout parameters of the popup's content view
     */
    private void invokePopup(WindowManager.LayoutParams p) {
        if (mContext != null)
            p.packageName = mContext.getPackageName();
        if (mWindowManager != null)
            mWindowManager.addView(mPopupView, p);
        isViewRemove = false;
        setTouchDelegate();
    }

    /**
     * <p>
     * Generate the layout parameters for the popup window.
     * </p>
     *
     * @param token
     *            the window token used to bind the popup's window
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
        p.width = mLastWidth = mWidth;
        p.height = mLastHeight = mHeight;
        if (mBackground != null) {
            p.format = mBackground.getOpacity();
        } else {
            p.format = PixelFormat.TRANSLUCENT;
        }
        p.flags = computeFlags(p.flags);
        p.type = mWindowLayoutType;
        p.token = token;
        p.softInputMode = mSoftInputMode;
        p.setTitle("PopupWindow:" + Integer.toHexString(hashCode()));

        return p;
    }

    private WindowManager.LayoutParams createTriangleLayout(IBinder token) {
        // take care about orientation when the popup window doesn't recreate,
        // the triangle layout maybe have problem on size
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        // these gravity settings put the view at the top left corner of the
        // screen. The view is then positioned to the appropriate location
        // by setting the x and y offsets to match the anchor's bottom
        // left corner
        p.gravity = Gravity.LEFT | Gravity.TOP;
        if (mExpandDirection == PopupBubbleWindow.EXPAND_DEFAULT
                || mExpandDirection == PopupBubbleWindow.EXPAND_UP
                || mExpandDirection == PopupBubbleWindow.EXPAND_DOWN) {
            p.width = mTriangle.getIntrinsicWidth();
            p.height = mTriangle.getIntrinsicHeight();
        } else {
            if(mExpandRightTriangledDrawable != null)
                p.width = mExpandRightTriangledDrawable.getIntrinsicWidth();
            if(mExpandLeftTriangledDrawable != null)
                p.height = mExpandLeftTriangledDrawable.getIntrinsicHeight();
        }
        if (mBackground != null) {
            p.format = mBackground.getOpacity();
        } else {
            p.format = PixelFormat.TRANSLUCENT;
        }
        p.flags = computeTriangleFlags(p.flags);
        p.type = mWindowLayoutType;
        p.token = token;
        p.softInputMode = mSoftInputMode;

        return p;
    }

    private int computeFlags(int curFlags) {
        curFlags &= ~(WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH);
        if (mIgnoreCheekPress) {
            curFlags |= WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES;
        }
        if (!mFocusable) {
            curFlags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            if (mInputMethodMode == INPUT_METHOD_NEEDED) {
                curFlags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
            }
        } else if (mInputMethodMode == INPUT_METHOD_NOT_NEEDED) {
            curFlags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        }
        if (!mTouchable) {
            curFlags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }
        if (mOutsideTouchable) {
            curFlags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        }
        if (!mClippingEnabled) {
            curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        }
        if (isSplitTouchEnabled()) {
            curFlags |= WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
        }
        if (mLayoutInScreen) {
            curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        }
        return curFlags;
    }

    private int computeTriangleFlags(int curFlags) {
        curFlags &= ~(WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH);
        if (mIgnoreCheekPress) {
            curFlags |= WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES;
        }

        curFlags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        if (mInputMethodMode == INPUT_METHOD_NEEDED) {
            curFlags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        }

        curFlags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        // curFlags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

        if (!mClippingEnabled) {
            curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        }
        if (isSplitTouchEnabled()) {
            curFlags |= WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
        }
        if (mLayoutInScreen) {
            curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        }
        return curFlags;
    }

    private int computeAnimationResource(WindowManager.LayoutParams p, WindowManager.LayoutParams p2) {
    // 20120518 add by Evelyn, new animation style
        float triangleCenterX = (float)p2.x + (float)p2.width/2;
        float triangleCenterY = (float)p2.y + (float)p2.height/2;
        float dropdownHorizontal1 =  (float)p.x + (float)p.width/3;
        float dropdownHorizontal2 = (float)p.x + (float)p.width/3 *2;
        float dropdownVertical1 =  (float)p.y + (float)p.height/3;
        float dropdownVertical2 = (float)p.y + (float)p.height/3 *2;

        boolean isEast  = triangleCenterX > dropdownHorizontal2;
        boolean isWest  = triangleCenterX < dropdownHorizontal1;
        boolean isNorth = triangleCenterY < dropdownVertical1;
        boolean isSouth = triangleCenterY > dropdownVertical2;
    // end: Evelyn
        if (mAnimationStyle == -1) {
            switch (mExpandDirection) {
            case EXPAND_UP:
                if (isEast)
                    return R.style.DropDownUpBottomEast; // new animation style
                else if (isWest)
                    return R.style.DropDownUpBottomWest; // new animation style
                else
                    return R.style.DropDownUpBottomCenter;
            case EXPAND_DOWN:
                if (isEast)
                    return R.style.DropDownDownTopEast; // new animation style
                else if (isWest)
                    return R.style.DropDownDownTopWest; // new animation style
                else
                    return R.style.DropDownDownTopCenter;
            case EXPAND_LEFT:
                if (isNorth)
                    return R.style.DropDownDownTopEast; // new animation style
                else if (isSouth)
                    return R.style.DropDownUpBottomEast; // new animation style
                else
                    return R.style.DropDownLeft;
            case EXPAND_RIGHT:
                if (isNorth)
                    return R.style.DropDownDownTopWest; // new animation style
                else if (isSouth)
                    return R.style.DropDownUpBottomWest; // new animation style
                else
                    return R.style.DropDownRight;
            default:
                return 0;
            }
        }
        return mAnimationStyle;
    }

    /**
     * <p>
     * Positions the popup window on screen. When the popup window is too tall
     * to fit under the anchor, a parent scroll view is seeked and scrolled up
     * to reclaim space. If scrolling is not possible or not enough, the popup
     * window gets moved on top of the anchor.
     * </p>
     *
     * <p>
     * The height must have been set on the layout parameters prior to calling
     * this method.
     * </p>
     *
     * @param anchor
     *            the view on which the popup window must be anchored
     * @param p
     *            the layout parameters used to display the drop down
     *
     * @return true if the popup is translated upwards to fit on screen
     */
    private void findDropDownPosition(View anchor,
            WindowManager.LayoutParams p, int xoff, int yoff) {
        anchor.getLocationInWindow(mScreenLocation);
        anchor.getLocationInWindow(mDrawingLocation);
        mGap =  mScreenLocation[1] - mDrawingLocation[1];

        int status_bar_height = HtcCommonUtil.getStatusBarHeight(mContext);
        final Rect displayFrame = new Rect();
        if (mContext != null)
            ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRectSize(displayFrame);

        if (mExpandDirection == PopupBubbleWindow.EXPAND_DEFAULT || mExpandDirectionUndefined) {
            mExpandDirection = ((displayFrame.bottom - mScreenLocation[1]
                    - anchor.getHeight() - yoff) < (mScreenLocation[1] - yoff - displayFrame.top)) ? PopupBubbleWindow.EXPAND_UP
                    : PopupBubbleWindow.EXPAND_DOWN;
            if(anchor.getHeight()==0){
                mExpandDirectionUndefined = true;
            }else{
                mExpandDirectionUndefined = false;
            }
        }

        if (mExpandDirection == PopupBubbleWindow.EXPAND_UP
                || mExpandDirection == PopupBubbleWindow.EXPAND_DOWN) {
            p.x = mDrawingLocation[0] + anchor.getWidth()/2 - p.width/2 + xoff;
            p.y = mExpandDirection == PopupBubbleWindow.EXPAND_UP ? mDrawingLocation[1]
                    + yoff - (mBubbleHeadOffset - mPopupShadowBottom) - mPopupHeight
                    : mDrawingLocation[1] + yoff + (mBubbleHeadOffset - mPopupShadowTop)
                            + anchor.getHeight();
        } else {
            p.x = (mExpandDirection == PopupBubbleWindow.EXPAND_RIGHT) ? mDrawingLocation[0]
                    + anchor.getWidth() + (mBubbleLandHeadOffset - mPopupShadowLeft) + xoff
                    : mDrawingLocation[0] - p.width - (mBubbleLandHeadOffset - mPopupShadowRight)
                            + xoff;
            p.y = mDrawingLocation[1] + anchor.getHeight()/2 - mPopupHeight/2 + yoff;
        }

        if (mClipToScreen) {
            final int displayFrameWidth = displayFrame.right
                    - displayFrame.left;
            final int displayFrameHeight = displayFrame.bottom
                    - displayFrame.top;


            int right = p.x + p.width;
            int bottom = p.y + p.height;

            if (mExpandDirection == PopupBubbleWindow.EXPAND_UP
                    || mExpandDirection == PopupBubbleWindow.EXPAND_DOWN) {
                // check left
                if (p.x < (mBubbleBodyOffset - mPopupShadowLeft)) {
                    p.x = (mBubbleBodyOffset - mPopupShadowLeft) + xoff;
                }
                // check top
                if (mExpandDirection == PopupBubbleWindow.EXPAND_UP){
                    if (p.y < (mBubbleBodyOffset - mPopupShadowBottom) + status_bar_height - mGap) {
                        p.y = (mBubbleBodyOffset - mPopupShadowBottom) + status_bar_height - mGap;
                    }
                }else{
                    if (p.y < (mBubbleHeadOffset - mPopupShadowTop) + status_bar_height) {
                        p.y = (mBubbleHeadOffset - mPopupShadowTop) + status_bar_height;
                }
                }
                // check width
                if (displayFrameWidth - (mBubbleBodyOffset - mPopupShadowLeft) - (mBubbleBodyOffset - mPopupShadowRight) < mPopupWidth) {
                    p.x = (mBubbleBodyOffset - mPopupShadowLeft) + xoff;
                    p.width = displayFrameWidth - (mBubbleBodyOffset - mPopupShadowLeft) - (mBubbleBodyOffset - mPopupShadowRight);
                    resetHeight(p);
                } else if (right > (displayFrameWidth - (mBubbleBodyOffset - mPopupShadowRight))) {
                    p.x -= right - displayFrameWidth + (mBubbleBodyOffset - mPopupShadowRight)
                            - xoff;
                }

                // check height
                if (mExpandDirection == PopupBubbleWindow.EXPAND_UP
                        && mDrawingLocation[1] + mGap - (mBubbleHeadOffset - mPopupShadowBottom) - (mBubbleBodyOffset - mPopupShadowTop) - status_bar_height < mPopupHeight) {
                    p.height = mDrawingLocation[1] + mGap - displayFrame.top - (mBubbleHeadOffset - mPopupShadowBottom)  -(mBubbleBodyOffset - mPopupShadowTop)
                            - yoff - status_bar_height;
                    p.y = mDrawingLocation[1] + yoff - p.height - (mBubbleHeadOffset - mPopupShadowBottom);
                } else if (mExpandDirection == PopupBubbleWindow.EXPAND_DOWN
                        && p.y + mPopupHeight > displayFrame.bottom
                                - (mBubbleBodyOffset - mPopupShadowBottom) - mGap) {
                    //should take vertical offset into consideration when computing window height
                    p.height = displayFrame.bottom - (mBubbleBodyOffset - mPopupShadowBottom) - p.y -mGap;
                }

            } else {
                // check top
                if (p.y < status_bar_height + (mBubbleLandBodyOffset - mPopupShadowTop) - mGap) {
                    p.y = status_bar_height + (mBubbleLandBodyOffset - mPopupShadowTop) + yoff - mGap;

                    if (p.y + p.height > displayFrame.bottom - (mBubbleLandBodyOffset - mPopupShadowBottom) - mGap) {
                        p.height = displayFrame.bottom - (mBubbleLandBodyOffset - mPopupShadowBottom) - p.y - mGap;
                    }
                }

                // check left
                if (mExpandDirection == PopupBubbleWindow.EXPAND_LEFT && (p.x < displayFrame.left + (mBubbleLandBodyOffset - mPopupShadowLeft))) {
                    p.width =  mDrawingLocation[0] - mBubbleLandBodyOffset - mBubbleLandHeadOffset - xoff ;
                    p.x = displayFrame.left + (mBubbleLandBodyOffset - mPopupShadowLeft) + xoff;
                    resetHeight(p);
                }
                // check right
                else if (mExpandDirection == PopupBubbleWindow.EXPAND_RIGHT && (p.x + p.width - mPopupShadowRight > displayFrame.right - mBubbleLandBodyOffset)) {
                    p.width = displayFrame.right - mBubbleLandBodyOffset - mBubbleLandHeadOffset - mDrawingLocation[0] -anchor.getWidth();
                    resetHeight(p);
                }

                // check bottom
                if (p.y + p.height > displayFrame.bottom - (mBubbleLandBodyOffset - mPopupShadowBottom) - mGap) {
                    int offset = (p.y + p.height) - (displayFrame.bottom - (mBubbleLandBodyOffset - mPopupShadowBottom) - mGap);
                    p.y -= offset + yoff;

                    if (p.y < status_bar_height + (mBubbleLandBodyOffset - mPopupShadowTop) - mGap) {
                        p.y = status_bar_height + (mBubbleLandBodyOffset - mPopupShadowTop) + yoff - mGap;
                        //p.height = displayFrame.bottom - displayFrame.top - 2 * mBubbleVerticalOffset;
                        p.height = displayFrame.bottom - (mBubbleLandBodyOffset - mPopupShadowBottom) - p.y - mGap;
                    }
                }
            }

        }

        p.gravity |= Gravity.DISPLAY_CLIP_VERTICAL | Gravity.DISPLAY_CLIP_HORIZONTAL;

        dropdownLayout = p;
    }

    /**
     * Reset text height when window width changes
     * @hide*/
    protected void resetHeight( WindowManager.LayoutParams params){

    }

    private void findTrianglePosition(View anchor,
            WindowManager.LayoutParams p, int xoff, int yoff) {
        anchor.getLocationInWindow(mDrawingLocation);
        Rect rect = new Rect();
        if (mBackground != null) {
            mBackground.getPadding(rect);
        }

        anchor.getLocationInWindow(mScreenLocation);
        final Rect displayFrame = new Rect();
        anchor.getWindowVisibleDisplayFrame(displayFrame);

        final View root = anchor.getRootView();

        if (mExpandDirection == PopupBubbleWindow.EXPAND_UP
                || mExpandDirection == PopupBubbleWindow.EXPAND_DOWN) {
            p.x = mDrawingLocation[0] + anchor.getWidth() / 2
                    - mTriangle.getIntrinsicWidth() / 2 + xoff + mCustomizeTriangleOffset;
            p.y = (mExpandDirection == PopupBubbleWindow.EXPAND_UP) ? mDrawingLocation[1]
                    + mTriangledOffset
                    - mPopupShadowBottom
                    - (mBubbleHeadOffset - mPopupShadowBottom)
                    + yoff
                    : mDrawingLocation[1] + anchor.getHeight()
                            - mTriangle.getIntrinsicHeight()
                            + (mBubbleHeadOffset - mPopupShadowTop) - mTriangledOffset
                            + mPopupShadowTop + yoff;
        } else {
            p.x = (mExpandDirection == PopupBubbleWindow.EXPAND_LEFT) ? mDrawingLocation[0]
                    + xoff
                    - (mBubbleLandHeadOffset - mPopupShadowRight)
                    + mTriangledOffset
                    - mPopupShadowRight
                    : mDrawingLocation[0] + anchor.getWidth()
                            + (mBubbleLandHeadOffset - mPopupShadowLeft)
                            - mExpandLeftTriangledDrawable.getIntrinsicWidth()
                            + xoff - mTriangledOffset + mPopupShadowLeft;
            p.y = mDrawingLocation[1] + anchor.getHeight() / 2
                    - mExpandRightTriangledDrawable.getIntrinsicHeight() / 2
                    + yoff + mCustomizeTriangleOffset;
        }

        if (mExpandDirection == PopupBubbleWindow.EXPAND_UP || mExpandDirection == PopupBubbleWindow.EXPAND_DOWN) {
            int start = dropdownLayout.x + mPopupShadowLeft + mTriangleEdgeLimit;
            int end = dropdownLayout.x + dropdownLayout.width - mPopupShadowRight - mTriangleEdgeLimit;
            if (p.x < start) p.x = start;
            else if (p.x + p.width > end) p.x = end - p.width;
        } else {
            int start = dropdownLayout.y + mPopupShadowTop + mTriangleEdgeLimit;
            int end = dropdownLayout.y + dropdownLayout.height - mPopupShadowBottom - mTriangleEdgeLimit;
            if (p.y < start) p.y = start;
            else if(p.y + p.height > end) p.y = end - p.height;
        }

        p.gravity |= Gravity.DISPLAY_CLIP_VERTICAL | Gravity.DISPLAY_CLIP_HORIZONTAL;
        triangleLayout = p;
    }

    /**
     * caculate window position/width/height and triangle position calculate the
     * intro scale animation pivot position
     *
     * @param p
     *            The popup bubble window's layoutparams
     * @param p2
     *            The Triangle window's layoutparams
     * @return
     *            The correct height and position layout parameters
     */
    private WindowManager.LayoutParams fixDrawingPosition(WindowManager.LayoutParams p,
            WindowManager.LayoutParams p2) {

        CheckUtil.checkLayoutParams(p);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(p);

        // ykhong
        int indicatorXOffset = 0;
        int indicatorYOffset = 0;

        // calculate window position/width/height and triangle position
        // calculate the intro scale animation pivot position
        switch (mExpandDirection) {
        case EXPAND_DOWN:
            indicatorXOffset = p2.x - p.x;
            indicatorYOffset = (p2.y + p2.height) - p.y;

            lp.height = p.height + p2.height;
            lp.y = p.y - p2.height;
            break;

        case EXPAND_UP:
            indicatorXOffset = p2.x - p.x;
            indicatorYOffset = (p.y + p.height) - p2.y;

            lp.height = p.height + p2.height;
            //lp.y =  p.y;
            break;

        case EXPAND_RIGHT:
            indicatorXOffset = (p2.x + p2.width) - p.x;

            lp.width = p.width + p2.width + mTriangledOffset;
            lp.x = p.x - p2.width;

            // the showdow sizes are different on top and bottom
            lp.y = (mPopupShadowBottom - mPopupShadowTop)/2 + p.y;
            indicatorYOffset = p2.y - lp.y;
            break;

        case EXPAND_LEFT:
            indicatorXOffset = (p.x + p.width) - p2.x;

            lp.width = p.width + p2.width  + mTriangledOffset;

            // the showdow sizes are different on top and bottom
            lp.y = (mPopupShadowBottom - mPopupShadowTop)/2 + p.y;
            indicatorYOffset = p2.y - lp.y;
            //lp.x = p.x;
            break;
        }

        PopupBubbleViewContainer viewContainer = (PopupBubbleViewContainer) mPopupView;

        if (viewContainer != null) {
            // setup the indicator environment and offset
            viewContainer.updateEnvironment();

            viewContainer.updateIndicatorOffset(indicatorXOffset,
                    indicatorYOffset);
        }
        return lp;
    }

    /**
     * Returns the maximum height that is available for the popup to be
     * completely shown. It is recommended that this height be the maximum for
     * the popup's height, otherwise it is possible that the popup will be
     * clipped.
     *
     * @param anchor
     *            The view on which the popup window must be anchored.
     * @return The maximum available height for the popup to be completely
     *         shown.
     * @hide
     */
    public int getMaxAvailableHeight(View anchor) {
        return getMaxAvailableHeight(anchor, 0);
    }

    /**
     * Returns the maximum height that is available for the popup to be
     * completely shown. It is recommended that this height be the maximum for
     * the popup's height, otherwise it is possible that the popup will be
     * clipped.
     *
     * @param anchor
     *            The view on which the popup window must be anchored.
     * @param yOffset
     *            y offset from the view's bottom edge
     * @return The maximum available height for the popup to be completely
     *         shown.
     * @hide
     */
    public int getMaxAvailableHeight(View anchor, int yOffset) {
        return getMaxAvailableHeight(anchor, yOffset, false);
    }

    /**
     * Returns the maximum height that is available for the popup to be
     * completely shown, optionally ignoring any bottom decorations such as the
     * input method. It is recommended that this height be the maximum for the
     * popup's height, otherwise it is possible that the popup will be clipped.
     *
     * @param anchor
     *            The view on which the popup window must be anchored.
     * @param yOffset
     *            y offset from the view's bottom edge
     * @param ignoreBottomDecorations
     *            if true, the height returned will be all the way to the bottom
     *            of the display, ignoring any bottom decorations
     * @return The maximum available height for the popup to be completely
     *         shown.
     *
     * @hide Pending API council approval.
     */
    public int getMaxAvailableHeight(View anchor, int yOffset,
            boolean ignoreBottomDecorations) {
        final Rect displayFrame = new Rect();
        anchor.getWindowVisibleDisplayFrame(displayFrame);

        final int[] anchorPos = mDrawingLocation;
        anchor.getLocationInWindow(anchorPos);

        if (mExpandDirection == EXPAND_UP || mExpandDirection == EXPAND_DOWN || mExpandDirection == EXPAND_DEFAULT) {
            int bottomEdge = displayFrame.bottom;
            if (ignoreBottomDecorations) {
                Resources res = anchor.getContext().getResources();
                bottomEdge = getScreenHeightPx(res);
            }
            final int distanceToBottom = bottomEdge
                    - (anchorPos[1] + anchor.getHeight()) - yOffset;
            final int distanceToTop = anchorPos[1] + yOffset;

            // anchorPos[1] is distance from anchor to top of screen
            int returnedHeight = Math.max(distanceToBottom, distanceToTop);
            if (mBackground != null) {
                mBackground.getPadding(mTempRect);
                returnedHeight -= mTempRect.top + mTempRect.bottom;
            }
            return returnedHeight;
        } else  {
            int bottomEdge = displayFrame.bottom;
            if (ignoreBottomDecorations) {
                Resources res = anchor.getContext().getResources();
                bottomEdge = getScreenHeightPx(res);
            }
            int topEdge = HtcCommonUtil.getStatusBarHeight(mContext);
            int returnedHeight = bottomEdge - topEdge;
            if (mBackground != null) {
                mBackground.getPadding(mTempRect);
                returnedHeight -= mTempRect.top + mTempRect.bottom;
            }
            return returnedHeight;
        }

    }

    /**
     * <p>
     * Dispose of the popup window. This method can be invoked only after
     * {@link #showAsDropDown(android.view.View)} has been executed. Failing
     * that, calling this method will have no effect.
     * </p>
     *
     * @see #showAsDropDown(android.view.View)
     */
    public void dismiss() {
        if (isShowing() && mPopupView != null) {
            unregisterForScrollChanged();
            unregisterForGlobalChanged();
            releaseTouchDelegate();
            dismissWithoutAnimation();
        }
    }

    private void dismissWithoutAnimation() {
        if (isShowing() && mPopupView != null) {
            endDismissAnimation();
        }
    }

    // record the latest position for dropdown and triangle
    private boolean isViewRemove = true;

    private WindowManager.LayoutParams dropdownLayout = null;
    private WindowManager.LayoutParams triangleLayout = null;

    private void endDismissAnimation() {
        try {
            if (mWindowManager != null)
                mWindowManager.removeViewImmediate(mPopupView);
            isViewRemove = true;
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //mark below code, because remove view will make popup window will show empty.
            //if (mPopupView != mContentView && mPopupView instanceof ViewGroup) {
            //  ((PopupBubbleViewContainer) mPopupView)
            //          .removeView(mContentView);
            //}
            mPopupView = null;
            mIsShowing = false;
            if (mOnDismissListener != null) {
                android.util.Log.i(TAG, "call onDismissListener");
                mOnDismissListener.onDismiss();
            }
        }
    }

    /**
     * Sets the listener to be called when the window is dismissed.
     *
     * @param onDismissListener
     *            The listener.
     */
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    /**
     * Sets the listener to be called when the window is dismissed by user tap the quicktips.
     *
     * @param onUserDismissListener
     *            The listener.
     */
    public void setOnUserDismissListener(OnUserDismissListener onUserDismissListener) {
        mOnUserDismissListener = onUserDismissListener;
    }

    /**
     * @hide
     */
    public void setOnDismissListener(android.widget.PopupWindow.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    /**
     * Updates the state of the popup window, if it is currently being
     * displayed, from the currently set state. This include:
     * {@link #setClippingEnabled(boolean)}, {@link #setFocusable(boolean)},
     * {@link #setIgnoreCheekPress()}, {@link #setInputMethodMode(int)},
     * {@link #setTouchable(boolean)}, and {@link #setAnimationStyle(int)}.
     * @hide
     */
    public void update() {
        if (!isShowing() || mContentView == null) {
            return;
        }

        WindowManager.LayoutParams p = (WindowManager.LayoutParams) mPopupView
                .getLayoutParams();

        boolean update = false;

        final int newAnim = computeAnimationResource(dropdownLayout, triangleLayout);
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
            CheckUtil.safeUpdateViewLayout(mPopupView, p, mWindowManager);
        }
    }

    /**
     * <p>
     * Updates the dimension of the popup window. Calling this function also
     * updates the window with the current popup state as described for
     * {@link #update()}.
     * </p>
     *
     * @param width
     *            the new width
     * @param height
     *            the new height
     *
     * @hide
     */
    public void update(int width, int height) {
        update(dropdownLayout.x, dropdownLayout.y, width, height, false);
    }

    /**
     * <p>
     * Updates the position and the dimension of the popup window. Width and
     * height can be set to -1 to update location only. Calling this function
     * also updates the window with the current popup state as described for
     * {@link #update()}.
     * </p>
     *
     *
     * @param x
     *            the new x location
     * @param y
     *            the new y location
     * @param width
     *            the new width, can be -1 to ignore
     * @param height
     *            the new height, can be -1 to ignore
     * @deprecated [Not use any longer]
     */
    /**@hide*/
    public void update(int x, int y, int width, int height) {
        update(x, y, width, height, false);
    }

    /**
     * <p>
     * Updates the position and the dimension of the popup window. Width and
     * height can be set to -1 to update location only. Calling this function
     * also updates the window with the current popup state as described for
     * {@link #update()}.
     * </p>
     *
     * @param x
     *            the new x location
     * @param y
     *            the new y location
     * @param width
     *            the new width, can be -1 to ignore
     * @param height
     *            the new height, can be -1 to ignore
     * @param force
     *            reposition the window even if the specified position already
     *            seems to correspond to the LayoutParams
     * @deprecated [Not use any longer]
     */
    /**@hide*/
    public void update(int x, int y, int width, int height, boolean force) {
        if (width != -1) {
            width = checkWidthLimit(width);
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

        WindowManager.LayoutParams p = dropdownLayout;

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

        final int newAnim = computeAnimationResource(p, triangleLayout);
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
            CheckUtil.safeUpdateViewLayout(mPopupView, fixDrawingPosition(p, triangleLayout), mWindowManager);
        }
    }

    /**
     * Set the horizontal offset of arrow.
     * @param offset the x offset of arrow
     * */
    public void setTriangleOffset(int offset) {
        mCustomizeTriangleOffset = offset;
    }

    int getTriangleOffset() {
        return mCustomizeTriangleOffset;
    }

    /**
     * <p>
     * Updates the position and the dimension of the popup window. Calling this
     * function also updates the window with the current popup state as
     * described for {@link #update()}.
     * </p>
     *
     * @param anchor
     *            the popup's anchor view
     * @param width
     *            the new width, can be -1 to ignore
     * @param height
     *            the new height, can be -1 to ignore
     * @deprecated [Not use any longer]
     */
    /**@hide*/
    public void update(View anchor, int width, int height) {
        update(anchor, false, 0, 0, true, width, height);
    }

    /**
     * <p>
     * Updates the position and the dimension of the popup window. Width and
     * height can be set to -1 to update location only. Calling this function
     * also updates the window with the current popup state as described for
     * {@link #update()}.
     * </p>
     *
     * <p>
     * If the view later scrolls to move <code>anchor</code> to a different
     * location, the popup will be moved correspondingly.
     * </p>
     *
     * @param anchor
     *            the popup's anchor view
     * @param xoff
     *            x offset from the view's left edge
     * @param yoff
     *            y offset from the view's bottom edge
     * @param width
     *            the new width, can be -1 to ignore
     * @param height
     *            the new height, can be -1 to ignore
     * @deprecated [Not use any longer]
     * @hide
     */
    public void update(View anchor, int xoff, int yoff, int width, int height) {
        update(anchor, true, xoff, yoff, true, width, height);
    }

    private void update(View anchor, boolean updateLocation, int xoff,
            int yoff, boolean updateDimension, int width, int height) {
        if (!isShowing() || mContentView == null) {
            return;
        }
        //width = checkWidthLimit(width);

        WeakReference<View> oldAnchor = mAnchor;
        final boolean needsUpdate = updateLocation
                && (mAnchorXoff != xoff || mAnchorYoff != yoff);
        if (oldAnchor == null || oldAnchor.get() != anchor
                || (needsUpdate && !mIsDropdown)) {
            registerForScrollChanged(anchor, xoff, yoff);
        } else if (needsUpdate) {
            // No need to register again if this is a DropDown, showAsDropDown
            // already did.
            mAnchorXoff = xoff;
            mAnchorYoff = yoff;
        }

        WindowManager.LayoutParams p = dropdownLayout;

        int oldWidth = p.width;
        int oldHeight = p.height;

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
            p.width = width;
            p.height = height;
            if (p.width!=oldWidth){
                resetHeight(p);
            }
        }

        int x = p.x;
        int y = p.y;

        boolean update = false;

        findDropDownPosition(anchor, p, mAnchorXoff, mAnchorYoff);

        WindowManager.LayoutParams p2 = triangleLayout;
        int triangleX = p2.x;
        int triangleY = p2.y;
        if (mExpandDirection == PopupBubbleWindow.EXPAND_DEFAULT
                || mExpandDirection == PopupBubbleWindow.EXPAND_UP
                || mExpandDirection == PopupBubbleWindow.EXPAND_DOWN) {
            p2.width = mTriangle.getIntrinsicWidth();
            p2.height = mTriangle.getIntrinsicHeight();
        } else {
            if(mExpandRightTriangledDrawable != null)
                p2.width = mExpandRightTriangledDrawable.getIntrinsicWidth();
            if(mExpandLeftTriangledDrawable != null)
                p2.height = mExpandLeftTriangledDrawable.getIntrinsicHeight();
        }
        findTrianglePosition(anchor, p2, mAnchorXoff, mAnchorYoff);

        if (p.x != x || p.y != y || oldWidth != p.width || oldHeight != p.height || p2.x != triangleX || p2.y != triangleY) {
            update = true;
        }

        final int newAnim = computeAnimationResource(p, p2);
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
            CheckUtil.safeUpdateViewLayout(mPopupView, fixDrawingPosition(p, p2), mWindowManager);
            setTouchDelegate();
        }

    }

    /**
     * Listener that is called when this popup window is dismissed.
     */
    public interface OnDismissListener extends android.widget.PopupWindow.OnDismissListener {
    }

    /**
     * Listener that is called when this popup window is dismissed.
     */
    public interface OnUserDismissListener extends android.widget.PopupWindow.OnDismissListener {
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
    private void unregisterForGlobalChanged() {
        WeakReference<View> anchorRef = mParent;
        View anchor = null;
        if (anchorRef != null) {
            anchor = anchorRef.get();
        }
        if (anchor != null) {
            ViewTreeObserver vto = anchor.getViewTreeObserver();
            vto.removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
        }
        mParent = null;
    }

    private void registerForGlobalChanged(View anchor) {
        unregisterForGlobalChanged();
        mParent = new WeakReference<View>(anchor);
        ViewTreeObserver vto = anchor.getViewTreeObserver();
        if (vto != null) {
            vto.addOnGlobalLayoutListener(mOnGlobalLayoutListener);
        }
    }

    /**@hide*/
    protected void setTouchDelegate(){

    }

    /**@hide*/
    protected void releaseTouchDelegate(){

    }

    /**@hide*/
    private class PopupBubbleViewContainer extends android.widget.LinearLayout {
        // setup the center container view group
        private FrameLayout containerView = null;

        private ImageView firstView = null;
        private ImageView lastView = null;

        private int previousDirection = Integer.MIN_VALUE;

        // /////////////////////////////////////////////////////////////////////////////////////////

        public PopupBubbleViewContainer(Context context) {
            super(context);
            setChildrenDrawingOrderEnabled(true);

            int parameter = LayoutParams.WRAP_CONTENT;

            // setup each child view and center container environment
            firstView = new ImageView(context);
            firstView.setVisibility(View.GONE);
            firstView.setLayoutParams(new LayoutParams(parameter, parameter));

            lastView = new ImageView(context);
            lastView.setVisibility(View.GONE);
            lastView.setLayoutParams(new LayoutParams(parameter, parameter));

            // default center view should match parent
            parameter = LayoutParams.MATCH_PARENT;

            containerView = new FrameLayout(context);
            containerView.setVisibility(View.GONE);
            containerView
                    .setLayoutParams(new LayoutParams(parameter, parameter));
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                this.setLayoutDirection(LAYOUT_DIRECTION_LTR);
            }
            // add all the child view to the parent
            addView(firstView);
            addView(containerView);
            addView(lastView);
        }

        /**
         *  @hide
         */
        @Override
        public void addView(View child, ViewGroup.LayoutParams params) {
            // force all operation for container only
            if (containerView != null) {
                // if child view has parent view, we should remove child view frome its' parnet,
                // or it will cause crash.
                if (child.getParent() != null) {
                    ((ViewGroup) child.getParent()).removeView(child);
                }
                containerView.addView(child, params);
            }
        }

        /**
         *  @hide
         */
        @Override
        public void removeView(View view) {
            // remove view from containerView
            if (containerView != null) {
                containerView.removeView(view);
            }
        }

        /**
         *  @hide
         */
        @Override
        public void setBackgroundDrawable(Drawable drawable) {
            // force all operation for container only
            if (containerView != null) {
                containerView.setBackground(drawable);
            }
        }

        /**
         *  @hide
         */
        @Override
        public Drawable getBackground() {
            if (containerView != null) {
                return containerView.getBackground();
            }
            return null;
        }

        // setup the environment based on the orientation
        private void updateEnvironment() {
            // skip useless operation to save resource
            if (mExpandDirection == previousDirection)
                return;

            containerView.setVisibility(View.GONE);
            switch (mExpandDirection) {
            case EXPAND_DOWN:
                lastView.setVisibility(View.GONE);
                firstView.setVisibility(View.VISIBLE);
                firstView.setBackground(mTriangle);

                if (getOrientation() != VERTICAL)
                    setOrientation(VERTICAL);
                break;

            case EXPAND_UP:
                firstView.setVisibility(View.GONE);
                lastView.setVisibility(View.VISIBLE);
                lastView.setBackground(mBelowTriangledDrawable);

                if (getOrientation() != VERTICAL)
                    setOrientation(VERTICAL);
                break;

            case EXPAND_RIGHT:
                lastView.setVisibility(View.GONE);
                firstView.setVisibility(View.VISIBLE);
                firstView.setBackground(mExpandRightTriangledDrawable);

                if (getOrientation() != HORIZONTAL)
                    setOrientation(HORIZONTAL);
                break;

            case EXPAND_LEFT:
                firstView.setVisibility(View.GONE);
                lastView.setVisibility(View.VISIBLE);
                lastView.setBackground(mExpandLeftTriangledDrawable);

                if (getOrientation() != HORIZONTAL)
                    setOrientation(HORIZONTAL);
                break;

            }

            // setup the container view layout environment
            LayoutParams lparams = (LayoutParams) containerView
                    .getLayoutParams();

            lparams.weight = 1;
            lparams.width = (getOrientation() == VERTICAL) ? LayoutParams.MATCH_PARENT
                    : 0;
            lparams.height = (getOrientation() == VERTICAL) ? 0

                    : LayoutParams.MATCH_PARENT;

            containerView.setLayoutParams(lparams);
            containerView.setVisibility(View.VISIBLE);

            // record current expand direction
            previousDirection = mExpandDirection;
        }

        private void updateEnvironmentNoArrow() {

            // setup the container view layout environment
            LayoutParams lparams = (LayoutParams) containerView
                    .getLayoutParams();

            lparams.weight = 1;
            lparams.width = (getOrientation() == VERTICAL) ? LayoutParams.MATCH_PARENT
                    : 0;
            lparams.height = (getOrientation() == VERTICAL) ? 0
                    : LayoutParams.MATCH_PARENT;

            containerView.setLayoutParams(lparams);
            containerView.setVisibility(View.VISIBLE);

        }

        // setup the triangle indicator offset position
        private void updateIndicatorOffset(int xoffset, int yoffset) {
            LinearLayout.LayoutParams lparams;


            switch (mExpandDirection) {
            case EXPAND_DOWN:
                lparams = (LayoutParams) firstView.getLayoutParams();
                lparams.leftMargin = xoffset;
                lparams.topMargin = yoffset;
                lparams.bottomMargin = -yoffset;
                firstView.setLayoutParams(lparams);

                break;

            case EXPAND_UP:
                lparams = (LayoutParams) lastView.getLayoutParams();
                lparams.leftMargin = xoffset;
                lparams.bottomMargin = yoffset;
                lparams.topMargin = -yoffset;
                lastView.setLayoutParams(lparams);

                break;

            case EXPAND_RIGHT:
                lparams = (LayoutParams) firstView.getLayoutParams();
                lparams.topMargin = yoffset;
                lparams.leftMargin = xoffset;
                lparams.rightMargin = -xoffset;
                firstView.setLayoutParams(lparams);

                break;

            case EXPAND_LEFT:
                lparams = (LayoutParams) lastView.getLayoutParams();
                lparams.topMargin = yoffset;
                lparams.rightMargin = xoffset;
                lparams.leftMargin = -xoffset;
                lastView.setLayoutParams(lparams);

                break;
            }
        }

        /**
         *  @hide
         */
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            /*final int x = (int) event.getX();
            final int y = (int) event.getY();

            if(mIsCloseVisible && (event.getAction() == MotionEvent.ACTION_UP)
                    && containerView!=null && containerView.getChildAt(0)!=null){

                boolean hasImage = (mImageSrc != null);
                int padding = hasImage ? M1 + M5 : M2;

                int left = containerView.getChildAt(0).getRight() - padding - mClearIconWidth - mIncreasedTouchSize;
                int right = containerView.getChildAt(0).getRight() - padding + mIncreasedTouchSize;
                int top = containerView.getChildAt(0).getTop() + padding - mIncreasedTouchSize;
                int bottom = containerView.getChildAt(0).getTop() + padding + mClearIconWidth + mIncreasedTouchSize;

                if(x > left && x < right && y > top && y < bottom){
                    if (mOnUserDismissListener != null) {
                        android.util.Log.i(TAG, "call onUserDismissListener");
                        mOnUserDismissListener.onDismiss();
                    }
                    dismiss();
                    return true;
                }

            }*/

            return super.onTouchEvent(event);
        }

        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**@hide*/
        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            if ((event.getKeyCode() == KeyEvent.KEYCODE_BACK
                    || event.getKeyCode() == KeyEvent.KEYCODE_MENU)) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getRepeatCount() == 0) {
                    KeyEvent.DispatcherState state = getKeyDispatcherState();
                    if (state != null) {
                        state.startTracking(event, this);
                    }
                    return true;
                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    KeyEvent.DispatcherState state = getKeyDispatcherState();
                    if (state != null && state.isTracking(event)
                            && !event.isCanceled()) {
                        dismiss();
                        return true;
                    }
                }
                return super.dispatchKeyEvent(event);
            } else {
                return super.dispatchKeyEvent(event);
            }
        }

        /**
         *  @hide
         */
        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (mTouchInterceptor != null
                    && mTouchInterceptor.onTouch(this, ev)) {
                return true;
            }
            return super.dispatchTouchEvent(ev);
        }

        /**
         *  @hide
         */
        @Override
        public void sendAccessibilityEvent(int eventType) {
            // clinets are interested in the content not the container, make it
            // event source
            if (mContentView != null) {
                mContentView.sendAccessibilityEvent(eventType);
            } else {
                super.sendAccessibilityEvent(eventType);
            }
        }

        /**
         *  @hide
         */
        @Override
        protected int getChildDrawingOrder(int childCount, int i) {
            // prevent APP add custom view to window
            if (childCount > 3)
                throw new RuntimeException("getChildDrawingOrder():"
                        + childCount);

            // reset the drawing order to make sure container view is first draw
            // this is special design since triangle overlap with container
            if (i == 0)
                return 1;
            if (i == 1)
                return 0;

            return i;
        }
    }

    void updatePositionByAnchor(View anchor) {
        if (anchor != null && mPopupView != null && dropdownLayout != null
                && triangleLayout != null) {
            findDropDownPosition(anchor, dropdownLayout, mAnchorXoff, mAnchorYoff);
            findTrianglePosition(anchor, triangleLayout, mAnchorXoff, mAnchorYoff);
            update(dropdownLayout.x, dropdownLayout.y, -1, -1);
        }
    }
}
