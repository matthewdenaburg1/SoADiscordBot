package com.soa.rs.discordbot.util;

public class NoSuchServerException extends Exception {

	private static final long serialVersionUID = 7697592447942724767L;

	/**
	 * Throws the exception.
	 */
	public NoSuchServerException() {
	}

	/**
	 * Throws the exception with the defined message
	 * 
	 * @param message
	 *            The message associated with the exception
	 */
	public NoSuchServerException(String message) {
		super(message);
	}

	/**
	 * Throws the exception with the defined message
	 * 
	 * @param cause
	 *            The cause of the exception
	 */
	public NoSuchServerException(Throwable cause) {
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
	public NoSuchServerException(String message, Throwable cause) {
		super(message, cause);
	}

}
