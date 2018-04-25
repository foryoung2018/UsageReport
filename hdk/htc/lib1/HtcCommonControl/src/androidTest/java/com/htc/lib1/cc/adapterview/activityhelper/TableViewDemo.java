
package com.htc.lib1.cc.adapterview.activityhelper;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.htc.lib1.cc.test.R;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.view.table.TableLayoutParams;
import com.htc.lib1.cc.view.table.TableView;
import com.htc.lib1.cc.widget.TableViewScrollControl;

public class TableViewDemo extends ActivityBase {
    public static final int ITEMCOUNT = 60;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tableview_layout);

        TableView tableView = (TableView) findViewById(R.id.tableview);

        tableView.setNumColumnRows(1);

        TableViewScrollControl mScrollControl = null;
        mScrollControl = new TableViewScrollControl();
        mScrollControl.setOrientation(TableLayoutParams.VERTICAL);
        mScrollControl.setTableView(tableView);
        tableView.setScrollControl(mScrollControl);

        tableView.setAdapter(new TableViewAdapter());
    }

    public class TableViewAdapter extends BaseAdapter {

        public int getCount() {

            return ITEMCOUNT;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                tv = new TextView(parent.getContext());
                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(Color.BLACK);
            } else {
                tv = (TextView) convertView;
            }
            tv.setText(position + "");
            return tv;
        }
    }

}
