package com.htc.lib2.opensense.cache;

import com.htc.lib2.opensense.internal.SystemWrapper;

import android.net.Uri;

/**
 * A container class which holds information about downloads.
 * 
 * @hide
 */
public interface Download {

    /**
     * The download ID.
     * 
     * @hide
     */
    String _ID = "_id"; /* download id = file name */

    /**
     * The download status.
     * 
     * @hide
     */
    String STATUS = "status";

    /**
     * The download URL.
     * 
     * @hide
     */
    String IMG_URL = "url";

    /**
     * The face detect value.
     * 
     * @hide
     */
    String FACE_DETECT = "face_detect";

    /**
     * The flag to only check if cached data exists
     * 
     * @hide
     */
    String CHECK_ONLY = "check_only";

    /**
     * The flag for HTTP header "Authorization"
     * 
     * @hide
     */
    String HTTP_HEADER_AUTHORIZATION = "http_header_authorization";

    /**
     * The flag for HTTP custom headers
     * 
     * @hide
     */
    String HTTP_HEADERS = "http_headers";

    /**
     * The download URL which is hashed.
     * 
     * @hide
     */
    String IMG_URL_HASH = "url_hash";

    /**
     * The download time that had been modified.
     * 
     * @hide
     */
    String LAST_MODIFIED_TIME = " last_modified_time";

    /**
     * The download file size.
     * 
     * @hide
     */
    String FILE_SIZE = "file_size";

    /**
     * The download folder where the images are stored.
     * 
     * @hide
     */
    String STORE_FOLDER = "store_folder";

    /**
     * The download content Uri.
     * 
     * @hide
     */
    String CONTENT_URI = "content_uri";

    /**
     * The cache of download image.
     * 
     * @hide
     * @deprecated
     */
    @Deprecated
    String IMG_CACHE = "img_cache";

    /**
     * The key of image encryption.
     * 
     * @hide
     */
    String ENCRYPTION_KEY = "encryption_key";

    /**
     * The account type.
     * 
     * @hide
     */
    String ACCOUNT_TYPE = "account_type";

    /**
     * The id of the user.
     * 
     * @hide
     */
    String USER_ID = "user_id";

    /**
     * The id that matches to the download table
     * 
     * @hide
     */
    String DOWNLOAD_ID = "download_id";

    /**
     * The authority.
     * 
     * @hide
     */
    String AUTHORITY = SystemWrapper.getCacheManagerAuthority() /* "com.htc.cachemanager" */;

    /**
     * The download table.
     * 
     * @hide
     */
    String DOWNLOAD_TB = "item";

    /**
     * The profile table.
     * 
     * @hide
     */
    String PROFILE_TB = "profile";

    /**
     * The rawquery.
     * 
     * @hide
     */
    String RAWQUERY = "rawquery";

    /**
     * The download content Uri.
     * 
     * @hide
     */
    Uri DOWNLOAD_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DOWNLOAD_TB);

    /**
     * The profile content Uri.
     * 
     * @hide
     */
    Uri PROFILE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PROFILE_TB);

    /**
     * The rawquery Uri.
     * 
     * @hide
     */
    Uri RAWQUERY_URI = Uri.parse("content://" + AUTHORITY + "/" + RAWQUERY);

    /**
     * The prefix of caching image.
     * 
     * @hide
     * @deprecated
     */
    @Deprecated
    String IMG_CACHE_PREFIX = "content://" + AUTHORITY + "/" + IMG_CACHE;

    /**
     * The caching image Uri.
     * 
     * @hide
     * @deprecated
     */
    @Deprecated
    Uri CACHE_IMG_URI = Uri.parse(IMG_CACHE_PREFIX);

    /**
     * The encryption key Uri.
     * 
     * @hide
     */
    Uri ENCRYPTION_KEY_URI = Uri.parse("content://" + AUTHORITY + "/" + "encryption_key");
}
