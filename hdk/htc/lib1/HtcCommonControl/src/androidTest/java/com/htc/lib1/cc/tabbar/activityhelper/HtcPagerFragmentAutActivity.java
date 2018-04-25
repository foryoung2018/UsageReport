
package com.htc.lib1.cc.tabbar.activityhelper;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.htc.aut.ActivityBase;

public class HtcPagerFragmentAutActivity extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView();

        FragmentManager fm = getFragmentManager();
        MyPagerFragment pagerFragment = new MyPagerFragment();
        FragmentTransaction tx = fm.beginTransaction();
        tx.add(android.R.id.content, pagerFragment, "Pager1");
        tx.commit();
    }
}
