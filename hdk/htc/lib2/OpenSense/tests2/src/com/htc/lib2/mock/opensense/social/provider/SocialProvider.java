package com.htc.lib2.mock.opensense.social.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.accounts.Account;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.UriMatcher;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Binder;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.htc.lib2.mock.opensense.social.provider.SocialDatabase.Tables;
import com.htc.lib2.opensense.social.SelectionBuilder;
import com.htc.lib2.opensense.social.SocialContract;
import com.htc.lib2.opensense.social.SocialContract.Stream;
import com.htc.lib2.opensense.social.SocialContract.SyncCursors;
import com.htc.lib2.opensense.social.SocialContract.SyncTypeContract;
import com.htc.lib2.opensense.social.SocialManager;

public class SocialProvider extends ContentProvider {

	private static final String TAG = SocialProvider.class.getSimpleName();

	private SocialDatabase mOpenHelper;

	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private static final int STREAM = 100;
	private static final int CURSORS = 200;
	private static final int CURSORS_ENDTIME_AFTER = 201;
	
	private static final int RAW = 1000;
	private static final int TOPSTORY = 2000;
	private static final int GET_PREFERENCE = 3000;
	private static final int SYNCTYPE = 4000;
	private static final int STREAMBUNDLE = 5000;
	private static final int EDIT_PREFERENCE = 6000;
	private static final int ENABLED_SYNCTYPE = 7000;

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = SocialContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, "stream", STREAM);
		matcher.addURI(authority, "cursors", CURSORS);
		matcher.addURI(authority,
				"cursors/account_type/*/account_name/*/sync_type/*/end_after/#",
				CURSORS_ENDTIME_AFTER);
		matcher.addURI(authority, "raw", RAW);
		matcher.addURI(authority, "stream/topstory", TOPSTORY);
		matcher.addURI(authority, "get_preference", GET_PREFERENCE);
		matcher.addURI(authority, "synctype", SYNCTYPE);
		matcher.addURI(authority, "streambundle", STREAMBUNDLE);
		matcher.addURI(authority, "edit_preference/*", EDIT_PREFERENCE);
		matcher.addURI(authority, "enabled_synctype", ENABLED_SYNCTYPE);

		return matcher;
	}

	@Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case STREAM:
			return Stream.CONTENT_TYPE;
		case CURSORS:
		case CURSORS_ENDTIME_AFTER:
			return SyncCursors.CONTENT_TYPE;
		case SYNCTYPE:
		case ENABLED_SYNCTYPE:
			return SyncTypeContract.CONTENT_TYPE;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new SocialDatabase(getContext());
		setupUidWhiteList();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		final int match = sUriMatcher.match(uri);
		switch(match){
		case TOPSTORY: {
			StringBuilder builder = new StringBuilder();
			String extraSelection = "";
			
			if (!TextUtils.isEmpty(selection)) {
				builder.append(" AND (");
				builder.append(selection);
				builder.append(")");
				extraSelection = builder.toString();
				builder.delete(0, builder.length());
			}
			
			//append uid where clause for security concern.
			int callingUid = Binder.getCallingUid();
			// check caller is in white list or not
			if (!mUidWhitelist.contains(callingUid)) {
				if (!TextUtils.isEmpty(extraSelection)) {
					builder.append(extraSelection);
				}
				builder.append(" AND ");
				builder.append("(");
				builder.append(Stream.COLUMN_OWNER_UID_INT);
				builder.append("=");
				builder.append(String.valueOf(callingUid));
				builder.append(")");
				extraSelection = builder.toString();
				builder.delete(0, builder.length());
			}
			
			Cursor cursor = db.query(Tables.STREAM_BUDNEL_VIEW, projection, BaseColumns._ID
					+ " in (SELECT " + BaseColumns._ID + " FROM "
					+ Tables.TOPSTREAM + ")" + extraSelection, selectionArgs,
					null, null, sortOrder);
			// TODO , notify URI?
			return cursor;
		}
		case GET_PREFERENCE:{
			return getPreferenceCursor(uri);
		}
		default:{
			final SelectionBuilder builder = buildSimpleSelection(uri);
			if (match == STREAM) {
				// override to stream bundle view if doing query.
				builder.table(Tables.STREAM_BUDNEL_VIEW);
			}
			
			if (match == SYNCTYPE) {
				builder.table(Tables.ENABLED_SYNCTYPE_VIEW);
			}

			String limit = uri.getQueryParameter(SocialManager.KEY_LIMIT);
			Cursor cursor = builder.where(selection, selectionArgs).query(db,
					projection, null, null, sortOrder, limit);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
		}
		}
	}

	private ArrayList<Integer> mUidWhitelist = new ArrayList<Integer>();

	private void setupUidWhiteList() {

		PackageManager pm = getContext().getPackageManager();
		List<ApplicationInfo> apps = pm.getInstalledApplications(0);
		for (ApplicationInfo app : apps) {
			if (pm.checkPermission("htc.socialmanager.permission.SOCIAL_HOST",
					app.packageName) == PackageManager.PERMISSION_GRANTED) {
				mUidWhitelist.add(app.uid);
			}
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		final int match = sUriMatcher.match(uri);
		long row = -1;
		switch (match) {
		case STREAM: {
			values.put(Stream.COLUMN_OWNER_UID_INT, Binder.getCallingUid());
			row = db.replaceOrThrow(Tables.STREAM, null, values);
			
			if (row != -1) {
				Uri ret = ContentUris.withAppendedId(uri, row);
				return ret;
			} else {
				return null;
			}
		}
		case CURSORS: {
			row = insertOrMergeCursors(db, values);
			if (row != -1) {
				Uri ret = ContentUris.withAppendedId(uri, row);
				getContext().getContentResolver().notifyChange(ret, null);
				return ret;
			} else {
				return null;
			}
		}
		case SYNCTYPE: {
			values.put("owner_uid", Binder.getCallingUid());
			row = db.replaceOrThrow(Tables.SYNCTYPE, null, values);

			if (row != -1) {
				Uri ret = ContentUris.withAppendedId(uri, row);
				return ret;
			} else {
				return null;
			}
		}
		case ENABLED_SYNCTYPE : {
			row = db.replaceOrThrow(Tables.ENABLED_SYNCTYPE, null, values);
			if (row != -1) {
				Uri ret = ContentUris.withAppendedId(uri, row);
				return ret;
			} else {
				return null;
			}
		}
		case STREAMBUNDLE : {
			row = db.replaceOrThrow(Tables.STREAMBUNDLE, null, values);
			if (row != -1) {
				Uri ret = ContentUris.withAppendedId(uri, row);
				return ret;
			} else {
				return null;
			}
		}
		case EDIT_PREFERENCE : {
			editPreference(uri, values);
			return uri;
		}
		default: {
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		}
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		int retVal = 0;
		if (sUriMatcher.match(uri) == RAW) {
			db.compileStatement(selection).executeUpdateDelete();
		}else{
			final SelectionBuilder builder = buildSimpleSelection(uri);
			retVal = builder.where(selection, selectionArgs).update(db,
					values);
		}
		return retVal;

	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final SelectionBuilder builder = buildSimpleSelection(uri);
		int retVal = builder.where(selection, selectionArgs).delete(db);
		return retVal;
	}

	private SelectionBuilder buildSimpleSelection(Uri uri) {
		final SelectionBuilder builder = new SelectionBuilder();
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case STREAM: {
			
			//append uid where clause for security concern.
			int callingUid = Binder.getCallingUid();
			//check caller is in white list or not
			if (!mUidWhitelist.contains(callingUid)) {
				builder.where(Stream.COLUMN_OWNER_UID_INT + "=?",
						String.valueOf(callingUid));
			}
			
			//apend syncType if parameter exist
			String syncTypeString = uri
					.getQueryParameter(Stream.PARA_STREAM_SYNC_TYPE);
			if (!TextUtils.isEmpty(syncTypeString)) {
				builder.appendWhere(Stream.COLUMN_SYNC_TYPE_STR + " like '%"
						+ encodeSyncType(syncTypeString) + "%'");
			}
			
			// append streamType if parameter exist
			String streamTypeString = uri
					.getQueryParameter(Stream.PARA_STREAM_TYPE);
			if (!TextUtils.isEmpty(streamTypeString)) {
				builder.appendWhere("(" + Stream.COLUMN_STREAM_TYPE_INT + "&"
						+ streamTypeString + ")<>0");
			}

			//append start end time if parameter exist
			String startTime = uri.getQueryParameter(Stream.PARA_STREAM_BETWEEN_START);
			String endTime = uri.getQueryParameter(Stream.PARA_STREAM_BETWEEN_END);
			if(startTime != null && endTime != null && 
					!startTime.isEmpty() && !endTime.isEmpty()){
				builder.where(Stream.COLUMN_TIMESTAMP_LONG + ">=?", startTime);
				builder.where(Stream.COLUMN_TIMESTAMP_LONG + "<?", endTime);
			}
			
			//append account name and type if parameter exist
			List<String> typeList = uri.getQueryParameters(Stream.PARA_STREAM_ACCOUNT_TYPE);
			List<String> nameList = uri.getQueryParameters(Stream.PARA_STREAM_ACCOUNT_NAME);
			
			StringBuilder orBuilder = new StringBuilder();
			if(!typeList.isEmpty() && !nameList.isEmpty()){
				for(int i = 0 ; i < typeList.size() ; i++ ){
					if(orBuilder.length() > 0 ){
						orBuilder.append(" OR ");
					}
					orBuilder.append("(");
					orBuilder.append(Stream.COLUMN_ACCOUNT_TYPE_STR 
							+ "=" + DatabaseUtils.sqlEscapeString(typeList.get(i)) );
					orBuilder.append(" AND ");
					orBuilder.append(Stream.COLUMN_ACCOUNT_NAME_STR 
							+ "=" + DatabaseUtils.sqlEscapeString(nameList.get(i)));
					orBuilder.append(")");
				}
			}
			builder.appendWhere(orBuilder.toString());
			
			//append poster and account type if parameter exist
			List<String> posterList = uri.getQueryParameters(Stream.PARA_STREAM_POSTER);
			
			if(!posterList.isEmpty() && typeList.isEmpty()){
				
				Log.d(TAG,"poster and account type should be linked!");
				throw new UnsupportedOperationException("Unknown uri: " + uri);
			} else if (!posterList.isEmpty()) {
				
				orBuilder.delete(0, orBuilder.length());
				
				for(int i = 0 ; i < posterList.size() ; i++ ){
					if(orBuilder.length() > 0 ){
						orBuilder.append(" OR ");
					}
					orBuilder.append("(");
					orBuilder.append(Stream.COLUMN_POSTER_ID_STR 
							+ "=" + DatabaseUtils.sqlEscapeString(posterList.get(i)));
					orBuilder.append(" AND ");
					orBuilder.append(Stream.COLUMN_ACCOUNT_TYPE_STR 
							+ "=" + DatabaseUtils.sqlEscapeString(typeList.get(i)));
					orBuilder.append(")");
				}
				builder.appendWhere(orBuilder.toString());
			}
			
			String latestOnly = uri.getQueryParameter(Stream.PARA_STREAM_LATEST);
			
			if (latestOnly != null && latestOnly.equals("true")) {
				String maxSubqueryString = Utils.generateMaxStartTimeString(
						typeList, nameList, syncTypeString);
				builder.appendWhere(Stream.COLUMN_TIMESTAMP_LONG + ">="
						+ maxSubqueryString);
			}
			return builder.table(Tables.STREAM);
		}
		case CURSORS: {
			return builder.table(Tables.SYNCCURSORS);
		}
		case CURSORS_ENDTIME_AFTER: {
			final String end_after_time = SyncCursors
					.getSyncCursorsEndAfterTime(uri);
			final String accountType = SyncCursors
					.getSyncCursorsAccountType(uri);
			final String accountName = SyncCursors
					.getSyncCursorsAccountName(uri);
			final String syncType = SyncCursors
					.getSyncCursorsSyncType(uri);
			return builder
					.table(Tables.SYNCCURSORS)
					.where(SyncCursors.COLUMN_END_TIME_LONG + ">=?",
							end_after_time)
					.where(SyncCursors.COLUMN_ACCOUNT_TYPE_STR + "=?",
							accountType)
					.where(SyncCursors.COLUMN_ACCOUNT_NAME_STR + "=?",
							accountName)
					.where(SyncCursors.COLUMN_SYNC_TYPE + "=?",
							syncType);
		}
		case SYNCTYPE: {
			// append account name and type if parameter exist
			List<String> typeList = uri
					.getQueryParameters(SyncTypeContract.PARA_ACCOUNT_TYPE);
			List<String> nameList = uri
					.getQueryParameters(SyncTypeContract.PARA_ACCOUNT_NAME);

			StringBuilder orBuilder = new StringBuilder();
			if (!typeList.isEmpty() && !nameList.isEmpty()) {
				for (int i = 0; i < typeList.size(); i++) {
					if (orBuilder.length() > 0) {
						orBuilder.append(" OR ");
					}
					orBuilder.append("(");
					orBuilder.append(SyncTypeContract.COLUMN_ACCOUNT_TYPE_STR
							+ "="
							+ DatabaseUtils.sqlEscapeString(typeList.get(i)));
					orBuilder.append(" AND ");
					orBuilder.append(SyncTypeContract.COLUMN_ACCOUNT_NAME_STR
							+ "="
							+ DatabaseUtils.sqlEscapeString(nameList.get(i)));
					orBuilder.append(")");
				}
			}
			builder.appendWhere(orBuilder.toString());
			
			//append uid where clause for security concern.
			int callingUid = Binder.getCallingUid();
			//check caller is in white list or not
			if (!mUidWhitelist.contains(callingUid)) {
				builder.where("owner_uid" + "=?",
						String.valueOf(callingUid));
			}

			return builder.table(Tables.SYNCTYPE);
		}
		case ENABLED_SYNCTYPE : {
			return builder.table(Tables.ENABLED_SYNCTYPE);
		}
		default: {
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		}
	}
	
	private static String encodeSyncType(String syncType) {
		return "[" + syncType + "]";
	}

	public static class Utils {
		private static final String MAX_START_TIME = "SELECT MAX("
				+ SyncCursors.COLUMN_START_TIME_LONG + ") FROM "
				+ Tables.SYNCCURSORS;
		
		public static String generateMaxStartTimeString(
				List<String> typeList, List<String> nameList, String syncType){
			StringBuilder builder = new StringBuilder();
			
			builder.append("(");
			builder.append(MAX_START_TIME);
			builder.append(" WHERE ");
			if (!TextUtils.isEmpty(syncType)) {
				builder.append(SyncCursors.COLUMN_SYNC_TYPE + "="
						+ DatabaseUtils.sqlEscapeString(syncType));
			}
			if(!typeList.isEmpty() && !nameList.isEmpty()){
				if (!TextUtils.isEmpty(syncType)) {
					builder.append(" AND ");
				}
				builder.append("(");
				StringBuilder orBuilder = new StringBuilder();
				for(int i = 0 ; i < typeList.size() ; i++ ){
					if(orBuilder.length() > 0 ){
						orBuilder.append(" OR ");
					}
					orBuilder.append("(");
					orBuilder.append(SyncCursors.COLUMN_ACCOUNT_TYPE_STR 
							+ "=" + DatabaseUtils.sqlEscapeString(typeList.get(i)));
					orBuilder.append(" AND ");
					orBuilder.append(SyncCursors.COLUMN_ACCOUNT_NAME_STR 
							+ "=" + DatabaseUtils.sqlEscapeString(nameList.get(i)));
					orBuilder.append(")");
				}
				builder.append(orBuilder.toString());
				builder.append(")");
			}
			
			builder.append(")");
			return builder.toString();
		}
		
		public static String generateMaxEndTimeString(Account[] accounts,
				String[] syncTypeIds) {
			//select max endtime from cursors where syncTypeIds in (ids) AND account in (accounts) GROUP BY syncType
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT MAX(" + SyncCursors.COLUMN_END_TIME_LONG
					+ ") AS time FROM "
					+ Tables.SYNCCURSORS + " WHERE ");
			
			if (syncTypeIds != null && syncTypeIds.length > 0) {
				StringBuilder args = new StringBuilder();
				for (int i = 0; i < syncTypeIds.length; i++) {
					if (args.length() > 0) {
						args.append(",");
					}
					args.append(DatabaseUtils.sqlEscapeString(syncTypeIds[i]));
				}
				builder.append(SyncCursors.COLUMN_SYNC_TYPE);
				builder.append(" in (");
				builder.append(args.toString());
				builder.append(")");
			}
			
			if (accounts != null && accounts.length > 0) {
				if (syncTypeIds != null && syncTypeIds.length > 0) {
					builder.append(" AND");
				}
				builder.append("(");
				StringBuilder orBuilder = new StringBuilder();
				for(int i = 0 ; i < accounts.length ; i++ ){
					if(orBuilder.length() > 0 ){
						orBuilder.append(" OR ");
					}
					orBuilder.append("(");
					orBuilder.append(SyncCursors.COLUMN_ACCOUNT_TYPE_STR 
							+ "=" + DatabaseUtils.sqlEscapeString(accounts[i].type));
					orBuilder.append(" AND ");
					orBuilder.append(SyncCursors.COLUMN_ACCOUNT_NAME_STR 
							+ "=" + DatabaseUtils.sqlEscapeString(accounts[i].name));
					orBuilder.append(")");
				}
				builder.append(orBuilder.toString());
				builder.append(")");
			}
			builder.append(" GROUP BY ");
			builder.append(SyncCursors.COLUMN_SYNC_TYPE);
			
			return builder.toString();
		}
		
		public static String genMaxEndTimeTokenString(Account account,
				String syncType) {
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT ");
			builder.append(SyncCursors.COLUMN_PAGE_TOKEN);
			builder.append(" FROM ");
			builder.append(Tables.SYNCCURSORS);
			builder.append(" WHERE ");
			if (!TextUtils.isEmpty(syncType)) {
				builder.append(SyncCursors.COLUMN_SYNC_TYPE + "="
						+ DatabaseUtils.sqlEscapeString(syncType));
				builder.append(" AND ");
			}

			builder.append(SyncCursors.COLUMN_ACCOUNT_TYPE_STR);
			builder.append("=");
			builder.append(DatabaseUtils.sqlEscapeString(account.type));

			builder.append(" AND ");

			builder.append(SyncCursors.COLUMN_ACCOUNT_NAME_STR);
			builder.append("=");
			builder.append(DatabaseUtils.sqlEscapeString(account.name));

			builder.append(" ORDER BY ");
			builder.append(SyncCursors.COLUMN_END_TIME_LONG);
			builder.append(" DESC");
			
			return builder.toString();
		}
	}

	private static String AS_MAX_ENDTIME = "max_endtime";
	private static String AS_MIN_STARTTIME = "min_starttime";

	private long insertOrMergeCursors(SQLiteDatabase db, ContentValues values) {

		final String accountName = values
				.getAsString(SyncCursors.COLUMN_ACCOUNT_NAME_STR);
		final String accountType = values
				.getAsString(SyncCursors.COLUMN_ACCOUNT_TYPE_STR);
		long startTime = values.getAsLong(SyncCursors.COLUMN_START_TIME_LONG);
		long endTime = values.getAsLong(SyncCursors.COLUMN_END_TIME_LONG);
		String syncType = values.getAsString(SyncCursors.COLUMN_SYNC_TYPE);

		// update new record's start time & end time with existing records.
		String sql = String
				.format(Locale.US, "SELECT MAX(%s) AS %s, MIN(%s) AS %s FROM %s WHERE %s =%s AND %s =%s AND %s >=%s AND %s =%s",
						SyncCursors.COLUMN_END_TIME_LONG, AS_MAX_ENDTIME,
						SyncCursors.COLUMN_START_TIME_LONG, AS_MIN_STARTTIME,
						Tables.SYNCCURSORS,
						SyncCursors.COLUMN_ACCOUNT_NAME_STR, DatabaseUtils.sqlEscapeString(accountName),
						SyncCursors.COLUMN_ACCOUNT_TYPE_STR, DatabaseUtils.sqlEscapeString(accountType),
						SyncCursors.COLUMN_END_TIME_LONG, startTime,
						SyncCursors.COLUMN_SYNC_TYPE, DatabaseUtils.sqlEscapeString(syncType));
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		long maxEndTime = cursor.getLong(0);
		long minStartTime = cursor.getLong(1);
		cursor.close();

		// maxEndTime == 0 means there is no record, use input startTime.
		minStartTime = maxEndTime == 0 ? startTime : Math.min(startTime, minStartTime);
		maxEndTime = Math.max(endTime, maxEndTime);

		values.remove(SyncCursors.COLUMN_START_TIME_LONG);
		values.remove(SyncCursors.COLUMN_END_TIME_LONG);
		values.put(SyncCursors.COLUMN_START_TIME_LONG, minStartTime);
		values.put(SyncCursors.COLUMN_END_TIME_LONG, maxEndTime);

		// delete old record
		delete(SyncCursors.getUriWithAccTypeAccNameEndAfterTime(accountType,
				accountName, startTime, syncType), null, null);

		// insert new record
		return db.insertOrThrow(Tables.SYNCCURSORS, null, values);
	}
	
	private Cursor getPreferenceCursor(Uri uri) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getContext());
		if (prefs != null) {
			String key = uri.getQueryParameter("key");
			if (!TextUtils.isEmpty(key)) {
				Map<String, ?> allPrefsMap = prefs.getAll();
				if (allPrefsMap == null) {
					return null;
				}
				MatrixCursor cursor = new MatrixCursor(new String[]{"value"});
				cursor.addRow(new Object[]{allPrefsMap.get(key)});
				return cursor;
			}

			List<String> keys = uri.getQueryParameters("keys");
			if (keys != null && keys.size() > 0) {
				Map<String, ?> allPrefsMap = prefs.getAll();
				if (allPrefsMap == null) {
					return null;
				}

				MatrixCursor cursor = new MatrixCursor(new String[]{"key",
						"value"});
				for (String keyString : keys) {
					Object value = allPrefsMap.get(keyString);
					if (value != null) {
						cursor.addRow(new Object[]{keyString,
								allPrefsMap.get(keyString)});
					}
				}
				return cursor;
			}
		}
		return null;
	}
	
	private void editPreference(Uri uri, ContentValues values) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getContext());

		if (uri.getPathSegments() == null || uri.getPathSegments().size() < 2) {
			Log.e("SocialManager", "error calling edit preference with uri "
					+ uri);
			return;
		}
		
		String type = uri.getPathSegments().get(1);
		if ("account_enabling".equals(type)) {
			Editor editor = prefs.edit();
			for (String accountType : values.keySet()) {
				if (values.getAsBoolean(accountType)) {
					// default value is true, so we just delete the preference.
					editor.remove(genAccountKey(accountType));
				} else {
					editor.putBoolean(genAccountKey(accountType), false);
				}
			}
			editor.apply();
		}
	}

	private String genAccountKey(String type) {
		return "key_enable_account_" + type;
	}
}
