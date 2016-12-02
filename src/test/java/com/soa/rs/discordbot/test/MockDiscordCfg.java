package com.soa.rs.discordbot.test;

import javax.xml.bind.JAXBException;

import com.soa.rs.discordbot.cfg.ConfigReader;
import com.soa.rs.discordbot.jaxb.DiscordConfiguration;

/**
 * This class is a clone of {@link com.soa.rs.discordbot.cfg.DiscordCfg} without
 * including the singleton elements, for the purposes of JUnit testing.
 */
public class MockDiscordCfg {
	/**
	 * The event calendar feed from the SoA forums.
	 */
	private String eventForumUrl = null;

	/**
	 * The Discord Login token used to log in the bot.
	 */
	private String token = null;

	public MockDiscordCfg() {
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
		loadFromDiscordConfiguration(cfg);
	}

	/**
	 * Load an initial configuration from a DiscordConfiguration object
	 * 
	 * @param cfg
	 *            DiscordConfiguration object
	 */
	public void loadFromDiscordConfiguration(DiscordConfiguration cfg) {
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
		eventForumUrl = url;
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
	public void setToken(String loginToken) {
		token = loginToken;
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
