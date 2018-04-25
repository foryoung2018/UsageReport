
package com.htc.sense.commoncontrol.demo.crabwalkview;

import com.htc.sense.commoncontrol.demo.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ListViewAdapter extends BaseAdapter {

    private static final int[] IMAGE_ARRAY = {
            R.drawable.htcgridview_sample_0,
            R.drawable.htcgridview_sample_1,
            R.drawable.htcgridview_sample_2,
            R.drawable.htcgridview_sample_3,
            R.drawable.htcgridview_sample_4,
            R.drawable.htcgridview_sample_5,
            R.drawable.htcgridview_sample_6,
            R.drawable.htcgridview_sample_7,
    };

    private LayoutInflater mLayoutInflater;

    public ListViewAdapter(LayoutInflater layoutInflater) {
        mLayoutInflater = layoutInflater;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            final ImageView iv = (ImageView) mLayoutInflater.inflate(
                    R.layout.crabwalkview_item_layout, null);
            holder = new ViewHolder();
            holder.iv = iv;
            iv.setTag(holder);
            convertView = iv;
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.iv.setImageResource(IMAGE_ARRAY[position]);

        return convertView;
    }

    @Override
    public int getCount() {
        return IMAGE_ARRAY.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}

class ViewHolder {
    ImageView iv;
}
