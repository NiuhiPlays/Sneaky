package com.niuhi;

import com.niuhi.config.YACL;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import static com.mojang.text2speech.Narrator.LOGGER;

public class SneakyModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing SneakyMod Client");
		if (FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")) {
			LOGGER.info("YACL detected, registering config screen");
			YACL.init();
		} else {
			LOGGER.info("YACL not present, skipping config screen");
		}
	}
}