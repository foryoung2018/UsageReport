
package com.htc.test;

import android.content.Context;
import android.graphics.Canvas;
import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

abstract public class HtcWidgetPerformanceTestBase<T extends View> extends
        ActivityInstrumentationTestCase2 {

    private Class<T> targetClass;
    private static final String TAG = "WidgetPerformance";
    private static final String CONSTRUCTOR = "constructor";
    private static final String MEASURE = "measure";
    private static final String LAYOUT = "layout";
    private static final String DRAW = "draw";
    private static final int COUNTER = 1000;
    private int mScreenWidth = 1080;
    private int mSreenHeight = 1920;

    public HtcWidgetPerformanceTestBase(Class activityClass, Class<T> targetClass) {
        super(activityClass);
        this.targetClass = targetClass;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);

        if (null != dm) {
            mScreenWidth = dm.widthPixels;
            mSreenHeight = dm.heightPixels;
        }
    }

    private static void Log(String className, String methodName, String info) {
        Log.i(TAG, className + "\t" + methodName + "\t" + info);
    }

    private View getInstance() {
        View instance = null;
        try {
            Constructor<? extends View> constructor = targetClass.getConstructor(
                    Context.class);
            instance = (View) constructor.newInstance(getActivity());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public void testConstructor() {
        long start = System.nanoTime();
        for (int i = 0; i < COUNTER; i++) {
            getInstance();
        }
        long time = System.nanoTime() - start;
        Log(targetClass.getSimpleName(), CONSTRUCTOR, String.valueOf(time));
    }

    public void testMeasure() {
        View v = getInstance();
        long start = System.nanoTime();
        for (int i = 0; i < COUNTER; i++) {
            v.measure(MeasureSpec.makeMeasureSpec(mScreenWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mSreenHeight, MeasureSpec.EXACTLY));
        }
        long time = System.nanoTime() - start;
        Log(targetClass.getSimpleName(), MEASURE, String.valueOf(time));
    }

    public void testLayout() {

        View v = getInstance();
        long start = System.nanoTime();
        for (int i = 0; i < COUNTER; i++) {
            v.layout(0, 0, mScreenWidth, mSreenHeight);
        }
        long time = System.nanoTime() - start;
        Log(targetClass.getSimpleName(), LAYOUT, String.valueOf(time));
    }

    public void testDraw() {
        View v = getInstance();
        Canvas canvas = new Canvas();
        long start = System.nanoTime();
        for (int i = 0; i < COUNTER; i++) {
            v.draw(canvas);
        }
        long time = System.nanoTime() - start;
        Log(targetClass.getSimpleName(), DRAW, String.valueOf(time));
    }

}
