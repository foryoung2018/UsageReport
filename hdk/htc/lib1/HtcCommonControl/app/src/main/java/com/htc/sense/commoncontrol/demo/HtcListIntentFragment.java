
package com.htc.sense.commoncontrol.demo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.htc.lib1.cc.app.HtcListFragment;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtcListIntentFragment extends HtcListFragment {
    private static final String CLASSES_KEY = "classes_key";
    private static final String INTENT_KEY = "intent_key";

    private Class<?>[] mActivityClasses = null;
    private Intent mListIntent = null;
    private OnActivityStartListener mListener = null;
    private HashMap<String, Intent> mIntenMap = new HashMap<String, Intent>();
    private String[] mLabels;
    private ArrayList<CharSequence> mLabelList = new ArrayList<CharSequence>();
    private boolean mSortLabel = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ensureIntentData();
    }

    public HtcListIntentFragment() {
    }

    public HtcListIntentFragment(Class<?>[] activityClasses) {
        this.mActivityClasses = activityClasses;
        Bundle arg = new Bundle();
        arg.putSerializable(CLASSES_KEY, mActivityClasses);
        setArguments(arg);
    }

    public HtcListIntentFragment(Intent listIntent) {
        this.mListIntent = listIntent;
        Bundle arg = new Bundle();
        arg.putParcelable(INTENT_KEY, mListIntent);
        setArguments(arg);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CommonUtil.applyHtcListViewStyle(getListView(), CommonUtil.MODE_LIGHT);
        setListAdapter(new Htc1LineListAdapter(getActivity(), loadIntentData()));
        registerForContextMenu(getListView());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = mIntenMap.get(getListAdapter().getItem(position));
        if (intent == null) return;

        if (null != mListener) {
            mListener.onStartActivity(position, intent);
        }
        startActivity(intent);
    }

    public void setOnActivityStartListener(OnActivityStartListener l) {
        mListener = l;
    }

    public interface OnActivityStartListener {
        void onStartActivity(int position, Intent intent);
    }

    private void ensureIntentData() {
        if (mListIntent != null || mActivityClasses != null) return;

        Bundle bundle = getArguments();
        if (bundle == null) return;

        mListIntent = bundle.getParcelable(INTENT_KEY);
        if (mListIntent != null) return;

        mActivityClasses = (Class<?>[]) bundle.getSerializable(CLASSES_KEY);
    }
    private String[] loadIntentData() {
        mIntenMap.clear();
        if (mActivityClasses == null && mListIntent == null) {
            return null;
        }

        final PackageManager pm = getActivity().getPackageManager();
        if (mListIntent != null) {
            List<ResolveInfo> list = pm.queryIntentActivities(mListIntent, 0);
            if (null == list) return null;

            String prefix = mListIntent.getStringExtra(DemoActivity.LABEL_PATH);

            if (prefix == null) {
                prefix = "";
            }

            String[] prefixPath;
            String prefixWithSlash = prefix;

            if (prefix.equals("")) {
                prefixPath = null;
            } else {
                prefixPath = prefix.split("/");
                prefixWithSlash = prefix + "/";
            }

            int len = list.size();

            Map<String, Boolean> entries = new HashMap<String, Boolean>();
            mLabelList.clear();

            for (int i = 0; i < len; i++) {
                ResolveInfo info = list.get(i);
                CharSequence labelSeq = info.loadLabel(pm);
                String label = labelSeq != null ? labelSeq.toString() : info.activityInfo.name;

                if (prefixWithSlash.length() == 0 || label.startsWith(prefixWithSlash)) {

                    String[] labelPath = label.split("/");

                    String nextLabel = prefixPath == null ? labelPath[0] : labelPath[prefixPath.length];

                    if ((prefixPath != null ? prefixPath.length : 0) == labelPath.length - 1) {
                        addItem(nextLabel, activityIntent(
                                info.activityInfo.applicationInfo.packageName,
                                info.activityInfo.name));
                    } else {
                        if (entries.get(nextLabel) == null) {
                            addItem(nextLabel, browseIntent(prefix.equals("") ? nextLabel : prefix + "/" + nextLabel));
                            entries.put(nextLabel, true);
                        }
                    }
                }
            }

        } else if (null != mActivityClasses) {
            final int length = mActivityClasses.length;
            mLabels = new String[length];

            for (int i = 0; i < length; i++) {
                ComponentName name = new ComponentName(getActivity(), mActivityClasses[i]);
                ActivityInfo activityInfo = null;
                try {
                    activityInfo = pm.getActivityInfo(name, 0);
                    final CharSequence label = activityInfo.loadLabel(pm);
                    mLabels[i] = TextUtils.isEmpty(label) ? mActivityClasses[i].getSimpleName() : label.toString();
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                    mLabels[i] = mActivityClasses[i].getSimpleName();
                }
                if (activityInfo != null) {
                    addItem(mLabels[i], activityIntent(activityInfo.packageName, activityInfo.name));
                } else {
                    addItem(mLabels[i], null);
                }
            }
        }

        String[] labels = new String[mIntenMap.size()];
        mIntenMap.keySet().toArray(labels);

        if (mSortLabel) Arrays.sort(labels);
        return labels;
    }
    protected Intent activityIntent(String pkg, String componentName) {
        Intent result = new Intent();
        result.setClassName(pkg, componentName);
        return result;
    }

    protected Intent browseIntent(String path) {
        Intent result = new Intent();
        result.setClass(getActivity(), DemoActivity.class);
        result.putExtra(DemoActivity.LABEL_PATH, path);
        return result;
    }

    protected void addItem(String name, Intent intent) {
        mIntenMap.put(name, intent);
    }

    public void setSortLabel(boolean sortLabel) {
        mSortLabel = sortLabel;
    }

}
