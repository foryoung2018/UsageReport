
package com.htc.sense.commoncontrol.demo.layout;

import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.lib1.cc.widget.HtcOverlapLayout;

public class HtcOverLapDemo extends CommonDemoActivityBase {
    private ListView listview;
    private HtcCheckBox cb;

    private static final int CHECK_BOX_ID = 123;
    private HtcOverlapLayout overlayLayout = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.overlaplayout);
        overlayLayout = (HtcOverlapLayout) findViewById(R.id.overlap_layout);
        listview = (ListView) findViewById(R.id.list);

        listview.setAdapter(new MyAdapter(this));

        // ListView handles the check state of HtcCheckBox
        listview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                cb = (HtcCheckBox) view.findViewById(CHECK_BOX_ID);
                cb.setChecked(!cb.isChecked());
            }
        });
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        getActionBar().addTab(getActionBar().newTab().setText("Translucent Off").setTabListener(new TabListener() {
            @Override
            public void onTabUnselected(Tab tab, FragmentTransaction ft) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                overlayLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                // overlayLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }

            @Override
            public void onTabReselected(Tab tab, FragmentTransaction ft) {
                // TODO Auto-generated method stub
            }
        }));
        getActionBar().addTab(getActionBar().newTab().setText("Translucent On").setTabListener(new TabListener() {

            @Override
            public void onTabUnselected(Tab tab, FragmentTransaction ft) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                // overlayLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                overlayLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                // overlayLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }

            @Override
            public void onTabReselected(Tab tab, FragmentTransaction ft) {
                // TODO Auto-generated method stub

            }
        }));
    }

    public void showActionBar(View view) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null && !actionBar.isShowing()) {
            actionBar.show();
            overlayLayout.isActionBarVisible(true);
        }
    }

    public void hideActionBar(View view) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null && actionBar.isShowing()) {
            actionBar.hide();
            overlayLayout.isActionBarVisible(false);
            // overlayLayout.setInsetStatusBar(false);
        }
    }

    private static final String[] GENRES = new String[] {
            "Action",
            "Adventure", "Animation", "Children", "Comedy", "Documentary",
            "Drama", "Foreign", "History", "Independent", "Romance", "Sci-Fi",
            "Television", "Thriller"
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

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

    @Override
    protected void applyCustomWindowFeature() {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
    }
}
