package com.mmt.qa.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class CalendarUtil {
	static Calendar calendar;
	static SimpleDateFormat dateFormat;	

	public static String getTodaysDateTime() {
		dateFormat = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss");
		return dateFormat.format(new Date());
	}

	public static List<Date> getDaysBetweenDates(Date enddate, Date startdate) {
		List<Date> dates = new ArrayList<Date>();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startdate);

		while (calendar.getTime().before(enddate)) {
			Date result = calendar.getTime();
			dates.add(result);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		return dates;
	}

	public static String getTodaysDate() {
		dateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
		return dateFormat.format(new Date());
	}
	
	public static String getFutureDateFromToday(int numberOfDaysToAdd) {
		dateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
		calendar = Calendar.getInstance();
		try {
			calendar.setTime(dateFormat.parse(dateFormat.format(new Date())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.add(Calendar.DAY_OF_MONTH, numberOfDaysToAdd);
		return dateFormat.format(calendar.getTime());
	}
	
	public static String getFormattedDate(String formatDate) {
		String formattedDate = null;
		try {
			dateFormat =  new SimpleDateFormat("EEE, dd MMM yyyy");
			formattedDate = new SimpleDateFormat("dd-MMMM-yyyy").format(dateFormat.parse(formatDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return formattedDate;
	}
}
