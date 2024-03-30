package com.aureskull.zmcmod.entity.goal;

import com.aureskull.zmcmod.entity.custom.StandingZombieEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.List;

public class MoveToNearestPlayerGoal extends Goal {
    private final MobEntity entity;
    private final double speed;
    private int recalculatePathTimer = 0;
    private BlockPos targetPos;

    public MoveToNearestPlayerGoal(StandingZombieEntity entity, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public void tick() {
        if (--this.recalculatePathTimer <= 0) {
            this.recalculatePathTimer = 7;//Cause lag

            refreshTargetPlayer();

            if(targetPos != null)
                this.entity.getNavigation().startMovingTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), speed);
        }
    }

    private void refreshTargetPlayer(){
        List<PlayerEntity> list = this.entity.getWorld().getNonSpectatingEntities(PlayerEntity.class, this.entity.getBoundingBox().expand(256.0F, 10.0D, 256.0F));
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
        if(this.entity instanceof StandingZombieEntity zombie && zombie.isPassedThroughWindow()
            && targetPos != null
            //&& !zombie.asJoinedPlayer
            && this.entity.squaredDistanceTo(Vec3d.ofCenter(this.targetPos)) > 1.0D
            ){
            return true;
        }else{
            refreshTargetPlayer();
        }

        return false;
    }

    @Override
    public boolean shouldContinue() {
        if(this.entity instanceof StandingZombieEntity zombie
                && !zombie.asJoinedPlayer
                && entity.getNavigation().isFollowingPath()
                && this.entity.squaredDistanceTo(Vec3d.ofCenter(this.targetPos)) > 1.0D){
            return true;
        }else{
            if(this.entity instanceof StandingZombieEntity zombie){
                zombie.asJoinedPlayer = true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        if(targetPos != null){
            Path path = this.entity.getNavigation().findPathTo(targetPos, 256);
            if (path != null) {
                this.entity.getNavigation().startMovingAlong(path, speed);
            }
        }
    }
}
