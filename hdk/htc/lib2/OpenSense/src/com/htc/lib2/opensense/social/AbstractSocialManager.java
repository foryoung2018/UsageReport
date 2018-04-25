package com.htc.lib2.opensense.social;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * An abstract class for social network manager
 * 
 * @hide
 */
public abstract class AbstractSocialManager {
	
	private Transport mTransport = new Transport();

	/**
	 * Gets IBinder instance
	 * 
	 * @return IBinder instance
	 * 
	 * @hide
	 */
	public final IBinder getIBinder() {
		return mTransport.asBinder();
	}

	private class Transport extends ISocialManager.Stub {

		/**
		 * @hide
		 */
		@Override
		public PluginDescription[] getPluginTypes() throws RemoteException {
			return AbstractSocialManager.this.getPluginTypes();
		}

		/**
		 * @hide
		 */
		@Override
		public void syncActivityStreams(ISocialManagerResponse response,
				Account[] accounts, Bundle options) throws RemoteException {
			AbstractSocialManager.this.syncActivityStreams(response, accounts, options);
		}

		/**
		 * @hide
		 */
		@Override
		public void publishActivityStream(ISocialManagerResponse response,
				Account[] accounts, Bundle options) throws RemoteException {
			AbstractSocialManager.this.publishActivityStream(response, accounts, options);
		}

		/**
		 * @hide
		 */
		@Override
		public void syncContacts(ISocialManagerResponse response,
				Account[] accounts, Bundle options) throws RemoteException {
			AbstractSocialManager.this.syncContacts(response, accounts, options);
		}

		/**
		 * @hide
		 */
		@Override
		public void getDataSources(ISocialManagerResponse response, String accountType,
				String[] features) throws RemoteException {
			AbstractSocialManager.this.getDataSources(response,accountType, features);
		}

		/**
		 * @hide
		 */
		@Override
		public void getSyncTypes(ISocialManagerResponse response,
				Account[] accounts, Bundle options) throws RemoteException {
			AbstractSocialManager.this.getSyncTypes(response, accounts, options);
		}

		/**
		 * @hide
		 */
		@Override
		public void syncSyncTypes(ISocialManagerResponse response,
				Account[] accounts, Bundle options) throws RemoteException {
			AbstractSocialManager.this.syncSyncTypes(response, accounts, options);
		}
		
		/**
		 * @hide
		 */
		@Override
		public void getSubscribeIntent(ISocialManagerResponse response,
				Account[] accounts, Bundle options) throws RemoteException {
			AbstractSocialManager.this.getSubscribeIntent(response, accounts, options);
		}
		
		/**
		 * @hide
		 */
		@Override
		public void getBI(ISocialManagerResponse response,
				Account[] accounts, Bundle options) throws RemoteException {
			AbstractSocialManager.this.getBI(response, accounts, options);
		}
	}

	/**
	 * Gets all support plugin types
	 * 
	 * @return all support plugin types
	 * @throws RemoteException if remote exception occurred
	 * 
	 * @hide
	 */
	public abstract PluginDescription[] getPluginTypes() throws RemoteException;

	/**
	 * Sync streams
	 * 
	 * @param response the given response
	 * @param accounts the given accounts
	 * @param options the given options
	 * @throws RemoteException if remote exception occurred
	 * 
	 * @hide
	 */
	public abstract void syncActivityStreams(ISocialManagerResponse response,
			Account[] accounts, Bundle options) throws RemoteException;

	/**
	 * Publish a stream
	 * 
	 * @param response the given response
	 * @param accounts the given accounts
	 * @param options the given options
	 * @throws RemoteException if remote exception occurred
	 * 
	 * @hide
	 */
	public abstract void publishActivityStream(ISocialManagerResponse response,
			Account[] accounts, Bundle options) throws RemoteException;

	/**
	 * Sync contacts
	 * 
	 * @param response the given response
	 * @param accounts the given accounts
	 * @param options the given options
	 * @throws RemoteException if remote exception occurred
	 * 
	 * @hide
	 */
	public abstract void syncContacts(ISocialManagerResponse response,
			Account[] accounts, Bundle options) throws RemoteException;

	/**
	 * Gets data sources, they would be accounts, post activity uri, or login activity uri
	 * 
	 * @param response the given response
	 * @param accountType the given account type
	 * @param features the given features
	 * @throws RemoteException if remote exception occurred
	 * 
	 * @hide
	 */
	public abstract void getDataSources(ISocialManagerResponse response,
			String accountType, String[] features) throws RemoteException;

	/**
	 * Gets types for stream sync
	 * 
	 * @param response the given response
	 * @param accounts the given accounts
	 * @param options the given options
	 * @throws RemoteException if remote exception occurred
	 * 
	 * @hide
	 */
	public abstract void getSyncTypes(ISocialManagerResponse response,
			Account[] accounts, Bundle options) throws RemoteException;

	/**
	 * @hide
	 */
	public abstract void syncSyncTypes(ISocialManagerResponse response,
			Account[] accounts, Bundle options) throws RemoteException;
	
	/**
	 * Gets subscribe intent for doing subscribe
	 * 
	 * @param response the given response
	 * @param accounts the given accounts
	 * @param options the given options
	 * @return the result state
	 * 
	 * @hide
	 */
	public abstract void getSubscribeIntent(ISocialManagerResponse response,
			Account[] accounts, Bundle options) throws RemoteException;
	
	/**
    * Gets BI
     * 
    * @param response the given response
	 * @param accounts the given accounts
	 * @param options the given options
	 * @return the result state
     * 
     * @hide
     */
	public abstract void getBI(ISocialManagerResponse response,
			Account[] accounts, Bundle options) throws RemoteException;
}
