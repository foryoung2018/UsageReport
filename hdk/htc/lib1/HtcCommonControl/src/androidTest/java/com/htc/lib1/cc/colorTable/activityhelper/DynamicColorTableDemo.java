
package com.htc.lib1.cc.colorTable.activityhelper;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.widget.ListView;
import com.htc.lib1.cc.test.R;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.util.HtcCommonUtil;

public class DynamicColorTableDemo extends ActivityBase {

    private int[] mColorValues;
    private String[] mColorNames;

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
        initColorArray();
        setContentView(R.layout.theme_color_table);
        mListView = (ListView) findViewById(R.id.lv);
        mListView.setAdapter(new ColorTableAdapter(mColorValues, mColorNames, getLayoutInflater()));
    }

    private void initColorArray() {
        final int colorCounts = R.styleable.ThemeColor.length;
        mColorValues = new int[colorCounts];
        mColorNames = new String[colorCounts];
        final TypedArray a = obtainStyledAttributes(R.styleable.ThemeColor);
        for (int i = 0; i < colorCounts; i++) {
            final int attr = R.styleable.ThemeColor[i];
            mColorValues[i] = HtcCommonUtil.getCommonThemeColor(this, i);
            mColorNames[i] = getResources().getResourceEntryName(attr);
        }
        a.recycle();
    }

    @Override
    protected boolean isDisableDynamicTheme() {
        return false;
    }

}
