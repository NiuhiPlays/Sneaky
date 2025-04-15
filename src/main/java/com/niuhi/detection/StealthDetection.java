package com.niuhi.detection;

import com.niuhi.config.Config;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class StealthDetection {
    public static boolean canDetectPlayer(PlayerEntity player, HostileEntity mob, Config config) {
        boolean isSneaking = player.isSneaking();
        World world = mob.getWorld();
        int lightLevel = world.getLightLevel(player.getBlockPos());

        // Linearly interpolate detection chance based on light level
        float lightFactor = (float) lightLevel / 15.0f;
        float chance = config.stealthDetection.lightLevelMinChance +
                lightFactor * (config.stealthDetection.lightLevelMaxChance - config.stealthDetection.lightLevelMinChance);

        // Apply sneaking multiplier
        if (isSneaking) {
            chance *= config.stealthDetection.sneakMultiplier;
        }

        // Random roll
        return mob.getRandom().nextFloat() < chance;
    }
}