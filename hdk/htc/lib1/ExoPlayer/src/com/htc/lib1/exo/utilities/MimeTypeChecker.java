package com.htc.lib1.exo.utilities;

import java.nio.ByteBuffer;

import android.net.Uri;

public class MimeTypeChecker {
    private static final String TAG = "MimeTypeChecker";
    public static boolean isStreaming(Uri uri) {
        return (isRTSP(uri, null) ||
                isHTTP(uri, null) ||
                isDTCP(uri, null));
    }

    /**
     *
     * SDP , HLS ,SS and DASH will used HTTP protocol but not download a single file.
     *
     */
    public static boolean isHTTPProgressive(Uri uri, String mimeType) {
        return (isHTTP(uri, mimeType) == true &&
                isSDP(uri, mimeType) == false &&
                isHLS(uri, mimeType) == false &&
                isSmoothStreaming(uri, mimeType) == false &&
                isDash(uri, mimeType) == false);
    }

    private static boolean isHTTP(Uri uri, String mimeType) {
        return usePatternsToCheck(HTTP_PATTERN, uri, mimeType);
    }

    public static boolean isRTSPorSDP(Uri uri, String mimeType) {
        return (isRTSP(uri, mimeType) ||
                isSDP(uri, mimeType));

    }

    private static boolean isRTSP(Uri uri, String mimeType) {
        return usePatternsToCheck(RTSP_PATTERN, uri, mimeType);

    }

    /**
     *
     * SDP is for RTSP session definition file.
     *
     */
    private static boolean isSDP(Uri uri, String mimeType) {
        return usePatternsToCheck(SDP_PATTERN, uri, mimeType);
    }

    private static boolean isDTCP(Uri uri, String mimeType) {
        return usePatternsToCheck(DTCP_PATTERN, uri, mimeType);

    }

    public static boolean isHLS(Uri uri, String mimeType) {
        return usePatternsToCheck(HLS_PATTERN, uri, mimeType);
    }

    public static boolean isSmoothStreaming(Uri uri, String mimeType) {
        return usePatternsToCheck(SS_PATTERN, uri, mimeType);
    }

    public static boolean isDash(Uri uri, String mimeType) {
        return usePatternsToCheck(DASH_PATTERN, uri, mimeType);
    }

    public static boolean isMp4(Uri uri, String mimeType) {
        return usePatternsToCheck(MP4_PATTERN, uri, mimeType);
    }

    public static boolean isMp3(Uri uri, String mimeType) {
        return usePatternsToCheck(MP3_PATTERN, uri, mimeType);
    }

    public static boolean isTs(Uri uri, String mimeType) {
        return usePatternsToCheck(TS_PATTERN, uri, mimeType);
    }

    public static boolean isWebm(final Uri uri, final String mimeType) {
        return usePatternsToCheck(WEBM_PATTERN, uri, mimeType);
    }

    public static boolean isMkv(final Uri uri, final String mimeType) {
        return usePatternsToCheck(MATROSKA_PATTERN, uri, mimeType);
    }

    private final static int MINETYPE_INDEX = 0;
    private final static int EXT_INDEX = 1;
    private final static int SCHEME_INDEX = 2;

    /*
     * XXX_PATTERN will in the following format
     * { String[] mime_type_patterns,
     *   String[] file_extension_patterns,
     *   String[] protocol_patterns,
     * }
     *
     */

    private final static String[][] HTTP_PATTERN = {
        null,
        null,
        {"http","https"}
    };

    private final static String[][] RTSP_PATTERN = {
        null,
        null,
        {"rtsp"}
    };

    private final static String[][] DTCP_PATTERN = {
        null,
        null,
        {"dtcp"}
    };

    private final static String[][] SDP_PATTERN = {
        {"application/sdp"},
        {".sdp"},
        {"http","https"}
    };

    private final static String[][] HLS_PATTERN = {
        {"application/vnd.apple.mpegurl", "application/x-mpegURL", "application/x-mpegurl"},
        {".m3u8"},
        {"http","https"}
    };

    private final static String[][] SS_PATTERN = {
        {"application/vnd.ms-sstr+xml"},
        {".ism", ".ism/Manifest", ".ism/manifest"},
        {"http","https"}
    };

    private final static String[][] DASH_PATTERN = {
        {"application/dash+xml","video/vnd.mpeg.dash.mpd"},
        {".mpd"},
        {"http","https"}
    };

    private final static String[][] MP4_PATTERN = {
        {"video/avc","video/mp4"},
        {".mp4",".3gp", ".m4v"},
        null
    };

    private final static String[][] MP3_PATTERN = {
        {"audio/mpeg3","audio/x-mpeg-3"},
        {".mp3",".MP3"},
        null
    };

    private final static String[][] TS_PATTERN = {
        {"video/MP2T","video/mp2t","video/vnd.dlna.mpeg-tts"},
        {".ts",".tsv",".tsa"},
        null
    };

    private final static String[][] WEBM_PATTERN = {
        {"video/webm","audio/webm"},
        {".webm"},
        null
    };

    private final static String[][] MATROSKA_PATTERN = {
        {"video/x-matroska","audio/x-matroska"},
        {".mkv", ".mk3d", ".mka"},
        null
    };

    private final static Byte[][] MP4_SIGNATURE = {
        {
            (byte)0x00, (byte)0x00, null,        null,
            (byte)0x66, (byte)0x74, (byte)0x79, (byte)0x70
        }
    };

    private final static Byte[][] ASF_SIGNATURE = {
        {
            (byte)0x30, (byte)0x26, (byte)0xB2, (byte)0x75,
            (byte)0x8E, (byte)0x66, (byte)0xCF, (byte)0x11,
            (byte)0xA6, (byte)0xD9, (byte)0x00, (byte)0xAA,
            (byte)0x00, (byte)0x62, (byte)0xCE, (byte)0x6C
        }
    };

    private final static Byte[][] WAV_SIGNATURE = {
        {
            (byte)0x52, (byte)0x49, (byte)0x46, (byte)0x46,
            null,       null,       null,       null,
            (byte)0x57, (byte)0x41, (byte)0x56, (byte)0x45
        }
    };

    private final static Byte[][] MIDI_SIGNATURE = {
        {(byte)0x4D, (byte)0x54, (byte)0x68, (byte)0x64}
    };

    private final static Byte[][] FLAC_SIGNATURE = {
        {(byte)0x66, (byte)0x4C, (byte)0x61, (byte)0x43}
    };

    private final static Byte[][] WEBM_SIGNATURE = {
        {(byte)0x1A, (byte)0x45, (byte)0xDF, (byte)0xA3}
    };

    private final static Byte[][] AIFF_SIGNATURE = {
        {
            (byte)0x46, (byte)0x4F, (byte)0x52, (byte)0x4D,
            (byte)0x41, (byte)0x49, (byte)0x46, (byte)0x46,
        }
    };

    static public boolean isAsf(ByteBuffer buffer) {
        return useSignatureToCheck(ASF_SIGNATURE, buffer);
    }

    static public boolean isMp4(ByteBuffer buffer) {
        return useSignatureToCheck(MP4_SIGNATURE, buffer);
    }

    static public boolean isWebm(ByteBuffer buffer) {
        return useSignatureToCheck(WEBM_SIGNATURE, buffer);
    }

    static public boolean isAiff(ByteBuffer buffer) {
        return useSignatureToCheck(AIFF_SIGNATURE, buffer);
    }

    static public boolean isFlac(ByteBuffer buffer) {
        return useSignatureToCheck(FLAC_SIGNATURE, buffer);
    }

    /**
     * There is only 1 sync byte "0x47" for TS file.
     * And it will repeat for each 188 bytes.
     *
     */
    static public boolean isTs(ByteBuffer buffer) {

        int buf_size = buffer.limit();

        for (int i =0 ; i < buf_size; i += 188) {
            if(buffer.get(i) != 0x47) return false;
        }
        return true;
    }
    /**
     * files may have more than one signatures,so signatures it's a set.
     *
     * The buffer must be at the start of the file.
     *
     */

    static public boolean useSignatureToCheck(Byte[][] signatures, ByteBuffer buffer) {
        int buf_size = buffer.limit();

        for (Byte[] signature : signatures) {
            int sig_size = signature.length;
            boolean isMatch = true;
            for(int i = 0; i < sig_size ; i++) {
                if(i > buf_size) break;

                Byte sByte = signature[i];
                if (sByte == null) continue;

                byte bByte = buffer.get(i);
                if(sByte.byteValue() == bByte) continue;

                isMatch = false;
                break;
            }
            if (isMatch) return true;
        }
        return false;
    }

    /**
     * patterns is include the pattern of mime-type, file extension and proper protocol.
     * we are check the mine-type fist, if there is no matched.
     * we will check file extension and protocol.
     * And each pattern are not case sensitive.
     *
     */
    static private boolean usePatternsToCheck(final String[][] patterns,
            final Uri uri,
            final String mimeType /* Optional */) {

        if (patterns == null) {
            return false;
        }

        if (uri == null) {
            return false;
        }

        final String path = uri.toString();

        if (path == null) {
            return false;
        }

        final String scheme = uri.getScheme();

        if (scheme == null) {
            return false;
        }

        //check mime type first
        if (checkMatched(patterns[MINETYPE_INDEX], mimeType)) {
            return true;
        }

        //check file and scheme extension
        if (patterns[EXT_INDEX] != null && patterns[SCHEME_INDEX] != null) {
            return (checkExisted(patterns[EXT_INDEX], path) && checkMatched(patterns[SCHEME_INDEX], scheme));

        } else if (patterns[EXT_INDEX] != null && patterns[SCHEME_INDEX] == null) {
            return checkExisted(patterns[EXT_INDEX], path);

        } else if (patterns[EXT_INDEX] == null && patterns[SCHEME_INDEX] != null) {
            return checkMatched(patterns[SCHEME_INDEX], scheme);

        }
        return false;
    }

    static private boolean checkMatched(String[] patterns, String target) {
        if (patterns == null) return false;
        if (target == null) return false;

        for(String patten : patterns) {
            if(target.equalsIgnoreCase(patten)) return true;
        }
        return false;
    }

    static private boolean checkExisted(String[] patterns, String target) {
        if (patterns == null) return false;
        if (target == null) return false;

        for (String pattern : patterns) {
            if(target.equalsIgnoreCase(pattern)) return true;
            int index = target.indexOf(pattern);

            if(index > 0) return true;
        }
        return false;
    }
}
