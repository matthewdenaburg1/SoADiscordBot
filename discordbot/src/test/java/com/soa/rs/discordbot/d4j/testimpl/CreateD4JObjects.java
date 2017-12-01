package com.soa.rs.discordbot.d4j.testimpl;

import sx.blah.discord.handle.impl.obj.Presence;
import sx.blah.discord.handle.obj.StatusType;

public class CreateD4JObjects {

	public static MockDiscordClient createMockClient() {
		return new MockDiscordClient();
	}

	public static MockGuild createMockGuild(String guildName, long id) {
		MockGuild guild = new MockGuild();
		guild.setName(guildName);
		guild.setLongID(id);
		return guild;
	}

	public static MockUser createMockUser(String name, String discriminator, long id, StatusType statusType) {
		MockUser user = new MockUser();
		user.setName(name);
		user.setDiscriminator(discriminator);
		Presence presence = new Presence(null, null, statusType);
		user.setPresence(presence);
		user.setId(id);
		return user;
	}

	public static MockChannel createMockChannel(long id) {
		MockChannel channel = new MockChannel();
		channel.setLongID(id);
		return channel;
	}

	public static MockRole createMockRole(long id, String name) {
		MockRole role = new MockRole();
		role.setLongID(id);
		role.setName(name);
		return role;
	}

	public static MockMessage createMockMessage(String content) {
		MockMessage message = new MockMessage();
		message.setContent(content);
		return message;
	}

}
