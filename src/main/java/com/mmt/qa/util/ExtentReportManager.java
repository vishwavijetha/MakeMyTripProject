package com.mmt.qa.util;

import java.io.File;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Protocol;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.mmt.qa.basesetup.Constants;

public class ExtentReportManager {
	private static ExtentReports extent;
	private static String reportFileName = "ExtentReports - ";
	private static String reportsPath = Constants.REPORTS_PATH;

	public static ExtentReports getInstance() {
		if (extent == null)
			createInstance();
		return extent;
	}

	// Create an extent report instance
	public static ExtentReports createInstance() {
		createReportPath(reportsPath);
		String filePath = reportsPath + reportFileName + CalendarUtil.getTodaysDateTime() + ".html";
		ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(filePath);
		htmlReporter.config().setAutoCreateRelativePathMedia(true);
		htmlReporter.config().setCSS("css-string");
		htmlReporter.config().setDocumentTitle("page title");
		htmlReporter.config().setEncoding("utf-8");
		htmlReporter.config().setJS("js-string");
		htmlReporter.config().setProtocol(Protocol.HTTPS);
		htmlReporter.config().setReportName("Test Automation Report");
		htmlReporter.config().setTheme(Theme.DARK);
		htmlReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
		htmlReporter.config().enableTimeline(true);
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		return extent;
	}

	// Create the report path if it does not exist
	private static void createReportPath(String reportPath) {
		File parentDirectory = new File(reportPath);
		if (!parentDirectory.exists()) {
			parentDirectory.mkdirs();
		}
	}
}