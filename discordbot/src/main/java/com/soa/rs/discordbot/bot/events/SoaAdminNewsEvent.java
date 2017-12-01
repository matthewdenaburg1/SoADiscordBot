package com.soa.rs.discordbot.bot.events;

import java.util.List;

import com.soa.rs.discordbot.util.NoDefinedRolesException;
import com.soa.rs.discordbot.util.SoaClientHelper;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;

/**
 * The SoaAdminNewsEvent can be used for an admin (Lian/Eldar or Jeff) to
 * provide information for the bot to repeat in the specified channel.
 * <p>
 * NOTE: This event is not to be displayed within the Help menu printed by
 * <tt>SoaHelpEvent</tt>.
 */
public class SoaAdminNewsEvent extends AbstractSoaMsgRcvEvent {

	/**
	 * The rest of the arguments entered for the message
	 */
	private String args[];

	/**
	 * Constructor
	 * 
	 * @param event
	 *            MessageReceivedEvent
	 */
	public SoaAdminNewsEvent(MessageReceivedEvent event) {
		super(event);
	}

	/**
	 * Set the arguments
	 * 
	 * @param args
	 *            arguments
	 */
	public void setArgs(String[] args) {
		this.args = args;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soa.rs.discordbot.bot.events.AbstractSoaMsgRcvEvent#executeEvent()
	 */
	@Override
	public void executeEvent() {
		try {
			if (permittedToExecuteEvent()) {
				String channelName = args[1];
				int i;
				StringBuilder sb = new StringBuilder();
				for (i = 2; i < args.length; i++) {
					sb.append(args[i] + " ");
				}

				IDiscordClient client = getEvent().getClient();
				List<IChannel> channels = client.getChannels();
				for (IChannel channel : channels) {
					if (channel.getName().equals(channelName)) {
						SoaClientHelper.sendMsgToChannel(channel, sb.toString());
					}
				}
			}
		} catch (NoDefinedRolesException e) {
		}
	}
}
