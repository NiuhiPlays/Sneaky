package com.niuhi.detection;

import com.niuhi.config.Config;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SoundDetection {
    private static final Map<UUID, Long> mobSoundCooldowns = new HashMap<>();

    public static void handleSoundEvent(World world, BlockPos pos, float soundRadius, Config config) {
        long currentTick = world.getTime();
        float cooldownTicks = config.soundDetection.soundCooldownSeconds * 20.0f;

        // Check for ambient sound sources
        float adjustedRadius = soundRadius;
        boolean isAmbientAffected = soundRadius == config.soundDetection.movement.defaultRadius ||
                config.soundDetection.use.items.values().stream().anyMatch(item -> item.radius == soundRadius) ||
                config.soundDetection.interaction.blocks.values().stream().anyMatch(block -> block.radius == soundRadius) ||
                config.soundDetection.fallingBlock.fallingBlocks.values().stream().anyMatch(block -> block.radius == soundRadius);
        if (!isAmbientAffected && config.soundDetection.interaction.useBlockTags) {
            for (var entry : config.soundDetection.interaction.tagConfigs.entrySet()) {
                if (entry.getValue().radius == soundRadius) {
                    isAmbientAffected = true;
                    break;
                }
            }
        }
        if (!isAmbientAffected && config.soundDetection.fallingBlock.useBlockTags) {
            for (var entry : config.soundDetection.fallingBlock.tagConfigs.entrySet()) {
                if (entry.getValue().radius == soundRadius) {
                    isAmbientAffected = true;
                    break;
                }
            }
        }
        if (isAmbientAffected) {
            boolean hasAmbientSound = false;
            for (int x = -3; x <= 3; x++) {
                for (int y = -3; y <= 3; y++) {
                    for (int z = -3; z <= 3; z++) {
                        BlockPos checkPos = pos.add(x, y, z);
                        var state = world.getBlockState(checkPos);
                        var block = state.getBlock();

                        if (block == Blocks.WATER || block == Blocks.LAVA) {
                            FluidState fluidState = world.getFluidState(checkPos);
                            if (fluidState.isStill()) continue;
                            hasAmbientSound = true;
                        } else if (block == Blocks.NETHER_PORTAL) {
                            hasAmbientSound = true;
                        } else if (block instanceof JukeboxBlock && state.get(JukeboxBlock.HAS_RECORD)) {
                            hasAmbientSound = true;
                        }

                        if (hasAmbientSound) break;
                    }
                    if (hasAmbientSound) break;
                }
                if (hasAmbientSound) break;
            }
            if (hasAmbientSound) {
                adjustedRadius *= config.soundDetection.ambientSoundMultiplier;
            }
        }

        // Apply category multiplier
        float finalRadius = adjustedRadius;
        if (soundRadius == config.soundDetection.movement.defaultRadius) {
            finalRadius *= config.soundDetection.movement.multiplier;
        } else if (config.soundDetection.use.items.values().stream().anyMatch(item -> item.radius == soundRadius)) {
            finalRadius *= config.soundDetection.use.multiplier;
        } else if (config.soundDetection.interaction.blocks.values().stream().anyMatch(block -> block.radius == soundRadius) ||
                (config.soundDetection.interaction.useBlockTags &&
                        config.soundDetection.interaction.tagConfigs.values().stream().anyMatch(tag -> tag.radius == soundRadius))) {
            finalRadius *= config.soundDetection.interaction.multiplier;
        } else if (config.soundDetection.fallingBlock.fallingBlocks.values().stream().anyMatch(block -> block.radius == soundRadius) ||
                (config.soundDetection.fallingBlock.useBlockTags &&
                        config.soundDetection.fallingBlock.tagConfigs.values().stream().anyMatch(tag -> tag.radius == soundRadius))) {
            finalRadius *= config.soundDetection.fallingBlock.multiplier;
        } else if (soundRadius == config.soundDetection.projectile.defaultRadius) {
            finalRadius *= config.soundDetection.projectile.multiplier;
        } else if (soundRadius == config.soundDetection.explosion.defaultRadius) {
            finalRadius *= config.soundDetection.explosion.multiplier;
        }

        // Use finalRadius for detection
        Vec3d center = Vec3d.ofCenter(pos);
        Box box = new Box(center.subtract(finalRadius, finalRadius, finalRadius),
                center.add(finalRadius, finalRadius, finalRadius));
        List<HostileEntity> mobs = world.getEntitiesByClass(HostileEntity.class, box, mob -> true);

        for (HostileEntity mob : mobs) {
            double distance = mob.getPos().distanceTo(center);
            if (distance <= finalRadius) {
                UUID mobId = mob.getUuid();
                if (!mobSoundCooldowns.containsKey(mobId) || (currentTick - mobSoundCooldowns.get(mobId)) >= cooldownTicks) {
                    mobSoundCooldowns.put(mobId, currentTick);
                    mob.getNavigation().startMovingTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 1.0);
                }
            }
        }
    }
}