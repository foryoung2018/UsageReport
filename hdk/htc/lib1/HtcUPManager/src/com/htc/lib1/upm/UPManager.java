package com.htc.lib1.upm;

/**
 * @hide
 */
public interface UPManager {

    public void write(String appID, String category, String action , /*label*/ String stringValue, /*value*/int intValue) ;

    public void write(String appID, String category, String action, /*attribute labels*/String[] labels, /*attribute values*/String[] values);

    public void secureWrite(String appID, String category, String action , /*label*/ String stringValue, /*value*/int intValue);

    public void enableSendingOnNonHtcDevice(boolean isEnable) ;

    public void enableDebugLog(boolean enable);

}
