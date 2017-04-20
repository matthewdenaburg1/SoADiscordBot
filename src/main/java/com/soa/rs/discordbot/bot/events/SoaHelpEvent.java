package com.soa.rs.discordbot.bot.events;

import com.soa.rs.discordbot.util.SoaClientHelper;

import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

/**
 * The SoaHelpEvent displays a help message of the bot's commands
 */
public class SoaHelpEvent extends AbstractSoaMsgRcvEvent {

	/**
	 * Constructor
	 * 
	 * @param event
	 *            MessageReceivedEvent
	 */
	public SoaHelpEvent(MessageReceivedEvent event) {
		super(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soa.rs.discordbot.bot.events.AbstractSoaMsgRcvEvent#executeEvent()
	 */
	@Override
	public void executeEvent() {
		StringBuilder sb = new StringBuilder();
		sb.append("```Help: SoA Commands\n");
		sb.append(".events - Displays an up-to-date listing of today's events.\n");
		sb.append(".info - Display's bot info.\n");
		sb.append(".music - Use .music help for music commands (Arquendi/Lian/Eldar/Temporary DJ rank only).\n");
		sb.append("```");

		SoaClientHelper.sendMsgToChannel(getEvent().getMessage().getChannel(), sb.toString());
	}
}
