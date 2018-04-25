package com.htc.sense.commoncontrol.demo.htcspinner;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class HtcSpinnerDemo extends CommonDemoActivityBase implements
        Spinner.OnItemSelectedListener, Spinner.OnTouchListener,
        Spinner.OnFocusChangeListener {
    LayoutInflater mInflater = null;
    private Context mContext;
    private ActionAdapter mAdapter;
    private Spinner htcSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        setContentView(R.layout.htcspinner);

        CharSequence[] array = mContext.getResources().getStringArray(
                R.array.spinner_items);
        mAdapter = new ActionAdapter(mContext, Arrays.asList(array));

        htcSpinner = (Spinner) findViewById(R.id.htc_spinner);
        htcSpinner.setAdapter(mAdapter);
        htcSpinner.setOnItemSelectedListener(this);
        htcSpinner.setOnTouchListener(this);
        htcSpinner.setOnFocusChangeListener(this);

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long id) {
        // TODO Auto-generated method stub
        parent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
        parent.setVisibility(View.VISIBLE);

    }

    private class ActionAdapter extends BaseAdapter {
        private Context mContext;
        private List mList;

        public ActionAdapter(Context mContext, List mList) {
            this.mContext = mContext;
            this.mList = mList;
        }

        public int getCount() {
            return mList.size();
        }

        public Object getItem(int position) {
            return mList.get(position).toString();
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.actionbarlistitem0,
                        parent, false);
                holder.text = (HtcListItem2LineText) convertView
                        .findViewById(R.id.text1);
                holder.text.setPrimaryText((CharSequence) mList.get(position));
                holder.text.setSecondaryTextVisibility(View.GONE);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            return convertView;

        }

        class ViewHolder {
            HtcListItem2LineText text;
        }
    }
}
