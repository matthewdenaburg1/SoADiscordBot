package com.soa.rs.discordbot.util;

/**
 * The No Defined Roles Exception should be used when an event attempts to check
 * if the user has the appropriate role set and no role restrictions have been
 * defined for that event.
 */
public class NoDefinedRolesException extends Exception {

	private static final long serialVersionUID = -8188658573295643611L;

	/**
	 * Throws the exception.
	 */
	public NoDefinedRolesException() {
	}

	/**
	 * Throws the exception with the defined message
	 * 
	 * @param message
	 *            The message associated with the exception
	 */
	public NoDefinedRolesException(String message) {
		super(message);
	}

	/**
	 * Throws the exception with the defined cause
	 * 
	 * @param cause
	 *            The cause of the exception
	 */
	public NoDefinedRolesException(Throwable cause) {
		super(cause);
	}

	/**
	 * Throws the exception with the defined message and cause
	 * 
	 * @param message
	 *            The message associated with the exception
	 * @param cause
	 *            The cause of the exception
	 */
	public NoDefinedRolesException(String message, Throwable cause) {
		super(message, cause);
	}

}
