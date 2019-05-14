package com.mmt.qa.pages;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.mmt.qa.basesetup.DriverBase;
import com.mmt.qa.util.CalendarUtil;

public class SearchPage extends DriverBase {

	public final static Logger logger = Logger.getLogger(SearchPage.class);
	protected WebDriver driver;

	public SearchPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath = "//input[@id='fromCity']")
	private WebElement fromCitySearchMenu;

	public String getSelectedFromCity() {
		return getAttributeValue(fromCitySearchMenu, "value");
	}

	@FindBy(xpath = "//input[@id='toCity']")
	private WebElement toCitySearchMenu;

	public String getSelectedToCity() {
		return getAttributeValue(toCitySearchMenu, "value");
	}

	@FindBy(xpath = "//input[@id='departure']")
	private WebElement departureDate;

	public String getSelectedDepartureDate() {
		return getAttributeValue(departureDate, "value");
	}

	@FindBy(xpath = "//input[@id='return']")
	private WebElement returnDate;

	public String getSelectedReturnDate() {
		return getAttributeValue(returnDate, "value");
	}

	@FindBy(xpath = "//div[@id='fli_filter__stops']//span[text()='Non Stop']")
	private WebElement labelNonStop;

	@FindBy(xpath = "//div[@id='fli_filter__stops']//span[text()='1 Stop']")
	private WebElement labelOneStop;

	/**
	 * @method selectStops()
	 * @param stops[] = {ALL, NONSTOP, ONESTOP }
	 * @return
	 * @purpose Filter flights by Stops
	 */
	public void selectStops(String... stops) {

		boolean isNonStopSelected, isOneStopSelected;
		while (!labelNonStop.isDisplayed()) {
			scrollToTopUsingJS(driver);
		}
		
		for (String stop : stops) {

			while(stop != "" || stop != null) {
				isNonStopSelected = Boolean.valueOf(
						(boolean) executeJavaScript(driver, "return document.getElementById('filter_stop0').checked;"));
				isOneStopSelected = Boolean.valueOf(
						(boolean) executeJavaScript(driver, "return document.getElementById('filter_stop1').checked;"));

				if (stop.equalsIgnoreCase("NONSTOP") && !isNonStopSelected) {
					clickOnElement(labelNonStop);
				} else if (stop.equalsIgnoreCase("ONESTOP") && !isOneStopSelected) {
					clickOnElement(labelOneStop);
				} 
				if (stop.equalsIgnoreCase("NONSTOP") && isOneStopSelected) {
					clickOnElement(labelOneStop);
				} else if (stop.equalsIgnoreCase("ONESTOP") && isNonStopSelected) {
					clickOnElement(labelNonStop);
				} 
				if (stop.equalsIgnoreCase("DEFAULT") && isNonStopSelected){
					clickOnElement(labelNonStop);
				}
				if (stop.equalsIgnoreCase("DEFAULT") && isOneStopSelected){
					clickOnElement(labelOneStop);
				}
				break;
			}
		}
	}

	By departureList = By.xpath("//div[@id='ow_domrt-jrny']//div[contains(@class,'splitVw-listing')]/label");

	/**
	 * @method getDepartureFlights()
	 * @return List<WebElement> {Departure Flights}
	 * @purpose Find the {Departure Flights List} by scrolling till the last Flight
	 */
	public List<WebElement> getDepartureFlights() {

		List<WebElement> departureFlightsList = driver.findElements(departureList);
		int departureFlightSize = 1;
		int lastElementIndex = 0;
		while (departureFlightSize != departureFlightsList.size()) {
			lastElementIndex = departureFlightsList.size() - 1;
			scrollToWebElement(departureFlightsList.get(lastElementIndex));
			departureFlightSize = departureFlightsList.size();
			departureFlightsList = driver.findElements(departureList);
		}
		executeJavaScript(driver, "window.scrollBy(0,-document.body.scrollHeight);");
		return departureFlightsList;
	}

	By returnList = By.xpath("//div[@id='rt-domrt-jrny']//div[contains(@class,'splitVw-listing')]/label");

	/**
	 * @method getDepartureFlights()
	 * @return List<WebElement> {Return Flights}
	 * @purpose Find the {Return Flights List} by scrolling till the last Flight
	 */
	public List<WebElement> getReturnFlights() {

		List<WebElement> returnFlightsList = driver.findElements(returnList);
		int returnFlightSize = 1;
		int lastElementIndex = 0;
		while (returnFlightSize != returnFlightsList.size()) {
			lastElementIndex = returnFlightsList.size() - 1;
			scrollToWebElement(returnFlightsList.get(lastElementIndex));
			returnFlightSize = returnFlightsList.size();
			returnFlightsList = driver.findElements(returnList);
		}
		executeJavaScript(driver, "window.scrollBy(0,-document.body.scrollHeight);");
		return returnFlightsList;
	}

	/**
	 * @method displayRoundTripFlightsCount()
	 * @param stops[] = {ALL, NONSTOP, ONESTOP}
	 * @return
	 * @purpose Display Total no. of Departure and Return flights
	 */
	public int displayRoundTripFlightsCount(String... stops) {
		
		int departureFlightsCount,returnFlightsCount,totalFlightsCount = 0;
			
		for(String stop: stops) {
			while(stop != "" || stop != null) {
				departureFlightsCount = getDepartureFlights().size();
				returnFlightsCount = getReturnFlights().size();
				totalFlightsCount = departureFlightsCount + returnFlightsCount;
				logger.info("Total no. of [" + stop + "] Departure Flights: " + departureFlightsCount);
				logger.info("Total no. of [" + stop + "] Return Flights: " + returnFlightsCount);
				break;
			}
		}	
		return totalFlightsCount;
	}

	@FindBy(xpath = "//div[@id='ow_domrt-jrny']/div/div/label//p[@class='actual-price']")
	private List<WebElement> departurePriceList;

	@FindBy(xpath = "//div[@id='rt-domrt-jrny']/div/div/label//p[@class='actual-price']")
	private List<WebElement> returnPriceList;

	/**
	 * @method selectRoundTripFlightsByIndex()
	 * @param departureFlightIndex
	 * @param returnFlightIndex
	 * @return HashMap<String,String> {DeparturePrice, ReturnPrice}
	 * @purpose Select Departure and Return Flights by Index
	 */
	public HashMap<String, String> selectRoundTripFlightsByIndex(int departureFlightIndex, int returnFlightIndex) {
		HashMap<String, String> priceMap = new HashMap<String, String>();
		/**
		 * @purpose Get the complete Flights list by scrolling till end. Select the
		 *          Flights by index. Capture the Flights prices.
		 */

		WebElement departureFlightAtIndex = departurePriceList.get(departureFlightIndex - 1);
		scrollToWebElement(departureFlightAtIndex);
		clickOnElementUsingJS(driver, departureFlightAtIndex);

		WebElement returnFlightAtIndex = returnPriceList.get(returnFlightIndex - 1);
		scrollToWebElement(returnFlightAtIndex);
		clickOnElementUsingJS(driver, returnFlightAtIndex);

		String departurePrice = getText(departureFlightAtIndex);
		logger.info("Departure Flight price found at index: " + departureFlightIndex + " --> " + departurePrice);

		String returnPrice = getText(returnFlightAtIndex);
		logger.info("Return Flight price found at index: " + returnFlightIndex + " --> " + returnPrice);

		priceMap.put("DeparturePrice", departurePrice);
		priceMap.put("ReturnPrice", returnPrice);

		scrollToTopUsingJS(driver);
		return priceMap;
	}

	@FindBy(xpath = "//div[@class='splitVw-footer-left ']//p[@class='actual-price']")
	private WebElement footerDepartureFlightPrice;

	@FindBy(xpath = "//div[@class='splitVw-footer-right ']//p[@class='actual-price']")
	private WebElement footerReturnFlightPrice;

	@FindBy(xpath = "//div[@class='footer-fare']//span[@class='INR']/parent::*")
	private List<WebElement> footerTotalFlightFare;

	public String getFooterDepartureFlightPrice() {
		return getText(footerDepartureFlightPrice);
	}

	public String getFooterReturnFlightPrice() {
		return getText(footerReturnFlightPrice);
	}

	public HashMap<String, String> getFooterTotalFlightFare() {
		HashMap<String, String> footerTotalFareMap = new HashMap<String, String>();
		int totalFooterPriceList = footerTotalFlightFare.size();
		if (totalFooterPriceList > 1) {
			footerTotalFareMap.put("BeforeDiscountTotalPrice", getText(footerTotalFlightFare.get(0)));
			footerTotalFareMap.put("FinalTotalPrice", getText(footerTotalFlightFare.get(1)));
			footerTotalFareMap.put("DiscountForTotalPrice", getText(footerTotalFlightFare.get(2)));
		} else {
			footerTotalFareMap.put("BeforeDiscountTotalPrice", "");
			footerTotalFareMap.put("FinalTotalPrice", getText(footerTotalFlightFare.get(0)));
			footerTotalFareMap.put("DiscountForTotalPrice", "");
		}
		return footerTotalFareMap;
	}

	/**
	 * @method verifySearchResult()
	 * @param fromCity
	 * @param toCity
	 * @param departureDate
	 * @param returnDate
	 * @return boolean
	 * @purpose Verify the Search Result with the mentioned parameter values
	 */
	public boolean verifySearchResult(String fromCity, String toCity, String departureDate, String returnDate) {
		String actualFromCity = getSelectedFromCity();
		String actualToCity = getSelectedToCity();
		String actualDepartureDate = CalendarUtil.getFormattedDate(getSelectedDepartureDate());
		String aactualReturnDate = CalendarUtil.getFormattedDate(getSelectedReturnDate());

		logger.info("Actual 'From' city: " + actualFromCity + ", Expected 'From' city: " + fromCity);
		logger.info("Actual 'To' city: " + actualToCity + ", Expected 'To' city: " + toCity);
		logger.info("Actual Departure date: " + actualDepartureDate + ", Expected Departure date: " + departureDate);
		logger.info("Actual Return date: " + aactualReturnDate + ", Expected Return date: " + returnDate);

		if (actualFromCity.toLowerCase().contains(fromCity.toLowerCase())
				&& actualToCity.toLowerCase().contains(toCity.toLowerCase())
				&& actualDepartureDate.equalsIgnoreCase(departureDate)
				&& aactualReturnDate.equalsIgnoreCase(returnDate)) {
			return true;
		}
		return false;
	}

	/**
	 * @method verifyPrice()
	 * @param selectedPrice
	 * @param footerPrice
	 * @return boolean
	 * @purpose Compare price by values
	 */
	public boolean verifyPrice(String selectedPrice, String footerPrice) {
		boolean status = getFormattedPrice(selectedPrice) == getFormattedPrice(footerPrice);
		logger.info("Verify Price: SelectedPrice = " + selectedPrice + ", FooterPrice = " + footerPrice + " Status --> "
				+ status);
		return status;
	}

	/**
	 * @method verifyTotalPrice()
	 * @param actualPrice1
	 * @param actualPrice2
	 * @param expectedTotalPrice
	 * @return boolean
	 * @purpose Verify Total Price displayed in footer
	 */
	public boolean verifyTotalPrice(String actualPrice1, String actualPrice2, String expectedTotalPrice,
			String discountIfApplied) {
		int totalPriceActual = getFormattedPrice(actualPrice1) + getFormattedPrice(actualPrice2);
		int totalPriceExpected = getFormattedPrice(expectedTotalPrice);
		int discount = 0;

		if (discountIfApplied != "") {
			discount = getFormattedPrice(discountIfApplied);
			totalPriceExpected = totalPriceExpected + discount;
		}

		boolean status = totalPriceActual == totalPriceExpected;
		logger.info("Verify Total Footer Price: Actual Price (Departure(" + actualPrice1 + ") + Return(" + actualPrice2
				+ ")) = " + totalPriceActual + ", Expected Price = (Actual (" + expectedTotalPrice + ")"
				+ " + Discount (" + discount + ") = " + totalPriceExpected + ") Status --> " + status);
		return status;
	}

	public int getFormattedPrice(String price) {
		return Integer.parseInt(price.replaceAll("[Rs\\s,]", ""));
	}
}
