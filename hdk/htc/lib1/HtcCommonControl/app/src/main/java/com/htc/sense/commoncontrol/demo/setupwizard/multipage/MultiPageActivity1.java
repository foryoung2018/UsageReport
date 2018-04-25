package com.htc.sense.commoncontrol.demo.setupwizard.multipage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;
import com.htc.lib1.cc.widget.setupwizard.HtcWizardActivity;
import com.htc.lib1.cc.widget.setupwizard.WizardConstants;

public class MultiPageActivity1 extends HtcWizardActivity {
    private final String CLASS_NAME = getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtil.reloadDemoTheme(this, savedInstanceState);
        CommonUtil.initHtcActionBar(this, true, true);

        hideProgress(true);
        Intent intent = getIntent();
        if (null != intent)
        {
            int progress = intent.getIntExtra(WizardConstants.INTENT_STRING_PROGRESS_BAR_NUMBER, -1);
            int max = intent.getIntExtra(WizardConstants.INTENT_STRING_PROGRESS_BAR_MAX_NUMBER, -1);
            setProgressBar(progress, max);
        }

        setTitleText(CLASS_NAME);
        setSubTitle(R.string.title_demo_multi_page);
   }

    @Override
    public void onDelayUIUpdate() {
        setSubContentView(R.layout.wizard_multi_page_activity);
        TextView desc = (TextView)findViewById(R.id.desc);
        if (desc != null) {
            desc.setText("This is the first page of multi-page demo.\n\nIt will link all the demo activities in a chain.");
        }
    }
}
