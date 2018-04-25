
package com.htc.lib1.cc.adapterview.activityhelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout.LayoutParams;
import com.htc.lib1.cc.test.R;

import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcProperty;

public class HtcListViewReorderAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private String[] mList;
    private boolean[] mCheckBoxStateList;
    private int mHtcListItemLayoutId = R.layout.htclistview_demo_htc_reorderlistview_item_layout;
    private int mHtcListItemSeparatorLayoutId = R.layout.separator;
    private int mCustomLayoutId = R.layout.custom_layout;
    private static final int TYPE_SEPARATOR = 0;
    private static final int TYPE_HTCLISTITEM = 1;
    private static final int TYPE_CUSTOM = 2;
    private static final int TYPE_COUNT = 3;
    private Context mContext;
    /**
     * Constructor with the array of string.
     * 
     * @param layoutId
     *            : the resource ID of the layout Default
     *            value:R.layout.htc_listview_item_layout.And you can choose
     *            another one named
     *            R.layout.htc_listview_item_multiplechoice_layout.
     * @param data
     *            : the source to generate the list.
     */
    public HtcListViewReorderAdapter(Context context, int layoutId, String[] data) {
        mContext = context;
        mInflater = (LayoutInflater) (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        mHtcListItemLayoutId = layoutId;
        mList = data;
        // save the state of each checkbox by position in adapter 
        mCheckBoxStateList = new boolean[data.length];
    }

    public boolean getCheckState(int position) {
        return mCheckBoxStateList[position];
    }

    public void setCheckState(int position, boolean isChecked) {
        mCheckBoxStateList[position] = isChecked;
    }

    class ViewHolder {
        HtcCheckBox checkbox;
        HtcListItem2LineText vlable;
    }

    /**
     * Grouping by first character of the string.
     * 
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getView(int, View,
     *      ViewGroup)
     * 
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        ViewHolder vholder = null;
        int viewType = getItemViewType(position);
        if (convertView == null) {
            if (viewType == TYPE_SEPARATOR) {
                v = mInflater.inflate(mHtcListItemSeparatorLayoutId, parent, false);
            } else if (viewType == TYPE_CUSTOM) {
                v = mInflater.inflate(mCustomLayoutId, parent, false);
                v.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, (Integer) HtcProperty.getProperty(mContext, "HtcListItemHeight")));
            } else {
                v = mInflater.inflate(mHtcListItemLayoutId, parent, false);
                vholder = new ViewHolder();

                vholder.checkbox = (HtcCheckBox) v.findViewById(R.id.checkbox);
                vholder.checkbox.setTag(position);
                vholder.checkbox.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getTag() instanceof Integer) {
                            int position = (Integer) view.getTag();
                            // reverse the state of checkbox if it is been clicked.
                            mCheckBoxStateList[position] = !mCheckBoxStateList[position];
                        }
                    }
                });

                vholder.vlable = (HtcListItem2LineText) v.findViewById(R.id.label);
                vholder.vlable.setSecondaryTextVisibility(View.GONE);
                v.setTag(vholder);
            }
        } else {
            v = convertView;
            if (viewType == TYPE_HTCLISTITEM) {
                vholder = (ViewHolder) v.getTag();

                vholder.checkbox.setTag(position);
                // set to "unchecked" as default when recycle views
                vholder.checkbox.setChecked(false);
            }
        }
        if (viewType == TYPE_HTCLISTITEM) {
            // set the state to checkbox according to state list
            if (mCheckBoxStateList[position] == true) {
                vholder.checkbox.setChecked(true);
            }

            String str = mList[position];

            if (str != null) {
                vholder.vlable.setPrimaryText(str);
            }
        }
        return v;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    public final long getItemId(int position) {
        return position;
    }

    public final Object getItem(int position) {
        return mList[position];
    }

    public final int getCount() {
        return mList.length;
    }

    @Override
    public int getItemViewType(int position) {
        if (mList[position].length() == 1) return TYPE_SEPARATOR;
        else if (mList[position].length() == 2) return TYPE_CUSTOM;
        else {
            return TYPE_HTCLISTITEM;
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }
}
