package com.htc.lib1.upm;

/**
 * @hide
 */
final class NullUPManager implements UPManager {

    @Override
    public void write(String appID, String category, String action, String stringValue, int intValue) {
    }

    @Override
    public void write(String appID, String category, String action, String[] labels, String[] values) {
    }

    @Override
    public void secureWrite(String appID, String category, String action, String stringValue,
            int intValue) {
    }

    @Override
    public void enableSendingOnNonHtcDevice(boolean isEnable) {
    }

    @Override
    public void enableDebugLog(boolean enable) {
    }

}
