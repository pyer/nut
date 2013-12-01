package nut.plugins;

import nut.logging.Log;
//import nut.plugins.testRunner;

import java.io.PrintStream;

import org.testng.TestListenerAdapter;
import org.testng.ITestListener;
import org.testng.ITestContext;
import org.testng.ITestResult;

public class TestListener extends TestListenerAdapter {

  @Override
  public void onStart(ITestContext tc) {
    System.setOut( nut.plugins.testRunner.getSysOut() );
    Log log = nut.plugins.testRunner.getLog();
    log.info("   Testing " + tc.getName());
  }
 
  @Override
  public void onFinish(ITestContext tc) {
    //Log log = nut.plugins.testRunner.getLog();
    //log.print("\n");
    System.setOut( nut.plugins.testRunner.getStdOut() );
  }
 
  @Override
  public void onTestFailure(ITestResult tr) {
    Log log = nut.plugins.testRunner.getLog();
    log.error("     " + tr.getName()+ " failed");
  }
 
  @Override
  public void onTestSkipped(ITestResult tr) {
    Log log = nut.plugins.testRunner.getLog();
    log.warn("      Skip " + tr.getName());
  }
/*
  @Override
  public void onTestSuccess(ITestResult tr) {
    Log log = nut.plugins.testRunner.getLog();
    log.print(".");
  }
*/

}

