package com.soa.rs.discordbot.bot.events;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.soa.rs.discordbot.util.NoDefinedRolesException;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;

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
	 * List of ranks of which the user who triggered the event must be in order
	 * to execute it.
	 */
	private String[] mustHavePermission = null;

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
	public void setMustHavePermission(String[] ranks) {
		this.mustHavePermission = ranks;
	}

	/**
	 * Checks if the user who has triggered the event is permitted to execute
	 * this event
	 * 
	 * @return true if the user may execute the event, false otherwise.
	 * @throws NoDefinedRolesException
	 *             If no roles have been specified
	 */
	protected boolean permittedToExecuteEvent() throws NoDefinedRolesException {
		if (mustHavePermission == null) {
			throw new NoDefinedRolesException("No ranks for which to limit this event to have been specified.");
		}
		IGuild guild = event.getMessage().getGuild();
		if (guild == null) {
				return false;
		}
		List<IRole> roleListing = new LinkedList<IRole>(event.getMessage().getAuthor().getRolesForGuild(guild));
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

	/**
	 * Execute the event. This method is specific to each event.
	 */
	public abstract void executeEvent();

}
