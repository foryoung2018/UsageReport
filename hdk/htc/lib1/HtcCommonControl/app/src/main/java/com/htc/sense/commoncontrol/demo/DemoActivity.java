/*
 * Copyright (C) 2009 HTC Inc.
 */

package com.htc.sense.commoncontrol.demo;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.util.HtcCommonUtil.ObtainThemeListener;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.ActionBarItemView;
import com.htc.lib1.cc.widget.ListPopupWindow;
import com.htc.sense.commoncontrol.demo.actionbar.ActionMenuAdapter;

public class DemoActivity extends CommonDemoActivityBase2 {

    private static final String SAMPLE_CATEGORY = "com.htc.sense.intent.category.SAMPLE_CODE";
    private static final String DYNAMIC_THEME_KEY = "dynamic_theme_key";

    private SparseIntArray mThemeColorArray = new SparseIntArray(R.styleable.ThemeColor.length);

    private boolean mEnableDynamicTheme = false;

    static final String LABEL_PATH = "com.example.htc.apis.Path";

    private boolean mLaunchList = true;

    private ObtainThemeListener mObtainThemeListener = new ObtainThemeListener() {
        @Override
        public int onObtainThemeColor(int index, int themeColor) {
            if (mEnableDynamicTheme) return themeColor;

            int foundColor = mThemeColorArray.get(index);
            if (foundColor != 0) return foundColor;

            TypedValue typedValue = new TypedValue();
            boolean result = getTheme().resolveAttribute(R.styleable.ThemeColor[index], typedValue, true);
            if (!result) return themeColor;

            int retColor = getResources().getColor(typedValue.resourceId);
            mThemeColorArray.put(index, retColor);
            return retColor;
        }
    };

    @Override
    protected Intent getActivityIntent() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(SAMPLE_CATEGORY);
        intent.putExtra(LABEL_PATH, getIntent().getStringExtra(LABEL_PATH));
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        String prefix = getIntent().getStringExtra(LABEL_PATH);
        if (prefix != null) {
            mLaunchList = false;
            setTitle(prefix);
        } else {
            mLaunchList = true;
            PackageInfo packageInfo = null;
            try {
                packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            if (packageInfo != null) {
                setTitle(packageInfo.versionName + " CC Api Demo");
            }
        }

        if (savedInstanceState != null && mLaunchList) {
            mEnableDynamicTheme = savedInstanceState.getBoolean(DYNAMIC_THEME_KEY);
        }

        super.onCreate(savedInstanceState);

        initActionBar();

        getWindow().setBackgroundDrawableResource(com.htc.lib1.cc.R.drawable.common_app_bkg);
    }
    private void initActionBar() {
        if (!mLaunchList) {
            return;
        }
        ActionBarExt actionBarExt = mActionBarExt;

        ActionBarContainer actionBarContainer = actionBarExt.getCustomContainer();

        final ActionBarItemView actionBarItemViewCategory = new ActionBarItemView(DemoActivity.this);
        actionBarItemViewCategory.setIcon(R.drawable.actionbar_icon_btn_macro_dark);

        actionBarItemViewCategory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showCategoryPopup(view);
            }
        });

        actionBarContainer.addEndView(actionBarItemViewCategory);
        actionBarItemViewCategory.setVisibility(mEnableDynamicTheme ? View.VISIBLE : View.GONE);

        final ActionBarItemView actionBarItemView = new ActionBarItemView(DemoActivity.this);
        actionBarItemView.setIcon(R.drawable.skin);

        actionBarItemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showSkinPopup(view);
            }
        });

        actionBarContainer.addEndView(actionBarItemView);

        final ActionBarItemView actionBarItemViewFont = new ActionBarItemView(DemoActivity.this);
        actionBarItemViewFont.setIcon(R.drawable.font);

        actionBarItemViewFont.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showFontPopup(view);
            }
        });

        actionBarContainer.addEndView(actionBarItemViewFont);
        actionBarItemViewFont.setVisibility(View.GONE);
    }

    private void applyFontSizeChange(Context context, int choise) {
        int fontsize = choise + 2;
        float fontscale = 1.0f;
        switch (choise) {
            case 0:
                fontscale = 0.85F;
                break;
            case 1:
                fontscale = 1.00F;
                break;
            case 2:
                fontscale = 1.15F;
                break;
            case 3:
                fontscale = 1.30F;
                break;
            case 4:
                fontscale = 1.30F;
                break;
            default:
                fontscale = 1.00F;
                break;
        }

        try {
            Class<?> activityManagerNative = Class.forName("android.app.ActivityManagerNative");
            Object am = activityManagerNative.getMethod("getDefault").invoke(activityManagerNative);
            Object conf = am.getClass().getMethod("getConfiguration").invoke(am);
            conf.getClass().getField("fontsize").setInt(conf, fontsize);
            conf.getClass().getField("fontScale").setFloat(conf, fontscale);
            am.getClass().getMethod("updateConfiguration", android.content.res.Configuration.class).invoke(am, conf);
            am.getClass().getMethod("updatePersistentConfiguration", android.content.res.Configuration.class).invoke(am, conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ListPopupWindow mSkinPopupWindow;
    private ListPopupWindow mFontPopupWindow;
    private ListPopupWindow mCategoryPopupWindow;
    private ArrayList<String> mSkinList = new ArrayList<String>();
    private ArrayList<String> mFontList = new ArrayList<String>();
    private String[] mCategoryList = new String[] {
            "HtcCommonUtil.BASELINE", "HtcCommonUtil.CATEGORYONE", "HtcCommonUtil.CATEGORYTWO", "HtcCommonUtil.CATEGORYTHREE"
    };

    private void initSkinList() {
        mSkinList.clear();
        mSkinList.add(getResources().getResourceEntryName(R.style.HtcDeviceDefault));
        mSkinList.add(getResources().getResourceEntryName(R.style.HtcDeviceDefault_CategoryOne));
        mSkinList.add(getResources().getResourceEntryName(R.style.HtcDeviceDefault_CategoryTwo));
        mSkinList.add(getResources().getResourceEntryName(R.style.HtcDeviceDefault_CategoryThree));
        mSkinList.add(getResources().getResourceEntryName(R.style.HtcDeviceDefault_CategoryFour));
    }

    private void initFontList() {
        mFontList.clear();
        mFontList.add("Font size : Small");
        mFontList.add("Font size : Medium");
        mFontList.add("Font size : Large");
        mFontList.add("Font size : Extra large");
        mFontList.add("Font size : Huge");
    }

    private void showSkinPopup(View anchorView) {
        if (mSkinPopupWindow != null && mSkinPopupWindow.isShowing()) return;

        if (mSkinPopupWindow == null) {
            mSkinPopupWindow = new ListPopupWindow(DemoActivity.this, android.R.attr.popupMenuStyle);
            initSkinList();
            mSkinPopupWindow.setAdapter(new ActionMenuAdapter(this, mSkinList.toArray(new String[mSkinList.size()]), false));
            mSkinPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String resName = mSkinList.get(position);
                    int themeResId = getResources().getIdentifier(resName, "style", getPackageName());
                    mSkinPopupWindow.dismiss();
                    if (themeResId != mThemeResId) {
                        mThemeResId = themeResId;
                        recreate();
                    }
                }
            });
        }
        mSkinPopupWindow.setAnchorView(anchorView);
        mSkinPopupWindow.show();
    }

    private void showFontPopup(View anchorView) {
        if (mFontPopupWindow != null && mFontPopupWindow.isShowing()) return;

        if (mFontPopupWindow == null) {
            mFontPopupWindow = new ListPopupWindow(DemoActivity.this, android.R.attr.popupMenuStyle);
            initFontList();
            mFontPopupWindow.setAdapter(new ActionMenuAdapter(this, mFontList.toArray(new String[mFontList.size()]), false));
            mFontPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    applyFontSizeChange(DemoActivity.this, position);
                    mFontPopupWindow.dismiss();
                }
            });
        }
        mFontPopupWindow.setAnchorView(anchorView);
        mFontPopupWindow.show();
    }

    private void showCategoryPopup(View anchorView) {
        if (mCategoryPopupWindow != null && mCategoryPopupWindow.isShowing()) return;

        if (mCategoryPopupWindow == null) {
            mCategoryPopupWindow = new ListPopupWindow(DemoActivity.this, android.R.attr.popupMenuStyle);
            mCategoryPopupWindow.setAdapter(new ActionMenuAdapter(this, mCategoryList, false));
            mCategoryPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mCategoryPopupWindow.dismiss();
                    if (mCategoryId != position) {
                        mCategoryId = position;
                        recreate();
                    }
                }
            });
        }
        mCategoryPopupWindow.setAnchorView(anchorView);
        mCategoryPopupWindow.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        try {
            menu.setHeaderTitle("TestContextMenu");
        } catch (Exception e) {
            return;
        }
        menu.add(0, 1, 0, "AA");
        menu.add(0, 2, 0, "BB");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (mLaunchList) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
        }
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mLaunchList) {
            MenuItem dynamicItem = menu.findItem(R.id.dynamic_id);
            dynamicItem.setChecked(mEnableDynamicTheme);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.dynamic_id:
                item.setChecked(mEnableDynamicTheme = !mEnableDynamicTheme);
                recreate();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mLaunchList) outState.putBoolean(DYNAMIC_THEME_KEY, mEnableDynamicTheme);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLaunchList) HtcCommonUtil.setObtianThemeListener(null);
    }

    @Override
    protected boolean shouldEnableBackup() {
        return !mLaunchList;
    }

    @Override
    protected void applyCustomWindowFeature() {
        if (mLaunchList) {
            mThemeColorArray.clear();
            HtcCommonUtil.setObtianThemeListener(mObtainThemeListener);
        }
    }
}
