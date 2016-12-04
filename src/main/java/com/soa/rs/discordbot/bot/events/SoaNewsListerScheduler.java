package com.soa.rs.discordbot.bot.events;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sx.blah.discord.api.IDiscordClient;

public class SoaNewsListerScheduler implements SoaTaskScheduler {

	private Timer timer;
	private IDiscordClient client;
	private String url;
	private static final Logger logger = LogManager.getLogger();

	public SoaNewsListerScheduler(IDiscordClient client, String url) {
		this.client = client;
		this.url = url;
	}

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
		timer.schedule(new SoaNewsListerTask(url, client, this), cal.getTime());
		logger.info("Set next execution time for SoaNewsListerTask to be " + cal.getTime().toString());

	}

	@Override
	public void rescheduleTask() {
		this.timer.cancel();
		scheduleTask();
	}

}
