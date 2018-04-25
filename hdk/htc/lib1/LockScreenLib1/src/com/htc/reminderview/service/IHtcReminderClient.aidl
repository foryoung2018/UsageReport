package com.htc.reminderview.service;

import android.os.Bundle;

/**
  * IHtcReminderClient
  */
interface IHtcReminderClient {
    /**
      * onViewModeChange
      * @param viewMode int
      */
    void onViewModeChange(int viewMode);
    /**
      * unlock
      */
    void unlock();
    /**
      * sendCommand
      * @param action String
      * @param extras Bundle
      * @return Bundle
      */
    Bundle sendCommand(String action, in Bundle extras);
}
