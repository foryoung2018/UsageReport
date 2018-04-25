
package com.htc.lib1.cc.appfragment.test;

import com.htc.lib1.cc.appfragment.activityhelper.PreferencesFragment;
import com.htc.test.HtcActivityTestCaseBase;

public class HtcPreferencesFagementTest extends HtcActivityTestCaseBase {

    public HtcPreferencesFagementTest(Class activityClass) {
        super(activityClass);
    }

    public HtcPreferencesFagementTest() throws ClassNotFoundException {
        super(PreferencesFragment.class);
    }

    public void testFagemetPreference() {
        initActivity();
    }
}
