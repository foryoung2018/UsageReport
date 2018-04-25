
package com.htc.lib1.cc.tabbar.activityhelper;

import android.os.Bundle;

import com.htc.aut.ActivityBase;

public class TabBarAutActivity extends ActivityBase {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
    }
}
