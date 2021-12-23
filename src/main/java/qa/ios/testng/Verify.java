package main.java.qa.ios.testng;

import java.util.Collection;
import java.util.Iterator;

import main.java.qa.ios.main.TestBase;

import org.testng.Assert;


/**
 * Verification tool class. Wrapper class around TestNG Assert.
 * @author <a href='mailto:gadigeppa.code@gmail.com'>Gadigeppa Jattennavar</a>
 */
public class Verify {

	protected Verify() {
		// hide constructor
	}
	
	static public void verifyTrue(boolean condition, String message) {		  
		try{
			Assert.assertTrue(condition, message);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}		  
	}

	static public void verifyTrue(boolean condition) {
		try{
			Assert.assertTrue(condition);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyFalse(boolean condition, String message) {
		try{
			Assert.assertFalse(condition, message);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyFalse(boolean condition) {
		try{
			Assert.assertFalse(condition);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void fail(String message, Throwable realCause) {
		try{
			Assert.fail(message, realCause);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void fail(String message) {
		try{
			Assert.fail(message);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void fail() {
		try{
			Assert.fail();
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyEquals(Object actual, Object expected, String message) {
		try{
			Assert.assertEquals(actual, expected, message);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyEquals(Object actual, Object expected) {
		try{
			Assert.assertEquals(actual, expected);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyEquals(String actual, String expected, String message) {
		try{
			Assert.assertEquals(actual, expected, message);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyEquals(String actual, String expected) {
		try{
			Assert.assertEquals(actual, expected);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}


	static public void verifyEquals(boolean actual, boolean expected, String message) {
		try{
			Assert.assertEquals(actual, expected, message);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}


	static public void verifyNotNull(Object object) {
		try{
			Assert.assertNotNull(object);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyNotNull(Object object, String message) {
		try{
			Assert.assertNotNull(object,message);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyNull(Object object) {
		try{
			Assert.assertNull(object);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyNull(Object object, String message) {
		try{
			Assert.assertNull(object,message);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifySame(Object actual, Object expected, String message) {
		try{
			Assert.assertSame(actual,expected,message);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifySame(Object actual, Object expected) {
		try{
			Assert.assertSame(actual,expected);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyNotSame(Object actual, Object expected, String message) {
		try{
			Assert.assertNotSame(actual,expected,message);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyNotSame(Object actual, Object expected) {
		try{
			Assert.assertNotSame(actual,expected);
		}catch(AssertionError e){
			//addToErrorBuffer(e);
			TestBase.addVerificationFailure(e);
		}
	}
	
	static public boolean verifyNotSameBoolean(Object actual, Object expected) {
		try{
			Assert.assertNotSame(actual,expected);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
		return true;
	}

	static public void verifyEquals(Collection<?> actual, Collection<?> expected) {
		try{
			Assert.assertEquals(actual,expected);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyEquals(Collection<?> actual, Collection<?> expected, String message) {
		try{
			Assert.assertEquals(actual,expected,message);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyEquals(Iterator<?> actual, Iterator<?> expected) {
		try{
			Assert.assertEquals(actual,expected);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyEquals(Iterator<?> actual, Iterator<?> expected, String message) {
		try{
			Assert.assertEquals(actual,expected,message);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyEquals(Iterable<?> actual, Iterable<?> expected) {
		try{
			Assert.assertEquals(actual,expected);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyEquals(Iterable<?> actual, Iterable<?> expected, String message) {
		try{
			Assert.assertEquals(actual,expected,message);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyEquals(Object[] actual, Object[] expected, String message) {
		try{
			Assert.assertEquals(actual,expected,message);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyEqualsNoOrder(Object[] actual, Object[] expected, String message) {
		try{
			Assert.assertEqualsNoOrder(actual,expected,message);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyEquals(Object[] actual, Object[] expected) {
		try{
			Assert.assertEquals(actual,expected);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	static public void verifyEqualsNoOrder(Object[] actual, Object[] expected) {
		try{
			Assert.assertEqualsNoOrder(actual,expected);
		}catch(AssertionError e){
			addToErrorBuffer(e);
		}
	}

	public static void addToErrorBuffer(Throwable e){	  

		try{				

			VerificationError verificationError = new VerificationError(e.getMessage());

			verificationError.setStackTrace(e.getStackTrace());

			TestMethodErrorBuffer.get().add(verificationError);

		}catch(NullPointerException ex){

			throw new RuntimeException("Please let TestNG know about " + TestMethodListener.class.getName() + " listener for verify statements to work. For more information go to http://testng.org/doc/documentation-main.html#testng-listeners");
		}

	}

}
