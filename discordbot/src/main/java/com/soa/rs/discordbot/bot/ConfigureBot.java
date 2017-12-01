package com.soa.rs.discordbot.bot;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import com.soa.rs.discordbot.cfg.ConfigValidator;
import com.soa.rs.discordbot.cfg.ConfigValidatorResult;
import com.soa.rs.discordbot.cfg.DefaultConfigValidator;
import com.soa.rs.discordbot.cfg.DiscordCfgFactory;
import com.soa.rs.discordbot.cfg.InvalidBotConfigurationException;
import com.soa.rs.discordbot.util.SoaLogging;

/**
 * The ConfigureBot class handles the initial configuration of the bot to
 * prepare it for proper startup.
 */
public class ConfigureBot {

	/**
	 * The bot object
	 */
	private static SoaDiscordBot bot;

	/**
	 * Constructor which passes the command line arguments to the parseArgs
	 * function.
	 * 
	 * @param args
	 *            command line arguments.
	 */
	public ConfigureBot(String[] args) {
		parseArgs(args);
	}

	/**
	 * This method is used to parse the arguments off of the command line for
	 * setting up the configuration for the bot.
	 * 
	 * @param args
	 *            command line arguments
	 */
	private void parseArgs(String[] args) {
		int i = 0;
		if (args.length == 0) {
			printUsage();
			System.exit(-1);
		}
		while (i < args.length) {
			if (args[i].equals("-token")) {
				i++;
				DiscordCfgFactory.getConfig().setDiscordToken(args[i]);
				// } else if (args[i].equals("-eventUrl")) {
				// i++;
				// DiscordCfgFactory.getInstance().setEventCalendarUrl(args[i]);
				// } else if (args[i].equals("-newsUrl")) {
				// i++;
				// DiscordCfgFactory.getInstance().setNewsUrl(args[i]);
			} else if (args[i].equals("-cfg")) {
				i++;
				loadCfg(args[i]);
				// break;
			} else if (args[i].equals("-avatar")) {
				i++;
				DiscordCfgFactory.getInstance().setAvatarUrl(args[i]);
			} else if (args[i].equals("-botname")) {
				i++;
				DiscordCfgFactory.getInstance().setBotname(args[i]);
			} else if (args[i].equals("-save")) {
				i++;
				// saveCfg(args[i]);
				break;
			} else if (args[i].equals("-help")) {
				printUsage();
			} else {
				SoaLogging.getLogger().warn("Invalid argument: " + args[i] + ", skipping.");
			}
			i++;
		}

	}

	/**
	 * Launches the bot
	 */
	public void launch() {
		// if (!DiscordCfgFactory.getInstance().checkNecessaryConfiguration()) {
		// printMissingConfiguration();
		// }
		ConfigValidatorResult validationResults = validateConfig();

		/*
		 * If event some event is invalid and we want to fail if that's the case?
		 */
		if (validationResults.isEventWasInvalid()) {
			// Something?
		}

		if (validationResults.isConfigValidSchema()) {
			SoaLogging.getLogger().info("*********STARTING*********");
			Runtime.getRuntime().addShutdownHook(new DisconnectBot());
			bot = new SoaDiscordBot();
			bot.start();
		} else {
			System.exit(-1);
		}
	}

	/**
	 * Prints out the missing configuration parameters
	 */
	// private void printMissingConfiguration() {
	// SoaLogging.getLogger().error("Missing required configuration item, the
	// following were missing:"
	// + DiscordCfgFactory.getInstance().getMissingConfigurationParameter());
	// printUsage();
	// System.exit(-1);
	// }

	/**
	 * Loads a pre-configured setup from a XML file
	 * 
	 * @param cfg
	 *            the name/path to the XML file
	 */
	private void loadCfg(String cfg) {
		try {
			DiscordCfgFactory.getInstance().loadFromFile(cfg);
		} catch (JAXBException | SAXException e) {
			SoaLogging.getLogger().error("Error loading configuration from xml file", e);
		}

	}

	/**
	 * Saves the currently entered configuration to an XML file
	 * 
	 * @param cfg
	 *            the name/path of the XML file
	 */
	// private void saveCfg(String cfg) {
	// DiscordConfiguration dsc = new DiscordConfiguration();
	// if (DiscordCfgFactory.getInstance().checkNecessaryConfiguration()) {
	// dsc.setDiscordToken(DiscordCfgFactory.getConfig().getDiscordToken());
	// dsc.setEventUrl(DiscordCfgFactory.getInstance().getEventCalendarUrl());
	// dsc.setNewsUrl(DiscordCfgFactory.getInstance().getNewsUrl());
	// XmlWriter writer = new XmlWriter();
	// try {
	// writer.writeConfig(dsc, cfg);
	// } catch (JAXBException | SAXException e) {
	// SoaLogging.getLogger().error("Error writing configuration to xml file", e);
	// }
	// } else {
	// printMissingConfiguration();
	// }
	// }

	/**
	 * Validates the bot's configuration. This method will validate all aspects of
	 * the configuration. Implementations wishing to only validate certain aspects
	 * should call the individual methods instead.
	 * <p>
	 * Any events within the configuration that are found to be invalid will be
	 * disabled, so that the bot can run with the valid events only.
	 * 
	 * 
	 * @return True if the configuration was valid, false if the configuration was
	 *         found to not be valid.
	 */
	private ConfigValidatorResult validateConfig() {
		ConfigValidatorResult result = new ConfigValidatorResult();
		ConfigValidator validator = new DefaultConfigValidator();

		try {
			if (DiscordCfgFactory.getConfig().getEventListingEvent() != null)
				validator.validateListingEvent(DiscordCfgFactory.getConfig().getEventListingEvent());
		} catch (InvalidBotConfigurationException e) {
			SoaLogging.getLogger()
					.warn("Event Listing Event failed to validate: " + e.getMessage() + ", setting disabled");
			DiscordCfgFactory.getConfig().getEventListingEvent().setEnabled(false);
			result.setEventWasInvalid(true);
		}
		try {
			if (DiscordCfgFactory.getConfig().getNewsListingEvent() != null)
				validator.validateListingEvent(DiscordCfgFactory.getConfig().getNewsListingEvent());
		} catch (InvalidBotConfigurationException e) {
			SoaLogging.getLogger()
					.warn("News Listing Event failed to validate: " + e.getMessage() + ", setting disabled");
			DiscordCfgFactory.getConfig().getNewsListingEvent().setEnabled(false);
			result.setEventWasInvalid(true);
		}

		try {
			if (DiscordCfgFactory.getConfig().getMusicPlayer() != null)
				validator.validateMusicPlayer(DiscordCfgFactory.getConfig().getMusicPlayer());
		} catch (InvalidBotConfigurationException e) {
			SoaLogging.getLogger().warn("Music Event failed to validate: " + e.getMessage() + ", setting disabled");
			DiscordCfgFactory.getConfig().getMusicPlayer().setEnabled(false);
			result.setEventWasInvalid(true);
		}

		try {
			if (DiscordCfgFactory.getConfig().getAdminEvent() != null)
				validator.validateAdminEvent(DiscordCfgFactory.getConfig().getAdminEvent());
		} catch (InvalidBotConfigurationException e) {
			SoaLogging.getLogger().warn("Admin Event failed to validate: " + e.getMessage() + ", setting disabled");
			DiscordCfgFactory.getConfig().getAdminEvent().setEnabled(false);
			result.setEventWasInvalid(true);
		}

		try {
			if (DiscordCfgFactory.getConfig().getUserTrackingEvent() != null)
				validator.validateUserTrackingEvent(DiscordCfgFactory.getConfig().getUserTrackingEvent());
		} catch (InvalidBotConfigurationException e) {
			SoaLogging.getLogger()
					.warn("User Tracking Event failed to validate: " + e.getMessage() + ", setting disabled");
			DiscordCfgFactory.getConfig().getUserTrackingEvent().setEnabled(false);
			result.setEventWasInvalid(true);
		}

		result.setConfigFailedSchema(validator.validateConformsToSchema(DiscordCfgFactory.getConfig()));
		return result;
	}

	/**
	 * Prints usage instructions if an incorrect usage is used or -help is sent on
	 * the command line.
	 */
	private void printUsage() {
		StringBuilder sb = new StringBuilder();
		sb.append("Usage:\n");
		sb.append("java -jar <jar file> -cfg <path-to-config-file>\n\n");
		sb.append("Optional arguments:\n");
		sb.append("-avatar <url>: Changes the bot's avatar from the current avatar.\n");
		sb.append("-botname <name>: Changes the bot's current name.\n");
		System.out.println(sb.toString());
	}

	/**
	 * Shutdown hook to ensure the bot disconnects from Discord upon shutdown of the
	 * bot
	 */
	static class DisconnectBot extends Thread {
		/**
		 * Shutdown hook to disconnect the bot.
		 */
		@Override
		public void run() {
			SoaLogging.getLogger().info("Disconnecting Bot");
			bot.disconnect();
		}
	}
}
