package com.niuhi.detection;

import com.niuhi.config.Config;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SoundDetection {
    // Track last sound reaction time per mob (UUID -> timestamp in ticks)
    private static final Map<UUID, Long> mobSoundCooldowns = new HashMap<>();

    public static void handleSoundEvent(World world, BlockPos pos, float soundRadius, Config config) {
        long currentTick = world.getTime();
        float cooldownTicks = config.soundDetection.soundCooldownSeconds * 20.0f; // Convert seconds to ticks

        // Get nearby hostile mobs within sound radius
        Vec3d center = Vec3d.ofCenter(pos);
        Box box = new Box(center.subtract(soundRadius, soundRadius, soundRadius),
                center.add(soundRadius, soundRadius, soundRadius));
        List<HostileEntity> mobs = world.getEntitiesByClass(HostileEntity.class, box, mob -> true);

        for (HostileEntity mob : mobs) {
            double distance = mob.getPos().distanceTo(center);
            if (distance <= soundRadius) {
                UUID mobId = mob.getUuid();
                // Check cooldown
                if (!mobSoundCooldowns.containsKey(mobId) || (currentTick - mobSoundCooldowns.get(mobId)) >= cooldownTicks) {
                    // Update cooldown
                    mobSoundCooldowns.put(mobId, currentTick);
                    // Make mob investigate the sound location
                    mob.getNavigation().startMovingTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 1.0);
                }
            }
        }
    }
}