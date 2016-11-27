package com.soa.rs.discordbot.bot;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soa.rs.discordbot.util.RssParser;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.Image;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class ReadyEventListener implements IListener<ReadyEvent> {

	private String eventURL = null;
	private Timer timer;
	private IDiscordClient client;
	private static final Logger logger = LogManager.getLogger();

	@Override
	public void handle(ReadyEvent event) {

		client = event.getClient();
		setDiscordUserSettings();
		timer = new Timer();
		timer.schedule(new SoaEventLister(), 2);

	}

	private void setDiscordUserSettings() {

		try {
			logger.info("Setting bot avatar");
			client.changeAvatar(Image.forUrl("png", "http://soa-rs.com/img/greenlogo.png"));
			logger.info("Setting bot username to 'SoA'");
			client.changeUsername("SoA");

		} catch (DiscordException | RateLimitException e) {
			logger.error("Error updating username or avatar", e);
		}
	}

	public void setUrl(String url) {
		this.eventURL = url;
	}

	class SoaEventLister extends TimerTask {

		@Override
		public void run() {
			logger.info("EventLister task started");
			RssParser parser = new RssParser(eventURL);
			String events = parser.parseEventFeed();

			// Grab channels named "events" and put message in each one
			try {
				List<IChannel> channels = client.getChannels();
				for (IChannel channel : channels) {
					if (channel.getName().equals("events")) {
						new MessageBuilder(client).withChannel(channel).withContent(events).build();
					}
				}
			} catch (RateLimitException | DiscordException | MissingPermissionsException e) {
				logger.error("Error listing events to Discord channels", e);
			}

			// Schedule to run at midnight UTC next day (game reset)
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.add(Calendar.DATE, 1);

			timer.cancel();
			timer = new Timer();
			timer.schedule(new SoaEventLister(), cal.getTime());
			logger.info("EventLister task finished, next runtime: " + cal.getTime().toString());

		}

	}

}
