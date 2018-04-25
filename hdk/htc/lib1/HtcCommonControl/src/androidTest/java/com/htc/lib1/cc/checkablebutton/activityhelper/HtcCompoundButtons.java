
package com.htc.lib1.cc.checkablebutton.activityhelper;

import android.os.Bundle;
import com.htc.lib1.cc.test.R;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.widget.HtcCheckBox;

public class HtcCompoundButtons extends ActivityBase {
    private HtcCheckBox mPartialLightChk, mPartialDarkChk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.htcbutton_demos_compound);

        mPartialLightChk = (HtcCheckBox) findViewById(R.id.chk_light_part);
        mPartialDarkChk = (HtcCheckBox) findViewById(R.id.chk_dark_part);

        if (mPartialLightChk != null)
            mPartialLightChk.setPartialSelection(true);

        if (mPartialDarkChk != null)
            mPartialDarkChk.setPartialSelection(true);
    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }

}
