package com.niuhi;

import com.niuhi.config.ConfigLoader;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SneakyMod implements ModInitializer {
	public static final String MOD_ID = "sneaky-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ConfigLoader.getConfig();
		registerCommands();
		if (FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3") &&
				FabricLoader.getInstance().isModLoaded("modmenu")) {
			try {
				Class.forName("com.niuhi.client.ClientInitializer").getMethod("init").invoke(null);
				LOGGER.info("YACL and ModMenu detected, initialized client config integration");
			} catch (Exception e) {
				LOGGER.error("Failed to initialize YACL/ModMenu integration: {}", e.getMessage());
			}
		}
		LOGGER.info("Sneaky Mod initialized");
	}

	private void registerCommands() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			registerReloadCommand(dispatcher);
		});
	}

	private void registerReloadCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
				CommandManager.literal("sneakymod")
						.then(
								CommandManager.literal("reload")
										.requires(source -> source.hasPermissionLevel(2))
										.executes(context -> {
											boolean success = ConfigLoader.reloadConfig();
											if (success) {
												context.getSource().sendMessage(Text.literal("SneakyMod config reloaded successfully"));
											} else {
												context.getSource().sendError(Text.literal("Failed to reload SneakyMod config. Check server logs for details"));
											}
											return success ? 1 : 0;
										})
						)
		);
	}
}