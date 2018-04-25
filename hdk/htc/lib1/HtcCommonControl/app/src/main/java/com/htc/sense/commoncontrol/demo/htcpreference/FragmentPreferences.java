package com.htc.sense.commoncontrol.demo.htcpreference;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.htc.lib1.cc.widget.preference.HtcPreferenceFragment;
import com.htc.lib1.cc.widget.preference.PreferenceUtil;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class FragmentPreferences extends CommonDemoActivityBase {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
//        for Sense60 status bar change color theme
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.RED));

    }

    @Override
    protected void applyCustomWindowFeature() {
        super.applyCustomWindowFeature();
        setTheme(R.style.HtcPreference);
    }

    public static class PrefsFragment extends HtcPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.htcpreferences);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            ViewGroup content = PreferenceUtil.applyHtcListViewStyle((ViewGroup) super.onCreateView(inflater,container,
                    savedInstanceState));
//            for Sense60 status bar change color theme
//            content.setFitsSystemWindows(true);
//            HtcListView lv = (HtcListView) content.findViewById(android.R.id.list);
//            lv.setBackgroundResource(com.htc.lib1.cc.R.drawable.common_app_bkg);
            return content;
        }
    }
}
