package com.soa.rs.discordbot.cfg;

import com.soa.rs.discordbot.jaxb.DiscordConfiguration;

public class DiscordCfg {
	private static DiscordConfiguration cfg = null;
	private static String cfgFile = null;

	protected DiscordCfg() {
	}

	public static DiscordConfiguration getInstance() throws Exception {
		if (cfg == null) {
			cfg = loadCfg();

		}
		return cfg;
	}

	public static void setCfgFile(String file) {
		cfgFile = file;
	}

	public static DiscordConfiguration loadCfg() throws Exception {
		ConfigReader reader = new ConfigReader();
		if (cfgFile == null) {
			throw new Exception("No configuration file was specified at startup");
		}
		cfg = reader.loadAppConfig(cfgFile);
		return cfg;

	}
}
