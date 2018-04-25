/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.htc.sense.commoncontrol.demo.actionbar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.sense.commoncontrol.demo.R;

public class ActionMenuAdapter extends BaseAdapter {
    private String[] mItemStrings;
    private Context mContext;
    private boolean mIsAutomotive;
    private LayoutInflater mLayoutInflater;

    public ActionMenuAdapter(Context context, String[] itemStrings, boolean isAutomotive) {
        mItemStrings = itemStrings;
        mIsAutomotive = isAutomotive;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return mItemStrings.length;
    }

    public Object getItem(int position) {
        return mItemStrings[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        HtcListItem listitem = (HtcListItem) mLayoutInflater.inflate(R.layout.actionbarlistitem, null);
        HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) listitem.findViewById(R.id.text1);
        text.setText(mItemStrings[position]);
        listitem.setAutoMotiveMode(mIsAutomotive);
        return listitem;

    }

}
