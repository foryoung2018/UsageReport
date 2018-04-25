
package com.htc.lib1.cs.account;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.text.TextUtils;

import com.htc.lib1.cs.StringUtils;
import com.htc.lib1.cs.account.restobj.GeoIpCountry;
import com.htc.lib1.cs.account.restobj.RegionList;
import com.htc.lib1.cs.httpclient.HtcRestRequestPropertiesBuilder;
import com.htc.lib1.cs.httpclient.HttpClient;
import com.htc.lib1.cs.httpclient.HttpConnectionFuture;
import com.htc.lib1.cs.httpclient.JsonInputStreamReader;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Identity configurations.
 */
public class ConfigurationResource {
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private String mServerUri;
    private HttpClient mHttpClient;
    private HashMap<String, String> mSupportedLocaleTable = null;
    private static final String DEFAULT_SUPPORTED_COUNTRY_CODE = "us";

    /**
     * Construct an instance.
     * 
     * @param context Context to operate on.
     * @param serverUri Identity server URI to use.
     */
    public ConfigurationResource(Context context, String serverUri) {
        // Test arguments.
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (TextUtils.isEmpty(serverUri))
            throw new IllegalArgumentException("'serverUri' is null or empty.");

        mServerUri = StringUtils.ensureTrailingSlash(serverUri);
        mHttpClient = new HttpClient(context, new HtcAccountRestErrorStreamReader(),
                new HtcRestRequestPropertiesBuilder(context).build());

        // initialize the supported country of privacy policy website
        mSupportedLocaleTable = new HashMap<String, String>();
        mSupportedLocaleTable.put("de_at", "at");       // Austria
        mSupportedLocaleTable.put("en_au", "au");       // Australia
        mSupportedLocaleTable.put("fr_be", "be-fr");    // Belgium
        mSupportedLocaleTable.put("nl_be", "be-nl");    // Belgium
//        mSupportedLocaleTable.put("bg", "bg");          // Bulgaria
        mSupportedLocaleTable.put("pt_br", "br");       // brazil
        mSupportedLocaleTable.put("en_ca", "ca");       // Canada
        mSupportedLocaleTable.put("fr_ca", "ca-fr");    // Canada
        mSupportedLocaleTable.put("de_ch", "ch-de");    // Switzerland
        mSupportedLocaleTable.put("fr_ch", "ch-fr");    // Switzerland
        mSupportedLocaleTable.put("it_ch", "ch-it");    // Switzerland
        mSupportedLocaleTable.put("zh_cn", "cn");       // China
//        mSupportedLocaleTable.put("el_cy", "cy");       // Cyprus
        mSupportedLocaleTable.put("cs", "cz");          // Czech Republic
        mSupportedLocaleTable.put("de", "de");          // Germany
        mSupportedLocaleTable.put("da", "dk");          // Denmark
//        mSupportedLocaleTable.put("et", "ee");          // Estonia
        mSupportedLocaleTable.put("es", "es");          // Spain
        mSupportedLocaleTable.put("fi", "fi");          // Finland
        mSupportedLocaleTable.put("fr", "fr");          // France
        mSupportedLocaleTable.put("el", "gr");          // Greece
        mSupportedLocaleTable.put("en_hk", "hk-en");    // Hongkong
        mSupportedLocaleTable.put("zh_hk", "hk-tc");    // Hongkong
        mSupportedLocaleTable.put("hr", "hr");          // croatia
        mSupportedLocaleTable.put("hu", "hu");          // Hungary
        mSupportedLocaleTable.put("in", "id");          // indonesia
        mSupportedLocaleTable.put("en_ie", "ie");       // Ireland
        mSupportedLocaleTable.put("en_in", "in");       // Indonesia
        mSupportedLocaleTable.put("it", "it");          // Italy
        mSupportedLocaleTable.put("ja", "jp");          // Japan
//        mSupportedLocaleTable.put("ko", "kr");          // korea
        mSupportedLocaleTable.put("kz", "kz");          // kazakhstan
        mSupportedLocaleTable.put("es_bz", "latam");    // Latin America
//        mSupportedLocaleTable.put("lt", "lt");          // Lithunia
//        mSupportedLocaleTable.put("fr_lu", "lu");       // Luxemburg
//        mSupportedLocaleTable.put("lv", "lv");          // Latvia
//        mSupportedLocaleTable.put("fr_sa", "mea-fr");   // Middle East
        mSupportedLocaleTable.put("ar_sa", "mea-sa");   // Middle East
        mSupportedLocaleTable.put("my", "mm");          // Myanmar
//        mSupportedLocaleTable.put("mt", "mt");          // Malta
        mSupportedLocaleTable.put("nl", "nl");          // Netherland
        mSupportedLocaleTable.put("no", "no");          // Norway
        mSupportedLocaleTable.put("en_nz", "nz");       // New Zealand
        mSupportedLocaleTable.put("pl", "pl");          // Poland
        mSupportedLocaleTable.put("pt", "pt");          // Portugal
        mSupportedLocaleTable.put("ro", "ro");          // Romania
        mSupportedLocaleTable.put("sr", "rs");          // Serbia
        mSupportedLocaleTable.put("ru", "ru");          // Russia
        mSupportedLocaleTable.put("sv", "se");          // Sweden
        mSupportedLocaleTable.put("en_id", "sea");      // Southeast Asia
//        mSupportedLocaleTable.put("sl", "si");          // Slovenia
        mSupportedLocaleTable.put("sk", "sk");          // slovakia
        mSupportedLocaleTable.put("th", "th");          // Thailand
        mSupportedLocaleTable.put("tr", "tr");          // Turkey
        mSupportedLocaleTable.put("zh_tw", "tw");       // Taiwan
        mSupportedLocaleTable.put("uk", "ua");          // ukraine
        mSupportedLocaleTable.put("en_gb", "uk");       // United Kingdom
        mSupportedLocaleTable.put("en_us", "us");       // United States
        mSupportedLocaleTable.put("vi", "vn");          // Vietnam
        mSupportedLocaleTable.put("en", "www");         // Global
        
        // patch mapping
        mSupportedLocaleTable.put("ar_eg", "mea-sa");
        mSupportedLocaleTable.put("ar_xb", "mea-sa");
        mSupportedLocaleTable.put("cs_cz", "cz");
        mSupportedLocaleTable.put("da_dk", "dk");
        mSupportedLocaleTable.put("de_de", "de");
        mSupportedLocaleTable.put("el_gr", "gr");
        mSupportedLocaleTable.put("es_es", "es");
        mSupportedLocaleTable.put("es_us", "es");
//        mSupportedLocaleTable.put("et_ee", "ee");
        mSupportedLocaleTable.put("fi_fi", "fi");
        mSupportedLocaleTable.put("fr_fr", "fr");
        mSupportedLocaleTable.put("gb", "uk");
        mSupportedLocaleTable.put("hr_hr", "hr");
        mSupportedLocaleTable.put("hu_hu", "hu");
        mSupportedLocaleTable.put("in_id", "id");
        mSupportedLocaleTable.put("it_it", "it");
        mSupportedLocaleTable.put("ja_jp", "jp");
//        mSupportedLocaleTable.put("lt_lt", "lt");
//        mSupportedLocaleTable.put("lv_lv", "lv");
        mSupportedLocaleTable.put("my_mm", "mm");
        mSupportedLocaleTable.put("nl_nl", "nl");
        mSupportedLocaleTable.put("pl_pl", "pl");
        mSupportedLocaleTable.put("pt_pt", "pt");
        mSupportedLocaleTable.put("ro_ro", "ro");
        mSupportedLocaleTable.put("ru_ru", "ru");
        mSupportedLocaleTable.put("sk_sk", "sk");
//        mSupportedLocaleTable.put("sl_si", "si");
        mSupportedLocaleTable.put("sr_rs", "rs");
        mSupportedLocaleTable.put("sv_se", "se");
        mSupportedLocaleTable.put("th_th", "th");
        mSupportedLocaleTable.put("tr_tr", "tr");
        mSupportedLocaleTable.put("uk_ua", "ua");
        mSupportedLocaleTable.put("vi_vn", "vn");
    }

    /**
     * Construct an instance with default server URI.
     * 
     * @param context Context to operate on.
     */
    public ConfigurationResource(Context context) {
        this(context, HtcAccountDefs.DEFAULT_SERVER_URI);
    }

    /**
     * New version of get available regions. <br>
     * Path:
     * {serverUri}/Services/Regions.svc/RegionsV2/?sortBy=Name&sortDescending
     * =false&start=0&count=0
     * 
     * @return {@link HttpConnectionFuture} which resolves to {@link RegionList}
     *         .
     */
    public HttpConnectionFuture<RegionList> getRegionsV2() {
        mLogger.verbose();

        // Compose URL.
        StringBuilder urlBuilder = new StringBuilder(mServerUri)
                .append("Services/Regions.svc/RegionsV2/?sortBy=Name&sortDescending=false&start=0&count=0");
        URL url;
        try {
            url = new URL(urlBuilder.toString());
        } catch (MalformedURLException e) {
            // The URL is a static constant. Malformed URL indicates a bug.
            throw new IllegalStateException(e.getMessage(), e);
        }

        // Make REST call.
        return mHttpClient.get(
                mHttpClient.getRequestBuilder(url, new JsonInputStreamReader<RegionList>() {
                }).build(), null, null);
    }

    /**
     * Get legal docs HTML page URL.
     * 
     * @param countryCode Country code. Must not be {@code null}.
     * @param locale The language code such as "en_US", or {@code null} for
     *            device default locale.
     * @return Legal docs URL.
     * @throws IllegalArgumentException If {@code countryCode} is {@code null}
     *             or empty.
     */
    public String getPendingLegalDocsUrl(String countryCode, String locale) {
        // Test arguments.
        if (TextUtils.isEmpty(countryCode))
            throw new IllegalArgumentException("'countryCode' is null or empty.");
        if (TextUtils.isEmpty(locale)) {
            Locale systemLocale = Locale.getDefault();
            StringBuilder sb = new StringBuilder().append(systemLocale.getLanguage());
            if (!TextUtils.isEmpty(systemLocale.getCountry())) {
                sb.append("_").append(systemLocale.getCountry());
            }
            locale = sb.toString();
        }

        // Compose legal docs URL.
        String url = new StringBuilder(mServerUri)
                .append("Services/LegalDocs.svc/LegalDocs/Pending/")
                .append(countryCode).append("/").append(locale).append("/content.html").toString();
        mLogger.debugS(url);
        return url;
    }

    /**
     * Get privacy policy HTML page URL.
     * 
     * @param countryCode Country code. Must not be {@code null}.
     * @param locale The language code such as "en_US", or {@code null} for
     *            device default locale.
     * @return Privacy policy URL.
     */
    public String getPrivacyPolicyUrl(String countryCode) {
        // Test arguments.
        if (TextUtils.isEmpty(countryCode))
            throw new IllegalArgumentException("'countryCode' is null or empty.");

        // Compose privacy policy URL.
        String url = new StringBuilder(HtcAccountDefs.DEFAULT_HTC_SERVER_URI)
                .append(getAvailableCountryCode(countryCode)).append("/terms/privacy/?text")
                .toString();
        mLogger.debugS(url);
        return url;
    }

    /**
     * Get learn more HTML page URL.
     *
     * @param locale The language code such as "en_US", or {@code null} for
     *            device default locale.
     * @return Learn more URL.
     */
    public String getLearnMoreUrl(String locale) {
        if (TextUtils.isEmpty(locale)) {
            Locale systemLocale = Locale.getDefault();
            StringBuilder sb = new StringBuilder().append(systemLocale.getLanguage());
            if (!TextUtils.isEmpty(systemLocale.getCountry())) {
                sb.append("_").append(systemLocale.getCountry());
            }
            locale = sb.toString();
        }

        // Compose privacy policy URL.
        String url = new StringBuilder(HtcAccountDefs.DEFAULT_HTC_SERVER_URI)
                .append(getAvailableCountryCode(locale)).append("/terms/learn-more/?text")
                .toString();
        mLogger.debugS(url);
        return url;
    }
    
    protected String getAvailableCountryCode(String locale) {
        String availableCountryCode = DEFAULT_SUPPORTED_COUNTRY_CODE;
        
        if (mSupportedLocaleTable != null && mSupportedLocaleTable.get(locale.toLowerCase()) != null) {
            availableCountryCode = mSupportedLocaleTable.get(locale.toLowerCase());
        }
        Locale l = Locale.getDefault();
        mLogger.debug("locale = ", locale, ", available country code = ", availableCountryCode, ", local.toString=", l.toString(), ", locale.language=", l.getLanguage());
        
        return availableCountryCode;
    }

    /**
     * Get country code according to the GeoIP.
     * 
     * @return {@link HttpConnectionFuture} which resolves to a country code.
     */
    public HttpConnectionFuture<GeoIpCountry> getGeoIpCountry() {
        mLogger.verbose();

        // Compose URL.
        StringBuilder urlBuilder = new StringBuilder(mServerUri)
                .append("Services/Regions.svc/Regions/GeoIP/");
        URL url;
        try {
            url = new URL(urlBuilder.toString());
        } catch (MalformedURLException e) {
            // The URL is a static constant. Malformed URL indicates a bug.
            throw new IllegalStateException(e.getMessage(), e);
        }

        // Make REST call.
        return mHttpClient.get(
                mHttpClient.getRequestBuilder(url, new JsonInputStreamReader<GeoIpCountry>() {
                }).build(), null, null);
    }
}
