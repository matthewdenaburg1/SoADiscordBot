package com.soa.rs.discordbot.bot.events;

import java.util.List;
import java.util.TimerTask;

import com.soa.rs.discordbot.util.SoaClientHelper;
import com.soa.rs.discordbot.util.SoaLogging;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;

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
		SoaLogging.getLogger().info("Executing SoaNewsListerTask");
		SoaNewsListParser parser = new SoaNewsListParser(this.url);
		String news = parser.parse();

		if (news != null && !news.equals("")) {
			List<IChannel> channels = client.getChannels();
			for (IChannel channel : channels) {
				if (channel.getName().equals("shoutbox")) {
					SoaClientHelper.sendMsgToChannel(channel, news);
				}
			}

		} else {
			SoaLogging.getLogger().debug("No news to list to Discord.");
		}

		this.scheduler.rescheduleTask();

	}
}
