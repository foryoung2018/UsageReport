
package com.htc.lib1.cc.appfragment.activityhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class CheckUtilDemo extends Activity {

    public CheckUtilCoustemView targetView;

    public static final String CREATE_TYPE = "createType";
    public static final int CREATE_WIDGET_IN_UITHREAD = 0;
    public static final int CREATE_WIDGET_NOTIN_UITHREAD = 1;
    public static final int CREATE_WIDGET_WITH_CONTEXTTHEMEWRAPPER = 2;
    public static final int CREATE_WIDGET_NOTWITH_CONTEXTTHEMEWRAPPER = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (null != intent) {
            int type = intent.getIntExtra(CREATE_TYPE, CREATE_WIDGET_IN_UITHREAD);
            switch (type) {
                case CREATE_WIDGET_IN_UITHREAD:
                    createWidgetInUIThread();
                    break;
                case CREATE_WIDGET_NOTIN_UITHREAD:
                    createWidgetNotInUIThread();
                    break;
                case CREATE_WIDGET_WITH_CONTEXTTHEMEWRAPPER:
                    createWidgetWithContextThemeWrapper();
                    break;
                case CREATE_WIDGET_NOTWITH_CONTEXTTHEMEWRAPPER:
                    createWidgetNotWithContextThemeWrapper();
                    break;

                default:
                    break;
            }
        }
    }

    public void createWidgetInUIThread() {
        targetView = new CheckUtilCoustemView(this);
    }

    public void createWidgetNotInUIThread() {
        new Thread() {
            public void run() {
                targetView = new CheckUtilCoustemView(CheckUtilDemo.this);
            };
        }.start();
    }

    public void createWidgetWithContextThemeWrapper() {
        targetView = new CheckUtilCoustemView(this);
    }

    public void createWidgetNotWithContextThemeWrapper() {
        targetView = new CheckUtilCoustemView(getApplicationContext());
    }
}
