package com.soa.rs.discordbot.bot;

import com.soa.rs.discordbot.bot.events.SoaEventListerScheduler;
import com.soa.rs.discordbot.bot.events.SoaNewsListerScheduler;
import com.soa.rs.discordbot.bot.events.SoaTaskScheduler;
import com.soa.rs.discordbot.cfg.DiscordCfg;
import com.soa.rs.discordbot.util.SoaDiscordBotConstants;
import com.soa.rs.discordbot.util.SoaLogging;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.Image;
import sx.blah.discord.util.RateLimitException;

/**
 * The Ready Event Listener handles the configuration of bot settings along with
 * starting any automated tasks which will run periodically during the bot's
 * runtime. The <tt>ReadyEvent</tt> is triggered upon the bot being ready to
 * interact with the Discord API.
 */
public class ReadyEventListener implements IListener<ReadyEvent> {

	private IDiscordClient client;

	/**
	 * Handles the ReadyEvent sent by the Discord API, this sets up the bot's
	 * user settings and schedules appropriate automated tasks.
	 */
	@Override
	public void handle(ReadyEvent event) {

		client = event.getClient();
		setDiscordUserSettings();
		SoaTaskScheduler listScheduler = new SoaEventListerScheduler(client,
				DiscordCfg.getInstance().getEventCalendarUrl());
		listScheduler.scheduleTask();
		SoaTaskScheduler newsScheduler = new SoaNewsListerScheduler(client, DiscordCfg.getInstance().getNewsUrl());
		newsScheduler.scheduleTask();
	}

	/**
	 * Sets up the bot's user settings
	 */
	private void setDiscordUserSettings() {

		try {
			SoaLogging.getLogger().info("Setting bot avatar");
			client.changeAvatar(Image.forUrl("png", SoaDiscordBotConstants.AVATAR_URL));
			SoaLogging.getLogger().info("Setting bot username to '" + SoaDiscordBotConstants.BOT_USERNAME + "'");
			client.changeUsername(SoaDiscordBotConstants.BOT_USERNAME);
			client.changeStatus(Status.game(SoaDiscordBotConstants.PLAYING_STATUS));

		} catch (DiscordException | RateLimitException e) {
			SoaLogging.getLogger().error("Error updating username or avatar", e);
		}
	}

}
