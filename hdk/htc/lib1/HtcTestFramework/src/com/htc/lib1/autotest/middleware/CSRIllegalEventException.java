package com.htc.lib1.autotest.middleware;

/**
 * CSRIllegalEventException is using on SREvent exceptions
 */
public class CSRIllegalEventException extends RuntimeException {

	private static final long serialVersionUID = 8828697936407024784L;

    /**
     * CSRIllegalEventException constructor.
	 * @param msg Exception message.
     */
	public CSRIllegalEventException(String msg) {
		super(msg);
	}
	
    /**
     * CSRIllegalEventException constructor.
	 * @param msg Exception message.
	 * @param throwable Throwable object.
     */
	public CSRIllegalEventException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

}
