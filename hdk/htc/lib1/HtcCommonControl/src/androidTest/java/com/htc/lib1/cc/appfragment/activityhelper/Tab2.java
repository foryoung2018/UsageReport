
package com.htc.lib1.cc.appfragment.activityhelper;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Tab2 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView v = new TextView(getActivity());
        v.setBackgroundColor(Color.BLUE);
        v.setTextColor(Color.WHITE);
        v.setTextSize(50);
        v.setGravity(Gravity.CENTER);
        v.setText(MyHtcPagerFragment.PAGE[2]);
        return v;
    }
}
