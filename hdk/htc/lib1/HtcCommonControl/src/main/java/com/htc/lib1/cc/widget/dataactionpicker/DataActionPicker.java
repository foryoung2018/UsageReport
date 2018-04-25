package com.htc.lib1.cc.widget.dataactionpicker;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents.Insert;
import android.provider.ContactsContract.PhoneLookup;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.htc.lib1.cc.R;
//import com.android.internal.telephony.CallerInfo;
import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.lib1.cc.widget.HtcImageButton;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.widget.IHtcAbsListView;

/**
 * This common module is to support have consistent parsed result when phone number, email address, URL and address show in text view and Webview.
 * There will be for common module dialogs :
 *
 * <pre>
 * 1. Email dialog
 * +-----------------------------+
 * |        email address        |
 * +-----------------------------+
 * | Send mail                   |
 * | Sense message               |
 * | Save to People              |
 * | Save to existing contact    |
 * | Copy                        |
 * +-----------------------------+
 *
 * 2. Phone number dialog
 * +-----------------------------+
 * |       phone number          |
 * +-----------------------------+
 * | Call                        |
 * | Edit number before calling  |
 * | Send message                |
 * | Save to People              |
 * | Save to existing contact    |
 * | Copy                        |
 * +-----------------------------+
 *
 * 3. URL dialog
 * +-----------------------------+
 * |            URL              |
 * +-----------------------------+
 * | Open                        |
 * | Copy                        |
 * +-----------------------------+
 *
 * 4. Address dialog
 * +-----------------------------+
 * |          address            |
 * +-----------------------------+
 * | Open in Map                 |
 * | Save to People              |
 * | Save to existing contact    |
 * | Copy                        |
 * +-----------------------------+
 * </pre>
 */
public class DataActionPicker {
    private static final String TAG = "DataActionPicker";
    private static final String STR_TO = "to";
    private static final String STR_CC = "cc";
    private static final String STR_BCC = "bcc";

    private static class HtcListItemGroup {
        private HtcImageButton photo;
        private HtcListItem2LineText text;
        private View item;

    }

    /**
     * A listener to listen which action item is clicked.
     *
     */
    public static class ActionHandler {
        /**
         * A callback to tell which action item is clicked.
         * @param action the action is under creating.
         * @return if APP won't create this action and add it to list, return false. Default value is true.
         */
        public boolean onCreateAction(int action) {
            return true;
        }
        /**
         * A callback to tell which action item is clicked.
         * @param action the action clicked.
         * @return if APP handles this action, return true. Or default action will be performed. Default value is false.
         */
        public boolean onClickAction(int action) {
            return false;
        }
    }

    /**
     * Supported data types to show dialog.
     *
     */
    public enum DataType {
        /**
         * Phone number.
         */
        PHONE_NUMBER,
        /**
         * E-mail address.
         */
        EMAIL,
        /**
         * URL.
         */
        URL,
        /**
         * Postal address.
         */
        ADDRESS,
    }

    private static class Action {
        private int type;
        private String description;
        public Action(int type, String description) {
            this.type = type;
            this.description = description;
        }
    }

    /**
     * Call.
     */
    public static final int ACTION_CALL = 1<<8;
    /**
     * Edit number before calling.
     */
    public static final int ACTION_EDIT_NUMBER_BEFORE_CALLING = 2<<8;
    /**
     * Send message.
     */
    public static final int ACTION_SEND_MESSAGE = 3<<8;
    /**
     * Send E-mail.
     */
    public static final int ACTION_SEND_EMAIL = 4<<8;
    /**
     * Save to People.
     */
    public static final int ACTION_SAVE_TO_PEOPLE = 5<<8;
    /**
     * Save to existing contact..
     */
    public static final int ACTION_SAVE_TO_EXISTING_CONTACT = 6<<8;
    /**
     * Open
     */
    public static final int ACTION_OPEN = 7<<8;
    /**
     * Copy.
     */
    public static final int ACTION_COPY = 8<<8;
    /**
     * Open in map.
     */
    public static final int ACTION_OPEN_IN_MAP = 10<<8;

    private Context mContext;
    private DataType mDataType;
    private CharSequence mInputDefaultText;

    private HtcListView mListView;
    private LinearLayout mDailogView;
    private HtcAlertDialog.Builder mBuilder;
    private HtcAlertDialog mDialog;
    private ActionListAdapter mActionListAdapter;
    private ActionHandler mActionHandler;
    private List<Action> mActionList;

    private long mCallerPersonId;
    private String mCallerName;

    /**
     * DataActionPicker show a dialog to select an action for specified data type.
     * @param context context.
     * @param dataType which type of inputDefaultText.
     * @param inputDefaultText parsed structure data.
     */
    public DataActionPicker(Context context, DataType dataType, CharSequence inputDefaultText) {
        mContext = context;
        mDataType = dataType;
        mInputDefaultText = inputDefaultText;

        mBuilder = new HtcAlertDialog.Builder(context);

        ContentResolver cr = mContext.getContentResolver();
        Uri uriBase = Uri.parse("content://" + ContactsContract.AUTHORITY);
        Uri.Builder builder = null;
        Uri uri = null;
        ContentValues values = null;

        switch (mDataType) {
        case PHONE_NUMBER:
            mBuilder.setTitle(inputDefaultText);

            //from Message team
//            CallerInfo info = CallerInfo.getCallerInfo(mContext, inputDefaultText.toString());
//            if (info.person_id > 0) {
//                mCallerPersonId = info.person_id;
//                mCallerName = info.name;
//            }

            //same as CallerInfo
//            builder = uriBase.buildUpon();
//            builder = builder.appendEncodedPath("data/phones/lookup_number_equal").appendPath(inputDefaultText.toString());
//            uri = builder.build();
//            Log.d(TAG, "lookup uri: " + uri);
//            values = lookup(cr, uri, new String[]{ContactsContract.RawContacts.CONTACT_ID, ContactsContract.Contacts.DISPLAY_NAME});
//            if (values != null) {
//                mCallerPersonId = values.getAsInteger(ContactsContract.RawContacts.CONTACT_ID);
//                mCallerName = values.getAsString(ContactsContract.Contacts.DISPLAY_NAME);
//            }

            //from People team

            uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(inputDefaultText.toString()));
            if (HtcBuildFlag.Htc_SECURITY_DEBUG_flag) {
                Log.d(TAG, "lookup uri: " + uri);
            }

            new LookUpTask() {
                protected void onPostExecute(ContentValues values) {
                    if (values != null) {
                        long oldId = mCallerPersonId;
                        mCallerPersonId = values.getAsInteger(ContactsContract.Contacts._ID);
                        mCallerName = values.getAsString(ContactsContract.Contacts.DISPLAY_NAME);
                        if (mCallerPersonId != oldId && mDialog != null && mDialog.isShowing()) {
                            updateActionInternal(ACTION_SAVE_TO_PEOPLE, getSaveContactActionDescription(mCallerPersonId));
                        }
                    }
                }
            }.execute(cr, uri, new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME});

            break;
        case EMAIL:
            mBuilder.setTitle(inputDefaultText);

            //from People team
            builder = uriBase.buildUpon();
            builder = builder.appendEncodedPath("contacts/filter_emailaddress").appendPath(inputDefaultText.toString());
            uri = builder.build();
            if (HtcBuildFlag.Htc_SECURITY_DEBUG_flag) {
                Log.d(TAG, "lookup uri: " + uri);
            }

            new LookUpTask() {
                protected void onPostExecute(ContentValues values) {
                    if (values != null) {
                        long oldId = mCallerPersonId;
                        mCallerPersonId = values.getAsInteger(ContactsContract.RawContacts.CONTACT_ID);
                        mCallerName = values.getAsString(ContactsContract.Contacts.DISPLAY_NAME);
                        if (mCallerPersonId != oldId && mDialog != null && mDialog.isShowing()) {
                            updateActionInternal(ACTION_SAVE_TO_PEOPLE, getSaveContactActionDescription(mCallerPersonId));
                        }
                    }
                }
            }.execute(cr, uri, new String[]{ContactsContract.RawContacts.CONTACT_ID, ContactsContract.Contacts.DISPLAY_NAME});

            //from google?
//            builder = uriBase.buildUpon();
//            builder = builder.appendEncodedPath("data/emails/filter").appendPath(inputDefaultText.toString());
//            uri = builder.build();
//            if (HtcBuildFlag.Htc_SECURITY_DEBUG_flag) {
////            Log.d(TAG, "lookup uri: " + uri);
//            }
//            values = lookup(cr, uri, new String[]{ContactsContract.RawContacts.CONTACT_ID, ContactsContract.Contacts.DISPLAY_NAME});
//            if (values != null) {
//                mCallerPersonId = values.getAsInteger("contact_id");
//                mCallerName = values.getAsString(ContactsContract.Contacts.DISPLAY_NAME);
//            }
            break;
        case URL:
            mBuilder.setTitle(inputDefaultText);
            break;
        case ADDRESS:
            mBuilder.setTitle(inputDefaultText);
            break;
           default:
               mBuilder.setTitle("Unknown....");
        }

        initViews();
        mBuilder.setView(mDailogView);
    }

    private class LookUpTask extends AsyncTask<Object, Void, ContentValues> {
        protected ContentValues doInBackground(Object... obj) {
            return lookup((ContentResolver)obj[0], (Uri)obj[1], (String[])obj[2]);
        }

        private ContentValues lookup(ContentResolver cr, Uri uri, String[] projections) {
            ContentValues values = null;
            Cursor c = null;
            try {
                c = cr.query(uri, projections, null, null, null);
                if (c.moveToFirst()) {
                    values = new ContentValues();
                    for (int i = 0, n = c.getColumnCount(); i < n; i++) {
                        values.put(c.getColumnName(i), c.getString(i));
                    }
                    if (HtcBuildFlag.Htc_SECURITY_DEBUG_flag) {
                        Log.d(TAG, "lookup result: " + values);
                    }
                }
            } catch(Exception e){
                Log.e(TAG, e.getMessage(), e);
            } finally {
                if(c != null) {
                    c.close();
                }
            }
            return values;
        }
    }

    private void initViews() {
        mDailogView = new LinearLayout(mContext);
        mDailogView.setOrientation(LinearLayout.VERTICAL);
        mListView = new HtcListView(mContext);
        mListView.setDivider(mContext.getResources().getDrawable(R.drawable.inset_list_divider));
        mListView.enableAnimation(IHtcAbsListView.ANIM_OVERSCROLL, false);
        mListView.setAdapter(mActionListAdapter = new ActionListAdapter(mContext));

        TypedArray a = mContext.obtainStyledAttributes(R.style.HtcListView, new int[]{android.R.attr.listSelector});
        if (a != null) {
            Drawable dr = a.getDrawable(0);
            if (dr != null) {
                mListView.setSelector(dr);
            }
            a.recycle();
        }

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int action = ((Action)mActionListAdapter.getItem(position)).type;
                boolean handled = false;
                if (mActionHandler != null) {
                    handled = mActionHandler.onClickAction(action);
                }
                if (!handled) {
                    String input = mInputDefaultText.toString();
                    switch (action) {
                    case ACTION_CALL:
                        Intent intent;
                        if (isDualSIM()) {
                            //show dialer screen
                            intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + input));
                        } else {
                            intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + input));
                        }
                        mContext.startActivity(intent);
                        break;
                    case ACTION_EDIT_NUMBER_BEFORE_CALLING:
                        intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + input));
                        mContext.startActivity(intent);
                        break;
                    case ACTION_SEND_MESSAGE:
                        intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", input, null));
                        mContext.startActivity(intent);
                        break;
                    case ACTION_SAVE_TO_PEOPLE:
                        if (mCallerPersonId > 0) {
                            Uri rawContactUri = Uri.parse(ContactsContract.AUTHORITY_URI + "/contacts/" + mCallerPersonId);
                            intent = new Intent(Intent.ACTION_VIEW, rawContactUri);
                        } else {
                            intent = new Intent(Intent.ACTION_INSERT);
                            intent.setData(android.provider.ContactsContract.Contacts.CONTENT_URI);
                            switch (mDataType) {
                            case PHONE_NUMBER:
                                intent.putExtra(Insert.PHONE, input);
                                break;
                            case ADDRESS:
                                intent.putExtra(Insert.POSTAL, input);
                                break;
                            case EMAIL:
                                String firstEmailAddress = getMailToFirstEmailAddress(input);
                                intent.putExtra(Insert.EMAIL, firstEmailAddress);
                                break;
                            }
                        }
                        mContext.startActivity(intent);
                        break;
                    case ACTION_SAVE_TO_EXISTING_CONTACT:
                        intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
                        intent.setType(android.provider.ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                        switch (mDataType) {
                        case PHONE_NUMBER:
                            intent.putExtra(Insert.PHONE, input);
                            break;
                        case ADDRESS:
                            intent.putExtra(Insert.POSTAL, input);
                            break;
                        case EMAIL:
                            String firstEmailAddress = getMailToFirstEmailAddress(input);
                            intent.putExtra(Insert.EMAIL, firstEmailAddress);
                            break;
                        }
                        mContext.startActivity(intent);
                        break;
                    case ACTION_COPY:
                        ClipboardManager clipboard = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("", input);
                        clipboard.setPrimaryClip(clip);
                        break;
                    case ACTION_SEND_EMAIL:
                        intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + input));
                        mContext.startActivity(intent);
                        break;
                    case ACTION_OPEN:
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(input));
                        mContext.startActivity(intent);
                        break;
                    case ACTION_OPEN_IN_MAP:
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + input));
                        mContext.startActivity(intent);
                        break;
                    }
                }
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
            }

            private boolean isDualSIM() {
                boolean ret = false;
                ClassLoader cl = getClass().getClassLoader();
                try {
                    Class htcTelephonyManager = cl.loadClass("com.htc.telephony.HtcTelephonyManager");
                    Method dualPhoneEnable = htcTelephonyManager.getDeclaredMethod("dualPhoneEnable");
                    Method dualGSMPhoneEnable = htcTelephonyManager.getDeclaredMethod("dualGSMPhoneEnable");
                    ret = ((Boolean)dualPhoneEnable.invoke(null)) || ((Boolean)dualGSMPhoneEnable.invoke(null));
                } catch (Throwable t) {
                    Log.d(TAG, t.getMessage(), t);
                    try {
                        Class telephonyManager = cl.loadClass("android.telephony.TelephonyManager");
                        Method dualPhoneEnable = telephonyManager.getDeclaredMethod("dualPhoneEnable");
                        Method dualGSMPhoneEnable = telephonyManager.getDeclaredMethod("dualGSMPhoneEnable");
                        ret = ((Boolean)dualPhoneEnable.invoke(null)) || ((Boolean)dualGSMPhoneEnable.invoke(null));
                    } catch (Throwable t2) {
                        Log.d(TAG, t2.getMessage(), t2);
                    }
                }
                return ret;
            }
        });
        mDailogView.addView(mListView);
    }

    private void addActionInternal(int action, String description) {
        addActionInternal(-1, action, description);
    }

    private void addActionInternal(int index, int type, String description) {
        if (mDialog == null) {
            if (mActionHandler == null || mActionHandler.onCreateAction(type)) {
                if (index < 0) {
                    mActionList.add(new Action(type, description));
                } else {
                    mActionList.add(index, new Action(type, description));
                }
            }
        }
    }

    /**
     * Add a custom action to action list.
     * @param action the custom action.
     * @param description a description about this action.
     */
    public void addAction(int action, String description) {
        if (action>>>8 != 0) {
            throw new IllegalArgumentException("Please pass an action type value between 0 ~ 255.");
        }
        prepareActionList();
        addActionInternal(action, description);
    }

    /**
     * Add a custom action to action list.
     * @param actionToAddBefore the action item that this custom action want to add in front of.
     * @param action the custom action.
     * @param description a description about this action.
     */
    public void addActionBefore(int actionToAddBefore, int action, String description) {
        if (action>>>8 != 0) {
            throw new IllegalArgumentException("Please pass an action type value between 0 ~ 255.");
        }
        if (mDialog == null) {
            prepareActionList();
            if (actionToAddBefore < 0) {
                addActionInternal(action, description);
            } else {
                int index = -1;
                for (int i = 0, n = mActionList.size(); i < n; i++) {
                    Action a = mActionList.get(i);
                    if (a.type == actionToAddBefore) {
                        index = i;
                        break;
                    }
                }
                if (index < 0) {
                    addActionInternal(action, description);
                } else {
                    addActionInternal(index, action, description);
                }
            }
        }
    }

    /**
     * Add a custom action to action list.
     * @param actionToAddAfter the action item that this custom action want to add in back of.
     * @param action the custom action.
     * @param description a description about this action.
     */
    public void addActionAfter(int actionToAddAfter, int action, String description) {
        if (action>>>8 != 0) {
            throw new IllegalArgumentException("Please pass an action type value between 0 ~ 255.");
        }
        if (mDialog == null) {
            prepareActionList();
            if (actionToAddAfter < 0) {
                addActionInternal(action, description);
            } else {
                int index = -1;
                for (int i = 0, n = mActionList.size(); i < n; i++) {
                    Action a = mActionList.get(i);
                    if (a.type == actionToAddAfter) {
                        index = i + 1;
                        break;
                    }
                }
                if (index < 0) {
                    addActionInternal(action, description);
                } else {
                    addActionInternal(index, action, description);
                }
            }
        }
    }

    private void updateActionInternal(int type, String description) {
        if (mActionList != null) {
            boolean changed = false;
            for (Action a : mActionList) {
                if (type == a.type) {
                    a.description = description;
                    changed = true;
                    break;
                }
            }
            if (changed) {
                mActionListAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Set a action lick listener to listen which action item is clicked.
     * NOTICE: You should call setActionHandler() before calling addAction() if you want to set an action handler to handle action item creation.
     * @param handler
     */
    public void setActionHandler(ActionHandler handler) {
        mActionHandler = handler;
    }

    /**
     * Show the action list dialog.
     * @return the dialog instance.
     */
    public HtcAlertDialog show() {
        if (mDialog == null) {
            prepareActionList();
            mActionListAdapter.notifyDataSetChanged();
            mDialog = mBuilder.show();
        }
        return mDialog;
    }

    private void prepareActionList() {
        if (mActionList != null) {
            return;
        }
        mActionList = new ArrayList<Action>();

        switch (mDataType) {
        case PHONE_NUMBER:
            addCallAction();
            addEditNumberBeforeCallingAction();
            addSendMessageAction();
            addSaveContactAction();
            addCopyAction();
            break;
        case EMAIL:
            addSendMailAction();
            addSendMessageAction();
            addSaveContactAction();
            addCopyAction();
            break;
        case URL:
            addOpenURLAction();
            addCopyAction();
            break;
        case ADDRESS:
            addOpenInMapAction();
            addSaveContactAction();
            addCopyAction();
            break;
        }
    }

    private void addCallAction() {
        addActionInternal(ACTION_CALL, mContext.getString(R.string.va_call));
    }

    private void addEditNumberBeforeCallingAction() {
        //TODO
        addActionInternal(ACTION_EDIT_NUMBER_BEFORE_CALLING, mContext.getString(R.string.ls_edit_number_before_calling));
    }

    private void addSendMessageAction() {
        //TODO
//        if(MmsConfig.isSupportMms() == true || MmsConfig.isSupportSmsEmailAddress() == true){
        addActionInternal(ACTION_SEND_MESSAGE, mContext.getString(R.string.va_send_message));
//        }
    }

    private String getSaveContactActionDescription(long id) {
        return (id > 0) ? mContext.getString(R.string.common_string_open_contact) :
            mContext.getString(R.string.common_string_save_to_people);
    }

    private void addSaveContactAction() {
        addActionInternal(ACTION_SAVE_TO_PEOPLE, getSaveContactActionDescription(mCallerPersonId));
        addActionInternal(ACTION_SAVE_TO_EXISTING_CONTACT, mContext.getString(R.string.common_string_save_to_existing_contact));
    }

    private void addSendMailAction() {
        addActionInternal(ACTION_SEND_EMAIL, mContext.getString(R.string.va_send_mail));
    }

    private void addOpenURLAction() {
        addActionInternal(ACTION_OPEN, mContext.getString(R.string.va_open));
    }

    private void addOpenInMapAction() {
        addActionInternal(ACTION_OPEN_IN_MAP, mContext.getString(R.string.ls_open_in_map));
    }

    private void addCopyAction() {
        addActionInternal(ACTION_COPY, mContext.getString(R.string.va_copy));
    }


    private class ActionListAdapter extends BaseAdapter {
        private class ViewHolder {
            HtcListItemGroup group;
            int type;
        }

        private Context mContext = null;
        private List<Action> data = new ArrayList<Action>();

        public ActionListAdapter(Context context) {
            mContext = context;
        }

        public int getCount(){
            return data.size();
        }

        public long getItemId(int position) {
            return position;
        }

        public Object getItem(int position) {
            return data.get(position);
        }

        private HtcListItemGroup createImageButtonListItem(Context context) {
            HtcListItemGroup group = new HtcListItemGroup();
            HtcListItem item = createListItem();

            HtcImageButton icon = (HtcImageButton)item.findViewById(1);

            HtcListItem2LineText text = (HtcListItem2LineText)item.findViewById(2);
            text.setSecondaryTextVisibility(View.GONE);

            group.item = item;
            group.text = text;
            group.photo = icon;

            return group;
        }

        private HtcListItem createListItem() {
            HtcListItem item = new HtcListItem(mContext);

            HtcImageButton icon = new HtcImageButton(mContext);
            icon.setId(1);
            icon.setVisibility(View.GONE);
            item.addView(icon);

            HtcListItem2LineText text = new HtcListItem2LineText(mContext);
            text.setId(2);
            item.addView(text);

            return item;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder cache = new ViewHolder();
            HtcListItemGroup group = null;
            HtcImageButton icon = null;
            HtcListItem2LineText text = null;

            if (convertView == null){
                group = createImageButtonListItem(mContext);
                cache.group = group;
                convertView = group.item;
                //FIXME: sense 40>> need check with designer use which background
//                group.Photo.setBackgroundResource(R.drawable.common_button_small_rest);
                convertView.setTag(cache);
            }
            else {
                cache = (ViewHolder) convertView.getTag();
                group = cache.group;
            }
            icon = group.photo;
            text = group.text;

            Action action = data.get(position);
            cache.type = action.type;
            text.setPrimaryText(action.description);
            return convertView;
        }

        @Override
        public void notifyDataSetChanged() {
            if (mActionList != null) {
                data.clear();
                data.addAll(mActionList);
            }
            super.notifyDataSetChanged();
        }
    }

    /**
     * Get first email address found in scheme-specific part of given mailto uri
     * @param encodeUriPart encoded scheme-specific part of mailto uri
     * @return decoded first found email address or null if not found
     */
    private static String getMailToFirstEmailAddress(String encodeUriPart) {
        ArrayList<Mailaddress> mailAddressList;

        String[] parts = encodeUriPart.split("\\?", 2);
        String decodeToField = Uri.decode(parts[0]);

        StringBuilder addressListSB = new StringBuilder();

        if (TextUtils.isEmpty(decodeToField) == false) {
            addressListSB.append(decodeToField);
            mailAddressList = Mailaddress.parse(addressListSB.toString());
            if (mailAddressList != null && mailAddressList.size() > 0) {
                for (Mailaddress address : mailAddressList) {
                    if (address != null && TextUtils.isEmpty(address.mEmail) == false) {
                        return address.mEmail;
                    }
                }
            }
        }

        // disguise this string as a URI in order to parse it
        Uri uri = Uri.parse("foo://" + encodeUriPart);

        // append additional to addresses to the "mailto" addresses
        List<String> to = uri.getQueryParameters(STR_TO);
        for (String toValues : to) {
            addressListSB.append(",");
            addressListSB.append(toValues);
        }

        List<String> cc = uri.getQueryParameters(STR_CC);
        for (String ccValues : cc) {
            addressListSB.append(",");
            addressListSB.append(ccValues);
        }

        List<String> bcc = uri.getQueryParameters(STR_BCC);
        for(String bccValues : bcc) {
            addressListSB.append(",");
            addressListSB.append(bccValues);
        }

        mailAddressList = Mailaddress.parse(addressListSB.toString());
        if (mailAddressList != null && mailAddressList.size() > 0) {
            for (Mailaddress address : mailAddressList) {
                if (address != null && TextUtils.isEmpty(address.mEmail) == false) {
                    return address.mEmail;
                }
            }
        }

        return null;
    }
}

/*@hide*/
class Mailaddress {

    private static final String LOOSE_EMAILADDRESS_PATTERN = "[^\\s<]+@[^\\s>]+";
    private static final String LOOSE_DISPLAYNAME_PATTERN = "(?:\\s+\"|^\")(.*" + LOOSE_EMAILADDRESS_PATTERN + ".*[^\\\\])\"\\s*";
    private static final String LOOSE_ANGLE_EMAILADDRESS_PATTERN = "\\s*<\\s*([^ <>]+)\\s*>\\s*";
    private static final String LOOSE_NAME_ADDRESS_PATTERN = "(?:" + LOOSE_DISPLAYNAME_PATTERN + ")*\\s*" + LOOSE_ANGLE_EMAILADDRESS_PATTERN;
    private static final String LOOSE_MAILBOX = LOOSE_NAME_ADDRESS_PATTERN + "|\\s*(" + LOOSE_EMAILADDRESS_PATTERN + ")\\s*";

    public String mDisplayName = null;

    public String mEmail = "";

    public static ArrayList<Mailaddress> parse(String str) {
        ArrayList<Mailaddress> mailAddressList = new ArrayList<Mailaddress>();

        StringReader reader = new StringReader(str);
        StringWriter writer = new StringWriter();
        int next;
        int previous = -1;
        boolean meetFirstQuoter = false;
        boolean meetLeftAngleParenthesis = false;
        boolean meetRightAngleParenthesis = false;
        boolean meetAtSymbole = false;
        try {
            while((next = reader.read()) != -1) {
                switch(next) {
                    case '<':
                        meetLeftAngleParenthesis = true;
                        break;
                    case '>':
                        meetRightAngleParenthesis = true;
                        break;
                    case '@':
                        meetAtSymbole = true;
                        break;
                }
                if(previous != '\\' && next == '"') {
                    if(!meetFirstQuoter) {
                        meetFirstQuoter = true;
                    } else {
                        meetFirstQuoter = false;
                    }
                }

                if ((!meetFirstQuoter || (meetLeftAngleParenthesis && meetRightAngleParenthesis && meetAtSymbole))
                        && (next == ',' || next == ';')) {
                    parseInternal(writer.toString(), mailAddressList);
                    writer = new StringWriter();
                    meetLeftAngleParenthesis = meetRightAngleParenthesis = meetAtSymbole = false;
                } else {
                    writer.write(next);
                }
                previous = next;
            }
            String remaining = writer.toString();
            if(remaining.length() > 0) {
                parseInternal(remaining, mailAddressList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mailAddressList;
    }

    private static void parseInternal(String str, ArrayList<Mailaddress> mailAddressList) {
        if(str == null || str.length() == 0) return;
        Matcher m = Pattern.compile(LOOSE_MAILBOX).matcher(str);
        int strStart = 0;
        if(m.find()) {
            strStart = getMailaddress(str, 0, m, mailAddressList);
            while(m.find()) {
                strStart = getMailaddress(str, strStart, m, mailAddressList);
            }
            if(strStart < str.length()) {
                Mailaddress address = new Mailaddress();
                address.mDisplayName = "";
                address.mEmail = str.substring(strStart, str.length());
                mailAddressList.add(address);
            }
        } else {
            str = trim(str);
            if(str.length() > 0) {
                Mailaddress address = new Mailaddress();
                address.mDisplayName = "";
                address.mEmail = str;
                mailAddressList.add(address);
            }
        }
    }

    private static int getMailaddress(String str, int strStart, Matcher m, ArrayList<Mailaddress> mailAddressList) {
        int start = m.start();
        int end = m.end();
        String emailAddress = m.group(3);
        if(emailAddress != null) {
            String displayName = str.substring(strStart, start);
            displayName = stripDisplayName(displayName);
            Mailaddress address = new Mailaddress();
            address.mDisplayName = displayName;
            address.mEmail = emailAddress;
            mailAddressList.add(address);
        } else {
            String displayName = m.group(1);
            emailAddress = m.group(2);
            if(displayName == null) {
                displayName = str.substring(strStart, start);
                displayName = stripDisplayName(displayName);
                Mailaddress address = new Mailaddress();
                address.mDisplayName = displayName;
                address.mEmail = emailAddress;
                mailAddressList.add(address);
            } else {
                Mailaddress address = new Mailaddress();
                address.mDisplayName = stripDisplayName(displayName);
                address.mEmail = emailAddress;
                mailAddressList.add(address);
            }
        }
        return end;
    }

    private static String stripDisplayName(String displayName) {
        if(displayName == null) return "";
        int size = displayName.length();
        if (size == 0) return "";

        displayName = trim(displayName);

        size = displayName.length();
        if(size >= 2) {
            if(displayName.charAt(0) == '"' && displayName.charAt(size - 1) == '"') {
                displayName = displayName.substring(1, size - 1);
            }
        }
        return displayName;
    }

    private static String trim(String str) {
        if(str == null) return null;
        int size = str.length();
        if(size == 0) return str;

        if(str.charAt(0) <= ' ') {
            str = str.trim();
        } else if (size >= 2 && str.charAt(size - 1) <= ' ') {
            str = str.trim();
        }
        return str;
    }

}
