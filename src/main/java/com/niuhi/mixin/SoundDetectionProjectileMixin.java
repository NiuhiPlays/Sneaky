package com.niuhi.mixin;

import com.niuhi.config.ConfigLoader;
import com.niuhi.detection.SoundDetection;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public class SoundDetectionProjectileMixin {
    @Inject(method = "onCollision(Lnet/minecraft/util/hit/HitResult;)V", at = @At("HEAD"))
    private void onProjectileHit(HitResult hitResult, CallbackInfo ci) {
        if (!(hitResult instanceof BlockHitResult blockHitResult)) return;

        ProjectileEntity projectile = (ProjectileEntity) (Object) this;
        World world = projectile.getWorld();
        if (world.isClient) return;

        BlockPos pos = blockHitResult.getBlockPos();
        var config = ConfigLoader.getConfig().soundDetection;
        SoundDetection.handleSoundEvent(world, pos, config.projectileImpactRadius, ConfigLoader.getConfig());
    }
}