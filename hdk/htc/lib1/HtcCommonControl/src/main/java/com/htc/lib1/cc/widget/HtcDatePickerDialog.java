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

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;

import com.htc.lib1.cc.widget.HtcDatePicker.OnDateChangedListener;
import com.htc.lib1.cc.R;

/**
 * A simple dialog containing an {@link HtcDatePicker}.
 */
public class HtcDatePickerDialog extends HtcAlertDialog implements OnClickListener,
        OnDateChangedListener, HtcNumberPicker.OnScrollIdleStateListener  {
    private static final String TAG = "HtcDatePickerDialog";

    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";

    private final HtcDatePicker mDatePicker;
    private final OnDateSetListener mCallBack;
    private final Calendar mCalendar;
    private final String[] mWeekDays;

    @ExportedProperty(category = "CommonControl")
    private int mInitialYear;

    @ExportedProperty(category = "CommonControl")
    private int mInitialMonth;

    @ExportedProperty(category = "CommonControl")
    private int mInitialDay;

    Handler mHandler = new Handler();
    boolean mInitDatePickerDialog;

    @ExportedProperty(category = "CommonControl")
    boolean isWithoutDayPicker = false;

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {

        /**
         * Callback when new date has been set.
         * @param view The view associated with this listener.
         * @param year The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility with {@link java.util.Calendar}.
         * @param dayOfMonth The day of the month that was set.
         */
        void onDateSet(HtcDatePicker view, int year, int monthOfYear, int dayOfMonth);
    }



    /**
     * Constructor.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param callBack How the parent is notified that the date is set.
     * @param yearStart The start year of the dialog.
     * @param yearEnd The End year of the dialog.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dateOrder The date order of the dialog.
     */
    public HtcDatePickerDialog(Context context,
            OnDateSetListener callBack,
        int yearStart,
        int yearEnd,
            int year,
            int monthOfYear,
            String dateOrder) {
        this(context, 0, callBack, yearStart, yearEnd, year, monthOfYear, 30, true);
        if(mDatePicker!=null) {
             mDatePicker.setPickersOrder(dateOrder);
        }
        this.updateTitleWithoutDay(year,monthOfYear);
        isWithoutDayPicker = true;
        setInverseBackgroundForced(true);
    }

    /**
     * Constructor.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param callBack How the parent is notified that the date is set.
     * @param yearStart The start year of the dialog.
     * @param yearEnd The End year of the dialog.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     * @param repeatEnable whether it is in repeat-able mode or not.
     */
    public HtcDatePickerDialog(Context context,
            OnDateSetListener callBack,
        int yearStart,
        int yearEnd,
            int year,
            int monthOfYear,
            int dayOfMonth,
        boolean repeatEnable) {
        this(context, 0, callBack, yearStart, yearEnd, year, monthOfYear, dayOfMonth, repeatEnable);
        setInverseBackgroundForced(true);
    }

    /**
     * Constructor.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param callBack How the parent is notified that the date is set.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     */
    public HtcDatePickerDialog(Context context,
            OnDateSetListener callBack,
            int year,
            int monthOfYear,
            int dayOfMonth) {
        this(context, 0, callBack, year, monthOfYear, dayOfMonth);
    }

    /**
     * Constructor.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param theme the theme to apply to this dialog
     * @param callBack How the parent is notified that the date is set.
     * @param yearStart The start year of the dialog.
     * @param yearEnd The End year of the dialog.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     * @param repeatEnable whether it is in repeat-able mode or not.
     */
    public HtcDatePickerDialog(Context context,
            int theme,
            OnDateSetListener callBack,
        int yearStart,
        int yearEnd,
            int year,
            int monthOfYear,
            int dayOfMonth,
        boolean repeatEnable) {
        super(context);
        mCallBack = callBack;
        mInitialYear = year;
        mInitialMonth = monthOfYear;
        mInitialDay = dayOfMonth;
        DateFormatSymbols symbols = new DateFormatSymbols();
        mWeekDays = symbols.getShortWeekdays();
        mCalendar = Calendar.getInstance();
        //Log.i(TAG, "------------->> HtcDatePickerDialog");
        updateTitle(mInitialYear, mInitialMonth, mInitialDay);

        setButton(context.getText(android.R.string.ok), this);
        setButton2(context.getText(android.R.string.cancel), (OnClickListener) null);

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.htc_date_picker_dialog, null);
        setView(view);

        setInverseBackgroundForced(true);

        mDatePicker = (HtcDatePicker) view.findViewById(R.id.datePicker);
    mDatePicker.setYearRange(yearStart, yearEnd);
        mDatePicker.setRepeatEnable(repeatEnable);
        mInitDatePickerDialog = true;

        //updateDate();
    updateDateInternal(mInitialYear, mInitialMonth, mInitialDay);
        //mDatePicker.dispatchOnScrollIdleStateListener(this) ;
        mDatePicker.requestFocus();
        //Log.i(TAG, "-------------<< HtcDatePickerDialog");
    }

    /**
     * Constructor.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param theme the theme to apply to this dialog
     * @param callBack How the parent is notified that the date is set.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     */
    public HtcDatePickerDialog(Context context,
            int theme,
            OnDateSetListener callBack,
            int year,
            int monthOfYear,
            int dayOfMonth) {
        super(context);
        mCallBack = callBack;
        mInitialYear = year;
        mInitialMonth = monthOfYear;
        mInitialDay = dayOfMonth;
        DateFormatSymbols symbols = new DateFormatSymbols();
        mWeekDays = symbols.getShortWeekdays();
        mCalendar = Calendar.getInstance();
        //Log.i(TAG, "------------->> HtcDatePickerDialog");
        updateTitle(mInitialYear, mInitialMonth, mInitialDay);

        setButton(context.getText(android.R.string.ok), this);
        setButton2(context.getText(android.R.string.cancel), (OnClickListener) null);

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.htc_date_picker_dialog, null);
        setView(view);

        setInverseBackgroundForced(true);

        mDatePicker = (HtcDatePicker) view.findViewById(R.id.datePicker);
        mDatePicker.setRepeatEnable(true);
        mInitDatePickerDialog = true;

        //updateDate();
    updateDateInternal(mInitialYear, mInitialMonth, mInitialDay);
        //mDatePicker.dispatchOnScrollIdleStateListener(this) ;
        mDatePicker.requestFocus();
        //Log.i(TAG, "-------------<< HtcDatePickerDialog");
    }

    private void updateDateInternal(int year, int month, int day) {
        //Log.i(TAG,"updateDateInternal ");
        mDatePicker.init(mInitialYear, mInitialMonth, mInitialDay, this);
    }

    private void updateDate() {
        //Log.i(TAG,"updateDate ");

        mHandler.postDelayed(new Runnable(){
            public void run(){
                updateDateInternal(mInitialYear, mInitialMonth, mInitialDay);
            }
        }, 100);

/*
        Animation an = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
        an.setDuration(50);
        an.setFillBefore(false);
        an.setAnimationListener(new AnimationListener(){
            public void onAnimationEnd(Animation animation) {
                mDatePicker.setCurrentYear(mInitialYear);
                mDatePicker.setCurrentMonth(mInitialMonth+1);
                mDatePicker.setCurrentDay(mInitialDay);
                //Log.i(TAG, "--onAnimationEnd-- mInitialMonth:"+mInitialMonth);
                updateTitle(mInitialYear, mInitialMonth, mInitialDay);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {
                //mDatePicker.setVisibility(View.INVISIBLE);
            }

        });

        if (mInitDatePickerDialog) {
            mDatePicker.setAnimation(an);
            mDatePicker.startAnimation(an);
            mInitDatePickerDialog = false;
        } else {
            mDatePicker.setCurrentYear(mInitialYear);
            mDatePicker.setCurrentMonth(mInitialMonth);
            mDatePicker.setCurrentDay(mInitialDay);
            mDatePicker.setVisibility(View.VISIBLE);
        }
*/
    }

/**
 * This method will be invoked when a button in the dialog is clicked.
 * @param dialog The dialog that received the click.
 * @param which The button that was clicked (e.g. BUTTON1) or the position of the item clicked.
 */
    public void onClick(DialogInterface dialog, int which) {
        //Log.i(TAG, "onClick");
        if (mCallBack != null) {
            mDatePicker.clearFocus();
            mCallBack.onDateSet(mDatePicker, mDatePicker.getCurrentYear(),
                    mDatePicker.getCurrentMonth()-1, mDatePicker.getCurrentDay());
        }
    }

    /**
     * To set new selected date.
     * @param year The year that was set.
     * @param monthOfYear The month of year that was set.
     * @param dayOfMonth The year that was set.
     */
    public void updateDate(int year, int monthOfYear, int dayOfMonth) {
        //Log.i(TAG, "updateDate: year="+year+" monthOfYear:"+monthOfYear+" dayOfMonth:"+dayOfMonth);
        mInitialYear = year;
        mInitialMonth = monthOfYear;
        mInitialDay = dayOfMonth;
        //mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
        mDatePicker.setCurrentYear(mInitialYear);
        mDatePicker.setCurrentMonth(mInitialMonth);
        mDatePicker.setCurrentDay(mInitialDay);
        //S ##add by Danny@hTC 090218
        updateTitle(year, monthOfYear-1, dayOfMonth);
        //E ##add by Danny@hTC 090218
    }

    @ExportedProperty(category = "CommonControl")
    private boolean isAutoUpdateTitle = true;

    /**
     * To set the title of this dialog.
     * @param title The title string.
     */
    @Override
    public void setTitle(CharSequence title) {
           isAutoUpdateTitle = false;
       super.setTitle(title);
        mAlert.setTitleCenterEnabled(false);
    }

    private void updateTitleWithoutDay(int year, int month){
        if(!isAutoUpdateTitle) return;

        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        String format = "";
        format = "MMM, yyyy";

        super.setTitle(DateFormat.format(format,mCalendar));
        mAlert.setTitleCenterEnabled(false);
    }

    private void updateTitle(int year, int month, int day) {
        //Log.i(TAG, "updateTitle: year="+year+" month:"+month+" day:"+day);
        /*mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, day);*/

        if(!isAutoUpdateTitle) return;

        month++;
        String monthStr = month+"";
        String dayStr = day+"";

        if(monthStr.length()==1) monthStr = "0"+ monthStr;
        if(dayStr.length()==1) dayStr = "0"+ dayStr;
        String dateStr =year+"-"+monthStr+"-"+dayStr;
        //Log.i("test",dateStr);
        parseEventUsingCalendar(dateStr, mCalendar);
//        String weekday = mWeekDays[mCalendar.get(Calendar.DAY_OF_WEEK)];
        String format = "";
        format = Settings.System.getString(getContext().getContentResolver(),Settings.System.DATE_FORMAT);
        if(TextUtils.isEmpty(format)) {
            format = "EE, MMM dd, yyyy";
        }
        super.setTitle(DateFormat.format(format,mCalendar));
        mAlert.setTitleCenterEnabled(false);
    }

    private boolean parseEventUsingCalendar(String timeInRfc3339, java.util.Calendar calendar) {
        if(TextUtils.isEmpty(timeInRfc3339)) {
            throw new IllegalArgumentException("Illegal RFC3339 format");
        }
        // Remove anything after T if it existed
        String processStr = "";
        int indexOfT = timeInRfc3339.indexOf("T");
        if(indexOfT > 0) {
            processStr = timeInRfc3339.substring(0, indexOfT);
        }
        else {
            processStr = timeInRfc3339;
        }
        // Split the remaining string using '-' character
        String[] splitStr = processStr.split("-");
        // This should give us either an array of 3 or 4 items depend on the
        // format of timeInRfc3339
        // If the string started with --M-D, an array of 4 items will be
        // returned with first 2 items empty, M and D at the 3rd and 4th
        // position respectively.
        // If the string started with Y-M-D, an array of 3 items will be
        // returned with Y, M and D filled in the 1st, 2nd and 3rd position.
        final int numOfItem = splitStr.length;
        boolean hasYearInfo = false;
        if(numOfItem == 3) {
            calendar.set(
                Integer.parseInt(splitStr[0]),
                Integer.parseInt(splitStr[1])-1,
                Integer.parseInt(splitStr[2]),
                12, 0, 0);
            calendar.set(java.util.Calendar.MILLISECOND, 0);
            hasYearInfo = true;
        }
        else if(numOfItem == 4) {
            // Year is missing so it might be an anniversary type of event,
            // let's set year to 1970 first and note this information somewhere
            // else
            calendar.set(
                1970,
                Integer.parseInt(splitStr[2])-1,
                Integer.parseInt(splitStr[3]),
                12, 0, 0);
            calendar.set(java.util.Calendar.MILLISECOND, 0);
            hasYearInfo = false;
        }
        return hasYearInfo;
    }

    /**
     * Saves the state of the dialog into a bundle.
     * @return A bundle with the state of the dialog.
     */
    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, mDatePicker.getCurrentYear());
        state.putInt(MONTH, mDatePicker.getCurrentMonth());
        state.putInt(DAY, mDatePicker.getCurrentDay());
        return state;
    }

    /**
     * Restore the state of the dialog from a previously saved bundle.
     * @param savedInstanceState The state of the dialog previously saved by onSaveInstanceState().
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int year = savedInstanceState.getInt(YEAR);
        int month = savedInstanceState.getInt(MONTH);
        int day = savedInstanceState.getInt(DAY);
        mDatePicker.init(year, month, day, this);
//        updateTitle(year, month-1, day);
    }

    /**
     * Called upon a date change.
     * @param view The view associated with this listener.
     * @param year The year that was set.
     * @param month The month that was set.
     * @param day The day of the month that was set.
     */
    public void onDateChanged(HtcDatePicker view, int year, int month, int day) {
        //Log.i(TAG, "--onDateChanged-- month:"+month);
        if(!isWithoutDayPicker) updateTitle(year, month, day);
        else updateTitleWithoutDay(year, month);
    }

    private void adjustDayNumber(int year, int month, int day) {
        //Log.i(TAG, ">>>>>> adjustDayNumber ("+month+","+day+")");
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //Log.i(TAG, "max:"+max);
        if( day > max ) {
            day = max;
        }
        //mDatePicker.init(year, month, day, null);
        mInitialYear = year;
        mInitialMonth = month;
        mInitialDay = day;
        mHandler.postDelayed(new Runnable(){
            public void run(){
                updateDateInternal(mInitialYear, mInitialMonth, mInitialDay);
            }
        }, 50);

        //Log.i(TAG, "<<<<<< adjustDayNumber ("+year+","+month+","+day+")");
    }

    /**
     * Callback method for the center view of the related HtcNumberPicker has changed.
     * @param target The related HtcNumberPicker.
     * @param data The data of the new center view.
     */
    public void onDataSet(HtcNumberPicker target, int data) {
        Log.i(TAG, "onDataSet >>> ");
        if (null != target) {
            //updateDate(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
            adjustDayNumber(mDatePicker.getCurrentYear(), mDatePicker.getCurrentMonth()-1, mDatePicker.getCurrentDay());
            //Log.i(TAG, "mInitial year:"+mInitialYear+" month:"+mInitialMonth+" day:"+mInitialDay);
            //Log.i(TAG, "mDatePicker year:"+mDatePicker.getYear()+" month:"+mDatePicker.getMonth()+" day:"+mDatePicker.getDayOfMonth());
            if( mInitialYear != mDatePicker.getCurrentYear() || mInitialMonth != mDatePicker.getCurrentDay() || mInitialDay != mDatePicker.getCurrentDay()) {
                if(!isWithoutDayPicker) updateTitle(mDatePicker.getCurrentYear(), mDatePicker.getCurrentMonth()-1, mDatePicker.getCurrentDay());
                else updateTitleWithoutDay(mDatePicker.getCurrentYear(), mDatePicker.getCurrentMonth()-1);
            }
        }
        //Log.i(TAG, "onDataSet <<< ");
    }
}
