package com.soa.rs.discordbot.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class provides a central place to obtain the logger for the bot,
 * removing the need to instantiate one for each class that needs to log.
 * 
 * <p>
 * Once the logger object is created (by running <tt>initializeLogging</tt> at
 * the beginning of the application), the logger can always be obtained by
 * calling the <tt>getLogger</tt> method. A new instance of the
 * <tt>SoaLogging</tt> class is not needed, and the class itself has been
 * structured as a singleton so there will only be one instance of the class at
 * any given time.
 */
public class SoaLogging {

	private static SoaLogging INSTANCE = null;
	private static Logger logger;

	private SoaLogging() {
		logger = LogManager.getRootLogger();
	}

	/**
	 * Returns the instance of the Soa Logging object. Recommended to use
	 * <tt>getLogger</tt> after calling <tt>initializeLogging</tt> rather than
	 * calling this method.
	 * 
	 * @return SoaLogging instance
	 */
	public static SoaLogging getInstance() {
		initializeLogging();
		return INSTANCE;
	}

	/**
	 * Initializes the logger object if it has not yet been created.
	 */
	public static void initializeLogging() {
		if (INSTANCE == null) {
			INSTANCE = new SoaLogging();
		}
	}

	/**
	 * Returns the logger object.
	 * 
	 * @return Logger for the application
	 */
	public static Logger getLogger() {
		return logger;
	}
}
