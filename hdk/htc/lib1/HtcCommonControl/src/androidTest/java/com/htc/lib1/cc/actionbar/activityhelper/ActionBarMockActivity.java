
package com.htc.lib1.cc.actionbar.activityhelper;

import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import com.htc.lib1.cc.test.R;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.util.HtcCommonUtil;

public class ActionBarMockActivity extends ActivityBase {
    private boolean mEnableMenuFlag = false;
    private final String ACCESSIBILITY_CONTENT_DESCRIPTION = "Should be set by AP";

    @Override
    protected boolean isInitOrientation() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HtcCommonUtil.initTheme(this, mCategoryId);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.actionbar_mock);
    }

    public void enableMenu(boolean enable) {
        mEnableMenuFlag = enable;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (mEnableMenuFlag) {
            getMenuInflater().inflate(R.menu.actionbar_actions, menu);
            menu.getItem(0).setTitle(ACCESSIBILITY_CONTENT_DESCRIPTION);
            menu.getItem(1).setTitle(ACCESSIBILITY_CONTENT_DESCRIPTION);
        } else {
            if (menu != null) {
                menu.clear();
            }
        }
        return true;
    }
}
