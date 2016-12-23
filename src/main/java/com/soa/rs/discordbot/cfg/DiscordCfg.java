package com.soa.rs.discordbot.cfg;

import java.time.LocalDateTime;
import java.util.Date;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soa.rs.discordbot.jaxb.DiscordConfiguration;
import com.soa.rs.discordbot.util.SoaDiscordBotConstants;

/**
 * The <tt>DiscordCfg</tt> singleton class is used for storing any configuration
 * which will be accessed by other parts of the bot. Configuration can be stored
 * within this class for easy access from other classes as needed.
 */

public class DiscordCfg {

	private static final Logger logger = LogManager.getLogger();

	/**
	 * The event calendar feed from the SoA forums.
	 */
	private static String eventForumUrl = null;

	/**
	 * The Discord Login token used to log in the bot.
	 */
	private static String token = null;

	/**
	 * The date the last time that news was posted.
	 */
	private static Date newsLastPost = null;

	/**
	 * The RSS feed URL for SoA News
	 */
	private static String newsUrl = null;

	/**
	 * The Uptime of the bot
	 */
	private static LocalDateTime launchTime = null;

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
		newsUrl = cfg.getNewsUrl();
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
	 * @param loginToken
	 *            the Discord login token
	 */
	public void setToken(String loginToken) {
		token = loginToken;
	}

	/**
	 * Get the last time news was posted
	 * 
	 * @return Date of last time news was posted.
	 */
	public Date getNewsLastPost() {
		return newsLastPost;
	}

	/**
	 * Set the last time news was posted
	 * 
	 * @param newsFeedLastPost
	 *            the last time news was posted.
	 */
	public void setNewsLastPost(Date newsFeedLastPost) {
		newsLastPost = newsFeedLastPost;
	}

	/**
	 * Get the news feed URL
	 * 
	 * @return the news feed URL
	 */
	public String getNewsUrl() {
		return newsUrl;
	}

	/**
	 * Set the news feed URL
	 * 
	 * @param newsFeedUrl
	 *            the news feed URL
	 */
	public void setNewsUrl(String newsFeedUrl) {
		newsUrl = newsFeedUrl;
	}

	/**
	 * Get the launch time of the bot
	 * @return the launch time for the bot
	 */
	public LocalDateTime getLaunchTime() {
		return launchTime;
	}

	/**
	 * Set the launch time of the bot
	 * @param launchTime the launch time of the bot
	 */
	public void setLaunchTime(LocalDateTime launchTime) {
		DiscordCfg.launchTime = launchTime;
	}

	/**
	 * Verify the necessary configuration items needed to start the bot are
	 * present.
	 * 
	 * @return true if all parameters are present, false if otherwise.
	 */
	public boolean checkNecessaryConfiguration() {
		if (eventForumUrl == null) {
			logger.warn("Event calendar URL was null, setting to default");
			setEventCalendarUrl(SoaDiscordBotConstants.EVENT_CALENDAR_URL);
		}
		if (newsUrl == null) {
			logger.warn("News feed URL was null, setting to default");
			setNewsUrl(SoaDiscordBotConstants.NEWS_FEED_URL);
		}
		if (eventForumUrl != null && newsUrl != null && token != null) {
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
		if (newsUrl == null) {
			sb.append(" - News feed Url was missing");
		}
		if (token == null) {
			sb.append(" - Discord Login token was missing");
		}

		return sb.toString();
	}

}
