package com.htc.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.test.InstrumentationTestCase;
import android.view.View;

import com.htc.test.util.PerformanceUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class HtcWidgetPerformanceTestCase<V extends View> extends
        InstrumentationTestCase {
    private Constructor<V> mWidgetConstructor = null;
    private Constructor<V> mTestCaseClass = null;

    public HtcWidgetPerformanceTestCase(Class widgetClass, Class testCaseClass) {
        super();
        try {
            mWidgetConstructor = widgetClass.getConstructor(Context.class);
            mTestCaseClass = testCaseClass.getConstructor();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private V getWidgetInstance() {
        V v = null;
        try {
            if (null != mWidgetConstructor)
                v = mWidgetConstructor.newInstance(getInstrumentation()
                        .getContext());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return v;
    }

    final public void testContructor() {
        int iteration = ((HtcWidgetPerformanceTest) mTestCaseClass
                .getAnnotation(HtcWidgetPerformanceTest.class))
                .contructorIteration();
        int expectedCpuTime = ((HtcWidgetPerformanceTest) mTestCaseClass
                .getAnnotation(HtcWidgetPerformanceTest.class))
                .contructorCpuTime();
        int expectedExcutionTime = ((HtcWidgetPerformanceTest) mTestCaseClass
                .getAnnotation(HtcWidgetPerformanceTest.class))
                .contructorExecutionTime();

        PerformanceUtil.start(getInstrumentation());
        for (int i = 0; i < iteration; i++) {
            getWidgetInstance();
        }
        PerformanceUtil.stop(getInstrumentation());

        PerformanceUtil.assertAllTypeTime(this, expectedCpuTime,
                expectedExcutionTime);
    }

    final public void testOnMeasure() {
        V v = getWidgetInstance();
        int widthSpec = View.MeasureSpec.makeMeasureSpec(1080,
                View.MeasureSpec.UNSPECIFIED);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(1920,
                View.MeasureSpec.UNSPECIFIED);
        int iteration = ((HtcWidgetPerformanceTest) mTestCaseClass
                .getAnnotation(HtcWidgetPerformanceTest.class))
                .measureIteration();
        int expectedCpuTime = ((HtcWidgetPerformanceTest) mTestCaseClass
                .getAnnotation(HtcWidgetPerformanceTest.class))
                .measureCpuTime();
        int expectedExcutionTime = ((HtcWidgetPerformanceTest) mTestCaseClass
                .getAnnotation(HtcWidgetPerformanceTest.class))
                .measureExecutionTime();

        PerformanceUtil.start(getInstrumentation());
        for (int i = 0; i < iteration; i++) {
            if (null != v)
                v.measure(widthSpec, heightSpec);
        }
        PerformanceUtil.stop(getInstrumentation());
        PerformanceUtil.assertAllTypeTime(this, expectedCpuTime,
                expectedExcutionTime);
    }

    final public void testOnLayout() {
        V v = getWidgetInstance();
        PerformanceUtil.start(getInstrumentation());
        int iteration = ((HtcWidgetPerformanceTest) mTestCaseClass
                .getAnnotation(HtcWidgetPerformanceTest.class))
                .layoutIteration();
        int expectedCpuTime = ((HtcWidgetPerformanceTest) mTestCaseClass
                .getAnnotation(HtcWidgetPerformanceTest.class)).layoutCpuTime();
        int expectedExcutionTime = ((HtcWidgetPerformanceTest) mTestCaseClass
                .getAnnotation(HtcWidgetPerformanceTest.class))
                .layoutExecutionTime();
        for (int i = 0; i < iteration; i++) {
            v.layout(0, 0, 1080, 1920);
        }
        PerformanceUtil.stop(getInstrumentation());
        PerformanceUtil.assertAllTypeTime(this, expectedCpuTime,
                expectedExcutionTime);
    }

    final public void testOnDraw() {
        V v = getWidgetInstance();
        Bitmap b = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        int iteration = ((HtcWidgetPerformanceTest) mTestCaseClass
                .getAnnotation(HtcWidgetPerformanceTest.class)).drawIteration();
        int expectedCpuTime = ((HtcWidgetPerformanceTest) mTestCaseClass
                .getAnnotation(HtcWidgetPerformanceTest.class)).drawCpuTime();
        int expectedExcutionTime = ((HtcWidgetPerformanceTest) mTestCaseClass
                .getAnnotation(HtcWidgetPerformanceTest.class))
                .drawExecutionTime();

        PerformanceUtil.start(getInstrumentation());
        for (int i = 0; i < iteration; i++) {
            v.draw(c);
        }
        PerformanceUtil.stop(getInstrumentation());
        PerformanceUtil.assertAllTypeTime(this, expectedCpuTime,
                expectedExcutionTime);
    }
}
