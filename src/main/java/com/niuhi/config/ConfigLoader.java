package com.niuhi.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.niuhi.SneakyMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigLoader {
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "sneaky_mod.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static volatile Config config;

    public static Config getConfig() {
        if (config == null) {
            synchronized (ConfigLoader.class) {
                if (config == null) {
                    loadConfig();
                }
            }
        }
        return config;
    }

    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            config = new Config();
            saveConfig();
            SneakyMod.LOGGER.info("Created default config file: {}", CONFIG_FILE.getPath());
        } else {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, Config.class);
                if (config == null) {
                    SneakyMod.LOGGER.warn("Config file is empty, using defaults");
                    config = new Config();
                    saveConfig();
                }
            } catch (Exception e) {
                SneakyMod.LOGGER.error("Failed to load config: {}", e.getMessage());
                config = new Config();
                saveConfig();
            }
        }
    }

    public static boolean reloadConfig() {
        synchronized (ConfigLoader.class) {
            if (!CONFIG_FILE.exists()) {
                SneakyMod.LOGGER.warn("Config file does not exist: {}", CONFIG_FILE.getPath());
                return false;
            }
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                Config newConfig = GSON.fromJson(reader, Config.class);
                if (newConfig == null) {
                    SneakyMod.LOGGER.warn("Reloaded config is empty, keeping current config");
                    return false;
                }
                config = newConfig;
                SneakyMod.LOGGER.info("Config reloaded successfully");
                return true;
            } catch (Exception e) {
                SneakyMod.LOGGER.error("Failed to reload config: {}", e.getMessage());
                return false;
            }
        }
    }

    private static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            SneakyMod.LOGGER.error("Failed to save config: {}", e.getMessage());
        }
    }
}