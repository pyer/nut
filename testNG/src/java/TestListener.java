package nut;

import java.io.FileOutputStream;
import java.io.PrintStream;

import org.testng.TestListenerAdapter;
import org.testng.ITestListener;
import org.testng.ITestContext;
import org.testng.ITestResult;

public class TestListener extends TestListenerAdapter {
  private static PrintStream sysout;
  //private static PrintStream stdout;

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
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
  }

  @Override
  public void onTestFailure(ITestResult tr) {
    error("    " + tr.getName()+ " failed");
  }

  @Override
  public void onTestSkipped(ITestResult tr) {
    warn( "    Skip " + tr.getName());
  }

  @Override
  public void onTestSuccess(ITestResult tr) {
    info( "    " + tr.getName()+ " succeded");
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
