
package com.htc.sense.commoncontrol.demo.htcviewpager;

import com.htc.lib1.cc.view.viewpager.HtcViewPager;
import com.htc.lib1.cc.widget.HtcFooter;
import com.htc.lib1.cc.widget.HtcFooterButton;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;

import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class HtcPagerFragmentDemoActivity extends CommonDemoActivityBase {
    public static final String LOG_TAG = "HtcPagerFragmentDemo";

    private DemoPagerFragment mPagerFragment;
    private boolean mShowTabBar = true;
    private boolean mShowFootBar = false;
    private Drawable mPageMarginDrawable;

    private static final int MENU_TOGGLE_TABBAR = Menu.FIRST;
    private static final int MENU_TOGGLE_ORIENTATION = Menu.FIRST + 1;
    private static final int MENU_TOGGLE_FOOTBAR = Menu.FIRST + 2;
    private static final int MENU_TOGGLE_PADDING = Menu.FIRST + 3;
    private static final int MENU_TOGGLE_PAGE_MARGIN = Menu.FIRST + 4;
    private static final int MENU_TOGGLE_PAGE_MARGIN_DRAWABLE = Menu.FIRST + 5;
    private static final int MENU_TOGGLE_WIDTH_FACTOR = Menu.FIRST + 6;
    private static final int MENU_TOGGLE_AUTOMOTIVE = Menu.FIRST + 7;
    private static final int MENU_TOGGLE_CN_MODE = Menu.FIRST + 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getFragmentManager();
        if (savedInstanceState != null) {
            mPagerFragment = (DemoPagerFragment) getFragmentManager().findFragmentByTag("PagerFragment");
        }
        if (mPagerFragment == null) {
            mPagerFragment = new DemoPagerFragment(1.0f, false, false);
        }
        if (!mPagerFragment.isAdded()) {
            fm.beginTransaction().replace(android.R.id.content, mPagerFragment, "PagerFragment").commit();
        }

        HtcFooter htcFooter = new HtcFooter(this);
        HtcFooterButton footerButton = new HtcFooterButton(this);
        footerButton.setText("Foot1");
        htcFooter.addView(footerButton);
        footerButton = new HtcFooterButton(this);
        footerButton.setText("Foot2");
        htcFooter.addView(footerButton);
        mPagerFragment.setFooter(htcFooter);
        mPagerFragment.hideFooter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_TOGGLE_TABBAR, 0, "Toogle TabBar");
        menu.add(0, MENU_TOGGLE_ORIENTATION, 0, "Toogle Orientation");
        menu.add(0, MENU_TOGGLE_FOOTBAR, 0, "Toogle FootBar");
        menu.add(0, MENU_TOGGLE_PADDING, 0, "Toogle Padding");
        menu.add(0, MENU_TOGGLE_PAGE_MARGIN, 0, "Toogle PagerMargin");
        menu.add(0, MENU_TOGGLE_PAGE_MARGIN_DRAWABLE, 0, "Toogle PagerMarginDrawable");
        menu.add(0, MENU_TOGGLE_WIDTH_FACTOR, 0, "Toogle WidthFactor");
        menu.add(0, MENU_TOGGLE_AUTOMOTIVE, 0, "Toogle Automotive");
        menu.add(0, MENU_TOGGLE_CN_MODE, 0, "Toogle CN Mode");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        HtcViewPager htcViewPager = mPagerFragment.getPager();
        switch (item.getItemId()) {
            case MENU_TOGGLE_TABBAR:
                if (mShowTabBar) {
                    mPagerFragment.hideTabBar();
                } else {
                    mPagerFragment.showTabBar();
                }
                mShowTabBar = !mShowTabBar;
                return true;

            case MENU_TOGGLE_ORIENTATION:
                htcViewPager.setOrientation(htcViewPager.getOrientation() == HtcViewPager.ORIENTATION_HORIZONTAL ? HtcViewPager.ORIENTATION_VERTICAL : HtcViewPager.ORIENTATION_HORIZONTAL);
                return true;

            case MENU_TOGGLE_FOOTBAR:
                if (mShowFootBar) {
                    mPagerFragment.hideFooter();
                } else {
                    mPagerFragment.showFooter();
                }
                mShowFootBar = !mShowFootBar;
                return true;

            case MENU_TOGGLE_PADDING:
                final int padding = htcViewPager.getPaddingTop() > 0 ? 0 : 100;
                htcViewPager.setPaddingRelative(padding, padding, padding, padding);
                return true;

            case MENU_TOGGLE_PAGE_MARGIN:
                final int pageMargin = htcViewPager.getPageMargin() > 0 ? 0 : 100;
                htcViewPager.setPageMargin(pageMargin);
                return true;

            case MENU_TOGGLE_PAGE_MARGIN_DRAWABLE:
                mPageMarginDrawable = (mPageMarginDrawable == null ? new ColorDrawable(Color.CYAN) : null);
                htcViewPager.setPageMarginDrawable(mPageMarginDrawable);
                return true;

            case MENU_TOGGLE_WIDTH_FACTOR:
                // TODO why this is not effect,there are something wrong in datasetChanged in
                // HtcViewPager
                final float widthFactor = (mPagerFragment.getPageWidthFactor() == 1.0f ? 0.8f : 1.0f);
                mPagerFragment.setPageWidthFactor(widthFactor);
                recreate();
                return true;
            case MENU_TOGGLE_AUTOMOTIVE:
                // TODO why this is not effect,there are something wrong in datasetChanged in
                // HtcViewPager
                mPagerFragment.setAutomotive(!mPagerFragment.getAutomotive());
                recreate();
                return true;
            case MENU_TOGGLE_CN_MODE:
                // TODO why this is not effect,there are something wrong in datasetChanged in
                // HtcViewPager
                mPagerFragment.setCNMode(!mPagerFragment.getCNMode());
                recreate();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
