
package com.htc.sense.commoncontrol.demo.androidpreference;

import com.htc.lib1.cc.widget.preference.PreferenceUtil;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.AndroidPreferenceUtil;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.ViewGroup;

public class AndroidActivityPreference extends PreferenceActivity {

    private String mPreferenceScreenkey;
    private PreferenceScreen mPreferenceScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AndroidPreferenceUtil.setAndroidTheme(this);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        CommonUtil.initHtcActionBar(this, true, true);
        mPreferenceScreenkey = getResources().getString(R.string.screen_preference);
        mPreferenceScreen = (PreferenceScreen) findPreference(mPreferenceScreenkey);
        mPreferenceScreen.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (null != mPreferenceScreenkey && mPreferenceScreenkey.equals(preference.getKey()))
                {
                    PreferenceUtil.applyHtcListViewStyle(AndroidActivityPreference.this,
                            (ViewGroup) ((PreferenceScreen)
                            preference).getDialog().getWindow().getDecorView());
                }
                return false;
            }
        });

    }

    @Override
    public void onContentChanged() {
        PreferenceUtil.applyHtcListViewStyle(this, (ViewGroup) getWindow().getDecorView());
        super.onContentChanged();
    }

}
