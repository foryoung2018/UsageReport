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

import com.htc.lib2.opensense.cache.Download;
import com.htc.lib2.opensense.cache.StorageManager;
import com.htc.lib2.opensense.cache.TaskManager;
import com.htc.lib2.opensense.cache.TaskManager.Task;
import com.htc.lib2.opensense.cache.TaskManager.TaskCallback;
import com.htc.lib2.opensense.internal.SystemWrapper;

import junit.framework.Assert;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.util.Log;

public class TaskManagerTestCase extends AndroidTestCase {

    private static final int TEST_HTTP_SERVER_PORT = 8082;
    private static final String LOG_TAG = TaskManagerTestCase.class.getSimpleName();

    private static boolean sHasHttpServerInited = false;
    private static int sTaskId1 = 0;
    private static int sTaskId2 = 0;
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
//        String dummyFileUri = sFileBaseUri + "dummy01.png";
//        File file = new File(dummyFileUri.substring("file:".length()) + ".enc");
//        if ( !file.exists() ) {
//            if ( !file.createNewFile() ) {
//                throw new IOException("Cannot create file");
//            }
//        }
        String originalFileUri = sFileBaseUri + "text02.png";
        boolean result = TaskManager.isValidImage(getContext(), Uri.parse(originalFileUri));
        Assert.assertEquals(false, result);
    }

    public void testGetUriQueryParameter1() throws Throwable {
        String result = TaskManager.getUriQueryParameter(null, null);
        Assert.assertNull(result);
    }

    public void testGetUriQueryParameter2() throws Throwable {
        String result = TaskManager.getUriQueryParameter(null, "key");
        Assert.assertNull(result);
    }

    public void testGetUriQueryParameter3() throws Throwable {
        Uri uri = Uri.parse("http://localhost?key=123");
        String result = TaskManager.getUriQueryParameter(uri, null);
        Assert.assertNull(result);
    }

    public void testGetUriQueryParameter4() throws Throwable {
        Uri uri = Uri.parse("http://localhost?key=123");
        String result = TaskManager.getUriQueryParameter(uri, "");
        Assert.assertNull(result);
    }

    public void testGetUriQueryParameter5() throws Throwable {
        Uri uri = Uri.parse("http://localhost?key=123");
        String result = TaskManager.getUriQueryParameter(uri, "key");
        Assert.assertEquals("123", result);
    }

    public void testGetParamAoi1() throws Throwable {
        String result = TaskManager.getParamAoi(null);
        Assert.assertNull(result);
    }

    public void testGetParamAoi2() throws Throwable {
        Uri uri = Uri.parse("http://localhost?key=123&AOI=456&key2=789");
        String result = TaskManager.getParamAoi(uri);
        Assert.assertEquals("456", result);
    }

    public void testGetParamWidthHeight1() throws Throwable {
        String result = TaskManager.getParamWidthHeight(null);
        Assert.assertNull(result);
    }

    public void testGetParamWidthHeight2() throws Throwable {
        Uri uri = Uri.parse("http://localhost?key=123&width=456&key2=789");
        String result = TaskManager.getParamWidthHeight(uri);
        Assert.assertEquals("width=456", result);
    }

    public void testGetParamWidthHeight3() throws Throwable {
        Uri uri = Uri.parse("http://localhost?key=123&width=456&height=345&key2=789");
        String result = TaskManager.getParamWidthHeight(uri);
        Assert.assertEquals("width=456&height=345", result);
    }

    public void testGetParamWidthHeight4() throws Throwable {
        Uri uri = Uri.parse("http://localhost?key=123&height=345&width=456&key2=789");
        String result = TaskManager.getParamWidthHeight(uri);
        Assert.assertEquals("width=456&height=345", result);
    }

    public void testGetParamWidthHeight5() throws Throwable {
        Uri uri = Uri.parse("http://localhost?key=123&height=345&key2=789");
        String result = TaskManager.getParamWidthHeight(uri);
        Assert.assertNull(result);
    }

    public void testTask1() throws Throwable {
        // TaskManager taskManager = new TaskManager(getContext());
        TaskManager taskManager = TaskManager.init(getContext());
        Task task = taskManager.generateNewTask(null, null);
        sTaskId1 = task.getId();
        int result = taskManager.getCurrentTaskSize();
        Assert.assertEquals(1, result);
    }

    public void testTask2() throws Throwable {
        // TaskManager taskManager = new TaskManager(getContext());
        TaskManager taskManager = TaskManager.init(getContext());
        Task task = taskManager.generateNewTask(null, null);
        sTaskId2 = task.getId();
        int result = taskManager.getCurrentTaskSize();
        Assert.assertEquals(2, result);
    }

    public void testTask3() throws Throwable {
        // TaskManager taskManager = new TaskManager(getContext());
        TaskManager taskManager = TaskManager.init(getContext());
        Task task = taskManager.generateNewTask(null, null);
        int taskId = task.getId();
        taskManager.removeTask(taskId);
        int result = taskManager.getCurrentTaskSize();
        Assert.assertEquals(2, result);
    }

    public void testTask4() throws Throwable {
        // TaskManager taskManager = new TaskManager(getContext());
        TaskManager taskManager = TaskManager.init(getContext());
        Task task1 = taskManager.generateNewTask(null, null);
        Task task2 = taskManager.generateNewTask(null, null);
        int taskId1 = task1.getId();
        int taskId2 = task2.getId();
        taskManager.removeTask(taskId1);
        taskManager.removeTask(taskId2);
        int result = taskManager.getCurrentTaskSize();
        Assert.assertEquals(2, result);
    }

    public void testTask5() throws Throwable {
        // TaskManager taskManager = new TaskManager(getContext());
        TaskManager taskManager = TaskManager.init(getContext());
        taskManager.removeTask(sTaskId2);
        taskManager.removeTask(sTaskId1);
        int result = taskManager.getCurrentTaskSize();
        sTaskId1 = 0;
        sTaskId2 = 0;
        Assert.assertEquals(0, result);
    }

    public void testA1() throws Throwable {
        Log.w(LOG_TAG, "A1:start");
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        TaskCallback callback = new TestTaskCallback(signal, result, positiveCount);
        // TaskManager taskManager = new TaskManager(getContext());
        TaskManager taskManager = TaskManager.init(getContext());
        String uri = sFileBaseUri + "image01.png";
        Task task = taskManager.generateNewTask(uri, callback);
        taskManager.executeTask(task);
        signal.await();
        Log.w(LOG_TAG, "A1:end");
        Assert.assertEquals(1, positiveCount.get());
        Assert.assertEquals(0, taskManager.getCurrentTaskSize());
    }

    public void testA2() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        String[] uris = {
                sFileBaseUri + "10.png",
                sFileBaseUri + "11.png",
                sFileBaseUri + "12.png",
                sFileBaseUri + "13.png",
                sFileBaseUri + "14.png",
                sFileBaseUri + "15.png",
                sFileBaseUri + "16.png",
                sFileBaseUri + "17.png",
                sFileBaseUri + "18.png",
                sFileBaseUri + "19.png"
        };
        CountDownLatch signal = new CountDownLatch(uris.length);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        TaskCallback callback = new TestTaskCallback(signal, result, positiveCount);
        // TaskManager taskManager = new TaskManager(getContext());
        TaskManager taskManager = TaskManager.init(getContext());
        long startTime = System.currentTimeMillis();
        for (String uri : uris) {
            Task task = taskManager.generateNewTask(uri, callback);
            taskManager.executeTask(task);
        }
        signal.await();
        long endTime = System.currentTimeMillis();
        Log.w(LOG_TAG, "testA2: " + (endTime - startTime));
        Assert.assertEquals(uris.length, positiveCount.get());
        Assert.assertEquals(0, taskManager.getCurrentTaskSize());
    }

    public void testA3() throws Throwable {
        Log.w(LOG_TAG, "A3:start");
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        TestTaskCallback callback = new TestTaskCallback(signal, result, positiveCount);
        // TaskManager taskManager = new TaskManager(getContext());
        TaskManager taskManager = TaskManager.init(getContext());
        String uri = sFileBaseUri + "image01.png";
        Bundle data = new Bundle();
        data.putBoolean(Download.CHECK_ONLY, true);
        Task task = taskManager.generateNewTask(uri, callback, data);
        taskManager.executeTask(task);
        signal.await();
        Log.w(LOG_TAG, "A3:end");
        Assert.assertEquals(1, positiveCount.get());
        Assert.assertEquals(0, taskManager.getCurrentTaskSize());
    }

    public void testA4() throws Throwable {
        Log.w(LOG_TAG, "A4:start");
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        TestTaskCallback callback = new TestTaskCallback(signal, result, positiveCount);
        // TaskManager taskManager = new TaskManager(getContext());
        TaskManager taskManager = TaskManager.init(getContext());
        String uri = sFileBaseUri + "image01.dummy.png";
        Bundle data = new Bundle();
        data.putBoolean(Download.CHECK_ONLY, true);
        Task task = taskManager.generateNewTask(uri, callback, data);
        taskManager.executeTask(task);
        signal.await();
        Bundle dataOnError = callback.getDataOnError();
        boolean isCheckOnly = false;
        if ( dataOnError != null ) {
            isCheckOnly = dataOnError.getBoolean(Download.CHECK_ONLY);
        }
        Log.w(LOG_TAG, "A4:end");
        Assert.assertEquals(0, positiveCount.get());
        Assert.assertEquals(0, taskManager.getCurrentTaskSize());
        Assert.assertEquals(true, isCheckOnly);
    }

    public void testA5() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        String[] uris = {
                sFileBaseUri + "10.png",
                sFileBaseUri + "11.png",
                sFileBaseUri + "12.png",
                sFileBaseUri + "13.png",
                sFileBaseUri + "14.png",
                sFileBaseUri + "15.png",
                sFileBaseUri + "16.png",
                sFileBaseUri + "17.png",
                sFileBaseUri + "18.png",
                sFileBaseUri + "19.png"
        };
        CountDownLatch signal = new CountDownLatch(uris.length);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        TaskCallback callback = new TestTaskCallback(signal, result, positiveCount);
        // TaskManager taskManager = new TaskManager(getContext());
        TaskManager taskManager = TaskManager.init(getContext());
        long startTime = System.currentTimeMillis();
        for (String uri : uris) {
            Bundle data = new Bundle();
            data.putBoolean(Download.CHECK_ONLY, true);
            Task task = taskManager.generateNewTask(uri, callback, data);
            taskManager.executeTask(task);
        }
        signal.await();
        long endTime = System.currentTimeMillis();
        Log.w(LOG_TAG, "testA5: " + (endTime - startTime));
        Assert.assertEquals(uris.length, positiveCount.get());
        Assert.assertEquals(0, taskManager.getCurrentTaskSize());
    }

    public void testA6() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        String[] uris = {
                sFileBaseUri + "10.dummy.png",
                sFileBaseUri + "11.dummy.png",
                sFileBaseUri + "12.dummy.png",
                sFileBaseUri + "13.dummy.png",
                sFileBaseUri + "14.dummy.png",
                sFileBaseUri + "15.dummy.png",
                sFileBaseUri + "16.dummy.png",
                sFileBaseUri + "17.dummy.png",
                sFileBaseUri + "18.dummy.png",
                sFileBaseUri + "19.dummy.png"
        };
        CountDownLatch signal = new CountDownLatch(uris.length);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        TestTaskCallback callback = new TestTaskCallback(signal, result, positiveCount);
        // TaskManager taskManager = new TaskManager(getContext());
        TaskManager taskManager = TaskManager.init(getContext());
        long startTime = System.currentTimeMillis();
        for (String uri : uris) {
            Bundle data = new Bundle();
            data.putBoolean(Download.CHECK_ONLY, true);
            Task task = taskManager.generateNewTask(uri, callback, data);
            taskManager.executeTask(task);
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
        Assert.assertEquals(0, taskManager.getCurrentTaskSize());
        Assert.assertEquals(true, isCheckOnly);
    }

    public void testB1() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        TaskCallback callback = new TestTaskCallback(signal, result, positiveCount);
        // TaskManager taskManager = new TaskManager(getContext());
        TaskManager taskManager = TaskManager.init(getContext());
        String uri = sHttpBaseUri + "image01.png";
        Task task = taskManager.generateNewTask(uri, callback);
        taskManager.executeTask(task);
        signal.await();
        Assert.assertEquals(1, positiveCount.get());
        Assert.assertEquals(0, taskManager.getCurrentTaskSize());
    }

    public void testB2() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        String[] uris = {
                sHttpBaseUri + "10.png",
                sHttpBaseUri + "11.png",
                sHttpBaseUri + "12.png",
                sHttpBaseUri + "13.png",
                sHttpBaseUri + "14.png",
                sHttpBaseUri + "15.png",
                sHttpBaseUri + "16.png",
                sHttpBaseUri + "17.png",
                sHttpBaseUri + "18.png",
                sHttpBaseUri + "19.png"
        };
        CountDownLatch signal = new CountDownLatch(uris.length);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        TaskCallback callback = new TestTaskCallback(signal, result, positiveCount);
        // TaskManager taskManager = new TaskManager(getContext());
        TaskManager taskManager = TaskManager.init(getContext());
        long startTime = System.currentTimeMillis();
        for (String uri : uris) {
            Task task = taskManager.generateNewTask(uri, callback);
            taskManager.executeTask(task);
        }
        signal.await();
        long endTime = System.currentTimeMillis();
        Log.w(LOG_TAG, "testB2: " + (endTime - startTime));
        Assert.assertEquals(uris.length, positiveCount.get());
        Assert.assertEquals(0, taskManager.getCurrentTaskSize());
    }

    public void testC1() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        TaskCallback callback = new TestTaskCallback(signal, result, positiveCount);
        // TaskManager taskManager = new TaskManager(getContext());
        TaskManager taskManager = TaskManager.init(getContext());

        String plainFileUri = sFileBaseUri + "image01.png";
        String uri = plainFileUri + ".enc";
        uri = "file+enc" + uri.substring("file".length());

        Bitmap bitmap = BitmapFactory.decodeFile(plainFileUri.substring("file:".length()));
        File file = new File(plainFileUri.substring("file:".length()) + ".enc");
        if ( !file.exists() ) {
            if ( !file.createNewFile() ) {
                throw new IOException("Cannot create file");
            }
        }

        OutputStream out = TaskManager.getFileOutputStream(
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

        Task task = taskManager.generateNewTask(uri, callback);
        taskManager.executeTask(task);
        signal.await();
        Assert.assertEquals(1, positiveCount.get());
        Assert.assertEquals(0, taskManager.getCurrentTaskSize());
    }

    public void testC2() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        String[] plainFileUris = {
//                sFileBaseUri + "10.png",
//                sFileBaseUri + "11.png",
////                fileBaseUri + "12.png",
//                sFileBaseUri + "13.png",
//                sFileBaseUri + "14.png",
////                fileBaseUri + "15.png",
//                sFileBaseUri + "16.png",
//                sFileBaseUri + "17.png",
////                fileBaseUri + "18.png",
                sFileBaseUri + "19.png"
        };
        String[] uris = {
//                sFileBaseUri + "10.png" + ".enc",
//                sFileBaseUri + "11.png" + ".enc",
////                fileBaseUri + "12.png" + ".enc",
//                sFileBaseUri + "13.png" + ".enc",
//                sFileBaseUri + "14.png" + ".enc",
////                fileBaseUri + "15.png" + ".enc",
//                sFileBaseUri + "16.png" + ".enc",
//                sFileBaseUri + "17.png" + ".enc",
////                fileBaseUri + "18.png" + ".enc",
                sFileBaseUri + "19.png" + ".enc"
        };

        CountDownLatch signal = new CountDownLatch(uris.length);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        TaskCallback callback = new TestTaskCallback(signal, result, positiveCount);
        // TaskManager taskManager = new TaskManager(getContext());
        TaskManager taskManager = TaskManager.init(getContext());

        SharedPreferences preferences = getContext().getSharedPreferences("cipher", Context.MODE_PRIVATE);
        String newCipherDigest = StorageManager.getCipherDigest(getContext());
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
            Log.w(LOG_TAG, "oldCipherDigest: " + oldCipherDigest);
            Log.w(LOG_TAG, "newCipherDigest: " + newCipherDigest);
//            if ( !oldCipherDigest.equals(newCipherDigest) ) {
                OutputStream out = StorageManager.getFileOutputStream(
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
//            } else {
//                Log.e(LOG_TAG, "skip encoding file: " + file.getAbsolutePath());
//            }
            bitmap = null;
        }

        long startTime = System.currentTimeMillis();
        for (String uri : uris) {
            uri = "file+enc" + uri.substring("file".length());
            Log.e(LOG_TAG, "uri: " + uri);
            Task task = taskManager.generateNewTask(uri, callback);
            taskManager.executeTask(task);
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
        TaskCallback callback = new TestTaskCallback(signal, result, positiveCount);
        // TaskManager taskManager = new TaskManager(getContext());
        TaskManager taskManager = TaskManager.init(getContext());
        String uri = sFileBaseUri + "text01.png";
        Task task = taskManager.generateNewTask(uri, callback);
        taskManager.executeTask(task);
        signal.await();
        Assert.assertEquals(0, positiveCount.get());
        Assert.assertEquals(0, taskManager.getCurrentTaskSize());
    }

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

    private static class TestTaskCallback implements TaskCallback {

        private CountDownLatch mSignal = null;
        private Map<String, Boolean> mResult;
        private AtomicInteger mPositiveCount = null;
        private Bundle mDataOnError = null;

        public TestTaskCallback(CountDownLatch signal, Map<String, Boolean> result, AtomicInteger positiveCount) {
            mSignal = signal;
            mResult = result;
            mPositiveCount = positiveCount;
        }

        @Override
        public void onSuccess(Uri uri, Bundle data) {
            Log.w(LOG_TAG, "uri: " + uri.toString());
            mPositiveCount.incrementAndGet();
            mResult.put("result", true);
            mSignal.countDown();
        }

        @Override
        public void onError(Exception e, Bundle data) {
            mResult.put("result", false);
            mDataOnError = data;
            if ( e != null ) {
                Log.w(LOG_TAG, "[TestTaskCallback] onError: " + e);
            }
            mSignal.countDown();
        }

        public Bundle getDataOnError() {
            return mDataOnError;
        }
    }

}
