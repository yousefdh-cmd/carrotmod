package com.carrotmod;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Right click: summons your rabbit of this tier (or dismisses it if you are riding it). */
public class RabbitStaffItem extends Item {
    private static final int COOLDOWN_TICKS = 20 * 20; // 20 seconds
    private final int tier;

    public RabbitStaffItem(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        // Riding your rabbit: right click the staff to send it away.
        if (player.getVehicle() instanceof SuperRabbit ridden && player.getUUID().equals(ridden.getOwnerUUID())) {
            player.stopRiding();
            ridden.discard();
            return InteractionResultHolder.success(stack);
        }

        // Only one rabbit per player: remove old ones nearby.
        for (SuperRabbit old : serverLevel.getEntitiesOfClass(SuperRabbit.class,
                player.getBoundingBox().inflate(128.0D),
                r -> player.getUUID().equals(r.getOwnerUUID()))) {
            old.discard();
        }

        SuperRabbit rabbit = ModEntities.SUPER_RABBIT.get().create(serverLevel);
        if (rabbit == null) {
            return InteractionResultHolder.fail(stack);
        }
        rabbit.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0.0F);
        rabbit.setTamed(true);
        rabbit.setOwnerUUID(player.getUUID());
        rabbit.setTier(this.tier);
        serverLevel.addFreshEntity(rabbit);
        player.startRiding(rabbit);

        serverLevel.playSound(null, player.blockPosition(), SoundEvents.RABBIT_JUMP,
                SoundSource.PLAYERS, 1.0F, 1.0F);
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return InteractionResultHolder.success(stack);
    }
}
