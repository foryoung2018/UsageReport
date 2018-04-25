
package com.htc.lib1.cc.recipientblock.activityhelper;

import java.util.ArrayList;

import android.content.DialogInterface;
import android.content.res.Configuration; // hTC, add
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ScrollView;
import com.htc.lib1.cc.test.R;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.lib1.cc.widget.HtcAutoCompleteTextView;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.HtcImageButton;
import com.htc.lib1.cc.widget.HtcRimButton;
import com.htc.lib1.cc.widget.recipientblock.ComposeRecipientArea;
import com.htc.lib1.cc.widget.recipientblock.ComposeRecipientArea.ComposeRecipientCallBack;
import com.htc.lib1.cc.widget.recipientblock.HtcRecipientButton;
import com.htc.lib1.cc.widget.recipientblock.ReceiverList;
import com.htc.lib1.cc.widget.recipientblock.RecipientBlock;

public class RecipientBlockMockActivity extends ActivityBase {
    boolean mInitByXML = false;
    boolean mMessageStyle = false;
    boolean isFooterExist = false;
    private RecipientBlock mRecipientBlock = null;
    private ComposeRecipientArea mComposeRecipientArea = null;
    private HtcAutoCompleteTextView mCustomizedInputField = null;// for Message style
    private HtcAutoCompleteTextView mRecipientInputField = null;
    private HtcImageButton mPickerButton = null;
    private final String ACCESSIBILITY_CONTENT_DESCRIPTION = "Should be set by AP";

    final static public String[] nameList = new String[] {
            "Franklin_Delano_Roosevelt", "Charles_de_Gaulle", "Mahatma_Gandhi", "Susan_Brownmiller", "Mikhail_Gorbachev"
            , "Haile_Selassie", "Charles_Lindbergh", "Susan_Brownmiller", "Nikita_Khrushchev", "Mark_Zuckerberg"
            , "Aaliyah", "Abbie", "Abby", "Abigail", "Aimee", "Alexandra", "Alice", "Alicia", "Alisha", "Amber", "Amelia", "Amy"
            , "Anna", "Bethany", "Brooke", "Caitlin", "Cerys", "Charlie", "Charlotte", "Chelsea", "Chloe", "Courtney", "Daisy"
            , "Danielle", "Eleanor", "Elizabeth", "Ella", "Ellie", "Eloise", "Emily", "Emma", "Erin", "Eve", "Evie", "Francesca"
            , "Freya", "Georgia", "Georgina", "Grace", "Hannah", "Harriet", "Hollie", "Holly", "Imogen", "Isabel", "Isabella"
            , "Isabelle", "Isobel", "Jade", "Jasmine", "Jennifer", "Jessica", "Jodie", "Kate", "Katherine", "Katie", "Kayleigh"
            , "Lara", "Laura", "Lauren", "Leah", "Libby", "Lily", "Louise", "Lucy", "Lydia", "Madeleine", "Madison", "Maisie", "Megan"
            , "Melissa", "Mia", "Millie", "Mollie", "Molly", "Morgan", "Naomi", "Natasha", "Niamh", "Nicole", "Olivia", "Paige"
            , "Phoebe", "Poppy", "Rachel", "Rebecca", "Rosie", "Ruby", "Samantha", "Sarah", "Shannon", "Sophia", "Sophie", "Summer "
            , "Tegan", "Tia", "Victoria", "Yasmin", "Zara", "Zoe"
    };

    final static public int recipientBlockID = 510; // for test

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HtcCommonUtil.initTheme(this, mCategoryId);
        setContentView(R.layout.recipient_block_demo);

        if (!mInitByXML) {
            init();
        } else {
            initXML();
        }

    }

    // for java init demo
    private void init() {
        ScrollView sv = (ScrollView) findViewById(R.id.scroll_view);
        if (sv != null) sv.removeAllViews();

        String label = mMessageStyle ? "" : "Receiver";
        int customizedInputFieldVisibility = mMessageStyle ? View.VISIBLE : View.GONE;

        // (1) Initialize Recipient block here
        mRecipientBlock = new RecipientBlock(this);
        mRecipientBlock.setup(this, label, isFooterExist, !mMessageStyle);
        mRecipientBlock.setId(recipientBlockID);
        // Log.i("DebugRB","[app] recipientBlockID = "+recipientBlockID);
        // (1) Initialize input field
        // message input field
        mCustomizedInputField = (HtcAutoCompleteTextView) findViewById(R.id.customizedInputField);
        mCustomizedInputField.setOnKeyListener(toKeyEvent);
        mCustomizedInputField.setVisibility(customizedInputFieldVisibility);

        // recipient input field
        mRecipientInputField = mRecipientBlock.getInputTextView();
        mRecipientInputField.setOnKeyListener(toKeyEvent);

        // (3) get image button from recipient block to set up OnClickListener
        mPickerButton = mRecipientBlock.getPickerButton();
        mPickerButton.setOnClickListener(mPickerListener);
        mPickerButton.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

        // (4) get compose recipient area from recipient block to add customized action button
        mComposeRecipientArea = mRecipientBlock.getComposeRecipientArea();
        if (mComposeRecipientArea != null) {
            if (!mMessageStyle) {
                mComposeRecipientArea.addActionButton("Edit", true, null);
                mComposeRecipientArea.addActionButton("Show Cc/Bcc", false, mCCListener);
            }
            // Add call back for Recipient Block
            mComposeRecipientArea.setComposeRecipientCallBack(mComposeRecipientCallBack);
        }

        sv.addView(mRecipientBlock);
    }

    // for xml init demo
    private void initXML() {

        String label = mMessageStyle ? "" : "Receiver";
        int customizedInputFieldVisibility = mMessageStyle ? View.VISIBLE : View.GONE;

        mRecipientBlock = (RecipientBlock) findViewById(R.id.recipient_block);
        mRecipientBlock.setup(this, label, isFooterExist, !mMessageStyle);
        mRecipientBlock.setId(recipientBlockID);

        // (1) Initialize input field
        // message input field
        mCustomizedInputField = (HtcAutoCompleteTextView) findViewById(R.id.customizedInputField);
        mCustomizedInputField.setOnKeyListener(toKeyEvent);
        mCustomizedInputField.setVisibility(customizedInputFieldVisibility);

        // recipient input field
        mRecipientInputField = mRecipientBlock.getInputTextView();
        mRecipientInputField.setOnKeyListener(toKeyEvent);

        // (3) get image button from recipient block to set up OnClickListener
        mPickerButton = mRecipientBlock.getPickerButton();
        mPickerButton.setOnClickListener(mPickerListener);

        // (4) get compose recipient area from recipient block to add customized action button
        mComposeRecipientArea = mRecipientBlock.getComposeRecipientArea();
        if (mComposeRecipientArea != null) {
            if (!mMessageStyle) {
                mComposeRecipientArea.addActionButton("Edit", true, null);
                mComposeRecipientArea.addActionButton("Show Cc/Bcc", false, mCCListener);
            }
            // Add call back for Recipient Block
            mComposeRecipientArea.setComposeRecipientCallBack(mComposeRecipientCallBack);
        }

    }

    private void removeAllRecipient() {
        if (mComposeRecipientArea != null) mComposeRecipientArea.updateNewRecipients(null);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    public void setContentToRecipientBlock(String multiAddr) {
        if (null == multiAddr || multiAddr.length() == 0) {
            return;
        }

        // Add single receiver to Recipient Block
        ReceiverList newReceiver = new ReceiverList(System.currentTimeMillis(), null, multiAddr, -1, -1);
        if (mComposeRecipientArea != null) {
            mComposeRecipientArea.addSingleRecipientByReceiverList(newReceiver, false);
        }

    }

    private OnKeyListener toKeyEvent = new OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            HtcAutoCompleteTextView edit = (HtcAutoCompleteTextView) v;

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        setContentToRecipientBlock(edit.getText().toString());
                        edit.setPressed(false);
                        edit.setText("");

                        Log.i("DebugRB", "listCount = " + mComposeRecipientArea.getReceivers().size());
                        return true;
                }
            }
            // fix the IME backspace (KEYCODE_BUTTON_R1) malfunction problem.
            return false;

        }
    };

    private OnClickListener mPickerListener = new OnClickListener() {
        public void onClick(View v) {

            ArrayList<ReceiverList> receiverList = new ArrayList<ReceiverList>();
            for (int i = 0; i < 100; i++) {

                ReceiverList newReceiver = new ReceiverList(System.currentTimeMillis(), i + ":" + nameList[i % 100] + "@htc.com", "", -1, -1);
                receiverList.add(newReceiver);
            }

            // Add several receivers to Recipient Block
            if (mComposeRecipientArea != null) {
                mComposeRecipientArea.addMultipleRecipientsByReceiverLists(receiverList, false);
            }

        }

    };

    private boolean mStatus = false;
    private OnClickListener mCCListener = new OnClickListener() {
        public void onClick(View v) {
            HtcRimButton btn = (HtcRimButton) ((HtcRecipientButton) v).getButton();
            mStatus = !mStatus;
            if (mStatus) {
                btn.setText("Hide Cc/Bcc");
            } else {
                btn.setText("Show Cc/BcC");
            }
        }
    };

    private ComposeRecipientCallBack mComposeRecipientCallBack = new ComposeRecipientCallBack() {

        // Implement the function for when user click the receiver button
        public void onReceiverButtonClick(final ComposeRecipientArea composeRecipientArea, final ReceiverList receiver) {

            if (isFinishing()) {
                return;
            }

            if (receiver == null) return;

            HtcAlertDialog.Builder builder = new HtcAlertDialog.Builder(RecipientBlockMockActivity.this);

            if (receiver.name != null) {
                builder.setTitle("" + receiver.name + "\n" + receiver.addr);
            } else {
                builder.setTitle(receiver.addr);
            }

            CharSequence[] options = getRecipientMenuOptions(receiver);

            builder.setItems(options, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            composeRecipientArea.removeSingleRecipientByReceiverList(receiver, true);
                            break;
                    }
                }

            });
            builder.show();
        }

        // Implement the function for after adding single receiver to Recipient Block, like mail and
        // calendar will increae the frequency of the receiver
        public void afterAddSingleReceiver(ComposeRecipientArea composeRecipientArea, ReceiverList receiver, boolean plusFrequency) {

        }

        // Implement the function for after adding several receivers to Recipient Block, like mail
        // and calendar will increae the frequency of the receiver
        public void afterAddMultipleReceivers(ComposeRecipientArea composeRecipientArea, ArrayList<ReceiverList> receivers, boolean plusFrequency) {

        }

        // Implement the function for after removing single receiver to Recipient Block, like mail
        // and calendar will increae the frequency of the receiver
        public void afterRemoveSingleReceiver(ComposeRecipientArea composeRecipientArea, ReceiverList receiver, boolean minusFrequency) {
        }
    };

    private CharSequence[] getRecipientMenuOptions(ReceiverList receiver) {
        if (receiver == null) return null;

        CharSequence[] options = null;

        options = new String[1];
        options[0] = "remove";
        return options;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipient_block_demo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_message:
                mMessageStyle = true;
                init();
                return true;

            case R.id.menu_task:
                mMessageStyle = false;
                init();
                return true;

            case R.id.menu_remove_all:
                removeAllRecipient();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
