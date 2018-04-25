
package com.htc.lib1.cs.account;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

/**
 * {@link AccountManager}-like interface to operate on HTC Accounts. Depends on
 * the system configuration, an instance should either has type
 * {@link HtcAccountManagerTypes#TYPE_LOCAL} or
 * {@link HtcAccountManagerTypes#TYPE_SYSTEM}.
 * 
 * @author samael_wang@htc.com
 */
public abstract class HtcAccountManager {
    public static final String VERSION_NOT_AVAILABLE = "NA";

    /**
     * The types of {@link HtcAccountManager} instances.
     * 
     * @author samael_wang@htc.com
     */
    public static class HtcAccountManagerTypes {
        /**
         * Indicates the {@link HtcAccountManager} works on local accounts
         * stored at application's local database.
         */
        public static final String TYPE_LOCAL = "local";

        /**
         * Indicates the {@link HtcAccountManager} works on a system accounts
         * through Android {@link AccountManager}.
         */
        public static final String TYPE_SYSTEM = "system";
    }

    /**
     * Gets the saved password associated with the account. This is intended for
     * authenticators and related code; applications should get an auth token
     * instead.
     * <p>
     * In order to be compatible with {@link AccountManager} interface, this
     * method can be invoked from the main thread. However, it involves database
     * operations, and SQLite doesn't support simultaneous accessing from
     * multiple processes. Hence, if responsiveness is critical for your app, or
     * your app needs to call this method in a sub-process, use
     * {@link #asyncGetPassword(Account, com.htc.cs.comm.account.DataServiceFuture.DataServiceCallback, Handler)}
     * instead.
     * <p>
     * This method requires the caller to hold the permission
     * {@link android.Manifest.permission#AUTHENTICATE_ACCOUNTS} and to have the
     * same UID as the account's authenticator.
     * 
     * @param account The account to query for a password
     * @return The account's password, null if none or if the account doesn't
     *         exist
     */
    public abstract String getPassword(final Account account);

    /**
     * Asynchronized implementation of {@link #getPassword(Account)} which
     * guarantees the database operation is in a background thread on main
     * process no matter what the calling process / thread is.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link DataServiceFuture} must not be used on the main thread.
     * 
     * @param account The account to query for a password
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link DataServiceFuture} which resolves to a {@link String}.
     */
    public abstract DataServiceFuture<String> asyncGetPassword(final Account account,
            final DataServiceFuture.DataServiceCallback<String> callback, final Handler handler);

    /**
     * Gets the user data named by "key" associated with the account. This is
     * intended for authenticators and related code to store arbitrary metadata
     * along with accounts. The meaning of the keys and values is up to the
     * authenticator for the account.
     * <p>
     * In order to be compatible with {@link AccountManager} interface, this
     * method can be invoked from the main thread. However, it involves database
     * operations, and SQLite doesn't support simultaneous accessing from
     * multiple processes. Hence, if responsiveness is critical for your app, or
     * your app needs to call this method in a sub-process, use
     * {@link #asyncGetUserData(Account, String, com.htc.cs.comm.account.DataServiceFuture.DataServiceCallback, Handler)}
     * instead.
     * <p>
     * This method requires the caller to hold the permission
     * {@link android.Manifest.permission#AUTHENTICATE_ACCOUNTS} and to have the
     * same UID as the account's authenticator.
     * 
     * @param account The account to query for user data
     * @param key The key of the user data to query
     * @return The user data, null if the account or key doesn't exist
     */
    public abstract String getUserData(final Account account, final String key);

    /**
     * Asynchronized implementation of {@link #getUserData(Account, String)}
     * which guarantees the database operation is in a background thread on main
     * process no matter what the calling process / thread is.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link DataServiceFuture} must not be used on the main thread.
     * 
     * @param account The account to query for user data
     * @param key The key of the user data to query
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link DataServiceFuture} which resolves to a {@link String}.
     */
    public abstract DataServiceFuture<String> asyncGetUserData(final Account account,
            final String key, final DataServiceFuture.DataServiceCallback<String> callback,
            final Handler handler);

    /**
     * Lists all HTC Accounts registered in {@link #HtcAccountManager}. It has a
     * different manner from {@link AccountManager#getAccounts()}. It's
     * equivalent to {@code getAccountsByType(HtcAccountDefs.TYPE_HTC_ACCOUNT)}
     * .
     * <p>
     * In order to be compatible with {@link AccountManager} interface, this
     * method can be invoked from the main thread. However, it involves database
     * operations, and SQLite doesn't support simultaneous accessing from
     * multiple processes. Hence, if responsiveness is critical for your app, or
     * your app needs to call this method in a sub-process, use
     * {@link #asyncGetAccounts(com.htc.cs.comm.account.DataServiceFuture.DataServiceCallback, Handler)}
     * instead.
     * <p>
     * This method requires the caller to hold the permission
     * {@link android.Manifest.permission#GET_ACCOUNTS}.
     * 
     * @return An array of {@link Account}, one per matching account. Empty
     *         (never null) if no HTC Accounts have been added.
     */
    public final Account[] getAccounts() {
        return getAccountsByType(HtcAccountDefs.TYPE_HTC_ACCOUNT);
    }

    /**
     * Asynchronized implementation of {@link #getAccounts()} which guarantees
     * the database operation is in a background thread on main process no
     * matter what the calling process / thread is.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link DataServiceFuture} must not be used on the main thread.
     * 
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link DataServiceFuture} which resolves to an array of
     *         {@link Account}.
     */
    public final DataServiceFuture<Account[]> asyncGetAccounts(
            final DataServiceFuture.DataServiceCallback<Account[]> callback, final Handler handler) {
        return asyncGetAccountsByType(HtcAccountDefs.TYPE_HTC_ACCOUNT, callback, handler);
    }

    /**
     * Lists all accounts of a particular type. The only supported account type
     * is {@link HtcAccountDefs#TYPE_HTC_ACCOUNT}. This method is provided for
     * apps to easily migrate from existing {@link AccountManager}
     * implementation. For new implementations, use {@link #getAccounts()}
     * instead.
     * <p>
     * In order to be compatible with {@link AccountManager} interface, this
     * method can be invoked from the main thread. However, it involves database
     * operations, and SQLite doesn't support simultaneous accessing from
     * multiple processes. Hence, if responsiveness is critical for your app, or
     * your app needs to call this method in a sub-process, use
     * {@link #asyncGetAccountsByType(String, com.htc.cs.comm.account.DataServiceFuture.DataServiceCallback, Handler)}
     * instead.
     * <p>
     * This method requires the caller to hold the permission
     * {@link android.Manifest.permission#GET_ACCOUNTS}.
     * 
     * @param type The type of accounts to return. The only supported account
     *            type is {@link HtcAccountDefs#TYPE_HTC_ACCOUNT}.
     * @return An array of {@link Account}, one per matching account. Empty
     *         (never null) if no accounts of the specified type have been
     *         added.
     */
    public abstract Account[] getAccountsByType(final String type);

    /**
     * Asynchronized implementation of {@link #getAccountsByType(String)} which
     * guarantees the database operation is in a background thread on main
     * process no matter what the calling process / thread is.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link DataServiceFuture} must not be used on the main thread.
     * 
     * @param type The type of accounts to return. The only supported account
     *            type is {@link HtcAccountDefs#TYPE_HTC_ACCOUNT}.
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return A {@link DataServiceFuture} which resolves to an array of
     *         {@link Account}.
     */
    public abstract DataServiceFuture<Account[]> asyncGetAccountsByType(final String type,
            final DataServiceFuture.DataServiceCallback<Account[]> callback, final Handler handler);

    /**
     * Adds an account directly to the {@link HtcAccountManager}. Normally used
     * by sign-up wizards associated with authenticators, not directly by
     * applications.
     * <p>
     * In order to be compatible with {@link AccountManager} interface, this
     * method can be invoked from the main thread. However, it involves database
     * operations, and SQLite doesn't support simultaneous accessing from
     * multiple processes. Hence, if responsiveness is critical for your app, or
     * your app needs to call this method in a sub-process, use
     * {@link #asyncAddAccountExplicitly(Account, String, Bundle, com.htc.cs.comm.account.DataServiceFuture.DataServiceCallback, Handler)}
     * instead.
     * <p>
     * This method requires the caller to hold the permission
     * {@link android.Manifest.permission#AUTHENTICATE_ACCOUNTS} and to have the
     * same UID as the added account's authenticator.
     * 
     * @param account The {@link Account} to add
     * @param password The password to associate with the account, null for none
     * @param userdata String values to use for the account's userdata, null for
     *            none
     * @return {@code true} if the account was successfully added, {@code false}
     *         if the account already exists, the account is null, or another
     *         error occurs.
     */
    public abstract boolean addAccountExplicitly(final Account account, final String password,
            final Bundle userdata);

    /**
     * Asynchronized implementation of
     * {@link #addAccountExplicitly(Account, String, Bundle)} which guarantees
     * the database operation is in a background thread on main process no
     * matter what the calling process / thread is.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link DataServiceFuture} must not be used on the main thread.
     * 
     * @param account The {@link Account} to add
     * @param password The password to associate with the account, null for none
     * @param userdata String values to use for the account's userdata, null for
     *            none
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link DataServiceFuture} which resolves to a {@link Boolean}.
     */
    public abstract DataServiceFuture<Boolean> asyncAddAccountExplicitly(final Account account,
            final String password, final Bundle userdata,
            final DataServiceFuture.DataServiceCallback<Boolean> callback, final Handler handler);

    /**
     * Removes an account from the {@link HtcAccountManager}. Does nothing if
     * the account does not exist. Does not delete the account from the server.
     * The authenticator may have its own policies preventing account deletion,
     * in which case the account will not be deleted.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link AccountManagerFuture} must not be used on the main thread.
     * <p>
     * This method requires the caller to hold the permission
     * {@link android.Manifest.permission#MANAGE_ACCOUNTS}.
     * 
     * @param account The {@link Account} to remove
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link AccountManagerFuture} which resolves to a Boolean, true
     *         if the account has been successfully removed, false if the
     *         authenticator forbids deleting this account.
     */
    public abstract AccountManagerFuture<Boolean> removeAccount(final Account account,
            final AccountManagerCallback<Boolean> callback, final Handler handler);

    /**
     * Overloaded method for convenience. Equivalent to
     * {@code invalidateAuthToken(HtcAccountDefs.TYPE_HTC_ACCOUNT, authToken)}.
     * <p>
     * This method can be invoked from the main thread. However, it involves
     * database operations, and SQLite doesn't support simultaneous accessing
     * from multiple processes. Hence, if responsiveness is critical for your
     * app, or your app needs to call this method in a sub-process, use
     * {@link #asyncInvalidateAuthToken(String, com.htc.cs.comm.account.DataServiceFuture.DataServiceCallback, Handler)}
     * instead.
     */
    public final void invalidateAuthToken(final String authToken) {
        invalidateAuthToken(HtcAccountDefs.TYPE_HTC_ACCOUNT, authToken);
    }

    /**
     * Asynchronized implementation of {@link #invalidateAuthToken(String)}
     * which guarantees the database operation is in a background thread on main
     * process no matter what the calling process / thread is.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link DataServiceFuture} must not be used on the main thread.
     * 
     * @param authToken The auth token to invalidate, may be {@code null}
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link DataServiceFuture} which can be used for block waiting.
     */
    public final DataServiceFuture<Void> asyncInvalidateAuthToken(final String authToken,
            final DataServiceFuture.DataServiceCallback<Void> callback, final Handler handler) {
        return asyncInvalidateAuthToken(HtcAccountDefs.TYPE_HTC_ACCOUNT, authToken, callback,
                handler);
    }

    /**
     * Removes an HTC Account auth token from the {@link HtcAccountManager}'s
     * cache. Does nothing if the auth token is not currently in the cache.
     * Applications must call this method when the auth token is found to have
     * expired or otherwise become invalid for authenticating requests. The
     * {@link HtcAccountManager} does not validate or expire cached auth tokens
     * otherwise.
     * <p>
     * In order to be compatible with {@link AccountManager} interface, this
     * method can be invoked from the main thread. However, it involves database
     * operations, and SQLite doesn't support simultaneous accessing from
     * multiple processes. Hence, if responsiveness is critical for your app, or
     * your app needs to call this method in a sub-process, use
     * {@link #asyncInvalidateAuthToken(String, String, com.htc.cs.comm.account.DataServiceFuture.DataServiceCallback, Handler)}
     * instead.
     * <p>
     * This method requires the caller to hold the permission
     * {@link android.Manifest.permission#MANAGE_ACCOUNTS} or
     * {@link android.Manifest.permission#USE_CREDENTIALS}
     * 
     * @param accountType The account type of the auth token to invalidate, must
     *            not be null. The only supported type is
     *            {@link HtcAccountDefs#TYPE_HTC_ACCOUNT}.
     * @param authToken The auth token to invalidate, may be {@code null}
     */
    public abstract void invalidateAuthToken(final String accountType, final String authToken);

    /**
     * Asynchronized implementation of
     * {@link #invalidateAuthToken(String, String)} which guarantees the
     * database operation is in a background thread on main process no matter
     * what the calling process / thread is.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link DataServiceFuture} must not be used on the main thread.
     * 
     * @param accountType The account type of the auth token to invalidate, must
     *            not be null. The only supported type is
     *            {@link HtcAccountDefs#TYPE_HTC_ACCOUNT}.
     * @param authToken The auth token to invalidate, may be {@code null}
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link DataServiceFuture} which can be used for block waiting.
     */
    public abstract DataServiceFuture<Void> asyncInvalidateAuthToken(final String accountType,
            final String authToken, final DataServiceFuture.DataServiceCallback<Void> callback,
            final Handler handler);

    /**
     * Gets an auth token from the {@link HtcAccountManager}'s cache. If no auth
     * token is cached for this account, null will be returned -- a new auth
     * token will not be generated, and the server will not be contacted.
     * Intended for use by the authenticator, not directly by applications.
     * <p>
     * In order to be compatible with {@link AccountManager} interface, this
     * method can be invoked from the main thread. However, it involves database
     * operations, and SQLite doesn't support simultaneous accessing from
     * multiple processes. Hence, if responsiveness is critical for your app, or
     * your app needs to call this method in a sub-process, use
     * {@link #asyncPeekAuthToken(Account, String, com.htc.cs.comm.account.DataServiceFuture.DataServiceCallback, Handler)}
     * instead.
     * <p>
     * This method requires the caller to hold the permission
     * {@link android.Manifest.permission#AUTHENTICATE_ACCOUNTS} and to have the
     * same UID as the account's authenticator.
     * 
     * @param account The account to fetch an auth token for
     * @param authTokenType The type of auth token to fetch, see {#getAuthToken}
     * @return The cached auth token for this account and type, or null if no
     *         auth token is cached or the account does not exist.
     */
    public abstract String peekAuthToken(final Account account, final String authTokenType);

    /**
     * Asynchronized implementation of {@link #peekAuthToken(Account, String)}
     * which guarantees the database operation is in a background thread on main
     * process no matter what the calling process / thread is.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link DataServiceFuture} must not be used on the main thread.
     * 
     * @param account The account to fetch an auth token for
     * @param authTokenType The type of auth token to fetch, see {#getAuthToken}
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link DataServiceFuture} which resolves to a {@link String}.
     */
    public abstract DataServiceFuture<String> asyncPeekAuthToken(final Account account,
            final String authTokenType,
            final DataServiceFuture.DataServiceCallback<String> callback,
            final Handler handler);

    /**
     * Sets or forgets a saved password. This modifies the local copy of the
     * password used to automatically authenticate the user; it does not change
     * the user's account password on the server. Intended for use by the
     * authenticator, not directly by applications.
     * <p>
     * In order to be compatible with {@link AccountManager} interface, this
     * method can be invoked from the main thread. However, it involves database
     * operations, and SQLite doesn't support simultaneous accessing from
     * multiple processes. Hence, if responsiveness is critical for your app, or
     * your app needs to call this method in a sub-process, use
     * {@link #asyncSetPassword(Account, String, com.htc.cs.comm.account.DataServiceFuture.DataServiceCallback, Handler)}
     * instead.
     * <p>
     * This method requires the caller to hold the permission
     * {@link android.Manifest.permission#AUTHENTICATE_ACCOUNTS} and have the
     * same UID as the account's authenticator.
     * 
     * @param account The account to set a password for
     * @param password The password to set, null to clear the password
     */
    public abstract void setPassword(final Account account, final String password);

    /**
     * Asynchronized implementation of {@link #setPassword(Account, String)}
     * which guarantees the database operation is in a background thread on main
     * process no matter what the calling process / thread is.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link DataServiceFuture} must not be used on the main thread.
     * 
     * @param account The account to set a password for
     * @param password The password to set, null to clear the password
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link DataServiceFuture} which can be used for block waiting.
     */
    public abstract DataServiceFuture<Void> asyncSetPassword(final Account account,
            final String password, final DataServiceFuture.DataServiceCallback<Void> callback,
            final Handler handler);

    /**
     * Forgets a saved password. This erases the local copy of the password; it
     * does not change the user's account password on the server. Has the same
     * effect as setPassword(account, null) but requires fewer permissions, and
     * may be used by applications or management interfaces to "sign out" from
     * an account.
     * <p>
     * In order to be compatible with {@link AccountManager} interface, this
     * method can be invoked from the main thread. However, it involves database
     * operations, and SQLite doesn't support simultaneous accessing from
     * multiple processes. Hence, if responsiveness is critical for your app, or
     * your app needs to call this method in a sub-process, use
     * {@link #asyncClearPassword(Account, com.htc.cs.comm.account.DataServiceFuture.DataServiceCallback, Handler)}
     * instead.
     * <p>
     * This method requires the caller to hold the permission
     * {@link android.Manifest.permission#MANAGE_ACCOUNTS}
     * 
     * @param account The account whose password to clear
     */
    public abstract void clearPassword(final Account account);

    /**
     * Asynchronized implementation of {@link #clearPassword(Account)} which
     * guarantees the database operation is in a background thread on main
     * process no matter what the calling process / thread is.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link DataServiceFuture} must not be used on the main thread.
     * 
     * @param account The account whose password to clear
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link DataServiceFuture} which can be used for block waiting.
     */
    public abstract DataServiceFuture<Void> asyncClearPassword(final Account account,
            final DataServiceFuture.DataServiceCallback<Void> callback, final Handler handler);

    /**
     * Sets one userdata key for an account. Intended by use for the
     * authenticator to stash state for itself, not directly by applications.
     * The meaning of the keys and values is up to the authenticator.
     * <p>
     * In order to be compatible with {@link AccountManager} interface, this
     * method can be invoked from the main thread. However, it involves database
     * operations, and SQLite doesn't support simultaneous accessing from
     * multiple processes. Hence, if responsiveness is critical for your app, or
     * your app needs to call this method in a sub-process, use
     * {@link #asyncSetUserData(Account, String, String, com.htc.cs.comm.account.DataServiceFuture.DataServiceCallback, Handler)}
     * instead.
     * <p>
     * This method requires the caller to hold the permission
     * {@link android.Manifest.permission#AUTHENTICATE_ACCOUNTS} and to have the
     * same UID as the account's authenticator.
     * 
     * @param account The account to set the userdata for
     * @param key The userdata key to set. Must not be null
     * @param value The value to set, null to clear this userdata key
     */
    public abstract void setUserData(final Account account, final String key, final String value);

    /**
     * Asynchronized implementation of
     * {@link #setUserData(Account, String, String)} which guarantees the
     * database operation is in a background thread on main process no matter
     * what the calling process / thread is.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link DataServiceFuture} must not be used on the main thread.
     * 
     * @param account The account to set the userdata for
     * @param key The userdata key to set. Must not be null
     * @param value The value to set, null to clear this userdata key
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link DataServiceFuture} which can be used for block waiting.
     */
    public abstract DataServiceFuture<Void> asyncSetUserData(final Account account,
            final String key, final String value,
            final DataServiceFuture.DataServiceCallback<Void> callback,
            final Handler handler);

    /**
     * Adds an auth token to the {@link HtcAccountManager} cache for an account.
     * If the account does not exist then this call has no effect. Replaces any
     * previous auth token for this account and auth token type. Intended for
     * use by the authenticator, not directly by applications.
     * <p>
     * In order to be compatible with {@link AccountManager} interface, this
     * method can be invoked from the main thread. However, it involves database
     * operations, and SQLite doesn't support simultaneous accessing from
     * multiple processes. Hence, if responsiveness is critical for your app, or
     * your app needs to call this method in a sub-process, use
     * {@link #asyncSetAuthToken(Account, String, String, com.htc.cs.comm.account.DataServiceFuture.DataServiceCallback, Handler)}
     * instead.
     * <p>
     * This method requires the caller to hold the permission
     * {@link android.Manifest.permission#AUTHENTICATE_ACCOUNTS} and to have the
     * same UID as the account's authenticator.
     * 
     * @param account The account to set an auth token for
     * @param authTokenType The type of the auth token, see {#getAuthToken}
     * @param authToken The auth token to add to the cache
     */
    public abstract void setAuthToken(Account account, final String authTokenType,
            final String authToken);

    /**
     * Asynchronized implementation of
     * {@link #setAuthToken(Account, String, String)} which guarantees the
     * database operation is in a background thread on main process no matter
     * what the calling process / thread is.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link DataServiceFuture} must not be used on the main thread.
     * 
     * @param account The account to set an auth token for
     * @param authTokenType The type of the auth token, see {#getAuthToken}
     * @param authToken The auth token to add to the cache
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link DataServiceFuture} which can be used for block waiting.
     */
    public abstract DataServiceFuture<Void> asyncSetAuthToken(final Account account,
            final String authTokenType, final String authToken,
            final DataServiceFuture.DataServiceCallback<Void> callback, final Handler handler);

    /**
     * Gets an auth token of the specified type for a particular account,
     * prompting the user for credentials if necessary. This method is intended
     * for applications running in the foreground where it makes sense to ask
     * the user directly for a password.
     * <p>
     * If a previously generated auth token is cached for this account and type,
     * then it is returned. Otherwise, if a saved refresh token is available, it
     * is sent to the server to generate a new auth token. Otherwise, the user
     * is prompted to enter credentials.
     * <p>
     * Some services use different auth token <em>types</em> to access different
     * functionality. Most integrated apps should use
     * {@link HtcAccountDefs#AUTHTOKEN_TYPE_DEFAULT}.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link AccountManagerFuture} must not be used on the main thread.
     * <p>
     * This method requires the caller to hold the permission
     * {@link android.Manifest.permission#USE_CREDENTIALS}.
     * 
     * @param account The account to fetch an auth token for
     * @param authTokenType The auth token type, must not be null. Most
     *            integrated apps should use
     *            {@link HtcAccountDefs#AUTHTOKEN_TYPE_DEFAULT}.
     * @param options Currently not in use, may be {@code null} or empty.
     * @param activity The {@link Activity} context to use for launching a new
     *            authenticator-defined sub-Activity to prompt the user for
     *            credentials if necessary; used only to call startActivity();
     *            must not be null.
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link AccountManagerFuture} which resolves to a Bundle with
     *         following fields:
     *         <ul>
     *         <li> {@link AccountManager#KEY_ACCOUNT_NAME} - the name of the
     *         account you supplied <li> {@link AccountManager#KEY_ACCOUNT_TYPE}
     *         - the type of the account <li>
     *         {@link AccountManager#KEY_AUTHTOKEN} - the auth token you wanted
     *         <li> {@link HtcAccountDefs#KEY_ACCOUNT_ID} - the account id of
     *         the operating HTC Account. Only available if
     *         {@code authTokenType} is not
     *         {@link HtcAccountDefs#AUTHTOKEN_TYPE_SOCIAL_SINA_ACCOUNT}.
     *         </ul>
     *         If an auth token could not be fetched,
     *         {@link AccountManagerFuture#getResult()} throws:
     *         <ul>
     *         <li> {@link AuthenticatorException} if the authenticator failed
     *         to respond <li> {@link OperationCanceledException} if the
     *         operation is canceled for any reason, including the user
     *         canceling a credential request <li> {@link IOException} if the
     *         authenticator experienced an I/O problem creating a new auth
     *         token, usually because of network trouble
     *         </ul>
     *         If the account is no longer present, the return value is
     *         undefined. The caller should verify the validity of the account
     *         before requesting an auth token.
     */
    public abstract AccountManagerFuture<Bundle> getAuthToken(final Account account,
            final String authTokenType, final Bundle options, final Activity activity,
            final AccountManagerCallback<Bundle> callback, final Handler handler);

    /**
     * Gets an auth token of the specified type for a particular account,
     * optionally raising a notification if the user must enter credentials.
     * This method is intended for background tasks and services where the user
     * should not be immediately interrupted with a password prompt.
     * <p>
     * If a previously generated auth token is cached for this account and type,
     * then it is returned. Otherwise, if a saved password is available, it is
     * sent to the server to generate a new auth token. Otherwise, an
     * {@link Intent} is returned which, when started, will prompt the user for
     * a password. If the notifyAuthFailure parameter is set, a status bar
     * notification is also created with the same Intent, alerting the user that
     * they need to enter a password at some point.
     * <p>
     * In that case, you may need to wait until the user responds, which could
     * take hours or days or forever. When the user does respond and supply a
     * new password, the account manager will broadcast the
     * {@link AccountManager#LOGIN_ACCOUNTS_CHANGED_ACTION} Intent, which
     * applications can use to try again.
     * <p>
     * If notifyAuthFailure is not set, it is the application's responsibility
     * to launch the returned Intent at some point. Either way, the result from
     * this call will not wait for user action.
     * <p>
     * Some authenticators have auth token <em>types</em>, whose value is
     * authenticator-dependent. Some services use different token types to
     * access different functionality -- for example, Google uses different auth
     * tokens to access Gmail and Google Calendar for the same account.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link AccountManagerFuture} must not be used on the main thread.
     * <p>
     * This method requires the caller to hold the permission
     * {@link android.Manifest.permission#USE_CREDENTIALS}.
     * 
     * @param account The account to fetch an auth token for
     * @param authTokenType The auth token type, an authenticator-dependent
     *            string token, must not be null
     * @param options Authenticator-specific options for the request, may be
     *            null or empty
     * @param notifyAuthFailure True to add a notification to prompt the user
     *            for a password if necessary, false to leave that to the caller
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link AccountManagerFuture} which resolves to a Bundle with
     *         following fields on success:
     *         <ul>
     *         <li> {@link AccountManager#KEY_ACCOUNT_NAME} - the name of the
     *         account you supplied <li> {@link AccountManager#KEY_ACCOUNT_TYPE}
     *         - the type of the account <li>
     *         {@link AccountManager#KEY_AUTHTOKEN} - the auth token you wanted
     *         <li> {@link HtcAccountDefs#KEY_ACCOUNT_ID} - the account id of
     *         the operating HTC Account. Only available if
     *         {@code authTokenType} is not
     *         {@link HtcAccountDefs#AUTHTOKEN_TYPE_SOCIAL_SINA_ACCOUNT}.
     *         </ul>
     *         If the user must enter credentials, the returned Bundle contains
     *         only {@link AccountManager#KEY_INTENT} with the {@link Intent}
     *         needed to launch a prompt. If an error occurred,
     *         {@link AccountManagerFuture#getResult()} throws:
     *         <ul>
     *         <li> {@link AuthenticatorException} if the authenticator failed
     *         to respond <li> {@link OperationCanceledException} if the
     *         operation is canceled for any reason, including the user
     *         canceling a credential request <li> {@link IOException} if the
     *         authenticator experienced an I/O problem creating a new auth
     *         token, usually because of network trouble
     *         </ul>
     *         If the account is no longer present, the return value is
     *         undefined. The caller should verify the validity of the account
     *         before requesting an auth token.
     */
    public abstract AccountManagerFuture<Bundle> getAuthToken(final Account account,
            final String authTokenType, final Bundle options, final boolean notifyAuthFailure,
            final AccountManagerCallback<Bundle> callback, final Handler handler);

    /**
     * Overloaded method for convenience. Equivalent to
     * {@code addAccount(HtcAccountDefs.TYPE_HTC_ACCOUNT, HtcAccountDefs.AUTHTOKEN_TYPE_DEFAULT, null, addAccountOptions, activity, callback, handler)}
     * .
     */
    public final AccountManagerFuture<Bundle> addAccount(final Bundle addAccountOptions,
            final Activity activity, final AccountManagerCallback<Bundle> callback,
            final Handler handler) {
        return addAccount(HtcAccountDefs.TYPE_HTC_ACCOUNT, HtcAccountDefs.AUTHTOKEN_TYPE_DEFAULT,
                null, addAccountOptions, activity, callback, handler);
    }

    /**
     * Asks the user to add an HTC Account. The authenticator processes this
     * request with the appropriate user interface. If the user does elect to
     * add an account, either through sign-in or sign-up, the account name is
     * returned.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link AccountManagerFuture} must not be used on the main thread.
     * <p>
     * This method requires the caller to hold the permission
     * {@link android.Manifest.permission#MANAGE_ACCOUNTS}.
     * 
     * @param accountType The type of account to add; must not be null. The only
     *            supported type is {@link HtcAccountDefs#TYPE_HTC_ACCOUNT}.
     * @param authTokenType The type of auth token (see {@link #getAuthToken})
     *            this account will need to be able to generate. Most apps
     *            should use {@link HtcAccountDefs#AUTHTOKEN_TYPE_DEFAULT}.
     * @param requiredFeatures Not in use for HTC Account.
     * @param addAccountOptions For {@link HtcAccountManagerTypes#TYPE_LOCAL}
     *            instance, the options take no effect as WebView sign-in
     *            doesn't support any customization at this moment. For
     *            {@link HtcAccountManagerTypes#TYPE_SYSTEM} instance, the
     *            following options can be supplied:
     *            <ul>
     *            <li> {@link HtcAccountDefs#KEY_REQUEST_TYPE} - type of the add
     *            account request. Can be one of the following options. The
     *            default value is {@link HtcAccountDefs#TYPE_SIGN_IN_SIGN_UP}
     *            if not specified.
     *            <ul>
     *            <li> {@link HtcAccountDefs#TYPE_SIGN_IN_SIGN_UP} - request
     *            user to either sign-in or sign-up an account. All available
     *            login options will be shown. <li>
     *            {@link HtcAccountDefs#TYPE_SIGN_IN} - request user to sign-in
     *            a regular HTC Account directly. All social login and sign-up
     *            options will be disabled. <li>
     *            {@link HtcAccountDefs#TYPE_SIGN_IN_SINA} - request user to
     *            sign-in with Sina account. Supported on all China projects
     *            since Sense 4+ except DLX_U_JB_45. All other login options
     *            will be disabled. On a unsupported device, the behavior is
     *            undefined.<li> {@link HtcAccountDefs#TYPE_SIGN_IN_FACEBOOK} -
     *            request user to sign-in with Facebook account. Supported on
     *            all global projects since Sense 5.0. All other login options
     *            will be disabled. On a unsupported device, the behavior is
     *            undefined. <li> {@link HtcAccountDefs#TYPE_SIGN_IN_GOOGLE} -
     *            request user to sign-in with Google account. Supported on all
     *            global projects since Sense 5+. All other login options will
     *            be disabled. On a unsupported device, the behavior is
     *            undefined. <li> {@link HtcAccountDefs#TYPE_SIGN_IN_QQ} -
     *            request user to sign-in with QQ account. Supported on all
     *            China projects since Sense 5+. All other login options will be
     *            disabled. On a unsupported device, the behavior is undefined.
     *            <li> {@link HtcAccountDefs#TYPE_SIGN_IN_EXT} - request user to
     *            sign-in with one of the social login options. All available
     *            sign-in options except regular HTC Account will be shown. This
     *            option is available since Sense 5.0. <li>
     *            {@link HtcAccountDefs#TYPE_CUSTOM} - use an integer as the
     *            flags to decide what login options should be shown, if
     *            available. The flags must be passed as an integer with key
     *            {@link HtcAccountDefs#KEY_CUSTOM_LOGIN_OPTIONS}. This options
     *            is supported since Sense 6.0.
     *            </ul>
     *            <li> {@link HtcAccountDefs#KEY_CUSTOM_LOGIN_OPTIONS} – an
     *            integer as flags to indicate what login options should be
     *            shown, if available, when the request type is
     *            {@link HtcAccountDefs#TYPE_CUSTOM}. The flags can be
     *            combination of the following values:
     *            <ul>
     *            <li>
     *            {@link HtcAccountDefs#FLAG_LOGIN_OPTION_SIGNIN_HTC_ACCOUNT},
     *            <li>
     *            {@link HtcAccountDefs#FLAG_LOGIN_OPTION_SIGNIN_SINA_ACCOUNT},
     *            <li>
     *            {@link HtcAccountDefs#FLAG_LOGIN_OPTION_SIGNIN_FACEBOOK_ACCOUNT}
     *            ,<li>
     *            {@link HtcAccountDefs#FLAG_LOGIN_OPTION_SIGNIN_QQ_ACCOUNT},
     *            and <li>
     *            {@link HtcAccountDefs#FLAG_LOGIN_OPTION_SIGNIN_GOOGLE_ACCOUNT}
     *            .
     *            </ul>
     *            The names are self-explanatory.</li> <li>
     *            {@link HtcAccountDefs#KEY_ACCOUNT_LIFETIME} - special option
     *            to specify the lifetime of the account to add. The option is
     *            first introduce in Sense 6.0.
     *            <ul>
     *            <li> {@link HtcAccountDefs#ACCOUNT_LIFETIME_ONETIME} -
     *            indicates the account shouldn't be added to the system, but
     *            instead the auth token should be included in the return value
     *            directly for one-time use. On China projects it's supported
     *            since Sense 6.0 K443 MR. <li>
     *            {@link HtcAccountDefs#ACCOUNT_LIFETIME_PERSISTENT} - indicates
     *            the account should be added to the system, and other
     *            applications can later use {@link HtcAccountManager} APIs to
     *            operate on this account. It’s also the default value to use if
     *            not specified.
     *            </ul>
     *            </li> <li> {@link HtcAccountDefs#KEY_TOS_ACCEPTED} -
     *            {@code true} to indicate user has viewed and accepted HTC
     *            Account terms and conditions. The authenticator will then skip
     *            landing page if possible. Use this option with care - if user
     *            didn't accept the terms and conditions but caller passes
     *            {@code true}, it might cause legal issues.
     *            </ul>
     * @param activity The {@link Activity} context to use for launching a new
     *            authenticator-defined sub-Activity to prompt the user to
     *            create an account; used only to call startActivity(); if null,
     *            the prompt will not be launched directly, but the necessary
     *            {@link Intent} will be returned to the caller instead
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link AccountManagerFuture} which resolves to a Bundle with
     *         these fields if activity was specified and an account was
     *         created:
     *         <ul>
     *         <li> {@link AccountManager#KEY_ACCOUNT_NAME} - the name of the
     *         account created <li> {@link AccountManager#KEY_ACCOUNT_TYPE} -
     *         the type of the account <li> {@link HtcAccountDefs#KEY_AUTHKEY} -
     *         the authtoken for the caller app. Presents only if the caller
     *         holds {@link android.Manifest.permission#USE_CREDENTIALS} and and
     *         runs on Sense 6.0 or later devices. <li>
     *         {@link HtcAccountDefs#KEY_ACCOUNT_ID} - the account id of the HTC
     *         Account. Presents only if the caller holds
     *         {@link android.Manifest.permission#USE_CREDENTIALS} and runs on
     *         Sense 7.0 or later devices.
     *         </ul>
     *         If no activity was specified, the returned Bundle contains only
     *         {@link AccountManager#KEY_INTENT} with the {@link Intent} needed
     *         to launch the actual account creation process. If an error
     *         occurred, {@link AccountManagerFuture#getResult()} throws:
     *         <ul>
     *         <li> {@link AuthenticatorException} if no authenticator was
     *         registered for this account type or the authenticator failed to
     *         respond <li> {@link OperationCanceledException} if the operation
     *         was canceled for any reason, including the user canceling the
     *         creation process <li> {@link IOException} if the authenticator
     *         experienced an I/O problem creating a new account, usually
     *         because of network trouble
     *         </ul>
     */
    public abstract AccountManagerFuture<Bundle> addAccount(final String accountType,
            final String authTokenType, final String[] requiredFeatures,
            final Bundle addAccountOptions, final Activity activity,
            final AccountManagerCallback<Bundle> callback, final Handler handler);

    /**
     * @deprecated Due to the limitation of WebView implementation and social
     *             accounts, HTC Account does not support
     *             {@code confirmCredentials}.
     */
    @Deprecated
    public abstract AccountManagerFuture<Bundle> confirmCredentials(final Account account,
            final Bundle options, final Activity activity,
            final AccountManagerCallback<Bundle> callback, final Handler handler);

    /**
     * @deprecated Due to the limitation of WebView implementation and social
     *             accounts, HTC Account does not support
     *             {@code updateCredentials}.
     */
    @Deprecated
    public abstract AccountManagerFuture<Bundle> updateCredentials(final Account account,
            final String authTokenType, final Bundle options, final Activity activity,
            final AccountManagerCallback<Bundle> callback, final Handler handler);

    /**
     * Get the type of authenticator the account manager works on. It safe to
     * call this method from the main thread.
     * 
     * @see HtcAccountManagerTypes
     * @return One of the types defined in {@link HtcAccountManagerTypes} .
     */
    public abstract String getType();

    /**
     * Register the {@code receiver} dynamically to receive HTC Account
     * broadcasts based on the type of {@link HtcAccountManager} instance. It's
     * provided for convenience so the caller doesn't need to determine what
     * type of {@link HtcAccountManager} it's using before registering receivers
     * for HTC Account broadcasts.
     * <p>
     * If the instance type is {@link HtcAccountManagerTypes#TYPE_SYSTEM},
     * calling this method equals to call
     * {@link Context#registerReceiver(BroadcastReceiver, IntentFilter, String, Handler)}
     * , where the {@link Context} object is the one {@link HtcAccountManager}
     * is working on (i.e. the {@link Context} object you used to construct
     * {@link HtcAccountManager} instance), the {@code filter} accepts all HTC
     * Account broadcasts defined in {@link HtcAccountBroadcasts}, the
     * {@code broadcastPermission} is
     * {@link HtcAccountBroadcasts#PERMISSION_SEND}, and the {@code scheduler}
     * is {@code null}; on the other hand, if the instance type is
     * {@link HtcAccountManagerTypes#TYPE_LOCAL}, it equals to
     * {@link LocalBroadcastManager#registerReceiver(BroadcastReceiver, IntentFilter)}
     * where the {@code filter} accepts all HTC Account broadcasts defined in
     * {@link HtcAccountBroadcasts} .
     * 
     * @param receiver The BroadcastReceiver to handle the broadcast.
     */
    public abstract void registerReceiver(BroadcastReceiver receiver);

    /**
     * Provide the authenticator's version for application's BI data.
     *
     * @return The HTC Account (authenticator) version name.
     * {@link #VERSION_NOT_AVAILABLE} if not available.
     */
    public abstract String getAccountAppVersionName();
}
