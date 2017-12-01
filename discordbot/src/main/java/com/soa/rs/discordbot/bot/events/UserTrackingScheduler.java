package com.soa.rs.discordbot.bot.events;

import java.util.Timer;
import java.util.TimerTask;

import sx.blah.discord.api.IDiscordClient;

/**
 * Handles the scheduling of the Tracking task.
 */
public class UserTrackingScheduler implements SoaTaskScheduler {

	/**
	 * The timer object
	 */
	private Timer timer;

	/**
	 * The task object
	 */
	private UserTrackingTask task;

	/**
	 * Constructor
	 * 
	 * @param client
	 *            The Discord Client object.
	 */
	public UserTrackingScheduler(IDiscordClient client) {
		task = new UserTrackingTask(client);
	}

	/**
	 * Schedules the tracking task. The task is set to run once a minute.
	 */
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

	/**
	 * The tracking task to be scheduled
	 */
	private class UserTrackingTask extends TimerTask {

		/**
		 * Constructor.
		 */
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
