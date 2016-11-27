package com.soa.rs.discordbot.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

public class RssParser {
	private URL url;
	private SyndFeedInput input;

	public RssParser(String string) {
		try {
			url = new URL(string);
			input = new SyndFeedInput();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setUrl(String string) {
		try {
			url = new URL(string);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				if (DateCheck.isBeforeDay(cal1, cal2)) // ongoing events
				{
					sb.append("The following event is ongoing!\n");
					sb.append(entry.getTitle());
					sb.append("\nFor details, visit: " + entry.getLink());
					sb.append("\n");
				} else if (DateCheck.isSameDay(cal1, cal2))// today's events
				{
					sb.append("\nEvent Title: " + entry.getTitle());
					sb.append("\nEvent Date: " + sdf.format(entry.getPublishedDate()));
					sb.append("\nFor details, visit: " + entry.getLink());
					sb.append("\n");
				}
			}
			return sb.toString();
		} catch (IllegalArgumentException | FeedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
