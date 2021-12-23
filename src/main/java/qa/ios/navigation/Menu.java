package main.java.qa.ios.navigation;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import main.java.qa.ios.main.TestBase;
import main.java.qa.ios.page.PageObjects;
import main.java.qa.ios.util.Helpers;

public class Menu extends TestBase{
	
	public static Menu withoutSignedIn() throws Exception{
		
		PageObjects.menu().click();
		Log.info("Menu clicked and hopefully opened! :)");
    	
		PageObjects.goToMenu().click();
		PageObjects.clickAlert().click();
    	PageObjects.checkUser("no logged in user");
    	
		return new Menu();
		
	}
	
	public static Menu logIn() throws Exception{
		
		driver.findElement(By.xpath("//XCUIElementTypeStaticText[@name='Back']")).click();
		driver.findElement(By.xpath("//XCUIElementTypeButton[@name='ChangeUser']")).click();
    	driver.findElement(By.xpath("(//XCUIElementTypeOther[@name='Login'])[1]/XCUIElementTypeOther[2]/XCUIElementTypeTextField")).sendKeys("GG");	
    	driver.findElement(By.xpath("(//XCUIElementTypeOther[@name='Login'])[1]/XCUIElementTypeOther[2]/XCUIElementTypeSecureTextField")).sendKeys("hola");
    	driver.findElement(By.xpath("//XCUIElementTypeButton[@name='Login']")).click();
    	
		PageObjects.menu().click();
		Log.info("Menu clicked and hopefully opened! :)");
		PageObjects.goToMenu().click();
		//TODO: add wait
    	PageObjects.checkUser("GG");
    	PageObjects.back();

    	return new Menu();
		
	}
	
}
