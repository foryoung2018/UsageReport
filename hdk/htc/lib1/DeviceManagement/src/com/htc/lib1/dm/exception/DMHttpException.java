package com.htc.lib1.dm.exception;

import com.google.api.client.http.HttpResponse;

/**
 * Created by Joe_Wu on 8/22/14.
 */
public class DMHttpException extends DMException {
    private HttpResponse httpResponse;

    public DMHttpException(HttpResponse httpResponse) {
        super(String.format("HTTP error code:%d msg:%s",httpResponse.getStatusCode(),httpResponse.getStatusMessage()));
        this.httpResponse = httpResponse;
    }

    public DMHttpException(String detailMessage, HttpResponse httpResponse) {
        super(detailMessage);
        this.httpResponse = httpResponse;
    }

    public DMHttpException(String detailMessage, Throwable throwable, HttpResponse httpResponse) {
        super(detailMessage, throwable);
        this.httpResponse = httpResponse;
    }

    public DMHttpException(Throwable throwable, HttpResponse httpResponse) {
        super(throwable);
        this.httpResponse = httpResponse;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }
}
