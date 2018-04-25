/*
 * Copyright (C) 2009 HTC Inc.
 */

package com.htc.lib1.cc.adapterview.activityhelper;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.htc.lib1.cc.test.R;

import com.htc.lib1.cc.adapterview.activityhelper.HtcListViewReorderAdapter.ViewHolder;
import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.lib1.cc.widget.HtcReorderListView;

/**
 * In this example, a HtcReorderListView is used to display the an array of
 * strings.
 */
public class HtcListViewReorderDemo extends ActivityBase implements
        OnItemClickListener {

    /**
     * Our data which will be displayed
     */
    private final String[] ENTRIES = {
            "Abbaye de Belloc",
            "Abbaye du Mont des Cats", "1", "22",
            "Bakers", "Cabecou", "Caboc", "Cabrales", "Danish Fontina",
            "Esbareich", "Esrom", "Etorki", "Evansdale Farmhouse Brie",
            "Evora De L'Alentejo", "Exmoor Blue", "Explorateur", "Feta",
            "Fruit Cream Cheese", "Frying Cheese", "Fynbo", "Gabriel",
            "Lou Palou", "Lou Pevre", "Lyonnais", "Maasdam", "Macconais",
            "Neufchatel", "Neufchatel (Australian)", "Niolo", "Nokkelost",
            "Northumberland", "Oaxaca", "Olde York", "Olivet au Foin",
            "Pyengana Cheddar", "Pyramide", "Quark", "Quark (Australian)",
            "Regal de la Dombes", "Reggianito", "Remedou", "Requeson"
    };

    /**
     * the adapter for HTC list view
     */
    private HtcListViewReorderAdapter m_adapter = null;

    /**
     * The HTC list view
     */
    private HtcReorderListView mHtcReorderListView = null;

    private ActionBarExt actionBarExt = null;
    private ActionBarContainer actionBarContainer = null;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState
     *            If the activity is being re-initialized after previously being
     *            shut down then this Bundle contains the data it most recently
     *            supplied in onSaveInstanceState(Bundle). Notice: Otherwise it
     *            is null.
     * @see onStart() onSaveInstanceState(Bundle) onRestoreInstanceState(Bundle)
     *      onPostCreate(Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.common_app_bkg);

        setContentView(R.layout.htclistview_demo_htc_reorderlistview_layout);
        setTitle(R.string.reorderlistview_title);
        mHtcReorderListView = (HtcReorderListView) findViewById(R.id.htc_reorderlist);
        if (mHtcReorderListView != null) {

            setAdapter();
            mHtcReorderListView.setDropListener(mDropListener);
            mHtcReorderListView.setDraggerId(R.id.my_dragger);

            // Be sure to call this api and set to false, otherwise onItemClick would not been called.
            mHtcReorderListView.setAllItemFocusable(false);
            mHtcReorderListView.setOnItemClickListener(this);
            mHtcReorderListView.setVerticalScrollBarEnabled(false);
            mHtcReorderListView.setBackgroundResource(R.drawable.common_app_bkg);
        }
    }

    /**
     * Prepare the Screen's standard options menu to be displayed. This is
     * called right before the menu is shown, every time it is shown.
     *
     * @param menu
     *            The options menu as last shown or first initialized by
     *            onCreateOptionsMenu().
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     *
     * @param menu
     *            The options menu in which you place your items.
     * @return You must return true for the menu to be displayed; if you return
     *         false it will not be shown.
     * @see onPrepareOptionsMenu(Menu) onOptionsItemSelected(MenuItem)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This callback is called whenever an item in your options menu is
     * selected.
     *
     * @param item
     *            The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed,
     *         true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Save the adapter's type.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Set the adapter according to the type.
     *
     * @param typeId
     *            The adapter's type.
     */
    private void setAdapter() {
        m_adapter = new HtcListViewReorderAdapter(this,
                R.layout.htclistview_demo_htc_reorderlistview_item_layout,
                ENTRIES);
        mHtcReorderListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mHtcReorderListView.setAdapter(m_adapter);
    }

    /**
     * to process the item clicked
     */
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        ViewHolder holder = (ViewHolder) view.getTag();
        HtcCheckBox checkbox = holder.checkbox;

        // performClick would trigger onClick as well, it would save the new state of checkbox
        checkbox.performClick();
    }

    private HtcReorderListView.DropListener mDropListener = new HtcReorderListView.DropListener() {
        public void drop(int from, int to) {
            String rtmp;
            boolean checked;
            if (to >= 0 && to <= ENTRIES.length - 1 && from != to) {

                /*
                 * down to up
                 */
                if (from > to) {
                    for (int i = from; i > to; i--)
                    {
                        if (i == 0)
                            break;
                        rtmp = ENTRIES[i - 1];
                        ENTRIES[i - 1] = ENTRIES[i];
                        ENTRIES[i] = rtmp;

                        // check box state change handling
                        checked = m_adapter.getCheckState(i - 1);
                        m_adapter.setCheckState(i - 1, m_adapter.getCheckState(i));
                        m_adapter.setCheckState(i, checked);
                    }

                    /*
                     * up to down
                     */
                } else if (from < to) {
                    for (int i = from; i < to; i++)
                    {
                        rtmp = ENTRIES[i + 1];
                        ENTRIES[i + 1] = ENTRIES[i];
                        ENTRIES[i] = rtmp;

                        // check box state change handling
                        checked = m_adapter.getCheckState(i + 1);
                        m_adapter.setCheckState(i + 1, m_adapter.getCheckState(i));
                        m_adapter.setCheckState(i, checked);
                    }
                }
            }

            m_adapter.notifyDataSetChanged();
            mHtcReorderListView.invalidate();
        }
    };

    public void improveCoverage() {
        HtcReorderListView mHtcReorderListView = new HtcReorderListView(this, null);
        mHtcReorderListView.addHeaderView(null, null, false);
        mHtcReorderListView.addFooterView(null, null, false);
        mHtcReorderListView.setReorderBackgroundColor(0);
        mHtcReorderListView.setSeparatorPositionListener(null);
    }
}
