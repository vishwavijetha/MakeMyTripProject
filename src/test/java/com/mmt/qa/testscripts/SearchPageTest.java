package com.mmt.qa.testscripts;

import java.util.HashMap;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.mmt.qa.basesetup.DriverBase;
import com.mmt.qa.pages.HomePage;
import com.mmt.qa.pages.SearchPage;
import com.mmt.qa.util.CalendarUtil;
import com.mmt.qa.util.RandomNumberGenerator;
import com.mmt.qa.util.TestNGListener;

@Listeners(TestNGListener.class)
public class SearchPageTest extends DriverBase {

	HomePage homePage;
	SearchPage searchPage;

	@BeforeTest
	public void setUp() {
		launchBrowser();
		openURL();
		homePage = new HomePage(super.driver);
		/**
		 * Precondition
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
	}

	@Test(enabled = true, priority = 1, description = "Verify SearchPage title")
	public void verifyTitle() {
		/**
		 * Verify Page title
		 */
		Assert.assertEquals(getPageTitle(), testData.getProperty("searchPageTitleExpected"));
	}

	@Test(enabled = true, priority = 2, dataProvider = "getStops", description = "Display total no. of Departure and Return flights for different Stops")
	public void displayRoundTripFlights(String stops) throws InterruptedException {
		/**
		 * 1. Select Stops i.e, DEFAULT | NONSTOP | ONESTOP
		 */
		searchPage.selectStops(stops);
		/**
		 * 2. Display total number of Departure and Return flights
		 */
		int totalFlightsCount = searchPage.displayRoundTripFlightsCount(stops);
		/**
		 * 3. Verify Search result
		 */
		Assert.assertTrue(totalFlightsCount > 0, "Total RoundTrip Flights count should be greater than 0");
	}

	@DataProvider(name = "getStops")
	public String[] getStops() {
		return new String[] { "DEFAULT", "NONSTOP", "ONESTOP" };
	}

	@Test(enabled = true, priority = 3, dataProvider = "getRandomRoundTripFlightsTop10Index", description = "Select random RoundTrip flights by index and verify prices")
	public void selectRandomRoundTripFlightsByIndexAndVerifyPrice(int departureIndex, int returnIndex) {
		/**
		 * 1. Clear filters
		 */
		searchPage.selectStops("DEFAULT");

		/**
		 * 2. Select Departure and Return flights by random indexes provided by Data
		 * Provider
		 */
		HashMap<String, String> selectionMap = searchPage.selectRoundTripFlightsByIndex(departureIndex, returnIndex);
		/**
		 * 3. Verify selected Departure flight price with Departure flight footer price
		 */
		String actualPrice, expectedPrice;
		actualPrice = selectionMap.get("DeparturePrice");
		expectedPrice = searchPage.getFooterDepartureFlightPrice();
		Assert.assertTrue(searchPage.verifyPrice(actualPrice, expectedPrice),
				"Price mismatch: Selected Departure flight price = " + actualPrice
						+ ", Departure flight footer price = " + expectedPrice);
		/**
		 * 4. Verify selected Return flight price with Return flight footer price
		 */
		actualPrice = selectionMap.get("ReturnPrice");
		expectedPrice = searchPage.getFooterReturnFlightPrice();
		Assert.assertTrue(searchPage.verifyPrice(actualPrice, expectedPrice),
				"Price mismatch: Selected Return flight price = " + actualPrice + ", Return flight footer price = "
						+ expectedPrice);
		/**
		 * 5. Verify footer total price = Departure footer price + Return footer price
		 */
		String actualDepartureFooterPrice, actualReturnFooterPrice, actualTotalFooterPrice, discountAppliedIfAny;
		actualDepartureFooterPrice = searchPage.getFooterDepartureFlightPrice();
		actualReturnFooterPrice = searchPage.getFooterReturnFlightPrice();
		actualTotalFooterPrice = searchPage.getFooterTotalFlightFare().get("FinalTotalPrice");
		discountAppliedIfAny = searchPage.getFooterTotalFlightFare().get("DiscountForTotalPrice");
		Assert.assertTrue(
				searchPage.verifyTotalPrice(actualDepartureFooterPrice, actualReturnFooterPrice, actualTotalFooterPrice,
						discountAppliedIfAny),
				"Total footer price mismatch: Departure footer price = " + actualDepartureFooterPrice
						+ ", Return footer price = " + actualReturnFooterPrice + ", Expected Total footer price = ("
						+ actualTotalFooterPrice + " + " + discountAppliedIfAny + ")");
	}

	@DataProvider(name = "getRandomRoundTripFlightsTop10Index")
	public Integer[][] getRandomRoundTripFlightsTop10Index() {
		/**
		 * @variable departureSize {Total count of Departure Flights}
		 * @variable returnSize {Total count of Return Flights}
		 * @variable maxDeparture {Max range value for Departure Flight}
		 * @variable maxReturn {Max range value for Return Flight}
		 */
		Integer departureSize = searchPage.getDepartureFlights().size();
		Integer returnSize = searchPage.getReturnFlights().size();
		Integer maxDeparture = departureSize > 10 ? 10 : departureSize;
		Integer maxReturn = returnSize > 10 ? 10 : returnSize;
		Integer min = 1;

		return new Integer[][] {
				{ RandomNumberGenerator.getRandomNumber(maxDeparture, min),
						RandomNumberGenerator.getRandomNumber(maxReturn, min) },
				{ RandomNumberGenerator.getRandomNumber(maxDeparture, min),
						RandomNumberGenerator.getRandomNumber(maxReturn, min) },
				{ RandomNumberGenerator.getRandomNumber(maxDeparture, min),
						RandomNumberGenerator.getRandomNumber(maxReturn, min) },
				{ RandomNumberGenerator.getRandomNumber(maxDeparture, min),
						RandomNumberGenerator.getRandomNumber(maxReturn, min) } };
	}

	@AfterTest
	public void tearDown() {
		quitBrowser();
	}
}
