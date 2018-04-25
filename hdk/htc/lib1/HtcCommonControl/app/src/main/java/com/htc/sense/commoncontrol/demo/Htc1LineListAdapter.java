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

package com.htc.sense.commoncontrol.demo;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Htc1LineListAdapter extends BaseAdapter implements Filterable {
    private List<CharSequence> mObjects;
    private ArrayList<CharSequence> mOriginalValues;

    private LayoutInflater mInflater;

    private Filter mFilter;

    public Htc1LineListAdapter(Context context, List<CharSequence> list) {
        mInflater = LayoutInflater.from(context);
        mObjects = list;
    }

    public Htc1LineListAdapter(Context context, CharSequence[] list) {
        this(context, Arrays.asList(list));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewTag viewTag;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.htc1line_item_layout, null);
            viewTag = new ViewTag((HtcListItem1LineCenteredText) convertView.findViewById(android.R.id.text1));
            convertView.setTag(viewTag);
        } else {
            viewTag = (ViewTag) convertView.getTag();
        }

        viewTag.text.setText(mObjects.get(position).toString());

        return convertView;
    }

    class ViewTag {
        HtcListItem1LineCenteredText text;

        public ViewTag(HtcListItem1LineCenteredText text) {
            this.text = text;
        }
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    /**
     * {@inheritDoc}
     */
    public CharSequence getItem(int position) {
        return mObjects.get(position);
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     * @return The position of the specified item.
     */
    public int getPosition(CharSequence item) {
        return mObjects.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new KeywordFilter();
        }
        return mFilter;
    }

    private class KeywordFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence keyword) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<CharSequence>(mObjects);
            }

            if (TextUtils.isEmpty(keyword)) {
                // wrong search keyword, return whole entries
                ArrayList<CharSequence> list = new ArrayList<CharSequence>(mOriginalValues);
                results.values = list;
                results.count = list.size();

            } else {
                // find entries that match search keyword
                String lower = keyword.toString().toLowerCase();

                ArrayList<CharSequence> values = new ArrayList<CharSequence>(mOriginalValues);

                final int count = values.size();
                final ArrayList<CharSequence> newValues = new ArrayList<CharSequence>();

                for (int i = 0; i < count; i++) {
                    final CharSequence value = values.get(i);
                    final String valueText = value.toString().toLowerCase();
                    if (!TextUtils.isEmpty(valueText) && valueText.contains(lower)) {
                        newValues.add(value);
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mObjects = (List<CharSequence>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

}
