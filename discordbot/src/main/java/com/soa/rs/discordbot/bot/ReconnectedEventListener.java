package com.soa.rs.discordbot.bot;

import com.soa.rs.discordbot.cfg.DiscordCfgFactory;
import com.soa.rs.discordbot.util.SoaClientHelper;

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
	 * 
	 * @see
	 * sx.blah.discord.api.events.IListener#handle(sx.blah.discord.api.events.Event)
	 */
	@Override
	public void handle(ReconnectSuccessEvent event) {
		while (!event.getClient().isReady()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (DiscordCfgFactory.getConfig().getDefaultStatus() != null
				&& !DiscordCfgFactory.getConfig().getDefaultStatus().trim().isEmpty())
			SoaClientHelper.setBotPlaying(event.getClient(), DiscordCfgFactory.getConfig().getDefaultStatus());
	}

}
