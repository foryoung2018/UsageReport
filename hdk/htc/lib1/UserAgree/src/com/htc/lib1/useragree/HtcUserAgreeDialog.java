package com.htc.lib1.useragree;

import com.htc.lib0.customization.HtcWrapCustomizationManager;
import com.htc.lib0.customization.HtcWrapCustomizationReader;
import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.lib1.cc.widget.HtcCompoundButton;
import com.htc.lib1.cc.widget.HtcAlertDialog.Builder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class HtcUserAgreeDialog {
	/**
     * The negative return result for caller
     */
	public final static int RESULT_NO = 0;
	/**
     * The positive return result for caller
     */
	public final static int RESULT_YES = 1;

	private static final boolean DEBUG = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;
	private static final String TAG = "[UserAgree]";
	
	private final static String SHOW_USER_AGREE_DIALOG = "show_user_agree_dialog";
	private static String message;
	private static String checkboxLabel;
	private static String positiveLabel;
	private static String negativeLabel;
	private static boolean bCheckboxSelected;
	private static HtcAlertDialog mAlertDialog;
	/**
     * This is to launch a HtcUserAgreeDialog.
     *
     * @param context caller context
     * @param OnUserClickListener callback function to notify caller HtcUserAgreeDialog's result
     * @param content a content class for customized dialog content
     */
	public static void launchUserAgreeDialog(final Context context, final OnUserClickListener onUserClickListener, UserAgreeContent content) {
		if (DEBUG) Log.d(TAG, "User agree dialog is launched");
		
		bCheckboxSelected = false;
		if (needShowUserAgreeDialog(context)) {
			if (TextUtils.isEmpty(content.message)) {
				message = getDefaultMessage(context);
			}
			else {
				message = content.message;
			}
			if (TextUtils.isEmpty(content.checkboxLabel)) {
				checkboxLabel = context.getResources().getString(R.string.default_checkbox);
			}
			else {
				checkboxLabel = content.checkboxLabel;
			}
			if (TextUtils.isEmpty(content.positiveLabel)) {
				positiveLabel = context.getResources().getString(R.string.yes);
			}
			else {
				positiveLabel = content.positiveLabel;
			}
			if (TextUtils.isEmpty(content.negativeLabel)) {
				negativeLabel = context.getResources().getString(R.string.no);
			}
			else {
				negativeLabel = content.negativeLabel;
			}
			
			Builder builder = new HtcAlertDialog.Builder(context);
			builder.setTitle(content.title);
			builder.setMessage(message);
			
			builder.setCheckBox(checkboxLabel, false, new HtcCompoundButton.OnCheckedChangeListener() {
				public void onCheckedChanged(HtcCompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						bCheckboxSelected = true;
					}
					else {
						bCheckboxSelected = false;
					}
				}
			}, true);
			
			builder.setPositiveButton(positiveLabel, new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (bCheckboxSelected) {
						saveShowUserAgreeDialog(context, false);
					}
					if (DEBUG) Log.d(TAG, "user check yes");
					onUserClickListener.onUserClick(RESULT_YES);
				}});
			builder.setNegativeButton(negativeLabel, new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (DEBUG) Log.d(TAG, "user check no");
					onUserClickListener.onUserClick(RESULT_NO);
				}});
			builder.setCancelable(false);
			mAlertDialog = builder.create();
			mAlertDialog.show();
		}
		else {
			onUserClickListener.onUserClick(RESULT_YES);
		}
	}

	/**
     * This is to launch a HtcUserAgreeDialog from IME, due to original one will crash.
     *
     * @param context caller context
     * @param OnUserClickListener callback function to notify caller HtcUserAgreeDialog's result
     * @param content a content class for customized dialog content
     */
	public static void launchUserAgreeDialogFromIME(final Context context, final OnUserClickListener onUserClickListener, UserAgreeContent content, View inputView) {
		if (DEBUG) Log.d(TAG, "launchUserAgreeDialogFromIME is launched");
		
		bCheckboxSelected = false;
		if (needShowUserAgreeDialog(context)) {
			if (TextUtils.isEmpty(content.message)) {
				message = getDefaultMessage(context);
			}
			else {
				message = content.message;
			}
			if (TextUtils.isEmpty(content.checkboxLabel)) {
				checkboxLabel = context.getResources().getString(R.string.default_checkbox);
			}
			else {
				checkboxLabel = content.checkboxLabel;
			}
			if (TextUtils.isEmpty(content.positiveLabel)) {
				positiveLabel = context.getResources().getString(R.string.yes);
			}
			else {
				positiveLabel = content.positiveLabel;
			}
			if (TextUtils.isEmpty(content.negativeLabel)) {
				negativeLabel = context.getResources().getString(R.string.no);
			}
			else {
				negativeLabel = content.negativeLabel;
			}
			
			Builder builder = new HtcAlertDialog.Builder(context);
			builder.setTitle(content.title);
			builder.setMessage(message);
			
			builder.setCheckBox(checkboxLabel, false, new HtcCompoundButton.OnCheckedChangeListener() {
				public void onCheckedChanged(HtcCompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						bCheckboxSelected = true;
					}
					else {
						bCheckboxSelected = false;
					}
				}
			}, true);
			
			builder.setPositiveButton(positiveLabel, new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (bCheckboxSelected) {
						saveShowUserAgreeDialog(context, false);
					}
					if (DEBUG) Log.d(TAG, "user check yes");
					onUserClickListener.onUserClick(RESULT_YES);
				}});
			builder.setNegativeButton(negativeLabel, new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (DEBUG) Log.d(TAG, "user check no");
					onUserClickListener.onUserClick(RESULT_NO);
				}});
			builder.setCancelable(false);
			mAlertDialog = builder.create();
			
			// --> add to prevent crash while launching from IME
			Window window = mAlertDialog.getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.token = inputView.getWindowToken();
			lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
			window.setAttributes(lp);
			window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			// <--
			
			mAlertDialog.getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
			mAlertDialog.show();
		}
		else {
			onUserClickListener.onUserClick(RESULT_YES);
		}
	}
	
	/**
     * This is to launch a HtcUserAgreeDialog with windows flag.
     *
     * @param context caller context
     * @param OnUserClickListener callback function to notify caller HtcUserAgreeDialog's result
     * @param content a content class for customized dialog content
     * @param flag needed windows flag
     */
	public static void launchUserAgreeDialogWithWindowsFlag(final Context context, final OnUserClickListener onUserClickListener, UserAgreeContent content, int flag) {
		if (DEBUG) Log.d(TAG, "launchUserAgreeDialogWithWindowsFlag is launched, windows flag = " + flag);
		
		bCheckboxSelected = false;
		if (needShowUserAgreeDialog(context)) {
			if (TextUtils.isEmpty(content.message)) {
				message = getDefaultMessage(context);
			}
			else {
				message = content.message;
			}
			if (TextUtils.isEmpty(content.checkboxLabel)) {
				checkboxLabel = context.getResources().getString(R.string.default_checkbox);
			}
			else {
				checkboxLabel = content.checkboxLabel;
			}
			if (TextUtils.isEmpty(content.positiveLabel)) {
				positiveLabel = context.getResources().getString(R.string.yes);
			}
			else {
				positiveLabel = content.positiveLabel;
			}
			if (TextUtils.isEmpty(content.negativeLabel)) {
				negativeLabel = context.getResources().getString(R.string.no);
			}
			else {
				negativeLabel = content.negativeLabel;
			}
			
			Builder builder = new HtcAlertDialog.Builder(context);
			builder.setTitle(content.title);
			builder.setMessage(message);
			
			builder.setCheckBox(checkboxLabel, false, new HtcCompoundButton.OnCheckedChangeListener() {
				public void onCheckedChanged(HtcCompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						bCheckboxSelected = true;
					}
					else {
						bCheckboxSelected = false;
					}
				}
			}, true);
			
			builder.setPositiveButton(positiveLabel, new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (bCheckboxSelected) {
						saveShowUserAgreeDialog(context, false);
					}
					if (DEBUG) Log.d(TAG, "user check yes");
					onUserClickListener.onUserClick(RESULT_YES);
				}});
			builder.setNegativeButton(negativeLabel, new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (DEBUG) Log.d(TAG, "user check no");
					onUserClickListener.onUserClick(RESULT_NO);
				}});
			builder.setCancelable(false);
			mAlertDialog = builder.create();
			mAlertDialog.getWindow().addFlags(flag);
			mAlertDialog.show();
		}
		else {
			onUserClickListener.onUserClick(RESULT_YES);
		}
	}
	
	/**
	 * Indicate user agree dialog is showing or not.
	 * @return true is showing, false is not
	 */
	public static boolean isShowing() {
		if (null != mAlertDialog)
		{
 			if (DEBUG) Log.d(TAG, "user agree dialog isShowing = " + mAlertDialog.isShowing());
			return mAlertDialog.isShowing();
		}
		return false;
	}

	/**
	 * Terminate user agree dialog.
	 */
	public static void terminate() {
		if (null != mAlertDialog)
		{
			if (DEBUG) Log.d(TAG, "terminate user agree dialog");
			mAlertDialog.dismiss();
			mAlertDialog = null;
		}
	}
	
	/**
	 * Indicate user agree dialog needs to show or not.
	 * @param context caller context
	 * @return true is need to show UserAgree dialog, false is not
	 */
	public static boolean needShowUserAgreeDialog(Context context) {
		if (isChinaProject()) {
			if (DEBUG) Log.d(TAG, "isChinaProject returns true");
			
			if (getShowUserAgreeDialog(context)) {
				if (DEBUG) Log.d(TAG, "getShowUserAgreeDialog returns true");
				
				return true;
			}
		}
		
		return false;
	}
	
	private static String getDefaultMessage(Context context)
	{
		String defaultMessage = context.getResources().getString(R.string.default_message);
		if(isChinaProject())
		{
			if(defaultMessage.contains("Wi-Fi"))
			{
				defaultMessage = defaultMessage.replace("Wi-Fi", "WLAN");
			}
		}
		
		return defaultMessage;
		
	}

	private static boolean isChinaProject(){
        boolean isCHS = false;
        HtcWrapCustomizationManager manager = new HtcWrapCustomizationManager();
        HtcWrapCustomizationReader reader = manager.getCustomizationReader("System", HtcWrapCustomizationManager.READER_TYPE_XML, false);
        int region = reader.readInteger("region", 0);
        if (region == 3) {
            isCHS = true;
        }
        return isCHS;
    }

	private static boolean getShowUserAgreeDialog(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(SHOW_USER_AGREE_DIALOG, Context.MODE_PRIVATE);
		boolean result = preferences.getBoolean(SHOW_USER_AGREE_DIALOG, true);
		return result;
	}

	private static void saveShowUserAgreeDialog(Context mContext, boolean set) {
		if (DEBUG) Log.d(TAG, "saveShowUserAgreeDialog: set to " + set);
		
		Editor editor = mContext.getSharedPreferences(SHOW_USER_AGREE_DIALOG, Context.MODE_PRIVATE).edit();
		editor.putBoolean(SHOW_USER_AGREE_DIALOG, set);
		if (false == editor.commit()) {
			if (DEBUG) Log.d(TAG, "saveShowUserAgreeDialog: commit to shared preference failed");
		}
	}
}

