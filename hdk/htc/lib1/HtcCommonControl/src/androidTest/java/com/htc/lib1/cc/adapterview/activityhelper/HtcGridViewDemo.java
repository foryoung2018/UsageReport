
package com.htc.lib1.cc.adapterview.activityhelper;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.ActionBarText;
import com.htc.lib1.cc.widget.HtcGridItem;
import com.htc.lib1.cc.widget.HtcGridView;
import com.htc.lib1.cc.widget.HtcGridView.DeleteAnimationListener;
import com.htc.lib1.cc.widget.IHtcAbsListView;

import java.util.ArrayList;

public class HtcGridViewDemo extends ActivityBase {

    private ActionBarExt actionBarExt = null;
    private ActionBarContainer actionBarContainer = null;
    private HtcGridView mGrid;
    private GridViewAdapter mGridViewAdapter;
    private ImageClickListener mImageClickLinstener;
    private ArrayList<Integer> mDeleteItem = new ArrayList<Integer>();
    private ArrayList<Integer> mAddItem = new ArrayList<Integer>();
    public static final int NUM_COLUMNS_PORTRAIT = 3;
    public static final int NUM_COLUMNS_LANDSCAPE = 5;

    // private scrollToAddedPositionListener mscrollToAddedPositionListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBarExt = new ActionBarExt(this, getActionBar());
        actionBarContainer = actionBarExt.getCustomContainer();
        ActionBarText actionBarText = new ActionBarText(this);
        actionBarText.setPrimaryText("HtcGridView");
        actionBarContainer.addCenterView(actionBarText);

        setContentView(R.layout.htcgridview_grid_2);
        getActionBar().setTitle("HtcGridViewDemo");
        mGridViewAdapter = new GridViewAdapter(this);
        mImageClickLinstener = new ImageClickListener();

        mGrid = (HtcGridView) findViewById(R.id.myGrid);
        mGrid.setMode(HtcGridView.MODE_GENERIC);
        int numColumns = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? NUM_COLUMNS_PORTRAIT : NUM_COLUMNS_LANDSCAPE;
        mGrid.setNumColumns(numColumns);
        mGrid.setAdapter(mGridViewAdapter);
        mGrid.setOnItemClickListener(mImageClickLinstener);
        mGrid.setVerticalScrollBarEnabled(false);
        mGrid.setDeleteAnimationListener(new DeleteAnimationListener() {
            @Override
            public void onAnimationEnd() {
                // mIsAnimationRunning = false;
            }

            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationUpdate() {
                mGridViewAdapter.notifyDataSetChanged();
            }
        });
        // mscrollToAddedPositionListener = new scrollToAddedPositionListener()
        // {
        // public void onScrollEnd() {
        // AddItem(mAddItem);
        // }
        // };
        // mGrid.setScrollToAddedPositionListener(mscrollToAddedPositionListener);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mGrid.setNumColumns(NUM_COLUMNS_PORTRAIT);
        } else {
            mGrid.setNumColumns(NUM_COLUMNS_LANDSCAPE);
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
            case R.id.add_item_front:
                mAddItem.add(0);
                mAddItem.add(3);
                mGrid.setSelection(0);
                // mGrid.scrollToFirstAddedPosition(0);
                return true;
            case R.id.add_item_middle:
                mAddItem.add(50);
                mAddItem.add(53);
                mGrid.setSelection(50);
                // mGrid.scrollToFirstAddedPosition(50);
                return true;
            case R.id.add_item_back:
                int size = mGrid.getAdapter().getCount();
                mAddItem.add(size);
                mAddItem.add(size + 1);
                mGrid.setSelection(size);
                // mGrid.scrollToFirstAddedPosition(size);
                return true;
            case R.id.intro_item:
                mGrid.startIntroAnimation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteItem() {
        mDeleteItem.clear();
        for (int i = 0; i < mGridViewAdapter.getCount(); i++) {

            if (mGridViewAdapter.getCheckState(i)) {
                mDeleteItem.add(i);
            }
        }
        if (mDeleteItem.size() < 1) {
            return;
        }

        for (int i = mDeleteItem.size() - 1; i >= 0; i--) {
            mGridViewAdapter.removeItem(mDeleteItem.get(i));
        }

        mGrid.setDelPositionsList(mDeleteItem);

    }

    private void AddItem(ArrayList<Integer> index) {
        mGridViewAdapter.addItem(index);
        // mGrid.setAddPositionsList(index);
    }

    public class ImageClickListener implements
            AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HtcGridItem item = (HtcGridItem) view;
            mGridViewAdapter.setCheckState(position, !mGridViewAdapter.getCheckState(position));
            item.setItemDeleted(mGridViewAdapter.getCheckState(position));

        }

    }

    class ViewHolder {
        ImageView img;
        ImageView delete;
        TextView text1;
        TextView text2;
    }

    /**
     * Total Grid Item Count
     */
    public static final int ITEMCOUNT = 60;
    private ArrayList<Integer> mThumbIdArrayList = new ArrayList<Integer>();
    private ArrayList<Boolean> mItemCheckedArrayList = new ArrayList<Boolean>();
    private ArrayList<String> mTexts = new ArrayList<String>();
    /**
     * Image Source Array
     */
    private Integer[] mThumbIds = {
            R.drawable.htcgridview_sample_0,
            R.drawable.htcgridview_sample_1,
            R.drawable.htcgridview_sample_2,
            R.drawable.htcgridview_sample_3,
            R.drawable.htcgridview_sample_4,
            R.drawable.htcgridview_sample_5,
            R.drawable.htcgridview_sample_6,
            R.drawable.htcgridview_sample_7,
    };

    public class GridViewAdapter extends BaseAdapter {

        public GridViewAdapter(Context c) {
            for (int i = 0; i < ITEMCOUNT; i++) {
                mItemCheckedArrayList.add(i, false);
                mThumbIdArrayList.add(i, mThumbIds[i % 8]);
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
            if (convertView == null) {
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

    public void improveCoverage() {
        HtcGridView mGridNew = new HtcGridView(this);
        mGridNew.setAdapter(mGridViewAdapter);
        mGridNew.enableAnimation(IHtcAbsListView.ANIM_OVERSCROLL | IHtcAbsListView.ANIM_DEL, false);
        mGridNew.disableTouchEventInAnim();
        mGridNew.endDelAnimator();
        mGridNew.setOnPullDownListener(null);
        mGridNew.setVerticalScrollbarPosition(0);
        try {
            mGridNew.setMode(-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mGridNew.setMode(HtcGridView.MODE_GENERIC);
        mGridNew.setMode(HtcGridView.MODE_GENERIC);
        mGridNew.setMode(HtcGridView.MODE_NONE);
        try {
            mGridNew.setDelPositionsList(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
