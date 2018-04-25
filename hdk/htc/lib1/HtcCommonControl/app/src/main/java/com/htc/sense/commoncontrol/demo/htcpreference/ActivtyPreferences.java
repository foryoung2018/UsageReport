package com.htc.sense.commoncontrol.demo.htcpreference;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ListView;

import com.htc.lib1.cc.widget.preference.HtcPreferenceActivity;
import com.htc.lib1.cc.widget.preference.PreferenceUtil;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

public class ActivtyPreferences extends HtcPreferenceActivity {
    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CommonUtil.reloadDemoTheme(this, savedInstanceState);

        setTheme(R.style.HtcPreference);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.htcpreferences);
        CommonUtil.initHtcActionBar(this, true, true);
//        for Sense60 status bar change color theme
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        ViewGroup vg =(ViewGroup) findViewById(android.R.id.content);
//        vg.getChildAt(0).setFitsSystemWindows(true);
//        getListView().setBackgroundResource(com.htc.lib1.cc.R.drawable.common_app_bkg);
//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.RED));
    }
    @Override
    public void onContentChanged() {
        PreferenceUtil.applyHtcListViewStyle(this, (ViewGroup) getWindow().getDecorView());
        super.onContentChanged();
    }



}
