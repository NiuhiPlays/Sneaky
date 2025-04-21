package com.niuhi.mixin;

import com.niuhi.config.ConfigLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public class PlayerFallingMixin {
    @ModifyVariable(method = "handleFallDamage(FFLnet/minecraft/entity/damage/DamageSource;)Z", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyFallDamage(float damage, float fallDistance, float damageMultiplier) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient) return damage;

        // Get the block the player landed on (block below their position)
        BlockPos landingPos = player.getBlockPos().down();
        var blockState = player.getWorld().getBlockState(landingPos);
        var block = blockState.getBlock();
        String blockId = Registries.BLOCK.getId(block).toString();

        // Check block tags for fall damage softening
        float fallDamageMultiplier = 1.0f;
        var config = ConfigLoader.getConfig().soundDetection;
        if (config.fallDamageSofteningUseBlockTags) {
            for (var entry : config.fallDamageSofteningTagConfigs.entrySet()) {
                String tagId = entry.getKey();
                float multiplier = entry.getValue().radius;
                // Add tag checks here if tags are configured
                // Example: if (tagId.equals("minecraft:some_tag") && blockState.isIn(BlockTags.SOME_TAG))
            }
        }

        // Check individual blocks for fall damage softening (only if no tag match)
        if (fallDamageMultiplier == 1.0f && config.fallDamageSofteningBlocks.containsKey(blockId)) {
            fallDamageMultiplier = config.fallDamageSofteningBlocks.get(blockId).radius;
        }

        return damage * fallDamageMultiplier;
    }
}