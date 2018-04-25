
package com.htc.lib1.cc.appfragment.activityhelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.htc.lib1.cc.test.R;

import com.htc.aut.util.ActivityUtil;
import com.htc.lib1.cc.widget.preference.HtcPreferenceActivity;
import com.htc.lib1.cc.widget.preference.HtcPreferenceFragment;
import com.htc.lib1.cc.widget.preference.PreferenceUtil;

public class PreferencesFragment extends HtcPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityUtil.initCategory(this,0);
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }

    private static class PrefsFragment extends HtcPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return PreferenceUtil.applyHtcListViewStyle((ViewGroup) super.onCreateView(inflater, container,
                    savedInstanceState));
        }
    }

}
