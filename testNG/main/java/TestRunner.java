package nut;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;

import java.util.ArrayList;
import java.util.List;

public class TestRunner
{
    public static void main( String[] args )
    {
      /*
        arg1 is testSuiteFileName
        arg2 is testReportDirectory
      */
        if( args.length != 2 ) {
            System.exit(1);
        }
        String suiteFileName   = args[0];
        String reportDirectory = args[1];

        TestNG tng = new TestNG();
        TestListenerAdapter tla = new nut.TestListener();
        tng.addListener(tla);
        tng.setOutputDirectory( reportDirectory );
        List<String> suites = new ArrayList<String>();
        suites.add( suiteFileName );
        tng.setTestSuites(suites);
        tng.run();
        if ( tng.hasFailure() ) {
            //  throw new Exception();
            System.exit(1);
        }
        System.exit(0);
    }
}
