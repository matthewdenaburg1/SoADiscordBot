package com.soa.rs.triviacreator.util;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.soa.rs.triviacreator.jaxb.TriviaConfiguration;

/**
 * The TriviaFileReader reads in a XML configuration file and marshalls it into
 * a <tt>TriviaConfiguration</tt> object. This is then used either to load and
 * edit an existing configuration for the TriviaCreator application, or by the
 * Soa Discord Bot to initialize a trivia session.
 */

public class TriviaFileReader {

	/**
	 * Load in the configuration from the provided filename
	 * 
	 * @param filename
	 *            path to the XML file
	 * @return The Trivia configuration to be used
	 * @throws JAXBException
	 *             If the file cannot be successfully loaded due to an error.
	 */
	public TriviaConfiguration loadTriviaConfigFile(String filename) throws JAXBException {
		TriviaConfiguration config = null;

		File file = new File(filename);
		JAXBContext jaxbContext = JAXBContext.newInstance(TriviaConfiguration.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		config = (TriviaConfiguration) jaxbUnmarshaller.unmarshal(file);

		return config;
	}
}
