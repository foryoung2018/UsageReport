package com.htc.sense.commoncontrol.demo.setupwizard;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.htc.lib1.cc.widget.setupwizard.HtcRadioListWizardActivity;

public class DemoRadioListActivity extends HtcRadioListWizardActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtil.reloadDemoTheme(this, savedInstanceState);
        CommonUtil.initHtcActionBar(this, true, true);

        setProgressBar(2, 4);

        // set image
        setImage(R.drawable.one);

        // set radio list
        setItem1PrimaryText("Primary 1");
        setItem2PrimaryText("Primary 2");
        setItem1SecondaryText("Secondary 1");
        setItem2SecondaryText("Secondary 2");
        setCheckedItem(ITEM1);
        setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case ITEM1:
                        setItem1SecondaryText("Item 1 clicked");
                        setItem2SecondaryText("Secondary 2");
                        break;
                    case ITEM2:
                        setItem2SecondaryText("Item 2 clicked");
                        setItem1SecondaryText("Secondary 1");
                        break;
                }
            }
        });

        // set description
        StringBuilder sb = new StringBuilder();
        sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
        for(int i=0; i<5; i++) {
            sb.append('\n')
              .append("desc line ")
              .append(i+1);
        }
        setDescriptionText(sb);

        // add custom view
        TextView tv = new TextView(this);
        tv.setText("\nThis is custom view.\n");
        tv.setTextAppearance(this, com.htc.lib1.cc.R.style.list_body_primary_l);
        tv.setBackgroundColor(0xffffff00);
        addCustomBottomView(tv);
    }

    @Override
    public void finish() {
        switch(getCheckedListItem()) {
            case ITEM1:
                Toast.makeText(this, "item 1 selected", Toast.LENGTH_SHORT).show();
                break;
            case ITEM2:
                Toast.makeText(this, "item 2 selected", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "nothing selected", Toast.LENGTH_SHORT).show();
                break;
        }
        super.finish();
    }
}
