
package com.htc.lib1.cs.pns;

/**
 * Constants indicate the policy of when should PNS being registered.
 * 
 * @author samael_wang@htc.com
 */
public enum RegistrationPolicy {

    /**
     * PNS should be automatically registered on app's first launch, and keep
     * up-to-date only within app's process lifetime.
     */
    ALWAYS_REGISTER,

    /**
     * PNS should be registered only after an HTC Account has been signed in,
     * and should be unregister when the account signs out. Keep the
     * registration record up-to-date only within app's process lifetime.
     */
    REGISTER_ON_SIGNED_IN
}
