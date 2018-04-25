package com.htc.sense.commoncontrol.demo.actionmodecustom;

import com.htc.lib1.cc.app.OnActionModeChangedListener;
import com.htc.lib1.cc.util.ActionBarUtil;
import com.htc.sense.commoncontrol.demo.R;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.ActionMode;

public class EditTextPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.actionmode_preference);
        Preference preference = (Preference) findPreference(getResources().getString(R.string.title_edittext_preference));
        preference.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                OnActionModeChangedListener actionModeChangedListener = new OnActionModeChangedListener() {
                    @Override
                    public void onActionModeStarted(ActionMode mode) {
                        ActionBarUtil.setActionModeBackground(getActivity(), mode, new ColorDrawable(Color.GRAY));
                    }
                };
                ActionBarUtil.wrapActionModeChangeForDialog(((DialogPreference) preference).getDialog(), actionModeChangedListener);
                return false;
            }
        });
    }

}
