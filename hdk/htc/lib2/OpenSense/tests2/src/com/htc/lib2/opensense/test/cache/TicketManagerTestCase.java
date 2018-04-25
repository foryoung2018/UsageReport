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

import org.nikkii.embedhttp.HttpServer;
import org.nikkii.embedhttp.handler.HttpStaticFileHandler;

import com.htc.lib2.opensense.cache.Download;
import com.htc.lib2.opensense.cache.DownloadCallback;
import com.htc.lib2.opensense.cache.TicketManager;
import com.htc.lib2.opensense.internal.SystemWrapper;

import junit.framework.Assert;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.util.Log;

public class TicketManagerTestCase extends AndroidTestCase {

    private static final int TEST_HTTP_SERVER_PORT = 8083;
    private static final String LOG_TAG = TicketManagerTestCase.class.getSimpleName();

    private static boolean sHasHttpServerInited = false;
    private static int sTicketId1 = 0;
    private static int sTicketId2 = 0;
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

    public void testATicket1() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        TicketManager ticketManager = TicketManager.init(getContext());
        sTicketId1 = ticketManager.generateNewTicket(null, null, null);
        int result = ticketManager.getCurrentTicketSize();
        Assert.assertEquals(1, result);
    }

    public void testATicket2() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        TicketManager ticketManager = TicketManager.init(getContext());
        sTicketId2 = ticketManager.generateNewTicket(null, null, null);
        int result = ticketManager.getCurrentTicketSize();
        Assert.assertEquals(2, result);
    }

    public void testATicket3() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        TicketManager ticketManager = TicketManager.init(getContext());
        int ticketId = ticketManager.generateNewTicket(null, null, null);
        ticketManager.removeTicket(ticketId);
        int result = ticketManager.getCurrentTicketSize();
        Assert.assertEquals(2, result);
    }

    public void testATicket4() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        TicketManager ticketManager = TicketManager.init(getContext());
        ticketManager.removeTicket(sTicketId1);
        ticketManager.removeTicket(sTicketId2);
        int result = ticketManager.getCurrentTicketSize();
        sTicketId1 = 0;
        sTicketId2 = 0;
        Assert.assertEquals(0, result);
    }

    public void testB1() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        TicketManager ticketManager = TicketManager.init(getContext());
        String uri = sFileBaseUri + "image02.png";
        ticketManager.generateNewTicket(uri, callback, null);
        signal.await();
        Assert.assertEquals(1, positiveCount.get());
        Assert.assertEquals(0, ticketManager.getCurrentTicketSize());
    }

    public void testB2() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        String[] uris = {
                sFileBaseUri + "20.png",
                sFileBaseUri + "21.png",
                sFileBaseUri + "22.png",
                sFileBaseUri + "23.png",
                sFileBaseUri + "24.png",
                sFileBaseUri + "25.png",
                sFileBaseUri + "26.png",
                sFileBaseUri + "27.png",
                sFileBaseUri + "28.png",
                sFileBaseUri + "29.png"
        };
        CountDownLatch signal = new CountDownLatch(uris.length);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        TicketManager ticketManager = TicketManager.init(getContext());
        long startTime = System.currentTimeMillis();
        for (String uri : uris) {
            ticketManager.generateNewTicket(uri, callback, null);
        }
        signal.await();
        long endTime = System.currentTimeMillis();
        Log.w(LOG_TAG, "testB2: " + (endTime - startTime));
        Assert.assertEquals(uris.length, positiveCount.get());
        Assert.assertEquals(0, ticketManager.getCurrentTicketSize());
    }

    public void testB3() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        TicketManager ticketManager = TicketManager.init(getContext());
        String uri = sFileBaseUri + "image02.png";
        Bundle data = new Bundle();
        data.putBoolean(Download.CHECK_ONLY, true);
        ticketManager.generateNewTicket(uri, callback, data);
        signal.await();
        Assert.assertEquals(1, positiveCount.get());
        Assert.assertEquals(0, ticketManager.getCurrentTicketSize());
    }

    public void testB4() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        TestDownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        TicketManager ticketManager = TicketManager.init(getContext());
        String uri = sFileBaseUri + "image02.dummy.png";
        Bundle data = new Bundle();
        data.putBoolean(Download.CHECK_ONLY, true);
        ticketManager.generateNewTicket(uri, callback, data);
        signal.await();
        Bundle dataOnError = callback.getDataOnError();
        boolean isCheckOnly = false;
        if ( dataOnError != null ) {
            isCheckOnly = dataOnError.getBoolean(Download.CHECK_ONLY);
        }
        Assert.assertEquals(0, positiveCount.get());
        Assert.assertEquals(0, ticketManager.getCurrentTicketSize());
        Assert.assertEquals(true, isCheckOnly);
    }

    public void testB5() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        String[] uris = {
                sFileBaseUri + "20.png",
                sFileBaseUri + "21.png",
                sFileBaseUri + "22.png",
                sFileBaseUri + "23.png",
                sFileBaseUri + "24.png",
                sFileBaseUri + "25.png",
                sFileBaseUri + "26.png",
                sFileBaseUri + "27.png",
                sFileBaseUri + "28.png",
                sFileBaseUri + "29.png"
        };
        CountDownLatch signal = new CountDownLatch(uris.length);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        TicketManager ticketManager = TicketManager.init(getContext());
        long startTime = System.currentTimeMillis();
        for (String uri : uris) {
            Bundle data = new Bundle();
            data.putBoolean(Download.CHECK_ONLY, true);
            ticketManager.generateNewTicket(uri, callback, data);
        }
        signal.await();
        long endTime = System.currentTimeMillis();
        Log.w(LOG_TAG, "testB5: " + (endTime - startTime));
        Assert.assertEquals(uris.length, positiveCount.get());
        Assert.assertEquals(0, ticketManager.getCurrentTicketSize());
    }

    public void testB6() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        String[] uris = {
                sFileBaseUri + "20.dummy.png",
                sFileBaseUri + "21.dummy.png",
                sFileBaseUri + "22.dummy.png",
                sFileBaseUri + "23.dummy.png",
                sFileBaseUri + "24.dummy.png",
                sFileBaseUri + "25.dummy.png",
                sFileBaseUri + "26.dummy.png",
                sFileBaseUri + "27.dummy.png",
                sFileBaseUri + "28.dummy.png",
                sFileBaseUri + "29.dummy.png"
        };
        CountDownLatch signal = new CountDownLatch(uris.length);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        TestDownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        TicketManager ticketManager = TicketManager.init(getContext());
        long startTime = System.currentTimeMillis();
        for (String uri : uris) {
            Bundle data = new Bundle();
            data.putBoolean(Download.CHECK_ONLY, true);
            ticketManager.generateNewTicket(uri, callback, data);
        }
        signal.await();
        Bundle dataOnError = callback.getDataOnError();
        boolean isCheckOnly = false;
        if ( dataOnError != null ) {
            isCheckOnly = dataOnError.getBoolean(Download.CHECK_ONLY);
        }
        long endTime = System.currentTimeMillis();
        Log.w(LOG_TAG, "testB6: " + (endTime - startTime));
        Assert.assertEquals(0, positiveCount.get());
        Assert.assertEquals(0, ticketManager.getCurrentTicketSize());
        Assert.assertEquals(true, isCheckOnly);
    }

    public void testC1() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        CountDownLatch signal = new CountDownLatch(1);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        TicketManager ticketManager = TicketManager.init(getContext());
        String uri = sHttpBaseUri + "image02.png";
        ticketManager.generateNewTicket(uri, callback, null);
        signal.await();
        Assert.assertEquals(1, positiveCount.get());
        Assert.assertEquals(0, ticketManager.getCurrentTicketSize());
    }

    public void testC2() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        String[] uris = {
                sHttpBaseUri + "20.png",
                sHttpBaseUri + "21.png",
                sHttpBaseUri + "22.png",
                sHttpBaseUri + "23.png",
                sHttpBaseUri + "24.png",
                sHttpBaseUri + "25.png",
                sHttpBaseUri + "26.png",
                sHttpBaseUri + "27.png",
                sHttpBaseUri + "28.png",
                sHttpBaseUri + "29.png"
        };
        CountDownLatch signal = new CountDownLatch(uris.length);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        TicketManager ticketManager = TicketManager.init(getContext());
        long startTime = System.currentTimeMillis();
        for (String uri : uris) {
            ticketManager.generateNewTicket(uri, callback, null);
        }
        signal.await();
        long endTime = System.currentTimeMillis();
        Log.w(LOG_TAG, "testC2: " + (endTime - startTime));
        Assert.assertEquals(uris.length, positiveCount.get());
        Assert.assertEquals(0, ticketManager.getCurrentTicketSize());
    }

    public void testD1() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        String[] uris = {
                sHttpBaseUri + "A0.png",
                sHttpBaseUri + "A0.png",
                sHttpBaseUri + "A0.png",
                sHttpBaseUri + "A0.png",
                sHttpBaseUri + "A0.png",
                sHttpBaseUri + "A0.png",
                sHttpBaseUri + "A0.png",
                sHttpBaseUri + "A0.png",
                sHttpBaseUri + "A0.png",
                sHttpBaseUri + "A0.png"
        };
        CountDownLatch signal = new CountDownLatch(uris.length);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        TicketManager ticketManager = TicketManager.init(getContext());
        long startTime = System.currentTimeMillis();
        int count = 0;
        int ticketId = 0;
        for (String uri : uris) {
            ticketId = ticketManager.generateNewTicket(uri, callback, null);
            if ( count == 3 ) {
                ticketManager.removeTicket(ticketId);
                Log.w(LOG_TAG, "removed ticket id: " + ticketId);
            }
            count++;
        }
        signal.await();
        long endTime = System.currentTimeMillis();
        Log.w(LOG_TAG, "testD1: " + (endTime - startTime));
        Assert.assertEquals(uris.length - 1, positiveCount.get());
        Assert.assertEquals(0, ticketManager.getCurrentTicketSize());
    }

    public void testD2() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        String[] uris = {
                sHttpBaseUri + "A1.png",
                sHttpBaseUri + "A1.png",
                sHttpBaseUri + "A1.png",
                sHttpBaseUri + "A1.png",
                sHttpBaseUri + "A1.png",
                sHttpBaseUri + "A1.png",
                sHttpBaseUri + "A1.png",
                sHttpBaseUri + "A1.png",
                sHttpBaseUri + "A1.png",
                sHttpBaseUri + "A1.png"
        };
        CountDownLatch signal = new CountDownLatch(uris.length);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        TicketManager ticketManager = TicketManager.init(getContext());
        long startTime = System.currentTimeMillis();
        int count = 0;
        int ticketId = 0;
        for (String uri : uris) {
            ticketId = ticketManager.generateNewTicket(uri, callback, null);
            if ( count / 3 == 1 ) {
                ticketManager.removeTicket(ticketId);
                Log.w(LOG_TAG, "removed ticket id: " + ticketId);
            }
            count++;
        }
        signal.await();
        long endTime = System.currentTimeMillis();
        Log.w(LOG_TAG, "testD1: " + (endTime - startTime));
        Assert.assertEquals(uris.length - 3, positiveCount.get());
        Assert.assertEquals(0, ticketManager.getCurrentTicketSize());
    }

    public void testD3() throws Throwable {
        SystemWrapper.setCacheManagerAuthority(DownloadTestCase.CACHEMANAGER_AUTHORITY);
        String[] uris = {
                sHttpBaseUri + "A2.png",
                sHttpBaseUri + "A2.png",
                sHttpBaseUri + "A2.png",
                sHttpBaseUri + "A2.png",
                sHttpBaseUri + "A2.png",
                sHttpBaseUri + "A2.png",
                sHttpBaseUri + "A2.png",
                sHttpBaseUri + "A2.png",
                sHttpBaseUri + "A2.png",
                sHttpBaseUri + "A2.png"
        };
        CountDownLatch signal = new CountDownLatch(uris.length);
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        AtomicInteger positiveCount = new AtomicInteger();
        DownloadCallback callback = new TestDownloadCallback(signal, result, positiveCount);
        TicketManager ticketManager = TicketManager.init(getContext());
        long startTime = System.currentTimeMillis();
//        int count = 0;
        int ticketId = 0;
        for (String uri : uris) {
            ticketId = ticketManager.generateNewTicket(uri, callback, null);
//            if ( count / 2 == 1 ) {
                ticketManager.removeTicket(ticketId);
                Log.w(LOG_TAG, "removed ticket id: " + ticketId);
//            }
//            count++;
        }
        signal.await();
        long endTime = System.currentTimeMillis();
        Log.w(LOG_TAG, "testD1: " + (endTime - startTime));
        Assert.assertEquals(0, positiveCount.get());
        Assert.assertEquals(0, ticketManager.getCurrentTicketSize());
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
            if ( e != null ) {
                Log.w(LOG_TAG, "[TestDownloadCallback] onError: " + e);
            }
            mSignal.countDown();
        }

        public Bundle getDataOnError() {
            return mDataOnError;
        }
    }
}
