
package com.htc.lib1.cc.appfragment.activityhelper;

import android.os.Bundle;
import com.htc.lib1.cc.test.R;

import com.htc.aut.ActivityBase;

public class HtcPagerFragmentDemo extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_htcpagerfragment);
        MyHtcPagerFragment pagerFragment = (MyHtcPagerFragment) getFragmentManager().findFragmentById(R.id.fragment);
        pagerFragment.setEditable(false);
    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }
}
