package com.htc.zero;

/**
 * Created by blaise on 10/18/13.
 */
public class Profile {
    public static final int A15 = 15;
    public static final int A16 = 16;
    public static final int H50 = 101;
    public static final int H55 = 102;
    public static final int H60 = 103;


    public static final MSI MS;// = new MS_A16();

    static {
        MS = new MS_A16();
    }

    public static boolean require(int version) {
        return MS.getVersion() >= version;
    }

    public interface MSI {

        public int getVersion();

        public String HTC_TYPE(boolean isMmpExist);

        public String V_FOLDER(boolean isMmpExist);

        public String FAVORITE(boolean isMmpExist);

        public static final String HTC_TYPE = "htc_type";
        public static final String V_FOLDER = "v_folder";
        public static final String FAVORITE = "favorite";
    }


    private static class MS_A16 implements MSI {

        @Override
        public int getVersion() {
            return A16;
        }

        @Override
        public String HTC_TYPE(boolean isMmpExist) {
        	if (isMmpExist)
        		return "htc_type";
        	else
        		return "-1 as htc_type";
        }

        @Override
        public String V_FOLDER(boolean isMmpExist) {
            return "'' as v_folder";
        }

        @Override
        public String FAVORITE(boolean isMmpExist) {
        	if (isMmpExist)
        		return "favorite";
        	else
        		return "0 as favorite";
        }
    }

    private static class MS_H50 implements MSI {

        @Override
        public int getVersion() {
            return H50;
        }

        @Override
        public String HTC_TYPE(boolean isMmpExist) {
            return "-1 as htc_type";
        }

        @Override
        public String V_FOLDER(boolean isMmpExist) {
            return "v_folder";
        }

        @Override
        public String FAVORITE(boolean isMmpExist) {
            return "favorite";
        }
    }

    private static class MS_H55 implements MSI {

        @Override
        public int getVersion() {
            return H55;
        }

        @Override
        public String HTC_TYPE(boolean isMmpExist) {
            return "htc_type";
        }

        @Override
        public String V_FOLDER(boolean isMmpExist) {
            return "v_folder";
        }

        @Override
        public String FAVORITE(boolean isMmpExist) {
            return "favorite";
        }
    }

//    "favorite"
//    "v_folder"
//    "htc_type"
}
