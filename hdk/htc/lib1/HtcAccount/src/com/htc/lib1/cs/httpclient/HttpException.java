
package com.htc.lib1.cs.httpclient;

/**
 * Represents an error from remote HTTP server.
 */
public class HttpException extends Exception {

    private static final long serialVersionUID = 1L;
    private int mStatusCode;

    /**
     * @param statusCode HTTP status code.
     */
    public HttpException(int statusCode) {
        mStatusCode = statusCode;
    }

    /**
     * @param statusCode HTTP status code.
     * @param message
     */
    public HttpException(int statusCode, String message) {
        super(message);
        mStatusCode = statusCode;
    }

    /**
     * @param statusCode HTTP status code.
     * @param message
     * @param cause
     */
    public HttpException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        mStatusCode = statusCode;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    @Override
    public String toString() {
        return new StringBuilder(this.getClass().getSimpleName())
                .append(" {status=\"").append(mStatusCode)
                .append("\", message=\"").append(getMessage())
                .append("\"}").toString();
    }
}
