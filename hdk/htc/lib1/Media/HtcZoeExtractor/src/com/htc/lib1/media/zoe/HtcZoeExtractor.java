package com.htc.lib1.media.zoe;

import java.io.ByteArrayInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

import android.os.ParcelFileDescriptor;
import android.util.Log;



public class HtcZoeExtractor{

    private final static String TAG = "HtcZoeExtractor";

    private final static String HTC_TABLE_TAG = "HMTa";

    private boolean mInitCheck = false;
    private Object mLock = new Object();

    @SuppressWarnings("unused")
	private long mHtcTableOffset = 0;
    @SuppressWarnings("unused")
	private long mHtcTableSize = 0;
    @SuppressWarnings("unused")
	private int mHtcTableVersion = 0;
    @SuppressWarnings("unused")
	private long mHtcDataSize = 0;

    private ParcelFileDescriptor mPFd = null;

    private HashMap<String, byte[]> mHtcMetadataMap = new HashMap<String, byte[]>();
    private HashMap<String, HtcData> mHtcDataMap =  new HashMap<String,HtcData>();

    public HtcZoeExtractor(){
        Log.d(TAG,"constructor");
    }

    protected void finalize() throws Throwable{
        this.release();
        super.finalize();
    }

    /**
     *
     */
    public void release(){

        synchronized(mLock) {
            Log.d(TAG,"release");

            mInitCheck = false;
            mHtcDataMap.clear();
            mHtcMetadataMap.clear();

            mHtcTableOffset = 0;
            mHtcTableSize = 0;
            mHtcTableVersion = 0;
            mHtcDataSize = 0;

            if(mPFd != null){
                try {
                    mPFd.close();
                    mPFd = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private synchronized void setDataSourceFd(FileDescriptor fd) throws IOException{
        if(fd == null || !fd.valid())
            throw new IllegalArgumentException("fd is null or invalid");

        if(mPFd != null) {
            try {
                mPFd.close();
                mPFd = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mPFd = ParcelFileDescriptor.dup(fd);
        if(mPFd == null)
            throw new IllegalArgumentException("can't dup fd");
    }


    /**
     * Sets the data source (FileDescriptor) to use. It is the caller's
     * responsibility to close the file descriptor. It is safe to do so as soon
     * as this call returns. Call this method before the rest of the methods in
     * this class. This method may be time-consuming.
     *
     * @param fd the FileDescriptor for the file you want to play
     * @throws IllegalArgumentException if the FileDescriptor is invalid
     * @throws IOException
     * */

    public void setDataSource(FileDescriptor fd)
            throws IllegalArgumentException, IOException{

        synchronized(mLock){

            if(this.mInitCheck)
                this.release();

            this.setDataSourceFd(fd);

            this.readMetadata();
        }
    }

    /**
     * Sets the data source (file pathname) to use. Call this
     * method before the rest of the methods in this class. This method may be
     * time-consuming.
     *
     * @param path The path of the input media file.
     * @throws IllegalArgumentException If the path s invalid.
     * @throws IOException
     */
    public void setDataSource(String path) throws IllegalArgumentException, IOException{
        synchronized(mLock){

            if(this.mInitCheck)
                this.release();

            FileInputStream fis = null;
            FileDescriptor fd = null;
            try{
                fis = new FileInputStream(path);
                fd = fis.getFD();
                this.setDataSourceFd(fd);
                fis.close();
                this.readMetadata();
            }catch (FileNotFoundException fileEx) {
                throw new IllegalArgumentException();
            }
        }

    }

    /**
     *
     * @param key
     * @return
     * @throws IllegalArgumentException
     */
    public int extractHtcMetadataAsInt(String key) throws IllegalArgumentException{
        if(!HtcZoeMetadata.isMetadataKeyValidForInt(key))
            throw new IllegalArgumentException("Invalid key " + key);

          byte[] bs = extractHtcMetadata(key);
          if(bs == null || bs.length != 4)
            return-1;

          ByteBuffer bb = ByteBuffer.wrap(bs);
          try {
              return bb.getInt();
          }catch (Exception e) {
              e.printStackTrace();
              return -1;
          }
    }

    /**
     *
     * @param key
     * @return
     * @throws IllegalArgumentException
     */
    public String extractHtcMetadataAsString(String key) throws IllegalArgumentException{
        if(!HtcZoeMetadata.isMetadataKeyValidForString(key))
            throw new IllegalArgumentException("Invalid key "+ key);

        byte[] bs = extractHtcMetadata(key);
        if(bs == null)
            return null;

        String str = new String(bs);

        return str;

    }

    /**
     *
     * @param key
     * @return
     * @throws IllegalArgumentException
     */
    public byte[] extractHtcMetadataAsByteArray(String key) throws IllegalArgumentException{
        return extractHtcMetadata(key);
    }

    private synchronized byte[] extractHtcMetadata(String key) throws IllegalArgumentException{
        synchronized(mLock){

            if(key == null || key.length() != 4)
                throw new IllegalArgumentException("key format is invalid");

            return this.mHtcMetadataMap.get(key);
        }
    }

    /**
     *
     * @param key
     * @return
     * @throws IllegalArgumentException
     */
    public IHtcData extractHtcDataInformation(String key) throws IllegalArgumentException{
        if(!HtcZoeMetadata.isDataKeyValid(key))
            throw new IllegalArgumentException("Invalid key "+ key);

        return extractHtcData(key);
    }


    /**
     *
     * @param key
     * @return
     * @throws IllegalArgumentException
     */
    public int extractHtcDataCounts(String key) throws IllegalArgumentException{
        if(!HtcZoeMetadata.isDataKeyValid(key))
            throw new IllegalArgumentException("Invalid key "+ key);
        IHtcData data = extractHtcData(key);
        if(data != null)
            return data.getCounts();
        return 0;
    }

    /**
     *
     * @param key
     * @return
     */
    public boolean isMetadataExist(String key) {

        if(extractHtcMetadata(key) != null)
            return true;

        if(extractHtcData(key) != null)
            return true;

        return false;
    }

    /**
     * Check is zoe video.
     * same as isMetadataExist(HtcZoeMetadata.HTC_DATA_ZOE_JPEG)
     * @return
     */
    public boolean isZoe(){
        IHtcData data = extractHtcData(HtcZoeMetadata.HTC_DATA_ZOE_JPEG);
        if(data == null)
            return false;
        return true;
    }

    /**
     *
     * @param key
     * @param index
     * @return
     * @throws IllegalArgumentException
     */
    public InputStream extractHtcDataByIndex(String key, int index) throws IllegalArgumentException{
        if(!HtcZoeMetadata.isDataKeyValid(key))
            throw new IllegalArgumentException("Invalid key "+ key);

        IHtcData data = extractHtcData(key);
        if(data == null) {
            return null;
        }

        InputStream is = null;

        long offset = data.getOffset(index);
        int length = data.getLength(index);
        if(offset >=0 && length > 0)
        {
            is = this.genInputStream(key, offset, length);
        }
        return is;
    }

    /**
     *
     * @param key
     * @param index
     * @return
     * @throws IllegalArgumentException
     */
    public byte[] extractHtcDataByIndexAsByteArray(String key, int index) throws IllegalArgumentException{
        if(!HtcZoeMetadata.isDataKeyValid(key))
            throw new IllegalArgumentException("Invalid key "+ key);

        InputStream htcfi = extractHtcDataByIndex(key, index);
        if(htcfi == null){
            Log.e(TAG, "inputstream is null");
            return null;
        }

        IHtcData data = extractHtcData(key);
        if(data == null) {
            Log.e(TAG, "htcdata is null");
            return null;
        }

        long offset = data.getOffset(index);
        int length = data.getLength(index);

        byte bRes[] = null;
        if(offset >=0 && length > 0)
        {
            try {
                int readCount = 0;
                bRes = new byte[length-8];
                if((readCount = htcfi.read(bRes)) != length-8) {
                    Log.e(TAG, "read data error : except read "+ (length - 8) + " bytes but is "+ readCount +" bytes");
                    bRes = null;
                }
            }catch(Exception e) {
                e.printStackTrace();
            }finally{
                try {
                    if(htcfi != null)
                        htcfi.close();
                }catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            return bRes;
        }

        Log.d(TAG, "byte[] is null");
        return null;
    }

    /**
     *
     * @param key
     * @param index
     * @param fd
     * @return
     * @throws IllegalArgumentException
     */
    public boolean extractHtcDataToFile(String key, int index, FileDescriptor fd) throws IllegalArgumentException{
        if(!HtcZoeMetadata.isDataKeyValid(key))
            throw new IllegalArgumentException("Invalid key "+ key);

        if(fd == null || !fd.valid())
            throw new IllegalArgumentException("fd is Null or invalid");

          InputStream htcfi = extractHtcDataByIndex(key, index);
        if(htcfi == null)
            return false;

        IHtcData data = extractHtcData(key);
        if(data == null) {
            return false;
        }

        long offset = data.getOffset(index);
        int length = data.getLength(index);
        if(offset >=0 && length > 0)
        {
            FileOutputStream fs = null;
            boolean res = false;
            try {
                fs = new FileOutputStream(fd);
                int readCount = 0;
                int readSize = (length - 8) > 1*1024*1024 ? 1*1024*1024 : length-8;
                byte b[] = new byte[readSize];
                while((readCount = htcfi.read(b)) > 0) {
                     fs.write(b, 0, readCount);
                }
                res = true;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    if(htcfi != null)
                        htcfi.close();
                }catch (Exception e1) {
                    e1.printStackTrace();
                }
                try {
                    if(fs != null)
                        fs.close();
                }catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            return res;
        }

        return false;
    }
    
    /**
     * Check is Semi-Video
     * @return
     */
	public boolean isSemiVideo(){
		 IHtcData data = extractHtcData(HtcZoeMetadata.HTC_DATA_SEMIVIDEO_MD);
	        if(data == null)
	            return false;
	        return true;
	}

    private IHtcData extractHtcData(String key){
        synchronized(mLock){
            HtcData data = this.mHtcDataMap.get(key);
            if(data == null)
                return null;
            data.validateDataCounts();
            return data;
        }
    }

    private synchronized InputStream genInputStream(final String tag,final long offset,final int length) throws IllegalStateException {
        Log.d(TAG,"genInputStream");
        synchronized(mLock){
            Log.d(TAG, tag +" "+ offset + " "+ length);
            boolean result = true;

            FileInputStream fis = null;

            if(mPFd != null){
                FileDescriptor fd = mPFd.getFileDescriptor();
                if(fd!= null && fd.valid())
                    fis = new FileInputStream(fd);
                else
                    Log.e(TAG,"fd is null or invalid");
            }

            if(fis == null){
                Log.e(TAG, "fileinputstream is null");
                return null;
            }

            ByteArrayInputStream bas = null;

            try {

                FileChannel fc = fis.getChannel();
                byte checkR[] = read(fc, offset, 4);
                String tagS = new String(checkR);

                if(tag.compareTo(tagS) != 0){
                    Log.e(TAG,"check tag header : excepte is " + tag + " but is" + tagS);
                    result = false;
                }

                if(result){
                    checkR = read(fc, offset, 4);
                    tagS = new String(checkR);
                    if(tag.compareTo(tagS) != 0){
                        Log.e(TAG,"check tag footer : excepte is " + tag + " but is" + tagS);
                        result = false;
                    }
                }
                if(result){
                    byte buffer[] = read(fc, offset+4, length-8);
                    bas = new ByteArrayInputStream(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }finally{
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(!result){
                Log.e(TAG, "result is false");
                try {
                    if(bas != null)
                        bas.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }
             }
            return bas;
        }
    }

    /**
     *
     * @param fis
     * @return
     * @throws IOException
     */
    private synchronized void readMetadata() throws IOException{

        Log.d(TAG,"readMetadata");

        if(this.mInitCheck)
            return;

        FileInputStream fis = new FileInputStream(mPFd.getFileDescriptor());

        FileChannel fc = fis.getChannel();


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
        
        fis.close();

        mInitCheck = true;
        return;

    }

    private boolean parseChunk(final FileChannel fc,final long[] offset,final int depth) throws IOException {
        Log.d(TAG, "entering parseChunk "+ offset[0] +"/"+ depth);

        long data_offset = offset[0];

        long chunk_size = readUInt(fc, data_offset);
        int chunk_type =  readInt(fc, data_offset+4);

        Log.v(TAG, chunk_type + " " + chunk_size);
        char sChunk[] = new char[4];
        MakeFourCCString(chunk_type, sChunk);
        Log.v(TAG, String.format("chunk %s", String.valueOf(sChunk)));

        data_offset += 8;
        long chunk_data_size = offset[0] + chunk_size - data_offset;

        if (chunk_size == 1) {

            chunk_size = readLong(fc, data_offset);

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

        if(chunk_type == FOURCC('m', 'o', 'o', 'v') || chunk_type == FOURCC('u', 'd', 't', 'a')) {
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

            if (chunk_type == FOURCC('m', 'o', 'o', 'v') ) {
                return true;
            }
        }
        else if(chunk_type == FOURCC('h', 't', 'c', 'b')) {
                Log.d(TAG,"Found Htc MetaData");
                mHtcTableOffset = data_offset;
                mHtcDataSize = mHtcTableSize = chunk_size;
                parseHtcMetaData(fc,data_offset,chunk_size);
                Log.d(TAG,"Parse Htc MetaData done");
                offset[0] += chunk_size;
        } else if (chunk_type == FOURCC('_', 'h', 't', 'c')
                || chunk_type == FOURCC('d', 't', 'a', 'h')) {
            Log.d(TAG, "Found HTC box");
            offset[0] += 8;
            parseChunk(fc, offset, depth + 1);
        } else if (chunk_type == FOURCC('s', 'l', 'm', 't')) {
            Log.d(TAG, "Found slmt box");
            if (chunk_data_size < 4) {
                return false;
            }

            if (mHtcMetadataMap.containsKey(HtcZoeMetadata.HTC_METADATA_SLOW_MOTION)) {
                Log.v(TAG, "Tag ["+HtcZoeMetadata.HTC_METADATA_SLOW_MOTION+"] is duplicated, remove previous one.");
            }
            mHtcMetadataMap.put(HtcZoeMetadata.HTC_METADATA_SLOW_MOTION, ByteBuffer.allocate(4).putInt(1).array());
            offset[0] += chunk_size;
        } else if (chunk_type == FOURCC((char)0xA9, 'x', 'y', 'z')) {
            Log.d(TAG, "Found ©xyz box");
            // Best case the total data length inside "@xyz" box
            // would be 8, for instance "@xyz" + "\x00\x04\x15\xc7" + "0+0/",
            // where "\x00\x04" is the text string length with value = 4,
            // "\0x15\xc7" is the language code = en, and "0+0" is a
            // location (string) value with longitude = 0 and latitude = 0.
            if (chunk_data_size < 8) {
                return false;
            }
            // Worst case the location string length would be 18,
            // for instance +90.0000-180.0000, without the trailing "/" and
            // the string length + language code.
            //char[] buffer = new char[18];
            final int stringLength = 18;

            // Substracting 5 from the data size is because the text string length +
            // language code takes 4 bytes, and the trailing slash "/" takes 1 byte.
            long location_length = chunk_data_size - 5;
            if (location_length >= stringLength) {
                return false;
            }

            byte[] bytes = read(fc, data_offset+4, (int)location_length);
            if (mHtcMetadataMap.containsKey(HtcZoeMetadata.HTC_METADATA_KEY_LOCATION)) {
                Log.v(TAG, "Tag ["+HtcZoeMetadata.HTC_METADATA_KEY_LOCATION+"] is duplicated, remove previous one.");
            }
            mHtcMetadataMap.put(HtcZoeMetadata.HTC_METADATA_KEY_LOCATION, bytes);

            offset[0] += chunk_size;
        } else {
            offset[0] += chunk_size;
        }
        return true;
    }

    private boolean parseHtcMetaData(final FileChannel fc, final long offset, final long size) throws IOException {

       int version = 0;
       boolean nBitsCheck = true;
       long nOffset = 0;
       int nSize = 0;

       byte[] buffer = read(fc, offset, (int)size);
       ByteArrayInputStream bis = new ByteArrayInputStream(buffer);

       mHtcTableVersion = version = readInt(bis);;

       nBitsCheck =readInt(bis)==1? true:false;

       if(version != 1) {
           Log.e(TAG, "Htc Table version("+ version+") incorrect!! Skip parsing Htc table");
           return true;
       }

       if(nBitsCheck) {
           nOffset = readUInt(bis);
       }else {
           nOffset = readLong(bis);
       }

       nSize = readInt(bis);

       Log.d(TAG, "version "+version+", use32Bits "+ nBitsCheck);
       Log.v(TAG, "offset "+nOffset+", size " +nSize);

       bis.close();

       mHtcDataSize+=nSize;
       return parseHtcTable(fc, nOffset, nSize, nBitsCheck);
    }

    private boolean parseHtcTable(final FileChannel fc, final long offset, final int size,final boolean bitsCheck) throws IOException {

       byte[] header_tag = read(fc, offset, 4);

       String check = new String(header_tag);
       if(check.compareTo(HTC_TABLE_TAG) != 0){
           Log.e(TAG,"Table header incorrect!");
           return false;
       }

       byte[] footer_tag = read(fc, offset+size - 4, 4);
       check = new String(footer_tag);
       if(check.compareTo(HTC_TABLE_TAG) != 0){
           Log.e(TAG,"Table footer incorrect!");
           return false;
       }

       long data_offset = offset;
       data_offset+=4;

       int version = readInt(fc, data_offset);
       data_offset+=4;

       Log.d(TAG, "check "+check+" , version "+ version);

       boolean nIsData;
       int nSize;

      //data offset must less then length - 4 (footer check tag)
       while(data_offset < ( offset + size - 4) ) {

           byte bContext[] = read(fc, data_offset, 12);

           ByteArrayInputStream bis = new ByteArrayInputStream(bContext);
           byte bTag[] = read(bis, 4);
           String cTag = new String(bTag);

           //is data or metadata
           nIsData = readInt(bis) == 1? true: false;

           //data size
           nSize = readInt(bis);
           bis.close();

           Log.v(TAG,"Retriever Htc Data mType ="+ cTag +", isData = "+ nIsData +", dataSize "+ nSize);

           data_offset += 12;

           if(nIsData) {
               if(nSize == (bitsCheck?12:16)) {

                   bContext = read(fc, data_offset, nSize);
                   bis = new ByteArrayInputStream(bContext);

                   int nIndex = readInt(bis);
                   long dOffset;
                   int dSize;
                   if(bitsCheck) {
                       dOffset = readUInt(bis);
                       dSize = readInt(bis);
                   }else {
                       dOffset = readLong(bis);
                       dSize = readInt(bis);
                   }
                   bis.close();

                   boolean checkRes = true;
                   //index is -1, means ths size will be the counts of this data.
                   if(nIndex != -1)
                       checkRes = checkHtcData(fc, cTag, dOffset, dSize);

                   if(checkRes) {
                       mHtcDataSize += dSize;

                       boolean res = false;
                       boolean exist = mHtcDataMap.containsKey(cTag);
                       if(exist) {
                            HtcData value = mHtcDataMap.get(cTag);
                            if(value != null){
                                value.setInfo(nIndex, dOffset, dSize);
                                res = true;
                            }
                       }
                       if(!res) {
                            HtcData value = new HtcData();
                            value.setInfo(nIndex, dOffset, dSize);
                            mHtcDataMap.put(cTag, value);
                       }
                       //Log.v(TAG, String.format("%s %d, %lld, %d", cTag, nIndex, dOffset, dSize));
                   }
               } else
                   Log.e(TAG, "size of data info is not correct, expect"+ (bitsCheck?12:16) +" but "+ nSize);
           }else {
               if(nSize < 4 *1024 * 1024){
                   byte meta[] = read(fc, data_offset, nSize);
                   Log.v(TAG, "Tag ["+cTag+"] found.");
                   if(this.mHtcMetadataMap.containsKey(cTag)){
                        Log.v(TAG, "Tag ["+cTag+"] is duplicated, remove previous one.");
                        this.mHtcMetadataMap.remove(cTag);
                   }
                   this.mHtcMetadataMap.put(cTag, meta);
               }else {
                   Log.e(TAG, "size of metadata is over 4MB, not allowed to read");
               }
           }
           data_offset += nSize;
       }


       return true;
    }

    private boolean checkHtcData(final FileChannel fc, String tag, long offset, int size) throws IOException{

        byte bCheck[] = read(fc, offset, 4);

        String check = new String(bCheck);
        if(tag.compareTo(check) !=0 ){
            Log.e(TAG, "Tag check header tag failed expect"+ tag +" but is "+ check);
            return false;
        }

        bCheck = read(fc, offset + size - 4, 4);
        check = new String(bCheck);
        if(tag.compareTo(check) !=0 ){
            Log.e(TAG, "Tag check footer tag failed expect"+ tag +" but is "+ check);
            return false;
        }

        Log.v(TAG, "Tag check "+tag+" successed");
        return true;
    }

    /*private long getHtcDataSize(){
        return this.mHtcDataSize;
    }*/   

    private static int FOURCC(char in1, char in2, char in3, char in4){        
         return Util.FOURCC(in1, in2, in3, in4);
    }


    @SuppressWarnings("unused")
	private static int readInt(final ByteArrayInputStream bis, final long offset) throws IOException
    {
        return Util.readInt(bis, offset);
    }

    @SuppressWarnings("unused")
	private static long readLong(final ByteArrayInputStream bis, final long offset) throws IOException
    {
        return Util.readLong(bis, offset);
    }

    @SuppressWarnings("unused")
	private static byte[] read(final ByteArrayInputStream bis, final long offset, final int length) throws IOException{       
        return Util.read(bis, offset, length);
    }


    private static int readInt(final ByteArrayInputStream bis) throws IOException
    {
        return Util.readInt(bis);
    }
    
    private static long readUInt(final ByteArrayInputStream bis) throws IOException
    {
        return Util.readUInt(bis);
    }

    private static long readLong(final ByteArrayInputStream bis) throws IOException
    {
        return Util.readLong(bis);

    }

    private static byte[] read(final ByteArrayInputStream bis, final int length) throws IOException{        
        return Util.read(bis, length);
    } 

    private static int readInt(final FileChannel fc, final long offset) throws IOException
    {        
        return Util.readInt(fc, offset);
    }
    
    private static long readUInt(final FileChannel fc, final long offset) throws IOException
    {        
        return Util.readUInt(fc, offset);
    }

    private static long readLong(final FileChannel fc, final long offset) throws IOException
    {
        return Util.readLong(fc, offset);
    }

    private static byte[] read(final FileChannel fc, final long offset, final int length) throws IOException{       
        return Util.read(fc, offset, length);
    }

    private static void MakeFourCCString(int x, char s[]) {
       Util.MakeFourCCString(x, s);
    }
}
