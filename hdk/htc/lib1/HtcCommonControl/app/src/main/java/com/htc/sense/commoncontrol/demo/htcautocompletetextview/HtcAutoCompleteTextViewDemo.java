package com.htc.sense.commoncontrol.demo.htcautocompletetextview;

import android.os.Bundle;

import com.htc.lib1.cc.widget.HtcAutoCompleteTextView;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.WidgetDataPreparer;

public class HtcAutoCompleteTextViewDemo extends CommonDemoActivityBase {

    int mItemBackgroundResId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
}
