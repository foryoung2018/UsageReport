
package com.htc.lib1.cc.reminderview.activityhelper;

import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.reminder.ui.ReminderView;
import com.htc.lib1.cc.widget.reminder.ui.ReminderView.ReminderTile;

public class TestActivityVideoCall extends ActivityBase {

    TestViewVideoCall mTestView;
    String TAG = "TestActivity";
    Handler mUIHandler = new Handler();

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.main_videocall);
        mTestView = (TestViewVideoCall) this.findViewById(R.id.test_reminder_view);
        mTestView.setFitsSystemWindows(true);

        if (mTestView != null) {
            mTestView.setCallback(new TestViewVideoCall.Callback() {
                @Override
                public void onTileDrop() {
                }

                @Override
                public void onTileDropEnd() {
                    finish();
                }

                @Override
                public void onButtonDrop() {
                }

                @Override
                public void onButtonDropEnd() {
                    finish();
                }
            });
        }
        increaseCoverage(mTestView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTestView.cleanUp();
    }

    private void increaseCoverage(ReminderView reminderView) {
        ReminderView rv = (ReminderView) reminderView;
        ReminderTile rt = rv.getTile(1);
        TextView textView = (TextView) findViewById(R.id.text11);
        rv.onStart();
        rv.onResume();
        rv.updateUI();
        rv.onButtonDrop(null);
        rv.onButtonDropEnd(null);
        rv.onTileDrop(null);
        rv.onTileDropEnd(null);
        rv.onButtonAccessibilityAction(null);
        rv.onTileAccessibilityAction(rt);
        rv.setTileDragAnimation(null, 1);
        rv.setTileDraggable(true, 1);
        rv.setTileDraggable(false, 1);
        rv.setReminderTile(R.layout.specific_lockscreen_3_lines_with_action, 1);
        rv.setReminderTile(0, 1);
        rv.setReminderTile(null, 1);
        rv.getButtonCount();
        rv.getButton(1);
        rt.getButton(1);
        rt.getButtonCount();
        rv.fadOutwhenDrop();
        rv.getDragThreshold();
        rv.getDragType();
        rv.startDragView();
        rv.stopDragView();

        rv.onGestureChanged(null, null);
        rv.setTitle(textView, "RRRRRR");
        rv.isShowMastHeadForDefault();
        rv.setMastHeadVisibility(true);
        rv.getTile(1);
        rv.getTile(2);
        rv.getTile(0);
        rv.onPause();
        rv.onStop();
        rv.cleanUp();
        new ReminderView(this);
        new ReminderView(this, null , 0);


    }
}
