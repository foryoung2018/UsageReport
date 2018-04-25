package com.htc.lib2.activeservice;

import com.htc.lib2.activeservice.ITransportModeListener;
import com.htc.lib2.activeservice.TransportModeRecord;

import com.htc.lib2.activeservice.TransportRecordsQueryResult;

interface IActiveService {
    boolean registerTransportModeListener(ITransportModeListener l);
    void unregisterTransportModeListener(ITransportModeListener l);

    TransportModeRecord getLatestTransportMode();
    TransportRecordsQueryResult queryTransportModeRecords(long from, long to);

    boolean isSupported();
    boolean isEnabled();
    boolean enableWithPermission();
    boolean disableWithPermission();
}