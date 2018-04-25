
package com.htc.lib1.cs.httpclient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.htc.lib1.cs.StringUtils.StringStreamReader;
import com.htc.lib1.cs.httpclient.HttpConnection.HttpErrorStreamReader;

/**
 * Simple error handler which creates an instance of {@link HttpException}
 * directly with response code / body.
 * 
 * @author samael_wang@htc.com
 */
public class SimpleErrorStreamReader implements HttpErrorStreamReader {
    private StringStreamReader mReader = new StringStreamReader();

    @Override
    public HttpException readFrom(int statusCode, Map<String, List<String>> responseHeader,
            BufferedInputStream estream) {
        try {
            return new HttpException(statusCode, mReader.read(estream));
        } catch (IOException e) {
            return new HttpException(statusCode, e.getMessage(), e);
        }
    }

}
