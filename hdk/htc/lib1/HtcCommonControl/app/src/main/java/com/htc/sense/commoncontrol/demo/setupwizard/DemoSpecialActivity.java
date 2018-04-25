package com.htc.sense.commoncontrol.demo.setupwizard;

import android.os.Bundle;
import android.widget.TextView;

import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;
import com.htc.lib1.cc.widget.setupwizard.HtcSpecialWizardActivity;

public class DemoSpecialActivity extends HtcSpecialWizardActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtil.reloadDemoTheme(this, savedInstanceState);
        CommonUtil.initHtcActionBar(this, true, true);

        hideProgress(true);

        setImage(R.drawable.one);
        // set description
        StringBuilder sb = new StringBuilder();
        sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
        for(int i=0; i<5; i++) {
            sb.append('\n')
              .append("desc line ")
              .append(i+1);
        }
        setDescriptionText(sb);
        TextView tv = new TextView(this);
        tv.setText("\nThis is custom view.\n");
        tv.setTextAppearance(this, com.htc.lib1.cc.R.style.list_body_primary_l);
        tv.setBackgroundColor(0xffffff00);
        addCustomBottomView(tv);
    }
}
