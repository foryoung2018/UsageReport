package com.htc.lib2.opensense.social;

import java.util.List;

import com.htc.lib2.opensense.internal.SystemWrapper;


import android.accounts.Account;
import android.content.ContentResolver;
import android.net.Uri;
import android.net.Uri.Builder;
import android.text.TextUtils;
import android.util.Pair;

/**
 * The contract between social database and application. Contains definitions
 * for the supported URIs and columns.
 * 
 * @hide
 */
public class SocialContract {

	/**
	 * Stream database content authority
	 * 
	 * @hide
	 */
	public static final String CONTENT_AUTHORITY = SystemWrapper.getSocialManagerAuthority() /* "com.htc.opensense.social" */;

	/**
	 * HSP stream database content authority
	 *
	 * @hide
	 */
	public static final String HSP_CONTENT_AUTHORITY = SystemWrapper.getSocialManagerPackageName()/* "com.htc.opensense.social" */;

	private static final Uri BASE_CONTENT_URI = Uri.parse("content://"
			+ CONTENT_AUTHORITY);

	private static final Uri HSP_CONTENT_URI = Uri.parse("content://"
			+ HSP_CONTENT_AUTHORITY);

	/**
	 * @hide
	 */
	interface StreamColumn {

		/**
		 * Constant of stream post id column, post id should be unique in your service type
		 * 
		 * @hide
		 */
		static final String COLUMN_POST_ID_STR = "stream_post_id";

		/**
		 * Constant of stream poster id column. The poster's user id
		 * 
		 * @hide
		 */
		static final String COLUMN_POSTER_ID_STR = "stream_poster";

		/**
		 * Constant of stream poster name column.
		 * 
		 * @hide
		 */
		static final String COLUMN_POSTER_NAME_STR = "stream_poster_name_str";

		/**
		 * Constant of stream avatar URL column.
		 * 
		 * @hide
		 */
		static final String COLUMN_AVATAR_URL_STR = "stream_avatar_url";

		/**
		 * Constant of stream timestamp column. Should be time in millisecond.
		 * 
		 * @hide
		 */
		static final String COLUMN_TIMESTAMP_LONG = "stream_timestamp";

		/**
		 * Constant of stream account type column.
		 * 
		 * @hide
		 */
		static final String COLUMN_ACCOUNT_TYPE_STR = "stream_account_type";

		/**
		 * Constant of stream account name column.
		 * 
		 * @hide
		 */
		static final String COLUMN_ACCOUNT_NAME_STR = "stream_account_name";

		/**
		 * Constant of stream click action column. The click action is an
		 * intent which need to be SimpleMenuItem and serialized by Gson
		 * utility.
		 * 
		 * @hide
		 */
		static final String COLUMN_CLICK_ACTION_STR = "stream_click_action_str";

		/**
		 * Constant of stream attachment click action column. The click action
		 * is an intent which need to be SimpleMenuItem and serialized by Gson
		 * utility.
		 * 
		 * @hide
		 */
		static final String COLUMN_ATTACHMENT_CLICK_ACTION_STR = "stream_attachment_click_action_str";

		/**
		 * Constant of stream context action column. The context action is an
		 * intent which need to be SimpleMenuItem and serialized by Gson
		 * utility.
		 * 
		 * @hide
		 */
		static final String COLUMN_CONTEXT_ACTION_STR = "stream_context_action_str";

		/**
		 * Constant of stream type column, e.g Stream.STREAM_TYPE_BIT_VIDEO.
		 * 
		 * @hide
		 */
		static final String COLUMN_STREAM_TYPE_INT = "stream_type";

		/**
		 * Constant of stream sync type column. Sync types are define by each plugin.
		 * 
		 * @hide
		 */
		static final String COLUMN_SYNC_TYPE_STR = "stream_sync_type_str";

		/**
		 * Constant of stream cover photo URI column. This URI should direct to
		 * a high quality photo.
		 * 
		 * @hide
		 */
		static final String COLUMN_COVER_URI_HQ_STR = "stream_cover_uri_hq_str";

		/**
		 * Constant of stream cover photo URI column. This URI should direct to
		 * a medium quality photo.
		 * 
		 * @hide
		 */
		static final String COLUMN_COVER_URI_MQ_STR = "stream_cover_uri_mq_str";

		/**
		 * Constant of stream cover photo URI column. This URI should direct to
		 * a low quality photo.
		 * 
		 * @hide
		 */
		static final String COLUMN_COVER_URI_LQ_STR = "stream_cover_uri_lq_str";

		/**
		 * Constant of stream title column.
		 * 
		 * @hide
		 */
		static final String COLUMN_TITLE_STR = "stream_title_str";

		/**
		 * Constant of stream title format column.
		 * 
		 * @hide
		 */
		static final String COLUMN_TITLE_FORMAT_STR = "stream_title_format_str";

		/**
		 * Constant of stream extra information column.
		 * 
		 * @hide
		 */
		static final String COLUMN_EXTRA_STR = "stream_extra_str";

		/**
		 * Constant of stream body column
		 * 
		 * @hide
		 */
		static final String COLUMN_BODY_STR = "stream_body_str";

		/**
		 * Constant of stream owner UID column
		 * 
		 * @hide
		 */
		static final String COLUMN_OWNER_UID_INT = "stream_owner_uid_int";

		/**
		 * Constant of stream provider icon column
		 * 
		 * @hide
		 */
		static final String COLUMN_PROVIDER_ICON_URI_STR = "stream_provider_icon_str";
		
		/**
		 * Constant of stream bundle id column
		 * 
		 * @hide
		 */
		static final String COLUMN_BUNDLE_ID_STR = "stream_bundle_id";

		/**
		 * Constant of stream bundle order column
		 * 
		 * @hide
		 */
		static final String COLUMN_BUNDLE_ORDER_INT = "stream_bundle_order";
	}

	/**
	 * @hide
	 */
	interface SyncCursorsColumn {

		/**
		 * Constant of cursor account name column
		 * 
		 * @hide
		 */
		static final String COLUMN_ACCOUNT_NAME_STR = "cursors_account_name";

		/**
		 * Constant of cursor account type column
		 * 
		 * @hide
		 */
		static final String COLUMN_ACCOUNT_TYPE_STR = "cursors_account_type";

		/**
		 * Constant of cursor start time column
		 * 
		 * @hide
		 */
		static final String COLUMN_START_TIME_LONG = "cursors_start_time";

		/**
		 * Constant of cursor end time column
		 * 
		 * @hide
		 */
		static final String COLUMN_END_TIME_LONG = "cursors_end_time";

		/**
		 * Constant of cursor sync type column
		 * 
		 * @hide
		 */
		static final String COLUMN_SYNC_TYPE = "cursors_sync_type";

		/**
		 * Constant of cursor page token column
		 * 
		 * @hide
		 */
		static final String COLUMN_PAGE_TOKEN = " cursors_page_token";
	}

	/**
	 * @hide
	 */
	interface SyncTypeColumn {

		/**
		 * Constant of sync type account type column, should be the type that
		 * the sync type belongs to.
		 * 
		 * @hide
		 */
		static final String COLUMN_ACCOUNT_NAME_STR = "account_name";

		/**
		 * Constant of sync type account name column, should be the name that
		 * the sync type belongs to.
		 * 
		 * @hide
		 */
		static final String COLUMN_ACCOUNT_TYPE_STR = "account_type";

		/**
		 * Constant of sync type package name column, your plugin's package
		 * name. It's must have if you specify title res id and sub title res
		 * id. If no, it could be empty.
		 * 
		 * @hide
		 */
		static final String COLUMN_PACKAGE_NAME_STR = "package_name";

		/**
		 * Constant of sync type title column, the label name on the preference.
		 * 
		 * @hide
		 */
		static final String COLUMN_TITLE_STR = "title";

		/**
		 * Constant of sync type title column, the label name on the preference
		 * with specific res name within plugin's package.
		 * 
		 * @hide
		 */
		static final String COLUMN_TITLE_RES_NAME_STR = "title_res";

		/**
		 * Constant of sync type sub title column, the label name on the
		 * preference summary. Could be empty.
		 * 
		 * @hide
		 */
		static final String COLUMN_SUB_TITLE_STR = "sub_title";

		/**
		 * Constant of sync type sub title column, the label name on the
		 * preference summary with specific res name within plugin's package.
		 * Could be empty.
		 * 
		 * @hide
		 */
		static final String COLUMN_SUB_TITLE_RES_NAME_STR = "sub_title_res";

		/**
		 * Constant of sync type id column, the sync type id that same with
		 * stream table ones. use this one for backward compatibility.
		 * 
		 * @hide
		 */
		static final String COLUMN_IDENTITY_STR = "_id";

		/**
		 * Constant of sync type category column, sync types with the same
		 * category name will be grouped together.
		 * 
		 * @hide
		 */
		static final String COLUMN_CATEGORY_STR = "category";

		/**
		 * Constant of sync type category res name column, sync types with the same
		 * category name will be grouped together.
		 * 
		 * @hide
		 */
		static final String COLUMN_CATEGORY_RES_NAME_STR = "category_res";

		/**
		 * Constant of sync type edition column, sync type category with
		 * the same edition name will be grouped together.
		 * 
		 * @hide
		 */
		static final String COLUMN_EDITION_STR = "edition";

		/**
		 * Constant of sync type edition res name column, sync type category with
		 * the same edition name will be grouped together.
		 * 
		 * @hide
		 */
		static final String COLUMN_EDITION_RES_NAME_STR = "edition_res";

		/**
		 * Constant of sync type color column.
		 * 
		 * @hide
		 */
		static final String COLUMN_COLOR_INT = "color";

		/**
		 * Constant of sync type icon res name column.
		 * 
		 * @hide
		 */
		static final String COLUMN_ICON_RES_NAME_STR = "icon_res";

		/**
		 * Constant of sync type icon url column.
		 * 
		 * @hide
		 */
		static final String COLUMN_ICON_URL_STR = "icon_url";
		
		/**
		 * Constant of sync type flag url column.
		 * 
		 * @hide
		 */
		static final String COLUMN_FLAG_URL_STR = "flag_url";
		
		/**
		 * Constant of sync type enabled column.
		 * 
		 * @hide
		 */
		static final String COLUMN_ENABLED_INT = "enabled";
	}

	/**
	 * @hide
	 */
	interface SyncTypeParameters {

		/**
		 * @hide
		 */
		static final String PARA_ACCOUNT_TYPE = "account_type";

		/**
		 * @hide
		 */
		static final String PARA_ACCOUNT_NAME = "account_name";
	}

	/**
	 * @hide
	 */
	interface StreamParameters {

		/**
		 * @hide
		 */
		static final String PARA_STREAM_POSTER = "poster";

		/**
		 * @hide
		 */
		static final String PARA_STREAM_POSTER_ID = "poster";

		/**
		 * @hide
		 */
		static final String PARA_STREAM_TYPE = "stream_type";

		/**
		 * @hide
		 */
		static final String PARA_STREAM_ACCOUNT_TYPE = "account_type";

		/**
		 * @hide
		 */
		static final String PARA_STREAM_ACCOUNT_NAME = "account_name";

		/**
		 * @hide
		 */
		static final String PARA_STREAM_LATEST = "latest";

		/**
		 * @hide
		 */
		static final String PARA_STREAM_BETWEEN_START = "between_start";

		/**
		 * @hide
		 */
		static final String PARA_STREAM_BETWEEN_END = "between_end";

		/**
		 * @hide
		 */
		static final String PARA_STREAM_SYNC_TYPE = "sync_type";
	}

	/**
	 * @hide
	 */
	interface StreamTypeBit {

		/**
		 * @hide
		 */
		static final int TYPE_BIT_TEXT = 1;

		/**
		 * @hide
		 */
		static final int TYPE_BIT_PHOTO = TYPE_BIT_TEXT << 1;

		/**
		 * @hide
		 */
		static final int TYPE_BIT_VIDEO = TYPE_BIT_TEXT << 2;

		/**
		 * @hide
		 */
		static final int TYPE_BIT_LINK = TYPE_BIT_TEXT << 3;

		/**
		 * @hide
		 */
		static final int TYPE_BIT_AUDIO = TYPE_BIT_TEXT << 4;

		/**
		 * @hide
		 */
		static final int TYPE_BIT_AVATAR_STYLE = TYPE_BIT_TEXT << 5;
	}
	
	/**
	 * @hide
	 */
	interface StreamBundleColumn {
		/**
		 * @hide
		 */
		static final String COLUMN_BID_STR = "bundle_id";
		/**
		 * @hide
		 */
		static final String COLUMN_TITLE_STR = "bundle_title";
		/**
		 * @hide
		 */
		static final String COLUMN_CLICK_ACTION_STR = "bundle_click_action";
		/**
		 * @hide
		 */
		static final String COLUMN_TIMESTAMP_INT = "bundle_timestamp";
	}

	private static final String PATH_STREAM = "stream";
	private static final String PATH_SYNC_CURSORS = "cursors";
	private static final String PATH_STREAMBUNDLE = "streambundle";

	public static class Stream implements StreamColumn, StreamParameters,
			StreamTypeBit {

		/**
		 * Stream content URI.
		 * 
		 * @hide
		 */
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_STREAM).build();

		/**
		 * @hide
		 */
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/vnd.opensense.social";

		/**
		 * @hide
		 */
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/vnd.opensense.social";

		/**
		 * Default sort when select streams, sort by time DESC.
		 * 
		 * @hide
		 */
		public static final String DEFAULT_SORT = StreamColumn.COLUMN_TIMESTAMP_LONG
				+ " DESC";
		
		public static final String DEFAULT_SORT_BY_BUNDLE = "common_timestamp DESC, "
				+ StreamColumn.COLUMN_BUNDLE_ID_STR
				+ " DESC, "
				+ StreamColumn.COLUMN_BUNDLE_ORDER_INT + " ASC";

		/**
		 * Default sync type
		 * 
		 * @hide
		 */
		public static final String DEFAULT_SYNC_TYPE = "default";

		/**
		 * Default stream type.
		 * 
		 * @hide
		 */
		public static final int DEFAULT_STREAM_TYPE = 0;

		private Stream() {
		}

		/**
		 * Get stream URI by poster id and account type
		 * @param PosterTypeList A poster_id-account_type pair list that you want to filter. 
		 * @param latestOnly Should the result be latest only?
		 * @return
		 * 
		 * @hide
		 */
		public static Uri buildUriWithPosterAndAccType(List<Pair<String,String>> PosterTypeList,
				boolean latestOnly) {
			Builder builder = CONTENT_URI.buildUpon();

			for(Pair<String,String> postertype : PosterTypeList){
				builder.appendQueryParameter(PARA_STREAM_POSTER_ID, postertype.first);
				builder.appendQueryParameter(PARA_STREAM_ACCOUNT_TYPE, postertype.second);
			}

			builder.appendQueryParameter(PARA_STREAM_LATEST, String.valueOf(latestOnly));
			return builder.build();
		}

		/**
		 * Get URI by accounts, and latest only default true
		 * @param accounts
		 * @return
		 * 
		 * @hide
		 */
		public static Uri buildUriWithAccounts(Account[] accounts) {
			return buildUriWithAccounts(accounts, true);
		}

		/**
		 * Get URI by accounts
		 * @param accounts
		 * @param latestOnly
		 * @return
		 * 
		 * @hide
		 */
		public static Uri buildUriWithAccounts(Account[] accounts,
				boolean latestOnly) {
			Builder builder = CONTENT_URI.buildUpon();

			for (Account account : accounts) {
				builder.appendQueryParameter(PARA_STREAM_ACCOUNT_TYPE,
						account.type);
				builder.appendQueryParameter(PARA_STREAM_ACCOUNT_NAME,
						account.name);
			}

			builder.appendQueryParameter(PARA_STREAM_LATEST,
					String.valueOf(latestOnly));

			return builder.build();
		}

		/**
		 * Get URI by Accounts, stream type and sync type.
		 * @param accounts 
		 * @param streamType stream type, can be null.
		 * @param syncType sync type, can be null
		 * @return
		 * 
		 * @hide
		 */
		public static Uri buildUriWithAccountsAndTypes(Account[] accounts,
				int streamType, String syncType) {
			return buildUriWithAccountsAndTypes(accounts, streamType, syncType, true);
		}

		/**
		 * Get URI by Accounts, stream type and sync type.
		 * @param accounts
		 * @param streamType stream type, can be null.
		 * @param syncType sync type, can be null
		 * @param latestOnly
		 * @return
		 * 
		 * @hide
		 */
		public static Uri buildUriWithAccountsAndTypes(Account[] accounts,
				int streamType, String syncType, boolean latestOnly) {
			Builder builder = CONTENT_URI.buildUpon();

			for (Account account : accounts) {
				builder.appendQueryParameter(PARA_STREAM_ACCOUNT_TYPE,
						account.type);
				builder.appendQueryParameter(PARA_STREAM_ACCOUNT_NAME,
						account.name);
			}

			if (streamType > 0) {
				builder.appendQueryParameter(PARA_STREAM_TYPE,
						String.valueOf(streamType));
			}

			if (!TextUtils.isEmpty(syncType)) {
				builder.appendQueryParameter(PARA_STREAM_SYNC_TYPE, syncType);
			}
			builder.appendQueryParameter(PARA_STREAM_LATEST,
					String.valueOf(latestOnly));

			return builder.build();
		}

		/**
		 * Get URI by Accounts, stream type and sync type.
		 * @param accounts
		 * @param streamType stream type, can be null.
		 * @param syncType sync type, can be null
		 * @param latestOnly
		 * @param limit
		 * @return
		 * 
		 * @hide
		 */
		public static Uri buildUriWithAccountsAndTypes(Account[] accounts,
				int streamType, String syncType, int limit, boolean latestOnly) {
			Builder builder = CONTENT_URI.buildUpon();

			for (Account account : accounts) {
				builder.appendQueryParameter(PARA_STREAM_ACCOUNT_TYPE,
						account.type);
				builder.appendQueryParameter(PARA_STREAM_ACCOUNT_NAME,
						account.name);
			}

			if (streamType > 0) {
				builder.appendQueryParameter(PARA_STREAM_TYPE,
						String.valueOf(streamType));
			}

			if (!TextUtils.isEmpty(syncType)) {
				builder.appendQueryParameter(PARA_STREAM_SYNC_TYPE, syncType);
			}

			if (limit > 0) {
				builder.appendQueryParameter(SocialManager.KEY_LIMIT,
						String.valueOf(limit));
			}

			builder.appendQueryParameter(PARA_STREAM_LATEST,
					String.valueOf(latestOnly));

			return builder.build();
		}
	}

	private static final String PATH_END_TIME_AFTER = "end_after";
	private static final String PATH_CURSORS_ACCOUNT_TYPE = "account_type";
	private static final String PATH_CURSORS_ACCOUNT_NAME = "account_name";
	private static final String PATH_CURSORS_SYNC_TYPE = "sync_type";

	/**
	 * @hide
	 */
	public static class SyncCursors implements SyncCursorsColumn {

		/**
		 * @hide
		 */
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_SYNC_CURSORS).build();

		/**
		 * @hide
		 */
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/vnd.opensense.synccursors";

		/**
		 * @hide
		 */
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/vnd.opensense.synccursors";

		private SyncCursors() {
		}

		/**
		 * @hide
		 */
		public static Uri getUriWithAccTypeAccNameEndAfterTime(
				String accountType, String accountName, long startTime, String syncType) {
			return CONTENT_URI.buildUpon()
					.appendPath(PATH_CURSORS_ACCOUNT_TYPE)
					.appendPath(accountType)
					.appendPath(PATH_CURSORS_ACCOUNT_NAME)
					.appendPath(accountName)
					.appendPath(PATH_CURSORS_SYNC_TYPE)
					.appendPath(syncType)
					.appendPath(PATH_END_TIME_AFTER)
					.appendPath(String.valueOf(startTime)).build();
		}

		/**
		 * @hide
		 */
		public static String getSyncCursorsAccountType(Uri uri) {
			return uri.getPathSegments().get(2);
		}

		/**
		 * @hide
		 */
		public static String getSyncCursorsAccountName(Uri uri) {
			return uri.getPathSegments().get(4);
		}

		/**
		 * @hide
		 */
		public static int getSyncCursorsStreamType(Uri uri) {
			return Integer.valueOf(uri.getPathSegments().get(6));
		}

		/**
		 * @hide
		 */
		public static String getSyncCursorsSyncType(Uri uri) {
			return uri.getPathSegments().get(6);
		}

		/**
		 * @hide
		 */
		public static String getSyncCursorsEndAfterTime(Uri uri) {
			return uri.getPathSegments().get(8);
		}
	}

	private static final String PATH_SYNC_TYPE = "synctype";

	/**
	 * @hide
	 */
	public static class SyncTypeContract implements SyncTypeColumn,
			SyncTypeParameters {

		/**
		 * @hide
		 */
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_SYNC_TYPE).build();

		public static final Uri CONTENT_URI_HSP = HSP_CONTENT_URI.buildUpon()
				.appendPath(PATH_SYNC_TYPE).build();

		/**
		 * @hide
		 */
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/vnd.opensense.synctype";

		/**
		 * @hide
		 */
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/vnd.opensense.synctype";

		/**
		 * @hide
		 */
		public static final String DEFAULT_SORT = SyncTypeColumn.COLUMN_ACCOUNT_TYPE_STR
				+ " DESC";

		private SyncTypeContract() {
		}

		/**
		 * @hide
		 */
		public static Uri buildUriWithAccount(String accName,
				String accType) {
			Builder builder = CONTENT_URI.buildUpon();
			builder.appendQueryParameter(SyncTypeContract.PARA_ACCOUNT_NAME,
					accName);
			builder.appendQueryParameter(SyncTypeContract.PARA_ACCOUNT_TYPE,
					accType);
			return builder.build();
		}

		/**
		 * @hide
		 */
		public static Uri buildUriWithAccounts(Account[] accounts) {
			Builder builder = CONTENT_URI.buildUpon();
			for (Account account : accounts) {
				builder.appendQueryParameter(PARA_ACCOUNT_TYPE, account.type);
				builder.appendQueryParameter(PARA_ACCOUNT_NAME, account.name);
			}
			return builder.build();
		}
	}

	/**
	 * @hide
	 */
	public static class StreamBundle implements StreamBundleColumn {
		/**
		 * @hide
		 */
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_STREAMBUNDLE).build();

		private StreamBundle() {
		}
	}

	private SocialContract() {
	}
}
