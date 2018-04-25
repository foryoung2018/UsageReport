
package com.htc.lib1.cc.alertdialog.test;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.util.HtcCommonUtil.ObtainThemeListener;
import com.htc.lib1.cc.util.HtcCommonUtil.ThemeChangeObserver;
import com.htc.lib1.theme.ThemeType;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.lib1.cc.test.R;

import java.util.Observable;
import java.util.Observer;

public class HtcCommonUtilTest extends HtcActivityTestCaseBase {

    private String mApkName;
    private String mApkPath;

    public HtcCommonUtilTest() {
        super(ActivityBase.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initActivity();
        mApkName = getInstrumentation().getContext().getPackageName();
        mApkPath = getInstrumentation().getContext().getPackageCodePath();
    }

    public void testInitTheme() {
        HtcCommonUtil.initTheme(mActivity, HtcCommonUtil.BASELINE);
    }

    public void testInitThemeAsUser() {
        HtcCommonUtil.initTheme(mActivity, HtcCommonUtil.CATEGORYTWO, -2, true);
    }

    public void testRegisterThemeChangeObserver() {
        HtcCommonUtil.registerThemeChangeObserver(mActivity, HtcCommonUtil.CATEGORYTHREE, themeChangeObserver);
    }

    public void testRegisterThemeChangeObserver_CC() {
        HtcCommonUtil.registerThemeChangeObserver(mActivity, ThemeType.HTC_THEME_CC, themeChangeObserver);
    }

    public void testRegisterThemeChangeObserver_CT() {
        HtcCommonUtil.registerThemeChangeObserver(mActivity, ThemeType.HTC_THEME_CT, themeChangeObserver);
    }

    public void testRegisterThemeChangeObserver_FULL() {
        HtcCommonUtil.registerThemeChangeObserver(mActivity, ThemeType.HTC_THEME_FULL, themeChangeObserver);
    }

    public void testRegisterThemeChangeObserverAsUser_CC() {
        HtcCommonUtil.registerThemeChangeObserver(mActivity, ThemeType.HTC_THEME_CC, themeChangeObserver, -2);
    }

    public void testAddObserver() {
        HtcCommonUtil.addObserver(HtcCommonUtil.TYPE_FONT_SIZE, observer);
    }

    public void testObtainThemeListener() {
        final ObtainThemeListener obtain = new ObtainThemeListener();
        obtain.onObtainThemeColor(0, 1);
        HtcCommonUtil.setObtianThemeListener(obtain);
    }

    public void testSetHtcThemePackage() {
        HtcCommonUtil.setHtcThemePackage(mActivity, ThemeType.HTC_THEME_WALLPAPER_ALLAPPS, "CBaseline");
    }

    public void testSetHtcThemePackageAsUser() {
        HtcCommonUtil.setHtcThemePackage(mActivity, ThemeType.HTC_THEME_WALLPAPER_ALLAPPS, "CBaseline", -2);
    }

    public void testGetHtcThemePackage() {
        HtcCommonUtil.getHtcThemePackage(mActivity, HtcCommonUtil.CATEGORYONE);
    }

    public void testGetHtcThemePackageAsUser() {
        HtcCommonUtil.getHtcThemePackage(mActivity, HtcCommonUtil.CATEGORYONE, -2);
    }

    public void testGetHtcThemeId_Baseline() {
        HtcCommonUtil.getHtcThemeId(mActivity, HtcCommonUtil.BASELINE);
    }

    public void testGetHtcThemeId() {
        HtcCommonUtil.getHtcThemeId(mActivity, 3);
    }

    public void testGetResources_2param() {
        HtcCommonUtil.getResources(mActivity, mApkName);
    }

    public void testGetResourcesAsUser() {
        HtcCommonUtil.getResources(mActivity, mApkName, -2);
    }

    public void testGetResources_3param() {
        HtcCommonUtil.getResources(mActivity, mApkName, mApkPath);
    }

    public void testGetCategoryTheme() {
        HtcCommonUtil.getCategoryTheme(mActivity);
    }

    public void testGetCurrentThemePath() {
        HtcCommonUtil.getCurrentThemePath();
    }

    public void testGetCurrentThemePathAsUser() {
        HtcCommonUtil.getCurrentThemePath(-2);
    }

    public void testGetCurrentThemePath_OneParam() {
        HtcCommonUtil.getCurrentThemePath(mActivity);
    }

    public void testGetCurrentThemePathAsUser_OneParam() {
        HtcCommonUtil.getCurrentThemePath(mActivity, -2);
    }

    public void testGetCommonThemeColor() {
        HtcCommonUtil.getCommonThemeColor(mActivity, R.styleable.ThemeColor_category_color);
    }

    public void testGetCommonThemeTexture() {
        HtcCommonUtil.getCommonThemeTexture(mActivity, 3);
    }

    public void testUpdateCommonResConfiguration() {
        HtcCommonUtil.updateCommonResConfiguration(mActivity);
    }

    public void testNotifyChange() {
        HtcCommonUtil.notifyChange(mActivity, HtcCommonUtil.TYPE_FONT_STYLE);
    }

    public void testUnregisterThemeChangeObserver() {
        HtcCommonUtil.unregisterThemeChangeObserver(HtcCommonUtil.CATEGORYTHREE, themeChangeObserver);
    }

    public void testUnregisterThemeChangeObserverAsUser() {
        HtcCommonUtil.unregisterThemeChangeObserver(HtcCommonUtil.CATEGORYTHREE, themeChangeObserver, -2);
    }

    public void testClearCache() {
        HtcCommonUtil.clearCache();
    }

    private ThemeChangeObserver themeChangeObserver = new ThemeChangeObserver() {
        @Override
        public void onThemeChange(int type) {
        }
    };

    private Observer observer = new Observer() {

        @Override
        public void update(Observable observable, Object data) {
        }
    };
}
