package com.htc.lib2.mock.opensense.cachemanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

import com.htc.lib2.opensense.cache.Download;
import com.htc.lib2.opensense.internal.SystemWrapper;
import com.htc.lib2.opensense.test.cache.DownloadTestCase;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

public class CacheProvider extends ContentProvider {

	final static int DOWNlOADS = 0;
	final static int DOWNlOAD_ID = 1;
	final static int PROFILES = 2;
	final static int PROFILE_ID = 3;
	final static int RAWQUERY = 4;
	final static int CACHE_IMAGE = 5;
	final static int ENCRYPTION_KEY = 6;
	public static final String LOG_TAG = "CacheProvider";
	public static final String IMAGE_CACHE_FOLDER = "img";
	private static final String DEFAULT_ENCRYPTION_STRING = "JYXaO8sfJCoZEk0OLfnnOOd9DmwnF9lcOAJSXk25524aqVKI6BIlukk5W1lzYfwS";
	final static UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	DatabaseHelper mDatabaseHelper;
	static {
			SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
			sURIMatcher.addURI(Download.AUTHORITY, Download.DOWNLOAD_TB, DOWNlOADS);
			sURIMatcher.addURI(Download.AUTHORITY, Download.DOWNLOAD_TB + "/#", DOWNlOAD_ID);
			sURIMatcher.addURI(Download.AUTHORITY, Download.PROFILE_TB, PROFILES);
			sURIMatcher.addURI(Download.AUTHORITY, Download.PROFILE_TB + "/#", PROFILE_ID);
			sURIMatcher.addURI(Download.AUTHORITY, Download.RAWQUERY, RAWQUERY);
			sURIMatcher.addURI(Download.AUTHORITY, Download.IMG_CACHE + "/*", CACHE_IMAGE);
			sURIMatcher.addURI(Download.AUTHORITY, Download.ENCRYPTION_KEY, ENCRYPTION_KEY);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		final static String DB_NAME = "cache.db";
		final static int DB_VERSION = 4;

		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			createTB(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			dropTB(db);
			createTB(db);
		}

		@Override
		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(LOG_TAG, "[onDowngrade]");
		}

		void dropTB(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS " + Download.DOWNLOAD_TB);
			db.execSQL("DROP TABLE IF EXISTS " + Download.PROFILE_TB);
		}

		void createTB(SQLiteDatabase db) {
			StringBuilder builder = new StringBuilder();
			builder.append("CREATE TABLE ");
			builder.append(Download.DOWNLOAD_TB);
			builder.append("(");
			builder.append(Download._ID);
			builder.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
			builder.append(Download.CONTENT_URI);
			builder.append(" TEXT, ");
			builder.append(Download.IMG_URL);
			builder.append(" TEXT, ");
			builder.append(Download.IMG_URL_HASH);
			builder.append(" TEXT, ");
			builder.append(Download.STORE_FOLDER);
			builder.append(" TEXT, ");
			builder.append(Download.STATUS);
			builder.append(" INTEGER, ");
			builder.append(Download.FILE_SIZE);
			builder.append(" INTEGER, ");
			builder.append(Download.LAST_MODIFIED_TIME);
			builder.append(" INETGER, UNIQUE(");
			builder.append(Download.IMG_URL_HASH);
			builder.append(",");
			builder.append(Download.STORE_FOLDER);
			builder.append("));");
			db.execSQL(builder.toString());

			builder = new StringBuilder();
			builder.append("CREATE TABLE ");
			builder.append(Download.PROFILE_TB);
			builder.append("(");
			builder.append(Download._ID);
			builder.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
			builder.append(Download.ACCOUNT_TYPE);
			builder.append(" TEXT, ");
			builder.append(Download.USER_ID);
			builder.append(" TEXT, ");
			builder.append(Download.DOWNLOAD_ID);
			builder.append(" INETGER, UNIQUE(");
			builder.append(Download.ACCOUNT_TYPE);
			builder.append(",");
			builder.append(Download.USER_ID);
			builder.append("));");
			db.execSQL(builder.toString());
		}
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		int match = sURIMatcher.match(uri);
		String table = null;
		long insertTime = System.currentTimeMillis();
		switch (match) {
			case DOWNlOADS: {
				table = Download.DOWNLOAD_TB;
				break;
			}
			case PROFILES: {
				table = Download.PROFILE_TB;
				break;
			}
		}
		if ( table != null ) {
			for (ContentValues value : values) {
				value.put(Download.LAST_MODIFIED_TIME, insertTime);
				db.insert(table, null, value);
			}
		}

		return values.length;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		int match = sURIMatcher.match(uri);
		String table = null;
		String where = null;
		String id;
		switch (match) {
			case DOWNlOADS: {
				table = Download.DOWNLOAD_TB;
				where = selection;
				break;
			}
			case DOWNlOAD_ID: {
				id = uri.getPathSegments().get(1);
				table = Download.DOWNLOAD_TB;
				where = Download._ID + " = " + id;
				if (selection != null) {
					where = selection + " AND " + where;
				}
				break;
			}
			case PROFILES: {
				table = Download.PROFILE_TB;
				where = selection;
				break;
			}
			case PROFILE_ID: {
				id = uri.getPathSegments().get(1);
				table = Download.PROFILE_TB;
				where = Download._ID + " = " + id;
				if (selection != null) {
					where = selection + " AND " + where;
				}
				break;
			}
			case CACHE_IMAGE: {
				return deleteFile(uri);
			}
		}
		if ( table != null ) {
			db.delete(table, where, selectionArgs);
		}

		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		int match = sURIMatcher.match(uri);
		String table = null;
		switch ( match ) {
			case DOWNlOADS: {
				table = Download.DOWNLOAD_TB;
				values.put(Download.LAST_MODIFIED_TIME, System.currentTimeMillis());
				break;
			}

			case PROFILES: {
				table = Download.PROFILE_TB;
				break;
			}
		}
		if ( table != null ) {
			long rowID = db.insert(table, null, values);
			return ContentUris.withAppendedId(uri, rowID);
		}
		return null;
	}

	@Override
	public boolean onCreate() {
		mDatabaseHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projections, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		Cursor c = null;
		Log.d(LOG_TAG, "uri " + uri);
		int match = sURIMatcher.match(uri);
		Log.d(LOG_TAG, "match  " + match);
		String table = null;
		String where = null;
		String id;
		switch (match) {
			case RAWQUERY: {
				Log.d(LOG_TAG, "selection " + selection);
				return db.rawQuery(selection, selectionArgs);
			}
			case DOWNlOADS: {
				table = Download.DOWNLOAD_TB;
				where = selection;
				break;
			}
			case DOWNlOAD_ID: {
				id = uri.getPathSegments().get(1);
				table = Download.DOWNLOAD_TB;
				where = Download._ID + " = " + id;
				if (selection != null) {
					where = selection + " AND " + where;
				}
				break;
			}
			case PROFILES: {
				table = Download.PROFILE_TB;
				where = selection;
				break;
			}
			case PROFILE_ID: {
				id = uri.getPathSegments().get(1);
				table = Download.PROFILE_TB;
				where = Download._ID + " = " + id;
				if (selection != null) {
					where = selection + " AND " + where;
				}
				break;
			}
			case CACHE_IMAGE: {
				if (isUriExist(uri)) {
					MatrixCursor cursor = new MatrixCursor(new String[] { "key",
							"value" });
					cursor.addRow(new String[] { "file_exist", "true" });
					return cursor;
				}
				return null;
			}
			case ENCRYPTION_KEY: {
				SharedPreferences preferences = getContext().getSharedPreferences("encryption", Context.MODE_PRIVATE);
				String encryptionKey = preferences.getString("key", "");
				if ( "".equals(encryptionKey) ) {
					encryptionKey = getEncryptionKey();
					
					preferences.edit().putString(
							"key",
							encryptionKey
					).apply();
				}

				MatrixCursor cursor = new MatrixCursor(new String[] {"key", "value"});
				cursor.addRow(new String[] { "encryption_key", encryptionKey });
				return cursor;
			}
		}
		if (table != null) {
			c = db.query(table, projections, where, selectionArgs, null, null, sortOrder);
		}

		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		int count = 0;
		int match = sURIMatcher.match(uri);
		String table = null;
		String where = null;
		String id;
		switch (match) {
			case DOWNlOADS: {
				table = Download.DOWNLOAD_TB;
				where = selection;
				break;
			}
			case DOWNlOAD_ID: {
				id = uri.getPathSegments().get(1);
				table = Download.DOWNLOAD_TB;
				where = Download._ID + " = " + id;
				if (selection != null) {
					where = selection + " AND " + where;
				}
				break;
			}
			case PROFILES: {
				table = Download.PROFILE_TB;
				where = selection;
				break;
			}
			case PROFILE_ID: {
				id = uri.getPathSegments().get(1);
				table = Download.PROFILE_TB;
				where = Download._ID + " = " + id;
				if (selection != null) {
					where = selection + " AND " + where;
				}
				break;
			}
		}
		if (table != null) {
			count = db.update(table, values, where, selectionArgs);
		}
		return count;

	}

	public boolean isUriExist(Uri uri) {
		final File cacheDir = new File(this.getContext().getCacheDir(),
				IMAGE_CACHE_FOLDER);
		File fileToDelete = new File(cacheDir, uri.getLastPathSegment());
		return fileToDelete.isFile();

	}

	public int deleteFile(Uri uri) {
		final File cacheDir = new File(this.getContext().getCacheDir(),
				IMAGE_CACHE_FOLDER);
		File fileToDelete = new File(cacheDir, uri.getLastPathSegment());
		if ( fileToDelete.isFile() ) {
			return fileToDelete.delete() ? 1 : 0;
		}
		return 1;
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {
		Log.d(LOG_TAG, "open file " + uri.toString());
		if ( sURIMatcher.match(uri) != CACHE_IMAGE ) {
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
		final File cacheDir = new File(this.getContext().getCacheDir(),
				IMAGE_CACHE_FOLDER);

		if ( !cacheDir.exists() ) {
			cacheDir.mkdirs();
		}
		File file = new File(cacheDir, uri.getLastPathSegment());
		int parcel_mode = 0;
		if ( mode.contains("r") && mode.contains("w") ) {
			parcel_mode |= ParcelFileDescriptor.MODE_READ_WRITE;
			parcel_mode |= ParcelFileDescriptor.MODE_CREATE;
		} else if ( mode.contains("r") ) {
			parcel_mode |= ParcelFileDescriptor.MODE_READ_ONLY;
		} else if ( mode.contains("w") ) {
			parcel_mode |= ParcelFileDescriptor.MODE_WRITE_ONLY;
			parcel_mode |= ParcelFileDescriptor.MODE_CREATE;
		}
		if ( mode.contains("t") ) {
			parcel_mode |= ParcelFileDescriptor.MODE_TRUNCATE;
		}
		if ( mode.contains("a") ) {
			parcel_mode |= ParcelFileDescriptor.MODE_APPEND;
		}
		if ( parcel_mode == 0 ) {
			throw new IllegalArgumentException("Unknown mode " + uri + " " + mode);
		}
		if ( mode.contains("w") && !file.exists() ) {
			try {
				file.createNewFile();
			} catch ( IOException e ) {
				Log.e(LOG_TAG, "openFile failed!", e);
				throw new FileNotFoundException();
			}
		}
		return ParcelFileDescriptor.open(file, parcel_mode);
	}

	private String getEncryptionKey() {
		RandomString randomString = new RandomString(64);
		String s = randomString.nextString();
		if ( TextUtils.isEmpty(s) ) {
			s = DEFAULT_ENCRYPTION_STRING;
		}
		return s;
	}

	private static class RandomString {

		private static final String SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

		private final Random mRandom = new SecureRandom();
		private final char[] mRandomStringContainer;

		public RandomString(int length) {
			if ( length < 1 ) {
				length = 1;
			}
			mRandomStringContainer = new char[length];
		}

		public String nextString() {
			for (int idx = 0; idx < mRandomStringContainer.length; ++idx) {
				mRandomStringContainer[idx] = SYMBOLS.charAt(mRandom.nextInt(SYMBOLS.length()));
			}
			return new String(mRandomStringContainer);
		}
	}
}
