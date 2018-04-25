
package com.htc.sense.commoncontrol.demo.footerbar;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.lib1.cc.widget.HtcFooter;
import com.htc.lib1.cc.widget.HtcFooterButton;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class FooterbarOverlay extends CommonDemoActivityBase {
    private HtcListView listview;
    private HtcCheckBox cb;
    private HtcFooter mHtcFooter = null;
    private static final int M1 = Menu.FIRST;
    private static final int M2 = Menu.FIRST + 1;

    private static final int CHECK_BOX_ID = 123;
    private ActionBarContainer actionBarContainer = null;
    private HtcFooterButton mHtcFooterButton = null;
    // private HtcOverlapLayout overlayLayout=null;
    HtcCommonUtil.ThemeChangeObserver mThemeChangeObserver;
    boolean mForceRecreate = true;
    boolean mNeedRecreate = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // actionBarExt.enableHTCLandscape(true);
        actionBarContainer = mActionBarExt.getCustomContainer();
        actionBarContainer.setProgressVisibility(View.VISIBLE);

        setContentView(R.layout.footerbaroverlay);
        listview = (HtcListView) findViewById(R.id.list);
        mHtcFooter = (HtcFooter) findViewById(R.id.footerbar);

        listview.setAdapter(new MyAdapter(this));

        // ListView handles the check state of HtcCheckBox
        listview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                cb = (HtcCheckBox) view.findViewById(CHECK_BOX_ID);
                cb.setChecked(!cb.isChecked());
                cb.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
            }
        });

    }

    private boolean isHorizontal() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (isHorizontal()) {
            mHtcFooter.enableThumbMode(false);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem thumbModeItem = menu.add(0, M1, Menu.NONE, "EnableThumbMode");
        MenuItem pureModeItem = menu.add(0, M2, Menu.NONE, "EnablePurebMode");
        thumbModeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        pureModeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        thumbModeItem.setCheckable(true);
        pureModeItem.setCheckable(true);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case M1:
                if (item.isChecked()) {
                    item.setChecked(false);
                    mHtcFooter.enableThumbMode(false);
                } else {
                    item.setChecked(true);
                    mHtcFooter.enableThumbMode(true);
                }
                break;
            case M2:
                if (item.isChecked()) {
                    item.setChecked(false);
                    mHtcFooter.setBackgroundStyleMode(HtcFooter.STYLE_MODE_LIGHT);
                } else {
                    item.setChecked(true);
                    mHtcFooter.setBackgroundStyleMode(HtcFooter.STYLE_MODE_PURELIGHT);
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void applyCustomWindowFeature() {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
    }

    private void initTheme() {
        if (mThemeChangeObserver == null) {
            mThemeChangeObserver = new HtcCommonUtil.ThemeChangeObserver() {

                @Override
                public void onThemeChange(int type) {
                    // TODO Auto-generated method stub
                    if (mForceRecreate) {
                        mForceRecreate = false;
                        mNeedRecreate = true;
                        onResume();
                    } else {
                        if (type == HtcCommonUtil.TYPE_THEME) {
                            mNeedRecreate = true;
                        }
                    }
                }
            };
            HtcCommonUtil.registerThemeChangeObserver(this, HtcCommonUtil.TYPE_THEME, mThemeChangeObserver);
        }
    }

    private void applyTheme() {
        HtcCommonUtil.updateCommonResConfiguration(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mThemeChangeObserver != null) {
            HtcCommonUtil.unregisterThemeChangeObserver(HtcCommonUtil.TYPE_THEME, mThemeChangeObserver);
        }
        mThemeChangeObserver = null;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (mNeedRecreate) {
            getWindow().getDecorView().postOnAnimation(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    recreate();
                }
            });
        }
    }

    private static final String[] GENRES = new String[] {
            "Action",
            "Adventure", "Animation", "Children", "Comedy", "Documentary",
            "Drama", "Foreign", "History", "Independent", "Romance", "Sci-Fi",
            "Television", "Thriller"
    };

    class MyAdapter extends BaseAdapter {

        private Context mContext;

        MyAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return GENRES.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup root) {
            HtcListItem item = new HtcListItem(mContext);
            HtcListItem1LineCenteredText tv = new HtcListItem1LineCenteredText(
                    mContext);
            HtcCheckBox cb = new HtcCheckBox(mContext);

            tv.setText(GENRES[position]);
            cb.setFocusable(false);
            cb.setClickable(false);
            cb.setId(CHECK_BOX_ID);

            item.addView(tv);
            item.addView(cb);
            return item;
        }

    }

}
