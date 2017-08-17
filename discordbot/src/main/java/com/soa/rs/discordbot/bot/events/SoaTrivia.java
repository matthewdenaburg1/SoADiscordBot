package com.soa.rs.discordbot.bot.events;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.bind.JAXBException;

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

public class SoaTrivia implements Runnable {

	private IDiscordClient client;
	private final String answerFormat = "PM your answers to me, beginning your answer with \".trivia answer\". \nThe question is: ";
	private final String timesUp = "Time's up!  The answer was: ";
	private boolean triviaEnabled = false;
	private TriviaConfiguration configuration;
	private long triviaMaster = -1;
	private TriviaQuestion question;
	private TriviaAnswers answersDoc;

	public SoaTrivia(IDiscordClient client) {
		this.client = client;
	}

	@Override
	public void run() {
		SoaLogging.getLogger().info("Starting Trivia...");
		initializeAnswersDoc();

		try {
			messageShoutbox("Its Trivia Time! Welcome to " + configuration.getTriviaName()
					+ " \nThe forum thread for this event is: " + configuration.getForumUrl());
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}

		Iterator<TriviaQuestion> questions = this.configuration.getQuestionBank().getTriviaQuestion().iterator();
		question = questions.next();
		messageShoutbox("Ready to play? " + answerFormat + question.getQuestion());

		this.answersDoc.getAnswerBank().getTriviaQuestion()
				.add(createQuestionAndAnswer(question.getQuestion(), question.getAnswer()));

		try {
			while (this.triviaEnabled && questions.hasNext()) {
				Thread.sleep((1000 * configuration.getWaitTime())); // time in seconds
				messageShoutbox(timesUp + question.getAnswer());

				Thread.sleep(3000);
				if (questions.hasNext()) {
					question = questions.next();
					messageShoutbox("The next question is: " + question.getQuestion());
					this.answersDoc.getAnswerBank().getTriviaQuestion()
							.add(createQuestionAndAnswer(question.getQuestion(), question.getAnswer()));

				}
			}

			// Last question
			Thread.sleep((1000 * configuration.getWaitTime())); // time in seconds
			messageShoutbox(timesUp + question.getAnswer());

		} catch (InterruptedException e) {
			// Place killswitch here
			this.triviaEnabled = false;
		}
		try {
			exportAnswersToTriviaMaster();
		} catch (IOException e) {
			SoaLogging.getLogger().error("Error exporting answers", e);
		}
		this.triviaEnabled = false;
		// wrap-up things here
		// shouldn't be needed any longer
	}

	private void messageShoutbox(String content) {
		SoaClientHelper.sendMsgToChannel(this.client.getChannelByID(Long.parseLong(this.configuration.getChannelId())),
				content);
	}

	public void enableTrivia(boolean enable) {
		this.triviaEnabled = enable;
	}

	public boolean isEnabled() {
		return this.triviaEnabled;
	}

	public void setConfiguration(TriviaConfiguration configuration) {
		this.configuration = configuration;
	}

	public TriviaConfiguration getConfiguration() {
		return this.configuration;
	}

	public long getTriviaMaster() {
		return this.triviaMaster;
	}

	public void setTriviaMaster(long triviaMaster) {
		this.triviaMaster = triviaMaster;
	}

	/*
	 * For use in JUnit testing, this method should remain package private
	 */
	void initializeAnswersDoc() {
		this.answersDoc = new TriviaAnswers();
		this.answersDoc.setTriviaName(this.configuration.getTriviaName());
		this.answersDoc.setAnswerBank(new AnswerBank());
	}

	/*
	 * For use in JUnit testing, this method should remain package private
	 */

	Questions createQuestionAndAnswer(String question, String answer) {
		Questions newQuestion = new Questions();
		newQuestion.setQuestion(question);
		newQuestion.setCorrectAnswer(answer);
		newQuestion.setAnswers(new Answers());
		return newQuestion;
	}

	public void submitAnswer(String user, String answer) {
		Participant participant = new Participant();
		participant.setParticipantName(user);
		participant.setParticipantAnswer(answer);
		int questionSize = this.answersDoc.getAnswerBank().getTriviaQuestion().size();
		this.answersDoc.getAnswerBank().getTriviaQuestion().get(questionSize - 1).getAnswers().getParticipant()
				.add(participant);
	}

	public void exportAnswersToTriviaMaster() throws IOException {
		TriviaAnswersStreamWriter writer = new TriviaAnswersStreamWriter();
		InputStream dataStream = null;
		try {
			dataStream = writer.writeTriviaAnswersToStream(this.answersDoc);
			String filename = new String(this.configuration.getTriviaName() + ".xml");
			filename = filename.replaceAll(" ", "_");
			SoaClientHelper.sendMsgWithFileToUser(this.triviaMaster, client,
					"Trivia Answers file for Trivia: " + this.configuration.getTriviaName(), dataStream, filename);
		} catch (JAXBException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dataStream.close();
		}

	}

	public void cleanupTrivia() {
		this.triviaMaster = -1;
		this.configuration = null;
		this.question = null;
		this.answersDoc = null;
		this.triviaEnabled = false;
	}

	/*
	 * The following two set & get methods are meant for access within JUnit
	 * testing; package private
	 */

	TriviaAnswers getAnswersDoc() {
		return answersDoc;
	}

	void setAnswersDoc(TriviaAnswers answersDoc) {
		this.answersDoc = answersDoc;
	}

}
