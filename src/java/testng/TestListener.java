package nut.testng;

import nut.logging.Log;

import java.io.PrintStream;

import org.testng.TestListenerAdapter;
import org.testng.ITestListener;
import org.testng.ITestContext;
import org.testng.ITestResult;

public class TestListener extends TestListenerAdapter {

  @Override
  public void onStart(ITestContext tc) {
    System.setOut( nut.goals.test.getSysOut() );
    Log log = nut.goals.test.getLog();
    log.info("   Testing " + tc.getName());
  }
 
  @Override
  public void onFinish(ITestContext tc) {
    //Log log = nut.goals.test.getLog();
    //log.print("\n");
    System.setOut( nut.goals.test.getStdOut() );
  }
 
  @Override
  public void onTestFailure(ITestResult tr) {
    Log log = nut.goals.test.getLog();
    log.error("     " + tr.getName()+ " failed");
  }
 
  @Override
  public void onTestSkipped(ITestResult tr) {
    Log log = nut.goals.test.getLog();
    log.warn("      Skip " + tr.getName());
  }
/*
  @Override
  public void onTestSuccess(ITestResult tr) {
    Log log = nut.goals.test.getLog();
    log.print(".");
  }
*/

}

