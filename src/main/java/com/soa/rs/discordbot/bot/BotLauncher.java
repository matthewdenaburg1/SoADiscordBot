package com.soa.rs.discordbot.bot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The RunBot class serves as the main class for the Discord bot.
 *
 */
public class BotLauncher {

	private static ConfigureBot launcher;

	private static final Logger logger = LogManager.getLogger();

	public static void main(String[] args) {
		launcher = new ConfigureBot(args);
		launcher.launch();
	}

}
