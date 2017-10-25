package com.soa.rs.discordbot.cfg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.soa.rs.discordbot.jaxb.DiscordConfiguration;
import com.soa.rs.discordbot.jaxb.ObjectFactory;
import com.soa.rs.discordbot.jaxb.TrackedInformation;

/**
 * The ConfigWriter writes the initial configuration provided via the command
 * line to an XML configuration file, for use in quick initial startup of the
 * bot.
 */
public class XmlWriter {

	private ObjectFactory objectFactory = new ObjectFactory();

	/**
	 * Writes out the configuration file.
	 * 
	 * @param cfg
	 *            The created DiscordConfiguration object to be marshalled to XML
	 * @param filename
	 *            The path to where the XML file will be stored
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public void writeConfig(DiscordConfiguration cfg, String filename) throws JAXBException, SAXException {
		File file = new File(filename);
		JAXBContext jaxbContext = JAXBContext.newInstance("com.soa.rs.discordbot.jaxb");

		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(this.getClass().getResource("/xsd/discordConfiguration.xsd"));

		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setSchema(schema);

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.marshal(cfg, file);

	}

	public void writeTrackedConfiguration(TrackedInformation cfg, String filename) throws JAXBException, SAXException {
		File file = new File(filename);
		JAXBContext jaxbContext = JAXBContext.newInstance("com.soa.rs.discordbot.jaxb");

		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(this.getClass().getResource("/xsd/userTracking.xsd"));

		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setSchema(schema);

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		JAXBElement<TrackedInformation> element = objectFactory.createTrackedInformation(cfg);

		jaxbMarshaller.marshal(element, file);

	}

	public InputStream writeTrackedConfigurationToStream(TrackedInformation cfg)
			throws JAXBException, IOException, SAXException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JAXBContext jaxbContext = JAXBContext.newInstance("com.soa.rs.discordbot.jaxb");

		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(this.getClass().getResource("/xsd/userTracking.xsd"));

		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setSchema(schema);

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		JAXBElement<TrackedInformation> element = objectFactory.createTrackedInformation(cfg);

		jaxbMarshaller.marshal(element, baos);

		InputStream bais = new ByteArrayInputStream(baos.toByteArray());
		baos.close();

		return bais;
	}
}
