package com.htc.lib2.mock.opensense.social.provider;

import java.util.HashSet;

import com.htc.lib2.opensense.plugin.PluginConstants;

import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class LocalPluginHelper implements PluginConstants {

	private final static String LOG_TAG = "SocialManagerService";

	public final static String URL_RAW_QUERY = "rawquery";

	public static final Uri RAWQUERY = Uri.parse("content://" + AUTHORITY + "/"
			+ URL_RAW_QUERY);

	static final Uri FEATURE_URI = Uri.parse("content://" + AUTHORITY + "/"
			+ FEATURE_TB);
	static final Uri METADATA_URI = Uri.parse("content://" + AUTHORITY + "/"
			+ METADATA_TB);

	private static final String[] BLINKFEED_FEATURES = new String[] {
			"'FriendStreamDataPlugin'", "'BlinkFeedStreamPlugin'" };

	public static ComponentName getBlinkFeedPluginComponentName(
			Context context, String accountType) {
		String sql = String
				.format("SELECT A1.%s, A2.%s FROM %s A1, %s A2 WHERE %s A1.%s IN "
						+ "(SELECT A2.%s FROM %s WHERE A2.%s IN (SELECT %s FROM %s WHERE %s in (%s)))"
						+ " AND A2.%s=%s", COLUMN_PACKAGE, COLUMN_PLUGIN_CLASS,
						PLUGIN_PKG_TB, PLUGIN_TB, ("A2."
								+ COLUMN_PLUGIN_REMOVED + "=0 AND"), _ID,
						COLUMN_PACKAGE_ID, PLUGIN_PKG_TB, COLUMN_FEATURE_ID,
						_ID, FEATURE_TB, COLUMN_FEATURE,
						TextUtils.join(",", BLINKFEED_FEATURES),
						COLUMN_PLUGIN_META,
						DatabaseUtils.sqlEscapeString(accountType));
		ContentProviderClient client = null;
		Cursor c = null;
		ComponentName componentName = null;
		try {
			client = context.getContentResolver()
					.acquireUnstableContentProviderClient(RAWQUERY);
			if (client != null) {
				c = client.query(RAWQUERY, null, sql, null, null);
			} else {
				Log.w(LOG_TAG, "ContentProviderClient is null for uri: "
						+ RAWQUERY.toString());
			}
			if (c != null && c.moveToFirst()) {
				componentName = new ComponentName(c.getString(0),
						c.getString(1));
			}
		} catch (Exception e) {
			Log.e(LOG_TAG,
					"getFriendStreamPluginComponentName() error: "
							+ e.getMessage());
		} finally {
			if (c != null) {
				c.close();
			}
			if (client != null) {
				client.release();
			}
		}
		return componentName;
	}

	public static String[] getBlinkFeedPluginAccountTypes(Context context) {
		String sql = String
				.format("SELECT t.%s FROM %s t WHERE %s AND t.%s in (SELECT %s FROM %s WHERE %s in (%s))",
						COLUMN_PLUGIN_META, PLUGIN_TB, "t."
								+ COLUMN_PLUGIN_REMOVED + "=0",
						COLUMN_FEATURE_ID, _ID, FEATURE_TB, COLUMN_FEATURE,
						TextUtils.join(",", BLINKFEED_FEATURES));

		ContentProviderClient client = null;
		Cursor cursor = null;
		try {
			client = context.getContentResolver()
					.acquireUnstableContentProviderClient(RAWQUERY);
			if (client != null) {
				cursor = client.query(RAWQUERY, null, sql, null, null);
			} else {
				Log.w(LOG_TAG, "ContentProviderClient is null for uri: "
						+ RAWQUERY.toString());
			}
			if (cursor != null) {
				int plugin_meta = cursor
						.getColumnIndexOrThrow(COLUMN_PLUGIN_META);
				HashSet<String> pluginAccountTypes = new HashSet<String>();
				for (int i = cursor.getCount() - 1; i >= 0; i--) {
					if (cursor.moveToPosition(i)) {
						pluginAccountTypes.add(cursor.getString(plugin_meta));
					}
				}
				return pluginAccountTypes.toArray(new String[0]);
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "getPlugins() error: " + e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (client != null) {
				client.release();
			}
		}
		return new String[0];
	}
}
