
package com.htc.lib1.cs.httpclient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.htc.lib1.cs.StringUtils.StringStreamReader;
import com.htc.lib1.cs.httpclient.HttpConnection.HttpInputStreamReader;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Simple reader which converts the response body to a single string.
 * 
 * @author samael_wang@htc.com
 */
public class StringInputStreamReader implements HttpInputStreamReader<String> {
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private StringStreamReader mReader = new StringStreamReader();

    @Override
    public String readFrom(int statusCode, Map<String, List<String>> responseHeader,
            BufferedInputStream istream) throws IOException {
        String str = mReader.read(istream);
        mLogger.verboseS(str);
        return str;
    }

}
