package com.htc.lib2.weather;

import android.location.Location;

oneway interface ILocationCallback {
   void onLocationChanged(in Location location);
}