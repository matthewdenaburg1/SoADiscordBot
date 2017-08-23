package com.soa.rs.discordbot.bot.events;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import com.soa.rs.triviacreator.jaxb.QuestionBank;
import com.soa.rs.triviacreator.jaxb.TriviaAnswers;
import com.soa.rs.triviacreator.jaxb.TriviaConfiguration;
import com.soa.rs.triviacreator.jaxb.TriviaQuestion;
import com.soa.rs.triviacreator.util.InvalidConfigurationException;

import sx.blah.discord.api.IDiscordClient;

public class SoaTriviaTest {

	private IDiscordClient client;
	private SoaTrivia trivia;
	private TriviaConfiguration configuration;
	private SoaTriviaManager manager = new SoaTriviaManager();

	@Before
	public void createTrivia() {
		this.trivia = new SoaTrivia(client);

		configuration = new TriviaConfiguration();
		configuration.setTriviaName("Test trivia name");
		configuration.setServerId("252267969617461248");
		configuration.setChannelId("252267969617461248");
		configuration.setWaitTime(5);
		configuration.setForumUrl("https://forums.soa-rs.com");
		QuestionBank bank = new QuestionBank();
		TriviaQuestion question = new TriviaQuestion();
		question.setQuestion("What is question 1?");
		question.setAnswer("This is question 1");
		bank.getTriviaQuestion().add(question);
		question = new TriviaQuestion();
		question.setQuestion("What is question 2?");
		question.setAnswer("This is question 2");
		bank.getTriviaQuestion().add(question);
		configuration.setQuestionBank(bank);

		this.trivia.setConfiguration(configuration);
	}

	@Test
	public void submitSingleQuestionAnswerTest() {
		this.trivia.initializeAnswersDoc();
		TriviaAnswers answers = this.trivia.getAnswersDoc();

		answers.getAnswerBank().getTriviaQuestion()
				.add(this.trivia.createQuestionAndAnswer(
						this.trivia.getConfiguration().getQuestionBank().getTriviaQuestion().get(0).getQuestion(),
						this.trivia.getConfiguration().getQuestionBank().getTriviaQuestion().get(0).getAnswer()));
		this.trivia.setAnswersDoc(answers);

		this.trivia.submitAnswer("Jeff", "Answer 1 to Question 1");
		this.trivia.submitAnswer("Josh", "Answer 2 to Question 1");

		// Check to make sure the question and the answer match
		Assert.assertEquals(this.trivia.getAnswersDoc().getAnswerBank().getTriviaQuestion().get(0).getQuestion(),
				"What is question 1?");
		Assert.assertEquals(this.trivia.getAnswersDoc().getAnswerBank().getTriviaQuestion().get(0).getCorrectAnswer(),
				"This is question 1");

		// Check to make sure the submitted answers match
		Assert.assertEquals(this.trivia.getAnswersDoc().getAnswerBank().getTriviaQuestion().get(0).getAnswers()
				.getParticipant().get(0).getParticipantAnswer(), "Answer 1 to Question 1");
		Assert.assertEquals(this.trivia.getAnswersDoc().getAnswerBank().getTriviaQuestion().get(0).getAnswers()
				.getParticipant().get(1).getParticipantAnswer(), "Answer 2 to Question 1");

	}

	@Test
	public void checkValidConfiguration() {
		boolean valid = true;
		try {

			manager.validateConfiguration(configuration);
		} catch (InvalidConfigurationException e) {
			valid = false;
		}
		Assert.assertEquals(valid, true);
	}

	@Test(expected = InvalidConfigurationException.class)
	public void testNullTriviaName() throws InvalidConfigurationException {
		String nullString = null;
		configuration.setTriviaName(nullString);
		manager.validateConfiguration(configuration);
	}

	@Test(expected = InvalidConfigurationException.class)
	public void testEmptyTriviaName() throws InvalidConfigurationException {
		configuration.setTriviaName("");
		manager.validateConfiguration(configuration);
	}

	@Test(expected = InvalidConfigurationException.class)
	public void testNullServerId() throws InvalidConfigurationException {
		String nullString = null;
		configuration.setServerId(nullString);
		manager.validateConfiguration(configuration);
	}

	@Test(expected = InvalidConfigurationException.class)
	public void testEmptyServerId() throws InvalidConfigurationException {
		configuration.setServerId("");
		manager.validateConfiguration(configuration);
	}

	@Test(expected = InvalidConfigurationException.class)
	public void testInvalidServerId() throws InvalidConfigurationException {
		configuration.setServerId("wat");
		manager.validateConfiguration(configuration);
	}

	@Test(expected = InvalidConfigurationException.class)
	public void testNullChannelId() throws InvalidConfigurationException {
		String nullString = null;
		configuration.setChannelId(nullString);
		manager.validateConfiguration(configuration);
	}

	@Test(expected = InvalidConfigurationException.class)
	public void testEmptyChannelId() throws InvalidConfigurationException {
		configuration.setChannelId("");
		manager.validateConfiguration(configuration);
	}

	@Test(expected = InvalidConfigurationException.class)
	public void testInvalidChannelId() throws InvalidConfigurationException {
		configuration.setChannelId("wat");
		manager.validateConfiguration(configuration);
	}

	@Test(expected = InvalidConfigurationException.class)
	public void testNegativeWaitTime() throws InvalidConfigurationException {
		configuration.setWaitTime(-1);
		manager.validateConfiguration(configuration);
	}

	@Test(expected = InvalidConfigurationException.class)
	public void testZeroWaitTime() throws InvalidConfigurationException {
		configuration.setWaitTime(0);
		manager.validateConfiguration(configuration);
	}

	@Test(expected = InvalidConfigurationException.class)
	public void testNullQuestion() throws InvalidConfigurationException {
		String nullString = null;
		configuration.getQuestionBank().getTriviaQuestion().get(0).setQuestion(nullString);
		manager.validateConfiguration(configuration);
	}

	@Test(expected = InvalidConfigurationException.class)
	public void testEmptyQuestion() throws InvalidConfigurationException {
		configuration.getQuestionBank().getTriviaQuestion().get(0).setQuestion("");
		manager.validateConfiguration(configuration);
	}

	@Test(expected = InvalidConfigurationException.class)
	public void testNullAnswer() throws InvalidConfigurationException {
		String nullString = null;
		configuration.getQuestionBank().getTriviaQuestion().get(0).setAnswer(nullString);
		manager.validateConfiguration(configuration);
	}

	@Test(expected = InvalidConfigurationException.class)
	public void testEmptyAnswer() throws InvalidConfigurationException {
		configuration.getQuestionBank().getTriviaQuestion().get(0).setAnswer("");
		manager.validateConfiguration(configuration);
	}
}
