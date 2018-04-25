package com.htc.lib2.opensense.facedetect;

import java.util.concurrent.Semaphore;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;

/**
 * face detection entry
 * 
 * @hide
 */
public class Entry {
	public Uri uri;
	public String path;
	public Resources res;
	public int res_id;
	public Bitmap bm;
	public Callback callback;
	public int[] box;
	public int[] outSize = {0,0};
	public long t, tt;
	public int id;
	public Semaphore semaphore;
	public Entry(Uri uri) {
		this.uri = uri;
	}
	public Entry(String path) {
		this.path = path;
	}
	public Entry(Resources res,int res_id) {
		this.res = res;
		this.res_id = res_id;
	}
	public Entry(Bitmap bm) {
		this.bm = bm;
	}
}
