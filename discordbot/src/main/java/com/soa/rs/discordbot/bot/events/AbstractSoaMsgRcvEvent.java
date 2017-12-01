package com.soa.rs.discordbot.bot.events;

import java.util.List;

import com.soa.rs.discordbot.util.NoDefinedRolesException;
import com.soa.rs.discordbot.util.SoaClientHelper;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * This abstract class can be used by MessageReceivedEvents for some convienence
 * methods. All events must implement the executeEvent method themselves.
 */
public abstract class AbstractSoaMsgRcvEvent {

	/**
	 * The message received Event
	 */
	private MessageReceivedEvent event;

	/**
	 * List of ranks of which the user who triggered the event must be in order to
	 * execute it.
	 */
	private List<String> mustHavePermission = null;

	/**
	 * Constructor
	 * 
	 * @param event
	 *            MessageReceivedEvent
	 */
	public AbstractSoaMsgRcvEvent(MessageReceivedEvent event) {
		this.event = event;
	}

	/**
	 * Sets the event
	 * 
	 * @param event
	 *            MessageReceivedEvent
	 */
	public void setEvent(MessageReceivedEvent event) {
		this.event = event;
	}

	/**
	 * Gets the event
	 * 
	 * @return the MessageReceivedEvent
	 */
	public MessageReceivedEvent getEvent() {
		return this.event;
	}

	/**
	 * Sets the permissions that the person who triggers the event must have.
	 * 
	 * @param ranks
	 *            an array of ranks which can execute this command.
	 */
	public void setMustHavePermission(List<String> ranks) {
		this.mustHavePermission = ranks;
	}

	/**
	 * Checks if the user who has triggered the event is permitted to execute this
	 * event
	 * 
	 * @return true if the user may execute the event, false otherwise.
	 * @throws NoDefinedRolesException
	 *             If no roles have been specified
	 */
	protected boolean permittedToExecuteEvent() throws NoDefinedRolesException {
		if (mustHavePermission == null) {
			throw new NoDefinedRolesException("No ranks for which to limit this event to have been specified.");
		}

		if (SoaClientHelper.isRank(event.getMessage(), mustHavePermission)) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Execute the event. This method is specific to each event.
	 */
	public abstract void executeEvent();

}
