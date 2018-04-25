
package com.htc.lib1.cc.appfragment.activityhelper;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.htc.lib1.cc.app.HtcListFragment;
import com.htc.lib1.cc.widget.HtcListView;

public class MyHtcListFragment extends HtcListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1, HtcListFragmentDemo.TITLES));
        HtcListView htcListView = getListView();
        htcListView.setVerticalScrollBarEnabled(false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Toast.makeText(getActivity(), HtcListFragmentDemo.TITLES[position], Toast.LENGTH_SHORT).show();
    }

}
