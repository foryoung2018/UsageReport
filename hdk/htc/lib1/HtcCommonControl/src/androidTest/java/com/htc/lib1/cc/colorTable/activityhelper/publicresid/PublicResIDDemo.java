package com.htc.lib1.cc.colorTable.activityhelper.publicresid;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.htc.lib1.cc.test.R;

import java.util.ArrayList;

public class PublicResIDDemo extends Activity {

    private ArrayList<String> mNames = new ArrayList<String>();
    private ArrayList<Integer> mIDs = new ArrayList<Integer>();

    private static final int[] PUBLIC_COLOR_ATTR_ID_ARRAY = R.styleable.ThemeColor;
    private static final int[] PUBLIC_STYLE_ID_ARRAY = { R.style.HtcDeviceDefault };
    private static final int[][] ALL_PUBLIC_ID_ARRAY = {
            PUBLIC_COLOR_ATTR_ID_ARRAY, PUBLIC_STYLE_ID_ARRAY };

    private LayoutInflater mInflater;

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        setContentView(R.layout.main);
        mListView = (ListView) findViewById(R.id.lv);
        mInflater = getLayoutInflater();
        setupListView();
    }

    private void initData() {

        for (int i = 0; i < ALL_PUBLIC_ID_ARRAY.length; i++) {
            int[] temp = ALL_PUBLIC_ID_ARRAY[i];

            for (int j = 0; j < temp.length; j++) {
                mIDs.add(temp[j]);
                mNames.add(getResources().getResourceEntryName(temp[j]));
            }
        }

    }

    private void setupListView() {
        mListView.setAdapter(new BaseAdapter() {

            public long getItemId(int position) {
                return position;
            }

            public Object getItem(int position) {
                return "Public Res Id Index" + position;
            }

            public int getCount() {
                if (null == mNames) {
                    return 0;
                }
                return mNames.size();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder = null;
                if (null == convertView) {
                    holder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.list_item, null);
                    holder.tvID = (TextView) convertView
                            .findViewById(R.id.tv_id);
                    holder.tvName = (TextView) convertView
                            .findViewById(R.id.tv_name);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.tvName.setText(mNames.get(position));
                holder.tvID.setText("0x"
                        + Integer.toHexString(mIDs.get(position)));
                return convertView;
            }
        });
    }

    private static class ViewHolder {
        TextView tvName;
        TextView tvID;
    }
}
