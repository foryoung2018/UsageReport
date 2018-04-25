
package com.htc.lib1.cs.account;

import android.accounts.Account;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Data source for {@link HtcAccountManager}. It's mainly for
 * {@link LocalHtcAccountManager} to store account info, but is also used to
 * keep account id record in {@link SystemHtcAccountManager}.
 * 
 * @author samael_wang@htc.com
 */
/* package */class HtcAccountManagerDataSource {
    /****************************************************************************************
     * Tables and columns.
     ****************************************************************************************/
    private static final String TABLE_ACCOUNTS = "accounts";
    private static final String TABLE_AUTHTOKENS = "authtokens";
    private static final String TABLE_AUTHTOKENS_INTERNAL = "authtokens_internal";
    private static final String TABLE_EXTRAS = "extras";
    private static final String TABLE_GUIDS = "guids";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ACCOUNTS_ID = "accounts_id";
    private static final String COLUMN_AUTHTOKEN = "authtoken";
    private static final String COLUMN_KEY = "key";
    private static final String COLUMN_VALUE = "value";
    private static final String COLUMN_GUID = "guid";

    private static HtcAccountManagerDataSource sInstance;
    private SQLiteOpenHelper mDbHelper;

    /**
     * Get the instance of {@link HtcAccountManagerDataSource}.
     * 
     * @param context Context used to retrieve application context.
     * @return {@link HtcAccountManagerDataSource}
     */
    public static synchronized HtcAccountManagerDataSource get(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        if (sInstance == null)
            sInstance = new HtcAccountManagerDataSource(context.getApplicationContext());
        return sInstance;
    }

    private HtcAccountManagerDataSource(Context context) {
        mDbHelper = new HtcAccountManagerDatabaseHelper(context);
    }

    /**
     * Clear all content in the database.
     */
    public void clear() {
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.delete(TABLE_ACCOUNTS, null, null);
            db.delete(TABLE_AUTHTOKENS, null, null);
            db.delete(TABLE_EXTRAS, null, null);
            db.delete(TABLE_GUIDS, null, null);
            db.close();
        }
    }

    /**
     * Add an account to the database.
     * 
     * @param account Account to insert. Must not be {@code null}.
     * @return True if the account is successfully inserted. False otherwise.
     */
    public boolean addAccount(Account account) {
        if (account == null || TextUtils.isEmpty(account.type) || TextUtils.isEmpty(account.name))
            throw new IllegalArgumentException("'account' is null or its content is insufficient.");

        // Compose values.
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, account.type);
        values.put(COLUMN_NAME, account.name);

        // Insert.
        boolean result;
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            result = (db.insertWithOnConflict(TABLE_ACCOUNTS, null, values,
                    SQLiteDatabase.CONFLICT_ABORT) != -1);
            db.close();
        }

        return result;
    }

    /**
     * Get accounts.
     * 
     * @param typeToQuery Optional string to specify type of accounts to query.
     * @return An array of accounts, never {@code null}. If no accounts found,
     *         it returns an empty array.
     */
    public Account[] getAccounts(String typeToQuery) {
        // Construct selection arguments.
        String selection = null;
        String[] selectionArgs = null;
        if (!TextUtils.isEmpty(typeToQuery)) {
            selection = COLUMN_TYPE + "=?";
            selectionArgs = new String[] {
                    typeToQuery
            };
        }

        // Make query.
        Account[] accounts;
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            Cursor cursor = mDbHelper.getReadableDatabase().query(TABLE_ACCOUNTS,
                    new String[] {
                            COLUMN_TYPE, COLUMN_NAME
                    }, selection, selectionArgs, null, null, null);

            // Compose account array.
            int count = cursor.getCount();
            accounts = new Account[count];
            if (count > 0) {
                cursor.moveToFirst();
                int columnType = cursor
                        .getColumnIndexOrThrow(COLUMN_TYPE);
                int columnName = cursor
                        .getColumnIndexOrThrow(COLUMN_NAME);
                for (int i = 0; i < count; i++) {
                    String type = cursor.getString(columnType);
                    String name = cursor.getString(columnName);
                    accounts[i] = new Account(name, type);
                    cursor.moveToNext();
                }
            }

            cursor.close();
            db.close();
        }

        return accounts;
    }

    /**
     * Set the password of specific account.
     * 
     * @param account Account to modify. Must not be {@code null}.
     * @param password Password to set.
     */
    public void setPassword(Account account, String password) {
        if (account == null || TextUtils.isEmpty(account.type) || TextUtils.isEmpty(account.name))
            throw new IllegalArgumentException("'account' is null or its content is insufficient.");

        // Compose values.
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, password);

        // Update.
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            try {
                throwIfNotExactlyOneRow(db.update(TABLE_ACCOUNTS, values,
                        COLUMN_TYPE + "=? AND " + COLUMN_NAME + "=?",
                        new String[] {
                                account.type, account.name
                        }));
            } finally {
                db.close();
            }
        }
    }

    /**
     * Get the password of a specific account.
     * 
     * @param account Account to query. Must not be {@code null}.
     * @return Password set before or {@code null} if not set yet.
     */
    public String getPassword(Account account) {
        if (account == null || TextUtils.isEmpty(account.type) || TextUtils.isEmpty(account.name))
            throw new IllegalArgumentException("'account' is null or its content is insufficient.");

        // Compose selections.
        String selection = COLUMN_TYPE + "=? AND " + COLUMN_NAME + "=?";
        String[] selectionArgs = new String[] {
                account.type, account.name
        };

        // Query.
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            Cursor cursor = db.query(TABLE_ACCOUNTS, new String[] {
                    COLUMN_PASSWORD
            }, selection, selectionArgs, null, null, null);

            try {
                if (!cursor.moveToFirst())
                    throw new IllegalStateException("No satisfied row found.");
                return cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
            } finally {
                cursor.close();
                db.close();
            }
        }

    }

    /**
     * Get the database primary key of the specific account.
     * 
     * @param account Account to find. Must not be {@code null}.
     * @return Id of the row in database.
     */
    public long getId(Account account) {
        if (account == null || TextUtils.isEmpty(account.type) || TextUtils.isEmpty(account.name))
            throw new IllegalArgumentException("'account' is null or its content is insufficient.");

        // Compose selections.
        String selection = COLUMN_TYPE + "=? AND " + COLUMN_NAME + "=?";
        String[] selectionArgs = new String[] {
                account.type, account.name
        };

        // Query.
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            Cursor cursor = db.query(TABLE_ACCOUNTS, new String[] {
                    COLUMN_ID
            }, selection, selectionArgs, null, null, null);

            try {
                if (!cursor.moveToFirst())
                    throw new IllegalStateException("No satisfied row found.");
                return cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
            } finally {
                cursor.close();
                db.close();
            }
        }
    }

    /**
     * Set the GUID of a specific account.
     * 
     * @param account Account of the GUID. Must not be {@code null}.
     * @param Guid GUID to store. Must not be {@code null}.
     * @param authToken Account authtoken to store. Must not be {@code null}.
     */
    public void setGuid(Account account, String guid, String authToken) {
        if (account == null || TextUtils.isEmpty(account.type) || TextUtils.isEmpty(account.name))
            throw new IllegalArgumentException("'account' is null or its content is insufficient.");
        if (TextUtils.isEmpty(guid))
            throw new IllegalArgumentException("'guid' is null or empty.");
        if (TextUtils.isEmpty(authToken))
            throw new IllegalArgumentException("'authToken' is null or empty.");

        // Compose values.
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, account.type);
        values.put(COLUMN_NAME, account.name);
        values.put(COLUMN_GUID, guid);
        values.put(COLUMN_AUTHTOKEN, authToken);

        // Insert.
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            try {
                throwOnErrorOccurs(db.insertWithOnConflict(TABLE_GUIDS, null, values,
                        SQLiteDatabase.CONFLICT_REPLACE));
            } finally {
                db.close();
            }
        }
    }

    /**
     * Get the GUID stored for the specific account.
     * 
     * @param account Account to find. Must not be {@code null}.
     * @param authToken Account authtoken to filter with. Must not be {@code null}.
     * @return Global user id for the specific account, or {@code null} if not
     *         found.
     */
    public String getGuid(Account account, String authToken) {
        if (account == null || TextUtils.isEmpty(account.type) || TextUtils.isEmpty(account.name))
            throw new IllegalArgumentException("'account' is null or its content is insufficient.");
        if (TextUtils.isEmpty(authToken))
            throw new IllegalArgumentException("'authToken' is null or empty.");

        // Compose selections.
        String selection = COLUMN_TYPE + "=? AND " + COLUMN_AUTHTOKEN + "=?";
        String[] selectionArgs = new String[] {
                account.type, authToken
        };

        // Query.
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            Cursor cursor = db.query(TABLE_GUIDS, new String[] {
                    COLUMN_GUID
            }, selection, selectionArgs, null, null, null);

            try {
                if (!cursor.moveToFirst()) {
                    return null;
                }
                return cursor.getString(cursor.getColumnIndex(COLUMN_GUID));
            } finally {
                cursor.close();
                db.close();
            }
        }
    }

    /**
     * Set the authtoken of a specific account.
     * 
     * @param account Account to modify. Must not be {@code null}.
     * @param authTokenType Type of the authtoken. Must not be {@code null}.
     * @param authToken Authtoken.
     */
    public void setAuthToken(Account account, String authTokenType, String authToken) {
        if (account == null || TextUtils.isEmpty(account.type) || TextUtils.isEmpty(account.name))
            throw new IllegalArgumentException("'account' is null or its content is insufficient.");
        if (TextUtils.isEmpty(authTokenType))
            throw new IllegalArgumentException("'authTokenType' is null or empty.");

        // Compose values.
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACCOUNTS_ID, getId(account));
        values.put(COLUMN_TYPE, authTokenType);
        values.put(COLUMN_AUTHTOKEN, authToken);

        // Insert.
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            try {
                throwOnErrorOccurs(db.insertWithOnConflict(TABLE_AUTHTOKENS, null, values,
                        SQLiteDatabase.CONFLICT_REPLACE));
            } finally {
                db.close();
            }
        }
    }

    /**
     * Get the authtoken of the given {@code account} and {@code authTokenType}.
     * 
     * @param account Account to modify. Must not be {@code null}.
     * @param authTokenType Type of the authtoken. Must not be {@code null}.
     * @return Authtoken found or {@code null} if no satisfied token exists.
     */
    public String getAuthToken(Account account, String authTokenType) {
        if (account == null || TextUtils.isEmpty(account.type) || TextUtils.isEmpty(account.name))
            throw new IllegalArgumentException("'account' is null or its content is insufficient.");
        if (TextUtils.isEmpty(authTokenType))
            throw new IllegalArgumentException("'authTokenType' is null or empty.");

        // Compose selections.
        String selection = COLUMN_ACCOUNTS_ID + "=? AND " + COLUMN_TYPE + "=?";
        String[] selectionArgs = new String[] {
                String.valueOf(getId(account)), authTokenType
        };

        // Query.
        String authtoken;
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            Cursor cursor = db.query(TABLE_AUTHTOKENS, new String[] {
                    COLUMN_AUTHTOKEN
            }, selection, selectionArgs, null, null, null);

            /*
             * The authtoken row does not necessarily exist. Hence if no
             * satisfied row found simply return null.
             */
            authtoken = null;
            if (cursor.moveToFirst())
                authtoken = cursor.getString(cursor.getColumnIndex(COLUMN_AUTHTOKEN));

            cursor.close();
            db.close();
        }

        return authtoken;
    }

    /**
     * Remove all authtoken(s) associated to the given account.
     * 
     * @param id Primary key of the operating account in database.
     */
    public void removeAllAuthTokens(long id) {
        // Compose selections.
        String selection = COLUMN_ACCOUNTS_ID + "=?";
        String[] selectionArgs = new String[] {
                String.valueOf(id)
        };

        // Delete.
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.delete(TABLE_AUTHTOKENS, selection, selectionArgs);
            db.close();
        }
    }

    /**
     * Remove the authtoken from the database.
     * 
     * @param id Primary key of the operating account in database.
     * @param authToken Authtoken to remove. Do nothing if it's {@code null} or
     *            empty.
     */
    public void removeAuthToken(long id, String authToken) {
        if (!TextUtils.isEmpty(authToken)) {
            // Compose selection.
            String selection = COLUMN_ACCOUNTS_ID + "=? AND " + COLUMN_AUTHTOKEN + "=?";
            String[] selectionArgs = new String[] {
                    String.valueOf(id),
                    authToken
            };

            // Delete.
            synchronized (mDbHelper) {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                db.delete(TABLE_AUTHTOKENS, selection, selectionArgs);
                db.close();
            }
        }
    }

    /**
     * Remove the authtoken from the database.
     * 
     * @param account Account to operate on.
     * @param authToken Authtoken to remove. Do nothing if it's {@code null} or
     *            empty.
     */
    public void removeAuthToken(Account account, String authToken) {
        if (account == null || TextUtils.isEmpty(account.type) || TextUtils.isEmpty(account.name))
            throw new IllegalArgumentException("'account' is null or its content is insufficient.");

        removeAuthToken(getId(account), authToken);
    }

    /**
     * Add user data of a specific account.
     * 
     * @param account Account to operate on. Must not be {@code null}.
     * @param key Key of the data. Must not be {@code null}.
     * @param value Value to set. Could be null or empty.
     */
    public void addUserData(Account account, String key, String value) {
        if (account == null || TextUtils.isEmpty(account.type) || TextUtils.isEmpty(account.name))
            throw new IllegalArgumentException("'account' is null or its content is insufficient.");
        if (TextUtils.isEmpty(key))
            throw new IllegalArgumentException("'key' is null or empty.");

        // Compose values.
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACCOUNTS_ID, getId(account));
        values.put(COLUMN_KEY, key);
        values.put(COLUMN_VALUE, value);

        // Insert.
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            try {
                throwOnErrorOccurs(db.insertWithOnConflict(TABLE_EXTRAS, null, values,
                        SQLiteDatabase.CONFLICT_REPLACE));
            } finally {
                db.close();
            }
        }
    }

    /**
     * Get the user data of a specific account with a specific key.
     * 
     * @param account Account to operate on. Must not be {@code null}.
     * @param key Key of the data. Must not be {@code null}.
     * @return User data in the database or {@code null} if not such key found.
     */
    public String getUserData(Account account, String key) {
        if (account == null || TextUtils.isEmpty(account.type) || TextUtils.isEmpty(account.name))
            throw new IllegalArgumentException("'account' is null or its content is insufficient.");
        if (TextUtils.isEmpty(key))
            throw new IllegalArgumentException("'key' is null or empty.");

        // Compose selections.
        String selection = COLUMN_ACCOUNTS_ID + "=? AND " + COLUMN_KEY + "=?";
        String[] selectionArgs = new String[] {
                String.valueOf(getId(account)), key
        };

        // Query.
        String value;
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            Cursor cursor = db.query(TABLE_EXTRAS, new String[] {
                    COLUMN_VALUE
            }, selection, selectionArgs, null, null, null);

            /*
             * The corresponding user data row does not necessarily exist. Hence
             * if no satisfied row found simply return null.
             */
            value = null;
            if (cursor.moveToFirst())
                value = cursor.getString(cursor.getColumnIndex(COLUMN_VALUE));

            cursor.close();
            db.close();
        }

        return value;
    }

    /**
     * Remove user data of a specific account.
     * 
     * @param id Primary key of the operating account in database.
     * @param key Key of the user data to remove. If not specific, it removes
     *            all user data associated with the given account.
     */
    public void removeUserData(long id, String key) {
        // Compose selections.
        String selection;
        String[] selectionArgs;
        if (TextUtils.isEmpty(key)) {
            selection = COLUMN_ACCOUNTS_ID + "=?";
            selectionArgs = new String[] {
                    String.valueOf(id)
            };
        } else {
            selection = COLUMN_ACCOUNTS_ID + "=? AND " + COLUMN_KEY + "=?";
            selectionArgs = new String[] {
                    String.valueOf(id), key
            };
        }

        // Delete.
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.delete(TABLE_EXTRAS, selection, selectionArgs);
            db.close();
        }
    }

    /**
     * Remove user data of a specific account.
     * 
     * @param account Account to operate on. Must not be {@code null}.
     * @param key Key of the user data to remove. If not specific, it removes
     *            all user data associated with the given account.
     */
    public void removeUserData(Account account, String key) {
        if (account == null || TextUtils.isEmpty(account.type) || TextUtils.isEmpty(account.name))
            throw new IllegalArgumentException("'account' is null or its content is insufficient.");

        removeUserData(getId(account), key);
    }

    /**
     * Remove all user data associated with the given account.
     * 
     * @param id Primary key of the operating account in database.
     */
    public void removeUserData(long id) {
        removeUserData(id, null);
    }

    /**
     * Remove an account and all its associated data from the database.
     * 
     * @param account Account to remove. Must not be {@code null}.
     */
    public void removeAccount(Account account) {
        if (account == null || TextUtils.isEmpty(account.type) || TextUtils.isEmpty(account.name))
            throw new IllegalArgumentException("'account' is null or its content is insufficient.");

        // Compose selection.
        long id = getId(account);
        String selection = COLUMN_ID + "=?";
        String[] selectionArgs = new String[] {
                String.valueOf(id)
        };

        // Delete.
        synchronized (mDbHelper) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.delete(TABLE_ACCOUNTS, selection, selectionArgs);
            db.close();
        }

        // Remove associated tokens and data.
        removeAllAuthTokens(id);
        removeUserData(id);
    }

    private void throwIfNotExactlyOneRow(long rows) {
        if (rows != 1)
            throw new IllegalStateException("Expect 1 row affected but got " + rows);
    }

    private long throwOnErrorOccurs(long rowId) {
        if (rowId == -1)
            throw new IllegalStateException("SQL execution fails.");
        return rowId;
    }

    /**
     * Database helper for {@link HtcAccountManagerDataSource}.
     * 
     * @author samael_wang@htc.com
     */
    private class HtcAccountManagerDatabaseHelper extends SQLiteOpenHelper {
        /****************************************************************************************
         * Database name / version.
         ****************************************************************************************/

        private static final String DATABASE_NAME = "accounts.db";
        private static final int DATABASE_VERSION = 4;

        /****************************************************************************************
         * SQLs.
         ****************************************************************************************/

        private static final String SQL_CREATE_TABLE_ACCOUNTS =
                "CREATE TABLE " + TABLE_ACCOUNTS + " ( "
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_NAME + " TEXT NOT NULL, "
                        + COLUMN_TYPE + " TEXT NOT NULL, "
                        + COLUMN_PASSWORD + " TEXT, "
                        + "UNIQUE(" + COLUMN_NAME + "," + COLUMN_TYPE + "))";

        private static final String SQL_CREATE_TABLE_AUTHTOKENS =
                "CREATE TABLE " + TABLE_AUTHTOKENS + " (  "
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_ACCOUNTS_ID + " INTEGER NOT NULL, "
                        + COLUMN_TYPE + " TEXT NOT NULL,  "
                        + COLUMN_AUTHTOKEN + " TEXT, "
                        + "UNIQUE (" + COLUMN_ACCOUNTS_ID + "," + COLUMN_TYPE + "))";

        private static final String SQL_CREATE_TABLE_EXTRAS =
                "CREATE TABLE " + TABLE_EXTRAS + " ( "
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_ACCOUNTS_ID + " INTEGER, "
                        + COLUMN_KEY + " TEXT NOT NULL, " + COLUMN_VALUE + " TEXT, "
                        + "UNIQUE(" + COLUMN_ACCOUNTS_ID + "," + COLUMN_KEY + "))";

        private static final String SQL_CREATE_TABLE_GUIDS =
                "CREATE TABLE " + TABLE_GUIDS + " ( "
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_NAME + " TEXT NOT NULL, "
                        + COLUMN_TYPE + " TEXT NOT NULL,  "
                        + COLUMN_GUID + " TEXT NOT NULL, "
                        + COLUMN_AUTHTOKEN + " TEXT, "
                        + "UNIQUE(" + COLUMN_GUID + "))";

        private HtcLogger mmLogger = new CommLoggerFactory(this).create();

        public HtcAccountManagerDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mmLogger.verbose("Creating tables.");
            db.execSQL(SQL_CREATE_TABLE_ACCOUNTS);
            db.execSQL(SQL_CREATE_TABLE_AUTHTOKENS);
            db.execSQL(SQL_CREATE_TABLE_EXTRAS);
            db.execSQL(SQL_CREATE_TABLE_GUIDS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            mmLogger.info("Upgrading database from version ", oldVersion, " to ", newVersion, ".");

            switch (oldVersion) {
                case 3:
                    /*
                     * TABLE_GUIDS has new columns from v3 to v4,
                     * and just discard the old data to upgrade.
                     */
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_GUIDS);
                    db.execSQL(SQL_CREATE_TABLE_GUIDS);
                    break;

                case 2:
                    /*
                     * TABLE_GUIDS was new from v2 to v3, and
                     * TABLE_AUTHTOKENS_INTERNAL was removed.
                     */
                    db.execSQL(SQL_CREATE_TABLE_GUIDS);
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTHTOKENS_INTERNAL);
                    break;

                default:
                    // For incompatible versions, drop all and re-create.
                    mmLogger.warning("Incompatible version found. All existing table will be dropped.");
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTHTOKENS);
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTHTOKENS_INTERNAL);
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXTRAS);
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_GUIDS);
                    onCreate(db);
            }
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            mmLogger.info("Downgrading database from version ", oldVersion, " to ", newVersion, ".");

            // For incompatible versions, drop all and re-create.
            mmLogger.warning("Incompatible version found. All existing table will be dropped.");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTHTOKENS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTHTOKENS_INTERNAL);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXTRAS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GUIDS);
            onCreate(db);
        }
    }
}
