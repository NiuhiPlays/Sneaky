package com.niuhi.mixin;

import com.niuhi.config.ConfigLoader;
import com.niuhi.detection.SoundDetection;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerInteractionMixin {
    static {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;

            BlockPos pos = hitResult.getBlockPos();
            var state = world.getBlockState(pos);
            var config = ConfigLoader.getConfig().soundDetection;
            Block block = state.getBlock();
            String blockId = Registries.BLOCK.getId(block).toString();
            float radius = 0.0f;

            if (config.interaction.useBlockTags) {
                for (var entry : config.interaction.tagConfigs.entrySet()) {
                    String tagId = entry.getKey();
                    if ((tagId.equals("minecraft:doors") && state.isIn(BlockTags.DOORS)) ||
                            (tagId.equals("minecraft:trapdoors") && state.isIn(BlockTags.TRAPDOORS)) ||
                            (tagId.equals("minecraft:fence_gates") && state.isIn(BlockTags.FENCE_GATES)) ||
                            (tagId.equals("minecraft:buttons") && state.isIn(BlockTags.BUTTONS)) ||
                            (tagId.equals("minecraft:pressure_plates") && state.isIn(BlockTags.PRESSURE_PLATES))) {
                        radius = entry.getValue().radius;
                        break;
                    }
                }
            }

            if (radius == 0.0f && config.interaction.blocks.containsKey(blockId)) {
                radius = config.interaction.blocks.get(blockId).radius;
            }

            if (radius > 0) {
                SoundDetection.handleSoundEvent(world, pos, radius, ConfigLoader.getConfig(), "interaction");
            }

            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClient) return ActionResult.PASS;

            var stack = player.getStackInHand(hand);
            var config = ConfigLoader.getConfig().soundDetection;
            String itemId = Registries.ITEM.getId(stack.getItem()).toString();

            if (config.use.items.containsKey(itemId)) {
                float radius = config.use.items.get(itemId).radius;
                SoundDetection.handleSoundEvent(world, player.getBlockPos(), radius, ConfigLoader.getConfig(), "use");
            }

            return ActionResult.PASS;
        });
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void dummyTick(CallbackInfo ci) {
        // Required for mixin to apply
    }
}