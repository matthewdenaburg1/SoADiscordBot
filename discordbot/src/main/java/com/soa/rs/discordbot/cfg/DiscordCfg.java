package com.soa.rs.discordbot.cfg;

import java.time.LocalDateTime;
import java.util.Date;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import com.soa.rs.discordbot.jaxb.DiscordConfiguration;
import com.soa.rs.discordbot.util.XmlReader;

/**
 * The <tt>DiscordCfg</tt> class is used for storing any configuration which
 * will be accessed by other parts of the bot. Configuration can be stored
 * within this class for easy access from other classes as needed.
 * <p>
 * The DiscordCfg should be created and accessed via
 * {@link DiscordCfgFactory#getInstance()}, and not via directly calling this
 * class.
 */

public class DiscordCfg {

	/**
	 * The date the last time that news was posted.
	 */
	private Date newsLastPost = null;

	/**
	 * The Uptime of the bot
	 */
	private LocalDateTime launchTime = null;

	private DiscordConfiguration config = null;

	private String botname = null;

	private String avatarUrl = null;

	/**
	 * Constructor for creating a DiscordCfg. This should never be called within the
	 * application outside of {@link DiscordCfgFactory}, and instead
	 * {@link DiscordCfgFactory#getInstance()} should be used.
	 */
	DiscordCfg() {
	}

	public DiscordConfiguration getConfig() {
		if (this.config == null) {
			this.config = new DiscordConfiguration();
		}
		return this.config;
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
		this.config = reader.loadAppConfig(filename);
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

	public String getBotname() {
		return botname;
	}

	public void setBotname(String botname) {
		this.botname = botname;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	/**
	 * Verify the necessary configuration items needed to start the bot are present.
	 * 
	 * @return true if all parameters are present, false if otherwise.
	 */
	// public boolean checkNecessaryConfiguration() {
	// return true;
	// if (eventForumUrl == null) {
	// SoaLogging.getLogger().warn("Event calendar URL was null, setting to
	// default");
	// setEventCalendarUrl(SoaDiscordBotConstants.EVENT_CALENDAR_URL);
	// }
	// if (newsUrl == null) {
	// SoaLogging.getLogger().warn("News feed URL was null, setting to default");
	// setNewsUrl(SoaDiscordBotConstants.NEWS_FEED_URL);
	// }
	// if (eventForumUrl != null && newsUrl != null && token != null) {
	// return true;
	// }
	// return false;
	// }

	/**
	 * If a parameter is determined to be missing, this function can return in
	 * string format which parameter is missing. This should be called during
	 * initial startup of the bot.
	 * 
	 * @return Missing parameters in String format.
	 */
	// public String getMissingConfigurationParameter() {
	// StringBuilder sb = new StringBuilder();
	// if (eventForumUrl == null) {
	// sb.append(" - Event Forum Url was missing");
	// }
	// if (newsUrl == null) {
	// sb.append(" - News feed Url was missing");
	// }
	// if (token == null) {
	// sb.append(" - Discord Login token was missing");
	// }
	//
	// return sb.toString();
	// }

}
