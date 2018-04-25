
package com.htc.lib1.cc.adapterview.activityhelper;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcGridView;

import java.util.ArrayList;

public class HtcGridViewOverFlingDemo extends ActivityBase {
    GridView mGridView;
    HtcGridView mHtcGridView;
    MyAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overfling_gridview_layout);
        mHtcGridView = (HtcGridView) findViewById(R.id.grid1);
        mHtcGridView.setNumColumns(2);
        mGridView = (GridView) findViewById(R.id.grid2);
        mGridView.setNumColumns(2);
        mAdapter = new MyAdapter();
        mHtcGridView.setAdapter(mAdapter);
        mGridView.setAdapter(mAdapter);
        fillDataSet();
        mGridView.setVerticalScrollBarEnabled(false);
    }

    void fillDataSet() {
        for (String text : texts) {
            data.add(text);
        }
    }

    ArrayList<String> data = new ArrayList<String>();
    String[] texts = {
            "Aba", "Bandit", "Cha-Cha", "Deuce", "Goldy", "Bubbles", "Fluffy", "Snuggles"
    };

    class MyAdapter extends BaseAdapter {
        public int getCount() {
            return data.size() * 3;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int positon) {
            return positon;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView item;

            if (convertView == null) {
                item = new TextView(HtcGridViewOverFlingDemo.this);
                item.setTextSize(90);
            } else {
                item = (TextView) convertView;
            }
            item.setText(data.get(position % data.size()));
            return item;
        }
    }

    public HtcGridView getHtcGridView() {
        return mHtcGridView;
    }

    public GridView getGridView() {
        return mGridView;
    }

    public BaseAdapter getAdapter() {
        return mAdapter;
    }

}
