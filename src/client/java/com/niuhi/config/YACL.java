package com.niuhi.config;

import com.niuhi.SneakyMod;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class YACL {
    // Regex for basic Minecraft ID validation (minecraft:<name> or <name>)
    private static final Pattern ID_PATTERN = Pattern.compile("^[a-z0-9_-]+(:[a-z0-9_-]+)?$");

    public static Screen getConfigScreen(Screen parent) {
        Config config = ConfigLoader.getConfig();

        return YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Sneaky Mod Config"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Sound Detection"))
                        .tooltip(Text.literal("General sound detection settings"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("General"))
                                .option(Option.<Float>createBuilder()
                                        .name(Text.literal("Ambient Sound Multiplier"))
                                        .description(OptionDescription.of(Text.literal("Multiplier for sounds near ambient sources")))
                                        .binding(
                                                0.5f,
                                                () -> config.soundDetection.ambientSoundMultiplier,
                                                value -> config.soundDetection.ambientSoundMultiplier = value
                                        )
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.0f, 1.0f).step(0.1f))
                                        .build())
                                .option(Option.<Float>createBuilder()
                                        .name(Text.literal("Sound Cooldown"))
                                        .description(OptionDescription.of(Text.literal("Seconds before mobs react again")))
                                        .binding(
                                                2.0f,
                                                () -> config.soundDetection.soundCooldownSeconds,
                                                value -> config.soundDetection.soundCooldownSeconds = value
                                        )
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.0f, 10.0f).step(0.5f))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Movement"))
                                .option(Option.<Float>createBuilder()
                                        .name(Text.literal("walk Radius"))
                                        .description(OptionDescription.of(Text.literal("Radius for detecting player movement (blocks)")))
                                        .binding(
                                                6.0f,
                                                () -> config.soundDetection.movement.walkRadius,
                                                value -> config.soundDetection.movement.walkRadius = value
                                        )
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.0f, 16.0f).step(1.0f))
                                        .build())
                                .option(Option.<Float>createBuilder()
                                        .name(Text.literal("Sprinting Radius"))
                                        .description(OptionDescription.of(Text.literal("Radius for detecting player movement (blocks)")))
                                        .binding(
                                                6.0f,
                                                () -> config.soundDetection.movement.sprintRadius,
                                                value -> config.soundDetection.movement.sprintRadius = value
                                        )
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.0f, 16.0f).step(1.0f))
                                        .build())
                                .option(Option.<Float>createBuilder()
                                        .name(Text.literal("Jumping Radius"))
                                        .description(OptionDescription.of(Text.literal("Radius for detecting player movement (blocks)")))
                                        .binding(
                                                6.0f,
                                                () -> config.soundDetection.movement.jumpRadius,
                                                value -> config.soundDetection.movement.jumpRadius = value
                                        )
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.0f, 16.0f).step(1.0f))
                                        .build())
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("View Cone"))
                        .tooltip(Text.literal("Settings for mob view cone detection"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("View Cone Settings"))
                                .option(Option.<Float>createBuilder()
                                        .name(Text.literal("Cone Angle"))
                                        .description(OptionDescription.of(Text.literal("View cone angle in degrees")))
                                        .binding(
                                                60.0f,
                                                () -> config.viewCone.coneAngle,
                                                value -> config.viewCone.coneAngle = value
                                        )
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(10.0f, 180.0f).step(5.0f))
                                        .build())
                                .option(Option.<Float>createBuilder()
                                        .name(Text.literal("Max Distance"))
                                        .description(OptionDescription.of(Text.literal("Maximum detection distance (blocks)")))
                                        .binding(
                                                16.0f,
                                                () -> config.viewCone.maxDistance,
                                                value -> config.viewCone.maxDistance = value
                                        )
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(1.0f, 32.0f).step(1.0f))
                                        .build())
                                .option(Option.<Float>createBuilder()
                                        .name(Text.literal("Proximity Radius"))
                                        .description(OptionDescription.of(Text.literal("Always-detect radius (blocks)")))
                                        .binding(
                                                1.0f,
                                                () -> config.stealthDetection.proximityRadius,
                                                value -> config.stealthDetection.proximityRadius = value
                                        )
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.0f, 5.0f).step(1.0f))
                                        .build())
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Stealth Detection"))
                        .tooltip(Text.literal("Settings for stealth-based mob detection"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Stealth Settings"))
                                .option(Option.<Float>createBuilder()
                                        .name(Text.literal("Sneak Multiplier"))
                                        .description(OptionDescription.of(Text.literal("Detection chance multiplier when sneaking")))
                                        .binding(
                                                0.5f,
                                                () -> config.stealthDetection.sneakMultiplier,
                                                value -> config.stealthDetection.sneakMultiplier = value
                                        )
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.0f, 1.0f).step(0.1f))
                                        .build())
                                .option(Option.<Float>createBuilder()
                                        .name(Text.literal("Max Light Chance"))
                                        .description(OptionDescription.of(Text.literal("Detection chance at light level 15")))
                                        .binding(
                                                1.0f,
                                                () -> config.stealthDetection.lightLevelMaxChance,
                                                value -> config.stealthDetection.lightLevelMaxChance = value
                                        )
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.0f, 1.0f).step(0.1f))
                                        .build())
                                .option(Option.<Float>createBuilder()
                                        .name(Text.literal("Min Light Chance"))
                                        .description(OptionDescription.of(Text.literal("Detection chance at light level 0")))
                                        .binding(
                                                0.0f,
                                                () -> config.stealthDetection.lightLevelMinChance,
                                                value -> config.stealthDetection.lightLevelMinChance = value
                                        )
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.0f, 1.0f).step(0.1f))
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Hide in Tall Plants"))
                                        .description(OptionDescription.of(Text.literal("Hide when sneaking in tall plants")))
                                        .binding(
                                                true,
                                                () -> config.stealthDetection.hideInTallPlants,
                                                value -> config.stealthDetection.hideInTallPlants = value
                                        )
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Use & Interactions"))
                        .tooltip(Text.literal("Settings for item use and block interaction sounds"))
                        .group(ListOption.<KeyValueController.KeyValuePair<String, Float>>createBuilder()
                                .name(Text.literal("Use Items"))
                                .description(OptionDescription.of(Text.literal("Items that trigger sound detection when used")))
                                .insertEntriesAtEnd(true)
                                .binding(
                                        defaultItemList(),
                                        () -> itemsToList(config.soundDetection.use.items),
                                        values -> updateItemsMap(config.soundDetection.use.items, values)
                                )
                                .customController(opt -> new KeyValueController<>(opt, 0.6,
                                        Option.<String>createBuilder()
                                                .name(Text.literal("Item ID"))
                                                .binding(
                                                        "",
                                                        () -> opt.pendingValue().getKey(),
                                                        newValue -> opt.requestSet(new KeyValueController.KeyValuePair<>(newValue, opt.pendingValue().getValue()))
                                                )
                                                .controller(StringControllerBuilder::create)
                                                .build().controller(),
                                        Option.<Float>createBuilder()
                                                .name(Text.literal("Radius"))
                                                .binding(
                                                        6.0f,
                                                        () -> opt.pendingValue().getValue(),
                                                        newValue -> opt.requestSet(new KeyValueController.KeyValuePair<>(opt.pendingValue().getKey(), newValue))
                                                )
                                                .controller(opt2 -> FloatSliderControllerBuilder.create(opt2).range(0.0f, 16.0f).step(1.0f))
                                                .build().controller()
                                ))
                                .initial(new KeyValueController.KeyValuePair<>("minecraft:new_item", 6.0f))
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Block Interaction"))
                                .option(Option.<Float>createBuilder()
                                        .name(Text.literal("Pressure Plates Radius"))
                                        .description(OptionDescription.of(Text.literal("Radius for pressure plate activation")))
                                        .binding(
                                                6.0f,
                                                () -> config.soundDetection.interaction.tagConfigs.get("minecraft:pressure_plates").radius,
                                                value -> config.soundDetection.interaction.tagConfigs.get("minecraft:pressure_plates").radius = value
                                        )
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.0f, 16.0f).step(1.0f))
                                        .build())
                                .option(Option.<Float>createBuilder()
                                        .name(Text.literal("Buttons Radius"))
                                        .description(OptionDescription.of(Text.literal("Radius for button presses")))
                                        .binding(
                                                4.0f,
                                                () -> config.soundDetection.interaction.tagConfigs.get("minecraft:buttons").radius,
                                                value -> config.soundDetection.interaction.tagConfigs.get("minecraft:buttons").radius = value
                                        )
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.0f, 16.0f).step(1.0f))
                                        .build())
                                .build())
                        // Create separate group for the block list
                        .group(ListOption.<KeyValueController.KeyValuePair<String, Float>>createBuilder()
                                .name(Text.literal("Interaction Blocks"))
                                .description(OptionDescription.of(Text.literal("Blocks that trigger sound detection when interacted with")))
                                .insertEntriesAtEnd(true)
                                .binding(
                                        defaultBlockList(),
                                        () -> blocksToList(config.soundDetection.interaction.blocks),
                                        values -> updateBlocksMap(config.soundDetection.interaction.blocks, values)
                                )
                                .customController(opt -> new KeyValueController<>(opt, 0.6,
                                        Option.<String>createBuilder()
                                                .name(Text.literal("Block ID"))
                                                .binding(
                                                        "",
                                                        () -> opt.pendingValue().getKey(),
                                                        newValue -> opt.requestSet(new KeyValueController.KeyValuePair<>(newValue, opt.pendingValue().getValue()))
                                                )
                                                .controller(StringControllerBuilder::create)
                                                .build().controller(),
                                        Option.<Float>createBuilder()
                                                .name(Text.literal("Radius"))
                                                .binding(
                                                        6.0f,
                                                        () -> opt.pendingValue().getValue(),
                                                        newValue -> opt.requestSet(new KeyValueController.KeyValuePair<>(opt.pendingValue().getKey(), newValue))
                                                )
                                                .controller(opt2 -> FloatSliderControllerBuilder.create(opt2).range(0.0f, 16.0f).step(1.0f))
                                                .build().controller()
                                ))
                                .initial(new KeyValueController.KeyValuePair<>("minecraft:new_block", 6.0f))
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Falling Blocks"))
                        .tooltip(Text.literal("Settings for falling block sounds"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Falling Blocks"))
                                .option(Option.<Float>createBuilder()
                                        .name(Text.literal("Concrete Powder Radius"))
                                        .description(OptionDescription.of(Text.literal("Radius for concrete powder landing")))
                                        .binding(
                                                10.0f,
                                                () -> config.soundDetection.fallingBlock.tagConfigs.get("minecraft:concrete_powder").radius,
                                                value -> config.soundDetection.fallingBlock.tagConfigs.get("minecraft:concrete_powder").radius = value
                                        )
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.0f, 16.0f).step(1.0f))
                                        .build())
                                .build())
                        .build())
                .save(() -> {
                    try (java.io.FileWriter writer = new java.io.FileWriter(new java.io.File(
                            net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().toFile(), "Sneaky Mod.json"))) {
                        new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(config, writer);
                    } catch (java.io.IOException e) {
                        com.niuhi.SneakyMod.LOGGER.error("Failed to save config from YACL: {}", e.getMessage());
                    }
                    ConfigLoader.reloadConfig();
                })
                .build()
                .generateScreen(parent);
    }

    private static List<KeyValueController.KeyValuePair<String, Float>> defaultItemList() {
        return new ArrayList<>(List.of(
                new KeyValueController.KeyValuePair<>("minecraft:ender_pearl", 6.0f),
                new KeyValueController.KeyValuePair<>("minecraft:flint_and_steel", 6.0f),
                new KeyValueController.KeyValuePair<>("minecraft:goat_horn", 6.0f),
                new KeyValueController.KeyValuePair<>("minecraft:fire_charge", 6.0f)
        ));
    }

    private static List<KeyValueController.KeyValuePair<String, Float>> defaultBlockList() {
        return new ArrayList<>(List.of(
                new KeyValueController.KeyValuePair<>("minecraft:bell", 24.0f),
                new KeyValueController.KeyValuePair<>("minecraft:chest", 6.0f),
                new KeyValueController.KeyValuePair<>("minecraft:anvil", 6.0f),
                new KeyValueController.KeyValuePair<>("minecraft:lever", 6.0f)
        ));
    }

    private static List<KeyValueController.KeyValuePair<String, Float>> itemsToList(Map<String, Config.SoundDetectionConfig.ItemConfig> itemMap) {
        List<KeyValueController.KeyValuePair<String, Float>> list = new ArrayList<>();
        for (Map.Entry<String, Config.SoundDetectionConfig.ItemConfig> entry : itemMap.entrySet()) {
            list.add(new KeyValueController.KeyValuePair<>(entry.getKey(), entry.getValue().radius));
        }
        return list;
    }

    private static List<KeyValueController.KeyValuePair<String, Float>> blocksToList(Map<String, Config.SoundDetectionConfig.BlockConfig> blockMap) {
        List<KeyValueController.KeyValuePair<String, Float>> list = new ArrayList<>();
        for (Map.Entry<String, Config.SoundDetectionConfig.BlockConfig> entry : blockMap.entrySet()) {
            list.add(new KeyValueController.KeyValuePair<>(entry.getKey(), entry.getValue().radius));
        }
        return list;
    }

    private static void updateItemsMap(Map<String, Config.SoundDetectionConfig.ItemConfig> itemMap, List<KeyValueController.KeyValuePair<String, Float>> values) {
        itemMap.clear();
        for (KeyValueController.KeyValuePair<String, Float> pair : values) {
            if (isValidId(pair.getKey()) && pair.getValue() >= 0) {
                itemMap.put(pair.getKey(), new Config.SoundDetectionConfig.ItemConfig(pair.getValue()));
            } else {
                SneakyMod.LOGGER.warn("Invalid item entry '{}|{}': invalid ID or negative radius", pair.getKey(), pair.getValue());
            }
        }
    }

    private static void updateBlocksMap(Map<String, Config.SoundDetectionConfig.BlockConfig> blockMap, List<KeyValueController.KeyValuePair<String, Float>> values) {
        blockMap.clear();
        for (KeyValueController.KeyValuePair<String, Float> pair : values) {
            if (isValidId(pair.getKey()) && pair.getValue() >= 0) {
                blockMap.put(pair.getKey(), new Config.SoundDetectionConfig.BlockConfig(pair.getValue()));
            } else {
                SneakyMod.LOGGER.warn("Invalid block entry '{}|{}': invalid ID or negative radius", pair.getKey(), pair.getValue());
            }
        }
    }

    private static boolean isValidId(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        return ID_PATTERN.matcher(id).matches();
    }
}