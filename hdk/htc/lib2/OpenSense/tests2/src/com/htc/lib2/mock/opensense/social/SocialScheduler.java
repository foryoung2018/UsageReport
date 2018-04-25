package com.htc.lib2.mock.opensense.social;

import java.util.concurrent.TimeUnit;

import android.accounts.Account;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.htc.lib2.opensense.internal.SystemWrapper.SystemProperties;
import com.htc.lib2.opensense.social.SocialContract.Stream;
import com.htc.lib2.opensense.social.SocialContract.SyncCursors;
import com.htc.lib2.opensense.social.SocialManager;
import com.htc.lib2.opensense.social.SocialManager.SocialManagerConnection;
import com.htc.lib2.opensense.social.SocialManagerCallback;
import com.htc.lib2.opensense.social.SocialManagerFuture;

public class SocialScheduler extends IntentService implements
		SocialManagerConnection {

	private final static String LOG_TAG = SocialScheduler.class.getSimpleName();

	private static final int CONNECTING_BUFFER = 5000; // 5 sec

	private static final String ACTION_SYNC_FEEDS = "com.htc.opensense.social.SYNC_FEEDS";
	private static final String ACTION_SYNC_CONTACTS = "com.htc.opensense.social.SYNC_CONTACTS";
	private static final String ACTION_STOP_SYNC_ALARM = "com.htc.opensense.social.STOP_SYNC_ALARM";
	public static final String ACTION_HANDLE_CLEAN_DATA = "com.htc.opensense.social.HANDLE_CLEAN_DATA";

	public static final String ACTION_RENEW_STOP_SYNC_ALARM = "com.htc.opensense.social.RENEW_STOP_SYNC_ALARM";
	public static final String ACTION_RESCHEDULE_CONTACT_SYNC = "com.htc.opensense.social.RESCHEDULE_CONTACT_SYNC";

	private static final String KEY_LAST_CLEAN_TIME = "key_last_clean_time_long";
	private static final String KEY_AUTO_REFRESH = "pref_key_auto_refresh";

	public static final long INTERVAL_DEFAULT_PERIODIC_SYNC_STREAM = AlarmManager.INTERVAL_HOUR * 2;
	private static final long INTERVAL_STOP_ALARM = AlarmManager.INTERVAL_DAY * 2;
	private static final long INTERVAL_PERIODIC_SYNC_CONTACT = AlarmManager.INTERVAL_DAY;
	public static final long INTERVAL_DEFAULT_PERIOD_CLEAN_DATA = AlarmManager.INTERVAL_DAY * 6;
	public static final long INTERVAL_INITIAL_VALUE_WITH_WIFI_CHARING = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
	public static final long INTERVAL_INITIAL_VALUE_WITH_WIFI = AlarmManager.INTERVAL_HOUR;

	private SocialManager mSocialManager = null;
	private AlarmManager mAlarmManager = null;

	private boolean isSyncingStream = false;
	private boolean isSyncingContact = false;

	private SharedPreferences mPrefs;
	private Editor mPrefsEditor;

	public SocialScheduler() {
		super("");
	}

	public SocialScheduler(String name) {
		super(name);
	}

	private PendingIntent getSyncFeedsIntent() {
		return getPendingIntent(ACTION_SYNC_FEEDS);
	}

	private PendingIntent getSyncFeedsIntent(long interval) {
		Intent syncIntent = new Intent(ACTION_SYNC_FEEDS);
		syncIntent.putExtra(KEY_SMART_SYNC_INTERVAL, interval);
		return PendingIntent.getService(getBaseContext(), 0, syncIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private PendingIntent getSyncContactsIntent() {
		return getPendingIntent(ACTION_SYNC_CONTACTS);
	}

	private PendingIntent getStopSyncAlarmIntent() {
		return getPendingIntent(ACTION_STOP_SYNC_ALARM);
	}

	private PendingIntent getPendingIntent(String action) {
		return PendingIntent.getService(getBaseContext(), 0,
				new Intent(action), 0);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mAlarmManager = (AlarmManager) getBaseContext().getSystemService(
				Context.ALARM_SERVICE);
		mPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		mPrefsEditor = mPrefs.edit();
	}

	@Override
	public void onDestroy() {
		Log.i(LOG_TAG, "onDestroy");
		mAlarmManager = null;

		super.onDestroy();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		Log.i(LOG_TAG, "action: " + action);
		if (action.equals(ACTION_SYNC_FEEDS)) {
			handleSyncStreamsAndNextAlarm(intent);
		} else if (action.equals(ACTION_SYNC_CONTACTS)) {
			syncContacts();
		} else if (action.equals(ACTION_HANDLE_CLEAN_DATA)) {
			handleCleanData();
		} else if (action.equals(ACTION_RENEW_STOP_SYNC_ALARM)) {
			if (shouldRenewStopSyncAlarm()) {
				doRenewStopSyncAlarm();
			} else {
				// do clean data broadcast only
				Log.i(LOG_TAG,"skipped renew sync stream alarm");
				handleCleanData();
			}
		} else if (action.equals(ACTION_RESCHEDULE_CONTACT_SYNC)) {
			doScheduleContactSync();
		} else if (action.equals(ACTION_STOP_SYNC_ALARM)) {
			doCancelSyncStreamAlarm();
		}
	}
	
	private boolean shouldRenewStopSyncAlarm() {
		if (syncOnlyWhenWifi()) {
			if (SyncUtils.isWifiOn(getBaseContext())) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	private boolean syncOnlyWhenWifi() {
//		String value = getBaseContext().getString(
//				R.string.pref_auto_refresh_value_wifi_only);
//		if (!TextUtils.isEmpty(value)) {
//			boolean result = value.equals(mPrefs.getString(KEY_AUTO_REFRESH,
//					value));
//			Log.i(LOG_TAG, "sync only when wifi ? " + result);
//			return result;
//		} else {
//			Log.i(LOG_TAG,
//					"sync only when wifi ? cannot find value, default return true");
			return true;
//		}
	}
	
	private long getInitialSyncInterval() {
		if (SyncUtils.shouldSmartSync(getBaseContext())) {
			if (SyncUtils.isCharging(getBaseContext())) {
				return INTERVAL_INITIAL_VALUE_WITH_WIFI_CHARING;
			} else {
				return INTERVAL_INITIAL_VALUE_WITH_WIFI;
			}
		} else {
			return INTERVAL_DEFAULT_PERIODIC_SYNC_STREAM;
		}
	}

	private void handleSyncStreamsAndNextAlarm(Intent intent) {
		if (SyncUtils.shouldSmartSync(getBaseContext())) {
			long prev_interval = intent.getLongExtra(KEY_SMART_SYNC_INTERVAL,
					0l);
			if (prev_interval != 0) {
				Log.i(LOG_TAG, "handleNextAlarm, prev smart sync interval: "
						+ prev_interval);
				prev_interval = prev_interval * 2;
				if (prev_interval > INTERVAL_DEFAULT_PERIODIC_SYNC_STREAM) {
					prev_interval = INTERVAL_DEFAULT_PERIODIC_SYNC_STREAM;
				}
				doScheduleSyncStreamAlarm(prev_interval);
			} else {
				Log.i(LOG_TAG,
						"handleNextAlarm, doesn't contain interval, just give it the initial value");
				doScheduleSyncStreamAlarm(getInitialSyncInterval());
			}
		}
		syncStreams();
	}

	private static String KEY_SMART_SYNC_INTERVAL = "key_smart_sync_interval";

	private void doScheduleSyncStreamAlarm(long interval) {
		Log.i(LOG_TAG, "doScheduleSyncStreamAlarm with interval " + interval);
		mAlarmManager.set(AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime() + interval,
				getSyncFeedsIntent(interval));
	}

	private void doRenewStopSyncAlarm() {
		mAlarmManager.set(AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime() + INTERVAL_STOP_ALARM,
				getStopSyncAlarmIntent());
		doScheduleSyncStreamAlarm(getInitialSyncInterval());
		handleCleanData();
	}

	private void doScheduleContactSync() {
		mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime() + INTERVAL_PERIODIC_SYNC_CONTACT,
				INTERVAL_PERIODIC_SYNC_CONTACT, getSyncContactsIntent());
	}

	private void doCancelSyncStreamAlarm() {
		mAlarmManager.cancel(getSyncFeedsIntent());
	}

	private void cancelAllAlarm() {
		Log.i(LOG_TAG, "cancel all Alarm");
		mAlarmManager.cancel(getSyncFeedsIntent());
		mAlarmManager.cancel(getSyncContactsIntent());
	}

	private void handleCleanData() {
		long lastCleanTime = mPrefs.getLong(KEY_LAST_CLEAN_TIME, 0);
		
		if (lastCleanTime == 0) {
			// for the very first time we only record current time but not
			// really do clean data.
			mPrefsEditor.putLong(KEY_LAST_CLEAN_TIME,
					System.currentTimeMillis());
			mPrefsEditor.apply();
			return;
		}
		
		long interval = getCleanDataIntervalFromDm();
		Log.d(LOG_TAG, "interval clean data got from DM " + interval);
		Log.d(LOG_TAG, "last clean time: " + lastCleanTime);
		if (System.currentTimeMillis() - lastCleanTime > AlarmManager.INTERVAL_DAY) {
			Log.i(LOG_TAG, "do clean data!");
			mPrefsEditor.putLong(KEY_LAST_CLEAN_TIME,
					System.currentTimeMillis());
			mPrefsEditor.apply();
			doCleanData(interval);
		} else {
			Log.d(LOG_TAG, "should not clean data, skip this time");
		}
	}

	private long getCleanDataIntervalFromDm() {
		return DMKeys.DEFAULT_INTERVAL_PERIOD_CLEAN_DATA;
	}
	
	private void doCleanData(long interval) {
		deleteOutDatedStreamFromDb(getBaseContext(),
				String.valueOf(System.currentTimeMillis() - interval));
	}
	
	private void deleteOutDatedStreamFromDb(Context context, String timeToDelete) {
		Log.i(LOG_TAG, "clear out dated feeds");
		try {
			context.getContentResolver().delete(Stream.CONTENT_URI,
					Stream.COLUMN_TIMESTAMP_LONG + " <= ?",
					new String[] { timeToDelete });

			context.getContentResolver().delete(SyncCursors.CONTENT_URI,
					SyncCursors.COLUMN_END_TIME_LONG + " <= ?",
					new String[] { timeToDelete });

			ContentValues values = new ContentValues();
			values.put(SyncCursors.COLUMN_START_TIME_LONG,
					Long.valueOf(timeToDelete));
			context.getContentResolver().update(
					SyncCursors.CONTENT_URI,
					values,
					SyncCursors.COLUMN_START_TIME_LONG + "<?" + " AND "
							+ SyncCursors.COLUMN_END_TIME_LONG + ">?",
					new String[] { timeToDelete, timeToDelete });
			context.getContentResolver().notifyChange(Stream.CONTENT_URI, null);
			context.sendBroadcast(new Intent(
					"com.htc.feed.action.FORCE_REFRESH"));
		} catch (Exception e) {
			Log.e(LOG_TAG, "error when clean db");
		}
	}
	
	private void syncStreams() {
		if (shouldDoSyncStream()) {
			Log.i(LOG_TAG, "begin to sync Streams");
			connectToSocialManager();

			if (mSocialManager != null) {

				Account[] accounts = getAccountsInfoBySocialManager();

				Bundle syncOption = new Bundle();
				syncOption.putLong(SocialManager.KEY_OFFSET, 0);
				syncOption.putBoolean(SocialManager.KEY_TRIGGER_SYNC_MANAGER,
						true);
				if (accounts != null && accounts.length != 0
						&& mSocialManager != null) {
					Log.i(LOG_TAG, "SocialManager syncActivityStreams");
					isSyncingStream = true;
					mSocialManager.syncActivityStreams(accounts, syncOption,
							new SocialManagerCallback<Bundle>() {
								@Override
								public void run(
										SocialManagerFuture<Bundle> future) {
									try {
										isSyncingStream = false;
										if (mSocialManager != null
												&& !isSyncingContact) {
											mSocialManager.disconnect();
											mSocialManager = null;
											Log.i(LOG_TAG,
													"social manager disconnect");
										} else {
											Log.i(LOG_TAG,
													"social manager is null or sync contact is running");
										}
									} catch (Exception e) {
										e.printStackTrace();
										Log.e(LOG_TAG,
												"something wrong when disconnect socialmanager");
									}
								}
							}, null);
				} else {
					Log.i(LOG_TAG, "account is null or empty! cancel all alarm");
					cancelAllAlarm();
				}
			}
		} else {
			Log.i(LOG_TAG, "syncStreams skipped");
		}
	}

	private void syncContacts() {
		if (shouldDoSyncContact()) {
			Log.i(LOG_TAG, "begin to sync Contacts");
			connectToSocialManager();

			if (mSocialManager != null) {
				Account[] accounts = getAccountsInfoBySocialManager();

				Bundle bundle = new Bundle();
				bundle.putBoolean(SocialManager.KEY_TRIGGER_SYNC_MANAGER, true);
				if (accounts != null && accounts.length != 0
						&& mSocialManager != null) {
					Log.i(LOG_TAG, "SocialManager syncContacts");
					isSyncingContact = true;
					mSocialManager.syncContacts(accounts, bundle,
							new SocialManagerCallback<Bundle>() {
								@Override
								public void run(
										SocialManagerFuture<Bundle> future) {
									try {
										isSyncingContact = false;
										if (mSocialManager != null
												&& !isSyncingStream) {
											mSocialManager.disconnect();
											mSocialManager = null;
											Log.i(LOG_TAG,
													"social manager disconnect");
										} else {
											Log.i(LOG_TAG,
													"social manager is null or sync stream is running");
										}
									} catch (Exception e) {
										e.printStackTrace();
										Log.e(LOG_TAG,
												"something wrong when disconnect socialmanager");
									}
								}
							}, null);
				} else {
					cancelAllAlarm();
				}
			}
		} else {
			Log.i(LOG_TAG, "syncContacts skipped");
		}
	}

	private boolean shouldDoSyncStream() {
		return ContentResolver.getMasterSyncAutomatically()
				&& shouldRenewStopSyncAlarm();
	}

	private boolean shouldDoSyncContact() {
		return ContentResolver.getMasterSyncAutomatically();
	}

	private void connectToSocialManager() {
		if (mSocialManager == null) {
			SocialManager.connect(getApplicationContext(), this);

			long timeLimit = System.currentTimeMillis() + CONNECTING_BUFFER;
			while (mSocialManager == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (System.currentTimeMillis() > timeLimit) {
					// time out
					break;
				}
			}
		}
	}

	private Account[] getAccountsInfoBySocialManager() {
		if (mSocialManager != null) {
			try {
				Bundle bundle = mSocialManager.getDataSources(null, null, null,
						null).getResult(15000, TimeUnit.MILLISECONDS);
				if (bundle != null) {
					return SocialManager.parseAccount(bundle);
				}
			} catch (Exception e) {
				Log.e(LOG_TAG, "exception when get accounts " + e.toString());
				return null;
			}
		}
		return null;
	}

	@Override
	public void onConnected(SocialManager manager) {
		mSocialManager = manager;
	}

	@Override
	public void onDisconnected() {
		mSocialManager = null;
	}

	@Override
	public void onBinderDied() {
		mSocialManager = null;
	}
	
	public static class SyncUtils {

		public static boolean isWifiOn(Context context) {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			boolean connectedWifi = false;
			if (cm != null && cm.getActiveNetworkInfo() != null) {
				connectedWifi = cm.getActiveNetworkInfo().isConnected()
						&& cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
			}
			return connectedWifi;
		}
		
		private static boolean isCharging(Context context) {
			IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			Intent batteryStatus = context.registerReceiver(null, ifilter);
			int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
					|| status == BatteryManager.BATTERY_STATUS_FULL;
			Log.i(LOG_TAG, "Charging ? " + isCharging);
			return isCharging;
		}
		
		private static boolean shouldSmartSync(Context context){
			return isWifiOn(context);
		}
		
		public static boolean shouldSync(Context context, long lastSyncTime) {
			if (context == null) {
				Log.e(LOG_TAG, "context is null! return false as default");
				return false;
			}
			if (ContentResolver.getMasterSyncAutomatically()) {
				if (shouldSmartSync(context)) {
					if (isCharging(context)) {
						Long interval = DMKeys.DEFAULT_INTERVAL_WITH_WIFI_CHARING;
						Log.i(LOG_TAG, "interval got " + interval);
						return System.currentTimeMillis() - lastSyncTime > interval;
					} else {
						Long interval = DMKeys.DEFAULT_INTERVAL_WITH_WIFI;
						Log.i(LOG_TAG, "interval got " + interval);
						return System.currentTimeMillis() - lastSyncTime > interval;
					}
				} else {
					if (syncOnlyWhenWifi(context)) {
						return false;
					} else {
						Long interval = DMKeys.DEFAULT_INTERVAL_WITH_MOBILE;
						Log.i(LOG_TAG, "interval got " + interval);
						return System.currentTimeMillis() - lastSyncTime > interval;
					}
				}
			} else {
				return false;
			}
		}
		
		public static boolean syncOnlyWhenWifi(Context context) {
//			SharedPreferences prefs = PreferenceManager
//					.getDefaultSharedPreferences(context);
//			String value = context
//					.getString(R.string.pref_auto_refresh_value_wifi_only);
//			if (!TextUtils.isEmpty(value)) {
//				boolean result = value.equals(prefs.getString(KEY_AUTO_REFRESH,
//						value));
//				Log.i(LOG_TAG, "sync only when wifi ? " + result);
//				return result;
//			} else {
//				Log.i(LOG_TAG,
//						"sync only when wifi ? cannot find value, default return true");
				return true;
//			}
		}
	}
}
