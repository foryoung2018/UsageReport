/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htc.lib1.exo.upstream;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.net.Uri;

import com.htc.lib1.exo.utilities.LOG;


/**
 * A {@link HttpDataSource} that uses Android's {@link HttpURLConnection}.
 */
public class HTTPHelper {
    static private String TAG = "HTTPHelper";
    static private int connection_head(HttpURLConnection connection, HashMap<String, String> headers) throws IOException
    {
        connection.setRequestMethod("HEAD");
        connection.setConnectTimeout(1 * 1000);
        //connection.setRequestProperty("transferMode.dlna.org", "Streaming");
        synchronized (headers) {
          for (Map.Entry<String, String> property : headers.entrySet()) {
            connection.setRequestProperty(property.getKey(), property.getValue());
          }
        }
        connection.connect();
        int response = connection.getResponseCode();
        return response;   
    }

    static public String getContentType(Uri uri, HashMap<String, String> headers){
        try {
            URL url = new URL(uri.toString());
            return getContentType((HttpURLConnection)url.openConnection(), headers);
        } catch (Exception e) {                    
            LOG.W(TAG, e);
        }
        return null;
    }

    static public String getContentType(HttpURLConnection connection, HashMap<String, String> headers){
        String type = null;
        try {
            int response = connection_head(connection, headers);
            if (response == HttpURLConnection.HTTP_OK) {

                type = connection.getContentType();
                LOG.I(TAG, "init() response :" + response);
                LOG.I(TAG, "init() type :" + type);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }
}
