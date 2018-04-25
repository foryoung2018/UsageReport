
package com.htc.lib1.cs.push;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.pns.RegistrationPolicy;

import java.util.UUID;

/**
 * Records of PNS.
 */
public class PnsRecords {
    /**
     * POJO class representing an event record.
     *
     * @author samael_wang@htc.com
     */
    public static class EventRecord {
        // Unix time in milliseconds of the event.
        public long timestamp;
        /**
         * Type of the event. Should be one of
         * {@link PnsRecordsDataSource#EVENT_TYPE_REGISTRATION},
         * {@link PnsRecordsDataSource#EVENT_TYPE_UPDATE} or
         * {@link PnsRecordsDataSource#EVENT_TYPE_UNREGISTRATION}.
         */
        public String type;
        // The reason the event occurs.
        public String actionCause;
        // The reason of the result.
        public String resultCause;
        // The result of the event.
        public boolean success;
    }

    /**
     * POJO class representing a message record.
     *
     * @author samael_wang@htc.com
     */
    public static class MessageRecord {
        // Unix time in milliseconds of the arrival time of the message.
        public long timestamp;
        // Message ID.
        public String msgId;
        // Space-separated list of apps which should receive the message.
        public String appList;
    }

    private static final String PREFS_NAME = "reg_info";
    private static final String KEY_ACCOUNT_ID = "accountId";
    private static final String KEY_UUID = "uuid";
    private static final String KEY_MCCMNC = "mccmnc";
    private static final String KEY_REG_ID = "regId";
    private static final String KEY_REG_KEY = "regKey";
    private static final String KEY_PUSH_PROVIDER = "pushProvider";
    private static final String KEY_REGISTRATION_POLICY = "registrationPolicy";
    private static final String KEY_ALLOW_ALARM = "allowAlarm";
    private static final String KEY_ENABLE_REGISTRATION_SERVICE = "enableRegistrationService";

    private static final String KEY_HAS_DATA_USAGE_AGREEMENT = "hasDataUsageAgreement";

    private static final String KEY_REGISTERED = "registered";
    private static final String KEY_UNREGISTERFAILED = "unregisterFailed";
    private static final String KEY_REGISTRATION_INVALIDATED = "registrationInvalidated";
    private static final String KEY_REG_FAIL_COUNT = "regFailCount";
    private static final String KEY_UNREG_FAIL_COUNT = "unregFailCount";
    private static final String KEY_UPDATE_FAIL_COUNT = "updateFailCount";
    private static final String KEY_NEXT_REGISTRATION = "nextRegistration";
    private static final String KEY_NEXT_UPDATE = "nextUpdate";
    private static final String KEY_NEXT_UNREGISTRATION = "nextUnregistration";

    // FIXME: will remove in the next version
    private static final String KEY_FORCE_BAIDU_REREGISTRATION = "forcebaidureregistration";

    private static final String KEY_PERMISSION_DENY_READ_PHONE_STATE = "permission_read_phone_state";
    private static final String KEY_PERMISSION_DENY_GET_ACCOUNT = "permission_get_account";
    private static final String KEY_PERMISSION_DENY_ACCESS_COARSE_LOCATION = "permission_access_coarse_location";
    private static final String KEY_PERMISSION_DENY_WRITE_EXTERNAL_STOGRAGE = "permission_write_external_storage";

    private static PnsRecords sInstance = null;
    private HtcLogger mLogger = new PushLoggerFactory(this).create();
    private SharedPreferences mPrefs;
    private PnsRecordsDataSource mDataSrc;
    private final boolean mIsPnsClient;

    /**
     * Get the instance of {@link PnsRecords}.
     *
     * @param context Context used to get application context.
     * @return Instance.
     */
    public static synchronized PnsRecords get(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("'context' is null.");
        }

        if (sInstance == null) {
            Context appContext = context.getApplicationContext();
            if (appContext == null) {
                appContext = context;
            }
            sInstance = new PnsRecords(appContext);
        }
        return sInstance;
    }

    /**
     * Construct a {@link PnsRecords} instance.
     *
     * @param context Context to operate on.
     */
    private PnsRecords(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        mIsPnsClient = PnsDefs.PKG_NAME_PNS_CLIENT.equals(context.getPackageName());
        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mDataSrc = PnsRecordsDataSource.get(context);
    }

    /**
     * Read a string value.
     *
     * @param key Key of the entry.
     * @return Corresponding value or {@code null} if not available.
     */
    private String readString(String key) {
        String value = mPrefs.getString(key, null);
        mLogger.verboseS(key, ": ", value);
        return value;
    }

    /**
     * Read a boolean value.
     *
     * @param key Key of the entry.
     * @return Corresponding value. If not available, return false.
     */
    private boolean readBoolean(String key) {
        return readBoolean(key, false);
    }

    private boolean readBoolean(String key, boolean defaultValue) {
        boolean value = mPrefs.getBoolean(key, defaultValue);
        mLogger.verboseS(key, ": ", value);
        return value;
    }

    /**
     * Read a long value.
     *
     * @param key Key of the entry.
     * @return Corresponding value or 0 if not set.
     */
    private long readLong(String key) {
        long value = mPrefs.getLong(key, 0);
        mLogger.verboseS(key, ": ", value);
        return value;
    }

    /**
     * Read a integer value.
     *
     * @param key Key of the entry.
     * @return Corresponding value or 0 if not set.
     */
    private int readInt(String key) {
        int value = mPrefs.getInt(key, 0);
        mLogger.verboseS(key, ": ", value);
        return value;
    }

    /**
     * Write a string value.
     *
     * @param key   Key of the entry.
     * @param value String value to write or {@code null} to remove the entry.
     */
    private void writeString(String key, String value) {
        mLogger.verboseS(key, ": ", value);

        if (TextUtils.isEmpty(value))
            mPrefs.edit().remove(key).apply();
        else
            mPrefs.edit().putString(key, value).apply();
    }

    /**
     * Write a boolean value.
     *
     * @param key   Key of the entry.
     * @param value Boolean value to write.
     */
    private void writeBoolean(String key, boolean value) {
        mLogger.verboseS(key, ": ", value);
        mPrefs.edit().putBoolean(key, value).apply();
    }

    /**
     * Write a long value.
     *
     * @param key   Key of the entry.
     * @param value Long value to write.
     */
    private void writeLong(String key, long value) {
        mLogger.verboseS(key, ": ", value);
        mPrefs.edit().putLong(key, value).apply();
    }

    /**
     * Write a integer value.
     *
     * @param key   Key of the entry.
     * @param value Long value to write.
     */
    private void writeInt(String key, int value) {
        mLogger.verboseS(key, ": ", value);
        mPrefs.edit().putInt(key, value).apply();
    }

    /**
     * Get the UUID used to identify this client.
     *
     * @return UUID or {@code null} if not available.
     */
    public UUID getUuid() {
        try {
            return UUID.fromString(readString(KEY_UUID));
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * Get the MCC/MNC used to identify detected MCC/MNC.
     *
     * @return String or {@code null} if not available.
     */
    public String getMccmnc() {
        return readString(KEY_MCCMNC);
    }

    /**
     * Get the Account ID used to identify this client.
     *
     * @return String or {@code null} if not available.
     */
    public String getAccountId() {
        return readString(KEY_ACCOUNT_ID);
    }

    /**
     * Get the registration id.
     *
     * @return Registration id or {@code null} if not available.
     */
    public String getRegId() {
        return readString(KEY_REG_ID);
    }

    /**
     * Get the registration key.
     *
     * @return Registration key or {@code null} if not available.
     */
    public String getRegKey() {
        return readString(KEY_REG_KEY);
    }

    /**
     * Get registration credentials.
     *
     * @return Registration credentials.
     */
    public RegistrationCredentials getRegCredentails() {
        return new RegistrationCredentials(getRegId(), getRegKey());
    }

    /**
     * Get the push provider.
     *
     * @return The push provider in use or {@code null}
     */
    public PushProvider getPushProvider() {
        return PushProvider.fromString(readString(KEY_PUSH_PROVIDER));
    }

    /**
     * Get the registration policy.
     *
     * @return The registration policy in use or {@code null}
     */
    public RegistrationPolicy getRegistrationPolicy() {
        RegistrationPolicy returnedPolicy = RegistrationPolicy.ALWAYS_REGISTER;
        try {
            int savedRegistrationPolicy = readInt(KEY_REGISTRATION_POLICY);
            returnedPolicy = RegistrationPolicy.values()[savedRegistrationPolicy];
            mLogger.debug("returnedPolicy=", returnedPolicy);
        } catch (Exception e) {
            mLogger.error(e);
        }
        return returnedPolicy;
    }

    /**
     * Get the value AllowAlarm.
     *
     * @return The AllowAlarm in use or {@code null}
     */
    public boolean getAllowAlarm() {
        return readBoolean(KEY_ALLOW_ALARM);
    }

    /**
     * Get the EnableRegistrationService.
     *
     * @return The EnableRegistrationService in use or {@code null}
     */
    public boolean getEnableRegistrationService() {
        return readBoolean(KEY_ENABLE_REGISTRATION_SERVICE);
    }

    /**
     * Check if the push client has registered.
     *
     * @return True if it is.
     */
    public boolean isRegistered() {
        return readBoolean(KEY_REGISTERED);
    }

    // FIXME: will remove in the next version

    /**
     * Check if the push client has re-registered for Baidu push client.
     *
     * @return True if it is.
     */
    public boolean isForceBaiduReRegistered() {
        return readBoolean(KEY_FORCE_BAIDU_REREGISTRATION);
    }

    /**
     * Check if the push client has unregistered.
     *
     * @return True if it is.
     */
    public boolean isUnregisterFailed() {
        return readBoolean(KEY_UNREGISTERFAILED);
    }

    /**
     * Get registration fail count.
     *
     * @return Fail count.
     */
    public long getRegistrationFailCount() {
        return readLong(KEY_REG_FAIL_COUNT);
    }

    /**
     * Get update fail count.
     *
     * @return Fail count.
     */
    public long getUpdateFailCount() {
        return readLong(KEY_UPDATE_FAIL_COUNT);
    }

    /**
     * Get unregistration fail count.
     *
     * @return Fail count.
     */
    public long getUnregistrationFailCount() {
        return readLong(KEY_UNREG_FAIL_COUNT);
    }

    /**
     * Set UUID as registration key
     *
     * @param uuid Registration key
     */
    public synchronized void setUuid(UUID uuid) {
        if (uuid != null)
            writeString(KEY_UUID, uuid.toString());
        else
            writeString(KEY_UUID, null);
    }

    /**
     * Set current MCC/MNC
     *
     * @param mccmnc detected MCC/MNC
     */
    public synchronized void setMccmnc(String mccmnc) {
        writeString(KEY_MCCMNC, mccmnc);
    }

    /**
     * Set current account id
     *
     * @param accountId HTC Account ID
     */
    public synchronized void setAccountId(String accountId) {
        if (accountId != null)
            writeString(KEY_ACCOUNT_ID, accountId);
        else
            writeString(KEY_ACCOUNT_ID, null);
    }

    /**
     * Set the registration id.
     *
     * @param regId Registration id.
     */
    public synchronized void setRegId(String regId) {
        writeString(KEY_REG_ID, regId);
    }

    /**
     * Set the registration key.
     *
     * @param regKey Registration key.
     */
    public synchronized void setRegKey(String regKey) {
        writeString(KEY_REG_KEY, regKey);
    }

    /**
     * Set registration credentials.
     *
     * @param credentials Credentials to set.
     */
    public synchronized void setRegCredentials(RegistrationCredentials credentials) {
        setRegId(credentials == null ? null : credentials.getId());
        setRegKey(credentials == null ? null : credentials.getKey());
    }

    /**
     * Set the push provider in use.
     *
     * @param provider The push provider in use.
     */
    public synchronized void setPushProvider(PushProvider provider) {
        writeString(KEY_PUSH_PROVIDER, provider == null ? null : provider.toString());
    }

    /**
     * Set the registration policy use.
     *
     * @param policy The registration policy in use.
     */
    public synchronized void setRegistrationPolicy(RegistrationPolicy policy) {
        writeInt(KEY_REGISTRATION_POLICY, policy.ordinal());
    }

    /**
     * Set value of allowAlarm in use.
     *
     * @param value The allow alarm in use.
     */
    public synchronized void setAllowAlarm(boolean value) {
        writeBoolean(KEY_ALLOW_ALARM, value);
    }

    /**
     * Set the value of EnableRegistrationService in use.
     *
     * @param value The EnableRegistrationService in use.
     */
    public synchronized void setEnableRegistrationService(boolean value) {
        writeBoolean(KEY_ENABLE_REGISTRATION_SERVICE, value);
    }

    /**
     * Set registration status. Implicitly clear registration fail count if set
     * to registered.
     *
     * @param registered True if registered.
     */
    public synchronized void setRegistered(boolean registered) {
        writeBoolean(KEY_REGISTERED, registered);
        if (registered)
            writeLong(KEY_REG_FAIL_COUNT, 0);
    }

    // FIXME: will remove in the next version

    /**
     * Set force Baidu push re-registration status.
     *
     * @param registered True if registered.
     */
    public synchronized void setForceBaiduReRegistered(boolean registered) {
        writeBoolean(KEY_FORCE_BAIDU_REREGISTRATION, registered);
    }


    /**
     * Set unregistration status. Implicitly clear registration fail count if
     * set to unregistered.
     *
     * @param unregisterFailed True if registered.
     */
    public synchronized void setUnregisterFailed(boolean unregisterFailed) {
        writeBoolean(KEY_UNREGISTERFAILED, unregisterFailed);
        if (unregisterFailed)
            writeLong(KEY_UNREG_FAIL_COUNT, 0);
    }

    /**
     * Invalidate the registration before it's actually expired. The reason to
     * do this is because push providers, such as GCM, expires automatically
     * after package updated. In this case, although the PNS record is still
     * valid, GCM registration id has been changed and server won't be able to
     * notify this client.
     */
    public synchronized void invalidateRegistration() {
        if (isRegistered()) {
            writeBoolean(KEY_REGISTRATION_INVALIDATED, true);
        }
    }

    /**
     * Record the scheduled time of next registration. Usually indicates a
     * retry.
     *
     * @param timestamp Timestamp in milliseconds since Unix epoch time.
     */
    public synchronized void setNextRegistration(long timestamp) {
        writeLong(KEY_NEXT_REGISTRATION, timestamp);
    }

    /**
     * Get the scheduled time of next registration.
     *
     * @return timestamp or 0 if no record found.
     */
    public synchronized long getNextRegistration() {
        return readLong(KEY_NEXT_REGISTRATION);
    }

    /**
     * Clear the record of the next scheduled registration.
     */
    public synchronized void clearNextRegistration() {
        mPrefs.edit().remove(KEY_NEXT_REGISTRATION).apply();
    }

    /**
     * Record the scheduled time of next update. Might be a regular update or a
     * retry.
     *
     * @param timestamp Timestamp in milliseconds since Unix epoch time.
     */
    public synchronized void setNextUpdate(long timestamp) {
        writeLong(KEY_NEXT_UPDATE, timestamp);
    }

    /**
     * Get the scheduled time of next update.
     *
     * @return timestamp or 0 if no record found.
     */
    public synchronized long getNextUpdate() {
        return readLong(KEY_NEXT_UPDATE);
    }

    /**
     * Clear the record of the next scheduled update.
     */
    public synchronized void clearNextUpdate() {
        mPrefs.edit().remove(KEY_NEXT_UPDATE).apply();
    }

    /**
     * Record the scheduled time of next unregistration. Usually indicates a
     * retry.
     *
     * @param timestamp Timestamp in milliseconds since Unix epoch time.
     */
    public synchronized void setNextUnregistration(long timestamp) {
        writeLong(KEY_NEXT_UNREGISTRATION, timestamp);
    }

    /**
     * Get the scheduled time of next unregistration.
     *
     * @return timestamp or 0 if no record found.
     */
    public synchronized long getNextUnregistration() {
        return readLong(KEY_NEXT_UNREGISTRATION);
    }

    /**
     * Clear the record of the next scheduled unregistration.
     */
    public synchronized void clearNextUnregistration() {
        mPrefs.edit().remove(KEY_NEXT_UNREGISTRATION).apply();
    }

    /**
     * Add a registration event record.
     *
     * @param actionCause The reason this registration event occurs.
     * @param resultCause Optional field to describe the reason of the failure /
     *                    success result.
     * @param success     {@code true} if the registration was successful.
     */
    public synchronized void addRegistrationEvent(String actionCause, String resultCause,
                                                  boolean success) {

        // Update database.
        mDataSrc.addEventRecord(PnsRecordsDataSource.EVENT_TYPE_REGISTRATION,
                actionCause, resultCause, success);
        mDataSrc.cleanExpiredEventRecords();

        // Update preferences.
        if (success) {
            Editor editor = mPrefs.edit();
            editor.remove(KEY_REG_FAIL_COUNT);
            editor.remove(KEY_REGISTRATION_INVALIDATED);
            editor.apply();
        } else {
            writeLong(KEY_REG_FAIL_COUNT, getRegistrationFailCount() + 1);
        }
    }

    /**
     * Add an update registration event record.
     *
     * @param actionCause The reason this registration event occurs.
     * @param resultCause Optional field to describe the reason of the failure /
     *                    success result.
     * @param success     {@code true} if the update was successful.
     */
    public synchronized void addUpdateEvent(String actionCause, String resultCause, boolean success) {

        // Update database.
        mDataSrc.addEventRecord(PnsRecordsDataSource.EVENT_TYPE_UPDATE,
                actionCause, resultCause, success);
        mDataSrc.cleanExpiredEventRecords();

        // Update preferences.
        if (success) {
            Editor editor = mPrefs.edit();
            editor.remove(KEY_UPDATE_FAIL_COUNT).apply();
            editor.remove(KEY_REGISTRATION_INVALIDATED);
            editor.apply();
        } else {
            writeLong(KEY_UPDATE_FAIL_COUNT, getRegistrationFailCount() + 1);
        }
    }

    /**
     * Add an unregistration event record.
     *
     * @param actionCause The reason this registration event occurs.
     * @param resultCause Optional field to describe the reason of the failure /
     *                    success result.
     * @param success     {@code true} if the unregistration was successful.
     */
    public synchronized void addUnregistrationEvent(String actionCause, String resultCause,
                                                    boolean success) {

        // Update database.
        mDataSrc.addEventRecord(PnsRecordsDataSource.EVENT_TYPE_UNREGISTRATION,
                actionCause, resultCause, success);
        mDataSrc.cleanExpiredEventRecords();

        // Update preferences.
        if (success) {
            // clear All shared preferences except for UUID
            UUID uuid = getUuid();

            // Clear preferences.
            mPrefs.edit().clear().apply();

            setUuid(uuid);
        } else {
            writeLong(KEY_UNREG_FAIL_COUNT, getUnregistrationFailCount() + 1);
        }
    }

    /**
     * Get the most recent {@code limit} records of events.
     *
     * @param limit Number of records to get.
     * @return {@link EventRecord}
     */
    public EventRecord[] getRecentEventRecords(int limit) {
        return mDataSrc.getRecentEventRecords(limit);
    }

    /**
     * Get the most recent {@code limit} records of messages.
     *
     * @param limit Number of records to get.
     * @return {@link MessageRecord}
     */
    public MessageRecord[] getRecentMessageRecords(int limit) {
        return mDataSrc.getRecentMessageRecords(limit);
    }

    /**
     * Add a message record.
     *
     * @param msgId   ID of the push message.
     * @param applist List of apps to receive this message.
     */
    public synchronized void addMessage(String msgId, String applist) {
        mDataSrc.addMessageRecord(msgId, applist);
        mDataSrc.cleanExpiredMessageRecords();
    }

    /**
     * Set Denied status of permission: READ_PHONE_STATE
     *
     * @param denied True if should show rationale before.
     */
    public synchronized void setDenyPermissionOfReadPhoneState(boolean denied) {
        writeBoolean(KEY_PERMISSION_DENY_READ_PHONE_STATE, denied);
    }

    /**
     * Set Denied status of permission: ACCESS_COARSE_LOCATION
     *
     * @param denied True if should show rationale before.
     */
    public synchronized void setDenyPermissionOfAccessCoarseLocation(boolean denied) {
        writeBoolean(KEY_PERMISSION_DENY_ACCESS_COARSE_LOCATION, denied);
    }

    /**
     * Set Denied status of permission: GET_ACCOUNT
     *
     * @param denied True if should show rationale before.
     */
    public synchronized void setDenyPermissionOfGetAccount(boolean denied) {
        writeBoolean(KEY_PERMISSION_DENY_GET_ACCOUNT, denied);
    }

    /**
     * Set Denied status of permission: WRITE_EXTERNAL_STOGRAGE
     *
     * @param denied True if should show rationale before.
     */
    public synchronized void setDenyPermissionOfWriteExternalStorage(boolean denied) {
        writeBoolean(KEY_PERMISSION_DENY_WRITE_EXTERNAL_STOGRAGE, denied);
    }


    /**
     * Get denied status of permission: READ_PHONE_STATE
     *
     * @return denied state True if it's denied
     */
    public synchronized boolean getDenyPermissionOfReadPhoneState() {
        return readBoolean(KEY_PERMISSION_DENY_READ_PHONE_STATE);
    }

    /**
     * Get denied status of permission: ACCESS_COARSE_LOCATION
     *
     * @return denied state True if it's denied
     */
    public synchronized boolean getDenyPermissionOfAccessCoarseLocation() {
        return readBoolean(KEY_PERMISSION_DENY_ACCESS_COARSE_LOCATION);
    }

    /**
     * Get denied status of permission: GET_ACCOUNT
     *
     * @return denied state True if it's denied
     */
    public synchronized boolean setDenyPermissionOfGetAccount() {
        return readBoolean(KEY_PERMISSION_DENY_GET_ACCOUNT);
    }

    /**
     * Get denied status of permission: WRITE_EXTERNAL_STOGRAGE
     *
     * @return denied state True if it's denied
     */
    public synchronized boolean setDenyPermissionOfWriteExternalStorage() {
        return readBoolean(KEY_PERMISSION_DENY_WRITE_EXTERNAL_STOGRAGE);
    }

    /**
     * Check permission to see if any denied
     *
     * @param permissions permissions which are not grant before
     * @return true if denied
     */
    public synchronized boolean checkDenyPermissions(String[] permissions) {
        boolean result = false;

        for (String permission : permissions) {
            if (android.Manifest.permission.ACCESS_COARSE_LOCATION.equalsIgnoreCase(permission)) {
                result = result || readBoolean(KEY_PERMISSION_DENY_ACCESS_COARSE_LOCATION);
            } else if (android.Manifest.permission.GET_ACCOUNTS.equalsIgnoreCase(permission)) {
                result = result || readBoolean(KEY_PERMISSION_DENY_GET_ACCOUNT);
            } else if (android.Manifest.permission.READ_PHONE_STATE
                    .equalsIgnoreCase(permission)) {
                result = result || readBoolean(KEY_PERMISSION_DENY_READ_PHONE_STATE);
            } else if (android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    .equalsIgnoreCase(permission)) {
                result = result || readBoolean(KEY_PERMISSION_DENY_WRITE_EXTERNAL_STOGRAGE);
            }
        }

        return result;
    }

    public synchronized boolean hasDataUsageAgreement() {
        return readBoolean(KEY_HAS_DATA_USAGE_AGREEMENT, mIsPnsClient ? true : false);
    }

    public synchronized void setHasDataUsageAgreement(boolean value) {
        writeBoolean(KEY_HAS_DATA_USAGE_AGREEMENT, value);
    }
}
