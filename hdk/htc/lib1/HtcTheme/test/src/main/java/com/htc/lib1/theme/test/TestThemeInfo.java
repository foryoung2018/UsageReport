package com.htc.lib1.theme.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.htc.lib1.theme.ThemeFileUtil;
import com.htc.lib1.theme.ThemeType;

import java.util.ArrayList;

public class TestThemeInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_theme_info);

        ListView lv1 = (ListView) findViewById(R.id.listView1);
        lv1.setAdapter(mLeftAdapter);


        for (int i =0; i < ThemeType.getKeyCount(); ++i) {
            mLeftList.add(new ThemeInfoStruct(i));
        }

        Button buttonRefresh = (Button) findViewById(R.id.button_refresh);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeftAdapter.notifyDataSetChanged();
            }
        });

        Button buttonUpdate = (Button) findViewById(R.id.button_update);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ThemeInfoStruct infoStruct : mLeftList) {
                    ThemeFileUtil.saveAppliedThemeInfo(v.getContext(), infoStruct.themeKey);
                    infoStruct.lastInfo = ThemeFileUtil.getCurrentAppliedThemeInfo(v.getContext(), infoStruct.themeKey, 0);
                }
                mLeftAdapter.notifyDataSetChanged();
            }
        });

        Button buttonClear = (Button) findViewById(R.id.button_clear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ThemeInfoStruct infoStruct : mLeftList) {
                    ThemeFileUtil.clearAppliedThemeInfo(v.getContext(), infoStruct.themeKey, 0);
                    infoStruct.lastInfo = "";
                }
                mLeftAdapter.notifyDataSetChanged();
            }
        });
    }

    public class ThemeInfoStruct {
        public int themeKey;
        public String lastInfo;
        public String currentInfo;

        public ThemeInfoStruct(int themeKey) {
            this.themeKey = themeKey;
        }
    }

    private ArrayList<ThemeInfoStruct> mLeftList = new ArrayList<ThemeInfoStruct>();

    private BaseAdapter mLeftAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mLeftList.size();
        }

        @Override
        public ThemeInfoStruct getItem(int position) {
            return mLeftList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup container = convertView == null ? (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theme_info, parent, false) : (ViewGroup) convertView;
            ThemeInfoStruct struct = getItem(position);

            TextView themeTypeName = (TextView) container.findViewById(R.id.theme_type_name);
            themeTypeName.setText(ThemeType.getKey(struct.themeKey));

            TextView themeInfoLast = (TextView) container.findViewById(R.id.theme_type_info_last);
            String lastInfo = ThemeFileUtil.getSprefAppliedThemeInfo(parent.getContext(), struct.themeKey);
            themeInfoLast.setText("shared preference:\n" + lastInfo);

            TextView themeInfoCurrent = (TextView) container.findViewById(R.id.theme_type_info_current);
            String currentInfo = ThemeFileUtil.getCurrentAppliedThemeInfo(parent.getContext(), struct.themeKey, 0);
            themeInfoCurrent.setText("current:\n" + currentInfo);

            TextView themeChanged = (TextView) container.findViewById(R.id.theme_changed);
            themeChanged.setText("same:\n" + String.valueOf(TextUtils.equals(lastInfo, currentInfo)));

            container.setTag(struct);
            return container;
        }
    };
}
