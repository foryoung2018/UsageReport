package com.htc.lib2.mock.opensense.pluginmanager;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.htc.lib2.mock.opensense.pluginmanager.data.FeatureList;
import com.htc.lib2.opensense.internal.SystemWrapper;
import com.htc.lib2.opensense.internal.SystemWrapper.HtcBuildFlag;
import com.htc.lib2.opensense.plugin.PluginConstants;
import com.htc.lib2.opensense.test.plugin.PluginConstantsTestCase;
import com.htc.lib2.opensense.tests2.R;

public class PluginProvider extends ContentProvider implements PluginConstants {

	final static String TAG = PluginProvider.class.getSimpleName();

	private final static int FEATURES = 0;
	private final static int FEATURES_ID = 1;
	private final static int METADATA = 2;
	private final static int METADATA_ID = 3;
	private final static int PLUGIN_RAWQUERY = 9;
	private final static int PLUGIN = 4;
	private final static int PLUGIN_ID = 5;
	private final static int PLUGIN_PKG = 6;
//	private final static int PLUGIN_PKG_ID = 7;
	private final static int RAWQUERY = 8;
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	final static String URL_FEATURES = "features";
	final static String URL_FEATURES_ID = "features/#";
	final static String URL_METADATA = "meta_data";
	final static String URL_METADATA_ID = "meta_data/#";
	final static String URL_PLUGIN_RAWQUERY = "plugins_raw";
	final static String URL_PLUGIN = "plugins";
	final static String URL_PLUGIN_ID = "plugins/#";
	final static String URL_PLUGIN_PKG = "plugin_pkg";
	final static String URL_RAW_QUERY = "rawquery";
	final static String URL_PLUGIN_PKG_ID = "plugin_pkg/#";

	private DatabaseHelper mDatabaseHelper;

	static {
			SystemWrapper.setPluginManagerAuthority(PluginConstantsTestCase.PLUGINMANAGER_AUTHORITY);
			sURIMatcher.addURI(AUTHORITY, URL_FEATURES, FEATURES);
			sURIMatcher.addURI(AUTHORITY, URL_FEATURES_ID, FEATURES_ID);
			sURIMatcher.addURI(AUTHORITY, URL_METADATA, METADATA);
			sURIMatcher.addURI(AUTHORITY, URL_METADATA_ID, METADATA_ID);
			sURIMatcher.addURI(AUTHORITY, URL_PLUGIN_RAWQUERY, PLUGIN_RAWQUERY);
			sURIMatcher.addURI(AUTHORITY, URL_PLUGIN, PLUGIN);
			sURIMatcher.addURI(AUTHORITY, URL_PLUGIN_ID, PLUGIN_ID);
			sURIMatcher.addURI(AUTHORITY, URL_PLUGIN_PKG, PLUGIN_PKG);
			sURIMatcher.addURI(AUTHORITY, URL_RAW_QUERY, RAWQUERY);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		private final static String DATABASE_NAME = "registry.db";
		private final static int DATABASE_VERSION = 1;

		private Context mContext;

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			mContext = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			createTable(db);
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
			dropTable(db);
			createTable(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG,"onUpgrade databases from " + oldVersion + " to " + newVersion);
			dropTable(db);
			createTable(db);
		}

		@Override
		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "[onDowngrade]");
		}

		private void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + FEATURE_TB + "(" + _ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_VERSION
					+ " INTEGER, " + COLUMN_FEATURE + " TEXT, "
					+ COLUMN_FEATURE_TYPE + " TEXT, UNIQUE(" + COLUMN_VERSION + ","
					+ COLUMN_FEATURE + "));");
			db.execSQL("CREATE TABLE " + METADATA_TB + "(" + _ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PACKAGE_ID
					+ " INTEGER, " + COLUMN_META_NAME + " TEXT, "
					+ COLUMN_META_TYPE + " TEXT, " + COLUMN_META_VALUE + " TEXT);");
			db.execSQL("CREATE TABLE " + PLUGIN_TB + "(" + _ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PACKAGE_ID
					+ " INTEGER, " + COLUMN_FEATURE_ID + " INTEGER, "
					+ COLUMN_VERSION + " INTEGER, " + COLUMN_PLUGIN_CLASS
					+ " TEXT, " + COLUMN_DESCRIPTION + " TEXT, "
					+ COLUMN_PLUGIN_META + " TEXT, " + COLUMN_PLUGIN_REMOVED
					+ " BOOLEAN, " + "UNIQUE(" + COLUMN_PACKAGE_ID + ", "
					+ COLUMN_FEATURE_ID + ", " + COLUMN_PLUGIN_CLASS + "));");
			db.execSQL("CREATE TABLE " + PLUGIN_PKG_TB + "(" + _ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PACKAGE
					+ " TEXT UNIQUE, " + COLUMN_CERTIFICATE + " TEXT, "
					+ COLUMN_ICON + " BLOB);");
			initFeatures(db);
		}

		private void dropTable(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS " + FEATURE_TB);
			db.execSQL("DROP TABLE IF EXISTS " + METADATA_TB);
			db.execSQL("DROP TABLE IF EXISTS " + PLUGIN_TB);
			db.execSQL("DROP TABLE IF EXISTS " + PLUGIN_PKG_TB);
		}

		private void initFeatures(SQLiteDatabase db) {
			XmlResourceParser parser = mContext.getResources().getXml(R.xml.feature);
			FeatureList list = null;
			try {
				list = FeatureList.parse(parser);
			} catch ( XmlPullParserException e ) {
				Log.e(TAG, "Error when parsing features", e);
			} catch ( IOException e ) {
				Log.e(TAG, "Error when loading features", e);
			}

			if ( list != null ) {
				for ( FeatureList.Feature feature : list ) {
					ContentValues values = new ContentValues();
					values.put(COLUMN_VERSION, feature.getVersion());
					values.put(COLUMN_FEATURE, feature.getName());
					values.put(COLUMN_FEATURE_TYPE, feature.getType());
					db.insert(FEATURE_TB, null, values);
				}
			}
		}
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		Log.d(TAG, "bulk Insert");
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			for ( int i = 0; i < values.length; i++ ) {
				insert(uri, values[i]);
			}
			db.setTransactionSuccessful();
			Log.d(TAG, "bulk Insert success");
		} finally {
			db.endTransaction();
		}
		return values.length;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int match = sURIMatcher.match(uri);
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

		String tb = getMatchTB(match);
		if (tb != null) {
			if (HtcBuildFlag.Htc_DEBUG_flag && selectionArgs != null) {
				Log.d(TAG, "delete " + selection + " from " + tb);
				for (String arg : selectionArgs) {
					Log.d(TAG, "arg = " + arg);
				}
			}
			db.delete(tb, selection, selectionArgs);
		}
		return 0;
	}

	String getMatchTB(int match) {
		String tb = null;
		switch ( match ) {
		case FEATURES:
			tb = FEATURE_TB;
			break;
		case METADATA:
			tb = METADATA_TB;
			break;
		case PLUGIN:
			tb = PLUGIN_TB;
			break;
		case PLUGIN_PKG:
			tb = PLUGIN_PKG_TB;
			break;
		}
		return tb;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> arg0) throws OperationApplicationException {
		Log.d(TAG, "Begin Apply Batch");
		final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		db.beginTransaction();
		ContentProviderResult[] results = null;
		try {
			results = super.applyBatch(arg0);
			db.setTransactionSuccessful();
			Log.d(TAG, "apply Batch success");
		} finally {
			db.endTransaction();
		}
		return results;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final int match = sURIMatcher.match(uri);
		final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		long rowID = -1;
		final String tb = getMatchTB(match);

		switch ( match ) {
			case FEATURES: {
				rowID = db.insertWithOnConflict(tb, null, values, SQLiteDatabase.CONFLICT_IGNORE);
				if ( rowID == -1 ) {
					Log.e(TAG, "conflict when insert feature, ignored");
					return uri;
				}
				break;
			}
			case PLUGIN: {
				rowID = db.replace(tb, null, values);
				break;
			}
			case PLUGIN_RAWQUERY: {
				String sql = "REPLACE INTO " + PLUGIN_TB + " (" + COLUMN_PACKAGE_ID
						+ ", " + COLUMN_FEATURE_ID + ", " + COLUMN_PLUGIN_CLASS + ", "
						+ COLUMN_VERSION + ", " + COLUMN_DESCRIPTION + ", "
						+ COLUMN_PLUGIN_META + ", " + COLUMN_PLUGIN_REMOVED
						+ ") SELECT ?, " + _ID + ", ?, ?, ?, ?, ? FROM " + FEATURE_TB
						+ " WHERE " + COLUMN_FEATURE + "=? ORDER BY " + COLUMN_VERSION
						+ " DESC LIMIT 1";
				Object[] bindArgs = new Object[] {
						values.getAsLong(COLUMN_PACKAGE_ID),
						values.getAsString(COLUMN_PLUGIN_CLASS),
						values.getAsInteger(COLUMN_VERSION),
						values.getAsString(COLUMN_DESCRIPTION),
						values.getAsString(COLUMN_PLUGIN_META),
						values.getAsBoolean(COLUMN_PLUGIN_REMOVED),
						values.getAsString(COLUMN_FEATURE)
				};
				try {
					rowID = 0;
					db.execSQL(sql, bindArgs);
				} catch ( SQLException e ) {
					Log.e(TAG, "Failed to insert row into " + uri);
					rowID = -1;
				}
				break;
			}
			default: {
				try {
					rowID = db.insert(tb, null, values);
				} catch ( Exception e ) {
					Log.e(TAG, "Failed to insert row into " + uri);
				}
				break;
			}
		}

		if ( rowID != -1 ) {
			final Uri ret = ContentUris.withAppendedId(uri, rowID);
			return ret;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		Log.d(TAG,"PluginProvider onCreate");
		mDatabaseHelper = new DatabaseHelper(getContext());
		boolean isDbNull = false;
		Cursor cursor = null;
		try {
			cursor = mDatabaseHelper.getReadableDatabase().rawQuery("select count(*) from " + PLUGIN_TB , null);
			cursor.moveToFirst();
			int count = cursor.getInt(0);
			Log.d(TAG, "current plugin count: " + count);
			if(count == 0) {
				isDbNull = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			isDbNull = true;
		} finally {
			if ( cursor != null ) {
				cursor.close();
			}
		}

		if ( isDbNull ) {
			Log.d(TAG, "Plugin DB is null, requery again");
			Intent intent = new Intent(RegisterService.ACTION_PACKAGE_RESCAN);
			getContext().startService(intent.setClass(getContext(), RegisterService.class));
		}

		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

		int match = sURIMatcher.match(uri);
		if ( match == RAWQUERY ) {
			return db.rawQuery(selection, selectionArgs);
		} else {
			String tb = getMatchTB(match);
			return db.query(tb, projection, selection, selectionArgs, null, null, sortOrder);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		if ( HtcBuildFlag.Htc_DEBUG_flag ) {
			if ( values.containsKey(COLUMN_PLUGIN_REMOVED) ) {
				Log.d(TAG, "Remove " + selection);
				for (String arg : selectionArgs) {
					Log.d(TAG, "arg = " + arg);
				}
			}
		}
		int match = sURIMatcher.match(uri);
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		String tb = getMatchTB(match);
		return db.update(tb, values, selection, selectionArgs);
	}
}
