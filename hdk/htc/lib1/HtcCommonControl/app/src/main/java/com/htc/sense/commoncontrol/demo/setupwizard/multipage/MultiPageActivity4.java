package com.htc.sense.commoncontrol.demo.setupwizard.multipage;

import android.content.Intent;
import android.os.Bundle;

import com.htc.sense.commoncontrol.demo.setupwizard.DemoButtonActivity;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;
import com.htc.lib1.cc.widget.setupwizard.WizardConstants;

public class MultiPageActivity4 extends DemoButtonActivity {
    private final String CLASS_NAME = getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtil.reloadDemoTheme(this, savedInstanceState);
        CommonUtil.initHtcActionBar(this, true, true);

        Intent intent = getIntent();
        if (null != intent)
        {
            int progress = intent.getIntExtra(WizardConstants.INTENT_STRING_PROGRESS_BAR_NUMBER, -1);
            int max = intent.getIntExtra(WizardConstants.INTENT_STRING_PROGRESS_BAR_MAX_NUMBER, -1);
            setProgressBar(progress, max);
        }

        setTitleText(CLASS_NAME);
   }
}
