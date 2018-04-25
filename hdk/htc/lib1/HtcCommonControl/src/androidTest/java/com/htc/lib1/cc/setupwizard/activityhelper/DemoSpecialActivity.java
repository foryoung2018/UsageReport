package com.htc.lib1.cc.setupwizard.activityhelper;

import android.os.Bundle;
import android.widget.TextView;
import com.htc.lib1.cc.test.R;

import com.htc.lib1.cc.widget.setupwizard.HtcSpecialWizardActivity;
import com.htc.lib1.cc.setupwizard.activityhelper.util.SetupWizardUtil;

public class DemoSpecialActivity extends HtcSpecialWizardActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SetupWizardUtil.initThemeAndCategory(this);
        super.onCreate(savedInstanceState);

        onDelayUIUpdate();
        setTitleText("Action Bar Title Special mode");
        hideProgress(true);

        setImage(R.drawable.display);
        setImage(getResources().getDrawable(R.drawable.display));

        StringBuilder sb = new StringBuilder();
        sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
        for (int i = 0; i < 5; i++) {
            sb.append('\n').append("desc line ").append(i + 1);
        }
        setDescriptionText(R.string.am);
        setDescriptionText(sb);
        TextView tv = new TextView(this);
        tv.setText("\nThis is custom view.\n");
        tv.setTextAppearance(this, R.style.list_body_primary_l);
        tv.setBackgroundColor(0xffffff00);
        addCustomBottomView(tv);

        onConfigurationChanged(getResources().getConfiguration());
    }
}
