package com.soa.rs.discordbot.bot;

/**
 * The RunBot class serves as the main class for the Discord bot.
 *
 */
public class BotLauncher {

	private static ConfigureBot launcher;

	public static void main(String[] args) {
		launcher = new ConfigureBot(args);
		launcher.launch();
	}

}
