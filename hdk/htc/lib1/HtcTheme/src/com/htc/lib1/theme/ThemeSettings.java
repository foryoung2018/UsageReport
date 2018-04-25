package com.htc.lib1.theme;


import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.SQLException;
import android.net.Uri;
import android.os.*;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.File;
import java.util.HashMap;

/**
 * A class modified from Setting to provide similar functionality in non-htc case.
 *
 */
public class ThemeSettings {

    public static final String AUTHORITY = "com.htc.themepicker.themeSetting";
    public static final String CALL_METHOD_USER_KEY = "_user";

    private static String LOG_TAG = "ThemeSettings";
    public static final boolean LOCAL_LOGV = true;

    public static final int DB_VERSION = 1;
    public static final String CALL_METHOD_GET_SYSTEM = "GET_system";
    public static final String CALL_METHOD_PUT_SYSTEM = "PUT_system";

    /**
     * Common base for tables of name/value settings.
     */

    public static class NameValueTable implements BaseColumns {
        public static final String NAME = "name";
        public static final String VALUE = "value";
        public static final String DB = "db";

        protected static boolean putString(ContentResolver resolver, Uri uri,
                                           String name, String value) {
            // The database will take care of replacing duplicates.
            try {
                ContentValues values = new ContentValues();
                values.put(NAME, name);
                values.put(VALUE, value);
                values.put(DB, DB_VERSION);
                resolver.insert(uri, values);
                return true;
            } catch (SQLException e) {
                ThemeSettingUtil.logw(LOG_TAG, "Can't set key " + name + " in " + uri, e);
                return false;
            }
        }

        public static Uri getUriFor(Uri uri, String name) {
            return Uri.withAppendedPath(uri, name);
        }
    }

    // Thread-safe.
    private static class NameValueCache {
        private final String mVersionSystemProperty;
        private final Uri mUri;

        private static final String[] SELECT_VALUE =
                new String[] { NameValueTable.VALUE };
        private static final String NAME_EQ_PLACEHOLDER = "name=?";

        // Must synchronize on 'this' to access mValues and mValuesVersion.
        private final HashMap<String, String> mValues = new HashMap<String, String>();

        // The method we'll call (or null, to not use) on the provider
        // for the fast path of retrieving settings.
        private final String mCallGetCommand;
        private final String mCallSetCommand;

        public NameValueCache(String versionSystemProperty, Uri uri,
                              String getCommand, String setCommand) {
            mVersionSystemProperty = versionSystemProperty;
            mUri = uri;
            mCallGetCommand = getCommand;
            mCallSetCommand = setCommand;
        }

        private ContentProviderClient lazyGetProvider(ContentResolver cr) {
            ContentProviderClient cp = cr.acquireUnstableContentProviderClient(mUri);
            return cp;
        }

        public boolean putStringForUser(ContentResolver cr, String name, String value,
                                        final int userHandle) {
            ThemeSettingUtil.logd(LOG_TAG, "putStringForUser %s, %s, %s, %s", name, value, userHandle, myUserId());
            ContentProviderClient cp = null;
            try {
                Bundle arg = new Bundle();
                arg.putString(NameValueTable.NAME, name);
                arg.putString(NameValueTable.VALUE, value);
                arg.putInt(CALL_METHOD_USER_KEY, userHandle);
                arg.putInt(NameValueTable.DB, DB_VERSION);
                cp = lazyGetProvider(cr);
                cp.call(mCallSetCommand, name, arg);
            } catch (Exception e) {
                ThemeSettingUtil.logw(LOG_TAG, "Can't set key " + name + " in " + mUri, e);
                return false;
            } finally {
                if (cp != null)
                    cp.release();
            }
            return true;
        }

        public String getStringForUser(ContentResolver cr, String name, final int userHandle, boolean forceGet) {
            ThemeSettingUtil.logd(LOG_TAG, "getStringForUser %s, %s, %s", name, userHandle, myUserId());
            final boolean isSelf = (userHandle == ThemeSettings.myUserId());
            if (!forceGet && isSelf) {
                // Our own user's settings data uses a client-side cache
                synchronized (this) {
                    if (mValues.containsKey(name)) {
                        String s = mValues.get(name);
                        ThemeSettingUtil.logd(LOG_TAG, "find cache key %s, value %s", name, s);
                        return s;
                    }
                }
            } else {
                if (LOCAL_LOGV)  ThemeSettingUtil.logd(LOG_TAG, "get setting for user " + userHandle
                        + " by user " + myUserId() + " so skipping cache");
            }

            ContentProviderClient cp = null;
            // Try the fast path first, not using query().  If this
            // fails (alternate provider that doesn't support
            // this interface?) then we fall back to the query/table
            // interface.
            if (mCallGetCommand != null) {
                try {
                    cp = lazyGetProvider(cr);
                    Bundle args = new Bundle();
                    args.putInt(CALL_METHOD_USER_KEY, userHandle);
                    args.putInt(NameValueTable.DB, DB_VERSION);
                    Bundle b = cp.call(mCallGetCommand, name, args);
                    if (b != null) {

                        String resultName = b.getString(NameValueTable.NAME);
                        String value = b.getString(NameValueTable.VALUE);
                        if (name != null && !name.equals(resultName)) {
                            ThemeSettingUtil.logw(LOG_TAG, "get setting name not match %s, %s, %s", resultName, name, value);
                            return null;
                        }

                        // Don't update our cache for reads of other users' data
                        if (isSelf) {
                            synchronized (this) {
                                mValues.put(name, value);
                            }
                        } else {
                            Log.i(LOG_TAG, "call-query of user " + userHandle
                                    + " by " + myUserId()
                                    + " so not updating cache");
                        }
                        ThemeSettingUtil.logd(LOG_TAG, "find key %s, value %s", name, value);
                        return value;
                    }
                    // If the response Bundle is null, we fall through
                    // to the query interface below.
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (cp != null)
                        cp.release();
                }
            }

//            Cursor c = null;
//            try {
//                c = cp.query(mUri, SELECT_VALUE, NAME_EQ_PLACEHOLDER,
//                        new String[]{name}, Integer.toString(DB_VERSION), null);
//                if (c == null) {
//                    ThemeSettingUtil.logw(LOG_TAG, "Can't get key " + name + " from " + mUri);
//                    return null;
//                }
//
//                String value = c.moveToNext() ? c.getString(0) : null;
//                synchronized (this) {
//                    mValues.put(name, value);
//                }
//                if (LOCAL_LOGV) {
//                    ThemeSettingUtil.logd(LOG_TAG, "cache miss [" + mUri.getLastPathSegment() + "]: " +
//                            name + " = " + (value == null ? "(null)" : value));
//                }
//                return value;
//            } catch (RemoteException e) {
//                ThemeSettingUtil.logw(LOG_TAG, "Can't get key " + name + " from " + mUri, e);
//                return null;  // Return null, but don't cache it.
//            } finally {
//                if (c != null) c.close();
//            }
            return null;
        }

        void removeValueCache(String key) {
            synchronized (this) {
                String s = mValues.remove(key);
                ThemeSettingUtil.logd(LOG_TAG, "removeValueCache key %s, value %s", key, s);
            }
        }
    }

    public static final class System extends NameValueTable {
        public static final String SYS_PROP_SETTING_VERSION = "sys.settings_system_version";

        /**
         * The content:// style URL for this table
         */
        public static final String NAME_VALUE_SPACE = "system";

        public static final Uri CONTENT_URI =
                Uri.parse("content://" + AUTHORITY + File.separator + NAME_VALUE_SPACE);

        private static final NameValueCache sNameValueCache = new NameValueCache(
                SYS_PROP_SETTING_VERSION,
                CONTENT_URI,
                CALL_METHOD_GET_SYSTEM,
                CALL_METHOD_PUT_SYSTEM);


        /**
         * Look up a name in the database.
         * @param resolver to access the database with
         * @param name to look up in the table
         * @return the corresponding value, or null if not present
         */
        public static String getString(ContentResolver resolver, String name) {
            return getStringForUser(resolver, name, myUserId(), false);
        }

        /**
         * @param forceGet force to get value from content provider instead of cache.
         * */
        public static String getString(ContentResolver resolver, String name, boolean forceGet) {
            return getStringForUser(resolver, name, myUserId(), forceGet);
        }

        public static String getStringForUser(ContentResolver resolver, String name,
                                              int userHandle) {
            return getStringForUser(resolver, name, userHandle, false);
        }

        public static String getStringForUser(ContentResolver resolver, String name,
                                              int userHandle, boolean forceGet) {
            return sNameValueCache.getStringForUser(resolver, name, userHandle, forceGet);
        }

        /**
         * Store a name/value pair into the database.
         * @param resolver to access the database with
         * @param name to store
         * @param value to associate with the name
         * @return true if the value was set, false on database errors
         */
        public static boolean putString(ContentResolver resolver, String name, String value) {
            return putStringForUser(resolver, name, value, myUserId());
        }

        public static boolean putStringForUser(ContentResolver cr, String name, String value,
                                               int userHandle) {

            return sNameValueCache.putStringForUser(cr, name, value, userHandle);
        }

        /**
         * Construct the content URI for a particular name/value pair,
         * useful for monitoring changes with a ContentObserver.
         * @param name to look up in the table
         * @return the corresponding content URI, or null if not present
         */
        public static Uri getUriFor(String name) {
            return getUriFor(CONTENT_URI, name);
        }

        static void removeValueCache(String nameValueSpace,String key) {
            if (!NAME_VALUE_SPACE.equals(nameValueSpace))
                return;

            sNameValueCache.removeValueCache(key);
        }
    }

    /**
     * Reflecation of UserHandle.java
     * Returns the user id of the current process
     * @return user id of the current process
     * @hide
     *
     *
     * don't need to deal with multiple user case
     */
    public static final int myUserId() {
        int myUid = 0;
//        try {
//            Class<?> clz = UserHandle.class;
//            Method method = clz.getDeclaredMethod("myUserId");
//            Object value = method.invoke(null);
//            myUid = (int) value;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            Log.w(LOG_TAG, "Exception occurs. fallback");
//            myUid = 0;
//        }
        return myUid;
    }
}
