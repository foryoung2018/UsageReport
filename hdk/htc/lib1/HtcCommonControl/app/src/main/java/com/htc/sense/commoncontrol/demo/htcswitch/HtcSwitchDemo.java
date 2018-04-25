
package com.htc.sense.commoncontrol.demo.htcswitch;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;

import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.widget.HtcSwitch;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class HtcSwitchDemo extends CommonDemoActivityBase {

    private static final int SWITCH_ITEM_COUNT = 50;
    private Toast mToast;
    private SparseBooleanArray mCheckedState;
    private SparseBooleanArray mEnabledState;
    private HtcListView mSwitchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_htc_switch_demo);
        HtcSwitch mSwitchLight = (HtcSwitch) findViewById(R.id.mySwitchLight);
        mSwitchLight.setOnCheckedChangeListener(mOnCheckedChangeListener);
        Switch mGoogleSwitchLight = (Switch) findViewById(R.id.myGoogleSwitchLight);
        mGoogleSwitchLight.setOnCheckedChangeListener(mOnCheckedChangeListener);

        HtcSwitch mSwitchDark = (HtcSwitch) findViewById(R.id.mySwitchDark);
        mSwitchDark.setOnCheckedChangeListener(mOnCheckedChangeListener);
        Switch mGoogleSwitchDark = (Switch) findViewById(R.id.myGoogleSwitchDark);
        mGoogleSwitchDark.setOnCheckedChangeListener(mOnCheckedChangeListener);

        initSwitchStateArray();
        mSwitchList = (HtcListView) findViewById(R.id.switch_list);
        mSwitchList.setAdapter(new SwitchAdapter(this));
        mSwitchList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mEnabledState.put(position, !mEnabledState.get(position));
                mSwitchList.invalidateViews();
            }
        });
    }

    private void initSwitchStateArray() {
        mCheckedState = new SparseBooleanArray();
        for (int i = 0; i < SWITCH_ITEM_COUNT; i++) {
            mCheckedState.put(i, false);
        }
        mEnabledState = new SparseBooleanArray();
        for (int i = 0; i < SWITCH_ITEM_COUNT; i++) {
            mEnabledState.put(i, true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            showToast(isChecked ? "Switch ON" : "Switch OFF");
        }
    };

    private void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    private class SwitchAdapter extends BaseAdapter {
        private LayoutInflater mInflate;

        public SwitchAdapter(Context context) {
            mInflate = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return SWITCH_ITEM_COUNT;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (mInflate == null) {
                return null;
            }
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflate.inflate(R.layout.list_item_switch, null);
                holder.mText = (HtcListItem2LineText) convertView.findViewById(R.id.text_item);
                holder.mText.setSecondaryTextVisibility(View.GONE);
                holder.mHtcSwitch = (HtcSwitch) convertView.findViewById(R.id.switch_item);
                holder.mHtcSwitch.setFocusable(false);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mHtcSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mCheckedState.put(position, isChecked);
                }
            });
            holder.mText.setPrimaryText("The Index of Switch is : " + position);
            holder.mHtcSwitch.setChecked(mCheckedState.get(position));
            holder.mHtcSwitch.setEnabled(mEnabledState.get(position));
            return convertView;
        }
    }

    private static class ViewHolder {
        HtcListItem2LineText mText;
        HtcSwitch mHtcSwitch;
    }

}
