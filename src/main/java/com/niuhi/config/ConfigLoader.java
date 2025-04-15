package com.niuhi.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigLoader {
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "Sneaky Mod.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Config config;

    public static Config getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }

    private static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            config = new Config();
            saveConfig();
        } else {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, Config.class);
            } catch (IOException e) {
                System.err.println("Failed to load config, using defaults: " + e.getMessage());
                config = new Config();
                saveConfig();
            }
        }
    }

    private static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }
}