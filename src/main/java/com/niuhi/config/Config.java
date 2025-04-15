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
        public float proximityRadius = 1.0f; // Blocks to always detect player
        public boolean hideInTallPlants = true; // Hides player when sneaking in tall plants
    }

    public static class StealthDetectionConfig {
        public float sneakMultiplier = 0.5f; // Detection chance multiplier when sneaking
        public float lightLevelMaxChance = 1.0f; // Chance at light level 15
        public float lightLevelMinChance = 0.0f; // Chance at light level 0
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

        public static class MovementConfig {
            public float defaultRadius = 6.0f; // Blocks for footsteps
            public float multiplier = 1.0f;
        }

        public static class UseConfig {
            public float multiplier = 1.0f;
            public Map<String, ItemConfig> items = new HashMap<>();
            public UseConfig() {
                items.put("minecraft:ender_pearl", new ItemConfig(6.0f));
                items.put("minecraft:flint_and_steel", new ItemConfig(6.0f));
                items.put("minecraft:goat_horn", new ItemConfig(6.0f));
                items.put("minecraft:fire_charge", new ItemConfig(6.0f));
            }
        }

        public static class InteractionConfig {
            public float multiplier = 1.0f;
            public boolean useBlockTags = true; // Enable tag-based detection
            public Map<String, BlockConfig> tagConfigs = new HashMap<>();
            public Map<String, BlockConfig> blocks = new HashMap<>();
            public InteractionConfig() {
                tagConfigs.put("minecraft:doors", new BlockConfig(8.0f));
                tagConfigs.put("minecraft:trapdoors", new BlockConfig(8.0f));
                tagConfigs.put("minecraft:fence_gates", new BlockConfig(8.0f));
                tagConfigs.put("minecraft:buttons", new BlockConfig(4.0f));
                tagConfigs.put("minecraft:pressure_plates", new BlockConfig(6.0f));
                blocks.put("minecraft:bell", new BlockConfig(24.0f));
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
                tagConfigs.put("minecraft:concrete_powder", new BlockConfig(10.0f));
                fallingBlocks.put("minecraft:anvil", new BlockConfig(10.0f));
                fallingBlocks.put("minecraft:pointed_dripstone", new BlockConfig(10.0f));
                fallingBlocks.put("minecraft:gravel", new BlockConfig(10.0f));
                fallingBlocks.put("minecraft:sand", new BlockConfig(10.0f));
            }
        }

        public static class ProjectileConfig {
            public float defaultRadius = 12.0f; // Blocks for projectile impact
            public float multiplier = 1.0f;
        }

        public static class ExplosionConfig {
            public float defaultRadius = 32.0f; // Blocks for explosions
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