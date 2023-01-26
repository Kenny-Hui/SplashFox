package com.lx.splashfox;

import com.lx.splashfox.Config.Config;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SplashFox implements ClientModInitializer {
	public static Config config;
	public static final Logger LOGGER = LoggerFactory.getLogger("splashfox");

	@Override
	public void onInitializeClient() {
		config = Config.readConfig();
	}
}