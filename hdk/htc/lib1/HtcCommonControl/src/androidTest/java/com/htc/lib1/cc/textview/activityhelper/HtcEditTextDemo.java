
package com.htc.lib1.cc.textview.activityhelper;

import android.os.Bundle;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.HtcEditText;

public class HtcEditTextDemo extends ActivityBase {

    int mItemBackgroundResId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HtcCommonUtil.initTheme(this, mCategoryId);
        setContentView(R.layout.htcedittext_layout);

        HtcEditText dark_input = (HtcEditText) findViewById(R.id.dark_input);
        dark_input.setMode(HtcEditText.MODE_DARK_BACKGROUND);

        HtcEditText full_input = (HtcEditText) findViewById(R.id.full_input);
        full_input.setMode(HtcEditText.MODE_BRIGHT_FULL_BACKGROUND);
    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }
}
