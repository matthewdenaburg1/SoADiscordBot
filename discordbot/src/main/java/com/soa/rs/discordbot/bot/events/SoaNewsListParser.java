package com.soa.rs.discordbot.bot.events;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.soa.rs.discordbot.cfg.DiscordCfgFactory;
import com.soa.rs.discordbot.util.SoaLogging;

/**
 * The SoaNewsListParser grabs information of new topics from the News and
 * announcements forum along with from the Promotions and Welcomes forum
 */
public class SoaNewsListParser extends SoaDefaultRssParser {

	/**
	 * Constructor which also sets the feed URL
	 * 
	 * @param string
	 *            the RSS feed URL
	 */
	public SoaNewsListParser(String string) {
		super(string);
	}

	/**
	 * Collects the information from the RSS feed, determines if it is newer
	 * than the last check, and if so, adds it to be printed to Discord.
	 * 
	 * @return content to be printed to Discord
	 */
	@Override
	public String parse() {
		try {
			SyndFeed feed = getFeed();
			Date now = new Date();

			if (DiscordCfgFactory.getConfig().getNewsLastPost() == null) {
				DiscordCfgFactory.getConfig().setNewsLastPost(now);
				SoaLogging.getLogger().info("LastNews is null, setting NewsLastPost to " + now.toString());
				return null;
			}

			Iterator<SyndEntry> entryIter = feed.getEntries().iterator();
			StringBuilder sb = new StringBuilder();
			while (entryIter.hasNext()) {
				SyndEntry entry = (SyndEntry) entryIter.next();
				if (entry.getPublishedDate().compareTo(DiscordCfgFactory.getConfig().getNewsLastPost()) > 0) {
					sb.append("**News: **");
					sb.append(entry.getTitle());
					sb.append(": " + entry.getLink() + "\n");
				}
			}
			DiscordCfgFactory.getConfig().setNewsLastPost(now);
			SoaLogging.getLogger().info("Setting NewsLastPost to " + now.toString());
			return sb.toString();
		} catch (IllegalArgumentException | FeedException | IOException e) {
			SoaLogging.getLogger().error("Error generating news list", e);
		}
		return null;
	}

}
