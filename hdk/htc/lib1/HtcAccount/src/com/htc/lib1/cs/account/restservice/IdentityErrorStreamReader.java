
package com.htc.lib1.cs.account.restservice;

import com.google.gson.Gson;
import com.htc.lib1.cs.account.HtcAccountRestServiceException;
import com.htc.lib1.cs.account.HtcAccountRestServiceException.WrongDataCenterException;
import com.htc.lib1.cs.account.HtcAccountServiceErrorCode;
import com.htc.lib1.cs.account.restobj.ConfirmAccountInfo;
import com.htc.lib1.cs.httpclient.HttpConnection.HttpErrorStreamReader;
import com.htc.lib1.cs.httpclient.HttpException;
import com.htc.lib1.cs.httpclient.JSONObjectInputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Convert the error stream to identity service error exception.
 * 
 * @author samael_wang@htc.com
 */
public class IdentityErrorStreamReader implements HttpErrorStreamReader {
    private JSONObjectInputStreamReader mJSONReader = new JSONObjectInputStreamReader();

    @Override
    public HttpException readFrom(int statusCode, Map<String, List<String>> responseHeader,
                                  BufferedInputStream estream) {
        try {
            JSONObject jsonObj = mJSONReader.readFrom(statusCode, responseHeader, estream);
            HtcAccountServiceErrorCode errCode = HtcAccountServiceErrorCode.valueOf(jsonObj
                    .getInt("code"));
            String errString = jsonObj.getString("message");

            // Generate specialized exception if needed.
            if (errCode == HtcAccountServiceErrorCode.WrongDataCenter) {
                return new WrongDataCenterException(statusCode, jsonObj.getString("location"));
            } else if (errCode == HtcAccountServiceErrorCode.BadCaptcha) {
                String captchaImageKey = null;
                if (jsonObj.has("captchaImageKey")) {
                    captchaImageKey = jsonObj.getString("captchaImageKey");
                }

                String captchaUrl = null;
                if (jsonObj.has("captchaUrl")) {
                    captchaUrl = jsonObj.getString("captchaUrl");
                }
                return new BadCaptchaException(statusCode, captchaImageKey, captchaUrl, errString);
            } else if (errCode == HtcAccountServiceErrorCode.ConfirmRequired) {
                return new ConfirmRequiredException(statusCode, new Gson().fromJson(
                        jsonObj.toString(), ConfirmAccountInfo.class), errString);
            } else if (errCode == HtcAccountServiceErrorCode.NeedChooseAndConfirm) {
                JSONObject phoneObj = jsonObj.getJSONObject("phoneNumber");
                String captchaNonce = null;
                if (jsonObj.has("captchaNonce"))
                    captchaNonce = jsonObj.getString("captchaNonce");
                return new ChooseAndConfirmException(statusCode, phoneObj.getString("countryCode"),
                        phoneObj.getString("number"), jsonObj.getString("email"),
                        captchaNonce, errString);
            } else if (errCode == HtcAccountServiceErrorCode.SendToBackupEmail) {
                return new SendToBackUpEmailException(statusCode, jsonObj.getString("email"),
                        errString);
            }
            return new HtcAccountRestServiceException(statusCode, errCode, errString);
        } catch (JSONException e) {
            return new HttpException(statusCode, e.getMessage(), e);
        } catch (IOException e) {
            return new HttpException(statusCode, e.getMessage(), e);
        }
    }
}
