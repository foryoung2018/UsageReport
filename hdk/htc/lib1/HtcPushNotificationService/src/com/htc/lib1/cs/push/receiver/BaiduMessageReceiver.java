
package com.htc.lib1.cs.push.receiver;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushMessageReceiver;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.push.BroadcastUtils;
import com.htc.lib1.cs.push.PnsInternalDefs;
import com.htc.lib1.cs.push.PnsModel;
import com.htc.lib1.cs.push.PnsRecords;
import com.htc.lib1.cs.push.PushLoggerFactory;
import com.htc.lib1.cs.push.service.RegistrationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Receiver to deliver PNS messages come from Baidu Push.
 *
 * @author autosun_li@htc.com
 */
public class BaiduMessageReceiver extends PushMessageReceiver {

    private static final String KEY_BROADCAST_INTENT = "broadcastIntent";
    private static final String KEY_BROADCAST_CUSTOMIZE_MESSAGE = "broadcastCustomizeMessage";
    private HtcLogger mLogger = new PushLoggerFactory(this).create();

    protected Class<? extends HandleBroadcastService> getServiceClass() {
        return HandleBroadcastServiceImpl.class;
    }

    @Override
    public void onBind(Context context, int errorCode, String appId, String userId, String channelId, String requestId) {
        mLogger.debugS("errorCode = ", errorCode, ", appId = ", appId, ", userId = ", userId, ", channelId = ", channelId, ", requestId = ", requestId);

        if (!PnsModel.checkDataUsageAgreement(context)) {
            mLogger.info("No data usage agreement. Abort operation.");
            return;
        }

        // Baidu's error code
        // 0 - Success
        // 10001 - Network Problem
        // 10002   服务不可用，连接server失败
        // 10003   服务不可用，503错误
        // 10101 - Integrate Check Error
        // 20001   未知错误
        // 30600 - Internal Server Error
        // 30601 - Method Not Allowed
        // 30602 - Request Parameters Not Valid
        // 30603 - Authentication Failed
        // 30604 - Quota Use Up Payment Required
        // 30605 - Data Required Not Found
        // 30606 - Request Time Expires Timeout
        // 30607 - Channel Token Timeout
        // 30608 - Bind Relation Not Found
        // 30609 - Bind Number Too Many
        if (errorCode == 0) {
            Intent intent = new Intent(PnsInternalDefs.ACTION_PNS_REGISTER_BAIDU);
            intent.setPackage(context.getPackageName());
            intent.putExtra(PnsInternalDefs.KEY_BAIDU_APP_ID, appId);
            intent.putExtra(PnsInternalDefs.KEY_BAIDU_USER_ID, userId);
            intent.putExtra(PnsInternalDefs.KEY_BAIDU_CHANNEL_ID, channelId);
            intent.putExtra(PnsInternalDefs.KEY_BAIDU_REQUEST_ID, requestId);

            mLogger.debug("send broadcast");
            context.sendBroadcast(intent, PnsInternalDefs.PERMISSION_SEND_MESSAGE);
        } else {
            mLogger.error("Baidu register error: ", errorCode);
        }
    }

    @Override
    public void onDelTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags, String requestId) {
        mLogger.debug("errorCode = ", errorCode, ", sucessTags = ", sucessTags, ", failTags = ", failTags, ", requestId = ", requestId);
    }

    @Override
    public void onListTags(Context context, int errorCode, List<String> tags, String requestId) {
        mLogger.debug("errorCode = ", errorCode, ", tags = ", tags, ", requestId = ", requestId);
    }

    @Override
    public void onMessage(Context context, String message, String customContentString) {
        mLogger.debugS("message = ", message, ", customContentString = ", customContentString);

        if (!PnsModel.checkDataUsageAgreement(context)) {
            mLogger.info("No data usage agreement. Abort operation.");
            return;
        }

        // Start service to handle the broadcast intent.
        Intent serviceIntent = new Intent(context, getServiceClass());

        Intent broadcastIntent = new Intent(PushConstants.ACTION_MESSAGE);
        broadcastIntent.putExtra(PushConstants.EXTRA_PUSH_MESSAGE, message);
        broadcastIntent.putExtra(KEY_BROADCAST_CUSTOMIZE_MESSAGE, customContentString);
        serviceIntent.putExtra(KEY_BROADCAST_INTENT, broadcastIntent);

        if (context.startService(serviceIntent) == null) {
            throw new IllegalStateException(
                    "Unable to start service "
                            + serviceIntent.toString()
                            + ". Have you forgot to declared it in AndroidManifest.xml, "
                            + "or set 'manifestmerger.enabled=true' "
                            + "in your project.properties?");
        }

        /*
         * Set success result if it's an ordered broadcast. Surely it indicates
         * no error result can be returned to the caller but since the broadcast
         * handling happens in an intent service and the receiver could finish
         * before the service finishes, the execution result can not be returned
         * through the receiver anyway.
         */
        if (isOrderedBroadcast())
            setResultCode(Activity.RESULT_OK);

    }

    @Override
    public void onNotificationArrived(Context context, String title, String description, String customContentString) {
        mLogger.debug("title = ", title, ", description = ", description, ", customContentString = ", customContentString);

        if (!PnsModel.checkDataUsageAgreement(context)) {
            mLogger.info("No data usage agreement. Abort operation.");
            return;
        }
    }

    @Override
    public void onNotificationClicked(Context context, String title, String description, String customContentString) {
        mLogger.debug("title = ", title, ", description = ", description, ", customContentString = ", customContentString);

        if (!PnsModel.checkDataUsageAgreement(context)) {
            mLogger.info("No data usage agreement. Abort operation.");
            return;
        }
    }

    @Override
    public void onSetTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags, String requestId) {
        mLogger.debug("errorCode = ", errorCode, ", sucessTags = ", sucessTags, ", failTags = ", failTags, ", requestId = ", requestId);
    }

    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        mLogger.debug("errorCode = ", errorCode, ", requestId = ", requestId);
    }

    /**
     * {@link IntentService} to handle the broadcast intent.
     *
     * @author samael_wang@htc.com
     */
    public static class HandleBroadcastService extends IntentService {
        protected HtcLogger mmLogger;

        public HandleBroadcastService(String name) {
            super(name);
            mmLogger = new PushLoggerFactory(name).create();
        }

        @Override
        protected final void onHandleIntent(Intent intent) {
            if (intent == null) {
                // handle service restart case
                new PushLoggerFactory(this).create().error("Intent is null, return!");
                return;
            }

            Intent broadcastIntent = intent.getParcelableExtra(KEY_BROADCAST_INTENT);

            if (broadcastIntent == null)
                throw new IllegalStateException("'" + KEY_BROADCAST_INTENT + "' is null.");

            mmLogger.verboseS(broadcastIntent);
            handleBroadcast(this, broadcastIntent);
        }

        /**
         * Handle the broadcast intent in the background thread of
         * {@link IntentService}. Subclasses can optionally
         * override this behavior.
         *
         * @param context Context to operate on.
         * @param intent  The broadcast intent.
         */
        protected void handleBroadcast(final Context context, final Intent intent) {
        }

    }

    public static class HandleBroadcastServiceImpl extends HandleBroadcastService {
        private static final String UNDELIVERED_TAG = " (undelivered)";

        public HandleBroadcastServiceImpl() {
            super(BaiduMessageReceiver.class.getSimpleName());
        }

        @Override
        protected void handleBroadcast(Context context, Intent intent) {
            if (intent != null && PushConstants.ACTION_MESSAGE.equals(intent.getAction())) {
                String json = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE);
                if (!TextUtils.isEmpty(json)) {
                    mmLogger.debugS("Message: ", json);
                    try {
                        // Parse and decrypt messages via Baidu push
                        JSONObject decryptedJsonObj = decryptMessage(new JSONObject(json));

                        // Get message id.
                        String msgId = decryptedJsonObj.getString(PnsInternalDefs.KEY_MESSAGE_ID);

                        // Get app list.
                        String applist = decryptedJsonObj.getString(PnsInternalDefs.KEY_MESSAGE_APPS);

                        if (!PnsRecords.get(context).isRegistered()) {
                            mmLogger.error("Unregistered status: Undelivered.");
                            PnsRecords.get(context).addMessage(msgId, String.format("%s%s", applist, UNDELIVERED_TAG));
                        } else if (TextUtils.isEmpty(applist)) {
                            mmLogger.error("Insufficient message content: Receiver apps list was empty.");
                            PnsRecords.get(context).addMessage(msgId, UNDELIVERED_TAG);
                        } else {
                            PnsRecords.get(context).addMessage(msgId, applist);
                            // Extract messages and remove metadata.
                            Bundle msg = new Bundle();
                            @SuppressWarnings("unchecked")
                            Iterator<String> iter = decryptedJsonObj.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                if (key.startsWith(PnsInternalDefs.PREFIX_MESSAGE_META_KEYS)) {
                                    iter.remove();
                                } else {
                                    msg.putString(key, decryptedJsonObj.getString(key));
                                }
                            }

                            // Deliver to all receivers.
                            BroadcastUtils.deliverMessages(context, applist, msg);
                        }
                    } catch (JSONException | IllegalStateException e) {
                        mmLogger.error(e);
                    }
                } else {
                    mmLogger.error("Json content is null!");
                }

                String customizeString = intent.getStringExtra(KEY_BROADCAST_CUSTOMIZE_MESSAGE);
                mmLogger.debugS("customizedString = ", customizeString);
            } else {
                mmLogger.error("Unhandled intent: ", intent);
            }
        }

        /**
         * Decrypt messages via Baidu push to protect messages be intercepted or easily be cracked
         *
         * @param jsonObj encrypted JSON object
         * @return Decrypted JSON object
         */
        private JSONObject decryptMessage(JSONObject jsonObj) {
            if (jsonObj != null && !jsonObj.has(PnsInternalDefs.KEY_MESSAGE_CIPHER)) {
                throw new IllegalStateException("No cipher field!");
            }
            JSONObject outputObj = jsonObj;
            try {
                // generate key spec
                Context context = getApplicationContext();
                PnsRecords records = PnsRecords.get(context);
                String reg_key = records.getRegKey();

                /*
                    The reg_key is null may result in the integrated app and PNSClient are using
                    the same Baidu API key. And the PNSClient registered with PNS server, but integrate is not.
                    While server sent push messages to PNSClient, the Baidu push client may sent
                    the messages to integrated apps but not PNSClient.
                    If integrated apps does not register yet, it will capuse NullPointerException.
                 */
                if (TextUtils.isEmpty(reg_key)) {
                    mmLogger.error("reg_key is null, schedule to re-register!");
                    records.addRegistrationEvent("descryptMessage", "regKey is null", false);
                    records.setRegistered(false);
                    RegistrationService.startService(context, "regKey is null!");
                    return jsonObj;
                }
                final MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.update(reg_key.getBytes());
                byte[] sha_reg_key = digest.digest();

                SecretKeySpec sKeySpec = new SecretKeySpec(sha_reg_key, "AES");
                IvParameterSpec ivSpec = new IvParameterSpec(Arrays.copyOf(sha_reg_key, 16));

                String cipher = null;
                if (outputObj != null) {
                    cipher = outputObj.getString(PnsInternalDefs.KEY_MESSAGE_CIPHER);
                }
                mmLogger.debugS("cipher = ", cipher);

                Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
                c.init(Cipher.DECRYPT_MODE, sKeySpec, ivSpec);
                byte[] encryptedByteArray = Base64.decode(cipher != null ? cipher.getBytes() : new byte[0], Base64.DEFAULT);
                String decryptedMessage = new String(c.doFinal(encryptedByteArray));
                mmLogger.debugS("decrypted message = ", decryptedMessage);

                outputObj = jsonObj;
                if (outputObj != null) {
                    outputObj.remove(PnsInternalDefs.KEY_MESSAGE_CIPHER);
                }
                JSONObject obj = new JSONObject(decryptedMessage);
                Iterator<?> keys = obj.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    if (outputObj != null) {
                        outputObj.put(key, obj.get(key));
                    }
                }
            } catch (NoSuchAlgorithmException | JSONException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
                mmLogger.error(e);
            }

            mmLogger.debugS("Decrypted JSON =", outputObj != null ? outputObj.toString() : null);
            return outputObj;
        }
    }
}
