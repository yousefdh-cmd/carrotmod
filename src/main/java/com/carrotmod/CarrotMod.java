package com.carrotmod;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@Mod(CarrotMod.MOD_ID)
public class CarrotMod {
    public static final String MOD_ID = "carrotmod";

    public CarrotMod(IEventBus modBus) {
        ModEntities.ENTITIES.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModItems.TABS.register(modBus);
        modBus.addListener(CarrotMod::registerAttributes);
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SUPER_RABBIT.get(), SuperRabbit.createAttributes().build());
    }
}
