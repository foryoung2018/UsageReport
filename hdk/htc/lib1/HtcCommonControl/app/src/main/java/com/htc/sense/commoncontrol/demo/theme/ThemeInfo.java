package com.htc.sense.commoncontrol.demo.theme;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.theme.ThemeFileInnerHelper;
import com.htc.lib1.theme.ThemeFileUtil;
import com.htc.lib1.theme.ThemeType;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class ThemeInfo extends CommonDemoActivityBase{
    private static final String TAG = "ThemeInfo";
    public static final String KEY_THEME_NAME = "HtcDeviceDefault";
    public static final String KEY_CATEGORY_NAME = null;

    private boolean firstLaunch = true;

    private String mThemeName;
    private String mCategoryName;


    private void getInfoFromIntent() {
        Intent intent = getIntent();
        mThemeName = (null == intent.getStringExtra(KEY_THEME_NAME)) ? KEY_THEME_NAME : intent.getStringExtra(KEY_THEME_NAME) ;
        mCategoryName = (null == intent.getStringExtra(KEY_CATEGORY_NAME)) ? KEY_CATEGORY_NAME : null ;
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
        setContentView(R.layout.theme_info);

        StringBuilder sb = new StringBuilder();
        TextView tv1 = (TextView) findViewById(android.R.id.text1);
        sb.append("- getAppsThemePath =").append(ThemeFileUtil.getAppsThemePath(this)).append("\n");
        sb.append("- getCurrentThemePath =").append(HtcCommonUtil.getCurrentThemePath(this)).append("\n");
        tv1.setText(sb.toString());

        sb.delete(0, sb.length());


        ThemeType.ThemeValue tttv = ThemeType.getValue(this, ThemeType.HTC_THEME_CT);
        sb.append("- isFile =").append(tttv.isFile).append("\n");
        sb.append("- selfData =").append(tttv.selfData).append("\n");
        sb.append("- themeTitle =").append(tttv.themeTitle).append("\n");
        tttv = ThemeType.getValue(this, ThemeType.HTC_THEME_CC);
        sb.append("\n--------------\n");
        sb.append("- isFile =").append(tttv.isFile).append("\n");
        sb.append("- selfData =").append(tttv.selfData).append("\n");
        sb.append("- themeTitle =").append(tttv.themeTitle).append("\n");


        TextView tv2 = (TextView) findViewById(android.R.id.text2);
        tv2.setSingleLine(false);
        tv2.setText(sb.toString());


        View v = findViewById(R.id.delete_item);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThemeFileInnerHelper.deleteFolderFile(ThemeFileUtil.getAppsThemePath(ThemeInfo.this), false);
            }
        });

        onThemeFileComplete();
    }

    @Override
    protected void onThemeFileComplete() {
        super.onThemeFileComplete();
        Drawable d = HtcCommonUtil.getCommonThemeTexture(this, R.styleable.CommonTexture_android_windowBackground);
        Drawable d1 = HtcCommonUtil.getCommonThemeTexture(this, R.styleable.CommonTexture_android_headerBackground);
        Drawable d2 = HtcCommonUtil.getCommonThemeTexture(this, R.styleable.CommonTexture_android_panelBackground);

        Log.d(TAG, "d1 = "+d1);

        if ( null != mActionBarExt )
            mActionBarExt.setBackgroundDrawable(d1);
    }

    private ContextThemeWrapper createThemeContext(String packageName,
                                                   String themeName) {
        ContextThemeWrapper ctw = null;
        try {
            Context context = createPackageContext(packageName,
                    Context.CONTEXT_IGNORE_SECURITY);
            int themeId = context.getResources().getIdentifier(themeName,
                    "style", packageName);
            ctw = new ContextThemeWrapper(context, themeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ctw;
    }

}
