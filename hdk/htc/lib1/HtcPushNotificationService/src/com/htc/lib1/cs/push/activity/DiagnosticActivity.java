
package com.htc.lib1.cs.push.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.htc.lib1.cs.app.SelfLogActivity;
import com.htc.lib1.cs.push.PnsRecords;
import com.htc.lib1.cs.push.PnsRecords.EventRecord;
import com.htc.lib1.cs.push.PnsRecords.MessageRecord;
import com.htc.lib1.cs.push.R;

/**
 * Activity to show diagnostic information.
 * 
 * @author samael_wang@htc.com
 */
public class DiagnosticActivity extends SelfLogActivity {
    private static final int LIMIT_RECENT_RECORDS = 30;
    private static final int REG_ID_SANITIZE_LENGTH = 8;
    private ScheduledExecutorService mScheduledExecutor = Executors.newScheduledThreadPool(1);
    @SuppressLint("SimpleDateFormat")
    private DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
    private AsyncLoadPnsRecordsTask mLoadRecordsTask;
    private EventRecord[] mEventRecords;
    private MessageRecord[] mMessageRecords;
    private ArrayAdapter<String> mEventRecordsAdapter;
    private ArrayAdapter<String> mMessageRecordsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specific_activity_diagnostic);

        // Init adapters and lists.
        mEventRecordsAdapter = new ArrayAdapter<>(this, R.layout.specific_item_event_record);
        mMessageRecordsAdapter = new ArrayAdapter<>(this, R.layout.specific_item_event_record);
        ListView eventRecordsView = (ListView) findViewById(R.id.list_events);
        if (eventRecordsView != null)
            eventRecordsView.setAdapter(mEventRecordsAdapter);
        ListView messageRecordsView = (ListView) findViewById(R.id.list_messages);
        if (messageRecordsView != null)
            messageRecordsView.setAdapter(mMessageRecordsAdapter);

        // Start loading.
        mScheduledExecutor.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                runOnUiThread(new LoadDiagnosticInfoRunnable());
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoadRecordsTask.cancel(true);
        mScheduledExecutor.shutdown();
    }

    /**
     * Load diagnostic information.
     */
    private class LoadDiagnosticInfoRunnable implements Runnable {

        @Override
        public void run() {
            if (isResumedCompact()) {
                mLogger.verbose("Load / update diagnostic info.");

                try {
                    // Load registration info.
                    TextView regInfoView = (TextView) findViewById(R.id.txt_reg_info);
                    if (regInfoView != null) {
                        PnsRecords records = PnsRecords.get(DiagnosticActivity.this);
                        StringBuilder builder = new StringBuilder();

                        long regFails = records.getRegistrationFailCount();
                        long updateFails = records.getUpdateFailCount();
                        long nextReg = records.getNextRegistration();
                        long nextUpdate = records.getNextUpdate();
                        long nextUnreg = records.getNextUnregistration();
                        
                        builder.append("isRegistered: ")
                                .append(records.isRegistered())
                                .append('\n')
                                .append("regId: ")
                                .append(sanitizeRegId(records.getRegId()))
                                .append('\n')
                                .append("#regFailures: ")
                                .append(regFails)
                                .append('\n')
                                .append("#updateFailures: ")
                                .append(updateFails)
                                .append('\n')
                                .append("nextRegistration: ")
                                .append(nextReg == 0 ? 0 : mDateFormat.format(new Date(nextReg)))
                                .append('\n')
                                .append("nextUpdate: ")
                                .append(nextUpdate == 0 ? 0 : mDateFormat.format(new Date(
                                        nextUpdate)))
                                .append('\n')
                                .append("nextUnregistration: ")
                                .append(nextUnreg == 0 ? 0 : mDateFormat.format(new Date(
                                        nextUnreg)))
                                .append('\n')
                                .append("pushProvider: ").append(records.getPushProvider());
                        regInfoView.setText(builder.toString());
                    }

                    // Load events / messages.
                    mLoadRecordsTask = new AsyncLoadPnsRecordsTask();
                    mLoadRecordsTask.execute();
                } catch (Exception e) {
                    mLogger.warning(e);
                }
            }
        }
    }

    /**
     * Keep only the first {@value #REG_ID_SANITIZE_LENGTH} characters of
     * {@code regId}.
     */
    private String sanitizeRegId(String regId) {
        if (!TextUtils.isEmpty(regId) && regId.length() > REG_ID_SANITIZE_LENGTH) {
            return regId.substring(0, REG_ID_SANITIZE_LENGTH)
                    + regId.substring(REG_ID_SANITIZE_LENGTH).replaceAll(".", "*");
        }

        return regId;
    }

    /**
     * Update message / event records to list view.
     */
    private void updateRecordsView() {
        mEventRecordsAdapter.clear();
        mMessageRecordsAdapter.clear();

        // Update events.
        for (EventRecord r : mEventRecords) {
            if (r.success) {
                mEventRecordsAdapter.add(mDateFormat
                        .format(new Date(r.timestamp)) +
                        "\n" +
                        String.format(getString(R.string.fmt_success_event_record),
                                r.type, r.actionCause));
            } else {
                mEventRecordsAdapter.add(mDateFormat
                        .format(new Date(r.timestamp)) +
                        "\n" +
                        String.format(getString(R.string.fmt_failure_event_record),
                                r.type, r.actionCause,
                                TextUtils.isEmpty(r.resultCause) ?
                                        getString(R.string.txt_unknown_reason)
                                        : r.resultCause
                        ));
            }

        }
        mEventRecordsAdapter.notifyDataSetChanged();

        // Update messages.
        for (MessageRecord r : mMessageRecords) {
            mMessageRecordsAdapter.add(
                    mDateFormat.format(new Date(r.timestamp)) +
                            "\n" +
                            String.format(getString(R.string.fmt_message_record),
                                    r.msgId, r.appList));
        }
        mMessageRecordsAdapter.notifyDataSetChanged();
    }

    /**
     * Async task to load event / message records.
     * 
     * @author samael_wang@htc.com
     */
    private class AsyncLoadPnsRecordsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // Load events.
            mEventRecords = PnsRecords.get(DiagnosticActivity.this).getRecentEventRecords(
                    LIMIT_RECENT_RECORDS);

            // Load messages.
            mMessageRecords = PnsRecords.get(DiagnosticActivity.this).getRecentMessageRecords(
                    LIMIT_RECENT_RECORDS);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            updateRecordsView();
        }

    }

}
