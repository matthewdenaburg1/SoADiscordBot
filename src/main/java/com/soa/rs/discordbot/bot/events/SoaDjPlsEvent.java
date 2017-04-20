package com.soa.rs.discordbot.bot.events;

import java.util.Random;

import com.soa.rs.discordbot.util.SoaClientHelper;

import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

/**
 * This event was created for the purposes of annoying DJ and can be considered
 * an 'easter egg'
 */
public class SoaDjPlsEvent extends AbstractSoaMsgRcvEvent {

	/**
	 * Create an instance of the event
	 * 
	 * @param event
	 */
	public SoaDjPlsEvent(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void executeEvent() {
		if (!getEvent().getMessage().getAuthor().isBot()) {
			Random rndm = new Random();
			int msg = rndm.nextInt(4) + 1;
			String message = null;
			switch (msg) {
			case 1:
				message = "DJ pls";
				break;
			case 2:
				message = "DJ is a noob";
				break;
			case 3:
				message = "#blameDJ";
				break;
			case 4:
				message = "Thank you for refreshing your twitter page";
				break;
			default:
				break;
			}

			SoaClientHelper.sendMsgToChannel(getEvent().getMessage().getChannel(), message);
		}
	}

}
