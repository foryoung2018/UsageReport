/*
 * Copyright (C) 2009 HTC Inc.
 */
package com.htc.sense.commoncontrol.demo.listview;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.htc.lib1.cc.widget.HtcReorderListView;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;


/**
 * In this example, a HtcReorderListView is used to display the an array of
 * strings.
 */
public class HtcListViewReorderDemo extends CommonDemoActivityBase implements
        OnItemClickListener, OnScrollListener {
    /**
     * Menu id for bouncing enability.
     */
    private static final int MENU_BOUNCING_ENABLED = 1001;
    /**
     * Our data which will be displayed
     */
    private static final String[] ENTRIES = { "Abbaye de Belloc",
            "Abbaye du Mont des Cats", "Abertam", "Baguette Laonnaise",
            "Bakers", "Cabecou", "Caboc", "Cabrales", "Danish Fontina",
            "Esbareich", "Esrom", "Etorki", "Evansdale Farmhouse Brie",
            "Evora De L'Alentejo", "Exmoor Blue", "Explorateur", "Feta",
            "Fruit Cream Cheese", "Frying Cheese", "Fynbo", "Gabriel",
            "Lou Palou", "Lou Pevre", "Lyonnais", "Maasdam", "Macconais",
            "Neufchatel", "Neufchatel (Australian)", "Niolo", "Nokkelost",
            "Northumberland", "Oaxaca", "Olde York", "Olivet au Foin",
            "Pyengana Cheddar", "Pyramide", "Quark", "Quark (Australian)",
            "Regal de la Dombes", "Reggianito", "Remedou", "Requeson" };

    /**
     * the adapter for HTC list view
     */
    private HtcListViewReorderAdapter m_adapter = null;

    /**
     * The HTC list view
     */
    private HtcReorderListView mHtcReorderListView = null;

    /**
     * Identify whether this View can bounced.
     */
    private boolean isBouncing = true;

    /**
     * Identify whether this View has been scrolled.
     */
    private boolean isScrolling = true;

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
        getWindow().setBackgroundDrawableResource(com.htc.lib1.cc.R.drawable.common_app_bkg);
        setContentView(R.layout.htclistview_demo_htc_reorderlistview_layout);
        setTitle(R.string.reorderlistview_title);
        mHtcReorderListView = (HtcReorderListView) findViewById(R.id.htc_reorderlist);
        if (mHtcReorderListView != null) {
            setAdapter();
            mHtcReorderListView.setDropListener(mDropListener);
            mHtcReorderListView.setDraggerId(R.id.my_dragger);
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

    // @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
    }

    /**
     * Notice the mark to identify whether this View has been dragged or not.
     */
    // @Override
    public void onScrollStateChanged(AbsListView arg0, int scrollState) {
        isScrolling = true;
    }

    /**
     * to process the item clicked
     */
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        // To process the item clicked;
    }

    private HtcReorderListView.DropListener mDropListener = new HtcReorderListView.DropListener() {
        public void drop(int from, int to) {
            String rtmp = new String();
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
                    }
                }
            }

            m_adapter.notifyDataSetChanged();
            mHtcReorderListView.invalidate();
            // lv.setSelection(to);
        }
    };
}
