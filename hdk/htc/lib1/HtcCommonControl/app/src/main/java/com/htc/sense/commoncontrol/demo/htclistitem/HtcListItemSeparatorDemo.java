
package com.htc.sense.commoncontrol.demo.htclistitem;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.lib1.cc.widget.HtcListItemSeparator;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.widget.IDividerController;
import com.htc.sense.commoncontrol.demo.R;

public class HtcListItemSeparatorDemo extends HtcListActivity {
    LayoutInflater mInflater = null;
    MyListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.common_app_bkg);
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAdapter = new MyListAdapter(this);
        setListAdapter(mAdapter);
        ((HtcListView) getListView()).setDividerController(new IDividerController() {

            @Override
            public int getDividerType(int position) {
                if (mAdapter.getItemViewType(position) == MyListAdapter.ITEM_VIEW_TYPE_SEPARATOR
                        || mAdapter.getItemViewType(position + 1) == MyListAdapter.ITEM_VIEW_TYPE_SEPARATOR)
                    return IDividerController.DIVIDER_TYPE_NONE;
                else
                    return IDividerController.DIVIDER_TYPE_NORAML;

            }
        });
    }

    private class MyListAdapter extends BaseAdapter {
        Context mContext = null;
        final int layouts[] = new int[] {
                R.layout.demo_separator00,
                R.layout.demo_separator01, R.layout.demo_separator02,
                R.layout.demo_separator03, R.layout.demo_separator04,
                R.layout.demo_separator05
        };
        private CharSequence[] description = {
                "Separator Light: left & right text",
                "Separator Light: left text & image button",
                "Separator Light: left text & toggle button light small",
                "Separator Dark: left & right text", "Separator Dark: left text & image button",
                "3 text", "Separtor with PowerBy icon", "Separtor with PowerBy icon & text"
        };

        final static int LIST_ITEM_COUNT = 16;
        final static int ITEM_VIEW_TYPE_SEPARATOR = 0;
        final static int ITEM_VIEW_TYPE_LIST_ITEM = 0;

        @Override
        public int getItemViewType(int position) {
            if (position % 2 == 1) {
                return ITEM_VIEW_TYPE_SEPARATOR;
            } else {
                return ITEM_VIEW_TYPE_LIST_ITEM;
            }
        }

        public MyListAdapter(Context context) {
            mContext = context;
        }

        public int getCount() {
            return LIST_ITEM_COUNT;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public int getViewTypeCount() {
            return LIST_ITEM_COUNT;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if ((position % 2) == 0) {
                HtcListItem item = new HtcListItem(mContext);
                HtcListItem1LineCenteredText text = new HtcListItem1LineCenteredText(mContext);
                text.setGravityCenterHorizontal(true);
                text.setText(description[(position) / 2]);
                item.addView(text);
                return item;

            } else {
                HtcListItemSeparator separator;

                if (position == 13)
                {
                    separator = new HtcListItemSeparator(mContext,
                            HtcListItemSeparator.ITEMMODE_DEFAULT,
                            HtcListItemSeparator.MODE_WHITE_STYLE);
                    separator.setSeparatorWithPowerBy();
                    separator.setIcon(HtcListItemSeparator.ICON_LEFT,
                            R.drawable.icon_indicator_lowpriority);
                }
                else if (position == 15)
                {
                    separator = new HtcListItemSeparator(mContext,
                            HtcListItemSeparator.ITEMMODE_DEFAULT,
                            HtcListItemSeparator.MODE_WHITE_STYLE);
                    separator.setSeparatorWithPowerBy();
                    separator.setIcon(HtcListItemSeparator.ICON_LEFT,
                            R.drawable.icon_indicator_lowpriority);
                    separator.setText(HtcListItemSeparator.TEXT_LEFT, "PowerBy text");
                }
                else
                    separator = (HtcListItemSeparator) mInflater.inflate(
                            layouts[(position - 1) / 2], null);

                return separator;
            }
        }
    }
}
