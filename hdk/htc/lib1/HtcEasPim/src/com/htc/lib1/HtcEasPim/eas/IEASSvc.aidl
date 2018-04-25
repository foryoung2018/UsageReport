package com.htc.lib1.HtcEasPim.eas;

import android.os.Bundle;
import com.htc.lib1.HtcEasPim.eas.EASGalSearchResult;
import com.htc.lib1.HtcEasPim.eas.EASResolveRecipientsResult;

/**
 * {@exthide}
 */
interface IEASSvc
{
    /**
     * Retrieve Exchange accounts
     *
     * @return Bundle array of Exchange accounts
     */
    Bundle[] getAccounts();

    /**
     * Search contacts from Exchange server
     * 
     * @param accountName the search name
     * @param criteria 
     * @return The request data
     */
    EASGalSearchResult searchGAL(String accountName, String criteria);

    /**
     * Set mail flag
     * @param accountId The account Id 
     * @param messageId The message Id
     * @param flag The new flag value want to update
     * @param parameter The extra data
     * @return The operation result
     */
    int setMailFlagStatus(long accountId, long messageId, int flag, in Bundle parameter);
    
     /**
     * Search contacts from Exchange server
     * 
     * @param accountName the search name
     * @param criteria 
     * @return The request data
     */
    EASResolveRecipientsResult retrievingFreeBusyData(in String accountName, in String[] accounts, long from, long to);
}
