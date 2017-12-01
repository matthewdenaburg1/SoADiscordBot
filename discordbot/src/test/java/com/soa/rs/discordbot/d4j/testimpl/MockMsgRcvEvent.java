package com.soa.rs.discordbot.d4j.testimpl;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

public class MockMsgRcvEvent extends MessageReceivedEvent {

	public MockMsgRcvEvent(IMessage message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public void setClient(IDiscordClient client) {
		this.client = client;
	}

}
