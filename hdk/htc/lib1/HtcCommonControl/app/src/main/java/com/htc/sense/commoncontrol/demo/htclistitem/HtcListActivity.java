/*
 * Copyright (C) 2006 The Android Open Source Project
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

package com.htc.sense.commoncontrol.demo.htclistitem;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.widget.ListItem.LayoutParams;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

/**
 * An activity that displays a list of items by binding to a data source such as
 * an array or Cursor, and exposes event handlers when the user selects an item.
 * <p>
 * ListActivity hosts a {@link android.widget.ListView ListView} object that can
 * be bound to different data sources, typically either an array or a Cursor
 * holding query results. Binding, screen layout, and row layout are discussed
 * in the following sections.
 * <p>
 * <strong>Screen Layout</strong>
 * </p>
 * <p>
 * ListActivity has a default layout that consists of a single, full-screen list
 * in the center of the screen. However, if you desire, you can customize the
 * screen layout by setting your own view layout with setContentView() in
 * onCreate(). To do this, your own view MUST contain a ListView object with the
 * id "@android:id/list" (or {@link android.R.id#list} if it's in code)
 * <p>
 * Optionally, your custom view can contain another view object of any type to
 * display when the list view is empty. This "empty list" notifier must have an
 * id "android:empty". Note that when an empty view is present, the list view
 * will be hidden when there is no data to display.
 * <p>
 * The following code demonstrates an (ugly) custom screen layout. It has a list
 * with a green background, and an alternate red "no data" message.
 * </p>
 *
 * <pre>
 * &lt;?xml version=&quot;1.0&quot; encoding=&quot;utf-8&quot;?&gt;
 * &lt;LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
 *         android:orientation=&quot;vertical&quot;
 *         android:layout_width=&quot;fill_parent&quot;
 *         android:layout_height=&quot;fill_parent&quot;
 *         android:paddingLeft=&quot;8dp&quot;
 *         android:paddingRight=&quot;8dp&quot;&gt;
 *
 *     &lt;ListView android:id=&quot;@id/android:list&quot;
 *               android:layout_width=&quot;fill_parent&quot;
 *               android:layout_height=&quot;fill_parent&quot;
 *               android:background=&quot;#00FF00&quot;
 *               android:layout_weight=&quot;1&quot;
 *               android:drawSelectorOnTop=&quot;false&quot;/&gt;
 *
 *     &lt;TextView id=&quot;@id/android:empty&quot;
 *               android:layout_width=&quot;fill_parent&quot;
 *               android:layout_height=&quot;fill_parent&quot;
 *               android:background=&quot;#FF0000&quot;
 *               android:text=&quot;No data&quot;/&gt;
 * &lt;/LinearLayout&gt;
 * </pre>
 * <p>
 * <strong>Row Layout</strong>
 * </p>
 * <p>
 * You can specify the layout of individual rows in the list. You do this by
 * specifying a layout resource in the ListAdapter object hosted by the activity
 * (the ListAdapter binds the ListView to the data; more on this later).
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
 *     android:layout_width=&quot;fill_parent&quot;
 *     android:layout_height=&quot;wrap_content&quot;
 *     android:orientation=&quot;vertical&quot;&gt;
 *
 *     &lt;TextView android:id=&quot;@+id/text1&quot;
 *         android:textSize=&quot;16sp&quot;
 *         android:textStyle=&quot;bold&quot;
 *         android:layout_width=&quot;fill_parent&quot;
 *         android:layout_height=&quot;wrap_content&quot;/&gt;
 *
 *     &lt;TextView android:id=&quot;@+id/text2&quot;
 *         android:textSize=&quot;16sp&quot;
 *         android:layout_width=&quot;fill_parent&quot;
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
 * You bind the ListActivity's ListView object to data using a class that
 * implements the {@link android.widget.ListAdapter ListAdapter} interface.
 * Android provides two standard list adapters:
 * {@link android.widget.SimpleAdapter SimpleAdapter} for static data (Maps),
 * and {@link android.widget.SimpleCursorAdapter SimpleCursorAdapter} for Cursor
 * query results.
 * </p>
 * <p>
 * The following code from a custom ListActivity demonstrates querying the
 * Contacts provider for all contacts, then binding the Name and Company fields
 * to a two line row layout in the activity's ListView.
 * </p>
 *
 * <pre>
 * public class MyListAdapter extends ListActivity {
 *
 *     &#064;Override
 *     protected void onCreate(Bundle savedInstanceState){
 *         super.onCreate(savedInstanceState);
 *
 *         // We'll define a custom screen layout here (the one shown above), but
 *         // typically, you could just use the standard ListActivity layout.
 *         setContentView(R.layout.custom_list_activity_view);
 *
 *         // Query for all people contacts using the {@link android.provider.Contacts.People} convenience class.
 *         // Put a managed wrapper around the retrieved cursor so we don't have to worry about
 *         // requerying or closing it as the activity changes state.
 *         mCursor = People.query(this.getContentResolver(), null);
 *         startManagingCursor(mCursor);
 *
 *         // Now create a new list adapter bound to the cursor.
 *         // SimpleListAdapter is designed for binding to a Cursor.
 *         ListAdapter adapter = new SimpleCursorAdapter(
 *                 this, // Context.
 *                 android.R.layout.two_line_list_item,  // Specify the row template to use (here, two columns bound to the two retrieved cursor
 * rows).
 *                 mCursor,                                    // Pass in the cursor to bind to.
 *                 new String[] {People.NAME, People.COMPANY}, // Array of cursor columns to bind to.
 *                 new int[]);                                 // Parallel array of which template objects to bind to those columns.
 *
 *         // Bind to our new adapter.
 *         setListAdapter(adapter);
 *     }
 * }
 *
 *
 * </pre>
 *
 * @see #setListAdapter
 */
public class HtcListActivity extends CommonDemoActivityBase {
    /**
     * This field should be made private, so it is hidden from the SDK. {@hide
     *
     * }
     */
    protected ListAdapter mAdapter;
    /**
     * This field should be made private, so it is hidden from the SDK. {@hide
     *
     * }
     */
    protected HtcListView mList;

    private Handler mHandler = new Handler();
    private boolean mFinishedStart = false;

    private Runnable mRequestFocus = new Runnable() {
        public void run() {
            mList.focusableViewAvailable(mList);
        }
    };

    protected boolean mAddMenuEnableAutomotive=false;
    protected boolean mAddMenuShowListItem=false;

    private static final String AUTOMOTIVE_MODE_KEY = "automotive_mode_key";
    private static final String SHOW_LISTITEM_KEY = "show_listitem_key";
    public boolean mEnableAutomotive = false;
    public boolean mShowListItem = false;
    public static final int M1 = 0;
    public static final int M2 = 1;
    public static final int M3 = 2;
    public static final int M4 = 3;
    public static final int M5 = 4;
    public static final int M6 = 5;
    public int[] mMargin = new int[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initMenu();
        if (savedInstanceState != null) {
            mEnableAutomotive = savedInstanceState.getBoolean(AUTOMOTIVE_MODE_KEY);
            mShowListItem = savedInstanceState.getBoolean(SHOW_LISTITEM_KEY);
        }
        initStaticValue(this, mEnableAutomotive);
        super.onCreate(savedInstanceState);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.htclistitem_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemEnableAutomotive = menu.findItem(R.id.automotive);
        MenuItem itemShowListItem= menu.findItem(R.id.listitem);

        if(mAddMenuEnableAutomotive){
            itemEnableAutomotive.setChecked(mEnableAutomotive);
        }else{
            itemEnableAutomotive.setVisible(false);
        }

        if(mAddMenuShowListItem){
            itemShowListItem.setChecked(mShowListItem);
        }else{
            itemShowListItem.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.automotive:
                item.setChecked(mEnableAutomotive = !mEnableAutomotive);
                recreate();
                return true;
            case R.id.listitem:
                item.setChecked(mShowListItem = !mShowListItem);
                recreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTOMOTIVE_MODE_KEY, mEnableAutomotive);
        outState.putBoolean(SHOW_LISTITEM_KEY, mShowListItem);
    }

    private void initStaticValue(Context c, boolean isAutomotiveMode) {
        Resources res = c.getResources();
        mMargin[M1] = res.getDimensionPixelOffset(R.dimen.margin_l);
        mMargin[M2] = isAutomotiveMode ? mMargin[M1] : res.getDimensionPixelOffset(R.dimen.margin_m);
        mMargin[M3] = res.getDimensionPixelOffset(R.dimen.margin_s);
        mMargin[M4] = res.getDimensionPixelOffset(R.dimen.margin_xs);
        mMargin[M5] = res.getDimensionPixelOffset(R.dimen.spacing);
        mMargin[M6] = res.getDimensionPixelOffset(R.dimen.leading);
    }

    public static void setMargin(View view, int start, int top, int end, int bottom) {
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        lp.setMargins(start, top, end, bottom);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            lp.setMarginEnd(end);
            lp.setMarginStart(start);
        }
    }

    protected void initMenu() {

    }

    /**
     * This method will be called when an item in the list is selected.
     * Subclasses should override. Subclasses can call
     * getListView().getItemAtPosition(position) if they need to access the data
     * associated with the selected item.
     *
     * @param l The ListView where the click happened
     * @param v The view that was clicked within the ListView
     * @param position The position of the view in the list
     * @param id The row id of the item that was clicked
     */
    protected void onListItemClick(ListView l, View v, int position, long id) {
    }

    /**
     * Ensures the list view has been created before Activity restores all of
     * the view states.
     *
     * @see Activity#onRestoreInstanceState(Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle state) {
        ensureList();
        super.onRestoreInstanceState(state);
    }

    /**
     * Updates the screen state (current list and other views) when the content
     * changes.
     *
     * @see Activity#onContentChanged()
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        View emptyView = findViewById(android.R.id.empty);
        mList = (HtcListView) findViewById(R.id.list);
        if (mList == null) {
            throw new RuntimeException("Your content must have a ListView whose id attribute is "
                    + "'android.R.id.list'");
        }
        if (emptyView != null) {
            mList.setEmptyView(emptyView);
        }
        mList.setOnItemClickListener(mOnClickListener);
        if (mFinishedStart) {
            setListAdapter(mAdapter);
        }
        mHandler.post(mRequestFocus);
        mFinishedStart = true;
    }

    /**
     * Provide the cursor for the list view.
     */
    public void setListAdapter(ListAdapter adapter) {
        synchronized (this) {
            ensureList();
            mAdapter = adapter;
            mList.setAdapter(adapter);
        }
    }

    /**
     * Set the currently selected list item to the specified position with the
     * adapter's data
     *
     * @param position
     */
    public void setSelection(int position) {
        mList.setSelection(position);
    }

    /**
     * Get the position of the currently selected list item.
     */
    public int getSelectedItemPosition() {
        return mList.getSelectedItemPosition();
    }

    /**
     * Get the cursor row ID of the currently selected list item.
     */
    public long getSelectedItemId() {
        return mList.getSelectedItemId();
    }

    /**
     * Get the activity's list view widget.
     */
    public ListView getListView() {
        ensureList();
        return mList;
    }

    /**
     * Get the ListAdapter associated with this activity's ListView.
     */
    public ListAdapter getListAdapter() {
        return mAdapter;
    }

    private void ensureList() {
        if (mList != null) {
            return;
        }
        if (mEnableAutomotive)
            setContentView(R.layout.htclistview_main_dark);
        else
            setContentView(R.layout.htclistview_main);
    }

    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            onListItemClick((ListView) parent, v, position, id);
        }
    };
}
