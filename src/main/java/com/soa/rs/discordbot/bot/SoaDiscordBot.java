package com.soa.rs.discordbot.bot;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soa.rs.discordbot.cfg.DiscordCfg;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;

/**
 * The SoaDiscordBot class contains the necessary logic to log the bot into
 * discord and register appropriate event listeners for the bot to interact with
 * Discord.
 * 
 */
public class SoaDiscordBot {

	/**
	 * Discord client object; interaction with Discord will be done through this
	 * object.
	 */
	private IDiscordClient client = null;
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Start the bot. This will log the bot into Discord and register
	 * appropriate event listeners
	 */
	public void start() {
		logger.info("Logging-in bot with Token: " + DiscordCfg.getInstance().getToken());
		try {
			client = loginClient();
		} catch (DiscordException e) {
			// TODO Auto-generated catch block
			logger.error("Error with logging in", e);
		}
		if (client != null) {
			logger.info("Logged in to Discord");
			DiscordCfg.getInstance().setLaunchTime(LocalDateTime.now());
			EventDispatcher dispatcher = client.getDispatcher();
			dispatcher.registerListener(new ReadyEventListener());
			dispatcher.registerListener(new MessageReceivedEventListener());
		}
	}

	/**
	 * Process logging the bot into Discord
	 * 
	 * @return logged-in Discord client
	 * @throws DiscordException
	 */
	private IDiscordClient loginClient() throws DiscordException {
		ClientBuilder clientBuilder = new ClientBuilder();
		clientBuilder.withToken(DiscordCfg.getInstance().getToken());
		return clientBuilder.login();
	}

	/**
	 * Log the bot out of Discord.
	 */
	public void disconnect() {
		try {
			client.logout();
		} catch (DiscordException e) {
			logger.error("Error when disconnecting from Discord", e);
		}
	}

}
