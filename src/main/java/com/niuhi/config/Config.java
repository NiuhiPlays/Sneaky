package com.niuhi.config;

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
        public float projectileImpactRadius = 12.0f; // Blocks for projectile impact
        public float bellRingRadius = 24.0f; // Blocks for bell sounds
        public float doorInteractRadius = 8.0f; // Blocks for door use
        public float useSoundRadius = 6.0f; // Blocks for chest, anvil, ender pearl, flint and steel
        public float movementSoundRadius = 6.0f; // Blocks for footsteps (not sneaking)
        public float explosionRadius = 32.0f; // Blocks for explosions
        public float blockFallRadius = 10.0f; // Blocks for anvil landing
        public float soundCooldownSeconds = 2.0f; // Seconds before mob can react again
    }
}