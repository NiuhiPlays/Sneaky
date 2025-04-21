package com.niuhi.detection;

import com.niuhi.config.Config;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
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

    public static boolean isValidBlockArea(World world, BlockPos center, String blockId, String tagId) {
        // Check 2x2 to 5x5 area centered on the block
        int minSize = 2;
        int maxSize = 5;
        boolean hasMinArea = false;

        for (int size = minSize; size <= maxSize; size += 2) { // Check 2x2, 3x3, 4x4, 5x5
            boolean allMatch = true;
            int halfSize = size / 2;
            for (int x = -halfSize; x <= halfSize; x++) {
                for (int z = -halfSize; z <= halfSize; z++) {
                    BlockPos checkPos = center.add(x, 0, z);
                    var state = world.getBlockState(checkPos);
                    boolean matches = false;

                    if (tagId != null) {
                        if (tagId.equals("minecraft:wool") && state.isIn(BlockTags.WOOL)) {
                            matches = true;
                        } else if (tagId.equals("minecraft:carpets") && state.isIn(BlockTags.WOOL_CARPETS)) {
                            matches = true;
                        } else if (tagId.equals("minecraft:leaves") && state.isIn(BlockTags.LEAVES)) {
                            matches = true;
                        }
                    } else {
                        String checkBlockId = Registries.BLOCK.getId(state.getBlock()).toString();
                        matches = checkBlockId.equals(blockId);
                    }

                    if (!matches) {
                        allMatch = false;
                        break;
                    }
                }
                if (!allMatch) break;
            }
            if (allMatch) {
                hasMinArea = true;
                break;
            }
        }
        return hasMinArea;
    }

    public static void handleSoundEvent(World world, BlockPos pos, float soundRadius, Config config) {
        long currentTick = world.getTime();
        float cooldownTicks = config.soundDetection.soundCooldownSeconds * 20.0f;

        // Check for sound-softening blocks
        float soundSofteningMultiplier = 1.0f;
        var blockState = world.getBlockState(pos.down());
        var block = blockState.getBlock();
        String blockId = Registries.BLOCK.getId(block).toString();

        // Check block tags for sound softening
        if (config.soundDetection.soundSofteningUseBlockTags) {
            for (var entry : config.soundDetection.soundSofteningTagConfigs.entrySet()) {
                String tagId = entry.getKey();
                float multiplier = entry.getValue().radius;
                if ((tagId.equals("minecraft:wool") && blockState.isIn(BlockTags.WOOL)) ||
                        (tagId.equals("minecraft:carpets") && blockState.isIn(BlockTags.WOOL_CARPETS)) ||
                        (tagId.equals("minecraft:leaves") && blockState.isIn(BlockTags.LEAVES))) {
                    if (isValidBlockArea(world, pos.down(), null, tagId)) {
                        soundSofteningMultiplier = multiplier;
                        break;
                    }
                }
            }
        }

        // Check individual blocks for sound softening (only if no tag match)
        if (soundSofteningMultiplier == 1.0f && config.soundDetection.soundSofteningBlocks.containsKey(blockId)) {
            if (isValidBlockArea(world, pos.down(), blockId, null)) {
                soundSofteningMultiplier = config.soundDetection.soundSofteningBlocks.get(blockId).radius;
            }
        }

        // Check for ambient sound sources
        float adjustedRadius = soundRadius * soundSofteningMultiplier;
        boolean isAmbientAffected = soundRadius == config.soundDetection.movement.walkRadius ||
                soundRadius == config.soundDetection.movement.sprintRadius ||
                soundRadius == config.soundDetection.movement.jumpRadius ||
                config.soundDetection.use.items.values().stream().anyMatch(item -> item.radius == soundRadius) ||
                config.soundDetection.interaction.blocks.values().stream().anyMatch(blockConfig -> blockConfig.radius == soundRadius);
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
                        var checkBlock = state.getBlock();

                        if (checkBlock == Blocks.WATER || checkBlock == Blocks.LAVA) {
                            FluidState fluidState = world.getFluidState(checkPos);
                            if (fluidState.isStill()) continue;
                            hasAmbientSound = true;
                        } else if (checkBlock == Blocks.NETHER_PORTAL) {
                            hasAmbientSound = true;
                        } else if (checkBlock instanceof JukeboxBlock && state.get(JukeboxBlock.HAS_RECORD)) {
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
        if (soundRadius == config.soundDetection.movement.walkRadius ||
                soundRadius == config.soundDetection.movement.sprintRadius ||
                soundRadius == config.soundDetection.movement.jumpRadius) {
            finalRadius *= config.soundDetection.movement.multiplier;
        } else if (config.soundDetection.use.items.values().stream().anyMatch(item -> item.radius == soundRadius)) {
            finalRadius *= config.soundDetection.use.multiplier;
        } else if (config.soundDetection.interaction.blocks.values().stream().anyMatch(blockConfig -> blockConfig.radius == soundRadius) ||
                (config.soundDetection.interaction.useBlockTags &&
                        config.soundDetection.interaction.tagConfigs.values().stream().anyMatch(tag -> tag.radius == soundRadius))) {
            finalRadius *= config.soundDetection.interaction.multiplier;
        } else if (config.soundDetection.fallingBlock.fallingBlocks.values().stream().anyMatch(blockConfig -> blockConfig.radius == soundRadius) ||
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