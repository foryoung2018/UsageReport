package com.htc.libfeedframework.service;

import android.os.Bundle;

/**
 * IFeedServiceCallback is for FeedService callback to host implementation.
 **/
interface IFeedServiceCallback {

    int getHostPropertyInt(in String strKey);
    boolean getHostPropertyBoolean(in String strKey);
    String getHostPropertyString(in String strKey);
    String getHostType();

    Bundle callHostCommand(in int nCommandId, inout Bundle bundleCommandArgument);
}
