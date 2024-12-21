package com.lx862.splashfox;

import com.lx862.splashfox.config.Config;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SplashFox implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("SplashFox");
	public static Config config;

	@Override
	public void onInitializeClient() {
		config = Config.readConfig();
	}
}