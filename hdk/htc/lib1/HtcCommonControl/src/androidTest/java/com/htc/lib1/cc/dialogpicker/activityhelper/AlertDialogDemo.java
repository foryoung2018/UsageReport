
package com.htc.lib1.cc.dialogpicker.activityhelper;

/*
 * Copyright (C) 2009 HTC Inc.
 */

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.htc.lib1.cc.test.R;

import com.htc.lib1.cc.app.HtcProgressDialog;
import com.htc.lib1.cc.app.OnActionModeChangedListener;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.lib1.cc.widget.HtcCompoundButton;
import com.htc.lib1.cc.widget.HtcListView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class AlertDialogDemo extends DialogActivityBase implements
        View.OnClickListener {

    private static final String TAG = "AlertDialogDemo";

    // main(top-level) 5+1 categories
    private static final int DIALOG_ALL_MESSAGE_DIALOGS = 1; // message dialogs
    private static final int DIALOG_ALL_LIST_DIALOGS = 2; // list dialogs
    private static final int DIALOG_ALL_PROGRESS_DIALOGS = 3; // progress
                                                              // dialogs
    private static final int DIALOG_ALL_AUTOMOTIVE_DIALOGS = 4; // automotive
                                                                // dialogs
    private static final int DIALOG_ALL_THEMED_DIALOGS = 5; // apply different
                                                            // themes for
                                                            // dialogs
    private static final int DIALOG_ALL_TIE_DIALOGS = 6;

    // 1. message dialogs
    private static final int DIALOG_ACKNOWLEDGEMENT = 11; // two-line message, 1
                                                          // button
    private static final int DIALOG_QUESTION = 12; // "longer, but less than one page"
                                                   // message, 2 buttons
    private static final int DIALOG_LEGAL_MESSAGE = 13; // legal message, super
                                                        // long (more than one
                                                        // page), 0 button
    private static final int DIALOG_ONE_LINE = 14; // one line message, 3
                                                   // buttons
    private static final int DIALOG_CHECK_MESSAGE = 16; // text message followed
                                                        // with check-box
    private static final int DIALOG_CHECK_MESSAGE2 = 17; // text message
                                                         // followed with
                                                         // check-box
    // private static final int DIALOG_INVERSEBKG = 15; // inverse (content)
    // background (check asset)

    // 2. list dialogs
    private static final int DIALOG_LIST = 21; // list dialog (a few items, less
                                               // than one page), 0 button
    private static final int DIALOG_SINGLE_CHOICE = 22; // single choice dialog
                                                        // (a few items), 1
                                                        // cancel button
    private static final int DIALOG_MULTIPLE_CHOICES = 23; // multiple choices
                                                           // dialog (many
                                                           // choices, more than
                                                           // one page), 2
                                                           // buttons
    private static final int DIALOG_LONG_LIST = 24; // long list (more than one
                                                    // page), 3 buttons
    private static final int DIALOG_CHECK_LIST = 25; // check-box following list
                                                     // view
    private static final int DIALOG_FONT_SELECTOR = 26; // pre-defined font
                                                        // selector dialog

    // 3. progress dialogs
    private static final int DIALOG_SPINNER_PROGRESS = 31; // spinner progress
                                                           // dialog with no
                                                           // title
    private static final int DIALOG_SPINNER_LISTITEM = 33; // spinner progress
                                                           // dialog with title,
                                                           // which shall
                                                           // transform to list
                                                           // view style
    private static final int DIALOG_HORIZONTAL_PROGRESS = 32; // horizontal
                                                              // progress dialog
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
    private static final int DIALOG_HORIZONTAL_NO_NUMBER = 36;
    private static final int DIALOG_HORIZONTAL_NO_NUMBER_MINHEIGHT = 37;

    // 4. automotive dialogs
    private static final int DIALOG_AUTO_MESSAGE = 41;
    private static final int DIALOG_AUTO_LIST = 42;
    private static final int DIALOG_AUTO_MULTICHOICE = 43;
    private static final int DIALOG_AUTO_CHECKBOX = 44;
    private static final int DIALOG_AUTO_SINGLECHOICE = 45;

    // 6. tie dialogs
    private static final int DIALOG_TIE_MESSAGE = 61;
    private static final int DIALOG_TIE_LIST = 62;
    private static final int DIALOG_TIE_MULTICHOICE = 63;
    private static final int DIALOG_TIE_SINGLECHOICE = 64;

    // lists for top 3 categories
    private static final int[] IDS_ALL_MESSAGE_DIALOGS = {
            DIALOG_ACKNOWLEDGEMENT, DIALOG_QUESTION, DIALOG_LEGAL_MESSAGE,
            DIALOG_ONE_LINE, DIALOG_CHECK_MESSAGE, DIALOG_CHECK_MESSAGE2
            /*
             * , DIALOG_INVERSEBKG
             */};

    private static final int[] IDS_ALL_LIST_DIALOGS = {
            DIALOG_LIST,
            DIALOG_SINGLE_CHOICE, DIALOG_MULTIPLE_CHOICES, DIALOG_LONG_LIST,
            DIALOG_CHECK_LIST, DIALOG_FONT_SELECTOR
    };

    private static final int[] IDS_ALL_PROGRESS_DIALOGS = {
            DIALOG_SPINNER_PROGRESS, DIALOG_SPINNER_LISTITEM,
            DIALOG_HORIZONTAL_PROGRESS, DIALOG_HORIZONTAL_NO_MESSAGE,
            DIALOG_HORIZONTAL_LONG_MESSAGE, DIALOG_HORIZONTAL_NO_NUMBER,
            DIALOG_HORIZONTAL_NO_NUMBER_MINHEIGHT
    };

    private static final int[] IDS_ALL_AUTOMOTIVE_DIALOGS = {
            DIALOG_AUTO_MESSAGE, DIALOG_AUTO_LIST, DIALOG_AUTO_MULTICHOICE,
            DIALOG_AUTO_CHECKBOX, DIALOG_AUTO_SINGLECHOICE
    };

    private static final int[] IDS_ALL_TIE_DIALOGS = {
            DIALOG_TIE_MESSAGE,
            DIALOG_TIE_LIST, DIALOG_TIE_MULTICHOICE, DIALOG_TIE_SINGLECHOICE
    };

    private static final int MAX_PROGRESS = 100; // The maximum progress of
                                                 // ProgressDialog
    private static final int REQ_RINGTONE_PICKER = 12345;

    private final Handler mHandler = new Handler(); // used to update the
                                                    // progress
    private boolean mCancelled = false;
    private boolean mShowToasts = true;
    private Dialog mDialog = null;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HtcCommonUtil.initTheme(this, mCategoryId);
        setContentView(R.layout.alert_dialog_layout);

        // disable toast for accessibility exam
        ((CheckBox) findViewById(R.id.showToasts)).setChecked(mShowToasts);
        Intent i = getIntent();
        if (null == i) {
            return;
        }
        int id = i.getIntExtra("dialogId", 0);
        showDialog(id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_RINGTONE_PICKER:
                if (Activity.RESULT_CANCELED == resultCode) {
                    showToast("cancelled 'RingtonePickerAlertActivity'");
                } else {
                    Uri uri = data
                            .getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    showToast("ringtone choosed: uri=" + uri);
                }

                break;
        }
    }

    private void showToast(String message) {
        if (mShowToasts)
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("deprecation")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alert_dialog_btn:
                showDialog(DIALOG_ALL_MESSAGE_DIALOGS);
                break;

            case R.id.list_dialog_btn:
                showDialog(DIALOG_ALL_LIST_DIALOGS);
                break;

            case R.id.progress_dialog_btn:
                showDialog(DIALOG_ALL_PROGRESS_DIALOGS);
                break;

            case R.id.automotive_dialog_btn:
                showDialog(DIALOG_ALL_AUTOMOTIVE_DIALOGS);
                break;

            case R.id.tie_dialog_btn:
                showDialog(DIALOG_ALL_TIE_DIALOGS);
                break;

            case R.id.ringtone_picker_dialog_btn:
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                // Intent intent = new Intent();
                // intent.setClassName("com.htc",
                // "com.htc.lib1.cc.app.HtcRingtonePickerActivity");
                try {
                    startActivityForResult(intent, REQ_RINGTONE_PICKER);
                } catch (Exception e) {
                    showToast(e.toString());
                    Log.d(TAG, "onClick: e=" + e);
                }
                break;
            case R.id.showToasts:
                mShowToasts = ((CheckBox) v).isChecked();
                break;

            case R.id.category_dialog_btn:
                showDialog(DIALOG_ALL_THEMED_DIALOGS);
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final Resources res = getResources();
        final String va_ok = getString(R.string.va_ok);
        final String va_cancel = getString(R.string.va_cancel);

        switch (id) {

            case DIALOG_ALL_MESSAGE_DIALOGS:
                // Create a list dialog
                mDialog = new HtcAlertDialog.Builder(mContext)
                        .setTitle(R.string.all_alert_dialogs)
                        .setItems(R.array.alert_dialog_items,
                                new OnClickListener() {
                                    @SuppressWarnings("deprecation")
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        showDialog(IDS_ALL_MESSAGE_DIALOGS[which]);
                                    }
                                }).create();
                return mDialog;

            case DIALOG_ALL_LIST_DIALOGS:
                // Create a list dialog
                mDialog = new HtcAlertDialog.Builder(mContext)
                        .setTitle(R.string.all_list_dialogs)
                        .setItems(R.array.list_dialog_items,
                                new OnClickListener() {
                                    @SuppressWarnings("deprecation")
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        showDialog(IDS_ALL_LIST_DIALOGS[which]);
                                    }
                                }).create();
                return mDialog;

            case DIALOG_ALL_PROGRESS_DIALOGS:
                // Create a list dialog
                mDialog = new HtcAlertDialog.Builder(mContext)
                        .setTitle(R.string.all_progress_dialogs)
                        .setItems(R.array.progress_dialog_items,
                                new OnClickListener() {
                                    @SuppressWarnings("deprecation")
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        showDialog(IDS_ALL_PROGRESS_DIALOGS[which]);
                                    }
                                }).create();
                return mDialog;

            case DIALOG_ALL_AUTOMOTIVE_DIALOGS:
                // Create a list dialog
                mDialog = new HtcAlertDialog.Builder(mContext)
                        .setTitle(R.string.all_automotive_dialogs)
                        .setItems(R.array.automotive_dialog_items,
                                new OnClickListener() {
                                    @SuppressWarnings("deprecation")
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        showDialog(IDS_ALL_AUTOMOTIVE_DIALOGS[which]);
                                    }
                                }).create();
                return mDialog;

            case DIALOG_ALL_THEMED_DIALOGS:
                mDialog = new HtcAlertDialog.Builder(mContext)
                        .setTitle(R.string.all_themed_dialogs)
                        .setItems(R.array.theme_dialog_items,
                                new OnClickListener() {

                                    @SuppressWarnings("deprecation")
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        switch (which) {
                                            case 0: // NA
                                                mContext = AlertDialogDemo.this;
                                                break;
                                            case 1: // category 1
                                                mContext = new ContextThemeWrapper(
                                                        AlertDialogDemo.this,
                                                        R.style.HtcDeviceDefault_CategoryOne);
                                                break;
                                            case 2: // category 2
                                                mContext = new ContextThemeWrapper(
                                                        AlertDialogDemo.this,
                                                        R.style.HtcDeviceDefault_CategoryTwo);
                                                break;
                                            case 3: // category 3
                                                mContext = new ContextThemeWrapper(
                                                        AlertDialogDemo.this,
                                                        R.style.HtcDeviceDefault_CategoryThree);
                                                break;
                                            case 4: // category 4
                                                mContext = new ContextThemeWrapper(
                                                        AlertDialogDemo.this,
                                                        R.style.HtcDeviceDefault_CategoryFour);
                                                break;
                                        }

                                        for (int i : IDS_ALL_AUTOMOTIVE_DIALOGS) {
                                            removeDialog(i);
                                        }
                                        for (int i : IDS_ALL_LIST_DIALOGS) {
                                            removeDialog(i);
                                        }
                                        for (int i : IDS_ALL_MESSAGE_DIALOGS) {
                                            removeDialog(i);
                                        }
                                        for (int i : IDS_ALL_PROGRESS_DIALOGS) {
                                            removeDialog(i);
                                        }
                                    }
                                }).create();
                return mDialog;

            case DIALOG_ALL_TIE_DIALOGS:
                mDialog = new HtcAlertDialog.Builder(mContext)
                        .setTitle(R.string.all_tie_dialogs)
                        .setItems(R.array.tie_dialog_items,
                                new OnClickListener() {
                                    @SuppressWarnings("deprecation")
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        showDialog(IDS_ALL_TIE_DIALOGS[which]);
                                    }
                                }).create();
                return mDialog;

                // -----------------------------------------------------------------------------------

            case DIALOG_ACKNOWLEDGEMENT:
                // Create a short message (about 2 lines) dialog with 1 OK
                // button
                final String ack_title = getString(R.string.delete_duplicate_contacts);
                mDialog = new HtcAlertDialog.Builder(mContext).setTitle(ack_title)
                        .setMessage(R.string.less_than_two)
                        .setPositiveButton(va_ok, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                showToast(va_ok + " " + ack_title);
                            }

                        }).setOnCancelListener(new OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface arg0) {
                                showToast(va_cancel + " " + ack_title);
                            }

                        }).create();
                return mDialog;

            case DIALOG_QUESTION:
                // Create a question (longer, about 3 lines) dialog with OK and
                // Cancel buttons
                final String question_title = getString(R.string.remove_account);
                mDialog = new HtcAlertDialog.Builder(mContext)
                        .setTitle(question_title + "?")
                        .setMessage(R.string.remove_account_warning)
                        .setPositiveButton(question_title, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showToast(question_title);
                            }

                        })
                        // according to latest UI/UX guideline,
                        // the "cancel" button is not used,
                        // because user can always tap "HW-Back" to cancel the
                        // action.
                        .setNegativeButton("More Info", new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showToast("More Info" + " " + question_title);
                            }
                        }).setOnCancelListener(new OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface arg0) {
                                showToast(va_cancel + " " + question_title);
                            }

                        }).create();
                return mDialog;

            case DIALOG_LEGAL_MESSAGE:
                // super long message with no button
                final String legal_title = getString(R.string.htc_legal);
                mDialog = new HtcAlertDialog.Builder(mContext)
                        .setTitle(R.string.htc_legal)
                        .setMessage(R.string.alert_dialog_long_message_content)
                        .setOnCancelListener(new OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface arg0) {
                                showToast(va_cancel + " " + legal_title);
                            }

                        }).create();
                return mDialog;

            case DIALOG_ONE_LINE:
                // for test vertical alignment only, also test 2 lines of title
                // text
                // also test long button text (2 lines and more)
                final String oneLine_title = getString(R.string.center_align);
                HtcAlertDialog oneLine_dialog = new HtcAlertDialog.Builder(mContext)
                        .setTitle(oneLine_title + " " + oneLine_title)
                        .setMessage(R.string.one_line_message)
                        .setPositiveButton(va_ok, null)
                        .setNegativeButton(
                                va_cancel + " " + va_ok + " " + va_ok + " " + va_ok,
                                null)
                        .setNeutralButton(
                                getString(R.string.neutral) + " " + va_ok + " "
                                        + va_cancel + " " + va_ok + " " + va_cancel,
                                null).setPositiveButtonDisabled(true)
                        .setOnCancelListener(new OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface dialog) {
                                showToast(va_cancel + " " + oneLine_title);
                            }

                        }).create();
                oneLine_dialog.setOnShowListener(new OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {
                        try {
                            View shadow = ((ViewGroup) ((HtcAlertDialog) dialog)
                                    .findViewById(android.R.id.content))
                                    .getChildAt(0);
                            Method m = shadow.getClass().getMethod(
                                    "setForcePortraitWidth", boolean.class);
                            m.invoke(shadow, true);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }

                });
                mDialog = oneLine_dialog;
                return mDialog;

            case DIALOG_CHECK_MESSAGE:
                // test check box feature, and lengthy title text (more then 2
                // lines)
                // PS. according to designer Potter, should show "..." at the
                // end
                final String checkMessageTitle = getString(R.string.demo_checkbox)
                        + " ";
                final HtcAlertDialog.Builder chkMsgBuilder = new HtcAlertDialog.Builder(
                        mContext)
                        .setTitle(
                                checkMessageTitle + checkMessageTitle
                                        + checkMessageTitle + checkMessageTitle
                                        + checkMessageTitle + checkMessageTitle)
                        .setMessage(R.string.checkbox_description)
                        .setCheckBox(getString(R.string.dont_ask), false, null,
                                true)
                        .setPositiveButton(va_ok, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // do nothing
                            }

                        });
                mDialog = chkMsgBuilder.create();
                return mDialog;

            case DIALOG_CHECK_MESSAGE2:
                // test check box feature, and lengthy title text (more then 2
                // lines)
                // PS. according to designer Potter, should show "..." at the
                // end
                final String checkMessage2Title = getString(R.string.demo_checkbox)
                        + " ";
                final HtcAlertDialog.Builder chkMsg2Builder = new HtcAlertDialog.Builder(
                        mContext)
                        .setTitle(checkMessage2Title)
                        .setCheckBox(getString(R.string.dont_ask), false, null,
                                true)
                        .setPositiveButton(va_ok, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // do nothing
                            }

                        });
                mDialog = chkMsg2Builder.create();
                return mDialog;

                // ----------------------------------------------------------------------------

            case DIALOG_LIST:
                // Create a list dialog, 0 button
                mDialog = new HtcAlertDialog.Builder(mContext)
                        .setTitle(R.string.list_dialog_title)
                        .setItems(R.array.list_dialog_items1,
                                new OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        String[] items = res
                                                .getStringArray(R.array.list_dialog_items1);
                                        showToast("Your favorite animal is "
                                                + items[which]);
                                    }
                                }).setOnCancelListener(new OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface dialog) {
                                showToast(va_cancel + " "
                                        + getString(R.string.list_dialog_title));
                            }

                        }).create();
                return mDialog;

            case DIALOG_SINGLE_CHOICE:
                // Create a single choice dialog, 1 button
                mDialog = new HtcAlertDialog.Builder(mContext)
                        .setTitle(R.string.single_choice_list_dialog_title)
                        .setSingleChoiceItems(R.array.single_choice_dialog_items2,
                                0, new OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        String[] items = res
                                                .getStringArray(R.array.single_choice_dialog_items2);
                                        showToast("choosed: " + items[which]);
                                        // don't forget to dismiss the dialog
                                        // manually
                                        dialog.dismiss();
                                    }
                                }).setNeutralButton("More Info", null).create();
                return mDialog;

            case DIALOG_MULTIPLE_CHOICES:
                // init items
                final String[] multipleItems = res
                        .getStringArray(R.array.multiple_choice_dialog_items3);
                final boolean[] checkedItems = new boolean[multipleItems.length];
                final boolean[] lastChecked = Arrays.copyOf(checkedItems,
                        checkedItems.length);

                mDialog = new HtcAlertDialog.Builder(mContext)
                        .setTitle(R.string.multiple_choice_list_dialog_title)
                        .setMultiChoiceItems(R.array.multiple_choice_dialog_items3,
                                checkedItems,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int which, boolean isChecked) {
                                        // update temp data (multiple_checked)
                                        showToast(multipleItems[which]
                                                + (isChecked ? " checked"
                                                        : " not checked"));
                                    }
                                })
                        .setPositiveButton(R.string.alert_dialog_ok,
                                new OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int whichButton) {
                                        StringBuilder str = new StringBuilder();
                                        SparseBooleanArray itemPos = ((HtcAlertDialog) dialog)
                                                .getListView()
                                                .getCheckedItemPositions();
                                        for (int i = 0; i < itemPos.size(); ++i) {
                                            int pos = itemPos.keyAt(i);
                                            boolean isChecked = itemPos.valueAt(i);
                                            if (isChecked) {
                                                str.append(pos).append(":")
                                                        .append(multipleItems[pos])
                                                        .append("\n");
                                            }
                                        }
                                        showToast("ok: \n" + str);

                                        // save result
                                        System.arraycopy(checkedItems, 0,
                                                lastChecked, 0, lastChecked.length);
                                    }
                                })
                        .setNegativeButton(R.string.alert_dialog_cancel,
                                new OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int whichButton) {
                                        showToast(va_cancel
                                                + " "
                                                + getString(R.string.multiple_choice_list_dialog_title));
                                        // restore selections
                                        for (int i = 0; i < lastChecked.length; ++i) {
                                            checkedItems[i] = lastChecked[i];
                                            ((HtcAlertDialog) dialog).getListView()
                                                    .setItemChecked(i,
                                                            lastChecked[i]);
                                        }
                                    }

                                }).setOnCancelListener(new OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface dialog) {
                                showToast(va_cancel
                                        + " "
                                        + getString(R.string.multiple_choice_list_dialog_title));
                                // restore selections
                                for (int i = 0; i < lastChecked.length; ++i) {
                                    checkedItems[i] = lastChecked[i];
                                    ((HtcAlertDialog) dialog).getListView()
                                            .setItemChecked(i, lastChecked[i]);
                                }
                            }

                        }).create();
                return mDialog;

            case DIALOG_LONG_LIST:
                mDialog = new HtcAlertDialog.Builder(mContext)
                        .setTitle(R.string.long_list)
                        .setItems(R.array.long_list_dialog_items, null)
                        .setPositiveButton(R.string.alert_dialog_ok, null)
                        .setNegativeButton(R.string.alert_dialog_cancel, null)
                        .setNeutralButton(R.string.neutral, null).create();
                return mDialog;

            case DIALOG_CHECK_LIST:
                HtcCompoundButton.OnCheckedChangeListener listCheckedListener = new HtcCompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(HtcCompoundButton arg0,
                            boolean arg1) {
                        Log.d("henry", "onCheckedChanged=" + arg1);
                    }
                };
                final HtcAlertDialog.Builder chkListBuilder = new HtcAlertDialog.Builder(
                        mContext)
                        .setTitle(R.string.demo_checkbox)
                        .setItems(R.array.list_dialog_items1,
                                new OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        String[] items = res
                                                .getStringArray(R.array.list_dialog_items1);
                                        showToast("Your favorite animal is "
                                                + items[which]);
                                    }
                                })
                        .setCheckBox(getString(R.string.long_confirm_message),
                                true, listCheckedListener, false)
                        .setPositiveButton(R.string.alert_dialog_ok,
                                new OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0,
                                            int arg1) {
                                        // do nothing
                                    }

                                });
                mDialog = chkListBuilder.create();
                return mDialog;

            case DIALOG_FONT_SELECTOR:
                OnClickListener fontSizeListener = new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                };
                final HtcAlertDialog.Builder fontSelectorBuilder = new HtcAlertDialog.Builder(
                        mContext).setFontSizeSelector(2, fontSizeListener, 2)
                        .setTitle(R.string.font_size);
                mDialog = fontSelectorBuilder.create();
                return mDialog;

                // ----------------------------------------------------------------------------

            case DIALOG_HORIZONTAL_PROGRESS:
                // Create a progress dialog
                final HtcProgressDialog horizontalProgress = new HtcProgressDialog(
                        mContext);

                final Runnable runnable = new Runnable() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void run() {
                        mHandler.removeCallbacks(this);

                        if (horizontalProgress.getMax() > horizontalProgress
                                .getProgress() && !mCancelled) {
                            Log.d(TAG, "runnable: increment by 1");
                            horizontalProgress.incrementProgressBy(1);
                            mHandler.postDelayed(this, 10000);
                        } else {
                            Log.d(TAG, "runnable: complete. remove dialog");
                            showToast("Finish exporting contacts");
                            removeDialog(DIALOG_HORIZONTAL_PROGRESS);
                        }
                    }

                };

                horizontalProgress.setTitle(R.string.progress_dialog_title);
                horizontalProgress.setMessage(getString(R.string.export_contact));
                horizontalProgress
                        .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                horizontalProgress.setMax(MAX_PROGRESS);
                horizontalProgress.setProgress(0);
                horizontalProgress.setCancelable(false);
                horizontalProgress.setButton(DialogInterface.BUTTON_POSITIVE,
                        getString(R.string.run_in_background),
                        new OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                showToast("Continue exporting contact data in background...");
                            }
                        });
                horizontalProgress.setButton(DialogInterface.BUTTON_NEGATIVE,
                        getText(R.string.alert_dialog_cancel),
                        new OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                showToast("cancel. Exporting contact data...");
                                mCancelled = true;
                                mHandler.removeCallbacks(runnable);
                                Log.d(TAG,
                                        "cancel: remove callback and set progress to 0");
                                horizontalProgress.setProgress(0);
                            }
                        });
                horizontalProgress.setOnShowListener(new OnShowListener() {

                    @Override
                    public void onShow(DialogInterface arg0) {
                        mCancelled = false;
                        mHandler.post(runnable);
                        Log.d(TAG, "onShow: start runnable");
                    }

                });

                mDialog = horizontalProgress;
                return mDialog;

            case DIALOG_HORIZONTAL_NO_MESSAGE:
                // this case is used to demo the margins when no text message
                // set.
                final String horizontalNoMessage_title = getString(R.string.horizontal_progress_without_message);
                HtcProgressDialog mProgressDialogNoMessage = new HtcProgressDialog(
                        mContext);
                mProgressDialogNoMessage.setTitle(horizontalNoMessage_title);
                mProgressDialogNoMessage
                        .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialogNoMessage.setMax(MAX_PROGRESS);
                mProgressDialogNoMessage.setButton(DialogInterface.BUTTON_POSITIVE,
                        getString(com.htc.lib1.cc.R.string.va_hide),
                        new OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                showToast(getString(R.string.va_hide) + " "
                                        + horizontalNoMessage_title);
                            }
                        });
                mProgressDialogNoMessage.setButton(DialogInterface.BUTTON_NEGATIVE,
                        va_cancel, new OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                showToast(va_cancel + horizontalNoMessage_title);
                            }
                        });
                // mProgressDialogNoMessage.setProgress(23);
                mProgressDialogNoMessage.incrementProgressBy(23);
                mProgressDialogNoMessage.setOnShowListener(new OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {
                        // in case of max=100, the percent number seems
                        // redundant.
                        // you may remove it as below:
                        View percent_number = ((HtcProgressDialog) dialog)
                                .findViewById(android.R.id.text2);
                        percent_number.setVisibility(View.GONE);
                    }

                });
                mDialog = mProgressDialogNoMessage;
                return mDialog;

            case DIALOG_HORIZONTAL_LONG_MESSAGE:
                final String horizontalLong_title = getString(R.string.horizontal_progress_with_long_message);
                HtcProgressDialog mProgressDialogLongMessage = new HtcProgressDialog(
                        mContext);
                mProgressDialogLongMessage
                        .setMessage(getString(R.string.alert_dialog_long_message_content));
                mProgressDialogLongMessage.setTitle(horizontalLong_title);
                mProgressDialogLongMessage
                        .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialogLongMessage.setMax(MAX_PROGRESS);
                mProgressDialogLongMessage.setButton(
                        DialogInterface.BUTTON_POSITIVE,
                        getString(com.htc.lib1.cc.R.string.va_hide),
                        new OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                showToast("ok " + horizontalLong_title);
                            }
                        });
                mProgressDialogLongMessage.setButton(
                        DialogInterface.BUTTON_NEGATIVE,
                        getText(R.string.alert_dialog_cancel),
                        new OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                showToast(va_cancel + " " + horizontalLong_title);
                            }
                        });
                mProgressDialogLongMessage.setProgress(23);
                mDialog = mProgressDialogLongMessage;
                return mDialog;

            case DIALOG_SPINNER_PROGRESS:
                // create an interminable progress dialog
                MyHtcProgressDialog mSpinnerDialog = new MyHtcProgressDialog(mContext);
                mSpinnerDialog
                        .setMessage(getString(com.htc.lib1.cc.R.string.st_loading));
                mSpinnerDialog.setIndeterminate(false);
                mSpinnerDialog.setCancelable(true);
                mSpinnerDialog.setOnCancelListener(new OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        showToast(va_cancel + " "
                                + getString(com.htc.lib1.cc.R.string.st_loading));
                    }

                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mSpinnerDialog.setProgressBarVisibility(View.INVISIBLE);
                }
                return mSpinnerDialog;

            case DIALOG_SPINNER_LISTITEM:
                // create an interminable progress dialog
                final String listSpinner_title = getString(R.string.spinner_with_title);
                final HtcProgressDialog mListItemSpinnerDialog = new HtcProgressDialog(
                        mContext);
                mListItemSpinnerDialog.setTitle(listSpinner_title);
                mListItemSpinnerDialog
                        .setMessage(getString(R.string.titled_spinner_message));
                mListItemSpinnerDialog.setIndeterminate(true);
                mListItemSpinnerDialog.setCancelable(true);
                mListItemSpinnerDialog.setOnCancelListener(new OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        showToast(va_cancel + " " + listSpinner_title);
                    }

                });
                mListItemSpinnerDialog.setOnShowListener(new OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {
                        // following code is ONLY used to improve Emma Code
                        // Coverage
                        mListItemSpinnerDialog
                                .getButton(HtcProgressDialog.BUTTON_NEGATIVE);
                        mListItemSpinnerDialog.getPadding1();
                        mListItemSpinnerDialog.getPadding3();
                        mListItemSpinnerDialog.getListView();
                    }

                });
                mDialog = mListItemSpinnerDialog;
                return mDialog;

            case DIALOG_HORIZONTAL_NO_NUMBER:
                final String horizontalNoNumber_title = getString(R.string.horizontal_progress_without_number);
                HtcProgressDialog mProgressDialogNoNumber = new HtcProgressDialog(mContext);
                mProgressDialogNoNumber.setTitle(horizontalNoNumber_title);
                mProgressDialogNoNumber
                        .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialogNoNumber.setMax(MAX_PROGRESS);
                mProgressDialogNoNumber.setButton(DialogInterface.BUTTON_POSITIVE,
                        getString(com.htc.lib1.cc.R.string.va_hide),
                        new OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                showToast(getString(R.string.va_hide) + " " + horizontalNoNumber_title);
                            }
                        });
                mProgressDialogNoNumber.incrementProgressBy(23);
                mProgressDialogNoNumber.setOnShowListener(new OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {
                        ((HtcProgressDialog) dialog).setProgressNumberFormat(null);
                        ((HtcProgressDialog) dialog).setProgressPercentFormat(null);
                    }
                });
                return mProgressDialogNoNumber;

            case DIALOG_HORIZONTAL_NO_NUMBER_MINHEIGHT:
                final String title = getString(R.string.horizontal_progress_without_number_minheight);
                MyProgressDialog myProgressDialog = new MyProgressDialog(mContext);
                myProgressDialog.setTitle(title);
                myProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                myProgressDialog.setMax(MAX_PROGRESS);
                myProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                        getString(com.htc.lib1.cc.R.string.va_hide),
                        new OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                showToast(getString(R.string.va_hide) + " " + title);
                            }
                        });
                myProgressDialog.incrementProgressBy(23);
                return myProgressDialog;

            case DIALOG_AUTO_MESSAGE:
                // automotive notice and disclaimer
                final String autoMesg_title = getString(R.string.notice_n_disclaimer_title);
                mDialog = new HtcAlertDialog.Builder(mContext)
                        .setTitle(autoMesg_title)
                        .setMessage(R.string.notice_n_disclaimer)
                        .setIsAutoMotive(true)
                        .setOnCancelListener(new OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface arg0) {
                                showToast(va_cancel + " " + autoMesg_title);
                            }

                        })
                        .setPositiveButton(R.string.agree, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showToast("agreed " + autoMesg_title);
                            }

                        })
                        .setNegativeButton(R.string.disagree,
                                new OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        showToast("disagreed " + autoMesg_title);
                                    }

                                }).create();
                return mDialog;

            case DIALOG_AUTO_LIST:
                // Create a single choice dialog, one cancel button
                final String autoSingle_title = getString(R.string.fm_station);
                mDialog = new HtcAlertDialog.Builder(mContext)
                        .setTitle(autoSingle_title)
                        .setIsAutoMotive(true)
                        .setCheckBox(getString(R.string.dont_ask), false, null,
                                false)
                        .setItems(R.array.dialog_auto_list,
                                new OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        String[] items = res
                                                .getStringArray(R.array.dialog_auto_list);
                                        showToast("choosed: " + items[which]);
                                        dialog.dismiss();
                                    }
                                }).setOnCancelListener(new OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface arg0) {
                                showToast(va_cancel + " " + autoSingle_title);
                            }

                        }).create();
                return mDialog;

            case DIALOG_AUTO_MULTICHOICE:
                // Create a single choice dialog, one cancel button
                final String titleAutoMultichoice = getString(R.string.auto_multi_choice);
                mDialog = new HtcAlertDialog.Builder(mContext)
                        .setTitle(titleAutoMultichoice)
                        .setIsAutoMotive(true)
                        .setMultiChoiceItems(
                                R.array.multiple_choice_dialog_items3,
                                new boolean[] {
                                        false, true, false, true, false,
                                        false, false
                                },
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int which, boolean isChecked) {
                                        String[] items = res
                                                .getStringArray(R.array.multiple_choice_dialog_items3);
                                        showToast("clicked. "
                                                + titleAutoMultichoice + " "
                                                + items[which] + "=" + isChecked);
                                    }
                                })
                        .setPositiveButton(R.string.alert_dialog_ok,
                                new OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int whichButton) {
                                        StringBuilder checked = new StringBuilder();
                                        SparseBooleanArray itemPos = ((HtcAlertDialog) dialog)
                                                .getListView()
                                                .getCheckedItemPositions();
                                        String[] items = res
                                                .getStringArray(R.array.multiple_choice_dialog_items3);
                                        for (int i = 0; i < itemPos.size(); ++i) {
                                            int pos = itemPos.keyAt(i);
                                            if (itemPos.valueAt(i)) {
                                                checked.append(i).append(":")
                                                        .append(items[pos])
                                                        .append("\n");
                                            }
                                        }
                                        showToast("ok: \n" + checked);
                                    }
                                }).create();
                return mDialog;

            case DIALOG_AUTO_SINGLECHOICE:
                final String titleAutoSingleChoice = getString(R.string.auto_single_choice);
                mDialog = new HtcAlertDialog.Builder(mContext)
                        .setTitle(titleAutoSingleChoice)
                        .setIsAutoMotive(true)
                        .setSingleChoiceItems(R.array.single_choice_dialog_items2,
                                0, new OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        String[] items = res
                                                .getStringArray(R.array.single_choice_dialog_items2);
                                        showToast("choosed: " + items[which]);
                                        // don't forget to dismiss the dialog
                                        // manually
                                        dialog.dismiss();
                                    }
                                }).setNeutralButton("More Info", null).create();
                return mDialog;

            case DIALOG_AUTO_CHECKBOX:
                final HtcAlertDialog.Builder chkAutoBuilder = new HtcAlertDialog.Builder(
                        mContext)
                        .setTitle(R.string.checkbox_in_auto)
                        .setIsAutoMotive(true)
                        .setMessage(R.string.checkbox_in_auto_description)
                        .setCheckBox(getString(R.string.dont_ask), true, null, true)
                        .setPositiveButton(R.string.alert_dialog_ok,
                                new OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0,
                                            int arg1) {
                                        // do nothing
                                    }

                                });

                mDialog = chkAutoBuilder.create();
                return mDialog;

                // ----------------------------------------------------------------------------

            case DIALOG_TIE_MESSAGE:
                try {
                    Log.d("henry", "1");
                    // create builder
                    Class c = Class
                            .forName("com.htc.dialog.HtcAlertDialog$Builder");
                    Constructor constructor = c.getConstructor(Context.class);
                    Object builder = constructor.newInstance(mContext);

                    Log.d("henry", "2");
                    // set title
                    Method methodTitle = c
                            .getMethod("setTitle", CharSequence.class);
                    methodTitle.invoke(builder, "Demo check-box");

                    // Log.d("henry", "3");
                    // // set message
                    // Method methodMessage = c.getMethod("setMessage",
                    // CharSequence.class);
                    // methodMessage.invoke(builder,
                    // "dialog box content description.");

                    // set button
                    Method methodButton = c.getMethod("setPositiveButton",
                            CharSequence.class,
                            OnClickListener.class);
                    methodButton.invoke(builder, "OK", null);

                    Log.d("henry", "4");
                    // set checkbox
                    Method methodCheckBox = c.getMethod("setCheckBox",
                            CharSequence.class, boolean.class,
                            CompoundButton.OnCheckedChangeListener.class,
                            boolean.class);
                    methodCheckBox.invoke(builder, "do not mention this again!",
                            true, new CompoundButton.OnCheckedChangeListener() {

                                @Override
                                public void onCheckedChanged(
                                        CompoundButton buttonView, boolean isChecked) {

                                }
                            }, true);

                    // create
                    Log.d("henry", "5");
                    Method methodCreate = c.getMethod("create");
                    Object result = methodCreate.invoke(builder);
                    mDialog = (Dialog) result;
                    return mDialog;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

        }
        return null;
    }

    public void setScrollBarEnabled(boolean enable) {
        if (null == mDialog) {
            return;
        }
        final ScrollView sv = (ScrollView) mDialog.findViewById(com.htc.lib1.cc.R.id.scrollView);
        final HtcListView lv = (HtcListView) mDialog.findViewById(com.htc.lib1.cc.R.id.select_dialog_listview);
        if (null != sv) {
            sv.setVerticalScrollBarEnabled(false);
        }
        if (null != lv) {
            lv.setVerticalScrollBarEnabled(false);
        }
    }

    public void improveCoverage() {
        HtcAlertDialog dialog = new HtcAlertDialog.Builder(this).create();
        final Resources res = getResources();
        dialog.setButton("test", new Message());
        dialog.setButton2("test2", new Message());
        dialog.setButton3("test3", new Message());
        dialog.setCustomTitle(new TextView(this));
        dialog.setIcon(R.drawable.icon_btn_done_dark);
        dialog.setIcon(res.getDrawable(R.drawable.icon_btn_done_dark));
        dialog.setMessage("message", 0);
        dialog.setView(new TextView(this), 0, 0, 0, 0);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        dialog.getPadding1();
        dialog.getPadding3();
        dialog.getListView();
        dialog.onKeyDown(1, null);
        dialog.onKeyUp(1, null);
        dialog.setButton3("Button3", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.setIcon(getResources().getDrawable(R.drawable.icon_btn_done_dark));
        dialog.setIconAttribute(-1);
        dialog.setIconAttribute(R.attr.leftIcon);
        final CustomDialog customDialog = new CustomDialog(this);
        CustomDialog otherDialog = new CustomDialog(this, R.style.Theme_DeviceDefault);
        otherDialog = new CustomDialog(this, false, new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        customDialog.getPadding3();
        customDialog.getPadding1();
        dialog.setOnActionModeChangedListener(new OnActionModeChangedListener() {
            @Override
            public void onActionModeStarted(ActionMode mode) {
            }
        });
    }

    public void testImproveCoverageCheckBox() {
        final HtcAlertDialog dialog = new HtcAlertDialog.Builder(mContext)
                .setTitle(R.string.checkbox_in_auto)
                .setIsAutoMotive(true)
                .setMessage(R.string.checkbox_in_auto_description)
                .setCheckBox(getString(R.string.dont_ask), true, null, true)
                .create();
        dialog.show();
        dialog.isCheckBoxChecked();
        dialog.setCheckBoxChecked(true);
        dialog.setCheckBoxEnabled(true);
    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }

    private class MyHtcProgressDialog extends HtcProgressDialog {
        private int visibility = View.VISIBLE;

        public MyHtcProgressDialog(Context context) {
            super(context);
        }

        @Override
        public void setView(View view) {
            ProgressBar progressBar = (ProgressBar) view.findViewById(android.R.id.progress);
            if (progressBar != null) {
                progressBar.setVisibility(visibility);
            }
            super.setView(view);
        }

        public void setProgressBarVisibility(int v) {
            visibility = v;
        }
    }

    private class MyProgressDialog extends HtcProgressDialog {

        public MyProgressDialog(Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            TextView progress_number = (TextView) findViewById(R.id.progress_percent);
            if (null != progress_number) {
                progress_number.setVisibility(View.GONE);
            }
            TextView progress_p = (TextView) findViewById(android.R.id.text2);
            if (null != progress_p) {
                progress_p.setVisibility(View.GONE);
            }
            View v = findViewById(R.id.custom);
            if (null != v) {

                v.setMinimumHeight(0);
            }
        }

    }

    private class CustomDialog extends HtcAlertDialog {
        protected CustomDialog(Context context) {
            super(context);
            mAlert = null;
        }

        public CustomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        public CustomDialog(Context context, int theme) {
            super(context, theme);
        }
    }
}
