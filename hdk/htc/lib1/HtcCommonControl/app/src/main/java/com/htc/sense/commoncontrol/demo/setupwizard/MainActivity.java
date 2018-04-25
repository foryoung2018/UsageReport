package com.htc.sense.commoncontrol.demo.setupwizard;

import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase2;
import com.htc.sense.commoncontrol.demo.setupwizard.multipage.MultiPageMainActivity;

public class MainActivity extends CommonDemoActivityBase2 {

    @Override
    protected Class[] getActivityList() {
        return new Class[] {
                DemoActivity1.class, DemoButtonActivity.class, DemoPreferenceActivity.class, DemoRadioListActivity.class, DemoSpecialActivity.class, MultiPageMainActivity.class
        };
    }
}
