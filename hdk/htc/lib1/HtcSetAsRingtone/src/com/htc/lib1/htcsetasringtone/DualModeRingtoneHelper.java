package com.htc.lib1.htcsetasringtone;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.lib1.htcsetasringtone.util.Constants;

public class DualModeRingtoneHelper
{
    private static final String TAG = "RingtoneTrimmer/DualModeRingtoneHelper";

    public static HtcAlertDialog createDualModeDialog(Context context, DialogInterface.OnClickListener listener)
    {
        Resources r = context.getResources();
        String[] items = new String[] {String.format(r.getString(R.string.nn_slot_number), 1), String.format(r.getString(R.string.nn_slot_number), 2), r.getString(R.string.dual_mode_dialog_both_slots)};
        
        HtcAlertDialog.Builder builder = new HtcAlertDialog.Builder(context);
        builder.setTitle(R.string.dual_mode_dialog_title).setItems(items, listener).setCancelable(true);
        
        return builder.create();
    }

    public static boolean isDualModeExists(Context context)
    {
        if (null == context)
        {
            Log.d(TAG, "isDualModeExists context == null return false");
            return false;
        }

        final String CLASS_NAME = "android.telephony.TelephonyManager";
        final String ISMULTISIMENABLEDMETHOD = "isMultiSimEnabled";

        try
        {
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

            Class c = Class.forName(CLASS_NAME);
            Method getIsMultiSimEnabeMethod = c.getMethod(ISMULTISIMENABLEDMETHOD, null);
            Object isMultiSimEnabeResult = getIsMultiSimEnabeMethod.invoke(telephonyManager, null);
            boolean multiSimEnable = (Boolean) isMultiSimEnabeResult;
            Log.d(TAG, "isDualModeExists = " + multiSimEnable);
            return multiSimEnable;
        }
        catch (ClassNotFoundException e)
        {
            Log.d(TAG, "isDualModeExists ClassNotFoundException " + e);
            return false;
        }
        catch (NoSuchMethodException e)
        {
            Log.d(TAG, "isDualModeExists NoSuchMethodException " + e);
            return false;
        }
        catch (Exception e)
        {
            Log.d(TAG, "isDualModeExists Exception " + e);
            return false;
        }
    }

    public static String getCdmaType(Context context)
    {
        if (null == context)
        {
            Log.d(TAG, "getCdmaType context == null return null");
            return null;
        }

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null)
        {
            Log.d(TAG, "getCdmaType telephonyManager == null return MODE_WCDMA");
            return Constants.MODE_WCDMA;
        }

        Object subId = 0l;
        try
        {
            subId = getSubId();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Constants.MODE_WCDMA;
        }

        int phoneType = getCurrentPhoneType(telephonyManager, subId);
        Log.d(TAG, "getCdmaType phoneType = " + phoneType);

        if (phoneType == TelephonyManager.PHONE_TYPE_GSM)
        {
            return Constants.MODE_WCDMA;
        }
        else if (phoneType == TelephonyManager.PHONE_TYPE_CDMA)
        {
            return Constants.MODE_CDMA;
        }
        else
        {
            return Constants.MODE_WCDMA;
        }
    }

    /* Java code sample need reflection.
       import android.telephony.SubscriptionManager;
       long[] slot1ids[] = SubscriptionManager.getSubId(PhoneConstants.SUB1);
       if(null != slot1ids && slot1ids.length > 0)
       {
           long subid1 = slog1ids[0]
       }
     */
    private static Object getSubId() throws Exception
    {
        int slotId = 0; // Sim 1 [PhoneConstants.SUB1];

        String methodName = "getSubId";
        String className = "android.telephony.SubscriptionManager";
        Object subId = null;

        try
        {
            Class classSubscriptionManager = Class.forName(className);
            Class<?>[] parmType = { int.class };
            Object[] parmInput = { slotId };
            subId = classSubscriptionManager.getDeclaredMethod(methodName, parmType).invoke(null, parmInput);
        }
        catch (ClassNotFoundException e)
        {
            Log.d(TAG, "getSubId ClassNotFoundException " + e);
            e.printStackTrace();
        }
        catch (Exception e)
        {
            Log.d(TAG, "getSubId Exception " + e);
            e.printStackTrace();
            throw new Exception("getSubId fail");
        }

        if (subId != null)
        {
        	if (android.os.Build.VERSION.SDK_INT >= 22) {
        		if (((int[]) subId).length >= 1) {
        			Log.d(TAG, "getSubId subId " + ((int[]) subId)[0]);
        			return ((int[]) subId)[0];
        		}
        	} else {
        		if (((long[]) subId).length >= 1) {
        			Log.d(TAG, "getSubId subId " + ((long[]) subId)[0]);
        			return ((long[]) subId)[0];
        		}
        	}
        }

        throw new Exception("getSubId fail subId invalid");
    }

    /* Java code sample need reflection.
       public int getCurrentPhoneType(long subId)
     */
    private static int getCurrentPhoneType(TelephonyManager telephonyManager,Object subId)
    {
        int phoneType = TelephonyManager.PHONE_TYPE_NONE;
        String methodName = "getCurrentPhoneType";
        try
        {
        	Class classTelephonyManager = TelephonyManager.class;
        	Class<?>[] parmType = {android.os.Build.VERSION.SDK_INT >= 22 ? int.class : long.class};
            Object[] parmInput = { subId };
            phoneType =(Integer) classTelephonyManager.getDeclaredMethod(methodName, parmType).invoke(telephonyManager, parmInput);
        }
        catch (Exception e)
        {
            Log.d(TAG, "getCurrentPhoneType Exception " + e);
            e.printStackTrace();
        }
        return phoneType;
    }
}
