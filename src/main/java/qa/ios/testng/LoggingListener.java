package qa.ios.testng;

/*
 * For custom logging messages after tests have finished.
 * 
 * 				under consideration yet
 */


import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;
import qa.ios.base.TestBase;

public class LoggingListener extends TestListenerAdapter {
		  	 
		  @Override
		  public void onTestFailure(ITestResult tr) {
		    
		    
				// call the superclass
		        super.onTestFailure(tr);
		        super.getFailedTests();
		        	        
	        Reporter.log("Fuck<br>");
	        TestBase.Log.info("Fuck");
		  }
		 
		  @Override
		  public void onTestSkipped(ITestResult tr) {
			  
				// call the superclass
		        super.onTestSkipped(tr);
		        super.getSkippedTests();
		     
		     Reporter.log("Shit happens");
		     TestBase.Log.info("Shit happens");
		  }
		 
		  @Override
		  public void onTestSuccess(ITestResult tr) {
			  
				// call the superclass
		        super.onTestSuccess(tr);
		        super.getPassedTests();

		    Reporter.log("Minden fasza");
		    TestBase.Log.info("Minden fasza");
		  }
		  
		  @Override
		  public void onFinish(ITestContext context) {
			  
				// call the superclass
		        super.onFinish(context); 
		        super.getAllTestMethods();
		        
		  }
		 
		} 


