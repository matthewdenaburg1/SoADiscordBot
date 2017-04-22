package com.soa.rs.discordbot.util;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.Image;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

/**
 * This utility class provides simple access to functions which interact with
 * Discord's API that may be rate limited. RequestBuffers are used to ensure
 * actions fired by various events are completed successfully, as if a RateLimit
 * is encountered the request will be tried again when the RateLimit has
 * expired.
 *
 */
public class SoaClientHelper {

	/**
	 * Sends a message to a channel
	 * 
	 * @param channel
	 *            The channel to receive the message
	 * @param msg
	 *            The message to send to the channel
	 */
	public static void sendMsgToChannel(IChannel channel, String msg) {
		RequestBuffer.request(() -> {
			try {
				channel.sendMessage(msg);
			} catch (MissingPermissionsException | DiscordException e) {
				SoaLogging.getLogger().error("Error sending message: " + e.getMessage(), e);
			}
		});
	}

	/**
	 * Sets the bot's avatar
	 * 
	 * @param client
	 *            The client object which represents the bot
	 * @param img
	 *            The image to set as the bot's avatar
	 */
	public static void setBotAvatar(IDiscordClient client, Image img) {
		RequestBuffer.request(() -> {
			try {
				client.changeAvatar(img);
			} catch (DiscordException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	/**
	 * Sets the bot's account name
	 * 
	 * @param client
	 *            The client object which represents the bot
	 * @param name
	 *            The desired account name
	 */
	public static void setBotName(IDiscordClient client, String name) {
		RequestBuffer.request(() -> {
			try {
				client.changeUsername(name);
			} catch (DiscordException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	/**
	 * Sets the bot's 'playing' status
	 * 
	 * @param client
	 *            The client object which represents the bot
	 * @param playing
	 *            The string containing the name of the game to show as being
	 *            played
	 */
	public static void setBotPlaying(IDiscordClient client, String playing) {
		RequestBuffer.request(() -> {
			client.changePlayingText(playing);
		});
	}

}
