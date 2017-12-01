package com.soa.rs.discordbot.d4j.testimpl;

import java.awt.Color;
import java.util.EnumSet;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

public class MockRole implements IRole {

	private long id;
	private String name;

	@Override
	public IDiscordClient getClient() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IShard getShard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRole copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getLongID() {
		// TODO Auto-generated method stub
		return this.id;
	}

	public void setLongID(long id) {
		this.id = id;
	}

	@Override
	public int getPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public EnumSet<Permissions> getPermissions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isManaged() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isHoisted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMentionable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IGuild getGuild() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void edit(Color color, boolean hoist, String name, EnumSet<Permissions> permissions, boolean isMentionable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeColor(Color color) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeHoist(boolean hoist) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changePermissions(EnumSet<Permissions> permissions) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeMentionable(boolean isMentionable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isEveryoneRole() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDeleted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String mention() {
		// TODO Auto-generated method stub
		return null;
	}

}
