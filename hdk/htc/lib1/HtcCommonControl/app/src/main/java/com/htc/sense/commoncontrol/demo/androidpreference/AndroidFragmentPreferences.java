
package com.htc.sense.commoncontrol.demo.androidpreference;

import com.htc.lib1.cc.widget.preference.PreferenceUtil;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.AndroidPreferenceUtil;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AndroidFragmentPreferences extends CommonDemoActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }

    @Override
    protected void applyCustomWindowFeature() {
        super.applyCustomWindowFeature();
        AndroidPreferenceUtil.setAndroidTheme(this);
    }

    public static class PrefsFragment extends PreferenceFragment {
        private String mPreferenceScreenkey;
        private PreferenceScreen mPreferenceScreen;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            mPreferenceScreenkey = getResources().getString(R.string.screen_preference);
            mPreferenceScreen = (PreferenceScreen) findPreference(mPreferenceScreenkey);
            mPreferenceScreen.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (null != mPreferenceScreenkey && mPreferenceScreenkey.equals(preference.getKey()))
                    {
                        PreferenceUtil.applyHtcListViewStyle((ViewGroup) ((PreferenceScreen)
                                preference).getDialog().getWindow().getDecorView());
                    }
                    return false;
                }
            });
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            ViewGroup content = PreferenceUtil.applyHtcListViewStyle((ViewGroup) super.onCreateView(inflater, container,
                    savedInstanceState));
            return content;
        }
    }

}
