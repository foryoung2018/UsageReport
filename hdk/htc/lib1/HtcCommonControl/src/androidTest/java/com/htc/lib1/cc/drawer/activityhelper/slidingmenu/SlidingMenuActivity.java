
package com.htc.lib1.cc.drawer.activityhelper.slidingmenu;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.htc.lib1.cc.test.R;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.view.viewpager.HtcPagerAdapter;
import com.htc.lib1.cc.view.viewpager.HtcViewPager;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.ActionBarText;
import com.htc.lib1.cc.widget.SlidingMenu;

/**
 *
 */
public class SlidingMenuActivity extends ActivityBase {
    public static final String KEY_TOUCH_MODE = "touchMode";
    public static final String KEY_DISPLAY_MODE = "displayMode";
    public static final String KEY_SLIDE_STYLE = "slideStyle";
    public static final String KEY_ACTIONBAR_OVERLAY = "actionbarOverlay";

    private static final int SHADOW_WIDTH = 5;
    private static final float WIDTH_SCALE = 0.8f;

    private int mTouchMode;
    private int mDisplayMode;
    private int mSlideStyle;
    private boolean mActionbarOverlay;

    public SlidingMenu mSlidingMenu = null;

    private LayoutInflater mInflater;

    private static final int PAGE_COUNT = 2;
    private static String mPageStr;

    private void getValueFromIntent() {
        final Intent i = getIntent();
        if (null == i) {
            return;
        }

        mTouchMode = i.getIntExtra(KEY_TOUCH_MODE, SlidingMenu.TOUCHMODE_FULLSCREEN);
        mDisplayMode = i.getIntExtra(KEY_DISPLAY_MODE, SlidingMenu.LEFT_RIGHT);
        mSlideStyle = i.getIntExtra(KEY_SLIDE_STYLE, SlidingMenu.SLIDING_WINDOW);
        mActionbarOverlay = i.getBooleanExtra(KEY_ACTIONBAR_OVERLAY, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getValueFromIntent();
        mInflater = getLayoutInflater();
        mPageStr = getString(R.string.viewPage);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (mActionbarOverlay) {
            getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        }

        final ActionBarExt abe = new ActionBarExt(this, getActionBar());
        final ActionBarText abt = new ActionBarText(this);
        abt.setPrimaryText("ActionBar");
        abe.getCustomContainer().addCenterView(abt);

        setContentView(R.layout.mainactivity);
        HtcViewPager pager = (HtcViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new CustomPagerAdapter());

        mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.attachToActivity(this, mSlideStyle, mActionbarOverlay);
        mSlidingMenu.setTouchModeAbove(mTouchMode);
        mSlidingMenu.setMode(mDisplayMode);
        mSlidingMenu.setShadowWidth(SHADOW_WIDTH);
        mSlidingMenu.setShadowDrawable(R.drawable.sliding_menu_shadow);

        setBehindWidthByOrientation(getResources().getConfiguration().orientation);

        final TextView tvLeft = (TextView) mInflater.inflate(R.layout.textview, null);
        tvLeft.setText(R.string.leftMenu);
        tvLeft.setBackgroundResource(android.R.color.holo_green_light);
        tvLeft.setId(android.R.id.text1);
        tvLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tvLeft.setText(R.string.clickTest);
            }
        });
        mSlidingMenu.setMenu(tvLeft);

        final TextView tvRight = (TextView) mInflater.inflate(R.layout.textview, null);
        tvRight.setText(R.string.rightMenu);
        tvRight.setBackgroundResource(android.R.color.holo_orange_light);
        mSlidingMenu.setSecondaryMenu(tvRight);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setBehindWidthByOrientation(newConfig.orientation);
    }

    private void setBehindWidthByOrientation(int orientation) {
        final DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

        if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
            mSlidingMenu.setBehindWidth((int) (outMetrics.heightPixels * WIDTH_SCALE));
        } else if (Configuration.ORIENTATION_PORTRAIT == orientation) {
            mSlidingMenu.setBehindWidth((int) (outMetrics.widthPixels * WIDTH_SCALE));
        }
    }

    private static class CustomPagerAdapter extends HtcPagerAdapter {

        @Override
        public int getPageCount(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public CharSequence getPageTitle(int position) {
            return mPageStr + position;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            final TextView tv = (TextView) LayoutInflater.from(container.getContext()).inflate(
                    R.layout.textview, null);
            tv.setText(mPageStr + position);
            container.addView(tv);
            return tv;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
