
package com.htc.lib1.cc.appfragment.activityhelper;

import android.content.Intent;
import android.os.Bundle;
import android.preference.TwoStatePreference;
import android.view.ViewGroup;
import android.widget.ListView;

import com.htc.aut.util.ActivityUtil;
import com.htc.lib1.cc.widget.preference.HtcPreferenceActivity;
import com.htc.lib1.cc.widget.preference.PreferenceUtil;

public class PreferencesFromXml extends HtcPreferenceActivity {

    public static final String XML_ID = "xmlId";
    public static final String INDEX = "index";
    public static final String ISTWOSTATE = "isTwoState";
    public static final String ISCHECKED = "isChecked";
    public static final String ISELLIPSIZENULL = "isEllipsizeNull";
    public static final String TAG = "tag";
    private static TwoStatePreference mTwoStatePreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityUtil.initCategory(this, 0);
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        if (null != intent) {
            int preferencesXmlId = intent.getIntExtra(XML_ID, 0);
            int preferenceIndex = intent.getIntExtra(INDEX, 0);
            boolean perferenceIsTwoState = intent.getBooleanExtra(ISTWOSTATE, false);
            boolean perferenceIschecked = intent.getBooleanExtra(ISCHECKED, false);
            String perferenceTag = intent.getStringExtra(TAG);
            addPreferencesFromResource(preferencesXmlId);
            if (null != perferenceTag) {
                mTwoStatePreference = (TwoStatePreference) findPreference(perferenceTag);
            }
            final ListView hlv = (ListView) findViewById(android.R.id.list);
            hlv.setSelection(preferenceIndex - 1);
            if (perferenceIsTwoState && null != mTwoStatePreference) {
                mTwoStatePreference.setChecked(perferenceIschecked);
            }
        }
    }

    @Override
    public void onContentChanged() {
        PreferenceUtil.applyHtcListViewStyle(this, (ViewGroup) getWindow().getDecorView());
        super.onContentChanged();
    }
}
