package com.htc.lib1.locationservicessettingmanager.base;

import com.htc.lib1.locationservicessettingmanager.R;
import com.htc.lib1.locationservicessettingmanager.R.drawable;
import com.htc.lib1.locationservicessettingmanager.util.Utils;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.HtcOverlapLayout;
import com.htc.lib1.theme.ThemeType;
import com.htc.lib2.configuration.HtcWrapConfiguration;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public abstract class BaseActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Before calling setContenView() in onCreate()
		setHtcFontScale();

		//#Theme
		HtcCommonUtil.initTheme(this,Utils.THEME_CATEGORY);
		HtcCommonUtil.registerThemeChangeObserver(this,ThemeType.HTC_THEME_FULL, mThemeChangeObserver);
		HtcCommonUtil.registerThemeChangeObserver(this,ThemeType.HTC_THEME_CC, mThemeChangeObserver);
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();

		//#Theme
		HtcCommonUtil.unregisterThemeChangeObserver(ThemeType.HTC_THEME_FULL, mThemeChangeObserver);
		HtcCommonUtil.unregisterThemeChangeObserver(ThemeType.HTC_THEME_CC, mThemeChangeObserver);
	};
	
	@Override
	protected void onResume() {
		super.onResume();

		// check htc font style if (Others -> Huge or Huge to Others) then
		// restart activity
		// check Font size change and theme id
		checkReCreate();
	}
	
	@Override
	public void onConfigurationChanged(
			android.content.res.Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// If your application handles some configurations by itself, Google
		// will modify fontScale to the original value when
		// onConfigurationChanged() is called
		HtcWrapConfiguration.applyHtcFontscale(this); // Htc font scale

		//#Theme 
		HtcCommonUtil.updateCommonResConfiguration(this);
		
		//Status bar
		switchHeaderBarBkg(newConfig.orientation);
	};
	
	public float mHtcFontscale = 0.0f;
	/**
	 * Set HTC FontScale
	 */
	public void setHtcFontScale() {
		HtcWrapConfiguration.applyHtcFontscale(this); // Htc font scale
		mHtcFontscale = getResources().getConfiguration().fontScale; // Htc font scale
	}	
	
	// get theme and font value.
	public boolean isHtcFontSizeChanged(Context context, float fontScale) {
		if (HtcWrapConfiguration.checkHtcFontscaleChanged(context, fontScale)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Check reCreate for onResume
	 */
	private void checkReCreate() {
		//#Theme
		if(mIsThemeChanged) {
			getWindow().getDecorView().postOnAnimation(new Runnable() {
				@Override
				public void run() {
					HtcCommonUtil.notifyChange(BaseActivity.this, HtcCommonUtil.TYPE_THEME);
					recreate();
				}
			});
			mIsThemeChanged = false;
		}

		//Fontsize change
		if (isHtcFontSizeChanged(this, mHtcFontscale)) {
			getWindow().getDecorView().postOnAnimation(new Runnable() {
				@Override
				public void run() {
					recreate();
				}
			});
		}
	}	
	
	public boolean mIsThemeChangedForChild = false; // For those activities need to do something when mIsThemeChagned is false
	private boolean mIsThemeChanged = false;
	HtcCommonUtil.ThemeChangeObserver mThemeChangeObserver = new HtcCommonUtil.ThemeChangeObserver() {
		public void onThemeChange(int type) {
			if(type == ThemeType.HTC_THEME_FULL || type == ThemeType.HTC_THEME_CC) {
				mIsThemeChanged = true;
				mIsThemeChangedForChild = true;
			}
		}
	};
	
	private final int STATUS_BAR_BKG_ID = 1; // U16 josh_kang comments: Don't set ID to 0
	private LayerDrawable mWindowBkg = null;
	private ColorDrawable mColorDrawable = null;
	private Drawable mTextureDrawable = null;
	private Drawable mActionBarDrawable = null;
	
	public void setHeaderBarColor() {	
		Window window = this.getWindow();

		int categoryColor = Utils.getMultiplyColor(this);

		mColorDrawable = new ColorDrawable(categoryColor);	
		mTextureDrawable = Utils.getStatusBarTexture(this);
		mActionBarDrawable = Utils.getActionBarTexture(this);

		// Create a LayerDrawable as the window background
		// The first layer is the status bar background and the second layer is the app background.
		Drawable[] drawables = { mColorDrawable, getResources().getDrawable(R.drawable.common_app_bkg)};

		mWindowBkg = new LayerDrawable(drawables);
		mWindowBkg.setLayerInset( 1, 0, Utils.getStatusBarHeight(this), 0, 0); //index, left, top, right, bottom
		window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		window.setBackgroundDrawable(mWindowBkg);

		mWindowBkg.setId(0, STATUS_BAR_BKG_ID);

		switchHeaderBarBkg(getResources().getConfiguration().orientation);

		//-------------------------------------------------------------------------------------------

		ViewGroup vg = (ViewGroup) findViewById(android.R.id.content);
		int childcount = vg.getChildCount();
		final View root = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);

		//enable translucent mode for status bar (status bar will be translucent.)
		window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

		if(root!=null) {
			if(root instanceof HtcOverlapLayout) {
				HtcOverlapLayout overlapLayout = (HtcOverlapLayout)root;
				overlapLayout.setInsetActionbarTop(true);
				overlapLayout.setInsetStatusBar(true);
			} else {
				root.setFitsSystemWindows(true);
			}
		}
	}

	private void switchHeaderBarBkg(int orientation) {
		if (mWindowBkg == null) {
			return;
		} 

		mWindowBkg.setLayerInset( 0, 0, 0, 0, 0 );		
		if(orientation == Configuration.ORIENTATION_PORTRAIT &&
				mTextureDrawable != null && mActionBarDrawable != null) {
			// Status bar
			mWindowBkg.setDrawableByLayerId(STATUS_BAR_BKG_ID, mTextureDrawable);
			// Action bar
			setActionBarTextureDrawable(mActionBarDrawable);
		} else {
			if (mColorDrawable != null) {
				// Status bar
				mWindowBkg.setDrawableByLayerId(STATUS_BAR_BKG_ID, mColorDrawable);
				// Action bar
				setActionBarTextureDrawable(mColorDrawable);
			}
		}
		
	}

	/**
	 * Let child class implement to set ActinoBar's background
	 * @param d
	 */
	abstract protected void setActionBarTextureDrawable(Drawable d);
}
