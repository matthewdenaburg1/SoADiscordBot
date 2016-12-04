package com.soa.rs.discordbot.bot;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soa.rs.discordbot.cfg.ConfigWriter;
import com.soa.rs.discordbot.cfg.DiscordCfg;
import com.soa.rs.discordbot.jaxb.DiscordConfiguration;

/**
 * The ConfigureBot class handles the initial configuration of the bot to
 * prepare it for proper startup.
 */
public class ConfigureBot {

	/**
	 * The bot object
	 */
	private static SoaDiscordBot bot;

	private static final Logger logger = LogManager.getLogger();

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
				DiscordCfg.getInstance().setToken(args[i]);
			} else if (args[i].equals("-eventUrl")) {
				i++;
				DiscordCfg.getInstance().setEventCalendarUrl(args[i]);
			} else if (args[i].equals("-newsUrl")) {
				i++;
				DiscordCfg.getInstance().setNewsUrl(args[i]);
			} else if (args[i].equals("-cfg")) {
				i++;
				loadCfg(args[i]);
				break;
			} else if (args[i].equals("-save")) {
				i++;
				saveCfg(args[i]);
				break;
			} else if (args[i].equals("-help")) {
				printUsage();
			} else {
				logger.warn("Invalid argument: " + args[i] + ", skipping.");
			}
			i++;
		}

	}

	/**
	 * Launches the bot
	 */
	public void launch() {
		if (!DiscordCfg.getInstance().checkNecessaryConfiguration()) {
			printMissingConfiguration();
		}
		logger.info("*********STARTING*********");
		Runtime.getRuntime().addShutdownHook(new DisconnectBot());
		bot = new SoaDiscordBot();
		bot.start();
	}

	/**
	 * Prints out the missing configuration parameters
	 */
	private void printMissingConfiguration() {
		logger.error("Missing required configuration item, the following were missing:"
				+ DiscordCfg.getInstance().getMissingConfigurationParameter());
		printUsage();
		System.exit(-1);
	}

	/**
	 * Loads a pre-configured setup from a XML file
	 * 
	 * @param cfg
	 *            the name/path to the XML file
	 */
	private void loadCfg(String cfg) {
		try {
			DiscordCfg.getInstance().loadFromFile(cfg);
		} catch (JAXBException e) {
			logger.error("Error loading configuration from xml file", e);
		}

	}

	/**
	 * Saves the currently entered configuration to an XML file
	 * 
	 * @param cfg
	 *            the name/path of the XML file
	 */
	private void saveCfg(String cfg) {
		DiscordConfiguration dsc = new DiscordConfiguration();
		if (DiscordCfg.getInstance().checkNecessaryConfiguration()) {
			dsc.setDiscordToken(DiscordCfg.getInstance().getToken());
			dsc.setEventUrl(DiscordCfg.getInstance().getEventCalendarUrl());
			dsc.setNewsUrl(DiscordCfg.getInstance().getNewsUrl());
			ConfigWriter writer = new ConfigWriter();
			try {
				writer.writeConfig(dsc, cfg);
			} catch (JAXBException e) {
				logger.error("Error writing configuration to xml file", e);
			}
		} else {
			printMissingConfiguration();
		}
	}

	/**
	 * Prints usage instructions if an incorrect usage is used or -help is sent
	 * on the command line.
	 */
	private void printUsage() {
		StringBuilder sb = new StringBuilder();
		sb.append("Usage:\n");
		sb.append("java -jar <jar file> -token <token>\n");
		sb.append("java -jar <jar file> -token <token> -eventUrl <eventUrl>\n");
		sb.append("java -jar <jar file> -token <token> -eventUrl <eventUrl> -newsUrl <newsUrl>\n");
		sb.append(
				"java -jar <jar file> -token <token> -eventUrl <eventUrl> -newsUrl <newsUrl> -save <path-to-config-file>\n");
		sb.append("java -jar <jar file> -cfg <path-to-config-file>\n\n");
		sb.append(
				"If -save is used, it must be the last argument in the command.  Arguments after it will be ignored.\n");
		sb.append(
				"If -cfg is used, no arguments entered on the command line will be honored.  The config file will explicitly be used.");
		System.out.println(sb.toString());
	}

	/**
	 * Shutdown hook to ensure the bot disconnects from Discord upon shutdown of
	 * the bot
	 */
	static class DisconnectBot extends Thread {
		/**
		 * Shutdown hook to disconnect the bot.
		 */
		public void run() {
			logger.info("Disconnecting Bot");
			bot.disconnect();
		}
	}
}
