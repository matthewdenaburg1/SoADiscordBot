package com.soa.rs.discordbot.util;

/**
 * The No Defined Roles Exception should be used when an event attempts to check
 * if the user has the appropriate role set and no role restrictions have been
 * defined for that event.
 */
public class NoDefinedRolesException extends Exception {

	/**
	 * Throws the exception.
	 */
	public NoDefinedRolesException() {
	}

	/**
	 * Throws the exception with the defined message
	 * 
	 * @param message
	 */
	public NoDefinedRolesException(String message) {
		super(message);
	}

	/**
	 * Throws the exception with the defined message
	 * 
	 * @param cause
	 */
	public NoDefinedRolesException(Throwable cause) {
		super(cause);
	}

	/**
	 * Throws the exception with the defined message and cause
	 * 
	 * @param message
	 * @param cause
	 */
	public NoDefinedRolesException(String message, Throwable cause) {
		super(message, cause);
	}

}
