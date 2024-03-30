package com.aureskull.zmcmod.entity.goal;

import com.aureskull.zmcmod.entity.custom.StandingZombieEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class ZombieAttackGoal extends MeleeAttackGoal {
    private final StandingZombieEntity zombie;
    private int ticks;

    public ZombieAttackGoal(StandingZombieEntity zombie, double speed, boolean pauseWhenMobIdle) {
        super(zombie, speed, pauseWhenMobIdle);
        this.zombie = zombie;
    }

    @Override
    public boolean canStart(){
        return this.zombie.isPassedThroughWindow();
    }

    @Override
    public void start() {
        super.start();
        this.ticks = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.zombie.setAttacking(false);
    }

    @Override
    public void tick() {
        super.tick();
        ++this.ticks;
        if (this.ticks >= 5 && this.getCooldown() < this.getMaxCooldown() / 2) {
            this.zombie.setAttacking(true);
        } else {
            this.zombie.setAttacking(false);
        }
    }
}