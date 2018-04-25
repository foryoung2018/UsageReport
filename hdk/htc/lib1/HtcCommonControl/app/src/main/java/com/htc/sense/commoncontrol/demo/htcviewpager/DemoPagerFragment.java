
package com.htc.sense.commoncontrol.demo.htcviewpager;

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

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.view.viewpager.HtcPagerAdapter;
import com.htc.lib1.cc.view.viewpager.HtcPagerFragment;
import com.htc.lib1.cc.view.viewpager.HtcTabFragmentPagerAdapter;
import com.htc.lib1.cc.view.viewpager.HtcTabFragmentPagerAdapter.TabSpec;
import com.htc.lib1.cc.view.viewpager.HtcViewPager;

public class DemoPagerFragment extends HtcPagerFragment {
    private Drawable mTextureDrawable;
    private float mWidthFactor = 1.0f;
    private boolean mIsAutomotive = false;
    private boolean mIsCNMode = false;

    private static final String KEY_WIDTH_FACTOR = "WIDTH_FACTOR";
    private static final String KEY_IS_AUTO = "IS_AUTO";
    private static final String KEY_IS_CN = "IS_CN";

    public DemoPagerFragment() {
    }

    public DemoPagerFragment(float widthFactor, boolean isAuto, boolean isCN) {
        Bundle args = new Bundle();
        args.putFloat(KEY_WIDTH_FACTOR, widthFactor);
        args.putBoolean(KEY_IS_AUTO, isAuto);
        args.putBoolean(KEY_IS_CN, isCN);
        setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mWidthFactor = args.getFloat(KEY_WIDTH_FACTOR, 1.0f);
            mIsAutomotive = args.getBoolean(KEY_IS_AUTO);
            mIsCNMode = args.getBoolean(KEY_IS_CN);
        }
    }
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
                        .setVisible(true));

        // add tab 3 that is always shown on tab bar
        adapter.addTab("T3",
                new TabSpec("Tab3")
                        .setRemovable(false).setBackground(new ColorDrawable(Color.LTGRAY)));

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
        mTextureDrawable = HtcCommonUtil.getCommonThemeTexture(getActivity(), com.htc.lib1.cc.R.styleable.CommonTexture_android_headerBackground);
        swtichTabBarbkg(getActivity().getResources().getConfiguration().orientation);
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

    public void setPageWidthFactor(float widthFactor) {
        mWidthFactor = widthFactor;
        Bundle args = getArguments();
        if (args != null) {
            args.putFloat(KEY_WIDTH_FACTOR, widthFactor);
        }
        getAdapter().notifyDataSetChanged();
    }

    public float getPageWidthFactor() {
        return mWidthFactor;
    }

    public void setAutomotive(boolean isAuto) {
        mIsAutomotive = isAuto;
        Bundle args = getArguments();
        if (args != null) {
            args.putBoolean(KEY_IS_AUTO, isAuto);
        }
        getAdapter().notifyDataSetChanged();
    }

    public boolean getAutomotive() {
        return mIsAutomotive;
    }

    public void setCNMode(boolean isCN) {
        mIsCNMode = isCN;
        Bundle args = getArguments();
        if (args != null) {
            args.putBoolean(KEY_IS_CN, isCN);
        }
        getAdapter().notifyDataSetChanged();
    }

    public boolean getCNMode() {
        return mIsCNMode;
    }

    void swtichTabBarbkg(int orientation) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            getTabBar().setBackground(mTextureDrawable);
        }
    }

    class MyTabAdapter extends HtcTabFragmentPagerAdapter {
        public MyTabAdapter(Fragment host) {
            super(host);
        }

        @Override
        public Fragment getItem(String tag) {
            if (tag.equals("T1")) {
                return new TabFragment(1);
            } else if (tag.equals("T2")) {
                return new TabFragment(2);
            } else if (tag.equals("T3")) {
                return new TabFragment(3);
            }
            return null;
        }

        @Override
        public void onTabChanged(String previousTag, String currentTag) {
        }

        @Override
        public float getPageWidth(int position) {
            return mWidthFactor;
        }

        @Override
        public boolean isAutomotiveMode() {
            return mIsAutomotive;
        }

        @Override
        public boolean isCNMode() {
            return mIsCNMode;
        }
    }

}
