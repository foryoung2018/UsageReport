package com.htc.lib2.opensense.social;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.htc.lib2.opensense.internal.SystemWrapper;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OperationCanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Parcelable;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

/**
 * This class provides api to interact with each social plugin.
 * 
 * @hide
 */
public class SocialManager implements ServiceConnection, DeathRecipient {

	private static final String LOG_TAG = SocialManager.class.getSimpleName();

	/**
	 * Error code remote exception.
	 * 
	 * @hide
	 */
	public static final int ERROR_CODE_REMOTE_EXCEPTION = 1;

	/**
	 * @hide
	 */
	public static final int ERROR_CODE_NETWORK_ERROR = 3;

	/**
	 * @hide
	 */
	public static final int ERROR_CODE_CANCELED = 4;

	/**
	 * Error code invalid response.
	 * 
	 * @hide
	 */
	public static final int ERROR_CODE_INVALID_RESPONSE = 5;

	/**
	 * @hide
	 */
	public static final int ERROR_CODE_UNSUPPORTED_OPERATION = 6;

	/**
	 * @hide
	 */
	public static final int ERROR_CODE_BAD_ARGUMENTS = 7;

	/**
	 * @hide
	 */
	public static final int ERROR_CODE_BAD_REQUEST = 8;

	/**
	 * @hide
	 */
	public static final int ERROR_CODE_NO_ACCOUNT = 9;

	/**
	 * @hide
	 */
	public static final String KEY_ACCOUNT_NAME = AccountManager.KEY_ACCOUNT_NAME;

	/**
	 * @hide
	 */
	public static final String KEY_ACCOUNT_TYPE = AccountManager.KEY_ACCOUNT_TYPE;

	/**
	 * @hide
	 */
	public static final String KEY_INTENT = AccountManager.KEY_INTENT;

	/**
	 * decide whether your account will show in topic&service or not. Default true.
	 * 
	 * @hide
	 */
	public static final String KEY_PROP_BOOL_SHOW_IN_ACCOUNT_LIST = "key_prop_show_in_list";

	/**
	 * Indicates your identity provider type: 
	 * IDENTITY_TYPE_REAL_ACCOUNT: Plugin has an account and registered in account manager.
	 * IDENTITY_TYPE_FAKE_ACCOUNT: Plugin has an account but not registered in account manager.
	 * IDENTITY_TYPE_NO_ACCOUNT: Plugin doesn't have an account.
	 * 
	 * @hide
	 */
	public static final String KEY_PROP_IDENTITY_PROVIDER_TYPE = "key_prop_identity_provider_type";

	/**
	 * customize your plugin's default sync type id. Default is highlights.
	 * 
	 * @hide
	 */
	public static final String KEY_PROP_DEFAULT_SYNC_TYPE_ID = "key_prop_default_sync_type_id";

	/**
	 * Type of KEY_PROP_IDENTITY_PROVIDER_TYPE. Plugin has an account and
	 * registered in account manager.
	 * 
	 * @hide
	 */
	public static final int IDENTITY_TYPE_REAL_ACCOUNT = 0;

	/**
	 * Type of KEY_PROP_IDENTITY_PROVIDER_TYPE. Plugin has an account but not
	 * registered in account manager.
	 * 
	 * @hide
	 */
	public static final int IDENTITY_TYPE_FAKE_ACCOUNT = 1;

	/**
	 * Type of KEY_PROP_IDENTITY_PROVIDER_TYPE. Plugin doesn't have an account.
	 * 
	 * @hide
	 */
	public static final int IDENTITY_TYPE_NO_ACCOUNT = 2;
	
	/**
	 * customize where your plugin's filter type to show. Default is inside account.
	 * 
	 * @hide
	 */
	public static final String KEY_PROP_FILTER_MODE = "key_prop_filter_mode";

	/**
	 * Type of KEY_PROP_FILTER_MODE. Filter will be shown in accounts.
	 * 
	 * @hide
	 */
	public static final int FILTER_MODE_DISPLAY_DEFAULT = 1;

	/**
	 * Type of KEY_PROP_FILTER_MODE. Filter will be expanded outside, just like news topics.
	 * 
	 * @hide
	 */
	public static final int FILTER_MODE_DISPLAY_EXPANDED = 2;

	/**
	 * Type of KEY_PROP_FILTER_MODE. No filter will be shown, only support being
	 * refreshed in highlight.
	 * 
	 * @hide
	 */
	public static final int FILTER_MODE_DISPLAY_NONE = 4;

	/**
	 * If your plugin doesn't have real account, you need specify the account label resource id.
	 * 
	 * @hide
	 */
	public static final String KEY_PROP_ACCOUNT_LABEL_ID = "key_prop_account_label_id";

	/**
	 * If your plugin doesn't have real account, you need specify the account icon resource id.
	 * 
	 * @hide
	 */
	public static final String KEY_PROP_ACCOUNT_ICON_ID = "key_prop_account_icon_id";

	/**
	 * If your plugin doesn't have real account, you need specify the package name.
	 * 
	 * @hide
	 */
	public static final String KEY_PROP_PACKAGE_NAME = "key_prop_package_name";

	/**
	 * Intent uri that launch the post activity of the plugin.
	 * 
	 * @hide
	 */
	public static final String KEY_PROP_POST_ACT_INTENT_URI = "key_prop_intent_uri";

	/**
	 * If your plugin cannot add account by AccountManager. Specify the intent
	 * uri that launch authenticator activity.
	 * 
	 * @hide
	 */
	public static final String KEY_PROP_ADD_ACCOUNT_URI = "custom_add_account_uri";
	
	/**
	 * Specify the description of plugin.
	 * 
	 * @hide
	 */
	public static final String KEY_PROP_ADD_DESCRIPTION = "custom_add_description";

	/**
	 * Indicates your plugin support multiple sync types feature. Default false.
	 * 
	 * @hide
	 */
	public static final String KEY_PROP_BOOL_SUPPORT_MULTI_SYNC_TYPES = "key_prop_bool_sup_multi_sync_types";

	/**
	 * Indicates your plugin support highlights feature. Default true.
	 * 
	 * @hide
	 */
	public static final String KEY_PROP_BOOL_SUPPORT_HIGHLIGHTS_FEATURE = "key_prop_bool_support_highlight";

	/**
	 * Indicates your plugin support personal channel. Default false.
	 * 
	 * @hide
	 */
	public static final String KEY_PROP_BOOL_SUPPORT_PERSONAL_CHANNEL = "key_prop_bool_support_personal_channel";
	
	/**
	 * Indicates your plugin support loadmore function. Default true.
	 * 
	 * @hide
	 */
	public static final String KEY_PROP_BOOL_SUPPORT_LOAD_MORE = "key_prop_bool_support_load_more";

	/**
	 * Key properties.
	 * 
	 * @hide
	 */
	public static final String KEY_PROPERTIES = "key_properties"; 

	/**
	 * Key accounts.
	 * 
	 * @hide
	 */
	public static final String KEY_ACCOUNTS = AccountManager.KEY_ACCOUNTS;

	/**
	 * Key contacts.
	 * 
	 * @hide
	 */
	public static final String KEY_CONTACTS = "contacts";

	/**
	 * Key features.
	 * 
	 * @hide
	 */
	public static final String KEY_FEATURES = "features";

	/**
	 * @hide
	 */
	public static final String KEY_SOCIAL_PLUGIN_RESPONSE = "socialPluginResponse";

	/**
	 * @hide
	 */
	public static final String KEY_SOCIAL_MANAGER_RESPONSE = "socialManagerResponse";

	/**
	 * Key boolean result.
	 * 
	 * @hide
	 */
	public static final String KEY_BOOLEAN_RESULT = AccountManager.KEY_BOOLEAN_RESULT;

	/**
	 * @hide
	 */
	public static final String KEY_ERROR_CODE = AccountManager.KEY_ERROR_CODE;

	/**
	 * @hide
	 */
	public static final String KEY_ERROR_MESSAGE = AccountManager.KEY_ERROR_MESSAGE;

	// public static final String KEY_CALLER_UID =
	// AccountManager.KEY_CALLER_UID;
	// public static final String KEY_CALLER_PID =
	// AccountManager.KEY_CALLER_PID;

	/**
	 * Key offset.
	 * 
	 * @hide
	 */
	public static final String KEY_OFFSET = "offset";

	/**
	 * Key limit.
	 * 
	 * @hide
	 */
	public static final String KEY_LIMIT = "limit";

	/**
	 * Key sync stream type.
	 * 
	 * @hide
	 */
	public static final String KEY_SYNC_STREAM_TYPE = "syncStreamType";

	/**
	 * Key trigger sync manager.
	 * 
	 * @hide
	 */
	public static final String KEY_TRIGGER_SYNC_MANAGER = "triggerSyncManager";

	/**
	 * Key sync type.
	 * 
	 * @hide
	 */
	public static final String KEY_SYNC_TYPE = "synctype";

	/**
	 * Key sync manual.
	 * 
	 * @hide
	 */
	public static final String KEY_SYNC_MANUAL = "key_sync_manual";

	/**
	 * The pre-defined sync type Highlights.
	 * 
	 * @hide
	 */
	public static final String SYNC_TYPE_HIGHLIGHTS = "highlights";

	/**
	 * Key sync time since.
	 * 
	 * @hide
	 */
	public static final String KEY_SYNC_TIME_SINCE = "key_sync_time_since";

	/**
	 * Key manual refresh.
	 * 
	 * @hide
	 */
	public static final String KEY_MANUAL_REFRESH = "key_manual_refresh";

	/**
	 * Feature enabled account only.
	 * 
	 * @hide
	 */
	public static final String FEATURE_ENABLED_ACCOUNT_ONLY = "key_enabled_account_only";

	/**
	 * Key ustom sync setting
	 * 
	 * @hide
	 */
	public static final String KEY_CUSTOM_SYNC_SETTINGS_URI = "custom_sync_settings_uri";

	/**
	 * Action intent to bind plugins' social network service
	 * 
	 * @hide
	 */
	public static final String ACTION_PLUGIN_INTENT = "com.htc.opensense.social.SocialPlugin";

	/**
	 * @hide
	 */
	public static final String PLUGIN_META_DATA_NAME = "com.htc.opensense.social.SocialPlugin";

	/**
	 * @hide
	 */
	public static final String PLUGIN_ATTRIBUTES_NAME = "social-plugin";
	
	/**
	 * Specify the intent uri that do subscribe.
	 * 
	 * @hide
	 */
	public static final String KEY_SUBSCRIBE_URI = "custom_subscribe_uri";

	/**
	 * Specify the intent uri that do information sharing.
	 *
	 * @hide
	 */
	public static final String KEY_REPORT_DATA_URI = "custom_report_data_uri";
	
	/**
	 * Specify the BI.
	 * 
	 * @hide
	 */
	public static final String KEY_BI = "custom_bi";
	
	/**
	 * Key boolean clear content.
	 * 
	 * @hide
	 */
	public static final String KEY_BOOLEAN_CLEARCONTENT = "clear_content";

	private Context mContext;
	private ISocialManager mService;
	private final Handler mMainHandler;
	private SocialManagerConnection mConnectionListener;

	// TODO Use a cache for storing the ISocialManager mService
	/**
	 * Connect to service, need a SocialManagerConnection as a callback
	 * 
	 * @param context the given context
	 * @param connectionListener the given connection listener
	 * @return a SocialManager instance
	 * 
	 * @hide
	 */
	public static SocialManager connect(Context context,
			SocialManagerConnection connectionListener) {

		if (context == null) {
			throw new IllegalArgumentException("Context cannot be null");
		}

		if (connectionListener == null) {
			throw new IllegalArgumentException(
					"SocialManagerConnection must be provided");
		}

		return new SocialManager(context, connectionListener);
	}

	/**
	 * Unbind service, call this function when you don't need service anymore
	 * 
	 * @hide
	 */
	public void disconnect() {
		try {
			mContext.unbindService(this);
			if (mService != null) {
				try {
					mService.asBinder().unlinkToDeath(this, 0);
				} catch (NoSuchElementException e) {
					Log.e(LOG_TAG, "The DeathRecipient is not registered.", e);
				} catch (NullPointerException e) {
					Log.e(LOG_TAG,
							"ISocialManager cannot be retrieved as a Binder.",
							e);
				}
			}
		} finally {
			mContext = null;
			mService = null;
			mConnectionListener = null;
		}
	}

	private SocialManager(Context context,
			SocialManagerConnection connectionListener) {
		mContext = context;
		mMainHandler = new Handler(mContext.getMainLooper());
		mConnectionListener = connectionListener;

		Intent intent = new Intent();
		String smPackageName = SystemWrapper.getSocialManagerPackageName();
		String hspPackageName = SystemWrapper.getHspPackageName();
		ComponentName component = new ComponentName(
				hspPackageName,
				smPackageName + ".SocialManagerService"
		);
		intent.setComponent(component);
		mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	/**
	 * @hide
	 */
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mService = ISocialManager.Stub.asInterface(service);

		try {
			service.linkToDeath(this, 0);
		} catch (RemoteException e) {
			Log.e(LOG_TAG, "Binder already died.", e);
			binderDied();
			return;
		}

		if (mConnectionListener != null) {
			mConnectionListener.onConnected(this);
		}
	}

	/**
	 * @hide
	 */
	@Override
	public void onServiceDisconnected(ComponentName name) {
		if (mService != null) {
			try {
				mService.asBinder().unlinkToDeath(this, 0);
			} catch (NoSuchElementException e) {
				Log.e(LOG_TAG, "The DeathRecipient is not registered.", e);
			} catch (NullPointerException e) {
				Log.e(LOG_TAG,
						"ISocialManager cannot be retrieved as a Binder.", e);
			}
			mService = null;
		}

		if (mConnectionListener != null) {
			mConnectionListener.onDisconnected();
		}
	}

	/**
	 * @hide
	 */
	@Override
	public void binderDied() {
		if (mService != null) {
			mService = null;
		}
		if (mConnectionListener != null) {
			mConnectionListener.onBinderDied();
		}
	}

	/**
	 * Check if SocialManager is alive or not
	 * 
	 * @return if SocialManager is alive or not
	 * 
	 * @hide
	 */
	public boolean isAlive() {
		if (mService != null) {
			IBinder binder = mService.asBinder();
			return binder != null && binder.isBinderAlive();
		}
		return false;
	}

	/**
	 * Get PluginDescription array
	 * 
	 * @return PluginDescription[]
	 * 
	 * @hide
	 */
	public PluginDescription[] getPluginTypes() {
		try {
			return mService.getPluginTypes();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * request each plugin to sync stream, offset time with key
	 * SocialManager.KEY_OFFSET is required in option bundle. There are two
	 * types of sync for activity streams: one for general purpose and the
	 * other for People contact sync. To sync for People, you have to put user
	 * id array with key account type to the option bundle.
	 * 
	 * @param accounts
	 *            The accounts which you want to bind service
	 * @param options
	 *            options bundle
	 * @param callback
	 *            call back object, once operation completed you will be
	 *            notified.
	 * @param handler
	 * @return SocialManagerFuture
	 * 
	 * @hide
	 */
	public SocialManagerFuture<Bundle> syncActivityStreams(
			final Account[] accounts, final Bundle options,
			SocialManagerCallback<Bundle> callback, Handler handler) {
		return new BaseFutureTask<Bundle>(handler, callback) {

			@Override
			public void doWork() throws RemoteException {
				mService.syncActivityStreams(mResponse, accounts, options);
			}

			@Override
			public Bundle bundleToResult(Bundle bundle) throws PluginException {
				return bundle;
			}

		}.start();
	}

	/**
	 * Publish stream to each one social service
	 * 
	 * @param accounts The accounts which you want to bind service
	 * @param options options bundle
	 * @param callback call back object, once operation completed you will be notified.
	 * @param handler message handler
	 * @return SocialManagerFuture
	 * 
	 * @hide
	 */
	public SocialManagerFuture<Bundle> publishActivityStream(
			final Account[] accounts, final Bundle options,
			SocialManagerCallback<Bundle> callback, Handler handler) {
		return new BaseFutureTask<Bundle>(handler, callback) {

			@Override
			public void doWork() throws RemoteException {
				mService.publishActivityStream(mResponse, accounts, options);
			}

			@Override
			public Bundle bundleToResult(Bundle bundle) throws PluginException {
				return bundle;
			}

		}.start();
	}

	/**
	 * Trigger each social service to sync list
	 * 
	 * @param accounts The accounts which you want to bind service
	 * @param options options bundle
	 * @param callback call back object, once operation completed you will be notified.
	 * @param handler message handler
	 * @return SocialManagerFuture
	 * 
	 * @hide
	 */
	public SocialManagerFuture<Bundle> syncContacts(final Account[] accounts,
			final Bundle options, SocialManagerCallback<Bundle> callback,
			Handler handler) {
		return new BaseFutureTask<Bundle>(handler, callback) {

			@Override
			public void doWork() throws RemoteException {
				mService.syncContacts(mResponse, accounts, options);
			}

			@Override
			public Bundle bundleToResult(Bundle bundle) throws PluginException {
				return bundle;
			}

		}.start();
	}

	/**
	 * Get account list from each social plugin, which might contain
	 * some properties within the bundle with key account type
	 * 
	 * @param accountType the given account type
	 * @param features the given features
	 * @param callback call back object, once operation completed you will be notified.
	 * @param handler message handler
	 * @return SocialManagerFuture
	 * 
	 * @hide
	 */
	public SocialManagerFuture<Bundle> getDataSources(final String accountType,
			final String[] features, SocialManagerCallback<Bundle> callback,
			Handler handler) {
		return new BaseFutureTask<Bundle>(handler, callback) {

			@Override
			public void doWork() throws RemoteException {
				mService.getDataSources(mResponse, accountType, features);
			}

			@Override
			public Bundle bundleToResult(Bundle bundle) throws PluginException {
				return bundle;
			}

		}.start();
	}

	/**
	 * Get SyncType from each social plugin
	 * 
	 * @param accounts accounts The accounts which you want to bind service
	 * @param options options options bundle
	 * @param callback callback call back object, once operation completed you will be notified.
	 * @param handler message handler
	 * @return SocialManagerFuture bundle contains with SyncType
	 * 
	 * @hide
	 */
	public SocialManagerFuture<Bundle> getSyncTypes(final Account[] accounts,
			final Bundle options, SocialManagerCallback<Bundle> callback,
			Handler handler) {
		return new BaseFutureTask<Bundle>(handler, callback) {

			@Override
			public void doWork() throws RemoteException {
				mService.getSyncTypes(mResponse, accounts, options);
			}

			@Override
			public Bundle bundleToResult(Bundle bundle) throws PluginException {
				return bundle;
			}

		}.start();
	}

	/**
	 * sync social plugin to sync SyncTypes into database
	 * 
	 * @param accounts accounts The accounts which you want to bind service
	 * @param options options options bundle
	 * @param callback callback call back object, once operation completed you will be notified.
	 * @param handler message handler
	 * @return SocialManagerFuture bundle contains with SyncType
	 * 
	 * @hide
	 */
	public SocialManagerFuture<Bundle> syncSyncTypes(final Account[] accounts,
			final Bundle options, SocialManagerCallback<Bundle> callback,
			Handler handler) {
		return new BaseFutureTask<Bundle>(handler, callback) {

			@Override
			public void doWork() throws RemoteException {
				mService.syncSyncTypes(mResponse, accounts, options);
			}

			@Override
			public Bundle bundleToResult(Bundle bundle) throws PluginException {
				return bundle;
			}
		}.start();
	}
	
	/**
	 * Get subscribe intent of plugin
	 * 
	 * @param accounts accounts The accounts which you want to bind service
	 * @param options options options bundle
	 * @param callback callback call back object, once operation completed you will be notified.
	 * @param handler message handler
	 * @return SocialManagerFuture bundle contains with SyncType
	 * 
	 * @hide
	 */
	public SocialManagerFuture<Bundle> getSubscribeIntent(final Account[] accounts,
			final Bundle options, SocialManagerCallback<Bundle> callback,
			Handler handler) {
		return new BaseFutureTask<Bundle>(handler, callback) {

			@Override
			public void doWork() throws RemoteException {
				mService.getSubscribeIntent(mResponse, accounts, options);
			}

			@Override
			public Bundle bundleToResult(Bundle bundle) throws PluginException {
				return bundle;
			}
		}.start();
	}
	
	/**
	 * Get BIs of plugin
	 * 
	 * @param accounts accounts The accounts which you want to bind service
	 * @param options options options bundle
	 * @param callback callback call back object, once operation completed you will be notified.
	 * @param handler message handler
	 * @return SocialManagerFuture bundle contains with SyncType
	 * 
	 * @hide
	 */
	public SocialManagerFuture<Bundle> getBI(final Account[] accounts,
			final Bundle options, SocialManagerCallback<Bundle> callback,
			Handler handler) {
		return new BaseFutureTask<Bundle>(handler, callback) {

			@Override
			public void doWork() throws RemoteException {
				mService.getBI(mResponse, accounts, options);
			}

			@Override
			public Bundle bundleToResult(Bundle bundle) throws PluginException {
				return bundle;
			}
		}.start();
	}
	
	

	/**
	 * put sync type id array into bundle
	 * @param bundle the target bundle, usually the syncOption
	 * @param accountType the account type that the sync type ids belong to
	 * @param syncTypeIds sync types
	 * 
	 * @hide
	 */
	public static void putSyncTypeIds(Bundle bundle, String accountType,
			String[] syncTypeIds) {
		if (bundle != null && !TextUtils.isEmpty(accountType)
				&& syncTypeIds != null && syncTypeIds.length > 0) {
			Bundle b = new Bundle();
			b.putStringArray(SocialManager.KEY_SYNC_TYPE, syncTypeIds);
			bundle.putBundle(accountType, b);
		}
	}

	/**
	 * parse SyncType item in bundle
	 * @param bundle bundle get from getSyncTypes()
	 * @param accountType account type of the social plugin
	 * @return SyncType array
	 * 
	 * @hide
	 */
	public static SyncType[] parseSyncType(Bundle bundle, String accountType) {
		Bundle b = bundle.getBundle(accountType);
		if (b != null) {
			return SyncType.getArrayFromBundle(b, SocialManager.KEY_SYNC_TYPE);
		}
		return null;
	}

	/**
	 * parse Account array in bundle
	 * @param bundle bundle get from getDataSources()
	 * @return Account array
	 * 
	 * @hide
	 */
	public static Account[] parseAccount(Bundle bundle) {
		Parcelable[] parcelables = bundle
				.getParcelableArray(SocialManager.KEY_ACCOUNTS);
		if (parcelables != null) {
			final int size = parcelables.length;
			Account[] descs = new Account[size];
			for (int i = 0; i < size; i++) {
				descs[i] = (Account) parcelables[i];
			}
			return descs;
		}
		return null;
	}

	/**
	 * Parses properties bundle get by getDataSources
	 * 
	 * @param bundle bundle get by getDataSources
	 * @param accountType accountType account type of the social plugin
	 * @return Bundle get from plugin
	 * 
	 * @hide
	 */
	public static Bundle parseProperties(Bundle bundle, String accountType) {
		Bundle b = bundle.getBundle(accountType);
		if (b != null) {
			return b.getBundle(SocialManager.KEY_PROPERTIES);
		}
		return null;
	}

	/**
	 * A connection listener interface for SocialManager
	 * 
	 * @hide
	 */
	public static interface SocialManagerConnection {

		/**
		 * Called when connected to SocialManager
		 * 
		 * @param manager the given SocialManager instance
		 * 
		 * @hide
		 */
		public void onConnected(SocialManager manager);

		/**
		 * Called when disconnected from SocialManager
		 * 
		 * @hide
		 */
		public void onDisconnected();

		/**
		 * Called when the binder died
		 * 
		 * @hide
		 */
		public void onBinderDied();

	}

	private abstract class BaseFutureTask<T> extends FutureTask<T> implements
			SocialManagerFuture<T> {

		/**
		 * @hide
		 */
		final public ISocialManagerResponse mResponse;
		final Handler mHandler;
		final SocialManagerCallback<T> mCallback;

		/**
		 * @hide
		 */
		public BaseFutureTask(Handler handler, SocialManagerCallback<T> callback) {
			super(new Callable<T>() {
				@Override
				public T call() throws Exception {
					throw new IllegalStateException(
							"this should never be called");
				}
			});
			mHandler = handler;
			mCallback = callback;
			mResponse = new Response();
		}

		/**
		 * @hide
		 */
		public abstract void doWork() throws RemoteException;

		/**
		 * @hide
		 */
		public abstract T bundleToResult(Bundle bundle) throws PluginException;

		/**
		 * @hide
		 */
		protected void postRunnableToHandler(Runnable runnable) {
			Handler handler = (mHandler == null) ? mMainHandler : mHandler;
			handler.post(runnable);
		}

		/**
		 * @hide
		 */
		public BaseFutureTask<T> start() {
			startTask();
			return this;
		}

		/**
		 * @hide
		 */
		protected void startTask() {
			try {
				doWork();
			} catch (RemoteException e) {
				setException(e);
			}
		}

		/**
		 * @hide
		 */
		@Override
		protected void done() {
			if ( mCallback != null ) {
				postRunnableToHandler(new Runnable() {

					@Override
					public void run() {
						mCallback.run(BaseFutureTask.this);
					}

				});
			}
		}

		private T internalGetResult(Long timeout, TimeUnit unit)
				throws OperationCanceledException, IOException, PluginException {
			if ( !isDone() ) {
				ensureNotOnMainThread();
			}
			try {
				if ( timeout == null ) {
					return get();
				} else {
					return get(timeout, unit);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				// fall through and cancel
			} catch (TimeoutException e) {
				e.printStackTrace();
				// fall through and cancel
			} catch (CancellationException e) {
				e.printStackTrace();
				// fall through and cancel
			} catch (ExecutionException e) {
				final Throwable cause = e.getCause();
				if (cause instanceof IOException) {
					throw (IOException) cause;
				} else if (cause instanceof UnsupportedOperationException) {
					throw new PluginException(cause);
				} else if (cause instanceof PluginException) {
					throw (PluginException) cause;
				} else if (cause instanceof RuntimeException) {
					throw (RuntimeException) cause;
				} else if (cause instanceof Error) {
					throw (Error) cause;
				} else {
					throw new IllegalStateException(cause);
				}
			} finally {
				cancel(true /* interrupt if running */);
			}
			throw new OperationCanceledException();
		}

		/**
		 * @hide
		 */
		@Override
		public T getResult() throws OperationCanceledException, IOException,
				PluginException {
			return internalGetResult(null, null);
		}

		/**
		 * @hide
		 */
		@Override
		public T getResult(long timeout, TimeUnit unit)
				throws OperationCanceledException, IOException, PluginException {
			return internalGetResult(timeout, unit);
		}

		/**
		 * @hide
		 */
		protected class Response extends ISocialManagerResponse.Stub {

			/**
			 * @hide
			 */
			@Override
			public void onResult(Bundle value) throws RemoteException {
				try {
					T result = bundleToResult(value);
					if (result == null) {
						return;
					}
					set(result);
					return;
				} catch (ClassCastException e) {
					// we will set the exception below
				} catch (PluginException e) {
					// we will set the exception below
				}
				onError(ERROR_CODE_INVALID_RESPONSE, "no result in response");
			}

			/**
			 * @hide
			 */
			@Override
			public void onError(int errorCode, String errorMessage)
					throws RemoteException {
				if (errorCode == ERROR_CODE_CANCELED) {
					cancel(true /* mayInterruptIfRunning */);
					return;
				}
				setException(convertErrorToException(errorCode, errorMessage));
			}

		}
	}

	private void ensureNotOnMainThread() {
		final Looper looper = Looper.myLooper();
		if(mContext == null)
		{
			final IllegalStateException exception = new IllegalStateException(
					"The context has been set to null");
			Log.e(LOG_TAG,
					"The context has been set to null",
					exception);
			
			throw exception;
		}
		else if (looper != null && looper == mContext.getMainLooper()) {
			final IllegalStateException exception = new IllegalStateException(
					"calling this from your main thread can lead to deadlock");
			Log.e(LOG_TAG,
					"calling this from your main thread can lead to deadlock and/or ANRs",
					exception);
			if (mContext.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.FROYO) {
				throw exception;
			}
		}
	}

	private Exception convertErrorToException(int code, String message) {
		if (code == ERROR_CODE_NETWORK_ERROR) {
			return new IOException(message);
		}

		if (code == ERROR_CODE_UNSUPPORTED_OPERATION) {
			return new UnsupportedOperationException(message);
		}

		if (code == ERROR_CODE_INVALID_RESPONSE) {
			return new PluginException(message);
		}

		if (code == ERROR_CODE_BAD_ARGUMENTS) {
			return new IllegalArgumentException(message);
		}

		return new PluginException(message);
	}
}
