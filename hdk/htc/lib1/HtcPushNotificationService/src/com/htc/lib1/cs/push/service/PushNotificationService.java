
package com.htc.lib1.cs.push.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.pns.IPnsAgent;
import com.htc.lib1.cs.pns.RegInfo;
import com.htc.lib1.cs.push.PnsRecords;
import com.htc.lib1.cs.push.PushLoggerFactory;
import com.htc.lib1.cs.push.PushProvider;

/**
 * Service to interact with integrated clients.
 * 
 * @author samael_wang@htc.com
 */
public class PushNotificationService extends Service {
    private HtcLogger mLogger = new PushLoggerFactory(this).create();
    private Transport mTransport;

    @Override
    public IBinder onBind(Intent intent) {
        mLogger.verboseS(intent);
        return mTransport.asBinder();
    }

    @Override
    public void onCreate() {
        mLogger.verbose();

        mTransport = new Transport();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLogger.verboseS(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mLogger.verbose();
        super.onDestroy();

        mTransport = null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mLogger.verboseS(intent);
        return super.onUnbind(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mLogger.verbose();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onRebind(Intent intent) {
        mLogger.verboseS(intent);
        super.onRebind(intent);
    }

    /**
     * Implementation of the AIDL.
     * 
     * @author samael_wang@htc.com
     */
    private class Transport extends IPnsAgent.Stub {

        @Override
        public RegInfo getRegInfo() throws RemoteException {
            PnsRecords records = PnsRecords.get(PushNotificationService.this);
            String regId = records.getRegId();
            PushProvider pushProvider = records.getPushProvider();

            RegInfo result = null;
            if (!TextUtils.isEmpty(regId) && pushProvider != null)
                result = new RegInfo(regId, pushProvider.toString());

            mLogger.verboseS(result);
            return result;
        }

    }
}
