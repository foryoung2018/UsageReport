/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.htc.lib1.cc.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;

//[CC] paul.wy_wang, 20131021, Remove for UI static library.
//import com.android.internal.content.PackageMonitor;

// [HTC] begin by paul.wy_wang, 2015/01/16, for HtcShareVia
import com.htc.lib0.customization.HtcWrapCustomizationManager;
import com.htc.lib0.customization.HtcWrapCustomizationReader;
// [HTC] end by paul.wy_wang, 2015/01/16, for HtcShareVia

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * This class represents a data model for choosing a component for handing a
 * given {@link Intent}. The model is responsible for querying the system for
 * activities that can handle the given intents and order found activities
 * based on historical data of previous choices. The historical data is stored
 * in an application private file. If a client does not want to have persistent
 * choice history the file can be omitted, thus the activities will be ordered
 * based on historical usage for the current session.
 * <p>
 * </p>
 * For each backing history file there is a singleton instance of this class. Thus,
 * several clients that specify the same history file will share the same model. Note
 * that if multiple clients are sharing the same model they should implement semantically
 * equivalent functionality since setting the model intent will change the found
 * activities and they may be inconsistent with the functionality of some of the clients.
 * For example, choosing a share activity can be implemented by a single backing
 * model and two different views for performing the selection. If however, one of the
 * views is used for sharing but the other for importing, for example, then each
 * view should be backed by a separate model.
 * </p>
 * <p>
 * The way clients interact with this class is as follows:
 * </p>
 * <p>
 * <pre>
 * <code>
 *  // Get a model and set it to a couple of clients with semantically similar function.
 *  HtcActivityChooserModel dataModel =
 *      HtcActivityChooserModel.get(context, "task_specific_history_file_name.xml");
 *
 *  HtcActivityChooserModelClient modelClient1 = getHtcActivityChooserModelClient1();
 *  modelClient1.setHtcActivityChooserModel(dataModel);
 *
 *  HtcActivityChooserModelClient modelClient2 = getHtcActivityChooserModelClient2();
 *  modelClient2.setHtcActivityChooserModel(dataModel);
 *
 *  // Set an intent to choose an activity for.
 *  dataModel.setIntent(intent);
 *  // Set a list of intents to choose an activity for.
 *  dataModel.setIntent(intents);
 * <pre>
 * <code>
 * </p>
 * <p>
 * <strong>Note:</strong> This class is thread safe.
 * </p>
 *
 * @hide
 */
public class HtcActivityChooserModel extends DataSetObservable {

    /**
     * Client that utilizes an {@link HtcActivityChooserModel}.
     */
    public interface HtcActivityChooserModelClient {

        /**
         * Sets the {@link HtcActivityChooserModel}.
         *
         * @param dataModel The model.
         */
        public void setHtcActivityChooserModel(HtcActivityChooserModel dataModel);
    }

    /**
     * Defines a sorter that is responsible for sorting the activities
     * based on the provided historical choices and an intent.
     */
    public interface ActivitySorter {

        /**
         * Sorts the <code>activities</code> in descending order of relevance
         * based on previous history and an intent.
         *
         * @param intent The list of {@link Intent}.
         * @param activities Activities to be sorted.
         * @param historicalRecords Historical records.
         */
        // This cannot be done by a simple comparator since an Activity weight
        // is computed from history. Note that Activity implements Comparable.
        public void sort(List<Intent> intent, List<ActivityResolveInfo> activities,
                List<HistoricalRecord> historicalRecords);
    }

    /**
     * Listener for choosing an activity.
     */
    public interface OnChooseActivityListener {

        /**
         * Called when an activity has been chosen. The client can decide whether
         * an activity can be chosen and if so the caller of
         * {@link HtcActivityChooserModel#chooseActivity(int)} will receive and {@link Intent}
         * for launching it.
         * <p>
         * <strong>Note:</strong> Modifying the intent is not permitted and
         *     any changes to the latter will be ignored.
         * </p>
         *
         * @param host The listener's host model.
         * @param intent The intent for launching the chosen activity.
         * @return Whether the intent is handled and should not be delivered to clients.
         *
         * @see HtcActivityChooserModel#chooseActivity(int)
         */
        public boolean onChooseActivity(HtcActivityChooserModel host, Intent intent);
    }

    /**
     * Flag for selecting debug mode.
     */
    private static final boolean DEBUG = false;

    /**
     * Tag used for logging.
     */
    private static final String LOG_TAG = HtcActivityChooserModel.class.getSimpleName();

    /**
     * The root tag in the history file.
     */
    private static final String TAG_HISTORICAL_RECORDS = "historical-records";

    /**
     * The tag for a record in the history file.
     */
    private static final String TAG_HISTORICAL_RECORD = "historical-record";

    /**
     * Attribute for the activity.
     */
    private static final String ATTRIBUTE_ACTIVITY = "activity";

    /**
     * Attribute for the choice time.
     */
    private static final String ATTRIBUTE_TIME = "time";

    /**
     * Attribute for the choice weight.
     */
    private static final String ATTRIBUTE_WEIGHT = "weight";

    /**
     * The default name of the choice history file.
     */
    public static final String DEFAULT_HISTORY_FILE_NAME =
        "activity_choser_model_history.xml";

    /**
     * The default maximal length of the choice history.
     */
    public static final int DEFAULT_HISTORY_MAX_LENGTH = 50;

    /**
     * The amount with which to inflate a chosen activity when set as default.
     */
    private static final int DEFAULT_ACTIVITY_INFLATION = 5;

    /**
     * Default weight for a choice record.
     */
    private static final float DEFAULT_HISTORICAL_RECORD_WEIGHT = 1.0f;

    /**
     * The extension of the history file.
     */
    private static final String HISTORY_FILE_EXTENSION = ".xml";

    /**
     * An invalid item index.
     */
    private static final int INVALID_INDEX = -1;

    /**
     * Lock to guard the model registry.
     */
    private static final Object sRegistryLock = new Object();

    /**
     * This the registry for data models.
     */
    private static final Map<String, HtcActivityChooserModel> sDataModelRegistry =
        new HashMap<String, HtcActivityChooserModel>();

    /**
     * Lock for synchronizing on this instance.
     */
    private final Object mInstanceLock = new Object();

    /**
     * Map from activity to matched intent(s)
     */
    private final Map<String, List<Intent>> mActivity2Intents = new HashMap<String, List<Intent>>();

    /**
     * List of activities that can handle the current intent.
     */
    private final List<ActivityResolveInfo> mActivities = new ArrayList<ActivityResolveInfo>();

    /**
     * List with historical choice records.
     */
    private final List<HistoricalRecord> mHistoricalRecords = new ArrayList<HistoricalRecord>();

    /**
     * Monitor for added and removed packages.
     */
    // [CC] paul.wy_wang, 20131021, Remove for UI static library.
    //private final PackageMonitor mPackageMonitor = new DataModelPackageMonitor();

    /**
     * Context for accessing resources.
     */
    private final Context mContext;

    /**
     * The name of the history file that backs this model.
     */
    private final String mHistoryFileName;

    /**
     * The intent for which a activity is being chosen.
     */
    private List<Intent> mIntents = new ArrayList<Intent>(); // [HTC] by paul.wy_wang, 2012/07/16, for HtcShareVia

    /**
     * The sorter for ordering activities based on intent and past choices.
     */
    private ActivitySorter mActivitySorter = new DefaultSorter();

    /**
     * The maximal length of the choice history.
     */
    private int mHistoryMaxSize = DEFAULT_HISTORY_MAX_LENGTH;

    /**
     * Flag whether choice history can be read. In general many clients can
     * share the same data model and {@link #readHistoricalDataIfNeeded()} may be called
     * by arbitrary of them any number of times. Therefore, this class guarantees
     * that the very first read succeeds and subsequent reads can be performed
     * only after a call to {@link #persistHistoricalDataIfNeeded()} followed by change
     * of the share records.
     */
    private boolean mCanReadHistoricalData = true;

    /**
     * Flag whether the choice history was read. This is used to enforce that
     * before calling {@link #persistHistoricalDataIfNeeded()} a call to
     * {@link #persistHistoricalDataIfNeeded()} has been made. This aims to avoid a
     * scenario in which a choice history file exits, it is not read yet and
     * it is overwritten. Note that always all historical records are read in
     * full and the file is rewritten. This is necessary since we need to
     * purge old records that are outside of the sliding window of past choices.
     */
    private boolean mReadShareHistoryCalled = false;

    /**
     * Flag whether the choice records have changed. In general many clients can
     * share the same data model and {@link #persistHistoricalDataIfNeeded()} may be called
     * by arbitrary of them any number of times. Therefore, this class guarantees
     * that choice history will be persisted only if it has changed.
     */
    private boolean mHistoricalRecordsChanged = true;

    /**
     * Flag whether to reload the activities for the current intent.
     */
    private boolean mReloadActivities = false;

    /**
     * Policy for controlling how the model handles chosen activities.
     */
    private OnChooseActivityListener mActivityChoserModelPolicy;

    // [HTC] begin by paul.wy_wang, 2012/07/16, for HtcShareVia
    /**
     * The package names allowed to appear in the list.
     */
    private HashSet<String> mAllows = new HashSet<String>();

    /**
     * The package names used to exclude from the list.
     */
    private HashSet<String> mExcludes = new HashSet<String>();

    /**
     * Handler for scheduling work on client thread.
     */
    private final Handler mHandler = new Handler();

    /**
     * Wildcard.
     */
    private static final String WILDCARD = "*";

    /**
     * Whether the query result is based on package name.
     */
    private boolean mUsePackageName = false;

    /**
     * Customization tag name in ACC.
     */
    private static final String CUSTOMIZATION_READER_NAME = "HTCCommonControl";

    /**
     * Used to skip some items.
     */
    private static final String IGNORE_SYMBOL = ";";
    // [HTC] end

    /**
     * Gets the data model backed by the contents of the provided file with historical data.
     * Note that only one data model is backed by a given file, thus multiple calls with
     * the same file name will return the same model instance. If no such instance is present
     * it is created.
     * <p>
     * <strong>Note:</strong> To use the default historical data file clients should explicitly
     * pass as file name {@link #DEFAULT_HISTORY_FILE_NAME}. If no persistence of the choice
     * history is desired clients should pass <code>null</code> for the file name. In such
     * case a new model is returned for each invocation.
     * </p>
     *
     * <p>
     * <strong>Always use difference historical data files for semantically different actions.
     * For example, sharing is different from importing.</strong>
     * </p>
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param historyFileName File name with choice history, <code>null</code>
     *        if the model should not be backed by a file. In this case the activities
     *        will be ordered only by data from the current session.
     *
     * @return The model.
     */
    public static HtcActivityChooserModel get(Context context, String historyFileName) {
        synchronized (sRegistryLock) {
            HtcActivityChooserModel dataModel = sDataModelRegistry.get(historyFileName);
            if (dataModel == null) {
                dataModel = new HtcActivityChooserModel(context, historyFileName);
                sDataModelRegistry.put(historyFileName, dataModel);
            }
            dataModel.setAllowedPackages(null);
            dataModel.setExcludedPackages(null);
            return dataModel;
        }
    }

    /**
     * [HTC] Gets the data model backed by the contents of the provided file with historical data.
     * Note that only one data model is backed by a given file, thus multiple calls with
     * the same file name will return the same model instance. If no such instance is present
     * it is created.
     * <p>
     * <strong>Note:</strong> To use the default historical data file clients should explicitly
     * pass as file name {@link #DEFAULT_HISTORY_FILE_NAME}. If no persistence of the choice
     * history is desired clients should pass <code>null</code> for the file name. In such
     * case a new model is returned for each invocation.
     * </p>
     *
     * <p>
     * <strong>Always use difference historical data files for semantically different actions.
     * For example, sharing is different from importing.</strong>
     * </p>
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param historyFileName File name with choice history, <code>null</code>
     *        if the model should not be backed by a file. In this case the activities
     *        will be ordered only by data from the current session.
     * @param observer Observer listener which is called when data set is changed.
     *
     * @return The model.
     */
    public static HtcActivityChooserModel get(Context context, String historyFileName, DataSetObserver observer) {
        synchronized (sRegistryLock) {
            HtcActivityChooserModel dataModel = sDataModelRegistry.get(historyFileName);
            if (dataModel == null) {
                dataModel = new HtcActivityChooserModel(context, historyFileName);
                sDataModelRegistry.put(historyFileName, dataModel);
            }
            dataModel.setAllowedPackages(null);
            dataModel.setExcludedPackages(null);
            if (observer != null) dataModel.registerObserver(observer);
            return dataModel;
        }
    }

    /**
     * Creates a new instance.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param historyFileName The history XML file.
     */
    private HtcActivityChooserModel(Context context, String historyFileName) {
        mContext = context.getApplicationContext();
        if (!TextUtils.isEmpty(historyFileName)
                && !historyFileName.endsWith(HISTORY_FILE_EXTENSION)) {
            mHistoryFileName = historyFileName + HISTORY_FILE_EXTENSION;
        } else {
            mHistoryFileName = historyFileName;
        }
        // [CC] paul.wy_wang, 20131021, Remove for UI static library.
        //mPackageMonitor.register(mContext, null, true);
    }

    /**
     * Sets an intent for which to choose a activity.
     * <p>
     * <strong>Note:</strong> Clients must set only semantically similar
     * intents for each data model.
     * <p>
     *
     * @param intent The intent.
     */
    public void setIntent(Intent intent) {
        synchronized (mInstanceLock) {
            mIntents.clear();
            if (intent != null) {
                mIntents.add(intent);
            }
            mReloadActivities = true;
            ensureConsistentState();
        }
    }

    /**
     * [HTC] Sets a list of intents for which to choose a activity.
     * <p>
     * <strong>Note:</strong> Clients must set only semantically similar
     * intents for each data model.
     * <p>
     *
     * @param intent The list of intents.
     */
    public void setIntent(List<Intent> intent) {
        synchronized (mInstanceLock) {
            mIntents.clear();
            if (intent != null && intent.size() > 0) {
                for (Intent i : intent) {
                    mIntents.add(i);
                }
            }
            mReloadActivities = true;
            ensureConsistentState();
        }
    }

    /**
     * Gets the intent for which a activity is being chosen.
     *
     * @return The intent.
     */
    public Intent getIntent() {
        synchronized (mInstanceLock) {
            // [HTC] begin by paul.wy_wang, 2012/07/16, for HtcShareVia
            if (mIntents.size() == 1) {
                // single intent
                return mIntents.get(0);
            } else {
                // multiple intents
                return null;
            }
            // [HTC] end
        }
    }

    private void linkRInfo2Intent(ResolveInfo resolveInfo, Intent intent) {
        // maintain the relationship between resolveInfo and intent
        // so that I can find out intent(s) by rInfo later
        String key = resolveInfo.activityInfo.packageName + "/" + resolveInfo.activityInfo.name;

        List<Intent> intents = mActivity2Intents.get(key);
        if (null == intents) {
            intents = new ArrayList<Intent>();
            mActivity2Intents.put(key, intents);
        }
        intents.add(intent);
    }

    /**
     * get matched intents for an activity
     * @param resolveInfo the resolveInfo user chose
     * @return all matched intents
     */
    List<Intent> getMatchedIntents(ResolveInfo resolveInfo) {
        String key = resolveInfo.activityInfo.packageName + "/" + resolveInfo.activityInfo.name;
        return mActivity2Intents.get(key);
    }

    /**
     * get if query By package
     * @return mUsePackageName
     */
    boolean queryByPackage() {
        return mUsePackageName;
    }

    /**
     * Get the intent list
     * ps. do not modify the returned object!
     * @return the intent list
     */
    List<Intent> getIntents() {
        return mIntents;
    }

    /**
     * Get allowed packages' names
     * ps. do not modify the returned object!
     * @return allowed packages' names
     */
    Set<String> getAllowed() {
        return mAllows;
    }

    /**
     * Get blocked packages' names
     * ps. do not modify the returned object!
     * @return blocked packages' names
     */
    Set<String> getBlocked() {
        return mExcludes;
    }

    /**
     * set parameters.
     * this will not update results automatically!
     * remember to call setIntents immediately to trigger update
     * @param allowed set of allowed packages
     * @param blocked set of blocked packages
     * @param queryByPackage queryByPackage or not
     */
    void setParametersLazy(Set<String> allowed, Set<String> blocked, boolean queryByPackage) {
        synchronized (mInstanceLock) {
            mUsePackageName = queryByPackage;

            mAllows.clear();
            if (null != allowed) mAllows.addAll(allowed);

            mExcludes.clear();
            if (null != blocked) mExcludes.addAll(blocked);
        }
    }

    /**
     * singleton get instance method.
     * which do not reset blocked and allowed.
     *
     * @param context context
     * @param historyFileName the file to store history data
     * @return an instance of HtcActivityChooserModel
     */
    static HtcActivityChooserModel getLazy(Context context, String historyFileName) {
        synchronized (sRegistryLock) {
            HtcActivityChooserModel dataModel = sDataModelRegistry.get(historyFileName);
            if (dataModel == null) {
                dataModel = new HtcActivityChooserModel(context, historyFileName);
                sDataModelRegistry.put(historyFileName, dataModel);
            }
            return dataModel;
        }
    }

    /**
     * Gets the number of activities that can handle the intent.
     *
     * @return The activity count.
     *
     * @see #setIntent(Intent)
     */
    public int getActivityCount() {
        synchronized (mInstanceLock) {
            ensureConsistentState();
            return mActivities.size();
        }
    }

    /**
     * Gets an activity at a given index.
     *
     * @return The activity.
     *
     * @see ActivityResolveInfo
     * @see #setIntent(Intent)
     */
    public ResolveInfo getActivity(int index) {
        synchronized (mInstanceLock) {
            ensureConsistentState();
            return mActivities.get(index).resolveInfo;
        }
    }

    /**
     * Gets the index of a the given activity.
     *
     * @param activity The activity index.
     *
     * @return The index if found, -1 otherwise.
     */
    public int getActivityIndex(ResolveInfo activity) {
        synchronized (mInstanceLock) {
            ensureConsistentState();
            List<ActivityResolveInfo> activities = mActivities;
            final int activityCount = activities.size();
            for (int i = 0; i < activityCount; i++) {
                ActivityResolveInfo currentActivity = activities.get(i);
                if (currentActivity.resolveInfo == activity) {
                    return i;
                }
            }
            return INVALID_INDEX;
        }
    }

    /**
     * Chooses a activity to handle the current intent. This will result in
     * adding a historical record for that action and construct intent with
     * its component name set such that it can be immediately started by the
     * client.
     * <p>
     * <strong>Note:</strong> By calling this method the client guarantees
     * that the returned intent will be started. This intent is returned to
     * the client solely to let additional customization before the start.
     * </p>
     *
     * @return An {@link Intent} for launching the activity or null if the
     *         policy has consumed the intent or there is not current intent
     *         set via {@link #setIntent(Intent)}.
     *
     * @see HistoricalRecord
     * @see OnChooseActivityListener
     */
    public Intent chooseActivity(int index) {
        synchronized (mInstanceLock) {
            if (mIntents.isEmpty()) {
                return null;
            }

            ensureConsistentState();

            ActivityResolveInfo chosenActivity = mActivities.get(index);

            final ComponentName chosenName = new ComponentName(
                    chosenActivity.resolveInfo.activityInfo.packageName,
                    chosenActivity.resolveInfo.activityInfo.name);

            Intent choiceIntent = null;
            if (mIntents.size() == 1) {
                choiceIntent = new Intent(mIntents.get(0));
                choiceIntent.setComponent(chosenName);
            }

            // [HTC] Do not support policy for multi-intent case.
            if (mActivityChoserModelPolicy != null && choiceIntent != null) {
                // Do not allow the policy to change the intent.
                Intent choiceIntentCopy = new Intent(choiceIntent);
                final boolean handled = mActivityChoserModelPolicy.onChooseActivity(this,
                        choiceIntentCopy);
                if (handled) {
                    return null;
                }
            }

            // [HTC-workaround] The list items may be swapped before dialog or pop-up
            // window is dismissed, so a runnable thread is posted to update its count.
            mHandler.post(new Runnable() {
                public void run() {
                    synchronized (mInstanceLock) {
                        HistoricalRecord historicalRecord = new HistoricalRecord(chosenName,
                                System.currentTimeMillis(), DEFAULT_HISTORICAL_RECORD_WEIGHT);
                        addHisoricalRecord(historicalRecord);
                    }
                }
            });

            // [HTC] Return an intent for single intent case.
            // [HTC] Return null for multiple intent case.
            return choiceIntent;
        }
    }

    /**
     * Sets the listener for choosing an activity.
     *
     * @param listener The listener.
     */
    public void setOnChooseActivityListener(OnChooseActivityListener listener) {
        synchronized (mInstanceLock) {
            mActivityChoserModelPolicy = listener;
        }
    }

    /**
     * Gets the default activity, The default activity is defined as the one
     * with highest rank i.e. the first one in the list of activities that can
     * handle the intent.
     *
     * @return The default activity, <code>null</code> id not activities.
     *
     * @see #getActivity(int)
     */
    public ResolveInfo getDefaultActivity() {
        synchronized (mInstanceLock) {
            ensureConsistentState();
            if (!mActivities.isEmpty()) {
                return mActivities.get(0).resolveInfo;
            }
        }
        return null;
    }

    /**
     * Sets the default activity. The default activity is set by adding a
     * historical record with weight high enough that this activity will
     * become the highest ranked. Such a strategy guarantees that the default
     * will eventually change if not used. Also the weight of the record for
     * setting a default is inflated with a constant amount to guarantee that
     * it will stay as default for awhile.
     *
     * @param index The index of the activity to set as default.
     */
    public void setDefaultActivity(int index) {
        synchronized (mInstanceLock) {
            ensureConsistentState();

            ActivityResolveInfo newDefaultActivity = mActivities.get(index);
            ActivityResolveInfo oldDefaultActivity = mActivities.get(0);

            final float weight;
            if (oldDefaultActivity != null) {
                // Add a record with weight enough to boost the chosen at the top.
                weight = oldDefaultActivity.weight - newDefaultActivity.weight
                    + DEFAULT_ACTIVITY_INFLATION;
            } else {
                weight = DEFAULT_HISTORICAL_RECORD_WEIGHT;
            }

            ComponentName defaultName = new ComponentName(
                    newDefaultActivity.resolveInfo.activityInfo.packageName,
                    newDefaultActivity.resolveInfo.activityInfo.name);
            HistoricalRecord historicalRecord = new HistoricalRecord(defaultName,
                    System.currentTimeMillis(), weight);
            addHisoricalRecord(historicalRecord);
        }
    }

    /**
     * Persists the history data to the backing file if the latter
     * was provided. Calling this method before a call to {@link #readHistoricalDataIfNeeded()}
     * throws an exception. Calling this method more than one without choosing an
     * activity has not effect.
     *
     * @throws IllegalStateException If this method is called before a call to
     *         {@link #readHistoricalDataIfNeeded()}.
     */
    private void persistHistoricalDataIfNeeded() {
        if (!mReadShareHistoryCalled) {
            throw new IllegalStateException("No preceding call to #readHistoricalData");
        }
        if (!mHistoricalRecordsChanged) {
            return;
        }
        mHistoricalRecordsChanged = false;
        if (!TextUtils.isEmpty(mHistoryFileName)) {
            new PersistHistoryAsyncTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                    new ArrayList<HistoricalRecord>(mHistoricalRecords), mHistoryFileName);
        }
    }

    /**
     * Sets the sorter for ordering activities based on historical data and an intent.
     *
     * @param activitySorter The sorter.
     *
     * @see ActivitySorter
     */
    public void setActivitySorter(ActivitySorter activitySorter) {
        synchronized (mInstanceLock) {
            if (mActivitySorter == activitySorter) {
                return;
            }
            mActivitySorter = activitySorter;
            if (sortActivitiesIfNeeded()) {
                notifyChanged();
            }
        }
    }

    /**
     * Sets the maximal size of the historical data. Defaults to
     * {@link #DEFAULT_HISTORY_MAX_LENGTH}
     * <p>
     *   <strong>Note:</strong> Setting this property will immediately
     *   enforce the specified max history size by dropping enough old
     *   historical records to enforce the desired size. Thus, any
     *   records that exceed the history size will be discarded and
     *   irreversibly lost.
     * </p>
     *
     * @param historyMaxSize The max history size.
     */
    public void setHistoryMaxSize(int historyMaxSize) {
        synchronized (mInstanceLock) {
            if (mHistoryMaxSize == historyMaxSize) {
                return;
            }
            mHistoryMaxSize = historyMaxSize;
            pruneExcessiveHistoricalRecordsIfNeeded();
            if (sortActivitiesIfNeeded()) {
                notifyChanged();
            }
        }
    }

    /**
     * Gets the history max size.
     *
     * @return The history max size.
     */
    public int getHistoryMaxSize() {
        synchronized (mInstanceLock) {
            return mHistoryMaxSize;
        }
    }

    /**
     * Gets the history size.
     *
     * @return The history size.
     */
    public int getHistorySize() {
        synchronized (mInstanceLock) {
            ensureConsistentState();
            return mHistoricalRecords.size();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        // [CC] paul.wy_wang, 20131021, Remove for UI static library.
        //mPackageMonitor.unregister();
    }

    /**
     * Ensures the model is in a consistent state which is the
     * activities for the current intent have been loaded, the
     * most recent history has been read, and the activities
     * are sorted.
     */
    private void ensureConsistentState() {
        boolean stateChanged = loadActivitiesIfNeeded();
        stateChanged |= readHistoricalDataIfNeeded();
        pruneExcessiveHistoricalRecordsIfNeeded();
        if (stateChanged) {
            sortActivitiesIfNeeded();
            notifyChanged();
        }
    }

    /**
     * Sorts the activities if necessary which is if there is a
     * sorter, there are some activities to sort, and there is some
     * historical data.
     *
     * @return Whether sorting was performed.
     */
    private boolean sortActivitiesIfNeeded() {
        if (mActivitySorter != null && !mIntents.isEmpty()
                && !mActivities.isEmpty() && !mHistoricalRecords.isEmpty()) {
            mActivitySorter.sort(mIntents, mActivities,
                    Collections.unmodifiableList(mHistoricalRecords));
            return true;
        }
        return false;
    }

    /**
     * Loads the activities for the current intent if needed which is
     * if they are not already loaded for the current intent.
     *
     * @return Whether loading was performed.
     */
    private boolean loadActivitiesIfNeeded() {
        if (mReloadActivities && !mIntents.isEmpty()) {
            mReloadActivities = false;
            mActivities.clear();
            mActivity2Intents.clear();
            // [HTC] Support multi-intent, allowed list and excluded list.
            HashSet<CharSequence> packageSet = new HashSet<CharSequence>();
            HashSet<CharSequence> nameSet = new HashSet<CharSequence>();
            int packageSetSize = 0;
            int nameSetSize = 0;
            for (Intent intent : mIntents) {
                List<ResolveInfo> resolveInfos = mContext.getPackageManager()
                        .queryIntentActivities(intent, 0);
                final int resolveInfoCount = resolveInfos.size();
                for (int i = 0; i < resolveInfoCount; i++) {
                    ResolveInfo resolveInfo = resolveInfos.get(i);
                    String packageName = resolveInfo.activityInfo.packageName;
                    String name = resolveInfo.activityInfo.name;
                    // Check allowed package list.
                    if (mAllows != null && !mAllows.isEmpty() && !mAllows.contains(packageName)) {
                        continue;
                    }
                    // Check excluded package list.
                    if (mExcludes != null && !mExcludes.isEmpty() && mExcludes.contains(packageName)) {
                        continue;
                    }
                    // Reduce the activities with the same package name.
                    if (mUsePackageName) {
                        packageSet.add(packageName);
                        if (packageSetSize < packageSet.size()) {
                             mActivities.add(new ActivityResolveInfo(resolveInfo));
                             packageSetSize = packageSet.size();
                        }
                    } else {
                        nameSet.add(name);
                        if (nameSetSize < nameSet.size()) {
                            mActivities.add(new ActivityResolveInfo(resolveInfo));
                            nameSetSize = nameSet.size();
                        }
                    }
                    linkRInfo2Intent(resolveInfo, intent);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Reads the historical data if necessary which is it has
     * changed, there is a history file, and there is not persist
     * in progress.
     *
     * @return Whether reading was performed.
     */
    private boolean readHistoricalDataIfNeeded() {
        if (mCanReadHistoricalData && mHistoricalRecordsChanged &&
                !TextUtils.isEmpty(mHistoryFileName)) {
            mCanReadHistoricalData = false;
            mReadShareHistoryCalled = true;
            readHistoricalDataImpl();
            return true;
        }
        return false;
    }

    /**
     * Adds a historical record.
     *
     * @param historicalRecord The record to add.
     * @return True if the record was added.
     */
    private boolean addHisoricalRecord(HistoricalRecord historicalRecord) {
        final boolean added = mHistoricalRecords.add(historicalRecord);
        if (added) {
            mHistoricalRecordsChanged = true;
            pruneExcessiveHistoricalRecordsIfNeeded();
            persistHistoricalDataIfNeeded();
            sortActivitiesIfNeeded();
            notifyChanged();
        }
        return added;
    }

    /**
     * Prunes older excessive records to guarantee maxHistorySize.
     */
    private void pruneExcessiveHistoricalRecordsIfNeeded() {
        final int pruneCount = mHistoricalRecords.size() - mHistoryMaxSize;
        if (pruneCount <= 0) {
            return;
        }
        mHistoricalRecordsChanged = true;
        for (int i = 0; i < pruneCount; i++) {
            HistoricalRecord prunedRecord = mHistoricalRecords.remove(0);
            if (DEBUG) {
                Log.i(LOG_TAG, "Pruned: " + prunedRecord);
            }
        }
    }

    /**
     * Represents a record in the history.
     */
    public final static class HistoricalRecord {

        /**
         * The activity name.
         */
        public final ComponentName activity;

        /**
         * The choice time.
         */
        public final long time;

        /**
         * The record weight.
         */
        public final float weight;

        /**
         * Creates a new instance.
         *
         * @param activityName The activity component name flattened to string.
         * @param time The time the activity was chosen.
         * @param weight The weight of the record.
         */
        public HistoricalRecord(String activityName, long time, float weight) {
            this(ComponentName.unflattenFromString(activityName), time, weight);
        }

        /**
         * Creates a new instance.
         *
         * @param activityName The activity name.
         * @param time The time the activity was chosen.
         * @param weight The weight of the record.
         */
        public HistoricalRecord(ComponentName activityName, long time, float weight) {
            this.activity = activityName;
            this.time = time;
            this.weight = weight;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((activity == null) ? 0 : activity.hashCode());
            result = prime * result + (int) (time ^ (time >>> 32));
            result = prime * result + Float.floatToIntBits(weight);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            HistoricalRecord other = (HistoricalRecord) obj;
            if (activity == null) {
                if (other.activity != null) {
                    return false;
                }
            } else if (!activity.equals(other.activity)) {
                return false;
            }
            if (time != other.time) {
                return false;
            }
            if (Float.floatToIntBits(weight) != Float.floatToIntBits(other.weight)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            builder.append("; activity:").append(activity);
            builder.append("; time:").append(time);
            builder.append("; weight:").append(new BigDecimal(weight));
            builder.append("]");
            return builder.toString();
        }
    }

    /**
     * Represents an activity.
     */
    public final class ActivityResolveInfo implements Comparable<ActivityResolveInfo> {

        /**
         * The {@link ResolveInfo} of the activity.
         */
        public final ResolveInfo resolveInfo;

        /**
         * Weight of the activity. Useful for sorting.
         */
        public float weight;

        /**
         * Creates a new instance.
         *
         * @param resolveInfo activity {@link ResolveInfo}.
         */
        public ActivityResolveInfo(ResolveInfo resolveInfo) {
            this.resolveInfo = resolveInfo;
        }

        @Override
        public int hashCode() {
            return 31 + Float.floatToIntBits(weight);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ActivityResolveInfo other = (ActivityResolveInfo) obj;
            if (Float.floatToIntBits(weight) != Float.floatToIntBits(other.weight)) {
                return false;
            }
            return true;
        }

        public int compareTo(ActivityResolveInfo another) {
            // [HTC] begin by paul.wy_wang, 2012/07/16, for HtcShareVia
            // Sort by weight and alphabet.
            int weightOrder = Float.floatToIntBits(another.weight)
                    - Float.floatToIntBits(weight);
            if (0 == weightOrder) {
                String label_another = another.resolveInfo
                        .loadLabel(mContext.getPackageManager()).toString();
                String label_this = resolveInfo
                        .loadLabel(mContext.getPackageManager()).toString();
                return label_this.compareTo(label_another);
            } else {
                return weightOrder;
            }
            //return  Float.floatToIntBits(another.weight) - Float.floatToIntBits(weight);
            // [HTC] end
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            builder.append("resolveInfo:").append(resolveInfo.toString());
            builder.append("; weight:").append(new BigDecimal(weight));
            builder.append("]");
            return builder.toString();
        }
    }

    /**
     * Default activity sorter implementation.
     */
    private final static class DefaultSorter implements ActivitySorter {
        private static final float WEIGHT_DECAY_COEFFICIENT = 1.0f;//0.95f; // [HTC] by paul.wy_wang, 2012/07/16, for HtcShareVia

        // [HTC] begin by paul.wy_wang, 2013/03/26, for HtcShareVia
        /*
        private final Map<String, ActivityResolveInfo> mPackageNameToActivityMap =
            new HashMap<String, ActivityResolveInfo>();
         */
        private final Map<String, List<ActivityResolveInfo>> mPackageNameToActivityMap =
            new HashMap<String, List<ActivityResolveInfo>>();
        private final Map<String, ActivityResolveInfo> mNameToActivityMap =
            new HashMap<String, ActivityResolveInfo>();
        // [HTC] end by paul.wy_wang, 2013/03/26, for HtcShareVia

        public void sort(List<Intent> intent, List<ActivityResolveInfo> activities,
                List<HistoricalRecord> historicalRecords) {
            // [HTC] begin by paul.wy_wang, 2013/03/26, for HtcShareVia
            // Because one intent will result in multiple activities with the
            // same package name (ex. WeChat), using package name for sorting is
            // not enough.

            /*
            Map<String, ActivityResolveInfo> packageNameToActivityMap =
                mPackageNameToActivityMap;
            packageNameToActivityMap.clear();

            final int activityCount = activities.size();
            for (int i = 0; i < activityCount; i++) {
                ActivityResolveInfo activity = activities.get(i);
                activity.weight = 0.0f;
                String packageName = activity.resolveInfo.activityInfo.packageName;
                packageNameToActivityMap.put(packageName, activity);
            }

            final int lastShareIndex = historicalRecords.size() - 1;
            float nextRecordWeight = 1;
            for (int i = lastShareIndex; i >= 0; i--) {
                HistoricalRecord historicalRecord = historicalRecords.get(i);
                String packageName = historicalRecord.activity.getPackageName();
                ActivityResolveInfo activity = packageNameToActivityMap.get(packageName);
                if (activity != null) {
                    activity.weight += historicalRecord.weight * nextRecordWeight;
                    nextRecordWeight = nextRecordWeight * WEIGHT_DECAY_COEFFICIENT;
                }
            }
            */

            Map<String, List<ActivityResolveInfo>> packageNameToActivityMap =
                    mPackageNameToActivityMap;
            packageNameToActivityMap.clear();

            Map<String, ActivityResolveInfo> nameToActivityMap =
                    mNameToActivityMap;
            nameToActivityMap.clear();

            final int activityCount = activities.size();
            for (int i = 0; i < activityCount; i++) {
                ActivityResolveInfo activity = activities.get(i);
                activity.weight = 0.0f;
                String packageName = activity.resolveInfo.activityInfo.packageName;
                List<ActivityResolveInfo> list = packageNameToActivityMap.get(packageName);
                if (list == null) {
                    list = new ArrayList<ActivityResolveInfo>();
                    packageNameToActivityMap.put(packageName, list);
                }
                list.add(activity);
                String name = activity.resolveInfo.activityInfo.name;
                nameToActivityMap.put(name, activity);
            }

            final int lastShareIndex = historicalRecords.size() - 1;
            for (int i = lastShareIndex; i >= 0; i--) {
                HistoricalRecord historicalRecord = historicalRecords.get(i);
                String name = historicalRecord.activity.getClassName();
                if (name.equals(HtcActivityChooserModel.WILDCARD)) {
                    // Add weight into activities with the same package name.
                    String packageName = historicalRecord.activity.getPackageName();
                    List<ActivityResolveInfo> list = packageNameToActivityMap.get(packageName);
                    if (list != null) {
                        for (ActivityResolveInfo activity : list) {
                            activity.weight += historicalRecord.weight;
                        }
                    }
                } else {
                    // Add weight into one activity.
                    ActivityResolveInfo activity = nameToActivityMap.get(name);
                    if (activity != null) {
                        activity.weight += historicalRecord.weight;
                    }
                }
            }
            // [HTC] end by paul.wy_wang, 2013/03/26, for HtcShareVia

            Collections.sort(activities);

            if (DEBUG) {
                for (int i = 0; i < activityCount; i++) {
                    Log.i(LOG_TAG, "Sorted: " + activities.get(i));
                }
            }
        }
    }

    /**
     * Command for reading the historical records from a file off the UI thread.
     */
    private void readHistoricalDataImpl() {
        FileInputStream fis = null;
        try {
            fis = mContext.openFileInput(mHistoryFileName);
        } catch (FileNotFoundException fnfe) {
            if (DEBUG) {
                Log.i(LOG_TAG, "Could not open historical records file: " + mHistoryFileName);
            }
            // [HTC] begin by paul.wy_wang, 2012/07/16, for HtcShareVia
            mHandler.post(new Runnable() {
                public void run() {
                    synchronized (mInstanceLock) {
                        setDefaultWeight();
                    }
                }
            });
            // [HTC] end
            return;
        }
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, null);

            int type = XmlPullParser.START_DOCUMENT;
            while (type != XmlPullParser.END_DOCUMENT && type != XmlPullParser.START_TAG) {
                type = parser.next();
            }

            if (!TAG_HISTORICAL_RECORDS.equals(parser.getName())) {
                throw new XmlPullParserException("Share records file does not start with "
                        + TAG_HISTORICAL_RECORDS + " tag.");
            }

            List<HistoricalRecord> historicalRecords = mHistoricalRecords;
            historicalRecords.clear();

            while (true) {
                type = parser.next();
                if (type == XmlPullParser.END_DOCUMENT) {
                    break;
                }
                if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                    continue;
                }
                String nodeName = parser.getName();
                if (!TAG_HISTORICAL_RECORD.equals(nodeName)) {
                    throw new XmlPullParserException("Share records file not well-formed.");
                }

                String activity = parser.getAttributeValue(null, ATTRIBUTE_ACTIVITY);
                final long time =
                    Long.parseLong(parser.getAttributeValue(null, ATTRIBUTE_TIME));
                final float weight =
                    Float.parseFloat(parser.getAttributeValue(null, ATTRIBUTE_WEIGHT));
                HistoricalRecord readRecord = new HistoricalRecord(activity, time, weight);
                historicalRecords.add(readRecord);

                if (DEBUG) {
                    Log.i(LOG_TAG, "Read " + readRecord.toString());
                }
            }

            if (DEBUG) {
                Log.i(LOG_TAG, "Read " + historicalRecords.size() + " historical records.");
            }
        } catch (XmlPullParserException xppe) {
            Log.e(LOG_TAG, "Error reading historical recrod file: " + mHistoryFileName, xppe);
        } catch (IOException ioe) {
            Log.e(LOG_TAG, "Error reading historical recrod file: " + mHistoryFileName, ioe);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ioe) {
                    /* ignore */
                }
            }
        }
    }

    /**
     * Command for persisting the historical records to a file off the UI thread.
     */
    private final class PersistHistoryAsyncTask extends AsyncTask<Object, Void, Void> {

        @Override
        @SuppressWarnings("unchecked")
        public Void doInBackground(Object... args) {
            List<HistoricalRecord> historicalRecords = (List<HistoricalRecord>) args[0];
            String hostoryFileName = (String) args[1];

            FileOutputStream fos = null;

            try {
                fos = mContext.openFileOutput(hostoryFileName, Context.MODE_PRIVATE);
            } catch (FileNotFoundException fnfe) {
                Log.e(LOG_TAG, "Error writing historical recrod file: " + hostoryFileName, fnfe);
                return null;
            }

            XmlSerializer serializer = Xml.newSerializer();

            try {
                serializer.setOutput(fos, null);
                serializer.startDocument("UTF-8", true);
                serializer.startTag(null, TAG_HISTORICAL_RECORDS);

                final int recordCount = historicalRecords.size();
                for (int i = 0; i < recordCount; i++) {
                    HistoricalRecord record = historicalRecords.remove(0);
                    serializer.startTag(null, TAG_HISTORICAL_RECORD);
                    serializer.attribute(null, ATTRIBUTE_ACTIVITY,
                            record.activity.flattenToString());
                    serializer.attribute(null, ATTRIBUTE_TIME, String.valueOf(record.time));
                    serializer.attribute(null, ATTRIBUTE_WEIGHT, String.valueOf(record.weight));
                    serializer.endTag(null, TAG_HISTORICAL_RECORD);
                    if (DEBUG) {
                        Log.i(LOG_TAG, "Wrote " + record.toString());
                    }
                }

                serializer.endTag(null, TAG_HISTORICAL_RECORDS);
                serializer.endDocument();

                if (DEBUG) {
                    Log.i(LOG_TAG, "Wrote " + recordCount + " historical records.");
                }
            } catch (IllegalArgumentException iae) {
                Log.e(LOG_TAG, "Error writing historical recrod file: " + mHistoryFileName, iae);
            } catch (IllegalStateException ise) {
                Log.e(LOG_TAG, "Error writing historical recrod file: " + mHistoryFileName, ise);
            } catch (IOException ioe) {
                Log.e(LOG_TAG, "Error writing historical recrod file: " + mHistoryFileName, ioe);
            } finally {
                mCanReadHistoricalData = true;
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        /* ignore */
                    }
                }
            }
            return null;
        }
    }

    /**
     * Keeps in sync the historical records and activities with the installed applications.
     */
    // [CC] paul.wy_wang, 20131021, Remove for UI static library.
    /*
    private final class DataModelPackageMonitor extends PackageMonitor {

        @Override
        public void onSomePackagesChanged() {
            // [HTC] begin by paul.wy_wang, 2012/07/16, for HtcShareVia
            synchronized (mInstanceLock) {
                mReloadActivities = true;
                ensureConsistentState();
            }
            //mReloadActivities = true;
            // [HTC] end
        }
    }
    */

    // [HTC] begin by paul.wy_wang, 2015/01/16, for HtcShareVia
    /**
     * The default order is stored in <PATH>/ACC/default.xml (ACC).
     * The customization is stored in <PATH>/CID/default.xml (SIE).
     * <PATH> could be (new) /custdata/customizeData or (old) /system/customize.
     */
    private void setDefaultWeight() {
        // Get the default order from ACC and put name and value into SparseArray.
        SparseArray<String> map = new SparseArray<String>();
        HtcWrapCustomizationManager mManager = new HtcWrapCustomizationManager();
        HtcWrapCustomizationReader mReader = mManager.getCustomizationReader(CUSTOMIZATION_READER_NAME,
                HtcWrapCustomizationManager.READER_TYPE_XML, false);
        if (mReader != null) {
            String[] order = mReader.readStringArray("htcsharevia_default_order", null);

            if (order != null) {
                int size = order.length;
                for (int i = 1; i < size; i++) {
                    if (!IGNORE_SYMBOL.equals(order[i])) {
                        map.put(i, order[i]);
                    }
                }
            } else {
                Log.e(LOG_TAG, "Default order is null!");
            }
        } else {
            Log.e(LOG_TAG, "Reader is null!");
        }

        // If empty, add one fake item into mHistoricalRecords to trigger sorting.
        if (map.size() == 0) {
            Log.w(LOG_TAG, "No default item!");
            map.put(1, HtcActivityChooserModel.WILDCARD);
        }

        // Set default weight
        setWeight(map);
    }

    /**
     * Set default weight for ShareVia
     */
    private void setWeight(SparseArray<String> map) {
        synchronized (mInstanceLock) {
            boolean added = false;
            long currentTime = System.currentTimeMillis();
            int maxKey = 0;
            int msize = map.size();
            for (int i = 0; i < msize; i++) {
                int key = map.keyAt(i);
                if (maxKey < key) {
                    maxKey = key;
                }
            }

            /**
             * The total number in the default customized history should be less
             * than DEFAULT_HISTORY_MAX_LENGTH (Sense50: 1+2+3+4+5+6+12 < 50).
             */
            for (int i = 1; i <= maxKey; i++) {
                for (int j = i; j <= maxKey; j++) {
                    String packageName = map.get(j); // 1-based database
                    if (packageName != null && packageName.length() != 0) {
                        ComponentName chosenName = new ComponentName(
                                packageName, HtcActivityChooserModel.WILDCARD);
                        HistoricalRecord historicalRecord = new HistoricalRecord(
                                chosenName, currentTime + i - 1,
                                DEFAULT_HISTORICAL_RECORD_WEIGHT);
                        added |= mHistoricalRecords.add(historicalRecord);
                    }
                }
            }

            if (added) {
                mHistoricalRecordsChanged = true;
                pruneExcessiveHistoricalRecordsIfNeeded();
                persistHistoricalDataIfNeeded();
                sortActivitiesIfNeeded();
                notifyChanged();
            }
        }
    }

    /**
     * Set the package names allowed to appear in the list.
     */
    public void setAllowedPackages(List<String> allows) {
        synchronized (mInstanceLock) {
            mAllows.clear();
            if (allows != null) {
                for (String s : allows) {
                    mAllows.add(s);
                }
            }
        }
    }

    /**
     * Set the package names used to exclude from the list.
     */
    public void setExcludedPackages(List<String> excludes) {
        synchronized (mInstanceLock) {
            mExcludes.clear();
            if (excludes != null) {
                for (String s : excludes) {
                    mExcludes.add(s);
                }
            }
        }
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
        synchronized (mInstanceLock) {
            mUsePackageName = enable;
            mReloadActivities = true;
            ensureConsistentState();
        }
    }
    // [HTC] end by paul.wy_wang, 2015/01/16, for HtcShareVia
}
