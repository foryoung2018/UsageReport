package com.htc.sense.commoncontrol.demo.alertdialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.htc.lib1.cc.app.HtcAlertActivity;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

// TODO: 1. extends HtcAlertActivity (not Activity) (optionally implements OnClickListener)
public class ListAlertActivity extends HtcAlertActivity implements OnClickListener {

    // private int mThemeId = 0; // TODO: 2. keep current theme id for resume check deprecated, change to use deep theme

    @Override
    protected void onCreate(Bundle bundle) {
        // TODO: 3. setup theme BEFORE super.onCreate
        CommonUtil.reloadDemoTheme(this, bundle);
        setTheme(R.style.HtcAlertActivityTheme);
        super.onCreate(bundle);

        setFinishOnTouchOutside(true);

        // TODO: 5. setup layout as usual
        mAlertParams.mPositiveButtonListener = this; // TODO: 6. don't forget to implements OnClickListener
        mAlertParams.mPositiveButtonText = getString(android.R.string.ok); // TODO: 7. you've to getString by yourself
        mAlertParams.mTitle = "HtcAlertActivityDemo";
        mAlertParams.mItems = getResources().getStringArray(R.array.list_dialog_items1);
        setupAlert();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        this.setResult(RESULT_OK); // TODO: 9. handle button click
    }
}
