package com.niuhi.mixin;

import com.niuhi.config.ConfigLoader;
import com.niuhi.detection.SoundDetection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(PlayerEntity.class)
public class PlayerSoundMixin {
    private static final Map<UUID, Long> playerSoundCooldowns = new HashMap<>();
    private static final float SOUND_COOLDOWN_SECONDS = 0.5f;

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onMove(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        World world = player.getWorld();
        if (world.isClient) return;

        long currentTick = world.getTime();
        UUID playerId = player.getUuid();
        float cooldownTicks = SOUND_COOLDOWN_SECONDS * 20.0f;

        if (playerSoundCooldowns.containsKey(playerId) && (currentTick - playerSoundCooldowns.get(playerId)) < cooldownTicks) {
            return;
        }

        var config = ConfigLoader.getConfig().soundDetection;
        var pos = player.getBlockPos();
        float radius = 0.0f;

        // Handle movement sounds
        if (player.isSneaking()) {
            radius = config.movement.sneakRadius;
        } else if (player.isSprinting() && player.getVelocity().horizontalLengthSquared() > 0.0001) {
            radius = config.movement.sprintRadius;
        } else if (player.getVelocity().horizontalLengthSquared() > 0.0001) {
            radius = config.movement.walkRadius;
        }

        // Handle jump landing (check for sound-softening blocks)
        if (!player.isOnGround() && player.getVelocity().y < -0.1 && player.prevY > player.getY()) {
            // Player is falling, check landing block on next tick
            BlockPos landingPos = player.getBlockPos().down();
            var blockState = world.getBlockState(landingPos);
            String blockId = net.minecraft.registry.Registries.BLOCK.getId(blockState.getBlock()).toString();
            float softeningMultiplier = 1.0f;

            if (config.soundSofteningUseBlockTags) {
                for (var entry : config.soundSofteningTagConfigs.entrySet()) {
                    if (SoundDetection.isValidBlockArea(world, landingPos, null, entry.getKey())) {
                        softeningMultiplier = entry.getValue().radius;
                        break;
                    }
                }
            }

            if (softeningMultiplier == 1.0f && config.soundSofteningBlocks.containsKey(blockId)) {
                if (SoundDetection.isValidBlockArea(world, landingPos, blockId, null)) {
                    softeningMultiplier = config.soundSofteningBlocks.get(blockId).radius;
                }
            }

            radius = config.movement.jumpRadius * softeningMultiplier;
        }

        if (radius > 0) {
            SoundDetection.handleSoundEvent(world, pos, radius, ConfigLoader.getConfig(), "movement");
            playerSoundCooldowns.put(playerId, currentTick);
        }
    }
}