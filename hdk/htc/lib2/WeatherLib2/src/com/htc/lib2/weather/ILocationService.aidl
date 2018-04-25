package com.htc.lib2.weather;

import android.location.Location;
import com.htc.lib2.weather.ILocationCallback;
import android.app.PendingIntent;
import android.location.Criteria;

interface ILocationService {
   Location getLastKnownLocation();
   Location getLastKnownLocationByProvider(String provider);
   Location requestSingleUpdate();
   List<Bundle> getWifiScanResults(int level);
   void requestLocationUpdate(ILocationCallback cb);
   void requestLocationUpdateDetail(ILocationCallback cb, long minTime, float minDistance, in Criteria criteria);
   void removeLocationUpdate(ILocationCallback cb);
   void removeLocationUpdateDetail(ILocationCallback cb);
   void requestLocationUpdateByCriteria(long minTime, float minDistance, in Criteria criteria, in PendingIntent intent);
   void requestLocationUpdateByProvider(String provider, long minTime, float minDistance, in PendingIntent intent);
}