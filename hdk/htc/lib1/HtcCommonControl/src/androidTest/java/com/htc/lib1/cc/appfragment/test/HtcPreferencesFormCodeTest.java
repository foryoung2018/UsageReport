
package com.htc.lib1.cc.appfragment.test;

import com.htc.lib1.cc.appfragment.activityhelper.PreferencesFromCode;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.appfragment.test.util.PreferencesTestUtil;
import com.htc.lib1.cc.widget.preference.DontUsePreference2LineText;
import com.htc.lib1.cc.widget.preference.DontUsePreferenceColorIcon;
import com.htc.lib1.cc.widget.preference.DontUsePreferenceSeparator;
import com.htc.lib1.cc.widget.preference.HtcCheckBoxPreference;
import com.htc.lib1.cc.widget.preference.HtcDialogPreference;
import com.htc.lib1.cc.widget.preference.HtcEditTextPreference;
import com.htc.lib1.cc.widget.preference.HtcListPreference;
import com.htc.lib1.cc.widget.preference.HtcPreference;
import com.htc.lib1.cc.widget.preference.HtcPreferenceCategory;
import com.htc.lib1.cc.widget.preference.HtcSwitchPreference;
import com.htc.lib1.cc.widget.preference.MySwitchPreference;
import com.htc.test.HtcActivityTestCaseBase;

public class HtcPreferencesFormCodeTest extends HtcActivityTestCaseBase {
    private static final String SWITCH = "switch_preference";
    private static final String CHECKBOX = "checkbox_preference";

    public HtcPreferencesFormCodeTest(Class activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initActivity();
    }

    public HtcPreferencesFormCodeTest() throws ClassNotFoundException {
        super(PreferencesFromCode.class);
    }

    public final void testHtcEditTextPreference() {
        PreferencesTestUtil.scrollToCodeAndScreenShotTest(4, this, mSolo, mActivity);
    }

    public final void testHtcSwitchPreference_Off() {
        PreferencesTestUtil.scrollToAndScreenShotTest(2, this, false, SWITCH, mSolo, mActivity);
    }

    public final void testHtcSwitchPreference_On() {
        PreferencesTestUtil.scrollToAndScreenShotTest(2, this, true, SWITCH, mSolo, mActivity);
    }

    public final void testHtcCheckBox_NoSelect() {
        PreferencesTestUtil.scrollToAndScreenShotTest(1, this, false, CHECKBOX, mSolo, mActivity);
    }

    public final void testHtcCheckBox_Select() {
        PreferencesTestUtil.scrollToAndScreenShotTest(1, this, true, CHECKBOX, mSolo, mActivity);
    }

    public final void testHtcSingleChoiceListPreference() {
        PreferencesTestUtil.scrollToCodeAndScreenShotTest(5, this, mSolo, mActivity);
    }

    public final void testHtcMultiSelectListPreference() {
        PreferencesTestUtil.scrollToCodeAndScreenShotTest(6, this, mSolo, mActivity);
    }

    public final void testHtcDialogPreference() {
        PreferencesTestUtil.scrollToCodeAndScreenShotTest(7, this, mSolo, mActivity);
    }

    public void testImproveHtcCoverage() {

        final HtcListPreference listPreference = new HtcListPreference(mActivity);
        listPreference.getDialogContext();
        listPreference.restoreHierarchyState(null);
        listPreference.setDialogContext(getActivity());
        listPreference.setLayoutResource(R.layout.progress_dialog);
        listPreference.setValue(mActivity.getResources().getString(R.string.am));

        final HtcPreference preference = new HtcPreference(mActivity);
        preference.getLayoutResource();
        preference.setLayoutResource(R.layout.action_dropdown);

        final HtcSwitchPreference stp = new HtcSwitchPreference(mActivity, null, 0);

        final HtcEditTextPreference editText = new HtcEditTextPreference(mActivity, null, 0);
        editText.setLayoutResource(R.layout.action_dropdown);

        final HtcCheckBoxPreference checkBox = new HtcCheckBoxPreference(mActivity, null, 0);

        final HtcDialogPreference dialog = new HtcDialogPreference(mActivity, null, 0);

        final HtcPreferenceCategory pCategory = new HtcPreferenceCategory(mActivity, null, 0);

        final MySwitchPreference mSwitch = (MySwitchPreference) mActivity.getLayoutInflater().inflate(R.layout.preference_widget_switch, null);
    }

    public void testImproveDontUseCoverage() {

        final DontUsePreference2LineText dont2LineTextOne = new DontUsePreference2LineText(mActivity);

        final DontUsePreference2LineText dont2LineTextTwo = new DontUsePreference2LineText(mActivity, 0);

        final DontUsePreference2LineText dont2LineTextThree = new DontUsePreference2LineText(mActivity, null, 0);

        final DontUsePreferenceColorIcon dontColorIcon = new DontUsePreferenceColorIcon(mActivity);

        final DontUsePreferenceColorIcon dontColorIconThree = new DontUsePreferenceColorIcon(mActivity, null, 0);

        final DontUsePreferenceSeparator dontSeparator = new DontUsePreferenceSeparator(mActivity);

        final DontUsePreferenceSeparator dontSeparatorTwo = new DontUsePreferenceSeparator(mActivity, 0);

        final DontUsePreferenceSeparator dontSeparatorThree = new DontUsePreferenceSeparator(mActivity, 0, 0);

        final DontUsePreferenceSeparator dontSeparatorTh = new DontUsePreferenceSeparator(mActivity, null, 0);
    }
}
