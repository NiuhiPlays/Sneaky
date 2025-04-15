package com.niuhi.mixin;

import com.niuhi.config.ConfigLoader;
import com.niuhi.detection.SoundDetection;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public class SoundDetectionFallingBlockMixin {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;discard()V"))
    private void onAnvilLand(CallbackInfo ci) {
        FallingBlockEntity entity = (FallingBlockEntity) (Object) this;
        World world = entity.getWorld();
        if (world.isClient || !(entity.getBlockState().getBlock() instanceof AnvilBlock)) return;

        BlockPos pos = entity.getBlockPos();
        var config = ConfigLoader.getConfig().soundDetection;
        SoundDetection.handleSoundEvent(world, pos, config.blockFallRadius, ConfigLoader.getConfig());
    }
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;discard()V"))
    private void onDripstoneLand(CallbackInfo ci) {
        FallingBlockEntity entity = (FallingBlockEntity) (Object) this;
        World world = entity.getWorld();
        if (world.isClient || !(entity.getBlockState().getBlock() instanceof PointedDripstoneBlock)) return;

        BlockPos pos = entity.getBlockPos();
        var config = ConfigLoader.getConfig().soundDetection;
            SoundDetection.handleSoundEvent(world, pos, config.blockFallRadius, ConfigLoader.getConfig());
        }
    }