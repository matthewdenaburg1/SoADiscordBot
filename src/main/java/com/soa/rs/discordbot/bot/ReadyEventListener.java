package com.soa.rs.discordbot.bot;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

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

	@Override
	public void handle(ReadyEvent event) {

		client = event.getClient();
		setDiscordUserSettings();
		timer = new Timer();
		timer.schedule(new SoaEventLister(), 2);

	}

	private void setDiscordUserSettings() {

		try {
			client.changeAvatar(Image.forUrl("png", "http://soa-rs.com/img/greenlogo.png"));
			client.changeUsername("SoA");

		} catch (DiscordException | RateLimitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setUrl(String url) {
		this.eventURL = url;
	}

	class SoaEventLister extends TimerTask {

		@Override
		public void run() {
			RssParser parser = new RssParser(eventURL);
			try {
				List<IChannel> channels = client.getChannels();
				for (IChannel channel : channels) {
					if (channel.getName().equals("events")) {
						new MessageBuilder(client).withChannel(channel).withContent(parser.parseEventFeed()).build();
					}
				}
			} catch (RateLimitException | DiscordException | MissingPermissionsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.add(Calendar.DATE, 1);

			timer.cancel();
			timer = new Timer();
			timer.schedule(new SoaEventLister(), cal.getTime());
			System.out.println("Next runtime: " + cal.getTime().toString());

		}

	}

}
