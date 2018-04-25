package com.htc.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used on an
 * {@link android.test.InstrumentationTestCase}'s test methods. When the
 * annotation is present, the test method is re-executed if the test fails. The
 * total number of executions is specified by the tolerance and defaults to 1.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HtcPerformanceTest {
    /**
     * Indicates how many times a test can run and fail before being reported as
     * a failed test. If the tolerance factor is less than 1, the test runs only
     * once.
     *
     * @return The total number of allowed run, the default is 1.
     */
    int iteration() default 1;

    int cpuTime() default 1;

    int cpuTimeMinBound() default -1;

    int cpuTimeMaxBound() default -1;

    int executionTime() default 1;

    int executionTimeMinBound() default -1;

    int executionTimeMaxBound() default -1;
}
