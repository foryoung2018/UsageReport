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

import java.util.Calendar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.htc.lib1.cc.R;

/**
 * A dialog that prompts the user for the time of day using a {@link HtcTimePicker}.
 */
public class HtcTimePickerDialog extends HtcAlertDialog implements OnClickListener, HtcTimePicker.OnTimeSetListener{

    private static final String TAG = "HtcTimePickerDialog";
    /**
     * The callback interface used to indicate the user is done filling in
     * the time (they clicked on the 'Set' button).
     */
    public interface OnTimeSetListener {

        /**
         * Callback when selected time has been changed.
         * @param view The view associated with this listener.
         * @param hourOfDay The hour that was set.
         * @param minute The minute that was set.
     * @param second The second that was set
         */
        void onTimeSet(HtcTimePicker view, int hourOfDay, int minute, int second);
    }

    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String IS_24_HOUR = "is24hour";

    private final HtcTimePicker mTimePicker;
    private final OnTimeSetListener mCallback;
    private final Calendar mCalendar;
    private final java.text.DateFormat mDateFormat;

    int mInitialHourOfDay;
    int mInitialMinute;
    int mInitialSecond;
    boolean mIs24HourView;

    Handler mHandler = new Handler();
    boolean mInitTimePickerDialog;

    /**
     * Constructor.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param callBack How parent is notified.
     * @param hourOfDay The initial hour.
     * @param minute The initial minute.
     * @param is24HourView Whether this is a 24 hour view, or AM/PM.
     * @param repeatEnable Whether this is in repeat-able mode or not.
     */
    public HtcTimePickerDialog(Context context,
            OnTimeSetListener callBack,
            int hourOfDay, int minute, boolean is24HourView, boolean repeatEnable) {
        this(context, 0, callBack, hourOfDay, minute, is24HourView);
        if(mTimePicker != null)
            mTimePicker.setRepeatEnable(repeatEnable);
        setInverseBackgroundForced(true);
    }

    /**
     * Constructor.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param callBack How parent is notified.
     * @param hourOfDay The initial hour.
     * @param minute The initial minute.
     * @param is24HourView Whether this is a 24 hour view, or AM/PM.
     */
    public HtcTimePickerDialog(Context context,
            OnTimeSetListener callBack,
            int hourOfDay, int minute, boolean is24HourView) {
        this(context, 0, callBack, hourOfDay, minute, is24HourView);
        setInverseBackgroundForced(true);
    }

    /**
     * Constructor.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param theme the theme to apply to this dialog
     * @param callBack How parent is notified
     * @param hourOfDay The initial hour.
     * @param minute The initial minute.
     * @param is24HourView Whether this is a 24 hour view, or AM/PM.
     */
    public HtcTimePickerDialog(Context context,
            int theme,
            OnTimeSetListener callBack,
            int hourOfDay, int minute, boolean is24HourView) {
        super(context);
        //Log.i(TAG, "HtcTimePickerDialog> hourOfDay:"+hourOfDay + " minute:"+minute +" is24HourView:"+is24HourView);

        mCallback = callBack;
        mInitialHourOfDay = hourOfDay;
        mInitialMinute = minute;
        mIs24HourView = is24HourView;

        mDateFormat = DateFormat.getTimeFormat(context);
        mCalendar = Calendar.getInstance();
        updateTitle(mInitialHourOfDay, mInitialMinute);

        setButton(context.getText(android.R.string.ok), this);
        setButton2(context.getText(android.R.string.cancel), (OnClickListener) null);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.htc_time_picker_dialog, null);
        setView(view);

        setInverseBackgroundForced(true);

        mTimePicker = (HtcTimePicker) view.findViewById(R.id.timePicker);
        mInitTimePickerDialog = true;

        if(mTimePicker != null) {
            //Log.i(TAG, "mTimePicker is not null");

            mTimePicker.setMinuteRange(0, 59, true);
            mTimePicker.setRepeatEnable(true);
//            if(is24HourView) {
//                mTimePicker.setHourRange(0, 23);
//            } else {
//                mTimePicker.setHourRange(1, 12);
//            }

            // initialize state
            //mTimePicker.setCurrentHour(mInitialHourOfDay-1);
            //mTimePicker.setCurrentMinute(mInitialMinute-1);
            //mTimePicker.setIs24HourView(mIs24HourView);
            //updateTime();
            //mTimePicker.setOnTimeChangedListener(this);
            //mTimePicker.dispatchOnScrollIdleStateListener(this) ;

            //mTimePicker.setCurrentHour(mInitialHourOfDay);
            //mTimePicker.setCurrentMinute(mInitialMinute) ;
        updateTime(mInitialHourOfDay, mInitialMinute, 0);

            mTimePicker.requestFocus();
        } else {
            Log.e(TAG, "can't find mTimePicker(R.id.timePicker)");
        }
        //Log.i(TAG, "HtcTimePickerDialog<");
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    private void updateTime() {}

/**
 * This method will be invoked when a button in the dialog is clicked.
 * @param dialog The dialog that received the click.
 * @param which The button that was clicked (e.g. BUTTON1) or the position of the item clicked.
 * @hide
 */
    public void onClick(DialogInterface dialog, int which) {
        if (mCallback != null) {
            mTimePicker.clearFocus();
        //Log.i("timepicker","dialog onClick h:"+mTimePicker.getCurrentHour()+", m:"
                   // +mTimePicker.getCurrentMinute()+" ,s:"+mTimePicker.getCurrentSecond());
        if(!mTimePicker.isSecondPickerEndabled()) {
        mCallback.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                    mTimePicker.getCurrentMinute(), 0);
        }else {
                mCallback.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                    mTimePicker.getCurrentMinute(), mTimePicker.getCurrentSecond());
        }
        }
    }

    /**
     * To set the new time of HtcTimePicker.
     * @param hourOfDay The hour that will be set.
     * @param minuteOfHour The minute that will be set.
     * @param second The second that will be set
     */
    public void updateTime(int hourOfDay, int minuteOfHour, int second) {
    mTimePicker.init(hourOfDay, minuteOfHour, second, this);
        //mTimePicker.setCurrentHour(hourOfDay);
        //mTimePicker.setCurrentMinute(minutOfHour);
    }

    private void updateTitle(int hour, int minute) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minute);
        setTitle(mDateFormat.format(mCalendar.getTime()));
        mAlert.setTitleCenterEnabled(false);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(HOUR, mTimePicker.getCurrentHour());
        state.putInt(MINUTE, mTimePicker.getCurrentMinute());
        //state.putBoolean(IS_24_HOUR, mTimePicker.is24HourView());
        return state;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int hour = savedInstanceState.getInt(HOUR);
        int minute = savedInstanceState.getInt(MINUTE);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);
        //mTimePicker.setIs24HourView(savedInstanceState.getBoolean(IS_24_HOUR));
        //mTimePicker.setOnTimeChangedListener(this);
        updateTitle(hour, minute);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void onTimeChanged(HtcTimePicker view, int hourOfDay, int minute) {
        updateTitle(hourOfDay, minute);
    }

        /**
         * @param view The view associated with this listener.
         * @param hourOfDay The hour that was set.
         * @param minute The minute that was set.
         * @param second The second that was set
         * @hide
         */
    public void onTimeSet(HtcTimePicker view, int hourOfDay, int minute, int second) {
        if( null != view) {
            updateTitle(view.getCurrentHour(), view.getCurrentMinute());
        }
    }
}
