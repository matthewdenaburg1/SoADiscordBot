package com.soa.rs.discordbot.cfg;

/**
 * The DiscordCfgFactory is responsible for the creation and maintaining of the
 * Discord Configuration for the running bot. Any access needed to the
 * configuration during runtime should go through this class by calling the
 * static {@link #getConfig()} method.
 * 
 */
public class DiscordCfgFactory {

	/**
	 * The Discord Configuration object for the bot.
	 */
	private static final DiscordCfg discordCfg = new DiscordCfg();

	/**
	 * Returns the Discord Configuration for the bot. Any call to this method will
	 * always return the same object.
	 * 
	 * @return Discord Configuration object
	 */
	public static DiscordCfg getConfig() {
		return discordCfg;
	}

}
