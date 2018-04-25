package com.htc.sense.commoncontrol.demo.htclistitem;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;

import com.htc.lib1.cc.widget.HtcListItemLabeledLayout;
import com.htc.sense.commoncontrol.demo.R;

import java.util.ArrayList;

public class HtcListItemActivity13 extends HtcListActivity {
    LayoutInflater mInflater = null;
    ArrayList<String> mDataSet = new ArrayList<String>();
    ListView mList;
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setListAdapter(new MyListAdapter());
        getWindow().setBackgroundDrawableResource(android.R.color.white);
        getListView().setCacheColorHint(0x00000000);
    }

    @Override
    protected void initMenu() {
        mAddMenuShowListItem = true;
    }

    class ViewHolder {
        HtcListItemLabeledLayout labelLayout;
        EditText text;
    }

    class MyListAdapter extends android.widget.BaseAdapter {
        int ITEM_TYPE = 4;
        Context mContext = HtcListItemActivity13.this;

        public int getCount() {
            return 4;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public int getViewTypeCount() {
            return 2;
        }

        public boolean isEnabled(int position) {
            return true;
        }

        public int getViewType(int position) {
            return position % ITEM_TYPE;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) HtcListItemActivity13.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (position == 0) {
                if (mShowListItem) {
                    convertView = inflater.inflate(R.layout.list_item30_new, null, false);
                } else {
                    convertView = inflater.inflate(R.layout.list_item30, null, false);
                }

            } else if (position == 1) {
                HtcListItemLabeledLayout labeledLayout = new HtcListItemLabeledLayout(mContext);
                AutoCompleteTextView input = new AutoCompleteTextView(mContext);
                input.setTextAppearance(mContext, com.htc.lib1.cc.R.style.list_primary_m);
                labeledLayout.addView(input);
                labeledLayout.setLabelText(R.string.label_text);
                convertView = labeledLayout;

            } else if (position == 2) {
                if (mShowListItem) {
                    convertView = inflater.inflate(R.layout.list_item34_new, null, false);
                } else {
                    convertView = inflater.inflate(R.layout.list_item34, null, false);
                }

            } else if (position == 3) {
                HtcListItemLabeledLayout labeledLayout = new HtcListItemLabeledLayout(mContext);
                AutoCompleteTextView input = new AutoCompleteTextView(mContext);
                input.setTextAppearance(mContext, com.htc.lib1.cc.R.style.list_primary_m);
                labeledLayout.addView(input);
                labeledLayout.setLabelTextAllCapsFalse();
                labeledLayout.setLabelText(R.string.description_text);
                convertView = labeledLayout;
            }

            return convertView;
        }
    }
}
