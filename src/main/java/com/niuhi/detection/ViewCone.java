package com.niuhi.detection;

import com.niuhi.config.Config;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ViewCone {
    public static boolean isInViewCone(HostileEntity mob, LivingEntity target, Config config) {
        // Check if player is hiding in tall plants (sneaking in tall grass, sugarcane, crops)
        if (config.viewCone.hideInTallPlants && target instanceof PlayerEntity player && player.isSneaking()) {
            BlockPos targetPos = player.getBlockPos();
            BlockState state = mob.getWorld().getBlockState(targetPos);
            Block block = state.getBlock();
            if (block instanceof TallPlantBlock) {
                return false; // Player is hidden, block view cone detection
            }
        }

        Vec3d mobPos = mob.getEyePos();
        Vec3d targetPos = target.getEyePos();
        double distance = mobPos.distanceTo(targetPos);

        // Check max distance
        if (distance > config.viewCone.maxDistance) {
            return false;
        }

        // Calculate angle
        float yaw = mob.getYaw();
        Vec3d toTarget = targetPos.subtract(mobPos).normalize();
        double yawRad = Math.toRadians(yaw);
        Vec3d facing = new Vec3d(-Math.sin(yawRad), 0, Math.cos(yawRad)).normalize();

        double dot = facing.dotProduct(toTarget);
        double angle = Math.acos(dot) * 180 / Math.PI;

        // Check if within cone
        return angle <= (config.viewCone.coneAngle / 2.0);
    }
}