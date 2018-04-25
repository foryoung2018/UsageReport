package com.htc.libfeedframework.service;

import java.util.List;
import android.os.Bundle;
import com.htc.libfeedframework.FeedData;
import com.htc.libfeedframework.service.IFeedServiceCallback;

/**
 * IFeedService is for FeedHost call FeedService to retrieve FeedData.
 **/
interface IFeedService {
    void register(IFeedServiceCallback callback);
    void unregister();

    List<FeedData> sync(in boolean bSyncToLatest);
    int truncateData(long nFeedId);
    Bundle onHostCommand(in int nCommandId, inout Bundle bundleCommandArgument);
}

