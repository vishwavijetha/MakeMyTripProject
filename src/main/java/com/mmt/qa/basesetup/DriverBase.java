package com.mmt.qa.basesetup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import com.mmt.qa.util.WebDriverListener;

public class DriverBase {

	public DriverBase() {
		loadConfig();
	}

	protected  WebDriver driver;
	public EventFiringWebDriver eventFiringWebDriver;
	public WebDriverListener eventListener;
	protected static Properties config, testData;
	public final static Logger logger = Logger.getLogger(DriverBase.class);

	public void launchBrowser() {

		BrowserFactory browserFactory = BrowserFactory.createInstance();
		browserFactory.setDriver(config.getProperty("browser"), config.getProperty("headless"));
		driver = browserFactory.getDriver();
		logger.info("Browser launched: " + config.getProperty("browser"));

		eventFiringWebDriver = new EventFiringWebDriver(driver);
		eventListener = new WebDriverListener();
		eventFiringWebDriver.register(eventListener);
		driver = eventFiringWebDriver;

		maximizeWindow();
		deleteAllCookies(driver);
		setImplicitWait(Constants.IMPLICIT_WAIT);
		setPageLoadTimeout(Constants.PAGE_LOAD);
	}

	public WebDriver getDriver() {
		return driver;
	}

	public static void loadConfig() {
		config = new Properties();
		testData = new Properties();
		try {
			PropertyConfigurator.configure(Constants.CONFIG_PATH + "log4j.properties");
			config.load(new FileInputStream(Constants.CONFIG_PATH + "config.properties"));
			testData.load(new FileInputStream(Constants.TESTDATA_PATH + "testdata.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeBrowser() {
		if (driver != null) {
			driver.close();
			eventFiringWebDriver.unregister(eventListener);
			logger.info("Browser closed...");
		}
	}

	public void quitBrowser() {
		if (driver != null) {
			driver.quit();
			eventFiringWebDriver.unregister(eventListener);
			logger.info("Browser closed...");
		}
	}

	public String getPageTitle() {
		String pageTitle = driver.getTitle();
		logger.info("Page Title: " + pageTitle);
		return pageTitle;
	}

	public void setPageLoadTimeout(long seconds) {
		logger.info("Setting PageLoadTimeout: " + seconds);
		driver.manage().timeouts().pageLoadTimeout(seconds, TimeUnit.SECONDS);
	}

	public void setScriptLoadTimeout(long seconds) {
		logger.info("Setting ScriptTimeout: " + seconds);
		driver.manage().timeouts().setScriptTimeout(seconds, TimeUnit.SECONDS);
	}

	public void setImplicitWait(long seconds) {
		logger.info("Setting ImplicitWait: " + seconds);
		driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
	}

	public boolean clickOnElement(WebElement element) {
		element.click();
		return true;
	}

	public void deleteAllCookies(WebDriver driver) {
		driver.manage().deleteAllCookies();
	}

	public void maximizeWindow() {
		logger.info("Maximize browser window");
		driver.manage().window().maximize();
	}

	public void openURL() {
		logger.info("Opening URL: " + config.getProperty("url"));
		driver.get(config.getProperty("url"));
	}

	public String takeScreenshot(String destination) {
		System.out.println();
		File parentDirectory = new File(destination).getParentFile();
		if (parentDirectory != null)
			parentDirectory.mkdirs();
		try {
			File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(file, new File(destination));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return destination;
	}

	public void enterText(WebElement element, String text) {
		element.sendKeys(text);
	}

	public void fluentWait(WebElement element, WebDriver driver, Class<? extends Throwable> exceptionClass) {
		if(element != null) {
			new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(Constants.EXPLICIT_WAIT))
			.pollingEvery(Duration.ofSeconds(Constants.POLLING)).ignoring(exceptionClass)
			.until(ExpectedConditions.refreshed(ExpectedConditions.not(ExpectedConditions.stalenessOf(element))));
		}
		
	}

	public Object executeJavaScript(WebDriver driver, String javascript, Object... args) {
		return ((JavascriptExecutor) driver).executeScript(javascript, args);
	}

	public void clickOnElementUsingJS(WebDriver driver, WebElement element) {
		executeJavaScript(driver, "arguments[0].click();", element);
	}

	public void scrollToWebElement(WebElement scrollToThisElement) {
		Coordinates coordinate = ((Locatable) scrollToThisElement).getCoordinates();
		coordinate.onPage();
		coordinate.inViewPort();
	}

	public void scrollToTopUsingJS(WebDriver driver) {
		executeJavaScript(driver, "window.scrollBy(0,-document.body.scrollHeight);");
	}

	public String getText(WebElement element) {
		return element.getText().replaceAll("[\\t\\n]", " ");
	}

	public String getAttributeValue(WebElement element, String attributeName) {
		return element.getAttribute(attributeName);
	}
}