
package com.htc.lib1.cs.account.restobj;

import android.text.TextUtils;
import android.util.SparseArray;

import com.htc.lib1.cs.account.restservice.IdentityJsonClasses;

import java.util.Date;
import java.util.UUID;

/**
 * Device token representation.
 */
public class DeviceToken {
    private String mAuthToken;
    private Boolean mIsEmailVerified;
    private String mCountryCode;
    private Status mStatus;
    private Date mGracePeriodExpireTime;
    private int mCheckGracePeriodInDay;
    private Boolean mIsFirstLogin;
    private UUID mAccountId;
    private String mRefreshToken;
    private String mServerUri;

    public enum Status {
        STATUS_NEED_MIGRATE(0),
        STATUS_NEED_UPDATE_SECURITY_PAIR(1),
        STATUS_GRACE_PERIOD_NOT_STARTED(2),
        STATUS_GRACE_PERIOD_EXPIRED(3);

        private static final SparseArray<Status> sStatusMap = new SparseArray<Status>();
        static {
            for (Status s : Status.values()) {
                sStatusMap.append(s.mValue, s);
            }
        }

        private int mValue;

        private Status(int code) {
            mValue = code;
        }

        /**
         * Set the value.
         * 
         * @param value integer.
         */
        public void setValue(int value) {
            this.mValue = value;
        }

        /**
         * Get the value.
         * 
         * @return Value in integer.
         */
        public int getValue() {
            return this.mValue;
        }

        /**
         * Convert an integer code to status.
         * 
         * @param code Code in integer.
         * @return Corresponding {@code Status}.
         */
        public static Status valueOf(int code) {
            return sStatusMap.get(code);
        }
    }

    /**
     * Construct a {@link DeviceToken} from {@link IdentityJsonClasses.WDeviceToken}.
     * 
     * @param webToken {@link DeviceToken}.
     * @param serverUri Server base uri.
     */
    public DeviceToken(IdentityJsonClasses.WDeviceToken webToken, String serverUri) {
        // Check necessary data.
        if (webToken == null)
            throw new IllegalArgumentException("'webToken' is null.");
        if (TextUtils.isEmpty(webToken.AuthKey))
            throw new IllegalArgumentException("'AuthKey' is null or empty.");
        if (TextUtils.isEmpty(webToken.CountryCode))
            throw new IllegalArgumentException("'CountryCode' is null or empty.");
        if (webToken.AccountId == null)
            throw new IllegalArgumentException("'AccountId' is null.");
        if (TextUtils.isEmpty(serverUri))
            throw new IllegalArgumentException("'serverUri' is null or empty.");

        mAuthToken = removeAuthKeyTimeStamp(webToken.AuthKey);
        mAccountId = webToken.AccountId;
        mIsEmailVerified = webToken.IsEmailVerified;
        mIsFirstLogin = webToken.FirstLogin;
        mCountryCode = webToken.CountryCode;
        mGracePeriodExpireTime = webToken.GracePeriodExpireTime;
        mCheckGracePeriodInDay = webToken.CheckGracePeriodInDay;
        mRefreshToken = webToken.RefreshToken;
        mServerUri = serverUri;
        if (webToken.Status != null)
            mStatus = Status.valueOf(Integer.parseInt(webToken.Status));
    }

    /**
     * Construct a {@link DeviceToken} from {@link AuthenticationToken}.
     * 
     * @param webToken {@link AuthenticationToken}.
     */
    public DeviceToken(AuthenticationToken webToken) {
        // Check necessary data.
        if (webToken == null)
            throw new IllegalArgumentException("'webToken' is null.");
        if (webToken.account == null)
            throw new IllegalArgumentException("'webToken account detail' is null.");
        if (TextUtils.isEmpty(webToken.accessToken))
            throw new IllegalArgumentException("'AuthKey' is null or empty.");
        if (TextUtils.isEmpty(webToken.account.countryCode))
            throw new IllegalArgumentException("'CountryCode' is null or empty.");
        if (webToken.account.accountId == null)
            throw new IllegalArgumentException("'AccountId' is null.");

        mAuthToken = removeAuthKeyTimeStamp(webToken.accessToken);
        mAccountId = webToken.account.accountId;
        mIsEmailVerified = webToken.account.isVerified;
        mIsFirstLogin = false;
        mCountryCode = webToken.account.countryCode;
        mRefreshToken = webToken.refreshToken;
        mServerUri = webToken.serviceUri;
    }

    /**
     * Get account UUID.
     * 
     * @return Account uuid.
     */
    public UUID getAccountId() {
        return mAccountId;
    }

    /**
     * Get the auth token.
     * 
     * @return Auth token.
     */
    public String getAuthToken() {
        return mAuthToken;
    }

    /**
     * Get the country code.
     * 
     * @return Country code.
     */
    public String getCountryCode() {
        return mCountryCode;
    }

    /**
     * Get if email has been verified.
     * 
     * @return True if verified.
     */
    public boolean isEmailVerified() {
        return mIsEmailVerified;
    }

    /**
     * Check if it's first login.
     * 
     * @return True if it is.
     */
    public boolean isFirstLogin() {
        return (mIsFirstLogin != null && mIsFirstLogin.booleanValue());
    }

    /**
     * Get the status.
     * 
     * @return Status.
     */
    public Status getStatus() {
        return mStatus;
    }

    /**
     * Status
     */
    public void clearStatus() {
        mStatus = null;
    }

    /**
     * Get grace period expire time if any.
     * 
     * @return Grace period expire time or {@code null} if not available.
     */
    public Date getGracePeriodExpireTime() {
        return mGracePeriodExpireTime;
    }

    /**
     * Set the grace period expire time.
     * 
     * @param time Time to set.
     */
    public void setGracePeriodExpireTime(Date time) {
        mGracePeriodExpireTime = time;
    }

    /**
     * Get the configured check period of email grace period.
     * 
     * @return Check period in days.
     */
    public int getGracePeriodCheckPeriod() {
        return mCheckGracePeriodInDay;
    }

    /**
     * Get refresh token
     * 
     * @return Refresh token or {@code null}.
     */
    public String getRefreshToken() {
        return mRefreshToken;
    }

    /**
     * Get account server uri.
     * 
     * @return Server uri or {@code null}.
     */
    public String getServerUri() {
        return mServerUri;
    }

    /**
     * Set check period of email grace period.
     * 
     * @param checkPeriod Check period in days.
     */
    public void setGracePeriodCheckPeriod(int checkPeriod) {
        mCheckGracePeriodInDay = checkPeriod;
    }

    /**
     * Remove the prefix timestamp of authKey if any.
     * 
     * @param authKey AuthKey to strip.
     * @return Timestamp-stripped authKey.
     */
    private String removeAuthKeyTimeStamp(String authKey) {
        if (authKey.contains(".")) {
            return authKey.substring(authKey.indexOf(".") + 1);
        }
        return authKey;
    }
}
