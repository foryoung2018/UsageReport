
package com.htc.lib1.cs.httpclient;

import java.io.BufferedOutputStream;
import java.io.IOException;

import com.htc.lib1.cs.JsonUtils;
import com.htc.lib1.cs.httpclient.HttpConnection.HttpOutputStreamWriter;

/**
 * Convert an object to json string by {@code JsonUtils} and write it to the
 * output stream.
 * 
 * @author samael_wang@htc.com
 */
public class JsonOutputStreamWriter implements HttpOutputStreamWriter {
    private Object mObject;

    public JsonOutputStreamWriter(Object obj) {
        mObject = obj;
    }

    @Override
    public void writeTo(BufferedOutputStream ostream) throws IOException {
        if (mObject != null) {
            new StringOutputStreamWriter(JsonUtils.toJson(mObject)).writeTo(ostream);
        }
    }

}
