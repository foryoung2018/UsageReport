package com.htc.lib1.cc.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.util.WindowUtil;
import com.htc.lib1.cc.view.table.AbstractTableView;
import com.htc.lib1.cc.view.table.AbstractTableView.OnScrollListener;
import com.htc.lib1.cc.view.table.TableLayoutParams;
import com.htc.lib1.cc.view.table.TableView;

/**
 * HtcNumberPicker
 */
public class HtcNumberPicker extends RelativeLayout implements OnScrollListener {

    /**
     * A Listener to listen if the scroll state is idle.
     */
    public interface OnScrollIdleStateListener {
        /**
         * callback to provide CenterView Id when scroll state turn to idle
         * @param target target NumberTableView
         * @param data relative centerView data
         */
        public void onDataSet(HtcNumberPicker target, int data);
    }

    private final String LOG_TAG  = "NumberTableView";
    private final boolean ahanLog = false;

    private LayoutInflater mTableInflater;

    private TableAdapter mAdapter;
    private TableLayoutParams mLayoutParams;
    private TableViewScrollControl mScrollControl = null;
    private MyTableView mTableView;

    @ExportedProperty(category = "CommonControl")
    private int mCurrent;

    @ExportedProperty(category = "CommonControl")
    private int mDigits;
    private String AM;
    private String PM;

    @ExportedProperty(category = "CommonControl")
    private boolean isRightIdle = false ;

    private OnScrollIdleStateListener mIdleScrollListener = null ;
    private boolean mChangeBkg = false;

    @ExportedProperty(category = "CommonControl")
    private int mPickerHeight;

    @ExportedProperty(category = "CommonControl")
    private int mTableHeight;
    private int mTableCenter;    //used by haptic to locate center position of TableView.
    private int mAmPmTextSize;

    @ExportedProperty(category = "CommonControl")
    private int mCenterViewTextColor;
    private int mOldCenterViewTextColor;

    private Context mHtcContext;

    @ExportedProperty(category = "CommonControl")
    private String mKeyOfPicker = null; // Used to identify the uasge of this picker.

    private int mAssetHeight = 0;

    @ExportedProperty(category = "CommonControl")
    private int mPickerHeightPortrait = Integer.MIN_VALUE;

    @ExportedProperty(category = "CommonControl")
    private int mPickerHeightLandscape = Integer.MIN_VALUE;

    private int mTableHeightInXML = Integer.MIN_VALUE;

    @ExportedProperty(category = "CommonControl")
    private int mTableChildHeightInXML = Integer.MIN_VALUE;
    private int mTableViewSlideOffsetInXML = Integer.MIN_VALUE;

    @ExportedProperty(category = "CommonControl")
    private int mBackgroundBorderWeight = Integer.MIN_VALUE;

    @ExportedProperty(category = "CommonControl")
    private int mBackgroundBorderColor = Integer.MIN_VALUE;

    private Drawable mFocusIndicator;

    @ExportedProperty(category = "CommonControl")
    private boolean mDrawFocusIndicator;

    private Paint mBackgroundPaint;
    private AnimatorSet mCenterViewFadingTextColorSet = null;
    private ObjectAnimator mCenterViewFadingTextColor;

    private boolean mHasAnimatorInited = false;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcNumberPicker(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     *
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     */
    public HtcNumberPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of View allows subclasses to use their own base style when
     * they are inflating. For example, a Button class's constructor would call
     * this version of the super class constructor and supply
     * <code>R.attr.buttonStyle</code> for <var>defStyle</var>; this allows
     * the theme's button style to modify all of the base view attributes (in
     * particular its background) as well as the Button class's attributes.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view. If 0, no style
     *        will be applied (beyond what is included in the theme). This may
     *        either be an attribute resource, whose value will be retrieved
     *        from the current theme, or an explicit style resource.
     * @see #View(Context, AttributeSet)
     */
    public HtcNumberPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        Resources res = context.getResources();

        mPickerHeightPortrait = res.getDimensionPixelSize(R.dimen.time_pick_picker_height_portrait);
        mPickerHeightLandscape = res.getDimensionPixelSize(R.dimen.time_pick_picker_height_landscape);
        mTableHeight = mTableHeightInXML = res.getDimensionPixelSize(R.dimen.table_view_height);
        mTableChildHeightInXML = res.getDimensionPixelSize(R.dimen.time_pick_text_view_height);
        mTableViewSlideOffsetInXML = res.getDimensionPixelSize(R.dimen.table_view_slide_offest);
        mBackgroundBorderWeight = res.getDimensionPixelSize(R.dimen.time_pick_border_weight);

        //+ [ahan_wu] Use HtcThemeUtils to fit theme change.
        mTextColor = mBackgroundBorderColor = getTimePickerColorResources(R.styleable.ThemeColor_dark_secondaryfont_color);
        mCenterViewTextColor = getTimePickerColorResources(R.styleable.ThemeColor_category_color);
        //- [ahan_wu] Use HtcThemeUtils to fit theme change.

        mTableCenter = mTableHeight / 2;

        mTableInflater = LayoutInflater.from(context);
        mTableInflater.inflate(R.layout.number_picker, this, true);

        mTableView = (MyTableView) findViewById(R.id.my_table_view);

        TableLayoutParams mLayoutParams;
        mLayoutParams = new TableLayoutParams();
        mLayoutParams.enableScrollOverBoundary(true);
        mLayoutParams.initialWithScrollControl(true);
        mLayoutParams.setOrientation(TableLayoutParams.VERTICAL);
        mTableView.setTableLayoutParams(0, mLayoutParams);

        mTableView.setNumColumnRows(1);
        mTableView.setHorizontalSpacing(5);
        mTableView.setVerticalSpacing(0);
        mTableView.setHorizontalSpacing(5);
        mTableView.setVerticalSpacing(0);
        mTableView.setTableEnabled(true);

        TableViewScrollControl mScrollControl = null;
        mScrollControl = new TableViewScrollControl();
        mScrollControl.setOrientation(TableLayoutParams.VERTICAL);
        mScrollControl.setTableView(mTableView);
        mTableView.setScrollControl(mScrollControl);

        mTableView.setSelector(android.R.color.transparent);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(mBackgroundBorderColor);

        initAllAboutFocus(context);
        initObjectAnimator();

        // set scroll listener
        if (null != mTableView) {
            mTableView.setOnScrollListener(this);
            mTableView.setCenterViewListener(new TableView.CenterViewSetListener() {
                @Override
                public void onCenterViewSet(ViewGroup vg, View view) {
                    if (vg == null) return;
                    for (int i=0; i<vg.getChildCount(); i++) {
                        HtcDateTimeText tmp = (HtcDateTimeText)vg.getChildAt(i);
                        if (tmp != null) tmp.setTextColor(mTextColor);
                    }
                    HtcDateTimeText textView = (HtcDateTimeText)view;
                    if (textView != null) {
                        textView.setTextColor(mCenterViewTextColor);
                    }
                }
            });
        }

        mHasAnimatorInited = true;
    }

    private int getTimePickerColorResources(int type) {
        //+ [ahan_wu] Use HtcThemeUtils to fit theme change.
        return HtcCommonUtil.getCommonThemeColor(getContext(), type);
        //- [ahan_wu] Use HtcThemeUtils to fit theme change.
    }

    private void initAllAboutFocus(Context context) {
        mFocusIndicator = context.getResources().getDrawable(R.drawable.common_focused);
        if (mFocusIndicator != null) {
            mFocusIndicator.mutate();
            mFocusIndicator.setColorFilter(HtcButtonUtil.getOverlayColor(context, null), PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void initObjectAnimator() {
        mCenterViewFadingTextColor = ObjectAnimator.ofInt(this, "centerFadingColor", new int[] {0, 255});
        mCenterViewFadingTextColor.setDuration(250);
        mCenterViewFadingTextColorSet = new AnimatorSet();
        AnimatorSet localAnimatorSet = mCenterViewFadingTextColorSet;
        Animator[] arrayOfAnimator = new Animator[1];
        arrayOfAnimator[0] = mCenterViewFadingTextColor;
        localAnimatorSet.playTogether(arrayOfAnimator);
    }

    private void setCenterFadingColor(int fadingAlpha) {
        HtcDateTimeText dateTimeText = (HtcDateTimeText)findCenterView();
        if (dateTimeText == null) return;
        dateTimeText.setTextColor(Color.argb(fadingAlpha, 0xFF & mCenterViewTextColor >> 16, 0xFF & mCenterViewTextColor >> 8, 0xFF & mCenterViewTextColor));
    }

    /**@hide*/
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mHasAnimatorInited && mCenterViewFadingTextColorSet==null) {
            initObjectAnimator();
        }
    }

    /**@hide*/
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCenterViewFadingTextColorSet != null) {
            if (mCenterViewFadingTextColorSet.isRunning()) mCenterViewFadingTextColorSet.cancel();
            mCenterViewFadingTextColorSet = null;
        }
    }

    /** @hide */
    protected void dispatchDraw (Canvas canvas) {
        super.dispatchDraw(canvas);

        Rect rect = canvas.getClipBounds();
        canvas.drawRect(rect.left, rect.top, rect.right, rect.top+mBackgroundBorderWeight, mBackgroundPaint);
        canvas.drawRect(rect.left, rect.bottom-mBackgroundBorderWeight, rect.right, rect.bottom, mBackgroundPaint);

        if (mDrawFocusIndicator && mFocusIndicator!=null) {
            mFocusIndicator.setBounds(canvas.getClipBounds());
            mFocusIndicator.draw(canvas);
        }
    }

    /** @hide */
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        boolean ret = false, handledByMe = false;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                mTableView.slideWithOffset(getTableViewSlideOffset());
                handledByMe = true;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mTableView.slideWithOffset(-getTableViewSlideOffset());
                handledByMe = true;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (getParent()!=null && mUsingTwoLayerFocus && checkIfLeftOrRightMost(true)) {
                    returnFocusToParent();
                    handledByMe = true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (getParent()!=null && mUsingTwoLayerFocus && checkIfLeftOrRightMost(false)) {
                    returnFocusToParent();
                    handledByMe = true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (mMyParent!=null && mUsingTwoLayerFocus && mMyParent.isFocusable()) {
                    returnFocusToParent();
                    handledByMe = true;
                }
                break;
        }
        ret = super.onKeyDown(keyCode, event);
        return (handledByMe ? handledByMe : ret);
    }

    /** @hide */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean ret = super.onKeyUp(keyCode, event);
        return ret;
    }

    private ViewGroup mMyParent;

    @ExportedProperty(category = "CommonControl")
    private boolean mUsingTwoLayerFocus;

    /** @hide */
    public void usingTwoLayerFocus(boolean twoLayer, ViewGroup myParent) {
        if ((myParent instanceof HtcDatePicker) || (myParent instanceof HtcTimePicker)) {
            mUsingTwoLayerFocus = twoLayer;
            mMyParent = myParent;
        }
    }

    private boolean checkIfLeftOrRightMost(boolean findLeftMost) {
        boolean isLeftOrRightMost = false, isDatePickerInst, isTimePickerInst;

        if (mMyParent == null)
            return false;

        isDatePickerInst = (mMyParent instanceof HtcDatePicker);
        isTimePickerInst = (mMyParent instanceof HtcTimePicker);

        if (!isDatePickerInst && !isTimePickerInst)
            return false;

        if (findLeftMost) {
            isLeftOrRightMost = isDatePickerInst ? ((HtcDatePicker)mMyParent).isTheMostLeftPicker(this) : ((HtcTimePicker)mMyParent).isTheMostLeftPicker(this);
        } else {
            isLeftOrRightMost = isDatePickerInst ? ((HtcDatePicker)mMyParent).isTheMostRightPicker(this) : ((HtcTimePicker)mMyParent).isTheMostRightPicker(this);
        }

        return isLeftOrRightMost;
    }

    private void returnFocusToParent() {
        if (mMyParent != null) {
            mMyParent.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            mMyParent.requestFocus();
        }
    }

    // To set the related key of this picker.
    void setKeyOfPicker(String key) {
        mKeyOfPicker = key;
        if (mTableView != null) mTableView.setKeyOfMyTableView(key);
    }

    // To get the key of this picker.
    String getKeyOfPicker() {
        return mKeyOfPicker;
    }

    private void setNumberPickerBackground(Context context, AttributeSet attrs, int defStyle) {
        Resources res;
        TypedArray ta;
        LayerDrawable ld;
        Drawable tumblerDrawable;
        ViewGroup tumbler;

        if (context == null) return;

        tumbler = (ViewGroup) findViewById(R.id.tumblers);
        res = context.getResources();
        ta = context.obtainStyledAttributes(attrs, R.styleable.HtcDateTimePickerStyle, R.attr.dateTimePickerStyle, defStyle);
        ld = (LayerDrawable) ta.getDrawable(R.styleable.HtcDateTimePickerStyle_android_drawable);

        if (tumbler != null) {
            tumblerDrawable = (ld==null ? res.getDrawable(R.drawable.common_timer_tumblers) : ld.getDrawable(0));
            tumbler.setBackgroundDrawable(tumblerDrawable);
            mAssetHeight = tumblerDrawable.getIntrinsicHeight();
        }

        ta.recycle();
    }

    /**
     * To set the listener to listen the scroll state changed to idle.
     * @param listener The listener to be set to listen the idle scroll state.
     */
    public void setOnScrollIdleStateListener(OnScrollIdleStateListener listener) {
        if (null != listener) mIdleScrollListener = listener;
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     * @deprecated [Not use any longer] It takes no effects and will be removed, never use this method.
     * @hide
     */
    public void changeBkg() {
        mChangeBkg = false;
    }

    //Add by Ahan 2011/12/02 for AP WorldClock
    private int mTumblerId, mShadowId;

    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     */
    public void setBackground(int tumblerId, int shadowId) {
        Resources res = getContext().getResources();

        mTumblerId = tumblerId;
        mShadowId = shadowId;

        Drawable tumbler = res.getDrawable(mTumblerId);
        findViewById(R.id.tumblers).setBackground(tumbler);
        findViewById(R.id.shadow).setBackgroundResource(mShadowId);

        mAssetHeight = (tumbler!=null ? tumbler.getIntrinsicHeight() : 0);
    }

    private int mTextColor, mTextStyle;
    private boolean mChangeTextColor, mChangeTextStyle;

    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     */
    public void setTextColor(int textColor) {
        mTextColor = textColor;
        mChangeTextColor = true;
    }

    /**
     * To set the text style, just size, color and type face will take effects.
     * @param style The resource id of the font style.
     */
    public void setTextStyle(int style) {
        mTextStyle = style;
        mChangeTextStyle = true;
    }

    // Add by Ahan 2012/02/06 for AP WorldClock
    /**
     * Constant to represent the layer of shadow, the lower layer draws first.
     */
    public static final int SHADOW_LAYER_1ST = 0;

    /**
     * Constant to represent the layer of shadow, the lower layer draws first.
     */
    public static final int SHADOW_LAYER_2ND = 1;

    /**
     * Constant to represent the layer of shadow, the lower layer draws first.
     */
    public static final int SHADOW_LAYER_3RD = 2;

    @ExportedProperty(category = "CommonControl")
    private boolean mCustomShadow;

    private class NumberTextShadow {
        private float radius, dx, dy;
        private int shadowColor;

        public NumberTextShadow(float r, float x, float y, int c) {
            this.radius = r;
            this.dx = x;
            this.dy = y;
            this.shadowColor = c;
        }
    };
    private NumberTextShadow shadow1st, shadow2nd, shadow3rd;

    /**
     * To set the shadow style of the text.
     * @param layer The layer of this shadow, the lower the layer draws first.
     * @param radius Radius of the shadow. Must be a floating point value, such as "1.2".
     * @param dx Horizontal offset of the shadow. Must be a floating point value, such as "1.2".
     * @param dy Vertical offset of the shadow. Must be a floating point value, such as "1.2".
     * @param color Place a shadow of the specified color behind the text. Must be a color value, in the form of "#rgb", "#argb", "#rrggbb", or "#aarrggbb".
     */
    public void setCustomShadow(int layer, float radius, float dx, float dy, int color) {
        if (layer == SHADOW_LAYER_1ST) shadow1st = new NumberTextShadow(radius, dx, dy, color);
        else if (layer == SHADOW_LAYER_2ND) shadow2nd = new NumberTextShadow(radius, dx, dy, color);
        else if (layer == SHADOW_LAYER_3RD) shadow3rd = new NumberTextShadow(radius, dx, dy, color);
        else return;
        mCustomShadow = true;
    }

    /**
     * To set if it is in the countdown mode, this API should be called just only by AP WorldClock.
     * @param enableCountDownMode true to enable, false to disable.
     */
    public void setCountDownMode(boolean enableCountDownMode) {
        mTableView.setCountDownMode(enableCountDownMode);
    }

    private Runnable mRunnable = new Runnable() {
        public void run() {
            mReadyToSet = true;
            setCenterView(mPos);
        }
    };

    boolean mReadyToSet = false;
    private int mPos;

    /**
     * To set the data scope of the adapter related to this picker.
     * @param start The start value.
     * @param end The end value.
     */
    public void setRange(int start, int end) {
        if (null != mAdapter) mAdapter = null;
        mStart = start;
        mEnd = end;
        mAdapter = new TableAdapter(start, end);
        mTableView.setAdapter(mAdapter);
        //mTableView.setVisibility(View.VISIBLE);
        mReadyToSet = false;
    }

    /**
     * To set the data scope of the adapter related to this picker.
     * @param start The start value.
     * @param end The end value.
     * @param candidates The custom strings.
     */
    public void setRange(int start, int end, String [] candidates) {
        if (null != mAdapter) mAdapter = null;
        mStart = start;
        mEnd = end;
        mAdapter = new TableAdapter(start, end, candidates);
        if(mTableView!=null) mTableView.setAdapter(mAdapter);
        //mTableView.setVisibility(View.VISIBLE);
        mReadyToSet = false;
    }

    /**
     * To set the max digits each date/time text view can draw.
     * @param d The max digits
     */
    public void setShowNumberDigits(int d){
        mDigits = d;
    }

    /**
     * To get the value of the center view.
     * @return The data of the center view of this picker.
     */
    public int getCenterView() {
        if (mTableView!=null)
            return (mAdapter.getCount() - mTableView.getCenterChildPosition() + mAdapter.getStart() - 1);
        else return 0;
    }

    //[Ahan][2012/10/05][For S50]
    /**
     * Called to determine the size requirements for this view and all of its children.
     * @param widthMeasureSpec horizontal space requirements as imposed by the parent. The requirements are encoded with View.MeasureSpec.
     * @param heightMeasureSpec vertical space requirements as imposed by the parent. The requirements are encoded with View.MeasureSpec.
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);

        measurePickerHeight(getResources().getConfiguration().orientation);
        setMeasuredDimension(measuredWidth, mPickerHeight);
        super.onMeasure(MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mPickerHeight, MeasureSpec.EXACTLY));
    }

    private void measurePickerHeight(int orientation) {
        // Need assign different values to table_view_height, table_view_slide_offset, time_pick_picker_height and time_pick_text_view_height between different mode
        if (!WindowUtil.isSuitableForLandscape(getResources())) {
            mPickerHeight = mPickerHeightPortrait; // Use the height set in xml as pickers' height in portrait mode
            mTableHeight = mPickerHeight; // Use the value set in xml as tables' height
            mTableView.setMyTableChildHeight(mTableChildHeightInXML); // Use the value set in xml as tableChildHeight
            mTableView.setMyTableViewSlideOffset(mTableChildHeightInXML*70/100); // Use the value set in xml as slideOffset
        } else if (WindowUtil.isSuitableForLandscape(getResources())) {
            mPickerHeight = mPickerHeightLandscape; // Use assets' height as pickers' height in landscape mode
            mTableHeight = mPickerHeight; // Let pickers' height as tables' height
            int tableChildHeight = (mTableHeight*10)/24; // (1+0.7*2) * tableChildHeight = mTableHeight
            mTableView.setMyTableChildHeight(tableChildHeight); // Set tableChildHeight
            mTableView.setMyTableViewSlideOffset(tableChildHeight*65/100); // Set slideOffset amount
        }
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        mDrawFocusIndicator = gainFocus;
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    //[Ahan][2012/10/05][For S50]
    private final Handler mHandler = new Handler();
    boolean mIsNeverSlidedBeforeSet = true;
    boolean mIsOnInitState = false;
    int mStart,mEnd;

    /**
     * To set the center view of this picker.
     * @param pos The value wish to be showed in the center of this picker.
     */
    public void setCenterView(int pos) {
        if (mAdapter == null) return;
        if (mReadyToSet) {
            if (!mIsNeverSlidedBeforeSet && mIsOnInitState) pos = mPos;
            mPos = pos;
            mCurrent = mAdapter.getCount() - pos + mAdapter.getStart() - 1;

            setCenter(mCurrent);

            mTableView.setVisibility(View.VISIBLE);
            mIsNeverSlidedBeforeSet = true;
            mIsOnInitState = false;
        } else {
            mPos = pos;
            mTableView.setVisibility(View.VISIBLE);
            mHandler.postDelayed(mRunnable, 30);
        }
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     */
    public void notifyOnInitState() {
        mIsOnInitState = true;
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     */
    public void setCenter(int pos) {
        if (mTableView!=null) {
            mTableView.setCenterView(pos, mTableHeight);
        }
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     */
    public void setTableEnabled(boolean enabled) {
        if(mTableView!=null) mTableView.setTableEnabled(enabled);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     */
    public TableView getTableView() {
        return mTableView;
    }

    /** @hide */
    public void slideWithOffset(int offset) {
        mTableView.slideWithOffset(offset);
        if (offset > 0) {
            if (mPos>mStart) mPos--;
            else mPos = mEnd;
        }
        mIsNeverSlidedBeforeSet = false;
        mTableView.setVisibility(View.VISIBLE);
    }

    int getMyTableChildHeight() {
        return (mTableView==null ? 0 : mTableView.getMyTableChildHeight());
    }

    /**
     * Callback method to be invoked while the list view or grid view is being scrolled. If the
     * view is being scrolled, this method will be called before the next frame of the scroll is
     * rendered. In particular, it will be called before any calls to
     * {@link Adapter#getView(int, View, ViewGroup)}.
     *
     * @param view The view whose scroll state is being reported
     * @param scrollState The current scroll state. One of SCROLL_STATE_IDLE, SCROLL_STATE_TOUCH_SCROLL or SCROLL_STATE_IDLE.
     * @hide
     */
    public void onScrollStateChanged(AbstractTableView view, int scrollState) {
        if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            //To set multistop start position
            mTableView.setStopExcept(getCenterView()-mAdapter.getStart());
        }

        if (mTableView == view) {
            if (OnScrollListener.SCROLL_STATE_TOUCH_SCROLL == scrollState) {
                isRightIdle = true;
                if ((mCenterViewFadingTextColorSet != null) && (mCenterViewFadingTextColorSet.isRunning())) {
                    mCenterViewFadingTextColorSet.cancel();
                }
                HtcDateTimeText dateTimeText = (HtcDateTimeText)findCenterView();
                if (dateTimeText != null) {
                    dateTimeText.setTextColor(mTextColor);
                }
            } else if (true == isRightIdle && OnScrollListener.SCROLL_STATE_IDLE == scrollState) {
                // move this flag init timing before the onDataSet() call back, to prevent multi entry call onDataSet() case
                // for hero issue 11375... Thanks the load..
                isRightIdle = false;
                int target = getCenterView();
                if (null != mIdleScrollListener) mIdleScrollListener.onDataSet(this, target);
                if (mCenterViewFadingTextColorSet != null) mCenterViewFadingTextColorSet.start();
            } else if (OnScrollListener.SCROLL_STATE_IDLE == scrollState) {
                isRightIdle = false;
            }
        }
    }

    private View findCenterView() {
        if (this.mTableView == null)
            return null;
        return this.mTableView.getCenterView();
    }

    /**
     * Callback method to be invoked when the list or grid has been scrolled, this will be called after the scroll has completed.
     * @param view The view whose scroll state is being reported
     * @param firstVisibleItem the index of the first visible cell (ignore if visibleItemCount == 0)
     * @param visibleItemCount the number of visible cells
     * @param totalItemCount the number of items in the list adaptor
     * @hide
     */
    public void onScroll(AbstractTableView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    //Add by Ahan 2012/01/10 for AP WorldClock.
    /**
     * It returns table_view_slide_offset value used by slideWithOffset().
     * @hide
     */
    public int getTableViewSlideOffset() {
        return mTableView.getTableViewSlideOffset();
    }

    class TableAdapter extends BaseAdapter {
        /**
         *  Hide Automatically by SDK Team [U12000]
         *  @hide
         */
         protected int mCount;
        /**
         *  Hide Automatically by SDK Team [U12000]
         *  @hide
         */
         protected int mStart;
        /**
         *  Hide Automatically by SDK Team [U12000]
         *  @hide
         */
         public int [] mData;
        /**
         *  Hide Automatically by SDK Team [U12000]
         *  @hide
         */
         protected LayoutInflater mInflater;
        /**
         *  Hide Automatically by SDK Team [U12000]
         *  @hide
         */
         protected ViewGroup mLayout;
        /**
         *  Hide Automatically by SDK Team [U12000]
         *  @hide
         */
         protected int mEnd;
         private String [] mCandidates = null;

        /**
         *  Hide Automatically by SDK Team [U12000]
         *  @hide
         */
        public TableAdapter(int start, int end) {
            mStart = start;
            mEnd = end;
            mCount = end - start + 1;
            mData = new int [mCount];
            for (int i=0; i<mCount; i++) {
                mData[i] = mCount - i - 1 + start;
            }
        }

        /**
         *  Hide Automatically by SDK Team [U12000]
         *  @hide
         */
        public TableAdapter(int start, int end, String[] candidates) {
            this(start,end);
            mCandidates = new String[mCount];
            for (int i = 0; i < mCount; i++) {
                mCandidates[i] = candidates[i];
            }
        }

        /**
         *  Hide Automatically by SDK Team [U12000]
         *  @hide
         */
        public int getCount() {
            return mCount;
        }

        /**
         *  Hide Automatically by SDK Team [U12000]
         *  @hide
         */
        public int getStart() {
            return mStart;
        }

        /**
         *  Hide Automatically by SDK Team [U12000]
         *  @hide
         */
        public int getEnd() {
            return mEnd;
        }

        /**
         *  Hide Automatically by SDK Team [U12000]
         *  @hide
         */
        public Object getItem(int position) {
            return position;
        }

        /**
         *  Hide Automatically by SDK Team [U12000]
         *  @hide
         */
        public long getItemId(int position) {
            return position;
        }

        /**
         *  Hide Automatically by SDK Team [U12000]
         *  @hide
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            HtcDateTimeText dateTimeText = (convertView==null ? new HtcDateTimeText(getContext()) : (HtcDateTimeText)convertView);

            dateTimeText.setTableView(mTableView, getKeyOfPicker());

            //Set to custom shadow if necessary
            if (mCustomShadow) {
                //1st is the top-most layer
                if (shadow1st != null) dateTimeText.setCustomShadow(SHADOW_LAYER_1ST, shadow1st.radius, shadow1st.dx, shadow1st.dy, shadow1st.shadowColor);
                if (shadow2nd != null) dateTimeText.setCustomShadow(SHADOW_LAYER_2ND, shadow2nd.radius, shadow2nd.dx, shadow2nd.dy, shadow2nd.shadowColor);
                if (shadow3rd != null) dateTimeText.setCustomShadow(SHADOW_LAYER_3RD, shadow3rd.radius, shadow3rd.dx, shadow3rd.dy, shadow3rd.shadowColor);
            }

            String candidate = null;

            if (mCandidates != null) {
                candidate = mCandidates[position];
            } else if (mData != null) {
                String numberString = Integer.toString(mData[position]);
                candidate = (mData[position]<10 ? "0"+numberString : numberString);
                if (mDigits > 0) {
                    int len = candidate.length();
                    candidate = candidate.substring(len - mDigits, len);
                }
            }

            if (mChangeTextStyle) dateTimeText.setTextStyle(mTextStyle);
            //Set to custom text color if necessary, setTextColor is just used by WorldClock currently.
            /*if (mChangeTextColor)*/ dateTimeText.setTextColor(mTextColor);

            //Set the text this view will draw
            dateTimeText.setText(candidate);

            return dateTimeText;
        }

        /**
         *  Hide Automatically by SDK Team [U12000]
         *  @hide
         */
        public void onDestroy() {
            mData = null;
        }
    }

    /**
     * Never call this method.
     * @deprecated [module internal use] Never call this method.
     */
    /**@hide*/
    public void releaseResource() {
        if (mAdapter != null) {
            mAdapter.onDestroy();
            mAdapter = null;
        }
        mTableView = null;
    }

    /**
     * To make the numbers shows circularly.
     * @param b True for circularly, false for linearly.
     */
    public void setRepeatEnable(boolean b) {
        if(mTableView!=null) mTableView.setRepeatEnable(b);
    }

    /**
     * To set the interval of each scroll.
     * @param d The interval each scroll.
     * @return If the interval is set successfully.
     */
    public boolean setMultiStopDistance(int d) {
        return mTableView.setMultiStopDistance(d);
    }

    /**
     * To set the interval of each scroll.
     * @param d The points to force stop scrolling.
     * @return If the stop points are set successfully.
     */
    public boolean setMultiStopDistance(int[] d) {
        return mTableView.setMultiStopDistance(d);
    }
}
