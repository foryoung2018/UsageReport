
package com.htc.sense.commoncontrol.demo.crabwalkview;

import android.os.Bundle;

import com.htc.lib1.cc.widget.CrabWalkView;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class CrabWalkViewDemo extends CommonDemoActivityBase {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.crabwalkview_layout);

        final CrabWalkView cwv = (CrabWalkView) findViewById(R.id.cwv);
        cwv.setAdapter(new ListViewAdapter(getLayoutInflater()));

    }

}
