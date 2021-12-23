package main.java.qa.ios.features;

import main.java.qa.ios.main.TestBase;
import main.java.qa.ios.navigation.Menu;
import main.java.qa.ios.navigation.SelectMovieFromCategory;

public class WithoutSignedIn extends TestBase {
	
	public WithoutSignedIn goToMenu() throws Exception {
		
		Menu.withoutSignedIn();
		SelectMovieFromCategory.movieFromCategory();
		return new WithoutSignedIn();
		
	}
}
