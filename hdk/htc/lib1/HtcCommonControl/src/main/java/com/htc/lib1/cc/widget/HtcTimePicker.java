package com.htc.lib1.cc.widget;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.content.res.Configuration;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.view.animation.Animation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;

import com.htc.lib1.cc.widget.HtcNumberPicker.OnScrollIdleStateListener;
import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.res.HtcResUtil;

/**
 * HtcTimePicker
 */
public class HtcTimePicker extends RelativeLayout implements OnScrollIdleStateListener {

    //Add By Ahan 2011/12/02 for identifing picker type
    /**
     * Constant to represent the Hour Picker.
     */
    public static final int PICKER_TYPE_HOUR = 0;
    /**
     * Constant to represent the Minute Picker.
     */
    public static final int PICKER_TYPE_MINUTE = 1;
    /**
     * Constant to represent the Second Picker.
     */
    public static final int PICKER_TYPE_SECOND = 2;
    /**
     * Constant to represent the AMPM Picker.
     */
    public static final int PICKER_TYPE_AMPM = 3;

    private static final boolean LOG = false;
    private static final String TAG = "HtcTimePicker";

    private static final int MULTISTOP_DISTANCE = 10;

    private LayoutInflater mTableInflater;

    /* UI Components */
    private final HtcNumberPicker mHourPicker;
    private final HtcNumberPicker mMinutePicker;
    private final HtcNumberPicker mSecondPicker;
    private final HtcNumberPicker mAmPmPicker;

    private final TextView mLabelHour;
    private final TextView mLabelMinute;
    private final TextView mLabelSecond;
    private final TextView mLabelAmPm;

    /**
     * How we notify users the date has changed.
     */
    private OnTimeSetListener mOnTimeSetListener;
    private OnScrollIdleStateListener mIdleScrollListener = null;

    @ExportedProperty(category = "CommonControl")
    private int mCurrentHour = 0; // 0-23

    @ExportedProperty(category = "CommonControl")
    private int mCurrentMinute = 0; // 0-59

    @ExportedProperty(category = "CommonControl")
    private int mCurrentSecond = 0; // 0 -59

    private int mMaxHour = 23;

    @ExportedProperty(category = "CommonControl")
    private int mCurrentAMPM = 0;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsHourInit = true;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsMinuteInit = true;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsSecondInit = true;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsMultiStop = false;

    @ExportedProperty(category = "CommonControl")
    private boolean mIsFirstCreate = true;

    @ExportedProperty(category = "CommonControl")
    private int mStartHour;

    @ExportedProperty(category = "CommonControl")
    private int mEndHour;

    @ExportedProperty(category = "CommonControl")
    private int mStartMinute;

    @ExportedProperty(category = "CommonControl")
    private int mEndMinute;

    @ExportedProperty(category = "CommonControl")
    private int mStartSecond;

    @ExportedProperty(category = "CommonControl")
    private int mEndSecond;

    private String mOrder;
    private ViewGroup.LayoutParams mLparams[] = new ViewGroup.LayoutParams[3];

    private Drawable mFocusIndicator;
    private boolean mDrawFocusIndicator;
    private HtcNumberPicker[] mPickers;

    /**
     * The callback used to indicate the user changes the date.
     */
    public interface OnTimeSetListener {

        /**
         * Callback when time has been set.
         * @param view The view associated with this listener.
         * @param hourOfDay The hour that was set.
         * @param minute The minute that was set.
         * @param second The second that was set.
         */
        void onTimeSet(HtcTimePicker view, int hourOfDay, int minute, int second);
    }

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcTimePicker(Context context) {
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
    public HtcTimePicker(Context context, AttributeSet attrs) {
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
    public HtcTimePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.timer_table3, this, true);

        mHourPicker = (HtcNumberPicker) findViewById(R.id.hour_table_view);
        mMinutePicker = (HtcNumberPicker) findViewById(R.id.minute_table_view);
        mSecondPicker = (HtcNumberPicker) findViewById(R.id.second_table_view);
        mAmPmPicker = (HtcNumberPicker) findViewById(R.id.ampm_table_view);

        mLabelHour = (TextView) findViewById(R.id.hour_label);
        mLabelMinute = (TextView) findViewById(R.id.minute_label);
        mLabelSecond = (TextView) findViewById(R.id.second_label);
        mLabelAmPm = (TextView) findViewById(R.id.ampm_label);

        boolean needAllCaps = HtcResUtil.isInAllCapsLocale(context);
        if (mLabelHour != null) mLabelHour.setAllCaps(needAllCaps);
        if (mLabelMinute != null) mLabelMinute.setAllCaps(needAllCaps);
        if (mLabelSecond != null) mLabelSecond.setAllCaps(needAllCaps);
        if (mLabelAmPm != null) mLabelAmPm.setAllCaps(needAllCaps);

        mSecondPicker.setEnabled(false);
        mSecondPicker.setVisibility(View.GONE);
        mLabelSecond.setVisibility(View.GONE);

        setMinuteRange(0, 59);
        setSecondRange(0,59);

    if (!DateFormat.is24HourFormat(context)) {
            setHourRange(1,12);
            mAmPmPicker.setEnabled(true);
            mAmPmPicker.setVisibility(View.VISIBLE);
            String [] ampm = new String[2];
            ampm[0] = getResources().getString(R.string.am);//"AM";
            ampm[1] = getResources().getString(R.string.pm);//"PM";

            if (ampm[0].length() > 2 || ampm[1].length() > 2) {
                ampm[0] = "AM";
                ampm[1] = "PM";
            }
            setAmPmRange(0,1,ampm);
        } else {
            setHourRange(0,23);
            mAmPmPicker.setEnabled(false);
            mAmPmPicker.setVisibility(View.GONE);
            mLabelAmPm.setVisibility(View.GONE);
        }

        Calendar cal = Calendar.getInstance();
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getShortMonths();

        dispatchOnScrollIdleStateListener(this);
        mIsFirstCreate = true;

        mHourPicker.setKeyOfPicker(mLabelHour==null ? "Hour" : mLabelHour.getText().toString());
        mMinutePicker.setKeyOfPicker(mLabelMinute==null ? "Minute" : mLabelMinute.getText().toString());
        mSecondPicker.setKeyOfPicker(mLabelSecond==null ? "Second" : mLabelSecond.getText().toString());
        mAmPmPicker.setKeyOfPicker("AmPm");

        initAllAboutFocus(context);

        mPickers = new HtcNumberPicker[] {mHourPicker, mMinutePicker, mSecondPicker, mAmPmPicker};

        for (int i=0; i<mPickers.length; i++) {
            if (mPickers[i] != null) {
                mPickers[i].setFocusable(true);
                mPickers[i].usingTwoLayerFocus(true, this);
            }
        }

        setFocusable(true);
        setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }

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

    /** @hide */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
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

        for (HtcNumberPicker tmp : mPickers) {
            if ((tmp.getVisibility()==View.VISIBLE) && (tmp.getLeft() < target.getLeft())) {
                isMostLeft = false;
                break;
            }
        }

        return isMostLeft;
    }

    /** @hide */
    @ExportedProperty(category = "CommonControl")
    public boolean isTheMostRightPicker(HtcNumberPicker target) {
        if (mPickers==null || target==null)
            return false;

        boolean isMostRight = true;

        for (HtcNumberPicker tmp : mPickers) {
            if ((tmp.getVisibility()==View.VISIBLE) && (tmp.getRight() > target.getRight())) {
                isMostRight = false;
                break;
            }
        }

        return isMostRight;
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

    //This API is used for onConfigurationChanged of HtcDatePciker/HtcDatePickerDialog, not for AP.
    /**
     * To do initPicker, this API should not be used by outside com.htc.widget
     * @param hour The initial hour.
     * @param minute The initial minute.
     * @param second The initial second.
     * @deprecated [module internal use] This API will change to package level on S50
     */
    /**@hide*/
    public void initPicker(int hour, int minute, int second){
        mHourPicker.getTableView().setVisibility(View.INVISIBLE);
        mMinutePicker.getTableView().setVisibility(View.INVISIBLE);
        mSecondPicker.getTableView().setVisibility(View.INVISIBLE);
        mAmPmPicker.getTableView().setVisibility(View.INVISIBLE);
        setMinuteRange(mStartMinute, mEndMinute);
        setSecondRange(mStartSecond, mEndSecond);

        if (!DateFormat.is24HourFormat(getContext())) {
            setHourRange(mStartHour,mEndHour);
            if (!mSecondPicker.isEnabled()) {
                mAmPmPicker.setEnabled(true);
                mAmPmPicker.setVisibility(View.VISIBLE);
                String [] ampm = new String[2];
                ampm[0] = getResources().getString(R.string.am);//"AM";
                ampm[1] = getResources().getString(R.string.pm);//"PM";

                if (ampm[0].length() > 2 || ampm[1].length() > 2) {
                    ampm[0] = "AM";
                    ampm[1] = "PM";
                }
                setAmPmRange(0,1,ampm);
            }
        } else {
            setHourRange(mStartHour,mEndHour);
            mAmPmPicker.setEnabled(false);
            mAmPmPicker.setVisibility(View.GONE);
            mLabelAmPm.setVisibility(View.GONE);
        }

        //Calendar cal = Calendar.getInstance();
        setMinuteRange(mStartMinute, mEndMinute, mIsMultiStop);
        setSecondRange(mStartSecond, mEndSecond, mIsMultiStop);
        setRepeatEnable(true);

        if(!mSecondPicker.isEnabled())     second = 0;
        mCurrentHour = hour;
        mCurrentMinute = minute;
        mCurrentSecond = second;
        mIsHourInit = true;
        mIsMinuteInit = true;
        mIsSecondInit = true;
        mSecondPicker.notifyOnInitState();
        mMinutePicker.notifyOnInitState();
        mHourPicker.notifyOnInitState();
        updateTables();
        setCurrentAmPm(mCurrentAMPM);
    requestFocus();
    dispatchOnScrollIdleStateListener(this);
    }

    /**
     * To set SecondPicker is enabled or not.
     * @param b True to show SecondPicker, false to show AmPmPicker instead.
     */
    public void setSecondPickerEnable(boolean b) {
        if (b) {
            mSecondPicker.setEnabled(true);
            mSecondPicker.setVisibility(View.VISIBLE);
            mLabelSecond.setVisibility(View.VISIBLE);
            mAmPmPicker.setEnabled(false);
            mAmPmPicker.setVisibility(View.GONE);
        } else {
            mSecondPicker.setEnabled(false);
            mSecondPicker.setVisibility(View.GONE);
            mLabelSecond.setVisibility(View.GONE);
        }
    }

    /**
     * To set SecondPicker is enabled or not.
     * @param b True to show SecondPicker, false to show AmPmPicker instead.
     * @param white To chage background or not.
     * @deprecated [not use any longer] Please use setSecondPickerEnable(boolean) and setPickerBackground(int, int, int) instead.
     */
    /**@hide*/
    public void setSecondPickerEnable(boolean b, boolean white) {
        setSecondPickerEnable(b);
        if (b && white) mSecondPicker.changeBkg();
    }

    /**
     * Initialize the state.
     * @param hour Init hour.
     * @param minute Init minute.
     * @param second Init second.
     * @param onTimeSetListener Listener to listen if selected time has changed.
     * @deprecated [module internal use] It is for internal usage, never use it outside.
     */
    public void init(int hour, int minute, int second, OnTimeSetListener onTimeSetListener) {
        mCurrentHour = hour;
        mCurrentMinute = minute;
        mCurrentSecond = second;
        mIsHourInit = true;
        mIsMinuteInit = true;
        mIsSecondInit = true;
        mOnTimeSetListener = onTimeSetListener;
        updateTables();
    }

    private void updateTables() {
        setCurrentHour(mCurrentHour);
        setCurrentMinute(mCurrentMinute);
        setCurrentSecond(mCurrentSecond);
    }

    /**
     * To set listener for scroll state has changed to idle to HtcDatePicker.
     * @param listener the callback method for HtcDatePicker.
     * @deprecated [Not use any longer] This takes no effect and will be removed soon.
     */
    /**@hide*/
    public void setOnScrollIdleStateListener(OnScrollIdleStateListener listener) {
        if (null != listener) mIdleScrollListener = listener;
    }

    /**
     * To set the listener for scroll state has changed to idle.
     * @param target the callback method for each picker.
     */
    public void dispatchOnScrollIdleStateListener(OnScrollIdleStateListener target) {
        if (null != target) {
            if (null != mMinutePicker) mMinutePicker.setOnScrollIdleStateListener(target);
            if (null != mHourPicker) mHourPicker.setOnScrollIdleStateListener(target);
            if (null != mAmPmPicker && mAmPmPicker.isEnabled()) mAmPmPicker.setOnScrollIdleStateListener(target);
            if (null != mSecondPicker && mSecondPicker.isEnabled()) mSecondPicker.setOnScrollIdleStateListener(target);
        }
    }

    /**
     * To set the center view of MinutePicker.
     * @param m The value of the view that will be center.
     */
    public void setCurrentMinute(int m) {
        mIsMinuteInit = false;
        mCurrentMinute = m;
        mMinutePicker.setCenterView(m);
        mMinutePicker.setContentDescription(Integer.toString(m)+" "+mMinutePicker.getKeyOfPicker());
    }

    /**
     * To set the center view of SecondPicker.
     * @param s The value of the view that will be center.
     */
    public void setCurrentSecond(int s) {
        mIsSecondInit = false;
        mCurrentSecond = s;
        mSecondPicker.setCenterView(s);
        mSecondPicker.setContentDescription(Integer.toString(s)+" "+mSecondPicker.getKeyOfPicker());
    }

    /**
     * To set the center view of HourPicker.
     * @param h The value of the view that will be center.
     */
    public void setCurrentHour(int h) {
        mIsHourInit = false;
        mCurrentHour = h;
        if (mAmPmPicker.isEnabled()) {
            if (h >= 12) {
                h = (h == 12 ? 12 : h - 12);
                setCurrentAmPm(0);
            } else {
                h = (h == 0 ? 12 : h);
                setCurrentAmPm(1);
            }
        }

        mHourPicker.setCenterView(h);
        mHourPicker.setContentDescription(Integer.toString(h)+" "+mHourPicker.getKeyOfPicker());
    }

    /**
     * To set the center view of AmPmPicker.
     * @param ampm The value of the view that will be center.
     */
    public void setCurrentAmPm(int ampm) {
        mCurrentAMPM = ampm;
        mAmPmPicker.setCenterView(ampm);
        mAmPmPicker.setContentDescription((ampm==0 ? " PM" : " AM")+" "+mAmPmPicker.getKeyOfPicker());
    }

    /**
     * To get the value of the center view of MinutePicker.
     * @return The value of the center view .
     */
    public int getCurrentMinute() {
        return mMinutePicker.getCenterView();
    }

    /**
     * To get the value of the center view of HourPicker.
     * @return The value of the center view .
     */
    @ExportedProperty(category = "CommonControl")
    public int getCurrentHour() {
        int h = mHourPicker.getCenterView();
        if (mAmPmPicker.isEnabled()) {
            if (h == 12) h = 0;
            int am = getCurrentAmPm();
            if (am == 0) h += 12;
        }
        return h;
    }

    /**
     * To get the value of the center view of SecondPicker.
     * @return The value of the center view .
     */
    @ExportedProperty(category = "CommonControl")
    public int getCurrentSecond() {
        return mSecondPicker.getCenterView();
    }

    /**
     * To get the value of the center view of AmPmPicker.
     * @return The value of the center view .
     */
    @ExportedProperty(category = "CommonControl")
    public int getCurrentAmPm() {
        return mAmPmPicker.getCenterView();
    }

    /**
     * To set the data range of the table adapter to the MinutePicker
     * @param min the first value
     * @param max the last value
     * @param multiStop true to enable multiStop, false to disable
     */
    public void setMinuteRange(int min, int max, boolean multiStop) {
        mMinutePicker.setRange(min, max);
        mStartMinute = min;
        mEndMinute = max;
        mIsMultiStop = multiStop;
        mMinutePicker.setMultiStopDistance((multiStop ? MULTISTOP_DISTANCE : -1));
    }

    /**
     * To set the data range of the table adapter to the MinutePicker
     * @param min the first value
     * @param max the last value
     */
    public void setMinuteRange(int min, int max) {
        this.setMinuteRange(min, max, false);
    }

    /**
     * To set the data range of the table adapter to the HourPicker
     * @param min the first value
     * @param max the last value
     */
    public void setHourRange(int min, int max) {
        mMaxHour = max;
        mHourPicker.setRange(min, max);
        mStartHour = min;
        mEndHour = max;
    }

    /**
     * To set the data range of the table adapter to the SecondPicker
     * @param min the first value
     * @param max the last value
     */
    public void setSecondRange(int min, int max) {
        this.setSecondRange(min, max, false);
    }

    /**
     * To set the data range of the table adapter to the SecondPicker
     * @param min the first value
     * @param max the last value
     * @param multiStop true to enable multiStop, false to disable
     */
    public void setSecondRange(int min, int max, boolean multiStop) {
        mSecondPicker.setRange(min, max);
        mStartSecond = min;
        mEndSecond = max;
        mIsMultiStop = multiStop;
        mSecondPicker.setMultiStopDistance((multiStop ? MULTISTOP_DISTANCE : -1));
    }

    /**
     * To set the data range of the table adapter to the AmPmPicker
     * @param min the first value, it represents the index of the data array this case
     * @param max the last value, it represents the index of the data array this case
     * @param candidates the text each index represents
     */
    public void setAmPmRange(int min, int max, String [] candidates) {
        mAmPmPicker.setRange(min, max, candidates);
        mAmPmPicker.setTextStyle(R.style.fixed_time_pick_primary_s);
    }

    /**
     * This API takes no effects.
     * @param an Useless.
     * @deprecated [module internal use] This API will change to private on S50
     */
    /**@hide*/
    public void startAnimation(Animation an) {
        mMinutePicker.getTableView().startAnimation(an);
        mHourPicker.getTableView().startAnimation(an);
        mAmPmPicker.getTableView().startAnimation(an);
    }

    /**
     * Nerver call this.
     * @deprecated [module internal use] Change to private on S50
     */
    /**@hide*/
    public void releaseResource() {
        mIdleScrollListener = null;
        mMinutePicker.releaseResource();
        mHourPicker.releaseResource();
        mSecondPicker.releaseResource();
        mAmPmPicker.releaseResource();
    }

    /**
     * Callback method, it will be callbacked when the scroll/fling is finished and the center view has changed.
     * @param target the picker which its center view has changed
     * @param data the new value of the new center view
     * @deprecated [module internal use] Never use it outside this pacakge
     */
    /**@hide*/
    public void onDataSet(HtcNumberPicker target, int data) {
        if (mHourPicker == target || mMinutePicker == target) {
            mCurrentHour = getCurrentHour();
            mCurrentMinute = getCurrentMinute();
            if (mOnTimeSetListener != null) mOnTimeSetListener.onTimeSet(this, mCurrentHour, mCurrentMinute, mCurrentSecond);
        } else if (null != mAmPmPicker && mAmPmPicker.isEnabled() && mAmPmPicker == target) {
            mCurrentHour = getCurrentHour();
            if (mOnTimeSetListener != null) mOnTimeSetListener.onTimeSet(this, mCurrentHour, mCurrentMinute, mCurrentSecond);
        } else if (null != mSecondPicker && mSecondPicker.isEnabled() && mSecondPicker == target) {
            mCurrentSecond = getCurrentSecond();
            if (mOnTimeSetListener != null) mOnTimeSetListener.onTimeSet(this, mCurrentHour, mCurrentMinute, mCurrentSecond);
        }

        handleAccessibilityOnDataSet(target, data);
    }

    private void handleAccessibilityOnDataSet(HtcNumberPicker target, int data) {
        if (target == null) return;

        String desc = (target==mAmPmPicker ? (data==0 ? "PM" : "AM") : Integer.toString(data));

        target.setContentDescription(null);
        target.announceForAccessibility(desc);
        target.setContentDescription(desc+" "+target.getKeyOfPicker());
    }

    /**
     * To set no boundary view to each picker.
     * @param b true to enable, false to disable
     */
    public void setRepeatEnable(boolean b) {
        mHourPicker.setRepeatEnable(b);
        mMinutePicker.setRepeatEnable(b);
        mSecondPicker.setRepeatEnable(b);
    }

    /**
     * To slide the child views of HourPicker by offset.
     * @param offset specify the offset to slide
     */
    public void slideSecondWithOffset(int offset) {
        mSecondPicker.slideWithOffset(offset);
        if(mCurrentSecond>mStartSecond) mCurrentSecond--;
        else mCurrentSecond = mEndSecond;
    }

    /**
     * To slide the child views of HourPicker by offset.
     * @param offset specify the offset to slide
     */
    public void slideMinuteWithOffset(int offset) {
        mMinutePicker.slideWithOffset(offset);
        if(mCurrentMinute>mStartMinute) mCurrentMinute--;
        else mCurrentMinute = mEndMinute;
    }

    /**
     * To slide the child views of HourPicker by offset.
     * @param offset specify the offset to slide
     */
    public void slideHourWithOffset(int offset) {
        mHourPicker.slideWithOffset(offset);
        if(mEndHour>mStartHour) mCurrentHour--;
        else mCurrentHour = mEndHour;
    }

    /**
     * To get the height each number takes.
     * @return Height each number takes in the pickers.
     */
    public int getPickerChildheight() {
        HtcNumberPicker tmp = null;

        if (mHourPicker != null) tmp = mHourPicker;
        else if (mMinutePicker != null) tmp = mMinutePicker;
        else if (mSecondPicker != null) tmp = mSecondPicker;

        return (tmp == null ? 0 : tmp.getMyTableChildHeight());
    }

    /**
     * Never call this.
     * @deprecated [Not use any longer] This API is useless since S50
     */
    /**@hide*/
    public void disableTitle() {
        mLabelHour.setVisibility(View.GONE);
        mLabelMinute.setVisibility(View.GONE);
        mLabelSecond.setVisibility(View.GONE);
        mLabelAmPm.setVisibility(View.GONE);

        RelativeLayout.LayoutParams tmpParam = (RelativeLayout.LayoutParams) mHourPicker.getLayoutParams();
        tmpParam.setMargins(0,0,0,0);
        tmpParam = (RelativeLayout.LayoutParams) mMinutePicker.getLayoutParams();
        tmpParam.setMargins(0,0,0,0);
        tmpParam = (RelativeLayout.LayoutParams) mSecondPicker.getLayoutParams();
        tmpParam.setMargins(0,0,0,0);
        tmpParam = (RelativeLayout.LayoutParams) mAmPmPicker.getLayoutParams();
        tmpParam.setMargins(0,0,0,0);
    }

    //Let AP WorldClock can their custom title style
    /**
     * To set the title of the HourPicker
     * @param title the title string
     * @deprecated [Not use any longer] It will be removed soon.
     */
    /**@hide*/
    public void setHourPickerTitle(String title) {
        setHourPickerTitle(title, 0);
    }

    /**
     * To set the title of the HourPicker
     * @param title the title string
     * @param titleStyle the resource id for text style will be applied
     * @deprecated [Not use any longer] It will be removed soon.
     */
    /**@hide*/
    public void setHourPickerTitle(String title, int titleStyle) {
        setHourPickerTitle(title, titleStyle, null);
    }

    /**
     * To set the title of the HourPicker
     * @param title the title string, null if no change.
     * @param titleStyle the resource id for text style, 0 if no change.
     * @param margins the margins of the picker, it just support only the bottom margin, null if no change.
     */
    public void setHourPickerTitle(String title, int titleStyle, MarginLayoutParams margins) {
        setPickerTitle(mLabelHour, title, titleStyle);
        setPickerMargin(mHourPicker, margins);
    }

    /**
     * To set the title of the MinutePicker
     * @param title the title string
     * @deprecated [Not use any longer] It will be removed soon.
     */
    /**@hide*/
    public void setMinutePickerTitle(String title) {
        setMinutePickerTitle(title, 0);
    }

    /**
     * To set the title of the MinutePicker
     * @param title the title string
     * @param titleStyle the resource id for text style will be applied
     * @deprecated [Not use any longer] It will be removed soon.
     */
    /**@hide*/
    public void setMinutePickerTitle(String title, int titleStyle) {
        setMinutePickerTitle(title, titleStyle, null);
    }

    /**
     * To set the title of the MinutePicker
     * @param title the title string, null if no change.
     * @param titleStyle the resource id for text style, 0 if no change.
     * @param margins the margins of the picker, it just support only the bottom margin, null if no change.
     */
    public void setMinutePickerTitle(String title, int titleStyle, MarginLayoutParams margins) {
        setPickerTitle(mLabelMinute, title, titleStyle);
        setPickerMargin(mMinutePicker, margins);
    }

    /**
     * To set the title of the SecondPicker
     * @param title the title string
     * @deprecated [Not use any longer] It will be removed soon.
     */
    /**@hide*/
    public void setSecondPickerTitle(String title) {
        setSecondPickerTitle(title, 0);
    }

    /**
     * To set the title of the SecondPicker
     * @param title the title string
     * @param titleStyle the resource id for text style will be applied
     * @deprecated [Not use any longer] It will be removed soon.
     */
    /**@hide*/
    public void setSecondPickerTitle(String title, int titleStyle) {
        setSecondPickerTitle(title, titleStyle, null);
    }

    /**
     * To set the title of the SecondPicker
     * @param title the title string, null if no change.
     * @param titleStyle the resource id for text style, 0 if no change.
     * @param margins the margins of the picker, it just support only the bottom margin, null if no change.
     */
    public void setSecondPickerTitle(String title, int titleStyle, MarginLayoutParams margins) {
        setPickerTitle(mLabelSecond, title, titleStyle);
        setPickerMargin(mSecondPicker, margins);
    }

    private void setPickerTitle(TextView target, String text, int textStyle) {
        if (target != null) {
            if (text != null) target.setText(text);
            if (textStyle != 0) target.setTextAppearance(getContext(), textStyle);
        }
    }

    private void setPickerMargin(HtcNumberPicker picker, MarginLayoutParams margins) {
        if (picker == null || margins == null) return;

        if (margins.bottomMargin >= 0) {
            MarginLayoutParams tmpParam = (MarginLayoutParams) picker.getLayoutParams();
            //[Ahan][2012/09/20][Let AP set below margin on S50 instead of top margin on S45 due to design has changed]
            tmpParam.setMargins(tmpParam.leftMargin, tmpParam.topMargin, tmpParam.rightMargin, margins.bottomMargin);
            //[Ahan][2012/09/20]
        }
    }
    //Let AP WorldClock can their custom title style

    /**
     * To enabled or not to all child pickers.
     * @param enabled True to enable, false to disable.
     */
    public void setEnabled(boolean enabled) {
        mHourPicker.setTableEnabled(enabled);
        mMinutePicker.setTableEnabled(enabled);
        mSecondPicker.setTableEnabled(enabled);
        mAmPmPicker.setTableEnabled(enabled);
    }

    /**
     * To check if SecondPicker is enabled.
     * @return True for enabled, false for disabled.
     */
    public boolean isSecondPickerEndabled() {
        if (mSecondPicker!=null) return mSecondPicker.isEnabled();
        return false;
    }

    //Add by Ahan 2012/04/02 for AP WorldClock
    @ExportedProperty(category = "CommonControl")
    private int mCustomWidth = Integer.MIN_VALUE;
    @ExportedProperty(category = "CommonControl")
    private boolean mSetCustomWidth = false;

    //Add by Ahan 2012/04/02 for AP WorldClock
    //TODO: Remove this on next sense version
    /**
     * Called to determine the size requirements for this view and all of its children.
     * @param widthMeasureSpec horizontal space requirements as imposed by the parent. The requirements are encoded with View.MeasureSpec.
     * @param heightMeasureSpec vertical space requirements as imposed by the parent. The requirements are encoded with View.MeasureSpec.
     * @deprecated [Not use any longer] Never use it.
     */
    @Override
    /**@hide*/
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mSetCustomWidth) {
            for (int i=0; i<getChildCount(); i++) {
                final View child = getChildAt(i);
                LayoutParams params = (LayoutParams)child.getLayoutParams();
                params.width = (mCustomWidth>0 ? mCustomWidth : params.width);
                child.setLayoutParams(params);
            }
            mSetCustomWidth = false;
        }
    }

    //Add by Ahan 2011/12/02 for AP WorldClock
    /**
     * To set the custom background to the specific picker
     * @param target to indicate which picker will be applied the background
     * @param tumblerId the resource Id of the tumbler asset
     * @param shadowId the resource Id of the shadow asset
     */
    public void setPickerBackground(int target, int tumblerId, int shadowId) {
        HtcNumberPicker tmpPicker = giveMeThePickerInstance(target);
        if (tmpPicker == null) return;
        tmpPicker.setBackground(tumblerId, shadowId);
    }

    //Add by Ahan 2012/04/02 for AP WorldClock
    /**
     * To set the custom background to the specific picker, this API will be deprecated on S50 and removed on next sense version, please do not use it anymore.
     * @param target to indicate which picker will be applied the background
     * @param tumblerId the resource Id of the tumbler asset
     * @param shadowId the resource Id of the shadow asset
     * @param width specify the width of the asset, the NumberPicker will fit this width
     * @deprecated [Not use any longer] This API used on S40a but please not use any longer from S50.
     */
    /**@hide*/
    public void setPickerBackground(int target, int tumblerId, int shadowId, int width) {
        HtcNumberPicker tmpPicker = giveMeThePickerInstance(target);

        if (tmpPicker == null) return;
        setAllPickerWidth(width);
        tmpPicker.setBackground(tumblerId, shadowId);
    }

    //Add by Ahan 2012/04/02 for AP WorldClock
    private void setAllPickerWidth(int width) {
        mCustomWidth = width;
        mSetCustomWidth = true;
    }

    //Add by Ahan 2011/12/02 for AP WorldClock
    /**
     * To set the text color to the specific picker.
     * @param target To indicate which picker will be applied the background.
     * @param textColor The text color.
     * @deprecated [Not use any longer] This will be removed soon.
     */
    public void setPickerTextColor(int target, int textColor) {
        HtcNumberPicker tmpPicker = giveMeThePickerInstance(target);
        if (tmpPicker == null) return;
        tmpPicker.setTextColor(textColor);
    }

    //Add by Ahan 2012/04/11 for AP WorldClock
    /**
     * To set if it is in the countdown mode, this API should be called just only in the Timer page of AP WorldClock.
     * @param enableCountDownMode true to enable countdown mode, false to disable.
     */
    public void setCountDownMode(boolean enableCountDownMode) {
        HtcNumberPicker tmpPicker;

        tmpPicker = giveMeThePickerInstance(PICKER_TYPE_MINUTE);
        if (tmpPicker != null) tmpPicker.setCountDownMode(enableCountDownMode);

        tmpPicker = giveMeThePickerInstance(PICKER_TYPE_SECOND);
        if (tmpPicker != null) tmpPicker.setCountDownMode(enableCountDownMode);
    }

    //Add by Ahan 2012/02/06 for AP WorldClock to set custom shadow for all HtcNumberPickers
    /**
     * To set the shadow style of the text.
     * @param layer The layer of this shadow, the lower the layer draws first.
     * @param radius Radius of the shadow. Must be a floating point value, such as "1.2".
     * @param dx Horizontal offset of the shadow. Must be a floating point value, such as "1.2".
     * @param dy Vertical offset of the shadow. Must be a floating point value, such as "1.2".
     * @param color Place a shadow of the specified color behind the text. Must be a color value, in the form of "#rgb", "#argb", "#rrggbb", or "#aarrggbb".
     */
    public void setCustomShadow(int layer, float radius, float dx, float dy, int color) {
        HtcNumberPicker tmpPicker;

        tmpPicker = giveMeThePickerInstance(PICKER_TYPE_HOUR);
        if (tmpPicker != null) tmpPicker.setCustomShadow(layer, radius, dx, dy, color);

        tmpPicker = giveMeThePickerInstance(PICKER_TYPE_MINUTE);
        if (tmpPicker != null) tmpPicker.setCustomShadow(layer, radius, dx, dy, color);

        tmpPicker = giveMeThePickerInstance(PICKER_TYPE_SECOND);
        if (tmpPicker != null) tmpPicker.setCustomShadow(layer, radius, dx, dy, color);

        tmpPicker = giveMeThePickerInstance(PICKER_TYPE_AMPM);
        if (tmpPicker != null) tmpPicker.setCustomShadow(layer, radius, dx, dy, color);
    }

    private HtcNumberPicker giveMeThePickerInstance(int target) {
        HtcNumberPicker tmp = null;

        if (target == PICKER_TYPE_HOUR) tmp = mHourPicker;
        else if (target == PICKER_TYPE_MINUTE) tmp = mMinutePicker;
        else if (target == PICKER_TYPE_SECOND) tmp = mSecondPicker;
        else if (target == PICKER_TYPE_AMPM) tmp = mAmPmPicker;

        return (tmp);
    }

    //Add by Ahan 2012/01/10 for AP WorldClock.
    /**
     * To get the slide offset for the child views.
     * @return The offset value each child view slides.
     */
    @ExportedProperty(category = "CommonControl")
    public int getTableViewSlideOffset() {
        HtcNumberPicker tmp = null;

        if (mHourPicker != null) tmp = mHourPicker;
        else if (mMinutePicker != null) tmp = mMinutePicker;
        else if (mSecondPicker != null) tmp = mSecondPicker;

        return (tmp == null ? 0 : tmp.getTableViewSlideOffset());
    }
}
