package nut.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Test marks a method as part of the test.
 *
 * enabled             Whether methods on this class/method are enabled.
 * expectedExceptions  The list of exceptions that a test method is expected to throw.
 *                     If no exception or a different than one on this list is thrown, this test will be marked a failure.
 * invocationCount     The number of times this method should be invoked.
 * invocationTimeOut   The maximum number of milliseconds this test should take for the cumulated time of all the invocationcounts.
 *                     This attribute will be ignored if invocationCount is not specified.
 * timeOut             The maximum number of milliseconds this test should take.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {

  /**
   * Whether methods on this class/method are enabled.
   *
   * @return the value (default true)
   */
  boolean enabled() default true;

  /**
   * The maximum number of milliseconds this test should take. If it hasn't returned after this
   * time, it will be marked as a FAIL.
   *
   * @return the value (default 0)
   */
  long timeOut() default 0;

  /**
   * The maximum number of milliseconds that the total number of invocations on this test method
   * should take. This annotation will be ignored if the attribute invocationCount is not specified
   * on this method. If it hasn't returned after this time, it will be marked as a FAIL.
   *
   * @return the value (default 0)
   */
  long invocationTimeOut() default 0;

  /**
   * The number of times this method should be invoked.
   *
   * @return the value (default 1)
   */
  int invocationCount() default 1;

  /**
   * The list of exceptions that a test method is expected to throw. If no exception or a different
   * than one on this list is thrown, this test will be marked a failure.
   *
   * @return the value
   */
  Class[] expectedExceptions() default {};

}
