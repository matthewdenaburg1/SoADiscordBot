package com.soa.rs.discordbot.bot.events;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.xml.sax.SAXException;

import com.soa.rs.discordbot.cfg.DiscordCfgFactory;
import com.soa.rs.discordbot.cfg.XmlReader;
import com.soa.rs.discordbot.cfg.XmlWriter;
import com.soa.rs.discordbot.jaxb.CurrentUser;
import com.soa.rs.discordbot.jaxb.DisplayNames;
import com.soa.rs.discordbot.jaxb.Guild;
import com.soa.rs.discordbot.jaxb.GuildUsers;
import com.soa.rs.discordbot.jaxb.LeftUser;
import com.soa.rs.discordbot.jaxb.LeftUsers;
import com.soa.rs.discordbot.jaxb.TrackedInformation;
import com.soa.rs.discordbot.util.DateAnalyzer;
import com.soa.rs.discordbot.util.SoaLogging;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IPresence;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;

public class UserTrackingUpdater {

	private static final UserTrackingUpdater INSTANCE = new UserTrackingUpdater();

	private IDiscordClient client;
	private XmlWriter writer = new XmlWriter();
	private XmlReader reader = new XmlReader();

	private TrackedInformation information = null;

	private UserTrackingUpdater() {

	}

	public static UserTrackingUpdater getInstance() {
		return INSTANCE;
	}

	public IDiscordClient getClient() {
		return client;
	}

	public void setClient(IDiscordClient client) {
		this.client = client;
	}

	public synchronized void loadInformation() {
		if (information == null) {
			try {
				this.information = reader.loadTrackedConfiguration(DiscordCfgFactory.getConfig().getTrackingFile());
			} catch (JAXBException | SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				this.information = new TrackedInformation();
			}
		}
	}

	public synchronized void populateInformation() {
		List<IGuild> guilds = this.client.getGuilds();
		for (IGuild guild : guilds) {
			long guildId = guild.getLongID();
			boolean guildExists = false;
			for (Guild savedGuild : this.information.getGuild()) {
				if (savedGuild.getGuildId() == guildId) {
					guildExists = true;
					updateGuildListing(savedGuild, guild);
					updateLeftListing(savedGuild, guild);
				}
			}
			if (!guildExists) {
				updateGuildListing(guildId, guild);
			}
		}
		try {
			writer.writeTrackedConfiguration(information, DiscordCfgFactory.getConfig().getTrackingFile());
		} catch (JAXBException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void updateGuildListing(long guildId, IGuild guild) {
		Guild newGuild = new Guild();
		newGuild.setGuildId(guildId);
		newGuild.setGuildName(guild.getName());
		newGuild.setGuildUsers(new GuildUsers());
		newGuild.setLeftUsers(new LeftUsers());
		newGuild = updateGuildListing(newGuild, guild);
		this.information.getGuild().add(newGuild);
	}

	private Guild updateGuildListing(Guild ourGuild, IGuild guild) {
		List<IUser> users = guild.getUsers();
		for (IUser user : users) {
			int position = getUserPosition(ourGuild, user);
			if (position != -1) {
				try {
					CurrentUser curUser = ourGuild.getGuildUsers().getUser().get(position);
					curUser.setUserName("@" + user.getName() + "#" + user.getDiscriminator());
					if (isNewDisplayName(curUser.getDisplayNames(), user.getDisplayName(guild))) {
						curUser.getDisplayNames().getDisplayName().add(0, user.getDisplayName(guild));
					}
					IPresence presence = user.getPresence();
					if (presence.getStatus() != StatusType.OFFLINE) {
						curUser.setLastOnline(getCurrentDate());
					}
				} catch (Exception e) {
					// Something went wrong
				}

			} else {
				try {
					CurrentUser curUser = new CurrentUser();

					curUser.setUserId(user.getLongID());
					curUser.setUserName("@" + user.getName() + "#" + user.getDiscriminator());
					curUser.setDisplayNames(new DisplayNames());
					curUser.getDisplayNames().getDisplayName().add(user.getDisplayName(guild));
					curUser.setJoined(convertDateToCal(guild.getJoinTimeForUser(user)));
					IPresence presence = user.getPresence();
					if (presence.getStatus() != StatusType.OFFLINE) {

						curUser.setLastOnline(getCurrentDate());
					} else {
						curUser.setLastOnline(convertDateToCal(guild.getJoinTimeForUser(user)));
					}

					ourGuild.getGuildUsers().getUser().add(curUser);
				} catch (Exception e) {
					// Something went wrong
				}
			}
		}
		return ourGuild;
	}

	private Guild updateLeftListing(Guild ourGuild, IGuild guild) {
		List<IUser> users = guild.getUsers();

		Iterator<CurrentUser> curIter = ourGuild.getGuildUsers().getUser().iterator();

		while (curIter.hasNext()) {
			CurrentUser user = curIter.next();
			boolean hasLeft = true;
			Iterator<IUser> iter = users.iterator();
			while (iter.hasNext()) {
				IUser checkUser = iter.next();
				SoaLogging.getLogger().trace("Our User " + user.getUserId() + " DiscordUser " + checkUser.getLongID());
				if (checkUser.getLongID() == user.getUserId()) {
					SoaLogging.getLogger().trace("This person has not left");
					hasLeft = false;
				}
			}
			if (hasLeft) {
				try {
					LeftUser leftUser = new LeftUser();
					leftUser.setUserId(user.getUserId());
					leftUser.setUserName(user.getUserName());
					leftUser.setDisplayNames(new DisplayNames());
					leftUser.getDisplayNames().getDisplayName().addAll(user.getDisplayNames().getDisplayName());

					leftUser.setLeft(getCurrentDate());

					ourGuild.getLeftUsers().getUser().add(leftUser);
					curIter.remove();
				} catch (DatatypeConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		Iterator<LeftUser> leftIter = ourGuild.getLeftUsers().getUser().iterator();

		while (leftIter.hasNext()) {
			LeftUser user = leftIter.next();
			if (DateAnalyzer.daysBetween(user.getLeft().toGregorianCalendar().getTime(), new Date()) > 30) {
				leftIter.remove();
			}
		}
		return ourGuild;
	}

	private int getUserPosition(Guild ourGuild, IUser user) {
		int position = -1;
		Iterator<CurrentUser> iter = ourGuild.getGuildUsers().getUser().iterator();
		int i = 0;
		while (iter.hasNext()) {
			CurrentUser checkUser = iter.next();
			if (checkUser.getUserId() == user.getLongID()) {
				position = i;
				return position;
			}
			i++;
		}

		return -1;
	}

	private boolean isNewDisplayName(DisplayNames displayNames, String name) {
		Iterator<String> iter = displayNames.getDisplayName().iterator();
		while (iter.hasNext()) {
			String dispName = iter.next();
			if (dispName.equals(name)) {
				return false;
			}
		}
		return true;
	}

	private XMLGregorianCalendar convertDateToCal(LocalDateTime dateTime) throws DatatypeConfigurationException {
		XMLGregorianCalendar calendar = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar(dateTime.toLocalDate().toString());
		return calendar;
	}

	private XMLGregorianCalendar getCurrentDate() throws DatatypeConfigurationException {
		return getDate(new Date());
	}

	private XMLGregorianCalendar getDate(Date date) throws DatatypeConfigurationException {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		c.setTimeZone(TimeZone.getTimeZone("UTC"));
		XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		return date2;
	}

	public synchronized boolean addKnownNameToUser(String search, String name) {
		boolean updated = false;

		Map<CurrentUser, Long> searchedUsers = searchUsers(search, true);

		if (searchedUsers.size() > 0) {
			Iterator<Entry<CurrentUser, Long>> iter = searchedUsers.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<CurrentUser, Long> entry = iter.next();
				entry.getKey().setKnownName(name);
			}
			updated = true;
		}

		return updated;
	}

	public Map<CurrentUser, Long> searchUsers(String search, boolean mustEqual) {
		Map<CurrentUser, Long> searchedUsers = new HashMap<CurrentUser, Long>();

		for (Guild savedGuild : this.information.getGuild()) {

			List<CurrentUser> guildUsers = savedGuild.getGuildUsers().getUser();
			Iterator<CurrentUser> iter = guildUsers.iterator();
			while (iter.hasNext()) {
				CurrentUser user = iter.next();
				if (mustEqual) {
					// For settingKnownName
					if (user.getUserName().equals(search)) {
						searchedUsers.put(user, savedGuild.getGuildId());
					} else {
						Iterator<String> displayNamesIter = user.getDisplayNames().getDisplayName().iterator();
						while (displayNamesIter.hasNext()) {
							String dispName = displayNamesIter.next();
							if (dispName.equals(search)) {
								searchedUsers.put(user, savedGuild.getGuildId());
							}
						}
					}
				} else {
					// For search
					if (user.getUserName().contains(search) || user.getKnownName().contains(search)) {
						searchedUsers.put(user, savedGuild.getGuildId());
					} else {
						Iterator<String> displayNamesIter = user.getDisplayNames().getDisplayName().iterator();
						while (displayNamesIter.hasNext()) {
							String dispName = displayNamesIter.next();
							if (dispName.contains(search)) {
								searchedUsers.put(user, savedGuild.getGuildId());
							}
						}
					}
				}
			}

		}

		return searchedUsers;
	}

	public InputStream writeInfoToStream() throws JAXBException, IOException, SAXException {
		return this.writer.writeTrackedConfigurationToStream(information);
	}

}
