package com.htc.lib1.mediamanager.utils;

import android.util.Log;

/**
*  Hide Automatically by Hsiaowei Chuang
*  @hide
*/
public class GeoInfoHelper 
{
    public static final float INVALID_LATLNG = 255f;
    private static final float FACTOR_LATITUDE = 0.027f; // 0.09;
    
    public static final int EARTH_RADIUS_METERS = 6378137;
    public static final int LATITUDE_MAX = 90;
    public static final int LONGITUDE_MAX = 180;
    
    private static final float FACTOR_LONGITUDES[] = {
        0.027f, //0.090,     // Latitude, 0
        0.027f, //0.091,     // Latitude, 10
        0.029f, //0.096,     // Latitude, 20
        0.031f, //0.104,     // Latitude, 30
        0.035f, //0.117,     // Latitude, 40
        0.042f, //0.140,     // Latitude, 50
        0.054f, //0.180,     // Latitude, 60
        0.079f, //0.263,     // Latitude, 70
        0.155f, //0.518,     // Latitude, 80
    };
    
    public static boolean isValidateLatLng(float lat, float lng)
    {
        //define in com.google.android.gms.maps.model.LatLng:
        //Latitude, in degrees, This value is in the range [-90, 90]
        //Longitude, in degrees, This value is in the range [-180, 180]
        return !((lat < -90.0 || lat > 90.0) || (lng < -180.0 || lng > 180.0) || (lat == 0 && lng == 0));
    }
    
    public static float getMaxLatitude(float lat)
    {
        return lat + FACTOR_LATITUDE;
    }

    public static float getMaxLongitude(float lat, float lng)
    {
        int latV = (int)(Math.abs(lat) / 10);
        if (latV >= 8)
            latV = 8;
        float longitude = FACTOR_LONGITUDES[latV];
        return lng + longitude;
    }

    public static float getMinLatitude(float lat)
    {
        return lat - FACTOR_LATITUDE;
    }

    public static float getMinLongitude(float lat, float lng)
    {
        int latV = (int)(Math.abs(lat) / 10);
        if (latV >= 8)
            latV = 8;
        float longitude = FACTOR_LONGITUDES[latV];
        return lng - longitude;
    }
    
    public static Long genLocationKey(float lat, float lng)
    {
        return (long) (((lat + GeoInfoHelper.LATITUDE_MAX) * 2 * GeoInfoHelper.LATITUDE_MAX + (lng + GeoInfoHelper.LONGITUDE_MAX)) * GeoInfoHelper.EARTH_RADIUS_METERS);
    }
}
