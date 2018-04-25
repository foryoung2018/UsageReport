package com.htc.lib1.cc.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

/**
 * An adapter that exposes data from the query results of a specified list of
 * intents. This is so-called multiple intent Share Via and the requirements
 * are:
 *
 * <li> The sharing list will be sorted by count, that is, the most used item
 *      will be placed at the 1st place.
 * <li> The items with equal counts will be placed in alphabetical order.
 * <li> For more than 5 items, the 5th item will be “More”. Upon clicking “More”,
 *      the whole item list will be shown and scrollable.
 * <li> Data sharing list will be provided from each application by different
 *      content type (Multiple intent).
 * @deprecated use HtcShareActivity instead
 */
@Deprecated
public class HtcShareViaMultipleAdapter extends IHtcShareViaAdapter {

    private Context context;
    private HtcActivityChooserModel mDataModel;

    private PackageManager mPackageManager;

    private boolean mIsDismissOk = false;
    private int mExpandStatus = EXPAND_DEFAULT;

    private Drawable mListBgDraw;
    private int mResId;
    private int mBgFlag;
    private int mTextResId;

    private boolean mIsRegister = false;
    private boolean mIsDataReady = false;

    private final Handler mHandler = new Handler();
    private Resources res;

    private static class ShareViaViewHolder {
        HtcListItemColorIcon icon;
        HtcListItem1LineCenteredText text;
    }

    private final DataSetObserver mModelDataSetOberver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            // This is called in the non-UI thread, but notifyDataSetChanged()
            // should be called in the UI-thread.
            mHandler.post(new Runnable() {
                public void run() {
                    mIsDataReady = true;
                    notifyDataSetChanged();
                }
            });
        }
    };

    /**
     * Constructor. Should use HtcShareViaMultipleAdapter(List&lt;Intent>
     * intent, Context applicationContext) or
     * HtcShareViaMultipleAdapter(List&lt;Intent> intent, List&lt;String>
     * allows, List&lt;String> excludes, Context applicationContext) instead.
     *
     * @param applicationContext
     *            The application's context
     *
     * @see HtcShareViaAdapter
     */
    private HtcShareViaMultipleAdapter(Context applicationContext) {
        context = applicationContext;
        mPackageManager = context.getPackageManager();
    }

    /**
     * Constructor.
     *
     * @param intent A list of intents used to query activities
     * @param applicationContext The application's context
     *
     * @see HtcShareViaAdapter
     */
    public HtcShareViaMultipleAdapter(List<Intent> intent, Context applicationContext) {
        this(applicationContext);
        setIntent(intent, null, null);
    }

    /**
     * Constructor.
     *
     * @param intent A list of intents used to query activities
     * @param allows The package names allowed to appear in the query results
     * @param excludes The package names used to exclude from the query results
     * @param applicationContext The application's context
     *
     * @see HtcShareViaAdapter
     */
    public HtcShareViaMultipleAdapter(List<Intent> intent, List<String> allows,
            List<String> excludes, Context applicationContext) {
        this(applicationContext);
        setIntent(intent, allows, excludes);
    }

    /**
     * Set a list of intents to query and a package name to exclude from the query results.
     *
     * @param intent A list of intents used to query activities.
     * @param allows The package names allowed to appear in the query results
     * @param excludes The package names used to exclude from the query results
     */
    final void setIntent(List<Intent> intent, List<String> allows, List<String> excludes) {
        mIsDataReady = false;
        mIsRegister = true;
        mDataModel = HtcActivityChooserModel.get(context, FILE_NAME,
                mModelDataSetOberver);
        mDataModel.setAllowedPackages(allows);
        mDataModel.setExcludedPackages(excludes);
        mDataModel.setIntent(intent);
    }

    /**
     * @hide
     */
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
        if (!mIsRegister) {
            mIsRegister = true;
            if (mDataModel != null) mDataModel.registerObserver(mModelDataSetOberver);
        }
    }

    /**
     * @hide
     */
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        super.unregisterDataSetObserver(observer);
        if (mIsRegister) {
            mIsRegister = false;
            if (mDataModel != null) mDataModel.unregisterObserver(mModelDataSetOberver);
        }
    }

    /**
     * @hide
     */
    @Override
    public int getCount() {
        int count = 0;
        if (mDataModel != null) {
            if (!mIsDataReady) {
                count  = 1;
            } else {
                count = mDataModel.getActivityCount();
                if (count == 0) {
                    count = 1;
                } else {
                    if (count > (INDEX_OF_MORE + 1)
                            && (mExpandStatus != HAD_EXPAND)) {
                        mExpandStatus = NEED_EXPAND;
                    } else if (count <= (INDEX_OF_MORE + 1)
                            && (mExpandStatus == NEED_EXPAND)) {
                        mExpandStatus = EXPAND_DEFAULT;
                    }
                    if (mExpandStatus == NEED_EXPAND) {
                        count = INDEX_OF_MORE + 1;
                    }
                }
            }
        }
        return count;
    }

    /**
     * {@inheritDoc}
     *
     * @param position
     * @return Return the matching resolveInfo for launching the activity. Once
     *         this method is called, the usage count for that specified
     *         activity will be added by one.
     * @deprecated [Module internal use]
     */
    /**@hide*/
    @Override
    public Object getItem(int position) {
        if (mDataModel != null && isDataReady()) {
            int count = mDataModel.getActivityCount();

            if ((count > 0) && (position >= 0) && (position < count)) {
                ResolveInfo ri = mDataModel.getActivity(position);
                mDataModel.chooseActivity(position);
                return ri;
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * @return Return the matching intent for launching the activity. Once
     *         this method is called, the usage count for that specified
     *         activity will be added by one.
     */
    Intent getIntentItem(int position) {
        if (mDataModel != null && isDataReady()) {
            int count = mDataModel.getActivityCount();

            if ((count > 0) && (position >= 0) && (position < count)) {
                return mDataModel.chooseActivity(position);
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * @hide
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * @hide
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShareViaViewHolder vh;
        if (convertView != null) {
            vh = (ShareViaViewHolder) convertView.getTag();
        } else {
            HtcListItem item;
            if (mTextResId == com.htc.lib1.cc.R.style.darklist_primary_s) {
                // Htc popup window
                item = new HtcListItem(context, HtcListItem.MODE_POPUPMENU);
            } else {
                // Htc & Google alertDialog
                item = new HtcListItem(context);
            }
            switch (mBgFlag) {
            case BG_SET_DRAWABLE:
                item.setBackgroundDrawable(mListBgDraw);
                break;
            case BG_SET_RES_ID:
                item.setBackgroundResource(mResId);
                break;
            default:
                //item.setBackgroundDrawable(null);
                break;
            }

            vh = new ShareViaViewHolder();
            vh.icon = new HtcListItemColorIcon(context);
            vh.text = new HtcListItem1LineCenteredText(context);

            vh.icon.setLayoutParams(new HtcListItem.LayoutParams(WP, WP));
            vh.text.setLayoutParams(new HtcListItem.LayoutParams(WP, WP));

            vh.text.setTextStyle(mTextResId);

            item.addView(vh.icon);
            item.addView(vh.text);
            convertView = item;
            convertView.setTag(vh);
        }

        if (!mIsDataReady) {
            vh.icon.setColorIconImageDrawable(null);
            vh.icon.setVisibility(View.GONE);
            if (res == null) res = context.getResources();
            int resId = getStringLoading(res);
            if (resId != 0) vh.text.setText(resId);
            vh.text.setGravityCenterHorizontal(false);
        } else {
            int count = mDataModel.getActivityCount();
            if (count == 0) {
                vh.icon.setColorIconImageDrawable(null);
                vh.icon.setVisibility(View.GONE);
                if (res == null) res = context.getResources();
                int resId = getStringEmpty(res);
                if (resId != 0) vh.text.setText(resId);
                vh.text.setGravityCenterHorizontal(false);
            } else {
                if (position == INDEX_OF_MORE && (NEED_EXPAND == mExpandStatus)) {
                    vh.icon.setColorIconImageDrawable(null);
                    vh.icon.setVisibility(View.GONE);
                    vh.text.setText(MORE);
                    // Make the text centered for MORE item.
                    vh.text.setGravityCenterHorizontal(true);
                } else {
                    ResolveInfo activity = mDataModel.getActivity(position);
                    vh.icon.setColorIconImageDrawable(activity
                            .loadIcon(mPackageManager));
                    vh.icon.setVisibility(View.VISIBLE);
                    vh.text.setText(activity.loadLabel(mPackageManager));
                    vh.text.setGravityCenterHorizontal(false);
                }
            }
        }
        return convertView;
    }

    /**
     * Get all the available activities' infos
     *
     * @return an ArrayList<ResolveInfo> will be returned
     *
     * @deprecated [no use any longer]
     */
    @Deprecated
    public List<ResolveInfo> getShareListResolveInfo() {
        List<ResolveInfo> infos = new ArrayList<ResolveInfo>();
        int count = mDataModel.getActivityCount();
        for (int i = 0; i < count; i++) {
            infos.add(mDataModel.getActivity(i));
        }
        return infos;
    }

    int isExpanded() {
        return mExpandStatus;
    }

    /**
     * Replace item "more" by real items.
     * This function will not work, since
     * Sense60-styled HtcShareVia has been deprecated.
     * @deprecated please use HtcShareActivity instead
     */
    @Deprecated
    void expand() {
//        mExpandStatus = HAD_EXPAND;
    }

    /**
     * Call this to shrink, AP needs to call this before showing the dialog.
     *
     * @return
     */
    public void shrink() {
        mExpandStatus = NOT_EXPAND;
    }

    void setIsDimissOk(boolean isDismissOk) {
        mIsDismissOk = isDismissOk;
    }

    boolean isDimissOk() {
        return mIsDismissOk;
    }

    void setListItemBackgroundDrawable(Drawable d) {
        mListBgDraw = d;
        mBgFlag = BG_SET_DRAWABLE;
    }

    void setListItemBackgroundResource(int resId) {
        mResId = resId;
        mBgFlag = BG_SET_RES_ID;
    }

    void setListItemTextAppearance(int resId) {
        mTextResId = resId;
    }

    boolean isDataReady() {
        return mIsDataReady;
    }

    boolean isDataEmpty() {
        if (mDataModel != null) {
            return (mDataModel.getActivityCount() == 0);
        }
        return true;
    }

    /**
     * Sets whether the query result is based on package name and only
     * allow one resolved activity with the same package name to show.
     * If true, the behavior will be the same as that in ResolverActivity.
     *
     * @param enable If true, the query result is based on package name.
     * @see ResolverActivity
     */
    public void setQueryByPackageName(boolean enable) {
        if (mDataModel != null) {
            mDataModel.setQueryByPackageName(enable);
        }
    }
}
