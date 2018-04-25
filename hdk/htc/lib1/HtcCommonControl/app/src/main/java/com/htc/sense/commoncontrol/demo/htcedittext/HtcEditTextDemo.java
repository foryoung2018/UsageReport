package com.htc.sense.commoncontrol.demo.htcedittext;

import android.os.Bundle;

import com.htc.lib1.cc.widget.HtcEditText;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class HtcEditTextDemo extends CommonDemoActivityBase {

    int mItemBackgroundResId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.htcedittext_layout);

        HtcEditText dark_input = (HtcEditText) findViewById(R.id.dark_input);
        dark_input.setMode(HtcEditText.MODE_DARK_BACKGROUND);

        HtcEditText full_input = (HtcEditText) findViewById(R.id.full_input);
        full_input.setMode(HtcEditText.MODE_BRIGHT_FULL_BACKGROUND);
    }
}
