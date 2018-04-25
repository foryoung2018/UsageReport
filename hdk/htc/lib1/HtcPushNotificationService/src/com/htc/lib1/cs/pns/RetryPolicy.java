
package com.htc.lib1.cs.pns;

/**
 * Interface of retry strategies.
 * 
 * @author samael_wang@htc.com
 */
public interface RetryPolicy {

    /**
     * Schedule a registration retry after a registration failure occurs.
     * 
     * @param cause Failure cause.
     * @param registrationPolicy Registration policy to use.
     * @param allowAlarms {@code true} to allow schedule alarms.
     * @param retryAfterInSec a time shift value in seconds assigned by server, {@code -1} as default
     */
    void scheduleRegistrationRetry(Exception cause,
                                   RegistrationPolicy registrationPolicy, boolean allowAlarms, int retryAfterInSec);

    /**
     * Schedule an update retry after an update failure occurs.
     * 
     * @param cause Failure cause.
     * @param registrationPolicy Registration policy to use.
     * @param allowAlarms {@code true} to allow schedule alarms.
     * @param retryAfterInSec a time shift value in seconds assigned by server, {@code -1} as default
     */
    void scheduleUpdateRetry(Exception cause,
                             RegistrationPolicy registrationPolicy, boolean allowAlarms, int retryAfterInSec);
    
    /**
     * Schedule a unregistration retry after a unregistration failure occurs.
     * 
     * @param cause Failure cause.
     * @param registrationPolicy Registration policy to use.
     * @param allowAlarms {@code true} to allow schedule alarms.
     * @param retryAfterInSec a time shift value in seconds assigned by server, {@code -1} as default
     */
    void scheduleUnregistrationRetry(Exception cause,
                                     RegistrationPolicy registrationPolicy, boolean allowAlarms, int retryAfterInSec);

}
