
package com.htc.lib1.cs.httpclient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.htc.lib1.cs.httpclient.HttpConnection.HttpInputStreamReader;

/**
 * Response handler to convert response body to a {@link JSONOject}.
 * 
 * @author samael_wang@htc.com
 */
public class JSONObjectInputStreamReader implements HttpInputStreamReader<JSONObject> {
    private StringInputStreamReader mReader = new StringInputStreamReader();

    @Override
    public JSONObject readFrom(int statusCode, Map<String, List<String>> responseHeader,
            BufferedInputStream istream) throws IOException {
        try {
            return new JSONObject(mReader.readFrom(statusCode, responseHeader, istream));
        } catch (JSONException e) {
            throw new ParseResponseException(e.getMessage(), e);
        }
    }

}
