
package com.htc.lib1.cs.pns;

import com.htc.lib1.cs.httpclient.HttpConnection.HttpErrorStreamReader;
import com.htc.lib1.cs.httpclient.HttpException;
import com.htc.lib1.cs.httpclient.JSONObjectInputStreamReader;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.pns.PnsRestServiceException.InvalidAuthKeyException;
import com.htc.lib1.cs.pns.PnsRestServiceException.TargetNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class PnsErrorStreamReader implements HttpErrorStreamReader {
    private JSONObjectInputStreamReader mJSONReader = new JSONObjectInputStreamReader();

    private HtcLogger mLogger = new CommLoggerFactory(this).create();

    @Override
    public HttpException readFrom(int statusCode, Map<String, List<String>> responseHeader,
            BufferedInputStream estream) {
        try {
            mLogger.debugS("statusCode=", statusCode, ", responseHeader = ", responseHeader);

            JSONObject jsonObj = mJSONReader.readFrom(statusCode, responseHeader, estream);
            PnsServiceErrorCode errCode = PnsServiceErrorCode.valueOf(jsonObj.getInt("code"));
            String errString = jsonObj.getString("message");

            if (statusCode == HttpURLConnection.HTTP_UNAVAILABLE) {
                int retryAfterValueInSec = PnsDefs.DEFAULT_RETRY_AFTER_VALUE_IN_SEC;
                if (responseHeader.containsKey(PnsDefs.HEADER_RETRY_AFTER)) {
                    List<String> values = responseHeader.get(PnsDefs.HEADER_RETRY_AFTER);
                    if (values != null && values.size() > 0) {
                        try {
                            retryAfterValueInSec = Integer.parseInt(values.get(0));

                            // prevent the retry-after value is negative
                            if (retryAfterValueInSec < 0) {
                                retryAfterValueInSec = PnsDefs.DEFAULT_RETRY_AFTER_VALUE_IN_SEC;
                            }
                        } catch (NumberFormatException e) {
                            mLogger.error(e);
                        }
                    }
                }
                mLogger.debug("Server assign a retry after value in seconds = ", retryAfterValueInSec);
                return new PnsServiceUnavailableException(statusCode, errCode, errString, retryAfterValueInSec);
            }

            // Generate specialized exception if needed.
            if (errCode == PnsServiceErrorCode.InvalidAuthKey)
                return new InvalidAuthKeyException(statusCode, errString);
            if (errCode == PnsServiceErrorCode.TargetNotFound)
                return new TargetNotFoundException(statusCode, errString);
            return new PnsRestServiceException(statusCode, errCode, errString);
        } catch (JSONException e) {
            return new HttpException(statusCode, e.getMessage(), e);
        } catch (IOException e) {
            return new HttpException(statusCode, e.getMessage(), e);
        }
    }

}
