package com.niuhi.mixin;

import com.niuhi.config.ConfigLoader;
import com.niuhi.detection.SoundDetection;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
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
public class SoundDetectionPlayerMixin {
    private static final Map<UUID, Long> playerSoundCooldowns = new HashMap<>();
    private static final float SOUND_COOLDOWN_SECONDS = 0.5f; // 0.5 seconds between sound events

    static {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;

            BlockPos pos = hitResult.getBlockPos();
            var state = world.getBlockState(pos);
            var config = ConfigLoader.getConfig().soundDetection;
            Block block = state.getBlock();
            String blockId = Registries.BLOCK.getId(block).toString();

            if (config.interaction.useBlockTags) {
                for (var entry : config.interaction.tagConfigs.entrySet()) {
                    String tagId = entry.getKey();
                    float radius = entry.getValue().radius;
                    if (tagId.equals("minecraft:doors") && state.isIn(BlockTags.DOORS)) {
                        SoundDetection.handleSoundEvent(world, pos, radius, ConfigLoader.getConfig());
                        return ActionResult.PASS;
                    } else if (tagId.equals("minecraft:trapdoors") && state.isIn(BlockTags.TRAPDOORS)) {
                        SoundDetection.handleSoundEvent(world, pos, radius, ConfigLoader.getConfig());
                        return ActionResult.PASS;
                    } else if (tagId.equals("minecraft:fence_gates") && state.isIn(BlockTags.FENCE_GATES)) {
                        SoundDetection.handleSoundEvent(world, pos, radius, ConfigLoader.getConfig());
                        return ActionResult.PASS;
                    } else if (tagId.equals("minecraft:buttons") && state.isIn(BlockTags.BUTTONS)) {
                        SoundDetection.handleSoundEvent(world, pos, radius, ConfigLoader.getConfig());
                        return ActionResult.PASS;
                    } else if (tagId.equals("minecraft:pressure_plates") && state.isIn(BlockTags.PRESSURE_PLATES)) {
                        SoundDetection.handleSoundEvent(world, pos, radius, ConfigLoader.getConfig());
                        return ActionResult.PASS;
                    }
                }
            }

            if (config.interaction.blocks.containsKey(blockId)) {
                SoundDetection.handleSoundEvent(world, pos, config.interaction.blocks.get(blockId).radius, ConfigLoader.getConfig());
            }

            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClient) return ActionResult.PASS;

            var stack = player.getStackInHand(hand);
            var config = ConfigLoader.getConfig().soundDetection;
            BlockPos pos = player.getBlockPos();
            String itemId = Registries.ITEM.getId(stack.getItem()).toString();

            if (config.use.items.containsKey(itemId)) {
                SoundDetection.handleSoundEvent(world, pos, config.use.items.get(itemId).radius, ConfigLoader.getConfig());
            }

            return ActionResult.PASS;
        });
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onMove(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        World world = player.getWorld();
        if (world.isClient) return;

        long currentTick = world.getTime();
        UUID playerId = player.getUuid();
        float cooldownTicks = SOUND_COOLDOWN_SECONDS * 20.0f;

        // Check cooldown to prevent spamming sound events
        if (playerSoundCooldowns.containsKey(playerId) && (currentTick - playerSoundCooldowns.get(playerId)) < cooldownTicks) {
            return;
        }

        var config = ConfigLoader.getConfig().soundDetection;
        BlockPos pos = player.getBlockPos();

        // Check if player is moving (walking or sprinting)
        if (!player.isSneaking() && player.getVelocity().horizontalLengthSquared() > 0.0001) {
            float radius = player.isSprinting() ? config.movement.sprintRadius : config.movement.walkRadius;
            SoundDetection.handleSoundEvent(world, pos, radius, ConfigLoader.getConfig());
            playerSoundCooldowns.put(playerId, currentTick);
        }

        // Check for jumping (vertical velocity indicates a jump)
        if (!player.isSneaking() && player.getVelocity().y > 0.3 && !player.isOnGround() && !player.isClimbing() && !player.isInLava() && !player.isTouchingWater()) {
            SoundDetection.handleSoundEvent(world, pos, config.movement.jumpRadius, ConfigLoader.getConfig());
            playerSoundCooldowns.put(playerId, currentTick);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void dummyTick(CallbackInfo ci) {
        // No-op
    }
}