package com.htc.lib2.mock.opensense.social.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.htc.lib2.opensense.social.SocialContract;
import com.htc.lib2.opensense.social.SocialContract.Stream;
import com.htc.lib2.opensense.social.SocialContract.SyncCursors;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

public class LocalMergeHelper {
	private static final String LOG_TAG = "SocialManagerService";
	private static final int BATCH_LIMIT = 30;
	
	private ContentResolver mResolver;
	private static LocalMergeHelper sInstance = null;
	
	private LocalMergeHelper(Context context){
		mResolver = context.getContentResolver();
	}
	
	public static LocalMergeHelper getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new LocalMergeHelper(context.getApplicationContext());
		}
		return sInstance;
	}
	
	public void mergeStreamToDb(long endTime, long startTime, Account account,
			List<ContentValues> values, String[] syncTypes, String pageToken,
			boolean wipeOldData) {
		if (syncTypes == null || syncTypes.length < 1) {
			Log.e(LOG_TAG, "mergeToDB , syncTypes is null or empty, do nothing");
			return;
		}

		if (account == null) {
			Log.e(LOG_TAG, "mergeToDB , account is null, do nothing");
			return;
		}

		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		//wipe old stream data & sync cursors data.
		if (wipeOldData) {
			addWipeOldStreamTimelineRecordOps(account, syncTypes, operations);
		}
		
		// insert or update the streams of the interval
		if (values != null) {
			handleInsertStream(account, operations, values);
		}

		// insert or merge the sync time line in SyncCursors table by syncType
		for (String syncType : syncTypes) {
			addInsertOrMergeSyncCursorsOperation(operations, account.name,
					account.type, syncType, startTime, endTime, pageToken);
		}

		if (operations.size() > 0) {
			applyBatchAndReset(SocialContract.CONTENT_AUTHORITY, operations);
		}

		mResolver.notifyChange(Stream.CONTENT_URI, null);
	}
	
	private void addWipeOldStreamTimelineRecordOps(Account account,
			String[] syncTypes, ArrayList<ContentProviderOperation> operations) {
		if (syncTypes == null || syncTypes.length == 0) {
			return;
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < syncTypes.length; i++) {
			if (builder.length() > 0) {
				builder.append(",");
			}
			builder.append(DatabaseUtils.sqlEscapeString(syncTypes[i]));
		}

		operations.add(ContentProviderOperation
				.newDelete(SyncCursors.CONTENT_URI)
				.withSelection(
						SyncCursors.COLUMN_SYNC_TYPE + " in ("
								+ builder.toString() + ")", null).build());
	}

	private void addInsertOrMergeSyncCursorsOperation(
			ArrayList<ContentProviderOperation> operations, String accountName,
			String accountType, String syncType, long startTime, long endTime,
			String pageToken) {
		
		ContentValues cursorsValue = new ContentValues();
		cursorsValue.put(SyncCursors.COLUMN_ACCOUNT_NAME_STR, accountName);
		cursorsValue.put(SyncCursors.COLUMN_ACCOUNT_TYPE_STR, accountType);
		cursorsValue.put(SyncCursors.COLUMN_SYNC_TYPE, syncType);
		cursorsValue.put(SyncCursors.COLUMN_START_TIME_LONG, startTime);
		cursorsValue.put(SyncCursors.COLUMN_END_TIME_LONG, endTime);
		cursorsValue.put(SyncCursors.COLUMN_PAGE_TOKEN, pageToken);

		operations.add(ContentProviderOperation.newInsert(SyncCursors.CONTENT_URI)
				.withValues(cursorsValue)
				.build());
	}

	private void handleInsertStream(Account account,
			ArrayList<ContentProviderOperation> operations,
			List<ContentValues> values) {
		if (values != null) {
			//generate existing sync types and post id mapping in db
			HashMap<String, String> existingSyncTypeMap = buildExistingSyncTypeMap(
					account, values);
			
			for (ContentValues value : values) {
				//merge inserting sync types with existing ones
				mergeAndSplitSyncTypes(existingSyncTypeMap, value);
				
				addInsertStreamOperations(operations, value);
				if (operations.size() >= BATCH_LIMIT) {
					applyBatchAndReset(SocialContract.CONTENT_AUTHORITY, operations);
				}
			}
		}
	}
	
	private HashMap<String, String> buildExistingSyncTypeMap(Account account,
			List<ContentValues> values) {
		HashMap<String, String> existingSyncTypeMap = new HashMap<String, String>();

		if (account == null || values == null) {
			return existingSyncTypeMap;
		}

		ArrayList<String> idList = new ArrayList<String>(values.size());
		for (ContentValues value : values) {
			if (value.containsKey(Stream.COLUMN_POST_ID_STR)) {
				idList.add(value.getAsString(Stream.COLUMN_POST_ID_STR));
			}
		}
		Cursor cursor = null;
		try {
			cursor = mResolver.query(
					Stream.buildUriWithAccounts(new Account[] { account },
							false),
					new String[] { Stream.COLUMN_POST_ID_STR,
							Stream.COLUMN_SYNC_TYPE_STR },
					generateWhereClause(Stream.COLUMN_POST_ID_STR,
							idList.toArray(new String[idList.size()])), null,
					Stream.DEFAULT_SORT);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					existingSyncTypeMap.put(cursor.getString(0),
							cursor.getString(1));
				}
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "error when query db" + e);
		} finally {
			if (cursor != null)
				try {
					cursor.close();
				} catch (Exception e) {
					Log.e(LOG_TAG, "error when close cursor" + e);
				}
		}
		return existingSyncTypeMap;
	}
	
	private void mergeAndSplitSyncTypes(HashMap<String, String> existingSyncTypeMap,
			ContentValues value) {
		if (value.containsKey(Stream.COLUMN_POST_ID_STR)
				&& value.containsKey(Stream.COLUMN_SYNC_TYPE_STR)) {
			String insertingPostId = value
					.getAsString(Stream.COLUMN_POST_ID_STR);
			String insertingSyncTypes = value
					.getAsString(Stream.COLUMN_SYNC_TYPE_STR);
			HashSet<String> finalSyncTypes = new HashSet<String>();

			//split inserting sync types
			if (!TextUtils.isEmpty(insertingSyncTypes)) {
				String[] splitedInsertingSyncTypes = insertingSyncTypes
						.split(",");
				for (String insertingSyncType : splitedInsertingSyncTypes) {
					finalSyncTypes.add(encodeSyncType(insertingSyncType));
				}
			}

			//merge existing sync types
			if (!TextUtils.isEmpty(insertingPostId)
					&& existingSyncTypeMap.containsKey(insertingPostId)) {
				String existingSyncTypes = existingSyncTypeMap
						.get(insertingPostId);
				if (!TextUtils.isEmpty(existingSyncTypes)) {
					String[] splitedSyncTypes = existingSyncTypes.split(",");
					for (String splitedSyncType : splitedSyncTypes) {
						finalSyncTypes.add(splitedSyncType);
					}
				}
			}
			
			if (!finalSyncTypes.isEmpty()) {
				value.remove(Stream.COLUMN_SYNC_TYPE_STR);
				value.put(Stream.COLUMN_SYNC_TYPE_STR,
						TextUtils.join(",", finalSyncTypes));
			}
		}
	}
	
	private String encodeSyncType(String syncType) {
		return "[" + syncType + "]";
	}
	
	private String generateWhereClause(String what, String[] ids) {
		if (ids == null || ids.length == 0) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		builder.append(what);
		builder.append(" in (");
		for (String id : ids) {
			builder.append(DatabaseUtils.sqlEscapeString(id));
			builder.append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append(")");

		return builder.toString();
	}
	
	private void addInsertStreamOperations(
			ArrayList<ContentProviderOperation> operations,
			ContentValues value){
		if(value == null){
			Log.e(LOG_TAG,"addInsertOperation, values is null");
			return;
		}
		operations.add(ContentProviderOperation.newInsert(Stream.CONTENT_URI)
				.withValues(value)
				.build());
	}
	
	private void applyBatchAndReset(
			String authorities,
			ArrayList<ContentProviderOperation> operations) {
		if ( operations == null || operations.size() == 0 )
			return;
		try {
			int count = 0;
			ContentProviderResult[] results = mResolver.applyBatch(authorities,
					operations);
			if (results != null) {
				for (ContentProviderResult result : results) {
					if ((result.count != null && result.count > 0)
							|| result.uri != null) {
						count++;
					}
				}
			}
			Log.i(LOG_TAG, "applyBatchAndReset completed " + count
					+ " ops successfully.");
		} catch ( RemoteException e ) {
			Log.e(LOG_TAG, "applyBatchAndReset failed!", e);
		} catch ( OperationApplicationException e ) {
			Log.e(LOG_TAG, "applyBatchAndReset failed!", e);
		} catch (NullPointerException e) {
			Log.e(LOG_TAG, "applyBatchAndReset failed!", e);
		} catch (Exception e) {
			Log.e(LOG_TAG, "applyBatchAndReset failed!", e);
		}
		operations.clear();
	}
}
