package com.htc.sense.commoncontrol.demo.htcpreference;

import java.util.List;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.htc.lib1.cc.widget.preference.HtcPreferenceActivity;
import com.htc.lib1.cc.widget.preference.HtcPreferenceFragment;
import com.htc.lib1.cc.widget.preference.PreferenceUtil;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

//BEGIN_INCLUDE(activity)
public class PreferenceWithHeaders extends HtcPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CommonUtil.reloadDemoTheme(this, savedInstanceState);

        setTheme(R.style.HtcPreference);
        super.onCreate(savedInstanceState);
        CommonUtil.initHtcActionBar(this, true, true);

    }
    @Override
    public void onContentChanged() {
        PreferenceUtil.applyHtcListViewStyle(this, (ViewGroup) getWindow().getDecorView());
        super.onContentChanged();
    }
    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }
    @Override
    protected boolean isValidFragment(String fragmentName) {
        //AP should verify fragment is valid by your self
        return true;
    }
    /**
     * This fragment shows the preferences for the first header.
     */
    public static class Prefs1Fragment extends HtcPreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Make sure default values are applied.  In a real app, you would
            // want this in a shared function that is used to retrieve the
            // SharedPreferences wherever they are needed.
            PreferenceManager.setDefaultValues(getActivity(),
                    R.xml.advanced_preferences, false);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.htcpreferences);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            ViewGroup content = PreferenceUtil.applyHtcListViewStyle((ViewGroup) super.onCreateView(inflater,container,
                    savedInstanceState));
            return content;
        }
    }

    /**
     * This fragment contains a second-level set of preference that you
     * can get to by tapping an item in the first preferences fragment.
     */
    public static class Prefs1FragmentInner extends HtcPreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Can retrieve arguments from preference XML.
            Log.i("args", "Arguments: " + getArguments());

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.fragmented_preferences_inner);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            ViewGroup content = PreferenceUtil.applyHtcListViewStyle((ViewGroup) super.onCreateView(inflater,container,
                    savedInstanceState));
            return content;
        }
    }

    /**
     * This fragment shows the preferences for the second header.
     */
    public static class Prefs2Fragment extends HtcPreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Can retrieve arguments from headers XML.
            Log.i("args", "Arguments: " + getArguments());

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preference_dependencies);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            ViewGroup content = PreferenceUtil.applyHtcListViewStyle((ViewGroup) super.onCreateView(inflater,container,
                    savedInstanceState));
            return content;
        }
    }
}
