package com.soa.rs.triviacreator.util;

/**
 * An enumeration defining known servers and their Discord IDs.
 */
public enum Servers {
	/**
	 * Spirits of Arianwyn's server
	 */
	SPIRITS_OF_ARIANWYN("Spirits of Arianwyn", "133922153010692096");

	/**
	 * The server name
	 */
	private String serverName;
	
	/**
	 * The server's ID in string format
	 */
	private String serverId;

	/**'
	 * Creates a Server
	 * @param name The name of the server
	 * @param id The server's ID in string format
	 */
	Servers(String name, String id) {
		this.serverName = name;
		this.serverId = id;
	}

	/**
	 * Gets the server name
	 * @return The server name
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * Gets the server's ID in string format
	 * @return The server's ID in string format
	 */
	public String getServerId() {
		return serverId;
	}

}
