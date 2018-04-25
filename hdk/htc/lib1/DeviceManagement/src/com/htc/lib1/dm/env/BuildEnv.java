package com.htc.lib1.dm.env;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;


/**
 * Build time defs.
 * 
 * @author brian_anderson
 *
 */
public class BuildEnv {

  // TODO: This should be a template that is substituted at build time.

  // --------------------------------------------------

  // Not instantiable...
  private BuildEnv() {
  }

  // --------------------------------------------------

  /**
   * Whether security debugging is enabled in this build.
   * <p>
   * Useful for conditionally compiling security debug code into the build.
   */
  
  public static boolean SECURITY_DEBUG_BUILD = HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag;

}
