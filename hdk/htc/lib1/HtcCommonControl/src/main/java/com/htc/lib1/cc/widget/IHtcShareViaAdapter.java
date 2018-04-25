package com.htc.lib1.cc.widget;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

abstract class IHtcShareViaAdapter extends BaseAdapter {

    static final int INDEX_OF_MORE = 4;

    static final int BG_SET_DRAWABLE = 0x01;
    static final int BG_SET_RES_ID = 0x02;

    static final int EXPAND_DEFAULT = 0xA0;
    static final int NEED_EXPAND = 0xA1;
    static final int HAD_EXPAND = 0xA2;
    static final int NOT_EXPAND = 0xA3;

    /** ViewGroup.LayoutParams.WRAP_CONTENT */
    static final int WP = ViewGroup.LayoutParams.WRAP_CONTENT;

    // PATH: /data/data/<PackageName>/files/<FILE_NAME>
    static final String FILE_NAME = "task_specific_history_file_name.xml";

    private int mResIdEmpty = 0; // Cannot reference com.android.internal.R.string.activity_list_empty
    private int mResIdLoading = 0; // Cannot reference com.android.internal.R.string.loading
    static final int MORE = com.htc.lib1.cc.R.string.st_more; //com.android.internal.R.string.more_item_label;
    // [CC] paul.wy_wang, 20131021, Remove for UI static library.
    //static final int VIEW_SEE_ALL = com.android.internal.R.string.activity_chooser_view_see_all;

    abstract int isExpanded();

    abstract void expand();

    /**
     * call this to shrink, AP needs to call this before showing the dialog.
     */
    abstract void shrink();

    abstract void setIsDimissOk(boolean isDismissOk);

    abstract boolean isDimissOk();

    abstract void setListItemBackgroundDrawable(Drawable d);

    abstract void setListItemBackgroundResource(int resId);

    abstract void setListItemTextAppearance(int resId);

    abstract boolean isDataReady();

    abstract boolean isDataEmpty();

    abstract void setQueryByPackageName(boolean enable);

    int getStringEmpty(Resources r) {
        if (r != null && mResIdEmpty == 0) {
            mResIdEmpty = r.getIdentifier("activity_list_empty", "string", "android");
        }
        return mResIdEmpty;
    }

    int getStringLoading(Resources r) {
        if (r != null && mResIdLoading == 0) {
            mResIdLoading = r.getIdentifier("loading", "string", "android");
        }
        return mResIdLoading;
    }
}
