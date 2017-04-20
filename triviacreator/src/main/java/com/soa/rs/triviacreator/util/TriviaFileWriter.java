package com.soa.rs.triviacreator.util;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.soa.rs.triviacreator.jaxb.TriviaConfiguration;

/**
 * The TriviaFileWriter writes out a Trivia configuration XML file that is
 * configured by the user using the TriviaCreator application.
 */
public class TriviaFileWriter {

	/**
	 * Writes out the configuration file.
	 * 
	 * @param cfg
	 *            The created TriviaConfiguration object to be marshalled to XML
	 * @param filename
	 *            The path to where the XML file will be stored
	 * @throws JAXBException
	 *             If they file cannot be written due to an error
	 */
	public void writeTriviaConfigFile(TriviaConfiguration cfg, String filename) throws JAXBException {
		File file = new File(filename);
		JAXBContext jaxbContext = JAXBContext.newInstance(TriviaConfiguration.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.marshal(cfg, file);

	}
}
