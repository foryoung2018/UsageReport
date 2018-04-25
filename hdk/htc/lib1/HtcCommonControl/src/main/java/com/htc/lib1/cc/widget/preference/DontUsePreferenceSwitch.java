package com.htc.lib1.cc.widget.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.widget.HtcSwitch;

/**
*
* @deprecated Common Control internal used
* Only use for replacing Preference by HtcSwitch.
* This can't be used by applications.
* @hide
*/
@Deprecated
public class DontUsePreferenceSwitch extends HtcSwitch {

    private void init() {
        View checkableView = findViewById(R.id.switchWidget);
        int ResId = getResources().getIdentifier("switchWidget", "id",
                "android");
        if (0 != ResId) {
            checkableView.setId(ResId);
        }
    }

    /**
     * Create a new DontUsePreferenceSwitch.
     * @param context the application environment
     */
    /** @hide */
    public DontUsePreferenceSwitch(Context context) {
        super(context);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }

    /**
     * Create a new DontUsePreferenceSwitch.
     * @param context the application environment
     * @param attrs attributeSet
     */
    /** @hide */
    public DontUsePreferenceSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }

}
