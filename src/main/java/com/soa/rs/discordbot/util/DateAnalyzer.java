package com.soa.rs.discordbot.util;

import java.util.Calendar;
import java.util.Date;

/**
 * The DateAnalyzer handles simple tasks such as determining if a date is on the
 * same day, previous day, or the number of days in between two dates.
 */
public class DateAnalyzer {

	/**
	 * Checks if <tt>cal1</tt> is before <tt>cal2</tt>
	 * 
	 * @param cal1
	 *            Calendar containing the earlier date
	 * @param cal2
	 *            Calendar containing the later date
	 * @return true if <tt>cal1</tt>is before <tt>cal2</tt>, false if otherwise
	 */
	public static boolean isBeforeDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA))
			return true;
		if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA))
			return false;
		if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR))
			return true;
		if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR))
			return false;
		return cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * Checks if <tt>cal1</tt> is the same date as <tt>cal2</tt>
	 * 
	 * @param cal1
	 *            Calendar 1
	 * @param cal2
	 *            Calendar 2
	 * @return true if the dates are the same, false if otherwise
	 */
	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
	}

	/**
	 * Gets the days in between two dates
	 * 
	 * @param d1
	 *            Date object containing the earlier date
	 * @param d2
	 *            Date Object containing the later date
	 * @return the number of days in between the dates
	 */
	public static int daysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}

}
