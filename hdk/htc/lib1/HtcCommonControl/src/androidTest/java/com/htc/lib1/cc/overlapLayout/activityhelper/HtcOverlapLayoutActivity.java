
package com.htc.lib1.cc.overlapLayout.activityhelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.htc.lib1.cc.test.R;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.widget.HtcFooter;
import com.htc.lib1.cc.widget.HtcOverlapLayout;

public class HtcOverlapLayoutActivity extends ActivityBase {
    boolean translucentStatusBar = true;
    boolean hasStatusBar = true;
    boolean hasActionBar = true;
    boolean translucentNavigationBar = true;
    boolean hasNavigationBar = true;
    boolean hasIME = true;
    int footerDisplayMode = HtcFooter.DISPLAY_MODE_DEFAULT;
    View main;

    private void getInfoFromIntent() {
        Intent i = getIntent();
        translucentStatusBar = i.getBooleanExtra("translucentStatusBar", true);
        hasStatusBar = i.getBooleanExtra("hasStatusBar", true);
        hasActionBar = i.getBooleanExtra("hasActionBar", true);
        translucentNavigationBar = i.getBooleanExtra("translucentNavigationBar", true);
        hasNavigationBar = i.getBooleanExtra("hasNavigationBar", true);
        hasIME = i.getBooleanExtra("hasIME", true);
        footerDisplayMode = i.getIntExtra("footerDisplayMode", HtcFooter.DISPLAY_MODE_DEFAULT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getInfoFromIntent();
        Window win = getWindow();

        if (!hasStatusBar)
            win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        if (!hasActionBar)
            getActionBar().hide();

        main = getLayoutInflater().inflate(R.layout.overlaplayout, null);
        if (!hasNavigationBar) {
            main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

        setContentView(main);
        HtcOverlapLayout overlap = (HtcOverlapLayout) main.findViewById(R.id.testoverlaplayout);
        HtcFooter footer = (HtcFooter) main.findViewById(R.id.testHtcFooter);
        footer.SetDisplayMode(footerDisplayMode);
        if (null != overlap)
            overlap.isActionBarVisible(hasActionBar);
    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }
}
