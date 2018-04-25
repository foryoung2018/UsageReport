package com.htc.sense.commoncontrol.demo.gridview;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.htc.lib1.cc.widget.HtcGridItem;
import com.htc.lib1.cc.widget.HtcGridView;
import com.htc.lib1.cc.widget.HtcGridView.DeleteAnimationListener;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;


/**
 * A grid that displays a set of framed photos.
 *
 */
public class HtcGridViewDemo extends CommonDemoActivityBase {

    private boolean mIsAnimationRunning = false;
    private HtcGridView mGrid;
    private GridViewAdapter mGridViewAdapter;
    private ImageClickListener mImageClickLinstener;
    private ArrayList<Integer> mDeleteItem = new ArrayList<Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setBackgroundDrawableResource(com.htc.lib1.cc.R.drawable.common_app_bkg);

        setContentView(R.layout.htcgridview_grid_2);
        mGridViewAdapter = new GridViewAdapter(this);
        mImageClickLinstener = new ImageClickListener();

        mGrid = (HtcGridView) findViewById(R.id.myGrid);
        mGrid.setMode(HtcGridView.MODE_GENERIC);
        int numColumns = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 3 : 5;
        mGrid.setNumColumns(numColumns);
        mGrid.setAdapter(mGridViewAdapter);
        mGrid.setOnItemClickListener((OnItemClickListener) mImageClickLinstener);
        mGrid.setDeleteAnimationListener(new DeleteAnimationListener() {
            @Override
            public void onAnimationEnd() {
                mIsAnimationRunning = false;
            }

            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationUpdate() {
                mGridViewAdapter.notifyDataSetChanged();
            }
        });
    }

    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mGrid.setNumColumns(3);
        } else {
            mGrid.setNumColumns(5);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.htcgridview_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.delete_item:
            deleteItem();
            return true;
        case R.id.intro_item:
            mGrid.startIntroAnimation();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void deleteItem() {
        if(!mIsAnimationRunning) {
            mIsAnimationRunning = true;
            mDeleteItem.clear();
            for (int i = 0; i < mGridViewAdapter.getCount(); i++) {

                if (mGridViewAdapter.getCheckState(i)) {
                    mDeleteItem.add(i);
                }
            }
            if (mDeleteItem.size() < 1) {
                return;
            } else {
                mIsAnimationRunning = false;
            }

            mGrid.disableTouchEventInAnim();
            for (int i = mDeleteItem.size() - 1; i >= 0; i--) {
                mGridViewAdapter.removeItem(mDeleteItem.get(i));
            }

            mGrid.setDelPositionsList(mDeleteItem);

        }
    }

    public class ImageClickListener implements
            AdapterView.OnItemClickListener {

        public void onItemClick(AdapterView<?> parent, View view,
                int position, long id) {
            HtcGridItem item = (HtcGridItem) view;
            mGridViewAdapter.setCheckState(position, !mGridViewAdapter.getCheckState(position));
            item.setItemDeleted(mGridViewAdapter.getCheckState(position));
        }

    }

    class ViewHolder{
        ImageView img;
        ImageView delete;
        TextView text1;
        TextView text2;
    }

    /**
     * Total Grid Item Count
     */
    int mItemCount = 1000;
    private ArrayList<Integer> mThumbIdArrayList = new ArrayList<Integer>();
    private ArrayList<Boolean> mItemCheckedArrayList = new ArrayList<Boolean>();
    private ArrayList<String> mTexts = new ArrayList<String>();
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

        public GridViewAdapter(Context c) {
            for (int i = 0; i < mItemCount; i++) {
                mItemCheckedArrayList.add(i, false);
                mThumbIdArrayList.add(i, mThumbIds[i%8]);
                mTexts.add("Item " + i);
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
                convertView = new HtcGridItem(parent.getContext());
            }
            HtcGridItem item = (HtcGridItem) convertView;
            item.setPrimaryText(mTexts.get(position));
            item.setSecondaryText("Secondary");
            item.getImage().setImageResource(mThumbIdArrayList.get(position));
            item.setItemDeleted(IsChecked(position));
            return convertView;

        }

        public boolean getCheckState(int index) {
            return mItemCheckedArrayList.get(index);
        }

        public void setCheckState(int index, boolean state) {
            mItemCheckedArrayList.set(index, state);
        }

        public void removeItem(int index) {
            mItemCheckedArrayList.remove(index);
            mThumbIdArrayList.remove(index);
            mTexts.remove(index);

        }

        public void addItem(ArrayList<Integer> index) {
            for (int i = 0; i < index.size(); i++) {
                mItemCheckedArrayList.add(index.get(i), false);
                mThumbIdArrayList.add(index.get(i), mThumbIds[3]);
                mTexts.add(index.get(i), "Added Item");
            }
        }

        public boolean IsChecked(int position) {
            return mItemCheckedArrayList.get(position);
        }
    }

}