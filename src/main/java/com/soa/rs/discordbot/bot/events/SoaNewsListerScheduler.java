package com.soa.rs.discordbot.bot.events;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soa.rs.discordbot.cfg.DiscordCfg;

import sx.blah.discord.api.IDiscordClient;

/**
 * Handles the scheduling for the SoaNewsLister automated task
 */
public class SoaNewsListerScheduler implements SoaTaskScheduler {

	private Timer timer;
	private IDiscordClient client;
	private String url;
	private boolean firstRun = true;
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Creates a SoaNewsListerScheduler. Task must be scheduled by calling the
	 * <tt>scheduleTask</tt> method.
	 * 
	 * @param client
	 *            Discord Client to be used by the task.
	 * @param url
	 *            URL to the events RSS feed from the SoA forum calendar
	 */
	public SoaNewsListerScheduler(IDiscordClient client, String url) {
		this.client = client;
		this.url = url;
		DiscordCfg.getInstance().setNewsLastPost(new Date());
		logger.info("Setting NewsLastPost to " + DiscordCfg.getInstance().getNewsLastPost().toString());
	}

	/**
	 * Schedule the next run of the task to be 30 minutes from the previous.
	 */
	@Override
	public void scheduleTask() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		if (cal.get(Calendar.MINUTE) >= 0 && cal.get(Calendar.MINUTE) < 30)
			cal.set(Calendar.MINUTE, 30);
		else {
			cal.add(Calendar.HOUR, 1);
			cal.set(Calendar.MINUTE, 0);
		}

		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		timer.schedule(new SoaNewsListerTask(url, client, this), cal.getTime());

		logger.info("Set next execution time for SoaNewsListerTask to be " + cal.getTime().toString());

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
