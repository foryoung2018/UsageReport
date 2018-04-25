package com.htc.lib1.autotest.middleware;

/**
 * CSRSpyFailException is using on SRSpy fail
 */
public class CSRSpyFailException extends RuntimeException {

	private static final long serialVersionUID = -172176257704949249L;

    /**
     * CSRSpyFailException constructor
	 * @param msg Exception message
     */
	public CSRSpyFailException(String msg) {
		super(msg);
	}

    /**
     * CSRSpyFailException constructor
     * @param msg Exception message
	 * @param throwable Throwable object
     */
	public CSRSpyFailException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

}
