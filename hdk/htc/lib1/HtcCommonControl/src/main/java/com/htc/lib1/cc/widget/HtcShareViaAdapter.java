package com.htc.lib1.cc.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

/**
 * An adapter that exposes data from the query results of a specified intent.
 * This is so-called single intent Share Via and the requirements are:
 *
 * <li> The sharing list will be sorted by count, that is, the most used item
 *      will be placed at the 1st place.
 * <li> The items with equal counts will be placed in alphabetical order.
 * <li> For more than 5 items, the 5th item will be “More”. Upon clicking “More”,
 *      the whole item list will be shown and scrollable.
 * <li> Data sharing list will be provided from each application by different
 *      content type (Single intent).
 * @deprecated please use HtcShareActivity instead
 */
@Deprecated
public class HtcShareViaAdapter extends HtcShareViaMultipleAdapter {

    private static List<Intent> getIntentList(Intent i) {
        List<Intent> list = new ArrayList<Intent>();
        list.add(i);

        return list;
    }

    /**
     * Constructor.
     *
     * @param intent An intent used to query activities
     * @param applicationContext The application's context
     *
     * @see HtcShareViaMultipleAdapter
     */
    public HtcShareViaAdapter(Intent intent, Context applicationContext) {
        super(getIntentList(intent), applicationContext);
    }

    /**
     * Constructor.
     *
     * @param intent An intent used to query activities
     * @param allows The package names allowed to appear in the query results
     * @param excludes The package names used to exclude from the query results
     * @param applicationContext The application's context
     *
     * @see HtcShareViaMultipleAdapter
     */
    public HtcShareViaAdapter(Intent intent, List<String> allows,
            List<String> excludes, Context applicationContext) {
        super(getIntentList(intent), allows, excludes, applicationContext);
    }

    /**
     * {@inheritDoc}
     *
     * @param position
     * @return Return an intent for launching the activity. Once this method is
     *         called, the usage count for that specified activity will be added
     *         by one.
     * @deprecated [Module internal use]
     */
    /**@hide*/
    @Override
    public Object getItem(int position) {
        return super.getIntentItem(position);
    }
}
