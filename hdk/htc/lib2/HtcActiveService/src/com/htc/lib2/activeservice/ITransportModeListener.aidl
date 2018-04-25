package com.htc.lib2.activeservice;

import com.htc.lib2.activeservice.TransportModeRecord;

interface ITransportModeListener {
    void onTransportModeChanged(in TransportModeRecord r);
}