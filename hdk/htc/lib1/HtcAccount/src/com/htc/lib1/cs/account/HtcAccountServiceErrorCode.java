package com.htc.lib1.cs.account;

import android.util.SparseArray;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * The standard error codes returned in HTTP error status response payload.
 */
public enum HtcAccountServiceErrorCode {

    /**
     * The error code representing an invalid/unknown error code.
     */
    UnknownErrorCode(-1),

    /**
     * 400: "The captcha you supplied are invalid"
     */
    BadCaptcha(3),

    /**
     * 409: "The email address you entered is already in use."
     */
    EmailInUse(4),

    /**
     * 400: {MSG}
     */
    BadRequest(9),

    /**
     * 400: "The credentials you supplied are invalid"
     */
    InvalidCredentials(11),

    /**
     * 400: "The phone number you entered is already in use."
     */
    PhoneNumberInUse(13),

    /**
     * 400:
     * "This handset has exceeded the maximum number of allowed account creation requests."
     */
    MaxCreateAccoutRequestsExceeded(16),

    /**
     * 400:
     * "The device for this account is not connected, or you attempting to connect with a different device."
     */
    MissingDevice(17),

    /**
     * 400: "You must verify your email address before logging in."
     */
    EmailNotVerified(18),

    /**
     * 400: "You must verify your handset before logging in."
     */
    HandsetNotVerified(19),

    /**
     * 400:
     * "This account is temporarily locked because the login failed too many times."
     */
    AccountLoginLockedOut(21),

    /**
     * 400: "Your token has expired, please login again."
     */
    TokenExpired(23),

    /**
     * 400: "Your authentication ticket has expired, please login again."
     */
    TicketExpired(25),

    /**
     * 400: {URL}
     */
    WrongDataCenter(26),

    /**
     * 400: "The account you are using has been suspended."
     */
    AccountSuspended(27),

    /**
     * 500: "Unable to communicate with the mail server."
     */
    UnableToSendEmail(28),

    /**
     * 404: {MSG}
     */
    ObjectNotFound(43),

    /**
     * 400:
     * "The account you are attempting to verify has already been verified."
     */
    AccountIsAlreadyVerified(44),

    /**
     * 400:
     * "This reset token is not valid, or it has already been used."
     */
    ResetTokenIsNotValid(54),

    /**
     * 412: "This account need to be migrated."
     */
    MigrationRequired(63),

    /**
     * 400: "Error validating facebook access token"
     */
    InvalidFacebookToken(66),

    /**
     * 400: "Error validating social access token"
     */
    InvalidSocialToken(70),

    /**
     * 400: "Error validating google access token"
     */
    InvalidGoogleToken(76),

    /**
     * 449: "This account creation must be confirmed"
     */
    ConfirmRequired(83),

    /**
     * 200: "Missing/error authkey"
     */
    MissingAuthkey(200),

    /**
     * 400: "The client id you supplied is invalid"
     */
    InvalidClient(1102),

    /**
     * 400: "Bad verification code"
     */
    BadVerificationCode(2101),

    /**
     * 400: "Exceed max retry"
     */
    ExceedMaxRetry(2102),

    /**
     * 400: "Exceed life time"
     */
    ExceedLifeTime(2103),

    /**
     * 400: "Exceed resend times"
     */
    ExceedResendTimes(2104),

    /**
     * 400: "Need captcha"
     */
    NeedCaptcha(2105),

    /**
     * 400: "Phone number does not exist"
     */
    PhoneNumberNotExist(2106),

    /**
     * 400: "Please use trust device"
     */
    PleaseUseTrustDevice(2107),

    /**
     * 400: "Send verification code to backup email"
     */
    SendToBackupEmail(2108),

    /**
     * 400: "Need verify account"
     */
    NeedVerifyAccount(2109),

    /**
     * 449: "Please choose one of them. Used in reset password choose one way to reset."
     */
    NeedChooseAndConfirm(2110);

    private static final HtcLogger sLogger = new CommLoggerFactory(HtcAccountServiceErrorCode.class)
            .create();
    private static final SparseArray<HtcAccountServiceErrorCode> sErrorCodeMap = new SparseArray<HtcAccountServiceErrorCode>();
    static {
        for (HtcAccountServiceErrorCode ec : HtcAccountServiceErrorCode.values())
            sErrorCodeMap.append(ec.mValue, ec);
    }

    private int mValue;

    private HtcAccountServiceErrorCode(int value) {
        this.mValue = value;
    }

    /**
     * Set the value manually. This only make sense when the code was not able
     * to map to any known ServiceErrorCode, but you still want to keep the
     * original value for debug.
     * 
     * @param value
     */
    public void setValue(int value) {
        this.mValue = value;
    }

    /**
     * Get the integer value of the error code.
     * 
     * @return Integer value of the error code.
     */
    public int getValue() {
        return this.mValue;
    }

    /**
     * Get the ServiceErrorCode from integer.
     * 
     * @param code
     * @return Corresponding CSErrorCode or UnknownErrorCode if the code maps to
     *         nothing.
     */
    public static HtcAccountServiceErrorCode valueOf(int code) {
        HtcAccountServiceErrorCode err = sErrorCodeMap.get(code);
        if (err == null) {
            sLogger.warning("Unknown service error code: ", code);
            err = HtcAccountServiceErrorCode.UnknownErrorCode;
            err.setValue(code);
        }
        return err;
    }
}
