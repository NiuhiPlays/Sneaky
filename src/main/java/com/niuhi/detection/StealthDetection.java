package com.niuhi.detection;

import com.niuhi.config.Config;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class StealthDetection {
    public static boolean canDetectPlayer(PlayerEntity player, HostileEntity mob, Config config) {
        float chance = calculateDetectionChance(player, mob, config);
        return mob.getRandom().nextFloat() < chance;
    }

    public static float calculateDetectionChance(PlayerEntity player, HostileEntity mob, Config config) {
        boolean isSneaking = player.isSneaking();
        World world = mob.getWorld();
        BlockPos pos = player.getBlockPos();

        // Get combined light level (block + sky, capped at 15)
        int blockLight = world.getLightLevel(LightType.BLOCK, pos);
        int skyLight = world.getLightLevel(LightType.SKY, pos);
        int combinedLight = Math.min(blockLight + skyLight, 15);

        // Linearly interpolate detection chance based on combined light level
        float lightFactor = (float) combinedLight / 15.0f;
        float chance = config.stealthDetection.lightLevelMinChance +
                lightFactor * (config.stealthDetection.lightLevelMaxChance - config.stealthDetection.lightLevelMinChance);

        // Apply sneaking multiplier
        if (isSneaking) {
            chance *= config.stealthDetection.sneakMultiplier;
        }

        return chance;
    }
}