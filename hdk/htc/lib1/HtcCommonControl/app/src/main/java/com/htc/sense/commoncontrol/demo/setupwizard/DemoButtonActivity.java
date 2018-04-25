package com.htc.sense.commoncontrol.demo.setupwizard;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;
import com.htc.lib1.cc.widget.setupwizard.HtcButtonWizardActivity;

public class DemoButtonActivity extends HtcButtonWizardActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtil.reloadDemoTheme(this, savedInstanceState);
        CommonUtil.initHtcActionBar(this, true, true);

        setProgressBar(3, 4);

        // set image
        setImage(getResources().getDrawable(R.drawable.one));

        // set description
        StringBuilder sb = new StringBuilder();
        sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
        for(int i=0; i<7; i++) {
            sb.append('\n')
              .append("desc line ")
              .append(i+1);
        }
        setDescriptionText(sb);

        // set button
        setButtonText("Button");
        setButtonOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(DemoButtonActivity.this, "button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // add custom view
        TextView tv = new TextView(this);
        tv.setText("\nThis is custom view.\n");
        tv.setTextAppearance(this, com.htc.lib1.cc.R.style.list_body_primary_l);
        tv.setBackgroundColor(0xffffff00);
        addCustomBottomView(tv);
    }
}
