package com.aureskull.zmcmod.entity.custom;

import com.aureskull.zmcmod.entity.goal.MoveToBlockGoal;
import com.aureskull.zmcmod.sound.ModSounds;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StandingZombieEntity extends HostileEntity {
    public final AnimationState walkingAnimationSate = new AnimationState();
    private int walkingAnimationTimeout = 0;

    public StandingZombieEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    private void setupAnimationStates(){
        if(this.walkingAnimationTimeout <= 0){
            this.walkingAnimationTimeout = this.random.nextInt(40) + 80;
            this.walkingAnimationSate.start(this.age);
        }else{
            --this.walkingAnimationTimeout;
        }
    }

    @Override
    protected void updateLimbs(float posDelta){
        float f = this.getPose() == EntityPose.STANDING ? Math.min(posDelta * 6.0f, 1.0f) : 0.0f;
        this.limbAnimator.updateLimbs(f, 0.2f);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getWorld().isClient()){
            setupAnimationStates();
        }
    }

    @Override
    protected void initGoals(){
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MoveToBlockGoal(this, new BlockPos(0, -60, 0), 1.0D));
        //this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        //this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class ,4f));
        //this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0));
    }

    public static DefaultAttributeContainer.Builder createStandingZombieAttributes(){
        /*return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, .5f)
                .add(EntityAttributes.GENERIC_ARMOR, 0.5f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2);*/

        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 999f);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.STANDING_ZOMBIE_AMB;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.STANDING_ZOMBIE_DEATH;
    }
}
