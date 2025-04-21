package com.niuhi.mixin;

import com.niuhi.config.ConfigLoader;
import com.niuhi.detection.StealthDetection;
import com.niuhi.detection.ViewConeDetection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class MobTargetMixin {
    @Inject(method = "setTarget(Lnet/minecraft/entity/LivingEntity;)V", at = @At("HEAD"), cancellable = true)
    private void checkDetection(LivingEntity target, CallbackInfo ci) {
        MobEntity mob = (MobEntity) (Object) this;
        if (!(mob instanceof HostileEntity hostileMob) || target == null) {
            return;
        }

        if (target instanceof PlayerEntity player) {
            if (!ViewConeDetection.isInViewCone(hostileMob, player, ConfigLoader.getConfig()) ||
                    !StealthDetection.canDetectPlayer(player, hostileMob, ConfigLoader.getConfig())) {
                ci.cancel();
            }
        } else {
            if (!ViewConeDetection.isInViewCone(hostileMob, target, ConfigLoader.getConfig())) {
                ci.cancel();
            }
        }
    }
}