package com.htc.lib2.activeservice;

import com.htc.lib2.activeservice.TransportModeRecord;

/**
 * Used for receiving realtime TransportMode data
 * when the transport mode, steps or MET changed.
 * */
public interface TransportModeListener {
    /**
     * Called about when a transport mode record is generated.
     * @return
     */
    public void onTransportModeChanged(TransportModeRecord r);
}
