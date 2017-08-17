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
import com.soa.rs.triviacreator.util.TriviaFileReader;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.PermissionUtils;

public class SoaTriviaManager {

	private SoaTrivia trivia = null;
	private IMessage msg;
	private Thread triviaThread;
	private Thread cleanupThread;

	public void setMsg(IMessage msg) {
		this.msg = msg;
	}

	public void executeCmd(String[] args) {
		if (args[1].equalsIgnoreCase("help")) {
			// insert some call to a help function here
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
			}
			// else if (args[1].equalsIgnoreCase("pause")) {
			// pauseTrivia();
			// } else if (args[1].equalsIgnoreCase("resume")) {
			// resumeTrivia();
			// }
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
			} else
				SoaClientHelper.sendMsgToChannel(msg.getChannel(), "Trivia Commands should be sent via Private Chat!");
		}

	}

	private void loadTriviaConfiguration() {
		if (!msg.getAttachments().isEmpty()) {
			try {
				String uploadedFileUrl = msg.getAttachments().get(0).getUrl();
				URL url = new URL(uploadedFileUrl);
				TriviaFileReader reader = new TriviaFileReader();

				TriviaConfiguration configuration = reader.loadTriviaConfigFromURL(url);
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

	private void startTrivia() {
		if (this.trivia != null) {
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

	private void stopTrivia() {
		if (this.trivia != null) {
			if (isTriviaMaster(this.msg) && this.trivia.isEnabled()) {
				this.trivia.enableTrivia(false);
				this.triviaThread.interrupt();
			} else {
				SoaClientHelper.sendMsgToChannel(this.msg.getChannel(),
						"Only the Trivia Master can stop a trivia round.");
			}
		}

	}

	private void recordAnswer(String answer) {
		if (this.trivia != null) {
			if (this.trivia.isEnabled()) {
				String displayName = msg.getAuthor().getDisplayName(
						msg.getClient().getGuildByID(Long.parseLong(this.trivia.getConfiguration().getServerId())));
				answer = answer.replace(".trivia answer ", "");
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
				}
			}
		}
	}

	private void exportAnswers() {
		if (this.trivia != null) {
			if (isTriviaMaster(this.msg) && this.trivia.isEnabled())
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
	}

	/*
	 * These probably won't work based on this setup
	 */
	// private void pauseTrivia() {
	// try {
	// this.triviaThread.wait();
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// private void resumeTrivia() {
	// this.trivia.notify();
	// }

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
			}
		}
	}

	private boolean isTriviaMaster(IMessage msg) {
		if (msg.getAuthor().getLongID() == this.trivia.getTriviaMaster())
			return true;
		else if (isStaff(msg))
			return true;
		else
			return false;
	}

	private boolean isStaff(IMessage msg) {
		String[] mustHavePermission = new String[] { "Eldar", "Lian", "Arquendi" };
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
