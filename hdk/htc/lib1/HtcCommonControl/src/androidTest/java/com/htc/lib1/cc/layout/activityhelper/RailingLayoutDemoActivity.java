
package com.htc.lib1.cc.layout.activityhelper;

import android.os.Bundle;
import android.widget.LinearLayout;
import com.htc.lib1.cc.test.R;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.widget.RailingLayout;

public class RailingLayoutDemoActivity extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_railinglayout);
        ((RailingLayout) this.findViewById(R.id.dark_rl1)).setMode(RailingLayout.DARK_MODE);
        ((RailingLayout) this.findViewById(R.id.dark_rl2)).setMode(RailingLayout.DARK_MODE);
        ((RailingLayout) this.findViewById(R.id.dark_rl2)).setLargerModeEnabled(true);

    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }

    public void improveCoverage()
    {
        RailingLayout railingLayoutNew = new RailingLayout(this);
        railingLayoutNew.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
    }
}
