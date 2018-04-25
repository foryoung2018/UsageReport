package com.htc.lib2.mock.opensense.social.provider;

import com.htc.lib2.opensense.social.SocialContract.Stream;
import com.htc.lib2.opensense.social.SocialContract.StreamBundle;
import com.htc.lib2.opensense.social.SocialContract.SyncCursors;
import com.htc.lib2.opensense.social.SocialContract.SyncTypeContract;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class SocialDatabase extends SQLiteOpenHelper {

//	private static final String LOG_TAG = SocialDatabase.class.getName();

	private static final String DATABASE_NAME = "stream.db";

	private static final int DATABASE_VERSION = 64;

	public interface Tables {
		String STREAM = "stream";
		String SYNCCURSORS = "cursors";
		String TOPSTREAM = "topstream";
		String SYNCTYPE = "synctype";
		String STREAMBUNDLE = "streambundle";
		String STREAM_BUDNEL_VIEW = "stream_bundle_view";
		String ENABLED_SYNCTYPE = "enabled_synctype";
		String ENABLED_SYNCTYPE_VIEW = "sync_type_view";
	}

	interface CreateString {
		String STR_CREATE_STREAM = BaseColumns._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ Stream.COLUMN_POST_ID_STR + " TEXT,"
				+ Stream.COLUMN_ACCOUNT_NAME_STR + " TEXT,"
				+ Stream.COLUMN_ACCOUNT_TYPE_STR + " TEXT,"
				+ Stream.COLUMN_POSTER_ID_STR + " TEXT,"
				+ Stream.COLUMN_POSTER_NAME_STR + " TEXT,"
				+ Stream.COLUMN_AVATAR_URL_STR + " TEXT,"
				+ Stream.COLUMN_PROVIDER_ICON_URI_STR + " TEXT,"
				+ Stream.COLUMN_TITLE_STR + " TEXT,"
				+ Stream.COLUMN_TITLE_FORMAT_STR + " TEXT,"
				+ Stream.COLUMN_EXTRA_STR + " TEXT,"
				+ Stream.COLUMN_BODY_STR + " TEXT,"
				+ Stream.COLUMN_COVER_URI_HQ_STR + " TEXT,"
				+ Stream.COLUMN_COVER_URI_MQ_STR + " TEXT,"
				+ Stream.COLUMN_COVER_URI_LQ_STR + " TEXT,"
				+ Stream.COLUMN_CLICK_ACTION_STR + " TEXT,"
				+ Stream.COLUMN_ATTACHMENT_CLICK_ACTION_STR + " TEXT,"
				+ Stream.COLUMN_CONTEXT_ACTION_STR + " TEXT,"
				+ Stream.COLUMN_TIMESTAMP_LONG + " INTEGER,"
				+ Stream.COLUMN_STREAM_TYPE_INT + " INTEGER,"
				+ Stream.COLUMN_SYNC_TYPE_STR + " TEXT,"
				+ Stream.COLUMN_OWNER_UID_INT + " INTEGER,"
				+ Stream.COLUMN_BUNDLE_ID_STR + " TEXT,"
				+ Stream.COLUMN_BUNDLE_ORDER_INT + " INTEGER";
		

		String STR_CREATE_CURSORS = BaseColumns._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ SyncCursors.COLUMN_ACCOUNT_NAME_STR + " TEXT,"
				+ SyncCursors.COLUMN_ACCOUNT_TYPE_STR + " TEXT,"
				+ SyncCursors.COLUMN_START_TIME_LONG + " INTEGER,"
				+ SyncCursors.COLUMN_END_TIME_LONG + " INTEGER,"
				+ SyncCursors.COLUMN_SYNC_TYPE + " TEXT,"
				+ SyncCursors.COLUMN_PAGE_TOKEN + " TEXT";
		
		

		String STR_CREATE_INDEX_ON_STREAM = "CREATE UNIQUE INDEX "
				+ Stream.COLUMN_ACCOUNT_NAME_STR + "_"
				+ Stream.COLUMN_ACCOUNT_TYPE_STR + "_"
				+ Stream.COLUMN_POST_ID_STR + " ON " + Tables.STREAM + " ("
				+ Stream.COLUMN_ACCOUNT_NAME_STR + " ASC,"
				+ Stream.COLUMN_ACCOUNT_TYPE_STR + " ASC,"
				+ Stream.COLUMN_POST_ID_STR + " DESC" + ")";
		
		String STR_CREATE_TOP_STREAM = BaseColumns._ID
				+ " INTEGER,"
				+ Stream.COLUMN_POST_ID_STR + " TEXT,"
				+ Stream.COLUMN_ACCOUNT_NAME_STR + " TEXT,"
				+ Stream.COLUMN_ACCOUNT_TYPE_STR + " TEXT,"
				+ Stream.COLUMN_TIMESTAMP_LONG + " INTEGER";
		
		String STR_CREATE_TRIGGER_INSERT_TOP_STREAM = "CREATE TRIGGER insert_topstream AFTER INSERT ON "
				+ Tables.STREAM
				+ " FOR EACH ROW "
				+ " WHEN "
				+ "new."
				+ Stream.COLUMN_SYNC_TYPE_STR + " like "
				+ "'%highlights%'"
				+ " BEGIN"
				+ " DELETE FROM "
				+ Tables.TOPSTREAM
				+ " WHERE "
				+ Stream.COLUMN_POST_ID_STR + "="
				+ "new."
				+ Stream.COLUMN_POST_ID_STR
				+ " AND "
				+ Stream.COLUMN_ACCOUNT_TYPE_STR + "="
				+ "new."
				+ Stream.COLUMN_ACCOUNT_TYPE_STR
				+ " AND "
				+ Stream.COLUMN_ACCOUNT_NAME_STR + "="
				+ "new."
				+ Stream.COLUMN_ACCOUNT_NAME_STR
				+ ";"
				+ " INSERT INTO "
				+ Tables.TOPSTREAM
				+ " ("
				+ BaseColumns._ID + ","
				+ Stream.COLUMN_POST_ID_STR + ","
				+ Stream.COLUMN_ACCOUNT_TYPE_STR + ","
				+ Stream.COLUMN_ACCOUNT_NAME_STR + ","
				+ Stream.COLUMN_TIMESTAMP_LONG 
				+ ")"
				+ " VALUES "
				+ "("
				+ "new."
				+ BaseColumns._ID + ","
				+ "new."
				+ Stream.COLUMN_POST_ID_STR + ","
				+ "new."
				+ Stream.COLUMN_ACCOUNT_TYPE_STR + ","
				+ "new."
				+ Stream.COLUMN_ACCOUNT_NAME_STR + ","
				+"("
				+ "SELECT CASE WHEN "
				+ Tables.STREAM_BUDNEL_VIEW
				+ "."
				+ Stream.COLUMN_BUNDLE_ID_STR
				+ " IS NULL THEN "
				+ Tables.STREAM_BUDNEL_VIEW
				+ "."
				+ Stream.COLUMN_TIMESTAMP_LONG
				+ " ELSE "
				+ Tables.STREAM_BUDNEL_VIEW
				+ "."
				+ StreamBundle.COLUMN_TIMESTAMP_INT
				+ " END "
				+ " FROM "
				+ Tables.STREAM_BUDNEL_VIEW
				+ " WHERE "
				+ Tables.STREAM_BUDNEL_VIEW
				+ "."
				+ BaseColumns._ID
				+ "="
				+ "new."
				+ BaseColumns._ID
				+ ")"
				+ "); "
				+ "DELETE FROM "
				+ Tables.TOPSTREAM
				+ " WHERE "
				+ Stream.COLUMN_ACCOUNT_TYPE_STR + "="
				+ "new."
				+ Stream.COLUMN_ACCOUNT_TYPE_STR
				+ " AND "
				+ Stream.COLUMN_ACCOUNT_NAME_STR + "="
				+ "new."
				+ Stream.COLUMN_ACCOUNT_NAME_STR
				+ " AND "
				+ BaseColumns._ID
				+ " NOT IN "
				+ "("
				+ "SELECT "
				+ BaseColumns._ID
				+ " FROM "
				+ Tables.TOPSTREAM
				+ " WHERE "
				+ Stream.COLUMN_ACCOUNT_TYPE_STR + "="
				+ "new."
				+ Stream.COLUMN_ACCOUNT_TYPE_STR
				+ " AND "
				+ Stream.COLUMN_ACCOUNT_NAME_STR + "="
				+ "new."
				+ Stream.COLUMN_ACCOUNT_NAME_STR
				+ " ORDER BY "
				+ Stream.COLUMN_TIMESTAMP_LONG
				+ " DESC LIMIT 200"
				+ ");"
				+ " END;";
		
		String STR_CREATE_TRIGGER_DELETE_TOP_STREAM = "CREATE TRIGGER delete_topstream AFTER DELETE ON "
				+ Tables.STREAM
				+ " FOR EACH ROW "
				+ " BEGIN DELETE FROM "
				+ Tables.TOPSTREAM
				+ " WHERE "
				+ BaseColumns._ID
				+ "="
				+ "old."
				+ BaseColumns._ID
				+ "; END;";
		
		String STR_CREATE_SYNC_TYPE = SyncTypeContract.COLUMN_TITLE_STR
				+ " TEXT," + SyncTypeContract.COLUMN_IDENTITY_STR + " TEXT,"
				+ SyncTypeContract.COLUMN_TITLE_RES_NAME_STR + " TEXT,"
				+ SyncTypeContract.COLUMN_SUB_TITLE_STR + " TEXT,"
				+ SyncTypeContract.COLUMN_SUB_TITLE_RES_NAME_STR + " TEXT,"
				+ SyncTypeContract.COLUMN_PACKAGE_NAME_STR + " TEXT,"
				+ SyncTypeContract.COLUMN_ACCOUNT_NAME_STR + " TEXT,"
				+ SyncTypeContract.COLUMN_ACCOUNT_TYPE_STR + " TEXT,"
				+ SyncTypeContract.COLUMN_CATEGORY_STR + " TEXT,"
				+ SyncTypeContract.COLUMN_CATEGORY_RES_NAME_STR + " TEXT,"
				+ SyncTypeContract.COLUMN_EDITION_STR + " TEXT,"
				+ SyncTypeContract.COLUMN_EDITION_RES_NAME_STR + " TEXT,"
				+ SyncTypeContract.COLUMN_COLOR_INT + " INTEGER,"
				+ SyncTypeContract.COLUMN_ICON_RES_NAME_STR + " TEXT,"
				+ SyncTypeContract.COLUMN_ICON_URL_STR + " TEXT,"
				+ "owner_uid" + " INTEGER";
		
		String STR_CREATE_INDEX_ON_SYNC_TYPE = "CREATE UNIQUE INDEX "
				+ SyncTypeContract.COLUMN_IDENTITY_STR + "_"
				+ SyncTypeContract.COLUMN_ACCOUNT_TYPE_STR + "_"
				+ SyncTypeContract.COLUMN_ACCOUNT_NAME_STR + " ON "
				+ Tables.SYNCTYPE + " ("
				+ SyncTypeContract.COLUMN_ACCOUNT_NAME_STR + " ASC,"
				+ SyncTypeContract.COLUMN_ACCOUNT_TYPE_STR + " ASC,"
				+ SyncTypeContract.COLUMN_IDENTITY_STR + " DESC" + ")";
		
		String STR_CREATE_STREAM_BUNDLE = 
				StreamBundle.COLUMN_BID_STR + " TEXT PRIMARY KEY," +
				StreamBundle.COLUMN_TITLE_STR + " TEXT," +
				StreamBundle.COLUMN_CLICK_ACTION_STR + " TEXT," +
				StreamBundle.COLUMN_TIMESTAMP_INT + " INTEGER";
		
		String STR_CREATE_STREAM_BUNDLE_VIEW = "CREATE VIEW "
				+ Tables.STREAM_BUDNEL_VIEW + " AS SELECT " + Tables.STREAM
				+ ".* " + "," + Tables.STREAMBUNDLE + ".*," + " CASE WHEN "
				+ Tables.STREAM + "." + Stream.COLUMN_BUNDLE_ID_STR
				+ " IS NOT NULL " + " THEN " + Tables.STREAMBUNDLE + "."
				+ StreamBundle.COLUMN_TIMESTAMP_INT + " ELSE " + Tables.STREAM
				+ "." + Stream.COLUMN_TIMESTAMP_LONG + " END AS "
				+ "common_timestamp" + " FROM " + Tables.STREAM + " LEFT JOIN "
				+ Tables.STREAMBUNDLE + " ON " + Tables.STREAM + "."
				+ Stream.COLUMN_BUNDLE_ID_STR + "=" + Tables.STREAMBUNDLE + "."
				+ StreamBundle.COLUMN_BID_STR;
		
		String STR_CREATE_ENABLED_SYNC_TYPE =
				SyncTypeContract.COLUMN_IDENTITY_STR + " TEXT," + 
				SyncTypeContract.COLUMN_ACCOUNT_NAME_STR + " TEXT," + 
				SyncTypeContract.COLUMN_ACCOUNT_TYPE_STR + " TEXT";
		
		String STR_CREATE_INDEX_ON_ENABLED_SYNC_TYPE = "CREATE UNIQUE INDEX "
				+ Tables.ENABLED_SYNCTYPE + "_"
				+ SyncTypeContract.COLUMN_IDENTITY_STR + "_"
				+ SyncTypeContract.COLUMN_ACCOUNT_TYPE_STR + "_"
				+ SyncTypeContract.COLUMN_ACCOUNT_NAME_STR + " ON "
				+ Tables.ENABLED_SYNCTYPE + " ("
				+ SyncTypeContract.COLUMN_ACCOUNT_NAME_STR + " ASC,"
				+ SyncTypeContract.COLUMN_ACCOUNT_TYPE_STR + " ASC,"
				+ SyncTypeContract.COLUMN_IDENTITY_STR + " DESC" + ")";
		
		String STR_CREATE_ENABLED_SYNCTYPE_VIEW = "CREATE VIEW "
				+ Tables.ENABLED_SYNCTYPE_VIEW + " AS SELECT "
				+ Tables.SYNCTYPE + ".*" + "," + " CASE WHEN "
				+ Tables.ENABLED_SYNCTYPE + "."
				+ SyncTypeContract.COLUMN_IDENTITY_STR + " IS NULL "
				+ "THEN 0 " + "ELSE 1 END " + "AS "
				+ SyncTypeContract.COLUMN_ENABLED_INT + " FROM "
				+ Tables.SYNCTYPE + " LEFT OUTER JOIN "
				+ Tables.ENABLED_SYNCTYPE + " ON " + "(" + Tables.SYNCTYPE
				+ "." + SyncTypeContract.COLUMN_IDENTITY_STR + "="
				+ Tables.ENABLED_SYNCTYPE + "."
				+ SyncTypeContract.COLUMN_IDENTITY_STR + " AND "
				+ Tables.SYNCTYPE + "."
				+ SyncTypeContract.COLUMN_ACCOUNT_NAME_STR + "="
				+ Tables.ENABLED_SYNCTYPE + "."
				+ SyncTypeContract.COLUMN_ACCOUNT_NAME_STR + " AND "
				+ Tables.SYNCTYPE + "."
				+ SyncTypeContract.COLUMN_ACCOUNT_TYPE_STR + "="
				+ Tables.ENABLED_SYNCTYPE + "."
				+ SyncTypeContract.COLUMN_ACCOUNT_TYPE_STR + ")";
	}

	private Context mContext = null;
	public SocialDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO backup stream data if needed
		dropTable(db);
		onCreate(db);
		if (mContext != null) {
			mContext.getContentResolver()
					.notifyChange(Stream.CONTENT_URI, null);
			mContext.sendBroadcast(new Intent(
					"com.htc.feed.action.FORCE_REFRESH"));
		}
	}

		@Override
		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(LOG_TAG, "[onDowngrade]");
		}

	private void createTable(SQLiteDatabase db) {
		createTable(db, Tables.STREAM, CreateString.STR_CREATE_STREAM);
		createTable(db, Tables.SYNCCURSORS, CreateString.STR_CREATE_CURSORS);
		createTable(db, Tables.TOPSTREAM, CreateString.STR_CREATE_TOP_STREAM);
		createTable(db, Tables.SYNCTYPE, CreateString.STR_CREATE_SYNC_TYPE);
		createTable(db, Tables.STREAMBUNDLE, CreateString.STR_CREATE_STREAM_BUNDLE);
		createTable(db, Tables.ENABLED_SYNCTYPE, CreateString.STR_CREATE_ENABLED_SYNC_TYPE);
		db.execSQL(CreateString.STR_CREATE_INDEX_ON_STREAM);
		db.execSQL(CreateString.STR_CREATE_TRIGGER_INSERT_TOP_STREAM);
		db.execSQL(CreateString.STR_CREATE_TRIGGER_DELETE_TOP_STREAM);
		db.execSQL(CreateString.STR_CREATE_INDEX_ON_SYNC_TYPE);
		db.execSQL(CreateString.STR_CREATE_STREAM_BUNDLE_VIEW);
		db.execSQL(CreateString.STR_CREATE_ENABLED_SYNCTYPE_VIEW);
		db.execSQL(CreateString.STR_CREATE_INDEX_ON_ENABLED_SYNC_TYPE);
	}

	public void createTable(SQLiteDatabase db, String table,
			String createCommand) {
		StringBuilder create = new StringBuilder("CREATE TABLE ");
		create.append(table);
		create.append("(");
		create.append(createCommand);
		create.append(");");
		db.execSQL(create.toString());
	}

	private void dropTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + Tables.STREAM);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.SYNCCURSORS);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.TOPSTREAM);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.SYNCTYPE);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.STREAMBUNDLE);
		db.execSQL("DROP TABLE IF EXISTS " + Tables.ENABLED_SYNCTYPE);
		db.execSQL("DROP TABLE IF EXISTS " + "usergroup");
		db.execSQL("DROP TABLE IF EXISTS " + "group0");
		db.execSQL("DROP VIEW IF EXISTS " + Tables.STREAM_BUDNEL_VIEW);
		db.execSQL("DROP VIEW IF EXISTS " + Tables.ENABLED_SYNCTYPE_VIEW);
	}

}
