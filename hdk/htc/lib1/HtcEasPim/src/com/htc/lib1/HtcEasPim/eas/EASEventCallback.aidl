package com.htc.lib1.HtcEasPim.eas;

import android.os.Bundle;

/**
 * {@exthide}
 */
oneway interface EASEventCallback {

    /**
     * Notify observer that status changed
     *
     * @param msg The callback data
     */
    void callback(in Bundle msg);
}
