package com.htc.lib2.mock.opensense.pluginmanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.htc.lib2.mock.opensense.pluginmanager.data.FeatureList;
import com.htc.lib2.mock.opensense.pluginmanager.data.MetaData;
import com.htc.lib2.mock.opensense.pluginmanager.data.Plugin;
import com.htc.lib2.mock.opensense.pluginmanager.data.PluginPackage;
import com.htc.lib2.mock.opensense.pluginmanager.data.MetaData.TypeValue;
import com.htc.lib2.opensense.plugin.PluginConstants;

public class RegisterService extends IntentService implements PluginConstants {

	public static final String ACTION_PACKAGE_RESCAN = "com.htc.plugin.package.RESCAN";
	public static final String KEY_SOCIAL_ACTION = "social_action";
	public static final String ACTION_TYPE_ENABLED = "enabled";
	public static final String ACTION_TYPE_DISABLED = "disabled";
	public static final String ACTION_TYPE_ADD = "add";
	public static final String ACTION_TYPE_REMOVE = "remove";
	public static final String ACTION_TYPE_REPLACE = "replace";

	private static final String BROADCAST_PERMISSION = "htc.pluginmanager.permission.USE_BROADCAST";
	private static final String ACTION_SERVICE_REGISTER = "com.htc.ACTION_SERVICE_REGISTER";
	private static final String ACTION_SERVICE_NAME = "com.htc.ACTION_SERVICE_NAME";
	private static final String SERVICE_META_DATA = "com.htc.ACTION_SERVICE_REGISTER";
	private static final String ACTION_SCAN_FINISH = "com.htc.plugin.ACTION_SCAN_FINISH";
	private static final String EXTRA_PKG_REPLACED = "com.htc.plugin.EXTRA_PKG_REPLACED";
	private static final String EXTRA_PKG_REMOVED = "com.htc.plugin.EXTRA_PKG_REMOVED";
	private static final String LOG_TAG = "[PluginManager]RegisterService";
	private static final String HEAD_TAG = "register_service";
	private static final String ATTR_SUPPORT = "supports";
	private static final String ACTION_BLINKFEED_PLUGIN_CHANGED = "com.htc.opensense.social.PLUGIN_CHANGED";

	private static final Uri FEATURE_AUTHORITY = Uri.parse("content://" + PluginProvider.AUTHORITY + "/" + PluginProvider.URL_FEATURES);
	private static final Uri CLASS_AUTHORITY = Uri.parse("content://" + PluginProvider.AUTHORITY + "/" + PluginProvider.URL_METADATA);
	private static final Uri PLUGIN_AUTHORITY = Uri.parse("content://" + PluginProvider.AUTHORITY + "/" + PluginProvider.URL_PLUGIN);
	private static final Uri RAWQUERY_AUTHORITY = Uri.parse("content://" + PluginProvider.AUTHORITY + "/" + PluginProvider.URL_RAW_QUERY);
	private static final Uri PLUGIN_RAWQUERY_AUTHORITY = Uri.parse("content://" + PluginProvider.AUTHORITY + "/" + PluginProvider.URL_PLUGIN_RAWQUERY);
	private static final Uri PLUGIN_PKG_AUTHORITY = Uri.parse("content://" + PluginProvider.AUTHORITY + "/" + PluginProvider.URL_PLUGIN_PKG);


	private final Intent registerServiceIntent = new Intent(ACTION_SERVICE_REGISTER);

	public RegisterService() {
		super(RegisterService.class.getSimpleName());
		this.setIntentRedelivery(true);
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	private final void insertFeatures(FeatureList features) {
		if ( features == null || features.size() < 0 ) {
			return;
		}

		final ContentValues[] values = new ContentValues[features.size()];

		int i = 0;
		for ( FeatureList.Feature feature : features ) {
			final ContentValues v = new ContentValues();
			v.put(COLUMN_VERSION, feature.getVersion());
			v.put(COLUMN_FEATURE, feature.getName());
			v.put(COLUMN_FEATURE_TYPE, feature.getType());
			values[i++] = v;
		}

		try {
			getContentResolver().bulkInsert(FEATURE_AUTHORITY, values);
		} catch ( Exception e ) {
			Log.e(LOG_TAG, "insertFeatures failed to bulkInsert", e);
		}
	}

	private void addInsertFeaturesOp(FeatureList features, ArrayList<ContentProviderOperation> operations){
		if ( operations != null && features != null && features.size() >= 0 ) {
			for ( FeatureList.Feature feature : features ) {
				final ContentValues v = new ContentValues();
				v.put(COLUMN_VERSION, feature.getVersion());
				v.put(COLUMN_FEATURE, feature.getName());
				v.put(COLUMN_FEATURE_TYPE, feature.getType());

				operations.add(ContentProviderOperation.newInsert(FEATURE_AUTHORITY).withValues(v).build());
			}
		}
	}

	private void addInsertMetaDataOp(ArrayList<MetaData> services, long pkgId, ArrayList<ContentProviderOperation> operations){
		if ( operations == null ) {
			return;
		}

		final ContentValues values = new ContentValues();
		values.put(COLUMN_PACKAGE_ID, pkgId);

		for ( MetaData service : services ) {
			values.put(COLUMN_META_NAME, service.getName());
			for ( TypeValue data : service.getDataList() ) {
				values.put(COLUMN_META_TYPE, data.type);
				values.put(COLUMN_META_VALUE, data.value);
				operations.add(ContentProviderOperation.newInsert(CLASS_AUTHORITY).withValues(values).build());
			}
		}
	}

	private final void insertMetaData(ArrayList<MetaData> services, long pkgId) {
		final ContentValues values = new ContentValues();
		values.put(COLUMN_PACKAGE_ID, pkgId);

		try {
			for ( MetaData service : services ) {
				values.put(COLUMN_META_NAME, service.getName());
				for ( TypeValue data : service.getDataList() ) {
					values.put(COLUMN_META_TYPE, data.type);
					values.put(COLUMN_META_VALUE, data.value);
					getContentResolver().insert(CLASS_AUTHORITY, values);
				}
			}
		} catch (Exception e) {
			Log.w(LOG_TAG, "[insertMetaData]insert failed", e);
		}
	}

	private final long getPackageId(String packageName) {
		return getId(PLUGIN_PKG_AUTHORITY, COLUMN_PACKAGE + "=?", new String[] { packageName });
	}

//	private final long getFeatureId(String feature) {
//		return getId(FEATURE_AUTHORITY, COLUMN_FEATURE + "=?", new String[] { feature });
//	}

	private final long getId(Uri uri, String selection, String[] selectionArgs) {
		Cursor c = null;
		try {    
			c = getContentResolver().query(uri, new String[] { BaseColumns._ID }, selection, selectionArgs, null);
			if ( c != null ) {
				if ( c.moveToNext() ) {
					return c.getLong(0);
				}
			}
		} catch (Exception e) {
			Log.d(LOG_TAG, "[getId]something worong.", e);
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return -1;
	}

	private void addInsertPluginsOp(ArrayList<Plugin> plugins, long pkgId, ArrayList<ContentProviderOperation> operations){
		if ( operations != null && plugins != null && plugins.size() > 0 ) {

			for ( Plugin plugin : plugins ) {
				final ContentValues v = new ContentValues();
				v.put(COLUMN_PACKAGE_ID, pkgId);
				v.put(COLUMN_FEATURE, plugin.getFeature());
				v.put(COLUMN_PLUGIN_CLASS, plugin.getClassName());
				v.put(COLUMN_VERSION, plugin.getVersion());
				v.put(COLUMN_DESCRIPTION, plugin.getDescription());
				v.put(COLUMN_PLUGIN_META, plugin.getPluginMeta());
				v.put(COLUMN_PLUGIN_REMOVED, false);

				operations.add(ContentProviderOperation.newInsert(PLUGIN_RAWQUERY_AUTHORITY).withValues(v).build());
			}
		}
	}

	private final void insertPlugins(ArrayList<Plugin> plugins, long pkgId) {
		if ( plugins == null || plugins.size() <= 0 ) {
			Log.i(LOG_TAG,"insertPlugin, plugin == null or plugins.size <=0, return");
			return;
		}

		final ArrayList<ContentValues> values = new ArrayList<ContentValues>();

		for ( Plugin plugin : plugins ) {
			final ContentValues v = new ContentValues();
			v.put(COLUMN_PACKAGE_ID, pkgId);
			v.put(COLUMN_FEATURE, plugin.getFeature());
			v.put(COLUMN_PLUGIN_CLASS, plugin.getClassName());
			v.put(COLUMN_VERSION, plugin.getVersion());
			v.put(COLUMN_DESCRIPTION, plugin.getDescription());
			v.put(COLUMN_PLUGIN_META, plugin.getPluginMeta());
			v.put(COLUMN_PLUGIN_REMOVED, false);
			values.add(v);
		}

		try {
			getContentResolver().bulkInsert(PLUGIN_RAWQUERY_AUTHORITY, values.toArray(new ContentValues[] {}));
		} catch ( Exception e ) {
			Log.e(LOG_TAG, "Something wrong while trying to insertPlugins!", e);
		}

		notifyChangeWithPlugins(plugins);

	}

	private void notifyChangeWithPlugins(ArrayList<Plugin> plugins){

		Set<String> featureNames = new HashSet<String>();
		for ( Plugin plugin : plugins ) {
			featureNames.add(plugin.getFeature());
		}
		if ( featureNames.size() > 0 ) {
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT DISTINCT ")
				.append(COLUMN_FEATURE_TYPE)
				.append(" FROM ")
				.append(FEATURE_TB)
				.append(" WHERE ")
				.append(COLUMN_FEATURE)
				.append(" IN (");
			for ( int i = featureNames.size(); i > 0; i-- ) {
				if ( i < featureNames.size() ) {
					builder.append(",");
				}
				builder.append("?");
			}
			builder.append(") AND ")
				.append(COLUMN_FEATURE_TYPE)
				.append("='com.htc.opensense.plugin.TabPlugin'");
			String[] selectionArgs = featureNames.toArray(new String[] {});
			Cursor c = null;
			try {
				c = getContentResolver().query(RAWQUERY_AUTHORITY, null, builder.toString(), selectionArgs, null);
				if ( c != null && c.getCount() > 0 ) {
					Log.i(LOG_TAG, "Notify Carousel that a new TabPlugin has been installed!");
					getContentResolver().notifyChange(PLUGIN_AUTHORITY, null);
				}
			} catch ( Exception e ) {
				Log.w(LOG_TAG, "Something wrong while trying to notify Carousel!", e);
			} finally {
				if ( c != null ) {
					c.close();
				}
			}
		}
	}

	private final long insertPluginPkg(PluginPackage pkg, long existingPkgId) {
		final ContentValues values = new ContentValues();
		values.put(COLUMN_CERTIFICATE, pkg.getCertification());

		if ( existingPkgId > -1 ) {
			try {
				getContentResolver().update(PLUGIN_PKG_AUTHORITY, values,
						BaseColumns._ID + "=?",
						new String[] { String.valueOf(existingPkgId) });
			} catch ( Exception e ) {
				Log.e(LOG_TAG, "Failed to update plugin_pkg: " + pkg.getName()
						+ ", existingPkgId: " + existingPkgId);
			}
			return existingPkgId;
		} else {
			values.put(COLUMN_PACKAGE, pkg.getName());
			long packageId = -1L;
			try {
				Uri uri = getContentResolver().insert(PLUGIN_PKG_AUTHORITY,
						values);
				packageId = ContentUris.parseId(uri);
			} catch ( Exception e ) {
				Log.e(LOG_TAG, "Failed to insert plugin_pkg: " + pkg.getName());
			}
			return packageId;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private static final PluginPackage getInstalledPluginPackage(
			PackageManager pm, ApplicationInfo app) {
		if ( app == null ) {
			return null;
		}

		PluginPackage p = null;

		if ( app.metaData != null && app.metaData.containsKey("opensense") ) {
			try {
				Resources r = pm.getResourcesForApplication(app);
				XmlResourceParser parser = r.getXml(app.metaData.getInt("opensense"));
				p = PluginPackage.parse(parser);
				p.setName(app.packageName);
			} catch ( NameNotFoundException e ) {
				Log.e(LOG_TAG, "getInstalledPluginPackage failed: "
						+ app.packageName, e);
			} catch ( XmlPullParserException e ) {
				Log.e(LOG_TAG, "getInstalledPluginPackage failed: "
						+ app.packageName, e);
			} catch ( IOException e ) {
				Log.e(LOG_TAG, "getInstalledPluginPackage failed: "
						+ app.packageName, e);
			} catch (Resources.NotFoundException e) {
				Log.e(LOG_TAG, "getInstalledPluginPackage failed: "
						+ app.packageName, e);
			}
		}

		return p;
	}

	private static final FeatureList getFeatureList(PackageManager pm,
			ApplicationInfo app) {
		if ( app == null ) {
			return null;
		}

		FeatureList features = null;

		if ( app.metaData != null
				&& app.metaData.containsKey("opensense_feature") ) {
			try {
				Resources r = pm.getResourcesForApplication(app);
				XmlResourceParser parser = r.getXml(app.metaData.getInt("opensense_feature"));
				features = FeatureList.parse(parser);
			} catch ( NameNotFoundException e ) {
				Log.e(LOG_TAG, "getFeatureList failed: " + app.packageName, e);
			} catch ( XmlPullParserException e ) {
				Log.e(LOG_TAG, "getFeatureList failed: " + app.packageName, e);
			} catch ( IOException e ) {
				Log.e(LOG_TAG, "getFeatureList failed: " + app.packageName, e);
			}
		}

		return features;
	}

	public static Pair<PluginPackage, FeatureList> getInstalledPluginPackage(
			PackageManager pm, String packageName) {
		if ( pm == null || TextUtils.isEmpty(packageName) ) {
			return null;
		}

		ApplicationInfo app = null;

		try {
			app = pm.getApplicationInfo(packageName,
					PackageManager.GET_META_DATA);
		} catch ( NameNotFoundException e ) {
			Log.e(LOG_TAG, "Unable to getApplicationInfo for " + packageName, e);
		}

		if ( app == null ) {
			return null;
		}

		final PluginPackage p = getInstalledPluginPackage(pm, app);
		final FeatureList features = getFeatureList(pm, app);

		if ( p == null && features == null ) {
			return null;
		}

		return new Pair<PluginPackage, FeatureList>(p, features);
	}

	public static Pair<ArrayList<PluginPackage>, FeatureList> getInstalledPluginPackages(
			PackageManager pm) {
		ArrayList<PluginPackage> packages = new ArrayList<PluginPackage>();
		FeatureList features = new FeatureList();

		List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

		if ( apps == null ) {
			throw new IllegalArgumentException("Plugin cannot be empty");
		}

		for ( ApplicationInfo app : apps ) {
			if(!app.enabled) {
				continue;
			}
			final PluginPackage p = getInstalledPluginPackage(pm, app);
			final FeatureList f = getFeatureList(pm, app);

			if ( p != null ) {
				packages.add(p);
			}

			if ( f != null && f.size() > 0 ) {
				features.addAll(f);
			}
		}

		return new Pair<ArrayList<PluginPackage>, FeatureList>(
				packages,
				features);
	}

	public enum ActionCmd {
		PACKAGE_ADDED, PACKAGE_INSTALL, PACKAGE_REMOVED, PACKAGE_REPLACED, BOOT_COMPLETED, PACKAGE_RESCAN
	};

	private static final HashMap<String, ActionCmd> ACTION_MAP = new HashMap<String, ActionCmd>();

	static {
		ACTION_MAP.put(Intent.ACTION_PACKAGE_ADDED, ActionCmd.PACKAGE_ADDED);
		ACTION_MAP.put(Intent.ACTION_PACKAGE_INSTALL, ActionCmd.PACKAGE_INSTALL);
		ACTION_MAP.put(Intent.ACTION_PACKAGE_REMOVED, ActionCmd.PACKAGE_REMOVED);
		ACTION_MAP.put(Intent.ACTION_PACKAGE_REPLACED,ActionCmd.PACKAGE_REPLACED);
		ACTION_MAP.put(Intent.ACTION_BOOT_COMPLETED, ActionCmd.BOOT_COMPLETED);
		ACTION_MAP.put(ACTION_PACKAGE_RESCAN, ActionCmd.PACKAGE_RESCAN);
	}

	private void addInsertPluginPackageOp(PluginPackage pluginPackage, ArrayList<ContentProviderOperation> operations) {
		if ( operations != null && pluginPackage != null ) {
			final long existingPkgId = getPackageId(pluginPackage.getName());
			final long newPkgId = insertPluginPkg(pluginPackage, existingPkgId);

			if ( newPkgId < 0 ) {
				Log.i(LOG_TAG,"insertPluginPackage newPkgId < 0, return");
				return;
			}
			addInsertMetaDataOp(pluginPackage.getServices(), newPkgId, operations);
			addInsertPluginsOp(pluginPackage.getPlugins(), newPkgId, operations);
		}
	}

	private final void insertPluginPackage(PluginPackage pluginPackage) {
		if ( pluginPackage == null ) {
			Log.i(LOG_TAG,"insertPluginPackage pluginPackage is null, return");
			return;
		}

		final long existingPkgId = getPackageId(pluginPackage.getName());
		final long newPkgId = insertPluginPkg(pluginPackage, existingPkgId);

		if ( newPkgId < 0 ) {
			Log.i(LOG_TAG,"insertPluginPackage newPkgId < 0, return");
			return;
		}

		insertMetaData(pluginPackage.getServices(), newPkgId);
		insertPlugins(pluginPackage.getPlugins(), newPkgId);
	}

	protected void onHandleIntent(Intent intent) {
		final String intentAction = intent.getAction();

		if ( intentAction == null || !ACTION_MAP.containsKey(intentAction) ) {
			return;
		}

		Log.i(LOG_TAG, "onHandleIntent, action: " + intentAction + ", data: " + intent.getDataString());

		final ActionCmd action = ACTION_MAP.get(intentAction);

		switch ( action ) {
			case PACKAGE_INSTALL: {
				// Do nothing for now. Unsure what this intent is for.
				break;
			}
			case PACKAGE_RESCAN: {
			}
			case BOOT_COMPLETED: {

				ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

				//clean plugin table before we build the installed plugin packages one
				operations.add(ContentProviderOperation.newDelete(PLUGIN_AUTHORITY).build());

				final Pair<ArrayList<PluginPackage>, FeatureList> result = getInstalledPluginPackages(getPackageManager());

				if ( result == null ) {
					return;
				}
				// Write features first
				final FeatureList features = result.second;
				addInsertFeaturesOp(features,operations);

				// Write plugins now
				final ArrayList<PluginPackage> pluginPackages = result.first;
				if ( result.first != null ) {
					for ( PluginPackage pluginPackage : pluginPackages ) {
						//insertPluginPackage(pluginPackage);
						addInsertPluginPackageOp(pluginPackage, operations);
					}
				} else {
					Log.i(LOG_TAG,"insertPluginPackage result.first is null");
				}
				try {
					getContentResolver().applyBatch(PluginProvider.AUTHORITY,operations);

					if ( pluginPackages != null ) {
						for ( PluginPackage pluginPackage : pluginPackages ) {
							notifyChangeWithPlugins(pluginPackage.getPlugins());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}

				Intent scanFinish = new Intent(ACTION_SCAN_FINISH);
				sendBroadcast(scanFinish, BROADCAST_PERMISSION);
				break;
			}
			case PACKAGE_ADDED: {
			}
			case PACKAGE_REPLACED: {
				final Uri pkg = intent.getData();
				handleNotifyBlinkfeedClientChanged(intent, action);

				final Pair<PluginPackage, FeatureList> result = getInstalledPluginPackage(
						getPackageManager(), pkg.getSchemeSpecificPart());

				if ( result == null ) {
					return;
				}

				// Write features first
				final FeatureList features = result.second;
				insertFeatures(features);

				// Write plugin now
				final PluginPackage pluginPackage = result.first;
				insertPluginPackage(pluginPackage);
				handleKillProcess(pluginPackage);
				handleNotifyBlinkfeedAddPlugin(pluginPackage, intent);
				Intent scanFinish = new Intent(ACTION_SCAN_FINISH);
				scanFinish.putExtra(EXTRA_PKG_REPLACED, pkg);
				sendBroadcast(scanFinish, BROADCAST_PERMISSION);
				break;
			}
			case PACKAGE_REMOVED: {
				final Uri pkg = intent.getData();
				final String where = COLUMN_PACKAGE_ID + " IN ( SELECT "
						+ BaseColumns._ID + " FROM " + PLUGIN_PKG_TB + " WHERE "
						+ COLUMN_PACKAGE + "=? )";
				final String[] selectionArgs = new String[] { pkg.getSchemeSpecificPart() };
				final ContentValues values = new ContentValues(1);
				values.put(COLUMN_PLUGIN_REMOVED, true);
				try {
					getContentResolver().update(PLUGIN_AUTHORITY, values, where,
							selectionArgs);
					getContentResolver().delete(CLASS_AUTHORITY, where, selectionArgs);
				} catch ( Exception e) {
					Log.w(LOG_TAG, "provider may killed!", e);
				}
				final String sql = "SELECT DISTINCT " + COLUMN_FEATURE_TYPE
						+ " FROM " + FEATURE_TB + " WHERE " + COLUMN_FEATURE_TYPE
						+ "='com.htc.opensense.plugin.TabPlugin' AND " + _ID
						+ " IN ( SELECT " + COLUMN_FEATURE_ID + " FROM " + PLUGIN_TB
						+ " WHERE " + COLUMN_PACKAGE_ID + " IN ( SELECT " + _ID
						+ " FROM " + PLUGIN_PKG_TB + " WHERE " + COLUMN_PACKAGE
						+ "=? ))";
				Cursor c = null;
				try {
					c = getContentResolver().query(RAWQUERY_AUTHORITY, null, sql,
							selectionArgs, null);
					if ( c != null && c.getCount() > 0 ) {
						Log.i(LOG_TAG,
								"Notify Carousel about removal of a TabPlugin: "
										+ pkg.getSchemeSpecificPart());
						getContentResolver().notifyChange(PLUGIN_AUTHORITY, null);
					}
				} catch ( Exception e ) {
					Log.w(LOG_TAG, "Something wrong while notifying Carousel!", e);
				} finally {
					if ( c != null ) {
						try {
							c.close();
						} catch (Exception e) {
							Log.e(LOG_TAG, "something wrong when close cursor");
						}
					}
				}

				handleNotifyBlinkfeedRemovePlugin(intent);
				handleNotifyBlinkfeedClientChanged(intent, action);
				Intent scanFinish = new Intent(ACTION_SCAN_FINISH);
				scanFinish.putExtra(EXTRA_PKG_REMOVED, pkg);
				sendBroadcast(scanFinish, BROADCAST_PERMISSION);
				break;
			}
		}

	}

	private void handleKillProcess(PluginPackage pluginPackage) {
		Log.i(LOG_TAG, "handle kill process");
		if (pluginPackage == null) {
			Log.e(LOG_TAG, "pluginPackage == null");
			return;
		}
		List<Plugin> plugins = pluginPackage.getPlugins();
		if (plugins != null) {
			for (Plugin plugin : plugins) {
				Log.e(LOG_TAG, "feature " + plugin.getFeature());
				if ("FriendStreamDataPlugin".equals(plugin.getFeature())) {
					killProcessByName("com.htc.opensense.social");
					break;
				}
			}
		}
	}


	private void handleNotifyBlinkfeedAddPlugin(PluginPackage pluginPackage, Intent intent) {
		Log.i(LOG_TAG, "handle notify Blinkfeed add Plugin");
		if (pluginPackage == null) {
			Log.e(LOG_TAG, "pluginPackage == null");
			return;
		}

		String socialAction = intent.getStringExtra(KEY_SOCIAL_ACTION);
		if (TextUtils.isEmpty(socialAction)) {
			socialAction = ACTION_TYPE_ADD;
		}
		boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
		Uri packageNameUri = intent.getData();

		List<Plugin> plugins = pluginPackage.getPlugins();
		if (plugins != null) {
			for (Plugin plugin : plugins) {
				Log.e(LOG_TAG, "feature " + plugin.getFeature());
				if ("FriendStreamDataPlugin".equals(plugin.getFeature())
						|| "BlinkFeedStreamPlugin".equals(plugin.getFeature())) {
					Intent addIntent = new Intent(ACTION_BLINKFEED_PLUGIN_CHANGED);
					addIntent.putExtra("action", socialAction);
					addIntent.putExtra(Intent.EXTRA_REPLACING, replacing);
					if (packageNameUri != null) {
						addIntent.setData(Uri.parse("social://" + packageNameUri.getSchemeSpecificPart()));
					}
					getBaseContext().sendBroadcast(addIntent);
					break;
				}
			}
		}
	}

	private void handleNotifyBlinkfeedRemovePlugin(Intent intent) {

		Uri pkg = intent.getData();
		String socialAction = intent.getStringExtra(KEY_SOCIAL_ACTION);
		if (TextUtils.isEmpty(socialAction)) {
			socialAction = ACTION_TYPE_REMOVE;
		}
		boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);

		StringBuilder builder = new StringBuilder();

		// select plugin_meta from plugin where package_id in (select _id from
		// package_id where package = ? ) and feature_id in ( select _id from
		// feature where feature = 'FriendStreamDataPlugin' or feature
		// ='BlinkFeedStreamPlugin')
		builder.append("SELECT ");
		builder.append(COLUMN_PLUGIN_META);
		builder.append(" FROM ");
		builder.append(PLUGIN_TB);
		builder.append(" WHERE ");
		builder.append(COLUMN_PACKAGE_ID);
		builder.append(" IN ( SELECT ");
		builder.append(_ID);
		builder.append(" FROM ");
		builder.append(PLUGIN_PKG_TB);
		builder.append(" WHERE ");
		builder.append(COLUMN_PACKAGE);
		builder.append("=? )");
		builder.append(" AND ");
		builder.append(COLUMN_FEATURE_ID);
		builder.append(" IN ( SELECT ");
		builder.append(_ID);
		builder.append(" FROM ");
		builder.append(FEATURE_TB);
		builder.append(" WHERE ");
		builder.append(COLUMN_FEATURE);
		builder.append("='FriendStreamDataPlugin'");
		builder.append(" OR ");
		builder.append(COLUMN_FEATURE);
		builder.append("='BlinkFeedStreamPlugin')");

		String packageName = pkg.getSchemeSpecificPart();
		final String[] selectionArgs = new String[] { packageName };

		Cursor c = null;
		try {
			c = getContentResolver().query(RAWQUERY_AUTHORITY, null,
					builder.toString(), selectionArgs, null);
			if (c != null && c.moveToFirst()) {
				String accountType = c.getString(0);
				Intent removeIntent = new Intent(ACTION_BLINKFEED_PLUGIN_CHANGED);
				removeIntent.putExtra("accountType", accountType)
					.putExtra("action", socialAction)
					.putExtra(Intent.EXTRA_REPLACING, replacing);
				removeIntent.setData(Uri.parse("social://" + packageName));
				getBaseContext().sendBroadcast(removeIntent);
			}
		} catch (Exception e) {
			Log.w(LOG_TAG, "Something wrong while notifying blinkfeed!", e);
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (Exception e) {
					Log.e(LOG_TAG, "something wrong when close cursor");
				}
			}
		}
	}

	private static final String ACTION_BLINKFEED_CLIENT_CHANGED = "com.htc.opensense.social.CLIENT_CHANGED";

	private static final String FACEBOOK_CLIENT_PACKAGE_NAME = "com.facebook.katana";
	private static final String TWITTER_CLIENT_PACKAGE_NAME = "com.twitter.android";
	private static final String[] clientPkgNameWatchingList = {
			FACEBOOK_CLIENT_PACKAGE_NAME, TWITTER_CLIENT_PACKAGE_NAME};

	private void handleNotifyBlinkfeedClientChanged(Intent intent, ActionCmd action) {
		Log.i(LOG_TAG, "handle notify Blinkfeed plugin client changed");

		String socialAction = intent.getStringExtra(KEY_SOCIAL_ACTION);
		if (TextUtils.isEmpty(socialAction)) {
			// social action empty means it's add/replace or remove
			if (ActionCmd.PACKAGE_ADDED.equals(action)) {
				socialAction = ACTION_TYPE_ADD;
			} else if (ActionCmd.PACKAGE_REPLACED.equals(action)) {
				socialAction = ACTION_TYPE_REPLACE;
			} else {
				socialAction = ACTION_TYPE_REMOVE;
			}
		}
		boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING,
				false);
		Uri packageNameUri = intent.getData();

		if (packageNameUri == null) {
			return;
		}

		for (String pkgName : clientPkgNameWatchingList) {
			if (pkgName.equals(packageNameUri.getSchemeSpecificPart())) {
				Intent changeIntent = new Intent(
						ACTION_BLINKFEED_CLIENT_CHANGED);
				changeIntent.putExtra("action", socialAction);
				changeIntent.putExtra(Intent.EXTRA_REPLACING, replacing);
				if (packageNameUri != null) {
					changeIntent.setData(Uri.parse("social://"
							+ packageNameUri.getSchemeSpecificPart()));
				}
				getBaseContext().sendBroadcast(changeIntent);
				break;
			}
		}
	}

	private void killProcessByName(String processName) {
		if (TextUtils.isEmpty(processName)) {
			Log.e(LOG_TAG, "process name is null or empty!");
			return;
		}
		Log.i(LOG_TAG, "attempt to kill process: " + processName + " ...");
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		if (am != null) {
			List<RunningAppProcessInfo> appProcessList = am
					.getRunningAppProcesses();
			if (appProcessList == null) {
				return;
			}

			try {
				for (RunningAppProcessInfo info : appProcessList) {
					if (processName.equals(info.processName)) {
						android.os.Process.killProcess(info.pid);
						Log.i(LOG_TAG, "process " + processName + " killed!");
					}
				}
			} catch (Exception e) {
				Log.e(LOG_TAG, "error when kill process");
			}
		}
	}
}