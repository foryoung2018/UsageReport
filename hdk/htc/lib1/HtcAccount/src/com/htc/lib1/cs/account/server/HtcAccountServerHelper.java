package com.htc.lib1.cs.account.server;

import android.content.Context;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.htc.lib1.cs.account.restobj.HtcAccountProfile;
import com.htc.lib1.cs.account.restservice.ProfileResource;
import com.htc.lib1.cs.auth.AuthLoggerFactory;
import com.htc.lib1.cs.auth.web.WebAuthConfig;
import com.htc.lib1.cs.logging.HtcLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by leohsu on 2017/3/31.
 */

public class HtcAccountServerHelper {
    private static final String DEFAULT_SERVICE_URI = "https://www.htcsense.com/$RAM$/$SS$/";
    private static final String DEFAULT_SERVICE_URI_STG = "https://www.htctouch.com/$RAM$/$SS$/";
    private static final String DEFAULT_COUNTRY_CODE = "US";

    private static final String IP_LOCATION_API = "Services/Regions.svc/Regions/GeoIP/";

    private static final String REGION_LIST_URL = "https://www.htcsense.com/$WS$/Services/Regions.svc/Regions/";
    private static final String REGION_LIST_URL_STG = "https://www.htctouch.com/$WS$/Services/Regions.svc/Regions/";

    private static final long COUNTRY_CODE_EXPIRE_TIME = TimeUnit.MINUTES.toMillis(30);
    private static final long REGION_LIST_EXPIRE_TIME = TimeUnit.HOURS.toMillis(12);

    private static final String PREF_NAME_ACCOUNT_SERVER = "com.htc.lib1.cs.account_server_helper";
    private static final String PREF_KEY_COUNTRY_CODE = "country_code";

    private static final String FILENAME_REGION_LIST = "com.htc.lib1.cs.account_server_helper.region_list";
    private static final String TEMPFILE_PREFIX_REGION_LIST = "com.htc.lib1.cs.account_server_helper.tmp_region_list";

    private static final HtcLogger sLogger =
            new AuthLoggerFactory(HtcAccountServerHelper.class).create();

    private static final Object sCountryCodeLock = new Object();
    private static long sCountryCodeExpireTime = 0;
    private static String sCountryCode = DEFAULT_COUNTRY_CODE;
    private static String sServiceUri = null;

    private static final Object sRegionListLock = new Object();
    private static String sRegionListUrl = null;
    private static long sRegionListUpdateTime = 0;
    private static List<RegionConfig> sRegionList = null;

    public static String getDefaultServiceUri(Context context) {
        ensureDefaultUri(context);
        return sServiceUri;
    }

    public static String getCountryCode(Context context) {
        synchronized (sCountryCodeLock) {
            TelephonyManager tm = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            String countryCode = tm.getSimCountryIso();

            if (TextUtils.isEmpty(countryCode) &&
                    SystemClock.elapsedRealtime() > sCountryCodeExpireTime) {
                // Always refresh if process got killed.
                countryCode = getIpLocation(context);
                if (!TextUtils.isEmpty(countryCode)) {
                    sCountryCodeExpireTime = SystemClock.elapsedRealtime() + COUNTRY_CODE_EXPIRE_TIME;
                }
            }

            if (TextUtils.isEmpty(countryCode)) {
                sCountryCode = context.getSharedPreferences(PREF_NAME_ACCOUNT_SERVER, Context.MODE_PRIVATE)
                        .getString(PREF_KEY_COUNTRY_CODE, DEFAULT_COUNTRY_CODE);
            } else if (!sCountryCode.equals(countryCode)) {
                // Always use upper case.
                sCountryCode = countryCode.toUpperCase();
                context.getSharedPreferences(PREF_NAME_ACCOUNT_SERVER, Context.MODE_PRIVATE)
                        .edit()
                        .putString(PREF_KEY_COUNTRY_CODE, sCountryCode)
                        .apply();
            }
            return sCountryCode;
        }
    }

    public static RegionConfig getRegionConfig(Context context) {
        String countryCode = getCountryCode(context);
        RegionConfig cfg = getRegionConfig(context, countryCode);
        if (cfg == null) {
            if (sRegionList == null) {
                sLogger.warning("Unable to load region list");
                return null;
            }
            sLogger.warning("Missing config for region " + countryCode + ". Use US instead.");
            cfg = getRegionConfig(context, "US");
        }
        return cfg;
    }

    public static RegionConfig getRegionConfig(Context context, String countryCode) {
        return getRegionConfig(context, countryCode, true);
    }

    public static RegionConfig getRegionConfigWithoutUpdate(Context context, String countryCode) {
        return getRegionConfig(context, countryCode, false);
    }

    public static HtcAccountProfile getProfile(Context context, String serviceUri, String token) {
        ProfileResource resource = new ProfileResource(
                context,
                serviceUri,
                token,
                context.getPackageName());
        HtcAccountProfile profile = null;
        try {
            profile = (HtcAccountProfile) resource.getAccountProfile()
                    .isValidOrThrow();
        } catch (Exception e) {
            sLogger.warning("Failed to retrieve profile", e);
        }
        return profile;
    }

    private static synchronized void ensureDefaultUri(Context context) {
        if (sServiceUri != null) {
            return;
        }
        String webAuthUri = WebAuthConfig.get(context).getBaseUri();
        if (TextUtils.isEmpty(webAuthUri) || !webAuthUri.contains("www.htctouch")) {
            sServiceUri = DEFAULT_SERVICE_URI;
            sRegionListUrl = REGION_LIST_URL;
        } else {
            sServiceUri = DEFAULT_SERVICE_URI_STG;
            sRegionListUrl = REGION_LIST_URL_STG;
        }
    }

    private static RegionConfig getRegionConfig(Context context, String countryCode, boolean canUpdate) {
        synchronized (sRegionListLock) {
            if (sRegionList == null) {
                // Always load from file first.
                loadRegionConfigList(context);
            }

            if (canUpdate) {
                boolean needUpdate = sRegionList == null;
                if (!needUpdate) {
                    long currentTime = System.currentTimeMillis();
                    if (sRegionListUpdateTime < currentTime - REGION_LIST_EXPIRE_TIME ||
                            sRegionListUpdateTime > currentTime + REGION_LIST_EXPIRE_TIME) {
                        needUpdate = true;
                    }
                }

                if (needUpdate) {
                    List<RegionConfig> regionList = downloadRegionConfigList(context);
                    if (regionList != null && !regionList.isEmpty()) {
                        sRegionListUpdateTime = System.currentTimeMillis();
                        sRegionList = regionList;
                        saveRegionConfigList(context);
                    }
                }
            }

            if (sRegionList != null) {
                for (RegionConfig cfg : sRegionList) {
                    if (cfg.countryCode.equals(countryCode)) {
                        return cfg;
                    }
                }
            }
            return null;
        }
    }

    private static String getIpLocation(Context context) {
        sLogger.debug("Try to resolve location by IP");
        String countryCode = null;
        HttpURLConnection conn = null;
        InputStream in = null;
        ensureDefaultUri(context);
        try {
            URL url = new URL(sServiceUri + IP_LOCATION_API);
            conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            JSONObject jo = readJsonObject(in);
            countryCode = jo.getString("country-code");
        } catch (Exception e) {
            sLogger.warning("Unable to resolve location by IP.");
        } finally {
            closeQuietly(in);
            if (conn != null) {
                conn.disconnect();
            }
        }
        return countryCode;
    }

    private static void loadRegionConfigList(Context context) {
        File regionListFile = new File(context.getFilesDir(), FILENAME_REGION_LIST);
        if (regionListFile.exists()) {
            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(new BufferedInputStream(
                        new FileInputStream(regionListFile)));
                sRegionListUpdateTime = in.readLong();
                sRegionList = (List<RegionConfig>) in.readObject();
            } catch (IOException e) {
                sLogger.warning("Failed to load region list: ", e);
                sRegionListUpdateTime = 0;
                sRegionList = null;
            } catch (ClassNotFoundException e) {
                sLogger.warning("Failed to load region list: ", e);
                sRegionListUpdateTime = 0;
                sRegionList = null;
            } finally {
                closeQuietly(in);
            }
        }
    }

    private static void saveRegionConfigList(Context context) {
        File tmpFile = null;
        ObjectOutputStream out = null;
        try {
            tmpFile = File.createTempFile(TEMPFILE_PREFIX_REGION_LIST, null);

            out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(tmpFile)));
            out.writeLong(sRegionListUpdateTime);
            out.writeObject(sRegionList);
            out.close();
            out = null;

            File regionListFile = new File(context.getFilesDir(), FILENAME_REGION_LIST);
            if (regionListFile.exists()) {
                if (!regionListFile.delete()) {
                    sLogger.warning("Failed to delete the previous region list file");
                }
            }
            if (!tmpFile.renameTo(regionListFile)) {
                sLogger.warning("Failed to update region list file");
            }
        } catch (Exception e) {
            sLogger.warning("Failed to save region list: ", e);
        } finally {
            closeQuietly(out);
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
        }
    }

    private static List<RegionConfig> downloadRegionConfigList(Context context) {
        HttpURLConnection conn = null;
        InputStream in = null;
        ArrayList<RegionConfig> list = new ArrayList<>(300);
        try {
            ensureDefaultUri(context);
            URL url = new URL(sRegionListUrl);
            conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            JSONArray ja = readJsonObject(in).getJSONArray("Results");
            for (int i = 0; i < ja.length(); ++i) {
                try {
                    JSONObject joEntry = ja.getJSONObject(i);
                    RegionConfig cfg = new RegionConfig(
                            UUID.fromString(joEntry.getString("Id")),
                            joEntry.getString("CountryCode").toUpperCase(),
                            joEntry.getJSONObject("DataCenter").getString("ServiceUri"),
                            joEntry.getBoolean("DefaultSendEmailAboutProduct"));
                    list.add(cfg);
                } catch (JSONException e) {
                    sLogger.warning("Failed to parse entry: ", e);
                }
            }
            sLogger.info("Downloaded configs for " + list.size() + " regions.");
        } catch (Exception e) {
            sLogger.warning("Unable to resolve location by IP.");
        } finally {
            closeQuietly(in);
            if (conn != null) {
                conn.disconnect();
            }
        }
        return list;
    }

    private static JSONObject readJsonObject(InputStream in)
            throws IOException, JSONException {
        StringBuilder sb = new StringBuilder(1024);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String response = sb.toString();
        return new JSONObject(response);
    }

    private static void closeQuietly(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            // Do nothing
        }
    }

    public static class RegionConfig implements Serializable{
        public final UUID id;
        public final String countryCode;
        public final String serviceUri;
        public final boolean defaultSendEmailAboutProduct;

        private RegionConfig(UUID _id,
                             String _countryCode,
                             String _serviceUri,
                             boolean _defaultSendEmailAboutProduct) {
            id = _id;
            countryCode = _countryCode;
            serviceUri = !TextUtils.isEmpty(_serviceUri) ? _serviceUri : DEFAULT_SERVICE_URI;
            defaultSendEmailAboutProduct = _defaultSendEmailAboutProduct;
        }
    }
}
