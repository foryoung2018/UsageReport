package com.htc.lib1.cc.widget.preference;

import com.htc.lib1.cc.R;

import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;

public class HtcSwitchPreference extends SwitchPreference {
    public HtcSwitchPreference(Context context, AttributeSet attrs, int defStyle) {
        super(new ContextThemeWrapper(context, R.style.Preference), attrs, defStyle);
    }

    public HtcSwitchPreference(Context context, AttributeSet attrs) {
        super(new ContextThemeWrapper(context, R.style.Preference), attrs);
    }

    public HtcSwitchPreference(Context context) {
        super(new ContextThemeWrapper(context, R.style.Preference));
    }
}
