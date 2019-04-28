package com.mmt.qa.util;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import com.mmt.qa.basesetup.Constants;

public class RetryFailedTestCases implements IRetryAnalyzer{

	int counter = 0;
	int retryLimit = Constants.RETRY_COUNT;	 
	 /**
	  * 
	  *This method decides how many times a test needs to be rerun.
	  * TestNg will call this method every time a test fails. So we 
	  * can put some code in here to decide when to rerun the test.
	  * 
	  * Note: This method will return true if a tests needs to be retried
	  * and false it not. 
	  */
	 @Override
	 public boolean retry(ITestResult result) {
	 
	 if(counter < retryLimit)
	 {
	 counter++;
	 return true;
	 }
	 return false;
	 }
}
