
package com.htc.lib1.cc.tabbar.activityhelper;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PagerTab extends Fragment {
    private int mIndex;

    public PagerTab(int index) {
        mIndex = index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        TextView v = new TextView(context);
        v.setTextColor(Color.WHITE);
        v.setTextSize(50);
        v.setGravity(Gravity.CENTER);
        switch (mIndex) {
            case 1:
                v.setBackgroundColor(Color.GREEN);
                v.setText("Page2");
                break;
            case 2:
                v.setBackgroundColor(Color.BLUE);
                v.setText("Page3");
                break;
            case 0:
            default:
                v.setBackgroundColor(Color.RED);
                v.setText("Page1");
                break;
        }
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mIndex == 0 || mIndex == 2) {
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        switch (mIndex) {
            case 0:
                menu.add("Menu1-1");
                menu.add("Menu1-2");
                break;
            case 2:
                menu.add("Menu3-1");
                menu.add("Menu3-2");
                break;
            default:
                break;
        }
    }
}
