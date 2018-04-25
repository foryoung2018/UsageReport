package com.htc.sense.commoncontrol.demo.actionmodecustom;

import com.htc.lib1.cc.util.ActionBarUtil;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;

import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ActionMode;

public class ActionModeDemoActivity extends CommonDemoActivityBase {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(android.R.id.content, new ActionModeFragment(), "HtcListFragment");
        transaction.commit();

    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        ActionBarUtil.setActionModeBackground(this, mode, new ColorDrawable(Color.GRAY));
    }

}
