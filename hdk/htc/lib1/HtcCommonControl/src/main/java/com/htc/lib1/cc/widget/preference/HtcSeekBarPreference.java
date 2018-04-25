package com.htc.lib1.cc.widget.preference;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.widget.HtcSeekBar;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class HtcSeekBarPreference extends HtcSeekBar {

    private void init() {
        SeekBar seekbar = (SeekBar) findViewById(R.id.seekbar);
        int ResId = this.getResources().getIdentifier("seekbar", "id",
                "android");
        if (0 != ResId)
            seekbar.setId(ResId);
    }

    /** @hide */
    public HtcSeekBarPreference(Context context) {
        super(context);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }

    /** @hide */
    public HtcSeekBarPreference(Context context, AttributeSet arg1) {
        super(context, arg1);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }

    /** @hide */
    public HtcSeekBarPreference(Context context, AttributeSet arg1, int arg2) {
        super(context, arg1, arg2);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }

}
