package com.soa.rs.triviacreator.util;

/**
 * An enumeration defining known SoA Channels and their Discord IDs. This only
 * includes channels which might be used for Trivia in SoA's Discord Server.
 */
public enum Channels {
	/**
	 * The shoutbox channel
	 */
	SHOUTBOX("#shoutbox", "133922153010692096"), 
	/**
	 * The events channel
	 */
	EVENTS("#events", "133942883274326016");

	/**
	 * The channel's name
	 */
	private String channelName;
	
	/**
	 * The channel's Discord ID in string format
	 */
	private String channelId;

	/**
	 * Creates a Discord Channel
	 * @param channelName The name of the channel
	 * @param channelId The ID of the channel in string format
	 */
	Channels(String channelName, String channelId) {
		this.channelName = channelName;
		this.channelId = channelId;
	}

	/**
	 * Gets the channel's name
	 * @return The channel's name
	 */
	public String getChannelName() {
		return channelName;
	}

	/**
	 * Gets the channel's Discord ID in string format
	 * @return The Discord ID in string format
	 */
	public String getChannelId() {
		return channelId;
	}

}
