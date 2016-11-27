package com.soa.rs.discordbot.bot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

public class SoaDiscordBot {

	private String token;
	private String eventURL;
	private IDiscordClient client = null;

	public void start(String token, String eventURL) {
		this.token = token;
		this.eventURL = eventURL;
		try {
			client = loginClient();
		} catch (DiscordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (client != null) {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
