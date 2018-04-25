
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.view.ContextThemeWrapper;

/**
 * HtcProperty is used to get information property.
 */
public class HtcProperty {

    private static int sContextHashCode;
    private static HtcListItemManager sHtcListItemManager;

    /**
     * Return a resource property for the given object. Use of this function is
     * discouraged and please must do the error handling.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param obj The object contain info that APP want to access
     * @return object that contain information
     */
    public static Object getProperty(Context context, Object obj) {
        if (obj == null)
            return null;
        Object obj1 = null;
        if (obj.equals("RefreshCheckDuration")) {
            obj1 = 500;
            return obj1;
        }

        if (context == null) {
            throw new IllegalStateException("The context object can not be null!");
        }

        if (obj.equals("RefreshVelocityThreshold")) {
            obj1 = (int) (context.getResources().getDisplayMetrics().density * (float) 1833);
            return obj1;
        }
        if (context instanceof ContextThemeWrapper && sContextHashCode == context.hashCode()) {
            obj1 = getValueByHtcListItemManager(sHtcListItemManager, obj);
        } else {
            sHtcListItemManager = HtcListItemManager.getInstance(context);
            sContextHashCode = context.hashCode();
            obj1 = getValueByHtcListItemManager(sHtcListItemManager, obj);
        }

        return obj1;
    }

    private static Object getValueByHtcListItemManager(HtcListItemManager htcListItemManager,
            Object obj) {
        Object obj1 = null;
        if (null != htcListItemManager) {
            if (obj.equals("HtcListItemHeight")) {
                obj1 = htcListItemManager.getDesiredListItemHeight(HtcListItem.MODE_DEFAULT);
            } else if (obj.equals("HtcListItemHeightOfPopupMenu")) {
                obj1 = htcListItemManager.getDesiredListItemHeight(HtcListItem.MODE_POPUPMENU);
            }
        }
        return obj1;
    }
}
