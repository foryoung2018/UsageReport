
package com.htc.lib1.cc.adapterview.activityhelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.htc.lib1.cc.test.R;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.widget.CrabWalkView;

public class CrabWalkViewDemo extends ActivityBase {
    public static final int ITEMCOUNT = 60;
    private Integer[] mImageIds = {
            R.drawable.htcgridview_sample_0,
            R.drawable.htcgridview_sample_1,
            R.drawable.htcgridview_sample_2,
            R.drawable.htcgridview_sample_3,
            R.drawable.htcgridview_sample_4,
            R.drawable.htcgridview_sample_5,
            R.drawable.htcgridview_sample_6,
            R.drawable.htcgridview_sample_7,
    };
    private LayoutInflater mLayoutInflater = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crabwalkview_layout);
        mLayoutInflater = LayoutInflater.from(this);
        final CrabWalkView cwv = (CrabWalkView) findViewById(R.id.cwv);
        cwv.setAdapter(new CrabWalkViewAdapter());
        cwv.setHorizontalScrollBarEnabled(false);
    }

    public class CrabWalkViewAdapter extends BaseAdapter {

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

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.crabwalkview_item_layout, null);
                convertView.setTag(convertView.findViewById(R.id.image));
            }
            ImageView image = (ImageView) convertView.getTag();
            image.setImageResource(mImageIds[position % mImageIds.length]);
            return convertView;
        }
    }

}
