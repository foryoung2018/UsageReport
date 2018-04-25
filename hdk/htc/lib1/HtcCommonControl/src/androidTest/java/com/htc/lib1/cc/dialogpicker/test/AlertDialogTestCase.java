
package com.htc.lib1.cc.dialogpicker.test;

import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.htc.lib1.cc.dialogpicker.activityhelper.AlertDialogDemo;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;
import com.htc.test.util.WidgetUtil;

public class AlertDialogTestCase extends HtcActivityTestCaseBase {
    // main(top) 3 categories
    private static final int DIALOG_ALL_MESSAGE_DIALOGS = 1; // message dialogs
    private static final int DIALOG_ALL_LIST_DIALOGS = 2; // list dialogs
    private static final int DIALOG_ALL_PROGRESS_DIALOGS = 3; // progress
                                                              // dialogs
    private static final int DIALOG_ALL_AUTOMOTIVE_DIALOGS = 4; // automotive
                                                                // dialogs

    // 1. message dialogs
    private static final int DIALOG_ACKNOWLEDGEMENT = 11; // short message, 1
                                                          // button
    private static final int DIALOG_QUESTION = 12; // longer message, 2 buttons
    private static final int DIALOG_LEGAL_MESSAGE = 13; // legal message, super
                                                        // long, no button
    private static final int DIALOG_ONE_LINE = 14; // one line message
    private static final int DIALOG_INVERSEBKG = 15; // inverse background

    // 2. list dialogs
    private static final int DIALOG_LIST = 21; // list dialog, no button
    private static final int DIALOG_SINGLE_CHOICE = 22; // single choice dialog,
                                                        // 1 cancel button
    private static final int DIALOG_MULTIPLE_CHOICES = 23; // multiple choices
                                                           // dialog, 2 buttons
    private static final int DIALOG_LONG_LIST = 24; // long list, 3 buttons

    // 3. progress dialogs
    private static final int DIALOG_SPINNER_PROGRESS = 31; // spinner progress
                                                           // dialog
    private static final int DIALOG_HORIZONTAL_PROGRESS = 32; // horizontal
                                                              // progress dialog
    // private static final int DIALOG_SPINNER_LISTITEM = 33; // spinner
    // progress dialog with title
    private static final int DIALOG_HORIZONTAL_NO_MESSAGE = 34; // horizontal
                                                                // progress
                                                                // dialog with
                                                                // no text
                                                                // message
    private static final int DIALOG_HORIZONTAL_LONG_MESSAGE = 35; // horizontal
                                                                  // progress
                                                                  // dialog with
                                                                  // long text
                                                                  // message

    // 4. automotive dialogs
    private static final int DIALOG_AUTO_MESSAGE = 41;
    private static final int DIALOG_AUTO_LIST = 42;
    private static final int DIALOG_AUTO_MULTICHOICE = 43;

    private static final int DIALOG_HORIZONTAL_NO_NUMBER = 36;
    private static final int DIALOG_HORIZONTAL_NO_NUMBER_MINHEIGHT = 37;


    public AlertDialogTestCase() {
        super(AlertDialogDemo.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    private void callShowDialog(final int id) {
        Intent intent = new Intent();
        intent.putExtra("dialogId", id);
        setActivityIntent(intent);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        getInstrumentation().waitForIdleSync();
    }

    private final void mTestShowAllTypeDialog(int id) {

        callShowDialog(id);
        getInstrumentation().waitForIdleSync();
        hideScrollView();
        View v = mSolo.getView(FrameLayout.class, 0).getRootView();
        if (id == DIALOG_SPINNER_PROGRESS && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            WidgetUtil.setProgressBarIndeterminatedStopRunning(this,
                    (ProgressBar) mSolo.getView(ProgressBar.class, 0));
        } else if (id == DIALOG_HORIZONTAL_PROGRESS) {
            mSolo.getView(ProgressBar.class, 0).setProgress(20);
        }
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, v, ScreenShotUtil.getScreenShotName(this));
        mSolo.goBack();
    }

    private void hideScrollView() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ((AlertDialogDemo) mActivity).setScrollBarEnabled(false);
            }
        });
    }
    public final void testDIALOG_ALL_MESSAGE_DIALOGS() {
        mTestShowAllTypeDialog(DIALOG_ALL_MESSAGE_DIALOGS);
    }

    public final void testDIALOG_ALL_LIST_DIALOGS() {
        mTestShowAllTypeDialog(DIALOG_ALL_LIST_DIALOGS);
    }

    public final void testDIALOG_ALL_PROGRESS_DIALOGS() {
        mTestShowAllTypeDialog(DIALOG_ALL_PROGRESS_DIALOGS);
    }

    public final void testDIALOG_ALL_AUTOMOTIVE_DIALOGS() {
        mTestShowAllTypeDialog(DIALOG_ALL_AUTOMOTIVE_DIALOGS);
    }

    public final void testDIALOG_ACKNOWLEDGEMENT() {
        mTestShowAllTypeDialog(DIALOG_ACKNOWLEDGEMENT);
    }

    public final void testDIALOG_QUESTION() {
        mTestShowAllTypeDialog(DIALOG_QUESTION);
    }

    public final void testDIALOG_LEGAL_MESSAGE() {
        mTestShowAllTypeDialog(DIALOG_LEGAL_MESSAGE);
    }

    public final void testDIALOG_ONE_LINE() {
        mTestShowAllTypeDialog(DIALOG_ONE_LINE);
    }

    public final void testDIALOG_LIST() {
        mTestShowAllTypeDialog(DIALOG_LIST);
    }

    public final void testDIALOG_SINGLE_CHOICE() {
        mTestShowAllTypeDialog(DIALOG_SINGLE_CHOICE);
    }

    public final void testDIALOG_MULTIPLE_CHOICES() {
        mTestShowAllTypeDialog(DIALOG_MULTIPLE_CHOICES);
    }

    public final void testDIALOG_LONG_LIST() {
        mTestShowAllTypeDialog(DIALOG_LONG_LIST);
    }

    public final void testDIALOG_HORIZONTAL_PROGRESS() {
        mTestShowAllTypeDialog(DIALOG_HORIZONTAL_PROGRESS);
    }

    public final void testDIALOG_SPINNER_PROGRESS() {
        mTestShowAllTypeDialog(DIALOG_SPINNER_PROGRESS);
    }

    public final void testDIALOG_HORIZONTAL_NO_MESSAGE() {
        mTestShowAllTypeDialog(DIALOG_HORIZONTAL_NO_MESSAGE);
    }

    public final void testDIALOG_HORIZONTAL_LONG_MESSAGE() {
        mTestShowAllTypeDialog(DIALOG_HORIZONTAL_LONG_MESSAGE);
    }

    public final void testDIALOG_AUTO_MESSAGE() {
        mTestShowAllTypeDialog(DIALOG_AUTO_MESSAGE);
    }

    public final void testDIALOG_AUTO_LIST() {
        mTestShowAllTypeDialog(DIALOG_AUTO_LIST);
    }

    public final void testDIALOG_AUTO_MULTICHOICE() {
        mTestShowAllTypeDialog(DIALOG_AUTO_MULTICHOICE);
    }

    public final void testDIALOG_HORIZONTAL_NO_NUMBER() {
        mTestShowAllTypeDialog(DIALOG_HORIZONTAL_NO_NUMBER);
    }

    public final void testDIALOG_HORIZONTAL_NO_NUMBER_MINHEIGHT() {
        mTestShowAllTypeDialog(DIALOG_HORIZONTAL_NO_NUMBER_MINHEIGHT);
    }

    public void testImproveCoverageCheckBox() {
        initActivity();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ((AlertDialogDemo) mActivity).testImproveCoverageCheckBox();
            }
        });
    }

    public final void testImproveCoverage() {
        initActivity();
        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((AlertDialogDemo) mActivity).improveCoverage();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
