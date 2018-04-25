package com.htc.lib2.opensense.test.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.Cipher;

import org.nikkii.embedhttp.HttpServer;
import org.nikkii.embedhttp.handler.HttpStaticFileHandler;

import junit.framework.Assert;

import com.htc.lib2.opensense.cache.CacheManager;
import com.htc.lib2.opensense.cache.Download;
import com.htc.lib2.opensense.cache.DownloadCallback;
import com.htc.lib2.opensense.internal.SystemWrapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.util.Log;

public class CacheManagerTestCase extends AndroidTestCase {

    private static final int TEST_HTTP_SERVER_PORT = 8081;
    private static final String LOG_TAG = CacheManagerTestCase.class.getSimpleName();
//    private static final String REMOTE_SITE_URL_PREFIX = "http://10.116.66.18/~kenelin/";

    private static boolean sHasHttpServerInited = false;
    private static String sFileBaseUri = null;
    private static String sHttpBaseUri = null;

    @Override
    protected void setUp() throws Exception {
        if ( !sHasHttpServerInited ) {
            copyAssets(getContext());
            initHttpServer(getContext(), TEST_HTTP_SERVER_PORT);
            sFileBaseUri = getContext().getCacheDir().toURI().toString();
            sHttpBaseUri = "http://localhost:" + TEST_HTTP_SERVER_PORT + "/";
            sHasHttpServerInited = true;
        }
    }

    public void testA0() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        CacheManager cacheManager = CacheManager.init(getContext());
        String uri = null;
        cacheManager.downloadPhotoByUrl(uri, callback, null);
        signal.await();
        Assert.assertEquals(0, positiveCount.get());
    }

    public void testA1() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        CacheManager cacheManager = CacheManager.init(getContext());
        String uri = sFileBaseUri + "image.png";
        cacheManager.downloadPhotoByUrl(uri, callback, null);
        signal.await();
        Assert.assertEquals(1, positiveCount.get());
    }

    public void testA2() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        String[] uris = {
                sFileBaseUri + "00.png",
                sFileBaseUri + "01.png",
                sFileBaseUri + "02.png",
                sFileBaseUri + "03.png",
                sFileBaseUri + "04.png",
                sFileBaseUri + "05.png",
                sFileBaseUri + "06.png",
                sFileBaseUri + "07.png",
                sFileBaseUri + "08.png",
                sFileBaseUri + "09.png"
        };
        CountDownLatch signal = new CountDownLatch(uris.length);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        CacheManager cacheManager = CacheManager.init(getContext());
        long startTime = System.currentTimeMillis();
        for (String uri : uris) {
            cacheManager.downloadPhotoByUrl(uri, callback, null);
        }
        signal.await();
        long endTime = System.currentTimeMillis();
        Log.w(LOG_TAG, "testA2: " + (endTime - startTime));
        Assert.assertEquals(uris.length, positiveCount.get());
    }

    public void testA3() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        CacheManager cacheManager = CacheManager.init(getContext());
        String uri = sFileBaseUri + "image.png";
        Bundle data = new Bundle();
        data.putBoolean(Download.CHECK_ONLY, true);
        cacheManager.downloadPhotoByUrl(uri, callback, data);
        signal.await();
        Assert.assertEquals(1, positiveCount.get());
    }

    public void testA4() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        TestDownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        CacheManager cacheManager = CacheManager.init(getContext());
        String uri = sFileBaseUri + "image.dummy.png";
        Bundle data = new Bundle();
        data.putBoolean(Download.CHECK_ONLY, true);
        cacheManager.downloadPhotoByUrl(uri, callback, data);
        signal.await();
        Bundle dataOnError = callback.getDataOnError();
        boolean isCheckOnly = false;
        if ( dataOnError != null ) {
            isCheckOnly = dataOnError.getBoolean(Download.CHECK_ONLY);
        }
        Assert.assertEquals(0, positiveCount.get());
        Assert.assertEquals(true, isCheckOnly);
    }

    public void testA5() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        String[] uris = {
                sFileBaseUri + "00.png",
                sFileBaseUri + "01.png",
                sFileBaseUri + "02.png",
                sFileBaseUri + "03.png",
                sFileBaseUri + "04.png",
                sFileBaseUri + "05.png",
                sFileBaseUri + "06.png",
                sFileBaseUri + "07.png",
                sFileBaseUri + "08.png",
                sFileBaseUri + "09.png"
        };
        CountDownLatch signal = new CountDownLatch(uris.length);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        CacheManager cacheManager = CacheManager.init(getContext());
        long startTime = System.currentTimeMillis();
        for (String uri : uris) {
            Bundle data = new Bundle();
            data.putBoolean(Download.CHECK_ONLY, true);
            cacheManager.downloadPhotoByUrl(uri, callback, data);
        }
        signal.await();
        long endTime = System.currentTimeMillis();
        Log.w(LOG_TAG, "testA5: " + (endTime - startTime));
        Assert.assertEquals(uris.length, positiveCount.get());
    }

    public void testA6() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        String[] uris = {
                sFileBaseUri + "00.dummy.png",
                sFileBaseUri + "01.dummy.png",
                sFileBaseUri + "02.dummy.png",
                sFileBaseUri + "03.dummy.png",
                sFileBaseUri + "04.dummy.png",
                sFileBaseUri + "05.dummy.png",
                sFileBaseUri + "06.dummy.png",
                sFileBaseUri + "07.dummy.png",
                sFileBaseUri + "08.dummy.png",
                sFileBaseUri + "09.dummy.png"
        };
        CountDownLatch signal = new CountDownLatch(uris.length);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        TestDownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        CacheManager cacheManager = CacheManager.init(getContext());
        long startTime = System.currentTimeMillis();
        for (String uri : uris) {
            Bundle data = new Bundle();
            data.putBoolean(Download.CHECK_ONLY, true);
            cacheManager.downloadPhotoByUrl(uri, callback, data);
        }
        signal.await();
        Bundle dataOnError = callback.getDataOnError();
        boolean isCheckOnly = false;
        if ( dataOnError != null ) {
            isCheckOnly = dataOnError.getBoolean(Download.CHECK_ONLY);
        }
        long endTime = System.currentTimeMillis();
        Log.w(LOG_TAG, "testA6: " + (endTime - startTime));
        Assert.assertEquals(0, positiveCount.get());
        Assert.assertEquals(true, isCheckOnly);
    }

    public void testB1() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        CacheManager cacheManager = CacheManager.init(getContext());
        String uri = sHttpBaseUri + "image.png";
        cacheManager.downloadPhotoByUrl(uri, callback, null);
        signal.await();
        Assert.assertEquals(1, positiveCount.get());
    }

    public void testB1a() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        CacheManager cacheManager = CacheManager.init(getContext());
        String uri = "https://fbexternal-a.akamaihd.net/safe_image.php?d=AQAINdOZ_Am8UFsR&w=130&h=130&url=https%3A%2F%2Fdmxgmmqiylm9v.cloudfront.net%2Fbb6b83b9-4d74-428e-8abe-c7e0195339bd%2F1417514058340%2Fe2215f0c-c5d4-48dd-9633-f95e88df3df5.jpg%3FExpires%3D1977281192%26Signature%3DTQk9Y6qyr3y7LyhVZoR1t4HRF1nbQxpKiF67RWHTJGvltxfC14h1d66CbwvjE1xEbPgsk4addrjtQBunZDQ4e-SysVjJ9O9P9Mpr7S8FjaYUDmVauQiw9tl1E9dAr4EXKRR5Fs3cckREXGc0wEJO22-cDAs-ceoJpSBppB-NOLo_%26Key-Pair-Id%3DAPKAICXLZP27IUJJDALQ";
        cacheManager.downloadPhotoByUrl(uri, callback, null);
        signal.await();
        Assert.assertEquals(1, positiveCount.get());
    }

    public void testB2() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        String[] uris = {
                sHttpBaseUri + "00.png",
                sHttpBaseUri + "01.png",
                sHttpBaseUri + "02.png",
                sHttpBaseUri + "03.png",
                sHttpBaseUri + "04.png",
                sHttpBaseUri + "05.png",
                sHttpBaseUri + "06.png",
                sHttpBaseUri + "07.png",
                sHttpBaseUri + "08.png",
                sHttpBaseUri + "09.png"
        };
        CountDownLatch signal = new CountDownLatch(uris.length);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        CacheManager cacheManager = CacheManager.init(getContext());
        long startTime = System.currentTimeMillis();
        for (String uri : uris) {
            Bundle data = new Bundle();
            data.putBoolean(Download.FACE_DETECT, true);
            cacheManager.downloadPhotoByUrl(uri, callback, data);
        }
        signal.await();
        long endTime = System.currentTimeMillis();
        Log.w(LOG_TAG, "testB2: " + (endTime - startTime));
        Assert.assertEquals(uris.length, positiveCount.get());
    }

    public void testC1() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        CacheManager cacheManager = CacheManager.init(getContext());

        String plainFileUri = sFileBaseUri + "image.png";
        String uri = plainFileUri + ".enc";
        uri = "file+enc" + uri.substring("file".length());

        Bitmap bitmap = BitmapFactory.decodeFile(plainFileUri.substring("file:".length()));
        File file = new File(plainFileUri.substring("file:".length()) + ".enc");
        if ( !file.exists() ) {
            if ( !file.createNewFile() ) {
                throw new IOException("Cannot create file");
            }
        }

        OutputStream out = CacheManager.getFileOutputStream(
                getContext(),
                Cipher.ENCRYPT_MODE,
                file
        );
        if ( out != null || bitmap != null ) {
            bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    out
            );
            out.close();
        }

        cacheManager.downloadPhotoByUrl(uri, callback, null);
        signal.await();
        Assert.assertEquals(1, positiveCount.get());
    }

    public void testC2() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        String[] plainFileUris = {
                sFileBaseUri + "00.png",
                sFileBaseUri + "01.png",
//                fileBaseUri + "02.png",
                sFileBaseUri + "03.png",
                sFileBaseUri + "04.png",
//                fileBaseUri + "05.png",
                sFileBaseUri + "06.png",
                sFileBaseUri + "07.png",
//                fileBaseUri + "08.png",
                sFileBaseUri + "09.png"
        };
        String[] uris = {
                sFileBaseUri + "00.png" + ".enc",
                sFileBaseUri + "01.png" + ".enc",
//                fileBaseUri + "02.png" + ".enc",
                sFileBaseUri + "03.png" + ".enc",
                sFileBaseUri + "04.png" + ".enc",
//                fileBaseUri + "05.png" + ".enc",
                sFileBaseUri + "06.png" + ".enc",
                sFileBaseUri + "07.png" + ".enc",
//                fileBaseUri + "08.png" + ".enc",
                sFileBaseUri + "09.png" + ".enc"
        };

        CountDownLatch signal = new CountDownLatch(uris.length);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        CacheManager cacheManager = CacheManager.init(getContext());

        SharedPreferences preferences = getContext().getSharedPreferences("cipher", Context.MODE_PRIVATE);
        String newCipherDigest = cacheManager.getCipherDigest();
        String oldCipherDigest = "";
        if ( preferences != null ) {
            oldCipherDigest = preferences.getString("digest", "");
        }

        for (String plainFileUri : plainFileUris) {
            Bitmap bitmap = BitmapFactory.decodeFile(plainFileUri.substring("file:".length()));
            File file = new File(plainFileUri.substring("file:".length()) + ".enc");
            if ( !file.exists() ) {
                if ( !file.createNewFile() ) {
                    throw new IOException("Cannot create file");
                }
            }

            // target file should be existed
            if ( !oldCipherDigest.equals(newCipherDigest) ) {
                OutputStream out = CacheManager.getFileOutputStream(
                        getContext(),
                        Cipher.ENCRYPT_MODE,
                        file
                );
                if ( out != null && bitmap != null ) {
                    Log.w(LOG_TAG, "compress to " + file.getAbsolutePath());
                    bitmap.compress(
                            Bitmap.CompressFormat.PNG,
                            100,
                            out
                    );
                    out.close();
                }
                if ( preferences != null ) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("digest", newCipherDigest).apply();
                }
            } else {
                Log.e(LOG_TAG, "skip encoding file: " + file.getAbsolutePath());
            }
            bitmap = null;
        }

        long startTime = System.currentTimeMillis();
        for (String uri : uris) {
            uri = "file+enc" + uri.substring("file".length());
            Log.e(LOG_TAG, "uri: " + uri);
            cacheManager.downloadPhotoByUrl(uri, callback, null);
        }
        signal.await();
        long endTime = System.currentTimeMillis();
        Log.w(LOG_TAG, "testC2: " + (endTime - startTime));
        Assert.assertEquals(uris.length, positiveCount.get());
    }

    public void testD1() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        CacheManager cacheManager = CacheManager.init(getContext());
        String uri = sFileBaseUri + "text.png";
        cacheManager.downloadPhotoByUrl(uri, callback, null);
        signal.await();
        Assert.assertEquals(0, positiveCount.get());
    }

    public void testE1() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        CacheManager cacheManager = CacheManager.init(getContext());
        String uri = sHttpBaseUri + "image11.png";
        Bundle data = new Bundle();
        data.putStringArray(Download.HTTP_HEADERS, null);
        cacheManager.downloadPhotoByUrl(uri, callback, data);
        signal.await();
        Assert.assertEquals(1, positiveCount.get());
    }

    public void testE2() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        CacheManager cacheManager = CacheManager.init(getContext());
        String uri = sHttpBaseUri + "image12.png";
        Bundle data = new Bundle();
        Bundle headers = new Bundle();
        headers.putString("X-Htc-Session-Token", "12345");
        headers.putString("X-Htc-Application-Id", "asdfg");
        data.putBundle(Download.HTTP_HEADERS, headers);
        cacheManager.downloadPhotoByUrl(uri, callback, data);
        signal.await();
        Assert.assertEquals(1, positiveCount.get());
    }

    public void testE3() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        CacheManager cacheManager = CacheManager.init(getContext());
        String uri = sHttpBaseUri + "image13.png";
        Bundle data = new Bundle();
        Bundle headers = new Bundle();
        headers.putString(null, "abc");
        headers.putString("X-Htc-Session-Token", "34567");
        headers.putString("X-Htc-Application-Id", "poiiy");
        data.putBundle(Download.HTTP_HEADERS, headers);
        data.putString(Download.HTTP_HEADER_AUTHORIZATION, "Bearer");
        data.putBoolean(Download.FACE_DETECT, true);
        cacheManager.downloadPhotoByUrl(uri, callback, data);
        signal.await();
        Assert.assertEquals(1, positiveCount.get());
    }

//    public void testE4() throws Throwable {
//        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
//        CountDownLatch signal = new CountDownLatch(1);
//        Map<String, Boolean> result = new HashMap<String, Boolean>();
//        AtomicInteger positiveCount = new AtomicInteger();
//        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
//        CacheManager cacheManager = CacheManager.init(getContext());
//        // String uri = sHttpBaseUri + "image14.png" + ".redirect";
//        // HttpURLConnection.setFollowRedirects(false);
//        String uri = REMOTE_SITE_URL_PREFIX + "test3/image2.png.r5";
//        Bundle data = new Bundle();
//        Bundle headers = new Bundle();
//        headers.putString(null, "abc");
//        headers.putString("X-Htc-Session-Token", "34567");
//        headers.putString("X-Htc-Application-Id", "poiiy");
//        data.putBundle(Download.HTTP_HEADERS, headers);
//        data.putString(Download.HTTP_HEADER_AUTHORIZATION, "Bearer");
//        cacheManager.downloadPhotoByUrl(uri, callback, data);
//        signal.await();
//        Assert.assertEquals(1, positiveCount.get());
//    }

//    public void testE() throws Throwable {
//        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
//        int counter = 100;
//        CountDownLatch signal = new CountDownLatch(counter);
//        Map<String, Boolean> result = new HashMap<String, Boolean>();
//        AtomicInteger positiveCount = new AtomicInteger();
//        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
//        CacheManager cacheManager = CacheManager.init(getContext());
//        // String uri = "http://www.htc.com/";
//        String uri = REMOTE_SITE_URL_PREFIX + "test2/100.jpg";
//        String base = REMOTE_SITE_URL_PREFIX + "test2/";
//        // String base = "http://www.htc.com/";
//        for (int i = 0; i < counter; i++) {
//            String a = base + (i + 100000) + ".jpg";
//            // Log.e(LOG_TAG, "uri-a: " + a);
//            cacheManager.downloadPhotoByUrl(uri, callback, null);
//        }
//        signal.await();
//        Assert.assertEquals(100, positiveCount.get());
//    }

    private static void copyAssets(Context context) {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if ( files != null ) {
            for (String file : files) {
                if ( "images".equals(file) || "sounds".equals(file) || "webkit".equals(file) ) {
                    continue;
                }
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(file);
                    File outFile = new File(context.getCacheDir(), file);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ( (read = in.read(buffer)) != -1 ){
            out.write(buffer, 0, read);
        }
    }

    private static void initHttpServer(Context context, int port) throws IOException {
        File cacheDir = context.getCacheDir();
        HttpServer server = new HttpServer();
        server.addRequestHandler(new HttpStaticFileHandler(cacheDir));
        server.bind(port);
        server.start();
    }

    private static class TestDownloadCallback implements DownloadCallback {

        private CountDownLatch mSignal = null;
        private Map<String, Boolean> mResult;
        private AtomicInteger mPositiveCount = null;
        private Bundle mDataOnError = null;

        public TestDownloadCallback(CountDownLatch signal, Map<String, Boolean> result, AtomicInteger positiveCount) {
            mSignal = signal;
            mResult = result;
            mPositiveCount = positiveCount;
        }

        @Override
        public void onDownloadSuccess(Uri uri, Bundle data) {
            Log.w(LOG_TAG, "uri: " + uri.toString());
            mPositiveCount.incrementAndGet();
            mResult.put("result", true);
            mSignal.countDown();
        }

        @Override
        public void onDownloadError(Exception e, Bundle data) {
            mResult.put("result", false);
            mDataOnError = data;
            mSignal.countDown();
        }

        public Bundle getDataOnError() {
            return mDataOnError;
        }
    }
}
