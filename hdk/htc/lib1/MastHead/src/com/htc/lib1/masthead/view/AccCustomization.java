package com.htc.lib1.masthead.view;

import com.htc.lib0.customization.HtcWrapCustomizationManager;
import com.htc.lib0.customization.HtcWrapCustomizationReader;

public class AccCustomization {
	private static final String APPLICATION_SYSTEM = "System";
    private static final String FLAG_NAME_IS_CHINA_SENSE = "support_china_sense_feature";
    private static HtcWrapCustomizationReader sCustomizationReader = null;

    private static void ensureCustomizationReader() {
    	if (sCustomizationReader != null)
    		return;
        HtcWrapCustomizationManager customizationManager = new HtcWrapCustomizationManager();
        sCustomizationReader = customizationManager.getCustomizationReader(APPLICATION_SYSTEM, HtcWrapCustomizationManager.READER_TYPE_XML, false);    	
    }
    
    public static boolean isSupportChinaSense() {
    	ensureCustomizationReader();
    	return sCustomizationReader.readBoolean(FLAG_NAME_IS_CHINA_SENSE, false);
    }    
}
