package com.htc.sense.commoncontrol.demo.colortable;

import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class ThemeColorTable extends CommonDemoActivityBase{
    public static final String KEY_THEME_NAME = "HtcDeviceDefault";
    public static final String KEY_CATEGORY_NAME = null;
    public static final String themePackage = "com.htc.sense.commoncontrol.demo";

    private boolean firstLaunch = true;
    private int[] mColorValues;
    private String[] mColorNames;

    private LayoutInflater mInflater;
    private String mThemeName;
    private String mCategoryName;

    private ListView mListView;

    private void getInfoFromIntent() {
        Intent intent = getIntent();
        mThemeName = (null == intent.getStringExtra(KEY_THEME_NAME)) ? KEY_THEME_NAME : intent.getStringExtra(KEY_THEME_NAME) ;
        mCategoryName = (null == intent.getStringExtra(KEY_CATEGORY_NAME)) ? KEY_CATEGORY_NAME : null ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getInfoFromIntent();
        if (null == mThemeName) {
            return;
        }
        String themeCategory;
        if (null == mCategoryName || "".equals(mCategoryName)) {
            themeCategory = mThemeName;
        } else {
            themeCategory = mThemeName + "." + mCategoryName;
        }
        initColorArray(themeCategory);
        setContentView(R.layout.theme_color_table);
        mListView = (ListView) findViewById(R.id.lv);
        mInflater = getLayoutInflater();
        setupListView();
        setupSpinner();
    }

    private ContextThemeWrapper createThemeContext(String packageName,
                                                   String themeName) {
        ContextThemeWrapper ctw = null;
        try {
            Context context = createPackageContext(packageName,
                    Context.CONTEXT_IGNORE_SECURITY);
            int themeId = context.getResources().getIdentifier(themeName,
                    "style", packageName);
            ctw = new ContextThemeWrapper(context, themeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ctw;
    }

    private void initColorArray(String themeName) {
        int colorCounts = R.styleable.ThemeColor.length;
        int attrs[] = new int[colorCounts];
        ContextThemeWrapper mCtx = createThemeContext(themePackage, themeName);
        if (null == mColorNames && null == mColorValues) {
            mColorValues = new int[colorCounts];
            mColorNames = new String[colorCounts];
        }
        TypedArray a = mCtx.obtainStyledAttributes(R.styleable.ThemeColor);
        for (int i = 0; i < colorCounts; i++) {
            attrs[i] = R.styleable.ThemeColor[i];
            String name = mCtx.getResources().getResourceEntryName(attrs[i]);
            mColorValues[i] = a.getColor(i, 0);
            mColorNames[i] = name;
        }
        a.recycle();
    }
    private void setupSpinner(){
        Spinner spinner = (Spinner) findViewById(R.id.colorTableSpinner);
        ArrayAdapter<CharSequence> adapter =ArrayAdapter.createFromResource(this,
                R.array.theme_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = view.getId() == android.R.id.text1 ? ((TextView) view).getText().toString() : "";
                Intent intent = new Intent();
                if (selectedItem.equals(KEY_THEME_NAME)){
                    intent.putExtra(KEY_THEME_NAME, selectedItem);
                }else{
                    intent.putExtra(KEY_THEME_NAME, selectedItem.split("\\.")[0]);
                    intent.putExtra(KEY_CATEGORY_NAME, selectedItem.split("\\.")[1]);
                }
//                startActivity(intent);
//                ThemeColorTable.this.finish();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setupListView() {
        mListView.setAdapter(new BaseAdapter() {

            public long getItemId(int position) {
                return position;
            }

            public Object getItem(int position) {
                return "Color Index" + position;
            }

            public int getCount() {
                if (null == mColorNames) {
                    return 0;
                }
                return mColorNames.length;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder = null;
                if (null == convertView) {
                    holder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.list_item_theme_color_table, null);
                    holder.imageView = (ImageView) convertView.findViewById(R.id.iv_color);
                    holder.tvColor = (TextView) convertView.findViewById(R.id.tv_color);
                    holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                int color = mColorValues[position];
                String name = mColorNames[position];
                holder.imageView.setBackgroundColor(color);
                holder.tvName.setText(name);

                String alpha = Integer.toHexString(Color.alpha(color));
                StringBuffer value = new StringBuffer("#");
                if (!"ff".equals(alpha)) {
                    value.append(alpha);
                }
                value.append(Integer.toHexString(Color.red(color)));
                value.append(Integer.toHexString(Color.green(color)));
                value.append(Integer.toHexString(Color.blue(color)));

                holder.tvColor.setText(value.toString());
                return convertView;
            }
        });
    }


    private static class ViewHolder {
        ImageView imageView;
        TextView tvName;
        TextView tvColor;
    }
}
