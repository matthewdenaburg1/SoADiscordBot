package com.soa.rs.discordbot.bot;

import com.soa.rs.discordbot.util.SoaLogging;

/**
 * The RunBot class serves as the main class for the Discord bot.
 *
 */
public class BotLauncher {

	private static ConfigureBot launcher;

	public static void main(String[] args) {
		SoaLogging.initializeLogging();
		launcher = new ConfigureBot(args);
		launcher.launch();
	}

}
