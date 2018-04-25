package com.htc.hfm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * This class provides speech for hand-free mode (HFM) service.
 */
public class Speech implements Parcelable {

    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     */
    public static final int SPEECH_TYPE_TEXT = 701;
    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     */
    public static final int SPEECH_TYPE_AUDIO_RESOURCE = 702;
    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     */
    public static final int SPEECH_TYPE_TEXT_RESOURCE = 703;
    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     */
    public static final int SPEECH_TYPE_AUDIO_FILE = 704;
    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     */
    public static final int SPEECH_TYPE_TEXT_WITH_DIGITS = 705;

    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     */
    public static final int AUDIO_TYPE_PCM_8K = 801;
    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     */
    public static final int AUDIO_TYPE_PCM_16K = 802;
    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     */
    public static final int AUDIO_TYPE_AMR = 803;
    
    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     */
    public static final int AUDIO_TYPE_SPEEX_8K = 804;
    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     */
    public static final int AUDIO_TYPE_SPEEX_16K = 805;
    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     */
    public static final int AUDIO_TYPE_SPEEX_32K = 806;

    private static final String TAG = Speech.class.getSimpleName();
    private static final String HTCSPEAK_DEFAULT_LANG = "htcspeak_default_lang";
    private static final String HTCSPEAK_USED_LANG   = "htcspeak_used_lang";
    private static final String PROMPT_LOCATION_DEFAULT = "/system/etc/.speak/languages/";
    private static final String PROMPT_LOCATION = "/data/.speak/languages/";

    private int mSpeechType;
    private int mAudioType;
    private String mText;
    private byte[] mAudio;
    private int mTextResId;
    private int mAudioResId;
    private String file;

    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     */
    public static final Parcelable.Creator<Speech> CREATOR = new Parcelable.Creator<Speech>() {
        @Override
        public Speech createFromParcel(Parcel source) {
            Speech s = new Speech();
            s.readFromParcel(source);
            return s;
        }

        @Override
        public Speech[] newArray(int size) {
            return new Speech[size];
        }
    };

    /**
     * Create Speech From Text.
     * @param text
     * Text string
     * @return
     * Parcelable object
     */
    public static Speech createSpeechFromText(String text) {
        return createSpeechFromText(text, SPEECH_TYPE_TEXT);
    }

    /**
     * Create Speech From Text Resource.
     * @param resourceId
     * Resource ID
     * @return
     * Parcelable object
     */
    public static Speech createSpeechFromTextResource(int resourceId) {
        Speech s = new Speech();
        s.mSpeechType = SPEECH_TYPE_TEXT_RESOURCE;
        s.mTextResId = resourceId;
        return s;
    }

    /**
     * Create Speech From Audio Resource.
     * @param resourceId
     * Resource ID
     * @return Parcelable object
     */
    public static Speech createSpeechFromAudioResource(int resourceId) {
        Speech s = new Speech();
        s.mSpeechType = SPEECH_TYPE_AUDIO_RESOURCE;
        s.mAudioResId = resourceId;
        s.mAudioType = AUDIO_TYPE_PCM_8K; // default
        return s;
    }

    /**
     * Create Speech From Audio Resource.
     * @param resourceId
     * Resource ID
     * @param audioType
     * Audio type
     * @return Parcelable object
     */
    public static Speech createSpeechFromAudioResource(int resourceId, int audioType) {
        Log.d(TAG, "audioType=" + audioTypeToString(audioType));
        Speech s = new Speech();
        s.mSpeechType = SPEECH_TYPE_AUDIO_RESOURCE;
        s.mAudioResId = resourceId;
        s.mAudioType = audioType;
        return s;
    }

    /**
     * Create Speech From Audio File.
     * @param file
     * File name
     * @param audioType
     * Audio type
     * @return
     * Parcelable object
     */
    public static Speech createSpeechFromAudioFile(String file, int audioType) {
        Log.d(TAG, "file=" + file + ", audioType=" + audioTypeToString(audioType));
        Speech s = new Speech();
        s.mSpeechType = SPEECH_TYPE_AUDIO_FILE;
        s.file = removeExtension(file);
        s.mAudioType = audioType;
        return s;
    }

    /**
     * Create Speech From Text with Digits.
     * @param text
     * Text string
     * @return Parcelable object
     */
    public static Speech createSpeechFromTextWithDigits(String text) {
        return createSpeechFromText(text, SPEECH_TYPE_TEXT_WITH_DIGITS);
    }

    /**
     * Create Speech From Text.
     * @param text
     * Text string
     * @param speechType
     * Speech type
     * @return Parcelable object
     */
    private static Speech createSpeechFromText(String text, int speechType) {
        Speech s = new Speech();
        s.mSpeechType = speechType;
        s.mText = text;
        return s;
    }

    private static String removeExtension(String filename) {
        if (filename == null) {
            return filename;
        }
        int index = filename.lastIndexOf('.');
        if (index == -1) {
            return filename;
        }
        return filename.substring(0, index);
    }

    private static String audioTypeToString(int audioType) {
        switch (audioType) {
        case AUDIO_TYPE_PCM_8K:
            return "AUDIO_TYPE_PCM_8K";
        case AUDIO_TYPE_PCM_16K:
            return "AUDIO_TYPE_PCM_16K";
        case AUDIO_TYPE_AMR:
            return "AUDIO_TYPE_PCM_16K";
        case AUDIO_TYPE_SPEEX_8K:
            return "AUDIO_TYPE_SPEEX_8K";
        case AUDIO_TYPE_SPEEX_16K:
            return "AUDIO_TYPE_SPEEX_16K";
        case AUDIO_TYPE_SPEEX_32K:
            return "AUDIO_TYPE_SPEEX_32K";
        default:
            return "UNKNOWN_AUDIO_TYPE";
        }
    }

    private Speech() {
    }

    /**
     *  Hide automatically by SDK TEAM [U12000] 
     *  @hide
     *  @return always return 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     * @param out
     * Container for a message (data and object references) that can be sent through an IBinder.
     * @param flags
     * No used in the API
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mSpeechType);
        out.writeInt(mAudioType);
        out.writeString(mText);
        out.writeByteArray(mAudio);
        out.writeInt(mTextResId);
        out.writeInt(mAudioResId);
        out.writeString(file);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     * @param in
     * Container for a message (data and object references) that can be sent through an IBinder.
     */
    public void readFromParcel(Parcel in) {
        mSpeechType = in.readInt();
        mAudioType = in.readInt();
        mText = in.readString();
        mAudio = in.createByteArray();
        mTextResId = in.readInt();
        mAudioResId = in.readInt();
        file = in.readString();
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     * @return Text string
     */
    public String getText() {
        return mText;
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     * @return Filename string
     */
    public String getFilename() {
        return file;
    }
    
    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     * @return Audio byte array
     */
    public byte[] getAudio() {
        return mAudio;
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     * @return Audio type
     */
    public int getAudioType() {
        return mAudioType;
    }

    /**
     * Get Speech Type.
     * @return Speech type
     */
    public int getSpeechType() {
        return mSpeechType;
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     * @hide
     * @param context
     * Context instance
     * @throws IOException
     * Throw IOException
     */
    public void convert(Context context) throws IOException {
        switch (mSpeechType) {
        case SPEECH_TYPE_AUDIO_RESOURCE:
            convertAudioResource(context);
            break;
        case SPEECH_TYPE_TEXT_RESOURCE:
            convertTextResource(context);
            break;
        case SPEECH_TYPE_AUDIO_FILE:
            convertAudioFile(context);
            break;
        case SPEECH_TYPE_TEXT_WITH_DIGITS:
            insertSpaceBetweenDigits();
            break;
        default:
            // no conversion needed
        }
    }

    private void convertAudioResource(Context context) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = getResources(context).openRawResource(mAudioResId);
        int i = -1;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        is.close();
        baos.close();
        mAudio = baos.toByteArray();
    }

    //{ 2013/01/17 Simon_Wu (M7_UL_JB_50#13149) begin
    private void convertTextResource(Context context)
    {
        // Resources res = getResources(context);
        // mText = res.getString(mTextResId);
        
        mText = context.getResources().getString(mTextResId);
    }
    //} 2013/01/17 Simon_Wu (M7_UL_JB_50#13149) end

    private Resources getResources(Context context) {
        Resources res = context.getResources();
        AssetManager am = res.getAssets();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        String language = getLanguage(context);
        config.locale = languageToLocale(language);
        res = new Resources(am, dm, config);
        return res;
    }

    private void convertAudioFile(Context context) throws IOException {
        String language = getLanguage(context);
        String path1 = PROMPT_LOCATION_DEFAULT + language + "_prompt/" + file;
        String path2 = PROMPT_LOCATION + language + "_prompt/" + file;
        File f = null;
        File f1 = new File(path1);
        File f2 = new File(path2);
        if (f1.exists()) {
            f = f1;
        } else if (f2.exists()) {
            f = f2;
        } else {
            //mSpeechType = SPEECH_TYPE_TEXT;
            //mText = "";
            //Log.d(TAG, "Both paths do not exist. Use empty string text.");
            Log.d(TAG, "Both paths do not exist.");
            return;
        }
        Log.d(TAG, "path=" + f.getAbsolutePath());
        
        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;
        try
        {
            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(f);
            
            int i = -1;
            while ((i = fis.read()) != -1)
            {
                baos.write(i);
            }
            
            mAudio = baos.toByteArray();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fis != null)
            {
                fis.close();
            }
            
            if (baos != null)
            {
                baos.close();
            }
        }
    }

    private String getLanguage(Context context) {
        String language = Settings.System.getString(context.getContentResolver(), HTCSPEAK_USED_LANG);
        if (language == null) {
            language = Settings.System.getString(context.getContentResolver(), HTCSPEAK_DEFAULT_LANG);
        }
        if (language == null) {
            language = "en_US";
        }
        Log.d(TAG, "language=" + language);
        return language;
    }

    private Locale languageToLocale(String language) {
        if ("zh_TW".equals(language)) {
            return Locale.TRADITIONAL_CHINESE;
        } else if ("zh_CN".equals(language)) {
            return Locale.SIMPLIFIED_CHINESE;
        } else {
            return new Locale(language);
        }
    }

    private void insertSpaceBetweenDigits() {
        if (mText == null) return;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mText.length(); i++) {
            char c = mText.charAt(i);
            if (Character.isDigit(c)) {
                sb.append(' ');
            }
            sb.append(c);
        }
        mText = sb.toString();
    }

}
