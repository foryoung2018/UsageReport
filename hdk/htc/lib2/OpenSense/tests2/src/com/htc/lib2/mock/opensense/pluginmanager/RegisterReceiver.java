package com.htc.lib2.mock.opensense.pluginmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class RegisterReceiver extends BroadcastReceiver {
	private static final String LOG_TAG = "RegisterReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
			/*
			 * for package enable/disable case, pluginManager will let it become
			 * add/remove
			 */
			final String packageName = intent.getData().getSchemeSpecificPart();
			if (!TextUtils.isEmpty(packageName)) {
				int enableSetting;
				try {
					enableSetting = context.getPackageManager()
							.getApplicationEnabledSetting(packageName);
				} catch (Exception e) {
					Log.e(LOG_TAG, "get app failed", e);
					enableSetting = -1008;
				}
				boolean enable = enableSetting == android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
						|| enableSetting == android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
				if (enable) {
					intent.setAction(Intent.ACTION_PACKAGE_ADDED).putExtra(
							RegisterService.KEY_SOCIAL_ACTION,
							RegisterService.ACTION_TYPE_ENABLED);
				} else {
					intent.setAction(Intent.ACTION_PACKAGE_REMOVED).putExtra(
							RegisterService.KEY_SOCIAL_ACTION,
							RegisterService.ACTION_TYPE_DISABLED);
				}
			} else {
				Log.d(LOG_TAG, "package name is null or empty");
			}
		}
		context.startService(intent.setClass(context, RegisterService.class));
	}

}