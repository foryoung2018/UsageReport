package com.htc.lib2.opensense.cache;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemProperties;

/**
 * To test Context is Not Null
 */
@RunWith(RobolectricTestRunner.class)
@PrepareForTest({
        Context.class,
        Environment.class,
        StatFs.class,
        SystemProperties.class
})
@Config(manifest=Config.NONE)
@SuppressStaticInitializationFor("android.os.Environment")
public class StorageManager1Test {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Test
    public void testGetAllStorages1() {
        mockSystemProperties0();
        mockEnvironment0();
        Context context = getMockContext0();

        List<StorageManager.StorageInfo> storageInfos = StorageManager.getAllStorages(context, Environment.MEDIA_MOUNTED);
        Assert.assertNotNull(storageInfos);
    }

    @Test
    public void testGetAllStorages2() {
        mockSystemProperties0();
        mockEnvironment0();
        Context context = getMockContext0();

        List<StorageManager.StorageInfo> storageInfos = StorageManager.getAllStorages(context, Environment.MEDIA_MOUNTED);
        int size = storageInfos.size();
        Assert.assertTrue(size > 0);
    }

    /**
     * Mock all SystemProperties values as empty
     */
    public static void mockSystemProperties0() {
        PowerMockito.mockStatic(SystemProperties.class);
        PowerMockito.when(SystemProperties.get(Mockito.anyString(), Mockito.anyString())).thenReturn("");
    }

    /**
     * Mock Environment
     */
    public static void mockEnvironment0() {
        PowerMockito.mockStatic(Environment.class);
        PowerMockito.when(Environment.getExternalStorageDirectory()).thenReturn(new File("/"));
    }

    /**
     * Mock Context
     * <ul>
     * <li>Context.getApplicationContext() : <code>Context</code></li>
     * </ul>
     */
    public static Context getMockContext0() {
        Context context = PowerMockito.mock(Context.class);
        PowerMockito.when(context.getApplicationContext()).thenReturn(context);
        return context;
    }
}
