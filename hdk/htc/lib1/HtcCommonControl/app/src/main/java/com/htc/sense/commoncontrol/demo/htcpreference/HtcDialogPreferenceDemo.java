package com.htc.sense.commoncontrol.demo.htcpreference;

import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class HtcDialogPreferenceDemo extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        CommonUtil.reloadDemoTheme(this, savedInstanceState);

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencesdialog);
        CommonUtil.initHtcActionBar(this, true, true);
    }

}
