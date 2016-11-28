package com.soa.rs.discordbot.bot;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soa.rs.discordbot.util.RssParser;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class SoaEventListerTask {

	private String url;
	private IChannel channel;
	private static final Logger logger = LogManager.getLogger();

	public SoaEventListerTask(String url) {
		this.url = url;
	}

	public SoaEventListerTask(String url, IChannel channel) {
		this.url = url;
		this.channel = channel;
	}

	public void execTask(IDiscordClient client, boolean daily) {
		RssParser parser = new RssParser(this.url);
		String events = parser.parseEventFeed();

		// Grab channels named "events" and put message in each one
		try {
			if (daily) {
				List<IChannel> channels = client.getChannels();
				for (IChannel channel : channels) {
					if (channel.getName().equals("events")) {
						new MessageBuilder(client).withChannel(channel).withContent(events).build();
					}
				}
			} else {
				if (channel != null) {
					new MessageBuilder(client).withChannel(channel).withContent(events).build();
				}

			}
		} catch (RateLimitException | DiscordException | MissingPermissionsException e) {
			logger.error("Error listing events to Discord channels", e);
		}
	}

}
