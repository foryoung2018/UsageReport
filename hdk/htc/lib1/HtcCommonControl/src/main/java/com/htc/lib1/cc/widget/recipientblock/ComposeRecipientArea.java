package com.htc.lib1.cc.widget.recipientblock;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.widget.LinearLayout;

import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.WindowUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/********************************************************************************************
 * Here is only describing logic of add/remove recipients, hide cc/bcc, show all, edit buttons
 * For operation part, please see {@link ComposeRecipientHelper}
 *
 ********************************************************************************************/
public class ComposeRecipientArea extends LinearLayout {
    private static final String TAG = "ComposeRecipientArea";
    private int mScreenHeightDp;
    private int mScreenWidthtDp;
    private boolean mIsPortMode = true;
    private boolean bIsInputFieldVisible = false;
    private boolean bIsLabelVisible = false;
    private boolean mIsAreaVisibilityChanged = false;
    private WeakReference<Activity> mWeakComposeActivity;

    @ExportedProperty(category = "CommonControl")
    private int mRecipientContainerMaxWidth = 0;
    @ExportedProperty(category = "CommonControl")
    private int mRecipientWidthMin = 0;
    @ExportedProperty(category = "CommonControl")
    private int mRecipientWidthMax = 0;
    @ExportedProperty(category = "CommonControl")
    private int mRecipientHeight = 0;
    /*@hide*/
    protected int getRecipientHeight(){
        return mRecipientHeight;
    }

    public static final int BTN_TYPE_RECIPIENT      = 100;
    public static final int BTN_TYPE_CUSTOMIZE    = 200;
    public static final int BTN_TYPE_SHOW_ALL       = 300;

    private ComposeRecipientHelper mComposeRecipientHelper;
    private ComposeRecipientCallBack mComposeRecipientCallBack = null;

    public ComposeRecipientArea(Context context) {
        super(context);
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);
    }

    public ComposeRecipientArea(Context context, AttributeSet attrs) {
        super(context, attrs);
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);
    }

    public ComposeRecipientArea(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);
    }

    private void setConfigMode() {
        mScreenHeightDp = getContext().getResources().getConfiguration().screenHeightDp;
        mScreenWidthtDp = getContext().getResources().getConfiguration().screenWidthDp;

        if (WindowUtil.isSuitableForLandscape(getContext().getResources())) { // landscape
            mIsPortMode = false;
            mRecipientContainerMaxWidth = (int) getGroupWidth() - ResUtils.getRecipientContainerPadding(getContext());
        } else { // portrait
            mIsPortMode = true;
            mRecipientContainerMaxWidth = (int) getGroupWidth() - ResUtils.getRecipientContainerPadding(getContext());
        }
    }

    /*@hide*/
    protected int getRecipientContainerMaxWidth() {
        return mRecipientContainerMaxWidth;
    }

    /**
     * setup the type of ComposeRecipientArea
     * @param weakComposeActivity reference activity.
     * @param isFooterExist  not use anymore.
     * @param isInputFieldVisible visibility of input field.
     * @param isLabelVisible visibility of the Label.
     */
    /*@hide*/
    protected void setup(WeakReference<Activity> weakComposeActivity, boolean isFooterExist, boolean isInputFieldVisible, boolean isLabelVisible) {
        mWeakComposeActivity = weakComposeActivity;
        mComposeRecipientHelper = new ComposeRecipientHelper(getContext(), mWeakComposeActivity, this);
        bIsInputFieldVisible = isInputFieldVisible;
        bIsLabelVisible = isLabelVisible;
        setConfigMode(); // moved from init for correct footer info
    }

    /**
     * Set show all preview line number
     * @param lineNum show all preview line number
    **/
    public void setShowAllPreviewLinesNum(int lineNum){
        if(mComposeRecipientHelper!=null)
        mComposeRecipientHelper.setShowAllPreviewLinesNum(lineNum);
    }

    /**
     * Get show all preview line number
     * @return show all preview line number
    **/
    public int getShowAllPreviewLinesNum(){
        if(mComposeRecipientHelper==null) return 0;
        return  mComposeRecipientHelper.getShowAllPreviewLinesNum();
    }

    private void setStatusByType(int btnType, boolean status) {
        if (mComposeRecipientHelper == null){
            Log.d(TAG, "set status by type: mComposeRecipientHelper is null ");
            return;
        }
        if (status == mComposeRecipientHelper.getStatusByType(btnType)) return;

        mComposeRecipientHelper.setStatusByType(btnType, status);
    }

    /**
     * Add customize action buttons
     * @param buttonText button's text
     * @param needShowInNextLine need to show the button in next line
     * @param listener buttons's OnClickListener
    **/
    public void addActionButton(String buttonText, boolean needShowInNextLine, OnClickListener listener) {
        mComposeRecipientHelper.addNewActionButton(buttonText, needShowInNextLine, listener);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        if ((mScreenHeightDp == getContext().getResources().getConfiguration().screenHeightDp) && (mScreenWidthtDp == getContext().getResources().getConfiguration().screenWidthDp)) return;
        setConfigMode();

        if (mComposeRecipientHelper != null) {
            mComposeRecipientHelper.onConfigurationChanged();
        }
    }

    /**
     * Change to a new receiver list
     * @param newReceivers new receivers list
    **/
    public void updateNewRecipients(ArrayList<ReceiverList> newReceivers) {
        if (mComposeRecipientHelper == null){
            Log.d(TAG, "update new recipients: mComposeRecipientHelper is null ");
            return;
        }

        mComposeRecipientHelper.relayoutAllFromNewReceivers(newReceivers);
        mComposeRecipientCallBack.afterAddMultipleReceivers(this, newReceivers, true);
        setComposeRecipientAreaVisibility();//To correct the bottom padding when updating null receivers
    }

    /********************************************************************************************
     *  Caculate MailRecipientButton Width Blocks
     ********************************************************************************************/
    private int getGroupWidth() {
        // phone width
        return getContext().getResources().getDisplayMetrics().widthPixels;
    }

    /*@hide*/
    protected int getBtnWidth(HtcRecipientButton btn) {
        int width = (int)btn.getButtonWidth();

        /*
         * Define mRecipientWidthMax:
         *
         * Portrait
         * +--------------------------------------------------------+
         * |      +-----------------+      +-----------------+      |
         * | ←M1→ | ( aaa@htc.com ) | ←M2→ | ( aaa@htc.com ) | ←M1→ |
         * |      +-----------------+      +-----------------+      |
         * +--------------------------------------------------------+
         *
         * Landscape
         * +---------------------------------------------------------------------------------+
         * |      +-----------------+      +-----------------+      +-----------------+      |
         * | ←M1→ | ( aaa@htc.com ) | ←M2→ | ( aaa@htc.com ) | ←M2→ | ( aaa@htc.com ) | ←M1→ |
         * |      +-----------------+      +-----------------+      +-----------------+      |
         * +---------------------------------------------------------------------------------+
         */
        if (mIsPortMode) {
            mRecipientWidthMax = (int) (getGroupWidth() - 2 * ResUtils.getRecipientContainerPadding(getContext()) - 1 * ResUtils.getDimenMarginM2(getContext())) / 2;
        } else {
            mRecipientWidthMax = (int) (getGroupWidth() - 2 * ResUtils.getRecipientContainerPadding(getContext()) - 2 * ResUtils.getDimenMarginM2(getContext()));
            mRecipientWidthMax = mRecipientWidthMax/ 3 ;
        }

        // define mRecipientWidthMin
        if (mRecipientWidthMin == 0 || mRecipientHeight == 0) {
            // design limit, the min length of MailRecipientButton is the length of string “1…” + the width of common_b_expand + margin_s
            HtcRecipientButton btn2 = new HtcRecipientButton(getContext());
            btn2.setText("WW");
            mRecipientWidthMin = (int) btn2.getButtonWidth() + 2 * ResUtils.getDimenMarginM2(getContext());
            mRecipientHeight = (int) btn2.getButtonHeight(mRecipientWidthMin);
        }

        // dynamic change button size
        if (width > mRecipientWidthMax) width = mRecipientWidthMax;
        if (width < mRecipientWidthMin) width = mRecipientWidthMin;

        return width;
    }

    /**
     * Add single receiver to Recipient Block
     * @param receiver ReceiverList
     * @param plusFrequency is the receiver need to plus frequency
    **/
    public void addSingleRecipientByReceiverList(ReceiverList receiver, boolean plusFrequency) {
        if (receiver == null){
            Log.d(TAG, "add single recipient: receiver is null ");
            return;
        }

        if (mWeakComposeActivity == null) {
            Log.d(TAG, "add single recipient: mWeakComposeActivity is null ");
            return;
        }

        Activity activity = mWeakComposeActivity.get();

        if(activity == null) {
            Log.d(TAG, "add single recipient: activity is null");
            return;
        }

        if(activity.isFinishing()){
            Log.d(TAG, "add single recipient: activity is finishing ");
            return;
        }

        mComposeRecipientHelper.removeAllRecipientActionBarBtn();

        mComposeRecipientHelper.addSingleReceiver(receiver);

        mComposeRecipientHelper.addAllRecipientActionBarBtn();

        mComposeRecipientCallBack.afterAddSingleReceiver(this, receiver, plusFrequency);

        setComposeRecipientAreaVisibility();

    }

    /**
     * Add multiple receivers to Recipient Block
     * @param receivers ReceiverList
     * @param plusFrequency is the receiver need to plus frequency
    **/
    public void addMultipleRecipientsByReceiverLists(final ArrayList<ReceiverList> receivers, boolean plusFrequency) {

        if (mComposeRecipientHelper == null){
            Log.d(TAG, "add multiple recipients: mComposeRecipientHelper is null");
            return;
        }

        if (receivers == null || receivers.size() <= 0){
            Log.d(TAG, "add multiple recipients: receivers is null or size<=0");
            return;
        }

        if (mWeakComposeActivity == null) {
            Log.d(TAG, "add multiple recipients: mWeakComposeActivity is null");
            return;
        }

        Activity activity = mWeakComposeActivity.get();

        if(activity == null) {
            Log.d(TAG, "add multiple recipients: activity is null");
            return;
        }

        if(activity.isFinishing()){
            Log.d(TAG, "add multiple recipients: activity is finishing ");
            return;
        }

        mComposeRecipientHelper.removeAllRecipientActionBarBtn();

        mComposeRecipientHelper.addMultipleReceivers(receivers);

        mComposeRecipientHelper.addAllRecipientActionBarBtn();

        mComposeRecipientCallBack.afterAddMultipleReceivers(this, receivers, plusFrequency);

        setComposeRecipientAreaVisibility();

    }

//    /********************************************************************************************
//     * Remove Recipient Buttons Block
//     ********************************************************************************************/
    /**
     * Remove single receiver from Recipient Block
     * @param receiver receiver to be removed
     * @param minusFrequency is the receiver need to minus frequency
    **/
    public void removeSingleRecipientByReceiverList(ReceiverList receiver, boolean minusFrequency) {

        if (mComposeRecipientHelper == null || mComposeRecipientHelper.getReceiverLists().size() <= 0){
            Log.d(TAG, "remove single recipient: mComposeRecipientHelper is null or receiver lists size <= 0");
            return;
        }

        if (receiver == null){
            Log.d(TAG, "remove single recipient: receiver is null ");
            return;
        }

        if (mWeakComposeActivity == null) {
            Log.d(TAG, "remove single recipient: mWeakComposeActivity is null ");
            return;
        }

        Activity activity = mWeakComposeActivity.get();

        if(activity == null) {
            Log.d(TAG, "remove single recipient: activity is null");
            return;
        }

        if(activity.isFinishing()){
            Log.d(TAG, "remove single recipient: activity is finishing ");
            return;
        }

        mComposeRecipientHelper.removeSingleReceiver(receiver);

        mComposeRecipientHelper.relayoutAllUI();

        mComposeRecipientCallBack.afterRemoveSingleReceiver(this, receiver, minusFrequency);

        setComposeRecipientAreaVisibility();
    }

    /*
     *  case 1: Input field exist, no receiver button : set top and bottom margin M2 to RecipientBlock.
     *  case 2: Input field exist, receiver button exist: Only set top margin M2 to RecipientBlock.
     *  case 3: No input field, no receiver button : keep previous layout.
     *  case 4: No input field, receiver button exist : keep previous layout.
     */
    private void setComposeRecipientAreaVisibility() {
        LinearLayout recipientBlock = (LinearLayout) getParent();
        if (!mIsAreaVisibilityChanged && hasRecipientButtons()
                || (mIsAreaVisibilityChanged && getVisibility() == View.VISIBLE)) {
            if (getVisibility() != View.VISIBLE) setVisibility(View.VISIBLE);
            if(recipientBlock != null) {
                if(bIsLabelVisible){
                    /*
                     * Label exist: set top margin M2 to RecipientBlock.
                     *
                     * +--------------------------------------------------------+
                     * |                        ↑ M2 ↑                          |
                     * +--------------------------------------------------------+
                     * | ←M2→ label                                        ←M2→ |
                     * |     ↓ M2 ↓                                             |
                     * +------+-------------------------------------+--+-----+--+
                     * | ←M2→ | input field...                      |  | img |  |
                     * +------+-------------------------------------+--+-----+--+
                     * |               (compose recipient area)                 |
                     * +--------------------------------------------------------+
                     */
                    recipientBlock.setPadding(0, ResUtils.getDimenMarginM2(getContext()), 0, 0);
                }else
                if(bIsInputFieldVisible) {
                    /*
                     * No label: set top margin M1 to RecipientBlock.
                     *
                     * +--------------------------------------------------------+
                     * |                        ↑ M1 ↑                          |
                     * +------+-------------------------------------+--+-----+--+
                     * | ←M2→ | input field...                      |  | img |  |
                     * +------+-------------------------------------+--+-----+--+
                     * |               (compose recipient area)                 |
                     * +--------------------------------------------------------+
                     */
                    recipientBlock.setPadding(0, ResUtils.getDimenMarginM1(getContext()), 0, 0);
                }
            }
        } else {

            if (getVisibility() != View.GONE) setVisibility(View.GONE);
            if(recipientBlock != null) {
                if(bIsLabelVisible){
                    /*
                     * Only input field and label exist: set top margin M2 and bottom margin M1 to RecipientBlock.
                     *
                     * +--------------------------------------------------------+
                     * |                        ↑ M2 ↑                          |
                     * +--------------------------------------------------------+
                     * | ←M1→ label                                        ←M1→ |
                     * |     ↓ M2 ↓                                             |
                     * +------+-------------------------------------+--+-----+--+
                     * | ←M1→ | input field...                      |  | img |  |
                     * +------+-------------------------------------+--+-----+--+
                     * |                        ↓ M1 ↓                          |
                     * +--------------------------------------------------------+
                     */
                    recipientBlock.setPadding(0, ResUtils.getDimenMarginM2(getContext()), 0, ResUtils.getDimenMarginM1(getContext()));
                }else
                if(bIsInputFieldVisible) {
                    /*
                     * Only input field exist: set top and bottom margin M1 to RecipientBlock.
                     *
                     * +--------------------------------------------------------+
                     * |                        ↑ M1 ↑                          |
                     * +------+-------------------------------------+--+-----+--+
                     * | ←M1→ | input field...                      |  | img |  |
                     * +------+-------------------------------------+--+-----+--+
                     * |                        ↓ M1 ↓                          |
                     * +--------------------------------------------------------+
                     */
                    recipientBlock.setPadding(0, ResUtils.getDimenMarginM1(getContext()), 0, ResUtils.getDimenMarginM1(getContext()));
                }
            }
        }
    }
    /*@hide*/
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
      super.onVisibilityChanged(changedView, visibility);
      if(getParent()!=null){
          if(hasRecipientButtons()){
              mIsAreaVisibilityChanged = true;
              setComposeRecipientAreaVisibility();
              mIsAreaVisibilityChanged = false;
          }
      }
    }

    private boolean hasRecipientButtons(){
        return mComposeRecipientHelper != null && mComposeRecipientHelper.getReceiverLists() != null
                && mComposeRecipientHelper.getReceiverLists().size() > 0;
    }

    /**
     * Get receivers list from Recipient Block
     * @return the receiver list from Recipient Block
    **/
    public ArrayList<ReceiverList> getReceivers() {
        if (mComposeRecipientHelper == null){
            Log.d(TAG, "get receivers: mComposeRecipientHelper is null");
            return null;
        }
        return mComposeRecipientHelper.getReceiverLists();
    }

    /**
     * For test case only.
     * Get specific recipient button of the compose recipient area
     * @return recipient button
     * @hide
    **/
    HtcRecipientButton getRecipinetButton(int index){

        if(getReceivers()!=null && getReceivers().size() > index){
            ReceiverList receiver = getReceivers().get(index);
            if(mComposeRecipientHelper!=null && mComposeRecipientHelper.getRecipientBtnByReceiverList(receiver)!=null)
                return mComposeRecipientHelper.getRecipientBtnByReceiverList(receiver).getHtcRecipientButton();
        }

        return null;
    }

    /**
     * For test case only
     * Get specific action button of the compose recipient area
     * @return action button
     * @hide
    **/
    public HtcRecipientButton getActionButton(int index){

        if(mComposeRecipientHelper!=null && mComposeRecipientHelper.getActionButtonLists()!= null
                && mComposeRecipientHelper.getActionButtonLists().size()>index)
            return    mComposeRecipientHelper.getActionButtonLists().get(index).getHtcRecipientButton();

        return null;
    }

    /**
     * Update receivers' data in Recipient Block, use in update current exist recipient's information
     * @param receivers new receivers' data
    **/
    public void updateContactDataInfo(ArrayList<ReceiverList> receivers) {
        if (mComposeRecipientHelper == null){
            Log.d(TAG, "update contact data info: mComposeRecipientHelper is null");
            return;
        }
        mComposeRecipientHelper.updateContactDataInfo(receivers);
    }

    /**
     * Relayout all UI
    **/
    public void relayoutAllUI() {
        if (mComposeRecipientHelper == null){
            Log.d(TAG, "relayout all UI: mComposeRecipientHelper is null");
            return;
        }
        mComposeRecipientHelper.relayoutAllUI();
    }

    public void onReceiverButtonClick(ReceiverList recevier) {
        mComposeRecipientCallBack.onReceiverButtonClick(this, recevier);
    }

    /**
     * Set ComposeRecipientCallBack
     * @param composeRecipientCallBack
    **/
    public void setComposeRecipientCallBack(ComposeRecipientCallBack  composeRecipientCallBack) {
        mComposeRecipientCallBack = composeRecipientCallBack;
    }

    /**
     * Change the state to show all or hide
     * @param showAll state of showall button
    **/
    public void updateShowAllVisibility(boolean showAll) {
        if (mComposeRecipientHelper == null){
            Log.d(TAG, "update show all visibility: mComposeRecipientHelper is null");
            return;
        }
        mComposeRecipientHelper.updateShowAllLayouts(showAll);
    }

    /**
     * modify the customize action button
     * @param index The index of customized action button
     * @param text The modified text
     * @param listener The modified OnClickListener
     * @param needShowInNexLine The button need to show in next line or not
    **/
    public void modifyCustomizedActionButton(int index, String text, OnClickListener listener, boolean needShowInNexLine) {
        if (mComposeRecipientHelper == null){
            Log.d(TAG, "modify customized action button: mComposeRecipientHelper is null");
            return ;
        }
        mComposeRecipientHelper.modifyCustomizedActionButton(index, text, listener, needShowInNexLine);
    }

    /**
     * Interface definition for a callback in Recipient Block.
     */
    public interface ComposeRecipientCallBack {
        /**
         * for after clicking receiver button in composeRecipientArea
         * @param composeRecipientArea the composeRecipientArea which receiver button is clicked
         * @param recevier the recevier of clicked button
         * */
        public void onReceiverButtonClick(final ComposeRecipientArea composeRecipientArea, ReceiverList recevier);

        /**
         * for after adding a single receiver, ex: add frequency for receiver
         * @param composeRecipientArea the composeRecipientArea which added a receiver button
         * @param recevier the added recevier
         * @param plusFrequency is need to plusFrequency
         */
        public void afterAddSingleReceiver(ComposeRecipientArea composeRecipientArea, ReceiverList receiver, boolean plusFrequency);

        /**
         * for after adding several receivers, ex: add frequency for receivers
         * @param composeRecipientArea the composeRecipientArea which added receiver buttons
         * @param receivers the added receviers
         * @param plusFrequency is need to plusFrequency
         */
        public void afterAddMultipleReceivers(ComposeRecipientArea composeRecipientArea, ArrayList<ReceiverList> receivers, boolean plusFrequency);

        /**
         * for after removing single receiver, ex: minus frequency for receivers
         * @param composeRecipientArea the composeRecipientArea which removed receiver button
         * @param recevier the removed recevier.
         * @param minusFrequency is need to minusFrequency
         */
        public void afterRemoveSingleReceiver(ComposeRecipientArea composeRecipientArea, ReceiverList receiver, boolean minusFrequency);

    }

}

