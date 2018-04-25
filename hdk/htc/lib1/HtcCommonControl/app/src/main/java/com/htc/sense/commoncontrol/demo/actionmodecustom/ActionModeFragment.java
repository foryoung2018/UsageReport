package com.htc.sense.commoncontrol.demo.actionmodecustom;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.htc.lib1.cc.app.HtcListFragment;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.sense.commoncontrol.demo.Htc1LineListAdapter;
import com.htc.sense.commoncontrol.demo.R;

public class ActionModeFragment extends HtcListFragment {

    private FragmentTransaction mTransaction;
    private final String[] ENTRIES = {
            "General Activity", "Google Dialog", "HtcAlertDialog", "EditTextPreference"
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new Htc1LineListAdapter(getActivity(), ENTRIES));

        HtcListView htcListView = getListView();
        htcListView.setDivider(getActivity().getResources().getDrawable(R.drawable.inset_list_divider));
        htcListView.setSelector(R.drawable.list_selector_light);
        htcListView.setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onListItemClick(ListView listview, View view, int position, long id) {
        super.onListItemClick(listview, view, position, id);

        mTransaction = getFragmentManager().beginTransaction();
        switch (position) {
            case 0:
                mTransaction.replace(android.R.id.content, new GeneralFragment(), "GeneralFragment");
                mTransaction.addToBackStack(null);
                mTransaction.commit();
                break;
            case 1:
                showDialog("GoogleDialogFragment",false);
                break;
            case 2:
                showDialog("HtcAlertDialog",true);
                break;
            case 3:
                mTransaction.replace(android.R.id.content, new EditTextPreferenceFragment(), "EditTextPreferenceFragment");
                mTransaction.addToBackStack(null);
                mTransaction.commit();
                break;
        }
    }

    private void showDialog(String title, boolean isHtcAlertDialog) {
        final Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            mTransaction.remove(prev);
        }
        mTransaction.addToBackStack(null);
        final DialogFragment newFragment = ActionModeCustomDialogFragment.newInstance(title, isHtcAlertDialog);
        newFragment.show(mTransaction, "dialog");
    }
}
