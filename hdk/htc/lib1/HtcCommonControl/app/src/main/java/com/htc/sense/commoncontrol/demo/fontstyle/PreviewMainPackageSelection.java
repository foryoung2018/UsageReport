package com.htc.sense.commoncontrol.demo.fontstyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ListView;

import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.ListPopupBubbleWindow;

public class PreviewMainPackageSelection extends CommonDemoActivityBase {
    private ListView mListView = null;
    private KeywordSearchableAdapter mAdapter;
    private AutoCompleteTextView mSearchKeyword;
    private Map<String, String> mIntentMapping = new HashMap<String, String>();
    private Map<String, String> mKeywordMapping = new HashMap<String, String>();
    private InputMethodManager imm;

    private Context mContext;
    // private ArrayList<String> mPackageName = null;
    private Map<String, String> mPackageName = null;
    private ListPopupBubbleWindow mLpbw;

    private String[] packageNameArray = null, packageLabelArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_fontstyle_selection_main);
        getWindow().setBackgroundDrawableResource(R.drawable.common_app_bkg);
        mContext = getApplicationContext();
        initPublicFontStyle();
        initCommonControlFontStyle();
        initThreadHandler();
        // initialize search text
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mSearchKeyword = (AutoCompleteTextView) findViewById(R.id.search_keyword);
        if (mSearchKeyword != null && mSearchKeywordWatcher != null) {
            mSearchKeyword.setSingleLine();
            mSearchKeyword.addTextChangedListener(mSearchKeywordWatcher);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mSearchKeyword.getVisibility() == View.GONE) {
            mSearchKeyword.setVisibility(View.VISIBLE);
            mSearchKeyword.requestFocus();
            // show ime
            imm.showSoftInput(mSearchKeyword, 0);
        } else {
            mSearchKeyword.setVisibility(View.GONE);
            // hide ime
            imm.hideSoftInputFromWindow(mSearchKeyword.getWindowToken(), 0);
        }
        return true;
    }

    private void initPublicFontStyle() {
        com.htc.lib1.cc.widget.HtcListItem2LineText mPublicText = (com.htc.lib1.cc.widget.HtcListItem2LineText) findViewById(R.id.text1_public_font_style);
        mPublicText.setPrimaryText("Public Common Font Style");
        mPublicText.setSecondaryText("com.htc.R");
        mPublicText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String publicIntent = "com.htc.intent.action.custom.publicfontstyle";
                if (publicIntent != null) {
                    Intent intent = new Intent(publicIntent);
                    startActivity(intent);
                }
            }
        });
    }

    private void initCommonControlFontStyle() {
        com.htc.lib1.cc.widget.HtcListItem2LineText mPublicText = (com.htc.lib1.cc.widget.HtcListItem2LineText) findViewById(R.id.text1_common_font_style);
        mPublicText.setPrimaryText("AllFont Style");
        mPublicText.setSecondaryText("R");
        mPublicText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = "com.htc.sense.commoncontrol.demo";
                Intent intent = new Intent(mContext, PreviewStyleActivity.class);
                intent.putExtra("PackageName", key);
                startActivity(intent);
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
    }

    private void initListView() {
        mListView = (ListView) findViewById(android.R.id.list);
        if (mListView != null) {
            mListView.setOnItemClickListener(new listViewOnItemClickListener());
            mAdapter = new KeywordSearchableAdapter(mContext,
                    R.layout.list_item21);
            mListView.setAdapter(mAdapter);
            mListView.setDividerHeight(2);
            mListView.setDivider(getResources().getDrawable(
                    R.drawable.common_list_divider));
        }
    }

    private void initThreadHandler() {
        final Handler myHandler = new Handler();
        (new Thread(new Runnable() {
            @Override
            public void run() {
                mPackageName = Utils.obtainPackageInfo(mContext);
                packageNameArray = new String[mPackageName.size()];
                packageLabelArray = new String[mPackageName.size()];
                int index = 0;
                for (Map.Entry<String, String> mapEntry : mPackageName
                        .entrySet()) {
                    packageNameArray[index] = mapEntry.getKey().toString();
                    packageLabelArray[index] = mapEntry.getValue().toString();
                    index++;
                }
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        initListView();
                    }
                });
            }
        })).start();
    }

    class listViewOnItemClickListener implements
            AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position,
                long arg3) {
            HtcListItem2LineText text = (HtcListItem2LineText) view
                    .findViewById(R.id.text1);
            String key = text.getSecondaryText().toString();
            Intent intent = new Intent(mContext, PreviewStyleActivity.class);
            intent.putExtra("PackageName", key);
            startActivity(intent);
            // hide search text and ime
            mSearchKeyword.setVisibility(View.GONE);
            imm.hideSoftInputFromWindow(mSearchKeyword.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchKeyword != null && mSearchKeywordWatcher != null) {
            mSearchKeyword.removeTextChangedListener(mSearchKeywordWatcher);
        }
    }

    private TextWatcher mSearchKeywordWatcher = new TextWatcher() {

        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
            if (mAdapter != null) {
                mAdapter.getFilter().filter(s);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
        }
    };

    private class KeywordSearchableAdapter extends ArrayAdapter<String> {

        private int resId;
        private LayoutInflater mInflater;
        private Filter mFilter;
        private Object[] mFilteredResultsSecondary;

        public KeywordSearchableAdapter(Context context, int res) {
            super(context, res);
            resId = res;
            mInflater = LayoutInflater.from(context);
            mFilteredResultsSecondary = packageNameArray;
        }

        @Override
        public int getCount() {
            return mFilteredResultsSecondary.length;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = mInflater.inflate(resId, null);
            }
            HtcListItem2LineText text = (HtcListItem2LineText) view
                    .findViewById(R.id.text1);
            text.setPrimaryText(mPackageName.get(
                    mFilteredResultsSecondary[position].toString()).toString());
            text.setSecondaryText(mFilteredResultsSecondary[position]
                    .toString());
            return view;
        }

        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new KeywordFilter();
            }
            return mFilter;
        }

        private class KeywordFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence keyword) {
                ArrayList<String> list = new ArrayList<String>();
                if (keyword == null || keyword.length() == 0) {
                    // wrong search keyword, return whole entries
                    for (int i = 0; i < packageNameArray.length; i++) {
                        list.add(packageNameArray[i]);
                    }
                } else {
                    // find entries that match search keyword
                    String lower = keyword.toString().toLowerCase();
                    for (int i = 0; i < packageNameArray.length; i++) {
                        String entry_keyword = mKeywordMapping
                                .get(packageNameArray[i]);
                        if (null != entry_keyword)
                            entry_keyword = entry_keyword.toLowerCase();
                        if ((null != packageNameArray[i] && packageNameArray[i]
                                .toString().toLowerCase().contains(lower))
                                || (null != entry_keyword && entry_keyword
                                        .contains(lower))) {
                            list.add(packageNameArray[i]);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = list.toArray();
                results.count = list.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint,
                    FilterResults results) {
                mFilteredResultsSecondary = (Object[]) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }

    }

}
