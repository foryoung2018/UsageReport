
package com.htc.lib1.cs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

import android.text.TextUtils;

/**
 * String utilities.
 * <p>
 * The naming follows a general rule: if a helper class includes only static
 * methods it's named as {@code Utils}, and if it needs an instance to work
 * (including singleton implementation) it's named as {@code Helper}.
 * </p>
 */
public class StringUtils {

    /**
     * A stream reader that reads strings.
     */
    public static class StringStreamReader {

        /**
         * Read the whole {@code InputStream} into a string. Note it won't close
         * the stream.
         * 
         * @param istream {@code InputStream} to read.
         * @return {@code String}
         * @throws IOException If an error occurs when reading the stream.
         */
        public String read(InputStream istream) throws IOException {
            if (istream == null)
                throw new IllegalArgumentException("'istream' is null.");

            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            return builder.toString();
        }
    }

    /**
     * A stream writer that writes strings.
     */
    public static class StringStreamWriter {
        private OutputStream mmOutStream;

        /**
         * Construct a {@code StringWriter} with specific {@code OutputStream}.
         * 
         * @param ostream An {@code OutputStream} to operate on.
         */
        public StringStreamWriter(OutputStream ostream) {
            if (ostream == null)
                throw new IllegalArgumentException("'ostream' is null.");
            mmOutStream = ostream;
        }

        /**
         * Write to a string to the {@code OutputStream}. Note it won't close
         * the stream.
         * 
         * @param string String to write. Do nothing if the string is
         *            {@code null} or empty.
         * @throws IOException If an error occurs when writing the stream.
         */
        public void write(String string) throws IOException {
            if (!TextUtils.isEmpty(string)) {
                OutputStreamWriter writer = new OutputStreamWriter(mmOutStream);
                writer.write(string);
                writer.flush();
            }
        }
    }

    /**
     * Remove the trailing "/" if any.
     * 
     * @param str Input string.
     * @return Modified string.
     */
    public static String removeTrailingSlash(String str) {
        if (null != str && str.charAt(str.length() - 1) == '/')
            return str.substring(0, str.length() - 1);
        return str;
    }

    /**
     * Add a pending "/" if the string is not ended with "/".
     * 
     * @param str Input string.
     * @return Modified string.
     */
    public static String ensureTrailingSlash(String str) {
        if (!str.endsWith("/"))
            return str + "/";
        return str;
    }

    /**
     * Convert the locale language code of the form like "en_US" to IETF
     * language tag form like "en-us".
     * 
     * @param locale Locale to generate language code.
     * @return The converted string. If the locale is {@code null} or the
     *         language is not available, it returns the default value "en-us".
     */
    public static String getIETFLangaugeTag(Locale locale)
    {
        // Default to en-us if locale info is not available.
        if (locale == null || TextUtils.isEmpty(locale.getLanguage())) {
            return "en-us";
        }

        StringBuilder langtag = new StringBuilder();
        langtag.append(locale.getLanguage().toLowerCase(Locale.US));
        if (!TextUtils.isEmpty(locale.getCountry())) {
            langtag.append("-").append(locale.getCountry().toLowerCase(Locale.US));
        }
        return langtag.toString();
    }

    /**
     * Repeat a given string for {@code count} times.
     * 
     * @param str String to repeat.
     * @param count Repeat count.
     * @return Generated string.
     */
    public static String repeat(String str, int count) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < count; i++)
            builder.append(str);

        return builder.toString();
    }
}
