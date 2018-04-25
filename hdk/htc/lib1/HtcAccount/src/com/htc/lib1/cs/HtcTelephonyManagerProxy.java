
package com.htc.lib1.cs;

import java.lang.reflect.InvocationTargetException;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Proxy to access HTC framework's {@code HtcTelephonyManager} class by
 * reflection.
 * 
 * @author samael_wang@htc.com
 */
public class HtcTelephonyManagerProxy {
    public static final int PHONE_TYPE_NONE = 0;
    public static final int PHONE_TYPE_GSM = 1;
    public static final int PHONE_TYPE_CDMA = 2;
    public static final int PHONE_TYPE_SUB_GSM = 5;

    /**
     * phone slot to point which slot. the same definition as
     * PhoneContants.PHONE_SLOT_1
     */
    public static final int PHONE_SLOT1 = 10;

    /**
     * phone slot to point which slot. the same definition as
     * PhoneContants.PHONE_SLOT_2
     */
    public static final int PHONE_SLOT2 = 11;

    /**
     * The action id to answer current ringing call.
     */
    public static final int ACTION_ANSWER_RINGING_CALL = 1;

    /**
     * The action id to reject current ringing call.
     */
    public static final int ACTION_REJECT_RINGING_CALL = 2;

    /**
     * The action id to silence current ringing call.
     */
    public static final int ACTION_SILENCE_RINGING_CALL = 3;

    /**
     * The action id to reject current ringing call and send a message to
     * caller.
     */
    public static final int ACTION_SEND_MSG = 4;

    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private Class<?> mProxyClass;
    private Object mProxyObject;

    public HtcTelephonyManagerProxy() {
        try {
            mProxyClass = Class.forName("com.htc.service.HtcTelephonyManager");
            mProxyObject = mProxyClass.getMethod("getDefault", (Class[]) null)
                    .invoke(null, (Object[]) null);
        } catch (Exception e) {
            mLogger.debug(e);
        }
    }

    /**
     * Get device id (IMEI for GSM phones or MEID for CDMA phones) with given
     * {@code slot}.
     * 
     * @param slot SIM slot to operate on.
     * @return Device id or {@code null} if not able to access
     *         {@code HtcTelephonyManager}.
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public String getDeviceIdExt(int slot) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException {
        if (mProxyClass != null && mProxyObject != null) {
            return (String) mProxyClass.getMethod("getDeviceIdExt", new Class[] {
                    Integer.TYPE
            }).invoke(mProxyObject, new Object[] {
                    slot
            });
        }
        return null;
    }

    /**
     * Get subscriber id (IMSI for GSM phones) with given {@code slot}.
     * 
     * @param slot SIM slot to operate on.
     * @return Subscriber id or {@code null} if not able to access
     *         {@code HtcTelephonyManager}.
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public String getSubscriberIdExt(int slot) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        if (mProxyClass != null && mProxyObject != null) {
            return (String) mProxyClass.getMethod("getSubscriberIdExt", new Class[] {
                    Integer.TYPE
            }).invoke(mProxyObject, new Object[] {
                    slot
            });
        }
        return null;
    }

    /**
     * Get network PLMN.
     * 
     * @param slot SIM slot to operate on.
     * @return Network PLMN or {@code null} if not able to access
     *         {@code HtcTelephonyManager}.
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public String getNetworkOperatorExt(int slot) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        if (mProxyClass != null && mProxyObject != null) {
            return (String) mProxyClass.getMethod("getNetworkOperatorExt", new Class[] {
                    Integer.TYPE
            }).invoke(mProxyObject, new Object[] {
                    slot
            });
        }
        return null;
    }

    /**
     * Get operator PLMN.
     * 
     * @param slot SIM slot to operate on.
     * @return Operator PLMN or {@code null} if not able to access
     *         {@code HtcTelephonyManager}.
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public String getIccOperator(int slot) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        if (mProxyClass != null && mProxyObject != null) {
            return (String) mProxyClass.getMethod("getIccOperator", new Class[] {
                    Integer.TYPE
            }).invoke(mProxyObject, new Object[] {
                    slot
            });
        }
        return null;
    }

    /**
     * Check if the device is a dual GSM phone
     * 
     * @return {@code true} if the device is a dual GSM phone.
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public boolean dualGSMPhoneEnable() throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        if (mProxyClass != null && mProxyObject != null) {
            return (Boolean) mProxyClass.getMethod("dualGSMPhoneEnable", (Class[]) null).invoke(
                    null, (Object[]) null);
        }
        return false;
    }

    /**
     * Check if the device is a dual SIM / UIM phone
     * 
     * @return {@code true} if the device is a dual SIM / UIM phone.
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public boolean dualPhoneEnable() throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        if (mProxyClass != null && mProxyObject != null) {
            return (Boolean) mProxyClass.getMethod("dualPhoneEnable", (Class[]) null).invoke(
                    null, (Object[]) null);
        }
        return false;
    }

}
