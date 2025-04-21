package com.niuhi.detection;

import com.niuhi.SneakyMod;
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
        int minSize = 2;
        int maxSize = 5;
        for (int size = minSize; size <= maxSize; size += 2) {
            boolean allMatch = true;
            int halfSize = size / 2;
            for (int x = -halfSize; x <= halfSize; x++) {
                for (int z = -halfSize; z <= halfSize; z++) {
                    BlockPos checkPos = center.add(x, 0, z);
                    var state = world.getBlockState(checkPos);
                    boolean matches = false;

                    if (tagId != null) {
                        matches = switch (tagId) {
                            case "minecraft:wool" -> state.isIn(BlockTags.WOOL);
                            case "minecraft:carpets" -> state.isIn(BlockTags.WOOL_CARPETS);
                            case "minecraft:leaves" -> state.isIn(BlockTags.LEAVES);
                            default -> false;
                        };
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
            if (allMatch) return true;
        }
        return false;
    }

    public static void handleSoundEvent(World world, BlockPos pos, float soundRadius, Config config, String soundCategory) {
        if (world.isClient) return;

        SneakyMod.LOGGER.debug("Attempting to handle sound event: category={}, pos={}, initialRadius={}", soundCategory, pos, soundRadius);

        long currentTick = world.getTime();
        float cooldownTicks = config.soundDetection.soundCooldownSeconds * 20.0f;

        // Sound softening
        float soundSofteningMultiplier = 1.0f;
        var blockState = world.getBlockState(pos.down());
        String blockId = Registries.BLOCK.getId(blockState.getBlock()).toString();

        if (config.soundDetection.soundSofteningUseBlockTags) {
            for (var entry : config.soundDetection.soundSofteningTagConfigs.entrySet()) {
                if (isValidBlockArea(world, pos.down(), null, entry.getKey())) {
                    soundSofteningMultiplier = entry.getValue().radius;
                    SneakyMod.LOGGER.debug("Applied softening multiplier {} for tag {}", soundSofteningMultiplier, entry.getKey());
                    break;
                }
            }
        }

        if (soundSofteningMultiplier == 1.0f && config.soundDetection.soundSofteningBlocks.containsKey(blockId)) {
            if (isValidBlockArea(world, pos.down(), blockId, null)) {
                soundSofteningMultiplier = config.soundDetection.soundSofteningBlocks.get(blockId).radius;
                SneakyMod.LOGGER.debug("Applied softening multiplier {} for block {}", soundSofteningMultiplier, blockId);
            }
        }

        // Ambient sound influence (category-specific)
        float adjustedRadius = soundRadius * soundSofteningMultiplier;
        boolean isAmbientAffected = switch (soundCategory) {
            case "movement", "interaction", "falling_block", "projectile" -> true;
            case "explosion", "use" -> false; // Explosions and firework rockets unaffected
            default -> true;
        };

        if (isAmbientAffected) {
            boolean hasAmbientSound = false;
            int maxChecks = 343; // 7x7x7 cube
            int checks = 0;
            for (int x = -3; x <= 3; x++) {
                for (int y = -3; y <= 3; y++) {
                    for (int z = -3; z <= 3; z++) {
                        if (++checks > maxChecks) {
                            SneakyMod.LOGGER.warn("Ambient sound check limit reached at {}", pos);
                            break;
                        }
                        BlockPos checkPos = pos.add(x, y, z);
                        var state = world.getBlockState(checkPos);
                        var block = state.getBlock();

                        if (block == Blocks.WATER || block == Blocks.LAVA) {
                            FluidState fluidState = world.getFluidState(checkPos);
                            if (!fluidState.isStill()) hasAmbientSound = true;
                        } else if (block == Blocks.NETHER_PORTAL || (block instanceof JukeboxBlock && state.get(JukeboxBlock.HAS_RECORD))) {
                            hasAmbientSound = true;
                        }

                        if (hasAmbientSound) break;
                    }
                    if (hasAmbientSound || checks > maxChecks) break;
                }
                if (hasAmbientSound || checks > maxChecks) break;
            }
            if (hasAmbientSound) {
                adjustedRadius *= config.soundDetection.ambientSoundMultiplier;
                SneakyMod.LOGGER.debug("Ambient sound detected near {}, reducing radius by {}", pos, config.soundDetection.ambientSoundMultiplier);
            }
        }

        // Apply category multiplier
        float finalRadius = adjustedRadius * switch (soundCategory) {
            case "movement" -> config.soundDetection.movement.multiplier;
            case "use" -> config.soundDetection.use.multiplier;
            case "interaction" -> config.soundDetection.interaction.multiplier;
            case "falling_block" -> config.soundDetection.fallingBlock.multiplier;
            case "projectile" -> config.soundDetection.projectile.multiplier;
            case "explosion" -> config.soundDetection.explosion.multiplier;
            default -> 1.0f;
        };

        SneakyMod.LOGGER.debug("Final sound event: category={}, pos={}, initialRadius={}, softeningMultiplier={}, adjustedRadius={}, finalRadius={}",
                soundCategory, pos, soundRadius, soundSofteningMultiplier, adjustedRadius, finalRadius);

        // Notify mobs
        if (finalRadius <= 0) {
            SneakyMod.LOGGER.debug("Skipping mob notification: finalRadius is {}", finalRadius);
            return;
        }

        Vec3d center = Vec3d.ofCenter(pos);
        Box box = new Box(center.subtract(finalRadius, finalRadius, finalRadius),
                center.add(finalRadius, finalRadius, finalRadius));
        List<HostileEntity> mobs = world.getEntitiesByClass(HostileEntity.class, box, mob -> true);

        SneakyMod.LOGGER.debug("Found {} mobs within radius {} at {}", mobs.size(), finalRadius, pos);
        for (HostileEntity mob : mobs) {
            double distance = mob.getPos().distanceTo(center);
            if (distance <= finalRadius) {
                UUID mobId = mob.getUuid();
                if (!mobSoundCooldowns.containsKey(mobId) || (currentTick - mobSoundCooldowns.get(mobId)) >= cooldownTicks) {
                    mobSoundCooldowns.put(mobId, currentTick);
                    SneakyMod.LOGGER.debug("Notifying mob {} at {} for sound at {}", mobId, mob.getPos(), pos);
                    mob.getNavigation().startMovingTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 1.0);
                } else {
                    SneakyMod.LOGGER.debug("Mob {} on cooldown, skipping notification", mobId);
                }
            } else {
                SneakyMod.LOGGER.debug("Mob {} at {} too far (distance={}) from sound at {}", mob, mob.getPos(), distance, pos);
            }
        }
    }
}