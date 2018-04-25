
package com.htc.lib1.cs.account;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.htc.lib1.cs.account.HtcAccountRestServiceException.TokenExpiredException;
import com.htc.lib1.cs.account.HtcAccountRestServiceException.WrongDataCenterException;
import com.htc.lib1.cs.httpclient.HttpConnection.HttpErrorStreamReader;
import com.htc.lib1.cs.httpclient.HttpException;
import com.htc.lib1.cs.httpclient.JSONObjectInputStreamReader;

/**
 * Convert the error stream to identity service error exception.
 * 
 * @author samael_wang@htc.com
 */
public class HtcAccountRestErrorStreamReader implements HttpErrorStreamReader {
    private JSONObjectInputStreamReader mJSONReader = new JSONObjectInputStreamReader();

    @Override
    public HttpException readFrom(int statusCode, Map<String, List<String>> responseHeader,
            BufferedInputStream estream) {
        try {
            JSONObject jsonObj = mJSONReader.readFrom(statusCode, responseHeader, estream);
            HtcAccountServiceErrorCode errCode = HtcAccountServiceErrorCode.valueOf(jsonObj.getInt("Code"));
            String errString = jsonObj.getString("ErrorString");

            // Generate specialized exception if needed.
            if (errCode == HtcAccountServiceErrorCode.WrongDataCenter)
                return new WrongDataCenterException(statusCode, errString);
            else if (errCode == HtcAccountServiceErrorCode.TokenExpired)
                return new TokenExpiredException(statusCode, errString);
            return new HtcAccountRestServiceException(statusCode, errCode, errString);
        } catch (JSONException e) {
            return new HttpException(statusCode, e.getMessage(), e);
        } catch (IOException e) {
            return new HttpException(statusCode, e.getMessage(), e);
        }
    }

}
