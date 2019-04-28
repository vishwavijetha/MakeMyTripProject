package com.mmt.qa.basesetup;

public class Constants {

	public static final String PROJECT_HOME = System.getProperty("user.dir");
	public static final String PROJECT_RESOURCES_PATH = PROJECT_HOME + "/src/main/resources/";
	public static final String BROWSER_DRIVERS_PATH = PROJECT_RESOURCES_PATH + "/BrowserDrivers/";
	public static final String CONFIG_PATH = PROJECT_RESOURCES_PATH + "/Config/";
	public static final String TEST_RESOURCES_PATH = PROJECT_HOME + "/src/test/resources/";
	public static final String TESTDATA_PATH = TEST_RESOURCES_PATH + "/TestData/";
	public static final String SCREENSHOTS_PATH = PROJECT_HOME + "/Screenshots/";
	public static final String REPORTS_PATH = PROJECT_HOME + "/Reports/";

	public static final long PAGE_LOAD = 60;
	public static final long SCRIPT_LOAD = 60;
	public static final long EXPLICIT_WAIT = 20;
	public static final long IMPLICIT_WAIT = 30;
	public static final long POLLING = 5;
	
	public static final int RETRY_COUNT = 2;

}
