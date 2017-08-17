package com.soa.rs.discordbot.bot;

import com.soa.rs.discordbot.util.SoaClientHelper;
import com.soa.rs.discordbot.util.SoaDiscordBotConstants;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.shard.ReconnectSuccessEvent;

/**
 * This class will handle resetting the playing status if the bot happens to be
 * disconnected from Discord for a long enough period where the status gets
 * lost.
 */
public class ReconnectedEventListener implements IListener<ReconnectSuccessEvent> {

	/*
	 * (non-Javadoc)
	 * @see sx.blah.discord.api.events.IListener#handle(sx.blah.discord.api.events.Event)
	 */
	@Override
	public void handle(ReconnectSuccessEvent event) {
		SoaClientHelper.setBotPlaying(event.getClient(), SoaDiscordBotConstants.PLAYING_STATUS);
	}

}
