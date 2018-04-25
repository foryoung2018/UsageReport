package com.htc.lib1.cc.widget.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.widget.HtcSwitch;

public class MySwitchPreference extends HtcSwitch {

    private void init() {
        View checkableView = (View) findViewById(R.id.switchWidget);
        int ResId = this.getResources().getIdentifier("switchWidget", "id",
                "android");
        if (0 != ResId)
            checkableView.setId(ResId);
    }

    /** @hide */
    public MySwitchPreference(Context context) {
        super(context);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }

    /** @hide */
    public MySwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }

}
