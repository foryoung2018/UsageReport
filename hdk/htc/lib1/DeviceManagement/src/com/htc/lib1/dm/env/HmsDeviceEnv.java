package com.htc.lib1.dm.env;

import com.htc.lib0.customization.HtcWrapCustomizationManager;
import com.htc.lib0.customization.HtcWrapCustomizationReader;

import android.content.Context;
import com.htc.lib1.dm.logging.Logger;

/**
 * Useful information about the device.
 * <p>
 * Information that can be obtained using HDK APIs.
 * 
 * @author brian_anderson
 *
 */
public class HmsDeviceEnv {

  private static final Logger LOGGER = Logger.getLogger("[DM]",HmsDeviceEnv.class);

  // --------------------------------------------------
  // Customization flags...

  private static final String CUSTOMIZATION_CATEGORY_SYSTEM = "system";

  private static final String CUSTOMIZATION_KEY_SENSE_VERSION = "sense_version";
  private static final String CUSTOMIZATION_KEY_EXTRA_SENSE_VERSION = "extra_sense_version";
  private static final String CUSTOMIZATION_KEY_REGION = "region";
  private static final String CUSTOMIZATION_KEY_SKU_ID = "sku_id";

  // --------------------------------------------------
  // Values for ACC:system:region

  public static final int REGION_GLOBAL = 0;
  public static final int REGION_NORTH_AMERICA = 1;
  public static final int REGION_SOUTH_AMERICA = 2;
  public static final int REGION_CHINA = 3;
  public static final int REGION_JAPAN = 4;
  public static final int REGION_ASIA = 5;
  public static final int REGION_EUROPE = 6;
  public static final int REGION_ARABIC = 7;
  public static final int REGION_HK = 8;
  public static final int REGION_TW = 9;
  public static final int REGION_MMR = 10;
  public static final int REGION_PACIFIC = 11;
  public static final int REGION_MIDDLE_EAST = 12;
  public static final int REGION_AFRICA = 13;
  public static final int REGION_AUSTRALIA = 14;

  // --------------------------------------------------

  // Singleton instance...
  private static HmsDeviceEnv sInstance = null;
  
  // --------------------------------------------------

  private HtcWrapCustomizationReader systemCategoryReader;

  // --------------------------------------------------

  private HmsDeviceEnv(Context context) {
    HtcWrapCustomizationManager customizationMgr = new HtcWrapCustomizationManager();
    systemCategoryReader = customizationMgr.getCustomizationReader(CUSTOMIZATION_CATEGORY_SYSTEM, HtcWrapCustomizationManager.READER_TYPE_XML, false);
  }

  public static HmsDeviceEnv get(Context context) {
    if (context == null) {
      throw new IllegalArgumentException("context is null");
    }

    synchronized (HmsDeviceEnv.class) {

		  if (sInstance == null) {
			  sInstance = new HmsDeviceEnv(context.getApplicationContext());
		    LOGGER.debug("Created new instance: ", sInstance);
		  }

		  return sInstance;
    }
  }


  // --------------------------------------------------
  // Hardware/firmware info...
  // --------------------------------------------------


  // ...


  // --------------------------------------------------
  // Build info...
  // --------------------------------------------------


  /**
   * The region
   * <p>
   * @return the region ID or <code>null</code> if none defined.
   */
  public Integer getRegionID() {
    int value = systemCategoryReader.readInteger(CUSTOMIZATION_KEY_REGION, Integer.MIN_VALUE);
    if (value == Integer.MIN_VALUE) {
      return null;
    }
    else {
      return Integer.valueOf(value);
    }
  }
  
  /**
   * The SKU ID
   * <p>
   * @return the SKU ID or <code>null</code> if none defined.
   */
  public Integer getSkuID() {
    int value = systemCategoryReader.readInteger(CUSTOMIZATION_KEY_SKU_ID, Integer.MIN_VALUE);
    if (value == Integer.MIN_VALUE) {
      return null;
    }
    else {
      return Integer.valueOf(value);
    }
  }
  
  /**
   * The core Sense version number.
   *  
   * @return the core Sense version or <code>null</code> if unknown.
   */
  public String getSenseVersion() {
    return systemCategoryReader.readString(CUSTOMIZATION_KEY_SENSE_VERSION, null);
  }

  /**
   * The "extra" Sense version.
   * <p>
   * This variant includes additional information associated with the Sense version.
   * For example:
   * <pre>
   * Sense 6.0:           sense_version = 6.0       extra_sense_version = 6.0 
   * Sense 6.0a:          sense_version = 6.0       extra_sense_version = 6.0a 
   * Desire Sense 6.0:    sense_version = 6.0       extra_sense_version = desire6.0 
   * </pre>
   *  
   * @return the "extra" Sense version or <code>null</code> if unknown.
   */
  public String getExtraSenseVersion() {
    return systemCategoryReader.readString(CUSTOMIZATION_KEY_EXTRA_SENSE_VERSION, null);
  }
}
