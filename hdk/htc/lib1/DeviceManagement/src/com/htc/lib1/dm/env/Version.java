package com.htc.lib1.dm.env;

import android.content.Context;
import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.dm.constants.Constants;
import com.htc.lib1.dm.logging.Logger;

/**
 * Information about the common library.
 * 
 * @author brian_anderson
 *
 */
public class Version {

  private static final Logger LOGGER = Logger.getLogger("[DM]",Version.class);

  // --------------------------------------------------

  // Singleton instance...
  private static Version sInstance = null;

  // --------------------------------------------------

  private Version(Context context) {
  }

  public static Version get(Context context) {
    if (context == null) {
      throw new IllegalArgumentException("context is null");
    }

    synchronized (Version.class) {

		  if (sInstance == null) {
			  sInstance = new Version(context.getApplicationContext());
		    LOGGER.debug("Created new instance: ", sInstance);
		  }

		  return sInstance;
    }
  }
  
  /**
   * A string suitable for use as a component of the User-Agent header
   * that the common library reports to servers.
   * 
   * @return descriptive information string for use in the User-Agent header
   */
  public String getUserAgentString() {
    String buildState = HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag ? Constants.LIBRARY_BUILD_STATE_DEBUG : Constants.LIBRARY_BUILD_STATE_RELEASE;
    return String.format("%s/%s (%s)", Constants.LIBRARY_PACKAGE_NAME, Constants.LIBRARY_VERSION_NAME, buildState );
  }
  
}
