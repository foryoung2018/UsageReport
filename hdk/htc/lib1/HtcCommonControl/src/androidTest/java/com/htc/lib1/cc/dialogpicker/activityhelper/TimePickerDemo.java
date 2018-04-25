
package com.htc.lib1.cc.dialogpicker.activityhelper;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.htc.lib1.cc.test.R;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.HtcNumberPicker;
import com.htc.lib1.cc.widget.HtcTimePicker;
import com.htc.lib1.cc.widget.HtcTimePicker.OnTimeSetListener;
import com.htc.lib1.cc.widget.HtcTimePickerDialog;

import java.util.Calendar;

public class TimePickerDemo extends DialogActivityBase implements OnTimeSetListener {
    private HtcTimePicker mHtcTimePicker;
    private TextView mTextViewShowTime;
    private Button mCreateDialog;
    private int mHour;
    private int mMinute;
    private int mSecond;
    private int mAmPm;
    private boolean is24HoursFormat;
    private Calendar mCalendar;
    private java.text.DateFormat mDateFormat;
    private static final int REPEAT = 0;
    private boolean REPEAT_ENABLE = true;
    private final int SHOW_NO_DIALOG = 0;
    private final int SHOW_DIALOG = 1;
    HtcTimePickerDialog mTimeDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HtcCommonUtil.initTheme(this, mCategoryId);
        setTitle(R.string.timepickerdemo);
        setContentView(R.layout.timepicker_layout);
        REPEAT_ENABLE = true;
        is24HoursFormat = DateFormat.is24HourFormat(this);
        mHtcTimePicker = (HtcTimePicker) findViewById(R.id.timerPicker);
        mTextViewShowTime = (TextView) findViewById(R.id.showTime);
        mCreateDialog = (Button) findViewById(R.id.timepickerdialog);
        mCreateDialog.setOnClickListener(mDialogButtonListener);
        mCalendar = Calendar.getInstance();
        mDateFormat = DateFormat.getTimeFormat(this);

        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mSecond = mCalendar.get(Calendar.SECOND);

        mHtcTimePicker.setRepeatEnable(REPEAT_ENABLE);
        mHtcTimePicker.setMinuteRange(00, 59, true);
        mHtcTimePicker.init(mHour, mMinute, mSecond, this);
        Intent i = getIntent();
        if (null == i) {
            return;
        }
        int id = i.getIntExtra("dialogId", 0);
        int hour = i.getIntExtra("hour", 10);
        int minute = i.getIntExtra("minute", 8);
        int second = i.getIntExtra("second", 8);
        show(id, hour, minute, second);
    }

    private void show(int id, int hour, int minute, int second) {
        switch (id) {
            case SHOW_NO_DIALOG:
                mHtcTimePicker.setCurrentHour(hour);
                mHtcTimePicker.setCurrentMinute(minute);
                mHtcTimePicker.setCurrentSecond(second);
                break;
            case SHOW_DIALOG:
                mTimeDialog = new HtcTimePickerDialog(TimePickerDemo.this, TimeListener, hour,
                        minute, is24HoursFormat);
                mTimeDialog.show();
                break;
            default:
                break;
        }
    }

    public void dismissWindow() {
        if (null != mTimeDialog) {
            mTimeDialog.dismiss();
        }
    }
    // for HtcTimePicker need by Activity implements OnTimeSetListener
    public void onTimeSet(HtcTimePicker view, int hourOfDay, int minute,
            int second) {
        mHour = hourOfDay;
        mMinute = minute;
        mSecond = second;
        showTimeResult();
    }

    private OnClickListener mDialogButtonListener = new OnClickListener() {

        public void onClick(View view) {
            mTimeDialog = new HtcTimePickerDialog(TimePickerDemo.this, TimeListener, mHour,
                    mMinute, is24HoursFormat);
            mTimeDialog.show();
        }

    };

    // for HtcTimePickerDialog
    private HtcTimePickerDialog.OnTimeSetListener TimeListener = new HtcTimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(HtcTimePicker view, int hourOfDay, int minute,
                int second) {
            mHour = hourOfDay;
            mMinute = minute;
            mSecond = second;
            mHtcTimePicker.setCurrentHour(mHour);
            mHtcTimePicker.setCurrentMinute(mMinute);
            mHtcTimePicker.setCurrentSecond(mSecond);
            showTimeResult();
        }
    };

    private void showTimeResult() {
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mDateFormat.format(mCalendar.getTime());

        if (!is24HoursFormat) {
            mAmPm = mHtcTimePicker.getCurrentAmPm();
        }
        mTextViewShowTime.setText(mDateFormat.format(mCalendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, REPEAT, 0, "Disable REPEAT");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (REPEAT_ENABLE)
            menu.add(0, REPEAT, 0, "Disable Repeat");
        else
            menu.add(0, REPEAT, 0, "Enable Repeat");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case REPEAT:
                if (REPEAT_ENABLE) {
                    mHtcTimePicker.setRepeatEnable(false);
                    REPEAT_ENABLE = false;
                } else {
                    mHtcTimePicker.setRepeatEnable(true);
                    REPEAT_ENABLE = true;
                }
        }
        return true;
    }

    public void improveHtcTimePickerDialogCoverage(int hour, int minute, int second) {
        HtcTimePickerDialog dialog = new HtcTimePickerDialog(TimePickerDemo.this,
                TimeListener, hour,
                minute, is24HoursFormat, true);
        dialog.onClick(null, -1);
        dialog.onSaveInstanceState();
        dialog.onTimeChanged(new HtcTimePicker(TimePickerDemo.this), hour, minute);
        dialog.onTimeSet(new HtcTimePicker(TimePickerDemo.this), hour, minute, second);
    }

    public void improveHtcTimePickerCoverage(int hour, int minute, int second) {
        HtcTimePicker picker = new HtcTimePicker(TimePickerDemo.this);
        picker.disableTitle();
        picker.getCurrentAmPm();
        picker.getCurrentHour();
        picker.getCurrentMinute();
        picker.getCurrentSecond();
        picker.getPickerChildheight();
        picker.getTableViewSlideOffset();
        picker.initPicker(hour, minute, second);
        picker.isSecondPickerEndabled();
        picker.isTheMostLeftPicker(new HtcNumberPicker(TimePickerDemo.this));
        picker.isTheMostRightPicker(new HtcNumberPicker(TimePickerDemo.this));
        picker.onDataSet(new HtcNumberPicker(TimePickerDemo.this), -1);
        picker.onKeyDown(KeyEvent.KEYCODE_ENTER, null);
        picker.onKeyUp(KeyEvent.KEYCODE_ENTER, null);
        picker.setCountDownMode(true);
        picker.setCustomShadow(1, 1, 1, 1, -1);
        picker.setEnabled(true);
        picker.setHourPickerTitle("hour");
        picker.setHourPickerTitle("hour", -1);
        picker.setHourPickerTitle("hour", -1, null);
        picker.setMinutePickerTitle("minute");
        picker.setMinutePickerTitle("minute", -1);
        picker.setMinutePickerTitle("minute", -1, null);
        picker.setOnScrollIdleStateListener(null);
        picker.setPickerTextColor(-1, -1);
        picker.setSecondPickerEnable(true);
        picker.setSecondPickerEnable(true, true);
        picker.setSecondPickerTitle("second");
        picker.setSecondPickerTitle("second", -1);
        picker.setSecondPickerTitle("second", -1, null);
        picker.slideHourWithOffset(0);
        picker.slideMinuteWithOffset(0);
        picker.slideSecondWithOffset(0);
    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }
}
