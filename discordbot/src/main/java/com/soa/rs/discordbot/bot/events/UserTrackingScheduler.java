package com.soa.rs.discordbot.bot.events;

import java.util.Timer;
import java.util.TimerTask;

import sx.blah.discord.api.IDiscordClient;

public class UserTrackingScheduler implements SoaTaskScheduler {

	private Timer timer;
	private UserTrackingTask task;

	public UserTrackingScheduler(IDiscordClient client) {
		task = new UserTrackingTask(client);
	}

	@Override
	public void scheduleTask() {
		timer = new Timer();
		timer.schedule(task, 0, 60000);
	}

	@Override
	public void rescheduleTask() {
		/*
		 * No need to reschedule, will run at fixed delay
		 */
	}

	private class UserTrackingTask extends TimerTask {

		public UserTrackingTask(IDiscordClient client) {
			UserTrackingUpdater.getInstance().setClient(client);
		}

		@Override
		public void run() {
			UserTrackingUpdater.getInstance().loadInformation();
			UserTrackingUpdater.getInstance().populateInformation();
		}

	}

}
