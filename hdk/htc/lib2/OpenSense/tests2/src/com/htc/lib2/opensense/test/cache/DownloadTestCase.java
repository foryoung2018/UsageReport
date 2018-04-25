package com.htc.lib2.opensense.test.cache;

import com.htc.lib2.opensense.cache.Download;
import com.htc.lib2.opensense.internal.SystemWrapper;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DownloadTestCase extends TestCase {

    public static final String CACHEMANAGER_AUTHORITY = "com.htc.lib2.mock.opensense.cachemanager";
//    public static final String CACHEMANAGER_AUTHORITY = "com.htc.sense.hsp.opensense.cachemanager";

    static {
        SystemWrapper.setPluginManagerAuthority(CACHEMANAGER_AUTHORITY);
    }

    public void testFieldAuthority() throws Throwable {
        SystemWrapper.setPluginManagerAuthority(CACHEMANAGER_AUTHORITY);
        Assert.assertEquals(CACHEMANAGER_AUTHORITY, Download.AUTHORITY);
    }
}
