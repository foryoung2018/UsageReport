package com.htc.lib2.opensense.social;

import android.accounts.AccountsException;

/**
 * @hide
 */
public class PluginException extends AccountsException {

    private static final long serialVersionUID = -2383881731184426905L;

    /**
     * Creates a PluginException instance
     * 
     * @hide
     */
    public PluginException() {
        super();
    }

    /**
     * Creates a PluginException instance
     * 
     * @param message the given error message
     * 
     * @hide
     */
    public PluginException(String message) {
        super(message);
    }

    /**
     * Creates a PluginException instance
     * 
     * @param message the given error message
     * @param cause the given throwable
     * 
     * @hide
     */
    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a PluginException instance
     * 
     * @param cause the given throwable
     * 
     * @hide
     */
    public PluginException(Throwable cause) {
        super(cause);
    }
}
