package com.htc.customization;

public class HtcCustomizationManager {

    static public HtcCustomizationManager getInstance() {
        return new HtcCustomizationManager();
    }

    public HtcCustomizationReader getCustomizationReader(String name, int type, boolean needSIMReady) {
        return new HtcCustomizationReader();
    }

    public String readCID() {
        return "";
    }
}
