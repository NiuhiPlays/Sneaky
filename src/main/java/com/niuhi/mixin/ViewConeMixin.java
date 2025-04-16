package com.niuhi.mixin;

import com.niuhi.config.ConfigLoader;
import com.niuhi.detection.ViewCone;
import net.minecraft.block.CropBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class ViewConeMixin {
    @Inject(method = "setTarget(Lnet/minecraft/entity/LivingEntity;)V", at = @At("HEAD"), cancellable = true)
    private void checkViewCone(LivingEntity target, CallbackInfo ci) {
        MobEntity mob = (MobEntity) (Object) this;
        if (!(mob instanceof HostileEntity hostileMob) || target == null) {
            return; // Only apply to hostile mobs and valid targets
        }

        // Hiding check: Sneaking in tall plants blocks detection
        var config = ConfigLoader.getConfig().stealthDetection;
        if (config.hideInTallPlants && target instanceof PlayerEntity player && player.isSneaking()) {
            var state = mob.getWorld().getBlockState(player.getBlockPos());
            var block = state.getBlock();
            if (block instanceof TallPlantBlock || block instanceof CropBlock) {
                ci.cancel(); // Block detection, including proximity
                return;
            }
        }

        // Proximity check: Detect if target is too close
        double distance = mob.getPos().distanceTo(target.getPos());
        if (distance <= config.proximityRadius) {
            return; // Allow targeting, skip view cone check
        }

        // Normal view cone check
        if (!ViewCone.isInViewCone(hostileMob, target, ConfigLoader.getConfig())) {
            ci.cancel(); // Prevent setting target if outside view cone
        }
    }
}