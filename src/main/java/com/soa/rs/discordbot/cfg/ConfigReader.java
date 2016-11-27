package com.soa.rs.discordbot.cfg;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.soa.rs.discordbot.jaxb.DiscordConfiguration;

public class ConfigReader {
	public DiscordConfiguration loadAppConfig(String filename) {
		DiscordConfiguration config = null;
		try {
			File file = new File(filename);
			JAXBContext jaxbContext = JAXBContext.newInstance(DiscordConfiguration.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			config = (DiscordConfiguration) jaxbUnmarshaller.unmarshal(file);

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return config;
	}
}
