package com.htc.lib1.exo.wrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.os.Parcel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.htc.lib1.exo.utilities.LOG;
import com.htc.lib1.exo.wrap.IPlayer;
import com.htc.lib1.exo.wrap.ClosedCaption;
import com.htc.lib1.exo.wrap.ClosedCaption.Region;

import android.graphics.BitmapFactory;
class HtcWrapMediaPlayer extends MediaPlayer implements IPlayer
{
    private static String TAG = "HtcWrapMediaPlayer";

    public static final String MEDIA_MIMETYPE_TEXT_SMPTETT = "application/x-smptett";

    HashMap<String, MethodItem> mMethodTable = new HashMap<String, MethodItem>();

    public HtcWrapMediaPlayer()
    {
        super();
        LOG.I(TAG,"HtcWrapMediaPlayer");

        Method[] methods = MediaPlayer.class.getDeclaredMethods();
        for(Method m : methods)
        {
            if("stepFrame".equals(m.getName()))
            {
                mMethodTable.put(m.getName(), new MethodItem(m, true));
            }
            else if ("getMetadata".equals(m.getName()))
            {
                mMethodTable.put(m.getName(), new MethodItem(m, true));
            }
        }

        setOnErrorListener(mOnErrorListener);
        setOnPreparedListener(mOnPreparedListener);
        setOnInfoListener(mOnInfoListener);
        setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        setOnSeekCompleteListener(mOnSeekCompleteListener);
        setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        setOnTimedTextListener(mOnTimedTextListener);
        setOnCompletionListener(mOnCompletionListener);
    }

    private Class<?> findClass(String className)
    {
        Class clz = null;
        try{
            clz= Class.forName(className);
        }catch( Exception e ){
            LOG.W(TAG, e);
        }
        return clz;
    }

    private Method findMethodAndToList(String functionName, Class<?>[] paramTypes)
    {
        try{
            Method method = MediaPlayer.class.getDeclaredMethod(functionName, paramTypes);

            mMethodTable.put(functionName, new MethodItem(method, true));

            return method;
        }catch( Exception e ){
            mMethodTable.put(functionName, new MethodItem(null, false));
            LOG.W(TAG, e);
        }
        return null;
    }
    
    private Method getMethodFromList(String functionName, Class<?>[] paramTypes)
    {
        MethodItem item = mMethodTable.get(functionName);

        if(item == null)
        {
            return findMethodAndToList(functionName, paramTypes);
        }
        else if(item != null && item.isSupport)
        {
            return item.method;
        }
        return null;
    }

    public void invokeEx(Parcel request, Parcel reply){
        LOG.I(TAG,"invokeEx");
        Method method = getMethodFromList("invoke", new Class[]{Parcel.class, Parcel.class});

        if (method == null)
        {
            method = getMethodFromList("htc_invoke", new Class[]{Parcel.class, Parcel.class});
        }

        if (method == null)
        {
            method = getMethodFromList("_invoke", new Class[]{Parcel.class, Parcel.class});
        }

        if (method == null)
        {
            LOG.I(TAG , "invoke failed , no method");
            return;
        }

        try{

          //Parameters
          Object[] params= new Object[2];
          params[0]= request;
          params[1]= reply;

          method.invoke((MediaPlayer)this, request, reply);
        } catch (IllegalAccessException x) {
            LOG.W(TAG, "IllegalAccessException");
        } catch (InvocationTargetException x) {
            LOG.W(TAG, "InvocationTargetException");
        }catch( Exception e ){
            LOG.W(TAG, e);
        }
    }

    public boolean isHTCDevice()
    {
        LOG.I(TAG,"isHTCDevice");
        Method method = getMethodFromList("captureFrame", (Class[])null);
        if (method == null)
        {
            LOG.I(TAG,"isHTCDevice false");
            return false;
        }
        LOG.I(TAG,"isHTCDevice true");
        return true;
    }

    public Bitmap captureFrameEx(){
        LOG.I(TAG,"captureFrame");
        Method method = getMethodFromList("captureFrame", (Class[])null);
        if (method == null)
        {
            LOG.I(TAG , "invoke failed , no method");
            return null;
        }

        Bitmap bitmap = null;
        try
        {
          bitmap = (Bitmap) method.invoke((MediaPlayer)this);

        } catch (IllegalAccessException x) {
            LOG.W(TAG, "IllegalAccessException");
        } catch (InvocationTargetException x) {
            LOG.W(TAG, "InvocationTargetException");
        }catch( Exception e ){
            LOG.W(TAG, e);
        }
        return bitmap;
    }

    protected boolean stepFrameEx(boolean bForward){
        Method method = getMethodFromList("stepFrame", new Class[]{Boolean.class});
        if (method == null)
        {
            LOG.I(TAG , "stepForward failed , no method");
            return false;
        }
        
        Boolean rtn = false;
        try{

          rtn = (Boolean) method.invoke((MediaPlayer)this, new Object[]{bForward});

        }catch( Exception e ){
            LOG.W(TAG, e);
        }
        return rtn;
    }

    public void setVolume (float leftVolume, float rightVolume)
    {
       LOG.I(TAG, "setVolume(" + leftVolume + "," + rightVolume + ")");
       super.setVolume(leftVolume,rightVolume);
    }
    public void setCharsetEx(String charset){
        //super.setCharset_htc(charset);
        Method method = getMethodFromList("setCharset_htc", new Class[]{String.class});
        if (method == null)
        {
            LOG.I(TAG , "setCharset failed , no method");
            return;
        }

        try{
          method.invoke((MediaPlayer)this, new Object[]{charset});

        }catch( Exception e ){
            LOG.W(TAG, e);
        }
    }

    public void deselectLanguageEx(String language){

        Method method = getMethodFromList("deselectLanguage", new Class[]{String.class});
        if (method == null)
        {
            LOG.I(TAG , "deselectLanguage failed , no method");
            return;
        }

        try{
          method.invoke((MediaPlayer)this, new Object[]{language});

        }catch( Exception e ){
            LOG.W(TAG, e);
        }
    }

    public void addTimedTextSourceEx(String textPath, String mimeType){

        Method method = getMethodFromList("addTimedTextSource", new Class[]{String.class, String.class});
        if (method == null)
        {
            LOG.I(TAG , "addTimedTextSource failed , no method");
            return;
        }

        try{
          method.invoke((MediaPlayer)this, new Object[]{textPath, mimeType});

        }catch( Exception e ){
            LOG.W(TAG, e);
        }
    }

    public Object getMetadataEx(final boolean update_only,
                                final boolean apply_filter){
        Method method = getMethodFromList("getMetadata", new Class[]{Boolean.class, Boolean.class});
        if (method == null)
        {
            LOG.I(TAG , "getMetadata failed , no method");
            return null;
        }
        Object rtn = null;
        try{
          rtn = method.invoke((MediaPlayer)this, new Object[]{update_only, apply_filter});

        }catch( Exception e ){
            LOG.W(TAG, e);
        }
        return rtn;
    }

    public void setPlaybackParams(/*PlaybackParams (API:23)*/ Object params){
        Class<?> clz = findClass("android.media.PlaybackParams");
        if (clz == null)
        {
            LOG.I(TAG , "setPlaybackParams failed , no PlaybackParams");
            return;
        }

        if (clz.isInstance(params) == false)
        {
            LOG.I(TAG , "setPlaybackParams failed , not PlaybackParams");
            return;
        }

        Method method = getMethodFromList("setPlaybackParams", new Class[]{clz});
        if (method == null)
        {
            LOG.I(TAG , "setPlaybackParams failed , no method");
            return;
        }
        try{
            method.invoke((MediaPlayer)this, new Object[]{params});
        }catch( Exception e ){
            LOG.W(TAG, e);
        }
    }

    public boolean isPauseable()
    {
        Object metadata = getMetadataEx(false, false);
        boolean rtn = parseMetaData(metadata, "PAUSE_AVAILABLE", true);

        LOG.I(TAG, "isPauseable = " + rtn);
        return rtn;
    }

    public boolean isSeekable()
    {
        Object metadata = getMetadataEx(false, false);
        boolean rtn = parseMetaData(metadata, "SEEK_AVAILABLE", true);

        LOG.I(TAG, "isSeekable = " + rtn);
        return rtn;
    }

    private <T> T parseMetaData (Object metadata, final String strKey, T defValue)
    {
        int key = -1;

        try
        {
            Field f = metadata.getClass().getField(strKey);
            Class<?> t = f.getType();
            if(t == int.class){
                key = f.getInt(null);
            }
        }
        catch (Exception e)
        {
            LOG.W(TAG,e);
            return defValue;
        }
        return parseMetaData (metadata, key,defValue);
    }
    private <T> T parseMetaData (Object metadata, final int key, T defValue)
    {
        LOG.I(TAG, "parseMetaData = " + key);
        T rtn = defValue;
        try
        {
            Method methodHas = null;

            Method methodGet = null;

            Method[] methods = metadata.getClass().getDeclaredMethods();
            for(Method m : methods)
            {
                if("has".equals(m.getName()))
                {
                    methodHas = m;
                }
                else{
                    if("getString".equals(m.getName()) && rtn instanceof String)
                    {
                        methodGet = m;
                    }
                    else if("getBoolean".equals(m.getName()) && rtn instanceof Boolean)
                    {
                        methodGet = m;
                    }
                    else if("getInt".equals(m.getName()) && rtn instanceof Integer)
                    {
                        methodGet = m;
                    }
                    else if("getByteArray".equals(m.getName()) && rtn instanceof byte[])
                    {
                        methodGet = m;
                    }
                }
            }

            if (methodHas == null)
            {
                LOG.I(TAG, "parseMetaData failed no methodHas");
                return rtn;
            }

            if (methodGet == null)
            {
                LOG.I(TAG, "parseMetaData failed no methodGet");
                return rtn;
            }

            boolean hasKey = false;
            if(methodHas != null) {
                hasKey = (Boolean) (methodHas.invoke(metadata, key));
            }

            if (hasKey == false)
            {
                LOG.I(TAG, "parseMetaData failed no key : " + key);
                return rtn;
            }


            if(hasKey && methodGet != null) {
                rtn = (T) (methodGet.invoke(metadata, key));
            }
            LOG.I(TAG, "parseMetaData value = " + rtn);

        }
        catch(Exception e)
        {
            LOG.I(TAG, "parseMetaData faled");
        }
        return rtn;
    }

    public Bitmap getAlbumArt()
    {
        int ALBUM_ART = -1;  //18
        Bitmap rtn = null;
        Object metadata = getMetadataEx(false, false);
        try
        {
            Field f = metadata.getClass().getField("ALBUM_ART");
            Class<?> t = f.getType();
            if(t == int.class){
                ALBUM_ART = f.getInt(null);
            }
        }
        catch (Exception e)
        {
            LOG.W(TAG,e);
        }

        Method methodHas = null;
        Method methodGetByteArray = null;
        try{
            Method[] methods = metadata.getClass().getDeclaredMethods();
            for(Method m : methods)
            {
                if("has".equals(m.getName()))
                {
                    methodHas = m;
                }
                else if("getByteArray".equals(m.getName()))
                {
                    methodGetByteArray = m;
                }
            }

            boolean hasArt = false;
            if(methodHas != null) {
                hasArt = (Boolean) (methodHas.invoke(metadata, ALBUM_ART));
            }
            byte[] array = null;
            if(hasArt && methodGetByteArray != null) {
                array = (byte[]) (methodGetByteArray.invoke(metadata, ALBUM_ART));
            }

            rtn = BitmapFactory.decodeByteArray(array, 0 , array.length);
        }catch( Exception e ){
            LOG.W(TAG, e);
        }

        return rtn;
    }
    public void setAnchorToMediaPlayer(Context context, Object root)
    {
        /*
         * final SubtitleController controller = new SubtitleController(activity, mMediaPlayer.getMediaTimeProvider(), mMediaPlayer);
         * controller.registerRenderer(new WebVttRenderer(activity));
         * mMediaPlayer.setSubtitleAnchor(controller, mRoot);
         */

        try
        {
            //LOG.I(TAG,"setAnchorToMediaPlayer 1");
            Constructor<?>[] SubtitleControllerConstructors = Class.forName("android.media.SubtitleController").getConstructors();

            //LOG.I(TAG,"setAnchorToMediaPlayer 2");
            if (SubtitleControllerConstructors == null) return;

            //LOG.I(TAG,"setAnchorToMediaPlayer 3");
            Object controller = null;
            for(Constructor constructor : SubtitleControllerConstructors)
            {
                controller = constructor.newInstance(context , getMediaTimeProviderEx(), this);
                if (controller != null) break;
            }

            Object webvttRenderer = null;
            {           
                //LOG.I(TAG,"setAnchorToMediaPlayer setwebvtt 1");
                Constructor<?> WebVttRendererConstructor = Class.forName("android.media.WebVttRenderer").getConstructor(Context.class);
                //LOG.I(TAG,"setAnchorToMediaPlayer setwebvtt 2");
                
                if (WebVttRendererConstructor == null) return;
                //LOG.I(TAG,"setAnchorToMediaPlayer setwebvtt 3");
                
                webvttRenderer = WebVttRendererConstructor.newInstance(context);
                //LOG.I(TAG,"setAnchorToMediaPlayer setwebvtt 4");
            }

            Object cea608Renderer = null;
            {
                //LOG.I(TAG,"setAnchorToMediaPlayer cea608 1");
                Constructor<?> ClosedCaptionConstructor = Class.forName("android.media.ClosedCaptionRenderer").getConstructor(Context.class);
                //LOG.I(TAG,"setAnchorToMediaPlayer cea608 2");
                
                if (ClosedCaptionConstructor == null) return;
                //LOG.I(TAG,"setAnchorToMediaPlayer cea608 3");
                
                cea608Renderer = ClosedCaptionConstructor.newInstance(context);
                //LOG.I(TAG,"setAnchorToMediaPlayer cea608 4");
            }

            Object ttmlRenderer = null;
            {
                //LOG.I(TAG,"setAnchorToMediaPlayer ttml 1");
                Constructor<?> ClosedCaptionConstructor = Class.forName("android.media.TtmlRenderer").getConstructor(Context.class);
                //LOG.I(TAG,"setAnchorToMediaPlayer ttml 2");

                if (ClosedCaptionConstructor == null) return;
                //LOG.I(TAG,"setAnchorToMediaPlayer ttml 3");

                ttmlRenderer = ClosedCaptionConstructor.newInstance(context);
                //LOG.I(TAG,"setAnchorToMediaPlayer ttml 4");
            }
            
            Method[] methods = Class.forName("android.media.SubtitleController").getDeclaredMethods();
            Method registerRenderer = null;
            for(Method method : methods)
            {
                //LOG.I(TAG, "setSubtitleAnchorEx() method " + method.getSimpleName());
                if (method.getName().endsWith("registerRenderer"))
                {
                    registerRenderer = method;
                    break;
                }
            }
            if (webvttRenderer != null)
            {
                //LOG.I(TAG,"setAnchorToMediaPlayer register webvtt");
                registerRenderer.invoke(controller, webvttRenderer);
            }

            if (cea608Renderer != null)
            {
                //LOG.I(TAG,"setAnchorToMediaPlayer register cea608");
                registerRenderer.invoke(controller, cea608Renderer);
            }

            if (ttmlRenderer != null)
            {
                //LOG.I(TAG,"setAnchorToMediaPlayer register ttml");
                registerRenderer.invoke(controller, ttmlRenderer);
            }

            //LOG.I(TAG,"setAnchorToMediaPlayer 14");
            setSubtitleAnchorEx(controller, root);
            //LOG.I(TAG,"setAnchorToMediaPlayer 15");
        }
        catch (Exception e)
        {
            LOG.I(TAG,e);
        }
    }
    public Object getMediaTimeProviderEx()
    {
        Method method = getMethodFromList("getMediaTimeProvider", (Class[])null);
        if (method == null)
        {
            LOG.I(TAG , "getMediaTimeProvider failed , no method");
            return null;
        }
        Object rtn = null;
        try{
          rtn = method.invoke((MediaPlayer)this);

        }catch( Exception e ){
            LOG.W(TAG, e);
        }
        return rtn;
    }
    public void setSubtitleAnchorEx(/*SubtitleController*/ Object controller,  /*SubtitleController.Anchor*/ Object anchor) {
        LOG.I(TAG , "setSubtitleAnchorEx in");
        try{
            Method[] methods = MediaPlayer.class.getDeclaredMethods();
            Method setSubtitleAnchor = null;
            for(Method method : methods)
            {
                //LOG.I(TAG, "setSubtitleAnchorEx() method " + method.getSimpleName());
                if (method.getName().endsWith("setSubtitleAnchor"))
                {
                    setSubtitleAnchor = method;
                    break;
                }
            }

            if (setSubtitleAnchor == null) return;

            Object myOnClosedCaptionChangeListener = null;
            Class<?>[] innerClasses = Class.forName("android.media.SubtitleController").getDeclaredClasses();
            for (Class<?> interfaze : innerClasses) {
                LOG.I(TAG, "setSubtitleAnchorEx() interfaze " + interfaze.getSimpleName());
                if (interfaze.getSimpleName().equalsIgnoreCase("Anchor")) {
                    Class<?>[] classArray = new Class<?>[1];
                    classArray[0] = interfaze;
                    myOnClosedCaptionChangeListener = Proxy.newProxyInstance(interfaze.getClassLoader(), classArray, (InvocationHandler) anchor);
                    setSubtitleAnchor.invoke( (MediaPlayer)this, controller, myOnClosedCaptionChangeListener);
                    //return Object;
                    LOG.I(TAG , "setSubtitleAnchorEx out");
                    return;
                }
            }
        }catch( Exception e ){
            LOG.W(TAG, e);
        }
        LOG.I(TAG , "setSubtitleAnchorEx failed");
    }

    public void setOnClosedCaptionListener(InvocationHandler handler)
    {
        try
        {
            Class<?> clazz= Class.forName("android.media.MediaPlayer");
            Class<?>[] allInterface= clazz.getDeclaredClasses();
            Class<?> OnClosedCaptionListener = null;
            for (Class<?> myinterface : allInterface)
            {
                LOG.I(TAG,"setOnClosedCaptionListener myinterface : " + myinterface.getSimpleName());
                if ("OnClosedCaptionListener".equals(myinterface.getSimpleName()))
                {
                    OnClosedCaptionListener = myinterface;
                    break;
                }
            }

            if (OnClosedCaptionListener == null) return ;

            Class<?>[] classArray = new Class<?>[1];
            classArray[0] = OnClosedCaptionListener;
            Object listener = Proxy.newProxyInstance(OnClosedCaptionListener.getClassLoader(), classArray , handler);

            if (listener == null) return ;

            Method method = getMethodFromList("setOnClosedCaptionListener", new Class[]{OnClosedCaptionListener});

            if (method == null) return ;

            method.invoke(this, listener);
        }
        catch(Exception e)
        {

        }
    }

    public com.htc.lib1.exo.wrap.TrackInfo[] getTrackInfoEx()
    {
        LOG.I(TAG,"getTrackInfo");
        android.media.MediaPlayer.TrackInfo[] infos =  getTrackInfo();
        if (infos == null) return null;

        int length = infos.length;
        if (infos.length < 0) return null;

        com.htc.lib1.exo.wrap.TrackInfo[] newInfos = new com.htc.lib1.exo.wrap.TrackInfo[length]; 

        for (int i = 0; i < length ; i++)
        {
        	android.media.MediaPlayer.TrackInfo oldInfo = infos[i];
            newInfos[i] = new com.htc.lib1.exo.wrap.TrackInfo(oldInfo.getTrackType(), oldInfo.getFormat());
        }
        return newInfos;
    }
    /* OnClosedCaptionListener */
    private OnClosedCaptionHandler /*MediaPlayer.OnClosedCaptionListener*/ mClosedCaptionListener = /*new MediaPlayer.OnClosedCaptionListener()*/ new OnClosedCaptionHandler();

    class OnClosedCaptionHandler implements InvocationHandler{

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = null;
            try {
                //if (args != null) 
                {
                    LOG.I(TAG,"OnClosedCaptionListenerextend.invoke " + method.getName());
                    if (method.getName().equals("onClosedCaption") ) 
                    {
                        ClosedCaption caption = new ClosedCaption(args[1]);
                        onClosedCaption((MediaPlayer) args[0], caption);
                    }
                    else if (method.getName().equals("onLanguageList") )
                    {
                        onLanguageList((MediaPlayer) args[0], (List) args[1]);
                    }
                    else if (method.getName().equals("onRegionList") )
                    {
                        List<?> oldList = (List) args[1];
                        if (oldList != null)
                        {
                            List<Region> newList = new ArrayList<Region>();
                            for(Object item : oldList)
                            {
                                Region region = new Region(item);

                                newList.add(region);
                            }

                            onRegionList((MediaPlayer) args[0], newList);
                        }
                    }
                    else if (method.getName().equals("onLogo") )
                    {
                        ClosedCaption caption = new ClosedCaption(args[1]);
                        onLogo((MediaPlayer) args[0], caption);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
            }
            return result;
        }

        //@Override
        public void onClosedCaption(MediaPlayer mp, ClosedCaption cc)
        {
            LOG.W(TAG,"OOD onSubtitleChange");
        }

        //@Override
        public void onLanguageList(MediaPlayer mp, List list)
        {
            LOG.W(TAG,"OOD onLanguageList");
        }

        //@Override
        public void onRegionList(MediaPlayer mp, List list)
        {
            LOG.W(TAG,"OOD onRegionList ");
        }

        //@Override
        public void onLogo(MediaPlayer mp, ClosedCaption logo)
        {
            LOG.W(TAG,"OOD onLogo ");
        }
    };

    //**** OnErrorListener Start ***
    private IPlayer.OnErrorListener mUpperOnErrorListener = null;
    /* OnErrorListener */
    private final MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer paramMediaPlayer, int nFramework_err, int nImplement_err)
        {
            if (mUpperOnErrorListener != null)
            {
                return mUpperOnErrorListener.onError( nFramework_err, nImplement_err);
            }
            return true;
        }
    };
    public void setOnErrorListener (IPlayer.OnErrorListener listener)
    {
        LOG.I(TAG,"setOnErrorListener");
        mUpperOnErrorListener = listener;
    }
    //**** OnErrorListener End ***

    //**** OnPreparedListener Start ***
    private IPlayer.OnPreparedListener mUpperOnPreparedListener = null;
    private final MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer paramMediaPlayer)
        {
            if (mUpperOnPreparedListener != null)
            {
                mUpperOnPreparedListener.onPrepared();
            }
            else
            {
                LOG.I(TAG,"onPrepared skipped, no mUpperOnPreparedListener");
            }
        }
    };

    public void setOnPreparedListener (IPlayer.OnPreparedListener listener)
    {
        LOG.I(TAG,"setOnPreparedListener");
        mUpperOnPreparedListener = listener;
    }
    //**** OnPreparedListener End ***

    //**** OnInfoListener Start ***
    private IPlayer.OnInfoListener mUpperOnInfoListener = null;
    private final MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra)
        {
            if (mUpperOnInfoListener != null)
            {
                mUpperOnInfoListener.onInfo(what, extra);
            }
            else
            {
                LOG.I(TAG,"onInfo skipped, no mUpperOnInfoListener");
            }
            return true;
        }
    };
    public void setOnInfoListener(IPlayer.OnInfoListener listener)
    {
        LOG.I(TAG,"setOnInfoListener");
        mUpperOnInfoListener = listener;
    }
    //**** OnInfoListener End ***

    //**** OnBufferingUpdateListener Start ***
    private IPlayer.OnBufferingUpdateListener mUpperOnBufferingUpdateListener = null;
    private final MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent)
        {
            if (mUpperOnBufferingUpdateListener != null)
            {
                mUpperOnBufferingUpdateListener.onBufferingUpdate(percent);
            }
            else
            {
                LOG.I(TAG,"onBufferingUpdate skipped, no mUpperOnBufferingUpdateListener");
            }
        }
    };
    public void setOnBufferingUpdateListener(IPlayer.OnBufferingUpdateListener listener)
    {
        LOG.I(TAG,"setOnBufferingUpdateListener");
        mUpperOnBufferingUpdateListener = listener;
    }
    //**** OnBufferingUpdateListener End ***

    //**** OnSeekCompleteListener Start ***
    private IPlayer.OnSeekCompleteListener mUpperOnSeekCompleteListener = null;
    private final MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp)
        {
            if (mUpperOnSeekCompleteListener != null)
            {
                mUpperOnSeekCompleteListener.onSeekComplete();
            }
            else
            {
                LOG.I(TAG,"onSeekComplete skipped, no mUpperOnSeekCompleteListener");
            }
        }
    };
    public void setOnSeekCompleteListener(IPlayer.OnSeekCompleteListener listener)
    {
        LOG.I(TAG,"setOnSeekCompleteListener");
        mUpperOnSeekCompleteListener = listener;
    }
    //**** OnSeekCompleteListener End ***

    //**** OnVideoSizeChangedListener Start ***
    private IPlayer.OnVideoSizeChangedListener mUpperOnVideoSizeChangedListener = null;
    private final MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height)
        {
            if (mUpperOnVideoSizeChangedListener != null)
            {
                mUpperOnVideoSizeChangedListener.onVideoSizeChanged(width ,height);
            }
            else
            {
                LOG.I(TAG,"onVideoSizeChanged skipped, no mUpperOnVideoSizeChangedListener");
            }
        }
    };
    public void setOnVideoSizeChangedListener(IPlayer.OnVideoSizeChangedListener listener)
    {
        LOG.I(TAG,"setOnVideoSizeChangedListener");
        mUpperOnVideoSizeChangedListener = listener;
    }
    //**** OnVideoSizeChangedListener End ***

    //**** OnTimedTextListener Start ***
    private IPlayer.OnTimedTextListener mUpperOnTimedTextListener = null;
    private final MediaPlayer.OnTimedTextListener mOnTimedTextListener = new MediaPlayer.OnTimedTextListener() {
        @Override
        public void onTimedText(MediaPlayer mp, TimedText text)
        {
            if (mUpperOnTimedTextListener != null)
            {
                mUpperOnTimedTextListener.onTimedText(text);
            }
            else
            {
                LOG.I(TAG,"onTimedText skipped, no mUpperOnTimedTextListener");
            }
        }
    };
    public void setOnTimedTextListener(IPlayer.OnTimedTextListener listener)
    {
        LOG.I(TAG,"setOnTimedTextListener");
        mUpperOnTimedTextListener = listener;
    }
    //**** OnTimedTextListener End ***

    //**** OnCompletionListener Start ***
    private IPlayer.OnCompletionListener mUpperOnCompletionListener = null;
    private final MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp)
        {
            if (mUpperOnCompletionListener != null)
            {
                mUpperOnCompletionListener.onCompletion();
            }
            else
            {
                LOG.I(TAG,"onCompletion skipped, no mUpperOnCompletionListener");
            }
        }
    };
    public void setOnCompletionListener(IPlayer.OnCompletionListener listener)
    {
        LOG.I(TAG,"setOnCompletionListener");
        mUpperOnCompletionListener = listener;
    }
    //**** OnCompletionListener End ***

    private class MethodItem
    {
        Method method;
        boolean isSupport;

        public MethodItem(Method method, boolean isSupport)
        {
            this.method = method;
            this.isSupport = isSupport;
        }
    }
}
