package com.htc.lib2.opensense.test.plugin;

import java.util.ArrayList;

import junit.framework.Assert;

import com.htc.lib2.opensense.internal.SystemWrapper;
import com.htc.lib2.opensense.plugin.Plugin;
import com.htc.lib2.opensense.plugin.PluginRegistryHelper;

import android.content.ComponentName;
import android.test.AndroidTestCase;

public class PluginRegistryHelperTestCase extends AndroidTestCase {

    private static final String PLUGINMANAGER_AUTHORITY = PluginConstantsTestCase.PLUGINMANAGER_AUTHORITY;
    private static final String FEATURE_NAME_1 = "TestDataPlugin1";
    private static final String FEATURE_NAME_2 = "TestDataPlugin2";

    public void testGetPluginComponents1() {
        SystemWrapper.setPluginManagerAuthority(PLUGINMANAGER_AUTHORITY);
        ComponentName[] names = PluginRegistryHelper.getPluginComponents(getContext(), FEATURE_NAME_1);
        Assert.assertEquals(0, names.length);
    }

    public void testGetPluginComponents2() {
        SystemWrapper.setPluginManagerAuthority(PLUGINMANAGER_AUTHORITY);
        ComponentName[] names = PluginRegistryHelper.getPluginComponents(getContext(), FEATURE_NAME_2);
        Assert.assertEquals(1, names.length);
    }

    public void testGetOpenSenseServices1() {
        SystemWrapper.setPluginManagerAuthority(PLUGINMANAGER_AUTHORITY);
        ArrayList<String> names = PluginRegistryHelper.getOpenSenseServices(getContext(), FEATURE_NAME_1);
        Assert.assertEquals(0, names.size());
    }

    public void testGetOpenSenseServices2() {
        SystemWrapper.setPluginManagerAuthority(PLUGINMANAGER_AUTHORITY);
        ArrayList<String> names = PluginRegistryHelper.getOpenSenseServices(getContext(), FEATURE_NAME_2);
        Assert.assertEquals(1, names.size());
    }

    public void testGetPlugins1() {
        SystemWrapper.setPluginManagerAuthority(PLUGINMANAGER_AUTHORITY);
        Plugin[] result = PluginRegistryHelper.getPlugins(getContext(), FEATURE_NAME_1);
        Assert.assertEquals(0, result.length);
    }

    public void testGetPlugins2() {
        SystemWrapper.setPluginManagerAuthority(PLUGINMANAGER_AUTHORITY);
        Plugin[] result = PluginRegistryHelper.getPlugins(getContext(), FEATURE_NAME_2);
        Assert.assertEquals(1, result.length);
    }
}
