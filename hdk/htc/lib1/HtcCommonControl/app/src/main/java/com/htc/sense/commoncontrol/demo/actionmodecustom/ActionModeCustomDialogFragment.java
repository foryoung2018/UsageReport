package com.htc.sense.commoncontrol.demo.actionmodecustom;

import com.htc.lib1.cc.app.OnActionModeChangedListener;
import com.htc.lib1.cc.util.ActionBarUtil;
import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.sense.commoncontrol.demo.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;

public class ActionModeCustomDialogFragment extends DialogFragment {

    public static ActionModeCustomDialogFragment newInstance(String title, boolean isHtcAlertDialog) {
        final ActionModeCustomDialogFragment dialogFragment = new ActionModeCustomDialogFragment();
        final Bundle args = new Bundle();
        args.putString("title", title);
        args.putBoolean("isHtcAlertDialog", isHtcAlertDialog);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    private OnActionModeChangedListener mOnActionModeChangedListener = new com.htc.lib1.cc.app.OnActionModeChangedListener() {
        @Override
        public void onActionModeStarted(ActionMode mode) {
            ActionBarUtil.setActionModeBackground(getActivity(), mode, new ColorDrawable(Color.GRAY));
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity act = getActivity();
        final String title = getArguments().getString("title");
        final boolean isHtcAlertDialog = getArguments().getBoolean("isHtcAlertDialog");
        if (isHtcAlertDialog) {
            LayoutInflater inflater = LayoutInflater.from(act);
            View view = inflater.inflate(R.layout.actionmode, null);
            HtcAlertDialog.Builder htcAlertDialog = new HtcAlertDialog.Builder(act);
            htcAlertDialog.setTitle(title);
            htcAlertDialog.setView(view);
            htcAlertDialog.setPositiveButton(R.string.va_ok, null);
            htcAlertDialog.setNegativeButton(R.string.va_cancel, null);
            HtcAlertDialog dialog = htcAlertDialog.create();

            dialog.setOnActionModeChangedListener(mOnActionModeChangedListener);

            return dialog;
        } else {
            final Dialog dialog = new Dialog(act);
            dialog.setContentView(R.layout.actionmode);
            dialog.setTitle(title);

            ActionBarUtil.wrapActionModeChangeForDialog(dialog, mOnActionModeChangedListener);
            return dialog;
        }

    }
}
