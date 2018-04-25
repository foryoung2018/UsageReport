package com.htc.lib1.exo.parser.mp4;

import android.content.Context;
import android.net.Uri;
import android.util.Pair;

import java.io.IOException;
import java.io.InputStream;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.TrackInfo;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.extractor.mp4.Mp4Extractor;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.MimeTypes;
import com.htc.lib1.exo.utilities.LOG;

public final class HtcVideoChecker {
    private static final String TAG = "HtcVideoChecker";
    private long mModifiedTime = -1;
    private boolean mbHTCBox = false;
    private long mlHTCBoxOffset = -1;
    private Context mContext = null;
    private Uri mUri = null;
    private boolean mbMp4 = false;
    private long mSlowMotionVersion = -1;

    private long mSlowMotionMultiple = -1;

    private boolean mbSlowMotion = false;
    private long mSlowMotionAudioOffset = -1;
    
    private long mCameraId = -1;

    class MediaBox 
    {
        public static final int TYPE_CamD = 0x43616D44;
        public static final int TYPE_HMTT = 0x484D5454; //Media taken time
        public static final int TYPE_HMTa = 0x484D5461;
        public static final int TYPE_ZCVR = 0x5A435652; //Zoe Cover Photo
        public static final int TYPE_ZPTH = 0x5A505448; //Zoe jpeg height
        public static final int TYPE_ZPTW = 0x5A505457; //Zoe jpeg width
        public static final int TYPE_ZSHT = 0x5A534854;
        public static final int TYPE_ZJPG = 0x5A4A5047;
    }

    public long getModifiedTime()
    {
        LOG.I(TAG,"getModifiedTime : " + mModifiedTime);
        return mModifiedTime;
    }

    public boolean isSlowMotionVideo()
    {
        LOG.I(TAG,"isSlowMotionVideo : " + mbSlowMotion);
        return mbSlowMotion;
    }

    public long getSlowMotionVersion()
    {
        if (mbSlowMotion){
            LOG.I(TAG,"getSlowMotionVersion : " + mSlowMotionVersion);
            return mSlowMotionVersion;
        }
        return -1;
    }
    
    public long getSlowMotionMultiple()
    {
        if (mbSlowMotion){
            LOG.I(TAG,"getSlowMotionMultiple : " + mSlowMotionMultiple);
            return mSlowMotionMultiple;
        }
        return -1;
    }
    
    public long getSlowMotionAudioOffset()
    {
        if (mbSlowMotion){
            LOG.I(TAG,"getSlowMotionAudioOffset : " + mSlowMotionAudioOffset);
            return mSlowMotionAudioOffset;
        }
        return -1;
    }

    /**
     * Get the camera id from the recored video, 
     *
     * @return 1 for main camera, 2 for front camera and 3 for sub camera 
     */
    public long getCameraId()
    {
        return mCameraId;
    }

    public boolean isMp4()
    {
        return mbMp4;
    }

    protected int doPrepare(SampleSource source, long positionUs) throws ExoPlaybackException {
      try {
        boolean sourcePrepared = source.prepare(positionUs);
        if (!sourcePrepared) {
          return TrackRenderer.STATE_UNPREPARED;
        }
      } catch (IOException e) {
        throw new ExoPlaybackException(e);
      }
      return TrackRenderer.STATE_PREPARED;
    }
    public Pair<Integer, Integer> getMp4VideoDimension() {

        final int BUFFER_SIZE = 10 * 1024 * 1024;
        if (isMp4() == false) return null;
        if (mContext == null) return null;
        if (mUri == null) return null;

        DataSource dataSource = new DefaultUriDataSource(mContext, null, new DefaultHttpDataSource("ExoPlayer",null));
        LOG.I(TAG, "parseFromExo uri : " + mUri);

        SampleSource sampleSource = new ExtractorSampleSource(mUri, dataSource, new Mp4Extractor(), 2, BUFFER_SIZE);

        try {
            while (doPrepare(sampleSource, 0) != TrackRenderer.STATE_PREPARED)
            {

            }
        } catch (Exception e) {
            LOG.W(TAG, e);
        }
        try {

			int trackCount = sampleSource.getTrackCount();
			int trackIndex = -1;
			LOG.I(TAG, "parseFromExo  trackCount " + trackCount);
			for (int i =0 ; i < trackCount ; i++) {
				if (MimeTypes.isVideo(sampleSource.getTrackInfo(i).mimeType)) {
					trackIndex = i;
					break;
				}
			}
			
			if (trackIndex != -1) {				
				String width = sampleSource.getTrackInfo(trackIndex).extractMetadata(TrackInfo.METADATA_KEY_VIDEO_WIDTH);
				LOG.I(TAG, "parseFromExo  width " + Integer.parseInt(width));
				String height = sampleSource.getTrackInfo(trackIndex).extractMetadata(TrackInfo.METADATA_KEY_VIDEO_HEIGHT);
				LOG.I(TAG, "parseFromExo  height " + height);
				return new Pair<Integer, Integer>(Integer.parseInt(width), Integer.parseInt(height));
			}
		} catch (Exception e) {
			LOG.W(TAG, e);
		} finally{
            sampleSource.release();
        }
        return null;
    }

    public void parse(Context context, Uri uri)
    {
        this.mContext = context;
        this.mUri = uri;
        LOG.I(TAG,"parse() uri = " + uri);
        InputStream in = null;
        CountedDataInputStream input = null;
        try {
            in = context.getContentResolver().openInputStream(uri);
            input = new CountedDataInputStream(in);

            long oldSMsize = findInAtom(input, Atom.TYPE__htc,-1);
            if(oldSMsize >= 0)
                check_htc(input, oldSMsize);

            long size = findInAtom(input, Atom.TYPE_moov, -1);

            mbMp4 = true;

            long udatSize = findInAtom(input, Atom.TYPE_udta, size);

            checkUdat(input, udatSize);

            size = findInAtom(input, Atom.TYPE_htka, size);

            if (size > 0){
                mSlowMotionAudioOffset = (input.getReadByteCount() - 8);
            }

            try
            {
                input.close();
                in.close();
            }
            catch(Exception e)
            {
                //LOG.W(TAG, e);
            }

            in = context.getContentResolver().openInputStream(uri);
            input = new CountedDataInputStream(in);

            checkHTCBox(input);            
        }
        catch(java.io.EOFException e)
        {
            LOG.D(TAG, "EOF");
        }
        catch(Exception e)
        {
            LOG.W(TAG, e);
        }
        finally
        {
            try
            {
                input.close();
                in.close();
            }
            catch(Exception e)
            {
                //LOG.W(TAG, e);
            }
        }
    }
    private void checkUdat(CountedDataInputStream input, long size) throws Exception
    {
        long used = 0;
        LOG.I(TAG,"checkUdat() size = " + size);
        while(used < (size - 8)){
            long offset = input.readUnsignedInt();
            LOG.I(TAG,"checkUdat() offset  = " + offset);
            if (offset > Integer.MAX_VALUE) {
                throw new Exception("Invalid offset " + offset);
            }

            long header = input.readUnsignedInt();
            used += offset;
            
            LOG.I(TAG,"checkUdat() header = " + toAscii(header));
            if (header == Atom.TYPE_htcb)
            {                
                long used2 = readHtcbOffset(input);

                offset = (offset) - used2;         	
            }
            else if (header == Atom.TYPE_dtah)
            {
                long used2 = readDtah(input, offset);

                offset = (offset) - used2;
            }
            if (offset > 8)
                input.skip((offset - 8));
        }
    }

    private void check_htc(CountedDataInputStream input, long size) throws Exception
    {
        long used = 0;
        LOG.I(TAG,"check_htc() size = " + size);
        while(used < (size - 8)){
            long offset = input.readUnsignedInt();
            LOG.I(TAG,"check_htc() offset  = " + offset);
            if (offset > Integer.MAX_VALUE) {
                throw new Exception("Invalid offset " + offset);
            }

            long header = input.readUnsignedInt();
            used += offset;

            LOG.I(TAG,"check_htc() header = " + toAscii(header));
            if (header == Atom.TYPE_slmt)
            {
                mbSlowMotion = true;
                mSlowMotionVersion = 1;
                mSlowMotionMultiple = input.readUnsignedInt();

                LOG.I(TAG,"check_htc SlowMotionMultiple = " + mSlowMotionMultiple);
            }
        }
    }

    private byte[] checkHTCBox(CountedDataInputStream input) throws Exception
    {
        if (mbHTCBox == false) return null;
        if (mlHTCBoxOffset < 0) return null;

        input.skipTo(mlHTCBoxOffset);

        long header = input.readUnsignedInt();
        LOG.I(TAG,"checkHTCBox() header = " + toAscii(header));

        long version = input.readUnsignedInt();
        LOG.I(TAG,"checkHTCBox() version = " + version);

        final int MAX_BUF_LENGTH = 1024;
        while(true){
            long key = input.readUnsignedInt();
            long is_data = input.readUnsignedInt();
            int value_size = (int) input.readUnsignedInt();
            
            //LOG.I(TAG,"checkHTCBox() key = " + toAscii(key));
            //LOG.I(TAG,"checkHTCBox() is_data = " + is_data);
            //LOG.I(TAG,"checkHTCBox() value_size = " + value_size);

            if (key == MediaBox.TYPE_HMTT)
            {
                //Media taken time
                if (value_size < MAX_BUF_LENGTH && value_size > 0)
                {
                    byte buf[] = new byte[value_size];
                    input.readOrThrow(buf);

                    if( buf != null)
                    {
                        String str = new String(buf, "ASCII");
                        mModifiedTime = Long.parseLong(str);
                        LOG.I(TAG, "checkHTCBox() mModifiedTime = " + mModifiedTime);
                    }
                }
                else
                {
                    throw new Exception("Invalid value_size " + value_size);
                }
            }
            else if (key == MediaBox.TYPE_HMTa)
            {
                // HMTa is placed at the begining and ending of the HTC Box
                input.skip(value_size);
            }
            else if (key == MediaBox.TYPE_CamD)
            {
                long camera_id = input.readUnsignedInt();
                LOG.I(TAG,"checkHTCBox() camera_id = " + camera_id);
                if (camera_id == 0 || camera_id == 1) 
                {
                    mCameraId = camera_id;
                }
            }
            else
            {
                input.skip(value_size);
            }
        }

    }
    private long findInAtom(CountedDataInputStream input, int target, long maxSize) throws Exception
    {
        long rtn = -1;
        long used = 0;
        String logPrefix = "findInAtom(" + toAscii(target) + "," + maxSize + ") = " + rtn;
        while((used < (maxSize - 8)) || maxSize < 0){
            int headerSize = 8;
            long offset = input.readUnsignedInt();
            if (offset > Integer.MAX_VALUE) {
                throw new Exception("Invalid offset " + offset);
            }

            long header = input.readUnsignedInt();

            if(offset == 1) { //the extended atom size is contained in next 8 bytes.
                offset = input.readLong();
                if(offset < 16)
                    throw new Exception("Invalid offset " + offset);
                headerSize = 16;
            }

            used += offset;

            LOG.I(TAG,logPrefix + "header = " + toAscii(header) + ", offset  = " + offset);
            if (header == target)
            {
                LOG.I(TAG,logPrefix + "find it offset = " + offset);
                rtn = offset;
                break;
            } else
            {
                input.skip((offset - headerSize));
            }
        }
        return rtn;
    }

    private long readHtcbOffset(CountedDataInputStream input) throws Exception
    {
        long used = 0;
        long version  = input.readUnsignedInt();
        used += 4;
        LOG.I(TAG,"readHtcbOffset() version = " + version);

        long offsetType = input.readUnsignedInt(); //0 for 64 , 1 for 32
        used += 4;
        LOG.I(TAG,"readHtcbOffset() offsetType = " + offsetType);

        boolean bUse32BitOffset = (offsetType == 1) ? true : false;

        long offset = -1;
        if (bUse32BitOffset)
        {
            offset = input.readUnsignedInt();
            used += 4;
            LOG.I(TAG,"readHtcbOffset() offset32 = " + Long.toHexString(offset));
        }
        else
        {      
            offset = input.readLong();
            used += 8;
            LOG.I(TAG,"readHtcbOffset() offset64 = " + Long.toHexString(offset));
        }

        if (offset > 0)
        {
            mbHTCBox = true;
            mlHTCBoxOffset = offset;
        }
        else
        {
            throw new Exception("Invalid offset " + offset);
        }

        long length = input.readUnsignedInt();
        used += 4;
        return used;
    }

    private long readDtah(CountedDataInputStream input, long offset) throws Exception
    {
        long used2 = findInAtom(input, Atom.TYPE_slmt, offset);
        if( used2 > 0)
        {
            mbSlowMotion = true;

            mSlowMotionVersion = input.readUnsignedInt();
            used2 += 4;
            LOG.I(TAG,"readDtahAndSlowMotion() SlowMotionVersion = " + mSlowMotionVersion);

            mSlowMotionMultiple = input.readUnsignedInt();
            used2 += 4;
            LOG.I(TAG,"readDtahAndSlowMotion() SlowMotionMultiple = " + mSlowMotionMultiple);                    
        }
        return used2;
    }

    private static String toAscii(long value)
    {
        String hex = Long.toHexString(value);

        return toAscii(hex);
    }

    private static String toAscii(String hex)
    {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i+=2) {
            String str = hex.substring(i, i+2);
            output.append((char)Integer.parseInt(str, 16));
        }

        return output.toString();
    }

}

