package com.htc.lib2.opensense.social;

import android.accounts.Account;
import android.os.Bundle;

import com.htc.lib2.opensense.social.ISocialPluginResponse;

/**
 * A generic social network plugin interface
 * 
 * @hide
 */
oneway interface ISocialPlugin {

    /**
     * Sync streams
     * 
     * @param response the given response callback
     * @param accounts the given accounts
     * @param options the given options
     * @throws android.os.RemoteException if other remote exception occurred
     * 
     * @hide
     */
    void syncActivityStreams(in ISocialPluginResponse response, in Account[] accounts, in Bundle options);

    /**
     * Publish a stream
     * 
     * @param response the given response callback
     * @param accounts the given accounts
     * @param options the given options
     * @throws android.os.RemoteException if other remote exception occurred
     * 
     * @hide
     */
    void publishActivityStream(in ISocialPluginResponse response, in Account[] accounts, in Bundle options);

    /**
     * Sync contacts
     * 
     * @param response the given response callback
     * @param accounts the given accounts
     * @param options the given options
     * @throws android.os.RemoteException if other remote exception occurred
     * 
     * @hide
     */
    void syncContacts(in ISocialPluginResponse response, in Account[] accounts, in Bundle options);

    /**
     * Gets data sources, they would be accounts, post activity uri, or login activity uri
     * 
     * @param response the given response callback
     * @param features the given features
     * @throws android.os.RemoteException if other remote exception occurred
     * 
     * @hide
     */
    void getDataSources(in ISocialPluginResponse response, in String[] features);

    /**
     * Gets types for stream sync
     * 
     * @param response the given response callback
     * @param accounts the given accounts
     * @param options the given options
     * @throws android.os.RemoteException if other remote exception occurred
     * 
     * @hide
     */
    void getSyncTypes(in ISocialPluginResponse response, in Account[] accounts, in Bundle options);

    /**
     * sync types for stream sync
     *
     * @param response the given response callback
     * @param accounts the given accounts
     * @param options the given options
     * @throws android.os.RemoteException if other remote exception occurred
     * 
     * @hide
     */
    void syncSyncTypes(in ISocialPluginResponse response, in Account[] accounts, in Bundle options);
    
    /**
     * Gets subscribe intent for doing subscribe
     * 
     * @param response the given response callback
     * @param accounts the given accounts
     * @param options the given options
     * @throws android.os.RemoteException if other remote exception occurred
     * 
     * @hide
     */
    void getSubscribeIntent(in ISocialPluginResponse response, in Account[] accounts, in Bundle options);
    
    /**
     * Gets BI
     * 
     * @param response the given response callback
     * @param accounts the given accounts
     * @param options the given options
     * @throws android.os.RemoteException if other remote exception occurred
     * 
     * @hide
     */
    void getBI(in ISocialPluginResponse response, in Account[] accounts, in Bundle options);
}