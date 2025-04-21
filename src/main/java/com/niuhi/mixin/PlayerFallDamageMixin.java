package com.niuhi.mixin;

import com.niuhi.config.ConfigLoader;
import com.niuhi.detection.SoundDetection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public class PlayerFallDamageMixin {
    @ModifyVariable(method = "handleFallDamage(FFLnet/minecraft/entity/damage/DamageSource;)Z", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyFallDamage(float damage, float fallDistance, float damageMultiplier) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient) return damage;

        BlockPos landingPos = player.getBlockPos().down();
        var blockState = player.getWorld().getBlockState(landingPos);
        var block = blockState.getBlock();
        String blockId = Registries.BLOCK.getId(block).toString();
        var config = ConfigLoader.getConfig().soundDetection;
        float fallDamageMultiplier = 1.0f;

        if (config.fallDamageSofteningUseBlockTags) {
            for (var entry : config.fallDamageSofteningTagConfigs.entrySet()) {
                if (SoundDetection.isValidBlockArea(player.getWorld(), landingPos, null, entry.getKey())) {
                    fallDamageMultiplier = entry.getValue().radius;
                    break;
                }
            }
        }

        if (fallDamageMultiplier == 1.0f && config.fallDamageSofteningBlocks.containsKey(blockId)) {
            if (SoundDetection.isValidBlockArea(player.getWorld(), landingPos, blockId, null)) {
                fallDamageMultiplier = config.fallDamageSofteningBlocks.get(blockId).radius;
            }
        }

        return damage * fallDamageMultiplier;
    }
}