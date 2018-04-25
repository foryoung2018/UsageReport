package com.htc.sense.commoncontrol.demo.quicktips;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.view.viewpager.HtcPagerAdapter;
import com.htc.lib1.cc.view.viewpager.HtcViewPager;
import com.htc.lib1.cc.widget.quicktips.QuickTipPopup;
import com.htc.lib1.theme.ThemeType;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

import java.util.ArrayList;
import java.util.List;

import static com.htc.lib1.cc.util.WindowUtil.isSuitableForLandscape;

public class QuickTipsDemo extends CommonDemoActivityBase implements SensorEventListener {
    private static String TAG = "QuickTipDemo";
    private Context mContext;
    protected WindowManager mWindowManager;

    private Button mAnchorViewTop;
    private Button mAnchorViewBottom;
    private QuickTipPopup mQuickTipsTop;
    private QuickTipPopup mQuickTipsBottom;

    private static boolean mHasArrow = true;
    private static boolean mHasImage = true;
    private static boolean mTestArabic = false;
    private static boolean mIsScreenFixed = false;
    private static boolean mIsRotatableInFixedScreen = true;
    private static boolean mIsScreenAlwaysInLandcape = false;

    private static int mScreenOrientation = !mIsScreenFixed ? ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED:
            (mIsScreenAlwaysInLandcape ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    private static String mTipText = "";
    private static String mShortTipText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.

        mContext = this;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        //initNotViewPager();
        initViewPager();
        initSensorManager();
        init();
    }

    private void initButton(View page){
        mAnchorViewTop = (Button) page.findViewById(R.id.button1);
        mAnchorViewTop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View anchor) {
                if (mHasArrow) {
                    if (mQuickTipsTop.isShowing()) {
                        mQuickTipsTop.dismiss();
                    } else {
                        mQuickTipsTop.showAsDropDown(anchor, 0, 0);
                    }
                } else {
                    if (mQuickTipsTop.isShowing()) {
                        mQuickTipsTop.dismiss();
                    } else {
                        mQuickTipsTop.showAtLocation(anchor, Gravity.CENTER, 0, 0);
                    }
                }

            }

        });

        mAnchorViewBottom = (Button) page.findViewById(R.id.button2);
        mAnchorViewBottom.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View anchor) {
                if (mHasArrow) {
                    if (mQuickTipsBottom.isShowing()) {
                        mQuickTipsBottom.dismiss();
                    } else {
                        mQuickTipsBottom.showAsDropDown(anchor, 0, 0);
                    }
                } else {
                    if (mQuickTipsBottom.isShowing()) {
                        mQuickTipsBottom.dismiss();
                    } else {
                        mQuickTipsBottom.showAtLocation(anchor, Gravity.CENTER, 0, 0);
                    }
                }
            }
        });
    }

    String[] is_rotatable = new String[]{"Rotatable in fixed screen","Not rotatable in fixed screen"};
    String[] orientations = new String[]{"Fix in portrait","Fix in landscape"};
    LinearLayout mSpinners;
    private void initSpinner(View page){
        mSpinners = (LinearLayout) page.findViewById(R.id.spinners);
        Spinner spinner_orientation = (Spinner) page.findViewById(R.id.spinner_orientation);
        ArrayAdapter<String> adapter_orientation = new ArrayAdapter<String>(mContext,R.layout.spinner_item,orientations);
        adapter_orientation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_orientation.setAdapter(adapter_orientation);
        spinner_orientation.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id){
                dismiss();
                mIsScreenAlwaysInLandcape = position==0 ? false : true;
                init();
                show();
            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        Spinner spinner_rotatable = (Spinner) page.findViewById(R.id.spinner_rotatable);
        ArrayAdapter<String> adapter_rotatable = new ArrayAdapter<String>(this,R.layout.spinner_item, is_rotatable);
        adapter_rotatable.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_rotatable.setAdapter(adapter_rotatable);
        spinner_rotatable.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id){
                dismiss();
                mIsRotatableInFixedScreen = position==0 ? true : false;
                init();
                show();
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void init(){
        mScreenOrientation = !mIsScreenFixed ? ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                : (mIsScreenAlwaysInLandcape ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mTipText = mTestArabic ? "بينما في الوضع السيارة، يمكنك إدخال الأوامر الصوتية مع إصبع الحنفية ثلاثة."
                : "While in car mode, you can enter a voice command with a three finger tap.";
        mShortTipText = mTestArabic ? "معلومات قصيرة"
                : "Short information!";

        mQuickTipsTop = new QuickTipPopup(this);
        mQuickTipsBottom = new QuickTipPopup(this);

        // hide "X" button
        mQuickTipsTop.setCloseVisibility(true);
        // If we use setOrientation before show, the anchor is a requirement.
        // Anchor is needed for estimating remaining space in order to prevent truncate issue.
        //mQuickTipsTop.setOrientation(QuickTipPopup.SCREEN_MODE_IPORTRAIT, mAnchorViewTop);

        if(mHasImage){
            int custom_height = (int) (mContext.getResources().getDrawable(R.drawable.quicktips_autimotive_tips_finger_tap).getIntrinsicHeight() +
                    mContext.getResources().getDimension(com.htc.lib1.cc.R.dimen.margin_l)*4);
            mQuickTipsTop.setImage(mContext.getResources().getDrawable(R.drawable.quicktips_autimotive_tips_finger_tap) , custom_height);
            mQuickTipsTop.setText(mTipText);
            mQuickTipsBottom.setImage(mContext.getResources().getDrawable(R.drawable.quicktips_easy_access_test_graphic));
            mQuickTipsBottom.setText(mTipText);
        }else{
            mQuickTipsTop.setText(mShortTipText);
            mQuickTipsTop.setImage(null);
            mQuickTipsBottom.setText(mShortTipText);
            mQuickTipsBottom.setImage(null);
        }

        if(mHasArrow)
            setExpandDirection();

        if(mIsRotatableInFixedScreen){
            // for Portrait or Landscape only application.
            if(mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    || mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){

                sensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(this, mSensor,SensorManager.SENSOR_DELAY_NORMAL);

                //calculate the orientation by G sensor
                //calculateOrientation(mTestAxis);
            }else
                sensorManager.unregisterListener(this);
        }else{
            sensorManager.unregisterListener(this);
        }
            setRequestedOrientation(mScreenOrientation);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mHasArrow && mAnchorViewTop!=null && mAnchorViewBottom!=null)
            setExpandDirection();
    }

    private void setExpandDirection() {
        if (isSuitableForLandscape(mContext.getResources())) {
            if( mQuickTipsTop!=null )
                mQuickTipsTop.setExpandDirection(QuickTipPopup.EXPAND_RIGHT);
            if( mQuickTipsBottom!=null )
                mQuickTipsBottom.setExpandDirection(QuickTipPopup.EXPAND_LEFT);
        } else {
            if( mQuickTipsTop!=null )
                mQuickTipsTop.setExpandDirection(QuickTipPopup.EXPAND_DOWN);
            if( mQuickTipsBottom!=null )
                mQuickTipsBottom.setExpandDirection(QuickTipPopup.EXPAND_UP);
        }
    }

    private void setupCustomColor(int customColor){
        // start : Set up custom color
        mQuickTipsTop.setBackgroundColor(customColor);
        // end : Set up custom color
    }
    /**
     * ===================================================================================================
     * [Begin] for rotatable fixed screen application like Camera
     *
     * The following samples is for Portrait or Landscape only application.
     * They should handle the orientation by themselves.
     *
     * USAGE :  Please call setOrientation(byte screen_mode) in an appropriate timing.
     *
     * There are 4 screen modes :
     *
     * (1) QuickTipPopup.SCREEN_MODE_PORTRAIT
     * (2) QuickTipPopup.SCREEN_MODE_IPORTRAIT
     * (3) QuickTipPopup.SCREEN_ORIENTATION_LANDSCAPE
     * (4) QuickTipPopup.SCREEN_MODE_ILANDSCAPE
     * ===================================================================================================
     * */
    SensorManager sensorManager;
    private boolean mIsPortrait = true ;
    private Sensor aSensor;
    private Sensor mSensor;
    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];
    final int AXIS_X = 0;
    final int AXIS_Y = 1;
    final int AXIS_Z = 2;
    //final int mTestAxis = AXIS_Y;

    private void initSensorManager(){
        // get sensor manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //for testing
//        List<Sensor> list = this.sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
//        if (list.isEmpty()) {
//            this.insert2Tv("不支援傾斜感應器");
//        }
//        else {
//            this.sensor = list.get(0);
//            this.insert2Tv("取得傾斜感應器：" + this.sensor.getName());
//        }
//        this.insert2Tv("onCreated");
    }

    // sensor event listener
//    final SensorEventListener this = new SensorEventListener() {
//        public void onSensorChanged(SensorEvent sensorEvent) {
//            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
//                magneticFieldValues = sensorEvent.values;
//            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
//                accelerometerValues = sensorEvent.values;
//            calculateOrientation(mTestAxis);
//
//            //for testing
//
//            // 方位角，就是手機頭的朝向，北為0，東為90，餘類推，實做結果不準
//            // float val = event.values[0];
//            // 南北向旋轉，手機水平放置螢幕朝向上（0）、頭朝上（-90）
//            // 頭向下（90）、水平放置螢幕朝向下（-180/180）
//            // float val = event.values[1];
//            // 東西向旋轉，手機水平放置螢幕朝向上（0）、向右翻螢幕朝右（-90）
//            // 向左翻螢幕朝左（90）、水平放置螢幕朝向下（0）
//            // float val = event.values[2];
//            this.insert2Tv("方位角：" + sensorEvent.values[0] + "南北向：" + sensorEvent.values[1]
//                    + "東西向：" + sensorEvent.values[2]);
//        }
//
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
//
//        private void insert2Tv(String msg) {
//
//            Log.d("5100", msg);
//        }
//    };

    public void onSensorChanged(SensorEvent sensorEvent) {
        // mark for testing
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magneticFieldValues = sensorEvent.values;
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accelerometerValues = sensorEvent.values;
        calculateOrientation();
        // mark for testing

        //for testing

        // 方位角，就是手機頭的朝向，北為0，東為90，餘類推，實做結果不準
        // float val = event.values[0];
        // 南北向旋轉，手機水平放置螢幕朝向上（0）、頭朝上（-90）
        // 頭向下（90）、水平放置螢幕朝向下（-180/180）
        // float val = event.values[1];
        // 東西向旋轉，手機水平放置螢幕朝向上（0）、向右翻螢幕朝右（-90）
        // 向左翻螢幕朝左（90）、水平放置螢幕朝向下（0）
        // float val = event.values[2];
        this.insert2Tv("方位角：" + sensorEvent.values[0] + "南北向：" + sensorEvent.values[1]
                + "東西向：" + sensorEvent.values[2]);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void insert2Tv(String msg) {

        Log.d("5100", msg);
    }

    // register to listen to sensors
    @Override
    public void onResume() {
        super.onResume();
        if(mIsRotatableInFixedScreen && (mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                || mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)){
            // mark for testing
            sensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, mSensor,SensorManager.SENSOR_DELAY_NORMAL);
            // mark for testing
        }
    }

    // unregister to listen to sensors
    @Override
    public void onPause() {
      super.onPause();
//      if(mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//              || mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//      sensorManager.unregisterListener(this);

    }

    // Note that: This is just a simple demo , please reconsider the orientation algorithm.
    // The point is to use setOrientation for controlling the screen mode.
    private  void calculateOrientation(){//int axis) {
        //if(axis!=AXIS_X && axis!=AXIS_Y && axis!=AXIS_Z)    return;

        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R, values);

        // Change to degrees
        values[0] = (float) Math.toDegrees(values[0]);
        Log.i("510", values[0]+"");
        values[1] = (float) Math.toDegrees(values[1]);
        Log.i("510", values[1]+"");
        values[2] = (float) Math.toDegrees(values[2]);
        Log.i("510", values[2]+"");


        int x = (int) values[0];
        int y = (int) values[1];
        int z = (int) values[2];


float value = y;

        if(value >= -45 && value < 45){
            Log.i(TAG, "Landscape 1");
            mIsPortrait = false;
         }
         else if(value >= 45 && value < 135){
             Log.i(TAG, "Portrait 1");
             mIsPortrait = true;
         }
         else if((value >= 135 && value <= 180) || (value) >= -180 && value < -135){
             Log.i(TAG, "Landscape 2");
             mIsPortrait = false;
         }
         else if(value >= -135 && value <-45){
             Log.i(TAG, "Portrait 2");
             mIsPortrait = true;
         }

        Log.i("510","screen_mode = "+screen_mode);
        setOrientation();
      }
private int delta = 25;
private byte screen_mode = QuickTipPopup.SCREEN_MODE_PORTRAIT;
    private void setOrientation() {
        // The point is to use setOrientation for controlling the screen mode
        boolean isActivityPortrait = (mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if( (isActivityPortrait && mIsPortrait) || (!isActivityPortrait && !mIsPortrait)){
            mQuickTipsTop.setOrientation(QuickTipPopup.SCREEN_MODE_PORTRAIT);
            mQuickTipsBottom.setOrientation(QuickTipPopup.SCREEN_MODE_PORTRAIT);
        }else{
            mQuickTipsTop.setOrientation(QuickTipPopup.SCREEN_MODE_LANDSCAPE);
            mQuickTipsBottom.setOrientation(QuickTipPopup.SCREEN_MODE_LANDSCAPE);
        }
   /*     byte adj_screen_mode = screen_mode;
        switch(screen_mode){
        case QuickTipPopup.SCREEN_MODE_PORTRAIT:
            if(!isActivityPortrait)
                adj_screen_mode = QuickTipPopup.SCREEN_MODE_LANDSCAPE;
            break;
        case QuickTipPopup.SCREEN_MODE_IPORTRAIT:
            if(!isActivityPortrait)
                adj_screen_mode = QuickTipPopup.SCREEN_MODE_ILANDSCAPE;
            break;
        case QuickTipPopup.SCREEN_MODE_LANDSCAPE:
            if(isActivityPortrait)
                adj_screen_mode = QuickTipPopup.SCREEN_MODE_PORTRAIT;
            break;
        case QuickTipPopup.SCREEN_MODE_ILANDSCAPE:
            if(isActivityPortrait)
                adj_screen_mode = QuickTipPopup.SCREEN_MODE_IPORTRAIT;
            break;

        }
        mQuickTipsTop.setOrientation(adj_screen_mode);
        mQuickTipsBottom.setOrientation(adj_screen_mode);*/
    }

    /**
     * ===================================================================================================
     * [End] for rotatable fixed screen application like Camera
     * ===================================================================================================
     * */

    /**
     * ===================================================================================================
     * [Start] for demo multiple situations
     * ===================================================================================================
     * */
    private static boolean mShowTopAgain = false;
    private static boolean mShowBottomAgain = false;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quicktips, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.has_arrow) {
            dismiss();

            mHasArrow = revert(mHasArrow);
            if(mHasArrow)
                item.setTitle(mContext.getResources().getString(R.string.quicktip_no_arrow));
            else
                item.setTitle(mContext.getResources().getString(R.string.quicktip_with_arrow));

            init();
            show();
            return true;
        } else if (id == R.id.has_image) {
            dismiss();

            mHasImage = revert(mHasImage);

            if(mHasImage)
                item.setTitle(mContext.getResources().getString(R.string.text_only_quicktip));
            else
                item.setTitle(mContext.getResources().getString(R.string.image_quicktip));

            init();
            show();
            return true;
        } else if (id == R.id.fix_screen) {
            dismiss();

            mIsScreenFixed  = revert(mIsScreenFixed);

            if(mIsScreenFixed){
                if(mIsScreenAlwaysInLandcape)
                    mScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                else
                    mScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                item.setTitle(mContext.getResources().getString(R.string.unspecified_screen));
                mSpinners.setVisibility(View.VISIBLE);
            }else{
                mScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
                item.setTitle(mContext.getResources().getString(R.string.fix_screen));
                mSpinners.setVisibility(View.GONE);
            }

            init();
            show();
            return true;
        } else if (id == R.id.is_arabic) {
            dismiss();

            mTestArabic  = revert(mTestArabic);
            if(mTestArabic){
                item.setTitle(mContext.getResources().getString(R.string.test_english));
            }else{
                item.setTitle(mContext.getResources().getString(R.string.test_arabic));
            }

            init();
            show();
            return true;
        } else if (id == R.id.customColor) {
            setupCustomColor(0xff0000ff);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void dismiss(){
        if(mQuickTipsTop!=null && mQuickTipsTop.isShowing()){
            mQuickTipsTop.dismiss();
            mShowTopAgain = true;
        }

        if(mQuickTipsBottom!=null && mQuickTipsBottom.isShowing()){
            mQuickTipsBottom.dismiss();
            mShowBottomAgain = true;
        }
    }

    private void show(){
        if(mAnchorViewTop!=null && mQuickTipsTop!=null && mShowTopAgain){
            if(mHasArrow){
                mQuickTipsTop.showAsDropDown(mAnchorViewTop,0,0);
            }
            else{
                //mQuickTipsTop.showAtLocation(mAnchorViewTop, Gravity.CENTER, 0, 0);
                mQuickTipsTop.showAtLocation(mAnchorViewTop, Gravity.LEFT, 130, 0);
            }

            mShowTopAgain = false;
        }

        if(mAnchorViewBottom!=null && mQuickTipsBottom!=null && mShowBottomAgain){
            if(mHasArrow){
                mQuickTipsBottom.showAsDropDown(mAnchorViewBottom,0,0);
            }
            else{
                //mQuickTipsBottom.showAtLocation(mAnchorViewBottom, Gravity.CENTER, 0, 0);
                mQuickTipsBottom.showAtLocation(mAnchorViewBottom, Gravity.LEFT, 130, 0);
            }

            mShowBottomAgain = false;
        }
    }

    private boolean revert(boolean value){
        return !value;
    }
    /**
     * ===================================================================================================
     * [End] for demo multiple situations
     * ===================================================================================================
     * */

    /**
     * ===================================================================================================
     * [Begin] for scrolling problem
     * ===================================================================================================
     */

    private HtcViewPager awesomePager;
    private AwesomePagerAdapter awesomeAdapter;
    private LayoutInflater mInflater;
    private List<View> mListViews;

    private void initNotViewPager(){
      setContentView(R.layout.quicktips);

      //initButton();
      mAnchorViewTop = (Button) findViewById(R.id.button1);
      mAnchorViewTop.setOnClickListener(new OnClickListener() {

          @Override
          public void onClick(View anchor) {
              if (mHasArrow) {
                  if (mQuickTipsTop.isShowing()) {
                      mQuickTipsTop.dismiss();
                  } else {
                      mQuickTipsTop.showAsDropDown(anchor, 0, 0);
                  }
              } else {
                  if (mQuickTipsTop.isShowing()) {
                      mQuickTipsTop.dismiss();
                  } else {
                      //mQuickTipsTop.showAtLocation(anchor, Gravity.CENTER, 0, 0);
                      mQuickTipsTop.showAtLocation(anchor, Gravity.LEFT, 130, 0);

                  }
              }

          }

      });

      mAnchorViewBottom = (Button) findViewById(R.id.button2);
      mAnchorViewBottom.setOnClickListener(new OnClickListener() {

          @Override
          public void onClick(View anchor) {
              if (mHasArrow) {
                  if (mQuickTipsBottom.isShowing()) {
                      mQuickTipsBottom.dismiss();
                  } else {
                      mQuickTipsBottom.showAsDropDown(anchor, 0, 0);
                  }
              } else {
                  if (mQuickTipsBottom.isShowing()) {
                      mQuickTipsBottom.dismiss();
                  } else {
                      // mQuickTipsBottom.showAtLocation(anchor, Gravity.CENTER, 0, 0);
                      mQuickTipsBottom.showAtLocation(anchor, Gravity.LEFT, 130, 0);
                  }
              }
          }
      });
      //initSpinner();
      mSpinners = (LinearLayout) findViewById(R.id.spinners);
      Spinner spinner_orientation = (Spinner) findViewById(R.id.spinner_orientation);
      ArrayAdapter<String> adapter_orientation = new ArrayAdapter<String>(mContext,R.layout.spinner_item,orientations);
      adapter_orientation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      spinner_orientation.setAdapter(adapter_orientation);
      spinner_orientation.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
          public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id){
              dismiss();
              mIsScreenAlwaysInLandcape = position==0 ? false : true;
              init();
              show();
          }
          public void onNothingSelected(AdapterView<?> arg0) {

          }
      });

      Spinner spinner_rotatable = (Spinner) findViewById(R.id.spinner_rotatable);
      ArrayAdapter<String> adapter_rotatable = new ArrayAdapter<String>(this,R.layout.spinner_item, is_rotatable);
      adapter_rotatable.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      spinner_rotatable.setAdapter(adapter_rotatable);
      spinner_rotatable.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
          public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
              dismiss();
              mIsRotatableInFixedScreen = position == 0 ? true : false;
              init();
              show();
          }

          public void onNothingSelected(AdapterView<?> arg0) {
          }
      });
    }
    private void initViewPager(){
        setContentView(R.layout.quicktip_viewpager);
        mListViews = new ArrayList<View>();
        mInflater = getLayoutInflater();
        View page1 = mInflater.inflate(R.layout.quicktips, null);
        initButton(page1);
        initSpinner(page1);
        mListViews.add(page1);
        mListViews.add(mInflater.inflate(R.layout.quicktips_empty, null));

        awesomeAdapter = new AwesomePagerAdapter();
        awesomePager = (HtcViewPager) findViewById(R.id.viewpager);
        awesomePager.setAdapter(awesomeAdapter);
    }

    private class AwesomePagerAdapter extends HtcPagerAdapter{

        @Override
        public int getCount() {
            return mListViews.size();
        }

        /**
         * 从指定的position创建page
         *
         * @param container ViewPager容器
         * @param position The page position to be instantiated.
         * @return 返回指定position的page，这里不需要是一个view，也可以是其他的视图容器.
         */
        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            ((HtcViewPager) collection).addView(mListViews.get(position),0);

            return mListViews.get(position);
        }

        /**
         * <span style="font-family:'Droid Sans';">从指定的position销毁page</span>
         *
         *
         *<span style="font-family:'Droid Sans';">参数同上</span>
         */
        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            ((HtcViewPager) collection).removeView(mListViews.get(position));
        }



        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==(object);
        }

        @Override
        public void finishUpdate(ViewGroup arg0) {}


        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {}

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(ViewGroup arg0) {}

    }

    /**
     * ===================================================================================================
     * [End] for scrolling problem
     * ===================================================================================================
     * */
}
