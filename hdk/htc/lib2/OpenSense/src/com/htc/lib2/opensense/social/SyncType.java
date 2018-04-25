package com.htc.lib2.opensense.social;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

/**
 * This class represents a social plugin sync type
 * 
 * @hide
 */
public class SyncType {

	private Bundle mBundle = null;

	/**
	 * @hide
	 */
	public SyncType() {
		mBundle = new Bundle();
	}

	public SyncType(Bundle b) {
		if (b != null) {
			mBundle = b;
		} else {
			mBundle = new Bundle();
		}
	}

	public Bundle toBundle() {
		return mBundle;
	}

	public static Bundle putIntoBundleAsArray(Bundle targetBundle, String key,
			SyncType[] syncTypes) {
		if (targetBundle != null) {
			ArrayList<Bundle> list = new ArrayList<Bundle>();
			for (SyncType syncType : syncTypes) {
				list.add(syncType.toBundle());
			}
			targetBundle.putParcelableArray(key, list.toArray(new Bundle[0]));
			return targetBundle;
		}
		return null;
	}

	public static Bundle putIntoBundleAsArray(Bundle targetBundle, String key,
			List<SyncType> syncTypes) {
		if (targetBundle != null) {
			ArrayList<Bundle> list = new ArrayList<Bundle>();
			for (SyncType syncType : syncTypes) {
				list.add(syncType.toBundle());
			}
			targetBundle.putParcelableArray(key, list.toArray(new Bundle[0]));
			return targetBundle;
		}
		return null;
	}

	public static SyncType[] getArrayFromBundle(Bundle targetBundle, String key) {
		if (targetBundle != null) {
			Parcelable[] bundles = targetBundle.getParcelableArray(key);
			if (bundles != null && bundles.length > 0
					&& bundles[0] instanceof Bundle) {
				ArrayList<SyncType> syncTypes = new ArrayList<SyncType>();
				for (Parcelable bundle : bundles) {
					syncTypes.add(new SyncType((Bundle) bundle));
				}
				return syncTypes.toArray(new SyncType[0]);
			}
		}
		return null;
	}

	public static ArrayList<SyncType> getArrayListFromBundle(
			Bundle targetBundle, String key) {
		if (targetBundle != null) {
			ArrayList<Parcelable> bundles = targetBundle
					.getParcelableArrayList(key);
			if (bundles != null && bundles.size() > 0
					&& bundles.get(0) instanceof Bundle) {
				ArrayList<SyncType> syncTypes = new ArrayList<SyncType>();
				for (Parcelable bundle : bundles) {
					syncTypes.add(new SyncType((Bundle) bundle));
				}
				return syncTypes;
			}
		}
		return null;
	}

	public static ArrayList<SyncType> getArrayListFromIntent(Intent intent,
			String key) {
		if (intent != null) {
			ArrayList<Parcelable> bundles = intent
					.getParcelableArrayListExtra(key);
			if (bundles != null && bundles.size() > 0
					&& bundles.get(0) instanceof Bundle) {
				ArrayList<SyncType> syncTypes = new ArrayList<SyncType>();
				for (Parcelable bundle : bundles) {
					syncTypes.add(new SyncType((Bundle) bundle));
				}
				return syncTypes;
			}
		}
		return null;
	}

	/**
	 * id getter
	 * 
	 * @return id
	 * 
	 * @hide
	 */
	public String getId() {
		return mBundle.getString("id");
	}

	/**
	 * id setter
	 * 
	 * @param id
	 * 
	 * @hide
	 */
	public void setId(String id) {
		mBundle.putString("id", id);
	}

	/**
	 * title getter
	 * 
	 * @return title
	 * 
	 * @hide
	 */
	public String getTitle() {
		return mBundle.getString("title");
	}

	/**
	 * title setter
	 * 
	 * @param title
	 * 
	 * @hide
	 */
	public void setTitle(String title) {
		mBundle.putString("title", title);
	}

	/**
	 * sub title getter
	 * 
	 * @return sub title
	 * 
	 * @hide
	 */
	public String getSubTitle() {
		return mBundle.getString("subTitle");
	}

	/**
	 * sub title setter
	 * 
	 * @param subTitle
	 * 
	 * @hide
	 */
	public void setSubTitle(String subTitle) {
		mBundle.putString("subTitle", subTitle);
	}

	/**
	 * package name getter
	 * 
	 * @return package name
	 * 
	 * @hide
	 */
	public String getPackageName() {
		return mBundle.getString("packageName");
	}

	/**
	 * package name setter
	 * 
	 * @param packageName
	 * 
	 * @hide
	 */
	public void setPackageName(String packageName) {
		mBundle.putString("packageName", packageName);
	}

	/**
	 * sub title resource name getter
	 * 
	 * @return sub title resource name
	 * 
	 * @hide
	 */
	public String getSubTitleResName() {
		return mBundle.getString("subTitleResName");
	}

	/**
	 * sub title resource name setter
	 * 
	 * @param subTitleResName
	 * 
	 * @hide
	 */
	public void setSubTitleResName(String subTitleResName) {
		mBundle.putString("subTitleResName", subTitleResName);
	}

	/**
	 * title res name getter
	 * 
	 * @return title resource name
	 * 
	 * @hide
	 */
	public String getTitleResName() {
		return mBundle.getString("titleResName");
	}

	/**
	 * title resource name setter
	 * 
	 * @param titleResName
	 * 
	 * @hide
	 */
	public void setTitleResName(String titleResName) {
		mBundle.putString("titleResName", titleResName);
	}

	/**
	 * color getter
	 * 
	 * @return color
	 * 
	 * @hide
	 */
	public int getColor() {
		return mBundle.getInt("color");
	}

	/**
	 * color setter
	 * 
	 * @param color
	 * 
	 * @hide
	 */
	public void setColor(int color) {
		mBundle.putInt("color", color);
	}

	/**
	 * edition resource name getter
	 * 
	 * @return edition resource name
	 * 
	 * @hide
	 */
	public String getEditionResName() {
		return mBundle.getString("editionResName");
	}

	/**
	 * edition resource name setter
	 * 
	 * @param editionResName
	 * 
	 * @hide
	 */
	public void setEditionResName(String editionResName) {
		mBundle.putString("editionResName", editionResName);
	}

	/**
	 * edition getter
	 * 
	 * @return edition
	 * 
	 * @hide
	 */
	public String getEdition() {
		return mBundle.getString("edition");
	}

	/**
	 * edition setter
	 * 
	 * @param edition
	 * 
	 * @hide
	 */
	public void setEdition(String edition) {
		mBundle.putString("edition", edition);
	}

	/**
	 * category resource name getter
	 * 
	 * @return category resource name
	 * 
	 * @hide
	 */
	public String getCategoryResName() {
		return mBundle.getString("categoryResName");
	}

	/**
	 * category resource name setter
	 * 
	 * @param categoryResName
	 * 
	 * @hide
	 */
	public void setCategoryResName(String categoryResName) {
		mBundle.putString("categoryResName", categoryResName);
	}

	/**
	 * category getter
	 * 
	 * @return category
	 * 
	 * @hide
	 */
	public String getCategory() {
		return mBundle.getString("category");
	}

	/**
	 * category setter
	 * 
	 * @param category
	 * 
	 * @hide
	 */
	public void setCategory(String category) {
		mBundle.putString("category", category);
	}

	/**
	 * iconUrl getter
	 * 
	 * @return iconUrl
	 * 
	 * @hide
	 */
	public String getIconUrl() {
		return mBundle.getString("iconUrl");
	}

	/**
	 * iconUrl setter
	 * 
	 * @param iconUrl
	 * 
	 * @hide
	 */
	public void setIconUrl(String iconUrl) {
		mBundle.putString("iconUrl", iconUrl);
	}

	/**
	 * iconResName getter
	 * 
	 * @return iconResName
	 * 
	 * @hide
	 */
	public String getIconResName() {
		return mBundle.getString("iconResName");
	}

	/**
	 * iconResName setter
	 * 
	 * @param iconResName
	 * 
	 * @hide
	 */
	public void setIconResName(String iconResName) {
		mBundle.putString("iconResName", iconResName);
	}

	/**
	 * categoryIconUrl getter
	 * 
	 * @return categoryIconUrl
	 */
	public String getCategoryIconUrl() {
		return mBundle.getString("categoryIconUrl");
	}

	/**
	 * categoryIconUrl setter
	 * 
	 * @param categoryIconUrl
	 */
	public void setCategoryIconUrl(String categoryIconUrl) {
		mBundle.putString("categoryIconUrl", categoryIconUrl);
	}

	/**
	 * categoryIconResName getter
	 * 
	 * @return categoryIconResName
	 */
	public String getCategoryIconResName() {
		return mBundle.getString("categoryIconResName");
	}

	/**
	 * categoryIconResName setter
	 * 
	 * @param categoryIconResName
	 */
	public void setCategoryIconResName(String categoryIconResName) {
		mBundle.putString("categoryIconResName", categoryIconResName);
	}

	/**
	 * categoryIconColor getter
	 * 
	 * @return categoryIconColor
	 */
	public int getCategoryIconColor() {
		return mBundle.getInt("categoryIconColor");
	}

	/**
	 * categoryIconColor setter
	 * 
	 * @param categoryIconColor
	 */
	public void setCategoryIconColor(int categoryIconColor) {
		mBundle.putInt("categoryIconColor", categoryIconColor);
	}
	
	/**
     * defaultEnabled getter
     * 
     * @return defaultEnabled
     */
    public boolean getDefaultEnabled() {
        return mBundle.getBoolean("defaultEnabled");
    }

    /**
     * defaultEnabled setter
     * 
     * @param defaultEnabled
     */
    public void setDefaultEnabled(boolean defaultEnabled) {
        mBundle.putBoolean("defaultEnabled", defaultEnabled);
    }
    
    /**
     * flagUrl getter
     * 
     * @return flagUrl
     */
    public String getFlagUrl() {
    	return mBundle.getString("flagUrl");
    }

    /**
     * flagUrl setter
     * 
     * @param flagUrl
     */
    public void setFlagUrl(String flagUrl) {
    	mBundle.putString("flagUrl", flagUrl);
    }

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (!(object instanceof SyncType)) {
			return false;
		}
		SyncType syncType = (SyncType) object;
		if (!isEqualString(syncType.getId(), getId())) {
			return false;
		}
		if (!isEqualString(syncType.getTitle(), getTitle())) {
			return false;
		}
		if (!isEqualString(syncType.getSubTitle(), getSubTitle())) {
			return false;
		}
		if (!isEqualString(syncType.getPackageName(), getPackageName())) {
			return false;
		}
		if (!isEqualString(syncType.getSubTitleResName(), getSubTitleResName())) {
			return false;
		}
		if (!isEqualString(syncType.getTitleResName(), getTitleResName())) {
			return false;
		}
		if (syncType.getColor() != getColor()) {
			return false;
		}
		if (!isEqualString(syncType.getEditionResName(), getEditionResName())) {
			return false;
		}
		if (!isEqualString(syncType.getEdition(), getEdition())) {
			return false;
		}
		if (!isEqualString(syncType.getCategoryResName(), getCategoryResName())) {
			return false;
		}
		if (!isEqualString(syncType.getCategory(), getCategory())) {
			return false;
		}
		if (!isEqualString(syncType.getIconUrl(), getIconUrl())) {
			return false;
		}
		if (!isEqualString(syncType.getIconResName(), getIconResName())) {
			return false;
		}
		if (!isEqualString(syncType.getCategoryIconResName(),
				getCategoryIconResName())) {
			return false;
		}
		if (!isEqualString(syncType.getCategoryIconUrl(), getCategoryIconUrl())) {
			return false;
		}
		if (syncType.getCategoryIconColor() != getCategoryIconColor()) {
			return false;
		}
		if (syncType.getDefaultEnabled() != getDefaultEnabled()) {
			return false;
		}
		if (syncType.getFlagUrl() != getFlagUrl()) {
			return false;
		}
		return true;
	}

	private static boolean isEqualString(String a, String b) {
		if (a == null) {
			if (b != null) {
				return false;
			}
		} else {
			if (!a.equals(b)) {
				return false;
			}
		}
		return true;
	}
}
