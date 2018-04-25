
package com.htc.lib1.cc.appfragment.activityhelper;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.htc.lib1.cc.view.viewpager.HtcPagerAdapter;
import com.htc.lib1.cc.view.viewpager.HtcPagerFragment;
import com.htc.lib1.cc.view.viewpager.HtcTabFragmentPagerAdapter;
import com.htc.lib1.cc.view.viewpager.HtcTabFragmentPagerAdapter.TabSpec;
import com.htc.lib1.cc.view.viewpager.HtcViewPager;

public class MyHtcPagerFragment extends HtcPagerFragment {
    public final static String TAG[] = {
            "T0", "T1", "T2"
    };
    public final static String TAB[] = {
            "TAB0", "TAB1", "TAB2"
    };
    public final static String PAGE[] = {
            "PAGE0", "PAGE1", "PAGE2"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        HtcTabFragmentPagerAdapter adapter = (HtcTabFragmentPagerAdapter) getAdapter();
        adapter.addTab(TAG[0], new TabSpec(TAB[0]).setBackground(new ColorDrawable(Color.YELLOW)));
        adapter.addTab(TAG[1], new TabSpec(TAB[1]).setVisible(true));
        adapter.addTab(TAG[2], new TabSpec(TAB[2]).setRemovable(false));
    }

    @Override
    public void onStart() {
        HtcTabFragmentPagerAdapter adapter = (HtcTabFragmentPagerAdapter) getAdapter();
        HtcViewPager pager = getPager();
        pager.setCurrentItem(adapter.getPagePosition(TAG[1]));
        super.onStart();
    }

    @Override
    protected HtcPagerAdapter onCreateAdapter(Context context) {
        return new MyTabAdapter(this);
    }
}

class MyTabAdapter extends HtcTabFragmentPagerAdapter {
    public MyTabAdapter(Fragment host) {
        super(host);
    }

    @Override
    public Fragment getItem(String tag) {
        if (MyHtcPagerFragment.TAG[0].equals(tag)) {
            return new Tab0();
        } else if (MyHtcPagerFragment.TAG[1].equals(tag)) {
            return new Tab1();
        } else if (MyHtcPagerFragment.TAG[2].equals(tag)) {
            return new Tab2();
        } else {
            return null;
        }
    }
}
