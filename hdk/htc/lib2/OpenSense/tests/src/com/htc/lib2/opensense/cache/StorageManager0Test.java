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

import android.os.Environment;
import android.os.StatFs;
import android.os.SystemProperties;

/**
 * To test Context is Null or not need Context
 */
@RunWith(RobolectricTestRunner.class)
@PrepareForTest({
        Environment.class,
        StatFs.class,
        SystemProperties.class
})
@Config(manifest=Config.NONE)
@SuppressStaticInitializationFor("android.os.Environment")
public class StorageManager0Test {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Test
    public void testGetAllStorages0() {
        mockSystemProperties0();
        mockEnvironment0();

        List<StorageManager.StorageInfo> storageInfos = StorageManager.getAllStorages(null, Environment.MEDIA_MOUNTED);
        Assert.assertNotNull(storageInfos);
    }

    @Test
    public void testGetAllStorages1() {
        mockSystemProperties0();
        mockEnvironment0();

        List<StorageManager.StorageInfo> storageInfos = StorageManager.getAllStorages(null, Environment.MEDIA_MOUNTED);
        Assert.assertNotNull(storageInfos);
    }

    @Test
    public void testGetAllStorages2() {
        mockSystemProperties0();
        mockEnvironment0();

        List<StorageManager.StorageInfo> storageInfos = StorageManager.getAllStorages(null, Environment.MEDIA_MOUNTED);
        int size = storageInfos.size();
        Assert.assertTrue(size == 0);
    }

    @Test
    public void testGetBlockSizeLong0() {
        mockSystemProperties0();
        mockEnvironment0();

        long result = StorageManager.getBlockSizeLong(null);
        Assert.assertTrue(result > 0);
    }

//    @Test
//    public void testGetBlockSizeLong1() throws Exception {
//        mockSystemProperties0();
//        mockEnvironment0();
//
//        long result = StorageManager.getBlockSizeLong("/");
//        Assert.assertTrue(result > 0);
//    }

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
}
