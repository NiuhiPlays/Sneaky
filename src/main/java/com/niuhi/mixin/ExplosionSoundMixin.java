package com.niuhi.mixin;

import com.niuhi.SneakyMod;
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
public class ExplosionSoundMixin {
    @Inject(method = "createExplosion(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/world/World$ExplosionSourceType;)V", at = @At("HEAD"))
    private void onExplosion(Entity entity, double x, double y, double z, float power, World.ExplosionSourceType explosionSourceType, CallbackInfo ci) {
        World world = (World) (Object) this;
        if (world.isClient || power <= 0.0f) {
            SneakyMod.LOGGER.debug("Skipping explosion sound event: isClient={}, power={}", world.isClient, power);
            return;
        }

        BlockPos pos = new BlockPos((int) x, (int) y, (int) z);
        var config = ConfigLoader.getConfig().soundDetection;
        float radius = config.explosion.defaultRadius;

        SneakyMod.LOGGER.debug("Explosion detected at {} with power {} and radius {}", pos, power, radius);
        SoundDetection.handleSoundEvent(world, pos, radius, ConfigLoader.getConfig(), "explosion");
    }
}