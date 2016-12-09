package com.soa.rs.discordbot.bot.events;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soa.rs.discordbot.cfg.DiscordCfg;

import sx.blah.discord.api.IDiscordClient;

public class SoaNewsListerScheduler implements SoaTaskScheduler {

	private Timer timer;
	private IDiscordClient client;
	private String url;
	private boolean firstRun = true;
	private static final Logger logger = LogManager.getLogger();

	public SoaNewsListerScheduler(IDiscordClient client, String url) {
		this.client = client;
		this.url = url;
		DiscordCfg.getInstance().setNewsLastPost(new Date());
		logger.info("Setting NewsLastPost to " + DiscordCfg.getInstance().getNewsLastPost().toString());
	}

	@Override
	public void scheduleTask() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		if (firstRun) {
			if (cal.get(Calendar.MINUTE) > 0 && cal.get(Calendar.MINUTE) < 30)
				cal.set(Calendar.MINUTE, 30);
			else {
				cal.add(Calendar.HOUR, 1);
				cal.set(Calendar.MINUTE, 0);
			}
			firstRun = false;
		}
		else
			cal.add(Calendar.MINUTE, 30);

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
