package com.htc.lib1.theme.test;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.htc.lib1.theme.ThemeFileInnerHelper;
import com.htc.lib1.theme.ThemeFileUtil;

import java.io.File;
import java.util.ArrayList;

public class TestFileUtil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_file_util);

        ListView lv1 = (ListView) findViewById(R.id.listView1);
        lv1.setAdapter(mLeftAdapter);

        ListView lv2 = (ListView) findViewById(R.id.listView2);
        lv2.setAdapter(mRightAdapter);

        mLeftList.add(ThemeFileUtil.ThemeFile.Avatar);
        mLeftList.add(ThemeFileUtil.ThemeFile.CBaseline);
        mLeftList.add(ThemeFileUtil.ThemeFile.CCategoryOne);
        mLeftList.add(ThemeFileUtil.ThemeFile.CCategoryTwo);
        mLeftList.add(ThemeFileUtil.ThemeFile.CCategoryThree);
        mLeftList.add(ThemeFileUtil.ThemeFile.CResources);
        mLeftList.add(ThemeFileUtil.ThemeFile.Dotview);
        mLeftList.add(ThemeFileUtil.ThemeFile.PhoneDialer);
        mLeftList.add(ThemeFileUtil.ThemeFile.WallpaperLockscreen);
        mLeftList.add(ThemeFileUtil.ThemeFile.WallpaperMessage);
        mLeftList.add(ThemeFileUtil.ThemeFile.WeatherClock);
        mLeftList.add(ThemeFileUtil.ThemeFile.Navigation);

        Button buttonAsync = (Button) findViewById(R.id.button_async_start);
        buttonAsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemeFileUtil.getThemeFilesAsync(v.getContext(),  getFileCallback(), mLeftCheckedList.toArray(new ThemeFileUtil.ThemeFile[mLeftCheckedList.size()]));
            }
        });

        Button buttonSync = (Button) findViewById(R.id.button_sync_start);
        buttonSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemeFileUtil.ThemeFileTaskInfo info = ThemeFileUtil.getThemeFiles(v.getContext(), mLeftCheckedList.toArray(new ThemeFileUtil.ThemeFile[mLeftCheckedList.size()]));
                showRightList(info.getAppLocalThemePath(), info.getTimeCost(), "Sync: ");
            }
        });

        Button buttonClear = (Button) findViewById(R.id.button_clear_file);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long begin = System.currentTimeMillis();
                ThemeFileInnerHelper.deleteFolderFile(ThemeFileUtil.getAppsThemePath(v.getContext()), false);
                begin = System.currentTimeMillis() - begin;
                showRightList(ThemeFileUtil.getAppsThemePath(v.getContext()), begin, "Clear: ");
            }
        });
    }

    public ThemeFileUtil.FileCallback getFileCallback() {
        return new ThemeFileUtil.FileCallback() {
            @Override
            public void onCompleted(Context context, ThemeFileUtil.ThemeFileTaskInfo result) {
                showRightList(result.getAppLocalThemePath(), result.getTimeCost(), "Async: ");
            };

            @Override
            public void onCanceled(Context context, ThemeFileUtil.ThemeFileTaskInfo result) {
                showRightList(result.getAppLocalThemePath(), result.getTimeCost(), "Async cancel duplicate: ");
            };
        };
    }

    private void showRightList(String path, long timeCost, String prefix) {
        mRightList.clear();

        File f = new File(path);
        listFiles(f, mRightList);

        mRightAdapter.notifyDataSetChanged();
        Toast.makeText(TestFileUtil.this, prefix + timeCost + "ms", Toast.LENGTH_SHORT).show();
    }

    private void listFiles(File file, ArrayList<String> list) {
        if (file == null || !file.exists())
            return;

        if (file.isDirectory()) {
            File[] children = file.listFiles();
            for(int i = 0; i < children.length; ++i) {
                listFiles(children[i], list);
            }

        } else {
            list.add(file.getPath());
            android.util.Log.d("TestFileUtil", file.getPath());
        }
    }

    private ArrayList<ThemeFileUtil.ThemeFile> mLeftList = new ArrayList<ThemeFileUtil.ThemeFile>();
    private ArrayList<ThemeFileUtil.ThemeFile> mLeftCheckedList = new ArrayList<ThemeFileUtil.ThemeFile>();

    private BaseAdapter mLeftAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mLeftList.size();
        }

        @Override
        public ThemeFileUtil.ThemeFile getItem(int position) {
            return mLeftList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = convertView == null ? new TextView(parent.getContext()) : (TextView) convertView;
            ThemeFileUtil.ThemeFile item = getItem(position);
            tv.setText(item.name());
            tv.setBackgroundColor(mLeftCheckedList.contains(item) ? Color.DKGRAY : Color.GRAY);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThemeFileUtil.ThemeFile item = (ThemeFileUtil.ThemeFile) v.getTag();
                    if (mLeftCheckedList.contains(item)) {
                        mLeftCheckedList.remove(item);
                        v.setBackgroundColor(Color.GRAY);
                    } else {
                        mLeftCheckedList.add(item);
                        v.setBackgroundColor(Color.DKGRAY);
                    }

                }
            });
            tv.setTag(item);
            return tv;
        }
    };

    private ArrayList<String> mRightList = new ArrayList<String>();

    private BaseAdapter mRightAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mRightList.size();
        }

        @Override
        public String getItem(int position) {
            return mRightList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = convertView == null ? new TextView(parent.getContext()) : (TextView) convertView;
            tv.setText(getItem(position));
            return tv;
        }
    };
}
