
package com.htc.lib1.cc.appfragment.test;

import android.content.Intent;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.ListView;

import com.htc.lib1.cc.appfragment.activityhelper.PreferencesFromXml;
import com.htc.lib1.cc.appfragment.test.util.PreferencesTestUtil;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.preference.HtcPreferenceActivity;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class HtcPreferenceBasicTest extends HtcActivityTestCaseBase {

    private View mView;
    private ListView mListview;
    private static final String SWITCH = "switch_preference";
    private static final String CHECKBOX = "checkbox_preference";
    private static final String TOGGLE = "toggle_preference";
    private static final String HTCTOGGLE = "htc_checkbox_preference";
    private static final String HTCSWITCH = "htcswitch_preference";

    public HtcPreferenceBasicTest(Class activityClass) {
        super(activityClass);
    }

    public HtcPreferenceBasicTest() {
        super(PreferencesFromXml.class);
    }

    private void initActivityWithXmlId(int xmlId,int index ,boolean isTwoState,boolean isChecked,String tag) {
        final Intent intent = new Intent();
        intent.putExtra(PreferencesFromXml.XML_ID, xmlId);
        intent.putExtra(PreferencesFromXml.INDEX, index);
        intent.putExtra(PreferencesFromXml.ISTWOSTATE, isTwoState);
        intent.putExtra(PreferencesFromXml.ISCHECKED, isChecked);
        intent.putExtra(PreferencesFromXml.TAG, tag);
        setActivityIntent(intent);
        initActivity();
    }

    private void scrollToAndScreenShotTest(int xmlId, int index, boolean isTwoState, boolean isChecked, String tag) {
        initActivityWithXmlId(xmlId, index, isTwoState, isChecked, tag);
        PreferencesTestUtil.scrollToAndScreenShotTest(this, index, mSolo);
    }

    private void showDialogAndScreenShotTest(int xmlId, String key, int index) {
        initActivityWithXmlId(xmlId, index, false, false, null);
        PreferencesTestUtil.showDialogAndScreenShotTest(key, index, this, mActivity, mSolo);
    }

    public final void testSwitchPreference_Off() {
        scrollToAndScreenShotTest(R.xml.twostatepreferences, 1, true, false, SWITCH);
    }

    public final void testSwitchPreference_On() {
        scrollToAndScreenShotTest(R.xml.twostatepreferences,1,true,true,SWITCH);
    }

    public final void testTwoLineChecekBox_NoSelect() {
        scrollToAndScreenShotTest(R.xml.twostatepreferences,2,true,false,CHECKBOX);
    }

    public final void testTwoLineChecekBox_Select() {
        scrollToAndScreenShotTest(R.xml.twostatepreferences,2,true,true,CHECKBOX);
    }

    public final void testSeekBarDialogPreference() {
        scrollToAndScreenShotTest(R.xml.dialogpreferences,2,false,false,null);
    }

    public final void testSeekBarDialogPreference_Click() {
        showDialogAndScreenShotTest(R.xml.dialogpreferences, "seekbardialog_preference", 3);
    }

    public final void testEditTextPreference() {
        scrollToAndScreenShotTest(R.xml.dialogpreferences,1,false,false,null);
    }

    public final void testEditTextPreference_show() {
        showDialogAndScreenShotTest(R.xml.dialogpreferences, "edittext_preference", 2);
    }

    public final void testSingleChoiceListPreference() {
        scrollToAndScreenShotTest(R.xml.listpreferences,1,false,false,null);
    }

    public final void testSingleChoiceListPreference_show() {
        showDialogAndScreenShotTest(R.xml.listpreferences, "list_preference", 2);
    }

    public final void testPreferenceScreenPreference() {
        scrollToAndScreenShotTest(R.xml.screenpreferences,1,false,false,null);
    }

    public final void testHtcSeekBarPreference() {
        scrollToAndScreenShotTest(R.xml.togglepreferences,2,false,false,null);
    }

    public final void testMultiSelectListPreference() {
        scrollToAndScreenShotTest(R.xml.listpreferences,2,false,false,null);
    }

    public final void testMultiSelectListPreference_show() {
        showDialogAndScreenShotTest(R.xml.listpreferences, "multiselect_preference", 3);
    }

    public final void testToggle_Select() {
        scrollToAndScreenShotTest(R.xml.togglepreferences,1,true,true,TOGGLE);
    }

    public final void testToggle_NoSelect() {
        scrollToAndScreenShotTest(R.xml.togglepreferences,1,true,false,TOGGLE);
    }

    public final void testHtcToggle_Select() {
        scrollToAndScreenShotTest(R.xml.htctwostatepreferences,1,true,true,HTCTOGGLE);
    }

    public final void testHtcToggle_NoSelect() {
        scrollToAndScreenShotTest(R.xml.htctwostatepreferences,1,true,false,HTCTOGGLE);
    }

    public final void testHtcMultiSelectListPreference() {
        scrollToAndScreenShotTest(R.xml.htclistpreferences,2,false,false,null);
    }

    public final void testHtcMultiSelectListPreference_show() {
        showDialogAndScreenShotTest(R.xml.htclistpreferences, "htc_multiselect_preference", 3);
    }

    public final void testHtcEditTextPreference() {
        scrollToAndScreenShotTest(R.xml.htcedittextpreferences,1,false,false,null);
    }

    public final void testHtcEditTextPreference_show() {
        showDialogAndScreenShotTest(R.xml.htcedittextpreferences, "htc_edittext_preference", 2);
    }

    public final void testHtcSingleChoiceListPreference() {
        scrollToAndScreenShotTest(R.xml.htclistpreferences,1,false,false,null);
    }

    public final void testHtcSingleChoiceListPreference_show() {
        showDialogAndScreenShotTest(R.xml.htclistpreferences, "htc_list_preference", 2);
    }

    public final void testHtcSwitchPreference_On() {
        scrollToAndScreenShotTest(R.xml.htctwostatepreferences,2,true,true,HTCSWITCH);
    }

    public final void testHtcSwitchPreference_Off() {
        scrollToAndScreenShotTest(R.xml.htctwostatepreferences,2,true,false,HTCSWITCH);
    }

    public final void testHtcDialogPreference() {
        scrollToAndScreenShotTest(R.xml.htcdialogpreferences,1, false, false, null);
    }

    public final void testHtcDialogPreference_show() {
        showDialogAndScreenShotTest(R.xml.htcdialogpreferences, "dialog_preference", 2);
    }

    public final void testPreferenceScreenPreference_show() {
        initActivityWithXmlId(R.xml.screenpreferences,-1,false,false,null);
        final PreferenceScreen preferencScreen = (PreferenceScreen) ((HtcPreferenceActivity) mActivity).findPreference("screen preference");
        preferencScreen.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mView = ((PreferenceScreen) preference).getDialog().getWindow().getDecorView();
                return false;
            }
        });
        mSolo.clickInList(2);
        mSolo.waitForDialogToOpen(5000);

        ScreenShotUtil.AssertViewEqualBefore(mSolo, mView, this);
    }
}
