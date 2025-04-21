package com.niuhi.mixin;

import com.niuhi.config.ConfigLoader;
import com.niuhi.detection.SoundDetection;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class RedstoneBlockMixin {
    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
            at = @At("HEAD"))
    private void onSetBlockState(BlockPos pos, BlockState newState, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        World world = (World) (Object) this;
        if (world.isClient) return;

        // Get the previous state
        BlockState oldState = world.getBlockState(pos);
        if (oldState == newState) return; // No state change

        // Check if the block is relevant (doors, trapdoors, fence gates, bells)
        var config = ConfigLoader.getConfig().soundDetection;
        String blockId = Registries.BLOCK.getId(newState.getBlock()).toString();
        float radius = 0.0f;

        if (config.interaction.useBlockTags) {
            for (var entry : config.interaction.tagConfigs.entrySet()) {
                String tagId = entry.getKey();
                if ((tagId.equals("minecraft:doors") && newState.isIn(BlockTags.DOORS)) ||
                        (tagId.equals("minecraft:trapdoors") && newState.isIn(BlockTags.TRAPDOORS)) ||
                        (tagId.equals("minecraft:fence_gates") && newState.isIn(BlockTags.FENCE_GATES))) {
                    radius = entry.getValue().radius;
                    break;
                }
            }
        }

        if (radius == 0.0f && config.interaction.blocks.containsKey(blockId)) {
            radius = config.interaction.blocks.get(blockId).radius; // E.g., for bells
        }

        // Trigger sound event if applicable
        if (radius > 0) {
            SoundDetection.handleSoundEvent(world, pos, radius, ConfigLoader.getConfig(), "interaction");
        }
    }
}