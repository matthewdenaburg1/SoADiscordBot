package com.soa.rs.discordbot.cfg;

import java.time.LocalDateTime;
import java.util.Date;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import com.soa.rs.discordbot.jaxb.DiscordConfiguration;
import com.soa.rs.discordbot.util.SoaDiscordBotConstants;
import com.soa.rs.discordbot.util.SoaLogging;

/**
 * The <tt>DiscordCfg</tt> class is used for storing any configuration which
 * will be accessed by other parts of the bot. Configuration can be stored
 * within this class for easy access from other classes as needed.
 * <p>
 * The DiscordCfg should be created and accessed via
 * {@link DiscordCfgFactory#getConfig()}, and not via directly calling this
 * class.
 */

public class DiscordCfg {

	/**
	 * The event calendar feed from the SoA forums.
	 */
	private String eventForumUrl = null;

	/**
	 * The Discord Login token used to log in the bot.
	 */
	private String token = null;

	/**
	 * The date the last time that news was posted.
	 */
	private Date newsLastPost = null;

	/**
	 * The RSS feed URL for SoA News
	 */
	private String newsUrl = null;

	/**
	 * The Uptime of the bot
	 */
	private LocalDateTime launchTime = null;

	private String trackingFile = null;

	/**
	 * Constructor for creating a DiscordCfg. This should never be called within the
	 * application outside of {@link DiscordCfgFactory}, and instead
	 * {@link DiscordCfgFactory#getConfig()} should be used.
	 */
	DiscordCfg() {
	}

	/**
	 * Load an initial configuration from the configuration file specified on the
	 * command line.
	 * 
	 * @param filename
	 *            the path to the configuration file
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public void loadFromFile(String filename) throws JAXBException, SAXException {
		XmlReader reader = new XmlReader();
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
		trackingFile = cfg.getTrackingFile();
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
	 * 
	 * @return the launch time for the bot
	 */
	public LocalDateTime getLaunchTime() {
		return launchTime;
	}

	/**
	 * Set the launch time of the bot
	 * 
	 * @param launchTime
	 *            the launch time of the bot
	 */
	public void setLaunchTime(LocalDateTime launchTime) {
		this.launchTime = launchTime;
	}

	public String getTrackingFile() {
		return trackingFile;
	}

	public void setTrackingFile(String trackingFile) {
		this.trackingFile = trackingFile;
	}

	/**
	 * Verify the necessary configuration items needed to start the bot are present.
	 * 
	 * @return true if all parameters are present, false if otherwise.
	 */
	public boolean checkNecessaryConfiguration() {
		if (eventForumUrl == null) {
			SoaLogging.getLogger().warn("Event calendar URL was null, setting to default");
			setEventCalendarUrl(SoaDiscordBotConstants.EVENT_CALENDAR_URL);
		}
		if (newsUrl == null) {
			SoaLogging.getLogger().warn("News feed URL was null, setting to default");
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
