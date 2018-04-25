package com.htc.lib1.cc.setupwizard.activityhelper;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import com.htc.lib1.cc.test.R;

import com.htc.lib1.cc.widget.setupwizard.HtcRadioListWizardActivity;
import com.htc.lib1.cc.setupwizard.activityhelper.util.SetupWizardUtil;

public class DemoRadioListActivity extends HtcRadioListWizardActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SetupWizardUtil.initThemeAndCategory(this);
        super.onCreate(savedInstanceState);

        setTitleText("Action Bar Title");
        setSubTitle(R.string.title_demo_radiolist_activity);
        setProgressBar(3, 5);

        setImage(R.drawable.one);
        setImage(getResources().getDrawable(R.drawable.one));

        setItem1PrimaryText("Primary 1");
        setItem2PrimaryText("Primary 2");
        setItem1SecondaryText("Secondary 1");
        setItem2SecondaryText("Secondary 2");

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
        getCheckedListItem();
        setCheckedItem(ITEM1);
        setItem1Enabled(true);
        setItem2Enabled(true);
        setItem1PrimaryText(R.string.Primary_1);
        setItem2PrimaryText(R.string.Primary_2);
        setItem1SecondaryText(R.string.Secondary_1);
        setItem2SecondaryText(R.string.Secondary_2);
        setListViewAdapter(null);
        setMinorDescriptionStyle(true);
        setMinorDescriptionStyle(false);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }
}
