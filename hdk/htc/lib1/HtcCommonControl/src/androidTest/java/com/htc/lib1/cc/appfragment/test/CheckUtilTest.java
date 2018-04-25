
package com.htc.lib1.cc.appfragment.test;

import android.content.Intent;

import com.htc.lib1.cc.appfragment.activityhelper.CheckUtilDemo;
import com.htc.lib1.cc.appfragment.activityhelper.CheckUtilCoustemView;
import com.htc.test.HtcActivityTestCaseBase;

public class CheckUtilTest extends HtcActivityTestCaseBase {

    private CheckUtilCoustemView targetView;

    public CheckUtilTest() {
        super(CheckUtilDemo.class);
    }

    private void startActivityWithType(int type) {
        Intent intent = new Intent();
        intent.putExtra(CheckUtilDemo.CREATE_TYPE, type);
        setActivityIntent(intent);
        initActivity();
        CheckUtilDemo demo = (CheckUtilDemo) mActivity;
        targetView = demo.targetView;
    }

    public void testInitViewInUIThread() {
        startActivityWithType(CheckUtilDemo.CREATE_WIDGET_IN_UITHREAD);
        assertTrue(targetView.isUIThread);
    }

    public void testInitViewNotInUIThread() {
        startActivityWithType(CheckUtilDemo.CREATE_WIDGET_NOTIN_UITHREAD);
        assertFalse(targetView.isUIThread);
    }

    public void testInitViewWithContextThemeWrapper() {
        startActivityWithType(CheckUtilDemo.CREATE_WIDGET_WITH_CONTEXTTHEMEWRAPPER);
        assertTrue(targetView.isContextThemeWrapper);
    }

    public void testInitViewNotWithContextThemeWrapper() {
        startActivityWithType(CheckUtilDemo.CREATE_WIDGET_NOTWITH_CONTEXTTHEMEWRAPPER);
        assertFalse(targetView.isContextThemeWrapper);
    }

}
