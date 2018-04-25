
package com.htc.lib1.cs.httpclient;

import java.io.BufferedOutputStream;
import java.io.IOException;

import org.json.JSONObject;

import com.htc.lib1.cs.httpclient.HttpConnection.HttpOutputStreamWriter;

/**
 * Writer to write a JSONObject to the HTTP output stream.
 * 
 * @author samael_wang@htc.com
 */
public class JSONObjectOutputStreamWriter implements HttpOutputStreamWriter {
    private JSONObject mJsonObj;

    public JSONObjectOutputStreamWriter(JSONObject jsonObj) {
        if (jsonObj == null)
            throw new IllegalArgumentException("'jsonObj' is null.");
        mJsonObj = jsonObj;
    }

    @Override
    public void writeTo(BufferedOutputStream ostream) throws IOException {
        new StringOutputStreamWriter(mJsonObj.toString()).writeTo(ostream);
    }

}
