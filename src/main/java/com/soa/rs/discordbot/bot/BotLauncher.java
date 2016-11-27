package com.soa.rs.discordbot.bot;

import com.soa.rs.discordbot.cfg.ConfigWriter;
import com.soa.rs.discordbot.cfg.DiscordCfg;
import com.soa.rs.discordbot.jaxb.DiscordConfiguration;

public class BotLauncher {

	private static String token;
	private static String eventURL;
	private static SoaDiscordBot bot;

	public static void main(String[] args) {
		parseArgs(args);
		bot = new SoaDiscordBot();
		Runtime.getRuntime().addShutdownHook(new DisconnectBot());
		bot.start(token, eventURL);

	}

	private static void parseArgs(String[] args) {
		int i = 0;
		while (i < args.length) {
			if (args[i].equals("-token")) {
				i++;
				token = args[i];
			} else if (args[i].equals("-url")) {
				i++;
				eventURL = args[i];
			} else if (args[i].equals("-cfg")) {
				i++;
				loadCfg(args[i]);
				break;
			} else if (args[i].equals("-save")) {
				i++;
				saveCfg(args[i]);
				break;
			}

			i++;
		}

	}

	private static void loadCfg(String cfg) {
		DiscordConfiguration dsc = null;
		DiscordCfg.setCfgFile(cfg);
		try {
			dsc = DiscordCfg.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (dsc != null) {
			token = dsc.getDiscordToken();
			eventURL = dsc.getEventUrl();
		}
	}

	private static void saveCfg(String cfg) {
		DiscordCfg.setCfgFile(cfg);
		DiscordConfiguration dsc = new DiscordConfiguration();
		dsc.setDiscordToken(token);
		dsc.setEventUrl(eventURL);
		ConfigWriter writer = new ConfigWriter();
		writer.writeConfig(dsc, cfg);
	}

	static class DisconnectBot extends Thread {
		public void run() {
			System.out.println("Disconnecting Bot");
			bot.disconnect();
		}
	}

}
