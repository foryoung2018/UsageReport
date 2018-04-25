package com.htc.lib2.opensense.test.plugin;

import android.content.ComponentName;

import com.htc.lib2.opensense.plugin.Feature;
import com.htc.lib2.opensense.plugin.Plugin;

import junit.framework.Assert;
import junit.framework.TestCase;

public class PluginTestCase extends TestCase {

    private static final int FEATURE_ID = 33;
    private static final int FEATURE_VERSION = 5;
    private static final int PLUGIN_ID = 45;
    private static final int PLUGIN_VERSION = 7;
    private static final String FEATURE_NAME = "featureName";
    private static final String FEATURE_TYPE = "featureType";
    private static final String PLUGIN_DESCRIPTION = "Plugin description";
    private static final String PLUGIN_META = "Plugin Metadata";
    private static final String COMPONENT_PACKAGE = "com.htc.test";
    private static final String COMPONENT_CLASS = "com.htc.test.MainActivity";

    public void testConstructor1() throws Throwable {
        Plugin plugin = new Plugin();
        Assert.assertEquals(-1, plugin.getId());
        Assert.assertEquals(null, plugin.getFeature());
        Assert.assertEquals(null, plugin.getComponentName());
        Assert.assertEquals(0, plugin.getVersion());
        Assert.assertEquals(null, plugin.getDescription());
        Assert.assertEquals(null, plugin.getPluginMeta());
    }

    public void testConstructor2() throws Throwable {
        Feature feature1 = new Feature(FEATURE_ID, FEATURE_VERSION, FEATURE_NAME, FEATURE_TYPE);
        Feature feature2 = new Feature(FEATURE_ID, FEATURE_VERSION, FEATURE_NAME, FEATURE_TYPE);
        Plugin plugin = new Plugin(feature1);
        Assert.assertEquals(-1, plugin.getId());
        Assert.assertEquals(feature2, plugin.getFeature());
        Assert.assertEquals(null, plugin.getComponentName());
        Assert.assertEquals(0, plugin.getVersion());
        Assert.assertEquals(null, plugin.getDescription());
        Assert.assertEquals(null, plugin.getPluginMeta());
    }

    public void testConstructor3() throws Throwable {
        Feature feature1 = new Feature(FEATURE_ID, FEATURE_VERSION, FEATURE_NAME, FEATURE_TYPE);
        Feature feature2 = new Feature(FEATURE_ID, FEATURE_VERSION, FEATURE_NAME, FEATURE_TYPE);
        ComponentName componentName1 = new ComponentName(COMPONENT_PACKAGE, COMPONENT_CLASS);
        ComponentName componentName2 = new ComponentName(COMPONENT_PACKAGE, COMPONENT_CLASS);
        Plugin plugin = new Plugin(PLUGIN_ID, feature1, componentName1, PLUGIN_VERSION, PLUGIN_DESCRIPTION, PLUGIN_META);
        Assert.assertEquals(PLUGIN_ID, plugin.getId());
        Assert.assertEquals(feature2, plugin.getFeature());
        Assert.assertEquals(componentName2, plugin.getComponentName());
        Assert.assertEquals(PLUGIN_VERSION, plugin.getVersion());
        Assert.assertEquals(PLUGIN_DESCRIPTION, plugin.getDescription());
        Assert.assertEquals(PLUGIN_META, plugin.getPluginMeta());
    }

    public void testSetId() throws Throwable {
        Plugin plugin = new Plugin();
        plugin.setId(PLUGIN_ID);
        Assert.assertEquals(PLUGIN_ID, plugin.getId());
        Assert.assertEquals(null, plugin.getFeature());
        Assert.assertEquals(null, plugin.getComponentName());
        Assert.assertEquals(0, plugin.getVersion());
        Assert.assertEquals(null, plugin.getDescription());
        Assert.assertEquals(null, plugin.getPluginMeta());
    }

    public void testSetFeature() throws Throwable {
        Feature feature1 = new Feature(FEATURE_ID, FEATURE_VERSION, FEATURE_NAME, FEATURE_TYPE);
        Feature feature2 = new Feature(FEATURE_ID, FEATURE_VERSION, FEATURE_NAME, FEATURE_TYPE);
        Plugin plugin = new Plugin();
        plugin.setFeature(feature1);
        Assert.assertEquals(-1, plugin.getId());
        Assert.assertEquals(feature2, plugin.getFeature());
        Assert.assertEquals(null, plugin.getComponentName());
        Assert.assertEquals(0, plugin.getVersion());
        Assert.assertEquals(null, plugin.getDescription());
        Assert.assertEquals(null, plugin.getPluginMeta());
    }

    public void testSetComponentName() throws Throwable {
        ComponentName componentName1 = new ComponentName(COMPONENT_PACKAGE, COMPONENT_CLASS);
        ComponentName componentName2 = new ComponentName(COMPONENT_PACKAGE, COMPONENT_CLASS);
        Plugin plugin = new Plugin();
        plugin.setComponentName(componentName1);
        Assert.assertEquals(-1, plugin.getId());
        Assert.assertEquals(null, plugin.getFeature());
        Assert.assertEquals(componentName2, plugin.getComponentName());
        Assert.assertEquals(0, plugin.getVersion());
        Assert.assertEquals(null, plugin.getDescription());
        Assert.assertEquals(null, plugin.getPluginMeta());
    }

    public void testSetVersion() throws Throwable {
        Plugin plugin = new Plugin();
        plugin.setVersion(PLUGIN_VERSION);
        Assert.assertEquals(-1, plugin.getId());
        Assert.assertEquals(null, plugin.getFeature());
        Assert.assertEquals(null, plugin.getComponentName());
        Assert.assertEquals(PLUGIN_VERSION, plugin.getVersion());
        Assert.assertEquals(null, plugin.getDescription());
        Assert.assertEquals(null, plugin.getPluginMeta());
    }

    public void testSetDescription() throws Throwable {
        Plugin plugin = new Plugin();
        plugin.setDescription(PLUGIN_DESCRIPTION);
        Assert.assertEquals(-1, plugin.getId());
        Assert.assertEquals(null, plugin.getFeature());
        Assert.assertEquals(null, plugin.getComponentName());
        Assert.assertEquals(0, plugin.getVersion());
        Assert.assertEquals(PLUGIN_DESCRIPTION, plugin.getDescription());
        Assert.assertEquals(null, plugin.getPluginMeta());
    }

    public void testSetPluginMeta() throws Throwable {
        Plugin plugin = new Plugin();
        plugin.setPluginMeta(PLUGIN_META);
        Assert.assertEquals(-1, plugin.getId());
        Assert.assertEquals(null, plugin.getFeature());
        Assert.assertEquals(null, plugin.getComponentName());
        Assert.assertEquals(0, plugin.getVersion());
        Assert.assertEquals(null, plugin.getDescription());
        Assert.assertEquals(PLUGIN_META, plugin.getPluginMeta());
    }

    public void testSetAll1() throws Throwable {
        Feature feature1 = new Feature(FEATURE_ID, FEATURE_VERSION, FEATURE_NAME, FEATURE_TYPE);
        Feature feature2 = new Feature(FEATURE_ID, FEATURE_VERSION, FEATURE_NAME, FEATURE_TYPE);
        ComponentName componentName1 = new ComponentName(COMPONENT_PACKAGE, COMPONENT_CLASS);
        ComponentName componentName2 = new ComponentName(COMPONENT_PACKAGE, COMPONENT_CLASS);
        Plugin plugin = new Plugin();
        plugin.setId(PLUGIN_ID);
        plugin.setFeature(feature1);
        plugin.setComponentName(componentName1);
        plugin.setVersion(PLUGIN_VERSION);
        plugin.setDescription(PLUGIN_DESCRIPTION);
        plugin.setPluginMeta(PLUGIN_META);
        Assert.assertEquals(PLUGIN_ID, plugin.getId());
        Assert.assertEquals(feature2, plugin.getFeature());
        Assert.assertEquals(componentName2, plugin.getComponentName());
        Assert.assertEquals(PLUGIN_VERSION, plugin.getVersion());
        Assert.assertEquals(PLUGIN_DESCRIPTION, plugin.getDescription());
        Assert.assertEquals(PLUGIN_META, plugin.getPluginMeta());
    }

    public void testSetAll2() throws Throwable {
        Feature feature1 = new Feature(FEATURE_ID, FEATURE_VERSION, FEATURE_NAME, FEATURE_TYPE);
        Feature feature2 = new Feature(FEATURE_ID, FEATURE_VERSION, FEATURE_NAME, FEATURE_TYPE);
        ComponentName componentName1 = new ComponentName(COMPONENT_PACKAGE, COMPONENT_CLASS);
        ComponentName componentName2 = new ComponentName(COMPONENT_PACKAGE, COMPONENT_CLASS);
        Plugin plugin = new Plugin(
                new Feature(9, 4, "123", "456")
        );
        plugin.setId(PLUGIN_ID);
        plugin.setFeature(feature1);
        plugin.setComponentName(componentName1);
        plugin.setVersion(PLUGIN_VERSION);
        plugin.setDescription(PLUGIN_DESCRIPTION);
        plugin.setPluginMeta(PLUGIN_META);
        Assert.assertEquals(PLUGIN_ID, plugin.getId());
        Assert.assertEquals(feature2, plugin.getFeature());
        Assert.assertEquals(componentName2, plugin.getComponentName());
        Assert.assertEquals(PLUGIN_VERSION, plugin.getVersion());
        Assert.assertEquals(PLUGIN_DESCRIPTION, plugin.getDescription());
        Assert.assertEquals(PLUGIN_META, plugin.getPluginMeta());
    }

    public void testSetAll3() throws Throwable {
        Feature feature1 = new Feature(FEATURE_ID, FEATURE_VERSION, FEATURE_NAME, FEATURE_TYPE);
        Feature feature2 = new Feature(FEATURE_ID, FEATURE_VERSION, FEATURE_NAME, FEATURE_TYPE);
        ComponentName componentName1 = new ComponentName(COMPONENT_PACKAGE, COMPONENT_CLASS);
        ComponentName componentName2 = new ComponentName(COMPONENT_PACKAGE, COMPONENT_CLASS);
        Plugin plugin = new Plugin(
                8,
                new Feature(9, 4, "123", "456"),
                new ComponentName("org.test", "org.test.Main"),
                11,
                "Something",
                "Wrong"
        );
        plugin.setId(PLUGIN_ID);
        plugin.setFeature(feature1);
        plugin.setComponentName(componentName1);
        plugin.setVersion(PLUGIN_VERSION);
        plugin.setDescription(PLUGIN_DESCRIPTION);
        plugin.setPluginMeta(PLUGIN_META);
        Assert.assertEquals(PLUGIN_ID, plugin.getId());
        Assert.assertEquals(feature2, plugin.getFeature());
        Assert.assertEquals(componentName2, plugin.getComponentName());
        Assert.assertEquals(PLUGIN_VERSION, plugin.getVersion());
        Assert.assertEquals(PLUGIN_DESCRIPTION, plugin.getDescription());
        Assert.assertEquals(PLUGIN_META, plugin.getPluginMeta());
    }
}
