
package com.htc.lib1.cc.dialogpicker.activityhelper;

import android.app.ActionBar;
import android.os.Bundle;

import com.htc.aut.ActivityBase;

public class DialogActivityBase extends ActivityBase {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar bar = getActionBar();
        if (null != bar) {
            bar.hide();
        }
    }
}
