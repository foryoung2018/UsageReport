/**
 *
 */
package com.htc.test.util;

import android.app.Instrumentation;
import android.os.Bundle;
import android.test.InstrumentationTestCase;

import junit.framework.Assert;

import java.lang.reflect.Field;

/**
 * @author felka
 *
 */
public class PerformanceUtil {
    private static Object getPerformanceCollector(Instrumentation inst) {
        if (null == inst)
            return null;

        try {
            Field f = Instrumentation.class
                    .getDeclaredField("mPerformanceCollector");
            f.setAccessible(true);
            return f.get(inst);
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static String getTimeKey(int type) {
        switch (type) {
        case PerformanceAssertProvider.CPU_TIME:
            return "cpu_time";
        case PerformanceAssertProvider.EXECUTION_TIME:
            return "execution_time";
        default:
            return null;
        }
    }

    public static long getTime(Instrumentation instrumentation, int type) {
        if (null == instrumentation)
            return Long.MAX_VALUE;

        String key = getTimeKey(type);
        if (null == key)
            return Long.MAX_VALUE;

        return getPerformanceMetrics(instrumentation).getLong(key,
                Long.MAX_VALUE);
    }

    public static void prepare(Instrumentation inst) {
        if (null == inst)
            return;
        Object collector = getPerformanceCollector(inst);
        if (null == collector)
            inst.setAutomaticPerformanceSnapshots();
    }

    public static void start(Instrumentation inst) {
        if (null == inst)
            return;
        prepare(inst);
        inst.startPerformanceSnapshot();
    }

    public static void stop(Instrumentation inst) {
        if (null == inst)
            return;
        inst.endPerformanceSnapshot();
    }

    public static Bundle getPerformanceMetrics(Instrumentation inst) {
        Bundle retBundle = null;

        if (null == inst)
            return retBundle;

        try {
            Field f = Instrumentation.class.getDeclaredField("mPerfMetrics");
            f.setAccessible(true);
            retBundle = (Bundle) f.get(inst);
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return retBundle;
    }

    public interface PerformanceAssertProvider {
        static public int CPU_TIME = 1;
        static public int EXECUTION_TIME = 2;

        int getTimeType();

        String getFailMessage(long timeSpend);

        boolean comparePerformance(long timeSpend);
    }

    private static void assertTime(InstrumentationTestCase testcase,
            PerformanceAssertProvider assertProvider) {
        long timeSpend = getTime(testcase.getInstrumentation(),
                assertProvider.getTimeType());
        boolean result = assertProvider.comparePerformance(timeSpend);
        Assert.assertTrue(assertProvider.getFailMessage(timeSpend), result);
    }

    /*
     * @Deprecated Please use assertTime(InstrumentationTestCase testcase, final
     * int timeType, final long expected) instead.
     */
    public static void assertTime(InstrumentationTestCase testcase,
            boolean bCpuTime, String msg, long expected) {
        long lTime = getTime(testcase.getInstrumentation(),
                (bCpuTime) ? PerformanceAssertProvider.CPU_TIME
                        : PerformanceAssertProvider.EXECUTION_TIME);
        Assert.assertTrue("expected = <" + expected
                + ((bCpuTime) ? "> cpuTime=<" : "> excutionTime=<") + lTime
                + ((null == msg) ? ">" : (">, " + msg)), lTime <= expected);
    }

    public static void assertCpuTime(InstrumentationTestCase testcase,
            long expected) {
        assertTime(testcase, PerformanceAssertProvider.CPU_TIME, expected);
    }

    public static void assertExcutionTime(InstrumentationTestCase testcase,
            long expected) {
        assertTime(testcase, PerformanceAssertProvider.EXECUTION_TIME, expected);
    }

    public static void assertAllTypeTime(InstrumentationTestCase testcase,
            long expectedCpuTime, long expectedExcutionTime) {
        assertTime(testcase, PerformanceAssertProvider.CPU_TIME,
                expectedCpuTime);
        assertTime(testcase, PerformanceAssertProvider.EXECUTION_TIME,
                expectedExcutionTime);
    }

    public static void assertTime(InstrumentationTestCase testcase,
            final int timeType, final long expected) {
        assertTime(testcase, new PerformanceAssertProvider() {
            @Override
            public int getTimeType() {
                // TODO Auto-generated method stub
                return timeType;
            }

            @Override
            public String getFailMessage(long timeSpend) {
                // TODO Auto-generated method stub

                StringBuilder sb = new StringBuilder(
                        "measure time must larger or eqaul then expected time");
                sb.append("expected ");
                sb.append(getTimeKey(timeType));
                sb.append("<");
                sb.append(expected);
                sb.append(">");

                sb.append("measure ");
                sb.append(getTimeKey(timeType));
                sb.append("<");
                sb.append(timeSpend);
                sb.append(">");

                return sb.toString();
            }

            @Override
            public boolean comparePerformance(long timeSpend) {
                // TODO Auto-generated method stub
                return timeSpend <= expected;
            }

        });
    }
}
