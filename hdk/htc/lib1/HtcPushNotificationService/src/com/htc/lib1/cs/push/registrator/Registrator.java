
package com.htc.lib1.cs.push.registrator;

import com.htc.lib1.cs.pns.RegistrationPolicy;
import com.htc.lib1.cs.push.PushProvider;
import com.htc.lib1.cs.push.RegistrationCredentials;
import com.htc.lib1.cs.push.exception.ReRegistrationNeeddedException;
import com.htc.lib1.cs.push.exception.RegistrationFailedException;
import com.htc.lib1.cs.push.exception.UnregistrationFailedException;
import com.htc.lib1.cs.push.exception.UnregistrationNeededException;
import com.htc.lib1.cs.push.exception.UpdateRegistrationFailedException;
import com.htc.lib1.cs.push.httputils.PnsServiceUnavailableException;

/**
 * Interface of different push provider registrators.
 */
public interface Registrator {

    /**
     * Register to the associated push provider and HTC push notification
     * service.
     * 
     * @param registrationPolicy Registration policy to use.
     * @throws RegistrationFailedException If the registration failed.
     */
    RegistrationCredentials register(RegistrationPolicy registrationPolicy)
            throws RegistrationFailedException, PnsServiceUnavailableException;

    /**
     * Update the registration.
     * 
     * @param registrationPolicy Registration policy to use.
     * @throws UpdateRegistrationFailedException If update failed.
     * @throws ReRegistrationNeeddedException If the registered record is not
     *             found on server and a re-registration has to be triggered.
     * @throws UnregistrationNeededException If the policy requires HTC Account
     *             but the account has been removed. In this case it should be
     *             unregistered.
     */
    void update(RegistrationPolicy registrationPolicy)
            throws UpdateRegistrationFailedException, ReRegistrationNeeddedException,
            UnregistrationNeededException, PnsServiceUnavailableException;

    /**
     * Unregister from HTC push notification service and the associated push
     * provider..
     * 
     * @throws UnregistrationFailedException If unregistration failed.
     */
    void unregister() throws UnregistrationFailedException, PnsServiceUnavailableException;

    /**
     * Get the push provider associated push provider.
     * 
     * @return Push provider.
     */
    PushProvider getProvider();
}
