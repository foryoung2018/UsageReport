package com.htc.test;
import android.graphics.Bitmap;

interface IFBSnapshotService {

    Bitmap takeDefaultSnapShotByBitmap();
    Bitmap takeSnapShotByBitmap(int minLayer, int maxLayer);
    String saveDefaultSnapShot(String name);
    String saveSnapShot(String name, int minLayer, int maxLayer);
	/* add compare View */
    Map compareBitmapEqualBefore(String name);
}
