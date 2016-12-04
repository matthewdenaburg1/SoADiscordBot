package com.soa.rs.discordbot.bot.events;

import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * The SoaNewsListerTask is executed once per hour, and will fetch information
 * from the News and Announcements forum along with the Promotions forum to
 * announce within the Discord channel.
 */
public class SoaNewsListerTask extends TimerTask {

	/**
	 * The news feed URL
	 */
	private String url;
	/**
	 * The discord client object in use for interacting with the Discord API.
	 */
	private IDiscordClient client;
	/**
	 * The scheduler for the TimerTask. This is used for the purpose of
	 * rescheduling the next run.
	 */
	private SoaNewsListerScheduler scheduler;
	private static final Logger logger = LogManager.getLogger();

	/**
	 * The constructor is used to instantiate the TimerTask
	 * 
	 * @param url
	 *            the URL of the RSS feed from the forums.
	 * @param client
	 *            The discord client object to be used for interacting with the
	 *            Discord API
	 * @param scheduler
	 *            The task scheduler to be used for rescheduling the task after
	 *            completion.
	 */
	public SoaNewsListerTask(String url, IDiscordClient client, SoaNewsListerScheduler scheduler) {
		this.url = url;
		this.client = client;
		this.scheduler = scheduler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		logger.info("Executing SoaNewsListerTask");
		SoaNewsListParser parser = new SoaNewsListParser(this.url);
		String news = parser.parse();

		if (news != null) {
			try {
				List<IChannel> channels = client.getChannels();
				for (IChannel channel : channels) {
					if (channel.getName().equals("shoutbox")) {
						new MessageBuilder(client).withChannel(channel).withContent(news).build();
					}
				}

			} catch (RateLimitException | DiscordException | MissingPermissionsException e) {
				logger.error("Error listing news to Discord channels", e);
			}
		} else {
			logger.debug("No events to list to Discord.");
		}

		this.scheduler.rescheduleTask();

	}
}
