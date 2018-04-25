package com.htc.lib1.cc.widget;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.htc.lib1.cc.R;

import java.util.List;
import java.util.Set;

/**
 * An adapter that exposes data from the query results of a specified list of
 * intents. This is so-called multiple intent Share Via and the requirements
 * are:
 * <p/>
 * <li> The sharing list will be sorted by count, that is, the most used item
 * will be placed at the 1st place.
 * <li> The items with equal counts will be placed in alphabetical order.
 * <li> For more than 5 items, the 5th item will be “More”. Upon clicking “More”,
 * the whole item list will be shown and scrollable.
 * <li> Data sharing list will be provided from each application by different
 * content type (Multiple intent).
 *
 * this class is modified from HtcShareViaMultipleAdapter.
 * @hide
 */
public class HtcShareAdapter extends BaseAdapter {

    // PATH: /data/data/<PackageName>/files/<FILE_NAME>
    private static final String FILE_NAME = "task_specific_history_file_name.xml";
    private static final boolean DEBUG = false;
    private static final String TAG = "HtcShareAdapter";
    private static final double FONTSCALE_LARGE = 1.15;

    private final PackageManager mPM;
    private final LayoutInflater mInflater;
    private final int mSize;
    private final int mLayout;
    private final HtcActivityChooserModel mDataModel;
    private final Activity mActivity;
    private final boolean mForceSingleLine;

    private boolean mRegistered = false;
    private boolean mDataReady;

    private final DataSetObserver mModelDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            // This is called in the non-UI thread, but notifyDataSetChanged()
            // should be called in the UI-thread.
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (DEBUG) Log.d(TAG, "run: notifyDataSetChanged");
                    mDataReady = true;
                    notifyDataSetChanged();
                }
            });
        }
    };

    /**
     * Constructor
     *
     * @param intents  A list of intents used to query activities
     * @param allowed  The package names allowed to appear in the query results
     * @param blocked  The package names used to exclude from the query results
     * @param activity The application's context
     * @see com.htc.lib1.cc.widget.HtcShareViaAdapter
     */
    public HtcShareAdapter(List<Intent> intents, Set<String> allowed,
                           Set<String> blocked, Activity activity, boolean darkTheme, boolean queryByPackage) {
        mActivity = activity;

        mPM = activity.getPackageManager();
        mInflater = activity.getLayoutInflater();
        mSize = activity.getResources().getDimensionPixelSize(android.R.dimen.app_icon_size);
        if (darkTheme) {
            mLayout = R.layout.adapteritem_resolveinfo_dark;
        } else {
            mLayout = R.layout.adapteritem_resolveinfo;
        }
        mForceSingleLine = activity.getResources().getConfiguration().fontScale > FONTSCALE_LARGE;

        mDataModel = HtcActivityChooserModel.getLazy(activity, FILE_NAME);

        setIntents(intents, allowed, blocked, queryByPackage);
    }

    /**
     * Set a list of intents to query and
     * lists of blocked/allowed packages
     *
     * @param intents A list of intents used to query activities.
     * @param allowed The package names allowed to appear in the query results
     * @param blocked The package names used to exclude from the query results
     */
    public void setIntents(List<Intent> intents, Set<String> allowed, Set<String> blocked, boolean queryByPackage) {
        if ((null == intents && (null == mDataModel.getIntents() || mDataModel.getIntents().isEmpty())) || (null != intents && intents.equals(mDataModel.getIntents()))) {
            if ((null == allowed && (null == mDataModel.getAllowed() || mDataModel.getAllowed().isEmpty())) || (null != allowed && allowed.equals(mDataModel.getAllowed()))) {
                if ((null == blocked && (null == mDataModel.getBlocked() || mDataModel.getBlocked().isEmpty())) || (null != blocked && blocked.equals(mDataModel.getBlocked()))) {
                    if (mDataModel.queryByPackage() == queryByPackage) {
                        // totally the same
                        // TODO actually, I do not know if data is ready or not, if user calls setIntents twice in a short time, mDataReady will be true, but mDataModel may not finish loading
                        mDataReady = true;
                        if (DEBUG) Log.d(TAG, "setIntents: totally the same. assume data ready");
                        return;
                    }
                }
            }
        }

        // before set into mDataModel, we reset the data (notifyDataSetChanged)
        if (!mRegistered) {
            mRegistered = true;
            mDataModel.registerObserver(mModelDataSetObserver);
        }
        mDataReady = false;
        notifyDataSetChanged();

        //
        mDataModel.setParametersLazy(allowed, blocked, queryByPackage);
        mDataModel.setIntent(intents);
    }

    /**
     * call this to update activity order/priority after user chooses one.
     *
     * @param position the position user chooses
     * @return resolveInfo of the chosen item
     */
    public void chooseItem(int position) {
        if (DEBUG) Log.d(TAG, "chooseItem: position=" + position);
        mDataModel.chooseActivity(position);
    }

    /**
     * get matched intents for a resolveInfo
     * @param rInfo the resolveInfo user chose
     * @return all matched intents
     */
    public List<Intent> getMatchedIntents(ResolveInfo rInfo) {
        return mDataModel.getMatchedIntents(rInfo);
    }

    /**
     * return if data ready.
     *
     * @return mDataReady
     */
    public boolean dataReady() {
        return mDataReady;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @hide
     */
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);

        // adapterView should refresh after calling this.
        // in case the data changed before registered.
        if (!mRegistered) {
            if (DEBUG) Log.d(TAG, "registerDataSetObserver: register observer...");
            mRegistered = true;
            mDataModel.registerObserver(mModelDataSetObserver);
        }
    }

    /**
     * @hide
     */
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (mRegistered) {
            if (DEBUG) Log.d(TAG, "unregisterDataSetObserver: unregister observer...");
            mRegistered = false;
            mDataModel.unregisterObserver(mModelDataSetObserver);
        }

        super.unregisterDataSetObserver(observer);
    }

    /**
     * @hide
     */
    @Override
    public int getCount() {
        return mDataReady ? mDataModel.getActivityCount() : 0;
    }

    /**
     * @hide
     */
    @Override
    public ResolveInfo getItem(int position) {
        if (mDataReady && position >= 0 && position < getCount()) {
            return mDataModel.getActivity(position);
        } else {
            return null;
        }
    }

    /**
     * @hide
     */
    @Override
    public long getItemId(int position) {
        ResolveInfo item = getItem(position);
        if (null == item) return -1;
        String tmp = item.activityInfo.packageName + "/" + item.activityInfo.name;
        return tmp.hashCode();
    }

    /**
     * @hide
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * @hide
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResolveInfo item = getItem(position);
        if (null != item) {
            CharSequence label = item.loadLabel(mPM);
            Drawable icon = item.loadIcon(mPM);
            icon.setBounds(0, 0, mSize, mSize);

            if (!(convertView instanceof TextView)) {
                convertView = mInflater.inflate(mLayout, parent, false);
                if (mForceSingleLine) {
                    ((TextView) convertView).setLines(1);
                }
            }
            TextView tv = (TextView) convertView;
            tv.setText(label);
            tv.setCompoundDrawables(null, icon, null, null);
        }
        return convertView;
    }
}
