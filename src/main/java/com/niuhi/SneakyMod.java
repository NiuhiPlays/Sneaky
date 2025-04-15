package com.niuhi;

import com.niuhi.config.ConfigLoader;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SneakyMod implements ModInitializer {
	public static final String MOD_ID = "sneaky-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ConfigLoader.getConfig();
		LOGGER.info("Sneaky Mod initialized");
	}
}