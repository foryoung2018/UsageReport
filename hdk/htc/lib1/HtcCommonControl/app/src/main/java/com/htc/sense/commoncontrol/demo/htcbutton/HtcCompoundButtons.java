package com.htc.sense.commoncontrol.demo.htcbutton;

import android.os.Bundle;

import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.lib1.cc.widget.HtcCheckBox;

public class HtcCompoundButtons extends CommonDemoActivityBase {
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

}
