package com.htc.lib2.opensense.test.plugin;

import com.htc.lib2.opensense.internal.SystemWrapper;
import com.htc.lib2.opensense.plugin.PluginConstants;

import junit.framework.Assert;
import junit.framework.TestCase;

public class PluginConstantsTestCase extends TestCase {

    public static final String PLUGINMANAGER_AUTHORITY = "com.htc.lib2.mock.opensense.plugin";

    static {
        SystemWrapper.setPluginManagerAuthority(PLUGINMANAGER_AUTHORITY);
    }

    public void testFieldAuthority() {
        SystemWrapper.setPluginManagerAuthority(PLUGINMANAGER_AUTHORITY);
        Assert.assertEquals(PLUGINMANAGER_AUTHORITY, PluginConstants.AUTHORITY);
    }
}
