package com.niuhi.mixin;

import com.niuhi.config.ConfigLoader;
import com.niuhi.detection.StealthDetection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class StealthDetectionMixin {
    @Inject(method = "setTarget(Lnet/minecraft/entity/LivingEntity;)V", at = @At("HEAD"), cancellable = true)
    private void applyStealthDetection(LivingEntity target, CallbackInfo ci) {
        MobEntity mob = (MobEntity) (Object) this;
        if (!(mob instanceof HostileEntity hostileMob) || !(target instanceof PlayerEntity player)) {
            return; // Only apply to hostile mobs targeting players
        }

        // Skip stealth check if player is within proximity radius
        var config = ConfigLoader.getConfig().stealthDetection;
        double distance = mob.getPos().distanceTo(target.getPos());
        if (distance <= config.proximityRadius) {
            return; // Allow targeting
        }

        // Apply stealth detection
        if (!StealthDetection.canDetectPlayer(player, hostileMob, ConfigLoader.getConfig())) {
            ci.cancel(); // Prevent setting target if stealth check fails
        }
    }
}