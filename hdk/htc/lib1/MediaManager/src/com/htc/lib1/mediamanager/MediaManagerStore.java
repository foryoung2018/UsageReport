package com.htc.lib1.mediamanager;

import android.net.Uri;
import android.provider.MediaStore;

/**
 * The Media Manager provider sync meta data from MediaProvider for image/video data on external storage devices.
 */
public final class MediaManagerStore {
    private final static String TAG = "MediaManagerStore";
    public static final String AUTHORITY = "mediamanager";

    /**
     * Service to raise MMP, AP should bind this service if it want to listen notifyChange from MMP
     * Bind Service
     * Intent intent = new Intent(MediaManagerStore.MonitorService);
     * this.getApplicationContext().bindService(intent,  mServiceConnection, Service.BIND_AUTO_CREATE);
     */
    public static final String MonitorService = "com.htc.mediamanager.MMPMonitorService";

    /**
     * Broadcast intent
     */
    public static final class Intent
    {
    	/**
    	 * Broadcast intent for MMP scan started.
    	 */
        public static final String ACTION_MMP_SCAN_STARTED = "com.htc.intent.action.MMP_SCAN_STARTED";
        /**
    	 * Broadcast intent for MMP scan finished.
    	 */
        public static final String ACTION_MMP_SCAN_FINISHED = "com.htc.intent.action.MMP_SCAN_FINISHED";
    }


    private static final String CONTENT_AUTHORITY_SLASH = "content://" + AUTHORITY + "/";

    /**
     * The parameter for trigger MMP update.
     */
    public static final String PARAM_TRIGGER_UPDATE = "TriggerMMPUpdate";

    /**
     * The parameter for Rename help.
     * MediaManager.renameFolder(mContext, "testFolder1", "testFolder2", new String[]{"column1", "column2", "column3", "column4"});
     */
    public static final String PARAM_RENAME_HELPER = "RenameHelper";
    
    /**
     * The parameter for Rename help. (for Zero use only)
     */
    public static final String PARAM_APPLYBATCH_LOCKDB = "Applybatch_lockDB";

    /**
     * The Uri to get scan state.
     * mContext.getContentResolver().query(MediaManagerStore.SCAN_STATE_URI, null, null, null, null);
     */
    public static final Uri SCAN_STATE_URI = Uri.parse("content://mediamanager/media/scan_state");

    /**
     * The Uri to get last shot.
     */
    public static final Uri LAST_SHOT_URI = Uri.parse("content://mediamanager/media/last_shot");
    
    /**
     * The command string for .call method.
     * Also it is the bundle key for the return bundle from .call.
     * Use the method to get MediaManager version.
     */
    public static final String MMP_CALL_COMMAND_GET_VERSION = "MMP_CALL_COMMAND_GET_VERSION";
    
    /**
     * The command string for .call method.
     * Use the method to pause hash computing.
     */
    public static final String MMP_CALL_COMMAND_PAUSE_HASH_COMPUTING = "MMP_CALL_COMMAND_PAUSE_HASH_COMPUTING";
    
    /**
     * The command string for .call method.
     * Use the method to resume hash computing.
     */
    public static final String MMP_CALL_COMMAND_RESUME_HASH_COMPUTING = "MMP_CALL_COMMAND_RESUME_HASH_COMPUTING";
    
    /**
     * The command string for .call method.
     * Use the method to compute target items's hash value then update DB.
     */
    public static final String MMP_CALL_COMMAND_COMPUTE_HASH = "MMP_CALL_COMMAND_COMPUTE_HASH";
    
    /**
     * The command string for .call method.
     * Use the method to start to calculate dedup hash code.
     */
    public static final String MMP_CALL_COMMAND_START_MISSED_DEDUP = "MMP_CALL_COMMAND_START_MISSED_DEDUP";
    
    /**
     * The command string for .call method.
     * Use the method to stop to calculate dedup hash code.
     */
    public static final String MMP_CALL_COMMAND_STOP_MISSED_DEDUP = "MMP_CALL_COMMAND_STOP_MISSED_DEDUP";
    
    /**
     * The arg string for .call method.
     * Use the arg to start/stop a user request dedup task.
     */
    public static final String MMP_CALL_COMMAND_PARAM_DEDUP_BY_USER = "MMP_CALL_COMMAND_PARAM_DEDUP_BY_USER";
    
    /**
     * The arg string for .call method.
     * Use the arg to start/stop a auto dedup task when gallery launched.
     */
    public static final String MMP_CALL_COMMAND_PARAM_DEDUP_BY_APP = "MMP_CALL_COMMAND_PARAM_DEDUP_BY_APP";
    
    /**
     * The command string for .call method.
     * Also it is the bundle key for the return bundle from .call.
     * Use the method to get MediaManagerProvider DB version.
     */
    public static final String MMP_CALL_COMMAND_GET_MMPDB_VERSION = "MMP_CALL_COMMAND_GET_MMPDB_VERSION";
    
    /**
     * The command string for .call method.
     * Use the method to duplicate the collection specific parameters from source to target.
     */
    public static final String MMP_CALL_COMMAND_CLONE_SOURCES = "MMP_CALL_COMMAND_CLONE_SOURCES";
    
    /**
     * The command string for .call method.
     * Also it is the bundle key for the return bundle from .call.
     * Use the method to get result of duplicate the collection specific parameters from source to target.
     */
    public static final String MMP_CALL_COMMAND_GET_CLONE_SOURCES_RESULT = "MMP_CALL_COMMAND_GET_CLONE_SOURCES_RESULT";

    /**
     * The command string for .call method.
     * Use the method to check all the dangerous permissions MediaManager need.
     */
    public static final String MMP_CALL_COMMAND_GET_LOST_PERMISSION = "MMP_CALL_COMMAND_GET_LOST_PERMISSION";

    /**
     * The command string for .call method.
     * Also it is the bundle key for the return bundle from .call.
     * Use the method to get result of all the permissions need user run time grant.
     */
    public static final String MMP_CALL_COMMAND_GET_LOST_PERMISSION_RESULT = "MMP_CALL_COMMAND_GET_LOST_PERMISSION_RESULT";
    
    /**
     * The bundle key to put and get the source Uri of CloneSource().
     */    
    public static final String MMP_CALL_COMMAND_CLONE_SOURCES_SOURCE_URI = "MMP_CALL_COMMAND_CLONE_SOURCES_SOURCE_URI";
    
    /**
     * The bundle key to put and get the destination ArrayList<Uri> of CloneSource().
     */        
    public static final String MMP_CALL_COMMAND_CLONE_SOURCES_DESTINATION_URI = "MMP_CALL_COMMAND_CLONE_SOURCES_DESTINATION_URI";

    /**
     * The extra parameter to indicate MediaManger should show setting dialog or not
     */
    public static final String PARAM_PERMISSION_SHOW_SETTING_DIALOG = "PARAM_PERMISSION_SHOW_SETTING_DIALOG";
    
    /**
     * The command for .call method.
     * Also it is the bundle key for the input & return bundle.
     * input: ArrayList<MediaObject>, 
     * output: ArrayList<Uri>
     * Use the command to retriever Thumbnail Uri, MUST put desired width/height in input bundle also.
     */
    public static final String MMP_CALL_COMMAND_RETRIEVE_THUMBNAIL_PATH = "MMP_CALL_COMMAND_RETRIEVE_THUMBNAIL_PATH";
    
    /**
    * The bundle key to assign desired width for call method command 
    * @see {@link #MMP_CALL_COMMAND_RETRIEVE_THUMBNAIL_PATH}
    */   
    public static final String MMP_KEY_INT_DESIRE_THUMBNAIL_WIDTH = "MMP_KEY_INT_DESIRE_THUMBNAIL_WIDTH";
    
    /**
    * The bundle key to assign desired height for call method command 
    * @see {@link #MMP_CALL_COMMAND_RETRIEVE_THUMBNAIL_PATH}
    */
    public static final String MMP_KEY_INT_DESIRE_THUMBNAIL_HEIGHT = "MMP_KEY_INT_DESIRE_THUMBNAIL_HEIGHT";

    /**
     *  M comes after L. (Constant Value: 23 (0x00000017))
     */
    public static final int SDK_VERSION_CODE_MARSHMALLOW = 23;

    /**
     * The Files parameter of MediaManagerStore.
     */
    public static final class Files {
    	/**
    	 * File URI of MediaManagerStore.
    	 */
        public static final Uri EXTERNAL_CONTENT_URI =
            Uri.parse(MediaStore.Files.getContentUri("external").toString().replace("content://", CONTENT_AUTHORITY_SLASH));
    }

    /**
     * The Images parameter of MediaManagerStore.
     */
    public static final class Images {
    	/**
    	 * Image URI of MediaManagerStore.
    	 */
        public static final Uri EXTERNAL_CONTENT_URI =
            Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString().replace("content://", CONTENT_AUTHORITY_SLASH));
    }

    /**
     * The Video parameter of MediaManagerStore.
     */
    public static final class Video {
    	/**
    	 * Video URI of MediaManagerStore.
    	 */
        public static final Uri EXTERNAL_CONTENT_URI =
            Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString().replace("content://", CONTENT_AUTHORITY_SLASH));
    }
    
    /**
     * String value of the columns of MMP media table
     */
    public interface MediaManagerColumns {

        /**
         * Column string of media_type
         */
        public static final String MEDIA_TYPE = MediaStore.Files.FileColumns.MEDIA_TYPE;
        /**
         * The value of media type
         */
        public static final int MEDIA_TYPE_NONE = MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;
        /**
         * The value of media type
         */
        public static final int MEDIA_TYPE_IMAGE = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
        /**
         * The value of media type
         */
        public static final int MEDIA_TYPE_AUDIO = MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;
        /**
         * The value of media type
         */
        public static final int MEDIA_TYPE_VIDEO = MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        /**
         * The value of media type
         */
        public static final int MEDIA_TYPE_PLAYLIST = MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST;
        
        /**
         * Column string of _id
         */
        public static final String _ID = MediaStore.Files.FileColumns._ID;
        /**
         * Column string of _data
         */
        public static final String DATA = MediaStore.Files.FileColumns.DATA;
        /**
         * Column string of _size
         */
        public static final String SIZE = MediaStore.Files.FileColumns.SIZE;
        /**
         * Column string of title
         */
        public static final String TITLE = MediaStore.Files.FileColumns.TITLE;
        /**
         * Column string of _display_name
         */
        public static final String DISPLAY_NAME = MediaStore.Files.FileColumns.DISPLAY_NAME;        
        /**
         * Column string of date_added
         */
        public static final String DATE_ADDED = MediaStore.Files.FileColumns.DATE_ADDED;
        /**
         * Column string of date_modified
         */
        public static final String DATE_MODIFIED = MediaStore.Files.FileColumns.DATE_MODIFIED;
        /**
         * Column string of mime_type
         */
        public static final String MIME_TYPE = MediaStore.Files.FileColumns.MIME_TYPE;
        /**
         * Column string of width
         */
        public static final String WIDTH = MediaStore.Files.FileColumns.WIDTH;
        /**
         * Column string of height
         */
        public static final String HEIGHT = MediaStore.Files.FileColumns.HEIGHT;
        /**
         * Column string of orientation
         */
        public static final String ORIENTATION = MediaStore.Images.ImageColumns.ORIENTATION;
        /**
         * Column string of bucket_id
         */
        public static final String BUCKET_ID = MediaStore.Images.ImageColumns.BUCKET_ID;
        /**
         * Column string of bucket_display_name
         */
        public static final String BUCKET_DISPLAY_NAME = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
        /**
         * Column string of datetaken
         */
        public static final String DATE_TAKEN = MediaStore.Images.ImageColumns.DATE_TAKEN;
        /**
         * Column string of latitude
         */
        public static final String LATITUDE = MediaStore.Images.ImageColumns.LATITUDE;
        /**
         * Column string of longitude
         */
        public static final String LONGITUDE = MediaStore.Images.ImageColumns.LONGITUDE;
        
        /**
         * Column string of duration
         */
        public static final String DURATION = MediaStore.Video.VideoColumns.DURATION;
        
        /**
         * Column string of is_drm
         */
        public static final String IS_DRM = "is_drm";
        /**
         * Column string of v_folder
         */
        public static final String V_FOLDER = "v_folder";
        /**
         * Column string of c_album
         */
        public static final String C_ALBUM = "c_album";
        /**
         * Column string of favorite
         */
        public static final String FAVORITE = "favorite";
        /**
         * Column string of htc_filter
         */
        public static final String HTC_FILTER = "htc_filter";

        /**
         * Column string of htc_type
         */
        public static final String HTC_TYPE = "htc_type";
        
        /**
         * Column string of deduplicate_hash1
         */
        public static final String DEDUPLICATE_HASH1 = "deduplicate_hash1";
        
        /**
         * Column string of deduplicate_hash2
         */
        public static final String DEDUPLICATE_HASH2 = "deduplicate_hash2";
        
        /**
         * Column string of deduplicate_hash3
         */
        public static final String DEDUPLICATE_HASH3 = "deduplicate_hash3";
        
        /**
         * Column string of whiteboard_value
         */
        public static final String WHITEBOARD_VALUE = "whiteboard_value";
        
        /**
         * Column string of date_user 
         */
        public static final String DATE_USER = "date_user";
        /**
         * A value of htc_type
         */
        public static final long HTC_TYPE_ZOE_FULL_CONTENT_MP4 = 1;
        /**
         * A value of htc_type
         */
        public static final long HTC_TYPE_ZOE_SHOT_PHOTO = 2;
        /**
         * A value of htc_type
         */
        public static final long HTC_TYPE_SLOW_MOTION_MP4 = 4;
        /**
         * A value of htc_type
         */
        public static final long HTC_TYPE_PANORAMA_PHOTO = 8;
        /**
         * A value of htc_type
         */
        public static final long HTC_TYPE_ZOE_TRIMMED_FULL_CONTENT_MP4 = 16;
        /**
         * A value of htc_type            
         */
        public static final long HTC_TYPE_DUAL_LENS_CONTENT = 32;
        /**
         * A value of htc_type
         */
        public static final long HTC_TYPE_MACRO_3D = 64;
        /**
         * A value of htc_type
         */
        public static final long HTC_TYPE_DUAL_LENS_U_FOCUS = 128;
        /**
         * A value of htc_type (This dual lens picture has depth map inside.)
         */
        public static final long HTC_TYPE_DUAL_LENS_HAS_DEPTH_MAP = 256;
        /**
         * A value of htc_type (This picture has human face inside.)
         */
        public static final long HTC_TYPE_HAS_FACE = 512;
        /**
         * A value of htc_type (This dual lens picture has done the operation of depth map processed & face detection.)
         */
        public static final long HTC_TYPE_DUAL_LENS_PROCESSED = 1024;
        
        /**
         * A value of htc_type (Auto capture burst)
         */
        public static final long HTC_TYPE_AUTO_CAPTURE_BURST = 2048;
        
        /**
         * A value of htc_type (Auto capture cover)
         */
        public static final long HTC_TYPE_AUTO_CAPTURE_COVER = 4096;
        
        /**
         * A value of htc_type (Capture by front camera)
         */
        public static final long HTC_TYPE_CAPTURE_BY_FRONT_CAM = 8192;
        
        /**
         * A value of htc_type (A flag to identify that target photo had been enhanced by photo lab)
         */
        public static final long HTC_TYPE_PHOTOLAB_ENHANCE = 16384;
        
        /**
         * A value of htc_type (A flag to identify that target photo hasn't been enhanced by photo lab)
         */
        public static final long HTC_TYPE_PHOTOLAB_ORIGINAL = 32768;
        
        /**
         * A value of htc_type (A generic flag to identify that the content is presented as a cover image)
         */
        public static final long HTC_TYPE_GENERIC_COVER = 65536;
        
        /**
         * A value of htc_type (A generic flag to identify that the content belong to a group)
         */
        public static final long HTC_TYPE_GENERIC_GROUP = 131072;
        
        /**
         * A value of htc_type (A flag to identify that target photo is a photo lab content)
         */
        public static final long HTC_TYPE_PHOTOLAB_CONTENT = 262144;
        
        /**
         * A value of htc_type (A flag to identify that target photo has a existed RAW file in same folder)
         */
        public static final long HTC_TYPE_RAW_FILE_EXISTED = 1<<19;
        
        /**
         * A value of htc_type (A flag to identify that target photo is Bokeh photos 2.0)
         */
        public static final long HTC_TYPE_BOKEHPLUS = 1<<20;
        
        /**
         * A value of htc_type (A flag to identify that target video is Hyperlapse Semi-video)
         */
        public static final long HTC_TYPE_SEMIVIDEO = 1<<21;

        /**
         * A value of htc_type (A flag to identify that target video is Surround sound video)
         */
        public static final long HTC_TYPE_SURROUND_SOUND_VIDEO = 1<<22;

        /**
         * A value of htc_type (A flag to identify that target photo is Bokeh photo)
         */

        public static final long HTC_TYPE_BOKEH = 1<<23;
    }
    
    /**
     * CloudProvider's contract
     * @author da_lin
     *
     */
    public static class MediaManagerCloudContract {
    	public static final String AUTHORITY = "mediamanager";
        
        /**
    	 * Table Cloud
    	 */
    	static public class Files {
    		public static String TAG = "mediamanagerstore$mediamanagercloudcontract$files";
    		private static Uri sUri = Uri.parse("content://" + AUTHORITY + "/cloud/" + TAG);
    		public static final Uri getContentUri() {
    			return sUri;
    		}

    		static public interface FileColumns{
    			// Order is the same as PP
    			String _ID = "_id";
    			String _DOCID = "_docid";
    			String TITLE = "title";
    			String DOC_NAME = "doc_name";
    			String SERVICE_TYPE = "service_type";
    			String MIME_TYPE = "mime_type";
    			String DATA = "_data";
    			String WIDTH = "width";
    			String HEIGHT = "height";
    			String THUMBNAILS = "thumbnails";
    			String DATE_TIME = "datetime";
    			String IS_LOCALTIME = "is_localtime";
    			String LONGITUDE = "longitude";
    			String LATITUDE = "latitude";
    			String FILE_SIZE = "file_size";
    			String DURATION = "duration";
    			String DEDUPLICATE_HASH1 = "deduplicate_hash1";
    			String DEDUPLICATE_HASH2 = "deduplicate_hash2";
    			String DEDUPLICATE_HASH3 = "deduplicate_hash3";
    			String WHITEBOARD_VALUE  = "whiteboard_value";
    			/*
    			 * Sync back to cloud
    			 */
    			String DATE_USER = "date_user";
    			String POI = "poi";
    			
    			// Extras
    			String DUPLICATE = "duplicate";
    			String DISPLAY_NAME = "_display_name";
    			String MEDIA_TYPE = "media_type";
    			String C_ALBUM = "c_album";
    			String V_FOLDER = "v_folder";
    			String FAVORITE = "favorite";
    			String HTC_TYPE = "htc_type";
    			String HTC_FILTER = "htc_filter";
    		}
    	}
    }
}
