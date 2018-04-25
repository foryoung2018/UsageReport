
package com.htc.lib1.cc.viewpager.activityhelper;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.htc.aut.ActivityBase;

public class HtcPagerFragmentDemo extends ActivityBase {
    public static final String LOG_TAG = "HtcPagerFragmentDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout content = new FrameLayout(this);
        content.setId(112233);

        FragmentManager fm = getFragmentManager();
        MyPagerFragment pagerFragment = (MyPagerFragment) fm.findFragmentById(112233);
        if (pagerFragment == null) {
            FragmentTransaction tx = fm.beginTransaction();
            tx.add(112233, pagerFragment = new MyPagerFragment(), "Pager1");
            tx.commit();
        }

        // requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(content);
    }
}
