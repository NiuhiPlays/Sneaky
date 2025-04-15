package com.niuhi.mixin;

import com.niuhi.config.ConfigLoader;
import com.niuhi.detection.SoundDetection;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class SoundDetectionExplosionMixin {
    @Inject(method = "createExplosion(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/world/World$ExplosionSourceType;)V", at = @At("HEAD"))
    private void onExplosion(Entity entity, double x, double y, double z, float power, World.ExplosionSourceType explosionSourceType, CallbackInfo ci) {
        World world = (World) (Object) this;
        if (world.isClient()) return;

        BlockPos pos = new BlockPos((int) x, (int) y, (int) z);
        var config = ConfigLoader.getConfig().soundDetection;
        SoundDetection.handleSoundEvent(world, pos, config.explosionRadius, ConfigLoader.getConfig());
    }
}