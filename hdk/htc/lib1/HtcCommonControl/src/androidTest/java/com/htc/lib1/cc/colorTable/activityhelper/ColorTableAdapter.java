
package com.htc.lib1.cc.colorTable.activityhelper;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.htc.lib1.cc.test.R;

public class ColorTableAdapter extends BaseAdapter {

    private int[] mColorValues;
    private String[] mColorNames;
    private LayoutInflater mInflater;

    public ColorTableAdapter(int[] colorValues, String[] colorNames, LayoutInflater inflater) {
        mColorValues = colorValues;
        mColorNames = colorNames;
        mInflater = inflater;
    }

    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        return "Color Index" + position;
    }

    public int getCount() {
        if (null == mColorNames) {
            return 0;
        }
        return mColorNames.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_theme_color_table, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_color);
            holder.tvColor = (TextView) convertView.findViewById(R.id.tv_color);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final int color = mColorValues[position];
        String name = mColorNames[position];
        holder.imageView.setBackgroundColor(color);
        holder.tvName.setText(name);

        String alpha = Integer.toHexString(Color.alpha(color));
        final StringBuffer value = new StringBuffer("#");
        if (!"ff".equals(alpha)) {
            value.append(alpha);
        }
        value.append(Integer.toHexString(Color.red(color)));
        value.append(Integer.toHexString(Color.green(color)));
        value.append(Integer.toHexString(Color.blue(color)));

        holder.tvColor.setText(value.toString());
        return convertView;
    }
}
