package com.htc.lib1.videohighlights;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.htc.lib0.customization.HtcWrapCustomizationManager;
import com.htc.lib0.customization.HtcWrapCustomizationReader;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;

public class VideoHighlightsPlayerLoader {
	protected final static Logger LOG = Logger.getLogger(VideoHighlightsPlayerLoader.class.getName());

	private static VideoHighlightsProxyPlayer mInstance = null;
	private static ArrayList<HostPacakgeInfo> mHsotInfoList = new ArrayList<HostPacakgeInfo>();
	private static boolean bIsInitialized = false;
	private static String mLastPackageName = null;
    private static Context mContext;
    private static ArrayList<Bundle> mPluginList;
	private static final String ZERO_PACKAGE_NAME = "com.htc.zero";
	private static final String ACTION_LAUNCH_VIDEO_HIGHLIGHTS = "com.htc.videohighlight.launch";
	private static final String VH_CLASS_NAME = "com.htc.videohighlights.VideoHighlightActivity6";
	private static final float ZERO_SUPPORT_VERSION = 2.0f;

    private static boolean s_isGalleryPluginExisting = false;
    private static Method s_isSupportedInGalleryPlugin = null;
    
    private static class PluginBundle {
        private static class Package {
            private static final String KEY = "package";
            private static final String ALBUM = "com.htc.album";
            private static final String ZOE = "com.htc.zero";
        }

        private static class ClassName {
            private static final String KEY = "className";
            private static String VALUE = "com.htc.videohighlights.DispatcherActivity";
        }

        private static class PrimaryText {
            private static final String KEY = "plugin_primary_text";
            private static final String RES_KEY = "plugin_zoe_primary_text";
        }

        private static class SecondaryText {
            private static final String KEY = "plugin_secondary_text";
            private static final String RES_KEY = "plugin_zoe_secondary_text";
        }

        private static class Order {
            private static final String KEY = "plugin_order";
            private static final String RES_KEY = "plugin_zoe_order";
        }

        private static class Background {
            private static final String KEY = "plugin_background";
            private static final String RES_KEY = "plugin_zoe_background";
        }

        private static class SupportType {
            private static class Drm {
                private static final String KEY = "plugin_support_type_drm";
                private static final String RES_KEY = "plugin_zoe_support_type_drm";
            }

            private static class Cloud {
                private static final String KEY = "plugin_support_type_cloud";
                private static final String RES_KEY = "plugin_zoe_support_type_cloud";
            }

            private static class Zoe {
                private static final String KEY = "plugin_support_type_zoe";
                private static final String RES_KEY = "plugin_zoe_support_type_zoe";
            }
        }
    }
	
	/*
	 * Get a VideoHighlightsProxyPlayer player instance, 
	 */
	public static synchronized VideoHighlightsProxyPlayer getPlayerInstance(final Context context, boolean bRefresh) {
		
		return getPlayerInstance(context,null, bRefresh);
	}
	
	/*
	 * Get a VideoHighlightsProxyPlayer player instance, 
	 */
	public static synchronized VideoHighlightsProxyPlayer getPlayerInstance(final Context context,final String packageName, boolean bRefresh) {
		if(mLastPackageName!=null && packageName!=null && !mLastPackageName.equals(packageName)){
			bRefresh = true;
		}
		
		if(mInstance!=null &&!mInstance.isUpToDate(context)){
			bRefresh = true;
			
			LOG.log(Level.INFO,"Detect out-of-date instance, force to refresh");
		}
		
		if (bRefresh == true) {
			bIsInitialized = false;
			mInstance = null;
		}
		
		LOG.log(Level.INFO, "packageName: "+packageName);
		
		
		if (context == null) {
			LOG.log(Level.INFO, "[Info] Context is null");
			return null;
		}
		
		if (bIsInitialized == false && mInstance == null) {			
			Context packageContext = null;
			
			// initialize the potential host packages
			
			if(packageName!=null){
				lookForHost(context, packageName);
			}else{
				lookForHost(context);
			}
			
			
			
			try {
				if (mHsotInfoList.size() > 0) {
					HostPacakgeInfo host = mHsotInfoList.get(0);
					final Fragment fragment;
					final Method method;
					final Object playerInstance;
					
					packageContext = context.createPackageContext(host.packageName, 
						Context.CONTEXT_INCLUDE_CODE|Context.CONTEXT_IGNORE_SECURITY);
					
					fragment = (Fragment) packageContext.getClassLoader().loadClass(Constants.HOST_IMPLY_CLASS).newInstance();
					/*
					Fragment.instantiate(packageContext, Constants.HOST_IMPLY_CLASS);*/

					method = fragment.getClass().getDeclaredMethod("getPreviewPlayer", Context.class);
					playerInstance = method.invoke(fragment,context);

					LOG.log(Level.INFO, "[Info] Load " + fragment.getClass().getName()
							+ " from "
							+ host.packageName+" with versionCode "+host.packageVersionCode);

					mInstance = new VideoHighlightsProxyPlayer(playerInstance,host.packageName, host.packageVersionCode);
					mLastPackageName = host.packageName;
				}
				else {
					LOG.log(Level.INFO, "[Info] No host found");
					throw new UnsupportedOperationException("Cannot find any VideoHighlights engine host");
				}
			} catch (Exception e) {
				bIsInitialized = true;
				LOG.log(Level.SEVERE, null, e);
			}
			
			bIsInitialized = true;
			
		}
		else {
			LOG.log(Level.INFO, "[Info] The instance of VideoHighlightsPlayerInterface was created: "+mInstance);
		}
		
		return mInstance;
	}
	
	private static void lookForHost(final Context context, final String packageName){
		ApplicationInfo info;
		try {
			final PackageInfo packageInfo = 
					context.getPackageManager().getPackageInfo(packageName, 0);
			info = context.getPackageManager().getApplicationInfo(packageName, 0);
			
			mHsotInfoList.clear();
			
			HostPacakgeInfo host = new HostPacakgeInfo();
			host.packageName = info.packageName;
			host.packagePath = info.sourceDir;
			if(packageInfo!=null){
				host.packageVersionCode = packageInfo.versionCode;		
			}
			
			Bundle meta = info.metaData;
			
			if (meta != null) {
				host.version = meta.getFloat(Constants.HOST_METADATA_VERSION);
				host.supportLocalSave = meta.getBoolean(Constants.HOST_METADATA_SUPPORT_LOCAL_SAVE, false);
			}
			
			mHsotInfoList.add(host);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			LOG.log(Level.WARNING, null,e);
		}
	}
	
	/*
	 * Get a VideoHighlightsProxyPlayer player instance, use existed one or create one if there is no instance 
	 */
	public static synchronized VideoHighlightsProxyPlayer getPlayerInstance(final Context context) {
		return getPlayerInstance(context, false);
	}
	
	/*
	 * Get a VideoHighlightsProxyPlayer player instance, use existed one or create one if there is no instance 
	 */
	public static synchronized VideoHighlightsProxyPlayer getPlayerInstance(final Context context, final String packageName) {
		return getPlayerInstance(context,packageName, false);
	}
	/*
	 * Return host package name
	 * This API may return NULL if the host is not found
	 */
	public static String getHostPackageName(final Context context) {
		if (bIsInitialized == false) {
			getPlayerInstance(context);
		}
		
		if (mInstance != null) {
			if (mHsotInfoList.size() > 0) {
				HostPacakgeInfo host = mHsotInfoList.get(0);
				return host.packageName;
			}
		}
		
		return null;
	}

    /*
     * Add/remove the Gallery plugin list with the correct Zoe plugin.
     * @param context Host context
     * @param pluginList The list of plugins may contain HTC Zoe client plugin, and others.
     * @return ArrayList<Bundle> The final list of plugins with the correct Zoe plugin.
     */
    public static ArrayList<Bundle> updateZoePlugin(Context context, ArrayList<Bundle> pluginList) {
        if (bIsInitialized == false) {
            getPlayerInstance(context);
        }

        mContext = context;
        mPluginList = pluginList;

        if (mPluginList == null || mPluginList.size() == 0) {
            LOG.log(Level.INFO, "invalid mPluginList");
            return mPluginList;
        }

        if (!isSupported(context)) {
            return removeZoeinPluginList();
        } else {
            return getSupportedPluginList();
        }
    }

    private static ArrayList<Bundle> removeZoeinPluginList() {
        for (Bundle bundle : mPluginList) {
            if (PluginBundle.Package.ZOE.equals(bundle.get(PluginBundle.Package.KEY))) {
                LOG.log(Level.INFO, "removeZoeinPluginList");
                mPluginList.remove(bundle);
                return mPluginList;
            }
        }
        return mPluginList;
    }

    private static ArrayList<Bundle> getSupportedPluginList() {
        for (Bundle bundle : mPluginList) {
            if (PluginBundle.Package.ZOE.equals(bundle.get(PluginBundle.Package.KEY))) {
                return getLocalSavePluginList();
            }
        }

        return createVHinGalleryToPlunginList();
    }

    private static ArrayList<Bundle> getLocalSavePluginList() {
        String packageName = getHostPackageNameSupportingLocalSave(mContext);
        LOG.log(Level.INFO, "packageName Supporting local save = " + packageName);
        if (!PluginBundle.Package.ZOE.equals(packageName)) {
            removeZoeinPluginList();
        }

        if (PluginBundle.Package.ALBUM.equals(packageName)) {
            createVHinGalleryToPlunginList();
        }

        return mPluginList;
    }

    /*
     * Return host package name supporting Local
     * @return String package name. If the host is not found, return NULL.
     */
    public static String getHostPackageNameSupportingLocalSave(final Context context) {
        if (bIsInitialized == false) {
            getPlayerInstance(context);
        }

        if (mInstance != null) {
            if (mHsotInfoList != null && mHsotInfoList.size() > 0) {
                for (HostPacakgeInfo hostInfo : mHsotInfoList) {
                    if (hostInfo.supportLocalSave) {
                        return hostInfo.packageName;
                    }
                }
            }
        }
        return null;
    }

    private static ArrayList<Bundle> createVHinGalleryToPlunginList() {
    Bundle VHinGallery = new Bundle();

        try {
            VHinGallery.putString(PluginBundle.Package.KEY, PluginBundle.Package.ALBUM);
            VHinGallery.putString(PluginBundle.ClassName.KEY, PluginBundle.ClassName.VALUE);
            VHinGallery.putInt(PluginBundle.PrimaryText.KEY, getStringResourceId(PluginBundle.PrimaryText.RES_KEY));
            VHinGallery.putInt(PluginBundle.SecondaryText.KEY, getStringResourceId(PluginBundle.SecondaryText.RES_KEY));
            VHinGallery.putInt(PluginBundle.Order.KEY, getStringResourceId(PluginBundle.Order.RES_KEY));
            VHinGallery.putInt(PluginBundle.Background.KEY, getDrawableResourceId(PluginBundle.Background.RES_KEY));
            VHinGallery.putInt(PluginBundle.SupportType.Drm.KEY, getStringResourceId(PluginBundle.SupportType.Drm.RES_KEY));
            VHinGallery.putInt(PluginBundle.SupportType.Cloud.KEY, getStringResourceId(PluginBundle.SupportType.Cloud.RES_KEY));
            VHinGallery.putInt(PluginBundle.SupportType.Zoe.KEY, getStringResourceId(PluginBundle.SupportType.Zoe.RES_KEY));
            mPluginList.add(VHinGallery);
        } catch (Exception e) {
            LOG.log(Level.INFO, "getResourceId Exception ", e);
        }

        return mPluginList;
    }

    private static int getStringResourceId(String resName) throws Exception {
        int resId = mContext.getResources().getIdentifier(resName, "string", mContext.getPackageName());

        if (resId == 0) {
            resId = mContext.getResources().getIdentifier(mContext.getPackageName() + "string/" + resName, null, null);
        }

        if (resId == 0) {
            throw new IllegalArgumentException("no such resource was found : " + resName);
        }

        return resId;
    }

    private static int getDrawableResourceId(String resName) throws Exception {
        int resId = mContext.getResources().getIdentifier(resName, "drawable", mContext.getPackageName());

        if (resId == 0) {
            resId = mContext.getResources().getIdentifier(mContext.getPackageName() + "drawable/" + resName, null, null);
        }

        if (resId == 0) {
            throw new IllegalArgumentException("no such resource was found : " + resName);
        }

        return resId;
    }

    private static void checkGalleryPluginIsExisting(final Context context) {
    	
    	if (s_isGalleryPluginExisting) {
    		return;
    	}
    	
    	try {
			Class<?> mgrClass = Class.forName("com.htc.galleryplugin.VideoHighlightsPlugin");
			// if class exist, find getCustomizationReader() method
			if (null != mgrClass) {
				s_isSupportedInGalleryPlugin = mgrClass.getMethod("isSupportedVideoHighlights", new Class[]{Context.class});
				
				s_isGalleryPluginExisting = true;
				LOG.log(Level.INFO, "[Info][checkGalleryPluginIsExisting] GalleryPlugin is existing");
			}
		} catch (ClassNotFoundException x) {
			LOG.log(Level.INFO, "[Info][checkGalleryPluginIsExisting] VideoHighlightsPlugin class NotFoundException");
		} catch (IllegalArgumentException x) {
			x.printStackTrace();
		} catch (NoSuchMethodException x) {
			LOG.log(Level.INFO, "[Info][checkGalleryPluginIsExisting] VideoHighlightsPlugin class NoSuchMethodException");
			x.printStackTrace();
		}
    }
    
	/*
	 * Function to check if VideoHighlights feature is supported in this device
	 */
	public static boolean isSupported(final Context context) {
		
		checkGalleryPluginIsExisting(context);
		
		if (s_isGalleryPluginExisting) {
			boolean isSupported = false;
			try {
				isSupported = (Boolean) s_isSupportedInGalleryPlugin.invoke(null, context);
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}
			LOG.log(Level.INFO, "[Info] isSupported: " + isSupported);
			return isSupported;
		} else {
			if (bIsInitialized == false) {
				getPlayerInstance(context);
			}
		
			if (mInstance != null) {
				return mInstance.isPlayerSupported();
			}
		
			// return false if there is no implementation
			return false;
		}
	}
	
	
	/*
	 * Function to check if VideoHighlights feature is supported in this device
	 */
	public static boolean isSupported(final Context context, final String packageName) {
		
		if (bIsInitialized == false) {
			getPlayerInstance(context, packageName);
		}
			
		if (mInstance != null) {
			return mInstance.isPlayerSupported();
		}
			
		// return false if there is no implementation
		return false;
	}
	
	/*
	 * Get Video Highlights exported folder names
	 */
	public static String[] getVideoExportFolders() {
		ArrayList<String> paths = new ArrayList<String>();
	
		paths.add(Constants.VIDEO_SAVED_PATH_SENSE55);
		paths.add(Constants.VIDEO_SAVED_PATH_SENSE60);
		
		String[] result = new String[paths.size()];
		result = paths.toArray(result);
		return result;
	}
	
	/*
	 * helper function to look for host apk described with meta in manifest.
	 */
	private static void lookForHost(final Context context) {
		
		final List<ApplicationInfo> appInfo = context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
		
		mHsotInfoList.clear();
		
		if(null == appInfo){
			return;
		}
		
		for(final ApplicationInfo info : appInfo) {
			try {
				if (info != null) {
					Bundle meta = info.metaData;
				
					if (meta != null) {
						final String packageName = meta.getString(Constants.HOST_METADATA_NAME);
						if (packageName != null) {
							LOG.log(Level.INFO, "[Info] host package found: "+ info.packageName + ", version:" + meta.getFloat(Constants.HOST_METADATA_VERSION));
							final PackageInfo packageInfo = 
									context.getPackageManager().getPackageInfo(packageName, 0);
						
							final HostPacakgeInfo host = new HostPacakgeInfo();
							host.packageName = info.packageName;
							host.packagePath = info.sourceDir;
							host.version = meta.getFloat(Constants.HOST_METADATA_VERSION);
							host.supportLocalSave = meta.getBoolean(Constants.HOST_METADATA_SUPPORT_LOCAL_SAVE, false);
						
							if(packageInfo!=null){
								host.packageVersionCode = packageInfo.versionCode;
							}
							mHsotInfoList.add(host);
							
							// we don't check the package name declared in manifest. just showing info
							if (!packageName.equals(info.packageName)) {
								LOG.log(Level.INFO, "[Info] host package declaration: "+ packageName);
							}
						}
					}
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				LOG.log(Level.SEVERE,null,e);
			}
		}
		
		// sorting based on version
		Collections.sort(mHsotInfoList, new Comparator<HostPacakgeInfo>() {
			@Override
			public int compare(HostPacakgeInfo lhs, HostPacakgeInfo rhs) {
				if (lhs.version < rhs.version) return 1;
				if (lhs.version == rhs.version) return 0;
				return -1;
			}
		});
	}
	
	public static synchronized void launchVideoHighlightActivity(Context context, Bundle extraBundle)
	{
		if (context == null) {
			LOG.log(Level.WARNING, "[launchVideoHighlightActivity] context is null");
			return;
		}
		
		final String packageName = getHostPackageNameSupportingLocalSave(context);
		LOG.log(Level.INFO, "launch VideoHighlight activity from plugin, package name: " + packageName);
		
		try {
			if (packageName != null) {
				
				Intent intent = new Intent(ACTION_LAUNCH_VIDEO_HIGHLIGHTS);
				intent.setClassName(packageName, VH_CLASS_NAME);
				
				if (extraBundle != null) {
					intent.putExtras(extraBundle);
				}
				context.startActivity(intent);
			} else {
				// go to google play
				checkChinaAndLaunchGooglePlay(context);
			}
		} catch(Exception ex) {
			LOG.log(Level.WARNING, "[launchVideoHighlightActivity] startActivity exception: " + ex);
		}
	}
	
	private static void checkChinaAndLaunchGooglePlay(Context context){
		
		if (isChina()) {
			LOG.log(Level.INFO, "This is China sku, do nothing");
			return;
		}
		
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + ZERO_PACKAGE_NAME));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(intent);
		} catch (android.content.ActivityNotFoundException anfe) {
			Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + ZERO_PACKAGE_NAME));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(intent);
		}
	}
	
	private enum AccType 
    {
        SKU_ID , REGION
    }
		
	private static boolean isChina()
    {
        int region = checkCustomFlag_Int(AccType.REGION);
        LOG.log(Level.INFO, "[isChina] region: " + region);
        if (region == 3)
        {
            LOG.log(Level.INFO, "[isChina] is True");
            return true;
        }
        LOG.log(Level.INFO, "[isChina] is False");
        return false;
    }
	
	// For new customization mechanism (2013/05/20) begin
    private static HtcWrapCustomizationManager mCustomizationManager = null;
    private static HtcWrapCustomizationReader mCustomizationReader_VIDEO =  null;
    private static HashMap<AccType, Object> mAccCache = null;
    
    private static int checkCustomFlag_Int(AccType key)
    {
        LOG.log(Level.INFO, "[checkCustomFlag_Int] Key is: " + key);

        if (mAccCache == null)
        {
            mAccCache = new HashMap<AccType, Object>();
        }
        else if(mAccCache.containsKey(key))
        {
            Object o = mAccCache.get(key);
            if( o instanceof Integer)
            {
                return (Integer)o;
            }
        }

        int defaultInt = 0;

        if (mCustomizationReader_VIDEO == null)
        {
            if (mCustomizationManager == null) {
                mCustomizationManager = new HtcWrapCustomizationManager();
            }

            if (mCustomizationManager != null) {
                mCustomizationReader_VIDEO = mCustomizationManager.getCustomizationReader("HtcVideoWidget", HtcWrapCustomizationManager.READER_TYPE_XML, true);
            }
        }

        if (mCustomizationReader_VIDEO != null)
        {
            switch(key)
            {
                case SKU_ID:
                    defaultInt = mCustomizationReader_VIDEO.readInteger("sku_id", 0);
                    mAccCache.put(key,defaultInt);
                    break;
                case REGION:
                    defaultInt = mCustomizationReader_VIDEO.readInteger("region", 0);
                    mAccCache.put(key,defaultInt);
                    break;
            }

            LOG.log(Level.INFO, "[checkCustomFlag_Int] Result is: " + defaultInt);
        }
        return defaultInt;
    }
	
	private static class HostPacakgeInfo {
		String packageName;
		String packagePath;
		float version;
		int packageVersionCode;
		boolean supportLocalSave;
	}
}
