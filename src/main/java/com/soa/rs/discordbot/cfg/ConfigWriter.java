package com.soa.rs.discordbot.cfg;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.soa.rs.discordbot.jaxb.DiscordConfiguration;

public class ConfigWriter {

	public void writeConfig(DiscordConfiguration cfg, String filename) {
		try {

			File file = new File(filename);
			JAXBContext jaxbContext = JAXBContext.newInstance(DiscordConfiguration.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(cfg, file);

		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}
}
