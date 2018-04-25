
package com.htc.lib1.cc.recipientblock.test;

import android.os.Parcel;
import android.os.SystemClock;
import android.view.MotionEvent;

import com.htc.lib1.cc.recipientblock.activityhelper.RecipientBlockMockActivity;
import com.htc.lib1.cc.widget.HtcAutoCompleteTextView;
import com.htc.lib1.cc.widget.recipientblock.ComposeRecipientArea;
import com.htc.lib1.cc.widget.recipientblock.ComposeRecipientArea.ComposeRecipientCallBack;
import com.htc.lib1.cc.widget.recipientblock.HtcRecipientButton;
import com.htc.lib1.cc.widget.recipientblock.ReceiverList;
import com.htc.lib1.cc.widget.recipientblock.RecipientBlock;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

import java.util.ArrayList;

public class TestRecipientBlock extends HtcActivityTestCaseBase {

    public TestRecipientBlock() {
        super(RecipientBlockMockActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    private void assertSnapShot() {
        getInstrumentation().waitForIdleSync();
        final HtcAutoCompleteTextView actv = (HtcAutoCompleteTextView) mSolo
                .getView("receiverList_inputfield_to");
        try {
            runTestOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (null != actv) {
                        actv.setCursorVisible(false);
                    }

                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }

        ScreenShotUtil.AssertViewEqualBefore(mSolo, mActivity.findViewById(android.R.id.content), this);
    }

    public final void testDefault() {
        assertSnapShot();
    }

    public void testComposeRecipientAreaIncreaseCoverage() {
        ComposeRecipientArea cpa = new ComposeRecipientArea(mActivity, null, 0);
        cpa = new ComposeRecipientArea(mActivity);

        final ReceiverList newReceiver = new ReceiverList(System.currentTimeMillis(), null, "Test",
                -1, -1);
        cpa.addSingleRecipientByReceiverList(newReceiver, false);
        ArrayList<ReceiverList> receiverList = new ArrayList<ReceiverList>();
        receiverList.add(newReceiver);
        cpa.addMultipleRecipientsByReceiverLists(receiverList, false);
        cpa.getActionButton(0);
        cpa.getReceivers();
        cpa.getShowAllPreviewLinesNum();
        cpa.setShowAllPreviewLinesNum(1);
        cpa.updateNewRecipients(receiverList);
        cpa.updateContactDataInfo(receiverList);
        cpa.updateShowAllVisibility(false);
        cpa.onConfigurationChanged(mActivity.getResources().getConfiguration());
        cpa.updateNewRecipients(receiverList);
        cpa.removeSingleRecipientByReceiverList(newReceiver, true);
        cpa.relayoutAllUI();
        cpa.setComposeRecipientCallBack(new ComposeRecipientCallBack() {

            @Override
            public void onReceiverButtonClick(ComposeRecipientArea composeRecipientArea,
                    ReceiverList recevier) {
            }

            @Override
            public void afterRemoveSingleReceiver(ComposeRecipientArea composeRecipientArea,
                    ReceiverList receiver, boolean minusFrequency) {
            }

            @Override
            public void afterAddSingleReceiver(ComposeRecipientArea composeRecipientArea,
                    ReceiverList receiver, boolean plusFrequency) {
            }

            @Override
            public void afterAddMultipleReceivers(ComposeRecipientArea composeRecipientArea,
                    ArrayList<ReceiverList> receivers, boolean plusFrequency) {
            }
        });
        cpa.onReceiverButtonClick(newReceiver);
    }

    public void testHtcRecipientButtonIncreaseCoverage() {
        HtcRecipientButton button = new HtcRecipientButton(mActivity, null);
        button = new HtcRecipientButton(mActivity);
        button.getButton();
        button.setOnClickListener(null);
        button.setOnLongClickListener(null);
        button.onInterceptTouchEvent(null);
        final long time = SystemClock.uptimeMillis();
        button.onTouchEvent(MotionEvent.obtain(time, time, MotionEvent.ACTION_UP, 0, 0, 0));
    }

    public void testReceiverListIncreaseCoverage() {
        final ReceiverList newReceiver = new ReceiverList(System.currentTimeMillis(), null, "Test",
                -1, -1);
        newReceiver.contactExists();
        newReceiver.describeContents();
        newReceiver.hasDisplayName();
        newReceiver.readFromParcel(Parcel.obtain());
        newReceiver.writeToParcel(Parcel.obtain(), 0);
    }

    public void testRecipientBlockIncreaseCoverage() {
        final RecipientBlock block = new RecipientBlock(mActivity, null, 0);
        block.setup(mActivity, "label", false, false);
        block.onConfigurationChanged(mActivity.getResources().getConfiguration());
    }
}
