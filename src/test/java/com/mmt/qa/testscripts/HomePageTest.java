package com.mmt.qa.testscripts;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.mmt.qa.basesetup.DriverBase;
import com.mmt.qa.pages.HomePage;
import com.mmt.qa.pages.SearchPage;
import com.mmt.qa.util.CalendarUtil;
import com.mmt.qa.util.TestNGListener;

@Listeners(TestNGListener.class)
public class HomePageTest extends DriverBase {

	HomePage homePage;
	SearchPage searchPage;

	@BeforeTest
	public void setUp() {
		launchBrowser();
		openURL();
		homePage = new HomePage(super.driver);
	}

	@Test(enabled = true, priority = 1, description = "Verify HomePage title")
	public void verifyTitle() {
		/**
		 * Verify Page title
		 */
		Assert.assertEquals(getPageTitle(), testData.getProperty("homePageTitleExpected"));
	}
	
	@Test(enabled = true, priority = 2, description = "Verify HomePage basic Search functionality")
	public void verifyHomePageSearch() throws Exception {
		/**
		 * 1. Select From City as 'DELHI'
		 * 2. Select To City as 'BANGALORE'
		 * 3. Select Departure date as Present date
		 * 4. Select Return date as Future date (7 days added to the present date)
		 */
		String fromCity = testData.getProperty("fromcity");
		String toCity = testData.getProperty("tocity"); 
		String departureDate = CalendarUtil.getTodaysDate(); 
		String returnDate = CalendarUtil.getFutureDateFromToday(7); 
		searchPage = homePage.searchRoundTripFlights(fromCity, toCity, departureDate, returnDate);
		/**
		 * 5. Verify Search result
		 */
		boolean searchResult = searchPage.verifySearchResult(fromCity, toCity, departureDate, returnDate);
		Assert.assertTrue(searchResult);
	}
	
	@AfterTest
	public void tearDown() {
		quitBrowser();
	}
}
