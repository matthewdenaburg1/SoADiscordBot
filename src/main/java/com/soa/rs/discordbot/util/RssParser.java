package com.soa.rs.discordbot.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

public class RssParser {
	private URL url;
	private SyndFeedInput input;
	private static final Logger logger = LogManager.getLogger();

	public RssParser(String string) {
		try {
			url = new URL(string);
			input = new SyndFeedInput();
		} catch (MalformedURLException e) {
			logger.error("Error setting URL", e);
		}
	}

	public void setUrl(String string) {
		try {
			url = new URL(string);
		} catch (MalformedURLException e) {
			logger.error("Error setting URL", e);
		}
	}

	private SyndFeed getFeed() throws IllegalArgumentException, FeedException, IOException {
		return input.build(new XmlReader(url));
	}

	public String parseEventFeed() {
		try {
			SyndFeed feed = getFeed();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date today = new Date();
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			cal1.setTimeZone(TimeZone.getTimeZone("UTC"));
			cal2.setTimeZone(TimeZone.getTimeZone("UTC"));
			cal2.setTime(today);

			Iterator<SyndEntry> entryIter = feed.getEntries().iterator();
			StringBuilder sb = new StringBuilder();
			sb.append("**Today's SoA Events**\n");
			while (entryIter.hasNext()) {
				SyndEntry entry = (SyndEntry) entryIter.next();
				cal1.setTime(entry.getPublishedDate());

				// Ongoing weekly competitions
				if (DateCheck.isBeforeDay(cal1, cal2) && entry.getTitle().toLowerCase().contains("comp")
						&& DateCheck.daysBetween(cal1.getTime(), cal2.getTime()) < 7) {
					sb.append("The following competition is ongoing!\n");
					sb.append(entry.getTitle());
					sb.append("\nFor details, visit: <" + entry.getLink() + ">");
					sb.append("\n\n");
				}
				// Ongoing events
				if (DateCheck.isBeforeDay(cal1, cal2) && entry.getTitle().toLowerCase().contains("ongoing")) {
					sb.append("The following event is ongoing!\n");
					sb.append(entry.getTitle());
					sb.append("\nFor details, visit: <" + entry.getLink() + ">");
					sb.append("\n\n");
				}
				// Today's events
				else if (DateCheck.isSameDay(cal1, cal2)) {
					sb.append("Event Title: " + entry.getTitle());
					sb.append("\nEvent Date: " + sdf.format(entry.getPublishedDate()));
					sb.append("\nFor details, visit: <" + entry.getLink() + ">");
					sb.append("\n\n");
				}
			}
			sb.append("For more event information and upcoming events, check out the");
			sb.append("\nEvents Forum: http://forums.soa-rs.com/forum/9-events/");
			sb.append("\nEvents Calendar: http://forums.soa-rs.com/calendar/");
			return sb.toString();
		} catch (IllegalArgumentException | FeedException | IOException e) {
			logger.error("Error generating event list", e);
		}

		return null;
	}
}
