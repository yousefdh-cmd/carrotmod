package com.carrotmod;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CarrotMod.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CarrotMod.MOD_ID);

    public static final DeferredItem<RabbitStaffItem> CARROT_STAFF = ITEMS.registerItem("carrot_staff",
            p -> new RabbitStaffItem(p.stacksTo(1), 1));
    public static final DeferredItem<RabbitStaffItem> GOLDEN_CARROT_STAFF = ITEMS.registerItem("golden_carrot_staff",
            p -> new RabbitStaffItem(p.stacksTo(1), 2));
    public static final DeferredItem<RabbitStaffItem> DIAMOND_CARROT_STAFF = ITEMS.registerItem("diamond_carrot_staff",
            p -> new RabbitStaffItem(p.stacksTo(1), 3));
    public static final DeferredItem<RabbitStaffItem> METEOR_CARROT_STAFF = ITEMS.registerItem("meteor_carrot_staff",
            p -> new RabbitStaffItem(p.stacksTo(1), 4));
    public static final DeferredItem<RabbitStaffItem> LEGENDARY_CARROT_STAFF = ITEMS.registerItem("legendary_carrot_staff",
            p -> new RabbitStaffItem(p.stacksTo(1), 5));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CARROT_TAB = TABS.register("carrots",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.carrotmod"))
                    .icon(() -> new ItemStack(CARROT_STAFF.get()))
                    .displayItems((params, output) -> {
                        output.accept(CARROT_STAFF.get());
                        output.accept(GOLDEN_CARROT_STAFF.get());
                        output.accept(DIAMOND_CARROT_STAFF.get());
                        output.accept(METEOR_CARROT_STAFF.get());
                        output.accept(LEGENDARY_CARROT_STAFF.get());
                    })
                    .build());
}
