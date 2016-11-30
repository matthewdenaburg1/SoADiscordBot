package com.soa.rs.discordbot.cfg;

import javax.xml.bind.JAXBException;

import com.soa.rs.discordbot.jaxb.DiscordConfiguration;

/**
 * The <tt>DiscordCfg</tt> singleton class is used for storing any configuration
 * which will be accessed by other parts of the bot. Configuration can be stored
 * within this class for easy access from other classes as needed.
 */
public class DiscordCfg {

	/**
	 * The event calendar feed from the SoA forums.
	 */
	private static String eventForumUrl = null;

	/**
	 * The Discord Login token used to log in the bot.
	 */
	private static String token = null;

	protected DiscordCfg() {
	}

	/**
	 * Singleton instantiator of the configuration. This setup is noted by
	 * online documentation of being thread safe.
	 */
	private static class DiscordConfigSingleton {
		private static final DiscordCfg INSTANCE = new DiscordCfg();
	}

	/**
	 * Returns the singleton instance of the configuration
	 * 
	 * @return Configuration instance
	 */
	public static DiscordCfg getInstance() {
		return DiscordConfigSingleton.INSTANCE;
	}

	/**
	 * Load an initial configuration from the configuration file specified on
	 * the command line.
	 * 
	 * @param filename
	 *            the path to the configuration file
	 * @throws JAXBException
	 */
	public void loadFromFile(String filename) throws JAXBException {
		ConfigReader reader = new ConfigReader();
		DiscordConfiguration cfg = reader.loadAppConfig(filename);
		eventForumUrl = cfg.getEventUrl();
		token = cfg.getDiscordToken();

	}

	/**
	 * Retrieve the event calendar feed URL
	 * 
	 * @return the event calendar feed URL
	 */
	public String getEventCalendarUrl() {
		return eventForumUrl;
	}

	/**
	 * Set the the event calendar feed URL
	 * 
	 * @param url
	 *            the event calendar feed URL
	 */
	public void setEventCalendarUrl(String url) {
		DiscordCfg.eventForumUrl = url;
	}

	/**
	 * Get the Discord login token
	 * 
	 * @return the Discord login token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Set the Discord login token
	 * 
	 * @param token
	 *            the Discord login token
	 */
	public void setToken(String token) {
		DiscordCfg.token = token;
	}

	/**
	 * Verify the necessary configuration items needed to start the bot are
	 * present.
	 * 
	 * @return true if all parameters are present, false if otherwise.
	 */
	public boolean checkNecessaryConfiguration() {
		if (eventForumUrl != null && token != null) {
			return true;
		}
		return false;
	}

	/**
	 * If a parameter is determined to be missing, this function can return in
	 * string format which parameter is missing. This should be called during
	 * initial startup of the bot.
	 * 
	 * @return Missing parameters in String format.
	 */
	public String getMissingConfigurationParameter() {
		StringBuilder sb = new StringBuilder();
		if (eventForumUrl == null) {
			sb.append(" - Event Forum Url was missing");
		}
		if (token == null) {
			sb.append(" - Discord Login token was missing");
		}

		return sb.toString();
	}

}
