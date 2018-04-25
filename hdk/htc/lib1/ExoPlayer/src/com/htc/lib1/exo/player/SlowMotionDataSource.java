package com.htc.lib1.exo.player;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.util.Map;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import com.htc.lib1.exo.parser.mp4.CountedDataInputStream;
import com.htc.lib1.exo.utilities.LOG;

public class SlowMotionDataSource implements DataSourceHandler{
    private String TAG = "SlowMotionDataSource";
    private Context mContext = null;
    private Uri mUri = null;
    private RandomAccessFile mFd = null;
    CountedDataInputStream mInput = null;
    private int mCount = 0;
    private long mAudioPos = 0;

    private String convertUriToPath(final Uri uri)
    {
        LOG.I(TAG,"convertUriToPath uri = " + uri);
        final String DATA = "_data";
        //final String IS_DRM = "is_drm";
        String path = null;
        if (null != uri)
        {
            String scheme = uri.getScheme();
            LOG.I(TAG,"convertUriToPath scheme = " + scheme);

            if (null == scheme || scheme.equals("") ||
                    scheme.equals(ContentResolver.SCHEME_FILE)) {
                path = uri.getPath();
            }
            else if (scheme.equals("http"))
            {
                path = uri.toString();
            }
            else if (scheme.equals(ContentResolver.SCHEME_CONTENT))
            {
                String[] projection = new String[] {DATA};
                Cursor cursor = null;
                ContentResolver resolver = null;
                ContentProviderClient cpc = null;

                try
                {
                    if (mContext == null) return null;

                    resolver = mContext.getContentResolver();

                    if (resolver == null) return null;

                    cpc = resolver.acquireUnstableContentProviderClient(uri);

                    if (cpc == null) return null;

                    cursor = cpc.query(uri, projection, null,null, null);

                    if (null == cursor || 0 == cursor.getCount() || !cursor.moveToFirst())
                    {
                        throw new IllegalArgumentException("Given Uri could not be found" +
                                                           " in media store");
                    }
                    int pathIndex = cursor.getColumnIndexOrThrow(DATA);
                    path = cursor.getString(pathIndex);
                    LOG.I(TAG,"convertUriToPath path = " + path);

                    File f = new File(path);
                    LOG.I(TAG,"convertUriToPath f.exists = " + f.exists());
                    LOG.I(TAG,"convertUriToPath f.canRead = " + f.canRead());
                }
                catch (SQLiteException e)
                {
                    LOG.I(TAG,"Given Uri is not formatted in a way so that it can be found in media store.");
                }
                catch (Exception e)
                {
                    LOG.I(TAG,e);
                }
                finally
                {
                    if (null != cpc)
                    {
                        cpc.release();
                    }

                    if (null != cursor)
                    {
                        cursor.close();
                    }
                }
            }
            else
            {
                LOG.W(TAG,"Given Uri scheme is not supported");
            }
        }
        return path;
    }

    @Override
    public void init(Context context, Uri uri, Map<String, String> headers){
        mContext = context;
        mUri = uri;

        if (headers != null)
        {
            if (headers.containsKey("x-htc-slowmotion-audiooffset"))
            {
                String sAudioOffset = headers.get("x-htc-slowmotion-audiooffset");
                LOG.I(TAG,"  sAudioOffset String = " + sAudioOffset);
                mAudioPos = Integer.parseInt(sAudioOffset);
                if (mAudioPos > 0)
                {
                    //including header size. 
                    mAudioPos += 4;
                }
                LOG.I(TAG," sAudioOffset int = " + mAudioPos);
            }else{
                LOG.I(TAG," sAudioOffset no offset");
            }
        }else{
            LOG.I(TAG," sAudioOffset no headers");
        }

        mFd = null;
        try {
            ContentResolver resolver = context.getContentResolver();

            mFd = new RandomAccessFile(convertUriToPath(uri), "r");
            if (mFd == null) {
                return;
            }
            return;
        } catch (Exception ex) {
            LOG.I(TAG, ex);
            if (mInput != null) {
                try {
                    mInput.close();
                } catch (IOException e) {
                    LOG.I(TAG, e);
                }
            }

            if (mFd != null) {
                try {
                    mFd.close();
                } catch (IOException e) {
                    LOG.I(TAG, e);
                }
            }
        }
    }
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        try {
            if (args != null) 
            {

                if (method.getName().equals("readAt") ) 
                {
                    //LOG.I(TAG,"DataSource : readAt");
                    return readAt((Long) args[0], (byte[]) args[1], (Integer) args[2]);
                }
                else if (method.getName().equals("getSize") ) 
                {
                    //LOG.I(TAG,"DataSource : getSize");
                    return getSize();
                }
                else
                {
                    LOG.I(TAG,"DataSource :" + method.getName());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
        }
        return result;
    }

    public int readAt(long offset, byte[] buffer, int size)
    {            
        //LOG.W(TAG,"readAt(" + offset + "," + buffer.length + "," + size + ") ");
        int rtn = 0;
        try {
            if (offset != mCount)
            {                    
                mCount = (int) offset;
                mFd.seek(offset);
            }
            
            rtn = (int)mFd.read(buffer, 0, size);

            
            if (mAudioPos > 0 && offset <= mAudioPos && (mAudioPos <=  offset + size))
            {
                LOG.W(TAG,"readAt(" + offset + "," + buffer.length + "," + size + ") find audio");
                int posOfBuf = (int)(mAudioPos - offset);

                buffer[posOfBuf] = 't';
                buffer[posOfBuf + 1] = 'r';
                buffer[posOfBuf + 2] = 'a';
                buffer[posOfBuf + 3] = 'k';
            }
            mCount += rtn;
        } catch (IOException e) {
            LOG.I(TAG, e);
        }
        //LOG.W(TAG,"readAt(" + offset + "," + buffer + "," + size + ") = " + rtn);
        
        return rtn;
    }

    public long getSize()
    {
        long rtn = 0;
        try {
            rtn = mFd.length();
        } catch (IOException e) {
            LOG.I(TAG, e);
        }
        LOG.W(TAG,"getSize()");
        return rtn;
    }

    public final void release()
    {
        LOG.W(TAG,"release()");
        if (mInput != null) {
            try {
                mInput.close();
            } catch (IOException e) {

            }
            mInput = null;
        }

        if (mFd != null) {
            try {
                mFd.close();
            } catch (IOException e) {

            }
            mFd = null;
        }
    }
};
