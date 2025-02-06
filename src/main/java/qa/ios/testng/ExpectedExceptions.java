package qa.ios.testng;

public class ExpectedExceptions extends Exception{

	private static final long serialVersionUID = 1L;
	
	public ExpectedExceptions(){}
	
	public ExpectedExceptions(String message){
		super(message);
		
	}
}
