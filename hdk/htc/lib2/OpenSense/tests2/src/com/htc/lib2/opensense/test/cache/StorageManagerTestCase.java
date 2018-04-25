package com.htc.lib2.opensense.test.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import android.content.Context;
import android.test.AndroidTestCase;

import com.htc.lib2.opensense.cache.StorageManager;
import com.htc.lib2.opensense.internal.SystemWrapper.Environment;

public class StorageManagerTestCase extends AndroidTestCase {

    public void testGetBlockSizeLong1() throws Throwable {
        long result = StorageManager.getBlockSizeLong(null);
        Assert.assertTrue(result > 0);
    }

    public void testGetBlockSizeLong2() throws Throwable {
        long result = StorageManager.getBlockSizeLong(new File("/").getAbsolutePath());
        Assert.assertTrue(result > 0);
    }

    public void testGetAllStorages1() throws Throwable {
        List<StorageManager.StorageInfo> result = StorageManager.getAllStorages(null, Environment.MEDIA_MOUNTED);
        Assert.assertNotNull(result);
    }

    public void testGetAllStorages2() throws Throwable {
        List<StorageManager.StorageInfo> storageInfos = StorageManager.getAllStorages(null, Environment.MEDIA_MOUNTED);
        int size = storageInfos.size();
        Assert.assertTrue(size > 0);
    }

    public void testGetCacheDir1() throws Throwable {
        String result = StorageManager.getCacheDir((Context) null);
        Assert.assertNotNull(result);
    }

    public void testGetCacheDir2() throws Throwable {
        String result = StorageManager.getCacheDir((StorageManager.StorageInfo) null);
        Assert.assertNull(result);
    }

    public void testGetCacheDir3() throws Throwable {
        List<StorageManager.StorageInfo> storageInfos = StorageManager.getAllStorages(null, Environment.MEDIA_MOUNTED);
        String result = StorageManager.getCacheDir(storageInfos.get(0));
        Assert.assertNotNull(result);
    }

    public void testPrepareCacheDir1() throws Throwable {
        List<StorageManager.StorageInfo> storageInfos = null;
        boolean result = StorageManager.prepareCacheDir(storageInfos);
        Assert.assertEquals(false, result);
    }

    public void testPrepareCacheDir2() throws Throwable {
        List<StorageManager.StorageInfo> storageInfos = new ArrayList<StorageManager.StorageInfo>();
        boolean result = StorageManager.prepareCacheDir(storageInfos);
        Assert.assertEquals(false, result);
    }

    public void testPrepareCacheDir3() throws Throwable {
        List<StorageManager.StorageInfo> storageInfos = StorageManager.getAllStorages(null, Environment.MEDIA_MOUNTED);
        boolean result = StorageManager.prepareCacheDir(storageInfos);
        Assert.assertEquals(true, result);
    }
}
