package com.niuhi.config;

import java.util.HashMap;
import java.util.Map;

public class Config {
    public ViewConeConfig viewCone = new ViewConeConfig();
    public StealthDetectionConfig stealthDetection = new StealthDetectionConfig();
    public SoundDetectionConfig soundDetection = new SoundDetectionConfig();

    public static class ViewConeConfig {
        public float coneAngle = 60.0f; // Degrees
        public float maxDistance = 16.0f; // Blocks
    }

    public static class StealthDetectionConfig {
        public float sneakMultiplier = 0.5f; // Detection chance multiplier when sneaking
        public float lightLevelMaxChance = 1.0f; // Chance at light level 15
        public float lightLevelMinChance = 0.0f; // Chance at light level 0
        public float proximityRadius = 2.0f; // Blocks to always detect player
        public boolean hideInTallPlants = true; // Hides player when sneaking in tall plants
    }

    public static class SoundDetectionConfig {
        public MovementConfig movement = new MovementConfig();
        public UseConfig use = new UseConfig();
        public InteractionConfig interaction = new InteractionConfig();
        public FallingBlockConfig fallingBlock = new FallingBlockConfig();
        public ProjectileConfig projectile = new ProjectileConfig();
        public ExplosionConfig explosion = new ExplosionConfig();

        public float ambientSoundMultiplier = 0.5f; // Multiplier for sounds near ambient sources
        public float soundCooldownSeconds = 2.0f; // Seconds before mob can react again

        public boolean soundSofteningUseBlockTags = true; // Enable tag-based detection for sound softening
        public Map<String, BlockConfig> soundSofteningTagConfigs = new HashMap<>();
        public Map<String, BlockConfig> soundSofteningBlocks = new HashMap<>();

        public boolean fallDamageSofteningUseBlockTags = true; // Enable tag-based detection for fall damage
        public Map<String, BlockConfig> fallDamageSofteningTagConfigs = new HashMap<>();
        public Map<String, BlockConfig> fallDamageSofteningBlocks = new HashMap<>();

        public SoundDetectionConfig() {
            // Default sound-softening tags (e.g., wool, carpets, leaves reduce sound)
            soundSofteningTagConfigs.put("minecraft:wool", new BlockConfig(0.5f));
            soundSofteningTagConfigs.put("minecraft:carpets", new BlockConfig(0.7f));
            soundSofteningTagConfigs.put("minecraft:leaves", new BlockConfig(0.6f));
            soundSofteningBlocks.put("minecraft:moss_block", new BlockConfig(0.6f));

            // Default fall damage-softening blocks (no tags by default, but structure allows it)
            fallDamageSofteningBlocks.put("minecraft:hay_block", new BlockConfig(0.0f)); // Negates fall damage
            fallDamageSofteningBlocks.put("minecraft:moss_block", new BlockConfig(0.2f)); // Reduces to 20%
            fallDamageSofteningTagConfigs.put("minecraft:leaves", new BlockConfig(0.6f)); // Reduces to 50%
        }

        public static class MovementConfig {
            public float walkRadius = 8.0f; // Blocks for walking
            public float sprintRadius = 12.0f; // Blocks for sprinting
            public float jumpRadius = 10.0f; // Blocks for jumping
            public float multiplier = 1.0f;
        }

        public static class UseConfig {
            public float multiplier = 1.0f;
            public Map<String, ItemConfig> items = new HashMap<>();
            public UseConfig() {
                items.put("minecraft:ender_pearl", new ItemConfig(8.0f));
                items.put("minecraft:flint_and_steel", new ItemConfig(8.0f));
                items.put("minecraft:goat_horn", new ItemConfig(32.0f));
                items.put("minecraft:fire_charge", new ItemConfig(8.0f));
            }
        }

        public static class InteractionConfig {
            public float multiplier = 1.0f;
            public boolean useBlockTags = true; // Enable tag-based detection
            public Map<String, BlockConfig> tagConfigs = new HashMap<>();
            public Map<String, BlockConfig> blocks = new HashMap<>();
            public InteractionConfig() {
                tagConfigs.put("minecraft:doors", new BlockConfig(14.0f));
                tagConfigs.put("minecraft:trapdoors", new BlockConfig(14.0f));
                tagConfigs.put("minecraft:fence_gates", new BlockConfig(14.0f));
                tagConfigs.put("minecraft:buttons", new BlockConfig(8.0f));
                tagConfigs.put("minecraft:pressure_plates", new BlockConfig(8.0f));
                blocks.put("minecraft:bell", new BlockConfig(30.0f));
                blocks.put("minecraft:chest", new BlockConfig(6.0f));
                blocks.put("minecraft:anvil", new BlockConfig(6.0f));
                blocks.put("minecraft:lever", new BlockConfig(6.0f));
            }
        }

        public static class FallingBlockConfig {
            public float multiplier = 1.0f;
            public boolean useBlockTags = true; // Enable tag-based detection
            public Map<String, BlockConfig> tagConfigs = new HashMap<>();
            public Map<String, BlockConfig> fallingBlocks = new HashMap<>();
            public FallingBlockConfig() {
                tagConfigs.put("minecraft:concrete_powder", new BlockConfig(12.0f));
                fallingBlocks.put("minecraft:anvil", new BlockConfig(24.0f));
                fallingBlocks.put("minecraft:pointed_dripstone", new BlockConfig(12.0f));
                fallingBlocks.put("minecraft:gravel", new BlockConfig(12.0f));
                fallingBlocks.put("minecraft:sand", new BlockConfig(12.0f));
            }
        }

        public static class ProjectileConfig {
            public float defaultRadius = 12.0f; // Blocks for projectile impact
            public float multiplier = 1.0f;
        }

        public static class ExplosionConfig {
            public float defaultRadius = 48.0f; // Blocks for explosions
            public float multiplier = 1.0f;
        }

        public static class ItemConfig {
            public float radius;
            public ItemConfig(float radius) {
                this.radius = radius;
            }
        }

        public static class BlockConfig {
            public float radius;
            public BlockConfig(float radius) {
                this.radius = radius;
            }
        }
    }
}