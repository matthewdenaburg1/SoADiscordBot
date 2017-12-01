package com.soa.rs.discordbot.bot.events;

/**
 * Generic interface used to represent a repeated, automated task.
 */
public interface SoaTaskScheduler {

	/**
	 * Schedule the task for the next run
	 */
	public abstract void scheduleTask();

	/**
	 * Cancel the existing timer and then call <tt>scheduleTask</tt> to schedule the
	 * next run of the task.
	 */
	public abstract void rescheduleTask();

}
