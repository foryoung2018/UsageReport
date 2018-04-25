package com.htc.lib1.dm.env;

import android.content.Context;
import android.text.TextUtils;
import com.htc.lib1.dm.logging.Logger;

/**
 * Useful information about the device.
 * <p>
 * Information specific to HEP ROMs and that must be obtained using hidden APIs.
 * 
 * @author brian_anderson
 *
 */
public class HepDeviceEnv {

  private static final Logger LOGGER = Logger.getLogger("[DM]",HepDeviceEnv.class);

  // --------------------------------------------------
  // Keys for obtaining values from system properties

  // Firmware...
  private static final String SYSTEM_KEY_DEVICE_MID = "ro.mid";
  private static final String SYSTEM_KEY_CID_KEY = "ro.cid";

  // Build info...
  private static final String SYSTEM_KEY_BUILD_DESCRIPTION_KEY = "ro.build.description";
  private static final String SYSTEM_KEY_PROJECT_NAME = "ro.build.project";

  // This has been replaced with ACC flags.
  private static final String SYSTEM_KEY_SENSE_VERSION = "ro.build.sense.version";

  // --------------------------------------------------

  // Singleton instance...
  private static HepDeviceEnv sInstance = null;
  
  // --------------------------------------------------

  private HepDeviceEnv(Context context) {
  }

  public static HepDeviceEnv get(Context context) {
    if (context == null) {
      throw new IllegalArgumentException("context is null");
    }

    synchronized (HepDeviceEnv.class) {

		  if (sInstance == null) {
			  sInstance = new HepDeviceEnv(context.getApplicationContext());
		    LOGGER.debug("Created new instance: ", sInstance);
		  }

		  return sInstance;
    }
  }


  // --------------------------------------------------
  // Hardware/firmware info...
  // --------------------------------------------------


  /**
   * The HTC device model id
   * 
   * @return the device model id or <code>null</code> if undefined
   */
  public String getDeviceModelId() {
    return SystemWrapper.SystemProperties.get(SYSTEM_KEY_DEVICE_MID, null);
  }

  /**
   * The HTC customer ID (channel through which the device is sold).
   * 
   * @return the CID or <code>null</code> if undefined
   */
  public String getCID() {
    return SystemWrapper.SystemProperties.get(SYSTEM_KEY_CID_KEY, null);
  }

  // --------------------------------------------------
  // Build info...
  // --------------------------------------------------


  /**
   * The HTC specific build description.
   * 
   * @return the build description or <code>null</code> if undefined
   */
  public String getBuildDescription() {
    return SystemWrapper.SystemProperties.get(SYSTEM_KEY_BUILD_DESCRIPTION_KEY, null);
  }

  /**
   * The HTC specific ROM version.
   * 
   * @return the ROM version or <code>null</code> if undefined
   */
  public String getRomVersion() {

    String buildDescription = getBuildDescription();

    if (TextUtils.isEmpty(buildDescription)) {
      return null;
    }

    StringBuffer romVersion = new StringBuffer();

    char ch;
    buildDescription = buildDescription.trim();
    for (int i = 0; i < buildDescription.length(); i++) {
      ch = buildDescription.charAt(i);
      if (ch == '.' || Character.isDigit(ch)) {
        romVersion.append(ch);
      }
      else {
        break;
      }
    }
    return romVersion.toString();
  }

  /**
   * The HTC (internal) project name.
   * 
   * @return the project name or <code>null</code> if undefined
   */
  public String getProjectName() {
    return SystemWrapper.SystemProperties.get(SYSTEM_KEY_PROJECT_NAME, null);
  }
  
  /**
   * The Sense version (using the legacy system property).
   * <p>
   * This is the legacy mechanism for accessing the Sense version using system properties.
   * This has been replaced in Sense 6 and beyond with ACC flags sense_version, extra_sense_version, ...
   *
   * @return the Sense version using the legacy system property of <code>null</code> if undefined
   */
  public String getLegacySenseVersion() {
    return SystemWrapper.SystemProperties.get(SYSTEM_KEY_SENSE_VERSION, null);
  }
}
