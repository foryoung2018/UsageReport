package com.htc.reminderview.service;

import com.htc.reminderview.service.IHtcReminderClient;
import android.os.Bundle;

/**
 * IHtcReminderService
 */
interface IHtcReminderService {
    /**
     * registerClient
     * @param callback HtcReminderClient
     * @return viewMode int
     */
    int registerClient(IHtcReminderClient client);
    /**
     * unregisterClient
     * @param callback HtcReminderClient
     */
    void unregisterClient(IHtcReminderClient client);
    /**
     * registerViewMode
     * @param viewMode int
     */
    void registerViewMode(int viewMode);
    /**
     * unregisterViewMode
     * @param viewMode int
     */
    void unregisterViewMode(int viewMode);
    /**
     * Unlock
     */
    void unlock();
    /**
     * get View Mode
     * @return viewMode int
     */
    int getViewMode();
    /**
      * sendCommand
      * @param action String
      * @param extras Bundle
      * @return Bundle
      */
    Bundle sendCommand(String action, in Bundle extras);
}