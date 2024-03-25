package com.aureskull.zmcmod.entity.goal;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.SmallZombieWindowBlockEntity;
import com.aureskull.zmcmod.entity.custom.StandingZombieEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

public class AttackWindowGoal extends Goal {
    private final StandingZombieEntity zombie;
    private long lastAttackTime = 0;

    public AttackWindowGoal(StandingZombieEntity zombie) {
        this.zombie = zombie;
    }

    @Override
    public boolean canStart() {
        if(this.zombie.isPassedThroughWindow()) return false;

        // Check for nearby window every second
        if (this.lastAttackTime + 3000 > System.currentTimeMillis()) {
            return false;
        }
        this.lastAttackTime = System.currentTimeMillis();

        // Check if there is a window within reach
        return this.findNearbyWindow() != null;
    }

    @Override
    public void start() {
        // Attack the window
        SmallZombieWindowBlockEntity window = this.findNearbyWindow();
        if (window != null) {
            window.removePlank();
        }
    }

    private SmallZombieWindowBlockEntity findNearbyWindow() {
        BlockPos pos = this.zombie.getBlockPos();
        // Search in a small area around the zombie for window block entities
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos checkPos = pos.add(dx, dy, dz);
                    BlockEntity entity = this.zombie.getWorld().getBlockEntity(checkPos);
                    if (entity instanceof SmallZombieWindowBlockEntity) {
                        return (SmallZombieWindowBlockEntity) entity;
                    }
                }
            }
        }
        return null;
    }
}
