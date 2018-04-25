package com.htc.lib2.mock.opensense.social;

import android.app.AlarmManager;

public interface DMKeys {
	String KEY_DM_CLEAN_DATA_INTERVAL = "key_dm_clean_data_interval";
	String KEY_DM_FG_REFRESH_INTERVAL_SN = "key_dm_fg_refresh_interval_sn";
	String KEY_DM_FG_REFRESH_INTERVAL_NEWS = "key_dm_fg_refresh_interval_news";
	String KEY_DM_AUTO_REFRESH_WIFI_CHARGING = "key_dm_auto_refresh_wifi_charging";
	String KEY_DM_AUTO_REFRESH_WIFI = "key_dm_auto_refresh_wifi";
	String KEY_DM_AUTO_REFRESH_WIFI_MOBILE = "key_dm_auto_refresh_mobile";
	String KEY_DM_ENABLED_FEATURE_TAB = "key_dm_enabled_feature_tab";

	long DEFAULT_FG_REFRESH_INTERVAL_SN = 60000;
	long DEFAULT_FG_REFRESH_INTERVAL_NEWS = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
	long DEFAULT_INTERVAL_PERIOD_CLEAN_DATA = SocialScheduler.INTERVAL_DEFAULT_PERIOD_CLEAN_DATA;
	long DEFAULT_INTERVAL_WITH_WIFI_CHARING = SocialScheduler.INTERVAL_INITIAL_VALUE_WITH_WIFI_CHARING;
	long DEFAULT_INTERVAL_WITH_WIFI = SocialScheduler.INTERVAL_INITIAL_VALUE_WITH_WIFI;
	long DEFAULT_INTERVAL_WITH_MOBILE = SocialScheduler.INTERVAL_DEFAULT_PERIODIC_SYNC_STREAM;

	boolean DEFAULT_ENABLED_FEATURE_TAB = false;
}
