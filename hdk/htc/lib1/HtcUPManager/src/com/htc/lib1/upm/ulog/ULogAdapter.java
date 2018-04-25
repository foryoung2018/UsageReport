package com.htc.lib1.upm.ulog;

import android.os.Bundle;
import android.text.TextUtils;

import com.htc.lib1.upm.Common;
import com.htc.lib1.upm.Log;
import com.htc.studio.bdi.log.codec.AsciiStringEncoder;
import com.htc.studio.bdi.log.schema.Attribute;
import com.htc.studio.bdi.log.schema.BDIPayload;
import com.htc.studio.bdi.log.schema.Event;

import java.util.ArrayList;
import java.util.List;

public class ULogAdapter {
    private static final String TAG = "ULogAdapter";
    
    public static boolean sendByULog(Bundle data) {
        boolean result = false;
        WrapReusableULogData uLogData = null;
        try {
            //Get reusable ulog data by reflection.
            uLogData = WrapReusableULogData.obtain();
            //We need to set timestamp due to there is time difference.
            uLogData.setAppId("bdi." + data.getString(Common.APP_ID)).setCategory(data.getString(Common.EVENT_CATEGORY)).setTimestamp(data.getLong(Common.TIMESTAMP));
            
            BDIPayload.Builder bDIBuilder = new BDIPayload.Builder();
            Event.Builder eventBuilder = new Event.Builder();
            List<Attribute> attributes = null;
            String action = data.getString(Common.EVENT_ACTION);
            String label = data.getString(Common.EVENT_LABEL);
            long value = data.getInt(Common.EVENT_VALUE, -1); 
            String[] labels = data.getStringArray(Common.ATTRIBUTE_LABLE);
            String[] values = data.getStringArray(Common.ATTRIBUTE_EXTRA);
            
            if (!TextUtils.isEmpty(action))
                eventBuilder.action = action;
                       
            if (labels != null) {
            	int N = labels.length;
                attributes = new ArrayList<Attribute>(N);
                for (int i = 0 ; i < N ; i ++)
                    attributes.add(new Attribute(labels[i], values[i]));
            } else {
            	if (!TextUtils.isEmpty(label))
                    eventBuilder.label = label;
            	if (value >= 0)
            		eventBuilder.value = value;
            }
                        
            bDIBuilder.event = eventBuilder.build();
            if (attributes != null)
                bDIBuilder.attr = attributes;
            String encode = AsciiStringEncoder.encode(bDIBuilder.build().toByteArray());
            
            uLogData.addData("bdi", encode);
            WrapULog.log(uLogData);
            
        } catch (Exception e) {
            Log.e(TAG,"Failed to use ULog APIs", e);
        } finally {
            if (uLogData != null) {
                try {
                    uLogData.recycle();
                } catch (Exception e) {}
            }
        }
        return result;
    }
}
