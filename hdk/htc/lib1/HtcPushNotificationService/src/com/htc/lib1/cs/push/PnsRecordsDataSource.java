
package com.htc.lib1.cs.push;

import com.htc.lib1.cs.logging.HtcLogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*package*/class PnsRecordsDataSource {
    /**
     * Event type for registrations.
     */
    public static final String EVENT_TYPE_REGISTRATION = "registration";

    /**
     * Event type for updates.
     */
    public static final String EVENT_TYPE_UPDATE = "update";

    /**
     * Event type for unregistrations.
     */
    public static final String EVENT_TYPE_UNREGISTRATION = "unregistration";

    /****************************************************************************************
     * Tables and columns.
     ****************************************************************************************/
    private static final String TABLE_EVENTS = "events";
    private static final String TABLE_MESSAGES = "messages";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_ACTION_CAUSE = "actionCause";
    private static final String COLUMN_RESULT_CAUSE = "resultCause";
    private static final String COLUMN_SUCCESS = "success";
    private static final String COLUMN_MSG_ID = "msgId";
    private static final String COLUMN_APPLIST = "applist";

    private static PnsRecordsDataSource sInstance;
    private final PnsRecordsDatabaseHelper mDbHelper;
    private HtcLogger mLogger = new PushLoggerFactory(this).create();

    private PnsRecordsDataSource(Context context) {
        mDbHelper = new PnsRecordsDatabaseHelper(context);
    }

    /**
     * Get the instance of {@link PnsRecordsDataSource}.
     *
     * @param context Context used to retrieve application context.
     * @return {@link PnsRecordsDataSource}
     */
    public static synchronized PnsRecordsDataSource get(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        if (sInstance == null) {
            sInstance = new PnsRecordsDataSource(context);
        }
        return sInstance;
    }

    /**
     * Clear all content in the database.
     */
    public void clear() {
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.delete(TABLE_EVENTS, null, null);
            db.delete(TABLE_MESSAGES, null, null);
            db.close();
        }
    }

    /**
     * Add a registration / update or unregistration event record.
     *
     * @param type        One of {@link #EVENT_TYPE_REGISTRATION},
     *                    {@link #EVENT_TYPE_UPDATE} or
     *                    {@link #EVENT_TYPE_UNREGISTRATION}.
     * @param actionCause The reason this event occurs.
     * @param resultCause Optional field to describe the reason of the failure /
     *                    success result.
     * @param success     {@code true} if the registration / update / unregistration
     *                    was successful; {@code false} otherwise.
     */
    public void addEventRecord(String type, String actionCause, String resultCause,
                               boolean success) {
        // Prepare the record.
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_ACTION_CAUSE, actionCause);
        values.put(COLUMN_RESULT_CAUSE, resultCause);
        values.put(COLUMN_SUCCESS, success ? 1 : 0);

        // Write record.
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.insertOrThrow(TABLE_EVENTS, null, values);
            db.close();
        }
    }

    /**
     * Get the most recent {@code limit} records of events.
     *
     * @param limit Number of records to get.
     * @return {@link PnsRecords.EventRecord}
     */
    public PnsRecords.EventRecord[] getRecentEventRecords(int limit) {
        PnsRecords.EventRecord records[];

        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            Cursor c = db.query(TABLE_EVENTS, new String[]{
                    COLUMN_TIMESTAMP, COLUMN_TYPE, COLUMN_ACTION_CAUSE, COLUMN_RESULT_CAUSE,
                    COLUMN_SUCCESS
            }, null, null, null, null, COLUMN_TIMESTAMP + " DESC", String.valueOf(limit));

            int count = c.getCount();
            records = new PnsRecords.EventRecord[count];
            if (count > 0) {
                // Get column indices.
                int columnTimestamp = c.getColumnIndexOrThrow(COLUMN_TIMESTAMP);
                int columnType = c.getColumnIndexOrThrow(COLUMN_TYPE);
                int columnActionCause = c.getColumnIndexOrThrow(COLUMN_ACTION_CAUSE);
                int columnResultCause = c.getColumnIndexOrThrow(COLUMN_RESULT_CAUSE);
                int columnSuccess = c.getColumnIndexOrThrow(COLUMN_SUCCESS);

                // Fill records.
                c.moveToFirst();
                for (int i = 0; i < count; i++) {
                    PnsRecords.EventRecord r = new PnsRecords.EventRecord();
                    r.timestamp = c.getLong(columnTimestamp);
                    r.type = c.getString(columnType);
                    r.actionCause = c.getString(columnActionCause);
                    r.resultCause = c.getString(columnResultCause);
                    r.success = (c.getInt(columnSuccess) == 1);
                    records[i] = r;
                    c.moveToNext();
                }
            }
            c.close();
            db.close();
        }

        return records;
    }

    /**
     * Get the most recent {@code limit} records of messages.
     *
     * @param limit Number of records to get.
     * @return {@link PnsRecords.MessageRecord}
     */
    public PnsRecords.MessageRecord[] getRecentMessageRecords(int limit) {
        PnsRecords.MessageRecord records[];

        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            Cursor c = db.query(TABLE_MESSAGES, new String[]{
                    COLUMN_TIMESTAMP, COLUMN_MSG_ID, COLUMN_APPLIST
            }, null, null, null, null, COLUMN_TIMESTAMP + " DESC", String.valueOf(limit));

            int count = c.getCount();
            records = new PnsRecords.MessageRecord[count];
            if (count > 0) {
                // Get column indices.
                int columnTimestamp = c.getColumnIndexOrThrow(COLUMN_TIMESTAMP);
                int columnMsgId = c.getColumnIndexOrThrow(COLUMN_MSG_ID);
                int columnAppList = c.getColumnIndexOrThrow(COLUMN_APPLIST);

                // Fill records.
                c.moveToFirst();
                for (int i = 0; i < count; i++) {
                    PnsRecords.MessageRecord r = new PnsRecords.MessageRecord();
                    r.timestamp = c.getLong(columnTimestamp);
                    r.msgId = c.getString(columnMsgId);
                    r.appList = c.getString(columnAppList);
                    records[i] = r;
                    c.moveToNext();
                }
            }
            c.close();
            db.close();
        }

        return records;
    }

    /**
     * Clean expired event records.
     */
    public void cleanExpiredEventRecords() {
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            cleanUpOldRecordsLocked(db, TABLE_EVENTS).close();
        }
    }

    /**
     * Add a message record.
     *
     * @param msgId   ID of the message.
     * @param applist Receiver apps of the message.
     */
    public void addMessageRecord(String msgId, String applist) {
        // Prepare the record.
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        values.put(COLUMN_MSG_ID, msgId);
        values.put(COLUMN_APPLIST, applist);

        // Write record.
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            try {
                db.insertOrThrow(TABLE_MESSAGES, "NULL", values);
            } catch (SQLException e) {
                /*
                 * The message records are for debugging purpose only. Failures
                 * of database operations here don't impact the app.
                 */
                mLogger.warning(e);
            } finally {
                db.close();
            }
        }
    }

    /**
     * Clean expired message records.
     */
    public void cleanExpiredMessageRecords() {
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            try {
                cleanUpOldRecordsLocked(db, TABLE_MESSAGES);
            } catch (SQLException e) {
                /*
                 * The message records are for debugging purpose only. Failures
                 * of database operations here don't impact the app.
                 */
                mLogger.warning(e);
            } finally {
                db.close();
            }
        }
    }

    /**
     * Get the number of registration and update events in last {@code period}
     * milliseconds.
     *
     * @param period Period in milliseconds.
     * @return Number of events satisfy given conditions.
     */
    public long getNumUpdatesInPeriod(long period) {
        mLogger.verbose("period=", period);

        long num;
        long timeThreshold = System.currentTimeMillis() - period;
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            num = DatabaseUtils.queryNumEntries(db, TABLE_EVENTS,
                    /*
                     * "type IN ('registration', 'update') AND success = 1 AND timestamp > timeThreshold)"
                     */
                    COLUMN_TYPE + " IN ('" + EVENT_TYPE_REGISTRATION + "', '" + EVENT_TYPE_UPDATE
                            + "') AND " + COLUMN_SUCCESS + "= 1 AND " + COLUMN_TIMESTAMP + " > ?",
                    new String[]{
                            String.valueOf(timeThreshold)
                    });
            db.close();
        }

        mLogger.verbose("num=", num);
        return num;
    }

    /**
     * Clean up the table to keep only the last
     * {@value PnsInternalDefs#MAX_PNS_RECORDS} records. This method must only
     * be invoked inside a critical section.
     *
     * @param writableDatabase Writable database to operate on.
     * @param table            Table to clean up. It must has the column
     *                         {@link #COLUMN_TIMESTAMP}.
     * @return The {@code writableDatabase} passed in.
     */
    private SQLiteDatabase cleanUpOldRecordsLocked(SQLiteDatabase writableDatabase, String table) {
        writableDatabase.delete(table,
                /*
                 * WHERE _id NOT IN ( SELECT _id FROM table ORDER BY timestamp
                 * DESC LIMIT 100 )
                 */
                COLUMN_ID + " NOT IN ( SELECT " + COLUMN_ID + " FROM " + table
                        + " ORDER BY " + COLUMN_TIMESTAMP + " DESC LIMIT "
                        + PnsInternalDefs.MAX_PNS_RECORDS + ")",
                null);

        return writableDatabase;
    }

    /**
     * Database helper for PNS registration / message records.
     *
     * @author samael_wang@htc.com
     */
    private class PnsRecordsDatabaseHelper extends SQLiteOpenHelper {
        /****************************************************************************************
         * Database name / version.
         ****************************************************************************************/

        private static final String DATABASE_NAME = "pns_records.db";
        private static final int DATABASE_VERSION = 5;

        /****************************************************************************************
         * SQLs.
         ****************************************************************************************/

        /**
         * SQL query to create events table.
         */
        private static final String SQL_CREATE_TABLE_EVENTS =
                "CREATE TABLE " + TABLE_EVENTS + " ( "
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_TIMESTAMP + " INTEGER NOT NULL, "
                        + COLUMN_TYPE + " TEXT NOT NULL CHECK (" + COLUMN_TYPE
                        + " IN ('" + EVENT_TYPE_REGISTRATION + "','" + EVENT_TYPE_UPDATE + "','"
                        + EVENT_TYPE_UNREGISTRATION + "')), "
                        + COLUMN_ACTION_CAUSE + " TEXT NOT NULL, "
                        + COLUMN_RESULT_CAUSE + " TEXT, "
                        + COLUMN_SUCCESS + " BOOLEAN NOT NULL "
                        + "CHECK (" + COLUMN_SUCCESS + " IN (0,1)))";

        /**
         * SQL query to create messages table.
         */
        private static final String SQL_CREATE_TABLE_MESSAGES =
                "CREATE TABLE " + TABLE_MESSAGES + " ( "
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_TIMESTAMP + " INTEGER NOT NULL, "
                        + COLUMN_MSG_ID + " TEXT, " + COLUMN_APPLIST + " TEXT)";

        /**
         * Temporary events table for database migration.
         */
        private static final String TABLE_EVENTS_TMP = "events_tmp";

        /**
         * Legacy registration event name in v4 and before.
         */
        private static final String EVENT_TYPE_REGISTRATION_V4 = "registeration";

        /**
         * Legacy unregistration event name in v4 and before.
         */
        private static final String EVENT_TYPE_UNREGISTRATION_V4 = "unregisteration";

        /**
         * SQL to migrate update events from v4 to v5.
         */
        private static final String SQL_MIGRATE_UPDATE_EVENTS_V4_TO_V5 =
                "INSERT INTO " + TABLE_EVENTS + "( "
                        + COLUMN_TIMESTAMP + ", "
                        + COLUMN_TYPE + ", "
                        + COLUMN_ACTION_CAUSE + ", "
                        + COLUMN_RESULT_CAUSE + ", "
                        + COLUMN_SUCCESS +
                        ") SELECT " + COLUMN_TIMESTAMP + ", "
                        + COLUMN_TYPE + ", "
                        + COLUMN_ACTION_CAUSE + ", "
                        + COLUMN_RESULT_CAUSE + ", "
                        + COLUMN_SUCCESS + " FROM " + TABLE_EVENTS_TMP
                        + " WHERE " + COLUMN_TYPE + " = '" + EVENT_TYPE_UPDATE + "'";

        /**
         * SQL to migrate registration events from v4 to v5.
         */
        private static final String SQL_MIGRATE_REG_EVENTS_V4_TO_V5 =
                "INSERT INTO " + TABLE_EVENTS + "( "
                        + COLUMN_TIMESTAMP + ", "
                        + COLUMN_TYPE + ", "
                        + COLUMN_ACTION_CAUSE + ", "
                        + COLUMN_RESULT_CAUSE + ", "
                        + COLUMN_SUCCESS +
                        ") SELECT " + COLUMN_TIMESTAMP + ", "
                        + "REPLACE(" + COLUMN_TYPE + ", '"
                        + EVENT_TYPE_REGISTRATION_V4 + "', '"
                        + EVENT_TYPE_REGISTRATION + "'), "
                        + COLUMN_ACTION_CAUSE + ", "
                        + COLUMN_RESULT_CAUSE + ", "
                        + COLUMN_SUCCESS + " FROM " + TABLE_EVENTS_TMP
                        + " WHERE " + COLUMN_TYPE + " = '" + EVENT_TYPE_REGISTRATION_V4 + "'";

        /**
         * SQL to migrate unregistration events from v4 to v5.
         */
        private static final String SQL_MIGRATE_UNREG_EVENTS_V4_TO_V5 =
                "INSERT INTO " + TABLE_EVENTS
                        + "( "
                        + COLUMN_TIMESTAMP + ", "
                        + COLUMN_TYPE + ", "
                        + COLUMN_ACTION_CAUSE + ", "
                        + COLUMN_RESULT_CAUSE + ", "
                        + COLUMN_SUCCESS +
                        ") SELECT " + COLUMN_TIMESTAMP + ", "
                        + "REPLACE(" + COLUMN_TYPE + ", '"
                        + EVENT_TYPE_UNREGISTRATION_V4 + "', '"
                        + EVENT_TYPE_UNREGISTRATION + "'), "
                        + COLUMN_ACTION_CAUSE + ", "
                        + COLUMN_RESULT_CAUSE + ", "
                        + COLUMN_SUCCESS + " FROM " + TABLE_EVENTS_TMP
                        + " WHERE " + COLUMN_TYPE + " = '" + EVENT_TYPE_UNREGISTRATION_V4 + "'";

        private HtcLogger mmLogger = new PushLoggerFactory(this).create();

        public PnsRecordsDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mmLogger.verbose("Creating tables.");
            db.execSQL(SQL_CREATE_TABLE_EVENTS);
            db.execSQL(SQL_CREATE_TABLE_MESSAGES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            mmLogger.info("Upgrading database from version ", oldVersion, " to ", newVersion, ".");

            switch (oldVersion) {
                case 4:
                    /*
                     * Re-create events table to update the check constraint.
                     */
                    db.beginTransaction();
                    try {
                        db.execSQL("ALTER TABLE " + TABLE_EVENTS + " RENAME TO " + TABLE_EVENTS_TMP);
                        db.execSQL(SQL_CREATE_TABLE_EVENTS);
                        db.execSQL(SQL_MIGRATE_UPDATE_EVENTS_V4_TO_V5);
                        db.execSQL(SQL_MIGRATE_REG_EVENTS_V4_TO_V5);
                        db.execSQL(SQL_MIGRATE_UNREG_EVENTS_V4_TO_V5);
                        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS_TMP);
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                    break;

                default:
                    mmLogger.warning("Incompatible version found. All existing table will be dropped.");
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
                    onCreate(db);
            }

        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            mmLogger.warning("old version = ", oldVersion, ", newVersion = ", newVersion);

            try {
                super.onDowngrade(db, oldVersion, newVersion);
            } catch (Exception e) {
                mmLogger.error(e);
            }
        }
    }
}
