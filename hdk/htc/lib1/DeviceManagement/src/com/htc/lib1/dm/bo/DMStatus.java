package com.htc.lib1.dm.bo;

/**
 * Created by Joe_Wu on 9/2/14.
 */
public class DMStatus {
    public static final int GET_CONFIG_INIT = 0;
    public static final int GET_CONFIG_CACHE_OK = 1;
    public static final int GET_CONFIG_CACHE_EXPIRED = 2;
    public static final int GET_CONFIG_RETRY = 3;
    public static final int GET_CONFIG_RUNNING = 4;

    public static final int PUT_PROFILE_INIT = 0;
    public static final int PUT_PROFILE_DONE = 1;
    public static final int PUT_PROFILE_RETRY = 2;
    public static final int PUT_PROFILE_RUNNING = 3;
}
