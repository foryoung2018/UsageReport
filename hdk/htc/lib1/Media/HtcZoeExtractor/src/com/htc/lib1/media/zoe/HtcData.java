package com.htc.lib1.media.zoe;

import java.util.HashMap;

import android.util.Log;
import android.util.Pair;

/**
 * @hide
 * @author vin
 *
 */
class HtcData implements IHtcData{

    private final static String TAG = "HtcData";

    private HashMap<Integer, Pair<Long, Integer> > mMap = null;

    private int mCounts = 0;

    public HtcData(){
        mMap = new HashMap<Integer, Pair<Long, Integer> >();
    }


    protected void finalize() throws Throwable{
        if(mMap != null)
           mMap.clear();
        super.finalize();
    }

    //HashMap<Integer, Pair<Long, Integer>> mInfos;

    public int getCounts(){
        return mCounts;
    }

    public long getOffset(int index) throws IndexOutOfBoundsException {
        if(index >= this.getCounts() || index < 0){
            Log.d(TAG, this.getCounts()+ " "+ index);
            throw new IndexOutOfBoundsException();
        }

        Pair<Long, Integer> info = mMap.get(index);
        if(info == null)
            return -1;

        return info.first;
    }

    public int getLength(int index) throws IndexOutOfBoundsException {
        if(index >= this.getCounts() || index < 0)
            throw new IndexOutOfBoundsException();

        Pair<Long, Integer> info = mMap.get(index);
        if(info == null)
            return -1;
        return info.second;
    }

    protected void setInfo(int index, long offset, int size){
        if(index == -1){
            mCounts = size;
            return;
        }
        Pair<Long, Integer> info  = new Pair<Long, Integer>(offset, size);
        Log.d(TAG,index + " : "+ offset +" : "+ size);
        mMap.put(index, info);
    }


    protected boolean setDataInfoAsString(String text) throws IllegalArgumentException{
        if(text == null) return false;

        String infos[] = text.split(";");
        for(int i = 0; i< infos.length; i++){
            String info[] = infos[i].split(",");
            if(info.length != 3){
                throw new IllegalArgumentException("format of text is invaild");
            }
            setInfo(mMap.size(), Long.parseLong(info[1]),Integer.parseInt(info[2]));
        }
        this.validateDataCounts();
        return true;
    }

    protected void validateDataCounts(){
        int realCount = mMap.size();
        if(realCount != mCounts) {
           Log.e(TAG,"data counts is corrupted, exptect "+ mCounts +" but is " + realCount);
           mCounts = realCount;
        }
    }

};
