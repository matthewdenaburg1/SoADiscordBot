package com.soa.rs.triviacreator.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.soa.rs.triviacreator.jaxb.ObjectFactory;
import com.soa.rs.triviacreator.jaxb.TriviaAnswers;

/**
 * The TriviaAnswersStreamWriter writes the Trivia Answers to a stream that can
 * then be sent to the Discord server
 */
public class TriviaAnswersStreamWriter {

	private final ObjectFactory objectFactory = new ObjectFactory();

	/**
	 * Writes out the Trivia Answers to an {@link InputStream} that can be sent to
	 * the Discord server.
	 * 
	 * @param answers
	 *            The created TriviaAnswers object to be marshalled to XML
	 * 
	 * @return An {@link InputStream} containing the data.
	 * 
	 * @throws JAXBException
	 *             If the data cannot be marshalled to due to an error
	 * @throws IOException
	 *             If there is an issue with writing to or reading from the data
	 *             streams
	 */
	public InputStream writeTriviaAnswersToStream(TriviaAnswers answers) throws JAXBException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JAXBContext jaxbContext = JAXBContext.newInstance("com.soa.rs.triviacreator.jaxb");
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		JAXBElement<TriviaAnswers> element = objectFactory.createTriviaAnswers(answers);

		jaxbMarshaller.marshal(element, baos);

		InputStream bais = new ByteArrayInputStream(baos.toByteArray());
		baos.close();

		return bais;
	}

}
