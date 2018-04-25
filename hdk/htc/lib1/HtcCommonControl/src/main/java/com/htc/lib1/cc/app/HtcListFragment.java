/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.htc.lib1.cc.app;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ListAdapter;

import com.htc.lib1.cc.widget.HtcListView;

/**
 * A fragment that displays a list of items by binding to a data source such as
 * an array or Cursor, and exposes event handlers when the user selects an item.
 * <p>
 * HtcListFragment hosts a {@link com.htc.lib1.cc.widget.HtcListView
 * HtcListView} object that can be bound to different data sources, typically
 * either an array or a Cursor holding query results. Binding, screen layout,
 * and row layout are discussed in the following sections.
 * <p>
 * <strong>Screen Layout</strong>
 * </p>
 * <p>
 * HtcListFragment has a default layout that consists of a single list view.
 * However, if you desire, you can customize the fragment layout by returning
 * your own view hierarchy from {@link #onCreateView}. To do this, your view
 * hierarchy <em>must</em> contain a HtcListView object with the id
 * "@android:id/list" (or {@link android.R.id#list} if it's in code)
 * <p>
 * Optionally, your view hierarchy can contain another view object of any type
 * to display when the list view is empty. This "empty list" notifier must have
 * an id "android:empty". Note that when an empty view is present, the list view
 * will be hidden when there is no data to display.
 * <p>
 * The following code demonstrates an (ugly) custom list layout. It has a list
 * with a green background, and an alternate red "no data" message.
 * </p>
 *
 * <pre>
 * &lt;?xml version=&quot;1.0&quot; encoding=&quot;utf-8&quot;?&gt;
 * &lt;LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
 *         android:orientation=&quot;vertical&quot;
 *         android:layout_width=&quot;match_parent&quot;
 *         android:layout_height=&quot;match_parent&quot;
 *         android:paddingLeft=&quot;8dp&quot;
 *         android:paddingRight=&quot;8dp&quot;&gt;
 *
 *     &lt;HtcListView android:id=&quot;@id/android:list&quot;
 *               android:layout_width=&quot;match_parent&quot;
 *               android:layout_height=&quot;match_parent&quot;
 *               android:background=&quot;#00FF00&quot;
 *               android:layout_weight=&quot;1&quot;
 *               android:drawSelectorOnTop=&quot;false&quot;/&gt;
 *
 *     &lt;TextView android:id=&quot;@id/android:empty&quot;
 *               android:layout_width=&quot;match_parent&quot;
 *               android:layout_height=&quot;match_parent&quot;
 *               android:background=&quot;#FF0000&quot;
 *               android:text=&quot;No data&quot;/&gt;
 * &lt;/LinearLayout&gt;
 * </pre>
 * <p>
 * <strong>Row Layout</strong>
 * </p>
 * <p>
 * You can specify the layout of individual rows in the list. You do this by
 * specifying a layout resource in the ListAdapter object hosted by the fragment
 * (the ListAdapter binds the HtcListView to the data; more on this later).
 * <p>
 * A ListAdapter constructor takes a parameter that specifies a layout resource
 * for each row. It also has two additional parameters that let you specify
 * which data field to associate with which object in the row layout resource.
 * These two parameters are typically parallel arrays.
 * </p>
 * <p>
 * Android provides some standard row layout resources. These are in the
 * {@link android.R.layout} class, and have names such as simple_list_item_1,
 * simple_list_item_2, and two_line_list_item. The following layout XML is the
 * source for the resource two_line_list_item, which displays two data
 * fields,one above the other, for each list row.
 * </p>
 *
 * <pre>
 * &lt;?xml version=&quot;1.0&quot; encoding=&quot;utf-8&quot;?&gt;
 * &lt;LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
 *     android:layout_width=&quot;match_parent&quot;
 *     android:layout_height=&quot;wrap_content&quot;
 *     android:orientation=&quot;vertical&quot;&gt;
 *
 *     &lt;TextView android:id=&quot;@+id/text1&quot;
 *         android:textSize=&quot;16sp&quot;
 *         android:textStyle=&quot;bold&quot;
 *         android:layout_width=&quot;match_parent&quot;
 *         android:layout_height=&quot;wrap_content&quot;/&gt;
 *
 *     &lt;TextView android:id=&quot;@+id/text2&quot;
 *         android:textSize=&quot;16sp&quot;
 *         android:layout_width=&quot;match_parent&quot;
 *         android:layout_height=&quot;wrap_content&quot;/&gt;
 * &lt;/LinearLayout&gt;
 * </pre>
 * <p>
 * You must identify the data bound to each TextView object in this layout. The
 * syntax for this is discussed in the next section.
 * </p>
 * <p>
 * <strong>Binding to Data</strong>
 * </p>
 * <p>
 * You bind the HtcListFragment's HtcListView object to data using a class that
 * implements the {@link android.widget.ListAdapter ListAdapter} interface.
 * Android provides two standard list adapters:
 * {@link android.widget.SimpleAdapter SimpleAdapter} for static data (Maps),
 * and {@link android.widget.SimpleCursorAdapter SimpleCursorAdapter} for Cursor
 * query results.
 * </p>
 * <p>
 * You <b>must</b> use {@link #setListAdapter(ListAdapter)
 * HtcListFragment.setListAdapter()} to associate the list with an adapter. Do
 * not directly call {@link HtcListView#setAdapter(ListAdapter)
 * HtcListView.setAdapter()} or else important initialization will be skipped.
 * </p>
 *
 * @see #setListAdapter
 * @see com.htc.lib1.cc.widget.HtcListView
 */
public class HtcListFragment extends ListFragment {
    /**
     * @hide
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View listView = view.findViewById(android.R.id.list);
        if (null != listView) {
            ViewGroup parent = (ViewGroup) listView.getParent();
            if (null != parent) {
                int index = parent.indexOfChild(listView);
                parent.removeView(listView);
                HtcListView htcListView = new HtcListView(getActivity());
                htcListView.setId(android.R.id.list);
                htcListView.setDrawSelectorOnTop(false);
                ViewGroup.LayoutParams lp = listView.getLayoutParams();
                if (null != lp) {
                    htcListView.setLayoutParams(lp);
                } else {
                    htcListView.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                }
                parent.addView(htcListView, index);
            }
        }
        return view;
    }

    /**
     * Get the activity's list view widget.
     */
    public HtcListView getListView() {
        return (HtcListView) super.getListView();
    }
}
