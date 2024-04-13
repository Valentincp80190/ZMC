package com.aureskull.zmcmod.entity.goal;

import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.entity.custom.StandingZombieEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class AttackNearestPlayerGoal extends MeleeAttackGoal {
    private final StandingZombieEntity entity;
    private final double speed;
    private int recalculatePathTimer = 0;
    private BlockPos targetPos;

    private int attackTicks;
    private final int ATTACK_TICK_INTERVAL = 3;

    public AttackNearestPlayerGoal(StandingZombieEntity entity, double speed, boolean pauseWhenMobIdle) {
        super(entity, speed, pauseWhenMobIdle);
        this.entity = entity;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public void tick() {
        //Move entity
        if (--this.recalculatePathTimer <= 0) {
            this.recalculatePathTimer = 7;//Cause lag

            refreshTargetPlayer();

            if(targetPos != null)
                entity.getNavigation().startMovingTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), speed);
        }

        //Attack player
        LivingEntity target = entity.getTarget();
        if (shouldAttackTarget(target)) {
            attackTarget(target);
        } else {
            attackTicks = 0;
        }
    }

    private void attackTarget(LivingEntity target) {
        ++attackTicks;
        if (attackTicks >= ATTACK_TICK_INTERVAL) {
            this.entity.tryAttack(target);
            attackTicks = 0;
        }
    }

    private boolean shouldAttackTarget(LivingEntity target) {
        return target != null && this.entity.squaredDistanceTo(target) <= 2.7D;
    }

    private void refreshTargetPlayer(){
        List<PlayerEntity> list = new ArrayList<>();

        if(entity.getMapControllerBlockPos() != null && entity.getWorld().getBlockEntity(entity.getMapControllerBlockPos()) instanceof MapControllerBlockEntity mapControllerBlockEntity){
            list = mapControllerBlockEntity.getPlayerManager().getConnectedSubscribedPlayers();
        }

        if (!list.isEmpty()) {
            for (PlayerEntity player : list) {
                if (!player.isSpectator() && !player.isCreative()) {
                    this.targetPos = player.getBlockPos();
                    this.entity.setTarget(player);
                }
            }
        }else
            targetPos = null;
    }

    @Override
    public boolean canStart() {
        if(entity.isPassedThroughWindow()
            && targetPos != null){
            return true;
        }else{
            refreshTargetPlayer();
        }

        return false;
    }

    @Override
    public boolean shouldContinue() {
        if(this.entity instanceof StandingZombieEntity
                && entity.getNavigation().isFollowingPath()){
            return true;
        }
        return false;
    }

    @Override
    public void stop() {
        super.stop();
        this.entity.setAttacking(false);
    }

    @Override
    public void start() {
    }
}
