package com.niuhi;

import net.fabricmc.api.ClientModInitializer;

public class SneakyModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		init();
	}

	public static void init() {
		// No-op: YACL/ModMenu integration handled by fabric.mod.json entrypoint
		// Future client-side initialization can be added here
	}
}