package com.htc.lib2.mock.opensense.social;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import com.htc.lib2.mock.opensense.social.SocialScheduler.SyncUtils;
import com.htc.lib2.mock.opensense.social.provider.LocalMergeHelper;
import com.htc.lib2.mock.opensense.social.provider.LocalPluginHelper;
import com.htc.lib2.mock.opensense.social.provider.SocialDatabase;
import com.htc.lib2.mock.opensense.social.provider.SocialProvider.Utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Message;
import android.os.Parcelable;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.htc.lib2.opensense.social.AbstractSocialManager;
import com.htc.lib2.opensense.social.ISocialManagerResponse;
import com.htc.lib2.opensense.social.ISocialPlugin;
import com.htc.lib2.opensense.social.ISocialPluginResponse;
import com.htc.lib2.opensense.social.MergeHelper;
import com.htc.lib2.opensense.social.PluginDescription;
import com.htc.lib2.opensense.social.SocialContract.Stream;
import com.htc.lib2.opensense.social.SocialContract.SyncCursors;
import com.htc.lib2.opensense.social.SocialContract.SyncTypeContract;
import com.htc.lib2.opensense.social.SocialManager;
import com.htc.lib2.opensense.social.SyncType;

public class SocialManagerService extends Service {

	private static final String LOG_TAG = SocialManagerService.class
			.getSimpleName();

	private final SocialManagerStub mStub = new SocialManagerStub();
	
	private List<RequestSession> mOngoingSessions = new ArrayList<RequestSession>();
	
	@Override
	public IBinder onBind(Intent intent) {
		return mStub.getIBinder();
	}

	@Override
	public void onDestroy() {
		List<RequestSession> clone = new ArrayList<RequestSession>();
		clone.addAll(mOngoingSessions);
		for (RequestSession request : clone) {
			if (request != null) {
				request.close();
			}
		}
		mOngoingSessions.clear();
		super.onDestroy();
	}
	
	public final static String getPluginPropPrefKey(String accountType, String key) {
		return "pref_" + key + "_" + accountType;
	}

	private class SocialManagerStub extends AbstractSocialManager {
		
		private final String[] storingPrefKeys = new String[]{
				SocialManager.KEY_PROP_DEFAULT_SYNC_TYPE_ID,
				SocialManager.KEY_PROP_BOOL_SUPPORT_PERSONAL_CHANNEL,
				SocialManager.KEY_PROP_BOOL_SUPPORT_HIGHLIGHTS_FEATURE,
				"key_prop_bd_ignore_sync_interval"};

		@Override
		public PluginDescription[] getPluginTypes() throws RemoteException {
			//@deprecated
			//return PluginManager.getFriendStreamPluginTypes(getBaseContext());
			return null;
		}

		@Override
		public void syncActivityStreams(ISocialManagerResponse response,
				Account[] accounts, Bundle options) throws RemoteException {
			Log.v(LOG_TAG, "syncActivityStreams!");
			if (accounts == null || accounts.length == 0) {
				throw new IllegalArgumentException("accounts is null or empty");
			}
			if (options == null) {
				throw new IllegalArgumentException("options is null");
			}

			RequestSession request = new RequestSession(response, accounts, options) {

				@Override
				public void runPlugin(PluginSession pluginSession,
						Account[] accounts, Bundle options)
						throws RemoteException {
					pluginSession.mPlugin.syncActivityStreams(pluginSession,
							accounts, options);
				}

				@Override
				void onPluginProgress(Account[] accounts, Bundle value) {
					if (value != null && value.containsKey("stream")) {
						ArrayList<ContentValues> valueArray = value
								.getParcelableArrayList("stream");

						if (valueArray != null) {
							Log.i(LOG_TAG, "length " + valueArray.size());
							Log.i(LOG_TAG,
									"start writing to db by social manager "
											+ accounts[0].type);
							boolean syncByContactId = mOptions
									.containsKey(SocialManager.KEY_CONTACTS);
							if (syncByContactId) {
								if (!valueArray.isEmpty()) {
									MergeHelper.getInstance(getBaseContext())
											.insertStreamToDb(valueArray);
								}
							} else {
								long startTime = 0;
								long offset = mOptions
										.getLong(SocialManager.KEY_OFFSET);
								Bundle pluginBundle = mOptions
										.getBundle(accounts[0].type);
								if (valueArray.isEmpty()) {
									if (pluginBundle != null && offset == 0) {
										startTime = pluginBundle
												.getLong(
														SocialManager.KEY_SYNC_TIME_SINCE,
														0);
									}
								} else {
									startTime = getMinimumTimestamp(valueArray);
								}
								String token = value.getString("page_token");
								boolean wipeOldData = value.getBoolean(
										"wipeOldData", false);
								String[] syncTypeIds = null;
								Bundle accountOption = mOptions
										.getBundle(accounts[0].type);
								if (accountOption != null) {
									syncTypeIds = accountOption
											.getStringArray(SocialManager.KEY_SYNC_TYPE);
								}
								if (syncTypeIds == null
										|| syncTypeIds.length == 0
										|| SocialManager.SYNC_TYPE_HIGHLIGHTS
												.equals(syncTypeIds[0])) {
									syncTypeIds = new String[] { SocialManager.SYNC_TYPE_HIGHLIGHTS };
								}

								LocalMergeHelper
										.getInstance(getBaseContext())
										.mergeStreamToDb(
												offset == 0 ? System.currentTimeMillis()
														: offset, startTime,
												accounts[0], valueArray,
												syncTypeIds, token, wipeOldData);
							}
							Log.i(LOG_TAG,
									"finish writing to write db by social manager "
											+ accounts[0].type);
						}
						value.remove("stream");
					}
					super.onPluginProgress(accounts, value);
				}

				private long getMinimumTimestamp(List<ContentValues> valueArray) {
					long minTimeStamp = 0;
					if (valueArray != null
							&& valueArray.size() > 0
							&& valueArray.get(0).getAsLong(
									Stream.COLUMN_TIMESTAMP_LONG) != null) {
						minTimeStamp = valueArray.get(0).getAsLong(
								Stream.COLUMN_TIMESTAMP_LONG);

						for (ContentValues values : valueArray) {
							if (values != null
									&& values
											.getAsLong(Stream.COLUMN_TIMESTAMP_LONG) != null
									&& values
											.getAsLong(Stream.COLUMN_TIMESTAMP_LONG) < minTimeStamp) {
								minTimeStamp = values
										.getAsLong(Stream.COLUMN_TIMESTAMP_LONG);
							}
						}
					}

					return minTimeStamp;
				}
				
				@Override
				void onSessionCompleted() {
					super.onSessionCompleted();
					if (!mOptions.containsKey(SocialManager.KEY_CONTACTS)
							&& !mOptions.getBoolean(
									SocialManager.KEY_TRIGGER_SYNC_MANAGER,
									false)
							&& mOptions.getBoolean(
									SocialManager.KEY_SYNC_MANUAL, true)
							&& mOptions.getBoolean(
									ContentResolver.SYNC_EXTRAS_MANUAL, true)) {
						getBaseContext()
								.startService(
										new Intent(
												SocialScheduler.ACTION_HANDLE_CLEAN_DATA)
												.setClass(
														getApplicationContext(),
														SocialScheduler.class));
						try {
							getBaseContext()
									.startService(
											new Intent(
													"com.htc.sense.hsp.HANDLE_ULOG")
													.setPackage("com.htc.launcher"));
						} catch (Exception e) {
							Log.e(LOG_TAG,
									"exception when starting handle ulog service");
						}
					}
				}
			};
			mOngoingSessions.add(request);
			if (options.getLong(SocialManager.KEY_OFFSET, 0l) == 0l) {
				//refresh.
				request.startWithSyncHeuristics(options);
			} else {
				//offset != 0 should be loadmore.
				request.start();
			}
		}

		@Override
		public void publishActivityStream(ISocialManagerResponse response,
				Account[] accounts, Bundle options) throws RemoteException {
			if (accounts == null || accounts.length == 0) {
				throw new IllegalArgumentException("accounts is null or empty");
			}
			if (options == null) {
				throw new IllegalArgumentException("options is null");
			}

			RequestSession request = new RequestSession(response, accounts, options) {

				@Override
				public void runPlugin(PluginSession pluginSession,
						Account[] accounts, Bundle options)
						throws RemoteException {
					pluginSession.mPlugin.publishActivityStream(pluginSession,
							accounts, options);
				}

			};
			mOngoingSessions.add(request);
			request.start();
		}

		@Override
		public void syncContacts(ISocialManagerResponse response,
				Account[] accounts, Bundle options) throws RemoteException {
			if (accounts == null || accounts.length == 0) {
				throw new IllegalArgumentException("accounts is null or empty");
			}
			if (options == null) {
				throw new IllegalArgumentException("options is null");
			}

			RequestSession request = new RequestSession(response, accounts, options) {

				@Override
				public void runPlugin(PluginSession pluginSession,
						Account[] accounts, Bundle options)
						throws RemoteException {
					pluginSession.mPlugin.syncContacts(pluginSession, accounts,
							options);
//					if (!options
//							.containsKey(SocialManager.KEY_TRIGGER_SYNC_MANAGER)) {
//						getBaseContext().startService(new Intent(SocialScheduler.ACTION_RESCHEDULE_CONTACT_SYNC));
//					}
				}

			};
			mOngoingSessions.add(request);
			request.start();
		}

		@Override
		public void getDataSources(ISocialManagerResponse response,
				String accountType, String[] features) throws RemoteException {
			Log.v(LOG_TAG, "getDataSources");
			if (response == null)
				throw new IllegalArgumentException("response is null");

			//generate fake account
			ArrayList<Account> list = new ArrayList<Account>();
			if (TextUtils.isEmpty(accountType)) {
				// scan all plugin account
				String[] accountTypes = LocalPluginHelper
						.getBlinkFeedPluginAccountTypes(getBaseContext());
				for (String pluginType : accountTypes) {
					// judge enable or not
					if (features != null && features.length > 0
							&& shouldCheckAccountEnabling(features)
							&& !isAccountEnabled(pluginType)) {
						continue;
					}
					Log.i(LOG_TAG, "plugin added: " + pluginType);
					list.add(new Account(pluginType, pluginType));
				}
			} else {
				list.add(new Account(accountType, accountType));
			}
			
			Account[] accounts = list.toArray(new Account[list.size()]);
			
			Bundle options = new Bundle();
			options.putStringArray(SocialManager.KEY_FEATURES, features);

			RequestSession request = new RequestSession(response, accounts, options) {

				@Override
				public void runPlugin(PluginSession pluginSession,
						Account[] accounts, Bundle options)
						throws RemoteException {
					String[] features = options
							.getStringArray(SocialManager.KEY_FEATURES);
					pluginSession.mPlugin.getDataSources(pluginSession,
							features);
				}

				@Override
				void onSessionCompleted() {
					super.onSessionCompleted();

					ArrayList<Account> accounts = new ArrayList<Account>();
					for (Account account : mAccounts) {
						String accountType = account.type;
						if (mValues != null
								&& mValues.getBundle(accountType) != null) {
							final Parcelable[] parcelables = mValues.getBundle(
									accountType).getParcelableArray(
									SocialManager.KEY_ACCOUNTS);
							if (parcelables != null) {
								final int size = parcelables.length;
								for (int i = 0; i < size; i++) {
									accounts.add((Account) parcelables[i]);
								}
							}
						}
					}
					if (mValues != null) {
						mValues.putParcelableArray(SocialManager.KEY_ACCOUNTS,
								accounts.toArray(new Account[0]));
					} else {
						Log.e(LOG_TAG, "mValues is null!");
					}
				}

				@Override
				void onPluginProgress(Account[] accounts, Bundle pluginBundle) {
					setAccountInfo(accounts[0].type, pluginBundle);
					savePluginProperty(accounts[0].type, pluginBundle);
					super.onPluginProgress(accounts, pluginBundle);
				}

				private void setAccountInfo(String accountType,
						Bundle pluginBundle) {
					if (pluginBundle != null) {
						Bundle pluginProp = pluginBundle
								.getBundle(SocialManager.KEY_PROPERTIES);
						if (pluginProp != null) {
							int type = pluginProp
									.getInt(SocialManager.KEY_PROP_IDENTITY_PROVIDER_TYPE,
											SocialManager.IDENTITY_TYPE_REAL_ACCOUNT);
							if (type == SocialManager.IDENTITY_TYPE_REAL_ACCOUNT) {
								AuthenticatorDescription desc = null;
								if (!pluginProp
										.containsKey(SocialManager.KEY_PROP_ACCOUNT_LABEL_ID)) {
									if (desc == null) {
										desc = getAccountDescription(accountType);
									}
									if (desc != null) {
										pluginProp
												.putInt(SocialManager.KEY_PROP_ACCOUNT_LABEL_ID,
														desc.labelId);
										pluginProp
												.putString(
														SocialManager.KEY_PROP_PACKAGE_NAME,
														desc.packageName);
									}
								}

								if (!pluginProp
										.containsKey(SocialManager.KEY_PROP_ACCOUNT_ICON_ID)) {
									if (desc == null) {
										desc = getAccountDescription(accountType);
									}

									if (desc != null) {
										pluginProp
												.putInt(SocialManager.KEY_PROP_ACCOUNT_ICON_ID,
														desc.iconId);
										pluginProp
												.putString(
														SocialManager.KEY_PROP_PACKAGE_NAME,
														desc.packageName);
									}
								}
							}
						} else {
							Log.e(LOG_TAG, "plugin property bundle is null!");
						}
					} else {
						Log.e(LOG_TAG, "plugin bundle is null!");
					}
				}
				
				private AuthenticatorDescription getAccountDescription(
						String accountType) {
					for (AuthenticatorDescription desc : AccountManager.get(
							getBaseContext()).getAuthenticatorTypes()) {
						if (desc.type.equals(accountType)) {
							return desc;
						}
					}
					return null;
				}
			};
			mOngoingSessions.add(request);
			request.start();
		}
		
		@Override
		public void syncSyncTypes(ISocialManagerResponse response,
				Account[] accounts, Bundle options) throws RemoteException {
			if (accounts == null || accounts.length == 0) {
				throw new IllegalArgumentException("accounts is null or empty");
			}
			if (options == null) {
				throw new IllegalArgumentException("options is null");
			}

			RequestSession request = new RequestSession(response, accounts,
					options) {
				@Override
				public void runPlugin(PluginSession pluginSession,
						Account[] accounts, Bundle options)
						throws RemoteException {
					pluginSession.mPlugin.syncSyncTypes(pluginSession,
							accounts, options);
				}
				
				@Override
				void onPluginProgress(Account[] accounts, Bundle value) {
					if (value != null && value.containsKey("synctype")) {
						ArrayList<SyncType> synctypes = SyncType
								.getArrayListFromBundle(value, "synctype");
						if (synctypes != null) {
							boolean wipeOldData = value.getBoolean(
									"wipeOldData", true);
							if (synctypes.size() > 0) {
								MergeHelper
										.getInstance(getApplicationContext())
										.insertSyncTypeToDb(synctypes,
												accounts[0].name,
												accounts[0].type, wipeOldData);
							}
							Log.i(LOG_TAG,
									"insert sync types by social manager, count: "
											+ synctypes.size());
						}

					}
					super.onPluginProgress(accounts, value);
				}
			};
			mOngoingSessions.add(request);
			request.start();
		}

		@Override
		public void getSyncTypes(ISocialManagerResponse response,
				Account[] accounts, Bundle options) throws RemoteException {
			Log.v(LOG_TAG, "getSyncTypes");
			if (response == null)
				throw new IllegalArgumentException("response is null");

			RequestSession request = new RequestSession(response, accounts,
					options) {

				@Override
				public void runPlugin(PluginSession pluginSession,
						Account[] accounts, Bundle options)
						throws RemoteException {
					pluginSession.mPlugin.getSyncTypes(pluginSession, accounts,
							options);
				}
				
				@Override
				public void start() {
					HashMap<String, HashSet<Account>> groupedAccounts = groupAccounts();

					//put news plugin into plugin session that attempt to bind
					if (groupedAccounts.keySet().contains(NEWS_ACCOUNT_TYPE)) {
						handleAddToPluginSessions(
								mPluginSessions,
								new PluginSession(groupedAccounts.get(
										NEWS_ACCOUNT_TYPE).toArray(
										new Account[0]), 0));
					}
					
					Cursor cursor = getBaseContext()
							.getContentResolver()
							.query(SyncTypeContract
									.buildUriWithAccounts(mAccounts),
									null,
									null,
									null,
									SyncTypeContract.COLUMN_TITLE_STR
											+ " COLLATE NOCASE ASC,"
											+ SyncTypeContract.COLUMN_TITLE_RES_NAME_STR
											+ " COLLATE NOCASE ASC");
					if (cursor != null) {
						HashMap<String, ArrayList<SyncType>> groupedSyncType = groupEnabledSyncType(cursor);
						cursor.close();

						if (groupedSyncType != null) {
							for (String accountType : groupedAccounts.keySet()) {
								ArrayList<SyncType> syncTypes = groupedSyncType
										.get(accountType);
								if (syncTypes != null) {
									Bundle bundle = new Bundle();
									SyncType.putIntoBundleAsArray(bundle,
											SocialManager.KEY_SYNC_TYPE,
											syncTypes);
									mValues.putBundle(accountType, bundle);
								}
							}
						}
					}
					
					if (mPluginSessions.isEmpty()) {
						handleResponseAndCallback();
					} else {
						handlePluginSessionsBinding();
					}
				}
				
				@Override
				void onPluginProgress(Account[] accounts, Bundle value) {
					if (mValues.containsKey(accounts[0].type)) {
						// means there is already sync type data in returning
						// mValues, combine data instead of replacing it.
						Bundle syncTypeBundle = mValues
								.getBundle(accounts[0].type);
						List<SyncType> localList = parseSyncType(syncTypeBundle);
						if (localList != null) {
							// merge sync type list
							ArrayList<SyncType> mergedList = new ArrayList<SyncType>();
							mergedList.addAll(localList);
							
							List<SyncType> targetList = parseSyncType(value);
							if (targetList != null) {
								mergedList.addAll(targetList);
							}
							SyncType.putIntoBundleAsArray(value,
									SocialManager.KEY_SYNC_TYPE, mergedList);
						}
					}
					super.onPluginProgress(accounts, value);
				}
				
				private List<SyncType> parseSyncType(Bundle b) {
					if (b != null) {
						SyncType[] syncTypes = SyncType.getArrayFromBundle(b,
								SocialManager.KEY_SYNC_TYPE);
						if (syncTypes != null && syncTypes.length > 0) {
							return Arrays.asList(syncTypes);
						}
					}
					return null;
				}
				
				private HashMap<String, ArrayList<SyncType>> groupEnabledSyncType(
						Cursor cursor) {
					HashMap<String, ArrayList<SyncType>> groupedSyncType = new HashMap<String, ArrayList<SyncType>>();
					if (cursor == null) {
						return groupedSyncType;
					}
					try {
						int index_id = cursor.getColumnIndex(SyncTypeContract.COLUMN_IDENTITY_STR);
						int index_title = cursor.getColumnIndex(SyncTypeContract.COLUMN_TITLE_STR);
						int index_title_res_name = cursor.getColumnIndex(SyncTypeContract.COLUMN_TITLE_RES_NAME_STR);
						int index_subtitle = cursor.getColumnIndex(SyncTypeContract.COLUMN_SUB_TITLE_STR);
						int index_subtitle_res_name = cursor.getColumnIndex(SyncTypeContract.COLUMN_SUB_TITLE_RES_NAME_STR);
						int index_account_type = cursor.getColumnIndex(SyncTypeContract.COLUMN_ACCOUNT_TYPE_STR);
						int index_category = cursor.getColumnIndex(SyncTypeContract.COLUMN_CATEGORY_STR);
						int index_category_res_name = cursor.getColumnIndex(SyncTypeContract.COLUMN_CATEGORY_RES_NAME_STR);
						int index_edition = cursor.getColumnIndex(SyncTypeContract.COLUMN_EDITION_STR);
						int index_edition_res_name = cursor.getColumnIndex(SyncTypeContract.COLUMN_EDITION_RES_NAME_STR);
						int index_pacakge_name = cursor.getColumnIndex(SyncTypeContract.COLUMN_PACKAGE_NAME_STR);
						int index_color = cursor.getColumnIndex(SyncTypeContract.COLUMN_COLOR_INT);
						int index_icon_res = cursor.getColumnIndex(SyncTypeContract.COLUMN_ICON_RES_NAME_STR);
						int index_icon_url = cursor.getColumnIndex(SyncTypeContract.COLUMN_ICON_URL_STR);
						
						int index_enabled = cursor.getColumnIndex(SyncTypeContract.COLUMN_ENABLED_INT);

						while (cursor.moveToNext()) {
							String accountType = cursor
									.getString(index_account_type);
							String id = cursor.getString(index_id);
							if (TextUtils.isEmpty(accountType)) {
								continue;
							}
							
							boolean enabled = (cursor.getInt(index_enabled) == 1);
							
							if (!enabled && !forceEnabled(accountType)) {
								continue;
							}

							ArrayList<SyncType> list = groupedSyncType
									.get(accountType);
							if (list == null) {
								list = new ArrayList<SyncType>();
								if (isPluginSupportHighlight(accountType)
										&& !accountType
												.equals(NEWS_ACCOUNT_TYPE)) {
									// we add default sync type here
									// news is a special case that it support
									// highlight but return sync type by itself,
									// so we don't add default sync type for it.
									list.add(getDefaultSyncType(accountType));
								}
								groupedSyncType.put(accountType, list);
							}
							SyncType syncType = new SyncType();
							syncType.setId(id);
							syncType.setTitle(cursor.getString(index_title));
							syncType.setTitleResName(cursor
									.getString(index_title_res_name));
							syncType.setSubTitle(cursor
									.getString(index_subtitle));
							syncType.setSubTitleResName(cursor.getString(index_subtitle_res_name));
							syncType.setPackageName(cursor.getString(index_pacakge_name));
							syncType.setEdition(cursor.getString(index_edition));
							syncType.setEditionResName(cursor.getString(index_edition_res_name));
							syncType.setCategory(cursor.getString(index_category));
							syncType.setCategoryResName(cursor.getString(index_category_res_name));
							syncType.setColor(cursor.getInt(index_color));
							syncType.setIconResName(cursor.getString(index_icon_res));
							syncType.setIconUrl(cursor.getString(index_icon_url));
							list.add(syncType);
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
					return groupedSyncType;
				}
				
				private boolean forceEnabled(String accountType) {
					if (isPluginSupportPersonalChannel(accountType)) {
						return true;
					}
					return false;
				}

				private SyncType getDefaultSyncType(String accountType) {
					SyncType syncType = new SyncType();
					syncType.setId(PreferenceManager
							.getDefaultSharedPreferences(getBaseContext())
							.getString(getDefaultSyncTypeIdKey(accountType),
									SocialManager.SYNC_TYPE_HIGHLIGHTS));
					syncType.setTitle("All");
					syncType.setTitleResName("filter_name_all");
					syncType.setPackageName(SocialManagerService.this
							.getPackageName());
					return syncType;
				}
			};
			mOngoingSessions.add(request);
			request.start();
		}
		
		private boolean isPluginSupportPersonalChannel(String accountType) {
			return PreferenceManager
					.getDefaultSharedPreferences(getBaseContext())
					.getBoolean(
							getPluginPropPrefKey(
									accountType,
									SocialManager.KEY_PROP_BOOL_SUPPORT_PERSONAL_CHANNEL),
							false);
		}
		
		private boolean isPluginSupportHighlight(String accountType) {
			return PreferenceManager
					.getDefaultSharedPreferences(getBaseContext())
					.getBoolean(
							getPluginPropPrefKey(
									accountType,
									SocialManager.KEY_PROP_BOOL_SUPPORT_HIGHLIGHTS_FEATURE),
							true);
		}

		private final String getDefaultSyncTypeIdKey(String accountType) {
			return getPluginPropPrefKey(accountType,
					SocialManager.KEY_PROP_DEFAULT_SYNC_TYPE_ID);
		}
		
		private boolean isAccountEnabled(String type) {
			return PreferenceManager.getDefaultSharedPreferences(
					getBaseContext()).getBoolean(genAccountEnablingKey(type),
					true);
		}

		private String genAccountEnablingKey(String type) {
			return "key_enable_account_" + type;
		}
		
		private boolean shouldCheckAccountEnabling(String[] features) {
			for (String feature : features) {
				if ("key_enabled_account_only".equals(feature)) {
					return true;
				}
			}
			return false;
		}
		
		private void savePluginProperty(String accountType, Bundle pluginBundle) {
			if (pluginBundle != null) {
				Bundle pluginProp = pluginBundle
						.getBundle(SocialManager.KEY_PROPERTIES);
				if (pluginProp != null) {
					boolean shouldApply = false;

					Editor editor = PreferenceManager
							.getDefaultSharedPreferences(getBaseContext())
							.edit();
					for (String key : storingPrefKeys) {
						if (pluginProp.containsKey(key)) {

							Object value = pluginProp.get(key);
							String prefKey = getPluginPropPrefKey(accountType,
									key);
							if (value instanceof Boolean) {
								editor.putBoolean(prefKey, (Boolean) value);
								shouldApply = true;
							} else if (value instanceof String) {
								editor.putString(prefKey, (String) value);
								shouldApply = true;
							} else if (value instanceof Integer) {
								editor.putInt(prefKey, (Integer) value);
								shouldApply = true;
							}
						}
					}
					if (shouldApply) {
						editor.apply();
					}
				} else {
					Log.e(LOG_TAG, "plugin prop bundle is null! " + accountType);
				}
			}
		}
	}

	private abstract class RequestSession implements DeathRecipient {

		private ISocialManagerResponse mResponse;
		final Account[] mAccounts;
		final Bundle mOptions;
		protected final LinkedList<PluginSession> mPluginSessions;
		protected final ConcurrentHashMap<String, Set<String>> mWorkingProcessPluginMap;
		final Bundle mValues;
		protected final PriorityBlockingQueue<PluginSession> mPendingAccountQueue;
		
		private HandlerThread mWorkerThread = new HandlerThread("worker thread");
		private Handler mTimeoutHandler = null;

		protected HashMap<String, HashSet<Account>> groupAccounts() {
			Log.i(LOG_TAG,"groupAccounts");
			HashMap<String, HashSet<Account>> groupedAccounts = new HashMap<String, HashSet<Account>>();
			for (Account account : mAccounts) {
				HashSet<Account> uniqueSet = groupedAccounts.get(account.type);
				if (uniqueSet == null) {
					uniqueSet = new HashSet<Account>();
					groupedAccounts.put(account.type, uniqueSet);
				}
				uniqueSet.add(account);
			}
			return groupedAccounts;
		}

		public RequestSession(ISocialManagerResponse response,
				Account[] accounts, Bundle options) {
			if (response == null) {
				throw new IllegalArgumentException("response is null");
			}
			if (accounts == null) {
				throw new IllegalArgumentException("accounts is null");
			}
			if (options == null) {
				throw new IllegalArgumentException("options is null");
			}
			mResponse = response;
			mAccounts = accounts;
			mOptions = options;
			mValues = new Bundle();

			mWorkingProcessPluginMap = new ConcurrentHashMap<String, Set<String>>();
			mPendingAccountQueue = new PriorityBlockingQueue<PluginSession>();

			mPluginSessions = new LinkedList<PluginSession>();

			try {
				mResponse.asBinder().linkToDeath(this, 0);
			} catch (Exception e) {
				mResponse = null;
				binderDied();
			}
			
			mWorkerThread.start();
			mTimeoutHandler = new Handler(mWorkerThread.getLooper()) {
				@Override
				public void handleMessage(Message msg) {
					if (msg.what == PluginSession.MSG_OPERATION_TIMEOUT) {
						if (msg.obj instanceof PluginSession) {
							PluginSession session = (PluginSession) msg.obj;
							Log.e(LOG_TAG, "plugin: " + session.mAccountType
									+ " timeout!");
							session.setError(
									SocialManager.ERROR_CODE_REMOTE_EXCEPTION,
									"remote timeout");
						}
					}
				}
			};
		}

		ISocialManagerResponse getResponseAndClose() {
			if (mResponse == null) {
				// this session has already been closed
				return null;
			}

			ISocialManagerResponse response = mResponse;
			close(); // this clears mResponse so we need to save the response
						// before this call
			return response;
		}

		private void close() {
			if (mResponse != null) {
				// stop listening for response deaths
				try {
					mResponse.asBinder().unlinkToDeath(this, 0);
				} catch (NoSuchElementException e) {
					Log.e(LOG_TAG, "NoSuchElementException");
				} catch (Exception e) {
					Log.e(LOG_TAG, "Unexpected Exception");
				}

				// clear this so that we don't accidentally send any further
				// results
				mResponse = null;
			}

			for (PluginSession pluginSession : mPluginSessions) {
				pluginSession.unbind();
			}

			mOngoingSessions.remove(this);
			mWorkerThread.quit();
		}

		@Override
		public void binderDied() {
			mResponse = null;
			close();
		}

		public abstract void runPlugin(PluginSession plugin,
				Account[] accounts, Bundle options) throws RemoteException;

		public void start() {
			HashMap<String, HashSet<Account>> groupedAccounts = groupAccounts();
			for (HashSet<Account> collection : groupedAccounts.values()) {
				handleAddToPluginSessions(mPluginSessions, new PluginSession(
						collection.toArray(new Account[0]), 0));
			}
			
			if (mPluginSessions.isEmpty()) {
				handleResponseAndCallback();
			} else {
				handlePluginSessionsBinding();
			}
		}
		
		public void startWithSyncHeuristics(Bundle option) {
			Log.v(LOG_TAG,"startWithSyncHeuristics");
			// Determine whether to make the Binder call or not
			// based on sync heuristics
			HashMap<String, HashSet<Account>> groupedAccounts = groupAccounts();
			for (HashSet<Account> collection : groupedAccounts.values()) {
				if (collection != null) {
					Account[] accounts = collection.toArray(new Account[0]);
					if (accounts == null || accounts.length < 1) {
						throw new IllegalArgumentException("accounts is null");
					}
					long lastSuccessfulTime = getLastSuccessfulTime(accounts, option);
					if (shouldBindPlugin(accounts, option, lastSuccessfulTime)) {
						Log.i(LOG_TAG, accounts[0].type + " should bind!");
						handleAddToPluginSessions(mPluginSessions,
								new PluginSession(accounts, lastSuccessfulTime));
					} else {
						Log.i(LOG_TAG, "plugin type :" + accounts[0].type
								+ " skipped!");
					}
				} else {
					Log.e(LOG_TAG, "error when getting grouped accounts!");
				}
			}
			if (mPluginSessions.isEmpty()) {
				handleResponseAndCallback();
			} else {
				handlePluginSessionsBinding();
			}
		}
		
		protected void handleAddToPluginSessions(
				List<PluginSession> pluginSessions, PluginSession session) {
			if (pluginSessions != null && session != null) {
				if (session.getProcessName() != "dummy") {
					pluginSessions.add(session);
				} else {
					Log.e(LOG_TAG,
							"skip binding the plugin without process name"
									+ session.mAccountType);
				}
			}
		}
		
		protected void handlePluginSessionsBinding() {
			for (PluginSession pluginSession : mPluginSessions) {
				mPendingAccountQueue.add(pluginSession);
				Log.i(LOG_TAG, "add " + pluginSession.mAccountType
						+ " to pending queue!");
			}
			consumePendingPluginSession();
		}
		
		final static int MAX_RUNNING_PLUGIN_PROCESS = 3;
		final static int MAX_RUNNING_PLUGIN_PER_PROCESS = 5;

		protected synchronized void consumePendingPluginSession() {
			Log.i(LOG_TAG, "consume pending plugin session");

			// scan all pending plug-in to see if any can be executed.
			for (PluginSession sessionToBind : mPendingAccountQueue) {
				if (addPluginToProcessMap(sessionToBind)) {
					mPendingAccountQueue.remove(sessionToBind);
					sessionToBind.bind();
				}
			}
		}

		protected boolean addPluginToProcessMap(PluginSession pluginSession) {
			Set<String> set = mWorkingProcessPluginMap
					.get(pluginSession.getProcessName());
			if (set != null) {
				if (set.size() < MAX_RUNNING_PLUGIN_PER_PROCESS) {
					Log.i(LOG_TAG, "add " + pluginSession.mAccountType
							+ " to process map!");
					set.add(pluginSession.mAccountType);
					return true;
				} else {
					Log.i(LOG_TAG, "skip to add " + pluginSession.mAccountType
							+ ", plugin per process full!");
					return false;
				}
			} else {
				if (mWorkingProcessPluginMap.size() < MAX_RUNNING_PLUGIN_PROCESS) {
					set = Collections
							.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
					Log.i(LOG_TAG, "add " + pluginSession.mAccountType
							+ " to process map!");
					set.add(pluginSession.mAccountType);
					mWorkingProcessPluginMap.put(pluginSession.getProcessName(),
							set);
					return true;
				} else {
					Log.i(LOG_TAG, "skip to add " + pluginSession.mAccountType
							+ ", process full!");
					return false;
				}
			}
		}

		protected void removePluginFromProcessMap(PluginSession pluginSession) {
			Set<String> set = mWorkingProcessPluginMap
					.get(pluginSession.getProcessName());
			if (set != null) {
				Log.i(LOG_TAG, "remove account type: "
						+ pluginSession.mAccountType + " from process map!");
				set.remove(pluginSession.mAccountType);
				if (set.isEmpty()) {
					mWorkingProcessPluginMap.remove(pluginSession.getProcessName());
					Log.i(LOG_TAG, "remove process: "
							+ pluginSession.getProcessName() + " from process map!");
				}
			}
		}
		
		private long getLastSuccessfulTime(Account[] accounts, Bundle option) {
			if (option.containsKey(SocialManager.KEY_CONTACTS)) {
				// for contact updates tab, sync we don't record last
				// successful time.
				Log.i(LOG_TAG, "sync by contact id, return 0");
				return 0;
			}
		    
			final SQLiteDatabase db = new SocialDatabase(getBaseContext())
					.getReadableDatabase();
			Bundle bundle = option.getBundle(accounts[0].type);
			String[] syncTypeIds = null;
			if (bundle != null) {
				syncTypeIds = bundle
						.getStringArray(SocialManager.KEY_SYNC_TYPE);
				
				if (syncTypeIds != null
						&& syncTypeIds.length > 1
						&& SocialManager.SYNC_TYPE_HIGHLIGHTS
								.equals(syncTypeIds[0])) {
					// work around here for customize highlights, if the first
					// sync type is highlights,
					// it mean sync id is highlights and followed with enabled topics
					// so we care only about highlights last sync time
					syncTypeIds = new String[] { SocialManager.SYNC_TYPE_HIGHLIGHTS };
				}
			}
			Cursor cursor = null;
			try {
				String sql = Utils.generateMaxEndTimeString(accounts, syncTypeIds);
				cursor = db.rawQuery(sql, null);
				if (cursor != null && cursor.moveToFirst()) {
					long timestamp = cursor.getLong(0);
					if (syncTypeIds != null
							&& syncTypeIds.length != cursor.getCount()) {
						timestamp = 0;
						Log.i(LOG_TAG, "timestamp reset to 0");
					}
					Log.i(LOG_TAG, accounts[0].type + " last sync time "
							+ timestamp);

					return timestamp;
				}
				return 0;
			} catch (Exception e) {
				Log.e(LOG_TAG, "some thing wrong when get last success time! "
						+ e);
				return 0;
			} finally {
				if (cursor != null) {
					cursor.close();
				}
				if (db != null) {
					db.close();
				}
			}
		}

		private boolean shouldBindPlugin(Account[] accounts, Bundle option,
				long lastSuccessfulTime) {
			
			boolean bIgnoreInterval = PreferenceManager
					.getDefaultSharedPreferences(getBaseContext()).getBoolean(
							getPluginPropPrefKey(accounts[0].type,
									"key_prop_bd_ignore_sync_interval"), false);
			if (bIgnoreInterval) {
				return true;
			}
			
			Bundle accountOption = mOptions.getBundle(accounts[0].type);
			if (accountOption == null) {
				accountOption = new Bundle();
				mOptions.putBundle(accounts[0].type, accountOption);
			}
			accountOption.putLong("key_sync_time_since", lastSuccessfulTime);
			Log.d(LOG_TAG, "ShouldBindPlugin? " + accounts[0].type
					+ " last sync time: " + lastSuccessfulTime);
			Log.d(LOG_TAG,
					"manual refresh? "
							+ option.getBoolean("key_manual_refresh"));
			if (option.getBoolean("key_manual_refresh", true)) {
				Log.d(LOG_TAG, "foreground refresh interval got from DM:"
						+ getFgSyncIntervalFromDM(accounts[0].type)
						+ ", with account type " + accounts[0].type);
				if (lastSuccessfulTime > System.currentTimeMillis()) {
					Log.e(LOG_TAG,
							"there is something wrong, lastSuccessful time is bigger than current time, probably you modified system time to the future?");
					Log.e(LOG_TAG, "timeline seems wrong, reset it "
							+ accounts[0].type);
					try {
						getBaseContext().getContentResolver().delete(
								SyncCursors.CONTENT_URI,
								SyncCursors.COLUMN_ACCOUNT_TYPE_STR + "=?",
								new String[] { accounts[0].type });
					} catch (Exception e) {
						Log.e(LOG_TAG, "error while reset timeline " + e);
					}
					return true;
				} else {
					if (System.currentTimeMillis() - lastSuccessfulTime > getFgSyncIntervalFromDM(accounts[0].type)) {
						return true;
					}
				}
				return false;
			} else {
				return SyncUtils.shouldSync(getApplicationContext(),
						lastSuccessfulTime);
			}
		}
		
		protected static final String NEWS_ACCOUNT_TYPE = "com.htc.opensense.htcnews";
		
		private long getFgSyncIntervalFromDM(String accountType) {
			if (NEWS_ACCOUNT_TYPE.equals(accountType)) {
				return DMKeys.DEFAULT_FG_REFRESH_INTERVAL_NEWS;
			} else {
				return DMKeys.DEFAULT_FG_REFRESH_INTERVAL_SN;
			}
		}

		private synchronized void onPluginCompleted(PluginSession pluginSession) {
			Log.v(LOG_TAG,"on plugin completed! plugin session type " + pluginSession.mAccountType);
			removePluginFromProcessMap(pluginSession);
			
			mTimeoutHandler.removeMessages(PluginSession.MSG_OPERATION_TIMEOUT,
					pluginSession);

			// TODO: Result value format undefined at this moment
			onPluginProgress(pluginSession.mAccounts, pluginSession.mValue);
			
			pluginSession.unbind();
			
			if (mPendingAccountQueue.isEmpty()
					&& mWorkingProcessPluginMap.isEmpty()) {
				handleResponseAndCallback();
			} else if (mPendingAccountQueue.size() > 0) {
				consumePendingPluginSession();
			}
		}
		
		void handleResponseAndCallback() {
			ISocialManagerResponse response = getResponseAndClose();
			if (response != null) {
				try {
					if (mValues == null) {
						response.onError(
								SocialManager.ERROR_CODE_INVALID_RESPONSE,
								"null bundle returned");
					} else {
						onSessionCompleted();
						response.onResult((Bundle) mValues.clone());
					}
				} catch (RemoteException e) {
					Log.v(LOG_TAG, "failure while notifying response", e);
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		}

		void onPluginProgress(Account[] accounts, Bundle value) {
			mValues.putBundle(accounts[0].type, value);
		}

		void onSessionCompleted() {
			Log.i(LOG_TAG,"onSessionCompleted");
			boolean sessionResult = false;
			for (PluginSession pluginSession : mPluginSessions) {
			    if (pluginSession.mValue != null) {
			    	pluginSession.mValue.setClassLoader(SyncType.class.getClassLoader());
    				final boolean pluginResult = pluginSession.mValue
    						.getBoolean(SocialManager.KEY_BOOLEAN_RESULT);
    				if (pluginResult) {
    					sessionResult = pluginResult;
    				}
			    }
			}
			mValues.putBoolean(SocialManager.KEY_BOOLEAN_RESULT, sessionResult);
		}

		public class PluginSession extends ISocialPluginResponse.Stub implements
				ServiceConnection, Comparable<PluginSession>{
			private final String mAccountType;
			private final Account[] mAccounts;

			private ISocialPlugin mPlugin;
			private Bundle mValue;
//			private int mErrorCode;
//			private String mErrorMessage;
			private long mLastSuccessfulTime;
			public static final int MSG_OPERATION_TIMEOUT = 128;
			public static final int MAX_OPERATION_TIME = 30000;// millisecond.
			private ComponentName mComponentName = null;
			private String mProcessName = null;

			public PluginSession(Account[] accounts, long time) {
				if (accounts == null || accounts.length == 0) {
					throw new IllegalArgumentException("accounts is null");
				}
				mAccounts = accounts;
				mAccountType = mAccounts[0].type;
				mLastSuccessfulTime = time;
				mComponentName = LocalPluginHelper
						.getBlinkFeedPluginComponentName(getBaseContext(),
								mAccountType);
				if (mComponentName != null) {
					try {
						ServiceInfo info = getBaseContext().getPackageManager()
								.getServiceInfo(mComponentName, 0);
						if (info != null) {
							mProcessName = info.processName;
						}
					} catch (NameNotFoundException e) {
						Log.e(LOG_TAG,
								"cannot find this plugin service");
						e.printStackTrace();
					}
				} else {
					Log.e(LOG_TAG,
							"component name is null!, cannot find this plugin");
				}
			}

			void bind() {
				Log.v(LOG_TAG, "initiating bind to plugin type " + mAccountType);
				if (!bindToPlugin()) {
					Log.d(LOG_TAG, "bind attemp failed for " + mAccountType);
					setError(SocialManager.ERROR_CODE_REMOTE_EXCEPTION,
							"bind failure");
				}
			}

			private void unbind() {
				if (mPlugin != null) {
					mPlugin = null;
					try {
						getBaseContext().unbindService(this);
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}
			}

			private boolean bindToPlugin() {
				if (mComponentName == null) {
					return false;
				}

				Intent intent = new Intent();
				intent.setAction(SocialManager.ACTION_PLUGIN_INTENT);
				intent.setComponent(mComponentName);
				intent.putExtra("hsp", true);
				boolean result = false;
				try {
					result = getBaseContext().bindService(intent, this,
							Context.BIND_AUTO_CREATE);

					if (!result) {
						Log.e(LOG_TAG, "bind service failed! ");
					}
				} catch (Exception e) {
					Log.e(LOG_TAG, "exception when bind to plugin " + e);
					result = false;
				}

				return result;
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mPlugin = ISocialPlugin.Stub.asInterface(service);
				try {
					Bundle bundle = getPluginOptionBundle(mOptions);
					mTimeoutHandler.sendMessageDelayed(Message.obtain(mTimeoutHandler,
							MSG_OPERATION_TIMEOUT, this), MAX_OPERATION_TIME);
					runPlugin(this, mAccounts, bundle);
				} catch (RemoteException e) {
					setError(SocialManager.ERROR_CODE_REMOTE_EXCEPTION,
							"remote exception");
				} catch (Exception e) {
					setError(SocialManager.ERROR_CODE_REMOTE_EXCEPTION,
							"exception occurs");
					Log.e(LOG_TAG, "exception occurs when service connected "
							+ e);
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				mPlugin = null;
				setError(SocialManager.ERROR_CODE_REMOTE_EXCEPTION,
						"disconnected");
			}

			@Override
			public void onResult(Bundle value) throws RemoteException {
				setResult(value);
			}

			@Override
			public void onError(int errorCode, String errorMessage)
					throws RemoteException {
				setError(errorCode, errorMessage);
			}

			private void setResult(Bundle value) {
				mValue = value;
				onPluginCompleted(this);
			}

			public void setError(int errorCode, String errorMessage) {
//				mErrorCode = errorCode;
//				mErrorMessage = errorMessage;
				onPluginCompleted(this);
			}
			
			private Bundle getPluginOptionBundle(Bundle options) {
				Bundle pluginBundle = (Bundle) options.clone();
				Bundle accountOption = pluginBundle.getBundle(mAccountType);
				String[] syncTypes = null;
				long time = 0;
				if (accountOption != null) {
					syncTypes = accountOption
							.getStringArray(SocialManager.KEY_SYNC_TYPE);
					time = accountOption.getLong("key_sync_time_since");
				}
				if (syncTypes == null) {
					syncTypes = new String[] { SocialManager.SYNC_TYPE_HIGHLIGHTS };
				}
				pluginBundle.putStringArray(SocialManager.KEY_SYNC_TYPE,
						syncTypes);
				pluginBundle.putLong("key_sync_time_since", time);
				boolean isLoadMore = pluginBundle.getLong(SocialManager.KEY_OFFSET,
						0) != 0;
				if (isLoadMore) {
					// page token is only needed when load more.
					setPageTokenToBundle(pluginBundle, syncTypes[0]);
				}
				return pluginBundle;
			}

			public long getLastSuccessfulTime(){
				return mLastSuccessfulTime;
			}

			@Override
			public int compareTo(PluginSession another) {
				if (mLastSuccessfulTime > another.getLastSuccessfulTime()) {
					return 1;
				} else if (mLastSuccessfulTime < another
						.getLastSuccessfulTime()) {
					return -1;
				}
				return 0;
			}
			
			public String getProcessName() {
				if (TextUtils.isEmpty(mProcessName)) {
					Log.e(LOG_TAG,
							"error when get process name! process name is null!");
					return "dummy";
				}
				return mProcessName;
			}

			private void setPageTokenToBundle(Bundle options, String syncType) {
				final SQLiteDatabase db = new SocialDatabase(getBaseContext())
						.getReadableDatabase();
				Cursor cursor = null;
				try {
					String sql = Utils.genMaxEndTimeTokenString(mAccounts[0],
							syncType);
					cursor = db.rawQuery(sql, null);
					if (cursor != null && cursor.moveToFirst()) {
						String pageToken = cursor.getString(0);

						options.putString("page_token", pageToken);
					}
				} catch (Exception e) {
					Log.e(LOG_TAG, "some thing wrong when get page token! " + e);
				} finally {
					if (cursor != null) {
						cursor.close();
					}
					if (db != null) {
						db.close();
					}
				}
			}
		}
	}
}
