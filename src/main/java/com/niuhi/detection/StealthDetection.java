package com.niuhi.detection;

import com.niuhi.config.Config;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

public class StealthDetection {
    public static boolean canDetectPlayer(PlayerEntity player, HostileEntity mob, Config config) {
        double distance = mob.getPos().distanceTo(player.getPos());
        if (distance <= config.stealthDetection.proximityRadius) {
            return true;
        }

        float chance = calculateDetectionChance(player, mob, config);
        return mob.getRandom().nextFloat() < chance;
    }

    public static float calculateDetectionChance(PlayerEntity player, HostileEntity mob, Config config) {
        var world = mob.getWorld();
        BlockPos pos = player.getBlockPos();
        int blockLight = world.getLightLevel(LightType.BLOCK, pos);
        int skyLight = world.getLightLevel(LightType.SKY, pos);
        int combinedLight = Math.min(blockLight + skyLight, 15);

        float lightFactor = (float) combinedLight / 15.0f;
        float chance = config.stealthDetection.lightLevelMinChance +
                lightFactor * (config.stealthDetection.lightLevelMaxChance - config.stealthDetection.lightLevelMinChance);

        if (player.isSneaking()) {
            chance *= config.stealthDetection.sneakMultiplier;
        }

        return chance;
    }
}