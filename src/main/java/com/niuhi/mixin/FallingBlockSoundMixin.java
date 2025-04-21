package com.niuhi.mixin;

import com.niuhi.config.ConfigLoader;
import com.niuhi.detection.SoundDetection;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public class FallingBlockSoundMixin {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;discard()V"))
    private void onBlockLand(CallbackInfo ci) {
        FallingBlockEntity entity = (FallingBlockEntity) (Object) this;
        World world = entity.getWorld();
        if (world.isClient) return;

        var block = entity.getBlockState().getBlock();
        String blockId = Registries.BLOCK.getId(block).toString();
        var config = ConfigLoader.getConfig().soundDetection;
        float radius = 0.0f;

        if (config.fallingBlock.useBlockTags) {
            for (var entry : config.fallingBlock.tagConfigs.entrySet()) {
                String tagId = entry.getKey();
                if (tagId.equals("minecraft:concrete_powder") && entity.getBlockState().isIn(net.minecraft.registry.tag.BlockTags.CONCRETE_POWDER)) {
                    radius = entry.getValue().radius;
                    break;
                }
            }
        }

        if (radius == 0.0f && config.fallingBlock.fallingBlocks.containsKey(blockId)) {
            radius = config.fallingBlock.fallingBlocks.get(blockId).radius;
        }

        if (radius > 0) {
            BlockPos pos = entity.getBlockPos();
            SoundDetection.handleSoundEvent(world, pos, radius, ConfigLoader.getConfig(), "falling_block");
        }
    }
}