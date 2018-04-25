
package com.htc.lib1.cc.button.activityhelper;

import android.os.Bundle;
import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;

public class HtcIconButtonDemo extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.htcbutton_demos_iconbutton);

    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }

}
