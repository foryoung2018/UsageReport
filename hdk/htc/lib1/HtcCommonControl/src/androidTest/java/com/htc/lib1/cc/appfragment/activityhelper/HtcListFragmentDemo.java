
package com.htc.lib1.cc.appfragment.activityhelper;

import android.os.Bundle;
import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;

public class HtcListFragmentDemo extends ActivityBase {
    public static final String[] TITLES = {
            "Henry IV (1)",
            "Henry V",
            "Henry VIII",
            "Richard II",
            "Richard III",
            "Merchant of Venice",
            "Othello",
            "King Lear"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_htclistfragment);
    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }
}
