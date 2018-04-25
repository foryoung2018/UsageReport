
package com.htc.lib1.cs.account;

import android.Manifest.permission;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Constants related to HTC Account. If not mentioned, the constants should
 * apply to all Sense 4+ and later HEP devices, and all GEP devices.
 * 
 * @author samael_wang@htc.com
 */
public class HtcAccountDefs {
    /**
     * The lib version number that begin from HtcAccount v8.40 behavior.
     *
     * 1: app sign-in support.
     */
    public static final String LIB_VERSION = "1";

    /**
     * The default identity server URI to use.
     */
    public static final String DEFAULT_SERVER_URI = "https://www.htcsense.com/$SS$/";

    /**
     * The default htc identity server URI to use.
     */
    public static final String DEFAULT_HTC_SERVER_URI = "https://www.htc.com/";

    /** General prefix for intent extra keys and actions. */
    public static final String GENERAL_PREFIX = "com.htc.cs";

    /**
     * General prefix including .identity section for intent extra keys and
     * actions.
     */
    public static final String GENERAL_PREFIX_IDENTITY = "com.htc.cs.identity";

    /** The package name of identity client HMS implementation since Sense 7.0. */
    public static final String PKG_NAME_IDENTITY_CLIENT = "com.htc.cs.identity";

    /**
     * The package name of legacy identity client preloaded on devices prior and
     * include Sense 6.0.
     */
    public static final String PKG_NAME_LEGACY_IDENTITY_CLIENT = "com.htc.cs";

    /****************************************************************************************
     * Account type / subtypes - the account type of HTC Account and the
     * subtypes stored in user data to indicate which integrated social account
     * type did the user signed in with.
     ****************************************************************************************/

    /** Type of HTC Account. */
    public static final String TYPE_HTC_ACCOUNT = "com.htc.cs";

    /**
     * Subtype indicating the signed in account is a regular HTC Account. Note
     * that subtypes are only available on Sense 5.0 and later versions for HEP
     * devices, and all versions on GEP devices.
     */
    public static final String ACCOUNT_SUB_TYPE_DEFAULT = "com.htc.cs";

    /**
     * Indicates the HTC native account was registered via email. Note that
     * native account subtypes are only available on HTC Account 8.0 or later.
     */
    public static final String HTC_NATIVE_ACCOUNT_SUB_TYPE_EMAIL = "email";

    /**
     * Indicates the HTC native account was registered via phone number. Note
     * that native account subtypes are only available on HTC Account 8.0 or
     * later.
     */
    public static final String HTC_NATIVE_ACCOUNT_SUB_TYPE_PHONE = "phone";

    /**
     * Subtype indicating the signed in account is a Facebook associated
     * account. Presents on Sense 5.0 and later versions for HEP devices, and
     * all versions on GEP devices.
     */
    public static final String ACCOUNT_SUB_TYPE_FACEBOOK = "com.facebook.auth.login";

    /**
     * Subtype indicating the signed in account is a Google+ associated account.
     * It presents on Sense 5+ and later versions for HEP devices and all
     * versions on GEP devices.
     */
    public static final String ACCOUNT_SUB_TYPE_GOOGLE = "com.google";

    /**
     * Subtype indicating the signed in account is a Steam associated account.
     * It presents on Sense 8 and later versions for HEP devices and all
     * versions on GEP devices.
     */
    public static final String ACCOUNT_SUB_TYPE_STEAM = "com.valvesoftware.android.steam";

    /**
     * Subtype indicating the signed in account is a virtual account associated
     * account, which could be either Sina Weibo or QQ account. Only presents on
     * HEP devices with Sense 5.0 and later versions. GEP devices won't use this
     * subtype.
     */
    public static final String ACCOUNT_SUB_TYPE_VIRTUAL_ACCOUNT = "com.htc.cn.virtual";

    /**
     * Indicates the virtual account was associated to a Sina Weibo account.
     */
    public static final String VIRTUAL_ACCOUNT_SUB_TYPE_SINA = "weibo";

    /**
     * Indicates the virtual account was associated to a QQ account.
     */
    public static final String VIRTUAL_ACCOUNT_SUB_TYPE_QQ = "qq";

    /**
     * Login type indicating the signed in account is not associate any type of
     * account. It presents on Sense 8 and later versions for HEP devices and
     * all version on GEP devices.
     */
    public static final int ACCOUNT_LOGIN_TYPE_UNKOWN = -1;

    /**
     * Login type indicating the signed in account is a email associated
     * account. It presents on Sense 8 and later versions for HEP devices and
     * all version on GEP devices.
     */
    public static final int ACCOUNT_LOGIN_TYPE_EMAIL = 0;

    /**
     * Login type indicating the signed in account is a phone number associated
     * account. It presents on Sense 8 and later versions for HEP devices and
     * all version on GEP devices.
     */
    public static final int ACCOUNT_LOGIN_TYPE_PHONE_NUMBER = 1;

    /****************************************************************************************
     * addAccount options - the keys and values which can be put into a bundle
     * and passed as the option when invoking
     * {@code AccountManager.addAccount()}.
     ****************************************************************************************/

    /** Key of request type passed in. */
    public static final String KEY_REQUEST_TYPE = "requestType";

    /**
     * Request to sign-in or sign-up. It causes identity-client to show a
     * landing page with all sign-in / sign-up options available. It's also the
     * default option if no request type is given.
     * 
     * @see #KEY_REQUEST_TYPE
     */
    public static final String TYPE_SIGN_IN_SIGN_UP = "signInSignUp";

    /**
     * Request to sign-in to an HTC Account directly. It causes identity-client
     * to show sign-in page directly. No landing page will be shown. It's only
     * available on HEP devices.
     * 
     * @see #KEY_REQUEST_TYPE
     */
    public static final String TYPE_SIGN_IN = "signIn";

    /**
     * Request to sign-in with HTC Account or any supported social network
     * integrated account. The only difference between {@link #TYPE_SIGN_IN_EXT}
     * and {@link #TYPE_SIGN_IN_SIGN_UP} is that the later option has create
     * account link. It's only available on HEP devices since Sense 5.0.
     * 
     * @see #KEY_REQUEST_TYPE
     */
    public static final String TYPE_SIGN_IN_EXT = "signInExt";

    /**
     * Request to sign-in with Steam account directly. Supported on HEP
     * devices only since HTC Identity client v8.1.
     * 
     * @see #KEY_REQUEST_TYPE
     */
    public static final String TYPE_SIGN_IN_STEAM = "signInSteam";

    /**
     * Request to sign-in with Sina account directly. Supported on HEP China
     * devices only.
     * 
     * @see #KEY_REQUEST_TYPE
     */
    public static final String TYPE_SIGN_IN_SINA = "signInSina";

    /**
     * Request to sign-in with QQ account directly. Supported on HEP China
     * devices only since Sense 5.0.
     * 
     * @see #KEY_REQUEST_TYPE
     */
    public static final String TYPE_SIGN_IN_QQ = "signInQQ";

    /**
     * Request to sign-in with Facebook account directly. Only available on HEP
     * devices since Sense 5.0.
     * 
     * @see #KEY_REQUEST_TYPE
     */
    public static final String TYPE_SIGN_IN_FACEBOOK = "signInFacebook";

    /**
     * Request to sign-in with Google account directly. Only available on HEP
     * devices since Sense 5+.
     * 
     * @see #KEY_REQUEST_TYPE
     */
    public static final String TYPE_SIGN_IN_GOOGLE = "signInGoogle";

    /**
     * Request to sign-up with all account type directly. Only available on HEP
     * devices since Sense 7+.
     * 
     * @see #KEY_REQUEST_TYPE
     */
    public static final String TYPE_SIGN_UP_ONLY = "signUpOnly";

    /**
     * Request to sign-in / sign-up with custom login options. The options must
     * be passed as an integer with key {@link #KEY_CUSTOM_LOGIN_OPTIONS} when
     * using this request type. The behavior is undefined if no given login
     * options. The type is supported since Sense 6.0.
     * 
     * @see #KEY_REQUEST_TYPE
     */
    public static final String TYPE_CUSTOM = "custom";

    /**
     * Indicate what options to show when request type is {@link #TYPE_CUSTOM}.
     * The value should be an integer composed of one or more flags. The flags
     * include {@link #FLAG_LOGIN_OPTION_SIGNIN_HTC_ACCOUNT},
     * {@link #FLAG_LOGIN_OPTION_SIGNIN_SINA_ACCOUNT},
     * {@link #FLAG_LOGIN_OPTION_SIGNIN_FACEBOOK_ACCOUNT},
     * {@link #FLAG_LOGIN_OPTION_SIGNIN_GOOGLE_ACCOUNT} and
     * {@link #FLAG_LOGIN_OPTION_SIGNIN_QQ_ACCOUNT}.
     */
    public static final String KEY_CUSTOM_LOGIN_OPTIONS = "customLoginOptions";

    /**
     * Indicates user has accepted the terms and conditions of HTC Account and
     * it's safe to login user directly without showing landing page and legal
     * document links. This option is available since Sense 7.0.
     */
    public static final String KEY_TOS_ACCEPTED = "tosAccepted";

    /**
     * Flag for "Sign in with HTC Account" option.
     * 
     * @see #KEY_CUSTOM_LOGIN_OPTIONS
     */
    public static final int FLAG_LOGIN_OPTION_SIGNIN_HTC_ACCOUNT = 0x1;

    /**
     * Flag for "Sign in with Sina" option.
     * 
     * @see #KEY_CUSTOM_LOGIN_OPTIONS
     */
    public static final int FLAG_LOGIN_OPTION_SIGNIN_SINA_ACCOUNT = 0x1 << 1;

    /**
     * Flag for "Sign in with Facebook" option.
     * 
     * @see #KEY_CUSTOM_LOGIN_OPTIONS
     */
    public static final int FLAG_LOGIN_OPTION_SIGNIN_FACEBOOK_ACCOUNT = 0x1 << 2;

    /**
     * Flag for "Sign in with Google" option.
     * 
     * @see #KEY_CUSTOM_LOGIN_OPTIONS
     */
    public static final int FLAG_LOGIN_OPTION_SIGNIN_GOOGLE_ACCOUNT = 0x1 << 3;

    /**
     * Flag for "Sign in with QQ" option.
     * 
     * @see #KEY_CUSTOM_LOGIN_OPTIONS
     */
    public static final int FLAG_LOGIN_OPTION_SIGNIN_QQ_ACCOUNT = 0x1 << 4;

    /**
     * Flag for "Sign up account with all" option.
     * 
     * @see #KEY_CUSTOM_LOGIN_OPTIONS
     */
    public static final int FLAG_LOGIN_OPTION_SIGNUP_ACCOUNT = 0x1 << 5;

    /**
     * Flag for "Sign in with steam" option.
     * 
     * @see #KEY_CUSTOM_LOGIN_OPTIONS
     */
    public static final int FLAG_LOGIN_OPTION_SIGNIN_STEAM_ACCOUNT = 0x1 << 6;

    /**
     * Key of email address passed in for pre-filling. The corresponding value
     * should be a string indicating an email address. The option is only
     * available on HEP devices and is only applicable for regular HTC Accounts.
     */
    public static final String KEY_EMAIL_ADDRESS = "emailAddress";

    /**
     * Describe the lifetime of the account to add. Either
     * {@link #ACCOUNT_LIFETIME_ONETIME} or {@link #ACCOUNT_LIFETIME_PERSISTENT}
     * and the default value is {@link #ACCOUNT_LIFETIME_PERSISTENT}. This
     * option is available since Sense 6.0.
     */
    public static final String KEY_ACCOUNT_LIFETIME = "accountLifeTime";

    /**
     * Indicates the caller wants a one-time authtoken only without actually
     * adding an account.
     * <p>
     * When adding a one-time account, the access token will be returned back to
     * the caller but the account won't be added to {@link AccountManager}, and
     * won't show in Account & Sync consequently. Caller can use the authtoken
     * to access HTC Services but since the account it's not added to the
     * system, the caller won't be able to use
     * {@code AccountManager.getAuthToken()} to retrieve the authtoken.
     * </p>
     * <p>
     * Identity client accepts the request for one-time account even if there
     * already exists an HTC Account in the system.
     * </p>
     * 
     * @see #KEY_ACCOUNT_LIFETIME
     */
    public static final String ACCOUNT_LIFETIME_ONETIME = "oneTime";

    /**
     * Indicates the caller is requesting for a regular add account operation,
     * which is also the default behavior. The account will be added to the
     * system, show in Account & Sync and the caller can later use
     * {@code AccountManager.getAuthToken()} to retrieve the authtoken.
     * 
     * @see #KEY_ACCOUNT_LIFETIME
     */
    public static final String ACCOUNT_LIFETIME_PERSISTENT = "persistent";

    /****************************************************************************************
     * addAccount return extras - in addition to the standard return values such
     * as account name / type, identity client adds some extras when
     * {@code AccountManager.addAccount()} returns. The definition of those keys
     * / values can be found here.
     ****************************************************************************************/

    /**
     * The identity service authkey in Sense 6.0 or the access token in Sense
     * 7.0 and later. The reason of not using
     * {@link AccountManager#KEY_AUTHTOKEN} is that AccountManagerService forces
     * strip authtoken from addAccount() response for security reason as
     * addAccount() requires only {@link permission#MANAGE_ACCOUNTS} but not
     * {@link permission#USE_CREDENTIALS}. The workaround in identity client is
     * using {@link #KEY_AUTHKEY}. Note that identity client checks for caller
     * permission at runtime and only return authkey if caller holds
     * {@link permission#USE_CREDENTIALS}.
     */
    public static final String KEY_AUTHKEY = "authkey";

    /****************************************************************************************
     * getAuthToken options - the options identity client accepts when invoking
     * {@code AccountManager.getAuthToken()}.
     ****************************************************************************************/

    /** Default HTC Account authToken type. */
    public static final String AUTHTOKEN_TYPE_DEFAULT = "default";

    /** The HTC Account refresh token type. */
    public static final String AUTHTOKEN_TYPE_REFRESH_TOKEN = "refreshToken";

    public static final String AUTHTOKEN_TYPE_SOCIAL_SINA_ACCOUNT = "com.htc.cn.social.weibo";

    /** The key of bundle field, which contains the id of integrated app. */
    public static final String AUTHTOKEN_KEY_APP_ID = "integratedAppId";

    /** The key of bundle field, which contains the client id of integrated app. */
    public static final String AUTHTOKEN_KEY_APP_CLIENT_ID = "integratedAppClientId";

    /**
     * The key of bundle field, which contains the scopes, separated by spaces,
     * of integrated app.
     */
    public static final String AUTHTOKEN_KEY_APP_SCOPES = "integratedAppScope";

    /** The key of bundle field, which contains the secret of integrated app. */
    public static final String AUTHTOKEN_KEY_APP_SECRET = "integratedAppSecret";

    /** The key of bundle field, which indicate to invalidate the social token. */
    public static final String AUTHTOKEN_KEY_INVALIDATE_SOCIAL_TOKEN = "invalidateToken";

    /**
     * The key of bunlde field, it is used for auto-notifying user when
     * auth-failed. This key is annotated as hide in frameworks' code. We should
     * be careful to use.Boolean, if set and 'customTokens' the authenticator is
     * responsible for notifications.
     */
    public static final String KEY_NOTIFY_ON_FAILURE = "notifyOnAuthFailure";

    /****************************************************************************************
     * confirmCredentials options - the options identity client accepts when
     * invoking {@code AccountManager.confirmCredentials}.
     ****************************************************************************************/

    /**
     * Key of validation period. The corresponding value is time in
     * milliseconds. The default value, if not given, is 0.
     */
    public static final String KEY_VALIDATION_PERIOD = "validationPeriod";

    /****************************************************************************************
     * User data - the definition of available keys stored as user data and the
     * content provider URIs. Those values can be queried from
     * {@code UserDataProvider}.
     ****************************************************************************************/

    /**
     * Permission to access user data provider.
     */
    public static final String PERMISSION_ACCESS_USER_DATA =
            GENERAL_PREFIX_IDENTITY + ".permission.ACCESS_USER_DATA";

    /**
     * Content URI for user data provider used in Sense 6.0 and later versions.
     */
    public static final Uri CONTENT_URI_USER_DATA_PROVIDER = Uri
            .parse("content://com.htc.cs.identity.provider.userdata/userdata");

    /**
     * Content URI for user data provider used in Sense 4+ to Sense 5+.
     */
    public static final Uri CONTENT_URI_LEGACY_USER_DATA_PROVIDER = Uri
            .parse("content://com.htc.cs.identity.providers/userdata");

    /**
     * The content URI format string used to compose user data provider URI in
     * local account implementation. (i.e. the path for library-implemented HTC
     * Account Android SDK). {@code %s} should be replaced with app package
     * name.
     */
    public static final String CONTENT_URI_STRING_LOCAL_USER_DATA_PROVIDER = "content://%s.identity.provider.userdata/userdata";

    /**
     * The column name of keys in user data provider.
     */
    public static final String COLUMN_KEY = "key";

    /**
     * The column name of values in user data provider.
     */
    public static final String COLUMN_VALUE = "value";

    /** Key of account id used in user data. Available since Sense 5+. */
    public static final String KEY_ACCOUNT_ID = "accountId";

    /** Key of server URI used in userData of AccountManager. */
    public static final String KEY_SERVER_URI = "serverUri";

    /** The auth server URI when creating a client account. */
    public static final String KEY_AUTH_SERVER_URI = "authServerUri";

    /** Key of profile server URI used in userData of AccountManager. */
    public static final String KEY_PROFILE_SERVER_URI = "profileServerUri";

    /** Key of avatar server URI used in userData of AccountManager. */
    public static final String KEY_AVATAR_SERVER_URI = "avatarServerUri";

    /** Key of authentication timestamp used in userData of AccoutManager. */
    public static final String KEY_AUTH_TIMESTAMP = "authTimestamp";

    /** Key of email verification status used in userData of AccountManager. */
    public static final String KEY_IS_EMAIL_VERIFIED = "isEmailVerified";

    /** Key of country code used in userData of AccountManager. */
    public static final String KEY_COUNTRY_CODE = "countryCode";

    /** Key of account sub-type. */
    public static final String KEY_ACCOUNT_SUB_TYPE = "accountSubType";

    /** Key of virtual account sub-type. Only available on GEP. */
    public static final String KEY_VIRTUAL_ACCOUNT_SUB_TYPE = "virtualAccountSubType";

    /** Key of htc native account sub-type. Started since HTC Account 8.0. */
    public static final String KEY_HTC_NATIVE_ACCOUNT_SUB_TYPE = "htcNativeAccountSubType";

    /** Key of account login type. */
    public static final String KEY_ACCOUNT_LOGIN_TYPE = "accountLoginType";

    /** True if a local account is authorized by client app. False if it's by WebView. */
    public static final String KEY_LOCAL_ACCOUNT_AUTH_BY_CLIENT = "localAcountAuthByClient";

    /**
     * Key of region id used in user data. It's not a necessary field, but only
     * used in some social account associated cases such as Google+ or Facebook.
     */
    public static final String KEY_REGION_ID = "regionId";

    /**
     * Key of social account name in user data, if user was signed in with
     * Google.
     */
    public static final String KEY_SOCIAL_ACCOUNT_NAME = "socialAccountName";

    /**
     * Key of virtual account uid in user data, if user was signed in with
     * virtual account (i.e. Sina / QQ on HEP).
     */
    public static final String KEY_VIRTUAL_ACCOUNT_UID = "virtualAccountUid";

    /**
     * Key of virtual account token in user data, if user was signed in with
     * virtual account (i.e. Sina / QQ on HEP).
     */
    public static final String KEY_VIRTUAL_ACCOUNT_TOKEN = "virtualAccountToken";

    /**
     * Key of Facebook user id in user data, if user was signed in with
     * Facebook.
     */
    public static final String KEY_FACEBOOK_UID = "facebookUid";

    /** Key of Google+ user id in user data, if user was signed in with Google. */
    public static final String KEY_GOOGLE_UID = "googlePlusUid";

    /**
     * Key of Sina Weibo user id in user data, if user was signed in with Sina
     * account on GEP.
     */
    public static final String KEY_SINA_UID = "sinaUid";

    /**
     * Key of Sina Weibo access token, if user was signed in with Sina account
     * on GEP.
     */
    public static final String KEY_SINA_TOKEN = "sinaToken";

    /**
     * Key of Sina Weibo screen name, if user was signed in with Sina account on
     * GEP.
     */
    public static final String KEY_SINA_NAME = "sinaName";

    /**
     * Key of QQ user id in user data, if user was signed in with QQ account on
     * GEP.
     */
    public static final String KEY_QQ_UID = "qqUid";

    /**
     * Key of QQ access token, if user was signed in with QQ account on GEP.
     */
    public static final String KEY_QQ_TOKEN = "qqToken";

    /**
     * Key of a boolean value stored as a String. If presents, it indicates if
     * we can enable autoBackup for this user. This happens only if user signs
     * in through the HTC Care flow in which we clearly stated backup feature in
     * landing page.
     */
    public static final String KEY_AUTOBACKUP = "autoBackup";

    /**
     * Key of account tag for application sign-in feature.
     */
    public static final String KEY_ACCOUNT_TAG = "accountTag";

    /****************************************************************************************
     * Intent actions - the actions used in HTC Account intents.
     ****************************************************************************************/

    /**
     * Action for initiate sign-in / sign-up flow without going through
     * AccountManager. It's mainly for OOBE integration.
     */
    public static final String ACTION_ADD_ACCOUNT = GENERAL_PREFIX_IDENTITY + ".ADD_ACCOUNT";

    /** Postfix part of action to show terms and conditions. */
    public static final String POSTFIX_ACTION_SHOW_TOS = ".SHOW_TOS";

    /** Postfix part of action to show privacy policy. */
    public static final String POSTFIX_ACTION_SHOW_PRIVACY_POLICY = ".SHOW_PRIVACY_POLICY";

    /** Postfix part of action to show learn more. */
    public static final String POSTFIX_ACTION_SHOW_LEARN_MORE = ".SHOW_LEARN_MORE";

    /** Action to show terms and conditions. */
    public static final String ACTION_SHOW_TOS = GENERAL_PREFIX + POSTFIX_ACTION_SHOW_TOS;

    /** Action to show privacy policy. */
    public static final String ACTION_SHOW_PRIVACY_POLICY = GENERAL_PREFIX
            + POSTFIX_ACTION_SHOW_PRIVACY_POLICY;

    /** Action to show sign in with account confirm page. */
    public static final String ACTION_SIGN_IN_ACCOUNT_CONFIRM = GENERAL_PREFIX_IDENTITY
            + ".SIGN_IN_ACCOUNT_CONFIRM";

    /** Action to find app implements {@link SignedInAccountAppProvider} */
    public static final String ACTION_SIGNED_IN_APP = GENERAL_PREFIX_IDENTITY + ".SIGNED_IN_APP";

    /****************************************************************************************
     * Intent extras - the key of intent extras used in HTC Account intents.
     ****************************************************************************************/

    /**
     * Source service is used to identify which integrated service initiates the
     * flow. It's usually the package name of the application but could
     * sometimes append a subname to distinguish details. The format is
     * {@code package-name[:subname]}, for example, {@code com.htc.backup} or
     * {@code com.htc.cs:confrim-credentials}.
     */
    public static final String KEY_SOURCE_SERVICE =
            GENERAL_PREFIX_IDENTITY + ".SOURCE_SERVICE";

    /**
     * Package name indicating which application initiates the flow. It's the
     * package part of the source service name.
     */
    public static final String KEY_SOURCE_PACKAGE =
            GENERAL_PREFIX_IDENTITY + ".SOURCE_PACKAGE";

    /**
     * Specify URL fragment identifier (anchor) when using
     * {@link #ACTION_SHOW_TOS}. The identifier should include the hash symbol
     * prefix.
     */
    public static final String KEY_URL_FRAGMENT_IDENTIFIER =
            GENERAL_PREFIX_IDENTITY + ".URL_FRAGMENT_IDENTIFIER";

    /**
     * If a caller would like to invoke HTC Account through
     * {@link #ACTION_ADD_ACCOUNT}, all the add account options should be put in
     * an extra {@link Bundle} with this key when setup the {@link Intent}.
     */
    public static final String KEY_ADD_ACCOUNT_OPTIONS =
            HtcAccountDefs.GENERAL_PREFIX_IDENTITY + ".ADD_ACCOUNT_OPTIONS";

    /****************************************************************************************
     * App sign-in content URI
     ****************************************************************************************/

    /**
     * HtcAccountLib internal use only.
     */
    public static final String SIGNIN_PROVIDER_AUTHORITY = "com.htc.cs.identity.provider.signed_in";

    /**
     * HtcAccountLib internal use only.
     */
    public static final String SIGNIN_PROVIDER_PATH_SIGNIN_TIMESTAMP = "signin_time";

    /**
     * HtcAccountLib internal use only.
     * Content URI for user data provider used in Sense 6.0 and later versions.
     */
    public static final Uri CONTENT_URI_SIGNIN_PROVIDER = Uri
            .parse("content://com.htc.cs.identity.provider.signed_in/"
                    + SIGNIN_PROVIDER_PATH_SIGNIN_TIMESTAMP);

    /**
     * HtcAccountLib internal use only.
     * Key of sign-in timestamp, used for sign-in app.
     */
    public static final String KEY_SIGNIN_TIMESTAMP = "signInTag";

    /**
     * HtcAccountLib internal use only.
     * Method to check whether app is signed in with HTC Account.
     */
    public static final String METHOD_IS_SIGNED_IN = "isAccountSignedIn";

    /**
     * HtcAccountLib internal use only.
     * Key for {@link SignedInAccountAppProvider#isSignedIn()} method result.
     */
    public static final String KEY_IS_SIGNED_IN = "is_account_signed_in";
}
