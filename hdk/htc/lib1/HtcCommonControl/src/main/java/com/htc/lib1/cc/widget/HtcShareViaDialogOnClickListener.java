package com.htc.lib1.cc.widget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ListView;

import com.htc.lib1.cc.widget.HtcAlertDialog;

/**
 * DialogInterface.OnClickListener interface for HtcShareVia.
 * @deprecated please use HtcShareActivity instead
 */
@Deprecated
public class HtcShareViaDialogOnClickListener implements
        DialogInterface.OnClickListener {

    private IHtcShareViaAdapter mAdapter;
    private DialogInterface.OnClickListener mOnClickListener;

    /**
     * for HTC Alert Dialog
     *
     * @param adapter The adapter to use to create this dialog's content.
     * @param listener The onClickListener used to run some code when an item on the dialog is clicked.
     */
    public HtcShareViaDialogOnClickListener(IHtcShareViaAdapter adapter,
            DialogInterface.OnClickListener listener) {
        mAdapter = adapter;
        mAdapter.setListItemTextAppearance(com.htc.lib1.cc.R.style.list_primary_m);
        mOnClickListener = listener;
    }

    /**
     * For Google pure Alert Dialog
     *
     * @param mDialog Don't care (used to distinguish between HTC's and Google's styles)
     * @param adapter The adapter to use to create this dialog's content.
     * @param listener The onClickListener used to run some code when an item on the dialog is clicked.
     */
    public HtcShareViaDialogOnClickListener(AlertDialog mDialog,
            IHtcShareViaAdapter adapter,
            DialogInterface.OnClickListener listener) {
        mAdapter = adapter;
        mAdapter.setListItemTextAppearance(com.htc.lib1.cc.R.style.darklist_primary_m);
        mOnClickListener = listener;
    }

    /**
     * {@inheritDoc}
     *
     * @hide
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mAdapter != null && mAdapter.isDataReady() && !mAdapter.isDataEmpty()) {
            if ((IHtcShareViaAdapter.NEED_EXPAND == mAdapter.isExpanded())
                    && (which == IHtcShareViaAdapter.INDEX_OF_MORE)) {
                mAdapter.expand();
                mAdapter.setIsDimissOk(false);
                mAdapter.notifyDataSetChanged();
                // Request accessibility focus to the list.
                // [CC] paul.wy_wang, 20131021, Remove for UI static library.
                /*
                if ((null != dialog) && (dialog instanceof HtcAlertDialog)) {
                    ListView lv = ((HtcAlertDialog)dialog).getListView();
                    if (null != lv) {
                        lv.requestAccessibilityFocus();
                    }
                }
                */
            } else {
                mAdapter.setIsDimissOk(true);
                mOnClickListener.onClick(dialog, which);
            }
        }
    }
}
