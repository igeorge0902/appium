package qa.ios.testng;

import java.util.ArrayList;
import java.util.List;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.internal.Utils;
import qa.ios.base.TestBase;

public class TestMethodListener extends TestBase implements IInvokedMethodListener{
	
 
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
 
    	if(method.isConfigurationMethod()){
    		TestMethodErrorBuffer.remove(); // remove stale
            System.out.println("Stale removed at beforeInvocation");
    		if(TestMethodErrorBuffer.get()!=null){
    			
    		try {
				throw new InterruptedException("Stale error buffer detected!");
				} catch (InterruptedException e) {
						e.printStackTrace();
			}
		}
    		TestMethodErrorBuffer.set(new ArrayList<Throwable>()); // each test method will have its own error buffer
		}
    	
    	if(method.isTestMethod()){
    		TestMethodErrorBuffer.remove(); // remove stale
            System.out.println("Stale removed at beforeInvocation");
    		if(TestMethodErrorBuffer.get()!=null){
    			
    		try {
				throw new InterruptedException("Stale error buffer detected!");
				} catch (InterruptedException e) {
						e.printStackTrace();
			}
		}
    		TestMethodErrorBuffer.set(new ArrayList<Throwable>()); // each test method will have its own error buffer
		}
	}
 
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
 
        if(method.isTestMethod()){
 
            List<Throwable> lThrowable = TestBase.getVerificationFailures();
 
            /* if there are verification failures */
            if(lThrowable.size() > 0){
 
                /* set test result to failure */
                testResult.setStatus(ITestResult.FAILURE);
 
                /* if there is assertion error/exception then add it to throwable list */
                if(testResult.getThrowable() != null){
                    lThrowable.add(testResult.getThrowable());
                }
 
                int size = lThrowable.size();
 
                /* if there is only one throwable then set it directly to test result */
                if(size == 0){
                    testResult.setThrowable(lThrowable.get(0));
                }else{
 
                    StringBuffer failureMessage = new StringBuffer("Multiple failures (").append(size).append(")\n");
                    StringBuffer fullStack = new StringBuffer();
 
                    for(int i =0 ; i < size-1; i++){
                        failureMessage.append("(").append(i+1).append(")").append(lThrowable.get(i).getClass().getName()).append(":").append(lThrowable.get(i).getMessage()).append("\n");                     
                        fullStack.append("Failure ").append(i+1).append(" of ").append(size).append("\n"); 
                        fullStack.append(Utils.stackTrace(lThrowable.get(i),false)[1]).append("\n");
                        
                    }
 
                    fullStack.append("Failure ").append(size).append(" of ").append(size).append("\n");
                    Throwable last = lThrowable.get(size-1);                   
                    failureMessage.append("(").append(size).append(")").append(last.getClass().getName()).append(":").append(last.getMessage()).append("\n\n");
                     
                    fullStack.append(last.toString());
 
                    testResult.setThrowable(new Throwable(failureMessage.toString() + fullStack.toString()));
                    testResult.getThrowable().setStackTrace(last.getStackTrace());
                }
 
            }
             
            TestMethodErrorBuffer.remove(); // remove stale
            System.out.println("Stale removed afterInvocation");

             
        }
    }
 
}