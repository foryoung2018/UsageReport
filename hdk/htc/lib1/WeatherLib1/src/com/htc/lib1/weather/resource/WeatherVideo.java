package com.htc.lib1.weather.resource;

import android.util.Log;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

import java.io.File;


/**
 * WeatherVideo
 * @author htc MASD_U59000
 */
public class WeatherVideo {
    
    private static boolean DEBUG = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;
    private static final String LOG_TAG = "WeatherVideo";
    private static final String PREFIX_PATH = "/data/preload/weather_";
    private static final String PREFIX_PATH_LAND = "/data/preload/weather_l_";
    // path for Sense 5.5 and previous version
    private static final String PREFIX_PATH_OLD = "/system/media/weather/weather_";
    private static final String PREFIX_PATH_LAND_OLD = "/system/media/weather/weather_l_";
    
    // Video Path
    private static final String path[] = { 
        "sunny.mp4", // 0
        "clear.mp4", // 1

        "thunderstorm_day.mp4", // 2
        "thunderstorm_night.mp4", // 3

        "cloudy_day.mp4", // 4
        "cloudy_night.mp4", // 5

        "fog_day.mp4", // 6
        "fog_night.mp4", // 7

        "hot.mp4", // 8

        "partly_cloud_day.mp4", // 9
        "partly_sunny.mp4", // 10

        "rain_day.mp4", // 11
        "rain_night.mp4", // 12

        "snow_day.mp4", // 13
        "snow_night.mp4", // 14

        "windy_day.mp4", // 15
        "windy_night.mp4", // 16

        "partly_cloud_night.mp4" // 17
    };

    /** Still Image Resource */
    public final static String IMAGE_MAPPING_VIDEO[] = { 
        "weather_sunny",
        "weather_clear",
        
        "weather_thunderstorm_day",
        "weather_thunderstorm_night",
        
        "weather_cloudy_day",
        "weather_cloudy_night",
        
        "weather_fog_day",
        "weather_fog_night",
        
        "weather_hot",
        
        "weather_partly_cloud", 
        "weather_partly_sunny",
        
        "weather_rain_day", 
        "weather_rain_night",
        
        "weather_snow_day", 
        "weather_snow_night",
        
        "weather_windy_day", 
        "weather_windy_night",
        
        "weather_partly_cloud_night"
    };

    /**
     * Weather Video Mapping Array
     */
    public static final int mWeatherVideoMapArray[] = { 
        0,  0, 10,  9,  0,  4,  4,
        4,  0,  0,  6, 11, 11, 11, 
        2,  2,  2, 11, 13, 13, 13, 
       13, 13, 13, 13, 11,  0,  0, 
       11,  8,  1, 15,  1,  1, 17, 
       17,  7,  5, 12, 12,  3,  3, 
       14, 14,  0,  0,  0,  0,  0,
        0,  3,  6,  6,  3 };

    // Default Weather Condition & Data - For Wallpaper
    /**
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     */
    private final static int DEFAULT_CONDITION = 1;
    /** DEFAULT_VIDEO_INDEX */
    private final static int DEFAULT_VIDEO_INDEX = 0;
    /** DEFAULT_VIDEO_PATH */
    private final static String DEFAULT_VIDEO_PATH = path[0];

    /** For Broadcast Intent - MediaPlayer Release */
    private final static String ACTION_MEDIA_RELEASE = "com.htc.ml.VST_MEDIA_RELEASE";
    /** RESOURCE_PACKAGE_NAME */
    private final static String RESOURCE_PACKAGE_NAME = "com.htc.weathervideo.base";

    /**
     * get Weather Image Path
     * @param order 
     * @return WeatherImage Path
     */
    public static String getWeatherImagePath(int order) {
        if (order < 0 || order >= IMAGE_MAPPING_VIDEO.length) {
            return "";
        } else {
            return IMAGE_MAPPING_VIDEO[order];
        }
    }
    /**
     * get Video Count
     * @return number of Video
     */
    public static int getVideoCount() {
        return path.length;
    }

    /**
     * get Video File Path
     * @param order order
     * @return Video File Path
     */
    public static String getVideoFilePath(int order) {
        if (order < 0 || order > (path.length - 1))
            order = 0;

        return getFilePath(path[order], true);
    }

    /**
     * get Video File Path By Icon
     * @param iconId condition id
     * @param daylightflag true:day, false:night:
     * @return Video File Path By Icon
     */
    public static String getVideoFilePathByIcon(String iconId,
            boolean daylightflag) {
        int in = 0;
        try {
            in = Integer.parseInt(iconId);
        } catch (NumberFormatException e) {
            if(DEBUG) Log.w(LOG_TAG, "cant parse iconId:" + iconId);
        }
        return getVideoFilePathByIcon(in, daylightflag, true);
    }
    
    /**
     * get Video File Path By Icon
     * 
     * @param iconId condition id
     * @param daylightflag true:day, false:night:
     * @param isPortrait true:portrait, false:landscape:
     * @return Video File Path By Icon
     */
    public static String getVideoFilePathByIcon(int iconId,
            boolean daylightflag, boolean isPortrait) {
        int index = getVideoFileIndexByIcon(iconId, daylightflag);
        return getVideoFilePathByIndex(index, isPortrait);
    }
    
    /**
     * Get Video File Index By Icon
     * @param iconId condition id
     * @param daylightflag true:day, false:night:
     * @return Video File Path Index By Icon
     */
    public static int getVideoFileIndexByIcon(int iconId, boolean daylightflag) {
        int index = -1;
        iconId --;
        if (iconId >= 0 && iconId < WeatherVideo.mWeatherVideoMapArray.length) {
            index = WeatherVideo.mWeatherVideoMapArray[iconId];
        }
        if (!daylightflag) {
            switch (index) {
            case 2:
                // weather_thunderstorm_day.mp4
                index = 3;
                break;
            case 4:
                // weather_cloudy_day.mp4
                index = 5;
                break;
            case 6:
                // weather_fog_day.mp4
                index = 7;
                break;
            case 11:
                // weather_rain.mp4
                index = 12;
                break;
            case 13:
                // weather_snow_day.mp4
                index = 14;
                break;
            case 15:
                // weather_windy_day.mp4
                index = 16;
                break;
            default:
                break;
            }
        }
        if(DEBUG) Log.v(LOG_TAG, "getVideoFileIndexByIcon - " + index);

        return index;
    }

    /**
     * get Video File Path By Index
     * 
     * @param index order
     * @param isPortrait true:portrait, false:landscape
     * @return Video File Path
     */
    public static String getVideoFilePathByIndex(int index, boolean isPortrait) {
        // error handling for incorrect index
        if (index < 0 || index > (path.length - 1)) {
            index = 0;
        }

        return getFilePath(path[index], isPortrait);
    }

    private static String getFilePath(String subpath, boolean isPortrait) {
        try {
        if (isPortrait) {
                if ((new File(PREFIX_PATH + subpath).exists())) {
                    return PREFIX_PATH + subpath;
                } else if ((new File(PREFIX_PATH_OLD + subpath).exists())) {
                    return PREFIX_PATH_OLD + subpath;
                }

        } else {
                if ((new File(PREFIX_PATH_LAND + subpath).exists())) {
                    return PREFIX_PATH_LAND + subpath;
                } else if ((new File(PREFIX_PATH_LAND_OLD + subpath).exists())) {
                    return PREFIX_PATH_LAND_OLD + subpath;
                }

            }
        } catch (Exception e) {
            if (DEBUG)
                Log.w(LOG_TAG, "fail to check video file path", e);
        }

        // fall to default path
        if (isPortrait) {
            return PREFIX_PATH + subpath;
        } else {
            return PREFIX_PATH_LAND + subpath;
        }
    }
}

