package com.htc.lib2.opensense.facedetect;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Semaphore;

import javax.crypto.Cipher;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.htc.lib2.opensense.cache.StorageManager;
import com.htc.lib2.opensense.internal.SystemWrapper.HtcBuildFlag;
import com.htc.lib2.opensense.internal.SystemWrapper.SystemProperties;

/**
 * @author WJ_Lee
 * 
 * @hide
 */
public abstract class FaceDetectTask {
	static final int nFDMinValue = HtcBuildFlag.Htc_DEBUG_flag ? SystemProperties.getInt("profile.fdeng", 20) : 20;
	static final int nFDMinValue_Denominator = HtcBuildFlag.Htc_DEBUG_flag ? SystemProperties.getInt("profile.fdeng.factor", 100) : 100;
	static boolean DEBUG_Performance = (FDLog.DEBUG ||(FDLog.bDebugOpen > 0));

	private String mEncryptionKey = null;

	// API
    public static int[] face_detect(String path) {
       	FaceDetectTask task = fd_task_next();
    	if(task == null)return(null);
    	return(task.fd_wait(path));
    }
    public static int[] face_detect(String path,int[] outSize) {
       	FaceDetectTask task = fd_task_next();
    	if(task == null)return(null);
    	return(task.fd_wait(path, outSize));
    }
    public static int[] face_detect(Uri uri) {
       	FaceDetectTask task = fd_task_next();
    	if(task == null)return(null);
    	return(task.fd_wait(uri));
    }
    public static int[] face_detect(Uri uri,int[] outSize) {
       	FaceDetectTask task = fd_task_next();
    	if(task == null)return(null);
    	return(task.fd_wait(uri, outSize));
    }
    public static int[] face_detect(Bitmap bm) {
        FaceDetectTask task = fd_task_next();
        if(task == null)return(null);
        return(task.fd_wait(bm));
    }
    public static int[] face_detect(Bitmap bm,int[] outSize) {
        FaceDetectTask task = fd_task_next();
        if(task == null)return(null);
        return(task.fd_wait(bm, outSize));
    }
    public static int face_detect(String path,Callback callback) {
    	FaceDetectTask task = fd_task_next();
    	if(task == null)return(-1);
    	return(task.fd_async(path, callback));
    }
    public static int face_detect(Bitmap bm,Callback callback) {
    	FaceDetectTask task = fd_task_next();
    	if(task == null)return(-1);
    	return(task.fd_async(bm, callback));
    }
    //
    public static final String LOG_TAG = "FaceDetectTask";
	public static final String TAG = "FaceDetectTask";
	public static final int MAX_ENTRY = 100;
	public static final int MAX_TASK = 10;
	public static FaceDetectTask fd_task[] = new FaceDetectTask[MAX_TASK];
	public static int fd_task_n = 0;
	public static int fd_task_i = 0;
	private static int fd_task_seq = 0;
	public static void fd_task_add(FaceDetectTask task) {
		synchronized(fd_task){
			task.seq = ++fd_task_seq;
		    fd_task[fd_task_n++] = task;
		    //Log.d(TAG, "fd_task_add(" + task.seq + ") total=" + fd_task_n);
		}
	}
	public static void fd_task_del(FaceDetectTask task) {
		synchronized(fd_task){
			int i;
			for(i = 0; i < fd_task_n; i++)if(fd_task[i] == task)break;
			if(i == fd_task_n)return;
			fd_task_n--;
			while(i < fd_task_n){
				fd_task[i] = fd_task[i + 1];
				i++;
			}
			//Log.d(TAG, "fd_task_del(" + task.seq + ") total=" + fd_task_n);
		}
	}
	public static FaceDetectTask fd_task_next() {
		synchronized(fd_task){
			
			if(fd_task_n <= 0) {
				FaceDetectTask task = new_task(0);
				if(task != null) {
					return task;
				} else {					
					return(new_task_google(0));
				}
			}
			return(fd_task[0]);
			/*
			if(fd_task_i >= fd_task_n)fd_task_i = 0;
			FaceDetectTask task = fd_task[fd_task_i++];
			if(fd_task_i >= fd_task_n)fd_task_i = 0;
			return(task);
			*/
		}
	}
	public static void fd_task_stop_all() {
		while(fd_task_n > 0)fd_task[0].stop();
	}
	
	/**
	 * static member to check Omron solution is enabled, this value will update when class init.
	 */
	public static boolean IsOmronEnable = false;  
	
	
	// first check when class init
	static {
		Log.d(LOG_TAG, "sOmronFaceDetectTaskClass : " + FaceDetectTask.sOmronFaceDetectTaskClass);

		// check morpho and omron exist 
		IsOmronEnable = checkIsOmronEnable();
		Log.d(LOG_TAG, "IsOmronEnable : " + IsOmronEnable);
		Log.d(LOG_TAG, "sOmronFaceDetectTaskClass : " + FaceDetectTask.sOmronFaceDetectTaskClass);
	}
	
	private static Class<?> sOmronFaceDetectTaskClass = null;
	private static Class<?> sPhotoEffectClass = null;
	
	// check morpho is enable
	private static boolean checkIsOmronEnable () {	
		Log.d(LOG_TAG, "checkIsOmronEnable start");

// for unknown problem, we can't use this method to detect .so, we should assume the .so file must exist in HTC device if the jar exist.
/*		try {
			Log.d(LOG_TAG, "try to load native library");
			System.loadLibrary("morpho_image_converter");
			System.loadLibrary("morpho_memory_allocator");
			System.loadLibrary("jni_fe");			
		} catch (UnsatisfiedLinkError e) {
			Log.w(LOG_TAG,"can't load library" ,e);
			return false;
		} catch (Exception e){
			Log.w(LOG_TAG,"can't load library" ,e);
			return false;
		} */

		try {
			// check class exist
			Class<?> testClass = null;
			testClass = Class.forName("com.morpho.lib.utils.NativeMemoryAllocator");
			if(testClass == null) {
				return false;
			}
			
			testClass = Class.forName("com.morpho.lib.utils.graphics.ImageConverter");
			if(testClass == null) {
				return false;
			}
			
			sPhotoEffectClass = Class.forName("com.htc.PhotoEffect.PhotoEffect");
			if(sPhotoEffectClass == null) {
				return false;
			}			
			
			sOmronFaceDetectTaskClass = Class.forName("com.htc.lib2.opensense.facedetect.OmronFaceDetectTask");
			Log.d(LOG_TAG, "sOmronFaceDetectTaskClass : " + sOmronFaceDetectTaskClass);
			if(sOmronFaceDetectTaskClass == null || sOmronFaceDetectTaskClass.getSuperclass() != FaceDetectTask.class) {
				return false;
			}
			
			return true;
			
		} catch (ClassNotFoundException e) {
			Log.w(LOG_TAG, "class not found", e);
		}
		Log.d(LOG_TAG, "checkIsOmronEnable end");

		return false;
	}
	
	
    public static FaceDetectTask new_task(int max) {
    	synchronized(fd_task){
    	if(fd_task_n >= MAX_TASK)return(null);
    	if((max <= 0) || (max > MAX_ENTRY))max = MAX_ENTRY;
    	//FaceDetectTask task = new FaceDetectTask(max);
    	Constructor<?> constructor;
    	FaceDetectTask task = null;
		try {
			Log.d(LOG_TAG, "sOmronFaceDetectTaskClass : " + sOmronFaceDetectTaskClass);
			if(sOmronFaceDetectTaskClass == null)
			{
				try {
					sOmronFaceDetectTaskClass = Class.forName("com.htc.lib2.opensense.facedetect.OmronFaceDetectTask");
				} catch (ClassNotFoundException e) {
					Log.w(LOG_TAG, "class not found", e);
				}								
			}
			if(sOmronFaceDetectTaskClass == null || sOmronFaceDetectTaskClass.getSuperclass() != FaceDetectTask.class) {
				return null;
			}
			constructor = sOmronFaceDetectTaskClass.getConstructor(int.class);
			task = (FaceDetectTask)constructor.newInstance(max);
		} catch (NoSuchMethodException e) {
			Log.w(LOG_TAG, "method not found", e);			
			return null;
		} catch (InstantiationException e) {
			Log.w(LOG_TAG, "new instance fail", e);			
			return null;
		} catch (IllegalAccessException e) {
			Log.w(LOG_TAG, "IllegalAccessException", e);
			return null;
		} catch (IllegalArgumentException e) {
			Log.w(LOG_TAG, "IllegalArgumentException", e);
			return null;
		} catch (InvocationTargetException e) {
			Log.w(LOG_TAG, "invoke fail", e);			
			return null;
		}
    	
		if(task != null) {
			fd_task_add(task);	
		}
        return(task);
    }
    }
    public static FaceDetectTask new_task_google(int max) {
    	synchronized(fd_task){
    	if(fd_task_n >= MAX_TASK)return(null);
    	if((max <= 0) || (max > MAX_ENTRY))max = MAX_ENTRY;
    	FaceDetectTask task = new GoogleFaceDetectTask(max);
    	fd_task_add(task);
    	task.use_google_face_detect = true;
        return(task);
    }
    }
    static int task_N = 0;
    protected FaceDetectTask(int max) {
    	entry = new Entry[max];
    	head = tail = size = 0;
    	this.max = max;
    	semaphore = new Semaphore(0, true);
    	isRunning = true;
    	new Thread(task_run, "FaceDetectTask_thread:" + ++task_N).start();
    }
    public boolean use_google_face_detect = false;
    int max, size, head, tail;
    Entry[] entry;
    Thread thread;
    boolean isRunning;
    Semaphore semaphore;
    int seq;
    public int fd_async(Uri uri,Callback callback) {
    	return(fd_async(new Entry(uri), callback));
    }
    public int fd_async(String path,Callback callback) {
    	return(fd_async(new Entry(path), callback));
    }
    public int fd_async(Resources res,int res_id,Callback callback) {
    	return(fd_async(new Entry(res, res_id), callback));
    }
    public int fd_async(Bitmap bm,Callback callback) {
    	return(fd_async(new Entry(bm), callback));
    }
    public int fd_async(Entry e,Callback callback) {
    	synchronized(this) {
    		if(size >= max){
    			FDLog.e(TAG, "*error overflow");
    			return(-1);
    		}
    		int id = head++;
    		if(head >= max)head = 0;
    		size++;
    		e.callback = callback;
    		e.tt = System.currentTimeMillis();
    		e.id = id;
    		if(callback == fd_callback)e.semaphore = new Semaphore(0, true);
    		entry[id] = e;
    		semaphore.release();
    		return(id);
    	}
    }
    public Entry get() {
    	synchronized(this) {
    		if(size <= 0)return(null);
    		int id = tail++;
    		if(tail >= max)tail = 0;
    		size--;
    		return(entry[id]);
    	}
    }
    public void stop() {
    	isRunning = false;
    	semaphore.release();
    	fd_task_del(this);
    }
	Runnable task_run = new Runnable() { public void run() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST); // 2012.12.17
		Entry e;
		while(isRunning) {
			try {
				semaphore.acquire();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			if(!isRunning)break;
			if((e = get()) == null)continue;
			e.t = System.currentTimeMillis();
			FDLog.d(TAG, ">>>>>> detect start");
			face_detect(e);
			e.t = System.currentTimeMillis() - e.t;
			e.tt = System.currentTimeMillis() - e.tt;
			if(e.callback != null) {
				e.callback.detect_end(e);
				if (e.callback != fd_callback)
					FDLog.d(TAG, "<<<<<< detect end2, user-callback");
			}
			else
				FDLog.d(TAG, "<<<<<< detect end3, no callback");
		}
	}};
	public int[] fd_wait(String path) {
		int id;
		if((id = fd_async(path, fd_callback)) < 0)return(null);
		return(fd_wait(entry[id]));
	}
	public int[] fd_wait(String path,int[] outSize) {
		int id;
		if((id = fd_async(path, fd_callback)) < 0)return(null);
		return(fd_wait(entry[id], outSize));
	}
	public int[] fd_wait(Uri uri) {
		int id;
		if((id = fd_async(uri, fd_callback)) < 0)return(null);
		return(fd_wait(entry[id]));
	}
	public int[] fd_wait(Uri uri,int[] outSize) {
		int id;
		if((id = fd_async(uri, fd_callback)) < 0)return(null);
		if (DEBUG_Performance)
			FDLog.i(TAG,"Uri =" + uri);
		return(fd_wait(entry[id], outSize));
	}
	public int[] fd_wait(Bitmap bm) {
		int id;
		if((id = fd_async(bm, fd_callback)) < 0)return(null);
		return(fd_wait(entry[id]));
	}
	public int[] fd_wait(Bitmap bm,int[] outSize) {
		int id;
		if((id = fd_async(bm, fd_callback)) < 0)return(null);
		return(fd_wait(entry[id], outSize));
	}
	private int[] fd_wait(Entry e) {
		boolean iflag = false;
		try {
		    e.semaphore.acquire();
		} catch (InterruptedException ex) {
		    ex.printStackTrace();
		    //e.box = null; // 2013.02.07
		    iflag = true; // 2013.02.19
		}
		if(iflag)return(null); // 2013.02.19
		return(e.box);
	}
	private int[] fd_wait(Entry e,int[] outSize) {
		boolean iflag = false;
		try {
		    e.semaphore.acquire();
		} catch (InterruptedException ex) {
		    ex.printStackTrace();
		    //e.box = null; // 2013.02.07
		    iflag = true; // 2013.02.19
		}

		if (DEBUG_Performance)
			FDLog.i(TAG, "FD schedule= " + e.tt  + " fd=" + e.t);
		
		if(outSize != null && outSize.length == 2){
			outSize[0] = e.outSize[0];
			outSize[1] = e.outSize[1];
		}
		if(iflag)return(null); // 2013.02.19
		return(e.box);
	}
    Callback fd_callback = new Callback() {
		public void detect_end(Entry e) {
			// dump log
			String tag = "";
			for (int i = 0; i < e.box.length; i++) {
				tag += (e.box[i] + " ");
				if (i > 0 && (i+1)%4 == 0)
					tag += ",";
			}
			FDLog.d(TAG, "<<<<<< detect end: path=" + e.path + ", box=" + tag);
			// dump log
			if(e.semaphore != null)e.semaphore.release();
		}
    };
    /*
    public void native_face_detect(Entry e,String path,int width,int height,int scale) {
    	//Log.d(TAG, "native_face_detect");
        final ImageLib imageLib = ImageLib.sInstance();
        final int nHandle = imageLib.decodeBegin();
        if(nHandle == 0)return;
        imageLib.setDegree(nHandle, 0);
        imageLib.setBitmapColorDepth(nHandle, 32);
        imageLib.setPreferSize(nHandle, width, ImageLib.IMAGELIB_PREFER_ORIGINAL_SIZE);
        imageLib.setScaleType(nHandle, ImageLib.IMAGELIB_SCALE_FIT);
        imageLib.loadFromFilePath(nHandle, path);
        while(imageLib.decodeIterate(nHandle, 2000) == ImageLib.IMAGELIB_OK) {}
        //ByteBuffer b_argb = imageLib.decodeEndForByteBuffer(nHandle); ---> java.lang.NoSuchMethodError in SDK 5.6
        Bitmap bm = imageLib.decodeEnd(nHandle);
        //Log.d(TAG, "bm(" + bm.getWidth() + "x" + bm.getHeight() + ") size(" + width + "x" + height + ")");
        width = bm.getWidth();
        height = bm.getHeight();
        ByteBuffer b_argb = NativeMemoryAllocator.allocateBuffer(4 * width * height);
        bm.copyPixelsToBuffer(b_argb);
        bm.recycle();
        int w2 = ((width & 0x01) == 0) ? width : (width + 1);
        int h2 = ((height & 0x01) == 0) ? height : (height + 1);
	    ByteBuffer b_yuv = NativeMemoryAllocator.allocateBuffer(w2 * h2 * 3 / 2);
	    if(imageConverter == null)imageConverter = new ImageConverter();
	    if(photoEffect == null)photoEffect = new PhotoEffect();
	    imageConverter.argb88882yuv420spByteBuffer(width, height, b_argb, b_yuv);
	    //imageLib.freeByteBuffer(b_argb);
	    NativeMemoryAllocator.freeBuffer(b_argb);
	    synchronized(fd_task) // multi-thread test will crash if not synchronized
	    {
            photoEffect.PhotoEffectDetection(b_yuv, width, height, 1, 1);
            NativeMemoryAllocator.freeBuffer(b_yuv);
            face_detect_result(e, scale);
            photoEffect.closePhotoEffect();
	    }
    }
    */
    public boolean use_native_decode = false;
    //
    public static final int MAX_WIDTH = 500;
    public static final int MAX_HEIGHT = 500;
    public static final int MAX_SIZE = 4 * MAX_WIDTH * MAX_HEIGHT;

	int[] noBox = {0,0,0,0};
    void face_detect(Entry e) {
    	String path = null;
    	//e.box = null;
    	e.box = noBox; // Leo: to avoid caller not check if return value == null
    	if(e.bm != null){
    		FDLog.d(TAG, "use bmp to decode.");
    		face_detect(e, e.bm, 1);
    		return;
    	}
    	if((e.res != null) && (e.res_id != 0)){
    		FDLog.d(TAG, "use resource id to decode.");
    		face_detect(e, e.res, e.res_id);
    		return;
    	}
	    if(e.uri != null)path = e.uri.getPath();
    	else if(e.path != null)path = e.path;
	    if(path == null) {
	    	FDLog.e(TAG, "error! path=null, bmp=null");
	    	return;
	    }
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		InputStream is = null;
		try {
			is = StorageManager.getFileInputStream(Cipher.DECRYPT_MODE, e.uri, getEncryptionKey());
			if ( is != null ) {
				BitmapFactory.decodeStream(is, null, options);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if ( is != null ) {
				try {
					is.close();
				} catch (IOException e1) {
					// ignore
				}
			}
		}

		int width = options.outWidth;
		int height = options.outHeight;
		if(e.outSize != null && e.outSize.length == 2){
		    e.outSize[0] = width;
		    e.outSize[1] = height;
		}
        int scale = 1;
        FDLog.d(TAG, "original w:" + width + ", h:" + height);
        // WJ: the scale value of inSampleSize must be 1, 2, 4, 8,
        // while(((4 * width * height) / (scale * scale)) > MAX_SIZE)scale++;
        while(((4 * width * height) / (scale * scale)) > MAX_SIZE)scale *= 2;
        if(scale > 1){
        	width /= scale;
        	height /= scale;
        }
        FDLog.d(TAG, "scaled w:" + width + ", h:" + height);
        Bitmap bm = null;
        if(!use_native_decode || use_google_face_detect){
            options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            if(use_google_face_detect)options.inPreferredConfig = Bitmap.Config.RGB_565;
            if(scale > 1)options.inSampleSize = scale;

            is = null;
            try {
                is = StorageManager.getFileInputStream(Cipher.DECRYPT_MODE, e.uri, getEncryptionKey());
                if ( is != null ) {
                    bm = BitmapFactory.decodeStream(is, null, options);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                if ( is != null ) {
                    try {
                        is.close();
                    } catch (IOException e1) {
                        // ignore
                    }
                }
            }

        }else{
            //native_face_detect(e, path, width, height, scale);
            return;
        }
		if(bm == null) {
			FDLog.e(TAG, "error! Cannot decode: path=" + path);
			return;
		}
		face_detect(e, bm, scale);
	}
	void face_detect(Entry e,Resources res,int res_id) {	
		long n1 = 0;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		if (DEBUG_Performance)
			n1 = System.currentTimeMillis();		
		BitmapFactory.decodeResource(res, res_id, options);
		int width = options.outWidth;
		int height = options.outHeight;
		if(e.outSize != null && e.outSize.length == 2 ){
		    e.outSize[0] = width;
		    e.outSize[1] = height;
		}
        int scale = 1;
        while(((4 * width * height) / (scale * scale)) > MAX_SIZE)scale++;
        if(scale > 1){
        	width /= scale;
        	height /= scale;
        }
        options = new BitmapFactory.Options();
    	options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    	if(use_google_face_detect)options.inPreferredConfig = Bitmap.Config.RGB_565;
    	if(scale > 1)options.inSampleSize = scale;
		Bitmap bm = BitmapFactory.decodeResource(res, res_id, options);
		if(bm == null) {
			FDLog.e(TAG, "error! Cannot decode: res_id=" + res_id);
			return;
		}
		if (DEBUG_Performance)
			FDLog.i(TAG, "decode=" + (System.currentTimeMillis() -n1));
		face_detect(e, bm, scale);
	}
    void face_detect(Entry e,Bitmap bm,int scale) {
		
		int width = bm.getWidth();
        int height = bm.getHeight();

		if(e.outSize != null && e.outSize.length == 2){
		    if(e.outSize[0] == 0)e.outSize[0] = width;
		    if(e.outSize[1] == 0)e.outSize[1] = height;
		}
//		if(use_google_face_detect){
////			int[] box = face_detect_google(bm);
////			if(scale > 1)for(int i = 0; i <  box.length; i++)box[i] = box[i] * scale;
////			e.box = box;
//			face_detect_google(e, bm, scale);
//			return;
//		}
		face_detect_impl(e, bm, scale);
    }
	protected abstract void face_detect_impl(Entry e, Bitmap bm, int scale);
	
	
    


    public void setEncryptionKey(String key) {
        if ( mEncryptionKey == null && key != null ) {
            mEncryptionKey = key;
        }
    }

    public String getEncryptionKey() {
        return mEncryptionKey;
    }

    public static class FDLog {
        static boolean DEBUG = HtcBuildFlag.Htc_DEBUG_flag;
        // back door to enable debug log in release ROM
        static int bDebugOpen = SystemProperties.getInt("profile.fdeng.debug", 0);

        /**
         * @hide
         */
        public static void e(String TAG, String Info) {
                Log.e(TAG, Info);
        }

        /**
         * @hide
         */
        public static void e(String TAG, String Info, Throwable Tr) {
                Log.e(TAG, Info, Tr);
        }

        /**
         * @hide
         */
        public static void i(String TAG, String Info) {
            if (DEBUG || bDebugOpen > 0)
                Log.d(TAG, Info);
        }

        /**
         * @hide
         */
        public static final void i(String TAG, String... messages)
        {
            if((DEBUG ||bDebugOpen > 0) && messages.length > 0)
            {
                StringBuilder builder = new StringBuilder();
                for(int i = 0 ; i < messages.length ; ++i)
                    builder.append(messages[i]);
                Log.d(TAG, builder.toString());
            }
        }

        /**
         * @hide
         */
        public static void d(String TAG, String Info) {
            if (DEBUG || bDebugOpen > 1)
                Log.d(TAG, Info);
        }

        /**
         * @hide
         */
        public static final void d(String TAG, String... messages)
        {
            if((DEBUG ||bDebugOpen > 1) && messages.length > 0)
            {
                StringBuilder builder = new StringBuilder();
                for(int i = 0 ; i < messages.length ; ++i)
                    builder.append(messages[i]);
                Log.d(TAG, builder.toString());
            }
        }
    }
}
