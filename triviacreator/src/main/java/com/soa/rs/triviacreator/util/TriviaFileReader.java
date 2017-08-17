package com.soa.rs.triviacreator.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
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
		JAXBContext jaxbContext = JAXBContext.newInstance("com.soa.rs.triviacreator.jaxb");

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		JAXBElement<?> jaxbElement = (JAXBElement<?>) jaxbUnmarshaller.unmarshal(file);
		config = (TriviaConfiguration) jaxbElement.getValue();

		return config;
	}

	public TriviaConfiguration loadTriviaConfigFromURL(URL url) throws JAXBException, IOException {
		TriviaConfiguration config = null;
		JAXBContext jaxbContext = JAXBContext.newInstance("com.soa.rs.triviacreator.jaxb");

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
		connection.setReadTimeout(10 * 1000);
		connection.connect();

		InputStream stream = connection.getInputStream();
		JAXBElement<?> jaxbElement = (JAXBElement<?>) jaxbUnmarshaller.unmarshal(stream);

		config = (TriviaConfiguration) jaxbElement.getValue();

		stream.close();

		return config;
	}
}
