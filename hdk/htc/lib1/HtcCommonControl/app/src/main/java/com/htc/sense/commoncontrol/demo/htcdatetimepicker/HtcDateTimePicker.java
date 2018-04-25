package com.htc.sense.commoncontrol.demo.htcdatetimepicker;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;

import com.htc.lib1.cc.widget.HtcDatePicker;
import com.htc.lib1.cc.widget.HtcDatePickerDialog;
import com.htc.lib1.cc.widget.HtcTimePicker;
import com.htc.lib1.cc.widget.HtcTimePickerDialog;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class HtcDateTimePicker extends CommonDemoActivityBase {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.htcdatetimepicker_demo);

        findViewById(R.id.btn3).setOnClickListener(mHtcDateListener);
        findViewById(R.id.btn4).setOnClickListener(mHtcTimeListener);

        final HtcTimePicker htp = (HtcTimePicker) findViewById(R.id.htctimepicker);
        htp.init(11, 00, 00, null);
        htp.setSecondPickerEnable(true);
        htp.setRepeatEnable(true);
        htp.setCurrentHour(11);
        htp.setCurrentMinute(59);
        htp.setCurrentSecond(59);

        findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int offset = htp.getTableViewSlideOffset();

                if (htp.isSecondPickerEndabled())
                    htp.slideSecondWithOffset(offset);

                htp.slideMinuteWithOffset(offset);
                htp.slideHourWithOffset(offset);
            }
        });
    }

    private View.OnClickListener mHtcDateListener = new View.OnClickListener() {
        private HtcDatePickerDialog.OnDateSetListener mListener = new HtcDatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(HtcDatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Toast.makeText(view.getContext(), "Htc set date => "+year+"/"+monthOfYear+"/"+dayOfMonth, Toast.LENGTH_SHORT).show();
            }
        };

        @Override
        public void onClick(View v) {
            new HtcDatePickerDialog(v.getContext(), mListener, 1918, 2020, 1982, 8, 13, true).show();
        }
    };

    private View.OnClickListener mHtcTimeListener = new View.OnClickListener() {
        private HtcTimePickerDialog.OnTimeSetListener mListener = new HtcTimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(HtcTimePicker view, int hourOfDay, int minute, int second) {
                Toast.makeText(view.getContext(), "Htc set time => "+hourOfDay+":"+minute+":"+second, Toast.LENGTH_SHORT).show();
            }
        };

        @Override
        public void onClick(View v) {
            new HtcTimePickerDialog(v.getContext(), mListener, 19, 00, DateFormat.is24HourFormat(v.getContext())).show();
        }
    };
}
