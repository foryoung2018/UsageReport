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
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
public @interface HtcWidgetPerformanceTest {
    /**
     * Indicates how many times a test can run and fail before being reported as
     * a failed test. If the tolerance factor is less than 1, the test runs only
     * once.
     *
     * @return The total number of allowed run, the default is 1.
     */
    int contructorIteration() default 1;

    int measureIteration() default 1;

    int layoutIteration() default 1;

    int drawIteration() default 1;

    int contructorCpuTime() default 1;

    int measureCpuTime() default 1;

    int layoutCpuTime() default 1;

    int drawCpuTime() default 1;

    int contructorExecutionTime() default 1;

    int measureExecutionTime() default 1;

    int layoutExecutionTime() default 1;

    int drawExecutionTime() default 1;

}
