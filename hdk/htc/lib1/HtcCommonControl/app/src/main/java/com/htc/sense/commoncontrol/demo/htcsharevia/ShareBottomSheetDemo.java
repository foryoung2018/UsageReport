package com.htc.sense.commoncontrol.demo.htcsharevia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.htc.lib1.cc.app.HtcShareActivity;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ShareBottomSheetDemo extends CommonDemoActivityBase {

    private static final int REQ_SHARE_PLAN_TEXT = 23512;
    private static final int REQ_SHARE_JPG_IMAGE = 23513;
    private static final int REQ_SHARE_BINARY = 23514;
    private static final int REQ_SHARE_ALLOWED_PACKAGES = 23515;
    private static final int REQ_SHARE_BLOCKED_PACKAGES = 23516;
    private static final int REQ_SHARE_MULTI_IMAGES = 23517;
    private static final int REQ_SHARE_MULTI_INTENTS = 23518;
    private static final int REQ_SHARE_LOCATION = 23519;

    private static final String KEY_MODE_DARK = "key_mode_dark";
    private boolean mIsDarkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); // comment this to test animation...
        if (savedInstanceState != null) {
            mIsDarkMode = savedInstanceState.getBoolean(KEY_MODE_DARK);
        }
        setContentView(R.layout.activity_sharebottomsheetdemo);

        // share plan text
        Button sharePlanText = (Button) findViewById(R.id.sharePlanText);
        sharePlanText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView planText = (TextView) findViewById(R.id.planText);
                Intent intent2Resolve = new Intent();
                intent2Resolve.setAction(Intent.ACTION_SEND);
                intent2Resolve.putExtra(Intent.EXTRA_TEXT, "" + planText.getText());
                intent2Resolve.setType("text/plain");
                Log.d("henry", "ShareBottomSheetDemo..onClick: text=" + planText.getText());

                // setup intent to resolve
                Intent[] intentData = new Intent[1];
                intentData[0] = intent2Resolve;

                Intent intent = prepareIntent(intentData);
                HtcShareActivity.startActivityForResult(intent, REQ_SHARE_PLAN_TEXT, ShareBottomSheetDemo.this);
            }
        });

        // share jpeg image
        Button shareJpgImage = (Button) findViewById(R.id.shareJpgImage);
        shareJpgImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // prepare data
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File file = new File(path, "htcShareActivity_SampleJpg_MonkeySelfie.jpg");

                try {
                    // Make sure the Pictures directory exists.
                    path.mkdirs();

                    // Very simple code to copy a picture from the application's
                    // resource into the external file.  Note that this code does
                    // no error checking, and assumes the picture is small (does not
                    // try to copy it in chunks).  Note that if external storage is
                    // not currently mounted this will silently fail.
                    InputStream is = getResources().openRawResource(R.drawable.monkey_selfie_contentfullwidth);
                    OutputStream os = new FileOutputStream(file);

                    byte[] data = new byte[is.available()];
                    is.read(data);
                    os.write(data);
                    is.close();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                String extraStream = Uri.fromFile(file).toString();
                Log.d("henry", "ShareBottomSheetDemo..onClick: extraStream=" + extraStream);

                // prepare intent
                Intent intent2Resolve = new Intent();
                intent2Resolve.setAction(Intent.ACTION_SEND);
                intent2Resolve.setType("image/jpeg");
                intent2Resolve.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

                // setup intent to resolve
                Intent[] intentData = new Intent[1];
                intentData[0] = intent2Resolve;

                Intent intent = prepareIntent(intentData);
                HtcShareActivity.startActivityForResult(intent, REQ_SHARE_JPG_IMAGE, ShareBottomSheetDemo.this);
            }
        });

        // share unknown binary
        Button shareBinary = (Button) findViewById(R.id.shareBinary);
        shareBinary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // prepare data
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File file = new File(path, "htcShareActivity_SampleBinary.bin");

                try {
                    // Make sure the Pictures directory exists.
                    path.mkdirs();

                    // Very simple code to copy a picture from the application's
                    // resource into the external file.  Note that this code does
                    // no error checking, and assumes the picture is small (does not
                    // try to copy it in chunks).  Note that if external storage is
                    // not currently mounted this will silently fail.
                    InputStream is = getResources().openRawResource(R.raw.unknown_binary_data);
                    OutputStream os = new FileOutputStream(file);

                    byte[] data = new byte[is.available()];
                    is.read(data);
                    os.write(data);
                    is.close();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                // prepare intent
                Intent intent2Resolve = new Intent();
                intent2Resolve.setAction(Intent.ACTION_SEND);
                intent2Resolve.setType("application/octet-stream");
                intent2Resolve.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

                // setup intent to resolve
                Intent[] intentData = new Intent[1];
                intentData[0] = intent2Resolve;

                Intent intent = prepareIntent(intentData);
                HtcShareActivity.startActivityForResult(intent, REQ_SHARE_BINARY, ShareBottomSheetDemo.this);
            }
        });

        // share plan text to allowed packages only
        Button shareToAllowedPackages = (Button) findViewById(R.id.shareToAllowedPackages);
        shareToAllowedPackages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] allowedPackages = null;
                TextView allowedPackagesTextView = (TextView) findViewById(R.id.allowedPackages);
                String[] tmp = allowedPackagesTextView.getText().toString().split("[,;: ]+");
                if (null != tmp && 0 < tmp.length && !TextUtils.isEmpty(tmp[0])) {
                    allowedPackages = tmp;
                    for (String str : tmp) Log.d("henry", "ShareBottomSheetDemo..onClick: str=" + str + ".");
                }

                Intent intent2Resolve = new Intent();
                intent2Resolve.setAction(Intent.ACTION_SEND);
                intent2Resolve.putExtra(Intent.EXTRA_TEXT, "Share plain text to allowed packages only");
                intent2Resolve.setType("text/plain");

                // setup intent to resolve
                Intent[] intentData = new Intent[1];
                intentData[0] = intent2Resolve;

                Intent intent = prepareIntent(intentData);
                if (null != allowedPackages && 0 != allowedPackages.length) {
                    intent.putExtra(HtcShareActivity.EXTRA_ALLOWED_PACKAGE_LIST, allowedPackages);
                }
                HtcShareActivity.startActivityForResult(intent, REQ_SHARE_ALLOWED_PACKAGES, ShareBottomSheetDemo.this);
            }
        });

        // share plan text to allowed packages only
        Button shareWithoutBlockedPackages = (Button) findViewById(R.id.shareWithoutBlockedPackages);
        shareWithoutBlockedPackages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] blockedPackages = null;
                TextView blockedPackagesTextView = (TextView) findViewById(R.id.blockedPackages);
                String[] tmp = blockedPackagesTextView.getText().toString().split("[,;: ]+");
                if (null != tmp && 0 < tmp.length && !TextUtils.isEmpty(tmp[0])) {
                    blockedPackages = tmp;
                    for (String str : tmp) Log.d("henry", "ShareBottomSheetDemo..onClick: str=" + str + ".");
                }

                Intent intent2Resolve = new Intent();
                intent2Resolve.setAction(Intent.ACTION_SEND);
                intent2Resolve.putExtra(Intent.EXTRA_TEXT, "Share plain text without blocked packages");
                intent2Resolve.setType("text/plain");

                // setup intent to resolve
                Intent[] intentData = new Intent[1];
                intentData[0] = intent2Resolve;

                Intent intent = prepareIntent(intentData);
                if (null != blockedPackages && 0 != blockedPackages.length) {
                    intent.putExtra(HtcShareActivity.EXTRA_BLOCKED_PACKAGE_LIST, blockedPackages);
                }
                HtcShareActivity.startActivityForResult(intent, REQ_SHARE_BLOCKED_PACKAGES, ShareBottomSheetDemo.this);
            }
        });

        // share multiple images
        Button shareMultiImages = (Button) findViewById(R.id.shareMultiImages);
        shareMultiImages.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // prepare data
                ArrayList<Uri> images = new ArrayList<Uri>();
                try {
                    final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    // Make sure the Pictures directory exists.
                    path.mkdirs();

                    int[] drawables = {R.drawable.giraffe, R.drawable.lion, R.drawable.goat};
                    String[] fileNames = {"giraffe.jpg", "lion.jpg", "goat.png"};

                    for (int i = 0; i < drawables.length; ++i) {
                        File file = new File(path, fileNames[i]);

                        // Very simple code to copy a picture from the application's
                        // resource into the external file.  Note that this code does
                        // no error checking, and assumes the picture is small (does not
                        // try to copy it in chunks).  Note that if external storage is
                        // not currently mounted this will silently fail.
                        OutputStream os = new FileOutputStream(file);
                        InputStream is = getResources().openRawResource(drawables[i]);
                        byte[] data = new byte[is.available()];
                        is.read(data);
                        os.write(data);
                        is.close();
                        os.close();

                        images.add(Uri.fromFile(file));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                // prepare intent
                Intent intent2Resolve = new Intent();
                intent2Resolve.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent2Resolve.setType("image/*");
                intent2Resolve.putExtra(Intent.EXTRA_STREAM, images);

                // setup intent to resolve
                Intent[] intentData = new Intent[1];
                intentData[0] = intent2Resolve;

                Intent intent = prepareIntent(intentData);
                HtcShareActivity.startActivityForResult(intent, REQ_SHARE_MULTI_IMAGES, ShareBottomSheetDemo.this);
            }
        });

        // share by different intents
        Button shareMultiIntents = (Button) findViewById(R.id.shareMultiIntents);
        shareMultiIntents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // prepare image data
                ArrayList<Uri> images = new ArrayList<Uri>();
                try {
                    final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    // Make sure the Pictures directory exists.
                    path.mkdirs();

                    int[] drawables = {R.drawable.monkey};
                    String[] fileNames = {"monkey.jpg"};

                    for (int i = 0; i < drawables.length; ++i) {
                        File file = new File(path, fileNames[i]);

                        // Very simple code to copy a picture from the application's
                        // resource into the external file.  Note that this code does
                        // no error checking, and assumes the picture is small (does not
                        // try to copy it in chunks).  Note that if external storage is
                        // not currently mounted this will silently fail.
                        OutputStream os = new FileOutputStream(file);
                        InputStream is = getResources().openRawResource(drawables[i]);
                        byte[] data = new byte[is.available()];
                        is.read(data);
                        os.write(data);
                        is.close();
                        os.close();

                        images.add(Uri.fromFile(file));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                TextView planText = (TextView) findViewById(R.id.planTextMultiIntents);
                // prepare intent 1: only plain text
                Intent textOnlyIntent = new Intent();
                textOnlyIntent.setAction(Intent.ACTION_SEND);
                textOnlyIntent.putExtra(Intent.EXTRA_TEXT, "" + planText.getText());
                textOnlyIntent.setType("text/plain");

                // prepare intent 2: only image
                Intent imageOnlyIntent = new Intent();
                imageOnlyIntent.setAction(Intent.ACTION_SEND);
                imageOnlyIntent.setType("image/jpeg");
                imageOnlyIntent.putExtra(Intent.EXTRA_STREAM, images.get(0));

                // prepare intent 3: both text and image
                Intent mixedContentIntent = new Intent();
                mixedContentIntent.setAction(Intent.ACTION_SEND);
                mixedContentIntent.setType("*/*");
                mixedContentIntent.putExtra(Intent.EXTRA_STREAM, images.get(0));
                mixedContentIntent.putExtra(Intent.EXTRA_TEXT, "" + planText.getText());


                // setup intent to resolve
                Intent[] intentData = new Intent[3];
                intentData[0] = textOnlyIntent;
                intentData[1] = imageOnlyIntent;
                intentData[2] = mixedContentIntent;

                Intent intent = prepareIntent(intentData);
                HtcShareActivity.startActivityForResult(intent, REQ_SHARE_MULTI_INTENTS, ShareBottomSheetDemo.this);
            }
        });

        // share location
        Button shareLocation = (Button) findViewById(R.id.shareLocation);
        shareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // prepare
                double latitude = 24.979027;
                double longitude = 121.545629;
                String locationUri = "geo:" + latitude + "," +longitude +
                        "?q=" + latitude + "," + longitude;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationUri));

                // setup intent to resolve
//                Intent[] intentData = new Intent[1];
//                intentData[0] = intent;
//                Intent intent2StartHtcShareActivity = new Intent(ShareBottomSheetDemo.this, HtcShareActivity.class);
//                intent2StartHtcShareActivity.putExtra(HtcShareActivity.EXTRA_INTENT_LIST, intentData);
//                intent2StartHtcShareActivity.putExtra(HtcShareActivity.EXTRA_THEME_CATEGORY, HtcCommonUtil.CATEGORYONE);
//                HtcShareActivity.startActivityForResult(intent2StartHtcShareActivity, REQ_SHARE_LOCATION, ShareBottomSheetDemo.this);

                HtcShareActivity.startActivityForResult(intent, HtcCommonUtil.CATEGORYONE, null, REQ_SHARE_LOCATION, ShareBottomSheetDemo.this);
            }
        });
    }

    private Intent prepareIntent(Intent[] intentData) {
        Intent intent = new Intent(ShareBottomSheetDemo.this, HtcShareActivity.class);
        intent.putExtra(HtcShareActivity.EXTRA_INTENT_LIST, intentData);
        if (mIsDarkMode) {
            intent.putExtra(HtcShareActivity.EXTRA_TITLE, "Share in Dark");
            intent.putExtra(HtcShareActivity.EXTRA_THEME_CATEGORY, HtcShareActivity.FLAG_THEME_DARK);
        }else{
            intent.putExtra(HtcShareActivity.EXTRA_THEME_CATEGORY, HtcCommonUtil.CATEGORYONE);
        }
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sharevia_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemDarkMode= menu.findItem(R.id.mode_dark);
        itemDarkMode.setChecked(mIsDarkMode);
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mode_dark) {
            item.setChecked(mIsDarkMode = !mIsDarkMode);
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_MODE_DARK, mIsDarkMode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            Log.d("henry", "ShareBottomSheetDemo.onActivityResult: component=" + data.getComponent());
            Log.d("henry", "ShareBottomSheetDemo.onActivityResult: action=" + data.getAction() + " type=" + data.getType());
            Log.d("henry", "ShareBottomSheetDemo.onActivityResult: EXTRA_TEXT=" + data.getStringExtra(Intent.EXTRA_TEXT));
            Log.d("henry", "ShareBottomSheetDemo.onActivityResult: EXTRA_STREAM=" + data.getParcelableExtra(Intent.EXTRA_STREAM));

            if (REQ_SHARE_MULTI_INTENTS == requestCode) {
                Parcelable[] intents = data.getParcelableArrayExtra(HtcShareActivity.EXTRA_INTENT_LIST);
                if (null != intents) {
                    Log.d("henry", "ShareBottomSheetDemo.onActivityResult: app should evaluate which intent to use:");
                    for (Parcelable i : intents) {
                        Log.d("henry", "ShareBottomSheetDemo.onActivityResult: i=" + i);
                    }
                    return;
                }
            }
            startActivity(data);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}