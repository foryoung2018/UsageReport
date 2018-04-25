
package com.htc.lib1.cc.appfragment.activityhelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceScreen;

import com.htc.aut.util.ActivityUtil;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.preference.HtcCheckBoxPreference;
import com.htc.lib1.cc.widget.preference.HtcDialogPreference;
import com.htc.lib1.cc.widget.preference.HtcEditTextPreference;
import com.htc.lib1.cc.widget.preference.HtcListPreference;
import com.htc.lib1.cc.widget.preference.HtcMultiSelectListPreference;
import com.htc.lib1.cc.widget.preference.HtcPreferenceActivity;
import com.htc.lib1.cc.widget.preference.HtcPreferenceCategory;
import com.htc.lib1.cc.widget.preference.HtcSwitchPreference;

public class PreferencesFromCode extends HtcPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.initCategory(this, 0);
        setPreferenceScreen(createPreferenceHierarchy());
    }

    private PreferenceScreen createPreferenceHierarchy() {
        final PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

        root.removeAll();
        root.setEnabled(true);

        final HtcPreferenceCategory inlinePrefCat = new HtcPreferenceCategory(this);
        inlinePrefCat.setTitle(R.string.inline_preferences);
        root.addPreference(inlinePrefCat);

        final HtcCheckBoxPreference checkboxPref = new HtcCheckBoxPreference(this);
        checkboxPref.setKey("checkbox_preference");
        checkboxPref.setTitle(R.string.title_checkbox_preference);
        checkboxPref.setSummary(R.string.summary_checkbox_preference);
        checkboxPref.setIcon(R.drawable.ic_launcher);
        checkboxPref.setSummaryOff(R.string.preference_off);
        checkboxPref.setSummaryOn(R.string.preference_on);
        inlinePrefCat.addPreference(checkboxPref);

        final HtcSwitchPreference switchPref = new HtcSwitchPreference(this);
        switchPref.setKey("switch_preference");
        switchPref.setTitle(R.string.title_switch_preference);
        switchPref.setSummary(R.string.summary_switch_preference);
        switchPref.setSummaryOff(R.string.preference_off);
        switchPref.setSummaryOn(R.string.preference_on);
        inlinePrefCat.addPreference(switchPref);

        final HtcPreferenceCategory dialogBasedPrefCat = new HtcPreferenceCategory(this);
        dialogBasedPrefCat.setTitle(R.string.dialog_based_preferences);
        root.addPreference(dialogBasedPrefCat);

        final HtcEditTextPreference editTextPref = new HtcEditTextPreference(this);
        editTextPref.setDialogTitle(R.string.dialog_title_edittext_preference);
        editTextPref.setKey("edittext_preference");
        editTextPref.setTitle(R.string.title_edittext_preference);
        editTextPref.setSummary(R.string.summary_edittext_preference);
        editTextPref.setDialogMessage("SIM PIN (2 retries left)");
        dialogBasedPrefCat.addPreference(editTextPref);

        final HtcListPreference listPref = new HtcListPreference(this);
        listPref.setEntries(R.array.entries_list_preference);
        listPref.setEntryValues(R.array.entryvalues_list_preference);
        listPref.setDialogTitle(R.string.dialog_title_list_preference);
        listPref.setKey("list_preference");
        listPref.setTitle(R.string.title_list_preference);
        listPref.setSummary("aaaaaaaaaaaaa");
        listPref.setIcon(R.drawable.ic_launcher);
        dialogBasedPrefCat.addPreference(listPref);

        final HtcMultiSelectListPreference mtlist = new HtcMultiSelectListPreference(this);
        mtlist.setEntries(R.array.entries_multilist_preference);
        mtlist.setEntryValues(R.array.entryvalues_multilist_preference);
        mtlist.setKey("mlist_preference");
        mtlist.setTitle(R.string.title_list_preference);
        mtlist.setSummary(R.string.summary_list_preference);
        mtlist.setDialogTitle("Choose one");
        mtlist.setLayoutResource(0);
        dialogBasedPrefCat.addPreference(mtlist);

        final HtcDialogPreference dialog = new HtcDialogPreference(this, null);
        dialog.setDialogTitle(R.string.title_htcdialog_preference);
        dialog.setKey("htcdialog_preference");
        dialog.setTitle(R.string.title_dialog_preference);
        dialog.setLayoutResource(R.layout.preference_widget_seekbar);
        dialogBasedPrefCat.addPreference(dialog);

        final HtcPreferenceCategory launchPrefCat = new HtcPreferenceCategory(this);
        launchPrefCat.setTitle(R.string.launch_preferences);
        root.addPreference(launchPrefCat);

        final PreferenceScreen screenPref = getPreferenceManager().createPreferenceScreen(this);
        screenPref.setKey("screen_preference");
        screenPref.setTitle(R.string.title_screen_preference);
        screenPref.setSummary(R.string.summary_screen_preference);
        launchPrefCat.addPreference(screenPref);

        final HtcCheckBoxPreference nextScreenCheckBoxPref = new HtcCheckBoxPreference(this);
        nextScreenCheckBoxPref.setKey("next_screen_toggle_preference");
        nextScreenCheckBoxPref.setTitle(R.string.title_next_screen_toggle_preference);
        nextScreenCheckBoxPref.setSummary(R.string.summary_next_screen_toggle_preference);
        screenPref.addPreference(nextScreenCheckBoxPref);

        final PreferenceScreen intentPref = getPreferenceManager().createPreferenceScreen(this);
        intentPref.setIntent(new Intent().setAction(Intent.ACTION_VIEW)
                .setData(Uri.parse("http://www.android.com")));
        intentPref.setTitle(R.string.title_intent_preference);
        intentPref.setSummary(R.string.summary_intent_preference);
        launchPrefCat.addPreference(intentPref);

        final HtcPreferenceCategory prefSeekCat = new HtcPreferenceCategory(this);
        prefSeekCat.setTitle("SeekBar preference");
        root.addPreference(prefSeekCat);

        return root;
    }

}
