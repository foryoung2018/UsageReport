package com.htc.lib2.opensense.plugin;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * A helper class which provides a set of utility methods for managing plugins.
 * 
 * @hide
 */
public class PluginRegistryHelper implements PluginConstants {

    private static final String LOG_TAG = PluginRegistryHelper.class.getSimpleName();
    private static final String URL_RAW_QUERY = "rawquery";
    private static final Uri RAWQUERY = Uri.parse("content://" + AUTHORITY + "/" + URL_RAW_QUERY);
//    private static final Uri FEATURE_URI = Uri.parse("content://" + AUTHORITY + "/" + FEATURE_TB);
//    private static final Uri METADATA_URI = Uri.parse("content://" + AUTHORITY + "/" + METADATA_TB);

    /**
     * Get the list of ComponentName by the specific feature.
     * 
     * @param context the given context
     * @param feature the given feature
     * @return a list of ComponentName
     * 
     * @hide
     */
    public static ComponentName[] getPluginComponents(Context context, String feature) {
        String sql = String.format(
                "SELECT A1.%s, A2.%s FROM %s A1, %s A2 WHERE %s A1.%s IN "
                        + "(SELECT A2.%s FROM %s WHERE A2.%s IN (SELECT %s FROM %s WHERE %s='%s'))",
                COLUMN_PACKAGE,
                COLUMN_PLUGIN_CLASS,
                PLUGIN_PKG_TB, PLUGIN_TB,
                ( "A2." + COLUMN_PLUGIN_REMOVED + "=0 AND" ),
                _ID,
                COLUMN_PACKAGE_ID,
                PLUGIN_PKG_TB,
                COLUMN_FEATURE_ID,
                _ID,
                FEATURE_TB,
                COLUMN_FEATURE,
                feature
        );

        ContentProviderClient client = null;
        Cursor cursor = null;
        try {
            client = context.getContentResolver().acquireUnstableContentProviderClient(RAWQUERY);
            if ( client != null ) {
                cursor = client.query(RAWQUERY, null, sql, null, null);
            } else {
                Log.w(LOG_TAG, "ContentProviderClient is null for uri: " + RAWQUERY.toString());
            }
            if ( cursor != null ) {
                ComponentName[] components = new ComponentName[cursor.getCount()];
                int i = 0;
                while ( cursor.moveToNext() ) {
                    components[i++] = new ComponentName(
                            cursor.getString(0),
                            cursor.getString(1)
                    );
                }
                return components;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "getPluginComponents() error: " + e.getMessage());
        } finally {
            if ( cursor != null ) {
                cursor.close();
            }
            if ( client != null ) {
                client.release();
            }
        }
        return new ComponentName[0];
    }

    /**
     * Get the string list of the Open Sense services by the specific feature.
     * 
     * @param context the given context
     * @param feature the given feature
     * @return a list of service in string
     * 
     * @hide
     */
    public static ArrayList<String> getOpenSenseServices(Context context, String feature) {
        // A1 = feature
        // A2 = plugin
        ArrayList<String> serviceList = new ArrayList<String>();
        String sql = String.format(
                "SELECT A2.%s FROM %s A1, %s A2 where A1.%s='%s' AND A2.%s = A1.%s %s",
                COLUMN_PLUGIN_META,
                FEATURE_TB,
                PLUGIN_TB,
                COLUMN_FEATURE,
                feature,
                COLUMN_FEATURE_ID,
                _ID,
                ( "AND A2." + COLUMN_PLUGIN_REMOVED + "=0" )
        );
        Log.d(LOG_TAG, "select " + sql);

        ContentProviderClient client = null;
        Cursor cursor = null;
        try {
            client = context.getContentResolver().acquireUnstableContentProviderClient(RAWQUERY);
            if ( client != null ) {
                cursor = client.query(RAWQUERY, null, sql, null, null);
            } else {
                Log.w(LOG_TAG, "ContentProviderClient is null for uri: " + RAWQUERY.toString());
            }
            if ( cursor != null ) {
                while ( cursor.moveToNext() ) {
                    serviceList.add(cursor.getString(0));
                }
                return serviceList;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "getOpenSenseServices() error: " + e.getMessage());
        } finally {
            if ( cursor != null ) {
                cursor.close();
            }
            if ( client != null ) {
                client.release();
            }
        }
        return new ArrayList<String>();
    }

    /**
     * Get the list of Plugin by the specific feature.
     * 
     * @param context the given context
     * @param feature the given feature
     * @return a list of Plugin
     * 
     * @hide
     */
    public static Plugin[] getPlugins(Context context, String feature) {
        String sql = String.format(
                "SELECT t1.%s plugin_id, t2.%s, "
                        + "t3.%s feature_id, t3.%s feature_version, t3.%s, t3.%s, "
                        + "t1.%s, t1.%s, t1.%s, t1.%s " + "FROM %s t1, %s t2, %s t3 "
                        + "WHERE t1.%s=t2.%s AND t1.%s=t3.%s AND t3.%s='%s' %s",
                _ID,
                COLUMN_PACKAGE,
                _ID,
                COLUMN_VERSION,
                COLUMN_FEATURE,
                COLUMN_FEATURE_TYPE,
                COLUMN_VERSION,
                COLUMN_PLUGIN_CLASS,
                COLUMN_DESCRIPTION,
                COLUMN_PLUGIN_META,
                PLUGIN_TB,
                PLUGIN_PKG_TB,
                FEATURE_TB,
                COLUMN_PACKAGE_ID,
                _ID,
                COLUMN_FEATURE_ID,
                _ID,
                COLUMN_FEATURE,
                feature,
                ( "AND t1." + COLUMN_PLUGIN_REMOVED + "=0" )
        );

        ContentProviderClient client = null;
        Cursor cursor = null;
        try {
            client = context.getContentResolver().acquireUnstableContentProviderClient(RAWQUERY);
            if ( client != null ) {
                cursor = client.query(RAWQUERY, null, sql, null, null);
            } else {
                Log.w(LOG_TAG, "ContentProviderClient is null for uri: " + RAWQUERY.toString());
            }
            if ( cursor != null ) {
                int plugin_id = cursor.getColumnIndexOrThrow("plugin_id");
                int package_name = cursor.getColumnIndexOrThrow(COLUMN_PACKAGE);
                int feature_id = cursor.getColumnIndexOrThrow("feature_id");
                int feature_version = cursor.getColumnIndexOrThrow("feature_version");
                int feature_name = cursor.getColumnIndexOrThrow(COLUMN_FEATURE);
                int feature_type = cursor.getColumnIndexOrThrow(COLUMN_FEATURE_TYPE);
                int plugin_version = cursor.getColumnIndexOrThrow(COLUMN_VERSION);
                int plugin_class = cursor.getColumnIndexOrThrow(COLUMN_PLUGIN_CLASS);
                int description = cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION);
                int plugin_meta = cursor.getColumnIndexOrThrow(COLUMN_PLUGIN_META);
                Plugin[] plugins = new Plugin[cursor.getCount()];
                for ( int i = cursor.getCount() - 1; i >= 0; i-- ) {
                    if ( cursor.moveToPosition(i) ) {
                        Feature f = new Feature(
                                cursor.getInt(feature_id),
                                cursor.getInt(feature_version),
                                cursor.getString(feature_name),
                                cursor.getString(feature_type)
                        );
                        ComponentName cname = new ComponentName(
                                cursor.getString(package_name),
                                cursor.getString(plugin_class)
                        );
                        plugins[i] = new Plugin(
                                cursor.getInt(plugin_id),
                                f,
                                cname,
                                cursor.getInt(plugin_version),
                                cursor.getString(description),
                                cursor.getString(plugin_meta)
                        );
                    }
                }
                return plugins;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "getPlugins() error: " + e.getMessage());
        } finally {
            if ( cursor != null ) {
                cursor.close();
            }
            if ( client != null ) {
                client.release();
            }
        }
        return new Plugin[0];
    }
}
