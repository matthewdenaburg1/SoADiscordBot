package com.soa.rs.discordbot.bot.events;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.soa.rs.discordbot.util.SoaLogging;

/**
 * This class serves as a base for any RSS parsing that will be done from the
 * forums.
 */
public abstract class SoaDefaultRssParser {

	private URL url;
	private SyndFeedInput input;

	/**
	 * Constructor which also sets the feed URL
	 * 
	 * @param string
	 *            the RSS feed URL
	 */
	public SoaDefaultRssParser(String string) {
		try {
			url = new URL(string);
			input = new SyndFeedInput();
		} catch (MalformedURLException e) {
			SoaLogging.getLogger().error("Error setting URL", e);
		}
	}

	/**
	 * Sets the RSS feed URL
	 * 
	 * @param string
	 *            the RSS feed URL
	 */
	public void setUrl(String string) {
		try {
			url = new URL(string);
		} catch (MalformedURLException e) {
			SoaLogging.getLogger().error("Error setting URL", e);
		}
	}

	/**
	 * Get the RSS feed from the forums
	 * 
	 * @return the RSS feed object
	 * @throws IllegalArgumentException
	 * @throws FeedException
	 * @throws IOException
	 */
	protected SyndFeed getFeed() throws IllegalArgumentException, FeedException, IOException {
		return input.build(new XmlReader(url));

	}

	/**
	 * Parse the feed and return the output to be printed into Discord
	 * 
	 * @return content to be printed to Discord
	 */
	public abstract String parse();
}
