package com.soa.rs.discordbot.bot.events;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import com.soa.rs.discordbot.util.SoaClientHelper;
import com.soa.rs.discordbot.util.SoaLogging;
import com.soa.rs.triviacreator.jaxb.AnswerBank;
import com.soa.rs.triviacreator.jaxb.Answers;
import com.soa.rs.triviacreator.jaxb.Participant;
import com.soa.rs.triviacreator.jaxb.Questions;
import com.soa.rs.triviacreator.jaxb.TriviaAnswers;
import com.soa.rs.triviacreator.jaxb.TriviaConfiguration;
import com.soa.rs.triviacreator.jaxb.TriviaQuestion;
import com.soa.rs.triviacreator.util.TriviaAnswersStreamWriter;

import sx.blah.discord.api.IDiscordClient;

/**
 * The <tt>SoaTrivia</tt> class contains the runnable trivia thread, along with
 * various configuration parameters needed for executing trivia.
 */
public class SoaTrivia implements Runnable {

	/**
	 * The client the bot is connected to for the Discord API.
	 */
	private IDiscordClient client;

	/**
	 * A constant string to be used for indicating how to answer a question
	 */
	private final String answerFormat = "PM your answers to me, beginning your answer with \".trivia answer\". \nThe question is: ";

	/**
	 * A constant 'times up' string when announcing an answer
	 */
	private final String timesUp = "Time's up!  The answer was: ";

	/**
	 * Boolean detailing whether trivia is currently running or not.
	 */
	private boolean triviaEnabled = false;

	/**
	 * Boolean detailing whether trivia currently is paused or not.
	 */
	private boolean triviaPaused = false;

	/**
	 * The trivia configuration to be used
	 */
	private TriviaConfiguration configuration;

	/**
	 * The Discord ID of the triviamaster who submitted the configuration
	 */
	private long triviaMaster = -1;

	/**
	 * An object representing a Trivia Question; contains a question and its
	 * associated correct answer
	 */
	private TriviaQuestion question;

	/**
	 * The Answers document for holding all submitted answers.
	 */
	private TriviaAnswers answersDoc;

	/**
	 * Basic constructor
	 * 
	 * @param client
	 *            The client representing the Discord API
	 */
	public SoaTrivia(IDiscordClient client) {
		this.client = client;
	}

	/**
	 * Execute trivia. This will start the trivia session and will periodically
	 * check to make sure it still should be running. Upon completion, it will
	 * export the answers to the triviamaster.
	 */
	@Override
	public void run() {
		SoaLogging.getLogger().info("Starting Trivia...");
		initializeAnswersDoc();

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("Its Trivia Time! Welcome to " + configuration.getTriviaName());
			if (configuration.getForumUrl() != null && !configuration.getForumUrl().isEmpty()) {
				sb.append("\nThe forum thread for this event is: " + configuration.getForumUrl());
			}
			messageChannel(sb.toString());
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			this.triviaEnabled = false;
			return;
		}

		Iterator<TriviaQuestion> questions = this.configuration.getQuestionBank().getTriviaQuestion().iterator();
		question = questions.next();
		if (!this.isEnabled())
			return;
		messageChannel("Ready to play? " + answerFormat + question.getQuestion());

		this.answersDoc.getAnswerBank().getTriviaQuestion()
				.add(createQuestionAndAnswer(question.getQuestion(), question.getAnswer()));

		try {
			while (this.triviaEnabled && questions.hasNext()) {
				/*
				 * Configuration stores number of seconds. Loop that number of times, sleeping 1
				 * second each time, and check if paused/stopped after each second passes.
				 */
				for (int i = 0; i < configuration.getWaitTime(); i++) {
					Thread.sleep(1000); // 1 second
					if (!checkStatus())
						return;
				}
				messageChannel(timesUp + question.getAnswer());

				Thread.sleep(3000);
				if (!checkStatus())
					return;
				if (questions.hasNext()) {
					question = questions.next();
					messageChannel("The next question is: " + question.getQuestion());
					this.answersDoc.getAnswerBank().getTriviaQuestion()
							.add(createQuestionAndAnswer(question.getQuestion(), question.getAnswer()));

				}
			}

			// Last question
			for (int i = 0; i < configuration.getWaitTime(); i++) {
				Thread.sleep(1000);
				if (!checkStatus())
					return;
			}
			messageChannel(timesUp + question.getAnswer());

		} catch (InterruptedException e) {
			this.triviaEnabled = false;
			return;
		}
		try {
			exportAnswersToTriviaMaster();
		} catch (IOException e) {
			SoaLogging.getLogger().error("Error exporting answers", e);
		}
		this.triviaEnabled = false;
		SoaLogging.getLogger().info("Trivia has ended as  all questions have been asked.");
	}

	/**
	 * Check if the thread is currently
	 * 
	 * @return true if trivia is still enabled, false if not.
	 * @throws InterruptedException
	 *             if the thread has been stopped while paused. This interrupted
	 *             exception should be caught in the run method and used to stop the
	 *             thread, as it means trivia is no longer enabled.
	 */
	private boolean checkStatus() throws InterruptedException {
		while (isTriviaPaused()) {
			if (!isEnabled())
				return false;
			Thread.sleep(1000);
		}
		if (!isEnabled())
			return false;
		else
			return true;
	}

	/**
	 * Submit a message to the channel trivia is being played in
	 * 
	 * @param content
	 */
	private void messageChannel(String content) {
		SoaClientHelper.sendMsgToChannel(this.client.getChannelByID(Long.parseLong(this.configuration.getChannelId())),
				content);
	}

	/**
	 * Toggle trivia as enabled or disabled
	 * 
	 * @param enable
	 *            Whether trivia should be enabled or disabled
	 */
	public void enableTrivia(boolean enable) {
		this.triviaEnabled = enable;
	}

	/**
	 * Check if trivia is enabled
	 * 
	 * @return true if enabled, false if not
	 */
	public boolean isEnabled() {
		return this.triviaEnabled;
	}

	/**
	 * Check if trivia is paused
	 * 
	 * @return true if paused, false if not
	 */
	public boolean isTriviaPaused() {
		return triviaPaused;
	}

	/**
	 * Set if trivia is paused
	 * 
	 * @param triviaPaused
	 *            Whether trivia should be paused or not. True indicates trivia
	 *            should be paused.
	 */
	public void setTriviaPaused(boolean triviaPaused) {
		this.triviaPaused = triviaPaused;
	}

	/**
	 * Set the trivia configuration to be used.
	 * 
	 * @param configuration
	 *            the configuration to be used
	 */
	public void setConfiguration(TriviaConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Get the trivia configuration
	 * 
	 * @return The trivia configuration
	 */
	public TriviaConfiguration getConfiguration() {
		return this.configuration;
	}

	/**
	 * Get the trivia master
	 * 
	 * @return The trivia master's ID
	 */
	public long getTriviaMaster() {
		return this.triviaMaster;
	}

	/**
	 * Set the trivia master
	 * 
	 * @param triviaMaster
	 *            The trivia master's ID
	 */
	public void setTriviaMaster(long triviaMaster) {
		this.triviaMaster = triviaMaster;
	}

	/*
	 * For use in JUnit testing, this method should remain package private
	 */

	/**
	 * Initialize the answers document
	 */
	void initializeAnswersDoc() {
		this.answersDoc = new TriviaAnswers();
		this.answersDoc.setTriviaName(this.configuration.getTriviaName());
		this.answersDoc.setAnswerBank(new AnswerBank());
	}

	/*
	 * For use in JUnit testing, this method should remain package private
	 */

	/**
	 * Create a question and answer for use in the answers document
	 * 
	 * @param question
	 *            The question text
	 * @param answer
	 *            The answer text
	 * @return A trivia question object for the answers document
	 */
	Questions createQuestionAndAnswer(String question, String answer) {
		Questions newQuestion = new Questions();
		newQuestion.setQuestion(question);
		newQuestion.setCorrectAnswer(answer);
		newQuestion.setAnswers(new Answers());
		return newQuestion;
	}

	/**
	 * Submits an answer to the answers document
	 * 
	 * @param user
	 *            The user submitting the answer
	 * @param answer
	 *            The answer text
	 */
	public void submitAnswer(String user, String answer) {
		Participant participant = new Participant();
		participant.setParticipantName(user);
		participant.setParticipantAnswer(answer);
		int questionSize = this.answersDoc.getAnswerBank().getTriviaQuestion().size();
		this.answersDoc.getAnswerBank().getTriviaQuestion().get(questionSize - 1).getAnswers().getParticipant()
				.add(participant);
	}

	/**
	 * Exports the trivia answers to the triviamaster.
	 * 
	 * @throws IOException
	 *             If there is an error in writing to the stream
	 */
	public void exportAnswersToTriviaMaster() throws IOException {
		TriviaAnswersStreamWriter writer = new TriviaAnswersStreamWriter();
		InputStream dataStream = null;
		try {
			dataStream = writer.writeTriviaAnswersToStream(this.answersDoc);
			String filename = new String(this.configuration.getTriviaName() + ".xml");
			filename = filename.replaceAll(" ", "_");
			SoaClientHelper.sendMsgWithFileToUser(this.triviaMaster, client,
					"Trivia Answers file for Trivia: " + this.configuration.getTriviaName(), dataStream, filename);
		} catch (JAXBException | SAXException | IOException e) {
			SoaClientHelper.sendMessageToUser(this.triviaMaster, client,
					"Trivia Answers were unable to be provided due to an error; this should be reported to the developer.");
			SoaLogging.getLogger().error("Error sending Trivia Answers to the user: " + e.getMessage(), e);
		} finally {
			dataStream.close();
		}

	}

	/**
	 * Cleanup trivia; nulls out necessary values to prepare for another trivia
	 * session.
	 */
	public void cleanupTrivia() {
		this.triviaMaster = -1;
		this.configuration = null;
		this.question = null;
		this.answersDoc = null;
		this.triviaEnabled = false;
		this.triviaPaused = false;
	}

	/*
	 * The following two set & get methods are meant for access within JUnit
	 * testing; package private
	 */

	/**
	 * Get the answer doc
	 * 
	 * @return The answer doc
	 */
	TriviaAnswers getAnswersDoc() {
		return answersDoc;
	}

	/**
	 * Set the answer doc
	 * 
	 * @param answersDoc
	 *            The answer doc
	 */
	void setAnswersDoc(TriviaAnswers answersDoc) {
		this.answersDoc = answersDoc;
	}

}
