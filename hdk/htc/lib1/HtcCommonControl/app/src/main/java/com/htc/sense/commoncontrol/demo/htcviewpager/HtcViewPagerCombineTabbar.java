
package com.htc.sense.commoncontrol.demo.htcviewpager;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.view.tabbar.TabBar;
import com.htc.lib1.cc.view.viewpager.HtcPagerAdapter;
import com.htc.lib1.cc.view.viewpager.HtcViewPager;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;

public class HtcViewPagerCombineTabbar extends CommonDemoActivityBase {

    private Drawable mTextureDrawable;
    TabBar mTabbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HtcViewPager htcviewpager = new HtcViewPager(this);
        htcviewpager.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT));
        setContentView(htcviewpager);

        mTabbar = new TabBar(this);
        HtcViewPager.LayoutParams params = new HtcViewPager.LayoutParams();
        params.gravity = Gravity.TOP;
        htcviewpager.addView(mTabbar, params);
        MyAdapter adapter = new MyAdapter();
        htcviewpager.setAdapter(adapter);
        htcviewpager.setOnPageChangeListener(adapter);
        mTabbar.linkWithParent(htcviewpager);

        htcviewpager.setCurrentItem(2, false);
        mTextureDrawable = HtcCommonUtil.getCommonThemeTexture(this, com.htc.lib1.cc.R.styleable.CommonTexture_android_headerBackground);
        swtichTabBarbkg(getResources().getConfiguration().orientation);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        swtichTabBarbkg(newConfig.orientation);
    }

    void swtichTabBarbkg(int orientation) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            mTabbar.setBackground(mTextureDrawable);
        }
    }

    public static class MyAdapter extends HtcPagerAdapter implements HtcViewPager.OnPageChangeListener {
        public MyAdapter() {
        }

        @Override
        public int getPageCount(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public CharSequence getPageTitle(int position) {
            return "test " + position;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            Context ctx = container.getContext();
            TextView content = new TextView(ctx);
            content.setTextSize(50);
            content.setGravity(Gravity.CENTER);
            content.setBackgroundColor(position % 2 == 0 ? Color.RED : Color.BLUE);
            content.setText("Page " + position);

            container.addView(content);
            return content;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int i) {
        }
    }
}
