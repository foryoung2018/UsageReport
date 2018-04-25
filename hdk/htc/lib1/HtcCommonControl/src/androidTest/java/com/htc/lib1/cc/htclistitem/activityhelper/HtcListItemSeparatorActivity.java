package com.htc.lib1.cc.htclistitem.activityhelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import com.htc.aut.ActivityBase;

public class HtcListItemSeparatorActivity extends ActivityBase {
    LayoutInflater mInflater = null;
    private int layoutId = 0;
    private void getValueFromIntent() {
        Intent i = getIntent();
        if (null == i) {
            return;
        }
        layoutId = i.getIntExtra("layoutid", 0);
        if (i.hasExtra("ico")) {

        }
        Log.e("HtcListItemSeparatorActivity", "layoutId = " + layoutId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getValueFromIntent();
        if (0 == layoutId) {
            return;
        }
        setContentView(layoutId);
    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }
    
}
