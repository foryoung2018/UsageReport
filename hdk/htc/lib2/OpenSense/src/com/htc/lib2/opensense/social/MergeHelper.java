package com.htc.lib2.opensense.social;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import com.htc.lib2.opensense.social.SocialContract;
import com.htc.lib2.opensense.social.SocialContract.Stream;
import com.htc.lib2.opensense.social.SocialContract.SyncCursors;
import com.htc.lib2.opensense.social.SocialContract.SyncTypeContract;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;


/**
 * This is a helper class that help you to do stream database operation.
 * 
 * @hide
 */
public class MergeHelper {

	private static final String LOG_TAG = MergeHelper.class.getSimpleName();
	private static final int BATCH_LIMIT = 30;
	
	private ContentResolver mResolver;
	private static MergeHelper sInstance = null;

	private MergeHelper(Context context){
		mResolver = context.getContentResolver();
	}
	
	/**
	 * use this function to get a MergeHelper instance
	 * @param context 
	 * @return MergeHelper instance
	 * 
	 * @hide
	 */
	public static MergeHelper getInstance(Context context){
		if(sInstance == null){
			sInstance = new MergeHelper(context);
		}
		return sInstance;
	}
	
	/**
	 * insert stream into database directly, do not record any time line information
	 * @param values
	 * 
	 * @hide
	 */
	public void insertStreamToDb(List<ContentValues> values) {
		if (values == null || values.isEmpty()) {
			Log.e(LOG_TAG, "insertToDB , values is null or empty");
			return;
		}
		
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
		
		//assume that all account type and name are the same of each values. 
		final String accountType = values.get(0).getAsString(Stream.COLUMN_ACCOUNT_TYPE_STR);
		final String accountName = values.get(0).getAsString(Stream.COLUMN_ACCOUNT_NAME_STR);
		if(!TextUtils.isEmpty(accountName) && !TextUtils.isEmpty(accountType)){
			handleInsertStream(new Account(accountName, accountType),
					operations, values);
		}
		
		if (operations.size() > 0) {
			applyBatchAndReset(SocialContract.CONTENT_AUTHORITY, operations);
		}
		
		mResolver.notifyChange(Stream.CONTENT_URI, null);
	}

	/**
	 * Insert streams into database and record start and end time. if you don't
	 * get any streams, you still have to call this function in order to update
	 * time line.
	 * 
	 * @param endTime bigger time stamp of your stream inserted to db.
	 * @param startTime startTime smaller time stamp of your stream inserted to db
	 * @param account the account that streams belong to.
	 * @param values stream values.
	 * @param syncTypes the sync type array of the stream inserted.
	 * 
	 * @hide
	 */
	public void mergeStreamToDb(long endTime, long startTime, Account account,
			List<ContentValues> values, String[] syncTypes) {
		if (syncTypes == null || syncTypes.length < 1) {
			Log.e(LOG_TAG, "mergeToDB , syncTypes is null or empty, do nothing");
			return;
		}

		if (account == null) {
			Log.e(LOG_TAG, "mergeToDB , account is null, do nothing");
			return;
		}

		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		// insert or update the streams of the interval
		if (values != null) {
			handleInsertStream(account, operations, values);
		}

		// insert or merge the sync time line in SyncCursors table by syncType
		for (String syncType : syncTypes) {
			addInsertOrMergeSyncCursorsOperation(operations, account.name,
					account.type, syncType, startTime, endTime);
		}

		if (operations.size() > 0) {
			applyBatchAndReset(SocialContract.CONTENT_AUTHORITY, operations);
		}

		mResolver.notifyChange(Stream.CONTENT_URI, null);
	}
	
	private void handleInsertStream(Account account,
			ArrayList<ContentProviderOperation> operations,
			List<ContentValues> values) {
		if (values != null) {
			//generate existing sync types and post id mapping in db
			HashMap<String, String> existingSyncTypeMap = buildExistingSyncTypeMap(
					account, values);

			HashMap<String, String> existingBundleIdMap = buildExistingBundleIdMap(
					account, values);
			
			for (ContentValues value : values) {
				//merge inserting sync types with existing ones
				mergeAndSplitSyncTypes(existingSyncTypeMap, value);
				handleBundleId(existingBundleIdMap, value);
				
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
	
	private HashMap<String, String> buildExistingBundleIdMap(Account account,
			List<ContentValues> values) {
		HashMap<String, String> existingBundleIdMap = new HashMap<String, String>();

		if (account == null || values == null) {
			return existingBundleIdMap;
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
					Stream.buildUriWithAccounts(new Account[]{account}, false),
					new String[]{Stream.COLUMN_POST_ID_STR,
							Stream.COLUMN_BUNDLE_ID_STR},
					generateWhereClause(Stream.COLUMN_POST_ID_STR,
							idList.toArray(new String[idList.size()])), null,
					Stream.DEFAULT_SORT);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					existingBundleIdMap.put(cursor.getString(0),
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
		return existingBundleIdMap;
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
	
	private void handleBundleId(HashMap<String, String> existingBundleIdMap,
			ContentValues value) {
		if (value != null
				&& !value.containsKey(Stream.COLUMN_BUNDLE_ID_STR)
				|| TextUtils.isEmpty(value
						.getAsString(Stream.COLUMN_BUNDLE_ID_STR))) {
			String postId = value.getAsString(Stream.COLUMN_POST_ID_STR);
			if (!TextUtils.isEmpty(postId)
					&& existingBundleIdMap.containsKey(postId)) {
				value.put(Stream.COLUMN_BUNDLE_ID_STR,
						existingBundleIdMap.get(postId));
			}
		}
	}

	/**
	 * helper function to update all the stream type to another
	 * @param accountType
	 * @param accountName
	 * @param streamType
	 * @param TypeToUpdate
	 * @param set set to true or false
	 * @return the number of rows updated
	 * 
	 * @hide
	 */
	public int updateStreamType(String accountType, String accountName,
			int streamType, int TypeToUpdate, boolean set) {
		Uri base_content_uri = Uri.parse("content://"
				+ SocialContract.CONTENT_AUTHORITY);
		Uri rawUri = base_content_uri.buildUpon()
				.appendPath("raw").build();
		String setString;
		if(set){
			setString = "%s=(%s | %d)";
		}else{
			setString = "%s=(%s & (~%d))";
		}
		
		String sql = String.format(Locale.US, "UPDATE %s SET " + setString + " WHERE %s=%s AND %s=%s AND (%s & %d)=%d",
				"stream",
				Stream.COLUMN_STREAM_TYPE_INT, Stream.COLUMN_STREAM_TYPE_INT, TypeToUpdate,
				Stream.COLUMN_ACCOUNT_TYPE_STR, DatabaseUtils.sqlEscapeString(accountType),
				Stream.COLUMN_ACCOUNT_NAME_STR, DatabaseUtils.sqlEscapeString(accountName),
				Stream.COLUMN_STREAM_TYPE_INT, streamType, streamType);
		int ret = mResolver.update(rawUri, new ContentValues(), sql, null);
		mResolver.notifyChange(Stream.CONTENT_URI, null);
		return ret;
	}
	
	/**
	 * helper function to update the stream type to another by the input type and id
	 * @param accountType
	 * @param accountName
	 * @param filter, eg. Stream.STREAM_POST_ID_STR
	 * @param ids filter value 
	 * @param TypeToUpdate what Stream type to update
	 * @param set true or false
	 * @return the number of rows updated
	 * 
	 * @hide
	 */
	public int updateStreamType(String accountType, String accountName,
			String what, String[] ids, int TypeToUpdate, boolean set) {
		Uri base_content_uri = Uri.parse("content://"
				+ SocialContract.CONTENT_AUTHORITY);
		Uri rawUri = base_content_uri.buildUpon().appendPath("raw").build();
		String setString;
		if (set) {
			setString = "%s=(%s | %d)";
		} else {
			setString = "%s=(%s & (~%d))";
		}

		String sql = String.format(Locale.US, "UPDATE %s SET " + setString
				+ " WHERE %s=%s AND %s=%s AND (" + generateWhereClause(what, ids) + ")",
				"stream", Stream.COLUMN_STREAM_TYPE_INT, Stream.COLUMN_STREAM_TYPE_INT,
				TypeToUpdate, Stream.COLUMN_ACCOUNT_TYPE_STR,
				DatabaseUtils.sqlEscapeString(accountType),
				Stream.COLUMN_ACCOUNT_NAME_STR,
				DatabaseUtils.sqlEscapeString(accountName));
		int ret = mResolver.update(rawUri, new ContentValues(), sql, null);
		mResolver.notifyChange(Stream.CONTENT_URI, null);
		return ret;
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
	
	/**
	 * delete stream from db by filter
	 * @param accountType
	 * @param accountName
	 * @param what
	 * @param ids
	 * 
	 * @hide
	 */
	public int deleteStreamFromDb(String accountType, String accountName,
			String what, String[] ids) {
		int affectedRowCount = 0;
		if (what == null || ids == null || ids.length == 0) {
			throw new RuntimeException("selection or selectionArg is null!");
		}
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		Uri uri = Stream.buildUriWithAccounts(new Account[] { new Account(
				accountName, accountType) }, false);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < ids.length; i++) {
			if (builder.length() > 0) {
				builder.append(",");
			}
			builder.append(DatabaseUtils.sqlEscapeString(ids[i]));
		}
		
		if (Stream.COLUMN_SYNC_TYPE_STR.equals(what)) {
			for (String id : ids) {
				operations.add(ContentProviderOperation
						.newDelete(uri)
						.withSelection(
								Stream.COLUMN_SYNC_TYPE_STR + " like '%"
										+ encodeSyncType(id) + "%'", null)
						.build());
			}
		} else {
			operations.add(ContentProviderOperation
					.newDelete(uri)
					.withSelection(what + " in (" + builder.toString() + ")",
							null).build());
		}
		
		if (Stream.COLUMN_SYNC_TYPE_STR.equals(what)) {
			// delete sync cursors if we want to delete stream by sync type
			operations.add(ContentProviderOperation
					.newDelete(SyncCursors.CONTENT_URI)
					.withSelection(
							SyncCursors.COLUMN_SYNC_TYPE + " in ("
									+ builder.toString() + ")", null).build());
		}

		if (operations.size() > 0) {
			affectedRowCount = applyBatchAndReset(SocialContract.CONTENT_AUTHORITY, operations);
		}
		mResolver.notifyChange(Stream.CONTENT_URI, null);
		return affectedRowCount;
	}
	
	private String encodeSyncType(String syncType) {
		return "[" + syncType + "]";
	}
	
	/**
	 * delete all from database by account type
	 * @param accountType
	 * 
	 * @hide
	 */
	public void deleteAllFromDb(String accountType) {
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		// delete stream
		operations.add(ContentProviderOperation
				.newDelete(Stream.CONTENT_URI)
				.withSelection(Stream.COLUMN_ACCOUNT_TYPE_STR + "=?",
						new String[] { accountType }).build());

		// delete cursor
		operations.add(ContentProviderOperation
				.newDelete(SyncCursors.CONTENT_URI)
				.withSelection(SyncCursors.COLUMN_ACCOUNT_TYPE_STR + "=?",
						new String[] { accountType }).build());
		
		// delete sync types
		operations.add(ContentProviderOperation
				.newDelete(SyncTypeContract.CONTENT_URI)
				.withSelection(SyncTypeContract.COLUMN_ACCOUNT_TYPE_STR + "=?",
						new String[]{accountType}).build());
		
		if(operations.size() > 0 ){
			applyBatchAndReset(SocialContract.CONTENT_AUTHORITY, operations);
		}
		mResolver.notifyChange(Stream.CONTENT_URI, null);
	}
	
	/**
	 * delete all from database by account name and type
	 * @param accountType
	 * @param accountName
	 * 
	 * @hide
	 */
	public void deleteAllFromDb(String accountType, String accountName){
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		// delete stream
		Account account = new Account(accountName, accountType);
		Uri uri = Stream.buildUriWithAccounts(new Account[] { account }, false);
		operations.add(ContentProviderOperation.newDelete(uri).build());

		// delete cursor
		uri = SyncCursors.CONTENT_URI;
		operations.add(ContentProviderOperation
				.newDelete(uri)
				.withSelection(
						SyncCursors.COLUMN_ACCOUNT_TYPE_STR + "=?" + " AND "
								+ SyncCursors.COLUMN_ACCOUNT_NAME_STR + "=?",
						new String[] { accountType, accountName }).build());
		
		// delete sync types
		operations.add(ContentProviderOperation
				.newDelete(SyncTypeContract.CONTENT_URI)
				.withSelection(
						SyncTypeContract.COLUMN_ACCOUNT_TYPE_STR + "=?"
								+ " AND "
								+ SyncTypeContract.COLUMN_ACCOUNT_NAME_STR
								+ "=?", new String[]{accountType, accountName})
				.build());
		
		if(operations.size() > 0 ){
			applyBatchAndReset(SocialContract.CONTENT_AUTHORITY, operations);
		}
		mResolver.notifyChange(Stream.CONTENT_URI, null);
	}

    private ArrayList<String> queryCurrentSyncType(String accountName, String accountType) {
        ArrayList<String> existingSyncTypeId = new ArrayList<String>();

        Cursor cursor = null;
        try {
            cursor = mResolver.query(
                    SyncTypeContract.CONTENT_URI,
                    new String[] {
                            SyncTypeContract.COLUMN_IDENTITY_STR,
                            SyncTypeContract.COLUMN_ACCOUNT_NAME_STR,
                            SyncTypeContract.COLUMN_ACCOUNT_TYPE_STR
                    }, SyncTypeContract.COLUMN_ACCOUNT_TYPE_STR + "=?"
                            + " AND "
                            + SyncTypeContract.COLUMN_ACCOUNT_NAME_STR
                            + "=?",
                    new String[] {
                            accountType, accountName
                    },
                    SyncTypeContract.DEFAULT_SORT);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    existingSyncTypeId.add(cursor.getString(0));
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
        return existingSyncTypeId;
    }

	/**
	 * @hide
	 */
	public void insertSyncTypeToDb(List<SyncType> syncTypes,
			String accountName, String accountType, boolean wipeOldData) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

        if (syncTypes != null && !syncTypes.isEmpty()) {
            ArrayList<String> currentSyncTypeId = queryCurrentSyncType(accountName, accountType);
            if (currentSyncTypeId.isEmpty()) {
                Log.i(LOG_TAG, "account:" + accountType + ", no existing syncType");
            } else {
                Log.i(LOG_TAG, "account:" + accountType + ", number of syncType=" + currentSyncTypeId.size());
            }

            Uri uri = Uri.parse("content://" + SocialContract.CONTENT_AUTHORITY
                    + "/enabled_synctype");

            for (SyncType syncType : syncTypes) {
                if (!currentSyncTypeId.contains(syncType.getId()) &&
                        syncType.getDefaultEnabled()) {
                    Log.i(LOG_TAG, "set syncType as default enabled, id:" + syncType.getId());
                    ContentValues values = new ContentValues();
                    values.put(SyncTypeContract.COLUMN_IDENTITY_STR, syncType.getId());
                    values.put(SyncTypeContract.COLUMN_ACCOUNT_NAME_STR, accountName);
                    values.put(SyncTypeContract.COLUMN_ACCOUNT_TYPE_STR, accountType);
                    operations.add(ContentProviderOperation
                            .newInsert(uri).withValues(values)
                            .build());
                }
            }
        }

		if (wipeOldData) {
			// delete old data first
			operations.add(ContentProviderOperation.newDelete(
					SyncTypeContract.buildUriWithAccount(accountName,
							accountType)).build());
		}
		if (syncTypes != null && !syncTypes.isEmpty()) {
			for (SyncType syncType : syncTypes) {
				ContentValues value = new ContentValues();
				value.put(SyncTypeContract.COLUMN_IDENTITY_STR, syncType.getId());
				value.put(SyncTypeContract.COLUMN_ACCOUNT_NAME_STR, accountName);
				value.put(SyncTypeContract.COLUMN_ACCOUNT_TYPE_STR, accountType);
				value.put(SyncTypeContract.COLUMN_PACKAGE_NAME_STR,
						syncType.getPackageName());
				value.put(SyncTypeContract.COLUMN_TITLE_STR,
						syncType.getTitle());
				value.put(SyncTypeContract.COLUMN_TITLE_RES_NAME_STR,
						syncType.getTitleResName());
				value.put(SyncTypeContract.COLUMN_SUB_TITLE_STR,
						syncType.getSubTitle());
				value.put(SyncTypeContract.COLUMN_SUB_TITLE_RES_NAME_STR,
						syncType.getSubTitleResName());
				value.put(SyncTypeContract.COLUMN_EDITION_STR,
						syncType.getEdition());
				value.put(SyncTypeContract.COLUMN_EDITION_RES_NAME_STR,
						syncType.getEditionResName());
				value.put(SyncTypeContract.COLUMN_CATEGORY_STR,
						syncType.getCategory());
				value.put(SyncTypeContract.COLUMN_CATEGORY_RES_NAME_STR,
						syncType.getCategoryResName());
				value.put(SyncTypeContract.COLUMN_ICON_RES_NAME_STR,
						syncType.getIconResName());
				value.put(SyncTypeContract.COLUMN_ICON_URL_STR,
						syncType.getIconUrl());
				operations.add(ContentProviderOperation
						.newInsert(SyncTypeContract.CONTENT_URI)
						.withValues(value).build());

				if (operations.size() >= BATCH_LIMIT) {
					applyBatchAndReset(SocialContract.CONTENT_AUTHORITY,
							operations);
				}
			}
		}
		if (operations.size() > 0) {
			applyBatchAndReset(SocialContract.CONTENT_AUTHORITY, operations);
		}
		mResolver.notifyChange(SyncTypeContract.CONTENT_URI, null);
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

	private void addInsertOrMergeSyncCursorsOperation(
			ArrayList<ContentProviderOperation> operations,
			String accountName,
			String accountType,
			String syncType,
			long startTime,
			long endTime){
		
		ContentValues cursorsValue = new ContentValues();
		cursorsValue.put(SyncCursors.COLUMN_ACCOUNT_NAME_STR, accountName);
		cursorsValue.put(SyncCursors.COLUMN_ACCOUNT_TYPE_STR, accountType);
		cursorsValue.put(SyncCursors.COLUMN_SYNC_TYPE, syncType);
		cursorsValue.put(SyncCursors.COLUMN_START_TIME_LONG, startTime);
		cursorsValue.put(SyncCursors.COLUMN_END_TIME_LONG, endTime);

		operations.add(ContentProviderOperation.newInsert(SyncCursors.CONTENT_URI)
				.withValues(cursorsValue)
				.build());
	}

	private int applyBatchAndReset(
			String authorities,
			ArrayList<ContentProviderOperation> operations) {
		int affectedRowCount = 0;
		if ( operations == null || operations.size() == 0 )
			return affectedRowCount;
		try {
			ContentProviderResult[] results = mResolver.applyBatch(authorities, operations);
			if(results != null && results.length > 0) {
				for(int i = 0; i < results.length; i++) {
					ContentProviderResult cpr = results[i];
					if(cpr != null && cpr.count != null) {
						affectedRowCount += cpr.count.intValue();
					}
				}
			}
			Log.i(LOG_TAG, "applyBatchAndReset completed " + operations.size() + " ops successfully.");
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
		return affectedRowCount;
	}
}
