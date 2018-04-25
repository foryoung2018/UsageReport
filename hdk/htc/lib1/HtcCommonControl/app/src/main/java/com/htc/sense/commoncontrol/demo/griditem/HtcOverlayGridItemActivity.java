package com.htc.sense.commoncontrol.demo.griditem;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.htc.lib1.cc.widget.HtcGridView;
import com.htc.lib1.cc.widget.HtcOverlayGridItem;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;



/**
 * A grid that displays a set of framed photos.
 *
 */
public class HtcOverlayGridItemActivity extends CommonDemoActivityBase {

    private HtcGridView mGrid;
    private GridViewAdapter mAdapter;

    final static int SHOW_INDICATOR = 4;
    boolean mShowIndicator = false;
    String mShowIndicatorText = "Show Indicator";
    String mHideIndicatorText = "Hide Indicator";
    LayoutInflater mInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawableResource(com.htc.lib1.cc.R.drawable.common_app_bkg);
        setContentView(R.layout.htcgriditem_main);
        mAdapter = new GridViewAdapter(this);
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        mGrid = (HtcGridView) findViewById(R.id.grid);
        mGrid.setAdapter(mAdapter);
        int numColumns = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 3 : 5;
        mGrid.setNumColumns(numColumns);
        // Specify MODE_OVERLAY mode
        mGrid.setMode(HtcGridView.MODE_OVERLAY);

        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HtcOverlayGridItem item = (HtcOverlayGridItem)view;
                // Set the deleted state for the clicked grid item.
                if (mAdapter.getCheckState(position)) {
                    item.setItemDeleted(false);
                    mAdapter.setCheckState(position, false);
                } else {
                    item.setItemDeleted(true);
                    mAdapter.setCheckState(position, true);
                }

            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mThumbIdArrayList.clear();
        mItemCheckedArrayList.clear();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        String title = mShowIndicator ? mHideIndicatorText : mShowIndicatorText;
        menu.add(0, SHOW_INDICATOR, 0, title);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        String title = "";
        switch(item.getItemId()){
        case SHOW_INDICATOR:
            mShowIndicator = !mShowIndicator;
            title = mShowIndicator ? mHideIndicatorText : mShowIndicatorText;
            item.setTitle(title);
            mGrid.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            break;
        }
        return true;
    }

    /**
     * Total Grid Item Count
     */
    int mItemCount = 150;
    private String[] mPrimaryTexts;
    private ArrayList<Integer> mThumbIdArrayList = new ArrayList<Integer>();
    private ArrayList<Boolean> mItemCheckedArrayList = new ArrayList<Boolean>();
    /**
     * Image Source Array
     */
    private Integer[] mThumbIds = { R.drawable.htcgridview_sample_0,
            R.drawable.htcgridview_sample_1,
            R.drawable.htcgridview_sample_2,
            R.drawable.htcgridview_sample_3,
            R.drawable.htcgridview_sample_4,
            R.drawable.htcgridview_sample_5,
            R.drawable.htcgridview_sample_6,
            R.drawable.htcgridview_sample_7,};

    public class GridViewAdapter extends BaseAdapter {
        Drawable indicator;
        public GridViewAdapter(Context c) {
            mPrimaryTexts = new String[mItemCount];
            indicator = getResources().getDrawable(R.drawable.icon_indicator_facebook_s);
            for (int i = 0; i < mItemCount; i++) {
                mItemCheckedArrayList.add(i, false);
                mThumbIdArrayList.add(i, mThumbIds[i%8]);
                mPrimaryTexts[i] = "Item " + i + " xxxxxxx";
            }
        }

        public int getCount() {
            return mThumbIdArrayList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = new HtcOverlayGridItem(parent.getContext());
            }
            HtcOverlayGridItem gridItem = (HtcOverlayGridItem) convertView;
            gridItem.setPrimaryText(mPrimaryTexts[position]);

            // Show/hide indicator
            gridItem.setIndicator(mShowIndicator ? indicator : null);

            // Set the image
            gridItem.getImage().setImageResource(mThumbIdArrayList.get(position));

            // Set the deleted state according to the current checked state of this grid item.
            gridItem.setItemDeleted(this.getCheckState(position));

            return convertView;

        }

        public boolean getCheckState(int index) {
            return mItemCheckedArrayList.get(index);
        }

        public void setCheckState(int index, boolean state) {
            mItemCheckedArrayList.set(index, state);
        }

        public boolean IsChecked(int position) {
            return mItemCheckedArrayList.get(position);
        }
    }

    public HtcGridView getGridView(){
        return mGrid;
    }

    public GridViewAdapter getAdapter() {
        return mAdapter;
    }

    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            final int numColumns = 3;
            mGrid.setNumColumns(numColumns);
        } else {
            final int numColumns = 5;
            mGrid.setNumColumns(numColumns);
        }
    }

}