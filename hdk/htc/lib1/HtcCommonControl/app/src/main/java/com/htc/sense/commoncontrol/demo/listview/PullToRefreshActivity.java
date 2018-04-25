
package com.htc.sense.commoncontrol.demo.listview;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.widget.OnPullDownListener;

public class PullToRefreshActivity extends CommonDemoActivityBase {

    ActionBarContainer mActionBarContainer;

    boolean mUpdatingFlag = true;

    Runnable mUpdateRunnable;

    Handler mHandler = new Handler();

    int mItemCount = 5;

    ArrayList<String> mDataSet = new ArrayList<String>();

    MyAdapter mAdapter;

    LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setBackgroundDrawableResource(com.htc.lib1.cc.R.drawable.common_app_bkg);
        setContentView(R.layout.pulltorefresh_listview_layout);
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarContainer = mActionBarExt.getCustomContainer();
        mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_NORMAL);

        HtcListView list = (HtcListView) findViewById(android.R.id.list);
        mAdapter = new MyAdapter();
        list.setAdapter(mAdapter);

        list.setOnPullDownListener(new OnPullDownListener() {
            @Override
            public void onGapChanged(int gap, int maxGap) {
                if (!(mActionBarContainer.getUpdatingState() == ActionBarContainer.UPDATING_MODE_UPDATING)) {
                    if (mActionBarContainer.getRotationProgress() == 0) {
                        mActionBarContainer.setRotationMax(maxGap);
                    }
                    mActionBarContainer.setRotationProgress(gap);
                }
            }

            @Override
            public void onPullDownFinish() {
                if (mUpdatingFlag) {
                    if (mUpdateRunnable == null) {
                        mUpdateRunnable = new Runnable() {
                            public void run() {
                                mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_NORMAL);
                                mActionBarContainer.setUpdatingViewText(
                                        ActionBarContainer.UPDATING_MODE_PULLDOWN, "LAST UPDATED PM10:10");

                                // Add an item
                                mDataSet.add("Added Item " + (mDataSet.size()));
                                mAdapter.notifyDataSetChanged();
                            }
                        };
                    }
                    mHandler.postDelayed(mUpdateRunnable, 1500);
                    mUpdatingFlag = false;
                }
            }

            @Override
            public void onPullDownCancel() {
                if (!(mActionBarContainer.getUpdatingState() == ActionBarContainer.UPDATING_MODE_UPDATING)) {
                    mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_NORMAL);
                }

            }

            @Override
            public void onPullDownToBoundary() {
                if (!(mActionBarContainer.getUpdatingState() == ActionBarContainer.UPDATING_MODE_UPDATING)) {
                    mActionBarContainer.setRotationProgress(mActionBarContainer.getRotationMax());
                }
            }

            @Override
            public void onPullDownRelease() {
                if (mActionBarContainer.getUpdatingState() == ActionBarContainer.UPDATING_MODE_PULLDOWN) {
                    mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_UPDATING,
                            "UPDATING... (10/10)");
                    mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_UPDATING);
                    mUpdatingFlag = true;
                }
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        public MyAdapter() {
            for (int i = 0; i < mItemCount; i++) {
                mDataSet.add("Item " + i);
            }
        }

        @Override
        public int getCount() {
            return mDataSet.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item01, parent, false);
                vh = new ViewHolder();
                vh.text = (HtcListItem2LineText) convertView.findViewById(R.id.text1);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            vh.text.setPrimaryText(mDataSet.get(position));
            return convertView;
        }
    }

    static class ViewHolder {
        HtcListItem2LineText text;
    }
}
