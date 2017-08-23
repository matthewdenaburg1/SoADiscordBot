package com.soa.rs.triviacreator.util;

/**
 * The InvalidConfigurationException is thrown if the provided configuration is
 * not able to be validated and therefore cannot be used.
 */
public class InvalidConfigurationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2726872510592033356L;

	/**
	 * Throws the exception with the defined message
	 * 
	 * @param message
	 *            The message associated with the exception
	 */
	public InvalidConfigurationException(String message) {
		super(message);
	}

	/**
	 * Throws the exception with the defined message
	 * 
	 * @param cause
	 *            The cause of the exception
	 */
	public InvalidConfigurationException(Throwable cause) {
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
	public InvalidConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

}
