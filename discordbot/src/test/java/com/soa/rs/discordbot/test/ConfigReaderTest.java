package com.soa.rs.discordbot.test;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.soa.rs.discordbot.cfg.ConfigReader;
import com.soa.rs.discordbot.jaxb.DiscordConfiguration;

public class ConfigReaderTest {

	@Test
	public void testConfigReader() {
		ConfigReader reader = new ConfigReader();
		DiscordConfiguration cfg = null;
		try {
			cfg = reader.loadAppConfig(this.getClass().getResource("/config-test.xml").getPath());
		} catch (JAXBException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Assert.assertEquals(cfg.getDiscordToken(), "DToken6");
		Assert.assertEquals(cfg.getEventUrl(), "http://forums.soa-rs.com/");
		Assert.assertEquals(cfg.getNewsUrl(), "http://forums.soa-rs.com/news");
	}

}
