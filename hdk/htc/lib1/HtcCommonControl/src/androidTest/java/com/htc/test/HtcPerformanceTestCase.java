package com.htc.test;

import android.test.ActivityInstrumentationTestCase2;

import com.htc.test.util.PerformanceUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class HtcPerformanceTestCase extends
        ActivityInstrumentationTestCase2 {

    public HtcPerformanceTestCase(Class activityClass) {
        super(activityClass);
        // TODO Auto-generated constructor stub
    }

    /*
     * Run performance testing if there are HtcPerformance annotation.
     *
     * @see android.test.InstrumentationTestCase#runTest()
     */
    @Override
    protected void runTest() throws Throwable {
        // TODO Auto-generated method stub
        String fName = getName();
        assertNotNull(fName);
        Method method = null;
        try {
            // use getMethod to get all public inherited
            // methods. getDeclaredMethods returns all
            // methods of this class but excludes the
            // inherited ones.
            method = getClass().getMethod(fName, (Class[]) null);
        } catch (NoSuchMethodException e) {
            fail("Method \"" + fName + "\" not found");
        }

        if (!Modifier.isPublic(method.getModifiers())) {
            fail("Method \"" + fName + "\" should be public");
        }

        boolean isHtcPerformanceTest = false;
        int iteration = 1;
        int cpuTime = 1;
        int cpuTimeMinBound = -1;
        int cpuTimeMaxBound = -1;
        int excutionTime = 1;
        int excutionTimeMinBound = -1;
        int excutionTimeMaxBound = -1;
        if (method.isAnnotationPresent(HtcPerformanceTest.class)) {
            iteration = method.getAnnotation(HtcPerformanceTest.class)
                    .iteration();
            cpuTime = method.getAnnotation(HtcPerformanceTest.class).cpuTime();
            cpuTimeMinBound = method.getAnnotation(HtcPerformanceTest.class)
                    .cpuTimeMinBound();
            cpuTimeMaxBound = method.getAnnotation(HtcPerformanceTest.class)
                    .cpuTimeMaxBound();
            excutionTime = method.getAnnotation(HtcPerformanceTest.class)
                    .executionTime();
            excutionTimeMinBound = method.getAnnotation(
                    HtcPerformanceTest.class).executionTimeMinBound();
            excutionTimeMaxBound = method.getAnnotation(
                    HtcPerformanceTest.class).executionTimeMaxBound();
            isHtcPerformanceTest = true;
        }

        if (isHtcPerformanceTest) {
            PerformanceUtil.start(getInstrumentation());
        }

        for (int i = 0; i < iteration; i++) {
            super.runTest();
        }

        if (isHtcPerformanceTest) {
            PerformanceUtil.stop(getInstrumentation());
            PerformanceUtil.assertAllTypeTime(this, cpuTime, excutionTime);
        }
    }
}
