package com.htc.lib1.cc.widget.preference;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;

import com.htc.lib1.cc.R;

/**
 * @deprecated [Not use any longer]
 *
 */
public final class PreferenceUtil {
    private static final String TAG = "PreferenceUtil";

    /**
     * Applied HtcListView style.
     *
     * @param context The Context this widget is running in, through which it can access the current
     *            theme, resources, etc and MUST be blong to the subclass of ContextThemeWrapper.
     * @param container The container is a ViewGroup,through it can access the current resources.
     */
    public final static void applyHtcListViewStyle(Context context,ViewGroup container){
        applyHtcListView(container);
    }

    /**
     * Applied Htc style.
     *
     * @param container The container is a ViewGroup,through it can access the current resources.
     * @return
     */
    public final static ViewGroup applyHtcListViewStyle(ViewGroup container){
        applyHtcListView(container);
        return container;
    }

    /**
     * Set ListView style, making it applied Htc style.
     *
     * @param container The container is a ViewGroup,through it can access the current resources.
     */
    private static void applyHtcListView(ViewGroup container) {
        if (null == container) {
            Log.d(TAG, "Container = null", new Exception());
            return;
        }

        if (null == container.getContext()) {
            Log.d(TAG, "container.getContext()= null", new Exception());
            return;
        }

        final Resources res = container.getContext().getResources();
        if (null == res) {
            Log.d(TAG, "container.getContext().getResources()= null", new Exception());
            return;
        }

        final ListView list = (ListView) container.findViewById(android.R.id.list);
        list.setBackgroundResource(com.htc.lib1.cc.R.drawable.common_app_bkg);
        list.setPadding(0, 0, 0, 0);
        list.setDivider(res.getDrawable(R.drawable.inset_list_divider));
        list.setSelector(res.getDrawable(R.drawable.list_selector_light));
    }
}
