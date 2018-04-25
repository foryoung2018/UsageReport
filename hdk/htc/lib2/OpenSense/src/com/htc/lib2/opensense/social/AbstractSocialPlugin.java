package com.htc.lib2.opensense.social;

import android.accounts.Account;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * An abstract class for each social network plugin
 * 
 * @hide
 */
public abstract class AbstractSocialPlugin {

	private static final String LOG_TAG = AbstractSocialPlugin.class
			.getSimpleName();

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

	private class Transport extends ISocialPlugin.Stub {

		// TODO Handle customized exceptions

		/**
		 * @hide
		 */
		private boolean isCallerValid(int uid) {
			return AbstractSocialPlugin.this.isCallerValid(uid);
		}
		
		/**
		 * 
		 * @hide
		 */
		@Override
		public void syncActivityStreams(final ISocialPluginResponse response,
				final Account[] accounts, final Bundle options)
				throws RemoteException {
			if (!isCallerValid(Binder.getCallingUid())) {
				throw new RemoteException("invalid caller");
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					Bundle result = AbstractSocialPlugin.this
							.syncActivityStreams(new SocialPluginResponse(
									response), accounts, options);
					if (result != null) {
						try {
							response.onResult(result);
						} catch (RemoteException e) {
							result = new Bundle();
							result.putBoolean(SocialManager.KEY_BOOLEAN_RESULT, false);
							try {
								response.onResult(result);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				}
			}).start();
		}

		/**
		 * 
		 * @hide
		 */
		@Override
		public void publishActivityStream(final ISocialPluginResponse response,
				final Account[] accounts, final Bundle options) throws RemoteException {
			if (!isCallerValid(Binder.getCallingUid())) {
				throw new RemoteException("invalid caller");
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					Bundle result = AbstractSocialPlugin.this
							.publishActivityStream(new SocialPluginResponse(
									response), accounts, options);
					if (result != null) {
						try {
							response.onResult(result);
						} catch (RemoteException e) {
							result = new Bundle();
							result.putBoolean(SocialManager.KEY_BOOLEAN_RESULT, false);
							try {
								response.onResult(result);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				}
			}).start();
		}

		/**
		 * 
		 * @hide
		 */
		@Override
		public void syncContacts(final ISocialPluginResponse response,
				final Account[] accounts, final Bundle options) throws RemoteException {
			if (!isCallerValid(Binder.getCallingUid())) {
				throw new RemoteException("invalid caller");
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					Bundle result = AbstractSocialPlugin.this
							.syncContacts(new SocialPluginResponse(
									response), accounts, options);
					if (result != null) {
						try {
							response.onResult(result);
						} catch (RemoteException e) {
							result = new Bundle();
							result.putBoolean(SocialManager.KEY_BOOLEAN_RESULT, false);
							try {
								response.onResult(result);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				}
			}).start();
		}

		/**
		 * @hide
		 */
		@Override
		public void getDataSources(final ISocialPluginResponse response,
				final String[] features) throws RemoteException {
			if (!isCallerValid(Binder.getCallingUid())) {
				throw new RemoteException("invalid caller");
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					Bundle result = AbstractSocialPlugin.this.getDataSources(
							new SocialPluginResponse(response), features);
					if (result != null) {
						try {
							response.onResult(result);
						} catch (RemoteException e) {
							result = new Bundle();
							try {
								response.onResult(result);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				}
			}).start();
		}

		/**
		 * @hide
		 */
		@Override
		public void getSyncTypes(final ISocialPluginResponse response,
				final Account[] accounts, final Bundle options)
				throws RemoteException {
			if (!isCallerValid(Binder.getCallingUid())) {
				throw new RemoteException("invalid caller");
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					Bundle result = AbstractSocialPlugin.this.getSyncTypes(
							new SocialPluginResponse(response), accounts,
							options);
					if (result != null) {
						try {
							response.onResult(result);
						} catch (RemoteException e) {
							result = new Bundle();
							try {
								response.onResult(result);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				}
			}).start();
		}

		/**
		 * @hide
		 */
		@Override
		public void syncSyncTypes(final ISocialPluginResponse response,
				final Account[] accounts, final Bundle options)
				throws RemoteException {
			if (!isCallerValid(Binder.getCallingUid())) {
				throw new RemoteException("invalid caller");
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					Bundle result = AbstractSocialPlugin.this.syncSyncTypes(
							new SocialPluginResponse(response), accounts,
							options);
					if (result != null) {
						try {
							response.onResult(result);
						} catch (RemoteException e) {
							result = new Bundle();
							try {
								response.onResult(result);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				}
			}).start();
		}
		
		/**
		 * @hide
		 */
		@Override
		public void getSubscribeIntent(final ISocialPluginResponse response,
				final Account[] accounts, final Bundle options)
				throws RemoteException {
			if (!isCallerValid(Binder.getCallingUid())) {
				throw new RemoteException("invalid caller");
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					Bundle result = AbstractSocialPlugin.this.getSubscribeIntent(
							new SocialPluginResponse(response), accounts,
							options);
					if (result != null) {
						try {
							response.onResult(result);
						} catch (RemoteException e) {
							result = new Bundle();
							try {
								response.onResult(result);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				}
			}).start();
		}
		
		/**
		 * @hide
		 */
		@Override
		public void getBI(final ISocialPluginResponse response,
				final Account[] accounts, final Bundle options)
				throws RemoteException {
			if (!isCallerValid(Binder.getCallingUid())) {
				throw new RemoteException("invalid caller");
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					Bundle result = AbstractSocialPlugin.this.getBI(
							new SocialPluginResponse(response), accounts,
							options);
					if (result != null) {
						try {
							response.onResult(result);
						} catch (RemoteException e) {
							result = new Bundle();
							try {
								response.onResult(result);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				}
			}).start();
		}
	}

	// Abstract methods

	/**
	 * Sync streams
	 * 
	 * @param response the given response
	 * @param accounts the given accounts
	 * @param options the given options
	 * @return the result state
	 * 
	 * @hide
	 */
	public abstract Bundle syncActivityStreams(SocialPluginResponse response,
			Account[] accounts, Bundle options);

	/**
	 * Publish a stream
	 * 
	 * @param response the given response
	 * @param accounts the given accounts
	 * @param options the given options
	 * @return the result state
	 * 
	 * @hide
	 */
	public Bundle publishActivityStream(SocialPluginResponse response,
			Account[] accounts, Bundle options) {
		return new Bundle();
	}

	/**
	 * Sync contacts
	 * 
	 * @param response the given response
	 * @param accounts the given accounts
	 * @param options the given options
	 * @return the result state
	 * 
	 * @hide
	 */
	public Bundle syncContacts(SocialPluginResponse response,
			Account[] accounts, Bundle options) {
		Log.w(LOG_TAG, "Method not implemented by plugin subclass");
		return new Bundle();
	}

	/**
	 * Gets data sources, they would be accounts, post activity uri, or login activity uri
	 * 
	 * @param response the given response
	 * @param features the given features
	 * @return the result state
	 * 
	 * @hide
	 */
	public Bundle getDataSources(SocialPluginResponse response,
			String[] features) {
		Log.w(LOG_TAG, "Method not implemented by plugin subclass");
		return new Bundle();
	}

	/**
	 * Gets types for stream sync
	 * 
	 * @param response the given response
	 * @param accounts the given accounts
	 * @param options the given options
	 * @return the result state
	 * 
	 * @hide
	 */
	public Bundle getSyncTypes(SocialPluginResponse response,
			Account[] accounts, Bundle options) {
		Log.w(LOG_TAG, "Method not implemented by plugin subclass");
		return new Bundle();
	}

	/**
	 * @hide
	 */
	public Bundle syncSyncTypes(SocialPluginResponse response,
			Account[] accounts, Bundle options) {
		Log.w(LOG_TAG, "Method not implemented by plugin subclass");
		return new Bundle();
	}
	
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
	public Bundle getSubscribeIntent(SocialPluginResponse response,
			Account[] accounts, Bundle options) {
		Log.w(LOG_TAG, "Method not implemented by plugin subclass");
		return new Bundle();
	}
	
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
	public Bundle getBI(SocialPluginResponse response,
			Account[] accounts, Bundle options) {
		Log.w(LOG_TAG, "Method not implemented by plugin subclass");
		return new Bundle();
	}

	/**
	 * @hide
	 */
	protected boolean isCallerValid(int uid) {
		return true;
	}
}
