
package com.htc.sense.commoncontrol.demo.slidingmenu;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.htc.lib1.cc.app.SlidingActivity;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.view.tabbar.TabBar;
import com.htc.lib1.cc.view.viewpager.HtcPagerAdapter;
import com.htc.lib1.cc.view.viewpager.HtcViewPager;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.ActionBarItemView;
import com.htc.lib1.cc.widget.ActionBarText;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.lib1.cc.widget.HtcListItemSeparator;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.widget.SlidingMenu;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.actionbar.ActionBarDemoUtil;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

/**
 *
 */
public class SlidingMenuActivity extends SlidingActivity implements OnClickListener {

    private int mThemeResId = R.style.HtcDeviceDefault;

    private SlidingMenu sm = null;

    private ActionBarContainer mActionBarContainer = null;
    private ActionBarExt mActionBarExt = null;
    private ActionBarItemView mActionBarItemViewLeft = null;
    private ActionBarText mActionBarText = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThemeResId = CommonUtil.reloadDemoTheme(this, savedInstanceState);

        sm = getSlidingMenu();
        HtcViewPager htcviewpager = new HtcViewPager(this);
        htcviewpager.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT));
        setContentView(htcviewpager);

        // set the Behind View
        HtcListView mList = new HtcListView(this);
        mList.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
        setBehindContentView(mList, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // set the Behind View
        HtcListView mListRight = new HtcListView(this);
        mListRight.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
        sm.setSecondaryMenu(mListRight);
        mListRight.setAdapter(new ActionAdapter());
        mListRight.setOnItemClickListener(new ListItemClickListener());

        initSlidingMenu();
        initActionBarModule();

        ActionAdapter mAdapter = new ActionAdapter();
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(new ListItemClickListener());

        TabBar tabbar = new TabBar(this);
        HtcViewPager.LayoutParams params = new HtcViewPager.LayoutParams();
        params.gravity = Gravity.TOP;
        htcviewpager.addView(tabbar, params);

        htcviewpager.setAdapter(new MyPagerAdapter());
        tabbar.linkWithParent(htcviewpager);
        htcviewpager.setCurrentItem(0, false);

        HtcViewPager.SimpleOnPageChangeListener onPageChangeListener = new HtcViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                } else {
                    getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                }
            }
        };
        htcviewpager.setOnPageChangeListener(onPageChangeListener);

    }

    private static class MyPagerAdapter extends HtcPagerAdapter {

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
            return "Tab " + position;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            Context ctx = container.getContext();
            TextView content = new TextView(ctx);
            content.setGravity(Gravity.CENTER);
            content.setBackgroundColor(position % 2 == 0 ? Color.RED : Color.BLUE);
            if (position == 0) {
                content.setText("SlidingMenu.TOUCHMODE_FULLSCREEN");
            } else {
                content.setText("SlidingMenu.TOUCHMODE_MARGIN");
            }

            container.addView(content);
            return content;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private class ActionAdapter extends BaseAdapter {

        // private String[] ItemStrings = new String[] { "listitem1", "listitem2", "listitem3",
        // "listitem4", "listitem5", "listitem6" };

        // final int layouts[] = new int[] { R.layout.actionbarlistitem0,
        // R.layout.actionbarlistitem1, R.layout.actionbarlistitem2,
        // R.layout.actionbarlistitem3, R.layout.actionbarlistitem4};

        public int getCount() {
            return 10;
        }

        public Object getItem(int position) {
            return "pass";
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 1) {
                final LinearLayout ll = new LinearLayout(getBaseContext());
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                final TextView tv = new TextView(getBaseContext());
                tv.setGravity(Gravity.CENTER);
                tv.setText("Click to show a toast");
                ll.addView(tv);
                final ImageView iv = new ImageView(getBaseContext());
                iv.setImageResource(R.drawable.ic_launcher);
                iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getBaseContext(),
                                getResources().getString(R.string.toastOn), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
                ll.addView(iv);
                return ll;
            } else if (position % 5 == 0)
            {
                HtcListItemSeparator s = new HtcListItemSeparator(getBaseContext());
                s.setText(HtcListItemSeparator.TEXT_LEFT, "separator");
                return s;
            }
            else
            {
                HtcListItem i = new HtcListItem(getBaseContext()/* , HtcListItem.MODE_POPUPMENU */);
                HtcListItem1LineCenteredText text = new HtcListItem1LineCenteredText(getBaseContext());
                text.setText(position + ":" + "Click to close menu");
                i.addView(text);
                return i;
            }
        }
    }

    /**
     * This list item click listener implements very simple view switching by changing the primary
     * content text. The slider is closed when a selection is made to fully reveal the content.
     */
    private class ListItemClickListener implements HtcListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            sm.toggle();
        }
    }

    private void initSlidingMenu() {

        // customize the SlidingMenu
        sm.setShadowWidth(5);
        sm.setShadowDrawable(R.drawable.sliding_menu_shadow);
        setBehindWidthByOrientation(getResources().getConfiguration().orientation);
        sm.setMode(SlidingMenu.LEFT);
        // 设置slding menu的几种手势模式
        // TOUCHMODE_FULLSCREEN 全屏模式，在content页面中，滑动，可以打开sliding menu
        // TOUCHMODE_MARGIN 边缘模式，在content页面中，如果想打开slding ,你需要在屏幕边缘滑动才可以打开slding menu
        // TOUCHMODE_NONE 自然是不能通过手势打开啦
        // sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
    }

    private View getViewWithoutParent(View v) {
        if (null == v) return v;

        if (null == v.getParent()) return v;

        ViewGroup vg = ((ViewGroup) v.getParent());
        vg.removeView(v);

        return v;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setBehindWidthByOrientation(newConfig.orientation);
    }

    private void setBehindWidthByOrientation(int orientation) {
        // Checks the orientation of the screen
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            sm.setBehindWidth((int) (outMetrics.heightPixels * 0.8));
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            sm.setBehindWidth((int) (outMetrics.widthPixels * 0.8));
        }
    }

    @Override
    public void onClick(View arg0) {
        toggle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu subMenu = menu.addSubMenu("Mode");
        subMenu.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        subMenu.add(0, 0, 0, "LEFT");
        subMenu.add(0, 1, 0, "RIGHT");
        subMenu.add(0, 2, 0, "LEFT_RIGHT");
        subMenu.add(0, 3, 0, "TOUCHMODE_MARGIN");
        subMenu.add(0, 4, 0, "TOUCHMODE_FULLSCREEN");
        subMenu.add(0, 5, 0, "TOUCHMODE_NONE");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                sm.setMode(SlidingMenu.RIGHT);
                break;
            case 2:
                sm.setMode(SlidingMenu.LEFT_RIGHT);
                break;
            case 3:
                sm.setTouchModeBehind(SlidingMenu.TOUCHMODE_MARGIN);
                break;
            case 4:
                sm.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);
                break;
            case 5:
                sm.setTouchModeBehind(SlidingMenu.TOUCHMODE_NONE);
                break;
            case 0:
            default:
                sm.setMode(SlidingMenu.LEFT);
                break;
        }
        return true;
    }

    private void initActionBarModule() {
        ActionBarDemoUtil.clearInstance();
        ActionBarDemoUtil abdu = ActionBarDemoUtil.getInstance(this, false);

        mActionBarExt = CommonUtil.initHtcActionBar(this, false, false);
        mActionBarContainer = mActionBarExt.getCustomContainer();

        mActionBarText = abdu.initActionBarText();
        mActionBarText.setPrimaryText("Drawer Primary");
        mActionBarText.setSecondaryText("Drawer Secondary");
        mActionBarContainer.addCenterView(getViewWithoutParent(mActionBarText));

        mActionBarItemViewLeft = new ActionBarItemView(this);
        mActionBarItemViewLeft.setIcon(R.drawable.common_rearrange_rest);

        mActionBarItemViewLeft.setOnClickListener(this);
        mActionBarContainer.addStartView(getViewWithoutParent(mActionBarItemViewLeft));
    }

}
