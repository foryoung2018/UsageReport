package com.htc.lib1.cc.alertdialog.test;

import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.widget.FrameLayout;

import com.htc.lib1.cc.alertdialog.activityhelper.AlertDialogAut;
import com.htc.lib1.cc.test.R;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class TestAlertDialog extends HtcActivityTestCaseBase {

    private Resources res;
    private String va_ok;
    private String va_cancel;
    private static final int DIALOG = 0;
    private static final int MESSAGE_DIALOG = 1;
    private static final int LIST_DIALOG = 2;
    private static final int PROGRESS_DIALOG = 3;
    private static final int HORIZATOR_DIALOG = 4;

    public TestAlertDialog() {
        super(AlertDialogAut.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        res = getInstrumentation().getTargetContext().getResources();
        int themeID = res.getIdentifier("HtcDeviceDefault.DevelopDontUse", "style", "com.htc.alertdialog.aut");
        if (0 != themeID) {
            mThemeName = "HtcDeviceDefault.DevelopDontUse";
        } else {
            mThemeName = "HtcDeviceDefault";
        }
        va_ok = res.getString(R.string.va_ok);
        va_cancel = res.getString(R.string.va_cancel);
    }

    @Override
    protected void tearDown() throws Exception {
        final AlertDialogAut ada = (AlertDialogAut) getActivity();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ada.dismissDialog();
            }
        });
        getInstrumentation().waitForIdleSync();
        super.tearDown();
    }

    public void testDIALOG_ACKNOWLEDGEMENT() {
        showDialog(res.getString(R.string.delete_duplicate_contacts), res.getString(R.string.less_than_two), va_ok, null, null);
    }

    public void testDIALOG_QUESTION() {
        showDialog(res.getString(R.string.remove_account), res.getString(R.string.remove_account_warning), res.getString(R.string.remove_account), null, "More Info");
    }

    public void testDIALOG_LEGAL_MESSAGE() {
        showDialog(res.getString(R.string.htc_legal), res.getString(R.string.alert_dialog_long_message_content), null, null, null);
    }

    public void testDIALOG_ONE_LINE() {
        String negativeButton = va_ok + " " + va_ok + " " + va_ok + " " + va_ok;
        String neutralButton = res.getString(R.string.neutral) + " " + va_ok + " "
                + va_cancel + " " + va_ok + " " + va_cancel;
        String message = res.getString(R.string.one_line_message);
        showDialog(res.getString(R.string.center_align), message, va_ok, neutralButton, negativeButton);
    }

    public void testDIALOG_CHECK_MESSAGE() {
        final String checkMessageTitle = res.getString(R.string.demo_checkbox);
        String title = checkMessageTitle + checkMessageTitle + checkMessageTitle + checkMessageTitle + checkMessageTitle + checkMessageTitle;
        showDialog(title, res.getString(R.string.checkbox_description), va_ok, null, null);
    }

    public void testDIALOG_CHECK_MESSAGE2() {
        final String checkMessage2Title = res.getString(R.string.demo_checkbox) + " ";
        showDialog(checkMessage2Title, null, va_ok, null, null);
    }

    public void testDIALOG_LIST() {
        showListDialog(res.getString(R.string.list_dialog_title), R.array.list_dialog_items1);
    }

    public void testDIALOG_SINGLE_CHOICE() {
        showMessageDialog(res.getString(R.string.single_choice_list_dialog_title), R.array.single_choice_dialog_items2, 0, null, "More Info");
    }

    public void testDIALOG_MULTIPLE_CHOICES() {
        showMessageDialog(res.getString(R.string.multiple_choice_list_dialog_title), 0, R.array.multiple_choice_dialog_items3, null, "More Info");
    }

    public void testDIALOG_FONT_SELECTOR() {
        showDialog(res.getString(R.string.demo_checkbox), null, null, null, null);
    }

    public void testDIALOG_HORIZONTAL_PROGRESS() {
        showHorizontalProgressDialog(res.getString(R.string.progress_dialog_title), res.getString(R.string.export_contact), res.getString(R.string.run_in_background),
                res.getString(R.string.alert_dialog_cancel));
    }

    public void testDIALOG_HORIZONTAL_NO_MESSAGE() {
        final String horizontalNoMessage_title = res.getString(R.string.horizontal_progress_without_message);
        showHorizontalProgressDialog(horizontalNoMessage_title, null, res.getString(R.string.va_hide), va_cancel);
    }

    public void testDIALOG_HORIZONTAL_LONG_MESSAGE() {
        final String horizontalLong_title = res.getString(R.string.horizontal_progress_with_long_message);
        showHorizontalProgressDialog(horizontalLong_title, res.getString(R.string.alert_dialog_long_message_content), res.getString(R.string.va_hide), va_cancel);
    }

    public void testDIALOG_SPINNER_PROGRESS() {
        showProgressDialog(null, res.getString(R.string.st_loading));
    }

    public void testDIALOG_SPINNER_LISTITEM() {

        final String listSpinner_title = res.getString(R.string.spinner_with_title);
        showProgressDialog(listSpinner_title, res.getString(R.string.titled_spinner_message));
    }

    public void testDIALOG_AUTO_MESSAGE() {

        final String autoMesg_title = res.getString(R.string.notice_n_disclaimer_title);
        showDialog(autoMesg_title, res.getString(R.string.notice_n_disclaimer), res.getString(R.string.agree), null, res.getString(R.string.disagree));
    }

    public void testDIALOG_AUTO_MULTICHOICE() {
        final String titleAutoMultichoice = res.getString(R.string.auto_multi_choice);
        showMessageDialog(titleAutoMultichoice, 0, R.array.multiple_choice_dialog_items3, res.getString(R.string.alert_dialog_ok), null);
    }

    public void testDIALOG_AUTO_SINGLECHOICE() {
        final String titleAutoSingleChoice = res.getString(R.string.auto_single_choice);
        showMessageDialog(titleAutoSingleChoice, R.array.single_choice_dialog_items2, 0, null, "More Info");
    }

    public void testDIALOG_AUTO_CHECKBOX() {
        showDialog(res.getString(R.string.checkbox_in_auto), res.getString(R.string.checkbox_in_auto_description), res.getString(R.string.alert_dialog_ok), null, null);
    }

    private void showListDialog(String title, int items) {
        Intent i = new Intent();
        i.putExtra("themeName", mThemeName);
        i.putExtra("title", title);
        i.putExtra("items", items);
        i.putExtra("type", LIST_DIALOG);
        setActivityIntent(i);
        initActivity();
        View v = mSolo.getView(FrameLayout.class, 0).getRootView();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, v, ScreenShotUtil.getScreenShotName(this));
    }

    private void showMessageDialog(final String title, final int singleItems, final int multiItems, final String positiveButton, final String neutralButton) {
        Intent i = new Intent();
        i.putExtra("themeName", mThemeName);
        i.putExtra("title", title);
        i.putExtra("singleItems", singleItems);
        i.putExtra("multiItems", multiItems);
        i.putExtra("positiveButton", positiveButton);
        i.putExtra("neutralButton", neutralButton);
        i.putExtra("type", MESSAGE_DIALOG);
        setActivityIntent(i);
        initActivity();
        View v = mSolo.getView(FrameLayout.class, 0).getRootView();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, v, ScreenShotUtil.getScreenShotName(this));
    }

    private void showDialog(final String title, final String message, final String positiveButton, final String neutralButton, final String negativeButton) {
        Intent i = new Intent();
        i.putExtra("themeName", mThemeName);
        i.putExtra("title", title);
        i.putExtra("message", message);
        i.putExtra("positiveButton", positiveButton);
        i.putExtra("neutralButton", neutralButton);
        i.putExtra("negativeButton", negativeButton);
        i.putExtra("type", DIALOG);
        setActivityIntent(i);
        initActivity();
        View v = mSolo.getView(FrameLayout.class, 0).getRootView();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, v, ScreenShotUtil.getScreenShotName(this));
    }

    private void showHorizontalProgressDialog(final String title, final String message, final String positiveButton, final String negativeButton) {
        Intent i = new Intent();
        i.putExtra("themeName", mThemeName);
        i.putExtra("title", title);
        i.putExtra("message", message);
        i.putExtra("positiveButton", positiveButton);
        i.putExtra("negativeButton", negativeButton);
        i.putExtra("type", HORIZATOR_DIALOG);
        setActivityIntent(i);
        initActivity();
        View v = mSolo.getView(FrameLayout.class, 0).getRootView();
        mSolo.waitForDialogToOpen();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, v, ScreenShotUtil.getScreenShotName(this));
    }

    private void showProgressDialog(final String title, final String message) {
        Intent i = new Intent();
        i.putExtra("themeName", mThemeName);
        i.putExtra("title", title);
        i.putExtra("message", message);
        i.putExtra("type", PROGRESS_DIALOG);
        setActivityIntent(i);
        initActivity();
        View v = mSolo.getView(FrameLayout.class, 0).getRootView();
        mSolo.waitForDialogToOpen();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, v, ScreenShotUtil.getScreenShotName(this));
    }
}
