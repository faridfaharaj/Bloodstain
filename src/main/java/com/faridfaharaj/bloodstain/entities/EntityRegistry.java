package com.faridfaharaj.bloodstain.entities;

import com.faridfaharaj.bloodstain.Bloodstain;
import com.faridfaharaj.bloodstain.Tags;
import com.faridfaharaj.bloodstain.entities.entity.Ghost;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class EntityRegistry {

    private static int id = 0;

    public static void registerEntities() {
        registerEntity("Player_ghost", Ghost.class, 64, 1, true);
    }

    private static void registerEntity(String name, Class<? extends Entity> entityClass, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
        net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(
                new ResourceLocation(Tags.MOD_ID, name),
                entityClass,
                name,
                id++,
                Bloodstain.getInstance(),
                trackingRange,
                updateFrequency,
                sendsVelocityUpdates
        );
    }

}
