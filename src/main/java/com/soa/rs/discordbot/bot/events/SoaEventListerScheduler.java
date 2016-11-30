package com.soa.rs.discordbot.bot.events;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sx.blah.discord.api.IDiscordClient;

/**
 * Handles the scheduling for the SoaEventLister automated task.
 */
public class SoaEventListerScheduler implements SoaTaskScheduler {

	private Timer timer;
	private IDiscordClient client;
	private String url;
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Creates a SoaEventListScheduler. Task must be scheduled by calling the
	 * <tt>scheduleTask</tt> method
	 * 
	 * @param client
	 *            Discord Client to be used by the task.
	 * @param url
	 *            URL to the events RSS feed from the SoA forum calendar
	 */
	public SoaEventListerScheduler(IDiscordClient client, String url) {
		this.client = client;
		this.url = url;
	}

	/**
	 * Schedule the next run of the task to be 1 minute after midnight UTC, to
	 * account for time for the forums to roll over feed data.
	 */
	@Override
	public void scheduleTask() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 1);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.DATE, 1);

		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		timer.schedule(new SoaEventListerTask(url, client, this), cal.getTime());
		logger.info("Set next execution time for SoaEventListerTask to be " + cal.getTime().toString());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soa.rs.discordbot.bot.events.SoaTaskScheduler#rescheduleTask()
	 */
	@Override
	public void rescheduleTask() {
		this.timer.cancel();
		scheduleTask();
	}

}
