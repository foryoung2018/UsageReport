package com.htc.lib2.opensense.plugin;

import com.htc.lib2.opensense.internal.SystemWrapper;

/**
 * An interface that containing constants for PluginManager
 * 
 * @hide
 */
public interface PluginConstants {

    /**
     * PluginManager's Authority
     * 
     * @hide
     */
    String AUTHORITY = SystemWrapper.getPluginManagerAuthority() /* "com.htc.opensense.plugin" */;

    /**
     * PluginManager's Authority
     *
     * @hide
     */
    String HSP_AUTHORITY = SystemWrapper.getPluginManagerPackageName() /* "com.htc.opensense.plugin" */;

    // Column names
    /**
     * Column name: _id
     * 
     * @hide
     */
    String _ID = "_id";

    /**
     * Column name: meta_name
     * 
     * @hide
     */
    String COLUMN_META_NAME = "meta_name";

    /**
     * Column name: feature_type
     * 
     * @hide
     */
    String COLUMN_FEATURE_TYPE = "feature_type";

    /**
     * Colume name: plugin_class
     * 
     * @hide
     */
    String COLUMN_PLUGIN_CLASS = "plugin_class";

    /**
     * Column name: plugin_meta
     * 
     * @hide
     */
    String COLUMN_PLUGIN_META = "plugin_meta";

    /**
     * Column name: package_id
     * 
     * @hide
     */
    String COLUMN_PACKAGE_ID = "package_id";

    /**
     * Column name: package
     * 
     * @hide
     */
    String COLUMN_PACKAGE = "package";

    /**
     * Column name: description
     * 
     * @hide
     */
    String COLUMN_DESCRIPTION = "description";

    /**
     * Column name: icon
     * 
     * @hide
     */
    String COLUMN_ICON = "icon";

    /**
     * Column name: certificate
     * 
     * @hide
     */
    String COLUMN_CERTIFICATE = "certificate";

    /**
     * Column name: feature_id
     * 
     * @hide
     */
    String COLUMN_FEATURE_ID = "feature_id";

    /**
     * Column name: feature
     * 
     * @hide
     */
    String COLUMN_FEATURE = "feature";

    /**
     * Column name: version
     * 
     * @hide
     */
    String COLUMN_VERSION = "version";

    /**
     * Column name: type
     * 
     * @hide
     */
    String COLUMN_META_TYPE = "type";

    /**
     * Column name: value
     * 
     * @hide
     */
    String COLUMN_META_VALUE = "value";

    /**
     * Column name: removed
     * 
     * @hide
     */
    String COLUMN_PLUGIN_REMOVED = "removed";

    // Table names
    /**
     * Table name: meta_data
     * 
     * @hide
     */
    String METADATA_TB = "meta_data";

    /**
     * Table name: features
     * 
     * @hide
     */
    String FEATURE_TB = "features";

    /**
     * Table name: plugin
     * 
     * @hide
     */
    String PLUGIN_TB = "plugin";

    /**
     * Table name: plugin_pkg
     * 
     * @hide
     */
    String PLUGIN_PKG_TB = "plugin_pkg";
}
