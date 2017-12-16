package com.soa.rs.discordbot.bot.events;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.soa.rs.discordbot.cfg.DiscordCfgFactory;
import com.soa.rs.discordbot.d4j.testimpl.CreateD4JObjects;
import com.soa.rs.discordbot.d4j.testimpl.MockDiscordClient;
import com.soa.rs.discordbot.d4j.testimpl.MockGuild;
import com.soa.rs.discordbot.d4j.testimpl.MockUser;
import com.soa.rs.discordbot.jaxb.CurrentUser;
import com.soa.rs.discordbot.jaxb.DisplayNames;
import com.soa.rs.discordbot.jaxb.Guild;
import com.soa.rs.discordbot.jaxb.GuildUsers;
import com.soa.rs.discordbot.jaxb.LeftUser;
import com.soa.rs.discordbot.jaxb.LeftUsers;
import com.soa.rs.discordbot.jaxb.RankList;
import com.soa.rs.discordbot.jaxb.TrackedInformation;
import com.soa.rs.discordbot.jaxb.User;
import com.soa.rs.discordbot.jaxb.UserTrackingEvent;
import com.soa.rs.discordbot.util.SoaLogging;

import sx.blah.discord.handle.obj.StatusType;

public class UserTrackingUpdaterTest {

	@Before
	public void setupTest() throws DatatypeConfigurationException {
		TrackedInformation info = new TrackedInformation();

		Guild guild = new Guild();
		guild.setGuildId(1234567890);
		guild.setGuildName("Test Guild");

		GuildUsers gusers = new GuildUsers();

		CurrentUser user = new CurrentUser();
		user.setUserId(213456789);
		user.setUserName("@user#1234");
		DisplayNames names = new DisplayNames();
		names.getDisplayName().add("User");
		user.setDisplayNames(names);
		user.setJoined(getCurrentDate());
		user.setLastOnline(getCurrentDate());
		gusers.getUser().add(user);

		user = new CurrentUser();
		user.setUserId(312456789);
		user.setUserName("@user2#5678");
		names = new DisplayNames();
		names.getDisplayName().add("User2");
		user.setDisplayNames(names);
		user.setJoined(getCurrentDate());
		user.setLastOnline(getCurrentDate());
		gusers.getUser().add(user);

		LeftUsers lusers = new LeftUsers();

		LeftUser luser = new LeftUser();
		luser.setUserId(456789123);
		luser.setUserName("@leftUser#9012");
		names = new DisplayNames();
		names.getDisplayName().add("Left User");
		luser.setDisplayNames(names);
		luser.setLeft(getCurrentDate());

		guild.setGuildUsers(gusers);
		guild.setLeftUsers(lusers);

		info.getGuild().add(guild);

		UserTrackingUpdater.getInstance().setInformation(info);

		DiscordCfgFactory.getConfig().setUserTrackingEvent(new UserTrackingEvent());
		RankList list = new RankList();
		list.getRole().add("TestRole");
		DiscordCfgFactory.getConfig().getUserTrackingEvent().setCanUpdateQuery(list);

		SoaLogging.initializeLogging();
	}

	@Test
	public void testSearch() {
		Map<User, Long> results = UserTrackingUpdater.getInstance().searchUsers("User", false);
		Iterator<User> mapIter = results.keySet().iterator();
		boolean user = false;
		boolean user2 = false;
		while (mapIter.hasNext()) {
			User curUser = mapIter.next();
			if (curUser.getDisplayNames().getDisplayName().contains("User")) {
				user = true;
			} else if (curUser.getDisplayNames().getDisplayName().contains("User2")) {
				user2 = true;
			}
		}

		Assert.assertTrue(user);
		Assert.assertTrue(user2);
	}

	@Test
	public void testSearchSpecificUser() {
		Map<User, Long> results = UserTrackingUpdater.getInstance().searchUsers("@user#1234", false);
		Iterator<User> mapIter = results.keySet().iterator();
		boolean user = false;
		boolean user2 = false;
		while (mapIter.hasNext()) {
			User curUser = mapIter.next();
			if (curUser.getDisplayNames().getDisplayName().contains("User")) {
				user = true;
			} else if (curUser.getDisplayNames().getDisplayName().contains("User2")) {
				user2 = true;
			}
		}

		Assert.assertTrue(user);
		Assert.assertFalse(user2);
	}

	@Test
	public void testSearchMustMatch() {
		Map<User, Long> results = UserTrackingUpdater.getInstance().searchUsers("User", true);
		Iterator<User> mapIter = results.keySet().iterator();
		boolean user = false;
		boolean user2 = false;
		while (mapIter.hasNext()) {
			User curUser = mapIter.next();
			if (curUser.getDisplayNames().getDisplayName().contains("User")) {
				user = true;
			} else if (curUser.getDisplayNames().getDisplayName().contains("User2")) {
				user2 = true;
			}
		}

		Assert.assertTrue(user);
		Assert.assertFalse(user2);
	}

	@Test
	public void testPopulateTwoUsers() {
		UserTrackingUpdater.getInstance().setInformation(new TrackedInformation());

		MockDiscordClient client = CreateD4JObjects.createMockClient();

		MockGuild guild = CreateD4JObjects.createMockGuild("Bot_Testing", 12345);

		MockUser muser1 = CreateD4JObjects.createMockUser("Test user 1", "1234", 678910, StatusType.ONLINE);
		MockUser muser2 = CreateD4JObjects.createMockUser("Test user 2", "5678", 111213, StatusType.ONLINE);

		guild.addUser(muser1);
		guild.addUser(muser2);

		client.addGuild(guild);

		UserTrackingUpdater.getInstance().setClient(client);
		UserTrackingUpdater.getInstance().populateInformation();

		TrackedInformation newinfo = null;

		try {
			InputStream stream = UserTrackingUpdater.getInstance().writeInfoToStream();
			newinfo = unmarshallData(stream);
		} catch (Exception e) {
			Assert.fail();
		}

		Assert.assertEquals(1, newinfo.getGuild().size());
		Assert.assertEquals(12345, newinfo.getGuild().get(0).getGuildId());
		Assert.assertEquals("Bot_Testing", newinfo.getGuild().get(0).getGuildName());
		Assert.assertEquals(2, newinfo.getGuild().get(0).getGuildUsers().getUser().size());

		CurrentUser user1 = newinfo.getGuild().get(0).getGuildUsers().getUser().get(0);
		Assert.assertEquals(678910, user1.getUserId());
		Assert.assertEquals("Test user 1", user1.getDisplayNames().getDisplayName().get(0));
		Assert.assertEquals("@Test user 1#1234", user1.getUserName());
		Assert.assertNotEquals(user1.getJoined(), user1.getLastOnline());

		CurrentUser user2 = newinfo.getGuild().get(0).getGuildUsers().getUser().get(1);
		Assert.assertEquals(111213, user2.getUserId());
		Assert.assertEquals("Test user 2", user2.getDisplayNames().getDisplayName().get(0));
		Assert.assertEquals("@Test user 2#5678", user2.getUserName());
		Assert.assertNotEquals(user1.getJoined(), user2.getLastOnline());
	}

	@Test
	public void testLeftUser() {
		UserTrackingUpdater.getInstance().setInformation(new TrackedInformation());

		MockDiscordClient client = CreateD4JObjects.createMockClient();

		MockGuild guild = CreateD4JObjects.createMockGuild("Bot_Testing", 1234567890);

		MockUser muser1 = CreateD4JObjects.createMockUser("Test user 1", "1234", 678910, StatusType.ONLINE);
		MockUser muser2 = CreateD4JObjects.createMockUser("Test user 2", "5678", 111213, StatusType.ONLINE);

		guild.addUser(muser1);
		guild.addUser(muser2);

		client.addGuild(guild);

		UserTrackingUpdater.getInstance().setClient(client);
		UserTrackingUpdater.getInstance().populateInformation();

		guild.removeUser(muser2);

		UserTrackingUpdater.getInstance().setClient(client);
		UserTrackingUpdater.getInstance().populateInformation();

		TrackedInformation newinfo = null;

		try {
			InputStream stream = UserTrackingUpdater.getInstance().writeInfoToStream();
			newinfo = unmarshallData(stream);
		} catch (Exception e) {
			Assert.fail();
		}

		Assert.assertEquals(1, newinfo.getGuild().get(0).getGuildUsers().getUser().size());
		Assert.assertEquals(1, newinfo.getGuild().get(0).getLeftUsers().getUser().size());

		Assert.assertEquals(111213, newinfo.getGuild().get(0).getLeftUsers().getUser().get(0).getUserId());
		Assert.assertEquals("@Test user 2#5678",
				newinfo.getGuild().get(0).getLeftUsers().getUser().get(0).getUserName());
	}

	@Test
	public void testOneUserOffline() {
		UserTrackingUpdater.getInstance().setInformation(new TrackedInformation());

		MockDiscordClient client = CreateD4JObjects.createMockClient();

		MockGuild guild = CreateD4JObjects.createMockGuild("Bot_Testing", 12345);

		MockUser muser1 = CreateD4JObjects.createMockUser("Test user 1", "1234", 678910, StatusType.ONLINE);
		MockUser muser2 = CreateD4JObjects.createMockUser("Test user 2", "5678", 111213, StatusType.OFFLINE);

		guild.addUser(muser1);
		guild.addUser(muser2);

		client.addGuild(guild);

		UserTrackingUpdater.getInstance().setClient(client);
		UserTrackingUpdater.getInstance().populateInformation();

		TrackedInformation newinfo = null;

		try {
			InputStream stream = UserTrackingUpdater.getInstance().writeInfoToStream();
			newinfo = unmarshallData(stream);
		} catch (Exception e) {
			Assert.fail();
		}

		Calendar cal = Calendar.getInstance();

		Assert.assertEquals(cal.get(Calendar.YEAR),
				newinfo.getGuild().get(0).getGuildUsers().getUser().get(0).getLastOnline().getYear());
		Assert.assertEquals(1970, newinfo.getGuild().get(0).getGuildUsers().getUser().get(1).getLastOnline().getYear());
	}

	@Test
	public void testMaxRecentActionsRecorded() {
		UserTrackingUpdater.getInstance().setInformation(new TrackedInformation());

		MockDiscordClient client = CreateD4JObjects.createMockClient();

		MockGuild guild = CreateD4JObjects.createMockGuild("Bot_Testing", 12345);

		MockUser muser1 = CreateD4JObjects.createMockUser("Test user 1", "1234", 678910, StatusType.ONLINE);
		MockUser muser2 = CreateD4JObjects.createMockUser("Test user 2", "5678", 111213, StatusType.OFFLINE);

		guild.addUser(muser1);
		guild.addUser(muser2);

		client.addGuild(guild);

		UserTrackingUpdater.getInstance().setClient(client);
		UserTrackingUpdater.getInstance().populateInformation();

		for (int i = 0; i < 20; i++) {
			muser1.setDisplayName("Test user 1 " + i);
			UserTrackingUpdater.getInstance().populateInformation();
		}

		TrackedInformation newinfo = null;

		try {
			InputStream stream = UserTrackingUpdater.getInstance().writeInfoToStream();
			newinfo = unmarshallData(stream);
		} catch (Exception e) {
			Assert.fail();
		}

		Assert.assertEquals(15, newinfo.getGuild().get(0).getRecentActions().getRecentAction().size());
	}

	/*
	 * Non-test support methods below
	 */
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

	private TrackedInformation unmarshallData(InputStream stream) throws Exception {

		TrackedInformation info = null;

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance("com.soa.rs.discordbot.jaxb");

			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(this.getClass().getResource("/xsd/userTracking.xsd"));

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbUnmarshaller.setSchema(schema);
			info = (TrackedInformation) jaxbUnmarshaller.unmarshal(stream);
		} catch (Exception e) {
			// fail test
			throw e;
		} finally {
			stream.close();
		}

		return info;
	}

}
