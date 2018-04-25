
package com.htc.sense.commoncontrol.demo.tabbar;

import java.util.ArrayList;
import java.util.List;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.htc.lib1.cc.view.tabbar.TabBar;
import com.htc.lib1.cc.view.tabbar.TabBar.TabAdapter;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;

public class TabBarDemo extends CommonDemoActivityBase {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TabBar tabbar = new TabBar(this);
        tabbar.setAdapter(new MyAdapter());

        LinearLayout content = new LinearLayout(this);
        content.addView(tabbar);
        setContentView(content);
    }

    private static class MyAdapter implements TabAdapter {
        private List<String> data = new ArrayList<String>();

        public MyAdapter() {
            for (int i = 0; i < 5; i++) {
                data.add("Title" + i);
            }
        }

        @Override
        public boolean isAutomotiveMode() {
            return false;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return data.get(position);
        }

        @Override
        public int getPageCount(int position) {
            return position;
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean isCNMode() {
            // TODO Auto-generated method stub
            return false;
        }
    }
}
