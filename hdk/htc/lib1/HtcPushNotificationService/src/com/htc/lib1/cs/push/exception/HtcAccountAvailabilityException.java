
package com.htc.lib1.cs.push.exception;

/**
 * Indicates the registration failed because HTC Account is missing.
 * 
 * @author samael_wang@htc.com
 */
public class HtcAccountAvailabilityException extends RegistrationFailedException {
    private static final long serialVersionUID = 1L;

    public HtcAccountAvailabilityException(String message) {
        super(message);
    }

    public HtcAccountAvailabilityException(String message, Throwable cause) {
        super(message, cause);
    }

}
