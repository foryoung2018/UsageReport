
package com.htc.lib1.cs.push.receiver;

import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.htc.lib1.cs.push.BroadcastUtils;
import com.htc.lib1.cs.push.PnsInternalDefs;
import com.htc.lib1.cs.push.PnsModel;
import com.htc.lib1.cs.push.PnsRecords;

/**
 * Receiver to deliver PNS messages come from GCM.
 */
public class GCMMessageReceiver extends AbstractIntentServiceBroadcastReceiver {

    public static class HandleBroadcastServiceImpl extends HandleBroadcastService {
        private static final String UNDELIVERED_TAG = " (undelivered)";
        
        public HandleBroadcastServiceImpl() {
            super(GCMMessageReceiver.class.getSimpleName());
        }

        @Override
        protected void handleBroadcast(Context context, Intent intent) {
            if (!PnsModel.checkDataUsageAgreement(context)) {
                mmLogger.info("No data usage agreement. Abort operation.");
                return;
            }

            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            String messageType = gcm.getMessageType(intent);

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                mmLogger.error(intent);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                mmLogger.warning(intent);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Get message id.
                String msgId = intent.getStringExtra(PnsInternalDefs.KEY_MESSAGE_ID);

                // Get app list.
                String applist = intent.getStringExtra(PnsInternalDefs.KEY_MESSAGE_APPS);

                if (!PnsRecords.get(context).isRegistered()) {
                    mmLogger.error("Unregistered status: Undelivered.");
                    PnsRecords.get(context).addMessage(msgId, String.format("%s%s",applist, UNDELIVERED_TAG));
                } else if (TextUtils.isEmpty(applist)) {
                    mmLogger.error("Insufficient message content: Receiver apps list was empty.");
                    PnsRecords.get(context).addMessage(msgId, UNDELIVERED_TAG);
                } else {
                    PnsRecords.get(context).addMessage(msgId, applist);
                    // Extract messages and remove metadata.
                    Bundle msg = intent.getExtras();
                    Iterator<String> iter = msg.keySet().iterator();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        if (key.startsWith(PnsInternalDefs.PREFIX_MESSAGE_META_KEYS))
                            iter.remove();
                    }
                    
                    // Deliver to all receivers.
                    BroadcastUtils.deliverMessages(context, applist, msg);
                }
            } else {
                mmLogger.error("Unhandled intent: ", intent);
            }
        }
    }

    @Override
    protected Class<? extends HandleBroadcastService> getServiceClass() {
        return HandleBroadcastServiceImpl.class;
    }
}
