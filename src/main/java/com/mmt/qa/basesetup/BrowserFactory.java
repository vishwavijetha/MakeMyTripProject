package com.mmt.qa.basesetup;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 * @author Vishwa.Vijetha 'BrowserFactory' class is a Singleton class to
 *         restrict the creation of multiple driver instances for each execution
 *         in JVM. It is used as Global point of access to the object.
 * 
 *         ThreadLocal<WebDriver> gives thread-safe environment and supports
 *         parallel execution with multi-threading
 */
public class BrowserFactory {
	private static BrowserFactory instance = null;
	private ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();
	public final static Logger logger = Logger.getLogger(BrowserFactory.class);

	/**
	 * @category Singleton Constructor
	 */
	private BrowserFactory() {
	}

	public static BrowserFactory createInstance() {
			synchronized (BrowserFactory.class) {
				if (instance == null) {
					instance = new BrowserFactory();
				}
			}
		return instance;
	}

	public synchronized void setDriver(String browserName, String headless) {
		logger.info("Setting browser: '" + browserName + "', headless: '" + headless + "'");
		checkPlatformAndSetBrowserDriver(browserName);
		try {
			switch (browserName.toLowerCase()) {
			case "ff":
			case "firefox":
				driver.set(new FirefoxDriver(getFirefoxConfig(Boolean.valueOf(headless))));
				break;

			case "ch":
			case "chrome":
				driver.set(new ChromeDriver(getChromeConfig(Boolean.valueOf(headless))));
				break;

			default:
				logger.error("Execution Terminated... Browser implementation not found for:  " + browserName);
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized WebDriver getDriver() {
		return driver.get();
	}

	public static ChromeOptions getChromeConfig(boolean headless) {

		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("test-type");
		chromeOptions.setExperimentalOption("useAutomationExtension", false);
		chromeOptions.addArguments("--disable-notifications");
		chromeOptions.addArguments("--disable-extensions");
		chromeOptions.addArguments("--incognito");
		if (headless) {
			chromeOptions.setHeadless(true);
		}
		return chromeOptions;
	}
	
	public static FirefoxOptions getFirefoxConfig(boolean headless) {

		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("dom.webnotifications.enabled", false);
		profile.setPreference("browser.privatebrowsing.autostart", true);
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setProfile(profile);
		firefoxOptions.setAcceptInsecureCerts(true);
		if (headless) {
			firefoxOptions.setHeadless(headless);
		}
		return firefoxOptions;
	}

	public static void checkPlatformAndSetBrowserDriver(String browserName) {

		String platform = System.getProperty("os.name");
		String firefoxDriverPath = Constants.BROWSER_DRIVERS_PATH + "geckodriver.exe";
		String chromeDriverPath = Constants.BROWSER_DRIVERS_PATH + "chromedriver.exe";

		if (platform.toUpperCase().contains("WINDOWS")) {
			if (browserName.equalsIgnoreCase("firefox") || browserName.equalsIgnoreCase("ff")) {
				System.setProperty("webdriver.gecko.driver", firefoxDriverPath);
			} else if (browserName.equalsIgnoreCase("chrome") || browserName.equalsIgnoreCase("ch")) {
				System.setProperty("webdriver.chrome.driver", chromeDriverPath);
			}
			
		} else if (platform.toUpperCase().contains("MAC")) {
			if (browserName.equalsIgnoreCase("firefox") || browserName.equalsIgnoreCase("ff")) {
				System.setProperty("webdriver.gecko.driver", firefoxDriverPath.replace(".exe", ""));
			} else if (browserName.equalsIgnoreCase("chrome") || browserName.equalsIgnoreCase("ch")) {
				System.setProperty("webdriver.chrome.driver", chromeDriverPath.replace(".exe", ""));
			}

		} else {
			logger.error("Execution Terminated... Platform implementation not found for:  " + platform);
			System.exit(0);
		}
		logger.info("Selected Platform: " + platform);
	}
}
