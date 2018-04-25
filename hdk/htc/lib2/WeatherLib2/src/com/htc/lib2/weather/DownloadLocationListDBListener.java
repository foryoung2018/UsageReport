package com.htc.lib2.weather;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

public abstract class DownloadLocationListDBListener {

    private final Messenger mMessenger = new Messenger(new DownloadHandler());
    /**
     * This callback will be invoked when download is success.
     */
    abstract public void onDownloadSuccess();

    /**
     * This callback will be invoked when download is fail.
     */
    abstract public void onDownloadFail(int code);

    public final Messenger getMessenger() {
        return mMessenger;
    }

    private class DownloadHandler extends Handler {
        public DownloadHandler() {
            super();
        }
        @Override
        public final void handleMessage(Message msg) {
            switch (msg.what) {
                case WeatherConsts.DOWNLOAD_RESULT_SUCCESS:
                    onDownloadSuccess();
                    break;
                case WeatherConsts.DOWNLOAD_RESULT_FAIL:
                    onDownloadFail(msg.arg1);
                    break;
            }
        }
    }
}
