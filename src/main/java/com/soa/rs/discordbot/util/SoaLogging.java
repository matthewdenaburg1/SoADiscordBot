package com.soa.rs.discordbot.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoaLogging {

	private static SoaLogging INSTANCE = null;
	private static Logger logger;

	private SoaLogging() {
		logger = LogManager.getRootLogger();
	}

	public static SoaLogging getInstance() {
		initializeLogging();
		return INSTANCE;

	}

	public static void initializeLogging() {
		if (INSTANCE == null) {
			INSTANCE = new SoaLogging();
		}
	}

	public static Logger getLogger() {
		return logger;
	}
}
