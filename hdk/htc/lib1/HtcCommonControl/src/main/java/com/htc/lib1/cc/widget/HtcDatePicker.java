package com.htc.lib1.cc.widget;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.view.animation.Animation;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.util.Log;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.ViewParent;

import com.htc.lib1.cc.widget.HtcNumberPicker.OnScrollIdleStateListener;
import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.res.HtcResUtil;

import android.provider.Settings;

/**
 * HtcDatePicker
 */
public class HtcDatePicker extends RelativeLayout implements OnScrollIdleStateListener{
    //Add by Ahan 2011/12/02 for identifing picker type
    /**
     * Constant to represent the Year Picker.
     */
    public static final int PICKER_TYPE_YEAR = 0;
    /**
     * Constant to represent the Month Picker.
     */
    public static final int PICKER_TYPE_MONTH = 1;
    /**
     * Constant to represent the Day Picker.
     */
    public static final int PICKER_TYPE_DAY = 2;

    private static final int DEFAULT_START_YEAR = 1991;
    private static final int DEFAULT_END_YEAR = 2030;

    private static final boolean LOG = false;
    private static final String TAG = "HtcDatePicker";
    private AttributeSet mAttrs;

    /* UI Components */
    private final HtcNumberPicker mDayPicker;
    private final HtcNumberPicker mMonthPicker;
    private final HtcNumberPicker mYearPicker;
    private final HtcNumberPicker mDayPicker_31;
    private final HtcNumberPicker mDayPicker_29;
    private final HtcNumberPicker mDayPicker_28;

    private final TextView mLabelDay;
    private final TextView mLabelMonth;
    private final TextView mLabelYear;
    private final View mDayCoat;
    private final View mMonthCoat;
    private final View mYearCoat;
    private int mOldMax = 0;

    /**
     * How we notify users the date has changed.
     */
    private OnDateChangedListener mOnDateChangedListener;
    private OnScrollIdleStateListener mIdleScrollListener = null ;

    @ExportedProperty(category = "CommonControl")
    private int mDay;

    @ExportedProperty(category = "CommonControl")
    private int mMonth;

    @ExportedProperty(category = "CommonControl")
    private int mYear;

    @ExportedProperty(category = "CommonControl")
    private int mStartYear;

    @ExportedProperty(category = "CommonControl")
    private int mEndYear;

    @ExportedProperty(category = "CommonControl")
    private String mOrder;

    //modfied by Ahan 2012/01/31, use MarginLayoutParams to support margin.
    private ViewGroup.MarginLayoutParams mLparams[] = new ViewGroup.MarginLayoutParams[3];

    private Drawable mFocusIndicator;

    @ExportedProperty(category = "CommonControl")
    private boolean mDrawFocusIndicator;

    private HtcNumberPicker[] mPickers;

    /**
     * Use a literal 'd' instead.
     */
    static final char DATE = 'd';

    /**
     * Use a literal 'M' instead.
     */
    static final char MONTH = 'M';

    /**
     * Use a literal 'y' instead.
     */
    static final char YEAR = 'y';

    /**
     * The callback used to indicate the user changes the date.
     */
    public interface OnDateChangedListener {

        /**
         * Callback when date has been changed.
         * @param view The view associated with this listener.
         * @param year The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility with {@link java.util.Calendar}.
         * @param dayOfMonth The day of the month that was set.
         */
        void onDateChanged(HtcDatePicker view, int year, int monthOfYear, int dayOfMonth);
    }

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcDatePicker(Context context) {
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
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     */
    public HtcDatePicker(Context context, AttributeSet attrs) {
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
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view. If 0, no style
     *        will be applied (beyond what is included in the theme). This may
     *        either be an attribute resource, whose value will be retrieved
     *        from the current theme, or an explicit style resource.
     * @see #View(Context, AttributeSet)
     */
    public HtcDatePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mAttrs = attrs;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.timer_table2, this, true);

        mMonthPicker = (HtcNumberPicker) findViewById(R.id.month);

        mDayPicker = (HtcNumberPicker) findViewById(R.id.day);
        mDayPicker.setRange(1, 30);
        mOldMax = 30;

        mDayPicker_31 = (HtcNumberPicker) findViewById(R.id.day_31);
        mDayPicker_31.setRange(1, 31);
        mDayPicker_31.setVisibility(View.INVISIBLE);

        mDayPicker_29 = (HtcNumberPicker) findViewById(R.id.day_29);
        mDayPicker_29.setRange(1, 29);
        mDayPicker_29.setVisibility(View.INVISIBLE);

        mDayPicker_28 = (HtcNumberPicker) findViewById(R.id.day_28);
        mDayPicker_28.setRange(1, 28);
        mDayPicker_28.setVisibility(View.INVISIBLE);

        setDayMultiStop(true);

        mYearPicker = (HtcNumberPicker) findViewById(R.id.year);
        mMonthCoat = findViewById(R.id.month_coat);
        mDayCoat = findViewById(R.id.day_coat);
        mYearCoat = findViewById(R.id.year_coat);

        mLabelDay = (TextView) findViewById(R.id.day_label);
        mLabelMonth = (TextView) findViewById(R.id.month_label);
        mLabelYear = (TextView) findViewById(R.id.year_label);

        boolean needAllCaps = HtcResUtil.isInAllCapsLocale(context);
        if (mLabelDay != null) mLabelDay.setAllCaps(needAllCaps);
        if (mLabelMonth != null) mLabelMonth.setAllCaps(needAllCaps);
        if (mLabelYear != null) mLabelYear.setAllCaps(needAllCaps);
        //remove all haptic function by Ahan 2012/02/15

        setMonthRange(1, 12);
        mYearPicker.setShowNumberDigits(2);
        // attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DatePicker);

        int mStartYear = a.getInt(R.styleable.DatePicker_android_startYear, DEFAULT_START_YEAR);
        int mEndYear = a.getInt(R.styleable.DatePicker_android_endYear, DEFAULT_END_YEAR);
        setYearRange(mStartYear, mEndYear);

        a.recycle();
        // initialize to current date
        //Calendar cal = Calendar.getInstance();
        //init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getShortMonths();
        //Log.i("ferro", "months " + months[0]);
        reorderPickers(months);

        dispatchOnScrollIdleStateListener(this);

        //Debug key and Accessibilities.
        String keyYear, keyMonth, keyDay;

        keyYear = (mLabelYear==null ? "Year" : mLabelYear.getText().toString());
        keyMonth = (mLabelMonth==null ? "Month" : mLabelMonth.getText().toString());
        keyDay = (mLabelDay==null ? "Day" : mLabelDay.getText().toString());

        mYearPicker.setKeyOfPicker(keyYear);
        mMonthPicker.setKeyOfPicker(keyMonth);
        mDayPicker.setKeyOfPicker(keyDay);
        mDayPicker_31.setKeyOfPicker(keyDay+"31");
        mDayPicker_29.setKeyOfPicker(keyDay+"29");
        mDayPicker_28.setKeyOfPicker(keyDay+"28");

        initAllAboutFocus(context);

        mYearCoat.setFocusable(false);
        mMonthCoat.setFocusable(false);
        mDayCoat.setFocusable(false);

        mPickers = new HtcNumberPicker[] {mMonthPicker, mDayPicker, mDayPicker_31, mDayPicker_29, mDayPicker_28, mYearPicker};
        for (int i=0; i<mPickers.length; i++) {
            if (mPickers[i] != null) {
                mPickers[i].setFocusable(true);
                mPickers[i].usingTwoLayerFocus(true, this);
            }
        }

        setFocusable(true);
        setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }

    /**
     * Initialize the state.
     * @param year The initial year.
     * @param monthOfYear The initial month.
     * @param dayOfMonth The initial day of the month.
     * @param onDateChangedListener How user is notified date is changed by user, can be null.
     * @deprecated [module internal use] This API will change to package level in the future.
     */
    public void init(int year, int monthOfYear, int dayOfMonth,
            OnDateChangedListener onDateChangedListener) {
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
        mOnDateChangedListener = onDateChangedListener;
        updateTables();
    }

    //This API is used for onConfigurationChanged of HtcDatePciker/HtcDatePickerDialog, not for AP.
    /**
     * To do initPicker, this API should not be used by outside com.htc.widget
     * @param year The initial year.
     * @param monthOfYear The initial month.
     * @param dayOfMonth The initial day of the month.
     * @deprecated [module internal use] This API will change to package level in the future.
     */
    /**@hide*/
    public void initPicker(int year, int monthOfYear, int dayOfMonth){
          mDayPicker.setRange(1, 30);
        mOldMax = 30;
        mDayPicker_31.setRange(1, 31);
        mDayPicker_29.setRange(1, 29);
        mDayPicker_28.setRange(1, 28);


        setMonthRange(1, 12);
    if(mStartYear==0 || mEndYear ==0) {
            TypedArray a = getContext().obtainStyledAttributes(mAttrs, R.styleable.DatePicker);
            mStartYear = a.getInt(R.styleable.DatePicker_android_startYear, DEFAULT_START_YEAR);
            mEndYear = a.getInt(R.styleable.DatePicker_android_endYear, DEFAULT_END_YEAR);
        a.recycle();
    }
        setYearRange(mStartYear, mEndYear);


        init(year,monthOfYear,dayOfMonth,mOnDateChangedListener);
    }

    @ExportedProperty(category = "CommonControl")
    boolean isOnCreate = true;

    private void initAllAboutFocus(Context context) {
        mFocusIndicator = context.getResources().getDrawable(R.drawable.common_focused);
        if (mFocusIndicator != null) {
            mFocusIndicator.mutate();
            mFocusIndicator.setColorFilter(HtcButtonUtil.getOverlayColor(context, null), PorterDuff.Mode.SRC_ATOP);
        }
    }

    /** @hide */
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        Rect focusBounds = canvas.getClipBounds();
        focusBounds.left -= getPaddingLeft();
        focusBounds.top -= getPaddingTop();
        focusBounds.right -= getPaddingRight();
        focusBounds.bottom -= getPaddingBottom();

        if (mDrawFocusIndicator && mFocusIndicator!=null) {
            mFocusIndicator.setBounds(focusBounds);
            mFocusIndicator.draw(canvas);
        }
    }

    /** @hide */
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        mDrawFocusIndicator = gainFocus;
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    /** @hide */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                findChildShouldBeFocused();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void findChildShouldBeFocused() {
        if (mPickers != null) {
            HtcNumberPicker tmp = null;
            for (int i=0; i<mPickers.length; i++) {
                tmp = mPickers[i];
                if ((tmp!=null) && (tmp.getVisibility()==View.VISIBLE) && (tmp.isFocusable())) break;
                tmp = null;
            }

            if (tmp != null) {
                setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                tmp.requestFocus();
            }
        }
    }

    /** @hide */
    @ExportedProperty(category = "CommonControl")
    public boolean isTheMostLeftPicker(HtcNumberPicker target) {
        if (mPickers==null || target==null)
            return false;

        boolean isMostLeft = true;
        ViewParent parent = target.getParent();
        ViewGroup targetCoat = (parent instanceof ViewGroup ? (ViewGroup)parent : null);

        if (targetCoat != null) {
            for (int i=0; i<getChildCount(); i++) {
                View tmp = getChildAt(i);
                if (tmp!=null && tmp.getVisibility()==View.VISIBLE && tmp.getLeft()<targetCoat.getLeft()) {
                    isMostLeft = false;
                    break;
                }
            }
        } else {
            isMostLeft = false;
        }

        return isMostLeft;
    }

    /** @hide */
    @ExportedProperty(category = "CommonControl")
    public boolean isTheMostRightPicker(HtcNumberPicker target) {
        if (mPickers==null || target==null)
            return false;

        boolean isMostRight = true;
        ViewParent parent = target.getParent();
        ViewGroup targetCoat = (parent instanceof ViewGroup ? (ViewGroup)parent : null);

        if (targetCoat != null) {
            for (int i=0; i<getChildCount(); i++) {
                View tmp = getChildAt(i);
                if (tmp!=null && tmp.getVisibility()==View.VISIBLE && tmp.getRight()>targetCoat.getRight()) {
                    isMostRight = false;
                    break;
                }
            }
        } else {
            isMostRight = false;
        }

        return isMostRight;
    }

    /** @hide */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    /**
     * Called when the current configuration of the resources being used
     * by the application have changed.  You can use this to decide when
     * to reload resources that can changed based on orientation and other
     * configuration characterstics.  You only need to use this if you are
     * not relying on the normal {@link android.app.Activity} mechanism of
     * recreating the activity instance upon a configuration change.
     *
     * @param newConfig The new resource configuration.
     * @deprecated [module internal use] This should be protected, not public
     */
    /**@hide*/
    public void onConfigurationChanged (Configuration newConfig) {
    }

    /**
     * This is called during layout when the size of this view has changed; If you were just added to the view hierarchy, you're called with the old values of 0.
     * @param w Current width of this view.
     * @param h Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     * @deprecated [module internal use] This should be protected, not public
     */
    /**@hide*/
    public void onSizeChanged(int w, int h, int oldw, int oldh){
    }

    /**
     * To set the order of pickers in the HtcDatePicker
     * @param order please refer to java.text.DateFormat for detail
     */
    public void setPickersOrder(String order) {
        mOrder = order;
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getShortMonths();
        reorderPickers(months);
    }

    private void reorderPickers(String[] months) {
        java.text.DateFormat format;
        String order;
        if (months[0].startsWith("1")) {
            format = DateFormat.getDateFormat(getContext());
        } else {
            format = DateFormat.getMediumDateFormat(getContext());
        }

        if (mOrder != null)
            order = mOrder;
        else if (format instanceof SimpleDateFormat) {
            order = Settings.System.getString(getContext().getContentResolver(),Settings.System.DATE_FORMAT);
            if (order == null || order.length() < 2)
                order = ((SimpleDateFormat) format).toPattern();
        } else {
            // Shouldn't happen, but just in case.
            order = new String(DateFormat.getDateFormatOrder(getContext()));
        }

        //Log.i("ferro", "order " + order);

        /*
        ViewGroup [] vtmp = new ViewGroup[3];
        vtmp[0] = (ViewGroup) findViewById(R.id.picker_1);
        vtmp[1] = (ViewGroup) findViewById(R.id.picker_2);
        vtmp[2] = (ViewGroup) findViewById(R.id.picker_3);
        vtmp[0].removeAllViews();
        vtmp[1].removeAllViews();
        vtmp[2].removeAllViews();
        */

        //ViewGroup.LayoutParams lparams[] = new ViewGroup.LayoutParams[3];
        if (mLparams[0] == null)
            mLparams[0] = (ViewGroup.MarginLayoutParams)mMonthCoat.getLayoutParams();
            //mLparams[0] = mMonthPicker.getLayoutParams();
        if (mLparams[1] == null)
            mLparams[1] = (ViewGroup.MarginLayoutParams)mDayCoat.getLayoutParams();
            //mLparams[1] = mDayPicker.getLayoutParams();
        if (mLparams[2] == null)
            mLparams[2] = (ViewGroup.MarginLayoutParams)mYearCoat.getLayoutParams();
            //mLparams[2] = mYearPicker.getLayoutParams();
        removeAllViews();

        boolean quoted = false;
        boolean didDay = false, didMonth = false, didYear = false;
        boolean isTwoPicker = false;
        if (order.indexOf("-") != -1 && order.indexOf("-") == order.lastIndexOf("-"))
            isTwoPicker = true;
        int tmpi = 0;
        for (int i = 0; i < order.length(); i++) {
            char c = order.charAt(i);

            if (c == '\'') {
                quoted = !quoted;
            }

            if (!quoted) {
                if (c == DATE && !didDay) {
                    //addView(mDayPicker, mLparams[tmpi]);
                    addView(mDayCoat, mLparams[tmpi]);
                    //vtmp[tmpi].addView(mDayPicker);
                    if (isTwoPicker)
                        tmpi = 2;
                    else
                        tmpi++;
                    didDay = true;
                } else if ((c == MONTH || c == 'L') && !didMonth) {
                    //addView(mMonthPicker, mLparams[tmpi]);
                    addView(mMonthCoat, mLparams[tmpi]);
                    //vtmp[tmpi].addView(mMonthPicker);
                    if (isTwoPicker)
                        tmpi = 2;
                    else
                        tmpi++;
                    didMonth = true;
                } else if (c == YEAR && !didYear) {
                    //addView (mYearPicker, mLparams[tmpi]);
                    addView (mYearCoat, mLparams[tmpi]);
                    //vtmp[tmpi].addView(mYearPicker);
                    if (isTwoPicker)
                        tmpi = 2;
                    else
                        tmpi++;
                    didYear = true;
                }
            }
        }
        if(didYear && didMonth && !didDay) {
            RelativeLayout.LayoutParams yearLP = new RelativeLayout.LayoutParams(mLparams[2].width,mLparams[2].height);
            yearLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
                //add by Ahan 2012/01/31, we need to add top margin from s40.
                yearLP.topMargin = mLparams[2].topMargin;
            mYearCoat.setLayoutParams(yearLP);
        }

    }
    private void updateTables() {
        setCurrentYear(mYear);
        setCurrentMonth(mMonth + 1);
        correctDayPicker();
        //updateDayPicker();
        //setCurrentDay(mDay);
    }

    private void correctDayPicker() {
        Calendar cal = Calendar.getInstance();
        cal.set(mYear, mMonth, 1);
        int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        mDayPicker_31.setVisibility(View.INVISIBLE);
        mDayPicker_29.setVisibility(View.INVISIBLE);
        mDayPicker_28.setVisibility(View.INVISIBLE);
        mDayPicker.setVisibility(View.INVISIBLE);

        if (mDay > max) mDay = max;
        else if (mDay < 1) mDay = 1;

        HtcNumberPicker tmp = null;

        if (max == 30) tmp = mDayPicker;
        else if (max == 31) tmp = mDayPicker_31;
        else if (max ==29) tmp = mDayPicker_29;
        else if (max == 28) tmp = mDayPicker_28;

        if (tmp != null)
            tmp.setVisibility(View.VISIBLE);

        mOldMax = max;

        setCurrentDay(mDayPicker_31, mDay);

        if (mDay < 31) setCurrentDay(mDayPicker, mDay);
        else setCurrentDay(mDayPicker, 30);

        if (mDay < 30) setCurrentDay(mDayPicker_29, mDay);
        else setCurrentDay(mDayPicker_29, 29);

        if (mDay < 29) setCurrentDay(mDayPicker_28, mDay);
        else setCurrentDay(mDayPicker_28, 28);

        if (mDay > max) mDay = max;

        setDayMultiStop(true);
    }

    private void setCurrentDay(HtcNumberPicker picker, int target) {
        if (picker == null) return;
        if (picker.mReadyToSet && target == picker.getCenterView()) return;
        picker.setCenterView(target);

        String desc = (mLabelDay==null ? picker.getKeyOfPicker() : mLabelDay.getText().toString());
        picker.setContentDescription(Integer.toString(target)+" "+desc);
    }

    /*
    private void updateDayPicker() {
        Calendar cal = Calendar.getInstance();
        cal.set(mYear, mMonth, 1);
        int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (mOldMax != max) {
            setDayRange(1, max);
        }

        if (mDay < 1) {
            mDay = 1;
            setCurrentDay(mDay);
        } else if (mDay > max) {
            mDay = max;
            setCurrentDay(mDay);
        } else if (mOldMax != max) {
            setCurrentDay(mDay);
    }
    mOldMax = max;

    }
    */

    /**
     * To set listener for scroll state has changed to idle to HtcDatePicker.
     * @param listener the callback method for HtcDatePicker.
     */
    public void setOnScrollIdleStateListener(OnScrollIdleStateListener listener) {
        if (null != listener)
            mIdleScrollListener = listener ;
    }

    /**
     * To set the listener for scroll state has changed to idle.
     * @param target the callback method for each picker.
     */
    public void dispatchOnScrollIdleStateListener(OnScrollIdleStateListener target) {
        if (null != target) {
            if (null != mMonthPicker)    mMonthPicker.setOnScrollIdleStateListener(target) ;
            if (null != mDayPicker)        mDayPicker.setOnScrollIdleStateListener(target) ;
            if (null != mDayPicker_31)        mDayPicker_31.setOnScrollIdleStateListener(target) ;
            if (null != mDayPicker_29)        mDayPicker_29.setOnScrollIdleStateListener(target) ;
            if (null != mDayPicker_28)        mDayPicker_28.setOnScrollIdleStateListener(target) ;
            if (null != mYearPicker)        mYearPicker.setOnScrollIdleStateListener(target) ;
        }
    }

    /**
     * To set the center view of MonthPicker.
     * @param month the number wish to be set in the center of the table
     */
    public void setCurrentMonth(int month) {
        mMonthPicker.setCenterView(month);
        mMonthPicker.setContentDescription(Integer.toString(month)+" "+mMonthPicker.getKeyOfPicker());
    }

    /**
     * To set the center view of DayPicker.
     * @param day the number wish to be set in the center of the table
     */
    public void setCurrentDay(int day) {
        //Edit by Ahan for Usage issue like PYRAMID_ICS#3599.
        mDay = day;
        correctDayPicker();
    }

    //boolean mRangeYearReset = false;
    /**
     * To set the center view of YearPicker.
     * @param year the number wish to be set in the center of the table
     */
    public void setCurrentYear(int year) {
        mYearPicker.setCenterView(year);
        mYearPicker.setContentDescription(Integer.toString(year)+" "+mYearPicker.getKeyOfPicker());
    }

    /**
     * To get current month
     * @return current month selected
     */
    @ExportedProperty(category = "CommonControl")
    public int getCurrentMonth() {
        int month = mMonthPicker.getCenterView();
    return month;
    }

    /**
     * To get current day.
     * @return current day selected
     */
    @ExportedProperty(category = "CommonControl")
    public int getCurrentDay() {
        if (mDayPicker_31.getVisibility() == View.VISIBLE){
            //Log.i("ferro", "getCurrentDay 1 " + mDayPicker_31.getCenterView());
            return mDayPicker_31.getCenterView();
        } else if (mDayPicker_29.getVisibility() == View.VISIBLE)
            return mDayPicker_29.getCenterView();
        else if (mDayPicker_28.getVisibility() == View.VISIBLE)
            return mDayPicker_28.getCenterView();

        return mDayPicker.getCenterView();
    }

    /**
     * To get current year.
     * @return current year selected
     */
    @ExportedProperty(category = "CommonControl")
    public int getCurrentYear() {
        return mYearPicker.getCenterView();
    }

    /**
     * To set the data range of the table adapter to the MonthPicker
     * @param min the first value
     * @param max the last value
     */
    public void setMonthRange(int min, int max) {
        mMonthPicker.setRange(min, max);
    }

    int mRangeDayMin = -1;
    int mRangeDayMax = -1;
    boolean mRangeDayReset = false;

    /**
     * To set the data range of the table adapter to the DayPicker
     * @param min the first value
     * @param max the last value
     */
    public void setDayRange(int min, int max) {
        /*
        mRangeDayReset = false;
        if (mRangeDayMin != min) {
            mRangeDayMin = min;
            mRangeDayReset = true;
        }
        if (mRangeDayMax != max) {
            mRangeDayMax = max;
            mRangeDayReset = true;
        }
        if (mRangeDayReset)
        */
        mDayPicker.setRange(mRangeDayMin, mRangeDayMax);
    }

    private void setDayMultiStop(boolean enabled) {
        if (enabled) {
            if (mDayPicker != null) mDayPicker.setMultiStopDistance(new int[] {0,10,20});
            if (mDayPicker_31 != null) mDayPicker_31.setMultiStopDistance(new int[] {1,11,21});
            if (mDayPicker_29 != null) mDayPicker_29.setMultiStopDistance(new int[] {9,19});
            if (mDayPicker_28 != null) mDayPicker_28.setMultiStopDistance(new int[] {8,18});
        }
    }

    /**
     * To set the data range of the table adapter to the YearPicker
     * @param min the first value
     * @param max the last value
     */
    public void setYearRange(int min, int max) {
        int firstStop, count;
        boolean skipLastStop = false;
        ArrayList<Integer> stops = new ArrayList<Integer> ();

        //mRangeYearReset = true;
    mStartYear = min;
    mEndYear = max;

        firstStop = mEndYear % 10;
        skipLastStop = (firstStop==0 && mStartYear%10==0 ? true : false);
        count = mEndYear - mStartYear + 1;

        for (int i=firstStop; i<count; i+=10)
            stops.add(i);

        if (stops!=null && stops.size()>0) {
            int size = skipLastStop ? stops.size()-1 : stops.size();
            int[] stop_indexes = new int[size];

            for (int x=0; x<size; x++)
                stop_indexes[x] = stops.get(x);

            mYearPicker.setRange(mStartYear, mEndYear);
            mYearPicker.setMultiStopDistance(stop_indexes);
        }
    }
    /*
    public void startAnimation(Animation an) {
        mMonthPicker.getTableView().startAnimation(an);
        mDayPicker.getTableView().startAnimation(an);
        mYearPicker.getTableView().startAnimation(an);
    }
    */

    /**
     * To release all resource used by DatePicker, AP should not call it.
     * @deprecated [module internal use]
     */
    /**@hide*/
    public void releaseResource() {
        mMonthPicker.releaseResource();
        mDayPicker.releaseResource();
        mDayPicker_31.releaseResource();
        mDayPicker_29.releaseResource();
        mDayPicker_28.releaseResource();
        mYearPicker.releaseResource();
    }

    /**
     * Callback method, it will be callbacked when the scroll/fling is finished and the center view has changed.
     * @param target the picker which its center view has changed
     * @param data the new value of the new center view
     */
    public void onDataSet(HtcNumberPicker target, int data) {
        if (mMonthPicker == target || mYearPicker == target) {
            mMonth = getCurrentMonth() - 1;
            mYear = getCurrentYear();
            correctDayPicker();
            if (mOnDateChangedListener != null) mOnDateChangedListener.onDateChanged(this, mYear, mMonth, mDay);
        } else if( mDayPicker == target || mDayPicker_28 == target ||
            mDayPicker_29 == target || mDayPicker_31 == target) {
            mDay = getCurrentDay();
            if (mOnDateChangedListener != null) mOnDateChangedListener.onDateChanged(this, mYear, mMonth, mDay);
        }

        handleAccessibilityOnDataSet(target, data);
    }

    private void handleAccessibilityOnDataSet(HtcNumberPicker target, int data) {
        if (target == null) return;

        String desc = Integer.toString(data);

        target.setContentDescription(null);
        target.announceForAccessibility(desc);

        if (target == mYearPicker || target == mMonthPicker || mLabelDay==null)
            target.setContentDescription(desc+" "+target.getKeyOfPicker());
        else
            target.setContentDescription(desc+" "+mLabelDay.getText().toString());
    }

        /**
         * To set the title of the DayPicker
         * @param title the title string
         */
    public void setDayPickerTitle(String title) {
        mLabelDay.setText(title);
    }

        /**
         * To set the title of the MonthPicker
         * @param title the title string
         */
    public void setMonthPickerTitle(String title) {
        //mMonthPicker.setTitle(title);
        mLabelMonth.setText(title);
    }

        /**
         * To set the title of the YearPicker
         * @param title the title string
         */
    public void setYearPickerTitle(String title) {
        //mYearPicker.setTitle(title);
        mLabelYear.setText(title);
    }

        /**
         * To disable default title, should not use it any more.
         * @deprecated [Not use any longer] This API is useless since S50
         */
        /**@hide*/
    public void disableTitle() {
        mLabelDay.setVisibility(View.GONE);
        mLabelMonth.setVisibility(View.GONE);
        mLabelYear.setVisibility(View.GONE);
        RelativeLayout.LayoutParams tmpParam = (RelativeLayout.LayoutParams) mMonthPicker.getLayoutParams();
        tmpParam.setMargins(0,0,0,0);
        tmpParam = (RelativeLayout.LayoutParams) mDayPicker.getLayoutParams();
        tmpParam.setMargins(0,0,0,0);
        tmpParam = (RelativeLayout.LayoutParams) mDayPicker_31.getLayoutParams();
        tmpParam.setMargins(0,0,0,0);
        tmpParam = (RelativeLayout.LayoutParams) mDayPicker_29.getLayoutParams();
        tmpParam.setMargins(0,0,0,0);
        tmpParam = (RelativeLayout.LayoutParams) mDayPicker_28.getLayoutParams();
        tmpParam.setMargins(0,0,0,0);
        tmpParam = (RelativeLayout.LayoutParams) mYearPicker.getLayoutParams();
        tmpParam.setMargins(0,0,0,0);
    }

        /**
         * To set no boundary view to each picker.
         * @param b true to enable, false to disable
         */
    public void setRepeatEnable(boolean b) {
        mDayPicker.setRepeatEnable(b);
        mDayPicker_31.setRepeatEnable(b);
        mDayPicker_29.setRepeatEnable(b);
        mDayPicker_28.setRepeatEnable(b);
        mMonthPicker.setRepeatEnable(b);
        mYearPicker.setRepeatEnable(b);
    }

    //Add by Ahan 2011/12/02 for AP WorldClock
    /**
     * To set the custom background to the specific picker
     * @param target to indicate which picker will be applied the background
     * @param tumblerId the resource Id of the tumbler asset
     * @param shadowId the resource Id of the shadow asset
     */
    public void setPickerBackground(int target, int tumblerId, int shadowId) {
        HtcNumberPicker tmp = giveMeThePickerInstance(target);

        if (tmp == null && target != PICKER_TYPE_DAY) return;
        else if (tmp == null && target == PICKER_TYPE_DAY) setDayPickersBackground(tumblerId, shadowId);
        else if (tmp != null) tmp.setBackground(tumblerId, shadowId);
    }

    //Add by Ahan 2011/12/02 for AP WorldClock
    /**
     * To set text color inner each picker.
     * @param target to indicate which picker will be applied
     * @param textColor textColor
     */
    public void setPickerTextColor(int target, int textColor) {
        HtcNumberPicker tmp = giveMeThePickerInstance(target);

        if (tmp == null && target != PICKER_TYPE_DAY) return;
        else if (tmp == null && target == PICKER_TYPE_DAY) setDayPickersTextColor(textColor);
        else if (tmp != null) tmp.setTextColor(textColor);
    }

    private void setDayPickersBackground(int tumblerId, int shadowId) {
        mDayPicker.setBackground(tumblerId, shadowId);
        mDayPicker_31.setBackground(tumblerId, shadowId);
        mDayPicker_29.setBackground(tumblerId, shadowId);
        mDayPicker_28.setBackground(tumblerId, shadowId);
    }

    private void setDayPickersTextColor(int textColor) {
        mDayPicker.setTextColor(textColor);
        mDayPicker_31.setTextColor(textColor);
        mDayPicker_29.setTextColor(textColor);
        mDayPicker_28.setTextColor(textColor);
    }

    private HtcNumberPicker giveMeThePickerInstance(int target) {
        HtcNumberPicker tmp = null;

        if (target == PICKER_TYPE_YEAR) tmp = mYearPicker;
        else if (target == PICKER_TYPE_MONTH) tmp = mMonthPicker;

        return (tmp);
    }
}
