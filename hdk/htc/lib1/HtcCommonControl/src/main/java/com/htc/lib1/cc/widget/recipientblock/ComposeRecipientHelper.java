package com.htc.lib1.cc.widget.recipientblock;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.htc.lib1.cc.R;

/********************************************************************************************
 * All Recipients Data, UI will be controlled here.
 *
 * UI Layout Guide :
 *
 *  Main :
 *   +------------------------------------------------------+  <- ComposeRecipientArea
 *   | ((Recipient1) (Recipient2) (Recipient3)) <-- Group1  |
 *   | ((Recipient4)) <-- Group2                            |
 *   | Edit | Show CC/Bcc | ShowAll ▼ <-- RecipientActionBar|
 *   +------------------------------------------------------+
 *
 *  RecipientBtn/NonRecipientBtn:
 *   +----------------------------------------+  <- ParentLayout
 *   | ( AA@AA.com ) <-- HtcRecipientButton  |
 *   +----------------------------------------+
 *
 *  RecipientActionBar:
 *  Edit | Show CC/Bcc | ShowAll ▼
 *  Edit | Show CC/Bcc
 *  Edit | ShowAll ▼
 *  Edit
 *
 *  We increase the touch area to improve UX in using RecipientActionBar.
 *  The width of touch area will update into M2*2 and height of touch area is M1/2.
 *  You can reference {@link HtcRecipientButton.setStye} However, we should add
 *  extra M1/2 top padding for RecipientActionBar's ParentLayout if the button has same
 *  group as "Edit" button. Moreover, we need update ComposeRecipientBlock bottom padding
 *  to extra M/2 when add/remove edit recipient, we just need to check "Edit" button to callback to ComposeActivity.
 *
 *   +-------------------------+  <- HtcRecipientButton (ceiling)
 *   |         ↑ M1 ↑          |
 *   | ← M1   ( Edit )   M1 →  |  <- increased touch area
 *   |         ↓ M1 ↓          |
 *   +-------------------------+  <- HtcRecipientButton (floor)
 *
 *    define in Sense 6.0 UIGL v1.5 p84.
 *    @hide
 ********************************************************************************************/
public class ComposeRecipientHelper {
    private static final String TAG = "ComposeRecipientHelper";
    private static final int BTN_TYPE_RECIPIENT      = ComposeRecipientArea.BTN_TYPE_RECIPIENT;
    private static final int BTN_TYPE_SHOW_ALL       = ComposeRecipientArea.BTN_TYPE_SHOW_ALL;
    private static final int BTN_TYPE_CUSTOMIZE = ComposeRecipientArea.BTN_TYPE_CUSTOMIZE;

    private WeakReference<ComposeRecipientArea> mWeakComposeRecipientArea;
    private WeakReference<Activity> mWeakComposeActivity;

    LinearLayout.LayoutParams mParaComposeRecipientArea;

    private ArrayList<ReceiverList> mReceiverLists = new ArrayList<ReceiverList>();

    private ArrayList<RecipientBtn> mRecipientBtns = new ArrayList<RecipientBtn>();

    private ArrayList<NonRecipientBtn> mNonRecipientBtns = new ArrayList<NonRecipientBtn>();

    private ArrayList <CustomizedActionButton> mCustomizeActionButtonList = new ArrayList <CustomizedActionButton>();

    private Context mContext;
    private LayoutInflater mInflater;
    private int mGroupIndex = -1;
    private int mChildIndex = -1;
    private HashMap<String, Integer> mGroupIndexMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> mChildIndexMap = new HashMap<String, Integer>();

    //For show/hide status
    private NonRecipientBtn mShowAll;
    private int sShowAllPreviewLinesNum = 3;
    private boolean mNeedAddShowAll = false;
    private boolean mShowAllStatus = true; // Show all recipient buttons by default
    private boolean mHasHideAllButton = false; // Show all button is visible with text "Hide all". This is for addMultipleReceivers to decide show all status
    private String mStrShowAll;
    private String mStrHide;

    /**
     * Set show all preview line numbers
     * @param lineNum, show all preview line numbers
     * @hide
    **/
    protected void setShowAllPreviewLinesNum(int lineNum){
        sShowAllPreviewLinesNum = lineNum;
    }

    /**
     * Get show all preview line numbers
     * @return show all preview line numbers
     * @hide
    **/
    protected int getShowAllPreviewLinesNum(){
        return sShowAllPreviewLinesNum;
    }

    private class CustomizedActionButton {
        private String mButtonText;
        private OnClickListener mBtnOnClickListener;
        private boolean bNeedShowInNextLine = false;

        public String getButtonText() { return mButtonText; }
        public OnClickListener getButtonOnClickListener() { return mBtnOnClickListener; }
        public boolean isNeedShowInNextLine() { return bNeedShowInNextLine; }

        public void setButtonText(String text) {
            mButtonText = text;
        }

        public void setButtonClickListener(OnClickListener onClickListener) {
            mBtnOnClickListener = onClickListener;
        }

        public void setNeedShowInNextLine(boolean nextLine) {
            bNeedShowInNextLine = nextLine;
        }

        public CustomizedActionButton(String buttonText, boolean needShowInNextLine, OnClickListener btnListener) {
            mButtonText = buttonText;
            mBtnOnClickListener = btnListener;
            bNeedShowInNextLine = needShowInNextLine;
        }
    }



    private OnClickListener mRecipientListener;

    public ComposeRecipientHelper(Context context, WeakReference<Activity> weakComposeActivity,
            ComposeRecipientArea weakComposeRecipientArea) {
        mContext = context;
        mWeakComposeActivity = weakComposeActivity;
        mWeakComposeRecipientArea = new WeakReference<ComposeRecipientArea>(weakComposeRecipientArea);
        mParaComposeRecipientArea = (LayoutParams) mWeakComposeRecipientArea.get().getLayoutParams();
        mRecipientListener = new showRecipientDialogClickListener(mWeakComposeRecipientArea);
        mInflater = LayoutInflater.from(context);
        mStrHide = mContext.getString(R.string.va_hide);
        mStrShowAll = mContext.getString(R.string.va_show_all);
    }

    /*@hide*/
    protected ArrayList<ReceiverList> getReceiverLists() {
        return mReceiverLists;
    }

    /**
     * For test case only
     * Get action button list
     * @return action button list
     * @hide
    **/
    protected ArrayList<NonRecipientBtn> getActionButtonLists() {
        return mNonRecipientBtns;
    }

    public void onConfigurationChanged() {
        mShowAllStatus = false;
        relayoutAllUI();
    }

    private RecipientBtn getNewRecipientBtn(ReceiverList receiver) {
        ComposeRecipientArea composeRecipientArea = (ComposeRecipientArea) mWeakComposeRecipientArea.get();
        if (composeRecipientArea == null){
            Log.d(TAG, "get new recipient button: composeRecipientArea is null ");
            return null;
        }

        LinearLayout parent = getNewRecipientBtnParentLayout();

        HtcRecipientButton btn = (HtcRecipientButton) parent.findViewById(R.id.recipientBtn);

        String displayName = receiver.name;
        if(displayName == null || displayName.length() == 0) {
            displayName = receiver.addr;
        }
        btn.setText(displayName);

        btn.setOnClickListener(mRecipientListener);

        int width = composeRecipientArea.getBtnWidth(btn);

        btn.setWidth(width);

        receiver.view = parent;

//        receiver.group = mRecipientArea;

        RecipientBtn recipientBtn = new RecipientBtn(mWeakComposeRecipientArea, parent, btn, BTN_TYPE_RECIPIENT, receiver, false);

        recipientBtn.setChildIndex(mChildIndex);

        btn.setTag(recipientBtn);

        return recipientBtn;
    }

    private LinearLayout getNewRecipientBtnParentLayout() {
        LinearLayout parent = (LinearLayout) mInflater.inflate(R.layout.recipient_item, null, false);

        mChildIndex++;
        mChildIndexMap.put(getViewHashKey(parent), mChildIndex);

        return parent;
    }
    /********************************************************************************************
     * Add Receivers Block
     ********************************************************************************************/

    /*@hide*/
    protected void addSingleReceiver(ReceiverList receiver) {

        addSingleReceiverToUI(receiver);

        addSingleReceiversToData(receiver);
    }

    private void addSingleReceiverToUI(ReceiverList receiver) {
        ComposeRecipientArea composeRecipientArea = (ComposeRecipientArea) mWeakComposeRecipientArea.get();
        if (composeRecipientArea == null){
            Log.d(TAG, "add single receiver to UI: composeRecipientArea is null ");
            return;
        }

        if (receiver == null){
            Log.d(TAG, "add single receiver to UI: receiver is null ");
            return;
        }

        RecipientBtn recipientBtn = getNewRecipientBtn(receiver);

//        if (breakForHideAll(recipientBtn)) {
//           return;
//        }

        if (addRecipientBtnToValidGroup(recipientBtn)) {
            mRecipientBtns.add(recipientBtn);
            Log.d(TAG, "add single receiver to UI: recipient size = " + mRecipientBtns.size());
        }
    }

    private void addSingleReceiversToData(ReceiverList receiver) {
        mReceiverLists.add(receiver);
    }

    private boolean addRecipientBtnToValidGroup(RecipientBtn recipientBtn) {
        ComposeRecipientArea composeRecipientArea = (ComposeRecipientArea) mWeakComposeRecipientArea.get();
        if (composeRecipientArea == null){
            Log.d(TAG, "add recipient button to valid group: composeRecipientArea is null ");
            return false;
        }

        if (recipientBtn == null) return false;

        int recipientWidth = getRecipientBtnWidth(recipientBtn);

        int groupCount = composeRecipientArea.getChildCount();
        LinearLayout lastGroup = null;

        // get first valid child from top to bottom
        for (int i = (groupCount-1); i >= 0; i--) {
            LinearLayout group = (LinearLayout) composeRecipientArea.getChildAt(i);
            int groupWidth = (Integer) group.getTag();

            if (groupWidth <= 0) {
                // empty is valid
                lastGroup = group;
                continue;
            }

            // The new button (only Edit button) needs at begin of next line from SPM request.
            if (recipientBtn.isBeginNextLine()) break;

            // The new button width exceeds remaining space
            if ((groupWidth + recipientWidth) > composeRecipientArea.getRecipientContainerMaxWidth()) break;

            lastGroup = group;
            break;
        }

        if (lastGroup == null) {
            lastGroup = getNewGroup();
            if(!(recipientBtn instanceof NonRecipientBtn))
                recipientBtn.getParentLayout().setPadding(ResUtils.getRecipientContainerPadding(mContext), ResUtils.getDimenMarginM2(mContext), 0, 0);
        }

        int groupCurrentWidth = (Integer) lastGroup.getTag();

        int newWidth = groupCurrentWidth + recipientWidth;
        lastGroup.setTag(newWidth);

        lastGroup.addView(recipientBtn.getParentLayout());

        int groupIndex = mGroupIndexMap.get(getViewHashKey(lastGroup));
        recipientBtn.setGroupIndex(groupIndex);

        return true;
    }

    private LinearLayout getNewGroup() {
        ComposeRecipientArea composeRecipientArea = (ComposeRecipientArea) mWeakComposeRecipientArea.get();
        if (composeRecipientArea == null){
            Log.d(TAG, "get new group: composeRecipientArea is null ");
            return null;
        }

        LinearLayout group = new LinearLayout(mContext);
        group.setOrientation(LinearLayout.HORIZONTAL);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        group.setLayoutParams(params);

        mGroupIndex++;
        mGroupIndexMap.put(getViewHashKey(group), mGroupIndex);

        int defaultWidth = 0;
        group.setTag(defaultWidth);

        composeRecipientArea.addView(group);

        return group;
    }

    /*@hide*/
    protected void updateShowAllLayouts(boolean newStatus) {
        mShowAllStatus = newStatus;

        if (mShowAll == null) return;

        mShowAll.setStatus(newStatus);

        relayoutAllUI();
    }

    /*@hide*/
    protected void relayoutAllUI() {
        mNeedAddShowAll = false;

        removeAllRecipientActionBarBtn();

        removeMultipleReceiversFromUI(mReceiverLists);

        if (mReceiverLists != null && mReceiverLists.size() > 0) {

            addMultipleReceiversToUI(mReceiverLists);

            addAllRecipientActionBarBtn();
        }

    }

    /*@hide*/
    protected void addMultipleReceivers(ArrayList<ReceiverList> receivers) {
        if (receivers == null || receivers.size() <= 0){
            Log.d(TAG, "add multiple receivers: receivers is null or size<=0");
            return;
        }
        mShowAllStatus = mHasHideAllButton ?
                true: // Show all buttons if current status is show all
                false;// Don't show all recipient buttons if user add multiple recipient button.
        addMultipleReceiversToUI(receivers);

        addMultipleReceiversToData(receivers);

        updateShowAllStatus(receivers, mRecipientBtns);
    }

    private void addMultipleReceiversToUI(ArrayList<ReceiverList> receivers) {
        ComposeRecipientArea composeRecipientArea = (ComposeRecipientArea) mWeakComposeRecipientArea.get();
        if (composeRecipientArea == null){
            Log.d(TAG, "add multiple receivers to UI: composeRecipientArea is null");
            return;
        }

        if (receivers == null){
            Log.d(TAG, "add multiple receivers to UI: receivers is null");
            return;
        }

        for (ReceiverList receiver : receivers) {

            RecipientBtn recipientBtn = getNewRecipientBtn(receiver);

            if (breakForHideAll(recipientBtn)) {
                break;
            }

            if (addRecipientBtnToValidGroup(recipientBtn)) {
                mRecipientBtns.add(recipientBtn);
            }
        }
        Log.d(TAG, "add multiple receivers to UI: recipient size = " + mRecipientBtns.size());

    }

    /************************************************************************************************
     * update "show all" status in order to fix wrong show/hide status problem:
     *
     * Scenario
     * 1: Add Mutiple buttons to exceed 3 lines and hide all
     *     ------------  ------------
     *     |AAA@htc.com| |AAA@htc.com|
     *     ------------  ------------
     *     ------------  ------------
     *     |AAA@htc.com| |AAA@htc.com|
     *     ------------  ------------
     *     ------------  ------------
     *     |AAA@htc.com| |AAA@htc.com|
     *     ------------  ------------
     *       show all
     *
     *  2. Remove buttons until "show all" doesn't exist.
     *     ------------  ------------
     *     |AAA@htc.com| |AAA@htc.com|
     *     ------------  ------------
     *     ------------
     *     |AAA@htc.com|
     *     ------------
     *
     *  3. add multiple button to exceed 3 lines
     *     ------------  ------------
     *     |AAA@htc.com| |AAA@htc.com|
     *     ------------  ------------
     *     ------------  ------------
     *     |AAA@htc.com| |AAA@htc.com|
     *     ------------  ------------
     *     ------------  ------------
     *     |AAA@htc.com| |AAA@htc.com|
     *     ------------  ------------
     *     ------------
     *     |AAA@htc.com|
     *     ------------
     *       show all <--------should be "hide"
     *
     * @param composeRecipientArea, the composeRecipientArea which added a receiver button
     * @param newReceiverList, new recipient list
     * @param visibleRecipientBtnList, visible recipient button list
     * */
    private void updateShowAllStatus(ArrayList<ReceiverList> newReceiverList, ArrayList<RecipientBtn> visibleRecipientBtnList) {
        ComposeRecipientArea composeRecipientArea = (ComposeRecipientArea) mWeakComposeRecipientArea.get();
        if (composeRecipientArea == null || newReceiverList == null || visibleRecipientBtnList == null){
            Log.d(TAG, "update show all status: composeRecipientArea or newReceiverList or visibleRecipientBtnList is null");
            return;
        }

        int groupIndex = 0;
        int groupWidth = 0;
        int btnCount = 0;
        final int maxGroupWidth = composeRecipientArea.getRecipientContainerMaxWidth();

        // Calculate visible line count
        for (RecipientBtn recipientBtn : visibleRecipientBtnList) {
            btnCount++;
            int recipientWidth = getRecipientBtnWidth(recipientBtn);

            if ((groupWidth + recipientWidth) > maxGroupWidth){
                groupIndex++;
                groupWidth = recipientWidth;
            }else{
                groupWidth += recipientWidth;
            }
            //if(groupIndex+1>sShowAllPreviewLinesNum) break;
        }
        int visibleLines = groupIndex+1;

        // Calculate real line count
        //mReceiverLists : all receiver list
        if(mReceiverLists.size()>visibleRecipientBtnList.size()){
            int startIndex = mReceiverLists.size()>newReceiverList.size() ? 0 : btnCount;
            for (int i = startIndex; i < newReceiverList.size() ; i++) {
                RecipientBtn recipientBtn = getNewRecipientBtn(newReceiverList.get(i));

                if (recipientBtn == null) break;

                int recipientWidth = getRecipientBtnWidth(recipientBtn);

                if ((groupWidth + recipientWidth) > maxGroupWidth){
                    groupIndex++;
                    groupWidth = recipientWidth;
                }else{
                    groupWidth += recipientWidth;
                }

                if(groupIndex+1 > visibleLines) break;
            }
        }
        int realLines = groupIndex+1;

        //update show all status
        if(realLines>sShowAllPreviewLinesNum && visibleLines<=sShowAllPreviewLinesNum || visibleLines < realLines ) {
            mShowAllStatus=false;//Current status: some buttons are hidden.
            mHasHideAllButton = false;// has show all button with text "Show all"
        }
        else{
            mShowAllStatus=true;//Current status: all buttons are shown.

            if(realLines<=sShowAllPreviewLinesNum)
                mHasHideAllButton = false;// no show all button
            else
                mHasHideAllButton = true;// show all button with text "Hide"
        }

    }

    private void addMultipleReceiversToData(ArrayList<ReceiverList> receivers) {
        mReceiverLists.addAll(receivers);
    }

    private boolean breakForHideAll(RecipientBtn recipientBtn) {
        ComposeRecipientArea composeRecipientArea = (ComposeRecipientArea) mWeakComposeRecipientArea.get();
        if (composeRecipientArea == null){
            Log.d(TAG, "break for hide all: composeRecipientArea is null");
            return false;
        }

        int lines = 0;

        int lastGroupWidth = -1;

        int groupCount = composeRecipientArea.getChildCount();

        for (int i = 0; i < groupCount; i++) {
            LinearLayout group = (LinearLayout) composeRecipientArea.getChildAt(i);
            if (group == null) continue;

            int groupWidth = (Integer) group.getTag();
            if (groupWidth <= 0) continue;

            lines++;
            lastGroupWidth = groupWidth;
        }

        if ((lastGroupWidth + (Integer) recipientBtn.getParentLayout().getTag()) > composeRecipientArea.getRecipientContainerMaxWidth()) {
            lines++;
        }

        if (lines > sShowAllPreviewLinesNum) {
            mNeedAddShowAll = true;
        }

        if (!mShowAllStatus && lines > sShowAllPreviewLinesNum) {
            return true;
        }

        return false;
    }

    private int getRecipientBtnWidth(RecipientBtn recipientBtn) {
        if (recipientBtn == null){
            Log.d(TAG, "get recipient button width: recipientBtn is null");
            return 0;
        }

        // btn width
        int result = recipientBtn.getHtcRecipientButton().getBtnWidth();

        // btn padding
        result = result + recipientBtn.getParentLayout().getPaddingRight() + recipientBtn.getParentLayout().getPaddingLeft();

        // divider width + padding
        if(recipientBtn.getRightDivider() != null && recipientBtn.getRightDivider().getBackground() != null) {
            result = result + recipientBtn.getRightDivider().getBackground().getIntrinsicWidth() +
                         ((LinearLayout.LayoutParams) recipientBtn.getRightDivider().getLayoutParams()).leftMargin +
                         ((LinearLayout.LayoutParams) recipientBtn.getRightDivider().getLayoutParams()).rightMargin;
        }
        return result;
    }

    /*@hide*/
    protected boolean getStatusByType(int btnType) {
        if (btnType == BTN_TYPE_SHOW_ALL) {
            return mShowAllStatus;
        }

        return false;
    }

    /*@hide*/
    protected void setStatusByType(int btnType, boolean status) {
       if (btnType == BTN_TYPE_SHOW_ALL) {
            if (mShowAllStatus == status) return;
            updateShowAllLayouts(status);
        }
    }

    /*@hide*/
    protected void addAllRecipientActionBarBtn() {
        addCustomizeActionBtn();
        addShowAllBtn();

        ComposeRecipientArea composeRecipientArea = (ComposeRecipientArea) mWeakComposeRecipientArea.get();
        if(mNonRecipientBtns == null || mNonRecipientBtns.size() == 0 && mShowAll == null){

// For sense55 Message style input filed.
//            if( composeRecipientArea.getChildCount()==1){
//                /*
//                 * If only single line recipient area exists,
//                 * set the compose recipient area height as list item height.
//                 *
//                 *   +------------------------------------------------------+
//                 *   |                                                      |
//                 *   +-----------------------+                              |
//                 *   |           ↑ M2 ↑      |           ↑    ↑             |
//                 *   +      +----------------+      List item height        |
//                 *   | ← M1 |( aaa@htc.com ) |           ↓    ↓             |
//                 *   +-----------------------+                              |
//                 *   |                                                      |
//                 *   +------------------------------------------------------+
//                 */
//                int padding = (int) ((ResUtils.getListItemHeight(mContext) - composeRecipientArea.getRecipientHeight()+1)/2);
//                mParaComposeRecipientArea.setMargins(0,padding- ResUtils.getDimenMarginM2(mContext), ResUtils.getRecipientContainerPadding(mContext), padding);
//                composeRecipientArea.setLayoutParams(mParaComposeRecipientArea);
//
//            }else {
                /*
                 * If action button doesn't exist, set right margin M2 and bottom margin M1 to ComposeRecipientArea.
                 *
                 * +--------------------------------------------------------+
                 * |            ↑ M2 ↑                               |      |
                 * |      +-----------------+      +-----------------+      |
                 * | ←M1→ | ( aaa@htc.com ) | ←M2→ | ( aaa@htc.com ) | ←M1→ |
                 * |------+-----------------+------+-----------------+------|
                 * |            ↓ M1 ↓                               |      |
                 * +--------------------------------------------------------+
                 * */
                mParaComposeRecipientArea.setMargins(0, 0, ResUtils.getRecipientContainerPadding(mContext), ResUtils.getRecipientContainerPadding(mContext));
                composeRecipientArea.setLayoutParams(mParaComposeRecipientArea);
//}

        }else{
            /*
             * If action button exist, only set right margin M2 to ComposeRecipientArea.
             *
             * +--------------------------------------------------------+
             * |            ↑ M2 ↑                               |      |
             * |      +-----------------+      +-----------------+      |
             * | ←M1→ | ( aaa@htc.com ) | ←M2→ | ( aaa@htc.com ) | ←M1→ |
             * |------+-----------------+------+-----------------+------|
             * |           ↑ M1 ↑                      ↑ M1 ↑           |
             * |       +----------+       |       +-------------+       |
             * |←M2*2→ | ( Edit ) | ←M2*2→|←M2*2→ | ( Show all) | ←M2*2→|
             * |       +----------+       |       +-------------+       |
             * |          ↓ M1 ↓                       ↓ M1 ↓           |
             * +--------------------------------------------------------+
             * */
            mParaComposeRecipientArea.setMargins(0, 0, ResUtils.getRecipientContainerPadding(mContext),0);
            composeRecipientArea.setLayoutParams(mParaComposeRecipientArea);
        }
    }

    private boolean mRemoveActionButton = false;// for debug
    /*@hide*/
    protected void removeAllRecipientActionBarBtn() {
        mRemoveActionButton = true;
        removeCustomizeBtn();
        removeShowAllBtn();
        mRemoveActionButton = false;
    }

    /*@hide*/
    protected void addShowAllBtn() {

        if (!mNeedAddShowAll && getRecipientLines() <= sShowAllPreviewLinesNum) return;

        if (mShowAll == null) createShowAllBtn();

        addRecipientBtnToValidGroup(mShowAll);
        mRecipientBtns.add(mShowAll);
        Log.d(TAG, "add show all button to UI: recipient size = " + mRecipientBtns.size());
    }

    /*@hide*/
    protected void addNewActionButton(String buttonText, boolean needShowInNextLine, OnClickListener buttonOnClickListener) {
        CustomizedActionButton customizeActionBtn = new CustomizedActionButton(buttonText, needShowInNextLine, buttonOnClickListener);
        mCustomizeActionButtonList.add(customizeActionBtn);
    }

    private void addCustomizeActionBtn() {
        if(mNonRecipientBtns == null || mNonRecipientBtns.size() == 0) {
            createCustomizeBtn();
        }

        for(NonRecipientBtn customizeActionButton : mNonRecipientBtns) {
            addRecipientBtnToValidGroup(customizeActionButton);
            mRecipientBtns.add(customizeActionButton);
        }
        Log.d(TAG, "add custom action button to UI: recipient size = " + mRecipientBtns.size());
    }

    private void createCustomizeBtn() {
        ComposeRecipientArea composeRecipientArea = (ComposeRecipientArea) mWeakComposeRecipientArea.get();
        if (composeRecipientArea == null){
            Log.d(TAG, "create custom button: composeRecipientArea is null");
            return;
        }
        boolean needShowRightDivider = false;
        int index = 0;
        for(CustomizedActionButton customizeActionButton : mCustomizeActionButtonList) {
            if(customizeActionButton == null) return;
            index++;
            needShowRightDivider = (index == mCustomizeActionButtonList.size()) ? false : true;
            LinearLayout parent = getNewRecipientBtnParentLayout();
            HtcRecipientButton htcRecipientButton = (HtcRecipientButton) parent.findViewById(R.id.recipientBtn);
            htcRecipientButton.setOnClickListener(customizeActionButton.getButtonOnClickListener());
            NonRecipientBtn customizeNonRecipientBtn = new NonRecipientBtn(mWeakComposeRecipientArea
                    , parent
                    , htcRecipientButton
                    , BTN_TYPE_CUSTOMIZE, null
                    , true
                    , customizeActionButton.getButtonText()
                    , customizeActionButton.getButtonText()
                    , customizeActionButton.isNeedShowInNextLine()
                    , needShowRightDivider);
            customizeNonRecipientBtn.setChildIndex(mChildIndex);
            mNonRecipientBtns.add(customizeNonRecipientBtn);
        }
    }

    /*@hide*/
    protected void relayoutAllFromNewReceivers(ArrayList<ReceiverList> newReceivers) {
        mNeedAddShowAll = false;

        removeAllRecipientActionBarBtn();

        removeMultipleReceivers(mReceiverLists);

        if (newReceivers != null && newReceivers.size() > 0) {

            addMultipleReceivers(newReceivers);

            addAllRecipientActionBarBtn();
        }
   }

    // Show All Button
    private void createShowAllBtn() {
        ComposeRecipientArea composeRecipientArea = (ComposeRecipientArea) mWeakComposeRecipientArea.get();
        if (composeRecipientArea == null){
            Log.d(TAG, "create show all button: composeRecipientArea is null");
            return;
        }
        LinearLayout parent = getNewRecipientBtnParentLayout();

        HtcRecipientButton htcRecipientButton = (HtcRecipientButton) parent.findViewById(R.id.recipientBtn);
        htcRecipientButton.setOnClickListener(mShowAllListener);

        mShowAll = new NonRecipientBtn(mWeakComposeRecipientArea, parent, htcRecipientButton, BTN_TYPE_SHOW_ALL, null,
                mShowAllStatus, mStrHide, mStrShowAll,
                (mNonRecipientBtns == null || mNonRecipientBtns.size() == 0)// If there is no customizeActionBtns, showAll should be put in the next line.
                , false);
        mShowAll.setChildIndex(mChildIndex);
    }

    private void removeCustomizeBtn() {
        if(mNonRecipientBtns == null || mNonRecipientBtns.size() == 0) return;
        for(NonRecipientBtn customizeActionButton : mNonRecipientBtns) {
            removeSingleReceiverByRecipientBtn(customizeActionButton);
        }
        mNonRecipientBtns.clear();
    }

    private void removeShowAllBtn() {
        if (mShowAll == null) return;

        removeSingleReceiverByRecipientBtn(mShowAll);

        mShowAll = null;
    }

    /********************************************************************************************
     * Remove Receivers Block
     ********************************************************************************************/
    // Single
    /*@hide*/
    protected void removeSingleReceiver(ReceiverList receiver) {

        if (receiver == null){
            Log.d(TAG, "remove single receiver: receiver is null ");
            return;
        }

        RecipientBtn recipientBtn = getRecipientBtnByReceiverList(receiver);

        if (recipientBtn != null) {
            removeSingleReceiverByRecipientBtn(recipientBtn);
        } else { // The recipientBtn return null because the button is hidden by "Show all", so only remove data
            removeSingleReceiverFromData(receiver);
        }
    }

    private void removeSingleReceiverFromData(ReceiverList receiver) {
        mReceiverLists.remove(receiver);
    }

    private void removeSingleReceiverFromUI(ReceiverList receiver) {

        if (receiver == null) return;

        RecipientBtn recipientBtn = getRecipientBtnByReceiverList(receiver);

        if (recipientBtn != null) {
            removeSingleRecipientBtnFromUI(recipientBtn);
        }
    }

    // Remove by Recipient Btn
    private void removeSingleReceiverByRecipientBtn(RecipientBtn recipientBtn) {

        if (recipientBtn == null){
            Log.d(TAG, "remove single receiver by recipient button: recipientBtn is null ");
            return;
        }

        // Remove from UI
        removeSingleRecipientBtnFromUI(recipientBtn);

        // Remove from Data
        removeSingleRecipientBtnFromData(recipientBtn);
    }

    private void removeSingleRecipientBtnFromUI(RecipientBtn recipientBtn) {
        ComposeRecipientArea composeRecipientArea = (ComposeRecipientArea) mWeakComposeRecipientArea.get();
        if (composeRecipientArea == null){
            Log.d(TAG, "remove single receiver from UI : composeRecipientArea is null ");
            return;
        }

        if (recipientBtn == null){
            Log.d(TAG, "remove single receiver from UI: recipientBtn is null ");
            return;
        }

        boolean deleted = false;

        int targetGroupIndex = recipientBtn.getGroupIndex();
        int targetChildIndex = recipientBtn.getChildIndex();

        LinearLayout group = null;

        int groupCount = composeRecipientArea.getChildCount();
        for (int i = 0; i < groupCount; i++) {
            group = (LinearLayout) composeRecipientArea.getChildAt(i);
            if (group == null) continue;

            int groupIndex = mGroupIndexMap.get(getViewHashKey(group));

            if (groupIndex != targetGroupIndex) continue;

            int childCount = group.getChildCount();
            for (int j = 0; j < childCount; j++) {
                LinearLayout child = (LinearLayout) group.getChildAt(j);
                if (child == null) continue;
                int childIndex = mChildIndexMap.get(getViewHashKey(child));
                if (childIndex != targetChildIndex) continue;

                int childWidth = getRecipientBtnWidth(recipientBtn);
                int groupWidth = (Integer) group.getTag();

                groupWidth -= childWidth;

                group.removeView(child);

                group.setTag(groupWidth);

                deleted = true;

                break;
            }

            if(group.getChildCount()==0)
                composeRecipientArea.removeView(group);

            if (deleted) break;
        }

        mChildIndexMap.remove(getViewHashKey(recipientBtn.getParentLayout()));
        mRecipientBtns.remove(recipientBtn);

        if(mRemoveActionButton){
            Log.d(TAG, "remove action buttons from UI: recipient size = " + mRecipientBtns.size());
        }else if(!mRemoveMultipleReceivers){
            Log.d(TAG, "remove single receiver from UI: recipient size = " + mRecipientBtns.size());
        }
    }

    private void removeSingleRecipientBtnFromData(RecipientBtn recipientBtn) {
        mReceiverLists.remove(recipientBtn.getReceiverList());
    }

    private void removeMultipleReceivers(ArrayList<ReceiverList> receivers) {
        for (int i = receivers.size() - 1; i >= 0; i--) {
            ReceiverList receiver = receivers.get(i);

            if (receiver == null) continue;

            removeSingleReceiver(receiver);
        }
    }

    private boolean mRemoveMultipleReceivers = false;// for debug
    private void removeMultipleReceiversFromUI(ArrayList<ReceiverList> receivers) {
        if (receivers == null){
            Log.d(TAG, "remove multiple receivers from UI: receivers is null");
            return;
        }

        mRemoveMultipleReceivers = true;
        for (int i = receivers.size() - 1; i >= 0; i--) {
            ReceiverList receiver = receivers.get(i);

            if (receiver == null) continue;

            removeSingleReceiverFromUI(receiver);
        }
        mRemoveMultipleReceivers = false;
        Log.d(TAG, "remove multiple receivers from UI: recipient size = " + mRecipientBtns.size());
    }

    /**
     * For test case only
     * Get recipient button which is related to the inputted receiver list.
     * @param receiver related receiver list
     * @return recipient button
     * @hide
    **/
    protected RecipientBtn getRecipientBtnByReceiverList(ReceiverList receiver) {
        if (mRecipientBtns.size() <= 0) return null;

        if (receiver == null){
            Log.d(TAG, "get recipient button: receiver is null ");
            return null;
        }

        RecipientBtn result = null;
        for (RecipientBtn tmpRecipientBtn : mRecipientBtns) {
            if (tmpRecipientBtn == null) continue;

            ReceiverList tmpReceiver = tmpRecipientBtn.getReceiverList();

            if (tmpReceiver == null) continue;

            if (!tmpReceiver.equals(receiver)) continue;

            result = tmpRecipientBtn;
            break;
        }

        return result;
    }

    /**
     * @return lines of only containing recipient buttons
     */
    private int getRecipientLines() {
        ComposeRecipientArea composeRecipientArea = (ComposeRecipientArea) mWeakComposeRecipientArea.get();
        if (composeRecipientArea == null){
            Log.d(TAG, "get recipient lines: composeRecipientArea is null ");
            return 0;
        }

        int count = 0;

        int groupCount = composeRecipientArea.getChildCount();


        for (int i = 0; i < groupCount; i++) {
            LinearLayout group = (LinearLayout) composeRecipientArea.getChildAt(i);
            if (group == null) continue;

            int groupIndex = mGroupIndexMap.get(getViewHashKey(group));
            int groupWidth = (Integer) group.getTag();

            if (mShowAll != null && mShowAll.getGroupIndex() == groupIndex) {
                groupWidth -= (Integer) mShowAll.getParentLayout().getTag();
            }

            if (mNonRecipientBtns != null && mNonRecipientBtns.size() != 0) {
                for(NonRecipientBtn customizeActionButton : mNonRecipientBtns) {
                    if( customizeActionButton.getGroupIndex() == groupIndex) {
                        groupWidth -= (Integer) customizeActionButton.getParentLayout().getTag();
                    }
                }
            }

            if (groupWidth <= 0) continue;

            count++;
        }

        return count;
    }

    /**
     * A holder class that used to hold informations in recipient
     * change to protected for test case
     * @hide
     */
    protected class RecipientBtn {
        protected WeakReference<ComposeRecipientArea> mWeakComposeRecipientArea = null;

        protected int mBtnType = -1;

        protected int mGroupIndex = -1;

        protected int mChildIndex = -1;

        protected LinearLayout mParentLayout = null;

        protected HtcRecipientButton mHtcRecipientButton = null;

        protected ReceiverList mReceiver = null;

        protected boolean mBeginNextLine = false;

        protected ImageView mRightDivider = null;

        public RecipientBtn(WeakReference<ComposeRecipientArea> weakComposeRecipientArea, LinearLayout parent,
                  HtcRecipientButton htcRecipientButton, int btnType, ReceiverList receiver, boolean beginNextLine) {
            mWeakComposeRecipientArea = weakComposeRecipientArea;
            mParentLayout = parent;
            mHtcRecipientButton = htcRecipientButton;
            mBtnType = btnType;
            mReceiver = receiver;
            setWidthToParentTag();
            mBeginNextLine = beginNextLine;
        }

        private void setWidthToParentTag() {
            int width = getRecipientBtnWidth(RecipientBtn.this);

            mParentLayout.setTag(width);
        }

        protected ReceiverList getReceiverList() {
            return mReceiver;
        }

        protected int getBtnType() {
            return mBtnType;
        }

        protected void setGroupIndex(int groupIndex) {
            mGroupIndex = groupIndex;
        }

        protected int getGroupIndex() {
            return mGroupIndex;
        }

        protected void setReceiverList(ReceiverList receiver) {
            mReceiver = receiver;
        }

        protected void setChildIndex(int childIndex) {
            mChildIndex = childIndex;
        }

        protected int getChildIndex() {
            return mChildIndex;
        }

        protected LinearLayout getParentLayout() {
            return mParentLayout;
        }

        protected HtcRecipientButton getHtcRecipientButton() {
            return mHtcRecipientButton;
        }

        protected void setBeginNextLine(boolean beginNextLine) {
            mBeginNextLine = beginNextLine;
        }
        protected boolean isBeginNextLine() {
            return mBeginNextLine;
        }

        protected ImageView getRightDivider() {
            return mRightDivider;
        };
    }

    private void updateParentAndGroupWidth(RecipientBtn recipientBtn) {
        ComposeRecipientArea weakComposeRecipientArea = mWeakComposeRecipientArea.get();
        if (weakComposeRecipientArea == null){
            Log.d(TAG, "update parent and group width: weakComposeRecipientArea is null ");
            return;
        }

        int groupCount = weakComposeRecipientArea.getChildCount();

        for (int i = 0; i < groupCount; i++) {
            LinearLayout group = (LinearLayout) weakComposeRecipientArea.getChildAt(i);

            int groupIndex = mGroupIndexMap.get(getViewHashKey(group));

            if (recipientBtn.getGroupIndex() != groupIndex) continue;

            int groupLength = (Integer) group.getTag();

            LinearLayout parentLayout = recipientBtn.getParentLayout();
            int oldChildLength = (Integer) parentLayout.getTag();

            groupLength -= oldChildLength;

            int newChildLength = getRecipientBtnWidth(recipientBtn);
            parentLayout.setTag(newChildLength);

            groupLength += newChildLength;
            group.setTag(groupLength);

            break;
        }
    }

    /**
     * A holder class that used to hold informations in non recipient
     * change to protected for test case
     * @hide
     */
    protected class NonRecipientBtn extends RecipientBtn {
        private boolean mStatus = false;

        private String mTitleForStatusFalse = null;

        private String mTitleForStatusTrue = null;

        private boolean bNeedRightDivider = false;

        public NonRecipientBtn(WeakReference<ComposeRecipientArea> weakComposeRecipientArea, LinearLayout parent,
                HtcRecipientButton htcRecipientButton, int btnType, ReceiverList receiver, boolean status, String titleTrue,
                String titleFalse, boolean beginNextLine, boolean needRightDivider) {
            super(weakComposeRecipientArea, parent, htcRecipientButton, btnType, receiver, beginNextLine);
            mStatus = status;
            mTitleForStatusTrue = titleTrue;
            mTitleForStatusFalse = titleFalse;
            bNeedRightDivider = needRightDivider;

            setRecipientActionBarButtonStyle();

            setBtnText();

            setIndicator();

            setBtnWidth();

            addRightDivider();

            super.setWidthToParentTag();    // The width mMailRecipientButton of NonRecipient is assigned after setBtnText(),
                                            // So we need to setWidthToParentTag() again.
        }

        /*
         *  The padding of action button and recipient button are different.
         *
         *  Default recipient button:
         *
         *   +-----------------------+  <- ParentLayout
         *   |           ↑ M2 ↑      |
         *   +      +----------------+  <- HtcRecipientButton (ceiling)
         *   | ← M2 |( aaa@htc.com ) |
         *   +-----------------------+  <- HtcRecipientButton (floor)
         *
         *   Action bar style button:
         *   +------------------------+  <- HtcRecipientButton (ceiling)
         *   |         ↑ M1 ↑         |
         *   | ← M1  ( Edit )  M1   → |  <- increased touch area
         *   |         ↓ M1 ↓         |
         *   +------------------------+  <- HtcRecipientButton (floor)
         *
         *   Therefore, we remove parent padding and then set HtcRecipientButton padding to increase touch area.
         *   You can reference {@link HtcRecipientButton.setStye}
         */
        private void setRecipientActionBarButtonStyle() { // update into RecipientActionBar
            mParentLayout.setPadding(0, 0, 0, 0);
            mHtcRecipientButton.setStyle(HtcRecipientButton.ACTION_BAR_BUTTON_STYLE);
        }

        protected void setStatus(boolean status) {

            if (mStatus == status) return;

            mStatus = status;

            setBtnText();

            setIndicator();

            //setBtnWidth();

            addRightDivider();

            updateParentAndGroupWidth(NonRecipientBtn.this);
        }

        protected boolean getStatus() {
            return mStatus;
        }


        private void setBtnText() {
            if (mStatus) {
                mHtcRecipientButton.setText(mTitleForStatusTrue);
            } else {
                mHtcRecipientButton.setText(mTitleForStatusFalse);
            }
        }

        private void setIndicator() {
            // Indicator only shows up at ShowAll button
            if (mBtnType == BTN_TYPE_RECIPIENT || mBtnType == BTN_TYPE_CUSTOMIZE) return;

            mHtcRecipientButton.setIndicatorExpanded(mStatus);
        }

        private void setBtnWidth() {
            int width = (int) mHtcRecipientButton.getButtonWidth();

            int maxWidth = getRecipientBtnMaxWidth();

            if (width > maxWidth) width = maxWidth;

            mHtcRecipientButton.setWidth(width);
        }

        private void addRightDivider() {
           // showAll button doesn't have RightDivider.
           if (mBtnType == BTN_TYPE_RECIPIENT || mBtnType == BTN_TYPE_SHOW_ALL) return;
           if (mBtnType == BTN_TYPE_CUSTOMIZE) {
               if (!bNeedRightDivider && (!mNeedAddShowAll && getRecipientLines() <= sShowAllPreviewLinesNum)) return;
           }

           if(mRightDivider == null) {
               mRightDivider = createRightDivider();
               mParentLayout.addView(mRightDivider);
           }
        }

        private int getRecipientBtnMaxWidth() {
            ComposeRecipientArea composeRecipientArea = (ComposeRecipientArea) mWeakComposeRecipientArea.get();
            if (composeRecipientArea == null){
                Log.d(TAG, "get recipient button max width: composeRecipientArea is null ");
                return Integer.MIN_VALUE;
            }

            int areaMaxWidth = composeRecipientArea.getRecipientContainerMaxWidth();

            if (mRightDivider != null && mRightDivider.getBackground() != null) {
                int dividerWidth = mRightDivider.getBackground().getIntrinsicWidth() +
                        ((LinearLayout.LayoutParams) mRightDivider.getLayoutParams()).leftMargin +
                        ((LinearLayout.LayoutParams) mRightDivider.getLayoutParams()).rightMargin;
                areaMaxWidth = areaMaxWidth - dividerWidth;
            }
            return areaMaxWidth;
        }

        private ImageView createRightDivider() {
            ImageView iv = new ImageView(mContext);
            iv.setBackgroundResource(R.drawable.common_list_divider);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(0, ResUtils.getDimenMarginM1(mContext), 0, ResUtils.getDimenMarginM1(mContext));
            iv.setLayoutParams(params);
            return iv;
        }
    }

    private String getViewHashKey(View view) {
        return view.getClass().getName() + '@' + Integer.toHexString(view.hashCode());
    }

    private OnClickListener mShowAllListener = new OnClickListener() {
        public void onClick(View v) {
            mShowAllStatus = !mShowAllStatus;
            updateShowAllLayouts(mShowAllStatus);
            mHasHideAllButton = mShowAllStatus;
        }
    };

    public void updateContactDataInfo(ArrayList<ReceiverList> receivers) {
        if (receivers == null || receivers.size() <= 0){
            Log.d(TAG, "update contact data info: receivers is null or size<=0");
            return;
        }

        for (ReceiverList receiver : receivers) {
            if (receiver == null) return;

            RecipientBtn recipientBtn = getRecipientBtnByReceiverList(receiver);

            if (recipientBtn == null) continue;

            recipientBtn.setReceiverList(receiver);

            HtcRecipientButton htcPhotoBtn = recipientBtn.getHtcRecipientButton();

            if (receiver.contactId == -1) {
                if (receiver.name != null && !receiver.name.isEmpty()) {
                    htcPhotoBtn.setText(receiver.name);
                } else {
                    htcPhotoBtn.setText(receiver.addr);
                }
            } else {
                htcPhotoBtn.setText(receiver.name);
            }
        }
    }

    public void modifyCustomizedActionButton(int index, String text, OnClickListener listener, boolean needShowInNexLine) {
        if(mCustomizeActionButtonList != null && index < mCustomizeActionButtonList.size()) {
            CustomizedActionButton button = mCustomizeActionButtonList.get(index);
            if(button != null) {
                if(!TextUtils.isEmpty(text)) {
                    button.setButtonText(text);
                }

                if(listener != null) {
                    button.setButtonClickListener(listener);
                }

                button.setNeedShowInNextLine(needShowInNexLine);
                removeAllRecipientActionBarBtn();
                addAllRecipientActionBarBtn();
            }
        }
    }

    /********************************************************************************************
     * Recipient Menu Options Block
     ********************************************************************************************/
    private class showRecipientDialogClickListener implements OnClickListener {

        private WeakReference<ComposeRecipientArea> mWeakComposeRecipientArea;

        public showRecipientDialogClickListener(WeakReference<ComposeRecipientArea> weakComposeRecipientArea) {
            mWeakComposeRecipientArea = weakComposeRecipientArea;
        }

        public void onClick(View view) {

            RecipientBtn recipientBtn = (RecipientBtn)view.getTag();

            if (recipientBtn == null) {
                Log.d(TAG, "click on recipient dialog: recipientBtn is null ");
                return;
            }

            ComposeRecipientArea composeRecipientArea =(ComposeRecipientArea) mWeakComposeRecipientArea.get();
            composeRecipientArea.onReceiverButtonClick(recipientBtn.getReceiverList());
        }
    }
}
