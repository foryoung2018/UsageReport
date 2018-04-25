package com.htc.lib1.mediamanager;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.htc.lib1.mediamanager.MediaManagerStore.MediaManagerColumns;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class MediaManager
{
    public static final String ACCESS_MM_PERMISSION = "com.htc.sense.permission.MEDIAMANAGER.ACCESS_MM";

    private static final String LOG_TAG = "MediaManager";
    private static final String SRV_MM = "com.htc.mediamanager.MMService";

    /* Constants start */
    //Internal
    private static final int MEDIA_TYPE_IMAGES     = (1 << 0);
    private static final int MEDIA_TYPE_DRM_FL_IMAGES = (1 << 1);
    private static final int MEDIA_TYPE_DRM_CD_IMAGES = (1 << 2);
    private static final int MEDIA_TYPE_DRM_SD_IMAGES = (1 << 3);
    private static final int MEDIA_TYPE_VIDEOS        = (1 << 4);
    private static final int MEDIA_TYPE_DRM_FL_VIDEOS = (1 << 5);
    private static final int MEDIA_TYPE_DRM_CD_VIDEOS = (1 << 6);
    private static final int MEDIA_TYPE_DRM_SD_VIDEOS = (1 << 7);
    private static final int MMP_DB_VERSION_DEFAULT = 1;
    private static final int MMP_DB_VERSION_3 = 3;

    private static final String[] BIND_PACKAGE_LIST = new String[]
            {
                "com.htc.album",
                "com.htc.albumgp",
                "com.htc.camera2",
                "com.htc.mediamanager"
            };
    /** 
     * MediaManager version number
     */
    public static final float CLOUD_GALLERY_BASE_VERSION = 1.6f;
    /** 
     * A media type for get Collection, target to get cloud images.
     */
    public static final int MEDIA_TYPE_CLOUD_IMAGES = (1 << 8);
    /** 
     * A media type for get Collection, target to get cloud videos.
     */
    public static final int MEDIA_TYPE_CLOUD_VIDEOS = (1 << 9);
    
    //Public
    /** 
     * A media type for get Collection, target to get regular images.
     */
    public static final int MEDIA_TYPE_REGULAR_IMAGES = MEDIA_TYPE_IMAGES;
    /** 
     * A media type for get Collection, target to get regular videos.
     */
    public static final int MEDIA_TYPE_REGULAR_VIDEOS = MEDIA_TYPE_VIDEOS;
    /** 
     * A media type for get Collection, target to get regular images and videos.
     */
    public static final int MEDIA_TYPE_REGULAR_MEDIA = MEDIA_TYPE_IMAGES | MEDIA_TYPE_VIDEOS;

    /** 
     * A media type for get Collection, target to get drm images.
     */
    public static final int MEDIA_TYPE_DRM_IMAGES = (MEDIA_TYPE_DRM_FL_IMAGES | MEDIA_TYPE_DRM_CD_IMAGES | MEDIA_TYPE_DRM_SD_IMAGES);
    /** 
     * A media type for get Collection, target to get drm videos.
     */
    public static final int MEDIA_TYPE_DRM_VIDEOS = (MEDIA_TYPE_DRM_FL_VIDEOS | MEDIA_TYPE_DRM_CD_VIDEOS | MEDIA_TYPE_DRM_SD_VIDEOS);
    /** 
     * A media type for get Collection, target to get drm images and videos.
     */
    public static final int MEDIA_TYPE_DRM_MEDIA = (MEDIA_TYPE_DRM_IMAGES | MEDIA_TYPE_DRM_VIDEOS);
    
    /** 
     * A media type for get Collection, target to get all images.
     */
    public static final int MEDIA_TYPE_ALL_IMAGES = MEDIA_TYPE_REGULAR_IMAGES | MEDIA_TYPE_DRM_IMAGES | MEDIA_TYPE_CLOUD_IMAGES;
    /** 
     * A media type for get Collection, target to get all videos.
     */
    public static final int MEDIA_TYPE_ALL_VIDEOS = MEDIA_TYPE_REGULAR_VIDEOS | MEDIA_TYPE_DRM_VIDEOS | MEDIA_TYPE_CLOUD_VIDEOS;
    /** 
     * A media type for get Collection, target to get all images and videos.
     */
    public static final int MEDIA_TYPE_ALL_MEDIA = MEDIA_TYPE_ALL_IMAGES | MEDIA_TYPE_ALL_VIDEOS;

    /** 
     * A source type for get Collection, the retrieve target is Albums information.
     */
    public static final int SOURCE_ALBUMS = 0;
    
    /** 
     * A source type for get Collection, the retrieve target is time line information.
     */
    public static final int SOURCE_TIMELINE = 1;
    
    /** 
     * A source type for get Collection, the retrieve target is virtual search information.
     */
    public static final int SOURCE_TAG = 2;
    
    /** 
     * A source type for get Collection, the retrieve target is location group information.
     */
    public static final int SOURCE_LOCATION = 3;
    
    /** 
     * A source type for get Collection, the retrieve target only contains virtual Collections.
     */
    public static final int SOURCE_VIRTUAL_ALBUMS = 4;
    
    /** 
     * A source type for get Collection, the retrieve target only contains cloud tag Collections.
     */
    public static final int SOURCE_CLOUDTAG = 5;
    
    /** 
     * A level to present the time line collections which group by 2 hours, 3km. 
     */
    public static final int LEVEL_TIMELINE_MOMENT = 0;
    
    /** 
     * A level to present the time line collections which group by day. 
     */
    public static final int LEVEL_TIMELINE_DAY = 1;
    
    /** 
     * A level to present the time line collections which group by month. 
     */
    public static final int LEVEL_TIMELINE_MONTH = 2;
    
    /** 
     * A level to present the time line collections which group by year. 
     */
    public static final int LEVEL_TIMELINE_YEAR = 3;
    
    /** 
     * A level to present the location collections which group by city.
     */
    public static final int LEVEL_LOCATION_CITY = 4;
    
    /** 
     * A level to present the location collections which group by POI. 
     */
    public static final int LEVEL_LOCATION_PLACE = 5;
    
    /** 
     * A level to present the location collections which group by MAP. 
     */
    public static final int LEVEL_LOCATION_MAP = 6;
    
    /** 
     * If client doesn't need to indicate level, use the value for default. 
     */
    public static final int LEVEL_DEFAULT = 7;
    
    /** 
     * The first callback status of grouping result.
     */
    public static final int GROUP_MODE_UPDATE_NEW = 0;
    
    /** 
     * The remaining callback status of grouping result.
     */
    public static final int GROUP_MODE_UPDATE_APPEND = 1;
    
    /** 
     * Address retriever status is start
     */
    public static final int RETRIEVE_STATE_START = 1000;
    
    /** 
     * Address retriever status is error
     */
    public static final int RETRIEVE_STATE_ERROR = 1001;
    
    /** 
     * Address retriever status is complete
     */
    public static final int RETRIEVE_STATE_COMPLETE = 1002;

    /**
     * The group task is in idle mode.
     */
    public static final int GROUP_STATE_IDLE = 2000;
    
    /**
     * The group task is ready to run.
     */
    public static final int GROUP_STATE_START = 2001;
    
    /**
     * The group task is running.
     */
    public static final int GROUP_STATE_GROUPING = 2002;
    
    /**
     * The group task is finished.
     */
    public static final int GROUP_STATE_COMPLETED = 2003;
    
    /**
     * Error occurs while executing group task.
     */
    public static final int GROUP_STATE_ERROR = 2004;
    
    /**
     * The expand task is in idle mode. (Sense65)
     */
    public static final int EXPAND_STATE_IDLE = 2000;
    
    /**
     * The expand task is ready to run. (Sense65)
     */
    public static final int EXPAND_STATE_START = 2001;
    
    /**
     * The expand task is running. (Sense65)
     */
    public static final int EXPAND_STATE_EXPANDING = 2002;
    
    /**
     * The expand task is finished. (Sense65)
     */
    public static final int EXPAND_STATE_COMPLETED = 2003;
    
    /**
     * Error occurs while executing expand task. (Sense65)
     */
    public static final int EXPAND_STATE_ERROR = 2004;
    
    /**
     * The search task is in idle mode. (Sense65)
     */
    public static final int SEARCH_STATE_IDLE = 2000;
    
    /**
     * The search task is ready to run. (Sense65)
     */
    public static final int SEARCH_STATE_START = 2001;
    
    /**
     * The search task is running. (Sense65)
     */
    public static final int SEARCH_STATE_SEARCHING = 2002;
    
    /**
     * The search task is finished. (Sense65)
     */
    public static final int SEARCH_STATE_COMPLETED = 2003;
    
	/**
	 * Error occurs while executing search task. (Sense65)
	 */
    public static final int SEARCH_STATE_ERROR = 2004;
    
    /**
     * The search task is canceled. (Sense65)
     */
	public static final int SEARCH_STATE_CANCEL = 2005;
    
    /**
     *  Extra data of KEY_INT_CLONE_HTC_TYPE: Force clone.
     */
    public static final int CLONE_HTCTYPE = 3001;
    
    /**
     *  Extra data of KEY_INT_CLONE_HTC_TYPE: Clone htc_type and remove slow motion bit.
     */
    public static final int CLONE_HTCTYPE_WITHOUT_SLOWMOTION = 3002;
    
    /**
     *  Extra data of KEY_INT_ERROR_CODE: bind service fail 
     */
    public static final int ERROR_SERVICE_BIND_FAIL = -1;
    
    /**
     *  Extra data of KEY_INT_ERROR_CODE: bind service time out
     */
    public static final int ERROR_SERVICE_BIND_TIME_OUT = -2;
    
    /**
     *  Integer value to indicate that the source Uri need to be converted to MMP image table Uri.
     */
    public static final int TYPE_IMAGE = 0;
    
    /**
     *  Integer value to indicate that the source Uri need to be converted to MMP video table Uri.
     */
    public static final int TYPE_VIDEO = 1;
    
    /**
     *  Integer value to indicate that the source Uri need to be converted to MMP file table Uri.
     */
    public static final int TYPE_FILE = 2;
    
    /**
     *  A collection type for all download & screenshots folder. 
     */
    public static final String COLLECTION_ALBUM_DOWNLOAD_AND_SCREENSHOTS = "collection_album_download_and_screenshots";
    
    /**
     *  A collection type for all medias. 
     */
    public static final String COLLECTION_ALBUM_ALL_MEDIA = "collection_all_media";

    /**
     *  A collection type for all photos. 
     */
    public static final String COLLECTION_ALBUM_ALL_PHOTOS = "collection_all_photos";
    
    /**
     *  A collection type for all videos. 
     */
    public static final String COLLECTION_ALBUM_ALL_VIDEOS = "collection_all_videos";
    
    /**
     *  A collection type for all downloads. 
     */
    public static final String COLLECTION_ALBUM_ALL_DOWNLOADS = "collection_all_downloads";
    
    /**
     *  A collection type for camera shots. 
     */
    public static final String COLLECTION_ALBUM_CAMERA_SHOTS = "collection_camera_shots";
    
    /**
     *  A collection type for high light. 
     */
    public static final String COLLECTION_ALBUM_HIGHLIGHT = "collection_highlight";
    
    /**
     *  A collection type for regular bucket. 
     */
    public static final String COLLECTION_ALBUM_BUCKET = "collection_regular_bucket";
    
    /**
     *  A collection type for user created virtual folder. 
     */
    public static final String COLLECTION_ALBUM_USER = "collection_album_user";
    
    /**
     *  A collection type for video highlight. 
     */
    public static final String COLLECTION_ALBUM_VIDEO_HIGHLIGHT = "collection_album_video_highlight";
    
    /**
     *  A collection type for screenshot. 
     */
    public static final String COLLECTION_ALBUM_SCREENSHOT = "collection_album_screenshot";
    //Mod by lvlin for screenrecord translation begin
    public static final String COLLECTION_ALBUM_SCREENRECORD = "collection_album_screenrecord";
    //Mod by lvlin for screenrecord translation end

    //Mod by lvlin for drawing translation begin
    public static final String COLLECTION_ALBUM_DRAWING = "collection_album_drawing";
    //Mod by lvlin for drawing translation end
    /**
     *  A collection type for music folder. 
     */
    public static final String COLLECTION_ALBUM_MUSIC = "collection_album_music";
    
    /**
     *  A collection type for selfie virtual folder. 
     */
    public static final String COLLECTION_ALBUM_SELFIE = "collection_album_selfie";
    
    /**
     *  A collection type for white board virtual folder. 
     */
    public static final String COLLECTION_ALBUM_WHITE_BOARD = "collection_album_white_board";
    
    /**
     *  A collection type for photo lab virtual folder. 
     */
    public static final String COLLECTION_ALBUM_PHOTO_LAB = "collection_album_photo_lab";
    
    /**
     *  A collection type for magic_thumbs virtual folder. 
     */
    public static final String COLLECTION_ALBUM_MAGIC_THUMB = "COLLECTION_ALBUM_MAGIC_THUMBS";
    
    /**
     *  A collection type for RAW photos virtual folder
     */
    public static final String COLLECTION_ALBUM_RAW_PHOTOS = "COLLECTION_ALBUM_RAW_PHOTOS";
    
    
    /**
     *  A collection type for timeline medias. 
     */
    public static final String COLLECTION_TIMELINE_MEDIAS = "COLLECTION_TIMELINE_MEDIAS";
    
    
    /**
     *  A collection type for time line moment. 
     */
    public static final String COLLECTION_TIMELINE_MOMENT = "collection_timeline_moment";
    
    /**
     *  A collection type for time line day. 
     */
    public static final String COLLECTION_TIMELINE_DAY = "collection_timeline_day";
    
    /**
     *  A collection type for time line month. 
     */
    public static final String COLLECTION_TIMELINE_MONTH = "collection_timeline_month";
    
    /**
     *  A collection type for time line year. 
     */
    public static final String COLLECTION_TIMELINE_YEAR = "collection_timeline_year";
    
    /**
     *  A collection type for virtual search. 
     */
    public static final String COLLECTION_TAG = "collection_tag";
    
    /**
     *  A collection type for location city. 
     */
    public static final String COLLECTION_LOCATION_CITY = "collection_location_city";
    
    /**
     *  A collection type for location place. 
     */
    public static final String COLLECTION_LOCATION_PLACE = "collection_location_place";
    
    /**
     *  A collection type for location map. 
     */
    public static final String COLLECTION_LOCATION_MAP = "collection_location_map";
    
    /**
     *  A collection type for online facebook. 
     */
    public static final String COLLECTION_ONLINE_FACEBOOK = "collection_online_facebook";
    
    /**
     *  A collection type for dropbox. 
     */
    public static final String COLLECTION_ONLINE_DROPBOX = "collection_online_dropbox";
    
    /**
     *  A collection type for google drive. 
     */
    public static final String COLLECTION_ONLINE_GOOGLEDRIVE = "collection_online_googledrive";
    
    /**
     *  A collection type for flickr. 
     */
    public static final String COLLECTION_ONLINE_FLICKR = "collection_online_flickr";
    
    /**
     *  A collection type for service cloud tag. 
     */
    public static final String COLLECTION_CLOUDTAG_SERVICE = "collection_cloudtag_service";
    
    /**
     *  A collection type for user define cloud tag. 
     */
    public static final String COLLECTION_CLOUDTAG_USER = "collection_cloudtag_user";
    
    /**
     *  A bundle key to indicate that the grouping result not refer to cache.  
     */
    public static final String KEY_BOOLEAN_FORCE_RELOAD = "key_boolean_force_reload";
    
    /**
     *  A bundle key to indicate that the grouping result need to carry name information.  
     */
    public static final String KEY_BOOLEAN_REQUEST_COLLECTION_NAME = "key_boolean_request_collection_name";

    /**
     *  A bundle key to indicate that {@link expand(Collection,int, Bundle)} need to query total count or not
     */    
    public static final String KEY_BOOLEAN_REQUEST_TOTAL_COUNT = "key_boolean_request_total_count";    
    
    /**
     *  A bundle key to indicate which album collection need to be filter out.   
     */
    public static final String KEY_INTEGER_ALBUMS_FILTER = "KEY_INTEGER_ALBUMS_FILTER";
    
    /**
     * A bundle key to indicate which cloud tag collection need to be filter out.
     */
    public static final String KEY_INTEGER_CLOUDTAG_FILTER = "key_integer_cloudtag_filter";
    
    /**
     *  Key to retrieve int value from AP has clone htc_type or not.
     *  0: Do nothing.
     *  CLONE_HTCTYPE: Force clone.
     *  CLONE_HTCTYPE_WITHOUT_SLOWMOTION: Clone htc_type and remove slow motion bit.
     */
    public static final String KEY_INT_CLONE_HTC_TYPE = "key_int_clone_htc_type";
    
    /**
     * Key to retrieve boolean value from AP has access network permission or not.
     */
    public static final String KEY_BOOLEAN_ALLOW_NETWORK_ACCESS = "key_boolean_allow_network_access";

    /**
     * A Key to retrieve that how many collection in this list. 
     */
    public static final String KEY_INT_ROW_SIZE = "key_int_row_size";
    
    /**
     * A Key to retrieve a index of start row in the list. 
     */
    public static final String KEY_INT_ROW_START = "key_int_row_start";
    
    /**
     * A Key to retrieve a index of end row in the list. 
     */
    public static final String KEY_INT_ROW_END = "key_int_row_end";
    
    /**
     * A Key to retrieve the total amount of collection in the list. 
     */
    public static final String KEY_INT_ROW_TOTAL_SIZE = "key_int_row_total_size";
    
    /**
     * A Key to indicate that collection need to have cover list.
     */
    public static final String KEY_BOOLEAN_ENABLE_COVERLIST = "key_boolean_enable_coverlist";
    
    /**
     * A Key to indicate that collection need to have cover list.
     */
    public static final String KEY_BOOLEAN_RETURN_COVERLIST = "KEY_BOOLEAN_RETURN_COVERLIST";
    
    /**
     * A Key to indicate that size of each partial update.
     */
    public static final String KEY_INTEGER_PARTIAL_LOAD_SIZE = "KEY_INTEGER_PARTIAL_LOAD_SIZE";
    
    /**
     * A Key to indicate that size of first partial update.
     */
    public static final String KEY_INTEGER_FIRST_PARTIAL_LOAD_SIZE = "KEY_INTEGER_FIRST_PARTIAL_LOAD_SIZE";
    
    /**
     * A Key to indicate that cloneSource need to exclude MMP db column(v_folder, c_album) clone.
     */
    public static final String KEY_BOOLEAN_EXCLUDE_VFOLDER_CALBUM = "key_boolean_exclude_vfolder_calbum";
    
    /**
     * A Key to indicate that cloneSource need to exclude MMP db column(v_folder) clone.
     */
    public static final String KEY_BOOLEAN_EXCLUDE_VFOLDER_ONLY = "key_boolean_exclude_vfolder_only";
    
    /**
     * A Key to indicate that cloneSource need to exclude MMP db column(c_album) clone.
     */
    public static final String KEY_BOOLEAN_EXCLUDE_CALBUM_ONLY = "key_boolean_exclude_calbum_only";
    
    /**
     * A Key to indicate that cloneSource need to exclude TAG(ImageMatch) db clone.
     */
    public static final String KEY_BOOLEAN_EXCLUDE_IMAGETAG = "key_boolean_exclude_imagetag";
    
    /**
     * A Key to indicate that cloneSource need to exclude MCP CloudTag db clone.
     */
    public static final String KEY_BOOLEAN_EXCLUDE_CLOUDTAG = "key_boolean_exclude_cloudtag";
    
    /**
     * A Key to indicate that cloneSource need to exclude  MMP db column(favorite) clone.
     */
    public static final String KEY_BOOLEAN_EXCLUDE_FAVORITE = "key_boolean_exclude_favorite";
    
    /**
     * A Key to indicate the max cover list size in the collection.
     */
    public static final String KEY_INTEGER_COVERLIST_SIZE = "key_integer_coverlist_size";
    
    /**
     * A Key to indicate the sort order of cover list.
     */
    public static final String KEY_INTEGER_COVERLIST_SORTORDER = "key_integer_coverlist_sortorder";
    
    /**
     * A Key to indicate the sort order of expand.
     */
    public static final String KEY_INTEGER_EXPAND_SORTORDER = "KEY_INTEGER_EXPAND_SORTORDER";
    
    /**
     * A Key to indicate those items which want to be computed hash value. (Sense65)
     */
    public static final String KEY_ARRAYLIST_COMPUTE_HASH_ITEMS = "key_arraylist_compute_hash_items";
    
    /**
     * A Key for search's bundle to set partial update size. (Sense65)
     */
    public static final String KEY_SEARCH_PARTIAL_UPDATE_SIZE = "key_search_partial_update_size";
    
    /**
     * A Key for search's bundle to set keyword. (Sense65)
     */
    public static final String KEY_SEARCH_STRING = "key_search_string";
    
    /**
     * A Key to indicate that collection need to have sub collection list
     */
    public static final String KEY_BOOLEAN_ENABLE_SUBLIST = "KEY_BOOLEAN_ENABLE_SUBLIST";
    
    /**
     * A Key to indicate that MediaObject of cloud content need to have title & display name, default value is false
     */
    public static final String KEY_BOOLEAN_RETRIEVE_CLOUD_MEDIAOBJECT_TITLE = "KEY_BOOLEAN_RETRIEVE_CLOUD_MEDIAOBJECT_TITLE";
    
    /**
     * Sort by ascend.
     * @deprecated not support anymore: default behavior is sort by time DESC
     */
    public static final int COVERLIST_SORT_ORDER_ASC = 0;
    
    /**
     * Sort by descend.  
     */
    public static final int COVERLIST_SORT_ORDER_DESC = 1;
    
    /**
     * Sort by shoebox rank value descend.  
     */
    public static final int COVERLIST_SORT_SHOEBOX_RANK_DESC = 2;
    
    /**
     * Sort by descend.  
     */
    public static final int EXPAND_SORT_BY_TIME_DESC = 0;
    /**
     * Sort by shoebox rank value descend.  
     */
    public static final int EXPAND_SORT_BY_SHOEBOX_RANK_DESC = 1;

    /**
     * List all cloud tag collection (cloud service tag + user created tag).
     */
    public static final int CLOUDTAG_FILTER_ALL = 0;
    
    /**
     * List cloud tag collection (cloud service only).
     */
    public static final int CLOUDTAG_FILTER_CLOUD_SERVICE_ONLY = 1;
    
    /**
     * List cloud tag collection (user created only).  
     */
    public static final int CLOUDTAG_FILTER_USER_TAG_ONLY = 2;
    
    /**
     * List all album (album folder + Virtual search + user created folder)
     */
    public static final int ALBUMS_FILTER_ALL = 0;
    
    /**
     * List album collection only  
     */
    public static final int ALBUMS_FILTER_EXCLUDE_VIRTUAL_ALBUMS = 1;
    
    /**
     * List virtual album collection only (Virtual search + user created folder)  
     */
    public static final int ALBUMS_FILTER_ONLY_VIRTUAL_ALBUMS = 2;
    
    /**  
     * Key to retrieve int value from getColletionList bundle for check Collection show or hide 
     */
    public static final String KEY_INT_FILTER = "filter";
    /** 
     * Collection for show items
     */
    public static final int FILTER_FOR_SHOW = 0;
    /** 
     * Collection for hide items
     */
    public static final int FILTER_FOR_HIDE = 1;
    /** 
     * Collection for show and hide items
     */
    public static final int FILTER_FOR_ALL = 2;
    /**  
     * Key to retrieve boolean value from getColletionList bundle for force update v_folder or not
     */
    public static final String KEY_BOOLEAN_FORCE_UPDATE = "force_update";
    /**
     * Key to retrieve boolean value from getColletion quickly but lost some info
     */
    public static final String KEY_BOOLEAN_QUICK_GET_COLLECTION = "quick_get_collection";
    /**
     *  Key to retrieve an extra string of customize image SQL filter 
     */
    public static final String KEY_CUSTOM_IMAGE_WHERE = "key_custom_image_where";
    /**
     *  Key to retrieve an extra string of customize video SQL filter 
     */
    public static final String KEY_CUSTOM_VIDEO_WHERE = "key_custom_video_where";
    /**
     *  Key to retrieve an extra string of customize file SQL filter 
     */
    public static final String KEY_CUSTOM_FILE_WHERE = "key_custom_files_where";

    // For service intent send/get use
    /**
     *  An intent action for start MediaManagerService to udpate v_folder
     */
    public static final String ACTION_GROUP_REQUEST_UPDATE = "com.htc.mediamanager.Intent.ACTION_GROUP_REQUEST_UPDATE";
    /**
     *  An intent action for call back onCollectionChange to MediaManager
     */
    public static final String ACTION_GROUP_UPDATE_COMPLETED = "com.htc.mediamanager.Intent.ACTION_GROUP_UPDATE_COMPLETED";
    /**
     * An intent action for triggering service to do DB recovery (ZOE, BRUST) 
     */
    public static final String ACTION_RECOVER_ZOE_BURST = "com.htc.mediamanager.ACTION_RECOVER_ZOE_BURST";
    /**
     *  Key for the service callback identifier
     */
    public static final String KEY_IDENTIFIER = "key_identifier";
    /**
     *  MCP use for get album name. 
     */
    public static final String MCP_CALL_CMD_GET_ALBUM_NAME = "MCP_CALL_CMD_GET_ALBUM_NAME";
    /**
     *  MPC use for get video highlights path.
     */
    public static final String MCP_CALL_CMD_GET_VIDEO_HIGHLIGHTS_PATH = "MCP_CALL_CMD_GET_VIDEO_HIGHLIGHTS_PATH";
    /**
     *  Command for getting MCP version
     */
    public static final String MCP_CALL_CMD_GET_VERSION = "MCP_CALL_CMD_GET_VERSION";
    
    /**
     * Key for the retrieving server type. (Sense65)
     */
    public static final String KEY_INTEGER_SERVICE_TYPE = "key_integer_service_type";
    
    /**
     * Key for the retrieving error code. (Sense65)
     */
    public static final String KEY_INT_ERROR_CODE = "key_int_error_code";
    
    /**
     * Key for the retrieving ID for expand task. (Sense65)
     */
    public static final String KEY_INT_TASK_ID = "key_int_task_id";
    
    /**
     * Service type identify. (Sense65)
     */
    public static final int SERVICE_TYPE_LOCAL               = 0x01;
    public static final int SERVICE_TYPE_ONLINE_DROPBOX      = 0x02;
    public static final int SERVICE_TYPE_ONLINE_FACEBOOK     = 0x04;
    public static final int SERVICE_TYPE_ONLINE_FLICKR       = 0x08;
    public static final int SERVICE_TYPE_ONLINE_GOOGLEDRIVE  = 0x10;
    public static final int SERVICE_TYPE_ONLINE = 
            SERVICE_TYPE_ONLINE_DROPBOX | 
            SERVICE_TYPE_ONLINE_FACEBOOK | 
            SERVICE_TYPE_ONLINE_FLICKR | 
            SERVICE_TYPE_ONLINE_GOOGLEDRIVE;
    public static final int SERVICE_TYPE_ALL = 
            SERVICE_TYPE_LOCAL | 
            SERVICE_TYPE_ONLINE;
    
    /**
     * Tagged type identify FLICKR. (Sense65)
     */
    public static final int TAGGED_TYPE_FLICKR = 0x01;
    /**
     * Tagged type identify FACEBOOK. (Sense65)
     */
    public static final int TAGGED_TYPE_FACEBOOK = 0x02;
    /**
     * Tagged type identify USER. (Sense65)
     */
    public static final int TAGGED_TYPE_USER = 0x04;

    /**
     * A interface to define callback functions for collection update.
     */
    public static interface onGroupInfoChangeListener
    {
        /**
         * Called when a mount of grouping data has been arrived. 
         */
        void onCollectionListUpdate(int nTaskId, ArrayList<Collection> collectionList, int mode, int level, Bundle extra);
        
        /**
         * Called when status change.
         */
        void onGroupStatusChanged(int nTaskId, int state, int level, Bundle extra);
    };
    
    /**
     * A interface to define callback functions for collection name update.
     */
    public static interface onDisplayNameUpdateListener
    {
        /**
         * Called when a mount of collection name has been updated.
         * @param al A mount of CollectionName object. 
         */
        void onDisplayNameUpdated(int nTaskId, int level, ArrayList<CollectionName> nameList, Bundle extra);
        void onDisplayNameRetrieverStateChange(int nTaskId, int state);
    };

    /**
     *  A callback listener for monitor the MediaManagerProvider or v_folder update timing.
     */
    public static interface onCollectionChangeListener
    {
        /**
         *  Notify user MediaManagerProvider or v_folder has changed.
         */
        void onCollectionChange();
    }

    /**
     * A interface to define callback functions for service status update.
     */
    public static interface onMMServiceStatusListener
    {
        /**
         * Called when the connection to MM service is established.
         *
         */
        void onServiceConnected();

        /**
         * Called when the connection to MM service is released.
         *
         */
        void onServiceDisconnected();
        
        /**
         * Called when there is an error during connecting to MCM service.
         *
         */
        void onServiceConnectionError(String errorReason);
    }
    
    /**
     * A interface to define callback functions for search result. (Sense65)
     */
    public static interface onSearchResultListener
    {
        /**
         * Called when a mount of grouping data has been arrived. 
         */
        void onSearchResult(int nTaskId, ArrayList<Collection> collectionList, int mode, Bundle extra);
        
        /**
         * Called when status change.
         */
        void onSearchStatusChanged(int nTaskId, int state, Bundle extra);
    };
    
    /**
     * A interface to define callback functions for search result. (Sense65)
     */
    public static interface onExpandResultListener
    {
        /**
         * Called when a mount of expanding data has been arrived.
         */
        void onExpandResult(int nTaskId, ArrayList<MediaObject> mediaObjectList, int mode, Bundle extra);
        
        /**
         * Called when collection info need to be updated.
         */
        void onCollectionUpdated(int nTaskid, Collection c, Bundle extra);
        
        /**
         * Called when status change.
         */
        void onExpandStatusChanged(int nTaskId, int state, Bundle extra);
    };
    
    /**
     * A interface to define callback functions for service state change
     */
    public static interface onServiceStateListener
    {
        /**
        * When setServiceFiltered is triggered, call back this function to let host reload data.
        */
        public void onFiltered(int beShownServices, Bundle extras);
    };
    
    /**
     * A interface to be called as PhotoPlatform's service status is changed
     */
    public static interface onPPServiceStatusListener
    {
		public void onPPServiceConnect();
		public void onPPServiceDisconnect();
	}
    
	/**
	 * return code for presenting no error.
	 */
    public static final int RETURN_CODE_OK  = 0;
    
    /**
     * return code for presenting invalid arguments.
     */
    public static final int RETURN_CODE_INVALID_ARGUMENT = -1001;
    
    /**
     * return code for presenting invalid arguments.
     */
    public static final int RETURN_CODE_WRONG_STATE = -1002;
    
    /**
     * return code for presenting bind MM service failed.
     */
    public static final int RETURN_CODE_BIND_SERVICE_FAILED = -1003;
    
    /**
     * Service status for service disconnected.
     */
    public static final int STATE_DISCONNECTED = 0;
    
    /**
     * Service status for service is connecting.
     */
    public static final int STATE_CONNECTING = 1;
    
    /**
     * Service status for service is connected.
     */
    public static final int STATE_CONNECTED = 2;
    /* Constants End */
    
    private Context mContext = null;
    private int mState = STATE_DISCONNECTED;
    private String mStrUuid = UUID.randomUUID().toString();
    private IMediaCollectionManager mService;
    private onMMServiceStatusListener mStatusListener;
    private MMServiceConnection mMMServiceConnection = new MMServiceConnection();
    private MMPServiceConnection mMMPServiceConnection = new MMPServiceConnection();
    private IntentFilter mIntentMediaCollectionFilter = null;
    private BroadcastReceiver mMediaCollectionReceiver = null;
    final private InternalonDisplayNameUpdateListener mInternalDisplayNameUpdateListener;
    final private InternalonGroupInfoChangeListener mInternalGroupInfoChangeListener;
    private onGroupInfoChangeListener mGroupInfoChangeListener; 
    private onDisplayNameUpdateListener mDisplayNameChangeListener;
    
    //Sense65 new append >>>
    private onSearchResultListener mSearchResultListener;
    private onExpandResultListener mExpandResultListener;
    private onServiceStateListener mServiceStateListener;
    
    final private InternalonSearchResultListener mInternalSearchResultListener;
    final private InternalonExpandResultListener mInternalExpandResultListener;
    final private InternalOnServiceStateListener mInternalServiceStateListener;
    //Sense65 new append <<<

    private boolean mIsCacheEnable = false;
    /**
     * 
     * @param context
     */
    public MediaManager(Context context)
    {
        LOG.I(LOG_TAG, "[Constructor], this = " + getThisHash());
        mContext = context;
        mInternalDisplayNameUpdateListener = new InternalonDisplayNameUpdateListener(this);
        mInternalGroupInfoChangeListener = new InternalonGroupInfoChangeListener(this);
        mInternalSearchResultListener = new InternalonSearchResultListener(this);
        mInternalExpandResultListener = new InternalonExpandResultListener(this);
        mInternalServiceStateListener = new InternalOnServiceStateListener(this);
    }
    
    private static class InternalonDisplayNameUpdateListener extends IonDisplayNameUpdateListener.Stub
    {
        private final WeakReference<MediaManager> mRef;
        
        public InternalonDisplayNameUpdateListener(MediaManager mgr)
        {
            mRef = new WeakReference<MediaManager>(mgr);
        }
        
        @Override
        public void onDisplayNameUpdated(
                int nTaskId, 
                int level,
                List<CollectionName> al,
                Bundle extra)
        {
            final MediaManager mgr = (mRef!=null) ? mRef.get() : null;
            
            if (null == mgr)
            {
                LOG.W(LOG_TAG, "[InternalonDisplayNameUpdateListener::onDisplayNameUpdated], mgr == null");
                return;
            }
            LOG.I(LOG_TAG, "[InternalonDisplayNameUpdateListener::onDisplayNameUpdated], mgr = " + mgr.getThisHash());
            onDisplayNameUpdateListener listener = mgr.mDisplayNameChangeListener;
            if (null != listener)
            {
                LOG.I(LOG_TAG, "[InternalonDisplayNameUpdateListener::onDisplayNameUpdated], nTaskId = " + nTaskId + ", level = " + level);
                listener.onDisplayNameUpdated(
                        nTaskId,
                        level,
                        (ArrayList<CollectionName>) al,
                        extra);
            }
            else
                LOG.W(LOG_TAG, "[InternalonDisplayNameUpdateListener::onDisplayNameUpdated], listener == null");
        }

        @Override
        public void onDisplayNameRetrieverStateChange(int nTaskId, int state) throws RemoteException
        {
            final MediaManager mgr = (mRef!=null) ? mRef.get() : null;
            
            if (null == mgr)
            {
                LOG.W(LOG_TAG, "[InternalonDisplayNameUpdateListener::onDisplayNameRetrieverStateChange], mgr == null");
                return;
            }
            LOG.I(LOG_TAG, "[InternalonDisplayNameUpdateListener::onDisplayNameRetrieverStateChange], mgr = " + mgr.getThisHash());

            onDisplayNameUpdateListener listener = mgr.mDisplayNameChangeListener;
            if (null != listener)
            {
                LOG.I(LOG_TAG, "[InternalonDisplayNameUpdateListener::onDisplayNameRetrieverStateChange], nTaskId = " + nTaskId + ", state = " + state);
                listener.onDisplayNameRetrieverStateChange(nTaskId, state);
            }
            else
                LOG.W(LOG_TAG, "[InternalonDisplayNameUpdateListener::onDisplayNameRetrieverStateChange], listener == null");
        }
    }
    
    private static class InternalonGroupInfoChangeListener extends IonGroupInfoChangeListener.Stub
    {
        private final WeakReference<MediaManager> mRef;
        
        public InternalonGroupInfoChangeListener(MediaManager mgr)
        {
            mRef = new WeakReference<MediaManager>(mgr);
        }
        
        @Override
        public void onGroupUpdated(
                int nTaskId, List<Collection> l, 
                int mode,
                int level, Bundle extra) 
        {
            final MediaManager mgr = (mRef!=null) ? mRef.get() : null;
            if(null == mgr)
            {
                LOG.W(LOG_TAG, "[InternalonGroupInfoChangeListener::onGroupUpdated], mgr == null");
                return;
            }
            LOG.I(LOG_TAG, "[InternalonGroupInfoChangeListener::onGroupUpdated], mgr = " + mgr.getThisHash());

            onGroupInfoChangeListener listener = mgr.mGroupInfoChangeListener;
            
            if (null != listener)
            {
                LOG.I(LOG_TAG, "[InternalonGroupInfoChangeListener::onGroupUpdated], nTaskId = " + nTaskId + ", mode = " + mode + ", level = " + level);
                ArrayList<Collection> al = (ArrayList<Collection>) l;
                LOG.I(LOG_TAG, "[InternalonGroupInfoChangeListener::onGroupUpdated], size of collection list = " + ((al!=null)?al.size():"null"));
                listener.onCollectionListUpdate(nTaskId, al, mode, level, extra);
            }
            else
                LOG.W(LOG_TAG, "[InternalonGroupInfoChangeListener::onGroupUpdated], listener == null");
        }

        @Override
        public void onGroupStatusChanged(
                int nTaskId, 
                int state, 
                int level,
                Bundle extra)
            
        {
            final MediaManager mgr = (mRef!=null) ? mRef.get() : null;
            if(null == mgr)
            {
                LOG.W(LOG_TAG, "[InternalonGroupInfoChangeListener::onGroupStatusChanged], mgr == null");
                return;
            }
            LOG.I(LOG_TAG, "[InternalonGroupInfoChangeListener::onGroupStatusChanged], mgr = " + mgr.getThisHash());

            onGroupInfoChangeListener listener = mgr.mGroupInfoChangeListener;
            
            if (null != listener)
            {
                LOG.I(LOG_TAG, "[InternalonGroupInfoChangeListener::onGroupStatusChanged], nTaskId = " + nTaskId + ", state = " + state + ", level = " + level);
                listener.onGroupStatusChanged(nTaskId, state, level, extra);
            }
            else
                LOG.W(LOG_TAG, "[InternalonGroupInfoChangeListener::onGroupStatusChanged], listener == null");
        }
    };
    
    private static class InternalonExpandResultListener extends IonExpandResultListener.Stub
    {
        private final WeakReference<MediaManager> mRef;
        
        public InternalonExpandResultListener(MediaManager mgr)
        {
            mRef = new WeakReference<MediaManager>(mgr);
        }
        
        @Override
        public void onExpandResult(int nTaskId,
                List<MediaObject> mediaObjectList, int mode, Bundle extra)
                throws RemoteException
        {
            final MediaManager mgr = (mRef != null) ? mRef.get() : null;
            if(null == mgr)
            {
                LOG.W(LOG_TAG, "[InternalonExpandResultListener::onExpandResult], mgr == null");
                return;
            }
            
            LOG.I(LOG_TAG, "[InternalonExpandResultListener::onExpandResult], mgr = " + mgr.getThisHash());
            onExpandResultListener listener = mgr.mExpandResultListener;
            
            if (null != listener)
            {
                LOG.I(LOG_TAG, "[InternalonExpandResultListener::onExpandResult], nTaskId = " + nTaskId);
                ArrayList<MediaObject> al = (ArrayList<MediaObject>) mediaObjectList;
                LOG.I(LOG_TAG, "[InternalonExpandResultListener::onExpandResult], size of mediaObjectList = " + ((al!=null)?al.size():"null"));
                LOG.I(LOG_TAG, "[InternalonExpandResultListener::onExpandResult], mode = " + mode);
                listener.onExpandResult(nTaskId, al, mode, extra);
            }
            else
            {
                LOG.W(LOG_TAG, "[InternalonExpandResultListener::onExpandResult], listener == null");
            }
            
        }

        @Override
        public void onCollectionUpdated(int nTaskid, Collection c, Bundle extra)
                throws RemoteException
        {
            final MediaManager mgr = (mRef != null) ? mRef.get() : null;
            if(null == mgr)
            {
                LOG.W(LOG_TAG, "[InternalonExpandResultListener::onCollectionUpdated], mgr == null");
                return;
            }
            LOG.I(LOG_TAG, "[InternalonExpandResultListener::onCollectionUpdated], mgr = " + mgr.getThisHash());

            onExpandResultListener listener = mgr.mExpandResultListener;
            if (null != listener)
            {
                LOG.I(LOG_TAG, "[InternalonExpandResultListener::onCollectionUpdated], nTaskid = " + nTaskid + ", Collection = " + c);
                listener.onCollectionUpdated(nTaskid, c, extra);
            }
            else
            {
                LOG.W(LOG_TAG, "[InternalonExpandResultListener::onCollectionUpdated], listener == null");
            }
        }

        @Override
        public void onExpandStatusChanged(int nTaskId, int state, Bundle extra)
                throws RemoteException
        {
            final MediaManager mgr = (mRef != null) ? mRef.get() : null;
            if(null == mgr)
            {
                LOG.W(LOG_TAG, "[InternalonExpandResultListener::onExpandStatusChanged], mgr == null");
                return;
            }
            LOG.I(LOG_TAG, "[InternalonExpandResultListener::onExpandStatusChanged], mgr = " + mgr.getThisHash());

            onExpandResultListener listener = mgr.mExpandResultListener;
            if (null != listener)
            {
                LOG.I(LOG_TAG, "[InternalonExpandResultListener::onExpandStatusChanged], nTaskid = " + nTaskId + ", state = " + state);
                listener.onExpandStatusChanged(nTaskId, state, extra);
            }
            else
            {
                LOG.W(LOG_TAG, "[InternalonExpandResultListener::onExpandStatusChanged], listener == null");
            }
        }
    }
    
    private static class InternalonSearchResultListener extends IonSearchResultListener.Stub
    {
        private final WeakReference<MediaManager> mRef;
        
        public InternalonSearchResultListener(MediaManager mgr)
        {
            mRef = new WeakReference<MediaManager>(mgr);
        }

        @Override
        public void onSearchResult(
                int nTaskId,
                List<Collection> collectionList, 
                int mode, 
                Bundle extra)
                throws RemoteException
        {
            final MediaManager mgr = (mRef != null) ? mRef.get() : null;
            if(null == mgr)
            {
                LOG.W(LOG_TAG, "[InternalonSearchResultListener::onSearchResult], mgr == null");
                return;
            }
            LOG.I(LOG_TAG, "[InternalonSearchResultListener::onSearchResult], mgr = " + mgr.getThisHash());
            onSearchResultListener listener = mgr.mSearchResultListener;
            
            if (null != listener)
            {
                LOG.I(LOG_TAG, "[InternalonSearchResultListener::onSearchResult], nTaskid = " + nTaskId + ", mode = " + mode);
                ArrayList<Collection> al = (ArrayList<Collection>) collectionList;
                LOG.I(LOG_TAG, "[InternalonSearchResultListener::onSearchResult], size of collectionList = " + ((al!=null)?al.size():"null"));
                listener.onSearchResult(nTaskId, al, mode, extra);
            }
            else
            {
                LOG.W(LOG_TAG, "[InternalonSearchResultListener::onSearchResult], listener == null");
            }
        }

        @Override
        public void onSearchStatusChanged(int nTaskId, int state, Bundle extra)
                throws RemoteException
        {
            final MediaManager mgr = (mRef != null) ? mRef.get() : null;
            if(null == mgr)
            {
                LOG.W(LOG_TAG, "[InternalonSearchResultListener::onSearchStatusChanged], mgr == null");
                return;
            }
            LOG.I(LOG_TAG, "[InternalonSearchResultListener::onSearchStatusChanged], mgr = " + mgr.getThisHash());

            onSearchResultListener listener = mgr.mSearchResultListener;
            
            if (null != listener)
            {
                LOG.I(LOG_TAG, "[InternalonSearchResultListener::onSearchStatusChanged], nTaskid = " + nTaskId + ", state = " + state);
                listener.onSearchStatusChanged(nTaskId, state, extra);
            }
            else
            {
                LOG.W(LOG_TAG, "[InternalonSearchResultListener::onSearchStatusChanged], listener == null");
            }
        }
    }

    private static class InternalOnServiceStateListener extends IonServiceStateListener.Stub
    {
        private final WeakReference<MediaManager> mRef;
        
        public InternalOnServiceStateListener(MediaManager mgr)
        {
            mRef = new WeakReference<MediaManager>(mgr);
        }
        @Override
        public void onFiltered(int shownServices, Bundle extras) throws RemoteException
        {
            final MediaManager mgr = (mRef != null) ? mRef.get() : null;
            if(null == mgr)
            {
                LOG.W(LOG_TAG, "[InternalOnServiceStateListener::onFiltered], mgr == null");
                return;
            }
            LOG.I(LOG_TAG, "[InternalOnServiceStateListener::onFiltered], mgr = " + mgr.getThisHash());

            onServiceStateListener listener = mgr.mServiceStateListener;
            
            if (null != listener)
            {
                listener.onFiltered(shownServices, extras);
            }
            else
            {
                LOG.W(LOG_TAG, "[InternalOnServiceStateListener::onFiltered], listener == null");
            }
        }
    }
    
    /**
     * Service connection for MMPService
     * 
     */
    private class MMPServiceConnection implements ServiceConnection
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            LOG.D(LOG_TAG, "MMPServiceConnection::onServiceConnected " + name.getShortClassName());
            
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            LOG.D(LOG_TAG, "MMPServiceConnection::onServiceDisconnected " + name.getShortClassName());
            
        }
    }
    
    /**
     * ServiceConnection for MMService.
     */
    private class MMServiceConnection implements ServiceConnection
    {
        public void onServiceConnected(ComponentName name, IBinder binder)
        {
            LOG.I(LOG_TAG, "[MMServiceConnection::onServiceConnected] " + name.getShortClassName() + " tid = " + android.os.Process.myTid() + ", this = " + getThisHash());
            String strError = null;
            try
            {
                mService = IMediaCollectionManager.Stub.asInterface(binder);
            }
            catch (Exception e)
            {
                LOG.W(LOG_TAG, "[MMServiceConnection::onServiceConnected] Exception = " + e.getMessage());
                e.printStackTrace();
                strError = e.getMessage();
            }
            
            if (null != mService)
            {
                mState = STATE_CONNECTED;
                if (null != mStatusListener)
                {
                    try
                    {
                        mService.setGroupInfoChangeListener(mStrUuid, mInternalGroupInfoChangeListener);
                        mService.setDisplayNameUpdateListener(mStrUuid, mInternalDisplayNameUpdateListener);
                        mService.enableCache(mStrUuid, mIsCacheEnable);
                        
                        //Sense65 new append >>>
                        mService.setExpandResultListener(mStrUuid, mInternalExpandResultListener);
                        mService.setSearchResultListener(mStrUuid, mInternalSearchResultListener);
                        mService.setServiceStateListener(mStrUuid, mInternalServiceStateListener);
                        //Sense65 new append <<<
                    }
                    catch (RemoteException e)
                    {
                        LOG.W(LOG_TAG, "[MMServiceConnection::onServiceConnected] Exception = " + e.getMessage());
                        e.printStackTrace();
                    }
                    LOG.D(LOG_TAG, "[MMServiceConnection::onServiceConnected] call back to AP");
                    mStatusListener.onServiceConnected();
                }
                else
                    LOG.W(LOG_TAG, "[MMServiceConnection::onServiceConnected] mStatusListener is null");
            }
            else
            {
                LOG.W(LOG_TAG, "[MMServiceConnection::onServiceConnected] mService is null, this = " + getThisHash());
                mState = STATE_DISCONNECTED;
                if (null != mStatusListener)
                {
                    mStatusListener.onServiceConnectionError(strError);
                }
                else
                    LOG.W(LOG_TAG, "[MMServiceConnection::onServiceConnected] mStatusListener is null");
            }
        }

        public void onServiceDisconnected(ComponentName name)
        {
            LOG.I(LOG_TAG, "[MMServiceConnection::onServiceDisconnected]" +  name);
            mState = STATE_DISCONNECTED;
            if (null != mStatusListener)
            {
                mStatusListener.onServiceDisconnected();
            }
            else
                LOG.D(LOG_TAG, "[MMServiceConnection::onServiceDisconnected] mStatusListener is null");
            
            mService = null;
        }
    };
    
    /**
     * Registers a callback to be invoked when background service has connection status change.
     *
     * @param listener The callback that will be run
     */
    public void setMMServiceStatusListener(onMMServiceStatusListener listener)
    {
        LOG.I(LOG_TAG, "[setMMServiceStatusListener] this = " + getThisHash() + ", listener = " + listener);
        mStatusListener = listener;
    }
    
    /**
     * Call this to bind to MM service.
     */
    public int connect()
    {
        LOG.I(LOG_TAG, "[connect] this = " + getThisHash() + " tid = " + android.os.Process.myTid());
        if (mContext == null)
        {
            LOG.W(LOG_TAG, "[connect] mContext is null, this = " + getThisHash());
            return RETURN_CODE_INVALID_ARGUMENT;
        }
        
        if (mStatusListener == null)
        {
            LOG.W(LOG_TAG, "[connect] mStatusListener == null");
            return RETURN_CODE_INVALID_ARGUMENT;
        }
        
        if ((mService != null) || (mState != STATE_DISCONNECTED))
        {
            LOG.W(LOG_TAG, "[connect] mService is " + mService + ", mState = " + mState);
            return RETURN_CODE_WRONG_STATE;
        }
        
        String myPackageName = autoBindMMService(mContext, mMMServiceConnection);
        // if bind failed, return package name will be null.
        if (!TextUtils.isEmpty(myPackageName))
        {
            mState = STATE_CONNECTING;
            boolean bindMMPSuccess = bindMMPService(mContext, mMMPServiceConnection, myPackageName);
            LOG.D(LOG_TAG, "[connect] bind MMP Service Success = " + bindMMPSuccess);
            return RETURN_CODE_OK;
        }
        return RETURN_CODE_BIND_SERVICE_FAILED;
    }
    
    private static String autoBindMMService(Context context, ServiceConnection connection)
    {
        for (String packageName : BIND_PACKAGE_LIST)
        {
            if (!TextUtils.isEmpty(packageName))
            {
                boolean bindServiceSuccess = bindMMService(context, connection, packageName);
                LOG.D(LOG_TAG, "[autoBindMMService] bind MM Service Success = " + bindServiceSuccess + ", PackageName = " + packageName);
                if (bindServiceSuccess)
                    return packageName;
            }
        }
        return null;
    }
    
    private static boolean bindMMService(Context context, ServiceConnection connection, String packageName)
    {
        if (context == null || connection == null || TextUtils.isEmpty(packageName))
        {
            LOG.W(LOG_TAG, "[bindMMService], input parameter is wrong.");
            return false;
        }
        boolean bindSuccess = false;
        Intent intent = new Intent(SRV_MM);
        intent.setPackage(packageName);
        bindSuccess = context.getApplicationContext().bindService(intent, connection, Service.BIND_AUTO_CREATE);
        return bindSuccess;
    }
    
    private static boolean bindMMPService(Context context, ServiceConnection connection, String packageName)
    {
        if (context == null || connection == null || TextUtils.isEmpty(packageName))
        {
            LOG.W(LOG_TAG, "[bindMMService], input parameter is wrong.");
            return false;
        }
        boolean bindSuccess = false;
        Intent intent = new Intent(MediaManagerStore.MonitorService);
        intent.setPackage(packageName);
        bindSuccess = context.getApplicationContext().bindService(intent, connection, Service.BIND_AUTO_CREATE);
        return bindSuccess;
    }
    /**
     * Call this to do MM service un-bind
     */
    public void disConnect()
    {
        LOG.I(LOG_TAG, "[disConnect], this = " + getThisHash());
        if (null != mService)
        {
            try
            {
                mService.clear(mStrUuid);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[disConnect] exception = " + e.getMessage());
                e.printStackTrace();
            }
            
            if (null != mContext)
            {
                mContext.getApplicationContext().unbindService(mMMServiceConnection);
                
                //Add try catch to protect exception if MonitorService has not be binded ready. 
                try
                {
                    mContext.getApplicationContext().unbindService(mMMPServiceConnection);
                }
                catch(Exception e)
                {
                   e.printStackTrace();
                }
                
                //Manual unbind service will not trigger system call onServiceDisconnected, so we need to trigger by ourself.
                mMMServiceConnection.onServiceDisconnected(null);
            }
            else
                LOG.W(LOG_TAG, "[disConnect] mContext is null, this = " + getThisHash());
        }
        else
            LOG.W(LOG_TAG, "[disConnect], mService is null, this = " + getThisHash());
        mService = null;
    }
    
    /**
     * request service to start grouping for retrieving collection list 
     * @return Task id
     */
    public int startGrouping(int sourceType, int mediaType, int level, Bundle extra)
    {
        int nTaskId = -1;
        if (null != mService)
        {
            try
            {
                nTaskId = mService.startGrouping(mStrUuid, sourceType, mediaType, level, extra);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[startGrouping], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            LOG.W(LOG_TAG, "[startGrouping], mService is null, this = " + getThisHash());

        LOG.I(LOG_TAG, "[startGrouping], this = " + getThisHash() + ", return taskId = " + nTaskId);
        return nTaskId;
    }
    
    /**
     * Cancel the grouping
     */
    public void stopGrouping()
    {
        LOG.I(LOG_TAG, "[stopGrouping], this = " + getThisHash());
        if (null != mService)
        {
            try
            {
                mService.stopGrouping(mStrUuid);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[stopGrouping], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            LOG.W(LOG_TAG, "[stopGrouping], mService is null, this = " + getThisHash());
    }
    
    /**
     * Get grouping status (Idle, Grouping, Grouped, GroupError)
     */
    public int getGroupingStatus()
    {
        int nRet = GROUP_STATE_IDLE;
        
        if (null != mService)
        {
            try
            {
                nRet = mService.getGroupingStatus(mStrUuid);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[getGroupingStatus], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            LOG.W(LOG_TAG, "[getGroupingStatus], mService is null, this = " + getThisHash());
        LOG.I(LOG_TAG, "[getGroupingStatus], this = " + getThisHash() + ", return status = " + nRet);
        return nRet;
    }
    
    /**
     * Set a listener to listen the expand result. (Sense65)
     */
    public void setExpandResultListener(onExpandResultListener l)
    {
        LOG.I(LOG_TAG, "[setCollectionExpandListener], this = " + getThisHash() + ", listener = " + l);
        mExpandResultListener = l;
    }
    
    /**
     * Set a listener to listen the search result. (Sense65)
     */
    public void setSearchResultListener(onSearchResultListener l)
    {
        LOG.I(LOG_TAG, "[setSearchCallbackListener], this = " + getThisHash() + ", listener = " + l);
        mSearchResultListener = l;
    }
    
    /**
     * Set a listener to listen the status change during grouping.
     */
    public void setGroupInfoChangeListener(onGroupInfoChangeListener l)
    {
        LOG.I(LOG_TAG, "[setGroupInfoChangeListener], this = " + getThisHash() + ", listener = " + l);
        mGroupInfoChangeListener = l;
    }
    
    /**
     * Set a listener to listen the update of collection name.
     */
    public void setDisplayNameUpdateListener(onDisplayNameUpdateListener l)
    {
        LOG.I(LOG_TAG, "[setDisplayNameUpdateListener], this = " + getThisHash() + ", listener = " + l);
        mDisplayNameChangeListener = l;
    }
    
    /**
     * Set a listener to listen the expand result. (Sense65)
     */
    public void setServiceStateListener(onServiceStateListener l)
    {
        LOG.I(LOG_TAG, "[setServiceStateListener], this = " + getThisHash() + ", listener = " + l);
        mServiceStateListener = l;
    }
    
    /**
     * Create a new collection object
     * @param sourceType Indicate its source type.
     * @param name Indicate its collection name.
     * @param level Indicate its collection level.
     * @param extra Reserve for future use.
     * @return The created collection object.
     */
    public Collection createCollection(int sourceType, String collectionType, String name, int level, Bundle extra)
    {
        Collection c = null;
        
        if (null != mService)
        {
            try
            {
                c = mService.createCollection(sourceType, collectionType, name, level, extra);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[createCollection], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            LOG.W(LOG_TAG, "[createCollection], mService is null, this = " + getThisHash());
        LOG.I(LOG_TAG, "[createCollection], return " + c);
        return c;
    }
    
    /**
     * Delete items from collection
     * @param target A target collection that you need to kill.
     * @param media MediaManager provider uri lists.
     * @param extra Reserve for future use.
     * @return How many items had been deleted.
     */
    public int deleteCollection(Collection target, long[] media, Bundle extra)
    {
        int nRet = -1;
        if (null != mService)
        {
            try
            {
                nRet = mService.deleteCollection(target, media, extra);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[deleteCollection], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            LOG.W(LOG_TAG, "[deleteCollection], mService is null, this = " + getThisHash());
        LOG.I(LOG_TAG, "[deleteCollection], return " + nRet);
        return nRet;
    }

    /**
     * Retrieve a collection object by specific parameters  
     * @param sourceType
     * @param collectionType
     * @param id Collection id
     * @param extra Reserve for future use.
     * @return 
     */
    public Collection getCollection(int sourceType, String collectionType, String id, Bundle extra) 
    {
        LOG.D(LOG_TAG, "getCollection");
        Collection c = null;
        if (mService != null)
        {
            try
            {
                c = mService.getCollection(sourceType, collectionType, id, extra);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[getCollection], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            LOG.W(LOG_TAG, "[getCollection], mService is null, this = " + getThisHash());
        LOG.I(LOG_TAG, "[getCollection], return " + c);
        return c;
    }
    
    /**
     * Hide a collection
     * @param collections
     * @param extra Reserve for future use.
     * @return How many items had been hided.
     */
    public int hideCollections(ArrayList<Collection> collections, Bundle extra)
    {
        LOG.D(LOG_TAG, "hideCollections");
        int nRet = -1;
        if (mService != null)
        {
            try
            {
                nRet = mService.hideCollections(collections, extra);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[hideCollections], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            LOG.W(LOG_TAG, "[hideCollections], mService is null, this = " + getThisHash());
        LOG.I(LOG_TAG, "[hideCollections], return " + nRet);
        return nRet;
    }
    
    /**
     *  Register MediaCollectionReceiver for monitor which intent action with {@link com.htc.lib1.MediaManager#ACTION_GROUP_UPDATE_COMPLETED}.
     *  If MediaCollectionReceiver receive the target intent will callback onCollectionChange to AP.
     *  Register timing: activity onCreate.
     *  @param l Listener {@link com.htc.lib1.MediaManager.onCollectionChangeListener}.
     */
    public void registerOnCollectionChangeListener(final MediaManager.onCollectionChangeListener l)
    {
        LOG.I(LOG_TAG, "[registerOnCollectionChangeListener], this = " + getThisHash() + ", listener =  " + l);
        if (mIntentMediaCollectionFilter == null)
        {
            mIntentMediaCollectionFilter = new IntentFilter();
            mIntentMediaCollectionFilter.addAction(ACTION_GROUP_UPDATE_COMPLETED);
        }
        
        mMediaCollectionReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (l != null)
                    l.onCollectionChange();
                else
                    LOG.W(LOG_TAG, "[mMediaCollectionReceiver:onReceive], onCollectionChangeListener is null");
            }
        };
        if (mContext != null)
        {
            mContext.getApplicationContext().registerReceiver(mMediaCollectionReceiver, mIntentMediaCollectionFilter, ACCESS_MM_PERMISSION, null);
        }
        else
        {
            LOG.W(LOG_TAG, "[registerOnCollectionChangeListener], context is null, can't not register Receiver");
        }
    }
    
    /**
     * Rename a collection
     * @param target
     * @param name
     * @param extra Reserve for future use.
     */
    public void renameCollection(Collection target, String name, Bundle extra)
    {
        LOG.I(LOG_TAG, "[renameCollection], target = " + target + ", name = " + name);
        if (mService != null)
        {
            try
            {
                mService.renameCollection(target, name, extra);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[renameCollection], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            LOG.W(LOG_TAG, "[renameCollection], mService is null, this = " + getThisHash());
    }
    
    /**
     * Show collections
     * @param collections
     * @param extra Reserve for future use.
     * @return
     */
    public int showCollections(ArrayList<Collection> collections, Bundle extra)
    {
        int nRet = -1;
        if (mService != null)
        {
            try
            {
                nRet = mService.showCollections(collections, extra);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[showCollections], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            LOG.W(LOG_TAG, "[showCollections], mService is null, this = " + getThisHash());
        LOG.I(LOG_TAG, "[showCollections], return " + nRet);
        return nRet;
    }
    
    /**
     *  Unregister MediaCollectionReceiver.
     *  Unregister timing: activity onDestroy.
     */
    public void unRegisterOnCollectionChangeListener()
    {
        LOG.I(LOG_TAG, "[unRegisterOnCollectionChangeListener], this = " + getThisHash());
        if (mContext != null)
        {
            mContext.getApplicationContext().unregisterReceiver(mMediaCollectionReceiver);
        }
        else
        {
            LOG.W(LOG_TAG, "[unRegisterOnCollectionChangeListener], context is null, can't not unregister Receiver");
        }
        mIntentMediaCollectionFilter = null;
        mMediaCollectionReceiver = null;
    }

    /**
     * Add items to a collection.
     * @param media
     * @param target
     * @param extra Reserve for future use.
     * @return How many items had been added.
     */
    public int addToCollection(long[] media, Collection target, Bundle extra)
    {
        int nRet = -1;
        if (null != mService)
        {
            try
            {
                nRet = mService.addToCollection(media, target, extra);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[addToCollection], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            LOG.W(LOG_TAG, "[addToCollection], mService is null, this = " + getThisHash());
        LOG.I(LOG_TAG, "[addToCollection], return " + nRet);
        return nRet;
    }
    
    /**
     * Remove items from collections.
     * @param media
     * @param target
     * @param extra Reserve for future use.
     * @return
     */
    public int removeFromCollection(long[] media, Collection target, Bundle extra)
    {
        int nRet = -1;
        if (null != mService)
        {
            try
            {
                nRet = mService.removeFromCollection(media, target, extra);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[removeFromCollection], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            LOG.W(LOG_TAG, "[removeFromCollection], mService is null, this = " + getThisHash());
        LOG.I(LOG_TAG, "[removeFromCollection], return " + nRet);
        return nRet;
    }
    
    /**
     * Query a collection id by media items id.
     * @param sourceType
     * @param id
     * @param extra Reserve for future use.
     * @return
     */
    public String[] queryCollectionID(int sourceType, int level, long[] id, Bundle extra)
    {
        String[] strings = null;
        if (null != mService)
        {
            try
            {
                strings = mService.queryCollectionID(
                        sourceType, 
                        level, 
                        id,
                        extra);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[queryCollectionID], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            LOG.W(LOG_TAG, "[queryCollectionID], mService is null, this = " + getThisHash());
        LOG.I(LOG_TAG, "[queryCollectionID], return " + strings);
        return strings;
    }
    
    /**
     * Enable/Disable grouping cache.
     * @param isEnable
     */
    public void enableCache(boolean isEnable)
    {
        mIsCacheEnable = isEnable;
        if (null != mService)
        {
            try
            {
                mService.enableCache(mStrUuid, isEnable);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[enableCache], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            LOG.W(LOG_TAG, "[enableCache], mService is null, this = " + getThisHash());
        LOG.I(LOG_TAG, "[enableCache], isEnable = " + isEnable + ", this = " + getThisHash());
    }
    
    /**
     * Check the MM service's life state.
     * @return
     */
    public boolean isServiceAlive()
    {
        boolean bRet = false;
        if (null != mService)
        {
            try
            {
                bRet =  mService.isServiceAlive();
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[isServiceAlive], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            LOG.W(LOG_TAG, "[isServiceAlive], mService is null, this = " + getThisHash());
        LOG.I(LOG_TAG, "[isServiceAlive], return = " + bRet + ", this = " + getThisHash());
        return bRet;
    }
    
    /**
     * Call this function to trigger collection name update.
     * @return
     */
    public boolean requestUpdateCollectionName(int sourceType, int level, Bundle extras)
    {
        LOG.D(LOG_TAG, "requestUpdateCollectionName");
        boolean bRet = false;
        if (null != mService)
        {
            try
            {
                bRet = mService.requestUpdateCollectionName(mStrUuid, sourceType, level,
                        extras);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[requestUpdateCollectionName], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            LOG.W(LOG_TAG, "[requestUpdateCollectionName], mService is null, this = " + getThisHash());
        LOG.I(LOG_TAG, "[requestUpdateCollectionName], return = " + bRet);
        return bRet;
    }
    
    /**
     * Duplicate the collection specific parameters from source to target.
     * @return
     */
    public static boolean cloneSources(Context context,  ArrayList<Uri> sourceUris, ArrayList<Uri> targetUris, Bundle extras)
    {
        LOG.D(LOG_TAG, "cloneSources");
        
        long lIn = System.currentTimeMillis();
        
        boolean bRet = false;
        
        if (sourceUris == null)
        {
            LOG.W(LOG_TAG, "cloneSources batch: sourceUris is invalid");
            return false;
        }

        if (targetUris == null)
        {
            LOG.W(LOG_TAG, "cloneSources batch : targetUris is invalid");
            return false;
        }

        if (sourceUris.size() == 0)
        {
            LOG.W(LOG_TAG, "cloneSources batch: No uri in sourceUris");
            return false;
        }
                    
        if (targetUris.size() == 0)
        {
            LOG.W(LOG_TAG, "cloneSources batch : No uri in targetUris");
            return false;
        }
        
        if (sourceUris.size() != targetUris.size())
        {
            LOG.W(LOG_TAG, "cloneSources batch : Uri counts not match between source and target");
            return false;
        }
        
        /* Compose src & dst mapping table */
        HashMap<String, String> idmap = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> idmaplist = new ArrayList<HashMap<String, String>>();
        idmaplist.add(idmap);
        
        for (int i = 0; i < sourceUris.size(); i++)
        {
            Uri src = sourceUris.get(i);
            List<String> lsrc = src.getPathSegments();
            
            Uri dst = targetUris.get(i);
            List<String> ldst = dst.getPathSegments();
            
            String srcId = lsrc.get(lsrc.size() - 1);
            String dstId = ldst.get(ldst.size() -1);
            
            if (TextUtils.isDigitsOnly(srcId) && TextUtils.isDigitsOnly(dstId))
            {
                idmap.put(srcId, dstId);
            }
            
            //separate by 999 rows
            if (idmap.size() >= 999)
            {
                LOG.D(LOG_TAG, "Create new hashmap for next 999 records");
                idmap = new HashMap<String, String>();
                idmaplist.add(idmap);
            }
        }
        
        boolean bIgnorevfoldercalbum = false;
        boolean bIgnorevfolderonly = false;
        boolean bIgnorecalbumonly = false;
        boolean bIgnoreimagetag = false;
        boolean bIgnorecloudtag = false;
        if (extras != null)
        {
            bIgnorevfoldercalbum = extras.getBoolean(KEY_BOOLEAN_EXCLUDE_VFOLDER_CALBUM, false);
            bIgnorevfolderonly = extras.getBoolean(KEY_BOOLEAN_EXCLUDE_VFOLDER_ONLY, false);
            bIgnorecalbumonly = extras.getBoolean(KEY_BOOLEAN_EXCLUDE_CALBUM_ONLY, false);
            bIgnoreimagetag = extras.getBoolean(KEY_BOOLEAN_EXCLUDE_IMAGETAG, false);
            bIgnorecloudtag = extras.getBoolean(KEY_BOOLEAN_EXCLUDE_CLOUDTAG, false);
        }
        
        ContentResolver cr = context.getContentResolver();
        Bundle result = null;
        if (null != cr)
        {
            result = cr.call(MediaManagerStore.Files.EXTERNAL_CONTENT_URI, MediaManagerStore.MMP_CALL_COMMAND_GET_MMPDB_VERSION, null, null);
        }
        
        int mmpVersion = MMP_DB_VERSION_DEFAULT;
        if (result != null)
        {
            mmpVersion  = result.getInt(MediaManagerStore.MMP_CALL_COMMAND_GET_MMPDB_VERSION);
            LOG.D(LOG_TAG, "cloneSources MMP DB Version is:" + mmpVersion);
        }
        
        for (HashMap<String, String> map : idmaplist)
        {
            if (map.size() > 0)
            {
                if (!bIgnorevfoldercalbum)
                {
                    cloneMMPAttributes(cr, map, bIgnorevfolderonly, bIgnorecalbumonly, mmpVersion);
                    bRet = true;
                }
                
                // TODO : Tag
                if (!bIgnoreimagetag)
                {
                    clonetagattributes(cr, map);
                    bRet = true;
                }

                cloneCityCaches(cr, map);
                clonePlaceCaches(cr, map);
                bRet = true;
                
                // CloudTag (New feature for CloudGallery, start from MM Version 1.6/MMP_DB Version 3)
                if (mmpVersion >= MMP_DB_VERSION_3 && !bIgnorecloudtag)
                {
                    cloneCloudTagAttributes(cr, map);
                    bRet = true;
                }
            }
        }
        
        if (bRet)
        {
            sendCollectionChangeBroadcast(context);
        }
        long lout =  System.currentTimeMillis() - lIn;
        LOG.D(LOG_TAG, "cloneSources consume " + lout + "ms for " + sourceUris.size() + " records");
        return bRet;
    }
    
    private static void clonetagattributes(ContentResolver cr, HashMap<String, String> idmap){
    	
    	Map<Long, String> LongkeyMap = new HashMap<Long, String>();
    	for(Map.Entry<String, String> entry : idmap.entrySet()) {
    		LongkeyMap.put(Long.valueOf(entry.getKey()), entry.getValue());
    	}
    	    	
    	ArrayList<Long> idlist = new ArrayList<Long>(LongkeyMap.keySet());
    	
    	StringBuilder stridlist = new StringBuilder();
		for (int i = 0; i < idlist.size(); i++) {
			if (i == idlist.size() - 1) {
				stridlist.append(idlist.get(i));
			} else {
				stridlist.append(idlist.get(i) + ",");
			}
		}
		
		ArrayList<Long> existidlist = new ArrayList<Long>();
    	Cursor cursor = null;
        try
        {
            String[] projection = new String[1];
            projection[0] = "Media_Provider_image_id";
            String selection = "Media_Provider_image_id" + " in (" + stridlist + ")";
            Uri URI_TGCP_IMGINFO_ADDRESS = Uri.parse("content://" + MediaManagerStore.AUTHORITY + "/" + "Image_tab_Table");
            cursor = cr.query(URI_TGCP_IMGINFO_ADDRESS, projection, selection, null, null);
            
            if (cursor != null) {
	            if (cursor.moveToFirst()) {    
	                do {
	                	existidlist.add(cursor.getLong(0));
	                } while (cursor.moveToNext());
	            }
	        }
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (cursor != null) cursor.close();
        }
        
        if(existidlist != null){
        if(existidlist.size() > 0){
        	
        Map<Long, String> existkeyMap = new HashMap<Long, String>();
        
        for(Long key : existidlist){
        	existkeyMap.put(key, LongkeyMap.get(key));
        }

        Map<Long, String> sortedMap = new TreeMap<Long, String>(existkeyMap);
        
        ArrayList<Long> cloneidlist = new ArrayList<Long>(sortedMap.keySet());
        
        StringBuilder strcloneidlist = new StringBuilder();
		for (int i = 0; i < cloneidlist.size(); i++) {
			if (i == cloneidlist.size() - 1) {
				strcloneidlist.append(cloneidlist.get(i));
			} else {
				strcloneidlist.append(cloneidlist.get(i) + ",");
			}
		}
		
		ArrayList<Long> tagidlist = new ArrayList<Long>();
        Cursor clonecursor = null;
        try
        {
            String[] projection = new String[1];
            projection[0] = "Tag_id";
            String selection = "Media_Provider_image_id" + " in (" + strcloneidlist + ")";
            Uri URI_TGCP_IMGINFO_ADDRESS = Uri.parse("content://" + MediaManagerStore.AUTHORITY + "/" + "Image_tab_Table");
            clonecursor = cr.query(URI_TGCP_IMGINFO_ADDRESS, projection, selection, null, "Media_Provider_image_id ASC");

            if (clonecursor != null) {
	            if (clonecursor.moveToFirst()) {    
	                do {
	                	tagidlist.add(clonecursor.getLong(0));
	                } while (clonecursor.moveToNext());
	            }
	        }
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (clonecursor != null) clonecursor.close();
        }
        
        ArrayList<String> targetidlist = new ArrayList<String>();
        
        for(Long key : cloneidlist){
        	targetidlist.add(sortedMap.get(key));
        }
     
        
        if(targetidlist != null && tagidlist != null){
        ContentValues[] cv_arr = new ContentValues[tagidlist.size()];
        
        for (int i = 0; i < tagidlist.size(); i++)
        {
                ContentValues tagcv = new ContentValues();
                tagcv.put("Media_Provider_image_id", targetidlist.get(i));
                tagcv.put("Tag_id", tagidlist.get(i));
                cv_arr[i] = tagcv;
        }
        Uri URI_TGCP_IMGINFO_ADDRESS = Uri.parse("content://" + MediaManagerStore.AUTHORITY + "/" + "Image_tab_Table");
        cr.bulkInsert(URI_TGCP_IMGINFO_ADDRESS, cv_arr);
        
        }
        }
      }
    }
    
    private static void cloneMMPAttributes(ContentResolver cr, HashMap<String, String> idmap, boolean excludevfolder, boolean excludecalbum, int version)
    {
        LOG.D(LOG_TAG, "cloneMMPAttributes map size = " + idmap.size());
        Cursor cursor = null;
        String where ="";
        
        for (int i = 0; i < idmap.size(); i++)
        {
            if (i == 0)
            {
                where += "_id IN (";
            }
            else
            {
                where += ",";
            }
            where += "?";
        }
        where += ")";
        
        LOG.D(LOG_TAG, "cloneMMPAttributes where = " + where);
        
        String[] args = idmap.keySet().toArray(new String[0]);
        
        try
        {
            // Range: MediaManager1.0, MediaManager1.1 (MMP version 1~2)
            String[] proj =
            {
                MediaManagerColumns._ID,
                MediaManagerColumns.V_FOLDER,
                MediaManagerColumns.C_ALBUM
            };
            // Range: MediaManager1.6 (MMP version 3~)
            String[] proj_cloud =
            {
                MediaManagerColumns._ID,
                MediaManagerColumns.V_FOLDER,
                MediaManagerColumns.C_ALBUM,
                MediaManagerColumns.DATE_USER
            };

            ArrayList<ContentProviderOperation> opList = new ArrayList<ContentProviderOperation>();
            if (version >= MMP_DB_VERSION_3)
            {
                cursor = cr.query(MediaManagerStore.Files.EXTERNAL_CONTENT_URI, proj_cloud, where, args, null);
            }
            else
            {
                cursor = cr.query(MediaManagerStore.Files.EXTERNAL_CONTENT_URI, proj, where, args, null);
            }
            while (cursor.moveToNext())
            {
                String srcId = String.valueOf(cursor.getLong(0));
                String dstId = idmap.get(srcId);
                
                if (dstId != null)
                {
                    ContentValues cv = new ContentValues();
                    String vfolder = cursor.getString(1);
                    String calbum = cursor.getString(2);
                    long dateuser = 0;
                    
                    boolean bHasValue = false;
                    if (null != vfolder && !excludevfolder)
                    {
                        cv.put("v_folder", vfolder);
                        bHasValue = true;
                    }
                    if (null != calbum && !excludecalbum)
                    {
                        cv.put("c_album", calbum);
                        bHasValue = true;
                    }
                    if (version >= MMP_DB_VERSION_3)
                    {
                        // MediaManager 1.6 Add new column need clone(MediaManagerColumns.DATE_USER).
                        dateuser = cursor.getLong(3);
                        if (dateuser > 0)
                        {
                            cv.put(MediaManagerColumns.DATE_USER, dateuser);
                            bHasValue = true;
                        }
                    }
                    
                    if (bHasValue)
                    {
                        Builder builder = ContentProviderOperation.newUpdate(MediaManagerStore.Files.EXTERNAL_CONTENT_URI);
                        builder.withSelection(MediaManagerStore.MediaManagerColumns._ID + " is " + dstId, null);
                        builder.withValues(cv);
                        try
                        {
                            opList.add(builder.build());
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
            
            if (opList.size() > 0)
            {
                cr.applyBatch(MediaManagerStore.AUTHORITY, opList);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
                cursor = null;
            }
        }
    }
    
    /**
     * Duplicate the collection specific parameters from source to target.
     * @return
     */
    public static boolean cloneSources(Context context, Uri sourceUri, ArrayList<Uri> targetUris, Bundle extras)
    {
        LOG.D(LOG_TAG, "cloneSources");
        Bundle retBundle = null;
        boolean nRet = false;
        
        if (null == extras)
        {
        	extras = new Bundle();
        }
        
        if (null != extras)
        {
        	extras.putParcelable(MediaManagerStore.MMP_CALL_COMMAND_CLONE_SOURCES_SOURCE_URI, sourceUri);
        	extras.putParcelableArrayList(MediaManagerStore.MMP_CALL_COMMAND_CLONE_SOURCES_DESTINATION_URI, targetUris);
        	ContentResolver cr = context.getContentResolver();
        	retBundle = cr.call(MediaManagerStore.Files.EXTERNAL_CONTENT_URI, MediaManagerStore.MMP_CALL_COMMAND_CLONE_SOURCES, null, extras);
        }
        
        if (null != retBundle) 
        	nRet = retBundle.getBoolean(MediaManagerStore.MMP_CALL_COMMAND_GET_CLONE_SOURCES_RESULT);

        return nRet;
    }
    
    /**
     * For APP's to change URI from MediaManagerProvider to MediaProvider and
     * indicate DB table by type.
     * 
     * @return MediaProvider URI.
     */
    public static Uri convertURI_MMPtoMP(Uri uri, int type, Bundle extras)
    {
        if (uri == null)
        {
            LOG.W(LOG_TAG, "[convertURI_MMPtoMP]: Convert fail!");
            return null;
        }

        // If fail to convert, just return the original one.
        Uri retUri = uri;
        String scheme = uri.getScheme();
        String authority = uri.getAuthority();
        String inputUri = uri.toString();
        if (null != inputUri)
        {
            LOG.I(LOG_TAG, "[convertURI_MMPtoMP] Input Uri: " + inputUri);
            if ((null != scheme && scheme.equals("content"))
                    && (null != authority && authority.equals("mediamanager")))
            {
                String id = uri.getLastPathSegment();

                if (null != id)
                {
                    Uri tmpUri;
                    if (type == TYPE_IMAGE)
                    {
                        tmpUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    }
                    else if (type == TYPE_VIDEO)
                    {
                        tmpUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    }
                    else
                    {
                        tmpUri = MediaStore.Files.getContentUri("external");
                    }

                    if (TextUtils.isDigitsOnly(id))
                    {
                        retUri = tmpUri.buildUpon().appendPath("" + id).build();
                    }
                    else
                    {
                        retUri = tmpUri;
                    }
                }
            }
        }
        LOG.I(LOG_TAG, "[convertURI_MMPtoMP] Output Uri: " + retUri.toString());
        return retUri;
    }

    /**
     * For APP's to change URI from MediaManagerProvider to MediaProvider.
     * @return MediaProvider URI.
     */
    public static Uri convertURI_MMPtoMP(Uri uri)
    {
        if (uri == null)
        {
            LOG.W(LOG_TAG, "[convertURI_MMPtoMP]: Convert fail!");
            return null;
        }
        Uri retUri = null;
        String scheme = uri.getScheme();
        String authority = uri.getAuthority();
        String inputUri = uri.toString();
        if (null != inputUri)
        {
            LOG.I(LOG_TAG, "[convertURI_MMPtoMP] Input Uri: " + inputUri);
            if ((null != scheme && scheme.equals("content")) && (null != authority && authority.equals("mediamanager")))
            {
                String convertUri = inputUri.replace("content://mediamanager/media/", "content://media/");
                LOG.I(LOG_TAG, "[convertURI_MMPtoMP] Uri after convert:" + convertUri);
                retUri = Uri.parse(convertUri);
            }
            else
            {
                LOG.I(LOG_TAG, "[convertURI_MMPtoMP] Input not MMP URI and return the original URI.");
                retUri = uri;
            }
        }
        return retUri;
    }

    /**
     * For APP's to change URI from MediaProvider to MediaManagerProvider.
     * @return MediaManagerProvider URI.
     */
    public static Uri convertURI_MPtoMMP(Uri uri)
    {
        if (uri == null)
        {
            LOG.W(LOG_TAG, "[convertURI_MPtoMMP]: Convert fail!");
            return null;
        }
        Uri retUri = null;
        String scheme = uri.getScheme();
        String authority = uri.getAuthority();
        String inputUri = uri.toString();
        if (null != inputUri)
        {
            LOG.I(LOG_TAG, "[convertURI_MPtoMMP] Input Uri: " + inputUri);
            if ((null != scheme && scheme.equals("content")) && (null != authority && authority.equals("media")))
            {
                String convertUri = inputUri.replace("content://media/", "content://mediamanager/media/");
                LOG.I(LOG_TAG, "[convertURI_MPtoMMP] Uri after convert:" + convertUri);
                retUri = Uri.parse(convertUri);
            }
            else
            {
                LOG.I(LOG_TAG, "[convertURI_MPtoMMP] Input not MP URI and return the original URI.");
                retUri = uri;
            }
        }
        return retUri;
    }

    private static Uri convertToMMP(Context context, Uri source)
    {
        if (null == source)
        {
            return null;
        }

        Uri retUri = null;
        String scheme = source.getScheme();
        if (null != scheme && scheme.equals("content"))
        {
            String authority = source.getAuthority();
            // MMP
            if (null != authority)
            {
                if (authority.equals("mediamanager"))
                {
                    LOG.D(LOG_TAG, "MMP mode");
                    retUri = source;
                }
                // MP
                else if (authority.equals("media")) 
                {
                    LOG.D(LOG_TAG, "MP mode");
                    // convertString_MPtoMMP
                    retUri = convertURI_MPtoMMP(source);
                }
            }
        }
        else 
        {
            // source.getScheme().equals("files")) or other scheme
            LOG.D(LOG_TAG, "File mode");
            String canonicalPath = source.getPath();
            try
            {
                File f = new File(canonicalPath);
                if (f.exists())
                {
                    canonicalPath = f.getCanonicalPath();
                }
                else
                {
                    LOG.I(LOG_TAG, "[convertToMMP] File is not exist");
                    return null;
                }
                retUri = convertURI_MPtoMMP(getContentUri(context, canonicalPath));
            }
            catch (Exception ex)
            {
                LOG.I(LOG_TAG, "[convertToMMP] failed;");
                return null;
            }
        }
        LOG.D(LOG_TAG, "[convertToMMP] MMPUri: " + retUri);
        return retUri;
    }
    
    private static class City {
        static final Uri CITY_CONTENT_URI = Uri.parse("content://" + MediaManagerStore.AUTHORITY + "/" + "location_city");

        static final String KEY_REAL_CITY = "real_city";
        static final String KEY_LOCALE_REAL_CITY = "locale_real_city";
        static final String KEY_CURRENT_LOCALE = "current_locale";
        static final String KEY_PHOTO_ID = "photo_id";
        static final String KEY_COUNTRY_CODE = "country_code";
        static final String KEY_LATITUDE = MediaManagerColumns.LATITUDE;
        static final String KEY_LONGITUDE = MediaManagerColumns.LONGITUDE;
        static final String KEY_SIZE = MediaManagerColumns.SIZE;
    }
    
    private static class Poi {
        static final Uri PLACE_CONTENT_URI = Uri.parse("content://" + MediaManagerStore.AUTHORITY + "/" + "location_place");

        static final String KEY_PLACE_NAME = "place_name";
        static final String KEY_PLACE_ID = "place_id";
        static final String KEY_CITY_ID = "city_id";
        static final String KEY_PHOTO_ID = "photo_id";
        static final String KEY_USER_LOCAL_ADDED = "user_local_added";
        static final String KEY_LATITUDE = MediaManagerColumns.LATITUDE;
        static final String KEY_LONGITUDE = MediaManagerColumns.LONGITUDE;
        static final String KEY_PHOTO_LATITUDE = "photo_" + MediaManagerColumns.LATITUDE;
        static final String KEY_PHOTO_LONGITUDE = "photo_" + MediaManagerColumns.LONGITUDE;
        static final String KEY_SIZE = MediaManagerColumns.SIZE;
   }

    private static void cloneCityCaches(ContentResolver cr, HashMap<String, String> idMap) {
        Cursor cursor = null;
        StringBuilder whereClause = new StringBuilder();
        
        for (int i = 0; i < idMap.size(); i++) {
            if (i == 0) {
                whereClause.append(City.KEY_PHOTO_ID).append(" IN (");
            } else
            {
                whereClause.append(",");
            }
            whereClause.append("?");
        }
        whereClause.append(")");
        
        LOG.D(LOG_TAG, "cloneCity where = " + whereClause.toString());
        
        String[] args = idMap.keySet().toArray(new String[idMap.size()]);
        
        String[] projection = { City.KEY_REAL_CITY,
                City.KEY_LOCALE_REAL_CITY,
                City.KEY_CURRENT_LOCALE,
                City.KEY_COUNTRY_CODE,
                City.KEY_LATITUDE,
                City.KEY_LONGITUDE,
                City.KEY_PHOTO_ID,
                City.KEY_SIZE
                };
        
        try {
            cursor = cr.query(City.CITY_CONTENT_URI, projection, whereClause.toString(), args, null);
            List<ContentValues> contentValues = new ArrayList<ContentValues>();
            if (cursor == null) {
                return;
            }
            if (!cursor.moveToFirst()) {
                cursor.close();
                cursor = null;
                return;
            }
            do {
                long oldPhotoId = cursor.getLong(cursor.getColumnIndex(City.KEY_PHOTO_ID));
                
                long newPhotoId = Long.parseLong(idMap.get(String.valueOf(oldPhotoId)));
                ContentValues value = new ContentValues();
                value.put(City.KEY_REAL_CITY, cursor.getString(cursor.getColumnIndex(City.KEY_REAL_CITY)));
                value.put(City.KEY_LOCALE_REAL_CITY, cursor.getString(cursor.getColumnIndex(City.KEY_LOCALE_REAL_CITY)));
                value.put(City.KEY_PHOTO_ID, newPhotoId);
                value.put(City.KEY_CURRENT_LOCALE, cursor.getString(cursor.getColumnIndex(City.KEY_CURRENT_LOCALE)));
                value.put(City.KEY_COUNTRY_CODE, cursor.getString(cursor.getColumnIndex(City.KEY_COUNTRY_CODE)));
                value.put(City.KEY_LATITUDE, cursor.getDouble(cursor.getColumnIndex(City.KEY_LATITUDE)));
                value.put(City.KEY_LONGITUDE, cursor.getDouble(cursor.getColumnIndex(City.KEY_LONGITUDE)));
                value.put(City.KEY_SIZE, cursor.getLong(cursor.getColumnIndex(City.KEY_SIZE)));
                contentValues.add(value);

            } while (cursor.moveToNext());
            cursor.close();
            cursor = null;
            ContentValues[] values = new ContentValues[contentValues.size()];
            values = contentValues.toArray(values);
            cr.bulkInsert(City.CITY_CONTENT_URI, values);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

    }
    
    private static void clonePlaceCaches(ContentResolver cr, HashMap<String, String> idMap) {
        Cursor cursor = null;
        StringBuilder whereClause = new StringBuilder();
        
        for (int i = 0; i < idMap.size(); i++) {
            if (i == 0) {
                whereClause.append(Poi.KEY_PHOTO_ID).append(" IN (");
            } else
            {
                whereClause.append(",");
            }
            whereClause.append("?");
        }
        whereClause.append(")");
        
        LOG.D(LOG_TAG, "clonePlace where = " + whereClause.toString());
        
        String[] args = idMap.keySet().toArray(new String[idMap.size()]);
        
        String[] projection = { Poi.KEY_PLACE_ID,
                Poi.KEY_CITY_ID,
                Poi.KEY_PLACE_NAME,
                Poi.KEY_USER_LOCAL_ADDED,
                Poi.KEY_LATITUDE,
                Poi.KEY_LONGITUDE,
                Poi.KEY_PHOTO_ID,
                Poi.KEY_PHOTO_LATITUDE,
                Poi.KEY_PHOTO_LONGITUDE,
                Poi.KEY_SIZE
                };
        
        try {
            cursor = cr.query(Poi.PLACE_CONTENT_URI, projection, whereClause.toString(), args, null);
            List<ContentValues> contentValues = new ArrayList<ContentValues>();
            if (cursor == null) {
                return;
            }
            if (!cursor.moveToFirst()) {
                cursor.close();
                cursor = null;
                return;
            }
            do {
                long oldPhotoId = cursor.getLong(cursor.getColumnIndex(Poi.KEY_PHOTO_ID));
                
                long newPhotoId = Long.parseLong(idMap.get(String.valueOf(oldPhotoId)));
                ContentValues value = new ContentValues();
                value.put(Poi.KEY_PLACE_ID, cursor.getString(cursor.getColumnIndex(Poi.KEY_PLACE_ID)));
                value.put(Poi.KEY_CITY_ID, cursor.getString(cursor.getColumnIndex(Poi.KEY_CITY_ID)));
                value.put(Poi.KEY_PHOTO_ID, newPhotoId);
                value.put(Poi.KEY_USER_LOCAL_ADDED, cursor.getInt(cursor.getColumnIndex(Poi.KEY_USER_LOCAL_ADDED)));
                value.put(Poi.KEY_PLACE_NAME, cursor.getString(cursor.getColumnIndex(Poi.KEY_PLACE_NAME)));
                value.put(Poi.KEY_LATITUDE, cursor.getDouble(cursor.getColumnIndex(Poi.KEY_LATITUDE)));
                value.put(Poi.KEY_LONGITUDE, cursor.getDouble(cursor.getColumnIndex(Poi.KEY_LONGITUDE)));
                value.put(Poi.KEY_PHOTO_LATITUDE, cursor.getDouble(cursor.getColumnIndex(Poi.KEY_PHOTO_LATITUDE)));
                value.put(Poi.KEY_PHOTO_LONGITUDE, cursor.getDouble(cursor.getColumnIndex(Poi.KEY_PHOTO_LONGITUDE)));
                value.put(Poi.KEY_SIZE, cursor.getLong(cursor.getColumnIndex(Poi.KEY_SIZE)));
                contentValues.add(value);

            } while (cursor.moveToNext());
            cursor.close();
            cursor = null;
            ContentValues[] values = new ContentValues[contentValues.size()];
            values = contentValues.toArray(values);
            cr.bulkInsert(Poi.PLACE_CONTENT_URI, values);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }
    
    private static Uri getContentUri(Context context, String filePath) 
    {
        Cursor cursor = null;
        try
        {
            cursor = context.getContentResolver().query(
                    MediaStore.Files.getContentUri("external"),
                    new String[] { MediaStore.MediaColumns._ID },
                    MediaStore.MediaColumns.DATA + "=? ",
                    new String[] { filePath }, null);
            if (cursor != null)
            {
                if (cursor.moveToFirst()) 
                {
                    int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                    //Uri baseUri = Uri.parse("content://media/external/files/");
                    Uri baseUri = MediaStore.Files.getContentUri("external");

                    return Uri.withAppendedPath(baseUri, "" + id);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (cursor != null) cursor.close();
        }

        return null;
    }

    /**
     * Clear all related sources(Timeline/Albums/Created Albums/Tag) information of target media files.
     * @return
     */
    public static void clearSources(Context context, ArrayList<Uri> targetUris, Bundle extras)
    {
        //Start a service to clear sources flag
        if (extras == null)
            extras = new Bundle();
        extras.putParcelableArrayList("targetUris", targetUris);
        context.getContentResolver().call(MediaManagerStore.Files.EXTERNAL_CONTENT_URI, "com.htc.providers.mediamanager.Intent.ACTION_CLEARSOURCES", null, extras);
    }
    
    
    /**
     * Wrap all parameters of Collection to Bundle object
     * @param collection
     * @param mediatype
     * @param extra 
     * @return
     */
    public Bundle exportCollectionToBundle(Collection collection, int mediatype, Bundle extra)
    {
        Bundle bundle = null;
        if (null != mService)
        {
            try
            {
                bundle = mService.exportCollectionToBundle(collection, mediatype, extra);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
        return bundle;
    }
    

    /**
     * Wrap all parameters of Collection to Bundle object
     * @param collection
     * @return
     */
    public Bundle exportCollectionToBundle(Collection collection)
    {
        Bundle bundle = null;
        if (null != mService)
        {
            try
            {
                bundle = mService.exportCollectionToBundle(collection, MediaManager.MEDIA_TYPE_ALL_MEDIA, null);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
        return bundle;
    }
    
   /**
     * Get a SQL where condition from specific collection object.
     * @param c
     * @param mediaType
     * @param extra
     * @return
     */
    public Bundle getWhereParameters(Collection c, int mediaType, Bundle extra)
    {
        Bundle bundle = null;
        if (null != mService)
        {
            try
            {
                bundle = mService.getWhereParameters(c, mediaType, extra);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
    	return bundle;
    }

    /**
      * Helper to rename path in MediaProvider & MediaManagerProvider.
      * @param context
      * @param oriPath
      * @param targetPath
      * @param keepColumns
      * MediaManager.renameFolder(mContext, "testFolder1", "testFolder2", new String[]{"column1", "column2", "column3", "column4"}); 
    */
    public static int renameFolder(Context context, String oriPath, String targetPath, String[] keepColumns)
    {
        ContentValues cv = new ContentValues();
        Uri renameUri = 
            MediaManagerStore.Files.EXTERNAL_CONTENT_URI.buildUpon().appendQueryParameter(MediaManagerStore.PARAM_RENAME_HELPER, targetPath).build();
        return context.getContentResolver().update(renameUri, cv, oriPath, keepColumns);
    }    

    /**
     * Call this function to insert a new record to MP & MMP. 
     * @param context
     * @param paths
     * @param mimeTypes
     * @param callback
     */
    public static void scanFile(Context context, String[] paths, String[] mimeTypes, MediaScannerConnection.OnScanCompletedListener callback)
    {
        ScanFileHolder proxy = new ScanFileHolder(context, callback);
        MediaScannerConnection.scanFile(context, paths, mimeTypes, proxy.mInternal);
    }
    
    private static class ScanFileHolder
    {
        final Context mContext;
        final MediaScannerConnection.OnScanCompletedListener ml;
        public ScanFileHolder(Context context, MediaScannerConnection.OnScanCompletedListener l)
        {
            mContext = context;
            ml = l;
        }
        
        final public MediaScannerConnection.OnScanCompletedListener mInternal = new MediaScannerConnection.OnScanCompletedListener()
        {
            @Override
            public void onScanCompleted(String path, Uri uri)
            {
                Uri retUri = uri;

                ContentResolver cr = mContext.getContentResolver();
                Cursor cursor = null;
                try
                {
                    cursor = cr.query(
                            MediaStore.Files.getContentUri("external"),
                            new String[] { MediaStore.MediaColumns._ID },
                            MediaStore.MediaColumns.DATA + "=? ",
                            new String[] { path }, null);
                    if (cursor != null)
                    {
                        if (cursor.moveToFirst()) 
                        {
                            int ids[] = new int[1];
                            ids[0]= cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                            int result = triggerMMPUpate(cr, ids);
                            if (result > 0)
                            {
                                retUri = convertToMMP(mContext, uri);
                            }
                        }
                    }
                }
                catch(Exception e)
                {
                    LOG.D(LOG_TAG, "[onScanCompleted] Query Exception");
                    e.printStackTrace();
                }
                finally
                {
                    if (cursor != null) cursor.close();
                }

                if (null != ml)
                {
                    //Callback client
                    ml.onScanCompleted(path, retUri);
                }
            }
        };
    };

    private static int triggerMMPUpate(ContentResolver cr, int[] ids)
    {
        StringBuilder whereClause = new StringBuilder();
        ArrayList<String> whereArgs = new ArrayList<String>(100);
        ContentValues values = new ContentValues();

        int count = 0;

        for (int id : ids)
        {
            if (whereClause.length() != 0)
            {
                whereClause.append(",");
            }
            whereClause.append("?");
            whereArgs.add("" + id);
            if (whereArgs.size() > 100)
            {
                count += update(cr, values, whereClause, whereArgs);
                whereClause.setLength(0);
                whereArgs.clear();
            }
        }

        count += update(cr, values, whereClause, whereArgs);
        whereClause.setLength(0);
        whereArgs.clear();

        LOG.I(LOG_TAG, "[triggerMMPUpate] MMP update" + count);

        return count;
    }

    private static int update(ContentResolver cr, ContentValues values, StringBuilder whereClause, ArrayList<String> whereArgs)
    {
        int size = whereArgs.size();
        int ret = 0;

        if (size > 0)
        {
            String[] foo = new String[size];
            foo = whereArgs.toArray(foo);
            ret = cr.update(MediaManagerStore.Files.EXTERNAL_CONTENT_URI.buildUpon().appendQueryParameter(MediaManagerStore.PARAM_TRIGGER_UPDATE, "1").build(), values, MediaStore.MediaColumns._ID + " IN (" + whereClause.toString() + ")", foo);
            // Log.i("@@@@@@@@@", "rows deleted: " + numrows);
        }

        return ret;
    }
    
    private static void sendCollectionChangeBroadcast(Context context)
    {
        try
        {
            Intent intent = new Intent(MediaManager.ACTION_GROUP_UPDATE_COMPLETED);
            context.sendBroadcast(intent, ACCESS_MM_PERMISSION);
            LOG.D(LOG_TAG, "[sendCollectionChangeBroadcast] finished");

        } catch (Exception e)
        {
            LOG.W(LOG_TAG, "[sendCollectionChangeBroadcast], Exception = " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Acquire the query string for filtering out images which reside in hided bucket
     * @param l An implementation of IHide
     * @param extra reserve for later use
     * @return True means MM module is available, need to wait the SQL string callback.
     *  False means that No MM module on the ROM, no need to acquire SQL string. 
     */
    public static boolean getAlbumHideSQLWhereString(Context c, onHideSQLWhereStringCallbackListener l, Bundle extra)
    {
        LOG.D(LOG_TAG, "getAlbumHideSQLWhereString");
        
        if (null == c)
            return false;
        
        if (null == l)
            return false;
        
        final Context context = c;
        final onHideSQLWhereStringCallbackListener listener = l;
        final Bundle bundle = extra;
        
        ServiceConnection connection = new ServiceConnection()
        {
            public void onServiceConnected(ComponentName name, IBinder binder)
            {
                LOG.D(LOG_TAG, "getAlbumHideSQLWhereString : onServiceConnected");
                final IMediaCollectionManager service = IMediaCollectionManager.Stub.asInterface(binder);
                GetHideSQLStringThread task = new GetHideSQLStringThread(
                        context, 
                        this, 
                        listener, 
                        service,
                        bundle);
                task.run();
            }
            @Override
            public void onServiceDisconnected(ComponentName name)
            {
                LOG.D(LOG_TAG, "getAlbumHideSQLWhereString : onServiceDisconnected");
            }
        };

        boolean bindServiceSuccess = false;
        try
        {
            String myPackageName = autoBindMMService(context, connection);
            bindServiceSuccess = !TextUtils.isEmpty(myPackageName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return bindServiceSuccess;
    }
    
    private static class GetHideSQLStringThread extends Thread
    {
        final Context mContext;
        final onHideSQLWhereStringCallbackListener ml;
        final IMediaCollectionManager mMM;
        final ServiceConnection mSC;
        final Bundle mExtra;
        public GetHideSQLStringThread(Context c, ServiceConnection sc, onHideSQLWhereStringCallbackListener l, IMediaCollectionManager MM, Bundle extra)
        {
            mContext = c;
            mSC = sc;
            ml = l;
            mMM = MM;
            mExtra = extra;
        }
        
        @Override
        public void run()
        {
            String str = null;
            try
            {
                str = mMM.getAlbumHideSQLWhereString(mExtra);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
            
            if (null != ml)
            {
                LOG.D(LOG_TAG, "GetHideSQLStringThread : SQL Hide string " + str);
                ml.onHideSQLWhereStringCallback(str);
            }
            mContext.getApplicationContext().unbindService(mSC);
        }
    }
    
    /**
     * Interface definition for a callback to be invoked when the hide SQL where string is ready.
     */
    public static interface onHideSQLWhereStringCallbackListener
    {
       /**
        * Called when the hide SQL where string is ready.
        */
       void onHideSQLWhereStringCallback(String string);
    }
    
    private int getThisHash()
    {
        return this.hashCode();
    }
    
    /**
     * Add items (uri) to a collection. (Sense65)
     * @param media
     * @param target
     * @param extra Reserve for future use.
     * @return How many items had been added.
     */
    public int addToCollection_Cloud(String[] docIdArray, Collection target, Bundle extra)
    {
        int nRet = -1;
        if (null != mService)
        {
            try
            {
                nRet = mService.addToCollection_Cloud(docIdArray, target, extra);
            }
            catch (Exception e)
            {
                LOG.W(LOG_TAG, "[addToCollection], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            LOG.W(LOG_TAG, "[addToCollection], mService is null, this = " + getThisHash());
        
        LOG.I(LOG_TAG, "[addToCollection], return " + nRet);
        return nRet;
    }
    
    /**
     * Remove items from collections. (Sense65)
     * @param media
     * @param target
     * @param extra Reserve for future use.
     * @return
     */
    public int removeFromCollection_Cloud(String[] docIdArray, Collection target, Bundle extra)
    {
        int nRet = -1;
        if (null != mService)
        {
            try
            {
                nRet = mService.removeFromCollection_Cloud(docIdArray, target, extra);
            }
            catch (Exception e)
            {
                LOG.W(LOG_TAG, "[removeFromCollection], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            LOG.W(LOG_TAG, "[removeFromCollection], mService is null, this = " + getThisHash());
        
        LOG.I(LOG_TAG, "[removeFromCollection], return " + nRet);
        return nRet;
    }
    
    /**
     * Get media list from a collection object. (Sense65)
     * @param c 
     * @param mediaType
     * @param extra
     * @return task id for interrupt
     */
    public int expand(Collection c, int mediaType, Bundle extra)
    {
        LOG.I(LOG_TAG, "[expand]");
        int nTaskId = -1;
        
        if (null != mService)
        {
            try
            {
                nTaskId = mService.expand(mStrUuid, c, mediaType, extra);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
        return nTaskId;
    }
   
    /**
     * Cancel the expand operation (Sense65)
     * @param ntaskId 
     * @param mediaType
     * @param extra
     * @return task id for interrupt
     */
    public void cancelExpand(Bundle extra)
    {
        LOG.I(LOG_TAG, "[cancelExpand]");
        if (null != mService)
        {
            try
            {
                mService.cancelExpand(mStrUuid, extra);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Search by keyword (Sense65)  
     * @param keyword 
     * @param extra
     * @return task id for interrupt
     */
    public int search(String keyword, Bundle extra)
    {
        LOG.I(LOG_TAG, "[search]");
        int nTaskId = -1;
        if (null != mService)
        {
            try
            {
                nTaskId = mService.search(mStrUuid, keyword, extra);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
        return nTaskId;
    }
    
    /**
     * Cancel the search operation (Sense65)  
     * @param keyword 
     * @param extra
     * @return task id for interrupt
     */
    public void cancelSearch(int taskId, Bundle extra)
    {
        LOG.I(LOG_TAG, "[cancelSearch]");
        if (null != mService)
        {
            try
            {
                mService.cancelSearch(mStrUuid, taskId, extra);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Set home location
     * @param latitude
     * @param longitude
     * @return success or fail
     */
    boolean setHome(double latitude, double longitude)
    {
        LOG.I(LOG_TAG, "[setHome] latitude = " + latitude + " Longitude = " + longitude);
        boolean bRet = false;
        
        if (null != mService)
        {
            try
            {
                mService.setHome(latitude, longitude);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
        return bRet;
    }
    
    /**
     * Tell MM service don't use home location for grouping
     */
    void resetHome()
    {
        if (null != mService)
        {
            try
            {
                mService.resetHome();
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Retrieve home latitude
     * @return  Latitude value of home 
     */
    double getHomeLatitude()
    {
        double d = 255.0d;
        
        if (null != mService)
        {
            try
            {
                d = mService.getHomeLatitude();
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
        return d;
    }
    
    /**
     * Retrieve home longitude
     * @return Longitude value of home
     */
    double getHomeLongitude()
    {
        double d = 255.0d;
        
        if (null != mService)
        {
            try
            {
                d = mService.getHomeLongitude();
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
        return d;
    }
    
    /**
     * Ask cloud server to acquire supported service types (A combine value which contains service type ex : FB, DropBox, Flickr ...)
     * @return Longitude value of home
     */
    int getSupportedServiceTypes()
    {
        int n = 0;
        if (null != mService)
        {
            try
            {
                n = mService.getSupportedServiceTypes();
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
        return n;
    }
    
    /**
     * Cancel the search operation (Sense65)  
     * @param serviceType: This is a bitwise combination of SERVICE_TYPE_ONLINE_DROPBOX, SERVICE_TYPE_ONLINE_FACEBOOK ...etc 
     * @param extra
     * @return ArrayList of Service Object
     */
    public ArrayList<ServiceObject> getServices(int serviceType, Bundle extras)
    {
        List<ServiceObject> list = null;
        if (null != mService)
        {
            try
            {
                list = mService.getServices(serviceType, extras);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
        if (list != null)
        {
            ArrayList<ServiceObject> alist = new ArrayList<ServiceObject>(list);
            return alist;
        }
        else
            return null;
    }

    /**
    * Set specify service to be enable or not.
    * Trigger notify onFiltered() across instances once filtered by given services.
    * @param serviceType :
    *      This is a bitwise combination of SERVICE_TYPE_ONLINE_DROPBOX, SERVICE_TYPE_ONLINE_FACEBOOK ...etc
    * @param filtered : 
    *      True : means disable service, False means enable service.
    */
    public void setServiceFiltered(int beShownServices, int beHidedServices, Bundle extras)
    {
        if (null != mService)
        {
            try
            {
                mService.setServiceFiltered(beShownServices, beHidedServices, extras);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 
     * @param collection The collection to be updated
     * @param year Year
     * @param month Month
     * @param day Day of month
     * @return if success, return true.
     */
    public boolean updateCollectionDateTime(Collection collection, int year, int month, int day, Bundle extras)
    {
    	
    	 if (null != mService)
         {
             try
             {
                 return mService.updateCollectionDateTime(collection, year, month, day, extras);
             }
             catch (RemoteException e)
             {
                 e.printStackTrace();
             }
         }
    	 
    	return false;
    }
    
    /**
     * 
     * @param mediaObjects The objects to be updated
     * @param year Year
     * @param month Month
     * @param day Day of month
     * @return if success, return true.
     */
    public boolean updateMediaObjectDateTime(MediaObject[] mediaObjects, int year, int month, int day, Bundle extras)
    {
    	 if (null != mService)
         {
             try
             {
                 return mService.updateMediaObjectDateTime(mediaObjects, year, month, day, extras);
             }
             catch (RemoteException e)
             {
                 e.printStackTrace();
             }
         }
    	 
    	return false;
    }

    /**
     * Get sort order for virtual album
     * @return String Array 
     */
    public String[] getVirtualAlbumSortList()
    {
    	if (null != mService)
    	{
    		try
    		{
    			return mService.getVirtualAlbumSortList();
    		}
    		catch (RemoteException e)
    		{
    			e.printStackTrace();
    		}
    	}

    	return null;
    }

    /**
     * Query the same hash1.2 or hash1.3 in cloud.db
     * @param MediaObject
     * @param bundle
     * @return Duplicated items from cloud services
     */
    public ArrayList<ServiceObject> getDupServices(MediaObject mediaObj, Bundle extras) {
    	List<ServiceObject> list = null;
    	if (null != mService)
    	{
    		try
    		{
    			list = mService.getDupServices(mediaObj, extras);
    		}
    		catch (RemoteException e)
    		{
    			e.printStackTrace();
    		}
    	}
    	
    	 if (list != null)
         {
             ArrayList<ServiceObject> alist = new ArrayList<ServiceObject>(list);
             return alist;
         }
    	
    	return null;
    }

    /**
     * Call it if the app is in the foreground. If connect to PhotoPlatform, it
	 * will sync cloud contents.
     * @return true if bind MMService and call connectPhotoPlatform successfully 
     */
    public boolean startPPSync() {
    	if (null != mService)
    	{
    		try
    		{
    			mService.connectPhotoPlatform(mStrUuid);
    			return true;
    		}
    		catch (RemoteException e)
    		{
    			e.printStackTrace();
    		}
    	}
    	
    	return false;
    }

    /**
     * Call it if the app is not in the foreground
     * Disconnect PhotoPlatform
     * @return true if bind MMService and call disConnectPhotoPlatform successfully  
     */
    public boolean stopPPSync() {
    	if (null != mService)
    	{
    		try
    		{
    			mService.disConnectPhotoPlatform(mStrUuid);
    			return true;
    		}
    		catch (RemoteException e)
    		{
    			e.printStackTrace();
    		}
    	}
    	
    	return false;
    }

    /**
     * Check PP service is alive
     * @return true if PP service is connected
     */
    public boolean isPPServiceConnected() {
    	boolean ret = false;
    	
    	if (null != mService)
    	{
    		try
    		{
    			ret = mService.isPPServiceConnected();
    		}
    		catch (RemoteException e)
    		{
    			e.printStackTrace();
    		}
    	}
    	
    	return ret;
    }
    
    /**
     * This is for Gallery to display all Tags info by given MediaObject
     * @param MediaObject
     * @param Bundle
     * @return All the tag info of duplicated copies
     */
    public ArrayList<CloudTagCollectionInfo> getMediaTags(MediaObject mo, Bundle extras)
    {
        List<CloudTagCollectionInfo> list = null;
        if (null != mService)
        {
            try
            {
                list = mService.getMediaTags(mo, extras);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[getMediaTags], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
        {
            LOG.W(LOG_TAG, "[getMediaTags], mService is null, this = " + getThisHash());
        }
        
         if (list != null)
         {
             ArrayList<CloudTagCollectionInfo> alist = new ArrayList<CloudTagCollectionInfo>(list);
             return alist;
         }
        
        return null;
    }
    
    /**
     * This is for Gallery to edit tag, the source collection should be updated to given name tag
     * @param source Collection
     * @param target tag name
     * @param Bundle
     * @return Update result
     */
    public boolean updateTagByCollection(Collection sourceCollection, String targetName, Bundle extras)
    {
        boolean bRet = false;
        if (null != mService)
        {
            try
            {
                bRet =  mService.updateTagByCollection(sourceCollection, targetName, extras);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[updateTagByCollection], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
        {
            LOG.W(LOG_TAG, "[updateTagByCollection], mService is null, this = " + getThisHash());
        }
        LOG.I(LOG_TAG, "[updateTagByCollection], return = " + bRet + ", this = " + getThisHash());
        return bRet;
    }
    
    /**
     * This is for Gallery to edit tag, the given medias should be updated to given name tag
     * @param given medias
     * @param target tag name
     * @param Bundle
     * @return Update result
     */
    public boolean updateTagByMediaObjects(MediaObject[] medias, String sourceName,String targetName, Bundle extras)
    {
        boolean bRet = false;
        if (null != mService)
        {
            try
            {
                bRet = mService.updateTagByMediaObjects(medias, sourceName, targetName, extras);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[updateTagByMediaObjects], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
        {
            LOG.W(LOG_TAG, "[updateTagByMediaObjects], mService is null, this = " + getThisHash());
        }
        LOG.I(LOG_TAG, "[updateTagByMediaObjects], return = " + bRet + ", this = " + getThisHash());
        return bRet;
    }
    
    /**
     * Get MediaObject List by docIds
     * @param docs
     * @return MeidaObject list
     */
    public ArrayList<MediaObject> getMediaObjectsByDocIds(String[] docs)
    {
    	List<MediaObject> list = null;
    	if (null != mService)
    	{
    		try
    		{
    			list = mService.getMediaObjectsByDocIds(docs);
    		}
    		catch (RemoteException e)
    		{
    			e.printStackTrace();
    		}
    	}
    	if (list != null)
    	{
    		ArrayList<MediaObject> alist = new ArrayList<MediaObject>(list);
    		return alist;
    	} else {
    		return null;
    	}
    }
    
    private static class CloudTag
    {
        static final String TABLE_NAME_CLOUD_TAG_CONTENT = "cloud_tag_content";
        static final Uri CloudTag_CONTENT_URI = Uri.parse("content://" + MediaManagerStore.AUTHORITY + "/" + TABLE_NAME_CLOUD_TAG_CONTENT);
        static final String KEY_MP_ID = "mp_id";
        static final String KEY_TAG_ID = "tag_id";
    }

    
    private static void cloneCloudTagAttributes(ContentResolver cr, HashMap<String, String> idmap)
    {
        LOG.D(LOG_TAG, "[cloneCloudTagAttributes] map size = " + idmap.size());
        Cursor cursor = null;
        String where ="";
        
        for (int i = 0; i < idmap.size(); i++)
        {
            if (i == 0)
            {
                where += CloudTag.KEY_MP_ID + " IN (";
            }
            else
            {
                where += ",";
            }
            where += "?";
        }
        where += ")";
        
        LOG.D(LOG_TAG, "[cloneCloudTagAttributes] where = " + where);
        
        String[] args = idmap.keySet().toArray(new String[idmap.size()]);

        String[] proj =
        {
            CloudTag.KEY_MP_ID,
            CloudTag.KEY_TAG_ID
        };

        try
        {
            cursor = cr.query(CloudTag.CloudTag_CONTENT_URI, proj, where, args, null);
            List<ContentValues> contentValues = new ArrayList<ContentValues>();
            if (cursor == null) {
                return;
            }
            if (!cursor.moveToFirst()) {
                cursor.close();
                cursor = null;
                return;
            }
            do {
                long oldPhotoId = cursor.getLong(cursor.getColumnIndex(CloudTag.KEY_MP_ID));
                
                long newPhotoId = Long.parseLong(idmap.get(String.valueOf(oldPhotoId)));
                ContentValues value = new ContentValues();
                value.put(CloudTag.KEY_MP_ID, newPhotoId);
                value.put(CloudTag.KEY_TAG_ID, cursor.getString(cursor.getColumnIndex(CloudTag.KEY_TAG_ID)));
                contentValues.add(value);

            } while (cursor.moveToNext());
            if (contentValues.size() != 0) 
            {
                ContentValues[] values = contentValues.toArray(new ContentValues[contentValues.size()]);
                cr.bulkInsert(CloudTag.CloudTag_CONTENT_URI, values);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
                cursor = null;
            }
        }
    }
    
    /**
     * Gen ID for expand task
     * @param Bundle
     * @return TaskID.
     */
    public int genTaskId(Bundle extras)
    {
        int iRet = ERROR_SERVICE_BIND_FAIL;
        if (null != mService)
        {
            try
            {
                iRet =  mService.genTaskId(extras);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[genTaskId], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
        {
            LOG.W(LOG_TAG, "[genTaskId], mService is null, this = " + getThisHash());
        }
        LOG.I(LOG_TAG, "[genTaskId], return = " + iRet + ", this = " + getThisHash());
        return iRet;
    }
    
    /**
     * Gen ID for expand task
     * @param MMP(MP) media id
     * @param Bundle
     * @return MediaObject
     */
    public MediaObject getMediaObjectById(long mediaId, Bundle extras)
    {
        MediaObject mo = null;
        if (null != mService)
        {
            try
            {
                mo =  mService.getMediaObjectById(mediaId, extras);
            }
            catch (RemoteException e)
            {
                LOG.W(LOG_TAG, "[getMediaObjectById], " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
        {
            LOG.W(LOG_TAG, "[getMediaObjectById], mService is null, this = " + getThisHash());
        }
        LOG.I(LOG_TAG, "[getMediaObjectById], return = " + mo + ", this = " + getThisHash());
        return mo;
    }

    //Android M new append >>>

    /**
     * Call the method to retrieve permissions that MM used but not be granted.
     * @param context application context
     *
     * @return permission string,  null means all permission has already be granted before.
     */
    public static String[] getLostPermissions(Context context) {
        if (Build.VERSION.SDK_INT < MediaManagerStore.SDK_VERSION_CODE_MARSHMALLOW)
            return null;

        ContentResolver cr = context.getContentResolver();
        Bundle bundle = null;
        if (null != cr) {
            try {
                bundle = cr.call(
                        MediaManagerStore.Files.EXTERNAL_CONTENT_URI,
                        MediaManagerStore.MMP_CALL_COMMAND_GET_LOST_PERMISSION,
                        null, null);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        if (bundle == null) return null;

        ArrayList<String> permissionLists =
                bundle.getStringArrayList(MediaManagerStore.MMP_CALL_COMMAND_GET_LOST_PERMISSION_RESULT);
        if (permissionLists != null && permissionLists.size() == 0)
            return null;

        return permissionLists.toArray(new String[permissionLists.size()]);
    }

    /**
     * Interface definition of a callback to be invoked to indicating the permissions granted result.
     */
    public interface onGrantPermissionListener {
        /**
         * Called when the permissions have been granted/denied.
         */
        void onGrantPermissionResult(String[] permission, int[] result);
    }

    /**
     * Interface definition of a callback to be invoked to indicating the status of show permission rationale.
     */
    public interface onShouldShowRequestPermissionRationaleListener {
        /**
         * Called when the should show permission rationale status is retrieved.
         */
        void onResult(boolean showRationale);
    }

    private static class InternalOnGrantPermissionListener extends IonGrantPermissionListener.Stub {
        private Context mContext;

        public InternalOnGrantPermissionListener(Context context) {
            mContext = context;
        }

        @Override
        public void onGrantPermissionResult(String[] permission, int[] result) throws RemoteException {
            LOG.W(LOG_TAG, "[InternalOnGrantPermissionListener::onGrantPermissionResult], result =" + result);
            onGrantPermissionListener listener = mGrantPermissionListener;
            try {
                if (null != listener) {
                    LOG.I(LOG_TAG, "[InternalOnGrantPermissionListener::onGrantPermissionResult], result = " + result);
                    listener.onGrantPermissionResult(permission, result);
                } else {
                    LOG.W(LOG_TAG, "[InternalOnGrantPermissionListener::onGrantPermissionResult], listener == null");
                }
                unbindService(mContext, mGrantPermissionServiceConnection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class InternalOnShouldShowRationaleListener extends IonShouldShowRationaleListener.Stub {
        private Context mContext;

        public InternalOnShouldShowRationaleListener(Context context) {
            mContext = context;
        }

        @Override
        public void onShouldShowRationaleResult(boolean result) throws RemoteException {
            LOG.W(LOG_TAG, "[InternalOnShouldShowRationaleListener::onShouldShowRationaleResult], result =" + result);
            onShouldShowRequestPermissionRationaleListener listener = mShouldShowRequestPermissionRationaleListener;

            try {
                if (null != listener) {
                    LOG.I(LOG_TAG, "[InternalOnShouldShowRationaleListener::onShouldShowRationaleResult], result = " + result);
                    listener.onResult(result);
                } else {
                    LOG.W(LOG_TAG, "[InternalOnShouldShowRationaleListener::onShouldShowRationaleResult], listener == null");
                }
                unbindService(mContext, mShouldShouldRationaleServiceConnection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static onGrantPermissionListener mGrantPermissionListener;
    private static onShouldShowRequestPermissionRationaleListener mShouldShowRequestPermissionRationaleListener;
    private static String mGrantPermissionStrUuid = UUID.randomUUID().toString();
    private static IMediaCollectionManager mGrantPermissionService;
    private static GPServiceConnection mGrantPermissionServiceConnection = new GPServiceConnection();
    private static IMediaCollectionManager mShouldShouldRationaleService;
    private static ShouldShouldRationaleServiceConnection mShouldShouldRationaleServiceConnection =
            new ShouldShouldRationaleServiceConnection();
    private static InternalOnGrantPermissionListener mInternalGrantPermissionListener;
    private static InternalOnShouldShowRationaleListener mInternalOnShouldShowRationaleListener;

    private static class GPServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            LOG.D(LOG_TAG, "[GPServiceConnection] onServiceConnected()");
            try {
                setGrantPermissionServiceObj(IMediaCollectionManager.Stub.asInterface(binder));
                getBindStatusObj().mbGPConnected = true;
            }
            catch (Exception e) {
                LOG.W(LOG_TAG, "[GPServiceConnection] Exception = " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LOG.D(LOG_TAG, "[GPServiceConnection] onServiceDisconnected()");
            getBindStatusObj().mbGPConnected = false;
            setGrantPermissionServiceObj(null);
        }
    }

    private static class ShouldShouldRationaleServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            LOG.D(LOG_TAG, "[ShouldShouldRationaleServiceConnection] onServiceConnected()");
            try {
                setShouldShouldRationaleServiceObj(IMediaCollectionManager.Stub.asInterface(binder));
                getBindStatusObj().mbSSRConnected = true;
            }
            catch (Exception e) {
                LOG.W(LOG_TAG, "[ShouldShouldRationaleServiceConnection] Exception = " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LOG.D(LOG_TAG, "[ShouldShouldRationaleServiceConnection] onServiceDisconnected()");
            getBindStatusObj().mbSSRConnected = false;
            setShouldShouldRationaleServiceObj(null);
        }
    }

    private static class BindStatus {
        boolean mbGPConnected = false;
        boolean mbSSRConnected = false;
    }
    private static BindStatus mBindStatus;
    private synchronized static BindStatus getBindStatusObj() {
        if (mBindStatus == null) {
            LOG.D(LOG_TAG, "[getBindStatusObj] new BindStatus()");
            mBindStatus = new BindStatus();
        }
        return mBindStatus;
    }

    private synchronized static void unbindService(Context context, ServiceConnection connection) {
        LOG.D(LOG_TAG, "[unbindService]");
        if (connection instanceof GPServiceConnection) {
            if (getBindStatusObj().mbGPConnected) {
                LOG.D(LOG_TAG, "[unbindService] GPServiceConnection");
                context.unbindService(connection);
                getBindStatusObj().mbGPConnected = false;
                //setGrantPermissionListener(null, null);
            }
        }
        else if (connection instanceof ShouldShouldRationaleServiceConnection) {
            if (getBindStatusObj().mbSSRConnected) {
                LOG.D(LOG_TAG, "[unbindService] ShouldShouldRationaleServiceConnection");
                context.unbindService(connection);
                getBindStatusObj().mbSSRConnected = false;
                //setShouldShowRationaleListener(null, null);
            }
        }

        if (getBindStatusObj().mbGPConnected == false && getBindStatusObj().mbSSRConnected == false)
            releaseBindStatusObj();
    }

    private synchronized static void releaseBindStatusObj() {
        LOG.D(LOG_TAG, "[releaseBindStatusObj]");
        mBindStatus = null;
    }

    private synchronized static IMediaCollectionManager getGrantPermissionServiceObj() {
        return mGrantPermissionService;
    }

    private synchronized static void setGrantPermissionServiceObj(IMediaCollectionManager mgr) {
        mGrantPermissionService = mgr;
    }

    private synchronized static IMediaCollectionManager getShouldShouldRationaleServiceObj() {
        return mShouldShouldRationaleService;
    }

    private synchronized static void setShouldShouldRationaleServiceObj(IMediaCollectionManager mgr) {
        mShouldShouldRationaleService = mgr;
    }

    private synchronized static void setGrantPermissionListener(
            InternalOnGrantPermissionListener internal,
            onGrantPermissionListener external
    ) {
        mInternalGrantPermissionListener = internal;
        mGrantPermissionListener = external;
    }

    private synchronized static void setShouldShowRationaleListener(
            InternalOnShouldShowRationaleListener internal,
            onShouldShowRequestPermissionRationaleListener external
    ) {
        mInternalOnShouldShowRationaleListener = internal;
        mShouldShowRequestPermissionRationaleListener = external;
    }

    /**
     * call the method to request MM grants permissions (please do not call this in main UI-thread)
     * @param context application context
     * @param permissions permissions needs to be granted
     * @param listener grant permission result callback listener
     *
     * @return true if bind service successfully and caller need to listen to result, otherwise return false.
     */
    public static boolean grantPermissions (
            Context context, String[] permissions, onGrantPermissionListener listener) {
        LOG.D(LOG_TAG, "[grantPermissions]");
        return grantPermissionsEx(context, permissions, listener, null);
    }


    /**
     * call the method to request MM grants permissions (please do not call this in main UI-thread)
     * @param context application context
     * @param permissions permissions needs to be granted
     * @param listener grant permission result callback listener
     * @param extras extra information
     *
     * @return true if bind service successfully and caller need to listen to result, otherwise return false.
     */
    public static boolean grantPermissionsEx (
            Context context, String[] permissions, onGrantPermissionListener listener, Bundle extras) {
        LOG.D(LOG_TAG, "[grantPermissionsEx]");
        if (permissions == null || permissions.length == 0)
            return false;

        if (listener == null)
            return false;

        if (Build.VERSION.SDK_INT < MediaManagerStore.SDK_VERSION_CODE_MARSHMALLOW)
            return false;

        if (mInternalGrantPermissionListener == null)
            mInternalGrantPermissionListener = new InternalOnGrantPermissionListener(context);
        mGrantPermissionListener = listener;
        setGrantPermissionListener(mInternalGrantPermissionListener, mGrantPermissionListener);

        if (!getBindStatusObj().mbGPConnected) {
            autoBindMMService(context, mGrantPermissionServiceConnection);

            int n = 0;
            while (n < 150) {
                if (getBindStatusObj() != null && getBindStatusObj().mbGPConnected) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
                }
                n++;
            }
        }

        if (getBindStatusObj() != null && getBindStatusObj().mbGPConnected) {
            try {
                getGrantPermissionServiceObj().setGrantPermissionListener(
                        mGrantPermissionStrUuid, mInternalGrantPermissionListener);
                getGrantPermissionServiceObj().grantPermissionsEx(permissions, extras);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }


    /**
     * Call the method to retrieve the status of should show request permission rationale.
     * @param context application context
     * @param listener show permission rationale status callback listener
     *
     * @return true if bind service successfully and caller need to listen to result, otherwise return false.
     */
    public static boolean shouldShowRequestPermissionRationale(
            Context context, onShouldShowRequestPermissionRationaleListener listener) {
        LOG.D(LOG_TAG, "[shouldShowRequestPermissionRationale]");
        if (listener == null)
            return false;

        if (Build.VERSION.SDK_INT < MediaManagerStore.SDK_VERSION_CODE_MARSHMALLOW)
            return false;

        if (mInternalOnShouldShowRationaleListener == null)
            mInternalOnShouldShowRationaleListener = new InternalOnShouldShowRationaleListener(context);
        setShouldShowRationaleListener(
                mInternalOnShouldShowRationaleListener, mShouldShowRequestPermissionRationaleListener);
        mShouldShowRequestPermissionRationaleListener = listener;

        if (!getBindStatusObj().mbSSRConnected) {
            autoBindMMService(context, mShouldShouldRationaleServiceConnection);

            int n = 0;
            while (n < 150) {
                if (getBindStatusObj() != null && getBindStatusObj().mbSSRConnected) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
                }
                n++;
            }
        }

        if (getBindStatusObj() != null && getBindStatusObj().mbSSRConnected) {
            try {
                getShouldShouldRationaleServiceObj().setShouldShowRationaleListener(
                        mGrantPermissionStrUuid, mInternalOnShouldShowRationaleListener);
                getShouldShouldRationaleServiceObj().shouldShowRequestPermissionRationale();
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    //Android M new append <<<
}
