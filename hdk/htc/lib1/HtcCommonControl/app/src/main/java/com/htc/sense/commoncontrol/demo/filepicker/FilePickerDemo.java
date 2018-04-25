package com.htc.sense.commoncontrol.demo.filepicker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

public class FilePickerDemo extends CommonDemoActivityBase implements RadioGroup.OnCheckedChangeListener {
    private RadioGroup mSelectionTypeGroup, mCloudEnabledGroup, mShareLinkEnabledGroup,
            mUsbConnectionGroup, mFolderPickerGroup, mFilePickerGroup;
    private int mViewTypeChoice, mSelectionTypeChoice;
    private boolean mGroupChoice = true;
    private int mModeTypeChoice; // hank+
    private Button launch;
    private TextView mOutput;
    private String KEY_USB_CONNECTION_MODE = "UsbConnectionMode";
    private int LIST_VIEW_TYPE_ID;
    private int GRID_VIEW_TYPE_ID;
    private int FOLDER_VIEW_TYPE_ID;
    private int NO_GROUP_ID;
    private int GROUP_BY_PATH_ID;
    private int SINGLE_SELECTION_ID;
    private int MULTIPLE_SELECTION_ID;
    private int MODE_TYPE_OF_FOLDER_VIEW_ID;// hank++
    private static final int FILEPICKER_REQUEST_CODE = 10;
    private boolean mCloudFunctionEnabled = true;
    private boolean mShareLinkEnabled = false;
    private int mUsbConnectionViewMode = -1;
    private boolean mFolderPickerModeEnabled = false;
    private boolean mFilePickerModeEnabled = true;
    private int mSortType = 0;
    private int mSortOrder = 0;
    private static final String KEY_SINGLE_CLOUD_SERVICE_ENABLED = "SINGLE_CLOUD_SERVICE_ENABLED";
    private static final String KEY_FOLDER_PICKER_MODE_ENABLED = "FOLDER_PICKER_MODE_ENABLED";
    private static final String KEY_FILE_PICKER_MODE_ENABLED = "FILE_PICKER_MODE_ENABLED";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.file_picker_layout);
        mCloudEnabledGroup = (RadioGroup) findViewById(R.id.cloud_enabled_radio_box);
        mShareLinkEnabledGroup = (RadioGroup) findViewById(R.id.shareLink_enabled_radio_box);
        mSelectionTypeGroup = (RadioGroup) findViewById(R.id.selection_type_radio_box);
        mUsbConnectionGroup = (RadioGroup) findViewById(R.id.usb_connection_radio_box);
        mFolderPickerGroup = (RadioGroup) findViewById(R.id.folder_picker_radio_box);
        mFilePickerGroup = (RadioGroup) findViewById(R.id.file_picker_radio_box);

        launch = (Button) findViewById(R.id.launch_file_picker);
        mOutput = (TextView) findViewById(R.id.output_selected_files);

        mCloudEnabledGroup.setOnCheckedChangeListener(this);
        mSelectionTypeGroup.setOnCheckedChangeListener(this);
        mShareLinkEnabledGroup.setOnCheckedChangeListener(this);
        mUsbConnectionGroup.setOnCheckedChangeListener(this);
        mFolderPickerGroup.setOnCheckedChangeListener(this);
        mFilePickerGroup.setOnCheckedChangeListener(this);

        launch.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
                launch.setEnabled(false);
                mOutput.setText(R.string.output_selected_files);
                Intent intent = new Intent();

                intent.setClassName("com.htc.FilePicker", "com.htc.FilePicker.FilePicker");

                Bundle filePickerInfo = new Bundle();
                filePickerInfo.putString(KEY_APP_NAME, "FilePickerDemo");
                filePickerInfo.putInt(KEY_VIEW_TYPE, mViewTypeChoice);

                filePickerInfo.putInt(KEY_SELECTION_TYPE, mSelectionTypeChoice);
                filePickerInfo.putBoolean(KEY_GROUP_BY_PATH, mGroupChoice);
                filePickerInfo.putInt(KEY_MODE_TYPE_OF_FOLDER_VIEW, mModeTypeChoice);
                Toast.makeText(getApplicationContext(), "mode:" + mModeTypeChoice,
                        Toast.LENGTH_LONG);
                filePickerInfo.putString(KEY_TITLE, "FilePicker Demo Title");

                // Add ".dcf" to find DRM files
                String[] filter = { ".mp3", ".dcf", ".txt", ".pdf", ".doc", ".ppt", ".png" };
                filePickerInfo.putStringArray(KEY_FILTER, filter);
                filePickerInfo.putBoolean(KEY_REMOVE_FILE_MODE, true);
                // Add type filter, not FilePicker only support audio type.
                String[] drm_filter = { "drm_audio" };
                filePickerInfo.putStringArray("drm_filter", drm_filter);
                filePickerInfo.putInt(KEY_SELECTION_TYPE, mSelectionTypeChoice);

                filePickerInfo.putInt(KEY_USB_CONNECTION_MODE, mUsbConnectionViewMode);

                // The sample code is the following:
                // for Office/PDF/Mail
                filePickerInfo.putBoolean("CloudFunctionEnabled", mCloudFunctionEnabled);
                // for Mail
                filePickerInfo.putBoolean("returnDropBoxShareLinkEnable", mShareLinkEnabled);

                filePickerInfo.putBoolean(KEY_FOLDER_PICKER_MODE_ENABLED, mFolderPickerModeEnabled);
                filePickerInfo.putBoolean(KEY_FILE_PICKER_MODE_ENABLED, mFilePickerModeEnabled);
                intent.putExtras(filePickerInfo);
                intent.putExtra(KEY_SORT_TYPE, mSortType);
                intent.putExtra(KEY_SORT_ORDER, mSortOrder);
                intent.putExtra(KEY_SEARCH_HINT_TEXT, "search hint");
                FilePickerDemo.this.startActivityForResult(intent, FILEPICKER_REQUEST_CODE);
            }

        });

        SINGLE_SELECTION_ID = R.id.radio_button_single_selection;
        MULTIPLE_SELECTION_ID = R.id.radio_button_multiple_selection;
    }

    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
        case R.id.file_picker_enabled:
            mFilePickerModeEnabled = true;
            break;
        case R.id.file_picker_disable:
            mFilePickerModeEnabled = false;
            break;
        case R.id.folder_picker_disable:
            mFolderPickerModeEnabled = false;
            break;
        case R.id.folder_picker_enabled:
            mFolderPickerModeEnabled = true;
            break;
        case R.id.usb_connection_disable:
            mCloudEnabledGroup.setVisibility(View.VISIBLE);
            mShareLinkEnabledGroup.setVisibility(View.VISIBLE);
            mSelectionTypeGroup.setVisibility(View.VISIBLE);
            mUsbConnectionViewMode = -1;
            break;
        case R.id.usb_connection_mode_0:
            mCloudEnabledGroup.setVisibility(View.GONE);
            mShareLinkEnabledGroup.setVisibility(View.GONE);
            mSelectionTypeGroup.setVisibility(View.GONE);
            mUsbConnectionViewMode = 0;
            break;
        case R.id.usb_connection_mode_1:
            mCloudEnabledGroup.setVisibility(View.GONE);
            mShareLinkEnabledGroup.setVisibility(View.GONE);
            mSelectionTypeGroup.setVisibility(View.GONE);
            mUsbConnectionViewMode = 1;
            break;
        case R.id.shareLink_enabled_true:
            mShareLinkEnabled = true;
            break;
        case R.id.shareLink_enabled_false:
            mShareLinkEnabled = false;
            break;
        case R.id.cloud_enabled_true:
            mCloudFunctionEnabled = true;
            break;
        case R.id.cloud_enabled_false:
            mCloudFunctionEnabled = false;
            break;
        case R.id.radio_button_single_selection:
            mSelectionTypeChoice = SINGLE_SELECTION;
            break;
        case R.id.radio_button_multiple_selection:
            mSelectionTypeChoice = MULTIPLE_SELECTION;
            break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        launch.setEnabled(true);
        String[] output;
        if (data != null)
            output = data.getStringArrayExtra("output");
        else
            output = new String[0];

        if (data != null) {
            mSortType = data.getIntExtra("sort_type", 0);
            mSortOrder = data.getIntExtra("sort_order", 0);
            Log.e("FilePickerDemo", "onActivityResult,  sortType = " + mSortType + ", sortOrder = "
                    + mSortOrder);
        }
        if (output != null) {
            Log.e("FilePickerDemo", "onActivityResult, output.length = " + output.length
                    + ", sortType = " + mSortType + ", sortOrder = " + mSortOrder);
            StringBuilder sb = new StringBuilder();
            String outputText;
            for (int i = 0; i < output.length; i++) {
                sb.append(output[i] + ",\n");
                outputText = sb.toString();
                mOutput.setText(outputText);
                Log.e("FilePickerDemo", "outputText=" + outputText);
            }
        }
        Log.e("FilePickerDemo", "requestCode=" + requestCode);

    }

    /** Selection type 1, Single selection */
    public static final int SINGLE_SELECTION = 0;
    /** Selection type 2, Multiple selection */
    public static final int MULTIPLE_SELECTION = 1;

    /** Key for putting and getting the view type in Bundle */
    public static final String KEY_VIEW_TYPE = "viewType";

    /** Key for putting and getting the mode type of the folder view in Bundle */
    public static final String KEY_MODE_TYPE_OF_FOLDER_VIEW = "ModeTypeOfFolderView";

    /** Key for enable group by path in Bundle */
    public static final String KEY_GROUP_BY_PATH = "showPath";
    /** Key for putting and getting the selection type in Bundle */
    public static final String KEY_SELECTION_TYPE = "selectionType";
    /** Key for putting and getting the filter in Bundle */
    public static final String KEY_FILTER = "filter";
    /** Key for putting and getting the application name in Bundle */
    public static final String KEY_APP_NAME = "application_name";
    /** Key for putting and getting the title */
    public static final String KEY_TITLE = "title";
    /** Key for putting and getting the text when no file was found. */
    public static final String KEY_NO_FILE_FOUND_TEXT = "no_file_found_text";
    /** Key for putting and getting the root path. */
    public static final String KEY_ROOT_PATH = "root_path";
    /** key for putting and getting the mode information */
    public static final String KEY_REMOVE_FILE_MODE = "remove_file_mode";
    /** key for default sort type */
    public static final String KEY_SORT_TYPE = "sort_type";
    /** Key for sort order */
    public static final String KEY_SORT_ORDER = "sort_order";
    /** Key for search bar hint. */
    public static final String KEY_SEARCH_HINT_TEXT = "hint_search_text";
}
