package com.htc.lib2.opensense.test.social;

import com.htc.lib2.opensense.internal.SystemWrapper;
import com.htc.lib2.opensense.social.SocialContract;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SocialContractTestCase extends TestCase {

    public static final String SOCIALMANAGER_AUTHORITY = "com.htc.lib2.mock.opensense.social";

    static {
        SystemWrapper.setSocialManagerAuthority(SOCIALMANAGER_AUTHORITY);
    }

    public void testFieldAuthority() {
        SystemWrapper.setPluginManagerAuthority(SOCIALMANAGER_AUTHORITY);
        Assert.assertEquals(SOCIALMANAGER_AUTHORITY, SocialContract.CONTENT_AUTHORITY);
    }
}
