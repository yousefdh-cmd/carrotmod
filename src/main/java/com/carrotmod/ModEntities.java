package com.carrotmod;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, CarrotMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<SuperRabbit>> SUPER_RABBIT =
            ENTITIES.register("super_rabbit", () -> EntityType.Builder
                    .of(SuperRabbit::new, MobCategory.CREATURE)
                    .sized(1.0F, 1.2F)
                    .passengerAttachments(0.95F)
                    .clientTrackingRange(10)
                    .build("super_rabbit"));
}
