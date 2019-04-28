package com.mmt.qa.util;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.mmt.qa.basesetup.Constants;
import com.mmt.qa.basesetup.DriverBase;

public class TestNGListener extends DriverBase implements ITestListener {

	public final static Logger logger = Logger.getLogger(TestNGListener.class);
	private static ExtentReports extent = ExtentReportManager.getInstance();
	private static ThreadLocal<ExtentTest> test = new ThreadLocal<ExtentTest>();
	Instant start, end;
	Duration timeElapsed;

	@Override
	public synchronized void onStart(ITestContext context) {
		PropertyConfigurator.configure(Constants.CONFIG_PATH + "log4j.properties");
		logger.info("\n");
		logger.info("///////////////////////////////////////////////////////////////////");
		logger.info("////__________________________ START ______________________________");
	}

	@Override
	public synchronized void onFinish(ITestContext context) {
		logger.info("////___________________________ END _______________________________");
		logger.info("///////////////////////////////////////////////////////////////////");
		extent.flush();
	}

	@Override
	public synchronized void onTestStart(ITestResult result) {
		start = Instant.now();
		logger.info("********************************************************************");
		logger.info("$$  Executing TestCase: --> " + getTestMethodName(result)+getTestInputArguments(result)+"  $$");
		logger.info("$$  Test Description: "+ result.getMethod().getDescription());
		logger.info("********************************************************************");
		ExtentTest extentTest = extent.createTest(getTestMethodName(result), result.getMethod().getDescription());
		test.set(extentTest);
		test.get().info("Executing TestCase: -->"+ getTestMethodName(result)+getTestInputArguments(result));
		test.get().info("Test Description: "+ result.getMethod().getDescription());
	}
	
	@Override
	public synchronized void onTestSuccess(ITestResult result) {
		end = Instant.now();
		timeElapsed = Duration.between(start, end);
		logger.info("////////////////////////////////////////////////////////////////////");
		logger.info("////________________________________________________________________");
		logger.info("//// TestCase: " + getTestMethodName(result)+getTestInputArguments(result));
		logger.info("////________________________________________________________________");
		logger.info("//// Status: PASSED");
		logger.info("////________________________________________________________________");
		logger.info("//// Total Duration: " + timeElapsed.toMillis() / 1000.0 + " seconds");
		logger.info("////________________________________________________________________");
		logger.info("////////////////////////////////////////////////////////////////////");
		test.get().pass("PASSED");
	}

	@Override
	public synchronized void onTestFailure(ITestResult result) {
		String screenshotPath = Constants.SCREENSHOTS_PATH + getTestMethodName(result) + " "
				+ CalendarUtil.getTodaysDateTime() + ".png";
		end = Instant.now();
		timeElapsed = Duration.between(start, end);
		logger.error("////////////////////////////////////////////////////////////////////");
		logger.error("////________________________________________________________________");
		logger.error("//// TestCase: " + getTestMethodName(result)+getTestInputArguments(result));
		logger.error("////________________________________________________________________");
		logger.error("//// Status: FAILED");
		logger.error("////________________________________________________________________");
		logger.error("//// Total Duration: " + timeElapsed.toMillis() / 1000.0 + " seconds");
		logger.error("////________________________________________________________________");
		logger.error("//// Screenshot Path: " + screenshotPath);
		logger.error("////________________________________________________________________");
		logger.error("////////////////////////////////////////////////////////////////////");
		Object currentClass = result.getInstance();
		this.driver = ((DriverBase) currentClass).getDriver();
		takeScreenshot(screenshotPath);
		try {
			test.get().fail(result.getThrowable().getMessage(), MediaEntityBuilder
					.createScreenCaptureFromPath(screenshotPath).build());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void onTestSkipped(ITestResult result) {
		String screenshotPath = Constants.SCREENSHOTS_PATH + getTestMethodName(result) + " "
				+ CalendarUtil.getTodaysDateTime() + ".png";
		end = Instant.now();
		timeElapsed = Duration.between(start, end);
		logger.warn("//////////////////////////////////////////////////////////////////////");
		logger.warn("////__________________________________________________________________");
		logger.warn("//// TestCase: " + getTestMethodName(result)+getTestInputArguments(result));
		logger.warn("////__________________________________________________________________");
		logger.warn("//// Status: SKIPPED");
		logger.warn("////__________________________________________________________________");
		logger.warn("//// Total Duration: " + timeElapsed.toMillis() / 1000.0 + " seconds");
		logger.warn("////__________________________________________________________________");
		logger.warn("//////////////////////////////////////////////////////////////////////");
		Object currentClass = result.getInstance();
		this.driver = ((DriverBase) currentClass).getDriver();
		takeScreenshot(screenshotPath);
		try {
			test.get().skip(result.getThrowable().getMessage(), MediaEntityBuilder
					.createScreenCaptureFromPath(screenshotPath).build());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		logger.error("-------------        " + getTestMethodName(result) + " --> FAILED (%): "
				+ ITestResult.SUCCESS_PERCENTAGE_FAILURE + "\n\n");
	}
	
	private static String getTestMethodName(ITestResult iTestResult) {
		return iTestResult.getMethod().getConstructorOrMethod().getName();
	}
	
	private static String getTestInputArguments( ITestResult result ) {

        StringBuilder inputArguments = new StringBuilder();
        Object[] inputArgs = result.getParameters();
        inputArguments.append("( ");
        if (inputArgs != null && inputArgs.length > 0) {
            for (Object inputArg : inputArgs) {
                if (inputArg == null) {
                    inputArguments.append("null");
                } else {
                    inputArguments.append(inputArg.toString());
                }
                inputArguments.append(", ");
            }
            inputArguments.delete(inputArguments.length() - 2, inputArguments.length() - 1); 
        }
        inputArguments.append(")");
        return inputArguments.toString();
    }
	
}
