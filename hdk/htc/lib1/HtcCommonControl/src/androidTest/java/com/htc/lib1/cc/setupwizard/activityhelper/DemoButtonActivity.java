package com.htc.lib1.cc.setupwizard.activityhelper;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.htc.lib1.cc.test.R;

import com.htc.lib1.cc.widget.setupwizard.HtcButtonWizardActivity;
import com.htc.lib1.cc.setupwizard.activityhelper.util.SetupWizardUtil;

public class DemoButtonActivity extends HtcButtonWizardActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SetupWizardUtil.initThemeAndCategory(this);
        super.onCreate(savedInstanceState);

        onDelayUIUpdate();
        setTitleText("Action Bar Title");
        setSubTitle(R.string.title_demo_button_activity);
        setProgressBar(4, 5);

        setImage(R.drawable.one);
        setImage(getResources().getDrawable(R.drawable.one));

        StringBuilder sb = new StringBuilder();
        sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
        for(int i=0; i<7; i++) {
            sb.append('\n')
              .append("desc line ")
              .append(i+1);
        }
        setDescriptionText(R.string.am);
        setDescriptionText(sb);

        setButtonText(R.string.button_hide_caption);
        setButtonText("Button");

        TextView tv = new TextView(this);
        tv.setText("\nThis is custom view.\n");
        tv.setTextAppearance(this, R.style.list_body_primary_l);
        tv.setBackgroundColor(0xffffff00);
        addCustomBottomView(tv);

        setButtonOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        setMinorDescriptionStyle(true);
        setMinorDescriptionStyle(false);
        onConfigurationChanged(getResources().getConfiguration());
    }
}
