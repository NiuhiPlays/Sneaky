package com.niuhi.mixin;

import com.niuhi.config.ConfigLoader;
import com.niuhi.detection.SoundDetection;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class SoundDetectionPlayerMixin {
    // Handle block and item interactions
    static {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;

            BlockPos pos = hitResult.getBlockPos();
            var state = world.getBlockState(pos);
            var config = ConfigLoader.getConfig().soundDetection;

            if (state.getBlock() instanceof BellBlock) {
                SoundDetection.handleSoundEvent(world, pos, config.bellRingRadius, ConfigLoader.getConfig());
            } else if (state.getBlock() instanceof DoorBlock) {
                SoundDetection.handleSoundEvent(world, pos, config.doorInteractRadius, ConfigLoader.getConfig());
            } else if (state.getBlock() instanceof ChestBlock || state.getBlock() instanceof AnvilBlock) {
                SoundDetection.handleSoundEvent(world, pos, config.useSoundRadius, ConfigLoader.getConfig());
            }

            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClient) return ActionResult.PASS;

            var stack = player.getStackInHand(hand);
            var config = ConfigLoader.getConfig().soundDetection;

            if (stack.isOf(Items.ENDER_PEARL) || stack.isOf(Items.FLINT_AND_STEEL)) {
                BlockPos pos = player.getBlockPos();
                SoundDetection.handleSoundEvent(world, pos, config.useSoundRadius, ConfigLoader.getConfig());
            }

            return ActionResult.PASS;
        });
    }

    // Handle movement sounds
    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onMove(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        World world = player.getWorld();
        if (world.isClient) return;

        var config = ConfigLoader.getConfig().soundDetection;
        BlockPos pos = player.getBlockPos();

        // Movement sounds (footsteps)
        if (!player.isSneaking() && (player.isSprinting() || player.getVelocity().horizontalLengthSquared() > 0.01)) {
            SoundDetection.handleSoundEvent(world, pos, config.movementSoundRadius, ConfigLoader.getConfig());
        }
    }

    // Dummy injection
    @Inject(method = "tick", at = @At("HEAD"))
    private void dummyTick(CallbackInfo ci) {
        // No-op
    }
}