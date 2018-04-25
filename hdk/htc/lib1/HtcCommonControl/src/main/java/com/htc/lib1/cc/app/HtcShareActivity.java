package com.htc.lib1.cc.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.util.HtcWrapConfigurationUtil;
import com.htc.lib1.cc.util.LogUtil;
import com.htc.lib1.cc.util.WindowUtil;
import com.htc.lib1.cc.widget.HtcShareAdapter;
import com.htc.lib1.cc.widget.HtcShareGridView;
import com.htc.lib1.cc.widget.HtcShareSlidingUpPanelLayout;
import com.htc.lib1.theme.ThemeType;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// done set 3/5 columns in portrait/landscape
// done disable GridView scroll in collapsed (but allow to click)
//      if collapsed,gridView should not intercept drag event,
// done disable SlidingUpPanelLayout scroll until GridView already scrolled to top
// done try not to modify SlidingUpPanelLayout / ViewDragHelper (extract modifications by extend or so)
// done add log for performance measurement
// done merge with lib1
// done set header color
// done change to use Paul's ShareVia adapter
// done fix icon size (in case of incorrect icon size)
// done check flingVelocity, used as minimum fling velocity...
// done add comment/javadoc and hide some internal components
// done support dark mode
// done add parameter for uniquePackage or byActivity
// done extract adapter
// done debug data changed after user chose
// done #1. implement stable id
// done #2. keep adapter between orientation change:
//          can not. because adapter depends on Activity. (leak)
//          but HtcActivityChooserModel may not needed to reload?
//          -> do not reset HtcActivityChooserModel it the same
// done add accessibility support
//      3. speak header and how to expand done
//      4. speak each item done
//      5. speak visible items (show x to y of z) pending
// done default expand in talk back mode, and do not allow tap header to collapse
// done accessibility: speak title instead of application name
// done accessibility: do not focus on drag panel
// done support allowed and blocked
// done move activity declaration from app to library
// done change file name or not -> not necessary
// done check for multiple instance or so
//      pre-condition: the same package, change to another activity while HtcShareActivity is stopped.
//      solution6: add a new parameter "auto-finish" when activity "onStop"
//      solution7: onResume check if (intents, allows, blocks) is the same
// cancel apply theme in java? because the theme includes some settings about window, we'd better set them as early as possible.
// done add permission in lib's AndroidManifest
// done fix multiple intent issue  (working with Aply)
// cancel refactor HtcActivityChooserModel, make it work totally in background
//        extract sorter, historical data, SIE reader...
// cancel discuss text support with Paul
// cancel support async loading. it seems HtcShareVia already did this
// cancel remove mainView?
// done handle font size change
// done handle theme change
// done add more debug log
// done try to fix multi-intents' hassle
// done add static startActivity method
// cancel separate process for theme change
//      scenarios for different launch mode:
//      23. a package uses only one HtcShareActivity <- no problem
//      24. a package uses more then one HtcShareActivity: A. in the same theme <- can use singleton for better memory usage and performance
//                                                         B. in different theme <- should separate process
//      I can set multi-process on HtcShareActivity + different processes on callers to fix "single theme category" issue.
//      but then it will have a problem that the data model fails to update.
//      data model can be fixed by monitoring file changes (i-node), but that is not needed at the moment.
// done add convenient method for users
// done resolve keyboard issue (animation)
// done set status bar to translucent
// done dynamically adjust panel collapsed height
// done apply material design (ripple) (by set target SDK version)
// done test preventing pull up when no item
//      do not disable sliding, in case the number changed due to install/removal of applications.
// pending add real test
// pending complete API Demo: create 2 demos, each can share:
//      8. one photo done
//      11. one arbitrary text (user input) done
//      13. e-mail with attachment(s) done
//      17. test allow and block done
//      18. multiple different content/data/intent done
//      9. many photos (3+)
//      10. one URL
//      12. e-mail: email, subject, text message
//      14. address of a place
//      15. video?
//      16. audio?
// done wait for UI guideline and review with Potter
//      25. grid item text style -- done
//      26. header text style -- done
//      27. header height -- done
//      28. content background color -- done
//      29. item height -- done
//      30. item width -- done (not defined in UI guideline)
//      31. item vertical/horizontal gap -- done
//      32. collapsed panel height -- done
//      34. item text line number -- done (1 on extraLarge/huge, 2 otherwise)
//      35. item text ellipse -- done

/**
 * HtcShareActivity
 *
 * new design for Sense70 referring to the Bottom Sheet in Material Design
 *
 * to use this, start activity with following "extra" parameters:
 * 1. EXTRA_INTENT_LIST, intent array, one or multiple.
 * 2. EXTRA_TITLE, optional, default = Share
 * 3. EXTRA_THEME_CATEGORY, optional, default = baseline. be warned, each application can set only one category.
 *                          set to FLAG_THEME_DARK for special dark theme.
 * 4. EXTRA_ALLOWED_PACKAGE_LIST, optional, package name array.
 * 5. EXTRA_BLOCKED_PACKAGE_LIST, optional, package name array.
 *
 * result intent can be used directly to launch the activity user chose.
 * if multiple intent is provided, the first one is returned, with target component set.
 * (the first one is treated as the major one.)
 *
 */
public class HtcShareActivity extends Activity {

    public static final String EXTRA_INTENT_LIST = "EXTRA_INTENT_LIST"; // the intents, in Parcelable array, mandatory
    public static final String EXTRA_ALLOWED_PACKAGE_LIST = "EXTRA_ALLOWED_PACKAGE_LIST"; // package names that is allowed, in String array, optional, default=null
    public static final String EXTRA_BLOCKED_PACKAGE_LIST = "EXTRA_BLOCKED_PACKAGE_LIST"; // package names that is blocked, in String array, optional, default=null
    public static final String EXTRA_QUERY_BY_PACKAGE = "EXTRA_QUERY_BY_PACKAGE"; // unique by package, boolean, optional, default=false
    public static final String EXTRA_TITLE = Intent.EXTRA_TITLE; // user may set title, in String, default=Share
    public static final String EXTRA_THEME_CATEGORY = "EXTRA_THEME_CATEGORY"; // user may change the theme category, int, optional, default=baseline
    public static final int FLAG_THEME_DARK = -31089; // set EXTRA_THEME_CATEGORY to this for dark theme

    private static final String TAG = "HtcShareActivity";
    private static final boolean DEBUG = true;

    // 4 essential parameters
    private final List<Intent> mIntents = new ArrayList<Intent>();
    private boolean mQueryByPackage;
    private Set<String> mBlocked = null;
    private Set<String> mAllowed = null;

    // internal use
    protected int mChosenOne = -1;
    protected HtcShareAdapter mAdapter;
    private HtcShareSlidingUpPanelLayout mSlidingLayout;
    private float mFontScale;
    private boolean mThemeChanged = false;

    /**
     * The max height of HtcShareActivity when it is collapsed.
     */
    private int mCollapsedHeight = 0;

    private final HtcCommonUtil.ThemeChangeObserver mThemeChangeObserver = new HtcCommonUtil.ThemeChangeObserver() {
        @Override
        public void onThemeChange(int type) {
            if (ThemeType.HTC_THEME_CC == type || ThemeType.HTC_THEME_FULL == type) {
                if (DEBUG) Log.d(TAG, "onThemeChange: type=" + type);
                mThemeChanged = true;
            }
        }
    };

    /**
     * @hide
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        applyHtcFontScale();

        // init theme
        Intent intent = getIntent();
        int mTheme = intent.getIntExtra(EXTRA_THEME_CATEGORY, HtcCommonUtil.BASELINE);
        int style;
        switch (mTheme) {
            case HtcCommonUtil.CATEGORYONE:
                style = R.style.HtcDeviceDefault_CategoryOne;
                break;
            case HtcCommonUtil.CATEGORYTWO:
                style = R.style.HtcDeviceDefault_CategoryTwo;
                break;
            case HtcCommonUtil.CATEGORYTHREE:
                style = R.style.HtcDeviceDefault_CategoryThree;
                break;
            case HtcCommonUtil.BASELINE:
            case FLAG_THEME_DARK:
                style = R.style.HtcDeviceDefault;
                break;
            default:
                style = R.style.HtcDeviceDefault;
                if (DEBUG) Log.w(TAG, "onCreate: unknown category. mTheme=" + mTheme);
                break;
        }
        getTheme().applyStyle(style, false);
        HtcCommonUtil.initTheme(this, FLAG_THEME_DARK == mTheme ? HtcCommonUtil.BASELINE : mTheme);
        HtcCommonUtil.registerThemeChangeObserver(this, ThemeType.HTC_THEME_CC, mThemeChangeObserver);
        HtcCommonUtil.registerThemeChangeObserver(this, ThemeType.HTC_THEME_FULL, mThemeChangeObserver);
        if (DEBUG) Log.d(TAG, "onCreate: mTheme=" + mTheme);

        // set content view
        setContentView(FLAG_THEME_DARK == mTheme ? R.layout.activity_htcshareactivity_dark : R.layout.activity_htcshareactivity);
        mSlidingLayout = (HtcShareSlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        // setup main view
        View main = mSlidingLayout.findViewById(R.id.main);
        main.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // touch main view to finish
                finish();
            }
        });

        // setup header
        TextView title = (TextView) mSlidingLayout.findViewById(R.id.title);
        int color = HtcCommonUtil.getCommonThemeColor(this, R.styleable.ThemeColor_multiply_color);
        if (FLAG_THEME_DARK == mTheme) color = getResources().getColor(R.color.dark_ap_background_color);
        title.setBackgroundColor(color);
        if (intent.hasExtra(EXTRA_TITLE)) {
            title.setText(intent.getStringExtra(EXTRA_TITLE));
            setTitle(intent.getStringExtra(EXTRA_TITLE)); // so that accessibility will speak the custom title
        } else {
            setTitle(R.string.common_string_share_title);
        }
        if (DEBUG) Log.d(TAG, "onCreate: color=" + Integer.toHexString(color));

        // setup adapter
        mQueryByPackage = intent.getBooleanExtra(EXTRA_QUERY_BY_PACKAGE, false);

        Parcelable[] tmp = intent.getParcelableArrayExtra(EXTRA_INTENT_LIST);
        for (Parcelable p : tmp) {
            mIntents.add((Intent) p);
        }

        String[] blockedArray = intent.getStringArrayExtra(EXTRA_BLOCKED_PACKAGE_LIST);
        if (null != blockedArray && 0 != blockedArray.length) {
            mBlocked = new HashSet<String>(blockedArray.length);
            Collections.addAll(mBlocked, blockedArray);
        }
        String[] allowedArray = intent.getStringArrayExtra(EXTRA_ALLOWED_PACKAGE_LIST);
        if (null != allowedArray && 0 != allowedArray.length) {
            mAllowed = new HashSet<String>(allowedArray.length);
            Collections.addAll(mAllowed, allowedArray);
        }
        mAdapter = new HtcShareAdapter(mIntents, mAllowed, mBlocked, this, FLAG_THEME_DARK == mTheme, mQueryByPackage);

        // setup gridView
        HtcShareGridView gridView = (HtcShareGridView) mSlidingLayout.findViewById(R.id.gridview);
        gridView.setSlidingUpPanelLayout(mSlidingLayout);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mChosenOne = position;
                ResolveInfo rInfo = mAdapter.getItem(position);
                if (DEBUG) Log.d(TAG, "onItemClick: position=" + position + " activity=" + rInfo.activityInfo.packageName + "/" + rInfo.activityInfo.name);

                List<Intent> intents = mAdapter.getMatchedIntents(rInfo);
                Intent data;
                if (1 == intents.size()) {
                    data = intents.get(0);
                } else {
                    // we have to return all matched intents
                    data = new Intent(); // preventing infinite loop
                    data.putExtra(EXTRA_INTENT_LIST, intents.toArray(new Intent[intents.size()]));
                }
                data.setClassName(rInfo.activityInfo.packageName, rInfo.activityInfo.name);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        mSlidingLayout.setGridView(gridView);

        mCollapsedHeight = getCollapsedHeightOfHtcShareActivity();
        mSlidingLayout.setPanelHeight(mCollapsedHeight);
    }

    /**
     * Get the height of HtcShareActivity when it is collapsed.
     * mCollapsedHeight = headerHeight_htcShareActivity + number(PORTRAIT is 2,LANDSCAPE is 1) * height_shareGridItem + paddingTop_shareGridItem + app_icon_size / 2.
     *
     * @return The height of HtcShareActivity when it is collapsed
     */
    private int getCollapsedHeightOfHtcShareActivity() {
        final Resources res = getResources();
        if (null == res) {
            LogUtil.logE(TAG, "getResources() return null");
            return 0;
        }

        final Configuration config = res.getConfiguration();
        if (null == config) {
            LogUtil.logE(TAG, "res.getConfiguration() return null");
        }

        int height = res.getDimensionPixelSize(R.dimen.headerHeight_htcShareActivity) + res.getDimensionPixelSize(R.dimen.paddingTop_shareGridItem)
                + res.getDimensionPixelSize(android.R.dimen.app_icon_size) / 2;
        if (null != config && !WindowUtil.isSuitableForLandscape(res)) {
            height += 2 * res.getDimensionPixelSize(R.dimen.height_shareGridItem);
        } else {
            height += res.getDimensionPixelSize(R.dimen.height_shareGridItem);
        }
        return height;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        recreate();
    }

    /**
     * @hide
     */
    @Override
    protected void onResume() {
        super.onResume();
        checkHtcFontScaleChanged();
        if (mThemeChanged) {
            mThemeChanged = false;
            getWindow().getDecorView().postOnAnimation(new Runnable() {
                @Override
                public void run() {
                    if (DEBUG) Log.d(TAG, "onResume: theme changed. recreating...");
                    recreate();
                }
            });
        }

        if (mAdapter.dataReady()) {
            int count = mAdapter.getCount();
            if (0 != count) {
                // do NOT adjust height if no data
                // in case data is not ready (somehow)
                // ps. 0 is not expected to happen

                Resources res = getResources();
                int col = res.getInteger(R.integer.columnNum_htcShareActivity);
                int row = (count + col - 1) / col;
                float newHeight = res.getDimensionPixelSize(R.dimen.headerHeight_htcShareActivity) +
                        row * res.getDimensionPixelSize(R.dimen.height_shareGridItem);
                if (newHeight < mCollapsedHeight) {
                    mSlidingLayout.setPanelHeight((int) newHeight);
//                    mSlidingLayout.setSlidingEnabled(false);
                }
            }
        }
    }

//    private int getDefaultHeight() {
//        int mSize = getResources().getDimensionPixelSize(android.R.dimen.app_icon_size);
//        Drawable d = new ColorDrawable(0);
//        d.setBounds(0, 0, mSize, mSize);
//
//        View item = getLayoutInflater().inflate(R.layout.adapteritem_resolveinfo, null);
//        ((HtcShareTextView) item).setText("test only");
//        ((HtcShareTextView) item).setCompoundDrawables(null, d, null, null);
//        item.measure(View.MeasureSpec.makeMeasureSpec(getResources().getDimensionPixelSize(R.dimen.width_shareGridItem), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        return item.getMeasuredHeight();
//    }

    /**
     * @hide
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        // be careful if data changed during onPause and onResume (not go through onRestart)
        if (DEBUG) Log.d(TAG, "onRestart: reset essential parameters");
        mAdapter.setIntents(mIntents, mAllowed, mBlocked, mQueryByPackage);
    }

    /**
     * @hide
     */
    @Override
    public void finish() {
        super.finish();
        if (SlidingUpPanelLayout.PanelState.EXPANDED == mSlidingLayout.getPanelState()) {
            // use another out animation
            overridePendingTransition(0, R.anim.slide_out_down_from_expand);
        } else {
            overridePendingTransition(0, R.anim.slide_out_down);
        }
    }

    /**
     * @hide
     */
    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (-1 != mChosenOne) {
            mAdapter.chooseItem(mChosenOne);
            if (DEBUG) Log.d(TAG, "onDetachedFromWindow: mChosenOne=" + mChosenOne);
        }
    }

    /**
     * @hide
     */
    @Override
    protected void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy: unregisterThemeChangeObserver");
        HtcCommonUtil.unregisterThemeChangeObserver(ThemeType.HTC_THEME_CC, mThemeChangeObserver);
        HtcCommonUtil.unregisterThemeChangeObserver(ThemeType.HTC_THEME_FULL, mThemeChangeObserver);
        super.onDestroy();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Htc font scale
     * apply htc huge font size change
     */
    private void applyHtcFontScale() {
        boolean applied = HtcWrapConfigurationUtil.applyHtcFontscale(this);
        mFontScale = getResources().getConfiguration().fontScale;
        if (DEBUG) Log.d(TAG, "applyHtcFontScale: applied=" + applied + " mFontScale=" + mFontScale);
    }

    /**
     * Htc font scale
     * check and recreate activity if font size changed to huge
     */
    private void checkHtcFontScaleChanged() {
        if (HtcWrapConfigurationUtil.checkHtcFontscaleChanged(this, mFontScale)) {
            getWindow().getDecorView().postOnAnimation(new Runnable() {
                @Override
                public void run() {
                    if (DEBUG) Log.d(TAG, "checkHtcFontScaleChanged: recreating...");
                    recreate();
                }
            });
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * convenient method for users to startActivityForResult with correct entering animation
     *
     * @param intent intent to launch HtcShareActivity
     * @param requestCode requestCode to use in onActivityResult
     * @param activity the activity
     */
    public static void startActivityForResult(Intent intent, int requestCode, Activity activity) {
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.slide_in_up, 0);
    }

    /**
     * convenient method for users to startActivityForResult with only one intent to resolve.
     *
     * @param intent the intent to be resolved. (not HtcShareActivity)
     * @param theme the theme category
     * @param title null to use default title
     * @param requestCode requestCode to use in onActivityResult
     * @param activity the activity
     */
    public static void startActivityForResult(Intent intent, int theme, String title, int requestCode, Activity activity) {
        Intent intent2StartHtcShareActivity = new Intent(activity, HtcShareActivity.class);
        intent2StartHtcShareActivity.putExtra(HtcShareActivity.EXTRA_INTENT_LIST, new Intent[]{intent});
        intent2StartHtcShareActivity.putExtra(HtcShareActivity.EXTRA_THEME_CATEGORY, theme);
        if (!TextUtils.isEmpty(title)) {
            intent2StartHtcShareActivity.putExtra(HtcShareActivity.EXTRA_TITLE, title);
        }
        HtcShareActivity.startActivityForResult(intent2StartHtcShareActivity, requestCode, activity);
    }
}
