package com.soa.rs.discordbot.bot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

public class SoaDiscordBot {

	private String token;
	private String eventURL;
	private IDiscordClient client = null;
	private static final Logger logger = LogManager.getLogger();

	public void start(String token, String eventURL) {
		this.token = token;
		this.eventURL = eventURL;
		logger.info("Logging-in bot with Token: " + this.token);
		try {
			client = loginClient();
		} catch (DiscordException e) {
			// TODO Auto-generated catch block
			logger.error("Error with logging in", e);
		}
		if (client != null) {
			logger.info("Logged in to Discord");
			EventDispatcher dispatcher = client.getDispatcher();
			ReadyEventListener rel = new ReadyEventListener();
			rel.setUrl(this.eventURL);
			dispatcher.registerListener(rel);
		}
	}

	private IDiscordClient loginClient() throws DiscordException {
		ClientBuilder clientBuilder = new ClientBuilder();
		clientBuilder.withToken(this.token);
		return clientBuilder.login();
	}

	public void disconnect() {
		try {
			client.logout();
		} catch (RateLimitException | DiscordException e) {
			logger.error("Error when disconnecting from Discord", e);
		}
	}

}
