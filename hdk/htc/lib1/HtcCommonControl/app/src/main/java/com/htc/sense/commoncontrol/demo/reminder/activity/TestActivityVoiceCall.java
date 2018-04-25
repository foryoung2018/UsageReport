package com.htc.sense.commoncontrol.demo.reminder.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.reminder.ui.TestViewVoiceCall;

public class TestActivityVoiceCall extends CommonDemoActivityBase {

    TestViewVoiceCall mTestView;
    int mViewMode;
    String TAG = "TestActivityCall";
    Handler mUIHandler = new Handler();

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        // TODO: Window Flag:
        // FLAG_TURN_SCREEN_ON: once the window has been shown then the system will poke the power manager's user activity to turn the screen on.
        // FLAG_KEEP_SCREEN_ON: keep the device's screen turned on and bright.
        // For more detail, please refer to
        // http://developer.android.com/reference/android/view/WindowManager.LayoutParams.html
        //
        // Because Google already deprecated FULL_WAKE_LOCK
        // and we should use FLAG_KEEP_SCREEN_ON instead of this type of wake lock.
        // http://developer.android.com/reference/android/os/PowerManager.html
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // TODO: case1: one Tile with two Buttons.
        setContentView(R.layout.main_voicecall);
        mTestView = (TestViewVoiceCall) this.findViewById(R.id.test_reminder_view);
        mTestView.setFitsSystemWindows(true);

        // TODO:
        // Please have to set Reminder View.
        // Then, ReminderActivity will handle the default action.
//        this.setReminderView(mTestView);

        if (mTestView != null) {
//            mViewMode = mTestView.getViewMode();
            mTestView.setCallback(new TestViewVoiceCall.Callback() {
                @Override
                public void onTileDrop() {
                }
                @Override
                public void onTileDropEnd() {
                    // TODO: just an example
                    finish();
                }
                @Override
                public void onButtonDrop(boolean isDeclineCall) {
                }
                @Override
                public void onButtonDropEnd(boolean isDeclineCall) {
                    finish();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTestView.cleanUp();
    }
}
