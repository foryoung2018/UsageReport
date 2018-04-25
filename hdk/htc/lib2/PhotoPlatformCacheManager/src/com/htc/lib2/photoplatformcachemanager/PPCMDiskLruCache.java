package com.htc.lib2.photoplatformcachemanager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.zip.CRC32;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

import com.aiqidii.mercury.provider.PhotoPlatformException;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Base64;

/**
 * Each instance of DiskLruCache is thread-safe, but it must never have multiple instances of DiskLruCache simultaneously pointing at the same directory in the filesystem. 
 * DiskLruCache makes no attempt to cooperate with other processes that may be writing to its directory, and so its behavior in that situation is not well defined.﻿
 */
public class PPCMDiskLruCache {
	private static final String TAG = PhotoPlatformCacheManager.class.getSimpleName();    	
    private static DiskLruCache sDiskCache = null;
    private static int appVersion = 1;
    private static final int valueCount = 1; // the number of values per cache entry. Must be positive
    public static final long DISK_CACHE_DEFAULT_SIZE = 1024 * 1024 * 32;        
    private static final boolean bEncrypt = false;    
    private static String packageName = null; 
    
    public static DiskLruCache getDiskCacheInstance(Context context) {
        if (null == sDiskCache) {
            File cacheDir = getDiskCacheDir(context);
            try {
            	synchronized (PPCMDiskLruCache.class) {
            		 if (null == sDiskCache) // double check
            			 sDiskCache = DiskLruCache.open(cacheDir, getAppVersion(context), valueCount, DISK_CACHE_DEFAULT_SIZE);
            	}
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return sDiskCache;
    }
    
    private static int getAppVersion(Context context) {
    	try {  
    		PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
    		return info.versionCode;  
    	} catch (NameNotFoundException e) {
    		e.printStackTrace();
    	}
    	
    	return appVersion;
    }       
    
    private static String getPackageNameByProcessID(Context context) {
        String processName = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);        
        Iterator<RunningAppProcessInfo> i = am.getRunningAppProcesses().iterator();
        while(i.hasNext()) 
        {
              ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
              try 
              { 
                  if(info.pid == android.os.Process.myPid())
                  {
                      CLog.d(TAG, "[getProcessName] Id: "+ info.pid +", ProcessName: "+ info.processName, false);
                      processName = info.processName;
                      break;
                  }
              }
              catch(Exception e) 
              {
                  CLog.e(TAG, "[getProcessName] " + e.getMessage(), true);
              }
       }
        
        return processName;
    }       
    
    private static String getUniqueFolderName(Context context) {
    	if (packageName == null) {
    		packageName = getPackageNameByProcessID(context);
    		if (packageName == null)
    			packageName = context.getPackageName();
    	}    	
    	
    	return "." + Base64.encodeToString(packageName.getBytes(), Base64.DEFAULT); // Adding a dot prefix to hide cache
    }       
    
    private static File getDiskCacheDir(Context context) {    	    	    	
    	String uniqueName = getUniqueFolderName(context);
        String cachePath = context.getFilesDir().getPath();
        
        return new File(cachePath + File.separator + uniqueName); // generate unique path => /top_layer_package_name/files/process_package_name_base64 
    }        
    
    public static int copyStream(InputStream input, OutputStream output) throws Exception {
	    byte[] buffer = new byte[4096];
	    int bytesRead = 0;
	    int nTotalRead = 0;
	    try {	    	
	    	CLog.d(TAG, "[copyStream] ready to copy stream" , false);
			while ((bytesRead = input.read(buffer)) != -1)
			{
			    output.write(buffer, 0, bytesRead);
			    nTotalRead += bytesRead;
			}
		} catch (IOException e) {
			e.printStackTrace();
        } catch (Exception e) {
        	PhotoPlatformException exception = PhotoPlatformException.cast(e);
        	throw exception;
        } finally {
	    	Utils.closeQuietly(input);
	    }
	    
	    return nTotalRead;
    }    
    
    private static int encryptOutputStream(Context context, InputStream input, OutputStream out) throws Exception {
    	InputStream in = null;
    	int readSize = 0;
    	int nTotalRead = 0;
    	Cipher cipher = EncryptionUtils.getCipher(Cipher.ENCRYPT_MODE, EncryptionUtils.getEncryptionKey(context), EncryptionUtils.CIPHER_IV_STR);
    	if (cipher != null) {
	    	out = new CipherOutputStream(out, cipher);
	    	if (out != null) {	    	
		    	try {
			        in = new BufferedInputStream(input, 2048);
			        CRC32 digester = new CRC32();
			        final byte[] buf = new byte[2048];
			        
			        while ( (readSize = in.read(buf)) >= 0 ) {
			            digester.update(buf, 0, readSize);
			            out.write(buf, 0, readSize);
			            nTotalRead += readSize;
			        }
			        out.flush();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } catch (Exception e) {
		            throw e;
		        } finally {
		        	Utils.closeQuietly(in);
		        }
	    	}
    	}
    	
    	return nTotalRead;
    }
    
    @Deprecated
    public static int writeToDiskCache(String imageUri, InputStream input, Context context) throws Exception {    	
    	int nInputStreamBytes = 0; // default is 0; -1 means write cache failed
        String key = getConvertString(imageUri);
        DiskLruCache dlc = getDiskCacheInstance(context);
        OutputStream output = null;
        
        if (dlc == null) {
        	CLog.e(TAG, "[writeToDiskCache] getDiskCacheInstance failed" , false);
            return nInputStreamBytes; 
        }

        try {
            final DiskLruCache.Editor editor = dlc.edit(key);
            if (editor != null) {
            	output = editor.newOutputStream(0);
            	
            	CLog.d(TAG, "[writeToDiskCache] copy stream +++ " , false);
            	nInputStreamBytes = (bEncrypt) ? 
            			encryptOutputStream(context, input, output) : copyStream(input, output);
            	CLog.d(TAG, "[writeToDiskCache] copy stream --- " , false);
            	
            	// Commit to cache file only if the InputStream is valid, abort otherwise 
            	if (nInputStreamBytes > 0) {
            		CLog.d(TAG, "[writeToDiskCache] nInputStreamBytes: " + nInputStreamBytes , false);
            		editor.commit();            		
            	}
            	else {
            		CLog.e(TAG, "[writeToDiskCache] InputStream is 0 byte, ABORT.", false);
            		editor.abort();
            	}
            }
            else {
            	CLog.e(TAG, "[writeToDiskCache] getDiskCache editor failed" , false);
            	nInputStreamBytes = -1;
            }
            	
        } catch (IOException e) {
        	e.printStackTrace();
        }  catch (Exception e) {
        	throw e;
        } finally {
        	Utils.closeQuietly(output);
        }
        
        return nInputStreamBytes;
    } 
    
    public static int writeToDiskCache(Context context, Uri imageUri) throws Exception {
    	int nInputStreamBytes = 0; // default is 0; -1 means write cache failed
        String key = getConvertString(imageUri.toString());
        DiskLruCache dlc = getDiskCacheInstance(context);
        InputStream input = null;
        OutputStream output = null;
        
        if (dlc == null) {
        	CLog.e(TAG, "[writeToDiskCache] getDiskCacheInstance failed" , false);
            return nInputStreamBytes; 
        }

        try {
            final DiskLruCache.Editor editor = dlc.edit(key);
            if (editor != null) {
            	long readTaskStart = System.currentTimeMillis();
            	input = context.getContentResolver().openInputStream(imageUri);
            	output = editor.newOutputStream(0);

            	nInputStreamBytes = (bEncrypt) ? 
            			encryptOutputStream(context, input, output) : copyStream(input, output);
            	CLog.d(TAG, "[writeToDiskCache] copy stream takes [" + (System.currentTimeMillis()- readTaskStart) + "ms]" , false);
            	
            	// Commit to cache file only if the InputStream is valid, abort otherwise 
            	if (nInputStreamBytes > 0) {
            		CLog.d(TAG, "[writeToDiskCache] number of bytes: " + nInputStreamBytes , false);
            		editor.commit();
            	}
            	else {
            		CLog.e(TAG, "[writeToDiskCache] InputStream is 0 byte, ABORT.", false);
            		editor.abort();
            	}
            }
            else {
            	CLog.e(TAG, "[writeToDiskCache] getDiskCache editor failed" , false);
            	nInputStreamBytes = -1;
            }
            	
        } catch (IOException e) {
        	e.printStackTrace();
        }  catch (Exception e) {
        	throw e;
        } finally {
        	Utils.closeQuietly(input);
        	Utils.closeQuietly(output);
        }

        return nInputStreamBytes;
    }     
    
    public static InputStream readFromDiskCache(Context context, String imageUri) {
        String key = getConvertString(imageUri);
        DiskLruCache.Snapshot snapshot = null;
        InputStream in = null;
        
        try {
            snapshot = getDiskCacheInstance(context).get(key);
            if (null != snapshot) {
            	in = snapshot.getInputStream(0);
            	if (bEncrypt) {           	             	            		
            		Cipher cipher = EncryptionUtils.getCipher(Cipher.DECRYPT_MODE, EncryptionUtils.getEncryptionKey(context), EncryptionUtils.CIPHER_IV_STR);
            		in = new CipherInputStream(in, cipher);
            	}
            	
            	// Double check before return disk cache InputStream
            	if (in.available() <= 0) {
            		CLog.e(TAG, "[readFromDiskCache] InputStream available() is  " + in.available(), false);
            		Utils.closeQuietly(in);
            		in = null;
            	}
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return in;
    }
    
    public static String getFilePathFromDiskCache(Context context, String imageUri) {
        String key = getConvertString(imageUri);
        DiskLruCache.Snapshot snapshot = null;
        InputStream in = null;
        String filePath = null;
        
        try {
            snapshot = getDiskCacheInstance(context).get(key);
            if (null != snapshot) {
                filePath = snapshot.getFilePath(0);
                in = snapshot.getInputStream(0);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.closeQuietly(in);
            in = null;
        }
        
        return filePath;
    }
    
    private static String getConvertString(String name) {
        int n = name.hashCode();
        String str = Integer.toString(n);
        return str;
    }
    
    public static void delete(Context context) {
    	try {
			getDiskCacheInstance(context).delete();
		} catch (IOException e) {
			CLog.d(TAG, "[delete] IOException: " + e.toString() , false);
		}
    }
    
    public static long size(Context context) {
		return getDiskCacheInstance(context).size();
    }    
    
    public static void setMaxSize(Context context, long size) {
		getDiskCacheInstance(context).setMaxSize(size);
    }        
}

