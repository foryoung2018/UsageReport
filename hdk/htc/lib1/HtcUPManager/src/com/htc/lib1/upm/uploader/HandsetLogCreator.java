package com.htc.lib1.upm.uploader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.htc.xps.pomelo.log.HandsetLogPKT;
import com.htc.xps.pomelo.log.LogPayload;
import com.htc.xps.pomelo.util.PacketUtil;
import com.htc.studio.bdi.log.codec.AsciiStringEncoder;
import com.htc.studio.bdi.log.schema.Attribute;
import com.htc.studio.bdi.log.schema.BDIPayload;
import com.htc.studio.bdi.log.schema.Event;
import com.htc.lib1.upm.HtcUPLocalStore;
import com.htc.lib1.upm.Log;

public class HandsetLogCreator implements HtcUPLocalStore.DataStoreGetter {
	private static final String TAG = "HandsetLogCreator";
	private Context mContext;
	private HandsetLogPKT.Builder mBuilder = new HandsetLogPKT.Builder();
	private int mDataCount;
	public HandsetLogCreator(Context context) {
		mContext = context;
		mBuilder.version("1.0");
		mBuilder.payload = new ArrayList<LogPayload>();
		mDataCount = 0;
	}
	
	@Override
    public void getData(String appID, String action, String category, String label, int value,
            String[] labels, String[] values, long timestamp) {
	    String finalString = "";
	    BDIPayload.Builder bDIBuilder = new BDIPayload.Builder();
	    List<Attribute> attributes = null;
	    Event.Builder eventBuilder = new Event.Builder();
	    if (action != null && action.length() > 0)
	        eventBuilder.action = action;
        if (labels != null && labels.length > 0) {
            int N = labels.length;
            attributes = new ArrayList<Attribute>(N);
            for (int i = 0 ; i < N ; i++) {
                attributes.add(new Attribute(labels[i], values[i]));
            }
        } else {
            if (label != null && label.length() > 0)
                eventBuilder.label = label;
            if (value >= 0)
                eventBuilder.value = (long) value;
        }
        bDIBuilder.event = eventBuilder.build();
        if (attributes != null)
            bDIBuilder.attr = attributes;
        
        //Log.d(TAG, "BDI: " + bDIBuilder.build().toString());
        String encode = AsciiStringEncoder.encode(bDIBuilder.build().toByteArray());
        
        JSONObject jObject = new JSONObject();
        try {
            jObject.put("bdi", encode);
        } catch (JSONException jsone) {
            Log.e(TAG, "Failed to put BDIPayload string to JSON object", jsone);
            try {
                jObject.put("bdi", "UNKNOWN");
            } catch (JSONException e) { }
        }
        if (jObject.length() > 0) {
            //Log.d(TAG, "Key-Value: " +jObject.toString());
            finalString = jObject.toString();
        }
        
        LogPayload.Builder payload = new LogPayload.Builder();
        payload.app_id("bdi." + appID).category(category).timestamp(timestamp).data(finalString);
        
        mBuilder.payload.add(payload.build());
        mDataCount ++;
    }
	
	@Override
	public void clearAllData() {
		Log.d(TAG, "[clearAllData] Out of memory error occurs, clear all data in HandsetLogCreator!");
		if (mBuilder != null && mBuilder.payload != null) {
			mBuilder.payload.clear();
			mDataCount = 0;
		}
	}
	
	public int getDataCount() {
	    return mDataCount;
	}
	
	public HandsetLogPKT toHandsetLog(){
		mBuilder.device_info(DeviceInfoHelper.getLogDeviceInfo(mContext));		  
		return PacketUtil.calcLogPacketCheckSum(mBuilder.build());
	}
  
}
