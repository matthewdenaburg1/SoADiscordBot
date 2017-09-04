package com.soa.rs.discordbot.bot.events;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.soa.rs.discordbot.util.NoSuchServerException;
import com.soa.rs.discordbot.util.SoaClientHelper;
import com.soa.rs.discordbot.util.SoaLogging;
import com.soa.rs.triviacreator.jaxb.TriviaConfiguration;
import com.soa.rs.triviacreator.jaxb.TriviaQuestion;
import com.soa.rs.triviacreator.util.InvalidConfigurationException;
import com.soa.rs.triviacreator.util.TriviaFileReader;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.PermissionUtils;

/**
 * The <tt>SoaTriviaManager</tt> class handles the management of the trivia
 * system through the commands issued by the user from Discord. It is
 * responsible for the loading of the Trivia configuration file, starting and
 * stopping trivia, recording answers from the participants, and exporting the
 * answers in XML format back to the trivia master.
 * <p>
 * The actual execution of the Trivia thread is not handled here. The
 * <tt>SoaTrivia</tt> class is responsible for that.
 */
public class SoaTriviaManager {

	/**
	 * The object representing the trivia session.
	 */
	private SoaTrivia trivia = null;

	/**
	 * Used to hold the most recently received message.
	 */
	private IMessage msg;

	/**
	 * The thread executing the trivia session. Note that <tt>SoaTrivia</tt> is the
	 * class implementing the <tt>Runnable</tt> interface.
	 */
	private Thread triviaThread;

	/**
	 * The thread managing trivia cleanup after a trivia session has ended. Trivia
	 * cleanup must be delayed to ensure answers are appropriately exported off and
	 * retrieved before they are lost.
	 */
	private Thread cleanupThread;

	/**
	 * Set the message received for use by the manager
	 * 
	 * @param msg
	 *            Message received by the bot from the user.
	 */
	public void setMsg(IMessage msg) {
		this.msg = msg;
	}

	/**
	 * Handles the processing of commands from the user. The following commands are
	 * valid:
	 * <ul>
	 * <li>Help: Will display a help menu with the available trivia commands.</li>
	 * <li>Config: Should be accompanied with an uploaded XML file containing the
	 * trivia configuration. Will validate and set up the trivia instance with a
	 * valid configuration to be used.</li>
	 * <li>Start: Starts the trivia thread</li>
	 * <li>Stop: Stops the trivia thread</li>
	 * <li>Export: Will export the current Trivia answers to the trivia master.</li>
	 * <li>Reset: Will stop the currently running trivia instance if one is running,
	 * and will immediately reset the system so another trivia session can be
	 * started.</li>
	 * <li>Answer: Will record an answer provided by the user if trivia is currently
	 * running.
	 * 
	 * @param args
	 *            The arguments provided with the message, minus the word "trivia"
	 */
	public void executeCmd(String[] args) {
		if (args[1].equalsIgnoreCase("help")) {
			triviaHelp();
		}
		if (msg.getChannel().isPrivate()) {

			if (args[1].equalsIgnoreCase("config")) {
				loadTriviaConfiguration();
			} else if (args[1].equalsIgnoreCase("start")) {
				startTrivia();
			} else if (args[1].equalsIgnoreCase("stop")) {
				stopTrivia();
			} else if (args[1].equalsIgnoreCase("export")) {
				exportAnswers();
			} else if (args[1].equalsIgnoreCase("reset")) {
				resetTriviaSystem();
			} else if (args[1].equalsIgnoreCase("answer")) {
				recordAnswer(msg.getContent());
			} else if (args[1].equalsIgnoreCase("pause")) {
				pauseTrivia();
			} else if (args[1].equalsIgnoreCase("resume")) {
				resumeTrivia();
			}
		}

		else {

			/*
			 * While trivia commands should be sent via PM, these two will be checked in the
			 * main channel as they are either revealing the questions and their answers, or
			 * the answers submitted by others. The bot will read the answer or
			 * configuration, and then will attempt to delete the message and will provide a
			 * message to submit commands via PM.
			 */
			if (args[1].equalsIgnoreCase("answer")) {
				recordAnswer(msg.getContent());
			} else if (args[1].equalsIgnoreCase("config")) {
				loadTriviaConfiguration();
			} else if (args[1].equalsIgnoreCase("pause")) {
				pauseTrivia();
			} else if (args[1].equalsIgnoreCase("resume")) {
				resumeTrivia();
			} else
				SoaClientHelper.sendMsgToChannel(msg.getChannel(), "Trivia Commands should be sent via Private Chat!");
		}

	}

	/**
	 * Prints out the command listing for the Trivia module.
	 */
	private void triviaHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append("```Help: Trivia (command: .trivia [args])\n");
		sb.append("Note - Trivia commands should be sent privately through private chat to the bot\n\n");
		sb.append(".trivia help - Bot displays this menu.");
		sb.append(".trivia answer - Submits an answer to the currently asked Trivia Question.\n");
		sb.append(
				".trivia config - Should be used in a file upload, uploads a configuration file to configure trivia.  The uploader will be assigned to be the \"Trivia Master\" if successful.\n");
		sb.append(
				"\nNote - The rest of the commands in this menu can only be executed by the current Trivia Master or a member of staff\n");
		sb.append(".trivia start - Starts a round of trivia if one has been successfully configured\n");
		sb.append(".trivia stop - Immediately ends the currently running instance of trivia\n");
		sb.append(
				".trivia pause & .trivia resume - Pauses or resumes trivia.  Answers can still be collected while paused.\n");
		sb.append(
				".trivia export - Exports the current set of collected answers as an XML document.  This document will also be exported automatically when trivia finishes.\n");
		sb.append(
				".trivia reset - This command immediately resets the trivia instance so that a new round can be uploaded and run.  This includes bypassing the 15 minute cooldown period between rounds.");
		sb.append("```");
		SoaClientHelper.sendMsgToChannel(msg.getChannel(), sb.toString());

	}

	/**
	 * Loads the provided configuration. If the configuration is not provided or is
	 * invalid, an error will be returned. If the configuration is inadvertantly
	 * placed in a public channel, the bot will try and delete it from that channel
	 * after it has read it.
	 */
	private void loadTriviaConfiguration() {
		if (!msg.getAttachments().isEmpty()) {
			try {
				String uploadedFileUrl = msg.getAttachments().get(0).getUrl();
				URL url = new URL(uploadedFileUrl);
				TriviaFileReader reader = new TriviaFileReader();

				TriviaConfiguration configuration = reader.loadTriviaConfigFromURL(url);
				validateConfiguration(configuration);
				if (this.trivia == null) {
					this.trivia = new SoaTrivia(msg.getClient());
				}
				if (!this.trivia.isEnabled() && this.trivia.getTriviaMaster() == -1) {
					if (checkIfServerExists(configuration, msg.getClient())) {
						this.trivia.setConfiguration(configuration);
						this.trivia.setTriviaMaster(msg.getAuthor().getLongID());
						SoaClientHelper.sendMsgToChannel(msg.getChannel(),
								"Trivia File loaded & you are the Trivia Master.  Run ``.trivia start`` to begin.");
					}
				} else {
					SoaClientHelper.sendMsgToChannel(msg.getChannel(),
							"Trivia is either in progress or it has not been 15 minutes since Trivia last ended.  Try again later");
				}

			} catch (JAXBException | IOException e) {
				SoaLogging.getLogger().error("Error loading trivia configuration file", e);
				SoaClientHelper.sendMsgToChannel(msg.getChannel(),
						"An error was encountered when loading the provided file: " + e.getMessage());
			} catch (NoSuchServerException e) {
				SoaLogging.getLogger().error("Error executing configuration: ", e);
				SoaClientHelper.sendMsgToChannel(msg.getChannel(),
						"An error occurred initializing trivia: " + e.getMessage());
			} catch (InvalidConfigurationException e) {
				SoaLogging.getLogger().error("Configuration provided was not valid", e);
				SoaClientHelper.sendMsgToChannel(msg.getChannel(),
						"The configuration provided could not be validated: " + e.getMessage());
			}

			if (!msg.getChannel().isPrivate()) {
				if (PermissionUtils.hasPermissions(msg.getChannel(), msg.getClient().getOurUser(),
						Permissions.MANAGE_MESSAGES)) {
					SoaClientHelper.deleteMessageFromChannel(msg);
					SoaClientHelper.sendMsgToChannel(msg.getAuthor().getOrCreatePMChannel(), msg.getAuthor()
							.getDisplayName(msg.getGuild())
							+ ", I got your configuration but please send those commands via PM in future so others don't see & cheat!  I deleted the config from the channel it was in.");
				} else {
					SoaClientHelper.sendMsgToChannel(msg.getChannel(), msg.getAuthor().getDisplayName(msg.getGuild())
							+ ", I got your configuration but please send those commands via PM in future so others don't see & cheat!  I can't delete your message, so please delete it from the channel so others don't see!");
				}
			}
		}
	}

	/**
	 * Validate that the configuration provided has all fields appropriately
	 * validated so that Trivia can run successfully.
	 * 
	 * @param configuration
	 *            The Trivia Configuration uploaded to the Discord bot.
	 * @throws InvalidConfigurationException
	 *             If the configuration is for some reason not valid.
	 */
	void validateConfiguration(TriviaConfiguration configuration) throws InvalidConfigurationException {
		if (configuration.getTriviaName() == null || configuration.getTriviaName().isEmpty()) {
			throw new InvalidConfigurationException(
					"The server name field is required, and is empty in the provided configuration");
		}
		if (configuration.getServerId() == null || configuration.getServerId().isEmpty()) {
			throw new InvalidConfigurationException(
					"The server id field is required, and is empty in the provided configuration");
		}
		try {
			Long.parseLong(configuration.getServerId());
		} catch (NumberFormatException ex) {
			throw new InvalidConfigurationException(
					"The server id field is not valid (should be a long, but could not be parsed as a long)");
		}
		if (configuration.getChannelId() == null || configuration.getChannelId().isEmpty()) {
			throw new InvalidConfigurationException(
					"The channel id field is required, and is empty in the provided configuration");
		}
		try {
			Long.parseLong(configuration.getChannelId());
		} catch (NumberFormatException ex) {
			throw new InvalidConfigurationException(
					"The channel id field is not valid (should be a long, but could not be parsed as a long)");
		}
		if (configuration.getWaitTime() <= 0) {
			throw new InvalidConfigurationException("The wait time cannot be less than 1");
		}
		for (TriviaQuestion question : configuration.getQuestionBank().getTriviaQuestion()) {
			if (question.getQuestion() == null || question.getQuestion().isEmpty()) {
				throw new InvalidConfigurationException(
						"One of the Trivia Questions provided has no text in the question field.");
			}
			if (question.getAnswer() == null || question.getAnswer().isEmpty()) {
				throw new InvalidConfigurationException(
						"One of the Trivia Questions provided has no text in the answer field.");
			}
		}

	}

	/**
	 * Checks if the server is one that the bot is currently connected to.
	 * 
	 * @param configuration
	 *            The Trivia Configuration uploaded to the Discord bot.
	 * @param client
	 *            The Discord Client object for the bot.
	 * @return true if the bot is connected to the provided server
	 * @throws NoSuchServerException
	 *             if the bot is not connected to the provided server
	 */
	private boolean checkIfServerExists(TriviaConfiguration configuration, IDiscordClient client)
			throws NoSuchServerException {
		List<IGuild> guilds = client.getGuilds();
		Iterator<IGuild> iter = guilds.iterator();
		while (iter.hasNext()) {
			IGuild checkGuild = iter.next();
			if (checkGuild.getLongID() == Long.parseLong(configuration.getServerId())) {
				return true;
			}
		}
		throw new NoSuchServerException("The bot is not a member of the specified server.");
	}

	/**
	 * Starts the trivia thread
	 */
	private void startTrivia() {
		if (this.trivia != null && this.trivia.getConfiguration() != null) {
			if (this.trivia.isEnabled()) {
				SoaClientHelper.sendMsgToChannel(this.msg.getChannel(), "Trivia is already running!");
				return;
			} else if (this.cleanupThread != null && this.cleanupThread.isAlive()) {
				SoaClientHelper.sendMsgToChannel(this.msg.getChannel(),
						"Trivia was just run and is in cooldown; another round cannot yet be started");
				return;
			}
			if (isTriviaMaster(this.msg)) {
				this.triviaThread = new Thread(this.trivia);
				this.trivia.enableTrivia(true);
				this.triviaThread.run();
				cleanupTask();
			} else {
				SoaClientHelper.sendMsgToChannel(this.msg.getChannel(),
						"Only the Trivia Master can start a trivia round.");
			}
		} else {
			SoaClientHelper.sendMsgToChannel(this.msg.getChannel(),
					"No Trivia Configuration has been provided; use ``.trivia config`` and upload a configuration file.");
		}
	}

	/**
	 * Stops the trivia thread
	 */
	private void stopTrivia() {
		if (this.trivia != null) {
			if (isTriviaMaster(this.msg) && this.trivia.isEnabled()) {
				this.trivia.enableTrivia(false);
				this.triviaThread.interrupt();
				SoaClientHelper.sendMsgToChannel(this.msg.getChannel(), "Trivia has been stopped.");
			} else {
				SoaClientHelper.sendMsgToChannel(this.msg.getChannel(),
						"Only the Trivia Master can stop a trivia round.");
			}
		}

	}

	/**
	 * Records an answer submitted by a participant
	 * 
	 * @param answer
	 *            The answer submitted by the participant
	 */
	private void recordAnswer(String answer) {
		if (this.trivia != null) {
			if (this.trivia.isEnabled()) {
				String displayName = msg.getAuthor().getDisplayName(
						msg.getClient().getGuildByID(Long.parseLong(this.trivia.getConfiguration().getServerId())));
				answer = answer.replace(".trivia answer ", "");
				if (answer.trim().length() == 0) {
					SoaClientHelper.sendMsgToChannel(msg.getChannel(),
							"The answer provided was empty; no answer recorded");
					return;
				}
				this.trivia.submitAnswer(displayName, answer);
				if (!msg.getChannel().isPrivate()) {
					if (PermissionUtils.hasPermissions(msg.getChannel(), msg.getClient().getOurUser(),
							Permissions.MANAGE_MESSAGES)) {
						SoaClientHelper.deleteMessageFromChannel(msg);
						SoaClientHelper.sendMsgToChannel(msg.getChannel(), msg.getAuthor()
								.getDisplayName(msg.getGuild())
								+ ", I got your answer but please PM future answers so others don't see!  I deleted the answer from here");
					} else {
						SoaClientHelper.sendMsgToChannel(msg.getChannel(), msg.getAuthor()
								.getDisplayName(msg.getGuild())
								+ ", I got your answer but please PM future answers so others don't see!  I can't delete your message, so please delete it so others don't see!");
					}
				} else {
					SoaClientHelper.sendMsgToChannel(msg.getChannel(),
							"Message recorded, " + msg.getAuthor().getDisplayName(this.msg.getClient()
									.getGuildByID(Long.parseLong(this.trivia.getConfiguration().getServerId()))));
				}
			}
		}
	}

	/**
	 * Exports the answers in an XML document to the triviamaster.
	 */
	private void exportAnswers() {
		if (this.trivia != null) {
			if (isTriviaMaster(this.msg) && this.cleanupThread != null && this.cleanupThread.isAlive()) {
				try {
					this.trivia.exportAnswersToTriviaMaster();
				} catch (IOException e) {
					SoaClientHelper.sendMsgToChannel(this.msg.getChannel(),
							"Error exporting answers: " + e.getMessage());
					SoaLogging.getLogger().error("Error exporting answers", e);
				}
			} else {
				SoaClientHelper.sendMsgToChannel(this.msg.getChannel(),
						"Either Trivia is not currently enabled or you are not the Trivia Master and therefore are not permitted to receive the answers.");
			}
		} else {
			SoaClientHelper.sendMsgToChannel(this.msg.getChannel(),
					"Either Trivia is not currently enabled or you are not the Trivia Master and therefore are not permitted to receive the answers.");
		}
	}

	/**
	 * Pauses trivia
	 */
	private void pauseTrivia() {
		this.trivia.setTriviaPaused(true);
	}

	/**
	 * Resumes trivia if it is paused
	 */
	private void resumeTrivia() {
		this.trivia.setTriviaPaused(false);
	}

	/**
	 * An automated task to cleanup the trivia configuration and various other
	 * items. This will happen 15 minutes after a trivia session has completed,
	 * allowing time for the triviamaster to retrieve answers if for some reason
	 * they failed to be provided at the end of the session.
	 */
	private void cleanupTask() {
		cleanupThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					triviaThread.join();

					// Sleep 15 minutes
					Thread.sleep(1000 * 60 * 15);
					trivia.cleanupTrivia();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		cleanupThread.start();
	}

	/**
	 * Resets the trivia system. This sets all major configuration parameters back
	 * to null values.
	 */
	private void resetTriviaSystem() {
		if (isStaff(msg)) {
			if (this.trivia != null) {
				if (this.triviaThread != null) {
					if (this.triviaThread.isAlive() && this.trivia.isEnabled()) {
						stopTrivia();
					}
				}
				if (this.cleanupThread != null) {
					if (this.cleanupThread.isAlive()) {
						this.cleanupThread.interrupt();
					}
				}
				this.trivia.cleanupTrivia();
				SoaClientHelper.sendMsgToChannel(msg.getChannel(), "Trivia has been reset");
			}
		}
	}

	/**
	 * Checks if the user is the trivia master (the person who uploaded the trivia
	 * configuration)
	 * 
	 * @param msg
	 *            The message submitted to the Discord bot.
	 * @return True if the user is the triviamaster or a member of staff, false if
	 *         not.
	 */
	private boolean isTriviaMaster(IMessage msg) {
		if (msg.getAuthor().getLongID() == this.trivia.getTriviaMaster())
			return true;
		else if (isStaff(msg))
			return true;
		else
			return false;
	}

	/**
	 * Checks if the user is a member of staff for the server or not. If not
	 * configured at bot startup, the standard SoA ranks (Eldar, Lian, Arquendi) are
	 * provided.
	 * 
	 * @param msg
	 *            The message submitted to the Discord bot.
	 * @return True if the member is staff, false if not.
	 */
	private boolean isStaff(IMessage msg) {
		String[] mustHavePermission = new String[] { "Eldar", "Lian", "Arquendi" };
		if (this.trivia.getConfiguration() == null) {
			return false;
		}
		List<IRole> roleListing = new LinkedList<IRole>(msg.getAuthor().getRolesForGuild(
				msg.getClient().getGuildByID(Long.parseLong(this.trivia.getConfiguration().getServerId()))));
		Iterator<IRole> roleIterator = roleListing.iterator();
		int i = 0;

		while (roleIterator.hasNext()) {
			IRole role = roleIterator.next();
			for (i = 0; i < mustHavePermission.length; i++) {
				if (mustHavePermission[i].equalsIgnoreCase(role.getName()))
					return true;
			}
		}
		return false;
	}

}