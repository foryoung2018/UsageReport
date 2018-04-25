/*
 * Copyright (C) 2009 HTC Inc.
 */

package com.htc.sense.commoncontrol.demo.alertdialog;

import java.util.Arrays;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.htc.lib1.cc.app.HtcProgressDialog;
import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.lib1.cc.widget.HtcCompoundButton;
import com.htc.lib1.cc.widget.HtcEditText;
import com.htc.lib1.cc.widget.ShadowLinearLayout;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

/**
 * Demo different variations of HtcAlertDialog, HtcProgressDialog and
 * HtcAlertActivity
 *
 * [About theme/style]
 *
 * When using HtcAlertDialog, HtcProgerssDialog and/or HtcAlertActivity, please
 * make sure the <uses-sdk> and theme are set correctly! Do not apply custom
 * theme/style or the appearance/skin might not work as expected.
 *
 * Normally, you should not have to worry about these, but if you are using your
 * own (customized) theme and/or SDK version, please check: 1.
 * AndroidManifest.xml for tag <uses-sdk> and attribute "android:theme" 2.
 * function call "setTheme" in your code
 *
 * if you still have any problem, welcome to contact Common Control team for
 * further information.
 *
 * [About linger dialog and related issues]
 *
 * Please do use "managed" dialogs, which means you shall invoke a dialog by 1.
 * activity.showDialog (deprecated) 2. extends DialogFragment Because a managed
 * dialog will automatically dismiss/destroy and/or show on configuration change
 * or automatically handle some activity life cycle events.
 *
 * [About cancel/back behavior]
 *
 * Please do not show "cancel" button if not necessary. Since in Android, users
 * use HW-BACK to cancel actions. This makes the UI more clean and the UX is
 * more consistent.
 *
 * [About preventing dismiss on accident]
 *
 * A dialog will dismiss if you tap out side of the dialog, by default. If there
 * is any concerns, you may use following 2 methods to inhibit it: 1.
 * builder.setCancelable 2. dialog.setCanceledOnTouchOutside
 *
 * [About change selected item]
 *
 * You may change selected item later in a list dialog, by: ListView listView =
 * dialog.getListView(); listView.setItemChecked(pos, true);
 * listView.setSelection(pos);
 *
 * edited by HenryCY_Lee on Jun 11 2012 revised by HenryCY_Lee on 19 Mar 2013
 * revised by HenryCY_Lee on 3 May 2013
 *
 */
public class AlertDialogDemo extends CommonDemoActivityBase implements
        android.view.View.OnClickListener {

    // TODO every category should test following combinations:
    //      no title, 1 line title, 2 lines title, 3 lines title=many lines (because title has 2 lines limitation)
    //      no line/item (empty), 1 line/item, 2 lines/items, 3 lines/items, many lines/items (long, pages, *)
    //      no check panel, 1 line check panel, 2 lines check panel, 3 lines check panel, many lines check panel
    //      no button, 1 button, 2 buttons, 3 buttons$
    //      *. special for list item: 0 line text (empty), 1 line text, 2 lines text, 3 lines text, many lines text
    //      $. special for button text: 1 line text, 2 lines text, 3 lines text=many lines (because of 2 lines limitation)
    private static final String TAG = "AlertDialogDemo";

    // main(top-level) 5 categories
    private static final int DIALOG_ALL_TEXT_DIALOGS = 1; // text message dialogs
    private static final int DIALOG_ALL_LIST_DIALOGS = 2; // list-view dialogs
    private static final int DIALOG_ALL_PROGRESS_DIALOGS = 3; // progress dialogs
    private static final int DIALOG_ALL_AUTOMOTIVE_DIALOGS = 4; // automotive dialogs
    private static final int DIALOG_ALL_ACTIVITY_DIALOGS = 5; // alert activities

    // 1. text message dialogs
    private static final int DIALOG_ACKNOWLEDGEMENT = 11;  // 1-line title, 2 lines text, 2-lines check, 1 button
    private static final int DIALOG_QUESTION = 12;         // 1-line title, 3 lines text, 3-lines check, 2 buttons
    private static final int DIALOG_LEGAL = 13;            // 1-line title, n+ lines text,      no check, 0 button
    private static final int DIALOG_ONE_LINE = 14;         // 2-lines title, 1 line text,       no check, 3 buttons
    private static final int DIALOG_CHECK_MESSAGE = 15;    //     no title, n lines text, n-lines check, NA
    private static final int DIALOG_CHECK_ONLY = 16;       // 3-lines title, 0 line text (no text), 1-line check, NA

    // 2. list-view dialogs
    private static final int DIALOG_LIST = 21;              // 1-line title, list dialog (1-page), 0 button
    private static final int DIALOG_SINGLE_CHOICE = 22;    // 1-line title, single choice dialog (1 item), 1 cancel button
    private static final int DIALOG_MULTIPLE_CHOICES = 23; // 2-line title, multiple choices dialog (many choices, more than one page), 2 buttons
    private static final int DIALOG_LONG_LIST = 24;         // 3-line title, long list (more than one page), 3 buttons
    private static final int DIALOG_FONT_SELECTOR = 25;    // 1-line title, pre-defined font selector dialog, 0 button
    private static final int DIALOG_CHECK_MULTIPLE = 26;   //     no title, list view (a few items), check, 1 button

    // 3. progress dialogs
    private static final int DIALOG_SPINNER_PROGRESS = 31; // spinner progress dialog with no title
    private static final int DIALOG_SPINNER_LISTITEM = 32; // spinner progress dialog with title, which shall transform to list view style
    private static final int DIALOG_BAR_PROGRESS = 33; // normal horizontal bar progress dialog
    private static final int DIALOG_BAR_NO_MESSAGE = 34; // horizontal progress dialog with no text message
    private static final int DIALOG_BAR_LONG_MESSAGE = 35; // horizontal progress dialog with long text message

    // 4. automotive dialogs
    private static final int DIALOG_AUTO_MESSAGE = 41;
    private static final int DIALOG_AUTO_LIST = 42;
    private static final int DIALOG_AUTO_SINGLECHOICE = 43;
    private static final int DIALOG_AUTO_MULTICHOICE = 44;
    private static final int DIALOG_AUTO_CHECK_ONLY = 45;
    private static final int DIALOG_AUTO_CHECK_MESSAGE = 46;
    private static final int DIALOG_AUTO_CHECK_LIST = 47;

    // 5. alert activities
    private static final int DIALOG_ACTIVITY_MESSAGE = 51;
    private static final int DIALOG_ACTIVITY_LIST = 52;

    // lists for top 3 categories
    private static final int[] IDS_ALL_TEXT_DIALOGS = {
            DIALOG_ACKNOWLEDGEMENT, DIALOG_QUESTION, DIALOG_LEGAL,
            DIALOG_ONE_LINE, DIALOG_CHECK_MESSAGE, DIALOG_CHECK_ONLY };

    private static final int[] IDS_ALL_LIST_DIALOGS = { DIALOG_LIST,
            DIALOG_SINGLE_CHOICE, DIALOG_MULTIPLE_CHOICES, DIALOG_LONG_LIST,
            DIALOG_FONT_SELECTOR, DIALOG_CHECK_MULTIPLE };

    private static final int[] IDS_ALL_PROGRESS_DIALOGS = {
            DIALOG_SPINNER_PROGRESS, DIALOG_SPINNER_LISTITEM,
            DIALOG_BAR_PROGRESS, DIALOG_BAR_NO_MESSAGE, DIALOG_BAR_LONG_MESSAGE };

    private static final int[] IDS_ALL_AUTOMOTIVE_DIALOGS = {
            DIALOG_AUTO_MESSAGE, DIALOG_AUTO_LIST, DIALOG_AUTO_SINGLECHOICE, DIALOG_AUTO_MULTICHOICE,
            DIALOG_AUTO_CHECK_ONLY, DIALOG_AUTO_CHECK_MESSAGE, DIALOG_AUTO_CHECK_LIST };

    private static final int MAX_PROGRESS = 100; // The maximum progress of ProgressDialog
    private final Handler mHandler = new Handler(); // used to update the progress
    private boolean mCancelled = false;
    private ToggleButton mToastToggle;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog_layout);

        // disable toast for accessibility exam
        mToastToggle = (ToggleButton) findViewById(R.id.showToasts);
    }

    private void showToast(String message) {
        if (mToastToggle.isChecked())
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("deprecation")
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.text_dialog_btn: // TODO change to DialogFragment
            showDialog(DIALOG_ALL_TEXT_DIALOGS);
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

        case R.id.activity_dialog_btn:
            showDialog(DIALOG_ALL_ACTIVITY_DIALOGS);
            break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final Resources res = getResources();
        final String va_ok = getString(R.string.va_ok);
        final String va_help = getString(R.string.va_help);

        switch (id) {

        case DIALOG_ALL_TEXT_DIALOGS:
            // Create a list dialog
            return new HtcAlertDialog.Builder(mContext)
                    .setTitle(R.string.all_text_dialogs)
                    .setItems(R.array.text_dialog_items,
                            new DialogInterface.OnClickListener() {
                                @SuppressWarnings("deprecation")
                                public void onClick(DialogInterface dialog, int which) {
                                    showDialog(IDS_ALL_TEXT_DIALOGS[which]);
                                }
                            })
                    .create();

        case DIALOG_ALL_LIST_DIALOGS:
            // Create a list dialog
            return new HtcAlertDialog.Builder(mContext)
                    .setTitle(R.string.all_list_dialogs)
                    .setItems(R.array.list_dialog_items,
                            new DialogInterface.OnClickListener() {
                                @SuppressWarnings("deprecation")
                                public void onClick(DialogInterface dialog, int which) {
                                    showDialog(IDS_ALL_LIST_DIALOGS[which]);
                                }
                            })
                    .create();

        case DIALOG_ALL_PROGRESS_DIALOGS:
            // Create a list dialog
            return new HtcAlertDialog.Builder(mContext)
                    .setTitle(R.string.all_progress_dialogs)
                    .setItems(R.array.progress_dialog_items,
                            new DialogInterface.OnClickListener() {
                                @SuppressWarnings("deprecation")
                                public void onClick(DialogInterface dialog, int which) {
                                    showDialog(IDS_ALL_PROGRESS_DIALOGS[which]);
                                }
                            })
                    .create();

        case DIALOG_ALL_AUTOMOTIVE_DIALOGS:
            // Create a list dialog
            return new HtcAlertDialog.Builder(mContext)
                    .setTitle(R.string.all_automotive_dialogs)
                    .setItems(R.array.automotive_dialog_items,
                            new DialogInterface.OnClickListener() {
                                @SuppressWarnings("deprecation")
                                public void onClick(DialogInterface dialog, int which) {
                                    showDialog(IDS_ALL_AUTOMOTIVE_DIALOGS[which]);
                                }
                            })
                    .create();

        case DIALOG_ALL_ACTIVITY_DIALOGS:
            // Create a list dialog
            return new HtcAlertDialog.Builder(mContext)
                    .setTitle(R.string.all_activity_dialogs)
                    .setItems(R.array.activity_dialog_items,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent;
                                    switch (which) {
                                    case 0:
                                        // text message alert activity
                                        intent = new Intent(AlertDialogDemo.this, TestAlertActivity.class);
                                        AlertDialogDemo.this.startActivityForResult(intent, DIALOG_ACTIVITY_MESSAGE);
                                        break;
                                    case 1:
                                        // list view alert activity
                                        intent = new Intent(AlertDialogDemo.this, ListAlertActivity.class);
                                        AlertDialogDemo.this.startActivityForResult(intent, DIALOG_ACTIVITY_LIST);
                                        break;
                                    }
                                }
                            })
                    .create();

            // -----------------------------------------------------------------------------------

        case DIALOG_ACKNOWLEDGEMENT:
            // Create a short message (about 2 lines) dialog with 1 OK button (1221)
            final String ack_title = getString(R.string.delete_duplicate_contacts);
            return new HtcAlertDialog.Builder(mContext)
                    .setTitle(ack_title)
                    .setMessage(R.string.less_than_two)
                    .setCheckBox(getString(R.string.ask_no_more_2), true, null, true)
                    .setPositiveButton(va_ok, new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            showToast(va_ok + " " + ack_title);
                        }

                    })
                    .setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface arg0) {
                            showToast(va_help + " " + ack_title);
                        }

                    })
                    .create();

        case DIALOG_QUESTION:
            // Create a question (longer, about 3 lines) dialog with OK and Cancel buttons (1332)
            final String question_title = getString(R.string.remove_account);
            return new HtcAlertDialog.Builder(mContext)
                    .setTitle(question_title + "?")
                    .setMessage(R.string.remove_account_warning)
                    .setCheckBox(getString(R.string.ask_no_more_3), false, null, true)
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
                    .setNegativeButton(R.string.more_info, new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showToast(getString(R.string.more_info) + " " + question_title);
                        }
                    })
                    .setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface arg0) {
                            showToast(va_help + " " + question_title);
                        }

                    })
                    .create();

        case DIALOG_LEGAL:
            // super long message with no button (1n00)
            return new HtcAlertDialog.Builder(mContext)
                    .setTitle(R.string.htc_legal)
                    .setMessage(R.string.alert_dialog_long_message_content)
                    .setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface arg0) {
                            showToast(va_help + " " + getString(R.string.htc_legal));
                        }

                    })
                    .create();

        case DIALOG_ONE_LINE:
            // for test vertical alignment only, also test 2 lines of title text
            // also test long button text (2 lines and more) (2103)
            final String neutral = getString(R.string.neutral);
            HtcAlertDialog oneLine_dialog = new HtcAlertDialog.Builder(mContext)
                    .setTitle(R.string.center_align)
                    .setMessage(R.string.one_line_message)
                    .setPositiveButton(va_ok, null)
                    .setNegativeButton(va_help + " " + va_help + " " + va_help + " " + va_help, null)
                    .setNeutralButton(neutral + " " + neutral + " " + neutral + " " + neutral + " " + neutral + " " + neutral + " " + neutral, null)
                    .setPositiveButtonDisabled(true)
                    .setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            showToast(va_help + " " + getString(R.string.center_align));
                        }

                    })
                    .create();
            oneLine_dialog.setOnShowListener(new OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {
                    try {
                        // TODO call directly.
                        View shadow = ((ViewGroup) ((HtcAlertDialog) dialog).findViewById(android.R.id.content))
                                .getChildAt(0);
                        ((ShadowLinearLayout) shadow).setLayoutArg(ShadowLinearLayout.KEEP_PORTRAIT_WIDTH);
//                        Method m = shadow.getClass().getMethod("setForcePortraitWidth", boolean.class);
//                        m.invoke(shadow, true);
//                        Method m = shadow.getClass().getMethod("setLayoutArg", int.class);
//                        m.invoke(shadow, ShadowLinearLayout.KEEP_PORTRAIT_WIDTH);
//                    } catch (NoSuchMethodException e) {
//                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    } catch (InvocationTargetException e) {
//                        e.printStackTrace();
                    }
                }

            });
            return oneLine_dialog;

        case DIALOG_CHECK_MESSAGE:
            // test check box feature, and lengthy title text (more then 2 lines)
            // PS. according to designer Potter, should show "..." at the end (0nnx)
            final String checkDescription = getString(R.string.checkbox_description);
            final String dontAsk = getString(R.string.dont_ask);
            return new HtcAlertDialog.Builder(mContext)
                    .setMessage(checkDescription + checkDescription + checkDescription + checkDescription + "(0nnx)")
                    .setCheckBox(dontAsk + dontAsk + dontAsk + dontAsk + dontAsk + dontAsk + dontAsk, false, null, true)
                    .setPositiveButton(va_ok, null).create();

        case DIALOG_CHECK_ONLY:
            // 301x
            final String demo_checkbox = getString(R.string.demo_checkbox);
                HtcAlertDialog dialog = new HtcAlertDialog.Builder(mContext)
                        .setView(new HtcEditText(this))
                        .setTitle(demo_checkbox + demo_checkbox + demo_checkbox + demo_checkbox + demo_checkbox + demo_checkbox + demo_checkbox)
                        .setCheckBox(getString(R.string.dont_ask), false, null, true)
                        .setPositiveButton(va_ok,
                                new OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // do nothing
                                    }

                                }).create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                return dialog;

            // ----------------------------------------------------------------------------

        case DIALOG_LIST:
            // Create a list dialog, 1 page, 0 button
            return new HtcAlertDialog.Builder(mContext)
                    .setTitle(R.string.list_dialog_title)
                    .setItems(R.array.list_dialog_items1,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String[] items = res.getStringArray(R.array.list_dialog_items1);
                                    showToast("Your favorite animal is " + items[which]);
                                }
                            })
                    .setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            showToast(va_help + " " + getString(R.string.list_dialog_title));
                        }

                    })
                    .create();

        case DIALOG_SINGLE_CHOICE:
            // Create a single choice dialog,1 item, 1 button
            return new HtcAlertDialog.Builder(mContext)
                    .setTitle(R.string.single_choice_list_dialog_title)
                    .setSingleChoiceItems(R.array.single_choice_dialog_items2, 0,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String[] items = res.getStringArray(R.array.single_choice_dialog_items2);
                                    showToast("choosed: " + items[which]);
                                    // don't forget to dismiss the dialog
                                    // manually
                                    dialog.dismiss();
                                }
                            })
                    .setNeutralButton(R.string.more_info, null)
                    .create();

        case DIALOG_MULTIPLE_CHOICES:
            // init items
            final String[] multipleItems = res.getStringArray(R.array.multiple_choice_dialog_items3);
            final boolean[] checkedItems = new boolean[multipleItems.length];
            final boolean[] lastChecked = Arrays.copyOf(checkedItems, checkedItems.length);

            return new HtcAlertDialog.Builder(mContext)
                    .setTitle(R.string.multiple_choice_list_dialog_title)
                    .setMultiChoiceItems(R.array.multiple_choice_dialog_items3, checkedItems,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    // update temp data (multiple_checked)
                                    showToast(multipleItems[which] + (isChecked ? " checked" : " not checked"));
                                }
                            })
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    StringBuilder str = new StringBuilder();
                                    SparseBooleanArray itemPos = ((HtcAlertDialog) dialog).getListView()
                                            .getCheckedItemPositions();
                                    for (int i = 0; i < itemPos.size(); ++i) {
                                        int pos = itemPos.keyAt(i);
                                        boolean isChecked = itemPos.valueAt(i);
                                        if (isChecked) {
                                            str.append(pos).append(":").append(multipleItems[pos]).append("\n");
                                        }
                                    }
                                    showToast("ok: \n" + str);

                                    // save result
                                    System.arraycopy(checkedItems, 0, lastChecked, 0, lastChecked.length);
                                }
                            })
                    .setNegativeButton(R.string.alert_dialog_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    showToast(va_help + " " + getString(R.string.multiple_choice_list_dialog_title));
                                    // restore selections
                                    for (int i = 0; i < lastChecked.length; ++i) {
                                        checkedItems[i] = lastChecked[i];
                                        ((HtcAlertDialog) dialog).getListView().setItemChecked(i, lastChecked[i]);
                                    }
                                }

                            })
                    .setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            showToast(va_help + " " + getString(R.string.multiple_choice_list_dialog_title));
                            // restore selections
                            for (int i = 0; i < lastChecked.length; ++i) {
                                checkedItems[i] = lastChecked[i];
                                ((HtcAlertDialog) dialog).getListView().setItemChecked(i, lastChecked[i]);
                            }
                        }

                    })
                    .create();

        case DIALOG_LONG_LIST:
            return new HtcAlertDialog.Builder(mContext).setTitle(R.string.long_list)
                    .setItems(R.array.long_list_dialog_items, null)
                    .setPositiveButton(R.string.alert_dialog_ok, null)
                    .setNegativeButton(R.string.alert_dialog_cancel, null)
                    .setNeutralButton(R.string.neutral, null)
                    .create();

        case DIALOG_FONT_SELECTOR:
            OnClickListener fontSizeListener = new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // assert this is the font selector
                    // switch (which) {
                    // case HtcWrapConfiguration.FONTSIZE_LARGE:
                    // // do what you want here
                    // break;
                    // case HtcWrapConfiguration.FONTSIZE_LARGEST:
                    // break;
                    // case HtcWrapConfiguration.FONTSIZE_SMALL:
                    // break;
                    // case HtcWrapConfiguration.FONTSIZE_NORMAL:
                    // default:
                    // // others
                    // break;
                    // }
                }
            };

            final HtcAlertDialog.Builder fontSelectorBuilder = new HtcAlertDialog.Builder(mContext)
                    .setFontSizeSelector(2, fontSizeListener, 2)
                    .setTitle(R.string.font_size);
            return fontSelectorBuilder.create();

        case DIALOG_CHECK_MULTIPLE:
            // one page, one button, with check box
            HtcCompoundButton.OnCheckedChangeListener listCheckedListener = new HtcCompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(HtcCompoundButton arg0, boolean arg1) {
                    Log.d("henry", "onCheckedChanged=" + arg1);
                }
            };
            final HtcAlertDialog.Builder chkListBuilder = new HtcAlertDialog.Builder(mContext)
                    .setTitle(R.string.list_dialog_title)
                    .setMultiChoiceItems(R.array.list_dialog_items1, null, null)
                    .setCheckBox(getString(R.string.long_confirm_message), true, listCheckedListener, false)
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    // do nothing
                                }

                            });
            return chkListBuilder.create();

            // ----------------------------------------------------------------------------

        case DIALOG_BAR_PROGRESS:
            // Create a progress dialog
            final HtcProgressDialog horizontalProgress = new HtcProgressDialog(mContext);

            final Runnable runnable = new Runnable() {

                @SuppressWarnings("deprecation")
                @Override
                public void run() {
                    mHandler.removeCallbacks(this);

                    if (horizontalProgress.getMax() > horizontalProgress.getProgress() && !mCancelled) {
                        Log.d(TAG, "runnable: increment by 1");
                        horizontalProgress.incrementProgressBy(1);
                        mHandler.postDelayed(this, 300);
                    } else {
                        Log.d(TAG, "runnable: complete. remove dialog");
                        showToast("Finish exporting contacts");
                        removeDialog(DIALOG_BAR_PROGRESS);
                    }
                }

            };

            horizontalProgress.setTitle(R.string.progress_dialog_title);
            horizontalProgress.setMessage(getString(R.string.export_contact));
            horizontalProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            horizontalProgress.setMax(MAX_PROGRESS);
            horizontalProgress.setProgress(0);
            horizontalProgress.setCancelable(false);
            horizontalProgress.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.run_in_background),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                int whichButton) {
                            showToast("Continue exporting contact data in background...");
                        }
                    });
            horizontalProgress.setButton(DialogInterface.BUTTON_NEGATIVE,
                    getText(R.string.alert_dialog_cancel),
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                int whichButton) {
                            showToast("cancel. Exporting contact data...");
                            mCancelled = true;
                            mHandler.removeCallbacks(runnable);
                            Log.d(TAG, "cancel: remove callback and set progress to 0");
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

            return horizontalProgress;

        case DIALOG_BAR_NO_MESSAGE:
            // this case is used to demo the margins when no text message set.
            final String horizontalNoMessage_title = getString(R.string.horizontal_progress_without_message);
            HtcProgressDialog mProgressDialogNoMessage = new HtcProgressDialog(mContext);
            mProgressDialogNoMessage.setTitle(horizontalNoMessage_title);
            mProgressDialogNoMessage.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialogNoMessage.setMax(MAX_PROGRESS);
            mProgressDialogNoMessage.setButton(DialogInterface.BUTTON_POSITIVE,
                    getString(com.htc.lib1.cc.R.string.va_hide),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            showToast(getString(R.string.va_hide) + " " + horizontalNoMessage_title);
                        }
                    });
            mProgressDialogNoMessage.setButton(DialogInterface.BUTTON_NEGATIVE,
                    va_help,
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            showToast(va_help + horizontalNoMessage_title);
                        }
                    });
            // mProgressDialogNoMessage.setProgress(23);
            mProgressDialogNoMessage.incrementProgressBy(23);
            mProgressDialogNoMessage.setOnShowListener(new OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {
                    // in case of max=100, the percent number seems redundant.
                    // you may remove it as below:
                    View percent_number = ((HtcProgressDialog) dialog).findViewById(android.R.id.text2);
                    percent_number.setVisibility(View.GONE);
                }

            });
            return mProgressDialogNoMessage;

        case DIALOG_BAR_LONG_MESSAGE:
            final String horizontalLong_title = getString(R.string.horizontal_progress_with_long_message);
            HtcProgressDialog mProgressDialogLongMessage = new HtcProgressDialog(mContext);
            mProgressDialogLongMessage.setMessage(getString(R.string.alert_dialog_long_message_content));
            mProgressDialogLongMessage.setTitle(horizontalLong_title);
            mProgressDialogLongMessage.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialogLongMessage.setMax(MAX_PROGRESS);
            mProgressDialogLongMessage.setButton(DialogInterface.BUTTON_POSITIVE,
                    getString(com.htc.lib1.cc.R.string.va_hide),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            showToast("ok " + horizontalLong_title);
                        }
                    });
            mProgressDialogLongMessage.setButton(DialogInterface.BUTTON_NEGATIVE,
                    getText(R.string.alert_dialog_cancel),
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            showToast(va_help + " " + horizontalLong_title);
                        }
                    });
            mProgressDialogLongMessage.setProgress(23);
            return mProgressDialogLongMessage;

        case DIALOG_SPINNER_PROGRESS:
            // create an interminable progress dialog
            HtcProgressDialog mSpinnerDialog = new HtcProgressDialog(mContext);
            mSpinnerDialog.setMessage(getString(com.htc.lib1.cc.R.string.st_loading));
            mSpinnerDialog.setIndeterminate(true);
            mSpinnerDialog.setCancelable(true);
            mSpinnerDialog.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    showToast(va_help + " " + getString(com.htc.lib1.cc.R.string.st_loading));
                }

            });
            return mSpinnerDialog;

        case DIALOG_SPINNER_LISTITEM:
            // create an interminable progress dialog
            final String listSpinner_title = getString(R.string.spinner_with_title);
            final HtcProgressDialog mListItemSpinnerDialog = new HtcProgressDialog(mContext);
            mListItemSpinnerDialog.setTitle(listSpinner_title);
            mListItemSpinnerDialog.setMessage(getString(R.string.titled_spinner_message));
            mListItemSpinnerDialog.setIndeterminate(true);
            mListItemSpinnerDialog.setCancelable(true);
            mListItemSpinnerDialog.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    showToast(va_help + " " + listSpinner_title);
                }

            });
            mListItemSpinnerDialog.setOnShowListener(new OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {
                    // following code is ONLY used to improve Emma Code Coverage
                    mListItemSpinnerDialog.getButton(HtcProgressDialog.BUTTON_NEGATIVE);
                    mListItemSpinnerDialog.getPadding1();
                    mListItemSpinnerDialog.getPadding3();
                    mListItemSpinnerDialog.getListView();
                }

            });
            return mListItemSpinnerDialog;

            // ----------------------------------------------------------------------------

        case DIALOG_AUTO_MESSAGE:
            // automotive notice and disclaimer
            final String autoMesg_title = getString(R.string.notice_n_disclaimer_title);
            return new HtcAlertDialog.Builder(mContext)
                    .setTitle(autoMesg_title)
                    .setMessage(R.string.notice_n_disclaimer)
                    .setIsAutoMotive(true)
                    .setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface arg0) {
                            showToast(va_help + " " + autoMesg_title);
                        }

                    }).setPositiveButton(R.string.agree, new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showToast("agreed " + autoMesg_title);
                        }

                    })
                    .setNegativeButton(R.string.disagree, new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showToast("disagreed " + autoMesg_title);
                        }

                    }).create();

        case DIALOG_AUTO_LIST:
            final String titleAutoSingleChoice = getString(R.string.auto_single_choice);
            return new HtcAlertDialog.Builder(mContext)
                    .setTitle(titleAutoSingleChoice)
                    .setIsAutoMotive(true)
                    .setItems(R.array.single_choice_dialog_items2, null)
                    .setNeutralButton(R.string.more_info, null)
                    .create();

        case DIALOG_AUTO_SINGLECHOICE:
            // Create a single choice dialog, one cancel button
            final String autoSingle_title = getString(R.string.fm_station);
            return new HtcAlertDialog.Builder(mContext)
                    .setTitle(autoSingle_title)
                    .setIsAutoMotive(true)
                    .setSingleChoiceItems(R.array.dialog_auto_list, 0,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String[] items = res.getStringArray(R.array.dialog_auto_list);
                                    showToast("choosed: " + items[which]);
                                    dialog.dismiss();
                                }
                            })
                    .setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface arg0) {
                            showToast(va_help + " " + autoSingle_title);
                        }

                    }).create();

        case DIALOG_AUTO_MULTICHOICE:
            // Create a single choice dialog, one cancel button
            final String titleAutoMultichoice = getString(R.string.auto_multi_choice);
            return new HtcAlertDialog.Builder(mContext)
                    .setTitle(titleAutoMultichoice)
                    .setIsAutoMotive(true)
                    .setMultiChoiceItems(R.array.multiple_choice_dialog_items3,
                            new boolean[] { false, true, false, true, false, false, false },
                            new DialogInterface.OnMultiChoiceClickListener() {
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    String[] items = res.getStringArray(R.array.multiple_choice_dialog_items3);
                                    showToast("clicked. " + titleAutoMultichoice + " " + items[which] + "=" + isChecked);
                                }
                            })
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    StringBuilder checked = new StringBuilder();
                                    SparseBooleanArray itemPos = ((HtcAlertDialog) dialog).getListView()
                                            .getCheckedItemPositions();
                                    String[] items = res.getStringArray(R.array.multiple_choice_dialog_items3);
                                    for (int i = 0; i < itemPos.size(); ++i) {
                                        int pos = itemPos.keyAt(i);
                                        if (itemPos.valueAt(i)) {
                                            checked.append(i).append(":").append(items[pos]).append("\n");
                                        }
                                    }
                                    showToast("ok: \n" + checked);
                                }
                            })
                    .create();

        case DIALOG_AUTO_CHECK_ONLY:
            return (new HtcAlertDialog.Builder(mContext))
                    .setTitle(R.string.auto_check_only)
                    .setIsAutoMotive(true)
                    .setCheckBox(getString(R.string.dont_ask), true, null, true)
                    .setPositiveButton(R.string.alert_dialog_ok, null)
                    .create();

        case DIALOG_AUTO_CHECK_MESSAGE:
            final HtcAlertDialog.Builder chkAutoBuilder = new HtcAlertDialog.Builder(mContext)
                    .setTitle(R.string.checkbox_in_auto)
                    .setIsAutoMotive(true)
                    .setMessage(R.string.checkbox_in_auto_description)
                    .setCheckBox(getString(R.string.dont_ask), true, null, true)
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    // do nothing
                                }

                            });

            return chkAutoBuilder.create();
        case DIALOG_AUTO_CHECK_LIST:
            return (new HtcAlertDialog.Builder(mContext))
                    .setIsAutoMotive(true)
                    .setTitle(R.string.auto_check_only)
                    .setItems(R.array.list_dialog_items1, null)
                    .setCheckBox(getString(R.string.dont_ask), true, null, true)
                    .setPositiveButton(R.string.alert_dialog_ok, null)
                    .create();
        }

        return null;
    }

}
