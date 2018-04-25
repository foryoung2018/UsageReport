package com.htc.sense.commoncontrol.demo.layout;

import android.os.Bundle;

import com.htc.lib1.cc.widget.RailingLayout;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class RailingLayoutDemoActivity extends CommonDemoActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.demo_railinglayout);
        ((RailingLayout) findViewById(R.id.dark_rl2)).setLargerModeEnabled(true);
    }

}