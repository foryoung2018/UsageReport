package com.htc.lib0.media.zoe;

public class HtcZoeMetadata
{

 //htc data
    /**
     * The data type or metadata key to set / retrieve zoe photo.
     * <ul>
     * <li>The data type to add jpeg into Zoe file or extract jpeg from Zoe file.
     * <li>The metadata key to retrieve the information of photo in the Zoe file.
     * </ul>
     * <p>
     * <li><B>type</B> data : byte[]
     * <li><B>readonly</B> no
     */
    public static final String HTC_DATA_ZOE_JPEG = "ZJPG";
    
    /**
     * The data type to set/retrieve semi-video motion data. 
     * <p>
     * <li><B>type</B> data : byte[]
     * <li><B>readonly</B> no
     */
    public static final String HTC_DATA_SEMIVIDEO_MD = "SVMD";   
    
//htc metadata int
    /**
     * The metadata key to set / retrieve the index of Zoe cover photo.
     * <p>
     * <li><B>type</B> int
     * <li><B>readonly</B> no
     */
    public static final String HTC_METADATA_ZOE_COVER_INDEX = "ZCVR";

    /**
     * The metadata key to set / retrieve the index of Zoe shot photo.
     * <p>
     * <li><B>type</B> int
     * <li><B>readonly</B> no
     */
    public static final String HTC_METADATA_ZOE_SHOT_INDEX = "ZSHT";

    /**
     * The metadata key to set / retrieve the information of zoe jpeg’s width
     * <p>
     * <li><B>type</B> int
     * <li><B>readonly</B> no
     */
    public static final String HTC_METADATA_ZOE_PHOTO_WIDTH = "ZPTW";

    /**
     * The metadata key to set / retrieve the information of zoe jpeg’s height
     * <p>
     * <li><B>type</B> int
     * <li><B>readonly</B> no
     */
    public static final String HTC_METADATA_ZOE_PHOTO_HEIGHT = "ZPTH";

    /**
     * The metadata key to set / retrieve the information of Dual Lens.
     * <p>
     * <li><B>type</B> int
     * <li><B>readonly</B> no
     */
    public static final String HTC_METADATA_DUAL_LENS = "DLen";

    /**
     * If this HTC metadata key exists the media belongs to slow motion.
     * Get 1 from HtcZoeExtractor.extractHtcMetadataAsInt, if it is.
     * <p>
     * <li><B>type</B> int
     * <li><B>readonly</B> yes
     */
    public static final String HTC_METADATA_SLOW_MOTION = "SLMT";

    /**
     * The metadata key retrieve the information of which camera id is used to capture video.
     * Using HtcZoeExtractor.extractHtcMetadataAsInt to retrieve it.
     * <p>
     * <li><B>type</B> int
     * <li><B>readonly</B> no
     */
    public static final String HTC_METADATA_CAMERA_ID = "CamD";
    
    /**
     * The metadata key retrieve the information of source(SemiVideoWriter input) video bitrate.
     * Using HtcZoeExtractor.extractHtcMetadataAsInt to retrieve it.
     * <p>
     * <li><B>type</B> int
     * <li><B>readonly</B> no
     */
    public static final String HTC_METADATA_BITRATE = "BITR";
    
    /**
     * The metadata key retrieve the information of source(SemiVideoWriter input) video frame drop ratio.
     * Using HtcZoeExtractor.extractHtcMetadataAsInt to retrieve it.
     * <p>
     * <li><B>type</B> int
     * <li><B>readonly</B> no
     */
    public static final String HTC_METADATA_FRAME_DROP_RATIO = "FDRT";
//htc metadata string
    /**
     * The metadata key to set / retrieve the information of media taken time.
     * <p>
     * <li><B>type</B> String
     * <li><B>readonly</B> no
     */
    public static final String HTC_METADATA_MEDIA_TAKEN_TIME = "HMTT";

    /**
     * This HTC metadata key to retrieve the location information, if available.
     * Using HtcZoeExtractor.extractHtcMetadataAsString to retrieve it.
     * <p>
     * <li><B>type</B> String
     * <li><B>readonly</B> yes
     */
    public static final String HTC_METADATA_KEY_LOCATION = "KLOC";

    private static final String[] dataKey = {HTC_DATA_ZOE_JPEG,
    	HTC_DATA_SEMIVIDEO_MD};
    private static final String[] metaDataKeyForInt = {HTC_METADATA_ZOE_COVER_INDEX, HTC_METADATA_ZOE_SHOT_INDEX,
                                                       HTC_METADATA_ZOE_PHOTO_WIDTH, HTC_METADATA_ZOE_PHOTO_HEIGHT,
                                                       HTC_METADATA_DUAL_LENS, HTC_METADATA_SLOW_MOTION,
                                                       HTC_METADATA_CAMERA_ID, HTC_METADATA_BITRATE,
                                                       HTC_METADATA_FRAME_DROP_RATIO };

    private static final String[] metaDataKeyForString = {HTC_METADATA_MEDIA_TAKEN_TIME, HTC_METADATA_KEY_LOCATION};

    private static final String[] keyReadOnly = {HTC_METADATA_SLOW_MOTION, HTC_METADATA_KEY_LOCATION};


    /**
     * check the given key is a valid data key
     *
     * @param key the key to check
     * @return true, if the key is valid
     */
    public static boolean isDataKeyValid(String key){
        if(key == null || key.length() != 4)
            throw new IllegalArgumentException("key format is invalid");

        for ( int i = 0 ; i < dataKey.length ; i++) {
            if(dataKey[i].compareTo(key) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * check the given key is a valid int key
     *
     * @param key the key to check
     * @return true, if the key is valid
     */
    public static boolean isMetadataKeyValidForInt(String key) {
        if(key == null || key.length() != 4)
            throw new IllegalArgumentException("key format is invalid");

        for ( int i = 0 ; i < metaDataKeyForInt.length ; i++) {
            if(metaDataKeyForInt[i].compareTo(key) == 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * check the given key is a valid String key
     *
     * @param key the key to check
     * @return true, if the key is valid
     */
    public static boolean isMetadataKeyValidForString(String key) {
        if(key == null || key.length() != 4)
            throw new IllegalArgumentException("key format is invalid");

        for ( int i = 0 ; i < metaDataKeyForString.length ; i++) {
            if(metaDataKeyForString[i].compareTo(key) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * check the given key is read only
     *
     * @param key the key to check
     * @return true, if the key is read only
     */
    public static boolean isKeyReadOnly(String key){
        if(key == null || key.length() != 4)
            throw new IllegalArgumentException("key format is invalid");

        for ( int i = 0 ; i < keyReadOnly.length ; i++) {
             if(keyReadOnly[i].compareTo(key) == 0) {
                 return true;
             }
        }
        return false;
    }
}
