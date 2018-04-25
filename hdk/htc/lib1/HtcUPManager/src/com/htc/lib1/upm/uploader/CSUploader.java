package com.htc.lib1.upm.uploader;

import java.util.List;
import android.content.Context;
import android.text.TextUtils;

//change to ReportAgent log for consistent Log tag
import com.htc.xps.pomelo.andrlib.LogLib;
import com.htc.xps.pomelo.andrlib.LogLib.SendResult;
import com.htc.xps.pomelo.log.LogPayload;
import com.htc.xps.pomelo.log.HandsetLogPKT;
import com.htc.xps.pomelo.log.DeviceInfo;
import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.upm.HtcUPDataUtils;
import com.htc.lib1.upm.Log;
import com.htc.lib1.upm.uploader.budget.BudgetManager;

public class CSUploader {

	private static final String TAG = "CSUploader";
	private Context mContext;
	private BudgetManager mBudgetManager;

	protected CSUploader(Context context, BudgetManager budgetManager) {
		mContext = context;
		mBudgetManager = budgetManager;
	}

	public boolean putReport(HandsetLogPKT envelope) {
		
		if(HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag){
			try{
				Log.d(TAG,toLimitedStringFrom(envelope));
			} catch(OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		
		LogLib logLib = new LogLib(mContext, HtcWrapHtcDebugFlag.Htc_DEBUG_flag, !HtcUPDataUtils.isShippingRom(mContext));
		
		if ( TextUtils.isEmpty(logLib.getServerHost()) ) {
			Log.i(TAG,"no log server url");
			return false;
		}
		
		int ret = SendResult.CONNECTFAILED.getValue();
		try {
			ret = logLib.sendLogEnvelope(envelope);
			Log.i(TAG,"returned value : "+ret);
		} catch (IllegalArgumentException e) {
			Log.i(TAG,"IllegalArgumentException happend during putReport");
			e.printStackTrace();
		}
		
		mBudgetManager.updateAppUsage(logLib.getTotalDownloadSize(), logLib.getTotalUploadSize(), "UploadPomeloLog");
		
		return ret == SendResult.SUCCESS.getValue();
	}
	
	private String toLimitedStringFrom(HandsetLogPKT envelope) {
		StringBuilder sb = new StringBuilder();
		// dump Device Info first
		sb.append("[envelope header]\n");
		DeviceInfo deviceInfo = envelope.device_info;
		if(deviceInfo != null)
			sb.append(deviceInfo.toString());
		
		// dump payload
		sb.append("[Previous 2 payloads of envelope] Note, data field is ignored.\n");
		List<LogPayload> payloadList = envelope.payload;
		for(int i=0; i<payloadList.size() && i<2; i++) {
			sb.append("=> AppId : ").append(payloadList.get(i).app_id).append("\n");
			sb.append("   Category : ").append(payloadList.get(i).category).append("\n");
			sb.append("   Timestamp : ").append(payloadList.get(i).timestamp).append("\n");
			String data = payloadList.get(i).data;
			if(data != null)
				sb.append("   Data : ").append(data).append("\n");
		}
		return sb.toString();
	}
}
