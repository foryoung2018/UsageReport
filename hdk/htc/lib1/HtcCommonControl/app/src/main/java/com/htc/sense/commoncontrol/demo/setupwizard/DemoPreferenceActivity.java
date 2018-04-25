package com.htc.sense.commoncontrol.demo.setupwizard;

import android.os.Bundle;

import com.htc.lib1.cc.widget.setupwizard.HtcWizardActivity;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

public class DemoPreferenceActivity extends HtcWizardActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState,false);
        CommonUtil.reloadDemoTheme(this, savedInstanceState);
        CommonUtil.initHtcActionBar(this, true, true);
    }
}
