
package com.htc.lib1.cc.dialogpicker.activityhelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
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
import com.htc.lib1.cc.widget.HtcDatePicker;
import com.htc.lib1.cc.widget.HtcDatePicker.OnDateChangedListener;
import com.htc.lib1.cc.widget.HtcDatePickerDialog;
import com.htc.lib1.cc.widget.HtcDatePickerDialog.OnDateSetListener;
import com.htc.lib1.cc.widget.HtcNumberPicker;

import java.util.Calendar;

public class DatePickerDemo extends DialogActivityBase implements OnDateChangedListener {
    private static final int REPEAT = 0;
    private boolean REPEAT_ENABLE = true;
    private final static int START_YEAR = 2000;
    private final static int END_YEAR = 2030;

    private Calendar mCalendar;
    private HtcDatePicker mHtcDatePicker;
    private TextView mTextViewShowDate;
    private Button mCreateDialog, mCreditDialog;
    HtcDatePickerDialog mDialog;

    private int mYear;
    private int mMonth;
    private int mDay;

    private final int SHOW_NO_DIALOG = 0;
    private final int SHOW_DIALOG = 1;
    private final int SHOW_DIALOG_CREDIT_CARD = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HtcCommonUtil.initTheme(this, mCategoryId);
        setTitle(R.string.datepickerdemo);
        setContentView(R.layout.datepicker_layout);

        mHtcDatePicker = (HtcDatePicker) findViewById(R.id.datePicker);
        mTextViewShowDate = (TextView) findViewById(R.id.showDate);
        mCreateDialog = (Button) findViewById(R.id.datepickerdialog);
        mCreditDialog = (Button) findViewById(R.id.datepickerdialog2);

        mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DATE);

        mHtcDatePicker.setRepeatEnable(REPEAT_ENABLE);
        mHtcDatePicker.setYearRange(2000, 2030);
        mHtcDatePicker.init(mYear, mMonth, mDay, this);
        Intent i = getIntent();
        if (null == i) {
            return;
        }
        int id = i.getIntExtra("dialogId", 0);
        int year = i.getIntExtra("year", 2014);
        int month = i.getIntExtra("month", 8);
        int day = i.getIntExtra("day", 20);
        show(id, year, month, day);
    }

    private void show(int id, int year, int month, int day) {
        switch (id) {
            case SHOW_NO_DIALOG:
                mHtcDatePicker.setCurrentYear(year);
                mHtcDatePicker.setCurrentMonth(month);
                mHtcDatePicker.setCurrentDay(day);
                break;
            case SHOW_DIALOG:
                mDialog = new HtcDatePickerDialog(DatePickerDemo.this, mDateSetListener,
                        START_YEAR, END_YEAR, year, month - 1, day, true);
                mDialog.show();
                break;
            case SHOW_DIALOG_CREDIT_CARD:
                mDialog = new HtcDatePickerDialog(DatePickerDemo.this, mDateSetListener,
                        START_YEAR, END_YEAR, year, month - 1, "My");
                mDialog.show();
                break;
            default:
                break;
        }
    }

    // Listener for HtcDatePicker
    public void onDateChanged(HtcDatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;

        showDate(DatePickerDemo.this);
    }

    // HtcDatePickerDialog - General form
    private OnClickListener mDialogButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            mDialog = new HtcDatePickerDialog(DatePickerDemo.this, mDateSetListener,
                    START_YEAR, END_YEAR, mYear, mMonth, mDay, true);
            mDialog.show();
        }
    };

    // Credit card form - Month/Year
    private OnClickListener mCreditDialogListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mDialog = new HtcDatePickerDialog(DatePickerDemo.this, mDateSetListener,
                    START_YEAR, END_YEAR, mYear, mMonth, "My");
            mDialog.show();
        }
    };

    // Listener for HtcDatePickerDialog
    private OnDateSetListener mDateSetListener = new OnDateSetListener() {

        public void onDateSet(HtcDatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;

            mHtcDatePicker.setCurrentYear(mYear);
            mHtcDatePicker.setCurrentMonth(mMonth + 1);
            mHtcDatePicker.setCurrentDay(mDay);

            showDate(DatePickerDemo.this);
        }

    };

    private void showDate(Context context) {
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.MONTH, mMonth);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);

        String format = "";
        format = Settings.System.getString(context.getContentResolver(),
                Settings.System.DATE_FORMAT);
        if (TextUtils.isEmpty(format)) {
            format = "EE, MMM dd, yyyy";
        }
        mTextViewShowDate.setText(DateFormat.format(format, mCalendar));
    }

    // =====================Menu=====================//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, REPEAT, 0, "Disable Repeat");
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
                    mHtcDatePicker.setRepeatEnable(false);
                    REPEAT_ENABLE = false;
                } else {
                    mHtcDatePicker.setRepeatEnable(true);
                    REPEAT_ENABLE = true;
                }
        }
        return true;
    }

    public void improveHtcDatePickerDialogCoverage(int year, int month, int day) {
        HtcDatePickerDialog dialog = new HtcDatePickerDialog(DatePickerDemo.this,
                mDateSetListener, year, month - 1, day);
        dialog.onDataSet(new HtcNumberPicker(DatePickerDemo.this), -1);
        dialog.onClick(null, -1);
        dialog.onDateChanged(null, year, month, day);
        dialog.onSaveInstanceState();
        dialog.setTitle("titile");
        dialog.updateDate(year, month, day);
    }

    public void improveHtcDatePickerCoverage(int year, int month, int day) {
        HtcDatePicker picker = new HtcDatePicker(DatePickerDemo.this);
        picker.disableTitle();
        picker.getCurrentDay();
        picker.getCurrentMonth();
        picker.getCurrentYear();
        picker.initPicker(year, month, day);
        picker.isTheMostLeftPicker(new HtcNumberPicker(DatePickerDemo.this));
        picker.isTheMostRightPicker(new HtcNumberPicker(DatePickerDemo.this));
        picker.onDataSet(new HtcNumberPicker(DatePickerDemo.this), 0);
        picker.onKeyDown(KeyEvent.KEYCODE_ENTER, null);
        picker.onKeyUp(KeyEvent.KEYCODE_ENTER, null);
        picker.setDayPickerTitle("day");
        picker.setDayRange(1, 31);
        picker.setMonthPickerTitle("month");
        picker.setPickerTextColor(HtcDatePicker.PICKER_TYPE_DAY, -1);
        picker.setOnScrollIdleStateListener(null);
        picker.setYearPickerTitle("year");
        picker.releaseResource();
    }

    public void dismissWindow() {
        if (null != mDialog) {
            mDialog.dismiss();
        }
    }
    @Override
    protected boolean isInitOrientation() {
        return false;
    }
}
