
package com.htc.lib1.cc.quicktipswidget.activityhelper;

import android.os.Bundle;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.ActionBarItemView;
import com.htc.lib1.cc.test.R;

public class QuickTipsDemo extends ActivityBase {

    private ActionBarContainer actionBarContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        perpareActionBar();
        setContentView(R.layout.quicktips_main);
    }

    private void perpareActionBar() {
        ActionBarExt ae = new ActionBarExt(this, getActionBar());
        actionBarContainer = ae.getCustomContainer();

        ActionBarItemView actionBtnLeft = new ActionBarItemView(this);
        actionBtnLeft.setId(R.id.action_btn_left);
        actionBtnLeft.setIcon(R.drawable.ic_launcher);
        actionBarContainer.addStartView(actionBtnLeft);

        ActionBarItemView actionBtnCenter = new ActionBarItemView(this);
        actionBtnCenter.setId(R.id.action_btn_center);
        actionBtnCenter.setIcon(R.drawable.ic_launcher);
        actionBarContainer.addCenterView(actionBtnCenter);

        ActionBarItemView actionBtnRight = new ActionBarItemView(this);
        actionBtnRight.setId(R.id.action_btn_right);
        actionBtnRight.setIcon(R.drawable.ic_launcher);
        actionBarContainer.addEndView(actionBtnRight);
    }
}
