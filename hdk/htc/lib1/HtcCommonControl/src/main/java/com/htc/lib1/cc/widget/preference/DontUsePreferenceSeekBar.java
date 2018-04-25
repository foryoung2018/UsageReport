package com.htc.lib1.cc.widget.preference;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.widget.HtcSeekBar;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
*
* @deprecated Common Control internal used
* Only use for replacing Preference by HtcSeekBar.
* This can't be used by applications.
* @hide
*/
@Deprecated
public class DontUsePreferenceSeekBar extends HtcSeekBar {

    private void init() {
        SeekBar seekbar = (SeekBar) findViewById(R.id.seekbar);
        int ResId = getResources().getIdentifier("seekbar", "id",
                "android");
        if (0 != ResId) {
            seekbar.setId(ResId);
        }
    }

    /**
     * Create a new DontUsePreferenceSeekBar and initial progress is 0.
     * @param context the application environment
     */
    /** @hide */
    public DontUsePreferenceSeekBar(Context context) {
        super(context);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }

    /**
     * Create a new DontUsePreferenceSeekBar and initial progress is 0.
     * @param context the application environment
     * @param attrs attributeSet
     */
    /** @hide */
    public DontUsePreferenceSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }

    /**
     * Create a new DontUsePreferenceSeekBar and initial progress is 0.
     * @param context the application environment
     * @param attrs attributeSet
     * @param defStyle default style for HtcSeekBar
     */
    /** @hide */
    public DontUsePreferenceSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }

}
