package nut;

import java.io.PrintStream;

import org.testng.TestListenerAdapter;
import org.testng.ITestContext;
import org.testng.ITestResult;

public class TestListener extends TestListenerAdapter {
  private static PrintStream sysout;

  @Override
  public void onStart(ITestContext tc) {
    // catch stdout
    sysout = System.out;
    //stdout = new PrintStream(new FileOutputStream("/dev/null"));
    //System.setOut(stdout);
    info("Testing " + tc.getName());
  }

  @Override
  public void onFinish(ITestContext tc) {
    // restore stdout
    System.setOut(sysout);
  }

  @Override
  public void onTestStart(ITestResult result) {
    // This method is intentionally empty.
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    // This method is intentionally empty.
  }

  @Override
  public void onTestFailure(ITestResult tr) {
    error("    " + tr.getInstanceName() + "." + tr.getName());
  }

  @Override
  public void onTestSkipped(ITestResult tr) {
    warn( "    Skip " + tr.getInstanceName() + "." + tr.getName());
  }

  @Override
  public void onTestSuccess(ITestResult tr) {
    success( "    " + tr.getInstanceName() + "." + tr.getName());
  }

  private void print( String prefix, String content )
  {
      System.out.println( "[" + prefix + "] " + content );
  }

  private void print( String content )
  {
      System.out.print( content );
  }

  private void info( String content )
  {
      print( " info  ", content );
  }

  public void success( String content )
  {
      print( "\033[1;32m" );
      print( "success", content );
      print( "\033[1;37m" );
  }

  private void warn( String content )
  {
      print( "\033[1;33m" );
      print( " warn  ", content );
      print( "\033[1;37m" );
  }

  private void error( String content )
  {
      print( "\033[1;31m" );
      print( " error ", content );
      print( "\033[1;37m" );
  }
}
