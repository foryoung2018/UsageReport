
package com.htc.lib1.cs.pns;

import com.htc.lib1.cs.pns.RegInfo;

/**
 * Interface to interact with HTC push notification service client.
 * 
 * @author samael_wang@htc.com
 */
interface IPnsAgent {

    /**
     * Get the registration id of push notification service.
     * 
     * @return {@link RegInfo} or {@code null} if not registered yet.
     */
    RegInfo getRegInfo();
}
