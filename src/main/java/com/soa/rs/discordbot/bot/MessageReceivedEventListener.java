package com.soa.rs.discordbot.bot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soa.rs.discordbot.bot.events.SoaAdminNewsEvent;
import com.soa.rs.discordbot.bot.events.SoaBotInfoEvent;
import com.soa.rs.discordbot.bot.events.SoaEventListerTask;
import com.soa.rs.discordbot.bot.events.SoaHelpEvent;
import com.soa.rs.discordbot.bot.events.SoaMusicPlayer;
import com.soa.rs.discordbot.cfg.DiscordCfg;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 * The MessageReceivedEventListener handles any commands that may come in via
 * messages typed within a Discord channel
 */
public class MessageReceivedEventListener implements IListener<MessageReceivedEvent> {

	/**
	 * Bot command prefix - all commands MUST begin with this.
	 */
	private String botPrefix = ".";
	private static final Logger logger = LogManager.getLogger();

	/*
	 * Recommend storing any classes for events as fields, as once they are
	 * created they hopefully won't need to be recreated
	 */
	/**
	 * Music player instance for the bot.
	 */
	private SoaMusicPlayer player = null;
	/**
	 * Event Lister for the MessageReceivedEvent
	 */
	private SoaEventListerTask eventListerTask = null;

	/**
	 * Handles the MessageReceivedEvent
	 * 
	 * @param event
	 *            The Message Received Event
	 */
	public void handle(MessageReceivedEvent event) {
		IMessage msg = event.getMessage();

		if (msg.getContent().startsWith(botPrefix)) {
			String command = msg.getContent().replaceFirst(botPrefix, "");
			String[] args = command.split(" ");

			/*
			 * Music Player Command
			 */
			if (args[0].equalsIgnoreCase("music")) {
				if (player == null) {
					player = new SoaMusicPlayer(msg);
				}
				try {
					player.setMsg(msg);
					player.handleMusicArgs(event, args);
				} catch (Exception e) {
					logger.error("Exception thrown in MusicPlayer", e);
				}
			}

			/*
			 * Event Lister command
			 */
			else if (args[0].equals("events")) {
				if (eventListerTask == null) {
					eventListerTask = new SoaEventListerTask(DiscordCfg.getInstance().getEventCalendarUrl(),
							event.getClient(), msg.getChannel());
					eventListerTask.run();
				} else {
					eventListerTask.setChannel(msg.getChannel());
					eventListerTask.run();
				}
			}

			else if (args[0].equals("info")) {
				SoaBotInfoEvent infoEvent = new SoaBotInfoEvent(event);
				infoEvent.executeEvent();
			}

			else if (args[0].equals("adminnews")) {
				SoaAdminNewsEvent newsEvent = new SoaAdminNewsEvent(event);
				newsEvent.setMustHavePermission(new String[] { "Eldar", "Lian" });
				newsEvent.setArgs(args);
				newsEvent.executeEvent();
			}

			else if (args[0].equals("help")) {
				SoaHelpEvent helpEvent = new SoaHelpEvent(event);
				helpEvent.executeEvent();
			}
		}

	}
}
