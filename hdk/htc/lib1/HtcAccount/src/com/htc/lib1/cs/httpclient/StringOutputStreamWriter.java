
package com.htc.lib1.cs.httpclient;

import java.io.BufferedOutputStream;
import java.io.IOException;

import android.text.TextUtils;

import com.htc.lib1.cs.StringUtils.StringStreamWriter;
import com.htc.lib1.cs.httpclient.HttpConnection.HttpOutputStreamWriter;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Simple writer to write a given string to the output stream
 * 
 * @author samael_wang@htc.com
 */
public class StringOutputStreamWriter implements HttpOutputStreamWriter {
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private String mData;

    /**
     * @param data Data to write.
     */
    public StringOutputStreamWriter(String data) {
        mData = data;
    }

    @Override
    public void writeTo(BufferedOutputStream ostream) throws IOException {
        if (!TextUtils.isEmpty(mData)) {
            mLogger.verboseS(mData);
            new StringStreamWriter(ostream).write(mData);
        }
    }

}
