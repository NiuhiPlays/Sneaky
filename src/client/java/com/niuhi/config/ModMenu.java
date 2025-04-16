package com.niuhi.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")) {
            return YACL::getConfigScreen;
        }
        return parent -> null; // No screen if YACL is not present
    }
}