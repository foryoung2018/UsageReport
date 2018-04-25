package com.htc.lib1.upm.uploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import android.content.Context;
import com.htc.lib1.upm.Common;
import com.htc.lib1.upm.Log;

public class EntryFile {
	private final File mFile;
	private final FileChangdListener mFileChangedListener;
	public EntryFile(File file, FileChangdListener fileChangedListener) throws IllegalArgumentException {
		if(file == null)
			throw new IllegalArgumentException("EntryFile can't accept a null pointer of file");
		mFile = file;
		mFileChangedListener = fileChangedListener;
	}
	
	public void delete() {
		if(mFile.exists()) {
			if(mFileChangedListener != null) {
				mFileChangedListener.onDelete(mFile);
			}
			mFile.delete();
		}
	}

	public String getName() {
		return mFile.getName();
	}
	
    public InputStream getFileInputStreamEx(Context context) throws IOException,GeneralSecurityException {
        FileInputStream fis = null;
        boolean isLogStream = false;
        try {
            fis = new FileInputStream(mFile);
            isLogStream = LogStream.isLogStream(fis);
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
               	e.printStackTrace();
            }
		}
        if(isLogStream) {
            fis = new FileInputStream(mFile);
            return LogStream.concatenateInputStream(fis, context);
        } 
        
        return null;			
	}
	
    // zip
    public static File writeNewFileEx(Context ctx, byte [] logBuf, String tag) {
        boolean ret = false;
        
        File logFolder = ctx.getDir(Common.RELATIVE_LOG_FOLDER_PATH, Context.MODE_PRIVATE);
        String filePath = logFolder.getAbsolutePath() + "/" + LogCacheUtil.generateFileName(tag);
        Log.d("store a new file: " + filePath +" , TAG="+tag);
        
        FileOutputStream fos = null;
        OutputStream os = null;
        try {
            fos = new FileOutputStream(filePath);
            os = LogStream.concatenateOutputStream(fos, true, ctx); // new format
            if(os != null) {
                os.write(logBuf);
                ret = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } finally {
            try {//[REX]: Must close os first, 2012/12/04
                if (os != null)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                // [CQG #88]modified by Ricky,2012.0925
            if (fos != null)
                fos.close();
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
        
        if(ret)
            return new File(filePath);
        else
            return null;
    }
}
