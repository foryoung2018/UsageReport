package com.htc.lib1.media.zoe;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;


public class HtcSVWriter {
    private static final String TAG = HtcSVWriter.class.getSimpleName();
    private static final String HTC_INFO_BOX = "ihtc";
    private static final String HTC_TABLE_TAG = "HMTa";
    private static boolean sUSE_32BIT_OFFSET = false;
    private static long sHMTA_Offset = -1;
    private static int sHMTA_Size = -1;
    private static long sHtcTableOffset = -1;
    /**
     * Note that this api is sync, non thread safe and motion data is required
     * @param inMpg
     * @param map
     * @return 
     * @throws IOException 
     */
    public static boolean mergeUserData(String inMpg, Map<String, Object> map) throws IOException{
        if(null == inMpg) { 
            throw new IllegalArgumentException("Empty Mpeg4 path!");
        }

        if(null == map) { 
            throw new IllegalArgumentException("Empty map!");
        }

        byte[] bytes = (byte[]) map.get(HtcZoeMetadata.HTC_DATA_SEMIVIDEO_MD);			
        Object bitRate =  map.get(HtcZoeMetadata.HTC_METADATA_BITRATE);
        Object frameDropRatio = map.get(HtcZoeMetadata.HTC_METADATA_FRAME_DROP_RATIO);

        if(0 == bytes.length){
            throw new IllegalArgumentException("Empty bytes!");
        }
		
        File ori = new File(inMpg);	
        if(!ori.exists()){
            throw new IllegalArgumentException(inMpg + " not exists!");
        }
        
        //Init 
        sUSE_32BIT_OFFSET = false;
        sHMTA_Offset = -1;
        sHMTA_Size = -1;
        sHtcTableOffset = -1;
        
        long fileLength = ori.length();
        RandomAccessFile raf = new RandomAccessFile(ori, "rws");

        //*****Open file channel to read byte
        FileChannel fc = raf.getChannel();

        //*****Get HMTA table offset and size
        long offset[] = new long[1];
        boolean err = false;

        offset[0] = 0;

        while (true) {
            try {
                err = parseChunk(fc, offset, 0);
            }catch (IOException e){
                err = false;
            }
            
            if (err) {
                continue;
            }
            break;
        }
	        
        if( -1 == sHMTA_Offset || - 1 == sHMTA_Size){
            if(null != raf){
                raf.close();
                raf = null;
            }	        	 

            throw new IllegalArgumentException("Not support MPEG4 format!");	        	
        } else {
            Log.d(TAG, "sHMTA_Offset is = " + sHMTA_Offset + " sHMTA_Size is = " + sHMTA_Size);
        }

        //*****Restore all table byte info and fill ori region as 0
        byte[] oriHMTA = Util.read(fc, sHMTA_Offset, sHMTA_Size);	   	        

        byte emptyHMTA[] = new byte[oriHMTA.length];        
        Arrays.fill( emptyHMTA, (byte) 0 );

        Log.d(TAG, "Empty HMTA: write bytes " +  Util.writeBytes(fc, emptyHMTA, sHMTA_Offset));

        //*****Append motion data in file end
        Util.setCurrentOffset(fileLength);
        //*****Append pseudo box info(HTC_INFO_BOX)                                                                                            
        //put 0 at first
        Log.d(TAG, "HTC_INFO_BOX(fake size) write bytes " + Util.writeInt(fc, 0));
        Log.d(TAG, "HTC_INFO_BOX(name) write bytes " + Util.writeString(fc, HTC_INFO_BOX));        
        Log.d(TAG, "HTC_DATA_SEMIVIDEO_MD(header) write bytes " + Util.writeString(fc, HtcZoeMetadata.HTC_DATA_SEMIVIDEO_MD));
        Log.d(TAG, "HTC_DATA_SEMIVIDEO_MD(content) write bytes " +  Util.writeBytes(fc, bytes));
        Log.d(TAG, "HTC_DATA_SEMIVIDEO_MD(footer) write bytes " + Util.writeString(fc, HtcZoeMetadata.HTC_DATA_SEMIVIDEO_MD));
        long motionDataOffset = Util.getCurrentOffset();
        //*****Append HMTA table and add HTC Data(Motion Data key) from HMTA footer
        //HMTA table ori data
        Log.d(TAG, "Append ori HMTA write bytes " + Util.writeBytes(fc, oriHMTA));
        long oriHMTAEndOffset = Util.getCurrentOffset();
        //Key: overwrite HMTA footer to add Motion Data section       
        long hMTaOffset = oriHMTAEndOffset - 4;
        Util.setCurrentOffset(hMTaOffset);
        Log.d(TAG, "HTC_DATA_SEMIVIDEO_MD(key name) write bytes " + Util.writeString(fc, HtcZoeMetadata.HTC_DATA_SEMIVIDEO_MD));
        //IsData        
        Log.d(TAG, "HTC_DATA_SEMIVIDEO_MD(IsData) write bytes " + Util.writeInt(fc, 1));
        //VALUE_SIZE
        int valSize = sUSE_32BIT_OFFSET?12:16;
        Log.d(TAG, "HTC_DATA_SEMIVIDEO_MD(valSize) write bytes " +  Util.writeInt(fc, valSize));
        //VALUE
        //VALUE_Index
        Log.d(TAG, "HTC_DATA_SEMIVIDEO_MD(VALUE_Index) write bytes " + Util.writeInt(fc, 0));
        //VALUE_offset:bitscheck(sUSE_32BIT_OFFSET) to know if use putlong
      //fileLength + 8 is for pseudo box header
        if(sUSE_32BIT_OFFSET){																																		
            Log.d(TAG, "HTC_DATA_SEMIVIDEO_MD(VALUE_offset) write bytes " + Util.writeInt(fc, (int) (fileLength + 8)));
        } else {
            Log.d(TAG, "HTC_DATA_SEMIVIDEO_MD(VALUE_offset_long) write bytes " + Util.writeLong(fc, fileLength + 8));
        }

        //VALUE_size:MotionData size = input bytes + header/footer size
        Log.d(TAG, "HTC_DATA_SEMIVIDEO_MD(VALUE_size) write bytes " + Util.writeInt(fc, bytes.length + 8));
        //MetaData Key:Bit rate key
        if(null != bitRate){
            Log.d(TAG, "HTC_METADATA_BITRATE(key name) write bytes " + Util.writeString(fc, HtcZoeMetadata.HTC_METADATA_BITRATE));
            //IsData(0)
            Log.d(TAG, "HTC_METADATA_BITRATE(IsData) write bytes " + Util.writeInt(fc, 0));
            //VALUE_SIZE(HTC_METADATA vale size is 4)
            Log.d(TAG, "HTC_METADATA_BITRATE(VALUE_size) write bytes " +  Util.writeInt(fc, 4));
            //VALUE
            Log.d(TAG, "HTC_METADATA_BITRATE(VALUE) write bytes " + Util.writeInt(fc, (Integer) bitRate));
        }
        
        //MetaData Key:Frame drop ratio key
        if(null != frameDropRatio){
            Log.d(TAG, "HTC_METADATA_FRAME_DROP_RATIO(key name) write bytes " + Util.writeString(fc, HtcZoeMetadata.HTC_METADATA_FRAME_DROP_RATIO));
            //IsData(0)
            Log.d(TAG, "HTC_METADATA_FRAME_DROP_RATIO(IsData) write bytes " + Util.writeInt(fc, 0));
            //VALUE_SIZE(HTC_METADATA vale size is 4)
            Log.d(TAG, "HTC_METADATA_FRAME_DROP_RATIO(VALUE_size) write bytes " +  Util.writeInt(fc, 4));
            //VALUE
            Log.d(TAG, "HTC_METADATA_FRAME_DROP_RATIO(VALUE) write bytes " + Util.writeInt(fc, (Integer) frameDropRatio));
        }
    
        //HMTA footer
        Log.d(TAG, "HMTA(footer) write bytes " +  Util.writeString(fc, HTC_TABLE_TAG));
        hMTaOffset = Util.getCurrentOffset();

        //*****Update HMTA table offset and size
        //offset
        //+8 is htcb header rule
        long localShift = sHtcTableOffset + 8;
        Util.setCurrentOffset(localShift);        
        if(sUSE_32BIT_OFFSET){
            Log.d(TAG, "HMTA(offset) write bytes " +  Util.writeInt(fc, (int) motionDataOffset));
        }  else {
            Log.d(TAG, "HMTA(offset_long) write bytes " +  Util.writeLong(fc, motionDataOffset));
        }            
        long hMTAEnhanceSize = hMTaOffset - oriHMTAEndOffset;
        //size
        Log.d(TAG, "HMTA(size) write bytes " +  Util.writeInt(fc, (int)(oriHMTA.length + hMTAEnhanceSize)));

        //*****Update pseudo box size (int)(oriHMTA.length + hMTAEnhanceSize + bytes.length(motion data size) + 8(motion data header/footer) + 8(HTC_INFO_BOX size/name)) 
        Util.setCurrentOffset(fileLength);        
        Log.d(TAG, "HTC_INFO_BOX(real size) write bytes " +  Util.writeInt(fc, (int)(oriHMTA.length + hMTAEnhanceSize + bytes.length + 8 + 8)));
    
        //*****Save file and return true
        if(null != raf){
            raf.close();
            raf = null;
        }
        return true;		
    }

    /**
     * Note that this api is sync, non thread safe
     * @param inMpg
     * @param bytes
     * @return 
     * @throws IOException 
     */
    public static boolean mergeMotionData(String inMpg, byte[] bytes) throws IOException{
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(HtcZoeMetadata.HTC_DATA_SEMIVIDEO_MD, bytes);
        return mergeUserData(inMpg, map);	
    }
    
	//TODO: reduce extraction/reference part
	//Copy and reduce from HtcZoeExtractor ++
	private static boolean parseChunk(final FileChannel fc,final long[] offset,final int depth) throws IOException {
	    Log.d(TAG, "entering parseChunk "+ offset[0] +"/"+ depth);

	    long data_offset = offset[0];

        long chunk_size = Util.readUInt(fc, data_offset);        
        int chunk_type =  Util.readInt(fc, data_offset+4);

        Log.v(TAG, chunk_type + " " + chunk_size);
        char sChunk[] = new char[4];
        Util.MakeFourCCString(chunk_type, sChunk);
        Log.v(TAG, String.format("chunk %s", String.valueOf(sChunk)));

        data_offset += 8;
       // long chunk_data_size = offset[0] + chunk_size - data_offset;

        if (chunk_size == 1) {

            chunk_size = Util.readLong(fc, data_offset);

            data_offset += 8;

            if (chunk_size < 16) {
                Log.e(TAG," The smallest valid chunk is 16 bytes long in this case.");
                return false;
            }

        } else if (chunk_size < 8) {
            Log.e(TAG,"The smallest valid chunk is 8 bytes long.");
            return false;
        }

        Log.v(TAG, String.format("parsing chunk %s at depth %d",
                String.valueOf(sChunk), depth));

        if(chunk_type == Util.FOURCC('m', 'o', 'o', 'v') || chunk_type == Util.FOURCC('u', 'd', 't', 'a')) {
            Log.d(TAG, String.valueOf(sChunk));

            long stop_offset = offset[0] + chunk_size;
            offset[0] = data_offset;
            while (offset[0] < stop_offset) {
                boolean err = parseChunk(fc,offset, depth + 1);
                if (!err) {
                    return err;
                }
            }

            if (offset[0] != stop_offset) {
                return false;
            }

            if (chunk_type == Util.FOURCC('m', 'o', 'o', 'v') ) {
                return true;
            }
        }
        else if(chunk_type == Util.FOURCC('h', 't', 'c', 'b')) {
                Log.d(TAG,"Found Htc MetaData");
                sHtcTableOffset = data_offset;
                Log.d(TAG,"sHtcTableOffset = " + sHtcTableOffset );
                parseHtcMetaData(fc,data_offset,chunk_size);
                Log.d(TAG,"Parse Htc MetaData done");
                offset[0] += chunk_size;
        } else if (chunk_type == Util.FOURCC('_', 'h', 't', 'c')
                || chunk_type == Util.FOURCC('d', 't', 'a', 'h')) {
            Log.d(TAG, "Found HTC box");
            offset[0] += 8;
            parseChunk(fc, offset, depth + 1);
        }  else {
            offset[0] += chunk_size;
        }
        return true;
    }
	//Copy and reduce from HtcZoeExtractor
	private static boolean parseHtcMetaData(final FileChannel fc, final long offset, final long size) throws IOException {

	       int version = 0;
	       boolean nBitsCheck = true;
	       long nOffset = 0;
	       int nSize = 0;

	       byte[] buffer = Util.read(fc, offset, (int)size);
	       ByteArrayInputStream bis = new ByteArrayInputStream(buffer);

	      version = Util.readInt(bis);;

	       nBitsCheck =Util.readInt(bis)==1? true:false;

	       if(version != 1) {
	           Log.e(TAG, "Htc Table version("+ version+") incorrect!! Skip parsing Htc table");
	           return true;
	       }

	       if(nBitsCheck) {
	           nOffset = Util.readUInt(bis);
	       }else {
	           nOffset = Util.readLong(bis);
	       }

	       nSize = Util.readInt(bis);
	       
	       sUSE_32BIT_OFFSET = nBitsCheck;
	       sHMTA_Offset = nOffset;
	       sHMTA_Size = nSize;

	       Log.d(TAG, "version "+version+", use32Bits "+ nBitsCheck);
	       Log.v(TAG, "offset "+nOffset+", size " +nSize);

	       bis.close();

	       return true;
	    }	   
}
