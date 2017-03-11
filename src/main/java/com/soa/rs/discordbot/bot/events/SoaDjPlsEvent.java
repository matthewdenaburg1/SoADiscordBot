package com.soa.rs.discordbot.bot.events;

import java.util.Random;

import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class SoaDjPlsEvent extends AbstractSoaMsgRcvEvent {

	public SoaDjPlsEvent(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void executeEvent() {
		if (!getEvent().getMessage().getAuthor().isBot()) {
			try {
				Random rndm = new Random();
				int msg = rndm.nextInt(4) + 1;
				switch (msg) {
				case 1:
					getEvent().getMessage().getChannel().sendMessage("DJ pls");
					break;
				case 2:
					getEvent().getMessage().getChannel().sendMessage("DJ is a noob");
					break;
				case 3:
					getEvent().getMessage().getChannel().sendMessage("#blameDJ");
					break;
				case 4:
					getEvent().getMessage().getChannel().sendMessage("Thank you for refreshing your twitter page");
					break;
				default:
					break;
				}
			} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
