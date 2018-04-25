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

import com.google.android.exoplayer.C;
import com.google.android.exoplayer.upstream.DataSpec;
import com.google.android.exoplayer.upstream.TransferListener;
import com.google.android.exoplayer.util.Predicate;
import com.htc.lib1.exo.utilities.LOG;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link HttpDataSource} that uses Android's {@link HttpURLConnection}.
 */
public class DLNADataSource extends HtcHttpDataSource {
  static String TAG = "DLNADataSource";
  private static final Pattern DLNA_CONTENT_RANGE_HEADER =
      Pattern.compile("^npt=(\\d+)-(\\d+)?(\\/(\\*|d+))(\\s+)bytes=(\\d+)-(\\d+)\\/(\\d+)$");
  private static final String HEADER_TIMESEEKRANGE_DLNA_ORG = "TimeSeekRange.dlna.org";

  private static final String HEADER_TRANSFERMODE_DLNA_ORG = "transferMode.dlna.org";

  private static final String HEADER_GETCONTENTFEATURES_DLNA_ORG = "GetContentFeatures.dlna.org";

  private static final String HEADER_PRAGMA         = "Pragma";

  private static final String HEADER_CONTENT_LENGTH         = "Content-Length";

  private boolean bStallMode = false;
  private boolean bTimeSeek = false;
  private boolean bByteSeek = false;

  public DLNADataSource(String userAgent, Predicate<String> contentTypePredicate, Map<String, String> headers) {
    super(userAgent, contentTypePredicate, null);
    LOG.I(TAG,"DLNADataSource");
    init(headers);
  }

  public DLNADataSource(String userAgent, Predicate<String> contentTypePredicate,
      TransferListener listener, Map<String, String> headers) {
    super(userAgent, contentTypePredicate, listener, DEFAULT_CONNECT_TIMEOUT_MILLIS,
        DEFAULT_READ_TIMEOUT_MILLIS);
    LOG.I(TAG,"DLNADataSource");
    init(headers);
  }

  public DLNADataSource(String userAgent, Predicate<String> contentTypePredicate,
      TransferListener listener, int connectTimeoutMillis, int readTimeoutMillis, Map<String, String> headers) {
    super(userAgent, contentTypePredicate, listener, connectTimeoutMillis,
        readTimeoutMillis);
    LOG.I(TAG,"DLNADataSource");
    init(headers);
  }

  private void checkHeaders(Map<String, String> headers){
      if (headers == null) return;

      if (headers.containsKey("x-htc-dlna-stallmode"))
      {
        String sVlaue = headers.get("x-htc-dlna-stallmode");
        if (sVlaue != null && sVlaue.equals("1"))
        {
          bStallMode = true;
        }
      }

      if (headers.containsKey("x-htc-dlna-timeseek"))
      {
        String sVlaue = headers.get("x-htc-dlna-timeseek");
        if (sVlaue != null && sVlaue.equals("1"))
        {
          bTimeSeek = true;
        }
      }

      if (headers.containsKey("x-htc-dlna-byteseek"))
      {
        String sVlaue = headers.get("x-htc-dlna-byteseek");
        if (sVlaue != null && sVlaue.equals("1"))
        {
          bByteSeek = true;
        }
      }

      LOG.I(TAG, "checkHeaders bStallMode " + bStallMode);
      LOG.I(TAG, "checkHeaders bTimeSeek " + bTimeSeek);
      LOG.I(TAG, "checkHeaders bByteSeek " + bByteSeek);
  }

  @Override
  protected HttpURLConnection makeConnection(DataSpec dataSpec) throws IOException {
    LOG.I(TAG, "makeConnection[" + dataSpec.position + "-" + dataSpec.length + "] [" + dataSpec.positionInUs + "]" + LOG.getLineInfo());

    /*  we need to customized the byte seek header
    if (bTimeSeek == false){
       return super.makeConnection(dataSpec);
    }*/

    URL url = new URL(dataSpec.uri.toString());
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS);
    connection.setReadTimeout(DEFAULT_READ_TIMEOUT_MILLIS);
    connection.setDoOutput(false);
    
    //For CTT [7.4.3.2][7.4.4.6][start]
    if (bTimeSeek || bByteSeek || bStallMode){
      LOG.I(TAG,"set [transferMode.dlna.org=Streaming]");
      setRequestProperty(HEADER_TRANSFERMODE_DLNA_ORG,"Streaming");
    }
    //For CTT [7.4.3.2][7.4.4.6][end]
    
    {
      LOG.I(TAG,"set [GetContentFeatures.dlna.org=1]");
      setRequestProperty(HEADER_GETCONTENTFEATURES_DLNA_ORG,"1");
    }

    {
      LOG.I(TAG,"set [Pragma=getIfoFileURI.dlna.org]");
      setRequestProperty(HEADER_PRAGMA,"getIfoFileURI.dlna.org");
    }

    //For CTT [7.4.26.7][start]
    if (bTimeSeek ){
      //For CTT[7.4.13.1][start]
      setTimeRangeHeader(connection, dataSpec);
      //For CTT[7.4.13.1][end]
    }
    else if ( bByteSeek || bStallMode)
    {
      setByteRangeHeader(connection, dataSpec);
    }
    //For CTT [7.4.26.7][end]

    connection.setRequestProperty("User-Agent", "ExoPlayer");
    if ((dataSpec.flags & DataSpec.FLAG_ALLOW_GZIP) == 0) {
      connection.setRequestProperty("Accept-Encoding", "identity");
    }
    connection.connect();

    return connection;
  }

  @Override
  public long open(DataSpec dataSpec) throws HttpDataSourceException {
    this.dataSpec = dataSpec;
    this.bytesRead = 0;
    this.bytesSkipped = 0;
    try {
      connection = makeConnection(dataSpec);
    } catch (IOException e) {
      throw new HttpDataSourceException("Unable to connect to " + dataSpec.uri.toString(), e,
          dataSpec);
    }

    int responseCode;
    try {
      responseCode = connection.getResponseCode();
    } catch (IOException e) {
      closeConnection();
      throw new HttpDataSourceException("Unable to connect to " + dataSpec.uri.toString(), e,
          dataSpec);
    }

    // Check for a valid response code.
    if (responseCode < 200 || responseCode > 299) {
      Map<String, List<String>> headers = connection.getHeaderFields();
      closeConnection();
      throw new InvalidResponseCodeException(responseCode, headers, dataSpec);
    }

    // Check for a valid content type.
    String contentType = connection.getContentType();
    if (contentTypePredicate != null && !contentTypePredicate.evaluate(contentType)) {
      closeConnection();
      throw new InvalidContentTypeException(contentType, dataSpec);
    }

    dumpHTTPHeaders(connection);

    long contentLength = getContentLength(connection);
    contentStart = getContentStart(connection);
    getContentType(connection);

    // If we requested a range starting from a non-zero position and received a 200 rather than a
    // 206, then the server does not support partial requests. We'll need to manually skip to the
    // requested position.
      LOG.I(TAG,"open responseCode = " + responseCode );
      LOG.I(TAG,"open dataSpec.position = " + dataSpec.position );

      if (bTimeSeek == true && contentStart > 0 && dataSpec.position > 0 && dataSpec.position > contentStart){
        bytesToSkip = dataSpec.position - contentStart;
      } else {
        bytesToSkip = responseCode == 200 && dataSpec.position != 0 ? dataSpec.position : 0;
      }
    // Determine the length of the data to be read, after skipping.
    if ((dataSpec.flags & DataSpec.FLAG_ALLOW_GZIP) == 0) {
      //long contentLength = getContentLength(connection);
      bytesToRead = dataSpec.length != C.LENGTH_UNBOUNDED ? dataSpec.length
          : contentLength != C.LENGTH_UNBOUNDED ? contentLength - bytesToSkip
          : C.LENGTH_UNBOUNDED;
      LOG.I(TAG,"open contentLength (" + contentLength + ") - bytesToSkip(" + bytesToSkip + ") = bytesToRead(" + (contentLength - bytesToSkip) + ") / " + bytesToRead);
    } else {
      // Gzip is enabled. If the server opts to use gzip then the content length in the response
      // will be that of the compressed data, which isn't what we want. Furthermore, there isn't a
      // reliable way to determine whether the gzip was used or not. Always use the dataSpec length
      // in this case.
      bytesToRead = dataSpec.length;
    }

    try {
      inputStream = connection.getInputStream();
    } catch (IOException e) {
      closeConnection();
      throw new HttpDataSourceException(e, dataSpec);
    }

    opened = true;
    if (listener != null) {
      listener.onTransferStart();
    }

    return bytesToRead;
  }

  protected void setByteRangeHeader(HttpURLConnection connection, DataSpec dataSpec) {
    /*if (dataSpec.position == 0 && dataSpec.length == C.LENGTH_UNBOUNDED) {
      // Not required.
      return;
    }*/
    String rangeRequest = "bytes=" + dataSpec.position + "-";
    if (dataSpec.length != C.LENGTH_UNBOUNDED) {
      rangeRequest += (dataSpec.position + dataSpec.length - 1);
    }
    LOG.I(TAG,"set [ByteRangeHeader = " + rangeRequest + "]");
    connection.setRequestProperty("Range", rangeRequest);
  }

  //For CTT[7.4.13.1][start]
  private void setTimeRangeHeader(HttpURLConnection connection, DataSpec dataSpec) {
    if (bTimeSeek == false) {
      // Not required.
      return;
    }
    if (dataSpec.position == 0 && dataSpec.length == C.LENGTH_UNBOUNDED) {
      // Not required.
      return;
    }
    long positionInUs = dataSpec.positionInUs;
    long TimeAdjustInUs = 1000 * 1000 * 3;
    positionInUs = (positionInUs > TimeAdjustInUs) ? positionInUs - TimeAdjustInUs : positionInUs;
    float position = ((float)(positionInUs) / (TimeAdjustInUs));
    String rangeRequest = "npt=" + String.format("%.2f",position) + "-";
    /*if (dataSpec.length != C.LENGTH_UNBOUNDED) {
      rangeRequest += (dataSpec.position + dataSpec.length - 1);
    }*/
    LOG.I(TAG,"set [TimeSeekRange.dlna.org = "+rangeRequest + "]");
    connection.setRequestProperty(HEADER_TIMESEEKRANGE_DLNA_ORG, rangeRequest);
  }
  //For CTT[7.4.13.1][end]

  private void init(Map<String, String> headers){
    LOG.I(TAG,"init");

    checkHeaders(headers);

    //For CTT [7.4.26.7][start]
    if (bTimeSeek || bByteSeek || bStallMode){
    //For CTT [7.4.26.7][end]
      //For CTT [7.4.3.2][7.4.4.6][start]
      LOG.I(TAG,"set transferMode.dlna.org=Streaming");
      setRequestProperty(HEADER_TRANSFERMODE_DLNA_ORG,"Streaming");
      //For CTT [7.4.3.2][7.4.4.6][end]
    }

  }

  protected static long getContentLength(HttpURLConnection connection) {
    long contentLength = C.LENGTH_UNBOUNDED;
    String contentLengthHeader = connection.getHeaderField(HEADER_CONTENT_LENGTH);
    if (!TextUtils.isEmpty(contentLengthHeader)) {
      try {
        contentLength = Long.parseLong(contentLengthHeader);
      } catch (NumberFormatException e) {
        Log.e(TAG, "Unexpected Content-Length [" + contentLengthHeader + "]");
      }
    }

    String contentRangeHeader = connection.getHeaderField(HEADER_TIMESEEKRANGE_DLNA_ORG);
    if (!TextUtils.isEmpty(contentRangeHeader)) {
      Matcher matcher = DLNA_CONTENT_RANGE_HEADER.matcher(contentRangeHeader);
      if (matcher.find()) {
        try {
          long contentLengthFromRange =
              Long.parseLong(matcher.group(7)) - Long.parseLong(matcher.group(6)) + 1;
          
          for (int i = 0 ; i <= matcher.groupCount() ; i++) {
              Log.i(TAG, "getContentLength matcher.groupCount(" + i + ")" + matcher.group(i));
          }
          if (contentLength < 0) {
            // Some proxy servers strip the Content-Length header. Fall back to the length
            // calculated here in this case.
            contentLength = contentLengthFromRange;
          } else if (contentLength != contentLengthFromRange) {
            // If there is a discrepancy between the Content-Length and Content-Range headers,
            // assume the one with the larger value is correct. We have seen cases where carrier
            // change one of them to reduce the size of a request, but it is unlikely anybody would
            // increase it.
            Log.w(TAG, "Inconsistent headers contentLength = " + contentLength);
            Log.w(TAG, "Inconsistent headers contentLengthFromRange = " + contentLengthFromRange);

            Log.w(TAG, "Inconsistent headers [" + contentLengthHeader + "] [" + contentRangeHeader
                + "]");
            contentLength = Math.max(contentLength, contentLengthFromRange);
          }
        } catch (NumberFormatException e) {
          Log.e(TAG, "Unexpected Content-Range [" + contentRangeHeader + "]");
        }
      }
    }
    Log.i(TAG, "getContentLength() = " + contentLength);
    return contentLength;
  }

  protected long getContentStart(HttpURLConnection connection) {
    long contentStart = C.LENGTH_UNBOUNDED;
    
    String contentRangeHeader = connection.getHeaderField(HEADER_TIMESEEKRANGE_DLNA_ORG);
    if (!TextUtils.isEmpty(contentRangeHeader)) {
      Matcher matcher = DLNA_CONTENT_RANGE_HEADER.matcher(contentRangeHeader);
      if (matcher.find()) {
        try {
          
          /*for (int i = 0 ; i <= matcher.groupCount() ; i++) {
              Log.i(TAG, "matcher.groupCount(" + i + ")" + matcher.group(i));
          }*/

          long contentLengthStart = Long.parseLong(matcher.group(6));

          contentStart = contentLengthStart;
        } catch (NumberFormatException e) {
          Log.e(TAG, "Unexpected Content-Range [" + contentRangeHeader + "]");
        }
      }else{
        contentStart = super.getContentStart(connection);
      }
    }else{
      contentStart = super.getContentStart(connection);
    }

    Log.i(TAG, "getContentStart() = " + contentStart);
    return contentStart;
  }
}
