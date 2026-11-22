package com.carrotmod;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * A rideable rabbit summoned by the carrot staffs. Built on AbstractHorse so it gets the
 * vanilla riding controls and jump bar. It is always "saddled" and only its owner can ride it.
 */
public class SuperRabbit extends AbstractHorse {
    private static final EntityDataAccessor<Integer> TIER =
            SynchedEntityData.defineId(SuperRabbit.class, EntityDataSerializers.INT);

    // Index 0..4 = tier 1..5
    /** Speed in blocks per second (a mounted horse moves at attribute * 43.17 blocks/s). */
    private static final double[] SPEED_BPS = {7.0, 8.5, 10.0, 11.5, 13.0};
    /** Jump strength attribute (calibrated for ~2.5 / 3.5 / 5 / 6 / 8 blocks on a full charge). */
    private static final double[] JUMP = {0.657, 0.793, 0.968, 1.073, 1.261};
    /** Max health in half hearts: 10 / 12 / 15 / 18 / 20 hearts. */
    private static final double[] HEALTH = {20.0, 24.0, 30.0, 36.0, 40.0};
    private static final double[] ARMOR = {0.0, 0.0, 0.0, 0.0, 6.0};

    public SuperRabbit(EntityType<? extends SuperRabbit> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractHorse.createBaseHorseAttributes().add(Attributes.ARMOR, 0.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TIER, 1);
    }

    public int getTier() {
        return Mth.clamp(this.entityData.get(TIER), 1, 5);
    }

    public void setTier(int tier) {
        this.entityData.set(TIER, Mth.clamp(tier, 1, 5));
        applyTierStats();
        this.setHealth(this.getMaxHealth());
    }

    private void applyTierStats() {
        int i = getTier() - 1;
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(HEALTH[i]);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(SPEED_BPS[i] / 43.17D);
        this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(JUMP[i]);
        this.getAttribute(Attributes.ARMOR).setBaseValue(ARMOR[i]);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("RabbitTier", getTier());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(TIER, tag.contains("RabbitTier") ? tag.getInt("RabbitTier") : 1);
        applyTierStats();
    }

    /** The rabbit always counts as saddled, so the owner can ride it and use the jump bar. */
    @Override
    public boolean isSaddled() {
        return true;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!player.getUUID().equals(this.getOwnerUUID())) {
            return InteractionResult.PASS;
        }
        if (!this.level().isClientSide && !this.isVehicle() && !player.isSecondaryUseActive()) {
            this.doPlayerRide(player);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    // Tier 3+: no fall damage (also protects the rider).
    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        if (getTier() >= 3) {
            return false;
        }
        return super.causeFallDamage(fallDistance, multiplier, source);
    }

    // Tier 4+: immune to fire and lava.
    @Override
    public boolean fireImmune() {
        return getTier() >= 4 || super.fireImmune();
    }

    @Override
    public void tick() {
        super.tick();
        int tier = getTier();

        // Tier 4+: floats on the water surface (may bob slightly).
        if (tier >= 4 && this.isInWater()) {
            double depth = this.getFluidHeight(FluidTags.WATER);
            Vec3 v = this.getDeltaMovement();
            if (depth > 0.25D) {
                this.setDeltaMovement(v.x, Math.max(v.y, 0.0D) + 0.06D, v.z);
            } else {
                this.setDeltaMovement(v.x, Math.min(v.y, 0.0D) * 0.2D, v.z);
            }
        }

        // Tier 5: slow glide when falling.
        if (tier >= 5 && !this.onGround() && !this.isInWater()) {
            Vec3 v = this.getDeltaMovement();
            if (v.y < -0.12D) {
                this.setDeltaMovement(v.x, -0.12D, v.z);
            }
            this.fallDistance = 0.0F;
        }

        if (!this.level().isClientSide) {
            // Tier 2+: slow regeneration (half a heart every 4 seconds).
            if (tier >= 2 && this.tickCount % 80 == 0 && this.getHealth() < this.getMaxHealth()) {
                this.heal(1.0F);
            }
            // Tier 4+: the rider is also protected from fire.
            if (tier >= 4 && this.tickCount % 20 == 0) {
                for (Entity passenger : this.getPassengers()) {
                    if (passenger instanceof LivingEntity living) {
                        living.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, true, false));
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RABBIT_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.RABBIT_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RABBIT_DEATH;
    }
}
