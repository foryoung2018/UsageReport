
package com.htc.lib1.cs.account;

import com.htc.lib1.cs.account.HtcAccount;

/**
 * Interface of the database wrapper service.
 * 
 * @author samael_wang@htc.com
 */
interface IHtcAccountManagerDataSource {

    /**
     * Add an HtcAccount to the database.
     * 
     * @param HtcAccount Account to insert. Must not be {@code null}.
     * @return True if the HtcAccount is successfully inserted. False otherwise.
     */
    boolean addAccount(in HtcAccount account);

    /**
     * Add user data of a specific account.
     * 
     * @param HtcAccount Account to operate on. Must not be {@code null}.
     * @param key Key of the data. Must not be {@code null}.
     * @param value Value to set. Could be null or empty.
     */
    void addUserData(in HtcAccount account, String key, String value);

    /**
     * Get accounts.
     * 
     * @param typeToQuery Optional string to specify type of accounts to query.
     * @return An array of accounts, never {@code null}. If no accounts found,
     *         it returns an empty array.
     */
    HtcAccount[] getAccounts(String typeToQuery);

    /**
     * Get the authtoken of the given {@code account} and {@code authTokenType}.
     * 
     * @param HtcAccount Account to modify. Must not be {@code null}.
     * @param authTokenType Type of the authtoken. Must not be {@code null}.
     * @return Authtoken found or {@code null} if no satisfied token exists.
     */
    String getAuthToken(in HtcAccount account, String authTokenType);

    /**
     * Get the database primary key of the specific account.
     * 
     * @param HtcAccount Account to find. Must not be {@code null}.
     * @return Id of the row in database.
     */
    long getId(in HtcAccount account);

    /**
     * Get the password of a specific account.
     * 
     * @param HtcAccount Account to query. Must not be {@code null}.
     * @return Password set before or {@code null} if not set yet.
     */
    String getPassword(in HtcAccount account);

    /**
     * Get the user data of a specific HtcAccount with a specific key.
     * 
     * @param HtcAccount Account to operate on. Must not be {@code null}.
     * @param key Key of the data. Must not be {@code null}.
     * @return User data in the database or {@code null} if not such key found.
     */
    String getUserData(in HtcAccount account, String key);
    
    /**
     * Get the GUID stored for the specific account.
     * 
     * @param HtcAccount Account to find. Must not be {@code null}.
     * @param AuthToken Account auth token to find. Must not be {@code null}.
     * @return Global user id for the specific account, or {@code null} if not
     *         found.
     */
    String getGuid(in HtcAccount account, String authToken);

    /**
     * Remove an HtcAccount and all its associated data from the database.
     * 
     * @param HtcAccount Account to remove. Must not be {@code null}.
     */
    void removeAccount(in HtcAccount account);

    /**
     * Remove all authtoken(s) associated to the given account.
     * 
     * @param id Primary key of the operating HtcAccount in database.
     */
    void removeAllAuthTokens(long id);

    /**
     * Remove the authtoken from the database.
     * 
     * @param id Primary key of the operating HtcAccount in database.
     * @param authToken Authtoken to remove. Do nothing if it's {@code null} or
     *            empty.
     */
    void removeAuthToken(long id, String authToken);

    /**
     * Remove user data of a specific account.
     * 
     * @param id Primary key of the operating HtcAccount in database.
     * @param key Key of the user data to remove. If not specific, it removes
     *            all user data associated with the given account.
     */
    void removeUserData(long id, String key);

    /**
     * Set the authtoken of a specific account.
     * 
     * @param HtcAccount Account to modify. Must not be {@code null}.
     * @param authTokenType Type of the authtoken. Must not be {@code null}.
     * @param authToken Authtoken.
     */
    void setAuthToken(in HtcAccount account, String authTokenType, String authToken);

    /**
     * Set the password of specific account.
     * 
     * @param HtcAccount Account to modify. Must not be {@code null}.
     * @param password Password to set.
     */
    void setPassword(in HtcAccount account, String password);

    /**
     * Set the GUID of a specific account.
     * 
     * @param HtcAccount Account of the GUID. Must not be {@code null}.
     * @param Guid GUID to store. Must not be {@code null}.
     * @param AuthToken AuthToken to store. Must not be {@code null}.
     */
	void setGuid(in HtcAccount account, String guid, String authToken);

    /**
     * Clear all content in the database.
     */
    void clear();
}
