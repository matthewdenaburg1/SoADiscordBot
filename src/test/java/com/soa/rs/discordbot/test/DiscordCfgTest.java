package com.soa.rs.discordbot.test;

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
	}

	@Test
	public void testLoadFromDiscordConfiguration() {
		MockDiscordCfg mockDiscordCfg = new MockDiscordCfg();

		mockDiscordCfg.loadFromDiscordConfiguration(cfg);

		Assert.assertEquals(cfg.getDiscordToken(), mockDiscordCfg.getToken());
		Assert.assertEquals(cfg.getEventUrl(), mockDiscordCfg.getEventCalendarUrl());

		/* Check to ensure they match the initial values */
		Assert.assertEquals("DToken1", mockDiscordCfg.getToken());
		Assert.assertEquals("http://1.google.com", mockDiscordCfg.getEventCalendarUrl());
	}
	
	@Test
	public void testLoadFromFile()
	{
		MockDiscordCfg mockDiscordCfg = new MockDiscordCfg();
		try {
			mockDiscordCfg.loadFromFile(this.getClass().getResource("/config-dev.xml").getPath());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Assert.assertEquals(mockDiscordCfg.getToken(), "DToken6");
		Assert.assertEquals(mockDiscordCfg.getEventCalendarUrl(), "http://forums.soa-rs.com/");
	}

	@Test
	public void testConfigurationNull() {
		MockDiscordCfg mockDiscordCfg = new MockDiscordCfg();

		Assert.assertEquals(mockDiscordCfg.getToken(), null);
		Assert.assertEquals(mockDiscordCfg.getEventCalendarUrl(), null);
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
	public void checkNecessaryConfigurationTest() {
		MockDiscordCfg mockDiscordCfg = new MockDiscordCfg();

		Assert.assertFalse(mockDiscordCfg.checkNecessaryConfiguration());

		mockDiscordCfg.setToken("DToken3");
		Assert.assertFalse(mockDiscordCfg.checkNecessaryConfiguration());

		mockDiscordCfg.setEventCalendarUrl("http://3.google.com");

		Assert.assertTrue(mockDiscordCfg.checkNecessaryConfiguration());
	}

}
