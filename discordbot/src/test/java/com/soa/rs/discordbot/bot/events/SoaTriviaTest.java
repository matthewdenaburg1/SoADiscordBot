package com.soa.rs.discordbot.bot.events;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import com.soa.rs.triviacreator.jaxb.QuestionBank;
import com.soa.rs.triviacreator.jaxb.TriviaAnswers;
import com.soa.rs.triviacreator.jaxb.TriviaConfiguration;
import com.soa.rs.triviacreator.jaxb.TriviaQuestion;

import sx.blah.discord.api.IDiscordClient;

public class SoaTriviaTest {

	private IDiscordClient client;
	private SoaTrivia trivia;

	@Before
	public void createTrivia() {
		this.trivia = new SoaTrivia(client);

		TriviaConfiguration configuration = new TriviaConfiguration();
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
		
		//Check to make sure the question and the answer match
		Assert.assertEquals(this.trivia.getAnswersDoc().getAnswerBank().getTriviaQuestion().get(0).getQuestion(), "What is question 1?");
		Assert.assertEquals(this.trivia.getAnswersDoc().getAnswerBank().getTriviaQuestion().get(0).getCorrectAnswer(), "This is question 1");

		//Check to make sure the submitted answers match
		Assert.assertEquals(this.trivia.getAnswersDoc().getAnswerBank().getTriviaQuestion().get(0).getAnswers()
				.getParticipant().get(0).getParticipantAnswer(), "Answer 1 to Question 1");
		Assert.assertEquals(this.trivia.getAnswersDoc().getAnswerBank().getTriviaQuestion().get(0).getAnswers()
				.getParticipant().get(1).getParticipantAnswer(), "Answer 2 to Question 1");

	}

}
