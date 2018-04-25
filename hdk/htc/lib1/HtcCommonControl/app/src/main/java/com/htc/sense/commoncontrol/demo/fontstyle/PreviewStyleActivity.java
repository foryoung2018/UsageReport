package com.htc.sense.commoncontrol.demo.fontstyle;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.ActionBarItemView;
import com.htc.lib1.cc.widget.ActionBarText;
import com.htc.lib1.cc.widget.HtcEditText;
import com.htc.lib1.cc.widget.HtcIconButton;
import com.htc.lib1.cc.widget.HtcRimButton;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class PreviewStyleActivity extends CommonDemoActivityBase {
    TextView mText, mInfoPanelSize, mInfoPanelColor, mInfoPanelStyle,
            mInfoPanelTypeface;
    HtcEditText mHtcEdit;
    Button mBtn;
    HtcRimButton mRimBtn, mRimBtn1;
    HtcIconButton mIconBtn, mIconBtn1;
    Switch mSwitch;
    CheckBox mCheck;
    RadioButton mRadio;
    Spinner mSpinnerFontStyle;
    Boolean lightMode;
    int fontStyleListResId[];
    private static boolean DEBUG = true;
    ArrayList<String> mfontStyleName = null;
    ArrayList<Integer> mfontStyleId = null;
    ArrayAdapter<String> fontStyleArrayAdapter = null,
            fontSizeLevelAdapter = null;
    private static final Object mSync = new Object();
    private Resources mPackageRes = null;
    private Context mContext = null, mPackageContext = null;
    private String RESOURCE_PACKAGE = null;
    private String RESOURCE_PACKAGE_TITLE = null;
    private static String DEFAULT_RESOURCE_PACKAGE = "com.htc.resources";
    private final String TAG = "Bill PreviewFontStyle";
    private final int rootId = 1;
    public static final String AUTHORITY = "com.example.previewfontstyle";
    public static final boolean USE_HTCSTYLE = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent currentIntent = this.getIntent();
        int spinnerPosition = currentIntent.getIntExtra("SpinnerPosition", 0);
        RESOURCE_PACKAGE = currentIntent.getStringExtra("PackageName");
        setContentView(R.layout.preview_style_main_activity);
        mContext = this;
        initPackageResource(RESOURCE_PACKAGE);
        initFontStyleData(mPackageContext, RESOURCE_PACKAGE);
        initSpinner(spinnerPosition);
        initPreviewComponent();
        // deinitResourceComponent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.light_dark_mode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = this.getIntent();
        int selectedPosition = mSpinnerFontStyle.getSelectedItemPosition();
        int targetThemeId;
        switch (item.getItemId()) {
        case R.id.menu_Dark:
            targetThemeId = android.R.style.Theme_DeviceDefault;
            Log.e("Uitls", "Target Theme ResId:" + targetThemeId);
            intent.putExtra("ThemeId", targetThemeId);
            intent.putExtra("SpinnerPosition", selectedPosition);
            Utils.changeToTheme(this, intent);
            return true;
        case R.id.menu_Light:
            targetThemeId = android.R.style.Theme_DeviceDefault_Light;
            Log.e("Uitls", "Target Theme ResId:" + targetThemeId);
            intent.putExtra("ThemeId", targetThemeId);
            intent.putExtra("SpinnerPosition", selectedPosition);
            Utils.changeToTheme(this, intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void deinitResourceComponent() {
        mfontStyleName = null;
        mfontStyleId = null;
        mPackageRes = null;
        mContext = null;
        mPackageContext = null;
    }

    private int[] getStyleResIdFromPackage() {
        int[] styelResIdArray = { 0 };
        return styelResIdArray;
    }

    private HashMap<String, ArrayList> obtainStyleNamesByType(
            Context mPackageContext, int styleTypeId, Resources targetRes) {
        if (null == mPackageContext || null == targetRes || 0 == styleTypeId){
            return null;
        }

        int MaxCount = 1000;
        int tempMask = 0;
        HashMap<String, ArrayList> map = new HashMap<String, ArrayList>();
        mfontStyleName = new ArrayList<String>();
        mfontStyleId = new ArrayList<Integer>();
        String tempString;
        for (int i = 0; i < MaxCount; i++) {
            try {
                tempMask = styleTypeId | i;
                tempString = targetRes.getResourceEntryName(tempMask);
                if (Utils.isFontStyle(mPackageContext, tempMask)) {
                    mfontStyleName.add(tempString);
                    mfontStyleId.add(tempMask);
                }
            } catch (NotFoundException e) {
                System.out.println("Style ResourceID Not found"
                        + Integer.toString(tempMask));
                break;
            }
        }
        map.put("fontStyleNameArray", mfontStyleName);
        map.put("fontStyleIdArray", mfontStyleName);
        return map;
    }

    /*
     * The New design to obtain font style into arrayList
     */

    private void initFontStyleData(Context targetContext,
            String targetPackageName) {
        int styleResId = Utils.getResIdByType(targetPackageName,
                Utils.TYPE_STYLE, mPackageRes);
        HashMap<String, ArrayList> map = obtainStyleNamesByType(targetContext,
                styleResId, mPackageRes);
        if (null != mfontStyleName) {
            // fontStyleArrayAdapter = new ArrayAdapter<String>(mContext,
            // android.R.layout.simple_spinner_item, mfontStyleName);
            // fontStyleArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fontStyleArrayAdapter = new ArrayAdapter<String>(mContext,
                    R.layout.spinner_item, mfontStyleName);
            fontStyleArrayAdapter
                    .setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        } else
            Log.d("previewFontStyle FontStyle or Typeface",
                    "fontStyleList/typefaceList = null");
    }

    /*
     *
     */
    private void initPackageResource(String targetPackageName) {
        synchronized (mSync) {
            if (null == mPackageRes || null != mContext) {
                try {
                    mPackageContext = mContext.createPackageContext(
                            targetPackageName, Context.CONTEXT_IGNORE_SECURITY);
                    mPackageRes = mContext.getPackageManager()
                            .getResourcesForApplication(targetPackageName);
                    RESOURCE_PACKAGE_TITLE = Utils
                            .getApplicationName(mPackageContext);
                } catch (android.content.pm.PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "can not find resource or context from : "
                            + targetPackageName);
                } catch (NotFoundException e) {
                    Log.e(TAG, "NotFoundException");
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * Initial ActionBar and tag the font size change component
     */
    private void initActionBar() {
        ActionBarExt actionBarExt = new ActionBarExt(this, getActionBar());

        ActionBarText actionBarText = new ActionBarText(this);
        actionBarText.setPrimaryText(getApplicationContext().getResources()
                .getString(mContext.getApplicationInfo().labelRes));

        ActionBarContainer actionBarContainer = actionBarExt
                .getCustomContainer();
        actionBarContainer.addCenterView(actionBarText);
        final ActionBarItemView actionBarItemViewFont = new ActionBarItemView(
                this);
        actionBarItemViewFont.setIcon(R.drawable.font);
        initFontSizeLevelAdapter();
        actionBarItemViewFont.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (view != null && view instanceof ActionBarItemView) {
                    final ListPopupWindow lpw = new ListPopupWindow(PreviewStyleActivity.this, null, android.R.attr.popupMenuStyle);
                    ListView lv = lpw.getListView();
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
                            lpw.dismiss();
                            applyFontSizeChange(mContext, position);
                        }
                    });

                    lpw.setAdapter(fontSizeLevelAdapter);
                    lpw.show();
                }
            }
        });

        actionBarContainer.addRightView(actionBarItemViewFont);
    }

    /*
     * Initial Spinner component
     */
    private void initSpinner(int spinnerPosition) {
        mSpinnerFontStyle = (Spinner) findViewById(R.id.myFontStyle);
        mSpinnerFontStyle.setAdapter(fontStyleArrayAdapter);
        mSpinnerFontStyle
                .setOnItemSelectedListener(new SpinnerSelectedListener());
        mSpinnerFontStyle.setSelection(spinnerPosition);
    }

    /*
     * Init PreviewComponent()
     */
    private void initPreviewComponent() {
        mText = (TextView) findViewById(R.id.myText);
        mHtcEdit = (HtcEditText) findViewById(R.id.myHtcEdit);
        mHtcEdit.setBackgroundResource(R.drawable.common_b_inputfield_rest);
        mBtn = (Button) findViewById(R.id.myBtn);

        mRimBtn = (HtcRimButton) findViewById(R.id.myBtn1);
        mRimBtn1 = (HtcRimButton) findViewById(R.id.myBtn2);
        mRimBtn.setIconResource(R.drawable.ic_launcher);
        mRimBtn1.setIconResource(R.drawable.ic_launcher);
        mRimBtn.setSingleLine();
        mRimBtn.setEllipsize(TruncateAt.MARQUEE);
        mRimBtn.setHorizontalFadingEdgeEnabled(true);

        mIconBtn = (HtcIconButton) findViewById(R.id.myBtn3);
        mIconBtn1 = (HtcIconButton) findViewById(R.id.myBtn4);
        mIconBtn.setIconResource(R.drawable.ic_launcher);
        mIconBtn1.setIconResource(R.drawable.ic_launcher);
        mIconBtn.setSingleLine();
        mIconBtn.setEllipsize(TruncateAt.MARQUEE);
        mIconBtn.setHorizontalFadingEdgeEnabled(true);
        mIconBtn.stayInPress(true);

        mCheck = (CheckBox) findViewById(R.id.myCheckbox);
        mRadio = (RadioButton) findViewById(R.id.myRadioButton);
        mSwitch = (Switch) findViewById(R.id.mySwitch);
        mInfoPanelSize = (TextView) findViewById(R.id.txPanelSize);
        mInfoPanelColor = (TextView) findViewById(R.id.txPanelColor);
        mInfoPanelStyle = (TextView) findViewById(R.id.txPanelStyle);
        mInfoPanelTypeface = (TextView) findViewById(R.id.txPanelTypeFace);
    }

    /*
     * Init InfoPanel
     */
    private void updateActionBarTitle() {
        ActionBarExt actionBarExt = new ActionBarExt(this, getActionBar());
        ActionBarText actionBarText = new ActionBarText(this);
        actionBarText.setPrimaryText("ApplicationName:");
        actionBarText.setSecondaryText(RESOURCE_PACKAGE_TITLE);

        ActionBarContainer actionBarContainer = actionBarExt
                .getCustomContainer();
        actionBarContainer.addCenterView(actionBarText);
    }

    class SpinnerSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1,
                int arrayIndex, long arg3) {
            String mInput = mHtcEdit.getText().toString();
            mText.setTextAppearance(mPackageContext,
                    mfontStyleId.get(arrayIndex));
            mText.setText(mInput);
            mHtcEdit.setTextAppearance(mPackageContext,
                    mfontStyleId.get(arrayIndex));
            mBtn.setTextAppearance(mPackageContext,
                    mfontStyleId.get(arrayIndex));
            mBtn.setText(mInput);
            mRimBtn.setText(mInput);
            mRimBtn.setTextAppearance(mPackageContext,
                    mfontStyleId.get(arrayIndex));
            mRimBtn1.setText(mInput);
            mRimBtn1.setTextAppearance(mPackageContext,
                    mfontStyleId.get(arrayIndex));
            mIconBtn.setText(mInput);
            mIconBtn.setTextAppearance(mPackageContext,
                    mfontStyleId.get(arrayIndex));
            mIconBtn1.setText(mInput);
            mIconBtn1.setTextAppearance(mPackageContext,
                    mfontStyleId.get(arrayIndex));
            mCheck.setTextAppearance(mPackageContext,
                    mfontStyleId.get(arrayIndex));
            mCheck.setText(mInput);
            mRadio.setTextAppearance(mPackageContext,
                    mfontStyleId.get(arrayIndex));
            mRadio.setText(mInput);
            mSwitch.setTextAppearance(mPackageContext,
                    mfontStyleId.get(arrayIndex));
            updateInfoPanel(mPackageContext, mfontStyleId.get(arrayIndex));
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }

    }

    private void updateInfoPanel(Context context, int applyStyleResId) {
        TypedArray a;
        int size, color, style;
        String typeface;
        a = context.obtainStyledAttributes(applyStyleResId,
                R.styleable.myInfoPanel);
        size = a.getDimensionPixelSize(
                R.styleable.myInfoPanel_android_textSize, 0);
        color = a.getColor(R.styleable.myInfoPanel_android_textColor, 0);
        style = a.getInteger(R.styleable.myInfoPanel_android_textStyle, 0);
        typeface = a.getString(R.styleable.myInfoPanel_android_fontFamily);
        mInfoPanelSize.setText(Integer.toString(size) + "px");
        mInfoPanelColor.setText(String.format("#%06X", 0xFFFFFF & color));
        mInfoPanelColor.setTextColor(color);
        switch (style) {
        case Typeface.BOLD:
            mInfoPanelStyle.setText("Bold");
            break;
        case Typeface.BOLD_ITALIC:
            mInfoPanelStyle.setText("Bold/Italic");
            break;
        case Typeface.ITALIC:
            mInfoPanelStyle.setText("Italic");
            break;
        case Typeface.NORMAL:
            mInfoPanelStyle.setText("Normal");
            break;
        default:
            mInfoPanelStyle.setText("Others");
            break;
        }
        mInfoPanelTypeface.setText(typeface);
        // mInfoPanelColor.setText(a.getColor(R.styleable.myInfoPanel_android_textColor,
        // 0));
        a.recycle();
    }

    private void initFontSizeLevelAdapter() {
        ArrayList<String> fontSizeArray = new ArrayList<String>();
        fontSizeArray.add("Font size : Small");
        fontSizeArray.add("Font size : Medium");
        fontSizeArray.add("Font size : Large");
        fontSizeArray.add("Font size : Extra large");
        fontSizeArray.add("Font size : Huge");

        fontSizeLevelAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, fontSizeArray);
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
            fontscale = 1.35F;
            break;
        case 4:
            fontscale = 1.45F;
            break;
        default:
            fontscale = 1.00F;
            break;
        }

        try {
            Class<?> activityManagerNative = Class
                    .forName("android.app.ActivityManagerNative");
            Object am = activityManagerNative.getMethod("getDefault").invoke(
                    activityManagerNative);
            Object conf = am.getClass().getMethod("getConfiguration")
                    .invoke(am);
            conf.getClass().getField("fontsize").setInt(conf, fontsize);
            conf.getClass().getField("fontScale").setFloat(conf, fontscale);
            am.getClass()
                    .getMethod("updateConfiguration",
                            android.content.res.Configuration.class)
                    .invoke(am, conf);
            am.getClass()
                    .getMethod("updatePersistentConfiguration",
                            android.content.res.Configuration.class)
                    .invoke(am, conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
