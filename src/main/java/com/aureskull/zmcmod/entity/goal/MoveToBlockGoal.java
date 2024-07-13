package com.aureskull.zmcmod.entity.goal;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.entity.custom.StandingZombieEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class MoveToBlockGoal extends Goal {
    private final MobEntity entity;
    private final BlockPos targetPos;
    private final double speed;
    private int recalculatePathTimer = 0;

    public MoveToBlockGoal(StandingZombieEntity entity, BlockPos targetPos, double speed) {
        this.entity = entity;
        this.targetPos = targetPos;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public void tick() {
        if (--this.recalculatePathTimer <= 0) {
            this.recalculatePathTimer = 20;
            this.entity.getNavigation().startMovingTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), speed);
        }
    }

    @Override
    public boolean canStart() {
        if(this.targetPos == null) return false;
        if(this.entity instanceof StandingZombieEntity zombie && zombie.isPassedThroughWindow()) return false;
        return this.entity.squaredDistanceTo(Vec3d.ofCenter(this.targetPos)) > 1.0D;
    }

    @Override
    public boolean shouldContinue() {
        return this.entity.getNavigation().isFollowingPath() && this.entity.squaredDistanceTo(Vec3d.ofCenter(this.targetPos)) > 1.0D;
    }

    @Override
    public void start() {
        Path path = this.entity.getNavigation().findPathTo(targetPos, 0);
        if (path != null) {
            this.entity.getNavigation().startMovingAlong(path, speed);
        }
    }
}
