
package com.htc.sense.commoncontrol.demo.htcviewpager;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TabFragment extends Fragment {
    private int mPosition;

    public TabFragment() {
    }

    public TabFragment(int position) {
        mPosition = position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt("position");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView v = new TextView(getActivity());
        v.setTextColor(Color.WHITE);
        v.setTextSize(50);
        v.setGravity(Gravity.CENTER);
        v.setText("Page" + mPosition);
        if (mPosition == 1) {
            v.setBackgroundColor(Color.RED);
        } else if (mPosition == 2) {
            v.setBackgroundColor(Color.GREEN);
        } else if (mPosition == 3) {
            v.setBackgroundColor(Color.BLUE);
        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", mPosition);
    }
}
