package com.htc.lib2.opensense.plugin;

import android.content.ComponentName;

/**
 * A generic class for all types of plugins.
 * 
 * @hide
 */
public class Plugin {

    private int mId;
    private Feature mFeature;
    private ComponentName mComponentName;
    private int mVersion;
    private String mDescription;
    private String mPluginMeta = null;

    /**
     * Used by PluginRegistryHelper().getPlugins()
     * 
     * @hide
     */
    public Plugin() {
        this(-1, null, null, 0, null, null);
    }

    /**
     * Used by PluginRegistryHelper().getPlugins()
     * 
     * @hide
     */
    public Plugin(Feature feature) {
        this(-1, feature, null, 0, null, null);
    }

    /**
     * Used by PluginRegistryHelper().getPlugins()
     * 
     * @hide
     */
    public Plugin(int id, Feature feature, ComponentName componentName,
            int version, String description, String pluginMeta) {
        mId = id;
        mFeature = feature;
        mComponentName = componentName;
        mVersion = version;
        mDescription = description;
        mPluginMeta = pluginMeta;
    }

    /**
     * Gets the plugin's ID
     * 
     * @return the plugin's ID
     * 
     * @hide
     */
    public int getId() {
        return mId;
    }

    /**
     * Gets the plugin's {@link Feature}
     * 
     * @return the plugin's {@link Feature}
     * 
     * @hide
     */
    public Feature getFeature() {
        return mFeature;
    }

    /**
     * Gets the plugin's {@link ComponentName}
     * 
     * @return the plugin's {@link ComponentName}
     * 
     * @hide
     */
    public ComponentName getComponentName() {
        return mComponentName;
    }

    /**
     * Gets the plugin's version
     * 
     * @return the plugin's version
     * 
     * @hide
     */
    public int getVersion() {
        return mVersion;
    }

    /**
     * Gets the plugin's description
     * 
     * @return the plugin's description
     * 
     * @hide
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Gest the plugin's meta
     * 
     * @return the plugin's meta
     * 
     * @hide
     */
    public String getPluginMeta() {
        return mPluginMeta;
    }

    /**
     * @hide
     */
    public void setId(int id) {
        mId = id;
    }

    /**
     * @hide
     */
    public void setFeature(Feature feature) {
        mFeature = feature;
    }

    /**
     * @hide
     */
    public void setComponentName(ComponentName componentName) {
        mComponentName = componentName;
    }

    /**
     * @hide
     */
    public void setVersion(int version) {
        mVersion = version;
    }

    /**
     * @hide
     */
    public void setDescription(String description) {
        mDescription = description;
    }

    /**
     * @hide
     */
    public void setPluginMeta(String pluginMeta) {
        mPluginMeta = pluginMeta;
    }
}
