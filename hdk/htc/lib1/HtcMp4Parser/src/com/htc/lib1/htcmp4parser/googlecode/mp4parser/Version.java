package com.htc.lib1.htcmp4parser.googlecode.mp4parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.logging.Logger;

/**
 * The classic version object.
 * @hide
 */
public class Version {
    private static final Logger LOG = Logger.getLogger(Version.class.getName());
    
    /**
     * @hide
     */
    public static final String VERSION;

    static {
    	final InputStream inputStream = Version.class.getResourceAsStream("/version.txt");
        LineNumberReader lnr = new LineNumberReader(new InputStreamReader(inputStream));
        String version;
        try {
            version = lnr.readLine();
        } catch (IOException e) {
            LOG.warning(e.getMessage());
            version = "unknown";
        } finally {
        	try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }       
        VERSION = version;

    }
}
