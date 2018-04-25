package com.htc.sense.commoncontrol.demo.progressbar;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.R.id;
import com.htc.sense.commoncontrol.demo.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ProgressBarSeekBarDemo extends CommonDemoActivityBase {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progressbar_seekbar);

        SeekBar.OnSeekBarChangeListener SwitchLisntener = new OnSeekBarChangeListener() {
            /**
             * Because of replacing the user's listener, need to implement the
             * onProgressChange by just calling the user's onProgressChanged
             *
             * @see android.widget.SeekBar.OnSeekBarChangeListener#onProgressChanged(android.widget.SeekBar,
             *      int, boolean)
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                // TODO Auto-generated method stub
                seekBar.setSecondaryProgress((seekBar.getProgress() + 10));
            }

            /**
             * Replace the thin progressDrawable with the thick progressDrawable
             * before the user's onStartTrackingTouch
             *
             * @see android.widget.SeekBar.OnSeekBarChangeListener#onStartTrackingTouch(android.widget.SeekBar)
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            /**
             * Replace the thick progressDrawable with the think
             * progressDrawable after the user's onStopTrackingTouch
             *
             * @see android.widget.SeekBar.OnSeekBarChangeListener#onStopTrackingTouch(android.widget.SeekBar)
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

        };

        SeekBar sb = (SeekBar) findViewById(R.id.HtcSeekBar_default);
        sb.setOnSeekBarChangeListener(SwitchLisntener);
        sb = (SeekBar) findViewById(R.id.HtcSeekBar2_2);
        sb.setOnSeekBarChangeListener(SwitchLisntener);
        sb = (SeekBar) findViewById(R.id.SeekBar1);
        sb.setOnSeekBarChangeListener(SwitchLisntener);
    }
}
