
package com.htc.lib1.cs.push;

import com.htc.lib0.customization.HtcWrapCustomizationManager;
import com.htc.lib0.customization.HtcWrapCustomizationReader;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * ACC configuration helper for push library.
 * 
 * @author autosun
 */
public class AccConfigHelper {
    private static final String CATEGORY_SYSTEM = "System";
    private static final String FLAG_REGION = "region";

    public static final int REGION_CODE_GLOBAL = 0;
    public static final int REGION_CODE_CHINA = 3;

    private static AccConfigHelper sInstance;

    public static synchronized AccConfigHelper getInstance() {
        if (sInstance == null)
            sInstance = new AccConfigHelper();
        return sInstance;
    }

    private HtcLogger mLogger = new PushLoggerFactory(this).create();
    private int mRomRegionId;

    private AccConfigHelper() {
        initialize();
    }

    /**
     * Read Acc Flags from ROM XML files, note that this is a blocking call.
     */
    private synchronized void initialize() {
        HtcWrapCustomizationManager customManager = new HtcWrapCustomizationManager();
        HtcWrapCustomizationReader reader = customManager.getCustomizationReader(CATEGORY_SYSTEM,
                HtcWrapCustomizationManager.READER_TYPE_XML, false);
        mRomRegionId = reader.readInteger(FLAG_REGION, REGION_CODE_GLOBAL);
        mLogger.debugS("Acc Flag: Rom Region = ", mRomRegionId);
    }

    public int getRomRegionId() {
        return mRomRegionId;
    }
}
