package com.soa.rs.discordbot.cfg;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.soa.rs.discordbot.jaxb.DiscordConfiguration;

/**
 * The ConfigWriter writes the initial configuration provided via the command
 * line to an XML configuration file, for use in quick initial startup of the
 * bot.
 */
public class ConfigWriter {

	/**
	 * Writes out the configuration file.
	 * 
	 * @param cfg
	 *            The created DiscordConfiguration object to be marshalled to
	 *            XML
	 * @param filename
	 *            The path to where the XML file will be stored
	 * @throws JAXBException
	 */
	public void writeConfig(DiscordConfiguration cfg, String filename) throws JAXBException {
		File file = new File(filename);
		JAXBContext jaxbContext = JAXBContext.newInstance(DiscordConfiguration.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.marshal(cfg, file);

	}
}
