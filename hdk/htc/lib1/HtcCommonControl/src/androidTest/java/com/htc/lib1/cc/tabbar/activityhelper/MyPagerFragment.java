
package com.htc.lib1.cc.tabbar.activityhelper;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.htc.lib1.cc.test.R;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.view.viewpager.HtcPagerAdapter;
import com.htc.lib1.cc.view.viewpager.HtcPagerFragment;
import com.htc.lib1.cc.view.viewpager.HtcTabFragmentPagerAdapter;
import com.htc.lib1.cc.view.viewpager.HtcTabFragmentPagerAdapter.TabSpec;
import com.htc.lib1.cc.view.viewpager.HtcViewPager;

public class MyPagerFragment extends HtcPagerFragment {
    private Drawable mTextureDrawable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        HtcTabFragmentPagerAdapter adapter = (HtcTabFragmentPagerAdapter) getAdapter();
        // add tab 1 with yellow background
        adapter.addTab("T1",
                new TabSpec("Tab1")
                        .setBackground(new ColorDrawable(Color.YELLOW)));

        // add tab 2 that is invisible in default
        adapter.addTab("T2",
                new TabSpec("Tab2")
                        .setVisible(false));

        // add tab 3 that is always shown on tab bar
        adapter.addTab("T3",
                new TabSpec("Tab3")
                        .setRemovable(false));

        adapter.setTitle("T2", "TAB2");
        adapter.setCount("T3", 45);

        HtcViewPager pager = getPager();

        // track scroll
        pager.setOnPageChangeListener(new HtcViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageSelected(int position) {
            }
        });

        // monitor edit mode
        setEditingListener(new EditingListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onStop() {
            }
        });
        mTextureDrawable = HtcCommonUtil.getCommonThemeTexture(getActivity(), R.styleable.CommonTexture_android_headerBackground);
        swtichTabBarbkg(getActivity().getResources().getConfiguration().orientation);
    }

    @Override
    public void onStart() {
        HtcTabFragmentPagerAdapter adapter = (HtcTabFragmentPagerAdapter) getAdapter();

        // set default tab
        HtcViewPager pager = getPager();
        pager.setCurrentItem(adapter.getPagePosition("T3"));

        super.onStart();
    }

    @Override
    protected HtcPagerAdapter onCreateAdapter(Context context) {
        return new MyTabAdapter(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        swtichTabBarbkg(newConfig.orientation);
    }

    void swtichTabBarbkg(int orientation) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            getTabBar().setBackground(mTextureDrawable);
        }
    }
}

class MyTabAdapter extends HtcTabFragmentPagerAdapter {
    public MyTabAdapter(Fragment host) {
        super(host);
    }

    @Override
    public Fragment getItem(String tag) {
        if (tag.equals("T1")) {
            return new PagerTab(0);
        } else if (tag.equals("T2")) {
            return new PagerTab(1);
        } else if (tag.equals("T3")) {
            return new PagerTab(2);
        }
        return null;
    }

    @Override
    public void onTabChanged(String previousTag, String currentTag) {
        //
    }
}
