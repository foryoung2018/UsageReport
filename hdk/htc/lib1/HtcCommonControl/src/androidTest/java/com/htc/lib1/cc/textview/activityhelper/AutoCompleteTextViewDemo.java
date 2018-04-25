
package com.htc.lib1.cc.textview.activityhelper;

import android.os.Bundle;
import com.htc.lib1.cc.test.R;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.HtcAutoCompleteTextView;
import com.htc.lib1.cc.textview.activityhelper.util.WidgetDataPreparer;

public class AutoCompleteTextViewDemo extends ActivityBase {

    int mItemBackgroundResId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HtcCommonUtil.initTheme(this, mCategoryId);
        setTheme(getIntent().getIntExtra("theme", R.style.MyTheme));
        setContentView(R.layout.htcautocompletetextview_layout);

        HtcAutoCompleteTextView bright_input = (HtcAutoCompleteTextView) findViewById(R.id.bright_input);
        bright_input.enableDropDownMinWidth(false);
        WidgetDataPreparer.prepareAdapater(this, bright_input);

        HtcAutoCompleteTextView dark_input = (HtcAutoCompleteTextView) findViewById(R.id.dark_input);
        dark_input.setMode(HtcAutoCompleteTextView.MODE_DARK_BACKGROUND);
        WidgetDataPreparer.prepareAdapater(this, dark_input);

        HtcAutoCompleteTextView full_input = (HtcAutoCompleteTextView) findViewById(R.id.full_input);
        full_input.setMode(HtcAutoCompleteTextView.MODE_BRIGHT_FULL_BACKGROUND);
        WidgetDataPreparer.prepareAdapater(this, full_input);
    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }
}
