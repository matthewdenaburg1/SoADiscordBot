package com.soa.rs.discordbot.cfg;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.soa.rs.discordbot.jaxb.DiscordConfiguration;

/**
 * The ConfigReader reads in a XML configuration file and marshalls it into a
 * <tt>DiscordConfiguration</tt> object, for use in initial startup of the bot.
 */
public class ConfigReader {

	/**
	 * Load in the configuration from the provided filename
	 * 
	 * @param filename
	 *            path to the XML file
	 * @return configuration
	 * @throws JAXBException
	 */
	public DiscordConfiguration loadAppConfig(String filename) throws JAXBException {
		DiscordConfiguration config = null;

		File file = new File(filename);
		JAXBContext jaxbContext = JAXBContext.newInstance(DiscordConfiguration.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		config = (DiscordConfiguration) jaxbUnmarshaller.unmarshal(file);

		return config;
	}
}
