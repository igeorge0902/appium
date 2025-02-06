package qa.ios.features;

import qa.ios.base.TestBase;
import qa.ios.navigation.Menu;
import qa.ios.navigation.SelectMovieFromCategory;

public class WithoutSignedIn extends TestBase {
	
	public WithoutSignedIn goToMenu() throws Exception {
		
		Menu.withoutSignedIn();
		SelectMovieFromCategory.movieFromCategory();
		return new WithoutSignedIn();
		
	}
}
