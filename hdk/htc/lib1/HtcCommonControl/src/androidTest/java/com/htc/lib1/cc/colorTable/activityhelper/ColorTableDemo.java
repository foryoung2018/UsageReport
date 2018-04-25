
package com.htc.lib1.cc.colorTable.activityhelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.ListView;

import com.htc.lib1.cc.test.R;

public class ColorTableDemo extends Activity {

    public static final String KEY_THEME_NAME = "ThemeName";
    public static final String KEY_CATEGORY_NAME = "CategoryName";
    public static final String themePackage = "com.htc.lib1.cc.test";

    private int[] mColorValues;
    private String[] mColorNames;

    private String mThemeName;
    private String mCategoryName;

    private ListView mListView;

    private void getInfoFromIntent() {
        Intent intent = getIntent();
        mThemeName = intent.getStringExtra(KEY_THEME_NAME);
        mCategoryName = intent.getStringExtra(KEY_CATEGORY_NAME);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getInfoFromIntent();
        if (null == mThemeName) {
            return;
        }
        String themeCategory;
        if (null == mCategoryName || "".equals(mCategoryName)) {
            themeCategory = mThemeName;
        } else {
            themeCategory = mThemeName + "." + mCategoryName;
        }
        initColorArray(themeCategory);
        setContentView(R.layout.theme_color_table);
        mListView = (ListView) findViewById(R.id.lv);
        mListView.setAdapter(new ColorTableAdapter(mColorValues, mColorNames, getLayoutInflater()));
    }

    private ContextThemeWrapper createThemeContext(String packageName,
            String themeName) {
        ContextThemeWrapper ctw = null;
        try {
            final Context context = createPackageContext(packageName,
                    Context.CONTEXT_IGNORE_SECURITY);
            final int themeId = context.getResources().getIdentifier(themeName,
                    "style", packageName);
            ctw = new ContextThemeWrapper(context, themeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ctw;
    }

    private void initColorArray(String themeName) {
        final int colorCounts = R.styleable.ThemeColor.length;
        final ContextThemeWrapper mCtx = createThemeContext(themePackage, themeName);
        if (null == mColorNames && null == mColorValues) {
            mColorValues = new int[colorCounts];
            mColorNames = new String[colorCounts];
        }
        final TypedArray a = mCtx.obtainStyledAttributes(R.styleable.ThemeColor);
        for (int i = 0; i < colorCounts; i++) {
            String name = mCtx.getResources().getResourceEntryName(R.styleable.ThemeColor[i]);
            mColorValues[i] = a.getColor(i, 0);
            mColorNames[i] = name;
        }
        a.recycle();
    }
}
