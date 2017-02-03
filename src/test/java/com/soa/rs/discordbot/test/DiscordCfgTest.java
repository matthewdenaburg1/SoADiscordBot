package com.soa.rs.discordbot.test;

import java.util.Date;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.soa.rs.discordbot.jaxb.DiscordConfiguration;

public class DiscordCfgTest {

	public DiscordConfiguration cfg = null;

	@Before
	public void setUpJaxbEnvironment() {
		cfg = new DiscordConfiguration();
		cfg.setDiscordToken("DToken1");
		cfg.setEventUrl("http://1.google.com");
		cfg.setNewsUrl("http://2.google.com");
	}

	@Test
	public void testLoadFromDiscordConfiguration() {
		MockDiscordCfg mockDiscordCfg = new MockDiscordCfg();

		mockDiscordCfg.loadFromDiscordConfiguration(cfg);

		Assert.assertEquals(cfg.getDiscordToken(), mockDiscordCfg.getToken());
		Assert.assertEquals(cfg.getEventUrl(), mockDiscordCfg.getEventCalendarUrl());
		Assert.assertEquals(cfg.getNewsUrl(), mockDiscordCfg.getNewsUrl());

		/* Check to ensure they match the initial values */
		Assert.assertEquals("DToken1", mockDiscordCfg.getToken());
		Assert.assertEquals("http://1.google.com", mockDiscordCfg.getEventCalendarUrl());
		Assert.assertEquals("http://2.google.com", mockDiscordCfg.getNewsUrl());
	}

	@Test
	public void testLoadFromFile() {
		MockDiscordCfg mockDiscordCfg = new MockDiscordCfg();
		try {
			mockDiscordCfg.loadFromFile(this.getClass().getResource("/config-test.xml").getPath());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Assert.assertEquals(mockDiscordCfg.getToken(), "DToken6");
		Assert.assertEquals(mockDiscordCfg.getEventCalendarUrl(), "http://forums.soa-rs.com/");
		Assert.assertEquals(mockDiscordCfg.getNewsUrl(), "http://forums.soa-rs.com/news");
	}

	@Test
	public void testSetAndGetToken() {
		MockDiscordCfg mockDiscordCfg = new MockDiscordCfg();

		Assert.assertEquals(mockDiscordCfg.getToken(), null);
		mockDiscordCfg.setToken("DToken2");

		Assert.assertEquals(mockDiscordCfg.getToken(), "DToken2");
	}

	@Test
	public void testSetAndGetEventUrl() {
		MockDiscordCfg mockDiscordCfg = new MockDiscordCfg();

		Assert.assertEquals(mockDiscordCfg.getEventCalendarUrl(), null);
		mockDiscordCfg.setEventCalendarUrl("http://2.google.com");

		Assert.assertEquals(mockDiscordCfg.getEventCalendarUrl(), "http://2.google.com");
	}

	@Test
	public void testSetAndGetNewsUrl() {
		MockDiscordCfg mockDiscordCfg = new MockDiscordCfg();

		Assert.assertEquals(mockDiscordCfg.getNewsUrl(), null);
		mockDiscordCfg.setNewsUrl("http://3.google.com");

		Assert.assertEquals(mockDiscordCfg.getNewsUrl(), "http://3.google.com");
	}

	@Test
	public void testSetAndGetNewsLastPost() {
		MockDiscordCfg mockDiscordCfg = new MockDiscordCfg();

		Assert.assertEquals(mockDiscordCfg.getNewsLastPost(), null);
		Date now = new Date();
		mockDiscordCfg.setNewsLastPost(now);

		Assert.assertEquals(mockDiscordCfg.getNewsLastPost(), now);
	}

	@Test
	public void checkNecessaryConfigurationTest() {
		MockDiscordCfg mockDiscordCfg = new MockDiscordCfg();

		Assert.assertFalse(mockDiscordCfg.checkNecessaryConfiguration());

		mockDiscordCfg.setToken("DToken3");
		Assert.assertTrue(mockDiscordCfg.checkNecessaryConfiguration());
		Assert.assertEquals(mockDiscordCfg.getEventCalendarUrl(), "https://forums.soa-rs.com/calendar/events.xml");
		Assert.assertEquals(mockDiscordCfg.getNewsUrl(), "https://forums.soa-rs.com/rss/1-soa-promos-and-news.xml");
		Assert.assertEquals(mockDiscordCfg.getNewsLastPost(), null);

	}

}
