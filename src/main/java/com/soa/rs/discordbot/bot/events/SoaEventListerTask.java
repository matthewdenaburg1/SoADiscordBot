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
 * The SoaEventListerTask can be run in one of two modes:
 * <ul>
 * <li>As a TimerTask, executed once a day. This is registered by the
 * <tt>ReadyEventListener</tt>.</li>
 * <li>From a command in a discord channel.</li>
 * </ul>
 * The method used is determined by the constructor used to instantiate the
 * class.
 */
public class SoaEventListerTask extends TimerTask {

	/**
	 * The URL of the event feed from the forums.
	 */
	private String url;

	/**
	 * The channel from which the command was entered. This is used for
	 * returning the task's output back to the correct channel.
	 */
	private IChannel channel = null;

	/**
	 * The scheduler for the TimerTask. This is used for the purpose of
	 * rescheduling the next run.
	 */
	private SoaEventListerScheduler scheduler = null;

	/**
	 * The discord client object in use for interacting with the Discord API.
	 */
	private IDiscordClient client;

	private static final Logger logger = LogManager.getLogger();

	/**
	 * This constructor is used for running the task as a TimerTask, and is used
	 * by the <tt>ReadyEventListener</tt>.
	 * 
	 * @param url
	 *            The URL of the event feed from the forums.
	 * @param client
	 *            The discord client object to be used for interacting with the
	 *            Discord API.
	 * @param scheduler
	 *            The task scheduler to be used for rescheduling the task after
	 *            completion.
	 */
	public SoaEventListerTask(String url, IDiscordClient client, SoaEventListerScheduler scheduler) {
		this.url = url;
		this.client = client;
		this.scheduler = scheduler;
	}

	/**
	 * This constructor is used for running the task from a command entered into
	 * a Discord channel.
	 * 
	 * @param url
	 *            The URL of the event feed from the forums.
	 * @param client
	 *            The discord client object to be used for interacting with the
	 *            Discord API.
	 * @param channel
	 *            The channel from which the event was triggered.
	 */
	public SoaEventListerTask(String url, IDiscordClient client, IChannel channel) {
		this.url = url;
		this.channel = channel;
	}

	/**
	 * Set the event feed url.
	 * 
	 * @param url
	 *            the event feed url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Set the channel upon which the command came from.
	 * 
	 * @param channel
	 *            the channel upon which the command came from
	 */
	public void setChannel(IChannel channel) {
		this.channel = channel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		logger.info("Executing SoaEventListerTask, scheduled = " + isScheduled());
		SoaEventListParser parser = new SoaEventListParser(this.url);
		String events = parser.parse();

		// Grab channels named "events" and put message in each one
		if (events != null) {
			try {
				if (this.scheduler != null) {
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
		} else {
			logger.error("Error listing events, method returned null.  An exception may have been thrown.");
		}

		if (isScheduled()) {
			this.scheduler.rescheduleTask();
		}

	}

	/**
	 * Check if the task was scheduled, to determine if it must be rescheduled.
	 * 
	 * @return true if scheduled, false if not scheduled.
	 */
	private boolean isScheduled() {
		if (this.scheduler != null) {
			return true;
		}
		return false;
	}

}
