package com.htc.sense.commoncontrol.demo.htcpreference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

public class HtcPreferenceDemo extends CommonDemoActivityBase implements  OnItemClickListener{
    private ListView mListView = null;
    private PreferenceItemAdapter madapter;
    private final String[] INTENTS = {"android.intent.action.custom.activitypreferences",
            "android.intent.action.custom.fragmentpreferences",
            "android.intent.action.custom.preferencewithheaders",
            "android.intent.action.custom.preferencesdialog",
            "android.intent.action.custom.preferencesfromcode",
            "android.intent.action.custom.DefaultValues"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        mListView = (ListView) findViewById(android.R.id.list);
        madapter=new PreferenceItemAdapter();
        mListView.setDividerHeight(2);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(madapter);

        mListView.setDivider(getResources().getDrawable(
                com.htc.lib1.cc.R.drawable.common_list_divider));
        mListView.setBackgroundResource(com.htc.lib1.cc.R.drawable.common_app_bkg);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
            int position, long id) {
        // TODO Auto-generated method stub
        Intent intent =new Intent(INTENTS[position]);
        if(INTENTS[position] == "android.intent.action.custom.preferencesfromxml")
        {
            intent.putExtra(":android:show_fragment", true);
            intent.putExtra(":android:show_fragment_title", 2);
            intent.putExtra(":android:show_fragment_short_title", 2);

            intent.putExtra("extra_prefs_show_button_bar",true);
            intent.putExtra(":android:no_headers", true);
        }



        startActivityForResult(intent, 1);
    }

    @Override
    public View onCreateView(View parent, String name, Context context,
            AttributeSet attrs) {
        // TODO Auto-generated method stub
        return super.onCreateView(parent, name, context, attrs);
    }
    public class PreferenceItemAdapter extends BaseAdapter {

        String[] ENTRIES ={"1.Add Preference Using Activity","2.Add Preference from Using Fragment","3.Add Preference with header","4.Add Preference with Dialog"};
//        ,"2.Add Preference from Code",
//                           "4.Add Preference with header",
//                           "5.Default Values"};
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return ENTRIES.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return ENTRIES[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            HtcListItem htclistitem =(HtcListItem)LayoutInflater.from(HtcPreferenceDemo.this).inflate(R.layout.list_item_layout,null);
            TextView tv =(TextView)htclistitem.findViewById(R.id.text1);
            tv.setTextAppearance(HtcPreferenceDemo.this, com.htc.lib1.cc.R.style.list_primary_m_bold);
            tv.setText(ENTRIES[position]);

            return htclistitem;
        }
    }

}
