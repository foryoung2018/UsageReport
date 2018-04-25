
package com.htc.sense.commoncontrol.demo.reminder.activity;

import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase2;

public class ReminderViewDemo extends CommonDemoActivityBase2 {

    @Override
    protected Class[] getActivityList() {
        return new Class[] {
                TestActivityVoiceCall.class, TestActivityVideoCall.class, TestActivityDualCall.class
        };
    }
}
