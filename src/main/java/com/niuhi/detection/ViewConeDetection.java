package com.niuhi.detection;

import com.niuhi.config.Config;
import net.minecraft.block.CropBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class ViewConeDetection {
    public static boolean isInViewCone(HostileEntity mob, LivingEntity target, Config config) {
        // Proximity check
        double distance = mob.getPos().distanceTo(target.getPos());
        if (distance <= config.stealthDetection.proximityRadius) {
            return true;
        }

        // Hiding in tall plants
        if (config.stealthDetection.hideInTallPlants && target instanceof PlayerEntity player && player.isSneaking()) {
            var state = mob.getWorld().getBlockState(player.getBlockPos());
            var block = state.getBlock();
            if (block instanceof TallPlantBlock || block instanceof CropBlock) {
                return false;
            }
        }

        // View cone check
        Vec3d mobPos = mob.getEyePos();
        Vec3d targetPos = target.getEyePos();
        if (mobPos.distanceTo(targetPos) > config.viewCone.maxDistance) {
            return false;
        }

        float yaw = mob.getYaw();
        Vec3d toTarget = targetPos.subtract(mobPos).normalize();
        double yawRad = Math.toRadians(yaw);
        Vec3d facing = new Vec3d(-Math.sin(yawRad), 0, Math.cos(yawRad)).normalize();
        double dot = facing.dotProduct(toTarget);
        double angle = Math.acos(dot) * 180 / Math.PI;

        if (angle > config.viewCone.coneAngle / 2.0) {
            return false;
        }

        // Line of sight check
        World world = mob.getWorld();
        RaycastContext context = new RaycastContext(
                mobPos, targetPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mob
        );
        BlockHitResult hitResult = world.raycast(context);
        boolean hasObstruction = hitResult.getType() == BlockHitResult.Type.BLOCK;

        if (!hasObstruction && target instanceof PlayerEntity player) {
            float chance = StealthDetection.calculateDetectionChance(player, mob, config);
            chance *= config.stealthDetection.clearViewChanceMultiplier;
            return mob.getRandom().nextFloat() < Math.min(chance, 1.0f);
        }

        return !hasObstruction;
    }
}