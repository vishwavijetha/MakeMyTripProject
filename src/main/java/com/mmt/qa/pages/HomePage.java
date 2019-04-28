package com.mmt.qa.pages;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import com.mmt.qa.basesetup.DriverBase;

public class HomePage extends DriverBase {

	public final static Logger logger = Logger.getLogger(HomePage.class);
	protected WebDriver driver;
	
	public HomePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath = "//span[text()='Flights']/ancestor::a")
	private WebElement flightsLink;

	public boolean clickOnFlightsLink() {
		if (getAttributeValue(flightsLink, "class").contains("active")) {
			return true;
		}
		return clickOnElement(flightsLink);
	}

	@FindBy(xpath = "//li[text()='Round Trip']")
	private WebElement roundTripCheckBox;

	public boolean clickOnRoundTrip() {
		if (getAttributeValue(roundTripCheckBox, "class").contains("selected")) {
			return true;
		}
		return clickOnElement(roundTripCheckBox);
	}
	
	By cityAutoSuggestionList = By.xpath("//li[@role='option']/div[contains(@class,'makeFlex')]");

	@FindBy(xpath = "//label[@for='fromCity']")
	private WebElement fromCityMenu;

	@FindBy(xpath = "//input[contains(@placeholder,'From')]")
	private WebElement fromCityTextBox;

	/**
	 * @method selectFromCity()
	 * @param cityName
	 * @return String {Selected City}
	 * @purpose Select 'From' city
	 */
	public String selectFromCity(String cityName) {
		if (!isMenuWidgetOpen()) {
			clickOnElement(fromCityMenu);
		}
		
		enterText(fromCityTextBox, cityName);
		List<WebElement> cityList = driver.findElements(cityAutoSuggestionList);
		
		String city = null;
		int cityIndex = 0;
		while (cityIndex != cityList.size()) {
			scrollToWebElement(cityList.get(cityIndex));
			city = getText(cityList.get(cityIndex));
			if (city.toLowerCase().contains(cityName.toLowerCase())) {
				clickOnElement(cityList.get(cityIndex));
				logger.info("Selected 'From' city: " + city);
				break;
			}
			cityIndex++;
			cityList = driver.findElements(cityAutoSuggestionList);
		}
		return city;
	}

	public String getSelectedFromCity() {
		return getText(fromCityMenu);
	}

	@FindBy(xpath = "//label[@for='toCity']")
	private WebElement toCityMenu;

	@FindBy(xpath = "//input[contains(@placeholder,'To')]")
	private WebElement toCityTextBox;

	/**
	 * @method selectToCity()
	 * @param cityName
	 * @return String {Selected City}
	 * @purpose Select 'To' city
	 */ 
	public String selectToCity(String cityName) {
		if (!isMenuWidgetOpen()) {
			clickOnElement(toCityMenu);
		}
		enterText(toCityTextBox, cityName);
		List<WebElement> cityList = driver.findElements(cityAutoSuggestionList);		
	
		String city = null;
		int cityIndex = 0;
		while (cityIndex != cityList.size()-1) {
			scrollToWebElement(cityList.get(cityIndex));
			city = getText(cityList.get(cityIndex));
			if (city.toLowerCase().contains(cityName.toLowerCase())) {
				clickOnElement(cityList.get(cityIndex));
				logger.info("Selected 'To' city: " + city);
				break;
			}
			cityIndex++;
			cityList = driver.findElements(cityAutoSuggestionList);
		}
		return city;
	}

	public String getSelectedToCity() {
		return getText(toCityMenu);
	}

	@FindBy(xpath = "//label[@for='departure']")
	private WebElement departureDate;

	@FindBy(xpath = "//label[@for='return']")
	private WebElement toDate;

	@FindBy(xpath = "//div[@class='DayPicker-Caption']")
	private List<WebElement> dateContainerCaption;

	@FindBy(xpath = "//div[@class='DayPicker-NavBar']/span[@aria-label='Next Month']")
	private WebElement nextMonthArrow;

	@FindBy(xpath = "//div[@class='DayPicker-Month'][1]//div[contains(@class,'DayPicker-Day') and @aria-disabled='false']//p")
	private List<WebElement> month1CalendarDays;

	@FindBy(xpath = "//div[@class='DayPicker-Month'][2]//div[contains(@class,'DayPicker-Day') and @aria-disabled='false']//p")
	private List<WebElement> month2CalendarDays;

	/**
	 * @method selectDate()
	 * @param date
	 * @value DD-MMMM-YYYY 25-April-2019
	 * @return
	 * @purpose Select any date within range i.e., (presentDay <--> 365)
	 */
	public void selectDate(String date) {

		// Precondition
		verifyDateRange(date);

		if (!isMenuWidgetOpen()) {
			clickOnElement(departureDate);
		}

		// Get 1st displayed month in the Calendar widget
		String displayDate = dateContainerCaption.get(0).getText();
		int displayMonth = Month.valueOf(displayDate.substring(0, displayDate.length() - 4).toUpperCase()).getValue();
		int displayYear = Integer.parseInt(displayDate.substring(displayDate.length() - 4));
		int givenDay = Integer.parseInt(date.split("-")[0]);
		int givenMonth = Month.valueOf(date.split("-")[1].toUpperCase()).getValue();
		int givenYear = Integer.parseInt(date.split("-")[2]);
		String givenMonthName = date.split("-")[1];
		int yearDiff = Math.abs(displayYear - givenYear);
		int monthDiff = Math.abs(displayMonth - givenMonth);
		int numberOfClicks = Math.abs((yearDiff * 12) - monthDiff);

		// Perform no. of clicks on Calendar Next Arrow until it matches the given month
		while (numberOfClicks != 0) {
			clickOnElement(nextMonthArrow);
			--numberOfClicks;
		}
		List<WebElement> days;
		for (WebElement month : dateContainerCaption) {
			// Select Calendar window based on displayed month
			// Capture substring from caption i.e., April2019 -> April
			if (getText(month).substring(0, getText(month).length() - 4)
					.equalsIgnoreCase(String.valueOf(givenMonthName))) {
				days = month1CalendarDays; // Pick 1st Calendar
			} else {
				days = month2CalendarDays; // Pick 2nd Calendar
			}
			for (WebElement day : days) {
				if (day.getText().equals(String.valueOf(givenDay))) {
					clickOnElement(day);
					logger.info("Selected date: " + date);
					break;
				}
			}
			break;
		}
	}

	/**
	 * @method verifyDateRange()
	 * @param date
	 * @value DD-MMMM-YYYY 25-April-2019
	 * @return
	 * @purpose Verifies input date range i.e., (presentDay <--> 365)
	 */
	public void verifyDateRange(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
		Calendar calendar = Calendar.getInstance();
		Date presentDate;
		try {
			presentDate = dateFormat.parse(dateFormat.format(calendar.getTime())); // Get present date
			calendar.add(Calendar.DAY_OF_MONTH, 365); // Add 1 year to the present date to satisfy the precondition
			Date afterOneYear = dateFormat.parse(dateFormat.format(calendar.getTime()));

			if ((dateFormat.parse(date).after(afterOneYear) || dateFormat.parse(date).before(presentDate))
					&& !dateFormat.parse(date).equals(presentDate)) {
				logger.error("Date Range Inavlid: Please select date within 365 days from the current date");
				throw new IllegalArgumentException(
						"Date Range Inavlid: Please select date within 365 days from the current date");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@FindBy(xpath = "//div[@class='makeFlex']/following-sibling::div")
	private WebElement menuWidget;

	/**
	 * @method isMenuWidgetOpen()
	 * @return boolean {true, false}
	 * @purpose Generic method to check whether the Widget is open or not
	 */
	public boolean isMenuWidgetOpen() {
		return getAttributeValue(menuWidget, "class").contains("widgetOpen");
	}
	
	

	@FindBy(xpath = "//a[contains(@class,'SearchBtn')]")
	private WebElement searchButton;

	public SearchPage clickOnSearchButton() {
		deleteAllCookies(driver);
		clickOnElement(searchButton);
		deleteAllCookies(driver);
		logger.info("Clicked on Search button");
		return new SearchPage(driver);
	}

	/**
	 * @method searchRoundTripFlights()
	 * @param fromCity
	 * @param toCity
	 * @param departureDate
	 * @param returnDate
	 * @return SearchPage instance
	 * @purpose Series of Actions
	 *          {Flights->RoundTrip->From->To->Departure->Return->Search}
	 */
	public SearchPage searchRoundTripFlights(String fromCity, String toCity, String departureDate, String returnDate) {
		clickOnFlightsLink();
		clickOnRoundTrip();
		selectFromCity(fromCity);
		selectToCity(toCity);
		selectDate(departureDate);
		selectDate(returnDate);
		clickOnSearchButton();
		return new SearchPage(driver);
	}
}
