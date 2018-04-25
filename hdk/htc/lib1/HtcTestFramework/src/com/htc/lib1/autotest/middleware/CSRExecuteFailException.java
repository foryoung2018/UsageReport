package com.htc.lib1.autotest.middleware;

/**
 * CSRExecuteFailException is using on SREvent execute fail
 */
public class CSRExecuteFailException extends RuntimeException {

	private static final long serialVersionUID = -7992107542894846213L;

    /**
     * CSRExecuteFailException constructor
	 * @param msg Exception message
     */
	public CSRExecuteFailException(String msg) {
		super(msg);
	}
	
    /**
     * CSRExecuteFailException constructor
     * @param msg Exception message
	 * @param throwable Throwable object
     */
	public CSRExecuteFailException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
	
}
